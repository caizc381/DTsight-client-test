package com.tijiantest.testcase.ops.coupon;

import com.alibaba.fastjson.JSON;
import com.tijiantest.base.Flag;
import com.tijiantest.base.HttpResult;
import com.tijiantest.base.dbcheck.PayChecker;
import com.tijiantest.model.coupon.CouponTemplate;
import com.tijiantest.model.coupon.TradeCreditAccount;
import com.tijiantest.testcase.ops.OpsBase;
import com.tijiantest.util.CvsFileUtil;
import com.tijiantest.util.DateUtils;
import com.tijiantest.util.db.SqlException;
import org.apache.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import sun.net.www.http.HttpClient;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;


/*@author:qfm
*ops新建优惠券
*位置：OPS后台--营销--医院优惠券--新建优惠券
*
*/

public class NewCouponTemplateTest extends OpsBase{
    @Test(description = "ops新建优惠券模板",groups = "qa",dataProvider = "newCoupon")
    public void test_01newCouponTemplates(String...args) throws SqlException, ParseException {
        //Step1:从数据文件中取值
        String name=args[1];
        String quantity=args[2];
        String price=args[3];
        String min_limit_price=args[4];
        String receive_limit_num=args[6];
        String description=args[9];
        String source=args[10];
        String ower_id=args[11];
        String limitTypestr = args[12];
        int LimitType = 1;
        int MinLimitPrice = 0;

        //step2：初始化调用数据
        CouponTemplate ct=new CouponTemplate();
        ct.setName(name);
        ct.setQuantity(Integer.parseInt(quantity));
        ct.setPrice(Integer.parseInt(price));
        //ct.setMinLimitPrice(Integer.parseInt(min_limit_price));
        ct.setOrganizationId(defSettHospitalId);
        ct.setOwnerId(Integer.parseInt(ower_id));
        ct.setReceiveLimitNumber(Integer.parseInt(receive_limit_num));
        ct.setSource(Integer.parseInt(source));
        ct.setStatus(0);
        ct.setFromSiteOrgType(1);
        ct.setStartTime(new Date());
        ct.setEndTime(DateUtils.offDate(5));
        //ct.setLimitType(1);
        ct.setDescription(description);

        if(!IsArgsNull(min_limit_price)){
            MinLimitPrice = Integer.parseInt(min_limit_price);
            ct.setMinLimitPrice(Integer.parseInt(min_limit_price));
        }

        if(!IsArgsNull(limitTypestr)){
            LimitType = Integer.parseInt(limitTypestr);
            ct.setLimitType(Integer.parseInt(limitTypestr));
        }


        int availableCreditBalance = 0;//可用的授信额度
        int trade_account_id = 0;//发放前客户经理的交易总账户
        int beforeFreezeCreitLimit=0;//创建之前客户经理的冻结额度
        int beforeCreditLimit = 0;//创建之前客户经理的信用额度


        //随机选择一个体检机构
      if(checkdb){
          String sql="select * from tb_coupon_template where organization_id= "+ defSettHospitalId+" order by id desc limit 1";

          List<CouponTemplate> couponTemplateList=PayChecker.getCouponTemplateList(sql);
          if(couponTemplateList!=null && couponTemplateList.size()>0){
              //选择体检机构中的任一一个客户经理
              int owerId=couponTemplateList.get(0).getOwnerId();
              trade_account_id=PayChecker.getTradeAccountIdByAccountId(owerId);
              TradeCreditAccount tradeCreditAccount=PayChecker.getTradeCreditAccount(trade_account_id);

              //创建之前的客户经理的账户信息
               beforeCreditLimit=tradeCreditAccount.getCreditLimit();
               beforeFreezeCreitLimit=tradeCreditAccount.getFreezeCreditLimit();
               availableCreditBalance=tradeCreditAccount.getCreditLimit()-tradeCreditAccount.getFreezeCreditLimit();
          }
          //调用接口,验证创建成功后的返回码
          HttpResult result= httpclient.post(Flag.OPS,Coupon_NewCouponTemplate, JSON.toJSONString(ct));
          if(availableCreditBalance>=Integer.parseInt(price)*Integer.parseInt(quantity)){
              System.out.println("可以创建优惠券");
              Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);
              Assert.assertNotNull(result.getBody());



              //从创建的优惠券模板中取一条数据，并将接口返回的数据与数据库中的数据比对
              if(checkdb){
                  String sql1="select c.* from tb_coupon_template c where c.status=1 and " +
                          "source=1 and c.from_site_org_type=1 and organization_id="+defSettHospitalId + " order by id desc limit 1";
                  try {
                      List<CouponTemplate> couponTemplateList1=PayChecker.getCouponTemplateList(sql1);
                      if(couponTemplateList1!=null && couponTemplateList1.size()>0){
                          CouponTemplate couponTemplate=couponTemplateList1.get(0);
                          Assert.assertEquals(couponTemplate.getName(),name);
                          Assert.assertEquals(couponTemplate.getQuantity(),Integer.parseInt(quantity));
                          Assert.assertEquals(couponTemplate.getPrice(),Integer.parseInt(price));
                          //Assert.assertEquals(couponTemplate.getMinLimitPrice(),Integer.parseInt(min_limit_price));
                          Assert.assertEquals(couponTemplate.getReceiveLimitNumber(),Integer.parseInt(receive_limit_num));
                          Assert.assertEquals(couponTemplate.getDescription(),description);

                          int ower_Id=couponTemplateList1.get(0).getOwnerId();
                          trade_account_id=PayChecker.getTradeAccountIdByAccountId(ower_Id);
                          TradeCreditAccount creditAccount = PayChecker.getTradeCreditAccount(trade_account_id);
                          Assert.assertEquals(creditAccount.getFreezeCreditLimit(),beforeFreezeCreitLimit+Integer.parseInt(price)*Integer.parseInt(quantity));
                          Assert.assertEquals(creditAccount.getCreditLimit(),beforeCreditLimit);

                      }
                  } catch (ParseException e) {
                      e.printStackTrace();
                  }
              }
          }else{
              System.out.println("授信账户余额不足，创建优惠券失败");
          }
      }
    }

    @DataProvider
    public Iterator<String[]> newCoupon(){
        try {
            return CvsFileUtil.fromCvsFileToIterator("./csv/coupon/ops/newCoupon.csv",18);
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
