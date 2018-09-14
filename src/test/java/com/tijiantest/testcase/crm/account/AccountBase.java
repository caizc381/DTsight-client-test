package com.tijiantest.testcase.crm.account;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.tijiantest.base.dbcheck.AccountChecker;
import com.tijiantest.model.account.AccountRelationInCrm;
import com.tijiantest.model.account.AcctRelationQueryDto;
import com.tijiantest.model.account.AddAccountTypeEnum;
import com.tijiantest.model.account.FileAccountImportInfo;
import com.tijiantest.model.resource.Address;
import com.tijiantest.testcase.crm.CrmBase;
import com.tijiantest.util.db.DBMapper;
import com.tijiantest.util.db.SqlException;

public class AccountBase extends CrmBase {

	// 上传用户，返回上传用户对象
	public Map<String, Object> uploadAccount(int companyId, int hospitalId, String groupname, String fileName,
			String accountNames, String idCards,AddAccountTypeEnum addAccountType) throws IOException, SqlException {
		AccountChecker.uploadAccount(httpclient, companyId, hospitalId, groupname, fileName,addAccountType);
		Map<String, Object> map = new HashMap<>();

		// 获取失败列表的数据
		String sql = "SELECT  DISTINCT * FROM tb_account_relationship_fail a WHERE" 
				+ " a.igroup = \'" + groupname + "\' AND a.name='" + accountNames + "' AND a.manager_id = ? ";
		if (idCards!=null &&!idCards.equals("")) {
			sql +=" AND a.idcard='"+idCards+"'";
		}
		sql+=" order by id desc";
		
		log.info("sql:"+sql);
		List<Map<String, Object>> failRecords = DBMapper.query(sql, defaccountId);
		List<FileAccountImportInfo> list = getFailAccounts(failRecords);
		map.put("failAccounts", list);

		// 获取成功列表的数据
		sql = "SELECT  DISTINCT a.id,c.name,c.mobile,c.idCard,c.employee_id,a.status,a.type,a.id_type,a.system, c.gender,c.marriagestatus ,c.company_id,c.department,c.sheet_name FROM tb_account a  left join tb_examiner c on a.id=c.customer_id WHERE "
				+ " c.igroup = \'" + groupname + "\' AND c.name ='" + accountNames
				+ "' AND c.manager_id = ? and c.is_delete=0";
		if (idCards!=null &&!idCards.equals("")) {
			sql+=" AND a.idcard in (" + idCards+")";
		}
		log.info("sql:" + sql);
		List<Map<String, Object>> aclist = DBMapper.query(sql, defaccountId);
		List<FileAccountImportInfo> fileAccountImportInfos= getSuccessAccounts(aclist);
		map.put("successAccounts", fileAccountImportInfos);

		return map;
	}

	public List<FileAccountImportInfo> getFailAccounts(List<Map<String, Object>> list) {
		List<FileAccountImportInfo> failAccountList = new ArrayList<>();
		for (Map<String, Object> map : list) {
			FileAccountImportInfo failAccount = new FileAccountImportInfo();
			failAccount.setAddAccountType(map.get("add_account_type").toString());
			failAccount.setAge(map.get("age").toString());
			if(map.get("company_id")!=null)
				failAccount.setCompanyId(Integer.valueOf(map.get("company_id").toString()));
			failAccount.setDepartment(map.get("department").toString());
			failAccount.setEmployeeId(map.get("employee_id").toString());
			failAccount.setFailReason(map.get("fail_reason").toString());
			failAccount.setGender(map.get("gender").toString());
			failAccount.setGroup(map.get("igroup").toString());
			failAccount.setId(map.get("id").toString());
			failAccount.setIdCard(map.get("idCard").toString());
			failAccount.setManagerId(Integer.valueOf(map.get("manager_id").toString()));
			failAccount.setMarriageStatus(map.get("marriageStatus").toString());
			failAccount.setMobile(map.get("mobile").toString());
			failAccount.setName(map.get("name").toString());
			failAccount.setOperator(map.get("operator").toString());
			failAccount.setPosition(map.get("position").toString());
			failAccount.setRetire(map.get("is_retire").toString());
			failAccount.setSequence(Integer.valueOf(map.get("sequence").toString()));
			failAccount.setSheetName(map.get("sheet_name").toString());

			failAccountList.add(failAccount);
		}
		return failAccountList;
	}
	
