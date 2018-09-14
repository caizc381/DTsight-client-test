package com.tijiantest.testcase.main.order;

import java.awt.print.Book;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

import com.tijiantest.base.BaseTest;
import com.tijiantest.base.MyHttpClient;
import com.tijiantest.base.dbcheck.*;
import com.tijiantest.model.account.SystemTypeEnum;
import com.tijiantest.model.hospital.HospitalParam;
import com.tijiantest.model.order.*;
import com.tijiantest.util.DateUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.alibaba.fastjson.JSON;
import com.jayway.jsonpath.JsonPath;
import com.tijiantest.base.Flag;
import com.tijiantest.base.HttpResult;
import com.tijiantest.model.card.Card;
import com.tijiantest.model.company.HospitalCompany;
import com.tijiantest.model.counter.BizExceptionEnum;
import com.tijiantest.model.counter.CompanyCapacityUsed;
import com.tijiantest.model.counter.HospitalCapacityUsed;
import com.tijiantest.model.examitempackage.ExamItemInPackageInfo;
import com.tijiantest.model.examitempackage.ExamItemPackage;
import com.tijiantest.model.examitempackage.ExamItemPackageVO;
import com.tijiantest.model.hospital.Hospital;
import com.tijiantest.model.item.ExamItem;
import com.tijiantest.model.item.limitExamItemsVo;
import com.tijiantest.model.order.snapshot.ExamItemPackageSnapshot;
import com.tijiantest.model.order.snapshot.ExamItemSnapshot;
import com.tijiantest.model.order.snapshot.MealSnapshot;
import com.tijiantest.model.order.snapshot.OrderMealSnapshot;
import com.tijiantest.model.resource.meal.Meal;
import com.tijiantest.model.resource.meal.MealGenderEnum;
import com.tijiantest.model.resource.meal.MealItem;
import com.tijiantest.util.CvsFileUtil;
import com.tijiantest.util.db.DBMapper;
import com.tijiantest.util.db.MongoDBUtils;
import com.tijiantest.util.db.SqlException;
import sun.misc.Resource;

/**
 * 流程：先下单(test01 下普通订单,test02下有单项包的订单）
 * ->查询订单GetOrderTest/MobileOrderDetailsPageTest ->改项目ChangeExamItemTest update
 * by huifang
 *
 */
public class BookTest extends OrderBaseTest {
	public static int commOrderId = 0; // 不带单项包的订单
	public static int havePackOrderId = 0; // 带单项包的订单
	public static int comm_entryCard_OrderId = 0; // 不带单项包使用入口卡进入的订单
	public static int entryCardId;// 不带单项包使用入口卡进入的订单 - 入口卡ID
	public static int channelOrderId = 0;// 渠道商二级站点下单
	public static List<Integer> orderId = new ArrayList<Integer>();
	public static Meal bookMeal = ResourceChecker.getOffcialMeal(defHospitalId, Arrays.asList(MealGenderEnum.FEMALE.getCode()),true).get(0);//可改项目的
	public static int bookMealId = bookMeal.getId();
	
	@Test(dataProvider = "book", groups = { "qa", "main_mainBook" })
	public void test_01_book(String... args) throws SqlException, ParseException {
		System.out.println("-----------------------01_C端下单(不带加项包的订单)Start----------------------------");
		String exam_date = args[1];
		HospitalCompany hc = CompanyChecker.getHospitalCompanyByPlatCompanyIdANDOrganizationId(1,defHospitalId);
		Map<String,Object> dateMap = CounterChecker.getDateBookableFromStartDate(sdf.parse(exam_date),sdf.parse(exam_date),hc.getId(), defHospitalId);
		int dayRangeId = Integer.parseInt(dateMap.get("dayRangeId").toString());
		String examTime_interval_id = dayRangeId+"";
		Integer addItemId = Integer.valueOf(args[6]);
		int account_id = defaccountId;
		ItemsInOrder itemsInOrder = new ItemsInOrder();
		itemsInOrder = OrderChecker.generateItemsInOrder(bookMealId, addItemId);

		Boolean needpaperreport = Boolean.parseBoolean(args[5]);
		//1.query form
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		NameValuePair _p = new BasicNameValuePair("_p", "" + "");
		String site = "mtjk";
		int fromSite = HospitalChecker.getHospitalIdBySite(site).getId();
		NameValuePair _site = new BasicNameValuePair("_site", site + "");
		NameValuePair _siteType = new BasicNameValuePair("_siteType", "mobile" + "");
		params.add(_p);
		params.add(_site);
		params.add(_siteType);
		//2.json Object
		BookParams bookParams = new BookParams();
		bookParams.setExamDate(exam_date);
		bookParams.setExamTimeIntervalId(Integer.parseInt(examTime_interval_id));
		bookParams.setAccountId(account_id);
		bookParams.setMealId(bookMealId);
		bookParams.setNeedPaperReport(needpaperreport);
		bookParams.setSource(2);


		int examinerId = 0;
		boolean is_self = false;
		if(checkdb){
			String sql = "select id,is_self from tb_examiner where organization_id = "+fromSite + " and customer_id = "+defaccountId +"  and is_delete = 0 order by update_time desc limit 1";
			log.info(sql);
			List<Map<String,Object>> dblist = DBMapper.query(sql);
			if(dblist != null && dblist.size() >0){
				examinerId = Integer.parseInt(dblist.get(0).get("id").toString());
				is_self =  Integer.parseInt(dblist.get(0).get("is_self").toString()) == 0?false:true;
			}
		}
		if(examinerId == 0 ){
			log.error("没有体检人，请先添加体检人");
			return;
		}
		bookParams.setExaminerId(examinerId);
		bookParams.setInformMe(is_self);

		// 获取单位/体检中心人数
		List<HospitalCapacityUsed> hospitalCounter = CounterChecker.getHospitalCount(defHospital.getId(), exam_date)
				.stream().filter(hcu -> hcu.getPeriodId().intValue() == Integer.valueOf(examTime_interval_id))
				.collect(Collectors.toList());

		List<Integer> finalItemIdList = ResourceChecker.getExamIdList(itemsInOrder.getFinalItems());
		bookParams.setItemIds(finalItemIdList);
		//最终单项之和
		long finalItemPrice = ResourceChecker.getExamListDiscountPrice(itemsInOrder.getFinalItems(),defHospitalId,bookMeal.getDiscount(),bookMeal.getMealSetting().getAdjustPrice());



		HttpResult response = httpclient.post(Flag.MAIN, MainOrder_Book, params,JSON.toJSONString(bookParams));
		log.info("bookTest...." + response.getBody());
		Assert.assertEquals(response.getCode(), HttpStatus.SC_OK);

		//检查下单返回值，异常处理
		HttpResult result = OrderChecker.checkMainOrderBookResponse(httpclient,response,params,JSON.toJSONString(bookParams),defaccountId,examinerId,defHospitalId);
		if(result == null) {
			log.error("下单时，人数不足，请注意！！！");
			return;
		}
		commOrderId = Integer.parseInt(JsonPath.read(result.getBody(),"$.orderId").toString());

		// Assert
		Assert.assertNotNull(commOrderId);
		orderId.add(commOrderId);
		log.info("[orderIds:]////" + orderId);
		if (checkdb) {
			Order order = OrderChecker.getOrderInfo(commOrderId);
			Assert.assertEquals(sdf.format(order.getExamDate()), exam_date);
			Assert.assertEquals(order.getNeedPaperReport().toString(),needpaperreport.toString());
			@SuppressWarnings("deprecation")
			String package_snapshot_detail = order.getPackageSnapshotDetail();
			log.info("orderId...." + order.getId() + "pack...." + package_snapshot_detail);

			List<HospitalCapacityUsed> hospitalCounter1 = CounterChecker.getHospitalCount(defHospital.getId(), exam_date)
					.stream().filter(hcu -> hcu.getPeriodId().intValue() == Integer.valueOf(examTime_interval_id))
					.collect(Collectors.toList());
			CounterChecker.reduceCounter(-1, null, null, hospitalCounter, hospitalCounter1, 1);// 验证新体检日人数扣除

//			Double mealItemPrice = 0.0;

			// 验证订单操作日志
			List<ExamOrderOperateLogDO> logs = OrderChecker.getOrderOperatrLog(order.getOrderNum());
			Assert.assertEquals(logs.size(), 1);
			Assert.assertEquals(logs.get(0).getType().intValue(), OrderOperateTypeEnum.BOOK_ORDER.getCode());
			Assert.assertEquals(logs.get(0).getOrderStatus().intValue(), OrderStatus.NOT_PAY.intValue());
			Assert.assertEquals(logs.get(0).getSystem().intValue(), OperateAppEnum.CLIENT.getCode());

			// 验证itemDetail、mealDetail
			OrderMealSnapshot orderMealSnapshot = new OrderMealSnapshot();
			orderMealSnapshot = OrderChecker.getMealSnapShotByOrder(commOrderId);
			if(orderMealSnapshot != null){
				// 验证itemDetail
				List<ExamItemSnapshot> itemSnapshot = orderMealSnapshot.getExamItemSnapList();
				// 根据项目ID排序
				itemSnapshot = itemSnapshot.stream().sorted(Comparator.comparing(ExamItemSnapshot::getId))
						.collect(Collectors.toList());
				itemsInOrder.setAllRelatedItems(itemsInOrder.getAllRelatedItems().stream()
						.sorted(Comparator.comparing(ExamItem::getId)).collect(Collectors.toList()));

				Assert.assertEquals(itemSnapshot.size(), itemsInOrder.getAllRelatedItems().size());

				// 验证数据库中单项信息正确(所有单项，最终套餐内项目，套餐内减项，套餐外加项，重复项，套餐)
				ResourceChecker.checkItemByTypeFromMysql(itemSnapshot, itemsInOrder, bookMeal);
			}


			// 验证mealDetail
			MealSnapshot mealSnapshot = orderMealSnapshot.getMealSnapshot();
			Assert.assertEquals(mealSnapshot.getOriginMeal().getClass(), bookMeal.getClass());

			if (checkmongo) {
				waitto(mongoWaitTime);
				List<Map<String, Object>> monlist = MongoDBUtils.query("{'id':" + order.getId() + "}",
						MONGO_COLLECTION);
				Assert.assertNotNull(monlist);
				Assert.assertEquals(sdf.format(monlist.get(0).get("examDate")), exam_date);
				net.sf.json.JSONObject examItemSnapshotInMongoMeal = OrderChecker.getMongoMealSnapshot(monlist.get(0).get("orderNum").toString(),"examItemSnapshot");
				String examItemSnapStr = examItemSnapshotInMongoMeal.getString("value");
				// 验证itemDetail
				JSONArray itemDetail_ja = JSONArray.fromObject(examItemSnapStr);
				sort(itemDetail_ja, "id", true);
				// 验证Mongo中单项信息正确
				ResourceChecker.checkItemByTypeFromMONGO(itemDetail_ja, itemsInOrder, bookMeal);

				// 验证mealDetail
				net.sf.json.JSONObject  mealSnapshotInMongoMeal = OrderChecker.getMongoMealSnapshot(monlist.get(0).get("orderNum").toString(),"mealSnapshot");
				JSONObject mealSnapshot_jo = JSONObject.fromObject(mealSnapshotInMongoMeal.getString("value"));
				JSONObject originMeal_js = JSONObject.fromObject(mealSnapshot_jo.get("originMeal").toString());
				Assert.assertEquals(originMeal_js.get("id"), bookMeal.getId());
				Assert.assertEquals(Integer.parseInt(originMeal_js.get("price").toString()), bookMeal.getPrice().intValue());
			}
			//校验结算
			OrderChecker.check_Book_ExamOrderSettlement(order);
		}
		
		// 校验支付页面
		PayChecker.checkPayPage(httpclient, commOrderId, -1,defSite,defaccountId);
		//校验C端订单详情
		OrderChecker.main_checkOrderDetails(httpclient,commOrderId,defSite,defaccountId);
		System.out.println("-----------------------01_C端下单(不带加项包的订单)End----------------------------");
	}
	
