package com.tijiantest.testcase.crm.settlement.sett;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.sun.org.apache.xpath.internal.operations.Bool;
import com.tijiantest.model.payment.trade.PayConstants;
import com.tijiantest.model.settlement.*;
import org.apache.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.jayway.jsonpath.JsonPath;
import com.tijiantest.base.HttpResult;
import com.tijiantest.base.dbcheck.SettleChecker;
import com.tijiantest.testcase.crm.settlement.SettleBase;
import com.tijiantest.util.CvsFileUtil;
import com.tijiantest.util.IdCardValidate;
import com.tijiantest.util.pagination.Page;

/**
 * 结算管理->执行结算->列出未结算的体检订单列表
 * @author huifang
 *
 */
public class UnSettlementOrderListTest extends SettleBase{

	private static  int companyid = 0;
	
	@Test(description="选择单位",groups={"qa"},dependsOnGroups = "crm_companySett")
//	@Test(description="选择单位",groups={"qa"})
	public void test_prepare_company(){

		List<CompanySettlementCount> dblist = CompanySettlePageTest.dblist;
		if(dblist!=null && dblist.size() > 0){
			int index=(int)(Math.random()*dblist.size());
			//随机取1个单位
			CompanySettlementCount settleCount = dblist.get(index);
			companyid = settleCount.getId();
			System.out.println("单位 id..."+companyid);
		}
//		companyid = 4400012;
	}
	
