package com.tijiantest.testcase.crm.account;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.message.BasicNameValuePair;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.alibaba.fastjson.JSON;
import com.tijiantest.base.HttpResult;
import com.tijiantest.base.dbcheck.HospitalChecker;
import com.tijiantest.model.account.AccountRelationInCrm;
import com.tijiantest.model.account.AccountRelationTypeEnum;
import com.tijiantest.model.account.AccountTypeEnum;
import com.tijiantest.model.account.AcctRelationQueryDto;
import com.tijiantest.model.account.FileAccountImportInfo;
import com.tijiantest.model.account.IdTypeEnum;
import com.tijiantest.model.company.HospitalCompany;
import com.tijiantest.util.CvsFileUtil;
import com.tijiantest.util.IdCardGeneric;
import com.tijiantest.util.PinYinUtil;
import com.tijiantest.util.db.DBMapper;
import com.tijiantest.util.db.SqlException;
import com.tijiantest.util.validator.MobileValidator;

/**
 * 
 * @author ChenSijia
 *
 */
public class CustomerTest extends AccountBase {
	HospitalCompany hospitalCompany = new HospitalCompany();
	Integer organizationType;
	FileAccountImportInfo fileAccountImportInfo = new FileAccountImportInfo();
	List<Integer> customerId = new ArrayList<>();
	boolean isExist = false;// 默认数据库不存在该身份证

	@SuppressWarnings("static-access")
	@Test(description = "单个添加用户 - 身份证/员工号/其他方式", dataProvider = "customer_Add_success", groups = { "qa", "online",
			"crm_addCustomer" },enabled = false)
	public void test_01_customer_Add_success(String... args) throws ParseException, IOException, SqlException {
		// get input & output from casefile
		String addAccountType = args[1];
		String age = args[2];
		int companyId = defSKXCnewcompany.getId();//Integer.parseInt(args[3]);
//		HospitalCompany hospitalCompany = defSKXCnewcompany;
		int organizationType = HospitalChecker.getOrganizationType(defhospital.getId());
		int organizationId = defhospital.getId();
		String employeeId = args[4];
		String group = args[5];
		int gender = Integer.parseInt(args[8]);
		String address = args[10];
		String description = args[11];
		String idCard = new IdCardGeneric().generateGender(gender);
		String name = args[7];
		String mobile = args[9];

		fileAccountImportInfo.setCustomerId(null);
		fileAccountImportInfo.setManagerId(defaccountId);
		fileAccountImportInfo.setAddAccountType(addAccountType);
		fileAccountImportInfo.setAge(age);
		fileAccountImportInfo.setCompanyId(companyId);
		fileAccountImportInfo.setNewCompanyId(companyId);
		fileAccountImportInfo.setOrganizationId(organizationId);
		fileAccountImportInfo.setOrganizationType(organizationType);

		fileAccountImportInfo.setGroup(group);
		fileAccountImportInfo.setIdCard(idCard);
		if (addAccountType.equals("employeeNo")) {
			employeeId = employeeId + ((int) (Math.random() * 90000) + 1000);
		}
		if (addAccountType.equals("other")) {
			name = name + ((int) (Math.random() * 90000) + 1000);
		}
		fileAccountImportInfo.setEmployeeId(employeeId);
		fileAccountImportInfo.setName(name);
		fileAccountImportInfo.setGender(gender + "");
		// fileAccountImportInfo.setMobile(mobile);
		fileAccountImportInfo.setInitialMobile(mobile);
		fileAccountImportInfo.setAddress(address);

		String jbody = JSON.toJSONString(fileAccountImportInfo);

		// 先tb_account中是否已经有了该身份证号
		String sql = "select * from tb_account where idcard=?";
		List<Map<String, Object>> list = DBMapper.query(sql, idCard);

		if (!list.isEmpty()) {
			isExist = true;// 数据库已经有了该身份证号
		}

		// post
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("hospitalId", defhospital.getId());
		HttpResult response = httpclient.post(Account_Customer, params, jbody);
		System.out.println(response.getBody());

		// assert
		Assert.assertEquals(response.getCode(), HttpStatus.SC_OK);
		Assert.assertEquals(response.getBody(), "{}");

		MobileValidator mobileValidator = new MobileValidator();
		Boolean isStandard = mobileValidator.valid(fileAccountImportInfo);

		// databse
		if (checkdb) {
			if (!isExist) {
				log.info("用户不存在,CRM添加单个用户" + idCard + "!");
				// tb_account
				String accountSql = "select * from tb_account where idcard=?";
				List<Map<String, Object>> accountList = DBMapper.query(accountSql, idCard);
				int accountId = Integer.valueOf(accountList.get(0).get("id").toString());
				Assert.assertTrue(!accountList.isEmpty());
				Assert.assertEquals(name, accountList.get(0).get("name"));

				Assert.assertNull(accountList.get(0).get("mobile"));
				Assert.assertEquals(AccountTypeEnum.Import.getCode(),
						Integer.parseInt(accountList.get(0).get("type").toString()));
				Assert.assertEquals(1, Integer.parseInt(accountList.get(0).get("system").toString()));
				if (addAccountType.equals("idCard"))
					Assert.assertEquals(IdTypeEnum.IDCARD.getCode(),
							Integer.parseInt(accountList.get(0).get("id_type").toString()));
				else
					Assert.assertEquals(IdTypeEnum.UNKOWN.getCode(),
							Integer.parseInt(accountList.get(0).get("id_type").toString()));
				// tb_role
				String roleSql = "select * from tb_account_role where account_id=?";
				List<Map<String, Object>> roleList = DBMapper.query(roleSql, accountId);
				Assert.assertEquals(1, Integer.parseInt(roleList.get(0).get("role_id").toString()));

				// tb_examiner
				String relaSql = "select * from tb_examiner where customer_id=? and manager_id=?";
				List<Map<String, Object>> relaList = DBMapper.query(relaSql, accountId, defaccountId);
				Assert.assertEquals(name, relaList.get(0).get("name").toString());
				Assert.assertEquals(AccountRelationTypeEnum.MedicalReserver.getCode(),
						Integer.parseInt(relaList.get(0).get("type").toString()));
				Assert.assertEquals(0, Integer.parseInt(relaList.get(0).get("is_delete").toString()));

				// tb_accounting
				String accountingSql = "select * from tb_accounting where account_id=?";
				List<Map<String, Object>> accountingList = DBMapper.query(accountingSql, accountId);
				Assert.assertTrue(!accountingList.isEmpty());
				Assert.assertEquals(0, Integer.parseInt(accountingList.get(0).get("balance").toString()));

				// tb_trade_account
				String tradeSql = "select * from tb_trade_account where ref_id = ?";
				List<Map<String, Object>> tradeList = DBMapper.query(tradeSql, accountId);
				Assert.assertTrue(!tradeList.isEmpty());
				Assert.assertEquals(accountingList.get(0).get("trade_account_id"), tradeList.get(0).get("id"));
			}
			AcctRelationQueryDto acctRelationQueryDto = new AcctRelationQueryDto();
			acctRelationQueryDto.setManagerId(fileAccountImportInfo.getManagerId());
			acctRelationQueryDto.setIdCard(fileAccountImportInfo.getIdCard());
			acctRelationQueryDto.setNewCompanyId(fileAccountImportInfo.getNewCompanyId());
			acctRelationQueryDto.setEmployeeId(fileAccountImportInfo.getEmployeeId());
			acctRelationQueryDto.setName(fileAccountImportInfo.getName());
			acctRelationQueryDto.setGender(gender);
			acctRelationQueryDto.setIsDelete(false);

			List<AccountRelationInCrm> accountRelationInCrm = getCustomersInCRM(acctRelationQueryDto);

			for (int i = 0; i < accountRelationInCrm.size(); i++) {
				System.out.println("========================" + description + "=====================");
				System.out.println(accountRelationInCrm.get(i).getCustomerId());
			}
			Assert.assertEquals(accountRelationInCrm.size(), 1, description);
			Assert.assertEquals(accountRelationInCrm.get(0).getGroup(), fileAccountImportInfo.getGroup());
			Assert.assertEquals(accountRelationInCrm.get(0).getName(), fileAccountImportInfo.getName());

			if (isStandard) {
				// 手机号未超过30位，则返回true
				if (mobileValidator.isMobile(mobile)) {
					Assert.assertEquals(accountRelationInCrm.get(0).getMobile(), fileAccountImportInfo.getMobile());
				} else {
					Assert.assertNull(accountRelationInCrm.get(0).getMobile());
				}

				Assert.assertEquals(accountRelationInCrm.get(0).getInitialMobile(),
						fileAccountImportInfo.getInitialMobile());
			}

			Assert.assertEquals(accountRelationInCrm.get(0).getType().intValue(), 1);
			Assert.assertEquals(accountRelationInCrm.get(0).getIdCard(), idCard);
			if (addAccountType.equals("other")) {
				// 其他方式，员工号为空
				Assert.assertEquals(accountRelationInCrm.get(0).getEmployeeId(), "");
			} else {
				Assert.assertEquals(accountRelationInCrm.get(0).getEmployeeId(), employeeId);
			}

			Assert.assertEquals(accountRelationInCrm.get(0).getNewCompanyId(), new Integer(companyId));
			Assert.assertEquals(accountRelationInCrm.get(0).getOrganizationId(), new Integer(organizationId));
			Assert.assertEquals(accountRelationInCrm.get(0).getOrganizationType(), new Integer(organizationType));

			fileAccountImportInfo.setCustomerId(accountRelationInCrm.get(0).getCustomerId());
			customerId.add(accountRelationInCrm.get(0).getCustomerId());
		}
	}

