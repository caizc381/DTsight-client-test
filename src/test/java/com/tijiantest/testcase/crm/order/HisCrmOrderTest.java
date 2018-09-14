package com.tijiantest.testcase.crm.order;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jayway.jsonpath.JsonPath;
import com.mongodb.BasicDBObject;
import com.tijiantest.base.HttpResult;
import com.tijiantest.base.dbcheck.AccountChecker;
import com.tijiantest.base.dbcheck.OrderChecker;
import com.tijiantest.model.account.AddAccountTypeEnum;
import com.tijiantest.model.order.MongoOrder;
import com.tijiantest.model.order.Order;
import com.tijiantest.model.settlement.SettlementHospitalConfirmEnum;
import com.tijiantest.testcase.crm.CrmMediaBase;
import com.tijiantest.util.CreateSimpleExcelToDisk;
import com.tijiantest.util.ExcelMember;
import com.tijiantest.util.db.MongoDBUtils;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;

/**
 * 浅对接回单测试
 * 步骤:医院普通单位创建订单->导出xls->xls退单(退单包括撤销订单&退项目2种)
 *
 *
 * 注意：批量回单会因为母卡报错类似：
 * card pay refund error! orderNum: 20180111161706365002632; orderNumVersion: 203801; tradePayRecord sn: 180111041706616632156334688292 ; excption: com.mytijian.exception.BizException: 母卡支付获取分布式锁失
 *
 * 目前一条一条的回单验证基本功能
 * @author huifang
 *
 */
public class HisCrmOrderTest extends CrmMediaBase {

	public static int orderLength = 3;
	public  static Order crmfirstOrder = null;   //撤订单
	public  static Order crmsecondOrder = null;  //退项目
	public  static Order crmthirdOrder = null; //体检完成
	public  static List<Order> hisOrderList = new ArrayList<Order>();
	private static String accountfileName = "./csv/opsRefund/company_account.xlsx";

	private String hisItems = null;
	private int hisAccountId = 0; 
	@Test(description = "CRM普通单位代预约",groups = {"qa"})
	public void test_01_createOrder() throws Exception{
		try {
			//创建导入用户xls
			JSONArray idCardNameList = AccountChecker.makeUploadXls(orderLength,accountfileName);
			AccountChecker.uploadAccount(httpclient, defnewcompany.getId(), defhospital.getId(), "autotest_回单测试",
					accountfileName,AddAccountTypeEnum.idCard);
			
			for(int i=0;i<idCardNameList.size();i++){
				JSONObject jo = (JSONObject)idCardNameList.get(i);
				String idCard = jo.getString("idCard");
				String name = jo.getString("name");
				hisAccountId = AccountChecker.getAccountId(idCard, name, "autotest_回单测试",defaccountId,defhospital.getId());
				//预约当天
				String examDate = sdf.format(new Date());
				
				Order hisOrder = OrderChecker.crm_createOrder(httpclient, defCompanyMaleMeal.getId(), hisAccountId, defnewcompany.getId(),defnewcompany.getName(),
						examDate,defhospital);
				hisOrderList.add(hisOrder);
				if(hisOrderList.size() == 1 )
					crmfirstOrder = hisOrder;
				if(hisOrderList.size() == 2)
					crmsecondOrder = hisOrder;
				if(hisOrderList.size() == 3)
					crmthirdOrder = hisOrder;

			}
			
		} catch (IOException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
	
		
	}



	@Test(description = "CRM->首页->订单查询->导出xls",groups = {"qa"},dependsOnMethods = "test_01_createOrder")
	public void test_02_orderInfoForExport(){
		String orderStr = "";
		for(Order order : hisOrderList){
			orderStr += order.getId()+",";
		}
		int lenth = orderStr.length();
		orderStr = orderStr.substring(0, lenth-1);

		HttpResult response = httpclient.post(Order_OrderInfoForExport, "[" + orderStr + "]");
		String body = response.getBody();
		log.info(body);
		Assert.assertEquals(response.getCode(), HttpStatus.SC_OK,"杭辽日常管理->订单浅对接导出查看xls出错");

		List<MongoOrder> retmongoList = JSONArray.parseArray(body,MongoOrder.class);
		Assert.assertEquals(retmongoList.size(),hisOrderList.size());
		Collections.sort(hisOrderList, new Comparator<Order>() {
			@Override
			public int compare(Order o1, Order o2) {
				return o1.getId() - o2.getId();
			}
		});

		Collections.sort(retmongoList, new Comparator<MongoOrder>() {
			@Override
			public int compare(MongoOrder o1, MongoOrder o2) {
				return o1.getId() - o2.getId();
			}
		});
		for(int i = 0;i<hisOrderList.size();i++)
			Assert.assertEquals(hisOrderList.get(i).getId(),retmongoList.get(i).getId());

	}

	@Test(description = "CRM->订单管理->导出至内网,查询订单是否可导出为xls",groups = {"qa"},dependsOnMethods = "test_02_orderInfoForExport")
	public void test_02_getOrderCanExportXls(){
		String orderStr = "";
		for(Order order : hisOrderList){
			orderStr += order.getId()+",";
		}
		int lenth = orderStr.length();
		orderStr = orderStr.substring(0, lenth-1);
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("hospitalId", defhospital.getId() + ""));
		NameValuePair nvp = new BasicNameValuePair("orderIds",
				"[" + orderStr + "]");
		params.add(nvp);

		HttpResult response = httpclient.post(Order_GetOrderCanExportXls, params);
		Assert.assertEquals(response.getCode(), HttpStatus.SC_OK,"浅对接导出查看xls出错");
		String body = response.getBody();
		log.info(body);
		int num = Integer.parseInt(JsonPath.read(body,"$.num").toString());
		List<Integer> orderIds = JSONArray.parseArray(JsonPath.read(body,"$.orderIds").toString(),Integer.class);
		for(Order order : hisOrderList)
			Assert.assertTrue(orderIds.contains(order.getId()),"返回订单列表是"+orderIds+"..但是实际订单"+order.getId()+"不在其中,请检查！");;
		Assert.assertEquals(orderIds.size(),num);

	}


