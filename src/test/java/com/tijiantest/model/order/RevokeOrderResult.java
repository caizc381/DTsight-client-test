package com.tijiantest.model.order;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 撤单返回结果集
 */
public class RevokeOrderResult implements Serializable{
    private static final long serialVersionUID = 5954436788136271498L;
    /**
     * 成功数
     */
    private List<Integer> successes;

    /**
     * 失败数
     */
    private List<FailOrder> fails = new ArrayList<>();

    public List<Integer> getSuccesses() {
        return successes;
    }

    public void setSuccesses(List<Integer> successes) {
        this.successes = successes;
    }

    public List<FailOrder> getFails() {
        return fails;
    }

    public void setFails(List<FailOrder> fails) {
        this.fails = fails;
    }

    public void addFailOrder(Integer orderId, String errMsg){
        FailOrder failOrder = new FailOrder(orderId,errMsg);
        fails.add(failOrder);
    }

}
