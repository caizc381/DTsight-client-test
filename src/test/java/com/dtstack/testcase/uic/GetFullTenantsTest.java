package com.dtstack.testcase.uic;

import com.dtstack.base.Flag;
import com.dtstack.base.HttpResult;
import org.apache.http.NameValuePair;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

public class GetFullTenantsTest extends UicBase{

    @Test
    public void getFullTenants(){
        List<NameValuePair> pairs = new ArrayList<>();
        //httpclient.setCookie("eeee");
        HttpResult result = httpclient.get(Flag.DTUIC, UIC_GetFullTenants);

        System.out.println(result.getBody());
        System.out.println(result.getCode());
    }
}
