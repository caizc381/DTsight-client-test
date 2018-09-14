package com.tijiantest.testcase.crm.settlement.consume;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.tijiantest.base.HttpResult;
import com.tijiantest.base.dbcheck.SettleChecker;
import com.tijiantest.model.settlement.*;
import com.tijiantest.testcase.crm.settlement.SettleBase;

/**
 * 获取医院未确认的消费额度
 * OPS添加的本医院的消费额度（医院开票|账务调整）,待确认状态
 * @author huifang
 * @param 
 *
 */
public class ListNotAuditedConsumeQuotaDetailTest extends SettleBase{

	@Test(description = "获取医获取医院未确认的消费额度",groups = {"qa"})
	public void test_01_getHospitalPlatFormBillList(){
		JSONObject jo = new JSONObject();
		jo.put("organizationId", defSettHospitalId);
		HttpResult response = httpclient.post(ListNotAuditedConsumeQuotaDetail, jo.toJSONString());
		Assert.assertEquals(response.getCode(), HttpStatus.SC_OK);
		String body = response.getBody();
		List<TradeConsumeQuotaDetail> consumeQuotaList = new ArrayList<TradeConsumeQuotaDetail>();
		System.out.println(body);
		//当返回不为空,获取返回的字段值
		if(!body.equals("{}") && !body.equals("")){
			consumeQuotaList = JSONArray.parseArray(body, TradeConsumeQuotaDetail.class);
		}
	
		if(checkdb){
			List<TradeConsumeQuotaDetail> dbConsumeList = null;
			String scenes = "("+ ConsumeQuotaDetailSceneEnum.HOSPITAL_INVOICE.getCode()+","
					+ConsumeQuotaDetailSceneEnum.FINANCIAL_ADJUST.getCode()+","+ConsumeQuotaDetailSceneEnum.PLATFORM_SERVICE.getCode()+")";
			String sql = "select * from tb_trade_consume_quota_detail where organization_id = "+defSettHospitalId  + " and is_deleted = 0 and scene in "+scenes+" and status = 1 order by gmt_created desc";
			log.info("sql..."+sql);
			dbConsumeList = SettleChecker.getTradeConsumeQuotaDetail(sql);
			Assert.assertEquals(consumeQuotaList.size(),dbConsumeList.size());
			//所有的待确认消费额度验证
			for(int i=0 ; i<dbConsumeList.size();i++){
				Assert.assertEquals(consumeQuotaList.get(i).getId(),dbConsumeList.get(i).getId());
				Assert.assertEquals(consumeQuotaList.get(i).getCompanyId(),dbConsumeList.get(i).getCompanyId());
				Assert.assertEquals(consumeQuotaList.get(i).getCompanyName(),dbConsumeList.get(i).getCompanyName());
				Assert.assertEquals(consumeQuotaList.get(i).getGmtCreated(),dbConsumeList.get(i).getGmtCreated());
				Assert.assertEquals(consumeQuotaList.get(i).getGmtModified(),dbConsumeList.get(i).getGmtModified());
				Assert.assertEquals(consumeQuotaList.get(i).getOrganizationId(),dbConsumeList.get(i).getOrganizationId());
				Assert.assertEquals(consumeQuotaList.get(i).getIsDeleted(),dbConsumeList.get(i).getIsDeleted());
				Assert.assertEquals(consumeQuotaList.get(i).getSn(),dbConsumeList.get(i).getSn());
				Assert.assertEquals(consumeQuotaList.get(i).getStatus(),dbConsumeList.get(i).getStatus());
				Assert.assertEquals(consumeQuotaList.get(i).getRemark(),dbConsumeList.get(i).getRemark());
				Assert.assertEquals(consumeQuotaList.get(i).getScene(),dbConsumeList.get(i).getScene());
				Assert.assertEquals(consumeQuotaList.get(i).getAmount(),dbConsumeList.get(i).getAmount());
				Assert.assertEquals(consumeQuotaList.get(i).getPayTime(),dbConsumeList.get(i).getPayTime());


			}
		}
	}
	  
}
