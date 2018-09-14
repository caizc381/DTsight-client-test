package com.tijiantest.testcase.channel.order;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.tijiantest.base.dbcheck.OrderChecker;
import org.apache.commons.collections.CollectionUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.tijiantest.model.hospital.Hospital;
import com.tijiantest.model.item.ExamItem;
import com.tijiantest.model.item.RefuseStatusEnum;
import com.tijiantest.model.order.AccomplishOrder;
import com.tijiantest.model.order.Order;
import com.tijiantest.model.order.OrderStatus;
import com.tijiantest.model.resource.meal.ExamItemSnap;
import com.tijiantest.testcase.channel.ChannelBase;
import com.tijiantest.util.db.DBMapper;
import com.tijiantest.util.db.MongoDBUtils;
import com.tijiantest.util.db.SqlException;

public class OrderDetailBase extends ChannelBase{
	/**
     * 获取订单中各单项以及其信息
     * tb_order
     * @param orderId
     * @return
    */
	 public Map<Integer, ExamItemSnap> getExamItemsDetail(Integer orderId) {
		 Map<Integer, ExamItemSnap> showItemList = new HashMap<Integer, ExamItemSnap>();
		 List<ExamItemSnap> snapList = new ArrayList<ExamItemSnap>();
		 String sql = "SELECT items_detail FROM tb_order WHERE id = ?;";
		 System.out.println("sql:"+sql);
		 List<Map<String, Object>> list = null;
		 try {
			 list = DBMapper.query(sql,orderId);
		 } catch (SqlException e) {
		 // TODO Auto-generated catch block
			 e.printStackTrace();
		 }
		 if(list.size()>0){	
			 Map<String,Object> m = list.get(0);
			 String detail = m.get("items_detail").toString();
			 snapList = JSON.parseArray(detail,
					 ExamItemSnap.class);
			 if(snapList!=null) {
				 for (ExamItemSnap snap : snapList) {
					 showItemList.put(snap.getId(), snap);
				 }
			 }
			 	return showItemList;
		}
		 return null;
	  }
	 
