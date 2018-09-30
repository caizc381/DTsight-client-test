package com.dtstack.model.domain.ide.batch;

import com.dtstack.model.domain.ide.TenantProjectEntity;

public class BatchFunction extends TenantProjectEntity {

    private String name;//函数名称
    private String className;//main函数类名
    private String purpose;//函数用途
    private String commandFormate;//函数命令格式
    private String paramDesc;//函数参数说明
    private Long nodePid;//父文件夹id
    private Long createUserId;
    private Long modifyUserId;
    private Integer type;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getCommandFormate() {
        return commandFormate;
    }

    public void setCommandFormate(String commandFormate) {
        this.commandFormate = commandFormate;
    }

    public String getParamDesc() {
        return paramDesc;
    }

    public void setParamDesc(String paramDesc) {
        this.paramDesc = paramDesc;
    }

    public Long getNodePid() {
        return nodePid;
    }

    public void setNodePid(Long nodePid) {
        this.nodePid = nodePid;
    }

    public Long getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(Long createUserId) {
        this.createUserId = createUserId;
    }

    public Long getModifyUserId() {
        return modifyUserId;
    }

    public void setModifyUserId(Long modifyUserId) {
        this.modifyUserId = modifyUserId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
