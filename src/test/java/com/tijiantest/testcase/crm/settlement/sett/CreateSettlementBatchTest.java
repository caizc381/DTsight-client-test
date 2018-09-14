package com.tijiantest.testcase.crm.settlement.sett;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.*;

import com.tijiantest.base.MyHttpClient;
import com.tijiantest.base.dbcheck.*;
import com.tijiantest.model.order.BatchOrderProcess;
import com.tijiantest.model.organization.OrganizationTypeEnum;
import com.tijiantest.model.payment.trade.PayConstants;
import com.tijiantest.model.settlement.*;
import com.tijiantest.util.CvsFileUtil;
import com.tijiantest.util.db.DBMapper;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.alibaba.fastjson.JSON;
import com.jayway.jsonpath.JsonPath;
import com.tijiantest.base.HttpResult;
import com.tijiantest.model.card.Card;
import com.tijiantest.model.company.CompanySettleModeEnum;
import com.tijiantest.model.order.Order;
import com.tijiantest.model.payment.trade.PayAmount;
import com.tijiantest.model.payment.trade.RefundAmount;
import com.tijiantest.testcase.crm.settlement.SettleBase;
import com.tijiantest.util.db.MongoDBUtils;
import com.tijiantest.util.db.SqlException;

/**
 * 执行结算
 * @author huifang
 *
 */
public class CreateSettlementBatchTest  extends SettleBase{

	private int hospitalId = defSettHospitalId;

	@Test(description = "生成可结算的数据",groups = {"qa"},dataProvider = "crm_prepareSettData")
	public void test_00_prepareSettDate(String ...args){
		//1.创建可以结算的订单(平台单位）
		MyHttpClient platClient = new MyHttpClient();
		try {
			List<Order> platOrderList = OrderChecker.plat_crm_createOrder(platClient,defPlatUsername,defPlatPasswd,defSettHospitalId,4);
			OrderChecker.exportToLightHis(httpclient,platOrderList,defSettHospitalId,defSettAccountId);
		} catch (SqlException e) {
			e.printStackTrace();
		}
		//2.创建可以结算的订单和卡(非平台单位）
		String settCompanyStr = args[1];
		int settCompanyId = Integer.parseInt(settCompanyStr);
		int settMealId = 0;
		List<Integer> accountList = new ArrayList<>();
		List<Order> crmOrderList = OrderChecker.crm_createOrder(httpclient,defSettAccountId,defSettHospitalId,settCompanyId,4);
		OrderChecker.exportToLightHis(httpclient,crmOrderList,defSettHospitalId,defSettAccountId);
		settMealId = crmOrderList.get(0).getOrderMealSnapshot().getMealSnapshot().getId();
		for(Order o : crmOrderList)
			accountList.add(o.getAccount().getId());
		for(Integer account : accountList)
			try {
				CardChecker.createCard(httpclient,account,settCompanyId,"结算卡",1000,settMealId,defSettHospitalId,defSettAccountId);
			} catch (SqlException e) {
				e.printStackTrace();
			}

	}

	@Test(description = "执行结算" ,groups={"qa","crm_create_set"},dataProvider = "crm_create_sett",dependsOnMethods = "test_00_prepareSettDate")
//		@Test(description = "执行结算" ,groups={"qa","crm_create_set"})
	public void test_01_createSettlementBatch(String ...args) throws SqlException, ParseException{
		String newCompanyStr = args[1];
		String organizationTypeStr = args[2];
		int newCompanyId = Integer.parseInt(newCompanyStr);
////		//判断是平台单位还是普通单位,普通单位=1，平台单位=2
		int organizationType = Integer.parseInt(organizationTypeStr);
//			int newCompanyId = 4400008;
//			int organizationType = 1;
		String placeOrderEndTime = "2019-12-12 23:59:59";//
		//STEP1 撤销指定单位的批次
		//提取待确认的结算批次列表(每天健康/http测试单位/散客现场)
		List<TradeSettlementBatch> needConfirmLists = SettleChecker.getTradeSettlementBatch(defSettHospitalId,newCompanyId,null,null,SettlementHospitalConfirmEnum.SETTLEMENT_TO_BE_CONFIRM.getCode(),null,false,null);
		if(needConfirmLists.size()>0){
			for(TradeSettlementBatch b : needConfirmLists){
				String sn = b.getSn();
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("sn", sn));
				HttpResult response = httpclient.get(RevokeSettlementBatch, params);
				log.info(response.getBody());
				Assert.assertEquals(response.getCode(), HttpStatus.SC_OK);
				boolean retFlag = Boolean.parseBoolean(JsonPath.read(response.getBody(), "$.result").toString());
				Assert.assertTrue(retFlag);
			}
		}

		//STEP2 提取单位的未结算卡/未结算订单/未结算预付款/未结算退款
		List<UnsettlementCard> cardList = SettleChecker.getNotSettlementCard(hospitalId, settle_time, newCompanyId,-1);
		List<UnsettlementOrder> orderList = SettleChecker.getNotSettlementOrder(hospitalId, settle_time, newCompanyId);
		List<UnsettlementPaymentOrder> paymentList = SettleChecker.getNotSettlementPaymentOrder(hospitalId, settle_time, newCompanyId);
		List<TradePrepaymentRecord> prepayList = SettleChecker.getNotSettlementPrepaymentRecords(hospitalId, settle_time, newCompanyId,placeOrderEndTime);
		List<Order> refundList = SettleChecker.getNotSettlementRefundOrder(hospitalId, settle_time, newCompanyId);
		List<PaymentOrder> refundPaymentLists = SettleChecker.getNotSettlementRefundPaymentOrder(hospitalId,settle_time,newCompanyId);
		int refundSize = 0;//退款数量
		int allItems = 0; //统计所有个记录（订单/卡/退款/特殊退款)
		//创建参数
		SettlementRequest request = new SettlementRequest();
		if(cardList != null && cardList.size()>0){
			List<Integer> cardIds = new ArrayList<Integer>();
			for(UnsettlementCard card :cardList)
				cardIds.add(card.getId());
			request.setCardIds(cardIds);
			allItems += cardIds.size();
			}else
				request.setCardIds(new ArrayList<Integer>());
		
