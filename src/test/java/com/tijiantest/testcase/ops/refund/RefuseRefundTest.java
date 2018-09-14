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
import com.tijiantest.model.payment.trade.PayConstants.TradeType;
import com.tijiantest.testcase.crm.order.HisOrderTest;
import com.tijiantest.testcase.ops.OpsBase;
import com.tijiantest.util.CvsFileUtil;
import com.tijiantest.util.db.DBMapper;
import com.tijiantest.util.db.MongoDBUtils;
import com.tijiantest.util.db.SqlException;

/**
 * 列举退款订单列表(OPS->退款审批->待审核页面->拒绝退款)
 * @author huifang
 *
 */
public class RefuseRefundTest extends OpsBase{
	
	@SuppressWarnings("deprecation")
	@Test(description="待审核页面->拒绝退款(全部退款&部分退款)",dataProvider="refuseRefund",groups = {"qa"},dependsOnGroups="crm_hisExportOrder",ignoreMissingDependencies = true)
	public void test_01_refuseRefund(String ...args) throws ParseException, SqlException{
		List<Order> orderList = new ArrayList<Order>();
		List<String> orderNumList = new ArrayList<String>();

		Order order1  = null;
		Order order2 = null;
		try{
			order1 = HisOrderTest.secondOrder;
			order2 = HisOrderTest.forthOrder;
		}catch (AssertionError e ){
			log.error("crm登陆出错，crm未开启");
		}catch (NoClassDefFoundError e){
			log.error("crm登陆出错，crm未开启");
		}
		if(order1 == null ){
			List<Order> tmporderList = OrderChecker.getRefundFullMoneyApplyOrderList(defHospitalId);
			if(tmporderList == null || tmporderList.size() == 0){
				log.error("医院id:"+defHospitalId+"待审批页面没有可用的审批记录，请先回单");
				return;
			}
			order1 = tmporderList.get(0);
		}
		int order1Settlement = SettleChecker.getExamOrderSettleByColumn("order_num",order1.getOrderNum()).get(0).getHospitalSettlementStatus().intValue();

		if(order2 == null ){
			List<Order> tmporderList = OrderChecker.getRefundPartMoneyApplyOrderList(defHospitalId);
			if(tmporderList == null || tmporderList.size() == 0){
				log.error("医院id:"+defHospitalId+"待审批页面没有可用的审批记录，请先回单");
				return;
			}
			order2 = tmporderList.get(0);
		}
		int order2Settlement = SettleChecker.getExamOrderSettleByColumn("order_num",order2.getOrderNum()).get(0).getHospitalSettlementStatus().intValue();

		orderList.add(order1);
		orderList.add(order2);

		for(Order order:orderList)
			orderNumList.add(order.getOrderNum());
		String reason = args[1];
		BatchOrderRefundAuditVO audit = new BatchOrderRefundAuditVO();
		audit.setOrderNumList(orderNumList);
		audit.setReason(reason);
		String request = JSON.toJSONString(audit);
		HttpResult result = httpclient.post(Flag.OPS,RefuseRefund,request);
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
			
			for(Order order:orderList){
				//tb_order_refund_apply
				String sql = "select * from tb_order_refund_apply where order_num='"+order.getOrderNum()+"'";
				List<Map<String,Object>> dblist = DBMapper.query(sql);
				Assert.assertEquals(dblist.size(),1);
				Map<String,Object> map = dblist.get(0);
				Assert.assertEquals(Integer.parseInt(map.get("status").toString()),2);//拒绝退款
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
				String logSql = "select * from tb_order_refund_apply_record where refund_id ="+refund_id +" and is_deleted = 0 order by id";
				List<Map<String,Object>> applyLogList = DBMapper.query(logSql);
				Assert.assertEquals(applyLogList.size(), 2);
				 Map<String,Object> secondApply = applyLogList.get(1);
				 Assert.assertEquals(Integer.parseInt(secondApply.get("account_id").toString()), order.getAccount().getId().intValue());
				 Assert.assertEquals(Integer.parseInt(secondApply.get("from_site").toString()), order.getHospital().getId());
				 Assert.assertEquals(Integer.parseInt(secondApply.get("hospital_company_id").toString()), order.getHospitalCompany().getId().intValue());
				 Assert.assertEquals(Integer.parseInt(secondApply.get("amount").toString()), amount);
				 Assert.assertEquals(Integer.parseInt(secondApply.get("status").toString()),2);//审核拒绝
				 Assert.assertEquals(secondApply.get("reason").toString(),reason);
				 Assert.assertEquals(secondApply.get("operator_name").toString(),employee_name);//客服
				 Assert.assertEquals(Integer.parseInt(secondApply.get("operator").toString()),employee_id);//客服账户	 
				 JSONObject jsecond = JSON.parseObject(secondApply.get("pay_detail").toString());
				 Assert.assertEquals(cardPayAmount,Integer.parseInt(jsecond.get("cardPayAmount").toString()));
				 Assert.assertEquals(offlinePayAmount,Integer.parseInt(jsecond.get("offlinePayAmount").toString()));
				 Assert.assertEquals(onlinePayAmount,Integer.parseInt(jsecond.get("onlinePayAmount").toString()));
				 Assert.assertEquals(parentCardPayAmount,Integer.parseInt(jsecond.get("parentCardPayAmount").toString()));
				 Assert.assertEquals(totalAmount,Integer.parseInt(jsecond.get("totalAmount").toString()));
				 
				 //验证订单&交易
				 Assert.assertEquals(OrderChecker.getOrderInfo(order.getId()).getStatus(),OrderStatus.EXAM_FINISHED.intValue());
				 if(checkmongo){
						List<Map<String,Object>> list = MongoDBUtils.query("{'id':"+order.getId()+"}", MONGO_COLLECTION);
						Assert.assertNotNull(list);
						Assert.assertEquals(1, list.size());
						 Assert.assertEquals(Integer.parseInt(list.get(0).get("status").toString()),OrderStatus.EXAM_FINISHED.intValue());

					}
				 //订单结算关系不变
				 if(order.getId() == order1.getId())
				 	OrderChecker.check_Order_UnSettlementRefund(order,order1Settlement);
				 else
					 OrderChecker.check_Order_UnSettlementRefund(order,order2Settlement);

				Assert.assertTrue(PayChecker.getTradeOrderByOrderNum(order.getOrderNum(),TradeType.refund).size() == 0);
			}
			
		}
			
	}

	@DataProvider
	public Iterator<String[]> refuseRefund() {
		try {
			return CvsFileUtil.fromCvsFileToIterator("./csv/opsRefund/refuseRefund.csv", 20);
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
