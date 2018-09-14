package com.tijiantest.testcase.crm.account;

/**
 * @author ChenSijia
 */
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.jayway.jsonpath.JsonPath;
import com.tijiantest.model.account.AccountCustomerManagerRelationVO;
import org.apache.http.HttpStatus;
import org.apache.http.ParseException;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.alibaba.fastjson.JSON;
import com.tijiantest.base.HttpResult;
import com.tijiantest.testcase.crm.CrmBase;
import com.tijiantest.util.CvsFileUtil;
import com.tijiantest.util.PinYinUtil;
import com.tijiantest.util.db.DBMapper;
import com.tijiantest.util.db.SqlException;
import com.tijiantest.model.account.AccountStatusEnum;
import com.tijiantest.model.account.AccountTypeEnum;
import com.tijiantest.model.account.ManagerVo;
import com.tijiantest.model.card.CardStatusEnum;
import com.tijiantest.model.card.CardTypeEnum;

public class ManagerSaveTest extends CrmBase {
	int accountId;

	int accountCompanyId;
	int hospitalId;
	int id;
	String identity;
	boolean importWithoutIdcard;
	boolean isSitePay;
	boolean keepLogin;
	String mobile;
	String name;
	boolean orderImmediately;
	boolean removeAllItems;
	int roleId;
	String username;
	int code;
	String message;
	Boolean agentReserve;

