package com.tijiantest.testcase.ops.coupon;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jayway.jsonpath.JsonPath;
import com.tijiantest.base.Flag;
import com.tijiantest.base.HttpResult;
import com.tijiantest.base.dbcheck.PayChecker;
import com.tijiantest.model.coupon.CouponTemplate;
import com.tijiantest.model.coupon.FindCouponsVO;
import com.tijiantest.model.hospital.HospitalQueryDto;
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
 * 优惠券模板列表查询
 *
 * 位置：OPS->营销->优惠券
 *
 * 操作:查看医院优惠券模板
 *
 * @author huifang
 */
public class FindCouponTemplateTest extends OpsBase {

    @Test(description = "查询优惠券模板",groups = {"qa"},dataProvider = "findCoupons")
    public void test_01_findCouponTemplates(String ...args) throws SqlException, ParseException {
        //STEP1:入参文件分割参数
        String organizationStr = args[1];
        String searchKeyStr = args[2];
        String rowCountStr = args[3];
        String currentPageStr = args[4];
        String pageSizeStr = args[5];
        String orgType = args[6];
        String provinceIdStr = args[7];
        String cityIdStr = args[8];
        String distrinctIdStr = args[9];
        int organizationId = -1;
        int rowCount = -1;
        int currentPage = -1;
        int pageSize = -1;
        int provinceId = -1;
        int districtId = -1;
        int cityId = -1;
        //STEP2:格式化接口参数
        FindCouponsVO vo = new FindCouponsVO();
        if(!IsArgsNull(searchKeyStr)){
            if(searchKeyStr.equals("BATCHNUM")){
                if(checkdb){//随机选1个医院的优惠券批次号
                    String sql = "select * from tb_coupon_template where   is_deleted = 0 order by id desc ";
                    List<CouponTemplate> couponTemplateList =  PayChecker.getCouponTemplateList(sql);
                    if(couponTemplateList!=null && couponTemplateList.size() > 0){
                        searchKeyStr = couponTemplateList.get(0).getBatchNum();
                    }
                }
            }
            vo.setSearchKey(searchKeyStr);
        }
        Page page = new Page();
        if(!IsArgsNull(rowCountStr)){
            rowCount = Integer.parseInt(rowCountStr);
            page.setRowCount(Integer.parseInt(rowCountStr));
        }

        if(!IsArgsNull(currentPageStr)){
            currentPage = Integer.parseInt(currentPageStr);
            page.setCurrentPage(Integer.parseInt(currentPageStr));
        }

        if(!IsArgsNull(pageSizeStr)){
            pageSize = Integer.parseInt(pageSizeStr);
            page.setPageSize(Integer.parseInt(pageSizeStr));
        }

        if(!IsArgsNull(organizationStr)){
            organizationId = Integer.parseInt(organizationStr);
            vo.setOrganizationId(organizationStr);
        }

        HospitalQueryDto hq = new HospitalQueryDto();
        hq.setOrgType(Integer.parseInt(orgType));
        if(!IsArgsNull(provinceIdStr)){
            provinceId = Integer.parseInt(provinceIdStr);
            hq.setProvinceId(provinceId);
            if(!IsArgsNull(cityIdStr)){
                cityId = Integer.parseInt(cityIdStr);
                hq.setCityId(cityId);
                if(!IsArgsNull(distrinctIdStr)){
                    districtId = Integer.parseInt(distrinctIdStr);
                    hq.setDistrictId(districtId);
                }
            }
        }
        vo.setHospitalQueryDto(hq);

        vo.setPage(page);

        //STEP3:调用接口
        HttpResult result = httpclient.post(Flag.OPS,Coupon_FindCouponTemplates, JSON.toJSONString(vo));
        log.info(result.getBody());
        //STEP4:验证接口返回 & DB
        Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);

        Page retPage = JSONObject.parseObject(JsonPath.read(result.getBody(),"$.page").toString(),Page.class);
        List<CouponTemplate> retCouponTemplateList = JSONArray.parseArray(JsonPath.read(result.getBody(),"$.records").toString(),CouponTemplate.class);

