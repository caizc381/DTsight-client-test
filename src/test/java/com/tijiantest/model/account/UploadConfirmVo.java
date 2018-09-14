package com.tijiantest.model.account;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * @author ren
 *
 */
public class UploadConfirmVo {
	
	/**
	 * 选择导入sheet
	 */
	private List<String> sheetNames;
	
	/**
	 * 批量导入关联文件地址
	 */
    private String filePath; 
    
    /**
     * 批量导入关联体检单位id
     */
    private Integer companyId;
    

	private Integer newCompanyId;
    
    /**
     * 体检中心id
     */
    private Integer hospitalId;
    
    /**
     * 批量导入分组信息
     */
    private String group;
    
    private AddAccountTypeEnum addAccountType;
    
    /**
     * key: sheet name
	 * value: column map
     */
    private Map<String, Map<String, Integer>> sheetColumnMap;
    
    private Integer organizationType;

	public UploadConfirmVo() {
		super();
	}

	public UploadConfirmVo(List<String> sheetNames, String filePath, Integer companyId,Integer newCompanyId,Integer organizationType,Integer hospitalId,
			String group, Map<String, Map<String, Integer>> sheetColumnMap,AddAccountTypeEnum addAccountType) {
		super();
		this.sheetNames = sheetNames;
		this.filePath = filePath;
		this.companyId = companyId;
		this.newCompanyId = newCompanyId;
		this.organizationType = organizationType;
		this.hospitalId = hospitalId;
		this.group = group;
		this.sheetColumnMap = sheetColumnMap;
		this.addAccountType = addAccountType;
	}

	public List<String> getSheetNames() {
		return sheetNames;
	}

	public void setSheetNames(List<String> sheetNames) {
		this.sheetNames = sheetNames;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public Integer getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Integer companyId) {
		this.companyId = companyId;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public Map<String, Map<String, Integer>> getSheetColumnMap() {
		return sheetColumnMap;
	}

	public void setSheetColumnMap(Map<String, Map<String, Integer>> sheetColumnMap) {
		this.sheetColumnMap = sheetColumnMap;
	}

	public Integer getHospitalId() {
		return hospitalId;
	}

	public void setHospitalId(Integer hospitalId) {
		this.hospitalId = hospitalId;
	}

	public Integer getNewCompanyId() {
		return newCompanyId;
	}

	public void setNewCompanyId(Integer newCompanyId) {
		this.newCompanyId = newCompanyId;
	}

	public Integer getOrganizationType() {
		return organizationType;
	}

	public void setOrganizationType(Integer organizationType) {
		this.organizationType = organizationType;
	}
	
    public AddAccountTypeEnum getAddAccountType() {
		return addAccountType;
	}

	public void setAddAccountType(AddAccountTypeEnum addAccountType) {
		this.addAccountType = addAccountType;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this); 
	}
    
}
