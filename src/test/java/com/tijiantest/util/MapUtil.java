package com.tijiantest.util;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;

/**
 * Created by huifang on 2018/1/2.
 */
public class MapUtil {

    public static List<Map<String, Object>> parseJSON2List(String jsonStr){
        JSONArray jsonArr = JSONArray.fromObject(jsonStr);
        List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
        Iterator<JSONObject> it = jsonArr.iterator();
        while(it.hasNext()){
            JSONObject json2 = it.next();
            list.add(parseJSON2Map(json2.toString()));
        }
        return list;
    }


    public static List<NameValuePair> parseJSON2NameValuePairs(String jsonStr){
        List<NameValuePair> pairlist = new ArrayList<>();
        NameValuePair pair = null;
        //最外层解析
        JSONObject json = JSONObject.fromObject(jsonStr);
        for(Object k : json.keySet()){
            Object v = json.get(k);
            //如果内层还是数组的话，继续解析
            if(v instanceof JSONArray){
                List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
                Iterator<JSONObject> it = ((JSONArray)v).iterator();
                while(it.hasNext()){
                    JSONObject json2 = it.next();
                    list.add(parseJSON2Map(json2.toString()));
                }
            } else {
                //中间有逗号，说明是数组格式
                if(v.toString().contains(","))
                    v = "["+v+"]";
                NameValuePair p =  new BasicNameValuePair(k.toString(), v.toString());
                pairlist.add(p);

            }
        }
        return pairlist;
    }

    public static Map<String, Object> parseJSON2Map(String jsonStr){
        Map<String, Object> map = new HashMap<String, Object>();
        //最外层解析
        JSONObject json = JSONObject.fromObject(jsonStr);
        for(Object k : json.keySet()){
            Object v = json.get(k);
            //如果内层还是数组的话，继续解析
            if(v instanceof JSONArray){
                List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
                Iterator<JSONObject> it = ((JSONArray)v).iterator();
                while(it.hasNext()){
                    JSONObject json2 = it.next();
                    list.add(parseJSON2Map(json2.toString()));
                }
                map.put(k.toString(), list);
            } else {
                map.put(k.toString(), v);
            }
        }
        return map;
    }


    public static List<Map<String, Object>> getListByUrl(String url){
        try {
            //通过HTTP获取JSON数据
            InputStream in = new URL(url).openStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder sb = new StringBuilder();
            String line;
            while((line=reader.readLine())!=null){
                sb.append(line);
            }
            return parseJSON2List(sb.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static Map<String, Object> getMapByUrl(String url){
        try {
            //通过HTTP获取JSON数据
            InputStream in = new URL(url).openStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder sb = new StringBuilder();
            String line;
            while((line=reader.readLine())!=null){
                sb.append(line);
            }
            return parseJSON2Map(sb.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
