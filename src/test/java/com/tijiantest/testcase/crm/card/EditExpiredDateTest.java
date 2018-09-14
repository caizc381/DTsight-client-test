package com.tijiantest.testcase.crm.card;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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
import com.tijiantest.model.card.EditExpiredDateDto;
import com.tijiantest.testcase.crm.CrmBase;
import com.tijiantest.util.CvsFileUtil;
import com.tijiantest.util.db.DBMapper;
import com.tijiantest.util.db.SqlException;

public class EditExpiredDateTest extends CrmBase {

	@Test(description = "所有卡记录 - 修改卡的有效期", groups = { "qa", "crm_card" }, dataProvider = "editExpiredDate_allRecords")
	public void test_01_editExpiredDate(String... args) throws SqlException, ParseException {
		// 先获取卡列表
		Integer[] batchIds = null;
		Integer companyId = CompanyChecker.getHCompanyByOldCompanyIdANDOrgId(Integer.valueOf(args[1]), defhospital.getId()).getId();
		int organizationType = HospitalChecker.getOrganizationType(defhospital.getId());
		boolean hideZeroCapacityCard = new Boolean(args[2]).booleanValue();
		int currentPage = Integer.parseInt(args[3]);
//		int pageSize = Integer.parseInt(args[4]);
		int rowCount = Integer.parseInt(args[5]);
		String searchKey = args[6];
		String useStatus = args[7];
		String expire = args[8];
//		String description = args[9];	
		
		
		String json = CardChecker.generateJson(batchIds, hideZeroCapacityCard, companyId, organizationType, searchKey, useStatus, currentPage, null, rowCount);
//		String json = CardBaseTest.generateJson(batchIds, companyId, hideZeroCapacityCard, name, status, group,
//				department, useStatus, currentPage, pageSize, rowCount);

		HttpResult result = httpclient.post(Card_CardRecords, json);
		Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);
		String body = result.getBody();

		List<CardRecordDto> dto = JSON.parseObject(JsonPath.read(body, "$.records").toString(),
				new TypeReference<List<CardRecordDto>>() {
				});

		// 修改第一张和第二张卡的有效期
		Integer[] cardIds = new Integer[2];
		for (int i = 0; i < 2; i++) {
			cardIds[i] = dto.get(i).getCard().getId();
		}

		// 过期时间是当前日期
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date expiredDate = sdf.parse(sdf.format(new Date()));
		EditExpiredDateDto editExpiredDateDto = new EditExpiredDateDto();
		editExpiredDateDto.setCardIds(cardIds);
		if (!expire.equals("")) {
			expiredDate = sdf.parse(expire);
		}
		editExpiredDateDto.setExpiredDate(expiredDate);

		String editExpiredDateJson = JSON.toJSONString(editExpiredDateDto);

		result = httpclient.post(Card_EditExpiredDate, editExpiredDateJson);

		Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);
		Assert.assertEquals(result.getBody(), "");

		if (checkdb) {
			for (int i = 0; i < cardIds.length; i++) {
				String sql = "select * from tb_card where id=?";
				List<Map<String, Object>> list = DBMapper.query(sql, cardIds[i]);

				String expireDateResult = list.get(0).get("expired_date").toString();
				int index = expireDateResult.indexOf(" ");
				expireDateResult = expireDateResult.substring(0, index);

				Assert.assertEquals(sdf.format(expiredDate), expireDateResult, "卡ID=" + cardIds[i]);
			}
		}
	}

	@DataProvider(name = "editExpiredDate_allRecords")
	public Iterator<String[]> allRecords() {
		try {
			return CvsFileUtil.fromCvsFileToIterator("./csv/card/editExpiredDate_allRecords.csv", 7);
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
