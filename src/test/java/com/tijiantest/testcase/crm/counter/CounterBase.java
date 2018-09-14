package com.tijiantest.testcase.crm.counter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.http.Header;

import com.tijiantest.base.BaseTest;
import com.tijiantest.base.ConfDefine;
import com.tijiantest.base.Flag;
import com.tijiantest.base.MyHttpClient;
import com.tijiantest.base.dbcheck.AccountChecker;
import com.tijiantest.base.dbcheck.CompanyChecker;
import com.tijiantest.base.dbcheck.HospitalChecker;
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

public class CounterBase extends BaseTest{

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
	// 是否开启单位同步
	public static boolean isOpenCompanySync;
	// 是否开启套餐同步
	public static boolean isOpenMealSync;


	public static MyHttpClient httpclient;
	public static int hospitalRecevieTradeAccountId;

	public static Header[] hs = null;


	
	static {
		loadDefaultParams();
		httpclient = new MyHttpClient();
		onceLoginInSystem(httpclient, Flag.CRM, defCrmUsername, defCrmPasswd);
		
		// 通过jvm进程的关闭钩子关闭共用的httpclient
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				httpclient.shutdown();
			}
		});
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
		
		//CRM医院的默认收款人
		try {
			hospitalRecevieTradeAccountId = PayChecker.getSuitableReceiveMethodId(defhospital.getId(), PayConstants.PayMethodBit.BalanceBit);
		} catch (Exception e) {
			e.printStackTrace();
		}
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

}
