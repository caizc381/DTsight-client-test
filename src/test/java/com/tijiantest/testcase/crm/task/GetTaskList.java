package com.tijiantest.testcase.crm.task;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.alibaba.fastjson.JSONArray;
import com.tijiantest.base.BaseTest;
import com.tijiantest.base.Flag;
import com.tijiantest.base.HttpResult;
import com.tijiantest.base.MyHttpClient;
import com.tijiantest.base.dbcheck.AccountChecker;
import com.tijiantest.base.dbcheck.OrderChecker;
import com.tijiantest.model.order.BatchOrderProcess;
import com.tijiantest.util.CvsFileUtil;
import com.tijiantest.util.db.SqlException;
/**
 * CRM首页任务列表
 * 体检中心操作员|主管|普通客户经理|平台客户经理查看异步任务
 * @author huifang
 *
 */
public class GetTaskList extends BaseTest{

	@Test(description = "异步任务列表",groups = {"qa"},dataProvider = "task_role")
	public void test_01_tasklist(String ...args) throws ParseException, SqlException{
		String username = args[1];
		String password = args[2];
		int hospitalId = Integer.parseInt(args[3]);
		int operator_id = AccountChecker.getUserInfo(username).getAccount_id();
		MyHttpClient myClient = new MyHttpClient();
		onceLoginInSystem(myClient, Flag.CRM, username, password);
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("hospitalId", hospitalId + ""));

		HttpResult response = myClient.get(GetTaskList, params);
		Assert.assertEquals(response.getCode(), HttpStatus.SC_OK);
		List<BatchOrderProcess> jsonArray = JSONArray.parseArray(response.getBody(),BatchOrderProcess.class);
		onceLogOutSystem(myClient, Flag.CRM);
		
		if(checkdb){
			List<BatchOrderProcess> dblist = OrderChecker.getRoleTaskList(hospitalId, operator_id, 10);
			for(int i = 0;i<jsonArray.size();i++){
						BatchOrderProcess jObject = jsonArray.get(i);
						BatchOrderProcess map = dblist.get(i);
						log.info(jObject.getId()+"...db..."+map.getId()+"taskContent"+jObject.getTaskContent());
						Assert.assertEquals(jObject.getDescription(),map.getDescription());
						Assert.assertEquals(jObject.getTotalNum(),map.getTotalNum());
						Assert.assertEquals(jObject.getDealNum(),map.getDealNum());
						Assert.assertEquals(jObject.getFailNum(),map.getFailNum());
						Assert.assertEquals(jObject.getSuccessNum(),map.getSuccessNum());
						Assert.assertEquals(jObject.getStatus(),map.getStatus());
						Assert.assertEquals(jObject.getOperatorId(),map.getOperatorId());
						Assert.assertEquals(jObject.getTaskContent(),map.getTaskContent());
						Assert.assertEquals(jObject.getTaskType(),map.getTaskType());
						Assert.assertEquals(jObject.getGmtCreated().getTime(),map.getGmtModified().getTime(),0.0002E12);
						Assert.assertEquals(jObject.getGmtModified().getTime(),map.getGmtModified().getTime(),0.0002E12);
			
		    }
				
		}
	}


	@DataProvider
	public Iterator<String[]> task_role() {
		try {
			return CvsFileUtil.fromCvsFileToIterator("./csv/task/task_role.csv", 4);
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
