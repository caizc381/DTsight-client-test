package com.tijiantest.testcase.ops.withdraw;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.jayway.jsonpath.JsonPath;
import com.tijiantest.base.Flag;
import com.tijiantest.base.HttpResult;
import com.tijiantest.model.account.Account;
import com.tijiantest.model.payment.SearchDto;
import com.tijiantest.model.payment.withdraw.SaveWithdraw;
import com.tijiantest.model.payment.withdraw.WithdrawAuditException;
import com.tijiantest.testcase.ops.OpsBase;
import com.tijiantest.util.CvsFileUtil;
import com.tijiantest.util.db.DBMapper;
import com.tijiantest.util.db.SqlException;

public class FirstTrialListTest extends OpsBase {
	// 点击客服审核
	private final Integer CUSTOMER_AUDIT = 1;

	// 点击财务审核
	private final Integer FINANCE_AUDIT = 2;

	//身份证|手机号搜索会报错，这个接口
	//case06,0,320382198602068320,,,,,,按照身份证搜索
	//case07,0,,15958312133,,,,,按照手机号搜索
	@Test(description = "提现管理列表", groups = { "qa", "manage_firstTrialList" }, dataProvider = "firstTrialList")
	public void test_01_firstTrialList(String... args)
			throws SqlException, NumberFormatException, WithdrawAuditException, ParseException {
		String auditType = args[1];
		String idCard = args[2].isEmpty() ? null : args[2];
		String mobile = args[3].isEmpty() ? null : args[3];
		String receivable = args[4].isEmpty() ? null : args[4];
		String serialNumber = args[5].isEmpty() ? null : args[5];
		String stateString = args[6];
		List<Integer> stateList = new ArrayList<>();
		String[] stateArray = new String[] {};
		if (!stateString.equals("")) {
			stateArray = stateString.split("#");
			for (String string : stateArray) {
				stateList.add(Integer.valueOf(string));
			}
		}

		String username = args[7].isEmpty() ? null : args[7];

		SearchDto searchDto = new SearchDto(username, idCard, mobile, null, serialNumber, receivable, stateList,
				Integer.valueOf(auditType));
		String json = JSON.toJSONString(searchDto);

		HttpResult result = httpclient.post(Flag.OPS, Withdrawaudit_FirstTrialList, json);
		String body = result.getBody();
		log.info(body);
		Assert.assertEquals(result.getCode(), HttpStatus.SC_OK,"报错");

		List<SaveWithdraw> saveWithdrawList = JSON.parseObject(JsonPath.read(body, "$.records").toString(),
				new TypeReference<List<SaveWithdraw>>() {
				});
		List<Map<String, Object>> list = new ArrayList<>();
		if (checkdb) {
			if (FINANCE_AUDIT.equals(searchDto.getAuditType()) || CUSTOMER_AUDIT.equals(searchDto.getAuditType())) {
				String sql = "SELECT sw.id, sw.receivable, sw.channel_name, sw.real_name, sw.amount, sw.state, sw.account_id, sw.serial_number, sw.withdraw_type, sw.create_date, sw.update_date, sw.operator, sw.remark, ai.balance as balance, ac.name as 'account_name', ac.mobile as 'account_mobile',ac.status AS 'account_status' FROM tb_withdraw sw left join tb_account ac on  sw.account_id=ac.id left join tb_accounting ai on sw.account_id = ai.account_id WHERE sw.state in (?) ORDER BY sw.id desc";
				// 验证条数
				if (!stateString.contains("#")) {
					list = DBMapper.query(sql, stateString);

					Assert.assertEquals(saveWithdrawList.size(), list.size());
					for (int i = 0; i < saveWithdrawList.size(); i++) {
						Assert.assertEquals(saveWithdrawList.get(i).getAccountId(), list.get(i).get("account_id"));
						Assert.assertEquals(saveWithdrawList.get(i).getAmount(), list.get(i).get("amount"));
						Assert.assertEquals(saveWithdrawList.get(i).getBalance(), list.get(i).get("balance"));
						Assert.assertEquals(saveWithdrawList.get(i).getChannelName(), list.get(i).get("channel_name"));
						Assert.assertEquals(saveWithdrawList.get(i).getRealName(), list.get(i).get("real_name"));
						Assert.assertEquals(saveWithdrawList.get(i).getState(), list.get(i).get("state"));
						Assert.assertEquals(saveWithdrawList.get(i).getWithdrawType(),
								list.get(i).get("withdraw_type"));
					}
				} else {
					for (String state : stateArray) {
						List<Map<String, Object>> list2 = DBMapper.query(sql, state);
						list.addAll(list2);
					}
					List<SaveWithdraw> saveWithdraws = new ArrayList<>();
					for (Map<String, Object> map : list) {
						Account account = new Account(map.get("real_name").toString(),
								map.get("account_mobile").toString(),
								Integer.valueOf(map.get("account_status").toString()));
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						SaveWithdraw saveWithdraw = new SaveWithdraw(map.get("real_name").toString(),
								map.get("amount").toString(), map.get("channel_name").toString(),
								map.get("receivable") == null ? null : map.get("receivable").toString(),
								Integer.valueOf(map.get("account_id").toString()),
								Integer.valueOf(map.get("state").toString()),
								Integer.valueOf(map.get("withdraw_type").toString()),
								sdf.parse(map.get("create_date").toString()), Integer.valueOf(map.get("id").toString()),
								Integer.valueOf(map.get("balance").toString()), account);
						saveWithdraws.add(saveWithdraw);
					}
					// 先按照ID进行排序
					Comparator<SaveWithdraw> comparator = new Comparator<SaveWithdraw>() {
						public int compare(SaveWithdraw s1, SaveWithdraw s2) {
							// 先排年龄
							if (s1.getId() != s2.getId()) {
								return s2.getId() - s1.getId();
							}
							return 0;
						}
					};
					// 这里就会自动根据规则进行排序
					Collections.sort(saveWithdraws, comparator);

					Assert.assertEquals(saveWithdrawList.size(), saveWithdraws.size());
					for (int i = 0; i < saveWithdrawList.size(); i++) {
						Assert.assertEquals(saveWithdrawList.get(i).getAccountId(),
								saveWithdraws.get(i).getAccountId());
						Assert.assertEquals(saveWithdrawList.get(i).getAmount(), saveWithdraws.get(i).getAmount());
						Assert.assertEquals(saveWithdrawList.get(i).getBalance(), saveWithdraws.get(i).getBalance());
						Assert.assertEquals(saveWithdrawList.get(i).getChannelName(),
								saveWithdraws.get(i).getChannelName());
						Assert.assertEquals(saveWithdrawList.get(i).getRealName(), saveWithdraws.get(i).getRealName());
						Assert.assertEquals(saveWithdrawList.get(i).getState(), saveWithdraws.get(i).getState());
						Assert.assertEquals(saveWithdrawList.get(i).getWithdrawType(),
								saveWithdraws.get(i).getWithdrawType());
					}
				}
			} else {
				// 即搜索
				String baseSql = "SELECT sw.id, sw.receivable, sw.channel_name, sw.real_name, sw.amount, sw.state, sw.account_id, sw.serial_number, sw.withdraw_type, sw.create_date, sw.update_date, sw.operator, sw.remark, ai.balance as balance, ac.name as 'account_name', ac.mobile as 'account_mobile' FROM tb_withdraw  sw left join tb_account ac on sw.account_id=ac.id left join tb_accounting ai on sw.account_id = ai.account_id WHERE 1=1";
				String sql = "";
				if (stateString != null && !stateString.equals("")) {
					String status = "";
					for (String string : stateArray) {
						status += string + ",";
					}
					int lastIndex = status.lastIndexOf(",");
					status = status.substring(0, lastIndex);
					sql = " AND	sw.state in ( " + status + ")";
					list = DBMapper.query(baseSql + sql + " ORDER BY sw.id desc");
				} else if (username != null && !username.equals("")) {
					sql = " and ac.name=?";
					list = DBMapper.query(baseSql + sql + " ORDER BY sw.id desc", username);
				} else if (idCard != null && !idCard.equals("")) {
					sql = " and ac.idCard=?";
					list = DBMapper.query(baseSql + sql + " ORDER BY sw.id desc", idCard);
				} else if (mobile != null && !mobile.equals("")) {
					sql = " AND ac.mobile=?";
					list = DBMapper.query(baseSql + sql + " ORDER BY sw.id desc", mobile);
				} else if (receivable != null && !receivable.equals("")) {
					sql = " AND sw.receivable=?";
					list = DBMapper.query(baseSql + sql + " ORDER BY sw.id desc", receivable);
				} else if (serialNumber != null && !serialNumber.equals("")) {
					sql = " AND sw.serial_number=?";
					list = DBMapper.query(baseSql + sql + " ORDER BY sw.id desc", serialNumber);
				}
				Assert.assertEquals(saveWithdrawList.size(), list.size());
			}
		}
	}

	@DataProvider(name = "firstTrialList")
	public Iterator<String[]> firstTrialList() {
		try {
			return CvsFileUtil.fromCvsFileToIterator("./csv/payment/firstTrialList.csv", 10);
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
