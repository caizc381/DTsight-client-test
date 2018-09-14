package com.tijiantest.testcase.crm.card;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.testng.Assert;
import org.testng.annotations.Test;

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
import com.tijiantest.testcase.crm.CrmBase;
import com.tijiantest.util.DateUtils;
import com.tijiantest.util.db.DBMapper;
import com.tijiantest.util.db.SqlException;

/**
 * CRM平台客户经理发实体卡
 * 
 * @author huifang
 *
 */
public class EntityCardTest extends CrmBase {
	private String startCardNum = "";
	private String endCardNum = "";
	private String cardPassword = "";
	private long defaultBalance = 10000l;
	MyHttpClient myClient = new MyHttpClient();

	@Test(description = "平台客户经理发实体卡", groups = { "qa" })
	public void test_01_setCardSegment() throws SqlException {
		int maxCardNum = 0;
		if (checkdb) {
			String sql = "SELECT * FROM tb_card_segment s  where s.card_num not in (select card_num from  tb_card ) ORDER BY s.id desc limit ?";
			List<Map<String, Object>> dblist1 = DBMapper.query(sql, 1);
			if(dblist1 == null || dblist1.isEmpty()){
				log.error("没有可用的实体卡段落，需要手动创建");
				return;
			}
			Map<String, Object> map = dblist1.get(0);
			maxCardNum = Integer.parseInt(map.get("card_num").toString().substring(2));
			cardPassword = map.get("card_pwd").toString();
		}

		// 1.平台客户经理登陆CRM
		onceLoginInSystem(myClient, Flag.CRM,defPlatUsername,defPlatPasswd);

		// 2.发实体卡
		startCardNum = maxCardNum + "";
		endCardNum = maxCardNum + "";
		List<NameValuePair> params = new ArrayList<>();
		params.add(new BasicNameValuePair("startCardNum", startCardNum));
		params.add(new BasicNameValuePair("endCardNum", endCardNum));

		HttpResult result = myClient.post(Card_SetCardSegment, params);
		Assert.assertEquals(result.getCode(), HttpStatus.SC_OK,"出错原因..."+ result.getBody());
		String body = result.getBody();
		int num = JsonPath.read(body, "$.cards");
		int balance = Integer.parseInt(JsonPath.read(body, "$.balance").toString());
		boolean success = JsonPath.read(body, "$.success");
		Assert.assertEquals(num, 1);
		Assert.assertEquals(success, true);
		if (checkdb) {
			String sql = "select * from tb_accounting where account_id = ?";
			List<Map<String, Object>> dblist1 = DBMapper.query(sql, defPlatAccountId);
			Map<String, Object> map = dblist1.get(0);
			Assert.assertEquals(balance, Integer.parseInt(map.get("balance").toString()));
		}
	}

