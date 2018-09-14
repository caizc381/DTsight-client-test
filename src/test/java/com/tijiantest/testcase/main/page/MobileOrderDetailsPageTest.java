package com.tijiantest.testcase.main.page;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jayway.jsonpath.JsonPath;
import com.tijiantest.base.Flag;
import com.tijiantest.base.HttpResult;
import com.tijiantest.base.dbcheck.HospitalChecker;
import com.tijiantest.base.dbcheck.OrderChecker;
import com.tijiantest.base.dbcheck.PayChecker;
import com.tijiantest.base.dbcheck.ResourceChecker;
import com.tijiantest.model.examitempackage.ExamItemPackage;
import com.tijiantest.model.hospital.Address;
import com.tijiantest.model.hospital.Hospital;
import com.tijiantest.model.hospital.HospitalParam;
import com.tijiantest.model.item.ExamItem;
import com.tijiantest.model.order.MealMultiChooseParam;
import com.tijiantest.model.order.MealMutiChhoseItemVO;
import com.tijiantest.model.order.MealMutiChooseVO;
import com.tijiantest.model.order.Order;
import com.tijiantest.model.order.snapshot.ExamItemPackageSnapshot;
import com.tijiantest.model.order.snapshot.ExamItemSnapshot;
import com.tijiantest.model.order.snapshot.ExamItemToMealEnum;
import com.tijiantest.model.payment.trade.PayAmount;
import com.tijiantest.model.payment.trade.PayConstants;
import com.tijiantest.testcase.main.MainBase;
import com.tijiantest.util.db.SqlException;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.text.ParseException;
import java.util.*;


/**
 * C端订单详情
 *
 */
public class MobileOrderDetailsPageTest extends MainBase {


