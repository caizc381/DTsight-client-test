package com.tijiantest.testcase.main.payment;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.tijiantest.base.dbcheck.PayChecker;
import com.tijiantest.model.coupon.UserCouponReceive;
import com.tijiantest.model.coupon.UserCouponVOs;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.jayway.jsonpath.JsonPath;
import com.tijiantest.base.Flag;
import com.tijiantest.base.HttpResult;
import com.tijiantest.base.dbcheck.CardChecker;
import com.tijiantest.base.dbcheck.HospitalChecker;
import com.tijiantest.base.dbcheck.OrderChecker;
import com.tijiantest.model.account.Account;
import com.tijiantest.model.card.Card;
import com.tijiantest.model.hospital.HospitalSettings;
import com.tijiantest.model.order.Order;
import com.tijiantest.model.paylog.TradeTypeEnum;
import com.tijiantest.model.payment.Accounting;
import com.tijiantest.model.payment.invoice.Invoice;
import com.tijiantest.model.resource.meal.MealSnap;
import com.tijiantest.testcase.main.MainBase;
import com.tijiantest.testcase.main.order.BookTest;
import com.tijiantest.util.CvsFileUtil;
import com.tijiantest.util.db.DBMapper;
import com.tijiantest.util.db.SqlException;

import static com.tijiantest.base.dbcheck.PayChecker.getUserCouponList;

public class PayPageTest extends MainBase {
	public static int orderId;// 订单ID
	public static int entryCardId;// 使用的卡id

	@SuppressWarnings({ "unused", "deprecation" })
	@Test(dependsOnGroups = { "main_bookWithEntryCard" }, description = "有入口卡的订单 - 支付页面", groups = {
			"qa" }, dataProvider = "payPage")
	public void test_01_payPage(String... args) throws SqlException, ParseException {
		orderId = BookTest.comm_entryCard_OrderId;
//		orderId = 4397952;
		System.out.println("支付页面 - 订单ID:" + orderId);

		Order order = OrderChecker.getOrderInfo(orderId);
		MealSnap mealSnap = JSON.parseObject(order.getMealDetail(), MealSnap.class);
		int mealId = mealSnap.getId();
		System.out.println("套餐ID:" + mealId);
		entryCardId = BookTest.entryCardId;
//		entryCardId = 4232924;
		Card entryCard = CardChecker.getCardInfo(entryCardId);
		String _site = HospitalChecker.getSiteByOrganizationId(order.getHospital().getId());
		String _siteType = args[2];

		List<NameValuePair> pairs = new ArrayList<>();
		pairs.add(new BasicNameValuePair("orderId", orderId + ""));
		pairs.add(new BasicNameValuePair("selectedCardId", entryCardId + ""));
		pairs.add(new BasicNameValuePair("_site", _site));
		pairs.add(new BasicNameValuePair("_siteType", _siteType));

		HttpResult result = httpclient.get(Flag.MAIN, Main_PayPageV2, pairs);
		String body = result.getBody();
		System.out.println(body);
		Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);
		// account
		Account account = JSON.parseObject(JsonPath.read(body, "$.account").toString(), Account.class);
		// accounting
		Accounting accounting = JSON.parseObject(JsonPath.read(body, "$.accounting").toString(), Accounting.class);
		// invoice
		Invoice invoice = JSON.parseObject(JsonPath.read(body, "$.invoice").toString(), Invoice.class);

		// acceptOfflinePay
		// aliPayList
		// canUseSelectedCard
		// cardList
		List<Card> cardList = JSON.parseObject(JsonPath.read(body, "$.cardList").toString(),
				new TypeReference<List<Card>>() {
				});

