package com.tijiantest.testcase.crm.account.query;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import com.tijiantest.base.dbcheck.CompanyChecker;
import com.tijiantest.model.company.HospitalCompany;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.message.BasicNameValuePair;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.jayway.jsonpath.JsonPath;
import com.tijiantest.base.HttpResult;
import com.tijiantest.base.dbcheck.HospitalChecker;
import com.tijiantest.model.account.AccountInHospital;
import com.tijiantest.model.account.AccountInfos;
import com.tijiantest.model.card.Card;
import com.tijiantest.model.order.Order;
import com.tijiantest.testcase.crm.account.AccountBase;
import com.tijiantest.util.CvsFileUtil;
import com.tijiantest.util.IdCardValidate;
import com.tijiantest.util.db.DBMapper;
import com.tijiantest.util.db.MongoDBUtils;
import com.tijiantest.util.db.SqlException;
import com.tijiantest.util.validator.MobileValidator;

/**
 * 位置：CRM->订单&用户->用户查询
 * 操作：输入用户->点击查询
 */
public class AccountManagePageInfoTest extends AccountBase {

	private static final int KEYWORD_IDCARD = 1;// 身份证号
	private static final int KEYWORD_MOBILE = 2;// 手机号
	private static final int KEYWORD_NAME = 3;// 姓名

	@Test(dataProvider = "accountManager", groups = { "qa", "online","queryCustomerAccountManager" })
	public void test_01_accountManager_success(String... args) throws ParseException, SqlException {

		HospitalCompany hospitalCompany  = CompanyChecker.getRandomCommonHospitalCompany(defhospital.getId());
		Set<Integer> accountLists = new TreeSet<Integer>();
		int firstAccountId = 0;

		String keyword = args[1];
		int rowCount = Integer.parseInt(args[2]);
		int currentPage = Integer.parseInt(args[3]);
		int pageSize = Integer.parseInt(args[4]);
		int newCompanyTmp = Integer.parseInt(args[5]);
		String newCompanyId = "";
		if(newCompanyTmp != -1)
			newCompanyId = hospitalCompany.getId() + "";

		Integer organizationType = HospitalChecker.getOrganizationType(defhospital.getId());

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("hospitalId", defhospital.getId() + ""));
		params.add(new BasicNameValuePair("keyword", keyword));
		params.add(new BasicNameValuePair("newCompanyId", newCompanyId));
		params.add(new BasicNameValuePair("rowCount", rowCount + ""));
		params.add(new BasicNameValuePair("currentPage", currentPage + ""));
		params.add(new BasicNameValuePair("pageSize", pageSize + ""));

		HttpResult result = httpclient.get(AccountQuery_AccountManagePageInfo, params);
		Assert.assertEquals(result.getCode(), HttpStatus.SC_OK, "错误原因:" + result.getBody());
		String body = result.getBody();
		log.info(body);
		// User
		if (body.equals("")) {
			log.info("body is null"); // 增加body为空的时候判断
			return;
		}
		List<AccountInfos> users = JSON.parseObject(JsonPath.read(body, "$.user").toString(),
				new TypeReference<List<AccountInfos>>() {
				});
		Set<Integer> retUsers = new TreeSet<Integer>();
		for (AccountInfos u : users) {
			int uid = u.getAccountId();
			if (firstAccountId == 0)
				firstAccountId = uid;
			retUsers.add(uid);
		}

		// Card
		List<Card> cards = JSON.parseObject(JsonPath.read(body, "$.card.records").toString(),
				new TypeReference<List<Card>>() {
				});

		// Order
		List<Order> orders = JSON.parseObject(JsonPath.read(body, "$.order.records").toString(),
				new TypeReference<List<Order>>() {
				});

