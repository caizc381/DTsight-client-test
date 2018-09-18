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
	// define sections
	public final static String PUBLIC = "public";
	public final static String DTUICSITE="dtuic-site";
	public final static String IDESITE = "ide-site";

	public final static String USE_STAF = "use_staf";

	public final static String DATABASE = "database";
	public final static String MYSQLWAITTIME = "mysql_waitTime";
	public final static String MONGOWAITTIME = "mongo_waitTime";

	// define variables
	public final static String CONNECTIONTIMEOUT = "connTimeout";
	public final static String SOCKETTIMEOUT = "socketTimeout";
	public final static String MAXCONNECTIONPERROUTE = "maxConnPerRoute";
	public final static String MAXCONNECTIONTOTAL = "maxConnTotal";
	public final static String DTUICURL="dtuic_url";
	public final static String IDEURL = "ide_url";
	public final static String MYSQL_DRIVER = "mysql_driver";
	public final static String MYSQL_URL = "mysql_url";
	public final static String MYSQL_USER = "mysql_user";
	public final static String MYSQL_PWD = "mysql_pwd";



	/*
	 * test config
	 */
	public final static String TEST_CONFIG = "./etc/test.conf";

	// define sections
	public final static String DTUICINFO="dtuic-info";
	public final static String CHECKDB = "check_db";
	public final static String CHECKMONGO = "check_mongo";
	public final static String USERNAME = "username";
	public final static String PASSWORD = "password";
}
