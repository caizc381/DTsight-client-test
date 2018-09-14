package com.tijiantest.testcase.crm.order;

import com.alibaba.fastjson.JSONArray;
import com.tijiantest.base.HttpResult;
import com.tijiantest.base.dbcheck.OrderChecker;
import com.tijiantest.model.order.BatchOrderProcess;
import com.tijiantest.testcase.crm.CrmBase;
import com.tijiantest.util.db.SqlException;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * CRM首页任务列表
 * 体检中心操作员|主管|普通客户经理|平台客户经理查看异步任务
 * @author huifang
 *
 */
public class GetOperateHistoryTest extends CrmBase{

	@Test(description = "批量预约历史记录",groups = {"qa"})
	public void test_01_getOperateHistoryTest() throws ParseException, SqlException{
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("processStatus",  "true"));
		HttpResult response = httpclient.get(Order_GetOperateHistory, params);
		Assert.assertEquals(response.getCode(), HttpStatus.SC_OK);
		Assert.assertTrue(response.getBody().equals("[]")||response.getBody().equals(""),"返回"+response.getBody());

		params.clear();;
		params.add(new BasicNameValuePair("processStatus",  "false"));

		 response = httpclient.get(Order_GetOperateHistory, params);
		Assert.assertEquals(response.getCode(), HttpStatus.SC_OK);
		List<BatchOrderProcess> jsonArray = JSONArray.parseArray(response.getBody(),BatchOrderProcess.class);

		if(checkdb){
			List<BatchOrderProcess> dblist = OrderChecker.getOwnerTaskList(defaccountId, Arrays.asList(1), 20);
			for(int i = 0;i<jsonArray.size();i++){
						BatchOrderProcess jObject = jsonArray.get(i);
						BatchOrderProcess map = dblist.get(i);
						log.info(jObject.getId()+"...db..."+map.getId());
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


}
