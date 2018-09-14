package com.tijiantest.testcase.crm.order;

import java.io.IOException;
import java.util.*;

import com.tijiantest.model.payment.trade.PayConstants;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.testng.Assert;
import org.testng.annotations.Test;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.tijiantest.base.Flag;
import com.tijiantest.base.HttpResult;
import com.tijiantest.base.MyHttpClient;
import com.tijiantest.base.dbcheck.AccountChecker;
import com.tijiantest.base.dbcheck.CompanyChecker;
import com.tijiantest.base.dbcheck.CounterChecker;
import com.tijiantest.base.dbcheck.HospitalChecker;
import com.tijiantest.base.dbcheck.OrderChecker;
import com.tijiantest.base.dbcheck.PayChecker;
import com.tijiantest.base.dbcheck.ResourceChecker;
import com.tijiantest.model.account.AccountGenderEnum;
import com.tijiantest.model.account.AddAccountTypeEnum;
import com.tijiantest.model.company.ChannelCompany;
import com.tijiantest.model.order.Order;
import com.tijiantest.model.order.OrderStatus;
import com.tijiantest.model.payment.trade.RefundAmount;
import com.tijiantest.model.resource.meal.Meal;
import com.tijiantest.testcase.crm.CrmBase;
import com.tijiantest.util.DateUtils;
import com.tijiantest.util.db.MongoDBUtils;

/**
 * 手动退款
 * CRM订单详情->手动退款
 */
public class ManuallyRefundTest extends CrmBase {
	public  static Order firstRefundOrder = null; //第一个订单
	public  static Order secondRefundOrder = null; //第一个订单
	public  static List<Order> hisOrderList = new ArrayList<Order>();
	private static String accountfileName = "./csv/opsRefund/company_account_refund.xlsx";
	private int hisAccountId = 0; 
	private MyHttpClient platClient = new MyHttpClient();
	@Test(description = "平台客户经理CRM代预约",groups = {"qa"})
	public void test_01_createPlatOrder_manual() throws Exception{
		Integer offSetDay = HospitalChecker.getPreviousBookDaysByHospitalId(defhospital.getId());
		Date start = DateUtils.offsetDay(offSetDay);
		Date end = DateUtils.offsetDestDay(start, 30);
		try {
			//创建导入用户xls
			JSONArray idCardNameList = AccountChecker.makeUploadXls(2,accountfileName);
			List<Integer> companyLists = CompanyChecker.getCompanysIdByManagerId(defPlatAccountId,true);
			int newCompanyId = 0;
			String newCompanyName = null;
			for(Integer i : companyLists){
				ChannelCompany channelCom = CompanyChecker.getChannelCompanyByCompanyId(i);
				if(channelCom.getPlatformCompanyId() > 5){
					newCompanyId = channelCom.getId();
					newCompanyName = channelCom.getName();
					break;
					}
			}
			onceLoginInSystem(platClient, Flag.CRM, defPlatUsername, defPlatPasswd);
			AccountChecker.uploadAccount(platClient, newCompanyId, defhospital.getId(), "autotest_回单测试",
					accountfileName,AddAccountTypeEnum.idCard,true);
			
			for(int i=0;i<idCardNameList.size();i++){
				JSONObject jo = (JSONObject)idCardNameList.get(i);
				String idCard = jo.getString("idCard");
				String name = jo.getString("name");
				hisAccountId = AccountChecker.getAccountId(idCard, name, "autotest_回单测试",defPlatAccountId);
				//预约当天
				Integer hCompanyId = CompanyChecker.getHospitalCompanyByChannelCompanyId(newCompanyId, defhospital.getId()).getId();
				Map<String,Object> dateMap = CounterChecker.getDateBookableFromStartDate(start, end, hCompanyId, defhospital.getId());
				String examDate = dateMap.get("examDate").toString();
				int dayRangeId = Integer.parseInt(dateMap.get("dayRangeId").toString());
				List<Meal> mealList = ResourceChecker.getOffcialMeal(defhospital.getId(), Arrays.asList(AccountGenderEnum.MALE.getCode(),AccountGenderEnum.Common.getCode()));
				Order hisOrder = OrderChecker.crm_createOrder(platClient, mealList.get(0).getId(), hisAccountId, newCompanyId,newCompanyName,
						examDate,defhospital,dayRangeId);
				if(hisOrder.getId() != 0)
					hisOrderList.add(hisOrder);
				if(hisOrderList.size() == 1 )
					firstRefundOrder = hisOrder;
				if(hisOrderList.size() == 2)
					secondRefundOrder = hisOrder;
			}
			onceLogOutSystem(platClient, Flag.CRM);
		} catch (IOException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
	
		
	}
	
	@Test(description="导出为xls",groups = {"qa"},dependsOnMethods="test_01_createPlatOrder_manual")
	public void test_02_exportPlatToHis_manual(){
		if(hisOrderList == null || hisOrderList.size() == 0){
			log.info("没有可用的订单，无法导出为xls");
			return;
		}
		String orderStr = "";
		for(Order order : hisOrderList){
			orderStr += order.getId()+",";
		}
		System.out.println("...hisOrderList"+orderStr);
		int lenth = orderStr.length();
		orderStr = orderStr.substring(0, lenth-1);
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("hospitalId", defhospital.getId() + ""));
		NameValuePair nvp = new BasicNameValuePair("orderIds",
					"[" + orderStr + "]");
		params.add(nvp);
		
		HttpResult response = httpclient.post(Order_OrderInfoForExportXls, params);
		Assert.assertEquals(response.getCode(), HttpStatus.SC_OK,response.getBody());
//		System.out.println("http返回.."+response.getBody() );
		for(Order order : hisOrderList){
		Assert.assertTrue(response.getBody().contains(order.getId()+""));
		}
		params.add(new BasicNameValuePair("readOnly","false"));
		response = httpclient.post(Order_ExportOrderXls, params);
		Assert.assertEquals(response.getCode(),HttpStatus.SC_OK,"接口返回..."+response.getBody());
		
		if(checkdb){
			waitto(2);
			for(Order order:hisOrderList){
				Assert.assertTrue(OrderChecker.getOrderInfo(order.getId()).getIsExport());
				if(checkmongo){
					List<Map<String,Object>> list = MongoDBUtils.query("{'id':"+order.getId()+"}", MONGO_COLLECTION);
					Assert.assertEquals(list.get(0).get("isExport").toString(),"true");
				}
			}
			
		}
	}
	
