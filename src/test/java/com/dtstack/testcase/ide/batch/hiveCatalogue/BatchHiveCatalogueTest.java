package com.dtstack.testcase.ide.batch.hiveCatalogue;

import com.dtstack.base.Flag;
import com.dtstack.base.HttpResult;
import com.dtstack.testcase.ide.IdeBase;
import org.apache.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.Test;

public class BatchHiveCatalogueTest extends IdeBase {
    @Test(description = "",groups = {"qa"})
    public void test_01_batchHiveCatalogue(){
        HttpResult result = httpclient.post(Flag.IDE,IDE_GetHiveCatalogue,"");
        String body = result.getBody();
        System.out.println(body);
        Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);

        if (checkdb){

        }
    }
}
