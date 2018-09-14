package com.tijiantest.testcase.crm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import org.apache.http.Header;
import org.apache.http.HttpStatus;

import com.beust.jcommander.internal.Lists;
import com.tijiantest.base.BaseTest;
import com.tijiantest.base.ConfDefine;
import com.tijiantest.base.Flag;
import com.tijiantest.base.HttpResult;
import com.tijiantest.base.MyHttpClient;
import com.tijiantest.base.dbcheck.AccountChecker;
import com.tijiantest.base.dbcheck.CompanyChecker;
import com.tijiantest.base.dbcheck.HospitalChecker;
import com.tijiantest.base.dbcheck.OrderChecker;
import com.tijiantest.base.dbcheck.PayChecker;
import com.tijiantest.base.dbcheck.ResourceChecker;
import com.tijiantest.model.account.SystemTypeEnum;
import com.tijiantest.model.account.User;
import com.tijiantest.model.company.HospitalCompany;
import com.tijiantest.model.company.HospitalGuestCompanyEnum;
import com.tijiantest.model.hospital.Hospital;
import com.tijiantest.model.hospital.HospitalParam;
import com.tijiantest.model.hospital.HospitalPeriodSetting;
import com.tijiantest.model.hospital.HospitalSettings;
import com.tijiantest.model.payment.trade.PayConstants;
import com.tijiantest.util.db.DBMapper;
import com.tijiantest.util.db.SqlException;

public class CrmBase extends BaseTest {
	public static User defUser;
	public static int defaccountId;
	public static HospitalCompany defnewcompany;

	public static HospitalCompany defSKXCnewcompany;//缺省散客现场新单位
	public static HospitalCompany defWSGRnewcompany;//缺省网上个人新单位
	public static HospitalCompany defMTJKnewcompany;//缺省每天健康新单位

	public static HospitalPeriodSetting defhospitalPeriod;
	public static Hospital defhospital;
	public static Integer defbasicMealId;
	public static String defPlatUsername;
	public static String defPlatPasswd;
	public static int defPlatAccountId;
	public static String defCustomer;
	public static int defCustomerId;
	//深对接体检中心用户密码
	public static Integer defDeepHosptailId;
	public static String defDeepUsername;
	public static String defDeepPaswd;
	// 是否开启单位同步
	public static boolean isOpenCompanySync;
	// 是否开启套餐同步
	public static boolean isOpenMealSync;


	public static MyHttpClient httpclient;
	public static int hospitalRecevieTradeAccountId;

