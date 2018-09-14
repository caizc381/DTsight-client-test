package com.tijiantest.model.account;

/**
 * Created by well on 17/5/11.
 */
public enum AddAccountTypeEnum {

    idCard("按身份证导入"),
    employeeNo( "按员工号导入"),
    other( "其他方式导入");

    private String desc;


    private AddAccountTypeEnum( String desc){
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public static AddAccountTypeEnum getByName(String name){
        if(name==null){
            return null;
        }
        AddAccountTypeEnum[] values = AddAccountTypeEnum.values();
        for(AddAccountTypeEnum addAccountTypeEnum:values){
            if(addAccountTypeEnum.name().equals(name)){
                return addAccountTypeEnum;
            }
        }
        return null;
    }
}
