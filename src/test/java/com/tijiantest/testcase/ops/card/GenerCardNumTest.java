package com.tijiantest.testcase.ops.card;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.tijiantest.base.Flag;
import com.tijiantest.base.HttpResult;
import com.tijiantest.testcase.ops.OpsBase;
import com.tijiantest.util.db.DBMapper;
import com.tijiantest.util.db.SqlException;

/**
 * 生成实体卡
 * @author huifang
 *
 */
public class GenerCardNumTest extends OpsBase {

	@Test(description = "生成实体卡段", groups = { "qa" ,"manage_generCardNum"})
	public void test_01_generCardNum() throws SqlException, ParseException {
	
		int maxCardNum = 0;
		int index = 0;
		int  nums = 2;
		if(checkdb){
			String sql = "SELECT * FROM tb_card_segment ORDER BY id desc limit 1";
			List<Map<String,Object>> dblist1 = DBMapper.query(sql);
			Map<String,Object> map = dblist1.get(0);
			index = Integer.parseInt(map.get("id").toString());
			maxCardNum = Integer.parseInt(map.get("card_num").toString().substring(2));
		}
		List<NameValuePair> params = new ArrayList<>();
		params.add(new BasicNameValuePair("increment", nums+""));
		HttpResult result = httpclient.get(Flag.OPS,OPS_GenerCardNum,params);
		
		Assert.assertEquals(result.getCode(),HttpStatus.SC_OK);
		
		if(checkdb){
			String sql = "SELECT * FROM tb_card_segment ORDER BY id desc limit ?";
			List<Map<String,Object>> dblist1 = DBMapper.query(sql, nums);
			for(int i=0;i<nums;i++){
				Map<String,Object> map = dblist1.get(i);
				Assert.assertEquals(map.get("card_num").toString(),"MT"+String.format("%07d", maxCardNum+nums-i));
				Assert.assertEquals(Integer.parseInt(map.get("id").toString()),index+nums-i);
			}
		}
	}
}
