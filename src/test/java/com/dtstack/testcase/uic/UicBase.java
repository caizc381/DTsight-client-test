package com.dtstack.testcase.uic;

import com.dtstack.base.*;
import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.*;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.*;

public class UicBase extends BaseTest {
    //public static MyHttpClient httpclient;
    static {
        try {
            //loadDefaultParams();
            //httpclient = new MyHttpClient();
            onceLoginInSystem(httpclient, Flag.DTUIC,defDtuicUsername, defDtuicPasswd);

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

	/*private static void loadDefaultParams() throws SqlException {
		defChannelPlatManager = testConf.getValue(ConfDefine.CHANNELINFO, ConfDefine.CHANNELPLATMANAGER);
		defChannelPlatManagerPwd = testConf.getValue(ConfDefine.CHANNELINFO, ConfDefine.CHANNELPLATMANAGERPWD);

		defUser = getUserInfo(defChannelUsername);
		defaccountId = defUser.getAccount_id();
		defChannelPlatMangerId = AccountChecker.getUserInfo(defChannelPlatManager, RoleEnum.CRM_USER.getCode()).getAccount_id();
	}*/

    public static HttpResult loginChannel(MyHttpClient hc, String username, String password, String validationCode, String callbackurl, String rememberMe) {
        HttpResult result = null;
        List<NameValuePair> loginParams = new ArrayList<>();
        NameValuePair pa1 = new BasicNameValuePair("username", username);
        NameValuePair pa2 = new BasicNameValuePair("password", password);
        NameValuePair pa3 = new BasicNameValuePair("validationCode", validationCode);
        NameValuePair pa4 = new BasicNameValuePair("callbackurl", callbackurl);
        NameValuePair pa5 = new BasicNameValuePair("rememberMe", rememberMe);
        loginParams.add(pa1);
        loginParams.add(pa2);
        loginParams.add(pa3);
        loginParams.add(pa4);
        loginParams.add(pa5);
        result = httpclient.post(Flag.DTUIC, Login, loginParams);
        return result;
    }

    /**
     *
     * 将InputStream 转化为String
     *
     * @param stream
     *            inputstream
     * @param utf8
     *            字符集
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