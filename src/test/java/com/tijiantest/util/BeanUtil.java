package com.tijiantest.util;

import java.beans.BeanInfo;  
import java.beans.Introspector;  
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
  
import org.apache.commons.beanutils.BeanUtils;  
  
/** 
 * 当把Person类作为BeanUtilTest的内部类时，程序出错<br> 
 * java.lang.NoSuchMethodException: Property '**' has no setter method<br> 
 * 本质：内部类 和 单独文件中的类的区别 <br> 
 * BeanUtils.populate方法的限制：<br> 
 * The class must be public, and provide a public constructor that accepts no arguments. <br> 
 * This allows tools and applications to dynamically create new instances of your bean, <br> 
 * without necessarily knowing what Java class name will be used ahead of time 
 */  
public class BeanUtil {    
  
    // Map --> Bean 2: 利用org.apache.commons.beanutils 工具类实现 Map --> Bean  
    public static void transMap2Bean2(Map<String, Object> map, Object obj) {  
        if (map == null || obj == null) {  
            return;  
        }  
        try {  
            BeanUtils.populate(obj, map);  
        } catch (Exception e) {  
            System.out.println("transMap2Bean2 Error " + e);  
        }  
    }  
  
    // Map --> Bean 1: 利用Introspector,PropertyDescriptor实现 Map --> Bean  
    public static void transMap2Bean(Map<String, Object> map, Object obj) {  
  
        try {  
            BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());  
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();  
  
            for (PropertyDescriptor property : propertyDescriptors) {  
                String key = property.getName();  
  
                if (map.containsKey(key)) {  
                    Object value = map.get(key);  
                    // 得到property对应的setter方法  
                    Method setter = property.getWriteMethod();  
                    setter.invoke(obj, value);  
                }  
  
            }  
  
        } catch (Exception e) {  
            System.out.println("transMap2Bean Error " + e);  
        }  
  
        return;  
  
    }  
  
    // Bean --> Map 1: 利用Introspector和PropertyDescriptor 将Bean --> Map  
    public static Map<String, Object> transBean2Map(Object obj) {  
  
        if(obj == null){  
            return null;  
        }          
        Map<String, Object> map = new HashMap<String, Object>();  
        try {  
            BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());  
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();  
            for (PropertyDescriptor property : propertyDescriptors) {  
                String key = property.getName();  
  
                // 过滤class属性  
                if (!key.equals("class")) {  
                    // 得到property对应的getter方法  
                    Method getter = property.getReadMethod();  
                    Object value = getter.invoke(obj);  
  
                    map.put(key, value);  
                }  
  
            }  
        } catch (Exception e) {  
            System.out.println("transBean2Map Error " + e);  
        }  
  
        return map;  
  
    }


    /**
     * 将数据库捞出的Map转成Bean
     * @param cla
     * @param map
     * @return
     */
    public static Object mapToClassInstance(Class<?> cla, Map<String, Object> map){
        Object bean = null;
        try {
            bean = cla.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        // 取出bean里的所有方法
        Method[] methods = cla.getDeclaredMethods();
        Field[] fields = cla.getDeclaredFields();
        for (String str : map.keySet()) {
//            System.out.println("key=" + str + " value=" + map.get(str));
            String fieldName = parseDbKeyName(str);
            Field field = null;
            try {
                field = cla.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                //在cla中找不到该成员变量，就在其父类中查找
                Class superClass = cla.getSuperclass();
                try {
                    field = superClass.getDeclaredField(fieldName);
                } catch (NoSuchFieldException e1) {
                    System.out.println(fieldName+"在本类"+cla.getSimpleName()+"和父类"+superClass.getSimpleName()+"中找不到对应成员变量");
                }
            }
            if(field!=null){
                String fieldSetName = parSetName(field.getName());//生成set方法名
                Method fieldSetMet = null;//根据set方法名和变量类型获取set方法
                try {
                    fieldSetMet = cla.getMethod(fieldSetName, field.getType());
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }
                if(map.get(str)!=null&&map.get("str")!=""){
                    Object value = map.get(str);
                    String fieldType = field.getType().getSimpleName();
                    try {
                        setFieldForBean(bean,value.toString(),fieldSetMet,fieldType);
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }

        }
        return bean;
    }

    /**
     * area_object_code -> areaObjectCode
     * @param dbKeyName
     * @return
     */
    public static String parseDbKeyName(String dbKeyName){
        String fieldName ="";
        char[] charArray = dbKeyName.toCharArray();
        int length = charArray.length;
        for (int i = 0; i < length; i++) {
            if(charArray[i] == '_'){
                if(i<length-1){
//					fieldName += (char)(charArray[i+1]+32);
                    fieldName += Character.toUpperCase(charArray[i+1]);
                    i++;
                }
            }else{
                fieldName += charArray[i];
            }
        }
        System.out.println("替换后字符串："+fieldName);
        return fieldName;
    }

    /**
     * 拼接在某属性的 set方法
     *
     * @param fieldName
     * @return String
     */
    public static String parSetName(String fieldName) {
        if (null == fieldName || "".equals(fieldName)) {
            return null;
        }
        int startIndex = 0;
        if (fieldName.charAt(0) == '_')
            startIndex = 1;
        return "set"
                + fieldName.substring(startIndex, startIndex + 1).toUpperCase()
                + fieldName.substring(startIndex + 1);
    }

    /**
     * 设置变量值
     * @param bean
     * @param value
     * @param fieldSetMet
     * @param fieldType
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public static void setFieldForBean(Object bean, String value, Method fieldSetMet, String fieldType) throws InvocationTargetException, IllegalAccessException {
        if (null != value && !"".equals(value)) {
            if ("String".equals(fieldType)) {
                fieldSetMet.invoke(bean, value);
            } else if ("Date".equals(fieldType)) {
                Date temp = parseDate(value);
                fieldSetMet.invoke(bean, temp);
            } else if ("Integer".equals(fieldType)
                    || "int".equals(fieldType)) {
                Integer intval = Integer.parseInt(value);
                fieldSetMet.invoke(bean, intval);
            } else if ("Long".equalsIgnoreCase(fieldType)) {
                Long temp = Long.parseLong(value);
                fieldSetMet.invoke(bean, temp);
            } else if ("Double".equalsIgnoreCase(fieldType)) {
                Double temp = Double.parseDouble(value);
                fieldSetMet.invoke(bean, temp);
            } else if ("Boolean".equalsIgnoreCase(fieldType)) {
                Boolean temp = Boolean.parseBoolean(value);
                fieldSetMet.invoke(bean, temp);
            } else {
                System.out.println("not supper type" + fieldType);
            }
        }
    }

    /**
     * 格式化string为Date
     *
     * @param datestr
     * @return date
     */
    public static Date parseDate(String datestr) {
        if (null == datestr || "".equals(datestr)) {
            return null;
        }
        try {
            String fmtstr = null;
            if (datestr.indexOf(':') > 0) {
                fmtstr = "yyyy-MM-dd HH:mm:ss";
            } else {
                fmtstr = "yyyy-MM-dd";
            }
            SimpleDateFormat sdf = new SimpleDateFormat(fmtstr, Locale.UK);
            return sdf.parse(datestr);
        } catch (Exception e) {
            return null;
        }
    }
}  