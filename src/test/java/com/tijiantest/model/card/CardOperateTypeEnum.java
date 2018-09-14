package com.tijiantest.model.card;

/**
 * @author king
 * 卡操作类型枚举
 */

public enum CardOperateTypeEnum {

    REVOKE_CARD(1,"撤销发卡"),
    RECOVER_BALANCE  (2,"余额回收"),
    UPDATE_EXPIRED_DATE(3,"修改卡有效期");

    private Integer code;

    private String value;

    private CardOperateTypeEnum(Integer code, String value) {
        this.code = code;
        this.value = value;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
