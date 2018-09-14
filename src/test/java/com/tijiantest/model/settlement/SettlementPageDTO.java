package com.tijiantest.model.settlement;



import java.io.Serializable;
import java.util.List;

import com.tijiantest.util.pagination.Page;

/**
 * Created by wangzhongxing on 2017/8/4.
 */
public class SettlementPageDTO<T, K> implements Serializable {

    private static final long serialVersionUID = -1L;

    private List<T> records;

    private List<K> totleIds;

    private Page page;

    public Page getPage() {
        return page;
    }

    public void setPage(Page page) {
        this.page = page;
    }

    public List<T> getRecords() {
        return records;
    }

    public void setRecords(List<T> records) {
        this.records = records;
    }

    public List<K> getTotleIds() {
        return totleIds;
    }

    public void setTotleIds(List<K> totleIds) {
        this.totleIds = totleIds;
    }
}
