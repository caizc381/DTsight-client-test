package com.dtstack.model.vo.ide.common;

import com.dtstack.model.domain.ide.Role;
import com.dtstack.model.domain.ide.User;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class UserRoleVO {

    private long userId;
    private User user;
    private List<Role> roles = new ArrayList<>();

    private Timestamp gmtCreate;//加入项目时间

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    public Timestamp getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Timestamp gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public void addRoles(Role role) {
        this.roles.add(role);
    }
}
