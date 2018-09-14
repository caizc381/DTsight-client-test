package com.tijiantest.base.dbcheck;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.*;

import com.alibaba.fastjson.*;
import com.tijiantest.model.account.*;
import com.tijiantest.model.organization.OrganizationTypeEnum;
import com.tijiantest.util.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.http.HttpClientConnection;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.testng.Assert;

import com.jayway.jsonpath.JsonPath;
import com.tijiantest.base.BaseTest;
import com.tijiantest.base.HttpResult;
import com.tijiantest.base.MyHttpClient;
import com.tijiantest.model.company.ChannelCompany;
import com.tijiantest.model.company.HospitalCompany;
import com.tijiantest.util.db.DBMapper;
import com.tijiantest.util.db.SqlException;

/**
 * 账号校验
 * @author huifang
 *
 */
public class AccountChecker extends BaseTest{


	public static String examinerColumns = "examin.id,examin.name,examin.id_card,examin.birthYear,examin.gender,examin.mobile," +
			"examin.manager_id,examin.new_company_id,examin.company_name,examin.customer_id,examin.email," +
			"examin.address_id,examin.address,examin.marriageStatus,examin.company_id,examin.igroup,examin.department," +
			"examin.sheet_name,examin.recent_card,examin.recent_meal,examin.employee_id,examin.position,examin.is_retire," +
			"examin.recent_order_date,examin.sequence,examin.health_level," +
			"examin.health_num,examin.organization_type,examin.social_security,examin.operator," +
			"examin.organization_id,examin.add_account_type,examin.initial_mobile,examin.is_self,examin.is_oper,examin.pinYin," +
			"examin.weight,examin.height,examin.relation_id";

