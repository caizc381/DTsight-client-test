package com.tijiantest.model.account;

import java.io.File;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/5/12.
 */
public class BatchImportCustomerDTO implements Serializable {
    private static final long serialVersionUID = -7788828283563750761L;

    Integer managerId;
    Integer companyId;
    Integer hospitalId;
    String group;
    List<String> sheetNames;
    File file;
    String operator;
    Integer newCompanyId;
    Integer organizationType;
    Map<String, Map<String, Integer>> sheetColumnMap;

    public Integer getManagerId() {
        return managerId;
    }

    public void setManagerId(Integer managerId) {
        this.managerId = managerId;
    }

    public Integer getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Integer companyId) {
        this.companyId = companyId;
    }

    public Integer getHospitalId() {
        return hospitalId;
    }

    public void setHospitalId(Integer hospitalId) {
        this.hospitalId = hospitalId;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public List<String> getSheetNames() {
        return sheetNames;
    }

    public void setSheetNames(List<String> sheetNames) {
        this.sheetNames = sheetNames;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public Integer getNewCompanyId() {
        return newCompanyId;
    }

    public void setNewCompanyId(Integer newCompanyId) {
        this.newCompanyId = newCompanyId;
    }

    public Integer getOrganizationType() {
        return organizationType;
    }

    public void setOrganizationType(Integer organizationType) {
        this.organizationType = organizationType;
    }

    public Map<String, Map<String, Integer>> getSheetColumnMap() {
        return sheetColumnMap;
    }

    public void setSheetColumnMap(Map<String, Map<String, Integer>> sheetColumnMap) {
        this.sheetColumnMap = sheetColumnMap;
    }
}
