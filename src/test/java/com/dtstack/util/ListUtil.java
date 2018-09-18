package com.dtstack.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class ListUtil {

	protected final static Logger log = Logger.getLogger(ListUtil.class);
	
	public static List<Integer> CollStringToIntegerLst(List<String> inList) {
		List<Integer> iList = new ArrayList<Integer>(inList.size());
		CollectionUtils.collect(inList, new Transformer() {
			public java.lang.Object transform(java.lang.Object input) {
				return new Integer((String) input);
			}
		}, iList);
		return iList;
	}

	public static List<String> StringToStringList(String str) {
		List<String> ret = new ArrayList<String>();
		String d[] = str.split(",");
		for (int i = 0; i < d.length; i++) {
			ret.add(d[i]);
		}
		return ret;
	}
	
	public static List<Integer> StringLstToIntegerLst(List<String> inList) {
		List<Integer> iList = new ArrayList<Integer>(inList.size());
		try {
			for (int i = 0, j = inList.size(); i < j; i++) {
				iList.add(Integer.parseInt(inList.get(i)));
			}
		} catch (Exception e) {
		}
		return iList;
	}
	
	public static String StringlistToString(List<String> list) {
		String ret = "";
		for (Object l : list) {
			ret += l + ",";
		}
		ret = ret.substring(0, ret.length() - 1);
		return ret;
	}
	
	public static List<Integer> StringArraysToIntegerList(String[] strs) {
		List<Integer> list = StringLstToIntegerLst(Arrays.asList(strs));
		return list;
	}
	
	public static String IntegerArraysToString(Integer[] strs) {
		List<Integer> list = Arrays.asList(strs);
		return IntegerlistToString(list);
	}
	
	public static String IntegerlistToString(List<Integer> list) {
		String ret = "";
		for (Object l : list) {
			ret += l + ",";
		}
		if (!ret.equals("")) {
			ret = ret.substring(0, ret.length() - 1);
		}
		return ret;
	}
	
	/***************************************************************************************/
	
	/**
	 * 在list中随机取一个数字
	 * @param list
	 * @return
	 */
	public static int getRandomIndexFromList(@SuppressWarnings("rawtypes") List list) {

		Random random = new Random();
		int index = random.nextInt(list.size()) % (list.size() + 1);
		return index;
	}
	
	/**
	 * 获取列表中的重复项 单项名称/个数
	 * 
	 * @param list
	 * @return
	 */
	public static Map<Integer, Integer> getSameDataMap(List<Integer> list) {
		Map<Integer, Integer> sameMap = new HashMap<Integer, Integer>();
		Map<Integer, Integer> map = new HashMap<Integer, Integer>();
		for (int i = 0; i < list.size(); i++) {
			Integer key = list.get(i);
			Integer old = map.get(key);
			if (old != null) {
				map.put(key, ++old);
			} else {
				map.put(key, 1);
			}
		}
		Iterator<Integer> it = map.keySet().iterator();
		while (it.hasNext()) {
			Integer key = it.next();
			Integer value = map.get(key);
			if (value > 1) {
				sameMap.put(key, value);
				log.debug(key + " 重复,个数 " + value);
			}
		}
//		System.out.println(sameMap);
		return sameMap;
	}	
	
	/**
	 * List 删除列表中的某个元素
	 */

	public static List<Integer> ListRemoveObj(List<Integer> list, int o) {
		Iterator<Integer> it = list.iterator();
		while (it.hasNext()) {
			if (it.next().equals(o))
				it.remove();
			;
		}
		return list;
	}
	
	
	/**
	 * JSONArray按照字段排序
	 * 
	 * @param ja
	 * @param field
	 * @param isAsc
	 */
	public void SortJSONArrayById(JSONArray ja, String field, boolean isAsc) {
		if (ja != null) {
			List<JSONObject> jsonList = new ArrayList<JSONObject>();
			for (int i = 0; i < ja.size(); i++)
				jsonList.add(ja.getJSONObject(i));
			Collections.sort(jsonList, new Comparator<JSONObject>() {
				@Override
				public int compare(JSONObject o1, JSONObject o2) {
					Object f1 = o1.get(field);
					Object f2 = o2.get(field);
					if (f1 instanceof Number && f2 instanceof Number) {
						return ((Number) f1).intValue() - ((Number) f2).intValue();
					} else {
						return f1.toString().compareTo(f2.toString());
					}
				}
			});
			if (!isAsc) {
				Collections.reverse(ja);
			}

		}
	}
	
	/*
	 *Sets 转换成 List
	 */
	public static List<Integer> SetsToLists(Set<Integer> set){
		List<Integer> list = new ArrayList<Integer>();
		Iterator<Integer> it = set.iterator();
		while(it.hasNext()){
			int a = it.next();
			list.add(a);
		}
		return list;
		
	}
	
	/**
	 * Integer JSONArray排序
	 * 
	 * @param ja
	 * @param field
	 * @param isAsc
	 */
	public static List<Integer> SortJSONIntegerArrayById(JSONArray ja) {
		List<Integer> jsonList = new ArrayList<Integer>();
		if (ja != null) {
			for (int i = 0; i < ja.size(); i++)
				jsonList.add(ja.getInteger(i));
			Collections.sort(jsonList);
		}
		return jsonList;
	}
	
	public static String SetsToString(Set<Integer> set) {
		String setStr = IntegerlistToString(SetsToLists(set));
		return setStr;
	}

	/**
	 * 判断2个List内容一致
	 * @param list1
	 * @param list2
	 * @return
	 */
	public static boolean equalList(List list1, List list2) {return (list1.size() == list2.size()) && list1.containsAll(list2);}
}
