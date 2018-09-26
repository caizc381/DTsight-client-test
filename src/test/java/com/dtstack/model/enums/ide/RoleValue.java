package com.dtstack.model.enums.ide;

public enum RoleValue {
    TEANTOWNER(1), PROJECTOWNER(2), PROJECTADMIN(3), MEMBER(4), OPERATION(5), DATADEVELOP(6),
    /**
     * 用户自定义
     */
    CUSTOM(7);

    private int roleValue;

    RoleValue(int roleId) {
        this.roleValue = roleId;
    }

    public int getRoleValue() {
        return roleValue;
    }
}