	@Test(dataProvider = "book_pack", groups = { "qa","main_mainBookPack" })
	public void test_02_book_pack(String... args) throws SqlException, ParseException {
		System.out.println("-----------------------02_C端下单(带加项包的订单)Start----------------------------");
		String exam_date = args[1];
		HospitalCompany hc = CompanyChecker.getHospitalCompanyByPlatCompanyIdANDOrganizationId(1,defHospitalId);
		Map<String,Object> dateMap = CounterChecker.getDateBookableFromStartDate(sdf.parse(exam_date),sdf.parse(exam_date),hc.getId(), defHospitalId);
		int dayRangeId = Integer.parseInt(dateMap.get("dayRangeId").toString());
		String examTime_interval_id = dayRangeId+"";

		// C端添加用户， 防止当日下单次数超过上限
		 int account_id = defaccountId;
//		int account_id = RegisterMobileTest.aid;
		Boolean needpaperreport = Boolean.parseBoolean(args[5]);
		String addItemId = args[6];
		List<Integer> item_list = new ArrayList<Integer>();
		// 套餐内初始项目
		List<ExamItem> itemsInMeal = new ArrayList<ExamItem>();
		// 套餐外加的项目
		List<ExamItem> addedItem = new ArrayList<ExamItem>();
		// 套餐内减的项目
		List<ExamItem> reducedItems = new ArrayList<ExamItem>();
		// 所有项目包括套餐内项目、减项、加项
		List<ExamItem> allRelatedItems = new ArrayList<ExamItem>();
		// 最终预约的单项
		List<ExamItem> finalItems = new ArrayList<ExamItem>();
		// 最终套餐内项目，不包括减项
		List<ExamItem> finalItemsInMeal = new ArrayList<ExamItem>();

		List<Integer> itemIdsInMeal = ResourceChecker.getMealExamItemIdList(bookMealId);
		List<MealItem> itemIdsInMeal1 = ResourceChecker.getMealIteminfo(bookMealId);
		itemsInMeal = ResourceChecker.getItemInfoByIds(itemIdsInMeal);
		// 当项目数量大于1时，预约时减去最后一个项目
		if (itemIdsInMeal1.size() > 1) {
			for(MealItem mi : itemIdsInMeal1){
				if(!mi.isBasic()&&mi.isSelected()){
//					System.out.println("要删除的单项为："+mi.getId());
					reducedItems.add(ResourceChecker.checkExamItem(mi.getId()));
					break;
				}
			}
		}
		addedItem.add(ResourceChecker.checkExamItem(Integer.parseInt(addItemId)));
		allRelatedItems.addAll(addedItem);
		allRelatedItems.addAll(itemsInMeal);

		//1.query form
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		NameValuePair _p = new BasicNameValuePair("_p", "" + "");
		NameValuePair _site = new BasicNameValuePair("_site", "mtjk" + "");
		int fromSite = HospitalChecker.getHospitalIdBySite("mtjk").getId();
		NameValuePair _siteType = new BasicNameValuePair("_siteType", "mobile" + "");
		params.add(_siteType);
		params.add(_p);
		params.add(_site);
		//2.json Object
		BookParams bookParams = new BookParams();
		bookParams.setExamDate(exam_date);
		bookParams.setExamTimeIntervalId(Integer.parseInt(examTime_interval_id));
		bookParams.setAccountId(account_id);
		bookParams.setMealId(bookMealId);
		bookParams.setNeedPaperReport(needpaperreport);
		bookParams.setSource(2);
		bookParams.setEntryCardId(entryCardId);

		int examinerId = 0;
		boolean is_self = false;
		if(checkdb){
			String sql = "select id,is_self from tb_examiner where organization_id = "+fromSite + " and customer_id = "+defaccountId +"  and is_delete = 0 order by update_time desc limit 1";
			log.info(sql);
			List<Map<String,Object>> dblist = DBMapper.query(sql);
			if(dblist != null && dblist.size() >0){
				examinerId = Integer.parseInt(dblist.get(0).get("id").toString());
				is_self =  Integer.parseInt(dblist.get(0).get("is_self").toString()) == 0?false:true;
			}
		}
		if(examinerId == 0 ){
			log.error("没有体检人，请先添加体检人");
			return;
		}
		bookParams.setExaminerId(examinerId);
		bookParams.setInformMe(is_self);


		// 获取单位/体检中心人数
		List<HospitalCapacityUsed> hospitalCounter = CounterChecker.getHospitalCount(defHospital.getId(), exam_date)
				.stream().filter(hcu -> hcu.getPeriodId().intValue() == Integer.valueOf(examTime_interval_id))
				.collect(Collectors.toList());

		finalItems = ResourceChecker.getFinalItems(itemsInMeal, addedItem, reducedItems);
		finalItemsInMeal = itemsInMeal;
		List<Integer> finalItemIds = ResourceChecker.getExamIdList(finalItems);
		bookParams.setItemIds(finalItemIds);

		int packId = ResourceChecker.getAvaiablePackages(defHospitalId, ResourceChecker.getMealInfo(bookMealId).getGender()).get(0).getId();
		bookParams.setPackageIds(Arrays.asList(packId));
		ExamItemPackage one_pack = ResourceChecker.getPackageInfo(packId);
		List<ExamItem> itemsInPack = one_pack.getItemList();
		// 重复项目
		List<ExamItem> duplicateItems = new ArrayList<ExamItem>();
		for (ExamItem j : itemsInPack) {
			List<ExamItem> dupItems = new ArrayList<ExamItem>();
			// 筛选出加项包中和全部项目中重复的项目
			dupItems = finalItems.stream().filter(item -> item.getId() == j.getId()).collect(Collectors.toList());
			if (dupItems.isEmpty()) {
				allRelatedItems.add(j);
				addedItem.add(j);
			}else
				duplicateItems.addAll(
						finalItems.stream().filter(item -> item.getId() == j.getId()).collect(Collectors.toList()));
		}



		HttpResult response = httpclient.post(Flag.MAIN, MainOrder_Book, params,JSON.toJSONString(bookParams));
		log.info("带加项包的订单..." + response.getBody());
		Assert.assertEquals(response.getCode(), HttpStatus.SC_OK);
		//检查下单返回值，异常处理
		OrderChecker.checkMainOrderBookResponse(httpclient,response,params,JSON.toJSONString(bookParams),defaccountId,examinerId,defHospitalId);

		havePackOrderId = Integer.parseInt(JsonPath.read(response.getBody(),"$.orderId").toString());
		// Assert
		Assert.assertNotNull(havePackOrderId);
		orderId.add(havePackOrderId);
		log.info("[orderIds:]////" + orderId);
		if (checkdb) {

			List<HospitalCapacityUsed> hospitalCounter1 = CounterChecker.getHospitalCount(defHospital.getId(), exam_date)
					.stream().filter(hcu -> hcu.getPeriodId().intValue() == Integer.valueOf(examTime_interval_id))
					.collect(Collectors.toList());
			CounterChecker.reduceCounter(-1, null, null, hospitalCounter, hospitalCounter1, 1);// 验证新体检日人数扣除

//			Double mealItemPrice = 0.0;
			// 验证itemDetail、mealDetail
			OrderMealSnapshot orderMealSnapshot = new OrderMealSnapshot();
			orderMealSnapshot = OrderChecker.getMealSnapShotByOrder(havePackOrderId);
			if(orderMealSnapshot!=null){
				// 验证itemDetail
				List<ExamItemSnapshot> itemSnapshot = orderMealSnapshot.getExamItemSnapList();
				// itemSnapshot.add(e);
				// 根据项目ID排序
				itemSnapshot = itemSnapshot.stream().sorted(Comparator.comparing(ExamItemSnapshot::getId))
						.collect(Collectors.toList());
				allRelatedItems = allRelatedItems.stream().sorted(Comparator.comparing(ExamItem::getId))
						.collect(Collectors.toList());
				System.out.println("havePackOrderId = " + havePackOrderId);
				Assert.assertEquals(itemSnapshot.size(), allRelatedItems.size());

				// 验证数据库中单项信息正确
				List<Integer> ids = itemSnapshot.stream().map(m->m.getId()).collect(Collectors.toList());
//				ResourceChecker.checkItemByTypeFromMysql(itemSnapshot, finalItemsInMeal, reducedItems, addedItem, duplicateItems, bookMeal);
				ResourceChecker.checkItemByTypeFromMysql(itemSnapshot, finalItemsInMeal, reducedItems, addedItem, null, bookMeal);

			}


			// 验证mealDetail
			MealSnapshot mealSnapshot = orderMealSnapshot.getMealSnapshot();
			Assert.assertEquals(mealSnapshot.getOriginMeal().getClass(), bookMeal.getClass());

			Order order = OrderChecker.getOrderInfo(havePackOrderId);
			Assert.assertEquals(sdf.format(order.getExamDate()), exam_date);
			Assert.assertEquals(order.getNeedPaperReport().toString(), needpaperreport.toString());
			String package_snapshot_detail = order.getPackageSnapshotDetail();
			ExamItemPackageSnapshot packageSnapshotDetail = orderMealSnapshot.getExamItemPackageSnapshot();
			List<ExamItemPackage> itemPack = packageSnapshotDetail.getPackages();
			for (ExamItemPackage e : itemPack)
				Assert.assertEquals(e.getId().intValue(), packId);
			List<ExamItemInPackageInfo> itemInPacksInfo = new ArrayList<ExamItemInPackageInfo>();
			itemInPacksInfo = packageSnapshotDetail.getExamItemInfos();

			// 根据项目ID排序
			itemInPacksInfo = itemInPacksInfo.stream()
					.sorted(Comparator.comparing(ExamItemInPackageInfo::getExamItemId)).collect(Collectors.toList());
			itemsInPack = itemsInPack.stream().sorted(Comparator.comparing(ExamItem::getId))
					.collect(Collectors.toList());
			duplicateItems = duplicateItems.stream().sorted(Comparator.comparing(ExamItem::getId))
					.collect(Collectors.toList());

			log.info("orderId...." + order.getId() + "pack...." + package_snapshot_detail);
			List<ExamItemPackageVO> packs = JSON.parseArray(
					JsonPath.read(package_snapshot_detail, "$.packages").toString(), ExamItemPackageVO.class);
			for (ExamItemPackageVO e : packs)
				Assert.assertEquals(e.getId().intValue(), packId);

			Assert.assertEquals(itemInPacksInfo.size(), itemsInPack.size());
			for (int i = 0; i < itemInPacksInfo.size(); i++) {
				ExamItemInPackageInfo DBItemInPack = itemInPacksInfo.get(i);
				ExamItem itemInPack = itemsInPack.get(i);
				Assert.assertEquals(DBItemInPack.getExamItemId().intValue(), itemInPack.getId());
			}
			List<ExamItem> DBDuplicateItems = packageSnapshotDetail.getDuplicatePackageItems();
			DBDuplicateItems = DBDuplicateItems.stream().sorted(Comparator.comparing(ExamItem::getId))
					.collect(Collectors.toList());
			Assert.assertEquals(DBDuplicateItems.size(), duplicateItems.size());
			for (int i = 0; i < DBDuplicateItems.size(); i++) {
				Assert.assertEquals(DBDuplicateItems.get(i).getId(), duplicateItems.get(i).getId());
			}
			// 验证订单操作日志
			List<ExamOrderOperateLogDO> logs = OrderChecker.getOrderOperatrLog(order.getOrderNum());
			Assert.assertEquals(logs.size(), 1);
			Assert.assertEquals(logs.get(0).getType().intValue(), OrderOperateTypeEnum.BOOK_ORDER.getCode());
			Assert.assertEquals(logs.get(0).getOrderStatus().intValue(), OrderStatus.NOT_PAY.intValue());
			Assert.assertEquals(logs.get(0).getSystem().intValue(), OperateAppEnum.CLIENT.getCode());

			if (checkmongo) {
				List<Map<String, Object>> monlist = MongoDBUtils.query("{'id':" + order.getId() + "}",
						MONGO_COLLECTION);
				Assert.assertNotNull(monlist);
				Assert.assertEquals(sdf.format(monlist.get(0).get("examDate")), exam_date);
			}
			//校验结算
			OrderChecker.check_Book_ExamOrderSettlement(order);
		}
		// 校验支付页面
		PayChecker.checkPayPage(httpclient, havePackOrderId, -1,defSite,defaccountId);
		System.out.println("-----------------------02_C端下单(带加项包的订单)End----------------------------");
	}

