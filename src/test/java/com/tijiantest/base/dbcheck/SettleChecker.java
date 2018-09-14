package com.tijiantest.base.dbcheck;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.mongodb.BasicDBObject;
import com.tijiantest.model.company.ChannelCompany;
import com.tijiantest.model.hospital.Hospital;
import com.tijiantest.model.order.OrderStatus;
import org.testng.Assert;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tijiantest.base.BaseTest;
import com.tijiantest.base.MyHttpClient;
import com.tijiantest.model.account.Account;
import com.tijiantest.model.account.AccountRelationInCrm;
import com.tijiantest.model.card.Card;
import com.tijiantest.model.company.HospitalCompany;
import com.tijiantest.model.hospital.HospitalParam;
import com.tijiantest.model.order.HospitalExamCompanySnapshot;
import com.tijiantest.model.order.Order;
import com.tijiantest.model.organization.OrganizationTypeEnum;
import com.tijiantest.model.settlement.CompanySettlementCount;
import com.tijiantest.model.settlement.ExamOrderSettlementDO;
import com.tijiantest.model.settlement.HospitalPlatformBillStatusEnum;
import com.tijiantest.model.settlement.SettlementHospitalConfirmEnum;
import com.tijiantest.model.settlement.TradeCommonLogResultDTO;
import com.tijiantest.model.settlement.TradeConsumeQuotaDetail;
import com.tijiantest.model.settlement.TradeConsumeQuotaStatistics;
import com.tijiantest.model.settlement.TradeHospitalCompanyBill;
import com.tijiantest.model.settlement.TradeHospitalPlatformBill;
import com.tijiantest.model.settlement.TradePrepaymentRecord;
import com.tijiantest.model.settlement.TradeSettlementBatch;
import com.tijiantest.model.settlement.TradeSettlementCard;
import com.tijiantest.model.settlement.TradeSettlementOrder;
import com.tijiantest.model.settlement.TradeSettlementPayRecord;
import com.tijiantest.model.settlement.TradeSettlementRefund;
import com.tijiantest.model.settlement.UnsettlementCard;
import com.tijiantest.model.settlement.UnsettlementOrder;
import com.tijiantest.model.payment.trade.TradeOrder;
import com.tijiantest.model.settlement.*;
import com.tijiantest.util.DateUtils;
import com.tijiantest.util.ListUtil;
import com.tijiantest.util.db.DBMapper;
import com.tijiantest.util.db.MongoDBUtils;
import com.tijiantest.util.db.SqlException;

/**
 * 结算相关验证
 * @author huifang
 *
 */
public class SettleChecker extends BaseTest {
  
/****************结 算 相 关************************/
	
	/**
	 * 根据医院ID,医院管理员和结算开启时间,查询有效的单位结算列表
	 * @param hospitalId
	 * @param managerId
	 * @return
	 */
	public static List<CompanySettlementCount> getNotSettlementCompanyCounts(MyHttpClient httpclient,int hospitalId,int managerId,String settleTime){
		List<CompanySettlementCount> settleList = new ArrayList<CompanySettlementCount>();
			List<HospitalCompany> companyList = CompanyChecker.getHospitalCompanyByOrganizationId(hospitalId, "gmt_created",false);		
			for(HospitalCompany h : companyList){
//				System.out.println(h.getId()+"单位名称..."+h.getName());
				int newCompanyId = h.getId();
				List<UnsettlementOrder> notSettOrders = getNotSettlementOrder(hospitalId, settleTime, newCompanyId);
				List<UnsettlementPaymentOrder> notSettPaymentOrders  = getNotSettlementPaymentOrder(hospitalId, settleTime, newCompanyId);
				List<UnsettlementCard> notSettCards = getNotSettlementCard(hospitalId, settleTime, newCompanyId,-1);
				List<Order> notSettOrderRefund = getNotSettlementRefundOrder(hospitalId, settleTime, newCompanyId);
				List<PaymentOrder> notSettPaymentRefund = getNotSettlementRefundPaymentOrder(hospitalId, settleTime, newCompanyId);
				List<TradePrepaymentRecord> notSettPrerecords = getNotSettlementPrepaymentRecords(hospitalId, settleTime, newCompanyId,null);
				//没有未结算的订单/未结算的卡/未结算的退款/未结算的预付款，则跳过
				if(notSettOrders == null && notSettCards == null && notSettOrderRefund == null && notSettPaymentRefund == null && notSettPrerecords == null && notSettPaymentOrders == null)
					continue;
				CompanySettlementCount csc = new CompanySettlementCount();
				csc.setCompanyName(h.getName());
				csc.setId(newCompanyId);
				csc.setOrganiztionType(OrganizationTypeEnum.HOSPITAL.getCode());
				if(h.getPlatformCompanyId()!=null ){
					int platforcompanyId = h.getPlatformCompanyId();
					if(platforcompanyId !=1 && platforcompanyId !=2)
						csc.setOrganiztionType(OrganizationTypeEnum.CHANNEL.getCode());
				}
				
				if(notSettOrders != null && notSettOrders.size() > 0)
					csc.setOrderNum(notSettOrders.size());
				else 
					csc.setOrderNum(0);
				if(notSettCards != null && notSettCards.size() > 0)
					csc.setCardNum(notSettCards.size());
				else 
					csc.setCardNum(0);
				int refundSize = 0;
				if(notSettOrderRefund != null && notSettOrderRefund.size() > 0)
					refundSize += notSettOrderRefund.size();
				if(notSettPaymentRefund != null && notSettPaymentRefund.size() > 0)
					refundSize += notSettPaymentRefund.size();
				//退款数量 = 体检订单退款数量 + 付款订单退款数量之和
				csc.setRefundNum(refundSize);
				if(notSettPrerecords != null && notSettPrerecords.size() >0)
					csc.setPrepayNum(notSettPrerecords.size());
				else
					csc.setPrepayNum(0);
				if(notSettPaymentOrders != null && notSettPaymentOrders.size() >0)
					csc.setPaymentOrderNum(notSettPaymentOrders.size());
				else
					csc.setPaymentOrderNum(0);
				if(csc.getCardNum()>0 || csc.getOrderNum()> 0 || csc.getPrepayNum()>0 || csc.getRefundNum()> 0 ||csc.getPaymentOrderNum()> 0)
				 settleList.add(csc);
 			}
		
		
		return settleList;
	}
	/**
	 * 查询医院未结算的体检订单列表
	 * @param hospitalId
	 * @param settleTime
	 * @param newCompanyId
	 * @return
	 */
	public static List<UnsettlementOrder> getNotSettlementOrder(int hospitalId,String settleTime,int newCompanyId){
		List<UnsettlementOrder> orderList = new ArrayList<UnsettlementOrder>();
		String statusList = "("+SettlementHospitalConfirmEnum.UNSETTLEMENT.getCode()+","+SettlementHospitalConfirmEnum.SETTLEMENT_CANCELED.getCode()+")";
		String sql = "select o.id from tb_order o ,tb_exam_order_settlement e "
				+ "where o.order_num = e.order_num and e.hospital_settlement_status in  "+statusList
				+ " and o.insert_time >  '"+settleTime+"' and  o.status in (3,9,2) and o.is_export=1  and o.hospital_id = "+hospitalId
				+ " and o.hospital_company_id = "+newCompanyId +" and o.exam_date is not null";
		log.info(sql);
		try {
			List<Map<String,Object>> list = DBMapper.query(sql);
			for(Map<String,Object> map : list){
				UnsettlementOrder uOrder = new UnsettlementOrder();
				int id = Integer.parseInt(map.get("id").toString());
				Order order = OrderChecker.getOrderInfo(id);
				@SuppressWarnings("deprecation")
				int accountId = order.getAccount().getId();
				int managerId = order.getManagerId();
				Account account = AccountChecker.getAccountById(accountId);
				Account manager = AccountChecker.getAccountById(managerId);
				HospitalExamCompanySnapshot hospitalCompany = order.getHospitalCompany();
				List<Integer> accountlist = new ArrayList<>();
				accountlist.add(accountId);
				//通过用户与客户经理关系查找部门
				List<AccountRelationInCrm> aric = AccountChecker.checkAccRelation(accountlist,hospitalCompany.getId() , managerId);
				if(checkmongo){
					String moSql1 = "{'id':"+id+"}";
					List<Map<String,Object>> mogoList1 = MongoDBUtils.query(moSql1,MONGO_COLLECTION);
					JSONObject js = JSONObject.parseObject(mogoList1.get(0).get("orderAccount").toString());
					uOrder.setAccountName(js.getString("name"));
				}
				
				//主账号
				uOrder.setAccountIdcard(account.getIdCard());
//				uOrder.setAccountName(account.getName());
				//客户经理
				uOrder.setOwnerId(managerId);
				uOrder.setManagerName(manager.getName());
				//单位|部门
				uOrder.setCompanyName(hospitalCompany.getName());
				if(aric != null && aric.size() > 0 && aric.get(0).getDepartment() != null)
					uOrder.setDepartment(aric.get(0).getDepartment());
				//体检时间|插入时间
				if(order.getExamDate() !=null )
					uOrder.setExamDate(order.getExamDate().toString());
				if(order.getInsertTime() != null)
					uOrder.setInsertTime(order.getInsertTime().getTime());
				//订单价格|状态|订单编号
				uOrder.setOrderPrice(order.getOrderPrice());
				uOrder.setStatus(order.getStatus());
				uOrder.setOrderNum(order.getOrderNum());
				uOrder.setInsertTime(order.getInsertTime().getTime());
				orderList.add(uOrder);
			}
		} catch (SqlException e) {
			e.printStackTrace();
		}
		return orderList;
	}



