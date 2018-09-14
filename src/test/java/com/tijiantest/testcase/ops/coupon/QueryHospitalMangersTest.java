package com.tijiantest.testcase.ops.coupon;

import com.alibaba.fastjson.JSONArray;
import com.tijiantest.base.Flag;
import com.tijiantest.base.HttpResult;
import com.tijiantest.base.dbcheck.AccountChecker;
import com.tijiantest.model.account.Account;
import com.tijiantest.model.coupon.QueryManagers;
import com.tijiantest.testcase.ops.OpsBase;
import com.tijiantest.util.CvsFileUtil;
import com.tijiantest.util.db.SqlException;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.*;
/*
* @author qfm
* 获取客户经理
* 位置：ops运维后台--营销--新建优惠券选择体检中心后选择客户经理
* */


//创建优惠券时,选择体检中心后，获取体检中心下的客户经理
public class QueryHospitalMangersTest extends OpsBase{
    @Test(description = "ops获取医院客户经理",groups = "qa",dataProvider = "queryManager")
    public void test_04QueryHospitalMangers(String... args) throws SqlException {
        //step1 新创建优惠券时，选择某个体检中心
//        String name;
//        Number id;
//
        String id=args[1];
        String name=args[2];
        QueryManagers qhm=new QueryManagers();
        qhm.setId(Integer.parseInt(id));
        qhm.setName(name);


        if (checkdb){
//            String sql="select * from tb_coupon_template where status=1 and source=1 and organization_id= " + defSettHospitalId;
//            List<Map<String,Object>> list= DBMapper.query(sql);

            //step2入参格式化
            List<NameValuePair> params=new ArrayList<>();
            params.add(new BasicNameValuePair("hospitalId",String.valueOf(defSettHospitalId)));
            /*
            * Step3调用接口
            * ops调用接口时必须要加Flag.Ops
            * */
            HttpResult result=httpclient.get(Flag.OPS,Coupon_QueryHospitalManagers,params);
            Assert.assertEquals(result.getCode(),HttpStatus.SC_OK);
            List<QueryManagers> jsonObjectList = JSONArray.parseArray(result.getBody(), QueryManagers.class);

            //对客户经理的ID进行排序

            Collections.sort(jsonObjectList, new Comparator<QueryManagers>() {

                @Override
                public int compare(QueryManagers o1,
                                   QueryManagers o2) {
                    return o1.getId()-o2.getId();//用compareto
                }
            });



            if(checkdb){
                List<Account> accountLists =  AccountChecker.getManagerListByHosptailId(defSettHospitalId);
//               accountLists.get(0).getName();
//               accountLists.get(0).getId();
                /*
                * 比较开发返回的数据和数据库中筛选出来的数据一致性
                * */
                /*
                * 左侧开发接口返回的数据大小=数据库中的数据大小
                * */
                Assert.assertEquals(jsonObjectList.size(),accountLists.size());
                for(int i=0;i<accountLists.size();i++){
                    Assert.assertEquals(jsonObjectList.get(i).getId(),accountLists.get(i).getId().intValue());
                    Assert.assertEquals(jsonObjectList.get(i).getName(),accountLists.get(i).getName());
                }

            }
        }
    }
    @DataProvider
    public Iterator<String[]> queryManager(){
        try {
            return CvsFileUtil.fromCvsFileToIterator("./csv/coupon/ops/queryManager.csv",18);
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
