package com.tijiantest.base.dbcheck;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.BeanUtils;
import org.testng.Assert;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.tijiantest.base.BaseTest;
import com.tijiantest.base.HttpResult;
import com.tijiantest.base.MyHttpClient;
import com.tijiantest.model.card.Card;
import com.tijiantest.model.company.BaseCompany;
import com.tijiantest.model.company.ChannelCompany;
import com.tijiantest.model.company.ChannelGuestCompanyEnum;
import com.tijiantest.model.company.CompanyHisRelationDto;
import com.tijiantest.model.company.HospitalCompany;
import com.tijiantest.model.company.HospitalCompanyVO;
import com.tijiantest.model.company.HospitalGuestCompanyEnum;
import com.tijiantest.model.company.ManagerExamCompanyRelation;
import com.tijiantest.model.company.PlatformCompHospitalApply;
import com.tijiantest.model.company.PlatformCompany;
import com.tijiantest.model.company.PlatformCompanyApplyLogDO;
import com.tijiantest.model.hospital.Hospital;
import com.tijiantest.model.organization.OrganizationTypeEnum;
import com.tijiantest.util.db.DBMapper;
import com.tijiantest.util.db.SqlException;

/**
 * 单位校验
 * @author huifang
 *
 */
public class CompanyChecker extends BaseTest{
	/**
	 * 根据老的单位id获取新的体检中心单位id
	 * 
	 * @param oldCompanyId
	 * @return
	 * @throws SqlException
	 */
	public static int getNewCompanyId(int oldCompanyId) throws SqlException {
		int newCompanyId = 0;
		String sql = "SELECT * from tb_hospital_company WHERE tb_exam_company_id=?";
		try {
			List<Map<String, Object>> list = DBMapper.query(sql, oldCompanyId);
			newCompanyId = Integer.parseInt(list.get(0).get("id").toString());
		} catch (SqlException e) {
			log.error("查询基础数据库抛错:" + e.getMessage());
			e.printStackTrace();
		}
		return newCompanyId;
	}

