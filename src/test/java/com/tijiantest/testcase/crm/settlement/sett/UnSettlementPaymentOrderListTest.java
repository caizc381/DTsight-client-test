package com.tijiantest.testcase.crm.settlement.sett;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.jayway.jsonpath.JsonPath;
import com.tijiantest.base.HttpResult;
import com.tijiantest.base.dbcheck.CompanyChecker;
import com.tijiantest.base.dbcheck.SettleChecker;
import com.tijiantest.model.settlement.*;
import com.tijiantest.testcase.crm.settlement.SettleBase;
import com.tijiantest.util.CvsFileUtil;
import com.tijiantest.util.IdCardValidate;
import com.tijiantest.util.pagination.Page;
import org.apache.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * 结算管理->执行结算->列出未结算的收款订单列表
 * 现场散客单位（收款订单结算）
 * @author huifang
 *
 */
public class UnSettlementPaymentOrderListTest extends SettleBase{

	@Test(description = "列出未结算的收款订单列表",groups = {"qa"},dataProvider="unsettlementPaymentOrder")
	public void test_01_unSettlementPaymentOrderList(String...args){

		SettlementPaymentOrderVO dto = new SettlementPaymentOrderVO();
		//现场散客
		int companyid = CompanyChecker.getHospitalCompanyByPlatCompanyIdANDOrganizationId(2,defSettHospitalId).getId();
		dto.setHospitalCompanyIdList(Arrays.asList(companyid));
		dto.setHospitalId(defSettHospitalId);
		String hospitalStr = args[1];
		String idCardStr = args[2];
		String insertStartTimeStr = args[3];
		String insertEndTimeStr = args[4];
		String pageStr = args[5];
		String batchSnStr = args[6];
		int hospitalId = -1;
		int  status = -1;
		String examStartTime = null;
		String examEndTime = null;
		String insertStartTime = null;
		String insertEndTime = null;
		String idCardOrAccountName = null;
		int page = -1;
		String batchSn = null;
		
		if(!IsArgsNull(hospitalStr)){
			hospitalId = defSettHospitalId;
			dto.setHospitalId(hospitalId);
		}
		if(!IsArgsNull(insertStartTimeStr)){
			insertStartTime = insertStartTimeStr;
			dto.setSettlementStartDate(insertStartTime);
			}
		
		if(!IsArgsNull(insertEndTimeStr)){
			insertEndTime = insertEndTimeStr;
			dto.setSettlementEndDate(insertEndTime);
			}
		if(!IsArgsNull(idCardStr)){
			idCardOrAccountName = idCardStr;
			dto.setPaymentName(idCardOrAccountName);
			}

		if(!IsArgsNull(pageStr)){
			page = Integer.parseInt(pageStr);
			dto.setPage(new Page(0, page));
			}

		if(!IsArgsNull(batchSnStr)){
			//随意取1个待确认的批次号
			List<TradeSettlementBatch> batchList = SettleChecker.getTradeSettlementBatch(defSettHospitalId,companyid,null,null,SettlementHospitalConfirmEnum.SETTLEMENT_TO_BE_CONFIRM.getCode(),null,false,"1");
			if(batchList!=null && batchList.size()>0){
				batchSn = batchList.get(0).getSn();
				dto.setBatchSn(batchSn);
			}else
				log.error("没有待确认的批次号，无法修改批次并进入未结算收款订单页面");

		}
		HttpResult response = httpclient.post(UnSettlementPaymentOrderList, JSON.toJSONString(dto));
		String body = response.getBody();
		System.out.println(body);
		Assert.assertEquals(response.getCode(), HttpStatus.SC_OK);
		Object records = JsonPath.read(body, "$.records");
		Object totleIds = JsonPath.read(body,"$.totleIds");
		List<UnsettlementPaymentOrder> retList  = new ArrayList<UnsettlementPaymentOrder>();
		List<String> orderIds = new ArrayList<String>();
		if(records != null ){
			String recordStr = records.toString();
			String totalIdStr = totleIds.toString();
			retList = JSONArray.parseArray(recordStr, UnsettlementPaymentOrder.class);
			orderIds = JSONArray.parseArray(totalIdStr, String.class);	
		}
		if(checkdb){
			String statusList = "("+SettlementHospitalConfirmEnum.UNSETTLEMENT.getCode()+","+SettlementHospitalConfirmEnum.SETTLEMENT_CANCELED.getCode()+")";
			String sql = "select DISTINCT o.* from tb_payment_order o ,tb_payment_order_settlement e "
					+ "where o.order_num = e.order_num "
					+ " and o.gmt_created >  '"+settle_time+"'  ";
			sql += " and (e.hospital_settlement_status in  "+statusList;
			if(batchSn!=null)
				sql += " or ( e.hospital_settlement_status = "+SettlementHospitalConfirmEnum.SETTLEMENT_TO_BE_CONFIRM.getCode()+" and e.order_num in " +
						"(select ref_order_num from  tb_trade_settlement_payment_order where batch_sn = "+batchSn+"))";
			sql += ")";
			if(status !=-1)
				sql += " and o.status = "+status;
			else
				sql += " and o.status in (2,3) ";
			if(insertStartTime !=null)
				sql += " and o.gmt_created >= '"+insertStartTime+"' ";
			if(insertEndTime !=null)
				sql += " and o.gmt_created <= '"+insertEndTime+"'";
			if(idCardOrAccountName != null){
				sql += " and o.payment_name = '"+idCardOrAccountName+"'";
			}
			sql +=  "  and o.organization_id = "+hospitalId
					+ " and o.hospital_company_id = "+companyid + " order by o.id desc  ";
			if(page!=-1)
				sql +="limit "+page;
			log.info(sql);
			List<UnsettlementPaymentOrder> dbOrderList = SettleChecker.getNotSettlementPaymentOrder(sql);
			Assert.assertEquals(retList.size(),dbOrderList.size());
//			Assert.assertEquals(orderIds.size(),SettleChecker.getNotSettlementOrder(defSettHospitalId, settle_time, companyid).size() );
			for(int i=0;i<retList.size();i++){
				log.info("第"+(i+1)+"条");
				Assert.assertEquals(retList.get(i).getOrderNum(),dbOrderList.get(i).getOrderNum());
				Assert.assertEquals(retList.get(i).getPaymentName(),dbOrderList.get(i).getPaymentName());
				Assert.assertEquals(retList.get(i).getAmount(),dbOrderList.get(i).getAmount());
				Assert.assertEquals(retList.get(i).getManagerName(),dbOrderList.get(i).getManagerName());
				Assert.assertEquals(retList.get(i).getManagerId(),dbOrderList.get(i).getManagerId());
				Assert.assertEquals(retList.get(i).getInsertTime(),dbOrderList.get(i).getInsertTime());
				Assert.assertEquals(retList.get(i).getStatus(),dbOrderList.get(i).getStatus());
				Assert.assertTrue(orderIds.contains(retList.get(i).getOrderNum()));

			}
		}
		
	}
	
	  @DataProvider
	  public Iterator<String[]> unsettlementPaymentOrder(){
			try {
				return CvsFileUtil.fromCvsFileToIterator("./csv/settlement/unsettlementPaymentOrder.csv",18);
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
