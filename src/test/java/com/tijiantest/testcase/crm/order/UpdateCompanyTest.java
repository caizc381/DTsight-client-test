package com.tijiantest.testcase.crm.order;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.jayway.jsonpath.JsonPath;
import com.tijiantest.base.Flag;
import com.tijiantest.base.HttpResult;
import com.tijiantest.base.MyHttpClient;
import com.tijiantest.base.dbcheck.CompanyChecker;
import com.tijiantest.base.dbcheck.HospitalChecker;
import com.tijiantest.model.company.CompanyManagerVo;
import com.tijiantest.model.company.ExamCompanyVo;
import com.tijiantest.model.company.HospitalCompany;
import com.tijiantest.model.hospital.Hospital;
import com.tijiantest.testcase.crm.CrmBase;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * 更新单位信息（同步更新订单信息）
 */
public class UpdateCompanyTest extends CrmBase {
  @Test(description="深对接体检中心单位页面-更新单位信息（同步订单信息）",groups={"qa","company"})
  public void test_updateCompany() {
	  System.out.println("----------------------更新散客现场单位Start---------------------");

	  HospitalCompany hc = CompanyChecker.getHospitalCompanyByPlatCompanyIdANDOrganizationId(2,defDeepHosptailId);
	  int newCompanyid = hc.getId();
	  List<CompanyManagerVo> companyManagerVoList;

	  MyHttpClient client = new MyHttpClient();
	  onceLoginInSystem(client,Flag.CRM,defDeepUsername,defDeepPaswd);
	  //获取单位信息，并获取该医院的客户经理
	  List<NameValuePair> param = new ArrayList<>();
	  param.add(new BasicNameValuePair("companyId", newCompanyid+""));
	  HttpResult result = client.get(Comp_CompanyInfo, param);
	  String body = result.getBody();
	  Assert.assertEquals(result.getCode(), HttpStatus.SC_OK,"错误提示："+result.getBody());
	  
	  companyManagerVoList = JSON.parseObject(JsonPath.read(body, "$.manager").toString(),
				new TypeReference<List<CompanyManagerVo>>() {
				});
	  
	  ExamCompanyVo ecVo = new ExamCompanyVo();
	  ecVo.setCompanyManagerRelList(companyManagerVoList.subList(0, 1));
	  ecVo.setHospitalCompany(hc);
	  ecVo.setHospitalId(defDeepHosptailId);

	  String jsonStr = JSON.toJSONString(ecVo);
	  result = client.post(Flag.CRM,Comp_UpdateCompany, jsonStr);
	  Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);
	  Assert.assertEquals(result.getBody(), "{}");

	  onceLogOutSystem(client,Flag.CRM);
	  System.out.println("----------------------更新散客现场单位End---------------------");
  }
}
