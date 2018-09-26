package com.dtstack.base;

import com.dtstack.model.domain.ide.Tenant;
import com.dtstack.model.domain.ide.User;
import com.dtstack.util.ConfParser;
import com.ibm.staf.STAFException;
import com.ibm.staf.STAFHandle;
import com.ibm.staf.STAFResult;
import com.ibm.staf.STAFUtil;
import org.apache.http.HttpStatus;
import org.apache.log4j.Logger;
import org.testng.Assert;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CountDownLatch;

public class BaseTest implements ConfDefine, ActionsDefine {

    public static MyHttpClient httpclient;


    protected final static Logger log = Logger.getLogger(BaseTest.class);
    protected static STAFHandle stafHandle = null;
    protected static CountDownLatch countDownLatch;

    // config
    public final static ConfParser envConf = new ConfParser(ENV_CONFIG);
    public final static ConfParser testConf = new ConfParser(TEST_CONFIG);
    public static int connTimeout;
    public static int socketTimeout;
    public static int maxConnPerRoute;
    public static int maxConnTotal;
    public static int mysqlWaitTime;
    public static String uicurl;
    public static String ideurl;
    public static String uicapiurl;
    public static String consoleurl;
    //mysql-ide
    public static String ideDbUrl;
    public static String ideDbUser;
    public static String ideDbPwd;
    //mysql-uic
    public static String uicDbUrl;
    public static String uicDbUser;
    public static String uicDbPwd;


