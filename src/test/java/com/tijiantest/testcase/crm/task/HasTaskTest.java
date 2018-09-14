package com.tijiantest.testcase.crm.task;

import com.jayway.jsonpath.JsonPath;
import com.tijiantest.base.HttpResult;
import com.tijiantest.base.dbcheck.OrderChecker;
import com.tijiantest.testcase.crm.CrmBase;
import com.tijiantest.testcase.crm.settlement.SettleBase;
import com.tijiantest.util.ListUtil;
import com.tijiantest.util.db.SqlException;
import net.minidev.json.JSONArray;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HasTaskTest extends SettleBase {
    @Test(description = "检查医院是否有正在结算的批次",groups = {"qa"})
    public void test_01_hasTask() throws SqlException, ParseException {
        int hospitalId = defSettHospital.getId();
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("hospitalId",hospitalId+""));

        HttpResult result = httpclient.get(HasTask,params);
        Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);
        String body = result.getBody();
        log.info(body);

        String companyIds = null;
        if(body != null && !body.equals("{}")) {
            JSONArray companyArray = JsonPath.read(body, "$.companyIds");
            companyIds = companyArray.toString();
        }
        if(checkdb){
            String dbCompanyIds = OrderChecker.getHosptialHasRunSettbach(hospitalId);
            if(body == null || body.equals("{}"))
                Assert.assertNull(dbCompanyIds);
            else
                Assert.assertEquals(companyIds,dbCompanyIds);
        }
    }
}
