package com.tijiantest.testcase.crm.settlement.sett;

import java.util.ArrayList;
import java.util.List;

import com.tijiantest.base.MyHttpClient;
import com.tijiantest.base.dbcheck.CardChecker;
import com.tijiantest.base.dbcheck.OrderChecker;
import com.tijiantest.model.order.Order;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.alibaba.fastjson.JSONArray;
import com.tijiantest.base.HttpResult;
import com.tijiantest.base.dbcheck.SettleChecker;
import com.tijiantest.model.settlement.CompanySettlementCount;
import com.tijiantest.testcase.crm.settlement.SettleBase;
import com.tijiantest.util.db.SqlException;

/**
 * CRM->维护后台->结算管理->单位结算页面查看数据
 * 包括：单位ID/单位名称/未结算订单数量/未结算卡数量/未结算退款数量/未结算预付款数量
 * @author huifang
 *
 */
public class CompanySettlePageTest extends SettleBase{

	public static boolean haveNotSettlementCompany = false;
	public static List<CompanySettlementCount> dblist = new ArrayList<CompanySettlementCount>();


	@Test(description = "单位结算页面",groups = {"qa","crm_companySett"})
	public void test_01_companySettlePage() throws SqlException{
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("hospitalId", defSettHospitalId + ""));

		HttpResult response = httpclient.get(CompanySettlePage, params);
		Assert.assertEquals(response.getCode(), HttpStatus.SC_OK);
		List<CompanySettlementCount> retSetts = JSONArray.parseArray(response.getBody(), CompanySettlementCount.class);
		
		if(checkdb){
			dblist = SettleChecker.getNotSettlementCompanyCounts(httpclient,defSettHospitalId,defSettAccountId,settle_time);
			Assert.assertEquals(retSetts.size(),dblist.size());
			if(dblist.size()>0){
				haveNotSettlementCompany = true;
				for(int i=0;i<retSetts.size();i++){
				int dbOrderNum = dblist.get(i).getOrderNum();
				int dbCardNum = dblist.get(i).getCardNum();
				int dbPrepayNum = dblist.get(i).getPrepayNum();
				int dbrefundNum = dblist.get(i).getRefundNum();
				int dbPaymentOrderNum = dblist.get(i).getPaymentOrderNum();
				log.info("单位名称"+dblist.get(i).getCompanyName() +"体检订单数"+dbOrderNum+"付款订单数"+dbPaymentOrderNum+"...卡数"+dbCardNum+"...特殊退款数量"+dbPrepayNum+"退款数量.."+dbrefundNum);
				Assert.assertEquals(retSetts.get(i).getCompanyName(),dblist.get(i).getCompanyName());
				Assert.assertEquals(retSetts.get(i).getId(),dblist.get(i).getId());
				Assert.assertEquals(retSetts.get(i).getOrderNum().intValue(), dbOrderNum);
				Assert.assertEquals(retSetts.get(i).getCardNum().intValue(), dbCardNum);
				Assert.assertEquals(retSetts.get(i).getPrepayNum().intValue(), dbPrepayNum);
				Assert.assertEquals(retSetts.get(i).getRefundNum().intValue(), dbrefundNum);
				Assert.assertEquals(retSetts.get(i).getPaymentOrderNum().intValue(), dbPaymentOrderNum);

				}
			}
		}
	}
}
