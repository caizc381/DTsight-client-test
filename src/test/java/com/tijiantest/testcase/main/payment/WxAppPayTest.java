package com.tijiantest.testcase.main.payment;

import com.alibaba.fastjson.JSON;
import com.jayway.jsonpath.JsonPath;
import com.tijiantest.base.ConfDefine;
import com.tijiantest.base.Flag;
import com.tijiantest.base.HttpResult;
import com.tijiantest.base.dbcheck.*;
import com.tijiantest.model.company.HospitalCompany;
import com.tijiantest.model.counter.HospitalCapacityUsed;
import com.tijiantest.model.item.ExamItem;
import com.tijiantest.model.order.*;
import com.tijiantest.model.order.snapshot.ExamItemSnapshot;
import com.tijiantest.model.order.snapshot.MealSnapshot;
import com.tijiantest.model.order.snapshot.OrderMealSnapshot;
import com.tijiantest.model.paylog.PayConsts;
import com.tijiantest.model.payment.Accounting;
import com.tijiantest.model.payment.PaymentForm;
import com.tijiantest.model.payment.PaymentTypeEnum;
import com.tijiantest.model.payment.trade.*;
import com.tijiantest.model.resource.meal.Meal;
import com.tijiantest.model.resource.meal.MealGenderEnum;
import com.tijiantest.testcase.main.MainBase;
import com.tijiantest.util.CvsFileUtil;
import com.tijiantest.util.db.DBMapper;
import com.tijiantest.util.db.MongoDBUtils;
import com.tijiantest.util.db.SqlException;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 微信小程序订单->支付页面->支付
 *
 * @author huifang
 */
public class WxAppPayTest extends MainBase {
    public static int channelOrderId = 0;// 渠道商二级站点下单
    public static List<Integer> orderId = new ArrayList<Integer>();
    private String site = "mtjk";
    private int fromSite = -1;
    public static com.tijiantest.model.resource.meal.Meal bookMeal = ResourceChecker.getOffcialMeal(defHospitalId, Arrays.asList(MealGenderEnum.FEMALE.getCode()),true).get(0);//可改项目的
    public static int bookMealId = bookMeal.getId();

