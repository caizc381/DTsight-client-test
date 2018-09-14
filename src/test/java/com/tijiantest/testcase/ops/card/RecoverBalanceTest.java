package com.tijiantest.testcase.ops.card;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.tijiantest.model.account.Examiner;
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
import com.tijiantest.base.dbcheck.CardChecker;
import com.tijiantest.model.account.AccountRelationInCrm;
import com.tijiantest.model.card.Card;
import com.tijiantest.model.card.CardManageDto;
import com.tijiantest.model.card.CardRecordDto;
import com.tijiantest.model.card.CardStatusEnum;
import com.tijiantest.testcase.crm.card.DistributeCardTest;
import com.tijiantest.testcase.ops.OpsBase;
import com.tijiantest.util.CvsFileUtil;
import com.tijiantest.util.db.DBMapper;
import com.tijiantest.util.db.SqlException;

public class RecoverBalanceTest extends OpsBase{

	@Test(description="金额回收",groups= {"qa"},dependsOnGroups= {"crm_distributeCard"},dataProvider="recoverBalance",ignoreMissingDependencies = true)
	public void test_recoverBalance(String... args) throws SqlException {
		String rowCount = args[1];
		String currentPage = args[2];
		String pageSize = args[3];

		List<Integer> accountIds = new ArrayList<>();
		try{
			accountIds = DistributeCardTest.accountIds;
		}catch (Exception e){
			log.error("CRM未启动，手动指定用户");
		}
		if(accountIds == null || accountIds.size() == 0){
			String sql = "select * from tb_card where status = "+CardStatusEnum.USABLE.getCode()+" and type  = 1 and parent_card_id is not null order by id desc limit 1";
			List<Map<String,Object>> dbList = DBMapper.query(sql);
			Map<String,Object> map = dbList.get(0);
			accountIds.add(Integer.parseInt(map.get("account_id").toString()));
		}
		int accountId = accountIds.get(0);
		String sql = "select * from tb_account where id=?";
		List<Map<String, Object>> list = DBMapper.query(sql, accountId);

		CardRecordDto cardRecordDto = new CardRecordDto();
		Examiner account = new Examiner();
		account.setName(list.get(0).get("name").toString());
		cardRecordDto.setAccount(account);

		// 获取卡 ， /ops/action/card接口
		List<NameValuePair> params = new ArrayList<>();
		params.add(new BasicNameValuePair("rowCount", rowCount));
		params.add(new BasicNameValuePair("currentPage", currentPage));
		params.add(new BasicNameValuePair("pageSize", pageSize));
		params.add(new BasicNameValuePair("accountId", accountId + ""));
		HttpResult response = httpclient.get(Flag.OPS, OPS_Card, params);
		Assert.assertEquals(response.getCode(), HttpStatus.SC_OK);
		String body = response.getBody();
		Map<String, Object> cardList = JsonPath.read(body, "$.cardList.[*]");

		List<CardManageDto> records = JSON.parseArray(cardList.get("records").toString(), CardManageDto.class);

		CardManageDto cmt = null;

		for (int i = 0; i < records.size(); i++) {
			CardManageDto cardManageDto = records.get(i);

			if ((cardManageDto.getStatus() == CardStatusEnum.USABLE.getCode()
					&& cardManageDto.getCapacity().equals(cardManageDto.getBalance()))
					|| (cardManageDto.getStatus() == CardStatusEnum.USABLE.getCode()
							&& cardManageDto.getCapacity() > cardManageDto.getBalance())) {
				cmt = cardManageDto;// 金额回收
			}
			if (cmt != null) {
				break;
			}
		}

		if (cmt == null) {
			log.info("没有可金额回收的卡！！！！");
			return;
		}
		Card card = new Card();
		card.setAccountId(accountId);
		card.setAvailableDate(cmt.getAvailableDate());
		card.setBalance(cmt.getBalance());
		card.setCapacity(cmt.getCapacity());
		card.setBalance(cmt.getBalance());
		card.setCardName(cmt.getCardName());
		card.setCardNum(cmt.getCardNum());
		card.setCardSetting(cmt.getCardSetting());
		card.setCompanyId(cmt.getCompanyId());
		card.setCreateDate(cmt.getCreateDate());
		card.setExamNoteId(cmt.getExamNoteId());
		card.setExpiredDate(cmt.getExpiredDate());
		card.setFreezeBalance(cmt.getFreezeBalance());
		card.setFromHospital(cmt.getFromHospital());
		card.setHospitalSettlementStatus(cmt.getHospitalSettlementStatus());
		System.out.println("要金额回收的卡ID:" + cmt.getId());
		card.setId(cmt.getId());
		card.setIsDeleted(cmt.getIsDeleted());
		card.setManagerId(cmt.getManagerId());
		card.setNewCompanyId(cmt.getNewCompanyId());
		card.setOrganizationType(cmt.getOrganizationType());
		card.setParentCardId(cmt.getParentCardId());
		card.setPassword(cmt.getPassword());
		card.setRechargeTime(cmt.getRechargeTime());
		card.setRecoverableBalance(cmt.getRecoverableBalance());
		card.setSettlementBatchSn(cmt.getSettlementBatchSn());
		card.setStatus(cmt.getStatus());
		card.setTradeAccountId(cmt.getTradeAccountId());
		card.setType(cmt.getType());

		cardRecordDto.setCard(card);

		String json = JSON.toJSONString(cardRecordDto);

		HttpResult result = httpclient.post(Flag.OPS, OPS_RecoverBalance, json);
		System.out.println(result.getBody());
		Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);

		if (checkdb) {
			Card resultCard = CardChecker.getCardById(card.getId());
			Assert.assertEquals(resultCard.getStatus(), CardStatusEnum.BALANCE_RECOVERED.getCode());
		}
	}
	
	@DataProvider(name = "recoverBalance")
	public Iterator<String[]> recoverBalance() {
		try {
			return CvsFileUtil.fromCvsFileToIterator("./csv/card/ops/recoverBalance.csv", 10);
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
