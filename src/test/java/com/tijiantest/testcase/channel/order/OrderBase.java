package com.tijiantest.testcase.channel.order;

import java.text.ParseException;
import java.util.*;

import com.tijiantest.model.order.OrderQueryParams;
import com.tijiantest.util.ListUtil;
import com.tijiantest.util.pagination.Page;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.testng.Assert;

import com.alibaba.fastjson.JSON;
import com.tijiantest.base.Flag;
import com.tijiantest.base.HttpResult;
import com.tijiantest.model.channel.HospitalsCompanysManagersVO;
import com.tijiantest.model.company.ChannelCompany;
import com.tijiantest.model.hospital.Hospital;
import com.tijiantest.testcase.channel.ChannelBase;
import com.tijiantest.util.IdCardValidate;
import com.tijiantest.util.db.MongoDBUtils;

public class OrderBase extends ChannelBase {

	public static int randomHospitalId = -1;
	public static int randomChannelCompanyId = -1;
//	public static OrderQueryRequestParams generateOrderQueryRequestParams(String... args) throws ParseException {
//
//		String currentPage = args[1];
//		String examEndDate = args[2];
//		String examStartDate = args[3];
//		String insertEndTime = args[4];
//		String insertStartTime = args[5];
//		String isExport = args[6];
//		int fromSite = defChannelid;
//		String nameOrIdCard = args[8];
//		String orderStatuses = args[9].replaceAll("#", ",");
//		String gender = args[10];
//		String isSelfMoneyZero = args[11];
//		String hosptialIdStr = args[12];
//		String channelCompanyStr = args[13];
//		Integer pageSize = args[14].equals("") ? null : Integer.valueOf(args[14]);
////		String rowCount = args[15];
////		String message = args[16];
//		int hospitalId = -1;
//		int channelExamCompanyId = -1;
//		if(!hosptialIdStr.equals("")){
//			hospitalId = Integer.parseInt(hosptialIdStr);}
//		if(!channelCompanyStr.equals(""))
//			channelExamCompanyId = Integer.parseInt(channelCompanyStr);
//
//
//		// 获取体检和渠道单位
//		List<NameValuePair> pairs = new ArrayList<>();
//		pairs.add(new BasicNameValuePair("channelId", defChannelid + ""));
//
//		HttpResult result = httpclient.get(Flag.CHANNEL, Order_GetHospitalsAndCompanysByOrganizationId, pairs);
//
//		HospitalsCompanysManagersVO hospitalsCompanysManagersVO = JSON.parseObject(result.getBody(),
//				HospitalsCompanysManagersVO.class);
//		List<Hospital> hospitals = hospitalsCompanysManagersVO.getHospitals();
//
//		OrderQueryRequestParams orderQueryRequestParams = new OrderQueryRequestParams();
//		orderQueryRequestParams.setCurrentPage(Integer.valueOf(currentPage));
//		if (!examEndDate.equals("") && !examStartDate.equals("")) {
////			orderQueryRequestParams.setExamEndDate(gmtFormater.format(sdf.parse(examEndDate)));
////			orderQueryRequestParams.setExamStartDate(gmtFormater.format(sdf.parse(examStartDate)));
//			orderQueryRequestParams.setExamStartDate(examStartDate);
//			orderQueryRequestParams.setExamEndDate(examEndDate);
//		}
//
//		if (!insertEndTime.equals("") && !insertStartTime.equals("")) {
////			orderQueryRequestParams.setInsertEndDate(gmtFormater.format(sdf.parse(insertEndTime)));
////			orderQueryRequestParams.setInsertStartDate(gmtFormater.format(sdf.parse(insertStartTime)));
//			orderQueryRequestParams.setInsertEndDate(insertEndTime);
//			orderQueryRequestParams.setInsertStartDate(insertStartTime);
//		}
//		Boolean export = true;
//		if (isExport.equals("")) {
//		} else if (isExport != null && isExport.equals("true")) {
//			orderQueryRequestParams.setIsExport(export);
//		} else {
//			orderQueryRequestParams.setIsExport(!export);
//		}
//		if (gender != null && !gender.equals("")) {
//			orderQueryRequestParams.setGender(Integer.valueOf(gender));
//		}
//
//		if (isSelfMoneyZero != null && !isSelfMoneyZero.equals("")) {
//			orderQueryRequestParams.setIsSelfMoneyZero(new Boolean(isSelfMoneyZero));
//		}
//
//		if (hospitalId != -1) {
//				if (getHospitals().getHospitals().size() != 0) {
//					// 随机获取一个渠道商
//					Random random = new Random();
//					int index = random.nextInt(hospitals.size()) % (hospitals.size() + 1);
//					hospitalId = hospitals.get(index).getId();
//					randomHospitalId = hospitalId;
//					orderQueryRequestParams.setHospitalIds(Arrays.asList(Integer.valueOf(hospitalId)));
//					if ( channelExamCompanyId != -1) {
//						List<ChannelCompany> channelCompanies = hospitalsCompanysManagersVO.getCompanies();
//						int index1 = random.nextInt(channelCompanies.size()) % (channelCompanies.size() + 1);
//						channelExamCompanyId = channelCompanies.get(index1).getId();
//						randomChannelCompanyId = channelExamCompanyId;
//						log.info("体检中心ID:" + hospitalId + ", 名称："+hospitals.get(index).getName()+" ----- 渠道单位ID:" + channelExamCompanyId+",名称："+channelCompanies.get(index1).getName());
//						orderQueryRequestParams.setChannelCompanyIds(Arrays.asList(Integer.valueOf(channelExamCompanyId)));
//					}
//				}else {
//					// 不支持体检中心
//					orderQueryRequestParams.setHospitalId(0);
//				}
//
//			}
//
//		if (channelExamCompanyId != -1) {
//			orderQueryRequestParams.setChannelCompanyIds(Arrays.asList(Integer.valueOf(channelExamCompanyId)));
//		}
//
//		orderQueryRequestParams.setFromSites(Arrays.asList(fromSite));
//		orderQueryRequestParams.setKeyWord(nameOrIdCard);
//		String[] orderArrays = orderStatuses.split(",");
//		orderQueryRequestParams.setOrderStatuseList(ListUtil.StringArraysToIntegerList(orderArrays));
//		if (pageSize != null) {
//			orderQueryRequestParams.setPageSize(Integer.valueOf(pageSize));
//		}
//
//		return orderQueryRequestParams;
//	}
//



