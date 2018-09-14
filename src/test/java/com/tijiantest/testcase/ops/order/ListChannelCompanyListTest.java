package com.tijiantest.testcase.ops.order;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.alibaba.fastjson.JSON;
import com.tijiantest.base.Flag;
import com.tijiantest.base.HttpResult;
import com.tijiantest.model.company.ChannelCompany;
import com.tijiantest.model.hospital.Hospital;
import com.tijiantest.testcase.ops.OpsBase;
import com.tijiantest.util.db.DBMapper;
import com.tijiantest.util.db.SqlException;

/**
 * 根据渠道ID，获取渠道单位
 * 
 * @author admin
 *
 */
public class ListChannelCompanyListTest extends OpsBase {

	@Test(description = "根据渠道ID，获取渠道单位", groups = { "qa" }, dependsOnGroups = { "orderOrganizationList" })
	public void test_listChannelCompanyList() throws SqlException {
		List<Hospital> channels = OrderOrganizationListTest.channels;
		if (channels.size() == 0) {
			log.error("没有渠道商！！！");
			return;
		}

		// 随机获取一个渠道商
		Random random = new Random();
		int index = random.nextInt(channels.size()) % (channels.size() + 1);
		int channelId = channels.get(index).getId();
		System.out.println("渠道商ID ： " + channelId);

		List<NameValuePair> pairs = new ArrayList<>();
		pairs.add(new BasicNameValuePair("channelId", channelId + ""));

		HttpResult result = httpclient.get(Flag.OPS, OpsOrder_ListChannelCompanyList, pairs);
		String body = result.getBody();
		System.out.println(body);
		Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);

		List<ChannelCompany> channelCompanies = JSON.parseArray(body, ChannelCompany.class);

		if (checkdb) {
			String sql = "select id, gmt_created,gmt_modified,name,platform_company_id,organization_id,organization_name,discount,"
					+ " settlement_mode,send_exam_sms,send_exam_sms_days,pinyin,is_deleted,tb_exam_company_id"
					+ " from tb_channel_company where organization_id=? and is_deleted=0";
			List<Map<String, Object>> list = DBMapper.query(sql, channelId);
			Assert.assertEquals(channelCompanies.size(), list.size());

			for (int i = 0; i < channelCompanies.size(); i++) {
				ChannelCompany channelCompany = channelCompanies.get(i);
				Map<String, Object> map = list.get(i);
				Assert.assertEquals(channelCompany.getId(), map.get("id"));
				Assert.assertEquals(channelCompany.getName(), map.get("name"));
				Assert.assertEquals(channelCompany.getOrganizationId(), map.get("organization_id"));
				Assert.assertEquals(channelCompany.getOrganizationName(), map.get("organization_name"));
				Assert.assertEquals(channelCompany.getPinyin(), map.get("pinyin"));
			}
		}

	}
}
