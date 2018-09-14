package com.tijiantest.testcase.main;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.tijiantest.base.*;
import org.apache.http.Header;

import com.tijiantest.base.dbcheck.AccountChecker;
import com.tijiantest.base.dbcheck.HospitalChecker;
import com.tijiantest.model.account.User;
import com.tijiantest.model.hospital.Hospital;
import com.tijiantest.model.hospital.HospitalParam;
import com.tijiantest.model.hospital.HospitalPeriodSetting;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.testng.Assert;

public class MainBaseNoLogin extends BaseTest{

	public static String defusername;
	public static String defpasswd;
	public static User defUser;
	public static int defaccountId; 
    public static int defHospitalId;
    public static HospitalPeriodSetting defhospitalPeriod;
    public static Hospital defHospital;
    public static String defSite; 
	public static List<MyHttpClient> hcList = new ArrayList<MyHttpClient>();
	public static MyHttpClient hc1 = null;
	public static MyHttpClient hc2 = null;
	public static MyHttpClient hc3 = null;
	public static MyHttpClient hc4 = null;
	public static int httpclientSize = 4; //可以调节需要的httpclient个数
	public static boolean isSmartRecommend;
	
	public static Header[] hs = null;
	public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	public static SimpleDateFormat simplehms = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
    public static String defaulturl = envConf.getValue(ConfDefine.MAINSITE, ConfDefine.MAINURL);
	

	static{
		loadDefaultParams();
		for(int i=0;i<httpclientSize;i++){
			hcList.add(new MyHttpClient());
			
		}
		hc1 = hcList.get(0);
		hc2 = hcList.get(1);
		hc3 = hcList.get(2);
		hc4 = hcList.get(3);
		
		
		//通过jvm进程的关闭钩子关闭共用的httpclient
		Runtime.getRuntime().addShutdownHook(new Thread(){
			@Override
			public void run() {
				for(MyHttpClient h:hcList)
					h.shutdown();
			}
		});
	}

	private static void loadDefaultParams() {
		defusername = testConf.getValue(ConfDefine.MAININFO, ConfDefine.USERNAME);
		defpasswd = testConf.getValue(ConfDefine.MAININFO, ConfDefine.PASSWORD);
		defUser = AccountChecker.getUserInfo(defusername);
		defaccountId = defUser.getAccount_id();
		defHospitalId = Integer.parseInt(testConf.getValue(ConfDefine.MAININFO, ConfDefine.HOSTPITALID));
		List<HospitalPeriodSetting> hospitalPeriodSettings = HospitalChecker.getHospitalPeriodSettings(defHospitalId);
		defhospitalPeriod = hospitalPeriodSettings.get(0);
		defHospital = HospitalChecker.getHospitalById(defHospitalId);
		//智能推荐
		Map<String,Object> hosSyncs = HospitalChecker.getHospitalSetting(defHospitalId,HospitalParam.IS_SMART_RECOMMEND);
		Integer smart_recommend = Integer.parseInt(hosSyncs.get(HospitalParam.IS_SMART_RECOMMEND).toString());
		isSmartRecommend = smart_recommend==1?true:false;
		//站点
		defSite = HospitalChecker.getSiteByHospitalId(defHospitalId).getUrl();
	}

	/**
	 * 在免登陆下单之前校验是否免登陆环境
	 * @param httpClient
	 * @param site
	 */
	public static void  checkNoLoginEnv(MyHttpClient httpClient,String site){
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		NameValuePair _site = new BasicNameValuePair("_site",site);
		NameValuePair _siteType = new BasicNameValuePair("_siteType", "mobile");
		params.add(_site);
		params.add(_siteType);
		HttpResult result = httpClient.get(Flag.MAIN,ValidateLoginAddToken,params);
		log.info("登陆验证 ..."+result.getBody());
		Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);
		if(result.getBody().contains("true")){
			onceLogOutSystem(httpClient,Flag.MAIN);
			result = hc4.get(Flag.MAIN,ValidateLoginAddToken,params);
			log.info("退出登录，再次登陆验证 ..."+result.getBody());
			Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);
			Assert.assertTrue(result.getBody().contains("false"));
		}
	}
}