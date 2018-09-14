package com.tijiantest.model.order.orderrefund;


import java.util.Date;
import java.util.List;

/**
 * 退款审核
 * @author weifeng
 * @date 2017/8/1
 */
public class OrderRefundApplyQueryVO{
    private String account;
    private Integer fromSite;
    private Integer hospitalCompanyId;
    private Date applyStartTime;
    private Date applyEndTime;
    private Date auditStartTime;
    private Date auditEndTime;
    private Integer minOnlinePay;
    private Integer maxOnlinePay;
    private Integer refundType;
    private Integer scene;
    private Integer pageSize;
    private Integer currentPage;
    private List<Integer> statusList;
    private boolean recentThreeDay;
    private String countMethod;
    

    public Integer getFromSite() {
        return fromSite;
    }

    public void setFromSite(Integer fromSite) {
        this.fromSite = fromSite;
    }

    public Integer getHospitalCompanyId() {
        return hospitalCompanyId;
    }

    public void setHospitalCompanyId(Integer hospitalCompanyId) {
        this.hospitalCompanyId = hospitalCompanyId;
    }

    public Date getApplyStartTime() {
        return applyStartTime;
    }

    public void setApplyStartTime(Date applyStartTime) {
        this.applyStartTime = applyStartTime;
    }

    public Date getApplyEndTime() {
        return applyEndTime;
    }

    public void setApplyEndTime(Date applyEndTime) {
        this.applyEndTime = applyEndTime;
    }

    public Integer getMinOnlinePay() {
        return minOnlinePay;
    }

    public void setMinOnlinePay(Integer minOnlinePay) {
        this.minOnlinePay = minOnlinePay;
    }

    public Integer getMaxOnlinePay() {
        return maxOnlinePay;
    }

    public void setMaxOnlinePay(Integer maxOnlinePay) {
        this.maxOnlinePay = maxOnlinePay;
    }

    public Integer getRefundType() {
        return refundType;
    }

    public void setRefundType(Integer refundType) {
        this.refundType = refundType;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }

    public Integer getScene() {
        return scene;
    }

    public void setScene(Integer scene) {
        this.scene = scene;
    }

    public List<Integer> getStatusList() {
        return statusList;
    }

    public void setStatusList(List<Integer> statusList) {
        this.statusList = statusList;
    }

    public Date getAuditStartTime() {
        return auditStartTime;
    }

    public void setAuditStartTime(Date auditStartTime) {
        this.auditStartTime = auditStartTime;
    }

    public Date getAuditEndTime() {
        return auditEndTime;
    }

    public void setAuditEndTime(Date auditEndTime) {
        this.auditEndTime = auditEndTime;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

	public boolean isRecentThreeDay() {
		return recentThreeDay;
	}

	public void setRecentThreeDay(boolean recentThreeDay) {
		this.recentThreeDay = recentThreeDay;
	}

	public String getCountMethod() {
		return countMethod;
	}

	public void setCountMethod(String countMethod) {
		this.countMethod = countMethod;
	}
}
