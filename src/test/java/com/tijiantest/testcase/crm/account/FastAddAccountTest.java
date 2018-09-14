package com.tijiantest.testcase.crm.account;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
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
import com.alibaba.fastjson.TypeReference;
import com.jayway.jsonpath.JsonPath;
import com.tijiantest.base.HttpResult;
import com.tijiantest.base.dbcheck.HospitalChecker;
import com.tijiantest.model.account.AccountImportInfoDto;
import com.tijiantest.model.account.FileAccountImportInfo;
import com.tijiantest.model.account.ModifyAccountType;
import com.tijiantest.model.company.HospitalCompany;
import com.tijiantest.testcase.crm.CrmBase;
import com.tijiantest.util.CvsFileUtil;
import com.tijiantest.util.IdCardGeneric;
import com.tijiantest.util.db.DBMapper;
import com.tijiantest.util.db.SqlException;

public class FastAddAccountTest extends CrmBase {
	String accountId;
	List<Integer> customerIds = new ArrayList<>();
	int newCompanyId;
	int organizationType;
	String exitName;
	String exitGender;
	String exitMobile;
	String exitGroup;
	String exitCustomerId;
	String exitAge;

	@Test(description = "验证极速预约添加客户->改项预约", dataProvider = "fastAddAccount_success", groups = { "qa", "online" })
	public void test_fastAddAccount_success(String... args) throws ParseException, IOException, SqlException {
		String addAccountType = args[1];
		String age = args[2];
		int companyId = Integer.parseInt(args[3]);
		String employeeId = args[4];
		String gender = args[5];
		String group = args[6];
		String initialMobile = args[8];
		String name = args[9];
		String description = args[10];
		String idCard = "";
		Boolean isNull= Boolean.valueOf(args[11]);
		if (description.equals("随机身份证导入")) {
			IdCardGeneric generic = new IdCardGeneric();
			idCard = generic.generateGender(Integer.parseInt(gender));
		} else {
			idCard = args[7];
		}

		// make parameters
		HospitalCompany hospitalCompany = new HospitalCompany();
		if (companyId == 1585)
			hospitalCompany = defSKXCnewcompany;
		else {
		}
		newCompanyId = hospitalCompany.getId();

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("hospitalId", defhospital.getId());
		FileAccountImportInfo  fileAccountImportInfo = new FileAccountImportInfo ();
		fileAccountImportInfo.setAge(age);
		fileAccountImportInfo.setAddAccountType(addAccountType);
		fileAccountImportInfo.setCompanyId(newCompanyId);
		fileAccountImportInfo.setNewCompanyId(newCompanyId);
		fileAccountImportInfo.setEmployeeId(employeeId);
		fileAccountImportInfo.setGender(gender);
		fileAccountImportInfo.setGroup(group);
		fileAccountImportInfo.setIdCard(idCard);
		fileAccountImportInfo.setInitialMobile(initialMobile);
		fileAccountImportInfo.setName(name);
		int organizationId = defhospital.getId();
		fileAccountImportInfo.setOrganizationId(organizationId);
		organizationType = HospitalChecker.getOrganizationType(defhospital.getId());
		fileAccountImportInfo.setOrganizationType(organizationType);
		if(isNull)
			fileAccountImportInfo=null;
		if(fileAccountImportInfo != null){
			
			String jbody = JSON.toJSONString(fileAccountImportInfo);
			
			boolean flag = false;
			boolean isExist = false;
			if (addAccountType.equals("idCard")) {
				String sql1 = "select a.* from tb_examiner a ,tb_account b  where  a.id_card = '" + idCard
						+ "' and a.customer_id = b.id " + "and a.new_company_id = " + newCompanyId + " and a.manager_id = "
						+ defaccountId;
				String sql2 = sql1 + " and a.name=?";
				List<Map<String, Object>> dbList1 = DBMapper.query(sql1);
				List<Map<String, Object>> dbList2 = DBMapper.query(sql2, name);
				
				if (!(dbList1.size() > 0)) {
					flag = false;
					isExist = false;
				} else if (dbList1.size() > 0 && dbList2.size() > 0) {
					flag = false;
					isExist = true;
				} else {
					flag = true;
					isExist = true;
					exitName = dbList1.get(0).get("name").toString();
					exitMobile = dbList1.get(0).get("initial_mobile").toString();
					exitGroup = dbList1.get(0).get("igroup").toString();
					exitCustomerId = dbList1.get(0).get("customer_id").toString();
					
				}
			} else if (addAccountType.equals("employeeNo")) {
				String sql1 = "select a.*,b.id from tb_examiner a ,tb_account b where  a.employee_id = '"
						+ employeeId + "' and a.customer_id = b.id "
						+ " and a.new_company_id = " + newCompanyId + " and a.manager_id = "
						+ defaccountId;
				String sql2 = sql1 + " and a.name=? and a.gender=?";
				List<Map<String, Object>> dbList1 = DBMapper.query(sql1);
				List<Map<String, Object>> dbList2 = DBMapper.query(sql2, name, gender);
				
				if (!(dbList1.size() > 0)) {
					flag = false;
					isExist = false;
				} else if (dbList1.size() > 0 && dbList2.size() > 0) {
					flag = false;
					isExist = true;
				} else {
					flag = true;
					isExist = true;
					exitName = dbList1.get(0).get("name").toString();
					exitMobile = dbList1.get(0).get("initial_mobile").toString();
					exitGroup = dbList1.get(0).get("igroup").toString();
					exitGender = dbList1.get(0).get("gender").toString();
					exitCustomerId = dbList1.get(0).get("customer_id").toString();
					int birth = Integer.parseInt(dbList1.get(0).get("birthYear").toString());
					exitAge = Calendar.getInstance().get(Calendar.YEAR) - birth + "";
				}
			}
			
			// List<Map<String,Object>> dblist = DBMapper.query("select * from
			// tb_examiner a ,tb_account b where b.idcard = '"+idCard+"' and
			// a.customer_id = b.id "
			// + "and a.company_id = "+companyId + " and a.manager_id = "+defaccountId + "
			// and a.is_delete = 0");
			// if(dblist.size() > 0 && addAccountType.equals("idCard"))
			
			// post
			HttpResult response = httpclient.post(Account_CheckAccountInfoConflict, params, jbody);
			// Assert
				Assert.assertEquals(response.getCode(), HttpStatus.SC_OK);				
				log.info(response.getBody());
				String json = response.getBody();
				Assert.assertEquals(JsonPath.read(json, "$.dataConflict").toString(), "" + flag);
				if (flag == true) {
					Assert.assertEquals(JsonPath.read(json, "$.addAccountType").toString(), addAccountType);
					String exitJson = JsonPath.read(json, "$.exitAccountInfo").toString();
					AccountImportInfoDto exitInfo = JSON.parseObject(exitJson, new TypeReference<AccountImportInfoDto>() {
					});
					String newJson = JsonPath.read(json, "$.newAccountInfo").toString();
					AccountImportInfoDto newInfo = JSON.parseObject(newJson, new TypeReference<AccountImportInfoDto>() {
					});
					
					System.out.println("json:" + json.toString());
					Assert.assertEquals(exitInfo.getCustomerId() + "", exitCustomerId);
					Assert.assertEquals(exitInfo.getName(), exitName);
					Assert.assertEquals(exitInfo.getInitialMobile(), exitMobile);
					Assert.assertEquals(exitInfo.getGroup(), exitGroup);
					Assert.assertEquals(newInfo.getAddAccountType(), addAccountType);
					Assert.assertEquals(newInfo.getName(), name);
					Assert.assertEquals(newInfo.getInitialMobile(), initialMobile);
					Assert.assertEquals(newInfo.getGroup(), group);
					
					if (addAccountType.equals("idCard")) {
						Assert.assertEquals(JsonPath.read(json, "$.sameNo").toString(), idCard);
						// toUpperCase()，数据库取出的身份证号和员工号统一转成大写
						Assert.assertEquals(exitInfo.getIdCard(), idCard.toUpperCase());
						Assert.assertEquals(newInfo.getIdCard(), idCard);
						
					} else if (addAccountType.equals("employeeNo")) {
						Assert.assertEquals(JsonPath.read(json, "$.sameNo").toString(), employeeId);
						Assert.assertEquals(exitInfo.getEmployeeId(), employeeId.toUpperCase());
						Assert.assertEquals(exitInfo.getAge(), exitAge);
						Assert.assertEquals(exitInfo.getGender(), exitGender);
						Assert.assertEquals(newInfo.getEmployeeId(), employeeId);
						Assert.assertEquals(newInfo.getGender(), gender);
						
					}
				}//flag
				
				int newAccountId = 0;
				if (!isExist) {
					params.clear();
					params.put("hospitalId", defhospital.getId());
					params.put("type", ModifyAccountType.NEW);
					HttpResult result = httpclient.post(Account_ModifyAccount, params, jbody);
					Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);
					newAccountId = Integer.parseInt(JsonPath.read(result.getBody(), "$.result").toString());
				}
				// database
				if (checkdb) {
					if (!isExist) {
						// tb_account
						if (addAccountType.equals("idCard")) {
							// 身份证导入
							String accountSql = "select * from tb_account where idcard=?";
							List<Map<String, Object>> accountList = DBMapper.query(accountSql, idCard);
							accountId = accountList.get(0).get("id").toString();
							Assert.assertEquals(name, accountList.get(0).get("name"));
							Assert.assertEquals(Integer.parseInt(accountId), newAccountId);
						} else if (addAccountType.equals("employeeNo")) {
							// 员工号导入
							String accountSql = "select * from tb_account where employee_id=? and id=?";
							List<Map<String, Object>> accountList = DBMapper.query(accountSql, employeeId, newAccountId);
							accountId = accountList.get(0).get("id").toString();
							Assert.assertEquals(name, accountList.get(0).get("name"));
							
						} else {
							// 其他方式导入
							String accountSql = "SELECT * from tb_account WHERE id_type=0 and name=? ORDER BY id desc limit 1";
							List<Map<String, Object>> accountList = DBMapper.query(accountSql, name);
							accountId = accountList.get(0).get("id").toString();
						}
						
						// tb_examiner
						String relationSql = "select * from tb_examiner where customer_id="+accountId+" and manager_id="+defaccountId+" and new_company_id="+newCompanyId;
						log.info("sql.."+relationSql);
						List<Map<String, Object>> relationList = DBMapper.query(relationSql);
						System.out.println(description + "--- 长度：" + relationList.size());
						Assert.assertEquals(name, relationList.get(0).get("name"));
						// Assert.assertEquals(null, relationList.get(0).get("company_id"));
						Assert.assertEquals(group, relationList.get(0).get("igroup"));
						Assert.assertEquals(newCompanyId, relationList.get(0).get("new_company_id"));
						Assert.assertEquals(organizationId, relationList.get(0).get("organization_id"));
						Assert.assertEquals(organizationType, relationList.get(0).get("organization_type"));
						Assert.assertTrue(relationList.get(0).get("add_account_type") == null
								|| relationList.get(0).get("add_account_type").equals(addAccountType));
						
						// tb_user
						String userSql = "select * from tb_user where account_id=?";
						List<Map<String, Object>> userList = DBMapper.query(userSql, accountId);
						if (addAccountType.equals("idCard")) {
							// 身份证导入
							Assert.assertEquals(idCard, userList.get(0).get("username"));
						} else if (addAccountType.equals("employeeNo")) {
							// 员工号导入
							Assert.assertEquals(employeeId, relationList.get(0).get("employee_id"));
							Assert.assertEquals(newCompanyId + employeeId, userList.get(0).get("username"));
						} else {
							// 其他方式导入
							Assert.assertTrue(userList.get(0).get("username").toString().contains(accountId));
							Assert.assertEquals(relationList.get(0).get("employee_id"), "");
						}
						
						// tb_accounting
						String accountingSql = "select * from tb_accounting where account_id = ?";
						List<Map<String, Object>> accountingList = DBMapper.query(accountingSql, accountId);
						Assert.assertEquals(accountingList.size(), 1);
						int accoutingTradeId = Integer.parseInt(accountingList.get(0).get("trade_account_id").toString());
						// tb_trade_account
						String tradeSql = "select * from tb_trade_account where ref_id = ?";
						List<Map<String, Object>> tradeList = DBMapper.query(tradeSql, accountId);
						Assert.assertEquals(tradeList.size(), 1);
						Assert.assertEquals(Integer.parseInt(tradeList.get(0).get("id").toString()), accoutingTradeId);
					}
				}//checkdb
		
		}
	
	}

	@AfterTest(description = "删除用户", groups = { "qa" }, alwaysRun = true)
	public void doAfter() throws SqlException {
		if (customerIds.size() > 0) {
			for (int i = 0; i < customerIds.size(); i++) {
				// delete
				List<NameValuePair> params = new ArrayList<NameValuePair>();

				params.add(new BasicNameValuePair("accountIds", customerIds.get(i) + ""));
				params.add(new BasicNameValuePair("newCompanyId", newCompanyId + ""));
				params.add(new BasicNameValuePair("organizationType", organizationType + ""));
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
	public Iterator<String[]> fastAddAccount_success() {
		try {
			return CvsFileUtil.fromCvsFileToIterator("./csv/account/fastaddaccount_success.csv", 12);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	public Map<String, String> getAccountInfo(String name) throws SqlException {
		Map<String, String> map = new HashMap<String, String>();
		String sql = "select tar.* from tb_examiner tar left join tb_account ta on ta.id= tar.customer_id where ta.account_id=? and tar.name=?";
		List<Map<String, Object>> list = DBMapper.query(sql, accountId, name);
		map.put("account_id", list.get(0).get("customer_id").toString());
		map.put("name", list.get(0).get("name").toString());
		map.put("idcard", list.get(0).get("idcard").toString());
		map.put("mobile", list.get(0).get("mobile").toString());
		map.put("gender", list.get(0).get("gender").toString());
		return map;
	}

}