	public List<FileAccountImportInfo> getSuccessAccounts (List<Map<String, Object>> list){
		List<FileAccountImportInfo> fileAccountImportInfos = new ArrayList<>();
		for (Map<String, Object> map : list) {
			FileAccountImportInfo accountImportInfo = new FileAccountImportInfo();
			accountImportInfo.setName(map.get("name").toString());
			accountImportInfo.setId(map.get("id").toString());
			if (map.get("mobile")!=null) {
				accountImportInfo.setMobile(map.get("mobile").toString());
			}
			if (map.get("idCard")!=null) {
				accountImportInfo.setIdCard(map.get("idCard").toString());
			}			
			accountImportInfo.setEmployeeId(map.get("employee_id").toString());
			accountImportInfo.setIdType(Integer.valueOf(map.get("id_type").toString()));
			accountImportInfo.setGender(map.get("gender").toString());
			accountImportInfo.setMarriageStatus(map.get("marriagestatus").toString());
			accountImportInfo.setCompanyId(Integer.valueOf(map.get("company_id").toString()));
			accountImportInfo.setDepartment(map.get("department").toString());
			accountImportInfo.setSheetName(map.get("sheet_name").toString());
			
			fileAccountImportInfos.add(accountImportInfo);
		}
		return fileAccountImportInfos;
	}
	
	/**
	 *获取N天前的日期 
	 * @param days
	 * @return
	 */
	public Date getDateBeforeDays(Integer days) {
		Calendar calendar = Calendar.getInstance();
		if (null == days) {
			return null;
		}

		if (days == 0) {
			return calendar.getTime();
		}
		calendar.add(Calendar.DATE, -days);
		return calendar.getTime();
	}
	