	public static OrderQueryParams generateOrderQueryParams(String... args) throws ParseException {

		String currentPage = args[1];
		String examEndDate = args[2];
		String examStartDate = args[3];
		String insertEndTime = args[4];
		String insertStartTime = args[5];
		String isExport = args[6];
		int fromSite = defChannelid;
		String nameOrIdCard = args[8];
		String orderStatuses = args[9].replaceAll("#", ",");
		String gender = args[10];
		String isSelfMoneyZero = args[11];
		String hosptialIdStr = args[12];
		String channelCompanyStr = args[13];
		Integer pageSize = args[14].equals("") ? null : Integer.valueOf(args[14]);
//		String rowCount = args[15];
//		String message = args[16];
		int hospitalId = -1;
		int channelExamCompanyId = -1;
		if(!hosptialIdStr.equals("")){
			hospitalId = Integer.parseInt(hosptialIdStr);}
		if(!channelCompanyStr.equals(""))
			channelExamCompanyId = Integer.parseInt(channelCompanyStr);


		// 获取体检和渠道单位
		List<NameValuePair> pairs = new ArrayList<>();
		pairs.add(new BasicNameValuePair("channelId", defChannelid + ""));

		HttpResult result = httpclient.get(Flag.CHANNEL, Order_GetHospitalsAndCompanysByOrganizationId, pairs);

		HospitalsCompanysManagersVO hospitalsCompanysManagersVO = JSON.parseObject(result.getBody(),
				HospitalsCompanysManagersVO.class);
		List<Hospital> hospitals = hospitalsCompanysManagersVO.getHospitals();

		OrderQueryParams orderQueryRequestParams = new OrderQueryParams();
		if(pageSize != null){
			Page page = new Page();
			page.setCurrentPage(Integer.valueOf(currentPage));
			page.setPageSize(pageSize);
			page.setRowCount(0);
			page.setOffset(0);
			orderQueryRequestParams.setPage(page);
		}

		if (!examEndDate.equals("") && !examStartDate.equals("")) {
//			orderQueryRequestParams.setExamEndDate(gmtFormater.format(sdf.parse(examEndDate)));
//			orderQueryRequestParams.setExamStartDate(gmtFormater.format(sdf.parse(examStartDate)));
			orderQueryRequestParams.setExamStartDate(simplehms.parse(examStartDate));
			orderQueryRequestParams.setExamEndDate(simplehms.parse(examEndDate));
		}

		if (!insertEndTime.equals("") && !insertStartTime.equals("")) {
//			orderQueryRequestParams.setInsertEndDate(gmtFormater.format(sdf.parse(insertEndTime)));
//			orderQueryRequestParams.setInsertStartDate(gmtFormater.format(sdf.parse(insertStartTime)));
			orderQueryRequestParams.setInsertEndDate(simplehms.parse(insertEndTime));
			orderQueryRequestParams.setInsertStartDate(simplehms.parse(insertStartTime));
		}
		Boolean export = true;
		if (isExport.equals("")) {
		} else if (isExport != null && isExport.equals("true")) {
			orderQueryRequestParams.setIsExport(export);
		} else {
			orderQueryRequestParams.setIsExport(!export);
		}
		if (gender != null && !gender.equals("")) {
			orderQueryRequestParams.setGender(Integer.valueOf(gender));
		}

		if (isSelfMoneyZero != null && !isSelfMoneyZero.equals("")) {
			orderQueryRequestParams.setIsSelfMoneyZero(new Boolean(isSelfMoneyZero));
		}

		if (hospitalId != -1) {
			if (getHospitals().getHospitals().size() != 0) {
				// 随机获取一个渠道商
				Random random = new Random();
				int index = random.nextInt(hospitals.size()) % (hospitals.size() + 1);
				hospitalId = hospitals.get(index).getId();
				randomHospitalId = hospitalId;
				orderQueryRequestParams.setHospitalIds(Arrays.asList(Integer.valueOf(hospitalId)));
				if ( channelExamCompanyId != -1) {
					List<ChannelCompany> channelCompanies = hospitalsCompanysManagersVO.getCompanies();
					int index1 = random.nextInt(channelCompanies.size()) % (channelCompanies.size() + 1);
					channelExamCompanyId = channelCompanies.get(index1).getId();
					randomChannelCompanyId = channelExamCompanyId;
					log.info("体检中心ID:" + hospitalId + ", 名称："+hospitals.get(index).getName()+" ----- 渠道单位ID:" + channelExamCompanyId+",名称："+channelCompanies.get(index1).getName());
					orderQueryRequestParams.setChannelCompanyIds(Arrays.asList(Integer.valueOf(channelExamCompanyId)));
				}
			}else {
				// 不支持体检中心
				orderQueryRequestParams.setHospitalId(0);
			}

		}

		if (channelExamCompanyId != -1) {
			orderQueryRequestParams.setChannelCompanyIds(Arrays.asList(Integer.valueOf(channelExamCompanyId)));
		}

		orderQueryRequestParams.setFromSites(Arrays.asList(fromSite));
		if(nameOrIdCard != null && !nameOrIdCard.equals(""))
			orderQueryRequestParams.setKeyWord(nameOrIdCard);
		String[] orderArrays = orderStatuses.split(",");
		orderQueryRequestParams.setOrderStatuses(ListUtil.StringArraysToIntegerList(orderArrays));
//		orderQueryRequestParams.setSortField("insertTime");
//		orderQueryRequestParams.setSortDirection("DESC");
		return orderQueryRequestParams;
	}
	public static HospitalsCompanysManagersVO getHospitals() {
		List<NameValuePair> pairs = new ArrayList<>();
		pairs.add(new BasicNameValuePair("channelId", defChannelid + ""));

		HttpResult result = httpclient.get(Flag.CHANNEL, Order_GetHospitalsAndCompanysByOrganizationId, pairs);
		Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);
		String body = result.getBody();

