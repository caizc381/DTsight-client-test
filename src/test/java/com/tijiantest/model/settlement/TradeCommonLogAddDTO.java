package com.tijiantest.model.settlement;

import java.io.Serializable;

public class TradeCommonLogAddDTO implements Serializable {
    private static final long serialVersionUID = 8867547489710208730L;

    private String refSn;
    private Integer logType;
    private Integer operatorId;
    private Integer operatorType;
    private String operatorName;
    /**
     * 操作内容,json串
     */
    private String operation;

    private Integer isDeleted;

    public String getRefSn() {
        return refSn;
    }

    public void setRefSn(String refSn) {
        this.refSn = refSn;
    }

    public Integer getLogType() {
        return logType;
    }

    public void setLogType(Integer logType) {
        this.logType = logType;
    }

    public Integer getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Integer operatorId) {
        this.operatorId = operatorId;
    }

    public Integer getOperatorType() {
        return operatorType;
    }

    public void setOperatorType(Integer operatorType) {
        this.operatorType = operatorType;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public Integer getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Integer isDeleted) {
        this.isDeleted = isDeleted;
    }
}
