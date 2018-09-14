package com.tijiantest.testcase.crm.coupon;

import com.jayway.jsonpath.JsonPath;
import com.tijiantest.base.HttpResult;
import com.tijiantest.model.coupon.CouponTemplate;
import com.tijiantest.testcase.crm.CrmBase;
import com.tijiantest.util.db.DBMapper;
import com.tijiantest.util.db.SqlException;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
/*
* @author:qfm
* 优惠券模板推广
* 位置：crm维护后台--营销--优惠券--操作功能（推广）
*
*
* */

public class SpreadCouponTest extends CrmBase{
    @Test(description = "优惠券模板推广",groups = "qa")
    public void test_03spreadCoupon() throws SqlException {
        String batchNum=null;
        String qRCode = null;
        String organizationId;
        //step1：查询数据库数据
        if(checkdb){
            String sql="select * from tb_coupon_template where organization_id="+defhospital.getId()+
                    " and status=2 and from_site_org_type=1 and is_deleted=0";
            List<Map<String,Object>> list= DBMapper.query(sql);
            if(list!=null && list.size()>0){
                batchNum=list.get(0).get("batch_num").toString();
//                organizationId=list.get(0).get("organization").toString();
                int organizaitionId = Integer.parseInt(list.get(0).get("organization_id").toString());

            }
            if (batchNum==null){
                log.error("没有可以推广的优惠券模板");
                return;
            }
            //step2:入参格式化  NameValuePair
            List<NameValuePair> params=new ArrayList<>();
            params.add(new BasicNameValuePair("organizaitionId",defhospital.getId()+""));
            params.add(new BasicNameValuePair("batchNum",batchNum));
            //调用接口
            HttpResult result=httpclient.get(Coupon_SpreadCoupon, params);
//          Assert.assertEquals(result.getCode(),HttpStatus.SC_OK);
            //拼url地址
            String url="batchNum='"+batchNum+"'&organizaitionId="+defhospital.getId();
            log.info("body.."+result.getBody());
            String body=result.getBody();
            //json里面有URL和qRcode
            String retUrl = JsonPath.read(body,"$.url");
//            String retQrCode = JsonPath.read(body,"$.qRcode");
          //  Assert.assertTrue(retUrl.contains(url));
            //返回的二维码不为空
//            Assert.assertNotNull(retQrCode);

        }
    }

}