	@Test(dataProvider = "book_card", groups = { "qa", "main_bookWithEntryCard" }, dependsOnMethods = "test_02_book_pack")
	public void test_03_book_entrycard(String... args) throws SqlException, ParseException {
		System.out.println("-----------------------03_C端下单(使用入口卡进入)Start----------------------------");
		String exam_date = args[1];
		HospitalCompany hc1 = CompanyChecker.getHospitalCompanyByPlatCompanyIdANDOrganizationId(1,defHospitalId);
		Map<String,Object> dateMap = CounterChecker.getDateBookableFromStartDate(sdf.parse(exam_date),sdf.parse(exam_date),hc1.getId(), defHospitalId);
		int dayRangeId = Integer.parseInt(dateMap.get("dayRangeId").toString());
		String examTime_interval_id = dayRangeId+"";
		int account_id = defaccountId;
		List<Integer> item_list = new ArrayList<Integer>();
		Boolean needpaperreport = Boolean.parseBoolean(args[5]);
		 List<Integer> cardList = 	CardChecker.getValidCardByAccountANDHospital(defHospitalId, defaccountId, true, false); // 获取入口卡
		if(cardList!=null&& cardList.size()>0)
				entryCardId = cardList.get(0);
		else{
			log.error("没有体检中心的入口卡，请手动创建!!");
			return;
		}
		System.out.println("book_card..."+entryCardId);
		HospitalCompany  hCompany = CardChecker.getHospitalCompanyByCardId(entryCardId,defHospitalId+"");
		int meal_id = 0;
		List<Integer> mealIds = CardChecker.getMealByCardId(entryCardId, defHospitalId);// 获取入口卡可用的套餐
		for (Integer id : mealIds) {
			if (ResourceChecker.getMealInfo(id).getGender() == defGender) {
				meal_id = id;
				break;
			}
		}
		//1.query form
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		NameValuePair _p = new BasicNameValuePair("_p", "" + "");
		NameValuePair _site = new BasicNameValuePair("_site", "" + "");
		NameValuePair _siteType = new BasicNameValuePair("_siteType", "mobile" + "");
		params.add(_p);
		params.add(_site);
		params.add(_siteType);
		//2.json Object
		BookParams bookParams = new BookParams();
		bookParams.setExamDate(exam_date);
		bookParams.setExamTimeIntervalId(Integer.parseInt(examTime_interval_id));
		bookParams.setAccountId(account_id);
		bookParams.setMealId(meal_id);
		bookParams.setNeedPaperReport(needpaperreport);
		bookParams.setEntryCardId(entryCardId);
		bookParams.setSource(2);
		List<MealItem> mealitem = ResourceChecker.getMealInnerItemList(meal_id);
		for (int i = 0; i < mealitem.size(); i++) {
			item_list.add(i, mealitem.get(i).getId());
		}
		bookParams.setItemIds(item_list);

		int examinerId = 0;
		boolean is_self = false;
		if(checkdb){
			String sql = "select id,is_self from tb_examiner where organization_id = "+defHospitalId + " and customer_id = "+defaccountId +"  and is_delete = 0 order by update_time desc limit 1";
			log.info(sql);
			List<Map<String,Object>> dblist = DBMapper.query(sql);
			if(dblist != null && dblist.size() >0){
				examinerId = Integer.parseInt(dblist.get(0).get("id").toString());
				is_self =  Integer.parseInt(dblist.get(0).get("is_self").toString()) == 0?false:true;
			}
		}
		if(examinerId == 0 ){
			log.error("没有体检人，请先添加体检人");
			return;
		}
		bookParams.setExaminerId(examinerId);
		bookParams.setInformMe(is_self);


		// 获取单位/体检中心人数
		List<HospitalCapacityUsed> hospitalCounter = CounterChecker.getHospitalCount(defHospitalId, exam_date).stream()
				.filter(hcu -> hcu.getPeriodId().intValue() == Integer.valueOf(examTime_interval_id)).collect(Collectors.toList());
		List<CompanyCapacityUsed> companyCounter = CounterChecker.getCompanyCount(defHospitalId,hCompany.getId(),exam_date)
				.stream().filter(ccu->ccu.getPeriodId().intValue()==Integer.valueOf(examTime_interval_id)).collect(Collectors.toList());

		// 根据套餐获取医院信息
		String mealSql = "select * from tb_meal where id=?";
		List<Map<String, Object>> mealList = DBMapper.query(mealSql, meal_id);
		String hospitalId = mealList.get(0).get("hospital_id").toString();

		// 根据入口卡获取单位信息
		Card card = CardChecker.getCardInfo(entryCardId);
		
		int hospitalCompanyId  = -1;
		if(card.getOrganizationType() == 2){
			HospitalCompany hc = CompanyChecker.getHospitalCompanyByChannelCompanyId(card.getNewCompanyId(), defHospitalId);
			hospitalCompanyId = hc.getId();
		}else
			hospitalCompanyId = card.getNewCompanyId();

		// 把单位设置为“非预留日可约”
		String capacityConfigSql = "update tb_company_capacity_info set can_order=1 where company_id=" + hospitalCompanyId
				+ " and hospital_id=" + hospitalId;
		DBMapper.update(capacityConfigSql);

		HttpResult response = httpclient.post(Flag.MAIN, MainOrder_Book, params,JSON.toJSONString(bookParams));
		log.info("bookTest  test_03_book_entrycard ...." + response.getBody());
		Assert.assertEquals(response.getCode(), HttpStatus.SC_OK);

		//检查下单返回值，异常处理
		OrderChecker.checkMainOrderBookResponse(httpclient,response,params,JSON.toJSONString(bookParams),defaccountId,examinerId,defHospitalId);

		comm_entryCard_OrderId = Integer.parseInt(JsonPath.read(response.getBody(),"$.orderId").toString());

		// Assert
		Assert.assertNotNull(comm_entryCard_OrderId);
		orderId.add(comm_entryCard_OrderId);
		log.info("[entrycard_orderIds:]////" + orderId);
		if (checkdb) {
			Order order = OrderChecker.getOrderInfo(comm_entryCard_OrderId);
			System.out.println(comm_entryCard_OrderId);
			Assert.assertEquals(sdf.format(order.getExamDate()), exam_date);
			Assert.assertEquals(order.getNeedPaperReport().toString(), needpaperreport.toString());
			///获取卡对应的客户经理
			Card accountCard = CardChecker.getCardInfo(entryCardId);
			int cardManagerId = accountCard.getManagerId();
			Assert.assertEquals(order.getManagerId().intValue(),cardManagerId); //卡预约订单=发卡的客户经理
			// String package_snapshot_detail = order.getPackageSnapshotDetail();
			log.info("orderId...." + order.getId() + "entryCard...." + entryCardId);

			// 获取单位/体检中心人数
			List<HospitalCapacityUsed> hospitalCounter1 = CounterChecker.getHospitalCount(defHospitalId, exam_date)
					.stream().filter(hcu -> hcu.getPeriodId().intValue() == Integer.valueOf(examTime_interval_id))
					.collect(Collectors.toList());
			List<CompanyCapacityUsed> companyCounter1 = CounterChecker.getCompanyCount(defHospitalId,hCompany.getId(),exam_date)
					.stream().filter(ccu->ccu.getPeriodId().intValue()==Integer.valueOf(examTime_interval_id)).collect(Collectors.toList());
			CounterChecker.reduceCounter(-1, companyCounter, companyCounter1, hospitalCounter, hospitalCounter1, 1);// 验证新体检日人数扣除

			// 验证订单操作日志
			List<ExamOrderOperateLogDO> logs = OrderChecker.getOrderOperatrLog(order.getOrderNum());
			Assert.assertEquals(logs.size(), 1);
			Assert.assertEquals(logs.get(0).getType().intValue(), OrderOperateTypeEnum.BOOK_ORDER.getCode());
			Assert.assertEquals(logs.get(0).getOrderStatus().intValue(), OrderStatus.NOT_PAY.intValue());
			Assert.assertEquals(logs.get(0).getSystem().intValue(), OperateAppEnum.CLIENT.getCode());

			if (checkmongo) {
				waitto(mongoWaitTime);
				List<Map<String, Object>> monlist = MongoDBUtils.query("{'id':" + order.getId() + "}",
						MONGO_COLLECTION);
				Assert.assertNotNull(monlist);
				Assert.assertEquals(sdf.format(monlist.get(0).get("examDate")), exam_date);
			}
			//校验结算
			OrderChecker.check_Book_ExamOrderSettlement(order);
		}
		// 校验支付页面信息
		PayChecker.checkPayPage(httpclient, comm_entryCard_OrderId, entryCardId,defSite,defaccountId);

		System.out.println("-----------------------03_C端下单(使用入口卡进入)End----------------------------");
	}

