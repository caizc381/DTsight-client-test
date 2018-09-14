package com.tijiantest.testcase.ops.coupon;

import com.alibaba.fastjson.JSON;
import com.tijiantest.base.Flag;
import com.tijiantest.base.HttpResult;
import com.tijiantest.base.dbcheck.PayChecker;
import com.tijiantest.model.coupon.CouponTemplate;
import com.tijiantest.model.coupon.TradeCreditAccount;
import com.tijiantest.testcase.crm.CrmBase;
import com.tijiantest.testcase.ops.OpsBase;
import com.tijiantest.util.CvsFileUtil;
import com.tijiantest.util.DateUtils;
import com.tijiantest.util.db.DBMapper;
import com.tijiantest.util.db.SqlException;
import org.apache.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * 编辑优惠券模板
 * 位置：OPS->营销->优惠券
 * 操作：编辑
 *
 * @author huifang
 */
public class UpdateCouponTemplateFiledTest extends OpsBase{

    @Test(description = "发放优惠券之前，修改优惠券模板内容",groups = {"qa"},dataProvider = "updateCouponTemplateFiled_beforeSend")
    public void test_01_ops_updateCouponTemplateFiled(String ...args) throws SqlException, ParseException {
        //STEP1:数据文件分割入参
        String name = args[2];
        String quantityStr = args[3];
        String priceStr = args[4];
        String description = args[5];
        String startTimeStr = args[6];
        String endTimeStr = args[7];
        String receiveLimitNumStr = args[8];
        String minLimitPriceStr = args[9];
//        String sourceStr = args[10];

        //STEP2:提取新建优惠券模板的批次号，以及查询现在的客户经理的授信账户
        String batchNum = null;
        int beforeCreditFreezeLimit = 0;//修改前客户经理授信账户冻结金额(初始值0)
        int beforeCreditLimit = 0;//修改前客户经理信用账户
        int beforeBalance = 0;//修改前客户经理余额
        int trade_account_id = 0;//修改前客户经理的交易总账户
        int beforeQuatity = 0;//修改前发放总量
        int beforePrice = 0;//修改前发放的单个优惠券面值
        int organizationId = 0;//机构的ID
        int ownerId = 0;//新建优惠券人
        if(checkdb){
            //随机从数据库查询1条新建的优惠券模板
            String sql = "select c.* from tb_coupon_template c  where c.status = 1 and c.end_time > now() and is_deleted = 0 ";
            List<CouponTemplate> couponTemplateList = PayChecker.getCouponTemplateList(sql);
            if(couponTemplateList != null && couponTemplateList.size() > 0){
                batchNum = couponTemplateList.get(0).getBatchNum();
                beforeQuatity = couponTemplateList.get(0).getQuantity();
                beforePrice = couponTemplateList.get(0).getPrice();
                organizationId = couponTemplateList.get(0).getOrganizationId();
                //获取修改前客户经理的授信账户
                ownerId = couponTemplateList.get(0).getOwnerId();
                trade_account_id = PayChecker.getTradeAccountIdByAccountId(ownerId);
            }
        }
        if(batchNum == null){
            log.error("数据库中没有新建的优惠券模板，不适用于本用例，本用例无法执行");
            return;
        }
        //STEP3:入参转换对象
        CouponTemplate ct = PayChecker.getCouponTemplateList("batch_num",batchNum,"is_deleted","0").get(0);
        int quantity = Integer.parseInt(quantityStr);
        int price = Integer.parseInt(priceStr);
        Date startTime = new Date();//开始时间当天
        Date endTime = DateUtils.offDate(10);//今天之后的10天内
        int receiveLimitNum = Integer.parseInt(receiveLimitNumStr);
        int minLimitPrice = Integer.parseInt(minLimitPriceStr) + price;
        int source = ct.getSource();
        ct.setBatchNum(batchNum);
        ct.setName(name);
        ct.setQuantity(quantity);
        ct.setPrice(price);
        ct.setDescription(description);
        ct.setStartTime(startTime);
        ct.setEndTime(endTime);
        ct.setReceiveLimitNumber(receiveLimitNum);
        ct.setMinLimitPrice(minLimitPrice);
        ct.setSource(source);
        ct.setOrganizationId(organizationId);
        ct.setOwnerId(ownerId);


        //STEP4.确保信用额度足够
        TradeCreditAccount creditAccount = PayChecker.getTradeCreditAccount(trade_account_id);
        beforeCreditFreezeLimit = creditAccount.getFreezeCreditLimit();
        beforeCreditLimit = creditAccount.getCreditLimit();
        beforeBalance = creditAccount.getBalance();
        if(beforeCreditLimit - beforeCreditFreezeLimit < (price * quantity - beforeQuatity * beforePrice)){//如果现在可用的额度比 需要增加的金额数量少则手动调大授信额度
            int needUpLimit = price * quantity - beforeQuatity * beforePrice - (beforeCreditLimit - beforeCreditFreezeLimit );
            DBMapper.update("update tb_trade_credit_account set credit_limit = "+ (beforeCreditFreezeLimit + needUpLimit+1000) +" where trade_account_id = "+trade_account_id);
            log.info("调整授信额度为"+(beforeCreditFreezeLimit + needUpLimit+1000));
            creditAccount = PayChecker.getTradeCreditAccount(trade_account_id);
            beforeCreditLimit = creditAccount.getCreditLimit();
            beforeBalance = creditAccount.getBalance();
            beforeCreditFreezeLimit = creditAccount.getFreezeCreditLimit();

        }
        //STEP5.调用接口
        HttpResult result = httpclient.post(Flag.OPS,Coupon_UpdateCouponTemplateFiled, JSON.toJSONString(ct));
        //STEP6:验证返回&DB
        log.info("body..."+result.getBody());
        Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);
        if(checkdb){
            //修改优惠券模板后，优惠券模板信息变更
            List<CouponTemplate> couponTemplateList = PayChecker.getCouponTemplateList("batch_num","'"+batchNum+"'","is_deleted","0");
            Assert.assertEquals(couponTemplateList.size(),1);
            CouponTemplate couponTemplate = couponTemplateList.get(0);
            Assert.assertEquals(couponTemplate.getName(),name);
            Assert.assertEquals(couponTemplate.getQuantity(),quantity);
            Assert.assertEquals(couponTemplate.getPrice(),price);
            Assert.assertEquals(couponTemplate.getDescription(),description);
            Assert.assertEquals(sdf.format(couponTemplate.getStartTime()),sdf.format(startTime));
            Assert.assertEquals(sdf.format(couponTemplate.getEndTime()),sdf.format(endTime));
            Assert.assertEquals(couponTemplate.getReceiveLimitNumber(),receiveLimitNum);
            Assert.assertEquals(couponTemplate.getMinLimitPrice(),minLimitPrice);
            Assert.assertEquals(couponTemplate.getSource(),source);
            Assert.assertEquals(couponTemplate.getStatus(),1);//新建状态
            Assert.assertEquals(couponTemplate.getIsDeleted(),0);//未删除

            //客户经理信用账户影响(冻结金额变化，余额/信用账户不变)
            TradeCreditAccount afterCreditAccount =  PayChecker.getTradeCreditAccount(trade_account_id);
            Assert.assertEquals(afterCreditAccount.getBalance(),beforeBalance);
            Assert.assertEquals(afterCreditAccount.getCreditLimit(),beforeCreditLimit);
            int calculate_feezeCreditLimit = beforeCreditFreezeLimit + quantity * price - beforeQuatity * beforePrice ;
            Assert.assertEquals(afterCreditAccount.getFreezeCreditLimit(),calculate_feezeCreditLimit);//现在的冻结金额 = 原来的冻结金额+之前发放优惠券的面值*数量 - 现在发放优惠券的面值*数量

        }
    }



    @Test(description = "发放优惠券之后，修改优惠券模板内容",groups = {"qa"},dataProvider = "updateCouponTemplateFiled_afterSend")
    public void test_02_ops_updateCouponTemplateFiled(String ...args) throws SqlException, ParseException {
        //STEP1:数据文件分割入参
        String name = args[2];
        String quantityStr = args[3];
        String description = args[4];
//        String sourceStr = args[5];

        //STEP2:提取已发放的优惠券模板的批次号，以及查询现在的客户经理的授信账户
        String batchNum = null;
        CouponTemplate ct = null;
        int beforeCreditFreezeLimit = 0;//修改前客户经理授信账户冻结金额(初始值0)
        int beforeCreditLimit = 0;//修改前客户经理信用账户
        int beforeBalance = 0;//修改前客户经理余额
        int trade_account_id = 0;//修改前客户经理的交易总账户
        int beforeQuatity = 0;//修改前发放总量
        int beforePrice = 0;//修改前发放的单个优惠券面值
        int organizationId = 0;//机构的ID
        int ownerId = 0;//新建优惠券人
        if(checkdb){
            //随机从数据库查询1条新建的优惠券模板
            String sql = "select c.* from tb_coupon_template c  where c.status = 2 and c.end_time > now() and is_deleted = 0 ";
            List<CouponTemplate> couponTemplateList = PayChecker.getCouponTemplateList(sql);
            if(couponTemplateList != null && couponTemplateList.size() > 0){
                batchNum = couponTemplateList.get(0).getBatchNum();
                organizationId = couponTemplateList.get(0).getOrganizationId();
                beforeQuatity = couponTemplateList.get(0).getQuantity();
                beforePrice = couponTemplateList.get(0).getPrice();
                //获取修改前客户经理的授信账户
                ownerId = couponTemplateList.get(0).getOwnerId();
                trade_account_id = PayChecker.getTradeAccountIdByAccountId(ownerId);
            }
        }
        if(batchNum == null){
            log.error("数据库中没有已发放的优惠券模板，不适用于本用例，本用例无法执行");
            return;
        }
        //STEP3:入参转换对象
        ct = PayChecker.getCouponTemplateList("batch_num",batchNum,"is_deleted","0").get(0);
        int quantity = Integer.parseInt(quantityStr) + ct.getQuantity();
        int source = ct.getSource();
        ct.setBatchNum(batchNum);
        ct.setName(name);
        ct.setQuantity(quantity);
        ct.setDescription(description);
        ct.setSource(source);
        ct.setOrganizationId(organizationId);
        ct.setOwnerId(ownerId);

        //STEP4.确保信用额度充足
        TradeCreditAccount creditAccount = PayChecker.getTradeCreditAccount(trade_account_id);
        beforeCreditFreezeLimit = creditAccount.getFreezeCreditLimit();
        beforeCreditLimit = creditAccount.getCreditLimit();
        beforeBalance = creditAccount.getBalance();
        if(beforeCreditLimit - beforeCreditFreezeLimit < (beforePrice * quantity - beforeQuatity * beforePrice)){//如果现在可用的额度比 需要增加的金额数量少则手动调大授信额度
            int needUpLimit = beforePrice * quantity - beforeQuatity * beforePrice - (beforeCreditLimit - beforeCreditFreezeLimit );
            DBMapper.update("update tb_trade_credit_account set credit_limit = "+ (beforeCreditFreezeLimit + needUpLimit+1000) +" where trade_account_id = "+trade_account_id);
            creditAccount = PayChecker.getTradeCreditAccount(trade_account_id);
            beforeCreditLimit = creditAccount.getCreditLimit();
            beforeBalance = creditAccount.getBalance();
            beforeCreditFreezeLimit = creditAccount.getFreezeCreditLimit();
        }

        //STEP5.调用接口
        HttpResult result = httpclient.post(Flag.OPS,Coupon_UpdateCouponTemplateFiled, JSON.toJSONString(ct));
        //STEP6:验证返回&DB
        log.info("body..."+result.getBody());
        Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);
        if(checkdb){
            //修改优惠券模板后，优惠券模板信息变更
            List<CouponTemplate> couponTemplateList = PayChecker.getCouponTemplateList("batch_num","'"+batchNum+"'","is_deleted","0");
            Assert.assertEquals(couponTemplateList.size(),1);
            CouponTemplate couponTemplate = couponTemplateList.get(0);
            Assert.assertEquals(couponTemplate.getName(),name);
            Assert.assertEquals(couponTemplate.getQuantity(),quantity);
            Assert.assertEquals(couponTemplate.getPrice(),beforePrice);
            Assert.assertEquals(couponTemplate.getDescription(),description);
            Assert.assertEquals(couponTemplate.getStatus(),2);//发放状态
            Assert.assertEquals(couponTemplate.getIsDeleted(),0);//未删除

            //客户经理信用账户影响(冻结金额变化，余额/信用账户不变)
            TradeCreditAccount afterCreditAccount =  PayChecker.getTradeCreditAccount(trade_account_id);
            Assert.assertEquals(afterCreditAccount.getBalance(),beforeBalance);
            Assert.assertEquals(afterCreditAccount.getCreditLimit(),beforeCreditLimit);
            int calculate_feezeCreditLimit = beforeCreditFreezeLimit - beforeQuatity * beforePrice + quantity * beforePrice;
            Assert.assertEquals(afterCreditAccount.getFreezeCreditLimit(),calculate_feezeCreditLimit);//现在的冻结金额 = 原来的冻结金额+之前发放优惠券的面值*数量 - 现在发放优惠券的面值*数量

        }
    }

    @DataProvider
    public Iterator<String[]> updateCouponTemplateFiled_beforeSend(){
        try {
            return CvsFileUtil.fromCvsFileToIterator("./csv/coupon/ops/updateCouponTemplateFiled_beforeSend.csv",18);
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
    public Iterator<String[]> updateCouponTemplateFiled_afterSend(){
        try {
            return CvsFileUtil.fromCvsFileToIterator("./csv/coupon/ops/updateCouponTemplateFiled_afterSend.csv",18);
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
