package com.tijiantest.testcase.crm.order;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mongodb.BasicDBObject;
import com.tijiantest.model.order.*;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.alibaba.fastjson.JSON;
import com.jayway.jsonpath.JsonPath;
import com.tijiantest.base.HttpResult;
import com.tijiantest.base.dbcheck.AccountChecker;
import com.tijiantest.base.dbcheck.CompanyChecker;
import com.tijiantest.base.dbcheck.CounterChecker;
import com.tijiantest.base.dbcheck.HospitalChecker;
import com.tijiantest.base.dbcheck.OrderChecker;
import com.tijiantest.base.dbcheck.PayChecker;
import com.tijiantest.base.dbcheck.ResourceChecker;
import com.tijiantest.model.account.AccountRelationInCrm;
import com.tijiantest.model.account.AddAccountTypeEnum;
import com.tijiantest.model.company.HospitalCompany;
import com.tijiantest.model.counter.CompanyCapacityUsed;
import com.tijiantest.model.counter.HospitalCapacityUsed;
import com.tijiantest.model.hospital.HospitalParam;
import com.tijiantest.model.item.ExamItem;
import com.tijiantest.model.paylog.PayConsts;
import com.tijiantest.model.paylog.PayLog;
import com.tijiantest.model.resource.meal.Meal;
import com.tijiantest.testcase.crm.CrmBase;
import com.tijiantest.testcase.crm.CrmMediaBase;
import com.tijiantest.util.CvsFileUtil;
import com.tijiantest.util.ListUtil;
import com.tijiantest.util.db.DBMapper;
import com.tijiantest.util.db.MongoDBUtils;
import com.tijiantest.util.db.SqlException;

/**
 * 
 * 批量下单
 * @author huifang
 *
 */
public class BatchOrderTest extends CrmMediaBase{

