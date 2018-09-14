package com.tijiantest.testcase.ops.settlement;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.tijiantest.model.common.LogTypeEnum;
import org.apache.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.alibaba.fastjson.JSON;
import com.jayway.jsonpath.JsonPath;
import com.tijiantest.base.Flag;
import com.tijiantest.base.HttpResult;
import com.tijiantest.base.dbcheck.AccountChecker;
import com.tijiantest.base.dbcheck.CompanyChecker;
import com.tijiantest.base.dbcheck.SettleChecker;
import com.tijiantest.model.account.Account;
import com.tijiantest.model.company.HospitalCompany;
import com.tijiantest.model.settlement.TradeCommonLogResultDTO;
import com.tijiantest.model.settlement.TradeConsumeQuotaDetail;
import com.tijiantest.testcase.ops.OpsBase;
import com.tijiantest.util.CvsFileUtil;

/**
 * OPS->消费额度
 * 
 * 添加消费额度
 * @author huifang
 *
 */
public class AddConsumeQuotaDetailTest extends OpsBase{

	@Test(description = "添加消费额度" , groups = {"qa"},dataProvider = "ops_addConsume")
	public void test_01_addConsumeQuota(String ...args) throws ParseException{
		waitto(1);
		String companyIdStr = args[2];
		String payTimeStr = args[3];
		String amountStr = args[4];
		String sceneStr = args[5];
		String remarkStr = args[6];
		String certificateStr = args[7];
		int hospitalId = defSettHospitalId;
		int companyId = -1;
		String pay_time = null;
		int scene = -1;
		long amount = 0l;
		String remark = null;
		String certificate = null;
		
		TradeConsumeQuotaDetail dto = new TradeConsumeQuotaDetail();
		dto.setOrganizationId(hospitalId);
		if(!IsArgsNull(companyIdStr)){
			companyId = CompanyChecker.getHospitalCompanyByOrganizationId(hospitalId, "id", true).get(0).getId();
			dto.setCompanyId(companyId);
		}
		if(!IsArgsNull(payTimeStr)){
			pay_time = payTimeStr;
			dto.setPayTime(simplehms.parse(pay_time));
			}
		if(!IsArgsNull(amountStr)){
			amount = Integer.parseInt(amountStr);
			dto.setAmount(amount);
			}
		if(!IsArgsNull(sceneStr)){
			scene = Integer.parseInt(sceneStr);
			dto.setScene(scene);
			}
		if(!IsArgsNull(remarkStr)){
			remark = remarkStr;
			dto.setRemark(remarkStr);
			}
		
		if(!IsArgsNull(certificateStr)){
			certificate = certificateStr;
			dto.setCertificate(certificate);
			}
	
	
		HttpResult response = httpclient.post(Flag.OPS,OPS_AddConsumeQuotaDetail, JSON.toJSONString(dto));
		Assert.assertEquals(response.getCode(), HttpStatus.SC_OK);
		Assert.assertEquals(response.getBody(),"true");
		
		if(checkdb){
			log.info("hospitalId"+hospitalId + "companyId"+companyId+"...pay_time"+pay_time);
			String sql = "select * from tb_trade_consume_quota_detail where is_deleted = 0 ";
			if(hospitalId != -1)
				sql += " and organization_id = "+hospitalId;
			if(companyId != -1)
				sql += " and company_id = "+companyId;
			if(pay_time != null)
				sql += " and pay_time = '"+pay_time+"'";
			if (scene != -1){
					sql += " and  scene =" + scene;
			}
			sql += "   order by gmt_created desc";
			log.info(sql);
			List<TradeConsumeQuotaDetail> dbList = SettleChecker.getTradeConsumeQuotaDetail(sql);
			TradeConsumeQuotaDetail dbDetail = dbList.get(0);
			if(certificate !=null )
				Assert.assertEquals(dbDetail.getCertificate(),certificate);
			if(remark !=null )
				Assert.assertEquals(dbDetail.getRemark(),remark);
			Assert.assertEquals(dbDetail.getOrganizationId().intValue(),hospitalId);
			if(companyId != -1){
				Assert.assertEquals(dbDetail.getCompanyId().intValue(),companyId);
				HospitalCompany hc = CompanyChecker.getHospitalCompanyById(companyId);
				Assert.assertEquals(dbDetail.getCompanyName(),hc.getName());
				}
			Assert.assertEquals(simplehms.format(dbDetail.getPayTime()),pay_time);
			Assert.assertEquals(dbDetail.getScene().intValue(),scene);//场景
			Assert.assertEquals(dbDetail.getStatus().intValue(),1);//医院审核中
			if(amount != 0)
				Assert.assertEquals(dbDetail.getAmount().longValue(),amount);
			Assert.assertEquals(dbDetail.getIsDeleted().intValue(),0);
			
			//check流转日志
			List<TradeCommonLogResultDTO> dbCommonLogList = SettleChecker.getTradeCommonLogList(dbDetail.getSn(), LogTypeEnum.LOG_TYPE_SETTLEMENT_CONSUME_QUOTA_AUDIT.getValue(),"desc");//获取消费额度的流转日志
			Account account = AccountChecker.getOpsAccount(defusername);
			Assert.assertEquals(dbCommonLogList.get(0).getOperatorId(),account.getId());
			Assert.assertEquals(dbCommonLogList.get(0).getOperatorName(),account.getName());
			Assert.assertEquals(dbCommonLogList.get(0).getOperation(),"添加明细");
			Assert.assertEquals(dbCommonLogList.get(0).getRefSn(),dbDetail.getSn());
			Assert.assertEquals(dbCommonLogList.get(0).getLogType().intValue(),2);
			Assert.assertEquals(sdf.format(dbCommonLogList.get(0).getGmtCreated().getTime()),sdf.format(new Date()));

		}	
	}
	

