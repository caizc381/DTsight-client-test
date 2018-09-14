package com.tijiantest.base.dbcheck;

import java.text.ParseException;
import java.util.*;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.tijiantest.model.card.Card;
import com.tijiantest.model.coupon.*;
import com.tijiantest.model.organization.OrganizationTypeEnum;
import com.tijiantest.model.settlement.PaymentOrder;
import com.tijiantest.model.settlement.TradeThreeAccounts;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.testng.Assert;
import com.jayway.jsonpath.JsonPath;
import com.tijiantest.base.BaseTest;
import com.tijiantest.base.Flag;
import com.tijiantest.base.HttpResult;
import com.tijiantest.base.MyHttpClient;
import com.tijiantest.model.company.HospitalCompany;
import com.tijiantest.model.hospital.HospitalParam;
import com.tijiantest.model.order.ExamOrderOperateLogDO;
import com.tijiantest.model.order.HospitalExamCompanySnapshot;
import com.tijiantest.model.order.Order;
import com.tijiantest.model.order.OrderOperateTypeEnum;
import com.tijiantest.model.order.OrderStatus;
import com.tijiantest.model.paylog.PayConsts.TradeStatus;
import com.tijiantest.model.paylog.PayLog;
import com.tijiantest.model.paylog.PaymentMethod;
import com.tijiantest.model.payment.Accounting;
import com.tijiantest.model.payment.PaymentTypeEnum;
import com.tijiantest.model.payment.trade.PayAmount;
import com.tijiantest.model.payment.trade.PayConstants;
import com.tijiantest.model.payment.trade.PayConstants.PayMethodBit;
import com.tijiantest.model.payment.trade.PayConstants.TradeType;
import com.tijiantest.model.payment.trade.RefundAmount;
import com.tijiantest.model.payment.trade.RefundConstants;
import com.tijiantest.model.payment.trade.TradeAccountDetail;
import com.tijiantest.model.payment.trade.TradeAccountDetailBizType;
import com.tijiantest.model.payment.trade.TradeOrder;
import com.tijiantest.model.payment.trade.TradePayRecord;
import com.tijiantest.model.payment.trade.TradeRefundRecord;
import com.tijiantest.model.payment.trade.TradeSubAccountType;
import com.tijiantest.util.db.DBMapper;
import com.tijiantest.util.db.MongoDBUtils;
import com.tijiantest.util.db.SqlException;

/**
 * 交易账户校验（包括交易支付 + 交易账户 2种验证）
 * @author huifang
 *
 */
public class PayChecker extends BaseTest{

	
	/**
	 * 校验C端站点登陆下支付页面
	 * 
	 * @param hc
	 * @param orderId
	 * @param cardId
	 *            -1:不传入cardid
	 * @throws SqlException
	 */
	public static void checkPayPage(MyHttpClient hc, int orderId, int cardId,String site,int accountId) throws SqlException, ParseException {
		// 支付页面
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("orderId", orderId + ""));
		if (cardId != -1)
			params.add(new BasicNameValuePair("selectedCardId", cardId + ""));// 通过卡进入支付页面
		params.add(new BasicNameValuePair("_site", site));
		params.add(new BasicNameValuePair("_siteType", "mobile"));
		HttpResult response = hc.get(Flag.MAIN, Main_PayPageV2, params);
		String body = response.getBody();
		log.info("支付页面...." + body);
		Assert.assertEquals(response.getCode(), HttpStatus.SC_OK, "卡支付页面报错:" + body);
		int retOrderId = Integer.parseInt(JsonPath.read(body, "$.orderId").toString());
		int retOrderPrice = Integer.parseInt(JsonPath.read(body, "$.orderPrice").toString());
		int retBalance = Integer.parseInt(JsonPath.read(body, "$.accounting.balance").toString());
		List<Integer> selectedCardIds = JsonPath.read(body, "$.selectedCardIds.[*]");
		List<UserCouponVOs> availableCouponList = JSONArray.parseArray(JsonPath.read(body,"$.hospitalCoupon.availableCoupon").toString(),UserCouponVOs.class);//可用医院优惠券列表
		int availableCouponNum = JsonPath.read(body,"$.hospitalCoupon.availableCouponNum");//可用医院优惠券数量
		List<UserCouponVOs> availableRedPacList = JSONArray.parseArray(JsonPath.read(body,"$.redPackage.availableRedPackage").toString(),UserCouponVOs.class);//可用红包列表
		int availableRedPacNum = JsonPath.read(body,"$.redPackage.availableRedPackageNum");//可用红包数量

		if (checkdb) {
			// 订单
			Order order = OrderChecker.getOrderInfo(orderId);
			Assert.assertEquals(order.getId(), retOrderId);
			int dbOrderPrice = order.getOrderPrice();

			if (cardId != -1){
				try {Card card = CardChecker.getCardInfo(cardId);
					if(card.getCardSetting().isShowCardMealPrice())//隐价卡
					{
						int amount = 0;
						if(card.getRecoverableBalance()!=null)//隐价卡有回收余额
							amount = card.getCapacity().intValue() - card.getRecoverableBalance().intValue();
						else
							amount = card.getCapacity().intValue();
						if( dbOrderPrice> amount ) //1.当订单金额 >卡实际可用金额，则取差值，否则为0显示
							Assert.assertEquals(retOrderPrice,dbOrderPrice-amount);
						else
							Assert.assertEquals(retOrderPrice,0);
					}
					else //普通卡
						Assert.assertEquals(retOrderPrice,dbOrderPrice );
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}

			// 账户余额
			String sql = "select * from tb_accounting where account_id = ?";
			List<Map<String,Object>> newlist = DBMapper.query(sql, accountId);
			Assert.assertEquals(newlist.get(0).get("balance"), retBalance);
			// 卡信息
			if (cardId != -1) {
				sql = "select * from tb_card where id = ?";
				newlist = DBMapper.query(sql, cardId);
				// 如果隐价卡，程序直接把balance和capacity设为0，但是仍显示卡列表
				String settingsSql = "select * from tb_card_settings where card_id=?";
				List<Map<String, Object>> settingList = DBMapper.query(settingsSql, cardId);

				int card_balance = Integer.parseInt(newlist.get(0).get("balance").toString());
				if (card_balance > 0)
					Assert.assertEquals(selectedCardIds.get(0).intValue(), cardId); // 没有选择卡列表
				else {
					if (settingList.get(0).get("is_show_card_meal_price").toString().equals("1")) {
						// 隐价卡
						Assert.assertEquals(selectedCardIds.get(0).intValue(), cardId); // 没有选择卡列表
					} else {
						Assert.assertEquals(selectedCardIds.size(), 0); // 没有选择卡列表
					}
				}
			} else
				Assert.assertEquals(selectedCardIds.size(), 0); // 没有选择卡列表

//			//排序
//			Collections.sort(availableCouponList, new Comparator<UserCouponVOs>() {
//				@Override
//				public int compare(UserCouponVOs o1, UserCouponVOs o2) {
//					return  - o1.getCouponId() + o2.getCouponId();
//				}
//			});
			//优惠券信息(未过期，未使用，未删除，医院ID正确,accountId正确,面值符合满减条件) 领取时间倒序排列
			List<UserCouponReceive> dbUserCouponReceiveList = getOrderAvaliableCouponList(order);
			if(dbUserCouponReceiveList != null && dbUserCouponReceiveList.size()>0){
				Assert.assertEquals(availableCouponNum ,dbUserCouponReceiveList.size());//判断可用优惠券数量
				for(int k=0;k<dbUserCouponReceiveList.size();k++){//判断可用优惠券的详情
//					log.info("aaa"+availableCouponList.get(k).getCouponTemplateResult().getBatchNum());
//					log.info("bbb"+dbUserCouponReceiveList.get(k).getCouponTemplate().getBatchNum());
					Assert.assertEquals(availableCouponList.get(k).getCouponTemplateResult().getBatchNum(),dbUserCouponReceiveList.get(k).getCouponTemplate().getBatchNum());
					Assert.assertEquals(availableCouponList.get(k).getCouponTemplateResult().getName(),dbUserCouponReceiveList.get(k).getCouponTemplate().getName());
					Assert.assertEquals(availableCouponList.get(k).getCouponTemplateResult().getPrice().intValue(),dbUserCouponReceiveList.get(k).getCouponTemplate().getPrice());
					Assert.assertEquals(availableCouponList.get(k).getCouponTemplateResult().getDescrpiton(),dbUserCouponReceiveList.get(k).getCouponTemplate().getDescription());
					Assert.assertEquals(availableCouponList.get(k).getCouponTemplateResult().getMinLimitPrice().intValue(),dbUserCouponReceiveList.get(k).getCouponTemplate().getMinLimitPrice());
					Assert.assertEquals(availableCouponList.get(k).getCouponTemplateResult().getStartTime(),dbUserCouponReceiveList.get(k).getCouponTemplate().getStartTime());
					Assert.assertEquals(availableCouponList.get(k).getCouponTemplateResult().getEndTime(),dbUserCouponReceiveList.get(k).getCouponTemplate().getEndTime());
					Assert.assertEquals(availableCouponList.get(k).getCouponId().intValue(),dbUserCouponReceiveList.get(k).getId());//优惠券id
					Assert.assertEquals(availableCouponList.get(k).getStatus(),dbUserCouponReceiveList.get(k).getStatus().intValue());//优惠券状态
				}
			}
			else
				Assert.assertTrue(availableCouponList == null || availableCouponList.size() == 0);

			//红包信息(未过期，未使用，未删除，医院ID正确,accountId正确,面值符合满减条件) 领取时间倒序排列
			List<UserCouponReceive> dbUserRedPacksReceiveList = getOrderAvaliableRedPackList(order);
			if(dbUserRedPacksReceiveList != null && dbUserRedPacksReceiveList.size()>0){
				Assert.assertEquals(availableRedPacNum ,dbUserRedPacksReceiveList.size());//判断可用红包数量
				for(int k=0;k<dbUserRedPacksReceiveList.size();k++){//判断可用红包的详情
//					log.info("aaa"+availableRedPacList.get(k).getCouponTemplateResult().getBatchNum());
//					log.info("bbb"+dbUserRedPacksReceiveList.get(k).getCouponTemplate().getBatchNum());
					Assert.assertEquals(availableRedPacList.get(k).getCouponTemplateResult().getBatchNum(),dbUserRedPacksReceiveList.get(k).getCouponTemplate().getBatchNum());
					Assert.assertEquals(availableRedPacList.get(k).getCouponTemplateResult().getName(),dbUserRedPacksReceiveList.get(k).getCouponTemplate().getName());
					Assert.assertEquals(availableRedPacList.get(k).getCouponTemplateResult().getPrice().intValue(),dbUserRedPacksReceiveList.get(k).getCouponTemplate().getPrice());
					Assert.assertEquals(availableRedPacList.get(k).getCouponTemplateResult().getDescrpiton(),dbUserRedPacksReceiveList.get(k).getCouponTemplate().getDescription());
					Assert.assertEquals(availableRedPacList.get(k).getCouponTemplateResult().getMinLimitPrice().intValue(),dbUserRedPacksReceiveList.get(k).getCouponTemplate().getMinLimitPrice());
					Assert.assertEquals(availableRedPacList.get(k).getCouponTemplateResult().getStartTime(),dbUserRedPacksReceiveList.get(k).getCouponTemplate().getStartTime());
					Assert.assertEquals(availableRedPacList.get(k).getCouponTemplateResult().getEndTime(),dbUserRedPacksReceiveList.get(k).getCouponTemplate().getEndTime());
					Assert.assertEquals(availableRedPacList.get(k).getCouponId().intValue(),dbUserRedPacksReceiveList.get(k).getId());//优惠券id
					Assert.assertEquals(availableRedPacList.get(k).getStatus(),dbUserRedPacksReceiveList.get(k).getStatus().intValue());//优惠券状态
				}
			}
			else
				Assert.assertTrue(availableRedPacList == null || availableRedPacList.size() == 0);
			if (checkmongo) {
				waitto(mongoWaitTime);
				List<Map<String, Object>> monlist = MongoDBUtils.query("{'id':" + orderId + "}", MONGO_COLLECTION);
				Assert.assertEquals(monlist.size(), 1);
				Assert.assertEquals(monlist.get(0).get("orderPrice"),dbOrderPrice);
			}
		}
	}



	/**
	 * 校验微信小程序/微信城市服务登陆态下的支付页面
	 *
	 * @param hc
	 * @param orderId
	 * @param cardId
	 *            -1:不传入cardid
	 * @throws SqlException
	 */
	public static void checkPaymentPage(MyHttpClient hc, int orderId, int cardId,String site,int accountId) throws SqlException, ParseException {
		// 支付页面
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("orderId", orderId + ""));
		if (cardId != -1)
			params.add(new BasicNameValuePair("selectedCardId", cardId + ""));// 通过卡进入支付页面
		params.add(new BasicNameValuePair("site", site));
		params.add(new BasicNameValuePair("operator", accountId+""));
		HttpResult response = hc.get(Flag.MAIN, Main_PaymentPage, params);
		String body = response.getBody();
		log.info("支付页面...." + body);
		Assert.assertEquals(response.getCode(), HttpStatus.SC_OK, "卡支付页面报错:" + body);
		int retOrderId = Integer.parseInt(JsonPath.read(body, "$.orderId").toString());
		int retOrderPrice = Integer.parseInt(JsonPath.read(body, "$.orderPrice").toString());
		int retBalance = Integer.parseInt(JsonPath.read(body, "$.accounting.balance").toString());
		List<Integer> selectedCardIds = JsonPath.read(body, "$.selectedCardIds.[*]");
		List<UserCouponVOs> availableCouponList = JSONArray.parseArray(JsonPath.read(body,"$.availableCoupon").toString(),UserCouponVOs.class);//可用优惠券列表
		int availableCouponNum = JsonPath.read(body,"$.availableCouponNum");//可用优惠券数量
		if (checkdb) {
			// 订单
			Order order = OrderChecker.getOrderInfo(orderId);
			Assert.assertEquals(order.getId(), retOrderId);
			int dbOrderPrice = order.getOrderPrice();

			if (cardId != -1){
				try {Card card = CardChecker.getCardInfo(cardId);
					if(card.getCardSetting().isShowCardMealPrice())//隐价卡
					{
						int amount = 0;
						if(card.getRecoverableBalance()!=null)//隐价卡有回收余额
							amount = card.getCapacity().intValue() - card.getRecoverableBalance().intValue();
						else
							amount = card.getCapacity().intValue();
						if( dbOrderPrice> amount ) //1.当订单金额 >卡实际可用金额，则取差值，否则为0显示
							Assert.assertEquals(retOrderPrice,dbOrderPrice-amount);
						else
							Assert.assertEquals(retOrderPrice,0);
					}
					else //普通卡
						Assert.assertEquals(retOrderPrice,dbOrderPrice );
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}

			// 账户余额
			String sql = "select * from tb_accounting where account_id = ?";
			List<Map<String,Object>> newlist = DBMapper.query(sql, accountId);
			Assert.assertEquals(newlist.get(0).get("balance"), retBalance);
			// 卡信息
			if (cardId != -1) {
				sql = "select * from tb_card where id = ?";
				newlist = DBMapper.query(sql, cardId);
				// 如果隐价卡，程序直接把balance和capacity设为0，但是仍显示卡列表
				String settingsSql = "select * from tb_card_settings where card_id=?";
				List<Map<String, Object>> settingList = DBMapper.query(settingsSql, cardId);

				int card_balance = Integer.parseInt(newlist.get(0).get("balance").toString());
				if (card_balance > 0)
					Assert.assertEquals(selectedCardIds.get(0).intValue(), cardId); // 没有选择卡列表
				else {
					if (settingList.get(0).get("is_show_card_meal_price").toString().equals("1")) {
						// 隐价卡
						Assert.assertEquals(selectedCardIds.get(0).intValue(), cardId); // 没有选择卡列表
					} else {
						Assert.assertEquals(selectedCardIds.size(), 0); // 没有选择卡列表
					}
				}
			} else
				Assert.assertEquals(selectedCardIds.size(), 0); // 没有选择卡列表

			//优惠券信息(未过期，未使用，未删除，医院ID正确,accountId正确,面值符合满减条件) 领取时间倒序排列
			List<UserCouponReceive> dbUserCouponReceiveList = getOrderAvaliableCouponList(order);
			if(dbUserCouponReceiveList != null && dbUserCouponReceiveList.size()>0){
				Assert.assertEquals(availableCouponNum ,dbUserCouponReceiveList.size());//判断可用优惠券数量
				for(int k=0;k<dbUserCouponReceiveList.size();k++){//判断可用优惠券的详情
					log.info("aaa"+availableCouponList.get(k).getCouponTemplateResult().getBatchNum());
					log.info("bbb"+dbUserCouponReceiveList.get(k).getCouponTemplate().getBatchNum());
					Assert.assertEquals(availableCouponList.get(k).getCouponTemplateResult().getBatchNum(),dbUserCouponReceiveList.get(k).getCouponTemplate().getBatchNum());
					Assert.assertEquals(availableCouponList.get(k).getCouponTemplateResult().getName(),dbUserCouponReceiveList.get(k).getCouponTemplate().getName());
					Assert.assertEquals(availableCouponList.get(k).getCouponTemplateResult().getPrice().intValue(),dbUserCouponReceiveList.get(k).getCouponTemplate().getPrice());
					Assert.assertEquals(availableCouponList.get(k).getCouponTemplateResult().getDescrpiton(),dbUserCouponReceiveList.get(k).getCouponTemplate().getDescription());
					Assert.assertEquals(availableCouponList.get(k).getCouponTemplateResult().getMinLimitPrice().intValue(),dbUserCouponReceiveList.get(k).getCouponTemplate().getMinLimitPrice());
					Assert.assertEquals(availableCouponList.get(k).getCouponTemplateResult().getStartTime(),dbUserCouponReceiveList.get(k).getCouponTemplate().getStartTime());
					Assert.assertEquals(availableCouponList.get(k).getCouponTemplateResult().getEndTime(),dbUserCouponReceiveList.get(k).getCouponTemplate().getEndTime());
					Assert.assertEquals(availableCouponList.get(k).getCouponId().intValue(),dbUserCouponReceiveList.get(k).getId());//优惠券id
					Assert.assertEquals(availableCouponList.get(k).getStatus(),dbUserCouponReceiveList.get(k).getStatus().intValue());//优惠券状态
				}
			}
			else
				Assert.assertTrue(availableCouponList == null || availableCouponList.size() == 0);
			if (checkmongo) {
				waitto(mongoWaitTime);
				List<Map<String, Object>> monlist = MongoDBUtils.query("{'id':" + orderId + "}", MONGO_COLLECTION);
				Assert.assertEquals(monlist.size(), 1);
				Assert.assertEquals(monlist.get(0).get("orderPrice"),dbOrderPrice);
			}
		}
	}
	/**
	 * 根据交易的方式和状态，验证交易记录
	 * 
	 * @param payMethodType
	 * @param tradeStatus
	 * @throws Exception
	 */
	public static  void checkPayMethodType(int orderId, String trade_order_num, int payMethodType, int tradeStatus,
			int useHosptialId, int dbReceiveTradeAccountId) throws Exception {
		String orderSql = "select * from tb_order where id = ?";
		List<Map<String, Object>> orderList = DBMapper.query(orderSql, orderId);
		String orderNum = orderList.get(0).get("order_num").toString();
		int orderprice = Integer.parseInt(orderList.get(0).get("order_price").toString());
		Object entryCard = orderList.get(0).get("entry_card_id");
		if (payMethodType == PayMethodBit.CardBit) {// 纯卡
			if (tradeStatus != TradeStatus.Successful)
				System.out.println("交易状态错误...." + tradeStatus);
			int entryCardId = 0;
			if (entryCard != null)
				entryCardId = Integer.parseInt(entryCard.toString());
			// 交易支付表
			List<TradePayRecord> tradePayRecList = getTradePayRecordByOrderNum(orderNum, trade_order_num,PayConstants.OrderType.MytijianOrder);
			Assert.assertEquals(tradePayRecList.size(), 1);
			TradePayRecord tpr = tradePayRecList.get(0);
			Assert.assertEquals(tpr.getRefOrderType().intValue(), 1);
			Assert.assertEquals(tpr.getTradeMethodConfigId().intValue(),
					getSuitablePayMethodId(useHosptialId, PayConstants.PayMethodBit.CardBit));
			Assert.assertEquals(tpr.getTradeMethodType().intValue(), PayConstants.PayMethod.Card);
			Assert.assertEquals(tpr.getPayStatus().intValue(), PayConstants.TradeStatus.Successful);
			Assert.assertEquals(tpr.getPayTradeAccountId().intValue(), getTradeAccountByOrderId(orderId));
			Assert.assertEquals(tpr.getPayTradeSubaccountId().intValue(),
					getSubAccounttingId(getTradeAccountByOrderId(orderId)));
			Assert.assertEquals(tpr.getPayTradeSubaccountType().intValue(), TradeSubAccountType.TRADE_CARD_ACCOUNT);
			Assert.assertEquals(tpr.getReceiveTradeAccountId().intValue(), dbReceiveTradeAccountId);
			Assert.assertEquals(tpr.getReceiveTradeSubaccountId().intValue(),
					getSubAccounttingId(dbReceiveTradeAccountId));
			Assert.assertEquals(tpr.getReceiveTradeSubaccountType().intValue(),
					TradeSubAccountType.TRADE_BALANCE_ACCOUNT);
			// 交易明细表
			List<TradeAccountDetail> tradeAccountList = getTradeAccountDetail(trade_order_num, 0);
			Assert.assertEquals(tradeAccountList.size(), 1);
			TradeAccountDetail tad = tradeAccountList.get(0);
			Assert.assertEquals(tad.getRefBizSn(), tpr.getSn());
			Assert.assertEquals(tad.getTradeAccountId().intValue(), dbReceiveTradeAccountId);
			Assert.assertEquals(tad.getTradeSubAccountType().intValue(), TradeSubAccountType.TRADE_BALANCE_ACCOUNT);
			Assert.assertEquals(tad.getTradeSubAccountId().intValue(), getSubAccounttingId(dbReceiveTradeAccountId));
			Assert.assertEquals(tad.getTradeOrderNum(), trade_order_num);
			Assert.assertEquals(tad.getBizType().intValue(), TradeAccountDetailBizType.PAYMENT);
			Assert.assertEquals(tad.getChangeAmount().intValue(), orderprice);

			tradeAccountList = getTradeAccountDetail(trade_order_num, 1);
			Assert.assertEquals(tradeAccountList.size(), 1);
			tad = tradeAccountList.get(0);
			Assert.assertEquals(tad.getRefBizSn(), tpr.getSn());
			Assert.assertEquals(tad.getTradeAccountId().intValue(), getTradeAccountByOrderId(orderId));
			Assert.assertEquals(tad.getTradeSubAccountType().intValue(), TradeSubAccountType.TRADE_CARD_ACCOUNT);
			Assert.assertEquals(tad.getTradeSubAccountId().intValue(), entryCardId);
			Assert.assertEquals(tad.getTradeOrderNum(), trade_order_num);
			Assert.assertEquals(tad.getBizType().intValue(), TradeAccountDetailBizType.PAYMENT);
			Assert.assertEquals(tad.getChangeAmount().intValue(), orderprice);
		}
		if (payMethodType == PayMethodBit.BalanceBit) {// 存余额
			if (tradeStatus != PayConstants.TradeStatus.Successful)
				System.out.println("交易状态错误...." + tradeStatus);
			// 交易支付表
			List<TradePayRecord> tradePayRecList = getTradePayRecordByOrderNum(orderNum, trade_order_num,PayConstants.OrderType.MytijianOrder);
			Assert.assertEquals(tradePayRecList.size(), 1);
			TradePayRecord tpr = tradePayRecList.get(0);
			Assert.assertEquals(tpr.getRefOrderType().intValue(), 1);
			Assert.assertEquals(tpr.getTradeMethodConfigId().intValue(),
					getSuitablePayMethodId(useHosptialId, PayConstants.PayMethodBit.BalanceBit));
			Assert.assertEquals(tpr.getTradeMethodType().intValue(), PayConstants.PayMethod.Balance);
			Assert.assertEquals(tpr.getPayStatus().intValue(), PayConstants.TradeStatus.Successful);
			Assert.assertEquals(tpr.getPayTradeAccountId().intValue(), getTradeAccountByOrderId(orderId));
			Assert.assertEquals(tpr.getPayTradeSubaccountId().intValue(),
					getSubAccounttingId(getTradeAccountByOrderId(orderId)));
			Assert.assertEquals(tpr.getPayTradeSubaccountType().intValue(), TradeSubAccountType.TRADE_BALANCE_ACCOUNT);
			Assert.assertEquals(tpr.getReceiveTradeAccountId().intValue(), dbReceiveTradeAccountId);
			Assert.assertEquals(tpr.getReceiveTradeSubaccountId().intValue(),
					getSubAccounttingId(dbReceiveTradeAccountId));
			Assert.assertEquals(tpr.getReceiveTradeSubaccountType().intValue(),
					TradeSubAccountType.TRADE_BALANCE_ACCOUNT);
			// 交易明细表
			List<TradeAccountDetail> tradeAccountList = getTradeAccountDetail(trade_order_num, 0);
			Assert.assertEquals(tradeAccountList.size(), 1);
			TradeAccountDetail tad = tradeAccountList.get(0);
			Assert.assertEquals(tad.getRefBizSn(), tpr.getSn());
			Assert.assertEquals(tad.getTradeAccountId().intValue(), dbReceiveTradeAccountId);
			Assert.assertEquals(tad.getTradeSubAccountType().intValue(), TradeSubAccountType.TRADE_BALANCE_ACCOUNT);
			Assert.assertEquals(tad.getTradeSubAccountId().intValue(), getSubAccounttingId(dbReceiveTradeAccountId));
			Assert.assertEquals(tad.getTradeOrderNum(), trade_order_num);
			Assert.assertEquals(tad.getBizType().intValue(), TradeAccountDetailBizType.PAYMENT);
			Assert.assertEquals(tad.getChangeAmount().intValue(), orderprice);

			tradeAccountList = getTradeAccountDetail(trade_order_num, 1);
			Assert.assertEquals(tradeAccountList.size(), 1);
			tad = tradeAccountList.get(0);
			Assert.assertEquals(tad.getRefBizSn(), tpr.getSn());
			Assert.assertEquals(tad.getTradeAccountId().intValue(), getTradeAccountByOrderId(orderId));
			Assert.assertEquals(tad.getTradeSubAccountType().intValue(), TradeSubAccountType.TRADE_BALANCE_ACCOUNT);
			Assert.assertEquals(tad.getTradeSubAccountId().intValue(),
					getSubAccounttingId(getTradeAccountByOrderId(orderId)));
			Assert.assertEquals(tad.getTradeOrderNum(), trade_order_num);
			Assert.assertEquals(tad.getBizType().intValue(), TradeAccountDetailBizType.PAYMENT);
			Assert.assertEquals(tad.getChangeAmount().intValue(), orderprice);
		}
		if (payMethodType == PayMethodBit.AlipayBit) {// 纯支付宝
			if (tradeStatus == PayConstants.TradeStatus.Paying) {
				List<TradePayRecord> tradePayRecList = getTradePayRecordByOrderNum(orderNum, trade_order_num,PayConstants.OrderType.MytijianOrder);
				Assert.assertEquals(tradePayRecList.size(), 1);
				TradePayRecord tpr = tradePayRecList.get(0);
				Assert.assertEquals(tpr.getRefOrderType().intValue(), 1);
				Assert.assertEquals(tpr.getTradeMethodConfigId().intValue(),
						getSuitablePayMethodId(useHosptialId, PayConstants.PayMethodBit.AlipayBit));
				Assert.assertEquals(tpr.getTradeMethodType().intValue(), PayConstants.PayMethod.Alipay);
				Assert.assertEquals(tpr.getPayStatus().intValue(), PayConstants.TradeStatus.Paying);
				Assert.assertEquals(tpr.getPayTradeAccountId().intValue(), getTradeAccountByOrderId(orderId));
				Assert.assertNull(tpr.getPayTradeSubaccountId());
				Assert.assertNull(tpr.getPayTradeSubaccountType());
				Assert.assertNull(tpr.getPayTradeAccountSnap());
				Assert.assertEquals(tpr.getReceiveTradeAccountId().intValue(), dbReceiveTradeAccountId);
				Assert.assertEquals(tpr.getReceiveTradeSubaccountId().intValue(),
						getSubAccounttingId(dbReceiveTradeAccountId));
				Assert.assertEquals(tpr.getReceiveTradeSubaccountType().intValue(),
						TradeSubAccountType.TRADE_BALANCE_ACCOUNT);
				List<TradeAccountDetail> tradeAccountList = getTradeAccountDetail(trade_order_num);
				Assert.assertEquals(tradeAccountList.size(), 0);
			}
		}
		if (payMethodType == PayMethodBit.WxpayBit) {// 纯微信
			if (tradeStatus == PayConstants.TradeStatus.Paying) {
				List<TradePayRecord> tradePayRecList = getTradePayRecordByOrderNum(orderNum, trade_order_num,PayConstants.OrderType.MytijianOrder);
				Assert.assertEquals(tradePayRecList.size(), 1);
				TradePayRecord tpr = tradePayRecList.get(0);
				Assert.assertEquals(tpr.getRefOrderType().intValue(), 1);
				Assert.assertEquals(tpr.getTradeMethodConfigId().intValue(),
						getSuitablePayMethodId(useHosptialId, PayConstants.PayMethodBit.WxpayBit));
				Assert.assertEquals(tpr.getTradeMethodType().intValue(), PayConstants.PayMethod.Wxpay);
				Assert.assertEquals(tpr.getPayStatus().intValue(), PayConstants.TradeStatus.Paying);
				Assert.assertEquals(tpr.getPayTradeAccountId().intValue(), getTradeAccountByOrderId(orderId));
				Assert.assertNull(tpr.getPayTradeSubaccountId());
				Assert.assertNull(tpr.getPayTradeSubaccountType());
				Assert.assertNull(tpr.getPayTradeAccountSnap());
				Assert.assertEquals(tpr.getReceiveTradeAccountId().intValue(), dbReceiveTradeAccountId);
				Assert.assertEquals(tpr.getReceiveTradeSubaccountId().intValue(),
						getSubAccounttingId(dbReceiveTradeAccountId));
				Assert.assertEquals(tpr.getReceiveTradeSubaccountType().intValue(),
						TradeSubAccountType.TRADE_BALANCE_ACCOUNT);
				List<TradeAccountDetail> tradeAccountList = getTradeAccountDetail(trade_order_num);
				Assert.assertEquals(tradeAccountList.size(), 0);
			}
		}
		if (payMethodType == PayMethodBit.AlipayScanBit + PayMethodBit.OnlinePayBit) {// 纯支付宝扫码
			if (tradeStatus == PayConstants.TradeStatus.Paying) {
				List<TradePayRecord> tradePayRecList = getTradePayRecordByOrderNum(orderNum, trade_order_num,PayConstants.OrderType.MytijianOrder);
				Assert.assertEquals(tradePayRecList.size(), 2);
				TradePayRecord tpr = tradePayRecList.get(0);
				// 第一条是线上取消
				Assert.assertEquals(tpr.getRefOrderType().intValue(), 1);
				Assert.assertEquals(tpr.getTradeMethodConfigId().intValue(),
						getSuitablePayMethodId(useHosptialId, PayConstants.PayMethodBit.OnlinePayBit));
				Assert.assertEquals(tpr.getTradeMethodType().intValue(), PayConstants.PayMethod.OnlinePay);
				Assert.assertEquals(tpr.getPayStatus().intValue(), PayConstants.TradeStatus.Canceled);
				Assert.assertEquals(tpr.getPayTradeAccountId().intValue(), getTradeAccountByOrderId(orderId));
				Assert.assertNull(tpr.getPayTradeSubaccountId());
				Assert.assertNull(tpr.getPayTradeSubaccountType());
				Assert.assertNull(tpr.getPayTradeAccountSnap());
				Assert.assertEquals(tpr.getReceiveTradeAccountId().intValue(), dbReceiveTradeAccountId);
				Assert.assertEquals(tpr.getReceiveTradeSubaccountId().intValue(),
						getSubAccounttingId(dbReceiveTradeAccountId));
				Assert.assertEquals(tpr.getReceiveTradeSubaccountType().intValue(),
						TradeSubAccountType.TRADE_BALANCE_ACCOUNT);
				// 第二条是支付宝扫码支付
				tpr = tradePayRecList.get(1);
				Assert.assertEquals(tpr.getRefOrderType().intValue(), 1);
				Assert.assertEquals(tpr.getTradeMethodConfigId().intValue(),
						getSuitablePayMethodId(useHosptialId, PayConstants.PayMethodBit.AlipayScanBit));
				Assert.assertEquals(tpr.getTradeMethodType().intValue(), PayConstants.PayMethod.AlipayScan);
				Assert.assertEquals(tpr.getPayStatus().intValue(), PayConstants.TradeStatus.Successful);
				Assert.assertEquals(tpr.getPayTradeAccountId().intValue(), getTradeAccountByOrderId(orderId));
				Assert.assertNull(tpr.getPayTradeSubaccountId());
				Assert.assertNull(tpr.getPayTradeSubaccountType());
				Assert.assertNull(tpr.getPayTradeAccountSnap());
				Assert.assertEquals(tpr.getReceiveTradeAccountId().intValue(), dbReceiveTradeAccountId);
				Assert.assertEquals(tpr.getReceiveTradeSubaccountId().intValue(),
						getSubAccounttingId(dbReceiveTradeAccountId));
				Assert.assertEquals(tpr.getReceiveTradeSubaccountType().intValue(),
						TradeSubAccountType.TRADE_BALANCE_ACCOUNT);
				List<TradeAccountDetail> tradeAccountList = getTradeAccountDetail(trade_order_num);
				Assert.assertEquals(tradeAccountList.size(), 0);

			}
			if (tradeStatus == PayConstants.TradeStatus.Successful) {
				List<TradePayRecord> tradePayRecList = getTradePayRecordByOrderNum(orderNum, trade_order_num,PayConstants.OrderType.MytijianOrder);
				Assert.assertEquals(tradePayRecList.size(), 2);
				TradePayRecord tpr = tradePayRecList.get(0);
				Assert.assertEquals(tpr.getRefOrderType().intValue(), 1);
				Assert.assertEquals(tpr.getTradeMethodConfigId().intValue(),
						getSuitablePayMethodId(useHosptialId, PayConstants.PayMethodBit.AlipayScanBit));
				Assert.assertEquals(tpr.getTradeMethodType().intValue(), PayConstants.PayMethod.AlipayScan);
				Assert.assertEquals(tpr.getPayStatus().intValue(), PayConstants.TradeStatus.Successful);
				Assert.assertEquals(tpr.getPayTradeAccountId().intValue(), getTradeAccountByOrderId(orderId));
				Assert.assertNull(tpr.getPayTradeSubaccountId());
				Assert.assertNull(tpr.getPayTradeSubaccountType());
				Assert.assertNull(tpr.getPayTradeAccountSnap());
				Assert.assertEquals(tpr.getReceiveTradeAccountId().intValue(), dbReceiveTradeAccountId);
				Assert.assertEquals(tpr.getReceiveTradeSubaccountId().intValue(),
						getSubAccounttingId(dbReceiveTradeAccountId));
				Assert.assertEquals(tpr.getReceiveTradeSubaccountType().intValue(),
						TradeSubAccountType.TRADE_BALANCE_ACCOUNT);

				// 交易明细表
				List<TradeAccountDetail> tradeAccountList = getTradeAccountDetail(trade_order_num, 0);
				Assert.assertEquals(tradeAccountList.size(), 1);
				TradeAccountDetail tad = tradeAccountList.get(0);
				Assert.assertEquals(tad.getRefBizSn(), tpr.getSn());
				Assert.assertEquals(tad.getTradeAccountId().intValue(), dbReceiveTradeAccountId);
				Assert.assertEquals(tad.getTradeSubAccountType().intValue(), TradeSubAccountType.TRADE_BALANCE_ACCOUNT);
				Assert.assertEquals(tad.getTradeSubAccountId().intValue(),
						getSubAccounttingId(dbReceiveTradeAccountId));
				Assert.assertEquals(tad.getTradeOrderNum(), trade_order_num);
				Assert.assertEquals(tad.getBizType().intValue(), TradeAccountDetailBizType.PAYMENT);
				Assert.assertEquals(tad.getChangeAmount().intValue(), orderprice);

				tradeAccountList = getTradeAccountDetail(trade_order_num, 1);
				Assert.assertEquals(tradeAccountList.size(), 1);
				tad = tradeAccountList.get(0);
				Assert.assertEquals(tad.getRefBizSn(), tpr.getSn());
				Assert.assertEquals(tad.getTradeAccountId().intValue(), getTradeAccountByOrderId(orderId));
				Assert.assertNull(tad.getTradeSubAccountType());
				Assert.assertNull(tad.getTradeSubAccountId());
				Assert.assertEquals(tad.getTradeOrderNum(), trade_order_num);
				Assert.assertEquals(tad.getBizType().intValue(), TradeAccountDetailBizType.PAYMENT);
				Assert.assertEquals(tad.getChangeAmount().intValue(), orderprice);

			}
		}
		if (payMethodType == PayMethodBit.WxpayScanBit + PayMethodBit.OnlinePayBit) {// 纯微信扫码
			if (tradeStatus == PayConstants.TradeStatus.Paying) {
				List<TradePayRecord> tradePayRecList = getTradePayRecordByOrderNum(orderNum, trade_order_num,PayConstants.OrderType.MytijianOrder);
				Assert.assertEquals(tradePayRecList.size(), 2);
				TradePayRecord tpr = tradePayRecList.get(0);
				// 第一条是线上取消
				Assert.assertEquals(tpr.getRefOrderType().intValue(), 1);
				Assert.assertEquals(tpr.getTradeMethodConfigId().intValue(),
						getSuitablePayMethodId(useHosptialId, PayConstants.PayMethodBit.OnlinePayBit));
				Assert.assertEquals(tpr.getTradeMethodType().intValue(), PayConstants.PayMethod.OnlinePay);
				Assert.assertEquals(tpr.getPayStatus().intValue(), PayConstants.TradeStatus.Canceled);
				Assert.assertEquals(tpr.getPayTradeAccountId().intValue(), getTradeAccountByOrderId(orderId));
				Assert.assertNull(tpr.getPayTradeSubaccountId());
				Assert.assertNull(tpr.getPayTradeSubaccountType());
				Assert.assertNull(tpr.getPayTradeAccountSnap());
				Assert.assertEquals(tpr.getReceiveTradeAccountId().intValue(), dbReceiveTradeAccountId);
				Assert.assertEquals(tpr.getReceiveTradeSubaccountId().intValue(),
						getSubAccounttingId(dbReceiveTradeAccountId));
				Assert.assertEquals(tpr.getReceiveTradeSubaccountType().intValue(),
						TradeSubAccountType.TRADE_BALANCE_ACCOUNT);
				// 第二条是微信扫码支付
				tpr = tradePayRecList.get(1);
				Assert.assertEquals(tpr.getRefOrderType().intValue(), 1);
				Assert.assertEquals(tpr.getTradeMethodConfigId().intValue(),
						getSuitablePayMethodId(useHosptialId, PayConstants.PayMethodBit.WxpayScanBit));
				Assert.assertEquals(tpr.getTradeMethodType().intValue(), PayConstants.PayMethod.WxpayScan);
				Assert.assertEquals(tpr.getPayStatus().intValue(), PayConstants.TradeStatus.Successful);
				Assert.assertEquals(tpr.getPayTradeAccountId().intValue(), getTradeAccountByOrderId(orderId));
				Assert.assertNull(tpr.getPayTradeSubaccountId());
				Assert.assertNull(tpr.getPayTradeSubaccountType());
				Assert.assertNull(tpr.getPayTradeAccountSnap());
				Assert.assertEquals(tpr.getReceiveTradeAccountId().intValue(), dbReceiveTradeAccountId);
				Assert.assertEquals(tpr.getReceiveTradeSubaccountId().intValue(),
						getSubAccounttingId(dbReceiveTradeAccountId));
				Assert.assertEquals(tpr.getReceiveTradeSubaccountType().intValue(),
						TradeSubAccountType.TRADE_BALANCE_ACCOUNT);
				// 交易明细
				List<TradeAccountDetail> tradeAccountList = getTradeAccountDetail(trade_order_num);
				Assert.assertEquals(tradeAccountList.size(), 0);
			}
			if (tradeStatus == PayConstants.TradeStatus.Successful) {
				List<TradePayRecord> tradePayRecList = getTradePayRecordByOrderNum(orderNum, trade_order_num,PayConstants.OrderType.MytijianOrder);
				Assert.assertEquals(tradePayRecList.size(), 2);
				TradePayRecord tpr = tradePayRecList.get(0);
				Assert.assertEquals(tpr.getRefOrderType().intValue(), 1);
				Assert.assertEquals(tpr.getTradeMethodConfigId().intValue(),
						getSuitablePayMethodId(useHosptialId, PayConstants.PayMethodBit.WxpayScanBit));
				Assert.assertEquals(tpr.getTradeMethodType().intValue(), PayConstants.PayMethod.WxpayScan);
				Assert.assertEquals(tpr.getPayStatus().intValue(), PayConstants.TradeStatus.Successful);
				Assert.assertEquals(tpr.getPayTradeAccountId().intValue(), getTradeAccountByOrderId(orderId));
				Assert.assertNull(tpr.getPayTradeSubaccountId());
				Assert.assertNull(tpr.getPayTradeSubaccountType());
				Assert.assertNull(tpr.getPayTradeAccountSnap());
				Assert.assertEquals(tpr.getReceiveTradeAccountId().intValue(), dbReceiveTradeAccountId);
				Assert.assertEquals(tpr.getReceiveTradeSubaccountId().intValue(),
						getSubAccounttingId(dbReceiveTradeAccountId));
				Assert.assertEquals(tpr.getReceiveTradeSubaccountType().intValue(),
						TradeSubAccountType.TRADE_BALANCE_ACCOUNT);
				// 交易明细表
				List<TradeAccountDetail> tradeAccountList = getTradeAccountDetail(trade_order_num, 0);
				Assert.assertEquals(tradeAccountList.size(), 1);
				TradeAccountDetail tad = tradeAccountList.get(0);
				Assert.assertEquals(tad.getRefBizSn(), tpr.getSn());
				Assert.assertEquals(tad.getTradeAccountId().intValue(), dbReceiveTradeAccountId);
				Assert.assertEquals(tad.getTradeSubAccountType().intValue(), TradeSubAccountType.TRADE_BALANCE_ACCOUNT);
				Assert.assertEquals(tad.getTradeSubAccountId().intValue(),
						getSubAccounttingId(dbReceiveTradeAccountId));
				Assert.assertEquals(tad.getTradeOrderNum(), trade_order_num);
				Assert.assertEquals(tad.getBizType().intValue(), TradeAccountDetailBizType.PAYMENT);
				Assert.assertEquals(tad.getChangeAmount().intValue(), orderprice);

				tradeAccountList = getTradeAccountDetail(trade_order_num, 1);
				Assert.assertEquals(tradeAccountList.size(), 1);
				tad = tradeAccountList.get(0);
				Assert.assertEquals(tad.getRefBizSn(), tpr.getSn());
				Assert.assertEquals(tad.getTradeAccountId().intValue(), getTradeAccountByOrderId(orderId));
				Assert.assertNull(tad.getTradeSubAccountType());
				Assert.assertNull(tad.getTradeSubAccountId());
				Assert.assertEquals(tad.getTradeOrderNum(), trade_order_num);
				Assert.assertEquals(tad.getBizType().intValue(), TradeAccountDetailBizType.PAYMENT);
				Assert.assertEquals(tad.getChangeAmount().intValue(), orderprice);
			}
		}
		if (payMethodType == PayMethodBit.OfflinePayBit) {// 纯线下付款
			if (tradeStatus == PayConstants.TradeStatus.Paying) {
				List<TradePayRecord> tradePayRecList = getTradePayRecordByOrderNum(orderNum, trade_order_num,PayConstants.OrderType.MytijianOrder);
				Assert.assertEquals(tradePayRecList.size(), 2);
				TradePayRecord tpr = tradePayRecList.get(0);
				Assert.assertEquals(tpr.getRefOrderType().intValue(), 1);
				Assert.assertEquals(tpr.getTradeMethodConfigId().intValue(),
						getSuitablePayMethodId(useHosptialId, PayConstants.PayMethodBit.OfflinePayBit));
				Assert.assertEquals(tpr.getTradeMethodType().intValue(), PayConstants.PayMethod.OfflinePay);
				Assert.assertEquals(tpr.getPayStatus().intValue(), PayConstants.TradeStatus.Paying);
				Assert.assertEquals(tpr.getPayTradeAccountId().intValue(), getTradeAccountByOrderId(orderId));
				Assert.assertNull(tpr.getPayTradeSubaccountId());
				Assert.assertNull(tpr.getPayTradeSubaccountType());
				Assert.assertNull(tpr.getPayTradeAccountSnap());
				Assert.assertEquals(tpr.getReceiveTradeAccountId().intValue(), dbReceiveTradeAccountId);
				Assert.assertEquals(tpr.getReceiveTradeSubaccountId().intValue(),
						getSubAccounttingId(dbReceiveTradeAccountId));
				Assert.assertEquals(tpr.getReceiveTradeSubaccountType().intValue(),
						TradeSubAccountType.TRADE_BALANCE_ACCOUNT);
				// 交易明细表
				List<TradeAccountDetail> tradeAccountList = getTradeAccountDetail(trade_order_num);
				Assert.assertEquals(tradeAccountList.size(), 0);
			}
			if (tradeStatus == PayConstants.TradeStatus.Successful) {
				List<TradePayRecord> tradePayRecList = getTradePayRecordByOrderNum(orderNum, trade_order_num,PayConstants.OrderType.MytijianOrder);
				Assert.assertEquals(tradePayRecList.size(), 1);
				TradePayRecord tpr = tradePayRecList.get(0);
				Assert.assertEquals(tpr.getRefOrderType().intValue(), 1);
				Assert.assertEquals(tpr.getTradeMethodConfigId().intValue(),
						getSuitablePayMethodId(useHosptialId, PayConstants.PayMethodBit.OfflinePayBit));
				Assert.assertEquals(tpr.getTradeMethodType().intValue(), PayConstants.PayMethod.OfflinePay);
				Assert.assertEquals(tpr.getPayStatus().intValue(), PayConstants.TradeStatus.Successful);
				Assert.assertEquals(tpr.getPayTradeAccountId().intValue(), getTradeAccountByOrderId(orderId));
				Assert.assertNull(tpr.getPayTradeSubaccountId());
				Assert.assertNull(tpr.getPayTradeSubaccountType());
				Assert.assertNull(tpr.getPayTradeAccountSnap());
				Assert.assertEquals(tpr.getReceiveTradeAccountId().intValue(), dbReceiveTradeAccountId);
				Assert.assertEquals(tpr.getReceiveTradeSubaccountId().intValue(),
						getSubAccounttingId(dbReceiveTradeAccountId));
				Assert.assertEquals(tpr.getReceiveTradeSubaccountType().intValue(),
						TradeSubAccountType.TRADE_BALANCE_ACCOUNT);
				// 交易明细表
				List<TradeAccountDetail> tradeAccountList = getTradeAccountDetail(trade_order_num, 0);
				Assert.assertEquals(tradeAccountList.size(), 0);

				tradeAccountList = getTradeAccountDetail(trade_order_num, 1);
				Assert.assertEquals(tradeAccountList.size(), 0);
			}
		}
		if (payMethodType == PayMethodBit.CardBit + PayMethodBit.BalanceBit) { // 卡+余额
			log.info("not implement!");
		}
	}