	@Test(description = "新建客户经理", dataProvider = "manag_Save_success", groups = { "qa", "online" })
	public void test_01_manag_save_success(String... args) throws ParseException, IOException, SqlException {

		String jbody = this.initData(args);
		// post
		HttpResult response = httpclient.post(Manag_Save, jbody);
		System.out.println(response.getBody());
		// Assert
		Assert.assertEquals(HttpStatus.SC_OK, response.getCode());

		// database
		if (checkdb) {
			// tb_account
			String accountSql = "select id, name, mobile, idcard, status, type, employee_id, create_time, update_time, id_type, system from tb_account order by id desc limit 1";
			List<Map<String, Object>> accountList = DBMapper.query(accountSql);
			accountId = Integer.valueOf(accountList.get(0).get("id").toString());
			Assert.assertEquals(name, accountList.get(0).get("name"));
			Assert.assertEquals(mobile, accountList.get(0).get("mobile"));
			Assert.assertEquals(AccountTypeEnum.Manager.getCode(), accountList.get(0).get("type"));
			Assert.assertEquals(2, accountList.get(0).get("system"));
			Assert.assertEquals(AccountStatusEnum.NORMAL.getCode(), accountList.get(0).get("status"));

			// tb_user
			String userSql = "select * from tb_user where account_id=?";
			List<Map<String, Object>> userList = DBMapper.query(userSql, accountId);
			Assert.assertEquals(username, userList.get(0).get("username"));
			Assert.assertEquals(2, userList.get(0).get("system"));

			
			// tb_logged_log
			String logSql = "select * from tb_logged_log where account_id=?";
			List<Map<String, Object>> logList = DBMapper.query(logSql, accountId);
			Assert.assertNotNull(logList);
			Assert.assertEquals(0, Integer.parseInt(logList.get(0).get("success_logged_count").toString()));

			// tb_manager_account_company_relation
			String managerAccountCompanySql = "select * from tb_manager_account_company_relation where manager_id=?";
			List<Map<String, Object>> managerAccountCompanyList = DBMapper.query(managerAccountCompanySql, accountId);
			Assert.assertNotNull(managerAccountCompanyList);
			Assert.assertEquals(accountCompanyId, managerAccountCompanyList.get(0).get("account_company_id"));
			Assert.assertEquals(isSitePay ? 1 : 0, managerAccountCompanyList.get(0).get("is_site_pay"));
			Assert.assertEquals(importWithoutIdcard ? 1 : 0,
					managerAccountCompanyList.get(0).get("import_without_idcard"));
			Assert.assertEquals(orderImmediately ? 1 : 0, managerAccountCompanyList.get(0).get("order_immediately"));
			Assert.assertEquals(keepLogin ? 1 : 0, managerAccountCompanyList.get(0).get("keep_login"));
			Assert.assertEquals(removeAllItems ? 1 : 0, managerAccountCompanyList.get(0).get("remove_all_items"));
			Assert.assertEquals(agentReserve ? 1 : 0, managerAccountCompanyList.get(0).get("agent_reserve"));


			// tb_account_role
			String accountRoleSql = "select * from tb_account_role where account_id=? order by role_id desc ";
			List<Map<String, Object>> accountRoleList = DBMapper.query(accountRoleSql, accountId);
			Assert.assertNotNull(accountRoleList);
			Assert.assertEquals(2, accountRoleList.size());

			// tb_accounting
			String accoutingSql = "select * from tb_accounting where account_id=?";
			List<Map<String, Object>> accountingList = DBMapper.query(accoutingSql, accountId);
			Assert.assertNotNull(accountingList);
			Assert.assertEquals(0, accountingList.get(0).get("balance"));
			int accoutingTradeId = Integer.parseInt(accountingList.get(0).get("trade_account_id").toString());

			// tb_card
			String cardSql = "select * from tb_card where account_id=?";
			List<Map<String, Object>> cardList = DBMapper.query(cardSql, accountId);
			Assert.assertNotNull(cardList);
			Assert.assertEquals("0", cardList.get(0).get("balance").toString());
			Assert.assertEquals(CardTypeEnum.VIRTUAL.getCode(), cardList.get(0).get("type"));
			Assert.assertEquals(CardStatusEnum.USABLE.getCode(), cardList.get(0).get("status"));
			int cardTradeId = Integer.parseInt(cardList.get(0).get("trade_account_id").toString());
			
			//tb_trade_account
			String tradeSql = "select * from tb_trade_account where ref_id = ?";
			List<Map<String, Object>> tradeList = DBMapper.query(tradeSql, accountId);
			Assert.assertEquals(tradeList.size(),1);
			Assert.assertEquals(Integer.parseInt(tradeList.get(0).get("id").toString()),accoutingTradeId);
			Assert.assertEquals(Integer.parseInt(tradeList.get(0).get("id").toString()),cardTradeId);

			// tb_promotion
			String promotionSql = "select * from tb_promotion where manager_id=?";
			List<Map<String, Object>> promotionList = DBMapper.query(promotionSql, accountId);
			Assert.assertNotNull(promotionList);
			Assert.assertEquals(identity, promotionList.get(0).get("identity"));
			Assert.assertEquals(defhospital.getId(), promotionList.get(0).get("hospital_id"));

			// tb_manager_hospital_settings
			String managerHosSettingsSql = "select * from tb_manager_hospital_settings where manager_id=?";
			List<Map<String, Object>> managerHosSettingsList = DBMapper.query(managerHosSettingsSql, accountId);
			int settingsId = Integer.valueOf(managerHosSettingsList.get(0).get("id").toString());
			Assert.assertEquals(orderImmediately ? 1 : 0, managerHosSettingsList.get(0).get("order_immediately"));
			Assert.assertEquals(importWithoutIdcard ? 1 : 0,
					managerHosSettingsList.get(0).get("import_without_idcard"));
			Assert.assertEquals(keepLogin ? 1 : 0, managerHosSettingsList.get(0).get("keep_login"));
			Assert.assertEquals(removeAllItems ? 1 : 0, managerHosSettingsList.get(0).get("remove_all_items"));
			Assert.assertEquals(isSitePay ? 1 : 0, managerHosSettingsList.get(0).get("is_site_pay"));

			// tb_hospital_manager_relation
			String hosManagerRelationSql = "select * from tb_hospital_manager_relation where manager_id=?";
			List<Map<String, Object>> hosManagerRelationList = DBMapper.query(hosManagerRelationSql, accountId);
			Assert.assertEquals(defhospital.getId(), hosManagerRelationList.get(0).get("hospital_id"));
			Assert.assertEquals(1, hosManagerRelationList.get(0).get("is_belong"));
			Assert.assertEquals(settingsId, hosManagerRelationList.get(0).get("manager_settings_id"));
		}
	}

