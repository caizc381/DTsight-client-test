package com.tijiantest.testcase.crm;

import java.io.IOException;
import java.util.*;

import com.tijiantest.base.Flag;
import com.tijiantest.model.account.Examiner;
import com.tijiantest.util.DateUtils;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.testng.Assert;

import com.alibaba.fastjson.JSON;
import com.beust.jcommander.internal.Lists;
import com.tijiantest.base.HttpResult;
import com.tijiantest.base.dbcheck.AccountChecker;
import com.tijiantest.base.dbcheck.CardChecker;
import com.tijiantest.base.dbcheck.CompanyChecker;
import com.tijiantest.base.dbcheck.CounterChecker;
import com.tijiantest.base.dbcheck.HospitalChecker;
import com.tijiantest.base.dbcheck.OrderChecker;
import com.tijiantest.base.dbcheck.ResourceChecker;
import com.tijiantest.model.account.AccountRelationInCrm;
import com.tijiantest.model.account.AddAccountTypeEnum;
import com.tijiantest.model.card.Card;
import com.tijiantest.model.card.CardRecordDto;
import com.tijiantest.model.company.HospitalCompany;
import com.tijiantest.model.order.Order;
import com.tijiantest.model.resource.meal.Meal;
import com.tijiantest.util.ConfParser;
import com.tijiantest.util.db.DBMapper;
import com.tijiantest.util.db.SqlException;

public class CrmMediaBase extends CrmBase {

	public static Meal sankeCommonMeal;
	public static Meal sankeFemaleMeal;
	public static Meal sankeMaleMeal;
	public static Meal defCompanyCommMeal;
	public static Meal defCompanyMaleMeal;
	public static int sankeAccountId;
	public static String sankeName;
	public static String sankeUserIdCard;
	public static int defCompanyAccountId;
	public static String companyName;
	public static String companyIdCard;
	public static Card sankeCard;
	public static Card defCompanyCard;
	public static Order sankeOrder;
	public static Order defCompanyOrder;
	private static List<Meal> mediaList = new ArrayList<Meal>();
	public final static ConfParser baseconf = new ConfParser("./csv/base/base.conf");

