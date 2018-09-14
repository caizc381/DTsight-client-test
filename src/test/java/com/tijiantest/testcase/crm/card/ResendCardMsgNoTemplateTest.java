package com.tijiantest.testcase.crm.card;

import com.alibaba.fastjson.JSON;
import com.jayway.jsonpath.JsonPath;
import com.tijiantest.base.HttpResult;
import com.tijiantest.base.dbcheck.AccountChecker;
import com.tijiantest.base.dbcheck.CardChecker;
import com.tijiantest.base.dbcheck.CompanyChecker;
import com.tijiantest.model.account.AccountRelation;
import com.tijiantest.model.account.AccountRelationInCrm;
import com.tijiantest.model.organization.OrganizationTypeEnum;
import com.tijiantest.testcase.crm.CrmBase;
import com.tijiantest.util.db.DBMapper;
import com.tijiantest.util.db.SqlException;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.message.BasicNameValuePair;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 发送短信，不使用模版
 * CRM->单位体检->发卡记录->所有发卡记录（发短信）
 * @author huifang
 *
 */
public class ResendCardMsgNoTemplateTest extends CrmBase{

	private String newMobile = "18054545555";

	@Test(description = "发送短信，不使用模版",groups = {"qa"})
	public void test_01_resendCardMsgNoTemplate() throws ParseException, IOException, SqlException{
		//1.随机找一个单位
		int companyId = CompanyChecker.getRandomHaveCardCommonHospitalCompany(defhospital.getId(),defaccountId).getId();
		//2.从单位找一个用户
		List<AccountRelationInCrm> accountRelationInCrmList = AccountChecker.getCompanyCardAccountRelations(companyId,defaccountId);
		AccountRelationInCrm accountRelationInCrm = accountRelationInCrmList.get(0);
		int customerId = accountRelationInCrm.getCustomerId();
		String mobile = null;
		if(accountRelationInCrm.getMobile()!=null){
				mobile = accountRelationInCrm.getMobile();
			    if(mobile.startsWith("123") || mobile.startsWith("100")||mobile.length() !=11 )
					DBMapper.update("update tb_examiner set mobile = '"+newMobile+"' where manager_id = "+defaccountId+" and customer_id = "+customerId+" and new_company_id = "+companyId);
				else
					newMobile = mobile;
		}
		else{
			log.error("没有手机号，无法发送短信,给用户设置手机号");
			DBMapper.update("update tb_examiner set mobile = '"+newMobile+"' where manager_id = "+defaccountId+" and customer_id = "+customerId+" and new_company_id = "+companyId);
		}
		//获取数据库当前时间
		String beforeDate = simplehms.format(DBMapper.query("select now()").get(0).get("now()"));

		int cardId = CardChecker.getCardByAccountANDHospitalCompany(defhospital.getId(),companyId,defaccountId,customerId).get(0);
		int cardBatchId = CardChecker.getCardById(cardId).getBatchId();
		String content = "测试发送短信";
	    List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("batchId", cardBatchId+""));
		params.add(new BasicNameValuePair("mobiles[]",newMobile+""));
		params.add(new BasicNameValuePair("content",content));
	    HttpResult response = httpclient.post(Card_ResendCardMsgNoTemplate,params);
	    //assert
	    Assert.assertEquals(response.getCode() , HttpStatus.SC_OK);
		String body = response.getBody();
		log.info(body);
		Assert.assertTrue(response.getBody().equals("")||response.getBody().equals("{}"),"发送短信不使用模板接口返回"+response.getBody());

		if(checkdb){
			boolean isSend = false;
			//重複等待多次
			for(int i=0;i<5;i++){
				String sql = "select * from tb_sms_send_record where mobile = '"+newMobile+"' and content = '"+content+"'  and insert_time >= '"+beforeDate+"' order by id desc";
				log.info(sql);
				List<Map<String,Object>> dblist = DBMapper.query(sql);
				if(dblist.size() == 0){
					waitto(1);
					continue;
				}
				isSend = true;
				Assert.assertEquals(dblist.size(),1);
				}
				if(!isSend){
				   Assert.assertEquals(1,0,"嘗試了5次，在DB中未查詢到短信内容");
				}
		}
	}
}
