package com.tijiantest.testcase.ops.coupon;

import com.tijiantest.base.Flag;
import com.tijiantest.base.HttpResult;
import com.tijiantest.base.dbcheck.PayChecker;
import com.tijiantest.model.coupon.CouponTemplate;
import com.tijiantest.model.coupon.CouponTemplateStatistics;
import com.tijiantest.model.coupon.TradeCreditAccount;
import com.tijiantest.testcase.crm.CrmBase;
import com.tijiantest.testcase.ops.OpsBase;
import com.tijiantest.util.db.SqlException;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * 停用|发放优惠券模板
 *
 * 位置：OPS->营销->优惠券(停用|发放优惠券)
 *
 * @author huifang
 */
public class UpdateTemplateCouponStatusTest extends OpsBase {

    @Test(description = "停用优惠券（前提时优惠券模板正常)",groups = {"qa"})
    public void test_01_updateTemplateCouponStatus_stop() throws SqlException, ParseException {
        //STEP1:随机获取1个医院符合条件的模板批次号
        String batchNum = null;
        int beforeCreditFreezeLimit = 0;//停用前客户经理授信账户冻结金额(初始值0)
        int beforeCreditLimit = 0;//停用前客户经理信用账户
        int beforeBalance = 0;//停用前客户经理余额
        int trade_account_id = 0;//停用前客户经理的交易总账户
        int hospitalId = 0; //医院ID
        if(checkdb){
            //随机从数据库查询1条可用于领取的优惠券模板（不限制机构ID）
            String sql = "select c.* from tb_coupon_template c  where c.status in (1,2) and c.end_time > now() and is_deleted = 0  ";
            List<CouponTemplate>  couponTemplateList = PayChecker.getCouponTemplateList(sql);
            if(couponTemplateList != null && couponTemplateList.size() > 0){
                batchNum = couponTemplateList.get(0).getBatchNum();
                hospitalId = couponTemplateList.get(0).getOrganizationId();
                //获取停用前客户经理的授信账户
                int ownerId = couponTemplateList.get(0).getOwnerId();
                trade_account_id = PayChecker.getTradeAccountIdByAccountId(ownerId);
                TradeCreditAccount creditAccount = PayChecker.getTradeCreditAccount(trade_account_id);
                beforeCreditFreezeLimit = creditAccount.getFreezeCreditLimit();
                beforeBalance = creditAccount.getBalance();
                beforeCreditLimit = creditAccount.getCreditLimit();
            }
        }
        if(batchNum == null){
            log.error("医院"+hospitalId+"中没有可用于正常状态的优惠券模板了（正常状态=新建/发放/未过期)，无法进行停用操作,本用例无法执行");
            return;
        }
        //STEP2:准备入参
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("status",3+""));//停用状态
        params.add(new BasicNameValuePair("batchNum",batchNum));
        params.add(new BasicNameValuePair("source",1+""));//OPS
        //STEP3:调用接口
        HttpResult result = httpclient.get(Flag.OPS,Coupon_UpdateTemplateCouponStatus,params);
        //STEP4:验证返回&DB
        Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);
        log.info("body:"+result.getBody());
        if(checkdb){
            //4.0 获取优惠券模板的现有统计情况
            List<CouponTemplateStatistics> statistics = PayChecker.getCouponTemplateStatistics("template_batch_num","'"+batchNum+"'");
            Assert.assertEquals(statistics.size(),1);
            CouponTemplateStatistics statistic = statistics.get(0);
            int receive_num = statistic.getReceivedNum();//已领取数量
            int quantity = statistic.getQuantity();//发放总量
            //4.1 优惠券模板被停用
            List<CouponTemplate> dbCoupTemplateList = PayChecker.getCouponTemplateList("batch_num","'"+batchNum+"'","is_deleted","0");
            Assert.assertEquals(dbCoupTemplateList.size(),1);
            CouponTemplate couponTemplate = dbCoupTemplateList.get(0);
            Assert.assertEquals(couponTemplate.getStatus(),3);//已停用
            Assert.assertFalse(couponTemplate.isRecovery());

            int needRecyclePrice = (quantity - receive_num) * couponTemplate.getPrice();
            //4.2 未领取的优惠券被回收(回收金额=未领取的数量*面值)
            TradeCreditAccount creditAccount2 = PayChecker.getTradeCreditAccount(trade_account_id);
            Assert.assertEquals(creditAccount2.getBalance(),beforeBalance);
            Assert.assertEquals(creditAccount2.getCreditLimit(),beforeCreditLimit);
            Assert.assertEquals(creditAccount2.getFreezeCreditLimit(),beforeCreditFreezeLimit - needRecyclePrice);

        }
    }

    @Test(description = "发放优惠券（前提是优惠券模板为新建状态)",groups = {"qa"})
    public void test_02_updateTemplateCouponStatus_send() throws SqlException, ParseException {
        //STEP1:随机获取1个医院符合条件的模板批次号
        String batchNum = null;
        int beforeCreditFreezeLimit = 0;//发放前客户经理授信账户冻结金额(初始值0)
        int beforeCreditLimit = 0;//发放前客户经理信用账户
        int beforeBalance = 0;//发放前客户经理余额
        int trade_account_id = 0;//发放前客户经理的交易总账户
        int hospitalId = 0;//医院ID
        if(checkdb){
            //随机从数据库查询1条新建的优惠券模板（不限制机构ID）
            String sql = "select c.* from tb_coupon_template c  where c.status =1 and c.end_time > now() and is_deleted = 0 ";
            List<CouponTemplate>  couponTemplateList = PayChecker.getCouponTemplateList(sql);
            if(couponTemplateList != null && couponTemplateList.size() > 0){
                batchNum = couponTemplateList.get(0).getBatchNum();
                hospitalId = couponTemplateList.get(0).getOrganizationId();
                //获取发放前客户经理的授信账户
                int ownerId = couponTemplateList.get(0).getOwnerId();
                trade_account_id = PayChecker.getTradeAccountIdByAccountId(ownerId);
                TradeCreditAccount creditAccount = PayChecker.getTradeCreditAccount(trade_account_id);
                beforeCreditFreezeLimit = creditAccount.getFreezeCreditLimit();
                beforeBalance = creditAccount.getBalance();
                beforeCreditLimit = creditAccount.getCreditLimit();
            }
        }
        if(batchNum == null){
            log.error("医院"+hospitalId+"中没有新建的优惠券模板，无法发放优惠券，本用例无法执行");
            return;
        }
        //STEP2:准备入参
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("status",2+""));//发放状态
        params.add(new BasicNameValuePair("batchNum",batchNum));
        params.add(new BasicNameValuePair("source",1+""));//OPS
        //STEP3:调用接口
        HttpResult result = httpclient.get(Flag.OPS,Coupon_UpdateTemplateCouponStatus,params);
        //STEP4:验证返回&DB
        Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);
        log.info("body:"+result.getBody());
        if(checkdb){
            //4.1 优惠券模板被停用
            List<CouponTemplate> dbCoupTemplateList = PayChecker.getCouponTemplateList("batch_num","'"+batchNum+"'","is_deleted","0");
            Assert.assertEquals(dbCoupTemplateList.size(),1);
            CouponTemplate couponTemplate = dbCoupTemplateList.get(0);
            Assert.assertEquals(couponTemplate.getStatus(),2);//已发放
            Assert.assertFalse(couponTemplate.isRecovery());

           //4.2 客户经理授信账户不变
            TradeCreditAccount creditAccount2 = PayChecker.getTradeCreditAccount(trade_account_id);
            Assert.assertEquals(creditAccount2.getBalance(),beforeBalance);
            Assert.assertEquals(creditAccount2.getCreditLimit(),beforeCreditLimit);
            Assert.assertEquals(creditAccount2.getFreezeCreditLimit(),beforeCreditFreezeLimit);

        }
    }
}
