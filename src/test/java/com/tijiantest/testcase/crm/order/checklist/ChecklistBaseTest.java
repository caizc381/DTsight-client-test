package com.tijiantest.testcase.crm.order.checklist;

import com.tijiantest.testcase.crm.CrmBase;
import com.tijiantest.util.db.DBMapper;
import com.tijiantest.util.db.SqlException;

public class ChecklistBaseTest extends CrmBase{
	
	static{
		int hospitalId = defhospital.getId();
		String sql = "update tb_hospital_settings set can_print_checklist=1 where hospital_id=?";
		try {
			DBMapper.update(sql, hospitalId);
		} catch (SqlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("更新tb_hospital_settings.can_print_checklist字段，设为1");
	}
	
	
}
