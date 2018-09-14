package com.tijiantest.testcase.crm.order;

import com.alibaba.fastjson.JSON;
import com.tijiantest.base.HttpResult;
import com.tijiantest.base.dbcheck.HospitalChecker;
import com.tijiantest.model.hospital.Hospital;
import com.tijiantest.model.hospital.HospitalParam;
import com.tijiantest.model.order.OrderQueryParams;
import com.tijiantest.model.order.channel.OrderQueryRequestParams;
import com.tijiantest.testcase.crm.CrmBase;
import com.tijiantest.util.CvsFileUtil;
import com.tijiantest.util.ListUtil;
import com.tijiantest.util.MapUtil;
import com.tijiantest.util.db.MongoDBUtils;
import com.tijiantest.util.db.SqlException;
import com.tijiantest.util.pagination.Page;
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
 * 查询结算批次
 * 位置：CRM->订单&用户->订单管理
 * @author huifang
 *
 */
public class SettleBatchTest extends CrmBase{

	boolean isSettleOpen = false;

	@Test(description = "批量查看订单结算批次信息" ,dataProvider="settleBatch" ,groups = {"qa"})
	public void test_01_settleBatch(String ... args) throws ParseException, IOException, SqlException, java.text.ParseException {
		String rowStr = args[1];
		String currentPageStr = args[2];
		String pageSizeStr = args[3];
		String examStartDateStr = args[4];
		String examEndDateStr = args[5];
		String insertStartDateStr = args[6];
		String insertEndDateStr = args[7];
		String hosptialStr = args[8];
		String orderStrs = args[9];
		String settleSignStr = args[10];
		String showImmStr = args[11];
		int rowCount = -1;
		int hospitalId = -1;
		int pageSize = 50;
		int cureentPage = 1;
		String examStartDate = null;
		String examEndDate = null;
		String insertStartDate = null;
		String insertEndDate = null;
		int  settleSign = -1;
		List<Integer> orderStatuses = new ArrayList<Integer>();
		boolean showImmediatelyImpOrder = true;
		OrderQueryParams params = new OrderQueryParams();
		if(!IsArgsNull(rowStr)){
			rowCount = Integer.parseInt(rowStr);
//			params.set
		}
		if(!IsArgsNull(currentPageStr) && !IsArgsNull(pageSizeStr)){
			cureentPage = Integer.parseInt(currentPageStr);
			pageSize = Integer.parseInt(pageSizeStr);
			params.setPage(new Page(cureentPage,pageSize));
		}
		if(!IsArgsNull(examStartDateStr)){
			examStartDate = examStartDateStr;
			params.setExamStartDate(simplehms.parse(examStartDate));
		}
		if(!IsArgsNull(examEndDateStr)){
			examEndDate = examEndDateStr;
			params.setExamEndDate(simplehms.parse(examEndDate));
		}
		if(!IsArgsNull(insertStartDateStr)){
			insertStartDate = insertStartDateStr;
			params.setInsertStartDate(simplehms.parse(insertStartDate));
		}
		if(!IsArgsNull(insertEndDateStr)){
			insertEndDate = insertEndDateStr;
			params.setInsertEndDate(simplehms.parse(insertEndDate));
		}
		if(!IsArgsNull(hosptialStr)){
			hospitalId = Integer.parseInt(hosptialStr);
			params.setHospitalIds(Arrays.asList(hospitalId));
		}
		if(!IsArgsNull(orderStrs)){
			String orders[] = orderStrs.split("#");
			orderStatuses = ListUtil.StringArraysToIntegerList(orders);
			params.setOrderStatuses(orderStatuses);
		}

		if(!IsArgsNull(settleSignStr)){
			settleSign = Integer.parseInt(settleSignStr);
			params.setSettleSign(settleSign);
			params.setSettleSigns(Arrays.asList(settleSign));
		}
		if(!IsArgsNull(showImmStr)){
			showImmediatelyImpOrder = Boolean.parseBoolean(showImmStr);
			params.setShowImmediatelyImpOrder(showImmediatelyImpOrder);
		}
		HttpResult result = httpclient.post(Order_SettleBatch,JSON.toJSONString(params));
		Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);
		String body = result.getBody();
		log.info("获取订单批次返回.."+ body);
		List<String> retList = JSON.parseArray(body,String.class);
		Map<String,Object> settings = HospitalChecker.getHospitalSetting(hospitalId, HospitalParam.SETTLEMENT_TIME, HospitalParam.SETTLEMENT_OPEN);
		if(Integer.parseInt(settings.get(HospitalParam.SETTLEMENT_OPEN).toString()) == 1){
			Object time = settings.get(HospitalParam.SETTLEMENT_TIME);
			if(time != null){
				if (sdf.parse(time.toString()).compareTo(new Date()) < 0)
					isSettleOpen = true;
			}
		}
		//新结算，结算批次返回为空
		if(isSettleOpen)
			Assert.assertEquals(body,"[]");
		else
			if(checkmongo) {
				String querySql = "{";
			if(hospitalId != -1)
				querySql += "\"hospital._id\":"+hospitalId+"";
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
			querySql += ",\"settleBatch\":{$nin:[null]}";
			querySql+= "}";
			log.info("querySql"+querySql);
			List<Map<String, Object>> mongoList = null;
			if(insertStartDate != null && insertEndDate != null)
			  mongoList = MongoDBUtils.queryByPageAndInsertTime(querySql, "insertTime", -1, 0,
					Integer.valueOf(pageSize), insertStartDate,insertEndDate,MONGO_COLLECTION);
			if(examStartDate != null && examEndDate != null)
				mongoList = MongoDBUtils.queryByPageAndExameDate(querySql, "insertTime", -1, 0,
						Integer.valueOf(pageSize), examStartDate,examEndDate,MONGO_COLLECTION);
//			log.info("mongoList..."+mongoList);
			List<String> dbSettleBatchList = new ArrayList<String>();
			for (int i = 0; i < mongoList.size(); i++) {
				Map<String, Object> mogoMap = mongoList.get(i);
				// 批次号
				String batch = mogoMap.get("settleBatch").toString();
				if(dbSettleBatchList.contains(batch))
					continue;
				dbSettleBatchList.add(batch);
			}
			Assert.assertEquals(retList.size(),dbSettleBatchList.size());
			Collections.sort(dbSettleBatchList);
			Collections.sort(retList);
			log.info("dbSettleBatchList..."+dbSettleBatchList);
			log.info("retList..."+dbSettleBatchList);
			Assert.assertEquals(retList,dbSettleBatchList);

		}
	}
	
	@DataProvider
	public Iterator<String[]> settleBatch(){
		try {
			return CvsFileUtil.fromCvsFileToIterator("./csv/order/settleBatch.csv",7);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}
}

