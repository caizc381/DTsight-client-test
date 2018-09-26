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

    //IDE
    public final static String IDE_GetProjectList = "/common/project/getProjectList";//根据项目名，分页查询
    public final static String IDE_GetAllProjects = "/common/project/getAllProjects";
    public final static String IDE_GetProjectByProjectId = "/common/project/getProjectByProjectId";
    public final static String IDE_GetUserById = "/common/user/getUserById";//根据用户id获取用户信息
    public final static String IDE_GetProjects = "/common/project/getProjects";//1.首页显示内容-不做权限设置;2.控制台顶端-项目下拉列表
    public final static String IDE_GetHiveCatalogue = "/batch/batchHiveCatalogue/getHiveCatalogue";

    //console
    public final static String CONSOLE_Status = "/service/status/status";

    // constant
    //public final static String cookieValue="dt_user_id=1; dt_username=admin%40dtstack.com; dt_token=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ0ZW5hbnRfaWQiOiIxIiwidXNlcl9pZCI6IjEiLCJ1c2VyX25hbWUiOiJhZG1pbkBkdHN0YWNrLmNvbSIsImV4cCI6MTU2NTQyNzk2MCwiaWF0IjoxNTM0MzIzOTY2fQ.-ODKDTpUUSvgoggqYmtrsD8UPog0a024GZiHPSn4P_4; dt_tenant_id=1; dt_tenant_name=DTStack%E7%A7%9F%E6%88%B7; dt_is_tenant_admin=true; dt_is_tenant_creator=false; DT_SESSION_ID=c0a413cf-7bc9-483f-b65f-23a7c7f92f1a; project_id=357";
    public final static String cookieValue="dt_user_id="+BaseTest.defUicUserId+"; dt_username="+BaseTest.defUicUsername+"; dt_token=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ0ZW5hbnRfaWQiOiIxIiwidXNlcl9pZCI6IjEiLCJ1c2VyX25hbWUiOiJhZG1pbkBkdHN0YWNrLmNvbSIsImV4cCI6MTU2NTQyNzk2MCwiaWF0IjoxNTM0MzIzOTY2fQ.-ODKDTpUUSvgoggqYmtrsD8UPog0a024GZiHPSn4P_4; dt_tenant_id="+BaseTest.defTenantId+"; dt_tenant_name="+BaseTest.defTenantName+"; dt_is_tenant_admin=true; dt_is_tenant_creator=false; DT_SESSION_ID=c0a413cf-7bc9-483f-b65f-23a7c7f92f1a; project_id=357";
}