	 /**
	   * tb_order_refund_log
	   * 获取最终体检项目（包括提交、拒检项目）
	   * @param orderId
	   * @return
	   */
	  public AccomplishOrder getAccomplishOrder(Integer orderId){
		  AccomplishOrder ac = new AccomplishOrder();
		  String sql = "select * from tb_order_refund_log where order_id = ? limit 1";
		  System.out.println("sql:"+sql);
		  List<Map<String, Object>> list = null;
		try {
			list = DBMapper.query(sql, orderId);
		} catch (SqlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		  if(list.size()>0){
			  Map<String,Object> m = list.get(0);
			  ac.setOrderId(Integer.valueOf(m.get("order_id").toString()));
			  ac.setHisItemIds(m.get("hisItemIds").toString());
			  ac.setStatus(m.get("status").toString());
			  AccomplishOrder acFromJson = JSONObject.parseObject(ac.getHisItemIds(), AccomplishOrder.class);
			  acFromJson.setOrderId(orderId);
			  return acFromJson;
		  }
		  return null;
	  }
	  
	  /**
	   * 从mongo获取订单项目的hisItemId
	   * @param orderId
	   * @return
	   */
	  public Map<String, Integer> getHisItemIds(Integer orderId) {
			String hisItemIds;
			Map<String, Integer> hisItemMap = new HashMap<String, Integer>();
			List<Map<String,Object>> monlist = MongoDBUtils.query("{\"id\": " + orderId + "}", MONGO_COLLECTION);
			if(monlist.size()>0){
				Map<String,Object> m = monlist.get(0);
				hisItemIds = m.get("hisItemIds").toString();
				for (String entryStr : hisItemIds.split(",")) {
					int index = entryStr.lastIndexOf(":");
					String code = entryStr.substring(0, index);
					String price = entryStr.substring(index + 1);
					
					int priceInFen = (int) (Double.parseDouble(price) * 100);
					hisItemMap.put(code, priceInFen);
				}
				return hisItemMap;
			}
			return null;
		}
	  
	  /**
	   * 通过体检中心id和hisItemId查询单项
	   * @param hospitalId
	   * @param hisItemId
	   * @return
	   */
	  public ExamItem getExamItemByHospitalAndHisItemId(Integer hospitalId, String hisItemId) {
		  String sql = "select * from tb_examitem where hospital_id = ? and his_item_id = ?";
		  System.out.println("sql:"+sql+"     hospitalId:"+hospitalId+"    hisItemId"+hisItemId);
		  List<Map<String, Object>> list = null;
		try {
			list = DBMapper.query(sql, hospitalId,hisItemId);
		} catch (SqlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			ExamItem item = new ExamItem();
		  if(list.size()>0){
			  Map<String,Object> m = list.get(0);
			  item.setId(Integer.valueOf(m.get("id").toString()));
			  item.setName(m.get("name").toString());
			  item.setPrice(Integer.valueOf(m.get("price").toString()));
			  item.setHisItemId(m.get("his_item_id").toString());
			  return item;
		  }
		  return null;
		}

	  /************************************以上从数据库或mongo获取数据******************************************/

	  
	//未检/拒检项目集合
	  public Map<String, Object> countOrderRefund(Integer orderId) {

			Map<String, Object> resultMap = new HashMap<String, Object>();
			AccomplishOrder ac = getAccomplishOrder(orderId);
			
			// 已完成订单日志中有记录，且订单状态不是撤销，则查询未检和拒检项目
			Order order = OrderChecker.getOrderInfo(orderId);
			if (ac != null && order.getStatus() != OrderStatus.REVOCATION.intValue()) {
				
				//获取未检项目集合
				Map<String, Object> unexamedItemsMap = getAccomplishUnexamedItemsRefund(ac.getExamItemList(), order);

				resultMap.putAll(unexamedItemsMap);

				// 拒检项目的金额和hisItemIds
				AccomplishOrder accomplishOrder = getAccomplishOrderWithRefusedItems(orderId);

				String[] refusedItemsArray = new String[] {};
				if (accomplishOrder != null && CollectionUtils.isNotEmpty(accomplishOrder.getExamItemList())) {

					List<String> hisItemIdsList = new ArrayList<String>();
					for (ExamItem item : accomplishOrder.getExamItemList()) {
						hisItemIdsList.add(item.getHisItemId());
					}

					refusedItemsArray = (String[]) hisItemIdsList.toArray(new String[hisItemIdsList.size()]);
					//refusedItemsPriceInFen = refundService.getItemsPrice(refusedItemsArray, order);
					
					List<ExamItem> refusedItems = new ArrayList<ExamItem>();
					for (String hisItemId : refusedItemsArray) {
						refusedItems.add(getExamItemByHospitalAndHisItemId(order.getHospital().getId(),
								hisItemId));
					}
					
					resultMap.put("refusedItemsDetail", refusedItems);

				}
				resultMap.put("refusedItems", refusedItemsArray);
				//resultMap.put("refusedItemsPriceInFen", refusedItemsPriceInFen);
			}
			return resultMap;
		}  
	  
	  /**
	   * 获取现场增加项目
	   * @param orderId
	   * @return
	   */
	  public List<ExamItem> getlocaleAddItems(Integer orderId) {
		  System.out.println("--------------------------------获取现场加项---------------------------------");
		  List<ExamItem> list = Lists.newArrayList();
		  Map<String, Integer> hisItemIds = getHisItemIds(orderId);
		  Order order = OrderChecker.getOrderInfo(orderId);
			AccomplishOrder accomplishOrder = getAccomplishOrder(orderId);
			if(accomplishOrder!=null){			
				// 用户实际体检项目中 比 用户订单项目要多的 说明是现场加项
				List<ExamItem> sceneAddItems = accomplishOrder.getExamItemList().stream()
						.filter(item->hisItemIds.get(item.getHisItemId())==null)
						.collect(Collectors.toList());
				// 查询体检项目价格 回单中的体检项目不包含价格
				for (ExamItem hisItem : sceneAddItems) {
					ExamItem relItem = getExamItemByHospitalAndHisItemId(order.getHospital().getId(), hisItem.getHisItemId());
					list.add(relItem);
				}	
			}
			return list;
		}
	  	  
	//获取拒检项目
	  public AccomplishOrder getAccomplishOrderWithRefusedItems(Integer orderId) {
		  System.out.println("--------------------------------获取拒检项目---------------------------------");
			AccomplishOrder accomplishOrder = getAccomplishOrder(orderId);
			if (accomplishOrder != null && CollectionUtils.isNotEmpty(accomplishOrder.getExamItemList())) {
				accomplishOrder
						.setExamItemList(accomplishOrder
								.getExamItemList()
								.stream()
								.filter(e -> Integer.parseInt(e.getRefuseStatus()) == RefuseStatusEnum.refused.getCode()
										.intValue()).collect(Collectors.toList()));

			}
			return accomplishOrder;
		}
	  
	  /**
	   * 获取未检项目
	   * @param accomplishOrderItemList
	   * @param order
	   * @return
	   */
	  public Map<String, Object> getAccomplishUnexamedItemsRefund(List<ExamItem> accomplishOrderItemList, Order order) {
		  System.out.println("--------------------------------获取未检项目---------------------------------");
		  //获取mongo中订单的单项
			Map<String, Integer> bookOrderItemMap = getHisItemIds(order.getId());
			
			if (accomplishOrderItemList!=null && !accomplishOrderItemList.isEmpty()) {
				ListIterator<ExamItem> iter = accomplishOrderItemList.listIterator();
				while (iter.hasNext()) {
					ExamItem item = iter.next();
					if (bookOrderItemMap.containsKey(item.getHisItemId())) {
						bookOrderItemMap.remove(item.getHisItemId());
					}
				}
			}

			Set<String> hisItemIdsSet = bookOrderItemMap.keySet();
			Map<String, Object> resultMap = new HashMap<String, Object>();
			// 计算未检项价格
			//int reducePriceInFen = 0;

			if (CollectionUtils.isNotEmpty(hisItemIdsSet)) {
				/*reducePriceInFen = getItemsPrice((String[]) hisItemIdsSet.toArray(new String[hisItemIdsSet.size()]), order,
						null, SettlementVisitor.defaultVisitor);
				logger.info("refund item is {},reduce price is {}", hisItemIdsSet.toString(), reducePriceInFen);*/
				
				List<ExamItem> unexamedItems = new ArrayList<ExamItem>();
				for (String hisItemId : hisItemIdsSet) {
					ExamItem ei = getExamItemByHospitalAndHisItemId(order.getHospital().getId(),hisItemId);
					if(ei==null) {
						continue;
					}
					unexamedItems.add(ei);
				}

				resultMap.put("unexamedItemsDetail", unexamedItems);
			}
			resultMap.put("unexamedItems", hisItemIdsSet.toArray(new String[] {}));
			return resultMap;
		}
}
