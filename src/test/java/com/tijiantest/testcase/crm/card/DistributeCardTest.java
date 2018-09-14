package com.tijiantest.testcase.crm.card;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.tijiantest.model.account.Examiner;
import org.apache.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.alibaba.fastjson.JSON;
import com.jayway.jsonpath.JsonPath;
import com.tijiantest.base.HttpResult;
import com.tijiantest.base.dbcheck.CardChecker;
import com.tijiantest.base.dbcheck.HospitalChecker;
import com.tijiantest.model.account.AccountRelationInCrm;
import com.tijiantest.model.card.Card;
import com.tijiantest.model.card.CardDto;
import com.tijiantest.model.card.CardRecordDto;
import com.tijiantest.model.card.CardSetting;
import com.tijiantest.model.card.CardStatusEnum;
import com.tijiantest.model.card.CardTypeEnum;
import com.tijiantest.model.hospital.HostpitalMealDto;
import com.tijiantest.model.paylog.PayConsts;
import com.tijiantest.model.settlement.SettlementHospitalConfirmEnum;
import com.tijiantest.testcase.crm.CrmMediaBase;
import com.tijiantest.util.CvsFileUtil;
import com.tijiantest.util.DateUtils;
import com.tijiantest.util.db.DBMapper;
import com.tijiantest.util.db.SqlException;

/**
 * 发卡过程
 * 
 * @author huifang
 *
 */
public class DistributeCardTest extends CrmMediaBase {

	// 卡数量
	private static int beforenum = 0;
	private static int afternum = 0;
	// 用户账号ID
	public static List<Integer> accountIds = new ArrayList<Integer>();
	// 卡列表
	protected static List<Card> cardList1 = new ArrayList<Card>();

	int parentCardId = CardChecker.getParentEntryCard(defaccountId);
	long parentCardPrice;
	
	public Integer companyId;

