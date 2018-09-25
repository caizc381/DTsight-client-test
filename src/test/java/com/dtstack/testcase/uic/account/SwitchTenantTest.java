package com.dtstack.testcase.uic.account;

import com.dtstack.base.Flag;
import com.dtstack.base.HttpResult;
import com.dtstack.model.vo.uic.account.UserFullTenantVo;
import com.dtstack.testcase.uic.UicBase;
import com.dtstack.util.ListUtil;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

public class SwitchTenantTest extends UicBase {

    @Test(description = "切换租户", groups = {"qa"}, dependsOnGroups = {"getFullTenants"})
    public void test_01_switchTenant() {
        List<UserFullTenantVo> userFullTenantVos = GetFullTenantsTest.userFullTenantVos;

        int index = ListUtil.getRandomIndexFromList(userFullTenantVos);
        UserFullTenantVo userFullTenantVo = userFullTenantVos.get(index);
        Long tenantId = userFullTenantVo.getTenantId();

        List<NameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair("tenantId", tenantId.toString()));

        HttpResult result = httpclient.post(Flag.UIC, UIC_SwitchTenant, pairs);

        String body = result.getBody();
        Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);

        if (checkdb) {

        }
    }
}
