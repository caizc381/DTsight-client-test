package com.tijiantest.testcase.crm.counter.hospital;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.apache.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.jayway.jsonpath.JsonPath;
import com.tijiantest.base.HttpResult;
import com.tijiantest.base.dbcheck.CounterChecker;
import com.tijiantest.model.counter.BizExceptionEnum;
import com.tijiantest.model.counter.HospitalCapacityConfig;
import com.tijiantest.model.counter.HospitalCapacityConfigDto;
import com.tijiantest.model.counter.HospitalCapacityUsed;
import com.tijiantest.testcase.crm.counter.CounterBase;
import com.tijiantest.util.CvsFileUtil;
import com.tijiantest.util.DateUtils;
import com.tijiantest.util.ListUtil;

public class ResetGlobalCapacityTest extends CounterBase {
	@SuppressWarnings("unused")
	@Test(groups = { "qa" }, dataProvider = "retHospitalCount", description = "恢复默认设置")
	public void test_01_reset(String... args) {
		System.out.println("----------------------医院恢复默认设置测试Start----------------------");
		HospitalCapacityConfigDto hospitalCapacityConfig = new HospitalCapacityConfigDto();

		Integer offsetMonth = Integer.valueOf(args[1]);// 距离现在几个月
		Date start = DateUtils.theFirstDayOfMonth(offsetMonth);
		Date end = DateUtils.theLastDayOfMonth(offsetMonth);
		String startDate = DateUtils.format("yyyy-MM-dd", start);
		String endDate = DateUtils.format("yyyy-MM-dd", end);
		System.out.println("测试开始时间为：" + startDate + " 结束时间为：" + endDate);

		Boolean globalConfig = false;
		Integer hospitalId = defhospital.getId();
		List<Integer> daysOfWeek = new ArrayList<Integer>(new TreeSet<Integer>(DateUtils.daysForWeek(start, end)));
		Map<String, Map<String, Integer>> hosptialPeriodSetting = null;
		hosptialPeriodSetting = CounterChecker.initHospitalPeriodSetting(hospitalId, start, end);
		hospitalCapacityConfig.setDaysOfWeek(daysOfWeek);
		hospitalCapacityConfig.setEndDate(endDate);
		hospitalCapacityConfig.setGlobalConfig(globalConfig);
		hospitalCapacityConfig.setHospitalId(hospitalId);
		hospitalCapacityConfig.setHosptialPeriodSetting(hosptialPeriodSetting);
		hospitalCapacityConfig.setStartDate(startDate);

		// 获取医院自定义表数据
		List<HospitalCapacityUsed> hospitalCountUseds = CounterChecker.getHospitalCapacityUsed(defhospital.getId(),
				null, null, start, end, null);
		// 人数
		Integer totalCount = 1;
		Integer liCount = 1;
		// 获取医院已约量
		if(hospitalCountUseds!=null&&!hospitalCountUseds.isEmpty()){			
			List<Integer> toUsedCount = CounterChecker.getUsedCount(hospitalCountUseds,daysOfWeek,false);
			List<Integer> liUsedCount = CounterChecker.getUsedCount(hospitalCountUseds,daysOfWeek,true);
			if(!toUsedCount.isEmpty())
				totalCount = Collections.max(toUsedCount) + 1;
			if(!liUsedCount.isEmpty())
				liCount = Collections.max(liUsedCount) + 1;
		}
		System.out.println("--------------恢复前先设置体检中心容量--------------");
		HttpResult resultConfig = CounterChecker.HospitalCapacityConfig(false, defhospital.getId(), startDate, endDate, daysOfWeek, totalCount,
				liCount, httpclient);
//		System.out.println(" 设置容量："+resultConfig.getBody());
		List<HospitalCapacityUsed> before = CounterChecker.getHospitalCapacityUsed(defhospital.getId(), null, null,
				start, end, null);
		// System.out.println("恢复前数据为："+JSON.toJSONString(before)+" \n");
		String json = JSON.toJSONString(hospitalCapacityConfig, SerializerFeature.DisableCircularReferenceDetect);
		HttpResult result = httpclient.post(CountHosp_Reset, json);
		if (result.getBody().contains(BizExceptionEnum.NOT_ENOUGH_RESERVE_NUM_LT_USED_NUM.getErrorCode())) {
			Assert.assertEquals(result.getCode(), HttpStatus.SC_BAD_REQUEST, "错误提示：" + result.getBody());
			String text = JsonPath.read(result.getBody(), "$.text").toString();
			System.out.println(text);
		} else {
			Assert.assertEquals(result.getCode(), HttpStatus.SC_OK, "错误提示：" + result.getBody());
			Assert.assertTrue(result.getBody().equals("{}")||result.getBody().equals(""));
		}

		if (checkdb && result.getCode() == HttpStatus.SC_OK) {
			List<HospitalCapacityUsed> hccList = CounterChecker.getHospitalCapacityUsed(defhospital.getId(), null, null,
					start, end, null);
			String days = ListUtil.IntegerlistToString(daysOfWeek);
			List<HospitalCapacityConfig> hospitalCountConfig = CounterChecker.getHospitalCapacityConfig(hospitalId,
					days, null, null);
			// System.out.println("used表："+JSON.toJSONString(hccList)+" \n"
			// + "config表："+JSON.toJSONString(hospitalCountConfig));
			for (HospitalCapacityUsed hcc : hccList) {
				Integer tmpDay = DateUtils.dayForWeek(hcc.getCurrentDate()) + 1;
				List<HospitalCapacityConfig> tmpHosCountConfig = hospitalCountConfig.stream()
						.filter(count -> count.getDayOfWeek() == tmpDay).collect(Collectors.toList());
				for (HospitalCapacityConfig h : tmpHosCountConfig) {
					if (h.getPeriodId() == hcc.getPeriodId())
						if (h.getExamItem() == hcc.getExamItem()) {
							Assert.assertEquals(hcc.getMaxNum(), h.getMaxNum());
							Assert.assertEquals(hcc.getConfigType(), 0);
						}
				}
			}

		}
		System.out.println("----------------------医院恢复默认设置测试End----------------------");
	}

	

	@DataProvider
	public Iterator<String[]> retHospitalCount() {
		try {
			return CvsFileUtil.fromCvsFileToIterator("./csv/counter/hospital/retCount.csv", 3);
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
