package com.dtstack.base;


public interface ActionsDefine {

    /******************************************************************************************
     *
     * action config
     *
     * ****************************************************************************************/
    //UIC
    public final static String Login = "/account/login"; //POST 登陆
    public final static String UIC_GetFullTenants = "/account/user/get-full-tenants";//获取所有租户信息
    public final static String UIC_User = "/user";//用户信息
    public final static String UIC_Profile = "/account/user/profile";
    public final static String UIC_SwitchTenant = "/account/user/switch-tenant";//切换租户
    public final static String UIC_ListAll = "/product/list-all";//获取所有产品信息

    //UIC-API
    public final static String API_GetProducts = "/get-products";//获取所有产品

    /******************************************************************************************
     *
     * IDE - Common - Project
     *
     * ****************************************************************************************/

    public final static String Project_GetProjectList = "/common/project/getProjectList";//根据项目名，分页查询
    public final static String Project_GetAllProjects = "/common/project/getAllProjects";//获取所有项目，在筛选下拉框里使用
    public final static String Project_GetProjectByProjectId = "/common/project/getProjectByProjectId";
    public final static String Project_GetProjects = "/common/project/getProjects";//1.首页显示内容-不做权限设置;2.控制台顶端-项目下拉列表
    public final static String Project_CloseOrOpenSchedule = "/common/project/closeOrOpenSchedule";//开启或关闭调度
    public final static String Project_GetBindingProjects = "/common/project/getBindingProjects";//获取待绑定的项目列表
    public final static String Project_BindingProject = "/common/project/bindingProject";//绑定测试环境和生产环境
    public final static String Project_GetProjectUsers = "/common/project/getProjectUsers";//角色权限改版后，项目成员管理

    /******************************************************************************************
     *
     * IDE - Common - User
     *
     * ****************************************************************************************/
    public final static String User_GetUserById = "/common/user/getUserById";//根据用户id获取用户信息

    /******************************************************************************************
     *
     * IDE - Batch - BatchHiveCatalogue
     *
     * ****************************************************************************************/
    public final static String BatchHiveCatalogue_GetHiveCatalogue = "/batch/batchHiveCatalogue/getHiveCatalogue";//获取租户下的类目


    /******************************************************************************************
     *
     * IDE - Batch - BatchTaskShade
     *
     * ****************************************************************************************/
    public final static String BatchTaskShade_PageQuery = "/batch/batchTaskShade/pageQuery";//分页查询已提交的任务

    /******************************************************************************************
     *
     * IDE - Batch - BatchResource
     *
     * ****************************************************************************************/
    public final static String BatchResource_PageQuery = "/batch/batchResource/pageQuery";//资源分页查询

    /******************************************************************************************
     *
     * IDE - Batch - BatchFunction
     *
     * ****************************************************************************************/
    public final static String BatchFunction_PageQuery = "/batch/batchFunction/pageQuery";//自定义函数分页查询

    /******************************************************************************************
     *
     * IDE - Batch - BatchHiveTableInfo
     *
     * ****************************************************************************************/
    public final static String BatchHiveTableInfo_SimplePageQuery = "batch/batchHiveTableInfo/simplePageQuery";//表分页查询


    //console
    public final static String CONSOLE_Status = "/service/status/status";

    // constant
    //public final static String cookieValue="dt_user_id=1; dt_username=admin%40dtstack.com; dt_token=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ0ZW5hbnRfaWQiOiIxIiwidXNlcl9pZCI6IjEiLCJ1c2VyX25hbWUiOiJhZG1pbkBkdHN0YWNrLmNvbSIsImV4cCI6MTU2NTQyNzk2MCwiaWF0IjoxNTM0MzIzOTY2fQ.-ODKDTpUUSvgoggqYmtrsD8UPog0a024GZiHPSn4P_4; dt_tenant_id=1; dt_tenant_name=DTStack%E7%A7%9F%E6%88%B7; dt_is_tenant_admin=true; dt_is_tenant_creator=false; DT_SESSION_ID=c0a413cf-7bc9-483f-b65f-23a7c7f92f1a; project_id=357";
    public final static String cookieValue = "dt_user_id=" + BaseTest.defUicUserId + "; dt_username=" + BaseTest.defUicUsername.replace("@", "%40") + "; dt_token=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ0ZW5hbnRfaWQiOiIxIiwidXNlcl9pZCI6IjEiLCJ1c2VyX25hbWUiOiJhZG1pbkBkdHN0YWNrLmNvbSIsImV4cCI6MTU2NTQyNzk2MCwiaWF0IjoxNTM0MzIzOTY2fQ.-ODKDTpUUSvgoggqYmtrsD8UPog0a024GZiHPSn4P_4; dt_tenant_id=" + BaseTest.defTenantId + "; dt_tenant_name=" + BaseTest.defTenantName + "; dt_is_tenant_admin=true; dt_is_tenant_creator=false; DT_SESSION_ID=c0a413cf-7bc9-483f-b65f-23a7c7f92f1a; project_id=" + BaseTest.defProjectId;
}