	@Test(dataProvider = "book_card", groups = { "qa" })
	public void test_04_book_hidePricecard(String... args) throws SqlException, ParseException {
		System.out.println("-----------------------03_C端下单(使用隐价卡下单进入)Start----------------------------");
		String exam_date = args[1];
		HospitalCompany hc1 = CompanyChecker.getHospitalCompanyByPlatCompanyIdANDOrganizationId(1,defHospitalId);
		Map<String,Object> dateMap = CounterChecker.getDateBookableFromStartDate(sdf.parse(exam_date),sdf.parse(exam_date),hc1.getId(), defHospitalId);
		int dayRangeId = Integer.parseInt(dateMap.get("dayRangeId").toString());
		String examTime_interval_id = dayRangeId+"";
		int account_id = defaccountId;
		List<Integer> item_list = new ArrayList<Integer>();
		Boolean needpaperreport = Boolean.parseBoolean(args[5]);
		List<Integer> cardList = CardChecker.getValidCardByAccountANDHospital(defHospitalId, defaccountId, true, true);
		if(cardList ==null || cardList.size() ==0){
			log.info("登陆CRM发隐价卡");
			try{
				MyHttpClient crmClient = new MyHttpClient();
				onceLoginInSystem(crmClient,Flag.CRM,defCrmUsername,defCrmPasswd);
				HospitalCompany hc = CompanyChecker.getRandomCommonHospitalCompany(defHospitalId);
				Card card = CardChecker
						.createHidePriceCard(crmClient,defaccountId,hc.getId(),"CC隐价卡",defHospitalId, AccountChecker.getUserInfo(defCrmUsername, SystemTypeEnum.CRM_LOGIN.getCode()).getAccount_id(),MealGenderEnum.FEMALE.getCode());
				cardList = new ArrayList<>();
				cardList.add(card.getId());
				onceLogOutSystem(crmClient,Flag.CRM);
			}catch (AssertionError e ){
				log.error("CRM未开启，请检查,无法正常发隐价卡!!!");
				return;
			}
		}
		entryCardId = 	cardList.get(0); // 获取入口卡
		System.out.println("book_hide_price_card..."+entryCardId);
		HospitalCompany  hCompany = CardChecker.getHospitalCompanyByCardId(entryCardId,defHospitalId+"");
		int meal_id = 0;
		List<Integer> mealIds = CardChecker.getMealByCardId(entryCardId, defHospitalId);// 获取入口卡可用的套餐
		for (Integer id : mealIds) {
			if (ResourceChecker.getMealInfo(id).getGender() == defGender) {
				meal_id = id;
				break;
			}
		}

		List<MealItem> mealitem = ResourceChecker.getMealInnerItemList(meal_id);


		//1.query form
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		NameValuePair _p = new BasicNameValuePair("_p", "" + "");
		NameValuePair _site = new BasicNameValuePair("_site", "" + "");
		NameValuePair _siteType = new BasicNameValuePair("_siteType", "mobile" + "");
		params.add(_p);
		params.add(_site);
		params.add(_siteType);
		//2.json Object
		BookParams bookParams = new BookParams();
		bookParams.setExamDate(exam_date);
		bookParams.setExamTimeIntervalId(Integer.parseInt(examTime_interval_id));
		bookParams.setAccountId(account_id);
		bookParams.setMealId(meal_id);
		bookParams.setNeedPaperReport(needpaperreport);
		bookParams.setEntryCardId(entryCardId);
		bookParams.setSource(2);

		int examinerId = 0;
		boolean is_self = false;
		if(checkdb){
			String sql = "select id,is_self from tb_examiner where organization_id = "+defHospitalId + " and customer_id = "+defaccountId +"  and is_delete = 0 order by update_time desc limit 1";
			log.info(sql);
			List<Map<String,Object>> dblist = DBMapper.query(sql);
			if(dblist != null && dblist.size() >0){
				examinerId = Integer.parseInt(dblist.get(0).get("id").toString());
				is_self =  Integer.parseInt(dblist.get(0).get("is_self").toString()) == 0?false:true;
			}
		}
		if(examinerId == 0 ){
			log.error("没有体检人，请先添加体检人");
			return;
		}
		bookParams.setExaminerId(examinerId);
		bookParams.setInformMe(is_self);

		for (int i = 0; i < mealitem.size(); i++) {
			item_list.add(i, mealitem.get(i).getId());
		}
		bookParams.setItemIds(item_list);

		// 获取单位/体检中心人数
		List<HospitalCapacityUsed> hospitalCounter = CounterChecker.getHospitalCount(defHospitalId, exam_date).stream()
				.filter(hcu -> hcu.getPeriodId().intValue() == Integer.valueOf(examTime_interval_id)).collect(Collectors.toList());
		List<CompanyCapacityUsed> companyCounter = CounterChecker.getCompanyCount(defHospitalId,hCompany.getId(),exam_date)
				.stream().filter(ccu->ccu.getPeriodId().intValue()==Integer.valueOf(examTime_interval_id)).collect(Collectors.toList());

		// 根据套餐获取医院信息
		String mealSql = "select * from tb_meal where id=?";
		List<Map<String, Object>> mealList = DBMapper.query(mealSql, meal_id);
		String hospitalId = mealList.get(0).get("hospital_id").toString();

		// 根据入口卡获取单位信息
		Card card = CardChecker.getCardInfo(entryCardId);

		int hospitalCompanyId  = -1;
		if(card.getOrganizationType() == 2){
			HospitalCompany hc = CompanyChecker.getHospitalCompanyByChannelCompanyId(card.getNewCompanyId(), defHospitalId);
			hospitalCompanyId = hc.getId();
		}else
			hospitalCompanyId = card.getNewCompanyId();

		// 把单位设置为“非预留日可约”
		String capacityConfigSql = "update tb_company_capacity_info set can_order=1 where company_id=" + hospitalCompanyId
				+ " and hospital_id=" + hospitalId;
		DBMapper.update(capacityConfigSql);

		boolean isContinuedOrder = false;//是否是撤销订单再下单
		HttpResult response = httpclient.post(Flag.MAIN, MainOrder_Book, params,JSON.toJSONString(bookParams));
		log.info("bookTest  test_04_book_hidePricecard ...." + response.getBody());
		Assert.assertEquals(response.getCode(), HttpStatus.SC_OK);
	    if (response.getBody().contains("当前体检卡已有未支付订单，如果继续下单，则系统会自动撤销未支付订单")){
			System.out.println("当前体检卡已有未支付订单，如果继续下单，则系统会自动撤销未支付订单");
			//继续下单
			isContinuedOrder = true;
			bookParams.setContinuedOrder(true);
			response = httpclient.post(Flag.MAIN, MainOrder_Book, params,JSON.toJSONString(bookParams));
		}
		//检查下单返回值，异常处理
		OrderChecker.checkMainOrderBookResponse(httpclient,response,params,JSON.toJSONString(bookParams),defaccountId,examinerId,defHospitalId);

		comm_entryCard_OrderId = Integer.parseInt(JsonPath.read(response.getBody(),"$.orderId").toString());

		// Assert
		Assert.assertNotNull(comm_entryCard_OrderId);
		orderId.add(comm_entryCard_OrderId);
		log.info("[entrycard_orderIds:]////" + orderId);
		if (checkdb) {
			Order order = OrderChecker.getOrderInfo(comm_entryCard_OrderId);
			System.out.println(comm_entryCard_OrderId);
			Assert.assertEquals(sdf.format(order.getExamDate()), exam_date);
			Assert.assertEquals(order.getNeedPaperReport().toString(), needpaperreport.toString());
			///获取卡对应的客户经理
			Card accountCard = CardChecker.getCardInfo(entryCardId);
			int cardManagerId = accountCard.getManagerId();
			Assert.assertEquals(order.getManagerId().intValue(),cardManagerId); //卡预约订单=发卡的客户经理
			// String package_snapshot_detail = order.getPackageSnapshotDetail();
			log.info("orderId...." + order.getId() + "entryCard...." + entryCardId);

			// 获取单位/体检中心人数
			List<HospitalCapacityUsed> hospitalCounter1 = CounterChecker.getHospitalCount(defHospitalId, exam_date)
					.stream().filter(hcu -> hcu.getPeriodId().intValue() == Integer.valueOf(examTime_interval_id))
					.collect(Collectors.toList());
			List<CompanyCapacityUsed> companyCounter1 = CounterChecker.getCompanyCount(defHospitalId,hCompany.getId(),exam_date)
					.stream().filter(ccu->ccu.getPeriodId().intValue()==Integer.valueOf(examTime_interval_id)).collect(Collectors.toList());
			if(isContinuedOrder)
				CounterChecker.reduceCounter(-1, companyCounter, companyCounter1, hospitalCounter, hospitalCounter1, 0);// 验证新体检日人数不变
			else
				CounterChecker.reduceCounter(-1, companyCounter, companyCounter1, hospitalCounter, hospitalCounter1, 1);// 验证新体检日人数扣除

			// 验证订单操作日志
			List<ExamOrderOperateLogDO> logs = OrderChecker.getOrderOperatrLog(order.getOrderNum());
			Assert.assertEquals(logs.size(), 1);
			Assert.assertEquals(logs.get(0).getType().intValue(), OrderOperateTypeEnum.BOOK_ORDER.getCode());
			Assert.assertEquals(logs.get(0).getOrderStatus().intValue(), OrderStatus.NOT_PAY.intValue());
			Assert.assertEquals(logs.get(0).getSystem().intValue(), OperateAppEnum.CLIENT.getCode());

			if (checkmongo) {
				waitto(mongoWaitTime);
				List<Map<String, Object>> monlist = MongoDBUtils.query("{'id':" + order.getId() + "}",
						MONGO_COLLECTION);
				Assert.assertNotNull(monlist);
				Assert.assertEquals(sdf.format(monlist.get(0).get("examDate")), exam_date);
			}
			//校验结算
			OrderChecker.check_Book_ExamOrderSettlement(order);
		}
		// 校验支付页面信息
		PayChecker.checkPayPage(httpclient, comm_entryCard_OrderId, entryCardId,defSite,defaccountId);

		System.out.println("-----------------------03_C端下单(使用隐价卡进入)End----------------------------");
	}

	@Test(description = "渠道商二级站点上下单", groups = { "qa", "main_channelBook" }, dataProvider = "channelBook")
	public void test_05_channelBook(String... args) throws SqlException, ParseException {
		System.out.println("------------------------04_渠道商二级站点下单 Start ----------------------------------");
		String exam_date = args[1];
		HospitalCompany hc = CompanyChecker.getHospitalCompanyByPlatCompanyIdANDOrganizationId(1,defHospitalId);
		Map<String,Object> dateMap = CounterChecker.getDateBookableFromStartDate(sdf.parse(exam_date),sdf.parse(exam_date),hc.getId(), defHospitalId);
		int dayRangeId = Integer.parseInt(dateMap.get("dayRangeId").toString());
		String examTime_interval_id = dayRangeId+"";
		int account_id = defaccountId;
		int meal_id = ResourceChecker.getOfficialMealList(defHospitalId, defGender).get(0).getId();
		List<Integer> item_list = new ArrayList<Integer>();
		List<MealItem> mealitem = ResourceChecker.getMealInnerItemList(meal_id);
		String _source = args[5];
		Boolean needpaperreport = Boolean.parseBoolean(args[6]);
		String site = args[7];
		String siteType = args[8];
		//1.query form
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		NameValuePair _p = new BasicNameValuePair("_p", "" + "");
		NameValuePair _site = new BasicNameValuePair("_site", site);
		NameValuePair _siteType = new BasicNameValuePair("_siteType", siteType);
		params.add(_p);
		params.add(_site);
		params.add(_siteType);
		//2.json Object
		BookParams bookParams = new BookParams();
		bookParams.setExamDate(exam_date);
		bookParams.setExamTimeIntervalId(dayRangeId);
		bookParams.setAccountId(account_id);
		bookParams.setMealId(meal_id);
		bookParams.setNeedPaperReport(needpaperreport);
		bookParams.setSource(Integer.parseInt(_source));

		int fromSite = HospitalChecker.getHospitalIdBySite(site).getId();
		int examinerId = 0;
		boolean is_self = false;
		if(checkdb){
			String sql = "select id,is_self from tb_examiner where organization_id = "+fromSite + " and customer_id = "+defaccountId +"  and is_delete = 0 order by update_time desc limit 1";
			log.info(sql);
			List<Map<String,Object>> dblist = DBMapper.query(sql);
			if(dblist != null && dblist.size() >0){
				examinerId = Integer.parseInt(dblist.get(0).get("id").toString());
				is_self =  Integer.parseInt(dblist.get(0).get("is_self").toString()) == 0?false:true;
			}
		}
		if(examinerId == 0 ){
			log.error("没有体检人，请先添加体检人");
			return;
		}
		bookParams.setExaminerId(examinerId);
		bookParams.setInformMe(is_self);

		for (int i = 0; i < mealitem.size(); i++) {
			item_list.add(i, mealitem.get(i).getId());
		}
		bookParams.setItemIds(item_list);

		// 获取单位/体检中心人数
		List<HospitalCapacityUsed> hospitalCounter = CounterChecker.getHospitalCount(defHospitalId, exam_date).stream()
				.filter(hcu -> hcu.getPeriodId().intValue() == Integer.valueOf(examTime_interval_id)).collect(Collectors.toList());
		
		HttpResult response = httpclient.post(Flag.MAIN, MainOrder_Book, params,JSON.toJSONString(bookParams));
		log.info("bookTest  test_04_channelBook ...." + response.getBody());
		Assert.assertEquals(response.getCode(), HttpStatus.SC_OK);
		//检查下单返回值，异常处理
		OrderChecker.checkMainOrderBookResponse(httpclient,response,params,JSON.toJSONString(bookParams),defaccountId,examinerId,defHospitalId);

		channelOrderId = Integer.parseInt(JsonPath.read(response.getBody(),"$.orderId").toString());
		orderId.add(channelOrderId);
		System.out.println("channelOrderId=" + channelOrderId);


		// Assert
		Assert.assertNotNull(channelOrderId);
		log.info("[channelOrderId:]////" + orderId);
		if (checkdb) {
			Order order = OrderChecker.getOrderInfo(channelOrderId);
			Assert.assertEquals(sdf.format(order.getExamDate()), exam_date);
			Assert.assertEquals(order.getNeedPaperReport().toString(), needpaperreport.toString());
			log.info("orderId...." + order.getId() + "entryCard...." + entryCardId);

			List<HospitalCapacityUsed> hospitalCounter1 = CounterChecker.getHospitalCount(defHospital.getId(), exam_date)
					.stream().filter(hcu -> hcu.getPeriodId().intValue() == Integer.valueOf(examTime_interval_id))
					.collect(Collectors.toList());
			CounterChecker.reduceCounter(-1, null, null, hospitalCounter, hospitalCounter1, 1);// 验证新体检日人数扣除

			// 验证订单操作日志
			List<ExamOrderOperateLogDO> logs = OrderChecker.getOrderOperatrLog(order.getOrderNum());
			Assert.assertEquals(logs.size(), 1);
			Assert.assertEquals(logs.get(0).getType().intValue(), OrderOperateTypeEnum.BOOK_ORDER.getCode());
			Assert.assertEquals(logs.get(0).getOrderStatus().intValue(), OrderStatus.NOT_PAY.intValue());
			Assert.assertEquals(logs.get(0).getSystem().intValue(), OperateAppEnum.CLIENT.getCode());

			if (checkmongo) {
				waitto(mongoWaitTime);
				List<Map<String, Object>> monlist = MongoDBUtils.query("{'id':" + order.getId() + "}",
						MONGO_COLLECTION);
				Assert.assertNotNull(monlist);
				Assert.assertEquals(sdf.format(monlist.get(0).get("examDate")),exam_date);
			}
			//校验结算
			OrderChecker.check_Book_ExamOrderSettlement(order);
		}
		// 校验支付页面信息
		PayChecker.checkPayPage(httpclient, channelOrderId, -1,defSite,defaccountId);

		System.out.println("------------------------04_渠道商二级站点下单 End ----------------------------------");
	}

