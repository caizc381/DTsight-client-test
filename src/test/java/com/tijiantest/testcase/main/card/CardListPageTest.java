package com.tijiantest.testcase.main.card;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.*;

import com.alibaba.fastjson.JSONArray;
import com.tijiantest.util.ListUtil;
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
import com.tijiantest.base.dbcheck.CardChecker;
import com.tijiantest.model.card.Card;
import com.tijiantest.model.card.CardStatusEnum;
import com.tijiantest.testcase.main.MainBase;
import com.tijiantest.util.CvsFileUtil;
import com.tijiantest.util.db.DBMapper;
import com.tijiantest.util.db.SqlException;

public class CardListPageTest extends MainBase {

	@Test(description = "体检预约", groups = { "qa" }, dataProvider = "cardListPage")
	public void test_01_cardListPage(String... args) throws SqlException, ParseException {
		String hospitalId = args[1];
		String site = args[2];

		List<NameValuePair> pairs = new ArrayList<>();
		pairs.add(new BasicNameValuePair("_p", ""));
		pairs.add(new BasicNameValuePair("_site", site));
		pairs.add(new BasicNameValuePair("_siteType", "mobile"));
		HttpResult result=null;
		String node;
		if(hospitalId.equals("undefined")){
			result = httpclient.get(Flag.MAIN, Mobile_LoadUserCardPage, pairs);
			node = "userCardList";
		}
		else{
			result = httpclient.get(Flag.MAIN, Card_LoadHospitalCardPage, pairs,hospitalId);
			node = "hospitalCardList";
		}
		Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);
		String body = result.getBody();
		log.info("body..."+body);
		List<Card> cards = JSON.parseObject(JsonPath.read(body, "$."+node+"").toString(), new TypeReference<List<Card>>() {
		});

		Collections.sort(cards, new Comparator<Card>() {
			@Override
			public int compare(Card o1, Card o2) {
				return o1.getId() - o2.getId();
			}
		});
		if (checkdb) {
			String status = CardStatusEnum.BALANCE_RECOVERED.getCode() + "," + CardStatusEnum.USABLE.getCode();
			if (hospitalId.equals("undefined")) {
			    List<Integer> dbList = CardChecker.getUserCanUseCardList(site,defaccountId,null) ;
				Assert.assertEquals(cards.size(), dbList.size());
				Collections.sort(dbList);
				if (!cards.isEmpty()) {
					for (int i = 0; i < cards.size(); i++) {
						Card card = cards.get(i);
						Card dbCard = CardChecker.getCardInfo(dbList.get(i));
						Assert.assertEquals(card.getCardName(), dbCard.getCardName());
						int isShowCardMealPrice = card.getCardSetting().isShowCardMealPrice()== true?1:0;
						if (isShowCardMealPrice == 1) {
							Assert.assertEquals(card.getBalance(), Long.valueOf(0));
						} else {
							Assert.assertEquals(card.getBalance(), dbCard.getBalance());
						}
						Assert.assertEquals(card.getCardNum(), dbCard.getCardNum());
					}
				}
			} else {
				// 对应体检中心的体检卡				
				List<Card> directionCardLists=CardChecker.getCardByHospital(hospitalId, defaccountId, status, cards);
				Assert.assertEquals(cards.size(), directionCardLists.size());
				if (!cards.isEmpty()) {
					for (int i = 0; i < cards.size(); i++) {
						Card card = cards.get(i);
						Assert.assertEquals(card.getCardName(), directionCardLists.get(i).getCardName());
						int isShowCardMealPrice = card.getCardSetting().isShowCardMealPrice()== true?1:0;
						if (isShowCardMealPrice == 1) {
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

	@DataProvider(name = "cardListPage")
	public Iterator<String[]> cardListPage() {
		try {
			return CvsFileUtil.fromCvsFileToIterator("./csv/card/main/cardListPage.csv", 5);
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
