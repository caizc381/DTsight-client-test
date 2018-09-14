package com.tijiantest.testcase.ops.order;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.mongodb.BasicDBObject;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.assertj.core.error.ElementsShouldBeExactly;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jayway.jsonpath.JsonPath;
import com.tijiantest.base.Flag;
import com.tijiantest.base.HttpResult;
import com.tijiantest.model.account.AccountManageDto;
import com.tijiantest.model.company.ChannelCompany;
import com.tijiantest.model.company.HospitalCompany;
import com.tijiantest.model.hospital.Hospital;
import com.tijiantest.model.order.Order;
import com.tijiantest.model.order.OrderQueryParams;
import com.tijiantest.model.organization.OrganizationListVo;
import com.tijiantest.testcase.ops.OpsBase;
import com.tijiantest.util.CvsFileUtil;
import com.tijiantest.util.DateUtils;
import com.tijiantest.util.ListUtil;
import com.tijiantest.util.db.MongoDBUtils;
import com.tijiantest.util.pagination.Page;

public class QueryOrderTest extends OpsBase {

	String DefhospitalId;
	String DefexamCompanyId;
	String DeffromSite;
	String DefchannelCompanyId;
	public static JSONArray recordList = new JSONArray();
	public static JSONArray queryOrderList = new JSONArray();

	 @Test(description = "用户管理 - 用户订单", groups = { "qa","queryOrder_account" },dataProvider = "queryOrder_account")
	public void test_01_queryOrder(String... args) throws ParseException {
		// 先通过AccountManagePageInfo获取accountId
		String rowCount = args[1];
		String currentPage = args[2];
		String pageSize = args[3];
		String name = args[4];
		String mobile = args[5];
		String idCard = args[6];

		List<NameValuePair> pairs = new ArrayList<>();
		pairs.add(new BasicNameValuePair("rowCount", rowCount));
		pairs.add(new BasicNameValuePair("currentPage", currentPage));
		pairs.add(new BasicNameValuePair("pageSize", pageSize));
		pairs.add(new BasicNameValuePair("name", name));
		pairs.add(new BasicNameValuePair("mobile", mobile));
		pairs.add(new BasicNameValuePair("idCard", idCard));

		HttpResult result = httpclient.get(Flag.OPS, OPS_AccountManagePageInfo, pairs);
		log.info("body"+result.getBody());
		Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);

		List<AccountManageDto> accountManageDtos = JSON.parseArray(result.getBody(), AccountManageDto.class);
		if (accountManageDtos.size() == 0) {
			log.info("没有搜索到对应的用户！！！");
			return;
		}
		AccountManageDto accountManageDto = accountManageDtos.get(0);

		OrderQueryParams orderQueryParams = new OrderQueryParams();
		List<Integer> accountIds = new ArrayList<>();
		accountIds.add(accountManageDto.getId());
		orderQueryParams.setAccountIds(accountIds);

		Page page = new Page();
		page.setCurrentPage(1);
		page.setPageSize(50);
		page.setRowCount(-1);
		orderQueryParams.setPage(page);

		String json = JSON.toJSONString(orderQueryParams);

