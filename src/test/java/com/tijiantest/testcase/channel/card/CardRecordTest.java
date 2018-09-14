package com.tijiantest.testcase.channel.card;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.alibaba.fastjson.JSON;
import com.jayway.jsonpath.JsonPath;
import com.tijiantest.base.Flag;
import com.tijiantest.base.HttpResult;
import com.tijiantest.base.dbcheck.CardChecker;
import com.tijiantest.model.card.CardRecordDto;
import com.tijiantest.model.card.CardRecordQueryDto;
import com.tijiantest.model.organization.OrganizationTypeEnum;
import com.tijiantest.util.DateUtils;
import com.tijiantest.util.pagination.Page;

public class CardRecordTest extends EntityCardBase {

	@Test(description = "渠道端获取实体卡列表", groups = { "qa" }, dataProvider = "cardRecord")
	public void test_getCardRecord(String... args) {
		System.out.println("取参：" + args[0]);
		// caseNum,companyId,cardNum,isDateQuery,searchKey,useStatus,status,capacity,balance
		Integer newCompanyId = null;
		if (!args[1].equals(""))
			newCompanyId = Integer.valueOf(args[1]);
		String cardNum = null;
		if (!args[2].equals(""))
			cardNum = args[2];

		Date createTimeStart = null;
		Date createTimeEnd = null;
		if (args[3].equals("true")) {
			Date now = new Date();
			createTimeStart = DateUtils.offsetDestDay(now, -3);
			createTimeEnd = now;
		}
		String searchKey = null;
		if (!args[4].equals(""))
			searchKey = args[4];

		Integer useStatus = null;
		if (!args[5].equals("")) {
			useStatus = Integer.valueOf(args[5]);
		}
		Integer status = null;
		if (!args[6].equals("")) {
			status = Integer.valueOf(args[6]);
		}

		Boolean cardCapacityIsZero = null;
		if (!args[7].equals("")) {
			if (args[7].equals("true")) {
				cardCapacityIsZero = false;
			} else if (args[7].equals("false"))
				cardCapacityIsZero = true;
		}

		Boolean cardBalanceIsZero = null;
		if (!args[8].equals("")) {
			if (args[8].equals("true")) {
				cardBalanceIsZero = false;
			} else if (args[8].equals("false"))
				cardBalanceIsZero = true;
		}

		Integer fromHospital = defChannelid;

		CardRecordQueryDto dto = new CardRecordQueryDto();
		dto.setFromHospital(fromHospital);
		dto.setNewCompanyId(newCompanyId);
		dto.setCreateTimeStart(createTimeStart != null ? DateUtils.toDayLastSecod(createTimeStart) : null);
		dto.setCreateTimeEnd(createTimeEnd != null ? DateUtils.getStartTime(createTimeEnd) : null);
		dto.setSearchKey(searchKey);
		dto.setCardBalanceIsZero(cardBalanceIsZero);
		dto.setCardCapacityIsZero(cardCapacityIsZero);
		dto.setStatus(status);
		dto.setUseStatus(useStatus);
		dto.setCardNum(cardNum);

		dto.setOrganizationType(OrganizationTypeEnum.CHANNEL.getCode());
		Page page = new Page();
		dto.setPage(page);

		String json = JSON.toJSONString(dto);
		HttpResult result = httpclient.post(Flag.CHANNEL, Card_CardRecord, json);
		Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);
		String body = result.getBody();
//		 System.out.println("body:"+body);
		Page pageResp = JSON.parseObject(JsonPath.read(body, "$.page").toString(), Page.class);
		int rowCount = pageResp.getRowCount();
		List<CardRecordDto> recordsDto = JSON.parseArray(JsonPath.read(body, "$.records").toString(),
				CardRecordDto.class);

