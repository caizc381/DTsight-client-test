package com.tijiantest.testcase.main.coupon;

import com.alibaba.fastjson.JSONObject;
import com.jayway.jsonpath.JsonPath;
import com.tijiantest.base.Flag;
import com.tijiantest.base.HttpResult;
import com.tijiantest.base.dbcheck.PayChecker;
import com.tijiantest.base.dbcheck.ResourceChecker;
import com.tijiantest.model.coupon.CouponTemplate;
import com.tijiantest.testcase.main.MainBaseNoLogin;
import com.tijiantest.util.db.DBMapper;
import com.tijiantest.util.db.SqlException;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 判断优惠券链接是否生效
 */
public class LinkStatusTest extends MainBaseNoLogin {

    @Test(description = "优惠券模板正常可用，链接生效",groups = {"qa"})
    public void test_01_linkStatus_success() throws SqlException, ParseException {
        int hospitalId = 0;
        String templateBatchNum = null;
        if(checkdb){
            //随机从数据库查询1条可用于领取的优惠券模板（不限制机构ID）
            String sql = "select c.* from tb_coupon_template c ,tb_coupon_template_statistics s where c.batch_num = s.template_batch_num and s.received_num < s.quantity and c.status = 2";
            List<CouponTemplate>  couponTemplateList = PayChecker.getCouponTemplateList(sql);
            if(couponTemplateList != null && couponTemplateList.size() > 0){
                templateBatchNum = couponTemplateList.get(0).getBatchNum();
                hospitalId = couponTemplateList.get(0).getOrganizationId();
            }
        }

        if(templateBatchNum == null){
            log.error("本数据库中没有可用于正常领取的优惠券了，本用例无法执行");
            return;
        }

        //STEP1:入参
        List<NameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair("templateBatchNum",templateBatchNum));

        //STEP2:调用接口
        HttpResult result = hc1.get(Flag.MAIN,Coupon_LinkStatus,pairs);

        //STEP3:返回值验证
        Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);
        boolean retResult = JsonPath.read(result.getBody(),"$.result");
        int linkStatus = JsonPath.read(result.getBody(),"$.linkStatus");
        String imageUrl = JsonPath.read(result.getBody(),"$.imageUrl");
        log.info("body..."+result.getBody());
        Assert.assertFalse(retResult);
        String sql = "select * from tb_site_resource where hospital_id = "+hospitalId+" and type = 1";
        log.info(sql);
        List<Map<String,Object>> dblist = DBMapper.query(sql);
        Assert.assertEquals(dblist.size(),1);
        Assert.assertEquals(imageUrl,dblist.get(0).get("value"));

    }


    @Test(description = "优惠券模板已停用，链接失效",groups = {"qa"})
    public void test_02_linkStatus_stop() throws SqlException, ParseException {
        int hospitalId = 0;
        String templateBatchNum = null;
        if(checkdb){
            //随机从数据库查询1条已停用的的优惠券模板（不限制机构ID）
            String sql = "select c.* from tb_coupon_template c ,tb_coupon_template_statistics s where c.batch_num = s.template_batch_num and  c.status = 3";
            List<CouponTemplate>  couponTemplateList = PayChecker.getCouponTemplateList(sql);
            if(couponTemplateList != null && couponTemplateList.size() > 0){
                templateBatchNum = couponTemplateList.get(0).getBatchNum();
                hospitalId = couponTemplateList.get(0).getOrganizationId();
            }
        }

        if(templateBatchNum == null){
            log.error("本数据库中没有已停用的的优惠券了，本用例无法执行");
            return;
        }

        //STEP1:入参
        List<NameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair("templateBatchNum",templateBatchNum));

        //STEP2:调用接口
        HttpResult result = hc1.get(Flag.MAIN,Coupon_LinkStatus,pairs);

        //STEP3:返回值验证
        Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);
        boolean retResult = JsonPath.read(result.getBody(),"$.result");
        int linkStatus = JsonPath.read(result.getBody(),"$.linkStatus");
        log.info(result.getBody());
        Assert.assertFalse(retResult);
        //链接失效
        Assert.assertEquals(linkStatus,0);

    }


       @Test(description = "优惠券模板已过期，链接失效",groups = {"qa"})
    public void test_03_linkStatus_expire() throws SqlException, ParseException {
        int hospitalId = 0;
        String templateBatchNum = null;
        if(checkdb){
            //随机从数据库查询1条已过期的的优惠券模板（不限制机构ID）
            String sql = "select c.* from tb_coupon_template c ,tb_coupon_template_statistics s where c.batch_num = s.template_batch_num and  c.status = 2 and c.end_time < now()";
            List<CouponTemplate>  couponTemplateList = PayChecker.getCouponTemplateList(sql);
            if(couponTemplateList != null && couponTemplateList.size() > 0){
                templateBatchNum = couponTemplateList.get(0).getBatchNum();
                hospitalId = couponTemplateList.get(0).getOrganizationId();
            }
        }

        if(templateBatchNum == null){
            log.error("本数据库中没有已过期的的优惠券了，本用例无法执行");
            return;
        }

        //STEP1:入参
        List<NameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair("templateBatchNum",templateBatchNum));

        //STEP2:调用接口
        HttpResult result = hc1.get(Flag.MAIN,Coupon_LinkStatus,pairs);

        //STEP3:返回值验证
        Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);
        log.info(result.getBody());
        boolean retResult = JsonPath.read(result.getBody(),"$.result");
        int linkStatus = JsonPath.read(result.getBody(),"$.linkStatus");
        Assert.assertFalse(retResult);
        //链接失效(过期失效)
        Assert.assertEquals(linkStatus,0);

    }


    @Test(description = "优惠券模板已全部领完，链接无法再领取",groups = {"qa"})
    public void test_04_linkStatus_over() throws SqlException, ParseException {
        String templateBatchNum = null;
        if(checkdb){
            //随机从数据库查询1条已全部被领取完毕的优惠券模板（不限制机构ID）
            String sql = "select c.* from tb_coupon_template c ,tb_coupon_template_statistics s where c.batch_num = s.template_batch_num and s.received_num = s.quantity  and c.end_time > now() and c.organization_id >0 and c.status = 2";
            List<CouponTemplate>  couponTemplateList = PayChecker.getCouponTemplateList(sql);
            if(couponTemplateList != null && couponTemplateList.size() > 0){
                templateBatchNum = couponTemplateList.get(0).getBatchNum();
            }
        }

        if(templateBatchNum == null){
            log.error("本数据库中没有已全部领完的的优惠券了，本用例无法执行");
            return;
        }

        //STEP1:入参
        List<NameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair("templateBatchNum",templateBatchNum));

        //STEP2:调用接口
        HttpResult result = hc1.get(Flag.MAIN,Coupon_LinkStatus,pairs);

        //STEP3:返回值验证
        Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);
        log.info(result.getBody());
        boolean retResult = JsonPath.read(result.getBody(),"$.result");
        int linkStatus = JsonPath.read(result.getBody(),"$.linkStatus");
        Assert.assertFalse(retResult);
        //优惠券领取完毕
        Assert.assertEquals(linkStatus,1);

    }

}
