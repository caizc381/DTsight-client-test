package com.tijiantest.testcase.main.card;

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

import com.alibaba.fastjson.JSON;
import com.tijiantest.base.Flag;
import com.tijiantest.base.HttpResult;
import com.tijiantest.base.dbcheck.HospitalChecker;
import com.tijiantest.base.dbcheck.OrderChecker;
import com.tijiantest.model.card.Card;
import com.tijiantest.model.order.Order;
import com.tijiantest.testcase.main.MainBase;
import com.tijiantest.testcase.main.order.BookTest;
import com.tijiantest.util.CvsFileUtil;
import com.tijiantest.util.db.DBMapper;
import com.tijiantest.util.db.SqlException;

public class GetCardTest extends MainBase {

	@Test(description = "入口卡 - 根据订单,获取卡信息", dependsOnGroups = "main_bookWithEntryCard", groups = {
			"qa" }, dataProvider = "getCard")
	public void test_01_getCard(String... args) throws SqlException {
		int orderId = BookTest.comm_entryCard_OrderId;
		Order order = OrderChecker.getOrderInfo(orderId);
		int cardId = BookTest.entryCardId;
		String _site = HospitalChecker.getSiteByOrganizationId(order.getHospital().getId());
		String _siteType = args[2];

		List<NameValuePair> pairs = new ArrayList<>();
		pairs.add(new BasicNameValuePair("cardId", cardId + ""));
		pairs.add(new BasicNameValuePair("orderId", orderId + ""));
		pairs.add(new BasicNameValuePair("_site", _site));
		pairs.add(new BasicNameValuePair("_siteType", _siteType));

		HttpResult result = httpclient.post(Flag.MAIN, Card_GetCard, pairs);
		Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);
		String body = result.getBody();
		System.out.println(body);

		Card card = JSON.parseObject(body, Card.class);

		if (checkdb) {
			// tb_card
			String sql = "select tb_card.id, batch_id, card_name, card_num, password, capacity, balance, recoverable_balance, tb_card.type, status, from_hospital, recharge_time, parent_card_id, available_date, expired_date, create_date, tb_card.account_id,tb_manager_card_relation.manager_id, tb_manager_card_relation.new_company_id from tb_card LEFT JOIN tb_manager_card_relation ON tb_manager_card_relation.card_id = tb_card.id WHERE tb_card.id = ?";
			List<Map<String, Object>> list = DBMapper.query(sql, cardId);
			Map<String, Object> map = list.get(0);
			Assert.assertEquals(card.getId(), map.get("id"));
			Assert.assertEquals(card.getBatchId(), map.get("batch_id"));
			Assert.assertEquals(card.getCardName(), map.get("card_name"));
			Assert.assertEquals(card.getCardNum(), map.get("card_num"));
			Assert.assertEquals(card.getCapacity(), map.get("capacity"));
			Assert.assertEquals(card.getBalance(), map.get("balance"));
			Assert.assertEquals(card.getType(), map.get("type"));
			Assert.assertEquals(card.getStatus(), map.get("status"));
			Assert.assertEquals(card.getParentCardId(), map.get("parent_card_id"));
			Assert.assertEquals(card.getAccountId(), map.get("account_id"));
			Assert.assertEquals(card.getManagerId(), map.get("manager_id"));
			Assert.assertEquals(card.getNewCompanyId(), map.get("new_company_id"));

			// tb_card_settings
			sql = "select * from tb_card_settings where card_id=?";
			list = DBMapper.query(sql, cardId);
			map = list.get(0);
			Assert.assertEquals(card.getCardSetting().isShowCardMealPrice() ? 1 : 0,
					map.get("is_show_card_meal_price"));
			Assert.assertEquals(card.getCardSetting().isPayFreeze() ? 1 : 0, map.get("is_pay_freeze"));
			Assert.assertEquals(card.getCardSetting().isPayMealCost() ? 1 : 0, map.get("is_pay_meal_cost"));
			Assert.assertEquals(card.getCardSetting().isPrivate() ? 1 : 0, map.get("isprivate"));
		}
	}

	@DataProvider(name = "getCard")
	public Iterator<String[]> getCard() {
		try {
			return CvsFileUtil.fromCvsFileToIterator("./csv/card/main/getCard.csv", 1);
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
