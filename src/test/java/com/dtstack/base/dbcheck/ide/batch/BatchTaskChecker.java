package com.dtstack.base.dbcheck.ide.batch;

import com.dtstack.base.BaseTest;
import com.dtstack.util.db.DBMapper;
import com.dtstack.util.db.SqlException;

import java.util.List;
import java.util.Map;

public class BatchTaskChecker extends BaseTest {

    //统计发布个数
    public static Integer countByProjectIdAndSubmit(Integer isSubmit, Long projectId, Long tenantId) throws SqlException {
        String sql = "select count(1) from rdos_batch_task where project_id=? and tenant_id=? and is_deleted=0";
        if (isSubmit != null) {
            sql += " and submit_status=" + isSubmit;
        }
        List<Map<String, Object>> list = DBMapper.query(sql, projectId, tenantId);
        return Integer.valueOf(list.get(0).get("COUNT(1)").toString());
    }
}
