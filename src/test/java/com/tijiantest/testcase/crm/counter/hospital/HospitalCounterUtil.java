package com.tijiantest.testcase.crm.counter.hospital;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tijiantest.model.counter.HospitalCapacityCell;
import com.tijiantest.util.db.DBMapper;
import com.tijiantest.util.db.SqlException;

public class HospitalCounterUtil {
	
	@SuppressWarnings("unused")
	public static List<Map<String, Object>> getItemUsedCapacity(Integer hospitalId, Date startDate,Date endDate) throws SqlException{
		Map<Date, Map<Integer, Map<Integer, HospitalCapacityCell>>> usedCellMaps = new HashMap<>();
		
		String usedSql = " select  id, hospital_id, period_id, cur_date, exam_item, available_num, max_num, config_type from tb_hospital_capacity_used where hospital_id = ? and cur_date between ? and ?";
		
		List<Map<String, Object>> usedList = DBMapper.query(usedSql, hospitalId,startDate,endDate);
		
		
		
		
		return usedList;
	}

}
