package com.dtstack.model.domain.ide.common;

import com.dtstack.model.domain.ide.BaseEntity;

public class NodeMachine extends BaseEntity {

    private String ip;//ip

    private Integer port;//端口

    private Integer machineType;//机器类型

    private String appType;//web(wen应用)，engine（执行引擎应用）

    private String deployInfo;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public Integer getMachineType() {
        return machineType;
    }

    public void setMachineType(Integer machineType) {
        this.machineType = machineType;
    }

    public String getAppType() {
        return appType;
    }

    public void setAppType(String appType) {
        this.appType = appType;
    }

    public String getDeployInfo() {
        return deployInfo;
    }

    public void setDeployInfo(String deployInfo) {
        this.deployInfo = deployInfo;
    }
}
