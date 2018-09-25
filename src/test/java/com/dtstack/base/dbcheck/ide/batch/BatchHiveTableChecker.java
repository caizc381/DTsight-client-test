package com.dtstack.base.dbcheck.ide.batch;

import com.dtstack.base.BaseTest;
import com.dtstack.model.domain.ide.BatchHiveTableInfo;
import com.dtstack.util.StringUtil;
import com.dtstack.util.db.DBMapper;
import com.dtstack.util.db.SqlException;

import java.sql.Timestamp;
import java.util.*;

public class BatchHiveTableChecker extends BaseTest {

    //表数量
    public static Integer countByProjectIds(Collection<Long> ids, Long tenantId) throws SqlException {
        String projectIdStr = "";
        ArrayList idList = new ArrayList(ids);
        Enumeration<Long> e = Collections.enumeration(idList);
        while (e.hasMoreElements()) {
            projectIdStr += e.nextElement() + ",";
        }
        if (!projectIdStr.equals("")) {
            projectIdStr = StringUtil.removeCommaAtEnd(projectIdStr);
        }
        String sql = "select count(1) from rdos_hive_table_info where tenant_id=? and is_deleted=0 and project_id in (" + projectIdStr + ")";
        List<Map<String, Object>> list = DBMapper.query(sql, tenantId);
        return Integer.valueOf(list.get(0).get("COUNT(1)").toString());
    }

    //项目的存储空间
    public static List<BatchHiveTableInfo> listByProjectIds(List<Long> ids, Long tenantId, Integer limit) throws SqlException {
        String projectIdStr = "";
        for (int i = 0; i < ids.size(); i++) {
            projectIdStr += ids.get(i) + ",";
        }
        if (!projectIdStr.equals("")) {
            projectIdStr = StringUtil.removeCommaAtEnd(projectIdStr);
        }

        String sql = "select id,tenant_id,project_id,table_name,life_day,life_status,table_size,size_update_time,"
                + " user_id, charge_user_id,path,location,delim,store_type,catalogue_id,gmt_create,gmt_modified,is_deleted,"
                + " is_dirty_data_table,last_ddl_time,last_dml_time,table_desc,grade,subject,refresh_rate,incre_type,is_ignore,"
                + " check_result,modify_user_id "
                + " from rdos_hive_table_info where is_deleted=0 and tenant_id=? and project_id in (" + projectIdStr + ")";
        List<Map<String, Object>> list = DBMapper.query(sql, tenantId);
        List<BatchHiveTableInfo> batchHiveTableInfoList = new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {
            BatchHiveTableInfo batchHiveTableInfo = map2BatchHiveTableInfo(list.get(i));
            batchHiveTableInfoList.add(batchHiveTableInfo);
        }
        return batchHiveTableInfoList;
    }

    public static BatchHiveTableInfo map2BatchHiveTableInfo(Map<String, Object> map) {
        BatchHiveTableInfo batchHiveTableInfo = new BatchHiveTableInfo();
        batchHiveTableInfo.setId(Long.valueOf(map.get("id").toString()));
        batchHiveTableInfo.setTenantId(Long.valueOf(map.get("tenant_id").toString()));
        batchHiveTableInfo.setProjectId(Long.valueOf(map.get("project_id").toString()));
        batchHiveTableInfo.setTableName(map.get("table_name").toString());
        batchHiveTableInfo.setLifeDay(Integer.valueOf(map.get("life_day").toString()));
        batchHiveTableInfo.setLifeStatus(Integer.valueOf(map.get("life_status").toString()));
        batchHiveTableInfo.setTableSize(Long.valueOf(map.get("table_size").toString()));
        batchHiveTableInfo.setSizeUpdateTime(Timestamp.valueOf(map.get("size_update_time").toString()));
        batchHiveTableInfo.setUserId(Long.valueOf(map.get("user_id").toString()));
        batchHiveTableInfo.setChargeUserId(Long.valueOf(map.get("charge_user_id").toString()));
        batchHiveTableInfo.setPath(map.get("path").toString());
        batchHiveTableInfo.setLocation(map.get("location").toString());
        batchHiveTableInfo.setDelim(map.get("delim").toString());
        batchHiveTableInfo.setStoreType(map.get("store_type").toString());
        batchHiveTableInfo.setCatalogueId(Long.valueOf(map.get("catalogue_id").toString()));
        batchHiveTableInfo.setIsDeleted(Integer.valueOf(map.get("is_deleted").toString()));
        batchHiveTableInfo.setIsDirtyDataTable(Integer.valueOf(map.get("is_dirty_data_table").toString()));
        batchHiveTableInfo.setLastDdlTime(Timestamp.valueOf(map.get("last_ddl_time").toString()));
        batchHiveTableInfo.setLastDmlTime(Timestamp.valueOf(map.get("last_dml_time").toString()));
        batchHiveTableInfo.setTableDesc(map.get("table_desc").toString());
        batchHiveTableInfo.setGrade(map.get("grade").toString());
        batchHiveTableInfo.setSubject(map.get("subject").toString());
        batchHiveTableInfo.setRefreshRate(map.get("refresh_rate").toString());
        batchHiveTableInfo.setIncreType(map.get("incre_type").toString());
        batchHiveTableInfo.setIsIgnore(Integer.valueOf(map.get("is_ignore").toString()));
        batchHiveTableInfo.setCheckResult(map.get("check_result").toString());
        batchHiveTableInfo.setModifyUserId(Long.valueOf(map.get("modify_user_id").toString()));
        return batchHiveTableInfo;
    }
}
