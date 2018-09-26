package com.dtstack.testcase;

import com.dtstack.base.BaseTest;
import com.dtstack.base.Flag;
import com.dtstack.base.dbcheck.uic.account.TenantChecker;
import com.dtstack.base.dbcheck.uic.account.UserChecker;
import com.dtstack.model.enums.ide.TaskStatus;
import com.dtstack.util.db.SqlException;
import com.google.common.collect.Lists;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Calendar;
import java.util.List;

public class UicBase extends BaseTest {
    //public static MyHttpClient httpclient;
    public static final String OrderBy_JOBSUM = "jobSum";
    public static final String OrderBy_STICK = "rps.stick";
    public static final List<Integer> FAILED_STATUS = Lists.newArrayList(TaskStatus.FAILED.getStatus(), TaskStatus.SUBMITFAILD.getStatus());

    static {
        try {
            loadDefaultParams();
            onceLoginInSystem(httpclient, Flag.UIC, defUicUsername, defUicPasswd);

            // 通过jvm进程的关闭钩子关闭共用的httpclient
            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    httpclient.shutdown();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void loadDefaultParams() throws SqlException {
        defUicUser = UserChecker.getUserByUicUserId(defUicUserId);
        defTenant = TenantChecker.getTenantByTenantId(defTenantId);
    }

    /**
     * 将InputStream 转化为String
     *
     * @param stream inputstream
     * @param utf8   字符集
     * @return
     * @throws IOException
     */
    public String getStreamAsString(InputStream stream, Charset utf8) throws IOException {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream, utf8), 8192);
            StringWriter writer = new StringWriter();

            char[] chars = new char[8192];
            int count = 0;
            while ((count = reader.read(chars)) > 0) {
                writer.write(chars, 0, count);
            }

            return writer.toString();
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
    }

    /*********** public functions *******/
    /**
     * 获取前数分钟的时间
     *
     * @param count
     * @return
     */
    public String getAbsoluteTime(int count) {
        Calendar beforeTime = Calendar.getInstance();
        beforeTime.add(Calendar.MINUTE, -count);
        return simplehms.format(beforeTime.getTime());
    }

    public String getAbsoluteMiniTime(int count) {
        Calendar beforeTime = Calendar.getInstance();
        beforeTime.add(Calendar.SECOND, -count);
        return simplehms.format(beforeTime.getTime());
    }


}