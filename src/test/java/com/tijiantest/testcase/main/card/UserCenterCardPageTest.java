package com.tijiantest.testcase.main.card;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
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
import com.tijiantest.model.card.Card;
import com.tijiantest.model.card.CardStatusEnum;
import com.tijiantest.model.organization.OrganizationTypeEnum;
import com.tijiantest.testcase.main.MainBase;
import com.tijiantest.util.CvsFileUtil;
import com.tijiantest.util.DateUtils;
import com.tijiantest.util.db.DBMapper;
import com.tijiantest.util.db.SqlException;

public class UserCenterCardPageTest extends MainBase {

	@Test(description = "个人预约 - 体检卡", groups = { "qa" }, dataProvider = "userCenterCardPageTest",enabled = false)
	public void test_01_userCenterCardPageTest(String... args) throws SqlException, ParseException {
		String _siteType = args[2];
		String _site = args[3];
		// 提取站点hospitalId
		List<Map<String, Object>> list = DBMapper.query("select hospital_id from tb_site where url = '" + _site + "'");
		Assert.assertEquals(list.size(), 1);
		int hospitalId = Integer.parseInt(list.get(0).get("hospital_id").toString());

		List<NameValuePair> pairs = new ArrayList<>();

		pairs.add(new BasicNameValuePair("hospitalId", hospitalId + ""));

		pairs.add(new BasicNameValuePair("_siteType", _siteType));
		pairs.add(new BasicNameValuePair("_p", ""));
		pairs.add(new BasicNameValuePair("_site", _site));

		HttpResult result = httpclient.get(Flag.MAIN, Main_UserCenterCardPage, pairs);
		log.info(result.getBody());
		Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);
		String body = result.getBody();
		List<Card> cards = JSON.parseObject(JsonPath.read(body, "$.cards").toString(), new TypeReference<List<Card>>() {
		});
		log.info("接口返回卡数量" + cards.size());
		if (checkdb) {

			String sql = "select tb_card.id, batch_id, card_name, card_num, password, capacity, balance,  recoverable_balance, tb_card.type,  status, from_hospital, recharge_time, parent_card_id, available_date, expired_date, create_date, tb_card.account_id from tb_card where  account_id = ? and status in ("
					+ CardStatusEnum.BALANCE_RECOVERED.getCode() + "," + CardStatusEnum.USABLE.getCode()
					+ ") order by balance desc, expired_date";
			List<Map<String, Object>> allCardList = DBMapper.query(sql, defaccountId);

			List<Map<String, Object>> removeCardList = new ArrayList<>();

			// 如果是隐价卡，过期了/使用过/回收状态，都不显示
			for (int i = 0; i < allCardList.size(); i++) {
				Map<String, Object> map = allCardList.get(i);
				String cardSettingsSql = "select * from tb_card_settings where card_id=?";
				List<Map<String, Object>> cardSettingsList = DBMapper.query(cardSettingsSql, map.get("id").toString());
				if (cardSettingsList.get(0).get("is_show_card_meal_price").toString().equals("1")) {
					Long timeNum = sdf.parse(sdf.format(new Date())).getTime();
					Date expiredDate = DateUtils.parse("yyyy-MM-dd HH:mm:ss", map.get("expired_date").toString());

					if (timeNum > expiredDate.getTime()  //过期时间只是比较日期，不精确到时分秒
							|| (!map.get("balance").toString().equals(map.get("capacity").toString())
									|| map.get("status").toString().equals(CardStatusEnum.BALANCE_RECOVERED))) {
						//allCardList.remove(map);
						System.out.println("去除cardId=" + cardSettingsList.get(0).get("card_id"));
						removeCardList.add(map);
					}
				}				
			}
			
			for (Map<String, Object> m : removeCardList) {
				allCardList.remove(m);
			}			

			int size = 0;
			if (hospitalId != 0) {// 二级站点
				// 判断是否是渠道商二级站点，还是体检中心二级站点
				String hospitalSql = "select * from tb_hospital where id=" + hospitalId;
				log.info("sql....." + hospitalSql);
				List<Map<String, Object>> hospitalList = DBMapper.query(hospitalSql);
				int organizationType = Integer.parseInt(hospitalList.get(0).get("organization_type").toString());
				if (!hospitalList.isEmpty() && organizationType == OrganizationTypeEnum.HOSPITAL.getCode()) {
					// 如果是体检中心
					// 过滤相同的id
					if (allCardList.size() == 0)
						return;
					log.info("...allcards.." + allCardList.size());
					for (int i = 0; i < allCardList.size(); i++) {
						Map<String, Object> map = allCardList.get(i);
						int cardId = Integer.parseInt(map.get("id").toString());
						// step1:首先查找官方套餐
						List<Map<String, Object>> list1 = DBMapper
								.query("select hospital_id from tb_card_hospital_relation where card_id= " + cardId
										+ " and hospital_id = " + hospitalId);
						if (list1.size() > 0) {
							int hospital_list1 = Integer.parseInt(list1.get(0).get("hospital_id").toString());
							// log.info("医院xxxxxxid..."+hospitalId +"当前id"+hospital_list1);
							if (hospital_list1 == hospitalId)
								size++; // 如果是官方套餐，数量+1
							else {// step2:查找是单位套餐
								List<Map<String, Object>> list2 = DBMapper
										.query("select hospital_id from tb_card_direction_meal_relation where card_id="
												+ cardId + " and hospital_id = " + hospitalId);
								if (list2.size() == 0)
									continue;
								int hospital_list2 = Integer.parseInt(list2.get(0).get("hospital_id").toString());
								// log.info("医院单位id..."+hospitalId +"当前id"+hospital_list2);
								if (hospital_list2 == hospitalId)
									size++;
							}
						} else {// step3://只查找单位套餐
							List<Map<String, Object>> list2 = DBMapper
									.query("select hospital_id from tb_card_direction_meal_relation where card_id="
											+ cardId + " and hospital_id = " + hospitalId);
							if (list2.size() == 0)
								continue;
							int hospital_list2 = Integer.parseInt(list2.get(0).get("hospital_id").toString());
							// log.info("医院id..."+hospitalId +"当前id"+hospital_list2);
							if (hospital_list2 == hospitalId)
								size++;
						}
					}
					log.info("size...." + size);
					Assert.assertEquals(cards.size(), size);
				} else if (!hospitalList.isEmpty() && organizationType == OrganizationTypeEnum.CHANNEL.getCode()) {
					System.out.println("------------cards-------------");
					for (Card card : cards) {
						System.out.println(card.getId());
					}

					System.out.println("------------allCardList------------------");
					for (Map<String, Object> map : allCardList) {
						System.out.println(map.get("id"));
					}

					Assert.assertEquals(cards.size(), allCardList.size());
				}
			}
		}
	}	

	@DataProvider(name = "userCenterCardPageTest")
	public Iterator<String[]> userCenterCardPageTest() {
		try {
			return CvsFileUtil.fromCvsFileToIterator("./csv/card/main/userCenterCardPageTest.csv", 10);
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
