package com.tijiantest.model.settlement;


import java.io.Serializable;
import java.util.List;

import com.tijiantest.util.pagination.Page;

/**
 * Created by wangzhongxing on 2017/8/23.
 */
public class UnsettlementCardQueryDTO implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * 医院id
     */
    private Integer hospitalId;
    /**
     * 单位id
     */
    private List<Integer> examCompanyIds;
    /**
     * 客户经理id
     */
    private Integer managerId;
    /**
     * 状态：0：不可用，1：可用，2：已撤销，3：余额收回
     * @See CardStatusEnum
     */
    private List<Integer> status;
    /**
     * 发卡开始时间
     */
    private String sendCardStartTime;
    /**
     * 发卡结束时间
     */
    private String sendCardEndTime;
    /**
     * 结算方式，0：按项目，1：按人数
     */
    private Integer settlementMode;
    /**
     * 自动过滤发卡时间为最近7天且按项目结算的卡：0：否，1：是
     */
    private Integer filter;
    /**
     * 用户姓名或身份证
     */
    private String userNameOrIdcard;
    /**
     * 分页信息
     */
    private Page page;
    /**
     * 测试批次号
     */
    private String batchSn;

    public Integer getHospitalId() {
        return hospitalId;
    }

    public void setHospitalId(Integer hospitalId) {
        this.hospitalId = hospitalId;
    }

    public List<Integer> getExamCompanyIds() {
        return examCompanyIds;
    }

    public void setExamCompanyIds(List<Integer> examCompanyIds) {
        this.examCompanyIds = examCompanyIds;
    }

    public Integer getManagerId() {
        return managerId;
    }

    public void setManagerId(Integer managerId) {
        this.managerId = managerId;
    }

    public List<Integer> getStatus() {
        return status;
    }

    public void setStatus(List<Integer> status) {
        this.status = status;
    }

    public String getSendCardEndTime() {
        return sendCardEndTime;
    }

    public void setSendCardEndTime(String sendCardEndTime) {
        this.sendCardEndTime = sendCardEndTime;
    }

    public String getSendCardStartTime() {
        return sendCardStartTime;
    }

    public void setSendCardStartTime(String sendCardStartTime) {
        this.sendCardStartTime = sendCardStartTime;
    }

    public Integer getSettlementMode() {
        return settlementMode;
    }

    public void setSettlementMode(Integer settlementMode) {
        this.settlementMode = settlementMode;
    }

    public Integer getFilter() {
        return filter;
    }

    public void setFilter(Integer filter) {
        this.filter = filter;
    }

    public String getUserNameOrIdcard() {
        return userNameOrIdcard;
    }

    public void setUserNameOrIdcard(String userNameOrIdcard) {
        this.userNameOrIdcard = userNameOrIdcard;
    }

    public Page getPage() {
        return page;
    }

    public void setPage(Page page) {
        this.page = page;
    }

    public String getBatchSn() {
        return batchSn;
    }

    public void setBatchSn(String batchSn) {
        this.batchSn = batchSn;
    }
}
