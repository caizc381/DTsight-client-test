package com.tijiantest.testcase.crm.coupon;

import com.tijiantest.base.HttpResult;
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

/**
 * 下载二维码
 *
 * 位置：维护后台->营销->优惠券管理->推广（下载二维码)
 *
 * @author huifang
 */
public class DownloadQRCodeTest extends CrmBase {


    @Test(description = "下载二维码",groups = {"qa"})
    public void test_01_downLoadQRCode() throws SqlException {
        String batchNum = null;
        //STEP1: 准备数据
        if(checkdb){
            String sql = "select * from tb_coupon_template where organization_id = "+defhospital.getId()+" and from_site_org_type = 1 and  is_deleted = 0 and status = 2";
            List<Map<String,Object>> list = DBMapper.query(sql);
            if(list != null && list.size()>0)
                batchNum = list.get(0).get("batch_num").toString();
        }

        if(batchNum == null){
            log.error("没有找到合适的优惠券批次，需要手动创建");
            return;
        }
        //STEP2: 入参格式化
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("organizationId",defhospital.getId()+""));
        params.add(new BasicNameValuePair("batchNum",batchNum));

        //STEP3: 调用http请求
        HttpResult result = httpclient.get(Coupon_DownloadQRCode,params);

        //STEP4:验证
        Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);
        Assert.assertNotNull(result.getBody());



    }
}