	@Test(description = "发卡", groups = { "qa", "crm_distributeCard" }, dataProvider = "distribute")
	public void test_01_distributeCard_success(String... args) throws SqlException {
		// 获取母卡信息
		String parentCardSql = "select * from tb_card where id=?";
		List<Map<String, Object>> parentCardList = DBMapper.query(parentCardSql, parentCardId);
		parentCardPrice = CardChecker.getParentBalanceFromRedis(parentCardId);
		companyId = defnewcompany.getId();

		// mealid,mealprice,parentcardid,13516,5500,33049
		// 账号id列表
		accountIds = new ArrayList<Integer>();
		accountIds.add(defCompanyAccountId);
		long capacity = Long.parseLong(args[2]);
		String cardName = args[3];

		int mealId = defCompanyMaleMeal.getId();
		int mealPrice = defCompanyMaleMeal.getPrice();
		int parentCardId = CardChecker.getParentEntryCard(defaccountId);
		Date expireDate = DateUtils.offsetDay(30);
		String isprivate = args[7];
		String showCardMealPrice = args[8];
		String examNote = args[9];

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
		hmd.setHospitalId(defhospital.getId());
		hmd.setIsMealForSelf(true);
		hmd.setIsMealForFamily(false);
		hmd.setMealId(mealId);
		hmd.setMealPrice(mealPrice);
		List<HostpitalMealDto> cardMealList = new ArrayList<HostpitalMealDto>();
		cardMealList.add(hmd);
		CardDto cdt = new CardDto();
		cdt.setAccountIdList(accountIds);
		cdt.setBookingDeadline(null);
		cdt.setCard(card);
		cdt.setCardMealList(cardMealList);
		cdt.setCompanyId(companyId);
		int organizationType = HospitalChecker.getOrganizationType(defhospital.getId());
		cdt.setNewCompanyId(companyId);
		cdt.setOrganizationType(organizationType);
		cdt.setIsSendBookingMsg(false);
		cdt.setIsSendCardMsg(false);
		cdt.setExamNote(examNote);
		//隐价卡,传递套餐价格
		if(Boolean.valueOf(showCardMealPrice))
			cdt.setMealPrice(mealPrice);

		String jbody = JSON.toJSONString(cdt);
		System.out.println(jbody);

		if (checkdb) {
			String sql = "SELECT * FROM tb_card WHERE card_name = '" + cardName + "' " + "and parent_card_id = ? "
					+ "and status = 1 ";
			log.info("sql:" + sql);
			List<Map<String, Object>> rets = DBMapper.query(sql, parentCardId);
			beforenum = rets.size();
		}

		waitto(3);
		HttpResult result = httpclient.post(Card_DistributeCard, jbody);

		Assert.assertEquals(result.getCode(), HttpStatus.SC_OK, "批量发卡:" + result.getBody());
		Assert.assertEquals(result.getBody(), "{}");

		CardChecker.waitDistributeCardProc(httpclient);
		if (checkdb) {
			// 卡数量
			String sql = "SELECT * FROM tb_card WHERE card_name = '" + cardName + "' " + "and parent_card_id = ? "
					+ "and status = 1 and capacity = balance";
			log.info("sql:" + sql);
			List<Map<String, Object>> rets = DBMapper.query(sql, parentCardId);
			afternum = rets.size();
			Assert.assertEquals(afternum, beforenum + 1);

			// 母卡金额
//			parentCardSql = "select * from tb_card where id=?";
//			parentCardList = DBMapper.query(parentCardSql, parentCardId);
			long nowAfterBalance = CardChecker.getParentBalanceFromRedis(parentCardId);
			long afterBalance = parentCardPrice - capacity;
			Assert.assertEquals(afterBalance, nowAfterBalance);

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
			Assert.assertEquals(companyId, cardList.get(0).get("new_company_id"));
			Assert.assertEquals(organizationType, cardList.get(0).get("organization_type"));
			Assert.assertEquals(defhospital.getId(), cardList.get(0).get("from_hospital"));
			Assert.assertEquals(defaccountId, cardList.get(0).get("manager_id"));
			
			//结算相关
			Assert.assertEquals(Long.parseLong(cardList.get(0).get("freeze_balance").toString()),0l);//冻结金额为0
			Assert.assertEquals(Integer.parseInt(cardList.get(0).get("hospital_settlement_status").toString()),SettlementHospitalConfirmEnum.UNSETTLEMENT.getCode().intValue());//未结算
			Assert.assertNull(cardList.get(0).get("settlement_batch_sn"));
			String cardId = cardList.get(0).get("id").toString();

			System.out.println("卡ID:" + cardId);

			// tb_card_batch
			String batchSql = "select batch.id,batch.company_id,batch.card_name,batch.capacity,batch.amount,batch.operator_id,batch.is_send_bookingmsg,batch.remark,batch.default_meal_id,batch.exam_note,batch.is_send_card_msg,batch.query_condition,batch.expired_date,batch.new_company_id,batch.organization_type from tb_card_batch batch LEFT JOIN tb_card card on card.batch_id= batch.id where card.id=?";
			List<Map<String, Object>> batchList = DBMapper.query(batchSql, cardId);
			Assert.assertEquals(cardName, batchList.get(0).get("card_name"));
			Assert.assertEquals(capacity, batchList.get(0).get("capacity"));
			Assert.assertEquals(accountIds.size(), batchList.get(0).get("amount"));
			Assert.assertEquals(defaccountId, batchList.get(0).get("operator_id"));
			Assert.assertEquals(companyId, batchList.get(0).get("new_company_id"));
			Assert.assertEquals(organizationType, batchList.get(0).get("organization_type"));

			// tb_card_direction_meal_relation
			String cardMealRelationSql = "select * from tb_card_direction_meal_relation where card_id=?";
			List<Map<String, Object>> cardMealRelationList = DBMapper.query(cardMealRelationSql, cardId);
			Assert.assertEquals(mealId, cardMealRelationList.get(0).get("meal_id"));
			Assert.assertEquals(defhospital.getId(), cardMealRelationList.get(0).get("hospital_id"));
			Assert.assertEquals(1, cardMealRelationList.get(0).get("meal_for_self"));
			Assert.assertEquals(0, cardMealRelationList.get(0).get("meal_for_family"));

			// tb_card_settings
			String settingSql = "select * from tb_card_settings where card_id=?";
			List<Map<String, Object>> settingList = DBMapper.query(settingSql, cardId);
			Assert.assertEquals(showCardMealPrice.equals("true") ? 1 : 0,
					settingList.get(0).get("is_show_card_meal_price"));
			Assert.assertEquals(isprivate.equals("true") ? 1 : 0, settingList.get(0).get("isprivate"));

			// paylog
			String payLogSql = "select * from tb_paylog where trade_body_type=? and trade_type = ? order by id desc limit 1";
			List<Map<String, Object>> payLogList = DBMapper.query(payLogSql, PayConsts.TradeBodyTypes.Card,
					PayConsts.TradeTypes.SendCard);

			Assert.assertEquals(parentCardId, payLogList.get(0).get("trade_body"));
			Assert.assertEquals(Float.valueOf(capacity + ""),
					Math.abs(Float.valueOf(payLogList.get(0).get("amount").toString())));
//			Assert.assertEquals(afterBalance + "", payLogList.get(0).get("surplus").toString());
			Assert.assertEquals(PayConsts.TradeStatus.Successful, payLogList.get(0).get("status"));

			// tb_manager_card_relation
			String managerCardRelationSql = "select * from tb_manager_card_relation where card_id=?";
			List<Map<String, Object>> managerCardRelationList = DBMapper.query(managerCardRelationSql, cardId);
			Assert.assertEquals(defaccountId, managerCardRelationList.get(0).get("manager_id"));
			Assert.assertEquals(companyId, managerCardRelationList.get(0).get("new_company_id"));
			Assert.assertEquals(organizationType, managerCardRelationList.get(0).get("organization_type"));

			// 体检须知
			String examNoteSql = "select batch.exam_note from tb_card_batch batch LEFT JOIN  tb_card card on card.batch_id = batch.id where card.id=?";
			List<Map<String, Object>> examNoteList = DBMapper.query(examNoteSql, cardId);
			Assert.assertEquals(examNote, examNoteList.get(0).get("exam_note"));

			// 用于撤销卡
			for (Map<String, Object> ret : rets) {
				Card card1 = new Card();
				card1.setBalance(Long.parseLong(ret.get("balance").toString()));
				card1.setCapacity(Long.parseLong(ret.get("capacity").toString()));
				card1.setCardName(ret.get("card_name").toString());
				card1.setId(Integer.parseInt(ret.get("id").toString()));
				card1.setStatus(Integer.parseInt(ret.get("status").toString()));
				card1.setAccountId(Integer.parseInt(ret.get("account_id").toString()));
				cardList1.add(card1);
			}
		}
	}

	

