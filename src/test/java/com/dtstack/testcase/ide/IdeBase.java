package com.dtstack.testcase.ide;

import com.dtstack.base.ActionsDefine;
import com.dtstack.base.BaseTest;
import com.dtstack.model.enums.ide.common.TaskStatus;
import com.dtstack.util.db.SqlException;
import com.google.common.collect.Lists;

import java.util.List;

public class IdeBase extends BaseTest {
    public static final String OrderBy_JOBSUM = "jobSum";
    public static final String OrderBy_STICK = "rps.stick";
    public static final List<Integer> FAILED_STATUS = Lists.newArrayList(TaskStatus.FAILED.getStatus(), TaskStatus.SUBMITFAILD.getStatus());

    static {
        try {
            loadDefaultParams();
            //onceLoginInSystem(httpclient, Flag.UIC, defUicUsername, defUicPasswd);

            // 通过jvm进程的关闭钩子关闭共用的httpclient
            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    httpclient.shutdown();
                }
            });
            httpclient.setCookie(ActionsDefine.cookieValue);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void loadDefaultParams() throws SqlException {
        defRdosUser = com.dtstack.base.dbcheck.ide.common.UserChecker.getOne(defRdosUserId);
    }
}