package com.tijiantest.testcase.main.order;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.alibaba.fastjson.TypeReference;
import com.tijiantest.base.dbcheck.AccountChecker;
import com.tijiantest.model.account.User;
import com.tijiantest.model.examitempackage.ExamItemPackage;
import com.tijiantest.model.order.snapshot.ExamItemSnapshot;
import org.apache.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.alibaba.fastjson.JSON;
import com.jayway.jsonpath.JsonPath;
import com.tijiantest.base.Flag;
import com.tijiantest.base.HttpResult;
import com.tijiantest.base.dbcheck.OrderChecker;
import com.tijiantest.base.dbcheck.ResourceChecker;
import com.tijiantest.model.examitempackage.ExamItemPackageVO;
import com.tijiantest.model.item.ExamItem;
import com.tijiantest.model.order.Order;
import com.tijiantest.testcase.main.MainBase;
import com.tijiantest.util.CvsFileUtil;
import com.tijiantest.util.db.MongoDBUtils;
import com.tijiantest.util.db.SqlException;

//@Test(groups = {"qa","mainGetOrder"},dependsOnGroups = {"mainBook","mainBookPack"})
public class GetOrderTest extends MainBase {

  @Test(dataProvider = "getOrder",description = "查看不带单项包的C端订单",groups={"qa","main_mainGetOrder"},dependsOnGroups = {"main_mainBook"})
  public void test_01_getOrder(String ...args) {
	  int orderId = BookTest.commOrderId;
	  boolean firstItems = Boolean.parseBoolean(args[2]);
	  
	  Map<String,Object> params = new HashMap<String, Object>();
	  params.put("firstItems", firstItems);
	  params.put("orderId", orderId);
	  params.put("_p", "");
	  params.put("_site", "");
	  
	  HttpResult response = httpclient.get(Flag.MAIN,Order_GetOrder, params);
	  //Assert
	  Assert.assertEquals(response.getCode(),HttpStatus.SC_OK);
	  String body = response.getBody();
	  Assert.assertFalse(body.contains("responseStatusCode"),"接口返回.."+body);
	  System.out.println("test_01_getOrder_getOrder返回..."+body);
	  Order order = JSON.parseObject(JsonPath.read(body,"$.order").toString(), Order.class);
	  Assert.assertEquals(JsonPath.read(body, "$.packageIds").toString(),"[]");
	  Assert.assertEquals(JsonPath.read(body, "$.packageItemIds").toString(),"[]");
	  if(checkdb){
		  Order o = OrderChecker.getOrderInfo(orderId);
		  Assert.assertEquals(order.getOrderNum(),o.getOrderNum());
		  Assert.assertEquals(order.getStatus(),o.getStatus());
		  Assert.assertEquals(order.getExamDate(),o.getExamDate());
		  Assert.assertEquals(order.getIsExport(),o.getIsExport());
		  if(checkmongo){
			  List<Map<String,Object>> monlist = MongoDBUtils.query("{'id':"+order.getId()+"}", MONGO_COLLECTION);
			  Assert.assertNotNull(monlist);
			  Assert.assertEquals(Integer.parseInt(monlist.get(0).get("status").toString()),o.getStatus());
		  }
	  }
  }

    @SuppressWarnings("deprecation")
	@Test(dataProvider = "getOrder",description = "查看带单项包的C端订单",groups={"qa","main_mainGetOrder"},dependsOnGroups = {"main_mainBookPack"})
	public void test_02_getOrder_with_packages(String ...args) throws SqlException {
		int orderId = BookTest.havePackOrderId;
		boolean firstItems = Boolean.parseBoolean(args[2]);

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("firstItems", firstItems);
		params.put("orderId", orderId);
		params.put("_p", "");
		params.put("_site", "");

		HttpResult response = httpclient.get(Flag.MAIN, Order_GetOrder, params);
		//Assert
//		  log.info("order_getOrder2...."+response.getBody());
		Assert.assertEquals(response.getCode(), HttpStatus.SC_OK);
		String body = response.getBody();
		System.out.println("test_02_getOrder_with_packages返回..." + body);
		Assert.assertFalse(body.contains("responseStatusCode"), "test_02_getOrder_with_packages返回.." + body);

		Order order = JSON.parseObject(JsonPath.read(body, "$.order").toString(), Order.class);
		Assert.assertNotNull(JsonPath.read(body, "$.packageIds"));
		List<Integer> packIds = JsonPath.read(body, "$.packageIds");
		List<Integer> packItemIds = JsonPath.read(body, "$.packageItemIds");
		Collections.sort(packItemIds);
		if (checkdb) {
			Order o = OrderChecker.getOrderInfo(orderId);
			Assert.assertEquals(order.getOrderNum(), o.getOrderNum());
			Assert.assertEquals(order.getStatus(), o.getStatus());
			Assert.assertEquals(order.getExamDate(), o.getExamDate());
			Assert.assertEquals(order.getIsExport(), o.getIsExport());
			List<ExamItemSnapshot> finalItemList = order.getOrderMealSnapshot().getExamItemSnapList();
			List<ExamItemPackage> dbpacks = order.getOrderMealSnapshot().getExamItemPackageSnapshot().getPackages();
			Assert.assertEquals(packIds.size(), dbpacks.size());
			List<Integer> dbPackItems = new ArrayList<Integer>();
			for (int i = 0; i < dbpacks.size(); i++) {
				log.debug("pack...." + packIds.get(i));
				Assert.assertEquals(packIds.get(i).intValue(), dbpacks.get(i).getId().intValue());
				List<ExamItem> itemList = ResourceChecker.getPackageInfo(dbpacks.get(i).getId()).getItemList();
				for (ExamItem e : itemList) {
					List<ExamItemSnapshot> dupItems = new ArrayList<ExamItemSnapshot>();
					//去除重复项目
					dupItems = finalItemList.stream().filter(item -> item.getId() == e.getId() && item.getTypeToMeal() == 1).collect(Collectors.toList());
					if (!dupItems.isEmpty()) //过滤重复项目,不在单项包快照中显示
						continue;
						dbPackItems.add(e.getId());
				}
				Collections.sort(dbPackItems);
				Assert.assertEquals(packItemIds, dbPackItems); //比较单项数量一致
				if (checkmongo) {
					List<Map<String, Object>> monlist = MongoDBUtils.query("{'id':" + order.getId() + "}", MONGO_COLLECTION);
					Assert.assertNotNull(monlist);
					Assert.assertEquals(Integer.parseInt(monlist.get(0).get("status").toString()), o.getStatus());
				}
			}
		}
	}
		    
