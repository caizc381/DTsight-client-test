package com.tijiantest.testcase.crm.card;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.tijiantest.base.HttpResult;
import com.tijiantest.testcase.crm.CrmBase;
import com.tijiantest.util.db.DBMapper;
import com.tijiantest.util.db.SqlException;

public class UpdateBatchMsgStatusTest extends CrmBase {

	@Test(description = "更新卡批次的发送短信状态", groups = { "qa" }, dependsOnGroups = "crm_resendCardMsg")
	public void updateBatchMsgStatus() throws SqlException {
		String batchId = ResendCardMsgTest.batchId;
		
		List<NameValuePair> pairs = new ArrayList<>();
		pairs.add(new BasicNameValuePair("batchIds[]", batchId + ""));

		HttpResult result = httpclient.post(Card_UpdateBatchMsgStatus, pairs);
		String body = result.getBody();
		System.out.println(body);
		Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);

		if (checkdb) {
			String sql = "select * from tb_card_batch where id=?";
			List<Map<String, Object>> list = DBMapper.query(sql, batchId);
			Assert.assertEquals(1, list.get(0).get("is_send_card_msg"));
		}
	}
}
