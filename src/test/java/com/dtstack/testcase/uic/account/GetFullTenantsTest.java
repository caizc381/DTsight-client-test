package com.dtstack.testcase.uic.account;

import com.alibaba.fastjson.JSON;
import com.dtstack.base.Flag;
import com.dtstack.base.HttpResult;
import com.dtstack.base.dbcheck.uic.account.TenantChecker;
import com.dtstack.base.dbcheck.uic.account.UserChecker;
import com.dtstack.base.dbcheck.uic.account.UserTenantRelChecker;
import com.dtstack.lang.Langs;
import com.dtstack.lang.exception.BizException;
import com.dtstack.model.po.uic.account.TenantPO;
import com.dtstack.model.po.uic.account.UserPO;
import com.dtstack.model.po.uic.account.UserTenantRelPO;
import com.dtstack.model.vo.uic.account.UserFullTenantAdminVo;
import com.dtstack.model.vo.uic.account.UserFullTenantVo;
import com.dtstack.testcase.UicBase;
import com.dtstack.util.db.SqlException;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.jayway.jsonpath.JsonPath;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

public class GetFullTenantsTest extends UicBase {
    public static List<UserFullTenantVo> userFullTenantVos = new ArrayList<>();

    @Test(description = "获取所有租户信息", groups = {"qa", "getFullTenants"})
    public void getFullTenants() throws SqlException {
        List<NameValuePair> pairs = new ArrayList<>();
        HttpResult result = httpclient.get(Flag.UIC, UIC_GetFullTenants);

        System.out.println(result.getBody());
        System.out.println(result.getCode());
        String body = result.getBody();

         userFullTenantVos = JSON.parseArray(JsonPath.read(body, "$.data").toString(),
                UserFullTenantVo.class);
        System.out.println(body);
        Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);


        if (checkdb) {
            List<UserFullTenantVo> resultUserFullTenantVos = TenantChecker.getFullTenants();
            Assert.assertEquals(userFullTenantVos.size(), resultUserFullTenantVos.size());

            for (int i = 0; i < resultUserFullTenantVos.size(); i++) {
                UserFullTenantVo userFullTenantVo = userFullTenantVos.get(i);
                UserFullTenantVo resultUserFullTenantVo = resultUserFullTenantVos.get(i);
                Assert.assertEquals(userFullTenantVo.getTenantId(), resultUserFullTenantVo.getTenantId());
                Assert.assertEquals(userFullTenantVo.getTenantName(), resultUserFullTenantVo.getTenantName());
                Assert.assertEquals(userFullTenantVo.getTenantDesc(), resultUserFullTenantVo.getTenantDesc());
            }

        }
    }

    public List<UserTenantRelPO> findByUserId(Long userId) throws SqlException {
        UserPO userPO = findUser(userId);
        List<UserTenantRelPO> userTenantRelPOS = new ArrayList<>();
        if (userPO.isRoot()) {
            List<TenantPO> tenantPO = TenantChecker.findByIsDeletedIsFalse();
            for (int i = 0; i < tenantPO.size(); i++) {
                UserTenantRelPO userTenantRelPO = TenantChecker.tenantPOconvert2UserTenantRelPO(userId, userPO, tenantPO.get(i));
                userTenantRelPOS.add(userTenantRelPO);
            }
        } else {
            userTenantRelPOS = UserTenantRelChecker.findByUserIdAndIsDeletedIsFalse(userId);
        }
        return userTenantRelPOS;
    }

    public UserPO findUser(Long userId) throws SqlException {
        UserPO userPO = UserChecker.findByIdAndIsDeletedIsFalse(userId);
        if (Objects.isNull(userPO)) {
            log.error("can't find " + userId + " user");
            throw new BizException("获取用户信息失败");
        } else {
            return userPO;
        }
    }

    public List<UserFullTenantVo> userTenantRelPOconvert2UserFullTenantVo(List<UserTenantRelPO> tenantRelList) throws SqlException {
        List<UserFullTenantVo> list = new ArrayList<>();
        if (Langs.isNotEmpty(tenantRelList)) {
            //设置基础信息
            tenantRelList.forEach(input -> {
                UserFullTenantVo vo = new UserFullTenantVo();
                vo.setTenantId(input.getTenantId());
                vo.setAdmin(input.isAdmin());
                vo.setCurrent(input.getTenantId().equals(Long.valueOf(defTenantId)));
                list.add(vo);
            });

            //设置租户信息
            List<TenantPO> tenantList = findByIds(list.stream().map(input -> input.getTenantId()).collect(toList()));
            Map<Long, TenantPO> tenantPOMap = tenantList.stream().collect(Collectors.toMap(input -> input.getId(), input -> input));

            //租户空间其他信息
            List<UserTenantRelPO> rels = findByTenantIds(list.stream().map(input -> input.getTenantId()).collect(toList()));
            //Map<Long, UserDTO> adminUserMap = ;

            ArrayListMultimap<Long, UserTenantRelPO> relMap = ArrayListMultimap.create();
            rels.stream().forEach(input -> relMap.put(input.getTenantId(), input));

            list.forEach(input -> {
                if (relMap.containsKey(input.getTenantId())) {
                    List<UserTenantRelPO> relList = relMap.get(input.getTenantId());
                    input.setOtherUserCount(relList.stream().filter(x -> !x.isAdmin()).collect(toList()).size());
                    input.setAdminList(Lists.newArrayList());

                    relList.stream().filter(x -> x.isAdmin()).forEach(x -> {
                        UserFullTenantAdminVo vo = new UserFullTenantAdminVo();
                        vo.setUserId(x.getUserId());
                        input.getAdminList().add(vo);
                    });
                } else {
                    input.setOtherUserCount(0);
                    input.setAdminList(Lists.newArrayList());
                }
            });

        }
        return list;
    }

    public List<TenantPO> findByIds(Collection<Long> tenantIds) throws SqlException {
        if (Langs.isEmpty(tenantIds)) {
            return Lists.newArrayList();
        } else {
            return TenantChecker.findByIdInAndIsDeletedIsFalse(tenantIds);
        }
    }

    public List<UserTenantRelPO> findByTenantIds(Collection<Long> tenantIds) throws SqlException {
        if (Langs.isNotEmpty(tenantIds)) {
            return UserTenantRelChecker.findByTenantIdInAndIsDeletedIsFalse(tenantIds);
        } else {
            return Lists.newArrayList();
        }
    }
}
