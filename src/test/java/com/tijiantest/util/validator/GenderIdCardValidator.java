package com.tijiantest.util.validator;

import org.apache.commons.lang.StringUtils;

import com.tijiantest.model.account.FileAccountImportInfo;
import com.tijiantest.model.account.IdTypeEnum;

public class GenderIdCardValidator implements Validator {

	@Override
	public boolean valid(FileAccountImportInfo fileAccountImportInfo) {
		// 若之前已经有身份证号码错误的判断，直接跳过身份证性别与填写性别的判断
		if (!checkIdCard(fileAccountImportInfo)) {
			return false;
		}
		String idCard = fileAccountImportInfo.getIdCard();
		String inputGender = fileAccountImportInfo.getGender();
		Integer idType = fileAccountImportInfo.getIdType();

		if (idType == IdTypeEnum.IDCARD.getCode() 
				&& StringUtils.isNotEmpty(inputGender) 
				&& StringUtils.isNotEmpty(idCard)
				&& (idCard.length() == 15 || idCard.length() == 18 )) {
			String genderOfIdCard = idCard.length() == 18 ? idCard.substring(16, 17) : idCard.substring(14);
			// 增加genderOfIdCard必须为数字的判断，防止输入错误的身份证号码带字符的问题
			if (!StringUtils.isNumeric(genderOfIdCard) || (Integer.parseInt(genderOfIdCard) % 2 == 0 && (inputGender.contains("男") || "0".equals(inputGender)))
					|| (Integer.parseInt(genderOfIdCard) % 2 == 1 && (inputGender.contains("女") || "1".equals(inputGender)))) {
				fileAccountImportInfo.appendFailMsg(INVALID_GENDER_AND_IDCARD.getDescription());
				return false;
			}
		}
		return true;
	}

	@Override
	public Integer getValidCode(FileAccountImportInfo fileAccountImportInfo) {
		if(!valid(fileAccountImportInfo)){
			return INVALID_GENDER_AND_IDCARD.getKey();
		}
		return null;
	}

	private boolean checkIdCard (FileAccountImportInfo fileAccountImportInfo) {
		if (StringUtils.isNotBlank(fileAccountImportInfo.getFailReason()) &&
				(fileAccountImportInfo.getFailReason().contains(IDCARD_LENGTH_GT_18.getDescription()) ||
						fileAccountImportInfo.getFailReason().contains(INVALID_IDCARD.getDescription()))) {
			return false;
		}
		return true;
	}
}

