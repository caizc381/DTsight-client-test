package com.tijiantest.testcase.ops;

import java.util.List;

import org.apache.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.alibaba.fastjson.JSON;
import com.tijiantest.base.Flag;
import com.tijiantest.base.HttpResult;
import com.tijiantest.base.dbcheck.HospitalChecker;
import com.tijiantest.model.hospital.Hospital;
import com.tijiantest.model.organization.OrganizationTypeEnum;

public class ChannelListTest extends OpsBase{
	public static List<Hospital> orgList;
	
  @Test(description="获取渠道列表",groups={"qa","ops_getChannelList"})
  public void test_getChannelList() {
	  System.out.println("----------------------获取渠道商列表Start---------------------");
	  HttpResult result = httpclient.get(Flag.OPS,OPS_ChannelList);
	  Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);
	  List<Hospital> channels= JSON.parseArray(result.getBody(),Hospital.class);
	  if(checkdb){
		  orgList = HospitalChecker.getOrganizations(OrganizationTypeEnum.CHANNEL.getCode());
		  Assert.assertEquals(orgList.size(), channels.size());
		  for (int i=0;i<channels.size();i++) {
			  Assert.assertEquals(orgList.get(i).getId(), channels.get(i).getId());
			  Assert.assertEquals(orgList.get(i).getName(), channels.get(i).getName());
		  }
	  }
	  System.out.println("----------------------获取渠道商列表End---------------------");
  }
}
