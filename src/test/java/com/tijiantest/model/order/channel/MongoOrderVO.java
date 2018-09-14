package com.tijiantest.model.order.channel;

import java.util.Date;

import com.tijiantest.model.account.Account;
import com.tijiantest.model.account.AccountRelation;
import com.tijiantest.model.account.Examiner;
import com.tijiantest.model.hospital.Hospital;
import com.tijiantest.model.order.HospitalSnapshot;
import com.tijiantest.model.order.OrderExportExtInfoSnapshot;
import com.tijiantest.model.order.OrderExtInfoSnapshot;
import com.tijiantest.model.order.OrderGuideInfoSnapshot;

/**
 * 渠道商订单展示页面VO
 * @author Administrator
 *
 */
public class MongoOrderVO {

	private Integer id;//订单ID
	private Examiner examiner;//体检人关系
	private String examCompany;//体检单位
	private Integer examCompanyId;//单位id
	private Integer status;//订单状态
	private Integer orderPrice;//订单金额
	private String selfMoney;//线上自付
	private HospitalSnapshot orderHospital;//体检中心
	private OrderExportExtInfoSnapshot orderExportExtInfo;
	private OrderGuideInfoSnapshot orderGuideInfo;
	private OrderExtInfoSnapshot orderExtInfo;

	private String manager;//客户经理
	private String mealName;//套餐名称
	private String mealType;//套餐类型
	private Integer sign;//标记（标记为已结算，未结算）
	private String signBatchNo;//结算批次
	private Date examDate;//体检日期
	private String examTimeIntervalName;//体检时段
	private Integer examTimeIntervalId;//时段的ID
	private Date insertTime;//预约时间
	private Boolean isExport;//是否已经导出
	private String remark;//备注
	private String marriageStatusLabel;//婚姻状态
	private String operator;//操作人

	public Examiner getExaminer() {
		return examiner;
	}

	public void setExaminer(Examiner examiner) {
		this.examiner = examiner;
	}

	public Boolean getExport() {
		return isExport;
	}

	public void setExport(Boolean export) {
		isExport = export;
	}

	public String getExamCompany() {
		return examCompany;
	}
	public void setExamCompany(String examCompany) {
		this.examCompany = examCompany;
	}
	public Integer getExamCompanyId() {
		return examCompanyId;
	}
	public void setExamCompanyId(Integer examCompanyId) {
		this.examCompanyId = examCompanyId;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer orderStatus) {
		this.status = orderStatus;
	}
	public Integer getOrderPrice() {
		return orderPrice;
	}
	public void setOrderPrice(Integer orderPrice) {
		this.orderPrice = orderPrice;
	}
	public String getSelfMoney() {
		return selfMoney;
	}
	public void setSelfMoney(String selfMoney) {
		this.selfMoney = selfMoney;
	}

	public HospitalSnapshot getOrderHospital() {
		return orderHospital;
	}

	public void setOrderHospital(HospitalSnapshot orderHospital) {
		this.orderHospital = orderHospital;
	}

	public OrderExportExtInfoSnapshot getOrderExportExtInfo() {
		return orderExportExtInfo;
	}

	public void setOrderExportExtInfo(OrderExportExtInfoSnapshot orderExportExtInfo) {
		this.orderExportExtInfo = orderExportExtInfo;
	}

	public OrderGuideInfoSnapshot getOrderGuideInfo() {
		return orderGuideInfo;
	}

	public void setOrderGuideInfo(OrderGuideInfoSnapshot orderGuideInfo) {
		this.orderGuideInfo = orderGuideInfo;
	}

	public OrderExtInfoSnapshot getOrderExtInfo() {
		return orderExtInfo;
	}

	public void setOrderExtInfo(OrderExtInfoSnapshot orderExtInfo) {
		this.orderExtInfo = orderExtInfo;
	}

	public String getManager() {
		return manager;
	}
	public void setManager(String manager) {
		this.manager = manager;
	}
	public String getMealName() {
		return mealName;
	}
	public void setMealName(String mealName) {
		this.mealName = mealName;
	}
	public String getMealType() {
		return mealType;
	}
	public void setMealType(String mealType) {
		this.mealType = mealType;
	}
	public Integer getSign() {
		return sign;
	}
	public void setSign(Integer sign) {
		this.sign = sign;
	}
	public String getSignBatchNo() {
		return signBatchNo;
	}
	public void setSignBatchNo(String signBatchNo) {
		this.signBatchNo = signBatchNo;
	}
	public Date getExamDate() {
		return examDate;
	}
	public void setExamDate(Date examDate) {
		this.examDate = examDate;
	}
	public String getExamTimeIntervalName() {
		return examTimeIntervalName;
	}
	public void setExamTimeIntervalName(String examTimeIntervalName) {
		this.examTimeIntervalName = examTimeIntervalName;
	}
	public Integer getExamTimeIntervalId() {
		return examTimeIntervalId;
	}
	public void setExamTimeIntervalId(Integer examTimeIntervalId) {
		this.examTimeIntervalId = examTimeIntervalId;
	}
	public Date getInsertTime() {
		return insertTime;
	}
	public void setInsertTime(Date insertTime) {
		this.insertTime = insertTime;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getMarriageStatusLabel() {
		return marriageStatusLabel;
	}
	public void setMarriageStatusLabel(String marriageStatusLabel) {
		this.marriageStatusLabel = marriageStatusLabel;
	}
	public String getOperator() {
		return operator;
	}
	public void setOperator(String operator) {
		this.operator = operator;
	}
	public Boolean getIsExport() {
		return isExport;
	}
	public void setIsExport(Boolean isExport) {
		this.isExport = isExport;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	
}
