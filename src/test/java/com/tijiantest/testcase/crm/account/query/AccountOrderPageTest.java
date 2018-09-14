package com.tijiantest.testcase.crm.account.query;

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
import com.alibaba.fastjson.TypeReference;
import com.jayway.jsonpath.JsonPath;
import com.tijiantest.base.HttpResult;
import com.tijiantest.model.order.Order;
import com.tijiantest.testcase.crm.CrmMediaBase;
import com.tijiantest.util.CvsFileUtil;
import com.tijiantest.util.db.MongoDBUtils;
import com.tijiantest.util.db.SqlException;

/**
 * 订单分页
 * CRM->订单&用户->用户查询->订单分页页面
 * @author huifang
 *
 */
public class AccountOrderPageTest extends CrmMediaBase{

	
	@Test(description = "订单分页查询" ,dataProvider="order_page" ,groups = {"qa","online"})
	public void test_01_orderpage_success(String ... args) throws ParseException, IOException, SqlException{
		int accountId = sankeAccountId;
		int rowcount = Integer.parseInt(args[2]);
		int currPage = Integer.parseInt(args[3]);
		int pageSize = Integer.parseInt(args[4]);
//		List<NameValuePair> params = new ArrayList<NameValuePair>();
//		params.add(new BasicNameValuePair("hospitalId", defhospital.getId()+""));
//		params.add(new BasicNameValuePair("accountId",accountId+""));
//		params.add(new BasicNameValuePair("rowCount",rowcount+""));
//		params.add(new BasicNameValuePair("currentPage",currPage+""));
//		params.add(new BasicNameValuePair("pageSize",pageSize+""));
		OrderQueryParams params = new OrderQueryParams();
		params.setHospitalIds(Arrays.asList(defhospital.getId()));
		params.setAccountIds(Arrays.asList(accountId));
		Page p = new Page(currPage,pageSize);
		p.setRowCount(rowcount);
		params.setPage(p);
		HttpResult result = httpclient.post(AccountQuery_AccountOrderPage,JSON.toJSONString(params));
		Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);
		
		String jbody = result.getBody();
		int page =  JsonPath.read(jbody,"$.page.pageSize");
		int curPage = JsonPath.read(jbody,"$.page.currentPage");
		int rowCount = JsonPath.read(jbody,"$.page.rowCount");
		String records = JsonPath.read(jbody,"$.records").toString();
		Assert.assertEquals(page,pageSize);
		Assert.assertEquals(curPage, currPage);
		log.info(jbody);
		List<Order> list = JSON.parseObject(records,new TypeReference<List<Order>>(){});
		Assert.assertNotNull(jbody);
		if(checkmongo){
			waitto(mongoWaitTime);
			String sql = "{'orderAccount._id':"+accountId+",'orderHospital._id':"+defhospital.getId()+"}";
			System.out.println(sql);
			List<Map<String,Object>> mogolist = MongoDBUtils.queryByPage(sql, "insertTime",-1,0,pageSize,MONGO_COLLECTION);
			Assert.assertEquals(mogolist.size(),list.size());
			for(int i=0;i<mogolist.size();i++){
				Assert.assertEquals(Integer.parseInt(mogolist.get(i).get("id").toString()),list.get(i).getId());
				Assert.assertEquals(Integer.parseInt(mogolist.get(i).get("orderPrice").toString()),list.get(i).getOrderPrice().intValue());
				Assert.assertEquals(Integer.parseInt(mogolist.get(i).get("status").toString()),list.get(i).getStatus());
				Assert.assertEquals(Integer.parseInt(mogolist.get(i).get("batchId").toString()),list.get(i).getBatchId());
				Assert.assertEquals(Boolean.parseBoolean(mogolist.get(i).get("isExport").toString()),list.get(i).getIsExport().booleanValue());
			}
			
			mogolist = MongoDBUtils.query(sql,MONGO_COLLECTION);
			Assert.assertEquals(rowCount,mogolist.size());
		}
		
	}
	
	@DataProvider
	public Iterator<String[]> order_page(){
		try {
			return CvsFileUtil.fromCvsFileToIterator("./csv/account/query/order_page.csv",7);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}
}

