package com.tijiantest.testcase.main.order;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.tijiantest.base.dbcheck.CompanyChecker;
import com.tijiantest.model.company.HospitalCompany;
import org.apache.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.tijiantest.base.Flag;
import com.tijiantest.base.HttpResult;
import com.tijiantest.base.dbcheck.AccountChecker;
import com.tijiantest.base.dbcheck.CounterChecker;
import com.tijiantest.base.dbcheck.OrderChecker;
import com.tijiantest.model.account.User;
import com.tijiantest.model.counter.CountForCheck;
import com.tijiantest.model.order.ExamOrderOperateLogDO;
import com.tijiantest.model.order.OperateAppEnum;
import com.tijiantest.model.order.Order;
import com.tijiantest.model.order.OrderOperateTypeEnum;
import com.tijiantest.testcase.main.payment.PayTest;
import com.tijiantest.util.CvsFileUtil;
import com.tijiantest.util.DateUtils;
import com.tijiantest.util.db.DBMapper;
import com.tijiantest.util.db.SqlException;

public class ChangeExamDateTest extends OrderBaseTest {
	public static int orderId;
	public Date oldExamDate = null;
	public String newExamDate = null;
	public String oldExamDateStr = null;

	@Test(dataProvider = "changeExamDate", groups = { "qa" }, dependsOnGroups = { "main_payWithBalance" })
	public void test_changeExamDateForOrderFromMain(String... args) throws SqlException, ParseException {
		System.out.println("-----------------------------C端改期测试Start---------------------------");
		Order order = PayTest.order;
		newExamDate = args[1];
		System.out.println("newExamDate:" + newExamDate);
		if (!order.equals(null) && !order.getIsExport()) {
			Date oldDate = cstFormater.parse(order.getExamDate().toString());
			oldExamDateStr = sdf.format(oldDate);
			oldExamDate = sdf.parse(oldExamDateStr);
			System.out.println("oldExamDateStr:"+oldExamDateStr+"        oldExamDate:"+oldExamDate);
			//老时间==新时间
			if (sdf.format(sdf.parse(newExamDate)).equals(oldExamDateStr))
				newExamDate = sdf.format(DateUtils.offsetDestDay(oldExamDate, 4));
			System.out.println("重新规划newExamDate:" + newExamDate);
			HospitalCompany hc = CompanyChecker.getHospitalCompanyByPlatCompanyIdANDOrganizationId(1,defHospitalId);
			Map<String,Object> dateMap = CounterChecker.getDateBookableFromStartDate(sdf.parse(newExamDate),sdf.parse(newExamDate),hc.getId(), defHospitalId);
			int dayRangeId = Integer.parseInt(dateMap.get("dayRangeId").toString());
			String examTime_interval_id = dayRangeId+"";
			Map<String, String> map = new HashMap<String, String>();
			map.put("orderId", String.valueOf(order.getId()));
			map.put("newExamDate", newExamDate);
			map.put("examTimeIntervalId", examTime_interval_id);

			//获取单位/体检中心人数
			CountForCheck oldDateCountRecord = CounterChecker.checkCount(order.getHospital().getId(), order.getExamCompanyId(), 
					oldExamDateStr, order.getExamTimeIntervalId());
			CountForCheck newDateCountRecord = CounterChecker.checkCount(order.getHospital().getId(), order.getExamCompanyId(), 
					newExamDate, order.getExamTimeIntervalId());
			
			HttpResult result = httpclient.post(Flag.MAIN, Order_ChangeExamDate, map);
			Assert.assertEquals(result.getCode(), HttpStatus.SC_OK,"改期失败.."+result.getBody()+"订单状态.."+OrderChecker.getOrderInfo(order.getId()).getStatus());
			System.out.println(result.getBody());
			orderId = order.getId();
			System.out.println("C端改期orderId：" + orderId);
			String sql = "select * from tb_order where id=?";
			List<Map<String, Object>> list = DBMapper.query(sql, orderId);
			Map<String, Object> map2 = list.get(0);

			System.out.println("orderId=" + orderId + "   orderNum=" + map2.get("order_num").toString()
					+ "       status=" + map2.get("status").toString() + "      is_export=" + map2.get("is_export")
					+ "      soruce=" + map2.get("source"));

			if (checkdb) {
				// 验证订单操作日志
				List<ExamOrderOperateLogDO> logs = OrderChecker.getOrderOperatrLog(order.getOrderNum());
				Assert.assertEquals(logs.get(0).getType().intValue(), OrderOperateTypeEnum.CHANGE_DATE.getCode());
				Assert.assertEquals(logs.get(0).getSystem().intValue(), OperateAppEnum.CLIENT.getCode());
				
				CountForCheck oldDateCountRecord1 = CounterChecker.checkCount(order.getHospital().getId(), order.getExamCompanyId(), 
						oldExamDateStr, order.getExamTimeIntervalId());
				CountForCheck newDateCountRecord1 = CounterChecker.checkCount(order.getHospital().getId(), order.getExamCompanyId(), 
						newExamDate, order.getExamTimeIntervalId());

				CounterChecker.counterCheck(-1, newDateCountRecord, newDateCountRecord1, 1, false);//验证人数扣除正确
				CounterChecker.counterCheck(-1, oldDateCountRecord, oldDateCountRecord1, 1, true);//验证人数回收正确

			}
		}
		System.out.println("-----------------------------C端改期测试End---------------------------");
	}
	
	
	@Test(dataProvider = "mainChangeExamDate_forCrm", groups = { "qa" },dependsOnGroups="main_mainUseCrmOrder")
	public void test_changeExamDateForOrderFromCRM(String... args) throws SqlException, ParseException, IOException {
		System.out.println("-----------------------------CRM订单C端改期测试Start---------------------------");
		Order crmCompOrder = PrepareMainOrderTest.crmOrder;;
		if (crmCompOrder!=null && !crmCompOrder.getIsExport()) {
			User mainUser = AccountChecker.getUser(crmCompOrder.getOrderAccount().getId()).get(0);
			onceLoginInSystem(httpclient, Flag.MAIN, mainUser.getUsername(), "111111");
			newExamDate = args[2];
			System.out.println("newExamDate:" + newExamDate);
			Date oldDate = cstFormater.parse(crmCompOrder.getExamDate().toString());
			oldExamDateStr = sdf.format(oldDate);
			oldExamDate = sdf.parse(oldExamDateStr);
			System.out.println("oldExamDateStr:"+oldExamDateStr+"        oldExamDate:"+oldExamDate);
//			HospitalCompany hc = CompanyChecker.getHospitalCompanyByPlatCompanyIdANDOrganizationId(1,defHospitalId);
//			Map<String,Object> dateMap = CounterChecker.getDateBookableFromStartDate(sdf.parse(newExamDate),sdf.parse(newExamDate),hc.getId(), defHospitalId);
//			int dayRangeId = Integer.parseInt(dateMap.get("dayRangeId").toString());
//			String examTime_interval_id = dayRangeId+"";
			Map<String, String> map = new HashMap<String, String>();
			map.put("orderId", String.valueOf(crmCompOrder.getId()));
			map.put("newExamDate", newExamDate);
			map.put("examTimeIntervalId", crmCompOrder.getExamTimeIntervalId()+"");
			
			//获取单位/体检中心人数	
			CountForCheck oldDateCountRecord = CounterChecker.checkCount(crmCompOrder.getHospital().getId(), crmCompOrder.getExamCompanyId(), 
					oldExamDateStr, crmCompOrder.getExamTimeIntervalId());
			CountForCheck newDateCountRecord = CounterChecker.checkCount(crmCompOrder.getHospital().getId(), crmCompOrder.getExamCompanyId(), 
					newExamDate, crmCompOrder.getExamTimeIntervalId());
			
			HttpResult result = httpclient.post(Flag.MAIN, Order_ChangeExamDate, map);
			System.out.println(result.getBody());
			orderId = crmCompOrder.getId();
			System.out.println("C端改期orderId：" + orderId);
			String sql = "select * from tb_order where id=?";
			List<Map<String, Object>> list = DBMapper.query(sql, orderId);
			Map<String, Object> map2 = list.get(0);

			System.out.println("orderId=" + orderId + "   orderNum=" + map2.get("order_num").toString()
					+ "       status=" + map2.get("status").toString() + "      is_export=" + map2.get("is_export")
					+ "      soruce=" + map2.get("source"));

			if (checkdb) {
				// 验证订单操作日志
				List<ExamOrderOperateLogDO> logs = OrderChecker.getOrderOperatrLog(crmCompOrder.getOrderNum());
				Assert.assertEquals(logs.get(0).getType().intValue(), OrderOperateTypeEnum.CHANGE_DATE.getCode());
				Assert.assertEquals(logs.get(0).getSystem().intValue(), OperateAppEnum.CLIENT.getCode());
				
				CountForCheck oldDateCountRecord1 = CounterChecker.checkCount(crmCompOrder.getHospital().getId(), crmCompOrder.getExamCompanyId(), 
						oldExamDateStr, crmCompOrder.getExamTimeIntervalId());
				CountForCheck newDateCountRecord1 = CounterChecker.checkCount(crmCompOrder.getHospital().getId(), crmCompOrder.getExamCompanyId(), 
						newExamDate, crmCompOrder.getExamTimeIntervalId());
				
				CounterChecker.counterCheck(-1, oldDateCountRecord, oldDateCountRecord1, 1, true);//验证人数回收正确
				CounterChecker.counterCheck(-1, newDateCountRecord, newDateCountRecord1, 1, false);//验证人数扣除正确
			}
		}
		else
			System.out.println("CRM下单失败，无法进行改期操作，请检查下单流程！！！");
		onceLogOutSystem(httpclient, Flag.MAIN);
		onceLoginInSystem(httpclient, Flag.MAIN, defMainUsername, defMainPasswd);
		System.out.println("-----------------------------CRM订单C端改期测试End---------------------------");
	}

	@AfterGroups(dependsOnGroups = { "qa" })
	public void resumeExamdate() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("orderId", String.valueOf(orderId));
		map.put("newExamDate", oldExamDateStr);
		map.put("examTimeIntervalId", "1");

		httpclient.post(Flag.MAIN, Order_ChangeExamDate, map);
	}

	@DataProvider
	public Iterator<String[]> changeExamDate() {
		try {
			return CvsFileUtil.fromCvsFileToIterator("./csv/order/mainChangeExamDate.csv", 3);
		} catch (FileNotFoundException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		return null;
	}
	@DataProvider
	public Iterator<String[]> mainChangeExamDate_forCrm() {
		try {
			return CvsFileUtil.fromCvsFileToIterator("./csv/order/mainChangeExamDate_forCrm.csv", 5);
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