		if (checkdb) {
			List<CardRecordDto> recordsDB = CardChecker.getCardRecordsByQuery(dto, true);
			Assert.assertEquals(rowCount, recordsDB.size());
			for (int i = 0; i < rowCount; i++) {
				CardRecordDto res = recordsDto.get(i);
				CardRecordDto db = recordsDB.get(i);
				Assert.assertEquals(res.getCard().getId(), db.getCard().getId());
				Assert.assertEquals(res.getCard().getCardName(), db.getCard().getCardName());
				Assert.assertEquals(res.getCard().getBalance(), db.getCard().getBalance());
				Assert.assertEquals(res.getCard().getCapacity(), db.getCard().getCapacity());
				if (res.getCard().getRecoverableBalance() != null)
					Assert.assertEquals(res.getCard().getRecoverableBalance(), db.getCard().getRecoverableBalance());
				else
					Assert.assertTrue(db.getCard().getRecoverableBalance() == null);
				Assert.assertEquals(res.getCard().getStatus(), db.getCard().getStatus());
				Assert.assertEquals(res.getCard().getNewCompanyId(), db.getCard().getNewCompanyId());
				if (res.getAccount() != null) {
					Assert.assertEquals(res.getAccount().getCustomerId(), db.getAccount().getCustomerId());
					Assert.assertEquals(res.getAccount().getName(), db.getAccount().getName());
					Assert.assertEquals(res.getAccount().getGender(), db.getAccount().getGender());
					Assert.assertEquals(res.getAccount().getBirthYear(), db.getAccount().getBirthYear());
					if (res.getAccount().getIdCard() != null)
						Assert.assertEquals(res.getAccount().getIdCard(), db.getAccount().getIdCard());
					else
						Assert.assertTrue(db.getAccount().getIdCard() == null);
					if (res.getAccount().getMobile() != null)
						Assert.assertEquals(res.getAccount().getMobile(), db.getAccount().getMobile());
					else
						Assert.assertTrue(db.getAccount().getMobile() == null);
					if (res.getAccount().getIsRetire() != null)
						Assert.assertEquals(res.getAccount().getIsRetire(), db.getAccount().getIsRetire());
					else
						Assert.assertTrue(db.getAccount().getIsRetire() == null);
					if (res.getAccount().getPosition() != null)
						Assert.assertEquals(res.getAccount().getPosition(), db.getAccount().getPosition());
					else
						Assert.assertTrue(db.getAccount().getPosition() == null);
				} else
					Assert.assertTrue(db.getAccount() == null);
			}
		}
	}

	@DataProvider(name = "cardRecord")
	public Iterator<String[]> cardRecord() {

		CardRecordQueryDto dto = new CardRecordQueryDto();
		dto.setFromHospital(defChannelid);

		dto.setOrganizationType(OrganizationTypeEnum.CHANNEL.getCode());
		List<CardRecordDto> recordsDB = CardChecker.getCardRecordsByQuery(dto, true);
		List<String[]> tmpParam = new ArrayList<String[]>();
		// caseNum,companyId,cardNum,isDateQuery,searchKey,useStatus,status,capacity,balance
		// isDateQuery
		String[] pa01 = { "01", "", "", "true", "", "", "", "", "" };
		// cardNum
		String[] pa02 = { "02", recordsDB.get(0).getCard().getNewCompanyId() + "",
				recordsDB.get(0).getCard().getCardNum(), "false", "", "", "", "", "" };
		// searchKey
		List<CardRecordDto> crdList1 = recordsDB.stream().filter(r -> r.getAccount() != null)
				.collect(Collectors.toList());
		CardRecordDto crd1 = new CardRecordDto();
		String pa03[] = new String[] { "03", "", "", "", "", "", "", "", "" };
		// searchKey--name
		if (crdList1.size() > 0) {
			crd1 = crdList1.get(0);
			pa03[0] = "03";
			pa03[1] = crd1.getCard().getNewCompanyId() + "";
			pa03[2] = "";
			pa03[3] = "false";
			pa03[4] = crd1.getAccount().getName();
			pa03[5] = "";
			pa03[6] = "";
			pa03[7] = "";
			pa03[8] = "";
		}
		List<CardRecordDto> crdList2 = recordsDB.stream()
				.filter(r -> r.getAccount() != null && r.getAccount().getIdCard() != null).collect(Collectors.toList());
		CardRecordDto crd2 = new CardRecordDto();
		String pa04[] = new String[] { "04", "", "", "", "", "", "", "", "" };
		// searchKey--idcard
		if (crdList2.size() > 0) {
			crd2 = crdList2.get(0);
			pa04[0] = "04";
			pa04[1] = crd2.getCard().getNewCompanyId() + "";
			pa04[2] = "";
			pa04[3] = "false";
			pa04[4] = crd2.getAccount().getIdCard();
			pa04[5] = "";
			pa04[6] = "";
			pa04[7] = "";
			pa04[8] = "";
		}
		List<CardRecordDto> crdList3 = recordsDB.stream()
				.filter(r -> r.getAccount() != null && r.getAccount().getMobile() != null).collect(Collectors.toList());
		CardRecordDto crd3 = new CardRecordDto();
		String pa05[] = new String[] { "05", "", "", "", "", "", "", "", "" };
		// searchKey--mobile
		if (crdList3.size() > 0) {
			crd3 = crdList3.get(0);
			pa05[0] = "05";
			pa05[1] = crd3.getCard().getNewCompanyId() + "";
			pa05[2] = "";
			pa05[3] = "false";
			pa05[4] = crd3.getAccount().getMobile();
			pa05[5] = "";
			pa05[6] = "";
			pa05[7] = "";
			pa05[8] = "";
		}

		String[] pa06 = { "06", "", "", "false", "", "0", "", "", "" };// useStatus--0
		String[] pa07 = { "07", "", "", "false", "", "", "2", "", "" };// status--2

		String[] pa08 = { "08", "", "", "false", "", "", "", "true", "" };// capacacity>0
		String[] pa09 = { "09", "", "", "false", "", "", "", "", "true" };// balance>0
		tmpParam.add(pa01);
		tmpParam.add(pa02);
		tmpParam.add(pa03);
		tmpParam.add(pa04);
		tmpParam.add(pa05);
		tmpParam.add(pa06);
		tmpParam.add(pa07);
		tmpParam.add(pa08);
		tmpParam.add(pa09);

		return tmpParam.iterator();
	}
}
