package com.tijiantest.testcase.channel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.tijiantest.base.*;
import com.tijiantest.base.dbcheck.HospitalChecker;
import com.tijiantest.model.account.RoleEnum;
import org.apache.http.Header;
import com.tijiantest.base.dbcheck.AccountChecker;
import com.tijiantest.model.account.LoggedLog;
import com.tijiantest.model.account.SystemTypeEnum;
import com.tijiantest.model.account.User;
import com.tijiantest.model.company.Company;
import com.tijiantest.model.hospital.Hospital;
import com.tijiantest.model.resource.meal.Meal;
import com.tijiantest.model.resource.meal.MealItem;
import com.tijiantest.model.resource.meal.MealSetting;
import com.tijiantest.util.Md5Util;
import com.tijiantest.util.db.DBMapper;
import com.tijiantest.util.db.SqlException;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

public class ChannelBase extends BaseTest {

	public static User defUser;
	public static int defaccountId;
	public static Company defcompany;
	public static Integer defChannelPlatMangerId;
	public static String defChannelPlatManager;
	public static String defChannelPlatManagerPwd;
	public static MyHttpClient httpclient;

	public static Header[] hs = null;
	public static SimpleDateFormat sy = new SimpleDateFormat("yyyy");
	public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	public static SimpleDateFormat simplehms = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	static {
		try {
			loadDefaultParams();
			httpclient = new MyHttpClient();
			onceLoginInSystem(httpclient, Flag.CHANNEL, defChannelUsername, defChannelPasswd);

			// 通过jvm进程的关闭钩子关闭共用的httpclient
			Runtime.getRuntime().addShutdownHook(new Thread() {
				@Override
				public void run() {
					httpclient.shutdown();
				}
			});
		} catch (SqlException e) {
			e.printStackTrace();
		}
	}

	private static void loadDefaultParams() throws SqlException {
		defChannelPlatManager = testConf.getValue(ConfDefine.CHANNELINFO, ConfDefine.CHANNELPLATMANAGER);
		defChannelPlatManagerPwd = testConf.getValue(ConfDefine.CHANNELINFO, ConfDefine.CHANNELPLATMANAGERPWD);

		defUser = getUserInfo(defChannelUsername);
		defaccountId = defUser.getAccount_id();
		defChannelPlatMangerId = AccountChecker.getUserInfo(defChannelPlatManager, RoleEnum.CRM_USER.getCode()).getAccount_id();
	}

