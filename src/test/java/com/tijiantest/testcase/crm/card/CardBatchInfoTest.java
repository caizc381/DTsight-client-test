package com.tijiantest.testcase.crm.card;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.alibaba.fastjson.JSON;
import com.tijiantest.base.HttpResult;
import com.tijiantest.model.card.Card;
import com.tijiantest.model.card.CardDto;
import com.tijiantest.model.hospital.HostpitalMealDto;
import com.tijiantest.model.resource.meal.MealStateEnum;
import com.tijiantest.testcase.crm.CrmMediaBase;
import com.tijiantest.util.DateUtils;
import com.tijiantest.util.db.DBMapper;
import com.tijiantest.util.db.SqlException;

public class CardBatchInfoTest extends CrmMediaBase {

	@Test(description = "获取发卡批次信息", groups = { "qa" })
	public void test_01_cardBatchInfo() throws SqlException, ParseException {
		Card card = defCompanyCard;

		List<NameValuePair> pairs = new ArrayList<>();
		pairs.add(new BasicNameValuePair("batchId", card.getBatchId() + ""));

		HttpResult result = httpclient.get(Card_CardBatchInfo, pairs);
		System.out.println(result.getBody());
		Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);
		String body = result.getBody();
		CardDto cardDto = JSON.parseObject(body, CardDto.class);

		if (checkdb) {
			// 获取卡批次信息
			String sql = "SELECT cb.id, cb.company_id, cb.card_name, cb.capacity, cb.amount, "
					+ "cb.operator_id, cb.create_time, cb.booking_deadline, cb.is_send_bookingmsg, "
					+ "cb.is_send_card_msg, cb.remark, cb.default_meal_id, cb.exam_note,cb.new_company_id,"
					+ "cb.organization_type FROM tb_card_batch cb WHERE id = ?";
			List<Map<String, Object>> list = DBMapper.query(sql, card.getBatchId());
			Assert.assertNotNull(list);
			Assert.assertEquals(cardDto.getCard().getCardName(), list.get(0).get("card_name"));
			Assert.assertEquals(cardDto.getCard().getCapacity(), Long.valueOf(list.get(0).get("capacity").toString()));
			Assert.assertEquals(cardDto.getBookingDeadline().toString(), DateUtils
					.parse("yyyy-MM-dd HH:mm:ss", parse(list.get(0).get("booking_deadline").toString())).toString());
			Assert.assertEquals(cardDto.getIsSendCardMsg()?"1":"0",
					list.get(0).get("is_send_card_msg").toString());
			Assert.assertEquals(cardDto.getNewCompanyId(), list.get(0).get("new_company_id"));
			Assert.assertEquals(cardDto.getOrganizationType(), list.get(0).get("organization_type"));

			// 获取卡信息
			sql = "select tb_card.id, batch_id, card_name, card_num, password, capacity, balance, recoverable_balance, tb_card.type, status, from_hospital, recharge_time, parent_card_id, available_date, expired_date,  create_date, tb_card.account_id from tb_card where batch_id =? limit 1";
			list = DBMapper.query(sql, card.getBatchId());
			Assert.assertNotNull(list);
			/*Assert.assertEquals(cardDto.getCard().getExpiredDate().toString(), DateUtils
					.parse("yyyy-MM-dd HH:mm:ss", parse(list.get(0).get("expired_date").toString())).toString());*/
			Assert.assertEquals(sdf.format(new Date(cardDto.getCard().getExpiredDate().getTime())),
					sdf.format(list.get(0).get("expired_date")));
			
			// 获取卡套餐信息
			sql = "SELECT  card_id as cardId, hospital_id as hospitalId, meal_id as mealId, meal_for_self as isMealForSelf, meal_for_family as isMealForFamily FROM  tb_card_direction_meal_relation WHERE  card_id =?";
			list = DBMapper.query(sql, list.get(0).get("id"));
			Assert.assertNotNull(list);
			Assert.assertEquals(cardDto.getCardMealList().size(), list.size());
			if (!cardDto.getCardMealList().isEmpty()) {
				for (int i = 0; i < cardDto.getCardMealList().size(); i++) {
					HostpitalMealDto mealDto = cardDto.getCardMealList().get(i);
					Assert.assertEquals(mealDto.getIsMealForFamily(),
							Boolean.valueOf(list.get(i).get("isMealForFamily").toString()));
					Assert.assertEquals(mealDto.getIsMealForSelf() ? "1" : "0",
							list.get(i).get("isMealForSelf").toString());
					Assert.assertEquals(mealDto.getMealId(), Integer.valueOf(list.get(i).get("mealId").toString()));

					String mealSql = "SELECT DISTINCT m.id, m.hospital_id, m.name, m.description, m.pinyin, m.discount, m.external_discount, m.gender, m.type, m.disable, m.keyword, m.init_price, m.price, m.tip_text, m.sequence, m.update_time, tb_meal_statistics.hot, tb_meal_statistics.order_count, tb_meal_statistics.click_count, tb_meal_settings.show_meal_price, tb_meal_settings.adjust_price, tb_meal_settings.lock_price FROM tb_meal m LEFT JOIN tb_meal_statistics ON tb_meal_statistics.meal_id = m.id LEFT JOIN tb_meal_settings ON tb_meal_settings.meal_id = m.id LEFT JOIN tb_meal_tag ON  tb_meal_tag.meal_id = m.id WHERE m.id = ? AND m.disable < ?";
					List<Map<String, Object>> mealList = DBMapper.query(mealSql, mealDto.getMealId(),
							MealStateEnum.DISABLE.getCode());
					if (mealList.size()>0) {
						Assert.assertEquals(mealDto.getMealName(), mealList.get(i).get("NAME").toString());
						Assert.assertEquals(mealDto.getGender(), Integer.valueOf(mealList.get(i).get("gender").toString()));
						Assert.assertEquals(mealDto.getMealType(), Integer.valueOf(mealList.get(i).get("type").toString()));
					}
				}
			}
		}
	}

	public String parse(String date) {
		int lastIndex = date.lastIndexOf(".");
		return date.substring(0, lastIndex);
	}
}
