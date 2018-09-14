package com.tijiantest.testcase.crm.settlement.sett;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.*;

import com.tijiantest.model.settlement.*;
import com.tijiantest.util.db.DBMapper;
import com.tijiantest.util.db.SqlException;
import net.minidev.json.JSONArray;
import org.apache.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jayway.jsonpath.JsonPath;
import com.tijiantest.base.HttpResult;
import com.tijiantest.base.dbcheck.CompanyChecker;
import com.tijiantest.base.dbcheck.SettleChecker;
import com.tijiantest.model.company.HospitalCompany;
import com.tijiantest.testcase.crm.settlement.SettleBase;
import com.tijiantest.util.CvsFileUtil;
import com.tijiantest.util.DateUtils;
import com.tijiantest.util.IdCardValidate;
import com.tijiantest.util.pagination.Page;

/**
 * 单位未结算体检卡列表
 * @author huifang
 *
 */
public class UnsettlementCardListTest  extends SettleBase{

	private static  int companyid = 0;

	@Test(description="选择单位",groups={"qa"},dependsOnGroups = "crm_companySett")
//	@Test(description="选择单位",groups={"qa"})
	public void test_prepare_company_card(){
		List<CompanySettlementCount> dblist = CompanySettlePageTest.dblist;
		if(dblist!=null && dblist.size() > 0){
			int index=(int)(Math.random()*dblist.size());
			//随机取1个单位
			CompanySettlementCount settleCount = dblist.get(index);
			companyid = settleCount.getId();
			System.out.println("单位 id..."+companyid);


		}
//			companyid = 4406947;
	}
	
