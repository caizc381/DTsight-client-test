package com.tijiantest.testcase.crm.card;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.jayway.jsonpath.JsonPath;
import com.tijiantest.base.HttpResult;
import com.tijiantest.base.dbcheck.CardChecker;
import com.tijiantest.base.dbcheck.CompanyChecker;
import com.tijiantest.base.dbcheck.HospitalChecker;
import com.tijiantest.model.card.CardRecordDto;
import com.tijiantest.model.card.CardTypeEnum;
import com.tijiantest.model.company.HospitalCompany;
import com.tijiantest.testcase.crm.CrmBase;
import com.tijiantest.util.CvsFileUtil;
import com.tijiantest.util.db.DBMapper;
import com.tijiantest.util.db.SqlException;

/**
 * 获取客户经理已卡明细信息
 * 
 * @author zhichun
 *
 */
public class CardRecordsTest extends CrmBase {
	static List<CardRecordDto> dto  = new ArrayList<>();

	@SuppressWarnings("unused")
	@Test(description = "获取客户经理已卡明细信息 - 所有记录", groups = { "qa","crm_allRecords" }, dataProvider = "allRecords")
	public void test_01_cardrecords_success(String... args) throws SqlException {
		Integer[] batchIds = null;
		Integer companyId = Integer.valueOf(args[1]);
		HospitalCompany hospitalCompany = CompanyChecker.getHCompanyByOldCompanyIdANDOrgId(companyId,defhospital.getId());
		int newCompanyId = hospitalCompany.getId();
		int organizationType = HospitalChecker.getOrganizationType(defhospital.getId());
		boolean hideZeroCapacityCard = new Boolean(args[2]).booleanValue();
		int currentPage = Integer.parseInt(args[3]);
//		int pageSize = Integer.parseInt(args[4]);
		int rowCount = Integer.parseInt(args[5]);
		String searchKey = args[6];
		String useStatus = args[7];
		String description = args[8];

		String json = CardChecker.generateJson(batchIds,hideZeroCapacityCard, newCompanyId,
				organizationType, searchKey, useStatus, currentPage, null, rowCount);

		HttpResult result = httpclient.post(Card_CardRecords, json);
		Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);
		String body = result.getBody();

		dto = JSON.parseObject(JsonPath.read(body, "$.records").toString(),
				new TypeReference<List<CardRecordDto>>() {
				});
		System.out.println("共有"+dto.size()+"张卡");
		Collections.sort(dto, new Comparator<CardRecordDto>() {
			@Override
			public int compare(CardRecordDto o1, CardRecordDto o2) {
				return o1.getCard().getId() - o2.getCard().getId();
			}
		});

