package com.dtstack.testcase.uic;

import com.dtstack.base.Flag;
import com.dtstack.base.HttpResult;
import org.apache.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.Test;

public class UserTest extends  UicBase{
    @Test(description = "",groups = {"qa","user"},dependsOnGroups = {"getFullTenants"})
    public void test_01_user(){
        HttpResult result = httpclient.get(Flag.DTUIC,UIC_User);
        String body = result.getBody();
        System.out.println(body);
        Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);

        if (checkdb){

        }
    }
}
