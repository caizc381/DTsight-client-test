package com.tijiantest.model.settlement;

import java.io.Serializable;
import java.util.Date;

public class TradePrepaymentRecordVO extends  TradePrepaymentRecord {

    private String channelSettlementBatch;

    private Integer channelSettlementStatus;

    private Boolean channelPlatformCompany;

    public String getChannelSettlementBatch() {
        return channelSettlementBatch;
    }

    public void setChannelSettlementBatch(String channelSettlementBatch) {
        this.channelSettlementBatch = channelSettlementBatch;
    }

    public Integer getChannelSettlementStatus() {
        return channelSettlementStatus;
    }

    public void setChannelSettlementStatus(Integer channelSettlementStatus) {
        this.channelSettlementStatus = channelSettlementStatus;
    }

    public Boolean getChannelPlatformCompany() {
        return channelPlatformCompany;
    }

    public void setChannelPlatformCompany(Boolean channelPlatformCompany) {
        this.channelPlatformCompany = channelPlatformCompany;
    }
}
