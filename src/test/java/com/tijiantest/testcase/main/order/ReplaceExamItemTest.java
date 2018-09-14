package com.tijiantest.testcase.main.order;

import com.alibaba.fastjson.JSON;
import com.tijiantest.base.Flag;
import com.tijiantest.base.HttpResult;
import com.tijiantest.base.dbcheck.CompanyChecker;
import com.tijiantest.base.dbcheck.CounterChecker;
import com.tijiantest.base.dbcheck.OrderChecker;
import com.tijiantest.base.dbcheck.ResourceChecker;
import com.tijiantest.model.company.HospitalCompany;
import com.tijiantest.model.order.MealMultiChooseParam;
import com.tijiantest.model.order.Order;
import com.tijiantest.model.order.ReplaceExamItemParam;
import com.tijiantest.model.resource.meal.MealExamitemGroup;
import com.tijiantest.model.resource.meal.MealGenderEnum;
import com.tijiantest.testcase.main.MainBase;
import com.tijiantest.util.CvsFileUtil;
import com.tijiantest.util.ListUtil;
import com.tijiantest.util.db.SqlException;
import org.apache.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.*;

/**
 * C端订单详情等价组换项目
 * 入口：多选一套餐的订单->订单详情->换项目
 */
public class ReplaceExamItemTest extends MainBase {
    @Test(description = "订单详情中等价组换项目，保存",groups = {"qa"},dataProvider = "replaceExamItem")
    public  void test_01_replaceExamItem(String ...args) throws SqlException, ParseException {
        log.info("============创建包括等价组的订单 START=============");
        //STEP1 准备数据 创建等价组订单
        com.tijiantest.model.resource.meal.Meal offMeal = ResourceChecker.getOfficialMealListByMultiChooseOne(defHospitalId, MealGenderEnum.FEMALE.getCode(),true).get(0);
        int offmealId = offMeal.getId().intValue(); //获取第一个官方女性套餐(等价组套餐)
        List<Integer> itemList = ResourceChecker.getMealExamItemIdList(offmealId);
        String exam_date = args[1];
        HospitalCompany hc = CompanyChecker.getHospitalCompanyByPlatCompanyIdANDOrganizationId(1,defHospitalId);
        Map<String,Object> dateMap = CounterChecker.getDateBookableFromStartDate(sdf.parse(exam_date),sdf.parse(exam_date),hc.getId(), defHospitalId);
        int dayRangeId = Integer.parseInt(dateMap.get("dayRangeId").toString());
        int account_id = defaccountId;
        int orderId = OrderChecker.main_createOrder(httpclient,exam_date,dayRangeId,account_id,defHospitalId,offmealId);
        log.info("============创建包括等价组的订单 END=============");

        Order order = OrderChecker.getOrderInfo(orderId);
        //STEP2 换项目
        ReplaceExamItemParam replaceExamItemParam = new ReplaceExamItemParam();
        replaceExamItemParam.setOrderNum(order.getOrderNum());
        List<MealMultiChooseParam> chooseParams = new ArrayList<>();
        List<Integer> removeGroupItemList = new ArrayList<>();//等价组内需要删除的单项列表
        List<String> groups = ResourceChecker.getMealGroupByMealId(offmealId);//官方套餐的等价组列表
        List<Integer> groupExamIds = new ArrayList<>();//等价组最后选择的单项列表
        for(String k : groups){
            MealMultiChooseParam chooseParam = new MealMultiChooseParam();
            chooseParam.setMultiChooseId(k);
            List<MealExamitemGroup> examitemLists =  ResourceChecker.getMealExamitemGroupByMealId(offmealId,k);//查询组内的所有单项ID,随机取一个
            int index = new Random().nextInt(examitemLists.size());
            int examId = examitemLists.get(index).getItemId();
            chooseParam.setSelectExamItemId(examId);//组内的单项ID
            chooseParam.setMultiChooseName(examitemLists.get(index).getGroupName());//组名称
            chooseParams.add(chooseParam);
            int defaultSelectItem = ResourceChecker.getExamitemGroupDefaultSelectId(offmealId,k);
            if(defaultSelectItem != examId){
                removeGroupItemList.add(defaultSelectItem);//去除默认选项
                itemList.add(examId);//增加已选项
            }
            groupExamIds.add(examId);//等价组最后选择的单项列表
        }
        itemList.removeAll(removeGroupItemList);

        replaceExamItemParam.setNewMealMultiChooseParams(chooseParams);

        //STEP3 接口调用
        HttpResult result = httpclient.post(Flag.MAIN,Mobile_ReplaceExamItem, JSON.toJSONString(replaceExamItemParam));
        Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);
        Assert.assertTrue(result.getBody().equals("")||result.getBody().equals("{}"));

        //STEP4 校验
        if(checkdb){
            //4.1 校验订单的状态/金额不变
            Order nowOrder = OrderChecker.getOrderInfo(orderId);
            Assert.assertEquals(nowOrder.getStatus(),order.getStatus());
            Assert.assertEquals(nowOrder.getOrderPrice(),order.getOrderPrice());
            //4.2 校验订单的调整金额变化
            int nowAdjustPrice = nowOrder.getOrderMealSnapshot().getMealSnapshot().getAdjustPrice();
            Assert.assertEquals(nowAdjustPrice,OrderChecker.calculateClientOrderAdjustPrice(offmealId,groupExamIds));
            //4.3 校验订单内单项改变
            Assert.assertTrue(ListUtil.equalList(itemList,OrderChecker.getOrderItemList(orderId)));
        }
    }

    @DataProvider
    public Iterator<String[]> replaceExamItem() {
        try {
            return CvsFileUtil.fromCvsFileToIterator("./csv/order/main_replaceExamItem.csv", 8);
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
