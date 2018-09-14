package com.tijiantest.testcase.ops.settlement;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.tijiantest.base.Flag;
import com.tijiantest.model.common.LogTypeEnum;
import com.tijiantest.testcase.ops.OpsBase;
import com.tijiantest.util.db.DBMapper;
import com.tijiantest.util.db.SqlException;
import org.apache.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jayway.jsonpath.JsonPath;
import com.tijiantest.base.HttpResult;
import com.tijiantest.base.dbcheck.SettleChecker;
import com.tijiantest.model.settlement.ConsumeQuotaDetailStatusEnum;
import com.tijiantest.model.settlement.HospitalPlatformBillStatusEnum;
import com.tijiantest.model.settlement.TradeCommonLogResultDTO;
import com.tijiantest.model.settlement.TradeConsumeQuotaDetail;
import com.tijiantest.model.settlement.TradeHospitalPlatformBill;

/**
 * 财务登陆OPS->结算系统->付款账单（财务审核中）
 * 操作:确认
 * @author huifang
 *
 */
public class AuditHospitalPlatformBillTest extends OpsBase{

	private static String platbillSn = null;
	/**
	 * ops运营经理审核/修改完账单后,医院确认账单
	 */
	@Test(description = "收款账单财务-确认",groups = {"qa","crm_auditHospitalPlatformBill"},dependsOnGroups="ops_updateReviewBill",ignoreMissingDependencies = true)
//	@Test(description = "收款账单财务-确认",groups = {"qa","crm_auditHospitalPlatformBill"})
	public void test_01_hospitalReceipt() throws SqlException {
		JSONObject jo = new JSONObject();
		platbillSn = ReviewBillTest.platBillSn;
		if(platbillSn == null){
			List<Map<String,Object>> querylist = DBMapper.query("select * from tb_trade_hospital_platform_bill where hospital_id = "+defSettHospitalId+" and settlement_view_type = 0 " +
					"and status = "+HospitalPlatformBillStatusEnum.PLATFORM_CAIWU_TOBE_CONFIRM.getCode());
			if(querylist == null || querylist.size() == 0){
				log.error("医院id="+defSettHospitalId+"没有可用于确认的平台账单，财务无法确认账单");
				return;
			}
			platbillSn = querylist.get(0).get("sn").toString();

		}
//		platbillSn = "20180515155847229474269";
		jo.put("sn" ,platbillSn);
		HttpResult response = httpclient.post(Flag.OPS,OPS_AuditHospitalPlatformBill, JSON.toJSONString(jo));
		log.info("body"+response.getBody());
		Assert.assertEquals(response.getCode(), HttpStatus.SC_OK);
		String body = response.getBody();
		boolean result = JsonPath.read(body,"$.result");
		Assert.assertTrue(result);

		if(checkdb){
			//check平台收款账单状态
		   	List<TradeHospitalPlatformBill> dbPlatBill = SettleChecker.getTradeHospitalPlatformBillByColumn("sn","'"+platbillSn+"'");
		   	Assert.assertEquals(dbPlatBill.size(),1);
		   	TradeHospitalPlatformBill dbPlat = dbPlatBill.get(0);
		   	Assert.assertEquals(dbPlat.getStatus(),HospitalPlatformBillStatusEnum.PLATFORM_CONFIREMD.getCode());

		   	//消费额度不为0时,需查看消费额度状态变化,流转日志
		   	if(dbPlatBill.get(0).getConsumeQuotaAmount().intValue() != 0){
				//check消费额度变成已确认
				List<TradeConsumeQuotaDetail> consumeDetails = SettleChecker.getTradeConsumeQuotaDetailByPlatformBillSn(platbillSn);
				Assert.assertEquals(consumeDetails.size(),1);
				TradeConsumeQuotaDetail detail = consumeDetails.get(0);
				Assert.assertEquals(detail.getStatus(),ConsumeQuotaDetailStatusEnum.HOSPITAL_HAS_CONFIRMED.getCode());//已确认

				List<TradeCommonLogResultDTO> dbCommonLogList = SettleChecker.getTradeCommonLogList(detail.getSn(),LogTypeEnum.LOG_TYPE_SETTLEMENT_CONSUME_QUOTA_AUDIT.getValue(),"desc");//获取消费额度审核
				Assert.assertEquals(dbCommonLogList.get(0).getOperatorId().intValue(),defAccount.getId().intValue());
				Assert.assertEquals(dbCommonLogList.get(0).getOperatorName(),defAccount.getName());
				Assert.assertEquals(dbCommonLogList.get(0).getOperation(),"财务审核");
				Assert.assertEquals(dbCommonLogList.get(0).getRefSn(),detail.getSn());
				Assert.assertEquals(dbCommonLogList.get(0).getLogType().intValue(),2);
				Assert.assertEquals(sdf.format(dbCommonLogList.get(0).getGmtCreated().getTime()),sdf.format(new Date()));
			}

		    //check流转日志
			List<TradeCommonLogResultDTO> dbCommonLogList = SettleChecker.getTradeCommonLogList(platbillSn, LogTypeEnum.LOG_TYPE_SETTLEMENT_PLATFORM_AUDIT.getValue(),"desc");//获取结算平台账单审核
			Assert.assertEquals(dbCommonLogList.get(0).getOperatorId().intValue(),defAccount.getId().intValue());
			Assert.assertEquals(dbCommonLogList.get(0).getOperatorName(),defAccount.getName());
			Assert.assertEquals(dbCommonLogList.get(0).getOperation(),"财务审核");
			Assert.assertEquals(dbCommonLogList.get(0).getRefSn(),platbillSn);
			Assert.assertEquals(dbCommonLogList.get(0).getLogType().intValue(),1);
			Assert.assertEquals(sdf.format(dbCommonLogList.get(0).getGmtCreated().getTime()),sdf.format(new Date()));
			


		}
	}
}
