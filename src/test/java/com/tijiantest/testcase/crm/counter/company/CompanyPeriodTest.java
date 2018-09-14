package com.tijiantest.testcase.crm.counter.company;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.tijiantest.base.HttpResult;
import com.tijiantest.base.dbcheck.CounterChecker;
import com.tijiantest.model.counter.BizExceptionEnum;
import com.tijiantest.model.counter.CompanyCapacityCell;
import com.tijiantest.model.counter.CompanyCapacityUsed;
import com.tijiantest.model.counter.CompanyCapacityUsedParam;
import com.tijiantest.model.counter.CompanyPeriodCapacitySetDto;
import com.tijiantest.model.counter.HospitalCapacityCell;
import com.tijiantest.model.counter.HospitalCapacityUsed;
import com.tijiantest.testcase.crm.counter.CounterBase;
import com.tijiantest.util.CvsFileUtil;
import com.tijiantest.util.DateUtils;

import net.sf.json.JSONObject;

public class CompanyPeriodTest extends CounterBase {
	/**
	 * 未验证限制项相关，只验证了总人数 只验证了正常情况，和设置的人数不够
	 */
	@SuppressWarnings("unused")
	@Test(description = "设置单位各时段预留", dataProvider = "companyPeriod", groups = { "qa" })
	public void test_peroid(String... args) {
		Map<String, Map<String, CompanyCapacityCell>> companyMap = new HashMap<>();
		Integer hospitalId = defhospital.getId();
		Integer companyId = defnewcompany.getId();
		Boolean isPositive = Boolean.parseBoolean(args[1]);
		Integer offSetDay = Integer.valueOf(args[2]);
		Date start = new Date();
		Date end = DateUtils.offsetDestDay(start, offSetDay);
		List<Integer> dayRangeIds = CounterChecker.getDayRangeIds(hospitalId);
		// 限制项
		List<Integer> limitItems = CounterChecker.getLimitItemIds(hospitalId);
		limitItems.add(-1);
		System.out.println("开始时间为：" + sdf.format(start) + "  结束时间为：" + sdf.format(end));
		List<Map<Integer, HospitalCapacityCell>> hospitalCapacity = null;
		Date tmpDate = start;
		while (tmpDate.getTime() != end.getTime()) {
			Boolean isZero = true;// 是休息日
			// if(!curDates.contains(tmpDate.getTime())){//未在已预留日期，并排除休息日的情况
			isZero = CounterChecker.isRestDay(hospitalId, tmpDate, dayRangeIds);
			// }
			if (!isZero) {
				break;
			}
			Calendar c = Calendar.getInstance();
			c.setTime(tmpDate);
			c.add(Calendar.DAY_OF_MONTH, 1);// +1天
			tmpDate = c.getTime();
		}
		System.out.println("预留的日期为" + sdf.format(tmpDate));
		CompanyPeriodCapacitySetDto compPeriodCapDto = new CompanyPeriodCapacitySetDto();
		compPeriodCapDto.setStartDate(tmpDate);
		compPeriodCapDto.setEndDate(tmpDate);
		// }
		// 获取该日体检中心最大/最小预约量，只计算总人数
		hospitalCapacity = CounterChecker.getPeriodCapacityByHospital(hospitalId, tmpDate, tmpDate);
		// System.out.println("hospitalCapacity:" +
		// JSON.toJSONString(hospitalCapacity));
		
		List<HospitalCapacityUsed> hospitalCountUseds = CounterChecker.getHospitalCapacityUsed(defhospital.getId(),
				dayRangeIds, null, tmpDate, tmpDate, null);
		// 获取该日单位单位最大/最小预约量
		CompanyCapacityUsedParam param1 = new CompanyCapacityUsedParam();
		param1.setCompanyId(companyId);
		param1.setHospitalId(hospitalId);
		param1.setStartDate(tmpDate);
		param1.setEndDate(tmpDate);
		List<CompanyCapacityUsed> companyCountUsed1 = CounterChecker.getCompanyUsedCapacity(param1);
		
//		 System.out.println("itemCapa:" + JSON.toJSONString(itemCapa));
		for (Integer d : dayRangeIds) {
			Map<String, CompanyCapacityCell> itemCapa = new HashMap<>();
			for(Integer item : limitItems){
				Integer availNum = getAvailNum(hospitalCountUseds,item,d);
				Integer useNum = getUsedNum(companyCountUsed1,item,d);
				Integer reserveNum;
				Integer limitNum;
				if (isPositive) {// 如果验证正常情况，输入的预留值为:最小使用量+体检中最小可约量
					if(availNum>0){
						reserveNum = 1 + useNum;
						limitNum = 1 + useNum;
					}else{
						reserveNum = useNum;
						limitNum = useNum;
					}
				} else {// 如果验证异常情况，输入的预留值大于体检中心最大预约量
					Integer compReserveNum = getReseverNum(companyCountUsed1, item, d);
					reserveNum = availNum + compReserveNum + 1;
					limitNum = availNum + compReserveNum + 1;
				}
				CompanyCapacityCell compCapa = new CompanyCapacityCell();
				compCapa.setReserveNum(reserveNum);
				compCapa.setLimit(limitNum);
				itemCapa.put(item+"", compCapa);
			}
			companyMap.put(d + "", itemCapa);
		}
//		 System.out.println("companyMap:" + JSON.toJSONString(companyMap));
		compPeriodCapDto.setCompanyId(companyId);
		compPeriodCapDto.setCompanyPeriodSetting(companyMap);
		compPeriodCapDto.setHospitalId(hospitalId);
		compPeriodCapDto.setOldCompanyId(companyId);
		String json = JSON.toJSONString(compPeriodCapDto,SerializerFeature.DisableCircularReferenceDetect);

		HttpResult result = httpclient.post(CountComp_Period, json);
		if (isPositive) {
			Assert.assertEquals(result.getCode(), HttpStatus.SC_OK, "错误提示：" + result.getBody());
		} else {
			Assert.assertEquals(result.getCode(), HttpStatus.SC_BAD_REQUEST, "错误提示：" + result.getBody());
			Assert.assertTrue(
					result.getBody().contains(BizExceptionEnum.NOT_ENOUGH_SET_HOSPITAL_CAPACITY.getErrorCode()));
		}

	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> Json2Map(String str) {

		JSONObject jsonObject = JSONObject.fromObject(str);

		Map<String, Object> mapJson = JSONObject.fromObject(jsonObject);

		/*
		 * for(Entry<String,Object> entry : mapJson.entrySet()){ Object strval1
		 * = entry.getValue(); JSONObject jsonObjectStrval1 =
		 * JSONObject.fromObject(strval1); Map<String, Object>
		 * mapJsonObjectStrval1 = JSONObject.fromObject(jsonObjectStrval1);
		 * System.out.println("KEY:"+entry.getKey()+"  -->  Value:"+entry.
		 * getValue()+"\n"); for(Entry<String, Object>
		 * entry1:mapJsonObjectStrval1.entrySet()){
		 * System.out.println("KEY:"+entry1.getKey()+"  -->  Value:"+entry1.
		 * getValue()+"\n"); }
		 * 
		 * }
		 */
		return mapJson;
	}

	@DataProvider(name = "companyPeriod")
	public Iterator<String[]> companyPeriod() {
		try {
			return CvsFileUtil.fromCvsFileToIterator("./csv/counter/company/companyPeriod.csv", 2);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 获取体检中心可约量
	 * @param hospitalCountUseds
	 * @param isLimitItem
	 * @return
	 */
	public Integer getAvailNum(List<HospitalCapacityUsed> hospitalCountUseds,Integer itemId,Integer peroidId){
		Integer availNum=0;
		List<HospitalCapacityUsed> hosCounts = new ArrayList<>();
		hosCounts = hospitalCountUseds.stream().filter(h->h.getExamItem()==itemId.intValue()&&h.getPeriodId()==peroidId.intValue()).collect(Collectors.toList());
	
		if(!hosCounts.isEmpty())
			availNum = hosCounts.get(0).getAvailableNum();
		return availNum;
	}
	
	/**
	 * 获取单位已约量
	 * @param companyCountUsed
	 * @param itemId
	 * @param peroidId
	 * @return
	 */
	public Integer getUsedNum(List<CompanyCapacityUsed> companyCountUsed,Integer itemId,Integer peroidId){
		Integer usedNum=0;
		List<CompanyCapacityUsed> compCounts = new ArrayList<>();
		compCounts = companyCountUsed.stream().filter(c->c.getExamItem()==itemId.intValue()&&c.getPeriodId()==peroidId.intValue()).collect(Collectors.toList());
		if(!compCounts.isEmpty())
			usedNum = compCounts.get(0).getUsedNum();
		return usedNum;
	}
	
	/**
	 * 获取 单位预留量
	 * @param companyCountUsed
	 * @param itemId
	 * @param peroidId
	 * @return
	 */
	public Integer getReseverNum(List<CompanyCapacityUsed> companyCountUsed,Integer itemId,Integer peroidId){
		Integer reserverNum=0;
		List<CompanyCapacityUsed> compCounts = new ArrayList<>();
		compCounts = companyCountUsed.stream().filter(c->c.getExamItem()==itemId.intValue()&&c.getPeriodId()==peroidId.intValue()).collect(Collectors.toList());
		if(!compCounts.isEmpty())
			reserverNum = compCounts.get(0).getReservationNum();
		return reserverNum;
	}
}