	/**
	 * 根据交易的方式和状态，验证交易退款记录
	 * 
	 * @param payMethodType
	 * @param tradeStatus
	 * @throws Exception
	 */
	public static void checkRefundMethodType(Order order, String trade_order_num, int payMethodType, int tradeStatus,
			int useHosptialId, int dbReceiveTradeAccountId,int refundAmont) throws Exception {
		int orderId = order.getId();
		String orderNum = order.getOrderNum();
		int orderprice = order.getOrderPrice();
		if(refundAmont == 0) {
			Assert.assertEquals(getTradeRefundRecordByOrderNum(orderNum,null).size(),0);//退款表无数据
		}
		Assert.assertTrue(refundAmont <=orderprice);
		if (payMethodType == PayMethodBit.CardBit) {// 纯卡
			if (tradeStatus != PayConstants.TradeStatus.Successful)
				System.out.println("交易状态错误...." + tradeStatus);
			int entryCardId = order.getEntryCardId();
			// 交易退款表
			List<TradeRefundRecord> tradeRefundRecList = getTradeRefundRecordByOrderNum(orderNum, trade_order_num);
			Assert.assertEquals(tradeRefundRecList.size(), 1);
			TradeRefundRecord tpr = tradeRefundRecList.get(0);
			Assert.assertEquals(tpr.getRefOrderType().intValue(), 1);
			Assert.assertEquals(tpr.getTradeMethodConfigId().intValue(),
					getSuitablePayMethodId(order.getFromSite(), PayConstants.PayMethodBit.CardBit));
			Assert.assertEquals(tpr.getTradeMethodType().intValue(), PayConstants.PayMethod.Card);
			Assert.assertEquals(tpr.getRefundStatus().intValue(),
					RefundConstants.RefundStatus.REFUND_SUCCESS.intValue());
			Assert.assertEquals(tpr.getReceiveTradeAccountId().intValue(), getTradeAccountByOrderId(orderId));
			Assert.assertEquals(tpr.getReceiveTradeSubAccountId().intValue(),
					getSubAccounttingId(getTradeAccountByOrderId(orderId)));
			Assert.assertEquals(tpr.getReceiveTradeSubAccountType().intValue(), TradeSubAccountType.TRADE_CARD_ACCOUNT);
			Assert.assertEquals(tpr.getRefundTradeAccountId().intValue(), dbReceiveTradeAccountId);
			Assert.assertEquals(tpr.getRefundTradeSubAccountId().intValue(),
					getSubAccounttingId(dbReceiveTradeAccountId));
			Assert.assertEquals(tpr.getRefundTradeSubAccountType().intValue(),
					TradeSubAccountType.TRADE_BALANCE_ACCOUNT);
			// 交易明细表
			List<TradeAccountDetail> tradeAccountList = getTradeAccountDetail(trade_order_num, 1);
			Assert.assertEquals(tradeAccountList.size(), 1);
			TradeAccountDetail tad = tradeAccountList.get(0);
			Assert.assertEquals(tad.getRefBizSn(), tpr.getSn());
			Assert.assertEquals(tad.getTradeAccountId().intValue(), dbReceiveTradeAccountId);
			Assert.assertEquals(tad.getTradeSubAccountType().intValue(), TradeSubAccountType.TRADE_BALANCE_ACCOUNT);
			Assert.assertEquals(tad.getTradeSubAccountId().intValue(), getSubAccounttingId(dbReceiveTradeAccountId));
			Assert.assertEquals(tad.getTradeOrderNum(), trade_order_num);
			Assert.assertEquals(tad.getBizType().intValue(), TradeAccountDetailBizType.REFUND);
			Assert.assertEquals(tad.getChangeAmount().intValue(), refundAmont);

			tradeAccountList = getTradeAccountDetail(trade_order_num, 0);
			Assert.assertEquals(tradeAccountList.size(), 1);
			tad = tradeAccountList.get(0);
			Assert.assertEquals(tad.getRefBizSn(), tpr.getSn());
			Assert.assertEquals(tad.getTradeAccountId().intValue(), getTradeAccountByOrderId(orderId));
			Assert.assertEquals(tad.getTradeSubAccountType().intValue(), TradeSubAccountType.TRADE_CARD_ACCOUNT);
			Assert.assertEquals(tad.getTradeSubAccountId().intValue(), entryCardId);
			Assert.assertEquals(tad.getTradeOrderNum(), trade_order_num);
			Assert.assertEquals(tad.getBizType().intValue(), TradeAccountDetailBizType.REFUND);
			Assert.assertEquals(tad.getChangeAmount().intValue(), refundAmont);
		}
		if (payMethodType == PayMethodBit.ParentCardBit) {// 纯母卡
			log.info("...存母卡..");
			if (tradeStatus != PayConstants.TradeStatus.Successful)
				System.out.println("交易状态错误...." + tradeStatus);
			int entryCardId = order.getEntryCardId();
			// 交易退款表
			List<TradeRefundRecord> tradeRefundRecList = getTradeRefundRecordByOrderNum(orderNum, trade_order_num);
			Assert.assertEquals(tradeRefundRecList.size(), 1);
			TradeRefundRecord tpr = tradeRefundRecList.get(0);
			Assert.assertEquals(tpr.getRefOrderType().intValue(), 1);
			Assert.assertEquals(tpr.getTradeMethodConfigId().intValue(),
					getSuitablePayMethodId(order.getFromSite(), PayConstants.PayMethodBit.ParentCardBit));
			Assert.assertEquals(tpr.getTradeMethodType().intValue(), PayConstants.PayMethod.ParentCard);
			Assert.assertEquals(tpr.getRefundStatus().intValue(),
					RefundConstants.RefundStatus.REFUND_SUCCESS.intValue());
			int receiveTradeAccountId = getTradeAccountIdByAccountId((OrderChecker.getOrderInfo(orderId).getOperatorId().intValue()));
			Assert.assertEquals(tpr.getReceiveTradeAccountId().intValue(),receiveTradeAccountId );
			Assert.assertEquals(tpr.getReceiveTradeSubAccountId().intValue(),
					getSubCardIdList(receiveTradeAccountId).get(0).intValue());
			Assert.assertEquals(tpr.getReceiveTradeSubAccountType().intValue(), TradeSubAccountType.TRADE_PARENT_CARD_ACCOUNT);
			Assert.assertEquals(tpr.getRefundTradeAccountId().intValue(), dbReceiveTradeAccountId);
			Assert.assertEquals(tpr.getRefundTradeSubAccountId().intValue(),
					getSubAccounttingId(dbReceiveTradeAccountId));
			Assert.assertEquals(tpr.getRefundTradeSubAccountType().intValue(),
					TradeSubAccountType.TRADE_BALANCE_ACCOUNT);
			// 交易明细表
			List<TradeAccountDetail> tradeAccountList = getTradeAccountDetail(trade_order_num, 1);
			Assert.assertEquals(tradeAccountList.size(), 1);
			TradeAccountDetail tad = tradeAccountList.get(0);
			Assert.assertEquals(tad.getRefBizSn(), tpr.getSn());
			Assert.assertEquals(tad.getTradeAccountId().intValue(), dbReceiveTradeAccountId);
			Assert.assertEquals(tad.getTradeSubAccountType().intValue(), TradeSubAccountType.TRADE_BALANCE_ACCOUNT);
			Assert.assertEquals(tad.getTradeSubAccountId().intValue(), getSubAccounttingId(dbReceiveTradeAccountId));
			Assert.assertEquals(tad.getTradeOrderNum(), trade_order_num);
			Assert.assertEquals(tad.getBizType().intValue(), TradeAccountDetailBizType.REFUND);
			Assert.assertEquals(tad.getChangeAmount().intValue(), refundAmont);

			tradeAccountList = getTradeAccountDetail(trade_order_num, 0);
			Assert.assertEquals(tradeAccountList.size(), 1);
			tad = tradeAccountList.get(0);
			Assert.assertEquals(tad.getRefBizSn(), tpr.getSn());
			Assert.assertEquals(tad.getTradeAccountId().intValue(), receiveTradeAccountId);
			Assert.assertEquals(tad.getTradeSubAccountType().intValue(), TradeSubAccountType.TRADE_PARENT_CARD_ACCOUNT);
			Assert.assertEquals(tad.getTradeSubAccountId().intValue(), entryCardId);
			Assert.assertEquals(tad.getTradeOrderNum(), trade_order_num);
			Assert.assertEquals(tad.getBizType().intValue(), TradeAccountDetailBizType.REFUND);
			Assert.assertEquals(tad.getChangeAmount().intValue(), refundAmont);
		}
		if (payMethodType == PayMethodBit.BalanceBit) {// 存余额
			if (tradeStatus != PayConstants.TradeStatus.Successful)
				System.out.println("交易状态错误...." + tradeStatus);
			// 交易支付表
			List<TradeRefundRecord> tradeRefundRecList = getTradeRefundRecordByOrderNum(orderNum, trade_order_num);
			Assert.assertEquals(tradeRefundRecList.size(), 1);
			TradeRefundRecord tpr = tradeRefundRecList.get(0);
			Assert.assertEquals(tpr.getRefOrderType().intValue(), 1);
			Assert.assertEquals(tpr.getTradeMethodConfigId().intValue(),
					getSuitablePayMethodId(order.getFromSite(), PayConstants.PayMethodBit.BalanceBit));
			Assert.assertEquals(tpr.getTradeMethodType().intValue(), PayConstants.PayMethod.Balance);
			Assert.assertEquals(tpr.getRefundStatus().intValue(),
					RefundConstants.RefundStatus.REFUND_SUCCESS.intValue());
			Assert.assertEquals(tpr.getReceiveTradeAccountId().intValue(), getTradeAccountByOrderId(orderId));
			Assert.assertEquals(tpr.getReceiveTradeSubAccountId().intValue(),
					getSubAccounttingId(getTradeAccountByOrderId(orderId)));
			Assert.assertEquals(tpr.getReceiveTradeSubAccountType().intValue(),
					TradeSubAccountType.TRADE_BALANCE_ACCOUNT);
			Assert.assertEquals(tpr.getRefundTradeAccountId().intValue(), dbReceiveTradeAccountId);
			Assert.assertEquals(tpr.getRefundTradeSubAccountId().intValue(),
					getSubAccounttingId(dbReceiveTradeAccountId));
			Assert.assertEquals(tpr.getRefundTradeSubAccountType().intValue(),
					TradeSubAccountType.TRADE_BALANCE_ACCOUNT);
			// 交易明细表
			List<TradeAccountDetail> tradeAccountList = getTradeAccountDetail(trade_order_num, 1);
			Assert.assertEquals(tradeAccountList.size(), 1);
			TradeAccountDetail tad = tradeAccountList.get(0);
			Assert.assertEquals(tad.getRefBizSn(), tpr.getSn());
			Assert.assertEquals(tad.getTradeAccountId().intValue(), dbReceiveTradeAccountId);
			Assert.assertEquals(tad.getTradeSubAccountType().intValue(), TradeSubAccountType.TRADE_BALANCE_ACCOUNT);
			Assert.assertEquals(tad.getTradeSubAccountId().intValue(), getSubAccounttingId(dbReceiveTradeAccountId));
			Assert.assertEquals(tad.getTradeOrderNum(), trade_order_num);
			Assert.assertEquals(tad.getBizType().intValue(), TradeAccountDetailBizType.REFUND);
			Assert.assertEquals(tad.getChangeAmount().intValue(), refundAmont);

			tradeAccountList = getTradeAccountDetail(trade_order_num, 0);
			Assert.assertEquals(tradeAccountList.size(), 1);
			tad = tradeAccountList.get(0);
			Assert.assertEquals(tad.getRefBizSn(), tpr.getSn());
			Assert.assertEquals(tad.getTradeAccountId().intValue(), getTradeAccountByOrderId(orderId));
			Assert.assertEquals(tad.getTradeSubAccountType().intValue(), TradeSubAccountType.TRADE_BALANCE_ACCOUNT);
			Assert.assertEquals(tad.getTradeSubAccountId().intValue(),
					getSubAccounttingId(getTradeAccountByOrderId(orderId)));
			Assert.assertEquals(tad.getTradeOrderNum(), trade_order_num);
			Assert.assertEquals(tad.getBizType().intValue(), TradeAccountDetailBizType.REFUND);
			Assert.assertEquals(tad.getChangeAmount().intValue(), refundAmont);
		}
		if (payMethodType == PayMethodBit.AlipayBit) {// 纯支付宝
			if (tradeStatus == PayConstants.TradeStatus.Paying) {
				List<TradeRefundRecord> tradeRefundList = getTradeRefundRecordByOrderNum(orderNum, trade_order_num);
				Assert.assertEquals(tradeRefundList.size(), 0);
				List<TradeAccountDetail> tradeAccountList = getTradeAccountDetail(trade_order_num);
				Assert.assertEquals(tradeAccountList.size(), 0);
			}
		}
		if (payMethodType == PayMethodBit.WxpayBit) {// 纯微信
			if (tradeStatus == PayConstants.TradeStatus.Paying) {
				List<TradeRefundRecord> tradeRefundList = getTradeRefundRecordByOrderNum(orderNum, trade_order_num);
				Assert.assertEquals(tradeRefundList.size(), 0);
				List<TradeAccountDetail> tradeAccountList = getTradeAccountDetail(trade_order_num);
				Assert.assertEquals(tradeAccountList.size(), 0);
			}
		}
		if (payMethodType == PayMethodBit.AlipayScanBit + PayMethodBit.OnlinePayBit) {// 纯支付宝扫码
			if (tradeStatus == PayConstants.TradeStatus.Paying) {
				List<TradeRefundRecord> tradeRefundList = getTradeRefundRecordByOrderNum(orderNum, trade_order_num);
				Assert.assertEquals(tradeRefundList.size(), 0);
				List<TradeAccountDetail> tradeAccountList = getTradeAccountDetail(trade_order_num);
				Assert.assertEquals(tradeAccountList.size(), 0);
			}
			if (tradeStatus == PayConstants.TradeStatus.Successful) {
				List<TradeRefundRecord> tradeRefundList = getTradeRefundRecordByOrderNum(orderNum, trade_order_num);
				Assert.assertEquals(tradeRefundList.size(), 1);
				TradeRefundRecord tpr = tradeRefundList.get(0);
				Assert.assertEquals(tpr.getRefOrderType().intValue(), 1);
				Assert.assertEquals(tpr.getTradeMethodConfigId().intValue(),
						getSuitablePayMethodId(order.getFromSite(), PayConstants.PayMethodBit.AlipayScanBit));
				Assert.assertEquals(tpr.getTradeMethodType().intValue(), PayConstants.PayMethod.AlipayScan);
				Assert.assertEquals(tpr.getRefundStatus().intValue(),
						RefundConstants.RefundStatus.REFUND_SUCCESS.intValue());
				Assert.assertEquals(tpr.getReceiveTradeAccountId().intValue(), getTradeAccountByOrderId(orderId));
				Assert.assertNull(tpr.getReceiveTradeSubAccountId());
				Assert.assertNull(tpr.getReceiveTradeSubAccountType());
				Assert.assertNull(tpr.getReceiveTradeAccountSnap());
				Assert.assertEquals(tpr.getRefundTradeAccountId().intValue(), dbReceiveTradeAccountId);
				Assert.assertEquals(tpr.getRefundTradeSubAccountId().intValue(),
						getSubAccounttingId(dbReceiveTradeAccountId));
				Assert.assertEquals(tpr.getRefundTradeSubAccountType().intValue(),
						TradeSubAccountType.TRADE_BALANCE_ACCOUNT);

				// 交易明细表
				List<TradeAccountDetail> tradeAccountList = getTradeAccountDetail(trade_order_num, 1);
				Assert.assertEquals(tradeAccountList.size(), 1);
				TradeAccountDetail tad = tradeAccountList.get(0);
				Assert.assertEquals(tad.getRefBizSn(), tpr.getSn());
				Assert.assertEquals(tad.getTradeAccountId().intValue(), dbReceiveTradeAccountId);
				Assert.assertEquals(tad.getTradeSubAccountType().intValue(), TradeSubAccountType.TRADE_BALANCE_ACCOUNT);
				Assert.assertEquals(tad.getTradeSubAccountId().intValue(),
						getSubAccounttingId(dbReceiveTradeAccountId));
				Assert.assertEquals(tad.getTradeOrderNum(), trade_order_num);
				Assert.assertEquals(tad.getBizType().intValue(), TradeAccountDetailBizType.REFUND);
				Assert.assertEquals(tad.getChangeAmount().intValue(), refundAmont);

				tradeAccountList = getTradeAccountDetail(trade_order_num, 0);
				Assert.assertEquals(tradeAccountList.size(), 0);
			}
		}
		if (payMethodType == PayMethodBit.WxpayScanBit + PayMethodBit.OnlinePayBit) {// 纯微信扫码
			if (tradeStatus == PayConstants.TradeStatus.Paying) {
				List<TradeRefundRecord> tradeRefundList = getTradeRefundRecordByOrderNum(orderNum, trade_order_num);
				Assert.assertEquals(tradeRefundList.size(), 0);
				List<TradeAccountDetail> tradeAccountList = getTradeAccountDetail(trade_order_num);
				Assert.assertEquals(tradeAccountList.size(), 0);
			}
			if (tradeStatus == PayConstants.TradeStatus.Successful) {
				List<TradeRefundRecord> tradeRefundList = getTradeRefundRecordByOrderNum(orderNum, trade_order_num);
				Assert.assertEquals(tradeRefundList.size(), 1);
				TradeRefundRecord tpr = tradeRefundList.get(0);
				Assert.assertEquals(tpr.getRefOrderType().intValue(), 1);
				Assert.assertEquals(tpr.getTradeMethodConfigId().intValue(),
						getSuitablePayMethodId(order.getFromSite(), PayConstants.PayMethodBit.WxpayScanBit));
				Assert.assertEquals(tpr.getTradeMethodType().intValue(), PayConstants.PayMethod.WxpayScan);
				Assert.assertEquals(tpr.getRefundStatus().intValue(),
						RefundConstants.RefundStatus.REFUND_SUCCESS.intValue());
				Assert.assertEquals(tpr.getReceiveTradeAccountId().intValue(), getTradeAccountByOrderId(orderId));
				Assert.assertNull(tpr.getReceiveTradeSubAccountId());
				Assert.assertNull(tpr.getReceiveTradeSubAccountType());
				Assert.assertNull(tpr.getReceiveTradeAccountSnap());
				Assert.assertEquals(tpr.getRefundTradeAccountId().intValue(), dbReceiveTradeAccountId);
				Assert.assertEquals(tpr.getRefundTradeSubAccountId().intValue(),
						getSubAccounttingId(dbReceiveTradeAccountId));
				Assert.assertEquals(tpr.getRefundTradeSubAccountType().intValue(),
						TradeSubAccountType.TRADE_BALANCE_ACCOUNT);

				// 交易明细表
				List<TradeAccountDetail> tradeAccountList = getTradeAccountDetail(trade_order_num, 1);
				Assert.assertEquals(tradeAccountList.size(), 1);
				TradeAccountDetail tad = tradeAccountList.get(0);
				Assert.assertEquals(tad.getRefBizSn(), tpr.getSn());
				Assert.assertEquals(tad.getTradeAccountId().intValue(), dbReceiveTradeAccountId);
				Assert.assertEquals(tad.getTradeSubAccountType().intValue(), TradeSubAccountType.TRADE_BALANCE_ACCOUNT);
				Assert.assertEquals(tad.getTradeSubAccountId().intValue(),
						getSubAccounttingId(dbReceiveTradeAccountId));
				Assert.assertEquals(tad.getTradeOrderNum(), trade_order_num);
				Assert.assertEquals(tad.getBizType().intValue(), TradeAccountDetailBizType.REFUND);
				Assert.assertEquals(tad.getChangeAmount().intValue(), refundAmont);

				tradeAccountList = getTradeAccountDetail(trade_order_num, 0);
				Assert.assertEquals(tradeAccountList.size(), 0);
			}
		}
		if (payMethodType == PayMethodBit.OfflinePayBit) {// 纯线下付款
			if (tradeStatus == PayConstants.TradeStatus.Paying) {
				List<TradeRefundRecord> tradeRefundList = getTradeRefundRecordByOrderNum(orderNum, trade_order_num);
				Assert.assertEquals(tradeRefundList.size(), 0);
				List<TradeAccountDetail> tradeAccountList = getTradeAccountDetail(trade_order_num);
				Assert.assertEquals(tradeAccountList.size(), 0);
			}
			if (tradeStatus == PayConstants.TradeStatus.Successful) {
				List<TradeRefundRecord> tradeRefundList = getTradeRefundRecordByOrderNum(orderNum, trade_order_num);
				Assert.assertEquals(tradeRefundList.size(), 1);
				TradeRefundRecord tpr = tradeRefundList.get(0);
				Assert.assertEquals(tpr.getRefOrderType().intValue(), 1);
				Assert.assertEquals(tpr.getTradeMethodConfigId().intValue(),
						getSuitablePayMethodId(order.getFromSite(), PayConstants.PayMethodBit.OfflinePayBit));
				Assert.assertEquals(tpr.getTradeMethodType().intValue(), PayConstants.PayMethod.OfflinePay);
				Assert.assertEquals(tpr.getRefundStatus().intValue(),
						RefundConstants.RefundStatus.REFUND_SUCCESS.intValue());
				Assert.assertEquals(tpr.getReceiveTradeAccountId().intValue(), getTradeAccountByOrderId(orderId));
				Assert.assertNull(tpr.getReceiveTradeSubAccountId());
				Assert.assertNull(tpr.getReceiveTradeSubAccountType());
				Assert.assertNull(tpr.getReceiveTradeAccountSnap());
				Assert.assertEquals(tpr.getRefundTradeAccountId().intValue(), dbReceiveTradeAccountId);
				Assert.assertEquals(tpr.getRefundTradeSubAccountId().intValue(),
						getSubAccounttingId(dbReceiveTradeAccountId));
				Assert.assertEquals(tpr.getRefundTradeSubAccountType().intValue(),
						TradeSubAccountType.TRADE_BALANCE_ACCOUNT);
				// 交易明细表
				List<TradeAccountDetail> tradeAccountList = getTradeAccountDetail(trade_order_num);
				Assert.assertEquals(tradeAccountList.size(), 0);
			}
		}
		if (payMethodType == PayMethodBit.CardBit + PayMethodBit.BalanceBit) { // 卡+余额
			log.info("not implement!");
		}
	}
	