	public static List<Order> orderList = new ArrayList<Order>();
	public static List<Integer> orderIdList = new ArrayList<Integer>();
	List<Integer>accountIntList = new ArrayList<Integer>();
	private int oldCompanyId = 0;
	HospitalCompany hospitalCompany = new HospitalCompany();
	public static List<Order> selfExamDateOrders = new ArrayList<>();
	@Test(description= "普通单位批量下单（母卡）",dataProvider="batchOrder_success",groups = {"qa"})
	public void test_01_order_success(String ...args) throws Exception{
		System.out.println("--------------------------批量下单开始-------------------------------------");
		List<Order> orders = new ArrayList<Order>();
		List<Integer> accounts = new ArrayList<Integer>();
		//idCard
		String idCards = args[1].replace("#", ",");
		//mealId
		int mealid = defCompanyCommMeal.getId();
		//examDate
		String examedate = args[3];
		//accountNames
		String accountnames = "\'"+args[4].replace("#","\',\'")+"\'";
		//group
		String group = args[5];
		//companyId
		oldCompanyId = Integer.parseInt(args[6]);//1259
		List<MealMultiChooseParam> mealMultiChooseParams = new ArrayList<>();


		hospitalCompany = CompanyChecker.getHCompanyByOldCompanyIdANDOrgId(oldCompanyId, defhospital.getId());
//		Integer organizationType = HospitalChecker.getOrganizationType(defhospital.getId());
		//step1:导入用户
		AccountChecker.uploadAccount(httpclient, hospitalCompany.getId(), defhospital.getId(), group,"./csv/order/batchOrder.xlsx",AddAccountTypeEnum.idCard);


		List<String> mobileList = new ArrayList<>();
	    //step2:获取导入用户accountid
	    String sql1 = "SELECT  DISTINCT a.id  ,r.mobile FROM tb_account a,tb_examiner r WHERE r.id_card in ("+idCards+") AND a.id = r.customer_id "
	    		+ "AND r.igroup = \'"+group+"\' AND r.name in ("+accountnames+") AND r.manager_id = ? order by r.update_time desc";
	    log.info("sql:"+sql1);
	    List<Map<String,Object>>aclist = DBMapper.query(sql1,defaccountId);
	    for(Map<String,Object> al : aclist){
	    	accounts.add((Integer)al.get("id"));
	    	if(al.get("mobile")!=null)
	    		mobileList.add(al.get("mobile").toString());
	    	accountIntList.add((Integer)al.get("id"));
	    }
	    
	    log.info("accountlist:"+accountIntList);
		Meal meal = ResourceChecker.getMealInfo(mealid);
		
		List<HospitalCapacityUsed> hospitalCounter = CounterChecker.getHospitalCount(defhospital.getId(),examedate);
		List<CompanyCapacityUsed> companyCounter = null;
		companyCounter = CounterChecker.getCompanyCount(defhospital.getId(),hospitalCompany.getId(),examedate);
		BookDto batchBody = new BookDto();
		batchBody.setHospitalId(defhospital.getId());
		batchBody.setHospitalName(defhospital.getName());
		batchBody.setMealId(meal.getId());
		batchBody.setMealName(meal.getName());
		batchBody.setMealGender(meal.getGender());
		batchBody.setMealPrice(meal.getPrice());
		batchBody.setCompanyId(hospitalCompany.getId());
		batchBody.setCompanyName(hospitalCompany.getName());
		batchBody.setAccountIdList(accounts);
		batchBody.setExamDate(examedate);
		batchBody.setSendMsg(true);//发送短信
		batchBody.setMutiChooseParams(mealMultiChooseParams);
		String smsMsg = "自定义模板测试~~你好，请准时来体检！！";
		batchBody.setSmsMsgTemplate(smsMsg);
		if(hospitalCompany.getPlatformCompanyId() !=null && hospitalCompany.getPlatformCompanyId()==2)
			batchBody.setSitePay(true);
		else
			batchBody.setSitePay(false);

		String beforeDate = simplehms.format(DBMapper.query("select now()").get(0).get("now()"));
		waitto(1);
		String jbody = JSON.toJSONString(batchBody);
		//step4:批量下单
		HttpResult response = httpclient.post(Order_BatchOrder, jbody);
		//判断下单是否异常，如果异常，先处理
		if(!response.getBody().contains("result")){			
			String exceptType = OrderChecker.orderExceptionAction(response,httpclient);
			if(exceptType!=null)
				response = httpclient.post(Order_BatchOrder, jbody);
		}
	    //Assert
		//Assert.assertEquals(response.getBody(), "{}");
		Assert.assertEquals(response.getCode() , HttpStatus.SC_OK);
		Integer processId = JsonPath.read(response.getBody(),"$.result");
		OrderChecker.waitBatchOrderProc(httpclient,processId.intValue());

		orders=OrderChecker.checkOrder(accounts);
		//check database
		if(checkdb){			
			//验证单位/体检中心人数			

			List<HospitalCapacityUsed> hospitalCounter1 = CounterChecker.getHospitalCount(defhospital.getId(),examedate);
			List<CompanyCapacityUsed> companyCounter1 = CounterChecker.getCompanyCount(defhospital.getId(),hospitalCompany.getId(),examedate);	
			CounterChecker.reduceCounter(-1,companyCounter,companyCounter1,hospitalCounter,hospitalCounter1,accounts.size());
			
			for(Order order : orders){
				Assert.assertTrue(order.getStatus() ==  OrderStatus.ALREADY_BOOKED.intValue()|| order.getStatus() == OrderStatus.ALREADY_PAY.intValue());
				if(checkmongo){
					waitto(mongoWaitTime);
					List<Map<String,Object>> list = MongoDBUtils.query("{'id':"+order.getId()+"}", MONGO_COLLECTION);
					Assert.assertNotNull(list);
					Assert.assertEquals(1, list.size());
				}
				
				for(PayLog paylog : PayChecker.getPaylog(order.getId(),PayConsts.TradeTypes.OrderPay)){
					Assert.assertEquals(Math.abs(paylog.getAmount()), meal.getPrice().longValue());
					log.debug("tradebodytype>>>>>>>>"+paylog.getTradeBodyType().intValue());
					Assert.assertEquals(paylog.getTradeBodyType().intValue(),PayConsts.TradeBodyTypes.Card);
					Assert.assertEquals(paylog.getTradeIndex().intValue(),1);
					Assert.assertEquals(paylog.getStatus(),PayConsts.TradeStatus.Successful);
					//Assert.assertEquals(paylog.getOperaterType().intValue(),PayConsts.OperaterTypes.Crm);
				}
				//验证结算
				OrderChecker.check_Book_ExamOrderSettlement(order);
				//验证交易
				PayChecker.checkCrmBatchOrderTradeOrder(defhospital.getId(), hospitalCompany, order,defaccountId );
				
			}
			//check sms 自定义模板短信
			String smsSql = "select * from tb_sms_send_record where mobile in ("+ListUtil.StringlistToString(mobileList)+") and insert_time >'"+beforeDate + "' order by id desc limit 3";
			log.info("smsSql..."+smsSql);
			List<Map<String,Object>> smsList = DBMapper.query(smsSql);
			Assert.assertTrue(smsList.size()>0,"验证发送预约短信失败，请查看trade-notify 或 gotone是否正常");
			Map<String,Object> smsMap = smsList.get(0);
			Assert.assertEquals(smsMap.get("content").toString(),smsMsg);
		}
		orderList.addAll(orders);
		System.out.println("--------------------------批量下单结束-------------------------------------");
	}
	
