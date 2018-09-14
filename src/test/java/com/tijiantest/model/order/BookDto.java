package com.tijiantest.model.order;


import java.io.Serializable;
import java.util.Date;
import java.util.List;


public class BookDto implements Serializable{

    /**
     *
     */
    private static final long serialVersionUID = 7719198854469945691L;

    /**
     * 客户现场付款，1：是，0：否
     */
    private boolean isSitePay;

    /**
     * 隐藏价格，1：是，0：否
     */
    private boolean isHidePrice;

    /**
     * 允许减项，1：是，0：否
     */
    private boolean isReduceItem;

    /**
     * 可改期，1：是，0：否
     */
    private boolean isChangeDate;

    /**
     * 短信模板标识
     */
    private Integer msgTmplId;
    /**
     *是否需要发短信
     */
    private boolean isSendMsg;

    /**
     * 体检日期
     */
    private String examDate;

    /**
     * 预约时间段
     */
    private Integer examTimeIntervalId;
    /**
     * 预约时间段名称
     */
    private String examTimeIntervalName;

    /**
     * 套餐标识
     */
    private Integer mealId;

    /**
     * 预约账户列表
     */
    private List<Integer> accountIdList;

    private List<MealMultiChooseParam> mutiChooseParams;

    public List<MealMultiChooseParam> getMutiChooseParams() {
        return mutiChooseParams;
    }

    public void setMutiChooseParams(List<MealMultiChooseParam> mutiChooseParams) {
        this.mutiChooseParams = mutiChooseParams;
    }

    /**
     * 体检项目列表
     */
    private List<Integer> examItemIdList;

    /**
     * 用户判断是否需要校验同一账户30天内有订单
     */
    private boolean verifyFlag;

    /**
     * 1：选定时间段订单，2：自选日期订单，3：即时导入订单
     */
    private Integer bookType;

    /**
     * 已选时段订单
     */
    public static final Integer SELECTED_TIME_PERIOD=1;
    /**
     * 自选日期订单
     */
    public static final Integer SELECT_TIME_BY_SELF=2;
    /**
     * 及时导入
     */
    public static final Integer IMMEDIATELY_IMPORT=3;

    /**
     * 体检中心id
     */
    private Integer hospitalId;
    private String mealName;

    /**
     * 套餐原價
     */
    private Integer mealPrice;

    /**
     * 减少之后的价格
     */
    private Integer reducedPrice;

    /**
     * 差价
     */
    private  Integer adjustPrice;
    private String hospitalName;
    private String operator;
    private Integer mealGender;
    private Integer cardId;
    /**
     * 体检单位id
     */
    private Integer companyId;

    /**
     * 体检单位名称
     */
    private String companyName;
    /**
     * 客户筛选条件
     */
    private String queryCondition;

    /**
     * 导引单备注
     */
    private String remarks;


    private String smsMsgTemplate;

    /**
     * 时间备注
     */
    private String timeRemarks;

    /**
     * 单位付款
     */
    private Integer companyMoney;

    /**
     * 是否是极速预约
     */
    private boolean fastBook = false;
    /**
     * 是否开通打印导检单
     */
    private Boolean openPrintExamGuide;
    /**
     * 是否开通队列
     */
    private Boolean openQueue;
    /**
     * 操作人ID
     */
    private Integer operatorId;
    /**
     * 操作人类型
     */
    private Integer operatorType;

    /**
     * 是否线上支付。wx或ali
     */
    private boolean payOnline;

    /**
     * 下单场景场景orderSceneEnum
     */
    private int bookScene;

    /**
     * 订单来源机构id
     */
    private Integer fromSite;
    /**
     * 订单来源机构类型 OrganizationTypeEnum
     */
    private Integer fromSiteOrgType;


    public int getBookScene() {
        return bookScene;
    }

    public void setBookScene(int bookScene) {
        this.bookScene = bookScene;
    }

