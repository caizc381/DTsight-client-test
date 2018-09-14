package com.tijiantest.testcase.ops.settlement;

import java.util.ArrayList;
import java.util.List;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.testng.Assert;
import org.testng.annotations.Test;
import com.tijiantest.base.Flag;
import com.tijiantest.base.HttpResult;
import com.tijiantest.base.dbcheck.SettleChecker;
import com.tijiantest.model.settlement.HospitalPlatformBillStatusEnum;
import com.tijiantest.model.settlement.TradeHospitalPlatformBill;
import com.tijiantest.testcase.ops.OpsBase;

/**
 * OPS->结算系统
 * 付款账单|付款记录页面（下载对账单）
 * @author huifang
 * @param 
 *
 */
public class DownloadSettlementBillTest extends OpsBase{

	@Test(description = "下载对账单",groups = {"qa"})
	public void test_01_downloadSettlementBillTest(){
		String platSn = null;
		//从数据库中随机找一条收款或者平台审核中的记录
		String statusList = "("+HospitalPlatformBillStatusEnum.PLATFORM_COMPLETE_PAYMENT.getCode() + ","+HospitalPlatformBillStatusEnum.PLATFORM_TO_BE_CONFIRMD.getCode()
				+","+HospitalPlatformBillStatusEnum.PLATFORM_CONFIREMD.getCode()+","+HospitalPlatformBillStatusEnum.PLATFORM_CAIWU_TOBE_CONFIRM.getCode()+")";
		List<TradeHospitalPlatformBill> platBills = SettleChecker.getTradeHospitalPlatformBillByColumn(
				"select * from  tb_trade_hospital_platform_bill where is_deleted = 0 and settlement_view_type=0 and status in "+statusList+" order by gmt_created desc");
		if(platBills !=null && platBills.size() > 0)
			platSn = platBills.get(0).getSn();
		if(platSn == null){
			System.out.println("没有可用的平台账单...,请先手动执行结算并生成批次");
			return;
		}
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("hospitalPlatformBillSns", platSn));
		HttpResult response = httpclient.get(Flag.OPS,OPS_DownloadSettlementBill, params);
		log.info(response.getBody());
		Assert.assertEquals(response.getCode(), HttpStatus.SC_OK);
		String body = response.getBody();
		Assert.assertNotNull(body);
	}
}
