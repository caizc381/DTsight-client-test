package com.tijiantest.testcase.crm.card;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.alibaba.fastjson.JSON;
import com.jayway.jsonpath.JsonPath;
import com.tijiantest.base.HttpResult;
import com.tijiantest.base.dbcheck.HospitalChecker;
import com.tijiantest.model.card.CardExamNote;
import com.tijiantest.testcase.crm.CrmBase;
import com.tijiantest.util.CvsFileUtil;
import com.tijiantest.util.db.DBMapper;
import com.tijiantest.util.db.SqlException;

/**
 * 新增/更新/删除体检须知
 * 
 * @author huifang
 *
 */
public class ExamNoteTest extends CrmBase {

	private final String sql = "SELECT * FROM tb_card_exam_note WHERE account_id = ? and id = ?";
	private int examNoteId = 0;

	@AfterClass(description = "删除体检须知", alwaysRun = true)
	public void afterTest() throws SqlException {
		if (examNoteId != 0) {
			log.info("examNoteId：" + examNoteId);
			// DELETE
			HttpResult response = httpclient.get(Card_ExamnoteById, examNoteId + "");
			Assert.assertEquals(response.getCode(), HttpStatus.SC_OK);
			Assert.assertEquals(response.getBody(), "{}");

			if (checkdb) {
				List<Map<String, Object>> list = DBMapper.query(sql, defaccountId, examNoteId);
				Assert.assertTrue(list.isEmpty());
			}
		}
	}

	@Test(dataProvider = "examNote_success", groups = { "qa" }, description = "新建体检须知")
	public void test_01_examNote_success(String... args) throws SqlException {
		String notename = args[1];
		String note = args[2];
		String author = args[3];

		CardExamNote cnote = new CardExamNote();
		cnote.setNote(note);
		cnote.setNoteName(notename);
		cnote.setAuthor(author);
		int companyId = defnewcompany.getId();
		cnote.setCompanyId(companyId);
		cnote.setNewCompanyId(companyId);
		int organizationType = HospitalChecker.getOrganizationType(defhospital.getId());
		cnote.setOrganizationType(organizationType);

		// INSERT
		HttpResult response = httpclient.post(Card_Examnote, JSON.toJSONString(cnote));
		Assert.assertEquals(response.getCode(), HttpStatus.SC_OK);

		String body = response.getBody();
		Assert.assertNotNull(body);

		examNoteId = JsonPath.read(body, "$.result");

		// check database
		if (checkdb) {
			List<Map<String, Object>> list = DBMapper.query(sql, defaccountId, examNoteId);
			Assert.assertEquals(list.size(), 1);
			Assert.assertEquals(note, list.get(0).get("note").toString());
			Assert.assertEquals(notename, list.get(0).get("note_name").toString());
			Assert.assertEquals(companyId, list.get(0).get("new_company_id"));
			Assert.assertEquals(organizationType, list.get(0).get("organization_type"));
		}

	}

	@Test(description = "更新体检须知", groups = { "qa" }, dependsOnGroups = {
			"crm_examNotes" }, dataProvider = "examNote_success")
	public void test_02_examNote_edit(String... args) throws SqlException {
		List<CardExamNote> examNotesList = ExamNotesTest.list;

		// 随机取一个
		Random random = new Random();
		int index = random.nextInt(examNotesList.size()) % (examNotesList.size() + 1);
		CardExamNote cardExamNote = examNotesList.get(index);

		String notename = args[1];
		String note = args[2];
		String updatenote = note + "FORTEST";

		cardExamNote.setNote(updatenote);
		cardExamNote.setNoteName(notename);

		HttpResult result = httpclient.post(Card_Examnote, JSON.toJSONString(cardExamNote));
		String body = result.getBody();
		Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);
		Assert.assertTrue(body.contains(cardExamNote.getId() + ""));

		if (checkdb) {
			if (checkdb) {
				List<Map<String, Object>> list = DBMapper.query(sql, defaccountId, cardExamNote.getId());
				Assert.assertEquals(list.size(), 1);
				Assert.assertEquals(updatenote, list.get(0).get("note").toString());
				Assert.assertEquals(notename, list.get(0).get("note_name").toString());
				Assert.assertEquals(cardExamNote.getNewCompanyId(), list.get(0).get("new_company_id"));
				Assert.assertEquals(cardExamNote.getOrganizationType(), list.get(0).get("organization_type"));
			}
		}
	}

	@DataProvider
	public Iterator<String[]> examNote_success() {
		try {
			return CvsFileUtil.fromCvsFileToIterator("./csv/card/examnote_success.csv", 6);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}
}