	/**
	 * 校验支付订单的交易数据
	 * 
	 * @param orderId
	 * @throws Exception
	 */
	public  static void checkPayTrade(int orderId, int hospitalId, int receiveTradeAccountId) throws Exception {
		String orderSql = "select * from tb_order where id = ?";
		List<Map<String, Object>> orderList = DBMapper.query(orderSql, orderId);
		String orderNum = orderList.get(0).get("order_num").toString();
		int orderprice = Integer.parseInt(orderList.get(0).get("order_price").toString());
		int orderStatus = Integer.parseInt(orderList.get(0).get("status").toString());

		/************** 提取支付数据 **********************/
		// 【交易订单表】
		List<TradeOrder> tradeOrderList = getTradeOrderByOrderNum(orderNum, TradeType.pay);
		if ((orderStatus == 2) || (orderStatus == 1)) {
			Assert.assertTrue(tradeOrderList.size() >= 1);
			if (tradeOrderList.size() > 1)
				return;// 有多比笔交易
			TradeOrder to = tradeOrderList.get(0);
			Assert.assertEquals(to.getRefOrderType().intValue(), 1);
			Assert.assertEquals(to.getAmount().longValue(), orderprice);
			Assert.assertEquals(to.getSuccAmount().longValue(), orderprice);
			Assert.assertEquals(to.getTradeStatus().intValue(), PayConstants.TradeStatus.Successful);
			Assert.assertEquals(to.getTradeType().intValue(), PayConstants.TradeType.pay);
			int payMethodType = to.getPayMethodType().intValue();
			checkPayMethodType(orderId, to.getTradeOrderNum(), payMethodType, to.getTradeStatus().intValue(),
					hospitalId, receiveTradeAccountId);
		}
		if (orderStatus == 7) {
			if (tradeOrderList.size() > 1)
				return; // 多条交易的略过
			TradeOrder to = tradeOrderList.get(0);
			Assert.assertEquals(to.getRefOrderType().intValue(), 1);
			Assert.assertEquals(to.getAmount().longValue(), orderprice);// 需要支付金额
			Assert.assertEquals(to.getSuccAmount().longValue(), 0);// 成功支付0
			Assert.assertEquals(to.getTradeStatus().intValue(), PayConstants.TradeStatus.Paying);
			Assert.assertEquals(to.getTradeType().intValue(), PayConstants.TradeType.pay);
			int payMethodType = to.getPayMethodType().intValue();
			checkPayMethodType(orderId, to.getTradeOrderNum(), payMethodType, to.getTradeStatus().intValue(),
					hospitalId, receiveTradeAccountId);
		}

		if (orderStatus == 11) {
			if (tradeOrderList.size() > 1)
				return; // 多条交易的略过
			TradeOrder to = tradeOrderList.get(0);
			Assert.assertEquals(to.getRefOrderType().intValue(), 1);
			Assert.assertEquals(to.getAmount().longValue(), orderprice);// 需要支付金额
			Assert.assertEquals(to.getSuccAmount().longValue(), 0);// 成功支付0
			Assert.assertEquals(to.getTradeStatus().intValue(), PayConstants.TradeStatus.Paying);
			Assert.assertEquals(to.getTradeType().intValue(), PayConstants.TradeType.pay);
			int payMethodType = to.getPayMethodType().intValue();
			checkPayMethodType(orderId, to.getTradeOrderNum(), payMethodType, to.getTradeStatus().intValue(),
					hospitalId, receiveTradeAccountId);
		}
	}

	/**
	 * 传入撤销订单之前的订单id,订单状态，医院id,以及退款人总账户id
	 * 
	 * @param order
	 * @param orderStatus
	 * @throws Exception
	 */
	public static void checkRevokeTrade(Order order, int orderStatus) throws Exception {
		int orderprice = order.getOrderPrice();
		int hospitalId = order.getHospital().getId();
		String orderNum = order.getOrderNum();

		/************** 提取退款数据 **********************/
		// 【交易订单表】
		if ((orderStatus == 2) || (orderStatus == 1)) {
			List<TradeOrder> tradeOrderList = getTradeOrderByOrderNum(orderNum, TradeType.refund);
			Assert.assertTrue(tradeOrderList.size() == 1);// 有且一笔退款
			TradeOrder to = tradeOrderList.get(0);
			Assert.assertEquals(to.getRefOrderType().intValue(), 1);
			Assert.assertEquals(to.getAmount().longValue(), orderprice);
			Assert.assertEquals(to.getSuccAmount().longValue(), orderprice);
			Assert.assertEquals(to.getTradeStatus().intValue(), PayConstants.TradeStatus.Successful);
			Assert.assertEquals(to.getTradeType().intValue(), PayConstants.TradeType.refund);
			List<TradeOrder> tradeOrderPayList = getTradeOrderByOrderNum(orderNum, TradeType.pay);
			int payMethodType = tradeOrderPayList.get(0).getPayMethodType().intValue();// 获取支付方式
			List<TradePayRecord> tradePayRecords = getTradePayRecordByOrderNum(orderNum, tradeOrderPayList.get(0).getTradeOrderNum(),PayConstants.OrderType.MytijianOrder);
			int refundAccountId = tradePayRecords.get(0).getReceiveTradeAccountId();
			checkRefundMethodType(order, to.getTradeOrderNum(), payMethodType, to.getTradeStatus().intValue(),
					hospitalId,refundAccountId ,to.getSuccAmount().intValue());
		}
		if (orderStatus == 7) {
			List<TradeOrder> tradeOrderList = getTradeOrderByOrderNum(orderNum, TradeType.pay);
			Assert.assertTrue(tradeOrderList.size() == 1);// 有且一笔支付,未成功支付
			TradeOrder to = tradeOrderList.get(0);
			Assert.assertEquals(to.getRefOrderType().intValue(), 1);
			Assert.assertEquals(to.getAmount().longValue(), orderprice);// 需要支付金额
			Assert.assertEquals(to.getTradeStatus().intValue(), PayConstants.TradeStatus.Paying);
			Assert.assertEquals(to.getTradeType().intValue(), PayConstants.TradeType.pay);
			
			List<TradeOrder> refundTradeOrderList = getTradeOrderByOrderNum(orderNum, TradeType.refund);//退款
			if(to.getSuccAmount() ==0){
				//无退款记录
				Assert.assertTrue(refundTradeOrderList.size() == 0);
				Assert.assertTrue(getTradeRefundRecordByOrderNum(orderNum, null).size()==0);
			}else{
				//有退款记录
				TradeOrder tox = refundTradeOrderList.get(0);
				Assert.assertTrue(refundTradeOrderList.size() == 1);
				Assert.assertEquals(tox.getRefOrderType().intValue(), 1);
				Assert.assertEquals(tox.getAmount().longValue(), to.getSuccAmount().longValue());
				Assert.assertEquals(tox.getSuccAmount().longValue(), to.getSuccAmount().longValue());
				Assert.assertEquals(tox.getTradeStatus().intValue(), PayConstants.TradeStatus.Successful);
				Assert.assertEquals(tox.getTradeType().intValue(), PayConstants.TradeType.refund);
				List<TradeOrder> tradeOrderPayList = getTradeOrderByOrderNum(orderNum, TradeType.pay);
				int payMethodType = tradeOrderPayList.get(0).getPayMethodType().intValue();// 获取支付方式
				List<TradePayRecord> tradePayRecords = getTradePayRecordByOrderNum(orderNum, tradeOrderPayList.get(0).getTradeOrderNum(),PayConstants.OrderType.MytijianOrder);
				int refundAccountId = tradePayRecords.get(0).getReceiveTradeAccountId();
				checkRefundMethodType(order, tox.getTradeOrderNum(), payMethodType, tox.getTradeStatus().intValue(),
						hospitalId, refundAccountId,tox.getSuccAmount().intValue());
			}
		}

		if (orderStatus == 11) {
			List<TradeOrder> tradeOrderList = getTradeOrderByOrderNum(orderNum, TradeType.pay);
			Assert.assertTrue(tradeOrderList.size() == 1);// 有且一笔退款
			TradeOrder to = tradeOrderList.get(0);
			Assert.assertEquals(to.getRefOrderType().intValue(), 1);
			Assert.assertEquals(to.getAmount().longValue(), orderprice);// 需要支付金额
			Assert.assertEquals(to.getTradeStatus().intValue(), PayConstants.TradeStatus.Paying);
			Assert.assertEquals(to.getTradeType().intValue(), PayConstants.TradeType.pay);
	
			List<TradeOrder> refundTradeOrderList = getTradeOrderByOrderNum(orderNum, TradeType.refund);//退款
			if(to.getSuccAmount() ==0){
				//无退款记录
				Assert.assertTrue(refundTradeOrderList.size() == 0);
				Assert.assertTrue(getTradeRefundRecordByOrderNum(orderNum, null).size()==0);
			}else{
				//有退款记录
				TradeOrder tox = refundTradeOrderList.get(0);
				Assert.assertTrue(refundTradeOrderList.size() == 1);
				Assert.assertEquals(tox.getRefOrderType().intValue(), 1);
				Assert.assertEquals(tox.getAmount().longValue(), to.getSuccAmount().longValue());
				Assert.assertEquals(tox.getSuccAmount().longValue(), to.getSuccAmount().longValue());
				Assert.assertEquals(tox.getTradeStatus().intValue(), PayConstants.TradeStatus.Successful);
				Assert.assertEquals(tox.getTradeType().intValue(), PayConstants.TradeType.refund);
				List<TradeOrder> tradeOrderPayList = getTradeOrderByOrderNum(orderNum, TradeType.pay);
				int payMethodType = tradeOrderPayList.get(0).getPayMethodType().intValue();// 获取支付方式
				List<TradePayRecord> tradePayRecords = getTradePayRecordByOrderNum(orderNum, tradeOrderPayList.get(0).getTradeOrderNum(),PayConstants.OrderType.MytijianOrder);
				int refundAccountId = tradePayRecords.get(0).getReceiveTradeAccountId();
				checkRefundMethodType(order, tox.getTradeOrderNum(), payMethodType, tox.getTradeStatus().intValue(),
						hospitalId, refundAccountId,tox.getSuccAmount().intValue());
			}
		
		}
	}
	
	/**
	 * 传入回单部分退款订单的订单id,以及退款人总账户id,退款金额
	 * 
	 * @param order
	 * @param refundTradeAccountId
	 * 	@param refundAmount
	 * @throws Exception
	 */
	public static void checkPartBackTrade(Order order,  int refundTradeAccountId,int refundAmount) throws Exception {
		int orderprice = order.getOrderPrice();
		int hospitalId = order.getHospital().getId();
		String orderNum = order.getOrderNum();
		/************** 提取退款数据 **********************/
		// 【交易订单表】
		List<TradeOrder> tradeOrderList = getTradeOrderByOrderNum(orderNum, TradeType.refund);
		Assert.assertTrue(tradeOrderList.size() == 1);// 有且一笔退款
		TradeOrder to = tradeOrderList.get(0);
		Assert.assertEquals(to.getRefOrderType().intValue(), 1);
		Assert.assertEquals(to.getAmount().longValue(), orderprice);
		Assert.assertEquals(to.getSuccAmount().longValue(), refundAmount);
		Assert.assertEquals(to.getTradeStatus().intValue(), PayConstants.TradeStatus.Successful);
		Assert.assertEquals(to.getTradeType().intValue(), PayConstants.TradeType.refund);
		int payMethodType = getTradeOrderByOrderNum(orderNum, TradeType.pay).get(0).getPayMethodType().intValue();// 获取支付方式
		checkRefundMethodType(order, to.getTradeOrderNum(), payMethodType, to.getTradeStatus().intValue(),
					hospitalId, refundTradeAccountId,refundAmount);
	}
	

	/**
	 * 检查CRM直接预约，改项预约|散客单位，非散客单位
	 * 
	 * @throws Exception
	 */
	public static void checkCrmBatchOrderTradeOrder(int usehospitalId, HospitalCompany hCompany, Order order, int defaccountId)
			throws Exception {
		int need_local_pay = Integer.parseInt(HospitalChecker.getHospitalSetting(usehospitalId, HospitalParam.NEED_LOCAL_PAY)
				.get(HospitalParam.NEED_LOCAL_PAY).toString());
		if (hCompany.getPlatformCompanyId()!=null&&hCompany.getPlatformCompanyId() == 2) {
			if (need_local_pay == 1) {
				// 检查交易状态
				// 验证订单操作日志
				System.out.println("--------验证操作日志开始---------");
				List<ExamOrderOperateLogDO> logs = OrderChecker.getOrderOperatrLog(order.getOrderNum());
				Assert.assertEquals(logs.get(0).getType().intValue(), OrderOperateTypeEnum.FINISH_PAY.getCode());
				Assert.assertEquals(logs.get(0).getOrderStatus().intValue(), OrderStatus.SITE_PAY.intValue());
				System.out.println("--------验证操作日志结束---------");

				System.out.println("--------验证交易记录开始---------");
				// 校验tb_paymentrecord
				String paymentSql = "select p.* from tb_paymentrecord p ,tb_payment_method m  where p.order_id = "
						+ order.getId() + " and p.payment_method_id = m.id and m.type = "
						+ PaymentTypeEnum.Offline.getCode();
				List<Map<String, Object>> paymentList = DBMapper.query(paymentSql);
				Assert.assertEquals(paymentList.size(), 1);
				Map<String, Object> paymentMap = paymentList.get(0);
				Assert.assertEquals(Integer.parseInt(paymentMap.get("status").toString()), 0); // 未支付
				Assert.assertEquals(Integer.parseInt(paymentMap.get("trade_type").toString()), 1); // 支付类型
				Assert.assertEquals(Integer.parseInt(paymentMap.get("is_primary").toString()), 0);
				// 校验tb_trade_order【交易订单表】
				List<TradeOrder> tradeOrderList = getTradeOrderByOrderNum(order.getOrderNum(), TradeType.pay);
				Assert.assertEquals(tradeOrderList.size(), 1); // 下单
				TradeOrder to = tradeOrderList.get(0);
				Assert.assertEquals(to.getRefOrderType().intValue(), 1);
				Assert.assertEquals(to.getAmount().longValue(), order.getOrderPrice().longValue());
				Assert.assertEquals(to.getSuccAmount().longValue(), 0l);
				Assert.assertEquals(to.getTradeStatus().intValue(), PayConstants.TradeStatus.Paying);
				Assert.assertEquals(to.getPayMethodType().intValue(), PayConstants.PayMethodBit.OfflinePayBit);
				Assert.assertEquals(to.getTradeType().intValue(), PayConstants.TradeType.pay);

				// 校验tb_trade_pay_record【交易支付表】
				int dbReceiveTradeAccountId = getSuitableReceiveMethodId(usehospitalId,
						PayConstants.PayMethodBit.OfflinePayBit);
				List<TradePayRecord> tradePayRecList = getTradePayRecordByOrderNum(order.getOrderNum(),
						to.getTradeOrderNum(),PayConstants.OrderType.MytijianOrder);
				Assert.assertEquals(tradePayRecList.size(), 1);
				TradePayRecord tpr = tradePayRecList.get(0);
				Assert.assertEquals(tpr.getRefOrderType().intValue(), 1);
				Assert.assertEquals(tpr.getTradeMethodConfigId().intValue(),
						getSuitablePayMethodId(order.getFromSite(), PayConstants.PayMethodBit.OfflinePayBit));
				Assert.assertEquals(tpr.getTradeMethodType().intValue(), PayConstants.PayMethod.OfflinePay);
				Assert.assertEquals(tpr.getPayStatus().intValue(), PayConstants.TradeStatus.Paying);
				Assert.assertEquals(tpr.getPayTradeAccountId().intValue(), getTradeAccountByOrderId(order.getId()));
				Assert.assertNull(tpr.getPayTradeSubaccountId());
				Assert.assertNull(tpr.getPayTradeSubaccountType());
				Assert.assertNull(tpr.getPayTradeAccountSnap());
				Assert.assertEquals(tpr.getReceiveTradeAccountId().intValue(), dbReceiveTradeAccountId);
				Assert.assertEquals(tpr.getReceiveTradeSubaccountId().intValue(),
						getSubAccounttingId(dbReceiveTradeAccountId));
				Assert.assertEquals(tpr.getReceiveTradeSubaccountType().intValue(),
						TradeSubAccountType.TRADE_BALANCE_ACCOUNT);

				// 交易tb_trade_account_detail,支付中不往这个表插入数据【账户明细表】
				List<TradeAccountDetail> tradeAccountList = getTradeAccountDetail(to.getTradeOrderNum(), 0);
				Assert.assertEquals(tradeAccountList.size(), 0);
				// 出账
				tradeAccountList = getTradeAccountDetail(to.getTradeOrderNum(), 1);
				Assert.assertEquals(tradeAccountList.size(), 0);
				System.out.println("--------验证交易记录结束---------");
			} else {
				// 检查交易状态
				// 验证订单操作日志
				System.out.println("--------验证操作日志开始---------");
				List<ExamOrderOperateLogDO> logs = OrderChecker.getOrderOperatrLog(order.getOrderNum());
				Assert.assertEquals(logs.get(0).getType().intValue(), OrderOperateTypeEnum.FINISH_PAY.getCode());
				Assert.assertEquals(logs.get(0).getOrderStatus().intValue(), OrderStatus.ALREADY_BOOKED.intValue());
				System.out.println("--------验证操作日志结束---------");

				System.out.println("--------验证交易记录开始---------");
				// 校验tb_paymentrecord
				String paymentSql = "select p.* from tb_paymentrecord p ,tb_payment_method m  where p.order_id = "
						+ order.getId() + " and p.payment_method_id = m.id and m.type = "
						+ PaymentTypeEnum.Offline.getCode();
				List<Map<String, Object>> paymentList = DBMapper.query(paymentSql);
				Assert.assertEquals(paymentList.size(), 1);
				Map<String, Object> paymentMap = paymentList.get(0);
				Assert.assertEquals(Integer.parseInt(paymentMap.get("status").toString()), 1); // 支付成功
				Assert.assertEquals(Integer.parseInt(paymentMap.get("trade_type").toString()), 1); // 支付类型
				Assert.assertEquals(Integer.parseInt(paymentMap.get("is_primary").toString()), 0);
				// 校验tb_trade_order【交易订单表】
				List<TradeOrder> tradeOrderList = getTradeOrderByOrderNum(order.getOrderNum(), TradeType.pay);
				Assert.assertEquals(tradeOrderList.size(), 1); // 下单
				TradeOrder to = tradeOrderList.get(0);
				Assert.assertEquals(to.getRefOrderType().intValue(), 1);
				Assert.assertEquals(to.getAmount().longValue(), order.getOrderPrice().longValue());
				Assert.assertEquals(to.getSuccAmount().longValue(), order.getOrderPrice().longValue());
				Assert.assertEquals(to.getTradeStatus().intValue(), PayConstants.TradeStatus.Successful);
				Assert.assertEquals(to.getPayMethodType().intValue(), PayConstants.PayMethodBit.OfflinePayBit);
				Assert.assertEquals(to.getTradeType().intValue(), PayConstants.TradeType.pay);

				// 校验tb_trade_pay_record【交易支付表】
				int dbReceiveTradeAccountId = getSuitableReceiveMethodId(usehospitalId,
						PayConstants.PayMethodBit.OfflinePayBit);
				List<TradePayRecord> tradePayRecList = getTradePayRecordByOrderNum(order.getOrderNum(),
						to.getTradeOrderNum(),PayConstants.OrderType.MytijianOrder);
				Assert.assertEquals(tradePayRecList.size(), 1);
				TradePayRecord tpr = tradePayRecList.get(0);
				Assert.assertEquals(tpr.getRefOrderType().intValue(), 1);
				Assert.assertEquals(tpr.getTradeMethodConfigId().intValue(),
						getSuitablePayMethodId(order.getFromSite(), PayConstants.PayMethodBit.OfflinePayBit));
				Assert.assertEquals(tpr.getTradeMethodType().intValue(), PayConstants.PayMethod.OfflinePay);
				Assert.assertEquals(tpr.getPayStatus().intValue(), PayConstants.TradeStatus.Successful);
				Assert.assertEquals(tpr.getPayTradeAccountId().intValue(), getTradeAccountByOrderId(order.getId()));
				Assert.assertNull(tpr.getPayTradeSubaccountId());
				Assert.assertNull(tpr.getPayTradeSubaccountType());
				Assert.assertNull(tpr.getPayTradeAccountSnap());
				Assert.assertEquals(tpr.getReceiveTradeAccountId().intValue(), dbReceiveTradeAccountId);
				Assert.assertEquals(tpr.getReceiveTradeSubaccountId().intValue(),
						getSubAccounttingId(dbReceiveTradeAccountId));
				Assert.assertEquals(tpr.getReceiveTradeSubaccountType().intValue(),
						TradeSubAccountType.TRADE_BALANCE_ACCOUNT);

				// 交易tb_trade_account_detail,支付中不往这个表插入数据【账户明细表】
				List<TradeAccountDetail> tradeAccountList = getTradeAccountDetail(to.getTradeOrderNum(), 0);
				Assert.assertEquals(tradeAccountList.size(), 0);
				// 出账
				tradeAccountList = getTradeAccountDetail(to.getTradeOrderNum(), 1);
				Assert.assertEquals(tradeAccountList.size(), 0);
				System.out.println("--------验证交易记录结束---------");
			}

		} else {
			// 检查交易状态
			// 验证订单操作日志
			System.out.println("--------验证操作日志开始---------");
			List<ExamOrderOperateLogDO> logs = OrderChecker.getOrderOperatrLog(order.getOrderNum());
			Assert.assertEquals(logs.get(0).getType().intValue(), OrderOperateTypeEnum.FINISH_PAY.getCode());
			Assert.assertEquals(logs.get(0).getOrderStatus().intValue(), OrderStatus.ALREADY_BOOKED.intValue());
			System.out.println("--------验证操作日志结束---------");

			System.out.println("--------验证交易记录开始---------");
			// 校验tb_paymentrecord
			String paymentSql = "select p.* from tb_paymentrecord p ,tb_payment_method m  where p.order_id = "
					+ order.getId() + " and p.payment_method_id = m.id and m.type = " + PaymentTypeEnum.Card.getCode();
			List<Map<String, Object>> paymentList = DBMapper.query(paymentSql);
			Assert.assertEquals(paymentList.size(), 1);
			Map<String, Object> paymentMap = paymentList.get(0);
			Assert.assertEquals(Integer.parseInt(paymentMap.get("status").toString()), 1); // 支付成功
			Assert.assertEquals(Integer.parseInt(paymentMap.get("trade_type").toString()), 1); // 支付类型
			Assert.assertEquals(Integer.parseInt(paymentMap.get("is_primary").toString()), 1);// 母卡
			Assert.assertEquals(Integer.parseInt(paymentMap.get("expense_account").toString()),
					CardChecker.getParentEntryCard(defaccountId).intValue());
			// 校验tb_trade_order【交易订单表】
			List<TradeOrder> tradeOrderList = getTradeOrderByOrderNum(order.getOrderNum(), TradeType.pay);
			Assert.assertEquals(tradeOrderList.size(), 1); // 下单
			TradeOrder to = tradeOrderList.get(0);
			Assert.assertEquals(to.getRefOrderType().intValue(), 1);
			Assert.assertEquals(to.getAmount().longValue(), order.getOrderPrice().longValue());
			Assert.assertEquals(to.getSuccAmount().longValue(), order.getOrderPrice().longValue());
			Assert.assertEquals(to.getTradeStatus().intValue(), PayConstants.TradeStatus.Successful);
			Assert.assertEquals(to.getPayMethodType().intValue(), PayConstants.PayMethodBit.ParentCardBit);
			Assert.assertEquals(to.getTradeType().intValue(), PayConstants.TradeType.pay);

			// 校验tb_trade_pay_record【交易支付表】
			int dbReceiveTradeAccountId = getSuitableReceiveMethodId(usehospitalId,
					PayConstants.PayMethodBit.BalanceBit);
			List<TradePayRecord> tradePayRecList = getTradePayRecordByOrderNum(order.getOrderNum(),
					to.getTradeOrderNum(),PayConstants.OrderType.MytijianOrder);
			Assert.assertEquals(tradePayRecList.size(), 1);
			TradePayRecord tpr = tradePayRecList.get(0);
			Assert.assertEquals(tpr.getRefOrderType().intValue(), 1);
			Assert.assertEquals(tpr.getTradeMethodConfigId().intValue(),
					getSuitablePayMethodId(order.getFromSite(), PayConstants.PayMethodBit.ParentCardBit));
			Assert.assertEquals(tpr.getTradeMethodType().intValue(), PayConstants.PayMethod.ParentCard);
			Assert.assertEquals(tpr.getPayStatus().intValue(), PayConstants.TradeStatus.Successful);
			Assert.assertEquals(tpr.getPayTradeAccountId().intValue(), getTradeAccountIdByAccountId(defaccountId));
			Assert.assertEquals(tpr.getPayTradeSubaccountId().intValue(),
					getSubParentCardId(getTradeAccountIdByAccountId(defaccountId)));
			Assert.assertEquals(tpr.getPayTradeSubaccountType().intValue(),
					TradeSubAccountType.TRADE_PARENT_CARD_ACCOUNT);
			Assert.assertEquals(tpr.getReceiveTradeAccountId().intValue(), dbReceiveTradeAccountId);
			Assert.assertEquals(tpr.getReceiveTradeSubaccountId().intValue(),
					getSubAccounttingId(dbReceiveTradeAccountId));
			Assert.assertEquals(tpr.getReceiveTradeSubaccountType().intValue(),
					TradeSubAccountType.TRADE_BALANCE_ACCOUNT);

			// 交易tb_trade_account_detail,支付中不往这个表插入数据【账户明细表】
			List<TradeAccountDetail> tradeAccountList = getTradeAccountDetail(to.getTradeOrderNum(), 0);
			Assert.assertEquals(tradeAccountList.size(), 1);
			TradeAccountDetail tad = tradeAccountList.get(0);
			Assert.assertEquals(tad.getRefBizSn(), tpr.getSn());
			Assert.assertEquals(tad.getTradeAccountId().intValue(), dbReceiveTradeAccountId);
			Assert.assertEquals(tad.getTradeSubAccountType().intValue(), TradeSubAccountType.TRADE_BALANCE_ACCOUNT);
			Assert.assertEquals(tad.getTradeSubAccountId().intValue(), getSubAccounttingId(dbReceiveTradeAccountId));
			Assert.assertEquals(tad.getTradeOrderNum(), to.getTradeOrderNum());
			Assert.assertEquals(tad.getBizType().intValue(), TradeAccountDetailBizType.PAYMENT);
			Assert.assertEquals(tad.getChangeAmount().intValue(), order.getOrderPrice().intValue());
			// 出账
			tradeAccountList = getTradeAccountDetail(to.getTradeOrderNum(), 1);
			Assert.assertEquals(tradeAccountList.size(), 1);
			tad = tradeAccountList.get(0);
			Assert.assertEquals(tad.getRefBizSn(), tpr.getSn());
			Assert.assertEquals(tad.getTradeAccountId().intValue(), getTradeAccountIdByAccountId(defaccountId));
			Assert.assertEquals(tad.getTradeSubAccountType().intValue(), TradeSubAccountType.TRADE_PARENT_CARD_ACCOUNT);
			Assert.assertEquals(tad.getTradeSubAccountId().intValue(),
					getSubParentCardId(getTradeAccountIdByAccountId(defaccountId)));
			Assert.assertEquals(tad.getTradeOrderNum(), to.getTradeOrderNum());
			Assert.assertEquals(tad.getBizType().intValue(), TradeAccountDetailBizType.PAYMENT);
			Assert.assertEquals(tad.getChangeAmount().intValue(), order.getOrderPrice().intValue());
			System.out.println("--------验证交易记录结束---------");
		}
	}

