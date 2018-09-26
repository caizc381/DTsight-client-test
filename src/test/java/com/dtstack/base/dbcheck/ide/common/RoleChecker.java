package com.dtstack.base.dbcheck.ide.common;

import com.dtstack.base.BaseTest;
import com.dtstack.model.domain.ide.Role;
import com.dtstack.util.db.DBMapper;
import com.dtstack.util.db.SqlException;

import java.util.List;
import java.util.Map;

public class RoleChecker extends BaseTest {
    public static Role getOne(Long roleId) throws SqlException {
        String sql = "select id,tenant_id,project_id,role_name,role_type,role_value,role_desc,modify_user_id,gmt_create,gmt_modified,is_deleted"
                + " from rdos_role where id=? and is_deleted=0";
        List<Map<String, Object>> list = DBMapper.query(sql, roleId);
        Role role = map2Role(list.get(0));
        return role;
    }

    public static Role map2Role(Map<String, Object> map) {
        Role role = new Role();
        role.setId(Long.valueOf(map.get("id").toString()));
        role.setTenantId(Long.valueOf(map.get("tenant_id").toString()));
        role.setProjectId(Long.valueOf(map.get("project_id").toString()));
        role.setRoleName(map.get("role_name").toString());
        role.setRoleType(Integer.valueOf(map.get("role_type").toString()));
        role.setRoleValue(Integer.valueOf(map.get("role_value").toString()));
        role.setRoleDesc(map.get("role_desc").toString());
        role.setModifyUserId(Long.valueOf(map.get("modify_user_id").toString()));
        role.setIsDeleted(Integer.valueOf(map.get("is_deleted").toString()));
        return role;
    }
}