	/**
	 * 查询医院未结算的体检订单列表
	 * @param sql
	 * @param mongoStr
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static List<UnsettlementOrder> getNotSettlementOrder(String sql,String mongoStr){
		List<UnsettlementOrder> orderList = new ArrayList<UnsettlementOrder>();
		try {
			List<Map<String,Object>> list = DBMapper.query(sql);
			for(Map<String,Object> map : list){
				UnsettlementOrder uOrder = new UnsettlementOrder();
				int id = Integer.parseInt(map.get("id").toString());
				Order order = OrderChecker.getOrderInfo(id);
				int accountId = order.getAccount().getId();
				int managerId = order.getManagerId();
				Account account = AccountChecker.getAccountById(accountId);
				Account manager = AccountChecker.getAccountById(managerId);
				HospitalExamCompanySnapshot hospitalCompany = order.getHospitalCompany();
				List<Integer> accountlist = new ArrayList<>();
				accountlist.add(accountId);
				//通过用户与客户经理关系查找部门
				List<AccountRelationInCrm> aric = AccountChecker.checkAccRelation(accountlist,hospitalCompany.getId() , managerId);
				if(checkmongo){
					String moSql1 = "{'id':"+id;
					if(mongoStr == null || mongoStr == "")
						moSql1 += "}";
					else{
						moSql1 += ","+mongoStr+"}" ;
						log.info("mongoSql.."+moSql1);
					}
					List<Map<String,Object>> mogoList1 = MongoDBUtils.query(moSql1,MONGO_COLLECTION);
					if(mogoList1 == null || mogoList1.size() == 0)
						continue;
					JSONObject js = JSONObject.parseObject(mogoList1.get(0).get("orderAccount").toString());
					uOrder.setAccountName(js.getString("name"));
					//主账号
					uOrder.setAccountIdcard(js.getString("idCard"));
					if(mogoList1.get(0).get("orderExtInfo")!=null)
						if(((BasicDBObject)mogoList1.get(0).get("orderExtInfo")).get("manager") != null)
							uOrder.setManagerName(((BasicDBObject)mogoList1.get(0).get("orderExtInfo")).get("manager").toString());
				}

//				uOrder.setAccountName(account.getName());
				//客户经理
				uOrder.setOwnerId(managerId);
//				uOrder.setManagerName(manager.getName());
				//单位|部门
				uOrder.setCompanyName(hospitalCompany.getName());
				if(aric != null && aric.size() > 0 && aric.get(0).getDepartment() != null)
					uOrder.setDepartment(aric.get(0).getDepartment());
				//体检时间|插入时间
				if(order.getExamDate() !=null )
					uOrder.setExamDate(sdf.format(order.getExamDate())+order.getExamTimeIntervalName().toString());
				if(order.getInsertTime() != null)
					uOrder.setInsertTime(order.getInsertTime().getTime());
				//订单价格|状态|订单编号
				uOrder.setOrderPrice(order.getOrderPrice());
				uOrder.setStatus(order.getStatus());
				uOrder.setOrderNum(order.getOrderNum());
				uOrder.setInsertTime(order.getInsertTime().getTime());
				orderList.add(uOrder);
			}
		} catch (SqlException e) {
			e.printStackTrace();
		}
		return orderList;
	}
	/**
	 * 查询医院未结算的订单列表
	 * @param hospitalId
	 * @param settleTime
	 * @param newCompanyId
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static List<UnsettlementOrder> getNotSettlementOrder(int hospitalId,String settleTime,int newCompanyId,int limit){
		List<UnsettlementOrder> orderList = new ArrayList<UnsettlementOrder>();
		String statusList = "("+SettlementHospitalConfirmEnum.UNSETTLEMENT.getCode()+","+SettlementHospitalConfirmEnum.SETTLEMENT_CANCELED.getCode()+")";
		String sql = "select o.* from tb_order o ,tb_exam_order_settlement e "
				+ "where o.order_num = e.order_num and e.hospital_settlement_status in  "+statusList
				+ " and o.insert_time >  '"+settleTime+"' and (o.status in (3,9) or (o.status = 2 and o.is_export=1) ) and o.hospital_id = "+hospitalId
				+ " and o.hospital_company_id = "+newCompanyId + " order by o.id desc  limit "+limit;
		try {
			List<Map<String,Object>> list = DBMapper.query(sql);
			for(Map<String,Object> map : list){
				UnsettlementOrder uOrder = new UnsettlementOrder();
				int id = Integer.parseInt(map.get("id").toString());
				Order order = OrderChecker.getOrderInfo(id);
				int accountId = order.getAccount().getId();
				int managerId = order.getManagerId();
				Account account = AccountChecker.getAccountById(accountId);
				Account manager = AccountChecker.getAccountById(managerId);
				HospitalExamCompanySnapshot hospitalCompany = order.getHospitalCompany();
				List<Integer> accountlist = new ArrayList<>();
				accountlist.add(accountId);
				//通过用户与客户经理关系查找部门
				List<AccountRelationInCrm> aric = AccountChecker.checkAccRelation(accountlist,hospitalCompany.getId() , managerId);
				if(checkmongo){
					String moSql1 = "{'id':"+id+"}";
					List<Map<String,Object>> mogoList1 = MongoDBUtils.query(moSql1,MONGO_COLLECTION);
					JSONObject js = JSONObject.parseObject(mogoList1.get(0).get("orderAccount").toString());
					uOrder.setAccountName(js.getString("name"));
					//主账号
					uOrder.setAccountIdcard(js.getString("idCard"));
				}
//				uOrder.setAccountName(account.getName());
				//客户经理
				uOrder.setOwnerId(managerId);
				uOrder.setManagerName(manager.getName());
				//单位|部门
				uOrder.setCompanyName(hospitalCompany.getName());
				if(aric != null && aric.size() > 0 && aric.get(0).getDepartment() != null)
					uOrder.setDepartment(aric.get(0).getDepartment());
				//体检时间|插入时间
				if(order.getExamDate() !=null )
					uOrder.setExamDate(sdf.format(order.getExamDate())+order.getExamTimeIntervalName().toString());
				if(order.getInsertTime() != null)
					uOrder.setInsertTime(order.getInsertTime().getTime());
				//订单价格|状态|订单编号
				uOrder.setOrderPrice(order.getOrderPrice());
				uOrder.setStatus(order.getStatus());
				uOrder.setOrderNum(order.getOrderNum());
				uOrder.setInsertTime(order.getInsertTime().getTime());
				orderList.add(uOrder);
			}
		} catch (SqlException e) {
			e.printStackTrace();
		}
		return orderList;
	}

	/**
	 * 查询医院未结算的付款订单列表
	 * @param hospitalId
	 * @param settleTime
	 * @param newCompanyId
	 * @return
	 */
	public static List<UnsettlementPaymentOrder> getNotSettlementPaymentOrder(int hospitalId, String settleTime, int newCompanyId){
		List<UnsettlementPaymentOrder> orderList = new ArrayList<UnsettlementPaymentOrder>();
		String statusList = "("+SettlementHospitalConfirmEnum.UNSETTLEMENT.getCode()+","+SettlementHospitalConfirmEnum.SETTLEMENT_CANCELED.getCode()+")";
		String sql = "select o.* from tb_payment_order o ,tb_payment_order_settlement e "
				+ "where o.order_num = e.order_num and e.hospital_settlement_status in  "+statusList
				+ " and o.gmt_created >  '"+settleTime+"' and (o.status in (2,3)) and o.organization_id = "+hospitalId
				+ " and o.hospital_company_id = "+newCompanyId ;
		try {
			List<Map<String,Object>> list = DBMapper.query(sql);
			for(Map<String,Object> map : list){
				UnsettlementPaymentOrder uOrder = new UnsettlementPaymentOrder();
				int id = Integer.parseInt(map.get("id").toString());
				uOrder.setPaymentName(map.get("payment_name").toString());
				int manangerId = Integer.parseInt(map.get("manager_id").toString());
				uOrder.setManagerId(manangerId);
				uOrder.setManagerName(AccountChecker.getAccountById(manangerId).getName());
				uOrder.setInsertTime(simplehms.parse(map.get("gmt_created").toString()).getTime());
				//订单价格|状态|订单编号
				uOrder.setAmount(Long.parseLong(map.get("amount").toString()));
				uOrder.setStatus(Integer.parseInt(map.get("status").toString()));
				uOrder.setOrderNum(map.get("order_num").toString());
				orderList.add(uOrder);
			}
		} catch (SqlException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return orderList;
	}

	/**
	 * 查询医院未结算的付款订单列表
	 * @param hospitalId
	 * @param settleTime
	 * @param newCompanyId
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static List<UnsettlementPaymentOrder> getNotSettlementPaymentOrder(String sql){
		List<UnsettlementPaymentOrder> orderList = new ArrayList<>();
		try {
			List<Map<String,Object>> list = DBMapper.query(sql);
			for(Map<String,Object> map : list){
				UnsettlementPaymentOrder uOrder = new UnsettlementPaymentOrder();
				int id = Integer.parseInt(map.get("id").toString());
				uOrder.setPaymentName(map.get("payment_name").toString());
				int manangerId = Integer.parseInt(map.get("manager_id").toString());
				uOrder.setManagerId(manangerId);
				uOrder.setManagerName(AccountChecker.getAccountById(manangerId).getName());
				uOrder.setInsertTime(simplehms.parse(map.get("gmt_created").toString()).getTime());
				//订单价格|状态|订单编号
				uOrder.setAmount(Long.parseLong(map.get("amount").toString()));
				uOrder.setStatus(Integer.parseInt(map.get("status").toString()));
				uOrder.setOrderNum(map.get("order_num").toString());
				orderList.add(uOrder);
			}
		} catch (SqlException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return orderList;
	}

	/**
	 * 查询医院未结算的卡列表
	 * @param hospitalId
	 * @param settleTime
	 * @param newCompanyId
	 * @return
	 */
	public static List<UnsettlementCard> getNotSettlementCard(String sql){
		List<UnsettlementCard> cardList = new ArrayList<UnsettlementCard>();
		try {
			List<Map<String,Object>> list = DBMapper.query(sql);
			for(Map<String,Object> map:list){
				UnsettlementCard uCard = new UnsettlementCard();
				int id = Integer.parseInt(map.get("id").toString());
				Card card = CardChecker.getCardInfo(id);
				int accountId = card.getAccountId();
				int managerId = card.getManagerId();
				Account account = AccountChecker.getAccountById(accountId);
				Account manager = AccountChecker.getAccountById(managerId);
				//卡信息
				uCard.setId(id);
				uCard.setSenCardTime(card.getCreateDate().getTime());
				uCard.setStatus(card.getStatus());
				//用户信息
				uCard.setAccountIdcard(account.getIdCard());
				uCard.setAccountName(account.getName());
				//客户经理
				uCard.setManagerId(managerId);
				uCard.setManagerName(manager.getName());
				//已使用金额 = 卡总额 - 剩余金额 - 回收金额
				long alreadyUsedAmount = 0l;
				if(card.getRecoverableBalance() !=null)
					alreadyUsedAmount = card.getCapacity() - card.getBalance() - card.getRecoverableBalance();
				else
					alreadyUsedAmount = card.getCapacity() - card.getBalance();
				//金额
				uCard.setAlreadyUsedAmount(alreadyUsedAmount);
				uCard.setBalance(card.getBalance());
				uCard.setCapacity(card.getCapacity());
				//单位|部门|结算方式
				HospitalCompany hc = CompanyChecker.getHospitalCompanyById(card.getNewCompanyId());
				uCard.setCompanyName(hc.getName());
				List<Integer> accountlist = new ArrayList<Integer>();
				accountlist.add(accountId);
				List<AccountRelationInCrm> aric = AccountChecker.checkAccRelation(accountlist,hc.getId() , managerId);
				if(aric != null && aric.size() > 0  && aric.get(0).getDepartment() != null)
					uCard.setDeptName(aric.get(0).getDepartment());
				uCard.setSettlementMode(hc.getSettlementMode());
				cardList.add(uCard);
			}
		} catch (SqlException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		return cardList;
		
	}
	
	/**
	 * 查询医院未结算的卡列表
	 * @param hospitalId
	 * @param settleTime
	 * @param newCompanyId
	 * @return
	 */
	public static List<UnsettlementCard> getNotSettlementCard(int hospitalId,String settleTime,int newCompanyId,int limit){
		List<UnsettlementCard> cardList = new ArrayList<UnsettlementCard>();
		String statusList = "("+SettlementHospitalConfirmEnum.UNSETTLEMENT.getCode()+","+SettlementHospitalConfirmEnum.SETTLEMENT_CANCELED.getCode()+")";
		String sql = "select c.* from tb_card c  where  c.hospital_settlement_status in "+statusList
				+ " and c.create_date > '"+settleTime +"' and c.is_deleted = 0 and c.new_company_id = "+newCompanyId +" order by c.id desc ";
		if(limit !=-1 )
			sql += " limit "+limit;
		log.debug("sql..."+sql);
		try {
			List<Map<String,Object>> list = DBMapper.query(sql);
			for(Map<String,Object> map:list){
				UnsettlementCard uCard = new UnsettlementCard();
				int id = Integer.parseInt(map.get("id").toString());
				Card card = CardChecker.getCardInfo(id);
				int accountId = card.getAccountId();
				int managerId = card.getManagerId();
				Account account = AccountChecker.getAccountById(accountId);
				Account manager = AccountChecker.getAccountById(managerId);
				//卡信息
				uCard.setId(id);
				uCard.setSenCardTime(card.getCreateDate().getTime());
				uCard.setStatus(card.getStatus());
				//用户信息
				uCard.setAccountIdcard(account.getIdCard());
				uCard.setAccountName(account.getName());
				//客户经理
				uCard.setManagerId(managerId);
				uCard.setManagerName(manager.getName());
				//已使用金额 = 卡总额 - 剩余金额 - 回收金额
				long alreadyUsedAmount = 0l;
				if(card.getRecoverableBalance() !=null)
					alreadyUsedAmount = card.getCapacity() - card.getBalance() - card.getRecoverableBalance();
				else
					alreadyUsedAmount = card.getCapacity() - card.getBalance();
				//金额
				uCard.setAlreadyUsedAmount(alreadyUsedAmount);
				uCard.setBalance(card.getBalance());
				uCard.setCapacity(card.getCapacity());
				//单位|部门|结算方式
				HospitalCompany hc = CompanyChecker.getHospitalCompanyById(card.getNewCompanyId());
				uCard.setCompanyName(hc.getName());
				List<Integer> accountlist = new ArrayList<Integer>();
				accountlist.add(accountId);
				List<AccountRelationInCrm> aric = AccountChecker.checkAccRelation(accountlist,hc.getId() , managerId);
				if(aric != null && aric.size() > 0  && aric.get(0).getDepartment() != null)
					uCard.setDeptName(aric.get(0).getDepartment());
				uCard.setSettlementMode(hc.getSettlementMode());
				cardList.add(uCard);
			}
		} catch (SqlException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		return cardList;
		
	}
	
	
	/**	
	单位A包括已撤销订单D1，回单部分退款D2(订单未结算/订单已结算)，回单全部退款D3（订单已结算），回单全部退款D4（订单未结算）且这些订单退款都不涉及到线下退款
	未结算退款数 =  回单部分退款D2 + 回单全部退款D3（订单已结算）
	**/


	/**
	 * 获取付款订单未结算退款数量
	 * @param orderNumList
	 * @param batchSn 批次号
	 * @return
	 */
	public static int getNotSettlementRefundPaymentOrderNumsByOrderNumList(List<String> orderNumList,int hospitalId,String settle_time,int companyid){
		int refund_sum = 0;
		String sql = "";
		//部分退款--付款订单未结算|已撤销结算
		if(orderNumList!=null && orderNumList.size()>0){
			String statusList = "("+SettlementHospitalConfirmEnum.UNSETTLEMENT.getCode()+","
					+SettlementHospitalConfirmEnum.SETTLEMENT_CANCELED.getCode()+")";
			sql = "select o.* from tb_payment_order o "
					+ "where o.order_num in ("+ ListUtil.StringlistToString(orderNumList)+")" + " and o.organization_id = "+hospitalId+"" +
					" and o.hospital_company_id = "+companyid+" and o.gmt_created > '"+settle_time+"'";
			sql += " and o.order_num in (select order_num from tb_payment_order_settlement r where  r.refund_settlement = 2 and  r.hospital_settlement_status in "+statusList+")";
			log.info("收款订单退款sql..."+sql);
			try {
				List<Map<String, Object>> list = DBMapper.query(sql);
				for(Map<String,Object> map : list){
					refund_sum++;
				}
			}catch (SqlException e){
				e.printStackTrace();
			}
		}


		return refund_sum;
	}

	/**
	 *
	 *
	 * 查看医院未结算的收款订单退款列表
	 * @param hospitalId
	 * @param settleTime
	 * @param newCompanyId
	 * @return
	 */
	public static List<PaymentOrder> getNotSettlementRefundPaymentOrder(int hospitalId,String settleTime,int newCompanyId){
		List<PaymentOrder> orderList = new ArrayList<PaymentOrder>();
		//部分退款--付款订单未结算|已撤销结算
		String statusList = "("+SettlementHospitalConfirmEnum.UNSETTLEMENT.getCode()+","
				+SettlementHospitalConfirmEnum.SETTLEMENT_CANCELED.getCode()+")";
		String sql = "select o.* from tb_payment_order o ,tb_payment_order_settlement r "
				+ "where o.order_num = r.order_num and r.refund_settlement = 2 and  r.hospital_settlement_status in "+statusList
				+ " and o.gmt_created >  '"+settleTime+"' and o.status = 3 and o.organization_id = "+hospitalId
				+ " and o.hospital_company_id = "+newCompanyId;
		log.debug("退款sql..."+sql);
		try {
			List<Map<String,Object>> list = DBMapper.query(sql);
			for(Map<String,Object> map : list){
				PaymentOrder uOrder = new PaymentOrder();
				String orderNum = map.get("order_num").toString();
				uOrder.setOrderNum(orderNum);
				uOrder.setId(Integer.parseInt(map.get("id").toString()));
				uOrder.setHospitalId(Integer.parseInt(map.get("organization_id").toString()));
				uOrder.setCompanyId(Integer.parseInt(map.get("hospital_company_id").toString()));
				uOrder.setName(map.get("payment_name").toString());
				int managerId = Integer.parseInt(map.get("manager_id").toString());
				uOrder.setManagerId(managerId);
				Account account = AccountChecker.getAccountById(managerId);
				uOrder.setManagerName(account.getName());
				uOrder.setAmount(Long.parseLong(map.get("amount").toString()));
				List<TradeOrder> refundTradeOrderList  =  PayChecker.getTradeOrderByOrderNum(orderNum,2);
				Assert.assertEquals(refundTradeOrderList.size(),1);
				long refundAmount = refundTradeOrderList.get(0).getSuccAmount();
				uOrder.setRefundAmount(refundAmount);
				orderList.add(uOrder);
			}
		} catch (SqlException e) {
			e.printStackTrace();
		}


		return orderList;
	}


	/**
	 * 获取体检订单未结算退款数量
	 * @param orderNumList
	 * @return
	 */
	public static int getNotSettlementRefundOrderNumsByOrderNumList(List<String> orderNumList,int hospitalId,String settleTime,int newCompanyId){
		int refund_sum = 0;
		List<Order> orderList = new ArrayList<Order>();
		String sql = "";
		//回单部分退款--订单未结算|已撤销结算
		if(orderNumList!=null && orderNumList.size()>0){
			String statusList = "("+SettlementHospitalConfirmEnum.UNSETTLEMENT.getCode()+","
					+SettlementHospitalConfirmEnum.SETTLEMENT_CANCELED.getCode() +")";
			sql = "select o.* ,r.amount,r.pay_detail  from tb_order o ,tb_order_refund_apply r "
					+ "where o.order_num = r.order_num and r.status = 1 and r.refund_type = 1 "
					+" and o.insert_time >  '"+settleTime+"' and o.status = "+ OrderStatus.PART_BACK.intValue()+" and o.hospital_id = "+hospitalId
					+ " and o.hospital_company_id = "+newCompanyId
					+ " and o.order_num in ("+ListUtil.StringlistToString(orderNumList)+") "
					+ " and o.order_num in (select order_num from tb_exam_order_settlement  where refund_settlement =2 and hospital_settlement_status in "+
					statusList +")";

			log.info("体检订单退款sql..."+sql);
			try {
				List<Map<String,Object>> list = DBMapper.query(sql);
				for(Map<String,Object> map : list){
					int id = Integer.parseInt(map.get("id").toString());
					int amount = Integer.parseInt(map.get("amount").toString());
					JSONObject jo = JSON.parseObject(map.get("pay_detail").toString());
					int totalAmount = Integer.parseInt(jo.get("totalAmount").toString());
					int offlinePayAmount = Integer.parseInt(jo.get("offlinePayAmount").toString());
					log.info(newCompanyId+"单位退款"+amount);
					if(offlinePayAmount > 0 &&  offlinePayAmount == totalAmount )//纯线下付款
						continue;
					if(amount ==0)
						continue;
					refund_sum++;
				}
			} catch (SqlException e) {
				e.printStackTrace();
			}

		}



		//回单部分退款--订单已结算
		sql = "select o.* ,r.amount,r.pay_detail  from tb_order o ,tb_order_refund_apply r "
				+ "where o.order_num = r.order_num and r.status = 1 and r.refund_type = 1 "
				+ " and o.insert_time >  '"+settleTime+"' and o.status = 9 and o.hospital_id = "+hospitalId
				+ " and o.hospital_company_id = "+newCompanyId
				+ " and o.order_num in (select order_num from tb_exam_order_settlement  where refund_settlement =2 and hospital_settlement_status "
				+ "= "+SettlementHospitalConfirmEnum.SETTLEMENT_CONFIRMD.getCode()+")";
		try {
			List<Map<String,Object>> list = DBMapper.query(sql);
			for(Map<String,Object> map : list){
				int id = Integer.parseInt(map.get("id").toString());
				int amount = Integer.parseInt(map.get("amount").toString());
				JSONObject jo = JSON.parseObject(map.get("pay_detail").toString());
				int totalAmount = Integer.parseInt(jo.get("totalAmount").toString());
				int offlinePayAmount = Integer.parseInt(jo.get("offlinePayAmount").toString());
				log.info(newCompanyId+"单位退款"+amount);
				if(offlinePayAmount > 0 &&  offlinePayAmount == totalAmount )//纯线下付款
					continue;
				if(amount ==0)
					continue;
				refund_sum++;
			}
		} catch (SqlException e) {
			e.printStackTrace();
		}

		//全部退款-订单已确认结算
		sql = "select o.* ,r.amount,r.pay_detail  from tb_order o,tb_order_refund_apply r,tb_exam_order_settlement e "
				+ "where o.order_num = r.order_num and o.order_num = e.order_num "
				+ " and r.status = 1 and r.refund_type = 0 "
				+ " and o.hospital_company_id = "+newCompanyId
				+ " and o.insert_time >  '"+settleTime+"' and o.status = 5 and o.hospital_id = "+hospitalId
				+ " and e.hospital_settlement_status = "+SettlementHospitalConfirmEnum.SETTLEMENT_CONFIRMD.getCode()
				+" and e.refund_settlement = 2"; //需要结算【已结算】退款
//		//退款涮选时间
//		if(placeOrderEndTime!=null)
//			sql += " and r.audit_time < '"+placeOrderEndTime+"'";
		log.debug("订单确认后退款sql..."+sql);
		try {
			List<Map<String,Object>> list2 = DBMapper.query(sql);
			for(Map<String,Object> map : list2){
				int id = Integer.parseInt(map.get("id").toString());
				int amount = Integer.parseInt(map.get("amount").toString());
				JSONObject jo = JSON.parseObject(map.get("pay_detail").toString());
				int totalAmount = Integer.parseInt(jo.get("totalAmount").toString());
				int offlinePayAmount = Integer.parseInt(jo.get("offlinePayAmount").toString());
				if(offlinePayAmount > 0 &&  offlinePayAmount == totalAmount )//纯线下付款
					continue;
				if(amount ==0)
					continue;
				refund_sum++;
			}
		} catch (SqlException e) {
			e.printStackTrace();
		}

		return refund_sum;
	}
	/**
	 *
	 * 
	 * 查看医院未结算的体检订单退款列表
	 * @param hospitalId
	 * @param settleTime
	 * @param newCompanyId
	 * @return
	 */
	public static List<Order> getNotSettlementRefundOrder(int hospitalId,String settleTime,int newCompanyId){
		List<Order> orderList = new ArrayList<Order>();
		//回单部分退款--订单未结算|已撤销结算|订单已确认
		String statusList = "("+SettlementHospitalConfirmEnum.UNSETTLEMENT.getCode()+","
		+SettlementHospitalConfirmEnum.SETTLEMENT_CANCELED.getCode()+","
		+SettlementHospitalConfirmEnum.SETTLEMENT_CONFIRMD.getCode()
		+")";
		String sql = "select o.* ,r.amount,r.pay_detail  from tb_order o ,tb_order_refund_apply r "
				+ "where o.order_num = r.order_num and r.status = 1 and r.refund_type = 1 "
				+ " and o.insert_time >  '"+settleTime+"' and o.status = 9 and o.hospital_id = "+hospitalId 
				+ " and o.hospital_company_id = "+newCompanyId
				+ " and o.order_num in (select order_num from tb_exam_order_settlement  where refund_settlement =2 and hospital_settlement_status in "+
				statusList+")";
		log.debug("退款sql..."+sql);
		try {
			List<Map<String,Object>> list = DBMapper.query(sql);
			for(Map<String,Object> map : list){
				int id = Integer.parseInt(map.get("id").toString());
				int amount = Integer.parseInt(map.get("amount").toString());
				JSONObject jo = JSON.parseObject(map.get("pay_detail").toString());
				int totalAmount = Integer.parseInt(jo.get("totalAmount").toString());
				int offlinePayAmount = Integer.parseInt(jo.get("offlinePayAmount").toString());
				log.info(newCompanyId+"单位退款"+amount);
				if(offlinePayAmount > 0 &&  offlinePayAmount == totalAmount )//纯线下付款
					continue;
				if(amount ==0)
					continue;
				Order order = OrderChecker.getOrderInfo(id);
				orderList.add(order);
			}
		} catch (SqlException e) {
			e.printStackTrace();
		}
		//全部退款-订单已确认结算
		sql = "select o.* ,r.amount,r.pay_detail  from tb_order o,tb_order_refund_apply r,tb_exam_order_settlement e "
				+ "where o.order_num = r.order_num and o.order_num = e.order_num "
				+ " and r.status = 1 and r.refund_type = 0 "
				+ " and o.hospital_company_id = "+newCompanyId
				+ " and o.insert_time >  '"+settleTime+"' and o.status = 5 and o.hospital_id = "+hospitalId 
						+ " and e.hospital_settlement_status = "+SettlementHospitalConfirmEnum.SETTLEMENT_CONFIRMD.getCode()
						+" and e.refund_settlement = 2"; //需要结算【已结算】退款
		log.debug("订单确认后退款sql..."+sql);
		try {
			List<Map<String,Object>> list2 = DBMapper.query(sql);
			for(Map<String,Object> map : list2){
				int id = Integer.parseInt(map.get("id").toString());
				int amount = Integer.parseInt(map.get("amount").toString());
				JSONObject jo = JSON.parseObject(map.get("pay_detail").toString());
				int totalAmount = Integer.parseInt(jo.get("totalAmount").toString());
				int offlinePayAmount = Integer.parseInt(jo.get("offlinePayAmount").toString());
				if(offlinePayAmount > 0 &&  offlinePayAmount == totalAmount )//纯线下付款
					continue;
				if(amount ==0)
					continue;
				Order order = OrderChecker.getOrderInfo(id);
				orderList.add(order);
			}
		} catch (SqlException e) {
			e.printStackTrace();
		}
		
		return orderList;
	}
	
	
	/**
	 * 查看医院未结算的预付款列表
	 * @param hospitalId
	 * @param settleTime
	 * @param newCompanyId
	 * @return
	 */
	public static List<TradePrepaymentRecord> getNotSettlementPrepaymentRecords(int hospitalId,String settleTime,int newCompanyId,String placeOrderEndTimec){
		List<TradePrepaymentRecord> prepayList = new ArrayList<TradePrepaymentRecord>();
		String statusList = "("+SettlementHospitalConfirmEnum.UNSETTLEMENT.getCode()+","+SettlementHospitalConfirmEnum.SETTLEMENT_CANCELED.getCode()+")";
		String sql = "select * from tb_trade_prepayments_record where is_deleted = 0 "
				+ " and company_id = "+newCompanyId
				+ " and settlement_view_type = 0 "//医院视角
				+ " and status in "+statusList
				+ " and organization_id = "+hospitalId + " and gmt_created > '"+settleTime+"'";
		if(placeOrderEndTimec !=null )
			sql += " and payment_time <='"+placeOrderEndTimec+"'";
		try {
			List<Map<String,Object>> list2 = DBMapper.query(sql);
			for(Map<String,Object> map : list2){
				int id = Integer.parseInt(map.get("id").toString());
				prepayList.addAll(getTradePrementRecordByColumn("id",id+""));
			}
		} catch (SqlException e) {
			e.printStackTrace();
		}
		return prepayList;
	}
	
	/**
	 * 根据Key,value查询预付款表
	 * @param args
	 * @return
	 */
	public static List<TradePrepaymentRecord>  getTradePrementRecordByColumn(String ...args){
		List<TradePrepaymentRecord> records = new ArrayList<TradePrepaymentRecord>();
		String ps = changeManyStringToOneStr(args);
		String sql = "select * from tb_trade_prepayments_record where "+ps;
		try {
			List<Map<String,Object>> list2 = DBMapper.query(sql);
			for(Map<String,Object> map : list2){
				TradePrepaymentRecord record = new TradePrepaymentRecord(); 
				int id = Integer.parseInt(map.get("id").toString());
				record.setId(id);
				record.setOrganizationId(Integer.parseInt(map.get("organization_id").toString()));
				record.setCompanyId(Integer.parseInt(map.get("company_id").toString()));
				if(map.get("batch_sn")!=null)
					record.setBatchSn(map.get("batch_sn").toString());
				record.setAmount(Integer.parseInt(map.get("amount").toString()));
				record.setPaymentTime(simplehms.parse(map.get("payment_time").toString()));
				record.setType(Integer.parseInt(map.get("type").toString()));
				record.setStatus(Integer.parseInt(map.get("status").toString()));
				record.setOperatorId(Integer.parseInt(map.get("operator_id").toString()));
				record.setSettlementViewType(Integer.parseInt(map.get("settlement_view_type").toString()));
				record.setRefundOrganizationId(Integer.parseInt(map.get("refund_organization_id").toString()));
				record.setRefundCompanyId(Integer.parseInt(map.get("refund_company_id").toString()));
				if(map.get("sn")!=null)
					record.setSn(map.get("sn").toString());
				if(map.get("certificate")!=null)
					record.setCertificate(map.get("certificate").toString());
				if(map.get("remark")!=null)
					record.setRemark(map.get("remark").toString());
				record.setIsDeleted(Integer.parseInt(map.get("is_deleted").toString()));
				record.setGmtCreated(simplehms.parse(map.get("gmt_created").toString()));
				record.setGmtModified(simplehms.parse(map.get("gmt_modified").toString()));
				records.add(record);
			}
		} catch (SqlException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		return records;
	} 
	
	/**
	 * 判断订单是否结算中
	 * @param order_num
	 * @return
	 */
	public static boolean isOrderInSettlement(int id ){
		boolean haveSettleSign = false;
		Order order = OrderChecker.getOrderInfo(id);
		List<ExamOrderSettlementDO> setDtoList = getExamOrderSettleByColumn("order_num","'"+order.getOrderNum()+"'");

		if(setDtoList != null && setDtoList.size() > 0)
			for(ExamOrderSettlementDO set : setDtoList){
				int status = set.getHospitalSettlementStatus();
				if(status == SettlementHospitalConfirmEnum.SETTLEMENT_CANCELED.getCode() || status == SettlementHospitalConfirmEnum.UNSETTLEMENT.getCode())
					haveSettleSign = false;
					//新订单已结算
				else 
					haveSettleSign = true;
			}
		List<Map<String,Object>> list = MongoDBUtils.query("{'id':"+id+"}", MONGO_COLLECTION);
		Assert.assertNotNull(list);
		Assert.assertEquals(1, list.size());
		//老的订单，标记为结算
		if(list.get(0).get("settleSign") !=null){
			if(Integer.parseInt(list.get(0).get("settleSign").toString()) == 5){
				if(!haveSettleSign)
					haveSettleSign = true;
			}
		}
		//老的订单不在结算关系表
		return haveSettleSign;
	}

	/**
	 * 根据sql查询付款订单结算关系表
	 * @param key
	 * @param value
	 * @return
	 */
	public static List<PaymentOrderSettlementDO> getPaymentOrderSettleBySql(String sql){
		List<PaymentOrderSettlementDO> records = new ArrayList<PaymentOrderSettlementDO>();
		log.info(sql);
		try {
			List<Map<String,Object>> list2 = DBMapper.query(sql);
			for(Map<String,Object> map : list2){
				PaymentOrderSettlementDO record = new PaymentOrderSettlementDO();
				int id = Integer.parseInt(map.get("id").toString());
				record.setId(id);
				record.setOrderNum(map.get("order_num").toString());
				record.setOrganizationId(Integer.parseInt(map.get("organization_id").toString()));
				record.setHospitalCompanyId(Integer.parseInt(map.get("hospital_company_id").toString()));
				record.setHospitalSettlementStatus(Integer.parseInt(map.get("hospital_settlement_status").toString()));
				record.setPaymentName(map.get("payment_name").toString());
				record.setRefundSettlement(Integer.parseInt(map.get("refund_settlement").toString()));
				record.setOrderType(Integer.parseInt(map.get("order_type").toString()));
				if(map.get("settlement_batch_sn")!=null)
					record.setSettlementBatchSn(map.get("settlement_batch_sn").toString());
				record.setIsDeleted(Integer.parseInt(map.get("is_deleted").toString()));
				record.setGmtCreated(simplehms.parse(map.get("gmt_created").toString()));
				record.setGmtModified(simplehms.parse(map.get("gmt_modified").toString()));
				records.add(record);
			}
		} catch (SqlException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		return records;
	}

	/**
	 * 根据key,value查询付款订单结算关系表
	 * @param key
	 * @param value
	 * @return
	 */
	public static List<PaymentOrderSettlementDO> getPaymentOrderSettleByColumn(String ...args){
		List<PaymentOrderSettlementDO> records = new ArrayList<PaymentOrderSettlementDO>();
		String ps = changeManyStringToOneStr(args);
		String sql = "select * from tb_payment_order_settlement where "+ps;
		log.debug(sql);
		try {
			List<Map<String,Object>> list2 = DBMapper.query(sql);
			for(Map<String,Object> map : list2){
				PaymentOrderSettlementDO record = new PaymentOrderSettlementDO();
				int id = Integer.parseInt(map.get("id").toString());
				record.setId(id);
				record.setOrderNum(map.get("order_num").toString());
				record.setOrganizationId(Integer.parseInt(map.get("organization_id").toString()));
				record.setHospitalCompanyId(Integer.parseInt(map.get("hospital_company_id").toString()));
				record.setHospitalSettlementStatus(Integer.parseInt(map.get("hospital_settlement_status").toString()));
				record.setPaymentName(map.get("payment_name").toString());
				record.setRefundSettlement(Integer.parseInt(map.get("refund_settlement").toString()));
				record.setOrderType(Integer.parseInt(map.get("order_type").toString()));
				if(map.get("settlement_batch_sn")!=null)
					record.setSettlementBatchSn(map.get("settlement_batch_sn").toString());
				record.setIsDeleted(Integer.parseInt(map.get("is_deleted").toString()));
				record.setGmtCreated(simplehms.parse(map.get("gmt_created").toString()));
				record.setGmtModified(simplehms.parse(map.get("gmt_modified").toString()));
				records.add(record);
			}
		} catch (SqlException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		return records;
	}
	/**
	 * 根据key,value查询体检订单结算关系表
	 * @param args
	 * @return
	 */
	public static List<ExamOrderSettlementDO> getExamOrderSettleByColumn(String ...args){
		List<ExamOrderSettlementDO> records = new ArrayList<ExamOrderSettlementDO>();
		String ps = changeManyStringToOneStr(args);
		String sql = "select * from tb_exam_order_settlement where "+ps;
		log.info(sql);
		try {
			List<Map<String,Object>> list2 = DBMapper.query(sql);
			for(Map<String,Object> map : list2){
				ExamOrderSettlementDO record = new ExamOrderSettlementDO(); 
				int id = Integer.parseInt(map.get("id").toString());
				record.setId(id);
				record.setOrderNum(map.get("order_num").toString());
				record.setOrganizationId(Integer.parseInt(map.get("organization_id").toString()));
				record.setHospitalCompanyId(Integer.parseInt(map.get("hospital_company_id").toString()));
				record.setHospitalSettlementStatus(Integer.parseInt(map.get("hospital_settlement_status").toString()));
				record.setRefundSettlement(Integer.parseInt(map.get("refund_settlement").toString()));
				if(map.get("settlement_batch_sn")!=null)
					record.setSettlementBatchSn(map.get("settlement_batch_sn").toString());
				if(map.get("channel_company_id")!=null)
					record.setChannelCompanyid(Integer.parseInt(map.get("channel_company_id").toString()));
				if(map.get("channel_settlement_status")!=null)
					record.setChannelSettlementStatus(Integer.parseInt(map.get("channel_settlement_status").toString()));
				if(map.get("channel_refund_settlement")!=null)
					record.setChannelRefundSettlement(Integer.parseInt(map.get("channel_refund_settlement").toString()));
				record.setIsDeleted(Integer.parseInt(map.get("is_deleted").toString()));
				record.setGmtCreated(simplehms.parse(map.get("gmt_created").toString()));
				record.setGmtModified(simplehms.parse(map.get("gmt_modified").toString()));
				records.add(record);
			}
		} catch (SqlException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		return records;
	}


	/**
	 * 根据sql查询订单结算表
	 * @param sql
	 * @return
	 */
	public static List<ExamOrderSettlementDO> getExamOrderSettleBySql(String sql){
		List<ExamOrderSettlementDO> records = new ArrayList<ExamOrderSettlementDO>();
		log.info(sql);
		try {
			List<Map<String,Object>> list2 = DBMapper.query(sql);
			for(Map<String,Object> map : list2){
				ExamOrderSettlementDO record = new ExamOrderSettlementDO();
				int id = Integer.parseInt(map.get("id").toString());
				record.setId(id);
				record.setOrderNum(map.get("order_num").toString());
				record.setOrganizationId(Integer.parseInt(map.get("organization_id").toString()));
				record.setHospitalCompanyId(Integer.parseInt(map.get("hospital_company_id").toString()));
				record.setHospitalSettlementStatus(Integer.parseInt(map.get("hospital_settlement_status").toString()));
				record.setRefundSettlement(Integer.parseInt(map.get("refund_settlement").toString()));
				if(map.get("settlement_batch_sn")!=null)
					record.setSettlementBatchSn(map.get("settlement_batch_sn").toString());
				if(map.get("company_id")!=null)
					record.setChannelCompanyid(Integer.parseInt(map.get("channel_company_id").toString()));
				if(map.get("channel_settlement_status")!=null)
					record.setChannelSettlementStatus(Integer.parseInt(map.get("channel_settlement_status").toString()));
				if(map.get("channel_refund_settlement")!=null)
					record.setChannelRefundSettlement(Integer.parseInt(map.get("channel_refund_settlement").toString()));
				record.setIsDeleted(Integer.parseInt(map.get("is_deleted").toString()));
				record.setGmtCreated(simplehms.parse(map.get("gmt_created").toString()));
				record.setGmtModified(simplehms.parse(map.get("gmt_modified").toString()));
				records.add(record);
			}
		} catch (SqlException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		return records;
	}

	/**
	 * 根据key,value查询结算体检订单表
	 * @param args
	 * @return
	 */
	public static List<TradeSettlementOrder> getTradeSettleOrderByColumn(String ...args){
		List<TradeSettlementOrder> records = new ArrayList<TradeSettlementOrder>();
		String ps = changeManyStringToOneStr(args);
		String sql = "select * from tb_trade_settlement_order where "+ps + " order by gmt_created desc";
		System.out.println("查询订单数据库.."+sql);
		try {
			List<Map<String,Object>> list2 = DBMapper.query(sql);
			for(Map<String,Object> map : list2){
				TradeSettlementOrder record = new TradeSettlementOrder(); 
				int id = Integer.parseInt(map.get("id").toString());
				record.setId(id);
				record.setSn(map.get("sn").toString());
				record.setRefOrderNum(map.get("ref_order_num").toString());
				record.setOrganizationId(Integer.parseInt(map.get("organization_id").toString()));
				record.setCompanyId(Integer.parseInt(map.get("company_id").toString()));
				if(map.get("batch_sn")!=null)
					record.setBatchSn(map.get("batch_sn").toString());
				if(map.get("hospital_platform_sn")!=null)
					record.setHospitalPlatformSn(map.get("hospital_platform_sn").toString());
				if(map.get("hospital_company_sn")!=null)
					record.setHospitalCompanySn(map.get("hospital_company_sn").toString());
				record.setPcardPayAmount(Long.parseLong(map.get("pcard_pay_amount").toString()));
				record.setOnlinePayAmount(Long.parseLong(map.get("online_pay_amount").toString()));
				record.setPlatformPayAmount(Long.parseLong(map.get("platform_pay_amount").toString()));
				record.setCardPayAmount(Long.parseLong(map.get("card_pay_amount").toString()));
				record.setOfflinePayAmount(Long.parseLong(map.get("offline_pay_amount").toString()));
				record.setHospitalCoupAmount(Long.parseLong(map.get("hospital_coupon_amount").toString()));
				record.setPlatformCoupAmount(Long.parseLong(map.get("platform_coupon_amount").toString()));
				record.setChannelCoupAmount(Long.parseLong(map.get("channel_coupon_amount").toString()));
				record.setHospitalSettlementStatus(Integer.parseInt(map.get("hospital_settlement_status").toString()));
				record.setIsDeleted(Integer.parseInt(map.get("is_deleted").toString()));
				record.setGmtCreated(simplehms.parse(map.get("gmt_created").toString()));
				record.setGmtModified(simplehms.parse(map.get("gmt_modified").toString()));
				records.add(record);
			}
		} catch (SqlException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		return records;
	}

	/**
	 * 根据args查询结算付款订单表
	 * @param args
	 * @return
	 */
	public static List<TradeSettlementPaymentOrder> getTradeSettlePaymentOrderByColumn(String ...args){
		List<TradeSettlementPaymentOrder> records = new ArrayList<TradeSettlementPaymentOrder>();
		String ps = changeManyStringToOneStr(args);
		String sql = "select * from tb_trade_settlement_payment_order where "+ps;
//		System.out.println("查询收款订单数据库.."+sql);
		try {
			List<Map<String,Object>> list2 = DBMapper.query(sql);
			for(Map<String,Object> map : list2){
				TradeSettlementPaymentOrder record = new TradeSettlementPaymentOrder();
				int id = Integer.parseInt(map.get("id").toString());
				record.setId(id);
				record.setSn(map.get("sn").toString());
				record.setRefOrderNum(map.get("ref_order_num").toString());
				record.setRefOrderType(Integer.parseInt(map.get("ref_order_type").toString()));
				record.setOrganizationId(Integer.parseInt(map.get("organization_id").toString()));
				record.setCompanyId(Integer.parseInt(map.get("company_id").toString()));
				if(map.get("batch_sn")!=null)
					record.setBatchSn(map.get("batch_sn").toString());
				if(map.get("hospital_platform_sn")!=null)
					record.setHospitalPlatformSn(map.get("hospital_platform_sn").toString());
				record.setOnlinePayAmount(Long.parseLong(map.get("online_pay_amount").toString()));
				record.setHospitalSettlementStatus(Integer.parseInt(map.get("hospital_settlement_status").toString()));
				record.setIsDeleted(Integer.parseInt(map.get("is_deleted").toString()));
				record.setGmtCreated(simplehms.parse(map.get("gmt_created").toString()));
				record.setGmtModified(simplehms.parse(map.get("gmt_modified").toString()));
				records.add(record);
			}
		} catch (SqlException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		return records;
	}
	/**
	 * 根据key,value查询结算卡表
	 * @param args
	 * @return
	 */
	public static List<TradeSettlementCard> getTradeSettleCardByColumn(String ...args){
		List<TradeSettlementCard> records = new ArrayList<TradeSettlementCard>();
		String ps = changeManyStringToOneStr(args);
		String sql = "select * from tb_trade_settlement_card where "+ps;
		try {
			List<Map<String,Object>> list2 = DBMapper.query(sql);
			for(Map<String,Object> map : list2){
				TradeSettlementCard record = new TradeSettlementCard(); 
				int id = Integer.parseInt(map.get("id").toString());
				record.setId(id);
				record.setSn(map.get("sn").toString());
				record.setRefCardId(Integer.parseInt(map.get("ref_card_id").toString()));
				record.setOrganizationId(Integer.parseInt(map.get("organization_id").toString()));
				record.setCompanyId(Integer.parseInt(map.get("company_id").toString()));
				if(map.get("batch_sn")!=null)
					record.setBatchSn(map.get("batch_sn").toString());
				if(map.get("hospital_platform_sn")!=null)
					record.setHospitalPlatformSn(map.get("hospital_platform_sn").toString());
				if(map.get("hospital_company_sn")!=null)
					record.setHospitalCompanySn(map.get("hospital_company_sn").toString());
				record.setSettlementAmount(Long.parseLong(map.get("settlement_amount").toString()));
				record.setRecycleAmount(Long.parseLong(map.get("recycle_amount").toString()));
				record.setSettlementMode(Integer.parseInt(map.get("settlement_mode").toString()));
				record.setHospitalSettlementStatus(Integer.parseInt(map.get("hospital_settlement_status").toString()));
				record.setIsDeleted(Integer.parseInt(map.get("is_deleted").toString()));
				record.setGmtCreated(simplehms.parse(map.get("gmt_created").toString()));
				record.setGmtModified(simplehms.parse(map.get("gmt_modified").toString()));
				records.add(record);
			}
		} catch (SqlException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		return records;
	} 
	
	
	/**
	 * 根据key,value查询结算退款表
	 * @param args
	 * @return
	 */
	public static List<TradeSettlementRefund> getTradeSettleRefundByColumn(String ...args){
		List<TradeSettlementRefund> refunds = new ArrayList<TradeSettlementRefund>();
		String ps = changeManyStringToOneStr(args);
		String sql = "select * from tb_trade_settlement_refund where "+ps+" order by gmt_created desc";
		try {
			List<Map<String,Object>> list2 = DBMapper.query(sql);
			for(Map<String,Object> map : list2){
				TradeSettlementRefund record = new TradeSettlementRefund(); 
				int id = Integer.parseInt(map.get("id").toString());
				record.setId(id);
				record.setSn(map.get("sn").toString());
				record.setRefOrderNum(map.get("ref_order_num").toString());
				record.setRefOrderType(Integer.parseInt(map.get("ref_order_type").toString()));
				record.setOrganizationId(Integer.parseInt(map.get("organization_id").toString()));
				record.setCompanyId(Integer.parseInt(map.get("company_id").toString()));
				if(map.get("batch_sn")!=null)
					record.setBatchSn(map.get("batch_sn").toString());
				if(map.get("hospital_platform_sn")!=null)
					record.setHospitalPlatformSn(map.get("hospital_platform_sn").toString());
				if(map.get("hospital_company_sn")!=null)
					record.setHospitalCompanySn(map.get("hospital_company_sn").toString());
				record.setPcardRefundAmount(Long.parseLong(map.get("pcard_refund_amount").toString()));
				record.setOnlineRefundAmount(Long.parseLong(map.get("online_refund_amount").toString()));
				record.setPlatformRefundAmount(Long.parseLong(map.get("platform_refund_amount").toString()));
				record.setCardRefundAmount(Long.parseLong(map.get("card_refund_amount").toString()));
				record.setOfflineRefundAmount(Long.parseLong(map.get("offline_refund_amount").toString()));
				record.setHospitalCoupRefundAmount(Long.parseLong(map.get("hospital_coupon_refund_amount").toString()));
				record.setPlatformCoupRefundAmount(Long.parseLong(map.get("platform_coupon_refund_amount").toString()));
				record.setChannelCoupRefundAmount(Long.parseLong(map.get("channel_coupon_refund_amount").toString()));
				record.setRefundTime(simplehms.parse(map.get("refund_time").toString()));
				record.setHospitalSettlementStatus(Integer.parseInt(map.get("hospital_settlement_status").toString()));
				record.setIsDeleted(Integer.parseInt(map.get("is_deleted").toString()));
				record.setGmtCreated(simplehms.parse(map.get("gmt_created").toString()));
				record.setGmtModified(simplehms.parse(map.get("gmt_modified").toString()));
				refunds.add(record);
			}
		} catch (SqlException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		return refunds;
	} 
	
	
	
	/**
	 * 传入sql语句,查询取得特殊退款记录
	 * @param sql
	 * @return
	 */
	public static List<TradePrepaymentRecord> getTradePrepaymentRecordBySql(String sql){
		List<TradePrepaymentRecord> records = new ArrayList<TradePrepaymentRecord>();
		try {
			List<Map<String,Object>> list2 = DBMapper.query(sql);
			for(Map<String,Object> map : list2){
				TradePrepaymentRecord record = new TradePrepaymentRecord(); 
				int id = Integer.parseInt(map.get("id").toString());
				record.setId(id);
                int organizationId = Integer.parseInt(map.get("organization_id").toString());
                String organizationName = HospitalChecker.getHospitalById(organizationId).getName();
                int refundOrganizationId = Integer.parseInt(map.get("refund_organization_id").toString());//退款机构ID
                String refundOrganizationName =  HospitalChecker.getHospitalById(refundOrganizationId).getName();//退款机构名
                int organizationType = HospitalChecker.getHospitalById(refundOrganizationId).getOrganizationType();
                int refundCompanyId = Integer.parseInt(map.get("refund_company_id").toString());//退款单位ID
                String refundCompanyName = null;
                if(organizationType == OrganizationTypeEnum.CHANNEL.getCode()){
                    refundCompanyName = CompanyChecker.getChannelCompanyByCompanyId(refundCompanyId).getName();
                }else if(organizationType == OrganizationTypeEnum.HOSPITAL.getCode())
                    refundCompanyName = CompanyChecker.getHospitalCompanyById(refundCompanyId).getName();
                record.setOrganizationId(organizationId);
                record.setOrganizationName(organizationName);
                record.setRefundOrganizationId(refundOrganizationId);
                record.setRefundOrganizationName(refundOrganizationName);
                record.setRefundCompanyId(refundCompanyId);
                record.setRefundCompanyName(refundCompanyName);
				record.setPaymentTime(simplehms.parse(map.get("payment_time").toString()));
				if(map.get("certificate")!=null)
					record.setCertificate(map.get("certificate").toString());
				if(map.get("remark")!=null)
					record.setRemark(map.get("remark").toString());
				if(map.get("type")!=null){
					int type = Integer.parseInt(map.get("type").toString());
					record.setType(type);
				  }
				if(map.get("company_id")!=null){
					int hospitalCompanyId = Integer.parseInt(map.get("company_id").toString());
					HospitalCompany hc = CompanyChecker.getHospitalCompanyById(hospitalCompanyId);
					record.setCompanyId(hospitalCompanyId);
					record.setCompanyName(hc.getName());
					}
				record.setAmount(Integer.parseInt(map.get("amount").toString()));
				int status = Integer.parseInt(map.get("status").toString());
				record.setStatus(status);
                record.setIsDeleted(Integer.parseInt(map.get("is_deleted").toString()));
                if(Integer.parseInt(map.get("is_deleted").toString()) == 1)
                    record.setSettlementStatus(2);//已删除
                else{
                    if(status == SettlementHospitalConfirmEnum.UNSETTLEMENT.getCode() || status == SettlementHospitalConfirmEnum.SETTLEMENT_CANCELED.getCode())
                        record.setSettlementStatus(0);//未结算
                    else if(status == SettlementHospitalConfirmEnum.SETTLEMENT_TO_BE_CONFIRM.getCode()|| status == SettlementHospitalConfirmEnum.SETTLEMENT_CONFIRMD.getCode())
                        record.setSettlementStatus(1);//已结算
                }
                if(map.get("batch_sn")!=null)
                    record.setBatchSn(map.get("batch_sn").toString());//医院/渠道结算批次号
                if(map.get("sn")!=null)
                    record.setSn(map.get("sn").toString());//新建批次
				record.setGmtCreated(simplehms.parse(map.get("gmt_created").toString()));
				record.setGmtModified(simplehms.parse(map.get("gmt_modified").toString()));
				record.setOperatorId(Integer.parseInt(map.get("operator_id").toString()));
				if(map.get("operator_name") !=null)
				record.setOperatorName(map.get("operator_name").toString());
				record.setSettlementViewType(Integer.parseInt(map.get("settlement_view_type").toString()));
				records.add(record);
				}
			
		} catch (SqlException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		return records;
	}


    /**
     * 传入医院视角的sql语句,查询取得特殊退款记录以及渠道批次号
     * @param sql
     * @return
     */
    public static List<TradePrepaymentRecordVO> getTradePrepaymentRecordView(String sql) {
        List<TradePrepaymentRecordVO> records = new ArrayList<TradePrepaymentRecordVO>();
        try {
            List<Map<String, Object>> list2 = DBMapper.query(sql);
            for (Map<String, Object> map : list2) {
                TradePrepaymentRecordVO record = new TradePrepaymentRecordVO();
                int id = Integer.parseInt(map.get("id").toString());
                record.setId(id);
                int organizationId = Integer.parseInt(map.get("organization_id").toString());
                String organizationName = HospitalChecker.getHospitalById(organizationId).getName();
                int refundOrganizationId = Integer.parseInt(map.get("refund_organization_id").toString());//退款机构ID
                log.info("res" + refundOrganizationId);
                String refundOrganizationName = HospitalChecker.getHospitalById(refundOrganizationId).getName();//退款机构名
                int organizationType = HospitalChecker.getHospitalById(refundOrganizationId).getOrganizationType();
                int refundCompanyId = Integer.parseInt(map.get("refund_company_id").toString());//退款单位ID
                String refundCompanyName = null;
                record.setChannelPlatformCompany(false);
                if (organizationType == OrganizationTypeEnum.CHANNEL.getCode()) {
                    ChannelCompany channelCompany = CompanyChecker.getChannelCompanyByCompanyId(refundCompanyId);
                    refundCompanyName = channelCompany.getName();
                    if (channelCompany.getPlatformCompanyId() != null) {
                        if (channelCompany.getPlatformCompanyId() > 5)//只有P单位
                            record.setChannelPlatformCompany(true);
                    }
                } else if (organizationType == OrganizationTypeEnum.HOSPITAL.getCode()){
                    HospitalCompany hospitalCompany = CompanyChecker.getHospitalCompanyById(refundCompanyId);
                    refundCompanyName = hospitalCompany.getName();
                    if (hospitalCompany.getPlatformCompanyId() != null) {
                        if (hospitalCompany.getPlatformCompanyId() >= 3)//医院每天健康，或者P单位
                            record.setChannelPlatformCompany(true);
                    }

                }
                record.setOrganizationId(organizationId);
                record.setOrganizationName(organizationName);
                record.setRefundOrganizationId(refundOrganizationId);
                record.setRefundOrganizationName(refundOrganizationName);
                record.setRefundCompanyId(refundCompanyId);
                record.setRefundCompanyName(refundCompanyName);
                record.setPaymentTime(simplehms.parse(map.get("payment_time").toString()));
                if (map.get("certificate") != null)
                    record.setCertificate(map.get("certificate").toString());
                if (map.get("remark") != null)
                    record.setRemark(map.get("remark").toString());
                if (map.get("type") != null) {
                    int type = Integer.parseInt(map.get("type").toString());
                    record.setType(type);
                }
                record.setPlatformCompany(false);
                if (map.get("company_id") != null) {
                    int hospitalCompanyId = Integer.parseInt(map.get("company_id").toString());
                    HospitalCompany hc = CompanyChecker.getHospitalCompanyById(hospitalCompanyId);
                    record.setCompanyId(hospitalCompanyId);
                    record.setCompanyName(hc.getName());
                    if (hc.getPlatformCompanyId() != null) {
                        if (hc.getPlatformCompanyId() >= 3)
                            record.setPlatformCompany(true);
                    }
                }
                record.setAmount(Integer.parseInt(map.get("amount").toString()));


                record.setIsDeleted(Integer.parseInt(map.get("is_deleted").toString()));
                record.setGmtCreated(simplehms.parse(map.get("gmt_created").toString()));
                record.setGmtModified(simplehms.parse(map.get("gmt_modified").toString()));
                record.setOperatorId(Integer.parseInt(map.get("operator_id").toString()));
                if (map.get("operator_name") != null)
                    record.setOperatorName(map.get("operator_name").toString());
                int settlement_view_type = Integer.parseInt(map.get("settlement_view_type").toString());
                record.setSettlementViewType(settlement_view_type);
                int status = Integer.parseInt(map.get("status").toString());
                record.setStatus(status);
                String sn = null;
                if (map.get("sn") != null) {
                    sn = map.get("sn").toString();
                    record.setSn(sn);
                }
                //医院视角
                if (organizationType == OrganizationTypeEnum.HOSPITAL.getCode()) {
                        if (map.get("batch_sn") != null)
                            record.setBatchSn(map.get("batch_sn").toString());//医院结算批次号
                        if (Integer.parseInt(map.get("is_deleted").toString()) == 1)
                            record.setSettlementStatus(2);//已删除
                        else {
                            if (status == SettlementHospitalConfirmEnum.UNSETTLEMENT.getCode() || status == SettlementHospitalConfirmEnum.SETTLEMENT_CANCELED.getCode())
                                record.setSettlementStatus(0);//未结算
                            else if (status == SettlementHospitalConfirmEnum.SETTLEMENT_TO_BE_CONFIRM.getCode() || status == SettlementHospitalConfirmEnum.SETTLEMENT_CONFIRMD.getCode())
                                record.setSettlementStatus(1);//已结算
                        }
                        record.setChannelSettlementStatus(null);
                        record.setChannelSettlementBatch(null);
                    }
                    //渠道视角
                    if (organizationType == OrganizationTypeEnum.CHANNEL.getCode()) {
                        //渠道结算
                        List<TradePrepaymentRecord> rrList = getTradePrepaymentRecordBySql("select * from tb_trade_prepayments_record where settlement_view_type = 1 and sn = '" + sn + "'");
                        Assert.assertEquals(rrList.size(), 1);
                        String channelSettBatch = rrList.get(0).getBatchSn();
                        int channelSettlement = rrList.get(0).getSettlementStatus();
                        record.setChannelSettlementBatch(channelSettBatch);
                        record.setChannelSettlementStatus(channelSettlement);
                        //医院结算
                        rrList = getTradePrepaymentRecordBySql("select * from tb_trade_prepayments_record where settlement_view_type = 0 and sn = '" + sn + "'");
                        Assert.assertEquals(rrList.size(), 1);
                        String hospitalSettBatch = rrList.get(0).getBatchSn();
                        int hospitalSettment = rrList.get(0).getSettlementStatus();
                        record.setSettlementStatus(hospitalSettment);
                        record.setBatchSn(hospitalSettBatch);
                    }
                    records.add(record);
            }

        } catch (SqlException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return records;
    }


		/**
	 * 传入sql语句,查询取得结果
	 * @param sql
	 * @return
	 */
	public static List<TradeSettlementPayRecord> getTradeSettlePaymentRecordBySql(String sql){
		List<TradeSettlementPayRecord> records = new ArrayList<TradeSettlementPayRecord>();
		try {
			List<Map<String,Object>> list2 = DBMapper.query(sql);
			for(Map<String,Object> map : list2){
				TradeSettlementPayRecord record = new TradeSettlementPayRecord(); 
				int id = Integer.parseInt(map.get("id").toString());
				record.setId(id);				
				String sn = map.get("sn").toString();//收款记录流水号
				record.setSn(sn);
				int organizationId = Integer.parseInt(map.get("organization_id").toString());
				record.setOrganizationId(organizationId);
				Hospital h = HospitalChecker.getHospitalById(organizationId);
				if(h != null && h.getName() != null){
					String organizationName = h.getName();
					record.setOrganizationName(organizationName);
				}
				record.setPayableAmount(Long.parseLong(map.get("payable_amount").toString()));
				record.setRealAmount(Long.parseLong(map.get("real_amount").toString()));
				record.setPaymentTime(simplehms.parse(map.get("payment_time").toString()));
				if(map.get("certificate")!=null)
					record.setCertificate(map.get("certificate").toString());
				if(map.get("remark")!=null)
					record.setRemark(map.get("remark").toString());
				if(map.get("type")!=null){
					int type = Integer.parseInt(map.get("type").toString());
					record.setType(type);
					if(type == 1 ){//平台付款
						int operator_id = Integer.parseInt(map.get("operator_id").toString());
//						String operator_name = AccountChecker.getOpsAccount(operator_id).getName() ;
						record.setOperatorId(operator_id);
//						record.setOperatorName(operator_name);	
						record.setCompanyName("平台");
						List<TradeHospitalPlatformBill> tradeHospitalPlatformBillList = 
								getTradeHospitalPlatformBillByColumn("select * from tb_trade_hospital_platform_bill where payment_record_sn = '"+sn +"' and is_deleted = 0 order by batch_sn desc");
						record.setTradeHospitalPlatformBillList(tradeHospitalPlatformBillList); //平台账单列表
					}
					if(type == 0 || type == 2 || type ==3 || type == 4 ){//单位开票
						int operator_id = Integer.parseInt(map.get("operator_id").toString());
//						String operator_name = AccountChecker.getAccountById(operator_id).getName() ;
						record.setOperatorId(operator_id);
//						record.setOperatorName(operator_name);
						if(map.get("company_id")!=null){
							int company_id = Integer.parseInt(map.get("company_id").toString());
							HospitalCompany hc  = CompanyChecker.getHospitalCompanyById(company_id);
							String companyName = hc.getName();
							record.setCompanyName(companyName);
							record.setCompanyId(company_id);
						}

						List<TradeHospitalCompanyBill> tradeHospitalCompanyBillList = getTradeHospitalCompanyBillByColumn("batch_sn", false,null,false, "payment_record_sn","'"+sn+"'","is_deleted","0");
						record.setTradeHospitalCompanyBillList(tradeHospitalCompanyBillList);//单位账单列表
					
					}
				}	
				record.setIsDeleted(Integer.parseInt(map.get("is_deleted").toString()));
				record.setGmtCreated(simplehms.parse(map.get("gmt_created").toString()));
				record.setGmtModified(simplehms.parse(map.get("gmt_modified").toString()));
				if(map.get("operator_name") !=null)
				record.setOperatorName(map.get("operator_name").toString());
				//折后应付/总消费额度
				if(map.get("discount_amount")!=null)
					record.setTotalDiscountAmount(Long.parseLong(map.get("discount_amount").toString()));
				if(map.get("consume_quota_amount")!=null)
					record.setTotalConsumeQuotaAmount(Long.parseLong(map.get("consume_quota_amount").toString()));
				records.add(record);
				}
			
		} catch (SqlException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		return records;
	}
	
	/**
	 * 多个条件查询收款记录表
	 * @param hospitalId
	 * @param companyId
	 * @param startTime
	 * @param endTime
	 * @param type
	 * @param orderColumn
	 * @param isAsc
	 * @param pageSize
	 * @return
	 */
	public static List<TradeSettlementPayRecord> getTradeSettlePaymentRecordByColumn(int hospitalId,int companyId,String startTime,String endTime,int type,String orderColumn,boolean isAsc,String pageSize){
		List<TradeSettlementPayRecord> records = new ArrayList<TradeSettlementPayRecord>();
		String sql = "select * from tb_trade_settlement_payment_record where organization_id = "+hospitalId;
		if(companyId != -1)
			sql += " and company_id = "+companyId;
		if(startTime != null)
			sql += " and gmt_created > '"+startTime+"'";
		if (endTime != null)
			sql += " and gmt_created < '"+endTime+"'";
		if (type != -1)
			sql += " and type ="+type;
		if (orderColumn != null){
			sql += " order by  "+orderColumn;
			if(isAsc)
				sql += " asc";
			else 
				sql += " desc";
			}
		if(pageSize != null)
			sql += " limit "+pageSize;
		log.info("sql.."+sql);
		try {
			List<Map<String,Object>> list2 = DBMapper.query(sql);
			for(Map<String,Object> map : list2){
				TradeSettlementPayRecord record = new TradeSettlementPayRecord(); 
				int id = Integer.parseInt(map.get("id").toString());
				record.setId(id);
				record.setSn(map.get("sn").toString());
				int organizationId = Integer.parseInt(map.get("organization_id").toString());
				String organizationName = HospitalChecker.getHospitalById(organizationId).getName();
				record.setOrganizationId(organizationId);
				record.setOrganizationName(organizationName);
				int company_id = Integer.parseInt(map.get("company_id").toString());
				String companyName = CompanyChecker.getHospitalCompanyById(company_id).getName();
				record.setCompanyId(company_id);
				record.setCompanyName(companyName);
				int operator_id = Integer.parseInt(map.get("operator_id").toString());
				String operator_name = AccountChecker.getAccountById(operator_id).getName() ;
				record.setOperatorId(operator_id);
				record.setOperatorName(operator_name);	
				record.setPayableAmount(Long.parseLong(map.get("payable_amount").toString()));
				record.setRealAmount(Long.parseLong(map.get("real_amount").toString()));
				record.setPaymentTime(simplehms.parse(map.get("payment_time").toString()));
				if(map.get("certificate")!=null)
					record.setCertificate(map.get("certificate").toString());
				if(map.get("remark")!=null)
					record.setRemark(map.get("remark").toString());
				if(map.get("type")!=null)
					record.setType(Integer.parseInt(map.get("type").toString()));
				record.setIsDeleted(Integer.parseInt(map.get("is_deleted").toString()));
				record.setGmtCreated(simplehms.parse(map.get("gmt_created").toString()));
				record.setGmtModified(simplehms.parse(map.get("gmt_modified").toString()));
				//折后应付/总消费额度
				if(map.get("discount_amount")!=null)
					record.setTotalDiscountAmount(Long.parseLong(map.get("discount_amount").toString()));
				if(map.get("consume_quota_amount")!=null)
					record.setTotalConsumeQuotaAmount(Long.parseLong(map.get("consume_quota_amount").toString()));
				records.add(record);
				}
			
		} catch (SqlException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		return records;
	}
	
	/**
	 * 根据字段和字段值查询结算收款记录表
	 * @param args
	 * @return
	 */
	public static List<TradeSettlementPayRecord> getTradeSettlePaymentRecordByColumn(String ...args){
		return getTradeSettlePaymentRecordByColumn(-1,args);
	}
	/**
	 * 根据字段和字段值查询结算收款记录表
	 * 有条数限制
	 * @param key
	 * @param value
	 * @return
	 */
	public static List<TradeSettlementPayRecord> getTradeSettlePaymentRecordByColumn(int limitnum,String ...args){
		List<TradeSettlementPayRecord> records = new ArrayList<TradeSettlementPayRecord>();
		String ps = changeManyStringToOneStr(args);
		String sql = "select * from tb_trade_settlement_payment_record ";
		if(args.length > 0)
			sql += " where "+ps;
		if(limitnum != -1)
			sql += " limit "+limitnum;
		try {
			List<Map<String,Object>> list2 = DBMapper.query(sql);
			for(Map<String,Object> map : list2){
				TradeSettlementPayRecord record = new TradeSettlementPayRecord(); 
				int id = Integer.parseInt(map.get("id").toString());
				record.setId(id);
				record.setSn(map.get("sn").toString());
				int organizationId = Integer.parseInt(map.get("organization_id").toString());
				String organizationName = HospitalChecker.getHospitalById(organizationId).getName();
				record.setOrganizationId(organizationId);
				record.setOrganizationName(organizationName);
				int company_id = Integer.parseInt(map.get("company_id").toString());
				String companyName = CompanyChecker.getHospitalCompanyById(company_id).getName();
				record.setCompanyId(company_id);
				record.setCompanyName(companyName);
				int operator_id = Integer.parseInt(map.get("operator_id").toString());
				String operator_name = AccountChecker.getAccountById(operator_id).getName() ;
				record.setOperatorId(operator_id);
				record.setOperatorName(operator_name);	
				record.setPayableAmount(Long.parseLong(map.get("payable_amount").toString()));
				record.setRealAmount(Long.parseLong(map.get("real_amount").toString()));
				record.setPaymentTime(simplehms.parse(map.get("payment_time").toString()));
				if(map.get("certificate")!=null)
					record.setCertificate(map.get("certificate").toString());
				if(map.get("remark")!=null)
					record.setRemark(map.get("remark").toString());
				if(map.get("type")!=null)
					record.setType(Integer.parseInt(map.get("type").toString()));
				record.setIsDeleted(Integer.parseInt(map.get("is_deleted").toString()));
				record.setGmtCreated(simplehms.parse(map.get("gmt_created").toString()));
				record.setGmtModified(simplehms.parse(map.get("gmt_modified").toString()));
				//折后应付/总消费额度
				if(map.get("discount_amount")!=null)
					record.setTotalDiscountAmount(Long.parseLong(map.get("discount_amount").toString()));
				if(map.get("consume_quota_amount")!=null)
					record.setTotalConsumeQuotaAmount(Long.parseLong(map.get("consume_quota_amount").toString()));
				records.add(record);
			}
		} catch (SqlException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		return records;	
	}

	/**
	 * 根据字段和字段值查询批次表
	 * @param args
	 * @return
	 */
	public static List<TradeSettlementBatch> getTradeSettlementBatchByColumn(String ...args){
		List<TradeSettlementBatch> batchs = new ArrayList<TradeSettlementBatch>();
		String ps = changeManyStringToOneStr(args);
		String sql = "select * from tb_trade_settlement_batch where "+ps;
		List<Map<String,Object>> dblist = null;
		try {
			dblist = DBMapper.query(sql);
			if(dblist != null && dblist.size()>0){
				for(Map<String,Object> map:dblist){
					TradeSettlementBatch batch = new TradeSettlementBatch();
					batch.setId(Integer.parseInt(map.get("id").toString()));
					batch.setSn(map.get("sn").toString());
					batch.setHospitalId(Integer.parseInt(map.get("hospital_id").toString()));
					batch.setCompanyId(Integer.parseInt(map.get("company_id").toString()));
					batch.setCompanyPayAmount(Long.parseLong(map.get("company_pay_amount").toString()));
					batch.setOnlinePayAmount(Long.parseLong(map.get("online_pay_amount").toString()));
					batch.setPlatformPayAmount(Long.parseLong(map.get("platform_pay_amount").toString()));
					batch.setCompanyRefundAmount(Long.parseLong(map.get("company_refund_amount").toString()));
					batch.setOnlineRefundAmount(Long.parseLong(map.get("online_refund_amount").toString()));
					batch.setPlatformRefundAmount(Long.parseLong(map.get("platform_refund_amount").toString()));
					batch.setPlatformPrepaymentAmount(Long.parseLong(map.get("platform_prepayment_amount").toString()));
					if(map.get("payment_record_sn")!=null)
						batch.setPaymentRecordSn(map.get("payment_record_sn").toString());
					if(map.get("channel_id")!=null)//渠道ID
						batch.setChannelId(Integer.parseInt(map.get("channel_id").toString()));
					if(map.get("channel_company_id")!=null)//渠道单位ID
						batch.setChannelCompanyId(Integer.parseInt(map.get("channel_company_id").toString()));
					batch.setSettlementViewType(Integer.parseInt(map.get("settlement_view_type").toString()));
					batch.setHospitalSettlementStatus(Integer.parseInt(map.get("hospital_settlement_status").toString()));
					int operator_id = Integer.parseInt(map.get("operator_id").toString());
					String operator_name = AccountChecker.getAccountById(operator_id).getName() ;
					batch.setOperatorId(operator_id);
					batch.setOperatorName(operator_name);
					batch.setIsdeleted(Integer.parseInt(map.get("is_deleted").toString()));
					batch.setGmtCreated(simplehms.parse(map.get("gmt_created").toString()));
					batch.setGmtModified(simplehms.parse(map.get("gmt_modified").toString()));
					batch.setHospitalCouponAmount(Long.parseLong(map.get("hospital_coupon_amount").toString()));
					batch.setHospitalCouponRefundAmount(Long.parseLong(map.get("hospital_coupon_refund_amount").toString()));
					batch.setPlatformCouponAmount(Long.parseLong(map.get("platform_coupon_amount").toString()));
					batch.setPlatformCouponRefundAmount(Long.parseLong(map.get("platform_coupon_refund_amount").toString()));
					batch.setChannelCouponAmount(Long.parseLong(map.get("channel_coupon_amount").toString()));
					batch.setChannelCouponRefundAmount(Long.parseLong(map.get("channel_coupon_refund_amount").toString()));
					batch.setHospitalOnlinePayAmount(Long.parseLong(map.get("hospital_online_pay_amount").toString()));
					batch.setHospitalOnlineRefundAmount(Long.parseLong(map.get("hospital_online_refund_amount").toString()));
					batch.setPlatformOnlinePayAmount(Long.parseLong(map.get("platform_online_pay_amount").toString()));
					batch.setPlatformOnlineRefundAmount(Long.parseLong(map.get("platform_online_refund_amount").toString()));
					batch.setChannelOnlinePayAmount(Long.parseLong(map.get("channel_online_pay_amount").toString()));
					batch.setChannelOnlineRefundAmount(Long.parseLong(map.get("channel_online_refund_amount").toString()));
					batch.setChannelCompanyPayAmount(Long.parseLong(map.get("channel_company_pay_amount").toString()));
					batch.setChannelCompanyRefundAmount(Long.parseLong(map.get("channel_company_refund_amount").toString()));
					batch.setOfflinePayAmount(Long.parseLong(map.get("offline_pay_amount").toString()));
					batch.setChannelCardPayAmount(Long.parseLong(map.get("channel_card_pay_amount").toString()));
					batch.setChannelCardRefundAmount(Long.parseLong(map.get("channel_card_refund_amount").toString()));
					batchs.add(batch);
				}
			}
		} catch (SqlException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		return batchs;
	}

	/**
	 * 根据类型获取医院有单位账单的单位列表
	 * type =0 医院单位账单, type=2 医院优惠券账单 type=3 医院线上账单 type=4 医院线下账单
	 * @param hospitalId
	 * @return
	 */
	public static List<Integer> getCompanyListWithCompanyBill(int hospitalId,int type){
		List<Integer> companyIdList = new ArrayList<Integer>();
		//医院所有已确认的账单，根据单位分组
		List<TradeHospitalCompanyBill> companyBillList = getTradeHospitalCompanyBillByColumn(null,false,"company_id", true, "hospital_id",hospitalId+"","status",SettlementHospitalConfirmEnum.SETTLEMENT_CONFIRMD.getCode()+"","type",type+"");
		for(TradeHospitalCompanyBill com : companyBillList)
			companyIdList.add(com.getCompanyId());
		return companyIdList;
	}
	
	/**
	 * 获取医院有单位账单的单位列表
	 * @param hospitalId
	 * @return
	 */
	public static List<Integer> getCompanyListWithCompanyBill(int hospitalId){
		List<Integer> companyIdList = new ArrayList<Integer>();
		//医院所有已确认的账单，根据单位分组
		List<TradeHospitalCompanyBill> companyBillList = getTradeHospitalCompanyBillByColumn(null,false,"company_id", true, "hospital_id",hospitalId+"","status",SettlementHospitalConfirmEnum.SETTLEMENT_CONFIRMD.getCode()+"");
		for(TradeHospitalCompanyBill com : companyBillList)
			companyIdList.add(com.getCompanyId());
		return companyIdList;
	}
	/**
	 * 根据字段&字段值查询医院单位账单表
	 * @param ascColumn
	 * @param isAsc
	 * @param column
	 * @param isGroup
	 * @param args
	 * @return
	 */
	public static List<TradeHospitalCompanyBill> getTradeHospitalCompanyBillByColumn(String ascColumn, boolean isAsc,String column,boolean isGroup,String ...args){
		List<TradeHospitalCompanyBill> bills = new ArrayList<TradeHospitalCompanyBill>();
		String ps = changeManyStringToOneStr(args);
		String sql = "select * from tb_trade_hospital_company_bill where "+ps;
		if(isGroup)
			sql += " group by "+column;
		if(ascColumn !=null){
			sql += " order by "+ascColumn;
			if(isAsc)
				sql += " asc";
			else
				sql += " desc";
		}
		log.info("数据库..."+sql);
		List<Map<String,Object>> dblist = null;
		try {
			dblist = DBMapper.query(sql);
			if(dblist != null && dblist.size()>0){
				for(Map<String,Object> map : dblist){
					TradeHospitalCompanyBill bill = new TradeHospitalCompanyBill();
					bill.setId(Integer.parseInt(map.get("id").toString()));
					bill.setSn(map.get("sn").toString());
					bill.setBatchSn(map.get("batch_sn").toString());
					bill.setHospitalId(Integer.parseInt(map.get("hospital_id").toString()));
					int companyId = Integer.parseInt(map.get("company_id").toString());
					String companyName = CompanyChecker.getHospitalCompanyById(companyId).getName();
					bill.setCompanyId(companyId);
					bill.setCompanyName(companyName);
					bill.setCompanyPayAmount(Long.parseLong(map.get("company_pay_amount").toString()));
					bill.setCompanyRefundAmount(Long.parseLong(map.get("company_refund_amount").toString()));
					bill.setCompanyChargedAmount(Long.parseLong(map.get("company_charged_amount").toString()));
					bill.setStatus(Integer.parseInt(map.get("status").toString()));
					int operator_id = Integer.parseInt(map.get("operator_id").toString());
					bill.setOperatorId(operator_id);
					bill.setIsDeleted(Integer.parseInt(map.get("is_deleted").toString()));
					if(map.get("payment_record_sn")!=null)
						bill.setPaymentRecordSn(map.get("payment_record_sn").toString());
					bill.setSettlementViewType(Integer.parseInt(map.get("settlement_view_type").toString()));
					bill.setGmtCreated(simplehms.parse(map.get("gmt_created").toString()));
					bill.setGmtModified(simplehms.parse(map.get("gmt_modified").toString()));
					bills.add(bill);
					
				}
			}
		} catch (SqlException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		return bills;
	}
	
	
	
	/**
	 * 查找平台账单数据,并排序
	 * @param sql
	 * @return
	 */
	public static List<TradeHospitalPlatformBill> getTradeHospitalPlatformBillByColumn(String sql){
		log.info("数据库..."+sql);
		List<TradeHospitalPlatformBill> bills = new ArrayList<TradeHospitalPlatformBill>();
		List<Map<String,Object>> dblist = null;
		try {
			dblist = DBMapper.query(sql);
			if(dblist != null && dblist.size()>0){
				for(Map<String,Object> map : dblist){
					TradeHospitalPlatformBill bill = new TradeHospitalPlatformBill();
					bill.setId(Integer.parseInt(map.get("id").toString()));
					//批次
					bill.setSn(map.get("sn").toString());
					bill.setBatchSn(map.get("batch_sn").toString());
					if(map.get("payment_record_sn")!=null)
						bill.setPaymentRecordSn(map.get("payment_record_sn").toString());
					//医院
					int hospitalId = Integer.parseInt(map.get("hospital_id").toString());
					bill.setHospitalId(hospitalId);
					//单位ID,单位名称,单位类型，单位折扣
					int companyid = Integer.parseInt(map.get("company_id").toString());
					HospitalCompany hc = CompanyChecker.getHospitalCompanyById(companyid);
					String companyname = hc.getName();
					bill.setCompanyId(companyid);
					bill.setCompanyName(companyname);
					Map<String,Object> discountsMap = HospitalChecker.getHospitalSetting(hospitalId, HospitalParam.GuestOnlineCompDiscount,HospitalParam.GuestOfflineCompDiscount,
							HospitalParam.HospitalCompDiscount,HospitalParam.PlatFormCompDiscount,HospitalParam.PlatFormGuestDiscount);
					double discount = 0;
					if(hc.getPlatformCompanyId() !=null){
						int plat_company_id = hc.getPlatformCompanyId();
						if(plat_company_id == 1){
							bill.setCompanyType("个人网上预约");
							discount = Double.parseDouble(discountsMap.get(HospitalParam.GuestOnlineCompDiscount).toString());
							}
						else if(plat_company_id == 2){
							bill.setCompanyType("前台散客");
							discount = Double.parseDouble(discountsMap.get(HospitalParam.GuestOfflineCompDiscount).toString());

							}
						else if(plat_company_id == 3){
							bill.setCompanyType("平台散客");
							discount = Double.parseDouble(discountsMap.get(HospitalParam.PlatFormGuestDiscount).toString());

							}
						else{
							bill.setCompanyType("平台单位");
							//平台单位取自身在医院的单位折扣
							discount = hc.getDiscount().doubleValue();

							}
							
					}else{
						bill.setCompanyType("普通单位");
						discount = Double.parseDouble(discountsMap.get(HospitalParam.HospitalCompDiscount).toString());
						}
					//单位折扣
					bill.setPlatformDiscount(discount);
					//平台金额（平台支付|平台退款|平台预付款|平台应收|平台实收）
					bill.setPlatformPayAmount(Long.parseLong(map.get("platform_pay_amount").toString()));
					bill.setPlatformRefundAmount(Long.parseLong(map.get("platform_refund_amount").toString()));
					bill.setPlatformPrepaymentAmount(Long.parseLong(map.get("platform_prepayment_amount").toString()));
					bill.setPlatformChargedAmount(Long.parseLong(map.get("platform_charged_amount").toString()));
					if(map.get("platform_acturally_pay_amount")!=null)
						bill.setPlatformActurallyPayAmount(Long.parseLong(map.get("platform_acturally_pay_amount").toString()));
					if(map.get("remark")!=null)
						bill.setRemark(map.get("remark").toString());
					//状态
					bill.setStatus(Integer.parseInt(map.get("status").toString()));
					int operator_id = Integer.parseInt(map.get("operator_id").toString());
					bill.setOperatorId(operator_id);
					bill.setSettlementViewType(Integer.parseInt(map.get("settlement_view_type").toString()));
					bill.setIsDeleted(Integer.parseInt(map.get("is_deleted").toString()));
					//时间
					if(map.get("gmt_created")!=null)
						bill.setGmtCreated(simplehms.parse(map.get("gmt_created").toString()));
					if(map.get("gmt_modified")!=null)
						bill.setGmtModified(simplehms.parse(map.get("gmt_modified").toString()));
					if(map.get("discount_amount")!=null)
						bill.setDiscountAmount(Long.parseLong(map.get("discount_amount").toString()));
					if(map.get("consume_quota_amount")!=null)
						bill.setConsumeQuotaAmount(Long.parseLong(map.get("consume_quota_amount").toString()));
					bills.add(bill);
				}
				
			}
		} catch (SqlException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		return bills;
	}
	/**
	 * 根据字段&字段值查询医院平台账单表
	 * @param key
	 * @param value
	 * @return
	 */
	public static List<TradeHospitalPlatformBill> getTradeHospitalPlatformBillByColumn(String ...keyvalues){
		String ps = changeManyStringToOneStr(keyvalues);
		String sql = "select * from tb_trade_hospital_platform_bill where "+ps;
		return getTradeHospitalPlatformBillByColumn(sql);
	}
	
	
	/**
	 * 查找医院的特定状态的平台账单
	 * @param hospitalId
	 * @param status
	 * @return
	 */
	public static List<TradeHospitalPlatformBill> getTradeHospitalPlatformBillByColumn(int hospitalId,int status){
		String sql = "select * from tb_trade_hospital_platform_bill where is_deleted  = 0 and hospital_id = "+hospitalId+" and status = " + status + " and settlement_view_type = 0";
		return getTradeHospitalPlatformBillByColumn(sql);
	}
	
	

	/**
	 * 查询医院的结算批次（可根据条件查询单位，起始时间的结算批次）
	 * @param hospitalId
	 * @param companyId 传-1表示所有单位
	 * @param startTime 传null 表示不限制开始时间
	 * @param endTime 传null 表示不限制结束时间
	 * @param orderColumn 排序字段,传null表示无需排序
	 * @param isAsc 是否升序,true为升序，false为降序（orderColumn必须非null才有效 ）
	 * @param pageSize 分页,显示每页条数,传null表示无需限制
	 * @return
	 */
	public static List<TradeSettlementBatch> getTradeSettlementBatch(int hospitalId,int companyId,String startTime,String endTime,int status,String orderColumn,boolean isAsc,String pageSize){
		List<TradeSettlementBatch> retList = new ArrayList<TradeSettlementBatch>();
		String sql = "select * from tb_trade_settlement_batch where hospital_id = "+hospitalId + " and is_deleted = 0 and settlement_view_type = 0 ";
		if(companyId != -1)
			sql += " and company_id = "+companyId;
		if(startTime != null)
			sql += " and gmt_created > '"+startTime+"'";
		if (endTime != null)
			sql += " and gmt_created < '"+endTime+"'";
		if (status != -1)
			sql += " and hospital_settlement_status ="+status;
		if (orderColumn != null){
			sql += " order by  "+orderColumn;
			if(isAsc)
				sql += " asc";
			else 
				sql += " desc";
			}
		if(pageSize != null)
			sql += " limit "+pageSize;
		log.info("sql.."+sql);
		try {
			List<Map<String,Object>> dblist = DBMapper.query(sql);
			if(dblist != null && dblist.size() > 0){
				for(Map<String,Object> map : dblist){
					TradeSettlementBatch batch = new TradeSettlementBatch();
					batch.setId(Integer.parseInt(map.get("id").toString()));
					batch.setSn(map.get("sn").toString());
					batch.setHospitalId(Integer.parseInt(map.get("hospital_id").toString()));
					batch.setCompanyId(Integer.parseInt(map.get("company_id").toString()));
					batch.setCompanyPayAmount(Long.parseLong(map.get("company_pay_amount").toString()));
					batch.setOnlinePayAmount(Long.parseLong(map.get("online_pay_amount").toString()));
					batch.setPlatformPayAmount(Long.parseLong(map.get("platform_pay_amount").toString()));
					batch.setCompanyRefundAmount(Long.parseLong(map.get("company_refund_amount").toString()));
					batch.setOnlineRefundAmount(Long.parseLong(map.get("online_refund_amount").toString()));
					batch.setPlatformRefundAmount(Long.parseLong(map.get("platform_refund_amount").toString()));
					batch.setPlatformPrepaymentAmount(Long.parseLong(map.get("platform_prepayment_amount").toString()));
					if(map.get("payment_record_sn")!=null)
						batch.setPaymentRecordSn(map.get("payment_record_sn").toString());
					batch.setHospitalSettlementStatus(Integer.parseInt(map.get("hospital_settlement_status").toString()));
					int operator_id = Integer.parseInt(map.get("operator_id").toString());
					log.info("operator_id"+operator_id);
					batch.setOperatorId(operator_id);
					Account operato  = AccountChecker.getAccountById(operator_id);
					if(operato!= null && operato.getName()!=null)
						batch.setOperatorName(operato.getName());
					batch.setIsdeleted(Integer.parseInt(map.get("is_deleted").toString()));
					batch.setGmtCreated(simplehms.parse(map.get("gmt_created").toString()));
					batch.setGmtModified(simplehms.parse(map.get("gmt_modified").toString()));
					batch.setHospitalCouponAmount(Long.parseLong(map.get("hospital_coupon_amount").toString()));
					batch.setHospitalCouponRefundAmount(Long.parseLong(map.get("hospital_coupon_refund_amount").toString()));
					batch.setPlatformCouponAmount(Long.parseLong(map.get("platform_coupon_amount").toString()));
					batch.setPlatformCouponRefundAmount(Long.parseLong(map.get("platform_coupon_refund_amount").toString()));
					batch.setChannelCouponAmount(Long.parseLong(map.get("channel_coupon_amount").toString()));
					batch.setChannelCouponRefundAmount(Long.parseLong(map.get("channel_coupon_refund_amount").toString()));
					batch.setHospitalOnlinePayAmount(Long.parseLong(map.get("hospital_online_pay_amount").toString()));
					batch.setHospitalOnlineRefundAmount(Long.parseLong(map.get("hospital_online_refund_amount").toString()));
					batch.setPlatformOnlinePayAmount(Long.parseLong(map.get("platform_online_pay_amount").toString()));
					batch.setPlatformOnlineRefundAmount(Long.parseLong(map.get("platform_online_refund_amount").toString()));
					batch.setChannelOnlinePayAmount(Long.parseLong(map.get("channel_online_pay_amount").toString()));
					batch.setChannelOnlineRefundAmount(Long.parseLong(map.get("channel_online_refund_amount").toString()));
					batch.setChannelCompanyPayAmount(Long.parseLong(map.get("channel_company_pay_amount").toString()));
					batch.setChannelCompanyRefundAmount(Long.parseLong(map.get("channel_company_refund_amount").toString()));
					batch.setChannelCardPayAmount(Long.parseLong(map.get("channel_card_pay_amount").toString()));
					batch.setChannelCardRefundAmount(Long.parseLong(map.get("channel_card_refund_amount").toString()));
					batch.setOfflinePayAmount(Long.parseLong(map.get("offline_pay_amount").toString()));
					retList.add(batch);
				}
			}
		} catch (SqlException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		return retList;
	}

	/*
	*
	*
	* 渠道结算批次*/
	public static List<TradeSettlementBatch> getTradeSettlementBatch1(int channelId, String startTime, String endTime, int status, int channelCompanyId, String orderColumn, boolean isAsc, String pageSize){
		List<TradeSettlementBatch> retList = new ArrayList<TradeSettlementBatch>();
		String sql = "select * from tb_trade_settlement_batch where hospital_id = "+channelId + " and is_deleted = 0 and settlement_view_type = 1 ";
		if(channelCompanyId != -1)
			sql += " and company_id = "+channelCompanyId;
		if(startTime != null)
			sql += " and gmt_created > '"+startTime+"'";
		if (endTime != null)
			sql += " and gmt_created < '"+endTime+"'";
		if (status != -1)
			sql += " and hospital_settlement_status ="+status;
		if (orderColumn != null){
			sql += " order by  "+orderColumn;
			if(isAsc)
				sql += " asc";
			else
				sql += " desc";
		}
		if(pageSize != null)
			sql += " limit "+pageSize;
		log.info("sql.."+sql);
		try {
			List<Map<String,Object>> dblist = DBMapper.query(sql);
			if(dblist != null && dblist.size() > 0){
				for(Map<String,Object> map : dblist){
					TradeSettlementBatch batch = new TradeSettlementBatch();
					batch.setId(Integer.parseInt(map.get("id").toString()));
					batch.setSn(map.get("sn").toString());
					batch.setChannelId(Integer.parseInt(map.get("channel_id").toString()));
					batch.setChannelCompanyId(Integer.parseInt(map.get("channel_company_id").toString()));
					batch.setCompanyPayAmount(Long.parseLong(map.get("company_pay_amount").toString()));
					batch.setOnlinePayAmount(Long.parseLong(map.get("online_pay_amount").toString()));
					batch.setPlatformPayAmount(Long.parseLong(map.get("platform_pay_amount").toString()));
					batch.setCompanyRefundAmount(Long.parseLong(map.get("company_refund_amount").toString()));
					batch.setOnlineRefundAmount(Long.parseLong(map.get("online_refund_amount").toString()));
					batch.setPlatformRefundAmount(Long.parseLong(map.get("platform_refund_amount").toString()));
					batch.setPlatformPrepaymentAmount(Long.parseLong(map.get("platform_prepayment_amount").toString()));
					if(map.get("payment_record_sn")!=null)
						batch.setPaymentRecordSn(map.get("payment_record_sn").toString());
					batch.setHospitalSettlementStatus(Integer.parseInt(map.get("hospital_settlement_status").toString()));
					int operator_id = Integer.parseInt(map.get("operator_id").toString());
					String operator_name = AccountChecker.getAccountById(operator_id).getName() ;
					batch.setOperatorId(operator_id);
					batch.setOperatorName(operator_name);
					batch.setIsdeleted(Integer.parseInt(map.get("is_deleted").toString()));
					batch.setGmtCreated(simplehms.parse(map.get("gmt_created").toString()));
					batch.setGmtModified(simplehms.parse(map.get("gmt_modified").toString()));
					batch.setHospitalCouponAmount(Long.parseLong(map.get("hospital_coupon_amount").toString()));
					batch.setHospitalCouponRefundAmount(Long.parseLong(map.get("hospital_coupon_refund_amount").toString()));
					batch.setPlatformCouponAmount(Long.parseLong(map.get("platform_coupon_amount").toString()));
					batch.setPlatformCouponRefundAmount(Long.parseLong(map.get("platform_coupon_refund_amount").toString()));
					batch.setChannelCouponAmount(Long.parseLong(map.get("channel_coupon_amount").toString()));
					batch.setChannelCouponRefundAmount(Long.parseLong(map.get("channel_coupon_refund_amount").toString()));
					batch.setHospitalOnlinePayAmount(Long.parseLong(map.get("hospital_online_pay_amount").toString()));
					batch.setHospitalOnlineRefundAmount(Long.parseLong(map.get("hospital_online_refund_amount").toString()));
					batch.setPlatformOnlinePayAmount(Long.parseLong(map.get("platform_online_pay_amount").toString()));
					batch.setPlatformOnlineRefundAmount(Long.parseLong(map.get("platform_online_refund_amount").toString()));
					batch.setChannelOnlinePayAmount(Long.parseLong(map.get("channel_online_pay_amount").toString()));
					batch.setChannelOnlineRefundAmount(Long.parseLong(map.get("channel_online_refund_amount").toString()));
					batch.setChannelCompanyPayAmount(Long.parseLong(map.get("channel_company_pay_amount").toString()));
					batch.setChannelCompanyRefundAmount(Long.parseLong(map.get("channel_company_refund_amount").toString()));
					batch.setChannelCardPayAmount(Long.parseLong(map.get("channel_card_pay_amount").toString()));
					batch.setChannelCardRefundAmount(Long.parseLong(map.get("channel_card_refund_amount").toString()));
					batch.setOfflinePayAmount(Long.parseLong(map.get("offline_pay_amount").toString()));
					retList.add(batch);
				}
			}
		} catch (SqlException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		return retList;
	}
	
	/*****************流转日志************************/
	/**
	 * 根据平台账单sn查询流转日志
	 * @param platSn
	 * @param log_time
	 * @param asc 默认是正序,否则id倒序
	 * @return
	 */
	public static List<TradeCommonLogResultDTO> getTradeCommonLogList(String platSn,int log_type,String asc){
		List<TradeCommonLogResultDTO> bills = new ArrayList<TradeCommonLogResultDTO>();
		List<Map<String,Object>> dblist = null;
		try {
			String sql = "select * from tb_trade_common_log where ref_sn = '"+platSn+"' and log_type = "+log_type  +" order by id ";
			if(asc!=null){
				if(asc.equals("desc") || asc.equals("asc"))
					sql += asc;
			}
			dblist = DBMapper.query(sql);
			if(dblist != null && dblist.size()>0){
				for(Map<String,Object> map : dblist){
					TradeCommonLogResultDTO bill = new TradeCommonLogResultDTO();
					bill.setRefSn(map.get("ref_sn").toString());
					bill.setLogType(Integer.parseInt(map.get("log_type").toString()));
					bill.setGmtCreated(simplehms.parse(map.get("gmt_created").toString()));
					bill.setGmtModified(simplehms.parse(map.get("gmt_modified").toString()));
					bill.setOperatorId(Integer.parseInt(map.get("operator_id").toString()));
					bill.setOperatorType(Integer.parseInt(map.get("operator_type").toString()));
					bill.setOperation(map.get("operation").toString());
					bill.setOperatorName(map.get("operator_name").toString());
					bills.add(bill);
				}	
			}
		} catch (SqlException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		return bills;
	} 
	
	/*****************消费额度************************/
	/**
	 * 根据平台账单流水号查询消费额度
	 * @param platBillSn
	 * @return
	 */
	public static List<TradeConsumeQuotaDetail> getTradeConsumeQuotaDetailByPlatformBillSn(String platBillSn){
		String sql = "select * from  tb_trade_consume_quota_detail where platform_Bill_sn = '"+platBillSn+"'";
		List<TradeConsumeQuotaDetail> list = getTradeConsumeQuotaDetail(sql);
		return list;
	}
	/**
	 * 查找消费额度列表，并按照记录时间倒序排序
	 * @param column
	 * @param isAsc
	 * @param keyvalues
	 * @return
	 */
	public static List<TradeConsumeQuotaDetail> getTradeConsumeQuotaDetail(String sql){
		List<TradeConsumeQuotaDetail> bills = new ArrayList<TradeConsumeQuotaDetail>();
		List<Map<String,Object>> dblist = null;
		try {
			dblist = DBMapper.query(sql);
			if(dblist != null && dblist.size()>0){
				for(Map<String,Object> map : dblist){
					TradeConsumeQuotaDetail bill = new TradeConsumeQuotaDetail();
					//流水号
					bill.setId(Integer.parseInt(map.get("id").toString()));
					bill.setSn(map.get("sn").toString());
					if(map.get("platform_Bill_sn")!=null)
						bill.setPlatformBillSn(map.get("platform_Bill_sn").toString());
					//医院
					int hospitalId = Integer.parseInt(map.get("organization_id").toString());
					bill.setOrganizationId(hospitalId);
					//单位ID,单位名称,单位类型，单位折扣
					if(map.get("company_id")!=null){
						int companyid = Integer.parseInt(map.get("company_id").toString());
						HospitalCompany hc = CompanyChecker.getHospitalCompanyById(companyid);
						String companyname = hc.getName();
						bill.setCompanyId(companyid);
						bill.setCompanyName(companyname);
					}
					if(map.get("remark")!=null)
						bill.setRemark(map.get("remark").toString());
					//状态
					bill.setStatus(Integer.parseInt(map.get("status").toString()));
					bill.setIsDeleted(Integer.parseInt(map.get("is_deleted").toString()));
					//时间
					bill.setGmtCreated(simplehms.parse(map.get("gmt_created").toString()));
					bill.setGmtModified(simplehms.parse(map.get("gmt_modified").toString()));
					//金额
					bill.setAmount(Long.parseLong(map.get("amount").toString()));
					bill.setPayTime(simplehms.parse(map.get("pay_time").toString()));
					//场景
					bill.setScene(Integer.parseInt(map.get("scene").toString()));
					bill.setVersion(Integer.parseInt(map.get("version").toString()));
					if(map.get("certificate")!=null)
					bill.setCertificate(map.get("certificate").toString());
					bills.add(bill);
				}
				
			}
		} catch (SqlException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		return bills;
	}
	
	/**
	 * 根据医院Id查询总消费额度/本月净消费额度/上月净消费额度
	 * 已确认+冻结中的消费额度之和
	 * @param organizationId
	 * @return
	 */
	public static TradeConsumeQuotaStatistics getConsumeQuotaStatistics(int organizationId){
		TradeConsumeQuotaStatistics bills = new TradeConsumeQuotaStatistics();
		long forwardMounthAmont = 0l;
		long presentMounthAmont = 0l;
		long totalAmount = 0l;
		int toTolist = 0;
		List<Map<String,Object>> dblist = null;
		try {
			String statuses = "("+ ConsumeQuotaDetailStatusEnum.FREEZING.getCode()+","+ConsumeQuotaDetailStatusEnum.HOSPITAL_HAS_CONFIRMED.getCode()+")";
			String sql = "select * from tb_trade_consume_quota_detail"
					+ " where organization_id = "+organizationId + " and is_deleted = 0 and status in  "+statuses;
			dblist = DBMapper.query(sql);
			if(dblist != null && dblist.size()>0){
				for(Map<String,Object> map : dblist){
					TradeConsumeQuotaDetail bill = new TradeConsumeQuotaDetail();
					bill.setOrganizationId(organizationId);
					//流水号
					bill.setId(Integer.parseInt(map.get("id").toString()));
					bill.setSn(map.get("sn").toString());
					if(map.get("platform_Bill_sn")!=null)
						bill.setPlatformBillSn(map.get("platform_Bill_sn").toString());
					//时间
					bill.setGmtCreated(simplehms.parse(map.get("gmt_created").toString()));
					bill.setGmtModified(simplehms.parse(map.get("gmt_modified").toString()));
					//金额
					bill.setAmount(Long.parseLong(map.get("amount").toString()));
					//场景
					bill.setScene(Integer.parseInt(map.get("scene").toString()));
					Date lastMonthDay = DateUtils.theLastDayOfMonth(-1);
					Date last2MonthDay = DateUtils.theLastDayOfMonth(-2);
					String lastMonthStr = sdf.format(lastMonthDay);
					String last2MonthStr = sdf.format(last2MonthDay);
					//本月
					if(sdf.parse(sdf.format(bill.getGmtCreated())).getTime() > sdf.parse(lastMonthStr).getTime())
						presentMounthAmont += bill.getAmount();
					//上个月（时间<=上个月最后一天，大于上上个月最后一天）
					if(sdf.parse(sdf.format(bill.getGmtCreated())).getTime()<= sdf.parse(lastMonthStr).getTime()
						&& sdf.parse(sdf.format(bill.getGmtCreated())).getTime() > sdf.parse(last2MonthStr).getTime()){
//						log.info("上个月消费额度"+bill.getAmount());
						forwardMounthAmont += bill.getAmount();
					}

					totalAmount += bill.getAmount();
				}
				
			}
			bills.setForwardMounthAmont(forwardMounthAmont);
			bills.setPresentMounthAmont(presentMounthAmont);
			bills.setTotalAmount(totalAmount);
			bills.setOrganizationId(organizationId);
			String organizationName = HospitalChecker.getHospitalById(organizationId).getName();
			bills.setOrganizationName(organizationName);
		} catch (SqlException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		String sql2 = "select * from tb_trade_consume_quota_detail "
		+ " where organization_id = "+organizationId + " and is_deleted = 0 and status = 1";
		try {
			dblist = DBMapper.query(sql2);
			if(dblist != null && dblist.size()>0){
				toTolist += dblist.size();
			}
		} catch (SqlException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		
		bills.setTodoList(toTolist);
		return bills;
	}
	
	
	/**PlatFromSummaryBillList
	 * 根据省份/地区/辖区/医院获取有有效平台账单的医院列表
	 * 按照医院id正序排列
	 * @param provinceId
	 * @param city
	 * @param district
	 * @param hospitalId
	 * @return
	 */
	public static List<Integer> getHavePlatFormSummaryHospitalList(int provinceId,int city,int district,int hospitalId){
		List<Integer> hospitalList = new ArrayList<Integer>();
		//平台审核中=4|平台审核通过=5|财务审核中=8
		String statusList = "("+HospitalPlatformBillStatusEnum.PLATFORM_TO_BE_CONFIRMD.getCode()
				+","+HospitalPlatformBillStatusEnum.PLATFORM_CONFIREMD.getCode()
				+","+HospitalPlatformBillStatusEnum.PLATFORM_CAIWU_TOBE_CONFIRM.getCode()
				+")";
		String sql = "select DISTINCT d.hospital_id from tb_trade_hospital_platform_bill d ,tb_hospital h where h.id = d.hospital_id and h.enable = 1  and d.settlement_view_type = 0";//医院视角
		if(hospitalId != -1){
			sql += " and d.is_deleted = 0 and d.hospital_id = "+hospitalId;
		}else  if(provinceId == -1){//全国
			sql += " and d.is_deleted = 0 ";

		}else{
			if(city == -1){//有省的时候
				sql += " and h.address_id  like  '"+provinceId/1000 +"%'  and d.is_deleted = 0 ";
			}else{
				if(district == -1){//有省/市的时候
					sql += "  and  h.address_id  like   '"+city/100 +"%'  and d.is_deleted = 0 ";
				}else//有省/市/区的时候
					sql += "  and h.address_id  like   '"+district +"%'  and d.is_deleted = 0";
			}
		}
		try {
			sql += " and  d.status in "+statusList+"  and h.organization_type = "+OrganizationTypeEnum.HOSPITAL.getCode()+" order by d.hospital_id ";
			System.out.println("sql"+sql);
			List<Map<String,Object>> dbList = DBMapper.query(sql);
			for(Map<String,Object> l : dbList)
				hospitalList.add(Integer.parseInt(l.get("hospital_id").toString()));
		} catch (SqlException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		return hospitalList;
	}
	
	/**
	 * 获取某个医院的消费总额
	 * @param hospitalId
	 * @return
	 */
	public static long getConsumeQuotaByHospital(int hospitalId){
		TradeConsumeQuotaStatistics s = getConsumeQuotaStatistics(hospitalId);
		return s.getTotalAmount().longValue();
		
	}
	
	/**
	 * 传入医院列表,提取医院消费额度统计信息
	 * @param hospitalList
	 * @return
	 */
	public static List<TradeConsumeQuotaStatistics> getConsumeQuotaStatiList(List<Integer> hospitalList){
		List<TradeConsumeQuotaStatistics> list = new ArrayList<TradeConsumeQuotaStatistics>();
		for(Integer i : hospitalList){
			list.add(getConsumeQuotaStatistics(i));
		}
		return list;
	}
	
	/**
	 * 根据省份/地区/辖区/医院获取医院列表
	 * 按照医院id正序排列
	 * @param provinceId
	 * @param city
	 * @param district
	 * @param hospitalId
	 * @return
	 */
	public static List<Integer> getHasConsumeQuotaHospitalList(int provinceId,int city,int district,int hospitalId){
		List<Integer> hospitalList = new ArrayList<Integer>();
		String sql = "select DISTINCT d.organization_id from tb_trade_consume_quota_detail d  , tb_hospital h where  d.organization_id = h.id and  h.enable = 1 ";
		if(hospitalId != -1){
			sql += " and d.is_deleted = 0 and d.organization_id = "+hospitalId;
		}else  if(provinceId == -1){//全国
			sql += " and d.is_deleted = 0 ";

		}else{
			if(city == -1){//有省的时候
				sql += " and  h.address_id  like  '"+provinceId/1000 +"%'   and d.is_deleted = 0  ";
			}else{
				if(district == -1){//有省/市的时候
					sql += " and  h.address_id  like   '"+city/100 +"%'   and d.is_deleted = 0  ";
				}else//有省/市/区的时候
					sql += " and  h.address_id  like   '"+district +"%'   and d.is_deleted = 0 ";
			}
		}
		try {
			sql += " order by d.organization_id ";
			System.out.println("sql"+sql);
			List<Map<String,Object>> dbList = DBMapper.query(sql);
			for(Map<String,Object> l : dbList)
				hospitalList.add(Integer.parseInt(l.get("organization_id").toString()));
		} catch (SqlException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		return hospitalList;
	}
	

	/**
	 * 根据省份/地区/辖区/医院获取医院列表
	 * 按照医院id正序排列
	 * @param provinceId
	 * @param city
	 * @param district
	 * @param hospitalId
	 * @return
	 */	
public static List<Integer> getHavePayRecordList(int provinceId,int city,int district,int hospitalId){
	List<Integer> hospitalList = new ArrayList<Integer>();
	String sql = "select DISTINCT d.organization_id from tb_trade_settlement_payment_record d ";
	if(hospitalId != -1){
		sql += " where d.is_deleted = 0 and d.organization_id = "+hospitalId;
	}else  if(provinceId == -1){//全国
		sql += " where d.is_deleted = 0 ";

	}else{
		if(city == -1){//有省的时候
			sql += " , tb_hospital h where  h.address_id  like  '"+provinceId/1000 +"%'  and h.id = d.organization_id  and d.is_deleted = 0 ";
		}else{
			if(district == -1){//有省/市的时候
				sql += " , tb_hospital h where  h.address_id  like   '"+city/100 +"%'  and h.id = d.organization_id  and d.is_deleted = 0 ";
			}else//有省/市/区的时候
				sql += "  , tb_hospital h where  h.address_id  like   '"+district +"%'  and h.id = d.organization_id  and d.is_deleted = 0";
		}
	}
	try {
		sql += " order by d.organization_id ";
//		System.out.println("sql"+sql);
		List<Map<String,Object>> dbList = DBMapper.query(sql);
		for(Map<String,Object> l : dbList)
			hospitalList.add(Integer.parseInt(l.get("organization_id").toString()));
	} catch (SqlException e) {
		// TODO 自动生成的 catch 块
		e.printStackTrace();
	}
	return hospitalList;
}
	public static List<Integer>getHavePayRecordList(List<Integer> hospitalId) throws SqlException {
		String sql = "select DISTINCT d.organization_id from tb_payment_order d ";
		if(hospitalId != null){
			sql += " where d.is_deleted = 0 and d.organization_id = "+hospitalId;
		List<Map<String,Object>> dblist = DBMapper.query(sql, hospitalId);
		for(Map<String,Object> l : dblist)
			hospitalId.add(Integer.parseInt(l.get("organization_id").toString()));
		
	}
		return hospitalId;
	
	}

}

	


