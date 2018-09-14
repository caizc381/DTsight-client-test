package com.tijiantest.testcase.crm.card;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.tijiantest.base.HttpResult;
import com.tijiantest.model.card.CardPreviewVo;
import com.tijiantest.model.card.CardRecordDto;
import com.tijiantest.model.card.CardStatusEnum;
import com.tijiantest.model.organization.OrganizationTypeEnum;
import com.tijiantest.testcase.crm.CrmBase;
import com.tijiantest.util.db.DBMapper;
import com.tijiantest.util.db.SqlException;

public class GetCardPreviewListTest extends CrmBase {

	@BeforeTest(description = "设置可以打印预检凭证", groups = { "qa" })
	public void doBefore() throws SqlException {
		String sql = "update tb_hospital_settings set can_print_checklist=1 where hospital_id=?";
		DBMapper.update(sql, defhospital.getId());
		log.info("设置打印预检凭证 为1 ");
	}

	@Test(description = "打印预检凭证", groups = { "qa" }, dependsOnGroups = "crm_allRecords")
	public void test_01_getCardPreviewList() throws SqlException {
		// 先获取Card列表
		List<CardRecordDto> dto = CardRecordsTest.dto;
		
		String cardIds = "";
		if (dto.size() == 0) {
			log.info("没有发卡记录！！！");
			return;
		}

		// 取一张卡
		for (int i = 0; i < dto.size(); i++) {
			CardRecordDto cardRecordDto = dto.get(i);
			if (cardRecordDto.getCard().getStatus() != CardStatusEnum.CANCELLED.getCode()) {
				cardIds += cardRecordDto.getCard().getId() + "";
				break;
			}
		}

		if (cardIds.equals("")) {
			log.info("没有未使用，已使用，金额回收的体检卡！！！");
			return;
		}

		List<NameValuePair> pairs = new ArrayList<>();
		pairs.add(new BasicNameValuePair("cardIds", cardIds));
		pairs.add(new BasicNameValuePair("hospitalId", defhospital.getId() + ""));

		HttpResult result = httpclient.get(Card_GetCardPreviewList, pairs);
		String body = result.getBody();
		Assert.assertEquals(result.getCode(), HttpStatus.SC_OK,"错误提示："+body);

		List<CardPreviewVo> cardPreviewVos = JSON.parseObject(body, new TypeReference<List<CardPreviewVo>>() {
		});

		CardPreviewVo cardPreviewVo = cardPreviewVos.get(0);

		if (checkdb) {
			String cardSql = "select * from tb_card where id=?";
			List<Map<String, Object>> cardList = DBMapper.query(cardSql, cardIds);
			String accountId = cardList.get(0).get("account_id") + "";
			String managerId = cardList.get(0).get("manager_id") + "";
			String newCompanyId = cardList.get(0).get("new_company_id") + "";
			String customerId = cardList.get(0).get("account_id") + "";
			String organizationType = cardList.get(0).get("organization_type") + "";
			String organizationId = cardList.get(0).get("from_hospital") + "";

			String accountSql = "select acr.customer_id, acr.name , acr.birthYear, acr.gender, acr.marriagestatus, acr.mobile, acr.initial_mobile as initialMobile, acr.id_card, acr.igroup,"
					+ " acr.position, acr.department, acr.is_retire, acr.employee_id, acr.add_account_type"
					+ " from tb_examiner acr "
					+ " WHERE acr.manager_id = ? AND acr.new_company_id = ? AND acr.organization_type =? AND acr.customer_id=?";
			List<Map<String, Object>> accountList = DBMapper.query(accountSql, managerId, newCompanyId,
					OrganizationTypeEnum.HOSPITAL.getCode(), customerId);

			Assert.assertEquals(cardPreviewVo.getIdCard(), accountList.get(0).get("id_card"));
			Assert.assertEquals(cardPreviewVo.getName(), accountList.get(0).get("name"));
			Assert.assertEquals(cardPreviewVo.getGender(), accountList.get(0).get("gender"));
			Assert.assertEquals(cardPreviewVo.getMobile(), accountList.get(0).get("mobile"));

			List<Map<String, Object>> companyList = new ArrayList<>();
			String companySql = "";
			if (organizationType.equals(OrganizationTypeEnum.HOSPITAL.getCode() + "")) {
				companySql = "select id, gmt_created, gmt_modified, name, platform_company_id, organization_id, organization_name, discount, show_report, settlement_mode, his_name, advance_export_order,"
						+ " send_exam_sms, send_exam_sms_days, pinyin, is_deleted, tb_exam_company_id, examination_address, examreport_interval_time"
						+ " from tb_hospital_company where id = ?";

			} else if (organizationType.equals(OrganizationTypeEnum.CHANNEL.getCode() + "")) {
				companySql = "select id,gmt_created, gmt_modified, name, platform_company_id, organization_id, organization_name, discount, settlement_mode, send_exam_sms, send_exam_sms_days,"
						+ " pinyin, is_deleted, tb_exam_company_id"
						+ " from tb_channel_company where id = ?  and is_deleted = 0";
			}
			companyList = DBMapper.query(companySql, newCompanyId);
			if (companyList.get(0).get("examination_address") == null) {
				// 取体检中心的地址
				String hospitalSql = "select * from tb_hospital where id=?";
				List<Map<String, Object>> hospitalList = DBMapper.query(hospitalSql, organizationId);
				Assert.assertTrue(
						cardPreviewVo.getExamAddress().contains(hospitalList.get(0).get("address").toString()));
			} else {
				Assert.assertTrue(cardPreviewVo.getExamAddress()
						.contains(companyList.get(0).get("examination_address").toString()));
			}

			Assert.assertEquals(cardPreviewVo.getCompanyName(), companyList.get(0).get("name"));
			// examAddress
			// examNote
			String examNoteSql = "SELECT b.exam_note as note FROM tb_card a  LEFT JOIN tb_card_batch b on a.batch_id = b.id WHERE a.id = ?";
			List<Map<String, Object>> examNoteList = DBMapper.query(examNoteSql, cardIds);
			String examNote;
			if (examNoteList.get(0).get("note") == null) {
				// 获取医院的体检须知
				String hospitalSql = "select * from tb_hospital where id=?";
				List<Map<String, Object>> hospitalList = DBMapper.query(hospitalSql, organizationId);
				examNote = hospitalList.get(0).get("exam_notice").toString();
			} else {
				examNote = examNoteList.get(0).get("note").toString();
			}

			Assert.assertEquals(cardPreviewVo.getExamNote(), examNote);

			// loginName
			String userSql = "select * from tb_user where account_id=?";
			List<Map<String, Object>> userList = DBMapper.query(userSql, accountId);
			Assert.assertEquals(cardPreviewVo.getLoginName(), userList.get(0).get("username"));
			Assert.assertEquals(cardPreviewVo.getPassword(), "111111");

		}
	}
}
