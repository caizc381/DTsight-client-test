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
import com.tijiantest.model.card.Card;
import com.tijiantest.model.card.CardOperateLog;
import com.tijiantest.model.card.CardOperateTypeEnum;
import com.tijiantest.model.card.CardRecordDto;
import com.tijiantest.model.card.CardRecordQueryDto;
import com.tijiantest.model.card.ResultVO;
import com.tijiantest.model.card.UpdateCardExpireVO;
import com.tijiantest.util.DateUtils;

public class UpdateCardExpireDateTest extends EntityCardBase {

	//@Test(groups = {"qa"},description="更改有效期")
	public void test_updateCardExpireDate() {
		System.out.println("------------------------测试渠道更改实体卡有效期Start-------------------------");
		CardRecordQueryDto dto = new CardRecordQueryDto();
		Integer fromHospital = defChannelid;
		dto.setFromHospital(fromHospital);
		List<CardRecordDto> recordsDB = CardChecker.getCardRecordsByQuery(dto, true);
		List<Integer> cardIds = recordsDB.stream().filter(r->r.getCard().getStatus().intValue()==1)//获取状态为可用的卡（未使用/已使用）
				.limit(2)//获取前两个
				.map(m->m.getCard().getId())//获取cardId
				.collect(Collectors.toList());
		//已撤销卡
		Card cardCanceled = recordsDB.stream().filter(r->r.getCard().getStatus().intValue()==2).collect(Collectors.toList()).get(0).getCard();
		cardIds.add(cardCanceled.getId());
//		Date newDate = DateUtils.offDate(30);
		Date newDate = DateUtils.offsetDestDay(new Date(), 30);
		UpdateCardExpireVO vo = new UpdateCardExpireVO();
		vo.setCardIds(cardIds);
		vo.setNewDate(newDate);
		System.out.println("newDate:"+vo.getNewDate());
		
		String json = JSON.toJSONString(vo);
		Date now = new Date();
		HttpResult result = httpclient.post(Flag.CHANNEL, Card_UpdateCardExpireDate, json);
		System.out.println("body:"+result.getBody());
		Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);
		ResultVO resultVo = JSON.parseObject(result.getBody(), ResultVO.class);
		Assert.assertEquals(resultVo.isSuccess(), true);
		Assert.assertEquals(resultVo.getResultCount().intValue(), 2);
		
		if(checkdb) {
			List<Card> cards = CardChecker.getCardsByIds(cardIds);
			for(Card c : cards) {
				if(c.getId().intValue()!=cardCanceled.getId().intValue()) {					
//					Assert.assertEquals(vo.getNewDate().toString(), c.getExpiredDate().toString());
					List<CardOperateLog> logs = CardChecker.getCardOperateLogsByCardIdAndCreateTime(c.getId(),now);
					Assert.assertEquals(logs.get(0).getOperateType(), CardOperateTypeEnum.UPDATE_EXPIRED_DATE.getCode());
				}
				else
				{
					
				}
			}
		}
		System.out.println("------------------------测试渠道更改实体卡有效期End-------------------------");
	}
}
