package com.tijiantest.testcase.ops.settlement;

import com.alibaba.fastjson.JSON;
import com.tijiantest.base.Flag;
import com.tijiantest.base.HttpResult;
import com.tijiantest.base.dbcheck.SettleChecker;
import com.tijiantest.model.organization.OrganizationTypeEnum;
import com.tijiantest.model.settlement.TradePrepaymentRecord;
import com.tijiantest.testcase.ops.OpsBase;
import com.tijiantest.util.CvsFileUtil;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * OPS->特殊退款
 * 
 * 删除特殊退款
 * @author huifang
 *
 */
public class DeletePrepaymentRecordTest extends OpsBase{

	@Test(description = "删除特殊退款" , groups = {"qa"},dataProvider = "ops_deletePrepayment")
	public void test_01_deletePrepaymentRecord(String ...args) throws ParseException{
		String refundOrganizationTypeStr = args[1];
		int refundOrganizationType = Integer.parseInt(refundOrganizationTypeStr);
		String sql = "select * from tb_trade_prepayments_record where status = 1 and is_deleted = 0 ";
		if(refundOrganizationType == OrganizationTypeEnum.CHANNEL.getCode())
			sql += " and settlement_view_type = 1 and sn is not null ";
		else
			sql += " and settlement_view_type = 0 and refund_organization_id in (select id from tb_hospital where organization_type = 1)";
		sql += " order by id desc limit 1";

		List<TradePrepaymentRecord>  prepaymentRecords = SettleChecker.getTradePrepaymentRecordBySql(sql);
		TradePrepaymentRecord dto = prepaymentRecords.get(0);
		String sn = dto.getSn();
		int preId = dto.getId();
		List<NameValuePair> params = new ArrayList<>();
		if(sn != null)
			params.add(new BasicNameValuePair("sn",sn));
		params.add(new BasicNameValuePair("prepaymentRecordId",preId+""));
		HttpResult response = httpclient.get(Flag.OPS,OPS_DeletePrepaymentRecord,params);
		Assert.assertEquals(response.getCode(), HttpStatus.SC_OK);
		Assert.assertEquals(response.getBody(),"");
		
		if(checkdb){
			List<TradePrepaymentRecord>  prepaymentRecordsList = null;
			if(sn != null)
				prepaymentRecordsList = SettleChecker.getTradePrepaymentRecordBySql("select * from tb_trade_prepayments_record where sn = '"+sn+"'");
			else
				prepaymentRecordsList = SettleChecker.getTradePrepaymentRecordBySql("select * from tb_trade_prepayments_record where id = "+preId);
			for(TradePrepaymentRecord p : prepaymentRecordsList)
					Assert.assertEquals(p.getIsDeleted().intValue(),1);
		}
				
	}

	@DataProvider
	public Iterator<String[]> ops_deletePrepayment(){
		try {
			return CvsFileUtil.fromCvsFileToIterator("./csv/settlement/ops_deletePrepayment.csv",18);
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