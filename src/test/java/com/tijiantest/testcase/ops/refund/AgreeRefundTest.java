package com.tijiantest.testcase.ops.refund;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.tijiantest.base.dbcheck.SettleChecker;
import org.apache.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tijiantest.base.Flag;
import com.tijiantest.base.HttpResult;
import com.tijiantest.base.dbcheck.OrderChecker;
import com.tijiantest.base.dbcheck.PayChecker;
import com.tijiantest.model.order.Order;
import com.tijiantest.model.order.OrderStatus;
import com.tijiantest.model.order.orderrefund.BatchOrderRefundAuditVO;
import com.tijiantest.testcase.crm.order.HisOrderTest;
import com.tijiantest.testcase.ops.OpsBase;
import com.tijiantest.util.CvsFileUtil;
import com.tijiantest.util.db.DBMapper;
import com.tijiantest.util.db.MongoDBUtils;
import com.tijiantest.util.db.SqlException;

/**
 * 列举退款订单列表(OPS->退款审批->待审核页面->同意退款)
 * @author huifang
 *
 */
public class AgreeRefundTest extends OpsBase{
	
	@SuppressWarnings("deprecation")
	@Test(description="待审核页面->同意退款(全部退款）",dataProvider="agreeRefund",groups = {"qa"},dependsOnGroups="crm_hisExportOrder",ignoreMissingDependencies = true)
	public void test_01_AgreeRefund(String ...args) throws ParseException, SqlException{
		Order order  = null;
		try{
		order = HisOrderTest.firstOrder;
		}catch (AssertionError e ){
			log.error("crm登陆出错，crm未开启");
		}catch (NoClassDefFoundError e1){
			log.error("crm登陆出错，crm未开启");
		}
		if(order == null){
			List<Order> orderList = OrderChecker.getRefundFullMoneyApplyOrderList(defHospitalId);
			if(orderList == null || orderList.size() == 0){
				log.error("医院id:"+defHospitalId+"待审批页面没有可用的审批记录，请先回单");
				return;
			}
			order = orderList.get(0);
		}
		int orderSettlement = SettleChecker.getExamOrderSettleByColumn("order_num",order.getOrderNum()).get(0).getHospitalSettlementStatus().intValue();

		String reason = args[1];
		List<String> orderNumList = new ArrayList<String>();
		orderNumList.add(order.getOrderNum());
		BatchOrderRefundAuditVO audit = new BatchOrderRefundAuditVO();
		audit.setOrderNumList(orderNumList);
		audit.setReason(reason);
		String request = JSON.toJSONString(audit);
		HttpResult result = httpclient.post(Flag.OPS,AgreeRefund,request);
		log.info(result.getBody());
		Assert.assertEquals(result.getCode(),HttpStatus.SC_OK);
		if(checkdb){
			//ops
			String opsSql = "select * from tb_employee where login_name = '"+defusername+"'";
			List<Map<String,Object>> opsList = DBMapper.queryOps(opsSql);
			Assert.assertEquals(opsList.size(),1);
			Map<String,Object> opsMap = opsList.get(0);
			String employee_name = opsMap.get("employee_name").toString();
			int employee_id = Integer.parseInt(opsMap.get("id").toString());
			
			//tb_order_refund_apply
			String sql = "select * from tb_order_refund_apply where order_num='"+order.getOrderNum()+"'";
			List<Map<String,Object>> dblist = DBMapper.query(sql);
			Assert.assertEquals(dblist.size(),1);
			Map<String,Object> map = dblist.get(0);
			Assert.assertEquals(Integer.parseInt(map.get("status").toString()),1);
			Assert.assertEquals(map.get("reason").toString(),reason);
			int refund_id = Integer.parseInt(map.get("id").toString());
			int amount = Integer.parseInt(map.get("amount").toString());
			JSONObject apply = JSON.parseObject(map.get("pay_detail").toString());
			int cardPayAmount = Integer.parseInt(apply.get("cardPayAmount").toString());
			int offlinePayAmount = Integer.parseInt(apply.get("offlinePayAmount").toString());
			int onlinePayAmount = Integer.parseInt(apply.get("onlinePayAmount").toString());
			int parentCardPayAmount = Integer.parseInt(apply.get("parentCardPayAmount").toString());
			int totalAmount = Integer.parseInt(apply.get("totalAmount").toString());
			
			//tb_order_refund_apply_log
			String logSql = "select * from tb_order_refund_apply_record where refund_id ="+refund_id +" order by id";
			List<Map<String,Object>> applyLogList = DBMapper.query(logSql);
			Assert.assertEquals(applyLogList.size(), 2);
			 Map<String,Object> secondApply = applyLogList.get(1);
			 Assert.assertEquals(Integer.parseInt(secondApply.get("account_id").toString()), order.getAccount().getId().intValue());
			 Assert.assertEquals(Integer.parseInt(secondApply.get("from_site").toString()), order.getHospital().getId());
			 Assert.assertEquals(Integer.parseInt(secondApply.get("hospital_company_id").toString()), order.getHospitalCompany().getId().intValue());
			 Assert.assertEquals(Integer.parseInt(secondApply.get("amount").toString()), amount);
			 Assert.assertEquals(Integer.parseInt(secondApply.get("status").toString()),1);//审核通过
			 Assert.assertEquals(secondApply.get("reason").toString(),reason);
			 Assert.assertEquals(secondApply.get("operator_name").toString(),employee_name);//客服
			 Assert.assertEquals(Integer.parseInt(secondApply.get("operator").toString()),employee_id);//客服账户	 
			 JSONObject jsecond = JSON.parseObject(secondApply.get("pay_detail").toString());
			 Assert.assertEquals(cardPayAmount,Integer.parseInt(jsecond.get("cardPayAmount").toString()));
			 Assert.assertEquals(offlinePayAmount,Integer.parseInt(jsecond.get("offlinePayAmount").toString()));
			 Assert.assertEquals(onlinePayAmount,Integer.parseInt(jsecond.get("onlinePayAmount").toString()));
			 Assert.assertEquals(parentCardPayAmount,Integer.parseInt(jsecond.get("parentCardPayAmount").toString()));
			 Assert.assertEquals(totalAmount,Integer.parseInt(jsecond.get("totalAmount").toString()));
			 
			 //验证订单&交易(已撤销)
			 Assert.assertEquals(OrderChecker.getOrderInfo(order.getId()).getStatus(),OrderStatus.REVOCATION.intValue());
			 if(checkmongo){
					List<Map<String,Object>> list = MongoDBUtils.query("{'id':"+order.getId()+"}", MONGO_COLLECTION);
					Assert.assertNotNull(list);
					Assert.assertEquals(1, list.size());
					 Assert.assertEquals(Integer.parseInt(list.get(0).get("status").toString()),OrderStatus.REVOCATION.intValue());

				}
			 //更改为需要结算退款
			 OrderChecker.check_AgreeRefund_ExamOrderSettlement(order,amount,orderSettlement);
			 try {
				PayChecker.checkRevokeTrade(order,OrderStatus.ALREADY_BOOKED.intValue());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
			
	}

	@SuppressWarnings("deprecation")
	@Test(description="待审核页面->同意退款(部分退款）",dataProvider="agreeRefund",groups = {"qa"},dependsOnGroups="crm_hisExportOrder",ignoreMissingDependencies=true)
	public void test_02_AgreeRefund(String ...args) throws ParseException, SqlException{	
		Order order = null;
		try{
			order = HisOrderTest.thirdOrder;
		}catch (NoClassDefFoundError e ){
			log.error("crm登陆出错，crm未开启");
		}
		if(order == null){
			List<Order> orderList = OrderChecker.getRefundPartMoneyApplyOrderList(defHospitalId);
			if(orderList == null || orderList.size() == 0){
				log.error("医院id:"+defHospitalId+"待审批页面没有可用的审批记录，请先回单");
				return;
			}

			order = orderList.get(0);
		}
		int orderSettlement = SettleChecker.getExamOrderSettleByColumn("order_num",order.getOrderNum()).get(0).getHospitalSettlementStatus().intValue();
		int tradeAccountId = hospitalRecevieTradeAccountId;
		String reason = args[1];
		List<String> orderNumList = new ArrayList<String>();
		orderNumList.add(order.getOrderNum());
		BatchOrderRefundAuditVO audit = new BatchOrderRefundAuditVO();
		audit.setOrderNumList(orderNumList);
		audit.setReason(reason);
		String request = JSON.toJSONString(audit);
		HttpResult result = httpclient.post(Flag.OPS,AgreeRefund,request);
		log.info(result.getBody());
		Assert.assertEquals(result.getCode(),HttpStatus.SC_OK);
		if(checkdb){
			//ops
			String opsSql = "select * from tb_employee where login_name = '"+defusername+"'";
			List<Map<String,Object>> opsList = DBMapper.queryOps(opsSql);
			Assert.assertEquals(opsList.size(),1);
			Map<String,Object> opsMap = opsList.get(0);
			String employee_name = opsMap.get("employee_name").toString();
			int employee_id = Integer.parseInt(opsMap.get("id").toString());
			
			//tb_order_refund_apply
			String sql = "select * from tb_order_refund_apply where order_num='"+order.getOrderNum()+"'";
			List<Map<String,Object>> dblist = DBMapper.query(sql);
			Assert.assertEquals(dblist.size(),1);
			Map<String,Object> map = dblist.get(0);
			Assert.assertEquals(Integer.parseInt(map.get("status").toString()),1);
			Assert.assertEquals(map.get("reason").toString(),reason);
			int refund_id = Integer.parseInt(map.get("id").toString());
			int amount = Integer.parseInt(map.get("amount").toString());
			JSONObject apply = JSON.parseObject(map.get("pay_detail").toString());
			int cardPayAmount = Integer.parseInt(apply.get("cardPayAmount").toString());
			int offlinePayAmount = Integer.parseInt(apply.get("offlinePayAmount").toString());
			int onlinePayAmount = Integer.parseInt(apply.get("onlinePayAmount").toString());
			int parentCardPayAmount = Integer.parseInt(apply.get("parentCardPayAmount").toString());
			int totalAmount = Integer.parseInt(apply.get("totalAmount").toString());
			
			//tb_order_refund_apply_log
			String logSql = "select * from tb_order_refund_apply_record where refund_id ="+refund_id +"  and is_deleted = 0 order by id";
			List<Map<String,Object>> applyLogList = DBMapper.query(logSql);
			Assert.assertEquals(applyLogList.size(), 2);
			 Map<String,Object> secondApply = applyLogList.get(1);
			 Assert.assertEquals(Integer.parseInt(secondApply.get("account_id").toString()), order.getAccount().getId().intValue());
			 Assert.assertEquals(Integer.parseInt(secondApply.get("from_site").toString()), order.getHospital().getId());
			 Assert.assertEquals(Integer.parseInt(secondApply.get("hospital_company_id").toString()), order.getHospitalCompany().getId().intValue());
			 Assert.assertEquals(Integer.parseInt(secondApply.get("amount").toString()), amount);
			 Assert.assertEquals(Integer.parseInt(secondApply.get("status").toString()),1);//审核通过
			 Assert.assertEquals(secondApply.get("reason").toString(),reason);
			 Assert.assertEquals(secondApply.get("operator_name").toString(),employee_name);//客服
			 Assert.assertEquals(Integer.parseInt(secondApply.get("operator").toString()),employee_id);//客服账户	 
			 JSONObject jsecond = JSON.parseObject(secondApply.get("pay_detail").toString());
			 Assert.assertEquals(cardPayAmount,Integer.parseInt(jsecond.get("cardPayAmount").toString()));
			 Assert.assertEquals(offlinePayAmount,Integer.parseInt(jsecond.get("offlinePayAmount").toString()));
			 Assert.assertEquals(onlinePayAmount,Integer.parseInt(jsecond.get("onlinePayAmount").toString()));
			 Assert.assertEquals(parentCardPayAmount,Integer.parseInt(jsecond.get("parentCardPayAmount").toString()));
			 Assert.assertEquals(totalAmount,Integer.parseInt(jsecond.get("totalAmount").toString()));
			 
			 //验证订单&交易(部分退款,若退款金额等于订单金额全退)
			 Order orderX = OrderChecker.getOrderInfo(order.getId());
			 if(amount == orderX.getOrderPrice().intValue())
				 Assert.assertEquals(orderX.getStatus(),OrderStatus.REVOCATION.intValue());
			 else
				Assert.assertEquals(orderX.getStatus(),OrderStatus.PART_BACK.intValue());
			 if(checkmongo){
					List<Map<String,Object>> list = MongoDBUtils.query("{'id':"+order.getId()+"}", MONGO_COLLECTION);
					Assert.assertNotNull(list);
					Assert.assertEquals(1, list.size());
					 if(amount == orderX.getOrderPrice().intValue())
						 Assert.assertEquals(Integer.parseInt(list.get(0).get("status").toString()),OrderStatus.REVOCATION.intValue());
					else
				 	Assert.assertEquals(Integer.parseInt(list.get(0).get("status").toString()),OrderStatus.PART_BACK.intValue());

				}
			 
			 //更改为需要结算退款
			 OrderChecker.check_AgreeRefund_ExamOrderSettlement(order,amount,orderSettlement);
			 try {
				 if(amount == orderX.getOrderPrice().intValue() )
				 	PayChecker.checkRevokeTrade(order,OrderStatus.ALREADY_BOOKED.intValue());
				 else
				 	PayChecker.checkPartBackTrade(order,tradeAccountId,amount);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
			
	}
	@DataProvider
	public Iterator<String[]> agreeRefund() {
		try {
			return CvsFileUtil.fromCvsFileToIterator("./csv/opsRefund/agreeRefund.csv", 20);
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
