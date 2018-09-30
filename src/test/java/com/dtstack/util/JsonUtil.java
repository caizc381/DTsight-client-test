package com.dtstack.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class JsonUtil {

    /**
     * 对jsonarray中某个对象的某一个字段进行排序
     *
     * @param key 按照key进行排序
     */
    public static JSONArray sort(JSONArray array, String key) {
        JSONArray sortJSONArray = new JSONArray();
        List<JSONObject> jsonValue = new ArrayList<>();
        for (int i = 0; i < array.size(); i++) {
            jsonValue.add(array.getJSONObject(i));
        }
        Collections.sort(jsonValue, new Comparator<JSONObject>() {
            @Override
            public int compare(JSONObject o1, JSONObject o2) {
                Integer valA = o1.getIntValue(key);
                Integer valB = o2.getIntValue(key);
                return valA.compareTo(valB);
            }
        });
        for (int i = 0; i < array.size(); i++) {
            sortJSONArray.add(jsonValue.get(i));
        }
        return sortJSONArray;
    }
}
