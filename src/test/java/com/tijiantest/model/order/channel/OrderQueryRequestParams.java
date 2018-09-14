package com.tijiantest.model.order.channel;

import com.alibaba.fastjson.JSON;
import com.tijiantest.model.order.OrderQueryParams;
import com.tijiantest.model.organization.SiteTypeEnum;
import com.tijiantest.util.DateUtils;
import com.tijiantest.util.IdCardValidate;
import com.tijiantest.util.pagination.Page;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.text.ParseException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * 订单查询参数
 *
 * @create 2016年7月29日 上午11:41:16
 * @author tangyi
 * @version
 */
public class OrderQueryRequestParams {

	//订单IDS的字符串
	@Deprecated
	private String  orderIds;
	//订单IDS
	private List<Integer>  orderIdList;
	//订单编号
	private List<String> orderNums;
	//用户IDS
	private List<Integer> accountIds;
	//预约人IDS
	private List<Integer> ownerIds;
	//客户经理ID
	private List<Integer> managerIds;
	//体检中心id列表
	private List<Integer> hospitalIds;
	//单位ID
	private List<Integer> examCompanyIds;
	//渠道单位ID
	private List<Integer> channelCompanyIds;
	//订单状态多条件查询，注意不能区分已导出/已预约
	private List<Integer> orderStatuseList;
	//订单状态多条件查询，注意不能区分已导出/已预约
	@Deprecated
	private String  orderStatuses;
	// 结算标记状态ID
	private List<Integer> settleSigns;
	//机构ID
	private List<Integer> fromSites;
	//机构类型
	private List<Integer> fromSiteOrgTypes;


	//体检开始时间
	private String  examStartDate;
	//体检结束时间
	private String  examEndDate;
	//下单开始时间
	private String  insertStartDate;
	//下单结束时间
	private String  insertEndDate;
	//体检中心ID 请使用：hospitalIds
	@Deprecated
	private Integer hospitalId;
	//单位ID 请使用：examCompanyIds
	@Deprecated
	private Integer examCompanyId;
	//渠道单位ID 请使用：channelCompanyIds
	@Deprecated
	//渠道单位ID
	private Integer channelCompanyId;
	//用户ID  请使用：accountIds
	@Deprecated
	private Integer accountId;
	//性别
	private Integer gender;
	//订单状态 请使用：orderStatuseList
	@Deprecated
	private Integer orderStatus;
	//是否已导出
	private Boolean isExport;
	//现场应付是否为0
	private Boolean isOfflinePayMoneyZero;
	//现场还没有付的款
	private Boolean isOfflineUnpayMoneyZero;
	//线上自付是否为0
	private Boolean isSelfMoneyZero;
	//仅显示可导
	private Boolean showExportable;
	//结算状态  请使用：settleSigns
	@Deprecated
	private Integer settleSign;
	//结算批次
	private String settleBatch;
	//是否显示立刻导入订单
	private Boolean showImmediatelyImpOrder;
	//客户经理   请使用：managerIds
	@Deprecated
	private Integer managerId;
	//机构ID 请使用：fromSites
	@Deprecated
	private Integer fromSite;
	//机构类型：1：医院，2：渠道  请使用：fromSiteOrgTypes
	@Deprecated
	private Integer       fromSiteOrgType;
	//idCard or name  模糊查询
	private String nameOrIdCard;
	//体检人关键搜索
	private String keyWord;
	//当前页
	private Integer currentPage;
	//每页大小
	private Integer pageSize;

	public List<Integer> getOrderIdList() {
		return orderIdList;
	}

	public void setOrderIdList(List<Integer> orderIdList) {
		this.orderIdList = orderIdList;
	}

	public List<String> getOrderNums() {
		return orderNums;
	}

	public void setOrderNums(List<String> orderNums) {
		this.orderNums = orderNums;
	}

	public List<Integer> getAccountIds() {
		return accountIds;
	}

	public void setAccountIds(List<Integer> accountIds) {
		this.accountIds = accountIds;
	}

	public List<Integer> getOwnerIds() {
		return ownerIds;
	}

	public void setOwnerIds(List<Integer> ownerIds) {
		this.ownerIds = ownerIds;
	}

