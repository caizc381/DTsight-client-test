package com.dtstack.base.dbcheck.ide.batch;

import com.dtstack.base.BaseTest;
import com.dtstack.model.domain.ide.batch.BatchResource;
import com.dtstack.model.dto.ide.batch.BatchResourceDTO;
import com.dtstack.util.db.DBMapper;
import com.dtstack.util.db.SqlException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BatchResourceChecker extends BaseTest {

    private static String selectContentFragment = "id,tenant_id,project_id,node_pid,url,resource_type,resource_name,origin_file_name,resource_desc,gmt_create,gmt_modified,"
            + " create_user_id,modify_user_id,is_deleted,node_id";

    public static Integer generalCount(BatchResourceDTO batchResourceDTO) throws SqlException {
        String sql = "select count(1) from rdos_batch_resource where tenant_id=? and project_id=? and is_deleted=0";
        if (batchResourceDTO.getResourceModifyUserId() != null) {
            sql += " and modify_user_id=" + batchResourceDTO.getResourceModifyUserId();
        }
        if (batchResourceDTO.getResourceName() != null && !batchResourceDTO.getResourceName().equals("")) {
            sql += " and resource_name like '%" + batchResourceDTO.getResourceName() + "%'";
        }

        if (batchResourceDTO.getStartTIme() != null) {
            sql += " and to_days(gmt_modified) >= to_days('" + batchResourceDTO.getStartTIme() + "')";
        }

        if (batchResourceDTO.getEndTime() != null) {
            sql += " and to_days('" + batchResourceDTO.getEndTime() + "') >=to_days(gmt_modified)";
        }

        List<Map<String, Object>> list = DBMapper.query(sql, batchResourceDTO.getTenantId(), batchResourceDTO.getProjectId());
        return Integer.valueOf(list.get(0).get("COUNT(1)").toString());
    }

    public static List<BatchResource> generalQuery(BatchResourceDTO dto, String orderBy, String sort, String start, String pageSize) throws SqlException {
        String sql = "select " + selectContentFragment + " from rdos_batch_resource where tenant_id=? and project_id=? and is_deleted=0";

        if (dto.getResourceModifyUserId() != null) {
            sql += " and modify_user_id=" + dto.getResourceModifyUserId();
        }

        if (dto.getResourceName() != null && !dto.getResourceName().equals("")) {
            sql += " and resource_name like '%" + dto.getResourceName() + "%'";
        }

        if (dto.getStartTIme() != null) {
            sql += " and to_days(gmt_modified)>= to_days('" + dto.getStartTIme() + "')";
        }

        if (dto.getEndTime() != null) {
            sql += " and to_days('" + dto.getEndTime() + "') >= to_days(gmt_modified)";
        }

        if (orderBy != null && sort != null) {
            sql += " order by " + orderBy + " " + sort;
        }

        if (orderBy != null && sort == null) {
            sql += " order by " + orderBy + "  desc";
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

        List<Map<String, Object>> list = DBMapper.query(sql, dto.getTenantId(), dto.getProjectId());
        List<BatchResource> batchResources = new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {
            BatchResource batchResource = map2BatchResource(list.get(i));
            batchResources.add(batchResource);
        }
        return batchResources;
    }

    public static BatchResource map2BatchResource(Map<String, Object> map) {
        BatchResource batchResource = new BatchResource();
        batchResource.setId(Long.valueOf(map.get("id").toString()));
        batchResource.setTenantId(Long.valueOf(map.get("tenant_id").toString()));
        batchResource.setProjectId(Long.valueOf(map.get("project_id").toString()));

        if (map.get("node_pid")!=null){
            batchResource.setNodePid(Long.valueOf(map.get("node_pid").toString()));
        }

        batchResource.setUrl(map.get("url").toString());
        batchResource.setResourceType(Integer.valueOf(map.get("resource_type").toString()));
        batchResource.setResourceName(map.get("resource_name").toString());
        batchResource.setOriginFileName(map.get("origin_file_name").toString());
        batchResource.setResourceDesc(map.get("resource_desc").toString());
        batchResource.setCreateUserId(Long.valueOf(map.get("create_user_id").toString()));
        batchResource.setModifyUserId(Long.valueOf(map.get("modify_user_id").toString()));
        batchResource.setIsDeleted(Integer.valueOf(map.get("is_deleted").toString()));
        if (map.get("node_id")!=null){
            batchResource.setNodePid(Long.valueOf(map.get("node_id").toString()));
        }

        return batchResource;
    }
}
