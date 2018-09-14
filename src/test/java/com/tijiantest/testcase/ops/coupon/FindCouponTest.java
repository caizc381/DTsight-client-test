package com.tijiantest.testcase.ops.coupon;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jayway.jsonpath.JsonPath;
import com.tijiantest.base.Flag;
import com.tijiantest.base.HttpResult;
import com.tijiantest.base.dbcheck.PayChecker;
import com.tijiantest.model.coupon.CouponTemplate;
import com.tijiantest.model.coupon.ReceiveCouponsVO;
import com.tijiantest.model.coupon.UserCouponReceive;
import com.tijiantest.testcase.crm.CrmBase;
import com.tijiantest.testcase.ops.OpsBase;
import com.tijiantest.util.CvsFileUtil;
import com.tijiantest.util.db.SqlException;
import com.tijiantest.util.pagination.Page;
import org.apache.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.Iterator;
import java.util.List;

/**
 * 查看该优惠券的领取情况
 *
 * 位置：OPS->营销->优惠券
 *
 * 操作：查看后进入的页面
 */
public class FindCouponTest extends OpsBase {

    @Test(description = "查看优惠券的领取情况",groups = {"qa"},dataProvider = "findCoupon")
    public void test_01_findCoupon(String ...args) throws SqlException, ParseException {
        String batchNum = null;
        //STEP1:提取有效的优惠券模板批次
        if(checkdb){
            //随机从数据库查询1条已经有领取的优惠券模板
            String sql = "select c.* from tb_coupon_template c, tb_coupon_template_statistics s  where c.batch_num = s.template_batch_num and  c.status =2  and s.received_num >0 and c.is_deleted = 0 ";
            List<CouponTemplate> couponTemplateList = PayChecker.getCouponTemplateList(sql);
            if(couponTemplateList != null && couponTemplateList.size() > 0){
                int index = (int)(Math.random() * couponTemplateList.size());
                batchNum = couponTemplateList.get(index).getBatchNum();
            }
        }
        if(batchNum == null){
            log.error("所有医院中没有已经领取过且还在发放状态的优惠券版本，本用例无法执行");
            return;
        }

        //STEP2:从数据文件分割入参
        String statusStr = args[2];
        String searchKey = args[3];
        String rowCountStr =  args[4];
        String currentPageStr = args[5];
        String pageSizeStr = args[6];
        int status = -1;
        int rowCount = -1;
        int currentPage = -1;
        int pageSize = -1;

        //STEP3:格式化接口传参
        ReceiveCouponsVO vo = new ReceiveCouponsVO();
        vo.setBatchNum(batchNum);//模板批次号

        if(!IsArgsNull(statusStr)){
            status = Integer.parseInt(statusStr);
            vo.setStatus(statusStr);//批次状态(未使用|已使用|已过期)
        }

        Page page = new Page();
        if(!IsArgsNull(rowCountStr)){
            rowCount = Integer.parseInt(rowCountStr);
            page.setRowCount(rowCount);
        }

        if(!IsArgsNull(currentPageStr)){
            currentPage = Integer.parseInt(currentPageStr);
            page.setCurrentPage(currentPage);
        }

        if(!IsArgsNull(pageSizeStr)){
            pageSize = Integer.parseInt(pageSizeStr);
            page.setPageSize(pageSize);
        }
        vo.setPage(page);//分页

        if(!IsArgsNull(searchKey)){
            vo.setSearchKey(searchKey);//搜索关键字
        }

        //STEP4:调用接口
        HttpResult result = httpclient.post(Flag.OPS,Coupon_FindCoupon, JSON.toJSONString(vo));
        //STEP5:验证返回值&DB
        Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);
        log.info("body..."+result.getBody());
        Page retPage = JSONObject.parseObject(JsonPath.read(result.getBody(),"$.page").toString(),Page.class);
        List<UserCouponReceive> retCouponTemplateList = JSONArray.parseArray(JsonPath.read(result.getBody(),"$.records").toString(),UserCouponReceive.class);

        if(checkdb){
            //查询模板批次的领取情况
            String sql = "select * from tb_user_coupon_receive_record where template_batch_num = '"+batchNum+"' and is_deleted = 0 ";
            if(status != -1)//状态涮选
                sql += " and status  = "+status;
            if(!IsArgsNull(searchKey)){//搜索关键字涮选
                sql += " and ( mobile = '"+searchKey+"' or idcard = '"+searchKey+"' or account_name = '"+searchKey+"')";
            }
            sql += " order by gmt_created desc ";//倒序排列
            List<UserCouponReceive>  dbUserCouponReceiveList = PayChecker.getUserCouponList(sql);
            Assert.assertEquals(retPage.getRowCount(),dbUserCouponReceiveList.size());
            if(pageSize != -1)
                sql += " limit "+pageSize;
            dbUserCouponReceiveList = PayChecker.getUserCouponList(sql);//分页条件后再查询一次
            Assert.assertEquals(retCouponTemplateList.size(),dbUserCouponReceiveList.size());
            for(int i=0;i<dbUserCouponReceiveList.size();i++){
                Assert.assertEquals(retCouponTemplateList.get(i).getAccountName(),dbUserCouponReceiveList.get(i).getAccountName());
                Assert.assertEquals(retCouponTemplateList.get(i).getMobile(),dbUserCouponReceiveList.get(i).getMobile());
                Assert.assertEquals(retCouponTemplateList.get(i).getIdCard(),dbUserCouponReceiveList.get(i).getIdCard());
                Assert.assertEquals(retCouponTemplateList.get(i).getStatus(),dbUserCouponReceiveList.get(i).getStatus());
                Assert.assertEquals(retCouponTemplateList.get(i).getId(),dbUserCouponReceiveList.get(i).getId());
                Assert.assertEquals(retCouponTemplateList.get(i).getReceiveTime(),dbUserCouponReceiveList.get(i).getReceiveTime());
                Assert.assertEquals(retCouponTemplateList.get(i).getUseTime(),dbUserCouponReceiveList.get(i).getUseTime());
            }
        }



    }

    @DataProvider
    public Iterator<String[]> findCoupon(){
        try {
            return CvsFileUtil.fromCvsFileToIterator("./csv/coupon/ops/findCoupon.csv",18);
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
