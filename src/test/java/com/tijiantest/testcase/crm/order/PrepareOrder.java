package com.tijiantest.testcase.crm.order;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.tijiantest.model.order.MealMultiChooseParam;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.message.BasicNameValuePair;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.alibaba.fastjson.JSON;
import com.jayway.jsonpath.JsonPath;
import com.tijiantest.base.HttpResult;
import com.tijiantest.base.dbcheck.ExamReportChecker;
import com.tijiantest.base.dbcheck.ResourceChecker;
import com.tijiantest.model.item.ExamItem;
import com.tijiantest.model.item.ExamItemGenderEnum;
import com.tijiantest.model.item.ExamItemTag;
import com.tijiantest.model.resource.meal.EditMealBody;
import com.tijiantest.model.resource.meal.Meal;
import com.tijiantest.model.resource.meal.MealChangeRecord;
import com.tijiantest.model.resource.meal.MealItem;
import com.tijiantest.model.resource.meal.MealOperationEnum;
import com.tijiantest.model.resource.meal.MealSetting;
import com.tijiantest.testcase.crm.CrmMediaBase;
import com.tijiantest.util.CvsFileUtil;
import com.tijiantest.util.PinYinUtil;
import com.tijiantest.util.db.DBMapper;
import com.tijiantest.util.db.SqlException;

public class PrepareOrder extends CrmMediaBase {
	public int newMealId;
	public String deleteMeal;
	public static int notSyncPriceItemId;
	public static List<ExamItem> notSyncPriceItems = new ArrayList<ExamItem>();
	public static ExamItem notSyncPriceItem;
	public static List<Meal> mealList = new ArrayList<Meal>();
	public static List<Meal> mealsContainNotSyncPrice = new ArrayList<Meal>();
	public List<ExamItem> itemList = new ArrayList<ExamItem>();
	public static List<ExamItem> items = new ArrayList<ExamItem>();
	public static List<Integer> itemIdList = new ArrayList<Integer>();
	public ExamItem examitem = new ExamItem();
	public List<NameValuePair> paramsA = new ArrayList<NameValuePair>();
	public static int itemId;
	 @Test(priority=0,description="新增单项",groups={"qa","crm_addItemTest"},dataProvider="addItems")
	 public void test_01_addItem(String... args) {
		
		 System.out.println("----------------------------新增单项TestStart-----------------------------");
		  int CRMSpecies = Integer.parseInt(args[1]);
		  boolean discount = Boolean.parseBoolean(args[2]);
		  boolean focus = Boolean.parseBoolean(args[3]);
		  int gender = Integer.parseInt(args[4]);
		  String hisItemId = UUID.randomUUID().toString().replace("-", "").substring(0, 6);
		  String description = args[5];
		  int id = 0;
		  String name = args[6]+getRandomHan();
		  int price = Integer.parseInt(args[7]);
		  boolean show = Boolean.parseBoolean(args[8]);
		  boolean syncPrice = Boolean.parseBoolean(args[9]);
		  String tagName = args[10];
		  Boolean showWarning = Boolean.parseBoolean(args[11]);
		  String warning = args[12];
		  String detail = args[13];
		  String fitPeople = args[14];
		  String unFitPeople = args[15]; 
		  Boolean bottlseNeck = Boolean.parseBoolean(args[16]);
		  
		  examitem.setItemType(1);
		  examitem.setDescription(description);
		  examitem.setDetail(detail);
		  examitem.setDiscount(discount);
		  examitem.setFitPeople(fitPeople);
		  examitem.setUnfitPeople(unFitPeople);
		  examitem.setFocus(focus);
		  examitem.setGender(gender);
		  examitem.setHisItemId(hisItemId);
		  examitem.setHospitalId(defhospital.getId());
		  examitem.setId(id);
		  examitem.setName(name);
		  examitem.setPinyin(PinYinUtil.getFirstSpell(name));
		  examitem.setPrice(price);
		  examitem.setShow(show);
		  examitem.setSyncPrice(syncPrice);
		  examitem.setTagName(tagName);
		  examitem.setShowWarning(showWarning);
		  examitem.setWarning(warning);
		  examitem.setBottleneck(bottlseNeck);
		  String jbody = JSON.toJSONString(examitem);
		  
		  NameValuePair hospitalId = new BasicNameValuePair("hospitalId",String.valueOf(defhospital.getId()));
		  NameValuePair speciesId = new BasicNameValuePair("speciesId",String.valueOf(CRMSpecies));
		  
		  paramsA.add(speciesId);
		  paramsA.add(hospitalId);	  
		  
		  HttpResult result = httpclient.post(Item_EditExamItem,paramsA,jbody);
		  
		  // Assert
		  Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);
		  
		  itemId = JsonPath.read(result.getBody(), "$.id");
		  String isSyncPrice = JsonPath.read(result.getBody(), "$.syncPrice").toString();
		  
