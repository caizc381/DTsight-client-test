package com.tijiantest.testcase.crm.card;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jayway.jsonpath.JsonPath;
import com.tijiantest.model.account.Examiner;
import com.tijiantest.model.card.CardStatusEnum;
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
import com.tijiantest.testcase.crm.CrmBase;
import com.tijiantest.util.db.DBMapper;
import com.tijiantest.util.db.SqlException;

public class RevokeCardTest extends CrmBase{
  @Test(description = "撤卡",groups={"qa"}, dataProvider="getCardForRevoke", dependsOnGroups={"crm_distributeCard","crm_recoverBalance"})
  public void revokeCard(Card card) throws SqlException, ParseException {
		//revocCard
		 System.out.println("revokeCard开始执行！！！");
		 String cardids = "";
		 List<CardRecordDto> cardRecordList = new ArrayList<CardRecordDto>();
		cardids = cardids + card.getId()+","; 
		CardRecordDto crd  = new CardRecordDto();
		String sql1 = "select name from tb_examiner where customer_id =? and manager_id = ? and new_company_id = ?";
		List<Map<String, Object>> rs = null;
		try {
			rs = DBMapper.query(sql1,card.getAccountId(),defaccountId,defnewcompany.getId());
		} catch (SqlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    Examiner ba = new Examiner();
		ba.setId(card.getId());
		ba.setName(rs.get(0).get("name").toString());
		crd.setCard(card);
		crd.setAccount(ba);
		cardRecordList.add(crd);

		log.info(JSON.toJSONString(cardRecordList));
		Map<String,Object> maps = new HashMap<String,Object>();
		maps.put("cleanCardRecord", true);
	    int cardStatus = CardChecker.getCardInfo(card.getId()).getStatus();//实时获取卡的状态
		//回收余额前判断卡是否已经结算
		boolean cannotRecove = CardChecker.isCardInSettlement(card.getId());
		HttpResult ret = httpclient.post(Card_RevocCard,maps,JSON.toJSONString(cardRecordList));
		log.info("ret-code"+ret.getCode()+"..ret-body"+ret.getBody());
		if(cannotRecove){
			Assert.assertEquals(ret.getCode(),HttpStatus.SC_BAD_REQUEST,"撤销卡:"+ret.getBody());
			Assert.assertTrue(ret.getBody().contains("体检卡已结算，不能被撤销!"));
		}else{
			//卡本身是撤销状态
			if(cardStatus == CardStatusEnum.CANCELLED.getCode().intValue()){
				Assert.assertEquals(ret.getCode(),HttpStatus.SC_BAD_REQUEST,"撤销卡:"+ret.getBody());
				String code = JsonPath.read(ret.getBody(),"$.code");
				Assert.assertEquals(code,"EX_1_0_CARD_00_00_017");
			}else{
				Assert.assertEquals(ret.getCode(),HttpStatus.SC_OK,"撤销卡:"+ret.getBody());
				Assert.assertEquals(ret.getBody(),"{}");
			}

			if(checkdb){
				log.info("cardids:"+cardids);
				String sql = "SELECT * FROM tb_card WHERE  id in ( "+cardids.substring(0, cardids.length()-1) +" )";			
				log.info("sql:"+sql);
				List<Map<String, Object>> rets = null;
				try {
					rets = DBMapper.query(sql);
				} catch (SqlException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	   
				for(Map<String,Object> m : rets){				
					Assert.assertEquals(Integer.parseInt(m.get("status").toString()),2);
				}
			}
		}
		
		
		
  }
    
  @DataProvider
  public Object[][] getCardForRevoke() {
	  Object[][] obj;
	  int size = DistributeCardTest.cardList1.size()+RecoverBalanceTest.recovBalaCardList.size();
	  obj = new Object[size][];
	  for(int i=0;i<size;i++){
		  if (i<DistributeCardTest.cardList1.size())
			  obj[i] = new Object[] {DistributeCardTest.cardList1.get(i)};
		  else
			  obj[size-i] = new Object[] {RecoverBalanceTest.recovBalaCardList.get(i)};
	  }	
    return obj;
  }
}