	/**
	 *Table tb_account
	 */
	public static Account getAccountInfo(int id){
	  Account account = new Account();
	  String sqlStr = "select * from  tb_account where id = ?";
		
		log.debug("sql:"+sqlStr);
		try {
			List<Map<String,Object>> list = DBMapper.query(sqlStr,id);
		    Assert.assertEquals(list.size(), 1);
		    Map<String,Object> maps = list.get(0);
		    account.setId(id);
		    Object idcard = maps.get("idcard");
		    Object mobile = maps.get("mobile");
		    Object name = maps.get("name");
		    if(idcard != null)
		    	account.setIdCard(idcard.toString());
		    if(mobile != null)
		    	account.setMobile(mobile.toString());
		    if(name != null)
		    	account.setName(name.toString());
		    return account;
		}catch(SqlException e){
			log.error("catch exception while get accountinfo from db!", e);
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 *Table tb_account
	 */
	public static Account getAccountByIdCard(String idcard){
	  Account account = new Account();
	  String sqlStr = "select * from  tb_account where idcard = '"+idcard+"'";
		
		log.debug("sql:"+sqlStr);
		try {
			List<Map<String,Object>> list = DBMapper.query(sqlStr);
		    Assert.assertEquals(list.size(), 1);
		    Map<String,Object> maps = list.get(0);
		    account.setId(Integer.parseInt(maps.get("id").toString()));
		    Object mobile = maps.get("mobile");
		    Object name = maps.get("name");
		    account.setIdCard(idcard);
		    if(mobile != null)
		    	account.setMobile(mobile.toString());
		    if(name != null)
		    	account.setName(name.toString());
		    return account;
		}catch(SqlException e){
			log.error("catch exception while get accountinfo from db!", e);
			e.printStackTrace();
		}
		return null;
	}
	
	
	
	/**
	 * 获取账户的角色id
	 * @param id
	 * @return
	 */
	public static int getAccountRoleId(int id ){
		 String sqlStr = "select r.id, r.name from  tb_account_role a , tb_role  r where a.account_id = ? and a.role_id = r.id";
			
			log.debug("sql:"+sqlStr);
			try {
				List<Map<String,Object>> list = DBMapper.query(sqlStr,id);
				if(list!=null && list.size()>0){
					if(list.size() == 1)
						return Integer.parseInt(list.get(0).get("id").toString());
					if(list.size() >1){
						for(Map<String,Object> m : list){
							int mid = Integer.parseInt(m.get("id").toString());
							if(mid != RoleEnum.CRM_USER.getCode())
								return mid;
						}
					}
				}
				}catch(SqlException e){
					e.printStackTrace();
				}
			return 0;
	}
	/**
	 * 提取客户经理非散客的单位 tb_manager_company_relation
	 * 
	 * @param username
	 * @return
	 */
	public static List<Integer> getManagerCompanyId(int managerId, int hospitalId) {
		String sql = "select * from tb_manager_company_relation where manager_id = " + managerId + " and hospital_id = "
				+ hospitalId + " and company_id !=1585 ";
		log.debug("sql..." + sql);
		List<Integer> companyList = new ArrayList<Integer>();
		List<Map<String, Object>> list = null;
		try {
			list = DBMapper.query(sql);
			if (list.size() > 0) {
				for (Map<String, Object> m : list)
					companyList.add(Integer.parseInt(m.get("company_id").toString()));
			}

		} catch (SqlException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		return companyList;
	}



	/**
	 * tb_user
	 * 
	 * @param username
	 * @return
	 */
	public static User getUserInfo(String username) {
		String sql = "SELECT usr.*, acc.* FROM tb_user usr LEFT JOIN tb_account acc ON acc.id = usr.account_id WHERE usr.username =  ? ";
		User user = null;
		List<Map<String, Object>> list = null;
		try {
			list = DBMapper.query(sql, username);
			if (list.size() > 0) {
				Map<String, Object> m = list.get(0);
				user = new User(username);
				user.setAccount_id((Integer) m.get("account_id"));
				if(m.get("url")!=null)
					user.setUrl(m.get("url").toString());
				if(m.get("name")!=null)
					user.setName(m.get("name").toString());
			}

		} catch (SqlException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		return user;
	}
	
	
	/**
	 * tb_user
	 * 
	 * @param username
	 * @param systemRole
	 * @return
	 */
	public static User getUserInfo(String username,int systemRole) {
		String sql = "SELECT usr.*, acc.* FROM tb_user usr LEFT JOIN tb_account acc ON acc.id = usr.account_id WHERE usr.username =  ?  and usr.system = ?";
		User user = null;
		List<Map<String, Object>> list = null;
		try {
			list = DBMapper.query(sql, username,systemRole);
			if (list.size() > 0) {
				Map<String, Object> m = list.get(0);
				user = new User(username);
				user.setAccount_id((Integer) m.get("account_id"));
				if(m.get("url")!=null)
					user.setUrl(m.get("url").toString());
				user.setName(m.get("name").toString());
			}

		} catch (SqlException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		return user;
	}
	public static List<User> getUser(Integer accountId){
		List<User> users = new ArrayList<>();
		String sql = "SELECT * FROM tb_user WHERE account_id = ?;";
		List<Map<String, Object>> list = null;
		try {
			list = DBMapper.query(sql, accountId);
		} catch (SqlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(list.isEmpty())
			System.out.println("该用户无登录账户："+accountId);
		else{
			for(Map<String,Object> m : list){
				User user = new User();
				user.setAccount_id(Integer.valueOf(m.get("account_id").toString()));
				user.setId(Integer.valueOf(m.get("id").toString()));
				user.setUsername(m.get("username").toString());
				users.add(user);
			}
		}
		return users;
	}
	
	public static User getUserInfo(String username,Integer system) {
		String sql = "SELECT usr.*, acc.* FROM tb_user usr LEFT JOIN tb_account acc ON acc.id = usr.account_id WHERE usr.username =  ? and usr.system=? ";
		User user = null;
		List<Map<String, Object>> list = null;
		try {
			list = DBMapper.query(sql, username,system);
			if (list.size() > 0) {
				Map<String, Object> m = list.get(0);
				user = new User(username);
				user.setAccount_id((Integer) m.get("account_id"));
				user.setUrl(m.get("url").toString());
				user.setName(m.get("name").toString());
				user.setSystem(Integer.valueOf(m.get("system").toString()));
			}

		} catch (SqlException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		return user;
	}


	/**
	 * 根据医院的客户经理和单位查询单位下有卡的用户
	 * @param companyid
	 * @param managerid
	 * @return
	 */
	public static List<AccountRelationInCrm> getCompanyCardAccountRelations(int companyid,int managerid){
		List<AccountRelationInCrm> retlist = new ArrayList<AccountRelationInCrm>();

//		String sqlStr = "select  igroup,department,position  from tb_examiner where new_company_id = "+companyid+" " +
//				"and manager_id = "+managerid+" and is_delete = 0 "
//				+ "and customer_id in (select account_id  from tb_card where new_company_id = "+companyid+")"

		String sqlStr = "SELECT acr.* FROM tb_examiner acr  WHERE acr.manager_id = "+managerid
				+" AND acr.new_company_id = "+companyid
				+" AND acr.organization_type = 1 " +
				" AND acr.customer_id in (select account_id from tb_card where  manager_id = "+managerid+" and new_company_id ="+companyid+ " and batch_id is not null ) ";
//		log.info(sqlStr);
		List<Map<String, Object>> list = null;
		try {
			list = DBMapper.query(sqlStr);
		} catch (SqlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for(Map<String,Object> m : list){
			AccountRelationInCrm aric = new AccountRelationInCrm();
			aric.setCustomerId(Integer.valueOf(m.get("customer_id").toString()));
			if(m.get("recent_card")!=null&&m.get("recent_card")!="")
				aric.setRecentCard(Integer.valueOf(m.get("recent_card").toString()));
			if(m.get("recent_meal")!=null&&m.get("recent_meal")!="")
				aric.setRecentMeal(m.get("recent_meal").toString());
			aric.setRecentOrderDate((Date)m.get("recent_order_date"));
			if(m.get("department")!=null)
				aric.setDepartment(m.get("department").toString());
			if(m.get("mobile") != null)
				aric.setMobile(m.get("mobile").toString());
			retlist.add(aric);
		}

		return retlist;
	}



	/**
	 * 根据医院的客户经理和单位查询卡的职位/组/部门
	 * @param companyid
	 * @param managerid
     * @return
     */
	public static Map<String,List<String>> getCompanyHeadTables(int companyid,int managerid){
		Map<String,List<String>> maps = new HashMap<>();
		List<String> postionList = new ArrayList<String>();
		List<String> igroupList = new ArrayList<String>();
		List<String> depaortmentList = new ArrayList<String>();

//		String sqlStr = "select  igroup,department,position  from tb_examiner where new_company_id = "+companyid+" " +
//				"and manager_id = "+managerid+" and is_delete = 0 "
//				+ "and customer_id in (select account_id  from tb_card where new_company_id = "+companyid+")"
		boolean isListHaveData = true;
		String sqlStr = "SELECT acr.* FROM tb_examiner acr  WHERE acr.manager_id = "+managerid
				+" AND acr.new_company_id = "+companyid
				+" AND acr.organization_type = 1 " +
				" AND acr.customer_id in (select account_id from tb_card where  manager_id = "+managerid+" and new_company_id ="+companyid+ ")";
		log.info(sqlStr);
		List<Map<String,Object>> list = null;
		try {
			list = DBMapper.query(sqlStr);
			if(list != null && list.size() > 0)
				isListHaveData = true;
			else {
				isListHaveData = false;
				sqlStr = "SELECT acr.* FROM tb_examiner acr  WHERE acr.manager_id = "+managerid
						+" AND acr.new_company_id = "+companyid
						+" AND acr.organization_type = 1 " ;
				list = DBMapper.query(sqlStr);
			}
			for(Map  m : list){
				if(m.get("position") != null)
					if(!postionList.contains(m.get("position").toString()))
						postionList.add(m.get("position").toString());
				if(m.get("department") != null)
					if(!depaortmentList.contains(m.get("department").toString()))
						depaortmentList.add(m.get("department").toString());
				if(m.get("igroup") != null)
					if(!igroupList.contains(m.get("igroup").toString()))
						igroupList.add(m.get("igroup").toString());
			}
		} catch (SqlException e) {
			e.printStackTrace();
		}

		Collections.sort(igroupList);
		Collections.sort(depaortmentList);
		Collections.sort(postionList);
		maps.put("igroup",igroupList);
		maps.put("department",depaortmentList);
		maps.put("position",postionList);
		System.out.println(maps);
		return maps;
	}
	/**
	 * Table tb_examiner
	 */
	public static List<AccountRelationInCrm> checkAccRelation(List<Integer> accountlist, int companyid,int managerid) {
		List<AccountRelationInCrm> retlist = new ArrayList<AccountRelationInCrm>();
		String accounts = "(" + ListUtil.IntegerlistToString(accountlist) + ")";
		String sqlStr = "SELECT * "
				+ "FROM tb_examiner "
				+ "WHERE manager_id = "+managerid+" "
				+ "AND new_company_id = "+companyid+" "
				+ "AND customer_id in "+accounts+" " ;
		
		log.debug("sql:"+sqlStr);
			List<Map<String, Object>> list = null;
			try {
				list = DBMapper.query(sqlStr);
			} catch (SqlException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    for(Map<String,Object> m : list){
		    	AccountRelationInCrm aric = new AccountRelationInCrm();
		    	aric.setCustomerId(Integer.valueOf(m.get("customer_id").toString()));
		    	if(m.get("recent_card")!=null&&m.get("recent_card")!="")
		    		aric.setRecentCard(Integer.valueOf(m.get("recent_card").toString()));
		    	if(m.get("recent_meal")!=null&&m.get("recent_meal")!="")
		    		aric.setRecentMeal(m.get("recent_meal").toString());
		    	aric.setRecentOrderDate((Date)m.get("recent_order_date"));
		    	if(m.get("department")!=null)
		    		aric.setDepartment(m.get("department").toString());
		    	retlist.add(aric);
		    }
			
			return retlist;
	}
	
	/**
	 * Table tb_examiner
	 */
	public static List<Examiner> getAccRelation(AcctRelationQueryDto accQuery) {
		List<Examiner> retlist = new ArrayList<Examiner>();
		String sql = "SELECT acr.customer_id, acr.name, acr.birthYear, acr.gender, "
				+ "acr.marriagestatus, acr.mobile, acr.initial_mobile, acr.id_card, "
				+ "acr.igroup, acr.position, acr.department, acr.is_retire, acr.employee_id, acr.add_account_type,acr.organization_id,acr.organization_type "
				+ "FROM tb_examiner acr "
				+ "WHERE ";
		if(accQuery.getManagerId()!=null) {
			sql = sql + "acr.manager_id = "+accQuery.getManagerId()+" ";
		}
//		if(accQuery.getNewCompanyId()!=null) {
//			sql = sql + "AND acr.new_company_id = "+accQuery.getNewCompanyId()+" ";
//		}
		if(accQuery.getCustomerId()!=null) {
			sql = sql + "AND acr.customer_id = "+accQuery.getCustomerId()+" ";
		}
		if(accQuery.getIsDelete()!=null) {
			if(accQuery.getIsDelete())
				sql = sql + "AND acr.is_delete =  1 ";
			else
				sql = sql + "AND acr.is_delete =  0 ";

		}
		if(accQuery.isHasCarded()!=null){
			if(accQuery.isHasCarded())
				sql = sql + "AND acr.customer_id in (select account_id from tb_card) ";
		}
		if(accQuery.getSearchKey()!=null) {
			sql = sql + "AND\r\n" + 
					"				( acr.name like CONCAT('%',"+accQuery.getSearchKey()+",'%')\r\n" + 
					"				or acr.mobile like CONCAT('%',"+accQuery.getSearchKey()+",'%')\r\n" + 
					"				or acr.initial_mobile like CONCAT('%',"+accQuery.getSearchKey()+",'%')\r\n" + 
					"				or acr.id_Card like CONCAT('%',"+accQuery.getSearchKey()+",'%')\r\n" +
					"				)";
		}
		sql += " order by acr.update_time desc limit 1";
		log.info("account query:"+sql);
			List<Map<String, Object>> list = null;
			try {
				list = DBMapper.query(sql);
			} catch (SqlException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(list != null && list.size()>0)
		      for(Map<String,Object> m : list){
		    	Examiner aric = new Examiner();
		    	aric.setCustomerId(Integer.valueOf(m.get("customer_id").toString()));
		    	aric.setName(m.get("name").toString());
				if(m.get("birthYear")!=null)
		    		aric.setBirthYear(Integer.valueOf(m.get("birthYear").toString()));
				if(m.get("gender")!=null)
		    		aric.setGender(Integer.valueOf(m.get("gender").toString()));
		    	aric.setMarriageStatus(m.get("marriagestatus")!=null?Integer.valueOf(m.get("marriagestatus").toString()):null);
		    	aric.setMobile(m.get("mobile")!=null?m.get("mobile").toString():null);
		    	aric.setInitialMobile(m.get("initial_mobile")!=null?m.get("initial_mobile").toString():null);
		    	aric.setIdCard(m.get("id_card")!=null?m.get("id_card").toString():null);
		    	aric.setGroup(m.get("igroup")!=null?m.get("igroup").toString():null);
		    	aric.setPosition(m.get("position")!=null?m.get("position").toString():null);
		    	aric.setDepartment(m.get("department")!=null?m.get("department").toString():null);
		    	aric.setIsRetire(m.get("is_retire")!=null?Integer.valueOf(m.get("is_retire").toString()):null);
		    	aric.setAddAccountType(m.get("add_account_type")!=null?m.get("add_account_type").toString():null);
		    	aric.setOrganizationId(Integer.parseInt(m.get("organization_id").toString()));
		    	aric.setOrganizationType(Integer.parseInt(m.get("organization_type").toString()));
		    	retlist.add(aric);
		    }
			
			return retlist;
	}

	
	/**
	 * 导入用户
	 * 
	 * @param hc
	 * @param companyId 新单位id
	 * @param hospitalId
	 * @param groupname
	 * @param fileName
	 * @throws IOException
	 */
	public static void uploadAccount(MyHttpClient hc, int companyId, int hospitalId, String groupname, String fileName,
			AddAccountTypeEnum addAccountType,boolean isPlatCrm) throws IOException {
		File file = new File(fileName);
		Integer organizationType = 0;
		if(isPlatCrm)
			organizationType  = 2;
		else 
			organizationType = 1;
		//step1:导入用户
		Map<String,Object> params =new HashMap<String,Object>();
		params.put("companyId",companyId);
		params.put("newCompanyId", companyId);
		params.put("hospitalId",hospitalId);
		params.put("group", groupname);
		
	    HttpResult result = hc.upload(Account_PrepareForUpload,params, file);
	    
	    Assert.assertEquals(result.getCode(),HttpStatus.SC_OK,"准备用户失败:"+result.getBody());
	    String jbody = result.getBody();
	    Assert.assertNotEquals(jbody,"{}");
	    Assert.assertNotNull(JsonPath.read(jbody,"$.filePath"));
	    Assert.assertNotEquals(JsonPath.read(jbody,"$.sheetList"),"[]");
	    
	    String filePath = JsonPath.read(jbody,"$.filePath"); //文件名称
	    List<TitleMatcher> sheetlists = JSON.parseObject(JsonPath.read(jbody, "$.sheetList").toString(),new TypeReference<List<TitleMatcher>>(){});
	    List<String> sheetNames = new ArrayList<String>();
	    Map<String, Map<String, Integer>> sheetColumnMap = new HashMap<String,Map<String,Integer>>();
	    for(TitleMatcher match : sheetlists){
	    	sheetNames.add(match.getSheetName());  //sheetnames
	    	Map<String,Integer> temp = new HashMap<String,Integer>();
	    	Map<Integer,String> matchColums = match.getColumnMap();
	    	for(Integer i : matchColums.keySet()){
	    		temp.put(matchColums.get(i), i);
	    	}
	    	sheetColumnMap.put(match.getSheetName(),temp);
	    }
	    
	    UploadConfirmVo confirm = new UploadConfirmVo();
	    confirm.setCompanyId(companyId);
	    confirm.setNewCompanyId(companyId);
	    confirm.setFilePath(filePath);
	    confirm.setHospitalId(hospitalId);
	    confirm.setSheetColumnMap(sheetColumnMap);
	    confirm.setGroup(groupname);
	    confirm.setSheetNames(sheetNames);
	    confirm.setOrganizationType(organizationType);
	    confirm.setAddAccountType(addAccountType);
	    result = hc.post(Account_Confirm,JSON.toJSONString(confirm));
	    Assert.assertEquals(result.getCode(),HttpStatus.SC_OK,"确认用户信息出错:"+result.getBody());
	    Assert.assertEquals(result.getBody(),"{}");

		waitImportProcess(hc);
	}
	/**
	 * 默认是普通客户经理
	 * @param hc
	 * @param newCompanyId
	 * @param hospitalId
	 * @param groupname
	 * @param fileName
	 * @param addAccountType
	 * @throws IOException 
	 */
	public static void uploadAccount(MyHttpClient hc ,Integer newCompanyId,int hospitalId,String groupname,String fileName,AddAccountTypeEnum addAccountType) throws IOException{
		uploadAccount(hc, newCompanyId, hospitalId, groupname, fileName, addAccountType, false);
	}
	
	/**
	 * 导入用户
	 * 
	 * @param hc
	 * @param companyId//新单位id
	 * @param hospitalId
	 * @param groupname
	 * @param fileName
	 * @throws IOException
	 */
	public static void uploadAccount(MyHttpClient hc ,Integer newCompanyId,int hospitalId,String groupname,String fileName,AddAccountTypeEnum addAccountType,boolean isPlatCrm) throws IOException{
		
		File file = new File(fileName);
		Integer organizationType = 0;
		if(isPlatCrm)
			organizationType  = 2;
		else 
			organizationType = 1;
		//step1:导入用户
		log.info("fileName..."+fileName);
		Map<String,Object> params =new HashMap<String,Object>();
		params.put("companyId",newCompanyId);
		params.put("newCompanyId", newCompanyId);
		params.put("hospitalId", hospitalId);
		params.put("group", groupname);
		
	    HttpResult result = hc.upload(Account_PrepareForUpload,params, file);
	    
	    Assert.assertEquals(result.getCode(),HttpStatus.SC_OK,"准备用户失败:"+result.getBody());
	    String jbody = result.getBody();
	    log.info("prepareForUpload result.."+jbody);
	    Assert.assertNotEquals(jbody,"{}");
	    Assert.assertNotNull(JsonPath.read(jbody,"$.filePath"));
	    Assert.assertNotEquals(JsonPath.read(jbody,"$.sheetList"),"[]");
	    
	    String filePath = JsonPath.read(jbody,"$.filePath"); //文件名称
	    List<TitleMatcher> sheetlists = JSON.parseObject(JsonPath.read(jbody, "$.sheetList").toString(),new TypeReference<List<TitleMatcher>>(){});
	    List<String> sheetNames = new ArrayList<String>();
	    Map<String, Map<String, Integer>> sheetColumnMap = new HashMap<String,Map<String,Integer>>();
	    for(TitleMatcher match : sheetlists){
	    	sheetNames.add(match.getSheetName());  //sheetnames
	    	Map<String,Integer> temp = new HashMap<String,Integer>();
	    	Map<Integer,String> matchColums = match.getColumnMap();
	    	for(Integer i : matchColums.keySet()){
	    		temp.put(matchColums.get(i), i);
	    	}
	    	sheetColumnMap.put(match.getSheetName(),temp);
	    }
	    
	    UploadConfirmVo confirm = new UploadConfirmVo();
	    confirm.setCompanyId(newCompanyId);
	    confirm.setNewCompanyId(newCompanyId);
	    confirm.setFilePath(filePath);
	    confirm.setHospitalId(hospitalId);
	    confirm.setSheetColumnMap(sheetColumnMap);
	    confirm.setGroup(groupname);
	    confirm.setSheetNames(sheetNames);
	    confirm.setOrganizationType(organizationType);
	    confirm.setAddAccountType(addAccountType);
	    result = hc.post(Account_Confirm,JSON.toJSONString(confirm));
		log.info("confirm result.."+result.getBody());
		Assert.assertEquals(result.getCode(),HttpStatus.SC_OK,"确认用户信息出错:"+result.getBody());
	    Assert.assertEquals(result.getBody(),"{}");

		waitImportProcess(hc);

	}


	public static void waitImportProcess(MyHttpClient hc){
		HttpResult result = null;
		for (int i = 0; i < 10; i++) {
			result = hc.get(Account_ImportProgress);
			Assert.assertEquals(result.getCode(), HttpStatus.SC_OK, "查询导入用户进度:" + result.getBody());
			String rbody = result.getBody();
			if(rbody != null && !rbody.toString().equals("")){
				int totalnum = JsonPath.read(rbody, "$.totalNum");
				int dealnum = JsonPath.read(rbody, "$.dealNum");
				int successCount = JsonPath.read(rbody, "$.successCount");
				int failCount = JsonPath.read(rbody,"$.failCount");
				if ((dealnum == totalnum && successCount == totalnum)|| failCount > 0)
					break;
			}
			waitto(2);
		}
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
	 * 根据平台客户经理ID,获取channelId
	 * @param managerId
	 * @return
	 * @throws SqlException 
	 */
	public static Integer getOrganizationIdByPlatManagerId(int managerId) throws SqlException {
		String sql = "select * from tb_manager_channel_relation where manager_id=?";
		List<Map<String, Object>> list = DBMapper.query(sql, managerId);
		
		return Integer.valueOf(list.get(0).get("channel_id").toString());
	}

	
	/**
	 * 新建身份证导入用户的xls文件
	 * @param userCounts
	 * @param fileName
	 */
	public static  JSONArray makeUploadXls(int userCounts,String fileName){
		JSONArray ja = new JSONArray();
		List<ExcelMember> members = new ArrayList<ExcelMember>();	
		members.add(new ExcelMember("姓名","性别","身份证号码","年龄"));
		IdCardGeneric generic = new IdCardGeneric();
		for(int i=0;i<userCounts;i++){
			String idCard = generic.generateGender(0);
			String name = "浅对接回单"+getRandomHan()+getRandomHan();
			String json = "{\"idCard\":\""+idCard+"\","+"\"name\":\""+name+"\"}";
//			System.out.println(json);
			ja.add(JSONObject.parse(json));
			members.add(new ExcelMember(name,"男",idCard,"22"));
		}

		try {
			CreateSimpleExcelToDisk.createSimpleExcel(members, fileName);
			return ja;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	/**
	 * 根据客户经理ID，获取支持的单位
	 * 
	 * @param manageId
	 * @param isPlatformManager 
	 * 			true- 平台客户经理
	 * 			false - 普通客户经理
	 * @throws SqlException
	 */
	public static List<Integer> getCompanysIdByManagerId(MyHttpClient httpClient,int managerId,boolean isPlatformManager) throws SqlException {
		HttpResult result = httpClient.get(Hos_GetHospitalCompanyByManager);
		// 单位ID
				List<Integer> tempList = new ArrayList<>();
		if (isPlatformManager) {
			List<ChannelCompany> vo = JSON.parseArray(result.getBody(), ChannelCompany.class);
			for (int i = 0; i < vo.size(); i++) {
				ChannelCompany v = vo.get(i);
				tempList.add(v.getId());
			}
		}else {
			List<HospitalCompany> vo = JSON.parseArray(result.getBody(), HospitalCompany.class);
			for (int i = 0; i < vo.size(); i++) {
				HospitalCompany v = vo.get(i);
				tempList.add(v.getId());
			}
		}		
		return tempList;
	}
	
	

	/**
	 * 筛选出指定日期指定单位指定医院下面的有体检报告的订单
	 * Table tb_order
	 */
	public  static Map<String,Integer>getAccountsInfo(int hospitalid,int companyId,String startTime,String endTime){
		Map<String,Integer> retmaps = new HashMap<String,Integer>();
		int mancount = 0;
		int womancount = 0;
		int unknow = 0;
		String sqlStr = "";
		
		List<Map<String,Object>> dblist = null;
		if(openSharding){
			//tb_report_base_age表以hospital_id分片
			int index = hospitalid % 4;
			sqlStr = "select  id,  "     +            
					"account_Id as accountId, "+    
					"order_id as orderId,    "+
				    "hospital_id as hospitalId, "+      
				    "company_id as companyId,  "+      
				    "gender,            "+
				    "age,               "+
				   "exam_date as examDate,    "+     
				   "insert_time as insertTime, "+      
				   "update_time as updateTime "+
				   "from tb_report_age_base_"+index+
		           " where new_company_id= ? and  hospital_id= ? "+
		           "and exam_date between '"+startTime+"' and  '"+endTime+"'" ;
			log.debug("sql:"+sqlStr);
			try {
				dblist = DBMapper.queryExamReport(sqlStr,companyId,hospitalid);
			} catch (SqlException e) {
				log.error("查询体检报告库抛错:"+e.getMessage());
				e.printStackTrace();
			}
		}else{
			sqlStr = "select  id,  "     +            
					"account_Id as accountId, "+    
					"order_id as orderId,    "+
				    "hospital_id as hospitalId, "+      
				    "company_id as companyId,  "+      
				    "gender,            "+
				    "age,               "+
				   "exam_date as examDate,    "+     
				   "insert_time as insertTime, "+      
				   "update_time as updateTime "+
				   "from tb_report_age_base "+
		           "where new_company_id= ? and  hospital_id= ? "+
		           "and exam_date between ? and ? " ;
		
			log.debug("sql:"+sqlStr);
			try {
				dblist = DBMapper.query(sqlStr,startTime,endTime,companyId,hospitalid);
			} catch (SqlException e) {
				log.error("查询基础数据库抛错:"+e.getMessage());
				e.printStackTrace();
			}
		}
		
		retmaps.put("total",dblist.size());
	    for(Map<String,Object> amap : dblist){
	    	String gender = amap.get("gender").toString();
	    	if(gender.equals("0"))
	    		mancount++;
	    	else if(gender.equals("1"))
	    		womancount++;
	    	else 
	    		unknow++;
	    }
	    retmaps.put("man", mancount);
	    retmaps.put("woman", womancount);
	    retmaps.put("unknown", unknow);
		return retmaps;

	}
	

	/**
	 * 根据身份证，用户名等信息获取accountid
	 * 
	 * @param idCard
	 * @param username
	 * @param group
	 * @return
	 */
	public static int getAccountId(String idCard, String username, String group,int managerId,int hospitalId) {
		String sql1 = "SELECT a.id FROM tb_account a, tb_examiner e WHERE e.id_card = \'" + idCard
				+ "\' AND a.id = e.customer_id " + "AND e.igroup = \'" + group + "\' AND e.name = \'" + username
				+ "\' AND e.manager_id = ? AND organization_id = ? order by e.update_time desc ";
		log.debug("sql:" + sql1);
		List<Map<String, Object>> aclist = new ArrayList<Map<String, Object>>();
		try {
			aclist = DBMapper.query(sql1, managerId,hospitalId);
			if(aclist != null && aclist.size() > 0)
				return (Integer) aclist.get(0).get("id");
		} catch (SqlException e) {
			e.printStackTrace();
		}
		return 0;
	}


	/**
	 * 根据身份证，用户名等信息获取accountid
	 *
	 * @param idCard
	 * @param username
	 * @param group
	 * @return
	 */
	public static int getAccountId(String idCard, String username, String group,int managerId) {
		String sql1 = "SELECT a.id FROM tb_account a, tb_examiner e WHERE e.id_card = \'" + idCard
				+ "\' AND a.id = e.customer_id " + "AND e.igroup = \'" + group + "\' AND e.name = \'" + username
				+ "\' AND e.manager_id = ? order by e.update_time desc ";
		log.debug("sql:" + sql1);
		List<Map<String, Object>> aclist = new ArrayList<Map<String, Object>>();
		try {
			aclist = DBMapper.query(sql1, managerId);
			if(aclist != null && aclist.size() > 0)
				return (Integer) aclist.get(0).get("id");
		} catch (SqlException e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * 获取账号信息
	 * @param id
	 * @return
	 */
	public static Account getAccountById(int id ){
		String sql = "SELECT * FROM tb_account WHERE id = ?";
		Account account = null;
		List<Map<String, Object>> list = null;
		try {
			list = DBMapper.query(sql, id);
			if(list.size() > 0 ){
				Map<String,Object> m = list.get(0);
				account = new Account();
				if(m.get("name")!=null)
					account.setName(m.get("name").toString());
				account.setId(id);
				if(m.get("idcard") !=null )
					account.setIdCard(m.get("idcard").toString());
				account.setType(Integer.parseInt(m.get("type").toString()));
				account.setStatus(Integer.parseInt(m.get("status").toString()));
				if(m.get("mobile")!=null)
				account.setMobile(m.get("mobile").toString());
				if(m.get("employee_id")!=null )
					account.setEmployeeId(m.get("employee_id").toString());
			}
			
		} catch (SqlException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		return account;
	}

	/**
	 * 获取账户的身份信息
	 * @param customer_id
	 * @param hospitalId
	 * @return
	 */
	public static ExaminerVo getExaminerByCustomerId(int customer_id,int hospitalId ){
		String sql = "SELECT * FROM tb_examiner WHERE customer_id = "+customer_id+" and  organization_id = "+hospitalId+" order by update_time desc limit 1";
		log.info("sql...."+sql);
		ExaminerVo examiner = null;
		List<Map<String, Object>> list = null;
		try {
			list = DBMapper.query(sql);
			if(list.size() > 0 ){
				Map<String,Object> m = list.get(0);
				examiner = new ExaminerVo();
				examiner.setBirthYear(Integer.parseInt(m.get("birthYear").toString()));
				examiner.setAge(Integer.parseInt(sd.format(new Date()))- Integer.parseInt(m.get("birthYear").toString()));
				examiner.setGender(Integer.parseInt(m.get("gender").toString()));
				if(m.get("marriageStatus")!=null)
					examiner.setMarriageStatus(Integer.parseInt(m.get("marriageStatus").toString()));
				examiner.setId(Integer.parseInt(m.get("id").toString()));
				if(m.get("mobile")!=null)
					examiner.setMobile(m.get("mobile").toString());
				examiner.setIsSelf(Integer.parseInt(m.get("is_self").toString()));
				if(m.get("id_card")!=null)
					examiner.setIdCard(m.get("id_card").toString());
				examiner.setCustomerId(Integer.parseInt(m.get("customer_id").toString()));
				if(m.get("type")!=null)
					examiner.setType(Integer.parseInt(m.get("type").toString()));
				examiner.setName(m.get("name").toString());

			}

		} catch (SqlException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		return examiner;
	}


	/**
	 * 获取用户地址
	 * tb_user_address
	 * @param id
	 * @return
	 */
	public static UserAddress getUserAddress(int id){
		UserAddress ua = new UserAddress();
		  String sql = "SELECT * FROM tb_user_address WHERE id = "+id+" ";
		  List<Map<String, Object>> list = null;
		try {
			list = DBMapper.query(sql);
		} catch (SqlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		  if(list.size()>0&&list!=null){
			  Map<String,Object> m = list.get(0);
			  ua.setId(Integer.valueOf(m.get("id").toString()));
			  ua.setAccountId(Integer.valueOf(m.get("account_id").toString()));
			  ua.setAddressee(m.get("addressee").toString());
			  if(m.get("address_id")!=null)
				  ua.setAddressId(Integer.valueOf(m.get("address_id").toString()));
			  ua.setDetailedAddress(m.get("detailed_address").toString());
			  if(m.get("mobile")!=null)
				  ua.setMobile(m.get("mobile").toString());
			  if(m.get("phone_number")!=null)
				  ua.setPhoneNumber(m.get("phone_number").toString());
			  if(m.get("email")!=null)
				  ua.setEmail(m.get("email").toString());
			  if(m.get("address_alias")!=null)
				  ua.setAddressAlias(m.get("address_alias").toString());
			  if(m.get("remark")!=null)
				  ua.setRemark(m.get("remark").toString());
			
			  return ua;
		  }
		  return null;
	  }
	

	/**
	 * @param queryCondition：id,mobile,username
	 * @param value
	 * @return
	 */
	@SuppressWarnings("null")
	public static List<UserAccount> getUserAccount(String queryCondition, String value) {
		List<UserAccount> uAccs = new ArrayList<UserAccount>();
		queryCondition = queryCondition == "username" ? "usr.username" : "a." + queryCondition;
		String sqlA = "SELECT a.id, a.mobile, usr.username " + "FROM tb_account a "
				+ "LEFT JOIN tb_user usr ON usr.account_id = a.id " + "WHERE a.system = 1 " + "AND usr.system = 1 "
				+ "AND " + queryCondition + " = " + value + "; ";
		List<Map<String, Object>> list = null;
		try {
			list = DBMapper.query(sqlA);
		} catch (SqlException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if (list != null || !list.isEmpty()) {
			for (Map<String, Object> m : list) {
				UserAccount uAcc = new UserAccount();
				uAcc.setId(Integer.valueOf(m.get("id").toString()));
				if (m.get("mobile") != null)
					uAcc.setMobile(m.get("mobile").toString());
				if (m.get("username") != null)
					uAcc.setUsername(m.get("username").toString());
				uAccs.add(uAcc);
			}
		}
		return uAccs;
	}
	
	/**
	 * 获取体检中心默认客户经理
	 * @param hospitalId
	 * @return
	 */
	public static Integer getDefMangerIdOfHospital(Integer hospitalId){
		Integer defManagerId = null;
		String sql = "SELECT default_manager_id FROM tb_hospital WHERE id =?;";
		List<Map<String, Object>> list = null;
		try {
			list = DBMapper.query(sql, hospitalId);
		} catch (SqlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		defManagerId = Integer.valueOf(list.get(0).get("default_manager_id").toString());
		return defManagerId;
	}
	
	/**
	 * 根据用户id和性别查询家属
	 * @param accountId
	 * @param gender
	 * @return
	 */
	public static List<Account> getFamilyAccount(int accountId,int gender){
		List<Account> accountLists = new ArrayList<Account>();
		String sql = "select a.* from tb_examiner r, tb_account a  "
				+ " where r.manager_id = "+accountId +" and r.customer_id = a.id and r.gender = " +gender +" and is_self = 0";
		List<Map<String, Object>> list = null;
		try {	
			list = DBMapper.query(sql);
			if(list !=null && list.size() > 0){
				for(Map<String,Object> map : list ){
					Account account = new Account();
					account.setId(Integer.parseInt(map.get("id").toString()));
					account.setName(map.get("name").toString());
					accountLists.add(account);
				}
			}
			
		} catch (SqlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return accountLists;
	}
	/*
	* 根据id找出客户经理的account_id
	* */

	public static List<Account> getManagerListByHosptailId(int hospital_id){
	    List<Account> accountLists=new ArrayList<>();
		String sql="select DISTINCT c.manager_id ,a.name from tb_manager_account_company_relation c , tb_account a ,tb_account_company t ,tb_account_role role" +
				" where c.manager_id = a.id and a.id=role.account_id and a.status = 0 and c.account_company_id = t.id and  role.role_id in(5,7) and t.hospital_id = "+hospital_id +" order by c.manager_id";
		List<Map<String,Object>> list=null;
		try {
			list=DBMapper.query(sql);
			if(list!=null && list.size()>0){
			    for(Map<String,Object>map:list){
			        Account account1=new Account();
			        account1.setName(map.get("name").toString());
			        account1.setId(Integer.parseInt((String) map.get("manager_id").toString()));
			        accountLists.add(account1);
                }
            }
		} catch (SqlException e) {
            e.printStackTrace();
        }
        return accountLists;
    }




	/**
	 * 获取OPS用户
	 * @param accountId
	 * @return
	 */
	public static Account getOpsAccount(Integer accountId){
		String sql = "SELECT * FROM tb_employee WHERE id = "+accountId;
		Account account = null;
		List<Map<String, Object>> list = null;
		try {
			list = DBMapper.queryOps(sql);
			if(list.size() > 0 ){
				Map<String,Object> m = list.get(0);
				account = new Account();
				account.setName(m.get("employee_name").toString());
				account.setId(Integer.parseInt(m.get("id").toString()));
			}
			
		} catch (SqlException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		return account;
	}
	
	
	
	/**
	 * 获取OPS用户
	 * @param accountId
	 * @return
	 */
	public static Account getOpsAccount(String  username){
		String sql = "SELECT * FROM tb_employee WHERE login_name  = '"+username+"'";
		Account account = null;
		List<Map<String, Object>> list = null;
		try {
			list = DBMapper.queryOps(sql);
			if(list.size() > 0 ){
				Map<String,Object> m = list.get(0);
				account = new Account();
				account.setName(m.get("employee_name").toString());
				account.setId(Integer.parseInt(m.get("id").toString()));
			}
			
		} catch (SqlException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		return account;
	}
	
	public static List<ManagerChannelRelDO> getManagersByChannelId(Integer channelId){
		List<ManagerChannelRelDO> managerChannel = new ArrayList<>();
		String sql = "SELECT * FROM tb_manager_channel_relation WHERE channel_id = ?;";
		List<Map<String, Object>> list = null;
		try {
			list = DBMapper.query(sql, channelId);
		} catch (SqlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(list!=null&&!list.isEmpty()) {
			for(Map<String,Object> m : list) {
				ManagerChannelRelDO mcr = new ManagerChannelRelDO();
				mcr.setChannelId(channelId);
				try {
					mcr.setGmtCreated(m.get("gmt_created")!=null?simplehms.parse(m.get("gmt_created").toString()):null);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					mcr.setGmtModified(m.get("gmt_modified")!=null?simplehms.parse(m.get("gmt_modified").toString()):null);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				mcr.setId(Integer.valueOf(m.get("id").toString()));
				mcr.setManagerId(Integer.valueOf(m.get("manager_id").toString()));
				managerChannel.add(mcr);
			}
		}
		return managerChannel;
	}

	/**
	 * 根据医院ID查询该医院下的可用P参数列表
	 * @param hospitalId
	 * @return
	 */
	public static List<String> getHospitalPromotions(int hospitalId){
		List<String> retList = new ArrayList<>();
		int org_type = HospitalChecker.getHospitalById(hospitalId).getOrganizationType();
		String sql = null;
		if(org_type == OrganizationTypeEnum.HOSPITAL.getCode())
			sql = " select identity,manager_id from tb_promotion where  identity is not null and identity != '' " +
				"and manager_id in (select DISTINCT c.manager_id from tb_manager_account_company_relation c , tb_account a ,tb_account_company t "
				+"where c.manager_id = a.id and a.status = 0 and c.account_company_id = t.id and t.hospital_id = ?)";
		else if ( org_type == OrganizationTypeEnum.CHANNEL.getCode())
			sql = "select identity,manager_id from tb_promotion where  identity is not null and identity != '' and hospital_id = ?";
		List<Map<String, Object>> list = null;
		try {
			list = DBMapper.query(sql, hospitalId);
		} catch (SqlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(list != null && list.size()>0){
			for(Map<String,Object> map : list){
				String identity = map.get("identity").toString();
				retList.add(identity);
			}
		}
		return  retList;
	}


	/**
	 * 获取与这个体检中心无关的其他体检中心的p参数列表
	 * @param hospitalId
	 * @return
	 */
	public static List<String> getHospitalUnrelationPromotions(int hospitalId){
		List<String> retList = new ArrayList<>();
		String sql = " select identity,manager_id from tb_promotion where  identity is not null and identity != '' " +
				"and manager_id in (select DISTINCT c.manager_id from tb_manager_account_company_relation c , tb_account a ,tb_account_company t "
				+"where c.manager_id = a.id and a.status = 0 and c.account_company_id = t.id and t.hospital_id != ?)";
		List<Map<String, Object>> list = null;
		try {
			list = DBMapper.query(sql, hospitalId);
		} catch (SqlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(list != null && list.size()>0){
			for(Map<String,Object> map : list){
				String identity = map.get("identity").toString();
				retList.add(identity);
			}
		}
		return  retList;
	}

	/**
	 * 判断p参数是某个医院的
	 * @return
	 */
	public static boolean judgePromotionInHospital(String p,int hospitalId){
		List<String> promotions = getHospitalPromotions(hospitalId);
		if(promotions != null && promotions.size()>0){
			if(promotions.contains(p))
				return true;
			else
				return false;
		}
		return  false;
	}

	/**
	 * 根据P参数获取客户经理ID
	 * @param p,hospitalId
	 * @return
	 */
	public static int getPromotionManagerId(String p,int hospitalId){
		String sql = " select * from tb_promotion where identity = '"+p+"' and hospital_id  = "+hospitalId;
		log.info(sql);
		List<Map<String, Object>> list = null;
		try {
			list = DBMapper.query(sql);
		} catch (SqlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(list != null && list.size()>0){
			for(Map<String,Object> map : list){
				int managerId = Integer.parseInt(map.get("manager_id").toString());
				return  managerId;
			}
		}
		return  0;
	}

	/**
	 * 手动添加用户
	 * @param hc
	 * @param customer
	 * @param hospitalId
	 * @param newCompanyId
	 * @return 用户的customerId
	 */
	public static int modifyAccount(MyHttpClient hc ,FileAccountImportInfo customer,int hospitalId,int newCompanyId){
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("hospitalId", hospitalId+""));
		params.add(new BasicNameValuePair("type", ModifyAccountType.NEW+""));
		HttpResult result = hc.post(Account_ModifyAccount, params, JSON.toJSONString(customer));

		Assert.assertEquals(result.getCode(),HttpStatus.SC_OK);
		int customerId = Integer.parseInt(JSONPath.read(result.getBody(),"$.result").toString());
		return  customerId;
	}

	public static Examiner getSelfExaminerForCSide(Integer orgId, Integer accountId) {
		if (orgId == null || accountId == null) {
			return null;
		}
		List<Examiner> examiners = getExaminerInfoByInfo(orgId, accountId, null, 1, 0);
		if (CollectionUtils.isNotEmpty(examiners)) {
			return examiners.get(0);
		}
		return null;
	}


	/**
	 * 根据医院获取所有体检人，包括本人
	 * @param orgId
	 * @param relationId
	 * @return
	 */
	public static List<Examiner> getAllExaminersByRelationIdAndOrgId(Integer orgId,Integer relationId){
		List<Examiner> examiners = AccountChecker.getExaminerInfoByInfo(orgId,relationId,null,1,0);
		List<Examiner> otherExaminers = AccountChecker.getExaminerInfoByInfo(orgId,relationId,null,0,0);
		examiners.addAll(otherExaminers);
		return examiners;
	}



	/**
	 * 生成查询条件，并查询
	 * @param organizationId
	 * @param relationId
	 * @param idCard
	 * @param isSelf
	 * @param isDelete
	 * @return
	 */
	public static List<Examiner> getExaminerInfoByInfo(Integer organizationId, Integer relationId, String idCard, Integer isSelf, Integer isDelete) {
		if (relationId == null) {
			return null;
		}
		ExaminerQueryParam examinerQueryParam = new ExaminerQueryParam();
		examinerQueryParam.setOrganizationId(organizationId);
		examinerQueryParam.setIsSelf(isSelf);
		examinerQueryParam.setIsDelete(isDelete);
		examinerQueryParam.setRelationId(relationId);
		examinerQueryParam.setIdCard(idCard);
		List<Examiner> examiners = getExaminerInfoByRelationId(examinerQueryParam);
		return examiners;
	}

	/**
	 * 根据查询条件获取Examiner
	 * @param examinerQueryParam
	 * @return
	 */
	public static List<Examiner> getExaminerInfoByRelationId(ExaminerQueryParam examinerQueryParam){
		List<Examiner> examiners = new ArrayList<>();

		String sql = "select " +examinerColumns+
				" from tb_examiner examin where organization_id = "+examinerQueryParam.getOrganizationId()+" AND relation_id = "+examinerQueryParam.getRelationId()+" and is_delete = 0";
		if(examinerQueryParam.getIdCard()!=null&&examinerQueryParam.getIdCard()!="")
			sql += " and id_card = "+examinerQueryParam.getIdCard()+"";
		if(examinerQueryParam.getIsDelete()!=null)
			sql += " and is_delete = "+examinerQueryParam.getIsDelete()+"";
		if(examinerQueryParam.getIsSelf()!=null && examinerQueryParam.getIsSelf().intValue()==1)
			sql += "  and is_self = "+examinerQueryParam.getIsSelf().intValue()+" ORDER by update_time desc limit 1";
		if(examinerQueryParam.getIsSelf()!=null && examinerQueryParam.getIsSelf().intValue()==0)
			sql += "  and is_self = "+examinerQueryParam.getIsSelf().intValue()+" ORDER by update_time desc";
		if(examinerQueryParam.getCustomerId()!=null)
			sql += " and customer_id = "+examinerQueryParam.getCustomerId()+" order by update_time DESC limit 1";
		List<Map<String,Object>> list = null;
		try {
			list = DBMapper.query(sql);
		} catch (SqlException e) {
			e.printStackTrace();
		}
		if(list!=null&&list.size()>0){
			for(Map<String,Object> m : list){
				Examiner examiner = (Examiner) BeanUtil.mapToClassInstance(Examiner.class,m);
//				System.out.println("examinerJson:"+JSON.toJSONString(examiner));
				if(m.get("igroup")!=null)
					examiner.setGroup(m.get("igroup").toString());
				examiners.add(examiner);
			}
		}
		return examiners;
	}
	/**
	 * 根据医院ID返回全民营销的客户经理Id列表
	 * @param hospitalId
	 * @return
	 */
	public static List<Integer> getMarketingManagerId(int hospitalId ){
		List<Integer> managerList = new ArrayList<>();
		String sql = "select * from tb_account_customer_manager_relation where manager_id in (select id from tb_account where type = 5) and hospital_id = "+hospitalId;
		log.info(sql);
		List<Map<String, Object>> list = null;
		try {
			list = DBMapper.query(sql);
		} catch (SqlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(list != null && list.size()>0){
			for(Map<String,Object> map : list){
				int managerId = Integer.parseInt(map.get("manager_id").toString());
				managerList.add(managerId);
			}
		}
		return  managerList;
	}
}
