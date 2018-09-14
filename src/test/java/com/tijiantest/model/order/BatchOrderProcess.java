package com.tijiantest.model.order;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 批量下单进程对象
 *
 */
public class BatchOrderProcess implements Serializable{
    private static final long serialVersionUID = -2408291002620790208L;


    // TODO: 2017/5/11 copy from processDto ,need refactor ProcessDto

    /**
     * 总数
     */
    private Integer totalNum;

    /**
     * 处理数
     */
    private Integer dealNum;

    /**
     * 状态 BatchOrderProcessEnum
     */
    private Integer status;

    /**
     * 描述
     */
    private String description;


    private Integer id;

    /**
     * 订单批次
     */
    private OrderBatch orderBatch;

    /**
     * 成功数
     */
    private Integer successNum;

    /**
     * 失败数
     */
    private Integer failNum;

    /**
     * 操作人id
     */
    private Integer operatorId;

    /**
     * 创建时间
     */
    private Date gmtCreated;

    /**
     * 更新时间
     */
    private Date gmtModified;

    /**
     * 1.单位代预约下单 2.单位结算
     * {@link com.mytijian.order.enums.BatchProcessTaskTypeEnum}
     */
    private Integer taskType;

    /**
     * 存储任务内容的json格式
     * taskType=1 存储内容为｛"examCompanyName":"某公司体检单位","mealName":"套餐名称",｝
     * taskType=2 存储内容为｛"settlementCompanyName":["某公司体检单位","某公司体检单位2"]｝
     */
    private String taskContent;

    /**
     * 体检中心ID
     */
    private Integer hospitalId;

    /**
     * 下单用户列表
     */
    private List<Integer> accountIds;

    /**
     * 下单记录
     */
    private List<BatchOrderProcessRecord> records;

    /**
     * 是否应该关闭任务
     */
    private boolean shouldBeTerminate;

    /**
     * 单位id
     */
    private String company_ids;

    public Integer getTotalNum() {
        return totalNum;
    }

    public void setTotalNum(Integer totalNum) {
        this.totalNum = totalNum;
    }

    public Integer getDealNum() {
        return dealNum;
    }

    public void setDealNum(Integer dealNum) {
        this.dealNum = dealNum;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public OrderBatch getOrderBatch() {
        return orderBatch;
    }

    public void setOrderBatch(OrderBatch orderBatch) {
        this.orderBatch = orderBatch;
    }

    public Integer getSuccessNum() {
        return successNum;
    }

    public void setSuccessNum(Integer successNum) {
        this.successNum = successNum;
    }

    public Integer getFailNum() {
        return failNum;
    }

    public void setFailNum(Integer failNum) {
        this.failNum = failNum;
    }

    public Integer getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Integer operatorId) {
        this.operatorId = operatorId;
    }

    public Date getGmtCreated() {
        return gmtCreated;
    }

    public void setGmtCreated(Date gmtCreated) {
        this.gmtCreated = gmtCreated;
    }

    public Date getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(Date gmtModified) {
        this.gmtModified = gmtModified;
    }

    public List<Integer> getAccountIds() {
        return accountIds;
    }

    public void setAccountIds(List<Integer> accountIds) {
        this.accountIds = accountIds;
    }

    public List<BatchOrderProcessRecord> getRecords() {
        return records;
    }

    public void setRecords(List<BatchOrderProcessRecord> records) {
        this.records = records;
    }

    public boolean isShouldBeTerminate() {
        return shouldBeTerminate;
    }

    public void setShouldBeTerminate(boolean shouldBeTerminate) {
        this.shouldBeTerminate = shouldBeTerminate;
    }

    public Integer getTaskType() {
        return taskType;
    }

    public void setTaskType(Integer taskType) {
        this.taskType = taskType;
    }

    public String getTaskContent() {
        return taskContent;
    }

    /**
     * 存储任务内容的json格式
     * taskType=1 存储内容为｛"examCompanyName":"某公司体检单位","mealName":"套餐名称",｝
     * taskType=2 存储内容为｛"settlementCompanyName":["某公司体检单位","某公司体检单位2"]｝
     */
    public void setTaskContent(String taskContent) {
        this.taskContent = taskContent;
    }

    public Integer getHospitalId() {
        return hospitalId;
    }

    public void setHospitalId(Integer hospitalId) {
        this.hospitalId = hospitalId;
    }

    public String getCompany_ids() {
        return company_ids;
    }

    public void setCompany_ids(String company_ids) {
        this.company_ids = company_ids;
    }
}