package com.tijiantest.testcase.channel.card;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.testng.Assert;

import com.alibaba.fastjson.JSON;
import com.jayway.jsonpath.JsonPath;
import com.tijiantest.base.Flag;
import com.tijiantest.base.HttpResult;
import com.tijiantest.base.MyHttpClient;
import com.tijiantest.base.dbcheck.AccountChecker;
import com.tijiantest.base.dbcheck.CardChecker;
import com.tijiantest.base.dbcheck.CompanyChecker;
import com.tijiantest.base.dbcheck.HospitalChecker;
import com.tijiantest.base.dbcheck.ResourceChecker;
import com.tijiantest.model.card.Card;
import com.tijiantest.model.card.CardDto;
import com.tijiantest.model.card.CardSetting;
import com.tijiantest.model.company.ChannelCompany;
import com.tijiantest.model.hospital.Hospital;
import com.tijiantest.model.hospital.HostpitalMealDto;
import com.tijiantest.model.resource.meal.Meal;
import com.tijiantest.testcase.channel.ChannelBase;
import com.tijiantest.util.DateUtils;
import com.tijiantest.util.db.DBMapper;
import com.tijiantest.util.db.SqlException;

public class EntityCardBase extends ChannelBase {

	static {
		try{
		sendEntityCards(2);
		}catch (AssertionError e){
			log.error("CRM-APP异常，从数据库读取实体卡");
			String sql = "select * from tb_card  where type = 2 and account_id is null and status = 1  order by id desc limit ? ";
			log.info("sql:"+sql);
			try {
				List<Map<String, Object>> dblist1 = DBMapper.query(sql, 2);
				if(dblist1 == null || dblist1.isEmpty()){
					log.error("没有可用的实体卡..请打开CRM-APP");
				}
			} catch (SqlException e1) {
				e1.printStackTrace();
			}

		}
		httpclient = new MyHttpClient();
		onceLoginInSystem(httpclient, Flag.CHANNEL, defChannelUsername, defChannelPasswd);
		// 通过jvm进程的关闭钩子关闭共用的httpclient
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				httpclient.shutdown();
			}
		});
	}

	public static void sendEntityCards(int num) {

		Integer platManagerId = defChannelPlatMangerId;
		// 0.生成实体卡号段
		GenEntityCardNum(num);

		// 1.获取实体卡号段
		Map<String, String> cardNum = CardChecker.getEntityCardNum(num);
		String startCardNum = cardNum.get("startCardNum").substring(2);
		String endCardNum = cardNum.get("endCardNum").substring(2);

		// 2..平台客户经理登陆CRM
		MyHttpClient httpclient = new MyHttpClient();
		onceLoginInSystem(httpclient, Flag.CRM, defChannelPlatManager, defChannelPlatManagerPwd);

		long defaultBalance = 10000l;

		// 3.更新平台客户经理的账户余额，保证预备金充足
		String updateSql = "update tb_accounting set balance=100000 where account_id=" + platManagerId;
		try {
			DBMapper.update(updateSql);
		} catch (SqlException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

		// 3.获取平台客户经理支持的单位信息
		Integer companyId = getCompanyIdByManagerId(httpclient, platManagerId, true);

		// 4.根据单位ID，获取体检中心信息
		List<Hospital> hospitals = CompanyChecker.getHospitalByCompanyId(httpclient, companyId);

		int parentCardId = CardChecker.getParentEntryCard(platManagerId);
		Date expireDate = DateUtils.offsetDay(30);
		// 卡信息
		Card card = new Card();
		card.setCapacity(defaultBalance);
		card.setCardName("实体卡ForChannel");
		card.setCardSetting(new CardSetting(parentCardId));
		card.setExpiredDate(expireDate);
		card.setId(parentCardId);

		// 卡绑定套餐
		List<HostpitalMealDto> cardMealList = getHospitalMeals(hospitals);

		CardDto cdt = new CardDto();
		cdt.setCard(card);
		cdt.setCardMealList(cardMealList);
		
		
		cdt.setCompanyId(companyId);
		int organizationId = 0;
		try {
			organizationId = AccountChecker.getOrganizationIdByPlatManagerId(platManagerId);
		} catch (SqlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ChannelCompany channelCompany = CompanyChecker.getChannelCompanyByCompanyId(companyId, organizationId);
		int newCompanyId = channelCompany.getId();
		System.out.println("newCompanyId=" + newCompanyId + "--------- organizationId:" + organizationId);
		int organizationType = HospitalChecker.getOrganizationType(channelCompany.getOrganizationId());
		cdt.setNewCompanyId(newCompanyId);
		cdt.setOrganizationType(organizationType);
		cdt.setIsSendBookingMsg(false);
		cdt.setIsSendCardMsg(false);

		// 实体卡起始号段-结束号段（张数）
		cdt.setStartSegment(startCardNum);
		cdt.setEndSegment(endCardNum);
		cdt.setEntryCardTotal(num);

		String jbody = JSON.toJSONString(cdt);
		//5.发卡
		HttpResult result = httpclient.post(Card_SendEntryCard, jbody);
		Assert.assertEquals(result.getCode(), HttpStatus.SC_OK, "发实体卡:" + result.getBody());
		Assert.assertEquals(result.getBody(), "{}");

		CardChecker.waitDistributeCardProc(httpclient);
		onceLogOutSystem(httpclient, Flag.CRM);
	}

	/**
	 * 登录OPS生成实体卡号段
	 * 
	 * @param num
	 */
	public static void GenEntityCardNum(int num) {
		MyHttpClient httpclient = new MyHttpClient();
		onceLoginInSystem(httpclient, Flag.OPS, defManagerUsername, defManagerPasswd);
		List<NameValuePair> params = new ArrayList<>();
		params.add(new BasicNameValuePair("increment", num + ""));
		HttpResult result = httpclient.get(Flag.OPS, OPS_GenerCardNum, params);
		if (result.getCode() != HttpStatus.SC_OK)
			System.out.println("发实体卡失败！！！");
		onceLogOutSystem(httpclient, Flag.OPS);
	}

	/**
	 * 根据客户经理获取单位
	 * 
	 * @param httpclient
	 * @param managerId
	 * @param isPlatManager
	 * @return
	 */
	public static Integer getCompanyIdByManagerId(MyHttpClient httpclient, Integer managerId, Boolean isPlatManager) {
		Integer companyId = null;
		List<Integer> companyIdList = null;
		try {
			companyIdList = AccountChecker.getCompanysIdByManagerId(httpclient, defPlatAccountId, isPlatManager);
		} catch (SqlException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if (companyIdList.size() == 1 ) {
			int new_company_id = companyIdList.get(0);
			ChannelCompany hc =  CompanyChecker.getChannelCompanyByCompanyId(new_company_id);
			if(hc.getPlatformCompanyId() != null && hc.getPlatformCompanyId() == 5){
				log.info("只有一个散客单位");
				return null;
			}else
				return new_company_id;

		}
		// 取第二个单位（因为第一个是散客单位）
		companyId = companyIdList.get(1);
		return companyId;
	}

	/**
	 * 获取医院官方套餐
	 * 
	 * @param hospitals
	 * @return
	 */
	public static List<HostpitalMealDto> getHospitalMeals(List<Hospital> hospitals) {
		List<HostpitalMealDto> cardMealList = new ArrayList<>();
		int hospitalId = -1;
		List<Meal> cpMeal = null;
		for (Hospital h : hospitals) {
			hospitalId = h.getId();
			cpMeal = ResourceChecker.getOffcialMeal(hospitalId);// 官方套餐
			if (cpMeal != null)
				break;
		}
		if (cpMeal == null) {
			log.info("套餐为空");
			throw new RuntimeException("套餐为空");
		}
		for (int i = 0; i < cpMeal.size(); i++) {
			Meal meal = cpMeal.get(i);
			HostpitalMealDto hmd = new HostpitalMealDto();
			hmd.setHospitalId(hospitalId);
			hmd.setIsMealForSelf(true);
			hmd.setIsMealForFamily(false);
			hmd.setMealId(meal.getId());
			hmd.setMealPrice(meal.getPrice());
			cardMealList.add(hmd);
		}
		return cardMealList;
	}
}