		if(orderList != null && orderList.size()>0){
			List<String> orderNums = new ArrayList<String>();
			for(UnsettlementOrder order :orderList)
				orderNums.add(order.getOrderNum());
			request.setOrderNums(orderNums);
			allItems += orderNums.size();
			}else request.setOrderNums(new ArrayList<String>());


		if(paymentList != null && paymentList.size()>0){
			List<String> payOrderNums = new ArrayList<String>();
			for(UnsettlementPaymentOrder paymentOrder :paymentList)
				payOrderNums.add(paymentOrder.getOrderNum());
			request.setPayOrderNums(payOrderNums);
			allItems += payOrderNums.size();
		}else request.setPayOrderNums(new ArrayList<String>());

		//叠加体检订单退款数量
		if(refundList !=null && refundList.size()>0){
			refundSize += refundList.size();
			allItems += refundList.size();
		}
		//叠加收款订单退款列表数量
		if(refundPaymentLists !=null && refundPaymentLists.size()>0){
			refundSize += refundPaymentLists.size();
			allItems += refundPaymentLists.size();
		}
		//叠加特殊退款数量
		if(prepayList !=null && prepayList.size()>0){
			refundSize += prepayList.size();
			allItems += prepayList.size();
		}


			request.setCompanyIds(Arrays.asList(newCompanyId));
		request.setHospitalId(hospitalId);
		request.setOperatorId(defSettAccountId);
		request.setRefundSize(refundSize);
		request.setPlaceOrderEndTime(placeOrderEndTime);

		String beforeDate = simplehms.format(DBMapper.query("select now()").get(0).get("now()"));
		waitto(1);
		HttpResult response = httpclient.post(CreateSettlementBatch, JSON.toJSONString(request));
		System.out.println(response.getBody());
		Assert.assertEquals(response.getCode(), HttpStatus.SC_OK);
		boolean retFlag = Boolean.parseBoolean(JsonPath.read(response.getBody(), "$.result").toString());
		Assert.assertTrue(retFlag);
		
