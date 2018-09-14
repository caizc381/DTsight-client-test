package com.tijiantest.model.account;

import java.util.List;

/**
 * 
 * @author ChenSijia
 *
 */
public class AcctRelations {
	// 体检中心
		private Integer companyId;

		private Integer newCompanyId;

		private Integer organizationType;
		
		// 客户列表
		private List<Integer> customerIds; 
		
		// 1:预约人 2:体检人
		private int type;
		
		// 所在组
		private String group;
		
		// 所在部门
		private String department;

		public Integer getOrganizationType() {
			return organizationType;
		}

		public void setOrganizationType(Integer organizationType) {
			this.organizationType = organizationType;
		}

		public Integer getNewCompanyId() {
	        return newCompanyId;
	    }

	    public void setNewCompanyId(Integer newCompanyId) {
	        this.newCompanyId = newCompanyId;
	    }

	    public Integer getCompanyId() {
			return companyId;
		}

		public void setCompanyId(Integer companyId) {
			this.companyId = companyId;
		}

		public List<Integer> getCustomerIds() {
			return customerIds;
		}

		public void setCustomerIds(List<Integer> customerIds) {
			this.customerIds = customerIds;
		}

		public int getType() {
			return type;
		}

		public void setType(int type) {
			this.type = type;
		}

		public String getGroup() {
			return group;
		}

		public void setGroup(String group) {
			this.group = group;
		}

		public String getDepartment() {
			return department;
		}

		public void setDepartment(String department) {
			this.department = department;
		}

}
