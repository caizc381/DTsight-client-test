package com.tijiantest.util.db;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import com.mongodb.*;
import com.mongodb.util.JSON;
import com.tijiantest.base.ConfDefine;
import com.tijiantest.util.ConfParser;
import com.tijiantest.util.DateUtils;

public class MongoDBUtils {

	private static ConfParser envParser = new ConfParser(ConfDefine.ENV_CONFIG);
	private static String dbName = envParser.getValue(ConfDefine.MONGO,ConfDefine.MONGO_DB);
	private static Map<String, MongoClient> cache = new ConcurrentHashMap<>();
//	static {
//		//通过jvm进程的关闭钩子关闭共用的mclient
//		Runtime.getRuntime().addShutdownHook(new Thread(){
//			@Override
//			public void run() {
//				cache.values().stream().forEach(t-> t.close());
//			}
//		});
//	}
	/**
	 * 清空集合数据
	 * @param collName 集合名字
	 */
	public static void removeCollection(String collName){
		MongoDBUtils.connectMongo(collName).drop();
	}

	/**
	 * 初始化添加数据
	 * @param xmlName
	 * @collName 集合名字
	 */
	public static void addData(String xmlName, String collName){
		DBCollection collection = MongoDBUtils.connectMongo(collName);
		List<DBObject> documents = MongoDBUtils.transformToDBObject(xmlName);
		collection.insert(documents);
//		documents.parallelStream().forEach(document -> collection.insert(document));
	}

	/**
	 * 删除数据
	 * @param jsonCondition
	 */
	public static void removeData(String jsonCondition, String collName){
		DBObject condition = (DBObject) JSON.parse(jsonCondition);
		DBCollection collection = MongoDBUtils.connectMongo(collName);
		collection.remove(condition);
	}

	/**
	 * 查询数据
	 * $lt:< , $lte: <= , $gt:>, $gte:>=, $in:in , $ne: !=, $or: or
	 * 例： {"age":{"$in":[1,2,3]}},{"age":{"$lt", 30}}
	 * @return
	 */
	@SuppressWarnings({"unchecked" })
	public static List<Map<String, Object>> query(String sql, String collName){
		DBObject dbo = (DBObject) JSON.parse(sql);
		DBCollection collection = MongoDBUtils.connectMongo(collName);
		List<DBObject> orderList = collection.find(dbo).toArray();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		for (DBObject obj : orderList) {
			list.add(obj.toMap());
		}
		return list;
	}


	public static List<Map<String, Object>> query(String sql, String collName,DBCollection collection){
		DBObject dbo = (DBObject) JSON.parse(sql);
		List<DBObject> orderList = collection.find(dbo).toArray();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		for (DBObject obj : orderList) {
			list.add(obj.toMap());
		}
		return list;
	}
	/**
 * 根据体检日期查找
 * @param sqlStr
 * @param startExamDate 格式："2016-08-29 00:00:00";
 * @param endExamDate  格式："2016-08-29 00:00:00";
 * @param collName
 * @return
 * @throws ParseException
 */
	public static List<Map<String, Object>> queryByExamDate(String sqlStr ,String startExamDate,String endExamDate, String collName) throws ParseException{
		return query(sqlStr,"examDate",startExamDate,endExamDate,collName);
	}

	/**
	 * 根据插入时间查找
	 * @param sqlStr
	 * @param startInsertTime  格式："2016-08-29 00:00:00";
	 * @param endInsertTime   格式："2016-08-29 00:00:00";
	 * @param collName
	 * @return
	 * @throws ParseException
	 */
	public static List<Map<String, Object>> queryByCreatedTime(String sqlStr ,String startInsertTime,String endInsertTime, String collName) throws ParseException{
		return query(sqlStr,"createdTime",startInsertTime,endInsertTime,collName);
	}

	/**
	 * 根据插入时间查找
	 * @param sqlStr
	 * @param startInsertTime  格式："2016-08-29 00:00:00";
	 * @param endInsertTime   格式："2016-08-29 00:00:00";
	 * @param collName
	 * @return
	 * @throws ParseException
	 */
	public static List<Map<String, Object>> queryByInsertTime(String sqlStr ,String startInsertTime,String endInsertTime, String collName) throws ParseException{
		return query(sqlStr,"insertTime",startInsertTime,endInsertTime,collName);
	}

	/**
	 * 查找体检时间段区间数据
	 * sql 普通sql语句不包括时间
	 * startDate 格式："2016-08-29 00:00:00";
	 * endDate 格式："2016-08-29 00:00:00";
	 * @return
	 * @throws ParseException
	 * 说明：3000年订单查询条件startDate="3000-01-01 00:00:00"; endDate=null
	 */
	@SuppressWarnings({"unchecked" })
	public static List<Map<String, Object>> query(String sqlStr ,String timeType,String startDate,String endDate, String collName) throws ParseException{
		BasicDBObject dbo = new BasicDBObject();
		if(sqlStr != null){
			dbo = (BasicDBObject) JSON.parse(sqlStr);
		}

		if(timeType.equals("examDate")){
			dbo.append("examDate",getTime(startDate,endDate));
		}

		if(timeType.equals("insertTime")){
			dbo.append("insertTime",getTime(startDate,endDate));
		}

		if(timeType.equals("createdTime")){
			dbo.append("createdTime",getTime(startDate,endDate));
		}

		DBCollection collection = MongoDBUtils.connectMongo(collName);
		List<DBObject> orderList = collection.find(dbo).toArray();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		for (DBObject obj : orderList) {
			list.add(obj.toMap());
		}
		return list;
	}


