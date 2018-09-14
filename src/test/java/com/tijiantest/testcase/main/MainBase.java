package com.tijiantest.testcase.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.*;

import com.tijiantest.util.ListUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.http.Header;
import org.apache.http.HttpStatus;
import org.testng.Assert;
import com.tijiantest.base.BaseTest;
import com.tijiantest.base.ConfDefine;
import com.tijiantest.base.Flag;
import com.tijiantest.base.HttpResult;
import com.tijiantest.base.MyHttpClient;
import com.tijiantest.base.dbcheck.AccountChecker;
import com.tijiantest.base.dbcheck.CompanyChecker;
import com.tijiantest.base.dbcheck.HospitalChecker;
import com.tijiantest.base.dbcheck.OrderChecker;
import com.tijiantest.base.dbcheck.PayChecker;
import com.tijiantest.base.dbcheck.ResourceChecker;
import com.tijiantest.model.account.AddAccountTypeEnum;
import com.tijiantest.model.account.User;
import com.tijiantest.model.company.HospitalCompany;
import com.tijiantest.model.hospital.Hospital;
import com.tijiantest.model.hospital.HospitalParam;
import com.tijiantest.model.hospital.HospitalPeriodSetting;
import com.tijiantest.model.order.Order;
import com.tijiantest.model.order.OrderStatus;
import com.tijiantest.model.payment.trade.PayConstants;
import com.tijiantest.model.resource.meal.Meal;
import com.tijiantest.util.db.DBMapper;
import com.tijiantest.util.db.SqlException;

