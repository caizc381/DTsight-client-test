package com.dtstack.base.dbcheck.ide.batch;

import com.dtstack.base.BaseTest;
import com.dtstack.model.domain.ide.batch.BatchTaskShade;
import com.dtstack.model.dto.ide.batch.BatchTaskShadeDTO;
import com.dtstack.util.db.DBMapper;
import com.dtstack.util.db.SqlException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BatchTaskShadeChecker extends BaseTest {
    private static String selectContentFragment = "id,tenant_id,project_id,node_pid,name,task_type,engine_type,compute_type,sql_text,"
            + " task_params,schedule_conf,period_type,schedule_status,submit_status,gmt_create,gmt_modified,modify_user_id,create_user_id,"
            + " owner_user_id,version,is_deleted,task_desc,main_class,exe_args,flow_id,is_publish_to_produce";

    public static Integer simpleCount(BatchTaskShadeDTO dto) throws SqlException {
        String sql = "select count(1) from rdos_batch_task_shade where tenant_id=? and project_id=?";
        if (dto.getTaskModifyUserId() != null) {
            sql += " and modify_user_id =" + dto.getTaskModifyUserId();
        }

        if (dto.getTaskName() != null && !dto.getTaskName().equals("")) {
            sql += " and name like '%" + dto.getTaskName() + "%'";
        }

        if (dto.getStartTime() != null) {
            sql += " and to_days(gmt_modified) >=to_days(\"" + dto.getStartTime() + "\")";
        }

        if (dto.getEndTime() != null) {
            sql += " and to_days(\"" + dto.getEndTime() + "\") >=to_days(gmt_modified)";
        }
        sql += " and if(is_publish_to_produce = 0,is_deleted=0,1=1)";
        System.out.println("sql...." + sql);

        List<Map<String, Object>> list = DBMapper.query(sql, dto.getTenantId(), dto.getProjectId());

        Integer count = Integer.valueOf(list.get(0).get("COUNT(1)").toString());
        return count;
    }

    public static List<BatchTaskShade> simpleQuery(BatchTaskShadeDTO dto, String orderBy, String sort, String start, String pageSize) throws SqlException {
        String sql = "select " + selectContentFragment + " from rdos_batch_task_shade where tenant_id=" + dto.getTenantId() + " and project_id=" + dto.getProjectId();
        if (dto.getTaskModifyUserId() != null) {
            sql += " and modify_user_id=" + dto.getTaskModifyUserId();
        }

        if (dto.getTaskName() != null && !dto.getTaskName().equals("")) {
            sql += " and name like '%" + dto.getTaskName() + "%'";
        }

        if (dto.getStartTime() != null) {
            sql += " and to_days(gmt_modified) >= to_days('" + dto.getStartTime() + "')";
        }

        if (dto.getEndTime() != null) {
            sql += " and to_days('" + dto.getEndTime() + "')>=to_days(gmt_modified)";
        }

        sql += " and if(is_publish_to_produce=0,is_deleted=0,1=1)";

        if (orderBy != null && sort != null) {
            sql += " order by " + orderBy + " " + sort;
        }
        if (orderBy != null && sort == null) {
            sql += " order by " + orderBy + " desc";
        }

        if (start != null && pageSize != null) {
            sql += " limit " + start + ", " + pageSize;
        }

        if (start == null && pageSize != null) {
            sql += " limit " + pageSize;
        }

        if (start == null && pageSize == null) {
            sql += " limit 1000";
        }

        log.info("sql... " + sql);
        List<Map<String, Object>> list = DBMapper.query(sql);
        List<BatchTaskShade> batchTaskShades = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            BatchTaskShade batchTaskShade = map2BatchTaskShade(list.get(i));
            batchTaskShades.add(batchTaskShade);
        }

        return batchTaskShades;
    }

    public static BatchTaskShade map2BatchTaskShade(Map<String, Object> map) {
        BatchTaskShade batchTaskShade = new BatchTaskShade();
        batchTaskShade.setId(Long.valueOf(map.get("id").toString()));
        batchTaskShade.setTenantId(Long.valueOf(map.get("tenant_id").toString()));
        batchTaskShade.setProjectId(Long.valueOf(map.get("project_id").toString()));
        batchTaskShade.setNodePid(Long.valueOf(map.get("node_pid").toString()));
        batchTaskShade.setName(map.get("name").toString());
        batchTaskShade.setTaskType(Integer.valueOf(map.get("task_type").toString()));
        batchTaskShade.setEngineType(Integer.valueOf(map.get("engine_type").toString()));
        batchTaskShade.setComputeType(Integer.valueOf(map.get("compute_type").toString()));
        batchTaskShade.setSqlText(map.get("sql_text").toString());
        batchTaskShade.setTaskParams(map.get("task_params").toString());
        batchTaskShade.setScheduleConf(map.get("schedule_conf").toString());
        batchTaskShade.setPeriodType(Integer.valueOf(map.get("period_type").toString()));
        batchTaskShade.setScheduleStatus(Integer.valueOf(map.get("schedule_status").toString()));
        batchTaskShade.setSubmitStatus(Integer.valueOf(map.get("submit_status").toString()));
        batchTaskShade.setModifyUserId(Long.valueOf(map.get("modify_user_id").toString()));
        batchTaskShade.setCreateUserId(Long.valueOf(map.get("create_user_id").toString()));
        batchTaskShade.setOwnerUserId(Long.valueOf(map.get("owner_user_id").toString()));
        batchTaskShade.setVersion(Integer.valueOf(map.get("version").toString()));
        batchTaskShade.setIsDeleted(Integer.valueOf(map.get("is_deleted").toString()));
        batchTaskShade.setTaskDesc(map.get("task_desc").toString());
        batchTaskShade.setMainClass(map.get("main_class").toString());
        batchTaskShade.setExeArgs(map.get("exe_args").toString());
        batchTaskShade.setFlowId(Long.valueOf(map.get("flow_id").toString()));
        batchTaskShade.setIsPublishToProduce(Long.valueOf(map.get("is_publish_to_produce").toString()));
        return batchTaskShade;
    }
}