        if(checkdb){
            String sql = "select c.* ,s.received_num ,s.used_num from tb_coupon_template c , tb_coupon_template_statistics s where c.batch_num = s.template_batch_num and  c.is_deleted = 0 ";
            if(!IsArgsNull(searchKeyStr))
                sql += " and ( c.batch_num = '"+searchKeyStr+"' or c.owner_name = '"+searchKeyStr+"' or c.name = '"+searchKeyStr+"' )";
            if(organizationId !=-1)
                sql += " and c.organization_id = "+organizationId;
            if(districtId != -1)
                sql += " and c.organization_id in (select h.id from  tb_hospital h where  h.address_id  = "+districtId+")";
            else if(cityId != -1)
                sql += " and c.organization_id in (select h.id from  tb_hospital h where  h.address_id  like '"+cityId/100+"%')";
            else if(provinceId != -1)
                sql += " and c.organization_id in (select h.id from  tb_hospital h where  h.address_id  like '"+provinceId/10000+"%')";
            sql += "  and c.organization_id !=-1  order by c.batch_num desc ";
            log.info("sql..."+sql);
            List<CouponTemplate> dbCouponTemplateList = PayChecker.getCouponTemplateList(sql);
            //总数量正确
//            Assert.assertEquals(dbCouponTemplateList.size(),retPage.getRowCount());
            //默认只查询第一页的
            if(pageSize != -1)
                sql += " limit "+pageSize;
            dbCouponTemplateList = PayChecker.getCouponTemplateList(sql);//应用分页再次查询DB
            Assert.assertEquals(retCouponTemplateList.size(),dbCouponTemplateList.size());
            for(int i=0;i<dbCouponTemplateList.size();i++) {
                Assert.assertEquals(retCouponTemplateList.get(i).getBatchNum(), dbCouponTemplateList.get(i).getBatchNum());
                Assert.assertEquals(retCouponTemplateList.get(i).getName(), dbCouponTemplateList.get(i).getName());
                Assert.assertEquals(retCouponTemplateList.get(i).getOwnerId(), dbCouponTemplateList.get(i).getOwnerId());
                Assert.assertEquals(retCouponTemplateList.get(i).getOwnerName(), dbCouponTemplateList.get(i).getOwnerName());
                Assert.assertEquals(retCouponTemplateList.get(i).getPrice(), dbCouponTemplateList.get(i).getPrice());//单个优惠券模板面值
                Assert.assertEquals(retCouponTemplateList.get(i).getMinLimitPrice(), dbCouponTemplateList.get(i).getMinLimitPrice());//最低限额
                Assert.assertEquals(retCouponTemplateList.get(i).getReceiveLimitNumber(), dbCouponTemplateList.get(i).getReceiveLimitNumber());//限制领取次数
                Assert.assertEquals(retCouponTemplateList.get(i).getQuantity(), dbCouponTemplateList.get(i).getQuantity());//发放数量
                Assert.assertEquals(retCouponTemplateList.get(i).getStatus(), dbCouponTemplateList.get(i).getStatus());//状态
                Assert.assertEquals(retCouponTemplateList.get(i).getStartTime(), dbCouponTemplateList.get(i).getStartTime());
                Assert.assertEquals(sdf.format(retCouponTemplateList.get(i).getEndTime()), sdf.format(dbCouponTemplateList.get(i).getEndTime()));
                Assert.assertEquals(retCouponTemplateList.get(i).getOrganizationId(), dbCouponTemplateList.get(i).getOrganizationId());
                Assert.assertEquals(retCouponTemplateList.get(i).getSource(), dbCouponTemplateList.get(i).getSource());
                Assert.assertEquals(retCouponTemplateList.get(i).isRecovery(), dbCouponTemplateList.get(i).isRecovery());
                Assert.assertEquals(retCouponTemplateList.get(i).getReceivedNumber(), dbCouponTemplateList.get(i).getReceivedNumber());//已经领取数量
                Assert.assertEquals(retCouponTemplateList.get(i).getUsedNumber(), dbCouponTemplateList.get(i).getUsedNumber());//已使用数量

            }

        }
    }

    @DataProvider
    public Iterator<String[]> findCoupons(){
        try {
            return CvsFileUtil.fromCvsFileToIterator("./csv/coupon/ops/findCoupons.csv",18);
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
