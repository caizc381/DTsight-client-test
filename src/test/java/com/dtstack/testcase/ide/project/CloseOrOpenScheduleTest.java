package com.dtstack.testcase.ide.project;

import com.alibaba.fastjson.JSON;
import com.dtstack.base.Flag;
import com.dtstack.base.HttpResult;
import com.dtstack.testcase.ide.IdeBase;
import com.dtstack.util.CvsFileUtil;
import com.dtstack.util.db.DBMapper;
import com.dtstack.util.db.SqlException;
import org.apache.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 位置：开发套件-》项目管理
 */

public class CloseOrOpenScheduleTest extends IdeBase {


    @Test(description = "开启或关闭调度", groups = {"qa"}, dataProvider = "closeOrOpenSchedule")
    public void test_01_closeOrOpenSchedule(String... args) throws SqlException {
        String status = args[1];

        if (!status.equals("0") && !status.equals("1")) {
            status = "0";
        }

        Map<String, Object> params = new HashMap<>();
        params.put("status", status);

        String json = JSON.toJSONString(params);

        HttpResult result = httpclient.post(Flag.IDE, Project_CloseOrOpenSchedule, json);
        String body = result.getBody();
        Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);

        if (checkdb) {
            String sql = "select schedule_status from rdos_project where tenant_id=? and id=?";
            List<Map<String, Object>> list = DBMapper.query(sql, defRdosTenantId, defProjectId);
            Assert.assertEquals(list.get(0).get("schedule_status").toString(), status);
        }
    }

    @DataProvider
    public Iterator<String[]> closeOrOpenSchedule() {
        try {
            return CvsFileUtil.fromCvsFileToIterator("./csv/ide/project/closeOrOpenSchedule.csv", 18);
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
