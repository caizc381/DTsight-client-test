package com.tijiantest.testcase.ops.withdraw;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.jayway.jsonpath.JsonPath;
import com.tijiantest.base.Flag;
import com.tijiantest.base.HttpResult;
import com.tijiantest.model.payment.PaymentRDto;
import com.tijiantest.model.payment.withdraw.SaveWithdraw;
import com.tijiantest.testcase.ops.OpsBase;
import com.tijiantest.util.CvsFileUtil;
import com.tijiantest.util.db.DBMapper;
import com.tijiantest.util.db.SqlException;

public class UserInfoTest extends OpsBase {

	@Test(description = "", groups = { "qa" }, dataProvider = "userInfo")
	public void test_01_userInfo(String... args) throws SqlException, ParseException {
		String id = args[1];

		List<NameValuePair> params = new ArrayList<>();
		params.add(new BasicNameValuePair("id", id));
		HttpResult result = httpclient.post(Flag.OPS, Withdrawaudit_UserInfo, params);
		Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);
		String body = result.getBody();
		if (body.contains("\"account\":null")) {
			return;
		}
		Map<String, Object> account = JsonPath.read(body, "$.account.[*]");
		Map<String, Object> accounting = JsonPath.read(body, "$.accounting.[*]");
		List<PaymentRDto> payList = JSON.parseObject(JsonPath.read(body, "$.list").toString(),
				new TypeReference<List<PaymentRDto>>() {
				});
		List<SaveWithdraw> withdrawList = JSON.parseObject(JsonPath.read(body, "$.withdrawList").toString(),
				new TypeReference<List<SaveWithdraw>>() {
				});

		if (checkdb) {
			// 验证Account
			String sql = "select id, name, mobile, idcard, status, type, employee_id, create_time, update_time, id_type, system from tb_account where id = ?";
			List<Map<String, Object>> list = DBMapper.query(sql, id);
			Assert.assertEquals(account.get("idCard"), list.get(0).get("idcard"));
			Assert.assertEquals(account.get("idType"), list.get(0).get("id_type"));
			Assert.assertEquals(account.get("mobile"), list.get(0).get("mobile"));
			Assert.assertEquals(account.get("name"), list.get(0).get("name"));
			Assert.assertEquals(account.get("status"), list.get(0).get("status"));
			Assert.assertEquals(account.get("system"), list.get(0).get("system"));
			Assert.assertEquals(account.get("type"), list.get(0).get("type"));

			// 验证Accounting
			sql = "SELECT id, account_id, balance FROM tb_accounting WHERE account_id = ?";
			list = DBMapper.query(sql, id);
			Assert.assertEquals(accounting.get("balance"), list.get(0).get("balance"));
			Assert.assertEquals(accounting.get("id"), list.get(0).get("id"));

			// 近期现金支付信息
			sql = "select o.order_num, p.pay_time, pm.`name`, p.amount, p.status, p.trade_no, p.trade_type from tb_paymentrecord p left join tb_payment_method pm on pm.id = p.payment_method_id LEFT JOIN tb_order o ON p.order_id=o.id where p.account_id = ? and pm.id !=1 and p.status=1 and p.trade_type in (1,2,3) order by p.pay_time desc limit 30";
			list = DBMapper.query(sql, id);
			Assert.assertEquals(payList.size(), list.size());
			if (payList != null && !payList.isEmpty()) {
				for (int i = 0; i < payList.size(); i++) {
					PaymentRDto paymentRDto = payList.get(i);
					
					Assert.assertEquals(paymentRDto.getOrderId(), list.get(i).get("order_num"));
					Assert.assertEquals(paymentRDto.getPayType(), list.get(i).get("name"));
					Assert.assertEquals(paymentRDto.getStatus(), list.get(i).get("STATUS"));
					Assert.assertEquals(paymentRDto.getTradeType(), list.get(i).get("trade_type"));
					Assert.assertEquals(paymentRDto.getUseBlance(), list.get(i).get("amount"));
				}
			}

			// 提现记录
			sql = "SELECT sw.id, sw.receivable, sw.channel_name, sw.real_name, sw.amount, sw.state, sw.account_id, sw.serial_number, sw.withdraw_type, sw.create_date, sw.update_date, sw.operator, sw.remark, ai.balance AS balance, ac. NAME AS 'account_name', ac.mobile AS 'account_mobile' FROM tb_withdraw sw LEFT JOIN tb_account ac ON sw.account_id = ac.id LEFT JOIN tb_accounting ai ON sw.account_id = ai.account_id WHERE sw.account_id = ? ORDER BY sw.id DESC";
			list = DBMapper.query(sql, id);
			Assert.assertEquals(withdrawList.size(), list.size());
			if (withdrawList != null && !withdrawList.isEmpty()) {
				for (int i = 0; i < withdrawList.size(); i++) {
					SaveWithdraw saveWithdraw = withdrawList.get(i);
					Assert.assertEquals(saveWithdraw.getAmount(), list.get(i).get("amount"));
					Assert.assertEquals(saveWithdraw.getState(), list.get(i).get("state"));
				}
			}
		}
	}

	@DataProvider(name = "userInfo")
	public Iterator<String[]> userInfo() {
		try {
			return CvsFileUtil.fromCvsFileToIterator("./csv/payment/userInfo.csv", 1);
		} catch (FileNotFoundException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		return null;
	}

	public String parse(String date) {
		int lastIndex = date.lastIndexOf(".");
		return date.substring(0, lastIndex);
	}
}
