package com.tijiantest.model.order;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 全名营销返回订单对象
 */
public class OrderMarketingVo implements Serializable {
    /**
     * 订单id
     */
    private Integer orderId;

    /**
     * 套餐名称
     */
    private String mealName;

    /**
     * 体检人姓名
     */
    private String name;

    /**
     * 订单状态
     */
    private String status;
    /**
     * 医院名称
     */
    private String hospitalName;
    /**
     * 订单价格
     */
    private String orderPrice;

    /**
     * 体检日期
     */
    private String examTime;

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public String getMealName() {
        return mealName;
    }

    public void setMealName(String mealName) {
        this.mealName = mealName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getHospitalName() {
        return hospitalName;
    }

    public void setHospitalName(String hospitalName) {
        this.hospitalName = hospitalName;
    }

    public String getOrderPrice() {
        return orderPrice;
    }

    public void setOrderPrice(String orderPrice) {
        this.orderPrice = orderPrice;
    }

    public String getExamTime() {
        return examTime;
    }

    public void setExamTime(String examTime) {
        this.examTime = examTime;
    }
}

