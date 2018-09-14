package com.tijiantest.testcase.main.page;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.jayway.jsonpath.JsonPath;
import com.tijiantest.base.Flag;
import com.tijiantest.base.HttpResult;
import com.tijiantest.base.dbcheck.AccountChecker;
import com.tijiantest.base.dbcheck.CardChecker;
import com.tijiantest.base.dbcheck.HospitalChecker;
import com.tijiantest.base.dbcheck.ResourceChecker;
import com.tijiantest.model.account.Account;
import com.tijiantest.model.account.Examiner;
import com.tijiantest.model.card.Card;
import com.tijiantest.model.company.HospitalCompany;
import com.tijiantest.model.resource.meal.Meal;
import com.tijiantest.model.resource.meal.MealGenderEnum;
import com.tijiantest.testcase.main.MainBase;
import com.tijiantest.util.CvsFileUtil;
import com.tijiantest.util.db.DBMapper;
import com.tijiantest.util.db.SqlException;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * 加载日期控件
 * C端选择体检日期/体检人页面
 */
public class LoadSubmitOrderPageTest extends MainBase {

    @Test(description = "无入口卡 - 选择日期和体检日页面", groups = { "qa" }, dataProvider = "loadSubmitOrderPage")
    public void test_01_loadSubmitOrderPage(String... args) throws SqlException {
        int hospitalId = defHospitalId;
        int mealId = ResourceChecker.getOfficialMealList(hospitalId, MealGenderEnum.FEMALE.getCode()).get(0).getId();
        // 根据套餐获取单项
        List<Integer> itemList = ResourceChecker.getMealExamItemIdList(mealId);
        String _site = args[1];
        String _siteType = args[2];
        Integer orgId = HospitalChecker.getHospitalBySite(_site).getId();

        List<NameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair("mealId", mealId + ""));
        pairs.add(new BasicNameValuePair("entryCardId", ""));
        pairs.add(new BasicNameValuePair("healtherId", ""));
        pairs.add(new BasicNameValuePair("evaluateReportId", ""));
        pairs.add(new BasicNameValuePair("_site", _site));
        pairs.add(new BasicNameValuePair("_siteType", _siteType));
        for (int i = 0; i < itemList.size(); i++) {
            pairs.add(new BasicNameValuePair("itemIds[]", itemList.get(i) + ""));
        }
        Reporter.log(Mobile_LoadSubmitOrderPage+" Post");
        HttpResult result = httpclient.post(Flag.MAIN, Mobile_LoadSubmitOrderPage, pairs);
        String body = result.getBody();
        Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);
        System.out.println("body1111:"+body);
        Examiner account = JSON.parseObject(JsonPath.read(body,"$.account").toString(),Examiner.class);
        // accountReList
        List<Examiner> accountRelationsList = JSON.parseObject(JsonPath.read(body, "$.examinerList").toString(),
                new TypeReference<List<Examiner>>() {
                });

        // address
        String latitude = JsonPath.read(body, "$.address.latitude").toString();
        String longitude = JsonPath.read(body, "$.address.longitude").toString();

        // mobileFieldOrder
        Boolean mobileFieldOrder = JsonPath.read(body, "$.mobileFieldOrder");

        // showExamReport
        Boolean showExamReport = JsonPath.read(body, "$.showExamReport");

        if (checkdb) {
            // account 验证
            Examiner self = AccountChecker.getSelfExaminerForCSide(orgId,defaccountId);
            Account ac = AccountChecker.getAccountById(defaccountId);
            Assert.assertEquals(account.getMobile(),ac.getMobile());//account对象中的手机号是绑定的手机号
            Assert.assertEquals(account.getCustomerId(),self.getCustomerId());
            Assert.assertEquals(account.getName(),self.getName());
            Assert.assertEquals(account.getPinYin(),self.getPinYin());
            Assert.assertEquals(account.getIdCard(),self.getIdCard());
            Assert.assertEquals(account.getStatus(),ac.getStatus());

            //examiners 验证
            List<Examiner> examiners = AccountChecker.getAllExaminersByRelationIdAndOrgId(orgId,defaccountId);
            Assert.assertEquals(accountRelationsList.size(), examiners.size());
            for (int i = 0; i < accountRelationsList.size(); i++) {
                Examiner aRelation = accountRelationsList.get(i);
                Examiner dbExaminers = examiners.get(i);
                Assert.assertEquals(aRelation.getBirthYear(), dbExaminers.getBirthYear());
                Assert.assertEquals(aRelation.getCustomerId(), dbExaminers.getCustomerId());
                Assert.assertEquals(aRelation.getGender(), dbExaminers.getGender());
                Assert.assertEquals(aRelation.getIdCard(), dbExaminers.getIdCard());
                Assert.assertEquals(aRelation.getName(), dbExaminers.getName());
            }
            // latitude， longitude
            String addressSql = "SELECT a.id, a.province, a.city, a.district, h.address, h.latitude, h.longitude FROM tb_hospital h LEFT JOIN tb_address a ON a.id = h.address_id WHERE h.id = ?";
            List<Map<String, Object>> addressList = DBMapper.query(addressSql, hospitalId);
            Assert.assertEquals(latitude, addressList.get(0).get("latitude"));
            Assert.assertEquals(longitude, addressList.get(0).get("longitude"));

            // mobileFieldOrder,showExamReport
            String settingsSql = "select * from tb_hospital_settings where hospital_id=?";
            List<Map<String, Object>> settingsList = DBMapper.query(settingsSql, hospitalId);
            Assert.assertEquals(mobileFieldOrder ? 1 : 0, settingsList.get(0).get("mobileFieldOrder"));
            Assert.assertEquals(showExamReport ? 1 : 0, settingsList.get(0).get("show_exam_report"));
        }

    }

    @Test(description = "有入口卡 - 选择日期和体检日页面", groups = { "qa" }, dataProvider = "loadSubmitOrderPage")
    public void test_02_loadSubmitOrderPage(String... args) throws SqlException {
        String _site = args[1];
        String _siteType = args[2];
        int hospitalId = defHospitalId;
        Integer orgId = HospitalChecker.getHospitalBySite(_site).getId();
        // 获取账户体检中心所有的卡
        List<Integer> cardList = CardChecker.getCardByAccountANDHospital(hospitalId, defaccountId);
        // 随机取一张卡
        Random random = new Random();
        int index = random.nextInt(cardList.size()) % (cardList.size() + 1);
        int entryCardId = cardList.get(index);
        System.out.println("entryCardId:" + entryCardId);

        // 根据cardId，获取支持的套餐
        List<Integer> mealList = CardChecker.getMealByCardId(entryCardId, hospitalId);
        index = random.nextInt(mealList.size()) % (mealList.size() + 1);
        int mealId = mealList.get(index);
        System.out.println("mealId:" + mealId);

        // 根据套餐获取单项
        List<Integer> itemList = ResourceChecker.getMealExamItemIdList(mealId);

        List<NameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair("mealId", mealId + ""));
        pairs.add(new BasicNameValuePair("entryCardId", entryCardId + ""));
        pairs.add(new BasicNameValuePair("healtherId", ""));
        for (int i = 0; i < itemList.size(); i++) {
            pairs.add(new BasicNameValuePair("itemIds[]", itemList.get(i) + ""));
        }
        pairs.add(new BasicNameValuePair("evaluateReportId", ""));
        pairs.add(new BasicNameValuePair("_site", _site));
        pairs.add(new BasicNameValuePair("_siteType", _siteType));

        HttpResult result = httpclient.post(Flag.MAIN, Mobile_LoadSubmitOrderPage, pairs);
        String body = result.getBody();
        Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);

        // 有一些无入口卡的情况已经验证过了，这里只验证无入口卡没验证的一些东西
        Card entryCard = JSON.parseObject(JsonPath.read(body, "$.entryCard").toString(), Card.class);
        int accountId = entryCard.getAccountId();
        int managerId = entryCard.getManagerId();
        HospitalCompany examCompany = JSON.parseObject(JsonPath.read(body, "$.examCompany").toString(), HospitalCompany.class);
        int examCompanyId = examCompany.getId();
        System.out.println("examCompanyId:" + examCompanyId);
        Meal meal = JSON.parseObject(JsonPath.read(body, "$.meal").toString(), Meal.class);
        //int accountId = JsonPath.read(body, "$.accountReList[0].customerId");
        List<Examiner> accountRelationsList = JSON.parseObject(JsonPath.read(body, "$.examinerList").toString(),
                new TypeReference<List<Examiner>>() {
                });
        if (checkdb) {
            // cardSettings
            String settingsSql = "select * from tb_card_settings where card_id=?";
            List<Map<String, Object>> settingsList = DBMapper.query(settingsSql, entryCardId);
            Assert.assertEquals(entryCard.getCardSetting().isPayFreeze() ? 1 : 0,
                    settingsList.get(0).get("is_pay_freeze"));
            Assert.assertEquals(entryCard.getCardSetting().isPayMealCost() ? 1 : 0,
                    settingsList.get(0).get("is_pay_meal_cost"));
            Assert.assertEquals(entryCard.getCardSetting().isPrivate()? 1 : 0, settingsList.get(0).get("isprivate"));
            Assert.assertEquals(entryCard.getCardSetting().isShowCardMealPrice()? 1 : 0,
                    settingsList.get(0).get("is_show_card_meal_price"));


            // entryCard
            String entryCardSql = "select tb_card.id, batch_id, card_name, card_num, password, capacity, balance, recoverable_balance, tb_card.type, status, from_hospital, recharge_time, parent_card_id, available_date, expired_date, create_date, tb_card.account_id,tb_manager_card_relation.manager_id, tb_manager_card_relation.exam_company_id from tb_card LEFT JOIN tb_manager_card_relation ON tb_manager_card_relation.card_id = tb_card.id WHERE tb_card.id = ?";
            List<Map<String, Object>> entryCardList = DBMapper.query(entryCardSql, entryCardId);
            Assert.assertEquals(entryCard.getAccountId(), entryCardList.get(0).get("account_id"));
            if(entryCard.getCardSetting().isShowCardMealPrice()){ //隐价卡
                Assert.assertEquals(entryCard.getBalance().longValue(),0l);
                Assert.assertEquals(entryCard.getCapacity().longValue(),0l);
            }
            else{
                Assert.assertEquals(entryCard.getBalance(), entryCardList.get(0).get("balance"));
                Assert.assertEquals(entryCard.getCapacity(), entryCardList.get(0).get("capacity"));

            }
            Assert.assertEquals(entryCard.getBatchId(), entryCardList.get(0).get("batch_id"));
            Assert.assertEquals(entryCard.getCardName(), entryCardList.get(0).get("card_name"));
            Assert.assertEquals(entryCard.getCardNum(), entryCardList.get(0).get("card_num"));
            Assert.assertEquals(entryCard.getManagerId(), entryCardList.get(0).get("manager_id"));
            Assert.assertEquals(entryCard.getParentCardId(), entryCardList.get(0).get("parent_card_id"));
            Assert.assertEquals(entryCard.getStatus(), entryCardList.get(0).get("STATUS"));
            Assert.assertEquals(entryCard.getType(), entryCardList.get(0).get("type"));


            // examCompany
            String mcSql="SELECT * FROM tb_manager_card_relation WHERE card_id = ?;";
            List<Map<String, Object>> mcList = DBMapper.query(mcSql, entryCardId);
            String examCompanySql;
            if(!mcList.get(0).get("organization_type").toString().equals("2")){//如果是体检中心发的卡
                examCompanySql = "SELECT hc.* FROM tb_hospital_company hc LEFT JOIN tb_manager_card_relation mcr ON mcr.new_company_id = hc.id WHERE mcr.card_id = ?";
            }
            else{//平台客户经理发的卡
                examCompanySql = "SELECT hc.* FROM tb_channel_company hc LEFT JOIN tb_manager_card_relation mcr ON mcr.new_company_id = hc.id WHERE mcr.card_id = ?";
            }
            List<Map<String, Object>> examCompanyList = DBMapper.query(examCompanySql, entryCardId);
            System.out.println("体检卡ID:"+entryCardId);
            Assert.assertEquals(examCompany.getId(), examCompanyList.get(0).get("id"));
            Assert.assertEquals(examCompany.getName(), examCompanyList.get(0).get("name"));

            // meal
            String mealSql = "SELECT DISTINCT  m.id, m.hospital_id, m.name, m.description, m.pinyin, m.discount, m.external_discount, m.gender, m.type, m.disable, m.keyword, m.init_price, m.display_price, m.price, m.tip_text, m.sequence, m.update_time, tb_meal_statistics.hot, tb_meal_statistics.order_count, tb_meal_statistics.click_count, tb_meal_settings.show_meal_price, tb_meal_settings.adjust_price, tb_meal_settings.lock_price FROM tb_meal m LEFT JOIN tb_meal_statistics ON tb_meal_statistics.meal_id = m.id LEFT JOIN tb_meal_settings ON tb_meal_settings.meal_id = m.id LEFT JOIN tb_meal_tag ON tb_meal_tag.meal_id = m.id WHERE m.id = ? AND m.disable < 2";
            List<Map<String, Object>> mealsList = DBMapper.query(mealSql, mealId);
            Assert.assertEquals(meal.getDescription(), mealsList.get(0).get("description"));
            Assert.assertEquals(meal.getDisable(), mealsList.get(0).get("disable"));
            Assert.assertEquals(meal.getDisplayPrice(), mealsList.get(0).get("display_price"));
            Assert.assertEquals(meal.getGender(), mealsList.get(0).get("gender"));
            Assert.assertEquals(meal.getHospitalId(), mealsList.get(0).get("hospital_id"));
            if (entryCard.getCardSetting().isShowCardMealPrice()) {
                Assert.assertEquals(meal.getPrice(), new Integer(0));
                Assert.assertEquals(meal.getInitPrice(), new Integer(0));
            } else {
                Assert.assertEquals(meal.getPrice(), mealsList.get(0).get("price"));
                Assert.assertEquals(meal.getInitPrice(), mealsList.get(0).get("init_price"));
            }
            Assert.assertEquals(meal.getKeyword(), mealsList.get(0).get("keyword"));
            Assert.assertEquals(meal.getName(), mealsList.get(0).get("name"));
            Assert.assertEquals(meal.getPinyin(), mealsList.get(0).get("pinyin"));
            Assert.assertEquals(meal.getType(), mealsList.get(0).get("type"));

            // mealSettings
            String mealSettingsSql = "select * from tb_meal_settings where meal_id=?";
            List<Map<String, Object>> mealSettingsList = DBMapper.query(mealSettingsSql, mealId);
            Assert.assertEquals(meal.getMealSetting().getAdjustPrice(), mealSettingsList.get(0).get("adjust_price"));
            Assert.assertEquals(meal.getMealSetting().getLockPrice() ? 1 : 0,
                    mealSettingsList.get(0).get("lock_price"));
            Assert.assertEquals(meal.getMealSetting().isOnlyShowMealItem() ? 1 : 0,
                    mealSettingsList.get(0).get("only_show_meal_item"));
            Assert.assertEquals(meal.getMealSetting().isShowItemPrice() ? 1 : 0,
                    mealSettingsList.get(0).get("show_item_price"));
            Assert.assertEquals(meal.getMealSetting().isShowMealPrice() ? 1 : 0,
                    mealSettingsList.get(0).get("show_meal_price"));

            //examiners 验证
            List<Examiner> examiners = AccountChecker.getAllExaminersByRelationIdAndOrgId(orgId,defaccountId);
            Assert.assertEquals(accountRelationsList.size(), examiners.size());
            for (int i = 0; i < accountRelationsList.size(); i++) {
                Examiner aRelation = accountRelationsList.get(i);
                Examiner dbExaminers = examiners.get(i);
                Assert.assertEquals(aRelation.getBirthYear(), dbExaminers.getBirthYear());
                Assert.assertEquals(aRelation.getCustomerId(), dbExaminers.getCustomerId());
                Assert.assertEquals(aRelation.getGender(), dbExaminers.getGender());
                Assert.assertEquals(aRelation.getIdCard(), dbExaminers.getIdCard());
                Assert.assertEquals(aRelation.getName(), dbExaminers.getName());
            }
        }

    }

    @DataProvider(name = "loadSubmitOrderPage")
    public Iterator<String[]> loadSubmitOrderPage() {
        try {
            return CvsFileUtil.fromCvsFileToIterator("./csv/page/mobile/loadSubmitOrderPage.csv", 1);
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
