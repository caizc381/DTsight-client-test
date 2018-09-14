package com.tijiantest.testcase.ops.order;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.tijiantest.base.Flag;
import com.tijiantest.base.HttpResult;
import com.tijiantest.testcase.ops.OpsBase;
import com.tijiantest.util.CvsFileUtil;
import com.tijiantest.util.db.DBMapper;
import com.tijiantest.util.db.SqlException;

/**
 * 订单管理- 发送短信
 * 
 * @author admin
 *
 */
public class BatchSendMsgTest extends OpsBase {

	@Test(description = "订单管理 - 发送短信", groups = { "qa" }, dependsOnGroups = { "ops_queryOrder" },dataProvider="batchSendMsg")
	public void test_batchSendMsg(String... args) throws SqlException {
		String msg = args[1];
		// 先获取订单
		JSONArray queryOrderList = QueryOrderTest.queryOrderList;
		if (queryOrderList.size() == 0) {
			log.info("没有可发送短信的订单！！！！");
			return;
		}
		//取第一个订单
		JSONObject o = queryOrderList.getJSONObject(0);
		JSONObject hosptial = o.getJSONObject("orderHospital");
		String hospitalId = hosptial.getString("id");
		String orderId = o.getString("id");
		List<NameValuePair> pairs = new ArrayList<>();
		pairs.add(new BasicNameValuePair("msg", msg));
		pairs.add(new BasicNameValuePair("hospitalId", hospitalId));
		pairs.add(new BasicNameValuePair("orderIds", "["+orderId+"]"));

		String beforeDate = simplehms.format(DBMapper.query("select now()").get(0).get("now()"));
		HttpResult result = httpclient.post(Flag.OPS, OpsOrder_BatchSendMsg,pairs);
		String body = result.getBody();
		System.out.println(body);
		Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);
		
		JSONObject examiner = o.getJSONObject("examiner");
		String mobile = examiner.getString("mobile");
		System.out.println(mobile);
		
		if (checkdb) {
			if (mobile == null || mobile.equals("")) {
				log.info("没有手机号！！！");
				return;
			}
			if(mobile.startsWith("10") || mobile.startsWith("123") || mobile.startsWith("11")){
				log.info("手机号格式错误!!");
				return;
			}


			boolean isSend = false;
			//重複等待多次
			for(int i=0;i<5;i++){
				String sql = "select * from tb_sms_send_record where mobile = '"+mobile+"' and content = '"+msg+"'  and insert_time >= '"+beforeDate+"' order by id desc";
				log.info(sql);
				List<Map<String,Object>> dblist = DBMapper.query(sql);
				if(dblist.size() == 0){
					waitto(1);
					continue;
				}
				isSend = true;
				Assert.assertEquals(dblist.size(),1);
			}
			if(!isSend){
				Assert.assertEquals(1,0,"嘗試了5次，在DB中未查詢到短信内容");
			}
		}
	}
	
	
	@DataProvider(name = "batchSendMsg")
	public Iterator<String[]> batchSendMsg() {
		try {
			return CvsFileUtil.fromCvsFileToIterator("./csv/order/ops/batchSendMsg.csv", 7);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}
}
