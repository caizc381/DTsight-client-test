package com.tijiantest.testcase.crm.paymentOrder;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.tijiantest.base.dbcheck.OrderChecker;
import com.tijiantest.base.dbcheck.SettleChecker;
import com.tijiantest.model.common.LogTypeEnum;
import com.tijiantest.model.payment.trade.PayConstants;
import com.tijiantest.model.settlement.PaymentOrder;
import com.tijiantest.model.settlement.TradeCommonLogResultDTO;
import com.tijiantest.testcase.crm.settlement.SettleBase;
import com.tijiantest.util.DateUtils;
import com.tijiantest.util.ListUtil;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jayway.jsonpath.JsonPath;
import com.tijiantest.base.HttpResult;
import com.tijiantest.base.dbcheck.CompanyChecker;
import com.tijiantest.base.dbcheck.OrderChecker;
import com.tijiantest.model.paymentOrder.PaymentOrderQueryDTO;
import com.tijiantest.model.settlement.PaymentOrder;
import com.tijiantest.model.settlement.TradeSettlementPayRecord;
import com.tijiantest.testcase.crm.CrmBase;
import com.tijiantest.util.CvsFileUtil;
import com.tijiantest.util.pagination.Page;
import sun.awt.image.IntegerComponentRaster;

/**
 * 查询收款记录
 * 位置: CRM->订单&用户->付款订单
 * @author hongyan
 *
 */


public class GetPaymentOrderByPageTest extends SettleBase{
	
