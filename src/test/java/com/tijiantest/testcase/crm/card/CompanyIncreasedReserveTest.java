package com.tijiantest.testcase.crm.card;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.tijiantest.util.DateUtils;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.tijiantest.base.HttpResult;
import com.tijiantest.base.dbcheck.AccountChecker;
import com.tijiantest.base.dbcheck.HospitalChecker;
import com.tijiantest.base.dbcheck.ResourceChecker;
import com.tijiantest.testcase.crm.CrmBase;
import com.tijiantest.util.CvsFileUtil;
import com.tijiantest.util.db.DBMapper;
import com.tijiantest.util.db.SqlException;
import com.tijiantest.model.resource.meal.Meal;

/**
 * 发卡，选择自动下单时，后端接口校验需要增加的单位人数
 */
public class CompanyIncreasedReserveTest extends CrmBase {

	@Test(description = "发卡选择自动下单时，计算单位要增加的单位预留人数", groups = { "qa" }, dataProvider = "companyIcreaseReserve")
	public void test_01_companyIcreaseReserve(String... args) throws SqlException {

		// 获取平台客户经理支持的体检单位, false -普通客户经理
		List<Integer> companyIdList = AccountChecker.getCompanysIdByManagerId(httpclient, defaccountId, false);
		// 随机取一个
		Random random = new Random();
		int index = random.nextInt(companyIdList.size()) % (companyIdList.size() + 1);
		Integer companyId = companyIdList.get(index);
//		companyId = 4401319;
		System.out.println("companyId = " + companyId);
		int organizationType = HospitalChecker.getOrganizationType(defhospital.getId());
		int hospitalId = defhospital.getId();
		int examItemId = -1;
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String date = simpleDateFormat.format(DateUtils.offDate(1));// 后一天
		int cardSumNum = Integer.parseInt(args[1]);
		List<Meal> mealList = ResourceChecker.getOffcialMeal(hospitalId);
		int mealId = 0;
		for (Meal meal : mealList) {
			if (meal.getGender() != 2) {// 通用性别
				mealId = meal.getId();
				break;
			}
		}
		if (mealId == 0) {
			log.info("无可用的官方套餐 或性别都为通用！！！");
			return;
		}

		System.out.println("mealId = " + mealId);

		List<NameValuePair> pairs = new ArrayList<>();
		pairs.add(new BasicNameValuePair("companyId", companyId + ""));
		pairs.add(new BasicNameValuePair("newCompanyId", companyId + ""));
		pairs.add(new BasicNameValuePair("organizationType", organizationType + ""));
		pairs.add(new BasicNameValuePair("hospitalId", hospitalId + ""));
		pairs.add(new BasicNameValuePair("examItemId", examItemId + ""));
		pairs.add(new BasicNameValuePair("date", date));
		pairs.add(new BasicNameValuePair("cardSumNum", cardSumNum + ""));
		pairs.add(new BasicNameValuePair("mealId", mealId + ""));

		HttpResult result = httpclient.get(Card_CompanyIncreseReserve, pairs);
		String body = result.getBody();
		System.out.println(body);
		int firstIndex = body.indexOf(":");
		int lastIndex = body.lastIndexOf("}");
		String num = body.substring(firstIndex + 1, lastIndex);

		Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);

		if (checkdb) {
			// 先查询该单位当天的剩余量
			int reservedNum;
			String reservedNumSql = "SELECT sum(reservation_num - used_num) as total FROM tb_company_capacity_used where company_id =? and hospital_id = ? and cur_date >=? and exam_item = -1 and is_release = 0";
			List<Map<String, Object>> reservedNumList = DBMapper.query(reservedNumSql, companyId, hospitalId, date);
			reservedNum = reservedNumList.get(0).get("total") == null ? 0
					: Integer.parseInt(reservedNumList.get(0).get("total").toString());

			// 再查询该单位在截止日期前未使用的卡
			String unusedCardNumSql = "SELECT count(1) FROM tb_card c LEFT JOIN tb_card_batch cb on c.batch_id = cb.id "
					+ " WHERE cb.default_meal_id is not null and c.capacity = balance and cb.new_company_id =? "
					+ " and cb.organization_type = ?  and cb.booking_deadline =? and cb.default_meal_id in"
					+ " (select id from tb_meal where hospital_id = (select hospital_id from tb_meal where id = ?))";
			List<Map<String, Object>> unusedCardNumList = DBMapper.query(unusedCardNumSql, companyId, organizationType,
					date, mealId);
			int unusedCardSum;
			unusedCardSum = unusedCardNumList.get(0).get("count(1") == null ? 0
					: Integer.parseInt(unusedCardNumList.get(0).get("count(1)").toString());

			reservedNum = reservedNum - unusedCardSum;

			int resultNum = 0;
			resultNum = reservedNum < cardSumNum ? cardSumNum - reservedNum : 0;
			Assert.assertEquals(Integer.parseInt(num.trim()), resultNum);

		}

	}

	@DataProvider(name = "companyIcreaseReserve")
	public Iterator<String[]> companyIcreaseReserve() {
		try {
			return CvsFileUtil.fromCvsFileToIterator("./csv/card/companyIcreaseReserve.csv", 11);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}
}
