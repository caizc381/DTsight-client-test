package com.tijiantest.base;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public interface ActionsDefine {

    /**********************************************************************************************************************************
     * actions config
     **********************************************************************************************************************************
     * /


     /*
     ******************************************************************************************************************************
     *
     *                                                 Coupon 优惠券
     * *****************************************************************************************************************************/
    //CRM
     public final static String Coupon_DownloadQRCode = "/coupon/downloadQRCode"; //GET 下载二维码

    public final static String Coupon_NewCouponTemplate="/coupon/newCouponTemplate";//POST创建优惠券
    public final static String Coupon_SpreadCoupon="/coupon/downloadQRCode";//GET推广优惠券


     public final static String Coupon_UpdateTemplateCouponStatus = "/coupon/updateTemplateCouponStatus";//GET 更新优惠券模板状态(停用|发放)
     public final static String Coupon_UpdateCouponTemplateFiled = "/coupon/updateCouponTemplateField"; //POST 优惠券模版修改
     public final static String Coupon_FindCouponTemplates = "/coupon/findCouponTemplates";//POST 优惠券模版列表查询
     public final static String Coupon_WatchCouponTemplate = "/coupon/watchCouponTemplate";//GET 优惠券模版查看
     public final static String Coupon_FindCoupon = "/coupon/findCoupon"; //POST 查看优惠券领取记录
    //C
     public final static String Coupon_CardAndCoupon = "/usercenter/get/cardAndCoupon";//GET 查看卡券接口
     public final static String Coupon_ReceiveCoupon = "/coupon/post/receiveCoupon";//领取优惠券
     public final static String Coupon_LinkStatus = "/coupon/get/linkstatus";//判断链接是否失效
    //OPS
    public final static String Coupon_QueryHospitalManagers="/manager/queryHospitalMangers";//GET获取医院客户经理
     //public final static String Coupon_NewCouponTemplate="/coupon/newCouponTemplate";

     /*
     ******************************************************************************************************************************
     *
     *                                                  1.PAY START
     * *****************************************************************************************************************************
     * /	/************** OPS refundApply ***********************/
    public final static String ListRefundApply = "/orderrefund/listRefundApply"; //筛选退款订单
    public final static String AgreeRefund = "/orderrefund/agreeRefund"; //同意退款
    public final static String RefuseRefund = "/orderrefund/refuseRefund";//拒绝退款
    public final static String ListRefundApplyRecord = "/orderrefund/listRefundApplyRecord";//审批记录页面
    public final static String ListHospital = "/hospital/listHospital"; //列举所有的医院列表
    public final static String ListCompany = "/hospital/listCompany"; //根据机构查询所有单位列表
    /************** OPS refundApply ***********************/

    /************** 结算管理 CRM部分 ***********************/
    public final static String GetTaskList = "/task/getTaskList"; //异步任务列表
    public final static String Order_TerminateOrderProcess = "/order/terminateOrderProcess";//手动停止异步任务任务

    //显示结算相关数据【结算管理】-》【单位结算】
    public final static String CompanySettlePage = "/companySettlePage"; //单位结算页面
    public final static String UnSettlementOrderList = "/unsettlementOrderList" ;//未结算订单列表
    public final static String UnSettlementCardIdList = "/unsettlementCardList"; //未结算卡列表
    public final static String UnSettlementPaymentOrderList = "/unsettlementPaymentOrderList" ;//未结算收款订单列表
    public final static String GetUnsettlementRefundNum = "/getUnsettlementRefundNum"; //获取未结算的退款数量
    public final static String GetAllOrderNumsBySn = "/getAllOrderNumsBySn";//获取待确认批次的结算订单编号，卡id,收款订单号
    public final static String HasTask = "/task/hasTask"; //查询是否有正在执行的结算任务

    //生成结算【结算管理】-》【单位结算】
    public final static String CreateSettlementBatch = "/createSettlementBatch"; //新建结算批次
    //结算批次【结算管理】-》【结算批次】
    public final static String GetTradeSettlementBatchList = "/settlement/getTradeSettlementBatchList"; //显示结算批次列表
    public final static String ConfirmSettlementBatch = "/confirmSettlementBatch"; //确认结算
    public final static String RevokeSettlementBatch = "/revokeSettlementBatch";//撤销结算
    public final static String DownLoadSettlementBill = "/settlement/downloadSettlementBill";//下载对账单
    public final static String GetHospitalCompanyForSearchOrder = "/getHospitalCompanyForSearchOrder";; //列举医院的所有单位

    //收款账单【结算管理】-》【收款账单】
    public final static String GetHospitalPlatformBillList = "/settlement/getHospitalPlatformBillList"; //获取医院平台账单
    public final static String GetHospitalCompanyBillList = "/settlement/getHospitalCompanySummaryBillList"; //获取医院单位账单
    public final static String HospitalReceipt = "/settlement/hospitalReceipt"; //医院收款

    //收款记录 【结算管理】-》【收款记录】
    public final static String GetSettlementPayRecordList = "/settlement/getSettlementPayRecordList"; //获取收款记录
    //消费额度 【结算管理】-》【消费额度】
    public final static String ListNotAuditedConsumeQuotaDetail = "/listNotAuditedConsumeQuotaDetail"; //获取未审核的消费额度
    public final static String ListHospitalConsumeQuotaDetail = "/listHospitalConsumeQuotaDetail";//获取消费额度明细
    public final static String AuditConsumeQuotaDetail = "/auditConsumeQuotaDetail"; //确认消费额度
    /************** 结算管理 OPS部分 ***********************/
    public final static String OPS_ReviewBill = "/settlement/reviewBill"; //审核账单
    public final static String OPS_ConsumeQuotaManage = "/settlement/consumeQuotaManage";//列举医院统计
    public final static String OPS_ListConsumeQuotaDetail = "/settlement/listConsumeQuotaDetail"; //列举所有的消费额度
    public final static String OPS_GetPlatConsumeQuotaStatistics = "/settlement/getPlatformConsumeQuotaStatistics"; //获取医院统计的消费额度统计值
    public final static String OPS_GetPrepaymentRecord = "/settlement/getPrepaymentRecordList"; //列举所有的特殊退款
    public final static String OPS_AddPrepaymentRecord = "/settlement/addPrepaymentRecord"; //新增特殊退款
    public final static String OPS_AddConsumeQuotaDetail = "/settlement/addConsumeQuotaDetail";//新增消费额度
    public final static String OPS_GetHospitalPlatformSummaryBillList = "/settlement/getHospitalPlatformSummaryBillList";//列举付款账单
    public final static String OPS_AddSettlementPayRecord = "/settlement/addSettlementPayRecord";//财务付款
    public final static String OPS_DownloadSettlementBill = "/settlement/downloadSettlementBill";//下载对账单
    public final static String OPS_UploadImage = "/settlement/uploadImage"; //上传凭证
    public final static String OPS_UpdatePrepaymentRecord = "/settlement/updatePrepaymentRecord";//更新特殊退款
    public final static String OPS_DeletePrepaymentRecord = "/settlement/deletePrepaymentRecord";//删除特殊退款
    public final static String OPS_AuditHospitalPlatformBill = "/settlement/auditHospitalPlatformBill";//OPS账单确认(财务确认)
    public final static String OPS_GetTradeSettlementBatchListTest = "/settlement/channel/getTradeSettlementBatchList";//渠道订单列表

    /************** ops withdraw ***********************/
    public final static String Withdrawaudit_FirstTrialList = "/withdrawaudit/firstTrialList";
    public final static String Withdrawaudit_DoFirstTrial = "/withdrawaudit/doFirstTrial";
    public final static String Withdrawaudit_UserInfo = "/withdrawaudit/userInfo";
    /*
         ******************************************************************************************************************************
     *
     *                                                  1.PAY END
     * *****************************************************************************************************************************
     * /


    /*
         ******************************************************************************************************************************
     *
     *                                                  2.ORDER START
     * *****************************************************************************************************************************
     * /
    /************** crm order ***********************/
    public final static String Order_GetOrder = "/order/getOrder"; // 根据id获取订单
    public final static String Order_GetCrmOrder = "/order/getorder"; // 根据id获取订单
    public final static String Order_ImmediateExport = "/order/immediateExport"; // 导出即时订单
    public final static String Order_BatchOrder = "/order/batchOrder";// 批量下单
    public final static String Order_GetBatchOrderProc = "/order/getBatchOrderProc"; // 获取批量下单进度
    public final static String Order_BatchOrderProc = "/order/batchOrderProc"; // 获取批量下单进度（已弃用）
    public final static String Order_OrderBatch = "/order/orderBatch"; // 查询和增加订单批次
    public final static String Order_Orderlist = "/order/orderlist"; // 根据订单批次查询订单列表
    public final static String Order_RevokeOrder = "/order/revokeorder"; // 撤单订单
    public final static String Order_DeleteOrder = "/order/deleteorder"; // 删除订单
    public final static String Order_MongoOrder = "/order/mongoOrder"; // 根据订单查询条件对象查询订单信息
    public final static String Order_MongoBatchOrder = "/order/mongoBatchOrder"; // 根据订单查询条件对象查询订单信息
    public final static String Order_OrderInfoForExport = "/order/orderInfoForExport"; // 根据订单id集合查询需要导出的订单信息
    public final static String Order_ExportOrder = "/order/exportOrder"; // 导出订单信息
    public final static String Order_OrderForNotExport = "/order/orderForNoExport"; // 将订单恢复为未导出
    public final static String Order_UploadRefundExcel = "/order/uploadRefundExcel";
    public final static String Order_ChangeExamDate = "/order/changeExamDate"; // 订单改期
    public final static String Order_Invoice = "/order/invoice"; // 查询订单的发票信息
    public final static String Order_FastBook = "/order/fastbook";//极速预约
    public final static String Order_LoadSubmitOrderPage = "/order/loadSubmitOrderPage";//极速预约请求套餐价格,折扣，极速预约文字显示
    public final static String Order_ExportOrderNumber = "/order/exportOrderNumber";
    public final static String Order_ManualExportOrder = "/order/manualexportOrder";
    public final static String Order_ExportOrderJob = "/order/exportOrderJob"; //单位订单->导出订单任务
    public final static String Order_TempUpdateMysqlAndMongoOrder = "/order/tempUpdateMySQLAndMongoOrder"; // 将订单恢复为未导出
    public final static String Order_ManualUpdateMongoOrder = "/order/manualUpdateMongoOrder"; // 特殊情况，直接更新mongo订单数据
    public final static String Order_QueryOrder = "/order/queryOrder";// CRM订单管理查询订单
    public final static String Order_SelectAllOrder = "/order/queryAllOrderInfo";// 全选订单
    public final static String Order_GetOrderCanExport = "/order/getOrderCanExport"; // CRM订单管理获取可以导出至体检软件的订单
    public final static String Order_BatchExportOrder = "/order/batchExportOrder"; // CRM订单管理导出至体检软件
    public final static String Order_OrderInfoForExportXls = "/order/orderInfoForExportXls"; //CRM订单管理导出到内网
    public final static String Order_ExportOrderXls = "/order/exportOrderXls";//确定导出xls
    public final static String Order_getOrderCanUnexport = "/order/getOrderCanUnexport"; // CRM订单管理获取可以恢复未导的订单
    public final static String Order_batchChangeOrderToUnExport = "/order/batchChangeOrderToUnExport";
    public final static String Order_confirmmedical = "/order/confirmmedical";//确认客户已到检
    public final static String Order_NEEDLOCALPAY_V2 = "/order/needLocalPay_v2";//确认收款
    public final static String Order_ManuallyRefund = "/manuallyRefund"; //手动退款
    public final static String Order_VerifySameAccountOrder = "/order/verifySameAccountOrder";//查询用户是否有重复订单
    public final static String Order_SettleBatch = "/order/settleBatch"; //查询结算标记
    public final static String Order_Crm_IsExportCheckbookOverRange = "/order/isExportCheckbookOverRange";//CRM订单管理导出查看
    public final static String Order_Crm_ExportCheckbook = "/order/exportCheckbook";//CRM订单管理导出查看
    public final static String Order_GetOrderCanExportXls = "/order/getOrderCanExportXls";//CRM订单管理 获取订单可导出xls
    public final static String Order_OrderPreExpDay = "/order/orderPreExpDay"; //杭辽体检中心，查询订单提前导出时间
    public final static String Order_SignOrderSettle = "/order/signOrderSettle";//没有开启新结算功能的体检中心，标记结算/撤销结算
    public final static String Order_GetOperateHistory = "/order/getOperateHistory";//查询批量预约历史记录
    public final static String Order_TotalMoney = "/order/totalMoney"; //订单&用户->订单管理->立即统计
    public final static String Order_PrintAndQueue = "/order/printAndQueue";//打印导检单并加入到排队系统
    /************** crm order checklist ***********************/
    public final static String Order_GetPrintTemplate="/getPrintTemplate";//获取备注内容模板内容
    public final static String Order_GetChecklist = "/getChecklist"; //打印
    public final static String Order_GetChecklistFastbook = "/getChecklistFastbook";//极速预约打印预检凭证
    /************** 收款订单 ***********************/
    public final static String GetPaymentOrderByPage = "/pay/getPaymentOrderByPage";//收款订单-CRM
    public final static String Payment_TotalMoney = "/pay/totalMoney";//统计金额-CRM
    public final static String Payment_CreatePaymentQRCode = "/manager/createPaymentQRCode";//GET 创建收款二维码-CRM
    public final static String Payment_ManaualRefund = "/pay/manuallyRefund"; //POST  收款订单手动退款-CRM
    public final static String Payment_RevokePaymentOrder = "/pay/revokePaymentOrder"; //GET 撤销收款订单-CRM
    public final static String Payment_DownloadPaymentOrder = "/pay/downloadPaymentOrder"; //GET 导出查看收款订单-CRM
    public final static String Payment_AddHospitalRemark = "/pay/addHospitalRemark"; //POST 增加医院备注-CRM

    public final static String Payment_CreatePaymentOrder = "/createPaymentOrder"; // POST 新建收款订单-MAIN
    public final static String Payment_GetPaymentOrder = "/payment/getPaymentOrder";//GET 读取收款订单的tips-MAIN
    public final static String OPS_GetPaymentOrderByPage = "/paymentorder/getPaymentOrderByPage";//OPS查询收款订单列表
    public final static String OPS_DownLoadPaymentOrder = "/paymentorder/downloadPaymentOrder"; //OPS导出查看收款订单列表
    public final static String OPS_ListChannelUnsettlementOrder = "/settlement/channel/listChannelUnsettlementOrder"; //OPS结算订单列表

    /************** 用户查询accountquery - CRM-2.18version ***********************/
    public final static String AccountQuery_AccountManagePageInfo = "/manager/accountManagePageInfo"; // GET
    public final static String AccountQuery_UpdateRelation = "/account/updateRelation"; // POST
    public final static String AccountQuery_CardMeal = "/resource/cardMeal"; // GET
    public final static String AccountQuery_ProxyFastbook = "/order/proxyFastBook"; // POST
    public final static String AccountQuery_AccountOrderPage = "/order/accountOrderPage";
    public final static String AccountQuery_CardInfoByAccount = "/card/cardInfoByAccount";
    public final static String AccountQuery_MealItems = "/resource/mealItems";// GET
    // /{mealId}/{type}
    public final static String AccountQuery_ShowPayWay = "/order/showPayWay";//GET
    /************** stat ***********************/
    public final static String STAT_COMPANYORDERREPORT = "/companyOrderReport"; // GET
    public final static String STAT_COMPANYDEPARTMENT = "/companyDepartment"; // GET
    public final static String STAT_IMPORTITEM = "/importItem"; // GET
    public final static String STAT_EXPORTORDERSTATIS = "/exportOrderStatis"; // GET



    /************** main order ***********************/
    public final static String MainOrder_GetExamNotice = "/order/getExamNotice";
    public final static String BackLog = "/backlog";//待办事项
    public final static String Order_ChangeExamItem = "/order/changeExamItem";// 主站点改项目
    public final static String Order_WatchOrder = "/order/watchOrder"; // 查看订单(CRMV2.18)
    public final static String Order_OrderExamItems = "/order/orderExamItems"; // 获取订单单项id数组
    public final static String Order_dayRange = "/order/dayRange"; //C端获取时间段
    public final static String Order_DateUnit = "/order/dateUnit";//获取体检日期窗口
    public final static String MainOrder_Book = "/order/book";// 主站点下单
    public final static String MainOrder_OrderListPage = "/orderListPage";// 主站点获取订单
    public final static String MainRevokerOrder = "/order/revokeOrder/";//C端撤单
    public final static String MainDeleteOrder = "/order/deleteOrder/"; // 删除订单
    public final static String MainQueryOrder = "/marketing/queryOrderList";//查看营销成就订单
    public final static String MainCountOrder = "/marketing/countOrder";//查看营销成就页面订单；列表

    /************** main mobileMeal 手机端套餐相关页面***********************/
    public final static String Mobile_MealDetailPage = "/mealDetailPage"; //GET /mealDetailPage/${mealId}
    public final static String Mobile_ListExamItemPackage = "/meal/listExamItemPackage"; //GET 列出单项包
    public final static String Mobile_FinishSelect = "/meal/finishSelect"; //GET 选好了
    public final static String Mobile_SelectPackage = "/meal/selectPackage"; //POST 选择单项包
    public final static String Mobile_MobileOrderDetailsPage = "/mobileOrderDetailsPage"; //GET订单详情
    public final static String Mobile_SelectItem = "/resource/selectItem";//用户每次选择一个项目时调用此接口,获得计算的金额
    public final static String Mobile_ReplaceExamItem = "/order/replaceExamItem";//等价组换项目

    /************** main invoice***********************/
    public final static String Invoice_OrderInvoiceApply = "/orderInvoiceApply";
    public final static String Invoice_GetInvoiceApply = "/orderInvoiceApply";//GET /orderInvoiceApply/${orderId}
    /************** main 免登陆预约***********************/
    public final static String ValidateLoginAddToken = "/validateLoginAddToken"; //GET 查询是否登陆
    public final static String LoadSubmitOrderPageNoLogin = "/loadSubmitOrderPageNoLogin"; //POST 加载免登陆页面
    public final static String NoLoginBook = "/order/noLoginBook";  //POST 免登陆预约
    public final static String NoLoginPayForOk = "/noLoginPayforOk";  //POST 免登陆预约
    public final static String NoLoginPayPageV2 = "/noLoginPayPage_v2"; //新的免登陆支付方式
    public final static String Main_PayPageV2 = "/payPage_v2"; //新的支付方式(支付页面)
    public final static String Main_OrderPay = "/orderpay";   //统一了C端支付页面（免登陆 + 登陆下下单)
    public final static String Main_PaymentPage = "/paymentPage";//微信小程序支付页面
    public final static String Main_OrderPayment = "/orderpayment"; //微信小程序支付
    public final static String Main_OrderBookPay = "/order/orderBookPay";//全民营销订单支付

    /************** channel order ***********************/
    public final static String Order_GetHospitalsAndCompanysByOrganizationId = "/getHospitalsAndCompanysByOrganizationId";//获取渠道商分配的体检中心
    public final static String Order_ListChannelMongoOrders="/listChannelMongoOrders";//渠道商mongo订单查询
    public final static String Order_GetOrderDetails="/getOrderDetails";//渠道商mongo订单查询
    public final static String Order_SelectAll = "/selectAll";//全选
    public final static String Order_BatchSendMsg = "/batchSendMsg";//批量发送短信
    public final static String Order_IsExportCheckbookOverRange = "/isExportCheckbookOverRange";//导出查看
    public final static String Channel_OrderExamItems = "/orderExamItems";//获取订单单项id数组
    public final static String Order_RevokeChannelOrder = "/revokeChannelOrder";//撤销订单

    /************** ops order ***********************/
    public final static String OpsOrder_QueryOrder = "/queryOrder";//订单列表
    public final static String OpsOrder_OrderDetail = "/orderDetail";
    public final static String OpsOrder_RevokeOrder = "/revokeorder";
    public final static String OpsOrder_OrderExamItems = "/orderExamItems";
    public final static String OpsOrder_WatchOrder = "/watchOrder";
    public final static String OpsOrder_ChangeExamDate = "/changeExamDate";
    public final static String OpsOrder_GetOrderCanExport = "/getOrderCanExport";
    public final static String OpsOrder_OrderOrganizationList = "/orderOrganizationList";//订单管理- 获取体检中心/渠道列表
    public final static String OpsOrder_AllCompanies = "/allCompanies";//根据体检中心，获取单位
    public final static String OpsOrder_ListChannelCompanyList = "/listChannelCompanyList";//根据渠道商，获取渠道单位
    public final static String OpsOrder_ExportCheckbook = "/exportCheckbook";//导出查看
    public final static String OpsOrder_GetOrderCanExportXls = "/getOrderCanExportXls";//导出XLS
    public final static String OpsOrder_BatchSendMsg = "/batchSendMsg";//发送短信
    public final static String OpsOrder_QueryAllOrderInfo = "/queryAllOrderInfo";//全选订单

    /************** manage order ***********************/
    public final static String Manage_Order = "/order";

    public final static String Manage_ReportOrder = "/report/order";
    public final static String Manage_OrderInfoForExport = "/orderInfoForExport"; // 导出订单信息
    public final static String Manage_ExportOrder = "/exportOrder";
	/*
 ******************************************************************************************************************************
 *
 *                                                  2.ORDER END
 * *****************************************************************************************************************************
 * /

/******************************************************************************************************************************
 *
 *                                                  3.CARD START
 * ******************************************************************************************************************************/

    //crm&c
    public final static String Card_DistributeCard = "/card/distributeCard";
    public final static String Card_CompanyMeals = "/card/companyMeals"; //接口已经被开发删除
    public final static String Card_ExamNotes = "/card/examNotes";
    public final static String Card_Examnote =  "/card/examNote" ;//POST 保存体检须知，删除体检须知/examNote/{noteId}
    public final static String Card_ExamnoteById = "/card/examNoteById"; //GET
    public final static String Card_ParentCard = "/card/parentCard"; // 查询母卡信息
    public final static String Card_DistributeCardProc = "/card/distributeCardProc";// 查询发卡进度
    public final static String Card_CardBatchs = "/card/cardBatchs"; // 参数：/cardBatchs/{companyId}
    public final static String Card_CardRecords = "/card/cardRecords";
    public final static String Card_ExportCardRecords = "/card/exportCardRecord";
    public final static String Card_RevocCard = "/card/revokeCard";
    public final static String Card_RecoverBalance = "/card/recoverBalance";
    public final static String Card_CompanyIncreseReserve = "/card/companyIncreasedReserve";
    public final static String Card_CleanCardRecord = "/card/cleanCardRecord";
    public final static String Card_CardBatchInfo = "/card/cardBatchInfo";
    public final static String Card_ResendCardMsg = "/card/resendCardMsg";
    public final static String Card_SetCardSegment = "/card/setCardSegment"; //POST
    public final static String Card_SendEntryCard = "/card/sendEntryCard";//POST
    public final static String Card_EditExpiredDate = "/card/editExpiredDate";//修改卡的有效期
    public final static String Card_Officialmeals = "/resource/officialmeals";//根据体检中心ID查询官方套餐，/resource/officialmeals/{hospitalId}
    public final static String Card_UpdateBatchMsgStatus = "/card/updateBatchMsgStatus";//更新卡批次发送短信状态
    public final static String Card_GetCardPreviewList = "/getCardPreviewList";//打印预检凭证
    public final static String Card_CardTableHead = "/card/cardTableHead"; //发卡记录过滤表头信息
    public final static String Card_ResendCardMsgNoTemplate = "/card/resendCardMsgNoTemplate";//卡发送短信不使用模板

    /************** main card***********************/
    public final static String Card_CardListPage = "/cardListPage";
    public final static String Card_LoadHospitalCardPage = "/loadHospitalCardPage";
    public final static String Card_BindCard = "/card/bindCard"; //POST
    public final static String Card_ValidCardsForAccount = "/card/validCardsForAccount";//获取登录人可用的卡
    public final static String Card_AccountCards="/accountCards";
    public final static String Card_GetCard = "/card/getCard";//根据订单获取卡信息
    public final static String Card_GetBindingCardPage = "/getBindingCardPage";
    //ops
    public final static String OPS_Card = "/card";
    public final static String OPS_GenerCardNum = "/generCardNum";//GET
    public final static String OPS_RevokeCard = "/revokeCard";//用户管理-撤销发卡
    public final static String OPS_RecoverBalance = "/recoverBalance";//用户管理-金额回收

    //channel
    public final static String Card_CardRecord = "/cardRecord";//Channel-查询实体卡记录
    public final static String Card_UpdateCardExpireDate = "/updateCardExpireDate";//Channel-查询实体卡记录
    public final static String Card_ChannelRecoverBalance = "/recoverBalance";//Channel-回收实体卡余额
    public final static String Card_ChannelRevokeCard = "/revokeCard";//Channel-撤销实体卡
    public final static String Card_ChannelListChannelManager = "/listChannelManager";//Channel-获取渠道的客户经理
    public final static String Card_ChannelListCardMeals = "/listCardMeals";//Channel-获取渠道的客户经理

    /*
     ******************************************************************************************************************************
     *
     *                                                  3.CARD END
     * *****************************************************************************************************************************
     * /

    /*
     ******************************************************************************************************************************
     *
     *                                                  4.COUNTER START
     * *****************************************************************************************************************************
     * /

    /************** counter company ***************/
    public final static String CountComp_Capacity = "/counter/company/capacity"; // 查询单位预留名额当前预留、预约、限额数据
    public final static String CountComp_Period = "/counter/company/period"; // 单位预留统计，companyId为空
    public final static String CountComp_periodDetal = "/counter/company/periodDetal"; // 查询单位分时间段预留名额当前预留、预约、限额数据
    public final static String CountComp_Config = "/counter/company/config"; // 体检中心为体检单位预留信息设置
    public final static String CountComp_BookCapacity = "/counter/company/book/capacity"; // 单位预约当前时间段可预约量
    public final static String CountComp_DoOrder = "/counter/company/doOrder";
    public final static String CountComp_CheckCapacity = "/counter/company/book/checkCapacity"; // 直接通过套餐批量预约，单位预约当前时间段可预约量检查
    public final static String CountComp_ModifyItemCheckCapacity = "/counter/company/book/modifyItemCheckCapacity"; // 改项批量预约，单位预约当前时间段可预约量检查
    public final static String CountComp_releaseCapacity = "/counter/company/releaseCapacity"; // 单位预约当前可时间段可预约量
    public final static String CountComp_RecycleUnsedCapacity = "/counter/company/recycleUnusedCapacity";
    public final static String CountComp_CompanyAvailableSumNum = "/counter/company/companyAvailableSumNum"; // 获取单位体检项目可用总人数
    public final static String CountComp_PeriodDetal = "/counter/company/periodDetal";//未完成
    /************** counter hospital ***************/
    public final static String CountHosp_Capacity = "/counter/hospital/capacity"; // 医院预约名额当前余量、容量信息
    public final static String CountHosp_Period = "/counter/hospital/period"; // 获取体检中心制定日期分时段容量设置信息
    public final static String CountHosp_Config = "/counter/hospital/config"; // 体检中心人数限制全局容量设置
    public final static String CountHosp_Reset = "/counter/hospital/reset"; // 恢复默认设置
    public final static String CountHosp_HeadCatalog = "/counter/hospital/headCatalog"; // 获取体检中心时间段和受限项目设置
    public final static String CountHosp_ImportItem = "/counter/hospital/importItem"; // 重点关注项目
    public final static String CountHosp_ImportItemMainPage = "/counter/hospital/importItemMainPage"; // 重点关注项目首页数据
    public final static String CountHosp_MealCount = "/counter/hospital/mealCount"; // 套餐
    // 统计
    public final static String CountHosp_OrderCount = "/counter/hospital/orderCount"; // 订单统计
    public final static String CountHosp_GetLocalCache = "/counter/hospital/getLoaclCache";
    public final static String CountHosp_CompanyOrderReport = "/counter/hospital/companyOrderReport";

	/*
	 ******************************************************************************************************************************
	 *
	 *                                                  4.COUNTER END
	 * *****************************************************************************************************************************
	 * /


	/******************************************
	 * 5.RESOURCE START
	 * ************************************************/

    /************** examitem package ***********************/
    public final static String ExamItemPackage_AllPkgs="/examItemPkg/allPkgs";//获取所有风险加项包 及 自定义加项包
    public final static String ExamItemPackage_Sort="/examItemPkg/sort";//更新单项包sequence(排序)
    public final static String ExamItemPackageTags_Tags = "/examItemPkgTag/tags";//根据体检中心标识查询加项包标签
    public final static String ExamItemPackage_Cuspackage = "/examItemPkg/cuspackage";//获取客户可选加项包（可选包标签）
    public final static String ExamItemPackage_TagPkgs = "/examItemPkg/tagPkgs";//已选加项包
    public final static String ExamItemPackageTags_Tag = "/examItemPkgTag/tag";//添加/编辑
    public final static String ExamItemPackageTags_UpdateTag = "/examItemPkgTag/updatetag";//更新单项包 POST
    public final static String ExamItemPackageTags_RemoveTag = "/examItemPkgTag/removeTag";//删除加项包标签 GET
    public final static String ExamitemPackageTags_ItemTagRelation = "/examItemPkgTag/itemTagRelation";//添加加项包和标签的关系
    public final static String ExamitemPackage_CustomizedPkgs = "/examItemPkg/customizedPkgs";//根据体检中心查询所有自定义加项包
    public final static String ExamitemPackage_PkgItems = "/examItemPkg/pkgItems";//获取所有加项包中的体检项目
    public final static String ExamitemPackage_ExamitemPackage = "/examItemPkg/examitemPackage";//新增或修改自定义加项包
    public final static String ExamitemPackage_RiskExamitemPackage = "/examItemPkg/riskExamitemPackage";//新增或修改风险加项包
    public final static String ExamitemPackage_UpdateRiskRange = "/examItemPkg/updateRiskRange";//更新风险区间
    public final static String ExamitemPackage_RiskPkgsByRisk = "/examItemPkg/riskPkgsByRisk";//根据风险获取风险加加项包
    public final static String ExamitemPackage_Delpkg = "/examItemPkg/delpkg";//删除加项包GET
    public final static String ExamitemPackage_BasicPkgs = "/examItemPkg/basicPkgs";//获取所有基础加项包 及 所有体检项目
    public final static String ExamitemPackage_CalculateItemTotalPrice = "/resource/calculateItemTotalPrice";//计算单项价格的总和，不包括调整价格
    public final static String ExamitemPackage_MealItems = "/resource/mealItems";//获取单项，包括冲突项

    /************** meal *****************/
    public final static String Meal = "/resource/meal"; // 套餐
    public final static String Meal_DeleteMeal = "/resource/removeMeal"; // 套餐/meal/{mealId}
    //	public final static String Meal_DeleteCustomizeMeal = "/resource/removeCustomizedMeal"; // 套餐/meal/{mealId} 接口废弃了
    public final static String Meal_CalculateBasicItemPrice = "/resource/calculateBasicItemPrice"; // 计算必选套餐项价格
    public final static String Meal_customizedMeals = "/resource/customizedMeals";// 根据体检中心标识、体检单位标识查询定制化套餐
    public final static String Meal_MealSaveAs = "/resource/mealSaveAs";// 复制套餐,该接口已被废弃
    public final static String Meal_Meals="/resource/meals/";// 官方套餐/meals/{hospitalId}
    public final static String Meal_CopyMeal="/resource/copyMeal";//新的复制套餐接口
    public final static String Meal_CopyMealList="/resource/copyMealList";//批量复制套餐
    public final static String Meal_ExportMealList="/resource/exportMealList";//套餐导出


    /************** item manager ***************/
    public final static String Item_Department = "/resource/examitem/department";
    public final static String Item_RemoveDepartment = "/resource/examitem/removeDepartment"; //GET删除科室
    public final static String Item_DepartmentItems = "/resource/examitem/departmentItems";
    public final static String Item_DepartmentReferencedCount = "/resource/examitem/departmentReferencedCount";
    public final static String Item_Departments = "/resource/examitem/departments";
    public final static String Item_LimitExamItem = "/limitExamItem";
    public final static String Item_LimitItems = "/limitItems";
    public final static String Item_CreateLimitItem = "/createLimitItem";
    public final static String Item_DeleteLimitItem = "/deleteLimitItem";
    public final static String Item_UpdateLimitItemName = "/updateLimitItemName";
    public final static String Item_ItemList = "/itemList";
    public final static String Item_AddLimitItems = "/addLimitItems";
    public final static String Item_ItemForSort = "/itemForSort";
    public final static String Item_SpeciesList = "/speciesList";
    public final static String Item_Itemsequence = "/itemsequence";
    public final static String Item_ReportItemList = "/reportItemList";
    public final static String Item_EditExamItem = "/editExamItem";
    public final static String Item_DeleteItems = "/deleteItems";
    public final static String Item_Match = "/match";
    public final static String Item_ItemWithSpecies="/itemWithSpecies";
    public final static String Item_SpeciesByType= "/speciesByType";
    public final static String Item_ChangeSpecies = "/changeSpecies";
    public final static String Item_ResumeItems = "/resumeItems";
    public final static String Item_EditExamItemRelation = "/editExamItemRelation";
    public final static String Item_Species = "/species";
    public final static String Item_ItemWithRelation = "/itemWithRelation";


    /************** examitemPackage *****************/
    public final static String Package_RiskExamitemPackage = "/examItemPkg/riskExamitemPackage"; //新增或修改自定义加项包
    /************** 总检报告 ***************/
    public final static String Report_AccountCount = "/report/accountCount";
    public final static String Report_SaveForword = "/report/saveForeword";
    public final static String Report_SaveReportBaseinfo = "/report/saveReportBaseinfo";
    public final static String Report_SaveSummary = "/report/saveSummary";
    public final static String Report_HistoryReport = "/report/historyReport";
    public final static String Report_ExportReport = "/report/exportReport";
    public final static String Report_DeleteReport = "/report/deleteReport"; // 参数/report/{reportId}
    public final static String Report_SummaryReport = "/report/summaryReport";
    public final static String Report_CreateSummaryReport = "/report/createSummaryReport";
    public final static String Report_GetTemplate = "/report/getTemplate";
    public final static String Report_SaveTemplate = "/report/saveTemplate";
    public final static String Report_DeleteTemplate = "/report/deleteTemplate"; // 参数
    // /report/{hospitalId}/{template}
    public final static String Report_UpdateTemplate = "/report/updateTemplate";
    public final static String Report_IsShowGroupReport = "/report/isShowGroupReport";

    /************** 体检报告 ********************************/
    public final static String ExamReport_CheckReport = "/report/doaudit";
    public final static String ExamReport_DoBatchReport = "/report/doBatchAudit";
    public final static String ExamReport_ReportList = "/report/list";
    public final static String ExamReport_Noreport = "/report/noreport";
    public final static String ExamReport_GetReport = "/report"; // 参数/report/{reportId}
    public final static String ExamReport_Auth = "/examreport/auth";
    public final static String ExamReport_BaseInfo = "/examreport/baseinfo"; // 参数/examreport/baseinfo/{id}/{hospitalId}
    public final static String ExamReport_Result = "/examreport/result"; // 参数/examreport/result/{id}/{hospitalId}
    public final static String ExamReport_Exceptional = "/examreport/exceptional";// 参数/examreport/exceptional/{id}/{hospitalId}
    public final static String ExamReport_Detail = "/examreport/detail";// 参数/examreport/detail/{id}/{hospitalId}
    public final static String ExamReport_HospitalName = "/examreport/hospitalname";// 参数/examreport/hospitalname/{id}/{hospitalId}
    public final static String ExamReport_SimpleExceptional = "/examreport/simpleexceptional"; // 参数/examreport/simpleexceptional/{id}/{hospitalId}
    public final static String ExamReport_SimpleResult = "/examreport/simpleresult"; // 参数
    // /examreport/simpleresult/{id}/{hospitalId}
    public final static String ExamReport_UnRead = "/examreport/unread";
    public final static String ExamReport_List = "/examreport/list";
    public final static String ExamReport_DownLoad = "/examreport/download"; // 参数
    // /examreport/download/{id}.pdf/{hospitalId}
    public final static String ExamReport_VerifyCode = "/examreport/verifyCode";//体检报告发送至邮件
    public final static String ExamReport_SendMail = "/examreport/sendMail";//体检报告发送至邮件

    /************************体检报告模板************************/
    public final static String ExamReportTpl_Group = "/groupTpl/group";//获取科室-分组列表内容
    public final static String ExamReportTpl_AddGroup = "/groupTpl/addGroup";//新增分组
    public final static String ExamReportTpl_EditGroup = "/groupTpl/editGroup";//编辑分组（名称）
    public final static String ExamReportTpl_DeleteGroup = "/groupTpl/deleteGroup";//删除分组GET
    public final static String ExamReportTpl_BindGroup = "/groupTpl/bindGroup";//科室绑定分组
    public final static String ExamReportTpl_Item = "/reportTplItem/item";//获取所有项目页面
    public final static String ExamReportTpl_GetTpl = "/tpl/getExamItemTpl";//获取科室-模板页面
    public final static String ExamReportTpl_AddTpl = "/tpl/addExamItemTpl";//新增模板
    public final static String ExamReportTpl_DelTpl = "/tpl/deleteExamItemTpl";//删除模板
    public final static String ExamReportTpl_EditTpl = "/tpl/editExamItemTpl";//编辑模板
    public final static String ExamReportTpl_ItemBindTpl = "/tpl/itemBindTpl";//项目绑定模板
    public final static String ExamReportTpl_DeptBindTpl = "/tpl/deptBindTpl";//科室绑定模板
    public final static String ExamReportTpl_Sort = "/tpl/sortOfExamReport";//排序
    public final static String ExamReportTpl_DeptItems = "/tpl/deptItems";//获取部门下所有的体检大项

    /******************************************5.RESOURCE END************************************************/


    /******************************************6.COMPANY START************************************************/
    /************** company ***************/
    public final static String Comp_AccountCompanyList = "/accountCompanyList";
    public final static String Comp_AccountCompany = "/accountcompany"; // 添加挂账单位，/accountcompany/{id}
    public final static String Comp_RemoveAccountCompany = "/removeAccountcompany"; //GET删除挂账单位参数
    public final static String Comp_Company = "/company"; // 获取体检单位列表 如果体检中心为空，则获取客户经理关联所有体检单位.该接口前端没用到
    public final static String Comp_RemoveCompany = "/removeCompany";//GET删除体检单位
    public final static String Comp_AllCompanies = "/allCompanies";//该接口已被废弃
    public final static String Comp_AddCompany = "/addCompany";
    public final static String Comp_ExamCompanyInfoByHospital = "/examCompanyInfoByHospital";//该接口已被废弃
    public final static String Comp_CompaniesByName = "/companiesByName";//该接口已被废弃
    public final static String Comp_CompanyApplyingList = "/companyApplyingList"; // 传参数companyApplyingList/{hospitalId}
    public final static String Comp_UpdateCompanyApplyStatus = "/updateCompanyApplyStatus";
    public final static String Comp_UpdateCompanyDiscount = "/updateCompanyDiscount";
    public final static String Comp_UpdateMCompanyRelation = "/updateMCompanyRelation";
    public final static String Comp_HideCompany = "/hideCompany";//删除单位使用post请求
    public final static String Comp_SearchCompany = "/syncExamCompany/searchCompany";//查询常用单位
    public final static String Comp_CompanyInfo = "/syncExamCompany/companyInfo";//获取单位信息
    public final static String Comp_UpdateCompany = "/updateCompany";//更新单位信息
    public final static String Comp_SearchByName = "/searchByName";//新建单位时，查询输入的名称，在系统中是否存在
    /************** message ***************/
    public final static String Message_List = "/message/list";
    public final static String Message_ReadAll = "/message/readAll";
    public final static String Message_Read = "/message/read";
    public final static String Message_GetTemplate = "/getTemplate"; // 参数:/getTemplate/{hospitalId}/{dateBySelf}
    public final static String Message_Preview = "/preview";
    public final static String Message_BatchSendMsg = "/order/batchSendMsg"; // 订单管理批量发送短信
    /******************************************6.COMPANY END************************************************/

    /******************************************7.ACCOUNT START************************************************/
    /************** account ***************/
    public final static String Login = "/login"; // POST
    public final static String IsLogin = "/islogin";
    public final static String Logout = "/logout"; // GET
    public final static String AccountInfo = "/accountInfo"; // GET
    public final static String UserNameExist = "/userNameExist"; // POST
    public final static String ValidateLogin = "/validateLogin"; // GET
    public final static String CallSession = "/callsession"; // GET
    public final static String RoleHospital = "/roleInHospital"; // GET
    public final static String RoleMembers = "/rolemembers"; // GET
    public final static String SaveRole = "/saverole"; // POST
    public final static String Account_FindCustomer = "/account/findcustomer"; // 获取用户账户关系信息
    // POST
    public final static String Account_Customer = "/account/customer"; // POST 接口已经被删除
    public final static String Account_RemoveCustomer = "/account/removeCustomer"; //GET
    public final static String Account_BasicAcctRelation = "/account/update/BasicAcctRelation"; // POST
    public final static String Account_AcctRelations = "/account/update/AcctRelations"; // POST
    public final static String Account_Download = "/account/download"; // GET
    public final static String Account_Confirm = "/account/upload/confirm"; // POST
    public final static String Account_ImportProgress = "/account/importProgress"; // GET
    public final static String Account_TableHead = "/account/tablehead"; // GET
    public final static String Account_GroupId = "/account/groupId"; // GET
    public final static String Account_FindFailRecord = "/account/findFailRecord"; // POST
    public final static String Account_FailRecord = "/account/failRecord"; // GET 接口被删除
    public final static String Account_RemoveFailRecord = 	"/account/removeFailRecord"; // POST
    public final static String Account_UpdatePwd = "/account/updatePwd"; // POST
    public final static String Account_ResetPwd = "/account/resetPwd"; // POST
    public final static String Account_lastGroup = "/account/lastGroup/"; // GET
    //	public final static String Account_FastAddAccount = "/account/fastAddAccount"; // POST
    public final static String Account_ForceImport = "/account/forceImport"; // POST
    public final static String Account_ExportAccountRecords = "/account/exportErrorAccountRecords"; // GET
    public final static String Account_UploadBatchModify = "/account/uploadBatchModify"; // POST
    public final static String Account_OperateContact = "/account/operateContact"; // POST
    public final static String Account_PrepareForUpload = "/account/prepareForUpload";// POST,
    // 预导入用户，模糊检索表头，并返回预览数据
    public final static String Account_CheckAccountInfoConflict = "/account/checkAccountInfoConflict"; // POST
    public final static String Account_ModifyAccount = "/account/modifyAccount"; //POST 急速预约添加用户

    /*************** manager *****************/
    public final static String Manag_List = "/manager/list"; // POST
    public final static String Manag_Accounting = "/manager/accounting"; // GET
    public final static String Manag_Recharge = "/manager/recharge"; // POST
    public final static String Manag_Save = "/manager/save"; // POST
    public final static String Manag_Remove = "/manager/remove"; // GET
    public final static String Manag_ReplaceOperate = "/manager/replaceOperate";
    public final static String Manag_CheckHospital = "/manager"; // 参数/{managerId}/{hospitalId}
    // GET
    public final static String Manag_UpdateInfo = "/manager/updateInfo"; // POST
    public final static String Manag_HospitalManager = "/manager/hospitalManager";//获取体检中心可用客户经理
    public final static String Manager_allowCreatePaymentQRCode = "/manager/allowCreatePaymentQRCode"; //GET 获取体检中心付款二维码


    /******************************************7.ACCOUNT END************************************************/

    // constant

    /************** hospital settings ***********************/
    public final static String Hos_SaveExamParameter = "/resource/saveExamParameter";
    public final static String Hos_HospitalSetting = "/resource/hospitalsetting"; // GET, POST
    public final static String Hos_SaveSmsParamSet = "/resource/saveSmsParamSet"; // 短信参数设置
    public final static String Hos_SaveMobileSet = "/resource/saveMobileSet"; // 客户手机端设置

    /************** hospital contact ***********************/
    public final static String HC_AddHospitalContactConfigs = "/addHospitalContactConfigs"; // POST
    public final static String HC_HospitalContact = "/hospitalContact"; // GET


    public final static String Hos_SaveHelpSet = "/resource/saveHelpSet";
    public final static String Hos_SaveJoinSet = "/resource/saveJoinSet";
    public final static String Hos_SaveDiscountSet = "/resource/saveDiscountSet";
    public final static String Hos_SaveDefaultSet = "/resource/saveDefaultSet";
    public final static String Hos_HospitalSms = "/resource/hospitalsms";
    public final static String Hos_Hospital = "/resource/hospital";
    public final static String Hos_Allhospitals = "/resource/allhospitals";
    public final static String Hos_HospitalsByAddress = "/resource/hospitalsByAddress";//列出平台中所有支持的体检中心
    public final static String Hos_GetHospitalCompanyByManager = "/getHospitalCompanyByManager";//获取医院crm客户经理/平台客户经理的单位列表
    public final static String Hos_GetHospitalCompanyByHospital = "/getHospitalCompanyByHospital";//管理员查询医院单位 - 体检报告管理


    /************** manage 接口 ***********************/
    public final static String Manage_Hospital = "/hospital"; // 获取体检中心列表 get
    public final static String Manage_HospitalList = "/getHospitalList"; // 获取体检中心列表

    /************** manage msg***********************/
    public final static String Manage_ListSMSSendRecords = "/listSMSSendRecords";//获取某个号码最近10天的发送记录
    public final static String Manage_ReSendSmsMessage = "/reSendSmsMessage";//重新发送短信发送记录中的某条短信



    /************** ops account ***********************/
    public final static String OPS_AccountManagePageInfo = "/accountManagePageInfo";
    public final static String OPS_UpdateAccount = "/updateAccount";
    public final static String OPS_ResetPwd = "/resetPwd";
    public final static String Manage_AccountInfo = "/accountInfo";


    /************** manage company ***********************/
    public final static String Manage_AddCompany = "/addCompany";//已废除
    public final static String Manage_EditCompany = "/editCompany";// GET, POST//已废除
    public final static String Manage_Company= "/company";//已废除


    /************** manage manager ***********************/
    public final static String Manage_ManagerList = "/manager/list";
    public final static String Manage_ManagerGain = "/manager/gain";
    public final static String Manage_ManagerSave = "/manager/save";
    public final static String Manage_ManagerRemove = "/manager/remove";
    public final static String Manage_ManagerResetPwd = "/manager/resetPwd";
    public final static String Manage_ManagerAccounting = "/manager/accounting";
    public final static String Manage_ManagerRecharge ="/manager/recharge";

    /************** manage hospital ***********************/
    public final static String Manage_HospitalSetting = "/hospitalsetting";

    /**************manage organization***********************/
    public final static String Manage_OrganizationList = "/organizationList";  //GET
    public final static String Manage_Organization = "/organization" ; //POST,GET
    public final static String Manage_ValidateSite = "/validateSite" ; //GET
    public final static String Manage_OrganizationSetting = "/organizationSetting";//GET ,POST
    public final static String Manage_DeletePeriodId = "/"; //${periodId} //DELETE
    public final static String Manage_SiteResource = "/siteResource"; //GET,POST
    public final static String Manage_ColorRule = "/colorRule"; //GET,POST
    public final static String Manage_WebCss = "/webCss"; //GET,POST
    public final static String Manage_SiteOpt = "/siteOpt"; //POST
    public final static String Manage_DelColorTem = "/delColorTem"; //DELETE
    public final static String Manage_Province = "/province"; //GET
    public final static String Manage_City = "/city"; //GET
    public final static String Manage_District = "/district"; //GET
    public final static String Manage_ValidateUserName = "/validateUserName"; //GET
    public final static String Manage_AllocateHospital = "/allocateHospital"; //GET
    public final static String Manage_GetHospitalSms = "/hospitalsms"; //GET
    public final static String Manage_SaveHospitalSms = "/hospitalsms"; //POST
    public final static String Manag_CannelList = "/channelList";//获取所有的渠道商列表

    /************** page ***********************/
    public final static String WithDraw_CashAccountPage = "/cashAccountPage";
    public final static String WithDraw_SaveWithdraw = "/saveWithdraw";
    public final static String Main_MainPage = "/mainPage";
    public final static String Mobile_MobileMainPage = "/mobileMainPage";//渠道商首页
    public final static String Mobile_LoadUserCardPage = "/loadUserCardPage";//首页 -> 体检预约 -> 体检卡页面
    public final static String Main_UserCenterCardPage = "/userCenterCardPage";//个人中心 -体检卡
    public final static String Mobile_UserCenterPage = "/userCenterPage";//个人中心页面
    public final static String Hos_HospitalDetailPage = "/hospitalDetailPage";//体检中心详情页
    public final static String Mobile_MobileMealListPage = "/mobileMealListPage";//套餐列表页

    /************** main resource***********************/
    public final static String Hos_HospitalPage = "/hospitalPage";
    public final static String Hos_SearchHospital = "/resource/searchHospital";
    public final static String Hos_HospitalPageM = "/hospitalPageM";
    public final static String Hos_GetPromptPageUrl = "/hospital/getPromptPageUrl";//获取手机端改项提示链接
    public final static String Hos_HospitalSettingByMeal = "/resource/hospitalSettingByMeal";//c端选择体检日期时，获取信息

    /************** main account***********************/
    public final static String Account_Profile = "/profile";
    public final static String Account_ValidateLogin = "/validateLogin";
    public final static String Account_ExamUserPage = "/examUserPage";
    public final static String Account_CheckIdCard = "/checkIdCard";
    public final static String Account_PersonalPage = "/personalPage";
    public final static String Account_UpdatePwdPage = "/updatePwdPage";
    public final static String Account_UpdatePwd1 = "/updatePwd";
    public final static String Account_UpdateAccount = "/updateAccount";
    public final static String Account_ExistMedicalUser = "/existMedicalUser";
    public final static String Account_ExaminerAdd= "/examiner/add";
    public final static String Account_ExaminerDelete ="/examiner/delete";
    public final static String Account_ImproveInfoPage = "/improveInfoPage";//mobile 的个人信息
    public final static String Account_RegisterMobie = "/registerByOnlyMobile"; //手机端注册
    public final static String Account_CheckRepeatedIdCard = "/checkRepeatedIdCard" ;//检查身份证号码是否重复
    public final static String Account_MobileValidationCode = "/mobileValidationCode"; //手机验证码发送
    public final static String Account_FindPwdStep3 = "/findpwdstep3";//找回密码中最后1个页面
    public final static String Account_GetArticleList = "/channelSite/getArticleList";
    /************** main resource***********************/
    public final static String Address_Address = "/address";
    public final static String Address_City = "/city";
    public final static String Address_CityList = "/citylist";//定位查询有体检中心的城市

    /************** mobile page 选择体检日期和体检人页**********************/
    public final static String Mobile_LoadSubmitOrderPage = "/loadSubmitOrderPage";//选择体检日期和体检人页
    public final static String Mobile_LoadHealtherListPage = "/loadHealtherListPage";//选择体检人
    public final static String Mobile_UpdatePhone= "/updatePhone";//更新关系表手机号



    /************** crm设置团检邮箱,main 团检申请+企业通道***********************/
    public final static String SendGroupApplicationEmail = "/sendEmail/sendGroupApplicationEmail";
    public final static String GetEnterpriseTel = "/enterprisePassage/getEnterpriseTel";


    /************** sms ***********************/
    public final static String Sms_GetTemplate= "/sms/getTemplate";
    public final static String Sms_Preview = "/sms/preview";

    /************** main survey***********************/
    public final static String Survey_ListSurvey = "/diagnosis/listSurvey";
    public final static String Survey_GetSurvey = "/diagnosis/survey";
    public final static String Survey_GetAccountSurvey = "/diagnosis/accountSurvey";
    public final static String Survey_ListAccountSurvey = "/diagnosis/listAccountSurvey";
    public final static String Survey_RemoveAccountSurvey ="/diagnosis/removeAccountSurvey";
    public final static String Survey_GetQuestion= "/diagnosis/questions";
    public final static String Survey_SaveAnswer = "/diagnosis/saveAnswers";
    public final static String Survey_FinishSurvey = "/diagnosis/finishSurvey";
    public final static String Survey_EvaluateReport = "/diagnosis/evaluateReport";
    /************** main diagnosis智能推荐***********************/
    public final static String Disgnosis_EvaluateReport = "/diagnosis/evaluateReport"; //GET评估报告详情
    public final static String Disgnosis_ListRiskPackages = "/diagnosis/listRiskPackages"; //GET列出风险包
    public final static String Disgnosis_ListEvaluateReport = "/diagnosis/listEvaluateReport"; //GET列出评估报告，没看到调用方式

    /************** new company *********************/
    public final static String NewCompany_SearchCompany = "/syncExamCompany/searchCompany";
    public final static String NewCompany_CompanyInfo = "/syncExamCompany/companyInfo";
    public final static String NewCompany_Pigeonhole = "/syncExamCompany/pigeonhole";//接口已废除
    public final static String NewCompany_RecoverCompany = "/syncExamCompany/recoverCompany";//接口已废除
    public final static String NewCompany_SearchHisCompany = "/syncExamCompany/hisExamCompany";
    public final static String NewCompany_saveCompany = "/syncExamCompany/saveCompany";//新建单位（接口已废弃）
    public final static String NewCompany_ExamCompany ="/syncExamCompany/examCompany";//根据内网单位创建CRM单位（接口已废弃）
    public final static String NewCompany_UpdateCompany="/syncExamCompany/updateCompany";//更新单位
    public final static String NewCompany_AddCompany = "/syncExamCompany/addCompany";//新建单位
    public final static String NewCompany_AddCrmCompanyByHisCompany = "/syncExamCompany/addCrmCompanyByHisCompany";//根据内网单位创建CRM单位
    public final static String NewCompany_ArchvingCompany = "/syncExamCompany/archvingCompany";//归档单位
    public final static String NewCompany_UnArchvingCompany = "/syncExamCompany/unarchivingCompany";//加入常用单位列表

    /************** ops login ***********************/
    public final static String OPS_LOGIN="/user/login";
    public final static String OPS_LOGOUT="/user/logout";
    public final static String OPS_USER="/sys/menu/user";


    /************** crm问卷管理 ***********************/
    public final static String Survey_GetSurveyByHospitalId = "/getSurveyListByHospitalId"; //获取医院的问卷
    public final static String Survey_GetAccountSurveyList = "/getAccountSurveyList"; //获取答卷记录

    /*********************OPS-Company**********************/
    public final static String OPS_ChannelList="/company/channel/channelList";//OPS登录
    public final static String OPS_ChannelCompanyList="/company/channel/companyList";//OPS渠道单位列表
    public final static String OPS_PlatformCompanyList="/company/platform/list";//OPS渠道单位列表
    public final static String OPS_AddChannelCompany="/company/channel/addCompany";//OPS新增渠道单位
    public final static String OPS_AddPlatFormCompany="/company/platform/addCompany";//OPS新增平台单位
    public final static String OPS_cCompanyInfo="/company/channel/companyInfo";//OPS渠道单位详情
    public final static String OPS_UpdateChannelCompany="/company/channel/update";//OPS更新渠道单位
    public final static String OPS_pCompanyInfo="/company/platform/companyInfo";//OPS平台单位详情
    public final static String OPS_UpdateChannelANdManager="/company/platform/updateChannelAndManager";//OPS更新渠道单位
    public final static String OPS_UpdateCompanyAndHospApply="/company/platform/updateCompanyAndHospApply";//OPS更新渠道单位

    // constant
    public final static String UNIQUE_SUBMIT_TOKEN = "unique-submit-token";
    public final static String X_Auth_Mytijian_Token = "x-auth-mytijian-token";

    public final static List<String> CheckTokenActionList = new ArrayList<String>(Arrays.asList(Order_BatchOrder,//订单
            Order_OrderBatch, Order_RevokeOrder, Order_DeleteOrder, Order_OrderForNotExport, Order_ChangeExamDate,Order_ManuallyRefund,//订单
            Order_TempUpdateMysqlAndMongoOrder, MainOrder_Book, Order_ChangeExamItem,NoLoginBook,MainRevokerOrder,MainDeleteOrder,//订单
            Main_OrderPay,//支付
            Payment_ManaualRefund,
            Card_DistributeCard, Card_RecoverBalance,Card_EditExpiredDate,Card_RevocCard,Card_CleanCardRecord,Card_SendEntryCard,//卡
            CountHosp_Config,CountHosp_Reset,CountComp_Config,CountComp_Period,//人数
            Meal,Meal_CopyMeal,Meal_CopyMealList,//资源
            Manag_Recharge,WithDraw_SaveWithdraw,
            Account_UpdatePwd1,Account_ExaminerDelete,
            Account_BasicAcctRelation,Account_UpdateAccount,Account_Customer,Account_ModifyAccount,Account_ExaminerAdd,
            Manage_ManagerRecharge,OPS_LOGIN,Manage_ManagerList,
            Manage_EditCompany,Comp_UpdateCompany,Comp_AddCompany,NewCompany_AddCompany,NewCompany_AddCrmCompanyByHisCompany,NewCompany_UpdateCompany,
            Comp_ExamCompanyInfoByHospital,Item_EditExamItem,Card_UpdateCardExpireDate,OpsOrder_RevokeOrder,
            OPS_AddPrepaymentRecord,OPS_UpdatePrepaymentRecord,OPS_AddSettlementPayRecord,OPS_AuditHospitalPlatformBill,
            OpsOrder_ChangeExamDate,Main_OrderBookPay,
            Payment_RevokePaymentOrder,Mobile_ReplaceExamItem,
            Login,Account_MobileValidationCode
    ));

    public final static List<String> SetTokenActionList = new ArrayList<String>(Arrays.asList(Order_BatchOrder,
            Order_OrderBatch, Order_RevokeOrder, Order_DeleteOrder, Order_OrderForNotExport, Order_ChangeExamDate,Order_ChangeExamItem,//订单
            Order_TempUpdateMysqlAndMongoOrder,Order_ManuallyRefund, //订单
            MainRevokerOrder,MainDeleteOrder,MainOrder_Book, NoLoginBook,//订单
            Main_OrderPay,//支付
            Payment_ManaualRefund,
            Card_DistributeCard, Card_RecoverBalance,Card_EditExpiredDate,Card_RevocCard,Card_CleanCardRecord,Card_SendEntryCard,//卡
            CountHosp_Config,CountHosp_Reset,CountComp_Config,CountComp_Period,//人数
            Meal,Meal_CopyMeal,Meal_CopyMealList,//资源
            Manag_Recharge, WithDraw_SaveWithdraw,ValidateLoginAddToken,
            Account_BasicAcctRelation,
            Account_UpdatePwd1,Account_ExaminerAdd,Account_ExaminerDelete,
            Account_UpdateAccount,Account_Customer,Account_ModifyAccount,
            Manage_AccountInfo,Manage_ManagerRecharge,Manage_ManagerList,
            Comp_UpdateCompany,Comp_AddCompany,NewCompany_AddCompany,NewCompany_AddCrmCompanyByHisCompany,NewCompany_UpdateCompany,
            Comp_ExamCompanyInfoByHospital ,Manage_EditCompany,Item_EditExamItem,Card_UpdateCardExpireDate,OpsOrder_RevokeOrder,
            OPS_AddPrepaymentRecord,OPS_UpdatePrepaymentRecord,OPS_AddSettlementPayRecord,OPS_AuditHospitalPlatformBill,
            OpsOrder_ChangeExamDate,Main_OrderBookPay,
            Payment_RevokePaymentOrder,Mobile_ReplaceExamItem,
            IsLogin,Account_Profile,OPS_LOGIN,Login,Account_MobileValidationCode
    ));//OPS_LOGIN


    public final static List<String> SetAuthTokenList = new ArrayList<>(Arrays.asList(Account_MobileValidationCode,Login,Account_Profile,ValidateLogin,Account_GetArticleList,MainOrder_Book,MainRevokerOrder,NoLoginBook,Mobile_MobileOrderDetailsPage,Main_OrderBookPay,WithDraw_SaveWithdraw,BackLog,Order_ChangeExamItem,Main_OrderPay,
            Main_PayPageV2,Main_PaymentPage,Invoice_OrderInvoiceApply,Invoice_GetInvoiceApply,WithDraw_SaveWithdraw,WithDraw_CashAccountPage,LoadSubmitOrderPageNoLogin,MainDeleteOrder,Mobile_ReplaceExamItem,Account_ExaminerAdd,Account_ExaminerDelete,MainQueryOrder,ValidateLoginAddToken,Payment_CreatePaymentOrder,Payment_GetPaymentOrder,Coupon_CardAndCoupon,NoLoginPayPageV2,Order_GetOrder));

    public final static List<String> CheckauthTokenList = new ArrayList<>(Arrays.asList(Account_MobileValidationCode,Login,MainOrder_Book,MainRevokerOrder,NoLoginBook,Mobile_MobileOrderDetailsPage,Main_OrderBookPay,WithDraw_SaveWithdraw,
            BackLog,Order_ChangeExamItem,Main_OrderPay,
            Main_PayPageV2,Main_PaymentPage,Invoice_OrderInvoiceApply,Invoice_GetInvoiceApply,WithDraw_SaveWithdraw,WithDraw_CashAccountPage,LoadSubmitOrderPageNoLogin,MainDeleteOrder,Mobile_ReplaceExamItem,Account_ExaminerAdd,Account_ExaminerDelete,MainQueryOrder,ValidateLoginAddToken,Payment_CreatePaymentOrder,Payment_GetPaymentOrder,Coupon_CardAndCoupon,NoLoginPayPageV2,Order_GetOrder));

    // response
    public final static String STATUSCODE = "statuscode";
    public final static String MESSAGE = "message";

}
