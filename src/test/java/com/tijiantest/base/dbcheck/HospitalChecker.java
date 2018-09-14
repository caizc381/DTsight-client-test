package com.tijiantest.base.dbcheck;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tijiantest.model.organization.OrganizationTypeEnum;
import org.apache.ibatis.cache.CacheException;
import org.testng.Assert;

import com.tijiantest.base.BaseTest;
import com.tijiantest.model.common.SystemParam;
import com.tijiantest.model.hospital.Address;
import com.tijiantest.model.hospital.CalculatorServiceEnum;
import com.tijiantest.model.hospital.Hospital;
import com.tijiantest.model.hospital.HospitalParam;
import com.tijiantest.model.hospital.HospitalPeriodSetting;
import com.tijiantest.model.hospital.HospitalVO;
import com.tijiantest.model.site.Site;
import com.tijiantest.util.PinYinUtil;
import com.tijiantest.util.db.DBMapper;
import com.tijiantest.util.db.SqlException;

/**
 * 医院校验（包括建站，站点设置属性)
 * 
 * @author huifang
 *
 */
public class HospitalChecker extends BaseTest {

	public List<Integer> getHospitalPeriod(Integer hospitalId) {
		List<Integer> periodIds = new ArrayList<>();
		String sql = "SELECT * FROM tb_hospital_period_settings WHERE hospital_id = ?;";
		List<Map<String, Object>> list = null;
		try {
			list = DBMapper.query(sql, hospitalId);
		} catch (SqlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		list.forEach(m -> {
			Integer id = Integer.valueOf(m.get("id").toString());
			periodIds.add(id);
		});
		return periodIds;
	}


	/**
	 * 根据url获取hospitalId
	 *
	 * @param site
	 * @return
	 */
	public static Hospital getHospitalBySite(String site) {
		Hospital hospital = new Hospital();
		String sql = "SELECT  h.* FROM tb_site s ,tb_hospital h  WHERE h.id = s.hospital_id and s.url=?";
		List<Map<String, Object>> list = null;
		try {
			list = DBMapper.query(sql, site);
		} catch (SqlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (list == null || list.isEmpty()) {
			log.error("该环境上没有渠道商二级站点/体检中心二级站点，请手动新建");
			return null;
		}
		Assert.assertEquals(list.size(), 1);
		Map<String, Object> map = list.get(0);
		Integer hospitalId = Integer.parseInt(map.get("id").toString());
		hospital.setId(hospitalId);
		hospital.setName(map.get("name").toString());
		hospital.setOrganizationType(Integer.parseInt(map.get("organization_type").toString()));
		hospital.setDefaultManagerId(Integer.parseInt(map.get("default_manager_id").toString()));
		hospital.setShowInList(Integer.parseInt(map.get("show_in_list").toString()));
		hospital.setEnable(Integer.parseInt(map.get("enable").toString()));
		return hospital;
	}

	/**
	 * 获取医院的预约时间段id列表
	 * @param hospitalId
	 * @return
     */
	public static List<Integer> getHospitalPeriodRangeLists(int hospitalId) {
		List<HospitalPeriodSetting> settings = HospitalChecker.getHospitalPeriodSettings(hospitalId);
		List<Integer> dayRangeIdLists = new ArrayList<Integer>();
		for(HospitalPeriodSetting s : settings)
			dayRangeIdLists.add(s.getId());
		return dayRangeIdLists;
	}
	/**
	 * tb_hospital_period
	 */
	public static List<HospitalPeriodSetting> getHospitalPeriodSettings(int hospitalId, int exam_time_interval_id) {
		List<HospitalPeriodSetting> hospitalSettings = new ArrayList<HospitalPeriodSetting>();
		HospitalPeriodSetting hospitalSetting = null;
		String sqlStr = "SELECT * " + "FROM tb_hospital_period_settings " + "WHERE hospital_id = ? and id = ?";
		log.debug("sqlstr:.............." + sqlStr);
		try {
			List<Map<String, Object>> list = DBMapper.query(sqlStr, hospitalId, exam_time_interval_id);
			for (Map<String, Object> m : list) {
				int id = (Integer) m.get("id");
				String name = m.get("name").toString();
				hospitalSetting = new HospitalPeriodSetting(name, hospitalId);
				hospitalSetting.setId(id);
				hospitalSettings.add(hospitalSetting);
			}
		} catch (SqlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return hospitalSettings;
	}

	/**
	 * tb_hospital_period
	 */
	public static List<HospitalPeriodSetting> getHospitalPeriodSettings(int hospitalId) {
		List<HospitalPeriodSetting> hospitalSettings = new ArrayList<HospitalPeriodSetting>();
		HospitalPeriodSetting hospitalSetting = null;
		String sqlStr = "SELECT * " + "FROM tb_hospital_period_settings " + "WHERE hospital_id = ?;";
		log.debug("sqlstr:.............." + sqlStr);
		try {
			List<Map<String, Object>> list = DBMapper.query(sqlStr, hospitalId);
			for (Map<String, Object> m : list) {
				int id = (Integer) m.get("id");
				String name = m.get("name").toString();
				hospitalSetting = new HospitalPeriodSetting(name, hospitalId);
				hospitalSetting.setId(id);
				hospitalSettings.add(hospitalSetting);
			}
		} catch (SqlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return hospitalSettings;
	}

	/**
	 * 查询体检中心配置
	 * 
	 * @param hospitalId
	 * @param params
	 * @return
	 */
	public static Map<String, Object> getHospitalSetting(int hospitalId, String... params) {
		String ps = "";
		for (String p : params) {
			ps += p + ",";
		}
		ps = ps.substring(0, ps.length() - 1);
		String sql = "SELECT " + ps + " FROM tb_hospital_settings WHERE hospital_id = ? ";
		List<Map<String, Object>> list = null;
		try {
			list = DBMapper.query(sql, hospitalId);
		} catch (SqlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		HashMap<String, Object> retMap = new HashMap<String, Object>();
		for (String s : params) {
			retMap.put(s, list.get(0).get(s));
		}

		return retMap;
	}

	/**
	 * 根据url获取hospitalId
	 * 
	 * @param site
	 * @return
	 */
	public static Hospital getHospitalIdBySite(String site) throws SqlException {
		Hospital hospital = new Hospital();
		String sql = "SELECT  h.* FROM tb_site s ,tb_hospital h  WHERE h.id = s.hospital_id and s.url=?";
		List<Map<String, Object>> list = DBMapper.query(sql, site);
		Assert.assertEquals(list.size(), 1);
		Map<String, Object> map = list.get(0);
		Integer hospitalId = Integer.parseInt(map.get("id").toString());
		hospital.setId(hospitalId);
		hospital.setName(map.get("name").toString());
		hospital.setOrganizationType(Integer.parseInt(map.get("organization_type").toString()));
		hospital.setDefaultManagerId(Integer.parseInt(map.get("default_manager_id").toString()));
		hospital.setShowInList(Integer.parseInt(map.get("show_in_list").toString()));
		hospital.setEnable(Integer.parseInt(map.get("enable").toString()));
		return hospital;
	}

	/**
	 * 根据url获取支持的体检中心
	 * 渠道商站点url对应多个体检中心
	 * 普通体检中心url对应1个体检中心
	 * 使用卡的时候isShow传false,不使用卡传入isSHow=true（体检中心显示只是在不用卡的时候有效）
	 *
	 *
	 * @param site
	 * @return
	 */
	public static List<Hospital> getSupportHospitalListBySite(String site,boolean isShow) throws SqlException {
		List<Hospital> hospitalList = new ArrayList<>();
		String sql = "SELECT  h.* FROM tb_site s ,tb_hospital h  WHERE h.id = s.hospital_id and s.url=?";
		List<Map<String, Object>> list = DBMapper.query(sql, site);
		if(list.size() == 1){
			Map<String, Object> map = list.get(0);
			//普通体检中心
			if(Integer.parseInt(map.get("organization_type").toString()) == OrganizationTypeEnum.HOSPITAL.getCode().intValue()){
				Hospital hospital = new Hospital();
				Integer hospitalId = Integer.parseInt(map.get("id").toString());
				hospital.setId(hospitalId);
				hospital.setName(map.get("name").toString());
				hospital.setOrganizationType(Integer.parseInt(map.get("organization_type").toString()));
				hospital.setDefaultManagerId(Integer.parseInt(map.get("default_manager_id").toString()));
				hospital.setShowInList(Integer.parseInt(map.get("show_in_list").toString()));
				hospital.setEnable(Integer.parseInt(map.get("enable").toString()));
				hospitalList.add(hospital);
			}else{//渠道商站点
				int hospitalId = Integer.parseInt(map.get("id").toString());
				sql = "select h.* from  tb_organization_hospital_relation r , tb_hospital " +
						" h  where r.hospital_id = h.id   and h.`enable` = 1 and  r.organization_id = "+hospitalId;
				if(isShow)
					sql += " and  h.show_in_list = 1";
				list = DBMapper.query(sql);
				for(Map<String,Object> map1 : list){
					Hospital hospital = new Hospital();
					hospital.setId(Integer.parseInt(map1.get("id").toString()));
					hospital.setName(map1.get("name").toString());
					hospital.setOrganizationType(Integer.parseInt(map1.get("organization_type").toString()));
					if(map1.get("default_manager_id")!=null)
						hospital.setDefaultManagerId(Integer.parseInt(map1.get("default_manager_id").toString()));
					if(map1.get("show_in_list")!=null)
						hospital.setShowInList(Integer.parseInt(map1.get("show_in_list").toString()));
					hospital.setEnable(Integer.parseInt(map1.get("enable").toString()));
					hospitalList.add(hospital);
				}
			}
		}
		return hospitalList;
	}

	/**
	 * 根据医院设定的计算精度四舍五入
	 */
	public static int calculator_data(int hospitalId, int price) {
		Object calculator_service = getHospitalSetting(hospitalId, HospitalParam.CALCULATOR_SERVICE)
				.get(HospitalParam.CALCULATOR_SERVICE);
		int nowPrice = price;
		if (calculator_service != null) {

			if (calculator_service.equals(CalculatorServiceEnum.YUAN_ROUND_CALCULATOR.getName())) {
				nowPrice = (int) Math.round(price / 100.0) * 100;
			}
			if (calculator_service.equals(CalculatorServiceEnum.JIAO_ROUND_CALCULATOR.getName())) {
				nowPrice = (int) Math.round(price / 10.0) * 10;
			}
			if (calculator_service.equals(CalculatorServiceEnum.FEN_ROUND_CALCULATOR.getName())) {
				nowPrice = price;
			}
			if (calculator_service.equals("")) {
				nowPrice = (int) Math.round(price / 100.0) * 100;
			}
		} else {
			nowPrice = (int) Math.round(price / 100.0) * 100;

		}
		return nowPrice;
	}

	/**
	 * 通过organization_id获取organization_type
	 * 
	 * @param organizationId
	 * @return
	 */
	public static Integer getOrganizationType(Integer organizationId) {
		Integer type = 0;
		String sql = "SELECT * FROM tb_hospital WHERE id = " + organizationId + ";";
		Map<String, Object> map = null;
		try {
			map = DBMapper.query(sql).get(0);
		} catch (SqlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		type = Integer.valueOf(map.get("organization_type").toString());
		return type;
	}

	/**
	 * 根据机构id获取体检中心列表
	 * 
	 * @param organizationId
	 * @return
	 * @throws SqlException
	 */
	public static List<Hospital> getHospitalByOrganizationId(Integer organizationId) throws SqlException {
		List<Hospital> hospitals = new ArrayList<>();
		String sql = "SELECT * FROM tb_organization_hospital_relation oh "
				+ "LEFT JOIN tb_hospital h on h.id = oh.hospital_id "
				+ "where oh.organization_id = ? AND h.`enable`=1 and h.show_in_list = 1 order by id ;";
		List<Map<String, Object>> list = DBMapper.query(sql, organizationId);
		// 渠道商现在支持所有体检中心
		if (list.size() == 0 || (list.size() > 0 && Integer.parseInt(list.get(0).get("status").toString()) == 2)) {
			String sql1 = "select id,name from tb_hospital where organization_type=1 and show_in_list=1 and enable=1";
			List<Map<String, Object>> list1 = DBMapper.query(sql1);
			for (Map<String, Object> m : list1) {
				Hospital hospital = new Hospital();
				hospital.setId(Integer.valueOf(m.get("id").toString()));
				hospital.setName(m.get("name").toString());
				hospitals.add(hospital);
			}
		}
		// 渠道商现在受控制，且不支持任何体检中心
		if (list.size() == 1 && Integer.parseInt(list.get(0).get("hospital_id").toString()) == -1) {
			hospitals = null;
		}

		// 渠道商现在支持部分体检中心
		if (list.size() > 0 && Integer.parseInt(list.get(0).get("status").toString()) == 1
				&& Integer.parseInt(list.get(0).get("hospital_id").toString()) != -1) {
			for (Map<String, Object> m : list) {
				Hospital hospital = new Hospital();
				hospital.setId(Integer.valueOf(m.get("hospital_id").toString()));
				hospitals.add(hospital);
			}
		}
		return hospitals;
	}

	/**
	 * 根据体检中心id查询站点信息
	 * 
	 * @param hospitalId
	 * @return
	 */
	public static Site getSiteByHospitalId(int hospitalId) {
		Site site = new Site();
		String sql = "select * from tb_site where hospital_id = ?";
		List<Map<String, Object>> list = null;
		try {
			list = DBMapper.query(sql, hospitalId);
			if (list.size() > 0) {
				Map<String, Object> m = list.get(0);
				site.setUrl(m.get("url").toString());
				site.setManagerId(Integer.parseInt(m.get("manager_id").toString()));
				if(m.get("site_type")!=null)
					site.setSiteType(Integer.parseInt(m.get("site_type").toString()));
				if(m.get("template_id")!=null)
					site.setTemplateId(Integer.parseInt(m.get("template_id").toString()));
			}

		} catch (SqlException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		return site;
	}

	/**
	 * 获取体检中心信息
	 * 
	 * @param id
	 * @return
	 */
	public static Hospital getHospitalById(int id) {
		String sql = "SELECT * FROM tb_hospital WHERE id = ?";
		Hospital hospital = null;
		List<Map<String, Object>> list = null;
		try {
			list = DBMapper.query(sql, id);
			if (list.size() > 0) {
				Map<String, Object> m = list.get(0);
				hospital = new Hospital();
				if(m.get("name") != null)
					hospital.setName(m.get("name").toString());
				hospital.setId(id);
				hospital.setPhone(m.get("phone").toString());
				if(m.get("exam_notice")!=null)
					hospital.setExamNotice(m.get("exam_notice").toString());
				if (m.get("keywords") != null)
					hospital.setKeywords(m.get("keywords").toString());
				hospital.setType(m.get("type").toString());
				if(m.get("longitude")!=null)
					hospital.setLongitude(m.get("longitude").toString());
				if(m.get("latitude")!=null)
					hospital.setLatitude(m.get("latitude").toString());
				if(m.get("default_manager_id")!=null)
					hospital.setDefaultManagerId(Integer.parseInt(m.get("default_manager_id").toString()));
				int address_id = Integer.parseInt(m.get("address_id").toString());

				Address address = getAddressByAddressId(address_id);
				address.setAddress(m.get("address").toString());
				hospital.setOrganizationType(Integer.parseInt(m.get("organization_type").toString()));
				hospital.setAddress(address);

			}

		} catch (SqlException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		return hospital;
	}

	/**
	 * 根据address_id查询省份/市/区
	 * @param addressId
	 * @return
	 */
	public static Address getAddressByAddressId(int addressId){
		Address address = new Address();
		String sql = "select * from tb_address where id = "+addressId;
		try {
			List<Map<String,Object>> list = DBMapper.query(sql);
			if(list != null && list.size()>0){
				Map<String,Object> map = list.get(0);
				if(map.get("city")!=null)
					address.setCity(map.get("city").toString());
				if(map.get("province")!=null)
					address.setProvince(map.get("province").toString());
				if(map.get("district")!=null)
					address.setDistrict(map.get("district").toString());
			}
		} catch (SqlException e) {
			e.printStackTrace();
		}
		return address;

	}
	/**
	 * 获取全局配置字典 tb_system_param
	 * 
	 * @param paramKey
	 * @param hospitalId
	 * @return
	 */
	public static SystemParam getSysParam(String paramKey, Integer hospitalId) {
		String sql = "SELECT id, " + "hospital_id, " + "param_name, " + "param_key, " + "param_value, "
				+ "description, STATUS, sequence, " + "create_time, " + "update_time " + "FROM tb_system_param "
				+ "WHERE STATUS != 2 AND param_key = '" + paramKey + "'";

		if (hospitalId != null)
			sql += "AND hospital_id = " + hospitalId + " ";
		else
			sql += "AND ISNULL(hospital_id) ";
		sql += "ORDER BY sequence ";
		System.out.println(sql);
		List<SystemParam> spList = new ArrayList<SystemParam>();
		List<Map<String, Object>> list = null;
		try {
			list = DBMapper.query(sql);
		} catch (SqlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (list.size() > 0 && !list.isEmpty()) {
			for (Map<String, Object> map : list) {
				SystemParam sp = new SystemParam();
				sp.setId(Integer.valueOf(map.get("id").toString()));
				if (map.get("hospital_id") != null)
					sp.setHospitalId(Integer.valueOf(map.get("hospital_id").toString()));
				sp.setParamName(map.get("param_name").toString());
				sp.setParamKey(map.get("param_key").toString());
				sp.setParamValue(map.get("param_value").toString());
				if (map.get("description") != null)
					sp.setDescription(map.get("description").toString());
				sp.setStatus(Integer.valueOf(map.get("status").toString()));
				sp.setSequence(Integer.valueOf(map.get("sequence").toString()));
				spList.add(sp);
			}
			if (spList.size() > 1) {
				throw new CacheException("too many SystemParam");
			}
			return spList.get(0);
		}
		return null;
	}

	public static String getSiteByOrganizationId(Integer organizationId) {
		String site = null;
		String sql = "select url from tb_site where hospital_id = ?";
		Map<String, Object> map = null;
		try {
			map = DBMapper.query(sql, organizationId).get(0);
		} catch (SqlException e) {
			e.printStackTrace();
		}
		site = map.get("url").toString();
		return site;
	}

	// 根据_site判断是否是体检中心，返回organizationType
	public static int checkHospitalOrChannel(String _site) throws SqlException {
		String siteSql = "select * from tb_site where url=?";
		List<Map<String, Object>> siteList = DBMapper.query(siteSql, _site);
		String hospitalSql = "select * from tb_hospital where id=?";
		List<Map<String, Object>> hospitalList = DBMapper.query(hospitalSql, siteList.get(0).get("hospital_id"));
		int organizationType = Integer.parseInt(hospitalList.get(0).get("organization_type").toString());
		return organizationType;
	}

	public static List<Hospital> getOrganizations(Integer type) {
		List<Hospital> orgList = new ArrayList<Hospital>();
		String sql = "select * from tb_hospital where organization_type=?";
		List<Map<String, Object>> dblist = null;
		try {
			dblist = DBMapper.query(sql, type);
		} catch (SqlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (dblist != null && dblist.size() > 0)
			for (Map<String, Object> m : dblist) {
				Hospital org = new Hospital();
				org.setId(Integer.valueOf(m.get("id").toString()));
				org.setName(m.get("name").toString());
				orgList.add(org);
			}
		return orgList;
	}

	/**
	 * 获取所有体检中心
	 * 
	 * @return
	 */
	public static List<HospitalVO> getAllOrganizations() {
		List<HospitalVO> orgList = new ArrayList<HospitalVO>();
		String sql = "select * from tb_hospital ";
		List<Map<String, Object>> dblist = null;
		try {
			dblist = DBMapper.query(sql);
		} catch (SqlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (dblist != null && dblist.size() > 0)
			for (Map<String, Object> m : dblist) {
				HospitalVO org = new HospitalVO();
				org.setId(Integer.valueOf(m.get("id").toString()));
				org.setName(m.get("name").toString());
				org.setOrganizationType(Integer.parseInt(m.get("organization_type").toString()));
				org.setPinyin(PinYinUtil.getFirstSpell(m.get("name").toString()));
				orgList.add(org);
			}
		return orgList;
	}


	/**
	 * 获取所有体检中心(不包括渠道站点)
	 *
	 * @return
	 */
	public static List<HospitalVO> getAllHospitals() {
		List<HospitalVO> orgList = new ArrayList<HospitalVO>();
		String sql = "select * from tb_hospital  where organization_type=1";
		List<Map<String, Object>> dblist = null;
		try {
			dblist = DBMapper.query(sql);
		} catch (SqlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (dblist != null && dblist.size() > 0)
			for (Map<String, Object> m : dblist) {
				HospitalVO org = new HospitalVO();
				org.setId(Integer.valueOf(m.get("id").toString()));
				org.setName(m.get("name").toString());
				org.setOrganizationType(Integer.parseInt(m.get("organization_type").toString()));
				org.setPinyin(PinYinUtil.getFirstSpell(m.get("name").toString()));
				orgList.add(org);
			}
		return orgList;
	}
	/**
	 * 获取医院联系人表最大id
	 * 
	 * @return
	 */
	public static Integer getMaxIdOfHospitalContact() {
		Integer maxId = 0;
		String sql = "SELECT MAX(id) FROM tb_hospital_contact;";
		List<Map<String, Object>> list = null;
		try {
			list = DBMapper.query(sql);
		} catch (SqlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (list != null && !list.isEmpty()) {
			maxId = Integer.valueOf(list.get(0).get("MAX(id)").toString());
		}
		return maxId;
	}

	/**
	 * 获取医院联系人配置表最大id
	 * 
	 * @return
	 */
	public static Integer getMaxIdOfHospitalContactMessConfig() {
		Integer maxId = 0;
		String sql = "SELECT MAX(id) FROM tb_hospital_contact_message_config;";
		List<Map<String, Object>> list = null;
		try {
			list = DBMapper.query(sql);
		} catch (SqlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (list != null && !list.isEmpty()) {
			maxId = Integer.valueOf(list.get(0).get("MAX(id)").toString());
		}
		return maxId;
	}
	
	/**
	 * 获取医院提前预约天数
	 * @param hospitalId
	 * @return
	 */
	public static Integer getPreviousBookDaysByHospitalId(Integer hospitalId){
		Integer day = null;
		Map<String, Object> settings = getHospitalSetting(hospitalId, "previous_book_days","previous_book_time");
		day = Integer.valueOf(settings.get("previous_book_days").toString());
		String tmpHourSecond = settings.get("previous_book_time").toString();
		String[] tmps = tmpHourSecond.split(":");
		String newtmpHoursSecords = "";
		if(tmps.length < 3 )
			newtmpHoursSecords += "00:";
		for(String t : tmps){
			if(t.length() == 1)
				newtmpHoursSecords += "0"+t;
			else
				newtmpHoursSecords += t;
			newtmpHoursSecords += ":";
		}
		newtmpHoursSecords = newtmpHoursSecords.substring(0, newtmpHoursSecords.length()-1);
		String date = sdf.format(new Date())+" "+newtmpHoursSecords;
		System.out.println("当天日期..."+date);
		try {
			if(simplehms.parse(date).compareTo(new Date())<0){
				day++;
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("提前天数为..."+day);
		return day;
	}

	public static Hospital getChannelInfo(String username) {
		String sql = "select h.id from tb_user u left join tb_hospital h on h.default_manager_id=u.account_id where u.username=?";
		List<Map<String, Object>> list = null;
		try {
			list = DBMapper.query(sql, username);
			Hospital hospital = new Hospital();
			hospital.setId(Integer.parseInt(list.get(0).get("id").toString()));
			return hospital;
		} catch (SqlException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 获取渠道支持的体检中心列表
	 * @param channelId
	 * @return
	 */
	public static List<Hospital> getChannelSupportHospitals(int channelId){
		List<Hospital> retHospitalList = new ArrayList<>();
		// hospital
		String hospitalSql = "select * from tb_organization_hospital_relation where organization_id=?  and status = 1 order by hospital_id asc ";

		List<Map<String, Object>> hospitalList = null;
		try {
			hospitalList = DBMapper.query(hospitalSql, channelId);
			// 如果hospitalList什么都没有，表示支持所有体检中心；如果hospitalId=-1，表示不支持体检中心
			if (hospitalList.size() == 0) {
				// 支持所有体检中心
				hospitalSql = "SELECT * FROM tb_hospital WHERE  organization_type=? and `enable`=1 ";
				hospitalList = DBMapper.query(hospitalSql, OrganizationTypeEnum.HOSPITAL.getCode());
			} else if (hospitalList.size() == 1 && hospitalList.get(0).get("hospital_id").toString().equals("-1")) {
				return  retHospitalList;
				// 不支持体检中心
			} else {//支持部分体检中心，必须在平台显示且可用
				// （以下为本渠道商支持的体检中心列表，若体检中心不可用或不在平台显示，则渠道同样不支持）
				List<Map<String, Object>> resultList = new ArrayList<>();
				for (int i = 0; i < hospitalList.size(); i++) {
					String hospitalsql = "select * from tb_hospital where id=? and enable = 1 ";
					List<Map<String, Object>> list = DBMapper.query(hospitalsql,
							hospitalList.get(i).get("hospital_id"));
					if(list.size()>0)
						resultList.add(list.get(0));
				}
				hospitalList = resultList;
			}
		} catch (SqlException e) {
			e.printStackTrace();
		}
		if(hospitalList != null && hospitalList.size()>0){
			for(Map<String,Object> m : hospitalList){
				Hospital hospital = new Hospital();
				if(m.get("name") != null)
					hospital.setName(m.get("name").toString());
				hospital.setId(Integer.parseInt(m.get("id").toString()));
				hospital.setPhone(m.get("phone").toString());
				if(m.get("exam_notice")!=null)
					hospital.setExamNotice(m.get("exam_notice").toString());
				if (m.get("keywords") != null)
					hospital.setKeywords(m.get("keywords").toString());
				hospital.setType(m.get("type").toString());
				if(m.get("longitude")!=null)
					hospital.setLongitude(m.get("longitude").toString());
				if(m.get("latitude")!=null)
					hospital.setLatitude(m.get("latitude").toString());
				if(m.get("default_manager_id")!=null)
					hospital.setDefaultManagerId(Integer.parseInt(m.get("default_manager_id").toString()));
				Address address = new Address();
				address.setAddress(m.get("address").toString());
				hospital.setOrganizationType(Integer.parseInt(m.get("organization_type").toString()));
				hospital.setAddress(address);
				retHospitalList.add(hospital);

			}
		}
			return  retHospitalList;

	}
}
