package com.dtstack.testcase.uic;

import com.dtstack.base.Flag;
import com.dtstack.base.HttpResult;
import org.apache.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.Test;

public class GetProductsTest extends UicBase {

    @Test(description = "获取所有产品",groups = {"qa"},dependsOnGroups = {"profile"})
    public void test_01_getProducts(){
        HttpResult result = httpclient.get(Flag.UICAPI,UIC_GetProducts);
        String body = result.getBody();
        System.out.println("body ======== "+body);
        Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);

        if (checkdb){

        }
    }
}
