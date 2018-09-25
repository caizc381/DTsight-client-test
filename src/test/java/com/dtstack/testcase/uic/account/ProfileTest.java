package com.dtstack.testcase.uic.account;

import com.dtstack.base.Flag;
import com.dtstack.base.HttpResult;
import com.dtstack.testcase.uic.UicBase;
import org.apache.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ProfileTest extends UicBase {
    @Test(description = "查询用户信息",groups = {"qa","profile"},dependsOnGroups = {"user"})
    public void test_01_profile(){
        HttpResult result = httpclient.get(Flag.UIC,UIC_Profile);
        String body = result.getBody();
        System.out.println(body);
        Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);
        if (checkdb){

        }
    }
}
