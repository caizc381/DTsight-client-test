package com.tijiantest.testcase.main.card;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.tijiantest.model.hospital.HospitalParam;
import com.tijiantest.model.order.*;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.tijiantest.base.Flag;
import com.tijiantest.base.HttpResult;
import com.tijiantest.base.dbcheck.CardChecker;
import com.tijiantest.base.dbcheck.HospitalChecker;
import com.tijiantest.base.dbcheck.OrderChecker;
import com.tijiantest.base.dbcheck.PayChecker;
import com.tijiantest.base.dbcheck.ResourceChecker;
import com.tijiantest.model.resource.meal.Meal;
import com.tijiantest.model.resource.meal.MealItem;
import com.tijiantest.testcase.main.MainBase;
import com.tijiantest.util.CvsFileUtil;
import com.tijiantest.util.db.DBMapper;
import com.tijiantest.util.db.MongoDBUtils;
import com.tijiantest.util.db.SqlException;

/**
 * 在C端绑定实体卡，并且用卡进行预约
 * @author huifang
 *
 */
public class UseEntityCardTest extends MainBase {

	private String entityCardNum = "";
	private String entityCardPasswd = "";
	private int entityCardId = 0;
	private int entityCardMeal = 0;
	private int newOrderId = 0;
	private String orderNum=null;
	@Test(description = "充值实体卡",groups = {"qa"},dependsOnGroups = "crm_entityCard",ignoreMissingDependencies = true)
	public void test_01_binding_card() throws SqlException{
		if(checkdb){
			//清理现有的实体卡
			DBMapper.update("update tb_card set status = 0 where account_id = "+defaccountId+" and card_num like 'MT%'");
			waitto(2);
			String sql = "select * from tb_card  where type = 2 and account_id is null and status = 1  order by id desc limit 1 ";
			log.info("sql:"+sql);
			List<Map<String,Object>> rets = DBMapper.query(sql);
			if(rets == null || rets.size()  == 0){
				log.error("无法充值实体卡，没有实体卡");
				return;
			}
			Map<String,Object> map = rets.get(0);
			entityCardId = Integer.parseInt(map.get("id").toString());
			entityCardNum = map.get("card_num").toString().substring(2);
			entityCardPasswd = map.get("password").toString();
		}
		List<NameValuePair> pairs = new ArrayList<>();
		pairs.add(new BasicNameValuePair("cardNum", entityCardNum));
		pairs.add(new BasicNameValuePair("password", entityCardPasswd));
		
		HttpResult result = httpclient.post(Flag.MAIN,Card_BindCard,pairs);
		log.info("resulst.."+result.getBody());
		Assert.assertEquals(result.getCode(),HttpStatus.SC_OK);
		Assert.assertEquals(result.getBody(),"{}");
		if(checkdb){
			String sql1 = "select * from tb_trade_account where ref_id = ?";
			List<Map<String,Object>> rets1 = DBMapper.query(sql1,defaccountId);
			String sql2 = "select * from tb_card where id = ?";
			List<Map<String,Object>> rets = DBMapper.query(sql2,entityCardId);
			Map<String,Object> map = rets.get(0);
			Assert.assertEquals(Integer.parseInt(map.get("account_id").toString()),defaccountId);
			Assert.assertEquals(Integer.parseInt(map.get("trade_account_id").toString()),Integer.parseInt(rets1.get(0).get("id").toString()));
		}
		
	}
	
