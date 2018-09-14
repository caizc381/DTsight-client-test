package com.tijiantest.testcase.crm.settlement.consume;

import com.jayway.jsonpath.JsonPath;
import com.tijiantest.base.HttpResult;
import com.tijiantest.base.dbcheck.AccountChecker;
import com.tijiantest.base.dbcheck.SettleChecker;
import com.tijiantest.model.account.Account;
import com.tijiantest.model.common.LogTypeEnum;
import com.tijiantest.model.settlement.*;
import com.tijiantest.testcase.crm.settlement.SettleBase;
import com.tijiantest.util.db.DBMapper;
import com.tijiantest.util.db.SqlException;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 医院CRM->结算管理->消费额度->确认按钮
 * 操作:确认
 * @author huifang
 *
 */
public class AuditConsumeQuotaDetailTest extends SettleBase{

	private static String consumeQuotaDetailSn = null;
	/**
	 * ops运营经理审核/修改完账单后,医院确认账单
	 */
	@Test(description = "收款账单医院-确认",groups = {"qa"})
	public void test_01_hospitalReceipt() throws SqlException {
		String scenes = "("+ ConsumeQuotaDetailSceneEnum.HOSPITAL_INVOICE.getCode()+","
				+ConsumeQuotaDetailSceneEnum.FINANCIAL_ADJUST.getCode()+","+ConsumeQuotaDetailSceneEnum.PLATFORM_SERVICE.getCode()+")";
		List<Map<String,Object>> querylist = DBMapper.query("select * from tb_trade_consume_quota_detail where organization_id = "+defSettHospitalId+" " +
					"and status = "+ConsumeQuotaDetailStatusEnum.HOSPITAL_HAS_CONFIRMED.getCode() +" and scene in "+scenes);
			if(querylist == null || querylist.size() == 0){
				log.error("医院id="+defSettHospitalId+"没有可用于确认的消费额度，医院无法确认消费额度");
				return;
			}
		consumeQuotaDetailSn = querylist.get(0).get("sn").toString();
		List<NameValuePair> params = new ArrayList<>();
		params.add(new BasicNameValuePair("consumeQuotaDetailSn" ,consumeQuotaDetailSn));
		HttpResult response = httpclient.get(AuditConsumeQuotaDetail,params);;
		Assert.assertEquals(response.getCode(), HttpStatus.SC_OK);
		String body = response.getBody();
		boolean result = JsonPath.read(body,"$.result");
		Assert.assertTrue(result);

		if(checkdb){
			//chec消费额度确认
		   	List<TradeConsumeQuotaDetail> dbConsumList = SettleChecker.getTradeConsumeQuotaDetail("select * from tb_trade_consume_quota_detail where sn = '"+consumeQuotaDetailSn+"'");
		   	Assert.assertEquals(dbConsumList.size(),1);
		   	TradeConsumeQuotaDetail dbPlat = dbConsumList.get(0);
		   	Assert.assertEquals(dbPlat.getStatus(),ConsumeQuotaDetailStatusEnum.HOSPITAL_HAS_CONFIRMED.getCode());
		 
		    //check流转日志
		   	Account account = AccountChecker.getAccountById(defSettAccountId);
			List<TradeCommonLogResultDTO> dbCommonLogList = SettleChecker.getTradeCommonLogList(consumeQuotaDetailSn, LogTypeEnum.LOG_TYPE_SETTLEMENT_CONSUME_QUOTA_AUDIT.getValue(),"desc");//获取消费额度明细
			Assert.assertEquals(dbCommonLogList.get(0).getOperatorId().intValue(),defSettAccountId);
			Assert.assertEquals(dbCommonLogList.get(0).getOperatorName(),account.getName());
			Assert.assertEquals(dbCommonLogList.get(0).getOperation(),"医院确认");
			Assert.assertEquals(dbCommonLogList.get(0).getRefSn(),consumeQuotaDetailSn);
			Assert.assertEquals(dbCommonLogList.get(0).getLogType().intValue(),2);
			Assert.assertEquals(sdf.format(dbCommonLogList.get(0).getGmtCreated().getTime()),sdf.format(new Date()));

		}
	}
}
