package com.tijiantest.testcase.crm.order;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONPath;
import com.jayway.jsonpath.JsonPath;
import com.mongodb.BasicDBObject;
import com.tijiantest.base.HttpResult;
import com.tijiantest.model.order.channel.OrderQueryRequestParams;
import com.tijiantest.testcase.crm.CrmBase;
import com.tijiantest.util.CvsFileUtil;
import com.tijiantest.util.ListUtil;
import com.tijiantest.util.MapUtil;
import com.tijiantest.util.db.MongoDBUtils;
import com.tijiantest.util.db.SqlException;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;


/**
 * 立即统计
 * 位置：CRM->订单&用户->体检订单->立即统计
 * @author huifang
 *
 */
public class TotalMoneyTest extends CrmBase{

	@Test(description = "立即统计" ,dataProvider="totalMoney" ,groups = {"qa"})
	public void test_01_totalMoney(String ... args) throws ParseException, IOException, SqlException, java.text.ParseException {
		String examStartDateStr = args[1];
		String examEndDateStr = args[2];
		String insertStartDateStr = args[3];
		String insertEndDateStr = args[4];
		String hosptialStr = args[5];
		String orderStrs = args[6];
		String settleSignStr = args[7];
		String showImmStr = args[8];
		int hospitalId = -1;
		String examStartDate = null;
		String examEndDate = null;
		String insertStartDate = null;
		String insertEndDate = null;
		int  settleSign = -1;
		List<Integer> orderStatuses = new ArrayList<Integer>();
		boolean showImmediatelyImpOrder = true;
		OrderQueryRequestParams params = new OrderQueryRequestParams();
		if(!IsArgsNull(examStartDateStr)){
			examStartDate = examStartDateStr;
			params.setExamStartDate(examStartDate);
		}
		if(!IsArgsNull(examEndDateStr)){
			examEndDate = examEndDateStr;
			params.setExamEndDate(examEndDate);
		}
		if(!IsArgsNull(insertStartDateStr)){
			insertStartDate = insertStartDateStr;
			params.setInsertStartDate(insertStartDate);
		}
		if(!IsArgsNull(insertEndDateStr)){
			insertEndDate = insertEndDateStr;
			params.setInsertEndDate(insertEndDate);
		}
		if(!IsArgsNull(hosptialStr)){
			hospitalId = Integer.parseInt(hosptialStr);
			params.setHospitalId(hospitalId);
		}
		if(!IsArgsNull(orderStrs)){
			String orders[] = orderStrs.split("#");
			orderStatuses = ListUtil.StringArraysToIntegerList(orders);
			params.setOrderStatuses(""+orderStatuses+"");
		}

		if(!IsArgsNull(settleSignStr)){
			settleSign = Integer.parseInt(settleSignStr);
			params.setSettleSign(settleSign);
		}
		if(!IsArgsNull(showImmStr)){
			showImmediatelyImpOrder = Boolean.parseBoolean(showImmStr);
			params.setShowImmediatelyImpOrder(showImmediatelyImpOrder);
		}
//		List<NameValuePair>  nameValuePairs = MapUtil.parseJSON2NameValuePairs(JSON.toJSONString(params));
		HttpResult result = httpclient.post(Order_TotalMoney,JSON.toJSONString(params));
		String body = result.getBody();
		log.info("立即统计返回.."+ body);
		Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);

		double totalOfflinePayMoney = Double.parseDouble(JsonPath.read(body,"$.totalOfflinePayMoney").toString());
		double totalOrderPrice = Double.parseDouble(JsonPath.read(body,"$.totalOrderPrice").toString());
		double totalSelfMoney = Double.parseDouble(JsonPath.read(body,"$.totalSelfMoney").toString());

		double dbTotalOfflienPayMoney = 0l;
		int dbtotalOrderPriceInt = 0;
		double dbtotalSelfMoney = 0l;
		if(checkmongo) {
			String querySql = "{";
			if(hospitalId != -1)
				querySql += "\"orderHospital._id\":"+hospitalId+"";
			if(orderStatuses != null && orderStatuses.size() > 0)
				querySql += ",\"status\":{$in:["+ListUtil.IntegerlistToString(orderStatuses)+"]}";
			if(settleSign != -1){
				if(settleSign == 3)//已结算
					querySql += ",\"settleSign\":{$in:[4,5]}";
				if(settleSign == 4)//未结算
					querySql += ",\"settleSign\":{$in:[3,6,null]}";
				if(settleSign == 2)//结算中
					querySql += ",\"settleSign\":{$in:[2]}";
			}
			querySql+= "}";
			log.info("querySql"+querySql);
			List<Map<String, Object>> mongoList = null;
			if(insertStartDate != null && insertEndDate != null)
			  mongoList = MongoDBUtils.queryByPageAndInsertTime(querySql, "insertTime", -1, 0,
					null, insertStartDate,insertEndDate,MONGO_COLLECTION);
			if(examStartDate != null && examEndDate != null)
				mongoList = MongoDBUtils.queryByPageAndExameDate(querySql, "insertTime", -1, 0,
						null, examStartDate,examEndDate,MONGO_COLLECTION);
			for (int i = 0; i < mongoList.size(); i++) {
				Map<String, Object> mogoMap = mongoList.get(i);
				double offlinePayMoney = Double.parseDouble(mogoMap.get("offlinePayMoney").toString());
				int orderPriceInt = Integer.parseInt(mogoMap.get("orderPrice").toString());
				double selfMoney = ((BasicDBObject)mogoMap.get("orderExtInfo")).getInt("selfMoney")/100;
				dbTotalOfflienPayMoney += offlinePayMoney;
				dbtotalOrderPriceInt += orderPriceInt;
				dbtotalSelfMoney += selfMoney;

			}
			log.info("返回,线下支付之和:"+totalOfflinePayMoney+";订单金额之和:"+totalOrderPrice+";线上支付之和:"+totalSelfMoney);
			log.info("数据库线下支付之和:"+dbTotalOfflienPayMoney+";订单金额之和:"+dbtotalOrderPriceInt+";线上支付之和:"+dbtotalSelfMoney);

			Assert.assertEquals(totalOfflinePayMoney,dbTotalOfflienPayMoney);
			Assert.assertEquals((int)(totalOrderPrice*100),dbtotalOrderPriceInt);
			Assert.assertEquals(totalSelfMoney,dbtotalSelfMoney);

		}
	}
	
	@DataProvider
	public Iterator<String[]> totalMoney(){
		try {
			return CvsFileUtil.fromCvsFileToIterator("./csv/order/totalMoney.csv",7);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}
}

