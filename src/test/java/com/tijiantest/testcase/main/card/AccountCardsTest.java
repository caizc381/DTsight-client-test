package com.tijiantest.testcase.main.card;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.*;

import com.tijiantest.base.dbcheck.HospitalChecker;
import com.tijiantest.model.card.Card;
import com.tijiantest.model.hospital.Hospital;
import com.tijiantest.util.ListUtil;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.poi.util.SystemOutLogger;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.jayway.jsonpath.JsonPath;
import com.tijiantest.base.Flag;
import com.tijiantest.base.HttpResult;
import com.tijiantest.base.dbcheck.CardChecker;
import com.tijiantest.model.card.CardStatusEnum;
import com.tijiantest.testcase.main.MainBase;
import com.tijiantest.util.CvsFileUtil;
import com.tijiantest.util.db.DBMapper;
import com.tijiantest.util.db.SqlException;

public class AccountCardsTest extends MainBase {

	@SuppressWarnings("unchecked")
	@Test(description = "体检中心列表-卡信息", groups = { "qa" }, dataProvider = "accountCards")
	public void test_01_accountCards(String... args) throws SqlException, ParseException {
		String _site = args[1];
		String _siteType = args[2];

		List<NameValuePair> pairs = new ArrayList<>();
		pairs.add(new BasicNameValuePair("_site", _site));
		pairs.add(new BasicNameValuePair("_siteType", _siteType));

		HttpResult result = httpclient.get(Flag.MAIN, Card_AccountCards, pairs);
		String body = result.getBody();
		log.info(body);
		Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);

		Map<String, Object> cardHospitals = JsonPath.read(body, "$.rels.[*]");
		
		if (checkdb) {
			String today = sdf.format(new Date())+" 00:00:00";
		    List<Integer> cards =  CardChecker.getUserCanUseCardList(_site,defaccountId,null);
			Assert.assertEquals(cardHospitals.size(),cards.size());
			for(int i=0;i<cards.size();i++){
				int cid = cards.get(i).intValue();
				List<Integer> hsList = CardChecker.getHospitalIdByCardId(cid);
				log.debug("XXXX"+cardHospitals);
				log.debug("XXXX"+cardHospitals.get(cid+"").toString());
				List<Integer>  expectSize = (List<Integer>)cardHospitals.get(cid+"");
				Assert.assertEquals(hsList.size(), expectSize.size());
			}

		}
	}

	@DataProvider(name = "accountCards")
	public Iterator<String[]> accountCards() {
		try {
			return CvsFileUtil.fromCvsFileToIterator("./csv/card/main/accountCards.csv", 10);
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
