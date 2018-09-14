package com.tijiantest.testcase.crm.coupon;

import com.alibaba.fastjson.JSONObject;
import com.jayway.jsonpath.JsonPath;
import com.tijiantest.base.HttpResult;
import com.tijiantest.base.dbcheck.PayChecker;
import com.tijiantest.model.coupon.CouponTemplate;
import com.tijiantest.testcase.crm.CrmBase;
import com.tijiantest.util.db.SqlException;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/***
 * 点击编辑按钮时调用
 *
 * 位置：CRM->维护后台->营销->优惠券
 *
 * 操作:查看优惠券模板
 *
 * @author huifang
 */
public class WatchCouponTemplateTest extends CrmBase {

    @Test(description = "查看优惠券模板",groups = {"qa"})
    public void test_01_watchCouponTemplate() throws SqlException, ParseException {
        String batchNum = null;
        int hospitalid = defhospital.getId();
        //STEP1:提取模板批次号
        if(checkdb){
            //随机从数据库查询1条可用于领取的优惠券模板
            String sql = "select c.* from tb_coupon_template c  where  is_deleted = 0 and organization_id = "+hospitalid;
            List<CouponTemplate> couponTemplateList = PayChecker.getCouponTemplateList(sql);
            if(couponTemplateList != null && couponTemplateList.size() > 0){
                int index=(int)(Math.random()*couponTemplateList.size());
                batchNum = couponTemplateList.get(index).getBatchNum();
            }
        }
        if(batchNum == null){
            log.error("医院"+hospitalid+"中没有可用于查看的优惠券模板，本用例无法执行");
            return;
        }
        //STEP2:接口入参格式化
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("batchNum",batchNum));
        //STEP3:调用接口
        HttpResult result = httpclient.get(Coupon_WatchCouponTemplate,params);
        //STEP4:验证接口返回&DB
        log.info("body.."+result.getBody());
        Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);
        CouponTemplate retCouponTemplate = JSONObject.parseObject(result.getBody(),CouponTemplate.class);
        if(checkdb){
            List<CouponTemplate> couponTemplateList = PayChecker.getCouponTemplateList("batch_num","'"+batchNum+"'","is_deleted","0");
            Assert.assertEquals(couponTemplateList.size(),1);
            CouponTemplate dbCouponTemplate = couponTemplateList.get(0);
            Assert.assertEquals(retCouponTemplate.getName(),dbCouponTemplate.getName());
            Assert.assertEquals(retCouponTemplate.getQuantity(),dbCouponTemplate.getQuantity());
            Assert.assertEquals(retCouponTemplate.getPrice(),dbCouponTemplate.getPrice());
            Assert.assertEquals(retCouponTemplate.getDescription(),dbCouponTemplate.getDescription());
            Assert.assertEquals(retCouponTemplate.getStartTime(),dbCouponTemplate.getStartTime());
            Assert.assertEquals(retCouponTemplate.getEndTime(),dbCouponTemplate.getEndTime());
            Assert.assertEquals(retCouponTemplate.getReceiveLimitNumber(),dbCouponTemplate.getReceiveLimitNumber());
            Assert.assertEquals(retCouponTemplate.getMinLimitPrice(),dbCouponTemplate.getMinLimitPrice());
            Assert.assertEquals(retCouponTemplate.getOwnerId(),dbCouponTemplate.getOwnerId());
            Assert.assertEquals(retCouponTemplate.getOwnerName(),dbCouponTemplate.getOwnerName());
            Assert.assertEquals(retCouponTemplate.getOrganizationId(),dbCouponTemplate.getOrganizationId());
            Assert.assertEquals(retCouponTemplate.getSource(),dbCouponTemplate.getSource());
            Assert.assertEquals(retCouponTemplate.getStatus(),dbCouponTemplate.getStatus());
        }
    }
}
