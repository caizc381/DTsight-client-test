package com.tijiantest.testcase.crm.card;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.message.BasicNameValuePair;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.tijiantest.base.HttpResult;
import com.tijiantest.model.resource.meal.Meal;
import com.tijiantest.testcase.crm.CrmBase;
import com.tijiantest.util.db.DBMapper;
import com.tijiantest.util.db.SqlException;

/**
 * 根据体检中心id、体检单位id查询单位套餐
 * @author huifang
 *
 */
public class CardCompanyTest extends CrmBase {
	   
		@Test(description = "卡单位套餐查询",groups = {"qa","online"},enabled = false)
		public void test_01_cardCompany_success() throws ParseException, IOException, SqlException{
			
			// make parameters
			List<NameValuePair>  params = new ArrayList<NameValuePair>();
			 params.add(new BasicNameValuePair("hospitalId", defhospital.getId()+""));
			 params.add(new BasicNameValuePair("companyId", defnewcompany.getId()+""));
			
		    //get
		    HttpResult response = httpclient.get(Card_CompanyMeals, params);
		    
		    //assert
		    Assert.assertEquals(response.getCode() , HttpStatus.SC_OK);   
		    String body = response.getBody();
		    Assert.assertNotNull(body);
		    
		    if(checkdb){
		    	//sort responseList
			    List<Meal> list = JSON.parseObject(body,new TypeReference<List<Meal>>(){});
			    Collections.sort(list, new Comparator<Meal>() {
			    	@Override
			    	public int compare(Meal o1,
			    			com.tijiantest.model.resource.meal.Meal o2) {
			    		return o1.getId()-o2.getId();
			    	}
				});
			    
		    	String sql = "SELECT  m.id , m.hospital_id , m.name, m.description,m.pinyin,m.discount,"+
		    					"m.external_discount,m.gender,m.type,m.disable,m.keyword,m.init_price,"+
		    					"m.price,m.tip_text,m.sequence,m.update_time, cm.account_id,cm.company_id," + 
		    					"se.meal_id, se.show_meal_price,se.show_item_price,se.only_show_meal_item,se.adjust_price " +
		    				    "FROM tb_meal m "+
		    					"LEFT JOIN tb_meal_customized cm ON cm.meal_id = m.id " +
		    				    "LEFT JOIN tb_meal_settings se ON se.meal_id = m.id " +
		    					"WHERE  cm.new_company_id = ? AND m.hospital_id = ? AND m.`disable` < 2 " +
		    					"AND m.type = 1 AND m.gender != 2 AND cm.account_id = ?  order by m.id ";
		    	List<Map<String,Object>> relist = DBMapper.query(sql, defnewcompany.getId(),defhospital.getId(),defaccountId);
		    	Assert.assertEquals(list.size(),relist.size());
		    	for(int i=0;i< list.size();i++){
		    		Assert.assertEquals(list.get(i).getId(),relist.get(i).get("id"));
		    		Assert.assertEquals(list.get(i).getName(),relist.get(i).get("name"));
		    		Assert.assertEquals(list.get(i).getPinyin(),relist.get(i).get("pinyin"));
		    		Assert.assertEquals(list.get(i).getDiscount(),relist.get(i).get("discount"));
		    		Assert.assertEquals(list.get(i).getDisable(),relist.get(i).get("disable"));
		    		Assert.assertEquals(list.get(i).getPrice(),relist.get(i).get("price"));
		    		Assert.assertEquals(list.get(i).getInitPrice(),relist.get(i).get("init_price"));
		    		Assert.assertEquals(list.get(i).getGender(),relist.get(i).get("gender"));
		    	}
		    }
		}
		
		
		
}
