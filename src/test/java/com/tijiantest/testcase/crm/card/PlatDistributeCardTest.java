package com.tijiantest.testcase.crm.card;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.jayway.jsonpath.JsonPath;
import com.tijiantest.base.Flag;
import com.tijiantest.base.HttpResult;
import com.tijiantest.base.MyHttpClient;
import com.tijiantest.base.dbcheck.AccountChecker;
import com.tijiantest.base.dbcheck.CardChecker;
import com.tijiantest.base.dbcheck.CompanyChecker;
import com.tijiantest.base.dbcheck.HospitalChecker;
import com.tijiantest.model.account.FileAccountImportInfo;
import com.tijiantest.model.account.ModifyAccountType;
import com.tijiantest.model.card.Card;
import com.tijiantest.model.card.CardDto;
import com.tijiantest.model.card.CardRecordDto;
import com.tijiantest.model.card.CardSetting;
import com.tijiantest.model.card.CardStatusEnum;
import com.tijiantest.model.card.CardTypeEnum;
import com.tijiantest.model.company.ChannelCompany;
import com.tijiantest.model.hospital.Hospital;
import com.tijiantest.model.hospital.HostpitalMealDto;
import com.tijiantest.model.organization.OrganizationTypeEnum;
import com.tijiantest.model.resource.meal.Meal;
import com.tijiantest.model.settlement.SettlementHospitalConfirmEnum;
import com.tijiantest.testcase.crm.CrmMediaBase;
import com.tijiantest.util.CvsFileUtil;
import com.tijiantest.util.DateUtils;
import com.tijiantest.util.IdCardGeneric;
import com.tijiantest.util.db.DBMapper;
import com.tijiantest.util.db.SqlException;

public class PlatDistributeCardTest extends CrmMediaBase {

	// 卡数量
	private static int beforenum = 0;
	private static int afternum = 0;
	// 获取母卡信息
	int parentCardId = CardChecker.getParentEntryCard(defPlatAccountId);
	String parentCardPrice;

	// 用户账号ID
	private static List<Integer> accountIds = new ArrayList<Integer>();

	// 平台客户经理发卡列表
	protected static List<Card> cardList2 = new ArrayList<Card>();
	protected static MyHttpClient platclient = new MyHttpClient();

	static{
		onceLoginInSystem(platclient, Flag.CRM,defPlatUsername,defPlatPasswd);
	}
	
	@Test(description = "平台客户经理发卡,自定义分配套餐", groups = { "qa" }, dataProvider = "distributeCard_plat")
	public void test_01_distributeCard(String... args) throws SqlException {
		System.out.println("=======================平台客户经理登录:" + defPlatAccountId + "=======================");
		// 平台客户经理登陆
		String parentCardSql = "select * from tb_card where id=?";
		List<Map<String, Object>> parentCardList = DBMapper.query(parentCardSql, parentCardId);
		parentCardPrice = parentCardList.get(0).get("balance").toString();
		// 获取平台客户经理支持的体检单位,true - 平台客户经理
		List<Integer> companyIdList = AccountChecker.getCompanysIdByManagerId(platclient,defPlatAccountId, true);
		for (int i = 0; i < companyIdList.size(); i++) {
			System.out.println(companyIdList.get(i));
		}
		if (companyIdList.size()==1) {
			//如果等于1，说明只有一个散客单位
			log.info("只有一个散客单位");
			return;
		}
		
		//取除散客单位外的第二个单位
		int companyId = companyIdList.get(1);
		log.info("-----------单位ID:" + companyId);

		// 根据companyId, 获取支持的体检中心
		List<NameValuePair> params = new ArrayList<>();
		params.add(new BasicNameValuePair("companyId", companyId + ""));
		HttpResult result = platclient.get(Hos_Hospital, params);

		Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);
		List<Hospital> hospitalList = JSON.parseArray(result.getBody(), Hospital.class);

		List<Meal> officalMeal = new ArrayList<>();
		// 取所有体检中心的第一个可用的官方套餐
		for (int j = 0; j < hospitalList.size(); j++) {
			List<NameValuePair> pairs = new ArrayList<>();
			pairs.add(new BasicNameValuePair("filterBasicMeal", "true"));
			pairs.add(new BasicNameValuePair("orderBy", "updateTime"));

			result = platclient.get(Card_Officialmeals, pairs, hospitalList.get(j).getId() + "");
			String body = result.getBody();
//			System.out.println(body);
			Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);
			
