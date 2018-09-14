package com.tijiantest.testcase.crm.settlement.bill;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.apache.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.tijiantest.base.HttpResult;
import com.tijiantest.base.dbcheck.CompanyChecker;
import com.tijiantest.base.dbcheck.SettleChecker;
import com.tijiantest.model.settlement.SettlementHospitalConfirmEnum;
import com.tijiantest.model.settlement.TradeHospitalCompanyBill;
import com.tijiantest.testcase.crm.settlement.SettleBase;
import com.tijiantest.util.CvsFileUtil;

/**
 * 获取医院单位账单列表/医院优惠券账单/医院线上账单/医院线下账单
 * 只展示已确认的医院单位账单
 * @author huifang
 *
 */
public class GetHospitalCompanySummaryBillListTest extends SettleBase{

	@Test(description = "获取医院单位账单列表",groups = {"qa"},dataProvider = "companyBill")
	public void test_01_getHospitalCompanySummaryBillList(String ...args){
		String companyStr = args[2];
		int companyId = -1;
		int hospitalId = defSettHospitalId;
		if(!IsArgsNull(companyStr)){
			companyId = CompanyChecker.getHospitalCompanyByOrganizationId(hospitalId, "id", true).get(0).getId();
			}
		JSONObject jo = new JSONObject();
		if(companyId == -1)
			jo.put("companyId","");
		else 
			jo.put("companyId", companyId+"");
		jo.put("organizationId", hospitalId+"");
		HttpResult response = httpclient.post(GetHospitalCompanyBillList, jo.toJSONString());
		Assert.assertEquals(response.getCode(), HttpStatus.SC_OK);
		String body = response.getBody();
		log.info("body..."+body);
		JSONObject jsonObject = null;
		List<JSONObject> jcompanyBillRet = null;
		List<JSONObject> jcompanyHospitalCoupRet = null;
		List<JSONObject> jcompanyHospitalOnlineRet = null;
		List<JSONObject> jcompanyHospitalOfflineRet = null;
		if(!body.equals("{}")&& !body.equals("")){
			jsonObject = JSONObject.parseObject(body);
			if(jsonObject.get(0)!=null)
				jcompanyBillRet = JSONArray.parseArray(jsonObject.get("0").toString(), JSONObject.class); //type类型换算成枚举
			if(jsonObject.get(2)!=null)
				jcompanyHospitalCoupRet = JSONArray.parseArray(jsonObject.get("2").toString(), JSONObject.class);
			if(jsonObject.get(3)!=null)
				jcompanyHospitalOnlineRet = JSONArray.parseArray(jsonObject.get("3").toString(), JSONObject.class);
			if(jsonObject.get(4)!=null)
				jcompanyHospitalOfflineRet = JSONArray.parseArray(jsonObject.get("4").toString(), JSONObject.class);
		}
		if(checkdb){

			//1.医院单位账单
			List<TradeHospitalCompanyBill> dbCompanyBills = SettleChecker.getTradeHospitalCompanyBillByColumn(null,false,"company_id",true,"hospital_id",hospitalId+"","company_id",companyId+"","status",SettlementHospitalConfirmEnum.SETTLEMENT_CONFIRMD.getCode()+"","type","0");;
			//逐个单位比较
			for(int k=0 ; k<dbCompanyBills.size();k++){
				int dbCompanyId = dbCompanyBills.get(k).getCompanyId();
				String dbCompanyName = CompanyChecker.getHospitalCompanyById(dbCompanyId).getName();
				JSONObject jdo = (JSONObject)jcompanyBillRet.get(k);
				Assert.assertEquals(jdo.get("companyName").toString(),dbCompanyName);//单位名称
				System.out.println("正在比较单位.."+dbCompanyName);
				long retTotalPayment = Long.parseLong(jdo.get("totalPayment").toString());//返回的单位应收合计
				long dbNeedPayment = 0l; //数据库中统计的单位应收金额合计（初始化）
				List<TradeHospitalCompanyBill> oneComRetBills = JSONArray.parseArray(jdo.get("tradeHospitalCompanyBillList").toString(), TradeHospitalCompanyBill.class);
				List<TradeHospitalCompanyBill> dbOneComBills = SettleChecker.getTradeHospitalCompanyBillByColumn("id",false,null, false,"company_id",dbCompanyId+"","status",SettlementHospitalConfirmEnum.SETTLEMENT_CONFIRMD.getCode()+"");
				Assert.assertEquals(oneComRetBills.size(), dbOneComBills.size());
				for(int i=0;i<oneComRetBills.size();i++){
					Assert.assertEquals(oneComRetBills.get(i).getId(),dbOneComBills.get(i).getId());
					Assert.assertEquals(oneComRetBills.get(i).getBatchSn(),dbOneComBills.get(i).getBatchSn());
					Assert.assertEquals(oneComRetBills.get(i).getCompanyId(),dbOneComBills.get(i).getCompanyId());
					Assert.assertEquals(oneComRetBills.get(i).getGmtCreated(),dbOneComBills.get(i).getGmtCreated());
					Assert.assertEquals(oneComRetBills.get(i).getGmtModified(),dbOneComBills.get(i).getGmtModified());
					Assert.assertEquals(oneComRetBills.get(i).getHospitalId(),dbOneComBills.get(i).getHospitalId());
					Assert.assertEquals(oneComRetBills.get(i).getIsDeleted(),dbOneComBills.get(i).getIsDeleted());
					Assert.assertEquals(oneComRetBills.get(i).getOperatorId(),dbOneComBills.get(i).getOperatorId());
					Assert.assertEquals(oneComRetBills.get(i).getCompanyPayAmount(),dbOneComBills.get(i).getCompanyPayAmount());
					Assert.assertEquals(oneComRetBills.get(i).getCompanyChargedAmount(),dbOneComBills.get(i).getCompanyChargedAmount());
					Assert.assertEquals(oneComRetBills.get(i).getCompanyRefundAmount(),dbOneComBills.get(i).getCompanyRefundAmount());
					Assert.assertEquals(oneComRetBills.get(i).getSn(),dbOneComBills.get(i).getSn());
					Assert.assertEquals(oneComRetBills.get(i).getStatus(),dbOneComBills.get(i).getStatus());
					dbNeedPayment += dbOneComBills.get(i).getCompanyChargedAmount().longValue();
				}
				//比较单位应收合计正确
				Assert.assertEquals(retTotalPayment,dbNeedPayment);
			}



			 //2.医院优惠券账单
			  dbCompanyBills = SettleChecker.getTradeHospitalCompanyBillByColumn(null,false,"company_id",true,"hospital_id",hospitalId+"","company_id",companyId+"","status",SettlementHospitalConfirmEnum.SETTLEMENT_CONFIRMD.getCode()+"","type","2");;
			//逐个单位比较
			for(int k=0 ; k<dbCompanyBills.size();k++){
				int dbCompanyId = dbCompanyBills.get(k).getCompanyId();
				String dbCompanyName = CompanyChecker.getHospitalCompanyById(dbCompanyId).getName();
				JSONObject jdo = (JSONObject)jcompanyHospitalCoupRet.get(k);
				Assert.assertEquals(jdo.get("companyName").toString(),dbCompanyName);//单位名称
				System.out.println("正在比较单位优惠券账单.."+dbCompanyName);
				long retTotalPayment = Long.parseLong(jdo.get("totalPayment").toString());//返回的单位应收合计
				long dbNeedPayment = 0l; //数据库中统计的单位应收金额合计（初始化）
				List<TradeHospitalCompanyBill> oneComRetBills = JSONArray.parseArray(jdo.get("tradeHospitalCompanyBillList").toString(), TradeHospitalCompanyBill.class);
				List<TradeHospitalCompanyBill> dbOneComBills = SettleChecker.getTradeHospitalCompanyBillByColumn("id",false,null, false,"company_id",dbCompanyId+"","status",SettlementHospitalConfirmEnum.SETTLEMENT_CONFIRMD.getCode()+"");
				Assert.assertEquals(oneComRetBills.size(), dbOneComBills.size());
				for(int i=0;i<oneComRetBills.size();i++){
					Assert.assertEquals(oneComRetBills.get(i).getId(),dbOneComBills.get(i).getId());
					Assert.assertEquals(oneComRetBills.get(i).getBatchSn(),dbOneComBills.get(i).getBatchSn());
					Assert.assertEquals(oneComRetBills.get(i).getCompanyId(),dbOneComBills.get(i).getCompanyId());
					Assert.assertEquals(oneComRetBills.get(i).getGmtCreated(),dbOneComBills.get(i).getGmtCreated());
					Assert.assertEquals(oneComRetBills.get(i).getGmtModified(),dbOneComBills.get(i).getGmtModified());
					Assert.assertEquals(oneComRetBills.get(i).getHospitalId(),dbOneComBills.get(i).getHospitalId());
					Assert.assertEquals(oneComRetBills.get(i).getIsDeleted(),dbOneComBills.get(i).getIsDeleted());
					Assert.assertEquals(oneComRetBills.get(i).getOperatorId(),dbOneComBills.get(i).getOperatorId());
					Assert.assertEquals(oneComRetBills.get(i).getCompanyPayAmount(),dbOneComBills.get(i).getCompanyPayAmount());
					Assert.assertEquals(oneComRetBills.get(i).getCompanyChargedAmount(),dbOneComBills.get(i).getCompanyChargedAmount());
					Assert.assertEquals(oneComRetBills.get(i).getCompanyRefundAmount(),dbOneComBills.get(i).getCompanyRefundAmount());
					Assert.assertEquals(oneComRetBills.get(i).getSn(),dbOneComBills.get(i).getSn());
					Assert.assertEquals(oneComRetBills.get(i).getStatus(),dbOneComBills.get(i).getStatus());
					dbNeedPayment += dbOneComBills.get(i).getCompanyChargedAmount().longValue();
				}
				//比较单位应收合计正确
				Assert.assertEquals(retTotalPayment,dbNeedPayment);
			}


			//3.医院线上账单
			dbCompanyBills = SettleChecker.getTradeHospitalCompanyBillByColumn(null,false,"company_id",true,"hospital_id",hospitalId+"","company_id",companyId+"","status",SettlementHospitalConfirmEnum.SETTLEMENT_CONFIRMD.getCode()+"","type","3");;
			//逐个单位比较
			for(int k=0 ; k<dbCompanyBills.size();k++){
				int dbCompanyId = dbCompanyBills.get(k).getCompanyId();
				String dbCompanyName = CompanyChecker.getHospitalCompanyById(dbCompanyId).getName();
				JSONObject jdo = (JSONObject)jcompanyHospitalOnlineRet.get(k);
				Assert.assertEquals(jdo.get("companyName").toString(),dbCompanyName);//单位名称
				System.out.println("正在比较单位线上账单.."+dbCompanyName);
				long retTotalPayment = Long.parseLong(jdo.get("totalPayment").toString());//返回的单位应收合计
				long dbNeedPayment = 0l; //数据库中统计的单位应收金额合计（初始化）
				List<TradeHospitalCompanyBill> oneComRetBills = JSONArray.parseArray(jdo.get("tradeHospitalCompanyBillList").toString(), TradeHospitalCompanyBill.class);
				List<TradeHospitalCompanyBill> dbOneComBills = SettleChecker.getTradeHospitalCompanyBillByColumn("id",false,null, false,"company_id",dbCompanyId+"","status",SettlementHospitalConfirmEnum.SETTLEMENT_CONFIRMD.getCode()+"");
				Assert.assertEquals(oneComRetBills.size(), dbOneComBills.size());
				for(int i=0;i<oneComRetBills.size();i++){
					Assert.assertEquals(oneComRetBills.get(i).getId(),dbOneComBills.get(i).getId());
					Assert.assertEquals(oneComRetBills.get(i).getBatchSn(),dbOneComBills.get(i).getBatchSn());
					Assert.assertEquals(oneComRetBills.get(i).getCompanyId(),dbOneComBills.get(i).getCompanyId());
					Assert.assertEquals(oneComRetBills.get(i).getGmtCreated(),dbOneComBills.get(i).getGmtCreated());
					Assert.assertEquals(oneComRetBills.get(i).getGmtModified(),dbOneComBills.get(i).getGmtModified());
					Assert.assertEquals(oneComRetBills.get(i).getHospitalId(),dbOneComBills.get(i).getHospitalId());
					Assert.assertEquals(oneComRetBills.get(i).getIsDeleted(),dbOneComBills.get(i).getIsDeleted());
					Assert.assertEquals(oneComRetBills.get(i).getOperatorId(),dbOneComBills.get(i).getOperatorId());
					Assert.assertEquals(oneComRetBills.get(i).getCompanyPayAmount(),dbOneComBills.get(i).getCompanyPayAmount());
					Assert.assertEquals(oneComRetBills.get(i).getCompanyChargedAmount(),dbOneComBills.get(i).getCompanyChargedAmount());
					Assert.assertEquals(oneComRetBills.get(i).getCompanyRefundAmount(),dbOneComBills.get(i).getCompanyRefundAmount());
					Assert.assertEquals(oneComRetBills.get(i).getSn(),dbOneComBills.get(i).getSn());
					Assert.assertEquals(oneComRetBills.get(i).getStatus(),dbOneComBills.get(i).getStatus());
					dbNeedPayment += dbOneComBills.get(i).getCompanyChargedAmount().longValue();
				}
				//比较单位应收合计正确
				Assert.assertEquals(retTotalPayment,dbNeedPayment);
			}


			//4.医院线下账单
			dbCompanyBills = SettleChecker.getTradeHospitalCompanyBillByColumn(null,false,"company_id",true,"hospital_id",hospitalId+"","company_id",companyId+"","status",SettlementHospitalConfirmEnum.SETTLEMENT_CONFIRMD.getCode()+"","type","4");;
			//逐个单位比较
			for(int k=0 ; k<dbCompanyBills.size();k++){
				int dbCompanyId = dbCompanyBills.get(k).getCompanyId();
				String dbCompanyName = CompanyChecker.getHospitalCompanyById(dbCompanyId).getName();
				JSONObject jdo = (JSONObject)jcompanyHospitalOfflineRet.get(k);
				Assert.assertEquals(jdo.get("companyName").toString(),dbCompanyName);//单位名称
				System.out.println("正在比较单位线下账单.."+dbCompanyName);
				long retTotalPayment = Long.parseLong(jdo.get("totalPayment").toString());//返回的单位应收合计
				long dbNeedPayment = 0l; //数据库中统计的单位应收金额合计（初始化）
				List<TradeHospitalCompanyBill> oneComRetBills = JSONArray.parseArray(jdo.get("tradeHospitalCompanyBillList").toString(), TradeHospitalCompanyBill.class);
				List<TradeHospitalCompanyBill> dbOneComBills = SettleChecker.getTradeHospitalCompanyBillByColumn("id",false,null, false,"company_id",dbCompanyId+"","status",SettlementHospitalConfirmEnum.SETTLEMENT_CONFIRMD.getCode()+"");
				Assert.assertEquals(oneComRetBills.size(), dbOneComBills.size());
				for(int i=0;i<oneComRetBills.size();i++){
					Assert.assertEquals(oneComRetBills.get(i).getId(),dbOneComBills.get(i).getId());
					Assert.assertEquals(oneComRetBills.get(i).getBatchSn(),dbOneComBills.get(i).getBatchSn());
					Assert.assertEquals(oneComRetBills.get(i).getCompanyId(),dbOneComBills.get(i).getCompanyId());
					Assert.assertEquals(oneComRetBills.get(i).getGmtCreated(),dbOneComBills.get(i).getGmtCreated());
					Assert.assertEquals(oneComRetBills.get(i).getGmtModified(),dbOneComBills.get(i).getGmtModified());
					Assert.assertEquals(oneComRetBills.get(i).getHospitalId(),dbOneComBills.get(i).getHospitalId());
					Assert.assertEquals(oneComRetBills.get(i).getIsDeleted(),dbOneComBills.get(i).getIsDeleted());
					Assert.assertEquals(oneComRetBills.get(i).getOperatorId(),dbOneComBills.get(i).getOperatorId());
					Assert.assertEquals(oneComRetBills.get(i).getCompanyPayAmount(),dbOneComBills.get(i).getCompanyPayAmount());
					Assert.assertEquals(oneComRetBills.get(i).getCompanyChargedAmount(),dbOneComBills.get(i).getCompanyChargedAmount());
					Assert.assertEquals(oneComRetBills.get(i).getCompanyRefundAmount(),dbOneComBills.get(i).getCompanyRefundAmount());
					Assert.assertEquals(oneComRetBills.get(i).getSn(),dbOneComBills.get(i).getSn());
					Assert.assertEquals(oneComRetBills.get(i).getStatus(),dbOneComBills.get(i).getStatus());
					dbNeedPayment += dbOneComBills.get(i).getCompanyChargedAmount().longValue();
				}
				//比较单位应收合计正确
				Assert.assertEquals(retTotalPayment,dbNeedPayment);
			}
		}
	}

	
	private void Sort(List<TradeHospitalCompanyBill> bills){
		  Collections.sort(bills, new Comparator<TradeHospitalCompanyBill>() {
		    	@Override
		    	public int compare(TradeHospitalCompanyBill o1,
		    			TradeHospitalCompanyBill o2) {
		    		return o1.getCompanyName().compareTo(o2.getCompanyName());
		    	}
			});
  
	}
	
	private void SortJSONObject (List<JSONObject> bills){
		  Collections.sort(bills, new Comparator<JSONObject>() {
		    	@Override
		    	public int compare(JSONObject o1,
		    			JSONObject o2) {
		    		return  o1.getString("companyName").compareTo(o2.getString("companyName"));
		    	}
			});

	}
	
	  @DataProvider
	  public Iterator<String[]> companyBill(){
			try {
				return CvsFileUtil.fromCvsFileToIterator("./csv/settlement/companyBill.csv",18);
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
