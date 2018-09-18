package com.dtstack.util;

public interface ErrorInfo {

	/**
     * 获取异常编码 
     * 
     * 异常码含义：
     * EX_1_0_ORDER_01_01_001 
     * EX：Exception缩写，表示异常 
     * 1：表示异常类型（0：系统异常，1：业务异常）
     * 0：表示日志级别（0：error，1：warn，2：info） 
     * ORDER：模块名称 
     * 01：子模块序号
     * 01：业务场景序号，例如，01：下单，02：改项，03：改期 
     * 001：异常的序号 
     * 
     * @return
     */
    String getErrorCode();

    /**
     * 获取异常信息
     * 
     * @return
     */
    String getErrorMsg();
}
