package com.tijiantest.testcase.main.account;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.*;

import com.jayway.jsonpath.JsonPath;
import com.tijiantest.base.dbcheck.HospitalChecker;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.alibaba.fastjson.JSON;
import com.tijiantest.base.Flag;
import com.tijiantest.base.HttpResult;
import com.tijiantest.model.account.AccountRelationTypeEnum;
import com.tijiantest.model.account.AccountTypeEnum;
import com.tijiantest.model.account.IdTypeEnum;
import com.tijiantest.model.account.ExaminerVo;
import com.tijiantest.model.account.SystemTypeEnum;
import com.tijiantest.testcase.main.MainBase;
import com.tijiantest.util.CvsFileUtil;
import com.tijiantest.util.IdCardGeneric;
import com.tijiantest.util.PinYinUtil;
import com.tijiantest.util.db.DBMapper;
import com.tijiantest.util.db.SqlException;

public class ExaminerAddTest extends MainBase {

	int accountId;
	boolean isExist = false;// 默认数据库不存在该身份证
	String idCard = null;
	
	@Test(description = "添加体检人", groups={"qa"},dataProvider = "medicalUser")
	public void test_01_addMedicalUser(String... args) throws SqlException {
		IdCardGeneric generic = new IdCardGeneric();
		idCard = generic.generateGender(1);
		String gender = "1";
		String mobile = args[2];
		String name = args[3];
		String type = args[4];
//		String address = args[5];
//		String id = args[6];
//		String age = args[7];
		String marriageStatus = args[9];
//		String birthYear =args[10];

		// 先tb_account中是否已经有了该身份证号
		String sql = "select * from tb_account where idcard= \'"+idCard+"\'";
		System.out.println("sql:"+sql);
		List<Map<String, Object>> list = DBMapper.query(sql);

		if (!list.isEmpty()) {
			isExist = true;// 数据库已经有了该身份证号
			accountId = Integer.valueOf(list.get(0).get("id").toString());
		}

		List<NameValuePair> params = new ArrayList<>();
		params.add(new BasicNameValuePair("_site","mtjk"));
		int fromSite = HospitalChecker.getHospitalIdBySite("mtjk").getId();

		ExaminerVo medicalUser = new ExaminerVo();
		medicalUser.setGender(Integer.valueOf(gender));
		medicalUser.setIdCard(idCard);
		medicalUser.setMarriageStatus(Integer.valueOf(marriageStatus));
		medicalUser.setMobile(mobile);
		medicalUser.setName(name);
		medicalUser.setType(Integer.valueOf(type));
		medicalUser.setAddType("NORMAL");
		int birthYear = 1990;
		medicalUser.setBirthYear(birthYear);
		medicalUser.setAge(Integer.parseInt(sd.format(new Date()))-birthYear);
		medicalUser.setIsSelf(0);//家属
		
		String user = JSON.toJSONString(medicalUser);		
		
		HttpResult result = httpclient.post(Flag.MAIN, Account_ExaminerAdd,params,user);
		System.out.println(result.getBody());
		Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);
		int retExaminerId = JsonPath.read(result.getBody(),"$.examinerId");
		if (checkdb) {

			if (!isExist) {// 执行请求前，数据库不存在该身份证
				//  tb_account
				String accountSql = "select * from tb_account where idcard = \'"+idCard+"\' ";
				System.out.println("accountSql:"+accountSql);
				List<Map<String, Object>> accountList = DBMapper.query(accountSql);
				accountId = Integer.valueOf(accountList.get(0).get("id").toString());
				Assert.assertTrue(!accountList.isEmpty());
				Assert.assertEquals(name, accountList.get(0).get("name"));
				Assert.assertEquals(type, accountList.get(0).get("type").toString());
				Assert.assertEquals(1, Integer.parseInt(accountList.get(0).get("system").toString()));
				Assert.assertEquals(IdTypeEnum.IDCARD.getCode(),
						Integer.parseInt(accountList.get(0).get("id_type").toString()));

				//  tb_role
				String roleSql = "select * from tb_account_role where account_id=?";
				List<Map<String, Object>> roleList = DBMapper.query(roleSql, accountId);
				Assert.assertEquals(1, Integer.parseInt(roleList.get(0).get("role_id").toString()));

				// tb_accounting
				String accountingSql = "select * from tb_accounting where account_id=?";
				List<Map<String, Object>> accountingList = DBMapper.query(accountingSql, accountId);
				Assert.assertTrue(!accountingList.isEmpty());
				Assert.assertEquals(0, Integer.parseInt(accountingList.get(0).get("balance").toString()));
				
				//tb_trade_account
				String tradeSql = "select * from tb_trade_account where ref_id = ?";
				List<Map<String, Object>> tradeList = DBMapper.query(tradeSql, accountId);
				Assert.assertTrue(!tradeList.isEmpty());
				Assert.assertEquals(accountingList.get(0).get("trade_account_id"),tradeList.get(0).get("id"));
				//tb_examiner
				sql = "select * from tb_examiner where customer_id = "+accountId + " order by update_time desc ";
				List<Map<String, Object>> examinerList = DBMapper.query(sql);
				Assert.assertEquals(examinerList.size(),1);
				Map<String,Object> map = examinerList.get(0);
				Assert.assertEquals(map.get("mobile").toString(),mobile);
				Assert.assertEquals(Integer.parseInt(map.get("is_self").toString()),0);
				Assert.assertEquals(Integer.parseInt(map.get("relation_id").toString()),defaccountId);
				Assert.assertEquals(retExaminerId,Integer.parseInt(map.get("id").toString()));

				//tb_user
				String userSql = "select * from tb_user where account_id=?";
				List<Map<String, Object>> userList = DBMapper.query(userSql, accountId);
				Assert.assertEquals(userList.size(), 1);
				Assert.assertEquals(userList.get(0).get("username").toString(), idCard);
				Assert.assertEquals(userList.get(0).get("system").toString(),SystemTypeEnum.C_LOGIN.getCode()+"");

			} else {// 执行请求前，数据库已经存在了该身份证
				// tb_examiner
				String relaSql = "select * from tb_examiner where customer_id=? and manager_id=?";
				List<Map<String, Object>> relaList = DBMapper.query(relaSql, accountId, defaccountId);
				Assert.assertTrue(!relaList.isEmpty());
				Assert.assertEquals(name, relaList.get(0).get("name"));
				Assert.assertEquals(AccountRelationTypeEnum.MedicalUser.getCode(), relaList.get(0).get("type"));
				Assert.assertEquals(marriageStatus, relaList.get(0).get("marriageStatus").toString());
				Assert.assertEquals(0, relaList.get(0).get("is_delete"));
			}
		}
	}
	
	@Test(description="体检人已存在",groups={"qa"},dataProvider="medicalUser",dependsOnMethods="test_01_addMedicalUser")
	public void test_02_medicalUser_fail(String... args){
		String mobile = args[2];
		String name = args[3];
//		String type = args[4];
//		String address = args[5];
//		String id = args[6];
//		String age = args[7];
		String gender = args[8];
		String marriageStatus = args[9];
		List<NameValuePair> params = new ArrayList<>();
		params.add(new BasicNameValuePair("_site","mtjk"));

		ExaminerVo medicalUserVo = new ExaminerVo();
		medicalUserVo.setGender(Integer.valueOf(gender));
		medicalUserVo.setIdCard(idCard);
		medicalUserVo.setMarriageStatus(Integer.valueOf(marriageStatus));
		medicalUserVo.setMobile(mobile);
		medicalUserVo.setName(name);
		medicalUserVo.setType(AccountTypeEnum.Medical.getCode());
		medicalUserVo.setAddType("NORMAL");
		int birthYear = 1990;
		medicalUserVo.setBirthYear(birthYear);
		medicalUserVo.setAge(Integer.parseInt(sd.format(new Date()))-birthYear);
		medicalUserVo.setIsSelf(0);//家属
		String json = JSON.toJSONString(medicalUserVo);
		
		HttpResult result = httpclient.post(Flag.MAIN, Account_ExaminerAdd, params,json);
		log.info(result.getBody());
		String code = JsonPath.read(result.getBody(),"$.content.code");
		String text = JsonPath.read(result.getBody(),"$.content.text");
		Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);
		Assert.assertEquals(code,"EX_1_2_USER_02_02_014");
		Assert.assertEquals(text,"该身份证号码的体检人已经存在，请重新输入");
	}

	@AfterClass(alwaysRun = true, description = "删除体检人")
	public void doAfter() throws SqlException {
		List<NameValuePair> params = new ArrayList<>();
		params.add(new BasicNameValuePair("_site","mtjk"));
		params.add(new BasicNameValuePair("_siteType","mobile"));

		HttpResult result = httpclient.post(Flag.MAIN, Account_ExaminerDelete+"/"+accountId,params);
		log.info(result.getBody());
		Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);

		String sql = "select * from tb_examiner where customer_id=? and manager_id=?";
		List<Map<String, Object>> list = DBMapper.query(sql, accountId, defaccountId);
		Assert.assertTrue(list.isEmpty(),"删除体检人失败");

	}

	@DataProvider(name = "medicalUser")
	public Iterator<String[]> medicalUser() {
		try {
			return CvsFileUtil.fromCvsFileToIterator("./csv/account/main/medicalUser.csv", 5);
		} catch (FileNotFoundException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		return null;
	}
	

}