	/**
	 * 根据平台单位ID，获取支持的体检中心
	 * @param companyId
	 */
	public static List<Hospital> getHospitalByCompanyId(MyHttpClient httpClient,int companyId) {
		List<NameValuePair> params = new ArrayList<>();
		params.add(new BasicNameValuePair("companyId", companyId+""));
		HttpResult result = httpClient.get(Hos_Hospital, params);

		Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);
		List<Hospital> hospitalList = JSON.parseArray(result.getBody(), Hospital.class);
		return hospitalList;
	}
	
	

	/**
	 * @param hospitalCompanyId
	 * @return
	 */
	public static HospitalCompany getHospitalCompanyById(Integer hospitalCompanyId){
		HospitalCompany hospitalCompany = new HospitalCompany();
		String sql = "SELECT * FROM tb_hospital_company WHERE id = "+hospitalCompanyId+" ";
		List<Map<String, Object>> list = null;
		try {
			list = DBMapper.query(sql);
		} catch (SqlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(list.size()>0&&list!=null){
			hospitalCompany = list2HospitalCompany(list).get(0);
			hospitalCompany.setName(list.get(0).get("name").toString());

		}
		return hospitalCompany;
	}
	
	/**
	 * @param name
	 * @return
	 */
	public static List<HospitalCompany> getHospitalCompanyByName(String name){
		List<HospitalCompany> hCompanys = new ArrayList<HospitalCompany>();
		String sql = "SELECT * FROM tb_hospital_company WHERE name = \'"+name+"\' ";
		List<Map<String, Object>> list = null;
		try {
			list = DBMapper.query(sql);
		} catch (SqlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(list.size()>0&&list!=null){
			hCompanys = list2HospitalCompany(list);
		}
		return hCompanys;
	}
	
	public static List<HospitalCompany> list2HospitalCompany(List<Map<String, Object>> list){
		List<HospitalCompany> hCompanys = new ArrayList<>();
		for(Map<String,Object> m:list){
			HospitalCompany hCompany = new HospitalCompany();
			BaseCompany baseCompany = new BaseCompany();
			baseCompany = getBaseCompany(m);
			BeanUtils.copyProperties(baseCompany, hCompany);
			hCompany.setShowReport(m.get("show_report").equals(1)?true:false);
			hCompany.setEmployeeImport(m.get("employee_import").equals(1)?true:false);
			if(m.get("his_name")!=null)
				hCompany.setHisName(m.get("his_name").toString());
			if(m.get("advance_export_order")!=null)
				hCompany.setAdvanceExportOrder(m.get("advance_export_order").equals(1)?true:false);
			if(m.get("employee_prefix")!=null)
				hCompany.setEmployeePrefix(m.get("employee_prefix").toString());
			if(m.get("examination_address")!=null)
				hCompany.setExaminationAddress(m.get("examination_address").toString());
			hCompany.setExamreportIntervalTime(Integer.valueOf(m.get("examreport_interval_time").toString()));
			hCompanys.add(hCompany);
		}
		return hCompanys;
	}
	
	/**
	 * Map->BaseCompany
	 * BaseCompany包含单位基础信息
	 * @param m
	 * @return
	 */
	public static BaseCompany getBaseCompany(Map<String,Object> m){
		BaseCompany baseCompany = new BaseCompany();
		baseCompany.setId(Integer.valueOf(m.get("id").toString()));
		baseCompany.setName(m.get("name").toString());
		if(m.get("platform_company_id")!=null)
			baseCompany.setPlatformCompanyId(Integer.valueOf(m.get("platform_company_id").toString()));
		baseCompany.setOrganizationId(Integer.valueOf(m.get("organization_id").toString()));
		baseCompany.setDiscount(Double.valueOf(m.get("discount").toString()));
		baseCompany.setSettlementMode(Integer.valueOf(m.get("settlement_mode").toString()));
		baseCompany.setSendExamSms(m.get("send_exam_sms").equals(1)?true:false);
		baseCompany.setSendExamSmsDays(Integer.valueOf(m.get("send_exam_sms_days").toString()));
		baseCompany.setPinyin(m.get("pinyin").toString());
		baseCompany.setDeleted(m.get("is_deleted").equals(1)?true:false);
		if(m.get("tb_exam_company_id")!=null)
			baseCompany.setTbExamCompanyId(Integer.valueOf(m.get("tb_exam_company_id").toString()));
		baseCompany.setOrganizationName(m.get("organization_name").toString());
		
		return baseCompany;
	}
	
	/**
	 * 根据company_id获取channelCompany
	 * 
	 * @param newCompanyId
	 * @return
	 * @throws SqlException
	 */
	public static ChannelCompany getChannelCompanyByCompanyId(int newCompanyId,int organizationId) {
		ChannelCompany channelCompany = new ChannelCompany();
		String sql = "select * from tb_channel_company where id=? and organization_id=? and is_deleted = 0";
		List<Map<String, Object>> list = null;
		try {
			list = DBMapper.query(sql, newCompanyId,organizationId);
		} catch (SqlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (list.size() > 0 && list != null) {
			Map<String, Object> m = list.get(0);
			BaseCompany baseCompany = new BaseCompany();
			baseCompany = getBaseCompany(m);
			BeanUtils.copyProperties(baseCompany, channelCompany);
		}
		return channelCompany;
	}

	/**
	 * 根据company_id获取channelCompanyList
	 *
	 * @param organizationId
	 * @return
	 * @throws SqlException
	 */
	public static List<ChannelCompany> getChannelCompanyByChannelId(int organizationId) {
		List<ChannelCompany> channelCompanyList = new ArrayList<ChannelCompany>();
		String sql = "select * from tb_channel_company where organization_id=? and is_deleted = 0";
		List<Map<String, Object>> list = null;
		try {
			list = DBMapper.query(sql,organizationId);
		} catch (SqlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (list.size() > 0 && list != null) {
			for(Map<String,Object> m : list){
				ChannelCompany c = new ChannelCompany();
				BaseCompany baseCompany = new BaseCompany();
				baseCompany = getBaseCompany(m);
				BeanUtils.copyProperties(baseCompany, c);
				channelCompanyList.add(c);
			}

		}
		return channelCompanyList;
	}

	/**
	 * 根据platformCompanyId获取channelCompany
	 * @param platformCompanyId
	 * @return
	 */
	public static List<ChannelCompany> getChannelCompany(Integer platformCompanyId){
		List<ChannelCompany> cCompanyList = new ArrayList<ChannelCompany>();
		String sql = "SELECT * from tb_channel_company WHERE is_deleted =0 AND platform_company_id =? ORDER BY organization_id ASC;";
		List<Map<String, Object>> list = null;
		try {
			list = DBMapper.query(sql,platformCompanyId);
		} catch (SqlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (list.size() > 0 && list != null) {
			for(Map<String,Object> m : list){
				ChannelCompany cCompany = new ChannelCompany();
				BaseCompany baseCompany = new BaseCompany();
				baseCompany = getBaseCompany(m);
				BeanUtils.copyProperties(baseCompany, cCompany);
				if(m.get("description")!=null)
					cCompany.setDescription(m.get("description").toString());
				cCompanyList.add(cCompany);
			}
		}
		return cCompanyList;
	}


	/**
	 * 根据company_id获取channelCompany
	 *
	 * @param newCompanyId
	 * @return
	 * @throws SqlException
	 */
	public static ChannelCompany getChannelCompanyByCompanyId(int newCompanyId) {
		ChannelCompany channelCompany = new ChannelCompany();
		String sql = "select * from tb_channel_company where id=?";
		List<Map<String, Object>> list = null;
		try {
			list = DBMapper.query(sql, newCompanyId);
		} catch (SqlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (list.size() > 0 && list != null) {
			Map<String, Object> m = list.get(0);
			BaseCompany baseCompany = new BaseCompany();
			baseCompany = getBaseCompany(m);
			BeanUtils.copyProperties(baseCompany, channelCompany);
		}
		return channelCompany;
	}

	/**
	 * 通过company_id和organization_id查询new_company_id
	 * @param platCompanyId
	 * @param organinzationId
	 * @return
	 */
	public static HospitalCompany getHospitalCompanyByPlatCompanyIdANDOrganizationId(Integer platCompanyId,Integer organinzationId){
		HospitalCompany hospitalCompany = new HospitalCompany();
		String sql = "SELECT * FROM tb_hospital_company WHERE platform_company_id = "+platCompanyId+" AND organization_id = "+organinzationId+"";
		List<Map<String, Object>> list = null;
		try {
			list = DBMapper.query(sql);
		} catch (SqlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(list.size()>0&&list!=null){
			Map<String,Object> m = list.get(0);
			BaseCompany baseCompany = new BaseCompany();
			baseCompany = getBaseCompany(m);
			BeanUtils.copyProperties(baseCompany, hospitalCompany);
			hospitalCompany.setShowReport(m.get("show_report").equals(1)?true:false);
			hospitalCompany.setEmployeeImport(m.get("employee_import").equals(1)?true:false);
			if(m.get("his_name")!=null)
				hospitalCompany.setHisName(m.get("his_name").toString());
			hospitalCompany.setAdvanceExportOrder(m.get("advance_export_order").equals(1)?true:false);
			if(m.get("employee_prefix")!=null)
				hospitalCompany.setEmployeePrefix(m.get("employee_prefix").toString());
			if(m.get("examination_address")!=null)
				hospitalCompany.setExaminationAddress(m.get("examination_address").toString());
			hospitalCompany.setExamreportIntervalTime(Integer.valueOf(m.get("examreport_interval_time").toString()));
			hospitalCompany.setName(m.get("name").toString());
		}
		return hospitalCompany;
	}
	
	
	/**
	 * 根据机构id随机找到体检中心普通单位
	 * @param organinzationId
	 * @return
	 */
	public static HospitalCompany getRandomCommonHospitalCompany(Integer organinzationId){
		HospitalCompany hospitalCompany = new HospitalCompany();
		String sql = "SELECT * FROM tb_hospital_company WHERE platform_company_id is null AND IS_DELETED = 0 AND organization_id = "+organinzationId+"";
		List<Map<String, Object>> list = null;
		try {
			list = DBMapper.query(sql);
		} catch (SqlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(list.size()>0&&list!=null){
			Map<String,Object> m = list.get(0);
			BaseCompany baseCompany = new BaseCompany();
			baseCompany = getBaseCompany(m);
			BeanUtils.copyProperties(baseCompany, hospitalCompany);
			hospitalCompany.setShowReport(m.get("show_report").equals(1)?true:false);
			hospitalCompany.setEmployeeImport(m.get("employee_import").equals(1)?true:false);
			if(m.get("his_name")!=null)
				hospitalCompany.setHisName(m.get("his_name").toString());
			hospitalCompany.setAdvanceExportOrder(m.get("advance_export_order").equals(1)?true:false);
			if(m.get("employee_prefix")!=null)
				hospitalCompany.setEmployeePrefix(m.get("employee_prefix").toString());
			if(m.get("examination_address")!=null)
				hospitalCompany.setExaminationAddress(m.get("examination_address").toString());
			hospitalCompany.setExamreportIntervalTime(Integer.valueOf(m.get("examreport_interval_time").toString()));
			hospitalCompany.setName(m.get("name").toString());
		}
		return hospitalCompany;
	}

	/**
	 * 根据机构id随机找到体检中心普通单位，且单位有卡
	 * @param organinzationId
	 * @return
	 */
	public static HospitalCompany getRandomHaveCardCommonHospitalCompany(Integer organinzationId,int managerId){
		HospitalCompany hospitalCompany = new HospitalCompany();
		String sql = "SELECT * FROM tb_hospital_company WHERE platform_company_id is null AND IS_DELETED = 0 AND organization_id = "+organinzationId+" " +
				"AND id in (SELECT new_company_id FROM tb_card WHERE from_hospital = "+organinzationId+" AND manager_id = "+managerId+" AND batch_id is not null)";
		List<Map<String, Object>> list = null;
		try {
			list = DBMapper.query(sql);
		} catch (SqlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(list.size()>0&&list!=null){
			Map<String,Object> m = list.get(0);
			BaseCompany baseCompany = new BaseCompany();
			baseCompany = getBaseCompany(m);
			BeanUtils.copyProperties(baseCompany, hospitalCompany);
			hospitalCompany.setShowReport(m.get("show_report").equals(1)?true:false);
			hospitalCompany.setEmployeeImport(m.get("employee_import").equals(1)?true:false);
			if(m.get("his_name")!=null)
				hospitalCompany.setHisName(m.get("his_name").toString());
			hospitalCompany.setAdvanceExportOrder(m.get("advance_export_order").equals(1)?true:false);
			if(m.get("employee_prefix")!=null)
				hospitalCompany.setEmployeePrefix(m.get("employee_prefix").toString());
			if(m.get("examination_address")!=null)
				hospitalCompany.setExaminationAddress(m.get("examination_address").toString());
			hospitalCompany.setExamreportIntervalTime(Integer.valueOf(m.get("examreport_interval_time").toString()));
			hospitalCompany.setName(m.get("name").toString());
		}
		return hospitalCompany;
	}
	/**
	 * 通过company_id和organization_id查询new_company_id
	 * @param oldCompanyId
	 * @param organinzationId
	 * @return
	 */
	public static List<HospitalCompany> getHospitalCompanyByPlatCompanyId(Integer platCompanyId){
		List<HospitalCompany> companys = new ArrayList<>();
		
		String sql = "SELECT * FROM tb_hospital_company WHERE platform_company_id = "+platCompanyId+" and is_deleted=0";
		List<Map<String, Object>> list = null;
		try {
			list = DBMapper.query(sql);
		} catch (SqlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(list.size()>0&&list!=null){
			companys.addAll(list2HospitalCompany(list));
		}
		return companys;
	}
	
	/**
	 * 根据客户经理ID，获取支持的单位
	 * 
	 * @param manageId
	 * @throws SqlException
	 */
	public static List<Integer> getCompanysIdByPlatManageId(int manageId) throws SqlException {
		String sql = "SELECT DISTINCT cc.* "
				+ "FROM tb_channel_company cc "
				+ "LEFT JOIN tb_manager_company_relation mc ON mc.new_company_id = cc.id "
				+ "LEFT JOIN tb_hospital_company hc ON hc.platform_company_id = cc.platform_company_id "
				+ "LEFT JOIN tb_manager_channel_relation mcr ON mcr.manager_id = mc.manager_id "
				+ "LEFT JOIN tb_organization_hospital_relation oh ON oh.organization_id = mcr.channel_id "
				+ "WHERE mc.manager_id = ? "
				+ "AND mc. STATUS = 1 "
				+ "AND cc.is_deleted = 0 "
				+ "AND mcr.channel_id = cc.organization_id "
				+ "AND oh.hospital_id IS NOT NULL;";
		System.err.println(sql);
		List<Map<String, Object>> companies = DBMapper.query(sql, manageId);

		// 去重后的compnay_id
		List<Integer> tempList = new ArrayList<>();
		for (int i = 0; i < companies.size(); i++) {
			Integer companyId = Integer.valueOf(companies.get(i).get("id").toString());
			if (!tempList.contains(companyId)) {
				tempList.add(companyId);
			}
		}

		return tempList;
	}
	
	/**
	 * 通过company_id和organization_id查询new_company_id
	 * @param oldCompanyId
	 * @param organinzationId
	 * @return
	 */
	public static HospitalCompany getHCompanyByOldCompanyIdANDOrgId(Integer oldCompanyId,Integer organinzationId){
		HospitalCompany hospitalCompany = new HospitalCompany();
		if(oldCompanyId==1585)
			oldCompanyId=-101;
		String sql = "SELECT * FROM tb_hospital_company WHERE tb_exam_company_id = "+oldCompanyId+" AND organization_id = "+organinzationId+"";
		List<Map<String, Object>> list = null;
		try {
			list = DBMapper.query(sql);
		} catch (SqlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(list.size()>0&&list!=null){
			Map<String,Object> m = list.get(0);
			BaseCompany baseCompany = new BaseCompany();
			baseCompany = getBaseCompany(m);
			BeanUtils.copyProperties(baseCompany, hospitalCompany);
			hospitalCompany.setShowReport(m.get("show_report").equals(1)?true:false);
			hospitalCompany.setEmployeeImport(m.get("employee_import").equals(1)?true:false);
			if(m.get("his_name")!=null)
				hospitalCompany.setHisName(m.get("his_name").toString());
			hospitalCompany.setAdvanceExportOrder(m.get("advance_export_order").equals(1)?true:false);
			if(m.get("employee_prefix")!=null)
				hospitalCompany.setEmployeePrefix(m.get("employee_prefix").toString());
			if(m.get("examination_address")!=null)
				hospitalCompany.setExaminationAddress(m.get("examination_address").toString());
			hospitalCompany.setExamreportIntervalTime(Integer.valueOf(m.get("examreport_interval_time").toString()));
			hospitalCompany.setName(m.get("name").toString());
		}
		return hospitalCompany;
	}

	
	/**
	 * 根据客户经理ID，获取支持的单位
	 * 
	 * @param managerId
	 * @param isPlatformManager 
	 * 			true- 平台客户经理
	 * 			false - 普通客户经理
	 * @throws SqlException
	 */
	public static List<Integer> getCompanysIdByManagerId(int managerId,boolean isPlatformManager) throws SqlException {
		List<Integer> idList = new ArrayList<>();
		String sql = "";
		List<Map<String, Object>> list = null;
		if(!isPlatformManager)
			sql = "SELECT DISTINCT company.* FROM tb_manager_company_relation mc "
					+ "LEFT JOIN tb_hospital_company company on company.id = mc.new_company_id "
					+ "WHERE mc.manager_id = ? AND mc.status=1 AND company.is_deleted=0 order by company.id desc;";
		else
			sql = "SELECT DISTINCT company.* FROM tb_manager_company_relation mc "
					+ "LEFT JOIN tb_channel_company company on company.id = mc.new_company_id "
					+ "WHERE mc.manager_id = ? AND mc.status=1 AND company.is_deleted=0 order by company.id ASC;";
		list = DBMapper.query(sql, managerId);
		
		for(Map<String,Object> m : list){
			BaseCompany baseCompany = new BaseCompany();
			baseCompany = getBaseCompany(m);
			if(!isPlatformManager){
				if(baseCompany.getPlatformCompanyId()!=null&&baseCompany.getPlatformCompanyId()==2)//如果是现场散客排在第一位
					idList.add(0, baseCompany.getId());
				else
					idList.add(baseCompany.getId());
			} 
			else{
				//获取客户经理所属渠道商
				String hospitalSql = "SELECT oh.* FROM tb_organization_hospital_relation oh "
						+ "LEFT JOIN tb_manager_channel_relation mc on mc.channel_id = oh.organization_id "
						+ "WHERE mc.manager_id = ?;";
				List<Map<String, Object>> list1 = DBMapper.query(hospitalSql, managerId);
				//如果客户经理支持的渠道商无支持的体检中心，则客户经理单位列表中不显示M单位
				if(list1!=null&&list1.size()>=0){
					if(baseCompany.getPlatformCompanyId()!=null&&baseCompany.getPlatformCompanyId().intValue()==3)
						idList.add(baseCompany.getId());
				}else
					System.out.println("该客户经理所属渠道无支持的体检中心");
				//如果P单位无支持的体检中心，则不显示在客户经理的单位列表
				if(baseCompany.getPlatformCompanyId()!=null&&baseCompany.getPlatformCompanyId().intValue()!=3){
					String companySql = "SELECT * FROM tb_hospital_company hc "
							+ "LEFT JOIN tb_platform_company pc on pc.id = hc.platform_company_id WHERE pc.id =?";
					List<Map<String, Object>> list2 = DBMapper.query(companySql, baseCompany.getPlatformCompanyId());
					if(list2!=null&&list2.size()>0)
						idList.add(baseCompany.getId());
				}
			}
		}
		
		return idList;

	}
	

	public static CompanyHisRelationDto getCompanyRelationByCompanyId(int hospitalId,int newCompanyId){
		CompanyHisRelationDto relation = new CompanyHisRelationDto();
		String sql = "select * from tb_crm_his_company_relation where hospital_id=? and new_company_id =? ";
		log.info("sql:"+sql);
		try {
			List<Map<String,Object>> list = null;
			//HospitalCompany comp = CompanyBaseTest.getHCompanyByOldCompanyIdANDOrgId(oldCompanyId,hospitalId);
			//int newCompanyId = comp.getId();
			list = DBMapper.query(sql, hospitalId,newCompanyId);
			if (list.size()==0) {
				log.error("没有这个单位 ，NEW单位ID:"+newCompanyId);
				return null;
			}
			if(list.size()==1){
			Map<String,Object> maps = list.get(0);
			if(maps.get("crm_company_id")!=null){
				relation.setCrmCompanyId((Integer)maps.get("crm_company_id"));
			}
			relation.setCrmCompanyName(maps.get("crm_company_name").toString());
			relation.setNewCompanyId((Integer)maps.get("new_company_id"));
			if(maps.get("his_company_code")!=null){
				relation.setHisCompanyCode(maps.get("his_company_code").toString());
			}
			if(maps.get("his_company_name")!=null){
				relation.setHisCompanyName(maps.get("his_company_name").toString());
			}
			
			relation.setId((Integer)maps.get("id"));
			relation.setIsDeleted((Integer)maps.get("is_deleted"));
			relation.setIsTar((Integer)maps.get("is_tar"));
			relation.setSyncStatus((Integer)maps.get("sync_status"));
			}
		    return relation;
		} catch (SqlException e) {
			log.error("catch exception while get companyrelation from db!", e);
			e.printStackTrace();
		}			
		return null;
	}
	
	public static List<CompanyHisRelationDto> selectSyncCompanyRelation(Integer hospitalId,int tar,String keyword,Integer discount,boolean isGuest){
		
		List<CompanyHisRelationDto> relationList = new ArrayList<CompanyHisRelationDto>();
		String sql="SELECT a.id,a.crm_company_name,a.his_company_code,a.his_company_name,a.hospital_id,a.is_deleted,a.is_tar,a.new_company_id,a.sync_status, "
				+ " b.platform_company_id,b.advance_export_order,b.discount,b.examination_address,b.name,b.pinyin,b.send_exam_sms,b.settlement_mode,b.show_report,b.gmt_created"
				+ " from tb_crm_his_company_relation a left join tb_hospital_company b on a.new_company_id=b.id"
				+ " WHERE a.new_company_id is not null and a.is_deleted=0 and a.hospital_id=? and a.is_tar= ?";
		
        
		if(keyword!=null){
			sql=sql+" and (b.name like'%"+keyword+"%' "
					+ "or a.his_company_name like'%"+keyword+"%' "
					+ "or a.new_company_id like'%"+keyword+"%' "
					+ "or a.his_company_code like'%"+keyword+"%' "
					+ "or b.pinyin like '%"+keyword+"%')";
		}
		if(discount!=null && discount ==1){
			sql=sql+" and (b.discount=1 or b.discount is null)";
		}
		if(discount!=null && discount ==0){
			sql=sql+" and b.discount<1";
		}
		if(isGuest==true){
            sql=sql+" and b.platform_company_id in(1,2,3)"
            		+ "ORDER BY b.platform_company_id";
        }
        if(isGuest==false){
        	sql=sql+" and (b.platform_company_id>3 or b.platform_company_id is null)"
        		+" ORDER BY b.id desc";
        }

		log.info("sql:"+sql);
		try {
			List<Map<String,Object>> list = null;
			list = DBMapper.query(sql, hospitalId,tar);
			for(Map<String,Object> remaps : list){
				CompanyHisRelationDto relation = new CompanyHisRelationDto();
				relation.setNewCompanyId((Integer)remaps.get("new_company_id"));
			    relation.setCrmCompanyName(remaps.get("name").toString());
			    if(remaps.get("his_company_code")!=null){
			    	relation.setHisCompanyCode(remaps.get("his_company_code").toString());
			    }
			    if(remaps.get("his_company_name")!=null){
			    	relation.setHisCompanyName(remaps.get("his_company_name").toString());
			    }
			    relation.setId((Integer)remaps.get("id"));
			    relation.setIsDeleted((Integer)remaps.get("is_deleted"));
			    relation.setIsTar((Integer)remaps.get("is_tar"));
			    relation.setSyncStatus((Integer)remaps.get("sync_status"));
			    relation.setDiscount(Double.valueOf(remaps.get("discount").toString()));
			    if(remaps.get("platform_company_id")!=null){
			    	relation.setPlatformCompanyId((Integer)remaps.get("platform_company_id"));
			    }
			    
			    relationList.add(relation);
			    
			    
			    
			 }
			
		    return relationList;
		} catch (SqlException e) {
			log.error("catch exception while get companyrelation from db!", e);
			e.printStackTrace();
		}			
		return null;
	}
	
	public static List<CompanyHisRelationDto> getSynCompanyRelationList(Integer hospitalId,int tar,String keyword,Integer discount)throws SqlException{
		List<CompanyHisRelationDto> resultList = Lists.newArrayList();
		//非散客单位
		List<CompanyHisRelationDto> list = selectSyncCompanyRelation(hospitalId,tar,keyword,discount,false);
		//散客单位
		List<CompanyHisRelationDto> guestList = selectSyncCompanyRelation(hospitalId,tar,keyword,discount,true);
		if(guestList!=null&&!guestList.isEmpty()){
			//散客单位排在前三位	
			for(int i=0;i<guestList.size();i++){
				if(guestList.get(i).getPlatformCompanyId()==1){
			    	CompanyHisRelationDto comp = guestList.get(i);
					comp.setDescription("二级站点客户体检单位");
					resultList.add(comp);
			    }
			    if(guestList.get(i).getPlatformCompanyId()==2){
			    	CompanyHisRelationDto comp = guestList.get(i);
					comp.setDescription("CRM散客体检单位");
					resultList.add(comp);
			    } 
			    if(guestList.get(i).getPlatformCompanyId()==3){
			    	CompanyHisRelationDto comp = guestList.get(i);
					comp.setDescription("平台客户体检单位");
					resultList.add(comp);}
			    }
				
		}				
		
		if(list!=null&&!list.isEmpty()){
			resultList.addAll(list);
			}
		
		return resultList;
	}
	

	public HospitalCompany getMTJKCompany(Integer hospitalId){
		HospitalCompany hospitalCompany = new HospitalCompany();
		String sql = "SELECT * FROM tb_hospital_company where organization_id = ? AND platform_company_id = 3;";
		List<Map<String,Object>> list=null;
		try {
			list = DBMapper.query(sql,hospitalId);
		} catch (SqlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(list.size()>0&&list!=null){
			Map<String,Object> m = list.get(0);
			BaseCompany baseCompany = new BaseCompany();
			baseCompany = getBaseCompany(m);
			BeanUtils.copyProperties(baseCompany, hospitalCompany);
			hospitalCompany.setShowReport(m.get("show_report").equals(1)?true:false);
			hospitalCompany.setEmployeeImport(m.get("employee_import").equals(1)?true:false);
			if(m.get("his_name")!=null)
				hospitalCompany.setHisName(m.get("his_name").toString());
			hospitalCompany.setAdvanceExportOrder(m.get("advance_export_order").equals(1)?true:false);
			if(m.get("employee_prefix")!=null)
				hospitalCompany.setEmployeePrefix(m.get("employee_prefix").toString());
			if(m.get("examination_address")!=null)
				hospitalCompany.setExaminationAddress(m.get("examination_address").toString());
			hospitalCompany.setExamreportIntervalTime(Integer.valueOf(m.get("examreport_interval_time").toString()));
		}
		return hospitalCompany;
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
	 * 普通的crm客户经理
	 * @param httpClient
	 * @param managerId
	 * @return
	 * @throws SqlException
	 */
	public static List<HospitalCompany> getHosptialCompanyByManagerId(MyHttpClient httpClient,int managerId) throws SqlException {
		HttpResult result = httpClient.get(Hos_GetHospitalCompanyByManager);
		// 单位ID		
			List<HospitalCompany> vo = JSON.parseArray(result.getBody(), HospitalCompany.class);
			return vo;
	}
	

	
	/**
	 * 根据keyword和orgId获取channelCompany
	 * @param keyword
	 * @param orgId
	 * @return
	 */
	public static List<ChannelCompany> getcCompanyByKeyWordAndOrgId(String keyword,Integer orgId){
		List<ChannelCompany> cCompanyList = new ArrayList<ChannelCompany>();
		String sql = "SELECT * from tb_channel_company WHERE organization_id =? AND platform_company_id =3 ";
		if(keyword!=null&&!keyword.equals(""))
			sql = sql+"AND (name like '%"+keyword+"%' or pinyin LIKE '%"+keyword+"%') ";
		sql = sql + "ORDER BY id desc";
		List<Map<String, Object>> list = null;
		try {
			list = DBMapper.query(sql,orgId);
		} catch (SqlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (list.size() > 0 && list != null) {
			for(Map<String,Object> m : list){
				ChannelCompany cCompany = new ChannelCompany();
				BaseCompany baseCompany = new BaseCompany();
				baseCompany = getBaseCompany(m);
				BeanUtils.copyProperties(baseCompany, cCompany);
				if(m.get("description")!=null)
					cCompany.setDescription(m.get("description").toString());
				cCompanyList.add(cCompany);
			}
		}
		return cCompanyList;
	}
	
	/**
	 * 根据platform_company_id查询platform_company
	 * @param id
	 * @return
	 */
	public static PlatformCompany getPlatformCompanyById(Integer id){
		PlatformCompany pCompany = new PlatformCompany();
		String sql = "SELECT * FROM tb_platform_company WHERE id = ?;";
		List<Map<String, Object>> list = null;
		try {
			list = DBMapper.query(sql, id);
		} catch (SqlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		pCompany.setId(Integer.valueOf(list.get(0).get("id").toString()));
		pCompany.setName(list.get(0).get("name").toString());
		Integer deleted = Integer.valueOf(list.get(0).get("is_deleted").toString());
		pCompany.setDeleted(deleted==0?false:true);
		if(list.get(0).get("description")!=null)
			pCompany.setDescription(list.get(0).get("description").toString());
		pCompany.setPinyin(list.get(0).get("pinyin").toString());
		if(list.get(0).get("init")!=null)
			pCompany.setInit(list.get(0).get("init").toString());
		pCompany.setGmtCreated((Date)list.get(0).get("gmt_created"));
		pCompany.setGmtModified((Date)list.get(0).get("gmt_modified"));
		return pCompany;
	}
	
	/**
	 * 根据keyword获取channelCompany
	 * @param platformCompanyId
	 * @return
	 */
	public static List<PlatformCompany> getPlatformCompanyByKeyword(String keyword){
		List<PlatformCompany> pCompanyList = new ArrayList<PlatformCompany>();
		String sql="SELECT * from tb_platform_company WHERE is_deleted =0 ";
		if(keyword!=null&&!keyword.equals(""))
			sql = sql+"AND (name like '%"+keyword+"%' or pinyin LIKE '%"+keyword+"%') ";
		sql = sql + "ORDER BY id desc";
		System.out.println(sql);
		List<Map<String, Object>> list = null;
		try {
			list = DBMapper.query(sql);
		} catch (SqlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (list.size() > 0 && list != null) {
			for(Map<String,Object> m : list){
				PlatformCompany pCompany = new PlatformCompany();
				pCompany.setId(Integer.valueOf(m.get("id").toString()));
				pCompany.setName(m.get("name").toString());
				Integer deleted = Integer.valueOf(m.get("is_deleted").toString());
				pCompany.setDeleted(deleted==0?false:true);
				if(m.get("description")!=null)
					pCompany.setDescription(m.get("description").toString());
				pCompany.setPinyin(m.get("pinyin").toString());
				if(m.get("init")!=null)
					pCompany.setInit(m.get("init").toString());
				pCompanyList.add(pCompany);
			}
		}
		return pCompanyList;
	}
	
	/**
	 * 获取单位关联的客户经理
	 * @param companyId
	 * @return
	 */
	public static List<ManagerExamCompanyRelation> getCompanyManagers(Integer companyId){
		List<ManagerExamCompanyRelation> companyManagerList = new ArrayList<ManagerExamCompanyRelation>();
		String sql = "SELECT DISTINCT mcr.*,a.name FROM tb_manager_company_relation mcr LEFT JOIN tb_account a on a.id = mcr.manager_id WHERE mcr.new_company_id = ?;";
		List<Map<String, Object>> list = null;
		try {
			list = DBMapper.query(sql, companyId);
		} catch (SqlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(list!=null&&!list.isEmpty())
			for(Map<String,Object> m :list){
				ManagerExamCompanyRelation mecr = new ManagerExamCompanyRelation();
				Integer asAccountCompany = Integer.valueOf(m.get("as_account_company").toString());
				mecr.setAsAccountCompany(asAccountCompany==0?false:true);
				if(m.get("contact_name")!=null)
					mecr.setContactName(m.get("contact_name").toString());
				if(m.get("contact_tel")!=null)
					mecr.setContactTel(m.get("contact_tel").toString());
				if(m.get("create_manager_id")!=null)
					mecr.setCreateManagerId(Integer.valueOf(m.get("create_manager_id").toString()));
				if(m.get("hospital_id")!=null)
					mecr.setHospitalId(Integer.valueOf(m.get("hospital_id").toString()));
				mecr.setManagerId(Integer.valueOf(m.get("manager_id").toString()));
				mecr.setNewCompanyId(Integer.valueOf(m.get("new_company_id").toString()));
				Integer status = Integer.valueOf(m.get("status").toString());
				mecr.setManagerName(m.get("name").toString());
				mecr.setStatus(status==1?true:false);
				companyManagerList.add(mecr);
			}
		return companyManagerList;
	}
	
	/**
	 * 获取平台单位申请体检中心记录
	 */
	public static List<PlatformCompHospitalApply> getHospitalWithApply(Integer companyId){
		List<PlatformCompHospitalApply> pchaList = new ArrayList<PlatformCompHospitalApply>();
		String sql = "SELECT * FROM tb_company_apply_log WHERE new_company_id = ?;";
		List<Map<String, Object>> list = null;
		try {
			list = DBMapper.query(sql, companyId);
		} catch (SqlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(list!=null&&!list.isEmpty())
			for(Map<String,Object> m : list){
				PlatformCompanyApplyLogDO pcha = new PlatformCompanyApplyLogDO();
				pcha.setHospitalId(Integer.valueOf(m.get("hospital_id").toString()));
				pcha.setStatus(Integer.valueOf(m.get("status").toString()));
				pcha.setNewCompanyId(Integer.valueOf(m.get("new_company_id").toString()));
				pcha.setSettingDetail(m.get("setting_detail").toString());
				pchaList.add(do2Model(pcha));
			}
		return pchaList;
	}
	
	public static PlatformCompHospitalApply do2Model(PlatformCompanyApplyLogDO applyDo) {
		PlatformCompHospitalApply apply = new PlatformCompHospitalApply();
		apply.setCompanyId(applyDo.getNewCompanyId());
		apply.setHospitalId(applyDo.getHospitalId());
		apply.setSettingDetail(applyDo.getSettingDetail());
		apply.setStatus(applyDo.getStatus());
		return apply;
	}
	
	/**
	 * 通过company_id和organization_id查询new_company_id
	 * 
	 * @param oldCompanyId
	 * @param organinzationId
	 * @return
	 */
	public static HospitalCompany getHospitalCompanyByCompanyIdANDOrganizationId(Integer oldCompanyId,
			Integer organinzationId) {
		HospitalCompany hospitalCompany = new HospitalCompany();
		if (oldCompanyId == 1585)
			oldCompanyId = -101;
		String sql = "SELECT * FROM tb_hospital_company WHERE tb_exam_company_id = " + oldCompanyId
				+ " AND organization_id = " + organinzationId + "";
		List<Map<String, Object>> list = null;
		try {
			list = DBMapper.query(sql);
		} catch (SqlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (list.size() > 0 && list != null) {
			Map<String, Object> m = list.get(0);
			BaseCompany baseCompany = new BaseCompany();
			baseCompany = getBaseCompany(m);
			BeanUtils.copyProperties(baseCompany, hospitalCompany);
			hospitalCompany.setShowReport(m.get("show_report").equals(1) ? true : false);
			hospitalCompany.setEmployeeImport(m.get("employee_import").equals(1) ? true : false);
			if (m.get("his_name") != null)
				hospitalCompany.setHisName(m.get("his_name").toString());
			if(m.get("advance_export_order")!=null)
				hospitalCompany.setAdvanceExportOrder(m.get("advance_export_order").equals(1) ? true : false);
			if (m.get("employee_prefix") != null)
				hospitalCompany.setEmployeePrefix(m.get("employee_prefix").toString());
			if (m.get("examination_address") != null)
				hospitalCompany.setExaminationAddress(m.get("examination_address").toString());
			hospitalCompany.setExamreportIntervalTime(Integer.valueOf(m.get("examreport_interval_time").toString()));
		}
		return hospitalCompany;
	}
	
	/**
	 * 获取体检中心的所有有效的单位列表
	 * is_deleted = 0
	 * @param sortColumn 排序字段
	 * @param isAsc 是否正序
	 * @param organinzationId
	 * @return
	 */
	public static List<HospitalCompany> getHospitalCompanyByOrganizationId(Integer organinzationId,String sortColumn ,boolean isAsc) {
		List<HospitalCompany> hospitalCompanyS = new ArrayList<HospitalCompany>();
		
		String sql = "SELECT * FROM tb_hospital_company WHERE organization_id = " + organinzationId + " and is_deleted = 0 ";
		if(sortColumn != null){
			sql += "order by "+sortColumn;
			if(isAsc)
				sql += " asc";
			else 
				sql += " desc";
			}
		List<Map<String, Object>> list = null;
		try {
			list = DBMapper.query(sql);
		} catch (SqlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (list.size() > 0 && list != null) {
			for(int i=0 ; i<list.size(); i++){
				Map<String, Object> m = list.get(i);
				BaseCompany baseCompany = new BaseCompany();
				baseCompany = getBaseCompany(m);
				HospitalCompany hc = new HospitalCompany();
				BeanUtils.copyProperties(baseCompany, hc);
				hc.setShowReport(m.get("show_report").equals(1) ? true : false);
				hc.setEmployeeImport(m.get("employee_import").equals(1) ? true : false);
				if (m.get("his_name") != null)
					hc.setHisName(m.get("his_name").toString());
				if(m.get("advance_export_order")!=null)
					hc.setAdvanceExportOrder(m.get("advance_export_order").equals(1) ? true : false);
				if (m.get("employee_prefix") != null)
					hc.setEmployeePrefix(m.get("employee_prefix").toString());
				if (m.get("examination_address") != null)
					hc.setExaminationAddress(m.get("examination_address").toString());
				hc.setExamreportIntervalTime(Integer.valueOf(m.get("examreport_interval_time").toString()));
				hc.setId(Integer.parseInt(m.get("id").toString()));
				hc.setName(m.get("name").toString());
				hc.setPinyin(m.get("pinyin").toString());
				hospitalCompanyS.add(hc);
			}
			
		}
		return hospitalCompanyS;
	}


    /**
     * 获取渠道单位下的所有有效的单位列表
     * is_deleted = 0
     * @param organinzationId
     * @param sortColumn 排序字段
     * @param isAsc 是否正序
     * @return
     */
    public static List<ChannelCompany> getChannelCompanyByOrganizationId(int organinzationId, String sortColumn , boolean isAsc) {
        List<ChannelCompany> channelCompanys = new ArrayList<ChannelCompany>();

        String sql = "SELECT * FROM tb_channel_company WHERE organization_id = " + organinzationId + " and is_deleted = 0 ";
        if(sortColumn != null){
            sql += "order by "+sortColumn;
            if(isAsc)
                sql += " asc";
            else
                sql += " desc";
        }
        List<Map<String, Object>> list = null;
        try {
            list = DBMapper.query(sql);
        } catch (SqlException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (list.size() > 0 && list != null) {
            for(int i=0 ; i<list.size(); i++){
                Map<String, Object> m = list.get(i);
                BaseCompany baseCompany = new BaseCompany();
                baseCompany = getBaseCompany(m);
                ChannelCompany hc = new ChannelCompany();
                BeanUtils.copyProperties(baseCompany, hc);
                hc.setId(Integer.parseInt(m.get("id").toString()));
                hc.setName(m.get("name").toString());
                hc.setPinyin(m.get("pinyin").toString());
                channelCompanys.add(hc);
            }

        }
        return channelCompanys;
    }

	/**
	 * 获取
	 * @param companyId
	 * @param cardId
	 * @param hospitalId
	 * @param site
	 * @return
	 */
	public static Integer getHospitalCompanyIdByCard(Integer companyId, Integer cardId, Integer hospitalId, Integer hospitalIdFromSiteUrl) {
		if (companyId == null && cardId != null) {
			// 使用卡预约
			Card card = CardChecker.getCardById(cardId);
			companyId = getCompanyIdByCard(card, hospitalId);
		} else {
			if (companyId == null) {
				companyId = getGuestCompany(hospitalIdFromSiteUrl, hospitalId);
			}
		}
		return companyId;
	}
	
	/**
	 * 获取单位对应的(医院)单位
	 * @param card
	 * @param hospitalId
	 * @return
	 */
	private static Integer getCompanyIdByCard(Card card, Integer hospitalId) {
		if (OrganizationTypeEnum.HOSPITAL.getCode().equals(card.getOrganizationType())) {
			return card.getNewCompanyId();
		} else {
			// 渠道单位转为医院单位，因为单位人数表都是医院维度的。
			ChannelCompany channelCompany = getChannelCompanyByCompanyId(card.getNewCompanyId());
			HospitalCompany hospitalCompany = getHospitalCompanyByChannelCompanyId(
					channelCompany.getId(), hospitalId);
			return hospitalCompany.getId();
		}
	}
	
	/**
	 * 根据渠道单位id获取对应的医院单位
	 * @param channelCompanyId
	 * @param hospitalId
	 * @return
	 */
	public static HospitalCompany getHospitalCompanyByChannelCompanyId(Integer channelCompanyId, Integer hospitalId) {
		ChannelCompany channelCompany =  getChannelCompanyByCompanyId(channelCompanyId);
		if(channelCompany == null){
			return null;
		}
		
		// 普通渠道商单位(platfrom_company_id = 3)/个人网上预约/现场散客转为医院的每天健康
		if (channelCompany.getPlatformCompanyId() == null
				|| channelCompany.getPlatformCompanyId().intValue() == HospitalGuestCompanyEnum.HOSPITAL_MTJK
						.getPlatformCompanyId()
				|| channelCompany.getPlatformCompanyId().intValue() == ChannelGuestCompanyEnum.CHANNEL_GUEST_ONLINE
						.getPlatformCompanyId()
				|| channelCompany.getPlatformCompanyId().intValue() == ChannelGuestCompanyEnum.CHANNEL_GUEST_OFFLINE
						.getPlatformCompanyId()) {

			return getHospitalCompanyByPlatCompanyIdANDOrganizationId(HospitalGuestCompanyEnum.HOSPITAL_MTJK.getPlatformCompanyId(), hospitalId);
		}
		// 其他
		return getHospitalCompanyByPlatCompanyIdANDOrganizationId(channelCompany.getPlatformCompanyId(), hospitalId);
	}
	
	/**
	 * 如果机构类型为医院，取医院默认散客单位单位（即个人网上预约）
	 * 如果机构类型为渠道，取医院每天健康单位
	 * @param hospitalIdFromSite 从_site获取当前站点，医院二级网站/渠道商二级网站
	 * @return
	 */
	private static Integer getGuestCompany(Integer hospitalIdFromSite, Integer hospitalId) {
		Hospital site = HospitalChecker.getHospitalById(hospitalIdFromSite);
		Integer siteType = site.getOrganizationType();
		if (siteType == OrganizationTypeEnum.HOSPITAL.getCode()) {
			return getDefaultGuestCompany(hospitalId).getId();
		} else {
			return getHospitalGuestCompany(hospitalId, HospitalGuestCompanyEnum.HOSPITAL_MTJK)
					.getId();
		}
	}
	
	public static HospitalCompany getDefaultGuestCompany(Integer orgnizationId) {
		return getHospitalGuestCompany(orgnizationId, HospitalGuestCompanyEnum.HOSPITAL_GUEST_ONLINE);
	}
	
	public static HospitalCompany getHospitalGuestCompany(Integer orgnizationId, HospitalGuestCompanyEnum guestEnum) {
		HospitalCompany company = getHospitalCompanyByPlatCompanyIdANDOrganizationId(guestEnum.getPlatformCompanyId(), orgnizationId);
		return company;
	}
	
	/**
	 * 获取医院的单位对象列表
	 * @param orgnizationId
	 * @param httpclient
	 * @return
	 */
	public static List<HospitalCompanyVO> getHospitalCompanyVoResult(Integer orgnizationId,MyHttpClient httpclient){
		List<NameValuePair> pairs = new ArrayList<>();
		pairs.add(new BasicNameValuePair("hospitalId", orgnizationId + ""));
		pairs.add(new BasicNameValuePair("hasGuestCompany", "true"));

		HttpResult result = httpclient.get(Hos_GetHospitalCompanyByHospital, pairs);
		String body = result.getBody();
		Assert.assertEquals(result.getCode(), HttpStatus.SC_OK,"错误提示："+body);
		List<HospitalCompanyVO> hospitalCompanyVOs = JSON.parseArray(body, HospitalCompanyVO.class);
		return hospitalCompanyVOs;
	
	}
	
	
	
}
