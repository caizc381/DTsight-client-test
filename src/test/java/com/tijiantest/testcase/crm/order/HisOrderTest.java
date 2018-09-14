package com.tijiantest.testcase.crm.order;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.util.*;

import com.mongodb.BasicDBObject;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.tijiantest.base.Flag;
import com.tijiantest.base.HttpResult;
import com.tijiantest.base.MyHttpClient;
import com.tijiantest.base.dbcheck.AccountChecker;
import com.tijiantest.base.dbcheck.CompanyChecker;
import com.tijiantest.base.dbcheck.CounterChecker;
import com.tijiantest.base.dbcheck.HospitalChecker;
import com.tijiantest.base.dbcheck.OrderChecker;
import com.tijiantest.base.dbcheck.ResourceChecker;
import com.tijiantest.model.account.AccountGenderEnum;
import com.tijiantest.model.account.AddAccountTypeEnum;
import com.tijiantest.model.company.ChannelCompany;
import com.tijiantest.model.order.Order;
import com.tijiantest.model.settlement.SettlementHospitalConfirmEnum;
import com.tijiantest.model.resource.meal.Meal;
import com.tijiantest.testcase.crm.CrmMediaBase;
import com.tijiantest.util.CreateSimpleExcelToDisk;
import com.tijiantest.util.DateUtils;
import com.tijiantest.util.ExcelMember;
import com.tijiantest.util.db.MongoDBUtils;

/**
 * 浅对接回单测试
 * 步骤:平台客户经理创建订单->导出xls->xls退单(退单包括撤销订单&退项目2种)
 * 
 * @author huifang
 *
 */
public class HisOrderTest extends CrmMediaBase {

	public  static Order firstOrder = null; //撤订单
	public  static Order secondOrder = null; //撤订单
	public  static Order thirdOrder = null; //退项目
	public  static Order forthOrder = null; //退项目
	public  static Order fiveOrder = null; //体检完成
	public  static List<Order> hisOrderList = new ArrayList<Order>();
	private static String accountfileName = "./csv/opsRefund/company_account.xlsx";
	private static String xlsfileName = "./csv/order/hisExport"+UUID.randomUUID().hashCode()+".xlsx";
	private String hisItems = null;
	private int hisAccountId = 0; 
	private MyHttpClient platClient = new MyHttpClient();
	@Test(description = "平台客户经理CRM代预约",groups = {"qa"})
	public void test_01_createPlatOrder() throws Exception{
		Integer offSetDay = HospitalChecker.getPreviousBookDaysByHospitalId(defhospital.getId());
		Date start = DateUtils.offsetDay(offSetDay);
		Date end = DateUtils.offsetDestDay(start, 30);
		try {
			//创建导入用户xls
			JSONArray idCardNameList = AccountChecker.makeUploadXls(5,accountfileName);
			List<Integer> companyLists = CompanyChecker.getCompanysIdByManagerId(defPlatAccountId,true);
			int newCompanyId = 0;
			String newCompanyName = null;
			for(Integer i : companyLists){
				ChannelCompany channelCom = CompanyChecker.getChannelCompanyByCompanyId(i);
				if(channelCom.getPlatformCompanyId() > 5){
					newCompanyId = channelCom.getId();
					newCompanyName = channelCom.getName();
					break;
					}
			}
			onceLoginInSystem(platClient, Flag.CRM, defPlatUsername, defPlatPasswd);
			AccountChecker.uploadAccount(platClient, newCompanyId, defhospital.getId(), "autotest_回单测试",
					accountfileName,AddAccountTypeEnum.idCard,true);
			
			for(int i=0;i<idCardNameList.size();i++){
				JSONObject jo = (JSONObject)idCardNameList.get(i);
				String idCard = jo.getString("idCard");
				String name = jo.getString("name");
				hisAccountId = AccountChecker.getAccountId(idCard, name, "autotest_回单测试",defPlatAccountId);
				//预约当天
				Integer hCompanyId = CompanyChecker.getHospitalCompanyByChannelCompanyId(newCompanyId, defhospital.getId()).getId();
				Map<String,Object> dateMap = CounterChecker.getDateBookableFromStartDate(start, end, hCompanyId, defhospital.getId());
				String examDate = dateMap.get("examDate").toString();
				int dayRangeId = Integer.parseInt(dateMap.get("dayRangeId").toString());
				List<Meal> mealList = ResourceChecker.getOffcialMeal(defhospital.getId(), Arrays.asList(AccountGenderEnum.MALE.getCode(),AccountGenderEnum.Common.getCode()));
				int meal_id = -1;
				for(Meal meal : mealList) {//找到套餐有至少2个项目的进行下单
					int tMealId = meal.getId();
					log.info("meal_id" + tMealId);
					if (ResourceChecker.getMealInnerItemList(meal.getId()).size() > 1)
						meal_id = meal.getId();
				}
				Order hisOrder = OrderChecker.crm_createOrder(platClient, meal_id, hisAccountId, newCompanyId,newCompanyName,
						examDate,defhospital,dayRangeId);
				if(hisOrder.getId() != 0 )
					hisOrderList.add(hisOrder);
				if(hisOrderList.size() == 1 )
					firstOrder = hisOrder;
				if(hisOrderList.size() == 2)
					secondOrder = hisOrder;
				if(hisOrderList.size() == 3)
					thirdOrder = hisOrder;
				if(hisOrderList.size() == 4)
					forthOrder = hisOrder;
				if(hisOrderList.size() == 5)
					fiveOrder = hisOrder;
			}
			onceLogOutSystem(platClient, Flag.CRM);
		} catch (IOException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
	
		
	}
	
	@Test(description="导出为xls",groups = {"qa"},dependsOnMethods="test_01_createPlatOrder")
	public void test_02_exportPlatToHis(){
		if(hisOrderList == null || hisOrderList.size() == 0){
			log.info("没有可用的订单，无法导出为xls");
			return;
		}
		String orderStr = "";
		for(Order order : hisOrderList){
			orderStr += order.getId()+",";
		}
		System.out.println("...hisOrderList"+orderStr);
		int lenth = orderStr.length();
		orderStr = orderStr.substring(0, lenth-1);
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("hospitalId", defhospital.getId() + ""));
		NameValuePair nvp = new BasicNameValuePair("orderIds",
					"[" + orderStr + "]");
		params.add(nvp);
		
		HttpResult response = httpclient.post(Order_OrderInfoForExportXls, params);
		Assert.assertEquals(response.getCode(), HttpStatus.SC_OK,response.getBody());
		System.out.println("http返回.."+response.getBody() );
		for(Order order : hisOrderList){
		Assert.assertTrue(response.getBody().contains(order.getId()+""));
		}
		params.add(new BasicNameValuePair("readOnly","false"));
		response = httpclient.post(Order_ExportOrderXls, params);
		Assert.assertEquals(response.getCode(),HttpStatus.SC_OK,"接口返回.."+response.getBody());
		
		if(checkdb){
			waitto(2);
			for(Order order:hisOrderList){
				Assert.assertTrue(OrderChecker.getOrderInfo(order.getId()).getIsExport());
				if(checkmongo){
					List<Map<String,Object>> list = MongoDBUtils.query("{'id':"+order.getId()+"}", MONGO_COLLECTION);
					hisItems = ((BasicDBObject)list.get(0).get("orderExportExtInfo")).getString("hisItemIds");
					order.setHisItemIds(hisItems.replace(",", ";"));
				}
			}
			
		}
	}
	