	@AfterTest(groups = { "qa", "online" }, dependsOnGroups = { "crm_addCustomer" }, alwaysRun = true,enabled = false)
	public void test_01_customer_Delete_success() throws ParseException, IOException, SqlException {
		if (customerId.size() > 0) {
			for (int i = 0; i < customerId.size(); i++) {
				// delete		
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("accountIds", customerId.get(i)+""));
				params.add(new BasicNameValuePair("newCompanyId", fileAccountImportInfo.getNewCompanyId()+""));
				params.add(new BasicNameValuePair("organizationType",fileAccountImportInfo.getOrganizationType()+""));
				
				HttpResult delete = httpclient.post(Account_RemoveCustomer, params);
				String body = delete.getBody();
				System.out.println(body);
				System.out.println("===============删除用户customer_id=" + customerId.get(i) + "=====================");
				Assert.assertEquals(delete.getCode(), HttpStatus.SC_OK);

				// database
				if (checkdb) {
					waitto(5);
					String sql = "select * from tb_examiner where customer_id=?";
					List<Map<String, Object>> list = DBMapper.query(sql, customerId.get(i));
					Assert.assertEquals(1, list.get(0).get("is_delete"));

				}
			}
		}
	}

	@DataProvider
	public Iterator<String[]> customer_Add_success() {
		try {
			return CvsFileUtil.fromCvsFileToIterator("./csv/account/customer_Add_success.csv", 9);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}
}
