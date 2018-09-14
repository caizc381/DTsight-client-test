package com.tijiantest.testcase.crm.counter.hospital;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.tijiantest.base.HttpResult;
import com.tijiantest.base.dbcheck.CounterChecker;
import com.tijiantest.model.counter.BizExceptionEnum;
import com.tijiantest.model.counter.HospitalCapacityConfig;
import com.tijiantest.model.counter.HospitalCapacityUsed;
import com.tijiantest.testcase.crm.counter.CounterBase;
import com.tijiantest.util.CvsFileUtil;
import com.tijiantest.util.DateUtils;
import com.tijiantest.util.ListUtil;
import com.tijiantest.util.db.SqlException;

public class HospitalCapacityConfigTest extends CounterBase {

	@Test(dataProvider = "hospitalCapacityConfigs", description = "医院设置自定义容量", groups = { "qa" })
	public void test_01_customizedConfig(String... args) throws SqlException, ParseException {
		System.out.println("-------------------自定义设置医院人数Start-------------------");
		Integer offsetMonth = Integer.valueOf(args[1]);// 距离现在几个月
		Date start = DateUtils.theFirstDayOfMonth(offsetMonth);
		Date end = DateUtils.theLastDayOfMonth(offsetMonth);
		String startDate = DateUtils.format("yyyy-MM-dd", start);
		String endDate = DateUtils.format("yyyy-MM-dd", end);
		System.out.println("设置自定义容量开始时间为：" + startDate + " 结束时间为：" + endDate);
		// 时段
		List<Integer> dayRangeIds = CounterChecker.getDayRange(defhospital.getId()).stream().map(d -> d.getId())
				.collect(Collectors.toList());
		// 限制项
		List<Integer> limitItems = CounterChecker.getLimitItem(defhospital.getId()).stream().map(l -> l.getId())
				.collect(Collectors.toList());
		// 周几
		String[] days = args[2].split("#");
		List<Integer> daysOfWeek = ListUtil.StringArraysToIntegerList(days);

		// 人数
		Integer totalCount = null;
		Integer limitItemCount = null;
		/*
		 * Integer totalCount = Integer.valueOf(args[3]); Integer
		 * limitItemCount=Integer.valueOf(args[4]);
		 */
		// 全局:true;自定义:false
		Boolean isGlobalConfig = false;

		Boolean isPositive = Boolean.parseBoolean(args[5]);

		List<HospitalCapacityUsed> hospitalCountUseds = CounterChecker.getHospitalCapacityUsed(defhospital.getId(),
				dayRangeIds, null, start, end, null);
		List<Integer> toUsedCount = hospitalCountUseds.stream()
				.filter(h -> h.getExamItem() == -1 && daysOfWeek.contains(DateUtils.dayForWeek(h.getCurrentDate()) + 1))
				.map(h -> h.getMaxNum() - h.getAvailableNum()).collect(Collectors.toList());//普通项已使用
		List<Integer> liUsedCount = hospitalCountUseds.stream()
				.filter(h -> h.getExamItem() != -1 && daysOfWeek.contains(DateUtils.dayForWeek(h.getCurrentDate()) + 1))
				.map(h -> h.getMaxNum() - h.getAvailableNum()).collect(Collectors.toList());//限制单项已使用
		if (isPositive) {
			System.out.println("正常情况测试");
			// 取当前最大值+1
			totalCount = Collections.max(toUsedCount.isEmpty()?Arrays.asList(0):toUsedCount) + 1;
			limitItemCount = Collections.max(liUsedCount.isEmpty()?Arrays.asList(0):liUsedCount) + 1;
			System.out.println("totalCount:"+totalCount+"\nlimitItemCount:"+limitItemCount);
			HttpResult result = CounterChecker.ConfigOfHospitalCapacity(isGlobalConfig, defhospital.getId(), startDate,
					endDate, daysOfWeek, totalCount, limitItemCount, dayRangeIds, limitItems, httpclient);
//			System.out.println("response:" + result.getBody());
			waitto(5);
			Assert.assertEquals(result.getCode(), HttpStatus.SC_OK, "错误提示：" + result.getBody());
			Assert.assertTrue(result.getBody().equals( "{}") || result.getBody().equals(""));
		} else if (!isPositive && !hospitalCountUseds.isEmpty()) {
			System.out.println("异常情况测试");
			totalCount = Collections.max(toUsedCount) - 1;
			limitItemCount = Collections.max(liUsedCount) - 1;
			System.out.println("totalCount:"+totalCount+"\nlimitItemCount:"+limitItemCount);
			HttpResult result = CounterChecker.ConfigOfHospitalCapacity(isGlobalConfig, defhospital.getId(), startDate,
					endDate, daysOfWeek, totalCount, limitItemCount, dayRangeIds, limitItems, httpclient);
			waitto(3);
			System.out.println("response:" + result.getBody()+"返回码.."+result.getCode());

			Assert.assertEquals(result.getCode(), HttpStatus.SC_BAD_REQUEST, "错误提示：" + result.getBody());
			Assert.assertTrue(
					result.getBody().contains(BizExceptionEnum.NOT_ENOUGH_RESERVE_NUM_LT_USED_NUM.getErrorCode()));
		}

		if (checkdb) {
			if (isPositive) {
				limitItems.add(-1);
				List<HospitalCapacityUsed> hccList = CounterChecker.getHospitalCapacityUsed(defhospital.getId(),
						dayRangeIds, limitItems, sdf.parse(startDate), sdf.parse(endDate), null);
				Integer tmpTotalCount = hccList.stream()
						.filter(h -> h.getExamItem().intValue() == -1// 总人数
								&& daysOfWeek.contains(DateUtils.dayForWeek(h.getCurrentDate())+1))// 所设置的周几
						.collect(Collectors.toList()).get(0).getMaxNum();
				Integer tmpLimitItemCount = hccList.stream()
						.filter(h -> h.getExamItem().intValue() != -1// 限制项
								&& daysOfWeek.contains(DateUtils.dayForWeek(h.getCurrentDate())+1))
						.collect(Collectors.toList()).get(0).getMaxNum();
				Assert.assertEquals(tmpTotalCount, totalCount);
				Assert.assertEquals(tmpLimitItemCount, limitItemCount);
			} else {
			}
		}
		System.out.println("-------------------自定义设置医院人数End-------------------");
	}

