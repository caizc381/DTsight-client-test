package com.tijiantest.testcase.ops.settlement;

import com.alibaba.fastjson.JSON;
import com.tijiantest.base.Flag;
import com.tijiantest.base.HttpResult;
import com.tijiantest.base.dbcheck.AccountChecker;
import com.tijiantest.base.dbcheck.CompanyChecker;
import com.tijiantest.base.dbcheck.HospitalChecker;
import com.tijiantest.base.dbcheck.SettleChecker;
import com.tijiantest.model.organization.OrganizationTypeEnum;
import com.tijiantest.model.settlement.SettlementHospitalConfirmEnum;
import com.tijiantest.model.settlement.TradePrepaymentRecord;
import com.tijiantest.testcase.ops.OpsBase;
import com.tijiantest.util.CvsFileUtil;
import com.tijiantest.util.db.DBMapper;
import org.apache.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * OPS->特殊退款
 * 
 * 更新特殊退款
 * @author huifang
 *
 */
public class UpdatePrepaymentRecordTest extends OpsBase{

	@Test(description = "更新特殊退款" , groups = {"qa"},dataProvider = "ops_updatePrepayment")
	public void test_01_updatePrepaymentRecord(String ...args) throws ParseException{
		String refundOrganizationTypeStr = args[1];
		String newRemark= args[2];
		int refundOrganizationType = Integer.parseInt(refundOrganizationTypeStr);
		String sql = "select * from tb_trade_prepayments_record where status = 1 and is_deleted = 0 ";
		if(refundOrganizationType == OrganizationTypeEnum.CHANNEL.getCode())
			sql += " and settlement_view_type = 1 and sn is not null ";
		else
			sql += " and settlement_view_type = 0 and refund_organization_id in (select id from tb_hospital where organization_type = 1)";
		sql += " order by id desc limit 1";

		List<TradePrepaymentRecord>  prepaymentRecords = SettleChecker.getTradePrepaymentRecordBySql(sql);
		TradePrepaymentRecord dto = prepaymentRecords.get(0);
		int preId = dto.getId();
		String sn = dto.getSn();
		int beforeAmount = dto.getAmount();
		int afterAmount = beforeAmount+100;
		dto.setAmount(afterAmount);
		dto.setPaymentTime(new Date());
		dto.setRemark(newRemark);
		int refundOrganiztionId = -1;
		int hospitalId = -1;
		int refundCompanyId = -1;
		int hospitalCompanyId = -1;
		if(refundOrganizationType == OrganizationTypeEnum.CHANNEL.getCode()){
			refundOrganiztionId = defChannelid;
			refundCompanyId = CompanyChecker.getChannelCompanyByChannelId(defChannelid).get(0).getId();
			hospitalId = HospitalChecker.getChannelSupportHospitals(refundOrganiztionId).get(0).getId();
			hospitalCompanyId = CompanyChecker.getHospitalCompanyByOrganizationId(hospitalId, "id", true).get(0).getId();
		}else{
			refundOrganiztionId = defSettHospitalId;
			refundCompanyId = CompanyChecker.getHospitalCompanyByOrganizationId(refundOrganiztionId, "id", true).get(0).getId();
			hospitalId = defSettHospitalId;
			hospitalCompanyId = refundCompanyId;
		}
		dto.setRefundOrganizationId(refundOrganiztionId);
		dto.setRefundCompanyId(refundCompanyId);
		dto.setOrganizationId(hospitalId);
		dto.setCompanyId(hospitalCompanyId);

		HttpResult response = httpclient.post(Flag.OPS,OPS_UpdatePrepaymentRecord, JSON.toJSONString(dto));
		Assert.assertEquals(response.getCode(), HttpStatus.SC_OK);
		Assert.assertEquals(response.getBody(),"");
		
		if(checkdb){
			List<TradePrepaymentRecord>  prepaymentRecordsList = null;
			if(sn != null)
				prepaymentRecordsList = SettleChecker.getTradePrepaymentRecordBySql("select * from tb_trade_prepayments_record where sn = '"+sn+"'");
			else
				prepaymentRecordsList = SettleChecker.getTradePrepaymentRecordBySql("select * from tb_trade_prepayments_record where id = "+preId);

			for(TradePrepaymentRecord p : prepaymentRecordsList){
				Assert.assertEquals(p.getAmount().intValue(),afterAmount);
				Assert.assertEquals(sdf.format(p.getPaymentTime()),sdf.format(new Date()));
				Assert.assertNotNull(p.getRemark(),newRemark);
				Assert.assertEquals(p.getOrganizationId().intValue(),hospitalId);
				Assert.assertEquals(p.getRefundOrganizationId().intValue(),refundOrganiztionId);
				Assert.assertEquals(p.getRefundCompanyId().intValue(),refundCompanyId);
				Assert.assertEquals(p.getCompanyId().intValue(),hospitalCompanyId);


			}
		}
				
	}

	@DataProvider
	public Iterator<String[]> ops_updatePrepayment(){
		try {
			return CvsFileUtil.fromCvsFileToIterator("./csv/settlement/ops_updatePrepayment.csv",18);
		} catch (FileNotFoundException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		return null;
	}

}