		// changed
		Boolean changed = JsonPath.read(body, "$.changed");
		// deadTime
		// examDate
		String examDate = JsonPath.read(body, "$.examDate").toString();
		// examReport
		// examTimeIntervalName
		String examTimeIntervalName = JsonPath.read(body, "$.examTimeIntervalName").toString();
		// hospitalId
		int hospitalId = JsonPath.read(body, "$.hospitalId");
		// hospitalName
		// hospitalSetting
		HospitalSettings hospitalSettings = JSON.parseObject(JsonPath.read(body, "$.hospitalSetting").toString(),
				HospitalSettings.class);
		// insertTime
		// offlinePay
		// orderId
		// orderInvoiceMakeOutDateTip
		String orderInvoiceMakeOutDateTip = JsonPath.read(body, "$.orderInvoiceMakeOutDateTip").toString();
		// orderPrice
		String orderPrice = JsonPath.read(body, "$.orderPrice").toString();
		// paid
		// paidMoney
		String paidMoney = JsonPath.read(body, "$.paidMoney").toString();
		// selectedCardIds
		List<Integer> selectedCardIds = JSON.parseObject(JsonPath.read(body, "$.selectedCardIds").toString(),
				new TypeReference<List<Integer>>() {
				});
		// showInvoice
		// unionPayList
		// weixinPayList
		// 体检项目
		int examProject = Integer.parseInt(JsonPath.read(body, "$.体检项目").toString());
		//orderAccount
		JSONObject retOrderAccount = JSONObject.parseObject(JsonPath.read(body, "$.orderAccount").toString());

		List<UserCouponVOs> availableCouponList = JSON.parseArray(JsonPath.read(body,"$.availableCoupon").toString(),UserCouponVOs.class);//可用优惠券列表
		int availableCouponNum = JsonPath.read(body,"$.availableCouponNum");//可用优惠券数量