			List<Meal> meals = JSON.parseObject(body, new TypeReference<List<Meal>>() {
			});
			Meal meal = new Meal();
			for (int i = 0; i < meals.size(); i++) {
				if (meals.get(i).getDisable() == 1) {
					continue;
				}

				meal = meals.get(i);
				officalMeal.add(meal);
				break;
			}
		}

		// mealid,mealprice,parentcardid,13516,5500,33049
		// 账号id列表
		accountIds = new ArrayList<Integer>();
		accountIds.add(defCompanyAccountId);
		long capacity = Long.parseLong(args[2]);
		String cardName = args[3];

		// int mealId = meal.getId();
		// int mealPrice = meal.getPrice();
		Date expireDate = DateUtils.offsetDay(30);
		String isprivate = args[7];
		String showCardMealPrice = args[8];

		// 卡设置
		CardSetting settings = new CardSetting();
		settings.setCardId(parentCardId);
		if (isprivate != null && !isprivate.equals("")) {
			settings.setPrivate(Boolean.valueOf(isprivate));
		}
		if (showCardMealPrice != null && !showCardMealPrice.equals("")) {
			settings.setShowCardMealPrice(Boolean.valueOf(showCardMealPrice));
		}

		CardDto cdt = new CardDto();
		int organizationId = AccountChecker.getOrganizationIdByPlatManagerId(defPlatAccountId);
		ChannelCompany channelCompany = CompanyChecker.getChannelCompanyByCompanyId(companyId,organizationId);
		log.info("companyId..." + companyId);
		int organizationType = HospitalChecker.getOrganizationType(channelCompany.getOrganizationId());
		for (int i = 0; i < officalMeal.size(); i++) {
			int mealId = officalMeal.get(i).getId();
			int mealPrice = officalMeal.get(i).getPrice();

			// 卡信息
			Card card = new Card();
			if (showCardMealPrice.equals("true") && isprivate.equals("true")) {
				capacity = mealPrice;
			}
			card.setCapacity(capacity);

			card.setCardName(cardName);
			card.setCardSetting(settings);
			card.setExpiredDate(expireDate);
			card.setId(parentCardId);
			// 卡绑定套餐
			HostpitalMealDto hmd = new HostpitalMealDto();
			String sql = "select * from tb_meal where id=?";
			List<Map<String, Object>> mealList = DBMapper.query(sql, mealId);
			hmd.setHospitalId(Integer.valueOf(mealList.get(0).get("hospital_id").toString()));
			hmd.setIsMealForSelf(true);
			hmd.setIsMealForFamily(false);
			hmd.setMealId(mealId);
			hmd.setMealPrice(mealPrice);
			List<HostpitalMealDto> cardMealList = new ArrayList<HostpitalMealDto>();
			cardMealList.add(hmd);

			cdt.setAccountIdList(accountIds);
			cdt.setCard(card);
			cdt.setCardMealList(cardMealList);
			cdt.setCompanyId(companyId);

			cdt.setNewCompanyId(companyId);
			cdt.setOrganizationType(2);
			cdt.setIsSendBookingMsg(false);
			cdt.setIsSendCardMsg(false);
		}

		String jbody = JSON.toJSONString(cdt);
