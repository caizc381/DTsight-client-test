package com.tijiantest.testcase.channel.order;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.jayway.jsonpath.JsonPath;
import com.tijiantest.base.Flag;
import com.tijiantest.base.HttpResult;
import com.tijiantest.model.item.ExamItem;
import com.tijiantest.model.order.channel.MongoOrderVO;
import com.tijiantest.model.order.channel.OrderDetailsVO;
import com.tijiantest.model.order.OrderQueryParams;
import com.tijiantest.model.resource.meal.ExamItemSnap;

public class GetOrderDetailsTest extends OrderDetailBase{
	
	public List<MongoOrderVO> orders = new ArrayList<>();
	@SuppressWarnings("unchecked")
	@Test(description = "查看订单详情", groups = { "qa" },dependsOnGroups={"channel_listOrder"})
  public void test_getOrderDetails() {
		System.out.println("---------------------------------查询订单详情Start----------------------------------------");
		orders = ListChannelMongoOrdersTest.listOrder;
		//Integer orderId = 4105130;
		if(orders.size()>0 && !orders.equals("[]")){
			for(MongoOrderVO orderVo : orders){			
				Integer orderId = orderVo.getId();
				Map<String,Object> params = new HashMap<String,Object>();
				params.put("orderId", orderId);
				HttpResult result = httpclient.get(Flag.CHANNEL, Order_GetOrderDetails, params);
				Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);
				
				OrderDetailsVO orderDetailsVO = new OrderDetailsVO();
				Map<Integer, ExamItemSnap> itemSnap = getExamItemsDetail(orderId);
				
				OrderQueryParams orderQueryParams = new OrderQueryParams();
				orderQueryParams.setOrderIds(Collections.singletonList(orderId));
				orderDetailsVO.setItemSnapMap(itemSnap);
				//退单相关
				Map<String, Object> refundMap = countOrderRefund(orderId);
				if (!refundMap.equals(null) && refundMap.size() > 0) {
					orderDetailsVO.setRefusedItems(refundMap.get("refusedItemsDetail") == null ? null : (List<ExamItem>) refundMap.get("refusedItemsDetail"));
					orderDetailsVO.setUnExamItems(refundMap.get("unexamedItemsDetail") == null ? null : (List<ExamItem>) refundMap.get("unexamedItemsDetail"));
				}
				OrderDetailsVO vo = JSON.parseObject(result.getBody(), OrderDetailsVO.class);
				
				//验证套餐内/外项目信息
				System.out.println("********验证套餐内/外项目********");
				String itemSnapMap = JsonPath.read(result.getBody(),"$.itemSnapMap").toString();
				Map<Integer, ExamItemSnap> responseItemSnap = JSON.parseObject(itemSnapMap, new TypeReference<Map<Integer, ExamItemSnap>>() {
				});      
				Assert.assertEquals(responseItemSnap.keySet().size(), orderDetailsVO.getItemSnapMap().keySet().size());        
				for(Integer key : responseItemSnap.keySet()){
					Assert.assertEquals(responseItemSnap.get(key).getId(), orderDetailsVO.getItemSnapMap().get(key).getId());
				}
				
				//验证拒检项目信息
				if(vo.getRefusedItems()!=null){
					System.out.println("********验证拒检项目********");
					String refusedItems = JsonPath.read(result.getBody(),"$.refusedItems[*]").toString();
					List<ExamItem> refusedItemsDetail = JSON.parseObject(refusedItems, new TypeReference<List<ExamItem>>() {        	
					});
					Assert.assertEquals(orderDetailsVO.getRefusedItems().size(), refusedItemsDetail.size());
					for(int i=0;i<refusedItemsDetail.size();i++){
						Assert.assertEquals(refusedItemsDetail.get(i).getId(), orderDetailsVO.getRefusedItems().get(i).getId());
					}
				}
				
				//验证未检项目信息
				if(vo.getUnExamItems()!=null){        	
					System.out.println("********验证未检项目********");
					String unexamedItems = JsonPath.read(result.getBody(),"$.unExamItems[*]").toString();        	
					List<ExamItem> unexamedItemsDetail = JSON.parseObject(unexamedItems, new TypeReference<List<ExamItem>>() {
					});
					Assert.assertEquals(orderDetailsVO.getUnExamItems().size(), unexamedItemsDetail.size());
					for(int i=0;i<unexamedItemsDetail.size();i++){
						Assert.assertEquals(unexamedItemsDetail.get(i).getId(), orderDetailsVO.getUnExamItems().get(i).getId());
					}
				}
				
				System.out.println("---------------------------------查询订单详情End----------------------------------------");
			}
			
		}
		else
			System.out.println("无订单可以查询");
        
  }
  
}
