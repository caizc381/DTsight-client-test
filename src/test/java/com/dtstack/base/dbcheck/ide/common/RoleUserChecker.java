package com.dtstack.base.dbcheck.ide.common;

import com.dtstack.base.BaseTest;
import com.dtstack.model.domain.ide.Role;
import com.dtstack.model.domain.ide.RoleUser;
import com.dtstack.util.db.DBMapper;
import com.dtstack.util.db.SqlException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RoleUserChecker extends BaseTest {

    public static List<RoleUser> getRoleUserIsAdmin(Long userId, Long tenantId) throws SqlException {
        String sql = "select t.id,t.tenant_id,t.project_id,t.role_id,t.user_id,t.gmt_create,t.gmt_modified,t.is_deleted "
                + " from rdos_role_user t left join rdos_role p on t.role_id = p.id"
                + " where p.role_value<=3 and t.project_id !=-1 and t.user_id=? and t.tenant_id=? and t.is_deleted=0";
        List<Map<String, Object>> list = DBMapper.query(sql, userId, tenantId);
        List<RoleUser> roleUsers = new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {
            Map<String, Object> map = list.get(i);
            RoleUser roleUser = map2RoleUser(map);
            roleUsers.add(roleUser);
        }
        return roleUsers;
    }

    public static List<RoleUser> getRoleUserByUserId(Long userId, Long tenantId) throws SqlException {
        String sql = "select id,tenant_id,project_id,role_id,user_id,gmt_create,gmt_modified,is_deleted "
                + " from rdos_role_user where project_id!=-1 and user_id=? and tenant_id=?";
        List<Map<String, Object>> list = DBMapper.query(sql, userId, tenantId);
        List<RoleUser> roleUsers = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            RoleUser roleUser = map2RoleUser(list.get(i));
            roleUsers.add(roleUser);
        }
        return roleUsers;
    }

    public static RoleUser map2RoleUser(Map<String, Object> map) throws SqlException {
        RoleUser roleUser = new RoleUser();
        roleUser.setRoleId(Long.valueOf(map.get("role_id").toString()));
        roleUser.setUserId(Long.valueOf(map.get("user_id").toString()));
        roleUser.setId(Long.valueOf(map.get("id").toString()));
        roleUser.setIsDeleted(Integer.valueOf(map.get("is_deleted").toString()));
        roleUser.setTenantId(Long.valueOf(map.get("tenant_id").toString()));
        roleUser.setProjectId(Long.valueOf(map.get("project_id").toString()));
        Role role = RoleChecker.getOne(Long.valueOf(map.get("role_id").toString()));
        roleUser.setRole(role);

        return roleUser;
    }
}