	@Test(description="浅对接回单（平台生成订单）",groups = {"qa","crm_hisExportOrder"},dependsOnMethods="test_02_exportPlatToHis")
	public void test_03_recyclePlatOrder() throws Exception{
		if(hisOrderList == null || hisOrderList.size() == 0){
			log.info("没有可用的订单，无法进行浅对接回单~~~~");
			return;
		}
		List<ExcelMember> members = new ArrayList<ExcelMember>();
		for(int i=0;i < hisOrderList.size();i++){
			if(i==0 || i==1)//第一二条撤订单
			members.add(new ExcelMember(hisOrderList.get(i).getOrderNum(),"1",hisItems.replace(",", ";"),"撤订单"));
			if(i==2 || i==3){//第三四条退项目(如果项目只有1个不用退项目)
				String newHisItemStr = hisItems.replace(",", ";");
				List<Map<String,Double>> itemlist = changeItemStrToList(newHisItemStr);
				System.out.println(itemlist);
				if(itemlist.size()>1)//只有订单单项大于1个才退项目
					for(Map<String,Double> item:itemlist){
						System.out.println("item..."+item);
						Set<String>keys=item.keySet();
						Iterator<String> it = keys.iterator();
						boolean isDeleted = false;
						while(it.hasNext()){
							if(item.get(it.next()).doubleValue() > 0){
								ListRemoveObj(itemlist, item);
								isDeleted = true;
							}
					}
					if(isDeleted)break;
				}
				String refundStr = changeItemListToStr(itemlist);
				hisOrderList.get(i).setXlsItemIds(refundStr);
				members.add(new ExcelMember(hisOrderList.get(i).getOrderNum(),"1",refundStr,"退项目"));
			}
			if(i==4)
				members.add(new ExcelMember(hisOrderList.get(i).getOrderNum(),"1",hisItems.replace(",", ";"),"退项目"));
		}
		CreateSimpleExcelToDisk.createSimpleExcel(members, xlsfileName);
		File file = new File(xlsfileName);
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("hospitalId",defhospital.getId());
		HttpResult result = httpclient.upload(Order_UploadRefundExcel, params,file);
		log.info("response..."+result.getBody());
		Assert.assertEquals(result.getCode(),HttpStatus.SC_OK);
			
		//校验订单回单申请情况,回单改数据需要一点时间
		waitto(5);
		boolean f1 = OrderChecker.checkLowHISOrderRefundApply(firstOrder, "撤订单");
		boolean f2 = OrderChecker.checkLowHISOrderRefundApply(secondOrder,"撤订单");
		boolean f3 = OrderChecker.checkLowHISOrderRefundApply(thirdOrder, "退项目");
		boolean f4 = OrderChecker.checkLowHISOrderRefundApply(forthOrder, "退项目");
		boolean f5 = OrderChecker.checkLowHISOrderRefundApply(fiveOrder,  "退项目");
		//校验回单后结算相关（回单自动审批完成，结算需要设置退款，待审核状态不修改退款标记）
		if(f1)//自动审批
			OrderChecker.check_Order_NeedSettlementRefund(firstOrder,SettlementHospitalConfirmEnum.UNSETTLEMENT.getCode());
		else
			OrderChecker.check_Book_ExamOrderSettlement(firstOrder);
		if(f2)
			OrderChecker.check_Order_NeedSettlementRefund(secondOrder,SettlementHospitalConfirmEnum.UNSETTLEMENT.getCode());
		else
			OrderChecker.check_Book_ExamOrderSettlement(secondOrder);
		if(f3)
			if(thirdOrder.getXlsItemIds().split(";").length == thirdOrder.getHisItemIds().split(";").length)
				OrderChecker.check_AgreeRefund_ExamOrderSettlement(thirdOrder, 0,SettlementHospitalConfirmEnum.UNSETTLEMENT.getCode());//退款金额为0
			else
				OrderChecker.check_Order_NeedSettlementRefund(thirdOrder,SettlementHospitalConfirmEnum.UNSETTLEMENT.getCode());
		else
			OrderChecker.check_Book_ExamOrderSettlement(thirdOrder);
		if(f4)
			if(forthOrder.getXlsItemIds().split(";").length == forthOrder.getHisItemIds().split(";").length)
				OrderChecker.check_AgreeRefund_ExamOrderSettlement(forthOrder, 0,SettlementHospitalConfirmEnum.UNSETTLEMENT.getCode());//退款金额为0
			else
				OrderChecker.check_Order_NeedSettlementRefund(forthOrder,SettlementHospitalConfirmEnum.UNSETTLEMENT.getCode());
		else
			OrderChecker.check_Book_ExamOrderSettlement(forthOrder);
		if(f5)//体检完成,不退任何项目,自动审批
			OrderChecker.check_AgreeRefund_ExamOrderSettlement(fiveOrder, 0,SettlementHospitalConfirmEnum.UNSETTLEMENT.getCode());//退款金额为0
		else//手动审批
			OrderChecker.check_Book_ExamOrderSettlement(fiveOrder);

		
	}
	