    public static boolean useStaf;
    public static SimpleDateFormat sd = new SimpleDateFormat("yyyy");
    public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    public static SimpleDateFormat simplehms = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static SimpleDateFormat cstFormater = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);
    public static SimpleDateFormat gmtFormater = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
    public static boolean checkdb;
    public static boolean checkmongo;


    //user & password
    public static String defUicUsername;
    public static String defUicPasswd;
    public static Long defUicUserId;
    public static User defUicUser;
    public static Long defRdosUserId;
    public static Long defTenantId;
    public static Long defRdosTenantId;
    public static Tenant defTenant;
    public static String defTenantName;
    public static String isAdmin;
    public static String isTenantCreator;


    static {
        httpclient = new MyHttpClient();
        // env config
        System.out.println(envConf.getValue(ConfDefine.PUBLIC, ConfDefine.CONNECTIONTIMEOUT));
        connTimeout = Integer.parseInt(envConf.getValue(ConfDefine.PUBLIC, ConfDefine.CONNECTIONTIMEOUT));
        socketTimeout = Integer.parseInt(envConf.getValue(ConfDefine.PUBLIC, ConfDefine.SOCKETTIMEOUT));
        maxConnPerRoute = Integer.parseInt(envConf.getValue(ConfDefine.PUBLIC, ConfDefine.MAXCONNECTIONPERROUTE));
        maxConnTotal = Integer.parseInt(envConf.getValue(ConfDefine.PUBLIC, ConfDefine.MAXCONNECTIONTOTAL));

        uicurl = envConf.getValue(ConfDefine.UICSITE, ConfDefine.UICURL);
        ideurl = envConf.getValue(ConfDefine.IDESITE, ConfDefine.IDEURL);
        uicapiurl = envConf.getValue(ConfDefine.UICAPISITE, ConfDefine.UICAPIURL);
        consoleurl = envConf.getValue(ConfDefine.CONSOLESITE, ConfDefine.CONSOLEURL);

        useStaf = ("yes".equals(testConf.getValue(PUBLIC, USE_STAF))
                || "true".equals(testConf.getValue(PUBLIC, USE_STAF))) ? true : false;
        //mysql-ide
        ideDbUrl = envConf.getValue(ConfDefine.DATABASE, ConfDefine.IDE_DB_URL);
        ideDbUser = envConf.getValue(ConfDefine.DATABASE, ConfDefine.IDE_DB_USER);
        ideDbPwd = envConf.getValue(ConfDefine.DATABASE, ConfDefine.IDE_DB_PWD);
        //mysql-uic
        uicDbUrl = envConf.getValue(ConfDefine.DATABASE, ConfDefine.UIC_DB_URL);
        uicDbUser = envConf.getValue(ConfDefine.DATABASE, ConfDefine.UIC_DB_USER);
        uicDbPwd = envConf.getValue(ConfDefine.DATABASE, ConfDefine.UIC_DB_PWD);

        log.info("UIC URL:" + uicurl);
        log.info("IDE URL:" + ideurl);
        log.info("mysqlUrl:" + ideDbUrl);
        log.info("mysqlUser:" + ideDbUser);
        log.info("mysqlPwd:" + ideDbPwd);


        System.out.println(ConfDefine.PUBLIC + "      ，     " + ConfDefine.MYSQLWAITTIME);
        mysqlWaitTime = Integer.parseInt(envConf.getValue(ConfDefine.PUBLIC, ConfDefine.MYSQLWAITTIME));


        String chdb = testConf.getValue(ConfDefine.PUBLIC, ConfDefine.CHECKDB);
        String chmongo = testConf.getValue(ConfDefine.PUBLIC, ConfDefine.CHECKMONGO);
        checkdb = (chdb.equalsIgnoreCase("true") || chdb.equals("1")) ? true : false;
        checkmongo = (chmongo.equalsIgnoreCase("true") || chdb.equals("1")) ? true : false;


        defUicUsername = testConf.getValue(ConfDefine.UICINFO, ConfDefine.USERNAME);
        defUicPasswd = testConf.getValue(ConfDefine.UICINFO, ConfDefine.PASSWORD);
        defUicUserId = Long.valueOf(testConf.getValue(ConfDefine.UICINFO, ConfDefine.UICUSERID));
        defRdosUserId = Long.valueOf(testConf.getValue(ConfDefine.UICINFO, ConfDefine.RDOSUSERID));
        defTenantId = Long.valueOf(testConf.getValue(ConfDefine.UICINFO, ConfDefine.TENANTID));
        defRdosTenantId = Long.valueOf(testConf.getValue(ConfDefine.UICINFO, ConfDefine.RDOSTENANTID));
        defTenantName = testConf.getValue(ConfDefine.UICINFO, ConfDefine.TENANTNAME);
        isAdmin = testConf.getValue(ConfDefine.UICINFO, ConfDefine.ISADMIN).equalsIgnoreCase("true") ? "1" : "0";
        isTenantCreator = testConf.getValue(ConfDefine.UICINFO, ConfDefine.ISTENANTCREATOR.equalsIgnoreCase("true") ? "1" : "0");
    }

    /**
     * Initialize stafHandle
     */
    public BaseTest() {
        if (stafHandle == null && useStaf) {
            try {
                stafHandle = new STAFHandle("Base Handle");
            } catch (STAFException e) {
                fail(e.getMessage());
            }
        }
    }


    /**
     * @param hc
     * @param flag
     */
    protected static void onceLoginInSystem(MyHttpClient hc, Flag flag, String username, String password) {
        // httpclient
        Map<String, String> mvm = new HashMap<String, String>();
        HttpResult result = null;
        if (flag.equals(Flag.UIC)) {
            //result = hc.get(flag,IsLogin);//获取登陆时Token
            mvm.put("username", username);
            mvm.put("password", password);
            mvm.put("verify_code", "1");
            result = hc.post(flag, Login, mvm);
            System.out.println("result.getBody():" + result.getBody() + "----------" + "result.getCode():" + result.getCode());
            System.out.println("result.header():" + result.getHeader());
        }

        Assert.assertEquals(result.getCode(), HttpStatus.SC_OK, "登陆错误：" + result.getBody());
    }

    /**protected static void onceLogOutSystem(MyHttpClient hc, Flag flag) {
     HttpResult result = null;
     if (flag.equals(Flag.MANAGE)||flag.equals(Flag.OPS))
     result = hc.get(Flag.OPS, OPS_LOGOUT);
     else
     result = hc.get(flag, Logout);
     Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);
     }**/


    /**
     * Execute command by stafHandle on IP
     *
     * @param stafHandle execute by which stafHandle
     * @param ip         host IP
     * @param cmd        command that to execute
     * @return STAF execute result
     */
    public final STAFResult executeByStaf(STAFHandle stafHandle, String ip, String cmd) {
        STAFResult result = stafHandle.submit2(ip, "PROCESS",
                "START SHELL COMMAND " + STAFUtil.wrapData(cmd) + " WAIT RETURNSTDOUT STDERRTOSTDOUT");
        return result;
    }

    /**
     * Execute command on specified IP
     *
     * @param ip  host IP
     * @param cmd command to execute
     */
    public void executeCmd(String ip, String cmd) {
        STAFResult result = executeByStaf(stafHandle, ip, cmd);
        if (result.rc != 0) {
            fail(ip + " result.rc not 0 while execute cmd: " + cmd + " -- " + result.rc);
        }
    }

    /**
     * Execute command on specified IP, then return specified type object
     *
     * @param ip  host IP
     * @param cmd command to execute
     * @param cls returned value's class: Integer | Float | String
     * @return specified type object
     */
    public Object executeCmd(String ip, String cmd, Class<?> cls) {
        Object ret = null;
        STAFResult result = executeByStaf(stafHandle, ip, cmd);
        if (result.rc != 0) {
            log.fatal(ip + " result.rc not 0 while execute cmd: " + cmd + " -- " + result.rc);
        } else {
            String stdout = getStafOutput(result);
            try {
                if (cls.equals(Integer.class))
                    ret = new Integer(stdout.trim());
                else if (cls.equals(Float.class))
                    ret = new Float(stdout.trim());
                else if (cls.equals(String.class))
                    ret = stdout.trim();
                else
                    fail("cls not supported! " + cls.getName());
            } catch (Exception e) {
                fail("get verify exception: " + stdout);
                e.printStackTrace();
            }
        }
        return ret;
    }

    /**
     * Execute command with option, then return specified type object
     *
     * @param ip     host IP
     * @param cmd    command to execute
     * @param option command option
     * @param cls    returned value's class
     * @return specified type object
     */
    public Object executeCmd(String ip, String cmd, String option, Class<?> cls) {
        Object ret = null;
        STAFResult result = executeByStaf(stafHandle, ip, cmd + " " + option);
        if (result.rc != 0) {
            log.fatal(ip + " result.rc not 0 while execute cmd: " + cmd + " " + option + " -- " + result.rc);
        } else {
            String stdout = getStafOutput(result);
            try {
                if (cls.equals(Integer.class))
                    ret = new Integer(stdout.trim());
                else if (cls.equals(String.class))
                    ret = stdout.trim();
                else
                    fail("cls not supported! " + cls.getName());
            } catch (Exception e) {
                fail("get verify exception: " + stdout);
                e.printStackTrace();
            }
        }
        return ret;
    }

    /**
     * Get command return string executed by stafHandle
     *
     * @param result STAFResult type parameter
     * @return command return string
     */
    @SuppressWarnings("rawtypes")
    public final String getStafOutput(STAFResult result) {
        Map rstMap = (Map) result.resultObj;
        List rstList = (List) rstMap.get("fileList");
        Map stdoutMap = (Map) rstList.get(0);
        String stdout = (String) stdoutMap.get("data");
        return stdout;
    }

    /**
     * Interrupt assertion and print error message
     *
     * @param errorMassage error message
     */
    public static final void fail(String errorMassage) {
        log.fatal(errorMassage);
        Assert.assertTrue(false, errorMassage);
    }

    /**
     * Interrupt assertion and print error message
     */
    public static final void fail(Throwable e) {
        log.fatal(e.getMessage(), e);
        Assert.assertTrue(false, e.getMessage());
    }

    /**
     * Wait specified seconds
     *
     * @param second second count
     */
    public static final void waitto(long second) {
        log.info("wait for " + second + "s...");
        try {
            Thread.sleep(second * 1000);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    public static final void waitformillisecond(long minis) {
        try {
            Thread.sleep(minis);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    protected static String getFunctionName() {
        return Thread.currentThread().getStackTrace()[2].getMethodName();
    }

    protected void waitThreadFinish() {
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    protected String getRamdomDataASC(int size) {
        String ret = "";
        String str[] = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S",
                "T", "U", "V", "W", "X", "Y", "Z"};
        for (int i = 0; i < size; i++) {
            int index = new Random().nextInt(26);
            ret += str[index];
        }
        return ret;
    }

    /**
     * Generate a random ASCII string of a given length.
     */
    protected static String getASCIIString(int length) {
        int interval = '~' - ' ' + 1;

        byte[] buf = new byte[length];
        new Random().nextBytes(buf);
        for (int i = 0; i < length; i++) {
            if (buf[i] < 0) {
                buf[i] = (byte) ((-buf[i] % interval) + ' ');
            } else {
                buf[i] = (byte) ((buf[i] % interval) + ' ');
            }
        }
        return new String(buf);
    }

    public static char getRandomHan() {
        String str = "";
        int hightPos; //
        int lowPos;

        Random random = new Random();

        hightPos = (176 + Math.abs(random.nextInt(39)));
        lowPos = (161 + Math.abs(random.nextInt(93)));

        byte[] b = new byte[2];
        b[0] = (Integer.valueOf(hightPos)).byteValue();
        b[1] = (Integer.valueOf(lowPos)).byteValue();

        try {
            str = new String(b, "GBK");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            System.out.println("错误");
        }

        return str.charAt(0);
    }

    public String readToString(String fileName) {
        String encoding = "UTF-8";
        File file = new File(fileName);
        Long filelength = file.length();
        byte[] filecontent = new byte[filelength.intValue()];
        try {
            FileInputStream in = new FileInputStream(file);
            in.read(filecontent);
            in.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            return new String(filecontent, encoding);
        } catch (UnsupportedEncodingException e) {
            System.err.println("The OS does not support " + encoding);
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 转换int To Bool
     *
     * @param num
     * @return
     */
    public Boolean ChangeToBool(int num) {
        if (num == 1) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * 时间戳转换成日期格式字符串
     *
     * @return
     */
    public static String timeStamp2Date(String miniseconds, String format) {
        if (miniseconds == null || miniseconds.isEmpty() || miniseconds.equals("null")) {
            return "";
        }
        if (format == null || format.isEmpty()) format = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date(Long.valueOf(miniseconds)));
    }

    public static boolean getBoolean(int i) {
        boolean bo = false;
        if (i == 1) {
            bo = true;
        }
        if (i == 0) {
            bo = false;
        }
        return bo;
    }

    protected boolean IsArgsNull(String ar) {
        if (ar.equals("NULL"))
            return true;
        else
            return false;
    }

    /**
     * 把可变的参数列表转换成1个字符串
     * 传参orderId,orderValue,sn,snValue
     * 结果为orderId = orderValue and sn = snValue
     *
     * @param args
     */
    protected static String changeManyStringToOneStr(String... args) {
        String ps = "";
        int paramlength = args.length;
        if (paramlength % 2 != 0 || paramlength == 0) {
            log.error("wrong params");
        } else {
            for (int i = 0; i < paramlength; i++) {
                if (i % 2 == 0)
                    ps += "and " + args[i];
                if (i % 2 == 1)
                    ps += " = " + args[i] + " ";
            }
            ps = ps.substring(3);
        }
        return ps;
    }


}