package com.tijiantest.testcase.crm.card;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpStatus;
import org.apache.http.ParseException;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.alibaba.fastjson.JSON;
import com.tijiantest.base.HttpResult;
import com.tijiantest.model.card.Card;
import com.tijiantest.testcase.crm.CrmBase;
import com.tijiantest.util.db.DBMapper;
import com.tijiantest.util.db.SqlException;
/**
 * 根据客户经理id查询母卡信息
 * @author huifang
 *
 */
public class ParentCardTest extends CrmBase{


	@Test(description = "查询母卡信息",groups = {"qa","online"})
	public void test_01_parentCard_success() throws ParseException, IOException, SqlException{
	    //get
	    HttpResult response = httpclient.get(Card_ParentCard);
	    
	    //assert
	    Assert.assertEquals(response.getCode() , HttpStatus.SC_OK);   
	    Card card = JSON.parseObject(response.getBody(),Card.class);
	    
	    if(checkdb){	    
	    	String sql = "SELECT * FROM tb_card WHERE account_id = ? and parent_card_id is null";
	    	
	    	List<Map<String,Object>> relist = DBMapper.query(sql, defaccountId);
	    	
	    	Assert.assertEquals(relist.size(),1);
	    	Map<String,Object> m = relist.get(0);
	    	
	    	Assert.assertEquals(card.getCardName(),m.get("card_name"));
	    	Assert.assertEquals(card.getCardNum(),m.get("card_num"));
	    	Assert.assertEquals(card.getCapacity(),m.get("capacity"));
	    	Assert.assertEquals(card.getBalance(),m.get("balance"));
	    	Assert.assertEquals(card.getType(),m.get("type"));
	    	Assert.assertEquals(card.getStatus(),m.get("status"));
	    	Assert.assertEquals(card.getFromHospital(),m.get("from_hospital"));
	    	Assert.assertEquals(card.getAccountId(),m.get("account_id"));
	    	Assert.assertEquals(simplehms.format(card.getExpiredDate()).split(" ")[0],m.get("expired_date").toString().split(" ")[0]);
	    	Assert.assertEquals(card.getRecoverableBalance(),m.get("recoverable_balance"));
	    }
	}
	
	
	
}
