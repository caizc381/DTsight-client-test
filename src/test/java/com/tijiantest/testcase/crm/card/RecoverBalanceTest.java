package com.tijiantest.testcase.crm.card;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.tijiantest.model.account.Examiner;
import org.apache.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.alibaba.fastjson.JSON;
import com.tijiantest.base.HttpResult;
import com.tijiantest.base.dbcheck.CardChecker;
import com.tijiantest.model.account.AccountRelationInCrm;
import com.tijiantest.model.card.Card;
import com.tijiantest.model.card.CardRecordDto;
import com.tijiantest.model.card.CardStatusEnum;
import com.tijiantest.testcase.crm.CrmMediaBase;
import com.tijiantest.util.db.DBMapper;
import com.tijiantest.util.db.SqlException;
/**
 * 回收卡的余额，已经撤销的卡余额不能回收，回收的余额将返还到客户经理的母卡中
 * crm v2.18 write
 * @author huifang
 *
 */
public class RecoverBalanceTest extends CrmMediaBase {
	protected static List<Card> recovBalaCardList = new ArrayList<Card>();
	
	@Test(description = "回收卡的余额",groups={"qa","crm_recoverBalance"},dataProvider="getCardForRevoke",dependsOnGroups={"crm_distributeCard"})
	public void test_01_recoverBalance_success(Card card) throws SqlException, ParseException{
		
		//先获取母卡余额
		String cardSql = "select * from tb_card where id=?";
		List<Map<String, Object>> cardList = DBMapper.query(cardSql, card.getId());
		int cardStatus = Integer.parseInt(cardList.get(0).get("status").toString());
		card.setStatus(cardStatus);
		long cardBalance = Integer.parseInt(cardList.get(0).get("balance").toString());

		String parentCardId = cardList.get(0).get("parent_card_id").toString();
//		cardList = DBMapper.query(cardSql, parentCardId);
//		int originalBalance = Integer.parseInt(cardList.get(0).get("balance").toString());
		long originalBalance = CardChecker.getParentBalanceFromRedis(Integer.parseInt(parentCardId));

		String cardids = "";
		if(card != null){
			List<CardRecordDto> cardRecordList = new ArrayList<CardRecordDto>();
			cardids = cardids + card.getId()+","; 
			CardRecordDto crd  = new CardRecordDto();
			crd.setCard(card);
			Examiner ba = new Examiner();
			ba.setId(defCompanyAccountId);
			ba.setName(companyName);
			crd.setAccount(ba);
			cardRecordList.add(crd);
			//回收余额前判断卡是否已经结算
			boolean cannotRecove = CardChecker.isCardInSettlement(card.getId());
			HttpResult ret = httpclient.post(Card_RecoverBalance, JSON.toJSONString(cardRecordList));
			System.out.println(ret.getBody());
			if(cannotRecove){
				Assert.assertEquals(ret.getCode(),HttpStatus.SC_BAD_REQUEST,"回收余额:"+ret.getBody());
				Assert.assertTrue(ret.getBody().contains("体检卡已结算，不能回收!"));
			}else{
				Assert.assertEquals(ret.getCode(),HttpStatus.SC_OK,"回收余额卡:"+ret.getBody());
				//回收卡金额的时候返回结果有时{}有事为空
				Assert.assertTrue(ret.getBody().equals("") || ret.getBody().equals("{}")); 
						
				if(checkdb){
					waitto(1);
					log.info("cardId:"+card.getId());
					Card retCard = CardChecker.getCardInfo(card.getId());
					log.info("cardid当前信息.."+retCard.getId()+"卡余额.."+retCard.getBalance()+"卡状态"+retCard.getStatus());
					if(card.getStatus().equals(CardStatusEnum.CANCELLED.getCode().intValue()))
						Assert.assertEquals(retCard.getStatus().intValue(),CardStatusEnum.CANCELLED.getCode().intValue());
					else
						Assert.assertEquals(retCard.getStatus().intValue(),CardStatusEnum.BALANCE_RECOVERED.getCode().intValue());
					Assert.assertEquals(retCard.getBalance().longValue(),0l);
					Assert.assertTrue(retCard.getRecoverableBalance().longValue()>0l);
					
					
//					cardList = DBMapper.query(cardSql, parentCardId);
//					int finalBalance = Integer.parseInt(cardList.get(0).get("balance").toString());
					long finalBalance =  CardChecker.getParentBalanceFromRedis(Integer.parseInt(parentCardId));
					
					System.out.println("origianlBalance = "+ originalBalance+" ---------- cardBalance="+cardBalance+" ======== fianlBalance="+finalBalance);
					Assert.assertEquals(originalBalance + cardBalance, finalBalance);
			}
		
			}
		}
 }
	@DataProvider
	public Object[][] getCardForRevoke() {
		Object[][] obj;
		obj = new Object[DistributeCardTest.cardList1.size()][];
		for(int i=0;i<DistributeCardTest.cardList1.size();i++){
			obj[i] = new Object[] {DistributeCardTest.cardList1.get(i)};
		}	
		return obj;
	}	
}