	@Test(description = "列出未结算的体检订单列表",groups = {"qa"},dataProvider="unsettlementOrder",dependsOnMethods="test_prepare_company")
	public void test_01_listUnSettlementOrders(String...args){

		UnsettlementOrderQueryDTO dto = new UnsettlementOrderQueryDTO();
		if(companyid ==0){
			log.error("没有可结算的单位,无法列出未结算的订单列表");
			return;
		}
		dto.setExamCompanyIds(Arrays.asList(companyid));
		dto.setHospitalId(defSettHospitalId);
		String examStartTimeStr = args[1];
		String examEndTimeStr = args[2];
		String hospitalStr = args[3];
		String idCardStr = args[4];
		String insertStartTimeStr = args[5];
		String insertEndTimeStr = args[6];
		String statusStr = args[7];
		String containOnlineStr = args[8];
		String pageStr = args[9];
		String batchSnStr = args[10];
		int hospitalId = -1;
		int  status = -1;
		String examStartTime = null;
		String examEndTime = null;
		String insertStartTime = null;
		String insertEndTime = null;
		String idCardOrAccountName = null;
		int page = -1;
		String batchSn = null;
		boolean containOnline = false;
		
		if(!IsArgsNull(hospitalStr)){
			hospitalId = defSettHospitalId;
			dto.setHospitalId(hospitalId);
		}	
		if(!IsArgsNull(examStartTimeStr)){
			examStartTime = examStartTimeStr;
			dto.setExamStartDate(examStartTime);
			}
		if(!IsArgsNull(examEndTimeStr)){
			examEndTime = examEndTimeStr;
			dto.setExamEndDate(examEndTime);
			}
		
		if(!IsArgsNull(insertStartTimeStr)){
			insertStartTime = insertStartTimeStr;
			dto.setPlaceOrderStartTime(insertStartTime);
			}
		
		if(!IsArgsNull(insertEndTimeStr)){
			insertEndTime = insertEndTimeStr;
			dto.setPlaceOrderEndTime(insertEndTime);
			}
		if(!IsArgsNull(idCardStr)){
			idCardOrAccountName = idCardStr;
			dto.setIdCardOrAccountName(idCardOrAccountName);
			}
		
		if(!IsArgsNull(statusStr)){
			status = Integer.parseInt(statusStr);
			dto.setStatus(Arrays.asList(status));
			}
		if(!IsArgsNull(containOnlineStr)){
			containOnline = Boolean.parseBoolean(containOnlineStr);
			dto.setContainOnline(containOnline);
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
				log.error("没有待确认的批次号，无法修改批次并进入未结算订单页面");

		}
		HttpResult response = httpclient.post(UnSettlementOrderList, JSON.toJSONString(dto));
		String body = response.getBody();
		System.out.println(body);
		Assert.assertEquals(response.getCode(), HttpStatus.SC_OK);
		Object records = JsonPath.read(body, "$.records");
		Object totleIds = JsonPath.read(body,"$.totleIds");
		List<UnsettlementOrder> retList  = new ArrayList<UnsettlementOrder>();
		List<String> orderIds = new ArrayList<String>();
		if(records != null ){
			String recordStr = records.toString();
			String totalIdStr = totleIds.toString();
			retList = JSONArray.parseArray(recordStr, UnsettlementOrder.class);
			orderIds = JSONArray.parseArray(totalIdStr, String.class);	
		}
		if(checkdb){
			String mongoStr = "";
			String statusList = "("+SettlementHospitalConfirmEnum.UNSETTLEMENT.getCode()+","+SettlementHospitalConfirmEnum.SETTLEMENT_CANCELED.getCode()+")";
			String sql = "select o.* from tb_order o ,tb_exam_order_settlement e "
					+ "where o.order_num = e.order_num "
					+ " and o.insert_time >  '"+settle_time+"'  ";
			sql += " and ( e.hospital_settlement_status in  "+statusList ;
			if(batchSn!=null)
				sql+=" or (e.hospital_settlement_status = "+SettlementHospitalConfirmEnum.SETTLEMENT_TO_BE_CONFIRM.getCode()+" and e.settlement_batch_sn =  "+batchSn+")";
			sql += " ) ";
			if(status !=-1)
				sql += " and o.status = "+status;
			else
				sql += " and o.status in (2,3,9) ";
			if(examStartTime !=null)
				sql += " and o.exam_date >= '"+examStartTime+"' ";
			if(examEndTime !=null)
				sql += " and o.exam_date <= '"+examEndTime+"' ";
			if(insertStartTime !=null)
				sql += " and o.insert_time >= '"+insertStartTime+"' ";
			if(insertEndTime !=null)
				sql += " and o.insert_time <= '"+insertEndTime+"'";
			if(idCardOrAccountName != null){
				if(IdCardValidate.isIdcard(idCardOrAccountName)){
					mongoStr += "'examiner.idCard':'"+idCardOrAccountName+"'";
				}else{
					mongoStr += "'examiner.name':'"+idCardOrAccountName+"'";
				}
			}
			if(containOnline){//包括线上支付
				String tradeMethodTypeList = "("+ PayConstants.PayMethod.Alipay + ","+PayConstants.PayMethod.Wxpay+","+PayConstants.PayMethod.AlipayScan+","+PayConstants.PayMethod.WxpayScan+","+PayConstants.PayMethod.Balance+")";
				sql += " and o.order_num  in (select ref_order_num from tb_trade_pay_record where trade_method_type in "+tradeMethodTypeList+" and pay_status = "+PayConstants.TradeStatus.Successful+")";
			}
			sql +=  " and o.is_export=1  and o.hospital_id = "+hospitalId
					+ " and o.hospital_company_id = "+companyid + " order by o.id desc  ";
			if(page!=-1)
				sql +="limit "+page;
			log.info(sql);
			List<UnsettlementOrder> dbOrderList = SettleChecker.getNotSettlementOrder(sql,mongoStr);
			Assert.assertEquals(retList.size(),dbOrderList.size());
//			Assert.assertEquals(orderIds.size(),SettleChecker.getNotSettlementOrder(defSettHospitalId, settle_time, companyid).size() );
			for(int i=0;i<retList.size();i++){
				log.info("ret orderNum.."+retList.get(i).getOrderNum()+"...db orderNum..."+dbOrderList.get(i).getOrderNum());
				Assert.assertEquals(retList.get(i).getOrderNum(),dbOrderList.get(i).getOrderNum());
				Assert.assertEquals(retList.get(i).getAccountIdcard(),dbOrderList.get(i).getAccountIdcard());
				Assert.assertEquals(retList.get(i).getAccountName(),dbOrderList.get(i).getAccountName());
				Assert.assertEquals(retList.get(i).getCompanyName(),dbOrderList.get(i).getCompanyName());
				Assert.assertEquals(retList.get(i).getManagerName(),dbOrderList.get(i).getManagerName());
				Assert.assertEquals(retList.get(i).getExamDate(),dbOrderList.get(i).getExamDate());
				Assert.assertEquals(retList.get(i).getOrderPrice(),dbOrderList.get(i).getOrderPrice());
				Assert.assertEquals(retList.get(i).getOwnerId(),dbOrderList.get(i).getOwnerId());
				Assert.assertEquals(retList.get(i).getStatus(),dbOrderList.get(i).getStatus());
				Assert.assertTrue(orderIds.contains(retList.get(i).getOrderNum()));

			}
		}
		
	}
	
	  @DataProvider
	  public Iterator<String[]> unsettlementOrder(){
			try {
				return CvsFileUtil.fromCvsFileToIterator("./csv/settlement/unsettlementOrder.csv",18);
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
