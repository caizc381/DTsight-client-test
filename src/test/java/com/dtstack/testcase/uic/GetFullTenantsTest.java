package com.dtstack.testcase.uic;

import com.alibaba.fastjson.JSON;
import com.dtstack.base.Flag;
import com.dtstack.base.HttpResult;
import com.dtstack.model.vo.uic.UserFullTenantVo;
import com.jayway.jsonpath.JsonPath;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

public class GetFullTenantsTest extends UicBase{

    @Test(description ="获取所有租户信息",groups = {"qa","getFullTenants"})
    public void getFullTenants(){
        List<NameValuePair> pairs = new ArrayList<>();
        //httpclient.setCookie("eeee");
        HttpResult result = httpclient.get(Flag.DTUIC, UIC_GetFullTenants);

        System.out.println(result.getBody());
        System.out.println(result.getCode());
        String body = result.getBody();

        List<UserFullTenantVo> userFullTenantVos = JSON.parseArray(JsonPath.read(body, "$.data").toString(),
                UserFullTenantVo.class);
        System.out.println(body);
        Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);


        if (checkdb){

        }
    }
}