		if (checkdb) {
			// account
			String accountSql = "select account.* from tb_account account LEFT JOIN tb_order o on o.account_id=account.id where o.id=?";
			List<Map<String, Object>> accountList = DBMapper.query(accountSql, orderId);
			Assert.assertEquals(account.getMobile(), accountList.get(0).get("mobile"));

			// accounting
			String accountingSql = "SELECT id, account_id, balance FROM tb_accounting WHERE account_id =?";
			List<Map<String, Object>> accountingList = DBMapper.query(accountingSql, defaccountId);
			Assert.assertEquals(accounting.getBalance(), accountingList.get(0).get("balance"));
			Assert.assertEquals(accounting.getId(), accountingList.get(0).get("id"));

			// maillingRecord

			// examReport

			// examDate


			Order dbOrder = OrderChecker.getOrderInfo(orderId);
			int dbExamUserId = dbOrder.getOrderAccount().getId();
			System.out.println("体检时间：" + dbOrder.getExamDate().toString());

			// examTimeIntervalName
			String examTimeIntervalNameSql = "select tb_order.id, batch_id, order_num, account_id, tb_order.hospital_id, tb_order.status, order_price, difference_price, exam_date, entry_card_id, discount, is_export, source, insert_time, tb_order.update_time,exam_time_interval_id, remark, from_site,hs.name as exam_time_interval_name from tb_order  left join tb_hospital_period_settings hs on hs.id = tb_order.exam_time_interval_id where tb_order.id = ?";
			List<Map<String, Object>> examTimeIntervalNameList = DBMapper.query(examTimeIntervalNameSql, orderId);
			Assert.assertEquals(examTimeIntervalName, examTimeIntervalNameList.get(0).get("exam_time_interval_name"));

			// changed
			String changedSql = "SELECT	id, order_id orderId, type, export_price_diff exportPriceDiff, order_price_diff orderPriceDiff, old_items oldItems, new_items newItems, operator_id operatorId, status FROM tb_order_change_log WHERE type = 1 AND	order_id=? ORDER BY id LIMIT 1";
			List<Map<String, Object>> changedList = DBMapper.query(changedSql, orderId);
			Assert.assertEquals(changed ? 1 : 0, changedList.size() > 0 ? 1 : 0);

			// cardList
			String cardListSql = "select * from tb_card where id = ? and balance > 0";
			List<Map<String, Object>> cardListList = DBMapper.query(cardListSql,entryCardId);
			boolean canUseSelectedCard = false;
			for (int i = 0; i < cardList.size(); i++) {
				Map<String, Object> map = cardListList.get(i);
				if (Integer.parseInt(map.get("id").toString()) == entryCardId) {
					canUseSelectedCard = true;
					break;
				}
			}
			Assert.assertEquals(Boolean.parseBoolean(JsonPath.read(body, "$.canUseSelectedCard").toString()),
					canUseSelectedCard);
			Integer selectedCardId = entryCardId;
			// cardList排序，把selectedCardId放在第一位
			List<Map<String, Object>> sortedList = new ArrayList<>();
			if (selectedCardId != null && canUseSelectedCard) {
				for (Map<String, Object> map : cardListList) {
					if (selectedCardId.equals(map.get("id"))) {
						sortedList.add(map);
						break;
					}
				}
			}
			Assert.assertEquals(cardList.size(), sortedList.size());
			for (int i = 0; i < cardList.size(); i++) {
				Card card = cardList.get(i);
				Map<String, Object> map = sortedList.get(i);
				Assert.assertEquals(card.getAccountId(), map.get("account_id"));
				if (card.getCardSetting().isShowCardMealPrice()) {
					Assert.assertEquals(card.getBalance(), new Long(0));
					Assert.assertEquals(card.getCapacity(), new Long(0));
				} else {
					Assert.assertEquals(card.getBalance(), map.get("balance"));
					Assert.assertEquals(card.getCapacity(), map.get("capacity"));
				}
				Assert.assertEquals(card.getBatchId(), map.get("batch_id"));
				Assert.assertEquals(card.getCardName(), map.get("card_name"));
				Assert.assertEquals(card.getCardNum(), map.get("card_num"));
				Assert.assertEquals(card.getId(), map.get("id"));
				Assert.assertEquals(card.getParentCardId(), map.get("parent_card_id"));
				Assert.assertEquals(card.getStatus(), map.get("status"));
				Assert.assertEquals(card.getType(), map.get("type"));

				String settingsSql = "select * from tb_card_settings where card_id=?";
				List<Map<String, Object>> settingsList = DBMapper.query(settingsSql, card.getId());
				Assert.assertEquals(card.getCardSetting().isPayFreeze() ? 1 : 0,
						settingsList.get(0).get("is_pay_freeze"));
				Assert.assertEquals(card.getCardSetting().isPayMealCost() ? 1 : 0,
						settingsList.get(0).get("is_pay_meal_cost"));
				Assert.assertEquals(card.getCardSetting().isPrivate() ? 1 : 0, settingsList.get(0).get("isprivate"));
				Assert.assertEquals(card.getCardSetting().isShowCardMealPrice() ? 1 : 0,
						settingsList.get(0).get("is_show_card_meal_price"));
			}

			// selectedCardIds
			if (cardListList.size() > 0) {
				Assert.assertEquals(selectedCardIds.get(0), cardListList.get(0).get("id"));
			}

			// hospitalId 与订单来源站点一致
			Assert.assertEquals(hospitalId, order.getFromSite().intValue());
			// hospitalSetting
			String hospitalSettingsSql = "select * from tb_hospital_settings where hospital_id=?";
			List<Map<String, Object>> hospitalSettingsList = DBMapper.query(hospitalSettingsSql, hospitalId);
			// 支付方式
			// 判断是否是体检中心，或者渠道商
			System.out.println("是否是体检中心：" + HospitalChecker.checkHospitalOrChannel(_site));
			//订单是通过入口卡进入，则支付方式按照入口卡源站点取
			Assert.assertEquals(hospitalSettings.getAcceptOfflinePay()?1:0, hospitalSettingsList.get(0).get("accept_offline_pay"));
			Assert.assertEquals(hospitalSettings.getAccountPay() ? 1 : 0,
						hospitalSettingsList.get(0).get("account_pay"));
			Assert.assertEquals(hospitalSettings.getAliPay() ? 1 : 0, hospitalSettingsList.get(0).get("ali_pay"));
			Assert.assertEquals(hospitalSettings.getNeedLocalPay() ? 1 : 0,
						hospitalSettingsList.get(0).get("need_local_pay"));
			Assert.assertEquals(hospitalSettings.getOnlyLocalePay() ? 1 : 0,
						hospitalSettingsList.get(0).get("only_locale_pay"));
			Assert.assertEquals(hospitalSettings.getWeiXinPay() ? 1 : 0,
						hospitalSettingsList.get(0).get("weixin_pay"));

//			}

			// 支付时的提示文字
			Assert.assertEquals(hospitalSettings.getPayTipText(), hospitalSettingsList.get(0).get("pay_tip_text"));

			// 发票
			Assert.assertEquals(hospitalSettings.getInvoiceRequired(),
					hospitalSettingsList.get(0).get("invoice_required"));
			Assert.assertEquals(hospitalSettings.getMakeOutInvoice() ? 1 : 0,
					hospitalSettingsList.get(0).get("make_out_invoice"));
			Assert.assertEquals(hospitalSettings.getInvoiceRequired(),
					hospitalSettingsList.get(0).get("invoice_required"));

			// 电话
			Assert.assertEquals(hospitalSettings.getTechnicalTel(), hospitalSettingsList.get(0).get("technical_tel"));

			// orderPrice,体检项目
			if(cardList != null && !cardList.isEmpty()){
				System.out.println("cardList..."+cardList);
				if (cardList.get(0).getCardSetting().isPrivate()//隐价卡
						&& cardList.get(0).getCardSetting().isShowCardMealPrice()) {
					Assert.assertEquals(Integer.parseInt(orderPrice), 0);
					Assert.assertEquals(examProject, dbOrder.getOrderPrice().intValue());

				}

			} else {
				Assert.assertEquals(Integer.parseInt(orderPrice),dbOrder.getOrderPrice().intValue());
				Assert.assertEquals(examProject, dbOrder.getOrderPrice().intValue());
			}
			

			// paidMoney
			String paymentSql = "select p.id, p.serial_number, p.order_id, p.account_id, p.payment_method_id, p.status, p.amount, p.pay_time, p.trade_type, p.is_primary, p.expense_account, p.trade_no, p.remark from tb_paymentrecord p left join tb_payment_method pm on pm.id = p.payment_method_id where p.order_id = ? and p.`status`=1 and p.trade_type=? order by pm.type desc, p.is_primary";
			List<Map<String, Object>> paymentList = DBMapper.query(paymentSql, orderId,
					TradeTypeEnum.OrderPayment.getCode());
			int sum = 0;
			for (int i = 0; i < paymentList.size(); i++) {
				sum += Integer.valueOf(paymentList.get(i).get("amount").toString());
			}
			// TODO: 隐价卡
			Assert.assertEquals(paidMoney, sum + "");

			// orderInvoiceMakeOutDateTip
			Assert.assertEquals(orderInvoiceMakeOutDateTip, "如需开票，请于体检完成一天后，在<我的订单-订单详情>中申请开票，我院免费提供邮寄纸质发票服务。<br/>恕现场不对线上付款开票。");
			
		
			//orderAccount---体检人
			Assert.assertEquals(retOrderAccount.get("name"),order.getOrderAccount().getName());

			//优惠券信息(未过期，未使用，未删除，医院ID正确,accountId正确,面值符合满减条件) 领取时间倒序排列
			List<UserCouponReceive> dbUserCouponReceiveList = PayChecker.getOrderAvaliableCouponList(dbOrder);
			if(dbUserCouponReceiveList != null && dbUserCouponReceiveList.size()>0){
				Assert.assertEquals(availableCouponNum ,dbUserCouponReceiveList.size());//判断可用优惠券数量
				for(int k=0;k<dbUserCouponReceiveList.size();k++){//判断可用优惠券的详情
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
		}

	}

	@DataProvider(name = "payPage")
	public Iterator<String[]> payPage() {
		try {
			return CvsFileUtil.fromCvsFileToIterator("./csv/payment/payPage.csv", 1);
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
