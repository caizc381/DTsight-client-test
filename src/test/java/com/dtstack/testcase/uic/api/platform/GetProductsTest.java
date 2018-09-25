package com.dtstack.testcase.uic.api.platform;

import com.alibaba.fastjson.JSON;
import com.dtstack.base.Flag;
import com.dtstack.base.HttpResult;
import com.dtstack.base.dbcheck.uic.platform.ProductChecker;
import com.dtstack.model.po.uic.platform.ProductPO;
import com.dtstack.model.vo.uic.platform.ProductVo;
import com.dtstack.testcase.uic.UicBase;
import com.dtstack.util.db.SqlException;
import com.jayway.jsonpath.JsonPath;
import org.apache.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

public class GetProductsTest extends UicBase {

    @Test(description = "获取所有产品", groups = {"qa", "getProducts"}, dependsOnGroups = {"profile"})
    public void test_01_getProducts() throws SqlException {
        HttpResult result = httpclient.get(Flag.UICAPI, API_GetProducts);
        String body = result.getBody();
        System.out.println("body ======== " + body);
        Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);
        List<ProductVo> productVos = JSON.parseArray(JsonPath.read(body, "$.data").toString(),
                ProductVo.class);

        if (checkdb) {
            List<ProductPO> productPOList = ProductChecker.findAllByIsDeletedIsFalse();
            List<ProductVo> resultProductVoList = new ArrayList<>();
            for (int i = 0; i < productPOList.size(); i++) {
                ProductVo productVo = productPOToVoConverter(productPOList.get(i));
                resultProductVoList.add(productVo);
            }

            Assert.assertEquals(productVos.size(), resultProductVoList.size());
            for (int i = 0; i < productVos.size(); i++) {
                ProductVo productVo = productVos.get(i);
                ProductVo resultProductVo = resultProductVoList.get(i);
                Assert.assertEquals(productVo.getProductCode(), resultProductVo.getProductCode());
                Assert.assertEquals(productVo.getProductName(), resultProductVo.getProductName());
                Assert.assertEquals(productVo.getProductDesc(), resultProductVo.getProductDesc());
                Assert.assertEquals(productVo.getReleaseVersion(), resultProductVo.getReleaseVersion());
                Assert.assertEquals(productVo.getUrl(), resultProductVo.getUrl());
            }
        }
    }

    public ProductVo productPOToVoConverter(ProductPO productPO) {
        ProductVo productVo = new ProductVo();
        productVo.setProductDesc(productPO.getDesc());
        productVo.setProductCode(productPO.getCode().code());
        productVo.setProductName(productPO.getName());
        productVo.setReleaseVersion(productPO.getVersion());
        productVo.setUrl(productPO.getUrl());
        productVo.setLogo(productPO.getImgURL());
        return productVo;
    }
}
