package com.dtstack.model.domain.ide.batch;

import com.dtstack.model.domain.ide.TenantProjectEntity;

import java.sql.Timestamp;

public class BatchHiveTableInfo extends TenantProjectEntity {
    private String tableName;//表名称
    private Long userId;//创建表的用户id
    private Long chargeUserId;//表负责人
    private Long modifyUserId;
    private Long tableSize;//表大小
    private Timestamp sizeUpdateTime;//表大小更新时间
    private Long catalogueId;//类目id
    private String path;//类目路径
    private String location;//hdfs路径
    private String delim;//列分隔符
    private String storeType;//存储格式
    private Integer lifeDay;//生命周期，单位：day
    private Integer lifeStatus;//生命周期状态，0：未开始，1：存活，2：销毁，3：执行过程出现异常
    private Integer isDirtyDataTable=0;//是否是脏数据表，0-否；1-是
    private Timestamp lastDdlTime;//表结构最近修改时间
    private Timestamp lastDmlTime;//表数据最后修改时间
    private String tableDesc;//表描述
    private String grade;//层级
    private String subject;//主题域
    private String refreshRate ;//刷新频率
    private String increType;//增量类型
    private Integer isIgnore;//是否忽略
    private String checkResult;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getChargeUserId() {
        return chargeUserId;
    }

    public void setChargeUserId(Long chargeUserId) {
        this.chargeUserId = chargeUserId;
    }

    public Long getModifyUserId() {
        return modifyUserId;
    }

    public void setModifyUserId(Long modifyUserId) {
        this.modifyUserId = modifyUserId;
    }

    public Long getTableSize() {
        return tableSize;
    }

    public void setTableSize(Long tableSize) {
        this.tableSize = tableSize;
    }

    public Timestamp getSizeUpdateTime() {
        return sizeUpdateTime;
    }

    public void setSizeUpdateTime(Timestamp sizeUpdateTime) {
        this.sizeUpdateTime = sizeUpdateTime;
    }

    public Long getCatalogueId() {
        return catalogueId;
    }

    public void setCatalogueId(Long catalogueId) {
        this.catalogueId = catalogueId;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDelim() {
        return delim;
    }

    public void setDelim(String delim) {
        this.delim = delim;
    }

    public String getStoreType() {
        return storeType;
    }

    public void setStoreType(String storeType) {
        this.storeType = storeType;
    }

    public Integer getLifeDay() {
        return lifeDay;
    }

    public void setLifeDay(Integer lifeDay) {
        this.lifeDay = lifeDay;
    }

    public Integer getLifeStatus() {
        return lifeStatus;
    }

    public void setLifeStatus(Integer lifeStatus) {
        this.lifeStatus = lifeStatus;
    }

    public Integer getIsDirtyDataTable() {
        return isDirtyDataTable;
    }

    public void setIsDirtyDataTable(Integer isDirtyDataTable) {
        this.isDirtyDataTable = isDirtyDataTable;
    }

    public Timestamp getLastDdlTime() {
        return lastDdlTime;
    }

    public void setLastDdlTime(Timestamp lastDdlTime) {
        this.lastDdlTime = lastDdlTime;
    }

    public Timestamp getLastDmlTime() {
        return lastDmlTime;
    }

    public void setLastDmlTime(Timestamp lastDmlTime) {
        this.lastDmlTime = lastDmlTime;
    }

    public String getTableDesc() {
        return tableDesc;
    }

    public void setTableDesc(String tableDesc) {
        this.tableDesc = tableDesc;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getRefreshRate() {
        return refreshRate;
    }

    public void setRefreshRate(String refreshRate) {
        this.refreshRate = refreshRate;
    }

    public String getIncreType() {
        return increType;
    }

    public void setIncreType(String increType) {
        this.increType = increType;
    }

    public Integer getIsIgnore() {
        return isIgnore;
    }

    public void setIsIgnore(Integer isIgnore) {
        this.isIgnore = isIgnore;
    }

    public String getCheckResult() {
        return checkResult;
    }

    public void setCheckResult(String checkResult) {
        this.checkResult = checkResult;
    }
}