	@Test(description= "批量下单,订单中含有不同步单项价格的项目",dataProvider="batchOrder_success_syncPrice",groups = {"qa"},dependsOnGroups = "crm_addMCNSTest")
	public void test_02_order_sync_price(String ...args) throws Exception{
		System.out.println("批量下单,订单中含有不同步单项价格的项目开始............");
		List<Order> orders = new ArrayList<Order>();
		//mealId
		Meal meal = PrepareOrder.mealsContainNotSyncPrice.get(0);
		log.info("批量下单,订单中含有不同步单项价格的项目的套餐："+meal.getName()+"  "+meal.getGender());
		//examDate
		String examedate = args[3];
		//accountNames
//		String accountnames = "\'"+args[4].replace("#","\',\'")+"\'";
		//group
		String group = args[5];
		//companyId
		List<MealMultiChooseParam> mealMultiChooseParams = new ArrayList<>();
		if(!args[6].equals("null"))
			oldCompanyId = Integer.parseInt(args[6]);//1585
		
		hospitalCompany = CompanyChecker.getHCompanyByOldCompanyIdANDOrgId(oldCompanyId, defhospital.getId());
//		Integer organizationType = HospitalChecker.getOrganizationType(defhospital.getId());
		
		List<Integer> accounts = new ArrayList<Integer>();

///////////////////////////////////
		List<Order> hisOrderList = new ArrayList<Order>();
		String accountfileName = "./csv/opsRefund/batchOrder_syncPrice_x.xlsx";
//		int hisAccountId = 0;
		//创建导入用户xls
		JSONArray idCardNameList = AccountChecker.makeUploadXls(3,accountfileName);
		//step1:导入用户
		AccountChecker.uploadAccount(httpclient, hospitalCompany.getId(), defhospital.getId(), group, accountfileName,AddAccountTypeEnum.idCard);
		String idCards = "";
		String accountnames = "";
			for(int i=0;i<idCardNameList.size();i++) {
				JSONObject jo = (JSONObject) idCardNameList.get(i);
				String idCard = jo.getString("idCard");
				idCards += "'" +idCard + "',";
				String name = jo.getString("name");
				accountnames += "'" + name + "',";
//				hisAccountId = AccountChecker.getAccountId(idCard, name, group, defaccountId);
			}
			idCards = idCards.substring(0,idCards.length() -1);
			accountnames = accountnames.substring(0,accountnames.length()-1);

//		AccountChecker.uploadAccount(httpclient, hospitalCompany.getId(), defhospital.getId(), group,"./csv/order/batchOrder_syncPrice.xlsx",AddAccountTypeEnum.idCard);

	    
	    //step2:获取导入用户accountid
		List<String> mobileList = new ArrayList<>();
	    String sql1 = "SELECT  DISTINCT a.id,r.gender,r.mobile FROM tb_account a,tb_examiner r  WHERE r.id_card in ("+idCards+") AND a.id = r.customer_id "
	    		+ "AND r.igroup = \'"+group+"\' AND r.name in ("+accountnames+") AND r.manager_id = ? order by r.update_time desc";
	    log.info("sql:"+sql1);
	    List<Map<String,Object>>aclist = DBMapper.query(sql1,defaccountId);
	    for(Map<String,Object> al : aclist){
	    	accounts.add((Integer)al.get("id"));
	    	if(al.get("mobile")!=null)
	    		mobileList.add(al.get("mobile").toString());
	    	accountIntList.add((Integer)al.get("id"));
	    	
	    }
	    log.info("accounts:"+accounts);
	    log.info("accountlist:"+accountIntList);
	    
	    List<HospitalCapacityUsed> hospitalCounter = CounterChecker.getHospitalCount(defhospital.getId(),examedate);
		List<CompanyCapacityUsed> companyCounter = null;
//
////		BatchOrderBody batchBody = new BatchOrderBody(defhospital.getId(), defhospital.getName(),
////				meal.getId(), meal.getPrice(), meal.getGender(),meal.getName(), hospitalCompany.getId(),hospitalCompany.getName(), accounts, examedate);

		BookDto batchBody = new BookDto();
		batchBody.setHospitalId(defhospital.getId());
		batchBody.setHospitalName(defhospital.getName());
		batchBody.setMealId(meal.getId());
		batchBody.setMealName(meal.getName());
		batchBody.setMealGender(meal.getGender());
		batchBody.setMealPrice(meal.getPrice());
		batchBody.setCompanyId(hospitalCompany.getId());
		batchBody.setCompanyName(hospitalCompany.getName());
		batchBody.setAccountIdList(accounts);
		batchBody.setExamDate(examedate);
		batchBody.setSendMsg(false);//不发送短信
		batchBody.setMutiChooseParams(mealMultiChooseParams);

		if(hospitalCompany.getPlatformCompanyId() !=null && hospitalCompany.getPlatformCompanyId()==2)
			batchBody.setSitePay(true);
		else{
			batchBody.setSitePay(false);
		}
		companyCounter = CounterChecker.getCompanyCount(defhospital.getId(),hospitalCompany.getId(),examedate);
	
		String jbody = JSON.toJSONString(batchBody);

		String beforeDate = simplehms.format(DBMapper.query("select now()").get(0).get("now()"));

		//step4:批量下单
		HttpResult response = httpclient.post(Order_BatchOrder, jbody);
		//判断下单是否异常，如果异常，先处理
		if(!response.getBody().contains("result")){			
			String exceptType = OrderChecker.orderExceptionAction(response,httpclient);
			if(exceptType!=null)
				response = httpclient.post(Order_BatchOrder, jbody);
		}
	    //Assert
		//Assert.assertEquals(response.getBody(), "{}");
		Assert.assertEquals(response.getCode() , HttpStatus.SC_OK);
		Integer processId = JsonPath.read(response.getBody(),"$.result");

		OrderChecker.waitBatchOrderProc(httpclient,processId.intValue());

		orders=OrderChecker.checkOrder(accounts);
		//check database
		if(checkdb){
			
			//验证单位/体检中心人数			
			List<HospitalCapacityUsed> hospitalCounter1 = CounterChecker.getHospitalCount(defhospital.getId(),examedate);
			List<CompanyCapacityUsed> companyCounter1 = CounterChecker.getCompanyCount(defhospital.getId(),hospitalCompany.getId(),examedate);	
			CounterChecker.reduceCounter(-1,companyCounter,companyCounter1,hospitalCounter,hospitalCounter1,accounts.size());
			
			for(Order order : orders){
				Map<String,Object> setts = HospitalChecker.getHospitalSetting(defhospital.getId(),HospitalParam.NEED_LOCAL_PAY);
				if(batchBody.isSitePay() && setts.get(HospitalParam.NEED_LOCAL_PAY).toString().equals("1") )
						Assert.assertTrue(order.getStatus() == OrderStatus.SITE_PAY.intValue());
				else
					Assert.assertTrue(order.getStatus() == OrderStatus.ALREADY_BOOKED.intValue()
						|| order.getStatus() == OrderStatus.ALREADY_PAY.intValue());
				if(checkmongo){
					waitto(mongoWaitTime);
					List<Map<String,Object>> list = MongoDBUtils.query("{'id':"+order.getId()+"}", MONGO_COLLECTION);
					Assert.assertNotNull(list);
					Assert.assertEquals(1, list.size());
					//查询并验证不同步单项价格的单项价格为0
					List<String> items = new ArrayList<String>();
					items = ListUtil.StringToStringList((((BasicDBObject)(list.get(0).get("orderExportExtInfo"))).getString("hisItemIds")));
					List<String> notSPitem = new ArrayList<String>();
					for(String item : items){
						for(ExamItem ei : PrepareOrder.notSyncPriceItems){
							if(item.startsWith(ei.getHisItemId(), 0)){
								String d[] = item.split(":");
								for (int i = 0; i < d.length; i++) 
								{
									notSPitem.add(d[i]); 
							    }
								Assert.assertEquals(notSPitem.get(1), "0.00");
							    log.debug("notSPitem.get(1)>>>>>>>>......."+notSPitem.get(1));
							}
						}						
					}	
				}
				for(PayLog paylog : PayChecker.getPaylog(order.getId(),PayConsts.TradeTypes.OrderPay)){
					Assert.assertEquals(Math.abs(paylog.getAmount()), meal.getPrice().longValue());
					log.debug("tradebodytype>>>>>>>>"+paylog.getTradeBodyType().intValue());
					Assert.assertEquals(paylog.getTradeBodyType().intValue(),PayConsts.TradeBodyTypes.Card);
					Assert.assertEquals(paylog.getTradeIndex().intValue(),1);
					Assert.assertEquals(paylog.getStatus(),PayConsts.TradeStatus.Successful);
					//Assert.assertEquals(paylog.getOperaterType().intValue(),PayConsts.OperaterTypes.Crm);
				}
				
				//验证结算
				OrderChecker.check_Book_ExamOrderSettlement(order);
				//验证交易
				PayChecker.checkCrmBatchOrderTradeOrder(defhospital.getId(), hospitalCompany, order,defaccountId );
			}
			
			//验证没有短信接受
            if(mobileList!=null && mobileList.size()>0){
                String smsSql = "select * from tb_sms_send_record where mobile in ("+ListUtil.StringlistToString(mobileList)+") and insert_time >'"+beforeDate + "' order by id desc limit 3";
                log.info("smsSql..."+smsSql);
                List<Map<String,Object>> smsList = DBMapper.query(smsSql);
                Assert.assertEquals(smsList.size() , 0);
            }

		}
		orderList.addAll(orders);
		System.out.println("批量下单,订单中含有不同步单项价格的项目结束............");
	}