	@Test(description = "新建客户经理 - 重复的username", groups = {
			"qa" }, dataProvider = "manag_Save_success", dependsOnMethods = "test_01_manag_save_success")
	public void test_02_manager_save_failed(String... args) {
		accountCompanyId = Integer.parseInt(args[1]);
		hospitalId = Integer.parseInt(args[2]);
		id = Integer.parseInt(args[3]);
		importWithoutIdcard = args[5].equals("true") ? true : false;
		isSitePay = args[6].equals("true") ? true : false;
		keepLogin = args[7].equals("true") ? true : false;
		mobile = args[8];
		orderImmediately = args[10].equals("true") ? true : false;
		removeAllItems = args[11].equals("true") ? true : false;
		roleId = Integer.parseInt(args[12]);
		code = Integer.parseInt(args[14]);
		message = args[15];
		agentReserve = Boolean.valueOf(args[16]);
		ManagerVo managerVo = this.initManagerVo(accountCompanyId, hospitalId, identity, importWithoutIdcard, isSitePay,
				keepLogin, orderImmediately, removeAllItems, agentReserve);

		String jbody = JSON.toJSONString(managerVo);

		// post
		HttpResult response = httpclient.post(Manag_Save, jbody);
		System.out.println(response.getBody());
		// Assert
		Assert.assertEquals(HttpStatus.SC_BAD_REQUEST, response.getCode());
		Assert.assertTrue(response.getBody().contains("已存在"));

	}

	@Test(description = "新建客户经理 - 重复的name", groups = {
			"qa" }, dataProvider = "manag_Save_success", dependsOnMethods = "test_02_manager_save_failed")
	public void test_03_manager_save_failed(String... args) {
		accountCompanyId = Integer.parseInt(args[1]);
		hospitalId = Integer.parseInt(args[2]);
		id = Integer.parseInt(args[3]);
		importWithoutIdcard = args[5].equals("true") ? true : false;
		isSitePay = args[6].equals("true") ? true : false;
		keepLogin = args[7].equals("true") ? true : false;
		mobile = args[8];
		orderImmediately = args[10].equals("true") ? true : false;
		removeAllItems = args[11].equals("true") ? true : false;
		roleId = Integer.parseInt(args[12]);
		username = args[13] + ((int) (Math.random() * 90000) + 1000);
		code = Integer.parseInt(args[14]);
		message = args[15];
		agentReserve = Boolean.valueOf(args[16]);

		ManagerVo managerVo = this.initManagerVo(accountCompanyId, hospitalId, identity, importWithoutIdcard, isSitePay,
				keepLogin, orderImmediately, removeAllItems, agentReserve);

		String jbody = JSON.toJSONString(managerVo);
		// post
		HttpResult response = httpclient.post(Manag_Save, jbody);
		System.out.println(response.getBody() +".."+name);
		String code = JsonPath.read(response.getBody(),"$.code");
		String text = JsonPath.read(response.getBody(),"$.text");
		// Assert
		Assert.assertEquals(HttpStatus.SC_BAD_REQUEST, response.getCode());
		Assert.assertEquals(code,"EX_1_2_USER_02_03_001");
		Assert.assertEquals(text,"客户经理 " + name + " 已存在");

	}

