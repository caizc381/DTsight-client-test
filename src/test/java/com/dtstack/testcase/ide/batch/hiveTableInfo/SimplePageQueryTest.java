package com.dtstack.testcase.ide.batch.hiveTableInfo;

import com.dtstack.testcase.ide.IdeBase;
import com.dtstack.util.CvsFileUtil;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;

/**
 * 位置：运维中心-》离线任务发布-》创建发布包-》对象类型：表
 */

public class SimplePageQueryTest extends IdeBase {

    @Test(description = "表分页查询",groups = {"qa"},dataProvider = "simplePageQ")
    public void test_01_simplePageQuery(){

    }

    @DataProvider
    public Iterator<String[]> simplePageQuery() {
        try {
            return CvsFileUtil.fromCvsFileToIterator("./csv/ide/batch/resource/pageQuery.csv", 6);
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
