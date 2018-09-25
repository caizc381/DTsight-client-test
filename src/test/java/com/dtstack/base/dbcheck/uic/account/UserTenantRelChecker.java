package com.dtstack.base.dbcheck.uic.account;

import com.dtstack.base.BaseTest;
import com.dtstack.model.po.uic.account.UserTenantRelPO;
import com.dtstack.util.StringUtil;
import com.dtstack.util.db.DBMapper;
import com.dtstack.util.db.SqlException;

import java.util.*;

public class UserTenantRelChecker extends BaseTest {

    public static List<UserTenantRelPO> findByUserIdAndIsDeletedIsFalse(Long userId) throws SqlException {
        String sql = "select * from uic_user_tenant_rel where user_id=? and is_deleted=?";
        List<Map<String, Object>> list = DBMapper.queryUic(sql, userId, "N");
        List<UserTenantRelPO> userTenantRelPOS = new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {
            Map<String, Object> map = list.get(i);
            UserTenantRelPO userTenantRelPO = new UserTenantRelPO();
            userTenantRelPO.setId(Long.valueOf(map.get("id").toString()));
            userTenantRelPO.setDeleted(Boolean.parseBoolean(map.get("is_deleted").toString()));
            userTenantRelPO.setAdmin(Boolean.parseBoolean(map.get("is_admin").toString()));
            userTenantRelPO.setTenantId(Long.valueOf(map.get("tenant_id").toString()));
            userTenantRelPO.setCreator(Long.valueOf(map.get("creator").toString()));
            userTenantRelPOS.add(userTenantRelPO);
        }
        return userTenantRelPOS;
    }

    public static List<UserTenantRelPO> findByTenantIdInAndIsDeletedIsFalse(Collection<Long> tenantIds) throws SqlException {
        String tenantsIdStr = "";
        Iterator<Long> iter = tenantIds.iterator();
        List<UserTenantRelPO> userTenantRelPOS = new ArrayList<>();
        while (iter.hasNext()) {
            tenantsIdStr += iter.next() + ",";
        }
        if (!tenantsIdStr.equals("")) {
            tenantsIdStr = StringUtil.removeCommaAtEnd(tenantsIdStr);
            String sql = "select * from uic_user_tenant_rel where tenant_id in (" + tenantsIdStr + ") and is_deleted=?";
            List<Map<String, Object>> list = DBMapper.queryUic(sql, 0);
            for (int i = 0; i < list.size(); i++) {
                UserTenantRelPO userTenantRelPO = map2UserTenantRelPO(list.get(i));
                userTenantRelPOS.add(userTenantRelPO);
            }
        }
        return userTenantRelPOS;
    }

    public static UserTenantRelPO map2UserTenantRelPO(Map<String, Object> map) {
        UserTenantRelPO userTenantRelPO = new UserTenantRelPO();
        userTenantRelPO.setId(Long.valueOf(map.get("id").toString()));
        userTenantRelPO.setTenantId(Long.valueOf(map.get("tenant_id").toString()));
        userTenantRelPO.setAdmin(Boolean.parseBoolean(map.get("is_admin").toString()));
        userTenantRelPO.setUserId(Long.valueOf(map.get("user_id").toString()));
        userTenantRelPO.setDeleted(Boolean.parseBoolean(map.get("is_deleted").toString()));
        return userTenantRelPO;
    }
}
