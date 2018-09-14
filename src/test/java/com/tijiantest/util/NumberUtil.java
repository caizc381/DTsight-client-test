package com.tijiantest.util;

import java.text.DecimalFormat;

public class NumberUtil {

    public  static String IntegerDivision(int a,int b){
        DecimalFormat df = new DecimalFormat("0.00");//格式化小数
        String num = df.format((float)a/b);//返回的是String类型
        return num;
    }
}