	@Test(dataProvider = "book", groups = { "qa", "main_mainBookWithLimitItem" })
	public void test_06_bookWithLimitItem(String... args) throws SqlException, ParseException {
		System.out.println("-----------------------05_C端无入口卡下单（含限制项）Start----------------------------");
		String exam_date = args[1];
		HospitalCompany hc = CompanyChecker.getHospitalCompanyByPlatCompanyIdANDOrganizationId(1,defHospitalId);
		Map<String,Object> dateMap = CounterChecker.getDateBookableFromStartDate(sdf.parse(exam_date),sdf.parse(exam_date),hc.getId(), defHospitalId);
		int dayRangeId = Integer.parseInt(dateMap.get("dayRangeId").toString());
		String examTime_interval_id = dayRangeId+"";
		limitExamItemsVo limitItem = getlimitItemsWithCount(defHospitalId, exam_date,
				Integer.valueOf(examTime_interval_id)).get(0);
		Integer addItemId = limitItem.getLimitItems().get(0).getId();
		System.out.println("限制项为：" + addItemId);
		int account_id = defaccountId;
		List<Meal> meals = ResourceChecker.getOffcialMeal(defHospitalId,Arrays.asList(MealGenderEnum.FEMALE.getCode()),true);
		Meal meal = meals.get(0);

		ItemsInOrder itemsInOrder = new ItemsInOrder();
		itemsInOrder = OrderChecker.generateItemsInOrder(meal.getId(), addItemId);
		Boolean needpaperreport = Boolean.parseBoolean(args[5]);

		//1.query form
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		NameValuePair _p = new BasicNameValuePair("_p", "" + "");
		NameValuePair _site = new BasicNameValuePair("_site", "mtjk");
		NameValuePair _siteType = new BasicNameValuePair("_siteType", "mobile");
		params.add(_p);
		params.add(_site);
		params.add(_siteType);
		//2.json Object
		BookParams bookParams = new BookParams();
		bookParams.setExamDate(exam_date);
		bookParams.setExamTimeIntervalId(Integer.parseInt(examTime_interval_id));
		bookParams.setAccountId(account_id);
		bookParams.setMealId(meal.getId());
		bookParams.setNeedPaperReport(needpaperreport);
		bookParams.setSource(2);

		int fromSite = HospitalChecker.getHospitalIdBySite("mtjk").getId();
		int examinerId = 0;
		boolean is_self = false;
		if(checkdb){
			String sql = "select id,is_self from tb_examiner where organization_id = "+fromSite + " and customer_id = "+defaccountId +"  and is_delete = 0 order by update_time desc limit 1";
			log.info(sql);
			List<Map<String,Object>> dblist = DBMapper.query(sql);
			if(dblist != null && dblist.size() >0){
				examinerId = Integer.parseInt(dblist.get(0).get("id").toString());
				is_self =  Integer.parseInt(dblist.get(0).get("is_self").toString()) == 0?false:true;
			}
		}
		if(examinerId == 0 ){
			log.error("没有体检人，请先添加体检人");
			return;
		}
		bookParams.setExaminerId(examinerId);
		bookParams.setInformMe(is_self);

		// 获取单位/体检中心人数
		List<Integer> dayRangesId = new ArrayList<Integer>();
		dayRangesId.add(Integer.valueOf(examTime_interval_id));
		List<Integer> litems = new ArrayList<Integer>();
		litems.add(-1);
		litems.add(limitItem.getItemId());
		List<HospitalCapacityUsed> hospitalCounter = CounterChecker
				.getHospitalCapacityUsed(defHospital.getId(), dayRangesId, litems, null, null, exam_date).stream()
				.filter(hcu -> hcu.getPeriodId().intValue() == Integer.valueOf(examTime_interval_id)).collect(Collectors.toList());

		List<Integer> finalItemIds = ResourceChecker.getExamIdList(itemsInOrder.getFinalItems());
		bookParams.setItemIds(finalItemIds);

		HttpResult response = httpclient.post(Flag.MAIN, MainOrder_Book, params,JSON.toJSONString(bookParams));
		log.info("bookTest...." + response.getBody());
		Assert.assertEquals(response.getCode(), HttpStatus.SC_OK);

		//检查下单返回值，异常处理
		OrderChecker.checkMainOrderBookResponse(httpclient,response,params,JSON.toJSONString(bookParams),defaccountId,examinerId,defHospitalId);

		log.info("response..."+response.getBody());
		commOrderId =Integer.parseInt(JsonPath.read(response.getBody(),"$.orderId").toString());

		// Assert
		Assert.assertNotNull(commOrderId);
		orderId.add(commOrderId);
		log.info("[orderIds:]////" + orderId);
		if (checkdb) {
			Order order = OrderChecker.getOrderInfo(commOrderId);
			Assert.assertEquals(sdf.format(order.getExamDate()), exam_date);
			Assert.assertEquals(order.getNeedPaperReport().toString(), needpaperreport.toString());
			String package_snapshot_detail = order.getPackageSnapshotDetail();
			log.info("orderId...." + order.getId() + "pack...." + package_snapshot_detail);

			List<HospitalCapacityUsed> hospitalCounter1 = CounterChecker
					.getHospitalCapacityUsed(defHospital.getId(), dayRangesId, litems, null, null, exam_date).stream()
					.filter(hcu -> hcu.getPeriodId().intValue() == Integer.valueOf(examTime_interval_id))
					.collect(Collectors.toList());
			CounterChecker.reduceCounter(-1, null, null, hospitalCounter, hospitalCounter1, 1);// 验证新体检日人数扣除
			CounterChecker.reduceCounter(limitItem.getItemId(), null, null, hospitalCounter, hospitalCounter1, 1);// 验证新体检日人数扣除

//			Double mealItemPrice = 0.0;

			// 验证订单操作日志
			List<ExamOrderOperateLogDO> logs = OrderChecker.getOrderOperatrLog(order.getOrderNum());
			Assert.assertEquals(logs.size(), 1);
			Assert.assertEquals(logs.get(0).getType().intValue(), OrderOperateTypeEnum.BOOK_ORDER.getCode());
			Assert.assertEquals(logs.get(0).getOrderStatus().intValue(), OrderStatus.NOT_PAY.intValue());
			Assert.assertEquals(logs.get(0).getSystem().intValue(), OperateAppEnum.CLIENT.getCode());

			// 验证itemDetail、mealDetail
			OrderMealSnapshot orderMealSnapshot = new OrderMealSnapshot();
			orderMealSnapshot = OrderChecker.getMealSnapShotByOrder(commOrderId);
			if(orderMealSnapshot !=null){
				// 验证itemDetail
				List<ExamItemSnapshot> itemSnapshot = orderMealSnapshot.getExamItemSnapList();
				// 根据项目ID排序
				itemSnapshot = itemSnapshot.stream().sorted(Comparator.comparing(ExamItemSnapshot::getId))
						.collect(Collectors.toList());
				itemsInOrder.setAllRelatedItems(itemsInOrder.getAllRelatedItems().stream()
						.sorted(Comparator.comparing(ExamItem::getId)).collect(Collectors.toList()));
				Assert.assertEquals(itemSnapshot.size(), itemsInOrder.getAllRelatedItems().size());

				// 验证数据库中单项信息正确(所有单项，最终套餐内项目，套餐内减项，套餐外加项，重复项，套餐)
				ResourceChecker.checkItemByTypeFromMysql(itemSnapshot, itemsInOrder, meal);

			}


			// 验证mealDetail
			MealSnapshot mealSnapshot = orderMealSnapshot.getMealSnapshot();
			Assert.assertEquals(mealSnapshot.getOriginMeal().getClass(), meal.getClass());

			if (checkmongo) {
				waitto(mongoWaitTime);
				List<Map<String, Object>> monlist = MongoDBUtils.query("{'id':" + order.getId() + "}",
						MONGO_COLLECTION);
				Assert.assertNotNull(monlist);
				Assert.assertEquals(sdf.format(monlist.get(0).get("examDate")), exam_date);
				// 验证itemDetail
				net.sf.json.JSONObject examItemSnapshotInMongoMeal = OrderChecker.getMongoMealSnapshot(monlist.get(0).get("orderNum").toString(),"examItemSnapshot");
				String examItemSnapStr = examItemSnapshotInMongoMeal.getString("value");
				JSONArray itemDetail_ja = JSONArray.fromObject(examItemSnapStr);
				sort(itemDetail_ja, "id", true);
				// 验证Mongo中单项信息正确
				ResourceChecker.checkItemByTypeFromMONGO(itemDetail_ja, itemsInOrder, meal);

				// 验证mealDetail
				net.sf.json.JSONObject mealSnapshotInMongoMeal = OrderChecker.getMongoMealSnapshot(monlist.get(0).get("orderNum").toString(),"mealSnapshot");
				String mealSnapshotStr = mealSnapshotInMongoMeal.getString("value");
				JSONObject mealSnapshot_jo = JSONObject.fromObject(mealSnapshotStr);
				JSONObject originMeal_js = JSONObject.fromObject(mealSnapshot_jo.get("originMeal").toString());
				//最终单项之和
				long finalItemPrice = ResourceChecker.getExamListDiscountPrice(itemsInOrder.getFinalItems(),defHospitalId,meal.getDiscount(),meal.getMealSetting().getAdjustPrice());
				Assert.assertEquals(originMeal_js.get("id"), meal.getId());
				Assert.assertEquals(Integer.parseInt(originMeal_js.get("price").toString()), bookMeal.getPrice().intValue());

			}
			
			//校验结算
			OrderChecker.check_Book_ExamOrderSettlement(order);
		}
		// 校验支付页面
		PayChecker.checkPayPage(httpclient, commOrderId, -1,defSite,defaccountId);
		System.out.println("-----------------------05_C端无入口卡下单（含限制项）End----------------------------");
	}