	@Test(description="导出为xls",groups = {"qa"},dependsOnMethods="test_02_getOrderCanExportXls")
	public void test_03_exportToHis(){
		String orderStr = "";
		for(Order order : hisOrderList){
			orderStr += order.getId()+",";
		}
		int lenth = orderStr.length();
		orderStr = orderStr.substring(0, lenth-1);
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("hospitalId", defhospital.getId() + ""));
		NameValuePair nvp = new BasicNameValuePair("orderIds",
					"[" + orderStr + "]");
		params.add(nvp);
		
		HttpResult response = httpclient.post(Order_OrderInfoForExportXls, params);
		Assert.assertEquals(response.getCode(), HttpStatus.SC_OK,"对接服务com.mytijian.mediator.order.inner.service.MediatorExportOrderService是否开启，请确认");
		for(Order order : hisOrderList){
		Assert.assertTrue(response.getBody().contains(order.getId()+""));
		}
		params.add(new BasicNameValuePair("readOnly","false"));
		response = httpclient.post(Order_ExportOrderXls, params);
		Assert.assertEquals(response.getCode(),HttpStatus.SC_OK,"接口返回..."+response.getBody());
		
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
	
	@Test(description="浅对接回单",groups = {"qa"},dependsOnMethods="test_03_exportToHis")
	public void test_04_recycleOrder() throws Exception{
		//单个xls文件只装载1条订单，因为后端防止并发退款会报错（并发数后端做的不好啊，在排期中。。。。）
		boolean isSencondOrderRefundItem = false;//第2个订单是否退单项
		for(int s = 1;s<=orderLength;s++){
			List<ExcelMember> members = new ArrayList<ExcelMember>();
			if(s%orderLength == 0){//撤销订单
				members.add(new ExcelMember(crmfirstOrder.getOrderNum(),"1",hisItems.replace(",", ";"),"撤订单"));
			}else if(s%orderLength == 1 ){//退项目
				String newHisItemStr = hisItems.replace(",", ";");
				List<Map<String,Double>> itemlist = changeItemStrToList(newHisItemStr);
				if(itemlist.size()>1){//只有订单单项大于1个才退项目
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
					isSencondOrderRefundItem = true;
				}
				String refundStr = changeItemListToStr(itemlist);
				crmsecondOrder.setXlsItemIds(refundStr);
				members.add(new ExcelMember(crmsecondOrder.getOrderNum(),"1",refundStr,"退项目"));
			}else if (s%orderLength == 2 ){//体检完成
				members.add(new ExcelMember(crmthirdOrder.getOrderNum(),"1",hisItems.replace(",", ";"),"退项目"));

			}
			String xlsfileName  = "./csv/order/hisCrmExport"+s+UUID.randomUUID().hashCode()+".xlsx";
			File file = new File(xlsfileName);
			if(file.exists())
				file.delete();
			CreateSimpleExcelToDisk.createSimpleExcel(members, xlsfileName);
			Map<String,Object> params = new HashMap<String,Object>();
			params.put("hospitalId",defhospital.getId());
			HttpResult result = httpclient.upload(Order_UploadRefundExcel, params,file);
			log.info("response..."+result.getBody());
			Assert.assertEquals(result.getCode(),HttpStatus.SC_OK);
		}

			
		//校验订单回单申请情况
		 waitto(5);//回单写这部分数据需要时间,需要等一点点时间
		boolean f1 = OrderChecker.checkLowHISOrderRefundApply(crmfirstOrder, "撤订单");
		boolean f2 = OrderChecker.checkLowHISOrderRefundApply(crmsecondOrder, "退项目");
		boolean f3 = OrderChecker.checkLowHISOrderRefundApply(crmthirdOrder,  "退项目");
		//校验回单后结算相关（回单自动审批完成，结算需要设置退款，待审核状态不修改退款标记）
		if(f1)//自动审批
			OrderChecker.check_Order_NeedSettlementRefund(crmfirstOrder,SettlementHospitalConfirmEnum.UNSETTLEMENT.getCode());
		else
			OrderChecker.check_Book_ExamOrderSettlement(crmfirstOrder);
		if(f2)
			if(isSencondOrderRefundItem)
				OrderChecker.check_Order_NeedSettlementRefund(crmsecondOrder,SettlementHospitalConfirmEnum.UNSETTLEMENT.getCode());
			else
				OrderChecker.check_AgreeRefund_ExamOrderSettlement(crmthirdOrder, 0,SettlementHospitalConfirmEnum.UNSETTLEMENT.getCode());//退款金额为0
		else
			OrderChecker.check_Book_ExamOrderSettlement(crmsecondOrder);
		if(f3)//体检完成,不退任何项目,自动审批
			OrderChecker.check_AgreeRefund_ExamOrderSettlement(crmthirdOrder, 0,SettlementHospitalConfirmEnum.UNSETTLEMENT.getCode());//退款金额为0
		else//手动审批
			OrderChecker.check_Book_ExamOrderSettlement(crmthirdOrder);

		
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