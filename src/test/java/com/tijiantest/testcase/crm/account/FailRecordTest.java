package com.tijiantest.testcase.crm.account;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.alibaba.fastjson.JSON;
import com.tijiantest.base.HttpResult;
import com.tijiantest.base.dbcheck.HospitalChecker;
import com.tijiantest.model.account.AccountRelationInCrm;
import com.tijiantest.model.account.AcctRelationQueryDto;
import com.tijiantest.model.account.AddAccountTypeEnum;
import com.tijiantest.model.account.FileAccountImportInfo;
import com.tijiantest.util.CvsFileUtil;
import com.tijiantest.util.IdCardGeneric;
import com.tijiantest.util.db.DBMapper;
import com.tijiantest.util.db.SqlException;
import com.tijiantest.util.validator.MobileValidator;

/**
 * 
 * @author jiang yiwei
 *
 */
public class FailRecordTest extends AccountBase {

	List<FileAccountImportInfo> fileAccountImportInfos = new ArrayList<>();
	String idCards = "";
	int companyId;
	int organizationType;

	List<Integer> customerIds = new ArrayList<>();

	@SuppressWarnings({ "unchecked", "static-access" })
	@Test(description = "验证修改导入异常的记录", dataProvider = "failRecord_update_success", groups = { "qa", "online" },
			enabled = false)
	public void test_failrecord_update_success(String... args) throws ParseException, IOException, SqlException {

		int hospitalId = defhospital.getId();
		companyId = defnewcompany.getId();
		String groupname = args[1];
		String fileName = args[2];
		// accountNames
		String accountnames = args[3];
		// idCard
		idCards = args[4];
		String newName = args[5];
		String employeeId = args[6];
		String age = args[7];
		String gender = args[8];
		IdCardGeneric generic = new IdCardGeneric();
		String newIdCard = generic.generateGender(Integer.parseInt(gender));
		String message = args[10];
		String description = args[11];

		Map<String, Object> idMaps = this.uploadAccount(companyId, hospitalId, groupname, fileName,
				accountnames, idCards,AddAccountTypeEnum.idCard);

		fileAccountImportInfos = (List<FileAccountImportInfo>) idMaps.get("failAccounts");
		FileAccountImportInfo info = fileAccountImportInfos.get(0);

		// make parameters
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("hospitalId", defhospital.getId());

		FileAccountImportInfo fileAccImportInfo = new FileAccountImportInfo();
		fileAccImportInfo.setAddAccountType(info.getAddAccountType());
		fileAccImportInfo.setAddress(info.getAddress());
		fileAccImportInfo.setAge(age);
		fileAccImportInfo.setCompanyId(info.getCompanyId());
		fileAccImportInfo.setCreateTime(new Date());
		fileAccImportInfo.setDepartment(info.getDepartment());
		fileAccImportInfo.setEmployeeId(employeeId);
		fileAccImportInfo.setFailReason(info.getFailReason());
		fileAccImportInfo.setGender(gender);
		fileAccImportInfo.setGroup(groupname);
		fileAccImportInfo.setId(info.getId());
		if (info.getAddAccountType().equals("idCard")) {
			IdCardGeneric g = new IdCardGeneric();
			newIdCard = g.generateGender(Integer.valueOf(gender));
		}
		fileAccImportInfo.setIdCard(newIdCard);
		fileAccImportInfo.setIdType(info.getIdType());
		fileAccImportInfo.setManagerId(info.getManagerId());
		fileAccImportInfo.setMarriageStatus(info.getMarriageStatus());
		//fileAccImportInfo.setMobile(info.getMobile());
		fileAccImportInfo.setInitialMobile(info.getMobile());
		fileAccImportInfo.setName(newName+((int) (Math.random() * 90000) + 1000));
		fileAccImportInfo.setNewCompanyId(companyId);
		fileAccImportInfo.setOperator(info.getOperator());
		fileAccImportInfo.setOrganizationId(defhospital.getId());
		organizationType = HospitalChecker.getOrganizationType(defhospital.getId());
		fileAccImportInfo.setOrganizationType(organizationType);
		fileAccImportInfo.setPosition(info.getPosition());
		fileAccImportInfo.setRetire(info.getRetire());
		fileAccImportInfo.setSequence(info.getSequence());
		fileAccImportInfo.setSheetName(info.getSheetName());
		fileAccImportInfo.setUpdateTime(new Date());
		fileAccImportInfo.setCreateTime(new Date());
		fileAccImportInfo.setManagerId(defaccountId);
		String jbody = JSON.toJSONString(fileAccImportInfo);

		// post
		HttpResult response = httpclient.post(Account_FailRecord, params, jbody);
		System.out.println(response.getBody());
		// Assert
		Assert.assertEquals(response.getCode(), HttpStatus.SC_OK, description);
		Assert.assertEquals(response.getBody(), message);
		
		MobileValidator mobileValidator = new MobileValidator();
		Boolean isStandard = mobileValidator.valid(fileAccImportInfo);

		// database
		if (checkdb) {
//						// tb_account
//						String accountSql = "select * from tb_account where idcard = ? or employee_id = ? ";
//						log.info("身份证号码:"+newIdCard);
//						List<Map<String, Object>> accountList = DBMapper.query(accountSql, newIdCard,employeeId);
//						int accountId = Integer.parseInt(accountList.get(0).get("id").toString());
//						Assert.assertEquals(newIdCard, accountList.get(0).get("idCard"));
//						Assert.assertEquals(newName, accountList.get(0).get("name"));
//
//						// tb_examiner
//						String relationSql = "select * from tb_examiner where customer_id=? and manager_id=? and company_id=?";
//						List<Map<String, Object>> relationList = DBMapper.query(relationSql, accountId, defaccountId, companyId);
//						Assert.assertEquals(newName, relationList.get(0).get("name"));
//						Assert.assertEquals(companyId, relationList.get(0).get("company_id"));
//						Assert.assertEquals(groupname, relationList.get(0).get("igroup"));
//						Assert.assertEquals(newCompanyId, relationList.get(0).get("new_company_id"));
//						Assert.assertEquals(defhospital.getId(), relationList.get(0).get("organization_id"));
//						Assert.assertEquals(organizationType, relationList.get(0).get("organization_type"));
//						Assert.assertTrue(relationList.get(0).get("add_account_type") == null
//								|| relationList.get(0).get("add_account_type").equals(info.getAddAccountType()));
//
//
//						// tb_user
//						String userSql = "select * from tb_user where account_id= ? ";
//						
//						List<Map<String, Object>> userList = DBMapper.query(userSql,accountId);
//						if (info.getAddAccountType().equals("idCard")) {
//							// 身份证导入
//							Assert.assertEquals(newIdCard, userList.get(0).get("username"));
//						} else if (info.getAddAccountType().equals("employeeNo")) {
//							// 员工号导入
//							Assert.assertEquals(employeeId,
//									accountList.get(0).get("employee_id") == null ? "" : accountList.get(0).get("employee_id"));
//							Assert.assertEquals(newCompanyId + employeeId, relationList.get(0).get("employee_id"));
//							Assert.assertEquals(newCompanyId + employeeId, userList.get(0).get("username"));
//						} else {
//							// 其他方式导入
//							Assert.assertTrue(userList.get(0).get("username").toString().contains(accountId+""));
//						}
//						//tb_accounting
//						String accountingSql = "select * from tb_accounting where account_id = ?";
//						List<Map<String, Object>> accountingList = DBMapper.query(accountingSql, accountId);
//						Assert.assertEquals(accountingList.size(),1);
//						int accoutingTradeId = Integer.parseInt(accountingList.get(0).get("trade_account_id").toString());
//						//tb_trade_account
//						String tradeSql = "select * from tb_trade_account where ref_id = ?";
//						List<Map<String, Object>> tradeList = DBMapper.query(tradeSql, accountId);
//						Assert.assertEquals(tradeList.size(),1);
//						log.info("创建新账户总账户id..."+tradeList.get(0).get("id"));
//						Assert.assertEquals(Integer.parseInt(tradeList.get(0).get("id").toString()),accoutingTradeId);		

			AcctRelationQueryDto acctRelationQueryDto = new AcctRelationQueryDto();
			acctRelationQueryDto.setManagerId(fileAccImportInfo.getManagerId());
			if (fileAccImportInfo.getAddAccountType().equals("idCard")) {
				acctRelationQueryDto.setIdCard(fileAccImportInfo.getIdCard());
			}
			
			acctRelationQueryDto.setNewCompanyId(fileAccImportInfo.getNewCompanyId());
			acctRelationQueryDto.setEmployeeId(fileAccImportInfo.getEmployeeId());
			acctRelationQueryDto.setName(fileAccImportInfo.getName());
			acctRelationQueryDto.setGender(Integer.valueOf(gender));
			acctRelationQueryDto.setIsDelete(false);

			List<AccountRelationInCrm> accountRelationInCrm = getCustomersInCRM(acctRelationQueryDto);

			for (int i = 0; i < accountRelationInCrm.size(); i++) {
				System.out.println("========================" + description + "=====================");
				System.out.println(accountRelationInCrm.get(i).getCustomerId());
			}
			Assert.assertEquals(accountRelationInCrm.get(0).getGroup(), fileAccImportInfo.getGroup());
			Assert.assertEquals(accountRelationInCrm.get(0).getName(), fileAccImportInfo.getName());
			if (isStandard) {
				//手机号未超过30位，返回true
				if (mobileValidator.isMobile(info.getMobile())) {
					Assert.assertEquals(accountRelationInCrm.get(0).getMobile(), fileAccImportInfo.getMobile());
				}else{
					Assert.assertNull(accountRelationInCrm.get(0).getMobile());
				}
				Assert.assertEquals(accountRelationInCrm.get(0).getInitialMobile(), fileAccImportInfo.getInitialMobile());
			}
			
			Assert.assertEquals(accountRelationInCrm.get(0).getType().intValue(), 1);
			if (fileAccImportInfo.getAddAccountType().equals("idCard")) {
				Assert.assertEquals(accountRelationInCrm.get(0).getIdCard(), newIdCard);
			}
			
			if (info.getAddAccountType().equals("other")) {
				// 其他方式，员工号为空
				Assert.assertEquals(accountRelationInCrm.get(0).getEmployeeId(), "");
			} else {
				Assert.assertEquals(accountRelationInCrm.get(0).getEmployeeId(), employeeId);
			}

			Assert.assertEquals(accountRelationInCrm.get(0).getNewCompanyId(), new Integer(companyId));
			Assert.assertEquals(accountRelationInCrm.get(0).getOrganizationId(), new Integer(defhospital.getId()));
			Assert.assertEquals(accountRelationInCrm.get(0).getOrganizationType(), new Integer(organizationType));

			customerIds.add(accountRelationInCrm.get(0).getCustomerId());
		}
	}

