package com.tijiantest.base.dbcheck;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSONArray;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.util.CollectionUtils;
import org.testng.Assert;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tijiantest.base.BaseTest;
import com.tijiantest.base.HttpResult;
import com.tijiantest.base.MyHttpClient;
import com.tijiantest.model.counter.CompanyCapacityCell;
import com.tijiantest.model.counter.CompanyCapacityInfo;
import com.tijiantest.model.counter.CompanyCapacityUsed;
import com.tijiantest.model.counter.CompanyCapacityUsedParam;
import com.tijiantest.model.counter.CountForCheck;
import com.tijiantest.model.counter.CounterUsedChange;
import com.tijiantest.model.counter.DayRange;
import com.tijiantest.model.counter.HospitalCapacity;
import com.tijiantest.model.counter.HospitalCapacityCell;
import com.tijiantest.model.counter.HospitalCapacityConfig;
import com.tijiantest.model.counter.HospitalCapacityConfigDto;
import com.tijiantest.model.counter.HospitalCapacityUsed;
import com.tijiantest.model.counter.OrderableCapacityCell;
import com.tijiantest.model.item.ExamItem;
import com.tijiantest.model.item.LimitItem;
import com.tijiantest.util.DateUtils;
import com.tijiantest.util.ListUtil;
import com.tijiantest.util.db.DBMapper;
import com.tijiantest.util.db.SqlException;

/**
 * 人数控制验证
 * 
 * @author huifang
 *
 */
public class CounterChecker extends BaseTest {

	// 容量设置为0
	public static final Integer NONE_CACPACITY = -1;

	// 体检单位未设置限额
	public static final Integer NONE_LIMIT = -1;

	/**
	 * 设置体检中心容量
	 * 
	 * @param hospitalId
	 * @param startDate
	 * @param endDate
	 * @param
	 */
	public static HttpResult ConfigOfHospitalCapacity(Boolean isGlobalConfig, Integer hospitalId, String startDate,
			String endDate, List<Integer> daysOfWeek, Integer totalcount, Integer limitItemCount, List<Integer> periods,
			List<Integer> limitItems, MyHttpClient httpclient) {
		Map<String, Map<String, Integer>> hosptialPeriodSetting = new HashMap<>();
		hosptialPeriodSetting = generateHosptialPeriodSetting(hospitalId, totalcount, limitItemCount, periods,
				limitItems);
		HospitalCapacityConfigDto hccd = new HospitalCapacityConfigDto();
		hccd.setHospitalId(hospitalId);
		hccd.setDaysOfWeek(daysOfWeek);
		if (startDate != null)
			hccd.setStartDate(startDate);
		if (endDate != null)
			hccd.setEndDate(endDate);
		hccd.setGlobalConfig(isGlobalConfig);
		hccd.setHosptialPeriodSetting(hosptialPeriodSetting);
		if (!isGlobalConfig) {
			if (startDate == null || endDate == null) {
				System.out.println("自定义设置体检中心容量，开始时间和结束时间不能为空!");
				return null;
			}
		}
		String json = JSON.toJSONString(hccd, SerializerFeature.DisableCircularReferenceDetect);// SerializerFeature.DisableCircularReferenceDetect,关闭引用检测后,解决$ref问题
		HttpResult result = httpclient.post(CountHosp_Config, json);
		log.info("设置返回..."+result.getBody());
		return result;
	}

	/**
	 * 设置体检中心容量
	 * 
	 * @param hospitalId
	 * @param startDate
	 * @param endDate
	 * @param hosptialPeriodSetting
	 */
	public static HttpResult HospitalCapacityConfig(Boolean isGlobalConfig, Integer hospitalId, String startDate,
			String endDate, List<Integer> daysOfWeek, Integer totalcount, Integer limitItemCount,
			MyHttpClient httpclient) {

		List<Integer> periods = getDayRange(hospitalId).stream().map(p -> p.getId()).collect(Collectors.toList());
		List<Integer> limitItems = getLimitItem(hospitalId).stream().map(l -> l.getId()).collect(Collectors.toList());

		HttpResult result = ConfigOfHospitalCapacity(isGlobalConfig, hospitalId, startDate, endDate, daysOfWeek,
				totalcount, limitItemCount, periods, limitItems, httpclient);
		return result;
	}

	/**
	 * @param hospitalId
	 * @param companyId
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public static List<Map<Integer, CompanyCapacityCell>> getPeriodCapacityByCompany(Integer hospitalId,
			Integer companyId, Date startDate, Date endDate) {
		// TODO Auto-generated method stub
		startDate = DateUtils.toDayStartSecond(startDate);
		endDate = DateUtils.toDayStartSecond(endDate);
		List<Map<Integer, CompanyCapacityCell>> periodCapacityLst = initPeriodCapacityCollection(startDate, endDate);
		System.out.println("periodCapacityLst.size():" + periodCapacityLst.size());
		if (companyId == null) {
			return periodCapacityLst;
		}

		CompanyCapacityUsedParam usedCapacityDto = new CompanyCapacityUsedParam();
		usedCapacityDto.setCompanyId(companyId);
		usedCapacityDto.setHospitalId(hospitalId);
		usedCapacityDto.setDayRanges(getDayRangeIds(hospitalId));
		usedCapacityDto.setExamItems(new ArrayList<Integer>(Arrays.asList(new Integer[] { -1 })));
		usedCapacityDto.setStartDate(startDate);
		usedCapacityDto.setEndDate(endDate);
		List<CompanyCapacityUsed> capacityUsedLst = getCompanyUsedCapacity(usedCapacityDto);
		if (capacityUsedLst == null) {
			return periodCapacityLst;
		}
		for (CompanyCapacityUsed capacityUsed : capacityUsedLst) {
			Date current = capacityUsed.getCurrentDate();
			int dayInterval = DateUtils.daysBetween(startDate, current);
			Map<Integer, CompanyCapacityCell> currentCapacity = periodCapacityLst.get(dayInterval);
			if (null == currentCapacity) {
				currentCapacity = new HashMap<Integer, CompanyCapacityCell>();
				periodCapacityLst.set(dayInterval, currentCapacity);
			}
			CompanyCapacityCell capacityCell = new CompanyCapacityCell();
			capacityCell.setUsedNum(capacityUsed.getUsedNum());
			capacityCell.setReserveNum(capacityUsed.getReservationNum());
			capacityCell.setLimit(capacityUsed.getMaxNum());
			capacityCell.setRelease(capacityUsed.getRelease());
			currentCapacity.put(capacityUsed.getPeriodId(), capacityCell);
			// periodCapacityLst.add(currentCapacity);
			periodCapacityLst.set(dayInterval, currentCapacity);
		}
		return periodCapacityLst;
	}

	/**
	 * 获取体检中心容量与可预约量 先取used表记录，如果没有取config表记录
	 * 
	 * @param hospitalId
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public static List<Map<Integer, HospitalCapacityCell>> getPeriodCapacityByHospital(Integer hospitalId,
			Date startDate, Date endDate) {
		startDate = DateUtils.toDayStartSecond(startDate);
		endDate = DateUtils.toDayStartSecond(endDate);
		// 获取该体检中心的各时段Id
		List<Integer> dayRangeIds = getDayRangeIds(hospitalId);
		// 从tb_hospital_capacity_used获取<date, <dayRange, capacity>>
		Map<Date, Map<Integer, HospitalCapacityCell>> periodUsedCapacitys = getPeriodUsedCapacity(hospitalId,
				dayRangeIds, startDate, endDate);
		// 从tb_hospital_capacity_config获取<dayOfWeek, <dayRange, capacity>>
		Map<Integer, Map<Integer, HospitalCapacityCell>> periodConfigCapacitys = getPeriodConfigCapacity(hospitalId,
				dayRangeIds);
		// 先取used表记录，如果没有取config表记录
		return mergeItemCapacity(periodUsedCapacitys, periodConfigCapacitys, dayRangeIds, startDate, endDate);
	}

	/**
	 * 先取used表记录，如果没有取config表记录
	 * 
	 * @param usedCapacitys
	 * @param configCapacitys
	 * @param dayRangeIds
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	private static List<Map<Integer, HospitalCapacityCell>> mergeItemCapacity(
			Map<Date, Map<Integer, HospitalCapacityCell>> usedCapacitys,
			Map<Integer, Map<Integer, HospitalCapacityCell>> configCapacitys, List<Integer> dayRangeIds, Date startDate,
			Date endDate) {
		// TODO Auto-generated method stub
		List<Map<Integer, HospitalCapacityCell>> capacityLst = new ArrayList<Map<Integer, HospitalCapacityCell>>();
		Calendar endCalendar = Calendar.getInstance();
		endCalendar.setTime(endDate);
		Calendar currentCalendar = Calendar.getInstance();
		currentCalendar.setTime(startDate);
		for (; !currentCalendar.after(endCalendar); currentCalendar.add(Calendar.DAY_OF_MONTH, 1)) {
			// 获取某天的各时段余量
			Map<Integer, HospitalCapacityCell> periodUsedCapacity = usedCapacitys.get(currentCalendar.getTime());
			if (null == periodUsedCapacity) {
				periodUsedCapacity = new HashMap<Integer, HospitalCapacityCell>();
			}
			for (Integer periodId : dayRangeIds) {
				// 先取used表记录，如果used没有记录，再去config表记录
				if (null != periodUsedCapacity.get(periodId)) {
					continue;
				}
				int currentDayOfWeek = currentCalendar.get(Calendar.DAY_OF_WEEK);
				HospitalCapacityCell hCapacityCell = null;
				if (null != configCapacitys.get(currentDayOfWeek)
						&& null != configCapacitys.get(currentDayOfWeek).get(periodId)) {
					hCapacityCell = configCapacitys.get(currentDayOfWeek).get(periodId);
				} else {
					hCapacityCell = new HospitalCapacityCell();
					hCapacityCell.setAvailableNum(0);
					hCapacityCell.setCapacity(0);
				}
				periodUsedCapacity.put(periodId, hCapacityCell);
			}
			capacityLst.add(periodUsedCapacity);
		}
		return capacityLst;
	}

	public static List<Map<Integer, CompanyCapacityCell>> initPeriodCapacityCollection(Date startDate, Date endDate) {
		List<Map<Integer, CompanyCapacityCell>> periodCapacityLst = new ArrayList<Map<Integer, CompanyCapacityCell>>();
		Calendar current = Calendar.getInstance();
		current.setTime(startDate);
		Calendar end = Calendar.getInstance();
		end.setTime(endDate);

		current.setTime(startDate);
		for (; !current.after(end); current.add(Calendar.DAY_OF_MONTH, 1)) {
			periodCapacityLst.add(null);
		}
		return periodCapacityLst;
	}

	/**
	 * 获取tb_hospital_capacity_config数据
	 * 
	 * @return <dayOfWeek, <dayRange, capacity>>
	 * @param hospitalId
	 * @param dayRangeIds
	 * @return
	 */
	public static Map<Integer, Map<Integer, HospitalCapacityCell>> getPeriodConfigCapacity(Integer hospitalId,
			List<Integer> dayRangeIds) {
		List<Integer> examItems = new ArrayList<Integer>(Arrays.asList(new Integer[] { LimitItem.TOTAL_NUM_ITEM_ID }));
		List<HospitalCapacityConfig> capacityConfigLst = getHospitalCapacityConfig(hospitalId, null, dayRangeIds,
				examItems);
		for (HospitalCapacityConfig capacityConfig : capacityConfigLst) {
			capacityConfig.setAvailableNum(capacityConfig.getMaxNum());
		}
		return convertData(capacityConfigLst, 5);
	}


