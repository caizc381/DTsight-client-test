package com.tijiantest.testcase.ops.refund;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.minidev.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.alibaba.fastjson.JSON;
import com.jayway.jsonpath.JsonPath;
import com.tijiantest.base.Flag;
import com.tijiantest.base.HttpResult;
import com.tijiantest.model.order.orderrefund.OrderRefundApplyQueryVO;
import com.tijiantest.testcase.ops.OpsBase;
import com.tijiantest.util.CvsFileUtil;
import com.tijiantest.util.DateUtils;
import com.tijiantest.util.ListUtil;
import com.tijiantest.util.db.DBMapper;
import com.tijiantest.util.db.SqlException;

/**
 * 列举退款订单列表(OPS->退款审批->待审批页面)
 * @author huifang
 *
 */
public class ListRefundApplyTest extends OpsBase{

	@Test(description="列举退款审批",dataProvider="refundApply",groups = {"qa"})
	public void test_01_listRefundApply(String ...args) throws ParseException, SqlException{
		boolean recentThreeDay = args[1].equals("true")?true:false;
		int scene  = -1;
		int hospitalCompany = -1;
		String applyStartTime = "";
		String applyEndTime = "";
		String countMethod = "";
		String account = "";
		int minOnlinePay = -1;
		int maxOnlinePay = -1;
		int refundType = -1;
		int pageSize = 0;
		int fromSite = -1;
		List<Integer> statusList = new ArrayList<Integer>();
		String statusStr = "";
		OrderRefundApplyQueryVO vo = new OrderRefundApplyQueryVO();
		vo.setRecentThreeDay(recentThreeDay);
		if(recentThreeDay)
			vo.setApplyEndTime(DateUtils.offDate(-3));
		if(!IsArgsNull(args[2])){
			   scene = Integer.parseInt(args[2]);
			   vo.setScene(scene);
			   }
		if(!IsArgsNull(args[3])){
				hospitalCompany = Integer.parseInt(args[3]);
				vo.setHospitalCompanyId(hospitalCompany);
			}
		if(!IsArgsNull(args[4])){
				applyStartTime = args[4];
				vo.setApplyStartTime(simplehms.parse(applyStartTime));
			}
		if(!IsArgsNull(args[5])){
				applyEndTime = args[5];
				vo.setApplyEndTime(simplehms.parse(applyEndTime));
		    }
		if(!IsArgsNull(args[6])){
		    	countMethod = args[6];
		    	vo.setCountMethod(countMethod);
		    }
		if(!IsArgsNull(args[7])){
				account = args[7];
				vo.setAccount(account);
			}
	
		if(!IsArgsNull(args[8])){
			   refundType = Integer.parseInt(args[8]);
			   vo.setRefundType(refundType);
			   }
		if(!IsArgsNull(args[9])){
				statusList.add(Integer.parseInt(args[9]));
				vo.setStatusList(statusList);
				statusStr += Integer.parseInt(args[9]);
			}
		if(!IsArgsNull(args[10])){
				pageSize = Integer.parseInt(args[10]);
				vo.setPageSize(pageSize);
			}
		if(!IsArgsNull(args[11])){
				fromSite = Integer.parseInt(args[11]);
				vo.setFromSite(fromSite);
			}	
		if(!IsArgsNull(args[12])){
			minOnlinePay = Integer.parseInt(args[12]);
			vo.setMinOnlinePay(minOnlinePay);
	    }
		if(!IsArgsNull(args[13])){
			maxOnlinePay = Integer.parseInt(args[13]);
			vo.setMaxOnlinePay(maxOnlinePay);
	    }
		vo.setCurrentPage(1);
		
		String request = JSON.toJSONString(vo);
		HttpResult result = httpclient.post(Flag.OPS,ListRefundApply,request);
		log.info(result.getBody());
		Assert.assertEquals(result.getCode(),HttpStatus.SC_OK);
		JSONArray jarray = JsonPath.read(result.getBody(), "$.records");
		
		if(checkdb){
			
			String sql = "select * from tb_order_refund_apply where scene = "+scene + " and status ="+statusStr;
			//日期
			if(recentThreeDay)
				sql += "  and apply_time < '" + simplehms.format(DateUtils.offDate(-3))+"' ";
			else{
				if(!applyStartTime.equals(""))
					sql += " and apply_time > '"+applyStartTime+"' ";
				if(!applyEndTime.equals(""))
					sql += " and apply_time < '"+applyEndTime+"' ";
			}
			//线上支付
			if(minOnlinePay==0 && maxOnlinePay == 0)
				sql += " and online_pay =0 ";
			else if(minOnlinePay ==0 && maxOnlinePay == -1)
				sql += " and online_pay >0 ";
			//退款类型
			if(refundType != -1)
				sql += " and refund_type = "+refundType;
			//体检机构+单位
			if(fromSite != -1)
				sql += " and from_site = "+fromSite;
			if(hospitalCompany != -1)
				sql += " and hospital_company_id = "+hospitalCompany;
			//体检人
			if(!account.equals("")){
				List<Map<String,Object>> accountCheckLists = DBMapper.query("select DISTINCT id from tb_account where "
						+ "idcard = '"+account +"' or mobile = '"+account +"' or name = '"+account+"'");
				List<Integer> accounts = new ArrayList<Integer>();
				for(Map<String,Object> a : accountCheckLists)
					accounts.add(Integer.parseInt(a.get("id").toString()));
				
				sql += " and account_id in ("+ListUtil.IntegerlistToString(accounts)+") " ;
			}
			sql += " order by apply_time desc limit "+pageSize;
			
			log.info("sql..."+sql);
			List<Map<String,Object>> dblist = DBMapper.query(sql);
			Assert.assertEquals(jarray.size(),dblist.size());
			for(int i=0;i<jarray.size();i++){
				JSONObject jo = JSONObject.fromObject(jarray.get(i));
//				System.out.println("orderNum"+jo.get("orderNum"));
				Assert.assertEquals(jo.get("orderNum").toString(),dblist.get(i).get("order_num").toString());
				Assert.assertEquals(jo.get("amount").toString(),dblist.get(i).get("amount").toString());
				Assert.assertEquals(Integer.parseInt(JsonPath.read(jo,"$.fromSiteInfo.id").toString()),Integer.parseInt(dblist.get(i).get("from_site").toString()));
				Assert.assertEquals(Integer.parseInt(JsonPath.read(jo,"$.hospitalCompanyInfo.id").toString()),Integer.parseInt(dblist.get(i).get("hospital_company_id").toString()));
				if(JsonPath.read(jo,"$.accountInfo")!=null  && !JsonPath.read(jo,"$.accountInfo").toString().equals("null") )
					Assert.assertEquals(Integer.parseInt(JsonPath.read(jo,"$.accountInfo.id").toString()),Integer.parseInt(dblist.get(i).get("account_id").toString()));
				Assert.assertEquals(Integer.parseInt(jo.get("refundType").toString()),Integer.parseInt(dblist.get(i).get("refund_type").toString()));
				JSONObject jdb = JSONObject.fromObject(dblist.get(i).get("pay_detail").toString());
				Assert.assertEquals(Integer.parseInt(JsonPath.read(jo,"$.payDetail.cardPayAmount").toString()),jdb.getInt("cardPayAmount"));
				Assert.assertEquals(Integer.parseInt(JsonPath.read(jo,"$.payDetail.offlinePayAmount").toString()),jdb.getInt("offlinePayAmount"));
				Assert.assertEquals(Integer.parseInt(JsonPath.read(jo,"$.payDetail.onlinePayAmount").toString()),jdb.getInt("onlinePayAmount"));
				Assert.assertEquals(Integer.parseInt(JsonPath.read(jo,"$.payDetail.parentCardPayAmount").toString()),jdb.getInt("parentCardPayAmount"));
				//优惠券支付金额
				if(JsonPath.read(jo,"$.payDetail.couponAmount")!= null){
					log.info("有优惠券支付场景");
					int couponAmount = Integer.parseInt(JsonPath.read(jo,"$.payDetail.couponAmount").toString());
					if(couponAmount >0)
						Assert.assertEquals(couponAmount,jdb.getInt("couponAmount"));
					else
						Assert.assertEquals(0,jdb.getInt("couponAmount")); //新数据优惠券有为0情况
				}else
					Assert.assertNull(jdb.get("couponAmount"));//历史数据优惠券为空



				if(jdb.get("totalAmount")!= null){
					log.debug("totalAmount..."+jdb.get("totalAmount"));
					Assert.assertEquals(Integer.parseInt(JsonPath.read(jo,"$.payDetail.totalAmount").toString()),jdb.getInt("totalAmount"));
				}
			}
		}
		
		
			
	}


	@DataProvider
	public Iterator<String[]> refundApply() {
		try {
			return CvsFileUtil.fromCvsFileToIterator("./csv/opsRefund/refundApply.csv", 20);
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
