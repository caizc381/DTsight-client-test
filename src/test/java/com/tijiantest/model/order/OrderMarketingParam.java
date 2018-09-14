package com.tijiantest.model.order;


import com.tijiantest.model.payment.invoice.Invoice;

import java.util.List;

/**
 * 全民营销订单支付参数
 */
public class OrderMarketingParam {


    /**
     * 全民营销订单支付参数
     */

    private String name;
    private String mobile;
    private String idCard;
    private Integer marriageStatus;
    private Integer mealId;

    private List<Integer> itemIds;
    private String examDate;
    private String validationCode;

    private  List<Integer> packageIds;
    private Boolean needPaperReport;
    private Integer examTimeIntervalId;
    private Boolean isInLocation;
    private String wxOpenId;

    /**
     * 套餐金额
     *
     * @return
     */
    private Integer mealPrice;

    /**
     * 全民营销客户经理id
     */
    private Integer marketingManagerId;

    public Integer getMarketingManagerId() {
        return marketingManagerId;
    }

    public void setMarketingManagerId(Integer marketingManagerId) {
        this.marketingManagerId = marketingManagerId;
    }

    private Integer orderId;

    /**
     * 新的支付系统暂时不支持使用多张卡支付，所有支付方式都为入口卡？。，
     */
    @Deprecated
    private List<Integer> idcards; //

    // 入口卡
    private Integer selectedCardId;
    private boolean useBalance;
    private String orderSnapId;

    private Invoice invoice;
    private Integer payType; // 3支付宝、4微信、7线下支付
    private String client; // wap\pc\wx 手机端、pc端、微信端
    private String subSite;
    private Integer hospitalId;

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public List<Integer> getIdcards() {
        return idcards;
    }

    public void setIdcards(List<Integer> idcards) {
        this.idcards = idcards;
    }

    public Integer getSelectedCardId() {
        return selectedCardId;
    }

    public void setSelectedCardId(Integer selectedCardId) {
        this.selectedCardId = selectedCardId;
    }

    public boolean isUseBalance() {
        return useBalance;
    }

    public void setUseBalance(boolean useBalance) {
        this.useBalance = useBalance;
    }

    public String getOrderSnapId() {
        return orderSnapId;
    }

    public void setOrderSnapId(String orderSnapId) {
        this.orderSnapId = orderSnapId;
    }

    public Invoice getInvoice() {
        return invoice;
    }

    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
    }

    public Integer getPayType() {
        return payType;
    }

    public void setPayType(Integer payType) {
        this.payType = payType;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public String getSubSite() {
        return subSite;
    }

    public void setSubSite(String subSite) {
        this.subSite = subSite;
    }

    public Integer getHospitalId() {
        return hospitalId;
    }

    public void setHospitalId(Integer hospitalId) {
        this.hospitalId = hospitalId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public Integer getMarriageStatus() {
        return marriageStatus;
    }

    public void setMarriageStatus(Integer marriageStatus) {
        this.marriageStatus = marriageStatus;
    }

    public Integer getMealId() {
        return mealId;
    }

    public void setMealId(Integer mealId) {
        this.mealId = mealId;
    }

    public List<Integer> getItemIds() {
        return itemIds;
    }

    public void setItemIds(List<Integer> itemIds) {
        this.itemIds = itemIds;
    }

    public String getExamDate() {
        return examDate;
    }

    public void setExamDate(String examDate) {
        this.examDate = examDate;
    }

    public String getValidationCode() {
        return validationCode;
    }

    public void setValidationCode(String validationCode) {
        this.validationCode = validationCode;
    }

    public List<Integer> getPackageIds() {
        return packageIds;
    }

    public void setPackageIds(List<Integer> packageIds) {
        this.packageIds = packageIds;
    }

    public Boolean getNeedPaperReport() {
        return needPaperReport;
    }

    public void setNeedPaperReport(Boolean needPaperReport) {
        this.needPaperReport = needPaperReport;
    }

    public Integer getExamTimeIntervalId() {
        return examTimeIntervalId;
    }

    public void setExamTimeIntervalId(Integer examTimeIntervalId) {
        this.examTimeIntervalId = examTimeIntervalId;
    }

    public Boolean getInLocation() {
        return isInLocation;
    }

    public void setInLocation(Boolean inLocation) {
        isInLocation = inLocation;
    }

    public String getWxOpenId() {
        return wxOpenId;
    }

    public void setWxOpenId(String wxOpenId) {
        this.wxOpenId = wxOpenId;
    }

    public Integer getMealPrice() {
        return mealPrice;
    }

    public void setMealPrice(Integer mealPrice) {
        this.mealPrice = mealPrice;
    }
}
