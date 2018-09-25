package com.dtstack.util;

public class StringUtil {

    public static String removeCommaAtEnd(String str){
        int index = str.lastIndexOf(",");
        str = str.substring(0,index);
        return str;
    }
}