	/**
	 * 获取tb_hospital_capacity_config数据 提供体检时间 & period区间 &医院id
	 *
	 * @return <dayOfWeek, <dayRange, capacity>>
	 * @param hospitalId
	 * @param dayRangeIds
	 * @return
	 */
	public static Map<Integer, Map<Integer, HospitalCapacityCell>> getPeriodConfigCapacity(Integer hospitalId,
																						   List<Integer> dayRangeIds,Date examDate) {
		List<Integer> examItems = getLimitItem(hospitalId).stream().map(e -> e.getId()).collect(Collectors.toList());
		examItems.add(-1);
		int dayWeeks = DateUtils.dayForWeek(examDate);
		List<HospitalCapacityConfig> capacityConfigLst = getHospitalCapacityConfig(hospitalId,dayWeeks, dayRangeIds,
				examItems);
		for (HospitalCapacityConfig capacityConfig : capacityConfigLst) {
			capacityConfig.setAvailableNum(capacityConfig.getMaxNum());
		}
		return convertData(capacityConfigLst, 1);
	}

	/**
	 * 获取时段Id
	 * 
	 * @param hospitalId
	 * @return
	 */
	public static List<Integer> getDayRangeIds(Integer hospitalId) {
		List<DayRange> dayRanges = getDayRange(hospitalId);
		List<Integer> dayRangeIds = new ArrayList<Integer>();
		for (DayRange dayRange : dayRanges) {
			dayRangeIds.add(dayRange.getId());
		}
		return dayRangeIds;
	}

	/**
	 * 获取tb_hospital_capacity_used数据，并转化 <date, <dayRange, capacity>>
	 * 
	 * @param hospitalId
	 * @param dayRangeIds
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public static Map<Date, Map<Integer, HospitalCapacityCell>> getPeriodUsedCapacity(Integer hospitalId,
			List<Integer> dayRangeIds, Date startDate, Date endDate) {
		List<Integer> examItems = new ArrayList<Integer>(Arrays.asList(new Integer[] { LimitItem.TOTAL_NUM_ITEM_ID }));
		// 获取体检中心余量表
		List<HospitalCapacityUsed> capacityUsedLst = getHospitalCapacityUsed(hospitalId, dayRangeIds, examItems,
				startDate, endDate, null);
		return convertData(capacityUsedLst, 4);
	}

	/**
	 * 获取tb_hospital_capacity_used数据，并转化 <date, <dayRange, capacity>>
	 * 
	 * @param hospitalId
	 * @param dayRangeIds
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public static Map<Integer, Map<Integer, HospitalCapacityCell>> getPeriodUsedCapacity(Integer hospitalId,
			List<Integer> dayRangeIds, Date date) {
		// List<Integer> examItems = new ArrayList<Integer>(Arrays.asList(new
		// Integer[] { LimitItem.TOTAL_NUM_ITEM_ID }));
		List<Integer> examItems = getLimitItem(hospitalId).stream().map(e -> e.getId()).collect(Collectors.toList());
		examItems.add(-1);
		// 获取体检中心余量表
		List<HospitalCapacityUsed> capacityUsedLst = getHospCount(hospitalId, sdf.format(date), dayRangeIds, examItems);
		return convertData(capacityUsedLst, 3);
	}

	/**
	 * 获取tb_company_capacity_used数据，并转化
	 * 
	 * @param hospitalId
	 * @param companyId
	 * @param dayRangeIds
	 * @param date
	 * @return
	 */
	public static Map<Integer, Map<Integer, CompanyCapacityCell>> getPeriodUsedCapacity(Integer hospitalId,
			Integer companyId, List<Integer> dayRangeIds, Date date) {
		// List<Integer> examItems = new ArrayList<Integer>(Arrays.asList(new
		// Integer[] { LimitItem.TOTAL_NUM_ITEM_ID }));
		List<Integer> examItems = getLimitItem(hospitalId).stream().map(e -> e.getId()).collect(Collectors.toList());
		examItems.add(-1);
		// 获取体检中心余量表
		List<CompanyCapacityUsed> capacityUsedLst = getCompCount(hospitalId, companyId, sdf.format(date), dayRangeIds,
				examItems);
		Map<Date, Map<Integer, Map<Integer, CompanyCapacityCell>>> threeMapCompanyCount = convertData(capacityUsedLst);
		Map<Integer, Map<Integer, CompanyCapacityCell>> twoMapCompCount = threeMapCompanyCount.get(date);
		return twoMapCompCount;
	}

	public static OrderableCapacityCell mergeCellValue(HospitalCapacityCell hospitalCapacityCell,
			CompanyCapacityCell companyCapacityCell, Integer orderNum) {
		int cUsedNum = null == companyCapacityCell ? 0 : companyCapacityCell.getUsedNum();
		int cReserveNum = null == companyCapacityCell ? 0 : companyCapacityCell.getReserveNum();
		int cLimitNum = null == companyCapacityCell ? NONE_CACPACITY : companyCapacityCell.getLimit();

		int availableNum = 0;
		int companyAvaible = cReserveNum - cUsedNum > 0 ? cReserveNum - cUsedNum : 0;
		if (null == hospitalCapacityCell || 0 == hospitalCapacityCell.getCapacity()) {// 体检中心容量为0，即休
			availableNum = 0;
		} else if (NONE_CACPACITY == cLimitNum) {// 单位未设置限额
			availableNum = hospitalCapacityCell.getAvailableNum() + companyAvaible;
		} else {
			int availableLimitNum = cLimitNum - cUsedNum > 0 ? cLimitNum - cUsedNum : 0;
			// 如果限额-已约<=体检中心可用量+单位可用量，则取限额-已约；否则，取体检中心可用量+单位可用量
			if (availableLimitNum <= hospitalCapacityCell.getAvailableNum() + companyAvaible) {
//				log.info("限额.."+availableLimitNum +"体检中心可用两"+hospitalCapacityCell.getAvailableNum()+"单位可用"+companyAvaible);
				availableNum = availableLimitNum;
			} else if (availableLimitNum > hospitalCapacityCell.getAvailableNum() + companyAvaible) {
				availableNum = companyAvaible + hospitalCapacityCell.getAvailableNum();
			}
		}

		OrderableCapacityCell orderableCapacityCell = new OrderableCapacityCell();
		orderableCapacityCell.setFromCompany(companyCapacityCell != null);
		orderableCapacityCell.setAvailableNum(availableNum);
		orderableCapacityCell.setLimit(cLimitNum);
		orderableCapacityCell.setEnough(availableNum >= orderNum ? true : false);
		orderableCapacityCell.setRest(0 == hospitalCapacityCell.getCapacity());
		if (companyCapacityCell != null) {
			orderableCapacityCell.setRelease(companyCapacityCell.getRelease());
		}
		return orderableCapacityCell;
	}

