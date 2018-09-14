package com.tijiantest.model.settlement;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author mawanqun
 */
public class PaymentOrder implements Serializable {

    private static final long serialVersionUID = 5135736121446603962L;
    private Integer id;
    /**
     * 付款订单号
     */
    private String orderNum;

    /**
     * 医院id
     */
    private Integer hospitalId;
    

    /**
     * 单位id
     */
    private Integer companyId;

    /**
     * 付款人姓名
     */
    private String name;
    
    /**
     * 付款人姓名
     */
    private String payment_name;

    /**
     * 订单状态：0未支付，1已支付，2支付中，3已撤销 ，4部分退款
     */
    private Integer status;
    
    /**
     * 订单编号
     */
    private Integer order_num;

    /**
     * 付款金额
     */
    private Long amount;

    /**
     * 退款金额
     */
    private Long refundAmount;

    /**
     * 客户经理id
     */
    private Integer managerId;

    /**
     * 客户经理姓名
     */
    private String managerName;

    /**
     * 用户备注
     */
    private String remark;



    /**
     * 医院备注
     */
    private String hospitalRemark;

    /**
     * 结算状态：0未结算1 已结算
     */
    private Integer settlementStatus;

    private String settlementBatchSn;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 支付订单流转日志
     */
    private List<TradeCommonLogResultDTO> circulationLog = new ArrayList<>();


    public String getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(String orderNum) {
        this.orderNum = orderNum;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public Integer getManagerId() {
        return managerId;
    }

    public void setManagerId(Integer managerId) {
        this.managerId = managerId;
    }

    public String getManagerName() {
        return managerName;
    }

    public void setManagerName(String managerName) {
        this.managerName = managerName;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Integer getSettlementStatus() {
        return settlementStatus;
    }

    public void setSettlementStatus(Integer settlementStatus) {
        this.settlementStatus = settlementStatus;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<TradeCommonLogResultDTO> getCirculationLog() {
        return circulationLog;
    }

    public void setCirculationLog(List<TradeCommonLogResultDTO> circulationLog) {
        this.circulationLog = circulationLog;
    }

    public Long getRefundAmount() {
        return refundAmount;
    }

    public void setRefundAmount(Long refundAmount) {
        this.refundAmount = refundAmount;
    }

    public String getSettlementBatchSn() {
        return settlementBatchSn;
    }

    public void setSettlementBatchSn(String settlementBatchSn) {
        this.settlementBatchSn = settlementBatchSn;
    }


	public String getpayment_name() {
        return payment_name;
    }

    public void setpayment_name(String payment_name) {
        this.payment_name = payment_name;
    }

    public String getHospitalRemark() {
        return hospitalRemark;
    }

    public void setHospitalRemark(String hospitalRemark) {
        this.hospitalRemark = hospitalRemark;
    }
}