	/**
	 * 验证卡代预约下今天单
	 * @param usehospitalId
	 * @param order
	 * @param healthId
	 * @param onlinePay
	 * @param cardId
     * @throws Exception
     */
	public static void checkCrmProxyFastBookTodayTradeOrder(int usehospitalId, Order order, int healthId, Boolean onlinePay,
													   int cardId) throws Exception {
		checkCrmProxyFastBookTradeOrder(usehospitalId,order,healthId,onlinePay,cardId,false);
	}

	/**
	 * 验证卡代预约交易部分
	 * 
	 * @param usehospitalId
	 * @param order
	 * @param healthId
	 * @param onlinePay
	 * @param cardId
	 * @param  isNeedLocalPay 是否需要确认收款，医院设置，非当天订单有效
	 *
	 * @throws Exception
	 */
	public static void checkCrmProxyFastBookTradeOrder(int usehospitalId, Order order, int healthId, Boolean onlinePay,
			int cardId,boolean isNeedLocalPay) throws Exception {
		if (onlinePay) {
			// 在线支付扫码
			// 验证订单操作日志
			System.out.println("--------验证操作日志开始---------");
			List<ExamOrderOperateLogDO> logs = OrderChecker.getOrderOperatrLog(order.getOrderNum());
			Assert.assertEquals(logs.get(0).getType().intValue(), OrderOperateTypeEnum.BOOK_ORDER.getCode());
			Assert.assertEquals(logs.get(0).getOrderStatus().intValue(), OrderStatus.NOT_PAY.intValue());
			System.out.println("--------验证操作日志结束---------");
			// 检查交易状态
			System.out.println("--------验证交易记录开始---------");
			// 校验tb_paymentrecord
			String paymentSql = "select p.* from tb_paymentrecord p ,tb_payment_method m  where p.order_id = "
					+ order.getId() + " and p.payment_method_id = m.id and m.type = " + PaymentTypeEnum.Card.getCode();
			List<Map<String, Object>> paymentList = DBMapper.query(paymentSql);
			Assert.assertEquals(paymentList.size(), 1);
			Map<String, Object> paymentMap = paymentList.get(0);
			Assert.assertEquals(Integer.parseInt(paymentMap.get("status").toString()), 1); // 支付成功
			Assert.assertEquals(Integer.parseInt(paymentMap.get("trade_type").toString()), 1); // 支付类型
			Assert.assertEquals(Integer.parseInt(paymentMap.get("is_primary").toString()), 0);// 用户卡
			Assert.assertEquals(Integer.parseInt(paymentMap.get("expense_account").toString()), cardId);
			int accountCardAmount = Integer.parseInt(paymentMap.get("amount").toString());

			paymentSql = "select p.* from tb_paymentrecord p ,tb_payment_method m  where p.order_id = " + order.getId()
					+ " and p.payment_method_id = m.id and m.type = " + PaymentMethod.ONLINE_PAY_METHOD_TYPE;
			List<Map<String, Object>> onlinePaymentList = DBMapper.query(paymentSql);
			if (onlinePaymentList.size() > 0) {
				Assert.assertEquals(onlinePaymentList.size(), 1);
				paymentMap = onlinePaymentList.get(0);
				Assert.assertEquals(Integer.parseInt(paymentMap.get("status").toString()), 0); // 支付中
				Assert.assertEquals(Integer.parseInt(paymentMap.get("trade_type").toString()), 1); // 支付类型
				Assert.assertEquals(Integer.parseInt(paymentMap.get("is_primary").toString()), 0);
			}
			// 校验tb_trade_order【交易订单表】
			List<TradeOrder> tradeOrderList = getTradeOrderByOrderNum(order.getOrderNum(), TradeType.pay);
			Assert.assertEquals(tradeOrderList.size(), 1); // 下单
			TradeOrder to = tradeOrderList.get(0);
			Assert.assertEquals(to.getRefOrderType().intValue(), 1);
			Assert.assertEquals(to.getAmount().longValue(), order.getOrderPrice().longValue());
			Assert.assertEquals(to.getSuccAmount().longValue(), accountCardAmount);
			if (onlinePaymentList.size() > 0) {
				Assert.assertEquals(to.getTradeStatus().intValue(), PayConstants.TradeStatus.Paying);
				Assert.assertEquals(to.getPayMethodType().intValue(),
						PayConstants.PayMethodBit.CardBit + PayMethodBit.OnlinePayBit);
			} else {
				Assert.assertEquals(to.getTradeStatus().intValue(), PayConstants.TradeStatus.Successful);
				Assert.assertEquals(to.getPayMethodType().intValue(), PayConstants.PayMethodBit.CardBit);
			}
			Assert.assertEquals(to.getTradeType().intValue(), PayConstants.TradeType.pay);

			// 校验tb_trade_pay_record【交易支付表】
			List<TradePayRecord> tradePayRecList = getTradePayRecordByOrderNum(order.getOrderNum(),
					to.getTradeOrderNum(),PayConstants.OrderType.MytijianOrder);
			if (onlinePaymentList.size() > 0) {
				Assert.assertEquals(tradePayRecList.size(), 2);
				// 线上付款
				int dbReceiveTradeAccountId = getSuitableReceiveMethodId(usehospitalId,
						PayConstants.PayMethodBit.OnlinePayBit);
				TradePayRecord tpr = tradePayRecList.get(1);
				Assert.assertEquals(tpr.getRefOrderType().intValue(), 1);
				Assert.assertEquals(tpr.getTradeMethodConfigId().intValue(),
						getSuitablePayMethodId(order.getFromSite(), PayConstants.PayMethodBit.OnlinePayBit));
				Assert.assertEquals(tpr.getTradeMethodType().intValue(), PayConstants.PayMethod.OnlinePay);
				Assert.assertEquals(tpr.getPayStatus().intValue(), PayConstants.TradeStatus.Paying);
				Assert.assertEquals(tpr.getPayTradeAccountId().intValue(), getTradeAccountIdByAccountId(healthId));
				Assert.assertNull(tpr.getPayTradeSubaccountId());
				Assert.assertNull(tpr.getPayTradeSubaccountType());
				Assert.assertNull(tpr.getPayTradeAccountSnap());
				Assert.assertEquals(tpr.getReceiveTradeAccountId().intValue(), dbReceiveTradeAccountId);
				Assert.assertEquals(tpr.getReceiveTradeSubaccountId().intValue(),
						getSubAccounttingId(dbReceiveTradeAccountId));
				Assert.assertEquals(tpr.getReceiveTradeSubaccountType().intValue(),
						TradeSubAccountType.TRADE_BALANCE_ACCOUNT);

				// 用户卡支付
				dbReceiveTradeAccountId = getSuitableReceiveMethodId(usehospitalId, PayConstants.PayMethodBit.CardBit);
				tpr = tradePayRecList.get(0);
				Assert.assertEquals(tpr.getRefOrderType().intValue(), 1);
				Assert.assertEquals(tpr.getTradeMethodConfigId().intValue(),
						getSuitablePayMethodId(order.getFromSite(), PayConstants.PayMethodBit.CardBit));
				Assert.assertEquals(tpr.getTradeMethodType().intValue(), PayConstants.PayMethod.Card);
				Assert.assertEquals(tpr.getPayStatus().intValue(), PayConstants.TradeStatus.Successful);
				Assert.assertEquals(tpr.getPayTradeAccountId().intValue(), getTradeAccountIdByAccountId(healthId));
				Assert.assertEquals(tpr.getPayTradeSubaccountId().intValue(), cardId);
				Assert.assertEquals(tpr.getPayTradeSubaccountType().intValue(), TradeSubAccountType.TRADE_CARD_ACCOUNT);
				Assert.assertEquals(tpr.getReceiveTradeAccountId().intValue(), dbReceiveTradeAccountId);
				Assert.assertEquals(tpr.getReceiveTradeSubaccountId().intValue(),
						getSubAccounttingId(dbReceiveTradeAccountId));
				Assert.assertEquals(tpr.getReceiveTradeSubaccountType().intValue(),
						TradeSubAccountType.TRADE_BALANCE_ACCOUNT);

				// 交易tb_trade_account_detail,支付中不往这个表插入数据【账户明细表】
				List<TradeAccountDetail> tradeAccountList = getTradeAccountDetail(to.getTradeOrderNum(), 0);
				Assert.assertEquals(tradeAccountList.size(), 1);
				TradeAccountDetail tad = tradeAccountList.get(0);
				Assert.assertEquals(tad.getRefBizSn(), tpr.getSn());
				Assert.assertEquals(tad.getTradeAccountId().intValue(), dbReceiveTradeAccountId);
				Assert.assertEquals(tad.getTradeSubAccountType().intValue(), TradeSubAccountType.TRADE_BALANCE_ACCOUNT);
				Assert.assertEquals(tad.getTradeSubAccountId().intValue(),
						getSubAccounttingId(dbReceiveTradeAccountId));
				Assert.assertEquals(tad.getTradeOrderNum(), to.getTradeOrderNum());
				Assert.assertEquals(tad.getBizType().intValue(), TradeAccountDetailBizType.PAYMENT);
				Assert.assertEquals(tad.getChangeAmount().intValue(), accountCardAmount);
				// 出账
				tradeAccountList = getTradeAccountDetail(to.getTradeOrderNum(), 1);
				Assert.assertEquals(tradeAccountList.size(), 1);
				tad = tradeAccountList.get(0);
				Assert.assertEquals(tad.getRefBizSn(), tpr.getSn());
				Assert.assertEquals(tad.getTradeAccountId().intValue(), getTradeAccountIdByAccountId(healthId));
				Assert.assertEquals(tad.getTradeSubAccountType().intValue(), TradeSubAccountType.TRADE_CARD_ACCOUNT);
				Assert.assertEquals(tad.getTradeSubAccountId().intValue(), cardId);
				Assert.assertEquals(tad.getTradeOrderNum(), to.getTradeOrderNum());
				Assert.assertEquals(tad.getBizType().intValue(), TradeAccountDetailBizType.PAYMENT);
				Assert.assertEquals(tad.getChangeAmount().intValue(), accountCardAmount);
			} else {
				Assert.assertEquals(tradePayRecList.size(), 1);
				int dbReceiveTradeAccountId = getSuitableReceiveMethodId(usehospitalId,
						PayConstants.PayMethodBit.CardBit);
				// 纯用户卡
				TradePayRecord tpr = tradePayRecList.get(0);
				Assert.assertEquals(tpr.getRefOrderType().intValue(), 1);
				Assert.assertEquals(tpr.getTradeMethodConfigId().intValue(),
						getSuitablePayMethodId(order.getFromSite(), PayConstants.PayMethodBit.CardBit));
				Assert.assertEquals(tpr.getTradeMethodType().intValue(), PayConstants.PayMethod.Card);
				Assert.assertEquals(tpr.getPayStatus().intValue(), PayConstants.TradeStatus.Successful);
				Assert.assertEquals(tpr.getPayTradeAccountId().intValue(), getTradeAccountIdByAccountId(healthId));
				Assert.assertEquals(tpr.getPayTradeSubaccountId().intValue(), cardId);
				Assert.assertEquals(tpr.getPayTradeSubaccountType().intValue(), TradeSubAccountType.TRADE_CARD_ACCOUNT);
				Assert.assertEquals(tpr.getReceiveTradeAccountId().intValue(), dbReceiveTradeAccountId);
				Assert.assertEquals(tpr.getReceiveTradeSubaccountId().intValue(),
						getSubAccounttingId(dbReceiveTradeAccountId));
				Assert.assertEquals(tpr.getReceiveTradeSubaccountType().intValue(),
						TradeSubAccountType.TRADE_BALANCE_ACCOUNT);

				// 交易tb_trade_account_detail,支付中不往这个表插入数据【账户明细表】
				List<TradeAccountDetail> tradeAccountList = getTradeAccountDetail(to.getTradeOrderNum(), 0);
				Assert.assertEquals(tradeAccountList.size(), 1);
				TradeAccountDetail tad = tradeAccountList.get(0);
				Assert.assertEquals(tad.getRefBizSn(), tpr.getSn());
				Assert.assertEquals(tad.getTradeAccountId().intValue(), dbReceiveTradeAccountId);
				Assert.assertEquals(tad.getTradeSubAccountType().intValue(), TradeSubAccountType.TRADE_BALANCE_ACCOUNT);
				Assert.assertEquals(tad.getTradeSubAccountId().intValue(),
						getSubAccounttingId(dbReceiveTradeAccountId));
				Assert.assertEquals(tad.getTradeOrderNum(), to.getTradeOrderNum());
				Assert.assertEquals(tad.getBizType().intValue(), TradeAccountDetailBizType.PAYMENT);
				Assert.assertEquals(tad.getChangeAmount().intValue(), order.getOrderPrice().intValue());
				// 出账
				tradeAccountList = getTradeAccountDetail(to.getTradeOrderNum(), 1);
				Assert.assertEquals(tradeAccountList.size(), 1);
				tad = tradeAccountList.get(0);
				Assert.assertEquals(tad.getRefBizSn(), tpr.getSn());
				Assert.assertEquals(tad.getTradeAccountId().intValue(), getTradeAccountIdByAccountId(healthId));
				Assert.assertEquals(tad.getTradeSubAccountType().intValue(), TradeSubAccountType.TRADE_CARD_ACCOUNT);
				Assert.assertEquals(tad.getTradeSubAccountId().intValue(), cardId);
				Assert.assertEquals(tad.getTradeOrderNum(), to.getTradeOrderNum());
				Assert.assertEquals(tad.getBizType().intValue(), TradeAccountDetailBizType.PAYMENT);
				Assert.assertEquals(tad.getChangeAmount().intValue(), order.getOrderPrice().intValue());
			}
			System.out.println("--------验证交易记录结束---------");
		} else {// 极速预约线下付款....
				// 检查交易状态
				// 验证订单操作日志
			System.out.println("--------验证操作日志开始---------");
			List<ExamOrderOperateLogDO> logs = OrderChecker.getOrderOperatrLog(order.getOrderNum());
			Assert.assertEquals(logs.get(0).getType().intValue(), OrderOperateTypeEnum.FINISH_PAY.getCode());
			//查询订单的下单日期是否非今天
			if(!sdf.format(order.getExamDate()).equals(sdf.format(new Date())) && isNeedLocalPay)
				Assert.assertEquals(logs.get(0).getOrderStatus().intValue(), OrderStatus.SITE_PAY.intValue());
			else
				Assert.assertEquals(logs.get(0).getOrderStatus().intValue(), OrderStatus.ALREADY_BOOKED.intValue());
			System.out.println("--------验证操作日志结束---------");

			System.out.println("--------验证交易记录开始---------");
			// 校验tb_paymentrecord
			String paymentSql = "select p.* from tb_paymentrecord p ,tb_payment_method m  where p.order_id = "
					+ order.getId() + " and p.payment_method_id = m.id and m.type = " + PaymentTypeEnum.Card.getCode();
			List<Map<String, Object>> paymentList = DBMapper.query(paymentSql);
			Assert.assertEquals(paymentList.size(), 1);
			Map<String, Object> paymentMap = paymentList.get(0);
			Assert.assertEquals(Integer.parseInt(paymentMap.get("status").toString()), 1); // 支付成功
			Assert.assertEquals(Integer.parseInt(paymentMap.get("trade_type").toString()), 1); // 支付类型
			Assert.assertEquals(Integer.parseInt(paymentMap.get("is_primary").toString()), 0);// 用户卡
			long cardAmount = Long.parseLong(paymentMap.get("amount").toString());
			Assert.assertTrue(getSubCardIdList(getTradeAccountIdByAccountId(healthId))
					.contains(Integer.parseInt(paymentMap.get("expense_account").toString())));

			paymentSql = "select p.* from tb_paymentrecord p ,tb_payment_method m  where p.order_id = " + order.getId()
					+ " and p.payment_method_id = m.id and m.type = " + PaymentTypeEnum.Offline.getCode();
			List<Map<String, Object>> offlinePaymentList = DBMapper.query(paymentSql);
			if (offlinePaymentList.size() > 0) {
				Assert.assertEquals(offlinePaymentList.size(), 1);
				paymentMap = offlinePaymentList.get(0);
				if(!sdf.format(order.getExamDate()).equals(sdf.format(new Date())) && isNeedLocalPay)
					Assert.assertEquals(Integer.parseInt(paymentMap.get("status").toString()), 0); // 期待支付中
				else
					Assert.assertEquals(Integer.parseInt(paymentMap.get("status").toString()), 1); // 支付成功
				Assert.assertEquals(Integer.parseInt(paymentMap.get("trade_type").toString()), 1); // 支付类型
				Assert.assertEquals(Integer.parseInt(paymentMap.get("is_primary").toString()), 0);
			}

			// 校验tb_trade_order【交易订单表】
			List<TradeOrder> tradeOrderList = getTradeOrderByOrderNum(order.getOrderNum(), TradeType.pay);
			Assert.assertEquals(tradeOrderList.size(), 1); // 下单
			TradeOrder to = tradeOrderList.get(0);
			Assert.assertEquals(to.getRefOrderType().intValue(), 1);
			Assert.assertEquals(to.getAmount().longValue(), order.getOrderPrice().longValue());
			if(!sdf.format(order.getExamDate()).equals(sdf.format(new Date())) && isNeedLocalPay)
				Assert.assertEquals(to.getSuccAmount().longValue(), cardAmount);
			else
				Assert.assertEquals(to.getSuccAmount().longValue(), order.getOrderPrice().longValue());
			if (offlinePaymentList.size() > 0){
				Assert.assertEquals(to.getPayMethodType().intValue(),
						PayConstants.PayMethodBit.CardBit + PayConstants.PayMethodBit.OfflinePayBit);
				if(!sdf.format(order.getExamDate()).equals(sdf.format(new Date())) && isNeedLocalPay)
					Assert.assertEquals(to.getTradeStatus().intValue(), PayConstants.TradeStatus.Paying);
				else
					Assert.assertEquals(to.getTradeStatus().intValue(), PayConstants.TradeStatus.Successful);
			}
			else{
				Assert.assertEquals(to.getPayMethodType().intValue(), PayConstants.PayMethodBit.CardBit);
				Assert.assertEquals(to.getTradeStatus().intValue(), PayConstants.TradeStatus.Successful);

			}
			Assert.assertEquals(to.getTradeType().intValue(), PayConstants.TradeType.pay);

			// 校验tb_trade_pay_record【交易支付表】
			List<TradePayRecord> tradePayRecList = getTradePayRecordByOrderNum(order.getOrderNum(),
					to.getTradeOrderNum(),PayConstants.OrderType.MytijianOrder);
			if (offlinePaymentList.size() > 0) {
				Assert.assertEquals(tradePayRecList.size(), 2);
				// 用户卡支付
				int dbReceiveTradeAccountId = getSuitableReceiveMethodId(usehospitalId,
						PayConstants.PayMethodBit.CardBit);
				TradePayRecord tpr = tradePayRecList.get(1);
				Assert.assertEquals(tpr.getRefOrderType().intValue(), 1);
				Assert.assertEquals(tpr.getTradeMethodConfigId().intValue(),
						getSuitablePayMethodId(order.getFromSite(), PayConstants.PayMethodBit.CardBit));
				Assert.assertEquals(tpr.getTradeMethodType().intValue(), PayConstants.PayMethod.Card);
				Assert.assertEquals(tpr.getPayStatus().intValue(), PayConstants.TradeStatus.Successful);
				Assert.assertEquals(tpr.getPayTradeAccountId().intValue(), getTradeAccountIdByAccountId(healthId));
				Assert.assertEquals(tpr.getPayTradeSubaccountId().intValue(), cardId);
				Assert.assertEquals(tpr.getPayTradeSubaccountType().intValue(), TradeSubAccountType.TRADE_CARD_ACCOUNT);
				Assert.assertEquals(tpr.getReceiveTradeAccountId().intValue(), dbReceiveTradeAccountId);
				Assert.assertEquals(tpr.getReceiveTradeSubaccountId().intValue(),
						getSubAccounttingId(dbReceiveTradeAccountId));
				Assert.assertEquals(tpr.getReceiveTradeSubaccountType().intValue(),
						TradeSubAccountType.TRADE_BALANCE_ACCOUNT);
				Assert.assertEquals(tpr.getPayAmount().longValue(),cardAmount);

				// 线下支付
				dbReceiveTradeAccountId = getSuitableReceiveMethodId(usehospitalId,
						PayConstants.PayMethodBit.OfflinePayBit);
				tpr = tradePayRecList.get(0);
				Assert.assertEquals(tpr.getRefOrderType().intValue(), 1);
				Assert.assertEquals(tpr.getTradeMethodConfigId().intValue(),
						getSuitablePayMethodId(order.getFromSite(), PayConstants.PayMethodBit.OfflinePayBit));
				Assert.assertEquals(tpr.getTradeMethodType().intValue(), PayConstants.PayMethod.OfflinePay);
				if(!sdf.format(order.getExamDate()).equals(sdf.format(new Date())) && isNeedLocalPay)
					Assert.assertEquals(tpr.getPayStatus().intValue(), PayConstants.TradeStatus.Paying);
				else
					Assert.assertEquals(tpr.getPayStatus().intValue(), PayConstants.TradeStatus.Successful);
				Assert.assertEquals(tpr.getPayTradeAccountId().intValue(), getTradeAccountIdByAccountId(healthId));
				Assert.assertNull(tpr.getPayTradeSubaccountId());
				Assert.assertNull(tpr.getPayTradeSubaccountType());
				Assert.assertNull(tpr.getPayTradeAccountSnap());
				Assert.assertEquals(tpr.getReceiveTradeAccountId().intValue(), dbReceiveTradeAccountId);
				Assert.assertEquals(tpr.getReceiveTradeSubaccountId().intValue(),
						getSubAccounttingId(dbReceiveTradeAccountId));
				Assert.assertEquals(tpr.getReceiveTradeSubaccountType().intValue(),
						TradeSubAccountType.TRADE_BALANCE_ACCOUNT);
				int offlineAmount = tpr.getPayAmount().intValue();

				// 交易tb_trade_account_detail
				List<TradeAccountDetail> tradeAccountList = getTradeAccountDetail(to.getTradeOrderNum(), 0);
				Assert.assertEquals(tradeAccountList.size(), 1);
				TradeAccountDetail tad = tradeAccountList.get(0);
				Assert.assertEquals(tad.getTradeAccountId().intValue(), dbReceiveTradeAccountId);
				Assert.assertEquals(tad.getTradeSubAccountType().intValue(), TradeSubAccountType.TRADE_BALANCE_ACCOUNT);
				Assert.assertEquals(tad.getTradeSubAccountId().intValue(),
						getSubAccounttingId(dbReceiveTradeAccountId));
				Assert.assertEquals(tad.getTradeOrderNum(), to.getTradeOrderNum());
				Assert.assertEquals(tad.getBizType().intValue(), TradeAccountDetailBizType.PAYMENT);
				Assert.assertEquals(tad.getChangeAmount().longValue(), cardAmount);
				// 出账
				tradeAccountList = getTradeAccountDetail(to.getTradeOrderNum(), 1);
				if(!sdf.format(order.getExamDate()).equals(sdf.format(new Date())) && isNeedLocalPay)//预约非今天（线下支付)
					Assert.assertEquals(tradeAccountList.size(), 1);//只有卡记录
				else
					Assert.assertEquals(tradeAccountList.size(), 1);//只有卡记录
				// 用户卡
				tad = tradeAccountList.get(0);
				Assert.assertEquals(tad.getTradeAccountId().intValue(), getTradeAccountIdByAccountId(healthId));
				Assert.assertEquals(tad.getTradeSubAccountType().intValue(), TradeSubAccountType.TRADE_CARD_ACCOUNT);
				Assert.assertEquals(tad.getTradeSubAccountId().intValue(), cardId);
				Assert.assertEquals(tad.getTradeOrderNum(), to.getTradeOrderNum());
				Assert.assertEquals(tad.getBizType().intValue(), TradeAccountDetailBizType.PAYMENT);
				Assert.assertEquals(tad.getChangeAmount().longValue(), cardAmount);
			} else {
				// 纯用户卡
				int dbReceiveTradeAccountId = getSuitableReceiveMethodId(usehospitalId,
						PayConstants.PayMethodBit.CardBit);
				Assert.assertEquals(tradePayRecList.size(), 1);
				TradePayRecord tpr = tradePayRecList.get(0);
				Assert.assertEquals(tpr.getRefOrderType().intValue(), 1);
				Assert.assertEquals(tpr.getTradeMethodConfigId().intValue(),
						getSuitablePayMethodId(order.getFromSite(), PayConstants.PayMethodBit.CardBit));
				Assert.assertEquals(tpr.getTradeMethodType().intValue(), PayConstants.PayMethod.Card);
				Assert.assertEquals(tpr.getPayStatus().intValue(), PayConstants.TradeStatus.Successful);
				Assert.assertEquals(tpr.getPayTradeAccountId().intValue(), getTradeAccountIdByAccountId(healthId));
				Assert.assertEquals(tpr.getPayTradeSubaccountId().intValue(), cardId);
				Assert.assertEquals(tpr.getPayTradeSubaccountType().intValue(), TradeSubAccountType.TRADE_CARD_ACCOUNT);
				Assert.assertEquals(tpr.getReceiveTradeAccountId().intValue(), dbReceiveTradeAccountId);
				Assert.assertEquals(tpr.getReceiveTradeSubaccountId().intValue(),
						getSubAccounttingId(dbReceiveTradeAccountId));
				Assert.assertEquals(tpr.getReceiveTradeSubaccountType().intValue(),
						TradeSubAccountType.TRADE_BALANCE_ACCOUNT);

				// 交易tb_trade_account_detail
				List<TradeAccountDetail> tradeAccountList = getTradeAccountDetail(to.getTradeOrderNum(), 0);
				Assert.assertEquals(tradeAccountList.size(), 1);
				TradeAccountDetail tad = tradeAccountList.get(0);
				Assert.assertEquals(tad.getRefBizSn(), tpr.getSn());
				Assert.assertEquals(tad.getTradeAccountId().intValue(), dbReceiveTradeAccountId);
				Assert.assertEquals(tad.getTradeSubAccountType().intValue(), TradeSubAccountType.TRADE_BALANCE_ACCOUNT);
				Assert.assertEquals(tad.getTradeSubAccountId().intValue(),
						getSubAccounttingId(dbReceiveTradeAccountId));
				Assert.assertEquals(tad.getTradeOrderNum(), to.getTradeOrderNum());
				Assert.assertEquals(tad.getBizType().intValue(), TradeAccountDetailBizType.PAYMENT);
				Assert.assertEquals(tad.getChangeAmount().intValue(), order.getOrderPrice().intValue());
				// 出账
				tradeAccountList = getTradeAccountDetail(to.getTradeOrderNum(), 1);
				Assert.assertEquals(tradeAccountList.size(), 1);
				tad = tradeAccountList.get(0);
				Assert.assertEquals(tad.getRefBizSn(), tpr.getSn());
				Assert.assertEquals(tad.getTradeAccountId().intValue(), getTradeAccountIdByAccountId(healthId));
				Assert.assertEquals(tad.getTradeSubAccountType().intValue(), TradeSubAccountType.TRADE_CARD_ACCOUNT);
				Assert.assertEquals(tad.getTradeSubAccountId().intValue(), cardId);
				Assert.assertEquals(tad.getTradeOrderNum(), to.getTradeOrderNum());
				Assert.assertEquals(tad.getBizType().intValue(), TradeAccountDetailBizType.PAYMENT);
				Assert.assertEquals(tad.getChangeAmount().intValue(), order.getOrderPrice().intValue());

				System.out.println("--------验证交易记录结束---------");
			}

		}
	}


