package com.tijiantest.testcase.crm.coupon;

import com.alibaba.fastjson.JSON;
import com.tijiantest.base.HttpResult;
import com.tijiantest.base.dbcheck.PayChecker;
import com.tijiantest.model.coupon.CouponTemplate;
import com.tijiantest.model.coupon.TradeCreditAccount;
import com.tijiantest.testcase.crm.CrmBase;
import com.tijiantest.util.CvsFileUtil;
import com.tijiantest.util.DateUtils;
import com.tijiantest.util.db.SqlException;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.*;

/*
@author:qfm
新建优惠券
位置：crm维护后台--营销--优惠券--新建优惠券
*/
public class NewCouponTemplateTest extends CrmBase{
    @Test(description = "新创建优惠券模板" ,groups = {"qa"},dataProvider = "crmCoupon")
    public void test_02_newCoupontemplate(String...args) throws SqlException, ParseException {
       /* String name;//优惠券名称
        Number quantity;//发放数量
        Number price;//券价格
        String description;//优惠券说明
        String startTime;//券启用起始时间
        String endTime;//券结束时间
        Number receiveLimitNumber;//每人限领数量
        Number minLimitPrice;//最低消费
        Number source;//优惠券来源
        Number ordanizationId;//机构ID*/

        String nameStr=args[1];
        String quantityNum=args[2];
        String priceNum=args[3];
        String min_limit_price=args[4];
        String receiveLimitNumber=args[5];
//        String startTime=args[6];
//        String endTime=args[7];
        String description=args[8];
//        String organizationId=args[9];
        String source=args[10];
        String limitTypestr = args[11];
        int LimitType = 1;
        int MinLimitPrice = 0;


        //初始化调用
        CouponTemplate ct=new CouponTemplate();
        ct.setName(nameStr);
        ct.setQuantity(Integer.parseInt(quantityNum));
        ct.setPrice(Integer.parseInt(priceNum));
        ct.setDescription(description);
        ct.setStartTime(new Date());
        ct.setEndTime(DateUtils.offDate(5));
        ct.setReceiveLimitNumber(Integer.parseInt(receiveLimitNumber));
        //ct.setMinLimitPrice(Integer.parseInt(minLimitPrice));
        ct.setOrganizationId(defhospital.getId());
        ct.setSource(Integer.parseInt(source));
        ct.setFromSiteOrgType(1);
        ct.setOperatorId(defaccountId);
        ct.setOwnerId(defaccountId);
        //ct.setLimitType(Integer.parseInt(limitType));

        if(!IsArgsNull(limitTypestr)){
            LimitType = Integer.parseInt(limitTypestr);
            ct.setLimitType(Integer.parseInt(limitTypestr));
        }
        if(!IsArgsNull(min_limit_price)){
            MinLimitPrice = Integer.parseInt(min_limit_price);
            ct.setMinLimitPrice(Integer.parseInt(min_limit_price));
        }


        int availableCreditBalance = 0;//可用的授信额度
        int trade_account_id=0;//发放前客户经理的交易总账户
        int beforeFreezeCreditLimit=0;//创建之前客户经理的冻结额度
//        int beforeBalance=0;//创建之前客户经理的余额
        int beforeCreditLimit =0;//创建之前客户经理的信用额度
        int availableCreditBalance1;//增加信用额度之后的客户经理的信用额度


        if(checkdb){
            //String sql="select ";
            trade_account_id = PayChecker.getTradeAccountIdByAccountId(defaccountId);
            TradeCreditAccount tradeCreditAccount = PayChecker.getTradeCreditAccount(trade_account_id);
            beforeFreezeCreditLimit=tradeCreditAccount.getFreezeCreditLimit();
            availableCreditBalance = tradeCreditAccount.getCreditLimit() - tradeCreditAccount.getFreezeCreditLimit();
            beforeCreditLimit=tradeCreditAccount.getCreditLimit();
            
        }
//        availableCreditBalance1=availableCreditBalance+1000000;

        HttpResult result = httpclient.post(Coupon_NewCouponTemplate, JSON.toJSONString(ct));
        if(availableCreditBalance >= Integer.parseInt(priceNum) * Integer.parseInt(quantityNum)){
            System.out.println("可以创建优惠券");
             log.info("body..."+result.getBody());
            Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);
            Assert.assertNotNull(result.getBody());

//新创建优惠券成功之后，result的值，验证数据库
            if(checkdb){
                /*String sql="select c.* from tb_coupon_template c where c.status=1 and " +
                        "source=0 and c.from_site_org_type=1 and organization_id="+defhospital.getId() + " order by id desc limit 1";*/
                String sql="select c.* from tb_coupon_template c where c.status=1 and " +
                        "source=0 and c.from_site_org_type=1 and organization_id="+defhospital.getId()+ " order by id desc limit 1";
                log.info("sql.."+sql);
                List<CouponTemplate> couponTemplatelist = PayChecker.getCouponTemplateList(sql);//把查询到的结果放到list集合中
                if(couponTemplatelist!=null && couponTemplatelist.size()>0){
                    //查看数据库中的字段是否与新建的优惠券的字段一致
                    CouponTemplate couponTemplate = couponTemplatelist.get(0);
                    Assert.assertEquals(couponTemplate.getName(),nameStr);
                    Assert.assertEquals(couponTemplate.getQuantity(),Integer.parseInt(quantityNum));
                    Assert.assertEquals(couponTemplate.getPrice(),Integer.parseInt(priceNum));
//                    Assert.assertEquals(couponTemplate.getStartTime(),sdf.parse(startTime));
//                    Assert.assertEquals(couponTemplate.getEndTime(),sdf.parse(endTime));
                    Assert.assertEquals(couponTemplate.getDescription(),description);

                    //获取创建之后客户经理的授信账户
                    int owerId=couponTemplatelist.get(0).getOwnerId();
                    trade_account_id=PayChecker.getTradeAccountIdByAccountId(owerId);
                    TradeCreditAccount creditAccount = PayChecker.getTradeCreditAccount(trade_account_id);
                    Assert.assertEquals(creditAccount.getFreezeCreditLimit(),beforeFreezeCreditLimit+Integer.parseInt(priceNum)*Integer.parseInt(quantityNum));
                    Assert.assertEquals(creditAccount.getCreditLimit(),beforeCreditLimit);
//                    Assert.assertEquals(creditAccount.getCreditLimit(),beforeCreditLimit-Integer.parseInt(priceNum)*Integer.parseInt(quantityNum));

                }
            }

        }else{
            System.out.println("授信账户余额不足，不能创建优惠券");
            Assert.assertEquals(result.getCode(), HttpStatus.SC_BAD_REQUEST);
        }
    }
    @DataProvider
    public Iterator<String[]> crmCoupon(){
        try {
            return CvsFileUtil.fromCvsFileToIterator("./csv/coupon/crm/crmCoupon.csv",18);
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
