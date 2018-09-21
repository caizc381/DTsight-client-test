package com.dtstack.base.dbcheck;

import com.dtstack.base.BaseTest;
import com.dtstack.model.domain.ide.User;
import com.dtstack.util.db.DBMapper;
import com.dtstack.util.db.SqlException;

import java.util.List;
import java.util.Map;


public class UserChecker extends BaseTest {
    public static User getUserByDTUicUserId(int dtUicUserId) throws SqlException {

        User user = new User();
        String sql = "select * from rdos_user where dtuic_user_id=?";
        List<Map<String, Object>> list = DBMapper.query(sql, dtUicUserId);
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

    public void  findUserByIdAndIsDeletedIsFalse(int userId){

    }
}