	@Test(description = "随机使用1个P参数下单，客户经理为机构默认客户经理",dataProvider = "book_p", groups = { "qa" })
	public void test_07_book_with_promotion(String... args) throws SqlException, ParseException {
		System.out.println("-----------------------01_C端下单使用P参数(不带加项包的订单)Start----------------------------");
		String exam_date = args[1];
		HospitalCompany hc = CompanyChecker.getHospitalCompanyByPlatCompanyIdANDOrganizationId(1,defHospitalId);
		Map<String,Object> dateMap = CounterChecker.getDateBookableFromStartDate(sdf.parse(exam_date),sdf.parse(exam_date),hc.getId(), defHospitalId);
		int dayRangeId = Integer.parseInt(dateMap.get("dayRangeId").toString());
		String examTime_interval_id = dayRangeId+"";
		Integer addItemId = Integer.valueOf(args[6]);
		int account_id = defaccountId;
		ItemsInOrder itemsInOrder = new ItemsInOrder();
		itemsInOrder = OrderChecker.generateItemsInOrder(bookMealId, addItemId);

		Boolean needpaperreport = Boolean.parseBoolean(args[5]);
		//1.query form
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		String pString = AccountChecker.getHospitalUnrelationPromotions(defHospitalId).get(0);
		String site = "hzlyy";
		int fromSite = HospitalChecker.getHospitalIdBySite(site).getId();
		NameValuePair _p = new BasicNameValuePair("_p", pString);
		NameValuePair _site = new BasicNameValuePair("_site", site);
		NameValuePair _siteType = new BasicNameValuePair("_siteType", "mobile");
		params.add(_p);
		params.add(_site);
		params.add(_siteType);
		//2.json Object
		BookParams bookParams = new BookParams();
		bookParams.setExamDate(exam_date);
		bookParams.setExamTimeIntervalId(Integer.parseInt(examTime_interval_id));
		bookParams.setAccountId(account_id);
		bookParams.setMealId(bookMealId);
		bookParams.setNeedPaperReport(needpaperreport);
		bookParams.setSource(2);

		int examinerId = 0;
		boolean is_self = false;
		if(checkdb){
			String sql = "select id,is_self from tb_examiner where organization_id = "+fromSite + " and customer_id = "+defaccountId +"  and is_delete = 0 order by update_time desc limit 1";
			log.info(sql);
			List<Map<String,Object>> dblist = DBMapper.query(sql);
			if(dblist != null && dblist.size() >0){
				examinerId = Integer.parseInt(dblist.get(0).get("id").toString());
				is_self =  Integer.parseInt(dblist.get(0).get("is_self").toString()) == 0?false:true;
			}
		}
		if(examinerId == 0 ){
			log.error("没有体检人，请先添加体检人");
			return;
		}
		bookParams.setExaminerId(examinerId);
		bookParams.setInformMe(is_self);
		// 获取单位/体检中心人数
		List<HospitalCapacityUsed> hospitalCounter = CounterChecker.getHospitalCount(defHospital.getId(), exam_date)
				.stream().filter(hcu -> hcu.getPeriodId().intValue() == Integer.valueOf(examTime_interval_id))
				.collect(Collectors.toList());

		List<Integer> finalItemIds = ResourceChecker.getExamIdList(itemsInOrder.getFinalItems());
		bookParams.setItemIds(finalItemIds);

		//最终单项之和
		long finalItemPrice = ResourceChecker.getExamListDiscountPrice(itemsInOrder.getFinalItems(),defHospitalId,bookMeal.getDiscount(),bookMeal.getMealSetting().getAdjustPrice());

		HttpResult response = httpclient.post(Flag.MAIN, MainOrder_Book, params,JSON.toJSONString(bookParams));
		log.info("bookTest...." + response.getBody());
		Assert.assertEquals(response.getCode(), HttpStatus.SC_OK);
		//检查下单返回值，异常处理
		OrderChecker.checkMainOrderBookResponse(httpclient,response,params,JSON.toJSONString(bookParams),defaccountId,examinerId,defHospitalId);

		commOrderId = Integer.parseInt(JsonPath.read(response.getBody(),"$.orderId").toString());
			// int newOrderId = JsonPath.read(response.getBody(), "$.orderId");

			// Assert
			Assert.assertNotNull(commOrderId);
			orderId.add(commOrderId);
			log.info("[orderIds:]////" + orderId);
			if (checkdb) {
				Order order = OrderChecker.getOrderInfo(commOrderId);
				Assert.assertEquals(sdf.format(order.getExamDate()), exam_date);
				Assert.assertEquals(order.getNeedPaperReport().toString(), needpaperreport.toString());
				Assert.assertEquals(order.getManagerId().intValue(),HospitalChecker.getHospitalById(defHospitalId).getDefaultManagerId().intValue()); //默认客户经理
				//获取体检中心默认的客户经理
//			Hospital hospital = HospitalChecker.getHospitalIdBySite(site);
//			int defaultManagerId = AccountChecker.getDefMangerIdOfHospital(hospital.getId());
//			Assert.assertEquals(order.getManagerId().intValue(),defaultManagerId); //非卡进入=站点默认客户经理
				@SuppressWarnings("deprecation")
				String package_snapshot_detail = order.getPackageSnapshotDetail();
				log.info("orderId...." + order.getId() + "pack...." + package_snapshot_detail);

				List<HospitalCapacityUsed> hospitalCounter1 = CounterChecker.getHospitalCount(defHospital.getId(), exam_date)
						.stream().filter(hcu -> hcu.getPeriodId().intValue() == Integer.valueOf(examTime_interval_id))
						.collect(Collectors.toList());
				CounterChecker.reduceCounter(-1, null, null, hospitalCounter, hospitalCounter1, 1);// 验证新体检日人数扣除

//			Double mealItemPrice = 0.0;

				// 验证订单操作日志
				List<ExamOrderOperateLogDO> logs = OrderChecker.getOrderOperatrLog(order.getOrderNum());
				Assert.assertEquals(logs.size(), 1);
				Assert.assertEquals(logs.get(0).getType().intValue(), OrderOperateTypeEnum.BOOK_ORDER.getCode());
				Assert.assertEquals(logs.get(0).getOrderStatus().intValue(), OrderStatus.NOT_PAY.intValue());
				Assert.assertEquals(logs.get(0).getSystem().intValue(), OperateAppEnum.CLIENT.getCode());

				// 验证itemDetail、mealDetail
				OrderMealSnapshot orderMealSnapshot = new OrderMealSnapshot();
				orderMealSnapshot = OrderChecker.getMealSnapShotByOrder(commOrderId);
				if(orderMealSnapshot != null){
					// 验证itemDetail
					List<ExamItemSnapshot> itemSnapshot = orderMealSnapshot.getExamItemSnapList();
					// 根据项目ID排序
					itemSnapshot = itemSnapshot.stream().sorted(Comparator.comparing(ExamItemSnapshot::getId))
							.collect(Collectors.toList());
					itemsInOrder.setAllRelatedItems(itemsInOrder.getAllRelatedItems().stream()
							.sorted(Comparator.comparing(ExamItem::getId)).collect(Collectors.toList()));

					Assert.assertEquals(itemSnapshot.size(), itemsInOrder.getAllRelatedItems().size());

					// 验证数据库中单项信息正确(所有单项，最终套餐内项目，套餐内减项，套餐外加项，重复项，套餐)
					ResourceChecker.checkItemByTypeFromMysql(itemSnapshot, itemsInOrder, bookMeal);
				}



				// 验证mealDetail
				MealSnapshot mealSnapshot = orderMealSnapshot.getMealSnapshot();
				Assert.assertEquals(mealSnapshot.getOriginMeal().getClass(), bookMeal.getClass());

				if (checkmongo) {
					waitto(mongoWaitTime);
					List<Map<String, Object>> monlist = MongoDBUtils.query("{'id':" + order.getId() + "}",
							MONGO_COLLECTION);
					Assert.assertNotNull(monlist);
					Assert.assertEquals(sdf.format(monlist.get(0).get("examDate")), exam_date);
					// 验证itemDetail
					net.sf.json.JSONObject examItemSnapshotInMongoMeal = OrderChecker.getMongoMealSnapshot(monlist.get(0).get("orderNum").toString(),"examItemSnapshot");
					String examItemSnapStr = examItemSnapshotInMongoMeal.getString("value");
					JSONArray itemDetail_ja = JSONArray.fromObject(examItemSnapStr);
					sort(itemDetail_ja, "id", true);
					// 验证Mongo中单项信息正确
					ResourceChecker.checkItemByTypeFromMONGO(itemDetail_ja, itemsInOrder, bookMeal);

					// 验证mealDetail
					net.sf.json.JSONObject mealSnapshotInMongoMeal = OrderChecker.getMongoMealSnapshot(monlist.get(0).get("orderNum").toString(),"mealSnapshot");
					String mealSnapshotStr = mealSnapshotInMongoMeal.getString("value");
					JSONObject mealSnapshot_jo = JSONObject.fromObject(mealSnapshotStr);
					JSONObject originMeal_js = JSONObject.fromObject(mealSnapshot_jo.get("originMeal").toString());
					Assert.assertEquals(originMeal_js.get("id"), bookMeal.getId());
//					Assert.assertEquals(originMeal_js.get("price").toString(), finalItemPrice+"");去掉
					Assert.assertEquals(Integer.parseInt(originMeal_js.get("price").toString()), bookMeal.getPrice().intValue());

				}
				//校验结算
				OrderChecker.check_Book_ExamOrderSettlement(order);
			}

			// 校验支付页面
			PayChecker.checkPayPage(httpclient, commOrderId, -1,defSite,defaccountId);
			System.out.println("-----------------------01_C端下单使用P参数(不带加项包的订单)End----------------------------");
	}


