package com.dtstack.util;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

/**
 * 拼音工具类
 * 
 * @author yuefengyang
 */
public class PinYinUtil {
	
	// 全角对应于ASCII表的可见字符从！开始，偏移值为65281
	static final char SBC_CHAR_START = 65281;   
     
	// 全角对应于ASCII表的可见字符到～结束，偏移值为65374
	static final char SBC_CHAR_END = 65374; 
    
	// ASCII表中除空格外的可见字符与对应的全角字符的相对偏移 
    static final int CONVERT_STEP = 65248; 

    //全角空格的值，它没有遵从与ASCII的相对偏移，必须单独处理 
    static final char SBC_SPACE = 12288;  
    
    //半角空格
    static final char DBC_SPACE = 32;
	
	/**
	 * 将字符串中的中文转化为拼音,其他字符不变
	 * 
	 * @param inputString
	 * @return
	 */
	public static String getPinYin(String inputString) {
		if(inputString == null){
			return "";
		}
		inputString = PinYinUtil.fullWidth2halfWidth(inputString);
		HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
		format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
		format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		format.setVCharType(HanyuPinyinVCharType.WITH_V);

		char[] input = inputString.trim().toCharArray();
		String output = "";

		try {
			for (int i = 0; i < input.length; i++) {
				if (java.lang.Character.toString(input[i]).matches("[\\u4E00-\\u9FA5]+")) {
					String[] temp = PinyinHelper.toHanyuPinyinStringArray(input[i], format);
					output += temp[0];
				} else
					output += java.lang.Character.toString(input[i]);
			}
		} catch (BadHanyuPinyinOutputFormatCombination e) {
			e.printStackTrace();
		}
		return output;
	}

	/**  
	 * 获取汉字串拼音首字母，英文字符不变  
	 * @param chinese 汉字串  
	 * @return 汉语拼音首字母  
	 */
	public static String getFirstSpell(String chinese) {
		if(chinese == null){
			return "";
		}
		chinese = PinYinUtil.fullWidth2halfWidth(chinese);
		StringBuffer pybf = new StringBuffer();
		char[] arr = chinese.toCharArray();
		HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
		defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
		defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		for (int i = 0; i < arr.length; i++) {
			if (arr[i] > 128) {
				try {
					String[] temp = PinyinHelper.toHanyuPinyinStringArray(arr[i], defaultFormat);
					if (temp != null) {
						pybf.append(temp[0].charAt(0));
					}
				} catch (BadHanyuPinyinOutputFormatCombination e) {
					e.printStackTrace();
				}
			} else {
				pybf.append(arr[i]);
			}
		}
		return pybf.toString().replaceAll("\\W", "").trim();
	}

	/**  
	 * 获取汉字串拼音，英文字符不变  
	 * @param chinese 汉字串  
	 * @return 汉语拼音  
	 */
	public static String getFullSpell(String chinese) {
		if(chinese == null){
			return "";
		}
		chinese = PinYinUtil.fullWidth2halfWidth(chinese);
		StringBuffer pybf = new StringBuffer();
		char[] arr = chinese.toCharArray();
		HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
		defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
		defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		for (int i = 0; i < arr.length; i++) {
			if (arr[i] > 128) {
				try {
					pybf.append(PinyinHelper.toHanyuPinyinStringArray(arr[i], defaultFormat)[0]);
				} catch (BadHanyuPinyinOutputFormatCombination e) {
					e.printStackTrace();
				}
			} else {
				pybf.append(arr[i]);
			}
		}
		return pybf.toString();
	}
	
	/**
	 * 全角转半角
	 * 
	 * @param fullWidthStr
	 *            字符串
	 * @return
	 */
	public static String fullWidth2halfWidth(String fullWidthStr) {
		if (null == fullWidthStr || fullWidthStr.length() <= 0) {
			return fullWidthStr;
		}
		char[] charArray = fullWidthStr.toCharArray();
		// 对全角字符转换的char数组遍历
		for (int i = 0; i < charArray.length; ++i) {
			int charIntValue = (int) charArray[i];
			// 如果符合转换关系,将对应下标之间减掉偏移量65248;如果是空格的话,直接做转换
			if (charIntValue >= SBC_CHAR_START && charIntValue <= SBC_CHAR_END) {
				charArray[i] = (char) (charIntValue - CONVERT_STEP);
			} else if (charIntValue == SBC_SPACE) {
				charArray[i] = (char) DBC_SPACE;
			}
		}
		return new String(charArray);
	}
}
