package com.dtstack.model.po.uic.account;


import com.dtstack.lang.data.BaseEntity;

public class UserTenantRelPO extends BaseEntity {
    private Long userId;//关联用户id
    private Long tenantId;//关联租户id

    private boolean isAdmin;//是否删除，Y是管理员，N普通成员

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }


}
