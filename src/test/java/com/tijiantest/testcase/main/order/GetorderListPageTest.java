package com.tijiantest.testcase.main.order;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.tijiantest.base.dbcheck.CardChecker;
import com.tijiantest.model.card.Card;
import com.tijiantest.model.order.Order;
import com.tijiantest.model.order.snapshot.MealSnapshot;
import com.tijiantest.util.db.SqlException;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.alibaba.fastjson.JSON;
import com.jayway.jsonpath.JsonPath;
import com.tijiantest.base.Flag;
import com.tijiantest.base.HttpResult;
import com.tijiantest.base.dbcheck.OrderChecker;
import com.tijiantest.testcase.main.MainBase;

/**
 * C端查看订单列表
 */
public class GetorderListPageTest extends MainBase {
	public static int newOrderId;
	public static List<Integer> orderList = new ArrayList<Integer>();
	
	@Test(groups = {"qa","main_getOrderList"})
	public void test_01_getOrderList() throws SqlException, ParseException {
	    List<NameValuePair> params = new ArrayList<NameValuePair>();
	    params.add(new BasicNameValuePair("_p",""));
		params.add(new BasicNameValuePair("_site","mtjk"));
		params.add(new BasicNameValuePair("_siteType","mobile"));

		HttpResult result = httpclient.post(Flag.MAIN,MainOrder_OrderListPage, params);
	    Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);
		String body = result.getBody();
		List<Order> retOrderList = JSONArray.parseArray(JsonPath.read(body,"$.orderList.records").toString(),Order.class);
	    if(checkdb){
			List<Integer> orderids = OrderChecker.getOrderList(defUser.getAccount_id());
			Assert.assertEquals(retOrderList.size(),orderids.size());
			for(int i=0;i<orderids.size();i++){
				Assert.assertEquals(retOrderList.get(i).getId(),orderids.get(i).intValue());
				Order o = OrderChecker.getOrderInfo(orderids.get(i));
				Assert.assertEquals(retOrderList.get(i).getExamDate().toString(),o.getExamDate().toString());
				MealSnapshot  mealSnap =  retOrderList.get(i).getOrderMealSnapshot().getMealSnapshot();
				Assert.assertEquals(mealSnap.getId(),o.getOrderMealSnapshot().getMealSnapshot().getId());
				Assert.assertEquals(mealSnap.getName(),o.getOrderMealSnapshot().getMealSnapshot().getName());
				Assert.assertEquals(retOrderList.get(i).getOrderAccount().getName(),o.getOrderAccount().getName());
				Assert.assertEquals(retOrderList.get(i).getOrderHospital().getName(),o.getHospital().getName());
				int dbOrderPrice = o.getOrderPrice();
				int retOrderPrice = retOrderList.get(i).getOrderPrice();
				if(retOrderList.get(i).getEntryCardId()!=null){
					Assert.assertEquals(retOrderList.get(i).getEntryCardId(),o.getEntryCardId());
					Card card = CardChecker.getCardInfo(retOrderList.get(i).getEntryCardId());
					if(card == null)
						continue;
					else
					if(card.getCardSetting().isShowCardMealPrice())//隐价卡
					{
						int amount = 0;
						if(card.getRecoverableBalance()!=null)//隐价卡有回收余额
							amount = card.getCapacity().intValue() - card.getRecoverableBalance().intValue();
						else
							amount = card.getCapacity().intValue();
						if( dbOrderPrice> amount ) //1.当订单金额 >卡实际可用金额，则取差值，否则为0显示
							Assert.assertEquals(retOrderPrice,dbOrderPrice-amount);
						else
							Assert.assertEquals(retOrderPrice,0);
					}
					else //普通卡
						Assert.assertEquals(retOrderPrice,dbOrderPrice);
				}else //非卡
					Assert.assertEquals(retOrderPrice,dbOrderPrice);
				Assert.assertEquals(retOrderList.get(i).getStatus(),o.getStatus());

			}
	    }
	}

	
	
}