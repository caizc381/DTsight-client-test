package com.dtstack.model.po.uic.platform;

import com.dtstack.lang.dtstack.prod.Products;
import com.dtstack.model.domain.ide.BaseEntity;

import java.util.Date;


public class ProductPO extends BaseEntity {
    private Products.Product code;
    private String name;
    private String version;
    private Date releaseTime;
    private String desc;
    private String imgURL;
    private String url;

    public Products.Product getCode() {
        return code;
    }

    public void setCode(Products.Product code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Date getReleaseTime() {
        return releaseTime;
    }

    public void setReleaseTime(Date releaseTime) {
        this.releaseTime = releaseTime;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getImgURL() {
        return imgURL;
    }

    public void setImgURL(String imgURL) {
        this.imgURL = imgURL;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