	@Test(description = "传入参数错误,边界值判定" , groups = {"qa"},dataProvider = "ops_addConsume_fail")
	public void test_01_addConsumeQuota_fail(String ...args) throws ParseException{
		String companyIdStr = args[2];
		String payTimeStr = args[3];
		String amountStr = args[4];
		String sceneStr = args[5];
		String remarkStr = args[6];
		String certificateStr = args[7];
		int hospitalId = defSettHospitalId;
		int companyId = -1;
		String pay_time = null;
		int scene = -1;
		long amount = 0l;
		@SuppressWarnings("unused")
		String remark = null;
		String certificate = null;
		
		TradeConsumeQuotaDetail dto = new TradeConsumeQuotaDetail();
		dto.setOrganizationId(hospitalId);
		if(!IsArgsNull(companyIdStr)){
			companyId = CompanyChecker.getHospitalCompanyByOrganizationId(hospitalId, "id", true).get(0).getId();
			dto.setCompanyId(companyId);
		}
		if(!IsArgsNull(payTimeStr)){
			pay_time = payTimeStr;
			dto.setPayTime(simplehms.parse(pay_time));
			}
		if(!IsArgsNull(amountStr)){
			amount = Integer.parseInt(amountStr);
			dto.setAmount(amount);
			}
		if(!IsArgsNull(sceneStr)){
			scene = Integer.parseInt(sceneStr);
			dto.setScene(scene);
			}
		if(!IsArgsNull(remarkStr)){
			remark = remarkStr;
			dto.setRemark(remarkStr);
			}
		
		if(!IsArgsNull(certificateStr)){
			certificate = certificateStr;
			dto.setCertificate(certificate);
			}
	
	
		HttpResult response = httpclient.post(Flag.OPS,OPS_AddConsumeQuotaDetail, JSON.toJSONString(dto));
		Assert.assertEquals(response.getCode(), HttpStatus.SC_BAD_REQUEST);
		String text = JsonPath.read(response.getBody(),"$.text");
		if(amount == 0){
			Assert.assertTrue(text.contains("不能等于零"));
			}
		if(amount < 0  && scene == 1)
			Assert.assertTrue(text.contains("金额不能小于等于零"));
	}
	  @DataProvider
	  public Iterator<String[]> ops_addConsume(){
			try {
				return CvsFileUtil.fromCvsFileToIterator("./csv/settlement/ops_addConsume.csv",18);
			} catch (FileNotFoundException e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			}
			return null;
		}
	  
	  @DataProvider
	  public Iterator<String[]> ops_addConsume_fail(){
			try {
				return CvsFileUtil.fromCvsFileToIterator("./csv/settlement/ops_addConsume_fail.csv",18);
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