package com.tijiantest.model.order;

import com.tijiantest.model.order.MealMultiChooseParam;

import java.util.List;

public class ReplaceExamItemParam {

    /**
     * 订单编号
     */
    private String orderNum;
    /**
     * 多选一参数
     */
    private List<MealMultiChooseParam> newMealMultiChooseParams;

    public String getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(String orderNum) {
        this.orderNum = orderNum;
    }

    public List<MealMultiChooseParam> getNewMealMultiChooseParams() {
        return newMealMultiChooseParams;
    }

    public void setNewMealMultiChooseParams(List<MealMultiChooseParam> newMealMultiChooseParams) {
        this.newMealMultiChooseParams = newMealMultiChooseParams;
    }
}
