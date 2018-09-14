package com.tijiantest.testcase.main.card;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.*;

import com.tijiantest.base.dbcheck.HospitalChecker;
import com.tijiantest.model.site.Site;
import com.tijiantest.util.DateUtils;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.alibaba.fastjson.JSON;
import com.tijiantest.base.Flag;
import com.tijiantest.base.HttpResult;
import com.tijiantest.base.dbcheck.CardChecker;
import com.tijiantest.model.card.Card;
import com.tijiantest.model.card.CardStatusEnum;
import com.tijiantest.testcase.main.MainBase;
import com.tijiantest.util.CvsFileUtil;
import com.tijiantest.util.db.DBMapper;
import com.tijiantest.util.db.SqlException;

/**
 * 位置：C端体检预约（使用体检卡页面）
 */
public class ValidCardsForAccountTest extends MainBase {

	@Test(description = "获取登录人可用的卡", groups = { "qa" }, dataProvider = "validCardsForAccount")
	public void test_01_validCardsForAccountTest(String... args) throws SqlException, ParseException {
		String hospitalId = args[1];
		String _siteType = args[2];
		List<NameValuePair> pairs = new ArrayList<>();
		if (hospitalId != null && !hospitalId.equals("")) {
			pairs.add(new BasicNameValuePair("hospitalId", hospitalId));
		}
		Site site = HospitalChecker.getSiteByHospitalId(Integer.parseInt(hospitalId));
		String siteUrl = site.getUrl();
		pairs.add(new BasicNameValuePair("_siteType", _siteType));
		pairs.add(new BasicNameValuePair("_p", ""));
		pairs.add(new BasicNameValuePair("_site", siteUrl));

		HttpResult result = httpclient.get(Flag.MAIN, Card_ValidCardsForAccount, pairs);
		Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);
		String body = result.getBody();
		log.info("ssssssssss"+body);
		List<Card> cards = JSON.parseArray(body, Card.class);

		if (checkdb) {
			String sql = "";
			if (Integer.parseInt(hospitalId)  == 196) {
				//非隐价卡
				sql = "select tb_card.id, batch_id, card_name, card_num, password, capacity, balance, recoverable_balance, tb_card.type, status, from_hospital, recharge_time, parent_card_id, available_date, expired_date, create_date, tb_card.account_id, tb_card_exam_note.note as note from tb_card left join tb_manager_card_relation on tb_card.id = tb_manager_card_relation.card_id left join tb_card_exam_note on tb_card.exam_note_id = tb_card_exam_note.id where status=1 and tb_card.available_date <= NOW() and tb_card.expired_date  >= '"+simplehms.format(DateUtils.offDate(-1))+"' and tb_card.account_id = ? and (tb_card.parent_card_id is not null or tb_card.type=2) and tb_card.id in (select card_id from tb_card_settings s where s.is_show_card_meal_price = 0)  order by balance desc, expired_date asc";
				List<Map<String, Object>> list = DBMapper.query(sql, defaccountId);
				//隐价卡
				sql = "select tb_card.id, batch_id, card_name, card_num, password, capacity, balance, recoverable_balance, tb_card.type, status, from_hospital, recharge_time, parent_card_id, available_date, expired_date, create_date, tb_card.account_id, tb_card_exam_note.note as note from tb_card left join tb_manager_card_relation on tb_card.id = tb_manager_card_relation.card_id left join tb_card_exam_note on tb_card.exam_note_id = tb_card_exam_note.id where status=1 and tb_card.available_date <= NOW() and tb_card.expired_date  >=  '"+simplehms.format(DateUtils.offDate(-1))+"' and tb_card.account_id = ? and (tb_card.parent_card_id is not null or tb_card.type=2) and tb_card.id in (select card_id from tb_card_settings s where s.is_show_card_meal_price = 1 ) and  capacity = balance  order by balance desc, expired_date asc";
				List<Map<String, Object>> list1 = DBMapper.query(sql, defaccountId);
				list.addAll(list1);

				if (cards.isEmpty()) {
					Assert.assertEquals(list.size(), 0);
				} else {
					Assert.assertEquals(list.size(), cards.size());
				}
//				String status = CardStatusEnum.BALANCE_RECOVERED.getCode() + "," + CardStatusEnum.USABLE.getCode();
//				List<Card> directionCardLists=CardChecker.getCardByHospital(hospitalId+"", defaccountId, status, cards);
//				Assert.assertEquals(cards.size(), directionCardLists.size());
			} else {
				Collections.sort(cards, new Comparator<Card>() {
					@Override
					public int compare(Card o1, Card o2) {
						return  - o1.getId() + o2.getId();
					}
				});
				String status = CardStatusEnum.BALANCE_RECOVERED.getCode() + "," + CardStatusEnum.USABLE.getCode();
				List<Card> directionCardLists = CardChecker.getCardByHospitalCanUse(hospitalId, defaccountId, status);
				Assert.assertEquals(cards.size(), directionCardLists.size());
				if (!cards.isEmpty()) {
					for (int i = 0; i < cards.size(); i++) {
						Card card = cards.get(i);
						Assert.assertEquals(card.getCardName(), directionCardLists.get(i).getCardName());
						if (card.getCardSetting().isShowCardMealPrice().booleanValue()) {
							Assert.assertEquals(card.getBalance(), Long.valueOf(0));
						} else {
							Assert.assertEquals(card.getBalance(), directionCardLists.get(i).getBalance());
						}
						Assert.assertEquals(card.getCardNum(), directionCardLists.get(i).getCardNum());
					}
				}
			}
		}
	}

	@DataProvider(name = "validCardsForAccount")
	public Iterator<String[]> validCardsForAccount() {
		try {
			return CvsFileUtil.fromCvsFileToIterator("./csv/card/validCardsForAccount.csv", 10);
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