	/**
	 * 此处的T通常指Date或DayOfWeek
	 * 
	 * @param capacityLst
	 * @param type
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private static <T> Map<T, Map<Integer, HospitalCapacityCell>> convertData(
			List<? extends HospitalCapacity> capacityLst, Integer type) {
		if (CollectionUtils.isEmpty(capacityLst)) {
			return new HashMap<T, Map<Integer, HospitalCapacityCell>>();
		}
		Map<T, Map<Integer, HospitalCapacityCell>> capacityMap = new HashMap<T, Map<Integer, HospitalCapacityCell>>();
		for (HospitalCapacity capacity : capacityLst) {
			// 获取某天或某个周几的体检中心余量
			Map<Integer, HospitalCapacityCell> periodCapacity = capacityMap.get(getFirstKey(capacity, type));
			if (null == periodCapacity) {
				periodCapacity = new HashMap<Integer, HospitalCapacityCell>();
				capacityMap.put((T) getFirstKey(capacity, type), periodCapacity);
			}
			HospitalCapacityCell cell = new HospitalCapacityCell();

			// 防止值为null的时候转换成int型报空指针异常
			if (capacity.getMaxNum() == null) {
				capacity.setMaxNum(0);
			}
			cell.setCapacity(capacity.getMaxNum());
			if (capacity.getAvailableNum() == null) {
				capacity.setAvailableNum(0);
			}
			cell.setAvailableNum(capacity.getAvailableNum());
			if (capacity instanceof HospitalCapacityUsed) {
				cell.setId(capacity.getId());
			}
			periodCapacity.put(getSecondKey(capacity, type), cell);
		}
		return capacityMap;
	}

	private static Map<Date, Map<Integer, Map<Integer, CompanyCapacityCell>>> convertData(
			List<CompanyCapacityUsed> CompanyCapacityUsedLst) {
		Map<Date, Map<Integer, Map<Integer, CompanyCapacityCell>>> dateCells = new HashMap<Date, Map<Integer, Map<Integer, CompanyCapacityCell>>>();
		for (CompanyCapacityUsed capacityUsed : CompanyCapacityUsedLst) {
			Map<Integer, Map<Integer, CompanyCapacityCell>> periodKeyMaps = dateCells
					.get(capacityUsed.getCurrentDate());
			if (null == periodKeyMaps) {
				periodKeyMaps = new HashMap<Integer, Map<Integer, CompanyCapacityCell>>();
				dateCells.put(capacityUsed.getCurrentDate(), periodKeyMaps);
			}
			Map<Integer, CompanyCapacityCell> itemMaps = periodKeyMaps.get(capacityUsed.getExamItem());
			if (null == itemMaps) {
				itemMaps = new HashMap<Integer, CompanyCapacityCell>();
				periodKeyMaps.put(capacityUsed.getExamItem(), itemMaps);
			}
			CompanyCapacityCell capacityCell = new CompanyCapacityCell();
			capacityCell.setLimit(capacityUsed.getMaxNum());
			capacityCell.setReserveNum(capacityUsed.getReservationNum());
			capacityCell.setUsedNum(capacityUsed.getUsedNum());
			itemMaps.put(capacityUsed.getPeriodId(), capacityCell);
		}
		return dateCells;
	}

	/**
	 * type:1 => <examItem, <dayRange, HospitalCapacityCell>> <br>
	 * type:2 => <Date, <dayRange, capacity>> <br>
	 * type:3 => <dayOfWeek, <dayRange, capacity>> <br>
	 * type:4 => <Date, <examItem, capacity>> <br>
	 * type:5 => <dayOfWeek, <examItem, capacity>> <br>
	 */
	private static Object getFirstKey(HospitalCapacity capacity, int type) {
		switch (type) {
		case 1:
			return capacity.getExamItem();
		case 2:
			return capacity.getPeriodId();
		case 3:
			return capacity.getExamItem();
		case 4:
			HospitalCapacityUsed capacityUsed = (HospitalCapacityUsed) capacity;
			return capacityUsed.getCurrentDate();
		default:
			HospitalCapacityConfig hospCapConfig = (HospitalCapacityConfig) capacity;
			return hospCapConfig.getDayOfWeek();

		}
	}

	private static Integer getSecondKey(HospitalCapacity capacity, int type) {
		switch (type) {
		case 1:
		case 2:
		case 3:
			return capacity.getPeriodId();
		case 4:
			return capacity.getPeriodId();
		case 5:
			return capacity.getPeriodId();
		default:
			return capacity.getExamItem();
		}
	}

