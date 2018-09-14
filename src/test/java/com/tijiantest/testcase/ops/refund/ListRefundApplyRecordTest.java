package com.tijiantest.testcase.ops.refund;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.*;

import net.minidev.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.http.HttpStatus;
import org.apache.poi.util.SystemOutLogger;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.alibaba.fastjson.JSON;
import com.jayway.jsonpath.JsonPath;
import com.tijiantest.base.Flag;
import com.tijiantest.base.HttpResult;
import com.tijiantest.model.order.orderrefund.OrderRefundApplyRecordQueryVO;
import com.tijiantest.testcase.ops.OpsBase;
import com.tijiantest.util.CvsFileUtil;
import com.tijiantest.util.ListUtil;
import com.tijiantest.util.db.DBMapper;
import com.tijiantest.util.db.SqlException;

/**
 * 列举退款订单列表(OPS->退款审批->审批记录页面)
 * @author huifang
 *
 */
public class ListRefundApplyRecordTest extends OpsBase{

	@Test(description="审批记录页面",dataProvider="refundApplyRecord",groups = {"qa"})
	public void test_01_listRefundApplyRecord(String ...args) throws ParseException, SqlException{
		int scene  = -1;
		int hospitalCompany = -1;
		String auditStartTime = "";
		String auditEndTime = "";
		String applyStartTime = "";
		String applyEndTime = "";
		String countMethod = "";
		String account = "";
		int minOnlinePay = -1;
		int maxOnlinePay = -1;
		int auditResult = -1;
		int pageSize = 0;
		int fromSite = -1;
		List<Integer> statusList = new ArrayList<Integer>();
		OrderRefundApplyRecordQueryVO vo = new OrderRefundApplyRecordQueryVO();
		if(!IsArgsNull(args[1])){
			   scene = Integer.parseInt(args[1]);
			   vo.setScene(scene);
			   }
		if(!IsArgsNull(args[2])){
				hospitalCompany = Integer.parseInt(args[2]);
				vo.setHospitalCompanyId(hospitalCompany);
			}
		if(!IsArgsNull(args[3])){
			auditStartTime = args[3];
			vo.setAuditStartTime(simplehms.parse(auditStartTime));
		}
		if(!IsArgsNull(args[4])){
			auditEndTime = args[4];
			vo.setAuditEndTime(simplehms.parse(auditEndTime));
	    }
		if(!IsArgsNull(args[5])){
				applyStartTime = args[5];
				vo.setApplyStartTime(simplehms.parse(applyStartTime));
			}
		if(!IsArgsNull(args[6])){
				applyEndTime = args[6];
				vo.setApplyEndTime(simplehms.parse(applyEndTime));
		    }
		if(!IsArgsNull(args[7])){
		    	countMethod = args[7];
		    	vo.setCountMethod(countMethod);
		    }
		if(!IsArgsNull(args[8])){
				account = args[8];
				vo.setAccount(account);
			}
	
		if(!IsArgsNull(args[9])){
			   auditResult = Integer.parseInt(args[9]);
			   vo.setAuditResult(auditResult);
			   }
		if(!IsArgsNull(args[10])){
				String[] sts = args[10].split("#");
				for(String s :sts)
					statusList.add(Integer.parseInt(s));
				vo.setStatusList(statusList);
			}
		if(!IsArgsNull(args[11])){
				pageSize = Integer.parseInt(args[11]);
				vo.setPageSize(pageSize);
			}
		if(!IsArgsNull(args[12])){
				fromSite = Integer.parseInt(args[12]);
				vo.setFromSite(fromSite);
			}	
		if(!IsArgsNull(args[13])){
			minOnlinePay = Integer.parseInt(args[13]);
			vo.setMinOnlinePay(minOnlinePay);
	    }
		if(!IsArgsNull(args[13])){
			maxOnlinePay = Integer.parseInt(args[13]);
			vo.setMaxOnlinePay(maxOnlinePay);
	    }
		vo.setCurrentPage(1);
		
		String request = JSON.toJSONString(vo);
		HttpResult result = httpclient.post(Flag.OPS,ListRefundApplyRecord,request);
		log.info(result.getBody());
		Assert.assertEquals(result.getCode(),HttpStatus.SC_OK);
		JSONArray jarray = JsonPath.read(result.getBody(), "$.records");

		if(checkdb){
			
			String sql = "select * from tb_order_refund_apply_record where scene = "+scene +" and status != 0 and amount !=0  and is_deleted = 0" ;
			//日期
			if(!auditStartTime.equals(""))
				sql += " and audit_time > '"+auditStartTime+"' ";
			if(!auditEndTime.equals(""))
				sql += " and audit_time < '"+auditEndTime+"' ";
			if(!applyStartTime.equals(""))
				sql += " and apply_time > '"+applyStartTime+"' ";
			if(!applyEndTime.equals(""))
				sql += " and apply_time < '"+applyEndTime+"' ";
			
			//线上支付
			if(minOnlinePay==0 && maxOnlinePay == 0)
				sql += " and online_pay =0 ";
			else if(minOnlinePay ==0 && maxOnlinePay == -1)
				sql += " and online_pay >0 ";
			//审核状态
			if(auditResult != -1)
				sql += " and status = "+auditResult;
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
			sql += " order by audit_time desc  limit "+pageSize;
//			sql += "  limit "+pageSize;

			log.info("sql..."+sql);
			List<Map<String,Object>> dblist = DBMapper.query(sql);
			Assert.assertEquals(jarray.size(),dblist.size());
			for(int i=0;i<jarray.size();i++){
				JSONObject jo = JSONObject.fromObject(jarray.get(i));
				log.info("第.."+(i+1)+"条");
				Assert.assertEquals(Long.parseLong(JsonPath.read(jo,"$.auditTime").toString()) ,simplehms.parse( dblist.get(i).get("audit_time").toString()).getTime());
				Object accountInfo = JsonPath.read(jo,"$.accountInfo");
				if(accountInfo!=null && accountInfo.toString() != "" && !accountInfo.toString().equals("null"))
					Assert.assertEquals(Integer.parseInt(JsonPath.read(jo,"$.accountInfo.id").toString()),Integer.parseInt(dblist.get(i).get("account_id").toString()));
				Assert.assertEquals(jo.get("amount").toString(),dblist.get(i).get("amount").toString());
				Assert.assertEquals(Integer.parseInt(JsonPath.read(jo,"$.fromSiteInfo.id").toString()),Integer.parseInt(dblist.get(i).get("from_site").toString()));
				Assert.assertEquals(Integer.parseInt(JsonPath.read(jo,"$.hospitalCompanyInfo.id").toString()),Integer.parseInt(dblist.get(i).get("hospital_company_id").toString()));
				Assert.assertEquals(Integer.parseInt(jo.get("status").toString()),Integer.parseInt(dblist.get(i).get("status").toString()));
				Assert.assertEquals(jo.get("reason").toString(),dblist.get(i).get("reason").toString());
				JSONObject jdb = JSONObject.fromObject(dblist.get(i).get("pay_detail").toString());
				Assert.assertEquals(Integer.parseInt(JsonPath.read(jo,"$.payDetail.cardPayAmount").toString()),jdb.getInt("cardPayAmount"));
				Assert.assertEquals(Integer.parseInt(JsonPath.read(jo,"$.payDetail.offlinePayAmount").toString()),jdb.getInt("offlinePayAmount"));
				Assert.assertEquals(Integer.parseInt(JsonPath.read(jo,"$.payDetail.onlinePayAmount").toString()),jdb.getInt("onlinePayAmount"));
				Assert.assertEquals(Integer.parseInt(JsonPath.read(jo,"$.payDetail.parentCardPayAmount").toString()),jdb.getInt("parentCardPayAmount"));
				//优惠券支付金额
				if(JsonPath.read(jo,"$.payDetail.couponAmount")!= null){
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
	public Iterator<String[]> refundApplyRecord() {
		try {
			return CvsFileUtil.fromCvsFileToIterator("./csv/opsRefund/refundApplyRecord.csv", 20);
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
