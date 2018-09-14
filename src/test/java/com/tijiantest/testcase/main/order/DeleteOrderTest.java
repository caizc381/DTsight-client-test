package com.tijiantest.testcase.main.order;

import com.alibaba.fastjson.JSON;
import com.jayway.jsonpath.JsonPath;
import com.tijiantest.base.Flag;
import com.tijiantest.base.HttpResult;
import com.tijiantest.base.dbcheck.*;
import com.tijiantest.model.card.Card;
import com.tijiantest.model.company.HospitalCompany;
import com.tijiantest.model.counter.BizExceptionEnum;
import com.tijiantest.model.counter.CompanyCapacityUsed;
import com.tijiantest.model.counter.HospitalCapacityUsed;
import com.tijiantest.model.examitempackage.ExamItemInPackageInfo;
import com.tijiantest.model.examitempackage.ExamItemPackage;
import com.tijiantest.model.examitempackage.ExamItemPackageVO;
import com.tijiantest.model.hospital.Hospital;
import com.tijiantest.model.item.ExamItem;
import com.tijiantest.model.item.limitExamItemsVo;
import com.tijiantest.model.order.*;
import com.tijiantest.model.order.snapshot.ExamItemPackageSnapshot;
import com.tijiantest.model.order.snapshot.ExamItemSnapshot;
import com.tijiantest.model.order.snapshot.MealSnapshot;
import com.tijiantest.model.order.snapshot.OrderMealSnapshot;
import com.tijiantest.model.resource.meal.Meal;
import com.tijiantest.model.resource.meal.MealGenderEnum;
import com.tijiantest.model.resource.meal.MealItem;
import com.tijiantest.util.CvsFileUtil;
import com.tijiantest.util.db.DBMapper;
import com.tijiantest.util.db.MongoDBUtils;
import com.tijiantest.util.db.SqlException;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;


/**
 * C端删除订单
 *
 */
public class DeleteOrderTest extends OrderBaseTest {
	private  static int commOrderId = 0; // 不带单项包的订单
	private static List<Integer> orderId = new ArrayList<Integer>();
	private static Meal bookMeal = ResourceChecker.getOffcialMeal(defHospitalId,Arrays.asList( MealGenderEnum.FEMALE.getCode()),true).get(0);
	private static int bookMealId = bookMeal.getId();
	
	@Test(description = "先下单后删除订单测试(订单状态是未支付)",dataProvider = "mainBookOrder_delete", groups = { "qa"})
	public void test_01_deleteOrder(String... args) throws SqlException, ParseException {
		System.out.println("-----------------------01_C端下单(不带加项包的订单)Start----------------------------");
		String exam_date = args[1];
		HospitalCompany hc = CompanyChecker.getHospitalCompanyByPlatCompanyIdANDOrganizationId(1,defHospitalId);
		Map<String,Object> dateMap = CounterChecker.getDateBookableFromStartDate(sdf.parse(exam_date),sdf.parse(exam_date),hc.getId(), defHospitalId);
		int dayRangeId = Integer.parseInt(dateMap.get("dayRangeId").toString());
		String examTime_interval_id = dayRangeId+"";
		Integer addItemId = Integer.valueOf(args[6]);
		int account_id = defaccountId;
		commOrderId = OrderChecker.main_createOrder(httpclient,exam_date,Integer.parseInt(examTime_interval_id),account_id,defHospitalId);

		System.out.println("-----------------------01_C端下单(不带加项包的订单)End----------------------------");
		System.out.println("-----------------------01 开始删除订单Start----------------------------");

		HttpResult result = httpclient.post(Flag.MAIN, MainDeleteOrder, commOrderId);
		Assert.assertEquals(result.getCode(), HttpStatus.SC_OK, "错误提示：" + result.getBody());
		String text = JsonPath.read(result.getBody(),"$.content.text").toString();
		Assert.assertEquals(text,DeprecatedOrderExceptionEnum.CANNOT_DELETE.getMsg());
	}