//		System.out.println(jbody);

		if (checkdb) {
			String sql = "SELECT * FROM tb_card WHERE card_name = '" + cardName + "' " + "and parent_card_id = ? "
					+ "and status = 1 ";
			log.info("sql:" + sql);
			List<Map<String, Object>> rets = DBMapper.query(sql, parentCardId);
			beforenum = rets.size();
		}

		result = platclient.post(Card_DistributeCard, jbody);

		Assert.assertEquals(result.getCode(), HttpStatus.SC_OK, "批量发卡:" + result.getBody());
		Assert.assertEquals(result.getBody(), "{}");

		CardChecker.waitDistributeCardProc(platclient);

		// 卡数量
		String sql = "SELECT * FROM tb_card WHERE card_name = '" + cardName + "' " + "and parent_card_id = ? "
				+ "and status = 1 and capacity = balance";
		log.info("sql:" + sql);
		List<Map<String, Object>> rets = DBMapper.query(sql, parentCardId);
		afternum = rets.size();
		Assert.assertEquals(afternum, beforenum + 1);

		// 卡信息(只验证其中一个人的信息)
		String cardSql = "select * from tb_card where account_id =? and parent_card_id=? and status=? order by id desc limit 1";
		List<Map<String, Object>> cardList = DBMapper.query(cardSql, accountIds.get(0), parentCardId,
				CardStatusEnum.USABLE.getCode());
		Assert.assertEquals(cardName, cardList.get(0).get("card_name"));
		Assert.assertEquals(capacity, cardList.get(0).get("capacity"));
		// 发卡时，capacity=balance
		Assert.assertEquals(capacity, cardList.get(0).get("balance"));
		Assert.assertEquals(CardTypeEnum.VIRTUAL.getCode(), cardList.get(0).get("type"));
		Assert.assertEquals(CardStatusEnum.USABLE.getCode(), cardList.get(0).get("status"));
		Assert.assertEquals(parentCardId, cardList.get(0).get("parent_card_id"));
		Assert.assertEquals(organizationId, cardList.get(0).get("from_hospital"));
		Assert.assertEquals(defPlatAccountId, cardList.get(0).get("manager_id"));
		Assert.assertEquals(organizationType, cardList.get(0).get("organization_type"));	
		//结算相关
		Assert.assertEquals(Long.parseLong(cardList.get(0).get("freeze_balance").toString()),0l);//冻结金额为0
		Assert.assertEquals(Integer.parseInt(cardList.get(0).get("hospital_settlement_status").toString()),SettlementHospitalConfirmEnum.UNSETTLEMENT.getCode().intValue());//未结算
		Assert.assertNull(cardList.get(0).get("settlement_batch_sn"));

		String cardId = cardList.get(0).get("id").toString();

		System.out.println("平台客户经理卡ID:" + cardId);

		// tb_card_batch
		String batchSql = "select batch.* from tb_card card LEFT JOIN  tb_card_batch batch on batch.id = card.batch_id where card.id=?";
		List<Map<String, Object>> batchList = DBMapper.query(batchSql, cardId);
		Assert.assertEquals(companyId, batchList.get(0).get("new_company_id"));
		Assert.assertEquals(2, batchList.get(0).get("organization_type"));

		// tb_manager_card_relation
		String relationSql = "select * from tb_manager_card_relation where card_id=?";
		List<Map<String, Object>> relationList = DBMapper.query(relationSql, cardId);
		Assert.assertEquals(companyId, relationList.get(0).get("new_company_id"));
		Assert.assertEquals(2, relationList.get(0).get("organization_type"));

		// 用于撤销卡
		for (Map<String, Object> ret : rets) {
			Card card1 = new Card();
			card1.setBalance(Long.parseLong(ret.get("balance").toString()));
			card1.setCapacity(Long.parseLong(ret.get("capacity").toString()));
			card1.setCardName(ret.get("card_name").toString());
			card1.setId(Integer.parseInt(ret.get("id").toString()));
			card1.setStatus(Integer.parseInt(ret.get("status").toString()));
			card1.setAccountId(Integer.parseInt(ret.get("account_id").toString()));
			cardList2.add(card1);
		}
	}

	@Test(description = "平台客户经理发卡，支持体检中心的官方套餐", groups = { "qa" }, dataProvider = "distributeCard_plat")
	public void test_02_distributeCard(String... args) throws SqlException {

		long capacity = Long.parseLong(args[2]);
		String cardName = "平台客户经理支持体检中心的官方套餐体检卡";

		Date expireDate = DateUtils.offsetDay(30);
		String isprivate = args[7];
		String showCardMealPrice = args[8];
		onceLogOutSystem(platclient, Flag.CRM);
		// 平台客户经理登录
		onceLoginInSystem(platclient, Flag.CRM, defPlatUsername,defPlatPasswd);
		// 选择单位
		HttpResult result = platclient.get(Hos_GetHospitalCompanyByManager);
		List<ChannelCompany> vo = JSON.parseArray(result.getBody(), ChannelCompany.class);

		if (vo.size() == 1) {
			System.out.println("只有一个散客单位");
			return;
		}
		// 取第二个单位，因为第一个是散客单位
		ChannelCompany v = vo.get(1);

		int newCompanyId = v.getId();
//		int companyId = v.getId();

		// 取单位支持的体检中心
		List<NameValuePair> params = new ArrayList<>();
		params.add(new BasicNameValuePair("companyId", newCompanyId + ""));
		result = platclient.get(Hos_Hospital, params);

		Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);
		List<Hospital> hospitalList = JSON.parseArray(result.getBody(), Hospital.class);
		// 取第一个体检中心
		int hospitalId = hospitalList.get(0).getId();

		// 单个添加用户
		FileAccountImportInfo fileAccountImportInfo = new FileAccountImportInfo();
		fileAccountImportInfo.setAddAccountType("idCard");
		fileAccountImportInfo.setCompanyId(newCompanyId);
		fileAccountImportInfo.setEmployeeId("");
		fileAccountImportInfo.setGroup("平台客户经理分组");
		IdCardGeneric idCardGeneric = new IdCardGeneric();
		fileAccountImportInfo.setIdCard(idCardGeneric.generate());
		fileAccountImportInfo.setName("平台客户经理发卡用户");
		fileAccountImportInfo.setNewCompanyId(newCompanyId);
		int organizationId = AccountChecker.getOrganizationIdByPlatManagerId(defPlatAccountId);
		fileAccountImportInfo.setOrganizationId(organizationId);
		fileAccountImportInfo.setOrganizationType(HospitalChecker.getOrganizationType(organizationId));

		List<NameValuePair> pairs = new ArrayList<NameValuePair>();

		pairs.add(new BasicNameValuePair("hospitalId", hospitalId + ""));
		pairs.add(new BasicNameValuePair("type", ModifyAccountType.NEW + ""));
		result = platclient.post(Account_ModifyAccount, pairs, JSON.toJSONString(fileAccountImportInfo));
		int accountId = JsonPath.read(result.getBody(), "$.result");

		// 构建发卡参数
		CardDto dto = new CardDto();
		List<Integer> accountIdList = new ArrayList<>();
		accountIdList.add(accountId);
		dto.setAccountIdList(accountIdList);
		dto.setBookingDeadline(null);
		// 卡设置
		CardSetting settings = new CardSetting();
		settings.setCardId(parentCardId);
		if (isprivate != null && !isprivate.equals("")) {
			settings.setPrivate(Boolean.valueOf(isprivate));
		}
		if (showCardMealPrice != null && !showCardMealPrice.equals("")) {
			settings.setShowCardMealPrice(Boolean.valueOf(showCardMealPrice));
		}

		// 卡信息
		Card card = new Card();
		card.setCapacity(capacity);
		card.setCardName(cardName);
		card.setCardSetting(settings);
		card.setExpiredDate(expireDate);
		card.setId(parentCardId);
		dto.setCard(card);

		// 卡套餐
		List<HostpitalMealDto> cardMealList = new ArrayList<>();
		HostpitalMealDto hostpitalMealDto = new HostpitalMealDto();
		hostpitalMealDto.setHospitalId(-1);
		hostpitalMealDto.setIsMealForFamily(false);
		hostpitalMealDto.setIsMealForSelf(true);
		cardMealList.add(hostpitalMealDto);
		dto.setCardMealList(cardMealList);

		dto.setCompanyId(newCompanyId);
		dto.setExamNote("");
		dto.setIsSendBookingMsg(false);
		dto.setIsSendCardMsg(false);
		dto.setMealPrice(null);
		dto.setNewCompanyId(newCompanyId);
		dto.setOrganizationType(HospitalChecker.getOrganizationType(organizationId));
		dto.setQueryCondition("");

		result = platclient.post(Card_DistributeCard, JSON.toJSONString(dto));
