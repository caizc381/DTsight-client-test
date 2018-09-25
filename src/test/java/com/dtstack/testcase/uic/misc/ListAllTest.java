package com.dtstack.testcase.uic.misc;

import com.alibaba.fastjson.JSON;
import com.dtstack.base.Flag;
import com.dtstack.base.HttpResult;
import com.dtstack.base.dbcheck.uic.platform.ProductChecker;
import com.dtstack.model.po.uic.platform.ProductPO;
import com.dtstack.model.vo.uic.misc.ProductListVo;
import com.dtstack.testcase.uic.UicBase;
import com.dtstack.util.db.SqlException;
import com.jayway.jsonpath.JsonPath;
import org.apache.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

public class ListAllTest extends UicBase {
    @Test(description = "获取所有产品信息",groups = {"qa"})
    public void test_01_listAll() throws SqlException {
        HttpResult result =httpclient.get(Flag.UIC,UIC_ListAll);
        String body = result.getBody();
        Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);
        List<ProductListVo> productListVoList = JSON.parseArray(JsonPath.read(body,"$.data").toString(),ProductListVo.class);

        if (checkdb){
            List<ProductPO> productPOS = ProductChecker.findAllByIsDeletedIsFalse();
            List<ProductListVo> productListVos = new ArrayList<>();

            for (int i = 0; i < productPOS.size(); i++) {
                ProductListVo productListVo = ProductChecker.productPOToProductListVoConverter(productPOS.get(i));
                productListVoList.add(productListVo);
            }

            Assert.assertEquals(productListVoList.size(),productListVos.size());


        }
    }
}