		if (checkdb) {
			List<AccountInHospital> resultList = new ArrayList<>();
			List<AccountInHospital> tempList = new ArrayList<>();

			waitto(mongoWaitTime);


			// step2 查询卡
			String sql4 = "select * from tb_card where account_id = ? "
					+ "and batch_id in (select id from tb_card_batch "
					+ "where (operator_id  in (select manager_id from tb_manager_company_relation where hospital_id = ? ))"
					+ "and operator_id not in  (select account_id from tb_account_role where role_id = 4 ) )order by recharge_time desc limit ?";
			List<Map<String, Object>> dblist4 = DBMapper.query(sql4, firstAccountId, defhospital.getId(), pageSize);
			Assert.assertEquals(cards.size(), dblist4.size());
			for (int i = 0; i < cards.size(); i++) {
				Assert.assertEquals(cards.get(i).getId(), dblist4.get(i).get("id"));
				Assert.assertEquals(cards.get(i).getCardName(), dblist4.get(i).get("card_name"));
				Assert.assertEquals(cards.get(i).getCardNum(), dblist4.get(i).get("card_num"));
				Assert.assertEquals(cards.get(i).getBalance(), dblist4.get(i).get("balance"));
				Assert.assertEquals(cards.get(i).getCapacity(), dblist4.get(i).get("capacity"));
				Assert.assertEquals(cards.get(i).getStatus(), dblist4.get(i).get("status"));
			}

			// step3 查询订单-根据mongo查询订单
			List<Map<String, Object>> dblist5 = MongoDBUtils.query(
					"{'orderAccount._id':" + firstAccountId + ",'orderHospital._id':" + defhospital.getId() + ",'isDeleted':{$exists:false}}",
					MONGO_COLLECTION);
			int dsize = dblist5.size();
			log.info("记录数量:" + dblist5.size());
			// 至多只返回前5条
			if (dsize >= 5)
				Assert.assertEquals(orders.size(), 5);
			else
				Assert.assertEquals(orders.size(), dsize);
			for (int i = 0; i < orders.size(); i++) {
//				log.info("id"+orders.get(i).getId()+"..."+dblist5.get(dsize - i - 1).get("id"));
				Assert.assertEquals(orders.get(i).getId(), dblist5.get(i).get("id"));
			}
		}
	}

	/**
	 * 判断keyword是身份证/手机号/姓名
	 *
	 * @param keyword
	 * @return
	 */
	private int checkKeyword(String keyword) {
		if (IdCardValidate.isIdcard(keyword)) {
			return KEYWORD_IDCARD;
		} else if (MobileValidator.isMobile(keyword)) {
			return KEYWORD_MOBILE;
		} else {
			return KEYWORD_NAME;
		}
	}

	private void sortList(List<AccountInHospital> tempList) {
		Map<Integer, List<AccountInHospital>> map = tempList.stream()
				.collect(Collectors.groupingBy(AccountInHospital::getAccountId));
		tempList.clear();
		for (Integer accountId : map.keySet()) {
			List<AccountInHospital> accountList = map.get(accountId);
			List<AccountInHospital> newList = new ArrayList<>();
			AccountInHospital mainAccount = null;
			for (AccountInHospital account : accountList) {
				if (account.getManagerId() != null) {
					newList.add(account);
				} else {
					mainAccount = account;
				}
			}

			if (mainAccount != null) {
				tempList.add(mainAccount);
			}
			tempList.addAll(newList);
		}
	}

	private AccountInHospital mapConvertToBean(Map<String, Object> map) {
		AccountInHospital bean = new AccountInHospital();
		bean.setAccountId(Integer.valueOf(map.get("accountId").toString()));
		if (map.get("addAccountType") == null) {
			bean.setAddAccountType("idCard");
		} else {
			bean.setAddAccountType(map.get("addAccountType").toString());
		}
		bean.setBirthYear(Integer.valueOf(map.get("birthYear").toString()));
		if (map.get("gender") != null) {
			bean.setGender(Integer.valueOf(map.get("gender").toString()));
		}
		bean.setIdcard(map.get("idCard").toString());
		if (map.get("username") != null) {
			bean.setLoginName(map.get("username").toString());
		}
		System.out.println(
				"initalMobile ... " + map.get("initalMobile") + " ======  mobile..." + map.get("mobile").toString());
		if (map.get("initalMobile") != null) {
			bean.setInitialMobile(map.get("initalMobile").toString());
		}
		if (map.get("mobile") != null) {
			bean.setMobile(map.get("mobile").toString());
		}

		bean.setName(map.get("name").toString());
		return bean;
	}

	@DataProvider
	public Iterator<String[]> accountManager() {
		try {
			return CvsFileUtil.fromCvsFileToIterator("./csv/account/query/accountManager.csv", 7);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}
}
