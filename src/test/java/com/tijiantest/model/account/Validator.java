package com.tijiantest.model.account;
/**
 * 
 */
	



/**
 * @author ren
 *
 */
public interface Validator {
	
	/*内容验证*/
	public static ImportError SAME_IDCARD = new ImportError(1, "重复的身份证");
	public static ImportError SAME_EMPLOYEEID = new ImportError(2, "重复员工号");
	public static ImportError SAME_NAME_GENDER_AGE = new ImportError(3, "重复的姓名，性别，年龄");
	public static ImportError NULL_NAME = new ImportError(4, "姓名为空");
	public static ImportError NULL_GENDER = new ImportError(5, "性别为空");
	public static ImportError NULL_AGE = new ImportError(6, "年龄为空");
	public static ImportError NULL_IDCARD_NAME = new ImportError(7, "身份证、姓名都要填写");
	public static ImportError NULL_IDCARD_AND_AGE = new ImportError(8, "身份证和年龄同时为空");
	public static ImportError NULL_NAME_GENDER_AGE = new ImportError(9, "姓名、性别、年龄不能为空");
	public static ImportError NULL_EMPLOYEEID_NAME_GENDER = new ImportError(10, "员工号、姓名、性别都要填写");
	public static ImportError INVALID_MOBILE = new ImportError(11, "手机号错误");
	public static ImportError INVALID_IDCARD = new ImportError(12, "身份证信息有误");
	public static ImportError INVALID_GENDER = new ImportError(13, "性别必须是男或女");
	public static ImportError INVALID_MARRY = new ImportError(14, "婚否信息无法识别");
	public static ImportError INVALID_AGE = new ImportError(15, "年龄应该是1-200的数字");
	public static ImportError INVALID_GENDER_AND_IDCARD = new ImportError(16, "性别与身份证性别不一致");
	public static ImportError IDCARD_LENGTH_GT_18 = new ImportError(17, "身份证位数超过18位");
    public static ImportError NULL_PASSPORT_NAME = new ImportError(18, "护照、姓名都要填写");
	
	/*文件格式验证*/
	public static ImportError COLUMN_ERROR_NOIDCARD = new ImportError(-1, "表格应该包含姓名，年龄/身份证等列");
	public static ImportError COLUMN_ERROR = new ImportError(-2, "表格应该包含姓名，身份证等列");
	public static ImportError EXCEL_ERROR = new ImportError(-3, "表格应该包含姓名，身份证等列");
	public static ImportError EXCEL_NO_DATA = new ImportError(-4, "文件前5张表格中未找到合适的数据，请您检查文件是否正确！表格中应该包含：姓名、年龄/身份证等列！");
	public static ImportError SAME_TITLE_REPEAT = new ImportError(-5, "");//标题重复错误
	
	public boolean valid(FileAccountImportInfo fileAccountImportInfo);
	
	/**
	 * 获取验证编码
	 * @param fileAccountImportInfo
	 * @return
	 */
	public Integer getValidCode(FileAccountImportInfo fileAccountImportInfo);

}
