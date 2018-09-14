package com.tijiantest.testcase.crm.settlement;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.tijiantest.base.BaseTest;
import com.tijiantest.base.ConfDefine;
import com.tijiantest.base.Flag;
import com.tijiantest.base.MyHttpClient;
import com.tijiantest.base.dbcheck.AccountChecker;
import com.tijiantest.base.dbcheck.HospitalChecker;
import com.tijiantest.base.dbcheck.PayChecker;
import com.tijiantest.model.account.SystemTypeEnum;
import com.tijiantest.model.hospital.Hospital;
import com.tijiantest.model.hospital.HospitalParam;
import com.tijiantest.model.hospital.HospitalPeriodSetting;
import com.tijiantest.model.payment.trade.PayConstants;
import com.tijiantest.util.db.DBMapper;
import com.tijiantest.util.db.SqlException;

public class SettleBase extends BaseTest{
	protected  static String settle_time = null;
	public static String defSettUsername;
	public static String defSettPasswd;
	public static int defSettAccountId;
	public static int defSettHospitalId;
	public static Hospital defSettHospital;
	public static MyHttpClient httpclient;
	public static int hospitalRecevieTradeAccountId;

	
	static{
		loadDefaultParams();
		httpclient = new MyHttpClient();
		onceLoginInSystem(httpclient, Flag.CRM, defSettUsername, defSettPasswd);

		// 通过jvm进程的关闭钩子关闭共用的httpclient
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				httpclient.shutdown();
			}
		});
		
		//CRM已经登陆,若变更用户,需要手动登陆
		Map<String,Object> map = HospitalChecker.getHospitalSetting(defSettHospitalId,HospitalParam.SETTLEMENT_OPEN,HospitalParam.SETTLEMENT_TIME);
		if(map.get(HospitalParam.SETTLEMENT_OPEN).toString().equals("0"))
			try {
				DBMapper.update("update tb_hospital_settings set  settlement_open = 1 ,settlement_time = NOW() where hospital_id = "+defSettHospitalId);
			} catch (SqlException e) {
				log.error("无法开启结算功能，手动查看!");
				e.printStackTrace();
			}
		 Date d = null;
		try {
			d = simplehms.parse(HospitalChecker.getHospitalSetting(defSettHospitalId,HospitalParam.SETTLEMENT_TIME).get(HospitalParam.SETTLEMENT_TIME).toString());
		} catch (ParseException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		 settle_time = simplehms.format(d);
	}
	
	
	private static void loadDefaultParams() {

		defSettHospitalId = Integer.parseInt(testConf.getValue(ConfDefine.CRMINFO, ConfDefine.SETTLEHOSPITALID));
		defSettHospital = HospitalChecker.getHospitalById(defSettHospitalId);
		@SuppressWarnings("unused")
		List<HospitalPeriodSetting> hospitalPeriodSettings = HospitalChecker.getHospitalPeriodSettings(defSettHospitalId);

		defSettUsername = testConf.getValue(ConfDefine.CRMINFO, ConfDefine.SETTLEUSERNAME);
		defSettPasswd = testConf.getValue(ConfDefine.CRMINFO, ConfDefine.SETTLEPASSWORD);
		defSettAccountId = AccountChecker.getUserInfo(defSettUsername,SystemTypeEnum.CRM_LOGIN.getCode()).getAccount_id();
//
//		defSKXCnewcompany = CompanyChecker.getHospitalCompanyByPlatCompanyIdANDOrganizationId(
//				HospitalGuestCompanyEnum.HOSPITAL_GUEST_OFFLINE.getPlatformCompanyId()
//				,hospitalid);
//		defWSGRnewcompany = CompanyChecker.getHospitalCompanyByPlatCompanyIdANDOrganizationId(
//				HospitalGuestCompanyEnum.HOSPITAL_GUEST_ONLINE.getPlatformCompanyId(),
//				hospitalid);
//		defMTJKnewcompany = CompanyChecker.getHospitalCompanyByPlatCompanyIdANDOrganizationId(
//				HospitalGuestCompanyEnum.HOSPITAL_MTJK.getPlatformCompanyId()
//				,hospitalid);
//		//读取体检中心单位同步/套餐同步配置
//		Map<String,Object> hosSyncs = HospitalChecker.getHospitalSetting(defSettHospitalId,HospitalParam.OPEN_SYNC_COMPANY,HospitalParam.OPEN_SYNC_MEAL);

		//CRM医院的默认收款人
		try {
			hospitalRecevieTradeAccountId = PayChecker.getSuitableReceiveMethodId(defSettHospitalId, PayConstants.PayMethodBit.BalanceBit);
		} catch (Exception e) {
			e.printStackTrace();
		}


	}
}