	static {

		
		clearData();

		String startDate = "2018-12-01";
		String endDate = "2018-12-31";

		String nowDate = sdf.format(new Date());
		String thirtyDate = sdf.format(DateUtils.offDate(30));
		List<Integer> daysOfWeek = Arrays.asList(1,2, 3, 4, 5, 6,7);
		CounterChecker.HospitalCapacityConfig(false, defhospital.getId(), startDate, endDate, daysOfWeek, 250, 25,httpclient);
		waitto(2);
		CounterChecker.HospitalCapacityConfig(false, defhospital.getId(), nowDate, thirtyDate, daysOfWeek, 250, 25,httpclient);
		System.out.println("                    ★★★★★★★★★★★★★★★★初始化数据完毕★★★★★★★★★★★★★★★★");
		
		
		HospitalCompany hCompany = CompanyChecker.getHospitalCompanyByPlatCompanyIdANDOrganizationId(2,defhospital.getId());
		// 1.Meal
		sankeCommonMeal = ResourceChecker.createMeal(httpclient, hCompany.getId(), "autotest_散客通用套餐1", 2,defhospital.getId()); // 散客单位下通用套餐
		mediaList.add(sankeCommonMeal);
		System.out.println("跟踪问题套餐：套餐id："+sankeCommonMeal.getId()+" 套餐状态："+ResourceChecker.getMealInfo(sankeCommonMeal.getId()).getDisable());
		waitto(2);
		sankeMaleMeal = ResourceChecker.createMeal(httpclient, hCompany.getId(), "autotest_散客男性套餐", 0,defhospital.getId()); // 散客单位下男性套餐
		mediaList.add(sankeMaleMeal);
		waitto(2);
		sankeFemaleMeal = ResourceChecker.createMeal(httpclient, hCompany.getId(), "autotest_散客女性套餐", 1,defhospital.getId()); // 散客单位下女性套餐
		mediaList.add(sankeFemaleMeal);
		waitto(2);
		defCompanyCommMeal = ResourceChecker.createMeal(httpclient, defnewcompany.getId(), "autotest_单位通用套餐1", 2,defhospital.getId()); // 默认单位通用套餐
		mediaList.add(defCompanyCommMeal);
		System.out.println("跟踪问题套餐：套餐id："+defCompanyCommMeal.getId()+" 套餐状态："+ResourceChecker.getMealInfo(defCompanyCommMeal.getId()).getDisable());
		waitto(2);
		defCompanyMaleMeal = ResourceChecker.createMeal(httpclient, defnewcompany.getId(), "autotest_单位男性套餐1", 0,defhospital.getId()); // 默认单位男性套餐
		mediaList.add(defCompanyMaleMeal);

		// 2.Account
		try {
			AccountChecker.uploadAccount(httpclient, hCompany.getId(), defhospital.getId(), "autotest_散客组", "./csv/base/sanke_account.xlsx",AddAccountTypeEnum.idCard);
			getBaseConf();
			sankeAccountId = AccountChecker.getAccountId(sankeUserIdCard, sankeName, "autotest_散客组",defaccountId,defhospital.getId());
			waitto(5);
			AccountChecker.uploadAccount(httpclient, defnewcompany.getId(), defhospital.getId(), "autotest_单位测试组",
					"./csv/base/company_account.xlsx",AddAccountTypeEnum.idCard);
			defCompanyAccountId = AccountChecker.getAccountId(companyIdCard, companyName, "autotest_单位测试组",defaccountId,defhospital.getId());
		} catch (IOException e) {
			e.printStackTrace();
		}

		// 3.Card现不支持散客单位发卡
		try {
			//sankeCard = createCard(httpclient, sankeAccountId, defSKXCnewcompany.getId(), "autotest_默认散客测试卡", 2000, sankeMaleMeal.getId());
			System.out.println("跟踪问题套餐(发卡前)：套餐id："+defCompanyCommMeal.getId()+" 套餐状态："+ResourceChecker.getMealInfo(defCompanyCommMeal.getId()).getDisable());
			defCompanyCard = CardChecker.createCard(httpclient, defCompanyAccountId, defnewcompany.getId(), "autotest_默认单位测试卡", 2000,
					defCompanyMaleMeal.getId(),defhospital.getId(),defaccountId);
			System.out.println("跟踪问题套餐(发卡后)：套餐id："+defCompanyCommMeal.getId()+" 套餐状态："+ResourceChecker.getMealInfo(defCompanyCommMeal.getId()).getDisable());
		} catch (SqlException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		// Order
		try {
			sankeOrder = OrderChecker.crm_createOrder(httpclient, sankeMaleMeal.getId(), sankeAccountId, defSKXCnewcompany.getId(), defSKXCnewcompany.getName(),"2018-12-01",defhospital);
		} catch (SqlException e1) {
			System.out.println("ERROR-----------初始化创建散客单位订单报错");
			e1.printStackTrace();
		} // 散客用户预约2018-12-01订单
		try {
			System.out.println("跟踪问题套餐(下单前)：套餐id："+defCompanyCommMeal.getId()+" 套餐状态："+ResourceChecker.getMealInfo(defCompanyCommMeal.getId()).getDisable());
			defCompanyOrder = OrderChecker.crm_createOrder(httpclient, defCompanyMaleMeal.getId(), defCompanyAccountId, defnewcompany.getId(),defnewcompany.getName(),
					"2018-12-01",defhospital);
			System.out.println("跟踪问题套餐(下单后)：套餐id："+defCompanyCommMeal.getId()+" 套餐状态："+ResourceChecker.getMealInfo(defCompanyCommMeal.getId()).getDisable());
		} catch (SqlException e1) {
			System.out.println("ERROR-----------初始化创建普通单位订单报错");
			e1.printStackTrace();
		}// 单位用户预约2018-12-01订单

		addListeners(new CrmLifeCycleListener() {
			@Override
			public void beforeShutdown() {
				try {
					revokeAndDeleteorders();
				} catch (SqlException e) {
					// TODO 自动生成的 catch 块
					e.printStackTrace();
				}
				revokeCard();
				removeMeal();
				removeAccount();

			}
		});
	}

	/**
	 * 删除创建的订单
	 * @throws SqlException 
	 */
	public static void revokeAndDeleteorders() throws SqlException {

		List<Integer> orderIdList = Lists.newArrayList();
		orderIdList.add(defCompanyOrder.getId());
		orderIdList.add(sankeOrder.getId());
		// 撤销单位订单
		OrderChecker.Run_CrmOrderRevokeOrder(httpclient,orderIdList,false,true,true);
		//删除订单
		OrderChecker.Run_CrmDeleteOrders(httpclient,orderIdList,true,true);

		
		

	}

	/**
	 * 删除创建的卡
	 * 
	 * @throws SqlException
	 */
	public static void revokeCard() {
		// 撤销单位卡
		List<CardRecordDto> cardRecordList = new ArrayList<CardRecordDto>();
		CardRecordDto crd = new CardRecordDto();
		String sql1 = "select name from tb_examiner where customer_id =? and manager_id = ? and new_company_id = ?";
		List<Map<String, Object>> rs;
		try {
			rs = DBMapper.query(sql1, defCompanyAccountId, defaccountId, defnewcompany.getId());
			Examiner ba = new Examiner();
			ba.setId(defCompanyCard.getId());
			ba.setName(rs.get(0).get("name").toString());
			crd.setCard(defCompanyCard);
			crd.setAccount(ba);
			cardRecordList.add(crd);
			log.info(JSON.toJSONString(cardRecordList));
		} catch (SqlException e) {
			e.printStackTrace();
		}

		Map<String, Object> maps = new HashMap<String, Object>();
		maps.put("cleanCardRecord", true);
		HttpResult ret = httpclient.post(Card_RevocCard, maps, JSON.toJSONString(cardRecordList));

		Assert.assertEquals(ret.getCode(), HttpStatus.SC_OK, "撤销卡:" + ret.getBody());
		Assert.assertEquals(ret.getBody(), "{}");

		/*// 撤销散客卡
		cardRecordList.clear();
		String sql2 = "select name from tb_examiner where customer_id =? and manager_id = ? and company_id = ?";
		try {
			rs = DBMapper.query(sql2, sankeAccountId, defaccountId, 1585);
			AccountRelationInCrm ba1 = new AccountRelationInCrm();
			ba1.setId(sankeCard.getId());
			ba1.setName(rs.get(0).get("name").toString());
			crd.setCard(sankeCard);
			crd.setAccount(ba1);
			cardRecordList.add(crd);
		} catch (SqlException e) {
			e.printStackTrace();
		}
		log.info(JSON.toJSONString(cardRecordList));
		Map<String, Object> maps1 = new HashMap<String, Object>();
		maps1.put("cleanCardRecord", true);
		ret = httpclient.post(Card_RevocCard, maps1, JSON.toJSONString(cardRecordList));

		Assert.assertEquals(ret.getCode(), HttpStatus.SC_OK, "撤销卡:" + ret.getBody());
		Assert.assertEquals(ret.getBody(), "{}");*/

	}

	/**
	 * 删除创建的用户
	 */
	public static void removeAccount() {
		/***** 移除用户 ******/
		// 移除散客用户
		HospitalCompany hospitalCompany = defSKXCnewcompany;
		// paramss.put("companyId", 1585);
		int newCompanyId = hospitalCompany.getId();
		int organizationType = HospitalChecker.getOrganizationType(defhospital.getId());
		List<NameValuePair> params = new ArrayList<NameValuePair>();

		params.add(new BasicNameValuePair("accountIds", sankeAccountId+""));
		params.add(new BasicNameValuePair("newCompanyId", newCompanyId+""));
		params.add(new BasicNameValuePair("organizationType", organizationType+""));
		
		HttpResult delete = httpclient.post(Account_RemoveCustomer, params);
		System.out.println(delete.getBody());
		Assert.assertEquals(delete.getCode(), HttpStatus.SC_OK);
		Assert.assertEquals(delete.getBody(), "{\"result\": 1}");

		// 移除单位用户
		params.clear();
		params.add(new BasicNameValuePair("accountIds", defCompanyAccountId+""));
		params.add(new BasicNameValuePair("companyId", defnewcompany.getId()+""));
		int nnewCompanyId = defnewcompany.getId();
		params.add(new BasicNameValuePair("newCompanyId", nnewCompanyId+""));
		params.add(new BasicNameValuePair("organizationType", organizationType+""));
		delete = httpclient.post(Account_RemoveCustomer, params);
		Assert.assertEquals(delete.getCode(), HttpStatus.SC_OK);
		Assert.assertEquals(delete.getBody(), "{\"result\": 1}");
	}

	/**
	 * 删除创建的默认套单
	 */
	public static void removeMeal() {
		String mealId = null;
		for (int i = 0; i < mediaList.size(); i++) {
//			String deleteMeal = (mediaList.get(i).getType() < 3) ? Meal_DeleteCustomizeMeal : Meal_DeleteOfficalMeal;
			mealId = mediaList.get(i).getId().toString();
			HttpResult response = httpclient.get(Meal_DeleteMeal, mealId);
			System.out.println("response of delete:......" + response.getBody());
			Assert.assertEquals(response.getCode(), HttpStatus.SC_OK);
			log.info("id为" + mealId + "的套餐已经删除");
		}
	}

	/**
	 * 从基本base.conf文件中读取姓名和身份证号
	 */
	public static void getBaseConf() {
		sankeName = baseconf.getValue("sanke", "姓名");
		sankeUserIdCard = baseconf.getValue("sanke", "身份证");
		companyName = baseconf.getValue("company", "姓名");
		companyIdCard = baseconf.getValue("company", "身份证");
		log.debug("散客:" + sankeName + " 身份证:" + sankeUserIdCard);
		log.debug("单位:" + companyName + " 身份证:" + companyIdCard);
	}

}
