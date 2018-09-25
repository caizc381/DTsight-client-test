package com.dtstack.util.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.testng.Assert;

/**
 *
 * @author linzhihao
 */
public class DBMapper {

	private static class DB {
		public static  Connection ideconnection;
		public static  Connection uicconnection;
		static {
			DBHelper dbHelper = new DBHelper();;
			try {
				ideconnection = dbHelper.getIdeConnection();
				uicconnection=dbHelper.getUicConnection();

			} catch (SqlException e) {
				e.printStackTrace();
			}
		}
	}
	

	
	public static List<Map<String, Object>> queryUic(String sql, Object... params) throws SqlException {
		QueryRunner run = new QueryRunner();
		MapListHandler h = new MapListHandler();
		List<Map<String, Object>> result = null;
		try {
			result = run.query(DB.uicconnection, sql, h, params);
		} catch (SQLException e) {
			throw new SqlException("查询数据异常", e);
		} 

		return result;
	}
	
	public static void deleteUic(String sql,int x, Object... params) throws SqlException {
		QueryRunner run = new QueryRunner();
		for(int i=1;i<=x;i++){
			try {
				run.update(DB.uicconnection, sql, params);
			} catch (SQLException e) {
				Assert.fail("删除数据错误");
			}
		}		
	}
	
	public static List<Map<String, Object>> query(String sql, Object... params) throws SqlException {
		QueryRunner run = new QueryRunner();
		MapListHandler h = new MapListHandler();
		List<Map<String, Object>> result = null;
		try {
			result = run.query(DB.ideconnection, sql, h, params);
		} catch (SQLException e) {
			throw new SqlException("查询数据异常", e);
		} 

		return result;
	}
	
	public static void delete(String sql,int x, Object... params) throws SqlException {
		QueryRunner run = new QueryRunner();
		for(int i=1;i<=x;i++){
			try {
				run.update(DB.ideconnection, sql, params);
			} catch (SQLException e) {
				Assert.fail("删除数据错误");
			}
		}
			
	}
	
	public static void update(String sql, Object... params) throws SqlException{
		QueryRunner run = new QueryRunner();
		//MapListHandler h = new MapListHandler();
			try {
				run.update(DB.ideconnection, sql, params);
			} catch (SQLException e) {
				Assert.fail("修改数据错误");
			}	
	}
}