    public boolean isPayOnline() {
        return payOnline;
    }
    public void setPayOnline(boolean payOnline) {
        this.payOnline = payOnline;
    }
    public boolean isSitePay() {
        return isSitePay;
    }
    public void setSitePay(boolean isSitePay) {
        this.isSitePay = isSitePay;
    }
    public boolean isHidePrice() {
        return isHidePrice;
    }
    public void setHidePrice(boolean isHidePrice) {
        this.isHidePrice = isHidePrice;
    }
    public boolean isReduceItem() {
        return isReduceItem;
    }
    public void setReduceItem(boolean isReduceItem) {
        this.isReduceItem = isReduceItem;
    }
    public boolean isChangeDate() {
        return isChangeDate;
    }
    public void setChangeDate(boolean isChangeDate) {
        this.isChangeDate = isChangeDate;
    }
    public Integer getMsgTmplId() {
        return msgTmplId;
    }
    public void setMsgTmplId(Integer msgTmplId) {
        this.msgTmplId = msgTmplId;
    }
    public String getExamDate() {
        return examDate;
    }
    public void setExamDate(String examDate) {
        this.examDate = examDate;
    }
    public Integer getMealId() {
        return mealId;
    }
    public void setMealId(Integer mealId) {
        this.mealId = mealId;
    }
    public Integer getExamTimeIntervalId() {
        return examTimeIntervalId;
    }
    public void setExamTimeIntervalId(Integer examTimeIntervalId) {
        this.examTimeIntervalId = examTimeIntervalId;
    }
    public List<Integer> getAccountIdList() {
        return accountIdList;
    }
    public void setAccountIdList(List<Integer> accountIdList) {
        this.accountIdList = accountIdList;
    }
    public boolean getVerifyFlag() {
        return verifyFlag;
    }
    public void setVerifyFlag(boolean verifyFlag) {
        this.verifyFlag = verifyFlag;
    }
    public Integer getBookType() {
        return bookType;
    }
    public void setBookType(Integer bookType) {
        this.bookType = bookType;
    }
    public Integer getHospitalId() {
        return hospitalId;
    }
    public void setHospitalId(Integer hospitalId) {
        this.hospitalId = hospitalId;
    }
    public Integer getCompanyId() {
        return companyId;
    }
    public void setCompanyId(Integer companyId) {
        this.companyId = companyId;
    }
    public String getMealName() {
        return mealName;
    }
    public void setMealName(String mealName) {
        this.mealName = mealName;
    }
    public Integer getMealPrice() {
        return mealPrice;
    }
    public void setMealPrice(Integer mealPrice) {
        this.mealPrice = mealPrice;
    }
    public String getHospitalName() {
        return hospitalName;
    }
    public void setHospitalName(String hospitalName) {
        this.hospitalName = hospitalName;
    }
    public String getOperator() {
        return operator;
    }
    public void setOperator(String operator) {
        this.operator = operator;
    }
    public Integer getMealGender() {
        return mealGender;
    }
    public void setMealGender(Integer mealGender) {
        this.mealGender = mealGender;
    }
    public String getExamTimeIntervalName() {
        return examTimeIntervalName;
    }
    public void setExamTimeIntervalName(String examTimeIntervalName) {
        this.examTimeIntervalName = examTimeIntervalName;
    }
    public boolean isSendMsg() {
        return isSendMsg;
    }
    public void setSendMsg(boolean isSendMsg) {
        this.isSendMsg = isSendMsg;
    }
    public List<Integer> getExamItemIdList() {
        return examItemIdList;
    }
    public void setExamItemIdList(List<Integer> examItemIdList) {
        this.examItemIdList = examItemIdList;
    }
    public String getQueryCondition() {
        return queryCondition;
    }
    public void setQueryCondition(String queryCondition) {
        this.queryCondition = queryCondition;
    }
    public String getRemarks() {
        return remarks;
    }
    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getSmsMsgTemplate() {
        return smsMsgTemplate;
    }
    public void setSmsMsgTemplate(String smsMsgTemplate) {
        this.smsMsgTemplate = smsMsgTemplate;
    }
    public String getTimeRemarks() {
        return timeRemarks;
    }
    public void setTimeRemarks(String timeRemarks) {
        this.timeRemarks = timeRemarks;
    }
    public Integer getCardId() {
        return cardId;
    }
    public void setCardId(Integer cardId) {
        this.cardId = cardId;
    }
    public Integer getCompanyMoney() {
        return companyMoney;
    }
    public void setCompanyMoney(Integer companyMoney) {
        this.companyMoney = companyMoney;
    }
    public boolean isFastBook() {
        return fastBook;
    }
    public void setFastBook(boolean fastBook) {
        this.fastBook = fastBook;
    }
    public Boolean getOpenPrintExamGuide() {
        return openPrintExamGuide;
    }
    public void setOpenPrintExamGuide(Boolean openPrintExamGuide) {
        this.openPrintExamGuide = openPrintExamGuide;
    }
    public Boolean getOpenQueue() {
        return openQueue;
    }
    public void setOpenQueue(Boolean openQueue) {
        this.openQueue = openQueue;
    }
    public Integer getOperatorId() {
        return operatorId;
    }
    public void setOperatorId(Integer operatorId) {
        this.operatorId = operatorId;
    }
    public Integer getOperatorType() {
        return operatorType;
    }
    public void setOperatorType(Integer operatorType) {
        this.operatorType = operatorType;
    }

    public Integer getFromSite() {
        return fromSite;
    }

    public void setFromSite(Integer fromSite) {
        this.fromSite = fromSite;
    }

    public Integer getFromSiteOrgType() {
        return fromSiteOrgType;
    }

    public void setFromSiteOrgType(Integer fromSiteOrgType) {
        this.fromSiteOrgType = fromSiteOrgType;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public Integer getReducedPrice() {
        return reducedPrice;
    }

    public void setReducedPrice(Integer reducedPrice) {
        this.reducedPrice = reducedPrice;
    }

    public Integer getAdjustPrice() {
        return adjustPrice;
    }

    public void setAdjustPrice(Integer adjustPrice) {
        this.adjustPrice = adjustPrice;
    }
}
