package com.tijiantest.testcase.main.payment.invoice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.testng.Assert;
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
import com.tijiantest.model.payment.invoice.OrderInvoiceApplyVo;
import com.tijiantest.testcase.main.MainBase;
import com.tijiantest.util.db.MongoDBUtils;

public class GetInvoiceAppliedTest extends MainBase {
  @Test(description="获取发票申请详情" ,groups = {"qa"} ,dependsOnGroups={"main_applyInvoice"})
  public void getInvoiceAppliedByOrderId() {
	  System.out.println("-----------------------获取发票详情Start----------------------------");
	  Order order = new Order();
	  order = ApplyInvoiceTest.order;
	  int orderId = order.getId();
	  Map<String,Object>map = new HashMap<String,Object>();
	  map.put("orderId",String.valueOf(orderId));
	  
	  HttpResult result = httpclient.get(Flag.MAIN,Invoice_GetInvoiceApply, map);
	  OrderInvoiceApplyVo vo = JSON.parseObject(JsonPath.read(result.getBody(),"$.[*]").toString(), OrderInvoiceApplyVo.class);
	  
	  if(checkdb){	  
		  InvoiceApply invoice = OrderChecker.getInvoiceAppliedByOrderId(orderId);
		  UserAddress userAddress = AccountChecker.getUserAddress(invoice.getAddressId());
		  invoice.setUserAddress(userAddress);
		  //验证
		  Assert.assertEquals(invoice.getId(), vo.getId());
		  Assert.assertEquals(invoice.getTitle(), vo.getTitle());
		  Assert.assertEquals(invoice.getAddressId(), vo.getAddressId());
		  Assert.assertEquals(invoice.getApplyAmount(), vo.getApplyAmount());
		  Assert.assertEquals(invoice.getApprover(), vo.getApprover());
		  Assert.assertEquals(invoice.getStatus(), vo.getStatus());
		  Assert.assertEquals(invoice.getUserAddress().getId(), vo.getUserAddress().getId());
		  Assert.assertEquals(invoice.getUserAddress().getAddressee(), vo.getUserAddress().getAddressee());
		  Assert.assertEquals(invoice.getUserAddress().getDetailedAddress(), vo.getUserAddress().getDetailedAddress());
		  Assert.assertEquals(invoice.getUserAddress().getMobile(),vo.getUserAddress().getMobile());
		  Assert.assertEquals(invoice.getUserAddress().getAccountId(), vo.getUserAddress().getAccountId());
		  
		  if(checkmongo){
			  List<Map<String,Object>> monlist = MongoDBUtils.query("{'id':"+order.getId()+"}", MONGO_COLLECTION);
			  Assert.assertNotNull(monlist);
			  InvoiceApply invoiceMG = JSON.parseObject(JsonPath.read(monlist.get(0),"$.invoiceApply").toString(), InvoiceApply.class);
			  Assert.assertEquals(invoiceMG.getTitle(), vo.getTitle());
			  Assert.assertEquals(invoiceMG.getAddressId(), vo.getAddressId());
			  Assert.assertEquals(invoiceMG.getApplyAmount(), vo.getApplyAmount());
			  Assert.assertEquals(invoiceMG.getApprover(), vo.getApprover());
			  Assert.assertEquals(invoiceMG.getStatus(), vo.getStatus());
			  Assert.assertEquals(invoiceMG.getUserAddress().getAddressee(), vo.getUserAddress().getAddressee());
			  Assert.assertEquals(invoiceMG.getUserAddress().getDetailedAddress(), vo.getUserAddress().getDetailedAddress());
			  Assert.assertEquals(invoiceMG.getUserAddress().getMobile(),vo.getUserAddress().getMobile());
			  Assert.assertEquals(invoiceMG.getUserAddress().getAccountId(), vo.getUserAddress().getAccountId());
		  }
	  }
	  System.out.println("-----------------------获取发票详情End----------------------------");
  }
}
