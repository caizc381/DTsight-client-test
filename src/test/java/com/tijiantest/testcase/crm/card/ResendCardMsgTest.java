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

import com.tijiantest.base.HttpResult;
import com.tijiantest.model.card.Card;
import com.tijiantest.model.card.CardRecordDto;
import com.tijiantest.testcase.crm.CrmMediaBase;
import com.tijiantest.util.CvsFileUtil;
import com.tijiantest.util.db.DBMapper;
import com.tijiantest.util.db.SqlException;

public class ResendCardMsgTest extends CrmMediaBase {
	public static String batchId = "";
	@Test(description = "发卡批次 - 发卡信息-重新发送发卡短信", groups = { "qa","crm_resendCardMsg" })
	public void test_01_resendCardMsg() throws SqlException {
		Card card = defCompanyCard;

		List<NameValuePair> pairs = new ArrayList<>();
		batchId = card.getBatchId()+"";
		pairs.add(new BasicNameValuePair("batchId", batchId));

		HttpResult result = httpclient.post(Card_ResendCardMsg, pairs);
		Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);
		Assert.assertEquals(result.getBody(), "{}");

		if (checkdb) {

			checkDB(card);
		}
	}

	@Test(description = "所有发卡记录-勾选一张卡-发送短信", groups = { "qa" }, dataProvider = "resendCardMsg")
	public void test_02_resendCardMsg(String... args) throws SqlException {
		String template = args[1];
		String mobiles = args[2];
		Card card = defCompanyCard;

		List<NameValuePair> pairs = new ArrayList<>();
		pairs.add(new BasicNameValuePair("batchId", card.getBatchId() + ""));
		pairs.add(new BasicNameValuePair("template", template));
		pairs.add(new BasicNameValuePair("cards[]", card.getId() + ""));
		pairs.add(new BasicNameValuePair("mobiles[]", mobiles));

		HttpResult result = httpclient.post(Card_ResendCardMsg, pairs);
		Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);
		Assert.assertEquals(result.getBody(), "{}");

		if (checkdb) {
			checkDB(card);
		}
	}
	
	@Test(description="所有发卡记录 - 选择两张卡-发短信",groups={"qa"},dependsOnGroups="crm_allRecords",dataProvider="resendCardMsg",enabled=false)
	public void test_03_resendCardMsg(String... args) throws SqlException{
		String template = args[1];
		List<CardRecordDto> dto = CardRecordsTest.dto;
		List<NameValuePair> pairs = new ArrayList<>();
		if (dto.size()>=2) {
			for (int i = 0; i < 2; i++) {
				CardRecordDto d = dto.get(i);
				pairs.add(new BasicNameValuePair("cards[]", d.getCard().getId()+""));
				if (d.getAccount().getMobile()!=null) {
					pairs.add(new BasicNameValuePair("mobiles[]", d.getAccount().getMobile()));
				}
			}
		}
		pairs.add(new BasicNameValuePair("template", template));
		HttpResult result = httpclient.post(Card_ResendCardMsg, pairs);
		Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);
		Assert.assertEquals(result.getBody(), "{}");
		
		if (checkdb) {
			checkDB(dto.get(0).getCard());
			checkDB(dto.get(1).getCard());
		}
	}

	public void checkDB(Card card) throws SqlException {
		// 获取卡信息
		String sql = "select tb_card.id, batch_id, card_name, card_num, password, capacity, balance, recoverable_balance, tb_card.type, status, from_hospital, recharge_time, parent_card_id, available_date, expired_date, create_date, tb_card.account_id from tb_card where batch_id =? ";
		List<Map<String, Object>> cardlist = DBMapper.query(sql, card.getBatchId());

		// 获取母卡信息,获取客户经理信息
		sql = "SELECT tb_card.id, batch_id, card_name, card_num, PASSWORD, capacity, balance, recoverable_balance, tb_card.type, STATUS, from_hospital, recharge_time, parent_card_id, available_date, expired_date, create_date, tb_card.account_id, "
				+ "tb_manager_card_relation.manager_id, tb_manager_card_relation.new_company_id "
				+ "FROM tb_card LEFT JOIN tb_manager_card_relation ON tb_manager_card_relation.card_id = tb_card.id WHERE tb_card.id = ?";
		List<Map<String, Object>> pCardlist = DBMapper.query(sql, cardlist.get(0).get("parent_card_id"));

		// 根据卡的信息获取到手机号
		sql = "select * from tb_examiner where manager_id=? and new_company_id=?";
		List<Map<String, Object>> accountList = DBMapper.query(sql, pCardlist.get(0).get("manager_id"),
				pCardlist.get(0).get("new_company_id"));
		Assert.assertNotNull(accountList);

		for (int i = 0; i < accountList.size(); i++) {
			Map<String, Object> map = accountList.get(i);
			if (map.get("mobile").toString().equals("") || map.get("mobile") == null) {
				accountList.remove(i);
			}
		}
		sql = "select * from tb_sms_send_record order by id desc limit ?";
		List<Map<String, Object>> smsList = DBMapper.query(sql, accountList.size());
		Assert.assertNotNull(smsList);

		// is_send_card_msg
		sql = "select * from tb_card_batch where id=?";
		List<Map<String, Object>> batchList = DBMapper.query(sql, card.getBatchId());
		Assert.assertEquals("1", batchList.get(0).get("is_send_card_msg").toString());
	}

	@DataProvider(name="resendCardMsg")
	public Iterator<String[]> resendCardMsg() {
		try {
			return CvsFileUtil.fromCvsFileToIterator("./csv/card/resendCardMsg.csv", 5);
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
