package com.tijiantest.testcase.crm.order.ordermanage;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;

import com.tijiantest.model.order.OrderQueryParams;
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
import com.tijiantest.base.HttpResult;
import com.tijiantest.testcase.crm.CrmBase;
import com.tijiantest.util.CvsFileUtil;
import com.tijiantest.util.db.MongoDBUtils;
/**
 * 接口：订单全选
 * 参数：startDate, endDate,hospitalId,examCompanyId,gender,orderStatus
 * POST
 * @author XpCHen
 *
 */
public class SelectAllOrderTest extends CrmBase{

	@Test(dataProvider = "selectAllOrderSuccess",groups = {"qa"}, description = "验证成功全选订单")
	public void test_01_selectAllOrderTest_success(String ... args)throws ParseException, IOException, java.text.ParseException{
		//get input & output from casefile
	    String examStartDate = args[1];
	    String examEndDate = args[2];
	    int orderStatus = Integer.parseInt(args[3]);
	    int examCompanyId = Integer.parseInt(args[4]);
	    int gender = Integer.parseInt(args[5]);
	    
		// make parameters
//	    List<NameValuePair> params = new ArrayList<NameValuePair>();
//		params.add(new BasicNameValuePair("examStartDate", examStartDate));
//		params.add(new BasicNameValuePair("examEndDate", examEndDate));
//		params.add(new BasicNameValuePair("hospitalId", defhospital.getId()+""));
//		params.add(new BasicNameValuePair("orderStatus", orderStatus+""));
//		params.add(new BasicNameValuePair("examCompanyId", examCompanyId+""));
//		params.add(new BasicNameValuePair("gender", gender+""));
		OrderQueryParams query = new OrderQueryParams();
		query.setExamStartDate(simplehms.parse(examStartDate));
		query.setExamEndDate(simplehms.parse(examEndDate));
		query.setHospitalIds(Arrays.asList(defhospital.getId()));
		query.setOrderStatuses(Arrays.asList(orderStatus));
		query.setExamCompanyIds(Arrays.asList(examCompanyId));
		query.setGender(gender);
		//post
		HttpResult response = httpclient.post(Order_SelectAllOrder, JSON.toJSONString(query));
		//assert
		Assert.assertEquals(response.getCode() , HttpStatus.SC_OK);
		String body = response.getBody();
		JSONArray jsonArray = JSON.parseArray(body);
	    int count = jsonArray.size();
		
		if(checkmongo){
			log.info("开始验证mongo");
			String sql = "{'hospital._id':"+defhospital.getId()+",\"status\":"+orderStatus+",\"examCompanyId\":"+examCompanyId+",\"accountRelation.gender\":"+gender+",\"isDeleted\":{$exists:false}}";
			List<Map<String, Object>> list1 = MongoDBUtils.queryByExamDate(sql, examStartDate, examEndDate, MONGO_COLLECTION);
			List<Map<String, Object>> list2 = MongoDBUtils.queryByPageAndExameDate(sql, "insertTime", -1, 0, 20, examStartDate, examEndDate, MONGO_COLLECTION);
			Assert.assertEquals(count, list1.size());
			
			for (int i = 0; i < list2.size(); i++){
				JSONObject jo = jsonArray.getJSONObject(i);
				Assert.assertEquals(jo.getString("id"), list2.get(i).get("id").toString());
				
				Object obj =  list2.get(i).get("account");
				JSONObject jso = JSONObject.parseObject(obj.toString());	
				Assert.assertEquals(jo.getJSONObject("account").getString("idCard"), jso.getString("idCard"));				
				
			}
			log.info("mongo验证正确！");
		}
	}
	
	@DataProvider
	public Iterator<String[]> selectAllOrderSuccess(){
		try {
			return CvsFileUtil.fromCvsFileToIterator("./csv/order/manage/selectAllOrderSuccess.csv", 5);
		}catch (FileNotFoundException e) {
			e.printStackTrace();
		}catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}		

}