	public static HttpResult loginChannel(MyHttpClient hc, String username, String password, String validationCode, String callbackurl, String rememberMe) {
		HttpResult result = null;
		List<NameValuePair> loginParams = new ArrayList<>();
		NameValuePair pa1 = new BasicNameValuePair("username", username);
		NameValuePair pa2 = new BasicNameValuePair("password", password);
		NameValuePair pa3 = new BasicNameValuePair("validationCode", validationCode);
		NameValuePair pa4 = new BasicNameValuePair("callbackurl", callbackurl);
		NameValuePair pa5 = new BasicNameValuePair("rememberMe", rememberMe);
		loginParams.add(pa1);
		loginParams.add(pa2);
		loginParams.add(pa3);
		loginParams.add(pa4);
		loginParams.add(pa5);
		result = httpclient.post(Flag.CHANNEL, Login, loginParams);
		return result;
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

	public String getAbsoluteMiniTime(int count) {
		Calendar beforeTime = Calendar.getInstance();
		beforeTime.add(Calendar.SECOND, -count);
		return simplehms.format(beforeTime.getTime());
	}

	/**
	 * tb_user
	 * 
	 * @param username
	 * @return
	 */
	public static User getUserInfo(String username) {
		String sql = "SELECT usr.*, acc.* FROM tb_user usr LEFT JOIN tb_account acc ON acc.id = usr.account_id WHERE usr.username =  ? AND usr.system=?";
		User user = null;
		List<Map<String, Object>> list = null;
		try {
			list = DBMapper.query(sql, username, SystemTypeEnum.CHANNEL_LOGIN.getCode());
			if (list.size() > 0) {
				Map<String, Object> m = list.get(0);
				user = new User(username);
				user.setAccount_id((Integer) m.get("account_id"));
				// user.setUrl(m.get("url").toString());
				user.setName(m.get("name").toString());
			}

		} catch (SqlException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		return user;
	}

	/**
	 * tb_meal
	 */
	public static Meal getMealInfo(int id) {
		String sql = "select * from tb_meal where id = ? ";
		Meal meal = null;
		List<Map<String, Object>> list = null;
		try {
			list = DBMapper.query(sql, id);
			if (list.size() > 0) {
				Map<String, Object> m = list.get(0);
				int hospitalId = (Integer) m.get("hospital_id");
				int gender = (Integer) m.get("gender");
				int type = (Integer) m.get("type");
				String name = m.get("name").toString();
				int price = (Integer) m.get("price");
				double discount = (double) m.get("discount");
				Object sequence = m.get("sequence");
				int init_price = (Integer) m.get("init_price");
				Object description = m.get("description");
				int disable = (Integer) m.get("disable");
				Object keyword = m.get("keyword");
				String pinYin = m.get("pinyin").toString();
				meal = new Meal(id, name, gender, price, hospitalId, init_price, type, discount, disable, pinYin);
				if (description != null)
					meal.setDescription(description.toString());
				if (m.get("sequence") != null)
					meal.setSequence((Integer) sequence);
				if (m.get("keyword") != null)
					meal.setKeyword(keyword.toString());
			}
		} catch (SqlException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		return meal;
	}

	public List<MealItem> getMealIteminfo(int mealId) {

		List<MealItem> mealItem = new ArrayList<MealItem>();
		MealItem item = null;
		String sqlStr = "select " + "me.*," + "e.gender " + "FROM tb_meal_examitem me " + "LEFT JOIN tb_examitem e "
				+ "ON me.item_id = e.id " + "WHERE me.meal_id = ? ";
		log.info("sqlstr:.............." + sqlStr);
		try {
			List<Map<String, Object>> list = DBMapper.query(sqlStr, mealId);
			for (Map<String, Object> m : list) {
				item = new MealItem((Integer) m.get("item_id"), (Integer) m.get("meal_id"),
						m.get("is_basic").equals(1) ? true : false, m.get("enable_select").equals(1) ? true : false,
						(Integer) m.get("gender"), m.get("selected").equals(1) ? true : false,
						(Integer) m.get("sequence"), m.get("is_show").equals(1) ? true : false);
				mealItem.add(item);

			}
		} catch (SqlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return mealItem;
	}

	public List<MealSetting> getMealSettingsInfo(int mealid) {

		List<MealSetting> mealettings = new ArrayList<MealSetting>();
		MealSetting mealSetting = null;
		String sqlStr = "SELECT * " + "FROM tb_meal_settings " + "WHERE meal_id = ?;";
		log.info("sqlstr:.............." + sqlStr);
		try {
			List<Map<String, Object>> list = DBMapper.query(sqlStr, mealid);
			for (Map<String, Object> m : list) {
				int mealId = (Integer) m.get("meal_id");
				boolean showMealPrice = m.get("show_meal_price").equals(1) ? true : false;
				boolean showItemPrice = m.get("show_meal_price").equals(1) ? true : false;
				Object only_show_meal_item = m.get("only_show_meal_item");
				int adjustPrice = (Integer) m.get("adjust_price");
				boolean lockPrice = m.get("lock_price").equals(1) ? true : false;

				mealSetting = new MealSetting(mealId, showMealPrice, showItemPrice, adjustPrice, lockPrice);

				if (m.get("only_show_meal_item") != null)
					mealSetting.setOnlyShowMealItem(Boolean.getBoolean(only_show_meal_item.toString()));

				mealettings.add(mealSetting);
			}
		} catch (SqlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return mealettings;
	}

	/**
	 * tb_hospital_settings
	 */
	public static Integer getBasicMealId(int hospitalId) {
		String sql = "SELECT basic_meal_id FROM tb_hospital_settings WHERE hospital_id = ? ";
		Integer basic_meal_id;
		List<Map<String, Object>> list = null;
		try {
			list = DBMapper.query(sql, hospitalId);
		} catch (SqlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		basic_meal_id = (Integer) list.get(0).get("basic_meal_id");
		return basic_meal_id;
	}


	public LoggedLog getLoggedCountByAccount(int accountId) {
		String sql = "SELECT * FROM tb_logged_log where account_id = ?;";
		List<Map<String, Object>> list = null;
		try {
			list = DBMapper.query(sql, accountId);
		} catch (SqlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		LoggedLog log = new LoggedLog();
		if(list.size()>0&&list!=null){
			Map<String,Object> map = list.get(0);
			log.setAccountId(Integer.valueOf(map.get("account_id").toString()));
			log.setId(Integer.valueOf(map.get("id").toString()));
			log.setSuccessLoggedCount(Integer.valueOf(map.get("success_logged_count").toString()));
			log.setSuccessLoggedTime((Date)map.get("success_logged_time"));
			System.out.println("success_logged_time:"+log.getSuccessLoggedTime());
		}
		return log;
	}
	
	public User getUserBySystemType(String username, int systemType) {
		User user = new User();
		String sql = "SELECT * FROM tb_user WHERE username = \""+username+"\" AND system = "+systemType+"";
		System.out.println("sql:"+sql);
		List<Map<String, Object>> list = null;
		try {
			list = DBMapper.query(sql);
		} catch (SqlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(list.size()>0&&list!=null){
			Map<String,Object> m = list.get(0);
			System.out.println("username:"+m.get("username").toString());
			int account_id = (Integer)m.get("account_id");
			System.out.println("account_id:"+account_id);
			user.setAccount_id(account_id);
			user.setUsername(m.get("username").toString());
			user.setPassword(m.get("password").toString());
			user.setSystem(Integer.valueOf(m.get("system").toString()));
		}
		return user;
	}
	
	public boolean matchPassword(Integer accountId, String password,
			String encrypted) {
		String encryptedPassword = getEncryptedPassword(password);
		return encrypted.equals(encryptedPassword);
	}
	
	public String getEncryptedPassword(String password) {		
		String salt = "123abc";
		String encryptedSalt = null;
		String encryptedPassword;

		if (encryptedSalt == null) {
			encryptedSalt = Md5Util.MD5(salt);
		}
		encryptedPassword = Md5Util.MD5(password + encryptedSalt);
		
		return encryptedPassword;
	}

}