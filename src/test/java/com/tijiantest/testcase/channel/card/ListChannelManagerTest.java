package com.tijiantest.testcase.channel.card;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.alibaba.fastjson.JSON;
import com.tijiantest.base.Flag;
import com.tijiantest.base.HttpResult;
import com.tijiantest.base.dbcheck.AccountChecker;
import com.tijiantest.model.account.Account;
import com.tijiantest.model.account.ManagerChannelRelDO;

public class ListChannelManagerTest extends EntityCardBase {

	@Test(groups = {"qa"},description="获取渠道所有客户经理")
	public void test_recoverBalance() {
		System.out.println("------------------------测试获取渠道所有客户经理Start-------------------------");
		Map<String,Object> map = new HashMap<>();
		map.put("channelId", defChannelid+"");
		
		HttpResult result = httpclient.get(Flag.CHANNEL, Card_ChannelListChannelManager, map);
		Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);
		
		List<Account> managerList = JSON.parseArray(result.getBody(), Account.class);
		if(checkdb) {
			List<Account> managers = new ArrayList<>();
			List<ManagerChannelRelDO> managerChannelRela = AccountChecker.getManagersByChannelId(defChannelid);
			for(ManagerChannelRelDO mcr : managerChannelRela) {
				Account a = AccountChecker.getAccountById(mcr.getManagerId());
				managers.add(a);
			}
			Assert.assertEquals(managerList.size(), managers.size());
			for(int i=0;i<managers.size(); i++) {
				Assert.assertEquals(managerList.get(i).getId(), managers.get(i).getId());
			}
		}
		System.out.println("------------------------测试获取渠道所有客户经理End-------------------------");
	}
	
}
