package com.tijiantest.testcase.crm.order;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tijiantest.model.order.OrderStatus;
import com.tijiantest.testcase.crm.CrmBase;
import com.tijiantest.util.DateUtils;

public class OrderBase extends CrmBase{

	@SuppressWarnings("serial")
	public static Map<String, Object> generateQueryOrderObj(String gte, String lte, int hospitalId,
			List<Integer> status, Boolean isExport, String where, String accountRelationName) throws ParseException {

		SimpleDateFormat zsdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		Map<String, Object> map = new HashMap<>();
		Map<String, Object> eqMap = new HashMap<>();
		eqMap.put("$eq", hospitalId);
		map.put("orderHospital._id", eqMap);

		Map<String, Object> inMap = new HashMap<>();
		inMap.put("$in", new ArrayList<Integer>() {
			{
				add(OrderStatus.ALREADY_BOOKED.intValue());
				add(OrderStatus.EXAM_FINISHED.intValue());
				add(OrderStatus.NOT_EXAM.intValue());
				add(OrderStatus.PART_BACK.intValue());
			}
		});
		map.put("status", inMap);

		Map<String, Object> isExportMap = new HashMap<>();
		isExportMap.put("$eq", isExport);
		map.put("isExport", isExportMap);
		if (where != null && !where.equals("")) {
			map.put("$where", where);
		}

		if (accountRelationName != null && !accountRelationName.equals("")) {
			Map<String, Object> regxMap = new HashMap<>();
			regxMap.put("$regex", accountRelationName);
			map.put("examiner.name", regxMap);
		}

		Map<String, Object> gteMap = new HashMap<>();
		if (gte != null && !gte.equals("")) {
			String gteDate = sdf.format(DateUtils.offDate(Integer.valueOf(gte)-1))+"T16:00:00Z";
			Map<String, Object> dateMap = new HashMap<>();
			// 往后推两天
			dateMap.put("$date", gteDate);
			gteMap.put("$gte", dateMap);
		}
		if (lte != null && !lte.equals("")) {
			String lteDate = sdf.format(DateUtils.offDate(Integer.valueOf(lte)-1))+"T16:00:00Z";
			Map<String, Object> dateMap1 = new HashMap<>();
			// 当前时间往前加两天
			dateMap1.put("$date", lteDate);
			gteMap.put("$lte", dateMap1);
		}

		if (gteMap.size() > 0) {
			map.put("examDate", gteMap);
		}

		return map;
	}
}
