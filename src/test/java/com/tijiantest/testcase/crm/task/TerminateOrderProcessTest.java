package com.tijiantest.testcase.crm.task;

import com.alibaba.fastjson.JSONArray;
import com.tijiantest.base.BaseTest;
import com.tijiantest.base.Flag;
import com.tijiantest.base.HttpResult;
import com.tijiantest.base.MyHttpClient;
import com.tijiantest.base.dbcheck.AccountChecker;
import com.tijiantest.base.dbcheck.OrderChecker;
import com.tijiantest.model.order.BatchOrderProcess;
import com.tijiantest.testcase.crm.CrmBase;
import com.tijiantest.util.CvsFileUtil;
import com.tijiantest.util.db.DBMapper;
import com.tijiantest.util.db.SqlException;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * CRM首页任务列表
 * 停止进行中的任务
 * @author huifang
 *
 */
public class TerminateOrderProcessTest extends CrmBase{

	@Test(description = "停止异步任务",groups = {"qa"})
	public void test_01_terminateOrderProcess() throws ParseException, SqlException{

		List<Map<String,Object>> paramList = DBMapper.query("select * from tb_batch_order_process where operator_id = "+defaccountId + " and status = 2 order by id desc limit 1");
		int process_id = Integer.parseInt(paramList.get(0).get("id").toString());
		DBMapper.update("update tb_batch_order_process set status = 1 where id = "+process_id);

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("processId", process_id + ""));

		HttpResult response = httpclient.post(Order_TerminateOrderProcess, params);
		Assert.assertEquals(response.getCode(), HttpStatus.SC_OK);
		Assert.assertEquals(response.getBody(),"{}");

		if(checkdb){
			BatchOrderProcess process = OrderChecker.getBatchProcessById(process_id);
			Assert.assertEquals(process.getStatus().intValue(),2);
				
		}
	}


}
