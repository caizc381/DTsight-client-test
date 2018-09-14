package com.tijiantest.testcase.crm.card;

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

import com.tijiantest.base.Flag;
import com.tijiantest.base.HttpResult;
import com.tijiantest.base.MyHttpClient;
import com.tijiantest.testcase.crm.CrmBase;
import com.tijiantest.util.AssertUtil;
import com.tijiantest.util.CvsFileUtil;
import com.tijiantest.util.GenerateOrderNum;
import com.tijiantest.util.db.DBMapper;
import com.tijiantest.util.db.SqlException;

public class SetCardSegmentTest extends CrmBase {
	MyHttpClient myClient = new MyHttpClient();

	@Test(description = "平台客户经理发卡- 异常处理", groups = { "qa" }, dataProvider = "setCardSegment")
	public void test_01_setCardSegment(String... args) throws SqlException {
		// 平台客户经理登录
		onceLoginInSystem(myClient, Flag.CRM, defPlatUsername,defPlatPasswd);
		String startCardNum = args[1];
		String endCardNum = args[2];

		List<NameValuePair> pairs = new ArrayList<>();
		pairs.add(new BasicNameValuePair("startCardNum", startCardNum));
		pairs.add(new BasicNameValuePair("endCardNum", endCardNum));

		HttpResult result = myClient.post(Card_SetCardSegment, pairs);
		String body = result.getBody();
		System.out.println(body);
		Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);

		if (checkdb) {
			String segments = "";
			for (int i = Integer.valueOf(startCardNum); i <= Integer.valueOf(endCardNum); i++) {
				String cardNum = GenerateOrderNum.getEntryCardnum(i);
				segments += "'MT" + cardNum + "',";
			}

			if (!segments.isEmpty()) {
				segments = segments.substring(0, segments.lastIndexOf(","));
				String sql = "select * from tb_card_segment where card_num in (" + segments + ")";
				List<Map<String, Object>> list = DBMapper.query(sql);

				if (AssertUtil.isEmpty(list)) {
					Assert.assertTrue(body.contains("false"));
					return;
				}

				sql = "select * from tb_card where status=1 and type=2 and card_num in (" + segments + ")";
				list = DBMapper.query(sql);

				if (AssertUtil.isEmpty(list)) {
					Assert.assertTrue(body.contains("false"));
					return;
				}
			}
		}

		// 平台客户经理登出
		onceLogOutSystem(myClient, Flag.CRM);
		// 普通客户经理登录
		onceLoginInSystem(httpclient, Flag.CRM,defCrmUsername,defCrmPasswd);
	}

	@DataProvider
	public Iterator<String[]> setCardSegment() {
		try {
			return CvsFileUtil.fromCvsFileToIterator("./csv/card/setCardSegment.csv", 5);
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