    @Test(description= "散客单位现场付款下单",dataProvider="sitePay_provider",groups = {"qa"})
	public void test_03_sitePay(String ...args) throws Exception{
		System.out.println("--------------------------散客单位现场付款下单开始-------------------------------------");
		List<Order> orders = new ArrayList<Order>();
		//idCard
		String idCards = args[1].replace("#", ",");
		//mealId
		int mealid = sankeCommonMeal.getId();
		//examDate
		String examedate = args[3];
		//accountNames
		String accountnames = "\'"+args[4].replace("#","\',\'")+"\'";
		//group
		String group = args[5];
		//companyId
		List<MealMultiChooseParam> mealMultiChooseParams = new ArrayList<>();
		oldCompanyId = Integer.valueOf(args[6]);//1585

		hospitalCompany = CompanyChecker.getHCompanyByOldCompanyIdANDOrgId(oldCompanyId, defhospital.getId());
//		Integer organizationType = HospitalChecker.getOrganizationType(defhospital.getId());
		
		//step1:导入用户
		AccountChecker.uploadAccount(httpclient, hospitalCompany.getId(), defhospital.getId(), group,"./csv/order/sitepay_1.xlsx",AddAccountTypeEnum.idCard);

	    
	    //step2:获取导入用户accountid
	    String sql1 = "SELECT  DISTINCT a.id FROM tb_account a,tb_examiner r WHERE r.id_card in ("+idCards+") AND a.id = r.customer_id "
	    		+ "AND r.igroup = \'"+group+"\' AND r.name in ("+accountnames+") AND r.manager_id = ? order by r.update_time desc";
	    log.info("sql:"+sql1);
	    
	    List<Map<String,Object>>aclist = DBMapper.query(sql1,defaccountId);
	    List<Integer> accounts = new ArrayList<Integer>();
	    for(Map<String,Object> al : aclist){
	    	accounts.add((Integer)al.get("id"));
	    	accountIntList.add((Integer)al.get("id"));
	    }
		Meal meal = ResourceChecker.getMealInfo(mealid);
		
		List<HospitalCapacityUsed> hospitalCounter = CounterChecker.getHospitalCount(defhospital.getId(),examedate);
		List<CompanyCapacityUsed> companyCounter = null;
		companyCounter = CounterChecker.getCompanyCount(defhospital.getId(),hospitalCompany.getId(),examedate);
			
		BatchOrderBody batchBody = new BatchOrderBody(defhospital.getId(), defhospital.getName(),
				meal.getId(), meal.getPrice(), meal.getGender(),meal.getName(), hospitalCompany.getId(),hospitalCompany.getName(), accounts, examedate);
		
		if(hospitalCompany.getPlatformCompanyId() !=null && hospitalCompany.getPlatformCompanyId()==2)
			batchBody.setSitePay(true);	
		else
			batchBody.setSitePay(false);
			
		String jbody = JSON.toJSONString(batchBody);
		
		//step4:批量下单
		HttpResult response = httpclient.post(Order_BatchOrder, jbody);
		//判断下单是否异常，如果异常，先处理
		if(!response.getBody().contains("result")){			
			String exceptType = OrderChecker.orderExceptionAction(response,httpclient);
			if(exceptType!=null)
				response = httpclient.post(Order_BatchOrder, jbody);
		}
	    //Assert
		//Assert.assertEquals(response.getBody(), "{}");
		Assert.assertEquals(response.getCode() , HttpStatus.SC_OK);
		Integer processId = JsonPath.read(response.getBody(),"$.result");

		OrderChecker.waitBatchOrderProc(httpclient,processId.intValue());

		int mealPrice=sankeCommonMeal.getPrice();
		//获取体检中心是否需要收款
		int need_local_pay = Integer.parseInt(HospitalChecker.getHospitalSetting(defhospital.getId(), HospitalParam.NEED_LOCAL_PAY).get(HospitalParam.NEED_LOCAL_PAY).toString());
		
		orders=OrderChecker.checkOrder(accounts);
		//验证单位/体检中心人数			
		List<HospitalCapacityUsed> hospitalCounter1 = CounterChecker.getHospitalCount(defhospital.getId(),examedate);
		List<CompanyCapacityUsed> companyCounter1 = CounterChecker.getCompanyCount(defhospital.getId(),hospitalCompany.getId(),examedate);	
		CounterChecker.reduceCounter(-1,companyCounter,companyCounter1,hospitalCounter,hospitalCounter1,accounts.size());
		
		for(Order order : orders){
			if(need_local_pay==1){
				if(checkdb){
					Assert.assertTrue(order.getStatus() ==  OrderStatus.SITE_PAY.intValue());
				}
				if(checkmongo){
					waitto(mongoWaitTime);
					List<Map<String,Object>> list = MongoDBUtils.query("{'id':"+order.getId()+"}", MONGO_COLLECTION);
					Assert.assertNotNull(list);
					Assert.assertEquals(1, list.size());
					Assert.assertEquals("11",list.get(0).get("status").toString());
					Assert.assertEquals(mealPrice/100+".00",list.get(0).get("offlinePayMoney").toString());					
				}
			}
			else{
				if(checkdb){
					Assert.assertTrue(order.getStatus() ==  OrderStatus.ALREADY_PAY.intValue() || order.getStatus() == OrderStatus.ALREADY_BOOKED.intValue());
				}
				if(checkmongo){
					waitto(mongoWaitTime);
					List<Map<String,Object>> list = MongoDBUtils.query("{'id':"+order.getId()+"}", MONGO_COLLECTION);
					Assert.assertNotNull(list);
					Assert.assertEquals(1, list.size());
					Assert.assertTrue(Integer.parseInt(list.get(0).get("status").toString())==1 || Integer.parseInt(list.get(0).get("status").toString())==2 );
					Assert.assertEquals(mealPrice/100+".00",list.get(0).get("offlinePayMoney").toString());					
				}
			}
			
			//验证结算
			OrderChecker.check_Book_ExamOrderSettlement(order);
			//验证交易
			PayChecker.checkCrmBatchOrderTradeOrder(defhospital.getId(), hospitalCompany, order,defaccountId );
		}
		orderList.addAll(orders);
		System.out.println("--------------------------散客单位现场付款下单结束-------------------------------------");
	}
	
	
	@Test(description = "自选日期订单，且隐藏订单价格", groups = { "qa","crm_selfExamDate" }, dataProvider = "selfExamDate")
	public void test_04_selfExamDate(String... args) throws Exception {
		System.out.println("--------------------------自选日期订单开始-------------------------------------");
		List<Order> orders = new ArrayList<Order>();
		// idCard
		String idCards = args[1].replace("#", ",");
		// mealId
		int mealid = sankeCommonMeal.getId();
		// examDate
//		String examedate = args[3];
		// accountNames
		String accountnames = "\'" + args[4].replace("#", "\',\'") + "\'";
		// group
		String group = args[5];
		// companyId
		oldCompanyId = Integer.parseInt(args[6]);
		// bookType
		String bookType = args[7];
		// changeDate
		String changeDate = args[8];
		// hidePrice
		String hidePrice = args[9];
		// reduceItem
		String reduceItem = args[10];
		// sitePay
		String sitePay = args[11];
		// timeRemarks
		String timeRemarks = args[12];
		// verifyFlags
		String verifyFlags = args[13];
		// smsMsgTemplate
		String smsMsgTemplate = args[14];
		
		hospitalCompany = CompanyChecker.getHCompanyByOldCompanyIdANDOrgId(oldCompanyId, defhospital.getId());
//		Integer organizationType = HospitalChecker.getOrganizationType(defhospital.getId());

		// step1:导入用户
		AccountChecker.uploadAccount(httpclient, hospitalCompany.getId(), defhospital.getId(), group, "./csv/order/selfExamDate.xlsx",AddAccountTypeEnum.idCard);

		// step2:获取导入用户accountid
		String sql1 = "SELECT  DISTINCT a.id FROM tb_account a,tb_examiner r WHERE r.id_card in (" + idCards
				+ ") AND a.id = r.customer_id " + "AND r.igroup = \'" + group + "\' AND r.name in (" + accountnames
				+ ") AND r.manager_id = ? order by r.update_time desc";
		log.info("sql:" + sql1);

		List<Map<String, Object>> aclist = DBMapper.query(sql1, defaccountId);
		List<Integer> accounts = new ArrayList<Integer>();
		for (Map<String, Object> al : aclist) {
			accounts.add((Integer) al.get("id"));
			accountIntList.add((Integer) al.get("id"));
		}
		Meal meal = ResourceChecker.getMealInfo(mealid);		
		BatchOrderBody batchBody = new BatchOrderBody();
		batchBody.setAccountIdList(accounts);
		batchBody.setBookType(Integer.parseInt(bookType));
		batchBody.setChangedate(Boolean.parseBoolean(changeDate));
		batchBody.setCompanyId(hospitalCompany.getId());
		batchBody.setCompanyName(hospitalCompany.getName());
		batchBody.setHidePrice(Boolean.parseBoolean(hidePrice));
		batchBody.setExamTimeIntervalId(null);
		batchBody.setHospitalId(defhospital.getId());
		batchBody.setHospitalName(defhospital.getName());
		batchBody.setMealGender(meal.getGender());
		batchBody.setMealId(meal.getId());
		batchBody.setMealName(meal.getName());
		batchBody.setMealPrice(meal.getPrice());
		batchBody.setQueryCondition("");
		batchBody.setReduceItem(Boolean.parseBoolean(reduceItem));
		if(hospitalCompany.getPlatformCompanyId() !=null && hospitalCompany.getPlatformCompanyId()==2){
			sitePay = "true";
			batchBody.setSitePay(true);
		}
		else
			batchBody.setSitePay(Boolean.parseBoolean(sitePay));
			
		
		batchBody.setTimeRemarks(timeRemarks);
		batchBody.setVerifyFlag(Boolean.parseBoolean(verifyFlags));
		batchBody.setSmsMsgTemplate(smsMsgTemplate);
		String jbody = JSON.toJSONString(batchBody);

		
		//step4:批量下单
		HttpResult response = httpclient.post(Order_BatchOrder, jbody);
		//判断下单是否异常，如果异常，先处理
		if(!response.getBody().contains("result")){			
			String exceptType = OrderChecker.orderExceptionAction(response,httpclient);
			if(exceptType!=null)
				response = httpclient.post(Order_BatchOrder, jbody);
		}
	    //Assert
		//Assert.assertEquals(response.getBody(), "{}");
		Assert.assertEquals(response.getCode() , HttpStatus.SC_OK);
		Integer processId = JsonPath.read(response.getBody(),"$.result");

		OrderChecker.waitBatchOrderProc(httpclient,processId.intValue());

		if (checkdb) {
			orders=OrderChecker.checkOrder(accounts);
			for (Order order : orders) {
				System.out.println("自选日期订单，且隐藏订单价格  订单ID:"+order.getId());
				Assert.assertNull(order.getExamDate());
				String orderSql = "select * from tb_order o LEFT JOIN tb_order_batch b on o.batch_id = b.id where o.id=?";
				List<Map<String, Object>> orderList = DBMapper.query(orderSql, order.getId());
				Assert.assertEquals(hidePrice.equals("true")?1:0, orderList.get(0).get("is_hide_price"));
				Assert.assertEquals(reduceItem.equals("true")?1:0, orderList.get(0).get("is_reduce_item"));
				Assert.assertEquals(sitePay.equals("true")?1:0, orderList.get(0).get("is_site_pay"));
				
				if (checkmongo) {
					List<Map<String,Object>> list = MongoDBUtils.query("{'id':"+order.getId()+"}", MONGO_COLLECTION);
					Assert.assertNotNull(list);
					Assert.assertEquals(1, list.size());
					Assert.assertNull(list.get(0).get("examDate"));
				}
				//验证结算
				OrderChecker.check_Book_ExamOrderSettlement(order);
				//验证交易
				PayChecker.checkCrmBatchOrderTradeOrder(defhospital.getId(), hospitalCompany, order,defaccountId );
			}		
		}
		orderList.addAll(orders);
		selfExamDateOrders =orders;
		System.out.println("--------------------------自选日期订单结束-------------------------------------");
	}
	