	// @Test(description = "验证删除导入异常的记录", dataProvider =
	// "failRecord_delete_success", groups = { "qa", "online" })
	public void test_failrecord_delete_success(String... args) throws ParseException, IOException, SqlException {
		int hospitalId = defhospital.getId();
		Integer companyId = null;
		if(args[1].equals("SKXC"))
			companyId = defSKXCnewcompany.getId();
		String groupname = args[2];
		String fileName = args[3];
		// accountNames
		String accountnames = "\'" + args[4].replace("#", "\',\'") + "\'";
		// idCard
		idCards = args[5].replace("#", ",");
		String message = args[6];

		Map<String, Object> idMaps = this.uploadAccount(companyId, hospitalId, groupname, fileName,
				accountnames, idCards,AddAccountTypeEnum.idCard);

		@SuppressWarnings("unchecked")
		List<FileAccountImportInfo> failAccounts = (List<FileAccountImportInfo>) idMaps.get("failAccounts");
		String deleteIds = "";
		for (FileAccountImportInfo failAccount : failAccounts) {
			deleteIds += failAccount.getId() + ",";
		}
		int lastIndex = deleteIds.lastIndexOf(",");
		deleteIds = deleteIds.substring(0, lastIndex);
		// make parameters
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("companyId", companyId);
		params.put("newCompanyId", companyId);
		params.put("organizationType", HospitalChecker.getOrganizationType(defhospital.getId()));
		params.put("ids", deleteIds);

		// delete
		HttpResult response = httpclient.get(Account_RemoveFailRecord, params);

		// Assert
		Assert.assertEquals(response.getCode(), HttpStatus.SC_OK);
		Assert.assertEquals(response.getBody(), message);

		// database
		if (checkdb) {
			String sql = "select * from tb_account_relationship_fail where new_company_id = ? and id=? ";
			List<Map<String, Object>> list = DBMapper.query(sql, companyId, deleteIds);
			Assert.assertEquals(list.size(), 0);
		}
	}