	@SuppressWarnings({"unchecked" })
	/**
	 *
	 * @param sql
	 * @param sortColumn
	 * @param orderby
	 * @param skipCount
	 * @param pageSize
	 * @return
	 */
	public static List<Map<String, Object>> queryByPage(String sql, String sortColumn,
			int orderby, Integer skipCount, Integer pageSize, String collName){
		DBObject dbo = (DBObject) JSON.parse(sql);
		DBCollection collection = MongoDBUtils.connectMongo(collName);
		List<DBObject> orderList = null;
		if(skipCount != null && pageSize != null){
			orderList = collection.find(dbo).skip(skipCount).
					sort(new BasicDBObject(sortColumn, orderby)).limit(pageSize).toArray();
		} else {
			orderList = collection.find(dbo).toArray();
		}
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		for (DBObject obj : orderList) {
			list.add(obj.toMap());
		}
		return list;
	}


	public static List<Map<String, Object>> queryByPageAndExameDate(String sql, String sortColumn,
			int orderby, Integer skipCount, Integer pageSize,String startTime,String endTime, String collName) throws ParseException{
		return queryByPageByTime(sql,sortColumn,orderby,skipCount,pageSize,startTime,endTime,"examDate",collName);
	}


	public static List<Map<String, Object>> queryByPageAndInsertTime(String sql, String sortColumn,
			int orderby, Integer skipCount, Integer pageSize,String startTime,String endTime, String collName) throws ParseException{
		return queryByPageByTime(sql,sortColumn,orderby,skipCount,pageSize,startTime,endTime,"insertTime",collName);
	}

	@SuppressWarnings({"unchecked" })
	private static List<Map<String, Object>> queryByPageByTime(String sql, String sortColumn,
			int orderby, Integer skipCount, Integer pageSize,String startTime,String endTime, String timeType,String collName) throws ParseException{
		BasicDBObject dbo = new BasicDBObject();
		if(sql != null)
			dbo = (BasicDBObject) JSON.parse(sql);

		if(timeType.equals("examDate")){
			dbo.append("examDate",getTime(startTime,endTime));
		}

		if(timeType.equals("insertTime")){
			dbo.append("insertTime",getTime(startTime, endTime));
		}

		DBCollection collection = MongoDBUtils.connectMongo(collName);
		List<DBObject> orderList = null;
		if(skipCount != null && pageSize != null){
			orderList = collection.find(dbo).skip(skipCount).
					sort(new BasicDBObject(sortColumn, orderby)).limit(pageSize).toArray();
		} else {
			orderList = collection.find(dbo).sort(new BasicDBObject(sortColumn, orderby)).toArray();
		}
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		for (DBObject obj : orderList) {
			list.add(obj.toMap());
		}
		return list;
	}


	private static BasicDBObject getTime(String stime ,String etime) throws ParseException{
		BasicDBObject time = new BasicDBObject();
		Date sd = null;
		Date se = null;
		if(stime != null)
			sd = DateUtils.parse("yyyy-MM-dd HH:mm:ss", stime);
		if(etime != null)
			se = DateUtils.parse("yyyy-MM-dd HH:mm:ss", etime);
		if(sd != null)
			time.append("$gte",sd);
		if(se != null)
			time.append("$lt", se);
		return time;

	}

	@SuppressWarnings("unchecked")
	private static List<DBObject> transformToDBObject(String xmlName){
		StringBuilder sb= new StringBuilder("");
		try {
			InputStream is = ClassLoader.getSystemResourceAsStream("dataset/" + xmlName);
	        String line;
	        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
	        line = reader.readLine();
	        while (line != null) {
	            sb.append(line);
	            line = reader.readLine();
	        }
	        reader.close();
	        is.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
        String content = Pattern.compile("\\s+").matcher(sb.toString()).replaceAll("");
        List<DBObject> dbObject = null;
        if(content != null && content.length() > 0){
        	dbObject = (List<DBObject>) JSON.parse(content);
        }
		return dbObject;
	}



	@SuppressWarnings({ "deprecation", "resource" })
	private static DBCollection connectMongo(String collName){
		MongoClient mongoClient = getMclient(collName);
		return mongoClient.getDB(dbName).getCollection(collName);
	}
	private static synchronized MongoClient getMclient(String collName) {
		if (cache.get(collName) != null) {
			return cache.get(collName);
		}
		MongoClient mongoClient = buildMclient();
		cache.put(collName, mongoClient);
		return mongoClient;
	}
	private static MongoClient buildMclient() {
		MongoClient mclient = null;
		try {
			String address = envParser.getValue(ConfDefine.MONGO,ConfDefine.REPLSET);

			ServerAddress seed1 = new ServerAddress(address.split(":")[0],Integer.parseInt(address.split(":")[1]));
			mclient = new MongoClient(seed1);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return mclient;
	}

	/**
	 * 修改数据
	 * @param query
	 * @param update
	 * @param collName
	 */
	public static void updateMongo(String query,String update, String collName){
		DBObject dbo_query = (DBObject) JSON.parse(query);
		DBObject dbo_update = (DBObject) JSON.parse(update);
		DBCollection collection = MongoDBUtils.connectMongo(collName);
		collection.update(dbo_query, dbo_update);
	}
}
