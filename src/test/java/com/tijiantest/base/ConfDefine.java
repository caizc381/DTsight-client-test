package com.tijiantest.base;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * define constant from configure files
 * 
 * @author huifang
 *
 */
public interface ConfDefine {

	/*
	 * env config
	 */
	public final static String ENV_CONFIG = "./etc/env.conf";
	// define sections
	public final static String PUBLIC = "public";
	public final static String CRMSITE = "crm-site";
	public final static String MAINSITE = "main-site";
	public final static String MANAGESITE = "manage-site";
	public final static String CHANNELSITE = "channel-site";
	public final static String OPSSITE="ops-site";
	public final static String DATABASE = "database";
	public final static String MONGO = "mongodb";
	public final static String REDIS = "redis";
	public final static String MYSQLWAITTIME = "mysql_waitTime";
	public final static String MONGOWAITTIME = "mongo_waitTime";
	public final static String CheckCompanyOpLog = "checkCompanyOpLog";//是否校验单位操作日志
	public final static String CheckCompanySync = "checkCompanySync";//是否校验新对接单位同步
	public final static String KLOV = "klov";
	public final static String WX = "wx";
	public final static String MEDIATORAGENTSTART = "mediatorAgentStart";//深对接是否开启了agent

	// define variables
	public final static String CONNECTIONTIMEOUT = "connTimeout";
	public final static String SOCKETTIMEOUT = "socketTimeout";
	public final static String MAXCONNECTIONPERROUTE = "maxConnPerRoute";
	public final static String MAXCONNECTIONTOTAL = "maxConnTotal";
	public final static String CRMURL = "crm_url";
	public final static String MAINURL = "main_url";
	public final static String MANAGEURL = "manage_url";
	public final static String CHANNELURL = "channel_url";
	public final static String OPSURL="ops_url";
	public final static String MYSQL_DRIVER = "mysql_driver";
	public final static String MYSQL_URL = "mysql_url";
	public final static String MYSQL_USER = "mysql_user";
	public final static String MYSQL_PWD = "mysql_pwd";

	//mongoDb
	public final static String REPLSET = "replset";
	public final static String MONGO_DB = "db";
	public final static String MONGO_COLLECTION = "mongoOrder";
	public final static String MONGOMEAL_COLLECTION = "mongoMeal";
	public final static String MONGO_COMPANY_COLLECTION = "examCompanyChangeLog";

	//Redis
	public final static String REDIS_HOST = "redis_host";
	public final static String REDIS_PORT = "redis_port";
	public final static String REDIS_PASSWORD = "redis_password";
	public final static String REDIS_TIMEOUT = "redis_timeout";
 	// examreport db config
	public final static String OPENSHARDING = "openSharding";
	public final static String EXAMREPORTDB_URL = "examreport_jdbc_url";
	public final static String EXAMREPORTDB_USER = "examreport_jdbc_username";
	public final static String EXAMREPORTDB_PWD = "examreport_jdbc_password";
	
	//OPS db config
	public final static String OPSDB_URL = "ops_jdbc_url";
	public final static String OPSDB_USER = "ops_jdbc_username";
	public final static String OPSDB_PWD = "ops_jdbc_password";

	/*
	 * test config
	 */
	public final static String TEST_CONFIG = "./etc/test.conf";

	// define sections
	public final static String CRMINFO = "crm-info";
	public final static String MAININFO = "main-info";
	public final static String MANAGEINFO = "manage-info";
	public final static String CHANNELINFO = "channel-info";
	public final static String OPSINFO = "ops-info";
	public final static String USE_STAF = "use_staf";
	public final static String REPORTMANAGE = "report-manage";
	public final static String MIGRATEINFO = "migrate-info";
	public final static String MTJKHOSPITALID = "mtjk_hospitalId";
	public final static String MIGRATEURL = "migrate_url";
	
	public final static String CHECKDB = "check_db";
	public final static String CHECKMONGO = "check_mongo";
	public final static String USERNAME = "username";
	public final static String PASSWORD = "password";
	public final static String CAIWUUSERNAME="caiwuusername";
	public final static String CAIWUPASSWORD = "caiwupassword";
	public final static String PACKUSERNAME="packUsername";
	public final static String PACKASSWORD = "packPasswd";
	public final static String HOSTPITALID = "hospitalid";
	public final static String HOSPITALNAME = "hospitalname";
	public final static String BASICMEALID = "basicmealid";
	public final static String COMPANYID = "companyid";
	public final static String COMPANYNAME = "companyname";
	public final static String COMPANYTYPE = "companytype";
	public final static String MEALID = "mealid";
	public final static String PLATUSERNAME = "platusername";
	public final static String PLATUSERPASSWORD ="platpassword";
	public final static String CUSTOMER ="customer";
	public final static String CHANNELID="channelid";
	public final static String CHANNELNAME = "channelname";
	public final static String PackHospitalId = "packHospitalId";
	public final static String HIDE_PRICE_HOSPITAL_SPEL = "hidePriceHospitalId";
	public final static String HIDE_PPRICE_MANAGER_USERNAME = "hidePriceUsername";
	public final static String HIDE_PPRICE_MANAGER_PASSWD = "hidePricePasswd";
	public final static String DeepHospitalId = "deepHospitalId";
	public final static String DeepUsername = "deepUsername";
	public final static String DeepPasswd = "deepPasswd";
	// team report
	public final static String GROUPREPORT = "group-report";
	public final static String STARTDATE = "startDate";
	public final static String ENDDATE = "endDate";
	public final static String TEMPLATE_TITLE = "template_title";
	public final static String TEMPLATE_CONTENT = "template_content";
	public final static String ISSHOWHOSPITALIDS = "isShowHospitalIds";
	
	// survey
	public final static String SURVEYINFO = "survey-info";
	public final static String MAIN_SECOND = "main_second";
	public final static String SURVEYID = "surveyId";

	//report Tpl
	public final static String REPORTTPL = "report-tpl";
	
	// newcompany info
	public final static String NEWCOMPANY = "newcompany-info";
	
	//settlement
	public final static String SETTLEUSERNAME = "settleUsername";
	public final static String SETTLEPASSWORD = "settlePasswd";
	public final static String SETTLEHOSPITALID = "settleHospitalId";
	
	//klov config
	public final static String REPORT_MONGO_HOST = "klov_host";
	public final static String REPORT_MONGO_PORT = "klov_port";
	public final static String KLOV_URL = "klov_url";
	public final static String BUILD_NAME = "build_name";
	
	//channel
	public final static String CHANNELPLATMANAGER = "channelPlatManager";
	public final static String CHANNELPLATMANAGERPWD = "channelPlatManagerPwd";

	//wx
	public final static String OPENID = "openid";
	public final static String WXAPPOPENID = "wxappOpenId";

	// message template
	public final static String SUC_ORDER_TEMP = "$examperiod您的体检预约已成功！请$examperson于$examdate #if($begintime != \\\"\\\")"
			+ "$begintime#end#if($endtime != \\\"\\\")(最晚请勿超过$endtime)#end携带本人身份证空腹前往$hospital(东门：杭州市杨公堤27号;西门：杭州市龙井路5号)"
			+ "健康管理中心大厅自助领取体检单。前一天20点后请勿进食进水，客服0571-87098128\"}";

	
	

}
