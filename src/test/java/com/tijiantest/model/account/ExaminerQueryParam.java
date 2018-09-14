package com.tijiantest.model.account;

import org.apache.commons.lang.StringUtils;

import java.io.Serializable;

public class ExaminerQueryParam implements Serializable {

    private Integer organizationId;

    private Integer newCompanyId;

    private Integer managerId;

    private Integer relationId;

    private String idCard;

    private String employeeNo;

    private Integer isSelf;

    private Integer isOper;

    private Integer isDelete;

    private Integer customerId;

    static ExaminerQueryParam bulid(Integer organizationId,
                                    Integer newCompanyId,
                                    Integer managerId,
                                    Integer relationId,
                                    String idCard,
                                    String employeeNo,
                                    Integer isSelf,
                                    Integer isDelete,
                                    Integer customerId,
                                    Integer isOper){
        ExaminerQueryParam queryParam = new ExaminerQueryParam();
        if (organizationId != null){
            queryParam.setOrganizationId(organizationId);
        }
        if (newCompanyId != null){
            queryParam.setNewCompanyId(newCompanyId);
        }
        if (managerId != null){
            queryParam.setManagerId(managerId);
        }
        if (relationId != null){
            queryParam.setRelationId(relationId);
        }
        if (StringUtils.isNotBlank(idCard)){
            queryParam.setIdCard(idCard);
        }
        if (StringUtils.isNotBlank(employeeNo)){
            queryParam.setEmployeeNo(employeeNo);
        }
        if (isSelf != null){
            queryParam.setIsSelf(isSelf);
        }
        if (isOper != null){
            queryParam.setIsOper(isOper);
        }
        if (isDelete != null){
            queryParam.setIsDelete(isDelete);
        }
        if (customerId != null){
            queryParam.setCustomerId(customerId);
        }
        return queryParam;
    }


    public Integer getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Integer organizationId) {
        this.organizationId = organizationId;
    }

    public Integer getNewCompanyId() {
        return newCompanyId;
    }

    public void setNewCompanyId(Integer newCompanyId) {
        this.newCompanyId = newCompanyId;
    }

    public Integer getManagerId() {
        return managerId;
    }

    public void setManagerId(Integer managerId) {
        this.managerId = managerId;
    }

    public Integer getRelationId() {
        return relationId;
    }

    public void setRelationId(Integer relationId) {
        this.relationId = relationId;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public String getEmployeeNo() {
        return employeeNo;
    }

    public void setEmployeeNo(String employeeNo) {
        this.employeeNo = employeeNo;
    }

    public Integer getIsSelf() {
        return isSelf;
    }

    public void setIsSelf(Integer isSelf) {
        this.isSelf = isSelf;
    }

    public Integer getIsOper() {
        return isOper;
    }

    public void setIsOper(Integer isOper) {
        this.isOper = isOper;
    }

    public Integer getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(Integer isDelete) {
        this.isDelete = isDelete;
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }
}