		HospitalsCompanysManagersVO hospitalsCompanysManagersVO = JSON.parseObject(body,
				HospitalsCompanysManagersVO.class);
		return hospitalsCompanysManagersVO;
	}

	public static  List<Map<String, Object>> generateMongoResultList(String... args) throws ParseException {
		return generateMongoResultList(true,args);
	}

	public static List<Map<String, Object>> generateMongoResultList(boolean isNotNeedPageSize,String... args) throws ParseException {
//		String currentPage = args[1];
		String examEndDate = args[2];
		String examStartDate = args[3];
		String insertEndTime = args[4];
		String insertStartTime = args[5];
		String isExport = args[6];
		int fromSite = defChannelid;
		String nameOrIdCard = args[8];
		String orderStatuses = args[9].replaceAll("#", ",");
		String gender = args[10];
		String isSelfMoneyZero = args[11];
		String hosptialIdStr = args[12];
		String channelCompanyStr = args[13];
		Integer pageSize = args[14].equals("") ? null : Integer.valueOf(args[14]);
//		String rowCount = args[15];
		String message = args[16];
		int hospitalId = -1;
		int channelExamCompanyId = -1;
		if(!hosptialIdStr.equals("")){
			hospitalId = Integer.parseInt(hosptialIdStr);}
		if(!channelCompanyStr.equals(""))
			channelExamCompanyId = Integer.parseInt(channelCompanyStr);
		HospitalsCompanysManagersVO hospitalsCompanysManagersVO = getHospitals();

		System.out.println(
				"================================" + message + "=============================================");

		String sql = "{'fromSite':" + fromSite ;

		if (nameOrIdCard != null && !nameOrIdCard.equals("")) {
			if (IdCardValidate.isIdcard(nameOrIdCard)) {
				sql += ",'account.idCard':'" + nameOrIdCard + "'";
			} else {
				sql += ",'account.name':'" + nameOrIdCard + "'";
			}
		}


		if (gender != null && !gender.equals("")) {
			sql += ", 'accountRelation.gender':" + gender;
		}

		if (isSelfMoneyZero != null && !isSelfMoneyZero.equals("")) {
			if (isSelfMoneyZero.equals("true")) {
				// 小于等于0
				sql += ",'$or':[{'selfMoney':'0.00'},{'selfMoney':''},{'selfMoney':null}]";
			} else {
				// 大于0
				sql += ",'selfMoney':{'$ne':'0.00'}";
			}
		}

		// 获取渠道商支持的体检中心
		List<Hospital> hospitals = hospitalsCompanysManagersVO.getHospitals();

		if (hospitals.size() != 0) {
			if (hospitalId !=-1) {
				hospitalId = randomHospitalId;
				// 说明选择了某个体检中心
				sql += ",'orderHospital._id':" + hospitalId;
			} else {
				// 取所有的体检中心
				String hospitalIds = "[";
				for (int i = 0; i < hospitals.size(); i++) {
					hospitalIds += hospitals.get(i).getId() + ",";
				}
				hospitalIds = hospitalIds.substring(0, hospitalIds.lastIndexOf(","));
				hospitalIds += "]";

				sql += ",'orderHospital._id':{'$in':" + hospitalIds + "}";
			}
		} else {
			// 不支持体检中心
			sql += ", 'orderHospital._id':0";
		}

		if (channelExamCompanyId!=-1) {
			channelExamCompanyId = randomChannelCompanyId;
//			sql += ",'examCompanyId':" + channelExamCompanyId;
			sql+=", 'channelCompany._id':"+channelExamCompanyId;
		}

		System.out.println("isExport=" + isExport);
		// 是否已导出
		if(orderStatuses.contains("2") && orderStatuses.length() > 2 && isExport.equals("true")){
			String t = orderStatuses.replace(",2","").replace("2,","");
			sql += ", '$or':[{'isExport':true,'status': 2},{'status':{'$in':[" + t + "]}}]";
		}else if(isExport.equals("true"))
			sql += ", 'isExport':true" + ",'status':{'$in':[" + orderStatuses + "]}";
		else if (isExport.equals("false")) {
			sql += ", 'isExport':false" + ",'status':{'$in':[" + orderStatuses + "]}";
		}else if (isExport.equals("")) {
			sql += ",'status':{'$in':[" + orderStatuses + "]}";
		}


		sql += "}";
		System.out.println(sql);
		List<Map<String, Object>> list = new ArrayList<>();

		if (!examEndDate.equals("") && !examStartDate.equals("")) {
			if(isNotNeedPageSize)
			list = MongoDBUtils.queryByPageAndExameDate(sql, "insertTime", -1, 0, pageSize, examStartDate, examEndDate,
					MONGO_COLLECTION);
			else
				list = MongoDBUtils.queryByPageAndExameDate(sql, "insertTime", -1, 0, null, examStartDate, examEndDate,
						MONGO_COLLECTION);
		}
		if (!insertEndTime.equals("") && !insertStartTime.equals("")) {
			if(isNotNeedPageSize)
				list = MongoDBUtils.queryByPageAndInsertTime(sql, "insertTime", -1, 0, pageSize, insertStartTime,
					insertEndTime, MONGO_COLLECTION);
			else
				list = MongoDBUtils.queryByPageAndInsertTime(sql, "insertTime", -1, 0, null, insertStartTime,
						insertEndTime, MONGO_COLLECTION);
		}

		if (examEndDate.equals("") && examStartDate.equals("") && insertEndTime.equals("")
				&& insertStartTime.equals("")) {
			list = MongoDBUtils.queryByPage(sql, "insertTime", -1, 0, pageSize, MONGO_COLLECTION);
		}

		return list;
	}

}
