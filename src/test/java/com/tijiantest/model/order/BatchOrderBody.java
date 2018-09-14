package com.tijiantest.model.order;

import java.util.List;

import com.tijiantest.base.ConfDefine;
import com.tijiantest.base.dbcheck.CompanyChecker;
import com.tijiantest.base.dbcheck.HospitalChecker;
import com.tijiantest.model.company.HospitalCompany;
import com.tijiantest.model.hospital.HospitalPeriodSetting;

public class BatchOrderBody {

	private boolean sitePay;
	private boolean hidePrice;
	private boolean reduceItem;
	private boolean changedate;
	private boolean verifyFlag;

	private int hospitalId;
	private int bookType;
	private int mealId;	
	private int mealPrice;
	private int mealGender;
	private int companyId;	
	private String companyName;
	private Integer examTimeIntervalId;
	
	private String examTimeIntervalName;
	private String timeRemarks;
	private String queryCondition;
	private String mealName;
	private String hospitalName;
	private String smsMsgTemplate;

	private List<Integer> accountIdList;
	private String examDate;
	private List<Integer> examItemIdList;
	
	
	public BatchOrderBody() {
		super();
		// TODO Auto-generated constructor stub
	}


	/**
	 * 
	 * @param hospitalId
	 * @param mealId
	 * @param mealPrice
	 * @param mealGender
	 * @param companyId
	 * @param examTimeIntervalId
	 * @param examTimeIntervalName
	 * @param mealName
	 * @param hospitalName
	 * @param accountIdList
	 * @param examDate
	 * @param examItemIdList
	 */
	public BatchOrderBody(Integer hospitalId,String hospitalName, Integer mealId, int mealPrice,
			int mealGender, int companyId, String companyName,Integer examTimeIntervalId,
			String examTimeIntervalName, String mealName, 
			List<Integer> accountIdList, String examDate,List<Integer> examItemIdList) {
		this(hospitalId,hospitalName,mealId,mealPrice,mealGender,companyId,companyName,mealName,accountIdList,examDate,examItemIdList);
		this.examTimeIntervalName  = examTimeIntervalName;
		this.examTimeIntervalId = examTimeIntervalId;
	}

	
	/**
	 * 
	 * @param hospitalId
	 * @param hospitalName
	 * @param mealId
	 * @param mealPrice
	 * @param mealGender
	 * @param companyId
	 * @param mealName
	 * @param accountIdList
	 * @param examDate
	 * @param examItemIdList
	 */
	public BatchOrderBody(int hospitalId, String hospitalName,int mealId, int mealPrice,
			int  mealGender, int companyId,String companyName,
			 String mealName, 
			List<Integer> accountIdList, String examDate,List<Integer> examItemIdList) {
		this(hospitalId,hospitalName,mealId,mealPrice,mealGender,mealName,companyId,companyName,accountIdList,examDate);
		this.setExamItemIdList(examItemIdList);
	}
	
	
	
	/**
	 * 
	 * @param hospitalId
	 * @param mealId
	 * @param mealPrice
	 * @param mealGender
	 * @param companyId
	 * @param examTimeIntervalId
	 * @param examTimeIntervalName
	 * @param mealName
	 * @param hospitalName
	 * @param accountIdList
	 * @param examDate
	 */
	public BatchOrderBody(int hospitalId, String hospitalName, int mealId, int mealPrice,
			int mealGender, 
			 String mealName,int companyId,String companyName,
			List<Integer> accountIdList, String examDate) {
		super();
		this.hospitalId = hospitalId;
		this.mealId = mealId;
		this.mealPrice = mealPrice;
		this.mealGender = mealGender;
		this.companyId = companyId;
		this.companyName = companyName;
		this.mealName = mealName;
		this.hospitalName = hospitalName;
		this.accountIdList = accountIdList;
		this.examDate = examDate;
		this.sitePay = false;
		this.hidePrice = false;
		this.reduceItem = false;
		this.changedate = true;
		this.verifyFlag = false;
		this.smsMsgTemplate = ConfDefine.SUC_ORDER_TEMP;
		this.bookType = 1;
		this.queryCondition = "";
		List<HospitalPeriodSetting> settins = HospitalChecker.getHospitalPeriodSettings(hospitalId);
		this.examTimeIntervalName = settins.get(0).getName();
		this.examTimeIntervalId = settins.get(0).getId();
		this.timeRemarks = this.examDate + " " + this.examTimeIntervalName;
	}

	public BatchOrderBody(int hospitalId, String hospitalName, int mealId, int mealPrice,
						  int mealGender,
						  String mealName,int companyId,String companyName,
						  List<Integer> accountIdList, String examDate,int examTimeIntervalId) {
		super();
		this.hospitalId = hospitalId;
		this.mealId = mealId;
		this.mealPrice = mealPrice;
		this.mealGender = mealGender;
		this.companyId = companyId;
		this.companyName = companyName;
		this.mealName = mealName;
		this.hospitalName = hospitalName;
		this.accountIdList = accountIdList;
		this.examDate = examDate;
		this.sitePay = false;
		HospitalCompany hc = CompanyChecker.getHospitalCompanyById(companyId);
		if(hc != null){
			if(hc.getPlatformCompanyId()!=null && hc.getPlatformCompanyId() == 2)
				this.sitePay = true;
		}
		this.hidePrice = false;
		this.reduceItem = false;
		this.changedate = true;
		this.verifyFlag = false;
		this.smsMsgTemplate = ConfDefine.SUC_ORDER_TEMP;
		this.bookType = 1;
		this.queryCondition = "";
		this.examTimeIntervalId = examTimeIntervalId;
		List<HospitalPeriodSetting> settins = HospitalChecker.getHospitalPeriodSettings(hospitalId,examTimeIntervalId);
		this.examTimeIntervalName = settins.get(0).getName();
		this.timeRemarks = this.examDate + " " + this.examTimeIntervalName;
	}

