package com.tijiantest.testcase.ops.withdraw;

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
import com.tijiantest.model.paylog.PayConsts;
import com.tijiantest.model.paylog.TradeTypeEnum;
import com.tijiantest.model.payment.withdraw.WithdrawAuditStates;
import com.tijiantest.testcase.main.MainBase;
import com.tijiantest.testcase.ops.OpsBase;
import com.tijiantest.util.CvsFileUtil;
import com.tijiantest.util.db.DBMapper;
import com.tijiantest.util.db.SqlException;

public class DoFirstTrialTest extends OpsBase {
	Integer originalBalance;
	String withDrawId;
	int accountId ;
	String amount = null;
	String sql = null;

	@Test(description = "提现 - 初审意见(客服)", groups = { "qa" }, dataProvider = "doFirstTrial")
	public void test_01_doFirstTrial(String... args) throws SqlException {

		List<Map<String, Object>> list = null;
		try{
			// 先main登陆，提现申请
			MyHttpClient mainClient = new MyHttpClient();
			onceLoginInSystem(mainClient,Flag.MAIN,defMainUsername,defMainPasswd);
			amount = args[1];
			List<NameValuePair> params = new ArrayList<>();
			params.add(new BasicNameValuePair("amount", amount));
			HttpResult result = mainClient.post(Flag.MAIN, WithDraw_SaveWithdraw, params);
			Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);
			log.info(result.getBody());
			if (!result.getBody().equals("") ||!result.getBody().equals("{}")) {
				return ;
			}
			accountId = MainBase.defaccountId;
			sql = "select * from tb_withdraw where account_id=? order by id desc limit 1";
			list = DBMapper.query(sql, accountId);
			withDrawId = list.get(0).get("id").toString();
			log.info("提现记录ID  withdrawId:" + withDrawId);
			onceLogOutSystem(mainClient, Flag.MAIN);
		}catch (AssertionError e ){
			//登陆错误，MAIN-APP未启动
			String sql1 = "select * from tb_withdraw where amount > 0 and state = 0 order by id desc limit 1";
			list = DBMapper.query(sql1);
			if(list == null || list.size() == 0){
				log.error("没有可用的提现记录，请从C端申请提现");
				return;
			}
			withDrawId = list.get(0).get("id").toString();
			amount = list.get(0).get("amount").toString();
			accountId = Integer.parseInt(list.get(0).get("account_id").toString());
			log.info("提现记录ID  withdrawId:" + withDrawId + "金额:"+amount+"账户id"+accountId);
		}
		sql = "select * from tb_accounting where account_id=?";
		list = DBMapper.query(sql,accountId);

		originalBalance = Integer.valueOf(list.get(0).get("balance").toString());

		String remark = args[2];
		List<NameValuePair> pairs = new ArrayList<>();
		pairs.add(new BasicNameValuePair("id", withDrawId));
		pairs.add(new BasicNameValuePair("remark", remark));
		pairs.add(new BasicNameValuePair("succ", "false"));
		HttpResult response = httpclient.post(Flag.OPS, Withdrawaudit_DoFirstTrial, pairs);
		Assert.assertEquals(response.getCode(), HttpStatus.SC_OK);
		Assert.assertEquals(response.getBody(), "{}");
		if (checkdb) {
			// tb_withdraw表数据
			sql = "select * from tb_withdraw where id=?";
			list = DBMapper.query(sql, withDrawId);
			Assert.assertEquals(WithdrawAuditStates.FirstTrialReject.getCode(), list.get(0).get("state"));
			Assert.assertEquals(remark, list.get(0).get("remark"));

			// tb_save_withdraw_log表
			sql = "select * from tb_save_withdraw_log where withdraw_id=? and state=? order by withdraw_id desc limit 1 ";
			list = DBMapper.query(sql, withDrawId, WithdrawAuditStates.FirstTrialReject.getCode());
			Assert.assertEquals(remark, list.get(0).get("remark"));
			Assert.assertEquals(WithdrawAuditStates.FirstTrialReject.getCode(), list.get(0).get("state"));

			// tb_paymentrecord表
			sql = "select * from tb_paymentrecord where account_id=? and trade_type= ? order by id desc limit 1 ";
			list = DBMapper.query(sql, accountId, TradeTypeEnum.WithdrawFailed.getCode());
			Assert.assertNotNull(list);
			Assert.assertTrue(list.get(0).get("remark").toString().contains("提现失败退款"));

			// tb_accounting表
			sql = "select * from tb_accounting where account_id=?";
			list = DBMapper.query(sql, accountId);
			Integer balance = originalBalance + (int)(Double.parseDouble(amount)*100);
			Assert.assertEquals(balance, list.get(0).get("balance"));

			// tb_paylog表
			sql = "select * from tb_paylog where trade_body=? and trade_type=? order by id desc limit 1";
			list = DBMapper.query(sql, accountId, PayConsts.TradeTypes.WithdrawFailed);
			Assert.assertEquals(PayConsts.TradeBodyTypes.Balance, list.get(0).get("trade_body_type"));
			Assert.assertEquals((int)(Double.parseDouble(amount)*100)+"", list.get(0).get("amount").toString());
			Assert.assertEquals(balance, Integer.valueOf(list.get(0).get("surplus").toString()));
			Assert.assertEquals(PayConsts.TradeStatus.Successful, list.get(0).get("status"));
		}
	}
	
	@DataProvider(name="doFirstTrial")
	public Iterator<String[]> doFirstTrial() {
		try {
			return CvsFileUtil.fromCvsFileToIterator("./csv/payment/doFirstTrial.csv", 5);
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
