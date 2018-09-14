package com.tijiantest.testcase.main.payment.withdraw;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.tijiantest.base.dbcheck.AccountChecker;
import com.tijiantest.base.dbcheck.CardChecker;
import com.tijiantest.base.dbcheck.HospitalChecker;
import com.tijiantest.base.dbcheck.PayChecker;
import com.tijiantest.model.account.Examiner;
import com.tijiantest.model.account.ExaminerVo;
import com.tijiantest.model.card.Card;
import com.tijiantest.model.card.CardStatusEnum;
import com.tijiantest.model.coupon.UserCouponReceive;
import com.tijiantest.model.item.ExamItem;
import com.tijiantest.model.site.Site;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.jayway.jsonpath.JsonPath;
import com.tijiantest.base.Flag;
import com.tijiantest.base.HttpResult;
import com.tijiantest.model.account.Account;
import com.tijiantest.testcase.main.MainBase;
import com.tijiantest.util.db.DBMapper;
import com.tijiantest.util.db.SqlException;

/**
 * 位置：C端个人中心->余额提现(点击操作)
 */
public class CashAccountPageTest extends MainBase {
	
	@Test(description = "获取账户金额（包括余额，卡）", groups = { "qa" })
	public void test_01_cashAccountPage() throws SqlException, ParseException {
		Site site = HospitalChecker.getSiteByHospitalId(defHospitalId);
		List<NameValuePair> pairs = new ArrayList<>();
		pairs.add(new BasicNameValuePair("_p",""));
		pairs.add(new BasicNameValuePair("_site",site.getUrl()));
		pairs.add(new BasicNameValuePair("_siteType","mobile"));

		log.info("cookie..."+JSON.toJSONString(httpclient.getCookies()));
		HttpResult result = httpclient.get(Flag.MAIN, WithDraw_CashAccountPage,pairs);

		Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);
		String body = result.getBody();
		log.info("body..."+body);
		Examiner account = JSON.parseObject(JsonPath.read(body, "$.account").toString(), new TypeReference<Examiner>() {
		});

		String balanceAmount = JsonPath.read(body, "$.amounts.balanceAmount").toString();
		String cardBalance = JsonPath.read(body, "$.amounts.cardBalance").toString();
		String cardCount = JsonPath.read(body, "$.amounts.cardCount").toString();
		String couponCount = JsonPath.read(body, "$.amounts.couponCount").toString();

		if (checkdb) {
			ExaminerVo examinerVo = AccountChecker.getExaminerByCustomerId(defaccountId,defHospitalId);
//			Assert.assertEquals(account.getBirthYear().intValue(), examinerVo.getBirthYear());
//			Assert.assertEquals(account.getAge().intValue(), examinerVo.getAge());
			Assert.assertEquals(account.getCustomerId().intValue(), examinerVo.getCustomerId());
//			Assert.assertEquals(account.getGender(), examinerVo.getGender());
//			Assert.assertEquals(account.getIdCard(),examinerVo.getIdCard());
//			Assert.assertEquals(account.getIdType(), examinerVo.getIdType());
//			Assert.assertEquals(account.getMarriageStatus(), examinerVo.getMarriageStatus());
//			Assert.assertEquals(account.getMobile(), examinerVo.getMobile());
//			Assert.assertEquals(account.getName(), examinerVo.getName());
//			Assert.assertEquals(account.getStatus(),examinerVo.getStatus());
//			Assert.assertEquals(account.getType(), examinerVo.getType());

			String today = sdf.format(new Date())+" 00:00:00";
			String sql = "select * from tb_accounting where account_id=?";
			List<Map<String,Object>> list = DBMapper.query(sql, defaccountId);
			Assert.assertEquals(balanceAmount, list.get(0).get("balance").toString()); //个人账户余额
			String status = CardStatusEnum.BALANCE_RECOVERED.getCode() + "," + CardStatusEnum.USABLE.getCode();
			List<Card> directionCardLists = CardChecker.getCardByHospitalCanUse(defHospitalId+"", defaccountId, status);
			log.info("卡数量"+directionCardLists.size());
			Assert.assertEquals(Integer.parseInt(cardCount), directionCardLists.size()); //返回的卡券数量=可用卡数量

			//可用优惠券数量(时间已过期的可能定时任务还未标记为过期)
			List<UserCouponReceive> userCouponReceiveList= PayChecker.getUserCouponList("select * from tb_user_coupon_receive_record where account_id = "+defaccountId+"  and is_deleted = 0 and organization_id in ("+defHospitalId + ",-1) and status = 0");
			log.info("优惠券数量"+userCouponReceiveList.size());
			Assert.assertEquals(Integer.parseInt(couponCount),  userCouponReceiveList.size()); //返回的优惠券+红包数量

			int cardBalanceAmount = 0;
			for (Card c  : directionCardLists) {
				if(c.getBalance().intValue() == 0)
					cardBalanceAmount += 0;
				else if(!c.getCardSetting().isShowCardMealPrice())//隐价卡的金额不统计在内
					cardBalanceAmount += c.getBalance().intValue();
			}
			Assert.assertEquals(cardBalance, String.valueOf(cardBalanceAmount)); //所有未过期的卡金额
			
		}
	}
}
