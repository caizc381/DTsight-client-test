package com.tijiantest.util.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.tijiantest.base.ConfDefine;
import com.tijiantest.util.ConfParser;


public class DBHelper {

	private Logger logger = Logger.getLogger(DBHelper.class);
	
	private String url;
	
	private String user;
	
	private String password;

	/** 
	 * 获取数据库连接 
	 * @return 
	 * @throws DeployTestException 
	 */
	public Connection getConnection() throws SqlException {
		ConfParser cparser = new ConfParser(ConfDefine.ENV_CONFIG);
		String driver = cparser.getValue(ConfDefine.DATABASE, ConfDefine.MYSQL_DRIVER);
		url = cparser.getValue(ConfDefine.DATABASE, ConfDefine.MYSQL_URL);
		user = cparser.getValue(ConfDefine.DATABASE, ConfDefine.MYSQL_USER);
		password = cparser.getValue(ConfDefine.DATABASE, ConfDefine.MYSQL_PWD);
		
		Connection con = null;
		try {
		
			Class.forName(driver);
			con = DriverManager.getConnection(url, user, password);

		} catch (ClassNotFoundException e) {
			throw new SqlException("连接数据库失败", e);
		} catch (Exception e) {
			throw new SqlException("连接数据库失败", e);
		}
		return con;
	}
	
	
	
	/** 
	 * 获取体检报告库数据库连接 
	 * @return 
	 * @throws DeployTestException 
	 */
	public Connection getExamReportConnection() throws SqlException {
		ConfParser cparser = new ConfParser(ConfDefine.ENV_CONFIG);
		String driver = cparser.getValue(ConfDefine.DATABASE, ConfDefine.MYSQL_DRIVER);
		url = cparser.getValue(ConfDefine.DATABASE, ConfDefine.EXAMREPORTDB_URL);
		user = cparser.getValue(ConfDefine.DATABASE, ConfDefine.EXAMREPORTDB_USER);
		password = cparser.getValue(ConfDefine.DATABASE, ConfDefine.EXAMREPORTDB_PWD);
		
		Connection con = null;
		try {
		
			Class.forName(driver);
			con = DriverManager.getConnection(url, user, password);

		} catch (ClassNotFoundException e) {
			throw new SqlException("连接数据库失败", e);
		} catch (Exception e) {
			throw new SqlException("连接数据库失败", e);
		}
		return con;
	}
	
	/** 
	 * 获取OPS数据库连接 
	 * @return 
	 * @throws DeployTestException 
	 */
	public Connection getOpsConnection() throws SqlException {
		ConfParser cparser = new ConfParser(ConfDefine.ENV_CONFIG);
		String driver = cparser.getValue(ConfDefine.DATABASE, ConfDefine.MYSQL_DRIVER);
		url = cparser.getValue(ConfDefine.DATABASE, ConfDefine.OPSDB_URL);
		user = cparser.getValue(ConfDefine.DATABASE, ConfDefine.OPSDB_USER);
		password = cparser.getValue(ConfDefine.DATABASE, ConfDefine.OPSDB_PWD);
		
		Connection con = null;
		try {
		
			Class.forName(driver);
			System.out.println("url...."+url);
			con = DriverManager.getConnection(url, user, password);

		} catch (ClassNotFoundException e) {
			throw new SqlException("连接数据库失败", e);
		} catch (Exception e) {
			throw new SqlException("连接数据库失败", e);
		}
		return con;
	}
	public void closeConnection(Connection con){
		closeAll(con, null, null);
	}




	/** 
	 * 关闭数据库 
	 * @param con 
	 * @param pst 
	 * @param rst 
	 */
	public void closeAll(Connection con, PreparedStatement pst, ResultSet rst) {
		if (rst != null) {
			try {
				rst.close();
			} catch (SQLException e) {
				logger.error("关闭数据库结果集失败：{}",e);
			}
		}

		if (pst != null) {
			try {
				pst.close();
			} catch (SQLException e) {
				logger.error("关闭数据库预处理失败：{}",e);
			}
		}

		if (con != null) {
			try {
				con.close();
			} catch (SQLException e) {
				logger.error("关闭数据库连接失败：{}",e);
			}
		}

	}

}