	/**
	 * 验证CRM确认收款交易相关
	 * @param usehospitalId
	 * @param hCompany
	 * @param order
	 * @throws Exception
     */
	public static void checkCrmNeedLocalPay(int usehospitalId, HospitalCompany hCompany,Order order,int parentAccountId)throws Exception {
		{
			int healthId = order.getAccount().getId();
			// 确认收款
			if (hCompany.getPlatformCompanyId() != null && hCompany.getPlatformCompanyId() == 2) {
				// 检查交易状态
				// 验证订单操作日志
				System.out.println("--------验证操作日志开始---------");
				List<ExamOrderOperateLogDO> logs = OrderChecker.getOrderOperatrLog(order.getOrderNum());
				Assert.assertEquals(logs.get(0).getType().intValue(), OrderOperateTypeEnum.GATHERING.getCode());
				Assert.assertEquals(logs.get(0).getOrderStatus().intValue(), OrderStatus.ALREADY_BOOKED.intValue());
				System.out.println("--------验证操作日志结束---------");

				System.out.println("--------验证交易记录开始---------");
				// 校验tb_paymentrecord
				String paymentSql = "select p.* from tb_paymentrecord p ,tb_payment_method m  where p.order_id = "
						+ order.getId() + " and p.payment_method_id = m.id and m.type = "
						+ PaymentTypeEnum.Offline.getCode();
				List<Map<String, Object>> paymentList = DBMapper.query(paymentSql);
				Assert.assertEquals(paymentList.size(), 1);
				Map<String, Object> paymentMap = paymentList.get(0);
				Assert.assertEquals(Integer.parseInt(paymentMap.get("status").toString()), 1); // 支付成功
				Assert.assertEquals(Integer.parseInt(paymentMap.get("trade_type").toString()), 1); // 支付类型
				Assert.assertEquals(Integer.parseInt(paymentMap.get("is_primary").toString()), 0);
				// 校验tb_trade_order【交易订单表】
				List<TradeOrder> tradeOrderList = getTradeOrderByOrderNum(order.getOrderNum(), TradeType.pay);
				Assert.assertEquals(tradeOrderList.size(), 1); // 下单
				TradeOrder to = tradeOrderList.get(0);
				Assert.assertEquals(to.getRefOrderType().intValue(), 1);
				Assert.assertEquals(to.getAmount().longValue(), order.getOrderPrice().longValue());
				Assert.assertEquals(to.getSuccAmount().longValue(), order.getOrderPrice().longValue());
				Assert.assertEquals(to.getTradeStatus().intValue(), PayConstants.TradeStatus.Successful);
				Assert.assertEquals(to.getPayMethodType().intValue(), PayConstants.PayMethodBit.OfflinePayBit);
				Assert.assertEquals(to.getTradeType().intValue(), PayConstants.TradeType.pay);

				// 校验tb_trade_pay_record【交易支付表】
				int dbReceiveTradeAccountId = getSuitableReceiveMethodId(usehospitalId,
						PayConstants.PayMethodBit.OfflinePayBit);
				List<TradePayRecord> tradePayRecList = getTradePayRecordByOrderNum(order.getOrderNum(),
						to.getTradeOrderNum(),PayConstants.OrderType.MytijianOrder);
				Assert.assertEquals(tradePayRecList.size(), 1);
				TradePayRecord tpr = tradePayRecList.get(0);
				Assert.assertEquals(tpr.getRefOrderType().intValue(), 1);
				Assert.assertEquals(tpr.getTradeMethodConfigId().intValue(),
						getSuitablePayMethodId(order.getFromSite(), PayConstants.PayMethodBit.OfflinePayBit));
				Assert.assertEquals(tpr.getTradeMethodType().intValue(), PayConstants.PayMethod.OfflinePay);
				Assert.assertEquals(tpr.getPayStatus().intValue(), PayConstants.TradeStatus.Successful);
				Assert.assertEquals(tpr.getPayTradeAccountId().intValue(), getTradeAccountByOrderId(order.getId()));
				Assert.assertNull(tpr.getPayTradeSubaccountId());
				Assert.assertNull(tpr.getPayTradeSubaccountType());
				Assert.assertNull(tpr.getPayTradeAccountSnap());
				Assert.assertEquals(tpr.getReceiveTradeAccountId().intValue(), dbReceiveTradeAccountId);
				Assert.assertEquals(tpr.getReceiveTradeSubaccountId().intValue(),
						getSubAccounttingId(dbReceiveTradeAccountId));
				Assert.assertEquals(tpr.getReceiveTradeSubaccountType().intValue(),
						TradeSubAccountType.TRADE_BALANCE_ACCOUNT);

				// 交易tb_trade_account_detail,支付中不往这个表插入数据【账户明细表】
				List<TradeAccountDetail> tradeAccountList = getTradeAccountDetail(to.getTradeOrderNum(), 0);
				Assert.assertEquals(tradeAccountList.size(), 0);
				// 出账
				tradeAccountList = getTradeAccountDetail(to.getTradeOrderNum(), 1);
				Assert.assertEquals(tradeAccountList.size(), 0);
				System.out.println("--------验证交易记录结束---------");
			} else {
				// 检查交易状态
				// 验证订单操作日志
				System.out.println("--------验证操作日志开始---------");
				List<ExamOrderOperateLogDO> logs = OrderChecker.getOrderOperatrLog(order.getOrderNum());
				Assert.assertEquals(logs.get(0).getType().intValue(), OrderOperateTypeEnum.FINISH_PAY.getCode());
				Assert.assertEquals(logs.get(0).getOrderStatus().intValue(), OrderStatus.ALREADY_BOOKED.intValue());
				System.out.println("--------验证操作日志结束---------");

				System.out.println("--------验证交易记录开始---------");
				// 校验tb_paymentrecord
				String paymentSql = "select p.* from tb_paymentrecord p ,tb_payment_method m  where p.order_id = "
						+ order.getId() + " and p.payment_method_id = m.id and m.type = "
						+ PaymentTypeEnum.Card.getCode();
				List<Map<String, Object>> paymentList = DBMapper.query(paymentSql);
				Assert.assertEquals(paymentList.size(), 1);
				Map<String, Object> paymentMap = paymentList.get(0);
				Assert.assertEquals(Integer.parseInt(paymentMap.get("status").toString()), 1); // 支付成功
				Assert.assertEquals(Integer.parseInt(paymentMap.get("trade_type").toString()), 1); // 支付类型
				Assert.assertEquals(Integer.parseInt(paymentMap.get("is_primary").toString()), 1);// 母卡
				Assert.assertEquals(Integer.parseInt(paymentMap.get("expense_account").toString()),
						CardChecker.getParentEntryCard(parentAccountId).intValue());

				paymentSql = "select p.* from tb_paymentrecord p ,tb_payment_method m  where p.order_id = "
						+ order.getId() + " and p.payment_method_id = m.id and m.type = "
						+ PaymentTypeEnum.Offline.getCode();
				List<Map<String, Object>> offlinePaymentList = DBMapper.query(paymentSql);
				if (offlinePaymentList.size() > 0) {
					Assert.assertEquals(offlinePaymentList.size(), 1);
					paymentMap = offlinePaymentList.get(0);
					Assert.assertEquals(Integer.parseInt(paymentMap.get("status").toString()), 1); // 支付成功
					Assert.assertEquals(Integer.parseInt(paymentMap.get("trade_type").toString()), 1); // 支付类型
					Assert.assertEquals(Integer.parseInt(paymentMap.get("is_primary").toString()), 0);
				}

				// 校验tb_trade_order【交易订单表】
				List<TradeOrder> tradeOrderList = getTradeOrderByOrderNum(order.getOrderNum(), TradeType.pay);
				Assert.assertEquals(tradeOrderList.size(), 1); // 下单
				TradeOrder to = tradeOrderList.get(0);
				Assert.assertEquals(to.getRefOrderType().intValue(), 1);
				Assert.assertEquals(to.getAmount().longValue(), order.getOrderPrice().longValue());
				Assert.assertEquals(to.getSuccAmount().longValue(), order.getOrderPrice().longValue());
				Assert.assertEquals(to.getTradeStatus().intValue(), PayConstants.TradeStatus.Successful);
				if (offlinePaymentList.size() > 0)
					Assert.assertEquals(to.getPayMethodType().intValue(),
							PayConstants.PayMethodBit.ParentCardBit + PayConstants.PayMethodBit.OfflinePayBit);
				else
					Assert.assertEquals(to.getPayMethodType().intValue(), PayConstants.PayMethodBit.ParentCardBit);
				Assert.assertEquals(to.getTradeType().intValue(), PayConstants.TradeType.pay);

				// 校验tb_trade_pay_record【交易支付表】
				List<TradePayRecord> tradePayRecList = getTradePayRecordByOrderNum(order.getOrderNum(),
						to.getTradeOrderNum(),PayConstants.OrderType.MytijianOrder);
				if (offlinePaymentList.size() > 0) {
					Assert.assertEquals(tradePayRecList.size(), 2);
					// 母卡支付
					int dbReceiveTradeAccountId = getSuitableReceiveMethodId(usehospitalId,
							PayConstants.PayMethodBit.ParentCardBit);
					TradePayRecord tpr = tradePayRecList.get(0);
					Assert.assertEquals(tpr.getRefOrderType().intValue(), 1);
					Assert.assertEquals(tpr.getTradeMethodConfigId().intValue(),
							getSuitablePayMethodId(order.getFromSite(), PayConstants.PayMethodBit.ParentCardBit));
					Assert.assertEquals(tpr.getTradeMethodType().intValue(), PayConstants.PayMethod.ParentCard);
					Assert.assertEquals(tpr.getPayStatus().intValue(), PayConstants.TradeStatus.Successful);
					Assert.assertEquals(tpr.getPayTradeAccountId().intValue(),
							getTradeAccountIdByAccountId(parentAccountId));
					Assert.assertEquals(tpr.getPayTradeSubaccountId().intValue(),
							getSubParentCardId(getTradeAccountIdByAccountId(parentAccountId)));
					Assert.assertEquals(tpr.getPayTradeSubaccountType().intValue(),
							TradeSubAccountType.TRADE_PARENT_CARD_ACCOUNT);
					Assert.assertEquals(tpr.getReceiveTradeAccountId().intValue(), dbReceiveTradeAccountId);
					Assert.assertEquals(tpr.getReceiveTradeSubaccountId().intValue(),
							getSubAccounttingId(dbReceiveTradeAccountId));
					Assert.assertEquals(tpr.getReceiveTradeSubaccountType().intValue(),
							TradeSubAccountType.TRADE_BALANCE_ACCOUNT);
					int parentAmount = tpr.getPayAmount().intValue();

					// 线下支付
					dbReceiveTradeAccountId = getSuitableReceiveMethodId(usehospitalId,
							PayConstants.PayMethodBit.OfflinePayBit);
					tpr = tradePayRecList.get(1);
					Assert.assertEquals(tpr.getRefOrderType().intValue(), 1);
					Assert.assertEquals(tpr.getTradeMethodConfigId().intValue(),
							getSuitablePayMethodId(order.getFromSite(), PayConstants.PayMethodBit.OfflinePayBit));
					Assert.assertEquals(tpr.getTradeMethodType().intValue(), PayConstants.PayMethod.OfflinePay);
					Assert.assertEquals(tpr.getPayStatus().intValue(), PayConstants.TradeStatus.Successful);
					Assert.assertEquals(tpr.getPayTradeAccountId().intValue(), getTradeAccountIdByAccountId(healthId));
					Assert.assertNull(tpr.getPayTradeSubaccountId());
					Assert.assertNull(tpr.getPayTradeSubaccountType());
					Assert.assertEquals(tpr.getReceiveTradeAccountId().intValue(), dbReceiveTradeAccountId);
					Assert.assertEquals(tpr.getReceiveTradeSubaccountId().intValue(),
							getSubAccounttingId(dbReceiveTradeAccountId));
					Assert.assertEquals(tpr.getReceiveTradeSubaccountType().intValue(),
							TradeSubAccountType.TRADE_BALANCE_ACCOUNT);
					int offlineAmount = tpr.getPayAmount().intValue();

					// 交易tb_trade_account_detail
					List<TradeAccountDetail> tradeAccountList = getTradeAccountDetail(to.getTradeOrderNum(), 0);
					Assert.assertEquals(tradeAccountList.size(), 1);
					TradeAccountDetail tad = tradeAccountList.get(0);
					Assert.assertEquals(tad.getTradeAccountId().intValue(), dbReceiveTradeAccountId);
					Assert.assertEquals(tad.getTradeSubAccountType().intValue(),
							TradeSubAccountType.TRADE_BALANCE_ACCOUNT);
					Assert.assertEquals(tad.getTradeSubAccountId().intValue(),
							getSubAccounttingId(dbReceiveTradeAccountId));
					Assert.assertEquals(tad.getTradeOrderNum(), to.getTradeOrderNum());
					Assert.assertEquals(tad.getBizType().intValue(), TradeAccountDetailBizType.PAYMENT);
					Assert.assertEquals(tad.getChangeAmount().intValue(), parentAmount);
					// 出账
					tradeAccountList = getTradeAccountDetail(to.getTradeOrderNum(), 1);
					Assert.assertEquals(tradeAccountList.size(), 1);
					// 母卡
					tad = tradeAccountList.get(0);
					Assert.assertEquals(tad.getTradeAccountId().intValue(),
							getTradeAccountIdByAccountId(parentAccountId));
					Assert.assertEquals(tad.getTradeSubAccountType().intValue(),
							TradeSubAccountType.TRADE_PARENT_CARD_ACCOUNT);
					Assert.assertEquals(tad.getTradeSubAccountId().intValue(),
							getSubParentCardId(getTradeAccountIdByAccountId(parentAccountId)));
					Assert.assertEquals(tad.getTradeOrderNum(), to.getTradeOrderNum());
					Assert.assertEquals(tad.getBizType().intValue(), TradeAccountDetailBizType.PAYMENT);
					Assert.assertEquals(tad.getChangeAmount().intValue(), parentAmount);
				} else {
					// 存母卡
					int dbReceiveTradeAccountId = getSuitableReceiveMethodId(usehospitalId,
							PayConstants.PayMethodBit.ParentCardBit);
					Assert.assertEquals(tradePayRecList.size(), 1);
					TradePayRecord tpr = tradePayRecList.get(0);
					Assert.assertEquals(tpr.getRefOrderType().intValue(), 1);
					Assert.assertEquals(tpr.getTradeMethodConfigId().intValue(),
							getSuitablePayMethodId(order.getFromSite(), PayConstants.PayMethodBit.ParentCardBit));
					Assert.assertEquals(tpr.getTradeMethodType().intValue(), PayConstants.PayMethod.ParentCard);
					Assert.assertEquals(tpr.getPayStatus().intValue(), PayConstants.TradeStatus.Successful);
					Assert.assertEquals(tpr.getPayTradeAccountId().intValue(),
							getTradeAccountIdByAccountId(parentAccountId));
					Assert.assertEquals(tpr.getPayTradeSubaccountId().intValue(),
							getSubParentCardId(getTradeAccountIdByAccountId(parentAccountId)));
					Assert.assertEquals(tpr.getPayTradeSubaccountType().intValue(),
							TradeSubAccountType.TRADE_PARENT_CARD_ACCOUNT);
					Assert.assertEquals(tpr.getReceiveTradeAccountId().intValue(), dbReceiveTradeAccountId);
					Assert.assertEquals(tpr.getReceiveTradeSubaccountId().intValue(),
							getSubAccounttingId(dbReceiveTradeAccountId));
					Assert.assertEquals(tpr.getReceiveTradeSubaccountType().intValue(),
							TradeSubAccountType.TRADE_BALANCE_ACCOUNT);

					// 交易tb_trade_account_detail
					List<TradeAccountDetail> tradeAccountList = getTradeAccountDetail(to.getTradeOrderNum(), 0);
					Assert.assertEquals(tradeAccountList.size(), 1);
					TradeAccountDetail tad = tradeAccountList.get(0);
					Assert.assertEquals(tad.getRefBizSn(), tpr.getSn());
					Assert.assertEquals(tad.getTradeAccountId().intValue(), dbReceiveTradeAccountId);
					Assert.assertEquals(tad.getTradeSubAccountType().intValue(),
							TradeSubAccountType.TRADE_BALANCE_ACCOUNT);
					Assert.assertEquals(tad.getTradeSubAccountId().intValue(),
							getSubAccounttingId(dbReceiveTradeAccountId));
					Assert.assertEquals(tad.getTradeOrderNum(), to.getTradeOrderNum());
					Assert.assertEquals(tad.getBizType().intValue(), TradeAccountDetailBizType.PAYMENT);
					Assert.assertEquals(tad.getChangeAmount().intValue(), order.getOrderPrice().intValue());
					// 出账
					tradeAccountList = getTradeAccountDetail(to.getTradeOrderNum(), 1);
					Assert.assertEquals(tradeAccountList.size(), 1);
					tad = tradeAccountList.get(0);
					Assert.assertEquals(tad.getRefBizSn(), tpr.getSn());
					Assert.assertEquals(tad.getTradeAccountId().intValue(),
							getTradeAccountIdByAccountId(parentAccountId));
					Assert.assertEquals(tad.getTradeSubAccountType().intValue(),
							TradeSubAccountType.TRADE_PARENT_CARD_ACCOUNT);
					Assert.assertEquals(tad.getTradeSubAccountId().intValue(),
							getSubParentCardId(getTradeAccountIdByAccountId(parentAccountId)));
					Assert.assertEquals(tad.getTradeOrderNum(), to.getTradeOrderNum());
					Assert.assertEquals(tad.getBizType().intValue(), TradeAccountDetailBizType.PAYMENT);
					Assert.assertEquals(tad.getChangeAmount().intValue(), order.getOrderPrice().intValue());

				}

				System.out.println("--------验证交易记录结束---------");
			}
		}
	}
	/**
	 * 验证极速预约交易部分
	 * 
	 * @param usehospitalId
	 * @param oldCompanyId
	 * @param order
	 * @param defaccountId
	 * @param onlinePay
	 * @throws Exception
	 */
	public static void checkCrmFastBookTradeOrder(int usehospitalId, HospitalCompany hCompany, Order order, int healthId,
			Boolean onlinePay, int parentAccountId) throws Exception {
		if (onlinePay) {// 在线支付扫码
			// 验证订单操作日志
			System.out.println("--------验证操作日志开始---------");
			List<ExamOrderOperateLogDO> logs = OrderChecker.getOrderOperatrLog(order.getOrderNum());
			Assert.assertEquals(logs.get(0).getType().intValue(), OrderOperateTypeEnum.BOOK_ORDER.getCode());
			Assert.assertEquals(logs.get(0).getOrderStatus().intValue(), OrderStatus.NOT_PAY.intValue());
			System.out.println("--------验证操作日志结束---------");

			if (hCompany.getPlatformCompanyId()!=null&&hCompany.getPlatformCompanyId() == 2) {
				// 检查交易状态
				System.out.println("--------验证交易记录开始---------");
				// 校验tb_paymentrecord
				String paymentSql = "select p.* from tb_paymentrecord p ,tb_payment_method m  where p.order_id = "
						+ order.getId() + " and p.payment_method_id = m.id and m.type = "
						+ PaymentMethod.ONLINE_PAY_METHOD_TYPE.intValue();
				List<Map<String, Object>> paymentList = DBMapper.query(paymentSql);
				Assert.assertEquals(paymentList.size(), 1);
				Map<String, Object> paymentMap = paymentList.get(0);
				Assert.assertEquals(Integer.parseInt(paymentMap.get("status").toString()), 0); // 未支付
				Assert.assertEquals(Integer.parseInt(paymentMap.get("trade_type").toString()), 1); // 支付类型
				Assert.assertEquals(Integer.parseInt(paymentMap.get("is_primary").toString()), 0);
				// 校验tb_trade_order【交易订单表】
				List<TradeOrder> tradeOrderList = getTradeOrderByOrderNum(order.getOrderNum(), TradeType.pay);
				Assert.assertEquals(tradeOrderList.size(), 1); // 下单
				TradeOrder to = tradeOrderList.get(0);
				Assert.assertEquals(to.getRefOrderType().intValue(), 1);
				Assert.assertEquals(to.getAmount().longValue(), order.getOrderPrice().longValue());
				Assert.assertEquals(to.getSuccAmount().longValue(), 0l);
				Assert.assertEquals(to.getTradeStatus().intValue(), PayConstants.TradeStatus.Paying);
				Assert.assertEquals(to.getPayMethodType().intValue(), PayConstants.PayMethodBit.OnlinePayBit);
				Assert.assertEquals(to.getTradeType().intValue(), PayConstants.TradeType.pay);

				// 校验tb_trade_pay_record【交易支付表】
				int dbReceiveTradeAccountId = getSuitableReceiveMethodId(usehospitalId,
						PayConstants.PayMethodBit.OnlinePayBit);
				List<TradePayRecord> tradePayRecList = getTradePayRecordByOrderNum(order.getOrderNum(),
						to.getTradeOrderNum(),PayConstants.OrderType.MytijianOrder);
				Assert.assertEquals(tradePayRecList.size(), 1);
				TradePayRecord tpr = tradePayRecList.get(0);
				Assert.assertEquals(tpr.getRefOrderType().intValue(), 1);
				Assert.assertEquals(tpr.getTradeMethodConfigId().intValue(),
						getSuitablePayMethodId(order.getFromSite(), PayConstants.PayMethodBit.OnlinePayBit));
				Assert.assertEquals(tpr.getTradeMethodType().intValue(), PayConstants.PayMethod.OnlinePay);
				Assert.assertEquals(tpr.getPayStatus().intValue(), PayConstants.TradeStatus.Paying);
				Assert.assertEquals(tpr.getPayTradeAccountId().intValue(), getTradeAccountByOrderId(order.getId()));
				Assert.assertNull(tpr.getPayTradeSubaccountId());
				Assert.assertNull(tpr.getPayTradeSubaccountType());
				Assert.assertNull(tpr.getPayTradeAccountSnap());
				Assert.assertEquals(tpr.getReceiveTradeAccountId().intValue(), dbReceiveTradeAccountId);
				Assert.assertEquals(tpr.getReceiveTradeSubaccountId().intValue(),
						getSubAccounttingId(dbReceiveTradeAccountId));
				Assert.assertEquals(tpr.getReceiveTradeSubaccountType().intValue(),
						TradeSubAccountType.TRADE_BALANCE_ACCOUNT);

				// 交易tb_trade_account_detail,支付中不往这个表插入数据【账户明细表】
				List<TradeAccountDetail> tradeAccountList = getTradeAccountDetail(to.getTradeOrderNum(), 0);
				Assert.assertEquals(tradeAccountList.size(), 0);
				// 出账
				tradeAccountList = getTradeAccountDetail(to.getTradeOrderNum(), 1);
				Assert.assertEquals(tradeAccountList.size(), 0);
				System.out.println("--------验证交易记录结束---------");
			} else {
				// 检查交易状态
				System.out.println("--------验证交易记录开始---------");
				// 校验tb_paymentrecord
				String paymentSql = "select p.* from tb_paymentrecord p ,tb_payment_method m  where p.order_id = "
						+ order.getId() + " and p.payment_method_id = m.id and m.type = "
						+ PaymentTypeEnum.Card.getCode();
				List<Map<String, Object>> paymentList = DBMapper.query(paymentSql);
				Assert.assertEquals(paymentList.size(), 1);
				Map<String, Object> paymentMap = paymentList.get(0);
				Assert.assertEquals(Integer.parseInt(paymentMap.get("status").toString()), 1); // 支付成功
				Assert.assertEquals(Integer.parseInt(paymentMap.get("trade_type").toString()), 1); // 支付类型
				Assert.assertEquals(Integer.parseInt(paymentMap.get("is_primary").toString()), 1);// 母卡
				Assert.assertEquals(Integer.parseInt(paymentMap.get("expense_account").toString()),
						CardChecker.getParentEntryCard(parentAccountId).intValue());
				int parentCardAmount = Integer.parseInt(paymentMap.get("amount").toString());

				paymentSql = "select p.* from tb_paymentrecord p ,tb_payment_method m  where p.order_id = "
						+ order.getId() + " and p.payment_method_id = m.id and m.type = "
						+ PaymentMethod.ONLINE_PAY_METHOD_TYPE;
				List<Map<String, Object>> onlinePaymentList = DBMapper.query(paymentSql);
				if (onlinePaymentList.size() > 0) {
					Assert.assertEquals(onlinePaymentList.size(), 1);
					paymentMap = onlinePaymentList.get(0);
					Assert.assertEquals(Integer.parseInt(paymentMap.get("status").toString()), 0); // 支付中
					Assert.assertEquals(Integer.parseInt(paymentMap.get("trade_type").toString()), 1); // 支付类型
					Assert.assertEquals(Integer.parseInt(paymentMap.get("is_primary").toString()), 0);
				}
				// 校验tb_trade_order【交易订单表】
				List<TradeOrder> tradeOrderList = getTradeOrderByOrderNum(order.getOrderNum(), TradeType.pay);
				Assert.assertEquals(tradeOrderList.size(), 1); // 下单
				TradeOrder to = tradeOrderList.get(0);
				Assert.assertEquals(to.getRefOrderType().intValue(), 1);
				Assert.assertEquals(to.getAmount().longValue(), order.getOrderPrice().longValue());
				Assert.assertEquals(to.getSuccAmount().longValue(), parentCardAmount);
				if (onlinePaymentList.size() > 0) {
					Assert.assertEquals(to.getTradeStatus().intValue(), PayConstants.TradeStatus.Paying);
					Assert.assertEquals(to.getPayMethodType().intValue(),
							PayConstants.PayMethodBit.ParentCardBit + PayMethodBit.OnlinePayBit);
				} else {
					Assert.assertEquals(to.getTradeStatus().intValue(), PayConstants.TradeStatus.Successful);
					Assert.assertEquals(to.getPayMethodType().intValue(), PayConstants.PayMethodBit.ParentCardBit);
				}
				Assert.assertEquals(to.getTradeType().intValue(), PayConstants.TradeType.pay);

				// 校验tb_trade_pay_record【交易支付表】
				List<TradePayRecord> tradePayRecList = getTradePayRecordByOrderNum(order.getOrderNum(),
						to.getTradeOrderNum(),PayConstants.OrderType.MytijianOrder);
				if (onlinePaymentList.size() > 0) {
					Assert.assertEquals(tradePayRecList.size(), 2);
					// 线上付款
					int dbReceiveTradeAccountId = getSuitableReceiveMethodId(usehospitalId,
							PayConstants.PayMethodBit.OnlinePayBit);
					TradePayRecord tpr = tradePayRecList.get(1);
					Assert.assertEquals(tpr.getRefOrderType().intValue(), 1);
					Assert.assertEquals(tpr.getTradeMethodConfigId().intValue(),
							getSuitablePayMethodId(order.getFromSite(), PayConstants.PayMethodBit.OnlinePayBit));
					Assert.assertEquals(tpr.getTradeMethodType().intValue(), PayConstants.PayMethod.OnlinePay);
					Assert.assertEquals(tpr.getPayStatus().intValue(), PayConstants.TradeStatus.Paying);
					Assert.assertEquals(tpr.getPayTradeAccountId().intValue(), getTradeAccountIdByAccountId(healthId));
					Assert.assertNull(tpr.getPayTradeSubaccountId());
					Assert.assertNull(tpr.getPayTradeSubaccountType());
					Assert.assertNull(tpr.getPayTradeAccountSnap());
					Assert.assertEquals(tpr.getReceiveTradeAccountId().intValue(), dbReceiveTradeAccountId);
					Assert.assertEquals(tpr.getReceiveTradeSubaccountId().intValue(),
							getSubAccounttingId(dbReceiveTradeAccountId));
					Assert.assertEquals(tpr.getReceiveTradeSubaccountType().intValue(),
							TradeSubAccountType.TRADE_BALANCE_ACCOUNT);

					// 母卡支付
					dbReceiveTradeAccountId = getSuitableReceiveMethodId(usehospitalId,
							PayConstants.PayMethodBit.ParentCardBit);
					tpr = tradePayRecList.get(0);
					Assert.assertEquals(tpr.getRefOrderType().intValue(), 1);
					Assert.assertEquals(tpr.getTradeMethodConfigId().intValue(),
							getSuitablePayMethodId(order.getFromSite(), PayConstants.PayMethodBit.ParentCardBit));
					Assert.assertEquals(tpr.getTradeMethodType().intValue(), PayConstants.PayMethod.ParentCard);
					Assert.assertEquals(tpr.getPayStatus().intValue(), PayConstants.TradeStatus.Successful);
					Assert.assertEquals(tpr.getPayTradeAccountId().intValue(),
							getTradeAccountIdByAccountId(parentAccountId));
					Assert.assertEquals(tpr.getPayTradeSubaccountId().intValue(),
							getSubParentCardId(getTradeAccountIdByAccountId(parentAccountId)));
					Assert.assertEquals(tpr.getPayTradeSubaccountType().intValue(),
							TradeSubAccountType.TRADE_PARENT_CARD_ACCOUNT);
					Assert.assertEquals(tpr.getReceiveTradeAccountId().intValue(), dbReceiveTradeAccountId);
					Assert.assertEquals(tpr.getReceiveTradeSubaccountId().intValue(),
							getSubAccounttingId(dbReceiveTradeAccountId));
					Assert.assertEquals(tpr.getReceiveTradeSubaccountType().intValue(),
							TradeSubAccountType.TRADE_BALANCE_ACCOUNT);

					// 交易tb_trade_account_detail,支付中不往这个表插入数据【账户明细表】
					List<TradeAccountDetail> tradeAccountList = getTradeAccountDetail(to.getTradeOrderNum(), 0);
					Assert.assertEquals(tradeAccountList.size(), 1);
					TradeAccountDetail tad = tradeAccountList.get(0);
					Assert.assertEquals(tad.getRefBizSn(), tpr.getSn());
					Assert.assertEquals(tad.getTradeAccountId().intValue(), dbReceiveTradeAccountId);
					Assert.assertEquals(tad.getTradeSubAccountType().intValue(),
							TradeSubAccountType.TRADE_BALANCE_ACCOUNT);
					Assert.assertEquals(tad.getTradeSubAccountId().intValue(),
							getSubAccounttingId(dbReceiveTradeAccountId));
					Assert.assertEquals(tad.getTradeOrderNum(), to.getTradeOrderNum());
					Assert.assertEquals(tad.getBizType().intValue(), TradeAccountDetailBizType.PAYMENT);
					Assert.assertEquals(tad.getChangeAmount().intValue(), parentCardAmount);
					// 出账
					tradeAccountList = getTradeAccountDetail(to.getTradeOrderNum(), 1);
					Assert.assertEquals(tradeAccountList.size(), 1);
					tad = tradeAccountList.get(0);
					Assert.assertEquals(tad.getRefBizSn(), tpr.getSn());
					Assert.assertEquals(tad.getTradeAccountId().intValue(),
							getTradeAccountIdByAccountId(parentAccountId));
					Assert.assertEquals(tad.getTradeSubAccountType().intValue(),
							TradeSubAccountType.TRADE_PARENT_CARD_ACCOUNT);
					Assert.assertEquals(tad.getTradeSubAccountId().intValue(),
							getSubParentCardId(getTradeAccountIdByAccountId(parentAccountId)));
					Assert.assertEquals(tad.getTradeOrderNum(), to.getTradeOrderNum());
					Assert.assertEquals(tad.getBizType().intValue(), TradeAccountDetailBizType.PAYMENT);
					Assert.assertEquals(tad.getChangeAmount().intValue(), parentCardAmount);
				} else {
					Assert.assertEquals(tradePayRecList.size(), 1);
					int dbReceiveTradeAccountId = getSuitableReceiveMethodId(usehospitalId,
							PayConstants.PayMethodBit.ParentCardBit);
					// 存母卡
					TradePayRecord tpr = tradePayRecList.get(0);
					Assert.assertEquals(tpr.getRefOrderType().intValue(), 1);
					Assert.assertEquals(tpr.getTradeMethodConfigId().intValue(),
							getSuitablePayMethodId(order.getFromSite(), PayConstants.PayMethodBit.ParentCardBit));
					Assert.assertEquals(tpr.getTradeMethodType().intValue(), PayConstants.PayMethod.ParentCard);
					Assert.assertEquals(tpr.getPayStatus().intValue(), PayConstants.TradeStatus.Successful);
					Assert.assertEquals(tpr.getPayTradeAccountId().intValue(),
							getTradeAccountIdByAccountId(parentAccountId));
					Assert.assertEquals(tpr.getPayTradeSubaccountId().intValue(),
							getSubParentCardId(getTradeAccountIdByAccountId(parentAccountId)));
					Assert.assertEquals(tpr.getPayTradeSubaccountType().intValue(),
							TradeSubAccountType.TRADE_PARENT_CARD_ACCOUNT);
					Assert.assertEquals(tpr.getReceiveTradeAccountId().intValue(), dbReceiveTradeAccountId);
					Assert.assertEquals(tpr.getReceiveTradeSubaccountId().intValue(),
							getSubAccounttingId(dbReceiveTradeAccountId));
					Assert.assertEquals(tpr.getReceiveTradeSubaccountType().intValue(),
							TradeSubAccountType.TRADE_BALANCE_ACCOUNT);

					// 交易tb_trade_account_detail,支付中不往这个表插入数据【账户明细表】
					List<TradeAccountDetail> tradeAccountList = getTradeAccountDetail(to.getTradeOrderNum(), 0);
					Assert.assertEquals(tradeAccountList.size(), 1);
					TradeAccountDetail tad = tradeAccountList.get(0);
					Assert.assertEquals(tad.getRefBizSn(), tpr.getSn());
					Assert.assertEquals(tad.getTradeAccountId().intValue(), dbReceiveTradeAccountId);
					Assert.assertEquals(tad.getTradeSubAccountType().intValue(),
							TradeSubAccountType.TRADE_BALANCE_ACCOUNT);
					Assert.assertEquals(tad.getTradeSubAccountId().intValue(),
							getSubAccounttingId(dbReceiveTradeAccountId));
					Assert.assertEquals(tad.getTradeOrderNum(), to.getTradeOrderNum());
					Assert.assertEquals(tad.getBizType().intValue(), TradeAccountDetailBizType.PAYMENT);
					Assert.assertEquals(tad.getChangeAmount().intValue(), order.getOrderPrice().intValue());
					// 出账
					tradeAccountList = getTradeAccountDetail(to.getTradeOrderNum(), 1);
					Assert.assertEquals(tradeAccountList.size(), 1);
					tad = tradeAccountList.get(0);
					Assert.assertEquals(tad.getRefBizSn(), tpr.getSn());
					Assert.assertEquals(tad.getTradeAccountId().intValue(),
							getTradeAccountIdByAccountId(parentAccountId));
					Assert.assertEquals(tad.getTradeSubAccountType().intValue(),
							TradeSubAccountType.TRADE_PARENT_CARD_ACCOUNT);
					Assert.assertEquals(tad.getTradeSubAccountId().intValue(),
							getSubParentCardId(getTradeAccountIdByAccountId(parentAccountId)));
					Assert.assertEquals(tad.getTradeOrderNum(), to.getTradeOrderNum());
					Assert.assertEquals(tad.getBizType().intValue(), TradeAccountDetailBizType.PAYMENT);
					Assert.assertEquals(tad.getChangeAmount().intValue(), order.getOrderPrice().intValue());
				}
				System.out.println("--------验证交易记录结束---------");
			}
		} else {// 极速预约线下付款....
			if (hCompany.getPlatformCompanyId()!=null&&hCompany.getPlatformCompanyId() == 2) {
				// 检查交易状态
				// 验证订单操作日志
				System.out.println("--------验证操作日志开始---------");
				List<ExamOrderOperateLogDO> logs = OrderChecker.getOrderOperatrLog(order.getOrderNum());
				Assert.assertEquals(logs.get(0).getType().intValue(), OrderOperateTypeEnum.FINISH_PAY.getCode());
				Assert.assertEquals(logs.get(0).getOrderStatus().intValue(), OrderStatus.ALREADY_BOOKED.intValue());
				System.out.println("--------验证操作日志结束---------");

				System.out.println("--------验证交易记录开始---------");
				// 校验tb_paymentrecord
				String paymentSql = "select p.* from tb_paymentrecord p ,tb_payment_method m  where p.order_id = "
						+ order.getId() + " and p.payment_method_id = m.id and m.type = "
						+ PaymentTypeEnum.Offline.getCode();
				List<Map<String, Object>> paymentList = DBMapper.query(paymentSql);
				Assert.assertEquals(paymentList.size(), 1);
				Map<String, Object> paymentMap = paymentList.get(0);
				Assert.assertEquals(Integer.parseInt(paymentMap.get("status").toString()), 1); // 支付成功
				Assert.assertEquals(Integer.parseInt(paymentMap.get("trade_type").toString()), 1); // 支付类型
				Assert.assertEquals(Integer.parseInt(paymentMap.get("is_primary").toString()), 0);
				// 校验tb_trade_order【交易订单表】
				List<TradeOrder> tradeOrderList = getTradeOrderByOrderNum(order.getOrderNum(), TradeType.pay);
				Assert.assertEquals(tradeOrderList.size(), 1); // 下单
				TradeOrder to = tradeOrderList.get(0);
				Assert.assertEquals(to.getRefOrderType().intValue(), 1);
				Assert.assertEquals(to.getAmount().longValue(), order.getOrderPrice().longValue());
				Assert.assertEquals(to.getSuccAmount().longValue(), order.getOrderPrice().longValue());
				Assert.assertEquals(to.getTradeStatus().intValue(), PayConstants.TradeStatus.Successful);
				Assert.assertEquals(to.getPayMethodType().intValue(), PayConstants.PayMethodBit.OfflinePayBit);
				Assert.assertEquals(to.getTradeType().intValue(), PayConstants.TradeType.pay);

				// 校验tb_trade_pay_record【交易支付表】
				int dbReceiveTradeAccountId = getSuitableReceiveMethodId(usehospitalId,
						PayConstants.PayMethodBit.OfflinePayBit);
				List<TradePayRecord> tradePayRecList = getTradePayRecordByOrderNum(order.getOrderNum(),
						to.getTradeOrderNum(),PayConstants.OrderType.MytijianOrder);
				Assert.assertEquals(tradePayRecList.size(), 1);
				TradePayRecord tpr = tradePayRecList.get(0);
				Assert.assertEquals(tpr.getRefOrderType().intValue(), 1);
				Assert.assertEquals(tpr.getTradeMethodConfigId().intValue(),
						getSuitablePayMethodId(order.getFromSite(), PayConstants.PayMethodBit.OfflinePayBit));
				Assert.assertEquals(tpr.getTradeMethodType().intValue(), PayConstants.PayMethod.OfflinePay);
				Assert.assertEquals(tpr.getPayStatus().intValue(), PayConstants.TradeStatus.Successful);
				Assert.assertEquals(tpr.getPayTradeAccountId().intValue(), getTradeAccountByOrderId(order.getId()));
				Assert.assertNull(tpr.getPayTradeSubaccountId());
				Assert.assertNull(tpr.getPayTradeSubaccountType());
				Assert.assertNull(tpr.getPayTradeAccountSnap());
				Assert.assertEquals(tpr.getReceiveTradeAccountId().intValue(), dbReceiveTradeAccountId);
				Assert.assertEquals(tpr.getReceiveTradeSubaccountId().intValue(),
						getSubAccounttingId(dbReceiveTradeAccountId));
				Assert.assertEquals(tpr.getReceiveTradeSubaccountType().intValue(),
						TradeSubAccountType.TRADE_BALANCE_ACCOUNT);

				// 交易tb_trade_account_detail,支付中不往这个表插入数据【账户明细表】
				List<TradeAccountDetail> tradeAccountList = getTradeAccountDetail(to.getTradeOrderNum(), 0);
				Assert.assertEquals(tradeAccountList.size(), 0);
				// 出账
				tradeAccountList = getTradeAccountDetail(to.getTradeOrderNum(), 1);
				Assert.assertEquals(tradeAccountList.size(), 0);
				System.out.println("--------验证交易记录结束---------");
			} else {
				// 检查交易状态
				// 验证订单操作日志
				System.out.println("--------验证操作日志开始---------");
				List<ExamOrderOperateLogDO> logs = OrderChecker.getOrderOperatrLog(order.getOrderNum());
				Assert.assertEquals(logs.get(0).getType().intValue(), OrderOperateTypeEnum.FINISH_PAY.getCode());
				Assert.assertEquals(logs.get(0).getOrderStatus().intValue(), OrderStatus.ALREADY_BOOKED.intValue());
				System.out.println("--------验证操作日志结束---------");

				System.out.println("--------验证交易记录开始---------");
				// 校验tb_paymentrecord
				String paymentSql = "select p.* from tb_paymentrecord p ,tb_payment_method m  where p.order_id = "
						+ order.getId() + " and p.payment_method_id = m.id and m.type = "
						+ PaymentTypeEnum.Card.getCode();
				List<Map<String, Object>> paymentList = DBMapper.query(paymentSql);
				Assert.assertEquals(paymentList.size(), 1);
				Map<String, Object> paymentMap = paymentList.get(0);
				Assert.assertEquals(Integer.parseInt(paymentMap.get("status").toString()), 1); // 支付成功
				Assert.assertEquals(Integer.parseInt(paymentMap.get("trade_type").toString()), 1); // 支付类型
				Assert.assertEquals(Integer.parseInt(paymentMap.get("is_primary").toString()), 1);// 母卡
				Assert.assertEquals(Integer.parseInt(paymentMap.get("expense_account").toString()),
						CardChecker.getParentEntryCard(parentAccountId).intValue());

				paymentSql = "select p.* from tb_paymentrecord p ,tb_payment_method m  where p.order_id = "
						+ order.getId() + " and p.payment_method_id = m.id and m.type = "
						+ PaymentTypeEnum.Offline.getCode();
				List<Map<String, Object>> offlinePaymentList = DBMapper.query(paymentSql);
				if (offlinePaymentList.size() > 0) {
					Assert.assertEquals(offlinePaymentList.size(), 1);
					paymentMap = offlinePaymentList.get(0);
					Assert.assertEquals(Integer.parseInt(paymentMap.get("status").toString()), 1); // 支付成功
					Assert.assertEquals(Integer.parseInt(paymentMap.get("trade_type").toString()), 1); // 支付类型
					Assert.assertEquals(Integer.parseInt(paymentMap.get("is_primary").toString()), 0);
				}

				// 校验tb_trade_order【交易订单表】
				List<TradeOrder> tradeOrderList = getTradeOrderByOrderNum(order.getOrderNum(), TradeType.pay);
				Assert.assertEquals(tradeOrderList.size(), 1); // 下单
				TradeOrder to = tradeOrderList.get(0);
				Assert.assertEquals(to.getRefOrderType().intValue(), 1);
				Assert.assertEquals(to.getAmount().longValue(), order.getOrderPrice().longValue());
				Assert.assertEquals(to.getSuccAmount().longValue(), order.getOrderPrice().longValue());
				Assert.assertEquals(to.getTradeStatus().intValue(), PayConstants.TradeStatus.Successful);
				if (offlinePaymentList.size() > 0)
					Assert.assertEquals(to.getPayMethodType().intValue(),
							PayConstants.PayMethodBit.ParentCardBit + PayConstants.PayMethodBit.OfflinePayBit);
				else
					Assert.assertEquals(to.getPayMethodType().intValue(), PayConstants.PayMethodBit.ParentCardBit);
				Assert.assertEquals(to.getTradeType().intValue(), PayConstants.TradeType.pay);

				// 校验tb_trade_pay_record【交易支付表】
				List<TradePayRecord> tradePayRecList = getTradePayRecordByOrderNum(order.getOrderNum(),
						to.getTradeOrderNum(),PayConstants.OrderType.MytijianOrder);
				if (offlinePaymentList.size() > 0) {
					Assert.assertEquals(tradePayRecList.size(), 2);
					// 母卡支付
					int dbReceiveTradeAccountId = getSuitableReceiveMethodId(usehospitalId,
							PayConstants.PayMethodBit.ParentCardBit);
					TradePayRecord tpr = tradePayRecList.get(0);
					Assert.assertEquals(tpr.getRefOrderType().intValue(), 1);
					Assert.assertEquals(tpr.getTradeMethodConfigId().intValue(),
							getSuitablePayMethodId(order.getFromSite(), PayConstants.PayMethodBit.ParentCardBit));
					Assert.assertEquals(tpr.getTradeMethodType().intValue(), PayConstants.PayMethod.ParentCard);
					Assert.assertEquals(tpr.getPayStatus().intValue(), PayConstants.TradeStatus.Successful);
					Assert.assertEquals(tpr.getPayTradeAccountId().intValue(),
							getTradeAccountIdByAccountId(parentAccountId));
					Assert.assertEquals(tpr.getPayTradeSubaccountId().intValue(),
							getSubParentCardId(getTradeAccountIdByAccountId(parentAccountId)));
					Assert.assertEquals(tpr.getPayTradeSubaccountType().intValue(),
							TradeSubAccountType.TRADE_PARENT_CARD_ACCOUNT);
					Assert.assertEquals(tpr.getReceiveTradeAccountId().intValue(), dbReceiveTradeAccountId);
					Assert.assertEquals(tpr.getReceiveTradeSubaccountId().intValue(),
							getSubAccounttingId(dbReceiveTradeAccountId));
					Assert.assertEquals(tpr.getReceiveTradeSubaccountType().intValue(),
							TradeSubAccountType.TRADE_BALANCE_ACCOUNT);
					int parentAmount = tpr.getPayAmount().intValue();

					// 线下支付
					dbReceiveTradeAccountId = getSuitableReceiveMethodId(usehospitalId,
							PayConstants.PayMethodBit.OfflinePayBit);
					tpr = tradePayRecList.get(1);
					Assert.assertEquals(tpr.getRefOrderType().intValue(), 1);
					Assert.assertEquals(tpr.getTradeMethodConfigId().intValue(),
							getSuitablePayMethodId(order.getFromSite(), PayConstants.PayMethodBit.OfflinePayBit));
					Assert.assertEquals(tpr.getTradeMethodType().intValue(), PayConstants.PayMethod.OfflinePay);
					Assert.assertEquals(tpr.getPayStatus().intValue(), PayConstants.TradeStatus.Successful);
					Assert.assertEquals(tpr.getPayTradeAccountId().intValue(), getTradeAccountIdByAccountId(healthId));
					Assert.assertNull(tpr.getPayTradeSubaccountId());
					Assert.assertNull(tpr.getPayTradeSubaccountType());
					Assert.assertEquals(tpr.getReceiveTradeAccountId().intValue(), dbReceiveTradeAccountId);
					Assert.assertEquals(tpr.getReceiveTradeSubaccountId().intValue(),
							getSubAccounttingId(dbReceiveTradeAccountId));
					Assert.assertEquals(tpr.getReceiveTradeSubaccountType().intValue(),
							TradeSubAccountType.TRADE_BALANCE_ACCOUNT);
					int offlineAmount = tpr.getPayAmount().intValue();

					// 交易tb_trade_account_detail
					List<TradeAccountDetail> tradeAccountList = getTradeAccountDetail(to.getTradeOrderNum(), 0);
					Assert.assertEquals(tradeAccountList.size(), 1);
					TradeAccountDetail tad = tradeAccountList.get(0);
					Assert.assertEquals(tad.getTradeAccountId().intValue(), dbReceiveTradeAccountId);
					Assert.assertEquals(tad.getTradeSubAccountType().intValue(),
							TradeSubAccountType.TRADE_BALANCE_ACCOUNT);
					Assert.assertEquals(tad.getTradeSubAccountId().intValue(),
							getSubAccounttingId(dbReceiveTradeAccountId));
					Assert.assertEquals(tad.getTradeOrderNum(), to.getTradeOrderNum());
					Assert.assertEquals(tad.getBizType().intValue(), TradeAccountDetailBizType.PAYMENT);
					Assert.assertEquals(tad.getChangeAmount().intValue(), parentAmount);
					// 出账
					tradeAccountList = getTradeAccountDetail(to.getTradeOrderNum(), 1);
					Assert.assertEquals(tradeAccountList.size(), 1);
					// 母卡
					tad = tradeAccountList.get(0);
					Assert.assertEquals(tad.getTradeAccountId().intValue(),
							getTradeAccountIdByAccountId(parentAccountId));
					Assert.assertEquals(tad.getTradeSubAccountType().intValue(),
							TradeSubAccountType.TRADE_PARENT_CARD_ACCOUNT);
					Assert.assertEquals(tad.getTradeSubAccountId().intValue(),
							getSubParentCardId(getTradeAccountIdByAccountId(parentAccountId)));
					Assert.assertEquals(tad.getTradeOrderNum(), to.getTradeOrderNum());
					Assert.assertEquals(tad.getBizType().intValue(), TradeAccountDetailBizType.PAYMENT);
					Assert.assertEquals(tad.getChangeAmount().intValue(), parentAmount);
				} else {
					// 存母卡
					int dbReceiveTradeAccountId = getSuitableReceiveMethodId(usehospitalId,
							PayConstants.PayMethodBit.ParentCardBit);
					Assert.assertEquals(tradePayRecList.size(), 1);
					TradePayRecord tpr = tradePayRecList.get(0);
					Assert.assertEquals(tpr.getRefOrderType().intValue(), 1);
					Assert.assertEquals(tpr.getTradeMethodConfigId().intValue(),
							getSuitablePayMethodId(order.getFromSite(), PayConstants.PayMethodBit.ParentCardBit));
					Assert.assertEquals(tpr.getTradeMethodType().intValue(), PayConstants.PayMethod.ParentCard);
					Assert.assertEquals(tpr.getPayStatus().intValue(), PayConstants.TradeStatus.Successful);
					Assert.assertEquals(tpr.getPayTradeAccountId().intValue(),
							getTradeAccountIdByAccountId(parentAccountId));
					Assert.assertEquals(tpr.getPayTradeSubaccountId().intValue(),
							getSubParentCardId(getTradeAccountIdByAccountId(parentAccountId)));
					Assert.assertEquals(tpr.getPayTradeSubaccountType().intValue(),
							TradeSubAccountType.TRADE_PARENT_CARD_ACCOUNT);
					Assert.assertEquals(tpr.getReceiveTradeAccountId().intValue(), dbReceiveTradeAccountId);
					Assert.assertEquals(tpr.getReceiveTradeSubaccountId().intValue(),
							getSubAccounttingId(dbReceiveTradeAccountId));
					Assert.assertEquals(tpr.getReceiveTradeSubaccountType().intValue(),
							TradeSubAccountType.TRADE_BALANCE_ACCOUNT);

					// 交易tb_trade_account_detail
					List<TradeAccountDetail> tradeAccountList = getTradeAccountDetail(to.getTradeOrderNum(), 0);
					Assert.assertEquals(tradeAccountList.size(), 1);
					TradeAccountDetail tad = tradeAccountList.get(0);
					Assert.assertEquals(tad.getRefBizSn(), tpr.getSn());
					Assert.assertEquals(tad.getTradeAccountId().intValue(), dbReceiveTradeAccountId);
					Assert.assertEquals(tad.getTradeSubAccountType().intValue(),
							TradeSubAccountType.TRADE_BALANCE_ACCOUNT);
					Assert.assertEquals(tad.getTradeSubAccountId().intValue(),
							getSubAccounttingId(dbReceiveTradeAccountId));
					Assert.assertEquals(tad.getTradeOrderNum(), to.getTradeOrderNum());
					Assert.assertEquals(tad.getBizType().intValue(), TradeAccountDetailBizType.PAYMENT);
					Assert.assertEquals(tad.getChangeAmount().intValue(), order.getOrderPrice().intValue());
					// 出账
					tradeAccountList = getTradeAccountDetail(to.getTradeOrderNum(), 1);
					Assert.assertEquals(tradeAccountList.size(), 1);
					tad = tradeAccountList.get(0);
					Assert.assertEquals(tad.getRefBizSn(), tpr.getSn());
					Assert.assertEquals(tad.getTradeAccountId().intValue(),
							getTradeAccountIdByAccountId(parentAccountId));
					Assert.assertEquals(tad.getTradeSubAccountType().intValue(),
							TradeSubAccountType.TRADE_PARENT_CARD_ACCOUNT);
					Assert.assertEquals(tad.getTradeSubAccountId().intValue(),
							getSubParentCardId(getTradeAccountIdByAccountId(parentAccountId)));
					Assert.assertEquals(tad.getTradeOrderNum(), to.getTradeOrderNum());
					Assert.assertEquals(tad.getBizType().intValue(), TradeAccountDetailBizType.PAYMENT);
					Assert.assertEquals(tad.getChangeAmount().intValue(), order.getOrderPrice().intValue());

				}

				System.out.println("--------验证交易记录结束---------");
			}

		}
	}