	@AfterTest(description = "删除客户", groups = { "qa" }, alwaysRun = true)
	public void doAfter() throws SqlException {
		if (customerIds.size() > 0) {
			for (int i = 0; i < customerIds.size(); i++) {
				// delete
				
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("accountIds", customerIds.get(i)+""));
				params.add(new BasicNameValuePair("newCompanyId", companyId+""));
				params.add(new BasicNameValuePair("organizationType",organizationType+""));
				
				HttpResult delete = httpclient.post(Account_RemoveCustomer, params);
				String body = delete.getBody();
				System.out.println(body);
				System.out.println("===============删除用户customer_id=" + customerIds.get(i) + "=====================");
				Assert.assertEquals(delete.getCode(), HttpStatus.SC_OK);

				// database
				if (checkdb) {
					waitto(5);
					String sql = "select * from tb_examiner where customer_id=?";
					List<Map<String, Object>> list = DBMapper.query(sql, customerIds.get(i));
					Assert.assertEquals(1, list.get(0).get("is_delete"));

				}
			}
		}
	}

	@DataProvider
	public Iterator<String[]> failRecord_update_success() {
		try {
			return CvsFileUtil.fromCvsFileToIterator("./csv/account/failrecord_update.csv", 11);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	@DataProvider
	public Iterator<String[]> failRecord_delete_success() {
		try {
			return CvsFileUtil.fromCvsFileToIterator("./csv/account/failrecord_delete.csv", 7);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

}