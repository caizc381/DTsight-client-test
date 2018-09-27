package com.dtstack.testcase.ide.project;

import com.alibaba.fastjson.JSON;
import com.dtstack.base.Flag;
import com.dtstack.base.HttpResult;
import com.dtstack.base.dbcheck.ide.common.RoleUserChecker;
import com.dtstack.model.domain.ide.Role;
import com.dtstack.model.domain.ide.RoleUser;
import com.dtstack.model.domain.ide.User;
import com.dtstack.model.vo.ide.UserRoleVO;
import com.dtstack.testcase.ide.IdeBase;
import com.dtstack.util.CvsFileUtil;
import com.dtstack.util.db.SqlException;
import com.jayway.jsonpath.JsonPath;
import org.apache.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * 位置：开发套件-》项目管理-》项目成员管理
 */
public class GetProjectUsersTest extends IdeBase {

    @Test(description = "角色权限改版后，项目成员管理", groups = {"qa"}, dataProvider = "getProjectUsers")
    public void test_01_getProjectUsers(String... args) throws SqlException {
        String currentPage = args[1];
        String pageSize = args[2];
        Map<String, Object> params = new HashMap<>();

        params.put("currentPage", currentPage);
        params.put("pageSize", pageSize);
        params.put("projectId", defProjectId);

        String json = JSON.toJSONString(params);

        HttpResult result = httpclient.post(Flag.IDE, Project_GetProjectUsers, json);
        String body = result.getBody();
        Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);

        List<UserRoleVO> userRoleVOS = JSON.parseArray(JsonPath.read(body, "$.data.data").toString(), UserRoleVO.class);


        if (checkdb) {
            List<RoleUser> roleUsers = RoleUserChecker.listByTenantIdAndProjectIdAndUserName(defRdosTenantId, defProjectId, "");

            //去重
            Map<Long, UserRoleVO> userMap = new HashMap<>();
            for (RoleUser roleUser : roleUsers
            ) {
                UserRoleVO vo = userMap.get(roleUser.getUserId());
                if (vo == null) {
                    vo = new UserRoleVO();
                    vo.setUserId(roleUser.getUserId());
                    vo.setUser(roleUser.getUser());
                    vo.setGmtCreate(roleUser.getGmtCreate());
                    userMap.put(roleUser.getUserId(), vo);
                }
                vo.addRoles(roleUser.getRole());
            }

            List<UserRoleVO> resultRoleUsers = new ArrayList<>(userMap.values());


            Assert.assertEquals(userRoleVOS.size(), resultRoleUsers.size());
            Collections.sort(userRoleVOS, new Comparator<UserRoleVO>() {
                @Override
                public int compare(UserRoleVO o1, UserRoleVO o2) {
                    return Integer.valueOf(o1.getUserId() + "") - Integer.valueOf(o2.getUserId() + "");
                }
            });

            Collections.sort(resultRoleUsers, new Comparator<UserRoleVO>() {
                @Override
                public int compare(UserRoleVO o1, UserRoleVO o2) {
                    return Integer.valueOf(o1.getUserId() + "") - Integer.valueOf(o2.getUserId() + "");
                }
            });

            for (int i = 0; i < userRoleVOS.size(); i++) {
                //userId
                Assert.assertEquals(userRoleVOS.get(i).getUserId(), resultRoleUsers.get(i).getUserId());
                //获取user
                User user = userRoleVOS.get(i).getUser();
                User resultUser = resultRoleUsers.get(i).getUser();
                Assert.assertEquals(user.getDefaultProjectId(), resultUser.getDefaultProjectId());
                Assert.assertEquals(user.getDtuicUserId(), resultUser.getDtuicUserId(), i + " ------------  " + i);
                Assert.assertEquals(user.getEmail(), resultUser.getEmail());
                Assert.assertEquals(user.getId(), resultUser.getId());
                Assert.assertEquals(user.getIsDeleted(), resultUser.getIsDeleted());
                Assert.assertEquals(user.getPhoneNumber(), resultUser.getPhoneNumber());
                Assert.assertEquals(user.getStatus(), resultUser.getStatus());
                Assert.assertEquals(user.getUserName(), resultUser.getUserName());

                //获取roles
                List<Role> roles = userRoleVOS.get(i).getRoles();
                List<Role> resultRoles = resultRoleUsers.get(i).getRoles();

                Assert.assertEquals(roles.size(), resultRoles.size());
                for (int j = 0; j < roles.size(); j++) {
                    Role role = roles.get(j);
                    Role resultRole = resultRoles.get(j);
                    Assert.assertEquals(role.getId(), resultRole.getId());
                    Assert.assertEquals(role.getIsDeleted(), resultRole.getIsDeleted());
                    Assert.assertEquals(role.getModifyUserId(), resultRole.getModifyUserId());
                    Assert.assertEquals(role.getProjectId(), resultRole.getProjectId());
                    Assert.assertEquals(role.getRoleDesc(), resultRole.getRoleDesc());
                    Assert.assertEquals(role.getRoleName(), resultRole.getRoleName());
                    Assert.assertEquals(role.getRoleType(), resultRole.getRoleType());
                    Assert.assertEquals(role.getRoleValue(), resultRole.getRoleValue());
                    Assert.assertEquals(role.getTenantId(), resultRole.getTenantId());
                }
            }
        }
    }

    @DataProvider
    public Iterator<String[]> getProjectUsers() {
        try {
            return CvsFileUtil.fromCvsFileToIterator("./csv/ide/project/getProjectUsers.csv", 18);
        } catch (FileNotFoundException e) {
            // TODO 自动生成的 catch 块
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            // TODO 自动生成的 catch 块
            e.printStackTrace();
        }
        return null;
    }
}
