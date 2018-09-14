package com.tijiantest.model.settlement;



import java.io.Serializable;
import java.util.List;

import com.tijiantest.util.pagination.Page;

public class SettlementBatchQueryDTO implements Serializable {


	/**
	 * 
	 */
	private static final long serialVersionUID = -1837782463336031601L;

	/**
     * 机构ID
     */
    private Integer organizationId;

    private List<Integer> organizationIds;

    /**
     * 单位ID
     */
    private Integer companyId;

    /**
     * 结算支付记录流水
     */
    private String paymentRecordSn;

    /**
     * 状态
     */
    private Integer status;

    private List<Integer> statues;

    /**
     * 省
     */
    private Integer provinceId;

    /**
     * 市
     */
    private Integer cityId;

    /**
     * 区
     */
    private Integer districtId;

    private Page page;

    /**
     * 开始时间
     */
    private String startTime;

    /**
     * 结束时间
     */
    private String endTime;

    /**
     * 单位支付
     */
    private Integer companyPay;

    /**
     * 线上支付
     */
    private Integer onlinePay;

    /**
     * 平台支付
     */
    private Integer platformPay;

    /**
     * 单位退款
     */
    private Integer companyRefund;

    /**
     * 线上退款
     */
    private Integer onlineRefund;

    /**
     * 平台退款
     */
    private Integer platformRefund;

    /**
     * 特殊退款
     */
    private Integer prepayment;
    private Integer type;
    private Integer settlementViewType;
    private Integer channelId;
    private String channelCompanyIds;
    public SettlementBatchQueryDTO() {
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getSettlementViewType() {
        return settlementViewType;
    }

    public void setSettlementViewType(Integer settlementViewType) {
        this.settlementViewType = settlementViewType;
    }

    public Integer getChannelId() {
        return channelId;
    }

    public void setChannelId(Integer channelId) {
        this.channelId = channelId;
    }

    public String getChannelCompanyIds() {
        return channelCompanyIds;
    }

    public void setChannelCompanyIds(String channelCompanyIds) {
        this.channelCompanyIds = channelCompanyIds;
    }

    public Integer getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Integer organizationId) {
        this.organizationId = organizationId;
    }

    public Integer getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Integer companyId) {
        this.companyId = companyId;
    }

    public String getPaymentRecordSn() {
        return paymentRecordSn;
    }

    public void setPaymentRecordSn(String paymentRecordSn) {
        this.paymentRecordSn = paymentRecordSn;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public List<Integer> getOrganizationIds() {
        return organizationIds;
    }

    public void setOrganizationIds(List<Integer> organizationIds) {
        this.organizationIds = organizationIds;
    }

    public List<Integer> getStatues() {
        return statues;
    }

    public void setStatues(List<Integer> statues) {
        this.statues = statues;
    }

    public Integer getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(Integer provinceId) {
        this.provinceId = provinceId;
    }

    public Integer getCityId() {
        return cityId;
    }

    public void setCityId(Integer cityId) {
        this.cityId = cityId;
    }

    public Integer getDistrictId() {
        return districtId;
    }

    public void setDistrictId(Integer districtId) {
        this.districtId = districtId;
    }

    public Page getPage() {
        return page;
    }

    public void setPage(Page page) {
        this.page = page;
    }

   

    public Integer getCompanyPay() {
        return companyPay;
    }

    public void setCompanyPay(Integer companyPay) {
        this.companyPay = companyPay;
    }

    public Integer getPrepayment() {
        return prepayment;
    }

    public void setPrepayment(Integer prepayment) {
        this.prepayment = prepayment;
    }

    public Integer getOnlinePay() {
        return onlinePay;
    }

    public void setOnlinePay(Integer onlinePay) {
        this.onlinePay = onlinePay;
    }

    public Integer getPlatformPay() {
        return platformPay;
    }

    public void setPlatformPay(Integer platformPay) {
        this.platformPay = platformPay;
    }

    public Integer getCompanyRefund() {
        return companyRefund;
    }

    public void setCompanyRefund(Integer companyRefund) {
        this.companyRefund = companyRefund;
    }

    public Integer getOnlineRefund() {
        return onlineRefund;
    }

    public void setOnlineRefund(Integer onlineRefund) {
        this.onlineRefund = onlineRefund;
    }

    public Integer getPlatformRefund() {
        return platformRefund;
    }

    public void setPlatformRefund(Integer platformRefund) {
        this.platformRefund = platformRefund;
    }

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
}