	@Test(description = "新建客户经理 - 重复的identity", groups = {
			"qa" }, dataProvider = "manag_Save_success", dependsOnMethods = "test_03_manager_save_failed")
	public void test_04_manager_save_failed(String... args) {
		accountCompanyId = Integer.parseInt(args[1]);
		hospitalId = Integer.parseInt(args[2]);
		id = Integer.parseInt(args[3]);
		importWithoutIdcard = args[5].equals("true") ? true : false;
		isSitePay = args[6].equals("true") ? true : false;
		keepLogin = args[7].equals("true") ? true : false;
		mobile = args[8];
		orderImmediately = args[10].equals("true") ? true : false;
		removeAllItems = args[11].equals("true") ? true : false;
		roleId = Integer.parseInt(args[12]);
		username = args[13] + ((int) (Math.random() * 90000) + 1000);
		name = args[9] + ((int) (Math.random() * 90000) + 1000);
		code = Integer.parseInt(args[14]);
		message = args[15];
		agentReserve = Boolean.valueOf(args[16]);

		ManagerVo managerVo = this.initManagerVo(accountCompanyId, hospitalId, identity, importWithoutIdcard, isSitePay,
				keepLogin, orderImmediately, removeAllItems, agentReserve);

		String jbody = JSON.toJSONString(managerVo);
		// post
		HttpResult response = httpclient.post(Manag_Save, jbody);
		System.out.println(response.getBody());
		// Assert
		Assert.assertEquals(HttpStatus.SC_BAD_REQUEST, response.getCode());
		Assert.assertTrue(response.getBody().contains("专属网址已存在"));

	}

	@AfterClass(description = "删除客户经理", groups = { "qa" })
	public void doAfter() {
		if(accountId !=0) {
			HttpResult response = httpclient.get(Manag_Remove, String.valueOf(accountId));
			Assert.assertEquals(response.getCode(), HttpStatus.SC_OK);
		}
	}

	@DataProvider
	public Iterator<String[]> manag_Save_success() {
		try {
			return CvsFileUtil.fromCvsFileToIterator("./csv/account/manag_Save_success.csv", 14);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	private String initData(String... args) {
		// get input & output from casefile
		accountCompanyId = Integer.parseInt(args[1]);
		hospitalId = Integer.parseInt(args[2]);
		id = Integer.parseInt(args[3]);
		identity = args[4] + ((int) (Math.random() * 90000) + 1000);
		importWithoutIdcard = args[5].equals("true") ? true : false;
		isSitePay = args[6].equals("true") ? true : false;
		keepLogin = args[7].equals("true") ? true : false;
		mobile = args[8];
		name = args[9] + ((int) (Math.random() * 800000) + 1000) + getRandomHan();
		orderImmediately = args[10].equals("true") ? true : false;
		removeAllItems = args[11].equals("true") ? true : false;
		roleId = Integer.parseInt(args[12]);
		username = args[13] + ((int) (Math.random() * 900000) + 1000);
		code = Integer.parseInt(args[14]);
		message = args[15];
		agentReserve = Boolean.valueOf(args[16]);

		ManagerVo managerVo = this.initManagerVo(accountCompanyId, hospitalId, identity, importWithoutIdcard, isSitePay,
				keepLogin, orderImmediately, removeAllItems, agentReserve);
		String jbody = JSON.toJSONString(managerVo);

		return jbody;
	}

	private ManagerVo initManagerVo(int accountCompanyId, int hospitalId, String identity, Boolean importWithoutIdcard,
			Boolean isSitePay, Boolean keepLogin, Boolean orderImmediately, Boolean removeAllItems,
			Boolean agentReserve) {
		ManagerVo managerVo = new ManagerVo();
		managerVo.setAccountCompanyId(accountCompanyId);
		managerVo.setHospitalId(hospitalId);
		managerVo.setIdentity(identity);
		managerVo.setImportWithoutIdcard(importWithoutIdcard);
		managerVo.setIsSitePay(isSitePay);
		managerVo.setKeepLogin(keepLogin);
		managerVo.setMobile(mobile);
		managerVo.setName(name);
		managerVo.setOrderImmediately(orderImmediately);
		managerVo.setRemoveAllItems(removeAllItems);
		managerVo.setRoleId(roleId);
		managerVo.setUsername(username);
		managerVo.setAgentReserve(agentReserve);
		managerVo.setId(null);
		AccountCustomerManagerRelationVO vo = new AccountCustomerManagerRelationVO();
		vo.setId(null);
		vo.setCustomerId(null);
		managerVo.setRelation(vo);
		return managerVo;
	}
}