	public static List<Map<String,Double>> ListRemoveObj(List<Map<String,Double>>list, Map<String,Double> o) {
		Iterator<Map<String,Double>>it = list.iterator();
		while (it.hasNext()) {
			if (it.next().equals(o))
				it.remove();
			;
		}
		return list;
	}
	
	public List<Map<String,Double>> changeItemStrToList(String hisItems){
		List<Map<String,Double>> hisItemLists = new ArrayList<Map<String,Double>>();
		String[] first = hisItems.split(";");
		for(String s : first){
			String[] second = s.split(":");
			Map<String,Double> map = new HashMap<String,Double>();
			map.put(second[0],Double.parseDouble(second[1]));
			hisItemLists.add(map);
		}
		return hisItemLists;
	}
	
	
	public  String changeItemListToStr(List<Map<String,Double>> list){
		String temp = "";
		for(Map<String,Double> item:list){
			Set<String>keys=item.keySet();
			Iterator<String> it = keys.iterator();
			while(it.hasNext()){
				String key = it.next();
				DecimalFormat df = new DecimalFormat("#0.00");
				temp += key +":"+df.format(item.get(key))+";";
			}
		}
		if(!temp.equals("")){
			int n = temp.length()-1;
			return temp.substring(0, n);
		}else
			return null;
		
	}

}