	@AfterTest(description = "撤销发卡", groups = { "qa" }, alwaysRun = true)
	public void revocCard() throws SqlException {
		// revocCard
		String cardids = "";
		int parentCardId = 0;
		long beforeParentCardAmount = 0l;
		if (cardList1.size() > 0) {
			List<CardRecordDto> cardRecordList = new ArrayList<CardRecordDto>();
			for (Card c : cardList1) {
				// 先撤销前需要判断卡是否已撤销
				String str = "SELECT * FROM tb_card WHERE id = " + c.getId() + "";
				log.info("sql:" + str);
				List<Map<String, Object>> rets = DBMapper.query(str);
				for (Map<String, Object> m : rets) {
					int status = Integer.parseInt(m.get("status").toString());
					parentCardId = Integer.parseInt(m.get("parent_card_id").toString());
					beforeParentCardAmount = CardChecker.getParentBalanceFromRedis(parentCardId);
					if (status != 2) {
						cardids = cardids + c.getId() + ",";
						CardRecordDto crd = new CardRecordDto();
						String sql1 = "select name from tb_examiner where customer_id =? and manager_id = ? and new_company_id = ?";
						List<Map<String, Object>> rs = DBMapper.query(sql1, c.getAccountId(), defaccountId,
								companyId);
						Examiner ba = new Examiner();
						ba.setId(c.getId());
						ba.setName(rs.get(0).get("name").toString());
						crd.setCard(c);
						crd.setAccount(ba);
						cardRecordList.add(crd);
					}
				}
			}
			if (cardRecordList.size() > 0) {
				log.info(JSON.toJSONString(cardRecordList));
				Map<String, Object> maps = new HashMap<String, Object>();
				maps.put("cleanCardRecord", true);
				HttpResult ret = httpclient.post(Card_RevocCard, maps, JSON.toJSONString(cardRecordList));

				Assert.assertEquals(ret.getCode(), HttpStatus.SC_OK, "撤销卡:" + ret.getBody());
				Assert.assertEquals(ret.getBody(), "{}");

				if (checkdb) {
					waitto(2);
					long dbRecovBalance = 0l;
					log.info("cardids:" + cardids);
					String sql = "SELECT * FROM tb_card WHERE  id in ( " + cardids.substring(0, cardids.length() - 1)
							+ " )";

					log.info("sql:" + sql);
					List<Map<String, Object>> rets = DBMapper.query(sql);
					Assert.assertEquals(rets.size(), cardList1.size());

					for (Map<String, Object> m : rets){
						Assert.assertEquals(Integer.parseInt(m.get("status").toString()), 2);
						Assert.assertEquals(Integer.parseInt(m.get("balance").toString()), 0);//金额清空
						if(m.get("recoverable_balance")!=null)
							dbRecovBalance += Long.parseLong(m.get("recoverable_balance").toString());

					}
					long afterParentCardAmount = CardChecker.getParentBalanceFromRedis(parentCardId);

					String acccounts = accountIds.toString().substring(1, accountIds.toString().length() - 1);
					String sql2 = "SELECT * FROM tb_examiner WHERE customer_id  IN (" + acccounts
							+ ")  AND manager_id = ? AND new_company_id = ?";
					log.info(sql2);
					List<Map<String, Object>> rets2 = DBMapper.query(sql2, defaccountId, defnewcompany.getId());
					for (Map<String, Object> r : rets2) {
						Assert.assertNull(r.get("recent_card"));
					}
				}
			}
		}
	}

	@DataProvider
	public Iterator<String[]> distribute() {
		try {
			return CvsFileUtil.fromCvsFileToIterator("./csv/card/distributeCard_success.csv", 7);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

}
