package com.tijiantest.testcase.crm.card;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import org.apache.http.HttpStatus;
import org.apache.http.ParseException;
import org.testng.Assert;
import org.testng.annotations.Test;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.tijiantest.base.HttpResult;
import com.tijiantest.model.card.CardExamNote;
import com.tijiantest.testcase.crm.CrmBase;
import com.tijiantest.util.db.DBMapper;
import com.tijiantest.util.db.SqlException;

/**
 * 根据用户id查询体检须知
 * 
 * @author huifang
 *
 */
public class ExamNotesTest extends CrmBase {
	static List<CardExamNote> list = new ArrayList<>();
	@Test(description = "查询体检须知", groups = { "qa", "crm_examNotes" })
	public void test_01_examNotes_success() throws ParseException, IOException, SqlException {
		// get
		HttpResult response = httpclient.get(Card_ExamNotes);

		// assert
		Assert.assertEquals(response.getCode(), HttpStatus.SC_OK);
		String body = response.getBody();

		if (checkdb) {
			// sort responseList
			list = JSON.parseObject(body, new TypeReference<List<CardExamNote>>() {
			});
			Collections.sort(list, new Comparator<CardExamNote>() {
				@Override
				public int compare(CardExamNote o1, CardExamNote o2) {
					return o1.getId() - o2.getId();
				}
			});

			String sql = "SELECT * FROM tb_card_exam_note WHERE account_id = ?";

			List<Map<String, Object>> relist = DBMapper.query(sql, defaccountId);

			Assert.assertEquals(list.size(), relist.size());
			for (int i = 0; i < list.size(); i++) {
				Assert.assertEquals(list.get(i).getId(), relist.get(i).get("id"));
				Assert.assertEquals(list.get(i).getAccountId(), relist.get(i).get("account_id"));
				Assert.assertEquals(list.get(i).getAuthor(), relist.get(i).get("author"));
				Assert.assertEquals(list.get(i).getNote(), relist.get(i).get("note"));
				Assert.assertEquals(list.get(i).getNoteName(), relist.get(i).get("note_name"));
				Assert.assertEquals(list.get(i).getNewCompanyId(), relist.get(i).get("new_company_id"));
				Assert.assertEquals(list.get(i).getOrganizationType(), relist.get(i).get("organization_type"));
			}
		}
	}

}
