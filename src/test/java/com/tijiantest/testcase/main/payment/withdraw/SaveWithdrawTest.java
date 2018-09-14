package com.tijiantest.testcase.main.payment.withdraw;

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
import org.testng.annotations.AfterTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.tijiantest.base.Flag;
import com.tijiantest.base.HttpResult;
import com.tijiantest.base.MyHttpClient;
import com.tijiantest.model.paylog.PayConsts;
import com.tijiantest.model.paylog.TradeTypeEnum;
import com.tijiantest.model.payment.withdraw.WithdrawAuditStates;
import com.tijiantest.testcase.main.MainBase;
import com.tijiantest.util.CvsFileUtil;
import com.tijiantest.util.db.DBMapper;
import com.tijiantest.util.db.SqlException;

public class SaveWithdrawTest extends MainBase {

	Integer originalBalance;
	String withDrawId;

	@Test(description = "提现", groups = { "qa" }, dataProvider = "saveWithdraw")
	public void test_01_saveWithdraw(String... args) throws SqlException {
		String amount = args[1];
		String channelName = args[2];

		String sql = "select * from tb_accounting where account_id=?";
		List<Map<String, Object>> list = DBMapper.query(sql, defaccountId);
		originalBalance = Integer.valueOf(list.get(0).get("balance").toString());

		List<NameValuePair> params = new ArrayList<>();
		params.add(new BasicNameValuePair("amount", amount));
		params.add(new BasicNameValuePair("_p", ""));
		params.add(new BasicNameValuePair("_site", ""));
		HttpResult result = httpclient.post(Flag.MAIN, WithDraw_SaveWithdraw, params);
		Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);
		if (result.getBody().equals("") || result.getBody().equals("{}")) {
			if (checkdb) {
				// tb_paymentrecord
				sql = "select * from tb_paymentrecord where account_id=? and trade_type=? order by id desc limit 1";
				list = DBMapper.query(sql, defaccountId, TradeTypeEnum.Withdraw.getCode());
				Assert.assertNotNull(list);
				Assert.assertEquals(Integer.valueOf(amount) * 100, list.get(0).get("amount"));

				// tb_withdraw
				sql = "select * from tb_withdraw where account_id=? order by id desc limit 1";
				list = DBMapper.query(sql, defaccountId);
				Assert.assertNotNull(list);
				Assert.assertEquals(amount, list.get(0).get("amount"));
				Assert.assertEquals(channelName, list.get(0).get("channel_name"));
				Assert.assertEquals(0, list.get(0).get("state"));
				Assert.assertEquals(3, list.get(0).get("withdraw_type"));
				withDrawId = list.get(0).get("id").toString();

				// tb_save_withdraw_log
				sql = "select * from tb_save_withdraw_log where withdraw_id=?";
				list = DBMapper.query(sql, list.get(0).get("id"));
				Assert.assertNotNull(list);
				Assert.assertEquals(0, list.get(0).get("state"));

				// tb_accounting表
				sql = "select * from tb_accounting where account_id=?";
				list = DBMapper.query(sql, defaccountId);
				Integer balance = originalBalance - Integer.valueOf(amount) * 100;
				Assert.assertEquals(balance, Integer.valueOf(list.get(0).get("balance").toString()));

				log.info("提现记录ID withdrawId：" + withDrawId);

				// tb_paylog表
				sql = "select * from tb_paylog where trade_body=? and trade_type=? order by id desc limit 1";
				list = DBMapper.query(sql, defaccountId, PayConsts.TradeTypes.Withdraw);
				Assert.assertNotNull(list);
				Assert.assertEquals(Integer.valueOf(PayConsts.TradeBodyTypes.Balance),
						Integer.valueOf(list.get(0).get("trade_body_type").toString()));
				Assert.assertEquals("-" + Integer.valueOf(amount) * 100, list.get(0).get("amount").toString());
				Assert.assertEquals(balance, Integer.valueOf(list.get(0).get("surplus").toString()));
				Assert.assertEquals(PayConsts.TradeStatus.Successful, list.get(0).get("status").hashCode());
				Assert.assertEquals(PayConsts.OperaterTypes.User, list.get(0).get("operater_type").hashCode());
			}
		} else if (result.getBody().contains("有正在处理中的提现申请，请等到当前申请处理完成")) {
			if (checkdb) {
				String state = WithdrawAuditStates.Wait.getCode() + "," + WithdrawAuditStates.FirstTrialAdopt.getCode();
				sql = "select * from tb_withdraw where account_id=? and state in ( " + state
						+ ")  order by id desc limit 1";
				list = DBMapper.query(sql, defaccountId);
				Assert.assertTrue(list.size() >= 1);
				withDrawId = list.get(0).get("id").toString();
				log.info("提现记录ID withdrawId：" + withDrawId);
			}
		} else if (result.getBody().contains("可提现余额不足")) {
			if (checkdb) {
				sql = "select * from tb_accounting where account_id=?";
				list = DBMapper.query(sql, defaccountId);
				Assert.assertTrue(Integer.parseInt(list.get(0).get("balance").toString())/100 < Integer.parseInt(amount));
				withDrawId = "";
			}
		}
	}

	@AfterTest(alwaysRun = true)
	public void doAfter() {
		if (withDrawId!="") {
			// 在manage中把提现申请拒绝掉
			MyHttpClient manageClient = new MyHttpClient();
			try{
			onceLoginInSystem(manageClient, Flag.OPS,defaultManagerUsername,defaultManagerPasswd);
			List<NameValuePair> pairs = new ArrayList<>();
			pairs.add(new BasicNameValuePair("id", withDrawId));
			pairs.add(new BasicNameValuePair("remark", "拒绝"));
			pairs.add(new BasicNameValuePair("succ", "false"));
			HttpResult result = manageClient.post(Flag.OPS, Withdrawaudit_DoFirstTrial, pairs);
			Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);
			onceLogOutSystem(manageClient, Flag.OPS);
			}catch (AssertionError e){
				log.error("OPS-ADMIN未开启");
			}catch (NoClassDefFoundError e){
				log.error("OPS-ADMIN未开启");
			}
		}
	}

	@DataProvider
	public Iterator<String[]> saveWithdraw() {
		try {
			return CvsFileUtil.fromCvsFileToIterator("./csv/payment/saveWithdraw.csv", 5);
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