	@Test(description = "发实体卡", groups = { "qa", "crm_entityCard" }, dependsOnMethods = "test_01_setCardSegment")
	public void test_02_sendEntryCard() throws SqlException {
		//0.更新平台客户经理的账户余额，保证预备金充足
		String updateSql = "update tb_accounting set balance=100000 where account_id="+defPlatAccountId;
		DBMapper.update(updateSql);
		// 1.平台客户经理登陆CRM
		onceLoginInSystem(myClient, Flag.CRM, defPlatUsername,defPlatPasswd);
		// 获取平台客户经理支持的单位信息
		List<Integer> companyIdList = AccountChecker.getCompanysIdByManagerId(myClient, defPlatAccountId, true);
		if (companyIdList.size() == 1) {
			log.info("只有一个散客单位");
			return;
		}
		// 取第二个单位（因为第一个是散客单位）
		int companyId = companyIdList.get(1);

		// 根据单位ID，获取体检中心信息

		List<Hospital> hospitals = CompanyChecker.getHospitalByCompanyId(myClient, companyId);

		int parentCardId = CardChecker.getParentEntryCard(defPlatAccountId);
		Date expireDate = DateUtils.offsetDay(30);
		// 卡信息
		Card card = new Card();
		card.setCapacity(defaultBalance);
		card.setCardName("P|M单位平台客户经理_实体卡");
		card.setCardSetting(new CardSetting(parentCardId));
		card.setExpiredDate(expireDate);
		card.setId(parentCardId);

		// 卡绑定套餐
		int hospitalId = -1;
		List<Meal> cpMeal = null;
		for(Hospital h : hospitals){
			hospitalId = h.getId();
			cpMeal = ResourceChecker.getOffcialMeal(hospitalId);//官方套餐
			if(cpMeal !=null)
				break;
		}
		if (cpMeal == null){
				log.info("套餐为空");
				throw new RuntimeException("套餐为空");
			}
		List<HostpitalMealDto> cardMealList = new ArrayList<HostpitalMealDto>();
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
		CardDto cdt = new CardDto();
		cdt.setCard(card);
		cdt.setCardMealList(cardMealList);
		cdt.setCompanyId(companyId);
		int organizationId = AccountChecker.getOrganizationIdByPlatManagerId(defPlatAccountId);
		ChannelCompany channelCompany = CompanyChecker.getChannelCompanyByCompanyId(companyId,organizationId);
		int newCompanyId = channelCompany.getId();
		System.out.println("newCompanyId=" + newCompanyId + "--------- organizationId:"+organizationId);
		int organizationType = HospitalChecker.getOrganizationType(channelCompany.getOrganizationId());
		cdt.setNewCompanyId(newCompanyId);
		cdt.setOrganizationType(organizationType);
		cdt.setIsSendBookingMsg(false);
		cdt.setIsSendCardMsg(false);

		// 实体卡起始号段-结束号段（张数）
		cdt.setStartSegment(startCardNum);
		cdt.setEndSegment(startCardNum);
		cdt.setEntryCardTotal(1);

		String jbody = JSON.toJSONString(cdt);
		HttpResult result = myClient.post(Card_SendEntryCard, jbody);
		Assert.assertEquals(result.getCode(), HttpStatus.SC_OK, "发实体卡:" + result.getBody());
		Assert.assertEquals(result.getBody(), "{}");
		CardChecker.waitDistributeCardProc(myClient);

		if (checkdb) {
			String sql = "select * from tb_card order by id desc limit 1 ";
			log.info("sql:" + sql);
			List<Map<String, Object>> rets = DBMapper.query(sql);
			Map<String, Object> map = rets.get(0);
			String cardId = map.get("id").toString();
			log.info("实体卡ID:"+cardId);
			Assert.assertEquals(map.get("card_name").toString(), "P|M单位平台客户经理_实体卡");
			Assert.assertEquals(map.get("card_num").toString(), "MT" + startCardNum);
			Assert.assertEquals(map.get("password").toString(), cardPassword);
			Assert.assertEquals(Integer.parseInt(map.get("capacity").toString()), defaultBalance);
			Assert.assertEquals(Integer.parseInt(map.get("balance").toString()), defaultBalance);
			Assert.assertEquals(Integer.parseInt(map.get("type").toString()), 2);
			Assert.assertEquals(Integer.parseInt(map.get("status").toString()), 1);
			Assert.assertNull(map.get("account_id"));
			
			Assert.assertEquals(organizationId, map.get("from_hospital"));
			Assert.assertEquals(defPlatAccountId, map.get("manager_id"));
			Assert.assertEquals(newCompanyId, map.get("new_company_id"));
			Assert.assertEquals(organizationType, map.get("organization_type"));

			int batchId = Integer.parseInt(map.get("batch_id").toString());
			// tb_card_batch
			sql = "select * from tb_card_batch where id = ?";
			rets = DBMapper.query(sql, batchId);
			map = rets.get(0);
			Assert.assertEquals(map.get("card_name").toString(), "P|M单位平台客户经理_实体卡");
			Assert.assertEquals(Integer.parseInt(map.get("capacity").toString()), defaultBalance);
			Assert.assertEquals(Integer.parseInt(map.get("amount").toString()), 1);
			Assert.assertEquals(Integer.parseInt(map.get("operator_id").toString()), defPlatAccountId);
			Assert.assertEquals(newCompanyId, map.get("new_company_id"));
			Assert.assertEquals(organizationType, map.get("organization_type"));

			// tb_manager_card_relation
			String relationSql = "select * from tb_manager_card_relation where card_id=?";
			List<Map<String, Object>> relationList = DBMapper.query(relationSql, cardId);
			Assert.assertEquals(newCompanyId, relationList.get(0).get("new_company_id"));
			Assert.assertEquals(organizationType, relationList.get(0).get("organization_type"));
			Assert.assertEquals(defPlatAccountId, relationList.get(0).get("manager_id"));
			Assert.assertEquals(newCompanyId, relationList.get(0).get("new_company_id"));
			Assert.assertEquals(organizationType, relationList.get(0).get("organization_type"));
		}

		// 平台客户经理登出
		onceLogOutSystem(myClient, Flag.CRM);
	}
}
