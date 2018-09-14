package com.tijiantest.testcase.ops;



import java.util.Date;

import com.tijiantest.base.dbcheck.AccountChecker;
import com.tijiantest.base.dbcheck.PayChecker;
import com.tijiantest.model.account.Account;
import com.tijiantest.model.payment.trade.PayConstants;
import org.apache.http.Header;

import com.tijiantest.base.BaseTest;
import com.tijiantest.base.ConfDefine;
import com.tijiantest.base.Flag;
import com.tijiantest.base.MyHttpClient;



public class OpsBase extends BaseTest{
	
	public static String defusername;
	public static String defpasswd;
	public static int defSettHospitalId;
	public static String defcaiwuusername;
	public static String defcaiwupassword;
	public static MyHttpClient httpclient;
	public static String nowyear;
	public static int hospitalRecevieTradeAccountId;
	public static int defHospitalId;
	public static Account defAccount;
	public static Header[] hs = null;


	static {
		try {
			loadDefaultParams();
		} catch (Exception e) {
			e.printStackTrace();
		}
		httpclient = new MyHttpClient();
		onceLoginInSystem(httpclient, Flag.OPS, defusername, defpasswd);

		// 通过jvm进程的关闭钩子关闭共用的httpclient
		Runtime.getRuntime().addShutdownHook(new Thread() {
		  @Override
		  public void run() {
		  httpclient.shutdown();
				}
		});
	}
	private static void loadDefaultParams() throws Exception {
		defcaiwuusername = testConf.getValue(ConfDefine.OPSINFO, ConfDefine.CAIWUUSERNAME);
		defcaiwupassword = testConf.getValue(ConfDefine.OPSINFO, ConfDefine.CAIWUPASSWORD);
		defusername = testConf.getValue(ConfDefine.OPSINFO, ConfDefine.USERNAME);
		defpasswd = testConf.getValue(ConfDefine.OPSINFO, ConfDefine.PASSWORD);
		defAccount = AccountChecker.getOpsAccount(defusername);
		log.info("defusername"+defusername+defpasswd);
		nowyear = sd.format(new Date());
		defSettHospitalId = Integer.parseInt(testConf.getValue(ConfDefine.CRMINFO, ConfDefine.SETTLEHOSPITALID));
		defHospitalId = Integer.parseInt(testConf.getValue(ConfDefine.CRMINFO, ConfDefine.HOSTPITALID));

		hospitalRecevieTradeAccountId = PayChecker.getSuitableReceiveMethodId(defHospitalId, PayConstants.PayMethodBit.BalanceBit);

		
	}
}
