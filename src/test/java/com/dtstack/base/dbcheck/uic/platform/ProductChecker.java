package com.dtstack.base.dbcheck.uic.platform;

import com.dtstack.base.BaseTest;
import com.dtstack.lang.dtstack.prod.Products;
import com.dtstack.model.po.uic.platform.ProductPO;
import com.dtstack.model.vo.uic.misc.ProductListVo;
import com.dtstack.util.db.DBMapper;
import com.dtstack.util.db.SqlException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ProductChecker extends BaseTest {
    public static List<ProductPO> findAllByIsDeletedIsFalse() throws SqlException {
        String sql = "select * from uic_product where is_deleted=?";
        List<Map<String, Object>> list = DBMapper.queryUic(sql, "N");
        List<ProductPO> productPOS = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            ProductPO productPO = map2ProductPO(list.get(i));
            productPOS.add(productPO);
        }
        return productPOS;
    }

    public static ProductPO map2ProductPO(Map<String, Object> map) {
        ProductPO productPO = new ProductPO();
        productPO.setId(Long.valueOf(map.get("id").toString()));
        productPO.setIsDeleted(map.get("is_deleted").toString().equals("N") ? 0 : 1);
        if (map.get("prodcut_desc")!=null){
            productPO.setDesc(map.get("prodcut_desc").toString());
        }

        productPO.setImgURL(map.get("product_img_url").toString());
        productPO.setName(map.get("product_name").toString());
        productPO.setVersion(map.get("product_version").toString());
        productPO.setUrl(map.get("product_url").toString());
        productPO.setCode(Products.getProduct(map.get("product_code").toString()));
        return productPO;
    }

    public static ProductListVo productPOToProductListVoConverter(ProductPO productPO) {
        return Optional.ofNullable(productPO).map(p -> {
            ProductListVo vo = new ProductListVo();
            vo.setProductCode(p.getCode().code());
            vo.setProductName(p.getName());
            vo.setProductDesc(p.getDesc());
            vo.setReleaseVersion(p.getVersion());
            vo.setReleaseDate(p.getReleaseTime());
            vo.setLogo(p.getImgURL());
            vo.setUrl(p.getUrl());
            return vo;
        }).orElse(null);
    }
}