	/************************************
	 * 交易部分 START
	 *******************************************/
	/**
	 * 返回tb_trade_order数据，1个订单可对应多条记录
	 * 
	 * @param ref_order_num
	 *            订单编号
	 * @return
	 * @throws SqlException
	 */
	public static List<TradeOrder> getTradeOrderByOrderNum(String ref_order_num, int trade_type) throws SqlException {
		List<TradeOrder> list = new ArrayList<TradeOrder>();
		String sql = "select * from tb_trade_order where ref_order_num = '" + ref_order_num
				+ "' and trade_type = ? order by id desc";
		List<Map<String, Object>> dblist = DBMapper.query(sql, trade_type);
		for (Map<String, Object> map : dblist) {
			TradeOrder t = new TradeOrder();
			t.setId(Integer.parseInt(map.get("id").toString()));
			t.setTradeOrderNum(map.get("trade_order_num").toString());
			t.setRefOrderNum(ref_order_num);
			if (map.get("ref_order_num_version") != null)
				t.setRefOrderNumVersion(map.get("ref_order_num_version").toString());
			t.setRefOrderType(Integer.parseInt(map.get("ref_order_type").toString()));
			t.setTradeType(Integer.parseInt(map.get("trade_type").toString()));
			t.setAmount(Long.parseLong(map.get("amount").toString()));
			t.setSuccAmount(Long.parseLong(map.get("succ_amount").toString()));
			if (map.get("pay_method_type") != null)
				t.setPayMethodType(Integer.parseInt(map.get("pay_method_type").toString()));
			t.setTradeStatus(Integer.parseInt(map.get("trade_status").toString()));
			if (map.get("extra_common_param") != null)
				t.setExtraCommonParam(map.get("extra_common_param").toString());
			if (map.get("coupon_id") != null){
				String jsonString = map.get("coupon_id").toString();
				Object hospitalCoupon = JsonPath.read(jsonString,"$.hospitalCouponIds");
				Object platformCoupon = JsonPath.read(jsonString,"$.platformCouponIds");
				if(hospitalCoupon != null){
					String t1 = hospitalCoupon.toString().substring(1,hospitalCoupon.toString().length()-1);
					log.info("t1.."+t1);
					t.setHospitalCouponId(Integer.parseInt(t1));
				}
				if(platformCoupon != null){
					String t2 = platformCoupon.toString().substring(1,platformCoupon.toString().length()-1);
					log.info("t2.."+t2);
					t.setPlatformCouponId(Integer.parseInt(t2));
				}

			}
			list.add(t);
		}
		return list;
	}
	
	
	/**
	 * 根据订单号,获取所有的退款方式汇总
	 * key,pcard,online,platform,card,offline
	 * @param ref_order_Num
	 * @return
	 * @throws SqlException 
	 */
	public static RefundAmount getRefundAmountByOrderNum(String ref_order_Num,int ref_order_type) throws SqlException{
		RefundAmount amount = new RefundAmount();
		long pcard = 0;//普通单位母卡退款
		long online = 0;//线上退款
		long platform = 0;//平台退款
		long card = 0;//卡退款
		long offline = 0;//线下退款
		long hosCoupon = 0;//医院优惠券退款
		long platformCoupon = 0;//平台优惠券退款
		long channelCoupon = 0;//渠道优惠券退款
		long hosOnline = 0;//医院线上
		long platformOnline = 0;//平台线上
		long channelOnline = 0;//渠道线上
		long channelCompanyPcard = 0;//渠道母卡
		long channelCard = 0;//渠道体检卡
		boolean isPlatCompany = false;
		//体检订单
		//获取是否使用平台支付配置
		int hospitalId = -1;
		int fromSite = -1;
		int orgType = OrganizationTypeEnum.HOSPITAL.getCode();
		if(ref_order_type == PayConstants.OrderType.MytijianOrder) {
			Order order = OrderChecker.getOrderInfo(ref_order_Num);
			hospitalId = order.getHospital().getId();
			fromSite = order.getFromSite();
			orgType = order.getFromSiteOrgType();
			HospitalExamCompanySnapshot hc = order.getHospitalCompany();
			if(hc.getPlatformCompanyId()!=null){
				int platcompanyId = hc.getPlatformCompanyId();
				if(platcompanyId != 1 && platcompanyId != 2 )
					isPlatCompany = true;
			}
		}else if (ref_order_type == PayConstants.OrderType.PaymentOrder){
			PaymentOrder order1 = OrderChecker.getPaymentOrderInfo(ref_order_Num);
			hospitalId = order1.getHospitalId();
			fromSite = order1.getHospitalId();
		}
		List<TradeRefundRecord> refunds = getTradeRefundRecordByOrderNum(ref_order_Num,null,ref_order_type);
		for(TradeRefundRecord p : refunds){
			int trade_method_config_id = p.getTradeMethodConfigId();
			//忽略退款失败的的记录 (只统计退款中+退款成功)
			if(p.getRefundStatus().intValue() != RefundConstants.RefundStatus.REFUND_SUCCESS && p.getRefundStatus().intValue() != RefundConstants.RefundStatus.REFUNDING)
				continue;
			if(p.getTradeMethodType().intValue() == PayConstants.PayMethod.Alipay){
				online += p.getRefundAmount();
				if(orgType == OrganizationTypeEnum.CHANNEL.getCode())//渠道站点
					if(isUsePlatPaymentConfig(fromSite,PayConstants.PayMethod.Alipay,trade_method_config_id))
						platformOnline += p.getRefundAmount();
					else
						channelOnline += p.getRefundAmount();
				else//体检中心
					if(isUsePlatPaymentConfig(fromSite,PayConstants.PayMethod.Alipay,trade_method_config_id))
						platformOnline += p.getRefundAmount();
					else
						hosOnline += p.getRefundAmount();
			}
			if(p.getTradeMethodType().intValue() == PayConstants.PayMethod.AlipayScan){
					online += p.getRefundAmount();
				if(orgType == OrganizationTypeEnum.CHANNEL.getCode())//渠道站点
					if(isUsePlatPaymentConfig(fromSite,PayConstants.PayMethod.AlipayScan,trade_method_config_id))
						platformOnline += p.getRefundAmount();
					else
						channelOnline += p.getRefundAmount();
				else//体检中心
					if(isUsePlatPaymentConfig(fromSite,PayConstants.PayMethod.AlipayScan,trade_method_config_id))
						platformOnline += p.getRefundAmount();
					else
						hosOnline += p.getRefundAmount();
			}
			if(p.getTradeMethodType().intValue() == PayConstants.PayMethod.Wxpay){
					online += p.getRefundAmount();
				if(orgType == OrganizationTypeEnum.CHANNEL.getCode())//渠道站点
					if(isUsePlatPaymentConfig(fromSite,PayConstants.PayMethod.Wxpay,trade_method_config_id))
						platformOnline += p.getRefundAmount();
					else
						channelOnline += p.getRefundAmount();
				else//体检中心
					if(isUsePlatPaymentConfig(fromSite,PayConstants.PayMethod.Wxpay,trade_method_config_id))
						platformOnline += p.getRefundAmount();
					else
						hosOnline += p.getRefundAmount();
			}
			if(p.getTradeMethodType().intValue() == PayConstants.PayMethod.WxpayScan){
					online += p.getRefundAmount();
				if(orgType == OrganizationTypeEnum.CHANNEL.getCode())//渠道站点
					if(isUsePlatPaymentConfig(fromSite,PayConstants.PayMethod.WxpayScan,trade_method_config_id))
						platformOnline += p.getRefundAmount();
					else
						channelOnline += p.getRefundAmount();
				else//体检中心
					if(isUsePlatPaymentConfig(fromSite,PayConstants.PayMethod.WxpayScan,trade_method_config_id))
						platformOnline += p.getRefundAmount();
					else
						hosOnline += p.getRefundAmount();
			}
			if(p.getTradeMethodType().intValue() == PayConstants.PayMethod.OnlinePay){
					online += p.getRefundAmount();
				if(orgType == OrganizationTypeEnum.CHANNEL.getCode())//渠道站点
					if(isUsePlatPaymentConfig(fromSite,PayConstants.PayMethod.OnlinePay,trade_method_config_id))
						platformOnline += p.getRefundAmount();
					else
						channelOnline += p.getRefundAmount();
				else//体检中心
					if(isUsePlatPaymentConfig(fromSite,PayConstants.PayMethod.OnlinePay,trade_method_config_id))
						platformOnline += p.getRefundAmount();
					else
						hosOnline += p.getRefundAmount();
			}
			if(p.getTradeMethodType().intValue() == PayConstants.PayMethod.Balance){//余额退款全部挂平台线上退款
					online += p.getRefundAmount();
					platformOnline += p.getRefundAmount();
//				if(orgType == OrganizationTypeEnum.CHANNEL.getCode())//渠道站点
//					if(isHospitalUsePlatConfig(hospitalId,PayConstants.PayMethod.Balance))
//						platformOnline += online;
//					else
//						channelOnline += online;
//				else//体检中心
//					if(isHospitalUsePlatConfig(hospitalId,PayConstants.PayMethod.Balance))
//						platformOnline += online;
//					else
//						hosOnline += online;
			}
			if(p.getTradeMethodType().intValue() == PayConstants.PayMethod.Card)
				if(!isPlatCompany)
					card += p.getRefundAmount();
				else
					channelCard += p.getRefundAmount();
			if(p.getTradeMethodType().intValue() == PayConstants.PayMethod.ParentCard)
				if(!isPlatCompany)
					pcard += p.getRefundAmount();
				else
					channelCompanyPcard += p.getRefundAmount();
			if(p.getTradeMethodType().intValue() == PayConstants.PayMethod.OfflinePay)
					offline += p.getRefundAmount();
			if(p.getTradeMethodType().intValue() == PayConstants.PayMethod.OfflinePay)
				offline += p.getRefundAmount();
			if(p.getTradeMethodType().intValue() == PayConstants.PayMethod.Coupon)
				if(getPaymentConfigHospitalId(PayConstants.PayMethod.Coupon,trade_method_config_id) == hospitalId)
					hosCoupon += p.getRefundAmount();
				else if(getPaymentConfigHospitalId(PayConstants.PayMethod.Coupon,trade_method_config_id) == -1)
					platformCoupon += p.getRefundAmount();
				else if(isPlatCompany && getPaymentConfigHospitalId(PayConstants.PayMethod.Coupon,trade_method_config_id) == fromSite && orgType == 2)
					channelCoupon += p.getRefundAmount();
		}
		//平台退款
		platform = platformCoupon + channelCoupon + channelCompanyPcard + platformOnline + channelOnline + channelCard;
		long hospitalAmount = pcard + card + offline + hosCoupon + hosOnline;
		online = hosOnline + channelOnline + platformOnline;
		amount.setCardRefundAmount(card);//医院体检卡退款
		amount.setPcardRefundAmount(pcard);//医院单位母卡
		amount.setChannelCompanyRefundAmount(channelCompanyPcard);//渠道单位母卡
		amount.setChannelCardRefundAmount(channelCard);//渠道单位母卡
		amount.setOfflineRefundAmount(offline);//线下退款
		amount.setOnlineRefundAmount(online);//线上退款
		amount.setPlatformRefundAmount(platform);//平台退款
		amount.setHospitalCouponRefundAmount(hosCoupon);//医院优惠券
		amount.setPlatformCouponRefundAmount(platformCoupon);//平台优惠券
		amount.setChannelCouponRefundAmount(channelCoupon);//渠道优惠券
		amount.setHospitalOnlineRefundAmount(hosOnline);//医院线上
		amount.setPlatformOnlineRefundAmount(platformOnline);//平台线上
		amount.setChannelOnlineRefundAmount(channelOnline);//渠道线上
		amount.setTotalSuccessRefundAmount(platform+ hospitalAmount);
		return amount;
	}

