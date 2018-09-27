package com.dtstack.base.dbcheck.ide.batch;

import com.dtstack.base.BaseTest;
import com.dtstack.model.domain.ide.batch.BatchHiveCatalogue;
import com.dtstack.util.db.DBMapper;
import com.dtstack.util.db.SqlException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BatchHiveCatalogueChecker extends BaseTest {
    private static String selectContantFragment = "id,tenant_id,node_name,node_pid,order_val,path,level,gmt_create,gmt_modified,create_user_id,is_deleted";

    public static List<BatchHiveCatalogue> listByTenantId(Long tenantId) throws SqlException {
        String sql = "select " + selectContantFragment + " from rdos_hive_catalogue where tenant_id=? and is_deleted=0";

        List<Map<String, Object>> list = DBMapper.query(sql, tenantId);

        List<BatchHiveCatalogue> batchHiveCatalogues = new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {
            BatchHiveCatalogue batchHiveCatalogue = map2BatchHiveCatalogue(list.get(i));
            batchHiveCatalogues.add(batchHiveCatalogue);
        }
        return batchHiveCatalogues;
    }

    public static BatchHiveCatalogue map2BatchHiveCatalogue(Map<String, Object> map) {
        BatchHiveCatalogue batchHiveCatalogue = new BatchHiveCatalogue();
        batchHiveCatalogue.setId(Long.valueOf(map.get("id").toString()));
        batchHiveCatalogue.setTenantId(Long.valueOf(map.get("tenant_id").toString()));
        batchHiveCatalogue.setNodeName(map.get("node_name").toString());
        batchHiveCatalogue.setNodePid(Long.valueOf(map.get("node_pid").toString()));
        if (map.get("order_val") != null) {
            batchHiveCatalogue.setOrderVal(Integer.valueOf(map.get("order_val").toString()));
        }

        batchHiveCatalogue.setPath(map.get("path").toString());
        batchHiveCatalogue.setLevel(Integer.valueOf(map.get("level").toString()));
        batchHiveCatalogue.setCreateUserId(Long.valueOf(map.get("create_user_id").toString()));
        batchHiveCatalogue.setIsDeleted(Integer.valueOf(map.get("is_deleted").toString()));
        return batchHiveCatalogue;
    }
}
