package com.tijiantest.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mongodb.BasicDBObject;
import com.tijiantest.base.ConfDefine;
import com.tijiantest.base.dbcheck.OrderChecker;
import com.tijiantest.model.order.Order;
import com.tijiantest.model.order.snapshot.MealSnapshot;
import com.tijiantest.model.resource.meal.Meal;
import com.tijiantest.util.db.MongoDBUtils;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

public class Test1 {
	
	@Test(description = "test",groups={"test"},dependsOnGroups="qa")
	public void test_01(){
		System.out.println("I am test group");
	}
	@Test(description = "qa1")
	public void test_02(){
		System.out.println("I am qa group");
	}


	@Test
	public void test_03(){
//		String orderNum = "20180702170621788009632";
//		List<Map<String,Object>> mogoMealList = MongoDBUtils.queryByPage("{'domain':'order','domainId':'"+orderNum+"','bizType':'mealSnapshot'}","createdTime",-1,0,1, ConfDefine.MONGOMEAL_COLLECTION);
//		System.out.println("size"+mogoMealList.size());
//		for(int i=0;i<mogoMealList.size();i++){
//			System.out.println("bizType.."+mogoMealList.get(i).get("bizType"));
//			System.out.println("bizId.."+mogoMealList.get(i).get("bizId"));
//			System.out.println("createdTime.."+mogoMealList.get(i).get("createdTime"));
//			System.out.println("value.."+mogoMealList.get(i).get("value"));
//
//
//		}
		String orderNum = "20180702170621788009632";
		Order order = OrderChecker.getOrderInfo(orderNum);
		System.out.println(order.getMealDetail());
		MealSnapshot mss = JSONObject.parseObject(order.getMealDetail(),MealSnapshot.class);
		System.out.println(JSON.toJSONString(mss));

	}

	@Test
	public  void test_04(){
		String orderNum = "20180702170621788009632";
		net.sf.json.JSONObject mealSnapshotInMongoMeal  = OrderChecker.getMongoMealSnapshot(orderNum,"mealSnapshot");
		System.out.println("mealS"+mealSnapshotInMongoMeal.get("value")+"...\n"+mealSnapshotInMongoMeal.get("bizId"));

		net.sf.json.JSONObject examItemInMongoMeal  = OrderChecker.getMongoMealSnapshot(orderNum,"examItemSnapshot");
		System.out.println("examItemSnapshot"+examItemInMongoMeal.get("value"));

		net.sf.json.JSONObject examItemPackageSnapshot  = OrderChecker.getMongoMealSnapshot(orderNum,"examItemPackageSnapshot");
		System.out.println("examItemPackageSnapshot"+examItemPackageSnapshot.get("value"));
	}
}