	@SuppressWarnings("unused")
	@Test(description = "获取付款订单记录" , groups = {"qa"},dataProvider = "paymentorder")
	public void test_01_PaymentOrder(String ...args) throws ParseException{
		
		String startTimeStr = args[1];
		String endTimeStr = args[2];		
		String nameStr=args[3];
		String statusStr=args[4];
		String settlementStatusStr = args[5];
		String managerIdStr = args[6];
		String pageSize =args[7];
		String start_time = null;
		String end_time = null;
		String name=null;
		int managerId=-1;
		List<Integer> status= new ArrayList<>();
		int settlementStatus = -1;
		
			
	    PaymentOrderQueryDTO dto = new PaymentOrderQueryDTO();

	    if(!IsArgsNull(startTimeStr)){
			start_time = startTimeStr;
			dto.setStartTime(startTimeStr);
	    }
	    if(!IsArgsNull(endTimeStr)){
			end_time = endTimeStr;
			dto.setEndTime(endTimeStr);
	    }
	    if(!IsArgsNull(pageSize)){
			Page page = new Page();
			page.setOffset(0);
			page.setPageSize(Integer.parseInt(pageSize));
			dto.setPage(page);
		}
	    if(!IsArgsNull(nameStr)){
	    	name = nameStr; 
			dto.setName(nameStr);
	    }
	    if(!IsArgsNull(statusStr)){
	    	String[] sts = statusStr.split("#");
			List<Integer> intStatusList = ListUtil.StringArraysToIntegerList(sts);
			status = intStatusList;
			dto.setStatusList(intStatusList);
	    }

		if(!IsArgsNull(settlementStatusStr)){
			settlementStatus = Integer.parseInt(settlementStatusStr);
			dto.setSettlementStatus(settlementStatus);
		}

	    if(!IsArgsNull(managerIdStr)){
	    	if(managerIdStr.equals("DEFAULT"))
				managerId = defSettAccountId;
	    	else
	    		managerId = Integer.parseInt(managerIdStr);
	    	dto.setManagerId(managerId);
		}

	    dto.setOrganizationId(defSettHospital.getId());

	    HttpResult response = httpclient.post(GetPaymentOrderByPage, JSON.toJSONString(dto));
	    Assert.assertEquals(response.getCode() , HttpStatus.SC_OK);
	    
	    String records = JsonPath.read(response.getBody(), "$.records").toString();
	    String body = response.getBody();
	    System.out.println(body);
	    List<PaymentOrder> retList = JSON.parseArray(records, PaymentOrder.class);
		if(checkdb) {
			
			log.info("hospitalId"+defSettHospital.getId() +"...start_time"+start_time+"...end_time"+end_time+"...name"+name+"...managerId"+managerId+"...status"+status+"...settlementStatus"+settlementStatus);
			String sql = "select * from tb_payment_order where organization_id ="+defSettHospital.getId();
			
			if(start_time != null)
				sql += " and gmt_created > '"+ sdf.format(DateUtils.offsetDestDay(sdf.parse(start_time),-1))+"'";
			if (end_time != null)
				sql += " and gmt_created < '"+sdf.format(DateUtils.offsetDestDay(sdf.parse(end_time),1))+"'";
			if(name != null)
				sql += " and payment_name = '"+name+"'";
			if(managerId !=-1)
				sql +=" and manager_id="+managerId;
			if(status!=null&& status.size()>0)
				sql +=" and status in ("+ListUtil.IntegerlistToString(status)+")";
				sql +=" order by gmt_created desc";
			log.info("sql.."+sql);
			List<PaymentOrder> initDbList = OrderChecker.getPaymentOrderListBySql(sql);
			if(settlementStatus!=-1){//有结算标识
				List<PaymentOrder> tmpList = new ArrayList<>();
				for(PaymentOrder o:initDbList){
					if(o.getSettlementStatus()!= null && o.getSettlementStatus().intValue() == settlementStatus)
						tmpList.add(o);

				}
				initDbList.clear();
				initDbList.addAll(tmpList);
			}
			//处理分页（在所有条件之后处理）
			List<PaymentOrder> dbList = new ArrayList<>();
			if(pageSize != null ){
				 int pageInt = Integer.parseInt(pageSize);
				 if(pageInt <= initDbList.size())
				 	for(int k=0;k< pageInt;k++)
				 		dbList.add(initDbList.get(k));
				 else
				 	dbList = initDbList;
			}

			Assert.assertEquals(retList.size(),dbList.size());
			
			for(int i=0;i<dbList.size();i++){
				log.debug("name"+retList.get(i).getName());
				Assert.assertEquals(retList.get(i).getName(), dbList.get(i).getName());
				Assert.assertEquals(retList.get(i).getManagerId(), dbList.get(i).getManagerId());
				Assert.assertEquals(retList.get(i).getManagerName(), dbList.get(i).getManagerName());
				Assert.assertEquals(retList.get(i).getCompanyId(), dbList.get(i).getCompanyId());
				Assert.assertEquals(retList.get(i).getHospitalId(), dbList.get(i).getHospitalId());
				Assert.assertEquals(retList.get(i).getStatus(), dbList.get(i).getStatus());
				if(retList.get(i).getRemark()!=null)
					Assert.assertEquals(retList.get(i).getRemark(), dbList.get(i).getRemark());
				else
					Assert.assertNull(dbList.get(i).getRemark());
				if(retList.get(i).getHospitalRemark()!=null)
					Assert.assertEquals(retList.get(i).getHospitalRemark(), dbList.get(i).getHospitalRemark());
				else
					Assert.assertNull(dbList.get(i).getHospitalRemark());
				Assert.assertEquals(retList.get(i).getOrderNum(), dbList.get(i).getOrderNum());
				Assert.assertEquals(retList.get(i).getCreateTime(), dbList.get(i).getCreateTime());	
				Assert.assertEquals(retList.get(i).getAmount(), dbList.get(i).getAmount());
				if(retList.get(i).getSettlementStatus()!=null){
					Assert.assertEquals(retList.get(i).getSettlementStatus(), dbList.get(i).getSettlementStatus());
					if(retList.get(i).getSettlementStatus().intValue() == 1 ||retList.get(i).getSettlementStatus().intValue() == 2)
						Assert.assertEquals(retList.get(i).getSettlementBatchSn(), dbList.get(i).getSettlementBatchSn());
				}else
					Assert.assertNull(dbList.get(i).getSettlementStatus());

				//流转日志
				List<TradeCommonLogResultDTO> dbCommonLogList = SettleChecker.getTradeCommonLogList(dbList.get(i).getId()+"", LogTypeEnum.LOG_TYPE_REFUND.getValue(),null);//获取结算平台账单审核
				List<TradeCommonLogResultDTO> retCommonLogList = retList.get(i).getCirculationLog();
				Assert.assertEquals(retCommonLogList.size(),dbCommonLogList.size());
				if(retCommonLogList.size() >0){
					Assert.assertEquals(retCommonLogList.size(),1);
					Assert.assertEquals(retCommonLogList.get(0).getOperation(),dbCommonLogList.get(0).getOperation());
					Assert.assertEquals(retCommonLogList.get(0).getOperatorName(),dbCommonLogList.get(0).getOperatorName());
					Assert.assertEquals(retCommonLogList.get(0).getRefSn(),dbCommonLogList.get(0).getRefSn());
					Assert.assertEquals(retCommonLogList.get(0).getLogType(),dbCommonLogList.get(0).getLogType());
					Assert.assertEquals(retCommonLogList.get(0).getGmtCreated().getTime(),dbCommonLogList.get(0).getGmtCreated().getTime());
				}

			}								
		}			 			
	}
	@DataProvider
	  public Iterator<String[]> paymentorder(){
		try {
				return CvsFileUtil.fromCvsFileToIterator("./csv/paymentOrder/paymentorder.csv",18);
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
		
		
		
		
		
		
		
	