	public List<Integer> getManagerIds() {
		return managerIds;
	}

	public void setManagerIds(List<Integer> managerIds) {
		this.managerIds = managerIds;
	}

	public List<Integer> getExamCompanyIds() {
		return examCompanyIds;
	}

	public void setExamCompanyIds(List<Integer> examCompanyIds) {
		this.examCompanyIds = examCompanyIds;
	}

	public List<Integer> getChannelCompanyIds() {
		return channelCompanyIds;
	}

	public void setChannelCompanyIds(List<Integer> channelCompanyIds) {
		this.channelCompanyIds = channelCompanyIds;
	}

	public List<Integer> getOrderStatuseList() {
		return orderStatuseList;
	}

	public void setOrderStatuseList(List<Integer> orderStatuseList) {
		this.orderStatuseList = orderStatuseList;
	}

	public List<Integer> getSettleSigns() {
		return settleSigns;
	}

	public void setSettleSigns(List<Integer> settleSigns) {
		this.settleSigns = settleSigns;
	}

	public List<Integer> getFromSites() {
		return fromSites;
	}

	public void setFromSites(List<Integer> fromSites) {
		this.fromSites = fromSites;
	}

	public List<Integer> getFromSiteOrgTypes() {
		return fromSiteOrgTypes;
	}

	public void setFromSiteOrgTypes(List<Integer> fromSiteOrgTypes) {
		this.fromSiteOrgTypes = fromSiteOrgTypes;
	}

	public String getKeyWord() {
		return keyWord;
	}

	public void setKeyWord(String keyWord) {
		this.keyWord = keyWord;
	}

	public List<Integer> getHospitalIds() {
		return hospitalIds;
	}

	public void setHospitalIds(List<Integer> hospitalIds) {
		this.hospitalIds = hospitalIds;
	}

	public String getNameOrIdCard() {
		return nameOrIdCard;
	}