	@Test(description = "使用体检中心的有效P参数下单，客户经理为P参数对应的客户经理",dataProvider = "book_pr", groups = { "qa" })
	public void test_08_book_with_hospital_rigth_promotion(String... args) throws SqlException, ParseException {
		System.out.println("-----------------------01_C端下单使用P参数(不带加项包的订单)Start----------------------------");
		String exam_date = args[1];
		HospitalCompany hc = CompanyChecker.getHospitalCompanyByPlatCompanyIdANDOrganizationId(1,defHospitalId);
		Map<String,Object> dateMap = CounterChecker.getDateBookableFromStartDate(sdf.parse(exam_date),sdf.parse(exam_date),hc.getId(), defHospitalId);
		int dayRangeId = Integer.parseInt(dateMap.get("dayRangeId").toString());
		String examTime_interval_id = dayRangeId+"";
		Integer addItemId = Integer.valueOf(args[6]);
		int account_id = defaccountId;
		ItemsInOrder itemsInOrder = new ItemsInOrder();
		itemsInOrder = OrderChecker.generateItemsInOrder(bookMealId, addItemId);

		Boolean needpaperreport = Boolean.parseBoolean(args[5]);

		//1.query form
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		String site = "hzlyy";
		int fromSite = HospitalChecker.getHospitalIdBySite(site).getId();
		String pString = AccountChecker.getHospitalPromotions(defHospitalId).get(0);
		NameValuePair _p = new BasicNameValuePair("_p", pString);
		NameValuePair _site = new BasicNameValuePair("_site", site);
		NameValuePair _siteType = new BasicNameValuePair("_siteType", "mobile");
		params.add(_p);
		params.add(_site);
		params.add(_siteType);
		//2.json object
		BookParams bookParams = new BookParams();
		bookParams.setExamDate(exam_date);
		bookParams.setExamTimeIntervalId(Integer.parseInt(examTime_interval_id));
		bookParams.setAccountId(account_id);
		bookParams.setMealId(bookMealId);
		bookParams.setNeedPaperReport(needpaperreport);
		bookParams.setSource(2);

		int examinerId = 0;
		boolean is_self = false;
		if(checkdb){
			String sql = "select id,is_self from tb_examiner where organization_id = "+fromSite + " and customer_id = "+defaccountId +"  and is_delete = 0 order by update_time desc limit 1";
			log.info(sql);
			List<Map<String,Object>> dblist = DBMapper.query(sql);
			if(dblist != null && dblist.size() >0){
				examinerId = Integer.parseInt(dblist.get(0).get("id").toString());
				is_self =  Integer.parseInt(dblist.get(0).get("is_self").toString()) == 0?false:true;
			}
		}
		if(examinerId == 0 ){
			log.error("没有体检人，请先添加体检人");
			return;
		}
		bookParams.setExaminerId(examinerId);
		bookParams.setInformMe(is_self);

		// 获取单位/体检中心人数
		List<HospitalCapacityUsed> hospitalCounter = CounterChecker.getHospitalCount(defHospital.getId(), exam_date)
				.stream().filter(hcu -> hcu.getPeriodId().intValue() == Integer.valueOf(examTime_interval_id))
				.collect(Collectors.toList());

		List<Integer> finalItemIds = ResourceChecker.getExamIdList(itemsInOrder.getFinalItems());
		bookParams.setItemIds(finalItemIds);
		//最终单项之和
		long finalItemPrice = ResourceChecker.getExamListDiscountPrice(itemsInOrder.getFinalItems(),defHospitalId,bookMeal.getDiscount(),bookMeal.getMealSetting().getAdjustPrice());

		HttpResult response = httpclient.post(Flag.MAIN, MainOrder_Book, params,JSON.toJSONString(bookParams));
		log.info("bookTest...." + response.getBody());
		Assert.assertEquals(response.getCode(), HttpStatus.SC_OK);
		//检查下单返回值，异常处理
		OrderChecker.checkMainOrderBookResponse(httpclient,response,params,JSON.toJSONString(bookParams),defaccountId,examinerId,defHospitalId);
		commOrderId = Integer.parseInt(JsonPath.read(response.getBody(),"$.orderId").toString());
		// int newOrderId = JsonPath.read(response.getBody(), "$.orderId");

		// Assert
		Assert.assertNotNull(commOrderId);
		orderId.add(commOrderId);
		log.info("[orderIds:]////" + orderId);
		if (checkdb) {
			Order order = OrderChecker.getOrderInfo(commOrderId);
			Assert.assertEquals(sdf.format(order.getExamDate()), exam_date);
			Assert.assertEquals(order.getNeedPaperReport().toString(), needpaperreport.toString());
			Assert.assertEquals(order.getManagerId().intValue(),AccountChecker.getPromotionManagerId(pString,defHospitalId)); //P参数的客户经理
			//获取体检中心默认的客户经理
//			Hospital hospital = HospitalChecker.getHospitalIdBySite(site);
//			int defaultManagerId = AccountChecker.getDefMangerIdOfHospital(hospital.getId());
//			Assert.assertEquals(order.getManagerId().intValue(),defaultManagerId); //非卡进入=站点默认客户经理
			@SuppressWarnings("deprecation")
			String package_snapshot_detail = order.getPackageSnapshotDetail();
			log.info("orderId...." + order.getId() + "pack...." + package_snapshot_detail);

			List<HospitalCapacityUsed> hospitalCounter1 = CounterChecker.getHospitalCount(defHospital.getId(), exam_date)
					.stream().filter(hcu -> hcu.getPeriodId().intValue() == Integer.valueOf(examTime_interval_id))
					.collect(Collectors.toList());
			CounterChecker.reduceCounter(-1, null, null, hospitalCounter, hospitalCounter1, 1);// 验证新体检日人数扣除

//			Double mealItemPrice = 0.0;

			// 验证订单操作日志
			List<ExamOrderOperateLogDO> logs = OrderChecker.getOrderOperatrLog(order.getOrderNum());
			Assert.assertEquals(logs.size(), 1);
			Assert.assertEquals(logs.get(0).getType().intValue(), OrderOperateTypeEnum.BOOK_ORDER.getCode());
			Assert.assertEquals(logs.get(0).getOrderStatus().intValue(), OrderStatus.NOT_PAY.intValue());
			Assert.assertEquals(logs.get(0).getSystem().intValue(), OperateAppEnum.CLIENT.getCode());

			// 验证itemDetail、mealDetail
			OrderMealSnapshot orderMealSnapshot = new OrderMealSnapshot();
			orderMealSnapshot = OrderChecker.getMealSnapShotByOrder(commOrderId);
			if(orderMealSnapshot != null){
				// 验证itemDetail
				List<ExamItemSnapshot> itemSnapshot = orderMealSnapshot.getExamItemSnapList();
				// 根据项目ID排序
				itemSnapshot = itemSnapshot.stream().sorted(Comparator.comparing(ExamItemSnapshot::getId))
						.collect(Collectors.toList());
				itemsInOrder.setAllRelatedItems(itemsInOrder.getAllRelatedItems().stream()
						.sorted(Comparator.comparing(ExamItem::getId)).collect(Collectors.toList()));

				Assert.assertEquals(itemSnapshot.size(), itemsInOrder.getAllRelatedItems().size());

				// 验证数据库中单项信息正确(所有单项，最终套餐内项目，套餐内减项，套餐外加项，重复项，套餐)
				ResourceChecker.checkItemByTypeFromMysql(itemSnapshot, itemsInOrder, bookMeal);
			}



			// 验证mealDetail
			MealSnapshot mealSnapshot = orderMealSnapshot.getMealSnapshot();
			Assert.assertEquals(mealSnapshot.getOriginMeal().getClass(), bookMeal.getClass());

			if (checkmongo) {
				waitto(mongoWaitTime);
				List<Map<String, Object>> monlist = MongoDBUtils.query("{'id':" + order.getId() + "}",
						MONGO_COLLECTION);
				Assert.assertNotNull(monlist);
				Assert.assertEquals(sdf.format(monlist.get(0).get("examDate")), exam_date);
				net.sf.json.JSONObject examItemSnapshotInMongoMeal = OrderChecker.getMongoMealSnapshot(monlist.get(0).get("orderNum").toString(),"examItemSnapshot");
				String examItemSnapStr = examItemSnapshotInMongoMeal.getString("value");
				// 验证itemDetail
				JSONArray itemDetail_ja = JSONArray.fromObject(examItemSnapStr);
				sort(itemDetail_ja, "id", true);
				// 验证Mongo中单项信息正确
				ResourceChecker.checkItemByTypeFromMONGO(itemDetail_ja, itemsInOrder, bookMeal);

				// 验证mealDetail
				net.sf.json.JSONObject mealSnapshotInMongoMeal = OrderChecker.getMongoMealSnapshot(monlist.get(0).get("orderNum").toString(),"mealSnapshot");
				String mealSnapshotStr = mealSnapshotInMongoMeal.getString("value");
				JSONObject mealSnapshot_jo = JSONObject.fromObject(mealSnapshot);
				JSONObject originMeal_js = JSONObject.fromObject(mealSnapshot_jo.get("originMeal").toString());
				Assert.assertEquals(originMeal_js.get("id"), bookMeal.getId());
				Assert.assertEquals(Integer.parseInt(originMeal_js.get("price").toString()), bookMeal.getPrice().intValue());

			}
			//校验结算
			OrderChecker.check_Book_ExamOrderSettlement(order);
		}

		// 校验支付页面
		PayChecker.checkPayPage(httpclient, commOrderId, -1,defSite,defaccountId);
		System.out.println("-----------------------01_C端下单使用P参数(不带加项包的订单)End----------------------------");
	}


