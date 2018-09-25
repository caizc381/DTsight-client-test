package com.dtstack.base.dbcheck.uic.account;

import com.dtstack.base.BaseTest;
import com.dtstack.model.domain.ide.User;
import com.dtstack.model.po.uic.account.UserPO;
import com.dtstack.util.db.DBMapper;
import com.dtstack.util.db.SqlException;

import java.util.List;
import java.util.Map;


public class UserChecker extends BaseTest {
    public static User getUserByUicUserId(Long uicUserId) throws SqlException {

        User user = new User();
        String sql = "select * from rdos_user where dtuic_user_id=?";
        List<Map<String, Object>> list = DBMapper.query(sql, uicUserId);
        Map<String, Object> map = list.get(0);
        user.setDtuicUserId(Long.valueOf(map.get("dtuic_user_id").toString()));
        user.setId(Long.valueOf(map.get("id").toString()));
        user.setUserName(map.get("user_name").toString());
        if (map.get("default_project_id") != null) {
            user.setDefaultProjectId(Long.valueOf(map.get("default_project_id").toString()));
        }
        user.setPhoneNumber(map.get("phone_number").toString());
        user.setStatus(Integer.valueOf(map.get("status").toString()));
        user.setEmail(map.get("email").toString());
        return user;
    }

    public static UserPO findByIdAndIsDeletedIsFalse(Long userId) throws SqlException {
        String sql = "select * from uic_user where id=? and is_deleted=?";
        List<Map<String, Object>> list = DBMapper.queryUic(sql, userId, false);
        UserPO userPO = new UserPO();

        for (int i = 0; i < list.size(); i++) {
            Map<String, Object> map = list.get(i);
            userPO.setId(Long.valueOf(map.get("id").toString()));
            userPO.setIsDeleted(map.get("is_deleted").equals("N") ? 0 : 1);
            userPO.setEmail(map.get("email").toString());
            userPO.setActive(Boolean.parseBoolean(map.get("is_active").toString()));
            userPO.setAdmin(Boolean.parseBoolean(map.get("is_admin").toString()));
            userPO.setCompany(map.get("company").toString());
            if (map.get("external_id") != null) {
                userPO.setExternalId(map.get("external_id").toString());
            }

            userPO.setFullName(map.get("full_name").toString());
            userPO.setOwnTenantId(Long.valueOf(map.get("own_tenant_id").toString()));
            if (map.get("last_login_tenant_id") != null) {
                userPO.setLastLoginTenantId(Long.valueOf(map.get("last_login_tenant_id").toString()));
            }

            userPO.setPassword(map.get("password").toString());
            userPO.setPhone(map.get("phone").toString());
            //userPO.setProducts();
            userPO.setRoot(Boolean.parseBoolean(map.get("is_root").toString()));
            userPO.setSource(Integer.valueOf(map.get("source").toString()));
            //userPO.setTenants();
            userPO.setUserName(map.get("username").toString());
            //userPO.setUserRenantRels();
        }
        return userPO;
    }
}