    @Test(description = "C端订单详情",groups = {"qa"})
    public void test_01_mobileOrderDetailsPage() throws SqlException, ParseException {
        int orderId = 0;
        if(checkdb){
            List<Order> orderList = OrderChecker.getOrderListBySql("select * from tb_order where account_id = "+defaccountId+" order by id desc limit 1");
            orderId = orderList.get(0).getId();
        }
        //STEP1 入参
        List<NameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair("_site", defSite));
        pairs.add(new BasicNameValuePair("_siteType", "mobile"));
        pairs.add(new BasicNameValuePair("_p",""));
        //STEP2 接口调用
        HttpResult result = httpclient.get(Flag.MAIN,Mobile_MobileOrderDetailsPage+"/"+orderId,pairs);
        Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);
        JSONObject jsonResult = JSONObject.parseObject(result.getBody(),JSONObject.class);
        log.info(result.getBody());
        int addNum = jsonResult.getIntValue("addNum");
        int couponAmount = jsonResult.getIntValue("couponAmount");
        String examAddress = jsonResult.getString("examAddress");
        String examCompany = jsonResult.getString("examCompany");
        boolean hasSettlementOpen = jsonResult.getBooleanValue("hasSettlementOpen");
        int inMealNum = jsonResult.getIntValue("inMealNum");
        boolean joinPromotion = jsonResult.getBooleanValue("joinPromotion");
        int needPayPrice = jsonResult.getIntValue("needPayPrice");
        int offlinePay = jsonResult.getIntValue("offlinePay");
        int showInvoice = jsonResult.getIntValue("showInvoice");
        int total = jsonResult.getIntValue("total");
        JSONObject healther = jsonResult.getJSONObject("healther");
        JSONObject order = jsonResult.getJSONObject("order");
        JSONObject itemSnap = jsonResult.getJSONObject("itemSnap");
        JSONArray packages = jsonResult.getJSONArray("packages");
        //couponAmount
        List<MealMutiChooseVO> mealMultiChooses = JSON.parseArray(jsonResult.getString("mealMultiChooses"), MealMutiChooseVO.class);//多选一等价组

        //STEP3 校验DB
        if(checkdb){
            Order dbOrder = OrderChecker.getOrderInfo(orderId);
            //3.1 订单
            Assert.assertEquals(order.getIntValue("status"),dbOrder.getStatus());//订单状态
            //比较订单总价/优惠金额/实际支付
            Assert.assertEquals(order.getIntValue("orderPrice"),dbOrder.getOrderPrice().intValue());//订单金额
            PayAmount payAmount = PayChecker.getPayAmountByOrderNum(dbOrder.getOrderNum(), PayConstants.OrderType.MytijianOrder);
            long successPayAmount = payAmount.getTotalSuccPayAmount();
            long dbCouponAmount = payAmount.getTotalCouponPayAmount();
            long dbOfflineAmount = payAmount.getOfflinePayAmount();
            Assert.assertEquals((int)dbCouponAmount,couponAmount);//优惠金额
            Assert.assertEquals(needPayPrice, dbOrder.getOrderPrice().intValue() -(int)dbCouponAmount ); //需要支付
            Assert.assertEquals(offlinePay,dbOfflineAmount);//现场支付金额

            Assert.assertEquals(sdf.format(dbOrder.getExamDate()),sdf.format(order.getLongValue("examDate")));//体检日期
            Assert.assertEquals(sdf.format(dbOrder.getInsertTime()),sdf.format(order.getLongValue("insertTime")));//下单时间
            Assert.assertEquals(order.getString("orderNum"),dbOrder.getOrderNum());//订单编号
            //3.2 体检人
            Assert.assertEquals(dbOrder.getOrderAccount().getName(),healther.getString("name"));//体检人姓名
            Assert.assertEquals(dbOrder.getOrderAccount().getIdCard(),healther.getString("idCard"));//体检人身份证号码
            Hospital hospital = HospitalChecker.getHospitalById(defHospitalId);
            Address dbAddress = hospital.getAddress();
            Assert.assertEquals(dbAddress.getProvince()+dbAddress.getCity()+dbAddress.getDistrict()+dbAddress.getAddress(),examAddress);
            Assert.assertEquals(dbOrder.getHospitalCompany().getName(),examCompany);
            //3.3 单项信息（等价组/增加项目/套餐内项目)
            int dbAddNum = 0;
            int dbInMealNum = 0;
            List<ExamItemSnapshot> examItemSnapshotList = dbOrder.getOrderMealSnapshot().getExamItemSnapList();//所有单项
            ExamItemPackageSnapshot examItemPackageSnapshot = dbOrder.getOrderMealSnapshot().getExamItemPackageSnapshot();//单项包
            List<ExamItemSnapshot> multiExamItemList = new ArrayList<>();//等价组列表
            List<ExamItem> packageItems = new ArrayList<>();
            //3.3.1 比较单项包
            if(examItemPackageSnapshot!=null){
                List<ExamItemPackage> dbPaks = examItemPackageSnapshot.getPackages();
                if(packages !=null && packages.size()>0){
                    Assert.assertEquals(dbPaks.size(),packages.size());
                    for(int k=0;k<packages.size();k++){
                        Assert.assertEquals(dbPaks.get(k).getId().intValue(),((JSONObject)packages.get(0)).getIntValue("id"));
                        packageItems.addAll(dbPaks.get(k).getItemList());
                        dbAddNum += dbPaks.get(k).getItemList().size();
                    }
                }else
                    Assert.assertEquals(dbPaks.size(),0);
            }

            //3.3.2 比较套餐内单项+增加单项+等价组内单项
            for(ExamItemSnapshot e : examItemSnapshotList){
                int itemId = e.getId();
//                log.info("单项id"+itemId);
                ExamItem examItem = ResourceChecker.checkExamItem(itemId);
                if(packageItems.contains(examItem))//过滤单项包内单项，不再套餐内
                    continue;
                else {
                    if(itemSnap.get(itemId)!=null){//场景2：查询单项/增加单项
                        JSONObject retItemJson = JSONObject.parseObject(itemSnap.get(itemId).toString(),JSONObject.class);
                        Assert.assertEquals(retItemJson.getIntValue("typeToMeal"),e.getTypeToMeal());
                        Assert.assertEquals(retItemJson.getString("name"),e.getName());
                        Assert.assertEquals(retItemJson.getString("description"),examItem.getDescription());
                        if(e.getTypeToMeal() == ExamItemToMealEnum.addToMeal.getCode())
                            dbAddNum ++;
                        else if(e.getTypeToMeal() == ExamItemToMealEnum.inMeal.getCode())
                            dbInMealNum ++;
                    }else{//场景3:等价组
                        if(e.getMultiChooseId()!=null && e.getMultiChooseName() != null)//构造DB中获取的等价组列表
                            multiExamItemList.add(e);
                    }
                }

            }
            Assert.assertEquals(dbAddNum,addNum);//套餐外增加项目数量
            Assert.assertEquals(dbInMealNum,inMealNum);//套餐内项目数量
            Assert.assertEquals(dbAddNum+dbInMealNum,total);//总数量
            //判断等价组列表
            Assert.assertEquals(multiExamItemList.size(),mealMultiChooses.size());
            Collections.sort(multiExamItemList, new Comparator<ExamItemSnapshot>() { //排序
                @Override
                public int compare(ExamItemSnapshot o1, ExamItemSnapshot o2) {
                    return o1.getMultiChooseId().hashCode() - o2.getMultiChooseId().hashCode();
                }
            });

            Collections.sort(mealMultiChooses, new Comparator<MealMutiChooseVO>() { //排序
                @Override
                public int compare(MealMutiChooseVO o1, MealMutiChooseVO o2) {
                    return  o1.getMultiChooseId().hashCode() - o2.getMultiChooseId().hashCode();
                }
            });
            for(int i=0;i<multiExamItemList.size();i++){//等价组内组和选择单项比较
                Assert.assertEquals(mealMultiChooses.get(i).getMultiChooseId(),multiExamItemList.get(i).getMultiChooseId());//组ID
                Assert.assertEquals(mealMultiChooses.get(i).getMultiChooseName(),multiExamItemList.get(i).getMultiChooseName());//组名称
                List<MealMutiChhoseItemVO> chhoseItemVOList =   mealMultiChooses.get(i).getGroupItemList();
                for(MealMutiChhoseItemVO v : chhoseItemVOList)
                    if(v.getId().equals(multiExamItemList.get(i).getId()))
                        Assert.assertTrue(v.getSelected().booleanValue()); //选中单项ID
                    else
                        Assert.assertFalse(v.getSelected().booleanValue());

            }
            //4.结算状态
            Map<String, Object> hospitalSettings = HospitalChecker.getHospitalSetting(defHospitalId, HospitalParam.SETTLEMENT_OPEN,HospitalParam.SETTLEMENT_TIME);
            int dbSettleOpen = Integer.parseInt(hospitalSettings.get(HospitalParam.SETTLEMENT_OPEN).toString());
            String dbSettleTime = hospitalSettings.get(HospitalParam.SETTLEMENT_TIME).toString();
            if(dbSettleOpen == 0)//未开启结算
                Assert.assertFalse(hasSettlementOpen);
            else
                if(simplehms.parse(dbSettleTime).compareTo(new Date()) == -1)//结算开启时间小于当前时间
                    Assert.assertTrue(hasSettlementOpen);
            else
                Assert.assertFalse(hasSettlementOpen);

        }
    }
}
