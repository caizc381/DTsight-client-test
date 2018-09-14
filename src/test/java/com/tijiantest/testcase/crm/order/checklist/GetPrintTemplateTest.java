package com.tijiantest.testcase.crm.order.checklist;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.alibaba.fastjson.JSON;
import com.tijiantest.base.HttpResult;
import com.tijiantest.model.order.checklist.ChecklistPrintTemplate;
import com.tijiantest.util.db.DBMapper;
import com.tijiantest.util.db.SqlException;

public class GetPrintTemplateTest extends ChecklistBaseTest {

	@Test(description = "获取备注内容模板", groups = { "qa" })
	public void test_01_getPrintTemplate() throws SqlException {
		int hospitalId = defhospital.getId();
		List<NameValuePair> pairs = new ArrayList<>();
		pairs.add(new BasicNameValuePair("hospitalId", hospitalId + ""));
		HttpResult result = httpclient.get(Order_GetPrintTemplate, pairs);
		System.out.println(result.getBody());
		String body = result.getBody();
		Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);
		if (body.contains("")) {
			log.info("请先在数据库中，添加备注信息！！！");
			return;
		}
		ChecklistPrintTemplate checklistPrintTemplate = JSON.parseObject(body, ChecklistPrintTemplate.class);

		if (checkdb) {
			String sql = "SELECT  pt.id, pt.hospital_id, pt.barcode_print_note, pt.pdf_print_note FROM tb_checklist_print_template pt WHERE pt.hospital_id = ?";
			List<Map<String, Object>> list = DBMapper.query(sql, hospitalId);

			Assert.assertEquals(checklistPrintTemplate.getBarcodePrintNote(), list.get(0).get("barcode_print_note"));
			Assert.assertEquals(checklistPrintTemplate.getPdfPrintNote(), list.get(0).get("pdf_print_note"));
		}
	}
}
