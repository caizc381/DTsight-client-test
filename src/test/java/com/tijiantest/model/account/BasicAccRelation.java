package com.tijiantest.model.account;
/**
 * 
 * @author ChenSijia
 *
 */
public class BasicAccRelation {
		private int companyId;
		private String group;
		private String idCard;
		private String name;
		private int managerId;
		private String mobile;
		private int customerId;
		private int type;
		public BasicAccRelation(int companyId,String group,String idCard,String name,int managerId,String mobile,int customerId,int type ){
			this.companyId=companyId;
			this.group=group;
			this.idCard=idCard;
			this.name=name;
			this.managerId=managerId;
			this.mobile=mobile;
			this.customerId=customerId;
			this.type=type;
		}
		public int getCompanyId() {
			return companyId;
		}
		public void setCompanyId(int companyId) {
			this.companyId = companyId;
		}
		public String getGroup() {
			return group;
		}
		public void setGroup(String group) {
			this.group = group;
		}
		public String getIdCard() {
			return idCard;
		}
		public void setIdCard(String idCard) {
			this.idCard = idCard;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public int getManagerId() {
			return managerId;
		}
		public void setManagerId(int managerId) {
			this.managerId = managerId;
		}
		public String getMobile() {
			return mobile;
		}
		public void setMobile(String mobile) {
			this.mobile = mobile;
		}
		public int getCustomerId() {
			return customerId;
		}
		public void setCustomerId(int customerId) {
			this.customerId = customerId;
		}
		public int getType() {
			return type;
		}
		public void setType(int type) {
			this.type = type;
		}
}
