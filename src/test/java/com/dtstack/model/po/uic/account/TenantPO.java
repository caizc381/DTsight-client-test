package com.dtstack.model.po.uic.account;


import com.dtstack.lang.data.BaseEntity;

import java.util.List;

public class TenantPO extends BaseEntity {
    private String tenantName;//租户名称
    private String tenantDesc;//租户描述信息
    private String contactName;//租户联系人姓名（默认为创建租户者）
    private String contactEmail;//租户联系邮箱（默认为租户创建者邮箱）
    private String contactPhone;//租户联系电话（默认为租户创建者电话）
    private Long belongUserId;//租户所属用户id（当前为创建租户的用户id）
    private String agentToken;//与agent交互时的token
    private List<UserPO> users;

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    public String getTenantDesc() {
        return tenantDesc;
    }

    public void setTenantDesc(String tenantDesc) {
        this.tenantDesc = tenantDesc;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public Long getBelongUserId() {
        return belongUserId;
    }

    public void setBelongUserId(Long belongUserId) {
        this.belongUserId = belongUserId;
    }

    public String getAgentToken() {
        return agentToken;
    }

    public void setAgentToken(String agentToken) {
        this.agentToken = agentToken;
    }

    public List<UserPO> getUsers() {
        return users;
    }

    public void setUsers(List<UserPO> users) {
        this.users = users;
    }
}