		//共用1个批次号
		if(checkdb){
			waitto(5);
			//数据库中各种金额累计，主要用于批次表，账单表计算比较
			long dbCompanyPayAmount = 0l; //单位支付
			long dbCompanyRefundAmount = 0l;//单位退款
			long dbOnlinePayAmount = 0l; //线上支付
			long dbOnlineRefundAmount = 0l;//线上退款
			long dbPlatFormPayAmount = 0l;//平台支付
			long dbPlatFormRefundAmount = 0l;//平台退款
			long dbPlatFormPrepaymentAmount = 0l;//平台特殊退款
			long dbHospitalCouponAmount = 0l; //医院优惠券支付
			long dbHospitalCouponRefundAmount = 0l;//医院优惠券退款
			long dbPlatformCouponAmount = 0l;//平台优惠券支付
			long dbPlatformCouponRefundAmount = 0l;//平台优惠券退款
			long dbChannelCouponAmount = 0l;//渠道优惠券支付
			long dbChannelCouponRefundAmount = 0l;//渠道优惠券退款
			long dbHospitalOnlinePayAmount = 0l;//医院线上支付
			long dbHospitalOnlineRefundAmount = 0l;//医院线上退款
			long dbPlatformOnlinePayAmount = 0l;//平台线上支付
			long dbPlatformOnlineRefundAmount = 0l;//平台线上退款
			long dbChannelOnlinePayAmount = 0l;//渠道线上支付
			long dbChannelOnlineRefundAmount = 0l;//渠道线上退款
			long dbOfflinePayAmount = 0l;//线下支付
			long dbChannelCompanyPayAmount = 0l;//渠道单位支付
			long dbChannelCompanyRefundAmount = 0l;//渠道单位退款
			long dbChannelCardPayAmount = 0l;//渠道体检卡支付
			long dbChannelCardRefundAmount = 0l;//渠道体检卡退款

			String settBatch = null;//结算批次号
			String companyBillSn = null;//单位医院账单流水号
			String platBillSn = null; //单位平台账单流水号
			
			//1.体检订单
			for(UnsettlementOrder setOrder : orderList){
				String order_num = setOrder.getOrderNum();
				System.out.println("开始比较结算体检订单.."+order_num);
				//1.1 交易结算订单表
				List<TradeSettlementOrder> dbOrderList = SettleChecker.getTradeSettleOrderByColumn("ref_order_num",order_num
						,"hospital_settlement_status",SettlementHospitalConfirmEnum.SETTLEMENT_TO_BE_CONFIRM.getCode()+"");
				Assert.assertTrue(dbOrderList.size()>=1);//待确认可能有多条（渠道结算）取最新的一条查看->为医院结算
				TradeSettlementOrder orderMap = dbOrderList.get(0);
				Assert.assertNotNull(orderMap.getSn());
				Assert.assertEquals(orderMap.getOrganizationId().intValue(),hospitalId);
				Assert.assertEquals(orderMap.getCompanyId().intValue(),newCompanyId);
				settBatch  = orderMap.getBatchSn();
				Assert.assertNotNull(settBatch);
				Assert.assertNull(orderMap.getHospitalPlatformSn());
				Assert.assertNull(orderMap.getHospitalCompanySn());

				PayAmount payAmount = PayChecker.getPayAmountByOrderNum(order_num,PayConstants.OrderType.MytijianOrder);
				Assert.assertEquals(orderMap.getOnlinePayAmount().intValue(),payAmount.getOnlinePayAmount());//线上支付
				Assert.assertEquals(orderMap.getHospitalCoupAmount().longValue(),payAmount.getHospitalCouponAmount());//医院优惠券
				Assert.assertEquals(orderMap.getPlatformCoupAmount().longValue(),payAmount.getPlatformCouponAmount());//平台优惠券
				Assert.assertEquals(orderMap.getChannelCoupAmount().longValue(),payAmount.getChannelCouponAmount());//渠道优惠券
				Assert.assertEquals(orderMap.getPlatformPayAmount().intValue(),payAmount.getPlatformPayAmount());//平台支付
				if(payAmount.getChannelCardPayAmount() != 0)//若渠道体检卡支付不为空则显示渠道体检卡，否则显示医院体检卡
					Assert.assertEquals(orderMap.getCardPayAmount().intValue(),payAmount.getChannelCardPayAmount());
				else
					Assert.assertEquals(orderMap.getCardPayAmount().intValue(),payAmount.getCardPayAmount());//体检卡支付(渠道体检卡支付 或者 医院体检卡）
				Assert.assertEquals(orderMap.getOfflinePayAmount().intValue(),payAmount.getOfflinePayAmount());//线下支付
				Assert.assertEquals(orderMap.getPcardPayAmount().intValue(),payAmount.getPcardPayAmount() + payAmount.getChannelCompanyPayAmount());//体检中心母卡/渠道母卡支付 2者选1
				Assert.assertEquals(orderMap.getHospitalSettlementStatus().intValue(),SettlementHospitalConfirmEnum.SETTLEMENT_TO_BE_CONFIRM.getCode().intValue());
				Assert.assertEquals(orderMap.getIsDeleted().intValue(),0);
				//1.2 订单结算关系表
				List<ExamOrderSettlementDO> examOrderS = SettleChecker.getExamOrderSettleByColumn("order_num",order_num);
				Assert.assertEquals(examOrderS.size(),1);//订单结算关系表只有1条数据
				ExamOrderSettlementDO examOrder = examOrderS.get(0);
				Assert.assertEquals(examOrder.getHospitalSettlementStatus(),SettlementHospitalConfirmEnum.SETTLEMENT_TO_BE_CONFIRM.getCode());

				if(checkmongo){
					List<Map<String,Object>> list = MongoDBUtils.query("{'orderNum':'"+order_num+"'}", MONGO_COLLECTION);
					Assert.assertEquals(1, list.size());
					Assert.assertTrue(Integer.parseInt(list.get(0).get("settleSign").toString()) == SettlementHospitalConfirmEnum.SETTLEMENT_TO_BE_CONFIRM.getCode());
					Assert.assertEquals(list.get(0).get("settleBatch").toString(),settBatch);
				}
				
				//累加计算支付金额(按照tb_trade_settlement_batch数据库表支付字段顺序)
				dbCompanyPayAmount += payAmount.getPcardPayAmount();
				dbOnlinePayAmount += payAmount.getOnlinePayAmount();
				dbPlatFormPayAmount += payAmount.getPlatformPayAmount();
				dbHospitalCouponAmount += payAmount.getHospitalCouponAmount();
				dbPlatformCouponAmount += payAmount.getPlatformCouponAmount();
				dbChannelCouponAmount += payAmount.getChannelCouponAmount();
				dbHospitalOnlinePayAmount += payAmount.getHospitalOnlinePayAmount();
				dbPlatformOnlinePayAmount += payAmount.getPlatformOnlinePayAmount();
				dbChannelOnlinePayAmount += payAmount.getChannelOnlinePayAmount();
				dbOfflinePayAmount += payAmount.getOfflinePayAmount();
				dbChannelCompanyPayAmount += payAmount.getChannelCompanyPayAmount();
				dbChannelCardPayAmount += payAmount.getChannelCardPayAmount();

			}
			//2.收款订单
			for(UnsettlementPaymentOrder setpay : paymentList){
				String order_num = setpay.getOrderNum();
				System.out.println("开始比较结算收款订单.."+order_num);
				//1.1 交易结算订单表
				List<TradeSettlementPaymentOrder> dbOrderList = SettleChecker.getTradeSettlePaymentOrderByColumn("ref_order_num",order_num
						,"hospital_settlement_status",SettlementHospitalConfirmEnum.SETTLEMENT_TO_BE_CONFIRM.getCode()+"");
				Assert.assertEquals(dbOrderList.size(),1);//待确认只有1条
				TradeSettlementPaymentOrder orderMap = dbOrderList.get(0);
				Assert.assertNotNull(orderMap.getSn());
				Assert.assertEquals(orderMap.getOrganizationId().intValue(),hospitalId);
				Assert.assertEquals(orderMap.getCompanyId().intValue(),newCompanyId);
				settBatch  = orderMap.getBatchSn();
				Assert.assertNotNull(settBatch);
				Assert.assertNull(orderMap.getHospitalPlatformSn());
				PayAmount payAmount = PayChecker.getPayAmountByOrderNum(order_num, PayConstants.OrderType.PaymentOrder);
				Assert.assertEquals(orderMap.getOnlinePayAmount().intValue(),payAmount.getOnlinePayAmount());
				Assert.assertEquals(orderMap.getHospitalSettlementStatus().intValue(),SettlementHospitalConfirmEnum.SETTLEMENT_TO_BE_CONFIRM.getCode().intValue());
				Assert.assertEquals(orderMap.getIsDeleted().intValue(),0);
				//1.2 收款订单结算关系表
				List<PaymentOrderSettlementDO> examOrderS = SettleChecker.getPaymentOrderSettleByColumn("order_num",order_num);
				Assert.assertEquals(examOrderS.size(),1);//订单结算关系表只有1条数据
				PaymentOrderSettlementDO examOrder = examOrderS.get(0);
				Assert.assertEquals(examOrder.getHospitalSettlementStatus(),SettlementHospitalConfirmEnum.SETTLEMENT_TO_BE_CONFIRM.getCode());


				//累加计算支付金额	(按照tb_trade_settlement_batch数据库表支付字段顺序)
				dbCompanyPayAmount += payAmount.getPcardPayAmount();
				dbOnlinePayAmount += payAmount.getOnlinePayAmount();
				dbPlatFormPayAmount += payAmount.getPlatformPayAmount();
				dbHospitalCouponAmount += payAmount.getHospitalCouponAmount();
				dbPlatformCouponAmount += payAmount.getPlatformCouponAmount();
				dbChannelCouponAmount += payAmount.getChannelCouponAmount();
				dbHospitalOnlinePayAmount += payAmount.getHospitalOnlinePayAmount();
				dbPlatformOnlinePayAmount += payAmount.getPlatformOnlinePayAmount();
				dbChannelOnlinePayAmount += payAmount.getChannelOnlinePayAmount();
				dbOfflinePayAmount += payAmount.getOfflinePayAmount();
				dbChannelCompanyPayAmount += payAmount.getChannelCompanyPayAmount();
				dbChannelCardPayAmount += payAmount.getChannelCardPayAmount();
			}

			//3.卡
			if(organizationType == 1){//医院单位
				//获取卡的结算方式，按项目还是按人数
				int settle_mode = CompanyChecker.getHospitalCompanyById(newCompanyId).getSettlementMode();
				for(UnsettlementCard setCard : cardList){
						int card_id = setCard.getId();
						System.out.println("开始比较结算卡.."+card_id);
						//2.1 交易结算卡表
						List<TradeSettlementCard> dbCardList = SettleChecker.getTradeSettleCardByColumn("ref_card_id",card_id+""
								,"hospital_settlement_status",SettlementHospitalConfirmEnum.SETTLEMENT_TO_BE_CONFIRM.getCode()+"");
						Assert.assertEquals(dbCardList.size(),1);//待确认只有1条
						TradeSettlementCard cardMap = dbCardList.get(0);
						Assert.assertNotNull(cardMap.getSn());
						Assert.assertEquals(cardMap.getOrganizationId().intValue(),hospitalId);
						Assert.assertEquals(cardMap.getCompanyId().intValue(),newCompanyId);
						if(settBatch != null)
							Assert.assertEquals(cardMap.getBatchSn(),settBatch);
						else
							settBatch = cardMap.getBatchSn();

						Assert.assertNull(cardMap.getHospitalPlatformSn());
						Assert.assertNull(cardMap.getHospitalCompanySn());
						Assert.assertEquals(cardMap.getHospitalSettlementStatus().intValue(),SettlementHospitalConfirmEnum.SETTLEMENT_TO_BE_CONFIRM.getCode().intValue());
						Assert.assertEquals(cardMap.getSettlementMode().intValue(),settle_mode);
						Assert.assertEquals(cardMap.getIsDeleted().intValue(),0);//数据有效
						
						//2.2 卡表(tb_card)
						Card card = CardChecker.getCardInfo(card_id);
						if(settle_mode == CompanySettleModeEnum.USE_PROJECT.getCode()){//按项目
							Assert.assertEquals(card.getFreezeBalance(),setCard.getBalance()); //冻结金额
							Assert.assertEquals(card.getBalance().longValue(), 0l);//卡剩余金额为0
							Assert.assertEquals(cardMap.getSettlementAmount().longValue(),
									card.getCapacity().longValue() - card.getRecoverableBalance().longValue() - setCard.getBalance().longValue());//面值-回收金额-之前余额
							Assert.assertEquals(cardMap.getRecycleAmount().longValue()
									,setCard.getBalance().longValue());//回收余额 = 余额

						}else{//按人数
							Assert.assertEquals(card.getFreezeBalance().longValue(),0l); //不冻结金额
							Assert.assertEquals(card.getBalance(), setCard.getBalance());//卡剩余金额保持不变
							Assert.assertEquals(cardMap.getSettlementAmount().longValue(),card.getCapacity().longValue() - card.getRecoverableBalance().longValue()); //结算金额=tb_card.capacity -tb_card.recoverableBalace
							Assert.assertEquals(cardMap.getRecycleAmount().longValue(),0l);//不回收
							}
						Assert.assertEquals(card.getHospitalSettlementStatus(),SettlementHospitalConfirmEnum.SETTLEMENT_TO_BE_CONFIRM.getCode()); //卡待确认状态
						Assert.assertEquals(card.getSettlementBatchSn(),settBatch); //卡中结算批次号

						//累加计算支付金额
						dbCompanyPayAmount += cardMap.getSettlementAmount();
				}
			}
			if(organizationType == 2)//渠道单位,没有结算的卡
				Assert.assertEquals(cardList.size(),0);
			
			//3.体检订单退款
			for(Order ref : refundList){
				System.out.println("开始比较结算退款，订单编号.."+ref.getOrderNum());
				//3.1 交易结算退款表
				List<TradeSettlementRefund> dbRefundList = SettleChecker.getTradeSettleRefundByColumn("ref_order_num",ref.getOrderNum()
						,"hospital_settlement_status",SettlementHospitalConfirmEnum.SETTLEMENT_TO_BE_CONFIRM.getCode()+"");
				Assert.assertTrue(dbRefundList.size()>=1);//退款可能结算2次（最新1条是医院)
				TradeSettlementRefund refundMap = dbRefundList.get(0);
				Assert.assertNotNull(refundMap.getSn());
				Assert.assertEquals(refundMap.getRefOrderNum(),ref.getOrderNum());
				Assert.assertEquals(refundMap.getOrganizationId().intValue(),hospitalId);
				Assert.assertEquals(refundMap.getCompanyId().intValue(),newCompanyId);
				if(settBatch != null)
					Assert.assertEquals(refundMap.getBatchSn(),settBatch);
				else
					settBatch = refundMap.getBatchSn();
				Assert.assertNull(refundMap.getHospitalPlatformSn());
				Assert.assertNull(refundMap.getHospitalCompanySn());
				RefundAmount refundAmount = PayChecker.getRefundAmountByOrderNum(ref.getOrderNum(),PayConstants.OrderType.MytijianOrder);
				Assert.assertEquals(refundMap.getPcardRefundAmount().intValue(),refundAmount.getPcardRefundAmount() + refundAmount.getChannelCompanyRefundAmount()); //体检中心母卡/渠道母卡2种种只能存在1个
				Assert.assertEquals(refundMap.getOnlineRefundAmount().intValue(),refundAmount.getOnlineRefundAmount());
				Assert.assertEquals(refundMap.getHospitalCoupRefundAmount().intValue(),refundAmount.getHospitalCouponRefundAmount());
				Assert.assertEquals(refundMap.getPlatformCoupRefundAmount().intValue(),refundAmount.getPlatformCouponRefundAmount());
				Assert.assertEquals(refundMap.getChannelCoupRefundAmount().intValue(),refundAmount.getChannelCouponRefundAmount());
				Assert.assertEquals(refundMap.getPlatformRefundAmount().intValue(),refundAmount.getPlatformRefundAmount());
				if(refundAmount.getChannelCardRefundAmount() != 0)//如果渠道体检卡退款有值，则该订单体检卡退款=渠道体检卡退款
					Assert.assertEquals(refundMap.getCardRefundAmount().intValue(),refundAmount.getChannelCardRefundAmount());
				else//如果渠道体检卡退款没有值，则该订单体检卡退款=医院体检卡退款
					Assert.assertEquals(refundMap.getCardRefundAmount().intValue(),refundAmount.getCardRefundAmount());
				Assert.assertEquals(refundMap.getOfflineRefundAmount().intValue(),0);//线下退款统一为0
				Assert.assertEquals(refundMap.getHospitalSettlementStatus().intValue(),SettlementHospitalConfirmEnum.SETTLEMENT_TO_BE_CONFIRM.getCode().intValue());
				Assert.assertEquals(refundMap.getIsDeleted().intValue(),0);
				//3.2 订单结算关系表
				List<ExamOrderSettlementDO> examOrderS = SettleChecker.getExamOrderSettleByColumn("order_num",ref.getOrderNum());
				Assert.assertEquals(examOrderS.size(),1);//订单结算关系表只有1条数据
				ExamOrderSettlementDO examOrder = examOrderS.get(0);
				Assert.assertEquals(examOrder.getRefundSettlement(),ExamOrderRefundSettleEnum.REFUND_OK.getCode()); //生成批次将退款结算掉
				
				//累加计算退款金额(按照tb_tade_settlement_batch的退款字段顺序)
				dbCompanyRefundAmount += refundAmount.getPcardRefundAmount();
				dbOnlineRefundAmount += refundAmount.getOnlineRefundAmount();
				dbPlatFormRefundAmount += refundAmount.getPlatformRefundAmount();
				dbHospitalCouponRefundAmount += refundAmount.getHospitalCouponRefundAmount();
				dbPlatformCouponRefundAmount += refundAmount.getPlatformCouponRefundAmount();
				dbChannelCouponRefundAmount += refundAmount.getChannelCouponRefundAmount();
				dbHospitalOnlineRefundAmount += refundAmount.getHospitalOnlineRefundAmount();
				dbPlatformOnlineRefundAmount += refundAmount.getPlatformOnlineRefundAmount();
				dbChannelOnlineRefundAmount += refundAmount.getChannelOnlineRefundAmount();
				dbChannelCompanyRefundAmount += refundAmount.getChannelCompanyRefundAmount();
				dbChannelCardRefundAmount += refundAmount.getChannelCardRefundAmount();

			}

			//4.收款订单退款
			for(PaymentOrder ref : refundPaymentLists){
				System.out.println("开始比较结算收款订单退款，订单编号.."+ref.getOrderNum());
				//3.1 交易结算退款表
				List<TradeSettlementRefund> dbRefundList = SettleChecker.getTradeSettleRefundByColumn("ref_order_num",ref.getOrderNum()
						,"hospital_settlement_status",SettlementHospitalConfirmEnum.SETTLEMENT_TO_BE_CONFIRM.getCode()+"");
				Assert.assertEquals(dbRefundList.size(),1);
				TradeSettlementRefund refundMap = dbRefundList.get(0);
				Assert.assertNotNull(refundMap.getSn());
				Assert.assertEquals(refundMap.getRefOrderNum(),ref.getOrderNum());
				Assert.assertEquals(refundMap.getOrganizationId().intValue(),hospitalId);
				Assert.assertEquals(refundMap.getCompanyId().intValue(),newCompanyId);
				if(settBatch != null)
					Assert.assertEquals(refundMap.getBatchSn(),settBatch);
				else
					settBatch = refundMap.getBatchSn();
				Assert.assertNull(refundMap.getHospitalCompanySn());
				Assert.assertNull(refundMap.getHospitalPlatformSn());
				RefundAmount refundAmount = PayChecker.getRefundAmountByOrderNum(ref.getOrderNum(),PayConstants.OrderType.PaymentOrder);
				Assert.assertEquals(refundMap.getPcardRefundAmount().intValue(),refundAmount.getPcardRefundAmount());
				Assert.assertEquals(refundMap.getOnlineRefundAmount().intValue(),refundAmount.getOnlineRefundAmount());
				Assert.assertEquals(refundMap.getPlatformRefundAmount().intValue(),refundAmount.getPlatformRefundAmount());
				Assert.assertEquals(refundMap.getCardRefundAmount().intValue(),refundAmount.getCardRefundAmount());//收款订单无体检卡退款的
				Assert.assertEquals(refundMap.getOfflineRefundAmount().intValue(),0);//线下退款统一为0
				Assert.assertEquals(refundMap.getHospitalSettlementStatus().intValue(),SettlementHospitalConfirmEnum.SETTLEMENT_TO_BE_CONFIRM.getCode().intValue());
				Assert.assertEquals(refundMap.getIsDeleted().intValue(),0);
				//3.2 收款订单结算关系表
				List<PaymentOrderSettlementDO> examOrderS = SettleChecker.getPaymentOrderSettleByColumn("order_num",ref.getOrderNum());
				Assert.assertEquals(examOrderS.size(),1);//订单结算关系表只有1条数据
				PaymentOrderSettlementDO examOrder = examOrderS.get(0);
				Assert.assertEquals(examOrder.getRefundSettlement(),ExamOrderRefundSettleEnum.REFUND_OK.getCode()); //生成批次将退款结算掉

				//累加计算退款金额(按照tb_tade_settlement_batch的退款字段顺序)
				dbCompanyRefundAmount += refundAmount.getPcardRefundAmount();
				dbOnlineRefundAmount += refundAmount.getOnlineRefundAmount();
				dbPlatFormRefundAmount += refundAmount.getPlatformRefundAmount();
				dbHospitalCouponRefundAmount += refundAmount.getHospitalCouponRefundAmount();
				dbPlatformCouponRefundAmount += refundAmount.getPlatformCouponRefundAmount();
				dbChannelCouponRefundAmount += refundAmount.getChannelCouponRefundAmount();
				dbHospitalOnlineRefundAmount += refundAmount.getHospitalOnlineRefundAmount();
				dbPlatformOnlineRefundAmount += refundAmount.getPlatformOnlineRefundAmount();
				dbChannelOnlineRefundAmount += refundAmount.getChannelOnlineRefundAmount();
				dbChannelCompanyRefundAmount += refundAmount.getChannelCompanyRefundAmount();
				dbChannelCardRefundAmount += refundAmount.getChannelCardRefundAmount();


			}
			//5.特殊退款
			for(TradePrepaymentRecord payRecord : prepayList){
				int payId = payRecord.getId();
				System.out.println("开始比较特殊退款.."+payId);
				List<TradePrepaymentRecord> dbPrepayList = SettleChecker.getTradePrementRecordByColumn("id",payId+""								
						,"status",SettlementHospitalConfirmEnum.SETTLEMENT_TO_BE_CONFIRM.getCode()+"");
				Assert.assertEquals(dbPrepayList.size(),1);
				TradePrepaymentRecord record = dbPrepayList.get(0);
				Assert.assertEquals(record.getOrganizationId().intValue(),hospitalId);
				Assert.assertEquals(record.getCompanyId().intValue(),newCompanyId);
				if(settBatch != null)
					Assert.assertEquals(record.getBatchSn(),settBatch);
				else
					settBatch = record.getBatchSn();
				
				Assert.assertEquals(record.getStatus(),SettlementHospitalConfirmEnum.SETTLEMENT_TO_BE_CONFIRM.getCode());//医院待确认
				
				//累加计算特殊退款(平台特殊退款)
				dbPlatFormPrepaymentAmount += record.getAmount();
			}
			log.info("单位部分------------该批次医院单位支付 "+dbCompanyPayAmount+";单位退款 "+dbCompanyRefundAmount
					+";线下付款 "+dbOfflinePayAmount+";渠道单位支付 "+dbChannelCompanyPayAmount+";渠道单位退款 "+dbChannelCompanyRefundAmount);
			log.info("平台支付/退款相关----------该批次的平台支付"+dbPlatFormPayAmount+"平台退款 "+dbPlatFormRefundAmount+";平台特殊退款 "+dbPlatFormPrepaymentAmount);
			log.info("线上支付/退款相关----------线上支付 "+dbOnlinePayAmount+";线上退款 "+dbOnlineRefundAmount+"医院线上支付 "+dbHospitalOnlinePayAmount+";医院线上退款 "+dbHospitalOnlineRefundAmount+";平台线上支付 "+dbPlatformOnlinePayAmount+";平台线上退款"+dbPlatformOnlineRefundAmount+";渠道线上支付"+dbChannelOnlinePayAmount+";渠道线上退款 "+dbChannelOnlineRefundAmount);
			log.info("优惠券支付/退款相关-----------该批次的平台优惠券支付 "+dbPlatformCouponAmount+";平台优惠券退款 "+dbPlatformCouponRefundAmount+";渠道优惠券支付 "+dbChannelCouponAmount+";渠道优惠券退款 "+dbChannelCouponRefundAmount+";医院优惠券支付 "+dbHospitalCouponAmount+";医院优惠券退款 "+dbHospitalCouponRefundAmount);
			//6.结算批次
			System.out.println("开始比较结算批次数据.."+settBatch);
			List<TradeSettlementBatch> batchList = SettleChecker.getTradeSettlementBatchByColumn("sn",settBatch);
			Assert.assertEquals(batchList.size(),1); //对单个单位执行结算，1个批次号对应1个单位
			TradeSettlementBatch batchMap = batchList.get(0);
			Assert.assertEquals(batchMap.getHospitalId().intValue(),hospitalId);
			Assert.assertEquals(batchMap.getCompanyId().intValue(),newCompanyId);
			Assert.assertNull(batchMap.getPaymentRecordSn());//收款记录流水号为空
			Assert.assertEquals(batchMap.getHospitalSettlementStatus(),SettlementHospitalConfirmEnum.SETTLEMENT_TO_BE_CONFIRM.getCode());//医院待确认
			Assert.assertNull(batchMap.getChannelId());//渠道ID为空
			Assert.assertNull(batchMap.getChannelCompanyId());//渠道单位ID为空
			Assert.assertEquals(batchMap.getSettlementViewType().intValue(),0);//医院视角
			Assert.assertEquals(batchMap.getOperatorId().intValue(),defSettAccountId);
			Assert.assertEquals(batchMap.getIsdeleted().intValue(),0); //未删除
			//////////各种金额比较(单位支付|单位退款|线上支付|线上退款|平台支付|平台退款|平台预付款)
			Assert.assertEquals(batchMap.getCompanyPayAmount().longValue(),dbCompanyPayAmount,"比较批次表单位支付异常,批次号"+batchMap.getSn());
			Assert.assertEquals(batchMap.getCompanyRefundAmount().longValue(),dbCompanyRefundAmount,"比较批次表单位退款异常,批次号"+batchMap.getSn());
			Assert.assertEquals(batchMap.getOnlinePayAmount().longValue(),dbOnlinePayAmount,"比较批次表线上支付异常,批次号"+batchMap.getSn());
			Assert.assertEquals(batchMap.getOnlineRefundAmount().longValue(),dbOnlineRefundAmount,"比较批次表线上退款异常,批次号"+batchMap.getSn());
			Assert.assertEquals(batchMap.getPlatformPayAmount().longValue(),dbPlatFormPayAmount,"比较批次表平台支付出错,批次号"+batchMap.getSn());
			Assert.assertEquals(batchMap.getPlatformRefundAmount().longValue(),dbPlatFormRefundAmount,"比较批次表平台退款出错,批次号"+batchMap.getSn());
			Assert.assertEquals(batchMap.getPlatformPrepaymentAmount().longValue(),dbPlatFormPrepaymentAmount,"比较批次表平台特殊退款出错,批次号"+batchMap.getSn());
			Assert.assertEquals(batchMap.getHospitalCouponAmount().longValue(),dbHospitalCouponAmount,"比较批次表医院优惠券支付,批次号"+batchMap.getSn());
			Assert.assertEquals(batchMap.getHospitalCouponRefundAmount().longValue(),dbHospitalCouponRefundAmount,"比较批次表医院优惠券退款,批次号"+batchMap.getSn());
			Assert.assertEquals(batchMap.getPlatformCouponAmount().longValue(),dbPlatformCouponAmount,"比较批次表平台优惠券支付,批次号"+batchMap.getSn());
			Assert.assertEquals(batchMap.getPlatformCouponRefundAmount().longValue(),dbPlatformCouponRefundAmount,"比较批次表平台优惠券退款,批次号"+batchMap.getSn());
			Assert.assertEquals(batchMap.getChannelCouponAmount().longValue(),dbChannelCouponAmount,"比较批次表渠道优惠券支付,批次号"+batchMap.getSn());
			Assert.assertEquals(batchMap.getChannelCouponRefundAmount().longValue(),dbChannelCouponRefundAmount,"比较批次表渠道优惠券退款,批次号"+batchMap.getSn());
			Assert.assertEquals(batchMap.getHospitalOnlinePayAmount().longValue(),dbHospitalOnlinePayAmount,"比较批次表医院线上支付,批次号"+batchMap.getSn());
			Assert.assertEquals(batchMap.getHospitalOnlineRefundAmount().longValue(),dbHospitalOnlineRefundAmount,"比较批次表医院线上退款,批次号"+batchMap.getSn());
			Assert.assertEquals(batchMap.getPlatformOnlinePayAmount().longValue(),dbPlatformOnlinePayAmount,"比较批次表平台线上支付,批次号"+batchMap.getSn());
			Assert.assertEquals(batchMap.getPlatformOnlineRefundAmount().longValue(),dbPlatformOnlineRefundAmount,"比较批次表平台线上退款,批次号"+batchMap.getSn());
			Assert.assertEquals(batchMap.getChannelOnlinePayAmount().longValue(),dbChannelOnlinePayAmount,"比较批次表渠道线上支付,批次号"+batchMap.getSn());
			Assert.assertEquals(batchMap.getChannelOnlineRefundAmount().longValue(),dbChannelOnlineRefundAmount,"比较批次表渠道线上退款,批次号"+batchMap.getSn());
			Assert.assertEquals(batchMap.getOfflinePayAmount().longValue(),dbOfflinePayAmount,"比较批次表线下支付,批次号"+batchMap.getSn());
			Assert.assertEquals(batchMap.getChannelCompanyPayAmount().longValue(),dbChannelCompanyPayAmount,"比较批次表渠道单位支付,批次号"+batchMap.getSn());
			Assert.assertEquals(batchMap.getChannelCompanyRefundAmount().longValue(),dbChannelCompanyRefundAmount,"比较批次表渠道单位退款,批次号"+batchMap.getSn());
			Assert.assertEquals(batchMap.getChannelCardPayAmount().longValue(),dbChannelCardPayAmount,"比较批次表渠道体检卡支付,批次号"+batchMap.getSn());
			Assert.assertEquals(batchMap.getChannelCardRefundAmount().longValue(),dbChannelCardRefundAmount,"比较批次表渠道体检卡退款,批次号"+batchMap.getSn());

			//7.医院单位账单 type=0
			System.out.println("开始比较结算医院单位账单..批次号"+settBatch);
			List<TradeHospitalCompanyBill> companyBillList = SettleChecker.getTradeHospitalCompanyBillByColumn(null,false,null, false, "batch_sn",settBatch,"type","0");
			if(dbCompanyPayAmount==0 && dbCompanyRefundAmount ==0)//单位支付未0，单位退款为0
				Assert.assertEquals(companyBillList.size(),0);
			else {
				Assert.assertEquals(companyBillList.size(),1);//有且1条单位账单
				TradeHospitalCompanyBill companyBill = companyBillList.get(0);
				Assert.assertNotNull(companyBill.getSn());
				Assert.assertEquals(companyBill.getHospitalId().intValue(),hospitalId);
				Assert.assertEquals(companyBill.getCompanyId().intValue(),newCompanyId);
				Assert.assertEquals(companyBill.getCompanyPayAmount().longValue(),dbCompanyPayAmount); //单位支付
				Assert.assertEquals(companyBill.getCompanyRefundAmount().longValue(),dbCompanyRefundAmount); //单位退款
				Assert.assertEquals(companyBill.getCompanyChargedAmount().longValue(),dbCompanyPayAmount - dbCompanyRefundAmount); //单位应收
				Assert.assertNull(companyBill.getPaymentRecordSn());//收款记录流水号为空
				Assert.assertEquals(companyBill.getStatus(),SettlementHospitalConfirmEnum.SETTLEMENT_TO_BE_CONFIRM.getCode());//医院待确认
				Assert.assertEquals(companyBill.getOperatorId().intValue(),defSettAccountId);
				Assert.assertEquals(companyBill.getSettlementViewType().intValue(),0);//医院视角
				Assert.assertEquals(companyBill.getIsDeleted().intValue(),0); //未删除
			}


			//8.医院优惠券账单 type=2
			System.out.println("开始比较结算医院优惠券账单..批次号"+settBatch);
			companyBillList = SettleChecker.getTradeHospitalCompanyBillByColumn(null,false,null, false, "batch_sn",settBatch,"type","2");
			if(dbHospitalCouponAmount==0 && dbHospitalCouponRefundAmount==0)//医院优惠券支付0，医院优惠券退款为0
				Assert.assertEquals(companyBillList.size(),0);
			else{
				Assert.assertEquals(companyBillList.size(),1);//有且1条医院优惠券账单
				TradeHospitalCompanyBill companyBill = companyBillList.get(0);
				Assert.assertNotNull(companyBill.getSn());
				Assert.assertEquals(companyBill.getHospitalId().intValue(),hospitalId);
				Assert.assertEquals(companyBill.getCompanyId().intValue(),newCompanyId);
				Assert.assertEquals(companyBill.getCompanyPayAmount().longValue(),dbHospitalCouponAmount); //医院优惠券支付
				Assert.assertEquals(companyBill.getCompanyRefundAmount().longValue(),dbHospitalCouponRefundAmount); //医院优惠券退款
				Assert.assertEquals(companyBill.getCompanyChargedAmount().longValue(),dbHospitalCouponAmount - dbHospitalCouponRefundAmount); //单位应收
				Assert.assertNull(companyBill.getPaymentRecordSn());//收款记录流水号为空
				Assert.assertEquals(companyBill.getStatus(),SettlementHospitalConfirmEnum.SETTLEMENT_TO_BE_CONFIRM.getCode());//医院待确认
				Assert.assertEquals(companyBill.getOperatorId().intValue(),defSettAccountId);
				Assert.assertEquals(companyBill.getSettlementViewType().intValue(),0);//医院视角
				Assert.assertEquals(companyBill.getIsDeleted().intValue(),0); //未删除
			}



			//9.医院线上支付账单 type=3
			System.out.println("开始比较结算医院线上支付账单..批次号"+settBatch);
			companyBillList = SettleChecker.getTradeHospitalCompanyBillByColumn(null,false,null, false, "batch_sn",settBatch,"type","3");
			if(dbHospitalOnlinePayAmount==0 && dbHospitalOnlineRefundAmount ==0)//医院线上支付，医院线上退款=0
				Assert.assertEquals(companyBillList.size(),0);
			else{
				Assert.assertEquals(companyBillList.size(),1);//有且1条医院线上账单
				TradeHospitalCompanyBill companyBill = companyBillList.get(0);
				Assert.assertNotNull(companyBill.getSn());
				Assert.assertEquals(companyBill.getHospitalId().intValue(),hospitalId);
				Assert.assertEquals(companyBill.getCompanyId().intValue(),newCompanyId);
				Assert.assertEquals(companyBill.getCompanyPayAmount().longValue(),dbHospitalOnlinePayAmount); //医院线上支付
				Assert.assertEquals(companyBill.getCompanyRefundAmount().longValue(),dbHospitalOnlineRefundAmount); //医院线上退款
				Assert.assertEquals(companyBill.getCompanyChargedAmount().longValue(),dbHospitalOnlinePayAmount - dbHospitalOnlineRefundAmount); //单位应收
				Assert.assertNull(companyBill.getPaymentRecordSn());//收款记录流水号为空
				Assert.assertEquals(companyBill.getStatus(),SettlementHospitalConfirmEnum.SETTLEMENT_TO_BE_CONFIRM.getCode());//医院待确认
				Assert.assertEquals(companyBill.getOperatorId().intValue(),defSettAccountId);
				Assert.assertEquals(companyBill.getSettlementViewType().intValue(),0);//医院视角
				Assert.assertEquals(companyBill.getIsDeleted().intValue(),0); //未删除
			}



			//10.医院线下支付账单 type=4
			System.out.println("开始比较结算医院线上支付账单..批次号"+settBatch);
			companyBillList = SettleChecker.getTradeHospitalCompanyBillByColumn(null,false,null, false, "batch_sn",settBatch,"type","4");
			if(dbOfflinePayAmount == 0)
				Assert.assertEquals(companyBillList.size(),0);
			else {
				Assert.assertEquals(companyBillList.size(),1);//有且1条医院线下账单
				TradeHospitalCompanyBill companyBill = companyBillList.get(0);
				Assert.assertNotNull(companyBill.getSn());
				Assert.assertEquals(companyBill.getHospitalId().intValue(),hospitalId);
				Assert.assertEquals(companyBill.getCompanyId().intValue(),newCompanyId);
				Assert.assertEquals(companyBill.getCompanyPayAmount().longValue(),dbOfflinePayAmount); //医院线下支付
				Assert.assertEquals(companyBill.getCompanyRefundAmount().longValue(),0l); //医院线上退款=0
				Assert.assertEquals(companyBill.getCompanyChargedAmount().longValue(),dbOfflinePayAmount); //单位应收
				Assert.assertNull(companyBill.getPaymentRecordSn());//收款记录流水号为空
				Assert.assertEquals(companyBill.getStatus(),SettlementHospitalConfirmEnum.SETTLEMENT_TO_BE_CONFIRM.getCode());//医院待确认
				Assert.assertEquals(companyBill.getOperatorId().intValue(),defSettAccountId);
				Assert.assertEquals(companyBill.getSettlementViewType().intValue(),0);//医院视角
				Assert.assertEquals(companyBill.getIsDeleted().intValue(),0); //未删除

			}


			//11.医院平台账单
			System.out.println("开始比较结算医院平台账单...批次号"+settBatch);
			List<TradeHospitalPlatformBill> platBillList = SettleChecker.getTradeHospitalPlatformBillByColumn("select * from tb_trade_hospital_platform_bill where batch_sn = "+settBatch + " and settlement_view_type = 0");
			if(dbPlatFormPayAmount == 0 && dbPlatFormRefundAmount == 0 && dbPlatFormPrepaymentAmount == 0)
				Assert.assertEquals(platBillList.size(),0);
			else{
				Assert.assertEquals(platBillList.size(),1);//有且1条平台账单
				TradeHospitalPlatformBill platBill = platBillList.get(0);
				Assert.assertNotNull(platBill.getSn());
				Assert.assertEquals(platBill.getHospitalId().intValue(),hospitalId);
				Assert.assertEquals(platBill.getCompanyId().intValue(),newCompanyId);
				Assert.assertEquals(platBill.getPlatformPayAmount().longValue(),dbPlatFormPayAmount); //平台支付
				Assert.assertEquals(platBill.getPlatformRefundAmount().longValue(),dbPlatFormRefundAmount); //平台退款
				Assert.assertEquals(platBill.getPlatformPrepaymentAmount().longValue(),dbPlatFormPrepaymentAmount); //平台预付款
				Assert.assertEquals(platBill.getPlatformChargedAmount().longValue(), dbPlatFormPayAmount - dbPlatFormRefundAmount - dbPlatFormPrepaymentAmount); //应收平台金额

				Assert.assertEquals(platBill.getPlatformActurallyPayAmount().intValue(),0); //平台实收,初始时为0
				Assert.assertNull(platBill.getRemark());
				Assert.assertNull(platBill.getPaymentRecordSn());//收款记录流水号为空
				Assert.assertEquals(platBill.getSettlementViewType().intValue(),0);//医院视角
				Assert.assertEquals(platBill.getStatus(),SettlementHospitalConfirmEnum.SETTLEMENT_TO_BE_CONFIRM.getCode());//医院待确认
//			Assert.assertEquals(platBill.getOperatorId().intValue(),-defSettAccountId); //平台账单中CRM客户经理为负数 -----------BUG?
				Assert.assertEquals(platBill.getIsDeleted().intValue(),0); //未删除
			}

			//12.批次进程表
			List<BatchOrderProcess> dblist = OrderChecker.getBatchOrderSettleList(defSettHospitalId,1);
			Assert.assertEquals(dblist.size(),1);
			BatchOrderProcess process = dblist.get(0);
			Assert.assertEquals(process.getCompany_ids().toString(),Arrays.asList(newCompanyId).toString());
			Assert.assertEquals(process.getTaskType().intValue(),2);//单位结算
			Assert.assertEquals(process.getTotalNum().intValue(),allItems);


		}
	}


	@DataProvider
	public Iterator<String[]> crm_prepareSettData(){
		try {
			return CvsFileUtil.fromCvsFileToIterator("./csv/settlement/crm_prepareSettData.csv",18);
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
	public Iterator<String[]> crm_create_sett(){
		try {
			return CvsFileUtil.fromCvsFileToIterator("./csv/settlement/crm_create_sett.csv",18);
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
