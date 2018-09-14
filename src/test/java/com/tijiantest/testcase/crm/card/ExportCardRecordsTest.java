package com.tijiantest.testcase.crm.card;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.tijiantest.base.HttpResult;
import com.tijiantest.model.card.Card;
import com.tijiantest.testcase.crm.CrmMediaBase;

/**
 * 根据卡id列表、单位id下载卡信息文件
 * @author huifang
 *
 */
public class ExportCardRecordsTest extends CrmMediaBase{

	@Test(description = "根据卡id列表、单位id下载卡信息文件",groups = {"qa"},dependsOnGroups="crm_allRecords")
	public void test_01_exportcardrecords_success(){
		Card card = defCompanyCard;
		int cardIds = card.getId();
		int newCompanyId = card.getNewCompanyId();
		int organizationType = card.getOrganizationType();
		
		List<NameValuePair> pairs = new ArrayList<>();
		pairs.add(new BasicNameValuePair("cardIds", cardIds+""));
		pairs.add(new BasicNameValuePair("newCompanyId", newCompanyId+""));
		pairs.add(new BasicNameValuePair("organizationType", organizationType+""));
		
		HttpResult result = httpclient.post(Card_ExportCardRecords,pairs);
//		String body = result.getBody();
//		System.out.println(body);
		Assert.assertEquals(result.getCode(), HttpStatus.SC_OK,"接口返回..."+result.getBody());
		
	}
}
