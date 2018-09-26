package com.dtstack.base;


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
	// uic-info
	public final static String PUBLIC = "public";
	public final static String UICSITE="uic-site";
	public final static String IDESITE = "ide-site";
	public final static String UICAPISITE="uic-api-site";
	public final static String CONSOLESITE="console-site";

	public final static String USE_STAF = "use_staf";

	public final static String DATABASE = "database";
	public final static String MYSQLWAITTIME = "mysql_waitTime";
	public final static String MONGOWAITTIME = "mongo_waitTime";

	// define variables
	public final static String CONNECTIONTIMEOUT = "connTimeout";
	public final static String SOCKETTIMEOUT = "socketTimeout";
	public final static String MAXCONNECTIONPERROUTE = "maxConnPerRoute";
	public final static String MAXCONNECTIONTOTAL = "maxConnTotal";
	public final static String UICURL="uic_url";
	public final static String IDEURL = "ide_url";
	public final static String UICAPIURL="api_url";
	public final static String CONSOLEURL="console_url";

	public final static String MYSQL_DRIVER = "mysql_driver";
	//mysql-ide
	public final static String IDE_DB_URL = "ide_jdbc_url";
	public final static String IDE_DB_USER = "ide_jdbc_username";
	public final static String IDE_DB_PWD = "ide_jdbc_password";

	//mysql-uic
	public final static String UIC_DB_URL="uic_jdbc_url";
	public final static String UIC_DB_USER="uic_jdbc_username";
	public final static String UIC_DB_PWD="uic_jdbc_password";

	//hive
	public final static String HIVE="hive";
	public final static String HIVE_DRIVER="hive_jdbc_driver";
	public final static String HIVE_URL="hive_jdbc_url";
	public final static String HIVE_USERNAME="hive_jdbc_username";
	public final static String HIVE_PASSWORD="hive_jdbc_password";
	public final static String HIVE_MAX_POOL_SIZE="hive_jdbc_max_pool_size";
	public final static String HIVE_IDLE="hive_jdbc_idle";
	public final static String HIVE_MAX_ROWS="hive_jdbc_max_rows";
	public final static String HIVE_QUERY_TIMEOUT="hive_jdbc_query_timeout";
	public final static String HIVE_IS_POOL="hive_jdbc_is_pool";



	/*
	 * test config
	 */
	public final static String TEST_CONFIG = "./etc/test.conf";

	// uic-info
	public final static String UICINFO="uic-info";
	public final static String CHECKDB = "check_db";
	public final static String CHECKMONGO = "check_mongo";
	public final static String USERNAME = "username";
	public final static String PASSWORD = "password";
	public final static String UICUSERID="uicuserid";
	public final static String RDOSUSERID="rdosuserid";
	public final static String TENANTID="tenantid";
	public final static String RDOSTENANTID="rdostenantid";
	public final static String TENANTNAME="tenantname";
	public final static String ISADMIN="isadmin";
	public final static String ISTENANTCREATOR="istenantcreator";


}
