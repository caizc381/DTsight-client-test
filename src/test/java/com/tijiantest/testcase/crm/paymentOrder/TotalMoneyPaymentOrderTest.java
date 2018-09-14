package com.tijiantest.testcase.crm.paymentOrder;

import com.alibaba.fastjson.JSON;
import com.jayway.jsonpath.JsonPath;
import com.tijiantest.base.HttpResult;
import com.tijiantest.base.dbcheck.OrderChecker;
import com.tijiantest.base.dbcheck.PayChecker;
import com.tijiantest.model.order.channel.OrderQueryRequestParams;
import com.tijiantest.model.payment.trade.PayAmount;
import com.tijiantest.model.payment.trade.PayConstants;
import com.tijiantest.model.payment.trade.RefundAmount;
import com.tijiantest.model.paymentOrder.PaymentOrderQueryDTO;
import com.tijiantest.model.paymentOrder.PaymentOrderStatusEnum;
import com.tijiantest.model.settlement.PaymentOrder;
import com.tijiantest.testcase.crm.CrmBase;
import com.tijiantest.testcase.crm.settlement.SettleBase;
import com.tijiantest.util.CvsFileUtil;
import com.tijiantest.util.DateUtils;
import com.tijiantest.util.ListUtil;
import com.tijiantest.util.MapUtil;
import com.tijiantest.util.db.MongoDBUtils;
import com.tijiantest.util.db.SqlException;
import com.tijiantest.util.pagination.Page;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.message.BasicNameValuePair;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 立即统计
 * 位置：CRM->订单&用户->收款订单>立即统计
 * @author huifang
 *
 */
public class TotalMoneyPaymentOrderTest extends SettleBase{

	@Test(description = "立即统计" ,dataProvider="totalMoney_paymentOrder" ,groups = {"qa"})
	public void test_01_paymentOrder_totalMoney(String ... args) throws ParseException, IOException, SqlException, java.text.ParseException {
		String startTimeStr = args[1];
		String endTimeStr = args[2];
		String nameStr=args[3];
		String statusStr=args[4];
		String settlementStatusStr = args[5];
		String managerIdStr = args[6];
		String start_time = null;
		String end_time = null;
		String name=null;
		int managerId=-1;
        List<Integer> status= new ArrayList<>();
		int settlementStatus = -1;

        //STEP1: 入参解析
		List<NameValuePair> params = new ArrayList<>();

		if(!IsArgsNull(startTimeStr)){
			start_time = startTimeStr;
			params.add(new BasicNameValuePair("startTime",startTimeStr));

		}
		if(!IsArgsNull(endTimeStr)){
			end_time = endTimeStr;
			params.add(new BasicNameValuePair("endTime",endTimeStr));
		}
		if(!IsArgsNull(nameStr)){
			name = nameStr;
			params.add(new BasicNameValuePair("name",nameStr));
		}
		if(!IsArgsNull(statusStr)){
            String[] sts = statusStr.split("#");
            List<Integer> intStatusList = ListUtil.StringArraysToIntegerList(sts);
			params.add(new BasicNameValuePair("status",statusStr.replace("#",",")));

		}

		if(!IsArgsNull(settlementStatusStr)){
			settlementStatus = Integer.parseInt(settlementStatusStr);
			params.add(new BasicNameValuePair("settlementStatus",settlementStatusStr));
		}

		if(!IsArgsNull(managerIdStr)){
			if(managerIdStr.equals("DEFAULT"))
				managerId = defSettAccountId;
			else
				managerId = Integer.parseInt(managerIdStr);
			params.add(new BasicNameValuePair("managerId",managerId+""));
		}

		params.add(new BasicNameValuePair("organizationId",defSettHospitalId+""));
		//STEP2: 调用接口
		HttpResult result = httpclient.get(Payment_TotalMoney,params);
		Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);
		String body = result.getBody();
		log.info("立即统计返回.."+ body);
		long totalPrice = Long.parseLong(JsonPath.read(body,"$.totalPrice").toString());
		long totalPayPrice = Long.parseLong(JsonPath.read(body,"$.totalPayPrice").toString());
		long totalRefundPrice = Long.parseLong(JsonPath.read(body,"$.totalRefundPrice").toString());


		//STEP3：查询数据库，涮选后的收款订单
		long dbTotalPrice = 0l;
		long dbTotalPayPrice = 0l;
		long dbTotalRefundPrice = 0l;
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

            log.info("sql"+sql);
            List<PaymentOrder> dbList = OrderChecker.getPaymentOrderListBySql(sql);
            if(settlementStatus!=-1){//有结算标识
                List<PaymentOrder> tmpList = new ArrayList<>();
                for(PaymentOrder o:dbList){
                    if(o.getSettlementStatus()!= null && o.getSettlementStatus().intValue() == settlementStatus)
                        tmpList.add(o);
                }
                dbList.clear();
                dbList.addAll(tmpList);
            }
            //STEP4：比对数据库计算的金额
           if(dbList != null && dbList.size()>0){//查询的有数据
                for(PaymentOrder o : dbList){
                    //只统计已支付+部分退款的收款订单的支付和退款
                    if(o.getStatus().intValue() == PaymentOrderStatusEnum.PAYSUCCESS.getCode() || o.getStatus().intValue() == PaymentOrderStatusEnum.PARTREFUND.getCode()){
                        PayAmount payAmount = PayChecker.getPayAmountByOrderNum(o.getOrderNum(), PayConstants.OrderType.PaymentOrder);
                        RefundAmount refundAmount = PayChecker.getRefundAmountByOrderNum(o.getOrderNum(),PayConstants.OrderType.PaymentOrder);
                        dbTotalPayPrice +=  payAmount.getOnlinePayAmount();
                        dbTotalRefundPrice += refundAmount.getOnlineRefundAmount();
						dbTotalPrice += payAmount.getOnlinePayAmount()-refundAmount.getOnlineRefundAmount();
					}
                }
           }

			log.info("返回,订单总金额之和:"+totalPrice+";订单支付金额之和:"+totalPayPrice+";订单退款金额之和:"+totalRefundPrice);
			log.info("数据库订单总金额之和:"+dbTotalPrice+";订单支付金额之和:"+dbTotalPayPrice+";订单退款金额之和:"+dbTotalRefundPrice);
			Assert.assertEquals(totalPrice,dbTotalPrice);
			Assert.assertEquals(totalPayPrice,dbTotalPayPrice);
			Assert.assertEquals(totalRefundPrice,dbTotalRefundPrice);
		}
	}
	
	@DataProvider
	public Iterator<String[]> totalMoney_paymentOrder(){
		try {
			return CvsFileUtil.fromCvsFileToIterator("./csv/paymentOrder/totalMoney.csv",7);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}
}

