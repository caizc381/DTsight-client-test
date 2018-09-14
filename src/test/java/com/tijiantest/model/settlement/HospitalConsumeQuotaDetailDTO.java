package com.tijiantest.model.settlement;

import java.io.Serializable;
import java.util.List;

import com.tijiantest.util.pagination.Page;

/**
 * Created by wangzhongxing on 2017/12/5.
 */
public class HospitalConsumeQuotaDetailDTO implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * 医院消费额度明细统计信息
     */
    private TradeConsumeQuotaStatistics consumeQuotaStatistics;

    /**
     * 医院消费额度明细
     */
    private List<TradeConsumeQuotaDetail> consumeQuotaDetails;

    private Page page;

    public TradeConsumeQuotaStatistics getConsumeQuotaStatistics() {
        return consumeQuotaStatistics;
    }

    public void setConsumeQuotaStatistics(TradeConsumeQuotaStatistics consumeQuotaStatistics) {
        this.consumeQuotaStatistics = consumeQuotaStatistics;
    }

    public List<TradeConsumeQuotaDetail> getConsumeQuotaDetails() {
        return consumeQuotaDetails;
    }

    public void setConsumeQuotaDetails(List<TradeConsumeQuotaDetail> consumeQuotaDetails) {
        this.consumeQuotaDetails = consumeQuotaDetails;
    }

    public Page getPage() {
        return page;
    }

    public void setPage(Page page) {
        this.page = page;
    }
}