	public List<AccountRelationInCrm> getCustomersInCRM(AcctRelationQueryDto acctRelationQueryDto){
		List<AccountRelationInCrm> customers = new ArrayList<AccountRelationInCrm>();
		String sql = "SELECT ship.*, a.idcard, a. NAME AS 'accountName', a.mobile AS 'accountMobile', a.employee_id AS 'accountEmployeeId',"
				+ " a. STATUS AS 'accountStatus', a.type AS 'accountType', a.system AS 'accountSystem', a.id_type AS 'accountIdType', "
				+ "ship.address AS 'infoAddress', ship.birthYear AS 'infoBirthYear', ship.email AS 'infoEmail', ship.gender AS 'infoGender',"
				+ "ship.height, ship.marriagestatus AS 'accountMarriageStatus', ship.pinYin, ship.weight "
				+ "FROM tb_examiner ship "
				+ "LEFT JOIN tb_account a ON ship.customer_id = a.id "
				+ "WHERE ship.manager_id = "+acctRelationQueryDto.getManagerId()+" AND a. STATUS >= 0 ";
		if (acctRelationQueryDto.getName()!=null &&!acctRelationQueryDto.getName().equals("")) {
			sql =sql+"and ship.name = '"+acctRelationQueryDto.getName()+"' ";
		}
		if (acctRelationQueryDto.getGender()!=null && !acctRelationQueryDto.getGender().equals("-1")) {
			sql=sql+"and ship.gender="+acctRelationQueryDto.getGender()+" ";
		}else if (acctRelationQueryDto.getGender()!=null&&!acctRelationQueryDto.getGender().equals("-1")) {
			sql =sql+" and ship.gender is null";
		}
		
		if(acctRelationQueryDto.getIdCard()!=null&&!acctRelationQueryDto.getIdCard().equals(""))
			sql = sql + "and a.idcard = '"+acctRelationQueryDto.getIdCard()+"' ";
		if(acctRelationQueryDto.getNewCompanyId()!=null)
			sql = sql + "and ship.new_company_id = "+acctRelationQueryDto.getNewCompanyId()+" ";
		if(acctRelationQueryDto.getOrganizationType()!=null)
			sql = sql + "AND ship.organization_type = "+acctRelationQueryDto.getOrganizationType()+" ";
		if(acctRelationQueryDto.getCustomerId()!=null)
			sql = sql + "AND ship.customer_id = "+acctRelationQueryDto.getCustomerId()+" ";
		if(acctRelationQueryDto.getStartBirthYear()!=null)
			sql = sql + "AND ship.birthYear <= "+acctRelationQueryDto.getStartBirthYear()+" ";
		if(acctRelationQueryDto.getEndBirthYear()!=null)
			sql = sql + "AND ship.birthYear >= "+acctRelationQueryDto.getEndBirthYear()+" ";
		if(acctRelationQueryDto.getGender()!=null&&acctRelationQueryDto.getGender()!=-1)
			sql = sql + "AND ship.gender = "+acctRelationQueryDto.getGender()+" ";
		if(acctRelationQueryDto.getGender()!=null&&acctRelationQueryDto.getGender()==-1)
			sql = sql + "AND ship.gender IS NULL ";
		if(acctRelationQueryDto.getSearchKey()!=null)
			sql = sql + "AND ( "
					+ "ship.name LIKE CONCAT('%',"+acctRelationQueryDto.getSearchKey()+",'%') OR "
					+ "ship.mobile LIKE CONCAT('%',"+acctRelationQueryDto.getSearchKey()+",'%') OR "
					+ "a.idCard LIKE CONCAT('%',"+acctRelationQueryDto.getSearchKey()+",'%') OR "
					+ "ship.pinyin LIKE CONCAT('%',"+acctRelationQueryDto.getSearchKey()+",'%') ";
		if(acctRelationQueryDto.getMarriageStatus()!=null&&acctRelationQueryDto.getMarriageStatus()!=-1)
			sql = sql + "AND ship.marriageStatus = "+acctRelationQueryDto.getMarriageStatus()+" ";
		if(acctRelationQueryDto.getMarriageStatus()!=null&&acctRelationQueryDto.getMarriageStatus()==-1)
			sql = sql + "AND ship.marriageStatus IS NULL ";
		if(acctRelationQueryDto.getGroup()!=null&&acctRelationQueryDto.getGroup()!="-1")
			sql = sql + "AND ship.igroup = "+acctRelationQueryDto.getGroup()+" ";
		if(acctRelationQueryDto.getGroup()!=null&&acctRelationQueryDto.getGroup()=="-1")
			sql = sql + "AND ship.igroup IS NULL OR ship.igroup = '' ";
		if(acctRelationQueryDto.getSheetName()!=null&&acctRelationQueryDto.getSheetName()!="-1")
			sql = sql + "AND ship.sheet_name = "+acctRelationQueryDto.getSheetName()+" ";
		if(acctRelationQueryDto.getSheetName()!=null&&acctRelationQueryDto.getSheetName()=="-1")
			sql = sql + "AND ship.sheet_name IS NULL OR ship.sheet_name = '' ";
		if(acctRelationQueryDto.getDepartment()!=null&&acctRelationQueryDto.getDepartment()!="-1")
			sql = sql + "AND ship.department = "+acctRelationQueryDto.getDepartment()+" ";
		if(acctRelationQueryDto.getDepartment()!=null&&acctRelationQueryDto.getDepartment()=="-1")
			sql = sql + "AND ship.department IS NULL OR ship.department = '' ";
		if(acctRelationQueryDto.getRecentMeal()!=null&&acctRelationQueryDto.getRecentMeal()!="-1")
			sql = sql + "AND ship.recent_meal = "+acctRelationQueryDto.getRecentMeal()+" ";
		if(acctRelationQueryDto.getRecentMeal()!=null&&acctRelationQueryDto.getRecentMeal()=="-1")
			sql = sql + "AND ship.recent_meal IS NULL OR ship.recent_meal = '' ";
		if(acctRelationQueryDto.getEmployeeId()!=null)
			sql = sql + "AND ship.employee_id = '"+acctRelationQueryDto.getEmployeeId()+"' ";
		if(acctRelationQueryDto.getPosition()!=null&&acctRelationQueryDto.getPosition()!="-1")
			sql = sql + "AND ship.position = "+acctRelationQueryDto.getPosition()+" ";
		if(acctRelationQueryDto.getPosition()!=null&&acctRelationQueryDto.getPosition()=="-1")
			sql = sql + "AND ship.position IS NULL OR ship.position = '' ";
		if(acctRelationQueryDto.getIsRetire()!=null&&acctRelationQueryDto.getIsRetire()!=-1)
			sql = sql + "AND ship.is_retire = "+acctRelationQueryDto.getIsRetire()+" ";
		if(acctRelationQueryDto.getIsRetire()!=null&&acctRelationQueryDto.getIsRetire()==-1)
			sql = sql + "AND ship.is_retire IS NULL ";
		if(acctRelationQueryDto.getHasOrdered()&&acctRelationQueryDto.getRecentOrderDate()!=null)
			sql = sql + "AND (ship.recent_order_date >= \""+simplehms.format(acctRelationQueryDto.getRecentOrderDate())+"\" or ship.recent_card is not null) ";
		if(!acctRelationQueryDto.getHasOrdered()&&acctRelationQueryDto.getRecentOrderDate()!=null)
			sql = sql + "AND (ship.recent_order_date <= \""+simplehms.format(acctRelationQueryDto.getRecentOrderDate())+"\" and ship.recent_card is null) ";
		if(acctRelationQueryDto.getRecentCard()!=null&&acctRelationQueryDto.getRecentCard()!="-1")
			sql = sql + "AND ship.recent_card = "+acctRelationQueryDto.getRecentCard()+" ";
		if(acctRelationQueryDto.getRecentCard()!=null&&acctRelationQueryDto.getRecentCard()=="-1")
			sql = sql + "AND ship.recent_card IS NULL ";
		if(acctRelationQueryDto.getBeforeDays()!=null)
			sql = sql + "AND ship.create_time > \""+simplehms.format(acctRelationQueryDto.getDateBeforeDays(acctRelationQueryDto.getBeforeDays()))+"\" ";
		if(acctRelationQueryDto.getIsDelete()!=null){
			Integer delete = acctRelationQueryDto.getIsDelete()?1:0;
			sql = sql + "AND is_delete = "+delete+" ";
		}
		if (acctRelationQueryDto.getSerachMobile()!=null && acctRelationQueryDto.getSerachMobile()==-1) {
			//空
			sql=sql+" AND (ship.initial_mobile is null or ship.initial_mobile = '')";
		}else if (acctRelationQueryDto.getSerachMobile()!=null&& acctRelationQueryDto.getSerachMobile()==0) {
			//非标准手机号
			sql=sql+" AND ((ship.mobile is null or ship.mobile = '') AND (ship.initial_mobile is not null and ship.initial_mobile != ''))";
		}else if (acctRelationQueryDto.getSerachMobile()!=null&& acctRelationQueryDto.getSerachMobile()==1) {
			//标准手机号
			sql = sql+" AND (ship.mobile is not null and ship.mobile != '')";
		}
		
		sql = sql + "ORDER BY ship.create_time DESC, ship.sequence ASC";
		System.out.println("查询客户SQL："+sql);
		List<Map<String, Object>> list = null;
		try {
			list = DBMapper.query(sql);
		} catch (SqlException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if(list!=null&&list.size()>0)
			for(Map<String,Object> m : list){
				AccountRelationInCrm customer = new AccountRelationInCrm();
				customer.setCustomerId(Integer.valueOf(m.get("customer_id").toString()));
				if(m.get("idcard")!=null)
					customer.setIdCard(m.get("idcard").toString());
				customer.setType(Integer.valueOf(m.get("type").toString()));
				customer.setName(m.get("name").toString());
				if(m.get("mobile")!=null)
					customer.setMobile(m.get("mobile").toString());
				if(m.get("email")!=null)
					customer.setEmail(m.get("email").toString());
				if(m.get("address")!=null){
					Address address = new Address();
					address.setAddress(m.get("address").toString());
					customer.setAddress(address);
				}
				if(m.get("birthYear")!=null)
					customer.setBirthYear(Integer.valueOf(m.get("birthYear").toString()));
				if(m.get("marriageStatus")!=null)
					customer.setMarriageStatus(Integer.valueOf(m.get("marriageStatus").toString()));
				if(m.get("company_id")!=null)
					customer.setCompanyId(Integer.valueOf(m.get("company_id").toString()));
				if(m.get("igroup")!=null)
					customer.setGroup(m.get("igroup").toString());
				if(m.get("department")!=null)
					customer.setDepartment(m.get("department").toString());
				if(m.get("sheet_name")!=null)
					customer.setSheetName(m.get("sheet_name").toString());
				if(m.get("recent_card")!=null)
					customer.setRecentCard(Integer.valueOf(m.get("recent_card").toString()));
				if(m.get("recent_meal")!=null)
					customer.setRecentMeal(m.get("recent_meal").toString());
				if(m.get("employee_id")!=null)
					customer.setEmployeeId(m.get("employee_id").toString());
				if(m.get("postion")!=null)
					customer.setPosition(m.get("postion").toString());
				if(m.get("is_retire")!=null)
					customer.setIsRetire(Integer.valueOf(m.get("is_retire").toString()));
				if(m.get("recent_order_date")!=null)
					try {
						customer.setRecentOrderDate(sdf.parse(m.get("recent_order_date").toString()));
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				customer.setSequence(Integer.valueOf(m.get("sequence").toString()));
				if(m.get("health_level")!=null)
					customer.setHealthLevel(m.get("health_level").toString());
				if(m.get("health_num")!=null)
					customer.setHealthNum(m.get("health_num").toString());
				if(m.get("social_security")!=null)
					customer.setSocialSecurity(m.get("social_security").toString());
				if(m.get("operator")!=null)
					customer.setOperator(m.get("operator").toString());
				if(m.get("new_company_id")!=null)
					customer.setNewCompanyId(Integer.valueOf(m.get("new_company_id").toString()));
				if(m.get("organization_id")!=null)
					customer.setOrganizationId(Integer.valueOf(m.get("organization_id").toString()));
				if(m.get("organization_type")!=null)
					customer.setOrganizationType(Integer.valueOf(m.get("organization_type").toString()));
				customer.setId(Integer.valueOf(m.get("id").toString()));
				if(m.get("add_account_type")!=null)
					customer.setAddAccountType(m.get("add_account_type").toString());
				if (m.get("infoGender")!=null) {
					customer.setGender(Integer.valueOf(m.get("infoGender").toString()));
				}
				if (m.get("initial_mobile")!=null) {
					customer.setInitialMobile(m.get("initial_mobile").toString());
				}
				
				customers.add(customer);
			}
		return customers;
	}
	
	public String getUserNameByAccountId(Integer accountId){
		String username="";
		String sql = "SELECT username FROM tb_user WHERE account_id = "+accountId+";";
		List<Map<String, Object>> list = null;
		try {
			list = DBMapper.query(sql);
		} catch (SqlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(list!=null&&list.size()>0)
			for(Map<String,Object> m :list){
				username = username+","+m.get("username").toString();
			}
		if(!username.equals(""))
			return username.substring(1);
		else
			return null;
	}
}
