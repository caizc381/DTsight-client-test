package com.tijiantest.testcase.ops.card;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.alibaba.fastjson.JSON;
import com.jayway.jsonpath.JsonPath;
import com.tijiantest.base.Flag;
import com.tijiantest.base.HttpResult;
import com.tijiantest.model.account.AccountManageDto;
import com.tijiantest.model.card.CardManageDto;
import com.tijiantest.model.card.CardStatusEnum;
import com.tijiantest.model.company.ChannelGuestCompanyEnum;
import com.tijiantest.model.company.HospitalGuestCompanyEnum;
import com.tijiantest.model.organization.OrganizationTypeEnum;
import com.tijiantest.testcase.ops.OpsBase;
import com.tijiantest.util.CvsFileUtil;
import com.tijiantest.util.DateUtils;
import com.tijiantest.util.db.DBMapper;
import com.tijiantest.util.db.SqlException;

/**
 * 用户查询->用户的体检卡
 */
public class CardTest extends OpsBase {

	@Test(description = "获取体检人的卡信息集合", groups = { "qa", "manage_card" }, dataProvider = "card")
	public void test_01_card(String... args) throws SqlException, ParseException {

		// 先搜索用户，获取用户ID
		String rowCount = args[1];
		String currentPage = args[2];
		String pageSize = args[3];
		String name = args[4];
		String mobile = args[5];
		String idCard = args[6];
//		String description = args[7];

		List<NameValuePair> pairs = new ArrayList<>();
		pairs.add(new BasicNameValuePair("rowCount", rowCount));
		pairs.add(new BasicNameValuePair("currentPage", currentPage));
		pairs.add(new BasicNameValuePair("pageSize", pageSize));
		pairs.add(new BasicNameValuePair("name", name));
		pairs.add(new BasicNameValuePair("mobile", mobile));
		pairs.add(new BasicNameValuePair("idCard", idCard));

		HttpResult result = httpclient.get(Flag.OPS, OPS_AccountManagePageInfo, pairs);
		Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);

		List<AccountManageDto> accountManageDtos = JSON.parseArray(result.getBody(), AccountManageDto.class);

