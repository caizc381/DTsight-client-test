package com.tijiantest.util.fuc;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.mongodb.DBCollection;
import com.tijiantest.util.ListUtil;
import com.tijiantest.util.db.DBMapper;
import com.tijiantest.util.db.MongoDBUtils;
import com.tijiantest.util.db.SqlException;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class OrderMysqlAndMongoTest {

    protected final static Logger log = Logger.getLogger(OrderMysqlAndMongoTest.class);

    SimpleDateFormat sdf1= new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
    SimpleDateFormat sdf2= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    //使用时间对比
    boolean useTimeSnap = false;
    //设置开始/结束时间
    String startTime = "2014-01-01 15:28:29";
    //    String endTime = "2016-10-12 10:00:00";
    String endTime = "2016-01-10 18:00:00";
    String orderS = "20180416163711661000154";
    String collName = "mongoMeal";
    @Test(description = "全量迁移")
    public void test_01_checkMysqlAndMongo_allCounts() throws SqlException, ParseException {
        //1.mysql
        String sql = "select order_num,items_detail,meal_detail,package_snapshot_detail,insert_time,update_time  from tb_order where insert_time <= '"+endTime+"' and insert_time >='"+startTime+"'";
        List<Map<String,Object>> dblist = DBMapper.query(sql);
        int orderSize = dblist.size();
        //2.mongo
        String mongoSql = null;
        List<Map<String,Object>> mongoList = null;
        for(int i=0;i<orderSize;i++){
            Map<String,Object> db = dblist.get(i);
            String orderNum = db.get("order_num").toString();
            log.info("订单编号"+orderNum);
            if(orderNum.equals("20140612090448"))
                continue;;
            //比较单项
            mongoSql = "{'domain':'order','domainId':'"+orderNum+"','bizType':'examItemSnapshot'}";
            mongoList = MongoDBUtils.query(mongoSql, collName);
            if(mongoList == null || mongoList.size()>0) {
                Map<String, Object> mo = mongoList.get(0);
                Assert.assertEquals(mo.get("operation").toString(), "create");//operation
                if (db.get("insert_time") != null) {
                    String insert = db.get("insert_time").toString();
                    Assert.assertEquals(sdf2.format(sdf1.parse(mo.get("createdTime").toString()))//createdTime
                            , insert.substring(0, insert.length() - 2));
                } else
                    log.info("该订单mysql插入时间为空" + orderNum);
                Assert.assertEquals(mo.get("domain").toString(), "order");//domain
                Assert.assertEquals(mo.get("_class").toString(), "com.mytijian.offer.snapshot.model.Snapshot");//_class
                Assert.assertNull(mo.get("bizId"));//bizId
                JSONArray mysqlItemArray = JSONArray.parseArray(db.get("items_detail").toString());
            JSONArray mongoItemArray = JSONArray.parseArray(mo.get("value").toString());
            String mysqlItems = JSON.toJSONString(mysqlItemArray, SerializerFeature.MapSortField);
            String mongoItems = JSON.toJSONString(mongoItemArray, SerializerFeature.MapSortField);
            Assert.assertEquals(mongoItems,mysqlItems);//value

            }else
                Assert.assertTrue(db.get("items_detail") == null || db.get("items_detail").toString().equals("[]"));
            //比较套餐
            mongoSql = "{'domain':'order','domainId':'"+orderNum+"','bizType':'mealSnapshot'}";
            mongoList = MongoDBUtils.query(mongoSql, collName);
            Assert.assertTrue(mongoList.size()>=1);
            Map<String,Object> mo1 = mongoList.get(0);
            Assert.assertEquals(mo1.get("operation").toString(),"create");//operation
            if(db.get("insert_time") != null){
                String insert = db.get("insert_time").toString();
                Assert.assertEquals(sdf2.format(sdf1.parse(mo1.get("createdTime").toString()))//createdTime
                        ,insert.substring(0,insert.length()-2));
            }
            Assert.assertEquals(mo1.get("domain").toString(), "order");//domain
            Assert.assertEquals(mo1.get("_class").toString(), "com.mytijian.offer.snapshot.model.Snapshot");//_class

            JSONObject mysqlMealArray = JSON.parseObject(db.get("meal_detail").toString());
            JSONObject mongoMealArray = JSON.parseObject(mo1.get("value").toString());
            String mysqlMeal = JSON.toJSONString(mysqlMealArray, SerializerFeature.MapSortField);
            String mongoMeal = JSON.toJSONString(mongoMealArray, SerializerFeature.MapSortField);
            Assert.assertEquals(mongoMeal,mysqlMeal);//value
            Assert.assertEquals(mo1.get("bizId").toString(),mysqlMealArray.get("id").toString() );//bizId


            //比较单项包
            mongoSql = "{'domain':'order','domainId':'"+orderNum+"','bizType':'examItemPackageSnapshot'}";
            mongoList = MongoDBUtils.query(mongoSql, collName);
            if(mongoList == null || mongoList.size()>0){
                Assert.assertEquals(mongoList.size(),1);
                Map<String,Object> mo2 = mongoList.get(0);
                Assert.assertEquals(mo2.get("operation").toString(),"create");//operation
                if(db.get("insert_time") != null){
                    String insert = db.get("insert_time").toString();
                    Assert.assertEquals(sdf2.format(sdf1.parse(mo2.get("createdTime").toString()))//createdTime
                            ,insert.substring(0,insert.length()-2));
                }
                Assert.assertEquals(mo2.get("domain").toString(), "order");//domain
                Assert.assertEquals(mo2.get("_class").toString(), "com.mytijian.offer.snapshot.model.Snapshot");//_class
                Assert.assertNull(mo2.get("bizId"));//bizId
                JSONObject mysqlPackArray = JSON.parseObject(db.get("package_snapshot_detail").toString());
                JSONObject mongoPackArray = JSON.parseObject(mo2.get("value").toString());
                String mysqlPack = JSON.toJSONString(mysqlPackArray, SerializerFeature.MapSortField);
                String mongoPack = JSON.toJSONString(mongoPackArray, SerializerFeature.MapSortField);
                Assert.assertEquals(mongoPack,mysqlPack); //value


            }else
                Assert.assertNull(db.get("package_snapshot_detail"));

        }
        log.info("订单总数量:"+orderSize);
//        Assert.assertEquals(mongoList.size(),dblist.size());
    }

    @Test(description = "增量迁移")
    public void test_02_some_add_items() throws SqlException, ParseException {
        //1.mysql
        String sql = "select order_num,items_detail,meal_detail,package_snapshot_detail,insert_time,update_time  from tb_order where";
        if(useTimeSnap)
            sql += " insert_time <= '"+endTime+"' and insert_time >='"+startTime+"'";
        else
            sql += " order_num in ("+orderS +" )";
        log.info(sql);
        List<Map<String,Object>> dblist = DBMapper.query(sql);
        int orderSize = dblist.size();
        //2.mongo
        String mongoSql = null;
        List<Map<String,Object>> mongoList = null;
        for(int i=0;i<orderSize;i++){
            Map<String,Object> db = dblist.get(i);
            String orderNum = db.get("order_num").toString();
            log.info("订单编号"+orderNum);
            if(orderNum.equals("20140612090448"))
                continue;;
            //比较单项
            mongoSql = "{'domain':'order','domainId':'"+orderNum+"','bizType':'examItemSnapshot'}";
            mongoList = MongoDBUtils.query(mongoSql, collName);
            if(mongoList == null || mongoList.size()>0) {
                Map<String, Object> mo = mongoList.get(0);
                Assert.assertTrue(mo.get("operation").toString().equals( "create")||mo.get("operation").equals("modify"));//operation
                if (db.get("insert_time") != null) {
                    String insert = db.get("insert_time").toString();
                    Assert.assertEquals(sdf2.format(sdf1.parse(mo.get("createdTime").toString()))//createdTime
                            , insert.substring(0, insert.length() - 2));
                } else
                    log.info("该订单mysql插入时间为空" + orderNum);
                Assert.assertEquals(mo.get("domain").toString(), "order");//domain
                Assert.assertEquals(mo.get("_class").toString(), "com.mytijian.offer.snapshot.model.Snapshot");//_class
                Assert.assertNull(mo.get("bizId"));//bizId
                JSONArray mysqlItemArray = JSONArray.parseArray(db.get("items_detail").toString());
                JSONArray mongoItemArray = JSONArray.parseArray(mo.get("value").toString());
                String mysqlItems = JSON.toJSONString(mysqlItemArray, SerializerFeature.MapSortField);
                String mongoItems = JSON.toJSONString(mongoItemArray, SerializerFeature.MapSortField);
                Assert.assertEquals(mongoItems,mysqlItems);//value
            }else
                Assert.assertTrue(db.get("items_detail") == null || db.get("items_detail").toString().equals("[]"));
            //比较套餐
            mongoSql = "{'domain':'order','domainId':'"+orderNum+"','bizType':'mealSnapshot'}";
            mongoList = MongoDBUtils.query(mongoSql, collName);
            Assert.assertTrue(mongoList.size()>=1);
            Map<String,Object> mo1 = mongoList.get(0);
            Assert.assertTrue(mo1.get("operation").toString().equals( "create")||mo1.get("operation").equals("modify"));//operation
            if(db.get("insert_time") != null){
                String insert = db.get("insert_time").toString();
                Assert.assertEquals(sdf2.format(sdf1.parse(mo1.get("createdTime").toString()))//createdTime
                        ,insert.substring(0,insert.length()-2));
            }
            Assert.assertEquals(mo1.get("domain").toString(), "order");//domain
            Assert.assertEquals(mo1.get("_class").toString(), "com.mytijian.offer.snapshot.model.Snapshot");//_class

            JSONObject mysqlMealArray = JSON.parseObject(db.get("meal_detail").toString());
            JSONObject mongoMealArray = JSON.parseObject(mo1.get("value").toString());
            Assert.assertEquals(mo1.get("bizId").toString(),mysqlMealArray.get("id").toString() );//bizId
            String mysqlMeal = JSON.toJSONString(mysqlMealArray, SerializerFeature.MapSortField);
            String mongoMeal = JSON.toJSONString(mongoMealArray, SerializerFeature.MapSortField);
            Assert.assertEquals(mongoMeal,mysqlMeal);//value

            //比较单项包
            mongoSql = "{'domain':'order','domainId':'"+orderNum+"','bizType':'examItemPackageSnapshot'}";
            mongoList = MongoDBUtils.query(mongoSql, collName);
            if(mongoList == null || mongoList.size()>0){
                Assert.assertEquals(mongoList.size(),1);
                Map<String,Object> mo2 = mongoList.get(0);
                Assert.assertTrue(mo2.get("operation").toString().equals( "create")||mo2.get("operation").equals("modify"));//operation
                if(db.get("insert_time") != null){
                    String insert = db.get("insert_time").toString();
                    Assert.assertEquals(sdf2.format(sdf1.parse(mo2.get("createdTime").toString()))//createdTime
                            ,insert.substring(0,insert.length()-2));
                }
                Assert.assertEquals(mo2.get("domain").toString(), "order");//domain
                Assert.assertEquals(mo2.get("_class").toString(), "com.mytijian.offer.snapshot.model.Snapshot");//_class
                Assert.assertNull(mo2.get("bizId"));//bizId
                JSONObject mysqlPackArray = JSON.parseObject(db.get("package_snapshot_detail").toString());
                JSONObject mongoPackArray = JSON.parseObject(mo2.get("value").toString());
                String mysqlPack = JSON.toJSONString(mysqlPackArray, SerializerFeature.MapSortField);
                String mongoPack = JSON.toJSONString(mongoPackArray, SerializerFeature.MapSortField);
                Assert.assertEquals(mongoPack,mysqlPack); //value


            }else
                Assert.assertNull(db.get("package_snapshot_detail"));

        }
        log.info("订单总数量:"+orderSize);
    }
}
