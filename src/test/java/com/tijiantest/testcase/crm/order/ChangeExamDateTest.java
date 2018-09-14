package com.tijiantest.testcase.crm.order;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.message.BasicNameValuePair;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.tijiantest.base.HttpResult;
import com.tijiantest.base.dbcheck.CounterChecker;
import com.tijiantest.base.dbcheck.OrderChecker;
import com.tijiantest.model.counter.HospitalCapacityUsed;
import com.tijiantest.model.order.ExamOrderOperateLogDO;
import com.tijiantest.model.order.OperateAppEnum;
import com.tijiantest.model.order.Order;
import com.tijiantest.model.order.OrderOperateTypeEnum;
import com.tijiantest.testcase.crm.CrmMediaBase;
import com.tijiantest.util.ChangeToDate;
import com.tijiantest.util.CvsFileUtil;
import com.tijiantest.util.ListUtil;
import com.tijiantest.util.db.DBMapper;
import com.tijiantest.util.db.MongoDBUtils;
import com.tijiantest.util.db.SqlException;

public class ChangeExamDateTest extends CrmMediaBase {
	private static List<Order> orderList = new ArrayList<Order>();
	List<Integer> ordersId = new ArrayList<>();
	public String examDate = null;
  @Test(dataProvider = "changeExamDate_success",groups = {"qa"})
  public void test_01_changeExamDate_success(String ...args) throws ParseException, IOException, java.text.ParseException {
	  orderList.add(sankeOrder);
	  
	  for(int i = 0;i<orderList.size();i++){
		  ordersId.add(orderList.get(i).getId());
	  }
	  
	  List<String> examDateList = Arrays.asList(args[2].split("#"));
	  List<Order> orderList = checkOrder(ordersId);
	  String orderDate = sdf.format(orderList.get(0).getExamDate());
	  if (orderDate.equals(examDateList.get(1))){
		  examDate = examDateList.get(0);
	  }
	  else{
		  examDate = examDateList.get(1);
	  }
	  int intervalId = orderList.get(0).getExamTimeIntervalId();
	  int expcode = Integer.parseInt(args[4]);
	  String message = args[5];
	  
	  List<HospitalCapacityUsed> oldDateCounter = CounterChecker.getHospitalCount(defhospital.getId(),orderDate);
	  List<HospitalCapacityUsed> newDateCounter = CounterChecker.getHospitalCount(defhospital.getId(),examDate);
//	  List<CompanyCapacityUsed> companyCounter = null;

	  List<NameValuePair> orderIds = new ArrayList<NameValuePair>();
	  for(int i=0;i<ordersId.size();i++){
		  NameValuePair nvp1 = new BasicNameValuePair("orderIds[]", ordersId.get(i)+"");
		  orderIds.add(nvp1);
		}
	  NameValuePair nvp2 = new BasicNameValuePair("examDate", examDate+"");
	  NameValuePair nvp3 = new BasicNameValuePair("intervalId", intervalId+"");
	  orderIds.add(nvp2);
	  orderIds.add(nvp3);
	  
	  HttpResult response = httpclient.post(Order_ChangeExamDate, orderIds);
	  
	//Assert
	  Assert.assertEquals(response.getBody(), message);
	  Assert.assertEquals(response.getCode(), expcode);
	  
		//database
		if(checkdb){
			
			List<HospitalCapacityUsed> oldDateCounter1 = CounterChecker.getHospitalCount(defhospital.getId(),orderDate);
			List<HospitalCapacityUsed> newDateCounter1 = CounterChecker.getHospitalCount(defhospital.getId(),examDate);
			CounterChecker.recycleCounterCheck(-1,null, null, oldDateCounter, oldDateCounter1, ordersId.size());//验证原体检日人数回收
			CounterChecker.reduceCounter(-1,null,null,newDateCounter,newDateCounter1,ordersId.size());//验证新体检日人数扣除
			List<Order> list = checkOrder(ordersId);
			for(Order order : list){
//				Date exam_date = order.getExamDate();
				
				//验证订单操作日志
				List<ExamOrderOperateLogDO> logs = OrderChecker.getOrderOperatrLog(order.getOrderNum());
			   	Assert.assertEquals(logs.get(0).getType().intValue(), OrderOperateTypeEnum.CHANGE_DATE.getCode());
			   	Assert.assertEquals(logs.get(0).getSystem().intValue(), OperateAppEnum.CRM.getCode());
				
				if(checkmongo){
					waitto(mongoWaitTime);
					List<Map<String,Object>> monlist = MongoDBUtils.query("{'id':"+order.getId()+"}", MONGO_COLLECTION);
					Assert.assertNotNull(monlist);
					Assert.assertEquals(sdf.format(monlist.get(0).get("examDate")),examDate);
				}
			}
	}
  }

  @DataProvider
	public Iterator<String[]> changeExamDate_success(){
		try {
			return CvsFileUtil.fromCvsFileToIterator("./csv/order/changeExamDate.csv",6);
		} catch (FileNotFoundException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		return null;
	}
	
  @AfterTest
  public void afterTest() {
  }
  
  /**
	 * Table tb_order
	 */
	public List<Order> checkOrder(List<Integer> orderList){
		waitto(mysqlWaitTime);
		List<Order> retlist = new ArrayList<Order>();
		//String orders = "(" + StringlistToString(orderList) + ")";
		String orders = "(" + ListUtil.IntegerlistToString(orderList) + ")";
		String sqlStr = "select id,order_num,status,exam_date from tb_order where id in "+orders+"" ;
		log.info("sql:"+sqlStr);

		List<Map<String, Object>> list = null;
		try {
			list = DBMapper.query(sqlStr);
		} catch (SqlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for(Map<String,Object> m : list){
			int orderId = (Integer)m.get("id");
			int status = (Integer)m.get("status");
			Order order = OrderChecker.getOrderInfo(orderId);
	    	retlist.add(order);
	    }
		    
		return retlist;
	}
}