	/**
	 * 第一个订单手动退款0，再手动退款部分
	 * @throws Exception 
	 */
	@Test(description = "手动退款0元->手动退款部分", groups = { "qa" },dependsOnMethods = "test_02_exportPlatToHis_manual")
	public void test_03_manuallyRefundTest() throws Exception {
		if(hisOrderList == null || hisOrderList.size() == 0){
			log.info("没有可用的订单，无法进行手动退款~~~~");
			return;
		}
		List<NameValuePair> params1 = new ArrayList<>();
		//手动退款0元
		Order o = firstRefundOrder;
		long firstRefundPrice = 0;
		long seconrdRefundPrice = o.getOrderPrice().longValue()-1;
		String firstRemark = "手动退款0元";
		String secondRemark = "手动部分退款";
		params1.add(new BasicNameValuePair("orderNum",o.getOrderNum() ));
		params1.add(new BasicNameValuePair("refundPrice",firstRefundPrice+""));
		params1.add(new BasicNameValuePair("remarks", firstRemark));

		HttpResult result = httpclient.get(Order_ManuallyRefund, params1);
		String body = result.getBody();
		Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);
		Assert.assertEquals(body,"{}");
		
		Order nowOrder = null;
		if(checkdb){
			nowOrder = OrderChecker.getOrderInfo(o.getId());
			Assert.assertEquals(nowOrder.getStatus(),OrderStatus.EXAM_FINISHED.intValue());//体检完成
			//退款金额为0
			RefundAmount refunds = PayChecker.getRefundAmountByOrderNum(nowOrder.getOrderNum(), PayConstants.OrderType.MytijianOrder);
			Assert.assertEquals(refunds.getCardRefundAmount(),0l);
			Assert.assertEquals(refunds.getOfflineRefundAmount(),0l);
			Assert.assertEquals(refunds.getOnlineRefundAmount(),0l);
			Assert.assertEquals(refunds.getPcardRefundAmount(),0l);
			Assert.assertEquals(refunds.getPlatformRefundAmount(),0l);
			//退款申请表
			System.out.println("defaccountId"+defaccountId);
			OrderChecker.checkManualRefundApply(nowOrder,firstRefundPrice,firstRemark,defaccountId,o.getStatus());
		}
		if(checkmongo){
			List<Map<String,Object>> list = MongoDBUtils.query("{'id':"+o.getId()+"}", MONGO_COLLECTION);
			Assert.assertNotNull(list);
			Assert.assertEquals(Integer.parseInt(list.get(0).get("status").toString()), OrderStatus.EXAM_FINISHED.intValue());//体检完成
		}
		waitto(21);//间隔20秒，否则提示您操作太频繁了，休息一下
		//手动退款>0 < 订单金额
		params1.clear();
		params1.add(new BasicNameValuePair("orderNum",o.getOrderNum() ));
		params1.add(new BasicNameValuePair("refundPrice",seconrdRefundPrice+""));
		params1.add(new BasicNameValuePair("remarks", secondRemark));

