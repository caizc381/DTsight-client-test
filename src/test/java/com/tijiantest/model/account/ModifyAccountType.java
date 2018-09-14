package com.tijiantest.model.account;

public enum ModifyAccountType {

    NEW("新增用户"),
    UPDATE("更新用户"),
    MODIFY_EXCEPTION("异常Tab页修改用户"),
    ;

    private String des;

    ModifyAccountType(String des) {
        this.des = des;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }
}