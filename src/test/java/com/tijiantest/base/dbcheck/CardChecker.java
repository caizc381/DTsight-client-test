package com.tijiantest.base.dbcheck;

import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

import com.tijiantest.model.account.Examiner;
import com.tijiantest.model.card.*;
import com.tijiantest.util.db.RedisUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.http.HttpStatus;
import org.springframework.beans.BeanUtils;
import org.testng.Assert;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.jayway.jsonpath.JsonPath;
import com.tijiantest.base.BaseTest;
import com.tijiantest.base.HttpResult;
import com.tijiantest.base.MyHttpClient;
import com.tijiantest.model.account.AccountRelationInCrm;
import com.tijiantest.model.account.AcctRelationQueryDto;
import com.tijiantest.model.company.BaseCompany;
import com.tijiantest.model.company.ChannelCompany;
import com.tijiantest.model.company.HospitalCompany;
import com.tijiantest.model.hospital.Hospital;
import com.tijiantest.model.hospital.HostpitalMealDto;
import com.tijiantest.model.organization.OrganizationTypeEnum;
import com.tijiantest.model.resource.meal.Meal;
import com.tijiantest.model.resource.meal.MealTypeEnum;
import com.tijiantest.model.settlement.SettlementHospitalConfirmEnum;
import com.tijiantest.util.DateUtils;
import com.tijiantest.util.ListUtil;
import com.tijiantest.util.db.DBMapper;
import com.tijiantest.util.db.SqlException;
import com.tijiantest.util.pagination.Page;
import redis.clients.jedis.Jedis;

/**
 * 卡验证
 * 
 * @author huifang
 *
 */
