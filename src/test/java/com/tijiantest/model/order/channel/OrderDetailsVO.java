package com.tijiantest.model.order.channel;

import java.util.List;
import java.util.Map;

import com.tijiantest.model.item.ExamItem;
import com.tijiantest.model.resource.meal.ExamItemSnap;

/**
 * 订单详情vo,包含订单的基本信息，以及加项减项
 */
public class OrderDetailsVO {
    //订单
    @SuppressWarnings("rawtypes")
	private Map order;

    //单项快照
    private Map<Integer, ExamItemSnap> itemSnapMap;

    //订单价格
    private Integer orderPrice;

    //未检项目
    private List<ExamItem> unExamItems;

    //拒绝检查项目
    private List<ExamItem> refusedItems;

    //拒绝检查项目价格
    private Integer refusedItemsPrice;

    //未检查项目价格
    private Integer unExamItemsPrice;

    @SuppressWarnings("rawtypes")
	public Map getOrder() {
        return order;
    }

    @SuppressWarnings("rawtypes")
	public void setOrder(Map order) {
        this.order = order;
    }

    public Map<Integer, ExamItemSnap> getItemSnapMap() {
        return itemSnapMap;
    }

    public void setItemSnapMap(Map<Integer, ExamItemSnap> itemSnapMap) {
        this.itemSnapMap = itemSnapMap;
    }

    public Integer getOrderPrice() {
        return orderPrice;
    }

    public void setOrderPrice(Integer orderPrice) {
        this.orderPrice = orderPrice;
    }

    public List<ExamItem> getUnExamItems() {
        return unExamItems;
    }

    public void setUnExamItems(List<ExamItem> unExamItems) {
        this.unExamItems = unExamItems;
    }

    public List<ExamItem> getRefusedItems() {
        return refusedItems;
    }

    public void setRefusedItems(List<ExamItem> refusedItems) {
        this.refusedItems = refusedItems;
    }

    public Integer getRefusedItemsPrice() {
        return refusedItemsPrice;
    }

    public void setRefusedItemsPrice(Integer refusedItemsPrice) {
        this.refusedItemsPrice = refusedItemsPrice;
    }

    public Integer getUnExamItemsPrice() {
        return unExamItemsPrice;
    }

    public void setUnExamItemsPrice(Integer unExamItemsPrice) {
        this.unExamItemsPrice = unExamItemsPrice;
    }
}
