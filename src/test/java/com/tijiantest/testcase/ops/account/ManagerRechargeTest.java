package com.tijiantest.testcase.ops.account;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jayway.jsonpath.JsonPath;
import com.tijiantest.base.*;
import com.tijiantest.model.account.ManagerDto;
import com.tijiantest.model.paylog.PayConsts;
import com.tijiantest.model.paylog.TradeTypeEnum;
import com.tijiantest.model.payment.PaymentTypeEnum;
import com.tijiantest.testcase.ops.OpsBase;
import com.tijiantest.util.CvsFileUtil;
import com.tijiantest.util.db.DBMapper;
import com.tijiantest.util.db.SqlException;
import com.tijiantest.util.pagination.Page;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 给平台客户经理充值
 * 调用查询支付记录服务，并记录accounting数据
 */
public class ManagerRechargeTest extends OpsBase {

	@Test(description = "充值", groups = { "qa" }, dataProvider = "recharge")
	public void test_01_recharge(String... args) throws SqlException {
		// caiwu登录
		MyHttpClient caiwuClient = new MyHttpClient();
		onceLoginInSystem(caiwuClient, Flag.OPS,defcaiwuusername, defcaiwupassword);
		// 先通过搜索获取用户
		String searchWord = args[1];
		String money = args[2] + "00";
		String remark = args[3];
		Page page = new Page();
		page.setPageSize(10);
		page.setCurrentPage(1);
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("searchWord",searchWord);
		jsonObject.put("page",page);
//		List<NameValuePair> pairs = new ArrayList<>();
//		pairs.add(new BasicNameValuePair("searchWord", searchWord));
		HttpResult result = caiwuClient.post(Flag.OPS, Manage_ManagerList, JSON.toJSONString(jsonObject));
		System.out.println(result.getBody());
		List<ManagerDto> managerDtos = JSON.parseArray(JsonPath.read(result.getBody(),"$.records").toString(), ManagerDto.class);
		// 如果没有搜索结果，就不进行下面验证操作了
		if (managerDtos == null || managerDtos.isEmpty()) {
			return;
		}

		// 取第一个用户，进行重置密码
		ManagerDto managerDto = managerDtos.get(0);
		System.out.println("客户经理ID:"+managerDto.getAccount().getId()+"  客户经理："+ managerDto.getAccount().getName());
		List<NameValuePair> params = new ArrayList<>();
		params.add(new BasicNameValuePair("managerId", managerDto.getAccount().getId() + ""));
		params.add(new BasicNameValuePair("money", money));
		params.add(new BasicNameValuePair("remark", remark));

		// 取出原有金额
		String sql = "select * from tb_card where status=1 and account_id=? and parent_card_id is null";
		List<Map<String, Object>> list = DBMapper.query(sql, managerDto.getAccount().getId());
		Integer cardId = Integer.valueOf(list.get(0).get("id").toString());
		Integer originalBalance = Integer.valueOf(list.get(0).get("balance").toString());
		Integer expectBalance = originalBalance + Integer.valueOf(money);

		HttpResult response = caiwuClient.post(Flag.OPS, Manage_ManagerRecharge, params);
		System.out.println(response.getBody());
		Assert.assertEquals(response.getCode(), HttpStatus.SC_OK);
		Assert.assertTrue(response.getBody().equals("{}") || response.getBody().equals(""));

		if (checkdb) {
			// tb_card
			list = DBMapper.query(sql, managerDto.getAccount().getId());
			Assert.assertEquals(expectBalance, Integer.valueOf(list.get(0).get("balance").toString()));

			// tb_paymentrecord
			sql = "select p.id, p.serial_number, p.order_id, p.account_id, p.payment_method_id, p.status, p.amount, p.pay_time, p.trade_type, p.is_primary, p.expense_account, p.trade_no, p.remark from tb_paymentrecord p where p.status = 1 and p.account_id = ? and p.trade_type = ? order by p.pay_time desc ";
			list = DBMapper.query(sql, managerDto.getAccount().getId(), TradeTypeEnum.Recharge.getCode());
			Assert.assertEquals(PaymentTypeEnum.Balance.getCode(),
					Integer.parseInt(list.get(0).get("payment_method_id").toString()));
			Assert.assertEquals("1", list.get(0).get("status").toString());
			Assert.assertEquals(TradeTypeEnum.Recharge.getCode(),
					Integer.parseInt(list.get(0).get("trade_type").toString()));
			Assert.assertEquals(-1 + "", list.get(0).get("expense_account").toString());
			Assert.assertEquals(remark+" 操作人: 财务", list.get(0).get("remark").toString());

			// tb_paylog
			sql = "select * from tb_paylog where trade_body=? and trade_type=? order by id desc ";
			list = DBMapper.query(sql, cardId, PayConsts.TradeTypes.CardRecharge);
			Assert.assertNotNull(list);
			Assert.assertEquals(money, list.get(0).get("amount").toString());
			Assert.assertEquals(expectBalance, Integer.valueOf(list.get(0).get("surplus").toString()));
			Assert.assertEquals(PayConsts.TradeStatus.Successful,
					Integer.parseInt(list.get(0).get("status").toString()));
//			Assert.assertEquals(PayConsts.OperaterTypes.Manage,
//					Integer.parseInt(list.get(0).get("operater_type").toString()));
//			Assert.assertEquals(defcaiwuAccountId, Integer.parseInt(list.get(0).get("operater").toString()));
			Assert.assertEquals(remark+" 操作人: 财务", list.get(0).get("remark").toString());

			// tb_sms_send_record
			//财务退出
			onceLogOutSystem(caiwuClient, Flag.OPS);
		}

	}

	@DataProvider(name = "recharge")
	public Iterator<String[]> recharge() {
		try {
			return CvsFileUtil.fromCvsFileToIterator("./csv/account/manage/managerRecharge.csv", 10);
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