	/**
	 * 根据订单号,获取所有的支付方式汇总
	 * key,pcard,online,platform,card,offline
	 * @param ref_order_Num
	 * @return
	 * @throws SqlException 
	 */
	public static PayAmount getPayAmountByOrderNum(String ref_order_Num,int ref_order_type) throws SqlException{
		PayAmount amount = new PayAmount();
		long pcard = 0;//普通单位母卡支付
		long online = 0;//线上支付
		long platform = 0;//平台支付
		long card = 0;//卡支付
		long offline = 0;//线下支付
		long hosCoupon = 0;//医院优惠券支付
		long platformCoupon = 0;//平台优惠券支付
		long channelCoupon = 0;//渠道优惠券支付
		long hosOnline = 0;//医院线上
		long platformOnline = 0;//平台线上
		long channelOnline = 0;//渠道线上
		long channelCompanyPcard = 0;//渠道母卡
		long channelCard = 0;//渠道体检卡
		boolean isPlatCompany = false;
		int hospitalId = -1;
		int fromSite = -1;
		int orgType = OrganizationTypeEnum.HOSPITAL.getCode();
		//判断是否是平台单位/普通单位
		//获取是否使用平台支付配置
		if(ref_order_type == PayConstants.OrderType.MytijianOrder) {
			Order order = OrderChecker.getOrderInfo(ref_order_Num);
			hospitalId = order.getHospital().getId();
			fromSite = order.getFromSite();
			orgType = order.getFromSiteOrgType();
			HospitalExamCompanySnapshot hc = order.getHospitalCompany();
			if(hc.getPlatformCompanyId()!=null){
				int platcompanyId = hc.getPlatformCompanyId();
				if(platcompanyId != 1 && platcompanyId != 2 )
					isPlatCompany = true;
			}
		}else if (ref_order_type == PayConstants.OrderType.PaymentOrder){
			PaymentOrder order1 = OrderChecker.getPaymentOrderInfo(ref_order_Num);
			hospitalId = order1.getHospitalId();
			fromSite = order1.getHospitalId();
		}

		List<TradePayRecord> pays = getTradePayRecordByOrderNum(ref_order_Num,null,ref_order_type);
		for(TradePayRecord p : pays){
			int trade_method_config_id = p.getTradeMethodConfigId();
			//忽略未成功支付的记录
			if(p.getPayStatus().intValue() != PayConstants.TradeStatus.Successful)
				continue;
			if(p.getTradeMethodType().intValue() == PayConstants.PayMethod.Alipay){
					online += p.getPayAmount();
				if(orgType == OrganizationTypeEnum.CHANNEL.getCode())//渠道站点
					if(isUsePlatPaymentConfig(fromSite,PayConstants.PayMethod.Alipay,trade_method_config_id))
						platformOnline += p.getPayAmount();
					else
						channelOnline += p.getPayAmount();
				else//体检中心
					if(isUsePlatPaymentConfig(fromSite,PayConstants.PayMethod.Alipay,trade_method_config_id))
						platformOnline += p.getPayAmount();
					else
						hosOnline += p.getPayAmount();
			}
			if(p.getTradeMethodType().intValue() == PayConstants.PayMethod.AlipayScan){
					online += p.getPayAmount();
				if(orgType == OrganizationTypeEnum.CHANNEL.getCode())//渠道站点
					if(isUsePlatPaymentConfig(fromSite,PayConstants.PayMethod.AlipayScan,trade_method_config_id))
						platformOnline += p.getPayAmount();
					else
						channelOnline += p.getPayAmount();
				else//体检中心
					if(isUsePlatPaymentConfig(fromSite,PayConstants.PayMethod.AlipayScan,trade_method_config_id))
						platformOnline += p.getPayAmount();
					else
						hosOnline += p.getPayAmount();
			}
			if(p.getTradeMethodType().intValue() == PayConstants.PayMethod.Wxpay){
					online += p.getPayAmount();
				if(orgType == OrganizationTypeEnum.CHANNEL.getCode())//渠道站点
					if(isUsePlatPaymentConfig(fromSite,PayConstants.PayMethod.Wxpay,trade_method_config_id))
						platformOnline += p.getPayAmount();
					else
						channelOnline += p.getPayAmount();
				else//体检中心
					if(isUsePlatPaymentConfig(fromSite,PayConstants.PayMethod.Wxpay,trade_method_config_id))
						platformOnline += p.getPayAmount();
					else
						hosOnline += p.getPayAmount();
			}
			if(p.getTradeMethodType().intValue() == PayConstants.PayMethod.WxpayScan){
					online += p.getPayAmount();
				if(orgType == OrganizationTypeEnum.CHANNEL.getCode())//渠道站点
					if(isUsePlatPaymentConfig(fromSite,PayConstants.PayMethod.WxpayScan,trade_method_config_id))
						platformOnline += p.getPayAmount();
					else
						channelOnline += p.getPayAmount();
				else//体检中心
					if(isUsePlatPaymentConfig(fromSite,PayConstants.PayMethod.WxpayScan,trade_method_config_id))
						platformOnline += p.getPayAmount();
					else
						hosOnline += p.getPayAmount();
			}
			if(p.getTradeMethodType().intValue() == PayConstants.PayMethod.OnlinePay){
					online += p.getPayAmount();
				if(orgType == OrganizationTypeEnum.CHANNEL.getCode())//渠道站点
					if(isUsePlatPaymentConfig(fromSite,PayConstants.PayMethod.OnlinePay,trade_method_config_id))
						platformOnline += p.getPayAmount();
					else
						channelOnline += p.getPayAmount();
				else//体检中心
					if(isUsePlatPaymentConfig(fromSite,PayConstants.PayMethod.OnlinePay,trade_method_config_id))
						platformOnline += p.getPayAmount();
					else
						hosOnline += p.getPayAmount();
			}
			if(p.getTradeMethodType().intValue() == PayConstants.PayMethod.Balance){ //余额支付全部挂平台线上
					online += p.getPayAmount();
					platformOnline +=p.getPayAmount();
//				if(orgType == OrganizationTypeEnum.CHANNEL.getCode())//渠道站点
//					if(isHospitalUsePlatConfig(hospitalId,PayConstants.PayMethod.Balance))
//						platformOnline += online;
//					else
//						channelOnline += online;
//				else//体检中心
//					if(isHospitalUsePlatConfig(hospitalId,PayConstants.PayMethod.Balance))
//						platformOnline += online;
//					else
//						hosOnline += online;
			}
			if(p.getTradeMethodType().intValue() == PayConstants.PayMethod.Card)
				if(!isPlatCompany)
					card += p.getPayAmount();
				else
					channelCard += p.getPayAmount();
			if(p.getTradeMethodType().intValue() == PayConstants.PayMethod.ParentCard)
				if(!isPlatCompany)
					pcard += p.getPayAmount();
				else
					channelCompanyPcard += p.getPayAmount();
			if(p.getTradeMethodType().intValue() == PayConstants.PayMethod.OfflinePay)
					offline += p.getPayAmount();
			if(p.getTradeMethodType().intValue() == PayConstants.PayMethod.Coupon)
				if(getPaymentConfigHospitalId(PayConstants.PayMethod.Coupon,trade_method_config_id) == hospitalId)
						hosCoupon += p.getPayAmount();
			    else if(getPaymentConfigHospitalId(PayConstants.PayMethod.Coupon,trade_method_config_id) == -1)
					    platformCoupon += p.getPayAmount();
			    else if(isPlatCompany && getPaymentConfigHospitalId(PayConstants.PayMethod.Coupon,trade_method_config_id) == fromSite && orgType == 2)
					    channelCoupon += p.getPayAmount();
		}

		//平台支付
		platform = platformCoupon + channelCoupon + channelCompanyPcard + platformOnline + channelOnline + channelCard;
		long hospitalAmount = pcard + card + offline + hosCoupon + hosOnline;
		amount.setCardPayAmount(card);//体检卡
		amount.setPcardPayAmount(pcard);//医院单位母卡
		amount.setChannelCompanyPayAmount(channelCompanyPcard);//渠道单位母卡
		amount.setChannelCardPayAmount(channelCard);//渠道体检卡
		amount.setOfflinePayAmount(offline);//线下支付
		amount.setOnlinePayAmount(platformOnline+hosOnline+channelOnline);//线上支付
		amount.setPlatformPayAmount(platform);//平台支付
		amount.setHospitalCouponAmount(hosCoupon);//医院优惠券
		amount.setPlatformCouponAmount(platformCoupon);//平台优惠券
		amount.setChannelCouponAmount(channelCoupon);//渠道优惠券
		amount.setHospitalOnlinePayAmount(hosOnline);//医院线上
		amount.setPlatformOnlinePayAmount(platformOnline);//平台线上
		amount.setChannelOnlinePayAmount(channelOnline);//渠道线上
		amount.setTotalCouponPayAmount(channelCoupon+platformCoupon+hosCoupon);//总共优惠金额
		amount.setTotalSuccPayAmount(platform+hospitalAmount);//医院支付+平台支付
		return amount;
	}

	
	/**
	 * 返回tb_trade_pay_record数据,1笔交易可对应多条记录
	 * 
	 * @param ref_order_num
	 * @param trade_order_num
	 * @param  ref_order_type
	 * @return
	 * @throws SqlException
	 */
	public static  List<TradePayRecord> getTradePayRecordByOrderNum(String ref_order_num, String trade_order_num,int ref_order_type)
			throws SqlException {
		List<TradePayRecord> list = new ArrayList<TradePayRecord>();
		String sql = "select * from tb_trade_pay_record where ref_order_num = '" + ref_order_num+"' ";
		if(trade_order_num != null)
			sql += " and trade_order_num = '" + trade_order_num + "'";
		if(ref_order_type!=-1)
			sql += " and ref_order_type = "+ref_order_type;
		sql += " order by trade_method_type desc";
		log.debug("sql..sql...." + sql);
		List<Map<String, Object>> dblist = DBMapper.query(sql);
		for (Map<String, Object> map : dblist) {
			TradePayRecord t = new TradePayRecord();
			t.setId(Integer.parseInt(map.get("id").toString()));
			t.setSn(map.get("sn").toString());
			t.setTradeOrderNum(map.get("trade_order_num").toString());
			t.setRefOrderNum(ref_order_num);
			if (map.get("ref_order_num_version") != null)
				t.setRefOrderNumVersion(map.get("ref_order_num_version").toString());
			t.setRefOrderType(Integer.parseInt(map.get("ref_order_type").toString()));
			t.setTradeMethodConfigId(Integer.parseInt(map.get("trade_method_config_id").toString()));
			t.setTradeMethodType(Integer.parseInt(map.get("trade_method_type").toString()));
			t.setPayStatus(Integer.parseInt(map.get("pay_status").toString()));
			t.setPayAmount(Long.parseLong(map.get("pay_amount").toString()));
			if(map.get("pay_trade_account_id")!=null)
				t.setPayTradeAccountId(Integer.parseInt(map.get("pay_trade_account_id").toString()));
			if(map.get("pay_trade_subaccount_id")!=null)
				t.setPayTradeSubaccountId(Integer.parseInt(map.get("pay_trade_subaccount_id").toString()));
			if(map.get("pay_trade_subaccount_type")!=null)
				t.setPayTradeSubaccountType(Integer.parseInt(map.get("pay_trade_subaccount_type").toString()));
			if(map.get("pay_trade_account_snap")!=null)
				t.setPayTradeAccountSnap(map.get("pay_trade_account_snap").toString());
			t.setReceiveTradeAccountId(Integer.parseInt(map.get("receive_trade_account_id").toString()));
			t.setReceiveTradeSubaccountId(Integer.parseInt(map.get("receive_trade_subaccount_id").toString()));
			t.setReceiveTradeSubaccountType(Integer.parseInt(map.get("receive_trade_subaccount_type").toString()));
			t.setReceiveTradeAccountSnap(map.get("receive_trade_account_snap").toString());
			if (map.get("out_oder_id") != null)
				t.setOutOrderId(map.get("out_order_id").toString());
			list.add(t);
		}
		return list;
	}

	/**
	 * 返回tb_trade_refund_record数据,1笔交易可对应多条记录
	 *
	 *
	 * @param ref_order_num
	 * @param trade_order_num
	 * @return
	 * @throws SqlException
	 */
	public static  List<TradeRefundRecord> getTradeRefundRecordByOrderNum(String ref_order_num ,String trade_order_num) throws SqlException {
		return getTradeRefundRecordByOrderNum(ref_order_num,trade_order_num,1);
	}
	
	/**
	 * 返回tb_trade_refund_record数据,1笔交易可对应多条记录
	 *
	 * 体检订单/付款订单
	 * @param ref_order_num
	 * @param trade_order_num
	 * @param ref_order_type
	 * @return
	 * @throws SqlException
	 */
	public static  List<TradeRefundRecord> getTradeRefundRecordByOrderNum(String ref_order_num ,String trade_order_num ,int ref_order_type)
			throws SqlException {
		List<TradeRefundRecord> list = new ArrayList<TradeRefundRecord>();
		String sql = "select * from tb_trade_refund_record where ref_order_num = '" + ref_order_num+"'";
			if(trade_order_num != null)
				sql += " and trade_order_num = '" + trade_order_num + "'";
			if(ref_order_type != -1)
			sql += " and ref_order_type = '" + ref_order_type + "'";
			sql += " order by id ";
		log.debug("sql..sql...." + sql);
		List<Map<String, Object>> dblist = DBMapper.query(sql);
		for (Map<String, Object> map : dblist) {
			TradeRefundRecord t = new TradeRefundRecord();
			t.setId(Integer.parseInt(map.get("id").toString()));
			t.setSn(map.get("sn").toString());
			t.setTradeOrderNum(map.get("trade_order_num").toString());
			t.setRefOrderNum(ref_order_num);
			if (map.get("ref_order_num_version") != null)
				t.setRefOrderNumVersion(map.get("ref_order_num_version").toString());
			t.setRefOrderType(Integer.parseInt(map.get("ref_order_type").toString()));
			t.setTradeMethodConfigId(Integer.parseInt(map.get("trade_method_config_id").toString()));
			t.setTradeMethodType(Integer.parseInt(map.get("trade_method_type").toString()));
			t.setRefundStatus(Integer.parseInt(map.get("refund_status").toString()));
			t.setRefundAmount(Long.parseLong(map.get("refund_amount").toString()));
			if(map.get("receive_trade_account_id")!=null)
				t.setReceiveTradeAccountId(Integer.parseInt(map.get("receive_trade_account_id").toString()));
			if(map.get("receive_trade_sub_account_id")!=null)
				t.setReceiveTradeSubAccountId(Integer.parseInt(map.get("receive_trade_sub_account_id").toString()));
			if(map.get("receive_trade_sub_account_type")!=null)
				t.setReceiveTradeSubAccountType((Integer.parseInt(map.get("receive_trade_sub_account_type").toString())));
			if(map.get("receive_trade_account_snap")!=null)
				t.setReceiveTradeAccountSnap((map.get("receive_trade_account_snap").toString()));
			t.setRefundTradeAccountId(Integer.parseInt(map.get("refund_trade_account_id").toString()));
			t.setRefundTradeSubAccountId(Integer.parseInt(map.get("refund_trade_sub_account_id").toString()));
			t.setRefundTradeSubAccountType(Integer.parseInt(map.get("refund_trade_sub_account_type").toString()));
			t.setRefundTradeAccountSnap((map.get("refund_trade_account_snap").toString()));
			if (map.get("out_oder_id") != null)
				t.setOutOrderId(map.get("out_order_id").toString());
			list.add(t);
		}
		return list;
	}
	

	/**
	 * 查看账户明细
	 * 
	 * @param trade_order_num
	 * @param flag
	 * @return
	 * @throws SqlException
	 */
	public  static List<TradeAccountDetail> getTradeAccountDetail(String trade_order_num, int flag) throws SqlException {
		List<TradeAccountDetail> list = new ArrayList<TradeAccountDetail>();
		String sql = "select * from tb_trade_account_detail where  trade_order_num = '" + trade_order_num
				+ "' and flag = ? order by sn,trade_sub_account_type";
		List<Map<String, Object>> dblist = DBMapper.query(sql, flag);
		for (Map<String, Object> map : dblist) {
			TradeAccountDetail t = new TradeAccountDetail();
			t.setSn(map.get("sn").toString());
			t.setTradeAccountId(Integer.parseInt(map.get("trade_account_id").toString()));
			t.setTradeSubAccountId(Integer.parseInt(map.get("trade_sub_account_id").toString()));
			t.setTradeSubAccountType(Integer.parseInt(map.get("trade_sub_account_type").toString()));
			t.setTradeOrderNum(map.get("trade_order_num").toString());
			t.setBizType(Integer.parseInt(map.get("biz_type").toString()));
			t.setRefBizSn(map.get("ref_biz_sn").toString());
			t.setChangeAmount(Long.parseLong(map.get("change_amount").toString()));
			t.setPreAmount(Long.parseLong(map.get("pre_amount").toString()));
			t.setAftAmount(Long.parseLong(map.get("aft_amount").toString()));
			t.setFlag(Integer.parseInt(map.get("flag").toString()));
			list.add(t);
		}
		return list;
	}

	/**
	 * 返回tb_trade_pay_record数据,1笔交易可对应多条记录
	 * 
	 * @param ref_order_num
	 * @param ref_order_num_version
	 * @return
	 * @throws SqlException
	 */
	public static List<TradeAccountDetail> getTradeAccountDetail(String trade_order_num) throws SqlException {
		List<TradeAccountDetail> list = new ArrayList<TradeAccountDetail>();
		list.addAll(getTradeAccountDetail(trade_order_num, 0));
		list.addAll(getTradeAccountDetail(trade_order_num, 1));
		return list;
	}

	/**
	 * 获取支付方式正确的payment_method_id
	 * 
	 * @param b
	 * @return
	 * @throws Exception
	 */
	public static int getSuitablePayMethodId(int hospitalId, int b) throws Exception {
		int type = 0;
		if (b == PayConstants.PayMethodBit.BalanceBit)
			type = 2;
		if (b == PayConstants.PayMethodBit.CardBit)
			type = 1;
		if (b == PayConstants.PayMethodBit.AlipayBit)
			type = 3;
		if (b == PayConstants.PayMethodBit.WxpayBit)
			type = 4;
		if (b == PayConstants.PayMethodBit.AlipayScanBit)
			type = 5;
		if (b == PayConstants.PayMethodBit.WxpayScanBit)
			type = 6;
		if (b == PayConstants.PayMethodBit.OfflinePayBit)
			type = 7;
		if (b == PayConstants.PayMethodBit.OnlinePayBit)
			type = 8;
		if (b == PayConstants.PayMethodBit.ParentCardBit)
			type = 9;
		if (b == PayMethodBit.CouponBit)
			type = 10;
		if (b == PayMethodBit.WxAppBit)
			type = 13;
		String sql = "select * from tb_payment_config where hospital_id = ? and type = " + type;
		List<Map<String, Object>> list = DBMapper.query(sql, hospitalId);
		if (list.size() == 0) {
			sql = "select * from tb_payment_config where hospital_id = -1 and type = " + type; // 取所有医院默认配置
			List<Map<String, Object>> dblist = DBMapper.query(sql);
			if (dblist.size() == 0) {
				throw new Exception("缺少配置...类型为" + type);
			}
			Assert.assertEquals(dblist.size(), 1);
			return Integer.parseInt(dblist.get(0).get("id").toString());
		} else {// 取医院的配置项
			return Integer.parseInt(list.get(0).get("id").toString());
		}
	}

	/**
	 * 获取收款方总账户id
	 * 
	 * @param b
	 * @return
	 * @throws Exception
	 */
	public static int getSuitableReceiveMethodId(int hospitalId, int b) throws Exception {
		int type = 0;
		if (b == PayConstants.PayMethodBit.BalanceBit)
			type = 2;
		if (b == PayConstants.PayMethodBit.CardBit)
			type = 1;
		if (b == PayConstants.PayMethodBit.AlipayBit)
			type = 3;
		if (b == PayConstants.PayMethodBit.WxpayBit)
			type = 4;
		if (b == PayConstants.PayMethodBit.AlipayScanBit)
			type = 5;
		if (b == PayConstants.PayMethodBit.WxpayScanBit)
			type = 6;
		if (b == PayConstants.PayMethodBit.OfflinePayBit)
			type = 7;
		if (b == PayConstants.PayMethodBit.OnlinePayBit)
			type = 8;
		if (b == PayConstants.PayMethodBit.ParentCardBit)
			type = 9;
		String sql = "select * from tb_payment_config where hospital_id = ? and type = " + type;
		List<Map<String, Object>> list = DBMapper.query(sql, hospitalId);
		if (list.size() == 0) {
			sql = "select * from tb_payment_config where hospital_id = -1 and type = " + type; // 取所有医院默认配置
			List<Map<String, Object>> dblist = DBMapper.query(sql);
			if (dblist.size() == 0) {
				throw new Exception("缺少配置...类型为" + type);
			}
			Assert.assertEquals(dblist.size(), 1);
			return Integer.parseInt(dblist.get(0).get("trade_account_id").toString());
		} else {// 取医院的配置项
			return Integer.parseInt(list.get(0).get("trade_account_id").toString());
		}
	}

	/**
	 * 根据ref_id查询主账户ID
	 *
	 * @param ref_id
	 * @param  account_type
	 * @return
	 * @throws SqlException
	 */
	public static int getTradeAccountIdByRefIdAndType(int ref_id,int account_type) throws SqlException {
		String sql = "select * from tb_trade_account where ref_id = ?  and account_type = ?";
		List<Map<String, Object>> list = DBMapper.query(sql, ref_id,account_type);
		if(list != null && list.size()>0){
			Map<String, Object> map = list.get(0);
			return Integer.parseInt(map.get("id").toString());
		}
		return 0;
	}


	/**
	 * 根据用户ID查询主账户ID
	 *
	 * @param account_id
	 * @return
	 * @throws SqlException
	 */
	public static int getTradeAccountIdByAccountId(int account_id) throws SqlException {
		String sql = "select * from tb_trade_account where ref_id = ?  and account_type = 1";
		List<Map<String, Object>> list = DBMapper.query(sql, account_id);
		if(list != null && list.size()>0){
			Map<String, Object> map = list.get(0);
			return Integer.parseInt(map.get("id").toString());
		}
			return 0;
	}


	/**
	 * 获取总账户卡账户
	 * 
	 * @param trade_account_id
	 * @return
	 * @throws SqlException
	 */
	public static List<Integer> getSubCardIdList(int trade_account_id) throws SqlException {
		List<Integer> intList = new ArrayList<Integer>();
		String sql = "select * from tb_card where trade_account_id = ?";
		List<Map<String, Object>> list = DBMapper.query(sql, trade_account_id);
		for (Map<String, Object> map : list) {
			intList.add(Integer.parseInt(map.get("id").toString()));
		}
		return intList;
	}


	/**
	 * 母卡
	 * 
	 * @param trade_account_id
	 * @return
	 * @throws SqlException
	 */
	public static int getSubParentCardId(int trade_account_id) throws SqlException {
		String sql = "select * from tb_card where trade_account_id = ?";
		List<Map<String, Object>> list = DBMapper.query(sql, trade_account_id);
		if(list != null && list.size()>0){
			Map<String, Object> map = list.get(0);
			return Integer.parseInt(map.get("id").toString());

		}
		return 0;
	}

	/**
	 * 获取总账户余额账户
	 * 
	 * @param trade_account_id
	 * @return
	 * @throws SqlException
	 */
	public static int getSubAccounttingId(int trade_account_id) throws SqlException {
		String sql = "select * from tb_accounting where  trade_account_id = ?";
		List<Map<String, Object>> list = DBMapper.query(sql, trade_account_id);
		if(list != null && list.size()>0){
			Map<String, Object> map = list.get(0);
			return Integer.parseInt(map.get("id").toString());
		}
			return  0;
	}

	public static Accounting getSubAccountting(int trade_account_id) throws SqlException {
		Accounting accounting = new Accounting();
		String sql = "select * from tb_accounting where  trade_account_id = ?";
		List<Map<String, Object>> list = DBMapper.query(sql, trade_account_id);
		if(list != null && list.size()>0){
			Map<String, Object> map = list.get(0);
			if (map.get("account_id") != null)
				accounting.setAccountId(Integer.parseInt(map.get("account_id").toString()));
			accounting.setBalance(Integer.parseInt(map.get("balance").toString()));
			accounting.setId(Integer.parseInt(map.get("id").toString()));
		}
		return accounting;
	}