		result = httpclient.post(Flag.OPS, OpsOrder_QueryOrder, json);
		System.out.println(result.getBody());
		Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);
		recordList = JSONArray.parseArray(JsonPath.read(result.getBody(), "$.records").toString());

		if (checkdb) {
			String sql = "{'examiner.customerId':"+accountManageDto.getId();
			sql += ",'isDeleted':{$exists:false}}";
			List<Map<String, Object>> list = MongoDBUtils.queryByPage(sql, "insertTime", -1, 0, Integer.valueOf(pageSize), MONGO_COLLECTION);
			Assert.assertEquals(recordList.size(), list.size());

			checkDB(recordList, list);
		}
	}

	@SuppressWarnings("unused")
	@BeforeClass(description = "获取医院/渠道商ID和单位ID", groups = { "qa" })
	public void doBefore() {
		// 先获取体检中心列表
		HttpResult result = httpclient.get(Flag.OPS, OpsOrder_OrderOrganizationList);
		OrganizationListVo vo = JSON.parseObject(result.getBody(), OrganizationListVo.class);

		System.out.println(result.getBody());
		List<Hospital> hospitals = vo.getHospitals();

		// 随机取一个医院
		Random random = new Random();
		int index1 = random.nextInt(hospitals.size()) % (hospitals.size() + 1);
		DefhospitalId = hospitals.get(index1).getId() + "";

		List<NameValuePair> pairs = new ArrayList<>();
		pairs.add(new BasicNameValuePair("hasGuestCompany", "true"));
		pairs.add(new BasicNameValuePair("hospitalId", DefhospitalId + ""));

		// 根据医院，取单位
		result = httpclient.get(Flag.OPS, OpsOrder_AllCompanies, pairs);

		List<HospitalCompany> hospitalCompanies = JSON.parseArray(result.getBody(), HospitalCompany.class);

		int index2 = random.nextInt(hospitalCompanies.size()) % (hospitalCompanies.size() + 1);
		DefexamCompanyId = hospitalCompanies.get(index2).getId() + "";

		// 取渠道商(优先取前面几个渠道商，后面渠道商数据单位有为空的）
		int index3 = random.nextInt(6) % (vo.getChannels().size() + 1);
		DeffromSite = vo.getChannels().get(index3).getId() + "";

		// 根据渠道商，取渠道单位
		List<NameValuePair> pairs2 = new ArrayList<>();
		pairs.add(new BasicNameValuePair("channelId", DeffromSite + ""));

		result = httpclient.get(Flag.OPS, OpsOrder_ListChannelCompanyList, pairs);

		System.out.println(result.getBody());
		List<ChannelCompany> channelCompanies = JSON.parseArray(result.getBody(), ChannelCompany.class);
		int index4 = random.nextInt(channelCompanies.size()) % (channelCompanies.size() + 1);
		DefchannelCompanyId = channelCompanies.get(index4).getId() + "";

		System.out.println("体检中心hospitalId = " + DefhospitalId + " --------- 单位examCompanyId=" + DefexamCompanyId
				+ " ---------  渠道商fromSite=" + DeffromSite + " -------------- 渠道单位ID:" + DefchannelCompanyId);
	}

	@Test(description = "订单管理-查询订单-默认(10条)", groups = { "qa" })
	public void test_02_queryOrder() throws ParseException {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String examStartDate = simpleDateFormat.format(new Date()) + " 00:00:00";
		String examEndDate = simpleDateFormat.format(new Date()) + " 23:59:59";

		OrderQueryParams pairs = new OrderQueryParams();
		int pageSize = 10;
		int currentPage = 1;
		pairs.setExamStartDate(simplehms.parse(examStartDate));
		pairs.setExamEndDate(simplehms.parse(examEndDate));
		pairs.setPage(new Page(currentPage, pageSize));

		HttpResult result = httpclient.post(Flag.OPS, OpsOrder_QueryOrder, JSON.toJSONString(pairs));
		String body = result.getBody();
		System.out.println(body);
		Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);
		JSONObject JsonArrayList = JSON.parseObject(body);
		String records = JsonArrayList.get("records").toString();
		recordList = JSONArray.parseArray(records);

		System.out.println("订单(默认)数量： " + recordList.size());
		if (checkmongo) {
			String sql = "{'isDeleted':{$exists:false}";
			sql += "}";
			List<Map<String, Object>> list = MongoDBUtils.queryByPageAndExameDate(sql, "insertTime", -1, 0, pageSize,
					examStartDate, examEndDate, MONGO_COLLECTION);
			Assert.assertEquals(recordList.size(), list.size());

			checkDB(recordList, list);
		}
	}

	@Test(description = "订单列表", groups = { "qa", "ops_queryOrder" }, dataProvider = "queryOrder_order")
	public void test_03_queryOrder(String... args) throws NumberFormatException, ParseException {
		String orderStatuses = args[1].replaceAll("#", ",");
		String examStartDate = args[2];
		String examEndDate = args[3];
		String insertStartDate = args[4];
		String insertEndDate = args[5];
		String isExport = args[6];
		String hospitalParams = args[7];
		String examCompanyIdParams = args[8];
		String fromSiteParams = args[9];
		String pageSize = args[10];
		String currentPage = args[11];
		String description = args[12];
		String channelSettStr = args[13];

		int hospitalId = -1;
		int examCompanyId = -1;
		int fromSite = -1;
		List<Integer> channelSett = new ArrayList<>();
		OrderQueryParams pairs = new OrderQueryParams();

		if (orderStatuses != null && !orderStatuses.isEmpty()) {
			pairs.setOrderStatuses(ListUtil.StringArraysToIntegerList(orderStatuses.split(",")));
		}
		if (examEndDate != null && examStartDate != null && !examEndDate.isEmpty() && !examStartDate.isEmpty()) {
			pairs.setExamStartDate(simplehms.parse(examStartDate));
			pairs.setExamEndDate(simplehms.parse(examEndDate));
		}
		if (insertEndDate != null && insertStartDate != null && !insertEndDate.isEmpty()
				&& !insertStartDate.isEmpty()) {
			pairs.setInsertStartDate(simplehms.parse(insertStartDate));
			pairs.setInsertEndDate(simplehms.parse(insertEndDate));
		}

		if (isExport != null && !isExport.equals("")) {
			pairs.setIsExport(Boolean.parseBoolean(isExport));
		}
		if (hospitalParams != null && !hospitalParams.equals("")) {
			if (hospitalParams.equals("DEF"))
				hospitalId = Integer.parseInt(DefhospitalId);
			else
				hospitalId = Integer.parseInt(hospitalParams);
			pairs.setHospitalIds(Arrays.asList(hospitalId));

		}

		if (examCompanyIdParams != null && !examCompanyIdParams.equals("")) {
			if (examCompanyIdParams.equals("DEF"))
				examCompanyId = Integer.parseInt(DefexamCompanyId);
			else
				examCompanyId = Integer.parseInt(examCompanyIdParams);
			pairs.setExamCompanyIds(Arrays.asList(examCompanyId));
		}
		if (fromSiteParams != null && !fromSiteParams.equals("")) {
			if (fromSiteParams.equals("DEF"))
				fromSite = Integer.parseInt(DeffromSite);
			else
				fromSite = Integer.parseInt(fromSiteParams);
			pairs.setFromSites(Arrays.asList(fromSite));
		}
		pairs.setPage(new Page(Integer.parseInt(currentPage), Integer.parseInt(pageSize)));

		if(channelSettStr!=null && !channelSettStr.equals("")){
			String[] channelSets = channelSettStr.split("#");
			channelSett = ListUtil.StringArraysToIntegerList(channelSets);
			pairs.setChannelSettlementStatus(channelSett);
		}
		HttpResult result = httpclient.post(Flag.OPS, OpsOrder_QueryOrder, JSON.toJSONString(pairs));
		String body = result.getBody();
		// System.out.println(body);
		Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);
		JSONObject JsonArrayList = JSON.parseObject(body);
		String records = JsonArrayList.get("records").toString();
		queryOrderList = JSONArray.parseArray(records);
		System.out.println("订单(" + description + ")数量： " + queryOrderList.size());

		if (checkdb) {
			String mongoSql = "{'isDeleted':{$exists:false},";

			if (orderStatuses != null && !orderStatuses.equals("")) {
				mongoSql += "'status':{'$in':[" + orderStatuses.replaceAll("\"", "") + "]},";
			}

			if (isExport != null && !isExport.equals("")) {
				mongoSql += "'isExport':" + isExport + ",";
			}

			if (hospitalId != -1)
				mongoSql += "'hospital._id':" + hospitalId + ",";
			if (examCompanyId != -1)
				mongoSql += "'examCompanyId':" + examCompanyId + ",";
			if (fromSite != -1)
				mongoSql += "'fromSite':" + fromSite + ",";
			if(channelSett != null && channelSett.size() > 0){
				if(channelSett.size() == 1 && channelSett.contains(4))//已结算
					mongoSql += "\"channelSettleInfo.settleSign\":{$in:[4]},";
				if(channelSett.size() > 1 && channelSett.contains(1) && channelSett.contains(3))//未结算
					mongoSql += "\"$or\":[{\"channelSettleInfo.settleSign\":{$in:[1,3]}},{\"channelSettleInfo\":{\"$exists\":false}}],";
				if(channelSett.size() == 1 && channelSett.contains(2))//结算中
					mongoSql += "\"channelSettleInfo.settleSign\":{$in:[2]},";
			}
			if (mongoSql.endsWith(","))
				mongoSql = mongoSql.substring(0, mongoSql.length() - 1);
			mongoSql += "}";
			System.out.println(mongoSql);
			List<Map<String, Object>> list = new ArrayList<>();
			if (examEndDate != null && examStartDate != null && !examEndDate.isEmpty() && !examStartDate.isEmpty()) {
				list = MongoDBUtils.queryByPageAndExameDate(mongoSql, "insertTime", -1, 0, Integer.valueOf(pageSize),
						examStartDate, examEndDate, MONGO_COLLECTION);
			} else if (insertEndDate != null && insertStartDate != null && !insertEndDate.isEmpty()
					&& !insertStartDate.isEmpty()) {
				list = MongoDBUtils.queryByPageAndInsertTime(mongoSql, "insertTime", -1, 0, Integer.valueOf(pageSize),
						insertStartDate, insertEndDate, MONGO_COLLECTION);
			}

			checkDB(queryOrderList, list);
		}
	}
	
	@SuppressWarnings("deprecation")
	@Test(description = "全选订单---刚好沿用上个用例的", groups = { "qa" }, dataProvider = "queryOrder_order" )
	public void test_04_queryAllOrderInfo(String...args) throws ParseException {
		String orderStatuses = args[1].replaceAll("#", ",");
		String examStartDate = args[2];
		String examEndDate = args[3];
		String insertStartDate = args[4];
		String insertEndDate = args[5];
		String isExport = args[6];
		String hospitalParams = args[7];
		String examCompanyIdParams = args[8];
		String fromSiteParams = args[9];
		String pageSize = args[10];
		String currentPage = args[11];
		String description = args[12];
		String channelSettStr = args[13];

		int hospitalId = -1;
		int examCompanyId = -1;
		int fromSite = -1;
		List<Integer> channelSett = new ArrayList<>();
		OrderQueryParams pairs = new OrderQueryParams();

		if (orderStatuses != null && !orderStatuses.isEmpty()) {
			pairs.setOrderStatuses(ListUtil.StringArraysToIntegerList(orderStatuses.split(",")));
		}
		if (examEndDate != null && examStartDate != null && !examEndDate.isEmpty() && !examStartDate.isEmpty()) {
			pairs.setExamStartDate(simplehms.parse(examStartDate));
			pairs.setExamEndDate(simplehms.parse(examEndDate));
		}
		if (insertEndDate != null && insertStartDate != null && !insertEndDate.isEmpty()
				&& !insertStartDate.isEmpty()) {
			pairs.setInsertStartDate(simplehms.parse(insertStartDate));
			pairs.setInsertEndDate(simplehms.parse(insertEndDate));
		}

		if (isExport != null && !isExport.equals("")) {
			pairs.setIsExport(Boolean.parseBoolean(isExport));
		}
		if(hospitalParams!=null && !hospitalParams.equals("")){
			if(hospitalParams.equals("DEF"))
				hospitalId = Integer.parseInt(DefhospitalId);
			else
				hospitalId = Integer.parseInt(hospitalParams);
			pairs.setHospitalIds(Arrays.asList(hospitalId));

		}

		if(examCompanyIdParams!=null && !examCompanyIdParams.equals("")){
			if(examCompanyIdParams.equals("DEF"))
				examCompanyId = Integer.parseInt(DefexamCompanyId);
			else
				examCompanyId = Integer.parseInt(examCompanyIdParams);
			pairs.setExamCompanyIds(Arrays.asList(examCompanyId));
		}
		if(fromSiteParams!=null&& !fromSiteParams.equals("")){
			if(fromSiteParams.equals("DEF"))
				fromSite = Integer.parseInt(DeffromSite);
			else
				fromSite = Integer.parseInt(fromSiteParams);
			pairs.setFromSites(Arrays.asList(fromSite));
		}

		if(channelSettStr!=null && !channelSettStr.equals("")){
			String[] channelSets = channelSettStr.split("#");
			channelSett = ListUtil.StringArraysToIntegerList(channelSets);
			pairs.setChannelSettlementStatus(channelSett);
		}
		HttpResult result = httpclient.post(Flag.OPS, OpsOrder_QueryAllOrderInfo,JSON.toJSONString(pairs) );
		String body = result.getBody();
        List<Order> orders = null;
        if(result.getCode() == HttpStatus.SC_BAD_REQUEST){
		    String text = JsonPath.read(body,"$.text");
		    Assert.assertEquals(text,"全选订单数量不能多于1000条");
        }else{
            Assert.assertEquals(result.getCode(), HttpStatus.SC_OK,"错误提示："+body);
            orders = JSON.parseArray(body, Order.class);

        }

        if (checkdb) {
            String mongoSql = "{'isDeleted':{$exists:false},";

            if (orderStatuses != null && !orderStatuses.equals("")) {
                mongoSql += "'status':{'$in':[" + orderStatuses.replaceAll("\"", "") + "]},";
            }

            if (isExport != null && !isExport.equals("")) {
                mongoSql += "'isExport':" + isExport+",";
            }

            if(hospitalId !=-1)
                mongoSql += "'orderHospital._id':" + hospitalId+",";
            if(examCompanyId !=-1)
                mongoSql +=  "'examCompanyId':" + examCompanyId+",";
            if(fromSite!=-1)
                mongoSql +=  "'fromSite':" + fromSite+",";
			if(channelSett != null && channelSett.size() > 0){
				if(channelSett.size() == 1 && channelSett.contains(4))//已结算
					mongoSql += "\"channelSettleInfo.settleSign\":{$in:[4]},";
				if(channelSett.size() > 1 && channelSett.contains(1) && channelSett.contains(3))//未结算
					mongoSql += "\"$or\":[{\"channelSettleInfo.settleSign\":{$in:[1,3]}},{\"channelSettleInfo\":{\"$exists\":false}}],";
				if(channelSett.size() == 1 && channelSett.contains(2))//结算中
					mongoSql += "\"channelSettleInfo.settleSign\":{$in:[2]},";
			}
            if(mongoSql.endsWith(","))
                mongoSql = mongoSql.substring(0,mongoSql.length()-1);
            mongoSql += "}";
            List<Map<String, Object>> list = null;
            if (examEndDate != null && examStartDate != null && !examEndDate.isEmpty() && !examStartDate.isEmpty()) {
                list = MongoDBUtils.queryByPageAndExameDate(mongoSql, "insertTime", -1, 0, null,
                        examStartDate, examEndDate, MONGO_COLLECTION);
            } else if (insertEndDate != null && insertStartDate != null && !insertEndDate.isEmpty()
                    && !insertStartDate.isEmpty()) {
                list = MongoDBUtils.queryByPageAndInsertTime(mongoSql, "insertTime", -1, 0, null,
                        insertStartDate, insertEndDate, MONGO_COLLECTION);
            }

            System.out.println(list.size());
            //全选不足1000条
            if(orders != null){
                Assert.assertEquals(orders.size(), list.size());
                for (int i = 0; i < orders.size(); i++) {
                    Order order = orders.get(i);
                    Map<String, Object> map = list.get(i);
//					log.info("订单号"+order.getOrderNum()+"...mongo查询"+list.get(i).get("orderNum").toString());


					// account
                    Object obj = map.get("orderAccount");
                    if(obj != null){
						JSONObject jso = JSONObject.parseObject(obj.toString());
						Assert.assertEquals(order.getOrderAccount().getId()+"", jso.getString("_id"));
						Assert.assertEquals(order.getOrderAccount().getName(), jso.getString("name"));
						Assert.assertEquals(order.getOrderAccount().getIdCard(), jso.getString("idCard"));
						Assert.assertEquals(order.getOrderAccount().getMobile(), jso.getString("mobile"));
						Assert.assertEquals(order.getOrderAccount().getStatus()+"", jso.getString("status"));
						if(jso.get("type")!=null)
							Assert.assertEquals(order.getOrderAccount().getType()+"", jso.getString("type"));
					}



                    Assert.assertEquals(order.getId(), map.get("id"));
                    Assert.assertEquals(order.getIsExport()?"true":"false", map.get("isExport").toString());
                    //Assert.assertEquals(order.getSource(), map.get("source"));
                    Assert.assertEquals(order.getStatus(), map.get("status"),order.getId()+"");

                    //渠道结算批次(bug待修复)
//					Object channelSettObj = map.get("channelSettleInfo");
//					if(channelSettObj!=null){
//						JSONObject jso =  JSONObject.parseObject(channelSettObj.toString());
//						Assert.assertEquals(order.getChannelSettleInfo().getSettleSign().intValue(),jso.getIntValue("settleSign"));
//						Assert.assertEquals(order.getChannelSettleInfo().getSettleBatch(),jso.getString("settleBatch"));
//					}else{
//						Assert.assertNull(order.getChannelSettleInfo());
//					}
                }

            }
            else//全选大于1000条
                Assert.assertTrue(list.size()>1000);




		}
	}

	public void checkDB(JSONArray recordList, List<Map<String, Object>> list) throws ParseException {
		for (int i = 0; i < recordList.size(); i++) {
			JSONObject jo = recordList.getJSONObject(i);
			Map<String, Object> map = list.get(i);
			Assert.assertEquals(jo.getString("id"), map.get("id").toString());
			System.out.println("orderId = " + jo.getString("id"));

			// account
			Object obj = map.get("orderAccount");
			JSONObject jso = JSONObject.parseObject(obj.toString());
			Assert.assertEquals(jo.getJSONObject("orderAccount").getString("id"), jso.getString("_id"));
			Assert.assertEquals(jo.getJSONObject("orderAccount").getString("name"), jso.getString("name"));
			Assert.assertEquals(jo.getJSONObject("orderAccount").getString("idCard"), jso.getString("idCard"));
			Assert.assertEquals(jo.getJSONObject("orderAccount").getString("mobile"), jso.getString("mobile"));
			Assert.assertEquals(jo.getJSONObject("orderAccount").getString("status"), jso.getString("status"));
			Assert.assertEquals(jo.getJSONObject("orderAccount").getString("type"), jso.getString("type"));

			// manager
			Assert.assertEquals(jo.getJSONObject("orderExtInfo").getString("accountManager"), ((BasicDBObject)map.get("orderExtInfo")).getString("accountManager"));

			// discount
			Assert.assertEquals(jo.getString("discount"), map.get("discount").toString());

			// examCompanyId
			Assert.assertEquals(jo.getString("examCompanyId"), map.get("examCompanyId").toString());
			// examCompany
			if(((BasicDBObject)map.get("orderExtInfo")).get("examCompany") !=null)
				Assert.assertEquals(jo.getJSONObject("orderExtInfo").getString("examCompany"), ((BasicDBObject)map.get("orderExtInfo")).getString("examCompany"));
			else// 通过医院单位id查询
				Assert.assertEquals(jo.getJSONObject("orderExtInfo").getString("examCompany"),
						JSONObject.parseObject(map.get("hospitalCompany").toString()).get("name"));

			// examDate
			if (jo.get("examDate") != null) {
				String object = jo.get("examDate").toString();
				long lt = new Long(object);
				Date date = new Date(lt);
				Assert.assertEquals(sdf.format(date), DateUtils.getGMTDateString(map.get("examDate")),"orderId.."+jo.getString("id"));
			} else {
				Assert.assertTrue(map.get("examDate") == null);
			}

			// examTimeIntervalName
			Assert.assertEquals(jo.getString("examTimeIntervalName"), map.get("examTimeIntervalName"));

			// fromSite
			if (jo.getString("fromSite") != null) {
				Assert.assertEquals(jo.getString("fromSite"), map.get("fromSite").toString());
			} else {
				Assert.assertNull(jo.getString("fromSite"));
			}

			// gender
			Assert.assertEquals(jo.getJSONObject("orderExportExtInfo").getString("genderLabel"), ((BasicDBObject)map.get("orderExportExtInfo")).getString("genderLabel"));
			// hospital
			Object hospitalObj = map.get("orderHospital");
			JSONObject hospitalJso = JSONObject.parseObject(hospitalObj.toString());
			Assert.assertEquals(jo.getJSONObject("orderHospital").getString("name"), hospitalJso.getString("name"));
			Assert.assertEquals(jo.getJSONObject("orderHospital").getString("organizationType"),
					hospitalJso.getString("organizationType"));

			// mealName
			Assert.assertEquals(jo.getString("mealName"), map.get("mealName"));

			// offlinePayMoney
			Assert.assertEquals(jo.getString("offlinePayMoney"), map.get("offlinePayMoney"));
			// offlineUnpayMoney
			if (jo.get("offlineUnpayMoney") != null) {
				Assert.assertEquals(jo.getString("offlineUnpayMoney"), map.get("offlineUnpayMoney").toString());
			} else {
				Assert.assertNull(jo.get("offlineUnpayMoney"));
			}

			// operator
			Assert.assertEquals(jo.getString("operator"), map.get("operator"));

			// selfMoney
			if (((BasicDBObject)map.get("orderExtInfo")).get("selfMoney") != null)
				Assert.assertEquals(jo.getJSONObject("orderExtInfo").getIntValue("selfMoney"), ((BasicDBObject)map.get("orderExtInfo")).getInt("selfMoney"));

			//渠道结算批次
			Object channelSettObj = map.get("channelSettleInfo");
			if(channelSettObj!=null){
				JSONObject json1 =  JSONObject.parseObject(channelSettObj.toString());
				Assert.assertEquals(jo.getJSONObject("channelSettleInfo").getIntValue("settleSign"),json1.getIntValue("settleSign"));
				Assert.assertEquals(jo.getJSONObject("channelSettleInfo").getString("settleBatch"),json1.getString("settleBatch"));
			}else{
				Assert.assertNull(jo.getJSONObject("channelSettleInfo"));
			}
			}


	}

	@DataProvider(name = "queryOrder_account")
	public Iterator<String[]> queryOrder_account() {
		try {
			return CvsFileUtil.fromCvsFileToIterator("./csv/order/ops/queryOrder_account.csv", 7);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	@DataProvider(name = "queryOrder_order")
	public Iterator<String[]> queryOrder_order() {
		try {
			return CvsFileUtil.fromCvsFileToIterator("./csv/order/ops/queryOrder_order.csv", 5);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}
}