public class MainBase extends BaseTest {
	public static User defUser;
	public static int defaccountId;
	public static int defGender;
	public static int defHospitalId;
	public static HospitalPeriodSetting defhospitalPeriod;
	public static Hospital defHospital;
	public static HospitalCompany defHospitalCompany;
	public static String defSite;
	public static MyHttpClient httpclient;
	public static boolean isSmartRecommend;
	public static int packHospitalId;
	public static String packSiteUrl;
	public static Header[] hs = null;
	public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	public static SimpleDateFormat simplehms = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public static SimpleDateFormat cstFormater = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);

	public static String defaulturl = envConf.getValue(ConfDefine.MAINSITE, ConfDefine.MAINURL);

	public static String defaultManagerUsername;
	public static String defaultManagerPasswd;
	public static String packCrmUsername;
	public static String packCrmPasswd;
	
	public static String sankeName;
	public static String sankeUserIdCard;
	public static String companyUserName;
	public static String companyUserIdCard;
	
	static {
		loadDefaultParams();
		httpclient = new MyHttpClient();
		onceLoginInSystem(httpclient, Flag.MAIN, defMainUsername, defMainPasswd);
		// 充值
		recharge(3000000, defaccountId);
		clearData();
		
		// 通过jvm进程的关闭钩子关闭共用的httpclient
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
			}
		});
	}

	private static void loadDefaultParams() {
		defUser = AccountChecker.getUserInfo(defMainUsername);
		defaccountId = defUser.getAccount_id();
		defHospitalId = Integer.parseInt(testConf.getValue(ConfDefine.MAININFO, ConfDefine.HOSTPITALID));
		List<HospitalPeriodSetting> hospitalPeriodSettings = HospitalChecker.getHospitalPeriodSettings(defHospitalId);
		defhospitalPeriod = hospitalPeriodSettings.get(0);
		defHospital = HospitalChecker.getHospitalById(defHospitalId);
		defGender = AccountChecker.getExaminerByCustomerId(defaccountId,defHospital.getId()).getGender();

		// 智能推荐
		Map<String, Object> hosSyncs = HospitalChecker.getHospitalSetting(defHospitalId, HospitalParam.IS_SMART_RECOMMEND);
		Integer smart_recommend = Integer.parseInt(hosSyncs.get(HospitalParam.IS_SMART_RECOMMEND).toString());
		isSmartRecommend = smart_recommend == 1 ? true : false;
		// 站点
		defSite = HospitalChecker.getSiteByHospitalId(defHospitalId).getUrl();
		packHospitalId = Integer.parseInt(testConf.getValue(ConfDefine.MAININFO, ConfDefine.PackHospitalId));
		packSiteUrl = HospitalChecker.getSiteByHospitalId(packHospitalId).getUrl();

		try {
			defReceiveTradeAccountId = PayChecker.getSuitableReceiveMethodId(defHospitalId, PayConstants.PayMethodBit.BalanceBit);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		defaultManagerUsername = testConf.getValue(ConfDefine.MANAGEINFO, ConfDefine.USERNAME);
		defaultManagerPasswd = testConf.getValue(ConfDefine.MANAGEINFO, ConfDefine.PASSWORD);
		packCrmUsername = testConf.getValue(ConfDefine.MAININFO, ConfDefine.PACKUSERNAME);
		packCrmPasswd = testConf.getValue(ConfDefine.MAININFO, ConfDefine.PACKASSWORD);
		Integer companyId = Integer.valueOf(testConf.getValue(ConfDefine.MAININFO, ConfDefine.COMPANYID));
		defHospitalCompany = CompanyChecker.getHCompanyByOldCompanyIdANDOrgId(companyId, defHospitalId);
	}

	private static void recharge(int balance, int accountId) {
		// TODO Auto-generated method stub
		String sql = "UPDATE tb_accounting set balance = " + balance + " WHERE account_id = " + accountId + "; ";
		try {
			DBMapper.update(sql);
		} catch (SqlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	protected static void clearData() {
		/**
		 * 清理订单 体检日期为2018-12-01至2018-12-31
		 */
		System.out.println("*****C端清理订单*****");
		String orderIds = "";
		// 获取垃圾订单（已撤销订单）
		String sqlStr = "SELECT * FROM tb_order " + "WHERE hospital_id = " + defHospital.getId()
				+ " AND STATUS IN (2, 11) AND source in (1,2) AND is_export = 0 " + "AND account_id = " + defaccountId + " " + "AND ("
				+ "( exam_date < NOW() ) OR " + "( exam_date BETWEEN '2018-12-01' AND '2018-12-31' ) OR "
				+ "( exam_date BETWEEN '2018-03-03' AND '2018-12-31' ) )" + "ORDER BY insert_time DESC";
		System.out.println(sqlStr);
		List<Map<String, Object>> list = null;
		try {
			list = DBMapper.query(sqlStr);
		} catch (SqlException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		if (list.size() > 0) {
			List<Integer> orderList = new ArrayList<Integer>();
			for(Map<String,Object> map : list)
				orderList.add(Integer.parseInt(map.get("id").toString()));
			orderIds = ListUtil.IntegerlistToString(orderList);
			try {
				OrderChecker.Run_MainOrderRevokeOrder(httpclient, orderList, false, true, true);
			} catch (SqlException e) {
				e.printStackTrace();
			}
			// database
			if (checkdb) {
				waitto(5);
				String sql = "SELECT * FROM tb_order WHERE id in (" + orderIds.substring(0, orderIds.length() - 1)
						+ ")";
				System.out.println(sql);
				List<Map<String, Object>> retlist = null;
				try {
					retlist = DBMapper.query(sql);
				} catch (SqlException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				for (Map<String, Object> r : retlist)
					Assert.assertEquals(Integer.parseInt(r.get("status").toString()),
							OrderStatus.REVOCATION.intValue());

			}
			log.info("订单已撤销!");
		}
		sqlStr = "SELECT * FROM tb_order WHERE account_id = " + defaccountId + " AND status in (5,8);";
		try {
			list = DBMapper.query(sqlStr);
		} catch (SqlException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if (list.size() > 0) {
			for (Map<String, Object> map : list) {
				Integer orderId = Integer.valueOf(map.get("id").toString());
				orderIds = orderIds + orderId + ",";
				HttpResult result = httpclient.post(Flag.MAIN, MainDeleteOrder, orderId);
				Assert.assertEquals(result.getCode(), HttpStatus.SC_OK, "错误提示：" + result.getBody());
			}
			System.out.println("删除的订单有：" + orderIds.substring(0, orderIds.length() - 1));
			// database
			if (checkdb) {
				waitto(5);
				String sql = "SELECT * FROM tb_order WHERE id in (" + orderIds.substring(0, orderIds.length() - 1)
						+ ")";
				List<Map<String, Object>> retlist = null;
				try {
					retlist = DBMapper.query(sql);
				} catch (SqlException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				for (Map<String, Object> r : retlist)
					Assert.assertEquals(Integer.parseInt(r.get("status").toString()), OrderStatus.DELETED.intValue());

			}
			log.info("订单已删除!");
		}
	}

	/**
	 * 
	 * 将InputStream 转化为String
	 * 
	 * @param stream
	 *            inputstream
	 * @param utf8
	 *            字符集
	 * @return
	 * @throws IOException
	 */
	public String getStreamAsString(InputStream stream, Charset utf8) throws IOException {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(stream, utf8), 8192);
			StringWriter writer = new StringWriter();

			char[] chars = new char[8192];
			int count = 0;
			while ((count = reader.read(chars)) > 0) {
				writer.write(chars, 0, count);
			}

			return writer.toString();
		} finally {
			if (stream != null) {
				stream.close();
			}
		}
	}

	/*********** public functions *******/
	/**
	 * 获取前数分钟的时间
	 * 
	 * @param count
	 * @return
	 */
	public String getAbsoluteTime(int count) {
		Calendar beforeTime = Calendar.getInstance();
		beforeTime.add(Calendar.MINUTE, -count);
		return simplehms.format(beforeTime.getTime());
	}



	/**
	 * 
	 * @param ja
	 *            json数组
	 * @param field
	 *            要排序的key
	 * @param isAsc
	 *            是否升序
	 */
	@SuppressWarnings("unchecked")
	public static void sort(JSONArray ja, final String field, boolean isAsc) {
		Collections.sort(ja, new Comparator<JSONObject>() {
			@Override
			public int compare(JSONObject o1, JSONObject o2) {
				Object f1 = o1.get(field);
				Object f2 = o2.get(field);
				if (f1 instanceof Number && f2 instanceof Number) {
					return ((Number) f1).intValue() - ((Number) f2).intValue();
				} else {
					return f1.toString().compareTo(f2.toString());
				}
			}
		});
		if (!isAsc) {
			Collections.reverse(ja);
		}
	}

    /**
	 * 从基本base.conf文件中读取姓名和身份证号
	 */
	public static void getBaseConf() {
		sankeName = baseconf.getValue("sanke", "姓名");
		sankeUserIdCard = baseconf.getValue("sanke", "身份证");
		companyUserName = baseconf.getValue("company", "姓名");
		companyUserIdCard = baseconf.getValue("company", "身份证");
		log.debug("散客:" + sankeName + " 身份证:" + sankeUserIdCard);
		log.debug("单位:" + companyUserName + " 身份证:" + companyUserIdCard);
	}

//	/**
//	 * CRM下单
//	 * @return
//	 */
//	public static Order createCrmOrder(){
//		Order order = new Order();
//		MyHttpClient myClient = new MyHttpClient();
//		onceLoginInSystem(myClient, Flag.CRM, defCrmUsername, defCrmPasswd);
//
//		//CRM下单准备
//		Integer companyId = defHospitalCompany.getId();
//		String companyName = defHospitalCompany.getName();
////		HospitalCompany hCompany = CompanyChecker.getHCompanyByOldCompanyIdANDOrgId(companyId, defHospitalId);
//		Integer companyAccountId;
//		String examDate = "2018-12-02";
//		Integer managerId = AccountChecker.getUserInfo(defCrmUsername).getAccount_id();
//		//导入客户
//		System.out.println("----(1)导入客户----");
//		try {
//			AccountChecker.uploadAccount(myClient, companyId.intValue(), defHospitalId, "autotest_单位测试组",
//					"./csv/base/company_account.xlsx",AddAccountTypeEnum.idCard);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		getBaseConf();
//		companyAccountId = AccountChecker.getAccountId(companyUserIdCard, companyUserName, "autotest_单位测试组",managerId,defHospital.getId());
////		User mainUser = AccountChecker.getUser(companyAccountId).get(0);
//		//获取套餐
//		System.out.println("----(2)获取套餐----");
//		Meal meal = ResourceChecker.getOffcialMeal(defHospitalId).get(0);
//		//CRM下单
//		System.out.println("----(3)CRM下单----");
//		try {
//			order = OrderChecker.crm_createOrder(myClient, meal.getId().intValue(), companyAccountId, companyId.intValue(),companyName,
//						examDate,defHospital);
//		} catch (SqlException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return order;
//	}

}