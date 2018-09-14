package com.tijiantest.base.dbcheck;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSONArray;
import com.fasterxml.jackson.databind.introspect.TypeResolutionContext;
import com.tijiantest.base.*;
import com.tijiantest.model.account.*;
import com.tijiantest.model.company.ChannelCompany;
import com.tijiantest.model.counter.BizExceptionEnum;
import com.tijiantest.model.examitempackage.ExamItemPackage;
import com.tijiantest.model.hospital.*;
import com.tijiantest.model.order.*;
import com.tijiantest.model.organization.OrganizationTypeEnum;
import com.tijiantest.model.order.snapshot.*;
import com.tijiantest.model.payment.trade.PayAmount;
import com.tijiantest.model.payment.trade.RefundAmount;
import com.tijiantest.model.resource.meal.*;
import com.tijiantest.model.settlement.*;
import com.tijiantest.util.*;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.testng.Assert;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.jayway.jsonpath.JsonPath;
import com.mongodb.BasicDBObject;
import com.tijiantest.model.common.SystemParam;
import com.tijiantest.model.company.HospitalCompany;
import com.tijiantest.model.item.ExamItem;
import com.tijiantest.model.order.OrderException.ExceptionItem;
import com.tijiantest.model.order.orderrefund.BatchOrderRefundAuditVO;
import com.tijiantest.model.paylog.PayConsts;
import com.tijiantest.model.paylog.PayLog;
import com.tijiantest.model.payment.invoice.InvoiceApply;
import com.tijiantest.model.payment.trade.PayConstants;
import com.tijiantest.util.db.DBMapper;
import com.tijiantest.util.db.MongoDBUtils;
import com.tijiantest.util.db.SqlException;

import javax.annotation.Resource;


/**
 * 订单校验
 * @author huifang
 *
 */