	public BatchOrderBody(boolean sitePay, boolean hidePrice, boolean reduceItem,
			boolean changedate, boolean verifyFlag, int hospitalId,
			int bookType, int mealId, int mealPrice, int mealGender,
			int companyId, Integer examTimeIntervalId, String examTimeIntervalName,
			String timeRemarks, String queryCondition, String mealName,
			String hospitalName, String smsMsgTemplate,
			List<Integer> accountIdList, String examDate,List<Integer> examItemIdList) {
		super();
		this.sitePay = sitePay;
		this.hidePrice = hidePrice;
		this.reduceItem = reduceItem;
		this.changedate = changedate;
		this.verifyFlag = verifyFlag;
		this.hospitalId = hospitalId;
		this.bookType = bookType;
		this.mealId = mealId;
		this.mealPrice = mealPrice;
		this.mealGender = mealGender;
		this.companyId = companyId;
		this.examTimeIntervalId = examTimeIntervalId;
		this.examTimeIntervalName = examTimeIntervalName;
		this.timeRemarks = timeRemarks;
		this.queryCondition = queryCondition;
		this.mealName = mealName;
		this.hospitalName = hospitalName;
		this.smsMsgTemplate = smsMsgTemplate;
		this.accountIdList = accountIdList;
		this.examDate = examDate;
		this.setExamItemIdList(examItemIdList);
	}

	
	public boolean isSitePay() {
		return sitePay;
	}
	public void setSitePay(boolean sitePay) {
		this.sitePay = sitePay;
	}
	public boolean isHidePrice() {
		return hidePrice;
	}
	public void setHidePrice(boolean hidePrice) {
		this.hidePrice = hidePrice;
	}
	public boolean isReduceItem() {
		return reduceItem;
	}
	public void setReduceItem(boolean reduceItem) {
		this.reduceItem = reduceItem;
	}
	public boolean isChangedate() {
		return changedate;
	}
	public void setChangedate(boolean changedate) {
		this.changedate = changedate;
	}
	public boolean isVerifyFlag() {
		return verifyFlag;
	}
	public void setVerifyFlag(boolean verifyFlag) {
		this.verifyFlag = verifyFlag;
	}
	public int getHospitalId() {
		return hospitalId;
	}
	public void setHospitalId(int hospitalId) {
		this.hospitalId = hospitalId;
	}
	public int getBookType() {
		return bookType;
	}
	public void setBookType(int bookType) {
		this.bookType = bookType;
	}
	public int getMealId() {
		return mealId;
	}
	public void setMealId(int mealId) {
		this.mealId = mealId;
	}
	public int getMealPrice() {
		return mealPrice;
	}
	public void setMealPrice(int mealPrice) {
		this.mealPrice = mealPrice;
	}
	public int getMealGender() {
		return mealGender;
	}
	public void setMealGender(int mealGender) {
		this.mealGender = mealGender;
	}
	public int getCompanyId() {
		return companyId;
	}
	public void setCompanyId(int companyId) {
		this.companyId = companyId;
	}
	public Integer getExamTimeIntervalId() {
		return examTimeIntervalId;
	}
	public void setExamTimeIntervalId(Integer examTimeIntervalId) {
		this.examTimeIntervalId = examTimeIntervalId;
	}
	public String getExamTimeIntervalName() {
		return examTimeIntervalName;
	}
	public void setExamTimeIntervalName(String examTimeIntervalName) {
		this.examTimeIntervalName = examTimeIntervalName;
	}
	public String getTimeRemarks() {
		return timeRemarks;
	}
	public void setTimeRemarks(String timeRemarks) {
		this.timeRemarks = timeRemarks;
	}
	public String getQueryCondition() {
		return queryCondition;
	}
	public void setQueryCondition(String queryCondition) {
		this.queryCondition = queryCondition;
	}
	public String getMealName() {
		return mealName;
	}
	public void setMealName(String mealName) {
		this.mealName = mealName;
	}
	public String getHospitalName() {
		return hospitalName;
	}
	public void setHospitalName(String hospitalName) {
		this.hospitalName = hospitalName;
	}
	public String getSmsMsgTemplate() {
		return smsMsgTemplate;
	}
	public void setSmsMsgTemplate(String smsMsgTemplate) {
		this.smsMsgTemplate = smsMsgTemplate;
	}
	public List<Integer> getAccountIdList() {
		return accountIdList;
	}
	public void setAccountIdList(List<Integer> accountIdList) {
		this.accountIdList = accountIdList;
	}
	public String getExamDate() {
		return examDate;
	}
	public void setExamDate(String examDate) {
		this.examDate = examDate;
	}


	public List<Integer> getExamItemIdList() {
		return examItemIdList;
	}


	public void setExamItemIdList(List<Integer> examItemIdList) {
		this.examItemIdList = examItemIdList;
	}


	public String getCompanyName() {
		return companyName;
	}


	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	
}
