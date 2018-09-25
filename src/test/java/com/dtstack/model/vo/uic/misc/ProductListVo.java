package com.dtstack.model.vo.uic.misc;

import com.dtstack.lang.support.web.Webs;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public class ProductListVo {
    private String productCode;
    private String productName;
    private String productDesc;
    private String releaseVersion;

    @JsonFormat(pattern = Webs.DATE_TIME_FORMAT,locale = "zh",timezone = "GMT+8")
    private Date releaseDate;
    private String logo;
    private String url;

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductDesc() {
        return productDesc;
    }

    public void setProductDesc(String productDesc) {
        this.productDesc = productDesc;
    }

    public String getReleaseVersion() {
        return releaseVersion;
    }

    public void setReleaseVersion(String releaseVersion) {
        this.releaseVersion = releaseVersion;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
