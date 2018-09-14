package com.tijiantest.testcase.channel.card;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.alibaba.fastjson.JSON;
import com.tijiantest.base.Flag;
import com.tijiantest.base.HttpResult;
import com.tijiantest.base.dbcheck.CardChecker;
import com.tijiantest.base.dbcheck.PayChecker;
import com.tijiantest.model.card.Card;
import com.tijiantest.model.card.CardOperateLog;
import com.tijiantest.model.card.CardOperateTypeEnum;
import com.tijiantest.model.card.CardRecordDto;
import com.tijiantest.model.card.CardRecordQueryDto;
import com.tijiantest.model.card.CardStatusEnum;
import com.tijiantest.model.card.ResultVO;
import com.tijiantest.model.payment.Accounting;
import com.tijiantest.util.db.SqlException;

public class RevokeCardTest extends EntityCardBase {
	
	@Test(groups = {"qa"},description="撤销实体卡测试")
	public void test_revokeCard() {
		System.out.println("------------------------测试渠道撤销实体卡Start-------------------------");
		CardRecordQueryDto dto = new CardRecordQueryDto();
		Integer fromHospital = defChannelid;
		dto.setFromHospital(fromHospital);
		dto.setManagerId(defChannelPlatMangerId);
		List<CardRecordDto> recordsDB = CardChecker.getCardRecordsByQuery(dto, true);
		List<Card> cards = recordsDB.stream().filter(r->r.getCard().getStatus().intValue()!=CardStatusEnum.CANCELLED.getCode())//卡未撤销
				.map(r->r.getCard())
				.limit(2)//获取前两个
				.collect(Collectors.toList());

		Accounting accounting = null;
		try {
			accounting = PayChecker.getAccouting(defChannelPlatMangerId.intValue());
		} catch (SqlException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Long recoBala = 0l;
		for(Card tmp : cards) {
			recoBala = recoBala + tmp.getBalance();
		}
		List<Integer> cardIds = cards.stream().map(r->r.getId()).collect(Collectors.toList());
		String json = JSON.toJSONString(cardIds);
		Date now = new Date();
		System.out.print(now);
		waitto(1);
		HttpResult result = httpclient.post(Flag.CHANNEL,Card_ChannelRevokeCard, json);
		Assert.assertEquals(result.getCode(),HttpStatus.SC_OK);
		ResultVO resultVo = JSON.parseObject(result.getBody(), ResultVO.class);
		Assert.assertEquals(resultVo.isSuccess(), true);
		Assert.assertEquals(resultVo.getResultCount().intValue(), cardIds.size());
		if(checkdb) {
			waitto(1);
			for(Card c : cards) {
				//tb_card
				Card card = CardChecker.getCardById(c.getId());
				Assert.assertEquals(card.getBalance().intValue(), 0);
				Assert.assertEquals(card.getStatus(), CardStatusEnum.CANCELLED.getCode());

				//tb_card_operate_log
				List<CardOperateLog> logs = CardChecker.getCardOperateLogsByCardIdAndCreateTime(c.getId(),now);
				Assert.assertEquals(logs.get(0).getOperateType(), CardOperateTypeEnum.REVOKE_CARD.getCode());
			}

			//tb_accounting
			Accounting accounting1 = null;
			try {
				accounting1 = PayChecker.getAccouting(defChannelPlatMangerId.intValue());
			} catch (SqlException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Assert.assertEquals(accounting1.getBalance().intValue(), accounting.getBalance()+recoBala);
		}
		System.out.println("------------------------测试渠道撤销实体卡End-------------------------");
	}
}