	@Test(description = "使用渠道有效P参数下单，客户经理为P参数对应的平台客户客户经理",dataProvider = "book_prk", groups = { "qa" })
	public void test_09_book_with_channel_rigth_promotion(String... args) throws SqlException, ParseException {
		System.out.println("-----------------------01_C端下单使用P参数(不带加项包的订单)Start----------------------------");
		String exam_date = args[1];
		HospitalCompany hc = CompanyChecker.getHospitalCompanyByPlatCompanyIdANDOrganizationId(1,defHospitalId);
		Map<String,Object> dateMap = CounterChecker.getDateBookableFromStartDate(sdf.parse(exam_date),sdf.parse(exam_date),hc.getId(), defHospitalId);
		int dayRangeId = Integer.parseInt(dateMap.get("dayRangeId").toString());
		String examTime_interval_id = dayRangeId+"";
		Integer addItemId = Integer.valueOf(args[6]);
		int account_id = defaccountId;
		ItemsInOrder itemsInOrder = new ItemsInOrder();
		itemsInOrder = OrderChecker.generateItemsInOrder(bookMealId, addItemId);

		Boolean needpaperreport = Boolean.parseBoolean(args[5]);

		//1.query form
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		String site = "mtjk";
		int fromSite = HospitalChecker.getHospitalIdBySite(site).getId();
		List<String> pStringList = AccountChecker.getHospitalPromotions(fromSite);
		String pString = "";
		for(String s : pStringList)
			if(!s.equals(site))
				pString = s;
			else
				continue;
		NameValuePair _p = new BasicNameValuePair("_p", pString);
		NameValuePair _site = new BasicNameValuePair("_site", site);
		NameValuePair _siteType = new BasicNameValuePair("_siteType", "mobile");
		params.add(_p);
		params.add(_site);
		params.add(_siteType);

		//2.json Object
		BookParams bookParams = new BookParams();
		bookParams.setExamDate(exam_date);
		bookParams.setExamTimeIntervalId(Integer.parseInt(examTime_interval_id));
		bookParams.setAccountId(account_id);
		bookParams.setMealId(bookMealId);
		bookParams.setNeedPaperReport(needpaperreport);
		bookParams.setSource(2);

		int examinerId = 0;
		boolean is_self = false;
		if(checkdb){
			String sql = "select id,is_self from tb_examiner where organization_id = "+fromSite + " and customer_id = "+defaccountId +"  and is_delete = 0 order by update_time desc limit 1";
			log.info(sql);
			List<Map<String,Object>> dblist = DBMapper.query(sql);
			if(dblist != null && dblist.size() >0){
				examinerId = Integer.parseInt(dblist.get(0).get("id").toString());
				is_self =  Integer.parseInt(dblist.get(0).get("is_self").toString()) == 0?false:true;
			}
		}
		if(examinerId == 0 ){
			log.error("没有体检人，请先添加体检人");
			return;
		}
		bookParams.setExaminerId(examinerId);
		bookParams.setInformMe(is_self);


		// 获取单位/体检中心人数
		List<HospitalCapacityUsed> hospitalCounter = CounterChecker.getHospitalCount(defHospital.getId(), exam_date)
				.stream().filter(hcu -> hcu.getPeriodId().intValue() == Integer.valueOf(examTime_interval_id))
				.collect(Collectors.toList());

		List<Integer> finalItemIds = ResourceChecker.getExamIdList(itemsInOrder.getFinalItems());
		bookParams.setItemIds(finalItemIds);
		//最终单项之和
		long finalItemPrice = ResourceChecker.getExamListDiscountPrice(itemsInOrder.getFinalItems(),defHospitalId,bookMeal.getDiscount(),bookMeal.getMealSetting().getAdjustPrice());

		HttpResult response = httpclient.post(Flag.MAIN, MainOrder_Book, params,JSON.toJSONString(bookParams));
		log.info("bookTest...." + response.getBody());
		Assert.assertEquals(response.getCode(), HttpStatus.SC_OK);
		//检查下单返回值，异常处理
		OrderChecker.checkMainOrderBookResponse(httpclient,response,params,JSON.toJSONString(bookParams),defaccountId,examinerId,defHospitalId);
		commOrderId = Integer.parseInt(JsonPath.read(response.getBody(),"$.orderId").toString());
		// int newOrderId = JsonPath.read(response.getBody(), "$.orderId");

		// Assert
		Assert.assertNotNull(commOrderId);
		orderId.add(commOrderId);
		log.info("[orderIds:]////" + orderId);
		if (checkdb) {
			Order order = OrderChecker.getOrderInfo(commOrderId);
			Assert.assertEquals(sdf.format(order.getExamDate()), exam_date);
			Assert.assertEquals(order.getNeedPaperReport().toString(), needpaperreport.toString());
			if(!pString.equals(""))
				Assert.assertEquals(order.getManagerId().intValue(),AccountChecker.getPromotionManagerId(pString,fromSite)); //P参数的客户经理
			else
				Assert.assertEquals(order.getManagerId().intValue(),HospitalChecker.getHospitalById(fromSite).getDefaultManagerId().intValue()); //站点的客户经理
			//获取体检中心默认的客户经理
//			Hospital hospital = HospitalChecker.getHospitalIdBySite(site);
//			int defaultManagerId = AccountChecker.getDefMangerIdOfHospital(hospital.getId());
//			Assert.assertEquals(order.getManagerId().intValue(),defaultManagerId); //非卡进入=站点默认客户经理
			@SuppressWarnings("deprecation")
			String package_snapshot_detail = order.getPackageSnapshotDetail();
			log.info("orderId...." + order.getId() + "pack...." + package_snapshot_detail);

			List<HospitalCapacityUsed> hospitalCounter1 = CounterChecker.getHospitalCount(defHospital.getId(), exam_date)
					.stream().filter(hcu -> hcu.getPeriodId().intValue() == Integer.valueOf(examTime_interval_id))
					.collect(Collectors.toList());
			CounterChecker.reduceCounter(-1, null, null, hospitalCounter, hospitalCounter1, 1);// 验证新体检日人数扣除

//			Double mealItemPrice = 0.0;

			// 验证订单操作日志
			List<ExamOrderOperateLogDO> logs = OrderChecker.getOrderOperatrLog(order.getOrderNum());
			Assert.assertEquals(logs.size(), 1);
			Assert.assertEquals(logs.get(0).getType().intValue(), OrderOperateTypeEnum.BOOK_ORDER.getCode());
			Assert.assertEquals(logs.get(0).getOrderStatus().intValue(), OrderStatus.NOT_PAY.intValue());
			Assert.assertEquals(logs.get(0).getSystem().intValue(), OperateAppEnum.CLIENT.getCode());

			// 验证itemDetail、mealDetail
			OrderMealSnapshot orderMealSnapshot = new OrderMealSnapshot();
			orderMealSnapshot = OrderChecker.getMealSnapShotByOrder(commOrderId);
			if(orderMealSnapshot != null){
				// 验证itemDetail
				List<ExamItemSnapshot> itemSnapshot = orderMealSnapshot.getExamItemSnapList();
				// 根据项目ID排序
				itemSnapshot = itemSnapshot.stream().sorted(Comparator.comparing(ExamItemSnapshot::getId))
						.collect(Collectors.toList());
				itemsInOrder.setAllRelatedItems(itemsInOrder.getAllRelatedItems().stream()
						.sorted(Comparator.comparing(ExamItem::getId)).collect(Collectors.toList()));

				Assert.assertEquals(itemSnapshot.size(), itemsInOrder.getAllRelatedItems().size());

				// 验证数据库中单项信息正确(所有单项，最终套餐内项目，套餐内减项，套餐外加项，重复项，套餐)
				ResourceChecker.checkItemByTypeFromMysql(itemSnapshot, itemsInOrder, bookMeal);
			}



			// 验证mealDetail
			MealSnapshot mealSnapshot = orderMealSnapshot.getMealSnapshot();
			Assert.assertEquals(mealSnapshot.getOriginMeal().getClass(), bookMeal.getClass());

			if (checkmongo) {
				waitto(mongoWaitTime);
				List<Map<String, Object>> monlist = MongoDBUtils.query("{'id':" + order.getId() + "}",
						MONGO_COLLECTION);
				Assert.assertNotNull(monlist);
				Assert.assertEquals(sdf.format(monlist.get(0).get("examDate")), exam_date);
				// 验证itemDetail
				net.sf.json.JSONObject examItemSnapshotInMongoMeal = OrderChecker.getMongoMealSnapshot(monlist.get(0).get("orderNum").toString(),"examItemSnapshot");
				String examItemSnapStr = examItemSnapshotInMongoMeal.getString("value");
				// 验证itemDetail
				JSONArray itemDetail_ja = JSONArray.fromObject(examItemSnapStr);
				sort(itemDetail_ja, "id", true);
				// 验证Mongo中单项信息正确
				ResourceChecker.checkItemByTypeFromMONGO(itemDetail_ja, itemsInOrder, bookMeal);

				// 验证mealDetail
				net.sf.json.JSONObject mealSnapshotInMongoMeal = OrderChecker.getMongoMealSnapshot(monlist.get(0).get("orderNum").toString(),"mealSnapshot");
				String mealSnapshotStr = mealSnapshotInMongoMeal.getString("value");
				JSONObject mealSnapshot_jo = JSONObject.fromObject(mealSnapshot);
				JSONObject originMeal_js = JSONObject.fromObject(mealSnapshot_jo.get("originMeal").toString());
				Assert.assertEquals(originMeal_js.get("id"), bookMeal.getId());
//				Assert.assertEquals(originMeal_js.get("price").toString(), finalItemPrice+"");去掉》》》
				Assert.assertEquals(Integer.parseInt(originMeal_js.get("price").toString()), bookMeal.getPrice().intValue());

			}
			//校验结算
			OrderChecker.check_Book_ExamOrderSettlement(order);
		}

		// 校验支付页面
		PayChecker.checkPayPage(httpclient, commOrderId, -1,defSite,defaccountId);
		System.out.println("-----------------------01_C端下单使用P参数(不带加项包的订单)End----------------------------");
	}


	@AfterTest(alwaysRun = true, description = "撤销删除订单", dependsOnGroups = { "qa" })
	public void afterTest() throws Exception {
		System.out.println("-----------------------开始撤销订单-----------------------");
		for (Integer oid : orderId) {
			log.info("group...." + oid);
			Order order1 = OrderChecker.getOrderInfo(oid);
			if (order1.getStatus() == OrderStatus.ALREADY_BOOKED.intValue()
					|| order1.getStatus() == OrderStatus.ALREADY_PAY.intValue()
					|| order1.getStatus() == OrderStatus.NOT_PAY.intValue()
					|| order1.getStatus() == OrderStatus.PAYING.intValue()
					|| order1.getStatus() == OrderStatus.SITE_PAY.intValue()) {

				List<HospitalCapacityUsed> hospitalCounter = CounterChecker
						.getHospitalCount(defHospital.getId(), sdf.format(order1.getExamDate())).stream()
						.filter(hcu -> hcu.getPeriodId().intValue() == order1.getExamTimeIntervalId().intValue())
						.collect(Collectors.toList());
				List<CompanyCapacityUsed> companyCounter = null;
				companyCounter = CounterChecker.getCompanyCount(defHospital.getId(), order1.getExamCompanyId(),
								sdf.format(order1.getExamDate()))
						.stream().filter(ccu -> ccu.getPeriodId().intValue() == order1.getExamTimeIntervalId().intValue())
						.collect(Collectors.toList());

				// 验证订单操作日志
				List<ExamOrderOperateLogDO> logs1 = OrderChecker.getOrderOperatrLog(order1.getOrderNum());
				HttpResult response = httpclient.post(Flag.MAIN, MainRevokerOrder, oid);
				waitto(2);
				// 交易数据验证
				System.out.println("-----------------------开始校验撤销后订单交易数据 订单 " + order1.getId() + "..状态.."
						+ order1.getStatus() + "-----------------------");
				PayChecker.checkRevokeTrade(order1, order1.getStatus());
				System.out.println("-----------------------结束校验撤销后订单交易数据-----------------------");

				Assert.assertEquals(response.getCode(), HttpStatus.SC_OK, "错误提示：" + response.getBody());
				if (response.getCode() == HttpStatus.SC_OK) {
					Order order = OrderChecker.getOrderInfo(oid);
					System.err.println("查看这个订单的状态：" + order.getId() + "---" + order.getStatus());

					// 验证订单操作日志
					List<HospitalCapacityUsed> hospitalCounter1 = CounterChecker
							.getHospitalCount(defHospital.getId(), sdf.format(order1.getExamDate())).stream()
							.filter(hcu -> hcu.getPeriodId().intValue() == order1.getExamTimeIntervalId().intValue())
							.collect(Collectors.toList());
					List<CompanyCapacityUsed> companyCounter1 = null;
						companyCounter1 = CounterChecker
								.getCompanyCount(defHospital.getId(), order1.getExamCompanyId(),
										sdf.format(order1.getExamDate()))
								.stream().filter(ccu -> ccu.getPeriodId().intValue() == order1.getExamTimeIntervalId().intValue())
								.collect(Collectors.toList());
					CounterChecker.recycleCounterCheck(-1, companyCounter, companyCounter1, hospitalCounter,
							hospitalCounter1, 1);

					/*// 验证订单操作日志
					List<ExamOrderOperateLogDO> logs = OrderChecker.getOrderOperatrLog(order.getOrderNum());
					Assert.assertEquals(logs.get(0).getType().intValue(), OrderOperateTypeEnum.CANCEL_ORDRE.getCode());
					Assert.assertEquals(logs.get(0).getOrderStatus().intValue(), OrderStatus.REVOCATION.intValue());
					Assert.assertEquals(logs.get(0).getSystem().intValue(), OperateAppEnum.CLIENT.getCode());*/
					System.out.println(oid + "撤销成功！");
				} else {
					System.err.println(oid + "撤销失败！");
				}
				HttpResult result = httpclient.post(Flag.MAIN, MainDeleteOrder, oid);
				Assert.assertEquals(result.getCode(), HttpStatus.SC_OK, "错误提示：" + result.getBody());
				if (result.getCode() == HttpStatus.SC_OK) {
					Order order = OrderChecker.getOrderInfo(oid);
					// 验证订单操作日志
					List<ExamOrderOperateLogDO> logs = OrderChecker.getOrderOperatrLog(order.getOrderNum());
					Assert.assertEquals(logs.get(0).getType().intValue(), OrderOperateTypeEnum.DELETE_ORDER.getCode());
					Assert.assertEquals(logs.get(0).getOrderStatus().intValue(), OrderStatus.DELETED.intValue());
					Assert.assertEquals(logs.get(0).getSystem().intValue(), OperateAppEnum.CLIENT.getCode());
					System.out.println(oid + "删除成功！");
				} else
					System.err.println(oid + "删除失败！");
			}
		}
		System.out.println("-----------------------撤销订单结束-----------------------");
	}


	@DataProvider
	public Iterator<String[]> book() {
		try {
			return CvsFileUtil.fromCvsFileToIterator("./csv/order/mainBookOrder.csv", 8);
		} catch (FileNotFoundException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		return null;
	}

	@DataProvider
	public Iterator<String[]> book_pack() {
		try {
			return CvsFileUtil.fromCvsFileToIterator("./csv/order/mainBookOrder_pack.csv", 8);
		} catch (FileNotFoundException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		return null;
	}

	@DataProvider
	public Iterator<String[]> book_card() {
		try {
			return CvsFileUtil.fromCvsFileToIterator("./csv/order/mainBookOrder_entrycard.csv", 8);
		} catch (FileNotFoundException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		return null;
	}

	@DataProvider(name = "channelBook")
	public Iterator<String[]> channelBook() {
		try {
			return CvsFileUtil.fromCvsFileToIterator("./csv/order/channel/channelBookOrder.csv", 8);
		} catch (FileNotFoundException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		return null;
	}


	@DataProvider
	public Iterator<String[]> book_p() {
		try {
			return CvsFileUtil.fromCvsFileToIterator("./csv/order/mainBookOrder_promotion.csv", 8);
		} catch (FileNotFoundException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		return null;
	}

	@DataProvider
	public Iterator<String[]> book_pr() {
		try {
			return CvsFileUtil.fromCvsFileToIterator("./csv/order/mainBookOrder_promotion_1.csv", 8);
		} catch (FileNotFoundException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		return null;
	}


	@DataProvider
	public Iterator<String[]> book_prk() {
		try {
			return CvsFileUtil.fromCvsFileToIterator("./csv/order/mainBookOrder_promotion_2.csv", 8);
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
