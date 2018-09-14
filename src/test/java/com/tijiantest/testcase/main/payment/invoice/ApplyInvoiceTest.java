package com.tijiantest.testcase.main.payment.invoice;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.mongodb.BasicDBObject;
import org.apache.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.alibaba.fastjson.JSON;
import com.jayway.jsonpath.JsonPath;
import com.tijiantest.base.Flag;
import com.tijiantest.base.HttpResult;
import com.tijiantest.base.dbcheck.AccountChecker;
import com.tijiantest.base.dbcheck.OrderChecker;
import com.tijiantest.model.account.UserAddress;
import com.tijiantest.model.order.Order;
import com.tijiantest.model.payment.invoice.InvoiceApply;
import com.tijiantest.testcase.main.MainBase;
import com.tijiantest.testcase.main.payment.PayTest;
import com.tijiantest.util.CvsFileUtil;
import com.tijiantest.util.db.DBMapper;
import com.tijiantest.util.db.MongoDBUtils;
import com.tijiantest.util.db.SqlException;

public class ApplyInvoiceTest extends MainBase {
	public static Order order = new Order();
	public static Date oldExamDate = new Date();
	
	public void getOrderAvail(){
		System.out.println("-----------------------准备申请开票Start----------------------------");
		order = PayTest.order;
		oldExamDate = order.getExamDate();
		String sql = "UPDATE tb_order SET status = 3,is_export = 1 WHERE id = "+order.getId()+"";
		try {
			DBMapper.update(sql);
		} catch (SqlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		 
		MongoDBUtils.updateMongo("{'id':"+order.getId()+"}", "{$set:{'status':3,'isExport':true}}", MONGO_COLLECTION);
		System.out.println("-----------------------准备申请开票End----------------------------");
	}
	
  @Test(description = "申请开票", dataProvider = "applyInvoice",groups = {"qa","main_applyInvoice","main_invoice"},dependsOnGroups = {"main_payWithBalance"})
  public void test_ApplyInvoice(String ...args) {
	  //准备数据Start
	  getOrderAvail(); 
	  //准备数据End
	  System.out.println("-----------------------申请开票Start----------------------------");
	  InvoiceApply ia = new InvoiceApply();
	  ia.setTitle(args[1]);
	  ia.setContent(OrderChecker.getOrderInvoiceContentConfig(defHospitalId));
	  ia.setOrderId(order.getId());
	  ia.setProposer(defaccountId);
	  UserAddress ua = new UserAddress();
	  ua.setAddressee(args[3]);
	  ua.setMobile(args[4]);
	  ua.setDetailedAddress(args[5]);
	  ua.setAccountId(defaccountId);
	  ia.setUserAddress(ua);
	  
	  List<Map<String,Object>> monlist = MongoDBUtils.query("{'id':"+order.getId()+"}", MONGO_COLLECTION);
	  ia.setApplyAmount(Double.valueOf(((BasicDBObject)(monlist.get(0).get("orderExtInfo"))).get("selfMoney").toString()).intValue()*100);
	  
	  String json = JSON.toJSONString(ia);
	  
	  HttpResult result = httpclient.post(Flag.MAIN,Invoice_OrderInvoiceApply,json);
	  Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);
	  
	  if(checkdb){
		  InvoiceApply invoice = OrderChecker.getInvoiceAppliedByOrderId(ia.getOrderId());
		  log.info("订单号.."+ia.getOrderId()+"发票"+invoice.getTitle());
		  UserAddress userAddress = AccountChecker.getUserAddress(invoice.getAddressId());
		  Assert.assertEquals(invoice.getTitle(),ia.getTitle());
		  Assert.assertEquals(invoice.getContent(), ia.getContent());
		  Assert.assertEquals(invoice.getApplyAmount(), ia.getApplyAmount());
		  Assert.assertEquals(invoice.getProposer(),ia.getProposer());
		  Assert.assertEquals(userAddress.getAddressee(),ua.getAddressee());
		  Assert.assertEquals(userAddress.getDetailedAddress(), ua.getDetailedAddress());
		  Assert.assertEquals(userAddress.getMobile(), ua.getMobile());
		  
		  if(checkmongo){
			  List<Map<String,Object>> monlist1 = MongoDBUtils.query("{'id':"+order.getId()+"}", MONGO_COLLECTION);
			  Assert.assertNotNull(monlist1);
			  InvoiceApply invoiceMG = JSON.parseObject(JsonPath.read(monlist1.get(0),"$.invoiceApply").toString(), InvoiceApply.class);
			  Assert.assertEquals(invoiceMG.getTitle(),ia.getTitle());
			  Assert.assertEquals(invoiceMG.getContent(), ia.getContent());
			  Assert.assertEquals(invoiceMG.getApplyAmount(), ia.getApplyAmount());
			  Assert.assertEquals(invoiceMG.getProposer(),ia.getProposer());
			  Assert.assertEquals(invoiceMG.getUserAddress().getAddressee(),ua.getAddressee());
			  Assert.assertEquals(invoiceMG.getUserAddress().getDetailedAddress(), ua.getDetailedAddress());
			  Assert.assertEquals(invoiceMG.getUserAddress().getMobile(), ua.getMobile());
		  }
	  }
	  System.out.println("-----------------------申请开票End----------------------------");
  }
  
  @AfterGroups(alwaysRun=true,groups = {"main_applyInvoice"})
	public void resumeOrder(){
		System.out.println("-----------------------发票测试完成，恢复数据Start----------------------------");		
		String sql = "UPDATE tb_order SET status = 2,is_export = 0 WHERE id = "+order.getId()+"";
		 try {
			DBMapper.update(sql);
		} catch (SqlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	 
		MongoDBUtils.updateMongo("{'id':"+order.getId()+"}", "{$set:{'status':2,'isExport':false}}", MONGO_COLLECTION);
		System.out.println("-----------------------发票测试完成，恢复数据End----------------------------");
	}
  
  @DataProvider
  public Iterator<String[]> applyInvoice() {
		try {
			return CvsFileUtil.fromCvsFileToIterator("./csv/invoice/mainApplyInvoice.csv", 6);
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
