package com.dtstack.testcase.uic.account;

import com.dtstack.base.Flag;
import com.dtstack.base.HttpResult;
import com.dtstack.model.vo.uic.account.UserFullTenantVo;
import com.dtstack.testcase.UicBase;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.testng.Assert;

import java.util.ArrayList;
import java.util.List;

public class SwitchTenantTest extends UicBase {

    //@Test(description = "切换租户", groups = {"qa","switchTenant"}, dependsOnGroups = {"getProducts"})
    public void test_01_switchTenant() {
        List<UserFullTenantVo> userFullTenantVos = GetFullTenantsTest.userFullTenantVos;


        Long tenantId = defTenantId;

        List<NameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair("tenantId", tenantId.toString()));

        HttpResult result = httpclient.post(Flag.UIC, UIC_SwitchTenant, pairs);

        String cookie = httpclient.getCookie();
        httpclient.setCookie(cookie+"dt_user_id="+defUicUserId+";dt_username="+defUicUsername.replace("@","%40"));
        String body = result.getBody();
        Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);

        if (checkdb) {

        }
    }
}