public class OrderChecker extends BaseTest {

	
	/**
	 * 根据订单id查询订单详细信息
	 * @param orderId
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static Order getOrderInfo(int orderId) {
		Order order = new Order();
		// String sql = "select * from tb_order where id = ?";
		String sqlStr = "SELECT " + "ord.id, " + "ord.account_id, " + "ord.batch_id," + "ord.order_price,"+"ord.source,"+"ord.operator_id,"+"ord.from_site_org_type,"
				+"ord.entry_card_id,ord.order_manager_id,"+"ord.from_site,"
				+ "ord.is_export," +"ord.remark," + "ord.status, "
				+ "ord.hospital_company_id," + "ord.channel_company_id,ord.old_exam_company_id,"+"ord.order_manager_id,"+"ord.account_manager_id,"
				+ "ord.exam_time_interval_id, ord.hospital_id," + "ord.order_num," + "ord.exam_date, " + "ord.discount,"+"ord.insert_time,"
				+ "ord.need_paper_report,ord.order_manager_id as managerId "
				+ "FROM tb_order ord "
				+ "WHERE ord.id = ? ";
		log.debug("sql..."+sqlStr+"--id"+orderId);
		List<Map<String, Object>> list = null;
		try {
			list = DBMapper.query(sqlStr, orderId);
			Map<String, Object> map = list.get(0);
			order.setId(orderId);
			if(map.get("batch_id")!=null)
				order.setBatchId(Integer.parseInt(map.get("batch_id").toString()));
			order.setStatus(Integer.parseInt(map.get("status").toString()));
			order.setOrderPrice(Integer.parseInt(map.get("order_price").toString()));
			int hospitalId = Integer.parseInt(map.get("hospital_id").toString());
			Hospital hospital = HospitalChecker.getHospitalById(hospitalId);
			HospitalSnapshot hospitalSnapshot = new HospitalSnapshot();
			hospitalSnapshot.setId(hospitalId);
			order.setHospital(hospital);
			order.setOrderHospital(hospitalSnapshot);
			log.debug("isExport..."+map.get("is_export").toString());
			Boolean isExport = map.get("is_export").equals(1) ? true : false;
			order.setIsExport(isExport);
			order.setSource(Integer.parseInt(map.get("source").toString()));
			if(map.get("need_paper_report")!=null){
				Boolean needPaperReport = map.get("need_paper_report").equals(1) ? true : false;
				order.setNeedPaperReport(needPaperReport);
			}
			if (map.get("remark") != null)
				order.setRemark(map.get("remark").toString());
			String orderNum = map.get("order_num").toString();
			order.setOrderNum(orderNum);
			order.setDiscount(Double.parseDouble(map.get("discount").toString()));
			order.setExamCompanyId(Integer.valueOf(map.get("hospital_company_id").toString()));
			int account_id = Integer.parseInt(map.get("account_id").toString());
			Account ac = AccountChecker.getAccountById(account_id);
			order.setAccount(ac);
			if(checkmongo){
				log.info("orderId."+orderId);
				List<Map<String,Object>> mogoList1 = MongoDBUtils.query("{'id':" + orderId + "}", MONGO_COLLECTION);
				if(mogoList1 == null || mogoList1.size() == 0){
					log.error("mongo中无此订单号"+orderId);
					return  null;
				}
				String jaccountSnap = mogoList1.get(0).get("orderAccount").toString();
				int accountSnapId = Integer.parseInt(JSON.parseObject(jaccountSnap).get("_id").toString());
				AccountSnapshot accountSnapshot = JSON.parseObject(jaccountSnap,AccountSnapshot.class);
				accountSnapshot.setId(accountSnapId);
				order.setOrderAccount(accountSnapshot);
				if(mogoList1.get(0).get("examDate")!=null)
					order.setExamDate(simplehms.parse(simplehms.format(mogoList1.get(0).get("examDate"))));
				if(mogoList1.get(0).get("insertTime")!=null)
					order.setInsertTime(simplehms.parse(simplehms.format(mogoList1.get(0).get("insertTime"))));
				if(mogoList1.get(0).get("examTimeIntervalName")!=null)
					order.setExamTimeIntervalName( mogoList1.get(0).get("examTimeIntervalName").toString());
				if(mogoList1.get(0).get("examTimeIntervalId")!=null)
					order.setExamTimeIntervalId(Integer.parseInt(mogoList1.get(0).get("examTimeIntervalId").toString()));

                //大字段item_detail,meal_detail,package_snapshot_detail
				net.sf.json.JSONObject mealSnapshotInMongoMeal = getMongoMealSnapshot(orderNum,"mealSnapshot");
				if(mealSnapshotInMongoMeal!=null)
					order.setMealDetail(mealSnapshotInMongoMeal.getString("value"));
				net.sf.json.JSONObject examItemSnapshotInMongoMeal = getMongoMealSnapshot(orderNum,"examItemSnapshot");
				if(examItemSnapshotInMongoMeal!=null)
					order.setItemsDetail(examItemSnapshotInMongoMeal.getString("value"));
				net.sf.json.JSONObject examItemPackageSnapshotInMongoMeal = getMongoMealSnapshot(orderNum,"examItemPackageSnapshot");
				if(examItemPackageSnapshotInMongoMeal!=null)
					order.setPackageSnapshotDetail(examItemPackageSnapshotInMongoMeal.getString("value"));

			}
			HospitalExamCompanySnapshot hospitalCompany = new HospitalExamCompanySnapshot();
			int hospitalCompanyId = Integer.parseInt(map.get("hospital_company_id").toString());
			HospitalCompany hc = CompanyChecker.getHospitalCompanyById(hospitalCompanyId);
			hospitalCompany.setId(hospitalCompanyId);
			hospitalCompany.setName(hc.getName());
			if(hc.getPlatformCompanyId()!=null)
				hospitalCompany.setPlatformCompanyId(hc.getPlatformCompanyId());
			order.setHospitalCompany(hospitalCompany);
			if (map.get("channel_company_id") != null){
				ChannelCompany channelCompany = CompanyChecker.getChannelCompanyByCompanyId(Integer.parseInt(map.get("channel_company_id").toString()));
				ChannelExamCompanySnapshot channelExamCompanySnapshot = new ChannelExamCompanySnapshot();
				channelExamCompanySnapshot.setId(channelCompany.getId());
				channelExamCompanySnapshot.setPlatformCompanyId(channelCompany.getPlatformCompanyId());
				channelExamCompanySnapshot.setName(channelCompany.getName());
				order.setChannelCompany(channelExamCompanySnapshot);
			}
			order.setExamCompanyId(hospitalCompanyId);
			if(map.get("operator_id")!=null)
				order.setOperatorId(Integer.parseInt(map.get("operator_id").toString()));
			Object entryCard = map.get("entry_card_id");
			if(entryCard!=null)
				order.setEntryCardId(Integer.parseInt(entryCard.toString()));
			if(map.get("managerId")!=null)
				order.setManagerId(Integer.parseInt(map.get("managerId").toString()));
//			Date insertTime = ChangeToDate.formatDate(map.get("insert_time").toString());
//			order.setInsertTime(insertTime);
			if(map.get("from_site")!=null)
				order.setFromSite(Integer.parseInt(map.get("from_site").toString()));
			if(map.get("from_site_org_type")!=null)
				order.setFromSiteOrgType(Integer.parseInt(map.get("from_site_org_type").toString()));
			OrderMealSnapshot orderMealSnapshot = getMealSnapShotByOrder(orderId);
			if(orderMealSnapshot!=null)
				order.setOrderMealSnapshot(orderMealSnapshot);
		} catch (SqlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return order;
	}
	
	
	
	@SuppressWarnings("deprecation")
	public static Order getOrderInfo(String orderNum) {
		Order order = new Order();
		// String sql = "select * from tb_order where id = ?";
		String sqlStr = "SELECT " + "ord.id, " + "ord.account_id, " + "ord.batch_id," + "ord.order_price,"+"ord.source,"+"ord.operator_id,"+"ord.from_site_org_type,"
				+"ord.entry_card_id,ord.order_manager_id,"+"ord.from_site,"
				+ "ord.is_export," + "ord.remark," + "ord.status, "
				+ "ord.hospital_company_id,ord.channel_company_id," + "ord.old_exam_company_id,"
				+ "ord.exam_time_interval_id, ord.hospital_id," + "ord.order_num," + "ord.exam_date, " + "ord.discount,"+"ord.account_manager_id,"
				+ "ord.need_paper_report "
				+ "FROM tb_order ord "
				+ "WHERE ord.order_num = ? ";
		List<Map<String, Object>> list = null;
		try {
			list = DBMapper.query(sqlStr, orderNum);
			Map<String, Object> map = list.get(0);
			int orderId = Integer.parseInt(map.get("id").toString());
			order.setId(orderId);
			if(map.get("batch_id")!=null)
				order.setBatchId(Integer.parseInt(map.get("batch_id").toString()));
			order.setStatus(Integer.valueOf(map.get("status").toString()));
			order.setOrderPrice(Integer.parseInt(map.get("order_price").toString()));
			log.debug("isExport..."+map.get("is_export").toString());
			Boolean isExport = map.get("is_export").equals(1) ? true : false;
			order.setIsExport(isExport);
			order.setSource(Integer.parseInt(map.get("source").toString()));
			Boolean needPaperReport = map.get("need_paper_report").equals(1) ? true : false;
			order.setNeedPaperReport(needPaperReport);
			if (map.get("remark") != null)
				order.setRemark(map.get("remark").toString());
			order.setOrderNum(map.get("order_num").toString());
			order.setDiscount(Double.parseDouble(map.get("discount").toString()));
			order.setExamCompanyId(Integer.valueOf(map.get("hospital_company_id").toString()));
			int account_id = Integer.parseInt(map.get("account_id").toString());
			Account ac = AccountChecker.getAccountById(account_id);
			order.setAccount(ac);
			if(checkmongo){
				String moSql = "{'id':"+orderId+"}";
				List<Map<String,Object>> mogoList1 = MongoDBUtils.query(moSql,MONGO_COLLECTION);
				if( mogoList1 != null && mogoList1.size()> 0){
					String jaccountSnap = mogoList1.get(0).get("orderAccount").toString();
					AccountSnapshot accountSnapshot = JSON.parseObject(jaccountSnap,AccountSnapshot.class);
					if(JSON.parseObject(jaccountSnap).get("_id")!=null){
						int accountSnapId = Integer.parseInt(JSON.parseObject(jaccountSnap).get("_id").toString());
						accountSnapshot.setId(accountSnapId);
					}
					order.setOrderAccount(accountSnapshot);
					if(mogoList1.get(0).get("examDate")!=null)
						order.setExamDate(simplehms.parse(simplehms.format(mogoList1.get(0).get("examDate"))));
					if(mogoList1.get(0).get("insertTime")!=null)
						order.setInsertTime(simplehms.parse(simplehms.format(mogoList1.get(0).get("insertTime"))));
					if(mogoList1.get(0).get("examTimeIntervalName")!=null)
						order.setExamTimeIntervalName( mogoList1.get(0).get("examTimeIntervalName").toString());
					if(mogoList1.get(0).get("examTimeIntervalId")!=null)
						order.setExamTimeIntervalId(Integer.parseInt(mogoList1.get(0).get("examTimeIntervalId").toString()));
				}

				//大字段item_detail,meal_detail,package_snapshot_detail
				net.sf.json.JSONObject mealSnapshotInMongoMeal = getMongoMealSnapshot(orderNum,"mealSnapshot");
				if(mealSnapshotInMongoMeal!=null)
					order.setMealDetail(mealSnapshotInMongoMeal.getString("value"));
				net.sf.json.JSONObject examItemSnapshotInMongoMeal = getMongoMealSnapshot(orderNum,"examItemSnapshot");
				if(examItemSnapshotInMongoMeal!=null)
					order.setItemsDetail(examItemSnapshotInMongoMeal.getString("value"));
				net.sf.json.JSONObject examItemPackageSnapshotInMongoMeal = getMongoMealSnapshot(orderNum,"examItemPackageSnapshot");
				if(examItemPackageSnapshotInMongoMeal!=null)
					order.setPackageSnapshotDetail(examItemPackageSnapshotInMongoMeal.getString("value"));

			}
			HospitalExamCompanySnapshot hospitalCompany = new HospitalExamCompanySnapshot();
			int hospitalCompanyId = Integer.parseInt(map.get("hospital_company_id").toString());
			HospitalCompany hc = CompanyChecker.getHospitalCompanyById(hospitalCompanyId);
			hospitalCompany.setId(hospitalCompanyId);
			hospitalCompany.setName(hc.getName());
			if(hc.getPlatformCompanyId()!=null)
				hospitalCompany.setPlatformCompanyId(hc.getPlatformCompanyId());
			order.setHospitalCompany(hospitalCompany);
			if (map.get("channel_company_id") != null){
				ChannelCompany channelCompany = CompanyChecker.getChannelCompanyByCompanyId(Integer.parseInt(map.get("channel_company_id").toString()));
				ChannelExamCompanySnapshot channelExamCompanySnapshot = new ChannelExamCompanySnapshot();
				channelExamCompanySnapshot.setId(channelCompany.getId());
				channelExamCompanySnapshot.setPlatformCompanyId(channelCompany.getPlatformCompanyId());
				channelExamCompanySnapshot.setName(channelCompany.getName());
				order.setChannelCompany(channelExamCompanySnapshot);
			}
			int hospitalId = Integer.parseInt(map.get("hospital_id").toString());
			Hospital hospital = HospitalChecker.getHospitalById(hospitalId);
			order.setHospital(hospital);
			order.setOperatorId(Integer.parseInt(map.get("operator_id").toString()));
			ManagerSnapshot managerShot = new ManagerSnapshot(); //客户经理
			managerShot.setId(Integer.parseInt(map.get("order_manager_id").toString()));
			order.setOrderManager(managerShot);
			if(map.get("account_manager_id") !=null )
				order.setAccountCompanyId(Integer.parseInt(map.get("account_manager_id").toString()));
			Object entryCard = map.get("entry_card_id");
			if(entryCard!=null)
				order.setEntryCardId(Integer.parseInt(entryCard.toString()));
			order.setManagerId(Integer.parseInt(map.get("order_manager_id").toString()));
			if(map.get("from_site")!=null)
				order.setFromSite(Integer.parseInt(map.get("from_site").toString()));
			if(map.get("from_site_org_type")!=null)
				order.setFromSiteOrgType(Integer.parseInt(map.get("from_site_org_type").toString()));
			OrderMealSnapshot orderMealSnapshot = getMealSnapShotByOrder(orderId);
			if(orderMealSnapshot!=null)
			order.setOrderMealSnapshot(orderMealSnapshot);
		} catch (SqlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return order;
	}

	/**
	 * 根据sql查询收款订单列表
	 * @param sql
	 * @return
	 */
	public static List<PaymentOrder> getPaymentOrderListBySql(String sql){
		List<PaymentOrder> payList = new ArrayList<>();
		try {
			List<Map<String,Object>> list = DBMapper.query(sql);
			for(Map<String,Object> map : list){
				PaymentOrder uOrder = new PaymentOrder();
				int id = Integer.parseInt(map.get("id").toString());
				uOrder.setId(id);
				uOrder.setName(map.get("payment_name").toString());
				int manangerId = Integer.parseInt(map.get("manager_id").toString());
				if(map.get("hospital_company_id")!=null){
					int hospital_company_id = Integer.parseInt(map.get("hospital_company_id").toString());
					uOrder.setCompanyId(hospital_company_id);
				}
				uOrder.setHospitalId(Integer.parseInt(map.get("organization_id").toString()));
				uOrder.setManagerId(manangerId);
				if(AccountChecker.getAccountById(manangerId)!=null)
					uOrder.setManagerName(AccountChecker.getAccountById(manangerId).getName());
				uOrder.setCreateTime(simplehms.parse(map.get("gmt_created").toString()));
				//订单价格|状态|订单编号
				uOrder.setAmount(Long.parseLong(map.get("amount").toString()));
				RefundAmount refundAmount = PayChecker.getRefundAmountByOrderNum(map.get("order_num").toString(), PayConstants.OrderType.PaymentOrder);
				uOrder.setRefundAmount(refundAmount.getTotalSuccessRefundAmount());//退款金额
				uOrder.setStatus(Integer.parseInt(map.get("status").toString()));
				uOrder.setOrderNum(map.get("order_num").toString());
				if(map.get("remark")!=null)
					uOrder.setRemark(map.get("remark").toString());
				if(map.get("hospital_remark")!=null)
					uOrder.setHospitalRemark(map.get("hospital_remark").toString());
				List<TradeSettlementPaymentOrder> tradeSettlementPaymentOrderList = SettleChecker.getTradeSettlePaymentOrderByColumn("ref_order_num",map.get("order_num").toString());
				for(TradeSettlementPaymentOrder tradeOrder : tradeSettlementPaymentOrderList){
					String batchSn = tradeOrder.getBatchSn();
					if(tradeOrder.getHospitalSettlementStatus().equals(SettlementHospitalConfirmEnum.SETTLEMENT_TO_BE_CONFIRM.getCode())){
						uOrder.setSettlementBatchSn(batchSn);
						uOrder.setSettlementStatus(1);//结算中
						break;
					}else if (tradeOrder.getHospitalSettlementStatus().equals(SettlementHospitalConfirmEnum.SETTLEMENT_CONFIRMD.getCode())){
						uOrder.setSettlementBatchSn(batchSn);
						uOrder.setSettlementStatus(2);//已结算
						break;
					}
				}
				if(uOrder.getSettlementStatus() == null){
                    List<PaymentOrderSettlementDO> dtoList = SettleChecker.getPaymentOrderSettleByColumn("order_num",map.get("order_num").toString());
                    if(dtoList != null && dtoList.size()>0){
                        PaymentOrderSettlementDO dto = dtoList.get(0);
                        int settStatus = dto.getHospitalSettlementStatus();
                        if(settStatus == SettlementHospitalConfirmEnum.UNSETTLEMENT.getCode().intValue() || settStatus == SettlementHospitalConfirmEnum.SETTLEMENT_CANCELED.getCode().intValue())
                            uOrder.setSettlementStatus(0);//未结算/撤销结算
                    }

                }
				payList.add(uOrder);
			}
		} catch (SqlException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return payList;
	}

	/**
	 * 根据orderNum查询收款订单
	 * @param orderNum
	 * @return
	 */
	public static PaymentOrder getPaymentOrderInfo(String orderNum){
		String sql = "select * from tb_payment_order where order_num = '"+orderNum+"'";
		try {
			List<Map<String,Object>> list = DBMapper.query(sql);
			for(Map<String,Object> map : list){
				PaymentOrder uOrder = new PaymentOrder();
				int id = Integer.parseInt(map.get("id").toString());
				uOrder.setId(id);
				uOrder.setName(map.get("payment_name").toString());
				int manangerId = Integer.parseInt(map.get("manager_id").toString());
				if(map.get("hospital_company_id")!=null){
					int hospital_company_id = Integer.parseInt(map.get("hospital_company_id").toString());
					uOrder.setCompanyId(hospital_company_id);
				}
				uOrder.setHospitalId(Integer.parseInt(map.get("organization_id").toString()));
				uOrder.setManagerId(manangerId);
				if(AccountChecker.getAccountById(manangerId)!=null)
					uOrder.setManagerName(AccountChecker.getAccountById(manangerId).getName());
				uOrder.setCreateTime(simplehms.parse(map.get("gmt_created").toString()));
				//订单价格|状态|订单编号
				uOrder.setAmount(Long.parseLong(map.get("amount").toString()));
				uOrder.setStatus(Integer.parseInt(map.get("status").toString()));
				uOrder.setOrderNum(map.get("order_num").toString());
				if(map.get("remark") !=null)
					uOrder.setRemark(map.get("remark").toString());
				if(map.get("hospital_remark") !=null)
					uOrder.setHospitalRemark(map.get("hospital_remark").toString());
				return uOrder;
			}
		} catch (SqlException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	/*
	* 根据医院id查询医院站点下的订单详情
	* */

	/*public static List<Integer> getOrder1(int hospitalIdlist) {
		List<Integer> retlist = new ArrayList<Integer>();
		*//*String hospitalIds = "(" + ListUtil.hospitalId + ")";*//*

		String sqlStr = "select * from tb_order where hospital_id in " + hospitalIdlist + " order by insert_time desc limit ";

		log.info("sql:" + sqlStr);
		try {
			List<Map<String, Object>> list = DBMapper.query(sqlStr);
			for (Map<String, Object> m : list) {
				Order order = getOrderInfo(Integer.parseInt(m.get("hospital_id").toString()));
				retlist.add(Integer.parseInt(String.valueOf(order)));
			}

			return retlist;

		} catch (SqlException e) {
			log.error("catch exception while get order status from db!", e);
			e.printStackTrace();
		}
		return null;
	}*/


	/*public static List<Integer> getOrderList(int accountId) {
		Order order = new Order();
		// TODO Auto-generated method stub
		// String sqlStr = "select * from tb_order where account_id = ? order by
		// id desc limit 1 ";
		String sqlStr = "SELECT DISTINCT tb_order.*, "
				+ "CASE WHEN difference_price IS NULL THEN order_price ELSE difference_price END order_price, "
				+ " tb_hospital_period_settings. NAME AS exam_time_interval_name "
				+ "FROM tb_order "
				+ "LEFT JOIN tb_hospital_period_settings ON tb_hospital_period_settings.id = tb_order.exam_time_interval_id "
				+ "WHERE tb_order.account_id = ? AND tb_order. STATUS IN (0, 1, 2, 3, 4, 7, 9, 11) "
				+ "UNION SELECT DISTINCT tb_order.*, "
				+ "CASE WHEN difference_price IS NULL THEN order_price ELSE difference_price END order_price, "
				+ "tb_hospital_period_settings. NAME AS exam_time_interval_name "
				+ "FROM tb_order "
				+ "LEFT JOIN tb_hospital_period_settings ON tb_hospital_period_settings.id = tb_order.exam_time_interval_id "
				+ "WHERE tb_order.operator_id = ? AND tb_order. STATUS != 6 ORDER BY insert_time desc";
		List<Map<String, Object>> list = null;
		try {
			list = DBMapper.query(sqlStr, accountId, accountId);
		} catch (SqlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<Integer> ids = new ArrayList<>();
		for (Map<String, Object> m : list) {
			order.setId((Integer) m.get("id"));
			Date examDate = ChangeToDate.formatDate(m.get("exam_date").toString());
			order.setExamDate(examDate);
			order.setStatus((Integer) m.get("status"));
			order.setOrderPrice((Integer) m.get("order_price"));
			ids.add(order.getId());
		}
		return ids;
	}
*/

	/**
	 *
	 * 获取渠道站点下的订单
	 * @param hospitalId
	 * @return
	 */
	public static List<Order> getordersList(String sql){
		List<Order> retOrderList = new ArrayList<>();
		List<Map<String,Object>> dblist = null;
		try {
			dblist =  DBMapper.query(sql);
			if(dblist != null || dblist.size() > 0){
				for(int k = 0;k<dblist.size();k++){
					int id = Integer.parseInt(dblist.get(k).get("id").toString());
					Order order = getOrderInfo(id);
					retOrderList.add(order);
				}
			}
		} catch (SqlException e) {
			e.printStackTrace();
		}
//		List<Integer> ids = new ArrayList<>();
//		if(dblist!=null&&dblist.size()!=0){
//			for(Map<String,Object> m : dblist){
//				/*int orderId = Integer.parseInt(m.get("id").toString());
//				Order order = getOrderInfo(orderId);
//				orders.add(order);*/
//				order.setId((Integer)m.get("id"));
//				Date examDate = null;
//				if (m.get("exam_date") != null){
//					examDate = ChangeToDate.formatDate(m.get("exam_date").toString());
//				}
//				order.setExamDate(examDate);
//				order.setOrderPrice((Integer)m.get("order_price"));
//				order.setStatus((Integer)m.get("status"));
//				ids.add(order.getId());
//			}
//		}
		return retOrderList;
	}

	/**
	 * 根据用户查询拥有的订单列表
	 */
	public static List<Order> checkOrder(List<Integer> accountlist) {
		List<Order> retlist = new ArrayList<Order>();
		String accounts = "(" + ListUtil.IntegerlistToString(accountlist) + ")";
		String sqlStr = "select * from tb_order where account_id in " + accounts + " order by insert_time desc limit "
				+ accountlist.size();

		log.info("sql:" + sqlStr);
		try {
			List<Map<String, Object>> list = DBMapper.query(sqlStr);
			for (Map<String, Object> m : list) {
				Order order = getOrderInfo(Integer.parseInt(m.get("id").toString()));
				retlist.add(order);
			}

			return retlist;

		} catch (SqlException e) {
			log.error("catch exception while get order status from db!", e);
			e.printStackTrace();
		}
		return null;
	}
	

	/**
	 * 获取客户指定状态的订单列表
	 * @param accountId
	 * @param status
	 * @return
	 */
	public static List<Order> getDesignatedOrderList(int accountId, int status) {
		List<Order> orders = new ArrayList<Order>();
		// TODO Auto-generated method stub
	  String sqlStr = "SELECT * FROM tb_order where account_id = "+accountId+" AND status = "+status+" ORDER BY insert_time DESC LIMIT 10;";
	  List<Map<String, Object>> list = null;
	  try {
		System.out.println(sqlStr);
		list = DBMapper.query(sqlStr);
	  } catch (SqlException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	  }
	  if(list!=null&&list.size()!=0){
		  for(Map<String,Object> m : list){
			  	int orderId = Integer.parseInt(m.get("id").toString());
			  	Order order = getOrderInfo(orderId);
				orders.add(order);
			 } 
		  return orders;		
	  }
	  return null;
	}
	
	/**
	 * 获取客户指定状态的订单列表
	 * @param accountId
	 * @param status
	 * @param balance 订单金额上限
	 * @return
	 */
	public static List<Order> getDesignatedOrderList(int accountId, int status,int balance) {
		List<Order> orders = new ArrayList<Order>();
		// TODO Auto-generated method stub
	  String sqlStr = "SELECT * FROM tb_order where account_id = "+accountId+" AND status = "+status+" AND order_price <= "+balance+" ORDER BY insert_time DESC LIMIT 10;";
	  List<Map<String, Object>> list = null;
	  try {
		System.out.println(sqlStr);
		list = DBMapper.query(sqlStr);
	  } catch (SqlException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	  }
	  if(list!=null&&list.size()!=0){
		  System.out.println("list.size:"+list.size());
		  for(Map<String,Object> m : list){
			  	int orderId = Integer.parseInt(m.get("id").toString());
			  	Order order = getOrderInfo(orderId);
				orders.add(order);
			 } 
		  return orders;		
	  }
	  return null;
	}
	

	/**
	 * 获取最新的订单
	 * @param accountId
	 * @return
	 */
	public static List<Integer> getRecentOrder(int accountId){
		Order order = new Order();
		String sqlStr = "select * from tb_order where account_id = ? order by id desc limit 1 ";
		  List<Map<String, Object>> list = null;
			try {
				list = DBMapper.query(sqlStr,accountId);
			} catch (SqlException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			List<Integer> ids =new ArrayList<>();
			  for(Map<String,Object> m : list){	
					order.setId((Integer)m.get("id"));
					Date examDate = ChangeToDate.formatDate(m.get("exam_date").toString());
					order.setExamDate(examDate);
					order.setStatus((Integer)m.get("status"));
					order.setOrderPrice((Integer)m.get("order_price"));
					ids.add(order.getId());
			  }
			return ids;		
	}

	/**
	 * 获取订单的accountId
	 * 
	 * @param orderId
	 * @return
	 * @throws SqlException
	 */
	public static int getOrderAccountId(int orderId) throws SqlException {
		String sql = "select  o.account_id from tb_order o  where  o.id = ? ";
		List<Map<String, Object>> list = DBMapper.query(sql, orderId);
		Map<String, Object> map = list.get(0);
		return Integer.parseInt(map.get("account_id").toString());
	}



	/**
	 * 根据sql语句查询订单列表
	 *
	 * @param sql
	 * @return
	 * @throws SqlException
	 */
	public static List<Order> getOrderListBySql(String sql) throws SqlException {
		List<Order> orderList = new ArrayList<Order>();
		List<Map<String, Object>> list = DBMapper.query(sql);
	   for(Map<String,Object> m : list){
	   	 int orderId = Integer.parseInt(m.get("id").toString());
	   	 Order order = getOrderInfo(orderId);
	   	 if(order != null)
	   		 orderList.add(order);
	   }
	   return orderList;
	}

	/**
	 * 获取客户订单列表 tb_order
	 * 
	 * @param flag
	 */
	public static List<Integer> getOrderList(int accountId) {
		Order order = new Order();
		// TODO Auto-generated method stub
		// String sqlStr = "select * from tb_order where account_id = ? order by
		// id desc limit 1 ";
		String sqlStr = "SELECT DISTINCT tb_order.*, "
				+ "CASE WHEN difference_price IS NULL THEN order_price ELSE difference_price END order_price, "
				+ " tb_hospital_period_settings. NAME AS exam_time_interval_name "
				+ "FROM tb_order "
				+ "LEFT JOIN tb_hospital_period_settings ON tb_hospital_period_settings.id = tb_order.exam_time_interval_id "
				+ "WHERE tb_order.account_id = ? AND tb_order. STATUS IN (0, 1, 2, 3, 4, 7, 9, 11) "
				+ "UNION SELECT DISTINCT tb_order.*, "
				+ "CASE WHEN difference_price IS NULL THEN order_price ELSE difference_price END order_price, "
				+ "tb_hospital_period_settings. NAME AS exam_time_interval_name "
				+ "FROM tb_order "
				+ "LEFT JOIN tb_hospital_period_settings ON tb_hospital_period_settings.id = tb_order.exam_time_interval_id "
				+ "WHERE tb_order.operator_id = ? AND tb_order. STATUS != 6 ORDER BY insert_time desc";
		List<Map<String, Object>> list = null;
		try {
			list = DBMapper.query(sqlStr, accountId, accountId);
		} catch (SqlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<Integer> ids = new ArrayList<>();
		for (Map<String, Object> m : list) {
			order.setId((Integer) m.get("id"));
			Date examDate = ChangeToDate.formatDate(m.get("exam_date").toString());
			order.setExamDate(examDate);
			order.setStatus((Integer) m.get("status"));
			order.setOrderPrice((Integer) m.get("order_price"));
			ids.add(order.getId());
		}
		return ids;
	}

	
	/**
	 * 获取能改项目的订单列表
	 * @param accountId
	 * @return
	 */
	public static List<Integer> getCanChangeExamItemOrderList(int accountId) {
		Order order = new Order();
		// TODO Auto-generated method stub
		// String sqlStr = "select * from tb_order where account_id = ? order by
		// id desc limit 1 ";
		String sqlStr = "SELECT DISTINCT tb_order.*, "
				+ "CASE WHEN difference_price IS NULL THEN order_price ELSE difference_price END order_price, "
				+ " tb_hospital_period_settings. NAME AS exam_time_interval_name "
				+ "FROM tb_order "
				+ "LEFT JOIN tb_hospital_period_settings ON tb_hospital_period_settings.id = tb_order.exam_time_interval_id "
				+ "WHERE tb_order.account_id = ? AND tb_order.STATUS IN (2,0) AND tb_order.IS_EXPORT = 0 "
				+ "UNION SELECT DISTINCT tb_order.*, "
				+ "CASE WHEN difference_price IS NULL THEN order_price ELSE difference_price END order_price, "
				+ "tb_hospital_period_settings. NAME AS exam_time_interval_name "
				+ "FROM tb_order "
				+ "LEFT JOIN tb_hospital_period_settings ON tb_hospital_period_settings.id = tb_order.exam_time_interval_id "
				+ "WHERE tb_order.operator_id = ? AND tb_order. STATUS != 6 ORDER BY insert_time DESC limit 10";
		List<Map<String, Object>> list = null;
		try {
			list = DBMapper.query(sqlStr, accountId, accountId);
		} catch (SqlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<Integer> ids = new ArrayList<>();
		for (Map<String, Object> m : list) {
			order.setId((Integer) m.get("id"));
			Date examDate = ChangeToDate.formatDate(m.get("exam_date").toString());
			order.setExamDate(examDate);
			order.setStatus((Integer) m.get("status"));
			order.setOrderPrice((Integer) m.get("order_price"));
			ids.add(order.getId());
		}
		return ids;
	}

	/**
	 * 获取订单操作日志
	 * 
	 * @param orderNum
	 * @return
	 */
	public static List<ExamOrderOperateLogDO> getOrderOperatrLog(String orderNum) {
		List<ExamOrderOperateLogDO> listOperateLog = new ArrayList<ExamOrderOperateLogDO>();
		String sql = "SELECT * FROM tb_exam_order_operate_log WHERE order_num = ? ORDER BY id DESC;";
		List<Map<String, Object>> list = null;
		try {
			list = DBMapper.query(sql, orderNum);
		} catch (SqlException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if (list.size() > 0) {
			for (Map<String, Object> m : list) {
				ExamOrderOperateLogDO log = new ExamOrderOperateLogDO();
				log.setId(Integer.valueOf(m.get("id").toString()));
				if (m.get("order_num") != null)
					log.setOrderNum(m.get("order_num").toString());
				if (m.get("type") != null)
					log.setType(Integer.valueOf(m.get("type").toString()));
				if (m.get("order_status") != null)
					log.setOrderStatus(Integer.valueOf(m.get("order_status").toString()));
				if (m.get("content") != null)
					log.setContent(m.get("content").toString());
				log.setRemark(m.get("remark").toString());
				if (m.get("system") != null)
					log.setSystem(Integer.valueOf(m.get("system").toString()));
				if (m.get("operator") != null)
					log.setOperator(Integer.valueOf(m.get("operator").toString()));
				if (m.get("gmt_created") != null)
					try {
						log.setGmt_created(sdf.parse(m.get("gmt_created").toString()));
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				listOperateLog.add(log);
			}
			return listOperateLog;
		}
		return null;
	}
	

	public static OrderMealSnapshot getMealSnapShotByOrder(Integer orderId){
		OrderMealSnapshot orderMeal = new OrderMealSnapshot();
		List<ExamItemSnapshot> itemList = new ArrayList<ExamItemSnapshot>();
		String sql = "SELECT * FROM tb_exam_order_meal_snapshot WHERE order_num in (SELECT order_num FROM tb_order WHERE id in ("+orderId+"))  order by id desc ";
		Map<String, Object> detail = null;
		try {
			List<Map<String,Object>> details = DBMapper.query(sql);
			if(details != null && details.size() > 0)
				detail = details.get(0);
			else
				return null;
		} catch (SqlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		MealSnapshot meal = JSON.parseObject(detail.get("meal_detail").toString(), MealSnapshot.class);
		itemList = JSON.parseObject(JsonPath.read(detail.get("items_detail").toString(), "$.[*]").toString(), new TypeReference<List<ExamItemSnapshot>>() {
		});
		ExamItemPackageSnapshot packageSnapshot = new ExamItemPackageSnapshot();
		if(detail.get("package_snapshot_detail")!=null){
			packageSnapshot = JSON.parseObject(detail.get("package_snapshot_detail").toString(), ExamItemPackageSnapshot.class);
			orderMeal.setExamItemPackageSnapshot(packageSnapshot);
		}

		orderMeal.setExamItemSnapList(itemList);
		orderMeal.setMealSnapshot(meal);
		if(!orderMeal.equals(new OrderMealSnapshot()))
			return orderMeal;
		return null;
	}

	/***
	 * 根据订单ID查询订单的单项ID列表
	 * @param orderId
	 * @return
	 */
	public static List<Integer> getOrderItemList(Integer orderId){
		List<Integer> itemList = new ArrayList<>();
		Order order = getOrderInfo(orderId);
		List<ExamItemSnapshot> examItemSnapshotList = order.getOrderMealSnapshot().getExamItemSnapList();
		for(ExamItemSnapshot e : examItemSnapshotList)
			itemList.add(e.getId());
		return itemList;
	}
	/**
	 * 根据id获取批量下单记录表 tb_batch_order_process
	 * 
	 * @param id
	 * @return
	 */
	public static BatchOrderProcess getBatchProcessById(Integer id) {
		BatchOrderProcess process = new BatchOrderProcess();
		OrderBatch orderBatch = new OrderBatch();
		String sql = "SELECT * FROM tb_batch_order_process WHERE id = ?;";
		Map<String, Object> m = null;
		try {
			m = DBMapper.query(sql, id).get(0);
		} catch (SqlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		process.setId(Integer.valueOf(m.get("id").toString()));
		process.setOperatorId(Integer.valueOf(m.get("operator_id").toString()));

		if (m.get("batch_id") != null) {
			orderBatch.setId(Integer.valueOf(m.get("batch_id").toString()));
			process.setOrderBatch(orderBatch);
		}

		if (m.get("status") != null)
			process.setStatus(Integer.valueOf(m.get("status").toString()));
		String accountIds = m.get("account_ids").toString();
		// accountIds = accountIds.substring(1, accountIds.length()-1);
		@SuppressWarnings("unchecked")
		List<Integer> accountIdList = JSONObject.parseObject(accountIds, ArrayList.class);
		process.setAccountIds(accountIdList);
		process.setTotalNum(Integer.valueOf(m.get("total_num").toString()));
		if (m.get("success_num") != null)
			process.setSuccessNum(Integer.valueOf(m.get("success_num").toString()));
		if (m.get("fail_num") != null)
			process.setFailNum(Integer.valueOf(m.get("fail_num").toString()));
		if (m.get("deal_num") != null)
			process.setDealNum(Integer.valueOf(m.get("deal_num").toString()));
		if (m.get("description") != null)
			process.setDescription(m.get("description").toString());

		List<BatchOrderProcessRecord> records = listBatchOrderProcessRecord(id);
		process.setRecords(records);

		return process;
	}
	
	/**
	 * 订单是否已经过期
	 * @param hospitalId
	 * @param orderDate
	 * @return
	 */
	public static boolean isExpiredOrder(Integer hospitalId, Date orderDate) {
		Map<String,Object> hospitalMap = HospitalChecker.getHospitalSetting(hospitalId,"previous_book_days");
		HospitalSettings hospitalSettings = new HospitalSettings();
		hospitalSettings.setPreviousBookDays(Integer.valueOf(hospitalMap.get("previous_book_days").toString()));
		Integer expireOrderDays = null;
		if (null == hospitalSettings || null == (expireOrderDays = hospitalSettings.getPreviousBookDays())) {
		    Date currentDate = DateUtils.toDayStartSecond(new Date());
		    if (currentDate.before(orderDate)) {
		    	return false;
		    }
			return true;
		}

		String expireTimeStr = StringUtils.isEmpty(hospitalSettings.getPreviousBookTime()) ? "01:01" : hospitalSettings
				.getPreviousBookTime();
		String[] timeElement = expireTimeStr.split(":");
		Integer hour = Integer.parseInt(timeElement[0]);
		Integer miniute = timeElement.length >= 2 ? Integer.parseInt(timeElement[1]) : 0;
		Calendar nowTime = Calendar.getInstance();
		Calendar orderDateTime = Calendar.getInstance();
		orderDateTime.setTime(orderDate);
		orderDateTime.set(Calendar.HOUR_OF_DAY, hour);
		orderDateTime.set(Calendar.MINUTE, miniute);
		nowTime.add(Calendar.DAY_OF_MONTH, expireOrderDays);
		if (nowTime.before(orderDateTime)) {
			return false;
		}
		return true;
	}
	

	/**
	 * 根据进程id获取批量下单详情表
	 * 
	 * @param processId
	 * @return
	 */
	public static List<BatchOrderProcessRecord> listBatchOrderProcessRecord(Integer processId) {
		List<BatchOrderProcessRecord> records = new ArrayList<BatchOrderProcessRecord>();
		String sql = "SELECT * FROM tb_batch_order_process_record WHERE process_id = ?;";
		List<Map<String, Object>> list = null;
		try {
			list = DBMapper.query(sql, processId);
		} catch (SqlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (Map<String, Object> m : list) {
			BatchOrderProcessRecord record = new BatchOrderProcessRecord();
			record.setId(Integer.valueOf(m.get("id").toString()));
			if (m.get("process_id") != null)
				record.setProcessId(Integer.valueOf(m.get("process_id").toString()));
			if (m.get("account_id") != null)
				record.setAccountId(Integer.valueOf(m.get("account_id").toString()));
			if (m.get("fail_msg") != null)
				record.setFailMsg(m.get("fail_msg").toString());
			if (m.get("order_num") != null)
				record.setOrderNum(m.get("order_num").toString());
			if (m.get("status") != null)
				record.setStatus(Integer.valueOf(m.get("status").toString()));
			records.add(record);
		}
		return records;
	}


	public static  Integer getRecentOrderProcess(){
		Integer processId = 0;
		String sql = "SELECT * FROM tb_batch_order_process WHERE STATUS = 2  AND task_type = 1 AND success_num <> 0 ORDER BY id DESC LIMIT 1;";
		Map<String, Object> map = null;
		try {
			map = DBMapper.query(sql).get(0);
		} catch (SqlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		processId = Integer.valueOf(map.get("id").toString());
		return processId;
	}

	public static Order getOrders(String query,String value){
		//List<Order> orders = new ArrayList<Order>();
		Order order = new Order();
		String sql = "SELECT * FROM tb_order WHERE "+query+" = ("+value+");";
		List<Map<String, Object>> list = null;
		try {
			list = DBMapper.query(sql);
		} catch (SqlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(list.size()>0){
			for(Map<String,Object> m : list){
				order.setId(Integer.valueOf(m.get("id").toString()));
				order.setStatus(Integer.valueOf(m.get("status").toString()));
			}
		}
		return order;
	}
	
	public static String orderExceptionAction(HttpResult response,MyHttpClient httpclient) throws SqlException{
		//OrderException exception = JSON.parseObject(JsonPath.read(response.getBody(), "$").toString(),OrderException.class);
		String exceptType = JsonPath.read(response.getBody(), "$.exceptType");
		List<OrderException.ExceptionItem> exceptionItem = JSON.parseObject(
				JsonPath.read(response.getBody(), "$.accountIdList[*]").toString(),
				new TypeReference<List<OrderException.ExceptionItem>>() {
				});
		// 如果下单时，同意日期已经有已预约订单，先撤单
		if (exceptType.equals("same")) {
			System.out.println("当天已有订单");
			// Set<ExceptionItem> accountList = exception.getAccoutSet();
			for (ExceptionItem account : exceptionItem) {
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				int accid = account.getAccountId();
				String examDate = account.getExamDate() + "%";
				String sql = "SELECT id FROM tb_order where account_id = " + accid + " AND exam_date like \'" + examDate
						+ "%\' and status = 2;";
				List<Map<String, Object>> list = null;
				try {
					list = DBMapper.query(sql);
				} catch (SqlException e) {
					e.printStackTrace();
				}
				int SameOrderId = Integer.valueOf(list.get(0).get("id").toString());

				NameValuePair nvp = new BasicNameValuePair("orderIds[]", String.valueOf(SameOrderId));
				params.add(nvp);
				params.add(new BasicNameValuePair("sendMsg", "false"));

				System.out.println("订单重复，开始撤单:"+SameOrderId);
				Run_CrmOrderRevokeOrder(httpclient,new ArrayList<>(SameOrderId),false,true,true);
				System.out.println("订单重复，撤单成功:"+SameOrderId);
			}
			return exceptType;
		}
		return null;

	}	
	
	/**********************订 单 发 票*****************************/

	public static String getOrderInvoiceContentConfig(Integer hospitalId) {
		String content = getInvoiceConfig(hospitalId, "exam:invoice:content", "获取发票内容配置失败");
		if (content == null) {
			try {
				throw new Exception("发票内容配置为空");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return content;
	}
	
	private static String getInvoiceConfig(int hospitalId, String paramKey, String errorMsg) {
		// TODO Auto-generated method stub
		SystemParam systemParam = null;
		systemParam = HospitalChecker.getSysParam(paramKey, hospitalId);
		if (systemParam == null) {
			systemParam = HospitalChecker.getSysParam(paramKey, null);
		}

		return systemParam == null ? null : systemParam.getParamValue();
	}


	@SuppressWarnings("unused")
	private List<InvoiceApply> getInvoiceApplysFromDB(List<Integer> orderIds, Integer status) {
		// TODO Auto-generated method stub
		List<InvoiceApply> invoiceList = new ArrayList<InvoiceApply>();
		String order = ListUtil.IntegerlistToString(orderIds);
		String sql="SELECT * FROM tb_invoice_apply WHERE order_id IN ("+order+") ";
		if(status!=null)
			sql += "AND STATUS = "+status+" ";
		List<Map<String, Object>> list = null;
		try {
			list = DBMapper.query(sql);
		} catch (SqlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(list.size()>0){
			for(Map<String,Object>map : list){
				InvoiceApply ia = new InvoiceApply();
				ia.setId(Integer.valueOf(map.get("id").toString()));
				  ia.setTitle(map.get("title").toString());
				  if(map.get("content")!=null)
					  ia.setContent(map.get("content").toString());
				  ia.setApplyAmount(Integer.valueOf(map.get("apply_amount").toString()));
				  if(map.get("amount")!=null)
					  ia.setAmount(Integer.valueOf(map.get("amount").toString()));
				  ia.setOrderId(Integer.valueOf(map.get("order_id").toString()));
				  ia.setDeliveryType(Integer.valueOf(map.get("delivery_type").toString()));
				  if(map.get("address_id")!=null)
					  ia.setAddressId(Integer.valueOf(map.get("address_id").toString()));
				  if(map.get("postage")!=null)
					  ia.setPostage(Integer.valueOf(map.get("postage").toString()));
				  if(map.get("remark")!=null)
					  ia.setRemark(map.get("remark").toString());
				  if(map.get("STATUS")!=null)
					  ia.setStatus(Integer.valueOf(map.get("STATUS").toString()));
				  if(map.get("proposer")!=null)
					  ia.setProposer(Integer.valueOf(map.get("proposer").toString()));
				  if(map.get("approver")!=null)
					  ia.setProposer(Integer.valueOf(map.get("approver").toString()));
				  invoiceList.add(ia);
			}
			return invoiceList;
		}
		return null;
	}

	

	/**
	 * 通过orderId获取发票详情页
	 * tb_invoice_apply
	 * @param orderId
	 * @return
	 */
	public static InvoiceApply getInvoiceAppliedByOrderId(int orderId){
		  InvoiceApply ia = new InvoiceApply();
		  String sql = "SELECT * FROM tb_invoice_apply WHERE order_id = "+orderId+" ";
		  List<Map<String, Object>> list = null;
		try {
			list = DBMapper.query(sql);
		} catch (SqlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		  if(list.size()>0&&list!=null){
			  Map<String,Object> map = list.get(0);
			  ia.setId(Integer.valueOf(map.get("id").toString()));
			  ia.setTitle(map.get("title").toString());
			  if(map.get("content")!=null)
				  ia.setContent(map.get("content").toString());
			  ia.setApplyAmount(Integer.valueOf(map.get("apply_amount").toString()));
			  if(map.get("amount")!=null)
				  ia.setAmount(Integer.valueOf(map.get("amount").toString()));
			  ia.setOrderId(Integer.valueOf(map.get("order_id").toString()));
			  ia.setDeliveryType(Integer.valueOf(map.get("delivery_type").toString()));
			  if(map.get("address_id")!=null)
				  ia.setAddressId(Integer.valueOf(map.get("address_id").toString()));
			  if(map.get("postage")!=null)
				  ia.setPostage(Integer.valueOf(map.get("postage").toString()));
			  if(map.get("remark")!=null)
				  ia.setRemark(map.get("remark").toString());
			  if(map.get("STATUS")!=null)
				  ia.setStatus(Integer.valueOf(map.get("STATUS").toString()));
			  if(map.get("proposer")!=null)
				  ia.setProposer(Integer.valueOf(map.get("proposer").toString()));
			  if(map.get("approver")!=null)
				  ia.setProposer(Integer.valueOf(map.get("approver").toString()));
			  
			  return ia;
		  }
		  return null;
	  }
	
	
	/*****************************回  单  验   证*****************************************/
	
	/**
	 * 验证撤销订单的回单申请相关记录
	 * 
	 * @param order
	 * @throws SqlException
	 */
	@SuppressWarnings("deprecation")
	public static void checkRevokeOrderRefundApply(Order order) throws SqlException{
		 //提取订单支付金额,各种方式下支付金额之和
		 String tradeSql = "select * from tb_trade_order where ref_order_num = '"+order.getOrderNum()+"'  and trade_type = "+PayConstants.TradeType.pay;
		 List<Map<String,Object>> tradeList = DBMapper.query(tradeSql);
		 int succ_amount = 0;
		 int cardPayAmount = 0;
		 int offlinePayAmount = 0;
		 int onlinePayAmount = 0;
		 int parentCardPayAmount = 0;
		 int couponAmount = 0;//优惠支付
		 int totalAmount = 0;
		 if(tradeList.size() > 0){
			 for(Map<String,Object> m : tradeList)
				 	succ_amount += Integer.parseInt(m.get("succ_amount").toString());
			 
			 String payRecordSql = "select * from tb_trade_pay_record where ref_order_num =  '"+order.getOrderNum()+"' and ref_order_type = "+PayConstants.TradeType.pay 
					 + " and pay_status = "+PayConstants.TradeStatus.Successful;
			 List<Map<String,Object>> payRecordList = DBMapper.query(payRecordSql);
			
			 for(Map<String,Object> map : payRecordList) {
				 if (map.get("trade_method_type").equals(PayConstants.PayMethod.Alipay))
					 onlinePayAmount += Integer.parseInt(map.get("pay_amount").toString());
				 if (map.get("trade_method_type").equals(PayConstants.PayMethod.AlipayScan))
					 onlinePayAmount += Integer.parseInt(map.get("pay_amount").toString());
				 if (map.get("trade_method_type").equals(PayConstants.PayMethod.Wxpay))
					 onlinePayAmount += Integer.parseInt(map.get("pay_amount").toString());
				 if (map.get("trade_method_type").equals(PayConstants.PayMethod.WxpayScan))
					 onlinePayAmount += Integer.parseInt(map.get("pay_amount").toString());
				 if (map.get("trade_method_type").equals(PayConstants.PayMethod.Card))
					 cardPayAmount += Integer.parseInt(map.get("pay_amount").toString());
				 if (map.get("trade_method_type").equals(PayConstants.PayMethod.ParentCard))
					 parentCardPayAmount += Integer.parseInt(map.get("pay_amount").toString());
				 if (map.get("trade_method_type").equals(PayConstants.PayMethod.OfflinePay))
					 offlinePayAmount += Integer.parseInt(map.get("pay_amount").toString());
				 if (map.get("trade_method_type").equals(PayConstants.PayMethod.Balance))
					 onlinePayAmount += Integer.parseInt(map.get("pay_amount").toString());
				 if (map.get("trade_method_type").equals(PayConstants.PayMethod.Coupon))
					 couponAmount += Integer.parseInt(map.get("pay_amount").toString());
			 }
			 	totalAmount = cardPayAmount + offlinePayAmount + onlinePayAmount + parentCardPayAmount + couponAmount;
		 } 
		 
		 //验证撤销订单后，退款申请表系统自动同意
		 String sql = "select * from tb_order_refund_apply where order_num = '"+order.getOrderNum()+"'";
		 log.info("sql..."+sql);
		 List<Map<String,Object>> applyList = DBMapper.query(sql);
		 Assert.assertEquals(applyList.size(), 1);
		 Map<String,Object> apply = applyList.get(0);
		 Assert.assertEquals(Integer.parseInt(apply.get("account_id").toString()), order.getAccount().getId().intValue());
		 Assert.assertEquals(Integer.parseInt(apply.get("from_site").toString()), order.getHospital().getId());
		 Assert.assertEquals(Integer.parseInt(apply.get("hospital_company_id").toString()), order.getHospitalCompany().getId().intValue());
		 Assert.assertEquals(Integer.parseInt(apply.get("amount").toString()), succ_amount);
		 Assert.assertEquals(Integer.parseInt(apply.get("refund_type").toString()), 0);//全额退款
		 Assert.assertNotEquals(Integer.parseInt(apply.get("scene").toString()), 3);//撤销订单
		 Assert.assertEquals(apply.get("reason").toString(),"系统自动同意");
		 Assert.assertEquals(Integer.parseInt(apply.get("status").toString()), 1);//同意退款
		 Assert.assertEquals(Integer.parseInt(apply.get("is_deleted").toString()), 0);
		 JSONObject jo = JSON.parseObject(apply.get("pay_detail").toString());
		 Assert.assertEquals(cardPayAmount,Integer.parseInt(jo.get("cardPayAmount").toString()));
		 Assert.assertEquals(offlinePayAmount,Integer.parseInt(jo.get("offlinePayAmount").toString()));
		 Assert.assertEquals(onlinePayAmount,Integer.parseInt(jo.get("onlinePayAmount").toString()));
		 Assert.assertEquals(parentCardPayAmount,Integer.parseInt(jo.get("parentCardPayAmount").toString()));
		 if(jo.get("couponAmount")!=null)
		 	Assert.assertEquals(couponAmount,Integer.parseInt(jo.get("couponAmount").toString()));
		 Assert.assertEquals(totalAmount,Integer.parseInt(jo.get("totalAmount").toString()));

		 int refund_id = Integer.parseInt(apply.get("id").toString());
		 //验证退款申请记录表插入2条记录，1条待审核，1条审核通过
		 String logSql = "select * from tb_order_refund_apply_record where refund_id ="+refund_id +" order by id";
		 List<Map<String,Object>> applyLogList = DBMapper.query(logSql);
		 Assert.assertEquals(applyLogList.size(), 2);
		 Map<String,Object> firstApply = applyLogList.get(0);
		 Assert.assertEquals(Integer.parseInt(firstApply.get("account_id").toString()), order.getAccount().getId().intValue());
		 Assert.assertEquals(Integer.parseInt(firstApply.get("from_site").toString()), order.getHospital().getId());
		 Assert.assertEquals(Integer.parseInt(firstApply.get("hospital_company_id").toString()), order.getHospitalCompany().getId().intValue());
		 Assert.assertEquals(Integer.parseInt(firstApply.get("amount").toString()), succ_amount);
		 Assert.assertEquals(Integer.parseInt(firstApply.get("status").toString()),0);//待审核
		 JSONObject jfirst = JSON.parseObject(apply.get("pay_detail").toString());
		 Assert.assertEquals(cardPayAmount,Integer.parseInt(jfirst.get("cardPayAmount").toString()));
		 Assert.assertEquals(offlinePayAmount,Integer.parseInt(jfirst.get("offlinePayAmount").toString()));
		 Assert.assertEquals(onlinePayAmount,Integer.parseInt(jfirst.get("onlinePayAmount").toString()));
		 Assert.assertEquals(parentCardPayAmount,Integer.parseInt(jfirst.get("parentCardPayAmount").toString()));
		if(jfirst.get("couponAmount")!=null)
			Assert.assertEquals(couponAmount,Integer.parseInt(jfirst.get("couponAmount").toString()));
		 Assert.assertEquals(totalAmount,Integer.parseInt(jfirst.get("totalAmount").toString()));
		 
		 Map<String,Object> secondApply = applyLogList.get(1);
		 Assert.assertEquals(Integer.parseInt(secondApply.get("account_id").toString()), order.getAccount().getId().intValue());
		 Assert.assertEquals(Integer.parseInt(secondApply.get("from_site").toString()), order.getHospital().getId());
		 Assert.assertEquals(Integer.parseInt(secondApply.get("hospital_company_id").toString()), order.getHospitalCompany().getId().intValue());
		 Assert.assertEquals(Integer.parseInt(secondApply.get("amount").toString()), succ_amount);
		 Assert.assertEquals(Integer.parseInt(secondApply.get("status").toString()),1);//审核通过
		 Assert.assertEquals(secondApply.get("reason").toString(),"系统自动同意");
		 Assert.assertEquals(secondApply.get("operator_name").toString(),"系统");
		 Assert.assertEquals(Integer.parseInt(secondApply.get("operator").toString()),-1);//系统账户	 
		 JSONObject jsecond = JSON.parseObject(secondApply.get("pay_detail").toString());
		 Assert.assertEquals(cardPayAmount,Integer.parseInt(jsecond.get("cardPayAmount").toString()));
		 Assert.assertEquals(offlinePayAmount,Integer.parseInt(jsecond.get("offlinePayAmount").toString()));
		 Assert.assertEquals(onlinePayAmount,Integer.parseInt(jsecond.get("onlinePayAmount").toString()));
		 Assert.assertEquals(parentCardPayAmount,Integer.parseInt(jsecond.get("parentCardPayAmount").toString()));
		if(jsecond.get("couponAmount")!=null)
			Assert.assertEquals(couponAmount,Integer.parseInt(jsecond.get("couponAmount").toString()));
		 Assert.assertEquals(totalAmount,Integer.parseInt(jsecond.get("totalAmount").toString()));
	}
	
	
	/**
	 * 验证HIS浅对接回单的申请相关记录
	 * @param order
	 * @throws SqlException 
	 */
	@SuppressWarnings("deprecation")
	public static boolean checkLowHISOrderRefundApply(Order order,String action) throws SqlException{
		 if(action.equals("退项目")){
			 double refundPriceDouble = 0.0;
			 if(order.getXlsItemIds() !=null ){
				 String refundItems = order.getHisItemIds().replace(order.getXlsItemIds(), "");
				 String rs[] = refundItems.split(";");
				 for(String s :rs){
					 if(s.equals("")||s == null||!s.contains(":"))continue; //过滤掉空的单项对
					 BigDecimal itemD = new BigDecimal(s.split(":")[1]);
					 BigDecimal discountD = new BigDecimal(order.getDiscount());
					 refundPriceDouble += itemD.multiply(discountD).doubleValue();
				 }
			 }
			 int refundCa = (int) Math.ceil(refundPriceDouble*100);
			 int refundPrice = HospitalChecker.calculator_data(order.getHospital().getId(),refundCa);
			 log.info("退款金额。。"+refundPrice);
			//提取订单支付金额,各种方式下支付金额之和
			 String tradeSql = "select * from tb_trade_order where ref_order_num = '"+order.getOrderNum()+"'  and trade_type = "+PayConstants.TradeType.pay;
			 List<Map<String,Object>> tradeList = DBMapper.query(tradeSql);
			 int cardPayAmount = 0;
			 int offlinePayAmount = 0;
			 int onlinePayAmount = 0;
			 int parentCardPayAmount = 0;
			 int couponAmount = 0;
			 int totalAmount = 0;
			 if(tradeList.size() > 0){
				 String payRecordSql = "select * from tb_trade_pay_record where ref_order_num =  '"+order.getOrderNum()+"' and ref_order_type = "+PayConstants.TradeType.pay 
						 + " and pay_status = "+PayConstants.TradeStatus.Successful;
				 List<Map<String,Object>> payRecordList = DBMapper.query(payRecordSql);
				
				 for(Map<String,Object> map : payRecordList){
					 if(map.get("trade_method_type").equals(PayConstants.PayMethod.Alipay))
						 onlinePayAmount += Integer.parseInt(map.get("pay_amount").toString());
					 if(map.get("trade_method_type").equals(PayConstants.PayMethod.AlipayScan))
						 onlinePayAmount += Integer.parseInt(map.get("pay_amount").toString());
					 if(map.get("trade_method_type").equals(PayConstants.PayMethod.Wxpay))
						 onlinePayAmount += Integer.parseInt(map.get("pay_amount").toString());
					 if(map.get("trade_method_type").equals(PayConstants.PayMethod.WxpayScan))
						 onlinePayAmount += Integer.parseInt(map.get("pay_amount").toString());
					 if(map.get("trade_method_type").equals(PayConstants.PayMethod.Card))
						 cardPayAmount += Integer.parseInt(map.get("pay_amount").toString());
					 if(map.get("trade_method_type").equals(PayConstants.PayMethod.ParentCard))
						 parentCardPayAmount += Integer.parseInt(map.get("pay_amount").toString());
					 if(map.get("trade_method_type").equals(PayConstants.PayMethod.OfflinePay))
						 offlinePayAmount += Integer.parseInt(map.get("pay_amount").toString());
					 if(map.get("trade_method_type").equals(PayConstants.PayMethod.Balance))
						 onlinePayAmount += Integer.parseInt(map.get("pay_amount").toString());
					 if(map.get("trade_method_type").equals(PayConstants.PayMethod.Coupon))
						 couponAmount += Integer.parseInt(map.get("pay_amount").toString());
				 }
				 	totalAmount = cardPayAmount + offlinePayAmount + onlinePayAmount + parentCardPayAmount + couponAmount;
			 } 
			 
			 //验证退项目退款申请表
			 String sql = "select * from tb_order_refund_apply where order_num = '"+order.getOrderNum()+"'";
			 log.info("sql..."+sql);
			 //循环检查退款申请表，最大等待时间15s
			 List<Map<String,Object>> applyList = DBMapper.query(sql);
			 for(int cycle = 0;cycle < 5 ;cycle++){
				 if(applyList ==null || applyList.size() == 0){
					 applyList = DBMapper.query(sql);
					 log.info("回单退款后,退款申请表无数据,循环检查中...");
					 waitto(3);
					 }
			 }
			 Assert.assertEquals(applyList.size(), 1);
			 Map<String,Object> apply = applyList.get(0);
			 boolean autoAssessor=false;//需要人工审核
			 JSONObject jon = JSON.parseObject(apply.get("pay_detail").toString());
			 int onlinePay = Integer.parseInt(jon.get("onlinePayAmount").toString());
			 int totalPay = Integer.parseInt(jon.get("totalAmount").toString());
			 int amount = Integer.parseInt(apply.get("amount").toString());
			 Object platCompany = order.getHospitalCompany().getPlatformCompanyId();
			 if(amount <= totalPay - onlinePay && (platCompany == null || Integer.parseInt(platCompany.toString()) < 3))
				 autoAssessor = true;
			 if(amount == 0 )
				 autoAssessor = true;
			 Assert.assertEquals(Integer.parseInt(apply.get("account_id").toString()), order.getAccount().getId().intValue());
			 Assert.assertEquals(Integer.parseInt(apply.get("from_site").toString()), order.getHospital().getId());
			 Assert.assertEquals(Integer.parseInt(apply.get("hospital_company_id").toString()), order.getHospitalCompany().getId().intValue());
			 if(refundPrice >= order.getOrderPrice().intValue()){//退款金额>=订单金额时全退
				 Assert.assertEquals(Integer.parseInt(apply.get("amount").toString()), order.getOrderPrice().intValue());
				 Assert.assertEquals(Integer.parseInt(apply.get("refund_type").toString()), 0);//全部退款
			 }else{
				 Assert.assertEquals(Integer.parseInt(apply.get("amount").toString()), refundPrice);
				 Assert.assertEquals(Integer.parseInt(apply.get("refund_type").toString()), 1);//部分退款
			 }

			 Assert.assertEquals(Integer.parseInt(apply.get("scene").toString()), 3);//回单
			 if(autoAssessor){
				 Assert.assertEquals(apply.get("reason").toString(),"系统自动同意");
				 Assert.assertEquals(Integer.parseInt(apply.get("status").toString()),1);//同意审批
				 }
			 else{
				 Assert.assertNull(apply.get("reason"));
				 Assert.assertEquals(Integer.parseInt(apply.get("status").toString()), 0);//待审核状态
			 }
				
			 Assert.assertEquals(Integer.parseInt(apply.get("is_deleted").toString()), 0);
			 JSONObject jo = JSON.parseObject(apply.get("pay_detail").toString());
			 Assert.assertEquals(cardPayAmount,Integer.parseInt(jo.get("cardPayAmount").toString()));
			 Assert.assertEquals(offlinePayAmount,Integer.parseInt(jo.get("offlinePayAmount").toString()));
			 Assert.assertEquals(onlinePayAmount,Integer.parseInt(jo.get("onlinePayAmount").toString()));
			 Assert.assertEquals(parentCardPayAmount,Integer.parseInt(jo.get("parentCardPayAmount").toString()));
			 if(jo.get("couponAmount")!=null)
			 	Assert.assertEquals(couponAmount,Integer.parseInt(jo.get("couponAmount").toString()));
			 Assert.assertEquals(totalAmount,Integer.parseInt(jo.get("totalAmount").toString()));

			 int refund_id = Integer.parseInt(apply.get("id").toString());
			 //验证退款申请记录表插入1条记录，1条待审核
			 String logSql = "select * from tb_order_refund_apply_record where refund_id ="+refund_id +" order by id";
			 List<Map<String,Object>> applyLogList = DBMapper.query(logSql);
			 if(autoAssessor)//退款金额为0或者自动审核
				 Assert.assertEquals(applyLogList.size(), 2);
			 else
				 Assert.assertEquals(applyLogList.size(), 1);
			 Map<String,Object> firstApply = applyLogList.get(0);
			 Assert.assertEquals(Integer.parseInt(firstApply.get("account_id").toString()), order.getAccount().getId().intValue());
			 Assert.assertEquals(Integer.parseInt(firstApply.get("from_site").toString()), order.getHospital().getId());
			 Assert.assertEquals(Integer.parseInt(firstApply.get("hospital_company_id").toString()), order.getHospitalCompany().getId().intValue());
			 if(refundPrice >= order.getOrderPrice().intValue())
				 Assert.assertEquals(Integer.parseInt(firstApply.get("amount").toString()), order.getOrderPrice().intValue());
			 else
				 Assert.assertEquals(Integer.parseInt(firstApply.get("amount").toString()), refundPrice);
			 Assert.assertEquals(Integer.parseInt(firstApply.get("status").toString()),0);//待审核
			 Assert.assertNull(firstApply.get("reason"));
			 JSONObject jfirst = JSON.parseObject(apply.get("pay_detail").toString());
			 Assert.assertEquals(cardPayAmount,Integer.parseInt(jfirst.get("cardPayAmount").toString()));
			 Assert.assertEquals(offlinePayAmount,Integer.parseInt(jfirst.get("offlinePayAmount").toString()));
			 Assert.assertEquals(onlinePayAmount,Integer.parseInt(jfirst.get("onlinePayAmount").toString()));
			 Assert.assertEquals(parentCardPayAmount,Integer.parseInt(jfirst.get("parentCardPayAmount").toString()));
			 if(jfirst.get("couponAmount")!=null)
				 Assert.assertEquals(couponAmount,Integer.parseInt(jfirst.get("couponAmount").toString()));
			 Assert.assertEquals(totalAmount,Integer.parseInt(jfirst.get("totalAmount").toString()));
			 if(refundPrice == 0 || autoAssessor){
				 Map<String,Object> secondApply = applyLogList.get(1);
				 Assert.assertEquals(Integer.parseInt(secondApply.get("account_id").toString()), order.getAccount().getId().intValue());
				 Assert.assertEquals(Integer.parseInt(secondApply.get("from_site").toString()), order.getHospital().getId());
				 Assert.assertEquals(Integer.parseInt(secondApply.get("hospital_company_id").toString()), order.getHospitalCompany().getId().intValue());
				 if(refundPrice >= order.getOrderPrice().intValue())
					 Assert.assertEquals(Integer.parseInt(firstApply.get("amount").toString()), order.getOrderPrice().intValue());
				 else
				 	Assert.assertEquals(Integer.parseInt(secondApply.get("amount").toString()), refundPrice);
				 Assert.assertEquals(Integer.parseInt(secondApply.get("status").toString()),1);//审核通过
				 Assert.assertEquals(secondApply.get("reason").toString(),"系统自动同意");
				 if(refundPrice == 0)
					 Assert.assertEquals(Integer.parseInt(secondApply.get("is_deleted").toString()),1);
			 }
			return autoAssessor;
		 }else if (action.equals("撤订单")){
			 //提取订单支付金额,各种方式下支付金额之和
			 String tradeSql = "select * from tb_trade_order where ref_order_num = '"+order.getOrderNum()+"'  and trade_type = "+PayConstants.TradeType.pay;
			 List<Map<String,Object>> tradeList = DBMapper.query(tradeSql);
			 int succ_amount = 0;
			 int cardPayAmount = 0;
			 int offlinePayAmount = 0;
			 int onlinePayAmount = 0;
			 int parentCardPayAmount = 0;
			 int totalAmount = 0;
			 int couponAmount = 0;
			 if(tradeList.size() > 0){
				 for(Map<String,Object> m : tradeList)
					 	succ_amount += Integer.parseInt(m.get("succ_amount").toString());
				 
				 String payRecordSql = "select * from tb_trade_pay_record where ref_order_num =  '"+order.getOrderNum()+"' and ref_order_type = "+PayConstants.TradeType.pay 
						 + " and pay_status = "+PayConstants.TradeStatus.Successful;
				 List<Map<String,Object>> payRecordList = DBMapper.query(payRecordSql);
				
				 for(Map<String,Object> map : payRecordList){
					 if(map.get("trade_method_type").equals(PayConstants.PayMethod.Alipay))
						 onlinePayAmount += Integer.parseInt(map.get("pay_amount").toString());
					 if(map.get("trade_method_type").equals(PayConstants.PayMethod.AlipayScan))
						 onlinePayAmount += Integer.parseInt(map.get("pay_amount").toString());
					 if(map.get("trade_method_type").equals(PayConstants.PayMethod.Wxpay))
						 onlinePayAmount += Integer.parseInt(map.get("pay_amount").toString());
					 if(map.get("trade_method_type").equals(PayConstants.PayMethod.WxpayScan))
						 onlinePayAmount += Integer.parseInt(map.get("pay_amount").toString());
					 if(map.get("trade_method_type").equals(PayConstants.PayMethod.Card))
						 cardPayAmount += Integer.parseInt(map.get("pay_amount").toString());
					 if(map.get("trade_method_type").equals(PayConstants.PayMethod.ParentCard))
						 parentCardPayAmount += Integer.parseInt(map.get("pay_amount").toString());
					 if(map.get("trade_method_type").equals(PayConstants.PayMethod.OfflinePay))
						 offlinePayAmount += Integer.parseInt(map.get("pay_amount").toString());
					 if(map.get("trade_method_type").equals(PayConstants.PayMethod.Balance))
						 onlinePayAmount += Integer.parseInt(map.get("pay_amount").toString());
					 if(map.get("trade_method_type").equals(PayConstants.PayMethod.Coupon))
						 couponAmount += Integer.parseInt(map.get("pay_amount").toString());
				 }
				 	totalAmount = cardPayAmount + offlinePayAmount + onlinePayAmount + parentCardPayAmount + couponAmount;
			 } 
			 
			 //验证撤销订单后，退款申请表系统自动同意
			 String sql = "select * from tb_order_refund_apply where order_num = '"+order.getOrderNum()+"'";
			 log.info("sql..."+sql);
			 //循环检查退款申请表，最大等待时间15s
			 List<Map<String,Object>> applyList = DBMapper.query(sql);
			 for(int cycle = 0;cycle < 5 ;cycle++){
				 if(applyList ==null || applyList.size() == 0){
					 applyList = DBMapper.query(sql);
					 log.info("撤销订单后,退款申请表无数据,循环检查中...");
					 waitto(3);
					 }
			 }
			 Assert.assertEquals(applyList.size(), 1);
			 Map<String,Object> apply = applyList.get(0);
			 //////判断是否包括线上退款
			 boolean autoAssessor=false;//需要人工审核
			 JSONObject jon = JSON.parseObject(apply.get("pay_detail").toString());
			 int onlinePay = Integer.parseInt(jon.get("onlinePayAmount").toString());
			 int totalPay = Integer.parseInt(jon.get("totalAmount").toString());
			 int amount = Integer.parseInt(apply.get("amount").toString());
			 Object platCompany = order.getHospitalCompany().getPlatformCompanyId();
			 if(amount <= totalPay - onlinePay && (platCompany == null || Integer.parseInt(platCompany.toString()) < 3))
				 autoAssessor = true;
			 if(amount == 0)
				 autoAssessor = true;
			 Assert.assertEquals(Integer.parseInt(apply.get("account_id").toString()), order.getAccount().getId().intValue());
			 Assert.assertEquals(Integer.parseInt(apply.get("from_site").toString()), order.getHospital().getId());
			 Assert.assertEquals(Integer.parseInt(apply.get("hospital_company_id").toString()), order.getHospitalCompany().getId().intValue());
			 Assert.assertEquals(amount, succ_amount);
			 Assert.assertEquals(Integer.parseInt(apply.get("refund_type").toString()), 0);//全额退款
			 Assert.assertEquals(Integer.parseInt(apply.get("scene").toString()), 3);//回单退款
			 if(!autoAssessor){
				 Assert.assertNull(apply.get("reason"));
				 Assert.assertEquals(Integer.parseInt(apply.get("status").toString()), 0);//待审核
				 }
			 else{
				 Assert.assertEquals(apply.get("reason").toString(),"系统自动同意");
				 Assert.assertEquals(Integer.parseInt(apply.get("status").toString()), 1);//审核通过
				 }
			
			 Assert.assertEquals(Integer.parseInt(apply.get("is_deleted").toString()), 0);
			 JSONObject jo = JSON.parseObject(apply.get("pay_detail").toString());
			 Assert.assertEquals(cardPayAmount,Integer.parseInt(jo.get("cardPayAmount").toString()));
			 Assert.assertEquals(offlinePayAmount,Integer.parseInt(jo.get("offlinePayAmount").toString()));
			 Assert.assertEquals(onlinePayAmount,Integer.parseInt(jo.get("onlinePayAmount").toString()));
			 Assert.assertEquals(parentCardPayAmount,Integer.parseInt(jo.get("parentCardPayAmount").toString()));
			 if(jo.get("couponAmount")!=null)
				 Assert.assertEquals(couponAmount,Integer.parseInt(jo.get("couponAmount").toString()));
			 Assert.assertEquals(totalAmount,Integer.parseInt(jo.get("totalAmount").toString()));

			 int refund_id = Integer.parseInt(apply.get("id").toString());
			 //验证退款申请记录表插入1条记录，1条待审核
			 String logSql = "select * from tb_order_refund_apply_record where refund_id ="+refund_id +" order by id";
			 List<Map<String,Object>> applyLogList = DBMapper.query(logSql);
			 if(!autoAssessor){
				 Assert.assertEquals(applyLogList.size(), 1);
			 	Map<String,Object> firstApply = applyLogList.get(0);
			 	Assert.assertEquals(Integer.parseInt(firstApply.get("account_id").toString()), order.getAccount().getId().intValue());
			 	Assert.assertEquals(Integer.parseInt(firstApply.get("from_site").toString()), order.getHospital().getId());
			 	Assert.assertEquals(Integer.parseInt(firstApply.get("hospital_company_id").toString()), order.getHospitalCompany().getId().intValue());
			 	Assert.assertEquals(Integer.parseInt(firstApply.get("amount").toString()), succ_amount);
			 	Assert.assertEquals(Integer.parseInt(firstApply.get("status").toString()),0);//待审核
			 	Assert.assertNull(firstApply.get("reason"));
			 	JSONObject jfirst = JSON.parseObject(apply.get("pay_detail").toString());
			 	Assert.assertEquals(cardPayAmount,Integer.parseInt(jfirst.get("cardPayAmount").toString()));
			 	Assert.assertEquals(offlinePayAmount,Integer.parseInt(jfirst.get("offlinePayAmount").toString()));
			 	Assert.assertEquals(onlinePayAmount,Integer.parseInt(jfirst.get("onlinePayAmount").toString()));
			 	Assert.assertEquals(parentCardPayAmount,Integer.parseInt(jfirst.get("parentCardPayAmount").toString()));
			 	if(jfirst.get("couponAmount")!=null)
					 Assert.assertEquals(couponAmount,Integer.parseInt(jfirst.get("couponAmount").toString()));
			 	Assert.assertEquals(totalAmount,Integer.parseInt(jfirst.get("totalAmount").toString()));
			 } else{
				 Assert.assertEquals(applyLogList.size(), 2);
				 //第一条待审核
				 Map<String,Object> firstApply = applyLogList.get(0);
				 Assert.assertEquals(Integer.parseInt(firstApply.get("account_id").toString()), order.getAccount().getId().intValue());
				 Assert.assertEquals(Integer.parseInt(firstApply.get("from_site").toString()), order.getHospital().getId());
				 Assert.assertEquals(Integer.parseInt(firstApply.get("hospital_company_id").toString()), order.getHospitalCompany().getId().intValue());
				 Assert.assertEquals(Integer.parseInt(firstApply.get("amount").toString()), succ_amount);
				 Assert.assertEquals(Integer.parseInt(firstApply.get("status").toString()),0);//待审核
				 Assert.assertNull(firstApply.get("reason"));
				 JSONObject jfirst = JSON.parseObject(apply.get("pay_detail").toString());
				 Assert.assertEquals(cardPayAmount,Integer.parseInt(jfirst.get("cardPayAmount").toString()));
				 Assert.assertEquals(offlinePayAmount,Integer.parseInt(jfirst.get("offlinePayAmount").toString()));
				 Assert.assertEquals(onlinePayAmount,Integer.parseInt(jfirst.get("onlinePayAmount").toString()));
				 Assert.assertEquals(parentCardPayAmount,Integer.parseInt(jfirst.get("parentCardPayAmount").toString()));
				 if(jfirst.get("couponAmount")!=null)
				 Assert.assertEquals(couponAmount,Integer.parseInt(jfirst.get("couponAmount").toString()));
				 Assert.assertEquals(totalAmount,Integer.parseInt(jfirst.get("totalAmount").toString()));
				 //第二条审核通过
				 Map<String,Object> secondApply = applyLogList.get(1);
				 Assert.assertEquals(Integer.parseInt(secondApply.get("account_id").toString()), order.getAccount().getId().intValue());
				 Assert.assertEquals(Integer.parseInt(secondApply.get("from_site").toString()), order.getHospital().getId());
				 Assert.assertEquals(Integer.parseInt(secondApply.get("hospital_company_id").toString()), order.getHospitalCompany().getId().intValue());
				 Assert.assertEquals(Integer.parseInt(secondApply.get("amount").toString()), succ_amount);
				 Assert.assertEquals(Integer.parseInt(secondApply.get("status").toString()),1);//审核通过
				 Assert.assertEquals(secondApply.get("reason").toString(),"系统自动同意");
				 if(succ_amount == 0)
						 Assert.assertEquals(Integer.parseInt(secondApply.get("is_deleted").toString()),1);
				 }

			 return autoAssessor;
			 
		 }else{
			 System.out.println("项目类型错误...");
			 return false;
		 }
		 
	}
	
	/**
	 * 验证手动退款退款申请表/退款申请就表
	 * @param order
	 * @param refundPrice
	 * @param remarks
	 * @param beforeOrderStatus订单之前状态
	 * @throws SqlException
	 */
	@SuppressWarnings("deprecation")
	public static void checkManualRefundApply(Order order,long refundPrice,String remarks,int managerId,int  beforeOrderStatus) throws SqlException{
		 //提取订单支付金额,各种方式下支付金额之和
		 String tradeSql = "select * from tb_trade_order where ref_order_num = '"+order.getOrderNum()+"'  and trade_type = "+PayConstants.TradeType.pay;
		 List<Map<String,Object>> tradeList = DBMapper.query(tradeSql);
//		 int succ_amount = 0;
		 int cardPayAmount = 0;
		 int offlinePayAmount = 0;
		 int onlinePayAmount = 0;
		 int parentCardPayAmount = 0;
		 int couponAmount = 0;
		 int totalAmount = 0;
		 if(tradeList.size() > 0){
//			 for(Map<String,Object> m : tradeList)
//				 	succ_amount += Integer.parseInt(m.get("succ_amount").toString());
			 
			 String payRecordSql = "select * from tb_trade_pay_record where ref_order_num =  '"+order.getOrderNum()+"' and ref_order_type = "+PayConstants.TradeType.pay 
					 + " and pay_status = "+PayConstants.TradeStatus.Successful;
			 List<Map<String,Object>> payRecordList = DBMapper.query(payRecordSql);
			
			 for(Map<String,Object> map : payRecordList){
				 if(map.get("trade_method_type").equals(PayConstants.PayMethod.Alipay))
					 onlinePayAmount += Integer.parseInt(map.get("pay_amount").toString());
				 if(map.get("trade_method_type").equals(PayConstants.PayMethod.AlipayScan))
					 onlinePayAmount += Integer.parseInt(map.get("pay_amount").toString());
				 if(map.get("trade_method_type").equals(PayConstants.PayMethod.Wxpay))
					 onlinePayAmount += Integer.parseInt(map.get("pay_amount").toString());
				 if(map.get("trade_method_type").equals(PayConstants.PayMethod.WxpayScan))
					 onlinePayAmount += Integer.parseInt(map.get("pay_amount").toString());
				 if(map.get("trade_method_type").equals(PayConstants.PayMethod.Card))
					 cardPayAmount += Integer.parseInt(map.get("pay_amount").toString());
				 if(map.get("trade_method_type").equals(PayConstants.PayMethod.ParentCard))
					 parentCardPayAmount += Integer.parseInt(map.get("pay_amount").toString());
				 if(map.get("trade_method_type").equals(PayConstants.PayMethod.OfflinePay))
					 offlinePayAmount += Integer.parseInt(map.get("pay_amount").toString());
				 if(map.get("trade_method_type").equals(PayConstants.PayMethod.Balance))
					 onlinePayAmount += Integer.parseInt(map.get("pay_amount").toString());
				 if(map.get("trade_method_type").equals(PayConstants.PayMethod.Coupon))
					 couponAmount += Integer.parseInt(map.get("pay_amount").toString());
			 }
			 	totalAmount = cardPayAmount + offlinePayAmount + onlinePayAmount + parentCardPayAmount + couponAmount;
		 } 
		 
		 //验证手动退款后，退款申请表系统自动同意
		 String sql = "select * from tb_order_refund_apply where order_num = '"+order.getOrderNum()+"' order by id desc";
		 log.info("sql..."+sql);
		 List<Map<String,Object>> applyList = DBMapper.query(sql);
		 Assert.assertTrue(applyList.size()>= 1);
		 //1.最新1条是手动退款记录
		 Map<String,Object> apply = applyList.get(0);
		 Assert.assertEquals(Integer.parseInt(apply.get("account_id").toString()), order.getAccount().getId().intValue());
		 Assert.assertEquals(Integer.parseInt(apply.get("from_site").toString()), order.getHospital().getId());
		 Assert.assertEquals(Integer.parseInt(apply.get("hospital_company_id").toString()), order.getHospitalCompany().getId().intValue());
		 Assert.assertEquals(Integer.parseInt(apply.get("amount").toString()), refundPrice);
		 if(refundPrice == order.getOrderPrice().longValue())
			 Assert.assertEquals(Integer.parseInt(apply.get("refund_type").toString()), 0);//全额退款
		 else
			 Assert.assertEquals(Integer.parseInt(apply.get("refund_type").toString()), 1);//部分退款
		 Assert.assertEquals(Integer.parseInt(apply.get("scene").toString()), 5);//手动退款
		 Assert.assertEquals(apply.get("reason").toString(),remarks);
		 Assert.assertEquals(Integer.parseInt(apply.get("status").toString()), 1);//同意退款
		 Assert.assertEquals(Integer.parseInt(apply.get("is_deleted").toString()), 0);
		 JSONObject jo = JSON.parseObject(apply.get("pay_detail").toString());
		 Assert.assertEquals(cardPayAmount,Integer.parseInt(jo.get("cardPayAmount").toString()));
		 Assert.assertEquals(offlinePayAmount,Integer.parseInt(jo.get("offlinePayAmount").toString()));
		 Assert.assertEquals(onlinePayAmount,Integer.parseInt(jo.get("onlinePayAmount").toString()));
		 Assert.assertEquals(parentCardPayAmount,Integer.parseInt(jo.get("parentCardPayAmount").toString()));
		if(jo.get("couponAmount")!=null)
			Assert.assertEquals(couponAmount,Integer.parseInt(jo.get("couponAmount").toString()));
		 Assert.assertEquals(totalAmount,Integer.parseInt(jo.get("totalAmount").toString()));
		 
		 int refund_id = Integer.parseInt(apply.get("id").toString());
		 //验证退款申请记录表插入2条记录，1条待审核，1条审核通过
		 String logSql = "select * from tb_order_refund_apply_record where refund_id ="+refund_id +" order by id";
		 List<Map<String,Object>> applyLogList = DBMapper.query(logSql);
		 Assert.assertEquals(applyLogList.size(), 2);
		 Map<String,Object> firstApply = applyLogList.get(0);
		 Assert.assertEquals(Integer.parseInt(firstApply.get("account_id").toString()), order.getAccount().getId().intValue());
		 Assert.assertEquals(Integer.parseInt(firstApply.get("from_site").toString()), order.getHospital().getId());
		 Assert.assertEquals(Integer.parseInt(firstApply.get("hospital_company_id").toString()), order.getHospitalCompany().getId().intValue());
		 Assert.assertEquals(Integer.parseInt(firstApply.get("amount").toString()), refundPrice);
		 Assert.assertEquals(Integer.parseInt(firstApply.get("status").toString()),0);//待审核
		 JSONObject jfirst = JSON.parseObject(apply.get("pay_detail").toString());
		 Assert.assertEquals(cardPayAmount,Integer.parseInt(jfirst.get("cardPayAmount").toString()));
		 Assert.assertEquals(offlinePayAmount,Integer.parseInt(jfirst.get("offlinePayAmount").toString()));
		 Assert.assertEquals(onlinePayAmount,Integer.parseInt(jfirst.get("onlinePayAmount").toString()));
		 Assert.assertEquals(parentCardPayAmount,Integer.parseInt(jfirst.get("parentCardPayAmount").toString()));
		 if(jfirst.get("couponAmount")!=null)
			Assert.assertEquals(couponAmount,Integer.parseInt(jfirst.get("couponAmount").toString()));
		 Assert.assertEquals(totalAmount,Integer.parseInt(jfirst.get("totalAmount").toString()));
		 System.out.println("账号。。。"+managerId);
		 Assert.assertEquals(firstApply.get("operator_name").toString(),AccountChecker.getAccountById(managerId).getName());//操作人名称
		 
		 Map<String,Object> secondApply = applyLogList.get(1);
		 Assert.assertEquals(Integer.parseInt(secondApply.get("account_id").toString()), order.getAccount().getId().intValue());
		 Assert.assertEquals(Integer.parseInt(secondApply.get("from_site").toString()), order.getHospital().getId());
		 Assert.assertEquals(Integer.parseInt(secondApply.get("hospital_company_id").toString()), order.getHospitalCompany().getId().intValue());
		 Assert.assertEquals(Integer.parseInt(secondApply.get("amount").toString()), refundPrice);
		 Assert.assertEquals(Integer.parseInt(secondApply.get("status").toString()),1);//审核通过
		 Assert.assertEquals(secondApply.get("reason").toString(),remarks);
		 Assert.assertEquals(secondApply.get("operator_name").toString(),AccountChecker.getAccountById(managerId).getName());//操作人名称
		 Assert.assertEquals(Integer.parseInt(secondApply.get("operator").toString()),managerId);//系统账户
		 Assert.assertEquals(Integer.parseInt(secondApply.get("before_refund_status").toString()),beforeOrderStatus);//之前状态
		 Assert.assertEquals(Integer.parseInt(secondApply.get("after_refund_status").toString()),order.getStatus());//之后状态
		 JSONObject jsecond = JSON.parseObject(secondApply.get("pay_detail").toString());
		 Assert.assertEquals(cardPayAmount,Integer.parseInt(jsecond.get("cardPayAmount").toString()));
		 Assert.assertEquals(offlinePayAmount,Integer.parseInt(jsecond.get("offlinePayAmount").toString()));
		 Assert.assertEquals(onlinePayAmount,Integer.parseInt(jsecond.get("onlinePayAmount").toString()));
		 Assert.assertEquals(parentCardPayAmount,Integer.parseInt(jsecond.get("parentCardPayAmount").toString()));
		if(jsecond.get("couponAmount")!=null)
			Assert.assertEquals(couponAmount,Integer.parseInt(jsecond.get("couponAmount").toString()));
		Assert.assertEquals(totalAmount,Integer.parseInt(jsecond.get("totalAmount").toString()));

		 
		 //2.如果存在回单的退款申请直接拒绝
		 if(applyList.size()>1){
			 for(int k=1;k<applyList.size();k++){
				 Map<String,Object> apply2 = applyList.get(k);
				 if(Integer.parseInt(apply2.get("amount").toString()) == 0)
					 continue;
				 Assert.assertNotEquals(Integer.parseInt(apply2.get("scene").toString()), 3);//回单退款
				 Assert.assertEquals(apply2.get("reason").toString(),"系统自动拒绝");
				 Assert.assertEquals(Integer.parseInt(apply2.get("status").toString()), 2);//拒绝退款
				 
				 int refund_id2 = Integer.parseInt(apply2.get("id").toString());
				 //验证退款申请记录表插入2条记录，1条待审核，1条审核拒绝
				 String logSql2 = "select * from tb_order_refund_apply_record where refund_id ="+refund_id2 +" order by id";
				 List<Map<String,Object>> applyLogList2 = DBMapper.query(logSql2);
				 Assert.assertEquals(applyLogList2.size(), 2);
				 Map<String,Object> firstApply2 = applyLogList2.get(0);
				 Assert.assertEquals(Integer.parseInt(firstApply2.get("status").toString()),0);//待审核
				 
				 Map<String,Object> secondApply2 = applyLogList2.get(1);
				 Assert.assertEquals(Integer.parseInt(secondApply2.get("status").toString()),2);//拒绝退款
				 Assert.assertEquals(secondApply.get("reason").toString(),"系统自动拒绝");
				 Assert.assertEquals(secondApply.get("operator_name").toString(),"系统");
				 Assert.assertEquals(Integer.parseInt(secondApply.get("operator").toString()),-1);//系统账户	 
			 }
			
		 }
	}

	/**
	 * 获取某个医院的待审批记录(全额退款）
	 * @param hospitalId
	 * @return
	 */
	public static List<Order> getRefundFullMoneyApplyOrderList(int hospitalId){
		List<Order> orderList = new ArrayList<>();
		String sql = "select * from tb_order_refund_apply where scene =  3 and status = 0  and is_deleted = 0  and refund_type = 0 and from_site = "+hospitalId+" order by apply_time ";

		log.info("sql..."+sql);
		List<Map<String,Object>> dblist = null;
		try {
			dblist = DBMapper.query(sql);
			if(dblist != null && dblist.size()>0){
				for(int k=0;k<dblist.size();k++){
					String orderNum = dblist.get(k).get("order_num").toString();
					int account_id = Integer.parseInt(dblist.get(k).get("account_id").toString());
					Order order = getOrderInfo(orderNum);
					//必须在mongo中存在
					List<Map<String,Object>> mongList = MongoDBUtils.query("{'id':"+order.getId()+"}", MONGO_COLLECTION);
					if(mongList != null && mongList.size()>0)
						if(order.getAccount().getId().equals(account_id))//过滤用户不一致的脏数据
								orderList.add(order);


				}
			}
		} catch (SqlException e) {
			e.printStackTrace();
		}
		return orderList;
	}


	/**
	 * 获取某个医院的待审批记录(部分退款）
	 * @param hospitalId
	 * @return
	 */
	public static List<Order> getRefundPartMoneyApplyOrderList(int hospitalId){
		List<Order> orderList = new ArrayList<>();
		String sql = "select * from tb_order_refund_apply where scene =  3 and status = 0  and is_deleted = 0  and refund_type = 1 and from_site = "+hospitalId+" order by apply_time ";

		log.info("sql..."+sql);
		List<Map<String,Object>> dblist = null;
		try {
			dblist = DBMapper.query(sql);
			if(dblist != null && dblist.size()>0){
				for(int k=0;k<dblist.size();k++){
					String orderNum = dblist.get(k).get("order_num").toString();
					int account_id = Integer.parseInt(dblist.get(k).get("account_id").toString());
					Order order = getOrderInfo(orderNum);
					//必须在mongo中存在
					List<Map<String,Object>> mongList = MongoDBUtils.query("{'id':"+order.getId()+"}", MONGO_COLLECTION);
					if(mongList != null && mongList.size()>0)
						if(order.getAccount().getId().equals(account_id))//过滤用户不一致的脏数据
							orderList.add(order);
				}
			}
		} catch (SqlException e) {
			e.printStackTrace();
		}
		return orderList;
	}
	
	/*****************************验 证 订 单 结 算****************************************/
	/**
	 * 下单/回单拒绝退款（医院未结算）/撤单（医院未结算）
	 * 查看订单结算数据
	 * @param order
	 */
	public static void check_Book_ExamOrderSettlement(Order order){
		check_Order_UnSettlementRefund(order, SettlementHospitalConfirmEnum.UNSETTLEMENT.getCode().intValue());
	}
	
	/**
	 * 回单同意退款
	 * 查看订单结算数据
	 * @param order
	 * @param amount 退款金额
	 * @param settleStatus 结算状态
	 */
	public static void check_AgreeRefund_ExamOrderSettlement(Order order,long amount,int settleStatus){
		if(amount > 0)
			check_Order_NeedSettlementRefund(order,settleStatus);
		else
			check_Order_UnSettlementRefund(order,settleStatus);

	}
	
	
	public static void check_Order_UnSettlementRefund(Order order,int confirmEnum){
		String sql = "select * from tb_exam_order_settlement where order_num ='"+order.getOrderNum()+"'";
		List<Map<String, Object>> list;
		try {
			list = DBMapper.query(sql);
			if(list == null || list.size() == 0){
				log.info("未记录订单结算关系表,该订单是老订单");
				return;

			}
			if(list.size()>1){
				throw new RuntimeException("1条订单有多个结算标记，有错误!!");
			}
			log.info("验证订单结算关系START~~~~~~~~~~~~");
			Map<String,Object> map = list.get(0);
//			if(order.getInsertTime().compareTo(simplehms.parse("2018-08-20 00:00:00"))>=0)//渠道结算上线之前的订单写入是按照订单的hospitalId存储
//				Assert.assertEquals(Integer.parseInt(map.get("organization_id").toString()),order.getHospital().getId());
//			else
				Assert.assertEquals(Integer.parseInt(map.get("organization_id").toString()),order.getFromSite().intValue());
			Assert.assertEquals(Integer.parseInt(map.get("hospital_company_id").toString()),order.getHospitalCompany().getId().intValue());
			Assert.assertEquals(Integer.parseInt(map.get("hospital_settlement_status").toString()),confirmEnum);
			Assert.assertEquals(Integer.parseInt(map.get("refund_settlement").toString()),ExamOrderRefundSettleEnum.NOT_NEED_REFUND.getCode().intValue());//不需结算退款
			if(order.getFromSiteOrgType() == OrganizationTypeEnum.CHANNEL.getCode()){//渠道订单退款渠道的退款状态需校验
				Assert.assertEquals(Integer.parseInt(map.get("channel_refund_settlement").toString()),ExamOrderRefundSettleEnum.NOT_NEED_REFUND.getCode().intValue());//不需结算退款
				Assert.assertEquals(Integer.parseInt(map.get("channel_settlement_status").toString()),confirmEnum);//本属性在回单/撤销订单时不变
				Assert.assertEquals(Integer.parseInt(map.get("channel_company_id").toString()),order.getChannelCompany().getId().intValue());
				log.info("本订单是渠道订单,校验渠道订单退款状态"+ExamOrderRefundSettleEnum.NOT_NEED_REFUND.getCode().intValue()+"..订单结算状态"+confirmEnum);
			}
			Assert.assertNull(map.get("settlement_batch_sn"));
			Assert.assertEquals(Integer.parseInt(map.get("is_deleted").toString()),0);//未删除
			log.info("验证订单结算关系END~~~~~~~~~~~~");
		} catch (SqlException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
//		} catch (ParseException e) {
//			e.printStackTrace();
//		}
	}
	
	/**
	 * 验证回单同意退款，退款金额>0时
	 * @param order
	 */
	public static void check_Order_NeedSettlementRefund(Order order,int confirmEnum){
		String sql = "select * from tb_exam_order_settlement where order_num ='"+order.getOrderNum()+"'";
		List<Map<String, Object>> list;
		try {
			list = DBMapper.query(sql);
			if(list == null || list.size() == 0){
				log.info("未记录订单结算关系表,该订单是老订单");
				return;

			}
			if(list.size()>1){
				throw new RuntimeException("1条订单有多个结算标记，有错误!!");
			}
			log.info("验证订单结算关系START~~~~~~~~~~~~");
			Map<String,Object> map = list.get(0);
			Assert.assertEquals(Integer.parseInt(map.get("organization_id").toString()),order.getFromSite().intValue());
			Assert.assertEquals(Integer.parseInt(map.get("hospital_company_id").toString()),order.getHospitalCompany().getId().intValue());
			Assert.assertEquals(Integer.parseInt(map.get("hospital_settlement_status").toString()),confirmEnum);//本属性在回单/撤销订单时不变
			Assert.assertEquals(Integer.parseInt(map.get("refund_settlement").toString()),ExamOrderRefundSettleEnum.NEED_REFUND.getCode().intValue());//需结算退款
			if(order.getFromSiteOrgType() == OrganizationTypeEnum.CHANNEL.getCode()){//渠道订单退款渠道的退款状态需校验
				Assert.assertEquals(Integer.parseInt(map.get("channel_refund_settlement").toString()),ExamOrderRefundSettleEnum.NEED_REFUND.getCode().intValue());//需结算退款
				Assert.assertEquals(Integer.parseInt(map.get("channel_settlement_status").toString()),confirmEnum);//本属性在回单/撤销订单时不变
				Assert.assertEquals(Integer.parseInt(map.get("channel_company_id").toString()),order.getChannelCompany().getId().intValue());
				log.info("本订单是渠道订单,校验渠道订单退款状态"+ExamOrderRefundSettleEnum.NEED_REFUND.getCode().intValue()+"..订单结算状态"+confirmEnum);

			}
			Assert.assertNull(map.get("settlement_batch_sn"));
			Assert.assertEquals(Integer.parseInt(map.get("is_deleted").toString()),0);//未删除
			log.info("验证订单结算关系END~~~~~~~~~~~~");
		} catch (SqlException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
	}
	
	
	
	/*****************************公 共 方 法****************************************/

	
	/**
	 * C端撤销订单 ----公共抽离方法
	 * 
	 * @param hc
	 * @param orderId
	 * @param sendMsg
	 * @param checkDb
	 * @param checkMongo
	 * @throws SqlException
	 */
	public static void Run_MainOrderRevokeOrder(MyHttpClient hc, int orderId, boolean sendMsg, boolean checkDb,
			boolean checkMongo) throws SqlException {
		/***** 撤销订单 ******/
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("sendMsg", sendMsg + ""));
		//检查订单是否正在结算中
		boolean cannotRevoke = SettleChecker.isOrderInSettlement(orderId);
		HttpResult result = hc.post(Flag.MAIN, MainRevokerOrder, orderId);
		judgeRevokeOrder(result,orderId,cannotRevoke,checkDb,checkMongo);
	
	}


	/**
	 *
	 * @param result 接口返回的result对象
	 * @param orderId 订单id
	 * @param cannotRevoke 订单是否在结算中，在则不能撤销，不在则可以撤销
	 * @param checkDb 检查数据库
	 * @param checkMongo 检查mongo
	 */
	public static void judgeRevokeOrder(HttpResult result,int orderId,boolean cannotRevoke,boolean checkDb,boolean checkMongo)throws SqlException{
		if(result.getBody().contains("此订单已经回单，正在审批，不可撤销")){
			MyHttpClient opsHc = new MyHttpClient();
			onceLoginInSystem(opsHc, Flag.OPS, defManagerUsername, defManagerPasswd);
			List<String> orderNumList = new ArrayList<String>();
			orderNumList.add(getOrderInfo(orderId).getOrderNum());
			BatchOrderRefundAuditVO audit = new BatchOrderRefundAuditVO();
			audit.setOrderNumList(orderNumList);
			audit.setReason("自动化清理订单--自动同意");
			String request = JSON.toJSONString(audit);
			HttpResult opsResult = opsHc.post(Flag.OPS,AgreeRefund,request);
			log.info(opsResult.getBody());
			Assert.assertEquals(opsResult.getCode(),HttpStatus.SC_OK);

			opsHc.shutdown();
			// database
			if (checkDb) {
				// 校验订单状态
				String sql = "SELECT * FROM tb_order WHERE id  = ?";
				List<Map<String, Object>> retlist = null;
				try {
					retlist = DBMapper.query(sql, orderId);
				} catch (SqlException e) {
					e.printStackTrace();
				}
				for (Map<String, Object> r : retlist){
					int mysqlOrderStatus = Integer.parseInt(r.get("status").toString());
					Assert.assertTrue(mysqlOrderStatus ==
							OrderStatus.REVOCATION.intValue() || mysqlOrderStatus == OrderStatus.PART_BACK.intValue());
				}
				if (checkMongo) {
					waitto(mongoWaitTime);
					List<Map<String, Object>> list = MongoDBUtils.query("{'id':" + orderId + "}", MONGO_COLLECTION);
					Assert.assertEquals(1, list.size());
					int orderStatus = Integer.parseInt(list.get(0).get("status").toString());
					Assert.assertTrue( orderStatus == OrderStatus.REVOCATION
							.intValue() ||orderStatus == OrderStatus.PART_BACK.intValue());
				}

			}
			log.info("订单已同意申请退款!");
		}else{
			if(!cannotRevoke){
				Assert.assertEquals(result.getCode(), HttpStatus.SC_OK, "撤单失败:" + result.getBody() + "orderList:" + orderId);
				log.info("订单撤销返回验证.."+result.getBody());
				if(result.getBody() != null && !result.getBody().equals("{}")){
					List<Integer> successes = JsonPath.read(result.getBody(), "$.successes");
					Assert.assertTrue(successes.get(0).intValue()== orderId);
				}

				// database
				if (checkDb) {
					// 校验订单状态
					String sql = "SELECT * FROM tb_order WHERE id  = ?";
					List<Map<String, Object>> retlist = null;
					try {
						retlist = DBMapper.query(sql, orderId);
						for (Map<String, Object> r : retlist)
							Assert.assertEquals(Integer.parseInt(r.get("status").toString()),
									OrderStatus.REVOCATION.intValue());

					} catch (SqlException e) {
						e.printStackTrace();
					}

					if (checkMongo) {
						waitto(mongoWaitTime);
						List<Map<String, Object>> list = MongoDBUtils.query("{'id':" + orderId+ "}", MONGO_COLLECTION);
						Assert.assertEquals(1, list.size());
						Assert.assertTrue(Integer.parseInt(list.get(0).get("status").toString()) == OrderStatus.REVOCATION
								.intValue());
					}

					// 校验退款申请/退款申请记录表
					checkRevokeOrderRefundApply(getOrderInfo(orderId));

				}
				log.info("订单已撤销!");
			}else{
				Assert.assertEquals(result.getCode(), HttpStatus.SC_BAD_REQUEST, "撤单失败:" + result.getBody() + "orderId:" + orderId);
				Assert.assertTrue(result.getBody().contains("撤单失败"),result.getBody());
			}

		}
	}
	/**
	 * CRM撤销订单Order_RevokeOrder ----公共抽离方法
	 * 
	 * @param hc
	 * @param orderList
	 * @param sendMsg
	 * @param checkDb
	 * @param checkMongo
	 * @throws SqlException
	 */
	public static void Run_CrmOrderRevokeOrder(MyHttpClient hc, List<Integer> orderList, boolean sendMsg, boolean checkDb,
			boolean checkMongo) throws SqlException {
		/***** 撤销订单 ******/
		for (Integer id : orderList) {
			
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			NameValuePair nvp = new BasicNameValuePair("orderIds[]", id + "");
			params.add(nvp);
//			params.add(new BasicNameValuePair("sendMsg", sendMsg + ""));
			//检查订单是否正在结算中
			boolean cannotRevoke = SettleChecker.isOrderInSettlement(id);
			HttpResult result = hc.post(Order_RevokeOrder, params);
			judgeRevokeOrder(result,id,cannotRevoke,checkDb,checkMongo);

		}
	
	}

	/**
	 * 坐标--CRM->单位体检->删除按钮
	 * crm批量删除订单
	 * @param hc
	 * @param orderList
	 * @param sendMsg
	 * @param checkDb
     * @param checkMongo
     */
	public static void Run_CrmDeleteOrders(MyHttpClient hc, List<Integer> orderList, boolean checkDb,
										   boolean checkMongo){
		boolean canDelete = true;
		String orderIds = "";
		for(Integer o : orderList){
			orderIds += o+",";
			Order order = getOrderInfo(o);
			int status = order.getStatus();
			if (status != OrderStatus.CLOSED.intValue()
					&& status != OrderStatus.REVOCATION.intValue()
					&& status != OrderStatus.PART_BACK.intValue()
					&& status != OrderStatus.EXAM_FINISHED.intValue()) {
				canDelete = false;
			}
		}
		orderIds = orderIds.substring(0,orderIds.length()-1);
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("orderIds",orderIds));
		HttpResult result = hc.get(Order_DeleteOrder,params);
		log.info("删除订单"+orderIds+"返回"+result.getBody());
		if(canDelete){
			Assert.assertEquals(result.getCode(),HttpStatus.SC_OK);
			Assert.assertTrue(result.getBody().equals("")||result.getBody().equals("{}"),"返回的是.."+result.getBody());
			if(checkdb){
				for (Integer orderId : orderList) {
					Order retOrder = getOrderInfo(orderId);
					Assert.assertEquals(retOrder.getStatus(),OrderStatus.DELETED.intValue());
					if (checkMongo) {
						List<Map<String, Object>> list = MongoDBUtils.query("{'id':" + orderId + "}", MONGO_COLLECTION);
						Assert.assertEquals(1, list.size());
						Assert.assertTrue(Integer.parseInt(list.get(0).get("status").toString()) == OrderStatus.DELETED
								.intValue());
					}
				}
			}
		}else{
			Assert.assertEquals(result.getCode(),HttpStatus.SC_BAD_REQUEST);
			Assert.assertTrue(result.getBody().contains("订单无法删除"),"返回的是.."+result.getBody());
		}

	}

	/**
	 * C撤销订单Order_RevokeOrder ----公共抽离方法
	 *
	 * @param hc
	 * @param orderList
	 * @param sendMsg
	 * @param checkDb
	 * @param checkMongo
	 * @throws SqlException
	 */
	public static void Run_MainOrderRevokeOrder(MyHttpClient hc, List<Integer> orderList, boolean sendMsg, boolean checkDb,
											   boolean checkMongo) throws SqlException {
		/***** 撤销订单 ******/
		for (Integer id : orderList) {
			//检查订单是否正在结算中
			boolean cannotRevoke = SettleChecker.isOrderInSettlement(id);
			HttpResult result = hc.post(Flag.MAIN, MainRevokerOrder, id);
			Assert.assertEquals(result.getCode(), HttpStatus.SC_OK, "撤单失败:" + result.getBody());
			judgeRevokeOrder(result,id,cannotRevoke,checkDb,checkMongo);

		}

	}

	/**
	 *  manage撤销订单Run_ManageOrderRevokeOrder ----公共抽离方法
	 * @param hc
	 * @param orderList
	 * @param sendMsg
	 * @param checkDb
	 * @param checkMongo
	 * @throws SqlException
	 */
	public static void Run_ManageOrderRevokeOrder(MyHttpClient hc, List<Integer> orderList, boolean sendMsg, boolean checkDb,
			boolean checkMongo) throws SqlException {
		/***** 撤销订单 ******/

		//检查订单是否正在结算中
		for (Integer id : orderList) {
			boolean cannotRevoke = true;
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			NameValuePair nvp = new BasicNameValuePair("orderIds[]", id + "");
			params.add(nvp);
			boolean flag = SettleChecker.isOrderInSettlement(id);
			cannotRevoke = cannotRevoke && flag;
			params.add(new BasicNameValuePair("sendMsg", sendMsg + ""));
			HttpResult result = hc.post(Flag.OPS, OpsOrder_RevokeOrder, params);
			judgeRevokeOrder(result,id,cannotRevoke,checkDb,checkMongo);
		}
	}

    /**
     *浅对接体检中心订单导出到HIS
     * 生成xls文件
     */
    public static void exportToLightHis(MyHttpClient  httpclient,List<Order> hisOrderList,int hospitalId ,int managerId){
        String orderStr = "";
        for(Order order : hisOrderList){
            orderStr += order.getId()+",";
        }
        System.out.println("...hisOrderList"+orderStr);
        int lenth = orderStr.length();
        orderStr = orderStr.substring(0, lenth-1);
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("hospitalId", hospitalId + ""));
        NameValuePair nvp = new BasicNameValuePair("orderIds",
                "[" + orderStr + "]");
        params.add(nvp);

        HttpResult response = httpclient.post(Order_OrderInfoForExportXls, params);
        Assert.assertEquals(response.getCode(), HttpStatus.SC_OK,response.getBody());
//		System.out.println("http返回.."+response.getBody() );
        for(Order order : hisOrderList){
            Assert.assertTrue(response.getBody().contains(order.getId()+""));
        }
        params.add(new BasicNameValuePair("readOnly","false"));
        response = httpclient.post(Order_ExportOrderXls, params);
        Assert.assertEquals(response.getCode(),HttpStatus.SC_OK,"接口返回.."+response.getBody());

        if(checkdb){
            waitto(2);
            for(Order order:hisOrderList){
                Assert.assertTrue(OrderChecker.getOrderInfo(order.getId()).getIsExport());
                if(checkmongo){
                    List<Map<String,Object>> list = MongoDBUtils.query("{'id':"+order.getId()+"}", MONGO_COLLECTION);
                }
            }

        }
    }
    /***
     * 平台客户经理创建订单
     * @param platClient
     * @param platUsername
     * @param platPasswd
     * @param hospitalId
     * @param accountNum
     * @return
     * @throws SqlException
     */
	public static List<Order> plat_crm_createOrder(MyHttpClient platClient,String platUsername,String platPasswd,int hospitalId,int accountNum) throws SqlException {
		List<Order> hisOrderList = new ArrayList<Order>();
		String accountfileName = "./csv/opsRefund/company_account_tmp.xlsx";
//		String xlsfileName = "./csv/order/hisExport_tmp.xlsx";
		int hisAccountId = 0;
		Integer offSetDay = HospitalChecker.getPreviousBookDaysByHospitalId(hospitalId);
		Date start = DateUtils.offsetDay(offSetDay);
		Date end = DateUtils.offsetDestDay(start, 30);
		try {
			//创建导入用户xls
			JSONArray idCardNameList = AccountChecker.makeUploadXls(accountNum,accountfileName);
            User account = AccountChecker.getUserInfo(platUsername);
			List<Integer> companyLists = CompanyChecker.getCompanysIdByManagerId(account.getAccount_id(),true);
			int newCompanyId = 0;
			String newCompanyName = null;
			for(Integer i : companyLists){
				ChannelCompany channelCom = CompanyChecker.getChannelCompanyByCompanyId(i);
				if(channelCom.getPlatformCompanyId() > 5){
					newCompanyId = channelCom.getId();
					newCompanyName = channelCom.getName();
					break;
				}
			}
			onceLoginInSystem(platClient, Flag.CRM,platUsername , platPasswd);
			AccountChecker.uploadAccount(platClient, newCompanyId, hospitalId, "autotest_回单测试",
					accountfileName, AddAccountTypeEnum.idCard,true);

			for(int i=0;i<idCardNameList.size();i++){
				JSONObject jo = (JSONObject)idCardNameList.get(i);
				String idCard = jo.getString("idCard");
				String name = jo.getString("name");
				hisAccountId = AccountChecker.getAccountId(idCard, name, "autotest_回单测试",defPlatAccountId);
				//预约当天
				Integer hCompanyId = CompanyChecker.getHospitalCompanyByChannelCompanyId(newCompanyId, hospitalId).getId();
				Map<String,Object> dateMap = CounterChecker.getDateBookableFromStartDate(start, end, hCompanyId, hospitalId);
				int dayRangeId = Integer.parseInt(dateMap.get("dayRangeId").toString());
				String examDate = dateMap.get("examDate").toString();
				List<Meal> mealList = ResourceChecker.getOffcialMeal(hospitalId, Arrays.asList(AccountGenderEnum.MALE.getCode(),AccountGenderEnum.Common.getCode()));
				Order hisOrder = OrderChecker.crm_createOrder(platClient, mealList.get(0).getId(), hisAccountId, newCompanyId,newCompanyName,
						examDate,HospitalChecker.getHospitalById(hospitalId),dayRangeId);
				hisOrderList.add(hisOrder);
			}
			onceLogOutSystem(platClient, Flag.CRM);
		} catch (IOException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		return hisOrderList;
	}

    /**
     * 客户经理CRM批量代预约订单
     * @param httpclient
     * @param managerId
     * @param hospitalId
     * @param accountnum
     * @return
     */
    public static List<Order> crm_createOrder(MyHttpClient httpclient,int managerId, int hospitalId,int accountnum){
        List<Order> hisOrderList = new ArrayList<Order>();
        String accountfileName = "./csv/opsRefund/company_account_tmpt.xlsx";
        int hisAccountId = 0;
        try {
            //创建导入用户xls
            JSONArray idCardNameList = AccountChecker.makeUploadXls(accountnum,accountfileName);
            HospitalCompany hc = CompanyChecker.getRandomCommonHospitalCompany(hospitalId);
            AccountChecker.uploadAccount(httpclient, hc.getId(), hospitalId, "auto测试",
                    accountfileName,AddAccountTypeEnum.idCard);

            for(int i=0;i<idCardNameList.size();i++){
                JSONObject jo = (JSONObject)idCardNameList.get(i);
                String idCard = jo.getString("idCard");
                String name = jo.getString("name");
                hisAccountId = AccountChecker.getAccountId(idCard, name, "auto测试",managerId);
                //预约当天
                String examDate = sdf.format(new Date());
                List<Meal> mealList = ResourceChecker.getOffcialMeal(hospitalId,Arrays.asList(AccountGenderEnum.MALE.getCode(),AccountGenderEnum.Common.getCode()));
                Order hisOrder = OrderChecker.crm_createOrder(httpclient, mealList.get(0).getId(), hisAccountId, hc.getId(),hc.getName(),
                        examDate,HospitalChecker.getHospitalById(hospitalId));
                hisOrderList.add(hisOrder);
            }

        } catch (IOException e) {
            // TODO 自动生成的 catch 块
            e.printStackTrace();
        } catch (SqlException e) {
            e.printStackTrace();
        }
        return hisOrderList;
    }


	/**
	 * 客户经理CRM批量代预约订单
	 * @param httpclient
	 * @param managerId
	 * @param hospitalId
	 * @param accountnum
	 * @return
	 */
	public static List<Order> crm_createOrder(MyHttpClient httpclient,int managerId, int hospitalId, int companyId,int accountnum){
		List<Order> hisOrderList = new ArrayList<Order>();
		String accountfileName = "./csv/opsRefund/company_account_tmpt.xlsx";
		int hisAccountId = 0;
		try {
			//创建导入用户xls
			JSONArray idCardNameList = AccountChecker.makeUploadXls(accountnum,accountfileName);
			HospitalCompany hc = CompanyChecker.getHospitalCompanyById(companyId);
			AccountChecker.uploadAccount(httpclient, hc.getId(), hospitalId, "auto测试",
					accountfileName,AddAccountTypeEnum.idCard);

			for(int i=0;i<idCardNameList.size();i++){
				JSONObject jo = (JSONObject)idCardNameList.get(i);
				String idCard = jo.getString("idCard");
				String name = jo.getString("name");
				hisAccountId = AccountChecker.getAccountId(idCard, name, "auto测试",managerId);
				//预约当天
				String examDate = sdf.format(new Date());
				List<Meal> mealList = ResourceChecker.getOffcialMeal(hospitalId,Arrays.asList(AccountGenderEnum.MALE.getCode(),AccountGenderEnum.Common.getCode()));
				Order hisOrder = OrderChecker.crm_createOrder(httpclient, mealList.get(0).getId(), hisAccountId, hc.getId(),hc.getName(),
						examDate,HospitalChecker.getHospitalById(hospitalId));
				hisOrderList.add(hisOrder);
			}

		} catch (IOException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		} catch (SqlException e) {
			e.printStackTrace();
		}
		return hisOrderList;
	}


    /**
     * 单个下单CRM-代预约
     * @param hc
     * @param mealId
     * @param accountId
     * @param newCompanyId
     * @param newCompanyName
     * @param examdate
     * @param hospital
     * @return
     * @throws SqlException
     */
	public static Order crm_createOrder(MyHttpClient hc ,int mealId,int accountId,int newCompanyId,String newCompanyName,String examdate,Hospital hospital,int examTimeIntervalId) throws SqlException{

		Order order = new Order();
		Meal meal = ResourceChecker.getMealInfo(mealId);
		
//		Integer organizationType = HospitalChecker.getOrganizationType(hospital.getId());
		
		BatchOrderBody batchBody = new BatchOrderBody(hospital.getId(), hospital.getName(),
				meal.getId(), meal.getPrice(), meal.getGender(),meal.getName(), newCompanyId, newCompanyName,new ArrayList<Integer>(){{add(accountId);}}, examdate,examTimeIntervalId);
		
		//散客现场sitePay=true
		HospitalCompany company = CompanyChecker.getHospitalCompanyById(newCompanyId);
		if(company.getPlatformCompanyId() != null && company.getPlatformCompanyId() == 2)
			batchBody.setSitePay(true);
		String jbody = JSON.toJSONString(batchBody);

		// step4:批量下单
		HttpResult response = hc.post(Order_BatchOrder, jbody);
		if (!response.getBody().contains("result")) {
			String exceptType = orderExceptionAction(response,hc);
			if (exceptType != null)
				response = hc.post(Order_BatchOrder, jbody);
		}

		// Assert
		Assert.assertEquals(response.getCode(), HttpStatus.SC_OK);
		Integer processId = JsonPath.read(response.getBody(), "$.result");

		OrderChecker.waitBatchOrderProc(hc,processId.intValue());

		// check database
		if (checkdb) {
			waitto(mysqlWaitTime);
			BatchOrderProcess process = getBatchProcessById(processId);
			if (process.getFailNum() != process.getTotalNum()) {
				List<BatchOrderProcessRecord> records = process.getRecords();
				for (BatchOrderProcessRecord record : records) {
					if (record.getOrderNum() != null) {
						String sqlStr = "select * from tb_order where order_num = ?";
						List<Map<String, Object>> orderlist = null;
						try {
							orderlist = DBMapper.query(sqlStr, record.getOrderNum());
							Map<String, Object> ords = orderlist.get(0);
							order = getOrderInfo(Integer.parseInt(ords.get("id").toString()));

						} catch (SqlException e) {
							// TODO 自动生成的 catch 块
							e.printStackTrace();
						}
						log.info("订单状态:" + order.getId() + "...." + order.getStatus());
						Map<String,Object> setts = HospitalChecker.getHospitalSetting(hospital.getId(),HospitalParam.NEED_LOCAL_PAY);
						if(batchBody.isSitePay() && setts.get(HospitalParam.NEED_LOCAL_PAY).toString().equals("1") )
								Assert.assertTrue(order.getStatus() == OrderStatus.SITE_PAY.intValue());
						else
							Assert.assertTrue(order.getStatus() == OrderStatus.ALREADY_BOOKED.intValue()
								|| order.getStatus() == OrderStatus.ALREADY_PAY.intValue());
						if (checkmongo) {
							waitto(mongoWaitTime);
							List<Map<String, Object>> list = MongoDBUtils.query("{'id':" + order.getId() + "}",
									MONGO_COLLECTION);
							Assert.assertNotNull(list);
							Assert.assertEquals(1, list.size());
						}
						//验证结算
						check_Book_ExamOrderSettlement(order);

						for (PayLog paylog : PayChecker.getPaylog(order.getId(), PayConsts.TradeTypes.OrderPay)) {
							Assert.assertEquals(Math.abs(paylog.getAmount()), order.getOrderPrice().longValue());
							log.debug("tradebodytype>>>>>>>>" + paylog.getTradeBodyType().intValue());
							Assert.assertEquals(paylog.getTradeBodyType().intValue(), PayConsts.TradeBodyTypes.Card);
							Assert.assertEquals(paylog.getTradeIndex().intValue(), 1);
							Assert.assertEquals(paylog.getStatus(), PayConsts.TradeStatus.Successful);
							// Assert.assertEquals(paylog.getOperaterType().intValue(),PayConsts.OperaterTypes.Crm);
						}
					}
				}
			} else
				System.err.println("下单全部失败，请检查");
		}
		return order;
	}

	/**
	 * 单个下单CRM-代预约
	 * @param hc
	 * @param mealId
	 * @param accountId
	 * @param newCompanyId
	 * @param newCompanyName
	 * @param examdate
	 * @param hospital
	 * @return
	 * @throws SqlException
	 */
	public static Order crm_createOrder(MyHttpClient hc ,int mealId,int accountId,int newCompanyId,String newCompanyName,String examdate,Hospital hospital) throws SqlException{

		Order order = new Order();
		Meal meal = ResourceChecker.getMealInfo(mealId);

//		Integer organizationType = HospitalChecker.getOrganizationType(hospital.getId());

		BatchOrderBody batchBody = new BatchOrderBody(hospital.getId(), hospital.getName(),
				meal.getId(), meal.getPrice(), meal.getGender(),meal.getName(), newCompanyId, newCompanyName,new ArrayList<Integer>(){{add(accountId);}}, examdate);

		//散客现场sitePay=true
		HospitalCompany company = CompanyChecker.getHospitalCompanyById(newCompanyId);
		if(company.getPlatformCompanyId() != null && company.getPlatformCompanyId() == 2)
			batchBody.setSitePay(true);
		String jbody = JSON.toJSONString(batchBody);

		// step4:批量下单
		HttpResult response = hc.post(Order_BatchOrder, jbody);
		if (!response.getBody().contains("result")) {
			String exceptType = orderExceptionAction(response,hc);
			if (exceptType != null)
				response = hc.post(Order_BatchOrder, jbody);
		}

		// Assert
		Assert.assertEquals(response.getCode(), HttpStatus.SC_OK);
		Integer processId = JsonPath.read(response.getBody(), "$.result");
		OrderChecker.waitBatchOrderProc(hc,processId.intValue());

		// check database
		if (checkdb) {
			waitto(mysqlWaitTime);
			BatchOrderProcess process = getBatchProcessById(processId);
			if (process.getFailNum() != process.getTotalNum()) {
				List<BatchOrderProcessRecord> records = process.getRecords();
				for (BatchOrderProcessRecord record : records) {
					if (record.getOrderNum() != null) {
						String sqlStr = "select * from tb_order where order_num = ?";
						List<Map<String, Object>> orderlist = null;
						try {
							orderlist = DBMapper.query(sqlStr, record.getOrderNum());
							Map<String, Object> ords = orderlist.get(0);
							order = getOrderInfo(Integer.parseInt(ords.get("id").toString()));

						} catch (SqlException e) {
							// TODO 自动生成的 catch 块
							e.printStackTrace();
						}
						log.info("订单状态:" + order.getId() + "...." + order.getStatus());
						Map<String,Object> setts = HospitalChecker.getHospitalSetting(hospital.getId(),HospitalParam.NEED_LOCAL_PAY);
						if(batchBody.isSitePay() && setts.get(HospitalParam.NEED_LOCAL_PAY).toString().equals("1") )
							Assert.assertTrue(order.getStatus() == OrderStatus.SITE_PAY.intValue());
						else
							Assert.assertTrue(order.getStatus() == OrderStatus.ALREADY_BOOKED.intValue()
									|| order.getStatus() == OrderStatus.ALREADY_PAY.intValue());
						if (checkmongo) {
							waitto(mongoWaitTime);
							List<Map<String, Object>> list = MongoDBUtils.query("{'id':" + order.getId() + "}",
									MONGO_COLLECTION);
							Assert.assertNotNull(list);
							Assert.assertEquals(1, list.size());
						}
						//验证结算
						check_Book_ExamOrderSettlement(order);

						for (PayLog paylog : PayChecker.getPaylog(order.getId(), PayConsts.TradeTypes.OrderPay)) {
							Assert.assertEquals(Math.abs(paylog.getAmount()), order.getOrderPrice().longValue());
							log.debug("tradebodytype>>>>>>>>" + paylog.getTradeBodyType().intValue());
							Assert.assertEquals(paylog.getTradeBodyType().intValue(), PayConsts.TradeBodyTypes.Card);
							Assert.assertEquals(paylog.getTradeIndex().intValue(), 1);
							Assert.assertEquals(paylog.getStatus(), PayConsts.TradeStatus.Successful);
							// Assert.assertEquals(paylog.getOperaterType().intValue(),PayConsts.OperaterTypes.Crm);
						}
					}
				}
			} else
				System.err.println("下单全部失败，请检查");
		}
		return order;
	}

	/**
	 * 查询CRM批量下单进度
	 * @param  httpclient
	 * @param processId
	 */
	public static void waitBatchOrderProc(MyHttpClient httpclient,int processId){
		Integer failCount =0 ;

		for(int i=0;i<10;i++){
			List<NameValuePair> pairs = new ArrayList<>();
			pairs.add(new BasicNameValuePair("processId", processId+""));
			HttpResult result = httpclient.get(Order_GetBatchOrderProc,pairs);
			Assert.assertEquals(result.getCode(),HttpStatus.SC_OK,"查询批量下单进度:"+result.getBody());
			BatchOrderProcess process = OrderChecker.getBatchProcessById(processId);
			String rbody = result.getBody();
			Integer totalnum = Integer.parseInt(JsonPath.read(rbody, "$.totalNum").toString());
			Integer dealnum = Integer.parseInt(JsonPath.read(rbody, "$.dealNum").toString());
			Integer status = Integer.parseInt(JsonPath.read(rbody, "$.status").toString());
			Integer failnum = Integer.parseInt(JsonPath.read(rbody, "$.failNum").toString());
			Integer succnum = Integer.parseInt(JsonPath.read(rbody, "$.successNum").toString());


			if(process.getFailNum()!=0&&(failnum-failCount)>0){
				List<BatchOrderProcessRecord> records = process.getRecords();
				Integer accountId = records.get(records.size()-1).getAccountId();
				String failMSG = records.get(records.size()-1).getFailMsg();
				System.err.println("批量下单中存在下单失败：accountId为"+accountId+"失败原因为："+failMSG);
			}
			if(succnum == totalnum && status==2){
				Assert.assertEquals(process.getTotalNum(), totalnum);
				Assert.assertEquals(process.getDealNum(), dealnum);
				Assert.assertEquals(process.getFailNum(), failnum);
				Assert.assertEquals(process.getSuccessNum(), succnum);
				break;
			}

			waitto(3);
			failCount = failnum;
		}
	}

	public static ItemsInOrder generateItemsInOrder(Integer mealId,Integer addItemId){
		ItemsInOrder itemsInOrder = new ItemsInOrder();
		int hospitalId = ResourceChecker.getMealInfo(mealId).getHospitalId();
		//套餐内初始项目
		List<Integer> itemIdsInMeal = ResourceChecker.getMealExamItemIdList(mealId);
		itemsInOrder.setItemsInMeal(ResourceChecker.getItemInfoByIds(itemIdsInMeal));
		System.out.println("套餐id"+mealId+"套餐内初始的项目为："+JSON.toJSONString(itemsInOrder.getItemsInMeal().stream().map(i->i.getId()).collect(Collectors.toList())));
		
		//套餐外加的项目
		List<ExamItem> addedItem = new ArrayList<ExamItem>();
		if(addItemId!=0){			
			addedItem.add(ResourceChecker.checkExamItem(addItemId));
			
			List<ExamItem> addItems = new ArrayList<ExamItem>();
		    for(ExamItem s: addedItem){
		    	Integer id = s.getId();
		        if(Collections.frequency(addItems.stream().map(r->r.getId()).collect(Collectors.toList()), id) < 1) 
		        	addItems.add(s);
		    } 
			
			itemsInOrder.setAddedItem(addItems);
			System.out.println("套餐外增加的项目为："+JSON.toJSONString(itemsInOrder.getAddedItem().stream().map(i->i.getId()).collect(Collectors.toList())));
		}
		
		//所有项目包括套餐内项目、减项、加项
		List<ExamItem> allRelatedItems = new ArrayList<ExamItem>();
		allRelatedItems.addAll(addedItem);
		allRelatedItems.addAll(itemsInOrder.getItemsInMeal());
		
		List<ExamItem> allRelaItems = new ArrayList<ExamItem>();
	    for(ExamItem s: allRelatedItems){
	    	Integer id = s.getId();
	        if(Collections.frequency(allRelaItems.stream().map(r->r.getId()).collect(Collectors.toList()), id) < 1) 
	        	allRelaItems.add(s);
	    }  
		
		itemsInOrder.setAllRelatedItems(allRelaItems);
		System.out.println("所有相关项目为："+JSON.toJSONString(itemsInOrder.getAllRelatedItems().stream().map(i->i.getId()).collect(Collectors.toList())));
		
		//预约时减去最后一个项目
		List<ExamItem> reducedItems = new ArrayList<ExamItem>();
		List<ExamItem> reduItems = new ArrayList<ExamItem>();
		if(itemsInOrder.getItemsInMeal().size()>1){	
			List<MealItem> mi = ResourceChecker.getMealInnerItemList(mealId);
			List<MealItem> ableReduceItems = mi.stream().filter(i->!i.isBasic()).collect(Collectors.toList());
			if(ableReduceItems.size()>0){
				ExamItem reducedItem = ResourceChecker.getItemInfoByIds(ableReduceItems.stream().map(i->i.getId()).collect(Collectors.toList())).get(0);
				System.out.println("删去的项目为："+reducedItem.getId());
				reducedItems.add(reducedItem);
			}
			

		    for(ExamItem s: reducedItems){
		    	Integer id = s.getId();
		        if(Collections.frequency(reduItems.stream().map(r->r.getId()).collect(Collectors.toList()), id) < 1) 
		        	reduItems.add(s);
		    }  		
			itemsInOrder.setReducedItems(reduItems);
		}
	    
	    //最终套餐内项目，不包括减项
		List<ExamItem> finalItemsInMeal = new ArrayList<ExamItem>();
	    finalItemsInMeal.addAll(itemsInOrder.getItemsInMeal());
	    if(reducedItems.size()>0){   	
	    	for(ExamItem e : reducedItems){
	    		finalItemsInMeal=finalItemsInMeal.stream().filter(i->i.getId()!=e.getId()).collect(Collectors.toList());
	    	}
	    }

	    
	    List<ExamItem> finalItemInMeal = new ArrayList<ExamItem>();
	    for(ExamItem s: finalItemsInMeal){
	    	Integer id = s.getId();
	        if(Collections.frequency(finalItemInMeal.stream().map(r->r.getId()).collect(Collectors.toList()), id) < 1) 
	        	finalItemInMeal.add(s);
	    }
		itemsInOrder.setFinalItemsInMeal(finalItemInMeal);

	    //最终预约的单项
		List<ExamItem> finalItems = new ArrayList<ExamItem>();
	    finalItems = ResourceChecker.getFinalItems(itemsInOrder.getFinalItemsInMeal(),itemsInOrder.getAddedItem()); 
	    
	    //过滤重复的项目
	    List<ExamItem> result = new ArrayList<ExamItem>();
	    for(ExamItem s: finalItems){
	    	Integer id = s.getId();
	        if(Collections.frequency(result.stream().map(r->r.getId()).collect(Collectors.toList()), id) < 1) 
	        	result.add(s);
	    }

	    itemsInOrder.setFinalItems(finalItems);
		List<Integer> finalItemIds = new ArrayList<>();
		for(ExamItem e : finalItems)
			finalItemIds.add(e.getId());
		//过滤冲突项目
		try {
			List<Integer> confilictItems = ResourceChecker.getDeleteItemList(finalItemIds,hospitalId);
			if(confilictItems != null && confilictItems.size()>0){
				for(Integer a : confilictItems){
					finalItems=finalItems.stream().filter(i->i.getId()!=a.intValue()).collect(Collectors.toList());
					itemsInOrder.setFinalItems(finalItems);
					List<ExamItem> addReduceItems=finalItemInMeal.stream().filter(i->i.getId()==a.intValue()).collect(Collectors.toList());
					reduItems.addAll(addReduceItems);
					itemsInOrder.setReducedItems(reduItems);
					finalItemInMeal=finalItemInMeal.stream().filter(i->i.getId()!=a.intValue()).collect(Collectors.toList());
					itemsInOrder.setFinalItemsInMeal(finalItemInMeal);
				}
			}
		} catch (SqlException e) {
			e.printStackTrace();
		}
		System.out.println("套餐内除去的项目为："+JSON.toJSONString(itemsInOrder.getReducedItems().stream().map(i->i.getId()).collect(Collectors.toList())));

		System.out.println("最终套餐内的项目为："+JSON.toJSONString(finalItemInMeal.stream().map(i->i.getId()).collect(Collectors.toList())));

		System.out.println("最终预约的项目为："+JSON.toJSONString(itemsInOrder.getFinalItems().stream().map(i->i.getId()).collect(Collectors.toList())));

	    
		return itemsInOrder;
	}

	public static List<ExamItem> ListRemoveObj(List<ExamItem> list, int o) {
		Iterator<ExamItem> it = list.iterator();
		while (it.hasNext()) {
			if (it.next().getId() == o)
				it.remove();
			;
		}
		return list;
	}



	/**
	 * C端创建订单指定套餐(不增加单项/单项包，不改变套餐任何项目）
	 * @param httpclient
	 * @param exam_date
	 * @param examTime_interval_id
	 * @param account_id
	 * @param hospitalId
	 * @param  mealId
	 * @return
	 */
	public static int main_createOrder(MyHttpClient httpclient,String exam_date ,int examTime_interval_id ,int account_id,int hospitalId,int mealId) throws SqlException {
		System.out.println("-----------------------C端下单(不带加项包的订单)Start----------------------------");
		int accountGender = AccountChecker.getExaminerByCustomerId(account_id,hospitalId).getGender().intValue();
		ItemsInOrder itemsInOrder = new ItemsInOrder();
		//1.query form
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		String site = "mt";
		int fromSite = 0;
		try {
			fromSite = HospitalChecker.getHospitalIdBySite(site).getId();
		} catch (SqlException e) {
			e.printStackTrace();
		}
		NameValuePair _site = new BasicNameValuePair("_site", site);
		NameValuePair _siteType = new BasicNameValuePair("_siteType", "mobile" + "");
		NameValuePair _p = new BasicNameValuePair("_p", "" + "");
		params.add(_p);
		params.add(_site);
		params.add(_siteType);

		//2.json Object
		BookParams bookParams = new BookParams();
		bookParams.setExamDate(exam_date);
		bookParams.setExamTimeIntervalId(examTime_interval_id);
		bookParams.setAccountId(account_id);
		bookParams.setMealId(mealId);
		bookParams.setNeedPaperReport(false);
		bookParams.setSource(2);
		List<MealMultiChooseParam> mealMultiChooseParams = new ArrayList<>();
		List<String> groups = ResourceChecker.getMealGroupByMealId(mealId);//官方套餐的等价组列表
		List<Integer> groupExamIds = new ArrayList<>();//等价组最后选择的单项列表
		for(String k : groups){
			MealMultiChooseParam chooseParam = new MealMultiChooseParam();
			chooseParam.setMultiChooseId(k);
			List<MealExamitemGroup> examitemLists =  ResourceChecker.getMealExamitemGroupByMealId(mealId,k);//查询组内的所有单项ID,随机取一个
			int defaultSelectItem = ResourceChecker.getExamitemGroupDefaultSelectId(mealId,k);
			chooseParam.setSelectExamItemId(defaultSelectItem);//组内的单项ID
			chooseParam.setMultiChooseName(examitemLists.get(0).getGroupName());//组名称
			mealMultiChooseParams.add(chooseParam);
		}
		bookParams.setMealMultiChooseParams(mealMultiChooseParams);
//		Object addItemId;
		itemsInOrder = generateItemsInOrder(mealId, 0);

		int examinerId = 0;
		boolean is_self = false;
		if(checkdb){
			String sql = "select id,is_self from tb_examiner where organization_id = "+fromSite + " and customer_id = "+account_id +"  and is_delete = 0 order by update_time desc limit 1";
			log.info(sql);
			List<Map<String,Object>> dblist = null;
			try {
				dblist = DBMapper.query(sql);
			} catch (SqlException e) {
				e.printStackTrace();
			}
			if(dblist != null && dblist.size() >0){
				examinerId = Integer.parseInt(dblist.get(0).get("id").toString());
				is_self =  Integer.parseInt(dblist.get(0).get("is_self").toString()) == 0?false:true;
			}
		}
		if(examinerId == 0 ){
			log.error("没有体检人，请先添加体检人");
			return 0;
		}
		bookParams.setExaminerId(examinerId);
		bookParams.setInformMe(is_self);
		List<Integer> finalItemIds = ResourceChecker.getExamIdList(itemsInOrder.getFinalItems());
		bookParams.setItemIds(finalItemIds);

		HttpResult response = httpclient.post(Flag.MAIN, MainOrder_Book, params,JSON.toJSONString(bookParams));
		log.info("bookTest...." + response.getBody());
		try {
			checkMainOrderBookResponse(httpclient,response,params,JSON.toJSONString(bookParams),account_id,examinerId,hospitalId);
		} catch (SqlException e) {
			e.printStackTrace();
		}
		Assert.assertEquals(response.getCode(), HttpStatus.SC_OK);
		int commOrderId = OrderChecker.getRecentOrder(account_id).get(0);
		return commOrderId;
	}

	/**
	 * C端创建订单
	 * @param httpclient
	 * @param exam_date
	 * @param examTime_interval_id
	 * @param account_id
	 * @param hospitalId
	 * @return
	 */
	public static int main_createOrder(MyHttpClient httpclient,String exam_date ,int examTime_interval_id ,int account_id,int hospitalId){
		System.out.println("-----------------------C端下单(不带加项包的订单)Start----------------------------");
		int accountGender = AccountChecker.getExaminerByCustomerId(account_id,hospitalId).getGender().intValue();
		List<Meal> mealList = ResourceChecker.getOffcialMeal(hospitalId,Arrays.asList(AccountGenderEnum.Common.getCode(),accountGender));
		int meal_id = mealList.get(0).getId();
//		Meal meal = ResourceChecker.getMealInfo(meal_id);

		ItemsInOrder itemsInOrder = new ItemsInOrder();
		//1.query form
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		String site = "mt";
		int fromSite = 0;
		try {
			fromSite = HospitalChecker.getHospitalIdBySite(site).getId();
		} catch (SqlException e) {
			e.printStackTrace();
		}
		NameValuePair _site = new BasicNameValuePair("_site", site);
		NameValuePair _siteType = new BasicNameValuePair("_siteType", "mobile" + "");
		NameValuePair _p = new BasicNameValuePair("_p", "" + "");
		params.add(_p);
		params.add(_site);
		params.add(_siteType);

		//2.json Object
		BookParams bookParams = new BookParams();
		bookParams.setExamDate(exam_date);
		bookParams.setExamTimeIntervalId(examTime_interval_id);
		bookParams.setAccountId(account_id);
		bookParams.setMealId(meal_id);
		bookParams.setNeedPaperReport(false);
		bookParams.setSource(2);

//		Object addItemId;
		itemsInOrder = generateItemsInOrder(meal_id, 0);



		int examinerId = 0;
		boolean is_self = false;
		if(checkdb){
			String sql = "select id,is_self from tb_examiner where organization_id = "+fromSite + " and customer_id = "+account_id +"  and is_delete = 0 order by update_time desc limit 1";
			log.info(sql);
			List<Map<String,Object>> dblist = null;
			try {
				dblist = DBMapper.query(sql);
			} catch (SqlException e) {
				e.printStackTrace();
			}
			if(dblist != null && dblist.size() >0){
				examinerId = Integer.parseInt(dblist.get(0).get("id").toString());
				is_self =  Integer.parseInt(dblist.get(0).get("is_self").toString()) == 0?false:true;
			}
		}
		if(examinerId == 0 ){
			log.error("没有体检人，请先添加体检人");
			return 0;
		}
		bookParams.setExaminerId(examinerId);
		bookParams.setInformMe(is_self);
		List<Integer> finalItemIds = ResourceChecker.getExamIdList(itemsInOrder.getFinalItems());
		bookParams.setItemIds(finalItemIds);

		HttpResult response = httpclient.post(Flag.MAIN, MainOrder_Book, params,JSON.toJSONString(bookParams));
		log.info("bookTest...." + response.getBody());
		try {
			checkMainOrderBookResponse(httpclient,response,params,JSON.toJSONString(bookParams),account_id,examinerId,hospitalId);
		} catch (SqlException e) {
			e.printStackTrace();
		}
		Assert.assertEquals(response.getCode(), HttpStatus.SC_OK);
		int commOrderId = OrderChecker.getRecentOrder(account_id).get(0);
		return commOrderId;
	}

	/**
	 * 创建免登陆订单
	 * @param hc1
	 * @param hosptialId
	 * @param site
	 * @param username
	 * @param mobile
	 * @param examDate
	 * @param examTimeIntervalId
	 * @return
	 * @throws SqlException
	 */
	public static int main_createNoLoginOrder(MyHttpClient hc1,int hosptialId ,String site,String username,String mobile,String examDate,int examTimeIntervalId) throws SqlException {
		//传参
		IdCardGeneric g = new IdCardGeneric();
		String idcard = g.generateGender(1);;
		com.tijiantest.model.resource.meal.Meal offMeal = ResourceChecker.getOfficialMealListByMultiChooseOne(hosptialId, MealGenderEnum.FEMALE.getCode(),false).get(0);
		int offmealId = offMeal.getId().intValue(); //获取第一个官方女性套餐
		List<Integer> itemList = ResourceChecker.getMealExamItemIdList(offmealId);

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		NameValuePair _site = new BasicNameValuePair("_site",site);
		NameValuePair _siteType = new BasicNameValuePair("_siteType", "mobile");
		NameValuePair _p = new BasicNameValuePair("_p", "");
		NameValuePair umobile = new BasicNameValuePair("mobile", mobile);
		//step1:获取手机验证码
		params.add(_site);
		params.add(_siteType);
		params.add(_p);
		params.add(umobile);
		HttpResult result = hc1.post(Flag.MAIN,Account_MobileValidationCode,params);
		Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);
		Assert.assertEquals(result.getBody(), "{}");
		String verifyCode = null;

		if(checkdb){
			String sql = "select * from tb_sms_send_record  where mobile = '"+mobile+"' order by id desc limit 1";
			List<Map<String,Object>> smslist = DBMapper.query(sql);
			String sms = smslist.get(0).get("content").toString();
			verifyCode = sms.split("：")[1].split("，")[0];
			log.info("verifyCode..."+verifyCode);
		}

		//step2:免登陆预约
		//清理数据
		if(checkdb){
			DBMapper.update("delete from tb_user where username = \""+mobile+"\"");
		}
		int marriageInt = 0;
		BookParams  bookParams = new BookParams();
		bookParams.setName(username);
		bookParams.setIdCard(idcard);
		bookParams.setMobile(mobile);
		bookParams.setValidationCode(verifyCode);
		bookParams.setExamDate(examDate);
		bookParams.setMealId(offmealId);
		bookParams.setMarriageStatus(marriageInt);
		bookParams.setItemIds(itemList);
		bookParams.setExamTimeIntervalId(examTimeIntervalId);
		bookParams.setNeedPaperReport(false);
		bookParams.setInLocation(false);
		bookParams.setScene(5);
		String beforeDate = simplehms.format(DBMapper.query("select now()").get(0).get("now()"));
		waitto(1);
		result = hc1.post(Flag.MAIN,NoLoginBook,params,JSON.toJSONString(bookParams));
		log.info("result...."+result.getBody());
		Assert.assertEquals(result.getCode(),HttpStatus.SC_OK);
		int newNologinBookId = Integer.parseInt(JsonPath.read(result.getBody(), "$.orderId").toString());
		return  newNologinBookId;
	}

	/**
	 * 校验C端订单详情内容正确
	 * @param httpClient
	 * @param orderId
	 * @param site
	 * @param accountId
	 * @throws SqlException
	 * @throws ParseException
	 */
	public static void main_checkOrderDetails(MyHttpClient httpClient,int orderId,String site,int accountId) throws SqlException, ParseException {
		//STEP1 入参
		List<NameValuePair> pairs = new ArrayList<>();
		pairs.add(new BasicNameValuePair("_site", site));
		pairs.add(new BasicNameValuePair("_siteType", "mobile"));
		pairs.add(new BasicNameValuePair("_p",""));
		//STEP2 接口调用
		HttpResult result = httpClient.get(Flag.MAIN,Mobile_MobileOrderDetailsPage,pairs,orderId+"");
		Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);
		JSONObject jsonResult = JSONObject.parseObject(result.getBody(),JSONObject.class);
		log.info(result.getBody());
		int addNum = jsonResult.getIntValue("addNum");
		int couponAmount = jsonResult.getIntValue("couponAmount");
		String examAddress = jsonResult.getString("examAddress");
		String examCompany = jsonResult.getString("examCompany");
		boolean hasSettlementOpen = jsonResult.getBooleanValue("hasSettlementOpen");
		int inMealNum = jsonResult.getIntValue("inMealNum");
		boolean joinPromotion = jsonResult.getBooleanValue("joinPromotion");
		int needPayPrice = jsonResult.getIntValue("needPayPrice");
		int offlinePay = jsonResult.getIntValue("offlinePay");
		int showInvoice = jsonResult.getIntValue("showInvoice");
		int total = jsonResult.getIntValue("total");
		JSONObject healther = jsonResult.getJSONObject("healther");
		JSONObject order = jsonResult.getJSONObject("order");
		JSONObject itemSnap = jsonResult.getJSONObject("itemSnap");
		JSONArray packages = jsonResult.getJSONArray("packages");
		List<MealMutiChooseVO> mealMultiChooses = JSON.parseArray(jsonResult.getString("mealMultiChooses"), MealMutiChooseVO.class);//多选一等价组

		//STEP3 校验DB
		if(checkdb){
			Order dbOrder = OrderChecker.getOrderInfo(orderId);
			//3.1 订单
			Assert.assertEquals(order.getIntValue("status"),dbOrder.getStatus());//订单状态
			//比较订单总价/优惠金额/实际支付
			Assert.assertEquals(order.getIntValue("orderPrice"),dbOrder.getOrderPrice().intValue());//订单金额
			PayAmount payAmount = PayChecker.getPayAmountByOrderNum(dbOrder.getOrderNum(), PayConstants.OrderType.MytijianOrder);
			long successPayAmount = payAmount.getTotalSuccPayAmount();
			long dbCouponAmount = payAmount.getTotalCouponPayAmount();
			long dbOfflineAmount = payAmount.getOfflinePayAmount();
			Assert.assertEquals((int)dbCouponAmount,couponAmount);//优惠金额
			Assert.assertEquals(needPayPrice, dbOrder.getOrderPrice().intValue() -(int)dbCouponAmount ); //需要支付
			Assert.assertEquals(offlinePay,dbOfflineAmount);//现场支付金额

			Assert.assertEquals(simplehms.format(dbOrder.getExamDate()),simplehms.format(order.getLongValue("examDate")));//体检日期
			Assert.assertEquals(sdf.format(dbOrder.getInsertTime()),sdf.format(order.getLongValue("insertTime")));//下单时间
			Assert.assertEquals(order.getString("orderNum"),dbOrder.getOrderNum());//订单编号
			//3.2 体检人
			Assert.assertEquals(dbOrder.getOrderAccount().getName(),healther.getString("name"));//体检人姓名
			Assert.assertEquals(dbOrder.getOrderAccount().getIdCard(),healther.getString("idCard"));//体检人身份证号码
			Hospital hospital = HospitalChecker.getHospitalById(dbOrder.getOrderHospital().getId());
			Address dbAddress = hospital.getAddress();
			Assert.assertEquals(dbAddress.getProvince()+dbAddress.getCity()+dbAddress.getDistrict()+dbAddress.getAddress(),examAddress);
			Assert.assertEquals(dbOrder.getHospitalCompany().getName(),examCompany);
			//3.3 单项信息（等价组/增加项目/套餐内项目)
			int dbAddNum = 0;
			int dbInMealNum = 0;
			List<ExamItemSnapshot> examItemSnapshotList = dbOrder.getOrderMealSnapshot().getExamItemSnapList();//所有单项
			ExamItemPackageSnapshot examItemPackageSnapshot = dbOrder.getOrderMealSnapshot().getExamItemPackageSnapshot();//单项包
			List<ExamItemSnapshot> multiExamItemList = new ArrayList<>();//等价组列表
			List<ExamItem> packageItems = new ArrayList<>();
			//3.3.1 比较单项包
			if(examItemPackageSnapshot!=null){
				List<ExamItemPackage> dbPaks = examItemPackageSnapshot.getPackages();
				if(packages !=null){
					Assert.assertEquals(dbPaks.size(),packages.size());
					for(int k=0;k<packages.size();k++){
						Assert.assertEquals(dbPaks.get(k).getId().intValue(),((JSONObject)packages.get(0)).getIntValue("id"));
						packageItems.addAll(dbPaks.get(k).getItemList());
						dbAddNum += dbPaks.get(k).getItemList().size();
					}
				}
				else
					Assert.assertEquals(dbPaks.size(),0);

			}

			//3.3.2 比较套餐内单项+增加单项
			for(ExamItemSnapshot e : examItemSnapshotList){
				int itemId = e.getId();
                log.info("单项id"+itemId);
				ExamItem examItem = ResourceChecker.checkExamItem(itemId);
				if(packageItems.contains(examItem))//场景1：过滤单项包内单项，不再套餐内
					continue;
				else {
					if(itemSnap.get(itemId)!=null){//场景2：查询单项/增加单项
						JSONObject retItemJson = JSONObject.parseObject(itemSnap.get(itemId).toString(),JSONObject.class);
						Assert.assertEquals(retItemJson.getIntValue("typeToMeal"),e.getTypeToMeal());
						Assert.assertEquals(retItemJson.getString("name"),e.getName());
						Assert.assertEquals(retItemJson.getString("description"),examItem.getDescription());
						if(e.getTypeToMeal() == ExamItemToMealEnum.addToMeal.getCode())
							dbAddNum ++;
						else if(e.getTypeToMeal() == ExamItemToMealEnum.inMeal.getCode())
							dbInMealNum ++;
					}else{//场景3:等价组
						if(e.getMultiChooseId()!=null && e.getMultiChooseName() != null)//构造DB中获取的等价组列表
							multiExamItemList.add(e);
					}
				}

			}
			Assert.assertEquals(dbAddNum,addNum);//套餐外增加项目数量
			Assert.assertEquals(dbInMealNum,inMealNum);//套餐内项目数量
			Assert.assertEquals(dbAddNum+dbInMealNum,total);//总数量
			//判断等价组列表
			Assert.assertEquals(multiExamItemList.size(),mealMultiChooses.size());
			Collections.sort(multiExamItemList, new Comparator<ExamItemSnapshot>() { //排序
				@Override
				public int compare(ExamItemSnapshot o1, ExamItemSnapshot o2) {
					return o1.getMultiChooseId().hashCode() - o2.getMultiChooseId().hashCode();
				}
			});

			Collections.sort(mealMultiChooses, new Comparator<MealMutiChooseVO>() { //排序
				@Override
				public int compare(MealMutiChooseVO o1, MealMutiChooseVO o2) {
					return  o1.getMultiChooseId().hashCode() - o2.getMultiChooseId().hashCode();
				}
			});
			for(int i=0;i<multiExamItemList.size();i++){//等价组内组和选择单项比较
				Assert.assertEquals(mealMultiChooses.get(i).getMultiChooseId(),multiExamItemList.get(i).getMultiChooseId());//组ID
				Assert.assertEquals(mealMultiChooses.get(i).getMultiChooseName(),multiExamItemList.get(i).getMultiChooseName());//组名称
				List<MealMutiChhoseItemVO> chhoseItemVOList =   mealMultiChooses.get(i).getGroupItemList();
				for(MealMutiChhoseItemVO v : chhoseItemVOList)
					if(v.getId().equals(multiExamItemList.get(i).getId()))
						Assert.assertTrue(v.getSelected().booleanValue()); //等价组内选中单项ID
					else
						Assert.assertFalse(v.getSelected().booleanValue());//等价组内未选中的单项

			}
			//4.结算状态
			Map<String, Object> hospitalSettings = HospitalChecker.getHospitalSetting(dbOrder.getOrderHospital().getId(), HospitalParam.SETTLEMENT_OPEN,HospitalParam.SETTLEMENT_TIME);
			int dbSettleOpen = Integer.parseInt(hospitalSettings.get(HospitalParam.SETTLEMENT_OPEN).toString());
			String dbSettleTime =  hospitalSettings.get(HospitalParam.SETTLEMENT_TIME).toString();
			if(dbSettleOpen == 0)//未开启结算
				Assert.assertFalse(hasSettlementOpen);
			else
			if(simplehms.parse(dbSettleTime).compareTo(new Date()) == -1)//结算开启时间小于当前时间
				Assert.assertTrue(hasSettlementOpen);
			else
				Assert.assertFalse(hasSettlementOpen);

		}
	}
	/**
	 * 根据httpclient,下单的返回值response,下单时的传入参数，用户id,体检人Id,医院id，检查MainOrder_Book返回值
	 * @param httpclient
	 * @param response
	 * @param params
	 * @param accountId
	 * @param examinerId
	 * @param hospitalId
	 */
	public static HttpResult checkMainOrderBookResponse(MyHttpClient httpclient, HttpResult response,List<NameValuePair> params,String queryJson,int accountId,int examinerId ,int hospitalId) throws SqlException {
		if(response.getBody().contains(BizExceptionEnum.NOT_ENOUGH_ORDER_CAPACITY.getErrorCode())){
			System.out.println("下单时，人数不足，请注意！！！");
			return null;
		}else if(response.getBody().contains("EX_1_3_ORDER_02_05_035")){
			System.out.println("体检人信息未完善，无法下单！手动修改数据库...");
			if(examinerId != -1){
				DBMapper.update("update tb_examiner set mobile = '13333333333' ,birthYear = 1994, marriageStatus = 1,gender = 1,id_card = '"+defMainUsername+"' where id = "+examinerId + " and customer_id = "+accountId);
				response = httpclient.post(Flag.MAIN, MainOrder_Book, params,queryJson);
				log.info("再次下单..."+response.getBody());
			}

		} else if (response.getBody().contains("EX_1_2_ORDER_02_01_027")){
			log.info("下单次数已经超过单日上限，无法下单！ 手动修改数据库....");
			int same_day_order_maximum = Integer.parseInt(HospitalChecker.getHospitalSetting(hospitalId, HospitalParam.SAME_DAY_ORDER_MAXIMUM).get(HospitalParam.SAME_DAY_ORDER_MAXIMUM).toString());
			DBMapper.update("update tb_hospital_settings set same_day_order_maximum ="+(same_day_order_maximum+1)+" where hospital_id = "+hospitalId);
			response = httpclient.post(Flag.MAIN, MainOrder_Book, params,queryJson);
			log.info("再次下单..."+response.getBody());

		}
		Assert.assertFalse(response.getBody().contains("\"responseStatusCode\": 400"),"返回的结果为"+response.getBody());
		return  response;
	}


	/**
	 * 根据httpclient,下单的返回值response,下单时的传入参数，用户id,体检人Id,医院id，检查MainOrder_Book返回值
	 * @param httpclient
	 * @param response
	 * @param params
	 * @param accountId
	 * @param examinerId
	 * @param hospitalId
	 */
	public static HttpResult checkMainOrderBookPayResponse(MyHttpClient httpclient, HttpResult response,List<NameValuePair> params,int accountId,int examinerId ,int hospitalId) throws SqlException {
		if(response.getBody().contains(BizExceptionEnum.NOT_ENOUGH_ORDER_CAPACITY.getErrorCode())){
			System.out.println("下单时，人数不足，请注意！！！");
			return null;
		}else if(response.getBody().contains("EX_1_3_ORDER_02_05_035")){
			System.out.println("体检人信息未完善，无法下单！手动修改数据库...");
			if(examinerId != -1){
				DBMapper.update("update tb_examiner set mobile = '13333333333' ,birthYear = 1994, marriageStatus = 1,gender = 1,id_card = '"+defMainUsername+"' where id = "+examinerId + " and customer_id = "+accountId);
				response = httpclient.post(Flag.MAIN, Main_OrderBookPay, params);
				log.info("再次下单..."+response.getBody());
			}

		} else if (response.getBody().contains("EX_1_2_ORDER_02_01_027")){
			log.info("下单次数已经超过单日上限，无法下单！ 手动修改数据库....");
			int same_day_order_maximum = Integer.parseInt(HospitalChecker.getHospitalSetting(hospitalId, HospitalParam.SAME_DAY_ORDER_MAXIMUM).get(HospitalParam.SAME_DAY_ORDER_MAXIMUM).toString());
			DBMapper.update("update tb_hospital_settings set same_day_order_maximum ="+(same_day_order_maximum+1)+" where hospital_id = "+hospitalId);
			response = httpclient.post(Flag.MAIN, Main_OrderBookPay, params);
			log.info("再次下单..."+response.getBody());

		}
		Assert.assertFalse(response.getBody().contains("\"responseStatusCode\": 400"),"返回的结果为"+response.getBody());
		return  response;
	}
	
	/**********************CRM 异 步 任 务 *****************************/
	/**
	 * 适用于个人任务
	 * @param operaterId
	 * @param taskTypeLists
	 * @param counts
	 * @return
	 * @throws SqlException
	 * @throws ParseException 
	 */
	public static List<BatchOrderProcess> getOwnerTaskList(int operaterId,List<Integer>taskTypeLists,int counts) throws SqlException, ParseException{
		List<BatchOrderProcess> dbSortList = new ArrayList<BatchOrderProcess>();
		String task_types = ListUtil.IntegerlistToString(taskTypeLists);
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		if(taskTypeLists.contains(1)){
			//卡代预约
			String sql = "select distinct p.*,b.hospital_id from tb_batch_order_process  p ,tb_order_batch b where b.id = p.batch_id "
					+ " and p.task_type in ("+task_types+") and p.operator_id = "+operaterId+"  "
					+ "order by p.id desc limit "+counts;
			 log.info("sql..."+sql);
				list = DBMapper.query(sql);
				if(list.size() > 0){
					for(Map<String,Object> map : list){
						BatchOrderProcess process = new BatchOrderProcess();
						process.setDealNum(Integer.parseInt(map.get("deal_num").toString()));
						process.setTotalNum(Integer.parseInt(map.get("total_num").toString()));
						process.setFailNum(Integer.parseInt(map.get("fail_num").toString()));
						process.setSuccessNum(Integer.parseInt(map.get("success_num").toString()));
						process.setTaskType(Integer.parseInt(map.get("task_type").toString()));
						process.setStatus(Integer.parseInt(map.get("status").toString()));
						process.setOperatorId(Integer.parseInt(map.get("operator_id").toString()));
						process.setHospitalId(Integer.parseInt(map.get("hospital_id").toString()));
						if(map.get("task_content")!=null)
							process.setTaskContent(map.get("task_content").toString());
						process.setId(Integer.parseInt(map.get("id").toString()));
						process.setGmtCreated(simplehms.parse(map.get("gmt_created").toString()));
						process.setGmtModified(simplehms.parse(map.get("gmt_modified").toString()));
						if(map.get("description")!=null)
							process.setDescription(map.get("description").toString());
						if(map.get("company_ids")!=null)
							process.setCompany_ids(map.get("company_ids").toString());
						dbSortList.add(process);
					}
					
				}
		}
	
		
		if(taskTypeLists.contains(2)){
			//单位结算
			String setsql = "select p.*,b.hospital_id from tb_batch_order_process  p , tb_trade_settlement_batch b  where  b.operator_id = p.operator_id and   p.task_content is not null and p.task_type in (2) and p.operator_id = ? and b.settlement_view_type = 0 "
					+ "order by p.id desc limit "+counts;
				List<Map<String,Object>> setlist = DBMapper.query(setsql,operaterId);
				if(list.size() > 0){
					for(Map<String,Object> map : setlist){
						BatchOrderProcess process = new BatchOrderProcess();
						process.setDealNum(Integer.parseInt(map.get("deal_num").toString()));
						process.setTotalNum(Integer.parseInt(map.get("total_num").toString()));
						process.setFailNum(Integer.parseInt(map.get("fail_num").toString()));
						process.setSuccessNum(Integer.parseInt(map.get("success_num").toString()));
						process.setTaskType(Integer.parseInt(map.get("task_type").toString()));
						process.setStatus(Integer.parseInt(map.get("status").toString()));
						process.setOperatorId(Integer.parseInt(map.get("operator_id").toString()));
						process.setHospitalId(Integer.parseInt(map.get("hospital_id").toString()));
						process.setTaskContent(map.get("task_content").toString());
						process.setId(Integer.parseInt(map.get("id").toString()));
						process.setGmtCreated(simplehms.parse(map.get("gmt_created").toString()));
						process.setGmtModified(simplehms.parse(map.get("gmt_modified").toString()));
						if(map.get("description")!=null)
							process.setDescription(map.get("description").toString());
						if(map.get("company_ids")!=null)
							process.setCompany_ids(map.get("company_ids").toString());
						dbSortList.add(process);
					}
					
				}	
		}
	
				return dbSortList;
				
	}

	/**
	 * 获取医院的单位结算批次进度数据
	 * @param hospitalId
	 * @param counts 获取几条
	 * @return
	 * @throws SqlException
	 * @throws ParseException
	 */
	public static List<BatchOrderProcess> getBatchOrderSettleList(int hospitalId,int counts) throws SqlException, ParseException {
		List<BatchOrderProcess> dbSortList = new ArrayList<BatchOrderProcess>();
		List<Integer> newlist = new ArrayList<Integer>();
		newlist.add(2);//机构任务,单位结算
		String sql = "select distinct p.* from tb_batch_order_process  p, tb_trade_settlement_batch b  where  b.operator_id = p.operator_id and  p.task_content is not null and"
				+ " p.task_type in (" + ListUtil.IntegerlistToString(newlist) + ") and b.hospital_id = ? and b.settlement_view_type = 0 "
				+ "order by p.id desc limit " + counts;
		log.info("sql..." + sql);
		List<Map<String, Object>> list = DBMapper.query(sql, hospitalId);
		if (list.size() > 0) {
			for (Map<String, Object> map : list) {
				BatchOrderProcess process = new BatchOrderProcess();
				process.setDealNum(Integer.parseInt(map.get("deal_num").toString()));
				process.setTotalNum(Integer.parseInt(map.get("total_num").toString()));
				process.setFailNum(Integer.parseInt(map.get("fail_num").toString()));
				process.setSuccessNum(Integer.parseInt(map.get("success_num").toString()));
				process.setTaskType(Integer.parseInt(map.get("task_type").toString()));
				process.setStatus(Integer.parseInt(map.get("status").toString()));
				process.setOperatorId(Integer.parseInt(map.get("operator_id").toString()));
				if (map.get("hospital_id") != null)
					process.setHospitalId(Integer.parseInt(map.get("hospital_id").toString()));
				process.setTaskContent(map.get("task_content").toString());
				process.setId(Integer.parseInt(map.get("id").toString()));
				process.setGmtCreated(simplehms.parse(map.get("gmt_created").toString()));
				process.setGmtModified(simplehms.parse(map.get("gmt_modified").toString()));
				if (map.get("description") != null)
					process.setDescription(map.get("description").toString());
				if (map.get("company_ids") != null)
					process.setCompany_ids(map.get("company_ids").toString());
				dbSortList.add(process);
			}
		}
		return dbSortList;
	}


	/**
	 * 获取正在结算的单位
	 * @param hospitalId
	 * @return
	 * @throws SqlException
	 * @throws ParseException
	 */
	public static String getHosptialHasRunSettbach(int hospitalId) throws SqlException, ParseException {
		List<BatchOrderProcess> dbSortList = new ArrayList<BatchOrderProcess>();
		List<Integer> newlist = new ArrayList<Integer>();
		newlist.add(2);//机构任务,单位结算
		String sql = "select distinct p.* from tb_batch_order_process  p, tb_trade_settlement_batch b  where  b.operator_id = p.operator_id and  p.task_content is not null and"
				+ " p.task_type in (" + ListUtil.IntegerlistToString(newlist) + ") and b.hospital_id = ?  and p.status = 1 and b.settlement_view_type = 0";
		log.info("sql..." + sql);
		List<Map<String, Object>> list = DBMapper.query(sql, hospitalId);
		if (list.size() > 0){
			if(list.get(0).get("company_ids") !=null ){
				String companyids =list.get(0).get("company_ids").toString();
				return companyids;
			}
		}
		return null;
	}

	/**
	 * 根据角色查看对应的任务列表
	 * 体检中心操作员|主管-》体检中心所有的机构任务+ 本人的个人任务
	 * 体检中心客户经理 -》 本人的机构任务 + 本人的个人任务
	 * 平台客户经理 -》 个人任务
	 * @param hospitalId
	 * @param operaterId
	 * @param taskTypeLists
	 * @param counts
	 * @return
	 * @throws SqlException
	 * @throws ParseException 
	 */
	public static List<BatchOrderProcess> getRoleTaskList(int hospitalId,int operaterId,int counts) throws SqlException, ParseException{
		List<BatchOrderProcess> dbSortList = new ArrayList<BatchOrderProcess>();
		int roleId = AccountChecker.getAccountRoleId(operaterId);
		if(roleId == RoleEnum.HOSPITAL_MANAGER.getCode() || roleId == RoleEnum.HOSPITAL_MANAGER_ADJUST_PRICE.getCode()){//普通客户经理
			List<Integer> newlist = new ArrayList<Integer>();
			newlist.add(1); //单位代预约
			newlist.add(2); //机构任务
			dbSortList = getOwnerTaskList(operaterId,newlist,counts);//本人的个人任务 + 机构任务
		}else if (roleId == RoleEnum.PLATFORM_MANAGER.getCode()){//平台客户经理
			List<Integer> newlist = new ArrayList<Integer>();
			newlist.add(1); //单位代预约
			dbSortList = getOwnerTaskList(operaterId,newlist,counts);//本人的个人任务
		}else if (roleId == RoleEnum.HOSPITAL_OPERATOR.getCode() || roleId == RoleEnum.HOSPITAL_GENERAL_MANAGER.getCode()){//体检中心主管/操作员
			List<Integer> newlist = new ArrayList<Integer>();
			newlist.add(2);//机构任务,单位结算
			String sql = "select distinct p.* from tb_batch_order_process  p, tb_trade_settlement_batch b  where  b.operator_id = p.operator_id and  p.task_content is not null and"
					+ " p.task_type in ("+ListUtil.IntegerlistToString(newlist)+") and b.hospital_id = ? and b.settlement_view_type = 0 "
					+ "order by p.id desc limit "+counts;
			log.info("sql..."+sql);
				List<Map<String,Object>> list = DBMapper.query(sql,hospitalId);
				if(list.size() > 0){
					for(Map<String,Object> map : list){
						BatchOrderProcess process = new BatchOrderProcess();
						process.setDealNum(Integer.parseInt(map.get("deal_num").toString()));
						process.setTotalNum(Integer.parseInt(map.get("total_num").toString()));
						process.setFailNum(Integer.parseInt(map.get("fail_num").toString()));
						process.setSuccessNum(Integer.parseInt(map.get("success_num").toString()));
						process.setTaskType(Integer.parseInt(map.get("task_type").toString()));
						process.setStatus(Integer.parseInt(map.get("status").toString()));
						process.setOperatorId(Integer.parseInt(map.get("operator_id").toString()));
						if(map.get("hospital_id")!=null)
							process.setHospitalId(Integer.parseInt(map.get("hospital_id").toString()));
						process.setTaskContent(map.get("task_content").toString());
						process.setId(Integer.parseInt(map.get("id").toString()));
						process.setGmtCreated(simplehms.parse(map.get("gmt_created").toString()));
						process.setGmtModified(simplehms.parse(map.get("gmt_modified").toString()));
						if(map.get("description")!=null)
							process.setDescription(map.get("description").toString());
						if(map.get("company_ids")!=null)
							process.setCompany_ids(map.get("company_ids").toString());
						dbSortList.add(process);  //体检中心所有机构任务

					}
				}
				newlist.clear();
				newlist.add(1); //单位代预约
				dbSortList.addAll(getOwnerTaskList(operaterId,newlist,counts));//本人的个人任务
			
		}
		//sort
	    Collections.sort(dbSortList, new Comparator<BatchOrderProcess>() {
	    	@Override
	    	public int compare(BatchOrderProcess o1,
	    			BatchOrderProcess o2) {
	    		int s = o1.getStatus().compareTo( o2.getStatus());
	    		if(s == 0)
//	    			return (int)(o2.getGmtCreated().getTime() - o1.getGmtCreated().getTime());
					return o2.getId() - o1.getId();
	    		else if (s > 0)
		    			return  o1.getStatus() - o2.getStatus();
	    		
	    		else 
	    			return (int)(o1.getGmtCreated().getTime() - o2.getGmtCreated().getTime());
	    	}
		});
		return dbSortList;
	}
	/**
	 * 根据订单与当前操作人权限，判定是否显示手动退款按钮
	 * @param order
	 * @param managerId
	 * @return
	 */
	public static boolean isRefundButtonShow(Order order,int managerId){
		//读取当前管理员的权限
		int managerRole = AccountChecker.getAccountRoleId(managerId);
		//不是主管和操作员，无手动退款按钮
		if( managerRole != RoleEnum.HOSPITAL_OPERATOR.getCode() && managerRole != RoleEnum.HOSPITAL_GENERAL_MANAGER.getCode())
			return false;
		//查询订单深/浅对接,忽略订单没有深/浅对接需要实时查这块（以后再写）
		//订单医院设置
		String sql = "{\"id\":"+order.getId()+"}";
		List<Map<String, Object>> dbList = MongoDBUtils.query(sql, MONGO_COLLECTION);
		Assert.assertEquals(dbList.size(),1);
		Map<String,Object> moMap = dbList.get(0);
		boolean exportWithXls = ((BasicDBObject)((BasicDBObject) moMap.get("orderHospital")).get("hospitalSettings")).getBoolean("exportWithXls");
		boolean supportManualRefund = ((BasicDBObject)((BasicDBObject) moMap.get("orderHospital")).get("hospitalSettings")).getBoolean("supportManualRefund");
		//如果没有开启手动退款设置,则不显示手动退款按钮
		if(!supportManualRefund)
			return false;
		else
			if(exportWithXls){
				//杭辽,按照深对接处理
				if(order.getHospital().getId() == 1){
					if(order.getIsExport()){
						//体检完成
						if(order.getStatus() == OrderStatus.EXAM_FINISHED.intValue())
							return true;
						else if(order.getStatus() == OrderStatus.ALREADY_BOOKED.intValue()){
							//待审核
							String refundSql = "select * from tb_order_refund_apply where order_num ='"+order.getOrderNum() + "' and scene = 3  and is_deleted = 0 and status = 0";
							try {
								List<Map<String,Object>> refundList = DBMapper.query(refundSql);
								if(refundList != null && refundList.size() > 0)
									return true;
							} catch (SqlException e) {
								// TODO 自动生成的 catch 块
								e.printStackTrace();
							}
						}
					}
				}else{
					//不是杭辽的浅对接体检中心，已导出后就显示手动退款按钮
					if(order.getIsExport())
						return true;
					else 
						return false;
				}
			}else{//深对接体检中心，必须回单（已导出，体检完成）订单
				if(order.getIsExport()){
					//体检完成
					if(order.getStatus() == OrderStatus.EXAM_FINISHED.intValue())
						return true;
					else if(order.getStatus() == OrderStatus.ALREADY_BOOKED.intValue()){
						//待审核
						String refundSql = "select * from tb_order_refund_apply where order_num ='"+order.getOrderNum() + "' and scene = 3  and is_deleted = 0 and status = 0";
						try {
							List<Map<String,Object>> refundList = DBMapper.query(refundSql);
							if(refundList != null && refundList.size() > 0)
								return true;
						} catch (SqlException e) {
							// TODO 自动生成的 catch 块
							e.printStackTrace();
						}
						
					}
				}
			}
		return false;
	}

	/**
	 * 根据机构id+单位id查询 订单批次信息
	 * @param hosptialId
	 * @param companyId
	 * @return
	 */
	public static List<OrderBatch> getCompanyOrderBatch(int hosptialId,int companyId,int managerId)  {
		List<OrderBatch> orderBatchList = new ArrayList<OrderBatch>();
		String sql = "select * from tb_order_batch where company_id = "+companyId + " and hospital_id = "+hosptialId+" and manager_id = "+managerId +" order by id desc";
		try {
			List<Map<String,Object>> dblist = DBMapper.query(sql);
			if(dblist != null && dblist.size() > 0){
				for(int i=0;i<dblist.size();i++){
					OrderBatch orderBatch = new OrderBatch();
					orderBatch.setId(Integer.parseInt(dblist.get(i).get("id").toString()));
					boolean sitePay = Integer.parseInt(dblist.get(i).get("is_site_pay").toString()) == 1?true:false;
					boolean hidePrice = Integer.parseInt(dblist.get(i).get("is_hide_price").toString()) == 1?true:false;
					boolean reduceItem = Integer.parseInt(dblist.get(i).get("is_reduce_item").toString()) == 1?true:false;
					boolean change_date = Integer.parseInt(dblist.get(i).get("is_change_date").toString()) == 1?true:false;
					boolean proxy_card = Integer.parseInt(dblist.get(i).get("is_proxy_card").toString()) == 1?true:false;
					orderBatch.setIsSitePay(sitePay);
					orderBatch.setIsHidePrice(hidePrice);
					orderBatch.setIsReduceItem(reduceItem);
					orderBatch.setIsChangeDate(change_date);
					orderBatch.setIsProxyCard(proxy_card);
					orderBatch.setMealName(dblist.get(i).get("meal_name").toString());
					orderBatch.setMealPrice(Integer.parseInt(dblist.get(i).get("meal_price").toString()));
					orderBatch.setMealGender(Integer.parseInt(dblist.get(i).get("meal_gender").toString()));
					orderBatch.setMealId(Integer.parseInt(dblist.get(i).get("meal_id").toString()));
					orderBatch.setHospitalId(Integer.parseInt(dblist.get(i).get("hospital_id").toString()));
					orderBatch.setHospitalName(dblist.get(i).get("hospital_name").toString());
					if(dblist.get(i).get("amount")!=null)
						orderBatch.setAmount(Integer.parseInt(dblist.get(i).get("amount").toString()));
					orderBatch.setExamDate(sdf.parse(dblist.get(i).get("exam_date").toString()));
					orderBatch.setBookTime(simplehms.parse(dblist.get(i).get("book_time").toString()));
					orderBatch.setManagerId(Integer.parseInt(dblist.get(i).get("manager_id").toString()));
					orderBatch.setCompanyId(Integer.parseInt(dblist.get(i).get("company_id").toString()));
					orderBatch.setExamTimeIntervalId(Integer.parseInt(dblist.get(i).get("exam_time_interval_id").toString()));
					orderBatch.setExamTimeIntervalName(dblist.get(i).get("exam_time_interval_name").toString());
					if(dblist.get(i).get("book_export_price")!=null)
						orderBatch.setBookExportPrice(Integer.parseInt(dblist.get(i).get("book_export_price").toString()));
					orderBatchList.add(orderBatch);

				}
			}
		} catch (SqlException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return orderBatchList;
	}




	public static OrderBatch getOrderBatch(int  id)  {
		OrderBatch orderBatch = new OrderBatch();
		String sql = "select * from tb_order_batch where id = "+id;
		try {
			List<Map<String, Object>> dblist = DBMapper.query(sql);
			if (dblist != null && dblist.size() > 0) {
				Map<String, Object> map = dblist.get(0);
				orderBatch.setId(Integer.parseInt(map.get("id").toString()));
				boolean sitePay = Integer.parseInt(map.get("is_site_pay").toString()) == 1 ? true : false;
				boolean hidePrice = Integer.parseInt(map.get("is_hide_price").toString()) == 1 ? true : false;
				boolean reduceItem = Integer.parseInt(map.get("is_reduce_item").toString()) == 1 ? true : false;
				boolean change_date = Integer.parseInt(map.get("is_change_date").toString()) == 1 ? true : false;
				boolean proxy_card = Integer.parseInt(map.get("is_proxy_card").toString()) == 1 ? true : false;
				orderBatch.setIsSitePay(sitePay);
				orderBatch.setIsHidePrice(hidePrice);
				orderBatch.setIsReduceItem(reduceItem);
				orderBatch.setIsChangeDate(change_date);
				orderBatch.setIsProxyCard(proxy_card);
				orderBatch.setMealName(map.get("meal_name").toString());
				orderBatch.setMealPrice(Integer.parseInt(map.get("meal_price").toString()));
				orderBatch.setMealGender(Integer.parseInt(map.get("meal_gender").toString()));
				orderBatch.setMealId(Integer.parseInt(map.get("meal_id").toString()));
				orderBatch.setHospitalId(Integer.parseInt(map.get("hospital_id").toString()));
				orderBatch.setHospitalName(map.get("hospital_name").toString());
				if (map.get("amount") != null)
					orderBatch.setAmount(Integer.parseInt(map.get("amount").toString()));
				orderBatch.setExamDate(sdf.parse(map.get("exam_date").toString()));
				orderBatch.setBookTime(simplehms.parse(map.get("book_time").toString()));
				orderBatch.setManagerId(Integer.parseInt(map.get("manager_id").toString()));
				orderBatch.setCompanyId(Integer.parseInt(map.get("company_id").toString()));
				orderBatch.setExamTimeIntervalId(Integer.parseInt(map.get("exam_time_interval_id").toString()));
				orderBatch.setExamTimeIntervalName(map.get("exam_time_interval_name").toString());
				if (map.get("book_export_price") != null)
					orderBatch.setBookExportPrice(Integer.parseInt(map.get("book_export_price").toString()));

			}
		}catch (Exception e ){
			e.printStackTrace();
		}
		return orderBatch;
	}


	/**
	 *
	 * 获取crm下单的已预约(未导出)的订单列表
	 * 非自选日期订单，非已导出订单
	 * @param hospitalId
	 * @return
	 */
	public static List<Order> getCrmBookedOrderList(int hospitalId){
		String sql = "select * from tb_order where source  = 3  and status  = 2  and is_export = 0  and exam_date is not null and hospital_id =  "+hospitalId+" order by id desc limit 10";
		List<Order> retOrderList = new ArrayList<>();
		List<Map<String,Object>> dblist = null;
		try {
			dblist =  DBMapper.query(sql);
			if(dblist != null || dblist.size() > 0){
				for(int k = 0;k<dblist.size();k++){
					int id = Integer.parseInt(dblist.get(k).get("id").toString());
					Order order = getOrderInfo(id);
					retOrderList.add(order);
				}
			}
		} catch (SqlException e) {
			e.printStackTrace();
		}
		return retOrderList;
	}
	/**
	 *
	 * 获取手机端下单的订单列表
	 * @param hospitalId
	 * @return
	 */
	public static List<Order> getMainOrderList(int hospitalId){
		String sql = "select * from tb_order where source in (2,5) and status in (0,1,2,3,9,7)  and hospital_id =  "+hospitalId+" order by id desc limit 10";
		List<Order> retOrderList = new ArrayList<>();
		List<Map<String,Object>> dblist = null;
		try {
			dblist =  DBMapper.query(sql);
			if(dblist != null || dblist.size() > 0){
				for(int k = 0;k<dblist.size();k++){
					int id = Integer.parseInt(dblist.get(k).get("id").toString());
					Order order = getOrderInfo(id);
					retOrderList.add(order);
				}
			}
		} catch (SqlException e) {
			e.printStackTrace();
		}
		return retOrderList;
	}

	/**
	 *
	 * 获取渠道站点下的订单
	 * @param channelId
	 * @return
	 */
	public static List<Order> getMainChannelOrderList(int channelId){
		String sql = "select * from tb_order where source in (2,5) and status in (0,1,2,3,9,7)  and from_site_org_type =2 and  from_site = "+channelId+" order by id desc limit 10";
		List<Order> retOrderList = new ArrayList<>();
		List<Map<String,Object>> dblist = null;
		try {
			dblist =  DBMapper.query(sql);
			if(dblist != null || dblist.size() > 0){
				for(int k = 0;k<dblist.size();k++){
					int id = Integer.parseInt(dblist.get(k).get("id").toString());
					Order order = getOrderInfo(id);
					retOrderList.add(order);
				}
			}
		} catch (SqlException e) {
			e.printStackTrace();
		}
		return retOrderList;
	}


	/**
	 * 根据订单号和BizType类型查询最新的1条记录,返回JSONObject对象
	 * @param orderNum
	 * @param bizType
	 * @return
	 */
	public static net.sf.json.JSONObject getMongoMealSnapshot(String orderNum,String bizType){
		List<Map<String,Object>> list = MongoDBUtils.queryByPage("{'domain':'order','domainId':'"+orderNum+"','bizType':'"+bizType+"'}","createdTime",-1,0,1, ConfDefine.MONGOMEAL_COLLECTION);
		if(list.size()>0){
			 return  net.sf.json.JSONObject.fromObject(list.get(0));
		}
		return  null;
	}

	/**
	 * 计算C端的订单调整金额
	 *
	 * @param mealId 套餐ID
	 * @param  groupExamIds 套餐包含等价组（组内单项选)
	 */
	public static int  calculateClientOrderAdjustPrice(int mealId, List<Integer> groupExamIds) throws SqlException {
		//1.查询套餐是否在C端活动中
		Meal meal = ResourceChecker.getMealByActivty(mealId);//获取套餐信息（活动套餐进行过滤生成）
		int adjustPrice = meal.getMealSetting().getAdjustPrice();
		double discount = meal.getDiscount();
		int hospitalId = meal.getHospitalId();
		List<MealExamitemGroup> mealExamitemGroupList = ResourceChecker.getMealExamitemGroupByMealId(mealId);//获取套餐的多选一组
		List<Integer> beforeItemIDs = new ArrayList<Integer>();
		if(groupExamIds!= null && groupExamIds.size() >0){//有等价组项目
			for(MealExamitemGroup g : mealExamitemGroupList){
				if(g.isSelected())
					beforeItemIDs.add(g.getItemId());
			}

			List<ExamItem> beforeExamList = ResourceChecker.getExamItemsBySelected(beforeItemIDs);
			List<ExamItem> afterExamList = ResourceChecker.getExamItemsBySelected(groupExamIds);
			int beforeDiscountPrice = ResourceChecker.getExamListDiscountPrice(beforeExamList,hospitalId,discount,0);
			int afterDiscountPrice = ResourceChecker.getExamListDiscountPrice(afterExamList,hospitalId,discount,0);
			adjustPrice += afterDiscountPrice - beforeDiscountPrice;
		}

		return  adjustPrice;
	}


	/**
	 * 计算CRM端的订单调整金额
	 *
	 * @param mealId 套餐ID
	 * @param  groupExamIds 套餐包含等价组（组内单项选)
	 */
	public static int calculateCrmOrderAdjustPrice(int mealId,List<Integer> groupExamIds) throws SqlException {
		Meal meal = ResourceChecker.getMealInfo(mealId);
		int adjustPrice = meal.getMealSetting().getAdjustPrice();
		double discount = meal.getDiscount();
		int hospitalId = meal.getHospitalId();
		List<MealExamitemGroup> mealExamitemGroupList = ResourceChecker.getMealExamitemGroupByMealId(mealId);//获取套餐的多选一组
		List<Integer> beforeItemIDs = new ArrayList<Integer>();
		if(groupExamIds!= null && groupExamIds.size() >0){//有等价组项目
			for(MealExamitemGroup g : mealExamitemGroupList){
				if(g.isSelected())
					beforeItemIDs.add(g.getItemId());
			}

			List<ExamItem> beforeExamList = ResourceChecker.getExamItemsBySelected(beforeItemIDs);
			List<ExamItem> afterExamList = ResourceChecker.getExamItemsBySelected(groupExamIds);
			int beforeDiscountPrice = ResourceChecker.getExamListDiscountPrice(beforeExamList,hospitalId,discount,0);
			int afterDiscountPrice = ResourceChecker.getExamListDiscountPrice(afterExamList,hospitalId,discount,0);
			adjustPrice += afterDiscountPrice - beforeDiscountPrice;
		}

		return adjustPrice;
	}
}
