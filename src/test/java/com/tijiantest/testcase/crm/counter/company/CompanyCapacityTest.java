package com.tijiantest.testcase.crm.counter.company;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.alibaba.fastjson.JSON;
import com.tijiantest.base.Flag;
import com.tijiantest.base.HttpResult;
import com.tijiantest.base.MyHttpClient;
import com.tijiantest.base.dbcheck.CompanyChecker;
import com.tijiantest.base.dbcheck.CounterChecker;
import com.tijiantest.model.company.ChannelCompany;
import com.tijiantest.model.company.HospitalCompany;
import com.tijiantest.model.counter.CompanyCapacityCell;
import com.tijiantest.testcase.crm.counter.CounterBase;
import com.tijiantest.util.DateUtils;
import com.tijiantest.util.db.SqlException;

public class CompanyCapacityTest extends CounterBase{
  @Test(description="体检中心客户经理CRM查看单位预留",groups={"qa"})
  public void test_getCompanyCapacity() {
	  System.out.println("-------------------------体检中心客户经理CRM获取单位预留start-------------------------");
	  Integer companyId = defnewcompany.getId();
	  Integer hospitalId = defhospital.getId();
	  Date start = DateUtils.theFirstDayOfMonth(0);
	  Date end = DateUtils.theLastDayOfMonth(0);
	  String startDate = DateUtils.format("yyyy-MM-dd", start);
	  String endDate = DateUtils.format("yyyy-MM-dd", end);
	  
	  List<NameValuePair> params = new ArrayList<NameValuePair>();
	  params.add(new BasicNameValuePair("companyId", companyId + ""));
	  params.add(new BasicNameValuePair("hospitalId", hospitalId + ""));
	  params.add(new BasicNameValuePair("startDate", startDate));
	  params.add(new BasicNameValuePair("endDate", endDate));
	  
	  HttpResult response = httpclient.get(CountComp_Capacity, params);
	  Assert.assertEquals(response.getCode(), HttpStatus.SC_OK);
	  List<CompanyCapacityCell> cCapacityCellList = JSON.parseArray(response.getBody(), CompanyCapacityCell.class);
	  
	  //获取单位人数人数设置和可用量
	  List<Map<Integer, CompanyCapacityCell>> companyCapacityLst = CounterChecker.getPeriodCapacityByCompany(hospitalId, companyId, start, end);
	  
	  //合并，同一天，各时段合并数据
	  List<CompanyCapacityCell> cCapacityCellDBList = CounterChecker.getUsedCapacitySumByDayRange(companyCapacityLst,start, end,hospitalId);
	  Assert.assertEquals(cCapacityCellList.size(), cCapacityCellDBList.size());
	  if(companyCapacityLst!=null)
		  for(int i=0;i<cCapacityCellList.size();i++){
			  CompanyCapacityCell cCapacityCell = cCapacityCellList.get(i);
			  CompanyCapacityCell companyCapacityDB = cCapacityCellDBList.get(i);
			  Assert.assertEquals(cCapacityCell.getReserveNum(), companyCapacityDB.getReserveNum());
			  Assert.assertEquals(cCapacityCell.getUsedNum(), companyCapacityDB.getUsedNum());
			  if(companyCapacityDB.getLimit()!=null&&companyCapacityDB.getLimit()!=-1)
				  Assert.assertEquals(cCapacityCell.getLimit(), companyCapacityDB.getLimit());
		  }
	  System.out.println("-------------------------体检中心客户经理CRM获取单位预留end-------------------------");
  }
  
  @Test(description="平台客户经理CRM查看单位预留",groups={"qa"})
  public void test_platCRMGetCompanyCapacity() throws SqlException {
	  System.out.println("-------------------------平台客户经理CRM获取单位预留start-------------------------");
	  MyHttpClient myClient = new MyHttpClient();
	  // 1.平台客户经理登陆CRM
	  onceLoginInSystem(myClient, Flag.CRM,defPlatUsername,defPlatPasswd);
	  List<Integer> ChannelCompanyIds = CompanyChecker.getCompanysIdByManagerId(myClient,defPlatAccountId,true);
	  List<ChannelCompany> cCompanys = new ArrayList<ChannelCompany>();
	  for(Integer id : ChannelCompanyIds){
		  ChannelCompany cCompany = CompanyChecker.getChannelCompanyByCompanyId(id);
		  cCompanys.add(cCompany);
	  }
	  for(ChannelCompany cCompany : cCompanys){
		  HospitalCompany hCompany = new HospitalCompany();
		  if(cCompany.getPlatformCompanyId()!=3)
			  hCompany = CompanyChecker.getHospitalCompanyByPlatCompanyId(cCompany.getPlatformCompanyId()).get(0);
		  else		  
			  hCompany = defMTJKnewcompany;
		  
		  Integer companyId = cCompany.getId();
		  Integer hospitalId = hCompany.getOrganizationId();
		  Date start = DateUtils.theFirstDayOfMonth(0);
		  Date end = DateUtils.theLastDayOfMonth(0);
		  String startDate = DateUtils.format("yyyy-MM-dd", start);
		  String endDate = DateUtils.format("yyyy-MM-dd", end);
		  
		  List<NameValuePair> params = new ArrayList<NameValuePair>();
		  params.add(new BasicNameValuePair("companyId", companyId + ""));
		  params.add(new BasicNameValuePair("hospitalId", hospitalId + ""));
		  params.add(new BasicNameValuePair("startDate", startDate));
		  params.add(new BasicNameValuePair("endDate", endDate));
		  
		  HttpResult response = myClient.get(CountComp_Capacity, params);
		  Assert.assertEquals(response.getCode(), HttpStatus.SC_OK);
		  List<CompanyCapacityCell> cCapacityCellList = JSON.parseArray(response.getBody(), CompanyCapacityCell.class);
		  
		  //获取单位人数人数设置和可用量
		  List<Map<Integer, CompanyCapacityCell>> companyCapacityLst = CounterChecker.getPeriodCapacityByCompany(hospitalId, hCompany.getId(), start, end);
		  
		  //合并，同一天，各时段合并数据
		  List<CompanyCapacityCell> cCapacityCellDBList = CounterChecker.getUsedCapacitySumByDayRange(companyCapacityLst,start, end,hospitalId);
		  Assert.assertEquals(cCapacityCellList.size(), cCapacityCellDBList.size());
		  if(companyCapacityLst!=null)
			  for(int i=0;i<cCapacityCellList.size();i++){
				  CompanyCapacityCell cCapacityCell = cCapacityCellList.get(i);
				  CompanyCapacityCell companyCapacityDB = cCapacityCellDBList.get(i);
//				  System.out.println("response:"+JSON.toJSONString(cCapacityCell)+"\n DB:"+JSON.toJSONString(companyCapacityDB));
				  Assert.assertEquals(cCapacityCell.getReserveNum(), companyCapacityDB.getReserveNum());
				  Assert.assertEquals(cCapacityCell.getUsedNum(), companyCapacityDB.getUsedNum());
				  if(cCapacityCell.getLimit()!=null&&cCapacityCell.getLimit()!=-1)
					  Assert.assertEquals(cCapacityCell.getLimit(), companyCapacityDB.getLimit());
			  }	  
	  }
	  /*int x=1+(int)(Math.random()*ChannelCompanyIds.size());
	  ChannelCompany cCompany = CompanyChecker.getChannelCompanyByCompanyId(ChannelCompanyIds.get(x-1));*/
	 
	  System.out.println("-------------------------平台客户经理CRM获取单位预留end-------------------------");
	  onceLogOutSystem(myClient, Flag.CRM);
  }
}