	public void setNameOrIdCard(String nameOrIdCard) {
		this.nameOrIdCard = nameOrIdCard;
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

	public String getExamStartDate() {
		return examStartDate;
	}

	public void setExamStartDate(String examStartDate) {
		this.examStartDate = examStartDate;
	}

	public String getExamEndDate() {
		return examEndDate;
	}

	public void setExamEndDate(String examEndDate) {
		this.examEndDate = examEndDate;
	}

	public String getInsertStartDate() {
		return insertStartDate;
	}

	public void setInsertStartDate(String insertStartDate) {
		this.insertStartDate = insertStartDate;
	}

	public String getInsertEndDate() {
		return insertEndDate;
	}

	public void setInsertEndDate(String insertEndDate) {
		this.insertEndDate = insertEndDate;
	}

	public Integer getHospitalId() {
		return hospitalId;
	}

	public void setHospitalId(Integer hospitalId) {
		this.hospitalId = hospitalId;
	}

	public Integer getExamCompanyId() {
		return examCompanyId;
	}

	public void setExamCompanyId(Integer examCompanyId) {
		this.examCompanyId = examCompanyId;
	}

	public Integer getAccountId() {
		return accountId;
	}

	public void setAccountId(Integer accountId) {
		this.accountId = accountId;
	}

	public Integer getGender() {
		return gender;
	}

	public void setGender(Integer gender) {
		this.gender = gender;
	}

	public Integer getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(Integer orderStatus) {
		this.orderStatus = orderStatus;
	}

	public String getOrderStatuses() {
		return orderStatuses;
	}

	public void setOrderStatuses(String orderStatuses) {
		this.orderStatuses = orderStatuses;
	}

	public Boolean getIsExport() {
		return isExport;
	}

	public void setIsExport(Boolean isExport) {
		this.isExport = isExport;
	}

	public Boolean getIsOfflinePayMoneyZero() {
		return isOfflinePayMoneyZero;
	}

	public void setIsOfflinePayMoneyZero(Boolean isOfflinePayMoneyZero) {
		this.isOfflinePayMoneyZero = isOfflinePayMoneyZero;
	}

	public String getOrderIds() {
		return orderIds;
	}

	public void setOrderIds(String orderIds) {
		this.orderIds = orderIds;
	}

	public Integer getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(Integer currentPage) {
		this.currentPage = currentPage;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public Boolean getShowExportable() {
		return showExportable;
	}

	public void setShowExportable(Boolean showExportable) {
		this.showExportable = showExportable;
	}

	public Boolean getIsSelfMoneyZero() {
		return isSelfMoneyZero;
	}

	public void setIsSelfMoneyZero(Boolean isSelfMoneyZero) {
		this.isSelfMoneyZero = isSelfMoneyZero;
	}

	public Integer getSettleSign() {
		return settleSign;
	}

	public void setSettleSign(Integer settleSign) {
		this.settleSign = settleSign;
	}

	public String getSettleBatch() {
		return settleBatch;
	}

	public void setSettleBatch(String settleBatch) {
		this.settleBatch = settleBatch;
	}

	public Boolean getShowImmediatelyImpOrder() {
		return showImmediatelyImpOrder;
	}

	public void setShowImmediatelyImpOrder(Boolean showImmediatelyImpOrder) {
		this.showImmediatelyImpOrder = showImmediatelyImpOrder;
	}

	public Integer getManagerId() {
		return managerId;
	}

	public void setManagerId(Integer managerId) {
		this.managerId = managerId;
	}

	public Boolean getIsOfflineUnpayMoneyZero() {
		return isOfflineUnpayMoneyZero;
	}

	public void setIsOfflineUnpayMoneyZero(Boolean isOfflineUnpayMoneyZero) {
		this.isOfflineUnpayMoneyZero = isOfflineUnpayMoneyZero;
	}

	public Integer getChannelCompanyId() {
		return channelCompanyId;
	}

	public void setChannelCompanyId(Integer channelCompanyId) {
		this.channelCompanyId = channelCompanyId;
	}

	public OrderQueryParams convertToQuery() throws ParseException {
		OrderQueryParams orderQueryParams = new OrderQueryParams();

		//订单ID
		if (StringUtils.isNotBlank(orderIds)) {
			orderQueryParams.setOrderIds(JSON.parseArray(orderIds, Integer.class));
		}
		if (CollectionUtils.isNotEmpty(orderIdList)) {
			orderQueryParams.setOrderIds(orderIdList);
		}
		//订单编号
		if (CollectionUtils.isNotEmpty(orderNums)) {
			orderQueryParams.setOrderNums(orderNums);
		}
		// 用户查询
		if (Objects.nonNull(accountId)) {
			orderQueryParams.setAccountIds(Collections.singletonList(accountId));
		}
		if (CollectionUtils.isNotEmpty(accountIds)) {
			orderQueryParams.setAccountIds(accountIds);
		}
		//预约人IDS
		if (CollectionUtils.isNotEmpty(ownerIds)) {
			orderQueryParams.setOwnerIds(ownerIds);
		}
		//客户经理ID
		if (Objects.nonNull(managerId)) {
			orderQueryParams.setManagerIds(Collections.singletonList(managerId));
			orderQueryParams.setManagerId(managerId);
		}
		if (CollectionUtils.isNotEmpty(managerIds)) {
			orderQueryParams.setManagerIds(managerIds);
		}
		//医院
		if (Objects.nonNull(hospitalId)) {
			orderQueryParams.setHospitalIds(Collections.singletonList(hospitalId));
			orderQueryParams.setHospitalId(hospitalId);
		}
		if (CollectionUtils.isNotEmpty(hospitalIds)) {
			orderQueryParams.setHospitalIds(hospitalIds);
		}
		//单位ID
		if (Objects.nonNull(examCompanyId)) {
			orderQueryParams.setExamCompanyIds(Collections.singletonList(examCompanyId));
		}
		if (CollectionUtils.isNotEmpty(examCompanyIds)) {
			orderQueryParams.setExamCompanyIds(examCompanyIds);
		}
		//渠道单位ID
		if (Objects.nonNull(channelCompanyId)) {
			orderQueryParams.setChannelCompanyIds(Collections.singletonList(channelCompanyId));
		}
		if (CollectionUtils.isNotEmpty(channelCompanyIds)) {
			orderQueryParams.setChannelCompanyIds(channelCompanyIds);
		}
		//订单状态
		if (Objects.nonNull(orderStatus)) {
			orderQueryParams.setOrderStatuses(Collections.singletonList(orderStatus));
			orderQueryParams.setOrderStatus(orderStatus);
		}
		if (StringUtils.isNotBlank(orderStatuses)) {
			orderQueryParams.setOrderStatuses(JSON.parseArray(orderStatuses, Integer.class));
		}
		if (CollectionUtils.isNotEmpty(orderStatuseList)) {
			orderQueryParams.setOrderStatuses(orderStatuseList);
		}
		// 结算标记状态ID
		if (Objects.nonNull(settleSign)) {
			orderQueryParams.setSettleSigns(Collections.singletonList(settleSign));
			orderQueryParams.setSettleSign(settleSign);
		}
		if (CollectionUtils.isNotEmpty(settleSigns)) {
			orderQueryParams.setSettleSigns(settleSigns);
		}
		//机构ID【fromSite=-1表示只查看渠道的订单，会重置fromSite和fromSiteOrgType的值】
		Integer QUERY_CHANNEL = -1;
		if (Objects.equals(fromSite, QUERY_CHANNEL) || (CollectionUtils.isNotEmpty(fromSites) && fromSites.contains(QUERY_CHANNEL))) {
			orderQueryParams.setFromSiteOrgTypes(Collections.singletonList(SiteTypeEnum.FOR_CHANNEL.getCode()));
		} else {
			//机构ID
			if (Objects.nonNull(fromSite)) {
				orderQueryParams.setFromSites(Collections.singletonList(fromSite));
				orderQueryParams.setFromSite(fromSite);
			}
			if (CollectionUtils.isNotEmpty(fromSites)) {
				orderQueryParams.setFromSites(fromSites);
			}
			//机构类型
			if (Objects.nonNull(fromSiteOrgType)) {
				orderQueryParams.setFromSiteOrgTypes(Collections.singletonList(fromSiteOrgType));
				orderQueryParams.setFromSiteOrgType(fromSiteOrgType);
			}
			if (CollectionUtils.isNotEmpty(fromSiteOrgTypes)) {
				orderQueryParams.setFromSiteOrgTypes(fromSiteOrgTypes);
			}
		}


		//体检日期
		if (StringUtils.isNotBlank(examStartDate)) {
			orderQueryParams
					.setExamStartDate(DateUtils.parse("yyyy-MM-dd HH:mm:ss", examStartDate));
		}
		if (StringUtils.isNotBlank(examEndDate)) {
			orderQueryParams.setExamEndDate(DateUtils.parse("yyyy-MM-dd HH:mm:ss", examEndDate));
		}
		//下单日期
		if (StringUtils.isNotBlank(insertStartDate)) {
			orderQueryParams.setInsertStartDate(DateUtils.parse("yyyy-MM-dd HH:mm:ss", insertStartDate));
		}
		if (StringUtils.isNotBlank(insertEndDate)) {
			orderQueryParams.setInsertEndDate(DateUtils.parse("yyyy-MM-dd HH:mm:ss", insertEndDate));
		}
		//其他非集合操作
		orderQueryParams.setGender(gender);
		orderQueryParams.setIsExport(isExport);
		orderQueryParams.setIsOfflinePayMoneyZero(isOfflinePayMoneyZero);
		orderQueryParams.setIsSelfMoneyZero(isSelfMoneyZero);
		orderQueryParams.setShowExportable(showExportable);
		orderQueryParams.setIsOfflineUnpayMoneyZero(isOfflineUnpayMoneyZero);
		orderQueryParams.setShowImmediatelyImpOrder(showImmediatelyImpOrder);
		orderQueryParams.setSettleBatch(settleBatch);
		orderQueryParams.setKeyWord(keyWord);

		//姓名或身份证查询
		if (!StringUtils.isEmpty(nameOrIdCard)){
			if (IdCardValidate.isIdcard(nameOrIdCard)){
				orderQueryParams.setIdCard(nameOrIdCard);
			}else {
				orderQueryParams.setAccountName(nameOrIdCard);
			}
		}
		//分页信息
		if (currentPage != null && pageSize != null) {
			Page page = new Page();
			page.setCurrentPage(currentPage);
			page.setPageSize(pageSize);
			orderQueryParams.setPage(page);
		}
		return orderQueryParams;
	}

}