	@AfterTest(alwaysRun = true, description = "撤销删除订单", dependsOnGroups = { "qa" })
	public void afterTest() throws Exception {
		System.out.println("-----------------------开始撤销订单-----------------------");
		for (Integer oid : orderId) {
			log.info("group...." + oid);
			Order order1 = OrderChecker.getOrderInfo(oid);
			if (order1.getStatus() == OrderStatus.ALREADY_BOOKED.intValue()
					|| order1.getStatus() == OrderStatus.ALREADY_PAY.intValue()
					|| order1.getStatus() == OrderStatus.NOT_PAY.intValue()
					|| order1.getStatus() == OrderStatus.PAYING.intValue()
					|| order1.getStatus() == OrderStatus.SITE_PAY.intValue()) {

				List<HospitalCapacityUsed> hospitalCounter = CounterChecker
						.getHospitalCount(defHospital.getId(), sdf.format(order1.getExamDate())).stream()
						.filter(hcu -> hcu.getPeriodId().intValue() == order1.getExamTimeIntervalId().intValue())
						.collect(Collectors.toList());
				List<CompanyCapacityUsed> companyCounter = null;
				companyCounter = CounterChecker.getCompanyCount(defHospital.getId(), order1.getExamCompanyId(),
								sdf.format(order1.getExamDate()))
						.stream().filter(ccu -> ccu.getPeriodId().intValue() == order1.getExamTimeIntervalId().intValue())
						.collect(Collectors.toList());

				// 验证订单操作日志
				List<ExamOrderOperateLogDO> logs1 = OrderChecker.getOrderOperatrLog(order1.getOrderNum());
				HttpResult response = httpclient.post(Flag.MAIN, MainRevokerOrder, oid);
				waitto(2);
				// 交易数据验证
				System.out.println("-----------------------开始校验撤销后订单交易数据 订单 " + order1.getId() + "..状态.."
						+ order1.getStatus() + "-----------------------");
				PayChecker.checkRevokeTrade(order1, order1.getStatus());
				System.out.println("-----------------------结束校验撤销后订单交易数据-----------------------");

				Assert.assertEquals(response.getCode(), HttpStatus.SC_OK, "错误提示：" + response.getBody());
				if (response.getCode() == HttpStatus.SC_OK) {
					Order order = OrderChecker.getOrderInfo(oid);
					System.err.println("查看这个订单的状态：" + order.getId() + "---" + order.getStatus());

					// 验证订单操作日志
					List<HospitalCapacityUsed> hospitalCounter1 = CounterChecker
							.getHospitalCount(defHospital.getId(), sdf.format(order1.getExamDate())).stream()
							.filter(hcu -> hcu.getPeriodId().intValue() == order1.getExamTimeIntervalId().intValue())
							.collect(Collectors.toList());
					List<CompanyCapacityUsed> companyCounter1 = null;
						companyCounter1 = CounterChecker
								.getCompanyCount(defHospital.getId(), order1.getExamCompanyId(),
										sdf.format(order1.getExamDate()))
								.stream().filter(ccu -> ccu.getPeriodId().intValue() == order1.getExamTimeIntervalId().intValue())
								.collect(Collectors.toList());
					CounterChecker.recycleCounterCheck(-1, companyCounter, companyCounter1, hospitalCounter,
							hospitalCounter1, 1);

					/*// 验证订单操作日志
					List<ExamOrderOperateLogDO> logs = OrderChecker.getOrderOperatrLog(order.getOrderNum());
					Assert.assertEquals(logs.get(0).getType().intValue(), OrderOperateTypeEnum.CANCEL_ORDRE.getCode());
					Assert.assertEquals(logs.get(0).getOrderStatus().intValue(), OrderStatus.REVOCATION.intValue());
					Assert.assertEquals(logs.get(0).getSystem().intValue(), OperateAppEnum.CLIENT.getCode());*/
					System.out.println(oid + "撤销成功！");
				} else {
					System.err.println(oid + "撤销失败！");
				}
				HttpResult result = httpclient.post(Flag.MAIN, MainDeleteOrder, oid);
				Assert.assertEquals(result.getCode(), HttpStatus.SC_OK, "错误提示：" + result.getBody());
				if (result.getCode() == HttpStatus.SC_OK) {
					Order order = OrderChecker.getOrderInfo(oid);
					// 验证订单操作日志
					List<ExamOrderOperateLogDO> logs = OrderChecker.getOrderOperatrLog(order.getOrderNum());
					Assert.assertEquals(logs.get(0).getType().intValue(), OrderOperateTypeEnum.DELETE_ORDER.getCode());
					Assert.assertEquals(logs.get(0).getOrderStatus().intValue(), OrderStatus.DELETED.intValue());
					Assert.assertEquals(logs.get(0).getSystem().intValue(), OperateAppEnum.CLIENT.getCode());
					System.out.println(oid + "删除成功！");
				} else
					System.err.println(oid + "删除失败！");
			}
		}
		System.out.println("-----------------------撤销订单结束-----------------------");
	}

	@DataProvider
	public Iterator<String[]> mainBookOrder_delete() {
		try {
			return CvsFileUtil.fromCvsFileToIterator("./csv/order/mainBookOrder_delete.csv", 8);
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