    @Test(description = "对微信小程序的订单进行支付（目前主要是微信支付）",groups = {"qa"},dataProvider = "wxBook")
//    @Test
    public void test_01_wxApp_orderPayment(String...args) throws Exception {
//        channelOrderId = 4403840;
//        fromSite = 127;

        System.out.println("-----------------------01_C端下单 微信小程序支付页面查看 Start----------------------------");
        String exam_date = args[1];
        HospitalCompany hc = CompanyChecker.getHospitalCompanyByPlatCompanyIdANDOrganizationId(1,defHospitalId);
        Map<String,Object> dateMap = CounterChecker.getDateBookableFromStartDate(sdf.parse(exam_date),sdf.parse(exam_date),hc.getId(), defHospitalId);
        int dayRangeId = Integer.parseInt(dateMap.get("dayRangeId").toString());
        String examTime_interval_id = dayRangeId+"";
        Integer addItemId = Integer.valueOf(args[6]);
        int account_id = defaccountId;
        channelOrderId = OrderChecker.main_createOrder(httpclient,exam_date,Integer.parseInt(examTime_interval_id),account_id,defHospitalId);

        System.out.println("-----------------------01_C端下单 微信小程序支付页面查看 End----------------------------");

        waitto(1);
        //STEP1:入参
        PaymentForm form = new PaymentForm();
        form.setClient("wap");
        form.setSubSite("/wxapp");
        form.setUseBalance(false);
        form.setOrderId(channelOrderId);
        form.setHospitalId(defHospitalId);
        String wxAppOpenid = envConf.getValue(ConfDefine.WX, ConfDefine.WXAPPOPENID);
        form.setOpenid(wxAppOpenid);
        form.setOperator(defaccountId);
        Order orders = OrderChecker.getOrderInfo(channelOrderId);
        String operatorName = AccountChecker.getExaminerByCustomerId(defaccountId,orders.getFromSite()).getName();
        form.setOperatorName(operatorName);
        form.setIsNoLoginIn(false);
        form.setApptype(1);
        form.setPayType(4);//微信支付
        //STEP2:接口调用
        HttpResult result = httpclient.post(Flag.MAIN,Main_OrderPayment, JSON.toJSONString(form));
        //STEP3:返回校验
        log.info("返回值.."+result.getBody());
        Assert.assertEquals(result.getCode(),HttpStatus.SC_OK);

        PayResponse payRes = JSON.parseObject(result.getBody(), PayResponse.class);
        Assert.assertFalse(payRes.isDone());
        Assert.assertTrue(payRes.isNeedNextAction());
        Assert.assertTrue(payRes.isSuccess());
        long requireAmount = payRes.getRequireAmount();
        long successAmount = payRes.getSuccessAmount();
        long payingAmount = payRes.getPayingAmount();
        String tradeOrderNum = payRes.getTradeOrderNum();
        String refOrderNum = payRes.getRefOrderNum();
        String refOrderNumVersion = payRes.getRefOrderNumVersion();
        Assert.assertEquals(payingAmount, requireAmount);

        if(checkdb) {
            //校验订单
            Order order = OrderChecker.getOrderInfo(channelOrderId);
            System.out.println("预约订单状态:" + order.getId() + "状态..." + order.getStatus());
            Assert.assertEquals(order.getStatus(), OrderStatus.PAYING.intValue());
            Assert.assertEquals(refOrderNum, order.getOrderNum());
            Assert.assertEquals(requireAmount, order.getOrderPrice().longValue());
            //支付后的支付情况
            PayAmount afterPayAmount = PayChecker.getPayAmountByOrderNum(order.getOrderNum(), PayConstants.OrderType.MytijianOrder);
            //校验tb_paymentrecord
            String paymentSql = "select p.* from tb_paymentrecord p ,tb_payment_method m  where p.order_id = " + channelOrderId + " and p.payment_method_id = m.id and m.type = " + PaymentTypeEnum.Weixin.getCode() + " order by p.id desc ";
            List<Map<String, Object>> paymentList = DBMapper.query(paymentSql);
            Assert.assertEquals(paymentList.size(), 1);
            Map<String, Object> paymentMap = paymentList.get(0);
            Assert.assertEquals(Integer.parseInt(paymentMap.get("status").toString()), 0); //支付中
            Assert.assertEquals(Integer.parseInt(paymentMap.get("trade_type").toString()), 1); //支付类型
            //校验tb_trade_order【交易订单表】
            List<TradeOrder> tradeOrderList = PayChecker.getTradeOrderByOrderNum(order.getOrderNum(), PayConstants.TradeType.pay);
            Assert.assertTrue(tradeOrderList.size() == 1); //下单 OR 改项目
            TradeOrder to = tradeOrderList.get(0);
            Assert.assertEquals(refOrderNumVersion, to.getRefOrderNumVersion());
            Assert.assertEquals(to.getRefOrderType().intValue(), 1);
            Assert.assertEquals(requireAmount,to.getAmount().longValue());
            Assert.assertEquals(successAmount, to.getSuccAmount().longValue());
            Assert.assertEquals(to.getTradeStatus().intValue(), PayConstants.TradeStatus.Paying);
            Assert.assertEquals(to.getTradeType().intValue(), PayConstants.TradeType.pay);
            Assert.assertEquals(to.getPayMethodType().intValue(), PayConstants.PayMethodBit.WxAppBit);
            //校验tb_trade_pay_record【交易支付表】
            int dbReceiveTradeAccountId =  PayChecker.getSuitableReceiveMethodId(fromSite, PayConstants.PayMethodBit.BalanceBit);
            Accounting mtjk = PayChecker.getSubAccountting(dbReceiveTradeAccountId);
            List<TradePayRecord> tradePayRecList = PayChecker.getTradePayRecordByOrderNum(order.getOrderNum(), to.getTradeOrderNum(), PayConstants.OrderType.MytijianOrder);
            Assert.assertEquals(tradePayRecList.size(), 1);
                TradePayRecord tpr = tradePayRecList.get(0);
                Assert.assertEquals(tpr.getRefOrderType().intValue(),1);
                Assert.assertEquals(tpr.getTradeMethodConfigId().intValue(), PayChecker.getSuitablePayMethodId(fromSite, PayConstants.PayMethodBit.WxAppBit));
                Assert.assertEquals(tpr.getTradeMethodType().intValue(),PayConstants.PayMethod.WxApp);
                Assert.assertEquals(tpr.getPayStatus().intValue(),PayConstants.TradeStatus.Paying);
                Assert.assertEquals(requireAmount,tpr.getPayAmount().longValue());
                Assert.assertEquals(tpr.getPayTradeAccountId().intValue(), PayChecker.getTradeAccountByOrderId(channelOrderId));
                Assert.assertNull(tpr.getPayTradeSubaccountId());
                Assert.assertNull(tpr.getPayTradeAccountSnap());
                Assert.assertNull(tpr.getPayTradeSubaccountType());
                Assert.assertEquals(tpr.getReceiveTradeAccountId().intValue(),dbReceiveTradeAccountId);
                Assert.assertEquals(tpr.getReceiveTradeSubaccountId().intValue(), PayChecker.getSubAccounttingId(dbReceiveTradeAccountId));
                Assert.assertEquals(tpr.getReceiveTradeSubaccountType().intValue(),TradeSubAccountType.TRADE_BALANCE_ACCOUNT);

            //交易tb_trade_account_detail,支付中不往这个表插入数据【账户明细表】
            List<TradeAccountDetail> tradeAccountList = PayChecker.getTradeAccountDetail(to.getTradeOrderNum(), 0);
            Assert.assertEquals(tradeAccountList.size(), 0);
            //出账
            tradeAccountList = PayChecker.getTradeAccountDetail(to.getTradeOrderNum(), 1);
            Assert.assertEquals(tradeAccountList.size(), 0);
          }
        }

    @DataProvider
    public Iterator<String[]> wxBook() {
        try {
            return CvsFileUtil.fromCvsFileToIterator("./csv/order/wxBookOrder.csv", 8);
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
