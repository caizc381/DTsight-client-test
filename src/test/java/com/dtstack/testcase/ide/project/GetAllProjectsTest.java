package com.dtstack.testcase.ide.project;

import com.dtstack.base.Flag;
import com.dtstack.base.HttpResult;
import com.dtstack.model.domain.ide.Project;
import com.dtstack.testcase.ide.IdeBase;
import org.apache.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

public class GetAllProjectsTest extends IdeBase {

    public static List<Project> projects = new ArrayList<>();

    @Test(description = "",groups = {"qa"})
    public void test_01_getAllProject(){
        HttpResult result = httpclient.post(Flag.IDE,IDE_GetAllProjects,"");
        System.out.println(result.getBody());
        Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);

    }

}
