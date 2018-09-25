package com.dtstack.model.vo.uic.account;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.dtstack.lang.support.web.Webs;

import java.util.Date;
import java.util.List;

public class UserFullTenantVo {
    private Long tenantId;
    private String tenantName;
    private String tenantDesc;
    private boolean current;
    private boolean lastLogin;
    private boolean admin;
    private String creator;
    @JsonFormat(pattern = Webs.DATE_TIME_FORMAT, locale = "zh", timezone = "GMT+8")
    private Date createTime;
    private Integer otherUserCount;
    private List<UserFullTenantAdminVo> adminList;


    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

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

    public boolean isCurrent() {
        return current;
    }

    public void setCurrent(boolean current) {
        this.current = current;
    }

    public boolean isLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(boolean lastLogin) {
        this.lastLogin = lastLogin;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getOtherUserCount() {
        return otherUserCount;
    }

    public void setOtherUserCount(Integer otherUserCount) {
        this.otherUserCount = otherUserCount;
    }

    public List<UserFullTenantAdminVo> getAdminList() {
        return adminList;
    }

    public void setAdminList(List<UserFullTenantAdminVo> adminList) {
        this.adminList = adminList;
    }
}
