package com.tijiantest.testcase.crm.order.checklist;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mongodb.BasicDBObject;
import com.tijiantest.base.HttpResult;
import com.tijiantest.model.order.checklist.ChecklistVO;
import com.tijiantest.testcase.crm.order.MongoBatchOrderTest;
import com.tijiantest.util.DateUtils;
import com.tijiantest.util.db.DBMapper;
import com.tijiantest.util.db.MongoDBUtils;
import com.tijiantest.util.db.SqlException;
import com.tijiantest.util.validator.MobileValidator;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class GetChecklistTest extends ChecklistBaseTest {

	@Test(description = "打印", groups = { "qa" }, dependsOnGroups = "crm_mongoBatchOrder")
	public void test_01_getChecklist() throws SqlException, ParseException {
		// 新获取订单列表，然后取出一个已预约的订单
		JSONArray records = MongoBatchOrderTest.records;
		String orderIds = "";
		log.info("订单数量。。。"+records.size());
		for (int i = 0; i < records.size(); i++) {
			JSONObject jsonObject = records.getJSONObject(i);
			if (jsonObject.get("status").toString().equals("2")) {
				orderIds += jsonObject.getString("id") + ",";
			} else {
				continue;
			}
		}
		if (orderIds.equals("")) {
			// 没有可打印的订单
			return;
		}
		List<NameValuePair> pairs = new ArrayList<>();
		int lastIndex = orderIds.lastIndexOf(",");
		orderIds = orderIds.substring(0, lastIndex);
		pairs.add(new BasicNameValuePair("orderIds", orderIds));
		pairs.add(new BasicNameValuePair("hospitalId", defhospital.getId() + ""));
		String examCompanyId = MongoBatchOrderTest.companyId;
		pairs.add(new BasicNameValuePair("companyId", examCompanyId));

		HttpResult result = httpclient.get(Order_GetChecklist, pairs);
		String body = result.getBody();
		Assert.assertEquals(result.getCode(), HttpStatus.SC_OK,"错误提示："+result.getBody());

		List<ChecklistVO> checklist = JSON.parseArray(body, ChecklistVO.class);
		Collections.sort(checklist, new Comparator<ChecklistVO>() {
			@Override
			public int compare(ChecklistVO t1, ChecklistVO t2) {
				String orderNum1 = t1.getOrderNum();
				String orderNum2 = t2.getOrderNum();
				String sql = "select * from tb_order where order_num=?";
				List<Map<String, Object>> list1 = null;
				List<Map<String, Object>> list2 = null;
				try {
					list1 = DBMapper.query(sql, orderNum1);
				} catch (SqlException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					list2 = DBMapper.query(sql, orderNum2);
				} catch (SqlException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				int orderId1 = Integer.parseInt(list1.get(0).get("id").toString());
				int orderId2 = Integer.parseInt(list2.get(0).get("id").toString());
				return orderId1 - orderId2;
			}
		});

		if (checkdb) {

			String mongoSql = "{'id':{$in:[" + orderIds + "]}}";
			List<Map<String, Object>> mongoList = MongoDBUtils.query(mongoSql, MONGO_COLLECTION);
			Collections.sort(mongoList, new Comparator<Map<String, Object>>() {
				@Override
				public int compare(Map<String, Object> t1, Map<String, Object> t2) {

					return Integer.valueOf(t1.get("id").toString()) - Integer.valueOf(t2.get("id").toString());
				}
			});

			Assert.assertEquals(checklist.size(), mongoList.size());
			for (int i = 0; i < checklist.size(); i++) {
				ChecklistVO vo = checklist.get(i);
				Map<String, Object> map = mongoList.get(i);
				// loginName
				String accountId = ((BasicDBObject) map.get("account")).get("_id").toString();
				String userSql = "select * from tb_user where account_id=? order by id desc";
				List<Map<String, Object>> userList = DBMapper.query(userSql, accountId);
				// 不显示手机号
				for (int j = 0; j < userList.size(); j++) {
					Map<String, Object> m = userList.get(j);
					String username = m.get("username").toString();
					if (!MobileValidator.isMobile(m.get("username").toString())&&username.length()!=11) {
						Assert.assertEquals(vo.getLoginName(), username);
					}
				}
				
				String sql = "select * from tb_hospital_company where organization_id=? and id=?";
				List<Map<String, Object>> list = DBMapper.query(sql, defhospital.getId(), examCompanyId);
//				Integer oldCompanyId = Integer.valueOf(list.get(0).get("tb_exam_company_id").toString());
				// 根据examCompanyId判断体检地址取值
					if (list.get(0).get("examination_address") == null
							|| list.get(0).get("examination_address").equals("")) {
						// 体检地址为空时，取医院地址
						String hospitalSql = "select * from tb_hospital where id=?";
						List<Map<String, Object>> hospitalList = DBMapper.query(hospitalSql, defhospital.getId());
						Assert.assertTrue(vo.getExamAddress().contains(hospitalList.get(0).get("address").toString()));
					} else {
						// 体检地址不为空
						Assert.assertTrue(body.contains(list.get(0).get("examination_address").toString()));
					}

				// orderNum
				Assert.assertEquals(vo.getOrderNum(), map.get("orderNum"));

				// examDate
				// 如果有timeremark,则返回timeremark； 否则是体检日期
				if(map.get("remark")!=null){					
					String remark = map.get("remark").toString();
					int index = remark.lastIndexOf(":\"");
					lastIndex = remark.lastIndexOf("\"}");
					String timeRemark = remark.substring(index + 2, lastIndex);
					if (timeRemark != null && !timeRemark.equals("")) {
						// 取timeremark
						Assert.assertEquals(vo.getExamDate(), timeRemark);
					} 
				}else {
					// 取体检日期
					if (map.get("examDate") != null && !map.get("examDate").equals("")) {
						String examTimeIntervalName = map.get("examTimeIntervalName").toString();
						if (examTimeIntervalName != null && !examTimeIntervalName.equals("")) {

							Assert.assertEquals(vo.getExamDate(),
									DateUtils.getGMTDateString(map.get("examDate")) + " " + examTimeIntervalName);
						} else {
							Assert.assertEquals(vo.getExamDate(), DateUtils.getGMTDateString(map.get("examDate")));
						}
					} else {
						// 自选日期订单
						Assert.assertEquals(vo.getExamDate(), "");
					}
				}

				// oneImages

				// password
				Assert.assertEquals(vo.getPassword(), "111111");

				// twoImages

				// wxImages

				// canModifyDate
				// 先获取提前几天导出
				String settingsSql = "select * from tb_hospital_settings where hospital_id=?";
				List<Map<String, Object>> settingsList = DBMapper.query(settingsSql, defhospital.getId());
				String previousExportDays = settingsList.get(0).get("previous_export_days").toString();
				if (map.get("examDate") == null) {
					// 自选日期
					Assert.assertNull(vo.getCanModifyDate());
				} else {
					String stringDate = map.get("examDate").toString();
					SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy",Locale.US);
					Date date =sdf.parse(stringDate);
					Date canModifyDate = DateUtils.offsetDestDay(date, 0 - Integer.parseInt(previousExportDays));
//					SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
					Assert.assertEquals(vo.getCanModifyDate(), DateUtils.format("yyyy-MM-dd",canModifyDate));
				}

			}
		}
	}

}