	/***线下子账户废除****/
	/**
	 * 获取总账户线下付款子账户
	 *
	 * @param trade_account_id
	 * @return
	 * @throws SqlException
	 */
//	public static int getSubLocalId(int trade_account_id) throws SqlException {
//		String sql = "select * from tb_trade_local_pay_account where  trade_account_id = ?";
//		List<Map<String, Object>> list = DBMapper.query(sql, trade_account_id);
//		if(list != null && list.size()>0){
//			Map<String, Object> map = list.get(0);
//			return Integer.parseInt(map.get("id").toString());
//		}
//		return 0;
//
//	}
//
	/***三方子账户废除****/
	/**
	 * 获取总账户第三方子账户
	 *
	 * @param trade_account_id
	 * @return
	 * @throws SqlException
	 */
//	public static int getSubThirdId(int trade_account_id) throws SqlException {
//		String sql = "select * from tb_trade_third_party_account where  trade_account_id = ?";
//		List<Map<String, Object>> list = DBMapper.query(sql, trade_account_id);
//		if(list != null && list.size()>0){
//			Map<String, Object> map = list.get(0);
//			return Integer.parseInt(map.get("id").toString());
//		}
//		return 0;
//	}

	/***三方子账户废除****/
	/**
	 *
	 * 根据sql查询付款订单第三方子账号
	 * @param sql
	 * @return
	 * @throws SqlException
	 */
//	public static TradeThreeAccounts getThreeAccounts(String sql) throws SqlException {
//		TradeThreeAccounts tradeThreeAccounts = new TradeThreeAccounts();
//		List<Map<String, Object>> list = DBMapper.query(sql);
//		if(list != null && list.size()>0){
//			Map<String, Object> map = list.get(0);
//			tradeThreeAccounts.setId(Integer.parseInt(map.get("id").toString()));
//			tradeThreeAccounts.setType(Integer.parseInt(map.get("type").toString()));
//			tradeThreeAccounts.setThreeAccountId(map.get("three_account_id").toString());
//			tradeThreeAccounts.setBalance(Long.parseLong(map.get("balance").toString()));
//			tradeThreeAccounts.setTradeAccountId(Integer.parseInt(map.get("trade_account_id").toString()));
//			tradeThreeAccounts.setIsDeleted(Integer.parseInt(map.get("is_deleted").toString()));
//		}
//		return tradeThreeAccounts;
//	}


	/**
	 *
	 * 根据交易总账户查询客户经理授信账户
	 * @param trade_account_id
	 * @return
	 * @throws SqlException
	 */
	public static TradeCreditAccount getTradeCreditAccount(int trade_account_id) throws SqlException {
		TradeCreditAccount tradeCreditAccount = new TradeCreditAccount();
		String sql = "select * from tb_trade_credit_account where trade_account_id ="+trade_account_id;
		List<Map<String, Object>> list = DBMapper.query(sql);
		if(list != null && list.size()>0){
			Map<String, Object> map = list.get(0);
			tradeCreditAccount.setId(Integer.parseInt(map.get("id").toString()));
			tradeCreditAccount.setTradeAccountId(Integer.parseInt(map.get("trade_account_id").toString()));
			tradeCreditAccount.setBalance(Integer.parseInt(map.get("balance").toString()));
			tradeCreditAccount.setCreditLimit(Integer.parseInt(map.get("credit_limit").toString()));
			tradeCreditAccount.setFreezeCreditLimit(Integer.parseInt(map.get("freeze_credit_limit").toString()));
			tradeCreditAccount.setVersion(Integer.parseInt(map.get("version").toString()));
			tradeCreditAccount.setIsDeleted(Integer.parseInt(map.get("is_deleted").toString()));
		}
		return tradeCreditAccount;
	}

	/**
	 *
	 * 根据交易总账户查询机构的优惠券账户
	 * @param trade_account_id
	 * @return
	 * @throws SqlException
	 */
	public static TradeCouponAccount getTradeCouponAccount(int trade_account_id) throws SqlException {
		TradeCouponAccount tradeCouponAccount = new TradeCouponAccount();
		String sql = "select * from tb_trade_coupon_account where trade_account_id ="+trade_account_id;
		List<Map<String, Object>> list = DBMapper.query(sql);
		if(list != null && list.size()>0){
			Map<String, Object> map = list.get(0);
			tradeCouponAccount.setId(Integer.parseInt(map.get("id").toString()));
			tradeCouponAccount.setTradeAccountId(Integer.parseInt(map.get("trade_account_id").toString()));
			tradeCouponAccount.setBalance(Integer.parseInt(map.get("balance").toString()));
			tradeCouponAccount.setVersion(Integer.parseInt(map.get("version").toString()));
			tradeCouponAccount.setIsDeleted(Integer.parseInt(map.get("is_deleted").toString()));
		}
		return tradeCouponAccount;
	}

	/**
	 * 根据订单号找到体检人的交易总账户
	 * 
	 * @param orderId
	 * @return
	 * @throws SqlException
	 */
	public static int getTradeAccountByOrderId(int orderId) throws SqlException {
		//step1 get AccountId
		String accountSql = "select source,account_id, operator_id from tb_order where id = "+orderId;
		List<Map<String, Object>> list = DBMapper.query(accountSql);
		Map<String, Object> map = list.get(0);
		int source = Integer.parseInt(map.get("source").toString());
		String sql = null;
		if(source == 2 || source == 5){//C端，则直接取operation_id
			sql = "select a.id from tb_trade_account a ,tb_order o  where o.operator_id = a.ref_id and a.account_type = 1 and o.id = ? ";
		}else//CRM端|MTJOB,取account_id
			 sql = "select a.id from tb_trade_account a ,tb_order o  where o.account_id = a.ref_id and a.account_type = 1 and o.id = ? ";
		list = DBMapper.query(sql, orderId);
		map = list.get(0);
		return Integer.parseInt(map.get("id").toString());
	}


	/**
	 * 获取余额账号对象
	 * 
	 * @param accountId
	 * @return
	 * @throws SqlException
	 */
	public static Accounting getAccouting(int accountId) throws SqlException {
		Accounting accounting = new Accounting();
		String sql = "select * from tb_accounting where  account_id = ?";
		List<Map<String, Object>> list = DBMapper.query(sql, accountId);
		if(list != null && list.size()>0){
			Map<String, Object> map = list.get(0);
			accounting.setAccountId(Integer.parseInt(map.get("account_id").toString()));
			accounting.setBalance(Integer.parseInt(map.get("balance").toString()));
			accounting.setId(Integer.parseInt(map.get("id").toString()));
		}
		return accounting;
	}



	/**
	 * tb_paylog
	 * 
	 * @return
	 */
	public static List<PayLog> getPaylog(Integer orderId, Integer tradeType) {
		String sql = "SELECT * FROM tb_paylog WHERE trade_type = ? and order_id = ? ORDER BY id desc LIMIT 1";
		PayLog paylog = new PayLog();
		List<PayLog> payLogList = new ArrayList<PayLog>();
		List<Map<String, Object>> list = null;
		try {
			list = DBMapper.query(sql, tradeType, orderId);
			if (list != null && list.size() > 0) {
				for (Map<String, Object> m : list) {
					paylog.setId((Integer) m.get("id"));
					;
					paylog.setAmount(Long.valueOf(m.get("amount").toString()));
					paylog.setChannel((Integer) m.get("channel"));
					paylog.setOperater((Integer) m.get("operater"));
					paylog.setOperaterType((Integer) m.get("operater_type"));
					paylog.setOrderId((Integer) m.get("order_id"));
					paylog.setStatus((Integer) m.get("status"));
					paylog.setSurplus(Long.valueOf(m.get("surplus").toString()));
					paylog.setTradeBody((Integer) m.get("trade_body"));
					paylog.setTradeBodyType((Integer) m.get("trade_body_type"));
					paylog.setTradeIndex((Integer) m.get("trade_index"));
					paylog.setTradeType((Integer) m.get("trade_type"));
					if (m.get("credentials") != null)
						paylog.setCredentials(m.get("credentials").toString());
					if (m.get("credentialsType") != null)
						paylog.setCredentialsType(m.get("credentials_type").toString());
					if (m.get("expenseAccount") != null)
						paylog.setExpenseAccount(m.get("expense_account").toString());
					if (m.get("tradeBatchNo") != null)
						paylog.setTradeBatchNo(m.get("trade_batch_no").toString());
					if (m.get("orderNum") != null)
						paylog.setOrderNum(m.get("order_num").toString());
					if (m.get("remark") != null)
						paylog.setRemark(m.get("remark").toString());

					payLogList.add(paylog);
				}
			}

		} catch (SqlException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		return payLogList;
	}

	
	/**
	 * 判断医院是否使用的平台配置
	 * true为使用的平台配置，false为使用的医院配置
	 * @param hospitalId
	 * @return
	 */
	public static boolean isHospitalUsePlatConfig(int hospitalId,int type){
		String sql = "select * from tb_payment_config where hospital_id = ? and type = "+type;
		try {
			List<Map<String,Object>> dblist = DBMapper.query(sql, hospitalId);
			if(dblist != null && dblist.size() > 0){
				return false;
			}
		} catch (SqlException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		return true;
	}

	/**
	 * 判断医院是否使用的平台配置
	 * true为使用的医院配置
	 * @param hospitalId
	 * @return
	 */
	public static boolean isUsePlatPaymentConfig(int hospitalId,int type,int configId){
		String sql = "select * from tb_payment_config where hospital_id = ? and type = "+type +" and id = "+configId;
		try {
			List<Map<String,Object>> dblist = DBMapper.query(sql, hospitalId);
			if(dblist != null && dblist.size() > 0){
				return false;
			}
		} catch (SqlException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		return true;
	}



	/**
	 * 判断医院是否使用的自有配置
	 * true为使用的医院配置
	 * @param type
	 * @param configId
	 * @return
	 */
	public static int getPaymentConfigHospitalId(int type,int configId){
		String sql = "select * from tb_payment_config where  type = "+type +" and id = "+configId;
		log.info("sql..."+sql);
		try {
			List<Map<String,Object>> dblist = DBMapper.query(sql);
			if(dblist != null && dblist.size() > 0){
				return Integer.parseInt(dblist.get(0).get("hospital_id").toString());
			}
		} catch (SqlException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		return -1;
	}

	/**
	 * 对于付款订单支付情况
	 */
	public static void updatePaymentOrderPaySuccess(String orderNum) throws SqlException {
		//更新付款订单表
		DBMapper.update("update tb_payment_order set status = 2 where order_num = '"+orderNum+"'");
		List<TradeOrder> tradeOrderS = getTradeOrderByOrderNum(orderNum, TradeType.pay);
		TradeOrder tradeOrder = tradeOrderS.get(0);
		//更新交易记录表
		String tradeSql = "update tb_trade_order set succ_amount  = "+tradeOrder.getAmount()+" ,trade_status = "+PayConstants.TradeStatus.Successful+" where ref_order_num = '"+orderNum+"'";
		log.info(tradeSql);
		DBMapper.update(tradeSql);
		//更新交易支付表
//		TradeThreeAccounts threeAccounts = getThreeAccounts("select * from tb_trade_three_accounts order by id desc limit 1");
//		int pay_trade_account_id = getTradeAccountByOrderId(OrderChecker.getOrderInfo(orderNum).getId());
//		int pay_trade_subaccount_id = threeAccounts.getId();
//		String pay_trade_account_snap = JSON.toJSONString(threeAccounts);
		String recordSql = "update tb_trade_pay_record set pay_status = "+PayConstants.TradeStatus.Successful + " where ref_order_num = '"+orderNum+"'";
		log.info(recordSql);
		DBMapper.update(recordSql);


	}

	/**************************************优惠券相关***********************************************/
	/**
	 * 查询优惠券模板列表
	 * @param args
	 * @return
	 * @throws SqlException
	 * @throws ParseException
	 */
	public static List<CouponTemplate> getCouponTemplateList(String...args) throws SqlException, ParseException {
		List<CouponTemplate> records = new ArrayList<CouponTemplate>();
		String ps = changeManyStringToOneStr(args);
		String sql = "select * from tb_coupon_template where "+ps;
		List<Map<String,Object>> list = DBMapper.query(sql);
		if(list != null && list.size()>0){
			for(Map<String,Object> map:list){
				CouponTemplate coupon = new CouponTemplate();
				coupon.setId(Integer.parseInt(map.get("id").toString()));
				coupon.setBatchNum(map.get("batch_num").toString());
				coupon.setName(map.get("name").toString());
				coupon.setQuantity(Integer.parseInt(map.get("quantity").toString()));
				coupon.setPrice(Integer.parseInt(map.get("price").toString()));
				coupon.setStatus(Integer.parseInt(map.get("status").toString()));
				coupon.setReceiveLimitNumber(Integer.parseInt(map.get("receive_limit_num").toString()));
				coupon.setMinLimitPrice(Integer.parseInt(map.get("min_limit_price").toString()));
				coupon.setOperatorId(Integer.parseInt(map.get("operator_id").toString()));
				coupon.setOwnerId(Integer.parseInt(map.get("owner_id").toString()));
				coupon.setQuantity(Integer.parseInt(map.get("quantity").toString()));
				if(map.get("description")!=null)
					coupon.setDescription(map.get("description").toString());
				coupon.setRecovery(Integer.parseInt(map.get("recovery").toString())==0?false:true);
				coupon.setStartTime(simplehms.parse(map.get("start_time").toString()));
				coupon.setEndTime(simplehms.parse(map.get("end_time").toString()));
				coupon.setOwnerName(map.get("owner_name").toString());
				coupon.setSource(Integer.parseInt(map.get("source").toString()));
				coupon.setFromSiteOrgType(Integer.parseInt(map.get("from_site_org_type").toString()));
				coupon.setOrganizationId(Integer.parseInt(map.get("organization_id").toString()));
				coupon.setIsDeleted(Integer.parseInt(map.get("is_deleted").toString()));
				records.add(coupon);
			}
		}
		return records;
	}


	/**
	 * 根据SQL条件查询优惠券模板列表(tb_coupon_template)
	 * @param args
	 * @return
	 * @throws SqlException
	 * @throws ParseException
	 */
	public static List<CouponTemplate> getCouponTemplateList(String sql) throws SqlException, ParseException {
		List<CouponTemplate> records = new ArrayList<>();
		List<Map<String,Object>> list = DBMapper.query(sql);
		if(list != null && list.size()>0){
			for(Map<String,Object> map:list){
				CouponTemplate coupon = new CouponTemplate();
				coupon.setId(Integer.parseInt(map.get("id").toString()));
				coupon.setBatchNum(map.get("batch_num").toString());
				coupon.setName(map.get("name").toString());
				coupon.setQuantity(Integer.parseInt(map.get("quantity").toString()));
				coupon.setPrice(Integer.parseInt(map.get("price").toString()));
				coupon.setStatus(Integer.parseInt(map.get("status").toString()));
				coupon.setReceiveLimitNumber(Integer.parseInt(map.get("receive_limit_num").toString()));
				coupon.setMinLimitPrice(Integer.parseInt(map.get("min_limit_price").toString()));
				coupon.setOperatorId(Integer.parseInt(map.get("operator_id").toString()));
				coupon.setOwnerId(Integer.parseInt(map.get("owner_id").toString()));
				coupon.setQuantity(Integer.parseInt(map.get("quantity").toString()));
				if(map.get("description")!=null)
				coupon.setDescription(map.get("description").toString());
				coupon.setRecovery(Integer.parseInt(map.get("recovery").toString())==0?false:true);
				coupon.setStartTime(simplehms.parse(map.get("start_time").toString()));
				coupon.setEndTime(simplehms.parse(map.get("end_time").toString()));
				coupon.setOwnerName(map.get("owner_name").toString());
				coupon.setSource(Integer.parseInt(map.get("source").toString()));
				coupon.setFromSiteOrgType(Integer.parseInt(map.get("from_site_org_type").toString()));
				coupon.setOrganizationId(Integer.parseInt(map.get("organization_id").toString()));
				coupon.setIsDeleted(Integer.parseInt(map.get("is_deleted").toString()));
				if(map.get("received_num")!=null)
					coupon.setReceivedNumber(Integer.parseInt(map.get("received_num").toString()));
				if(map.get("used_num")!=null)
					coupon.setUsedNumber(Integer.parseInt(map.get("used_num").toString()));
				records.add(coupon);
			}
		}
		return records;
	}


	/**
	 * 根据SQL语句查询用户的优惠券列表
	 * @param args
	 * @return
	 * @throws SqlException
	 * @throws ParseException
	 */
	public static List<UserCouponReceive> getUserCouponList(String sql) throws SqlException, ParseException {
		log.info(sql);
		List<UserCouponReceive> couponReceives = new ArrayList<>();
		List<Map<String,Object>> list = DBMapper.query(sql);
		if(list != null && list.size()>0){
			for(Map<String,Object> map:list){
				UserCouponReceive receive = new UserCouponReceive();
				receive.setId(Integer.parseInt(map.get("id").toString()));
				receive.setTemplateBatchNum(map.get("template_batch_num").toString());
				receive.setOrganizationId(Integer.parseInt(map.get("organization_id").toString()));
				if(map.get("account_id")!=null)
					receive.setAccountId(Integer.parseInt(map.get("account_id").toString()));
				if(map.get("mobile")!=null)
					receive.setMobile(map.get("mobile").toString());
				if(map.get("account_name")!=null)
					receive.setAccountName(map.get("account_name").toString());
				if(map.get("idcard")!=null)
					receive.setIdCard(map.get("idcard").toString());
				receive.setStatus(Integer.parseInt(map.get("status").toString()));
				receive.setEndTime(simplehms.parse(map.get("end_time").toString()));
				if(map.get("used_date_time")!=null)
					receive.setUsedDateTime(simplehms.parse(map.get("used_date_time").toString()));
				receive.setIsDeleted(Integer.parseInt(map.get("is_deleted").toString()));
				receive.setGmtCreated(simplehms.parse(map.get("gmt_created").toString()));
				receive.setGmtModifyed(simplehms.parse(map.get("gmt_modified").toString()));
				CouponTemplate couponTemplate = getCouponTemplateList("batch_num","'"+map.get("template_batch_num").toString()+"'","is_deleted","0").get(0);
				receive.setCouponTemplate(couponTemplate);
				receive.setReceiveTime(simplehms.parse(map.get("gmt_created").toString()));//创建时间为领取时间
				if(map.get("used_date_time")!=null)
				receive.setUseTime(simplehms.parse(map.get("used_date_time").toString()));//使用时间
				couponReceives.add(receive);
			}
		}
		return couponReceives;
	}
	/**
	 * 根据条件查询用户的优惠券列表
	 * @param args
	 * @return
	 * @throws SqlException
	 * @throws ParseException
	 */
	public static List<UserCouponReceive> getUserCouponList(String ...args) throws SqlException, ParseException {
		List<UserCouponReceive> couponReceives = new ArrayList<>();
		String ps = changeManyStringToOneStr(args);
		String sql = "select * from tb_user_coupon_receive_record where "+ps;
		List<Map<String,Object>> list = DBMapper.query(sql);
		if(list != null && list.size()>0){
			for(Map<String,Object> map:list){
				UserCouponReceive receive = new UserCouponReceive();
				receive.setId(Integer.parseInt(map.get("id").toString()));
				receive.setTemplateBatchNum(map.get("template_batch_num").toString());
				receive.setOrganizationId(Integer.parseInt(map.get("organization_id").toString()));
				if(map.get("account_id")!=null)
					receive.setAccountId(Integer.parseInt(map.get("account_id").toString()));
				if(map.get("mobile")!=null)
					receive.setMobile(map.get("mobile").toString());
				if(map.get("account_name")!=null)
					receive.setAccountName(map.get("account_name").toString());
				if(map.get("idcard")!=null)
					receive.setIdCard(map.get("idcard").toString());
				receive.setStatus(Integer.parseInt(map.get("status").toString()));
				receive.setEndTime(simplehms.parse(map.get("end_time").toString()));
				if(map.get("used_date_time")!=null){
					receive.setUsedDateTime(simplehms.parse(map.get("used_date_time").toString()));
					receive.setUseTime(simplehms.parse(map.get("used_date_time").toString()));//使用时间
				}
				receive.setIsDeleted(Integer.parseInt(map.get("is_deleted").toString()));
				receive.setGmtCreated(simplehms.parse(map.get("gmt_created").toString()));
				receive.setGmtModifyed(simplehms.parse(map.get("gmt_modified").toString()));
				CouponTemplate couponTemplate = getCouponTemplateList("batch_num","'"+map.get("template_batch_num").toString()+"'","is_deleted","0").get(0);
				receive.setCouponTemplate(couponTemplate);
				receive.setReceiveTime(simplehms.parse(map.get("gmt_created").toString()));//创建时间为领取时间
				couponReceives.add(receive);
			}
		}
		return couponReceives;
	}



	/**
	 * 根据条件查询用户的优惠券统计表
	 * @param args
	 * @return
	 * @throws SqlException
	 * @throws ParseException
	 */
	public static List<UserReceiveStatistics> getUserReceiveStatistics(String ...args) throws SqlException, ParseException {
		List<UserReceiveStatistics> couponReceives = new ArrayList<UserReceiveStatistics>();
		String ps = changeManyStringToOneStr(args);
		String sql = "select * from tb_user_receive_statistics where "+ps;
		List<Map<String,Object>> list = DBMapper.query(sql);
		if(list != null && list.size()>0){
			for(Map<String,Object> map:list){
				UserReceiveStatistics receive = new UserReceiveStatistics();
				receive.setId(Integer.parseInt(map.get("id").toString()));
				receive.setTemplateBatchNum(map.get("template_batch_num").toString());
				receive.setOrganizationId(Integer.parseInt(map.get("organization_id").toString()));
				if(map.get("account_id")!=null)
					receive.setAccountId(Integer.parseInt(map.get("account_id").toString()));
				receive.setIsDeleted(Integer.parseInt(map.get("is_deleted").toString()));
				receive.setGmtCreated(simplehms.parse(map.get("gmt_created").toString()));
				receive.setGmtModified(simplehms.parse(map.get("gmt_modified").toString()));
				receive.setReceiveLimitNum(Integer.parseInt(map.get("receive_limit_num").toString()));
				receive.setReceiveNum(Integer.parseInt(map.get("received_num").toString()));
				receive.setUsedNum(Integer.parseInt(map.get("used_num").toString()));
				couponReceives.add(receive);
			}
		}
		return couponReceives;
	}


	/**
	 * 根据条件查询医院的的优惠券统计表
	 * @param args
	 * @return
	 * @throws SqlException
	 * @throws ParseException
	 */
	public static List<CouponTemplateStatistics> getCouponTemplateStatistics(String ...args) throws SqlException, ParseException {
		List<CouponTemplateStatistics> couponReceives = new ArrayList<CouponTemplateStatistics>();
		String ps = changeManyStringToOneStr(args);
		String sql = "select * from tb_coupon_template_statistics where "+ps;
		List<Map<String,Object>> list = DBMapper.query(sql);
		if(list != null && list.size()>0){
			for(Map<String,Object> map:list){
				CouponTemplateStatistics receive = new CouponTemplateStatistics();
				receive.setId(Integer.parseInt(map.get("id").toString()));
				receive.setTemplateBatchNum(map.get("template_batch_num").toString());
				receive.setOrganizationId(Integer.parseInt(map.get("organization_id").toString()));
				receive.setIsDeleted(Integer.parseInt(map.get("is_deleted").toString()));
				receive.setGmtCreated(simplehms.parse(map.get("gmt_created").toString()));
				receive.setGmtModified(simplehms.parse(map.get("gmt_modified").toString()));
				receive.setReceivedNum(Integer.parseInt(map.get("received_num").toString()));
				receive.setQuantity(Integer.parseInt(map.get("quantity").toString()));
				receive.setUsedNum(Integer.parseInt(map.get("used_num").toString()));
				couponReceives.add(receive);
			}
		}
		return couponReceives;
	}

	/**
	 * 获取C端订单可用的优惠券列表(只在C端使用)
	 * @param order
	 * @return
	 * @throws SqlException
	 * @throws ParseException
	 */
	public static List<UserCouponReceive> getOrderAvaliableCouponList(Order order) throws SqlException, ParseException {
		//自付满减
		PayAmount payAmount = getPayAmountByOrderNum(order.getOrderNum(), PayConstants.OrderType.MytijianOrder);
		long successPayAmount = payAmount.getTotalSuccPayAmount(); //用户订单成功支付金额
		long onlinePayAmount = payAmount.getOnlinePayAmount();//用户第三方支付金额
		long orderPrice = order.getOrderPrice();//订单价格
		long entryCardBalance = 0l;//订单入口卡剩余金额初始值为0
		if(order.getEntryCardId() !=null){
			entryCardBalance = CardChecker.getCardInfo(order.getEntryCardId()).getBalance();
		}
		long selfMoneyPay = orderPrice - (successPayAmount - onlinePayAmount) - entryCardBalance ;//自付金额
		//优惠券信息(未过期，未使用，未删除，医院ID正确,accountId正确,面值符合满减条件) 领取时间倒序排列(订单满减少)
		String sql = "select DISTINCT u.* from tb_user_coupon_receive_record u ,tb_coupon_template c  where u.template_batch_num = c.batch_num and  u.organization_id = "+order.getHospital().getId()+" and u.is_deleted = 0 and u.status = 0 and u.end_time > now() and account_id = "+order.getOperatorId()+"  and ( (c.min_limit_price <= "+order.getOrderPrice()+"  and c.limit_type = 0 )" ;
		if(selfMoneyPay > 0)
			sql +=" or ( c.min_limit_price <= "+selfMoneyPay+"  and c.limit_type = 1  )  ";
		sql +=" ) order by u.end_time ,u.id asc ";
		log.info("支付页面订单满减可用+自付满减减优惠券查询:"+sql);
		List<UserCouponReceive> orderUserCouponReceiveList = getUserCouponList(sql);

//		if(selfMoneyPay > 0){
//			sql = "select DISTINCT u.* from tb_user_coupon_receive_record u ,tb_coupon_template c  where u.template_batch_num = c.batch_num and  u.organization_id = "+order.getFromSite()+" and u.is_deleted = 0 and u.status = 0 and u.end_time > now() and account_id = "+order.getOperatorId()+"  and c.min_limit_price <= "+selfMoneyPay+"  and c.limit_type = 1  order by u.end_time,u.id  asc ";
//			log.info("支付页面自付满减可用优惠券查询:"+sql);
//			List<UserCouponReceive> selfUserCouponReceiveList = getUserCouponList(sql);
//			retUserCouponReceiveList.addAll(selfUserCouponReceiveList);
//		}
//		retUserCouponReceiveList.addAll(orderUserCouponReceiveList);
//		Collections.sort(retUserCouponReceiveList, new Comparator<UserCouponReceive>() {
//			@Override
//			public int compare(UserCouponReceive o1, UserCouponReceive o2) {
//				if(o1.getEndTime().getTime() == o2.getEndTime().getTime())
//					return  o1.getId() - o2.getId();
//				else if(o1.getEndTime().getTime() > o2.getEndTime().getTime())
//					return (int)( - o1.getEndTime().getTime() + o2.getEndTime().getTime());
//				else
//					return (int)(  o1.getEndTime().getTime() - o2.getEndTime().getTime());
//
//			}
//		});
		return orderUserCouponReceiveList;
	}


	/**
	 * 获取C端可用的红包列表
	 * @param order
	 * @return
	 * @throws SqlException
	 * @throws ParseException
	 */
	public static List<UserCouponReceive> getOrderAvaliableRedPackList(Order order) throws SqlException, ParseException {
		//自付满减
		PayAmount payAmount = getPayAmountByOrderNum(order.getOrderNum(), PayConstants.OrderType.MytijianOrder);
		long successPayAmount = payAmount.getTotalSuccPayAmount(); //用户订单成功支付金额
		long onlinePayAmount = payAmount.getOnlinePayAmount();//用户第三方支付金额
		long orderPrice = order.getOrderPrice();//订单价格
		long entryCardBalance = 0l;//订单入口卡剩余金额初始值为0
		if(order.getEntryCardId() !=null){
			entryCardBalance = CardChecker.getCardInfo(order.getEntryCardId()).getBalance();
		}
		long selfMoneyPay = orderPrice - (successPayAmount - onlinePayAmount) - entryCardBalance ;//自付金额
		//优惠券信息(未过期，未使用，未删除，医院ID正确,accountId正确,面值符合满减条件) 领取时间倒序排列(订单满减少)
		String sql = "select DISTINCT u.* from tb_user_coupon_receive_record u ,tb_coupon_template c  where u.template_batch_num = c.batch_num and u.organization_id = -1  and u.is_deleted = 0 and u.status = 0 and u.end_time > now() and account_id = "+order.getOperatorId()+"  and ( (c.min_limit_price <= "+order.getOrderPrice()+"  and c.limit_type = 0 )" ;
		if(selfMoneyPay > 0)
			sql +=" or ( c.min_limit_price <= "+selfMoneyPay+"  and c.limit_type = 1  )  ";
		sql +=" ) order by u.end_time ,u.id asc ";
		log.info("支付页面订单满减可用+自付满减减优惠券查询:"+sql);
		List<UserCouponReceive> orderUserCouponReceiveList = getUserCouponList(sql);

//		if(selfMoneyPay > 0){
//			sql = "select DISTINCT u.* from tb_user_coupon_receive_record u ,tb_coupon_template c  where u.template_batch_num = c.batch_num and  u.organization_id = "+order.getFromSite()+" and u.is_deleted = 0 and u.status = 0 and u.end_time > now() and account_id = "+order.getOperatorId()+"  and c.min_limit_price <= "+selfMoneyPay+"  and c.limit_type = 1  order by u.end_time,u.id  asc ";
//			log.info("支付页面自付满减可用优惠券查询:"+sql);
//			List<UserCouponReceive> selfUserCouponReceiveList = getUserCouponList(sql);
//			retUserCouponReceiveList.addAll(selfUserCouponReceiveList);
//		}
//		retUserCouponReceiveList.addAll(orderUserCouponReceiveList);
//		Collections.sort(retUserCouponReceiveList, new Comparator<UserCouponReceive>() {
//			@Override
//			public int compare(UserCouponReceive o1, UserCouponReceive o2) {
//				if(o1.getEndTime().getTime() == o2.getEndTime().getTime())
//					return  o1.getId() - o2.getId();
//				else if(o1.getEndTime().getTime() > o2.getEndTime().getTime())
//					return (int)( - o1.getEndTime().getTime() + o2.getEndTime().getTime());
//				else
//					return (int)(  o1.getEndTime().getTime() - o2.getEndTime().getTime());
//
//			}
//		});
		return orderUserCouponReceiveList;
	}
}
