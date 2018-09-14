package com.tijiantest.testcase.crm.account.query;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.message.BasicNameValuePair;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.jayway.jsonpath.JsonPath;
import com.tijiantest.base.HttpResult;
import com.tijiantest.model.card.Card;
import com.tijiantest.testcase.crm.CrmBase;
import com.tijiantest.util.CvsFileUtil;
import com.tijiantest.util.db.DBMapper;
import com.tijiantest.util.db.SqlException;

/**
 * 卡分页查询
 * CRM->订单&用户->用户查询->卡分页页面
 * @author huifang
 *
 */
public class CardInfoByAccountTest extends CrmBase{

	
	@Test(description = "卡分页查询" ,dataProvider="card_page" ,groups = {"qa","online"})
	public void test_01_cardMeal_success(String ... args) throws ParseException, IOException, SqlException{
		int accountId = Integer.parseInt(args[1]);
		int rowcount = Integer.parseInt(args[2]);
		int currPage = Integer.parseInt(args[3]);
		int pageSize = Integer.parseInt(args[4]);
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("hospitalId", defhospital.getId()+""));
		params.add(new BasicNameValuePair("accountId",accountId+""));
		params.add(new BasicNameValuePair("rowCount",rowcount+""));
		params.add(new BasicNameValuePair("currentPage",currPage+""));
		params.add(new BasicNameValuePair("pageSize",pageSize+""));
		
		HttpResult result = httpclient.get(AccountQuery_CardInfoByAccount,params);
		Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);
		
		String jbody = result.getBody();
		log.info("body:"+jbody);
		int page =  JsonPath.read(jbody,"$.page.pageSize");
		int curPage = JsonPath.read(jbody,"$.page.currentPage");
		int rowCount = JsonPath.read(jbody,"$.page.rowCount");
		String records = JsonPath.read(jbody,"$.records").toString();
		Assert.assertEquals(page,pageSize);
		Assert.assertEquals(curPage, currPage);
		
		List<Card> list = JSON.parseObject(records,new TypeReference<List<Card>>(){});
		Assert.assertNotNull(jbody);
		if(checkdb){
			//核实前1页卡的详细信息
			String sql = "SELECT c.id ,c.card_name,c.card_num,c.capacity,c.balance ,batch.new_company_id,batch.organization_type FROM  tb_manager_card_relation m "
					  +"LEFT JOIN tb_card c ON c.id = m.card_id "
					  +"LEFT JOIN tb_account_role  role ON role.account_id = m.manager_id "
					  +"LEFT JOIN tb_card_batch batch on batch.id= c.batch_id "
					  +"WHERE (m.manager_id IN (SELECT manager_id FROM tb_manager_company_relation WHERE hospital_id = ?) "
					  + "AND role.role_id NOT IN (2,4) and c.account_id = ?) ORDER BY c.recharge_time DESC ,c.id ASC LIMIT ?";

			List<Map<String,Object>> mlist = DBMapper.query(sql, defhospital.getId(),accountId,page);
			Assert.assertEquals(mlist.size(),list.size());
			for(int i=0;i<mlist.size();i++){
				Assert.assertEquals(Integer.parseInt(mlist.get(i).get("id").toString()),list.get(i).getId().intValue());
				Assert.assertEquals(mlist.get(i).get("card_name").toString(),list.get(i).getCardName());
				Assert.assertEquals(mlist.get(i).get("card_num").toString(),list.get(i).getCardNum());
				Assert.assertEquals(Long.parseLong(mlist.get(i).get("capacity").toString()),list.get(i).getCapacity().longValue());
				Assert.assertEquals(Long.parseLong(mlist.get(i).get("balance").toString()),list.get(i).getBalance().longValue());
				Assert.assertEquals(mlist.get(i).get("new_company_id"), list.get(i).getNewCompanyId());
				Assert.assertEquals(mlist.get(i).get("organization_type"), list.get(i).getOrganizationType());
			}
			
			//对比卡数量
			String sql2 = "SELECT count(c.id) FROM  tb_manager_card_relation m "
					  +"LEFT JOIN tb_card c ON c.id = m.card_id "
					  +"LEFT JOIN tb_account_role  role ON role.account_id = m.manager_id "
					  +"WHERE (m.manager_id IN (SELECT manager_id FROM tb_manager_company_relation WHERE hospital_id = ?) "
					  + "AND role.role_id NOT IN (2,4) and c.account_id = ?) ";

			mlist = DBMapper.query(sql2, defhospital.getId(),accountId);
			Object mcount = mlist.get(0).get("count(c.id)");//total cardNum
			if(mcount != null){
				log.info("查询到卡数量:"+mcount.toString());
				Assert.assertEquals(rowCount, Integer.parseInt(mcount.toString()));
				}  
		}
		
	}
	
	@DataProvider
	public Iterator<String[]> card_page(){
		try {
			return CvsFileUtil.fromCvsFileToIterator("./csv/account/query/card_page.csv",7);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}
}

