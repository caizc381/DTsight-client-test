package com.tijiantest.testcase.crm;

import com.tijiantest.base.Flag;
import com.tijiantest.base.MyHttpClient;
import com.tijiantest.base.dbcheck.AccountChecker;
import com.tijiantest.model.account.SystemTypeEnum;

/**
 * 深对接体检中心
 */
public class CrmDeepBase extends  CrmBase{

    public static MyHttpClient deepClient;
    public static int deepManagerId;
    static {
        deepClient = new MyHttpClient();
        onceLoginInSystem(deepClient, Flag.CRM, defDeepUsername, defDeepPaswd);
        deepManagerId = AccountChecker.getUserInfo(defDeepUsername, SystemTypeEnum.CRM_LOGIN.getCode()).getAccount_id();
        // 通过jvm进程的关闭钩子关闭共用的httpclient
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                deepClient.shutdown();
            }
        });
    }
}
