package com.dtstack.base.dbcheck.ide.batch;

import com.dtstack.base.BaseTest;
import com.dtstack.model.domain.ide.batch.BatchFunction;
import com.dtstack.model.dto.ide.batch.BatchFunctionDTO;
import com.dtstack.util.db.DBMapper;
import com.dtstack.util.db.SqlException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BatchFunctionChecker extends BaseTest {

    public static String selectContentFragment = "id,name,class_name,purpose,command_formate,param_desc,node_pid,tenant_id,project_id,"
            + " create_user_id,modify_user_id,type,gmt_create,gmt_modified,is_deleted";

    public static Integer generalCount(BatchFunctionDTO batchFunctionDTO) throws SqlException {
        String sql = "select count(1) from rdos_batch_function where tenant_id=? and project_id=? and is_deleted=0 and type=0";
        if (batchFunctionDTO.getFunctionModifyUserId() != null) {
            sql += " and modify_user_id=" + batchFunctionDTO.getFunctionModifyUserId();
        }

        if (batchFunctionDTO.getFunctionName() != null && !batchFunctionDTO.getFunctionName().equals("")) {
            sql += " and name like '%" + batchFunctionDTO.getFunctionName() + "%'";
        }

        if (batchFunctionDTO.getStartTIme() != null) {
            sql += " and to_days(gmt_modified) >= to_days('" + batchFunctionDTO.getStartTIme() + "')";
        }

        if (batchFunctionDTO.getEndTime() != null) {
            sql += " and to_days('" + batchFunctionDTO.getEndTime() + "') >= to_days(gmt_modified)";
        }

        List<Map<String, Object>> list = DBMapper.query(sql, defRdosTenantId, defProjectId);

        return Integer.valueOf(list.get(0).get("count(1)").toString());
    }

    public static List<BatchFunction> generalQuery(BatchFunctionDTO batchFunctionDTO, String orderBy, String sort, String start, String pageSize) throws SqlException {
        String sql = "select " + selectContentFragment + " from rdos_batch_function "
                + " where tenant_id=? and project_id=? and type=0 and is_deleted=0";
        if (batchFunctionDTO.getFunctionModifyUserId() != null) {
            sql += " and modify_user_id=" + batchFunctionDTO.getFunctionModifyUserId();
        }
        if (batchFunctionDTO.getFunctionName() != null && !batchFunctionDTO.getFunctionName().equals("")) {
            sql += " and name like '%" + batchFunctionDTO.getFunctionName() + "%'";
        }
        if (batchFunctionDTO.getStartTIme() != null) {
            sql += " and to_days(gmt_modified) >= to_days('" + batchFunctionDTO.getStartTIme() + "')";
        }

        if (batchFunctionDTO.getEndTime() != null) {
            sql += " and to_days('" + batchFunctionDTO.getEndTime() + "')>=to_days(gmt_modified)";
        }

        if (orderBy != null && sort != null) {
            sql += " order by " + orderBy + " " + sort;
        }

        if (orderBy != null && sort == null) {
            sql += " order by " + orderBy;
        }

        if (start == null && pageSize != null) {
            sql += " limit " + pageSize;
        }

        if (start == null && pageSize == null) {
            sql += " limit 1000";
        }

        log.info("sql .... " + sql);
        List<Map<String, Object>> list = DBMapper.query(sql, batchFunctionDTO.getTenantId(), batchFunctionDTO.getProjectId());
        List<BatchFunction> batchFunctions = new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {
            BatchFunction batchFunction = map2BatchFunction(list.get(i));
            batchFunctions.add(batchFunction);
        }
        return batchFunctions;
    }

    public static BatchFunction map2BatchFunction(Map<String, Object> map) {
        BatchFunction batchFunction = new BatchFunction();
        batchFunction.setId(Long.valueOf(map.get("id").toString()));
        batchFunction.setName(map.get("name").toString());
        batchFunction.setClassName(map.get("class_name").toString());
        if (map.get("purpose") != null) {
            batchFunction.setPurpose(map.get("purpose").toString());
        }

        if (map.get("command_formate") != null) {
            batchFunction.setCommandFormate(map.get("command_formate").toString());
        }

        if (map.get("param_desc") != null) {
            batchFunction.setParamDesc(map.get("param_desc").toString());
        }

        batchFunction.setNodePid(Long.valueOf(map.get("node_pid").toString()));
        batchFunction.setTenantId(Long.valueOf(map.get("tenant_id").toString()));
        batchFunction.setProjectId(Long.valueOf(map.get("project_id").toString()));
        batchFunction.setCreateUserId(Long.valueOf(map.get("create_user_id").toString()));
        batchFunction.setModifyUserId(Long.valueOf(map.get("modify_user_id").toString()));
        batchFunction.setType(Integer.valueOf(map.get("type").toString()));
        batchFunction.setIsDeleted(Integer.valueOf(map.get("is_deleted").toString()));
        return batchFunction;
    }
}
