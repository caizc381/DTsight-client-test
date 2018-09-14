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
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.alibaba.fastjson.JSON;
import com.tijiantest.base.HttpResult;
import com.tijiantest.base.dbcheck.HospitalChecker;
import com.tijiantest.model.card.CardBatchVo;
import com.tijiantest.testcase.crm.CrmBase;
import com.tijiantest.util.CvsFileUtil;
import com.tijiantest.util.db.DBMapper;
import com.tijiantest.util.db.SqlException;

/**
 * 获取单位发卡批次信息
 * 
 * @author huifang
 *
 */
public class CardBatchsTest extends CrmBase {

	@Test(description = "获取单位发卡批次信息/发卡记录-发卡批次", groups = { "qa", "crm_cardBatch" }, dataProvider = "cardbatch_success")
	public void test_01_cardBatch_success(String... args) throws SqlException {
		/// cardBatchs/{companyId}
		Integer companyid = defSKXCnewcompany.getId();
		String beginDate = args[2];
		String endDate = args[3];
		
//		HospitalCompany hospitalCompany = CompanyChecker.getHospitalCompanyById(companyid);;
		int organizationType = HospitalChecker.getOrganizationType(defhospital.getId());
		List<NameValuePair> pairs = new ArrayList<>();
		pairs.add(new BasicNameValuePair("organizationType", organizationType + ""));
		if (beginDate!=null && !beginDate.equals("")) {
			pairs.add(new BasicNameValuePair("beginDate", beginDate));
		}
		
		if (endDate!=null && !endDate.equals("")) {
			pairs.add(new BasicNameValuePair("endDate", endDate));
		}

		HttpResult result = httpclient.get(Card_CardBatchs, pairs, companyid + "");

		Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);
		String body = result.getBody();
//		System.out.println(body);
		List<CardBatchVo> cardBatchVos = JSON.parseArray(body, CardBatchVo.class);
		Collections.sort(cardBatchVos, new Comparator<CardBatchVo>() {
			@Override
			public int compare(CardBatchVo o1, CardBatchVo o2) {
				return o1.getId() - o2.getId();
			}
		});
		if (checkdb) {
			String sql = " SELECT  cb.id, cb.new_company_id,cb.card_name,cb.capacity,cb.amount,cb.operator_id,cb.create_time,"
					+ "cb.booking_deadline,cb.is_send_bookingmsg,cb.is_send_card_msg,cb.remark, "
					+ "cb.default_meal_id,cb.exam_note,cb.query_condition,cb.expired_date,"
					+ "cb.new_company_id,cb.organization_type,ac.name as operatorName "
					+ "FROM tb_card_batch cb LEFT JOIN tb_account ac ON cb.operator_id = ac.id "
					+ "WHERE cb.new_company_id = ? AND cb.organization_type=? AND cb.operator_id = ?";
			if (beginDate!=null&&!beginDate.equals("") && endDate!=null&&!endDate.equals("")) {

				sql += " AND cb.create_time between '"+beginDate+" 00:00:00' and '"+endDate+" 23:59:59'";
			}
			
			sql += "  ORDER BY cb.id";
			
			System.out.println(sql);
			List<Map<String, Object>> dblist = DBMapper.query(sql, companyid, organizationType, defaccountId);

			Assert.assertEquals(cardBatchVos.size(), dblist.size());
			for (int i = 0; i < cardBatchVos.size(); i++) {
				Assert.assertEquals(cardBatchVos.get(i).getAmount(), dblist.get(i).get("amount"));
				Assert.assertEquals(cardBatchVos.get(i).getCapacity() + "", dblist.get(i).get("capacity") + "");
				Assert.assertEquals(cardBatchVos.get(i).getCardName(), dblist.get(i).get("card_name"));
				Assert.assertEquals(cardBatchVos.get(i).getNewCompanyId(), dblist.get(i).get("new_company_id"));
				Assert.assertEquals(cardBatchVos.get(i).getId(), dblist.get(i).get("id"));
				Boolean dflag = false;
				if (dblist.get(i).get("is_send_bookingmsg") == null
						|| dblist.get(i).get("is_send_bookingmsg").toString() == "0")
					dflag = false;
				Assert.assertEquals(cardBatchVos.get(i).getIsSendBookingMsg(), dflag);
				Assert.assertEquals(cardBatchVos.get(i).getIsSendCardMsg() ? 1 : 0,
						dblist.get(i).get("is_send_card_msg"));
				Assert.assertEquals(cardBatchVos.get(i).getNewCompanyId(), dblist.get(i).get("new_company_id"));
				Assert.assertEquals(cardBatchVos.get(i).getOrganizationType(), dblist.get(i).get("organization_type"));
				Assert.assertEquals(cardBatchVos.get(i).getRemark(), dblist.get(i).get("remark"));
				Assert.assertEquals(cardBatchVos.get(i).getDefaultMealId(), dblist.get(i).get("default_meal_id"));
				Assert.assertEquals(cardBatchVos.get(i).getOperatorName(), dblist.get(i).get("operatorName"));
				Assert.assertEquals(cardBatchVos.get(i).getOperatorId(), dblist.get(i).get("operator_id"));

			}
		}
	}

	@DataProvider
	public Iterator<String[]> cardbatch_success() {
		try {
			return CvsFileUtil.fromCvsFileToIterator("./csv/card/cardbatch_success.csv", 2);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}
}