		if (!accountManageDtos.isEmpty()) {
			// 取第一个用户，根据用户ID，获取对应的订单
			int accountId = accountManageDtos.get(0).getId();
			List<NameValuePair> params = new ArrayList<>();
			params.add(new BasicNameValuePair("rowCount", rowCount));
			params.add(new BasicNameValuePair("currentPage", currentPage));
			params.add(new BasicNameValuePair("pageSize", pageSize));
			params.add(new BasicNameValuePair("accountId", String.valueOf(accountId)));

			HttpResult response = httpclient.get(Flag.OPS, OPS_Card, params);
			Assert.assertEquals(response.getCode(), HttpStatus.SC_OK);
			String body = response.getBody();
			Map<String, Object> cardList = JsonPath.read(body, "$.cardList.[*]");

			List<CardManageDto> records = JSON.parseArray(cardList.get("records").toString(), CardManageDto.class);
			if (!records.isEmpty()) {
				Integer cardTotalMoney = JsonPath.read(body, "$.cardTotalMoney");
				Integer totalCard = JsonPath.read(body, "$.totalCard");

				if (checkdb) {
					String sql = "select distinct tb_card.id, tb_card.batch_id, tb_card.card_name, tb_card.card_num, tb_card.password, tb_card.capacity, tb_card.balance, tb_card.recoverable_balance,"
							+ " tb_card.type, tb_card.status, tb_card.from_hospital, tb_card.recharge_time, tb_card.parent_card_id, tb_card.available_date, tb_card.expired_date,"
							+ " tb_card.create_date, tb_card.account_id, tb_card.manager_id, tb_card.new_company_id, tb_card.organization_type, tb_role.name as managerRole"
							+ " from tb_card left join tb_account_role on tb_account_role.account_id = tb_card.manager_id"
							+ " left join tb_role on tb_role.id = tb_account_role.role_id"
							+ " where tb_card.account_id = ? and tb_account_role.role_id > 2 order by tb_card.id desc";
					log.info(sql);
					List<Map<String, Object>> list = DBMapper.query(sql, accountId);
					Assert.assertEquals(totalCard.intValue(), list.size());
					for (int i = 0; i < records.size(); i++) {
						log.info("card_id="+records.get(i).getId());
						CardManageDto cardManageDto = records.get(i);
						Map<String, Object> map = list.get(i);
//						int cardId = cardManageDto.getId();
						// balance
						Assert.assertEquals(cardManageDto.getBalance(), map.get("balance"));
						// capacity
						Assert.assertEquals(cardManageDto.getCapacity(), map.get("capacity"));
						// cardName
						Assert.assertEquals(cardManageDto.getCardName(), map.get("card_name"));
						// cardNum
						Assert.assertEquals(cardManageDto.getCardNum(), map.get("card_num"));
						// companyName
						if (cardManageDto.getOrganizationType() == OrganizationTypeEnum.HOSPITAL.getCode()) {
							if (cardManageDto.getNewCompanyId() != null) {
								String hospitalCompanySql = "select id, gmt_created, gmt_modified, name, platform_company_id, organization_id, organization_name, discount, show_report, settlement_mode,"
										+ " his_name, advance_export_order, send_exam_sms, send_exam_sms_days, pinyin, is_deleted, tb_exam_company_id, examination_address, examreport_interval_time"
										+ " from tb_hospital_company where id = ?";
								List<Map<String, Object>> hospitalCompanyList = DBMapper.query(hospitalCompanySql,
										cardManageDto.getNewCompanyId());
								Assert.assertEquals(cardManageDto.getCompanyName(),
										hospitalCompanyList.get(0).get("name"));
								// companyType
								if (hospitalCompanyList.get(0).get("platform_company_id") == null) {
									Assert.assertEquals(cardManageDto.getCompanyType().toString(), 0 + "");
								} else if (Integer.valueOf(hospitalCompanyList.get(0).get("platform_company_id")
										.toString()) > HospitalGuestCompanyEnum.HOSPITAL_MTJK.getPlatformCompanyId()) {
									Assert.assertEquals(cardManageDto.getCompanyType().toString(), 1 + "");
								}
							}
						}

						if (cardManageDto.getOrganizationType() == OrganizationTypeEnum.CHANNEL.getCode()) {
							if (cardManageDto.getNewCompanyId() != null) {
								String channelCompanySql = "select id,gmt_created,gmt_modified,name,platform_company_id,organization_id,organization_name,discount,settlement_mode,send_exam_sms,"
										+ " send_exam_sms_days,pinyin,is_deleted,tb_exam_company_id"
										+ " from tb_channel_company where id=? and is_deleted=0";
								List<Map<String, Object>> channelCompanyList = DBMapper.query(channelCompanySql,
										cardManageDto.getNewCompanyId());
								Map<String, Object> channelCompanyMap = channelCompanyList.get(0);
								if (channelCompanyList.size() > 0) {
									Assert.assertEquals(cardManageDto.getCompanyName(),
											channelCompanyList.get(0).get("name"));
									if (channelCompanyMap.get("platform_company_id") != null
											&& Integer.valueOf(channelCompanyMap.get("platform_company_id")
													.toString()) == HospitalGuestCompanyEnum.HOSPITAL_MTJK
															.getPlatformCompanyId()) {
										Assert.assertEquals(cardManageDto.getCompanyType().toString(), 2 + "");
									}
									if (channelCompanyMap.get("platform_company_id") != null
											&& Integer.valueOf(channelCompanyMap.get("platform_company_id")
													.toString()) > ChannelGuestCompanyEnum.CHANNEL_GUEST_OFFLINE
															.getPlatformCompanyId()) {
										Assert.assertEquals(cardManageDto.getCompanyType().toString(), 1 + "");
									}
								}

							}
						}

						// fromHospital
						Assert.assertEquals(cardManageDto.getFromHospital(), map.get("from_hospital"));
						// id
						Assert.assertEquals(cardManageDto.getId(), map.get("id"));

						// managerId
						Assert.assertEquals(cardManageDto.getManagerId(), map.get("manager_id"));

						// managerName
						String managerSql = "select * from tb_account where id=?";
						List<Map<String, Object>> managerList = DBMapper.query(managerSql,
								cardManageDto.getManagerId());
						Assert.assertEquals(cardManageDto.getManagerName(), managerList.get(0).get("name"));

						// managerRole
						Assert.assertEquals(cardManageDto.getManagerRole(), map.get("managerRole"));

						// newCompanyId
						Assert.assertEquals(cardManageDto.getNewCompanyId(), map.get("new_company_id"));

						// organizationName
						if (cardManageDto.getFromHospital() != null) {
							String hospitalSql = "select * from tb_hospital where id=?";
							List<Map<String, Object>> hospitalList = DBMapper.query(hospitalSql,
									cardManageDto.getFromHospital());
							Assert.assertEquals(cardManageDto.getOrganizationName(),
									hospitalList.size() == 0 ? null : hospitalList.get(0).get("name").toString());
						} else {
							Assert.assertNull(cardManageDto.getOrganizationName());
						}
						// organizationType
						Assert.assertEquals(cardManageDto.getOrganizationType(), map.get("organization_type"));
						// parentCardId
						Assert.assertEquals(cardManageDto.getParentCardId(), map.get("parent_card_id"));
						// status
						Assert.assertEquals(cardManageDto.getStatus(), map.get("status"));
						// type
						Assert.assertEquals(cardManageDto.getType(), map.get("type"));

						Assert.assertEquals(sdf.format(new java.util.Date(cardManageDto.getExpiredDate().getTime())),
								sdf.format(map.get("expired_date")));
						Assert.assertEquals(sdf.format(new java.util.Date(cardManageDto.getAvailableDate().getTime())),
								sdf.format(map.get("available_date")));
						Assert.assertEquals(sdf.format(new java.util.Date(cardManageDto.getCreateDate().getTime())),
								sdf.format(map.get("create_date")));
						Assert.assertEquals(sdf.format(new java.util.Date(cardManageDto.getRechargeTime().getTime())),
								sdf.format(map.get("recharge_time")));
					}

					sql = "select tb_card.id, batch_id, card_name, card_num, password, capacity, balance, recoverable_balance, tb_card.type, status, from_hospital, recharge_time, parent_card_id, available_date, expired_date, create_date, tb_card.account_id from tb_card where status =1 and account_id = ? order by balance desc, expired_date";
					list = DBMapper.query(sql, accountId);

					Integer totalMoney = 0;
					if (totalCard != null) {
						Assert.assertEquals(totalCard, new Integer(list.size()));
						for (Map<String, Object> map : list) {
							if (Integer.valueOf(map.get("status").toString()) == CardStatusEnum.USABLE.getCode()) {
								Long timeNum = new Date().getTime();
								int lastIndex = map.get("expired_date").toString().lastIndexOf(".");
								String expiredDate = map.get("expired_date").toString().substring(0, lastIndex);
								if (timeNum <= DateUtils.parse("yyyy-MM-dd HH:mm:ss", expiredDate).getTime()) {
									totalMoney += Integer.valueOf(map.get("balance").toString());
								}
							}
						}
					}
					Assert.assertEquals(cardTotalMoney == null ? new Integer(0) : cardTotalMoney, totalMoney);
				}
			}
		}
	}

	@DataProvider(name = "card")
	public Iterator<String[]> card() {
		try {
			return CvsFileUtil.fromCvsFileToIterator("./csv/card/ops/card.csv", 10);
		} catch (FileNotFoundException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		return null;
	}

	public String parse(String date) {
		int lastIndex = date.lastIndexOf(".");
		return date.substring(0, lastIndex);
	}
}
