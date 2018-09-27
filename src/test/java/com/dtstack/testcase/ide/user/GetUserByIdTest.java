package com.dtstack.testcase.ide.user;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.dtstack.base.Flag;
import com.dtstack.base.HttpResult;
import com.dtstack.base.dbcheck.ide.common.RoleUserChecker;
import com.dtstack.base.dbcheck.ide.common.UserChecker;
import com.dtstack.model.domain.ide.RoleUser;
import com.dtstack.model.domain.ide.User;
import com.dtstack.model.enums.ide.RoleValue;
import com.dtstack.testcase.ide.IdeBase;
import com.dtstack.util.db.SqlException;
import com.jayway.jsonpath.JsonPath;
import org.apache.commons.collections.CollectionUtils;
import org.apache.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;
/**
 * 位置：点击开发套件的请求
 */

public class GetUserByIdTest extends IdeBase {
    @Test(description = "根据用户id获取用户信息", groups = {"qa"})
    public void test_01_getuserById() throws SqlException {
        HttpResult result = httpclient.post(Flag.IDE, User_GetUserById, "");

        String body = result.getBody();
        System.out.println(body);
        Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);

        Map<String, Object> userMap = JSON.parseObject(JsonPath.read(body, "$.data").toString(), new TypeReference<Map<String, Object>>() {
        });

        if (checkdb) {
            User user = UserChecker.getOne(defRdosUserId);
            Assert.assertEquals(userMap.get("id").toString(), user.getId().toString());
            Assert.assertEquals(userMap.get("dtuicUserId").toString(), user.getDtuicUserId().toString());
            Assert.assertEquals(userMap.get("defaultProjectId"), user.getDefaultProjectId());
            Assert.assertEquals(userMap.get("email").toString(), user.getEmail());
            Assert.assertEquals(userMap.get("phoneNumber"), user.getPhoneNumber());
            Assert.assertEquals(userMap.get("status").toString(), user.getStatus().toString());
            Assert.assertEquals(userMap.get("userName").toString(), defUicUsername);

            List<RoleUser> roleUsers = RoleUserChecker.getRoleUserIsAdmin(defRdosUserId, defRdosTenantId);
            if (CollectionUtils.isEmpty(roleUsers)) {
                Assert.assertEquals(userMap.get("isAdminAbove"), 0);
            } else {
                if (roleUsers.size() == 1 && roleUsers.get(0).getRole().getRoleValue() == RoleValue.MEMBER.getRoleValue()) {
                    Assert.assertEquals(userMap.get("isAdminAbove"), 0);
                } else {
                    for (RoleUser roleUser : roleUsers
                    ) {
                        if (roleUser.getRole().getRoleValue() == RoleValue.TEANTOWNER.getRoleValue()) {
                            Assert.assertEquals(userMap.get("isAdminAbove"), 2);
                            break;
                        }
                    }
                }
            }
        }
    }
}
