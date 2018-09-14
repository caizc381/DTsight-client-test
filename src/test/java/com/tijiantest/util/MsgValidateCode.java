package com.tijiantest.util;

import java.util.Random;

public class MsgValidateCode {
		
	//获取4位数字随机验证码	 
	public static String getRandomNum(int num){     
		
		String[] digits = {"1","2","3","4","5","6","7","8","9","0"};     
		Random rnum = new Random(System.nanoTime()); 
		for(int i=0; i<digits.length;i++)
		{     int index = Math.abs(rnum.nextInt())%10;
		       String tmpDigit=digits[index]; 
		       digits[index]=digits[i];   
		       digits[i]=tmpDigit;    
		 }                        
		 String returnStr=digits[0];                    
		 for(int i=1;i<num;i++)                   
		 {                            
			 returnStr=digits[i]+returnStr;                    
		 }                    
		 return   returnStr;   
	 }
	 //获取4位字母随机验证码	
	 public static String getRandomChar(){
		//生成一个0、1、2的随机数字
		int rand = (int)Math.round(Math.random() * 2);
		long itmp = 0;
		char ctmp = '\u0000';
		switch (rand)
		{
			//生成大写字母
			case 1:
				itmp = Math.round(Math.random() * 25 + 65);
				ctmp = (char)itmp;
				return String.valueOf(ctmp);
				//生成小写字母
			case 2:
				itmp = Math.round(Math.random() * 25 + 97);
				ctmp = (char)itmp;
				return String.valueOf(ctmp);
				//生成数字
			default :
				itmp = Math.round(Math.random() * 9);
				return  itmp + "";
		}
	}
	
}

