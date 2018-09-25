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
    public final static String UIC_ListAll= "/product/list-all";//获取所有产品信息

    //UIC-API
    public final static String API_GetProducts = "/get-products";//获取所有产品

    //IDE
    public final static String IDE_GetProjectList = "/common/project/getProjectList";//根据项目名，分页查询
    public final static String IDE_GetAllProjects = "/common/project/getAllProjects";
    public final static String IDE_GetProjectByProjectId = "/common/project/getProjectByProjectId";

    // constant

    public final static String cookieValue="dt_language=zh; dt_user_id=6380; dt_username=admin%40dtstack.com; dt_token=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VyX2lkIjoiNjM4MCIsInVzZXJfbmFtZSI6ImFkbWluQGR0c3RhY2suY29tIiwiZXhwIjoxNTUzMDg3MDY2LCJpYXQiOjE1Mzc1MzUwNjZ9.S3oifhEY3uekl27oCCDRzsCgSgmxTdEMLGYMsyzOchw; DT_SESSION_ID=36cc40bd-a928-4ec1-aec9-4050dd4e320c";

}
