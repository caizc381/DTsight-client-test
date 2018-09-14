package com.tijiantest.testcase.crm.card;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.tijiantest.base.Flag;
import com.tijiantest.base.HttpResult;
import com.tijiantest.model.card.Card;
import com.tijiantest.testcase.crm.CrmBase;
import com.tijiantest.util.db.DBMapper;
import com.tijiantest.util.db.SqlException;

public class CleanCardRecordTest extends CrmBase {

	@Test(description = "设置客户为未发卡", groups = { "qa" }, dependsOnGroups = "crm_distributeCard")
	public void test_01_distributeCard() throws SqlException {
		List<Card> cardList = DistributeCardTest.cardList1;
		List<Integer> accountIds = DistributeCardTest.accountIds;

		List<NameValuePair> pairs = new ArrayList<>();
		for (int i = 0; i < cardList.size(); i++) {
			pairs.add(new BasicNameValuePair("cardIds[]", cardList.get(i).getId() + ""));
		}

		HttpResult result = httpclient.post(Flag.CRM, Card_CleanCardRecord, pairs);

		String body = result.getBody();
		System.out.println(body);
		Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);

		if (checkdb) {

			for (int i = 0; i < accountIds.size(); i++) {
				String relationSql = "select * from tb_examiner where customer_id=? and manager_id=?";
				List<Map<String, Object>> relationList = DBMapper.query(relationSql, accountIds.get(i), defaccountId);
				System.out.println("recent_card = " + relationList.get(0).get("recent_card"));

				Assert.assertNull(relationList.get(0).get("recent_card"));
			}
		}
	}
}
