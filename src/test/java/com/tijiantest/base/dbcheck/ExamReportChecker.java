package com.tijiantest.base.dbcheck;

import java.util.List;
import java.util.Map;

import com.tijiantest.base.BaseTest;
import com.tijiantest.util.db.DBMapper;
import com.tijiantest.util.db.SqlException;

public class ExamReportChecker extends BaseTest {

	/**
	 * 根据医院Id查询医院的套餐规则id
	 * @param hospitalId
	 * @return
	 */
	public static int getHospitalRuleId(int hospitalId){
		List<Map<String,Object>> dblist = null;
		try {
			dblist = DBMapper.queryExamReport("select * from tb_drools_rule where hospital_id = "+hospitalId);
			Map<String,Object> map = dblist.get(0);
			return Integer.parseInt(map.get("id").toString());
		} catch (SqlException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		return 0;
	}
}