		result = httpclient.get(Order_ManuallyRefund, params1);
		body = result.getBody();
		log.info(body);
		Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);
		Assert.assertEquals(body,"{}");
		
		if(checkdb){
			Order nowNewOrder = OrderChecker.getOrderInfo(o.getId());
			Assert.assertEquals(nowNewOrder.getStatus(),OrderStatus.PART_BACK.intValue());//部分退款
			//退款金额大于0
			PayChecker.checkPartBackTrade(nowNewOrder,hospitalRecevieTradeAccountId,(int)seconrdRefundPrice);
			//退款申请表
			OrderChecker.checkManualRefundApply(nowNewOrder,seconrdRefundPrice,secondRemark,defaccountId,nowOrder.getStatus());
		}
		if(checkmongo){
			List<Map<String,Object>> list = MongoDBUtils.query("{'id':"+o.getId()+"}", MONGO_COLLECTION);
			Assert.assertNotNull(list);
			Assert.assertEquals(Integer.parseInt(list.get(0).get("status").toString()), OrderStatus.PART_BACK.intValue());//部分退款
		}
		
	}
	
	/**
	 * 第二个订单全额退款
	 * @throws Exception 
	 */
	@Test(description = "手动退款全额退款", groups = { "qa" },dependsOnMethods = "test_02_exportPlatToHis_manual")
	public void test_04_manuallyRefundTest() throws Exception {
		if(hisOrderList == null || hisOrderList.size() == 0){
			log.info("没有可用的订单，无法进行手动退款~~~~");
			return;
		}
		List<NameValuePair> params1 = new ArrayList<>();
		//手动退款0元
		Order o = secondRefundOrder;
		long firstRefundPrice = o.getOrderPrice().longValue();
		String firstRemark = "手动全额退款";
		params1.add(new BasicNameValuePair("orderNum",o.getOrderNum() ));
		params1.add(new BasicNameValuePair("refundPrice",firstRefundPrice+""));
		params1.add(new BasicNameValuePair("remarks", firstRemark));

		HttpResult result = httpclient.get(Order_ManuallyRefund, params1);
		String body = result.getBody();
		Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);
		Assert.assertEquals(body,"{}");
		
		
		if(checkdb){
			Order nowOrder = OrderChecker.getOrderInfo(o.getId());
			Assert.assertEquals(nowOrder.getStatus(),OrderStatus.REVOCATION.intValue());//订单撤销
			//退款金额为订单总额
			PayChecker.checkRevokeTrade(nowOrder, OrderStatus.ALREADY_BOOKED.intValue());
			//退款申请表
			OrderChecker.checkManualRefundApply(nowOrder,firstRefundPrice,firstRemark,defaccountId,o.getStatus());
		}
		if(checkmongo){
			List<Map<String,Object>> list = MongoDBUtils.query("{'id':"+o.getId()+"}", MONGO_COLLECTION);
			Assert.assertNotNull(list);
			Assert.assertEquals(Integer.parseInt(list.get(0).get("status").toString()), OrderStatus.REVOCATION.intValue());//订单撤销
		}
		
	}

}