	//@AfterTest(groups = { "qa" })
	public void afterTests() throws SqlException {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		log.info("orderList:" + orderList);
		String orderIds = "";
		for (Order order : orderList) {
			orderIds = orderIds +order.getId()+",";
			orderIdList.add(order.getId());
		}
		//验证订单撤销
		OrderChecker.Run_CrmOrderRevokeOrder(httpclient, orderIdList, false, true, true);
		//验证用户关系
		if (checkdb) {
			waitto(mysqlWaitTime);
			List<Integer> accountList = new ArrayList<Integer>();
			String sql = "SELECT * FROM tb_order WHERE id in (" + orderIds.substring(0, orderIds.length() - 1) + ")";
			List<Map<String, Object>> retlist = DBMapper.query(sql);
			for (Map<String, Object> r : retlist) {
				Integer accId = Integer.parseInt(r.get("account_id").toString());
				accountList.add(accId);
			}

			for (AccountRelationInCrm arc : AccountChecker.checkAccRelation(accountList, hospitalCompany.getId(),defaccountId)) {
				Assert.assertEquals(arc.getRecentMeal(), "");
				Assert.assertEquals(arc.getRecentOrderDate().toString(), "1999-09-09 09:09:09.0");
			}
		}
		log.info("用户相关最近预约记录已清空!");

		/***** 移除用户 ******/
		String acStr = "";
		for (Integer ai : accountIntList) {
			acStr = acStr + ai + ",";
		}
		int organizationId = CrmBase.defhospital.getId();
		int organizationType = HospitalChecker.getOrganizationType(organizationId);
		List<NameValuePair> paramss = new ArrayList<NameValuePair>();

		paramss.add(new BasicNameValuePair("accountIds", acStr.substring(0, acStr.length() - 1)+""));
		paramss.add(new BasicNameValuePair("newCompanyId", hospitalCompany.getId()+""));
		paramss.add(new BasicNameValuePair("organizationType", organizationType+""));
		HttpResult delete = httpclient.post(Account_RemoveCustomer, params);
		// assert
		Assert.assertEquals(delete.getCode(), HttpStatus.SC_OK);
		Assert.assertEquals(delete.getBody(), "{\"result\": " + accountIntList.size() + "}");

	}
	
	
	@DataProvider
	public Iterator<String[]> batchOrder_success(){
		try {
			return CvsFileUtil.fromCvsFileToIterator("./csv/order/batchOrder_success.csv",10);
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
	public Iterator<String[]> batchOrder_success_syncPrice(){
		try {
			return CvsFileUtil.fromCvsFileToIterator("./csv/order/batchOrder_success_syncPrice.csv",10);
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
	public Iterator<String[]> sitePay_provider(){
		try {
			return CvsFileUtil.fromCvsFileToIterator("./csv/order/sitepay_2.csv",10);
		} catch (FileNotFoundException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		return null;
	}
	
	@DataProvider(name="selfExamDate")
	public Iterator<String[]> selfExamDate(){
		try {
			return CvsFileUtil.fromCvsFileToIterator("./csv/order/selfExamDate.csv",10);
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