	/**
	 * 获取体检中心时段
	 * 
	 * @param hospitalId
	 * @return
	 */
	public static List<DayRange> getDayRange(Integer hospitalId) {
		// TODO Auto-generated method stub
		List<DayRange> drList = new ArrayList<DayRange>();
		String str = "SELECT id, NAME FROM tb_hospital_period_settings WHERE hospital_id = ? order by id asc";
		List<Map<String, Object>> list = null;
		try {
			list = DBMapper.query(str, hospitalId);
		} catch (SqlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (Map<String, Object> map : list) {
			DayRange dr = new DayRange();
			dr.setId(Integer.valueOf(map.get("id").toString()));
			dr.setName(map.get("NAME").toString());
			drList.add(dr);
		}
		return drList;
	}

	/**
	 * 取tb_company_capacity_used数据
	 * 
	 * @param usedCapacityDto
	 * @return
	 */
	@SuppressWarnings("null")
	public static List<CompanyCapacityUsed> getCompanyUsedCapacity(CompanyCapacityUsedParam usedCapacityDto) {
		// TODO Auto-generated method stub
		List<CompanyCapacityUsed> ccuList = new ArrayList<CompanyCapacityUsed>();
		List<Integer>  periodList = HospitalChecker.getHospitalPeriodRangeLists(usedCapacityDto.getHospitalId());
		String sql = "SELECT * FROM tb_company_capacity_used WHERE hospital_id = " + usedCapacityDto.getHospitalId()
				+ " and company_id = " + usedCapacityDto.getCompanyId() + " ";
		if (usedCapacityDto.getDayRanges() != null && usedCapacityDto.getDayRanges().size() > 0) {
			String dayRanges = "(" + ListUtil.IntegerlistToString(usedCapacityDto.getDayRanges()) + ")";
			sql = sql + " and period_id in " + dayRanges + " ";
		}
		if (usedCapacityDto.getExamItems() != null && usedCapacityDto.getExamItems().size() > 0) {
			String itemIds = "(" + ListUtil.IntegerlistToString(usedCapacityDto.getExamItems()) + ")";
			sql = sql + " and exam_item in " + itemIds + " ";
		}
		if (usedCapacityDto.getStartDate() != null && usedCapacityDto.getEndDate() != null) {
			String startDate = sdf.format(usedCapacityDto.getStartDate());
			String endDate = sdf.format(usedCapacityDto.getEndDate());
			sql = sql + " and cur_date between \'" + startDate + "\' and \'" + endDate + "\' ";
		}
		sql = sql + " AND period_id IN ("+ListUtil.IntegerlistToString(periodList)+")";
		log.info("tb_company_capacity_used:" + sql);
		List<Map<String, Object>> list = null;
		try {
			list = DBMapper.query(sql);
		} catch (SqlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (list != null || list.size() > 0) {
			for (Map<String, Object> m : list) {
				CompanyCapacityUsed ccu = new CompanyCapacityUsed();
				ccu.setCompanyId(Integer.valueOf(m.get("company_id").toString()));
				ccu.setCurrentDate((Date) m.get("cur_date"));
				ccu.setExamItem(Integer.valueOf(m.get("exam_item").toString()));
				ccu.setHospitalId(Integer.valueOf(m.get("hospital_id").toString()));
				ccu.setId(Integer.valueOf(m.get("id").toString()));
				ccu.setMaxNum(Integer.valueOf(m.get("max_num").toString()));
				ccu.setPeriodId(Integer.valueOf(m.get("period_id").toString()));
				ccu.setRelease(Integer.valueOf(m.get("is_release").toString()));
				ccu.setReservationNum(Integer.valueOf(m.get("reservation_num").toString()));
				if (m.get("reservation_used_num") != null)
					ccu.setReservationUsedNum(Integer.valueOf(m.get("reservation_used_num").toString()));
				ccu.setUsedNum(Integer.valueOf(m.get("used_num").toString()));
				ccuList.add(ccu);
			}
			return ccuList;
		}
		return null;
	}

	/**
	 * tb_hospital_capacity_config
	 * 
	 * @param hospitalId
	 * @param object
	 *            周几
	 * @param dayRangeIds
	 * @param examItems
	 * @return
	 */
	public static List<HospitalCapacityConfig> getHospitalCapacityConfig(Integer hospitalId, Object object,
			List<Integer> dayRangeIds, List<Integer> examItems) {
		// TODO Auto-generated method stub
		List<HospitalCapacityConfig> hccList = new ArrayList<HospitalCapacityConfig>();
		List<Integer>  periodList = HospitalChecker.getHospitalPeriodRangeLists(hospitalId);
		String sql = "SELECT * FROM tb_hospital_capacity_config WHERE hospital_id = " + hospitalId + " ";
		if (dayRangeIds != null && dayRangeIds.size() > 0) {
			String dayRanges = "(" + ListUtil.IntegerlistToString(dayRangeIds) + ")";
			sql = sql + " and period_id in " + dayRanges + " ";
		}
		if (examItems != null && examItems.size() > 0) {
			String itemIds = "(" + ListUtil.IntegerlistToString(examItems) + ")";
			sql = sql + " and exam_item in " + itemIds + " ";
		}
		if (object != null && object.toString() != "")
			sql = sql + " and day_of_week in (" + object + ") ";
		sql = sql + " AND period_id IN ("+ListUtil.IntegerlistToString(periodList)+")";
		log.info("tb_hospital_capacity_config with week.."+sql);
		List<Map<String, Object>> list = null;
		try {
			list = DBMapper.query(sql);
		} catch (SqlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (list != null && list.size() > 0) {
			for (Map<String, Object> m : list) {
				HospitalCapacityConfig hcc = new HospitalCapacityConfig();
				hcc.setDayOfWeek(Integer.valueOf(m.get("day_of_week").toString()));
				hcc.setExamItem(Integer.valueOf(m.get("exam_item").toString()));
				hcc.setHospitalId(Integer.valueOf(m.get("hospital_id").toString()));
				hcc.setId(Integer.valueOf(m.get("id").toString()));
				hcc.setPeriodId(Integer.valueOf(m.get("period_Id").toString()));
				hcc.setMaxNum(Integer.valueOf(m.get("capacity").toString()));
				hccList.add(hcc);
			}
			return hccList;
		}
		return null;
	}

	/***
	 * tb_hospital_capacity_used
	 * 
	 * @param hospitalId
	 * @param dayRangeIds
	 * @param examItems
	 * @param startDate
	 * @param endDate
	 * @param object
	 * @return
	 */
	public static List<HospitalCapacityUsed> getHospitalCapacityUsed(Integer hospitalId, List<Integer> dayRangeIds,
			List<Integer> examItems, Date startDate, Date endDate, Object object) {
		// TODO Auto-generated method stub
		List<HospitalCapacityUsed> hccList = new ArrayList<HospitalCapacityUsed>();
		List<Integer>  periodList = HospitalChecker.getHospitalPeriodRangeLists(hospitalId);
		String sql = "SELECT * FROM tb_hospital_capacity_used " + "WHERE hospital_id = " + hospitalId + " ";
		if (dayRangeIds != null && !dayRangeIds.isEmpty()) {
			String dayRanges = "(" + ListUtil.IntegerlistToString(dayRangeIds) + ")";
			sql = sql + "AND period_id IN " + dayRanges + " " ;
		}
		if (examItems != null && !examItems.isEmpty()) {
			String itemIds = "(" + ListUtil.IntegerlistToString(examItems) + ")";
			sql = sql + "AND exam_item IN " + itemIds + " ";
		}
		if (startDate != null && endDate != null) {
			String start = sdf.format(startDate);
			String end = sdf.format(endDate);
			sql = sql + "and cur_date between \'" + start + "\' and \'" + end + "\' ";
		}
		/*
		 * if(endDate == null) sql = sql +
		 * "and cur_date >= DATE_FORMAT(now(), \"%Y-%m-%d\")";
		 */
		if (object != null)
			sql = sql + "and cur_date = \'" + object + "\' ";
		sql = sql + " AND period_id IN ("+ListUtil.IntegerlistToString(periodList)+")";
		List<Map<String, Object>> list = null;
		log.info("tb_hospital_capacity_used:" + sql);
		try {
			list = DBMapper.query(sql);
		} catch (SqlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (list != null && list.size() > 0) {
			for (Map<String, Object> m : list) {
				HospitalCapacityUsed hcc = new HospitalCapacityUsed();
				hcc.setAvailableNum(Integer.valueOf(m.get("available_num").toString()));
				hcc.setConfigType(Integer.valueOf(m.get("config_type").toString()));
				hcc.setCurrentDate((Date) m.get("cur_date"));
				hcc.setExamItem(Integer.valueOf(m.get("exam_item").toString()));
				hcc.setHospitalId(Integer.valueOf(m.get("hospital_id").toString()));
				hcc.setId(Integer.valueOf(m.get("id").toString()));
				hcc.setMaxNum(Integer.valueOf(m.get("max_num").toString()));
				hcc.setPeriodId(Integer.valueOf(m.get("period_id").toString()));
				hccList.add(hcc);
			}
		}
		return hccList;
	}

	/**
	 * 获取tb_company_capacity_info数据
	 * 
	 * @param companyId
	 * @param hospitalId
	 * @return
	 */
	public static CompanyCapacityInfo getCapacitySetting(int companyId, int hospitalId) {
		CompanyCapacityInfo cci = new CompanyCapacityInfo();
		String sql = "SELECT * FROM tb_company_capacity_info WHERE hospital_id = " + hospitalId + " AND company_id = "
				+ companyId + ";";
		Map<String, Object> map = null;
		List<Map<String, Object>> list = null;
		try {
			list = DBMapper.query(sql);
		} catch (SqlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (list != null && !list.isEmpty()) {
			map = list.get(0);
			cci.setId(Integer.valueOf(map.get("id").toString()));
			cci.setCompanyId(Integer.valueOf(map.get("company_id").toString()));
			cci.setHospitalId(Integer.valueOf(map.get("hospital_id").toString()));
			if (map.get("ahead_days") != null)
				cci.setAheadDays(Integer.valueOf(map.get("ahead_days").toString()));
			if (map.get("can_order") != null)
				cci.setCanOrder(Integer.valueOf(map.get("can_order").toString()) == 1 ? true : false);
			if (map.get("prompt_text") != null)
				cci.setPromptText(map.get("prompt_text").toString());
			return cci;
		}
		return null;
	}

	public static String Map2Json(Map<Integer, OrderableCapacityCell> m) {
		ObjectMapper mapper = new ObjectMapper();
		String json = "";
		try {
			json = mapper.writeValueAsString(m);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Map2Json：" + json);
		return json;
	}

	/**
	 * 获取体检中心使用表数据
	 * 
	 * @param hospitalId
	 * @param date
	 * @return
	 */
	public static List<HospitalCapacityUsed> getHospitalCount(Integer hospitalId, String date) {
		List<HospitalCapacityUsed> hCUS = new ArrayList<>();
		// 获取时段
		List<DayRange> dayRangeLst = getDayRange(hospitalId);
		List<Integer> dayRangesId = new ArrayList<Integer>();
		dayRangesId.add(dayRangeLst.get(0).getId());
		hCUS = getHospitalCapacityUsed(hospitalId, dayRangesId, null, null, null, date);
		return hCUS;
	}

	/**
	 * 获取体检中心可用人数，先取used表再去config表
	 * 
	 * @param hospitalId
	 * @param date
	 * @param examTime_interval_id
	 * @param examItems
	 * @return
	 */
	public static List<HospitalCapacityUsed> getHospitalCount(Integer hospitalId, String date,
			Integer examTime_interval_id, List<Integer> examItems) {
		List<HospitalCapacityUsed> hCUS = new ArrayList<>();
		// 获取时段
		List<Integer> dayRangesId = Arrays.asList(examTime_interval_id);
		hCUS = getHospitalCapacityUsed(hospitalId, dayRangesId, null, null, null, date);

		if (hCUS != null)
			hCUS = hCUS.stream().filter(hcu -> hcu.getPeriodId() == Integer.valueOf(examTime_interval_id))
					.collect(Collectors.toList());
		else {
			List<HospitalCapacityConfig> hospitalCounterConfig = null;
			try {
				hospitalCounterConfig = CounterChecker.getHospitalCapacityConfig(hospitalId,
						DateUtils.dayForWeek(sdf.parse(date)) + 1, dayRangesId, examItems);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				BeanUtils.copyProperties(hCUS, hospitalCounterConfig);
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return hCUS;
	}

	/**
	 * 获取体检中心可用人数，先取used表再去config表
	 * 
	 * @param hospitalId
	 * @param date
	 * @param examTime_interval_id
	 * @param examItems
	 * @return
	 */
	public static List<HospitalCapacityUsed> getHospCount(Integer hospitalId, String date, List<Integer> dayRangesId,
			List<Integer> examItems) {
		List<HospitalCapacityUsed> hosCountList = new ArrayList<>();
		Date tmpDate = null;
		Object dayForWeek = null;
		try {
			tmpDate = sdf.parse(date);
			dayForWeek = DateUtils.dayForWeek(tmpDate) + 1;
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		List<HospitalCapacityUsed> hCUS = new ArrayList<>();
		hCUS = getHospitalCapacityUsed(hospitalId, dayRangesId, examItems, null, null, date);
		// 获取时段
		if (hCUS != null && !hCUS.isEmpty()) {
			for (Integer d : dayRangesId) {
				for (Integer ei : examItems) {
					List<HospitalCapacityUsed> hosCount = hCUS.stream()
							.filter(hcu -> hcu.getPeriodId() == d.intValue() && hcu.getExamItem() == ei.intValue())
							.collect(Collectors.toList());
					// used表没有，取config表，availableNum取容量
					if (hosCount == null || hosCount.isEmpty()) {
						List<Integer> dayRange = Arrays.asList(d);
						List<Integer> item = Arrays.asList(ei);
						List<HospitalCapacityConfig> hospitalCounterConfig = CounterChecker
								.getHospitalCapacityConfig(hospitalId, dayForWeek, dayRange, item);
						try {
							if (hospitalCounterConfig != null && hospitalCounterConfig.size() > 0)
								for (HospitalCapacityConfig h : hospitalCounterConfig) {
									HospitalCapacityUsed hospitalCountUsed = new HospitalCapacityUsed();
									BeanUtils.copyProperties(hospitalCountUsed, h);
									hospitalCountUsed.setAvailableNum(h.getMaxNum());
									hosCount.add(hospitalCountUsed);
								}
						} catch (IllegalAccessException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (InvocationTargetException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					hosCountList.addAll(hosCount);
				}
			}
		} else {
			List<HospitalCapacityConfig> hospitalCounterConfig = null;
			hospitalCounterConfig = CounterChecker.getHospitalCapacityConfig(hospitalId, dayForWeek, dayRangesId,
					examItems);
			List<HospitalCapacityUsed> hospitalCounterConfigUsedList = new ArrayList<HospitalCapacityUsed>();
			for(HospitalCapacityConfig c : hospitalCounterConfig){
				HospitalCapacityUsed u = new HospitalCapacityUsed();
				u.setMaxNum(c.getMaxNum());
				u.setAvailableNum(c.getMaxNum());
				u.setExamItem(c.getExamItem());
				u.setPeriodId(c.getPeriodId());
				hospitalCounterConfigUsedList.add(u);

			}
			hCUS = hospitalCounterConfigUsedList;
			hosCountList.addAll(hCUS);
		}
		return hosCountList;
	}

	/**
	 * 获取体检中心可用人数，先取used表再去config表
	 * 
	 * @param hospitalId
	 * @param date
	 * @param examTime_interval_id
	 * @param examItems
	 * @return
	 */
	public static List<HospitalCapacityUsed> getHospitalCount(Integer hospitalId, Date start, Date end,
			Integer examTime_interval_id, List<Integer> examItems) {
		List<HospitalCapacityUsed> hCUS = new ArrayList<>();
		List<Integer> dayRangesId = null;
		// 获取时段
		if (examTime_interval_id != null)
			dayRangesId = Arrays.asList(examTime_interval_id);
		hCUS = getHospitalCapacityUsed(hospitalId, dayRangesId, examItems, start, end, null);

		List<Integer> dayWeek = new ArrayList<Integer>(new TreeSet<Integer>(DateUtils.daysForWeek(start, end)));
		if (!hCUS.isEmpty() && examTime_interval_id != null)
			hCUS = hCUS.stream().filter(hcu -> hcu.getPeriodId() == Integer.valueOf(examTime_interval_id))
					.collect(Collectors.toList());
		else {
			List<HospitalCapacityConfig> hospitalCounterConfig = CounterChecker.getHospitalCapacityConfig(hospitalId,
					ListUtil.IntegerlistToString(dayWeek), dayRangesId, examItems);
			try {
				BeanUtils.copyProperties(hCUS, hospitalCounterConfig);
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return hCUS;
	}

	/**
	 * 获取单位预留使用表数据
	 * 
	 * @param hospitalId
	 * @param companyId
	 * @param date
	 * @return
	 */
	public static List<CompanyCapacityUsed> getCompanyCount(Integer hospitalId, Integer companyId, String date) {
		List<CompanyCapacityUsed> cCUS = new ArrayList<>();
		// 获取时段
		List<DayRange> dayRangeLst = getDayRange(hospitalId);
		List<Integer> dayRangesId = new ArrayList<Integer>();
		dayRangesId.add(dayRangeLst.get(0).getId());
		CompanyCapacityUsedParam usedCapacityDto = new CompanyCapacityUsedParam();
		usedCapacityDto.setHospitalId(hospitalId);
		usedCapacityDto.setCompanyId(companyId);
		usedCapacityDto.setDayRanges(dayRangesId);
		try {
			usedCapacityDto.setStartDate(sdf.parse(date));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			usedCapacityDto.setEndDate(sdf.parse(date));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		cCUS = getCompanyUsedCapacity(usedCapacityDto);
		return cCUS;
	}

	/**
	 * 获取单位预留表
	 * 
	 * @param hospitalId
	 * @param companyId
	 * @param date
	 * @param dayRangesId
	 * @param examItems
	 * @return
	 */
	public static List<CompanyCapacityUsed> getCompCount(Integer hospitalId, Integer companyId, String date,
			List<Integer> dayRangesId, List<Integer> examItems) {
		List<CompanyCapacityUsed> compCapacityUsedList = new ArrayList<>();

		// 获取时段
		CompanyCapacityUsedParam usedCapacityDto = new CompanyCapacityUsedParam();
		usedCapacityDto.setHospitalId(hospitalId);
		usedCapacityDto.setCompanyId(companyId);
		usedCapacityDto.setDayRanges(dayRangesId);
		try {
			usedCapacityDto.setStartDate(sdf.parse(date));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			usedCapacityDto.setEndDate(sdf.parse(date));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		compCapacityUsedList = getCompanyUsedCapacity(usedCapacityDto);

		return compCapacityUsedList;
	}

	/**
	 * 获取单位预留表
	 * 
	 * @param hospitalId
	 * @param companyId
	 * @param date
	 * @param dayRangesId
	 * @param examItems
	 * @return
	 */
	public static List<CompanyCapacityUsed> getCompCount(Integer hospitalId, Integer companyId, Date startDate,
			Date endDate, List<Integer> dayRangesId, List<Integer> examItems) {
		List<CompanyCapacityUsed> compCapacityUsedList = new ArrayList<>();

		// 获取时段
		CompanyCapacityUsedParam usedCapacityDto = new CompanyCapacityUsedParam();
		usedCapacityDto.setHospitalId(hospitalId);
		usedCapacityDto.setCompanyId(companyId);
		usedCapacityDto.setDayRanges(dayRangesId);
		usedCapacityDto.setStartDate(startDate);
		usedCapacityDto.setEndDate(endDate);

		compCapacityUsedList = getCompanyUsedCapacity(usedCapacityDto);

		return compCapacityUsedList;
	}

	/**
	 * 验证单位/体检中心扣人数前后人数
	 * 
	 * @param itemId
	 *            限制项
	 * @param beforeCompanyCounter
	 *            扣人数前的单位预留
	 * @param afterCompanyCounter
	 *            扣人数后的单位预留
	 * @param beforeHospitalCounter
	 *            扣人数前的公共池余量
	 * @param afterHospitalCounter
	 *            扣人数后的公共池余量
	 * @param num
	 *            (扣的人数)
	 */
	public static void reduceCounter(Integer itemId, List<CompanyCapacityUsed> beforeCompanyCounter,
			List<CompanyCapacityUsed> afterCompanyCounter, List<HospitalCapacityUsed> beforeHospitalCounter,
			List<HospitalCapacityUsed> afterHospitalCounter, int num) {

		CounterUsedChange change = new CounterUsedChange();
		change = getChange(change, beforeCompanyCounter, beforeHospitalCounter, itemId,true);//扣人数前,单位、体检人数详情
		change = getChange(change, afterCompanyCounter, afterHospitalCounter, itemId,false);//扣人数后,单位、体检人数详情
		System.out.println("测试限制项为:"+itemId+"\n"+change.toString());
		// Assert
		if (afterCompanyCounter != null && !afterCompanyCounter.isEmpty()) {
			Assert.assertEquals(change.getAfterCompUsedNum().intValue(), change.getBeforeCompUsedNum() + num);
			Assert.assertEquals(change.getAfterCompReserNum(), change.getBeforeCompReserNum());
			if (change.getBeforeCompReserNum() - change.getBeforeCompReserUsedNum() >= num) {// 如果单位预留人数足够（单位预留-单位预留已约>=下单数）
				Assert.assertEquals(change.getAfterCompUsedNum().intValue(),
						change.getBeforeCompUsedNum().intValue() + num);// 单位已约增加
				Assert.assertEquals(change.getAfterCompReserNum().intValue(),
						change.getBeforeCompReserNum().intValue());// 单位预留不变
				//单位限额判断
				// 若之前的单位限额>单位已约
				if(change.getBeforeCompUsedNum() < change.getBeforeCompanyMaxNum())
					//&& 单位限额 > 已约数 则单位限额不变（有富余）
					if(change.getBeforeCompanyMaxNum() > change.getBeforeCompUsedNum())
						Assert.assertEquals(change.getAfterCompanyMaxNum().intValue(),
							change.getBeforeCompanyMaxNum().intValue());
					else
						Assert.assertEquals(change.getAfterCompanyMaxNum().intValue(),
								change.getBeforeCompanyMaxNum().intValue() + num );
				else//若单位之前的预留已约数量>= 限额 则单位限额+ （保持单位限额永远>=已约数）
					Assert.assertEquals(change.getAfterCompanyMaxNum().intValue(),
							change.getBeforeCompanyMaxNum().intValue() + num );
				/*if (change.getBeforeCompUsedNum() >= change.getBeforeCompReserUsedNum()) {// 如果扣人数前单位已约>=单位预留已约，则扣人数后，单位预留已约值不变
					Assert.assertEquals(change.getAfterCompReserUsedNum().intValue(),
							change.getBeforeCompReserUsedNum().intValue());
				} else {
					Assert.assertEquals(change.getAfterCompReserNum().intValue(),
							change.getBeforeCompReserUsedNum().intValue() + num);
				}*/
				
				Assert.assertEquals(change.getAfterCompReserUsedNum().intValue(),
						change.getBeforeCompReserUsedNum().intValue() + num);// 单位预留已约增加
				
				// 体检中心人数不变
				Assert.assertEquals(change.getAfterHosAvailNum().intValue(), change.getBeforeHosAvailNum().intValue());
				Assert.assertEquals(change.getAfterHospitalMaxNum().intValue(), change.getBeforeHospitalMaxNum().intValue());
			} else {// 如果单位预留不足，（单位预留-单位预留已约<下单数）
				Assert.assertEquals(change.getAfterCompUsedNum().intValue(),
						change.getBeforeCompUsedNum().intValue() + num);
				Assert.assertEquals(change.getAfterCompReserNum().intValue(), change.getBeforeCompReserNum().intValue());// 单位预留不变
				Assert.assertEquals(change.getAfterCompReserUsedNum().intValue(),change.getBeforeCompReserUsedNum().intValue());// 单位预留已约不变
				int offNum = num - (change.getBeforeCompReserNum() - change.getBeforeCompReserUsedNum());// 需要体检中心扣除的量
				if (change.getBeforeCompanyMaxNum() != -1) {
					if (change.getBeforeCompUsedNum() >= change.getBeforeCompanyMaxNum())// 如果原单位已约量>=原单位限额，扣人数后，单位限额扩容，（原单位限额+需要体检中心扣除的量）
						Assert.assertEquals(change.getAfterCompanyMaxNum().intValue(),
								change.getBeforeCompanyMaxNum().intValue() + offNum);
					else
						Assert.assertEquals(change.getAfterCompanyMaxNum().intValue(),
								change.getBeforeCompanyMaxNum().intValue());
				}
				if (change.getBeforeHosAvailNum() >= offNum) {// 如果体检中心余量足够，（体检中心余量>=需要体检中心扣除的量）
					Assert.assertEquals(change.getAfterHosAvailNum().intValue(),
							change.getBeforeHosAvailNum() - offNum);
					Assert.assertEquals(change.getAfterHospitalMaxNum().intValue(),//体检中心容量不变
							change.getBeforeHospitalMaxNum().intValue());
				} else {// 如果体检中心余量不足
					Assert.assertEquals(change.getAfterHosAvailNum().intValue(), 0);// 预约后，体检中心余量为0
					Assert.assertEquals(change.getAfterHospitalMaxNum().intValue(), 
							change.getBeforeHospitalMaxNum() + (offNum - change.getBeforeHosAvailNum()));// 预约后，体检中心容量扩容至（需要体检中心扣除的人数-体检中心之前的余量）
				}
			}
		}
	}
	
	/**
	 * 集合单位与医院人数详情
	 * @param change
	 * @param companyCount
	 * @param hospitalCount
	 * @param itemId
	 * @param isBefore
	 * @return
	 */
	public static CounterUsedChange getChange(CounterUsedChange change,List<CompanyCapacityUsed> companyCount,
			List<HospitalCapacityUsed> hospitalCount,Integer itemId,Boolean isBefore){
		if(isBefore){			
			change.setBeforeHosAvailNum(getHosCountSpecie(hospitalCount, itemId).get(0).getAvailableNum());
			change.setBeforeHospitalMaxNum(getHosCountSpecie(hospitalCount, itemId).get(0).getMaxNum());
			if (companyCount != null && !companyCount.isEmpty()) {
				change.setBeforeCompanyMaxNum(getCompCountSpecie(companyCount, itemId).get(0).getMaxNum());
				change.setBeforeCompReserNum(getCompCountSpecie(companyCount, itemId).get(0).getReservationNum());
				change.setBeforeCompReserUsedNum(
						getCompCountSpecie(companyCount, itemId).get(0).getReservationUsedNum());
				change.setBeforeCompUsedNum(getCompCountSpecie(companyCount, itemId).get(0).getUsedNum());
			}
			return change;
		}
		change.setAfterHosAvailNum(getHosCountSpecie(hospitalCount, itemId).get(0).getAvailableNum());
		change.setAfterHospitalMaxNum(getHosCountSpecie(hospitalCount, itemId).get(0).getMaxNum());
		if (companyCount != null && !companyCount.isEmpty()) {
			change.setAfterCompanyMaxNum(getCompCountSpecie(companyCount, itemId).get(0).getMaxNum());
			change.setAfterCompReserNum(getCompCountSpecie(companyCount, itemId).get(0).getReservationNum());
			change.setAfterCompReserUsedNum(getCompCountSpecie(companyCount, itemId).get(0).getReservationUsedNum());
			change.setAfterCompUsedNum(getCompCountSpecie(companyCount, itemId).get(0).getUsedNum());
		}
		return change;
	}

	/**
	 * 根据单项筛选单位人数详情
	 * @param compCount
	 * @param itemId
	 * @return
	 */
	public static List<CompanyCapacityUsed> getCompCountSpecie(List<CompanyCapacityUsed> compCount, Integer itemId) {
		compCount = compCount.stream().filter(c -> c.getExamItem().intValue() == itemId.intValue()).collect(Collectors.toList());
		return compCount;
	}

	/**
	 * 根据单项筛选医院人数详情
	 * @param hosCount
	 * @param itemId
	 * @return
	 */
	public static List<HospitalCapacityUsed> getHosCountSpecie(List<HospitalCapacityUsed> hosCount, Integer itemId) {
		hosCount = hosCount.stream().filter(c -> c.getExamItem().intValue() == itemId.intValue()).collect(Collectors.toList());
		log.info("hosCount..."+ JSONArray.toJSONString(hosCount));
		return hosCount;
	}

	/**
	 * 验证单位/体检中心回收前后人数
	 * 
	 * @param itemId
	 *            限制项
	 * @param beforeCompanyCounter
	 * @param afterCompanyCounter
	 * @param beforeHospitalCounter
	 * @param afterHospitalCounter
	 * @param num(扣的人数)
	 */
	public static void recycleCounterCheck(Integer itemId, List<CompanyCapacityUsed> beforeCompanyCounter,
			List<CompanyCapacityUsed> afterCompanyCounter, List<HospitalCapacityUsed> beforeHospitalCounter,
			List<HospitalCapacityUsed> afterHospitalCounter, int num) {
		Integer beforeUsedNum = 0, beforeReservationNum = 0, beforeCompanyMaxNum = 0, beforeAvailNum = 0,
				beforeHospitalMaxNum = 0, beforeIsRelease = 0, beforeReservationUsedNum = 0;
		@SuppressWarnings("unused")
		Integer afterUsedNum = 0, afterReservationNum = 0, afterCompanyMaxNum = 0, afterAvailNum = 0,
				afterHospitalMaxNum = 0, afterIsRelease = 0, afterReservationUsedNum = 0;

		// 扣人数前
		beforeAvailNum = beforeHospitalCounter.stream().filter(c -> c.getExamItem().intValue() == itemId.intValue())
				.collect(Collectors.toList()).get(0).getAvailableNum();
		beforeHospitalMaxNum = beforeHospitalCounter.stream().filter(c -> c.getExamItem().intValue() == itemId.intValue())
				.collect(Collectors.toList()).get(0).getMaxNum();
		System.out.println("限制项Id:" + itemId + " 回收前体检中心可约量：" + beforeAvailNum + " 容量：" + beforeHospitalMaxNum);
		if (beforeCompanyCounter != null && !beforeCompanyCounter.isEmpty()) {
			beforeUsedNum = beforeCompanyCounter.stream().filter(c -> c.getExamItem().intValue() == itemId.intValue())
					.collect(Collectors.toList()).get(0).getUsedNum();
			beforeReservationNum = beforeCompanyCounter.stream().filter(c -> c.getExamItem().intValue() == itemId.intValue())
					.collect(Collectors.toList()).get(0).getReservationNum();
			beforeCompanyMaxNum = beforeCompanyCounter.stream().filter(c -> c.getExamItem().intValue() == itemId.intValue())
					.collect(Collectors.toList()).get(0).getMaxNum();
			beforeIsRelease = beforeCompanyCounter.stream().filter(c -> c.getExamItem().intValue() == itemId.intValue())
					.collect(Collectors.toList()).get(0).getRelease();
			System.out.println("限制项Id:" + itemId + " 回收前单位已约：" + beforeUsedNum + " 预留数：" + beforeReservationNum + ""
					+ "  限额：" + beforeCompanyMaxNum + " 预留已使用量：" + beforeReservationUsedNum);
		}
		// 扣人数后
		afterAvailNum = afterHospitalCounter.stream().filter(c -> c.getExamItem() == itemId.intValue())
				.collect(Collectors.toList()).get(0).getAvailableNum();
		afterHospitalMaxNum = afterHospitalCounter.stream().filter(c -> c.getExamItem() == itemId.intValue())
				.collect(Collectors.toList()).get(0).getMaxNum();
		System.out.println("限制项Id:" + itemId + " 回收后体检中心可约量：" + afterAvailNum + " 容量：" + afterHospitalMaxNum);
		// Assert
		if (afterCompanyCounter != null && !afterCompanyCounter.isEmpty()) {
			afterUsedNum = afterCompanyCounter.stream().filter(c -> c.getExamItem() == itemId.intValue())
					.collect(Collectors.toList()).get(0).getUsedNum();
			afterReservationNum = afterCompanyCounter.stream().filter(c -> c.getExamItem() == itemId.intValue())
					.collect(Collectors.toList()).get(0).getReservationNum();
			afterCompanyMaxNum = afterCompanyCounter.stream().filter(c -> c.getExamItem() == itemId.intValue())
					.collect(Collectors.toList()).get(0).getMaxNum();
			System.out.println("限制项Id:" + itemId + " 回收后单位已约：" + afterUsedNum + " 预留数：" + afterReservationNum + ""
					+ "  限额：" + afterCompanyMaxNum + " 预留已使用量：" + afterReservationUsedNum);
			if (beforeIsRelease == 0) {// 如果下单前单位无预留余额（预留-已约）
				Assert.assertEquals(afterUsedNum.intValue(), beforeUsedNum - num);
				Assert.assertEquals(afterReservationNum, beforeReservationNum);
			} else {// 回收人数前，单位人数已被释放，人数回收至体检中心，单位预留变为减
				Assert.assertEquals(afterAvailNum.intValue(), beforeAvailNum + num);
				Assert.assertEquals(afterUsedNum.intValue(), beforeUsedNum - num);
				Assert.assertEquals(afterReservationNum.intValue(), beforeReservationNum - num);
			}
		} else {
			Assert.assertEquals(afterAvailNum.intValue(), beforeAvailNum + num);
		}
	}

	/**
	 * 验证人数回收是否正确
	 * 
	 * @param itemId
	 * @param oldDateCountReord
	 * @param newDateCountRecord
	 * @param num
	 */
	public static void counterCheck(Integer itemId, CountForCheck beforeDateCountReord,
			CountForCheck afterDateCountRecord, int num, Boolean isRecycle) {
		List<CompanyCapacityUsed> beforeCompanyCounter = beforeDateCountReord.getCompanyCount();
		List<HospitalCapacityUsed> beforeHospitalCounter = beforeDateCountReord.getHospitalCount();
		List<CompanyCapacityUsed> afterCompanyCounter = afterDateCountRecord.getCompanyCount();
		List<HospitalCapacityUsed> afterHospitalCounter = afterDateCountRecord.getHospitalCount();
		if (isRecycle)
			recycleCounterCheck(itemId, beforeCompanyCounter, afterCompanyCounter, beforeHospitalCounter,
					afterHospitalCounter, num);
		else
			reduceCounter(itemId, beforeCompanyCounter, afterCompanyCounter, beforeHospitalCounter,
					afterHospitalCounter, num);
	}

	/**
	 * 获取限制项详情
	 * 
	 * @param hospitalId
	 * @return
	 */
	public static List<ExamItem> getLimitItem(Integer hospitalId) {
		List<ExamItem> items = new ArrayList<>();
		String itemsSql = "select distinct a.id as id, a.name as name " + "from tb_examitem a  "
				+ "inner join tb_examitem_relation b on a.id = b.item_id " + "and a.hospital_id = ? and b.type = 4";
		List<Map<String, Object>> list = null;
		try {
			list = DBMapper.query(itemsSql, hospitalId);
		} catch (SqlException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if (list.size() > 0)
			list.forEach(m -> {
				ExamItem e = new ExamItem();
				e.setId(Integer.parseInt(m.get("id").toString()));
				e.setName(m.get("name").toString());
				items.add(e);
			});
		return items;
	}

	public static List<Integer> getLimitItemIds(Integer hospitalId) {
		List<Integer> itemIds = new ArrayList<>();
		String sql = "SELECT * FROM tb_examitem where hospital_id = ? AND type =4;";
		List<Map<String, Object>> list = null;
		try {
			list = DBMapper.query(sql, hospitalId);
		} catch (SqlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (list.size() > 0)
			list.forEach(m -> {
				itemIds.add(Integer.valueOf(m.get("id").toString()));
			});
		return itemIds;
	}

	/**
	 * 设置时段人数配置
	 * 
	 * @param hospitalId
	 * @return
	 */
	public static Map<String, Map<String, Integer>> generateHosptialPeriodSetting(Integer hospitalId,
			Integer totalcount, Integer limitItemCount, List<Integer> periods, List<Integer> limitItems) {
		Map<String, Map<String, Integer>> hosptialPeriodSetting = new HashMap<>();
		Map<String, Integer> itemSettings = new HashMap<>();
		Map<String, Integer> itemSettings1 = new HashMap<>();
		// 设置限制项
		for (Integer item : limitItems) {
			for (Integer period : periods) {
				itemSettings.put(String.valueOf(period), limitItemCount);
			}
			hosptialPeriodSetting.put(String.valueOf(item), itemSettings);
		}
		// 设置总人数
		for (Integer period : periods) {
			itemSettings1.put(String.valueOf(period), totalcount);
		}
		hosptialPeriodSetting.put("-1", itemSettings1);
		return hosptialPeriodSetting;
	}

	public static List<CompanyCapacityCell> getUsedCapacitySumByDayRange(
			List<Map<Integer, CompanyCapacityCell>> companyCapacityLst, Date startDate, Date endDate,
			Integer hospitalId) {
		List<CompanyCapacityCell> capacityCellLst = initCellLst(startDate, endDate);
		for (int i = 0; i < companyCapacityLst.size(); i++) {
			Map<Integer, CompanyCapacityCell> companyCapacity = companyCapacityLst.get(i);

			Integer sumReserveNum = 0;
			Integer sumUsedNum = 0;
			Integer sumLimit = 0;
			if (companyCapacity != null) {
				CompanyCapacityCell capacityCell = capacityCellLst.get(i);
				List<Integer> dayRangeIds = getDayRangeIds(hospitalId);
				for (Integer d : dayRangeIds) {
					CompanyCapacityCell capacityCell1 = companyCapacity.get(d);
					if (capacityCell1 != null && capacityCell1.getReserveNum() != null)
						sumReserveNum = sumReserveNum + capacityCell1.getReserveNum();
					if (capacityCell1 != null && capacityCell1.getUsedNum() != null)
						sumUsedNum = sumUsedNum + capacityCell1.getUsedNum();
					if (capacityCell1 != null && capacityCell1.getLimit() != null)
						if (capacityCell1.getLimit() != -1)
							sumLimit = sumLimit + capacityCell1.getLimit();
						else
							sumLimit = -1;
				}
				capacityCell.setLimit(sumLimit);
				capacityCell.setReserveNum(sumReserveNum);
				capacityCell.setUsedNum(sumUsedNum);
				// capacityCellLst.
			}
		}
		return capacityCellLst;
	}

	private static List<CompanyCapacityCell> initCellLst(Date startDate, Date endDate) {
		Calendar current = Calendar.getInstance();
		current.setTime(startDate);
		Calendar end = Calendar.getInstance();
		end.setTime(endDate);
		List<CompanyCapacityCell> capacityCellLst = new ArrayList<CompanyCapacityCell>();
		for (; !current.after(end); current.add(Calendar.DAY_OF_MONTH, 1)) {
			CompanyCapacityCell cCapacityCell = new CompanyCapacityCell();
			cCapacityCell.setReserveNum(0);
			cCapacityCell.setUsedNum(0);
			capacityCellLst.add(cCapacityCell);
		}
		return capacityCellLst;
	}

	public static List<HospitalCapacityUsed> getCounterHospital(Integer hospitalId, String date,
			Integer examTimeIntervalId) {
		List<HospitalCapacityUsed> hospitalCount = getHospitalCount(hospitalId, date);
		hospitalCount = hospitalCount.stream().filter(hcu -> hcu.getPeriodId() == examTimeIntervalId)
				.collect(Collectors.toList());
		return hospitalCount;
	}

	public static CountForCheck checkCount(Integer hospitalId, Integer companyId, String date,
			Integer examTimeIntervalId) {
		CountForCheck countForCheck = new CountForCheck();
		List<HospitalCapacityUsed> hospitalCount = getHospitalCount(hospitalId, date).stream()
				.filter(hcu -> hcu.getPeriodId().intValue() == examTimeIntervalId).collect(Collectors.toList());
		countForCheck.setHospitalCount(hospitalCount);
		List<CompanyCapacityUsed> companyCount = null;
		if (companyId != null) {
			companyCount = getCompanyCount(hospitalId, companyId, date).stream()
					.filter(ccu -> ccu.getPeriodId().intValue() == examTimeIntervalId).collect(Collectors.toList());
			countForCheck.setCompanyCount(companyCount);
		}
		return countForCheck;
	}

	public static Map<String, Map<String, Integer>> initHospitalPeriodSetting(Integer hospitalId, Date startDate,
			Date endDate) {
		Map<String, Map<String, Integer>> hosptialPeriodSetting = new HashMap<>();
		Map<String, Integer> peroidSetting = new HashMap<>();
		// 获取限制项
		List<Integer> limitItems = getLimitItem(hospitalId).stream().map(l -> l.getId()).collect(Collectors.toList());
		for (Integer item : limitItems) {
			hosptialPeriodSetting.put(item.toString(), peroidSetting);
		}
		return hosptialPeriodSetting;
	}

	/**
	 * 是否为休息日
	 *
	 * 
	 * @param hospitalId
	 * @param date
	 * @param dayRangeIds
	 * @return
	 */
	public static Boolean isRestDay(Integer hospitalId, Date date, List<Integer> dayRangeIds) {
		Boolean isZero = true;// 是休息日
		List<Map<Integer, HospitalCapacityCell>> hospitalCapacity = CounterChecker
				.getPeriodCapacityByHospital(hospitalId, date, date);
		System.out.println("hospitalCapacity:" + JSON.toJSONString(hospitalCapacity));
		if (hospitalCapacity == null || hospitalCapacity.isEmpty()) {
			isZero = true;
		} else {
			for (Map<Integer, HospitalCapacityCell> m : hospitalCapacity) {
				for (Integer d : dayRangeIds) {
					HospitalCapacityCell hcc = m.get(d);
					//如果其中一个时间段是休，继续轮询
					if (hcc.getCapacity() == 0) {
						isZero = true;
					} else {//如果其中一个时间段不是休息日，直接返回（本日期不是休息日）
						isZero = false;
						return isZero;
					}
				}
			}
		}
		return isZero;
	}

	/**
	 * 获取一段时间段内第一个可以预约的时间
	 * 
	 * @param start
	 * @param end
	 * @param companyId
	 * @param hospitalId
	 * @return
	 */
	public static Map<String, Object> getDateBookableFromStartDate(Date start, Date end, Integer companyId,
			Integer hospitalId) {
		Map<String, Object> dateMap = new HashMap<String, Object>();
		List<Integer> limitItem = Arrays.asList(-1);
		// 先查询单位预留，有单位预留，取单位预留
		List<CompanyCapacityUsed> companyCounts = getCompCount(hospitalId, companyId, start, end, null, limitItem);
		if (!companyCounts.isEmpty()) {
			for (CompanyCapacityUsed count : companyCounts) {
				if (count.getReservationNum() - count.getUsedNum() > 0) {
					dateMap.put("examDate", count.getCurrentDate());
					dateMap.put("dayRangeId", count.getPeriodId());
					return dateMap;
				}
			}
		}
		// 单位是否非预留日可约， 如果是，则取医院余量
		CompanyCapacityInfo compCapaInfo = getCapacitySetting(companyId, hospitalId);
		if (compCapaInfo != null && compCapaInfo.isCanOrder()) {
			List<HospitalCapacityUsed> hospitalCounts = getHospitalCount(hospitalId, start, end, null, limitItem);
			for (HospitalCapacityUsed count : hospitalCounts) {
				if (count.getAvailableNum() > 0) {
					dateMap.put("examDate", count.getCurrentDate());
					dateMap.put("dayRangeId", count.getPeriodId());
					return dateMap;
				}
			}
		}
		return null;
	}

	/**
	 * 获取医院已约量
	 * 
	 * @param hospitalCountUseds
	 * @param daysOfWeek
	 * @param isLimitItem
	 * @param isCutomized
	 * @return
	 */
	public static List<Integer> getUsedCount(List<HospitalCapacityUsed> hospitalCountUseds, List<Integer> daysOfWeek,
			Boolean isLimitItem, Boolean isCutomized) {
		// TODO Auto-generated method stub
		List<Integer> usedCount = null;
		if (!isLimitItem) {
			usedCount = hospitalCountUseds.stream()
					.filter(h -> h.getExamItem() == -1 && h.getConfigType() == (isCutomized ? 1 : 0)
							&& daysOfWeek.contains(DateUtils.dayForWeek(h.getCurrentDate()) + 1)
							&& !h.getCurrentDate().before(new Date()))// 全局设置容量只会比较与设置今天及今天之后的的数据
					.map(h -> h.getMaxNum() - h.getAvailableNum()).collect(Collectors.toList());
		} else {
			usedCount = hospitalCountUseds.stream()
					.filter(h -> h.getExamItem() != -1 && h.getConfigType() == (isCutomized ? 1 : 0)
							&& daysOfWeek.contains(DateUtils.dayForWeek(h.getCurrentDate()) + 1)
							&& !h.getCurrentDate().before(new Date()))// 全局设置容量只会比较与设置今天及今天之后的的数据
					.map(h -> h.getMaxNum() - h.getAvailableNum()).collect(Collectors.toList());
		}
		return usedCount;
	}

	/**
	 * 获取医院已约量
	 * 
	 * @param hospitalCountUseds
	 * @param daysOfWeek
	 * @param isLimitItem
	 * @return
	 */
	public static List<Integer> getUsedCount(List<HospitalCapacityUsed> hospitalCountUseds, List<Integer> daysOfWeek,
			Boolean isLimitItem) {
		// TODO Auto-generated method stub
		List<Integer> usedCount = null;
		if (!isLimitItem) {
			usedCount = hospitalCountUseds.stream()
					.filter(h -> h.getExamItem().intValue() == -1
							&& daysOfWeek.contains(DateUtils.dayForWeek(h.getCurrentDate()) + 1)
							&& !h.getCurrentDate().before(new Date()))// 全局设置容量只会比较与设置今天及今天之后的的数据
					.map(h -> h.getMaxNum() - h.getAvailableNum()).collect(Collectors.toList());
		} else {
			usedCount = hospitalCountUseds.stream()
					.filter(h -> h.getExamItem().intValue() != -1
							&& daysOfWeek.contains(DateUtils.dayForWeek(h.getCurrentDate()) + 1)
							&& !h.getCurrentDate().before(new Date()))// 全局设置容量只会比较与设置今天及今天之后的的数据
					.map(h -> h.getMaxNum() - h.getAvailableNum()).collect(Collectors.toList());
		}
		return usedCount;
	}
}
