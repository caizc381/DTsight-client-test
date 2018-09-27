package com.dtstack.testcase.ide.batch.hiveCatalogue;

import com.alibaba.fastjson.JSON;
import com.dtstack.base.Flag;
import com.dtstack.base.HttpResult;
import com.dtstack.base.dbcheck.ide.batch.BatchHiveCatalogueChecker;
import com.dtstack.base.dbcheck.ide.batch.BatchHiveTableInfoChecker;
import com.dtstack.dtcenter.common.tree.TreeNode;
import com.dtstack.model.domain.ide.batch.BatchHiveCatalogue;
import com.dtstack.model.domain.ide.batch.HiveTableCataloguePO;
import com.dtstack.testcase.ide.IdeBase;
import com.dtstack.util.CvsFileUtil;
import com.dtstack.util.db.SqlException;
import com.jayway.jsonpath.JsonPath;
import org.apache.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * 位置：点击开发套件的请求
 */
public class GetHiveCatalogueTest extends IdeBase {
    //TODO
    @Test(description = "获取租户下的类目", groups = {"qa"}, dataProvider = "getHiveCatalogue")
    public void test_01_getHiveCatalogue(String... args) throws SqlException {
        String isGetFile = args[1];
        Map<String, Object> params = new HashMap<>();
        params.put("isGetFile", isGetFile);
        String json = JSON.toJSONString(params);
        HttpResult result = httpclient.post(Flag.IDE, BatchHiveCatalogue_GetHiveCatalogue, json);
        String body = result.getBody();
        System.out.println(body);
        Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);

        TreeNode treeNode = JSON.parseObject(JsonPath.read(body, "$.data").toString(), TreeNode.class);

        if (checkdb) {
            List<BatchHiveCatalogue> batchHiveCatalogueList = BatchHiveCatalogueChecker.listByTenantId(defRdosTenantId);
            //查询各节点下的表
            List<Object> allList = new ArrayList<>(batchHiveCatalogueList);

            if (isGetFile.equals("true")) {
                //保持不用传true，load全部表的操作
                for (BatchHiveCatalogue hc : batchHiveCatalogueList
                ) {
                    List<HiveTableCataloguePO> ht = BatchHiveTableInfoChecker.listByCatalogueId(hc.getId());
                    ht.forEach(t -> t.setTableId(t.getId()));
                    allList.addAll(ht);
                }
            }
        }
    }

    @DataProvider
    public Iterator<String[]> getHiveCatalogue() {
        try {
            return CvsFileUtil.fromCvsFileToIterator("./csv/ide/batch/getHiveCatalogue.csv", 18);
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