	@Test(description = "未结算的卡列表" ,groups={"qa"},dataProvider="unSettlementCard",dependsOnMethods="test_prepare_company_card")
	public void test_01_unSettlementCardList(String ...args) throws ParseException, SqlException {
		String sendCardStartTimeStr = args[1];
		String sendCardEndTimeStr = args[2];
		String hospitalStr = args[3];
		String filterStr = args[4];
		String settlementModeStr = args[5];
		String statusStr = args[6];
		String userNameOrIdcardStr = args[7];
		String pageStr = args[8];
		String batchSnStr = args[9];
		int hospitalId = -1;
		int  status = -1;
		String sendCardStartTime = null;
		String sendCardEndTime = null;
		String userNameOrIdcard = null;
		int filter = -1;
		int settleMode = -1;
		int page = -1;
		String batchSn = null;
		
		UnsettlementCardQueryDTO dto = new UnsettlementCardQueryDTO();
		if(companyid ==0){
			log.error("没有可结算的单位,无法列出未结算的卡列表");
			return;
		}
		dto.setExamCompanyIds(Arrays.asList(companyid));
		
		if(!IsArgsNull(hospitalStr)){
			hospitalId = defSettHospitalId;
			dto.setHospitalId(hospitalId);
		}
		
		if(!IsArgsNull(sendCardStartTimeStr)){
			sendCardStartTime = sendCardStartTimeStr;
			dto.setSendCardStartTime(sendCardStartTime);
			}

		if(!IsArgsNull(sendCardEndTimeStr)){
			sendCardEndTime = sendCardEndTimeStr;
			dto.setSendCardEndTime(sendCardEndTime);
			}
	
		if(!IsArgsNull(userNameOrIdcardStr)){
			userNameOrIdcard = userNameOrIdcardStr;
			dto.setUserNameOrIdcard(userNameOrIdcard);
			} else dto.setUserNameOrIdcard("");

		if(!IsArgsNull(filterStr)){
			filter = Integer.parseInt(filterStr);
			dto.setFilter(filter);
			}
		if(!IsArgsNull(settlementModeStr)){
			settleMode = Integer.parseInt(settlementModeStr);
			dto.setSettlementMode(settleMode);
			}
		
		if(!IsArgsNull(statusStr)){
			status = Integer.parseInt(statusStr);
			dto.setStatus(Arrays.asList(status));
			}

		if(!IsArgsNull(pageStr)){
			page = Integer.parseInt(pageStr);
			Page p = new Page();
			p.setCurrentPage(0);
			p.setPageSize(page);
			dto.setPage(p);
			}
		if(!IsArgsNull(batchSnStr)){
			//随意取1个待确认的批次号
			List<TradeSettlementBatch> batchList = SettleChecker.getTradeSettlementBatch(defSettHospitalId,companyid,null,null,SettlementHospitalConfirmEnum.SETTLEMENT_TO_BE_CONFIRM.getCode(),null,false,"1");
			if(batchList!=null && batchList.size()>0){
				batchSn = batchList.get(0).getSn();
				dto.setBatchSn(batchSn);
			}else
				log.error("没有待确认的批次号，无法修改批次并进入未结算卡页面");

		}
		String beforeTime = simplehms.format(DBMapper.query("select now()").get(0).get("now()"));
		String beforeTimeHms = beforeTime.split(" ")[1];
		HttpResult response = httpclient.post(UnSettlementCardIdList, JSON.toJSONString(dto));
		System.out.println(response.getBody());
		Assert.assertEquals(response.getCode(), HttpStatus.SC_OK);
		String body = response.getBody();
	
		Object records = JsonPath.read(body, "$.records");
		JSONArray totleIds = JsonPath.read(body,"$.totleIds");
		int rowCount = JsonPath.read(body,"$.page.rowCount");
		List<UnsettlementCard> retList  = new ArrayList<UnsettlementCard>();
		List<Integer> cardPairs = new ArrayList<Integer>();
		List<Integer> cardIdPairs = new ArrayList<Integer>();
		if(records != null ){
			String recordStr = records.toString();
			String totalIdStr = totleIds.toString();
			retList = com.alibaba.fastjson.JSONArray.parseArray(recordStr, UnsettlementCard.class);
			if(totleIds != null && totleIds.size()>0){
				for(int i = 0;i<totleIds.size();i++)
					cardIdPairs.add(Integer.parseInt(totleIds.get(i).toString()));
			}
		}

		if(checkdb){
			//1.找到未结算的卡
			String statusList = "("+SettlementHospitalConfirmEnum.UNSETTLEMENT.getCode()+","+SettlementHospitalConfirmEnum.SETTLEMENT_CANCELED.getCode()+")";
			String sql = "select c.* from tb_card c  , tb_account a ,tb_hospital_company h  where c.account_id = a.id and  h.id = c.new_company_id "
					+ " and c.create_date > '"+settle_time +"' and c.is_deleted = 0 and c.new_company_id = "+companyid +"  ";
				sql += "and ( c.hospital_settlement_status in "+statusList;
			if(batchSn!=null)
				sql+=" or (c.hospital_settlement_status = "+SettlementHospitalConfirmEnum.SETTLEMENT_TO_BE_CONFIRM.getCode()+" and c.settlement_batch_sn =  "+batchSn+")";
			sql += " ) ";
			if(hospitalId != -1)
				sql += " and c.from_hospital = "+hospitalId;
			if(status !=-1)
				sql += " and c.status = "+status;
			if(settleMode !=-1)
				sql += " and h.settlement_mode = "+settleMode;
			if(filter !=-1){
				if(filter == 1){//过滤发卡时间为最近7天且按项目结算发的卡
					HospitalCompany hc = CompanyChecker.getHospitalCompanyById(companyid);
					if(hc.getSettlementMode()!=null && hc.getSettlementMode() == 0)//按项目
						sql += " and c.create_date < '"+sdf.format(DateUtils.offsetDestDay(new Date(),-7))+" "+beforeTimeHms+"' ";
					}
				else{
					if(sendCardStartTime !=null)
						sql += " and c.create_date >= '"+sendCardStartTime+"' ";
					if(sendCardEndTime !=null)
						sql += " and c.create_date <= '"+sendCardEndTime+"' ";
				}
			}
			if(userNameOrIdcard != null){
				if(IdCardValidate.isIdcard(userNameOrIdcard)){
					sql += " and a.idCard='"+userNameOrIdcard+"'";
				}else{
					sql += " and a.name='"+userNameOrIdcard+"'";
				}
			}
			sql += " order by c.create_date desc,c.id desc ";
			if(page!=-1)
					sql +=" limit "+page;
			log.info(sql);
			List<UnsettlementCard> dbCardList = SettleChecker.getNotSettlementCard(sql);
			Assert.assertEquals(retList.size(),dbCardList.size());
				for(int i=0;i<retList.size();i++){ //第一次进入时
				log.info("比较卡号.."+retList.get(i).getId());
				Assert.assertEquals(retList.get(i).getAccountIdcard(),dbCardList.get(i).getAccountIdcard());
				Assert.assertEquals(retList.get(i).getAccountName(),dbCardList.get(i).getAccountName());
				Assert.assertEquals(retList.get(i).getCompanyName(),dbCardList.get(i).getCompanyName());
				Assert.assertEquals(retList.get(i).getId(),dbCardList.get(i).getId());
				Assert.assertEquals(retList.get(i).getManagerName(),dbCardList.get(i).getManagerName());
				Assert.assertEquals(retList.get(i).getManagerId(),dbCardList.get(i).getManagerId());
				Assert.assertEquals(retList.get(i).getSenCardTime(),dbCardList.get(i).getSenCardTime());
				Assert.assertEquals(retList.get(i).getAlreadyUsedAmount(),dbCardList.get(i).getAlreadyUsedAmount());
				Assert.assertEquals(retList.get(i).getBalance(),dbCardList.get(i).getBalance());
				Assert.assertEquals(retList.get(i).getCapacity(),dbCardList.get(i).getCapacity());
				Assert.assertEquals(retList.get(i).getStatus(),dbCardList.get(i).getStatus());
				Assert.assertEquals(retList.get(i).getSettlementMode(),dbCardList.get(i).getSettlementMode());
				Assert.assertTrue(cardIdPairs.contains(retList.get(i).getId()));//单独的卡列表中有返回卡
			}


		}
	}
	
	 @DataProvider
	  public Iterator<String[]> unSettlementCard(){
			try {
				return CvsFileUtil.fromCvsFileToIterator("./csv/settlement/unsettlementCard.csv",18);
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