		  if(checkdb){
			  ExamItem ei = ResourceChecker.checkExamItem(itemId);
			  examitem.setId(ei.getId());
			  examitem.setSequence(ei.getSequence());
			  itemIdList.add(ei.getId());
			  items.add(ei);
			  Assert.assertEquals(ei.isSyncPrice(), Boolean.parseBoolean(isSyncPrice));
			  Assert.assertEquals(ei.getName(), name);
			  Assert.assertEquals(ei.isShow(), show);
			  Assert.assertEquals(ei.isBottleneck(), bottlseNeck.booleanValue());
			  Assert.assertEquals(ei.getDescription(), description);
			  Assert.assertEquals(ei.getDetail(), detail);
			  Assert.assertEquals(ei.getFitPeople(), fitPeople);
			  Assert.assertEquals(ei.getHisItemId(), hisItemId);
			  Assert.assertEquals(ei.getPinyin(), PinYinUtil.getFirstSpell(name));
			  Assert.assertEquals(ei.getUnfitPeople(), unFitPeople);
			  Assert.assertEquals(ei.getWarning(), warning);
			  Assert.assertEquals(ei.getGender().intValue(), gender);
			  Assert.assertEquals(ei.getPrice().intValue(),price);
			  Assert.assertEquals(ei.getShowWarning(),showWarning);
			  
			  if(tagName!=null){
				  ExamItemTag eit = selectTagByName(ei.getHospitalId(),tagName);
				  Assert.assertNotNull(eit);
			  }		  
		  } 
		  
	}
	 
	 @Test(dataProvider = "addMeal", groups = { "qa" , "crm_addMCNSTest"}, dependsOnMethods = "test_01_addItem")
		public void test_02_addMealContainNotSnycPrice(String... args) throws ParseException, IOException {
			System.out.println("-------------新增含不同步价格单项的套餐测试开始-------------");
			int companyid = defSKXCnewcompany.getId();
			int basicMealId = ResourceChecker.getBasicMealId(defhospital.getId());
			
			Meal meal = ResourceChecker.getMealInfo(basicMealId);
			meal.setName("MCNS套餐");
			meal.setId(null);
			List<MealItem> mealItemList = ResourceChecker.getMealIteminfo(basicMealId);

			
			//新加项目
			itemList = items;
			boolean basic = Boolean.parseBoolean(args[2]);
			boolean enableSelect = Boolean.parseBoolean(args[3]);
			boolean selected = Boolean.parseBoolean(args[4]);
			boolean show = Boolean.parseBoolean(args[5]);
			for(ExamItem ei : itemList){
				if(ei.getGender() == ExamItemGenderEnum.GENERAL.getCode() || ei.getGender() == meal.getGender()){
					if(!ei.isSyncPrice()){
						int sequence = mealItemList.size()+1;
						mealItemList.add(new MealItem(
								ei.getId(),
								basicMealId,
								basic,
								enableSelect,
								ei.getGender(),
								selected,
								sequence,
								show
								));
						notSyncPriceItemId = ei.getId();
						notSyncPriceItem = ResourceChecker.checkExamItem(ei.getId());
						notSyncPriceItems.add(notSyncPriceItem);
					}	
				}
				
			}//新加项目
			//meal.setGender(MealGenderEnum.GENERAL.getCode());
			List<MealSetting> mealSetting = ResourceChecker.getMealSettingsInfo(basicMealId);
			meal.setMealSetting(mealSetting.get(0));
			int ruleId = ExamReportChecker.getHospitalRuleId(defhospital.getId());
			EditMealBody mealBody = new EditMealBody(companyid, meal, mealItemList,ruleId);
			String jbody = JSON.toJSONString(mealBody);	

			// post
			HttpResult response = httpclient.post(Meal, jbody);
			
			// Assert
			Assert.assertEquals(response.getCode(), HttpStatus.SC_OK);
			System.out.println("准备订单套餐..."+response.getBody()+"返回码"+response.getCode());
			int newMlId = JsonPath.read(response.getBody(), "$.id");
			mealsContainNotSyncPrice.add(ResourceChecker.getMealInfo(newMlId));
			
			// database
			if (checkdb) {
				Meal mealList = ResourceChecker.getMealInfo(newMlId);
				Assert.assertNotNull(mealList);
				List<MealChangeRecord> records = ResourceChecker.getMealChangeRecord(newMlId);
				Assert.assertEquals(records.size(),1);
				Assert.assertEquals(records.get(0).getOperation(), MealOperationEnum.CREATE.value());
			}
			System.out.println("-------------新增含不同步价格单项的套餐测试结束-------------");
		}
	
	public ExamItemTag selectTagByName(int hospitalId,String name){
		ExamItemTag eit = new ExamItemTag();
		name = "\""+name+"\"";
		String sql = "SELECT * FROM tb_examitem_tag WHERE hospital_id = "+hospitalId+" AND name = "+name+" ";
		try {
			List<Map<String,Object>> list = DBMapper.query(sql);
			if(list.size()==1){
				for(Map<String,Object> m : list){
					eit.setId(Integer.valueOf(m.get("id").toString()));
					eit.setHospitalId(Integer.valueOf(m.get("hospital_id").toString()));
					eit.setName(m.get("name").toString());
				}
			}
			else
				return null;
		} catch (SqlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return eit;
	}
	
	@DataProvider
	public Iterator<String[]> addMeal() {
		try {
			return CvsFileUtil.fromCvsFileToIterator("./csv/resource/addMeal.csv", 6);
		} catch (FileNotFoundException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		return null;
	}
	
	  @DataProvider(name = "addItems")
		public Iterator<String[]> addLimitItems() {
			try {
				return CvsFileUtil.fromCvsFileToIterator("./csv/item/addItems.csv", 17);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			return null;
		}
}
