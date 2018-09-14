package com.tijiantest.testcase.ops.settlement;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.Iterator;
import java.util.List;

import com.sun.org.apache.xpath.internal.operations.Or;
import com.tijiantest.base.dbcheck.HospitalChecker;
import com.tijiantest.model.organization.OrganizationTypeEnum;
import com.tijiantest.util.db.DBMapper;
import com.tijiantest.util.db.SqlException;
import org.apache.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.alibaba.fastjson.JSON;
import com.tijiantest.base.Flag;
import com.tijiantest.base.HttpResult;
import com.tijiantest.base.dbcheck.AccountChecker;
import com.tijiantest.base.dbcheck.CompanyChecker;
import com.tijiantest.base.dbcheck.SettleChecker;
import com.tijiantest.model.settlement.SettlementHospitalConfirmEnum;
import com.tijiantest.model.settlement.TradePrepaymentRecord;
import com.tijiantest.testcase.ops.OpsBase;
import com.tijiantest.util.CvsFileUtil;

/**
 * 界面位置：OPS->特殊退款
 * 
 * 具体操作：新增特殊退款
 * @author huifang
 *
 */
public class AddPrepaymentRecordTest extends OpsBase{

	@Test(description = "新增特殊退款" , groups = {"qa","ops_addprepayment"},dataProvider = "ops_addPrepaymentRecord")
	public void test_01_addPrepaymentRecord(String ...args) throws ParseException, SqlException {
		//STEP1 解析数据文件
		String refundOrganizationStr = args[1];
		String refundCompanyStr = args[2];
		String hospitalStr = args[3];
		String companyIdStr = args[4];
		String payTimeStr = args[5];
		String amountStr = args[6];
		String remarkStr = args[7];
		String certificateStr = args[8];
		int refundOrganiztionId = -1;
		int hospitalId = -1;
		int refundCompanyId = -1;
		int hospitalCompanyId = -1;
		int organizationType = -1;
		String pay_time = null;
		int amount = 0;
		String remark = null;
		String certificate = null;

		//STEP2 数据转换为对象入参
		TradePrepaymentRecord dto = new TradePrepaymentRecord();
		if(!IsArgsNull(refundOrganizationStr)){
			if(!refundOrganizationStr.equals(hospitalStr)){//场景1：新建渠道特殊退款
				refundOrganiztionId = defChannelid;
				refundCompanyId = CompanyChecker.getChannelCompanyByChannelId(defChannelid).get(0).getId();
				hospitalId = HospitalChecker.getChannelSupportHospitals(refundOrganiztionId).get(0).getId();
				hospitalCompanyId = CompanyChecker.getHospitalCompanyByOrganizationId(hospitalId, "id", true).get(0).getId();
				organizationType = OrganizationTypeEnum.CHANNEL.getCode();
			}else{//场景2：新建医院特殊退款
				refundOrganiztionId = defSettHospitalId;
				refundCompanyId = CompanyChecker.getHospitalCompanyByOrganizationId(refundOrganiztionId, "id", true).get(0).getId();
				hospitalId = defSettHospitalId;
				hospitalCompanyId = refundCompanyId;
				organizationType = OrganizationTypeEnum.HOSPITAL.getCode();
			}
			dto.setRefundOrganizationId(refundOrganiztionId);
			dto.setRefundCompanyId(refundCompanyId);
			dto.setOrganizationId(hospitalId);
			dto.setCompanyId(hospitalCompanyId);
		}

		if(!IsArgsNull(payTimeStr)){
			pay_time = payTimeStr;
			dto.setPaymentTime(simplehms.parse(pay_time));
			}
		if(!IsArgsNull(amountStr)){
			amount = Integer.parseInt(amountStr);
			dto.setAmount(amount);
			}
		if(!IsArgsNull(remarkStr)){
			remark = remarkStr;
			dto.setRemark(remarkStr);
			}
		
		if(!IsArgsNull(certificateStr)){
			certificate = certificateStr;
			dto.setCertificate(certificate);
			}
	
		//STEP3 调用接口
		String beforeDate = simplehms.format(DBMapper.query("select now()").get(0).get("now()"));
		waitto(1);
		HttpResult response = httpclient.post(Flag.OPS,OPS_AddPrepaymentRecord, JSON.toJSONString(dto));
		Assert.assertEquals(response.getCode(), HttpStatus.SC_OK);
		Assert.assertEquals(response.getBody(),"");

		//STEP4 验证DB
		if(checkdb){
			log.info("refundOrganiztion="+refundOrganiztionId+"refundCompanyId="+refundCompanyId+"hospitalId="+hospitalId + "hospitalCompany="+hospitalCompanyId+"...pay_time="+pay_time);
			String sql = "";
			if(organizationType == OrganizationTypeEnum.HOSPITAL.getCode()){//新建医院特殊退款
				sql = "select * from tb_trade_prepayments_record where is_deleted = 0 ";
				if(hospitalId != -1)
					sql += " and organization_id = "+hospitalId;
				if(hospitalCompanyId != -1)
					sql += " and company_id = "+hospitalCompanyId;
				if(pay_time != null)
					sql += " and payment_time = '"+pay_time+"'";
				sql += "   and gmt_created > '"+beforeDate+"' order by gmt_created desc";

				log.info(sql);
				List<TradePrepaymentRecord> dbList = SettleChecker.getTradePrepaymentRecordBySql(sql);
				Assert.assertEquals(dbList.size(),1);//1条记录
				TradePrepaymentRecord dbDetail = dbList.get(0);
				if(certificate !=null )
					Assert.assertEquals(dbDetail.getCertificate(),certificate);
				if(remark !=null )
					Assert.assertEquals(dbDetail.getRemark(),remark);
				Assert.assertEquals(dbDetail.getSettlementViewType().intValue(),0);//医院视角
				Assert.assertEquals(dbDetail.getOrganizationId().intValue(),hospitalId);
				Assert.assertEquals(dbDetail.getRefundOrganizationId().intValue(),hospitalId);
				if(hospitalCompanyId != -1){
					Assert.assertEquals(dbDetail.getCompanyId().intValue(),hospitalCompanyId);
					Assert.assertEquals(dbDetail.getRefundCompanyId().intValue(),hospitalCompanyId);
				}
				Assert.assertEquals(simplehms.format(dbDetail.getPaymentTime()),pay_time);
				Assert.assertEquals(dbDetail.getType().intValue(),2);//特殊退款
				Assert.assertEquals(dbDetail.getStatus(),SettlementHospitalConfirmEnum.UNSETTLEMENT.getCode());//未结算
				if(amount != 0)
					Assert.assertEquals(dbDetail.getAmount().intValue(),amount);
				Assert.assertEquals(dbDetail.getOperatorId(),AccountChecker.getOpsAccount(defusername).getId());
				Assert.assertNull(dbDetail.getBatchSn());
				Assert.assertNotNull(dbDetail.getSn());
				Assert.assertEquals(dbDetail.getIsDeleted().intValue(),0);
			}
			if(organizationType == OrganizationTypeEnum.CHANNEL.getCode()){//新建渠道特殊退款
				sql = "select * from tb_trade_prepayments_record where is_deleted = 0 ";
				if(refundOrganiztionId != -1)
					sql += " and refund_organization_id = "+refundOrganiztionId;
				if(hospitalId != -1)
					sql += " and organization_id = "+hospitalId;
				if(refundCompanyId != -1)
					sql += " and refund_company_id = "+refundCompanyId;
				if(hospitalCompanyId != -1)
					sql += " and company_id = "+hospitalCompanyId;
				if(pay_time != null)
					sql += " and payment_time = '"+pay_time+"'";
				sql += "   and gmt_created > '"+beforeDate+"' order by gmt_created desc";

				log.info(sql);
				List<TradePrepaymentRecord> dbList = SettleChecker.getTradePrepaymentRecordBySql(sql);
				Assert.assertEquals(dbList.size(),2);//2条记录
				//1条渠道
				TradePrepaymentRecord dbDetail = dbList.get(0);
				if(certificate !=null )
					Assert.assertEquals(dbDetail.getCertificate(),certificate);
				if(remark !=null )
					Assert.assertEquals(dbDetail.getRemark(),remark);
				Assert.assertEquals(dbDetail.getSettlementViewType().intValue(),1);//渠道视角
				Assert.assertEquals(dbDetail.getOrganizationId().intValue(),hospitalId);
				Assert.assertEquals(dbDetail.getRefundOrganizationId().intValue(),refundOrganiztionId);
				if(hospitalCompanyId != -1){
					Assert.assertEquals(dbDetail.getCompanyId().intValue(),hospitalCompanyId);
				}
				if(refundCompanyId != -1){
					Assert.assertEquals(dbDetail.getRefundCompanyId().intValue(),refundCompanyId);
				}
				Assert.assertEquals(simplehms.format(dbDetail.getPaymentTime()),pay_time);
				Assert.assertEquals(dbDetail.getType().intValue(),2);//特殊退款
				Assert.assertEquals(dbDetail.getStatus(),SettlementHospitalConfirmEnum.UNSETTLEMENT.getCode());//未结算
				if(amount != 0)
					Assert.assertEquals(dbDetail.getAmount().intValue(),amount);
				Assert.assertEquals(dbDetail.getOperatorId(),AccountChecker.getOpsAccount(defusername).getId());
				Assert.assertNull(dbDetail.getBatchSn());
				String sn = dbDetail.getSn();
				Assert.assertNotNull(sn);
				Assert.assertEquals(dbDetail.getIsDeleted().intValue(),0);
				//1条医院
				dbDetail = dbList.get(1);
				if(certificate !=null )
					Assert.assertEquals(dbDetail.getCertificate(),certificate);
				if(remark !=null )
					Assert.assertEquals(dbDetail.getRemark(),remark);
				Assert.assertEquals(dbDetail.getSettlementViewType().intValue(),0);//医院视角
				Assert.assertEquals(dbDetail.getOrganizationId().intValue(),hospitalId);
				Assert.assertEquals(dbDetail.getRefundOrganizationId().intValue(),refundOrganiztionId);
				if(hospitalCompanyId != -1){
					Assert.assertEquals(dbDetail.getCompanyId().intValue(),hospitalCompanyId);
				}
				if(refundCompanyId != -1){
					Assert.assertEquals(dbDetail.getRefundCompanyId().intValue(),refundCompanyId);
				}
				Assert.assertEquals(simplehms.format(dbDetail.getPaymentTime()),pay_time);
				Assert.assertEquals(dbDetail.getType().intValue(),2);//特殊退款
				Assert.assertEquals(dbDetail.getStatus(),SettlementHospitalConfirmEnum.UNSETTLEMENT.getCode());//未结算
				if(amount != 0)
					Assert.assertEquals(dbDetail.getAmount().intValue(),amount);
				Assert.assertEquals(dbDetail.getOperatorId(),AccountChecker.getOpsAccount(defusername).getId());
				Assert.assertNull(dbDetail.getBatchSn());
				Assert.assertEquals(dbDetail.getSn(),sn);//批次号一致
				Assert.assertEquals(dbDetail.getIsDeleted().intValue(),0);

			}

		}
				
	}

	  //测试数据
	  @DataProvider
	  public Iterator<String[]> ops_addPrepaymentRecord(){
			try {
				return CvsFileUtil.fromCvsFileToIterator("./csv/settlement/ops_addPrepaymentRecord.csv",18);
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