public class CardChecker extends BaseTest {
	/**
	 * 发卡操作，默认基础套餐
	 * 
	 * @param hc
	 * @param account_id
	 *            账号Id
	 * @param companyId新单位id
	 *            单位名称
	 * @param cardName
	 *            卡名称
	 * @param capacity
	 *            价格
	 * @return
	 * @throws SqlException
	 */
	public static Card createCard(MyHttpClient hc, int account_id, int newCompanyId, String cardName, long capacity,
			int mealId, int hospitalId, int managerid) throws SqlException {
		Card retCard = new Card();
		System.err.println("companyId:" + newCompanyId);

		int organizationType = HospitalChecker.getOrganizationType(hospitalId);
		int parentCardId = getParentEntryCard(managerid);
		Date expireDate = DateUtils.offDate(30);
		// 卡信息
		Card card = new Card();
		card.setCapacity(capacity);
		card.setCardName(cardName);
		card.setCardSetting(new CardSetting(parentCardId));
		card.setExpiredDate(DateUtils.toDayLastSecod(expireDate));
		card.setId(parentCardId);

		// 卡绑定套餐
		HostpitalMealDto hmd = new HostpitalMealDto();
		hmd.setHospitalId(hospitalId);
		hmd.setIsMealForSelf(true);
		hmd.setIsMealForFamily(false);
		hmd.setMealId(mealId);
		hmd.setMealPrice(ResourceChecker.getMealInfo(mealId).getPrice());
		List<HostpitalMealDto> cardMealList = new ArrayList<HostpitalMealDto>();
		cardMealList.add(hmd);
		CardDto cdt = new CardDto();
		cdt.setAccountIdList(Arrays.asList(account_id));
		cdt.setCard(card);
		cdt.setCardMealList(cardMealList);
		cdt.setCompanyId(newCompanyId);
		cdt.setNewCompanyId(newCompanyId);
		cdt.setOrganizationType(organizationType);
		cdt.setIsSendBookingMsg(false);
		cdt.setIsSendCardMsg(false);

		String jbody = JSON.toJSONString(cdt);

		int beforenum = 0;
		int afternum = 0;
		if (checkdb) {
			String sql = "SELECT * FROM tb_card WHERE card_name = '" + cardName + "' " + "and parent_card_id = ? "
					+ "and status = 1 order by id desc";
			log.info("sql:" + sql);
			List<Map<String, Object>> rets = null;
			try {
				rets = DBMapper.query(sql, parentCardId);
			} catch (SqlException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			beforenum = rets.size();
		}

		HttpResult result = hc.post(Card_DistributeCard, jbody);

		Assert.assertEquals(result.getCode(), HttpStatus.SC_OK, "批量发卡:" + result.getBody());
		Assert.assertEquals(result.getBody(), "{}");

		CardChecker.waitDistributeCardProc(hc);
		if (checkdb) {
			String sql = "SELECT * FROM tb_card WHERE card_name = '" + cardName + "' " + "and parent_card_id = ? "
					+ "and status = 1 order by id desc";
			log.info("sql:" + sql);
			List<Map<String, Object>> rets = null;
			try {
				rets = DBMapper.query(sql, parentCardId);
			} catch (SqlException e) {
				e.printStackTrace();
			}
			afternum = rets.size();
			Assert.assertEquals(afternum, beforenum + 1);

			// 用于恢复客户为未发卡状态
			Map<String, Object> ret = rets.get(0);

			retCard.setBalance(Long.parseLong(ret.get("balance").toString()));
			retCard.setCapacity(Long.parseLong(ret.get("capacity").toString()));
			retCard.setCardName(ret.get("card_name").toString());
			retCard.setId(Integer.parseInt(ret.get("id").toString()));
			retCard.setStatus(Integer.parseInt(ret.get("status").toString()));
			retCard.setAccountId(Integer.parseInt(ret.get("account_id").toString()));
			retCard.setBatchId(Integer.valueOf(ret.get("batch_id").toString()));
			retCard.setHospitalSettlementStatus(Integer.parseInt(ret.get("hospital_settlement_status").toString()));
			Object settleBatch = ret.get("settlement_batch_sn");
			if (settleBatch != null)
				retCard.setSettlementBatchSn(settleBatch.toString());
			retCard.setFreezeBalance(Long.parseLong(ret.get("freeze_balance").toString()));
			String batchSql = "select * from tb_card_batch batch left join tb_card card on card.batch_id=batch.id where card.id=?";
			List<Map<String, Object>> batchList = DBMapper.query(batchSql, ret.get("id"));
			retCard.setNewCompanyId(Integer.parseInt(batchList.get(0).get("new_company_id").toString()));
			retCard.setOrganizationType(Integer.parseInt(batchList.get(0).get("organization_type").toString()));

		}
		return retCard;
	}

	/**
	 * 发隐价卡
	 * 
	 * @param hc
	 * @param account_id
	 * @param newCompanyId
	 * @param cardName
	 * @param hospitalId
	 * @param managerid
	 * @return
	 * @throws SqlException
	 */
	public static Card createHidePriceCard(MyHttpClient hc, int account_id, int newCompanyId, String cardName,
			int hospitalId, int managerid, int gender) throws SqlException {
		Card retCard = new Card();
		System.err.println("companyId:" + newCompanyId);

		int organizationType = HospitalChecker.getOrganizationType(hospitalId);
		int parentCardId = getParentEntryCard(managerid);
		Date expireDate = DateUtils.offDate(30);
		// 卡绑定套餐
		// 找寻与性别一致的单位套餐
		int mealId = -1;
		List<Meal> mealList = ResourceChecker.getMealByRelation(hospitalId, MealTypeEnum.COMPANY_MEAL.getCode(),
				newCompanyId);
		if (mealList == null || mealList.size() == 0) {
			log.error("没有符合新单位的套餐,自动创建套餐---");
			mealId = ResourceChecker.createMeal(hc, newCompanyId, "单位套餐—-自动化创建", gender, hospitalId).getId();
		} else {
			for (Meal m : mealList) {
				if (m.getGender() == gender)
					mealId = m.getId();
			}
		}
		int mealPrice = ResourceChecker.getMealInfo(mealId).getPrice();
		HostpitalMealDto hmd = new HostpitalMealDto();
		hmd.setHospitalId(hospitalId);
		hmd.setIsMealForSelf(true);
		hmd.setIsMealForFamily(false);
		hmd.setMealId(mealId);
		hmd.setMealPrice(mealPrice);
		List<HostpitalMealDto> cardMealList = new ArrayList<HostpitalMealDto>();
		cardMealList.add(hmd);

		// 卡信息
		Card card = new Card();
		card.setCapacity((long) mealPrice);
		card.setCardName(cardName);
		// 隐价卡属性
		CardSetting settings = new CardSetting();
		settings.setCardId(parentCardId);
		settings.setShowCardMealPrice(true);
		settings.setPrivate(true);
		card.setCardSetting(settings);
		card.setExpiredDate(DateUtils.toDayLastSecod(expireDate));
		card.setId(parentCardId);

		CardDto cdt = new CardDto();
		cdt.setAccountIdList(Arrays.asList(account_id));
		cdt.setCard(card);
		cdt.setCardMealList(cardMealList);
		cdt.setCompanyId(newCompanyId);
		cdt.setNewCompanyId(newCompanyId);
		cdt.setOrganizationType(organizationType);
		cdt.setIsSendBookingMsg(false);
		cdt.setIsSendCardMsg(false);

		String jbody = JSON.toJSONString(cdt);

		int beforenum = 0;
		int afternum = 0;
		if (checkdb) {
			String sql = "SELECT * FROM tb_card WHERE card_name = '" + cardName + "' " + "and parent_card_id = ? "
					+ "and status = 1 order by id desc";
			log.info("sql:" + sql);
			List<Map<String, Object>> rets = null;
			try {
				rets = DBMapper.query(sql, parentCardId);
			} catch (SqlException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			beforenum = rets.size();
		}

		HttpResult result = hc.post(Card_DistributeCard, jbody);

		Assert.assertEquals(result.getCode(), HttpStatus.SC_OK, "批量发卡:" + result.getBody());
		Assert.assertEquals(result.getBody(), "{}");

		CardChecker.waitDistributeCardProc(hc);
		if (checkdb) {
			String sql = "SELECT * FROM tb_card WHERE card_name = '" + cardName + "' " + "and parent_card_id = ? "
					+ "and status = 1 order by id desc";
			log.info("sql:" + sql);
			List<Map<String, Object>> rets = null;
			try {
				rets = DBMapper.query(sql, parentCardId);
			} catch (SqlException e) {
				e.printStackTrace();
			}
			afternum = rets.size();
			Assert.assertEquals(afternum, beforenum + 1);

			// 用于恢复客户为未发卡状态
			Map<String, Object> ret = rets.get(0);

			retCard.setBalance(Long.parseLong(ret.get("balance").toString()));
			retCard.setCapacity(Long.parseLong(ret.get("capacity").toString()));
			retCard.setCardName(ret.get("card_name").toString());
			retCard.setId(Integer.parseInt(ret.get("id").toString()));
			retCard.setStatus(Integer.parseInt(ret.get("status").toString()));
			retCard.setAccountId(Integer.parseInt(ret.get("account_id").toString()));
			retCard.setBatchId(Integer.valueOf(ret.get("batch_id").toString()));

			String batchSql = "select * from tb_card_batch batch left join tb_card card on card.batch_id=batch.id where card.id=?";
			List<Map<String, Object>> batchList = DBMapper.query(batchSql, ret.get("id"));
			retCard.setNewCompanyId(Integer.parseInt(batchList.get(0).get("new_company_id").toString()));
			retCard.setOrganizationType(Integer.parseInt(batchList.get(0).get("organization_type").toString()));

		}
		return retCard;
	}


	/**
	 * 发卡后调用查询卡进度
	 * @param httpclient
	 */
	public static void waitDistributeCardProc(MyHttpClient httpclient){
		HttpResult result = null;
		for (int i = 0; i < 10; i++) {
			result = httpclient.get(Card_DistributeCardProc);
			Assert.assertEquals(result.getCode(), HttpStatus.SC_OK, "查询发实体卡进度:" + result.getBody());
			String rbody = result.getBody();
			log.info(rbody);
			if(rbody != null && !rbody.toString().equals("")){
				int totalnum = JsonPath.read(rbody, "$.totalNum");
				int dealnum = JsonPath.read(rbody, "$.dealNum");
				String status = JsonPath.read(rbody, "$.status");
				if (dealnum == totalnum && status.equals("completed"))
					break;
			}
			waitto(2);
		}
	}

	// /**
	// *
	// * @param cardId
	// * @return
	// * @throws SqlException
	// */
	// public static Card getCardInfo(int cardId) throws SqlException {
	// String sql = "select * from tb_card where id = ?";
	// List<Map<String, Object>> list = DBMapper.query(sql, cardId);
	// Map<String, Object> map = list.get(0);
	// Card card = new Card();
	// card.setAccountId(Integer.parseInt(map.get("account_id").toString()));
	// card.setBalance(Long.parseLong(map.get("balance").toString()));
	// card.setCapacity(Long.parseLong(map.get("capacity").toString()));
	// if (map.get("batch_id") != null)
	// card.setBatchId(Integer.parseInt(map.get("batch_id").toString()));
	// card.setType(Integer.parseInt(map.get("type").toString()));
	// return card;
	// }
	//

	/**
	 * 卡基本信息
	 * 
	 * @param cardId
	 * @return
	 * @throws SqlException
	 * @throws ParseException
	 */
	public static Card getCardInfo(int cardId) throws SqlException, ParseException {
		String sql3 = "select a.*,b.* ,s.* from tb_card a ,tb_manager_card_relation b ,tb_card_settings s where a.id=b.card_id and a.id = s.card_id and  a.id= ?";
		List<Map<String, Object>> dblist = DBMapper.query(sql3, cardId);
		if (dblist.size() < 1) {
			log.error("卡不存在" + cardId);
			return null;
		}
		Card c = new Card();
		Map<String, Object> dbMap = dblist.get(0);
		c.setId(cardId);
		c.setAccountId(Integer.parseInt(dbMap.get("account_id").toString()));
		c.setCapacity(Long.parseLong(dbMap.get("capacity").toString()));
		c.setBalance(Long.parseLong(dbMap.get("balance").toString()));
		if (dbMap.get("recoverable_balance") != null)
			c.setRecoverableBalance((Long.parseLong(dbMap.get("recoverable_balance").toString())));
		else
			c.setRecoverableBalance(0l);
		c.setType(Integer.parseInt(dbMap.get("type").toString()));
		c.setStatus(Integer.parseInt(dbMap.get("status").toString()));
		if (dbMap.get("from_hospital") != null)
			c.setFromHospital(Integer.parseInt(dbMap.get("from_hospital").toString()));
		if (dbMap.get("parent_card_id") != null)
			c.setParentCardId(Integer.parseInt(dbMap.get("parent_card_id").toString()));
		c.setRechargeTime(simplehms.parse(dbMap.get("recharge_time").toString()));
		c.setAvailableDate(simplehms.parse((dbMap.get("available_date").toString())));
		c.setExpiredDate(simplehms.parse(dbMap.get("expired_date").toString()));
		c.setCreateDate(simplehms.parse(dbMap.get("create_date").toString()));
		c.setCardName(dbMap.get("card_name").toString());
		c.setCardNum(dbMap.get("card_num").toString());
		if(dbMap.get("manager_id") != null)
			c.setManagerId(Integer.parseInt(dbMap.get("manager_id").toString()));
		if (dbMap.get("new_company_id") != null)
			c.setNewCompanyId(Integer.valueOf(dbMap.get("new_company_id").toString()));
		if (dbMap.get("organization_type") != null)
			c.setOrganizationType(Integer.valueOf(dbMap.get("organization_type").toString()));
		if (dbMap.get("password") != null)
			c.setPassword(dbMap.get("password").toString());
		if (dbMap.get("batch_id") != null)
			c.setBatchId(Integer.parseInt(dbMap.get("batch_id").toString()));
		c.setTradeAccountId(Integer.parseInt(dbMap.get("trade_account_id").toString()));
		c.setFreezeBalance(Long.parseLong(dbMap.get("freeze_balance").toString()));
		c.setHospitalSettlementStatus(Integer.parseInt(dbMap.get("hospital_settlement_status").toString()));
		if (dbMap.get("settlement_batch_sn") != null)
			c.setSettlementBatchSn(dbMap.get("settlement_batch_sn").toString());
		if(dbMap.get("exam_note_id") != null)
			c.setExamNoteId(Integer.parseInt(dbMap.get("exam_note_id").toString()));
		CardSetting cardSetting = new CardSetting();
		cardSetting.setShowCardMealPrice(Integer.parseInt(dbMap.get("is_show_card_meal_price").toString()) == 1 ? true:false);
		cardSetting.setPrivate(Integer.parseInt(dbMap.get("isprivate").toString()) == 1 ? true:false);
		cardSetting.setPayFreeze(Integer.parseInt(dbMap.get("is_pay_freeze").toString()) == 1 ? true:false);
		cardSetting.setPayMealCost(Integer.parseInt(dbMap.get("is_pay_meal_cost").toString()) == 1 ? true:false);
		c.setCardSetting(cardSetting);
		return c;
	}

	/**
	 * tb_card
	 */
	public static Integer getParentEntryCard(int accountId) {
		String sql = "SELECT * FROM tb_card WHERE parent_card_id is null  AND account_id = ?";
		Integer parentCardId;
		List<Map<String, Object>> list = null;
		try {
			list = DBMapper.query(sql, accountId);
		} catch (SqlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		parentCardId = (Integer) list.get(0).get("id");
		return parentCardId;
	}

	/**
	 * 获取与套餐关联的卡
	 * 
	 * @param hospitalId
	 * @param account_id
	 * @param mealId
	 * @return
	 */
	public static List<Integer> getOneHosCards(int hospitalId, int account_id, int mealId) {
		List<Integer> cardIds = new ArrayList<Integer>();
		String sql = "select * from tb_card  c ,tb_card_direction_meal_relation d where " + "c.id = d.card_id and "
				+ "d.hospital_id = ? and c.account_id = ? and c.status = 1 and d.meal_id = ?";
		try {
			List<Map<String, Object>> list = DBMapper.query(sql, hospitalId, account_id, mealId);
			log.info(hospitalId + "..." + account_id + "...." + mealId);
			log.info("list size..." + list.size());
			if (list.size() > 0) {
				for (Map<String, Object> m : list) {
					Integer id = Integer.parseInt(m.get("card_id").toString());
					cardIds.add(id);
				}
			} else
				return null;
		} catch (SqlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return cardIds;
	}

	/**
	 * 卡设置信息
	 * 
	 * @param cardId
	 * @return
	 * @throws SqlException
	 * @throws ParseException
	 */
	public static CardSetting getCardSettings(int cardId) throws SqlException, ParseException {
		String sql = "select * from tb_card_settings where card_id  = ?";
		List<Map<String, Object>> dblist = DBMapper.query(sql, cardId);
		Assert.assertEquals(dblist.size(), 1);
		Map<String, Object> dbMap = dblist.get(0);
		CardSetting cardSet = new CardSetting();
		cardSet.setCardId(cardId);
		cardSet.setPayFreeze(dbMap.get("is_pay_freeze").toString().equals("1") ? true : false);
		cardSet.setShowCardMealPrice(dbMap.get("is_show_card_meal_price").toString().equals("1") ? true : false);
		cardSet.setPayMealCost(dbMap.get("is_pay_meal_cost").toString().equals("1") ? true : false);
		cardSet.setPrivate(dbMap.get("isprivate").toString().equals("1") ? true : false);
		if (dbMap.get("recoverable_balance_time") != null)
			cardSet.setRecoverableBalTime(simplehms.parse(dbMap.get("recoverable_balance_time").toString()));
		if (dbMap.get("revocable_time") != null)
			cardSet.setRevocableTime(simplehms.parse(dbMap.get("revocable_time").toString()));
		return cardSet;
	}

	/**
	 * 获取与套餐关联的虚拟卡
	 * 
	 * @param hospitalId
	 * @param account_id
	 * @param mealId
	 * @return
	 */
	public static List<Integer> getOneHosVirtualCards(int hospitalId, int account_id, int mealId) {
		List<Integer> cardIds = new ArrayList<Integer>();
		String today = sdf.format(new Date()) + " 00:00:00";
		String sql = "select * from tb_card  c ,tb_card_direction_meal_relation d where " + "c.id = d.card_id and "
				+ "d.hospital_id = ? and c.account_id = ? and c.status = 1 and d.meal_id = ? and c.type = 1 and c.expired_date >= '"
				+ today + "'";
		try {
			List<Map<String, Object>> list = DBMapper.query(sql, hospitalId, account_id, mealId);
			log.debug(hospitalId + "..." + account_id + "...." + mealId);
			log.debug("list size..." + list.size());
			if (list.size() > 0) {
				for (Map<String, Object> m : list) {
					Integer id = Integer.parseInt(m.get("card_id").toString());
					cardIds.add(id);
				}
			} else {
				log.error("C端用户没有虚拟卡，请为此用户发卡!!!!!!");
				return null;
			}
		} catch (SqlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return cardIds;
	}

	/**
	 * // * 通过卡id查找关联的体检中心id列表 // * @param cardId // * @return //
	 */
	// public List<Hospital> getSupportHospitalByCardId(int cardId){
	// try {
	// Card card = getCardInfo(cardId);
	// int new_company_id = card.getNewCompanyId();
	// int organizaton_type = card.getOrganizationType();
	// if(organizaton_type == OrganizationTypeEnum.CHANNEL.getCode()){
	// MyHttpClient hc = new MyHttpClient();
	// onceLoginInSystem(hc, Flag.CRM, defCrmUsername, defCrmPasswd);
	// List<Hospital> hospitals =
	// CompanyChecker.getHospitalByCompanyId(hc,new_company_id);
	// return hospitals;
	//
	// }else if(organizaton_type == OrganizationTypeEnum.HOSPITAL.getCode()){
	// HospitalCompany hcompany =
	// CompanyChecker.getHospitalCompanyById(new_company_id);
	// Hospital hospital = getHospitalIdByCardId(cardId)
	//
	// }else{
	// return null;
	// }
	// } catch (SqlException e) {
	// e.printStackTrace();
	// } catch (ParseException e) {
	// e.printStackTrace();
	// }
	// return null;
	// }
	/**
	 * 获取与客户可用的虚拟卡
	 * 
	 * @param hospitalId
	 * @param account_id
	 * @return
	 */
	public List<Integer> getVirtualCardsByAccountId(int account_id) {
		List<Integer> cardIds = new ArrayList<Integer>();
		String today = sdf.format(new Date()) + " 00:00:00";
		String sql = "SELECT * from tb_card WHERE account_id = ? AND type =1 AND status = 1 AND expired_date >= '"
				+ today + "';";
		try {
			List<Map<String, Object>> list = DBMapper.query(sql, account_id);
			log.info("list size..." + list.size());
			if (list.size() > 0) {
				for (Map<String, Object> m : list) {
					Integer id = Integer.parseInt(m.get("id").toString());
					cardIds.add(id);
				}
			} else
				return null;
		} catch (SqlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return cardIds;
	}

	/**
	 * 提取卡的体检通知
	 * @param exam_note_id
	 * @return
	 */
	public static CardExamNote getCardExamNotes(int exam_note_id){
		String sql = "select * from tb_card_exam_note where id = ?";
		try {
			List<Map<String,Object>> list = DBMapper.query(sql,exam_note_id);
			if(list != null && !list.isEmpty()){
				CardExamNote cardExamNote = new CardExamNote();
				cardExamNote.setId(Integer.parseInt(list.get(0).get("id").toString()));
				cardExamNote.setNote(list.get(0).get("note").toString());
				cardExamNote.setNoteName(list.get(0).get("note_name").toString());
				cardExamNote.setAccountId(Integer.parseInt(list.get(0).get("account_id").toString()));
				cardExamNote.setNewCompanyId(Integer.parseInt(list.get(0).get("new_company_id").toString()));
				cardExamNote.setOrganizationType(Integer.parseInt(list.get(0).get("organization_type").toString()));
				return cardExamNote;
			}
		} catch (SqlException e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 获取与客户可用的实体卡
	 * 
	 * @param hospitalId
	 * @param account_id
	 * @return
	 */
	public static List<Integer> getEntityCardsByAccountId(int account_id) {
		List<Integer> cardIds = new ArrayList<Integer>();
		String today = sdf.format(new Date()) + " 00:00:00";
		String sql = "SELECT * from tb_card WHERE account_id = ? AND type =2 AND status = 1 AND expired_date >= '"
				+ today + "' order by recharge_time desc;";
		try {
			List<Map<String, Object>> list = DBMapper.query(sql, account_id);
			log.info("list size..." + list.size());
			if (list.size() > 0) {
				for (Map<String, Object> m : list) {
					Integer id = Integer.parseInt(m.get("id").toString());
					cardIds.add(id);
				}
			} else
				return null;
		} catch (SqlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return cardIds;
	}

	/**
	 * tb_card_direction_meal_relation tb_card_hospital_relation
	 * 
	 * @param cardId
	 * @param hospitalId
	 * @return
	 */
	public static List<Integer> getMealByCardId(Integer cardId, Integer hospitalId) {
		List<Integer> mealIds = new ArrayList<Integer>();
		// 从tb_card_direction_meal_relation获取套餐
		String sql = "SELECT m.* FROM tb_meal m " + "LEFT JOIN tb_card_direction_meal_relation cm ON cm.meal_id = m.id "
				+ "LEFT JOIN tb_card c ON c.id = cm.card_id " + "WHERE cm.hospital_id = ? " + "AND m. DISABLE = 0 "
				+ "AND c.id = ?;";
		List<Map<String, Object>> list = null;
		try {
			list = DBMapper.query(sql, hospitalId, cardId);
			System.err.println(
					"tb_card_direction_meal_relation:" + sql + "  cardId:" + cardId + " list.size" + list.size());
		} catch (SqlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (list.isEmpty()) {
			// 从tb_card_hospital_relation获取套餐
			sql = "SELECT m.* FROM tb_meal m "
					+ "LEFT JOIN tb_card_hospital_relation ch ON ch.hospital_id = m.hospital_id "
					+ "LEFT JOIN tb_card c ON c.id = ch.card_id " + "WHERE ch.hospital_id = ? " + "AND c.id = ? "
					+ "AND m.gender <> 2 " + "AND m.type = 3 " + "AND m. DISABLE = 0 ";
			try {
				list = DBMapper.query(sql, hospitalId, cardId);
				System.err.println("tb_card_hospital_relation:" + sql);
			} catch (SqlException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (list.size() > 0 && list != null) {
			for (Map<String, Object> m : list) {
				Integer mealId = Integer.valueOf(m.get("id").toString());
				mealIds.add(mealId);
			}
			return mealIds;
		}
		return null;
	}

	/**
	 * 获取套餐的实体卡
	 * 
	 * @param hospitalId
	 * @param account_id
	 * @param mealId
	 * @return
	 */
	public List<Integer> getOneHosEntityCards(int hospitalId, int account_id, int mealId) {
		List<Integer> cardIds = new ArrayList<Integer>();
		String sql = "select * from tb_card  c ,tb_card_direction_meal_relation d where " + "c.id = d.card_id and "
				+ "d.hospital_id = ? and c.account_id = ? and c.status = 1 and d.meal_id = ? and c.type = 2";
		try {
			List<Map<String, Object>> list = DBMapper.query(sql, hospitalId, account_id, mealId);
			log.info(hospitalId + "..." + account_id + "...." + mealId);
			log.info("list size..." + list.size());
			if (list.size() > 0) {
				for (Map<String, Object> m : list) {
					Integer id = Integer.parseInt(m.get("card_id").toString());
					cardIds.add(id);
				}
			} else
				return null;
		} catch (SqlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return cardIds;
	}

	public static List<Integer> getMultiHosCards(int account_id) {
		List<Integer> cardIds = new ArrayList<Integer>();
		String sql = "SELECT ch.card_id FROM tb_card card " + "LEFT JOIN ( "
				+ "SELECT chr.card_id AS card_id, chr.hospital_id AS hospital_id "
				+ "FROM tb_card_hospital_relation chr " + "UNION "
				+ "SELECT cdmr.card_id AS card_id, cdmr.hospital_id AS hospital_id "
				+ "FROM tb_card_direction_meal_relation cdmr ) ch ON ch.card_id = card.id " + "WHERE card.account_id = "
				+ account_id + " AND card. STATUS = 1 " + "AND card.available_date <= NOW() "
				+ "AND card.expired_date >= NOW() " + "AND ( card.parent_card_id IS NOT NULL OR card.type = 2 ) "
				+ "GROUP BY ch.card_id HAVING COUNT(ch.hospital_id) > 1";
		try {
			List<Map<String, Object>> list = DBMapper.query(sql);
			if (list.size() > 0) {
				for (Map<String, Object> m : list) {
					Integer id = Integer.parseInt(m.get("card_id").toString());
					cardIds.add(id);
				}
			} else
				return null;
		} catch (SqlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return cardIds;
	}

	/**
	 * 
	 * 获取包括有效套餐的虚拟或者实体卡
	 * 
	 * @param hospitalId
	 *            -1为不限制体检中心
	 * @param accountId
	 * @param isVirtual
	 * @param isHidePrice
	 *            是否隐价 普通虚拟卡 : isVirtual=true ,isHidePrice=false
	 *            隐价卡:isVirtual=true ,isHidePrice=true 实体卡:isVirtual=false
	 *            ,isHidePrice=false
	 * @return
	 */
	public static List<Integer> getValidCardByAccountANDHospital(Integer hospitalId, Integer accountId,
			boolean isVirtual, boolean isHidePrice) {
		List<Integer> cardIds = new ArrayList<Integer>();
		String sql = " SELECT DISTINCT c.*, chr.hospital_id " + "FROM tb_card c  "
				+ "LEFT JOIN tb_card_hospital_relation chr ON c.id = chr.card_id "
				+ "LEFT JOIN tb_card_settings se ON c.id = se.card_id " + "WHERE c.account_id = " + accountId
				+ " AND c. STATUS = 1 " + "AND c.expired_date >= NOW() ";
		if (hospitalId != -1)
			sql += " AND chr.hospital_id = " + hospitalId;
		if (isVirtual)
			sql += " AND c.type = 1 AND c.parent_card_id is not null";
		else
			sql += " AND c.type = 2  ";
		if (isHidePrice)
			sql += " AND se.is_show_card_meal_price = 1 AND c.capacity = c.balance ";
		else
			sql += " AND se.is_show_card_meal_price = 0";
		sql += " order by c.id desc";
		List<Map<String, Object>> list = null;
		try {
			list = DBMapper.query(sql);
		} catch (SqlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (list.size() > 0) {
			for (Map<String, Object> m : list) {
				Integer id = Integer.parseInt(m.get("id").toString());
				cardIds.add(id);
			}
		}
		sql = "SELECT DISTINCT c.* " + "FROM tb_card c "
				+ "LEFT JOIN tb_card_direction_meal_relation cdmr ON c.id = cdmr.card_id "
				+ "LEFT JOIN tb_meal m ON m.id = cdmr.meal_id " + "LEFT JOIN tb_card_settings se ON c.id = se.card_id "
				+ "WHERE c.account_id = " + accountId + " AND c. STATUS = 1 AND c.expired_date >= NOW() "
				+ "AND m.`disable` = 0 ";
		if (hospitalId != -1)
			sql += "AND m.hospital_id = " + hospitalId;

		if (isVirtual)
			sql += " AND c.type = 1 AND c.parent_card_id is not null";
		else
			sql += " AND c.type = 2  ";
		if (isHidePrice)
			sql += " AND se.is_show_card_meal_price = 1 AND c.capacity = c.balance  ";
		else
			sql += " AND se.is_show_card_meal_price = 0";
		sql += " order by c.id desc";
		List<Map<String, Object>> list1 = null;
		try {
			list1 = DBMapper.query(sql);
			if (list1.size() > 0) {
				for (Map<String, Object> m : list1) {
					Integer id = Integer.parseInt(m.get("id").toString());
					cardIds.add(id);
				}
			}
		} catch (SqlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (cardIds.size() > 0)
			return cardIds;

		return null;
	}

	/**
	 * 获取账户体检中心所有的卡
	 * 
	 * @param hospitalId
	 * @param accountId
	 * @return
	 */
	public static List<Integer> getCardByAccountANDHospital(Integer hospitalId, Integer accountId) {
		List<Integer> ids = new ArrayList<Integer>();
		List<Integer> virtualCardSize = getCardByAccountANDHospital(hospitalId,-1, -1,accountId, true);
		if (virtualCardSize != null) {
			ids.addAll(virtualCardSize);
		}

		List<Integer> entityCardSize = getCardByAccountANDHospital(hospitalId, -1,-1,accountId, false);
		if (entityCardSize != null) {
			ids.addAll(entityCardSize);
		}

		System.out.println("cards..." + ids);

		return ids;
	}


	/**
	 * 获取账户体检中心某个单位的卡
	 *
	 * @param hospitalId
	 * @param accountId
	 * @return
	 */
	public static List<Integer> getCardByAccountANDHospitalCompany(Integer hospitalId, int companyId,int managerId,Integer accountId) {
		List<Integer> ids = new ArrayList<Integer>();
		List<Integer> virtualCardSize = getCardByAccountANDHospital(hospitalId, companyId,managerId,accountId, true);
		if (virtualCardSize != null) {
			ids.addAll(virtualCardSize);
		}

		List<Integer> entityCardSize = getCardByAccountANDHospital(hospitalId, companyId,managerId,accountId, false);
		if (entityCardSize != null) {
			ids.addAll(entityCardSize);
		}

		System.out.println("cards..." + ids);

		return ids;
	}

	/**
	 * 获取账户的体检中心所有虚拟/实体卡（包括有无效套餐的卡）
	 * 虚拟卡针对可用的，排除已经使用的隐价卡
	 *
	 * @param hospitalId
	 * @param accountId
	 * @param isVirtual
	 * @return
	 */
	public static List<Integer> getCardByAccountANDHospital(Integer hospitalId, Integer companyId,Integer managerId,Integer accountId, boolean isVirtual) {
		List<Integer> cardIds = new ArrayList<Integer>();
		String now = sdf.format(new Date());
		String sql = " SELECT DISTINCT c.*, chr.hospital_id " + "FROM tb_card c "
				+ "LEFT JOIN tb_card_hospital_relation chr ON c.id = chr.card_id " + "WHERE c.account_id = " + accountId
				+ " AND c. STATUS = 1 " + "AND c.expired_date >= '"+now+"'  AND c.batch_id is not null AND chr.hospital_id = " + hospitalId;
		if(companyId != -1)
			sql += " AND c.new_company_id = "+companyId;
		if(managerId != -1)
			sql += " AND c.manager_id = "+managerId;
		if (isVirtual)
			sql += " AND c.type = 1 AND c.parent_card_id is not null AND (c.id in (select card_id from tb_card_settings where is_show_card_meal_price = 0) OR  c.id in (select card_id from tb_card_settings where is_show_card_meal_price = 1 )and c.capacity = c.balance ) ";
		else
			sql += " AND c.type = 2  ";
		log.debug(sql);
		List<Map<String, Object>> list = null;
		try {
			list = DBMapper.query(sql);
		} catch (SqlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (list.size() > 0) {
			for (Map<String, Object> m : list) {
				Integer id = Integer.parseInt(m.get("id").toString());
				cardIds.add(id);
			}
		}
		sql = "SELECT DISTINCT c.* " + "FROM tb_card c "
				+ "LEFT JOIN tb_card_direction_meal_relation cdmr ON c.id = cdmr.card_id "
				+ "LEFT JOIN tb_meal m ON m.id = cdmr.meal_id " + "WHERE c.account_id = " + accountId
				+ " AND c. STATUS = 1 AND c.expired_date >= '"+now + "' AND c.batch_id is not null AND m.hospital_id = " + hospitalId;
		if(companyId != -1)
			sql += " AND c.new_company_id = "+companyId;
		if(managerId != -1)
			sql += " AND c.manager_id = "+managerId;
		if (isVirtual)
			sql += " AND c.type = 1 AND c.parent_card_id is not null AND (c.id in (select card_id from tb_card_settings where is_show_card_meal_price = 0) OR  c.id in (select card_id from tb_card_settings where is_show_card_meal_price = 1 )and c.capacity = c.balance ) ";
		else
			sql += " AND c.type = 2  ";
		log.debug(sql);

		List<Map<String, Object>> list1 = null;
		try {
			list1 = DBMapper.query(sql);
		} catch (SqlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (list1.size() > 0) {
			for (Map<String, Object> m : list1) {
				Integer id = Integer.parseInt(m.get("id").toString());
				cardIds.add(id);
			}
		}

		if (cardIds.size() > 0)
			return cardIds;

		return null;
	}

	public static String generateJson(Integer[] batchIds, Boolean hideZeroCapacityCard, Integer newCompanyId,
			Integer organizationType, String searchKey, String useStatus, Integer currentPage, Integer pageSize,
			Integer rowCount) {
		CardRecordQueryDto cardRecordQueryDto = new CardRecordQueryDto();
		cardRecordQueryDto.setCompanyId(newCompanyId);
		cardRecordQueryDto.setHideZeroCapacityCard(hideZeroCapacityCard);
		cardRecordQueryDto.setNewCompanyId(newCompanyId);
		cardRecordQueryDto.setOrganizationType(organizationType);

		if (batchIds != null) {
			cardRecordQueryDto.setBatchIds(batchIds);
			;
		}
		if (searchKey != null && !searchKey.equals("")) {
			cardRecordQueryDto.setSearchKey(searchKey);
		}

		if (useStatus != null && !useStatus.equals("")) {
			cardRecordQueryDto.setUseStatus(Integer.valueOf(useStatus));
		}

		Page page = new Page();
		page.setCurrentPage(currentPage);
		if (pageSize != null) {
			page.setPageSize(pageSize);
		}
		page.setRowCount(rowCount);
		cardRecordQueryDto.setPage(page);
		String json = JSON.toJSONString(cardRecordQueryDto);
		return json;
	}

	public static HospitalCompany getHospitalCompanyByCardId(Integer cardId, String hosptialIdStr) {
		HospitalCompany hospitalCompany = new HospitalCompany();
		String cardSql = "SELECT * from tb_card where id = ?";
		try {
			List<Map<String, Object>> cardList = DBMapper.query(cardSql, cardId);
			if (cardList.size() > 0 && cardList != null) {
				Map<String, Object> cardMap = cardList.get(0);
				int organization_type = Integer.parseInt(cardMap.get("organization_type").toString());
				int newCompanyId = Integer.parseInt(cardMap.get("new_company_id").toString());
				if (organization_type == OrganizationTypeEnum.HOSPITAL.getCode().intValue()) {
					String sql = "SELECT * FROM tb_hospital_company WHERE id = ?";
					List<Map<String, Object>> list = DBMapper.query(sql, newCompanyId);
					if (list.size() > 0 && list != null) {
						Map<String, Object> m = list.get(0);
						BaseCompany baseCompany = new BaseCompany();
						baseCompany = CompanyChecker.getBaseCompany(m);
						BeanUtils.copyProperties(baseCompany, hospitalCompany);
						hospitalCompany.setShowReport(m.get("show_report").equals(1) ? true : false);
						hospitalCompany.setEmployeeImport(m.get("employee_import").equals(1) ? true : false);
						if (m.get("his_name") != null)
							hospitalCompany.setHisName(m.get("his_name").toString());
						if (m.get("advance_export_order") != null)
							hospitalCompany
									.setAdvanceExportOrder(m.get("advance_export_order").equals(1) ? true : false);
						if (m.get("employee_prefix") != null)
							hospitalCompany.setEmployeePrefix(m.get("employee_prefix").toString());
						if (m.get("examination_address") != null)
							hospitalCompany.setExaminationAddress(m.get("examination_address").toString());
						hospitalCompany.setExamreportIntervalTime(
								Integer.valueOf(m.get("examreport_interval_time").toString()));
						hospitalCompany.setId(Integer.valueOf(m.get("id").toString()));
					}
				} else if (organization_type == OrganizationTypeEnum.CHANNEL.getCode().intValue()) {
					if (hosptialIdStr == null) {
						System.out.println("请确认机构id正确");
						return null;
					}
					int defhospitalId = Integer.parseInt(hosptialIdStr);
					String sql = "select * from tb_hospital_company hc ,tb_channel_company ch where "
							+ "ch.id = ? and ch.platform_company_id = hc.platform_company_id and hc.organization_id = ? ;";
					List<Map<String, Object>> list = DBMapper.query(sql, newCompanyId, defhospitalId);
					if (list.size() > 0 && list != null) {
						Map<String, Object> m = list.get(0);
						BaseCompany baseCompany = new BaseCompany();
						baseCompany = CompanyChecker.getBaseCompany(m);
						BeanUtils.copyProperties(baseCompany, hospitalCompany);
						hospitalCompany.setShowReport(m.get("show_report").equals(1) ? true : false);
						hospitalCompany.setEmployeeImport(m.get("employee_import").equals(1) ? true : false);
						if (m.get("his_name") != null)
							hospitalCompany.setHisName(m.get("his_name").toString());
						if (m.get("advance_export_order") != null)
							hospitalCompany
									.setAdvanceExportOrder(m.get("advance_export_order").equals(1) ? true : false);
						if (m.get("employee_prefix") != null)
							hospitalCompany.setEmployeePrefix(m.get("employee_prefix").toString());
						if (m.get("examination_address") != null)
							hospitalCompany.setExaminationAddress(m.get("examination_address").toString());
						hospitalCompany.setExamreportIntervalTime(
								Integer.valueOf(m.get("examreport_interval_time").toString()));
						hospitalCompany.setId(Integer.valueOf(m.get("id").toString()));
					}
				}
				return hospitalCompany;

			}
		} catch (SqlException e1) {
			// TODO 自动生成的 catch 块
			e1.printStackTrace();
		}
		return null;
	}

	public static List<HospitalCompany> getSupportHospitalCompanyByCardId(Integer cardId) {
		List<HospitalCompany> retList = new ArrayList<HospitalCompany>();
		String cardSql = "SELECT * from tb_card where id = ?";
		try {
			List<Map<String, Object>> cardList = DBMapper.query(cardSql, cardId);
			if (cardList.size() > 0 && cardList != null) {
				Map<String, Object> cardMap = cardList.get(0);
				int organization_type = Integer.parseInt(cardMap.get("organization_type").toString());
				int newCompanyId = Integer.parseInt(cardMap.get("new_company_id").toString());
				if (organization_type == OrganizationTypeEnum.HOSPITAL.getCode().intValue()) {
					String sql = "SELECT * FROM tb_hospital_company WHERE id = ?";
					List<Map<String, Object>> list = DBMapper.query(sql, newCompanyId);
					if (list.size() > 0 && list != null) {
						HospitalCompany hospitalCompany = new HospitalCompany();
						Map<String, Object> m = list.get(0);
						BaseCompany baseCompany = new BaseCompany();
						baseCompany = CompanyChecker.getBaseCompany(m);
						BeanUtils.copyProperties(baseCompany, hospitalCompany);
						hospitalCompany.setShowReport(m.get("show_report").equals(1) ? true : false);
						hospitalCompany.setEmployeeImport(m.get("employee_import").equals(1) ? true : false);
						if (m.get("his_name") != null)
							hospitalCompany.setHisName(m.get("his_name").toString());
						if (m.get("advance_export_order") != null)
							hospitalCompany
									.setAdvanceExportOrder(m.get("advance_export_order").equals(1) ? true : false);
						if (m.get("employee_prefix") != null)
							hospitalCompany.setEmployeePrefix(m.get("employee_prefix").toString());
						if (m.get("examination_address") != null)
							hospitalCompany.setExaminationAddress(m.get("examination_address").toString());
						hospitalCompany.setExamreportIntervalTime(
								Integer.valueOf(m.get("examreport_interval_time").toString()));
						retList.add(hospitalCompany);
					}
				} else if (organization_type == OrganizationTypeEnum.CHANNEL.getCode().intValue()) {
					ChannelCompany channelCompany = CompanyChecker.getChannelCompanyByCompanyId(newCompanyId);
					int platCompanyId = channelCompany.getPlatformCompanyId();
					int channelId = channelCompany.getOrganizationId();
					if (platCompanyId == 3) {// M单位
						List<Hospital> hosptialS = HospitalChecker.getHospitalByOrganizationId(channelId);
						for (Hospital h : hosptialS) {
							HospitalCompany hc = CompanyChecker.getHospitalCompanyByPlatCompanyIdANDOrganizationId(3,
									h.getId());
							retList.add(hc);
						}

					} else {// P单位
						String sql = "select hc.* from tb_hospital_company hc ,tb_channel_company ch where "
								+ "ch.id = ? and ch.platform_company_id = hc.platform_company_id and hc.platform_company_id = ? ";
						List<Map<String, Object>> list = DBMapper.query(sql, newCompanyId, platCompanyId);
						for (Map<String, Object> m : list) {
							HospitalCompany hospitalCompany = new HospitalCompany();
							BaseCompany baseCompany = new BaseCompany();
							baseCompany = CompanyChecker.getBaseCompany(m);
							BeanUtils.copyProperties(baseCompany, hospitalCompany);
							hospitalCompany.setShowReport(m.get("show_report").equals(1) ? true : false);
							hospitalCompany.setEmployeeImport(m.get("employee_import").equals(1) ? true : false);
							if (m.get("his_name") != null)
								hospitalCompany.setHisName(m.get("his_name").toString());
							if (m.get("advance_export_order") != null)
								hospitalCompany
										.setAdvanceExportOrder(m.get("advance_export_order").equals(1) ? true : false);
							if (m.get("employee_prefix") != null)
								hospitalCompany.setEmployeePrefix(m.get("employee_prefix").toString());
							if (m.get("examination_address") != null)
								hospitalCompany.setExaminationAddress(m.get("examination_address").toString());
							hospitalCompany.setExamreportIntervalTime(
									Integer.valueOf(m.get("examreport_interval_time").toString()));
							hospitalCompany.setOrganizationId(Integer.parseInt(m.get("organization_id").toString()));
							hospitalCompany.setOrganizationName(m.get("organization_name").toString());
							retList.add(hospitalCompany);

						}

					}
				}
				return retList;

			}
		} catch (SqlException e1) {
			// TODO 自动生成的 catch 块
			e1.printStackTrace();
		}
		return null;
	}

	// 对应体检中心的体检卡
	public static List<Card> getCardByHospital(String hospitalId, int accountId, String status, List<Card> cards)
			throws SqlException, ParseException {
		//获取该体检中心能够使用的额所有体检卡
		String sql = "select DISTINCT card_id from tb_card_direction_meal_relation where card_id in (select id from tb_card where account_id = ? and status in ( "+status+"))"
				+ " and hospital_id = ?";
		//隐价卡若过期，则过滤掉
		List<Map<String, Object>> directionCardList = DBMapper.query(sql, accountId, hospitalId);
		List<Card> retCardList = new ArrayList<>();
		if(directionCardList != null && directionCardList.size()>0){
			for(Map<String,Object> directionCard:directionCardList){
				Card card = getCardInfo(Integer.parseInt(directionCard.get("card_id").toString()));
				if(card.getExpiredDate().compareTo(new Date()) == -1 && card.getCardSetting().isShowCardMealPrice().booleanValue())//只有隐价卡已过期不显示，其他卡过期显示
					continue;
				if(card.getCardSetting().isShowCardMealPrice() && card.getCapacity().intValue() != card.getBalance().intValue())//隐价卡已使用
					continue;
				retCardList.add(card);
			}
		}
		return retCardList;
	}


	// 对应体检中心的可以正常使用的体检卡
	public static List<Card> getCardByHospitalCanUse(String hospitalId, int accountId, String status)
			throws SqlException, ParseException {
		//获取该体检中心能够使用的额所有体检卡
		String sql = "select DISTINCT card_id from tb_card_direction_meal_relation where card_id in (select id from tb_card where account_id = ? and status in ( "+status+"))"
				+ " and hospital_id = ? order by card_id desc ";
		log.info(sql);
		//隐价卡若过期，则过滤掉
		List<Map<String, Object>> directionCardList = DBMapper.query(sql, accountId, hospitalId);
		List<Card> retCardList = new ArrayList<>();
		if(directionCardList != null && directionCardList.size()>0){
			for(Map<String,Object> directionCard:directionCardList){
				Card card = getCardInfo(Integer.parseInt(directionCard.get("card_id").toString()));
				if(card.getExpiredDate().compareTo(new Date()) == -1 )
					continue;
				if(card.getCardSetting().isShowCardMealPrice() && card.getCapacity().intValue() != card.getBalance().intValue())//隐价卡已使用
					continue;
				retCardList.add(card);
			}
		}
		return retCardList;
	}

	/**
	 * 查询卡支持的体检中心
	 * 
	 * @param cardId
	 * @return
	 * @throws SqlException
	 */
	public static List<Integer> getHospitalIdByCardId(int cardId) throws SqlException {
		String sql = "select hospital_id from tb_card_hospital_relation where card_id=?";
		List<Map<String, Object>> list1 = DBMapper.query(sql, cardId);
		List<Integer> hspIdList1 = new ArrayList<>();
		for (int i = 0; i < list1.size(); i++) {
			hspIdList1.add(Integer.valueOf(list1.get(i).get("hospital_id").toString()));
		}

		sql = "select hospital_id from tb_card_direction_meal_relation where card_id=?";
		List<Map<String, Object>> list2 = DBMapper.query(sql, cardId);
		List<Integer> hspIdList2 = new ArrayList<>();
		for (int i = 0; i < list2.size(); i++) {
			hspIdList2.add(Integer.valueOf(list2.get(i).get("hospital_id").toString()));
		}

		// 过滤相同的id
		Set<Integer> hspIdSet = Sets.newHashSet();
		hspIdSet.addAll(hspIdList1);
		hspIdSet.addAll(hspIdList2);

		return Lists.newArrayList(hspIdSet);
	}
	
	/**
	 * 根据tb_card_hospital_relation获取医院
	 * @param cardId
	 * @return
	 */
	public static List<Integer> getHospitalIdByCardHosRela(Integer cardId){
		List<Integer> hospitalIds = new ArrayList<>();
		String sql = "select hospital_id from tb_card_hospital_relation where card_id=?";
		List<Map<String, Object>> list = null;
		try {
			list = DBMapper.query(sql, cardId);
		} catch (SqlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(list!=null&&!list.isEmpty()) {
			for(Map<String,Object> m : list) {
				Integer hospitalId = Integer.valueOf(m.get("hospital_id").toString());
				hospitalIds.add(hospitalId);
			}
		}
		return hospitalIds;
	}

	public static List<Card> directionCardList(List<Map<String, Object>> officalCardList,
			List<Map<String, Object>> directionCardList) throws ParseException {
		List<Map<String, Object>> list = new ArrayList<>();

		for (Map<String, Object> offCard : officalCardList) {
			if (!inDirectionCardList(offCard, directionCardList)) {
				list.add(offCard);
			}
		}

		// 去重后，把所有的卡都放到directionCardList中
		if (CollectionUtils.isNotEmpty(list)) {
			directionCardList.addAll(list);
		}

		List<Card> directionCardLists = new ArrayList<>();
		for (Map<String, Object> map : directionCardList) {
			Card card = initCard(map);
			directionCardLists.add(card);
		}

		// 排序
		if (CollectionUtils.isNotEmpty(directionCardLists)) {
			Collections.sort(directionCardLists, new Comparator<Card>() {

				public int compare(Card card, Card card2) {
					Long rechTime = (card.getRechargeTime() == null) ? 0 : card.getRechargeTime().getTime();
					Long rechTime2 = (card2.getRechargeTime() == null) ? 0 : card2.getRechargeTime().getTime();
					int res = card2.getBalance().compareTo(card.getBalance());
					if (res == 0) {
						return rechTime2.compareTo(rechTime);
					} else {
						return res;
					}
				}
			});
		}
		return directionCardLists;
	}

	// 验证directionCardList中是否包含offCard
	public static boolean inDirectionCardList(Map<String, Object> offCard,
			List<Map<String, Object>> directionCardList) {
		if (CollectionUtils.isNotEmpty(directionCardList)) {
			for (Map<String, Object> dirCard : directionCardList) {
				if (offCard.get("id").toString().equals(dirCard.get("id").toString())) {
					return true;
				}
			}
		}
		return false;
	}

	private static Card initCard(Map<String, Object> map) throws ParseException {
		Card card = new Card();
		card.setId(Integer.valueOf(map.get("id").toString()));
		if (map.get("batch_id") != null)
			card.setBatchId(Integer.valueOf(map.get("batch_id").toString()));
		card.setCardName(map.get("card_name").toString());
		card.setCardNum(map.get("card_num").toString());
		card.setPassword(map.get("PASSWORD") == null ? null : map.get("PASSWORD").toString());
		card.setCapacity(Long.valueOf(map.get("capacity").toString()));
		card.setBalance(Long.valueOf(map.get("balance").toString()));
		card.setRecoverableBalance(map.get("recoverable_balance") == null ? null
				: Long.valueOf(map.get("recoverable_balance").toString()));
		card.setType(Integer.valueOf(map.get("type").toString()));
		card.setStatus(Integer.valueOf(map.get("status").toString()));
		card.setFromHospital(
				map.get("from_hospital") == null ? null : Integer.valueOf(map.get("from_hospital").toString()));
		card.setRechargeTime(map.get("recharge_time") == null ? null
				: DateUtils.parse("yyyy-MM-dd HH:mm:ss", map.get("recharge_time").toString()));
		if (map.get("parent_card_id") != null) {
			card.setParentCardId(Integer.valueOf(map.get("parent_card_id").toString()));
		}

		card.setAvailableDate(map.get("available_date") == null ? null
				: DateUtils.parse("yyyy-MM-dd HH:mm:ss", map.get("available_date").toString()));
		card.setExpiredDate(map.get("expired_date") == null ? null
				: DateUtils.parse("yyyy-MM-dd HH:mm:ss", map.get("expired_date").toString()));
		card.setCreateDate(map.get("create_date") == null ? null
				: DateUtils.parse("yyyy-MM-dd HH:mm:ss", map.get("create_date").toString()));
		card.setAccountId(map.get("account_id") != null ? Integer.valueOf(map.get("account_id").toString()) : null);
		return card;
	}

	public static List<Map<String, Object>> getOfficalCardListByHospitalId(int hospitalId, int accountId, String status)
			throws SqlException {
		String officalCardSql = "select  DISTINCT tb_card.id, tb_card.card_name, tb_card.card_num, tb_card.password, tb_card.capacity, tb_card.balance, tb_card.type, tb_card.status, tb_card.from_hospital, tb_card.parent_card_id, tb_card.recharge_time, tb_card.available_date, tb_card.expired_date, tb_card.account_id,tb_card.batch_id from tb_card left join tb_manager_card_relation on tb_card.id = tb_manager_card_relation.card_id left join tb_card_hospital_relation on tb_card.id = tb_card_hospital_relation.card_id where tb_card.account_id = ? and tb_card_hospital_relation.hospital_id = ? and status in  ("
				+ status
				+ ") and tb_card.available_date <=  NOW() and tb_card.expired_date >= NOW() and (tb_card.parent_card_id is not null or tb_card.type=2)";
		List<Map<String, Object>> officalCardList = DBMapper.query(officalCardSql, accountId, hospitalId);
		return officalCardList;
	}

	public static List<Map<String, Object>> getDirectionCardListByHospitalId(int hospitalId, int accountId,
			String status) throws SqlException {
		String directionCardSql = "select distinct tb_card.id, batch_id, card_name, card_num, password,capacity,balance, recoverable_balance, tb_card.type, status, from_hospital, recharge_time, parent_card_id, available_date, expired_date,create_date, tb_card.account_id from tb_card left join tb_card_direction_meal_relation on tb_card.id = tb_card_direction_meal_relation.card_id where status in ("
				+ status
				+ ") and available_date <= NOW() and expired_date >= NOW() and account_id = ? and tb_card_direction_meal_relation.hospital_id = ? and (tb_card.parent_card_id is not null or tb_card.type=2)";
		List<Map<String, Object>> directionCardList = DBMapper.query(directionCardSql, accountId, hospitalId);
		return directionCardList;
	}

	public static List<Card> getCardListByAccountId(int accountId, String status) throws SqlException, ParseException {
		String now = sdf.format(new Date());
		String cardSql = "SELECT tb_card.id, batch_id, card_name, card_num, PASSWORD, capacity, balance, recoverable_balance, tb_card.type, STATUS, from_hospital, recharge_time, parent_card_id, available_date, expired_date, create_date, tb_card.account_id FROM tb_card WHERE STATUS IN ( "
				+ status + " ) AND account_id = ? and expired_date >= '"+now+"' ORDER BY balance DESC, expired_date";
		List<Map<String, Object>> list = DBMapper.query(cardSql, accountId);

		List<Card> cardList = new ArrayList<>();
		for (Map<String, Object> map : list) {
			Card card = initCard(map);
			cardList.add(card);
		}
		return cardList;
	}

	/**
	 * 获取实体卡支持的套餐
	 * 
	 * @param cardId
	 * @param gender
	 * @return
	 * @throws SqlException
	 */
	public static List<Integer> getEntityCardMeal(int cardId, int gender) throws SqlException {
		List<Integer> idlist = new ArrayList<Integer>();
		String sql = "select m.id from tb_meal m ,tb_card_direction_meal_relation d where m.id = d.meal_id  and m.gender = ? and d.card_id = ?";
		List<Map<String, Object>> newlist = DBMapper.query(sql, gender, cardId);
		for (Map<String, Object> m : newlist) {
			idlist.add(Integer.parseInt(m.get("id").toString()));
		}

		if (newlist.isEmpty()) {
			// 从tb_card_hospital_relation获取套餐
			sql = "SELECT m.* FROM tb_meal m "
					+ "LEFT JOIN tb_card_hospital_relation ch ON ch.hospital_id = m.hospital_id "
					+ "LEFT JOIN tb_card c ON c.id = ch.card_id " + "WHERE   c.id = ? "
					+ "AND m.gender <> 2 " + "AND m.type = 3 " + "AND m. DISABLE = 0 ";
			try {
				newlist = DBMapper.query(sql, cardId);
				System.out.println("tb_card_hospital_relation:" + sql);
			} catch (SqlException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (newlist.size() > 0 && newlist != null) {
			for (Map<String, Object> m : newlist) {
				Integer mealId = Integer.valueOf(m.get("id").toString());
				idlist.add(mealId);
			}
		}
		return idlist;
	}

	public static Card getCardById(Integer cardId) {
		// TODO Auto-generated method stub
		String sql = "SELECT * FROM tb_card WHERE id = ?;";
		List<Map<String, Object>> list = null;
		try {
			list = DBMapper.query(sql, cardId);
		} catch (SqlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (list.isEmpty())
			System.out.println("卡不存在：" + cardId);
		else
			try {
				Card card = initCard(list.get(0));
				return card;
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		return null;
	}

	/**
	 * 获取操作员的母卡
	 * 
	 * @param managerId
	 * @return
	 * @throws ParseException
	 */
	public static Card getParentCardByManagerId(Integer managerId) throws ParseException {
		String sql = "select * from tb_card where parent_card_id is null and type = 1  and account_id = " + managerId;
		List<Map<String, Object>> list = null;
		try {
			list = DBMapper.query(sql);
			if (list != null && list.size() > 0) {
				int cardId = Integer.parseInt(list.get(0).get("id").toString());
				sql = "select * from tb_card where id = "+cardId;
				List<Map<String, Object>> dblist = DBMapper.query(sql);
				if (dblist.size() < 1) {
					log.error("卡不存在" + cardId);
					return null;
				}
				Map<String, Object> dbMap = dblist.get(0);
				Card c = new Card();
				c.setId(cardId);
				c.setAccountId(Integer.parseInt(dbMap.get("account_id").toString()));
				c.setCapacity(Long.parseLong(dbMap.get("capacity").toString()));
				c.setBalance(Long.parseLong(dbMap.get("balance").toString()));
				if (dbMap.get("recoverable_balance") != null)
					c.setRecoverableBalance((Long.parseLong(dbMap.get("recoverable_balance").toString())));
				else
					c.setRecoverableBalance(0l);
				c.setType(Integer.parseInt(dbMap.get("type").toString()));
				c.setStatus(Integer.parseInt(dbMap.get("status").toString()));
				if (dbMap.get("from_hospital") != null)
					c.setFromHospital(Integer.parseInt(dbMap.get("from_hospital").toString()));
				if (dbMap.get("parent_card_id") != null)
					c.setParentCardId(Integer.parseInt(dbMap.get("parent_card_id").toString()));
				c.setRechargeTime(simplehms.parse(dbMap.get("recharge_time").toString()));
				c.setAvailableDate(simplehms.parse((dbMap.get("available_date").toString())));
				c.setExpiredDate(simplehms.parse(dbMap.get("expired_date").toString()));
				c.setCreateDate(simplehms.parse(dbMap.get("create_date").toString()));
				c.setCardName(dbMap.get("card_name").toString());
				c.setCardNum(dbMap.get("card_num").toString());
				if(dbMap.get("manager_id") != null)
					c.setManagerId(Integer.parseInt(dbMap.get("manager_id").toString()));
				if (dbMap.get("new_company_id") != null)
					c.setNewCompanyId(Integer.valueOf(dbMap.get("new_company_id").toString()));
				if (dbMap.get("organization_type") != null)
					c.setOrganizationType(Integer.valueOf(dbMap.get("organization_type").toString()));
				if (dbMap.get("password") != null)
					c.setPassword(dbMap.get("password").toString());
				if (dbMap.get("batch_id") != null)
					c.setBatchId(Integer.parseInt(dbMap.get("batch_id").toString()));
				return c ;
			}

		} catch (SqlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 判断卡是否结算
	 * 
	 * @param order_num
	 * @return
	 * @throws ParseException
	 * @throws SqlException
	 */
	public static boolean isCardInSettlement(int cardId) throws SqlException, ParseException {
		Card card = getCardInfo(cardId);
		if (card != null) {
			int status = card.getHospitalSettlementStatus();
			if (status == SettlementHospitalConfirmEnum.SETTLEMENT_CANCELED.getCode()
					|| status == SettlementHospitalConfirmEnum.UNSETTLEMENT.getCode())
				return false;
		}

		// 老的订单不在结算关系表
		return false;
	}

	/**
	 * 获取实体卡号段
	 * 
	 * @param num
	 * @return
	 */
	public static Map<String, String> getEntityCardNum(int num) {
		Map<String, String> map = new HashMap<String, String>();
		String startCardNum = null;
		String endCardNum = null;
		String sql = "SELECT * FROM tb_card_segment ORDER BY id desc limit ?";
		List<Map<String, Object>> dblist1 = null;
		try {
			dblist1 = DBMapper.query(sql, num);
		} catch (SqlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		startCardNum = dblist1.get(dblist1.size() - 1).get("card_num").toString();
		endCardNum = dblist1.get(0).get("card_num").toString();
		map.put("startCardNum", startCardNum);
		map.put("endCardNum", endCardNum);
		return map;
	}

	/**
	 * 根据查询条件获取卡列表
	 * 
	 * @param query
	 * @param isEntity
	 * @return
	 */
	public static List<CardRecordDto> getCardRecordsByQuery(CardRecordQueryDto query, Boolean isEntity) {
		List<CardRecordDto> cardRecords = new ArrayList<>();
		String sql = CardRecordQueryDto2Sql(query, isEntity);
		System.out.println("card query:\n" + sql);
		List<Map<String, Object>> list = null;
		try {
			list = DBMapper.query(sql);
		} catch (SqlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (list != null && !list.isEmpty()) {
			for (Map<String, Object> m : list) {
				CardRecordDto dto = new CardRecordDto();
				Card card = map2Card(m);
				if (card.getAccountId() != null) {
					AcctRelationQueryDto accQuery = new AcctRelationQueryDto();
					accQuery.setManagerId(card.getManagerId());
					accQuery.setNewCompanyId(card.getNewCompanyId());
					accQuery.setCustomerId(card.getAccountId());
					Examiner accountRelation = AccountChecker.getAccRelation(accQuery).get(0);
					dto.setAccount(accountRelation);
				}
				dto.setCard(card);
				if (query.getSearchKey() != null && dto.getAccount() != null) {
					if (dto.getAccount().getName().indexOf(query.getSearchKey()) != -1
							|| (dto.getAccount().getIdCard() != null
									&& dto.getAccount().getIdCard().indexOf(query.getSearchKey()) != -1)
							|| (dto.getAccount().getMobile() != null
									&& dto.getAccount().getMobile().indexOf(query.getSearchKey()) != -1)) {
						cardRecords.add(dto);
					} else
						continue;
				} else if (query.getSearchKey() == null)
					cardRecords.add(dto);
			}
		}
		return cardRecords;
	}

	public static String CardRecordQueryDto2Sql(CardRecordQueryDto query, Boolean isEntity) {
		String sql = "SELECT * FROM tb_card WHERE ";
		if (isEntity != null) {
			int entity = isEntity ? 2 : 1;
			sql = sql + "type = " + entity + " ";
		}
		if (query.getNewCompanyId() != null) {
			sql = sql + "AND new_company_id = " + query.getNewCompanyId() + " ";
		}
		if (query.getCardBalanceIsZero() != null) {
			if (query.getCardBalanceIsZero())
				sql = sql + "AND balance = 0 ";
			else
				sql = sql + "AND balance > 0 ";
		}
		if (query.getCardCapacityIsZero() != null) {
			if (query.getCardCapacityIsZero())
				sql = sql + "AND capacity = 0 ";
			else
				sql = sql + "AND capacity > 0 ";
		}
		if (query.getExpired() != null) {
			String nowStr = simplehms.format(new Date());
			if (query.getExpired()) {
				sql = sql + "AND expired_date < \"" + nowStr + "\" ";
			}
		}
		if (query.getAccountIds() != null && !query.getAccountIds().isEmpty()) {
			String accountIds = ListUtil.SetsToString(query.getAccountIds());
			sql = sql + "AND account_id in (" + accountIds + ") ";
		}
		if (query.getBatchIds() != null) {
			String batchIds = ListUtil.IntegerArraysToString(query.getBatchIds());
			sql = sql + "AND batch_id in (" + batchIds + ") ";
		}
		if (query.getBindStatus() != null) {
			if (query.getBindStatus().intValue() == 1)
				sql = sql + "AND account_id is not null ";
			else
				sql = sql + "AND account_id is null ";
		}
		if (query.getCardNum() != null) {
			sql = sql + "AND card_num like CONCAT('%',\"" + query.getCardNum() + "\",'%')";
		}
		if (query.getCreateTimeEnd() != null) {
			String endStr = simplehms.format(query.getCreateTimeEnd());
			sql = sql + "AND create_date <= \"" + endStr + "\" ";
		}
		if (query.getCreateTimeStart() != null) {
			String startStr = simplehms.format(query.getCreateTimeStart());
			sql = sql + "AND create_date >= \"" + startStr + "\" ";
		}
		if (query.getFromHospital() != null) {
			sql = sql + "AND from_hospital = " + query.getFromHospital() + " ";
		}
		if (query.getManagerId() != null) {
			sql = sql + "AND manager_id = " + query.getManagerId() + " ";
		}
		if (query.getOrganizationType() != null) {
			sql = sql + "AND organization_type = " + query.getOrganizationType() + " ";
		}
		if (query.getUseStatus() != null) {
			if (query.getUseStatus().intValue() == 0)
				sql = sql + "AND balance = capacity ";
			else
				sql = sql + "AND balance < capacity ";
		}
		if (query.getStatus() != null) {
			sql = sql + "AND status = " + query.getStatus() + " ";
		}
		sql = sql + " order by id desc";
		return sql;
	}

	public static Card map2Card(Map<String, Object> m) {
		Card card = new Card();
		card.setId(Integer.valueOf(m.get("id").toString()));
		card.setAccountId(m.get("account_id") != null ? Integer.valueOf(m.get("account_id").toString()) : null);
		card.setBalance(Long.valueOf(m.get("balance").toString()));
		card.setBatchId(Integer.valueOf(m.get("batch_id").toString()));
		card.setCapacity(Long.valueOf(m.get("capacity").toString()));
		card.setCardName(m.get("card_name").toString());
		card.setCardNum(m.get("card_num").toString());
		try {
			card.setCreateDate(simplehms.parse(m.get("create_date").toString()));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		card.setExamNoteId(m.get("exam_note_id") != null ? Integer.valueOf(m.get("exam_note_id").toString()) : null);
		try {
			card.setExpiredDate(simplehms.parse(m.get("expired_date").toString()));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		card.setFreezeBalance(Long.valueOf(m.get("freeze_balance").toString()));
		card.setFromHospital(
				m.get("from_hospital") != null ? Integer.valueOf(m.get("from_hospital").toString()) : null);
		card.setHospitalSettlementStatus(Integer.valueOf(m.get("hospital_settlement_status").toString()));
		card.setIsDeleted(Integer.valueOf(m.get("is_deleted").toString()));
		card.setManagerId(Integer.valueOf(m.get("manager_id").toString()));
		card.setNewCompanyId(Integer.valueOf(m.get("new_company_id").toString()));
		card.setOrganizationType(Integer.valueOf(m.get("organization_type").toString()));
		card.setParentCardId(
				m.get("parent_card_id") != null ? Integer.valueOf(m.get("parent_card_id").toString()) : null);
		card.setPassword(m.get("password") != null ? m.get("password").toString() : null);
		try {
			card.setRechargeTime(simplehms.parse(m.get("recharge_time").toString()));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		card.setRecoverableBalance(
				m.get("recoverable_balance") == null ? null : Long.valueOf(m.get("recoverable_balance").toString()));
		card.setSettlementBatchSn(
				m.get("settlement_batch_sn") != null ? m.get("settlement_batch_sn").toString() : null);
		card.setStatus(Integer.valueOf(m.get("status").toString()));
		card.setTradeAccountId(
				m.get("trade_account_id") != null ? Integer.valueOf(m.get("trade_account_id").toString()) : null);
		card.setType(Integer.valueOf(m.get("type").toString()));
		return card;
	}

	public static List<Card> getCardsByIds(List<Integer> cardIds) {
		List<Card> cards = new ArrayList<>();
		String ids = ListUtil.IntegerlistToString(cardIds);
		String sql = "SELECT * FROM tb_card WHERE id in (" + ids + ");";
		List<Map<String, Object>> list = null;
		try {
			list = DBMapper.query(sql);
		} catch (SqlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (list != null && !list.isEmpty()) {
			for (Map<String, Object> m : list) {
				Card card = map2Card(m);
				cards.add(card);
			}
		}
		return cards;
	}

	public static List<CardOperateLog> getCardOperateLogsByCardId(Integer cardId) {
		List<CardOperateLog> logs = new ArrayList<>();
		String sql = "SELECT * FROM tb_card_operate_log WHERE card_id = ? order by gmt_modified desc;";
		List<Map<String, Object>> list = null;
		try {
			list = DBMapper.query(sql, cardId);
		} catch (SqlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (list != null && !list.isEmpty())
			for (Map<String, Object> m : list) {
				CardOperateLog log = new CardOperateLog();
				log.setId(Integer.valueOf(m.get("id").toString()));
				log.setCardId(Integer.valueOf(m.get("card_id").toString()));
				log.setCardStatus(Integer.valueOf(m.get("card_status").toString()));
				log.setContent(m.get("content").toString());
				try {
					log.setGmtCreated(simplehms.parse(m.get("gmt_modified").toString()));
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				log.setOperateType(Integer.valueOf(m.get("operate_type").toString()));
				log.setOperatorId(Integer.valueOf(m.get("operator_id").toString()));
				logs.add(log);
			}
		return logs;
	}

	/**
	 * 获取指定时间之后的卡操作日志
	 * 
	 * @param cardId
	 * @param date
	 * @return
	 */
	public static List<CardOperateLog> getCardOperateLogsByCardIdAndCreateTime(Integer cardId, Date date) {
		List<CardOperateLog> logs = new ArrayList<>();
		logs = getCardOperateLogsByCardId(cardId).stream().filter(d -> d.getGmtCreated().after(date))
				.collect(Collectors.toList());
		return logs;
	}

	/**
	 * C端可用的体检卡列表
	 * @return
	 */
	public static List<Integer> getUserCanUseCardList(String site, int accountId,String status) throws SqlException, ParseException {
		List<Hospital> hs = HospitalChecker.getSupportHospitalListBySite(site,false);
		Set<Integer> tcards = new HashSet<>();
		if(hs != null && hs.size()>0){
			for(Hospital h : hs){
				log.debug("hospital_id..."+h.getId());
				List<Integer> cards1 = CardChecker.getCardByAccountANDHospital(h.getId(),accountId);
				tcards.addAll(cards1);
			}
		}else{
			List<Card> cards1 = CardChecker.getCardListByAccountId(accountId,status);
			for(Card c : cards1)
				tcards.add(c.getId());
		}

		List<Integer> cards = ListUtil.SetsToLists(tcards);
		log.info("最后卡列表..."+cards);
		return cards;
	}

	/**
	 * 根据母卡ID获取母卡在REDIS中的金额
	 * @param parentCardId
	 * @return
	 */
	public static long getParentBalanceFromRedis(int parentCardId){
		Jedis jedis = RedisUtils.jedis;
		String key = "parent_card_number_balance_"+parentCardId;
		String value = jedis.get(key);
		if(value != null)
			return Long.parseLong(value);
		else
			return 0;
	}
}
