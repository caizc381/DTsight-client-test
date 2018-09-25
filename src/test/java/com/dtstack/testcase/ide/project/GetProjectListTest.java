package com.dtstack.testcase.ide.project;

import com.alibaba.fastjson.JSON;
import com.dtstack.base.Flag;
import com.dtstack.base.HttpResult;
import com.dtstack.base.dbcheck.ide.ProjectChecker;
import com.dtstack.dtcenter.common.pager.PageQuery;
import com.dtstack.model.dto.ide.ProjectDTO;
import com.dtstack.model.enums.ide.Sort;
import com.dtstack.testcase.ide.IdeBase;
import com.dtstack.util.CvsFileUtil;
import com.dtstack.util.db.SqlException;
import com.jayway.jsonpath.JsonPath;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class GetProjectListTest extends IdeBase {
    @Test(description = "根据名称，分页查询",groups = {"qa"},dataProvider = "getProjectList",dependsOnGroups = {"getProducts"})
    public void test_01_getProjectList(String... args) throws SqlException {
        String page = args[1];
        String pageSize = args[2];
        String orderBy=args[3];
        String sort = args[4];

        if (StringUtils.isEmpty(sort)){
            sort = Sort.DESC.name();
        }

        if (StringUtils.isEmpty(orderBy) || !OrderBy_JOBSUM.equals(orderBy)){
            orderBy = OrderBy_STICK;
        }

        String json = "{page:"+page +",pageSize:"+pageSize+"}";

        HttpResult result = httpclient.post(Flag.IDE,IDE_GetProjectList,json);
        String body = result.getBody();
        Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);
        System.out.println(body);

        List<ProjectDTO> projectDTOS = JSON.parseArray(JsonPath.read(body,"$.data.data").toString(),ProjectDTO.class);

        if (checkdb){
            //获取该userId下的projectIds
            Set<Long> userProjectIds = ProjectChecker.getUsefulProjectIds(defUicUserId,defTenantId,Boolean.valueOf(isAdmin));
            PageQuery pageQuery = new PageQuery(Integer.valueOf(page),Integer.valueOf(pageSize),orderBy,sort);
            List<Long> userProjectIdArrayList = new ArrayList<>();
            userProjectIdArrayList.addAll(userProjectIds);
            List<ProjectDTO> resultProjectDTOS = ProjectChecker.listJobSumByIdsAndFuzzyName(userProjectIdArrayList,"",FAILED_STATUS,pageQuery,defTenantId);
            Assert.assertEquals(projectDTOS.size(),resultProjectDTOS.size());
        }
    }

    @DataProvider
    public Iterator<String[]> getProjectList(){
        try {
            return CvsFileUtil.fromCvsFileToIterator("./csv/ide/project/getProjectList.csv",18);
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