	@SuppressWarnings("deprecation")
	@Test(dataProvider = "getOrder",description = "C端查看CRM订单",groups={"qa","main_mainGetOrder"},dependsOnGroups = {"main_mainUseCrmOrder"})
	public void test_03_getOrderFromCRM(String ...args) throws SqlException{
    	System.out.println("--------------------------C端查看CRM订单Start-------------------------");
    	  Order crmOrder = PrepareMainOrderTest.crmOrder;;
		  int orderId = crmOrder.getId();
		  User mainUser = AccountChecker.getUser(crmOrder.getOrderAccount().getId()).get(0);
		  onceLoginInSystem(httpclient, Flag.MAIN, mainUser.getUsername(), "111111");
		  boolean firstItems = Boolean.parseBoolean(args[2]);

		  Map<String,Object> params = new HashMap<String, Object>();
		  params.put("firstItems", firstItems);
		  params.put("orderId", orderId);
		  params.put("_p", "");
		  params.put("_site", "");

		  HttpResult response = httpclient.get(Flag.MAIN,Order_GetOrder, params);
		  //Assert
		  log.info("CRM订单,点击[改项目]返回值为...."+response.getBody());
		  Assert.assertEquals(response.getCode(),HttpStatus.SC_OK);
		  String body = response.getBody();
		 Assert.assertFalse(body.contains("responseStatusCode"),"test_03_getOrderFromCRM返回"+body);
		  Order order = JSON.parseObject(JsonPath.read(body,"$.order").toString(), Order.class);
		  Assert.assertNotNull(JsonPath.read(body, "$.packageIds"));
		  List<Integer> packIds = JsonPath.read(body, "$.packageIds");
		  List<Integer> packItemIds = JsonPath.read(body, "$.packageItemIds");
		  Collections.sort(packItemIds);
		  if(checkdb){
			  Order o = OrderChecker.getOrderInfo(orderId);
			  Assert.assertEquals(order.getOrderNum(),o.getOrderNum());
			  Assert.assertEquals(order.getStatus(),o.getStatus());
			  Assert.assertEquals(order.getExamDate(),o.getExamDate());
			  Assert.assertEquals(order.getIsExport(),o.getIsExport());
			  List<ExamItemSnapshot> finalItemList = order.getOrderMealSnapshot().getExamItemSnapList();
//			  String package_snapshot_detail = order.getPackageSnapshotDetail();
//			  Assert.assertEquals(order.getPackageSnapshotDetail(),o.getPackageSnapshotDetail());
//		      List<ExamItemPackageVO> dbpacks = JSON.parseArray(JsonPath.read(package_snapshot_detail,"$.packages").toString(),ExamItemPackageVO.class);
              List<ExamItemPackage> dbpacks = order.getOrderMealSnapshot().getExamItemPackageSnapshot().getPackages();
              Assert.assertEquals(packIds.size(), dbpacks.size());
		      List<Integer> dbPackItems = new ArrayList<Integer>();
			  for(int i=0;i<dbpacks.size();i++){
				  log.debug("pack...."+packIds.get(i));
				  Assert.assertEquals(packIds.get(i).intValue(),dbpacks.get(i).getId().intValue());
				  List<ExamItem> itemList = ResourceChecker.getPackageInfo(dbpacks.get(i).getId()).getItemList();
				  for(ExamItem e : itemList){
					  List<ExamItemSnapshot> dupItems = new ArrayList<ExamItemSnapshot>();
					  //去除重复项目
					  dupItems = finalItemList.stream().filter(item -> item.getId() == e.getId() && item.getTypeToMeal() == 1).collect(Collectors.toList());
					  if (!dupItems.isEmpty())//过滤重复项目,不在单项包快照中显示
						  continue;
					  dbPackItems.add(e.getId());
			  }
			  Collections.sort(dbPackItems);
			  Assert.assertEquals(packItemIds,dbPackItems); //比较单项数量一致
			  if(checkmongo){
				  List<Map<String,Object>> monlist = MongoDBUtils.query("{'id':"+order.getId()+"}", MONGO_COLLECTION);
				  Assert.assertNotNull(monlist);
				  Assert.assertEquals(Integer.parseInt(monlist.get(0).get("status").toString()),o.getStatus());
			  }
		  }
		  System.out.println("--------------------------C端查看CRM订单End-------------------------");
		  onceLogOutSystem(httpclient, Flag.MAIN);
		  onceLoginInSystem(httpclient, Flag.MAIN, defMainUsername, defMainPasswd);
		  }
	  }

	
	@DataProvider
	public Iterator<String[]> getOrder(){
		try {
			return CvsFileUtil.fromCvsFileToIterator("./csv/order/mainGetOrder.csv",3);
		} catch (FileNotFoundException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		return null;
	}
}
