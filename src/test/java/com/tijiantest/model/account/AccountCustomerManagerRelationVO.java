package com.tijiantest.model.account;


import java.util.List;

public class AccountCustomerManagerRelationVO {

    private Integer id;
    /**
     * C端客户账户id
     */
    private Integer customerId;


    private String name;

    /**
     * C端账号
     */
    private List<String> customerUserName;

    /**
     * 客户经理账户id
     */
    private Integer managerId;

    private Integer hospitalId;

    private Boolean isDeleted;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public List<String> getCustomerUserName() {
        return customerUserName;
    }

    public void setCustomerUserName(List<String> customerUserName) {
        this.customerUserName = customerUserName;
    }

    public Integer getManagerId() {
        return managerId;
    }

    public void setManagerId(Integer managerId) {
        this.managerId = managerId;
    }

    public Integer getHospitalId() {
        return hospitalId;
    }

    public void setHospitalId(Integer hospitalId) {
        this.hospitalId = hospitalId;
    }

    public Boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }




}
