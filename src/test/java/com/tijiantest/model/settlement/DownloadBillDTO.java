package com.tijiantest.model.settlement;

import java.util.List;

/**
 * Created by wangzhongxing on 2017/8/30.
 */
public class DownloadBillDTO {

    private Integer hospitalId;

    private List<String> batchSns;

    private List<String>  hospitalPlatformBillSns;

    private List<String>  hospitalCompanyBillSns;

    private String token;

    public Integer getHospitalId() {
        return hospitalId;
    }

    public void setHospitalId(Integer hospitalId) {
        this.hospitalId = hospitalId;
    }

    public List<String> getBatchSns() {
        return batchSns;
    }

    public void setBatchSns(List<String> batchSns) {
        this.batchSns = batchSns;
    }

    public List<String> getHospitalCompanyBillSns() {
        return hospitalCompanyBillSns;
    }

    public void setHospitalCompanyBillSns(List<String> hospitalCompanyBillSns) {
        this.hospitalCompanyBillSns = hospitalCompanyBillSns;
    }

    public List<String> getHospitalPlatformBillSns() {
        return hospitalPlatformBillSns;
    }

    public void setHospitalPlatformBillSns(List<String> hospitalPlatformBillSns) {
        this.hospitalPlatformBillSns = hospitalPlatformBillSns;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
