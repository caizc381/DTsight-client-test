package com.tijiantest.testcase.ops.paymentOrder;

import com.alibaba.fastjson.JSON;
import com.jayway.jsonpath.JsonPath;
import com.tijiantest.base.Flag;
import com.tijiantest.base.HttpResult;
import com.tijiantest.base.dbcheck.CompanyChecker;
import com.tijiantest.base.dbcheck.HospitalChecker;
import com.tijiantest.base.dbcheck.OrderChecker;
import com.tijiantest.base.dbcheck.SettleChecker;
import com.tijiantest.model.common.LogTypeEnum;
import com.tijiantest.model.paymentOrder.PaymentOrderQueryVO;
import com.tijiantest.model.paymentOrder.PaymentOrderVO;
import com.tijiantest.model.settlement.*;
import com.tijiantest.testcase.ops.OpsBase;
import com.tijiantest.util.CvsFileUtil;
import com.tijiantest.util.ListUtil;
import com.tijiantest.util.pagination.Page;
import org.apache.commons.collections.ListUtils;
import org.apache.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * 查询收款订单列表
 * 位置: ops->用户&订单->收款订单管理
 * @author huifang
 *
 */
public class GetPaymentOrderByPageTest extends OpsBase{

	@Test(description = "获取收款订单记录列表" , groups = {"qa"},dataProvider = "getPaymentOrderByPage")
	public void test_01_getPaymentOrderByPage(String ...args) throws ParseException{
		String organizationStr = args[1];
		String provinceStr = args[2];
		String cityStr = args[3];
		String districtStr = args[4];
		String startTimeStr = args[5];
		String endTimeStr = args[6];
		String statusLists = args[7];
		String settlementStr = args[8];
		String name = args[9];
		String pageSize = args[10];
		int organizationId = -1;
		int province=-1;
		int city=-1;
		int district=-1;
		List<Integer> statusList = new ArrayList<>();
		int settlementStatus = -1;
		String start_time = null;
		String end_time = null;
		PaymentOrderQueryVO dto = new PaymentOrderQueryVO();

		if(!IsArgsNull(organizationStr)){
			organizationId = Integer.parseInt(organizationStr);
			dto.setOrganizationId(organizationId);
		}
		if(!IsArgsNull(provinceStr)){
			province = Integer.parseInt(provinceStr);
			dto.setProvinceId(province);
			}
		if(!IsArgsNull(cityStr)){
			city = Integer.parseInt(cityStr);
			dto.setCityId(city);
			}
		if(!IsArgsNull(districtStr)){
			district = Integer.parseInt(districtStr);
			dto.setDistrictId(district);
			}

		if(!IsArgsNull(startTimeStr)){
			start_time = startTimeStr;
			dto.setStartTime(startTimeStr);
		}
		if(!IsArgsNull(endTimeStr)){
			end_time = endTimeStr;
			dto.setEndTime(endTimeStr);
		}
		if(!IsArgsNull(statusLists)){
			String[] statusArray = statusLists.split("#");
			statusList = ListUtil.StringArraysToIntegerList(statusArray);
			dto.setStatusList(statusList);
		}

		if(!IsArgsNull(settlementStr)){
			settlementStatus = Integer.parseInt(settlementStr);
			dto.setSettlementStatus(settlementStatus);
		}
		if(!IsArgsNull(name)){
			dto.setName(name);
		}
		if(!IsArgsNull(pageSize)){
			Page page = new Page();
			page.setOffset(0);
			//log.info("pageSize:"+pageSize);
			page.setPageSize(Integer.parseInt(pageSize));
			dto.setPage(page);
		}
		
	
		HttpResult response = httpclient.post(Flag.OPS,OPS_GetPaymentOrderByPage, JSON.toJSONString(dto));
		log.info(response.getBody());
		Assert.assertEquals(response.getCode(), HttpStatus.SC_OK);
		String records = JsonPath.read(response.getBody(), "$.records").toString();
		String body = response.getBody();
		System.out.println(body);
		List<PaymentOrderVO> retList = JSON.parseArray(records, PaymentOrderVO.class);
		if(checkdb){
			 List<Integer> hospitalList = SettleChecker.getHavePayRecordList(province, city, district, organizationId);
			 System.out.println("医院列表"+hospitalList); 
			 
				String sql = "select d.* from tb_payment_order  d ";
				if(organizationId != -1){
					sql += " where d.is_delete = 0 and d.organization_id = "+organizationId;
				}else  if(province == -1){//全国
					sql += " where d.is_delete = 0 ";

				}else{
					if(city == -1){//有省的时候
						sql += " , tb_hospital h where  h.address_id  like  '"+province/1000 +"%'  and h.id = d.organization_id  and d.is_delete = 0 ";
					}else{
						if(district == -1){//有省/市的时候
							sql += " , tb_hospital h where  h.address_id  like   '"+city/100 +"%'  and h.id = d.organization_id  and d.is_delete = 0 ";
						}else//有省/市/区的时候
							sql += "  , tb_hospital h where  h.address_id  like   '"+district +"%'  and h.id = d.organization_id  and d.is_delete = 0 ";
					}
				}
				
				if(start_time != null)
					sql += " and d.gmt_created > '"+start_time+"'";
				if (end_time != null)
					sql += " and d.gmt_created < '"+end_time+"'";
				if(!IsArgsNull(name))
					sql += " and d.payment_name = '"+name+"'";
				if(statusList.size()!=0)
					sql += " and d.status in ("+ListUtil.IntegerlistToString(statusList)+")";
//				if(settlementStatus != -1)
//					if(settlementStatus == 1)//结算中
//						sql += " and d.order_num in (select order_num from tb_trade_settlement_payment_order where hospital_settlement_status = "+ SettlementHospitalConfirmEnum.SETTLEMENT_TO_BE_CONFIRM.getCode()+" )";
//					else if (settlementStatus == 2)//已结算
//						sql += " and d.order_num in (select order_num from tb_trade_settlement_payment_order where hospital_settlement_status in = "+ SettlementHospitalConfirmEnum.SETTLEMENT_CONFIRMD.getCode()+")";
//					else if (settlementStatus == 0)//未结算
//						sql += " and d.order_num  not in (select order_num from tb_trade_settlement_payment_order where hospital_settlement_status in ( "+ SettlementHospitalConfirmEnum.SETTLEMENT_TO_BE_CONFIRM.getCode()+","+SettlementHospitalConfirmEnum.SETTLEMENT_CONFIRMD.getCode()+"))";//不在结算订单表中的已结算/结算中
				sql += "   order by d.gmt_created desc";
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
				Assert.assertEquals(retList.get(i).getAmount(), dbList.get(i).getAmount());
				Assert.assertEquals(retList.get(i).getCompanyId(), dbList.get(i).getCompanyId());
				Assert.assertEquals(retList.get(i).getCompanyName(), CompanyChecker.getHospitalCompanyById(dbList.get(i).getCompanyId()).getName());
				Assert.assertEquals(retList.get(i).getCreateTime(), dbList.get(i).getCreateTime());
				Assert.assertEquals(retList.get(i).getHospitalId(), dbList.get(i).getHospitalId());
				Assert.assertEquals(retList.get(i).getHospitalName(), HospitalChecker.getHospitalById(dbList.get(i).getHospitalId()).getName());
				if(retList.get(i).getRemark()!=null)
					Assert.assertEquals(retList.get(i).getRemark(), dbList.get(i).getRemark());
				else
					Assert.assertNull(dbList.get(i).getRemark());
				if(retList.get(i).getHospitalRemark()!=null)
					Assert.assertEquals(retList.get(i).getHospitalRemark(), dbList.get(i).getHospitalRemark());
				else
					Assert.assertNull(dbList.get(i).getHospitalRemark());
				if(retList.get(i).getSettlementStatus()!=null){
					Assert.assertEquals(retList.get(i).getSettlementStatus(), dbList.get(i).getSettlementStatus());
					if(retList.get(i).getSettlementStatus().intValue() == 1 ||retList.get(i).getSettlementStatus().intValue() == 2)
						Assert.assertEquals(retList.get(i).getSettlementBatchSn(), dbList.get(i).getSettlementBatchSn());
				}else
					Assert.assertNull(dbList.get(i).getSettlementStatus());
				Assert.assertEquals(retList.get(i).getId(), dbList.get(i).getId());
				Assert.assertEquals(retList.get(i).getManagerId(), dbList.get(i).getManagerId());
				Assert.assertEquals(retList.get(i).getManagerName(), dbList.get(i).getManagerName());
				Assert.assertEquals(retList.get(i).getName(), dbList.get(i).getName());
				Assert.assertEquals(retList.get(i).getStatus(), dbList.get(i).getStatus());
				Assert.assertEquals(retList.get(i).getOrderNum(), dbList.get(i).getOrderNum());
				Assert.assertEquals(retList.get(i).getRefundAmount(),dbList.get(i).getRefundAmount());
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
	  public Iterator<String[]> getPaymentOrderByPage(){
			try {
				return CvsFileUtil.fromCvsFileToIterator("./csv/paymentOrder/ops_getPaymentOrderList.csv",18);
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