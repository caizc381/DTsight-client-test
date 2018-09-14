package com.tijiantest.testcase.main.coupon;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jayway.jsonpath.JsonPath;
import com.tijiantest.base.Flag;
import com.tijiantest.base.HttpResult;
import com.tijiantest.base.dbcheck.AccountChecker;
import com.tijiantest.base.dbcheck.CompanyChecker;
import com.tijiantest.base.dbcheck.PayChecker;
import com.tijiantest.model.account.SystemTypeEnum;
import com.tijiantest.model.account.User;
import com.tijiantest.model.coupon.CouponTemplate;
import com.tijiantest.model.coupon.CouponTemplateStatistics;
import com.tijiantest.model.coupon.UserCouponReceive;
import com.tijiantest.model.coupon.UserReceiveStatistics;
import com.tijiantest.testcase.main.MainBase;
import com.tijiantest.util.CvsFileUtil;
import com.tijiantest.util.db.DBMapper;
import com.tijiantest.util.db.SqlException;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 点击领取优惠券
 * 位置：扫优惠券二维码，或者点击优惠券链接
 *
 * @author huifang
 */
public class ReceiveCouponTest extends MainBase {

    @Test(description = "成功领取优惠券",groups = {"qa"},dataProvider = "receiveCoupon")
    public void test_01_receiveCoupon_success(String ...args) throws SqlException, ParseException {
        //STEP1：准备入参手机号
        String mobile = args[1];
        int organizationId = -1;
        boolean isNewUser = true;//判断是否是新用户,默认是新用户
        String templateBatchNum = null;
        //STEP2：准备入参优惠券批次号
        int used_received_num = 0;//初始化用户已经领取的数量
        int template_received_num = 0;//初始化模板已经领取的数量
        if(checkdb){
            String sql = "select c.* from tb_coupon_template c ,tb_coupon_template_statistics s " +
                    "where c.batch_num=s.template_batch_num and c.status = 2 and s.received_num < s.quantity and c.end_time > now() and " +
                    "c.receive_limit_num = -1 ";
            List<CouponTemplate> couponTemplates = PayChecker.getCouponTemplateList(sql);
            if(couponTemplates == null || couponTemplates.size() == 0){
                sql = "select c.* from tb_coupon_template c ,tb_coupon_template_statistics s ,tb_user_receive_statistics t " +
                        "where c.batch_num=s.template_batch_num and c.status = 2 and s.received_num < s.quantity and c.end_time > now() and " +
                        " t.template_batch_num = c.batch_num and t.received_num < t.receive_limit_num and t.mobile = "+mobile;
                couponTemplates = PayChecker.getCouponTemplateList(sql);
                if(couponTemplates == null || couponTemplates.size() == 0){
                    log.error("没有合适的优惠券模板，请重新生成优惠券模板");
                    return;
                }
            }
            templateBatchNum = couponTemplates.get(0).getBatchNum();
            organizationId = couponTemplates.get(0).getOrganizationId();

            //STEP2.1：检测是否是新用户
            User user = AccountChecker.getUserInfo(mobile, SystemTypeEnum.C_LOGIN.getCode());
            if(user != null)
                isNewUser = false;
            List<UserReceiveStatistics> userReceiveStatistics =  PayChecker.getUserReceiveStatistics("mobile",mobile,"organization_id",organizationId+"","template_batch_num","'"+templateBatchNum+"'");
            if(userReceiveStatistics != null && userReceiveStatistics.size() > 0)
                used_received_num = userReceiveStatistics.get(0).getReceiveNum();

            //STEP2.2 检测医院优惠券模板领取数量
            List<CouponTemplateStatistics> couponTemplateStatistics =  PayChecker.getCouponTemplateStatistics("organization_id",organizationId+"","template_batch_num","'"+templateBatchNum+"'");
            if(couponTemplateStatistics != null && couponTemplateStatistics.size()>0)
                template_received_num = couponTemplateStatistics.get(0).getReceivedNum();
        }

        //STEP3:格式化入参
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("hospitalId",organizationId+""));

        JSONObject jo = new JSONObject();
        jo.put("templateBatchNum",templateBatchNum);
        jo.put("mobile",mobile);

        //获取当前时间
        String beforeTime = simplehms.format(DBMapper.query("select now()").get(0).get("now()"));
        waitto(1);

        //STEP4:调用接口
        HttpResult result = httpclient.post(Flag.MAIN,Coupon_ReceiveCoupon,params, JSON.toJSONString(jo));
        //STEP5:验证返回码
        Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);
        log.info("body..."+result.getBody());
        boolean retResult = JsonPath.read(result.getBody(),"$.result");
        Assert.assertTrue(retResult);
        //STEP6:验证DB
        if(checkdb){
            int accountId = 0;
            if(!isNewUser){
                User user = AccountChecker.getUserInfo(mobile, SystemTypeEnum.C_LOGIN.getCode());
                accountId = user.getAccount_id();
                }

            //2.用户优惠券领取表插入1条记录
            List<UserCouponReceive> couponReceives = PayChecker.getUserCouponList("select * from tb_user_coupon_receive_record where template_batch_num = '"+templateBatchNum+"' " +
                    "and organization_id = "+organizationId + " and mobile = '"+mobile + "' order by id desc limit 1");

            Assert.assertEquals(couponReceives.size(),1);
            UserCouponReceive couponReceive = couponReceives.get(0);
            Assert.assertEquals(couponReceive.getMobile(),mobile);
            if(isNewUser)
                Assert.assertNull(couponReceive.getAccountId());
            else
                Assert.assertEquals(couponReceive.getAccountId().intValue(),accountId);
            Assert.assertEquals(couponReceive.getStatus().intValue(),0);//未使用
            List<CouponTemplate> couponTemplates = PayChecker.getCouponTemplateList("batch_num","'"+templateBatchNum+"'","is_deleted","0");
            Assert.assertEquals(couponReceive.getEndTime(),couponTemplates.get(0).getEndTime());
            Assert.assertEquals(couponReceive.getIsDeleted(),0);
            Assert.assertEquals(couponReceive.getGmtCreated().compareTo(simplehms.parse(beforeTime)),1);//创建时间在开始时间之后，表示这条记录是新插入的

            //3.用户优惠券统计表更新数据(received_num+1)
            List<UserReceiveStatistics> userReceiveStatistics =  PayChecker.getUserReceiveStatistics("mobile","'"+mobile+"'","organization_id",organizationId+"","template_batch_num","'"+templateBatchNum+"'");
            Assert.assertEquals(userReceiveStatistics.size(),1);
            if(isNewUser)
                Assert.assertNull(userReceiveStatistics.get(0).getAccountId());
            else
                Assert.assertEquals(userReceiveStatistics.get(0).getAccountId().intValue(),accountId);
            Assert.assertEquals(used_received_num+1,userReceiveStatistics.get(0).getReceiveNum());

            //4.机构优惠券模板统计表更新数据(received_num+1)
            List<CouponTemplateStatistics> couponTemplateStatistics =  PayChecker.getCouponTemplateStatistics("organization_id",organizationId+"","template_batch_num","'"+templateBatchNum+"'");
            Assert.assertEquals(template_received_num+1,couponTemplateStatistics.get(0).getReceivedNum());

        }
    }

    @DataProvider
    public Iterator<String[]> receiveCoupon() {
        try {
            return CvsFileUtil.fromCvsFileToIterator("./csv/coupon/main/receiveCoupon.csv", 10);
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


