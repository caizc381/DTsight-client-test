package com.tijiantest.testcase.crm.order.checklist;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.alibaba.fastjson.JSON;
import com.jayway.jsonpath.JsonPath;
import com.tijiantest.base.HttpResult;
import com.tijiantest.base.dbcheck.OrderChecker;
import com.tijiantest.model.account.FileAccountImportInfo;
import com.tijiantest.model.account.ModifyAccountType;
import com.tijiantest.model.order.FastBookVo;
import com.tijiantest.model.order.checklist.FastbookChecklistVO;
import com.tijiantest.testcase.crm.order.FastBookTest;
import com.tijiantest.testcase.crm.order.checklist.ChecklistBaseTest;
import com.tijiantest.util.CvsFileUtil;
import com.tijiantest.util.DateUtils;
import com.tijiantest.util.db.DBMapper;
import com.tijiantest.util.db.MongoDBUtils;
import com.tijiantest.util.db.SqlException;
import com.tijiantest.util.validator.MobileValidator;

public class GetChecklistFastbookTest extends ChecklistBaseTest{
	int orderId = 0;
	int hospitalId=0;
	int companyId = defSKXCnewcompany.getId();;
	int healtherId = 0;
	
	@Test(description="极速预约",groups={"qa","crm_fastbook"},dataProvider="sanke_fastbook_1")
	public void test_01_fastbook(String...args ){
		Map<String,Object> mas = FastBookTest.parseArgs(args);
		FileAccountImportInfo customer = (FileAccountImportInfo)mas.get("customer");
		FastBookVo fast = (FastBookVo)mas.get("bookDto");
//		String idCard = mas.get("idCard").toString();
		int mealPrice = Integer.parseInt(mas.get("mealPrice").toString());
//		String mealName = mas.get("mealName").toString();
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		hospitalId= defhospital.getId();
		params.add(new BasicNameValuePair("hospitalId", hospitalId+""));
		params.add(new BasicNameValuePair("type", ModifyAccountType.NEW+""));

		//Step1:极速增加体检人
		HttpResult result = httpclient.post(Account_ModifyAccount, params, JSON.toJSONString(customer));
		Assert.assertEquals(result.getCode(),HttpStatus.SC_OK,"错误原因："+result.getBody());
		healtherId = JsonPath.read(result.getBody(),"$.result");
		
		//Step2:极速下单
		String jbody = JSON.toJSONString(fast);
		params.add(new BasicNameValuePair("healtherId", healtherId+""));
		result = httpclient.post(Order_FastBook, params,jbody);
		orderId = Integer.parseInt(JsonPath.read(result.getBody(),"$.orderId").toString());
		System.out.println("orderId="+orderId);
		
		Assert.assertEquals(result.getCode() , HttpStatus.SC_OK,"错误原因："+result.getBody());
		Assert.assertNotNull(result.getBody());
		Assert.assertEquals(Integer.parseInt(JsonPath.read(result.getBody(),"$.fee").toString()), mealPrice);
		
	}

	@Test(description = "极速预约打印预检凭证", groups = { "qa" }, dependsOnGroups = { "crm_fastbook" })
	public void test_01_getChecklistFastbook() throws SqlException, ParseException {
		List<NameValuePair> pairs = new ArrayList<>();
		pairs.add(new BasicNameValuePair("hospitalId", hospitalId + ""));
		pairs.add(new BasicNameValuePair("orderId", orderId + ""));

		HttpResult result = httpclient.get(Order_GetChecklistFastbook, pairs);
		String body = result.getBody();
		Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);

		FastbookChecklistVO fastbookChecklistVO = JSON.parseObject(body, FastbookChecklistVO.class);

		if (checkdb) {
			String mongoSql = "{'id':" + orderId + "}";
			List<Map<String, Object>> mongoList = MongoDBUtils.query(mongoSql, MONGO_COLLECTION);
			Map<String, Object> map = mongoList.get(0);
			// canModifyDate
			// 先获取提前几天导出
			String settingsSql = "select * from tb_hospital_settings where hospital_id=?";
			List<Map<String, Object>> settingsList = DBMapper.query(settingsSql, defhospital.getId());
			String previousExportDays = settingsList.get(0).get("previous_export_days").toString();
			if (map.get("examDate") == null) {
				// 自选日期
				Assert.assertNull(fastbookChecklistVO.getCanModifyDate());
			} else {
				Date examDate =  sdf.parse(getGMTDateString(map.get("examDate")));
				Date canModifyDate = DateUtils.offsetDestDay(examDate, 0 - Integer.parseInt(previousExportDays));
				Assert.assertEquals(fastbookChecklistVO.getCanModifyDate(), sdf.format(canModifyDate));
			}
			//examAddress
			String sql = "select * from tb_hospital_company where organization_id=? and id=?";
			List<Map<String, Object>> list = DBMapper.query(sql, defhospital.getId(), companyId);
			if (list.get(0).get("examination_address") == null
					|| list.get(0).get("examination_address").equals("")) {
				// 体检地址为空时，取医院地址
				String hospitalSql = "select * from tb_hospital where id=?";
				List<Map<String, Object>> hospitalList = DBMapper.query(hospitalSql, defhospital.getId());
				Assert.assertTrue(fastbookChecklistVO.getExamAddress().contains(hospitalList.get(0).get("address").toString()));
			} else {
				// 体检地址不为空
//				System.out.println("body...."+body);
				System.out.println(list.get(0).get("examination_address"));
				Assert.assertTrue(body.contains(list.get(0).get("examination_address").toString()));
			}
			
			//examDate
			String examDate = sdf.format(new Date());//当天
			String examTimeIntervalName = map.get("examTimeIntervalName").toString(); 
			Assert.assertEquals(fastbookChecklistVO.getExamDate(), examDate+" "+examTimeIntervalName);
			
			//loginName
			String userSql = "select * from tb_user where account_id=?";
			List<Map<String, Object>> userList = DBMapper.query(userSql, healtherId);
			// 不显示手机号
			for (int j = 0; j < userList.size(); j++) {
				Map<String, Object> m = userList.get(j);
				if (!MobileValidator.isMobile(m.get("username").toString())) {
					Assert.assertEquals(fastbookChecklistVO.getLoginName(), userList.get(0).get("username"));
				}
			}
			
			//order ，由于该功能和订单无关，只是需要orderId,所以不验证order了			
		}
	}
	
	@AfterClass(description="撤单",groups={"qa"},alwaysRun=true)
	private  void revokeOrder() throws SqlException{
		/*****撤销订单******/
		List<Integer> idList = new ArrayList<Integer>();
		idList.add(orderId);
		OrderChecker.Run_CrmOrderRevokeOrder(httpclient,idList,false,true,true);
		log.info("orderId = "+orderId+"订单已撤销!");
	}
	
	@DataProvider
	public Iterator<String[]> sanke_fastbook_1(){
		try {
			return CvsFileUtil.fromCvsFileToIterator("./csv/order/checklist/fastbook_1.csv",23);
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
