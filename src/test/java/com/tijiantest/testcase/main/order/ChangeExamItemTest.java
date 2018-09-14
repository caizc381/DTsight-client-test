package com.tijiantest.testcase.main.order;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.jayway.jsonpath.JsonPath;
import com.tijiantest.base.Flag;
import com.tijiantest.base.HttpResult;
import com.tijiantest.base.dbcheck.HospitalChecker;
import com.tijiantest.base.dbcheck.OrderChecker;
import com.tijiantest.base.dbcheck.ResourceChecker;
import com.tijiantest.model.examitempackage.ExamItemPackage;
import com.tijiantest.model.order.ExamOrderOperateLogDO;
import com.tijiantest.model.order.OperateAppEnum;
import com.tijiantest.model.order.Order;
import com.tijiantest.model.order.OrderOperateTypeEnum;
import com.tijiantest.model.resource.meal.Meal;
import com.tijiantest.model.resource.meal.MealItem;
import com.tijiantest.util.CvsFileUtil;
import com.tijiantest.util.db.MongoDBUtils;
import com.tijiantest.util.db.SqlException;
/**
 * 改项
 * @author huifang
 *
 */
///@Test(groups = {"qa","changeExamItem"})
public class ChangeExamItemTest extends OrderBaseTest{
  
  private Meal useMeal = null; //改项基于的套餐
  private int addPrice = 0; //增加单项/单项包的价格
  
  @Test(description = "改项+增加单项/单项包",dataProvider = "changeExamItem",dependsOnGroups={"main_mainGetOrder"})
  public void test_01_changeExamItem(String ...args) throws SqlException {
	  System.out.println("-----------------------改项Start----------------------------");
	  int orderId = BookTest.commOrderId;
	  Order order = OrderChecker.getOrderInfo(orderId);
	  int mealId = order.getOrderMealSnapshot().getMealSnapshot().getOriginMeal().getId();
	  String addItem = args[1];
	  String addPack = args[2];
	  
	  useMeal = ResourceChecker.getMealInfo(mealId);
	  List<NameValuePair> params = new ArrayList<NameValuePair>();
	  params.add(new BasicNameValuePair("orderId",orderId+""));
	  params.add(new BasicNameValuePair("_p", ""));
	  params.add(new BasicNameValuePair("_site", ""));
	  params.add(new BasicNameValuePair("_siteType", "mobile"));
	  List<MealItem> mealitem = ResourceChecker.getMealInnerItemList(mealId);
		List<Integer> item_list = new ArrayList<Integer>();
	    for(int i=0;i<mealitem.size();i++){
			item_list.add(i, mealitem.get(i).getId());
			NameValuePair itemIds = new BasicNameValuePair("itemIds[]", item_list.get(i)+"");
			params.add(itemIds);
	    }
	  if(!addItem.equals("NULL")){
		  params.add(new BasicNameValuePair("itemIds[]",Integer.parseInt(addItem)+""));
		  addPrice = ResourceChecker.checkExamItem(Integer.parseInt(addItem)).getPrice();
	  } 
	  if(!addPack.equals("NULL")){
		  List<ExamItemPackage> packageList = ResourceChecker.getAvaiablePackages(defHospitalId,ResourceChecker.getMealInfo(mealId).getGender());
		  int packId = packageList.get(1).getId();
		  params.add(new BasicNameValuePair("packageIds[]", packId+""));
		  ExamItemPackage onePack = ResourceChecker.getPackageInfo(packId);
		  /*for(ExamItem ei : onePack.getItemList()){
		    	 params.add(new BasicNameValuePair("packageItemIds[]", ei.getId()+""));
		    }*/
		  addPrice = onePack.getPrice();
		  //addPrice = packageList.get(1).getPrice();
		  
	  }
	  log.info("订单...."+orderId+"...");
	  HttpResult response = httpclient.post(Flag.MAIN,Order_ChangeExamItem, params);
	  //Assert
	  Assert.assertEquals(response.getCode(),HttpStatus.SC_OK);
	  String body = response.getBody();
	  log.info("changeExamItem......"+body);
	  log.info("userMeal..."+useMeal.getPrice()+"....id..."+useMeal.getId());
	  Order ret_order = JSON.parseObject(JsonPath.read(body, "$.order").toString(), new TypeReference<Order>() {});
	  System.err.println("useMeal.getPrice():>>>>>>>>>>>>>"+useMeal.getPrice());
	  System.err.println("addPrice:>>>>>>>>>>>>>"+addPrice);
	  System.err.println("useMeal.getDiscount():>>>>>>>>>>>>>"+useMeal.getDiscount());
	  if(checkdb){
		  Order o = OrderChecker.getOrderInfo(orderId);
		  Assert.assertEquals(o.getOrderPrice().intValue(),
				 HospitalChecker.calculator_data(defHospitalId, useMeal.getPrice())  + HospitalChecker.calculator_data(defHospitalId, (int)Math.round(addPrice*useMeal.getDiscount())));
		  Assert.assertEquals(ret_order.getOrderPrice(),o.getOrderPrice());
		  Assert.assertEquals(ret_order.getStatus(),o.getStatus());
		  Assert.assertEquals(ret_order.getDiscount(),o.getDiscount());
		  Assert.assertEquals(ret_order.getExamDate(),o.getExamDate());
		  Assert.assertEquals(ret_order.getIsExport(),o.getIsExport());
		  Assert.assertEquals(ret_order.getOrderNum(),o.getOrderNum());
		  Assert.assertEquals(ret_order.getBatchId(),o.getBatchId());
		  
		  if(checkdb){
				//验证订单操作日志
			  System.err.println("---------------验证操作日志--------------");
				  List<ExamOrderOperateLogDO> logs = OrderChecker.getOrderOperatrLog(o.getOrderNum());
			   	  Assert.assertEquals(logs.get(0).getType().intValue(), OrderOperateTypeEnum.CHANGE_ITEM.getCode());
			   	  Assert.assertEquals(logs.get(0).getSystem().intValue(), OperateAppEnum.CLIENT.getCode());
		  }
		  
	      if(checkmongo){
	    	    waitto(mongoWaitTime);
	    	    log.info("orderId:"+o.getId());
				List<Map<String,Object>> monlist = MongoDBUtils.query("{'id':"+o.getId()+"}", MONGO_COLLECTION);
				Assert.assertNotNull(monlist);
				Assert.assertEquals(Integer.parseInt(monlist.get(0).get("orderPrice").toString()),
						HospitalChecker.calculator_data(defHospitalId, useMeal.getPrice()) +HospitalChecker.calculator_data(defHospitalId, (int)Math.round(addPrice*useMeal.getDiscount()))); 
				Assert.assertEquals(Integer.parseInt(monlist.get(0).get("status").toString()),ret_order.getStatus());
				Assert.assertEquals(monlist.get(0).get("isExport").toString(),ret_order.getIsExport().toString());
				Assert.assertEquals(monlist.get(0).get("orderNum").toString(),ret_order.getOrderNum());
			}	
	      //校验结算
		 OrderChecker.check_Book_ExamOrderSettlement(o);
	  }
	  System.out.println("-----------------------改项End----------------------------");
  }

   @DataProvider
	public Iterator<String[]> changeExamItem(){
		try {
			return CvsFileUtil.fromCvsFileToIterator("./csv/order/changeExamItem.csv",3);
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