		if (checkdb) {
			// 新获取该客户经理，该单位的人，再根据人获取卡
			String accountSql = "select acr.customer_id, acr.name , acr.birthYear, acr.gender, acr.marriagestatus, acr.mobile, acr.id_card , acr.igroup, acr.position, acr.department, acr.is_retire, acr.employee_id, acr.add_account_type,acr.initial_mobile "
					+ "from tb_examiner acr left join tb_account ai on ai.id = acr.customer_id "
					+ "WHERE acr.manager_id = ? AND acr.new_company_id = ? AND acr.organization_type = ? AND acr.organization_id = ?";
			if (searchKey != null && !searchKey.equals("")) {
				accountSql += " AND ( acr.name like CONCAT('%','" + searchKey + "','%') or acr.mobile like CONCAT('%','"
						+ searchKey + "','%') or acr.id_Card like CONCAT('%','" + searchKey + "','%') )";
			}
			
//			System.out.println("sql.... " + accountSql);
//			log.info("defaccountId"+defaccountId+"..newCompanyId"+newCompanyId);
			List<Map<String, Object>> accountList = DBMapper.query(accountSql, defaccountId, newCompanyId,
					organizationType,defhospital.getId());
			
			if (accountList.size()==0) {
				log.info("没有找到对应的人");
				Assert.assertEquals(dto.size(), 0);
				return;
			}
			String accountIdStr = "";
			for (int i = 0; i < accountList.size(); i++) {
				accountIdStr += accountList.get(i).get("customer_id") + ",";
			}
			
			
			int lastIndex = accountIdStr.lastIndexOf(",");
//			System.out.println("accountIdStr..." + accountIdStr);
			accountIdStr = accountIdStr.substring(0, lastIndex);

			String cardSql = "SELECT c.id AS 'card.id', c.batch_id AS 'card.batchId', c.card_name AS 'card.cardName', c.card_num AS 'card.cardNum', c.capacity AS 'card.capacity', c.balance AS 'card.balance', c.recoverable_balance AS 'card.recoverableBalance', c.status AS 'card.status', c.account_id As 'card.accountId', c.expired_date AS 'card.expiredDate', cs.is_show_card_meal_price AS 'cardSetting.isShowCardMealPrice', cs.isprivate AS 'cardSetting.isPrivate'"
					+ " FROM tb_card c LEFT JOIN tb_card_settings cs ON c.id = cs.card_id LEFT JOIN tb_card_batch cb ON cb.id = c.batch_id "
					+ " WHERE cb.new_company_id = "+newCompanyId+" AND cb.organization_type = "+organizationType+" AND cb.operator_id = "+defaccountId+" AND c.type = "+CardTypeEnum.VIRTUAL.getCode();
			if (!accountIdStr.equals("")) {
				cardSql += " AND c.account_id in (" + accountIdStr + ")";
			}

			if (batchIds != null && batchIds.length > 0) {
				String batchIdStr = "";
				for (int i = 0; i < batchIds.length; i++) {
					batchIdStr += batchIds[i] + ",";
				}
				lastIndex = batchIdStr.lastIndexOf(",");
				batchIdStr = batchIdStr.substring(0, lastIndex);
				cardSql += " AND c.batch_id in (" + batchIdStr + ")";
			}

			if (useStatus.equals("0")) {
				// 未使用
				cardSql += " AND c.capacity = c.balance AND (c.status = 0 OR c.status = 1)";
			} else if (useStatus.equals("1")) {
				// 已使用
				cardSql += " AND c.capacity != c.balance AND (c.status = 0 OR c.status = 1)";
			}

			if (hideZeroCapacityCard) {
				cardSql += " AND c.capacity != 0";
			}
			cardSql += " order by c.id";
			log.debug(cardSql);
			List<Map<String, Object>> list = DBMapper.query(cardSql);
			Assert.assertEquals(dto.size(), list.size(), description);
			for (int i = 0; i < dto.size(); i++) {
				CardRecordDto cardRecordDto = dto.get(i);
				Map<String, Object> map = list.get(i);

				// card
				Assert.assertEquals(cardRecordDto.getCard().getAccountId(), map.get("card.accountId"));
				Assert.assertEquals(cardRecordDto.getCard().getBalance(), map.get("card.balance"));
				Assert.assertEquals(cardRecordDto.getCard().getBatchId(), map.get("card.batchId"));
				Assert.assertEquals(cardRecordDto.getCard().getCapacity(), map.get("card.capacity"));
				Assert.assertEquals(cardRecordDto.getCard().getCardName(), map.get("card.cardName"));
				Assert.assertEquals(cardRecordDto.getCard().getCardNum(), map.get("card.cardNum"));
				Assert.assertEquals(cardRecordDto.getCard().getId(), map.get("card.id"));

				// account
				Assert.assertEquals(cardRecordDto.getCard().getStatus(), map.get("card.status"));
				accountSql = "SELECT relationship.birthYear, relationship.customer_id,relationship.department, relationship.employee_id,relationship.igroup, relationship. NAME, relationship.position, relationship.initial_mobile,relationship.id_card, relationship.gender "
						+ " FROM tb_examiner relationship LEFT JOIN tb_account account ON account.id = relationship.customer_id"
						+ " WHERE customer_id = ? AND new_company_id = ? AND organization_type = ? and manager_id=?";
				
				accountList = DBMapper.query(accountSql, cardRecordDto.getCard().getAccountId(), newCompanyId,
						organizationType, defaccountId);
				Assert.assertEquals(cardRecordDto.getAccount().getBirthYear(), accountList.get(0).get("birthYear"));
				Assert.assertEquals(cardRecordDto.getAccount().getCustomerId(), accountList.get(0).get("customer_id"));
				Assert.assertEquals(cardRecordDto.getAccount().getDepartment(), accountList.get(0).get("department"));
				Assert.assertEquals(cardRecordDto.getAccount().getEmployeeId(), accountList.get(0).get("employee_id"));
				Assert.assertEquals(cardRecordDto.getAccount().getGender(), accountList.get(0).get("gender"));
				Assert.assertEquals(cardRecordDto.getAccount().getGroup(), accountList.get(0).get("igroup"));
				Assert.assertEquals(cardRecordDto.getAccount().getIdCard(), accountList.get(0).get("id_Card"));
				Assert.assertEquals(cardRecordDto.getAccount().getName(), accountList.get(0).get("name"));
				Assert.assertEquals(cardRecordDto.getAccount().getPosition(), accountList.get(0).get("position"));
				if (cardRecordDto.getAccount().getInitialMobile()!=null) {
					Assert.assertEquals(cardRecordDto.getAccount().getInitialMobile(), accountList.get(0).get("initial_mobile"));
				}				

				// cardSettings
				String setttingsSql = "select * from tb_card_settings where card_id=?";
				List<Map<String, Object>> settingsList = DBMapper.query(setttingsSql, cardRecordDto.getCard().getId());
				Assert.assertEquals(cardRecordDto.getCardSetting().isPrivate() ? 1 : 0,
						settingsList.get(0).get("isprivate"));
				Assert.assertEquals(cardRecordDto.getCardSetting().isShowCardMealPrice() ? 1 : 0,
						settingsList.get(0).get("is_show_card_meal_price"));
			}
		}
	}

	@DataProvider(name = "allRecords")
	public Iterator<String[]> allRecords() {
		try {
			return CvsFileUtil.fromCvsFileToIterator("./csv/card/allRecords.csv", 7);
		} catch (FileNotFoundException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		return null;
	}

	@DataProvider(name = "cardRecords")
	public Iterator<String[]> cardRecords() {
		try {
			return CvsFileUtil.fromCvsFileToIterator("./csv/card/cardRecords.csv", 7);
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
