package com.tijiantest.testcase.crm.order;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.*;

import com.tijiantest.model.order.OrderQueryParams;
import com.tijiantest.util.ListUtil;
import com.tijiantest.util.pagination.Page;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.tijiantest.base.Flag;
import com.tijiantest.base.HttpResult;
import com.tijiantest.util.CvsFileUtil;
import com.tijiantest.util.db.DBMapper;
import com.tijiantest.util.db.MongoDBUtils;
import com.tijiantest.util.db.SqlException;

public class MongoBatchOrderTest extends OrderBase {
	public static JSONArray records = new JSONArray();
	public static String companyId;

	@Test(description = "根据订单查询条件对象查询订单信息", groups = { "qa","crm_mongoBatchOrder" }, dataProvider = "mongoBatchOrder")
	public void test_01_mongoBatchOrder(String... args) throws ParseException, SqlException {
		String status = args[1];
		String query = args[2];
		String companyType = args[3];
		Integer examCompanyId;
		if(companyType.equals("SKXC"))
			  examCompanyId=defSKXCnewcompany.getId();
		  else
			  examCompanyId=defnewcompany.getId();
		String rowCount = args[4];
		String currentPage = args[5];
		String pageSize = args[6];
		String description = args[7];
		String queryStatus = "'status':{" + status + "}";
		companyId= String.valueOf(examCompanyId);
		String queryExamCompanyId = "'examCompanyId':{"+query+"" + examCompanyId + "}";
		String queryOrderObj = "{" + queryStatus + "," + queryExamCompanyId + "}";

//		List<NameValuePair> params = new ArrayList<>();
//		params.add(new BasicNameValuePair("rowCount", rowCount));
//		params.add(new BasicNameValuePair("currentPage", currentPage));
//		params.add(new BasicNameValuePair("pageSize", pageSize));
		OrderQueryParams params = new OrderQueryParams();
		Page page = new Page();
		page.setRowCount(Integer.parseInt(rowCount));
		page.setPageSize(Integer.parseInt(pageSize));
		page.setCurrentPage(Integer.parseInt(currentPage));
		params.setPage(page);
		params.setExamCompanyIds(Arrays.asList(examCompanyId));
		params.setOrderStatuses(Arrays.asList(Integer.parseInt(status.split(":")[1])));
//		Map<String, String> paMap = new HashMap<>();
//		paMap.put("queryOrderObj", queryOrderObj);

		HttpResult result = httpclient.post(Flag.CRM, Order_MongoBatchOrder, JSON.toJSONString(params));
		String body = result.getBody();
		Assert.assertEquals(result.getCode(), HttpStatus.SC_OK, description);

		JSONObject jsonArrayList = JSON.parseObject(body);
		String stringArray = jsonArrayList.get("records").toString();
		records = JSONArray.parseArray(stringArray);
		if (checkmongo) {
			String querySql = "{" + queryStatus + "," + queryExamCompanyId + ", 'managerId':" + defaccountId + ",'isDeleted':{$exists:false}}";
			log.info(querySql);
			List<Map<String, Object>> mongoList = MongoDBUtils.queryByPage(querySql, "insertTime", -1, 0,
					Integer.valueOf(pageSize), MONGO_COLLECTION);
			Assert.assertEquals(records.size(), mongoList.size());
			for (int i = 0; i < records.size(); i++) {
				JSONObject jsonObject = (JSONObject) records.get(i);
				Map<String, Object> mogoMap = mongoList.get(i);
				log.debug(mogoMap.get("id"));

				// 体检日期
				if (jsonObject.get("examDate") != null) {
					String object = jsonObject.get("examDate").toString();
					long lt = new Long(object);
					Date date = new Date(lt);
					Assert.assertEquals(sdf.format(date),
							getGMTDateString(mogoMap.get("examDate")));
				} else {
					Assert.assertTrue(mogoMap.get("examDate") == null);
				}

				// 体检时段
				if (jsonObject.get("examTimeIntervalName") != null) {
					Assert.assertEquals(jsonObject.get("examTimeIntervalName"), mogoMap.get("examTimeIntervalName"));
				} else {
					Assert.assertNull(mogoMap.get("examTimeIntervalName"));
				}

				// 体检人姓名
				if(mogoMap.get("orderAccount")!=null)
					Assert.assertEquals(((JSONObject) jsonObject.get("orderAccount")).get("name"),
						((BasicDBObject) mogoMap.get("orderAccount")).get("name"));
				else
					Assert.assertNull(jsonObject.get("orderAccount"));
				// 性别
				Assert.assertEquals(((JSONObject) jsonObject.get("examiner")).get("gender"),
						((BasicDBObject) mogoMap.get("examiner")).get("gender"));
				// 年龄
				Assert.assertEquals(((JSONObject) jsonObject.get("examiner")).get("birthYear"),
						((BasicDBObject) mogoMap.get("examiner")).get("birthYear"));

				// 手机号
				Assert.assertEquals(((JSONObject) jsonObject.get("examiner")).get("mobile"),
						((BasicDBObject) mogoMap.get("examiner")).get("mobile"));


				// 身份证
				Assert.assertEquals(((JSONObject) jsonObject.get("examiner")).get("idCard"),
						((BasicDBObject) mogoMap.get("examiner")).get("idCard"));

				// 职级
				Assert.assertEquals(((JSONObject) jsonObject.get("examiner")).get("position"),
						((BasicDBObject) mogoMap.get("examiner")).get("position"));

				// 部门
				Assert.assertEquals(((JSONObject) jsonObject.get("examiner")).get("department"),
						((BasicDBObject) mogoMap.get("examiner")).get("department"));

				// 员工号
				Assert.assertEquals(((JSONObject) jsonObject.get("examiner")).get("employeeId"),
						((BasicDBObject) mogoMap.get("examiner")).get("employeeId"));

				// 婚否
				Assert.assertEquals(jsonObject.getJSONObject("orderExportExtInfo").getString("marriageStatusLabel"),((BasicDBObject)mogoMap.get("orderExportExtInfo")).get("marriageStatusLabel").toString());


				// 退休
				Assert.assertEquals(jsonObject.getJSONObject("orderExportExtInfo").getString("retireLabel"),((BasicDBObject)mogoMap.get("orderExportExtInfo")).get("retireLabel").toString());



				// 订单金额
				Assert.assertEquals(jsonObject.get("orderPrice"), mogoMap.get("orderPrice"));
				// 结算金额

				// 状态
				Assert.assertEquals(jsonObject.get("status").toString(), mogoMap.get("status").toString());
				// 操作员
				Assert.assertEquals(jsonObject.get("operator"), mogoMap.get("operator"));
				//退款状态
				String orderNum = mogoMap.get("orderNum").toString();
				String refundSql = "select * from tb_order_refund_apply where order_num = '"+orderNum+"' order by gmt_created desc";
				List<Map<String,Object>> refundList = DBMapper.query(refundSql);
				if(refundList.size()>0)
					Assert.assertEquals(jsonObject.get("refundScene").toString(),refundList.get(0).get("scene").toString());
				//订单医院设置
				if(((JSONObject) jsonObject.get("orderHospital")).get("hospitalSettings")!=null && ((JSONObject)((JSONObject) jsonObject.get("orderHospital")).get("hospitalSettings")).get("exportWithXls")!=null){
					Assert.assertEquals(((JSONObject)((JSONObject) jsonObject.get("orderHospital")).get("hospitalSettings")).getBooleanValue("exportWithXls"),
							((BasicDBObject)((BasicDBObject) mogoMap.get("orderHospital")).get("hospitalSettings")).getBoolean("exportWithXls"));
					Assert.assertEquals(((JSONObject)((JSONObject) jsonObject.get("orderHospital")).get("hospitalSettings")).getBooleanValue("supportManualRefund"),
							((BasicDBObject)((BasicDBObject) mogoMap.get("orderHospital")).get("hospitalSettings")).getBoolean("supportManualRefund"));
					Assert.assertEquals(((JSONObject)((JSONObject) jsonObject.get("orderHospital")).get("hospitalSettings")).getBooleanValue("mobileFieldOrder"),
							((BasicDBObject)((BasicDBObject) mogoMap.get("orderHospital")).get("hospitalSettings")).getBoolean("mobileFieldOrder"));
				}

				
				//退款单项详情
				JSONArray ja = (JSONArray) jsonObject.get("refundItemsClassify");
				BasicDBList jmongo = (BasicDBList)mogoMap.get("refundItemsClassify");
				if(ja != null && ja.size()>0){
					for(int s=0;s<ja.size();s++){
						Assert.assertEquals(((JSONObject)ja.get(s)).getBooleanValue("addExam"),((BasicDBObject)jmongo.get(s)).getBoolean("addExam"));
						Assert.assertEquals(((JSONObject)ja.get(s)).getBooleanValue("refundRefusedItem"),((BasicDBObject)jmongo.get(s)).getBoolean("refundRefusedItem"));
						Assert.assertEquals(((JSONObject)ja.get(s)).getBooleanValue("syncPrice"),((BasicDBObject)jmongo.get(s)).getBoolean("syncPrice"));
						Assert.assertEquals(((JSONObject)ja.get(s)).getIntValue("checkState"),((BasicDBObject)jmongo.get(s)).getInt("checkState"));
						Assert.assertEquals(((JSONObject)ja.get(s)).getBooleanValue("discount"),((BasicDBObject)jmongo.get(s)).getBoolean("discount"));
						Assert.assertEquals(((JSONObject)ja.get(s)).getString("hisId"),((BasicDBObject)jmongo.get(s)).getString("hisId"));
						Assert.assertEquals(((JSONObject)ja.get(s)).getString("name"),((BasicDBObject)jmongo.get(s)).getString("name"));
						Assert.assertEquals(((JSONObject)ja.get(s)).getIntValue("originalPrice"),((BasicDBObject)jmongo.get(s)).getInt("originalPrice"));
						Assert.assertEquals(((JSONObject)ja.get(s)).getIntValue("price"),((BasicDBObject)jmongo.get(s)).getInt("price"));
						Assert.assertEquals(((JSONObject)ja.get(s)).getIntValue("refundState"),((BasicDBObject)jmongo.get(s)).getInt("refundState"));
						Assert.assertEquals(((JSONObject)ja.get(s)).getIntValue("type"),((BasicDBObject)jmongo.get(s)).getInt("type"));
						Assert.assertEquals(((JSONObject)ja.get(s)).getIntValue("typeToMeal"),((BasicDBObject)jmongo.get(s)).getInt("typeToMeal"));
						Assert.assertEquals(((JSONObject)ja.get(s)).getIntValue("_id"),((BasicDBObject)jmongo.get(s)).getInt("_id"));
					}
				}
				
			}
		}
		System.out.println(companyId);
	}

	@DataProvider(name = "mongoBatchOrder")
	protected Iterator<String[]> mongoBatchOrder() {
		try {
			return CvsFileUtil.fromCvsFileToIterator("./csv/order/mongoBatchOrder.csv", 18);
		} catch (FileNotFoundException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		return null;
	}
}
