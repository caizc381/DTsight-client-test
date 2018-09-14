package com.tijiantest.util.validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import com.tijiantest.model.account.FileAccountImportInfo;
import com.tijiantest.model.account.Validator;

public class MobileValidator implements Validator {

	@Override
	public boolean valid(FileAccountImportInfo fileAccountImportInfo) {
		fileAccountImportInfo.setMobile(null);
		String initialMobile= fileAccountImportInfo.getInitialMobile();
		if (StringUtils.isNotEmpty(initialMobile)) {
			if (initialMobile.length()>30) {
				fileAccountImportInfo.appendFailMsg(INVALID_MOBILE.getDescription());
				return false;
			}
			if (isMobile(initialMobile)) {
				fileAccountImportInfo.setMobile(initialMobile);
				fileAccountImportInfo.setIsStandardMobile(true);
			}else{
				fileAccountImportInfo.setIsStandardMobile(false);
			}
		}
		return true;
	}

	@Override
	public Integer getValidCode(FileAccountImportInfo fileAccountImportInfo) {
		if (!valid(fileAccountImportInfo)) {
			return INVALID_MOBILE.getKey();
		}
		return null;
	}
	
	public static boolean isMobile(String str){
		Pattern p = null;
		Matcher m = null;
		boolean b = false;
		p= Pattern.compile("^[1][0-9][0-9]{9}$");//验证手机号
		m = p.matcher(str);
		b = m.matches();
		return b;
	}

}
