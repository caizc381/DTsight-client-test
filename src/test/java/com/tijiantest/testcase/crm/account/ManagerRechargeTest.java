package com.tijiantest.testcase.crm.account;

/**
 * @author ChenSijia
 */
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpStatus;
import org.apache.http.ParseException;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.tijiantest.base.HttpResult;
import com.tijiantest.testcase.crm.CrmBase;
import com.tijiantest.util.CvsFileUtil;
import com.tijiantest.util.db.DBMapper;
import com.tijiantest.util.db.SqlException;

public class ManagerRechargeTest extends CrmBase {
	@Test(description = "客户经理充值", dataProvider = "manag_Recharge_success", groups = { "qa", "online" })
	public void test_01_manag_Recharge_success(String... args) throws ParseException, IOException, SqlException {
		// get input & output from casefile
		int managerId = Integer.parseInt(args[1]);
		int money = Integer.parseInt(args[2]);
		String remark = args[3];
		int balance = 0;
		// database
		if (checkdb) {
			String sqlStr = "select a.id,a.name,c.balance from tb_account a LEFT JOIN tb_card c  on  a.id=c.account_id where a.id=?";
			List<Map<String, Object>> list = DBMapper.query(sqlStr, managerId);
			Assert.assertEquals(managerId, list.get(0).get("id"));
			balance = Integer.parseInt(list.get(0).get("balance").toString());
		}
		// params
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("managerId", managerId);
		params.put("remark", remark);
		params.put("money", money);
		// post
		HttpResult response = httpclient.post(Manag_Recharge, params, "");

		// Assert
		Assert.assertEquals(response.getCode(), HttpStatus.SC_OK,"接口返回.."+response.getBody());
		Assert.assertTrue(response.getBody().equals("{}")||response.getBody().equals(""));
		// database
		if (checkdb) {
			String sqlStr = "select a.id,a.name,c.balance from tb_account a LEFT JOIN tb_card c  on  a.id=c.account_id where a.id=?";
			List<Map<String, Object>> list = DBMapper.query(sqlStr, managerId);
			Assert.assertEquals(managerId, list.get(0).get("id"));
			Assert.assertEquals(balance + money, Integer.parseInt(list.get(0).get("balance").toString()));
		}
	}

	@DataProvider
	public Iterator<String[]> manag_Recharge_success() {
		try {
			return CvsFileUtil.fromCvsFileToIterator("./csv/account/manag_Recharge_success.csv", 6);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}
}