//		System.out.println(result.getBody());
		Assert.assertEquals(result.getBody(),"{}","返回结果"+result.getBody());

		CardChecker.waitDistributeCardProc(platclient);

		if (checkdb) {
			// 防止数据库或者程序慢
			waitto(2);
			String sql = "select * from tb_card where card_name=? and parent_card_id=? and status=1 and account_id=?";
			List<Map<String, Object>> list = DBMapper.query(sql, cardName, parentCardId, accountId);
			int cardId = Integer.valueOf(list.get(0).get("id").toString());
			log.info("平台客户经理发的支持体检中心官方套餐ID:" + cardId);
			// 用于撤销卡
			Card card1 = new Card();
			card1.setBalance(Long.parseLong(list.get(0).get("balance").toString()));
			card1.setCapacity(Long.parseLong(list.get(0).get("capacity").toString()));
			card1.setCardName(list.get(0).get("card_name").toString());
			card1.setId(Integer.parseInt(list.get(0).get("id").toString()));
			card1.setStatus(Integer.parseInt(list.get(0).get("status").toString()));
			card1.setAccountId(Integer.parseInt(list.get(0).get("account_id").toString()));
			cardList2.add(card1);

			// tb_card
			Assert.assertEquals(organizationId+"", list.get(0).get("from_hospital").toString());
			Assert.assertEquals(defPlatAccountId + "", list.get(0).get("manager_id").toString());
			Assert.assertEquals(newCompanyId+"", list.get(0).get("new_company_id").toString());
			Assert.assertEquals(HospitalChecker.getOrganizationType(organizationId)+"", list.get(0).get("organization_type").toString());

			// tb_card_batch
			String batchSql = "select * from tb_card_batch where id=?";
			List<Map<String, Object>> batchList = DBMapper.query(batchSql, list.get(0).get("batch_id").toString());
			Assert.assertEquals(newCompanyId+"", batchList.get(0).get("new_company_id").toString());
			Assert.assertEquals(HospitalChecker.getOrganizationType(organizationId)+"",
					batchList.get(0).get("organization_type").toString());

			// tb_manager_card_relation
			String managerCardRelationSql = "select * from tb_manager_card_relation where card_id=?";
			List<Map<String, Object>> managerCardRelationList = DBMapper.query(managerCardRelationSql, cardId);
			Assert.assertEquals(newCompanyId+"", managerCardRelationList.get(0).get("new_company_id").toString());
			Assert.assertEquals(HospitalChecker.getOrganizationType(organizationId)+"",
					managerCardRelationList.get(0).get("organization_type").toString());

			if (v.getPlatformCompanyId()!=null&&v.getPlatformCompanyId()>5) {
				// 如果是P单位，tb_card_hospital_relation是P单位支持的体检中心
				// 获取P单位支持的体检中心
				String hospitalCompanySql = "select * from tb_hospital_company where platform_company_id=? order by organization_id desc";
				List<Map<String, Object>> hospitalCompanyList = DBMapper.query(hospitalCompanySql, v.getPlatformCompanyId());

				// 获取卡和体检中心的关系
				String cardHospitalRelationSql = "select * from tb_card_hospital_relation where card_id=? order by hospital_id desc ";
				List<Map<String, Object>> cardHospitalRelationList = DBMapper.query(cardHospitalRelationSql, cardId);

				Assert.assertEquals(hospitalCompanyList.size(), cardHospitalRelationList.size());
				for (int i = 0; i < hospitalCompanyList.size(); i++) {
					Assert.assertEquals(hospitalCompanyList.get(i).get("organization_id"),
							cardHospitalRelationList.get(i).get("hospital_id"));
				}
			} else {
				// 如果是M单位，tb_card_hospital_relation是渠道商支持的体检中心
				// 获取M单位支持的体检中心
				String organizationHospitalSql = "SELECT oh.hospital_id AS id FROM tb_organization_hospital_relation oh "
						+ "LEFT JOIN tb_hospital h ON h.id = oh.hospital_id "
						+ "WHERE oh.organization_id = ? AND oh. STATUS = 1 AND h.show_in_list = 1 and h.enable=1 "
						+ "ORDER BY oh.hospital_id DESC";
				List<Map<String, Object>> organizaitonHospitalList = DBMapper.query(organizationHospitalSql,
						organizationId);
				if (organizaitonHospitalList.size()==0) {
					//渠道商支持所有体检中心
					String hospitalSql = "select * from tb_hospital where enable=1 and show_in_list=1 and organization_type=? order by id desc ";
					organizaitonHospitalList = DBMapper.query(hospitalSql, OrganizationTypeEnum.HOSPITAL.getCode());
				}

				// 获取卡和体检中心的关系
				String cardHospitalRelationSql = "select * from tb_card_hospital_relation where card_id=? order by hospital_id desc ";
				List<Map<String, Object>> cardHospitalRelationList = DBMapper.query(cardHospitalRelationSql, cardId);

				Assert.assertEquals(organizaitonHospitalList.size(), cardHospitalRelationList.size());
				for (int i = 0; i < organizaitonHospitalList.size(); i++) {
					Assert.assertEquals(organizaitonHospitalList.get(i).get("id"),
							cardHospitalRelationList.get(i).get("hospital_id"));
				}
			}

		}
	}

	@AfterClass(description = "撤销发卡", groups = { "qa" }, alwaysRun = true)
	public void revocCard() throws SqlException {

		System.out.println("------------------平台客户经理撤销卡  Start-----------------");
		List<CardRecordDto> cardRecordDtos = new ArrayList<>();
		CardRecordDto cardRecordDto = new CardRecordDto();
		for (int i = 0; i < cardList2.size(); i++) {
			cardRecordDto.setCard(cardList2.get(i));
		}
		cardRecordDtos.add(cardRecordDto);
		Map<String, Object> maps = new HashMap<String, Object>();
		maps.put("cleanCardRecord", true);

		HttpResult ret = platclient.post(Card_RevocCard, maps, JSON.toJSONString(cardRecordDtos));
		System.out.println(ret.getBody());
		Assert.assertEquals(ret.getCode(), HttpStatus.SC_OK, "撤销卡:" + ret.getBody());

		onceLogOutSystem(platclient, Flag.CRM);
		System.out.println("------------------平台客户经理撤销卡  End -----------------");
	}

	@DataProvider(name = "distributeCard_plat")
	public Iterator<String[]> distribute_plat() {
		try {
			return CvsFileUtil.fromCvsFileToIterator("./csv/card/distributeCard_plat.csv", 7);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

}
