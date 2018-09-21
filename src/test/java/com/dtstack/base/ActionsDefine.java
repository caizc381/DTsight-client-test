package com.dtstack.base;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public interface ActionsDefine {

    /******************************************************************************************
     *
     * action config
     *
     * ****************************************************************************************/
    //DTUIC
     public final static String Login = "/account/login"; //POST 登陆

    //IDE
    public final static String IDE_GetAllProjects="/common/project/getAllProjects";
    public final static String IDE_GetProjectByProjectId="/common/project/getProjectByProjectId";

    //UIC
    public final static String UIC_GetFullTenants="/account/user/get-full-tenants";//获取所有租户信息
    public final static String UIC_User="/user";
    public final static String UIC_Profile = "/account/user/profile";
    public final static String UIC_GetProducts="/get-products";




    // constant
    //public final static String cookieValue="dt_user_id=1; dt_username=admin%40dtstack.com; dt_token=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ0ZW5hbnRfaWQiOiIxIiwidXNlcl9pZCI6IjEiLCJ1c2VyX25hbWUiOiJhZG1pbkBkdHN0YWNrLmNvbSIsImV4cCI6MTU2NTQyNzk2MCwiaWF0IjoxNTM0MzIzOTY2fQ.-ODKDTpUUSvgoggqYmtrsD8UPog0a024GZiHPSn4P_4; dt_tenant_id=1; dt_tenant_name=DTStack%E7%A7%9F%E6%88%B7; dt_is_tenant_admin=true; dt_is_tenant_creator=false; DT_SESSION_ID=c0a413cf-7bc9-483f-b65f-23a7c7f92f1a; project_id=357";

    public final static String cookieValue="dt_language=zh; project_id=91; dt_user_id=1; dt_username=admin%40dtstack.com; dt_tenant_id=1; dt_tenant_name=DTStack%E7%A7%9F%E6%88%B7; dt_token=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ0ZW5hbnRfaWQiOiIxIiwidXNlcl9pZCI6IjEiLCJ1c2VyX25hbWUiOiJhZG1pbkBkdHN0YWNrLmNvbSIsImV4cCI6MTU2ODQ1NDYwOCwiaWF0IjoxNTM3MzUwNjE0fQ.4OUcXSFyzKcpmpvasATOuTU8b1DW-XuFzPCQnypc6Zw; dt_is_tenant_admin=true; dt_is_tenant_creator=false; DT_SESSION_ID=9106e95e-e929-4a04-a517-46a0bb8ff371";

    public final static String UNIQUE_SUBMIT_TOKEN = "unique-submit-token";
    public final static String X_Auth_Mytijian_Token = "x-auth-mytijian-token";

    public final static List<String> CheckTokenActionList = new ArrayList<String>(Arrays.asList(IDE_GetAllProjects
    ));

    public final static List<String> SetTokenActionList = new ArrayList<String>(Arrays.asList(IDE_GetAllProjects
    ));//OPS_LOGIN


    public final static List<String> SetAuthTokenList = new ArrayList<>(Arrays.asList());

    public final static List<String> CheckauthTokenList = new ArrayList<>(Arrays.asList());

    // response
    public final static String STATUSCODE = "statuscode";
    public final static String MESSAGE = "message";

}
