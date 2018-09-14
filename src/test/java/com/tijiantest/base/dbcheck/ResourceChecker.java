package com.tijiantest.base.dbcheck;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import com.tijiantest.model.resource.meal.*;
import com.tijiantest.model.hospital.Hospital;
import com.tijiantest.model.item.ExamItemRelationEnum;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.collections.CollectionUtils;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.BeanUtils;
import org.testng.Assert;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.jayway.jsonpath.JsonPath;
import com.tijiantest.base.BaseTest;
import com.tijiantest.base.Flag;
import com.tijiantest.base.HttpResult;
import com.tijiantest.base.MyHttpClient;
import com.tijiantest.model.card.CardMeal;
import com.tijiantest.model.card.HospitalMealDto;
import com.tijiantest.model.examitempackage.ExamItemPackage;
import com.tijiantest.model.item.ExamItem;
import com.tijiantest.model.item.ExamItemTypeEnum;
import com.tijiantest.model.item.ItemRelationFunction;
import com.tijiantest.model.item.ItemSelectException.ConflictType;
import com.tijiantest.model.order.ItemsInOrder;
import com.tijiantest.model.order.snapshot.ExamItemSnapshot;
import com.tijiantest.util.AssertUtil;
import com.tijiantest.util.ListUtil;
import com.tijiantest.util.db.DBMapper;
import com.tijiantest.util.db.SqlException;
/**
 * 资源（单项/单项包/套餐）
 * @author huifang
 *
 */
public class ResourceChecker extends BaseTest{
	/**
	 * tb_meal
	 */
	public static Meal getMealInfo(int id) {
		String sql = "select * from tb_meal where id = " + id + " ";
		log.debug("tb_meal:" + sql);
		Meal meal = null;
		List<Map<String, Object>> list = null;
		try {
			list = DBMapper.query(sql);
			if (list.size() > 0) {
				Map<String, Object> m = list.get(0);
				int hospitalId = (Integer) m.get("hospital_id");
				int gender = (Integer) m.get("gender");
				int type = (Integer) m.get("type");
				String name = m.get("name").toString();
				int price = (Integer) m.get("price");
				double discount = (double) m.get("discount");
				Object sequence = m.get("sequence");
				int init_price = (Integer) m.get("init_price");
				Object description = m.get("description");
				int disable = (Integer) m.get("disable");
				Object keyword = m.get("keyword");
				String pinYin = m.get("pinyin").toString();
				meal = new Meal(id, name, gender, price, hospitalId, init_price, type, discount, disable, pinYin);
				if (description != null)
					meal.setDescription(description.toString());
				if (m.get("sequence") != null)
					meal.setSequence((Integer) sequence);
				if (m.get("keyword") != null)
					meal.setKeyword(keyword.toString());
				List<MealSetting> settings = getMealSettingsInfo(id);
				if(settings != null && settings.size() > 0)
					meal.setMealSetting(settings.get(0));
			}
		} catch (SqlException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		return meal;
	}
	/**
	 * 根据医院id查询可用的单项列表
	 * @param hospitalId
	 * @return
	 */
	public static List<ExamItem> getHospitalExamItems(int hospitalId,int gender,boolean isShow){
		List<ExamItem> retList = new ArrayList<ExamItem>();
		String sql = "select * from tb_examitem where hospital_id = ? and gender in (2,"+gender+") and type = 1 ";
		if(isShow)
			sql += " and is_show = 1";
		else 
			sql += " and is_show = 0";
		try {
			List<Map<String,Object>> list = DBMapper.query(sql, hospitalId);
			for(Map<String,Object> m : list){
				int examItemId = Integer.parseInt(m.get("id").toString());
				ExamItem e = checkExamItem(examItemId);
				retList.add(e);
			}
			return retList;
		} catch (SqlException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		return retList;
	}
	/**
	 * tb_examitem
	 * 
	 * @param itemIds
	 * @return
	 */
	public static List<ExamItem> getItems(String itemIds) {
		List<ExamItem> eis = new ArrayList<ExamItem>();
		;
		String ids = "(" + itemIds + ")";
		String sqlStr = "SELECT * FROM tb_examitem where id in " + ids + "";
		log.info("sqlstr:.............." + sqlStr);
		try {
			List<Map<String, Object>> list = DBMapper.query(sqlStr);
			for (Map<String, Object> m : list) {
				ExamItem item = new ExamItem();
				item.setDiscount(m.get("is_discount").equals(1) ? true : false);
				item.setFocus(m.get("focus").equals(1) ? true : false);
				item.setGender((Integer) m.get("gender"));
				item.setHospitalId((Integer) m.get("hospital_id"));
				item.setId((Integer) m.get("id"));
				item.setItemType((Integer) m.get("type"));
				item.setName(m.get("name").toString());
				item.setPinyin(m.get("pinyin").toString());
				item.setPrice((Integer) m.get("price"));
				item.setSyncPrice(m.get("sync_price").equals(1) ? true : false);
				if (m.get("description") != null)
					item.setDescription(m.get("description").toString());
				if (m.get("detail") != null)
					item.setDetail(m.get("detail").toString());
				if (m.get("fit_people") != null)
					item.setFitPeople(m.get("fit_people").toString());
				if (m.get("his_item_id") != null)
					item.setHisItemId(m.get("his_item_id").toString());
				if (m.get("sequence") != null)
					item.setSequence((Integer) m.get("sequence"));
				eis.add(item);
			}
		} catch (SqlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return eis;
	}

	/**
	 * Table tb_examitem
	 */
	public static ExamItem checkExamItem(int itemId) {
		ExamItem item = new ExamItem();
		String sqlStr = "select * from tb_examitem where id = " + itemId + "";
		log.debug("sql:" + sqlStr);
		List<Map<String, Object>> list = null;
		try {
			list = DBMapper.query(sqlStr);
			for (Map<String, Object> m : list) {
				boolean discount = ((Integer) m.get("is_discount") == 1) ? true : false;
				boolean enablecustom = ((Integer) m.get("enable_custom") == 1) ? true : false;
				boolean focus = ((Integer) m.get("focus") == 1) ? true : false;
				boolean sync_price = ((Integer) m.get("sync_price") == 1) ? true : false;
				boolean is_show = ((Integer) m.get("is_show") == 1) ? true : false;
				if(m.get("show_warning")!=null){
					boolean is_show_warning = ((Integer) m.get("show_warning") == 1) ? true : false;
					item.setShowWarning(is_show_warning);
				}
				boolean bottleneck = ((Integer) m.get("bottleneck") == 1) ? true : false;

				item.setId((Integer) m.get("id"));
				item.setHospitalId((Integer) m.get("hospital_id"));
				item.setGender((Integer) m.get("gender"));
				item.setName(m.get("name").toString());
				if(m.get("pinyin")!=null)
					item.setPinyin(m.get("pinyin").toString());
				item.setPrice((Integer) m.get("price"));
				item.setDiscount(discount);
				item.setShow(is_show);
				item.setEnableCustom(enablecustom);
				item.setItemType((Integer) m.get("type"));
				item.setFocus(focus);
				item.setSyncPrice(sync_price);
				item.setBottleneck(bottleneck);

				if (m.get("description") != null)
					item.setDescription(m.get("description").toString());
				if (m.get("detail") != null)
					item.setDetail(m.get("detail").toString());
				if (m.get("fit_people") != null)
					item.setFitPeople(m.get("fit_people").toString());
				if (m.get("unfit_people") != null)
					item.setUnfitPeople(m.get("unfit_people").toString());
				if (m.get("group_id") != null)
					item.setGroupId((Integer) m.get("group_id"));
				if (m.get("sequence") != null)
					item.setSequence((Integer) m.get("sequence"));
				if (m.get("his_item_id") != null)
					item.setHisItemId(m.get("his_item_id").toString());
				if (m.get("department_id") != null)
					item.setDepartmentId((Integer) m.get("department_id"));
				if (m.get("tag_name") != null)
					item.setTagName(m.get("tag_name").toString());
				if (m.get("warning") != null)
					item.setWarning(m.get("warning").toString());
			}
		} catch (SqlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return item;

	}
	
	

	// 创建人数控制大项
	public static int createLimitItem(MyHttpClient httpclient,int hospitalId, String name) throws SqlException {
		List<NameValuePair> params = new ArrayList<>();
		params.add(new BasicNameValuePair("hospitalId", String.valueOf(hospitalId)));
		params.add(new BasicNameValuePair("name", name));

		HttpResult result = httpclient.get(Item_CreateLimitItem, params);
		Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);

		String sql = "SELECT DISTINCT a.id AS id, a. NAME AS NAME FROM tb_examitem a WHERE a.hospital_id =? AND a.type = ? and a.name=? ORDER BY id DESC limit 1";
		List<Map<String, Object>> list = DBMapper.query(sql, hospitalId, ExamItemTypeEnum.LIMIT.getCode(), name);

		return Integer.parseInt(list.get(0).get("id").toString());
	}

	// 删除人数控制单项
	public static void deleteLimitItem(MyHttpClient httpclient,String itemId) {
		List<NameValuePair> params = new ArrayList<>();
		params.add(new BasicNameValuePair("itemId", itemId));
		HttpResult result = httpclient.get(Item_DeleteLimitItem, params);
		Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);
	}
	
	// 获取该医院单项，且获取前两项
	public static String getItemList(MyHttpClient httpclient,int hospitalId){
		List<NameValuePair> params = new ArrayList<>();
		params.add(new BasicNameValuePair("hospitalId", String.valueOf(hospitalId)));

		HttpResult result = httpclient.get(Item_ItemList, params);
		Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);

		List<ExamItem> examItemList = JSON.parseArray(result.getBody(), ExamItem.class);
		String selectIds = "";
		for (int i = 0; i <=2; i++) {
			selectIds = selectIds + examItemList.get(i).getId()+",";
		}
		//取前两项，添加为子项
		int index = selectIds.lastIndexOf(",");
		selectIds = selectIds.substring(0, index);
		return selectIds;
	}

	// 添加子项
	public static void addLimitItems1(MyHttpClient httpclient,int itemId, String selectIds) {
		List<NameValuePair> pairs = new ArrayList<>();
		pairs.add(new BasicNameValuePair("selectIds", selectIds));
		pairs.add(new BasicNameValuePair("itemId", String.valueOf(itemId)));
		HttpResult result = httpclient.post(Item_AddLimitItems, pairs);
		log.info(result.getBody());
		Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);
	}
	
	//删除单项
	public void deleteItems(MyHttpClient httpclient,String itemIdList){
		List<NameValuePair> params = new ArrayList<>();
		params.add(new BasicNameValuePair("itemIdList", itemIdList));
		log.info("删除的ID:" + itemIdList);

		Map<String, String> map = new HashMap<>();
		map.put("mask", "true");
		map.put("object", "true");

		HttpResult response = httpclient.post(Flag.CRM, Item_DeleteItems, params, map);	
		Assert.assertEquals(response.getCode(), HttpStatus.SC_OK);
	}
	

