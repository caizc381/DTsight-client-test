package com.dtstack.base.dbcheck.ide.common;

import com.dtstack.base.BaseTest;
import com.dtstack.model.domain.ide.User;
import com.dtstack.util.StringUtil;
import com.dtstack.util.db.DBMapper;
import com.dtstack.util.db.SqlException;

import java.util.*;

public class UserChecker extends BaseTest {
    public static String selectContentFragment = "id,dtuic_user_id,user_name,email,status,gmt_create,gmt_modified,is_deleted,default_project_id,phone_number";

    public static User getOne(Long id) throws SqlException {
        String sql = "select id,dtuic_user_id,user_name,email,status,gmt_create,gmt_modified,is_deleted,"
                + " default_project_id,phone_number from rdos_user where id=? and is_deleted=0";
        List<Map<String, Object>> list = DBMapper.query(sql, id);
        User user = map2User(list.get(0));
        return user;
    }

    public static List<User> listByIds(Collection<Long> ids) throws SqlException {
        String idStr = "";
        Iterator it = ids.iterator();
        while (it.hasNext()) {
            idStr += it.next() + ",";
        }
        List<User> userList = new ArrayList<>();

        if (!idStr.equals("")) {
            idStr = StringUtil.removeCommaAtEnd(idStr);

            String sql = "select " + selectContentFragment + " from rdos_user where 1=1 and id in (" + idStr + ") and is_deleted=0";
            List<Map<String, Object>> list = DBMapper.query(sql);

            for (int i = 0; i < list.size(); i++) {
                User user = map2User(list.get(i));
                userList.add(user);
            }
        }
        return userList;
    }

    public static User map2User(Map<String, Object> map) {
        User user = new User();
        user.setId(Long.valueOf(map.get("id").toString()));
        user.setDtuicUserId(Long.valueOf(map.get("dtuic_user_id").toString()));
        user.setUserName(map.get("user_name").toString());
        user.setEmail(map.get("email").toString());
        user.setStatus(Integer.valueOf(map.get("status").toString()));
        user.setIsDeleted(Integer.valueOf(map.get("is_deleted").toString()));
        if (map.get("default_project_id") != null) {
            user.setDefaultProjectId(Long.valueOf(map.get("default_project_id").toString()));
        }

        if (map.get("phone_number") != null) {
            user.setPhoneNumber(map.get("phone_number").toString());
        }

        return user;
    }
}
