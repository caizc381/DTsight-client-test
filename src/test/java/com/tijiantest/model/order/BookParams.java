package com.tijiantest.model.order;

import java.io.Serializable;
import java.util.List;

public class BookParams implements Serializable {
    private static final long serialVersionUID = 2983589820906932243L;
    private String examDate;
    private Integer accountId;
    private Integer examinerId;
    private Integer mealId;
    private List<Integer> itemIds;
    private List<Integer> packageIds;
    private Integer source;
    private Integer examTimeIntervalId;
    private Integer entryCardId;
    private Integer recidenceId;
    private Integer addressId;
    private String remark;
    private String maillingRecord;
    private Boolean needPaperReport;
    private Integer evaluateReportId;
    private Boolean isNologinBook;
    private Boolean isInformMe;
    private Boolean isInLocation;
    private Integer noLoginManagerId;
    private String receiveMsgMobile;
    private List<MealMultiChooseParam> mealMultiChooseParams;
    private Boolean continuedOrder;
    /**
     * 有值表示是微信城市服务的
     */
    private Integer scene;
    /**
     * 全民营销活动新加订单参数--套餐价格

     */
    private Integer mealPrice;
    /**
     * 全民营销客户经理id
     *
     * @param builder
     */
    private Integer marketingManagerId;
    private String idCard;
    /**\
     *     免登录使用的手机号

     */
    private String mobile;
    private Integer marriageStatus;
    private String validationCode;
    private String wxOpenId;
    private String name;

    public Integer getScene() {
        return scene;
    }

    public void setScene(Integer scene) {
        this.scene = scene;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWxOpenId() {
        return wxOpenId;
    }

    public void setWxOpenId(String wxOpenId) {
        this.wxOpenId = wxOpenId;
    }

    public String getValidationCode() {
        return validationCode;
    }

    public void setValidationCode(String validationCode) {
        this.validationCode = validationCode;
    }

    public Integer getMarriageStatus() {
        return marriageStatus;
    }

    public void setMarriageStatus(Integer marriageStatus) {
        this.marriageStatus = marriageStatus;
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

    public Integer getMealPrice() {
        return mealPrice;
    }

    public Boolean getContinuedOrder() {
        return continuedOrder;
    }

    public void setContinuedOrder(Boolean continuedOrder) {
        this.continuedOrder = continuedOrder;
    }

    public List<MealMultiChooseParam> getMealMultiChooseParams() {
        return mealMultiChooseParams;
    }

    public String getExamDate() {
        return examDate;
    }

    public Integer getAccountId() {
        return accountId;
    }

    public Integer getMealId() {
        return mealId;
    }

    public List<Integer> getItemIds() {
        return itemIds;
    }

    public List<Integer> getPackageIds() {
        return packageIds;
    }

    public Integer getSource() {
        return source;
    }

    public Integer getExamTimeIntervalId() {
        return examTimeIntervalId;
    }

    public Integer getEntryCardId() {
        return entryCardId;
    }

    public Integer getRecidenceId() {
        return recidenceId;
    }

    public Integer getAddressId() {
        return addressId;
    }

    public String getRemark() {
        return remark;
    }

    public String getMaillingRecord() {
        return maillingRecord;
    }

    public Boolean getNeedPaperReport() {
        return needPaperReport;
    }

    public Integer getEvaluateReportId() {
        return evaluateReportId;
    }

    public Boolean getIsNologinBook() {
        return isNologinBook;
    }

    public Boolean getIsInLocation() {
        return isInLocation;
    }

    public Boolean getInformMe() {
        return isInformMe;
    }

    public void setInformMe(Boolean informMe) {
        isInformMe = informMe;
    }

    public Integer getNoLoginManagerId() {
        return noLoginManagerId;
    }

    public String getReceiveMsgMobile() {
        return receiveMsgMobile;
    }

    public Integer getMarketingManagerId(){
        return marketingManagerId;}

    public Integer getExaminerId() {
        return examinerId;
    }

    public void setExaminerId(Integer examinerId) {
        this.examinerId = examinerId;
    }


    public void setExamDate(String examDate) {
        this.examDate = examDate;
    }

    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }

    public void setMealId(Integer mealId) {
        this.mealId = mealId;
    }

    public void setItemIds(List<Integer> itemIds) {
        this.itemIds = itemIds;
    }

    public void setPackageIds(List<Integer> packageIds) {
        this.packageIds = packageIds;
    }

    public void setSource(Integer source) {
        this.source = source;
    }

    public void setExamTimeIntervalId(Integer examTimeIntervalId) {
        this.examTimeIntervalId = examTimeIntervalId;
    }

    public void setEntryCardId(Integer entryCardId) {
        this.entryCardId = entryCardId;
    }

    public void setRecidenceId(Integer recidenceId) {
        this.recidenceId = recidenceId;
    }

    public void setAddressId(Integer addressId) {
        this.addressId = addressId;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public void setMaillingRecord(String maillingRecord) {
        this.maillingRecord = maillingRecord;
    }

    public void setNeedPaperReport(Boolean needPaperReport) {
        this.needPaperReport = needPaperReport;
    }

    public void setEvaluateReportId(Integer evaluateReportId) {
        this.evaluateReportId = evaluateReportId;
    }

    public Boolean getNologinBook() {
        return isNologinBook;
    }

    public void setNologinBook(Boolean nologinBook) {
        isNologinBook = nologinBook;
    }

    public Boolean getInLocation() {
        return isInLocation;
    }

    public void setInLocation(Boolean inLocation) {
        isInLocation = inLocation;
    }

    public void setNoLoginManagerId(Integer noLoginManagerId) {
        this.noLoginManagerId = noLoginManagerId;
    }

    public void setReceiveMsgMobile(String receiveMsgMobile) {
        this.receiveMsgMobile = receiveMsgMobile;
    }

    public void setMealMultiChooseParams(List<MealMultiChooseParam> mealMultiChooseParams) {
        this.mealMultiChooseParams = mealMultiChooseParams;
    }

    public void setMealPrice(Integer mealPrice) {
        this.mealPrice = mealPrice;
    }

    public void setMarketingManagerId(Integer marketingManagerId) {
        this.marketingManagerId = marketingManagerId;
    }
}
