package com.tijiantest.model.common;

public enum LogTypeEnum {
    LOG_TYPE_SETTLEMENT_PLATFORM_AUDIT(1, "结算平台账单审核"),
    LOG_TYPE_SETTLEMENT_CONSUME_QUOTA_AUDIT(2, "结算消费额度明细审核流程"),
    LOG_TYPE_REFUND(3, "付款订单退款或撤销日志"),
    LOG_TYPE_HOSITALREMARK(4,"医院新增/修改备注"),
    Log_TYPE_CREDIT_ACCOUNT(5,"信用账户的操作日志"),
    LOG_TYPE_COUPON_TEMPLATE(6, "优惠券模版操作日志");
    ;

    private int value;
    private String desc;

    LogTypeEnum(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public static boolean isValidLogType(Integer logType){
        for(LogTypeEnum logTypeEnum : values()){
            if(logType.equals(logTypeEnum.getValue())){
                return true;
            }
        }
        return false;
    }
}
