package com.tijiantest.testcase.crm.card;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.tijiantest.base.HttpResult;
import com.tijiantest.model.resource.meal.Meal;
import com.tijiantest.model.resource.meal.MealTypeEnum;
import com.tijiantest.testcase.crm.CrmBase;
import com.tijiantest.util.AssertUtil;
import com.tijiantest.util.CvsFileUtil;
import com.tijiantest.util.db.DBMapper;
import com.tijiantest.util.db.SqlException;

public class OfficialmealsTest extends CrmBase {

	@Test(description = "根据体检中心id查询官方套餐", groups = { "qa" }, dataProvider = "officialmeals")
	public void test_01_officialmeals(String... args) throws SqlException {
		String filterBasicMeal = args[1];
		int hospitalId = defhospital.getId();
		List<NameValuePair> pairs = new ArrayList<>();
		pairs.add(new BasicNameValuePair("filterBasicMeal", filterBasicMeal));
		pairs.add(new BasicNameValuePair("orderBy", "updateTime"));

		HttpResult result = httpclient.get(Card_Officialmeals, pairs, hospitalId + "");
		String body = result.getBody();
//		System.out.println(body);
		Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);		
		List<Meal> meals = JSON.parseObject(body, new TypeReference<List<Meal>>() {
		});

		// 按照ID排序
		Collections.sort(meals, new Comparator<Meal>() {
			@Override
			public int compare(Meal t1, Meal t2) {
				return t1.getId() - t2.getId();
			}
		});

		if (checkdb) {
			String sql = "SELECT DISTINCT m.id, m.hospital_id, m.name, m.description, m.pinyin, m.discount, m.external_discount, m.gender, m.type, m.disable, m.keyword, m.init_price, m.display_price, m.price, m.tip_text, m.sequence, m.update_time, tb_meal_statistics.hot, tb_meal_statistics.order_count, tb_meal_statistics.click_count, tb_meal_settings.show_meal_price, tb_meal_settings.adjust_price, tb_meal_settings.lock_price FROM tb_meal m LEFT JOIN tb_meal_statistics ON tb_meal_statistics.meal_id = m.id LEFT JOIN tb_meal_settings ON tb_meal_settings.meal_id = m.id LEFT JOIN tb_meal_tag ON tb_meal_tag.meal_id = m.id where m.hospital_id=? AND m.disable < 2 AND m.type = ? ORDER BY m.sequence";
			List<Map<String, Object>> mealList = DBMapper.query(sql, hospitalId, MealTypeEnum.COMMON_MEAL.getCode());
			Collections.sort(mealList, new Comparator<Map<String, Object>>() {
				@Override
				public int compare(Map<String, Object> o1, Map<String, Object> o2) {
					return Integer.valueOf(o1.get("id").toString()) - Integer.valueOf(o2.get("id").toString());
				}
			});

			// items 按照id 排序
			Collections.sort(mealList, new Comparator<Map<String, Object>>() {
				@Override
				public int compare(Map<String, Object> t1, Map<String, Object> t2) {
					return Integer.valueOf(t1.get("id").toString()) - Integer.valueOf(t2.get("id").toString());
				}
			});
			if (AssertUtil.isNotNull(filterBasicMeal) && Boolean.valueOf(filterBasicMeal)) {
				// 再获取体检中心的基础套餐
				sql = "select * from tb_hospital_settings where hospital_id=?";
				List<Map<String, Object>> basicMealList = DBMapper.query(sql, hospitalId);
				String basicMealId = basicMealList.get(0).get("basic_meal_id").toString();
				if (AssertUtil.isNotNull(basicMealId)) {
					mealList = mealList.stream().filter(meal -> {
						return AssertUtil.areNotEquals(meal.get("id"), basicMealList.get(0).get("basic_meal_id"));
					}).collect(Collectors.toList());
				}
			}

			Assert.assertEquals(meals.size(), mealList.size());
			for (int i = 0; i < meals.size(); i++) {
				Meal meal = meals.get(i);
				Map<String, Object> map = mealList.get(i);
				Assert.assertEquals(meal.getDescription(), map.get("description"));
				Assert.assertEquals(meal.getDisable(), map.get("disable"));
				Assert.assertEquals(meal.getDiscount(), map.get("discount"));
				Assert.assertEquals(meal.getDisplayPrice(), map.get("display_price"));
				Assert.assertEquals(meal.getGender(), map.get("gender"));
				Assert.assertEquals(meal.getHospitalId(), map.get("hospital_id"));
				Assert.assertEquals(meal.getId(), map.get("id"));
				Assert.assertEquals(meal.getInitPrice(), map.get("init_price"));
				Assert.assertEquals(meal.getKeyword(), map.get("keyword"));
				Assert.assertEquals(meal.getName(), map.get("name"));
				Assert.assertEquals(meal.getPinyin(), map.get("pinyin"));
				Assert.assertEquals(meal.getPrice(), map.get("price"));
				Assert.assertEquals(meal.getSequence(), map.get("sequence"));
				Assert.assertEquals(meal.getType(), map.get("type"));

				String settingsSql = "select * from tb_meal_settings where meal_id=?";
				List<Map<String, Object>> settingsList = DBMapper.query(settingsSql, meal.getId());
				Assert.assertEquals(meal.getMealSetting().getAdjustPrice(), settingsList.get(0).get("adjust_price"));
				if(settingsList.get(0).get("lock_price") != null)
				Assert.assertEquals(meal.getMealSetting().getLockPrice() ? 1 : 0,
						settingsList.get(0).get("lock_price"));
				Assert.assertEquals(meal.getMealSetting().isOnlyShowMealItem() ? 1 : 0,
						settingsList.get(0).get("only_show_meal_item") == null ? 0
								: settingsList.get(0).get("only_show_meal_item"));
				Assert.assertEquals(meal.getMealSetting().isShowItemPrice() ? 1 : 0,
						settingsList.get(0).get("show_item_price") == null ? 0
								: settingsList.get(0).get("show_item_price"));
				Assert.assertEquals(meal.getMealSetting().isShowMealPrice() ? 1 : 0,
						settingsList.get(0).get("show_meal_price") == null ? 0
								: settingsList.get(0).get("show_meal_price"));
			}
		}
	}

	@DataProvider(name = "officialmeals")
	public Iterator<String[]> officialmeals() {
		try {
			return CvsFileUtil.fromCvsFileToIterator("./csv/card/officialmeals.csv", 1);
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
