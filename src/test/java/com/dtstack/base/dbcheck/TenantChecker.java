package com.dtstack.base.dbcheck;

import com.dtstack.base.BaseTest;
import com.dtstack.model.domain.ide.Tenant;
import com.dtstack.util.db.DBMapper;
import com.dtstack.util.db.SqlException;

import java.util.List;
import java.util.Map;

public class TenantChecker extends BaseTest {
    public static Tenant getTenantByTenantId(int tenantId) throws SqlException {
        Tenant tenant = new Tenant();
        String sql = "select * from rdos_tenant where id=?";
        List<Map<String,Object>> list = DBMapper.query(sql,tenantId);

        for (Map<String, Object> map: list
             ) {
            tenant.setId(Long.valueOf(map.get("id").toString()));
            tenant.setDtuicTenantId(Long.valueOf(map.get("dtuic_tenant_id").toString()));
            tenant.setTenantName(map.get("tenant_name").toString());
            if (map.get("tenant_desc")!=null) {
                tenant.setTenantDesc(map.get("tenant_desc").toString());
            }
            tenant.setStatus(Integer.valueOf(map.get("status").toString()));
            tenant.setIsDeleted(Integer.valueOf(map.get("is_deleted").toString()));
            if (map.get("create_user_id")!=null) {
                tenant.setCreateUserId(Long.valueOf(map.get("create_user_id").toString()));
            }
        }
        return tenant;
    }

}
