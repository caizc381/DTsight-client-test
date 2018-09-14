package com.tijiantest.testcase.main.coupon;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.TypeReference;
import com.jayway.jsonpath.JsonPath;
import com.tijiantest.base.Flag;
import com.tijiantest.base.HttpResult;
import com.tijiantest.base.dbcheck.CardChecker;
import com.tijiantest.base.dbcheck.HospitalChecker;
import com.tijiantest.base.dbcheck.PayChecker;
import com.tijiantest.model.card.Card;
import com.tijiantest.model.card.CardStatusEnum;
import com.tijiantest.model.coupon.UserCouponReceive;
import com.tijiantest.model.coupon.UserCouponVOs;
import com.tijiantest.model.coupon.UserReceiveStatistics;
import com.tijiantest.model.hospital.Hospital;
import com.tijiantest.testcase.main.MainBase;
import com.tijiantest.util.CvsFileUtil;
import com.tijiantest.util.db.SqlException;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.*;

/**
 * 位置:医院二级站点 -> 个人中心 ->优惠券
 * 优惠券项目上线后页面改造(接口改造)
 *
 * @author huifang
 */
public class CardAndCouponTest extends MainBase {

	@Test(description = "医院个人中心 - 优惠券列表", groups = { "qa" })
	public void test_01_cardAndCoupon() throws SqlException, ParseException {
		//STEP1:准备入参&格式化参数
		int hospitalId =  defHospitalId;
		String site = HospitalChecker.getSiteByOrganizationId(hospitalId);
		List<NameValuePair> pairs = new ArrayList<>();
		pairs.add(new BasicNameValuePair("hospitalId", hospitalId+""));
		pairs.add(new BasicNameValuePair("_p", ""));
		pairs.add(new BasicNameValuePair("_site", site));
		pairs.add(new BasicNameValuePair("_siteType", "mobile"));

		//STEP2：调用接口
		HttpResult  result = httpclient.get(Flag.MAIN, Coupon_CardAndCoupon, pairs);

		Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);
		String body = result.getBody();
//		log.info("body..."+body);
		List<Card> cards = JSON.parseObject(JsonPath.read(body, "$.cards").toString(), new TypeReference<List<Card>>() {
		});
		String userC = JsonPath.read(body, "$.userCouponVOs[*]").toString();
		List<UserCouponVOs> userCoupoVos = JSON.parseArray(userC, UserCouponVOs.class);

		int cardNum = JsonPath.read(body,"$.cardNum");
		int couponNum = JsonPath.read(body,"$.couponNum");

		//卡按照ID排序
		Collections.sort(cards, new Comparator<Card>() {
			@Override
			public int compare(Card o1, Card o2) {
				return o1.getId() - o2.getId();
			}
		});
		//优惠券+红包按照ID排序
		Collections.sort(userCoupoVos, new Comparator<UserCouponVOs>() {
			@Override
			public int compare(UserCouponVOs o1, UserCouponVOs o2) {
				return o1.getCouponId() - o2.getCouponId();
			}
		});

		//验证DB
		if (checkdb) {
			String status = CardStatusEnum.BALANCE_RECOVERED.getCode() + "," + CardStatusEnum.USABLE.getCode();
				// 1.对应体检中心的体检卡
				List<Card> directionCardLists=CardChecker.getCardByHospital(hospitalId+"", defaccountId, status, cards);
				Assert.assertEquals(cards.size(), directionCardLists.size());
				if (!cards.isEmpty()) {
					for (int i = 0; i < cards.size(); i++) {
						Card card = cards.get(i);
						Assert.assertEquals(card.getCardName(), directionCardLists.get(i).getCardName());
						int isShowCardMealPrice = card.getCardSetting().isShowCardMealPrice()== true?1:0;
						if (isShowCardMealPrice == 1) {
							Assert.assertEquals(card.getBalance(), Long.valueOf(0));
						} else {
							Assert.assertEquals(card.getBalance(), directionCardLists.get(i).getBalance());
						}
						Assert.assertEquals(card.getCardNum(), directionCardLists.get(i).getCardNum());
					}
				}
				//2.对应体检中心的优惠券数量(医院优惠券+红包)
			 List<UserCouponReceive> userCouponList = PayChecker.getUserCouponList("select *  from tb_user_coupon_receive_record where account_id ="+defaccountId+" and is_deleted = 0 and organization_id in ( "+hospitalId+",-1)");
			 Assert.assertEquals(userCoupoVos.size(),userCouponList.size());
			 for(int i=0;i<userCouponList.size();i++){
			 	Assert.assertEquals(userCoupoVos.get(i).getCouponTemplateResult().getBatchNum(),userCouponList.get(i).getTemplateBatchNum());
			 	Assert.assertEquals(userCoupoVos.get(i).getCouponId().intValue(),userCouponList.get(i).getId());
			 	Assert.assertEquals(userCoupoVos.get(i).getMobile(),userCouponList.get(i).getMobile());
			 	Assert.assertEquals(userCoupoVos.get(i).getAccountId(),userCouponList.get(i).getAccountId());
			 	Assert.assertEquals(userCoupoVos.get(i).getStatus(),userCouponList.get(i).getStatus().intValue());
			 	Assert.assertEquals(userCoupoVos.get(i).getCouponTemplateResult().getOrganizationId(),userCouponList.get(i).getOrganizationId());
			 	Assert.assertEquals(sdf.format(userCoupoVos.get(i).getCouponTemplateResult().getEndTime()),sdf.format(userCouponList.get(i).getEndTime()));
			 }

			 //3.验证卡的数量
			Assert.assertEquals(cardNum,directionCardLists.size());

			//4.验证优惠券数量
			Assert.assertEquals(couponNum,userCouponList.size());

		}


		}


}