	public static Header[] hs = null;
	private static List<CrmLifeCycleListener> listeners = new ArrayList<CrmLifeCycleListener>();
	public static int hidePriceHospitalId;
	public static String hidePriceUsername;
	public static String hidePricePasswd;

	
	static {
		loadDefaultParams();
		httpclient = new MyHttpClient();
		onceLoginInSystem(httpclient, Flag.CRM, defCrmUsername, defCrmPasswd);

		// 通过jvm进程的关闭钩子关闭共用的httpclient
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				for (CrmLifeCycleListener listener : listeners)
					listener.beforeShutdown();
				httpclient.shutdown();
			}
		});
	}

	public static void addListeners(CrmLifeCycleListener listener) {
		if (listener != null)
			listeners.add(listener);
	}

	private static void loadDefaultParams() {
		defUser = AccountChecker.getUserInfo(defCrmUsername,SystemTypeEnum.CRM_LOGIN.getCode());
		defCustomer = testConf.getValue(ConfDefine.CRMINFO, ConfDefine.CUSTOMER);
		defCustomerId = AccountChecker.getUserInfo(defCustomer,SystemTypeEnum.C_LOGIN.getCode()).getAccount_id();
		defaccountId = defUser.getAccount_id();

		int companyid = Integer.parseInt(testConf.getValue(ConfDefine.CRMINFO, ConfDefine.COMPANYID));

		int hospitalid = Integer.parseInt(testConf.getValue(ConfDefine.CRMINFO, ConfDefine.HOSTPITALID));
		System.out.println("医院ID:"+ hospitalid);
		String hospitalname = testConf.getValue(ConfDefine.CRMINFO, ConfDefine.HOSPITALNAME);
		defbasicMealId = ResourceChecker.getBasicMealId(hospitalid);
		HospitalSettings defhospitalSetting = new HospitalSettings(defbasicMealId);
		defhospital = new Hospital(hospitalid, hospitalname, defhospitalSetting);
		List<HospitalPeriodSetting> hospitalPeriodSettings = HospitalChecker.getHospitalPeriodSettings(defhospital.getId());
		defhospitalPeriod = hospitalPeriodSettings.get(0);

		defPlatUsername = testConf.getValue(ConfDefine.CRMINFO, ConfDefine.PLATUSERNAME);
		defPlatPasswd = testConf.getValue(ConfDefine.CRMINFO, ConfDefine.PLATUSERPASSWORD);
		defPlatAccountId = AccountChecker.getUserInfo(defPlatUsername,SystemTypeEnum.CRM_LOGIN.getCode()).getAccount_id();


		
		defnewcompany = CompanyChecker.getHCompanyByOldCompanyIdANDOrgId(companyid, defhospital.getId());
		defSKXCnewcompany = CompanyChecker.getHospitalCompanyByPlatCompanyIdANDOrganizationId(
				HospitalGuestCompanyEnum.HOSPITAL_GUEST_OFFLINE.getPlatformCompanyId()
				,hospitalid);
		System.out.println("hospitalid="+ hospitalid+"      defSKXCnewcompany.ID = " + defSKXCnewcompany.getId());
		defWSGRnewcompany = CompanyChecker.getHospitalCompanyByPlatCompanyIdANDOrganizationId(
				HospitalGuestCompanyEnum.HOSPITAL_GUEST_ONLINE.getPlatformCompanyId(),
				hospitalid);
		defMTJKnewcompany = CompanyChecker.getHospitalCompanyByPlatCompanyIdANDOrganizationId(
				HospitalGuestCompanyEnum.HOSPITAL_MTJK.getPlatformCompanyId()
				,hospitalid);
		//读取体检中心单位同步/套餐同步配置
		Map<String,Object> hosSyncs = HospitalChecker.getHospitalSetting(defhospital.getId(),HospitalParam.OPEN_SYNC_COMPANY,HospitalParam.OPEN_SYNC_MEAL);
		Integer sync_company = Integer.parseInt(hosSyncs.get(HospitalParam.OPEN_SYNC_COMPANY).toString());
		Integer sync_meal = Integer.parseInt(hosSyncs.get(HospitalParam.OPEN_SYNC_MEAL).toString());
		isOpenCompanySync = sync_company == 1 ? true : false;
		isOpenMealSync = sync_meal == 1 ? true : false;

		//北京航空总医院特殊处理
		hidePriceHospitalId = Integer.parseInt(testConf.getValue(ConfDefine.CRMINFO, ConfDefine.HIDE_PRICE_HOSPITAL_SPEL));
		hidePriceUsername = testConf.getValue(ConfDefine.CRMINFO, ConfDefine.HIDE_PPRICE_MANAGER_USERNAME);
		hidePricePasswd  = testConf.getValue(ConfDefine.CRMINFO, ConfDefine.HIDE_PPRICE_MANAGER_PASSWD);

		//CRM医院的默认收款人
		try {
			hospitalRecevieTradeAccountId = PayChecker.getSuitableReceiveMethodId(defhospital.getId(), PayConstants.PayMethodBit.BalanceBit);
		} catch (Exception e) {
			e.printStackTrace();
		}

		defDeepHosptailId  = Integer.parseInt(testConf.getValue(ConfDefine.CRMINFO, ConfDefine.DeepHospitalId));
		defDeepUsername = testConf.getValue(ConfDefine.CRMINFO, ConfDefine.DeepUsername);
		defDeepPaswd = testConf.getValue(ConfDefine.CRMINFO, ConfDefine.DeepPasswd);
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

	/***
	 * list随机取n个值
	 * 
	 * @param list
	 * @param n
	 * @return
	 */
	public static <T> List<T> randomList(List<T> list, int n) {
		List<T> Tlist = list;
		List<T> Ts = new ArrayList<T>();
		if (Tlist.size() > 0) {
			if (n <= Tlist.size()) {
				for (int i = 0; i < n; i++) {
					Random rd = new Random();
					int k = rd.nextInt(Tlist.size() - 1);
					System.out.println("K:" + k);
					T t = Tlist.get(k);
					Ts.add(t);
					Tlist.remove(k);
				}
				return Ts;
			}
			System.out.println("n大于size，不正确");
			return null;
		}
		return null;
	}




	/************************* 操 作 函 数 ***********************/

	
	
	/**
	 * 创建订单
	 * 
	 * @param hc
	 * @param mealId
	 * @param accountId
	 * @param companyId
	 * @param examdate
	 * @return
	 * @throws SqlException 
	 */
	
	
	protected static void clearData() {
		/**
		 * 清理套餐
		 */
		System.out.println("*****清理套餐*****");
		Integer basicMealId = ResourceChecker.getBasicMealId(defhospital.getId());
		try {
			DBMapper.update("UPDATE tb_meal set `disable` = 2 " + "WHERE hospital_id = " + defhospital.getId()
					+ " AND id <> " + basicMealId + " " + "AND (name like '%copyMeal%' " + "OR name LIKE '%autotest%' "
					+ "OR name like '%MCNS套餐%'  " + "OR NAME LIKE '散客manage测试%'  "
					+ "OR name like '%ceshiMealAABBCCDD%'  " + "OR name like '%银泰百货员工套餐(2016)%'  "
					+ "OR name like '%自动化测试%'  " + "OR name like '超级套餐' );");
		} catch (SqlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		waitto(2);

		/**
		 * 清理加项包
		 */
		System.out.println("*****清理加项包*****");
		try {
			DBMapper.update("UPDATE tb_examitem_package set `disable` = 2 " + "WHERE name like '加项包%' "
					+ "OR name LIKE 'testPKG%' " + "AND hospital_id = " + defhospital.getId() + ";");
		} catch (SqlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		waitto(2);

		/**
		 * 清理订单 杭疗 体检日期为2018-12-01至2018-12-31
		 */
		System.out.println("*****清理订单*****");
		String nowday = sdf.format(new Date());
		// 获取垃圾订单
		String sqlStr = "SELECT * FROM tb_order " + "WHERE hospital_id = " + defhospital.getId()
				+ " AND STATUS IN (0, 2, 11) AND source = 3 "
				+ "AND (( exam_date BETWEEN '2018-12-01' AND '2018-12-31' ) "
				+ "OR ( exam_date BETWEEN '2018-03-03' AND '2018-03-04' ) " + "or exam_date = \"" + nowday + "\") "
				+ "ORDER BY insert_time DESC";
		System.out.println(sqlStr);
		List<Map<String, Object>> list = null;
		try {
			list = DBMapper.query(sqlStr);
		} catch (SqlException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		List<Integer> successList = Lists.newArrayList();
		if(list.size()>0){
			for(Map<String,Object> map : list){
				successList.add(Integer.valueOf(map.get("id").toString()));
			}
			try {
				 OrderChecker.Run_CrmOrderRevokeOrder(httpclient, successList, false, true, true);
			} catch (SqlException e) {
				e.printStackTrace();
			}
		}
	}



	/**
	 * 获取GMT时间
	 * 
	 * @param obj
	 * @return
	 * @throws ParseException
	 */
	@SuppressWarnings("deprecation")
	public static String getGMTDateString(Object obj) throws ParseException {
		String s = obj.toString();
		if (s.contains("UTC")) {
			// sdf.setTimeZone(TimeZone.getTimeZone(envConf.getValue(ConfDefine.PUBLIC,ConfDefine.TIMEZONE)));
			return sdf.format(new Date(s));
		} else if (s.contains("CST"))
			return sdf.format(new Date(s));
		else if (s.contains("GMT")) {
			SimpleDateFormat sf = new SimpleDateFormat("EEE MMM dd hh:mm:ss z yyyy", Locale.ENGLISH);
			return sdf.format(sf.parse(s));
		} else
			return sdf.format(obj);
	}

	


	public Boolean checkResult(HttpResult result) {
		Boolean isPass = false;
		if (result.getCode() == HttpStatus.SC_OK)
			isPass = true;
		return isPass;
	}

	
	
	
	
	
	
	
	
}