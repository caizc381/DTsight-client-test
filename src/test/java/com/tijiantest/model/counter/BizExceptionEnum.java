package com.tijiantest.model.counter;

import com.tijiantest.util.ErrorInfo;

/**
 * @author weifeng
 * @date 2017/5/5
 */
public enum BizExceptionEnum implements ErrorInfo {
    /**
     * 业务异常
     * 格式：EX_[异常类型]_[日志级别]_[模块名称]_[子模块序号]_[业务场景]_[异常序号]
     * 异常类型（0：系统异常，1：业务异常）
     * 日志级别（0：error，1：warn，2：info）
     * 模块名称（此处为COUNTER，表示人数控制模块）
     * 子模块序号（00：默认，01：预处理，01：计算，02：规则，03：持久层
     * 业务场景（00：默认，01：扣人数，02：回收人数）
     * 异常序号（从001开始）
     */
    PARAMS_EMPTY("EX_1_0_COUNTER_01_00_001", "参数为空"),
    PARAMS_HOSPITAL_ID_ILLEGAL("EX_1_0_COUNTER_01_00_002", "hospitalId非法"),
    PARAMS_START_DATE_ILLEGAL("EX_1_0_COUNTER_01_00_003", "startDate非法"),
    PARAMS_END_DATE_ILLEGAL("EX_1_0_COUNTER_01_00_004", "endDate非法"),
    PARAMS_EXAMITEM_ID_ILLEGAL("EX_1_0_COUNTER_01_00_005", "examItemId非法"),
    PARAMS_COMPANY_ID_ILLEGAL("EX_1_0_COUNTER_01_00_006", "companyId非法"),
    PARAMS_COUNT_ILLEGAL("EX_1_0_COUNTER_01_00_007", "count非法"),
    PARAMS_ORDER_NUM_ILLEGAL("EX_1_0_COUNTER_01_00_008", "订单Num非法"),
    PARAMS_PERIOD_EXAM_ILLEGAL("EX_1_0_COUNTER_01_00_009", "容量为空，请设置"),
    BIZ_RECYCLE_HAS_DONE("EX_1_1_COUNTER_01_02_001", "容量已全部回收"),


    RULE_HOSPITAL_MAX_NUM_ZERO("EX_1_0_COUNTER_02_00_004", "体检中心容量0为休息日"),
    RULE_NO_PRIVILEGE_ORDER_IMMEDIATELY("EX_1_0_COUNTER_02_00_005", "无限制预约下单权限"),
    RULE_NOT_RESERVE_DAY_CANNOT_ORDER("EX_1_0_COUNTER_02_00_006", "在仅预留可约下，非预留日不可下单"),
    //第二个占位是项目id
    NOT_ENOUGH_CAPACITY("EX_1_0_COUNTER_02_00_007", "可预约名额不足"),
    NOT_ENOUGH_SET_HOSPITAL_CAPACITY("EX_1_0_COUNTER_02_03_001", "你在{0}日{1}时段{2}项目体检中心余量比单位预留小{3}"),
    NOT_ENOUGH_SET_COMPANY_LIMIT("EX_1_0_COUNTER_02_03_001", "你在{0}日{1}时段{2}项目设置的限额小于已预约人数{3}，请修改"),
    NOT_ENOUGH_SET_COMPANY_RESERVATION("EX_1_0_COUNTER_02_03_001", "你在{0}日{1}时段{2}项目设置的预留大于限额{3}，请修改"),
    NOT_ENOUGH_ORDER_CAPACITY("EX_1_0_COUNTER_02_00_008", "{0}号{1}时段{2}可预约名额不足,请选择其它时间段"),
    CONFIG_EMPTY_HOSPITAL_GLOABLE("EX_1_0_COUNTER_02_00_009", "{0}号本时段体检中心全局配置为空,请配置"),
    RULE_EXPIRE_ORDER("EX_1_0_COUNTER_02_00_010", "超过提前预约日"),
    NOT_ENOUGH_RESERVE_NUM_LT_USED_NUM("EX_1_0_COUNTER_02_00_011", "{0}日 {1}时段 {2}项目预留设置余量不足"),


    ERROR_UPDATE_COMPANY_INFO("EX_1_0_COUNTER_03_03_001", "更新单位配置失败，请稍后再试"),
    ;

    private String errorCode;
    private String errorMsg;

    private BizExceptionEnum(String errorCode, String errorMsg) {
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    @Override
    public String getErrorCode() {
        return errorCode;
    }

    @Override
    public String getErrorMsg() {
        return errorMsg;
    }
}
