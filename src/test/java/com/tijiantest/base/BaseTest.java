package com.tijiantest.base;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CountDownLatch;

import com.tijiantest.base.dbcheck.HospitalChecker;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;
import org.testng.Assert;

import com.alibaba.fastjson.JSON;
import com.ibm.staf.STAFException;
import com.ibm.staf.STAFHandle;
import com.ibm.staf.STAFResult;
import com.ibm.staf.STAFUtil;
import com.tijiantest.base.dbcheck.AccountChecker;
import com.tijiantest.model.account.SystemTypeEnum;
import com.tijiantest.model.channel.UserVo;
import com.tijiantest.model.order.Order;
import com.tijiantest.util.ConfParser;



public class BaseTest implements ConfDefine,ActionsDefine{

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
	public static int mongoWaitTime;
	public static boolean mediatorAgentStart;
	public static String crmurl;
	public static String mainurl;
	public static String manageurl;
	public static String channelurl;
	public static String opsurl;
	public static String mysqlUrl;
	public static String mysqlUser;
	public static String mysqlPwd;
	public static String mongoReplset;
	public static String mongoDb;
	public static String mongoSnapShotDb;
	public static String defaulturl;
	public static boolean useStaf;
	public static boolean openSharding;
	public static boolean checkCompanyOpLog;
	public static boolean checkCompanySync;
	public static String examreportDbUrl;
	public static String examreportDbUser;
	public static String examreportDbPwd;
	public static String mainSecond;
	public static int defReceiveTradeAccountId;
	public static SimpleDateFormat sd = new SimpleDateFormat("yyyy");
	public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	public static SimpleDateFormat simplehms = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public static SimpleDateFormat cstFormater = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);
	public static SimpleDateFormat gmtFormater = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
	public static boolean checkdb;
	public static boolean checkmongo;
	public static String redisHost;
	public static String redisPort;
	public static String redisPasswd;
	public static String redisTimeout;

	//user & password
	public static String defCrmUsername;
	public static String defCrmPasswd;
	public static String defMainUsername;
	public static String defMainPasswd;
	public static String defManagerUsername;
	public static String defManagerPasswd;
	public static String defChannelUsername;
	public static String defChannelPasswd;
	public static int defChannelid;
	public static String defChannelname;
	public static String defPlatUsername;
	public static String defPlatPasswd;
	public static int defPlatAccountId;
	public final static ConfParser baseconf = new ConfParser("./csv/base/base.conf");
	
	public static String klovMongoHost;
	public static int klovMongoPort;
	public static String klovUrl;
	public static String buildName;

	
	static {
		// env config
		connTimeout = Integer.parseInt(envConf.getValue(ConfDefine.PUBLIC, ConfDefine.CONNECTIONTIMEOUT));
		socketTimeout = Integer.parseInt(envConf.getValue(ConfDefine.PUBLIC, ConfDefine.SOCKETTIMEOUT));
		maxConnPerRoute = Integer.parseInt(envConf.getValue(ConfDefine.PUBLIC, ConfDefine.MAXCONNECTIONPERROUTE));
		maxConnTotal = Integer.parseInt(envConf.getValue(ConfDefine.PUBLIC, ConfDefine.MAXCONNECTIONTOTAL));
		mediatorAgentStart = ("yes".equals(envConf.getValue(PUBLIC, MEDIATORAGENTSTART))
				|| "true".equals(envConf.getValue(PUBLIC, MEDIATORAGENTSTART))) ? true : false;

		crmurl = envConf.getValue(ConfDefine.CRMSITE, ConfDefine.CRMURL);
		mainurl = envConf.getValue(ConfDefine.MAINSITE, ConfDefine.MAINURL);

		mysqlUrl = envConf.getValue(ConfDefine.DATABASE, ConfDefine.MYSQL_URL);
		mysqlUser = envConf.getValue(ConfDefine.DATABASE, ConfDefine.MYSQL_USER);
		mysqlPwd = envConf.getValue(ConfDefine.DATABASE, ConfDefine.MYSQL_PWD);

		mongoReplset = envConf.getValue(ConfDefine.MONGO, ConfDefine.REPLSET);
		mongoDb = envConf.getValue(ConfDefine.MONGO, ConfDefine.MONGO_DB);
		mongoSnapShotDb = envConf.getValue(ConfDefine.MONGO, ConfDefine.MONGO_DB);
		defaulturl = envConf.getValue(ConfDefine.CRMSITE, ConfDefine.CRMURL);
		mainurl = envConf.getValue(ConfDefine.MAINSITE, ConfDefine.MAINURL);
		manageurl = envConf.getValue(ConfDefine.MANAGESITE, ConfDefine.MANAGEURL);
		channelurl = envConf.getValue(ConfDefine.CHANNELSITE, ConfDefine.CHANNELURL);
		opsurl = envConf.getValue(ConfDefine.OPSSITE, ConfDefine.OPSURL);

		openSharding = ("yes".equals(envConf.getValue(PUBLIC, OPENSHARDING))
				|| "true".equals(envConf.getValue(PUBLIC, OPENSHARDING))) ? true : false;
		examreportDbUrl = envConf.getValue(ConfDefine.DATABASE, ConfDefine.EXAMREPORTDB_URL);
		examreportDbUser = envConf.getValue(ConfDefine.DATABASE, ConfDefine.EXAMREPORTDB_USER);
		examreportDbPwd = envConf.getValue(ConfDefine.DATABASE, ConfDefine.EXAMREPORTDB_PWD);
		
		klovMongoHost = envConf.getValue(ConfDefine.KLOV, ConfDefine.REPORT_MONGO_HOST);
		klovMongoPort = Integer.valueOf(envConf.getValue(ConfDefine.KLOV, ConfDefine.REPORT_MONGO_PORT));
		klovUrl = envConf.getValue(ConfDefine.KLOV, ConfDefine.KLOV_URL);
		buildName = envConf.getValue(ConfDefine.KLOV, ConfDefine.BUILD_NAME);

		redisHost = envConf.getValue(ConfDefine.REDIS, ConfDefine.REDIS_HOST);
		redisPort = envConf.getValue(ConfDefine.REDIS, ConfDefine.REDIS_PORT);
		redisPasswd = envConf.getValue(ConfDefine.REDIS, ConfDefine.REDIS_PASSWORD);
		redisTimeout = envConf.getValue(ConfDefine.REDIS, ConfDefine.REDIS_TIMEOUT);

		log.info("mediator-agent启动状态:"+mediatorAgentStart);
		log.info("CRM URL:" + crmurl);
		log.info("MAIN URL:" + mainurl);
		log.info("mysqlUrl:" + mysqlUrl);
		log.info("mysqlUser:" + mysqlUser);
		log.info("mysqlPwd:" + mysqlPwd);
		log.info("mongoReplset:" + mongoReplset);
		log.info("mongoConnectionDb:" + mongoDb);
		log.info("redisServer:" + redisHost+":"+redisPort);
		log.info("redisPassword:" + redisPasswd);
		log.info("examreportDbUrl:" + examreportDbUrl);
		log.info("examreportDbUser:" + examreportDbUser);
		log.info("examreportDbPwd:" + examreportDbPwd);

		// test config
		useStaf = ("yes".equals(testConf.getValue(PUBLIC, USE_STAF))
				|| "true".equals(testConf.getValue(PUBLIC, USE_STAF))) ? true : false;
		mainSecond = testConf.getValue(ConfDefine.SURVEYINFO, ConfDefine.MAIN_SECOND);

		mysqlWaitTime = Integer.parseInt(envConf.getValue(ConfDefine.PUBLIC, ConfDefine.MYSQLWAITTIME));
		mongoWaitTime = Integer.parseInt(envConf.getValue(ConfDefine.PUBLIC, ConfDefine.MONGOWAITTIME));

		checkCompanyOpLog = ("yes".equals(envConf.getValue(PUBLIC, CheckCompanyOpLog))
				|| "true".equals(envConf.getValue(PUBLIC, CheckCompanyOpLog))) ? true : false;
		checkCompanySync = ("yes".equals(envConf.getValue(PUBLIC, CheckCompanySync))
				|| "true".equals(envConf.getValue(PUBLIC, CheckCompanySync))) ? true : false;
		
		String chdb = testConf.getValue(ConfDefine.PUBLIC, ConfDefine.CHECKDB);
		String chmongo = testConf.getValue(ConfDefine.PUBLIC, ConfDefine.CHECKMONGO);
		checkdb = (chdb.equalsIgnoreCase("true") || chdb.equals("1")) ? true : false;
		checkmongo = (chmongo.equalsIgnoreCase("true") || chdb.equals("1")) ? true : false;
		
		
		defCrmUsername = testConf.getValue(ConfDefine.CRMINFO, ConfDefine.USERNAME);
		defCrmPasswd = testConf.getValue(ConfDefine.CRMINFO, ConfDefine.PASSWORD);
		defManagerUsername = testConf.getValue(ConfDefine.MANAGEINFO, ConfDefine.USERNAME);
		defManagerPasswd = testConf.getValue(ConfDefine.MANAGEINFO, ConfDefine.PASSWORD);
		defMainUsername = testConf.getValue(ConfDefine.MAININFO, ConfDefine.USERNAME);
		defMainPasswd = testConf.getValue(ConfDefine.MAININFO, ConfDefine.PASSWORD);
		defPlatUsername = testConf.getValue(ConfDefine.CRMINFO, ConfDefine.PLATUSERNAME);
		defPlatPasswd = testConf.getValue(ConfDefine.CRMINFO, ConfDefine.PLATUSERPASSWORD);
		defPlatAccountId = AccountChecker.getUserInfo(defPlatUsername,SystemTypeEnum.CRM_LOGIN.getCode()).getAccount_id();

		//默认渠道
		defChannelUsername = testConf.getValue(ConfDefine.CHANNELINFO, ConfDefine.USERNAME);
		defChannelPasswd = testConf.getValue(ConfDefine.CHANNELINFO, ConfDefine.PASSWORD);
		defChannelid = HospitalChecker.getChannelInfo(defChannelUsername).getId();
		defChannelname = testConf.getValue(ConfDefine.CHANNELINFO, ConfDefine.CHANNELNAME);
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
	 * 
	 * @param hc
	 * @param flag
	 *
	 */
	protected static void onceLoginInSystem(MyHttpClient hc, Flag flag,String username,String password) {
		// httpclient
		Map<String, String> mvm = new HashMap<String, String>();
		HttpResult result = null;
		if (flag.equals(Flag.CRM)) {
			result = hc.get(flag,IsLogin);//获取登陆时Token
			mvm.put("username", username);
			mvm.put("password", password);
			mvm.put("rememberMe", "false");
			result = hc.post(flag, Login, mvm);
		}
		if (flag.equals(Flag.MANAGE)) {
			mvm.put("loginName", username);
			mvm.put("password", password);
			mvm.put("validationCode", "1111");
			mvm.put("rememberMe", "false");
			mvm.put("rememberMeFive", "false");
			result = hc.post(Flag.OPS, OPS_LOGIN, mvm);
			waitto(1);
			result = hc.get(Flag.MANAGE, Manage_AccountInfo);
			log.info("result..." + result.getBody());
		}
		if (flag.equals(Flag.MAIN)) {
			result = hc.get(flag,Account_Profile);//获取登陆时Token
			List<NameValuePair> params = new ArrayList<>();
			params.add(new BasicNameValuePair("_site","mt"));
			params.add(new BasicNameValuePair("_p",""));
			params.add(new BasicNameValuePair("_siteType","mobile"));
			result = hc.get(flag,Account_GetArticleList,params);//获取登陆时Token
			System.out.println("XAGG:"+result.getHeader().get(X_Auth_Mytijian_Token));
			mvm.put("_site", "mt");
			mvm.put("_p", "");
			mvm.put("_siteType", "mobile");
			mvm.put("username", username);
			mvm.put("password", password);
			mvm.put("rememberMe", "false");
			mvm.put("validationCode", "");
			mvm.put("callbackurl", "");
			result = hc.post(flag, Login, mvm);
		}
		if(flag.equals(Flag.OPS)){
			mvm.put("loginName", username);
			mvm.put("password", password);
			mvm.put("validationCode", "1111");
			mvm.put("rememberMe", "false");
			mvm.put("rememberMeFive", "false");
			log.info("mvm:" + mvm);
			result = hc.post(Flag.OPS, OPS_LOGIN, mvm);
			Assert.assertEquals(result.getCode(), HttpStatus.SC_OK, "登陆错误："+result.getBody());
			waitto(1);
			result = hc.get(Flag.OPS, OPS_USER);
//			log.info("result..." + result.getBody());
//			result = hc.get(Flag.MANAGE, Manage_AccountInfo);
//			log.info("result..." + result.getBody());
		}
		if (flag.equals(Flag.CHANNEL)) {
			/*UserVo userVo = new UserVo();
			userVo.setUserName(username);
			userVo.setPassword(password);
			userVo.setValidationCode("");
			result = hc.post(flag, Login, JSON.toJSONString(userVo));*/
			List<NameValuePair>params = new ArrayList<>();
			NameValuePair pa1 = new BasicNameValuePair("username",username);
			NameValuePair pa2 = new BasicNameValuePair("password",password);
			NameValuePair pa3 = new BasicNameValuePair("validationCode","");
			NameValuePair pa4 = new BasicNameValuePair("callbackurl","");
			NameValuePair pa5 = new BasicNameValuePair("rememberMe","false");
			params.add(pa1);params.add(pa2);params.add(pa3);params.add(pa4);params.add(pa5);
			result = hc.post(flag, Login, params);
		}
		Assert.assertEquals(result.getCode(), HttpStatus.SC_OK, "登陆错误："+result.getBody());
	}

	protected static void onceLogOutSystem(MyHttpClient hc, Flag flag) {
		HttpResult result = null;
		if (flag.equals(Flag.MANAGE)||flag.equals(Flag.OPS))
			result = hc.get(Flag.OPS, OPS_LOGOUT);
		else
			result = hc.get(flag, Logout);
		Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);
	}
	

	/**
	 * Execute command by stafHandle on IP
	 * 
	 * @param stafHandle
	 *            execute by which stafHandle
	 * @param ip
	 *            host IP
	 * @param cmd
	 *            command that to execute
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
	 * @param ip
	 *            host IP
	 * @param cmd
	 *            command to execute
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
	 * @param ip
	 *            host IP
	 * @param cmd
	 *            command to execute
	 * @param cls
	 *            returned value's class: Integer | Float | String
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
	 * @param ip
	 *            host IP
	 * @param cmd
	 *            command to execute
	 * @param option
	 *            command option
	 * @param cls
	 *            returned value's class
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
	 * @param result
	 *            STAFResult type parameter
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
	 * @param errorMassage
	 *            error message
	 */
	public static final void fail(String errorMassage) {
		log.fatal(errorMassage);
		Assert.assertTrue(false, errorMassage);
	}

	/**
	 * Interrupt assertion and print error message
	 * 
	 * @param errorMassage
	 *            error message
	 */
	public static final void fail(Throwable e) {
		log.fatal(e.getMessage(), e);
		Assert.assertTrue(false, e.getMessage());
	}

	/**
	 * Wait specified seconds
	 * 
	 * @param second
	 *            second count
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
		String str[] = { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S",
				"T", "U", "V", "W", "X", "Y", "Z" };
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


	/*********** public functions *******/
	/**
	 * 订单排序，升序
	 */
	public void SortOrderById(List<Order> list) {
		Collections.sort(list, new Comparator<Order>() {
			@Override
			public int compare(Order o1, Order o2) {
				return o1.getId() - o2.getId();
			}
		});
	}

	/** 
     * 时间戳转换成日期格式字符串 
     * @param seconds 精确到秒的字符串 
     * @param formatStr 
     * @return 
     */  
    public static String timeStamp2Date(String miniseconds,String format) {  
        if(miniseconds == null || miniseconds.isEmpty() || miniseconds.equals("null")){  
            return "";  
        }  
        if(format == null || format.isEmpty()) format = "yyyy-MM-dd";  
        SimpleDateFormat sdf = new SimpleDateFormat(format);  
        return sdf.format(new Date(Long.valueOf(miniseconds)));  
    }  
    
    public static boolean getBoolean(int i){
		boolean bo = false;
		if(i==1){
			bo=true;
		}
		if(i==0){
			bo=false;
		}
		return bo;
	}
    
	protected boolean IsArgsNull(String ar){
		if(ar.equals("NULL"))
			return true;
		else 
			return false;
	}
	
	/**
	 * 把可变的参数列表转换成1个字符串
	 * 传参orderId,orderValue,sn,snValue
	 * 结果为orderId = orderValue and sn = snValue
	 * @param args
	 */
	protected static String changeManyStringToOneStr(String ...args){
		String ps = "";
		int paramlength =args.length;
		if(paramlength%2 != 0 || paramlength == 0){
			log.error("wrong params");
		}else{
		for(int i =0;i<paramlength;i++){
			if(i%2 == 0)
				ps += "and "+args[i];
			if(i%2 == 1)
				ps += " = "+args[i]+" ";
		}
		ps = ps.substring(3);
		}
		return ps;
	}

	
}