	/**
	 * 根据医院，套餐类型，单位，CRM登陆账户id来提取单位套餐详情
	 * 
	 * @param hospitalId
	 * @param type
	 * @param companyId
	 * @param accountId
	 * @return
	 */
	public static List<Meal> getMealByRelation(int hospitalId, int type, int newCompanyId) {

		List<Meal> mealList = new ArrayList<Meal>();
		String sql = "select m.* from tb_meal m ,tb_meal_customized c  where m.hospital_id = ? and m.type = ? and m.id = c.meal_id and c.new_company_id = ? and m.disable = 0";
		Meal meal = null;
		List<Map<String, Object>> list = null;
		try {
			list = DBMapper.query(sql, hospitalId, type, newCompanyId);
		} catch (SqlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				Map<String, Object> m = list.get(i);
				int id = Integer.parseInt(m.get("id").toString());
				int gender = (Integer) m.get("gender");
				String name = m.get("name").toString();
				int price = (Integer) m.get("price");
				double discount = (double) m.get("discount");
				Object sequence = m.get("sequence");
				int init_price = (Integer) m.get("init_price");
				Object description = m.get("description");
				int disable = (Integer) m.get("disable");
				Object keyword = m.get("keyword");
				String pinYin = m.get("pinyin").toString();
				meal = new Meal(id, name, gender, price, hospitalId, init_price, type, discount, disable, pinYin);
				if (description != null)
					meal.setDescription(description.toString());
				if (m.get("sequence") != null)
					meal.setSequence((Integer) sequence);
				if (m.get("keyword") != null)
					meal.setKeyword(keyword.toString());
				mealList.add(meal);
			}
			return mealList;
		}
		return null;
	}

	/**
	 * tb_meal_settings
	 * 
	 * @param mealid
	 * @return
	 */
	public static List<MealSetting> getMealSettingsInfo(int mealid) {

		List<MealSetting> mealettings = new ArrayList<MealSetting>();
		MealSetting mealSetting = null;
		String sqlStr = "SELECT * " + "FROM tb_meal_settings " + "WHERE meal_id = ?;";
		log.debug("sqlstr:.............." + sqlStr);
		try {
			List<Map<String, Object>> list = DBMapper.query(sqlStr, mealid);
			for (Map<String, Object> m : list) {
				int mealId = (Integer) m.get("meal_id");
				boolean showMealPrice = m.get("show_meal_price").equals(1) ? true : false;
				boolean showItemPrice = m.get("show_meal_price").equals(1) ? true : false;
				int adjustPrice = (Integer) m.get("adjust_price");
				boolean lockPrice = false;
				if(m.get("lock_price") !=null)
					lockPrice = m.get("lock_price").equals(1) ? true : false;
				boolean onlyShowMealItem = false;
				if(m.get("only_show_meal_item")!=null)
					onlyShowMealItem = m.get("only_show_meal_item").equals(1)?true:false;
				mealSetting = new MealSetting(mealId, showMealPrice, showItemPrice, adjustPrice, lockPrice);

				if (m.get("only_show_meal_item") != null)
					mealSetting.setOnlyShowMealItem(onlyShowMealItem);

				mealettings.add(mealSetting);
			}
		} catch (SqlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return mealettings;
	}
	
	
	/**
	 * 获取改项时上一次的订单项目
	 * @param orderId
	 * @return
	 */
	public static List<Integer> getFirstItems(int orderId) {
		// TODO Auto-generated method stub
	  String sqlStr = "SELECT *"
	  		+ "FROM tb_order_change_log "
	  		+ "WHERE order_id = ? "
	  		+ "ORDER BY id "
	  		+ "LIMIT 1;";
	  List<Map<String, Object>> list = null;
	try {
		list = DBMapper.query(sqlStr, orderId);
	} catch (SqlException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	List<Integer> ids = Lists.newArrayList();
	String logString = list.get(0).get("old_items").toString();
	if (!Strings.isNullOrEmpty(logString)) {
		logString = logString.replaceAll("\\:\\d+", "");
		String[] idStrings= logString.split(",");
		for (String idString:idStrings) {
			if (idString.matches("\\d+")) {
				ids.add(Integer.valueOf(idString));
			} else {
				log.info("change log 中 {} 不是有效数字,没有加入items列表"+idString);
			}
		}
	}
		return ids;
	}
	

	/**
	 * 根据医院，提取套餐详情
	 * 
	 * @param hospitalId
	 * @return
	 */
	public static List<Meal> getOffcialMeal(int hospitalId) {
		List<Meal> mealList = new ArrayList<Meal>();
		String sql = "SELECT * from tb_meal WHERE type = 3 AND `disable` = 0  AND price > 0 AND hospital_id = ? order by id ;";
		Meal meal = null;
		List<Map<String, Object>> list = null;
		try {
			list = DBMapper.query(sql, hospitalId);
		} catch (SqlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				Map<String, Object> m = list.get(i);
				int id = Integer.parseInt(m.get("id").toString());
				int gender = (Integer) m.get("gender");
				String name = m.get("name").toString();
				int price = (Integer) m.get("price");
				double discount = (double) m.get("discount");
				Object sequence = m.get("sequence");
				int init_price = (Integer) m.get("init_price");
				Object description = m.get("description");
				int disable = (Integer) m.get("disable");
				Object keyword = m.get("keyword");
				String pinYin = m.get("pinyin").toString();
				meal = new Meal(id, name, gender, price, hospitalId, init_price, 3, discount, disable, pinYin);
				if (description != null)
					meal.setDescription(description.toString());
				if (m.get("sequence") != null)
					meal.setSequence((Integer) sequence);
				if (m.get("keyword") != null)
					meal.setKeyword(keyword.toString());
				mealList.add(meal);
			}
			return mealList;
		}
		return null;
	}
	
	/**
	 * 根据医院，提取套餐详情
	 * 
	 * @param hospitalId
	 * @return
	 */
	public static List<Meal> getOffcialMealByHospitalIdAndType(Integer hospitalId,Integer type) {
		List<Meal> mealList = new ArrayList<Meal>();
		String sql = "SELECT * from tb_meal WHERE type = ? and disable < 2 AND hospital_id = ? order by id ;";
		Meal meal = null;
		List<Map<String, Object>> list = null;
		try {
			list = DBMapper.query(sql, type,hospitalId);
		} catch (SqlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				Map<String, Object> m = list.get(i);
				int id = Integer.parseInt(m.get("id").toString());
				int gender = (Integer) m.get("gender");
				String name = m.get("name").toString();
				int price = (Integer) m.get("price");
				double discount = (double) m.get("discount");
				Object sequence = m.get("sequence");
				int init_price = (Integer) m.get("init_price");
				Object description = m.get("description");
				int disable = (Integer) m.get("disable");
				Object keyword = m.get("keyword");
				String pinYin = m.get("pinyin").toString();
				meal = new Meal(id, name, gender, price, hospitalId, init_price, 3, discount, disable, pinYin);
				if (description != null)
					meal.setDescription(description.toString());
				if (m.get("sequence") != null)
					meal.setSequence((Integer) sequence);
				if (m.get("keyword") != null)
					meal.setKeyword(keyword.toString());
				mealList.add(meal);
			}
		}
		return mealList;
	}

	

	/**
	 * 根据医院，提取官方套餐详情
	 * 
	 * @param hospitalId
	 * @return
	 */
	public static List<Meal> getOffcialMeal(int hospitalId,List<Integer> gender) {
		List<Meal> mealList = new ArrayList<Meal>();
		String sql = "SELECT * from tb_meal WHERE type = 3 AND gender in ("+ListUtil.IntegerlistToString(gender)+") AND `disable` = 0  AND price > 0 AND hospital_id = ? order by id ;";
		Meal meal = null;
		List<Map<String, Object>> list = null;
		try {
			list = DBMapper.query(sql, hospitalId);
		} catch (SqlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				Map<String, Object> m = list.get(i);
				int id = Integer.parseInt(m.get("id").toString());
				int gender1 = (Integer) m.get("gender");
				String name = m.get("name").toString();
				int price = (Integer) m.get("price");
				double discount = (double) m.get("discount");
				Object sequence = m.get("sequence");
				int init_price = (Integer) m.get("init_price");
				Object description = m.get("description");
				int disable = (Integer) m.get("disable");
				Object keyword = m.get("keyword");
				String pinYin = m.get("pinyin").toString();
				meal = new Meal(id, name, gender1, price, hospitalId, init_price, 3, discount, disable, pinYin);
				if (description != null)
					meal.setDescription(description.toString());
				if (m.get("sequence") != null)
					meal.setSequence((Integer) sequence);
				if (m.get("keyword") != null)
					meal.setKeyword(keyword.toString());
				mealList.add(meal);
			}
			return mealList;
		}
		return null;
	}


	/**
	 * 根据医院，提取可改项目/不可改项目的官方套餐详情
	 *
	 * @param hospitalId
	 * @return
	 */
	public static List<Meal> getOffcialMeal(int hospitalId,List<Integer> gender,boolean allow_change_item) {
		List<Meal> mealList = new ArrayList<Meal>();
		String sql = "SELECT * from tb_meal WHERE type = 3 AND gender in ("+ListUtil.IntegerlistToString(gender)+") AND `disable` = 0  AND price > 0 AND hospital_id = ?";
		if(allow_change_item)
			sql += " AND allow_change_item = 1 ";
		else
			sql += " AND allow_change_item = 0 ";

		sql += " order by id ;";
		Meal meal = null;
		List<Map<String, Object>> list = null;
		try {
			list = DBMapper.query(sql, hospitalId);
		} catch (SqlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				Map<String, Object> m = list.get(i);
				int id = Integer.parseInt(m.get("id").toString());
				int gender1 = (Integer) m.get("gender");
				String name = m.get("name").toString();
				int price = (Integer) m.get("price");
				double discount = (double) m.get("discount");
				Object sequence = m.get("sequence");
				int init_price = (Integer) m.get("init_price");
				Object description = m.get("description");
				int disable = (Integer) m.get("disable");
				Object keyword = m.get("keyword");
				String pinYin = m.get("pinyin").toString();
				meal = new Meal(id, name, gender1, price, hospitalId, init_price, 3, discount, disable, pinYin);
				if (description != null)
					meal.setDescription(description.toString());
				if (m.get("sequence") != null)
					meal.setSequence((Integer) sequence);
				if (m.get("keyword") != null)
					meal.setKeyword(keyword.toString());
                List<MealSetting> settings = getMealSettingsInfo(id);
                if(settings != null && settings.size() > 0)
                    meal.setMealSetting(settings.get(0));
				mealList.add(meal);
			}
			return mealList;
		}
		return null;
	}

	/**
	 * 根据医院+单位，提取单位套餐详情
	 *
	 * @param hospitalId
	 * @return
	 */
	public static List<Meal> getCompanyMeals(int hospitalId,int companyId,int gender) {
		List<Meal> mealList = new ArrayList<Meal>();
		String sql = "SELECT m.* from tb_meal m ,tb_meal_customized c WHERE m.id = c.meal_id AND c.new_company_id = ? AND " +
				"m.type = 1 AND m.gender = ? AND m.disable = 0  AND m.price > 0 AND m.hospital_id = ? order by m.id ;";
		Meal meal = null;
		List<Map<String, Object>> list = null;
		try {
			list = DBMapper.query(sql, companyId,gender,hospitalId);
		} catch (SqlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				Map<String, Object> m = list.get(i);
				int id = Integer.parseInt(m.get("id").toString());
				int gender1 = (Integer) m.get("gender");
				String name = m.get("name").toString();
				int price = (Integer) m.get("price");
				double discount = (double) m.get("discount");
				Object sequence = m.get("sequence");
				int init_price = (Integer) m.get("init_price");
				Object description = m.get("description");
				int disable = (Integer) m.get("disable");
				Object keyword = m.get("keyword");
				String pinYin = m.get("pinyin").toString();
				meal = new Meal(id, name, gender1, price, hospitalId, init_price, 3, discount, disable, pinYin);
				if (description != null)
					meal.setDescription(description.toString());
				if (m.get("sequence") != null)
					meal.setSequence((Integer) sequence);
				if (m.get("keyword") != null)
					meal.setKeyword(keyword.toString());
				mealList.add(meal);
			}
			return mealList;
		}
		return null;
	}


	/**
	 * tb_hospital_settings
	 */
	public static Integer getBasicMealId(int hospitalId) {
		String sql = "SELECT basic_meal_id FROM tb_hospital_settings WHERE hospital_id = ? ";
		Integer basic_meal_id;
		List<Map<String, Object>> list = null;
		try {
			list = DBMapper.query(sql, hospitalId);
		} catch (SqlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		basic_meal_id = (Integer) list.get(0).get("basic_meal_id");
		return basic_meal_id;
	}
	
	
	/**
	 * 获取套餐单项idlist
	 */
	public static List<Integer> getMealExamItemIdList(int mealId){
		List<Integer> idList = new ArrayList<Integer>();
		List<MealItem> itemList = getMealIteminfo(mealId);
		for(MealItem m : itemList){
			idList.add(m.getId());
		}
		return idList;
	}
	/**
	 * tb_meal_examitem
	 * @param mealId
	 * @return
	 */
	public static List<MealItem> getMealIteminfo(int mealId) {

		List<MealItem> mealItem = new ArrayList<MealItem>();
		MealItem item = null;
		String sqlStr = "select " + "me.meal_id,me.id as id ,me.is_basic ,me.enable_select,me.selected,me.sequence,me.is_show," + "e.id as eid ,e.name,e.price,e.is_discount,e.gender " + "FROM tb_meal_examitem me " + "LEFT JOIN tb_examitem e "
				+ "ON me.item_id = e.id " + "WHERE me.meal_id = ? order by e.sequence ";
		log.debug("sqlstr:.............." + sqlStr);
		try {
			List<Map<String, Object>> list = DBMapper.query(sqlStr, mealId);
			for (Map<String, Object> m : list) {
				if(m.get("eid")!=null){
					item = new MealItem((Integer) m.get("eid"), (Integer) m.get("meal_id"),
							m.get("is_basic").equals(1) ? true : false, m.get("enable_select").equals(1) ? true : false,
							(Integer) m.get("gender"), m.get("selected").equals(1) ? true : false,
							(Integer) m.get("sequence"), m.get("is_show").equals(1) ? true : false);
					mealItem.add(item);
				}


			}
		} catch (SqlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return mealItem;
	}



	
	/**
	 * 获取基本官方套餐
	 * @param hospitalId
	 * @param gender
	 * @return
	 * @throws SqlException
	 */
	public static  List<Meal> getOfficialMealList(int hospitalId,int gender) throws SqlException{
		String sqlStr = "SELECT * FROM tb_meal where type = 3 and disable = 0 and gender = ? and  price > 0 and init_price > 0  and hospital_id = ? order by id";
		List<Meal> packagelists = new ArrayList<Meal>();
			List<Map<String, Object>> list = DBMapper.query(sqlStr,gender, hospitalId);
			for (Map<String, Object> m : list) {
				Meal pack = new Meal();
				pack.setId(Integer.parseInt(m.get("id").toString()));
				pack.setType(Integer.parseInt(m.get("type").toString()));
				pack.setHospitalId(Integer.parseInt(m.get("hospital_id").toString()));
				pack.setGender(Integer.parseInt(m.get("gender").toString()));
				pack.setPrice(Integer.parseInt(m.get("price").toString()));
				pack.setDisable(Integer.parseInt(m.get("disable").toString()));
				packagelists.add(pack);
		}
			return packagelists;
	}




	/**
	 * 获取是/否套餐多选一的官方套餐
	 * @param hospitalId
	 * @param gender
	 * @param isMultiChooseOne true:是套餐多选一;false:不是套餐多选一
	 * @return
	 * @throws SqlException
	 */
	public static  List<Meal> getOfficialMealListByMultiChooseOne(int hospitalId,int gender,boolean isMultiChooseOne) throws SqlException{
		String sqlStr = "SELECT * FROM tb_meal where type = 3 and disable = 0 and gender = ? and  price > 0 and init_price > 0  and hospital_id = ? ";
		if(isMultiChooseOne)
			sqlStr += " and id in (select meal_id from tb_meal_multi_choosen_examitem)";
		else
			sqlStr += " and id not in (select meal_id from tb_meal_multi_choosen_examitem)";
		sqlStr += " order by id";
		List<Meal> packagelists = new ArrayList<Meal>();
		List<Map<String, Object>> list = DBMapper.query(sqlStr,gender, hospitalId);
		for (Map<String, Object> m : list) {
			Meal pack = new Meal();
			pack.setId(Integer.parseInt(m.get("id").toString()));
			pack.setType(Integer.parseInt(m.get("type").toString()));
			pack.setHospitalId(Integer.parseInt(m.get("hospital_id").toString()));
			pack.setGender(Integer.parseInt(m.get("gender").toString()));
			pack.setPrice(Integer.parseInt(m.get("price").toString()));
			pack.setDisable(Integer.parseInt(m.get("disable").toString()));
			packagelists.add(pack);
		}
		return packagelists;
	}

	/**
	 * 通过套餐ID查询套餐多选一组内的组合单项信息
	 * @param mealId
	 * @return
	 */
	public static List<MealExamitemGroup> getMealExamitemGroupByMealId(int mealId) throws SqlException {
		String sql = "select * from tb_meal_multi_choosen_examitem where meal_id = "+mealId;
		List<MealExamitemGroup> packagelists = new ArrayList<MealExamitemGroup>();
		List<Map<String, Object>> list = DBMapper.query(sql);
		for (Map<String, Object> m : list) {
			MealExamitemGroup pack = new MealExamitemGroup();
			pack.setId(Integer.parseInt(m.get("id").toString()));
			pack.setMealId(Integer.parseInt(m.get("meal_id").toString()));
			pack.setGroupId(m.get("multi_choosen_id").toString());
			pack.setGroupName(m.get("multi_choosen_name").toString());
			pack.setItemId(Integer.parseInt(m.get("item_id").toString()));
			pack.setSelected(Integer.parseInt(m.get("selected").toString()) == 1?true:false);
			if(m.get("ext")!=null)
				pack.setExt(m.get("ext").toString());
			packagelists.add(pack);
		}
		return packagelists;
	}


	/**
	 * 通过套餐ID查询套餐多选一组列表
	 * @param mealId
	 * @return
	 */
	public static List<String> getMealGroupByMealId(int mealId)  {
		String sql = "select DISTINCT multi_choosen_id from tb_meal_multi_choosen_examitem where meal_id = "+mealId;
		List<String> packagelists = new ArrayList<String>();
		List<Map<String, Object>> list = null;
		try {
			list = DBMapper.query(sql);
			if(list != null && list.size()>0)
				for (Map<String, Object> m : list) {
					packagelists.add(m.get("multi_choosen_id").toString());
				}
		} catch (SqlException e) {
			e.printStackTrace();
		}

		return packagelists;
	}


	/**
	 * 通过套餐ID & 套餐内的组ID 查询套餐多选一组内的组合单项信息
	 * @param mealId
	 * @param groupId
	 * @return
	 */
	public static List<MealExamitemGroup> getMealExamitemGroupByMealId(int mealId,String groupId) {
		String sql = "select * from tb_meal_multi_choosen_examitem where meal_id = "+mealId + " and multi_choosen_id = '"+groupId+"'";
		List<MealExamitemGroup> packagelists = new ArrayList<MealExamitemGroup>();
		List<Map<String, Object>> list = null;
		try {
			list = DBMapper.query(sql);
			for (Map<String, Object> m : list) {
				MealExamitemGroup pack = new MealExamitemGroup();
				pack.setId(Integer.parseInt(m.get("id").toString()));
				pack.setMealId(Integer.parseInt(m.get("meal_id").toString()));
				pack.setGroupId(m.get("multi_choosen_id").toString());
				pack.setGroupName(m.get("multi_choosen_name").toString());
				pack.setItemId(Integer.parseInt(m.get("item_id").toString()));
				pack.setSelected(Integer.parseInt(m.get("selected").toString()) == 1?true:false);
				if(m.get("ext")!=null)
					pack.setExt(m.get("ext").toString());
				packagelists.add(pack);
			}
		} catch (SqlException e) {
			e.printStackTrace();
		}

		return packagelists;
	}

	/**
	 * 根据套餐ID和等价组ID查询默认等价组默认单项ID
	 * @param mealId
	 * @param groupId
	 * @return
	 * @throws SqlException
	 */
	public static int getExamitemGroupDefaultSelectId(int mealId,String groupId) {
		String sql = "select * from tb_meal_multi_choosen_examitem where meal_id = "+mealId + " and multi_choosen_id = '"+groupId+"'";
		List<MealExamitemGroup> packagelists = new ArrayList<MealExamitemGroup>();
		List<Map<String, Object>> list = null;
		try {
			list = DBMapper.query(sql);
			for (Map<String, Object> m : list) {
				if(Integer.parseInt(m.get("selected").toString()) == 1)
					return Integer.parseInt(m.get("item_id").toString());
			}
		} catch (SqlException e) {
			e.printStackTrace();
		}

		return 0;
	}

	/***************单项包*******************************************
	 * 
	 * 
	 */
	

	/**
	 * 获取套餐与单项之间的关联关系 套餐外单项，套餐内单项
	 * 
	 * @param mealId
	 * @return
	 * @throws SqlException
	 */
	public static List<ExamItem> getMealExamItemsRelationLists(int mealId, List<Integer> itemIds) throws SqlException {
		if (itemIds != null && itemIds.size() > 0) {
			List<ExamItem> eis = new ArrayList<ExamItem>();
			String ids = '(' + ListUtil.IntegerlistToString(itemIds) + ')';
			List<Map<String, Object>> list = null;
			String str = "SELECT e.*, m.is_basic ,m.enable_select,m.selected ,m.is_show FROM tb_examitem e , tb_meal_examitem m  where   m.item_id = e.id "
					+ " and  e.id IN " + ids + " and m.meal_id = " + mealId + " order by e.id ";
			List<Integer> mealExistItemIds = new ArrayList<Integer>();// 套餐内单项id列表
			List<Integer> mealExtraItemIds = new ArrayList<Integer>();// 套餐外单项id列表
			if (itemIds.size() > 0) {
				list = DBMapper.query(str);
				for (Map<String, Object> m : list) {
					ExamItem ei = new ExamItem();
					ei.setId((Integer.parseInt(m.get("id").toString())));
					ei.setName(m.get("name").toString());
					ei.setGender(Integer.parseInt(m.get("gender").toString()));
					ei.setHospitalId(Integer.parseInt(m.get("hospital_id").toString()));
					if (m.get("meal_id") != null)
						ei.setMealId(Integer.parseInt(m.get("meal_id").toString()));
					ei.setPrice(Integer.parseInt(m.get("price").toString()));
					if (m.get("group_id") != null)
						ei.setGroupId(Integer.parseInt(m.get("group_id").toString()));
					ei.setDiscount(((Integer) m.get("is_discount") == 1) ? true : false);
					ei.setEnableCustom(((Integer) m.get("enable_custom") == 1) ? true : false);
					if (m.get("his_item_id") != null)
						ei.setHisItemId(m.get("his_item_id").toString());
					ei.setSyncPrice(((Integer) m.get("sync_price") == 1) ? true : false);
					ei.setItemType(1);
					if (m.get("description") != null)
						ei.setDescription(m.get("description").toString());
					if (m.get("detail") != null)
						ei.setDetail(m.get("detail").toString());
					if (m.get("fit_people") != null)
						ei.setFitPeople((m.get("fit_people").toString()));
					ei.setPrice(Integer.parseInt(m.get("price").toString()));
					ei.setShow(((Integer) m.get("is_show") == 1) ? true : false);
					ei.setBasic(((Integer) m.get("is_basic") == 1) ? true : false);
					ei.setEnableSelect(((Integer) m.get("enable_select") == 1) ? true : false);
					if ((Integer) m.get("selected") == 1) {
						ei.setSelected(true);
						mealExistItemIds.add(Integer.parseInt(m.get("id").toString()));
					} else
						ei.setSelected(false);
					eis.add(ei);
				}
				if (mealExistItemIds.size() < itemIds.size()) {
					for (Integer i : itemIds) {
						if (!mealExistItemIds.contains(i))
							mealExtraItemIds.add(i);
					}
					List<ExamItem> elist = getExamItemsBySelected(mealExtraItemIds);
					if(elist != null){
						for (ExamItem e : elist) 
						e.setBasic(false);
						eis.addAll(elist);
					}
					
					
				}

				// sort responseList
				Collections.sort(eis, new Comparator<ExamItem>() {
					@Override
					public int compare(ExamItem o1, ExamItem o2) {
						return o1.getId() - o2.getId();
					}
				});
				return eis;
			}
			return null;
		}
		return null;
	}

	/**
	 * 获取套餐内显示的单项列表以及各种属性
	 * 
	 * @param mealId
	 * @return
	 * @throws SqlException
	 */
	public static List<ExamItem> getMealExamItemsBySelected(int mealId) throws SqlException {
		List<Integer> itemIds = getMealExamItemIdList(mealId);
		if (itemIds != null && itemIds.size() > 0) {
			List<ExamItem> eis = new ArrayList<ExamItem>();
			String ids = '(' + ListUtil.IntegerlistToString(itemIds) + ')';
			List<Map<String, Object>> list = null;
			String str = "SELECT e.*, m.is_basic ,m.enable_select,m.selected ,m.is_show FROM tb_examitem e , tb_meal_examitem m  where   m.item_id = e.id "
					+ " and  e.id IN " + ids + " and m.meal_id = " + mealId + " order by e.id ";
			if (itemIds.size() > 0) {
				list = DBMapper.query(str);
				for (Map<String, Object> m : list) {
					ExamItem ei = new ExamItem();
					ei.setId((Integer.parseInt(m.get("id").toString())));
					ei.setName(m.get("name").toString());
					ei.setGender(Integer.parseInt(m.get("gender").toString()));
					ei.setHospitalId(Integer.parseInt(m.get("hospital_id").toString()));
					if (m.get("meal_id") != null)
						ei.setMealId(Integer.parseInt(m.get("meal_id").toString()));
					ei.setPrice(Integer.parseInt(m.get("price").toString()));
					if (m.get("group_id") != null)
						ei.setGroupId(Integer.parseInt(m.get("group_id").toString()));
					ei.setDiscount(((Integer) m.get("is_discount") == 1) ? true : false);
					ei.setEnableCustom(((Integer) m.get("enable_custom") == 1) ? true : false);
					if (m.get("his_item_id") != null)
						ei.setHisItemId(m.get("his_item_id").toString());
					ei.setSyncPrice(((Integer) m.get("sync_price") == 1) ? true : false);
					ei.setItemType(1);
					ei.setShow(((Integer) m.get("is_show") == 1) ? true : false);
					ei.setBasic(((Integer) m.get("is_basic") == 1) ? true : false);
					ei.setEnableSelect(((Integer) m.get("enable_select") == 1) ? true : false);
					ei.setSelected(((Integer) m.get("selected") == 1) ? true : false);
					if (m.get("description") != null)
						ei.setDescription(m.get("description").toString());
					if (m.get("detail") != null)
						ei.setDetail(m.get("detail").toString());
					if (m.get("fit_people") != null)
						ei.setFitPeople((m.get("fit_people").toString()));
					ei.setPrice(Integer.parseInt(m.get("price").toString()));
					if(ei.isSelected())
						eis.add(ei);
				}
				return eis;
			}
			return null;
		}
		return null;
	}

	/**
	 * 获取单项信息
	 * 
	 * @param itemIds
	 * @return List<ExamItem>
	 */
	public static List<ExamItem> getExamItemsBySelected(List<Integer> itemIds) throws SqlException {
		if (itemIds != null && itemIds.size() > 0) {
			List<ExamItem> eis = new ArrayList<ExamItem>();
			String ids = '(' + ListUtil.IntegerlistToString(itemIds) + ')';
			List<Map<String, Object>> list = null;
			String str = "SELECT * FROM tb_examitem WHERE id IN " + ids + "";
			if (itemIds.size() > 0) {
				list = DBMapper.query(str);
				for (Map<String, Object> m : list) {
					ExamItem ei = new ExamItem();
					ei.setId((Integer.parseInt(m.get("id").toString())));
					ei.setName(m.get("name").toString());
					ei.setGender(Integer.parseInt(m.get("gender").toString()));
					ei.setHospitalId(Integer.parseInt(m.get("hospital_id").toString()));
					if (m.get("meal_id") != null)
						ei.setMealId(Integer.parseInt(m.get("meal_id").toString()));
					ei.setPrice(Integer.parseInt(m.get("price").toString()));
					if (m.get("group_id") != null)
						ei.setGroupId(Integer.parseInt(m.get("group_id").toString()));
					ei.setDiscount(((Integer) m.get("is_discount") == 1) ? true : false);
					ei.setShow(((Integer) m.get("is_show") == 1) ? true : false);
					ei.setEnableCustom(((Integer) m.get("enable_custom") == 1) ? true : false);
					if (m.get("his_item_id") != null)
						ei.setHisItemId(m.get("his_item_id").toString());
					ei.setSyncPrice(((Integer) m.get("sync_price") == 1) ? true : false);
					if (m.get("description") != null)
						ei.setDescription(m.get("description").toString());
					if (m.get("detail") != null)
						ei.setDetail(m.get("detail").toString());
					if (m.get("fit_people") != null)
						ei.setFitPeople((m.get("fit_people").toString()));
					ei.setPrice(Integer.parseInt(m.get("price").toString()));
					ei.setItemType(1);
					eis.add(ei);
				}
				return eis;
			}
			return null;
		}
		return null;
	}


	public static List<ExamItem> getItemInfoByIds(List<Integer> ids) {
		List<ExamItem> items = new ArrayList<ExamItem>();
		String idStr = ListUtil.IntegerlistToString(ids);
		String sql = "SELECT * FROM tb_examitem WHERE id in (" + idStr + ");";
		List<Map<String, Object>> list = null;
		try {
			list = DBMapper.query(sql);
		} catch (SqlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(list != null && list.size() > 0)
			for (Map<String, Object> m : list) {
			ExamItem item = new ExamItem();
			item = setItem(m);
			items.add(item);
		}
		return items;
	}

	/**
	 * Map转为ExamItem
	 * 
	 * @param m
	 * @return
	 */
	public static ExamItem setItem(Map<String, Object> m) {
		ExamItem item = new ExamItem();
		boolean discount = ((Integer) m.get("is_discount") == 1) ? true : false;
		boolean enablecustom = ((Integer) m.get("enable_custom") == 1) ? true : false;
		boolean focus = ((Integer) m.get("focus") == 1) ? true : false;
		boolean sync_price = ((Integer) m.get("sync_price") == 1) ? true : false;
		boolean is_show = ((Integer) m.get("is_show") == 1) ? true : false;
		boolean is_show_warning = ((Integer) m.get("show_warning") == 1) ? true : false;
		boolean bottleneck = ((Integer) m.get("bottleneck") == 1) ? true : false;

		item.setId((Integer) m.get("id"));
		item.setHospitalId((Integer) m.get("hospital_id"));
		item.setGender((Integer) m.get("gender"));
		item.setName(m.get("name").toString());
		item.setPinyin(m.get("pinyin").toString());
		item.setPrice((Integer) m.get("price"));
		item.setDiscount(discount);
		item.setShow(is_show);
		item.setEnableCustom(enablecustom);
		item.setItemType((Integer) m.get("type"));
		item.setFocus(focus);
		item.setSyncPrice(sync_price);
		item.setShowWarning(is_show_warning);
		item.setBottleneck(bottleneck);

		if (m.get("description") != null)
			item.setDescription(m.get("description").toString());
		if (m.get("detail") != null)
			item.setDetail(m.get("detail").toString());
		if (m.get("fit_people") != null)
			item.setFitPeople(m.get("fit_people").toString());
		if (m.get("unfit_people") != null)
			item.setUnfitPeople(m.get("unfit_people").toString());
		if (m.get("group_id") != null)
			item.setGroupId((Integer) m.get("group_id"));
		if (m.get("sequence") != null)
			item.setSequence((Integer) m.get("sequence"));
		if (m.get("his_item_id") != null)
			item.setHisItemId(m.get("his_item_id").toString());
		if (m.get("department_id") != null)
			item.setDepartmentId((Integer) m.get("department_id"));
		if (m.get("tag_name") != null)
			item.setTagName(m.get("tag_name").toString());
		if (m.get("warning") != null)
			item.setWarning(m.get("warning").toString());
		return item;
	}


	/**
	 * 获取套餐包含的单项列表
	 * 
	 * @param mealId
	 * @return
	 */
	public static List<MealItem> getMealInnerItemList(int mealId) {
		List<MealItem> list = getMealRelationItemList(mealId);
		Iterator<MealItem> it = list.iterator();
		while (it.hasNext()) {
			MealItem mitem = it.next();
			if (!mitem.isSelected())
				it.remove();
		}
		return list;
	}

	/**
	 * 获取与套餐关联的单项列表 tb_meal_examitem
	 * 
	 * @param mealId
	 * @return
	 */
	public static List<MealItem> getMealRelationItemList(int mealId) {

		List<MealItem> mealItem = new ArrayList<MealItem>();
		String sqlStr = "select " + "me.meal_id,me.id as id ,me.is_basic ,me.enable_select,me.selected,me.sequence,me.is_show," + "e.id as eid ,e.name,e.price,e.is_discount,e.gender " + "FROM tb_meal_examitem me " + "LEFT JOIN tb_examitem e "
				+ "ON me.item_id = e.id " + "WHERE me.meal_id = ? order by e.sequence ";
		log.debug("sqlstr:.............." + sqlStr);
		try {
			List<Map<String, Object>> list = DBMapper.query(sqlStr, mealId);
			for (Map<String, Object> m : list) {
				MealItem item = new MealItem();
				if(m.get("eid")!=null)
					item.setId(Integer.valueOf(m.get("eid").toString()));
				else
					continue;
				item.setName(m.get("name").toString());
				item.setPrice(Integer.valueOf(m.get("price").toString()));
				item.setMealId(Integer.valueOf(m.get("meal_id").toString()));
				item.setGender(Integer.valueOf(m.get("gender").toString()));
				item.setBasic(m.get("is_basic").equals(1) ? true : false);
				item.setEnableSelect(m.get("enable_select").equals(1) ? true : false);
				item.setSelected(m.get("selected").equals(1) ? true : false);
				item.setSequence(Integer.valueOf(m.get("sequence").toString()));
				item.setShow(m.get("is_show").equals(1) ? true : false);
				item.setDiscount(m.get("is_discount").equals(1) ? true : false);
				mealItem.add(item);

			}
		} catch (SqlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return mealItem;
	}

	
	/**
	 * 获取有用的，且与套餐没有冲突/有冲突的关系的单项包 1.isConflict传true,获取有冲突的包
	 * 2.isConflict传false,获取无冲突的包
	 * 
	 * @param hospitalId
	 * @param mealId
	 * @return
	 * @throws SqlException
	 */
	public static List<ExamItemPackage> getPackageWithMeal(int hospitalId, int mealId, boolean isConflict,boolean isRepeat)
			throws SqlException {
		List<ExamItemPackage> returnPacks = new ArrayList<ExamItemPackage>();
		List<Integer> itemlist = new ArrayList<Integer>();
		Meal meal = getMealInfo(mealId);
		// 获取与套餐符合的所有单项包
		List<ExamItemPackage> ep = getAvaiablePackages(hospitalId, meal.getGender());

		// 逐一排除与套餐有冲突的单项包
		for (ExamItemPackage e : ep) {
			log.debug("单项包..." + e.getName() + "...id..." + e.getId());
			ExamItemPackage enew = getPackageInfo(e.getId());
			itemlist.clear();
			// 拼装单项列表,先套餐,后单项包
			List<MealItem> mealItems = getMealInnerItemList(mealId);
			for (MealItem i : mealItems)
				itemlist.add(i.getId());
			List<ExamItem> items = enew.getItemList();
			for (ExamItem j : items)
				itemlist.add(j.getId());
			boolean flag = judgeItemsHaveConflict(itemlist, hospitalId);
			boolean dupflag = judgeItemsHaveDuplicateItem(itemlist,hospitalId);
			if (isConflict == flag && isRepeat == dupflag)
				returnPacks.add(e);
			
		}
		return returnPacks;
	}
	
	/**
	 * 获取同组关系的单项包列表
	 * @param hospitalId
	 * @param mealId
	 * @return
	 * @throws SqlException
	 */
	public static List<ExamItemPackage> getGroupPackageList(int hospitalId,int mealId,MyHttpClient crmClient) throws SqlException{
		List<ExamItemPackage> returnPacks = new ArrayList<ExamItemPackage>();
		List<Integer> itemlist = new ArrayList<Integer>();
		Meal meal = getMealInfo(mealId);
		// 获取与套餐符合的所有单项包
		List<ExamItemPackage> ep = getAvaiablePackages(hospitalId, meal.getGender());

		// 逐一排除与套餐有冲突的单项包
		for (ExamItemPackage e : ep) {
			log.debug("单项包..." + e.getName() + "...id..." + e.getId());
			ExamItemPackage enew = getPackageInfo(e.getId());
			itemlist.clear();
			// 拼装单项列表,先套餐,后单项包
			List<MealItem> mealItems = getMealInnerItemList(mealId);
			for (MealItem i : mealItems)
				itemlist.add(i.getId());
			List<ExamItem> items = enew.getItemList();
			for (ExamItem j : items)
				itemlist.add(j.getId());
			boolean judge = judgeItemsHaveGroupConflit(itemlist, hospitalId);
			if(judge)
				returnPacks.add(e);
		}
		if(returnPacks.isEmpty()){
			ExamItemPackage g = createGroupExamItemPackage(crmClient, mealId);
			if(g != null)
				returnPacks.add(g);
			}
		
		return returnPacks;
	}
	
	
	/**
	 * 获取合并关系的单项包列表
	 * @param hospitalId
	 * @param mealId
	 * @return
	 * @throws SqlException
	 */
	public static List<ExamItemPackage> getComposePackageList(int hospitalId,int mealId,MyHttpClient crmClient) throws SqlException{
		List<ExamItemPackage> returnPacks = new ArrayList<ExamItemPackage>();
		List<Integer> itemlist = new ArrayList<Integer>();
		Meal meal = getMealInfo(mealId);
		// 获取与套餐符合的所有单项包
		List<ExamItemPackage> ep = getAvaiablePackages(hospitalId, meal.getGender());

		// 逐一排除与套餐有冲突的单项包
		for (ExamItemPackage e : ep) {
			log.debug("单项包..." + e.getName() + "...id..." + e.getId());
			ExamItemPackage enew = getPackageInfo(e.getId());
			itemlist.clear();
			// 拼装单项列表,先套餐,后单项包
			List<MealItem> mealItems = getMealInnerItemList(mealId);
			for (MealItem i : mealItems)
				itemlist.add(i.getId());
			List<ExamItem> items = enew.getItemList();
			for (ExamItem j : items)
				itemlist.add(j.getId());
			boolean judge = judgeItemsHaveComposeConflit(itemlist, hospitalId);
			if(judge)
				returnPacks.add(e);
		}
		if(returnPacks.isEmpty()){
			ExamItemPackage e = createComposeExamItemPackage(crmClient, mealId);
			if(e !=null)
				returnPacks.add(e);
		}
		
		return returnPacks;
	}
	
	
	
	/**
	 * 获取父子关系的单项包列表
	 * @param hospitalId
	 * @param mealId
	 * @return
	 * @throws SqlException
	 */
	public static List<ExamItemPackage> getParentPackageList(int hospitalId,int mealId,MyHttpClient crmClient) throws SqlException{
		List<ExamItemPackage> returnPacks = new ArrayList<ExamItemPackage>();
		List<Integer> itemlist = new ArrayList<Integer>();
		Meal meal = getMealInfo(mealId);
		// 获取与套餐符合的所有单项包
		List<ExamItemPackage> ep = getAvaiablePackages(hospitalId, meal.getGender());

		// 逐一排除与套餐有冲突的单项包
		for (ExamItemPackage e : ep) {
			log.debug("单项包..." + e.getName() + "...id..." + e.getId());
			ExamItemPackage enew = getPackageInfo(e.getId());
			itemlist.clear();
			// 拼装单项列表,先套餐,后单项包
			List<MealItem> mealItems = getMealInnerItemList(mealId);
			for (MealItem i : mealItems)
				itemlist.add(i.getId());
			List<ExamItem> items = enew.getItemList();
			for (ExamItem j : items)
				itemlist.add(j.getId());
			boolean judge = judgeItemHaveParentChildConflit(itemlist, hospitalId);
			if(judge)
				returnPacks.add(e);
		}
		if(returnPacks.isEmpty()){
			ExamItemPackage p = createParentExamItemPackage(crmClient, mealId);
			if(p!=null)
				returnPacks.add(p);
			ExamItemPackage c = createChildExamItemPackage(crmClient, mealId);
			if(c!=null)
				returnPacks.add(c);
		}
		return returnPacks;
	}
	
	
	/**
	 * 获取互斥关系的单项包列表
	 * @param hospitalId
	 * @param mealId
	 * @return
	 * @throws SqlException
	 */
	public static List<ExamItemPackage> getMutexPackageList(int hospitalId,int mealId,MyHttpClient crmClient) throws SqlException{
		List<ExamItemPackage> returnPacks = new ArrayList<ExamItemPackage>();
		List<Integer> itemlist = new ArrayList<Integer>();
		Meal meal = getMealInfo(mealId);
		// 获取与套餐符合的所有单项包
		List<ExamItemPackage> ep = getAvaiablePackages(hospitalId, meal.getGender());

		// 逐一排除与套餐有冲突的单项包
		for (ExamItemPackage e : ep) {
			log.debug("单项包..." + e.getName() + "...id..." + e.getId());
			ExamItemPackage enew = getPackageInfo(e.getId());
			itemlist.clear();
			// 拼装单项列表,先套餐,后单项包
			List<MealItem> mealItems = getMealInnerItemList(mealId);
			for (MealItem i : mealItems)
				itemlist.add(i.getId());
			List<ExamItem> items = enew.getItemList();
			for (ExamItem j : items)
				itemlist.add(j.getId());
			boolean judge = judgeItemHaveMutexConflit(itemlist, hospitalId);
			if(judge)
				returnPacks.add(e);
		}
		if(returnPacks.isEmpty()){
			ExamItemPackage p = createConflitExamItemPackage(crmClient, mealId);
			if(p!=null)
				returnPacks.add(p);
			}
		
		return returnPacks;
	}

	/**
	 * 获取体检中心对用户可见且可用的单项包
	 * 
	 * @throws SqlException
	 */
	public static List<ExamItemPackage> getAvaiablePackages(int hospitalId, int gender) throws SqlException {
		String sqlStr = "SELECT * FROM tb_examitem_package where is_show = 1 and disable = 0 and gender in (2,?) and  price > 0  and hospital_id = ? order by id";
		List<ExamItemPackage> packagelists = new ArrayList<ExamItemPackage>();
		List<Map<String, Object>> list = DBMapper.query(sqlStr, gender, hospitalId);
		for (Map<String, Object> m : list) {
			ExamItemPackage pack = new ExamItemPackage();
			int id = Integer.parseInt(m.get("id").toString());
			pack.setId(id);
			pack.setName(m.get("name").toString());
			pack.setType(Integer.parseInt(m.get("type").toString()));
			pack.setHospitalId(Integer.parseInt(m.get("hospital_id").toString()));
			pack.setGender(Integer.parseInt(m.get("gender").toString()));
			pack.setPrice(Integer.parseInt(m.get("price").toString()));
			if (m.get("display_price") != null)
				pack.setDisplayPrice(Integer.parseInt(m.get("display_price").toString()));
			pack.setInitPrice(Integer.parseInt(m.get("init_price").toString()));
			pack.setAdjustPrice(Integer.parseInt(m.get("adjust_price").toString()));
			pack.setIsShow(m.get("is_show").toString().equals("1") ? true : false);
			pack.setDisable(Integer.parseInt(m.get("disable").toString()));
			pack.setSequence(Integer.parseInt(m.get("sequence").toString()));
			int num = Integer.parseInt(m.get("show_init_price").toString());
			if (num == 1)
				pack.setShowInitPrice(true);
			else
				pack.setShowInitPrice(false);
			String sqlStr2 = "SELECT e.id ,e.name,e.description,e.is_discount as discount ,e.enable_custom as enableCustom, e.hospital_id AS hospitalId, "
					+ " e.price, e.group_id as groupId , e.type ,e.his_item_id as hisItemId ,e.gender as gender,e.pinyin as pinyin  "
					+ "FROM tb_examitem  e WHERE e.id in (SELECT item_id FROM tb_examitem_package_item where package_id = ? )";
			list = DBMapper.query(sqlStr2,id);
			List<ExamItem> examitems = new ArrayList<ExamItem>();
			for (Map<String, Object> m2 : list) {
				JSONObject js = JSONObject.fromObject(m2);
				ExamItem ei = JSON.parseObject(js.toString(), ExamItem.class);
				examitems.add(ei);
			}
			pack.setItemList(examitems);
			packagelists.add(pack);
		}
		return packagelists;
	}

	/**
	 * 获取基础加项包
	 * 
	 * @param hospitalId
	 * @param gender
	 * @return
	 * @throws SqlException
	 */
	public static List<Meal> getBasePackageList(int hospitalId, int gender) throws SqlException {
		String sqlStr = "SELECT * FROM tb_meal where type = 4 and disable = 0 and gender = ? and  price > 0 and init_price > 0  and hospital_id = ? order by id";
		List<Meal> packagelists = new ArrayList<Meal>();
		List<Map<String, Object>> list = DBMapper.query(sqlStr, gender, hospitalId);
		if (list != null && list.size() > 0) {
			for (Map<String, Object> m : list) {
				Meal pack = new Meal();
				pack.setId(Integer.parseInt(m.get("id").toString()));
				pack.setType(Integer.parseInt(m.get("type").toString()));
				pack.setHospitalId(Integer.parseInt(m.get("hospital_id").toString()));
				pack.setGender(Integer.parseInt(m.get("gender").toString()));
				pack.setPrice(Integer.parseInt(m.get("price").toString()));
				pack.setDisable(Integer.parseInt(m.get("disable").toString()));
				packagelists.add(pack);
			}
			return packagelists;
		}
		return null;
	}



	/**
	 * 根据单项包ID查看包信息
	 * 
	 * @param package_id
	 * @return
	 * @throws SqlException
	 */
	public static ExamItemPackage getPackageInfo(int package_id) throws SqlException {
		String sqlStr = "SELECT * FROM tb_examitem_package where is_show = 1 and disable = 0 and  id = ?";
		List<Map<String, Object>> list = DBMapper.query(sqlStr, package_id);
		Map<String, Object> m = list.get(0);
		ExamItemPackage pack = new ExamItemPackage();
		pack.setId(Integer.parseInt(m.get("id").toString()));
		pack.setName(m.get("name").toString());
		pack.setType(Integer.parseInt(m.get("type").toString()));
		pack.setHospitalId(Integer.parseInt(m.get("hospital_id").toString()));
		pack.setGender(Integer.parseInt(m.get("gender").toString()));
		pack.setPrice(Integer.parseInt(m.get("price").toString()));
		if (m.get("sequence") != null)
			pack.setSequence(Integer.parseInt(m.get("sequence").toString()));
		if (m.get("display_price") != null)
			pack.setDisplayPrice(Integer.parseInt(m.get("display_price").toString()));
		pack.setInitPrice(Integer.parseInt(m.get("init_price").toString()));
		pack.setAdjustPrice(Integer.parseInt(m.get("adjust_price").toString()));
		pack.setIsShow(m.get("is_show").toString().equals("1") ? true : false);
		pack.setDisable(Integer.parseInt(m.get("disable").toString()));
		String sqlStr2 = "SELECT e.id ,e.name,e.description,e.is_discount as discount ,e.enable_custom as enableCustom, e.hospital_id AS hospitalId, "
				+ " e.price, e.group_id as groupId , e.type ,e.his_item_id as hisItemId ,e.gender as gender,e.pinyin as pinyin  "
				+ "FROM tb_examitem  e WHERE e.id in (SELECT item_id FROM tb_examitem_package_item where package_id = ? )";
		list = DBMapper.query(sqlStr2, package_id);
		List<ExamItem> examitems = new ArrayList<ExamItem>();
		for (Map<String, Object> m2 : list) {
			JSONObject js = JSONObject.fromObject(m2);
			ExamItem ei = JSON.parseObject(js.toString(), ExamItem.class);
			examitems.add(ei);
		}
		pack.setItemList(examitems);
		return pack;

	}

	/**
	 * 获取多个单项的折扣后价格,用于套餐详情的增加项合计
	 * 
	 * @return
	 */
	public static int getExamListDiscountPrice(List<ExamItem> elist, int hospitalId, double discount,int adjustPrice) {
		double doublePrice = 0;
		for (ExamItem e : elist) {
			doublePrice += dealExamDiscount(hospitalId, e, discount);
		}
		return HospitalChecker.calculator_data(hospitalId, (int) doublePrice - adjustPrice);
	}


	/**
	 * 处理有his编码的单项，换算折扣 非合并项
	 *
	 * @param e
	 * @param discount
	 * @return
	 */
	public static double dealExamDiscount(int hospitalId, ExamItem e, double discount) {
		double retExamDiscountPrice = 0;
		if(e.getHisItemId()!=null){//普通项目
			// 不打折单项
			if (!e.isDiscount())
				retExamDiscountPrice =  HospitalChecker.calculator_data(hospitalId, e.getPrice());
			else {// 打折单项
				BigDecimal itemD = new BigDecimal(e.getPrice());
				BigDecimal discountD = new BigDecimal(discount);
				double price = itemD.multiply(discountD).doubleValue();
				retExamDiscountPrice =  HospitalChecker.calculator_data(hospitalId, (int)Math.ceil(price));
			}
		}else{//合并项目
			try {
				List<Integer> relationItemList =  ItemRelationFunction.getComposeItemId(e.getId(), ExamItemRelationEnum.COMPOSE.getCode());
				if(relationItemList!=null && relationItemList.size()>0)
					for(Integer a : relationItemList){
						List<ExamItem> examItem = getExamItemsBySelected(Arrays.asList(a));
						// 不打折单项
						if (!examItem.get(0).isDiscount())
							retExamDiscountPrice += HospitalChecker.calculator_data(hospitalId, examItem.get(0).getPrice());
						else {// 打折单项
							BigDecimal itemD = new BigDecimal(examItem.get(0).getPrice());
							BigDecimal discountD = new BigDecimal(discount);
							double price = itemD.multiply(discountD).doubleValue();
							retExamDiscountPrice += HospitalChecker.calculator_data(hospitalId, (int)Math.ceil(price));
						}
					}
			} catch (SqlException e1) {
				e1.printStackTrace();
			}
		}
		return retExamDiscountPrice;

	}

	/**
	 * 获取单项包的折后价(C端显示按照套餐折后价格显示)
	 * 
	 * @param packId
	 * @param discount
	 * @throws SqlException
	 */
	public static int getPackDiscountPrice(int hospitalId, int packId, double discount) throws SqlException {
		double nowdPrice = 0;
		ExamItemPackage epck = getPackageInfo(packId);
		// 1.获取包单项列表，单项打散打折，同时处理不打折单项
		List<ExamItem> elist = epck.getItemList();
		for (ExamItem e : elist) {
			// 判定是否是合并大项
			if (e.getHisItemId() == null) {
				List<Integer> composeItems = ItemRelationFunction.getComposeItemId(e.getId(), hospitalId);
				if (composeItems == null)
					continue; // 合并大项没有建立小项目,忽略
				List<ExamItem> composeItemLists = ItemRelationFunction.getExamItemsBySelected(composeItems);
				for (ExamItem e1 : composeItemLists)
					nowdPrice += dealExamDiscount(hospitalId, e1, discount);
			} else {
				nowdPrice += dealExamDiscount(hospitalId, e, discount);
			}

			log.debug("add item " + e.getId() + "是否打折" + e.isDiscount() + "..nowdPrice..." + nowdPrice);
		}
		// 2.获取包调整金额,调整金额打折
		int pAdjust = epck.getAdjustPrice();
		int pAdjustDiscount = HospitalChecker.calculator_data(hospitalId, (int) (pAdjust * discount));
		log.debug("包折后调整金额..." + pAdjustDiscount);
		// 3.返回（所有打折单项 *折扣 + 不打折单项+ 调整金额*折扣）
		return HospitalChecker.calculator_data(hospitalId, (int) (nowdPrice - pAdjustDiscount));
	}

	
	

	/**
	 * 返回单项的id列表
	 * 
	 * @param examlist
	 * @return
	 */
	public static List<Integer> getExamIdList(List<ExamItem> examlist) {
		List<Integer> list = new ArrayList<Integer>();
		for (ExamItem e : examlist) {
			list.add(e.getId());
		}
		return list;
	}
	
	/**
	 * 返回单项的id列表
	 * 
	 * @param examlist
	 * @return
	 */
	public static List<Integer> getMealItemIdList(List<MealItem> examlist) {
		List<Integer> list = new ArrayList<Integer>();
		for (MealItem e : examlist) {
			list.add(e.getId());
		}
		return list;
	}

	/***
	 * 从单项包中清除所有需要删除的单项
	 * 
	 * @param packlists
	 *            单项包列表
	 * @param dels
	 *            删除单项列表
	 * @return
	 */
	public static List<ExamItemPackage> getOptimizationPackgeList(List<ExamItemPackage> packlists, List<Integer> dels) {
		// 判断有没有删除项目
		for (ExamItemPackage pack : packlists) {
			List<ExamItem> pexamLists = pack.getItemList();
			List<Integer> pexamIdLists = getExamIdList(pexamLists);
			List<Integer> needToDelIdLists = new ArrayList<Integer>();
			for (Integer d : dels) {
				if (pexamIdLists.contains(d)) {
					needToDelIdLists.add(d);
					ListUtil.ListRemoveObj(pexamIdLists, d);
				}
			}
			// 有单项被删除
			if (pexamLists.size() > pexamIdLists.size()) {
				Iterator<ExamItem> it = pexamLists.iterator();
				while (it.hasNext()) {
					ExamItem e = it.next();
					if (needToDelIdLists.contains(e.getId())) {
						log.info("包" + pack.getId() + "删除单项.." + e.getId());
						it.remove();
					}
				}
			}

			Collections.sort(pexamLists, new Comparator<ExamItem>() {
				@Override
				public int compare(ExamItem o1, ExamItem o2) {
					return o1.getId() - o2.getId();
				}
			});
		}
		return packlists;
	}

	
	/**
	 * 验证是否有同组冲突
	 * @param itemlist
	 * @param hospitalId
	 * @return
	 * @throws SqlException 
	 */
	public static boolean judgeItemsHaveGroupConflit(List<Integer> itemlist, int hospitalId) throws SqlException{
		List<Map<Integer, List<Integer>>> allItemRelationLists = new ArrayList<Map<Integer, List<Integer>>>();
		// 1.同组关系
		for (Integer i : itemlist) {
			Map<Integer, List<Integer>> map = new HashMap<Integer, List<Integer>>();
			// 获取同组项
			List<Integer> groupItemIds = ItemRelationFunction.queryGroupItems(i, hospitalId);
			map.put(i, groupItemIds);
			allItemRelationLists.add(map);
		}

		// 检查传入的单项列表是否在同组项
		Iterator<Integer> it = itemlist.iterator();
		while (it.hasNext()) {
			Integer i = (Integer) it.next();
			for (Map<Integer, List<Integer>> m : allItemRelationLists) {
				// 同组
				Set<Integer> set = m.keySet();
				for (Integer s : set) {
					if (s == i)
						continue;
					if (m.get(s).contains(i)) {
						if (checkExamItem(s).getPrice() > checkExamItem(i).getPrice()) {// 检查价格是否为低价
							return true;
						}
					}
				}
			}
		}
		log.debug("当前的itemList..." + itemlist);
		log.debug("同组关系..." + allItemRelationLists);
		return false;
	
	}
	
	/**
	 * 验证是否有合并关系
	 * @param itemlist
	 * @param hospitalId
	 * @return
	 * @throws SqlException
	 */
	public static boolean judgeItemsHaveComposeConflit(List<Integer> itemlist, int hospitalId) throws SqlException{
		List<Map<Integer, List<Integer>>> allItemRelationLists = new ArrayList<Map<Integer, List<Integer>>>();

		for (Integer i : itemlist) {
			Map<Integer, List<Integer>> map = new HashMap<Integer, List<Integer>>();
			// 获取合并小项
			List<Integer> composeItemIds = ItemRelationFunction.getComposeItemId(i, hospitalId);
			map.put(i, composeItemIds);
			allItemRelationLists.add(map);
		}
		log.debug("合并关系..." + allItemRelationLists);
		// 检查传入的单项列表是否在合并小项
		Iterator<Integer> it = itemlist.iterator();
		while (it.hasNext()) {
			Integer i = (Integer) it.next();
			for (Map<Integer, List<Integer>> m : allItemRelationLists) {
				Set<Integer> set = m.keySet();
				for (Integer s : set) {
					if (s == i)
						continue;
					if (m.get(s) == null)
						continue;
					else if (m.get(s).contains(i)) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	/**
	 * 验证是否有父子关系冲突
	 * @param itemlist
	 * @param hospitalId
	 * @return
	 * @throws SqlException
	 */
	public static boolean judgeItemHaveParentChildConflit(List<Integer> itemlist, int hospitalId) throws SqlException{
		List<Map<Integer, List<Integer>>> allItemRelationLists = new ArrayList<Map<Integer, List<Integer>>>();
		// 3.父子关系
		for (Integer i : itemlist) {
					Map<Integer, List<Integer>> map = new HashMap<Integer, List<Integer>>();
					// 获取父子小项
					List<Integer> childItemIds = ItemRelationFunction.getChildItems(i, hospitalId);
					map.put(i, childItemIds);
					allItemRelationLists.add(map);
				}
				log.debug("父子关系..." + allItemRelationLists);
				// 检查传入的单项列表是否在父子小项
				Iterator<Integer>it = itemlist.iterator();
				while (it.hasNext()) {
					Integer i = (Integer) it.next();
					for (Map<Integer, List<Integer>> m : allItemRelationLists) {
						Set<Integer> set = m.keySet();
						for (Integer s : set) {
							if (s == i)
								continue;
							if (m.get(s).contains(i)) {
								return true;
							}
						}
					}
				}
			return false;

	}
	
	/**
	 * 验证是否有互斥关系
	 * @param itemlist
	 * @param hospitalId
	 * @return
	 * @throws SqlException
	 */
	public static boolean judgeItemHaveMutexConflit(List<Integer> itemlist, int hospitalId) throws SqlException{
		List<Map<Integer, List<Integer>>> allItemRelationLists = new ArrayList<Map<Integer, List<Integer>>>();
		for (Integer i : itemlist) {
			Map<Integer, List<Integer>> map = new HashMap<Integer, List<Integer>>();
			// 获取互斥
			List<Integer> conflictItemIds = ItemRelationFunction.getConflictItemId(i, hospitalId);
			map.put(i, conflictItemIds);
			allItemRelationLists.add(map);
		}
		log.debug("互斥关系..." + allItemRelationLists);
		// 检查传入的单项列表是否在互斥项
		Iterator<Integer> it = itemlist.iterator();
		while (it.hasNext()) {
			Integer i = (Integer) it.next();
			for (Map<Integer, List<Integer>> m : allItemRelationLists) {
				Set<Integer> set = m.keySet();
				for (Integer s : set) {
					if (s == i)
						continue;
					if (m.get(s) == null)
						continue;
					else if (m.get(s).contains(i)) {// 互斥关系
						log.debug("s.." + s + "排序" + getListIndex(s, itemlist) + "..i.." + i + "排序"
								+ getListIndex(i, itemlist));
						if (getListIndex(s, itemlist) < getListIndex(i, itemlist)) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}	
	/**
	 * 判断单项列表内部是否有冲突，有则返回true，无则返回false
	 * 
	 * @param itemlist
	 * @param hospitalId
	 * @return
	 * @throws SqlException
	 */
	public static boolean judgeItemsHaveConflict(List<Integer> itemlist, int hospitalId) throws SqlException {
		//1.同组关系
		boolean groupConflit = judgeItemsHaveGroupConflit(itemlist,hospitalId);
		if(groupConflit)
			return true;
		
		// 2.合并关系
		boolean composeConflit = judgeItemsHaveComposeConflit(itemlist,hospitalId);
		if(composeConflit)
			return true;
		
		//3.父子关系
		boolean parentConflit = judgeItemHaveParentChildConflit(itemlist,hospitalId);
		if(parentConflit)
			return true;
		
		//4.互斥关系
		boolean mutexConflit = judgeItemHaveMutexConflit(itemlist,hospitalId);
		if(mutexConflit)
			return true;
		return false;
		
	}
	
	/**
	 * 判断单项列表是否有重复项目
	 * @param itemlist
	 * @param hospitalId
	 * @return
	 * @throws SqlException
	 */
	public static boolean judgeItemsHaveDuplicateItem(List<Integer> itemlist, int hospitalId) throws SqlException {
		 Map<Integer, Integer> duplicateMap = getDuplicateItemMap(itemlist);
		 if(duplicateMap != null && duplicateMap.size()>0)
			 return true;
		  return false;
	}

	/**
	 * 获取单项中重复项
	 * DB验证
	 * @param itemlist
	 * @param hospitalId
	 * @return
	 */
	public static Map<Integer, Integer> getDuplicateItemMap(List<Integer> itemlist) {
		return ListUtil.getSameDataMap(itemlist);
	}

	/**
	 * 全部单项id里面，计算出因为同组/父子/合并/互斥关系需要被去除的单项id列表 互斥保留单项列表前面的元素，删除后面的元素
	 * 
	 * DB验证
	 * @param list
	 * @throws SqlException
	 */
	public static List<Integer> getDeleteItemList(List<Integer> itemlist, int hospitalId) throws SqlException {
		log.debug("传入参数" + itemlist);
		// 获取互斥关系
		List<Integer> deleteItems = new ArrayList<Integer>();
		List<Map<Integer, List<Integer>>> allItemRelationLists = new ArrayList<Map<Integer, List<Integer>>>();
		// 1.同组关系
		for (Integer i : itemlist) {
			Map<Integer, List<Integer>> map = new HashMap<Integer, List<Integer>>();
			// 获取同组项
			List<Integer> groupItemIds = ItemRelationFunction.queryGroupItems(i, hospitalId);
			map.put(i, groupItemIds);
			allItemRelationLists.add(map);
		}
		// 检查传入的单项列表是否在同组项
		Iterator<Integer> it = itemlist.iterator();
		while (it.hasNext()) {
			Integer i = (Integer) it.next();
			for (Map<Integer, List<Integer>> m : allItemRelationLists) {
				// 同组
				Set<Integer> set = m.keySet();
				for (Integer s : set) {
					if (s == i)
						continue;
					if (m.get(s).contains(i)) {
						if (checkExamItem(s).getPrice() > checkExamItem(i).getPrice()) {// 检查价格是否为低价(价格相等需要验证)
							deleteItems.add(i);
							log.info("因为选择单项/单项包，同组关系删除元素:..." + i);
							it.remove();
							set.remove(i);
						}
					}
				}
			}
		}
		log.debug("当前的itemList..." + itemlist);
		log.debug("同组关系..." + allItemRelationLists);
		allItemRelationLists.clear(); // 清除数据
		// 2.合并关系
		for (Integer i : itemlist) {
			Map<Integer, List<Integer>> map = new HashMap<Integer, List<Integer>>();
			// 获取合并小项
			List<Integer> composeItemIds = ItemRelationFunction.getComposeItemId(i, hospitalId);
			map.put(i, composeItemIds);
			allItemRelationLists.add(map);
		}
		log.debug("合并关系..." + allItemRelationLists);
		// 检查传入的单项列表是否在合并小项
		it = itemlist.iterator();
		while (it.hasNext()) {
			Integer i = (Integer) it.next();
			for (Map<Integer, List<Integer>> m : allItemRelationLists) {
				Set<Integer> set = m.keySet();
				for (Integer s : set) {
					if (s == i)
						continue;
					if (m.get(s) == null)
						continue;
					else if (m.get(s).contains(i)) {
						log.info("因为选择单项/单项包，合并关系删除合并小项:..." + i);
						deleteItems.add(i);
						it.remove();
					}
				}
			}
		}
		allItemRelationLists.clear(); // 清除数据
		// 3.父子关系
		for (Integer i : itemlist) {
			Map<Integer, List<Integer>> map = new HashMap<Integer, List<Integer>>();
			// 获取父子小项
			List<Integer> childItemIds = ItemRelationFunction.getChildItems(i, hospitalId);
			map.put(i, childItemIds);
			allItemRelationLists.add(map);
		}
		log.debug("父子关系..." + allItemRelationLists);
		// 检查传入的单项列表是否在父子小项
		it = itemlist.iterator();
		while (it.hasNext()) {
			Integer i = (Integer) it.next();
			for (Map<Integer, List<Integer>> m : allItemRelationLists) {
				Set<Integer> set = m.keySet();
				for (Integer s : set) {
					if (s == i)
						continue;
					if (m.get(s).contains(i)) {
						deleteItems.add(i);
						log.info("因为选择单项/单项包，父子关系删除子项目:..." + i);
						it.remove();
					}
				}
			}
		}

		allItemRelationLists.clear(); // 清除数据
		// 4.互斥关系
		for (Integer i : itemlist) {
			Map<Integer, List<Integer>> map = new HashMap<Integer, List<Integer>>();
			// 获取互斥
			List<Integer> conflictItemIds = ItemRelationFunction.getConflictItemId(i, hospitalId);
			map.put(i, conflictItemIds);
			allItemRelationLists.add(map);
		}
		log.debug("互斥关系..." + allItemRelationLists);
		// 检查传入的单项列表是否在互斥项
		it = itemlist.iterator();
		while (it.hasNext()) {
			Integer i = (Integer) it.next();
			for (Map<Integer, List<Integer>> m : allItemRelationLists) {
				Set<Integer> set = m.keySet();
				for (Integer s : set) {
					if (s == i)
						continue;
					if (m.get(s) == null)
						continue;
					else if (m.get(s).contains(i)) {// 互斥关系
						log.debug("s.." + s + "排序" + getListIndex(s, itemlist) + "..i.." + i + "排序"
								+ getListIndex(i, itemlist));
						if (getListIndex(s, itemlist) < getListIndex(i, itemlist)) {
							deleteItems.add(i);
							log.info("因为选择单项/单项包，互斥关系删除项目:..." + i);
							it.remove();
						}
					}
				}
			}
		}
		return deleteItems;
	}

	/**
	 * 获取单项在列表中的索引
	 * 
	 * @return
	 */
	private static int getListIndex(Integer i, List<Integer> itemLists) {
		for (int x = 0; x < itemLists.size(); x++) {
			if (itemLists.get(x) == i)
				return x;
		}
		return -1;
	}

	
	
	public static List<ExamItem> getFinalItems(List<ExamItem> itemsInMeal,List<ExamItem> addedItem,List<ExamItem> reducedItems){
		List<ExamItem> finalItems = new ArrayList<ExamItem>();
		finalItems.addAll(itemsInMeal);
	    finalItems.addAll(addedItem);
	    List<Integer> reducedItemIds = reducedItems.stream().map(i->i.getId()).collect(Collectors.toList());
	    for(int i=0;i<finalItems.size();i++){
	    	ExamItem e = finalItems.get(i);
	    	if(reducedItemIds.contains(e.getId())){	    		
	    		System.out.println("减去的项目为："+e.getId());
	    		finalItems.remove(i);
	    	}
	    }
	    
	    /*for(ExamItem e : reducedItems){
	    	System.out.println("减去的项目为："+e.getId());
	    	finalItems.remove(e);
	    }*/
		return finalItems;
	}
	
	public static List<ExamItem> getFinalItems(List<ExamItem> finalItemsInMeal,List<ExamItem> addedItem){
		List<ExamItem> finalItems = new ArrayList<ExamItem>();
		finalItems.addAll(finalItemsInMeal);
		if(addedItem.size()>0&&addedItem!=null){
			finalItems.addAll(addedItem);
		}
		return new ArrayList<ExamItem>(new HashSet<ExamItem>(finalItems));
	}
	
	/**
	 * 验证itemDetail中的项目(DB)
	 * @param itemSnapshot
	 * @param finalItemsInMeal
	 * @param reducedItems
	 * @param addedItem
	 * @param meal
	 */
	public static void checkItemByTypeFromMysql(List<ExamItemSnapshot> itemSnapshot
			,List<ExamItem> finalItemsInMeal
			,List<ExamItem> reducedItems
			,List<ExamItem> addedItem
			,List<ExamItem> duplicateItems
			,Meal meal){
		System.out.println("-------------------------验证数据库中单项信息正确Start----------------------------");
//		Double mealDiscount = meal.getDiscount();
		Double mealItemPrice = 0.0;
		finalItemsInMeal = finalItemsInMeal.stream().sorted(Comparator.comparing(ExamItem::getId)).collect(Collectors.toList());
		List<ExamItem> reducedItems1 = reducedItems.stream().sorted(Comparator.comparing(ExamItem::getId)).collect(Collectors.toList());
		List<Integer> reducedItemIds = reducedItems.stream().map(i->i.getId()).collect(Collectors.toList());
		addedItem = addedItem.stream().sorted(Comparator.comparing(ExamItem::getId)).collect(Collectors.toList());
		//typeToMeal=1
    	List<ExamItemSnapshot> DBitemsInMeal = itemSnapshot.stream()
    			.filter(item->item.getTypeToMeal()==1)
    			.collect(Collectors.toList());
    	DBitemsInMeal.forEach(item->{
    		System.out.println("套餐内项目为："+item.getId());
    	});
    	//typeToMeal=2
    	List<ExamItemSnapshot> DBitemsNotInMeal = itemSnapshot.stream()
    			.filter(item->item.getTypeToMeal()==2)
    			.collect(Collectors.toList());
    	DBitemsNotInMeal.forEach(item->{
    		System.out.println("套餐内删除项目为："+item.getId());
    		if(!reducedItemIds.contains(item.getId())){
    			reducedItemIds.add(item.getId());
    			ExamItem e = ResourceChecker.checkExamItem(item.getId());
    			reducedItems1.add(e);
    		}
    	});
    	//typeToMeal=3
    	List<ExamItemSnapshot> DBitemsOutMeal = itemSnapshot.stream()
    			.filter(item->item.getTypeToMeal()==3)
    			.collect(Collectors.toList());
    	DBitemsNotInMeal.forEach(item->{
    		System.out.println("套餐外增加项目为："+item.getId());
    	});
    	//typeToPackage=1
    	List<ExamItemSnapshot> DBDupliItem = itemSnapshot.stream()
    			.filter(item->item.getTypeToPackage()!=null)
    			.filter(item->item.getTypeToPackage()==1)
    			.collect(Collectors.toList());
    	DBDupliItem.forEach(item->{
//    		System.out.println("加项包重复项目为："+item.getId());
    	});
    	
		for(int i=0;i<finalItemsInMeal.size();i++){
	    	ExamItem e = finalItemsInMeal.get(i);
	    	if(reducedItemIds.contains(e.getId())){
	    		finalItemsInMeal.remove(i);
	    	}
	    }
    	
//		List<Integer> DBitemsInMealIds = DBitemsInMeal.stream().map(m->m.getId()).collect(Collectors.toList());
//    	List<Integer> finalItemsInMealIds = finalItemsInMeal.stream().map(m->m.getId()).collect(Collectors.toList());
//    	System.out.println("DBitemsInMeal:"+JSON.toJSONString(DBitemsInMealIds)+" \nfinalItemsInMeal:"+JSON.toJSONString(finalItemsInMealIds));
		
    	Assert.assertEquals(DBitemsInMeal.size(), finalItemsInMeal.size());
    	for(int i=0;i<DBitemsInMeal.size();i++){
    		ExamItem item = finalItemsInMeal.get(i);
    		Assert.assertEquals(DBitemsInMeal.get(i).getId(), item.getId());
    		if(item.isDiscount()){
    			int mealItemIntPrice = HospitalChecker.calculator_data(meal.getHospitalId(),(int)(item.getPrice()*meal.getDiscount()));
    			Assert.assertEquals(DBitemsInMeal.get(i).getPrice().intValue(),mealItemIntPrice);
    		}
    	}
    	
    	if(reducedItems!=null){    		
    		Assert.assertEquals(DBitemsNotInMeal.size(), reducedItems1.size());
    		for(int i=0;i<DBitemsNotInMeal.size();i++){
    			Assert.assertEquals(DBitemsNotInMeal.get(i).getId(), reducedItems1.get(i).getId());
    		}
    	}
    	if(addedItem!=null){		
    		Assert.assertEquals(DBitemsOutMeal.size(), addedItem.size());
    		for(int i=0;i<DBitemsOutMeal.size();i++){
    			ExamItem item = addedItem.get(i);
    			Assert.assertEquals(DBitemsOutMeal.get(i).getId(), item.getId());
    			if(item.isDiscount()){    			
    				mealItemPrice=item.getPrice()*meal.getDiscount();
    				int nowPrice=HospitalChecker.calculator_data(item.getHospitalId(), mealItemPrice.intValue());
    				Assert.assertEquals(DBitemsOutMeal.get(i).getPrice().intValue(),nowPrice);
    			}
    		}
    	}
    	if(duplicateItems!=null){    		
    		Assert.assertEquals(DBDupliItem.size(), duplicateItems.size());
    		for(int i=0;i<DBDupliItem.size();i++){
    			ExamItem item = duplicateItems.get(i);
    			Assert.assertEquals(DBDupliItem.get(i).getId(), item.getId());
    		}
    	}
    	System.out.println("-------------------------验证数据库中单项信息正确End----------------------------");
	}
	

	public static void checkItemByTypeFromMysql(List<ExamItemSnapshot> itemSnapshot,ItemsInOrder itemsInOrder,Meal meal){
		System.out.println("-------------------------验证数据库中单项信息正确Start----------------------------");
//		Double mealDiscount = meal.getDiscount();
		Double mealItemPrice = 0.0;
		itemsInOrder.setFinalItemsInMeal(itemsInOrder.getFinalItemsInMeal().stream().sorted(Comparator.comparing(ExamItem::getId)).collect(Collectors.toList()));
		//finalItemsInMeal = finalItemsInMeal.stream().sorted(Comparator.comparing(ExamItem::getId)).collect(Collectors.toList());
		itemsInOrder.setReducedItems(itemsInOrder.getReducedItems().stream().sorted(Comparator.comparing(ExamItem::getId)).collect(Collectors.toList()));
		//reducedItems = reducedItems.stream().sorted(Comparator.comparing(ExamItem::getId)).collect(Collectors.toList());
		itemsInOrder.setAddedItem(itemsInOrder.getAddedItem().stream().sorted(Comparator.comparing(ExamItem::getId)).collect(Collectors.toList()));
		//addedItem = addedItem.stream().sorted(Comparator.comparing(ExamItem::getId)).collect(Collectors.toList());
		//typeToMeal=1
		System.err.println("itemSnapshot:"+JSON.toJSONString(itemSnapshot));
    	List<ExamItemSnapshot> DBitemsInMeal = itemSnapshot.stream()
    			.filter(item->item.getTypeToMeal()==1)
    			.collect(Collectors.toList());
    	//typeToMeal=2
    	List<ExamItemSnapshot> DBitemsNotInMeal = itemSnapshot.stream()
    			.filter(item->item.getTypeToMeal()==2)
    			.collect(Collectors.toList());
    	//typeToMeal=3
    	List<ExamItemSnapshot> DBitemsOutMeal = itemSnapshot.stream()
    			.filter(item->item.getTypeToMeal()==3)
    			.collect(Collectors.toList());
    	//typeToPackage=1
    	List<ExamItemSnapshot> DBDupliItem = itemSnapshot.stream()
    			.filter(item->item.getTypeToPackage()!=null)
    			.filter(item->item.getTypeToPackage()==1)
    			.collect(Collectors.toList());
    	    	
    	Assert.assertEquals(DBitemsInMeal.size(), itemsInOrder.getFinalItemsInMeal().size());
    	for(int i=0;i<DBitemsInMeal.size();i++){
    		ExamItem item = itemsInOrder.getFinalItemsInMeal().get(i);
    		Assert.assertEquals(DBitemsInMeal.get(i).getId(), item.getId());
    		if(item.isDiscount()){
				mealItemPrice=item.getPrice()*meal.getDiscount();
				int newMealItemPrice = HospitalChecker.calculator_data(meal.getHospitalId(),mealItemPrice.intValue());
    			Assert.assertEquals(DBitemsInMeal.get(i).getPrice().intValue(),newMealItemPrice,item.getId()+" "+ item.getName());
    		}
    	}
    	
    	if(itemsInOrder.getReducedItems()!=null){    		
    		Assert.assertEquals(DBitemsNotInMeal.size(), itemsInOrder.getReducedItems().size());
    		for(int i=0;i<DBitemsNotInMeal.size();i++){
    			Assert.assertEquals(DBitemsNotInMeal.get(i).getId(), itemsInOrder.getReducedItems().get(i).getId());
    		}
    	}
    	if(itemsInOrder.getAddedItem()!=null){		
    		Assert.assertEquals(DBitemsOutMeal.size(), itemsInOrder.getAddedItem().size());
    		for(int i=0;i<DBitemsOutMeal.size();i++){
    			ExamItem item = itemsInOrder.getAddedItem().get(i);
    			Assert.assertEquals(DBitemsOutMeal.get(i).getId(), item.getId());
    			if(item.isDiscount()){    			
    				mealItemPrice=item.getPrice()*meal.getDiscount();
					int newMealItemPrice = HospitalChecker.calculator_data(meal.getHospitalId(),mealItemPrice.intValue());
    				Assert.assertEquals(DBitemsOutMeal.get(i).getPrice().intValue(),newMealItemPrice);
    			}
    		}
    	}
    	if(itemsInOrder.getDuplicateItems()!=null){    		
    		Assert.assertEquals(DBDupliItem.size(), itemsInOrder.getDuplicateItems().size());
    		for(int i=0;i<DBDupliItem.size();i++){
    			ExamItem item = itemsInOrder.getDuplicateItems().get(i);
    			Assert.assertEquals(DBDupliItem.get(i).getId(), item.getId());
    		}
    	}
    	System.out.println("-------------------------验证数据库中单项信息正确End----------------------------");
	}
	
	/**
	 * 验证itemDetail中的项目(MONGO)
	 * @param itemDetail_ja
	 * @param finalItemsInMeal
	 * @param reducedItems
	 * @param addedItem
	 * @param meal
	 */
	public void checkItemByTypeFromMONGO(JSONArray itemDetail_ja
			,List<ExamItem> finalItemsInMeal
			,List<ExamItem> reducedItems
			,List<ExamItem> addedItem
			,Meal meal){
		System.out.println("-------------------------验证Mongo中单项信息正确Start----------------------------");
		JSONArray jaInMeal = new JSONArray();
		JSONArray jaNotMeal = new JSONArray();
		JSONArray jaAddItem = new JSONArray();
		Double mealItemPrice = 0.0;
		finalItemsInMeal = finalItemsInMeal.stream().sorted(Comparator.comparing(ExamItem::getId)).collect(Collectors.toList());
		reducedItems = reducedItems.stream().sorted(Comparator.comparing(ExamItem::getId)).collect(Collectors.toList());
		addedItem = addedItem.stream().sorted(Comparator.comparing(ExamItem::getId)).collect(Collectors.toList());

		for(int i=0;i<itemDetail_ja.size();i++){
    		JSONObject jo = JSONObject.fromObject(itemDetail_ja.get(i));
    		//typeToMeal=1
    		if(jo.get("typeToMeal").equals(1)){
    			jaInMeal.add(jo);
    		}
    		//typeToMeal=2
    		if(jo.get("typeToMeal").equals(2)){
    			jaNotMeal.add(jo);
    		}
    		//typeToMeal=3
    		if(jo.get("typeToMeal").equals(3)){
    			jaAddItem.add(jo);
    		}
    	}

    	
    	Assert.assertEquals(jaInMeal.size(), finalItemsInMeal.size());
    	Assert.assertEquals(jaNotMeal.size(), reducedItems.size());
    	Assert.assertEquals(jaAddItem.size(), addedItem.size());
    	
    	for(int i=0;i<jaInMeal.size();i++){
    		//jaInMeal.getJSONObject(i).get
    		ExamItem item = finalItemsInMeal.get(i);
    		Assert.assertEquals(jaInMeal.getJSONObject(i).get("_id"), item.getId());
    		if(item.isDiscount()){
    			mealItemPrice=item.getPrice()*meal.getDiscount();
    			Assert.assertEquals(jaInMeal.getJSONObject(i).get("price"),mealItemPrice.intValue());    			
    		}
    	}
    	for(int i=0;i<jaNotMeal.size();i++){
    		Assert.assertEquals(jaNotMeal.getJSONObject(i).get("_id"), reducedItems.get(i).getId());
    	}
    	for(int i=0;i<jaAddItem.size();i++){
    		ExamItem item = addedItem.get(i);
    		Assert.assertEquals(jaAddItem.getJSONObject(i).get("_id"), item.getId());
    		if(item.isDiscount()){    			
    			mealItemPrice=item.getPrice()*meal.getDiscount();
    			Assert.assertEquals(jaAddItem.getJSONObject(i).get("price"),mealItemPrice.intValue());
    		}
    	}
    	System.out.println("-------------------------验证Mongo中单项信息正确End----------------------------");
	}
	
	/**
	 * 验证itemDetail中的项目(MONGO)
	 * @param itemDetail_ja
	 * @param itemsInOrder
	 * @param meal
	 */
	public static void checkItemByTypeFromMONGO(JSONArray itemDetail_ja
			,ItemsInOrder itemsInOrder
			,Meal meal){
		System.out.println("-------------------------验证Mongo中单项信息正确Start----------------------------");
		JSONArray jaInMeal = new JSONArray();
		JSONArray jaNotMeal = new JSONArray();
		JSONArray jaAddItem = new JSONArray();
		Double mealItemPrice = 0.0;

		for(int i=0;i<itemDetail_ja.size();i++){
    		JSONObject jo = JSONObject.fromObject(itemDetail_ja.get(i));
    		//typeToMeal=1
    		if(jo.get("typeToMeal").equals(1)){
    			jaInMeal.add(jo);
    		}
    		//typeToMeal=2
    		if(jo.get("typeToMeal").equals(2)){
    			jaNotMeal.add(jo);
    		}
    		//typeToMeal=3
    		if(jo.get("typeToMeal").equals(3)){
    			jaAddItem.add(jo);
    		}
    	}

    	
    	Assert.assertEquals(jaInMeal.size(), itemsInOrder.getFinalItemsInMeal().size());
    	Assert.assertEquals(jaNotMeal.size(), itemsInOrder.getReducedItems().size());
    	Assert.assertEquals(jaAddItem.size(), itemsInOrder.getAddedItem().size());
    	
    	for(int i=0;i<jaInMeal.size();i++){
    		//jaInMeal.getJSONObject(i).get
    		ExamItem item = itemsInOrder.getFinalItemsInMeal().get(i);
    		Assert.assertEquals(jaInMeal.getJSONObject(i).get("id"), item.getId());
    		if(item.isDiscount()){
    			mealItemPrice=item.getPrice()*meal.getDiscount();
				int newMealItemPrice = HospitalChecker.calculator_data(meal.getHospitalId(),mealItemPrice.intValue());
    			Assert.assertEquals(jaInMeal.getJSONObject(i).get("price"),newMealItemPrice);
    		}
    	}
    	for(int i=0;i<jaNotMeal.size();i++){
    		Assert.assertEquals(jaNotMeal.getJSONObject(i).get("id"), itemsInOrder.getReducedItems().get(i).getId());
    	}
    	for(int i=0;i<jaAddItem.size();i++){
    		ExamItem item = itemsInOrder.getAddedItem().get(i);
    		Assert.assertEquals(jaAddItem.getJSONObject(i).get("id"), item.getId());
    		if(item.isDiscount()){    			
    			mealItemPrice=item.getPrice()*meal.getDiscount();
				int newMealItemPrice = HospitalChecker.calculator_data(meal.getHospitalId(),mealItemPrice.intValue());
				Assert.assertEquals(jaAddItem.getJSONObject(i).get("price"),newMealItemPrice);
    		}
    	}
    	System.out.println("-------------------------验证Mongo中单项信息正确End----------------------------");
	}
	
	
	/****************************公 共 方 法****************************************
	
	/**
	 * 创建套餐
	 * 
	 * @param hc
	 * @param companyId
	 *            单位名称
	 * @param name
	 *            套餐名字
	 * @param gender
	 *            套餐性别 0 n男性 1女性 2通用
	 * @return
	 */
	public static Meal createMeal(MyHttpClient hc, int companyId, String name, int gender,int hospitalId) {
		int basicMealId = getBasicMealId(hospitalId);

		Meal meal = getMealInfo(basicMealId);
		meal.setName(name);
		meal.setGender(gender);
		meal.setId(null);
		meal.setType(1); // 变为单位套餐
		List<MealItem> mealItemList = getMealIteminfo(basicMealId);
		
		List<ExamItem> et = getHospitalExamItems(hospitalId, gender, true);
		ExamItem addItem = et.get(0);
		// 增加尿酸
		mealItemList.add(new MealItem(addItem.getId(), basicMealId, true, false, gender, true, 1, true));
		List<MealSetting> mealSetting = getMealSettingsInfo(basicMealId);
		meal.setMealSetting(mealSetting.get(0));
		int ruleId = ExamReportChecker.getHospitalRuleId(hospitalId);
		EditMealBody mealBody = new EditMealBody(companyId, meal, mealItemList, ruleId);
		String jbody = JSON.toJSONString(mealBody);

		// post
		HttpResult response = hc.post(Meal, jbody);

		// Assert
		Assert.assertEquals(response.getCode(), HttpStatus.SC_OK,"错误提示："+response.getBody());
		log.info("新建套餐返回.."+response.getBody());
		int newMealId = JsonPath.read(response.getBody(), "$.id");
		log.debug("addMeal.newMealId:..................." + newMealId);
		Meal newMeal = getMealInfo(newMealId);
		return newMeal;
	}
	
	

	/**
	 * 创建与套餐中某个单项有互斥项的单项包
	 * @param httpclient
	 * @param mealId
	 * @return
	 */
	public static ExamItemPackage createConflitExamItemPackage(MyHttpClient httpclient,int mealId){
		Meal meal  = getMealInfo(mealId);
		//获取套餐内已勾选的单项列表
		List<MealItem> mealItemLists = getMealInnerItemList(meal.getId());
		List<ExamItem> itemList = new ArrayList<ExamItem>();
		int packagePrice = 0;
		//选套餐内1个单项,提取互斥项目
		for(MealItem item : mealItemLists){
			List<Integer> conflitIds = new ArrayList<Integer>();
			try {
				conflitIds = ItemRelationFunction.getConflictItemId(item.getId(), meal.getHospitalId());
				if(conflitIds != null && conflitIds.size()>0){
					for(Integer conflitId : conflitIds){
						ExamItem e = checkExamItem(conflitId);
						if(!e.isShow())continue;
						packagePrice = e.getPrice();
						itemList.add(e);
						break;
					}
					if(itemList.size()==1)break;

				}
			} catch (SqlException e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			}
		
		}
		if(itemList.size() == 0){
			log.error("无法创建互斥单项包，套餐内项目没有互斥项");
			return null;
		}
		
		List<Integer> ids = getNoConflitItemsWithMealId(mealId);
		itemList.add(checkExamItem(ids.get(0)));
		//设置调整金额=-0
		int displayPrice = packagePrice;
		int adjustPrice = -10;
		int price = packagePrice  - adjustPrice;
		ExamItemPackage pack = createExamItemPackage(httpclient,adjustPrice,"自动化互斥加项包",0,displayPrice,price,
				meal.getGender(),meal.getHospitalId(),packagePrice,itemList,"自动化互斥"+getRandomHan()+getRandomHan());
		return pack;
	}
	
	
	
	/**
	 * 创建与套餐中某个单项有父关系的单项包
	 * @param httpclient
	 * @param mealId
	 * @return
	 */
	public static ExamItemPackage createParentExamItemPackage(MyHttpClient httpclient,int mealId){
		Meal meal  = getMealInfo(mealId);
		//获取套餐内已勾选的单项列表
		List<MealItem> mealItemLists = getMealInnerItemList(meal.getId());
		List<ExamItem> itemList = new ArrayList<ExamItem>();
		int packagePrice = 0;
		//选套餐内1个单项,提取互斥项目
		for(MealItem item : mealItemLists){
			List<Integer>  parentIds = new ArrayList<Integer>();
			try {
				parentIds = ItemRelationFunction.getParentItemIds(item.getId(),meal.getHospitalId());
				if(parentIds.size()>0){
					for(Integer parentId : parentIds){
						ExamItem e = checkExamItem(parentId);
						if(!e.isShow())continue;
						packagePrice = e.getPrice();
						itemList.add(e);
						break;
					}
					if(itemList.size()==1)break;
				}
			} catch (SqlException e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			}
		
		}
		if(itemList.size() == 0){
			log.error("无法创建父单项包，套餐内项目没有父子项中的父项");
			return null;
		}
		
		List<Integer> ids = getNoConflitItemsWithMealId(mealId);
		itemList.add(checkExamItem(ids.get(0)));
		//设置调整金额=-0
		int displayPrice = packagePrice;
		int adjustPrice = -10;
		int price = packagePrice  - adjustPrice;
		ExamItemPackage pack = createExamItemPackage(httpclient,adjustPrice,"自动化父加项包",0,displayPrice,price
				,meal.getGender(),meal.getHospitalId(),packagePrice,itemList,"自动化父加项包"+getRandomHan()+getRandomHan());
		return pack;
	}
	
	
	
	/**
	 * 创建与套餐中某个单项有子关系的单项包
	 * @param httpclient
	 * @param mealId
	 * @return
	 */
	public static ExamItemPackage createChildExamItemPackage(MyHttpClient httpclient,int mealId){
		Meal meal  = getMealInfo(mealId);
		//获取套餐内已勾选的单项列表
		List<MealItem> mealItemLists = getMealInnerItemList(meal.getId());
		List<ExamItem> itemList = new ArrayList<ExamItem>();
		int packagePrice = 0;
		//选套餐内1个单项,提取互斥项目
		for(MealItem item : mealItemLists){
			List<Integer>  childIds = new ArrayList<Integer>();
			try {
				childIds = ItemRelationFunction.getChildItems(item.getId(),meal.getHospitalId());
				if(childIds.size()>0){
					for(Integer childId : childIds){
						ExamItem e = checkExamItem(childId);
						if(!e.isShow())continue;
						packagePrice = e.getPrice();
						itemList.add(e);
						break;
					}
					if(itemList.size()==1)break;
					
				}
			} catch (SqlException e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			}
		
		}
		if(itemList.size() == 0){
			log.error("无法创建子单项包，套餐内项目没有父子项中的子项");
			return null;
		}
		
		List<Integer> ids = getNoConflitItemsWithMealId( mealId);
		itemList.add(checkExamItem(ids.get(0)));
		//设置调整金额=0
		int displayPrice = packagePrice;
		int adjustPrice = -10;
		int price = packagePrice  - adjustPrice;
		packagePrice += adjustPrice;
		ExamItemPackage pack = createExamItemPackage(httpclient,adjustPrice,"自动化子加项包",0,displayPrice,price,meal.getGender(),
				meal.getHospitalId(),packagePrice,itemList,"自动化子加项包"+getRandomHan()+getRandomHan());
		return pack;
	}
	

	
	/**
	 * 创建与套餐中某个单项有同组关系的单项包
	 * @param httpclient
	 * @param mealId
	 * @return
	 */
	public static ExamItemPackage createGroupExamItemPackage(MyHttpClient httpclient,int mealId){
		Meal meal  = getMealInfo(mealId);
		//获取套餐内已勾选的单项列表
		List<MealItem> mealItemLists = getMealInnerItemList(meal.getId());
		List<ExamItem> itemList = new ArrayList<ExamItem>();
		int packagePrice = 0;
		//选套餐内1个单项,提取互斥项目
		for(MealItem item : mealItemLists){
			List<ExamItem>  groupsIds = new ArrayList<ExamItem>();
			try {
				groupsIds = ItemRelationFunction.queryItemsInGroupId(item.getId(),meal.getHospitalId());
				if(groupsIds.size()>0){
					for(ExamItem group : groupsIds){
						if(!group.isShow())
							continue;
						if(group.getId() == item.getId())
							continue;
						packagePrice = group.getPrice();
						itemList.add(group);
						break;
					}
					if(itemList.size()==1)break;

				}
			} catch (SqlException e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			}
		
		}
		if(itemList.size() == 0){
			log.error("无法创建同组单项包，套餐内项目没有同组项");
			return null;
		}
		
		List<Integer> ids = getNoConflitItemsWithMealId( mealId);
		itemList.add(checkExamItem(ids.get(0)));
		//设置调整金额=0
		int displayPrice = packagePrice;
		int adjustPrice = -10;
		int price = packagePrice  - adjustPrice;
		ExamItemPackage pack = createExamItemPackage(httpclient,adjustPrice,"自动化同组加项包",0,displayPrice,price
				,meal.getGender(),meal.getHospitalId(),packagePrice,itemList,"自动化同组"+getRandomHan()+getRandomHan());
		return pack;
		
	}
	
	
	/**
	 * 创建与套餐中某个单项有合并关系的单项包
	 * @param httpclient
	 * @param mealId
	 * @return
	 */
	public static ExamItemPackage createBeComposeExamItemPackage(MyHttpClient httpclient,int mealId){
		Meal meal  = getMealInfo(mealId);
		//获取套餐内已勾选的单项列表
		List<MealItem> mealItemLists = getMealInnerItemList(meal.getId());
		List<ExamItem> itemList = new ArrayList<ExamItem>();
		int packagePrice = 0;
		//选套餐内1个单项,提取互斥项目
		for(MealItem item : mealItemLists){
			List<Integer>  composeIds = new ArrayList<Integer>();
			try {
				composeIds = ItemRelationFunction.getBeComposedItemIds(item.getId(), ConflictType.COMPOSE.getCode(), meal.getHospitalId());
				if(composeIds != null && composeIds.size()>0){
					for(Integer composeId : composeIds){
						ExamItem e = checkExamItem(composeId);
						if(!e.isShow())continue;
						packagePrice = e.getPrice();
						itemList.add(e);
						break;
					}
					if(itemList.size()==1)break;

				}
			} catch (SqlException e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			}
		
		}
		if(itemList.size() == 0){
			log.error("无法创建合并单项包，套餐内项目没有合并项");
			return null;
		}
		List<Integer> ids = getNoConflitItemsWithMealId(mealId);
		itemList.add(checkExamItem(ids.get(0)));
		//设置调整金额=0
		int displayPrice = packagePrice;
		int adjustPrice = -10;
		int price = packagePrice  - adjustPrice;
		ExamItemPackage pack = createExamItemPackage(httpclient,adjustPrice,"自动化合并大项加项包",0,displayPrice,price,
				meal.getGender(),meal.getHospitalId(),packagePrice,itemList,"自动化合并大项"+getRandomHan()+getRandomHan());
		return pack;
	}
	
	/**
	 * 创建与套餐中某个单项有合并关系的单项包
	 * @param httpclient
	 * @param mealId
	 * @return
	 */
	public static ExamItemPackage createComposeExamItemPackage(MyHttpClient httpclient,int mealId){
		Meal meal  = getMealInfo(mealId);
		//获取套餐内已勾选的单项列表
		List<MealItem> mealItemLists = getMealInnerItemList(meal.getId());
		List<ExamItem> itemList = new ArrayList<ExamItem>();
		int packagePrice = 0;
		//选套餐内1个单项,提取互斥项目
		for(MealItem item : mealItemLists){
			List<Integer>  composeIds = new ArrayList<Integer>();
			try {
				composeIds = ItemRelationFunction.getComposeItemId(item.getId(), meal.getHospitalId());
				if(composeIds != null && composeIds.size()>0){
					for(Integer composeId : composeIds){
						ExamItem e = checkExamItem(composeId);
						if(!e.isShow())continue;
						packagePrice = e.getPrice();
						itemList.add(e);
						break;
					}
					if(itemList.size()==1)break;

				}
			} catch (SqlException e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			}
		
		}
		if(itemList.size() == 0){
			log.error("无法创建合并单项包，套餐内项目没有合并项");
			return null;
		}
		
		List<Integer> ids = getNoConflitItemsWithMealId( mealId);
		itemList.add(checkExamItem(ids.get(0)));
		//设置调整金额=0
		int displayPrice = packagePrice;
		int adjustPrice = -10;
		int price = packagePrice  - adjustPrice;
		ExamItemPackage pack = createExamItemPackage(httpclient,adjustPrice,"自动化合并小项加项包",0,displayPrice,price,
				meal.getGender(),meal.getHospitalId(),packagePrice,itemList,"自动化合并小项"+getRandomHan()+getRandomHan());
		return pack;
	}
	
	/**
	 * 提取与套餐无冲突项目的单项列表
	 * @param httpClient
	 * @param mealId
	 */
	public static List<Integer> getNoConflitItemsWithMealId(int mealId){
		Set<Integer> noConflitLists = new HashSet<Integer>();
		Meal meal = getMealInfo(mealId);
		List<ExamItem> hosptialExamItemLists = getHospitalExamItems(meal.getHospitalId(),meal.getGender(),true);
		List<MealItem> mealItemLists = getMealInnerItemList(meal.getId());
		List<Integer> mealExamItemIdLists = getMealItemIdList(mealItemLists);
		try{
		for(ExamItem e : hosptialExamItemLists){
			int itemId = e.getId();
			List<Integer> childs = ItemRelationFunction.getChildItems(itemId, meal.getHospitalId());
			List<Integer> parents = ItemRelationFunction.getParentItemIds(itemId, meal.getHospitalId());
			List<Integer> composes = ItemRelationFunction.getComposeItemId(itemId, meal.getHospitalId());
			List<Integer> becomposes = ItemRelationFunction.getBeComposedItemIds(itemId, ConflictType.COMPOSE.getCode(), meal.getHospitalId());
			List<Integer> conflists = ItemRelationFunction.getConflictItemId(itemId, meal.getHospitalId());
			List<Integer> groups = ItemRelationFunction.queryGroupItems(itemId, meal.getHospitalId());
			//子项目
			if(childs !=null){
				boolean cflag = false;
				for(Integer c : childs){
					if(mealExamItemIdLists.contains(c)){
						cflag = true;
						break;
					}
				}
				if(cflag) continue;
			}
			//父项目
			if(parents !=null){
				boolean cflag = false;
				for(Integer p : parents){
					if(mealExamItemIdLists.contains(p)){
						cflag = true;
						break;
					}
				}
				if(cflag) continue;
			}
			//合并小项
			if(composes !=null){
				boolean cflag = false;
				for(Integer c : composes){
					if(mealExamItemIdLists.contains(c)){
						cflag = true;
						break;
					}
				}
				if(cflag) continue;
			}
			//合并大项
			if(becomposes !=null){
				boolean cflag = false;
				for(Integer c : becomposes){
					if(mealExamItemIdLists.contains(c)){
						cflag = true;
						break;
					}
				}
				if(cflag) continue;
			}
			//互斥项
			if(conflists != null){
				boolean cflag = false;
				for(Integer c : conflists){
					if(mealExamItemIdLists.contains(c)){
						cflag = true;
						break;
					}
				}
				if(cflag) continue;
			}
			//同组项
			if(groups != null){
				boolean cflag = false;
				for(Integer c : groups){
					if(mealExamItemIdLists.contains(c)){
						cflag = true;
						break;
					}
				}
				if(cflag) continue;
			}
			//重复项
			boolean cflag = false;
			if(mealExamItemIdLists.contains(e.getId()))
				cflag = true;
			
			if(cflag) continue;
			noConflitLists.add(e.getId());
			
		}}catch(Exception e ){
			e.printStackTrace();
		}
		return ListUtil.SetsToLists(noConflitLists);
		
	}
	
	/**
	 * 根据与套餐是否冲突创建单项包
	 * @param httpclient
	 * @param mealId
	 * @param isConflit 是否冲突
	 * @param repeat 是否重复
	 * @return
	 * @throws SqlException 
	 */
	public static ExamItemPackage createExamItemPackage(MyHttpClient httpclient ,int mealId,boolean isConflit,boolean repeat) throws SqlException{
		String packageName = "自动化加项目包";
		if(isConflit){
			ExamItemPackage pack = createComposeExamItemPackage(httpclient, mealId);
			if(pack!=null && pack.getId()>0)
				return pack;
			 pack = createGroupExamItemPackage(httpclient, mealId);
			 if(pack!=null && pack.getId()>0)
				return pack;
			 pack = createConflitExamItemPackage(httpclient, mealId);
			 if(pack!=null && pack.getId()>0)
				return pack;
			 pack = createChildExamItemPackage(httpclient, mealId);
			 if(pack!=null && pack.getId()>0)
				return pack;
			 pack = createParentExamItemPackage(httpclient, mealId);
			 if(pack!=null && pack.getId()>0)
				return pack;
			 pack = createBeComposeExamItemPackage(httpclient, mealId);
			 if(pack!=null && pack.getId()>0)
					return pack;
		}else{
			List<ExamItem> itemList = new ArrayList<ExamItem>();
			
			Meal meal  = getMealInfo(mealId);
			int hospitalId = meal.getHospitalId();
			//获取套餐内已勾选的单项列表
			List<MealItem> mealItemLists = getMealInnerItemList(meal.getId());
			List<Integer> mealExamItemIdLists = getMealItemIdList(mealItemLists);
			
			int packagePrice = 0;
			//取出医院所有的单项列表，提取冲突的单项列表，查看冲突单项在不在套餐内,在套餐内继续for循环，否则以当前单项作为单项包基础单项
			List<ExamItem> hosptialExamItemLists = getHospitalExamItems(hospitalId,meal.getGender(),true);
			for(ExamItem e : hosptialExamItemLists){
				int itemId = e.getId();
				List<Integer> childs = ItemRelationFunction.getChildItems(itemId, hospitalId);
				List<Integer> parents = ItemRelationFunction.getParentItemIds(itemId, hospitalId);
				List<Integer> composes = ItemRelationFunction.getComposeItemId(itemId, hospitalId);
				List<Integer> becomposes = ItemRelationFunction.getBeComposedItemIds(itemId, ConflictType.COMPOSE.getCode(), hospitalId);
				List<Integer> conflists = ItemRelationFunction.getConflictItemId(itemId, hospitalId);
				List<Integer> groups = ItemRelationFunction.queryGroupItems(itemId, hospitalId);
				if(childs !=null){
					//默认冲突
					boolean cflag = false;
					for(Integer c : childs){
						if(mealExamItemIdLists.contains(c)){
							cflag = true;
							break;
						}
					}
					//验证确实冲突了,寻找下一个不冲突的单项
					if(cflag)continue;
				}
				if(parents !=null){
					boolean cflag = false;
					for(Integer p : parents){
						if(mealExamItemIdLists.contains(p)){
							cflag = true;
							break;
						}
					}
					if(cflag)continue;
				}

				if(composes !=null){
					boolean cflag = false;
					for(Integer c : composes){
						if(mealExamItemIdLists.contains(c)){
							cflag = true;
							break;
						}
					}
					if(cflag)continue;
				}

				if(becomposes !=null){
					boolean cflag = false;
					for(Integer c : becomposes){
						if(mealExamItemIdLists.contains(c)){
							cflag = true;
							break;
						}
					}
					if(cflag)continue;
				}
				
				if(conflists != null){
					boolean cflag = false;
					for(Integer c : conflists){
						if(mealExamItemIdLists.contains(c)){
							cflag = true;
							break;
						}
					}
					if(cflag)continue;
				}

				if(groups != null){
					boolean cflag = false;
					for(Integer c : groups){
						if(mealExamItemIdLists.contains(c)){
							cflag = true;
							break;
						}
					}
					if(cflag)continue;
				}

				//重复项
				if(repeat){
					if(mealExamItemIdLists.contains(e.getId())){
						itemList.add(e);
						packagePrice += e.getPrice();
						packageName += "-重复项";
					}
				}else{
					if(!mealExamItemIdLists.contains(e.getId())){
						itemList.add(e);
						packagePrice += e.getPrice();
						packageName += "无冲突项";

					}
				}
				if(itemList.size() > 0)break;
				
			}
			if(itemList.size() == 0){
				log.error("无法创正常的单项包，因为都与套餐冲突了");
				return null;
			}
			//设置调整金额=0
			log.info("packagePrice..."+packagePrice);
			int displayPrice = packagePrice;
			int adjustPrice = -10;
			int price = packagePrice  - adjustPrice;
			ExamItemPackage pack = createExamItemPackage(httpclient,adjustPrice,packageName,0,displayPrice,price,
					meal.getGender(),meal.getHospitalId(),packagePrice,itemList,packageName+getRandomHan()+getRandomHan());
			return pack;
		}
		return null;
	}
	
	/**
	 * 根据单项列表创建单项包
	 * @param httpclient
	 * @param itemList
	 * @return
	 */
	public static ExamItemPackage createExamItemPackage(MyHttpClient httpclient ,List<ExamItem>itemList,int hospitalId,int gender){
		int packagePrice = 0;
		for(ExamItem e : itemList)
			packagePrice += e.getPrice();
		int displayPrice = packagePrice;
		int adjustPrice = -10;
		int price = packagePrice - adjustPrice;
		ExamItemPackage pack = createExamItemPackage(httpclient,adjustPrice,"自动化指定单项加项包",0,displayPrice,price,
				gender,hospitalId,packagePrice,itemList,"自动化指定单项"+getRandomHan()+getRandomHan());
		return pack;
	}
	/**
	 * 创建可显示的自定义加项包
	 * @param httpclient
	 * @param adjustPrice
	 * @param description
	 * @param disable
	 * @param displayPrice
	 * @param price
	 * @param gender
	 * @param hospitalId
	 * @param initPrice
	 * @param itemList
	 * @param pacakgeName
	 * @return
	 */
	public static ExamItemPackage createExamItemPackage(MyHttpClient httpclient,int adjustPrice,String description,int disable,int displayPrice,int price,
			int gender,int hospitalId,int initPrice,List<ExamItem> itemList,String pacakgeName){
		ExamItemPackage examItemPackage = new ExamItemPackage();
		examItemPackage.setAdjustPrice(Integer.valueOf(adjustPrice));
		examItemPackage.setDescription(description);
		examItemPackage.setDisable(Integer.valueOf(disable));
		examItemPackage.setDisplayPrice(Integer.valueOf(displayPrice));
		examItemPackage.setGender(Integer.valueOf(gender));
		examItemPackage.setHospitalId(hospitalId);
		examItemPackage.setInitPrice(Integer.valueOf(initPrice));
		examItemPackage.setIsShow(true);
		examItemPackage.setName(pacakgeName);
		examItemPackage.setPrice(price);
		examItemPackage.setShowInitPrice(true);
		examItemPackage.setItemList(itemList);
		String json = JSON.toJSONString(examItemPackage);

		HttpResult result = httpclient.post(ExamitemPackage_ExamitemPackage, json);
		String body = result.getBody();
		log.info("新建单项包结果..."+body);
		Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);

		ExamItemPackage pkg = JSON.parseObject(body, ExamItemPackage.class);
		return pkg;
	} 

	
	public static List<MealChangeRecord> getMealChangeRecord(Integer mealId){
		List<MealChangeRecord> records = new ArrayList<MealChangeRecord>();
		String sql = "SELECT * FROM tb_meal_change_record WHERE meal_id = ? order by id desc";
		List<Map<String, Object>> list = null;
		try {
			list = DBMapper.query(sql, mealId);
		} catch (SqlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(list.size()>0&&list!=null)
			list.forEach(m->{
				MealChangeRecord record = new MealChangeRecord();
				record.setId(Integer.valueOf(m.get("id").toString()));
				record.setMealId(Integer.valueOf(m.get("meal_id").toString()));
				record.setCreateDate((Date)m.get("create_date"));
				record.setOperationDate((Date)m.get("operation_date"));
				if(m.get("hospital_id")!=null)
					record.setHosptialId(Integer.valueOf(m.get("hospital_id").toString()));
				record.setOperation(m.get("operation").toString());
				records.add(record);
			});
		return records;
	}
	
	/**
	 * 根据卡id获取套餐
	 * 
	 * @param cardId
	 * @return
	 */
	public static List<HospitalMealDto> getHospitalMealByCardId(Integer cardId) {
		// 从卡套餐关系表获取
		List<HospitalMealDto> cardMeals = getFromCardMealRelation(cardId);

		if(cardMeals==null||cardMeals.isEmpty()) {			
			// 从卡医院列表获取
			cardMeals = getFromCardHospitalRelation(cardId, cardMeals);
		}

		// 排序 hosId-sequence
		sortWithHospitalIdThenSequence(cardMeals);
		return cardMeals;
	}
	
	private static void sortWithHospitalIdThenSequence(List<HospitalMealDto> cardMeals) {
		// TODO Auto-generated method stub
		cardMeals.sort((o1, o2) -> {
            if (Objects.equals(o1.getHospitalId(),o2.getHospitalId())){
				if (o1.getMealSequence() == null && o2.getMealSequence() == null){
					return 0;
				}
                if (o1.getMealSequence() == null){
                    return 1;
                }
                if (o2.getMealSequence() == null){
                    return -1;
                }
                return o1.getMealSequence()-o2.getMealSequence();

            }else {
                return o1.getHospitalId()-o2.getHospitalId();
            }
        });
	}

	/**
	 * 根据card-hospital关系表，获取医院官方套餐
	 * @param cardId
	 * @param cardMeals
	 * @return
	 */
	public static List<HospitalMealDto> getFromCardHospitalRelation(Integer cardId, List<HospitalMealDto> cardMeals) {
		// TODO Auto-generated method stub
		if (CollectionUtils.isEmpty(cardMeals)) {
			//获取卡支持的医院
			List<Integer> hospitalIdByCardId = CardChecker.getHospitalIdByCardHosRela(cardId);
			List<Meal> mealList = Lists.newArrayList();
			for (Integer hospitalId : hospitalIdByCardId) {
				//获取医院所有的官方套餐
				List<Meal> offcialMeals = ResourceChecker.getOffcialMealByHospitalIdAndType(hospitalId,MealTypeEnum.COMMON_MEAL.getCode());
				mealList.addAll(offcialMeals);
			}
			for (Meal meal : mealList) {
				if (cardMeals == null) {
					cardMeals = Lists.newArrayList();
				}
				HospitalMealDto hospitalMealDto = new HospitalMealDto();
				hospitalMealDto.setMealId(meal.getId());
				hospitalMealDto.setGender(meal.getGender());
				hospitalMealDto.setHospitalId(meal.getHospitalId());
				hospitalMealDto.setCardId(cardId);
				hospitalMealDto.setMealName(meal.getName());
				hospitalMealDto.setMealPrice(meal.getPrice());
				hospitalMealDto.setMealStatus(meal.getDisable());
				hospitalMealDto.setMealType(meal.getType());
				cardMeals.add(hospitalMealDto);
			}
			return cardMeals;
		}
		return null;
	}

	/**
	 * 从卡-套餐关系表查询套餐 CardMeal->HospitalMealDto
	 * @param cardId
	 * @return
	 */
	public static List<HospitalMealDto> getFromCardMealRelation(Integer cardId) {
		List<CardMeal> tmpCardMealList = getCardMealByCardId(cardId);
		List<HospitalMealDto> cardMealList = Lists.newArrayList();
		tmpCardMealList.forEach(cardMeal -> {
			HospitalMealDto mealDto = new HospitalMealDto();
			BeanUtils.copyProperties(cardMeal, mealDto);
			Meal meal = ResourceChecker.getMealInfo(cardMeal.getMealId());
			if(AssertUtil.isNotNull(meal)){
				mealDto.setMealName(meal.getName());
				mealDto.setMealPrice(meal.getPrice());
				mealDto.setMealType(meal.getType());
				mealDto.setGender(meal.getGender());
				mealDto.setMealStatus(meal.getDisable());
				mealDto.setMealSequence(meal.getSequence());
			}
			cardMealList.add(mealDto);
		});
		return cardMealList;
	}
	
	/**
	 * tb_card_direction_meal_relation查套餐
	 * @param cardId
	 * @return
	 */
	public static List<CardMeal> getCardMealByCardId(Integer cardId){
		List<CardMeal> cardMeals = new ArrayList<>();
		String sql = "SELECT * FROM tb_card_direction_meal_relation WHERE card_id = ?;";
		List<Map<String, Object>> list = null;
		try {
			list = DBMapper.query(sql, cardId);
		} catch (SqlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(list!=null&&!list.isEmpty()) {
			for(Map<String,Object> m : list) {
				CardMeal cm = new CardMeal();
				cm.setCardId(Integer.valueOf(m.get("card_id").toString()));
				cm.setHospitalId(Integer.valueOf(m.get("hospital_id").toString()));
				cm.setIsMealForFamily(Integer.valueOf(m.get("meal_for_family").toString())==1?true:false);
				cm.setIsMealForSelf(Integer.valueOf(m.get("meal_for_self").toString())==1?true:false);
				cm.setMealId(Integer.valueOf(m.get("meal_id").toString()));
				cardMeals.add(cm);
			}
		}
		return cardMeals;
	}

	/**
	 * 根据套餐ID查询套餐是否在活动中，不在活动中返回原始套餐信息
	 * 在活动中返回活动售价/调整金额/折扣
	 * @param mealId
	 * @return
	 */
	public static Meal getMealByActivty(int mealId){
		Meal meal = getMealInfo(mealId);
		String sql = "select m.* from tb_activity_meal m,tb_activity a where m.activity_id = a.id and a.is_deleted = 0 and a.end_time > now() and m.meal_id = "+mealId;
		List<Map<String, Object>> list = null;
		try {
			list = DBMapper.query(sql);
		} catch (SqlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(list!=null&&!list.isEmpty()) {
			meal.setPrice(Integer.parseInt(list.get(0).get("price").toString()));//售价
			meal.setDisplayPrice(Integer.parseInt(list.get(0).get("display_price").toString()));//显示价格
			meal.getMealSetting().setAdjustPrice(Integer.parseInt(list.get(0).get("adjust_price").toString()));//调整金额
			int activity_meal_id = Integer.parseInt(list.get(0).get("id").toString());
			sql = "select c.discount from tb_activity_meal_settings  s ,tb_activity_meal_discount_coupon c where  c.coupon_id = s.id and s.activity_meal_id = "+activity_meal_id;
			List<Map<String, Object>> list2 = null;
			try {
				list2 = DBMapper.query(sql);
			} catch (SqlException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(list2 != null && list2.isEmpty())
				meal.setDiscount(Double.parseDouble(list2.get(0).get("discount").toString()));
		}
		return meal;
	}

	/*public static void getMealByCrm(int multi_choosen_id){
		Meal meal = getMealInfo(multi_choosen_id);
		String sql = "select * from tb_meal_multi_choosen_examitem where multi_choosen_id = "+multi_choosen_id;
		List<Map<String,Object>> list = null;
		try {
			list = DBMapper.query(sql);
		} catch (SqlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(list!=null&&list.isEmpty()){
			meal.setPrice(Integer.parseInt(list.get(0).get("price").toString()));//售价
			meal.setDisplayPrice(Integer.parseInt(list.get(0).get("display_price").toString()));//显示价格
			meal.getMealSetting().setAdjustPrice(Integer.parseInt(list.get(0).get("adjust_price").toString()));//调整金额
			int multi_choosen_id = Integer.parseInt(list.get(0).get("id").toString());
			sql = "select c.discount from tb_activity_meal_settings  s ,tb_activity_meal_discount_coupon c where  c.coupon_id = s.id and s.activity_meal_id = "+activity_meal_id;
			List<Map<String, Object>> list2 = null;
			try {
				list2 = DBMapper.query(sql);
			} catch (SqlException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(list2 != null && list2.isEmpty())
				meal.setDiscount(Double.parseDouble(list2.get(0).get("discount").toString()));

		}
	}*/


}
