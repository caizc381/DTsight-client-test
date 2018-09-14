package com.tijiantest.testcase.crm.order.ordermanage;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;

import com.tijiantest.model.order.OrderQueryParams;
import com.tijiantest.util.pagination.Page;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.message.BasicNameValuePair;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jayway.jsonpath.JsonPath;
import com.tijiantest.base.HttpResult;
import com.tijiantest.testcase.crm.CrmBase;
import com.tijiantest.util.CvsFileUtil;
import com.tijiantest.util.db.MongoDBUtils;

/**
 * 接口：根据页码查询订单
 * 参数：examStartDate,examEndDate,insertStartDate,insertEndDate,hospitalId,examCompanyId,gender,orderStatus,isOfflinePayMoneyZero,currentPage,pageSize
 * GET
 * @author XpCHen
 *
 */
public class QueryOrderTest extends CrmBase{
	/**
	 * 必选参数的验证
	 * @param args
	 * @throws IOException
	 * @throws ParseException
	 */
	//@Test(dataProvider = "queryOrderSuccess",groups = {"qa"}, description = "验证成功查询订单1")
	public void test_01_queryOrderTest_success(String ... args)throws ParseException, IOException{
		//get input & output from casefile
	    int currentPage = Integer.parseInt(args[3]);
	    int pageSize = Integer.parseInt(args[4]);
		// make parameters
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("hospitalId", defhospital.getId()+""));
	    params.add(new BasicNameValuePair("currentPage", currentPage+""));
	    params.add(new BasicNameValuePair("pageSize", pageSize+""));
	    
		//get
		HttpResult response = httpclient.post(Order_QueryOrder, params);
		//assert
		Assert.assertEquals(response.getCode() , HttpStatus.SC_OK);
		String body = response.getBody();
		int pageSize1 = JsonPath.read(body, "$page.pageSize");
		int currentPage1 = JsonPath.read(body, "$page.currentPage");
		int count = JsonPath.read(body, "$page.rowCount");
		Assert.assertEquals(pageSize1,pageSize);
		Assert.assertEquals(currentPage1,currentPage);
		
		JSONObject JsonArrayList = JSON.parseObject(body);
		String records = JsonArrayList.get("records").toString();
		JSONArray recordList = JSONArray.parseArray(records);
		
		if(checkmongo){
			log.info("开始验证mongo");
			String sql = "{'hospital._id':"+defhospital.getId()+",'isDeleted':{$exists:false}}";
			List<Map<String, Object>> list1 = MongoDBUtils.queryByPage(sql, "insertTime",-1 ,0, pageSize ,MONGO_COLLECTION);
			List<Map<String, Object>> list2 = MongoDBUtils.query(sql,MONGO_COLLECTION);
			Assert.assertEquals(count, list2.size());
			
			for (int i = 0; i < recordList.size(); i++){
				JSONObject jo = recordList.getJSONObject(i);
				Assert.assertEquals(jo.getString("id"), list1.get(i).get("id").toString());
				
				Object obj =  list1.get(i).get("account");
				JSONObject jso = JSONObject.parseObject(obj.toString());	
				Assert.assertEquals(jo.getJSONObject("account").getString("_id"), jso.getString("_id"));
			}
			log.info("查询条件1mongo验证正确！");
			
		}
		
	}
	
	/**
	 * 可选参数的验证
	 * @param hospitalId,currentPage,pageSize,examStartDate,examEndDate,status,companyid...
	 * @throws IOException
	 * @throws ParseException
	 * @throws java.text.ParseException
	 */
	@Test(dataProvider = "queryOrderSuccess",groups = {"qa"}, description = "验证成功查询订单2")
	public void test_02_queryOrderTest_success(String ... args)throws IOException, ParseException, java.text.ParseException {
		//get input & output from casefile
	    String examStartDate= args[1];
	    String examEndDate = args[2];
	    int currentPage = Integer.parseInt(args[3]);
	    int pageSize = Integer.parseInt(args[4]);
	    int status = Integer.parseInt(args[5]);
	    int companyid = Integer.parseInt(args[6]);

		// make parameters
//	    List<NameValuePair> params = new ArrayList<NameValuePair>();
//		params.add(new BasicNameValuePair("examStartDate", examStartDate));
//		params.add(new BasicNameValuePair("examEndDate", examEndDate));
//		params.add(new BasicNameValuePair("hospitalId", defhospital.getId()+""));
//	    params.add(new BasicNameValuePair("currentPage", currentPage+""));
//	    params.add(new BasicNameValuePair("pageSize", pageSize+""));
//	    params.add(new BasicNameValuePair("orderStatus", status+""));
//	    params.add(new BasicNameValuePair("examCompanyId", companyid+""));
		OrderQueryParams params = new OrderQueryParams();
		params.setExamStartDate(simplehms.parse(examStartDate));
		params.setExamEndDate(simplehms.parse(examEndDate));
		params.setHospitalIds(Arrays.asList(defhospital.getId()));
		params.setPage(new Page(currentPage,pageSize));
		params.setOrderStatuses(Arrays.asList(status));
		params.setExamCompanyIds(Arrays.asList(companyid));
		//get
		HttpResult response = httpclient.post(Order_QueryOrder, JSON.toJSONString(params));
		//assert
		Assert.assertEquals(response.getCode() , HttpStatus.SC_OK);
		String body = response.getBody();
		log.info(body);
		int pageSize1 = JsonPath.read(body, "$.orderListPage.page.pageSize");
		int currentPage1 = JsonPath.read(body, "$.orderListPage.page.currentPage");
		int count = JsonPath.read(body, "$.orderListPage.page.rowCount");
		Assert.assertEquals(pageSize1,pageSize);
		Assert.assertEquals(currentPage1,currentPage);

		JSONArray recordList = JSONArray.parseArray(JsonPath.read(body,"$.orderListPage.records").toString());
				
		if(checkmongo){
			log.info("开始验证mongo");
			
			String sql = "{\"hospital._id\":"+defhospital.getId()+",\"status\":"+status+",\"examCompanyId\":"+companyid+",\"isDeleted\":{$exists:false}}";
			List<Map<String, Object>> list1 = MongoDBUtils.queryByExamDate(sql, examStartDate, examEndDate, MONGO_COLLECTION);
			List<Map<String, Object>> list2 = MongoDBUtils.queryByPageAndExameDate(sql, "insertTime", -1, 0, pageSize, examStartDate, examEndDate, MONGO_COLLECTION);
			Assert.assertEquals(count,list1.size());

			for (int i = 0; i < recordList.size(); i++){
				JSONObject jo = recordList.getJSONObject(i);
				Assert.assertEquals(jo.getString("id"), list2.get(i).get("id").toString());
				
				Object obj =  list2.get(i).get("account");
				JSONObject jso = JSONObject.parseObject(obj.toString());	
				Assert.assertEquals(jo.getJSONObject("account").getString("_id"), jso.getString("_id"));
			}
			log.info("查询条件2mongo验证正确！");
			
		}		
		
	}
	
	@DataProvider
	public Iterator<String[]> queryOrderSuccess(){
		try {
			return CvsFileUtil.fromCvsFileToIterator("./csv/order/manage/queryOrderSuccess.csv", 6);
		}catch (FileNotFoundException e) {
			e.printStackTrace();
		}catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

}
