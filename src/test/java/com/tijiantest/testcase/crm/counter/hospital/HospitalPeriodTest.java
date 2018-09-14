package com.tijiantest.testcase.crm.counter.hospital;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.tijiantest.util.DateUtils;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.alibaba.fastjson.JSON;
import com.tijiantest.base.HttpResult;
import com.tijiantest.base.dbcheck.CounterChecker;
import com.tijiantest.model.counter.HospitalCapacityCell;
import com.tijiantest.testcase.crm.counter.CounterBase;
import com.tijiantest.util.CvsFileUtil;

import net.sf.json.JSONObject;

public class HospitalPeriodTest extends CounterBase{
	
  @Test(groups={"qa"},description = "获取体检中心各时段容量",dataProvider = "hospitalPeriod")
  public void test_hospitalPeroid(String ...args) throws ParseException {
	  System.out.println("----------------------获取体检中心各时段容量测试Start----------------------");
	  Integer hospitalId = defhospital.getId();
	  String dateStr = args[1];
//	  String dateStr = "2018-09-20";
	  List<NameValuePair> pairs = new ArrayList<>();
	  pairs.add(new BasicNameValuePair("hospitalId", String.valueOf(hospitalId)));
	  pairs.add(new BasicNameValuePair("date", dateStr));
	  
	  HttpResult result = httpclient.get(CountHosp_Period, pairs);
	  Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);
	  log.info(result.getBody());
	  Map<Integer, Map<Integer, HospitalCapacityCell>> periodRes = new HashMap<>();
	  Map<String, Object> periodStr = Json2Map(result.getBody());

	  for(String in : periodStr.keySet()){
		  Map<String, Object> hccstr = Json2Map(periodStr.get(in).toString());
		  Map<Integer,HospitalCapacityCell> hccMap = new HashMap<>();
		  for(String inte : hccstr.keySet()){
			  HospitalCapacityCell hcc = JSON.parseObject(hccstr.get(inte).toString(), HospitalCapacityCell.class);
			  hccMap.put(Integer.valueOf(inte), hcc);
		  }
		  periodRes.put(Integer.valueOf(in), hccMap);
	  }
	  
	  if (checkdb) {
		  List<Integer> dayRangeIds = CounterChecker.getDayRangeIds(hospitalId);
		  Date date = null;
		try {
			date = sdf.parse(dateStr);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//查看容量使用表 tb_hospital_capacity_used
		  Map<Integer, Map<Integer, HospitalCapacityCell>> periodMap = CounterChecker.getPeriodUsedCapacity(hospitalId, dayRangeIds, date);
		  Assert.assertEquals(periodRes.size(), periodMap.size());
//		  System.out.println("periodRes:"+JSON.toJSONString(periodRes)+"  \nperiodMap:"+JSON.toJSONString(periodMap));
		  List<Integer> examItems = CounterChecker.getLimitItem(hospitalId).stream().map(e->e.getId()).collect(Collectors.toList());
		  examItems.add(-1);
		  for(Integer e : examItems){
			  Map<Integer, HospitalCapacityCell> hMapRes = periodRes.get(e);
			  Map<Integer, HospitalCapacityCell> hMapDb = periodMap.get(e);
			  for(Integer d : dayRangeIds){
				  HospitalCapacityCell hRes = hMapRes.get(d);
				  HospitalCapacityCell hDb = hMapDb.get(d);
				  if(hRes != null){
				  	  log.debug("exam"+e+"..d"+d);
				  	  if(hDb != null){
				  	  	Assert.assertEquals(hRes.getAvailableNum(), hDb.getAvailableNum());
				  	  	Assert.assertEquals(hRes.getCapacity(), hDb.getCapacity());
					  }
				  	  else{
						  Assert.assertEquals(hRes.getAvailableNum(), 0);
						  Assert.assertEquals(hRes.getCapacity(),0);
				  	  }
				  }	  
			  }
		  }
	  }
	  System.out.println("----------------------获取体检中心各时段容量测试Start----------------------");
  }
  
  @SuppressWarnings("unchecked")
public static Map<String, Object> Json2Map(String str){
	  
	  JSONObject jsonObject = JSONObject.fromObject(str);
      
      Map<String, Object> mapJson = JSONObject.fromObject(jsonObject);
       
      /*for(Entry<String,Object> entry : mapJson.entrySet()){
          Object strval1 = entry.getValue();
          JSONObject jsonObjectStrval1 = JSONObject.fromObject(strval1);
          Map<String, Object> mapJsonObjectStrval1 = JSONObject.fromObject(jsonObjectStrval1);
          System.out.println("KEY:"+entry.getKey()+"  -->  Value:"+entry.getValue()+"\n");
          for(Entry<String, Object> entry1:mapJsonObjectStrval1.entrySet()){
              System.out.println("KEY:"+entry1.getKey()+"  -->  Value:"+entry1.getValue()+"\n");
          }
           
      }*/
      return mapJson;
  }
  
  @DataProvider
	public Iterator<String[]> hospitalPeriod(){
		try {
			return CvsFileUtil.fromCvsFileToIterator("./csv/counter/hospital/period.csv",2);
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