	/**
	 * 使用实体卡下单，验证支付页面部分有Bug，需上线后打开checkPayPage
	 * @param args
	 * @throws Exception 
	 */
	@Test(description = "使用实体卡下单",groups = {"qa"},dependsOnMethods = "test_01_binding_card",dataProvider="entitybook")
	public void test_02_useCard_create_Order(String ...args) throws Exception{
		String exam_date = args[1];
		entityCardMeal = CardChecker.getEntityCardMeal(entityCardId, defGender).get(0);//查看卡绑定的女性的单位套餐
		Meal meal = ResourceChecker.getMealInfo(entityCardMeal);
		int examTime_interval_id = HospitalChecker.getHospitalPeriodSettings(meal.getHospitalId()).get(0).getId();
		log.info("套餐id:"+entityCardMeal);
		List<Integer> item_list = new ArrayList<Integer>();
		List<MealItem> mealitem = ResourceChecker.getMealInnerItemList(entityCardMeal);
		Boolean needpaperreport = Boolean.parseBoolean(args[5]);
		int fromSiteId = CardChecker.getCardById(entityCardId).getFromHospital();

		//1.form query
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		NameValuePair _p = new BasicNameValuePair("_p", "" + "");
		NameValuePair _site = new BasicNameValuePair("_site", "mt");
		NameValuePair _siteType = new BasicNameValuePair("_siteType", "mobile");
		params.add(_p);
		params.add(_site);
		params.add(_siteType);
		//2.json Object
		BookParams bookParams = new BookParams();
		bookParams.setExamDate(exam_date);
		bookParams.setExamTimeIntervalId(examTime_interval_id);
		bookParams.setAccountId(defaccountId);
		bookParams.setMealId(entityCardMeal);
		bookParams.setNeedPaperReport(needpaperreport);
		bookParams.setEntryCardId(entityCardId);
		bookParams.setSource(2);

		int examinerId = 0;
		boolean is_self = false;
		if(checkdb){
			String sql = "select id,is_self from tb_examiner where organization_id = "+fromSiteId + " and customer_id = "+defaccountId +"  and is_delete = 0 order by update_time desc limit 1";
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

	    for(int i=0;i<mealitem.size();i++){
			item_list.add(i, mealitem.get(i).getId());
			NameValuePair itemIds = new BasicNameValuePair("itemIds[]", item_list.get(i)+"");
	    }
	    bookParams.setItemIds(item_list);

	    HttpResult response = httpclient.post(Flag.MAIN,MainOrder_Book, params, JSON.toJSONString(bookParams));
	    Assert.assertEquals(response.getCode(), HttpStatus.SC_OK,"使用实体卡预约失败:"+response.getBody());
	    log.info("实体卡下单..."+response.getBody());
	    boolean needRebackSettings = false;
	    if(response.getBody().contains("仅预留日可约")){//变更单位为可约状态
	    	 log.info("单位设定仅预留日可约,开始设置为非预约日可约....");
	    	 int hospitalId = ResourceChecker.getMealInfo(entityCardMeal).getHospitalId();
	    	 int companyId = CardChecker.getCardInfo(entityCardId).getNewCompanyId();
	    	 System.out.println("hosptialId..."+hospitalId+"..."+companyId);
	    	 DBMapper.update("update tb_company_capacity_info set can_order = 1 where hospital_id = "+hospitalId +" and company_id = "+companyId);
	    	 needRebackSettings = true;
	    	 response = httpclient.post(Flag.MAIN,MainOrder_Book, params,JSON.toJSONString(bookParams));
	 	     Assert.assertEquals(response.getCode(), HttpStatus.SC_OK,"使用实体卡再次预约失败:"+response.getBody());
	 	     if(needRebackSettings)
		    	 DBMapper.update("update tb_company_capacity_info set can_order = 0 where hospital_id = "+hospitalId +" and company_id = "+companyId);
	 	    log.info("实体卡再次下单..."+response.getBody());
	 	    if(response.getBody().contains("仅预留日可约")){
	 	    	log.error("单位仅预留日可约，请手动设置单位预留，否则无法预约!!");
	 	    	throw new Exception("单位仅预留日可约，请手动设置单位预留，否则无法预约!!");
	 	    }
	    }
		//检查下单返回值，异常处理
		OrderChecker.checkMainOrderBookResponse(httpclient,response,params,JSON.toJSONString(bookParams),defaccountId,examinerId,defHospitalId);

	    //Assert
	    if(checkdb){
	    	waitto(3);
	    	String sql = "select * from tb_order where account_id = "+defaccountId+" and exam_date = '"+exam_date+"' order by id desc limit 1";
	    	log.info("实体卡查询sql.."+sql);
	    	List<Map<String,Object>> newlist = DBMapper.query(sql);
	    	newOrderId = Integer.parseInt(newlist.get(0).get("id").toString());
	    	orderNum = newlist.get(0).get("order_num").toString();
	    	Assert.assertEquals(newlist.size(),1);
	    	Assert.assertEquals(newlist.get(0).get("entry_card_id"),entityCardId);
	    	int newOrderId = Integer.parseInt(newlist.get(0).get("id").toString());
	    	//tb_paymentrecord,tb_paylog
	    	sql = "select * from tb_paymentrecord where order_id = ?";
	    	newlist = DBMapper.query(sql,newOrderId);
	    	Assert.assertEquals(newlist.size(),0);
	    	sql = "select * from tb_paylog where order_id = ?";
	    
	    	//验证订单操作日志
	    	List<ExamOrderOperateLogDO> logs = OrderChecker.getOrderOperatrLog(orderNum);
	    	Assert.assertEquals(logs.size(), 1);
	    	Assert.assertEquals(logs.get(0).getType().intValue(), OrderOperateTypeEnum.BOOK_ORDER.getCode());
	    	Assert.assertEquals(logs.get(0).getOrderStatus().intValue(), OrderStatus.NOT_PAY.intValue());
	    	Assert.assertEquals(logs.get(0).getSystem().intValue(), OperateAppEnum.CLIENT.getCode());
	    	
	    	newlist = DBMapper.query(sql,newOrderId);
	    	Assert.assertEquals(newlist.size(),0);
	    		if(checkmongo){
	    			waitto(mongoWaitTime);
					List<Map<String,Object>> monlist = MongoDBUtils.query("{'id':"+newOrderId+"}", MONGO_COLLECTION);
					Assert.assertEquals(monlist.size(),1); 		
					Assert.assertEquals(monlist.get(0).get("entryCardId"),entityCardId);
				}	
	       //强制修改卡的avaiable_date为10分钟之前时间，在支付页面会判断卡的available_date
	       /**这一段以后可能去掉,当开发判断卡过期时间不受avalable_date约束时***/
	    	
	       DBMapper.update("update tb_card set  available_date = DATE_ADD(now(),INTERVAL-10 MINUTE) where id  ="+entityCardId); 
	    }
	    //校验支付页面
	    PayChecker.checkPayPage(httpclient, newOrderId,entityCardId,defSite,defaccountId);
	  
	}
	
	@AfterClass(description = "撤销订单",dependsOnGroups={"qa"})
	public void afterTest(){
		if(newOrderId !=0 ){
			log.info("group...."+newOrderId);
			try {
				OrderChecker.Run_MainOrderRevokeOrder(httpclient, newOrderId, false, true, true);
			} catch (SqlException e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			}
		}
	}
	
	
	
	@DataProvider
	  public Iterator<String[]> entitybook() {
			try {
				return CvsFileUtil.fromCvsFileToIterator("./csv/order/mainBookWithCardOrder.csv", 6);
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