	@Test(dataProvider = "hospitalCapacityConfigs",description = "医院全局设置容量",groups = { "qa" })
	public void test_02_globalConfig(String... args) throws SqlException, ParseException {
		System.out.println("-------------------全局设置医院人数Start-------------------");
		Integer offsetMonth = Integer.valueOf(args[1]);// 距离现在几个月
		Date start = DateUtils.theFirstDayOfMonth(offsetMonth);
		Date end = DateUtils.theLastDayOfMonth(offsetMonth);
		String startDate = DateUtils.format("yyyy-MM-dd", start);
		String endDate = DateUtils.format("yyyy-MM-dd", end);
		System.out.println("设置自定义容量开始时间为：" + startDate + " 结束时间为：" + endDate);
		// 时段
		List<Integer> dayRangeIds = CounterChecker.getDayRange(defhospital.getId()).stream().map(d -> d.getId())
				.collect(Collectors.toList());
		// 限制项
		List<Integer> limitItems = CounterChecker.getLimitItem(defhospital.getId()).stream().map(l -> l.getId())
				.collect(Collectors.toList());
		// 周几
		String[] days = args[2].split("#");
		List<Integer> daysOfWeek = ListUtil.StringArraysToIntegerList(days);

		// 人数
		Integer totalCount = null;
		Integer limitItemCount = null;
		/*
		 * Integer totalCount = Integer.valueOf(args[3]); Integer
		 * limitItemCount=Integer.valueOf(args[4]);
		 */
		// 全局:true;自定义:false
		Boolean isGlobalConfig = true;
		Boolean isPositive = Boolean.parseBoolean(args[5]);

		// 获取医院自定义表数据
		List<HospitalCapacityUsed> hospitalCountUseds = CounterChecker.getHospitalCapacityUsed(defhospital.getId(),
				dayRangeIds, null, null, null, null);
		// 获取医院已约量
		List<Integer> toUsedCount = hospitalCountUseds.stream()
				.filter(h -> h.getExamItem() == -1 && h.getConfigType() == 0
						&& daysOfWeek.contains(DateUtils.dayForWeek(h.getCurrentDate()) + 1)
						&& !h.getCurrentDate().before(new Date()))// 全局设置容量只会比较与设置今天及今天之后的的数据
				.map(h -> h.getMaxNum() - h.getAvailableNum()).collect(Collectors.toList());
		List<Integer> liUsedCount = hospitalCountUseds.stream()
				.filter(h -> h.getExamItem() != -1 && h.getConfigType() == 0
						&& daysOfWeek.contains(DateUtils.dayForWeek(h.getCurrentDate()) + 1)
						&& !h.getCurrentDate().before(new Date()))
				.map(h -> h.getMaxNum() - h.getAvailableNum()).collect(Collectors.toList());
		
		List<HospitalCapacityUsed> orginHccList = CounterChecker.getHospitalCapacityUsed(defhospital.getId(),
				dayRangeIds, limitItems, sdf.parse(startDate), sdf.parse(endDate), null);
		
		if (isPositive) {
			System.out.println("正常情况测试，输入的容量值比现有最大值大");
			// 取当前最大值+1
			totalCount = Collections.max(toUsedCount) + 1;
			limitItemCount = Collections.max(liUsedCount) + 1;

			HttpResult result = CounterChecker.ConfigOfHospitalCapacity(isGlobalConfig, defhospital.getId(), null, null,
					daysOfWeek, totalCount, limitItemCount, dayRangeIds, limitItems, httpclient);
//			System.out.println("response:" + result.getBody());

			Assert.assertEquals(result.getCode(), HttpStatus.SC_OK, "错误提示：" + result.getBody());
			Assert.assertTrue(result.getBody().equals("{}")||result.getBody().equals(""));
		} else {
			System.out.println("异常情况测试，输入的容量值比现有最大值小");
			totalCount = Collections.max(toUsedCount) - 1;
			limitItemCount = Collections.max(liUsedCount) - 1;

			HttpResult result = CounterChecker.ConfigOfHospitalCapacity(isGlobalConfig, defhospital.getId(), null, null,
					daysOfWeek, totalCount, limitItemCount, dayRangeIds, limitItems, httpclient);
			System.out.println("response:" + result.getBody()+"返回码是.."+result.getCode());

			Assert.assertEquals(result.getCode(), HttpStatus.SC_BAD_REQUEST, "错误提示：" + result.getBody());
			Assert.assertTrue(
					result.getBody().contains(BizExceptionEnum.NOT_ENOUGH_RESERVE_NUM_LT_USED_NUM.getErrorCode()));
		}

		if (checkdb) {
			if (isPositive) {
				limitItems.add(-1);
				List<HospitalCapacityUsed> hccList = CounterChecker.getHospitalCapacityUsed(defhospital.getId(),
						dayRangeIds, limitItems, sdf.parse(startDate), sdf.parse(endDate), null);
//				System.out.println("hccList:" + JSON.toJSONString(hccList));
				if (hccList != null && !hccList.isEmpty()) {
					Integer tmpTotalCount = getTmpTotalCount(hccList,true,daysOfWeek,false);
					Integer tmpLimitItemCount = getTmpTotalCount(hccList,false,daysOfWeek,false);
					if(tmpTotalCount!=null)
						Assert.assertEquals(tmpTotalCount, totalCount);
					else{
						Integer tmpTotalCount1 = getTmpTotalCount(orginHccList,true,daysOfWeek,true);
						Integer originTmpTotalCount = getTmpTotalCount(orginHccList,true,daysOfWeek,true);
						Assert.assertEquals(tmpTotalCount1, originTmpTotalCount);
					}
					if(tmpLimitItemCount!=null)
						Assert.assertEquals(tmpLimitItemCount, limitItemCount);
					else{
						Integer tmpLimitItemCount1 = getTmpTotalCount(orginHccList,false,daysOfWeek,true);
						Integer originTmpLimitItemCount = getTmpTotalCount(orginHccList,false,daysOfWeek,true);
						Assert.assertEquals(tmpLimitItemCount1, originTmpLimitItemCount);
					}
				} else {
					List<HospitalCapacityConfig> hccList1 = CounterChecker.getHospitalCapacityConfig(
							defhospital.getId(), ListUtil.IntegerlistToString(daysOfWeek), dayRangeIds, limitItems);
					Integer tmpTotalCount = hccList1.stream()
							.filter(h -> h.getExamItem().intValue() == -1 && daysOfWeek.contains(h.getDayOfWeek()))
							.collect(Collectors.toList()).get(0).getMaxNum();
					Integer tmpLimitItemCount = hccList1.stream()
							.filter(h -> h.getExamItem().intValue() != -1 && daysOfWeek.contains(h.getDayOfWeek()))
							.collect(Collectors.toList()).get(0).getMaxNum();
					Assert.assertEquals(tmpTotalCount, totalCount);
					Assert.assertEquals(tmpLimitItemCount, limitItemCount);
				}
			} else {
			}
		}
		System.out.println("-------------------全局设置医院人数End-------------------");
	}
	/**
	 * 获取最大值
	 * @param hccList
	 * @param isLimitItem，总人数：false，限制项：true
	 * @param daysOfWeek 周几
	 * @param isCustomized 是否自定义
	 * @return
	 */
	@SuppressWarnings("unused")
	public Integer getTmpTotalCount(List<HospitalCapacityUsed> hccList,Boolean isLimitItem,List<Integer> daysOfWeek,Boolean isCustomized){
//		Integer 
		for(HospitalCapacityUsed h : hccList){
			Integer d = DateUtils.dayForWeek(h.getCurrentDate()) + 1;
			if(isLimitItem&&h.getExamItem().intValue()==-1&&h.getConfigType() == (isCustomized?1:0)){
//				System.out.println("周几："+d+"  日期为："+sdf.format(h.getCurrentDate()));
				if(h.getCurrentDate().compareTo(new Date())>0&&daysOfWeek.contains(DateUtils.dayForWeek(h.getCurrentDate()) + 1)){
//					System.out.println("*******周几："+d+"  日期为："+sdf.format(h.getCurrentDate()));
					return h.getMaxNum();
				}
			}
			if(!isLimitItem&&h.getExamItem().intValue()!=-1&&h.getConfigType() == (isCustomized?1:0)){
//				System.out.println("周几："+d+"  日期为："+sdf.format(h.getCurrentDate()));
				if(h.getCurrentDate().compareTo(new Date())>0&&daysOfWeek.contains(DateUtils.dayForWeek(h.getCurrentDate()) + 1)){
//					System.out.println("*******周几："+d+"  日期为："+sdf.format(h.getCurrentDate()));
					return h.getMaxNum();
				}
			}
		}
		return null;
	}

	@DataProvider
	public Iterator<String[]> hospitalCapacityConfigs() {
		try {
			return CvsFileUtil.fromCvsFileToIterator("./csv/counter/hospital/capacityConfig.csv", 6);
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
