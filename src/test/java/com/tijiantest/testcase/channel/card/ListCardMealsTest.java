package com.tijiantest.testcase.channel.card;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.alibaba.fastjson.JSON;
import com.tijiantest.base.Flag;
import com.tijiantest.base.HttpResult;
import com.tijiantest.base.dbcheck.CardChecker;
import com.tijiantest.base.dbcheck.ResourceChecker;
import com.tijiantest.model.card.CardRecordDto;
import com.tijiantest.model.card.CardRecordQueryDto;
import com.tijiantest.model.card.HospitalMealDto;

public class ListCardMealsTest extends EntityCardBase {

	@Test(groups = { "qa" }, description = "获取卡对应的套餐", dataProvider = "listCardMeals")
	public void test_listCardMeals(Integer... args) {
		System.out.println("------------------------测试获取卡对应的套餐Start-------------------------");
		Integer cardId = args[0];
		if (cardId == null) {
			return;
		}

		Map<String, Object> params = new HashMap<>();
		params.put("cardId", cardId);

		HttpResult result = httpclient.get(Flag.CHANNEL, Card_ChannelListCardMeals, params);
		Assert.assertEquals(result.getCode(), HttpStatus.SC_OK, "错误提示：" + result.getBody());
		List<HospitalMealDto> hosMealDto = JSON.parseArray(result.getBody(), HospitalMealDto.class);

		List<HospitalMealDto> mealDbDto = ResourceChecker.getHospitalMealByCardId(cardId);
		Assert.assertEquals(hosMealDto.size(), mealDbDto.size());
		for (int i = 0; i < hosMealDto.size(); i++) {
			HospitalMealDto mealResp = hosMealDto.get(i);
			HospitalMealDto mealDb = mealDbDto.get(i);
			Assert.assertEquals(mealResp.getMealId(), mealDb.getMealId());
			Assert.assertEquals(mealResp.getHospitalId(), mealDb.getHospitalId());
		}
		System.out.println("------------------------测试获取卡对应的套餐End-------------------------");
	}

	@DataProvider(name = "listCardMeals")
	public Iterator<Integer[]> listCardMeals() {
		List<Integer[]> tmpParam = new ArrayList<Integer[]>();
		// 获取所有实体卡
		CardRecordQueryDto dto = new CardRecordQueryDto();
		Integer fromHospital = defChannelid;
		dto.setFromHospital(fromHospital);
		List<CardRecordDto> recordsDB = CardChecker.getCardRecordsByQuery(dto, true);

		Integer cardIdWithDirctionMeal = null;
		Integer cardIdWithoutDirctionMeal = null;
		for (CardRecordDto record : recordsDB) {
			List<HospitalMealDto> cardMeals = ResourceChecker.getFromCardMealRelation(record.getCard().getId());
			if (cardMeals != null && !cardMeals.isEmpty()) {
				cardIdWithDirctionMeal = record.getCard().getId();
				break;
			}
		}
		for (CardRecordDto record : recordsDB) {
			List<HospitalMealDto> cardMeals = ResourceChecker.getFromCardMealRelation(record.getCard().getId());
			if (cardMeals == null || cardMeals.isEmpty()) {
				cardMeals = ResourceChecker.getFromCardHospitalRelation(record.getCard().getId(), cardMeals);
				if (cardMeals != null && !cardMeals.isEmpty()) {
					cardIdWithoutDirctionMeal = record.getCard().getId();
					break;
				}
			}
		}
		System.out.println("指定套餐的卡：" + cardIdWithDirctionMeal + "\n绑定医院的卡：" + cardIdWithoutDirctionMeal);
		Integer[] idTmp = { cardIdWithDirctionMeal };
		Integer[] idTmp1 = { cardIdWithoutDirctionMeal };
		tmpParam.add(idTmp);
		tmpParam.add(idTmp1);
		return tmpParam.iterator();
	}

}
