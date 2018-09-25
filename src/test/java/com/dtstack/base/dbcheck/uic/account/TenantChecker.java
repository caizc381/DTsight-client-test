package com.dtstack.base.dbcheck.uic.account;

import com.dtstack.base.BaseTest;
import com.dtstack.model.domain.ide.Tenant;
import com.dtstack.model.po.uic.account.TenantPO;
import com.dtstack.model.po.uic.account.UserPO;
import com.dtstack.model.po.uic.account.UserTenantRelPO;
import com.dtstack.model.vo.uic.account.UserFullTenantVo;
import com.dtstack.util.StringUtil;
import com.dtstack.util.db.DBMapper;
import com.dtstack.util.db.SqlException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class TenantChecker extends BaseTest {
    public static Tenant getTenantByTenantId(Long tenantId) throws SqlException {
        Tenant tenant = new Tenant();
        String sql = "select * from rdos_tenant where id=?";
        List<Map<String, Object>> list = DBMapper.query(sql, tenantId);

        for (Map<String, Object> map : list
        ) {
            tenant.setId(Long.valueOf(map.get("id").toString()));
            tenant.setDtuicTenantId(Long.valueOf(map.get("dtuic_tenant_id").toString()));
            tenant.setTenantName(map.get("tenant_name").toString());
            if (map.get("tenant_desc") != null) {
                tenant.setTenantDesc(map.get("tenant_desc").toString());
            }
            tenant.setStatus(Integer.valueOf(map.get("status").toString()));
            tenant.setIsDeleted(Integer.valueOf(map.get("is_deleted").toString()));
            if (map.get("create_user_id") != null) {
                tenant.setCreateUserId(Long.valueOf(map.get("create_user_id").toString()));
            }
        }
        return tenant;
    }

    public static List<TenantPO> findByIsDeletedIsFalse() throws SqlException {
        String sql = "select * from uic_tenant where is_deleted=?";
        List<Map<String, Object>> list = DBMapper.queryUic(sql, 0);
        List<TenantPO> tenantPOS = new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {
            Map<String, Object> map = list.get(i);
            TenantPO po = map2TenantPO(map);
            tenantPOS.add(po);
        }
        return tenantPOS;
    }

    public static UserTenantRelPO tenantPOconvert2UserTenantRelPO(Long userId, UserPO userPO, TenantPO tenantPO) {
        UserTenantRelPO userTenantRelPO = new UserTenantRelPO();
        userTenantRelPO.setDeleted(Boolean.FALSE);
        userTenantRelPO.setGmtCreate(userPO.getGmtCreate());
        userTenantRelPO.setGmtModified(userPO.getGmtModified());
        userTenantRelPO.setModifier(userId);
        userTenantRelPO.setCreator(userId);
        userTenantRelPO.setAdmin(Boolean.TRUE);
        userTenantRelPO.setTenantId(tenantPO.getId());
        userTenantRelPO.setUserId(userId);
        return userTenantRelPO;
    }

    public static List<TenantPO> findByIdInAndIsDeletedIsFalse(Iterable<Long> tenantIds) throws SqlException {
        List<TenantPO> tenantPOS = new ArrayList<>();
        String ids = "";
        for (Iterator iter = tenantIds.iterator(); iter.hasNext(); ) {
            ids += iter.next() + ",";
        }
        ids = StringUtil.removeCommaAtEnd(ids);
        if (!ids.equals("")) {
            String sql = "select * from uic_tenant where id in (" + ids + ") and is_deleted=?";
            List<Map<String, Object>> list = DBMapper.queryUic(sql, 0);
            for (int i = 0; i < list.size(); i++) {
                Map<String, Object> map = list.get(i);
                TenantPO tenantPO = map2TenantPO(map);
                tenantPOS.add(tenantPO);
            }
        }
        return tenantPOS;
    }

    public static List<UserFullTenantVo> getFullTenants() throws SqlException {
        String sql = "select * from uic_tenant where is_deleted=?";
        List<Map<String, Object>> list = DBMapper.queryUic(sql, "N");
        List<UserFullTenantVo> userFullTenantVos = new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {
            UserFullTenantVo userFullTenantVo = map2UserFullTenantVo(list.get(i));
            userFullTenantVos.add(userFullTenantVo);
        }
        return userFullTenantVos;
    }

    public static TenantPO map2TenantPO(Map<String, Object> map) {
        TenantPO po = new TenantPO();
        po.setId(Long.valueOf(map.get("id").toString()));
        po.setAgentToken(map.get("agent_token").toString());
        po.setDeleted(Boolean.valueOf(map.get("is_deleted").toString()));
        po.setBelongUserId(Long.valueOf(map.get("belong_user_id").toString()));
        po.setContactPhone(map.get("contact_phone").toString());
        po.setContactEmail(map.get("contact_email").toString());
        po.setContactName(map.get("contact_name").toString());
        if (map.get("tenant_desc") != null) {
            po.setTenantDesc(map.get("tenant_desc").toString());
        }

        po.setTenantName(map.get("tenant_name").toString());
        return po;
    }

    public static UserFullTenantVo map2UserFullTenantVo(Map<String, Object> map) {
        UserFullTenantVo userFullTenantVo = new UserFullTenantVo();
        userFullTenantVo.setTenantId(Long.valueOf(map.get("id").toString()));
        if (map.get("tenant_desc")!=null){
            userFullTenantVo.setTenantDesc(map.get("tenant_desc").toString());
        }

        userFullTenantVo.setTenantName(map.get("tenant_name").toString());
        return userFullTenantVo;
    }


}
