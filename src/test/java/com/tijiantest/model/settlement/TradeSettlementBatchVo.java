package com.tijiantest.model.settlement;

public class TradeSettlementBatchVo {
    private Integer id;
    private String sn;
    private Integer channelId;
    private Integer channelCompanyId;
    private String companyName;
    private String organizationName;
    private Integer settlementStatus;
    private String operatorName;
    private Integer settlementViewType;
    private Integer totalOrderPrice;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public Integer getChannelId() {
        return channelId;
    }

    public void setChannelId(Integer channelId) {
        this.channelId = channelId;
    }

    public Integer getChannelCompanyId() {
        return channelCompanyId;
    }

    public void setChannelCompanyId(Integer channelCompanyId) {
        this.channelCompanyId = channelCompanyId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public Integer getSettlementStatus() {
        return settlementStatus;
    }

    public void setSettlementStatus(Integer settlementStatus) {
        this.settlementStatus = settlementStatus;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public Integer getSettlementViewType() {
        return settlementViewType;
    }

    public void setSettlementViewType(Integer settlementViewType) {
        this.settlementViewType = settlementViewType;
    }

    public Integer getTotalOrderPrice() {
        return totalOrderPrice;
    }

    public void setTotalOrderPrice(Integer totalOrderPrice) {
        this.totalOrderPrice = totalOrderPrice;
    }
}
