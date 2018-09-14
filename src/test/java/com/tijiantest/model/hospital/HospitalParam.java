package com.tijiantest.model.hospital;

public class HospitalParam{

	public final static String ENABLE_DATE_PERIOD = "enable_date_period"; //'预约是否可选时间段'
	public final static String SHOWI_ITEM_PRICE = "show_item_price";  // '是否可见单项价格',
	public final static String CALCULATOR_SERVICE = "calculator_service";  //'价格计算调用的接口'
	public final static String DELIVERY_PRICE = "delivery_price";  //'寄送费用'
	public final static String BASIC_MEAL_ID = "basic_meal_id";   //'基础套餐标识'
	public final static String SHOW_EXAM_REPORT = "show_exam_report";//'默认报告信息是否显示-0:隐藏，1:显示'
	public final static String SHOW_INVOICE = "show_invoice";  //'默认发票信息是否显示-0:隐藏，1:显示'
	public final static String PREVIOUS_BOOK_DAYS = "previous_book_days"; //'提前几天预约'
	public final static String PREVIOUS_BOOK_TIME = "previous_book_time"; //'提前预约时间点'
	public final static String PREVIOUS_EXPORT_DAYS = "previous_export_days"; //'提前几天导出'
	public final static String PREVIOUS_EXPORT_TIME = "previous_export_time";  //'提前导出时间点'
	public final static String VIP_PRICE = "vip_price";  //'VIP订单的最低标准'
	public final static String AUTO_RELEASE_DAYS = "auto_release_days";  //'提前几天预留名额自动释放'
	public final static String SERVICE_TEL = "service_tel";  //'客服电话'
	public final static String TECHNICAL_TEL = "technical_tel";  //'技术支持电话'
	public final static String EXAM_START_TIME = "exam_start_time";  //'体检开始时间'
	public final static String EXAM_END_TIME = "exam_end_time";  //'最晚到检时间'
	public final static String AUTO_CONFIG_ORDER = "auto_confirm_order";  //'是否自动确认订单-0:手动，1:自动'
	public final static String COOPERATE_COMPANY = "cooperate_company"; //'对接厂商'
	public final static String COOPERATE_TYPE = "cooperate_type";  // '对接方式-0无缝对接，1：有缝对接'
	public final static String AUTO_EXPORT_ORDER = "auto_export_order"; //'是否自动确认订单-0:手动，1:自动'
	public final static String ONLY_LOCAL_PAY = "only_locale_pay";  //'散客代预约只能现场支付'
	public final static String SEND_EXAM_SMS = "send_exam_sms";  //'是否发送检前短信-0:不发，1:发送'
	public final static String SEND_EXAM_SMS_DAYS = "send_exam_sms_days";  // '提前几天发送'
	public final static String SEND_EXAM_SMS_TIME = "send_exam_sms_time";  //'提前发送时间点'
	public final static String REFUND_REFUSED_ITEM = "refund_refused_item";  //'拒检项目是否退款'
	public final static String SUPPORT_EXT_DISCOUNT = "support_ext_discount"; //'是否支持加项折扣'
	public final static String GUEST_OFFLINE_COMP_ALIAS = "guest_offline_comp_alias"; //'散客现场付款的体检单位名称'
	public final static String GUEST_ONLINE_COMP_ALIAS = "guest_online_comp_alias"; //'散客挂账的体检单位名称'
	public final static String LOCAL_PAY = "local_pay";  //'是否支持现场支付 0：否 1：是',
	public final static String ALLOW_ADJUST_PRICE = "allow_adjust_price";  //'允许调整价格，0：不允许，1：允许',
	public final static String IS_SEND_MESSAGE = "is_send_message";  //'预约成功是否向客户发送短信 0:否、1：是',
	public final static String RESERVE_DAY_AVAILABLE = "reserve_day_available"; //'单位预留默认值 0:仅预留日可约、1：非预留日可约'
	public final static String M_GUEST_COMP_ALIAS = "m_guest_comp_alias";   //'每天健康',
	public final static String MANUAL_EXPORT_ORDER = "manual_export_order"; //'是否可以手动导出订单到体检中心，0：不可用，1：可以'
	public final static String EXPORT_WITH_XLS = "export_with_xls";  //'是否要导出为xls 1：需要 0：不需要',
	public final static String MAKE_OUT_INVOICE = "make_out_invoice"; //'是否开票，0:不开票，1:开票',
	public final static String INVOICE_REQUIRED = "invoice_required"; //'开票要求，0:普通，1:高',
	public final static String MOBILEFIELDORDER = "mobileFieldOrder";  //'0未开启 1开启',
	public final static String ACCOUNT_PAY = "account_pay";   //'是否支持账户支付 0：否 1：是',
	public final static String ALI_PAY = "ali_pay";   // '是否支持支付宝支付 0：否 1：是',
	public final static String WEIXIN_PAY = "weixin_pay";   // '是否支持微信支付 0：否 1：是',
	public final static String ACCEPT_OFFLINE_PAY = "accept_offline_pay"; 
	public final static String SHOW_COMPANY_REPORT = "show_company_report";  //'医院对单位体检报告是否可见，默认可见',
	public final static String NEED_LOCAL_PAY = "need_local_pay";  //'C端现场支付订单是否需要收款确认 0：否  1：是',
	public final static String OPEN_PRINT_EXAM_GUIDE = "open_print_exam_guide"; //'是否开通crm打印导检单 0:未开通 1：开通',
	public final static String OPEN_QUEUE = "open_queue"; //'是否开通排队系统 0:未开通 1：开通'
	public final static String OPEN_SYNC_COMPANY = "open_sync_company";//是否开通单位同步 0:未开通 1：开通',
	public final static String OPEN_SYNC_MEAL = "open_sync_meal";//是否开通套餐同步 0：未开通 1：开通',
	public final static String IS_SMART_RECOMMEND = "is_Smart_Recommend"; //是否开通智能推荐
	public final static String IS_ADVANCE_EXPORT_COMPANY_ORDER = "is_advance_export_company_order";//单位是否可以提前导出订单
	public final static String EXAMREPORT_INTERVAL_TIME = "examreport_interval_time";//体检报告设置间隔时间对用户可见, 0:立即，1：1天，2：2天，以此类推
	public final static String OPEN_GROUP_EXAMREPORT = "open_group_examreport"; //是否开通团检报告
	public final static String SAME_DAY_ORDER_MAXIMUM = "same_day_order_maximum"; //用户在医院同一天预约次数上线
	public final static String SETTLEMENT_MODE = "settlement_mode";  //'单位结算方式默认 0:按项目、1：按人数'
	public final static String SETTLEMENT_OPEN = "settlement_open";//'0=不开启新结算功能 1=开启新结算功能',
	public final static String SETTLEMENT_TIME = "settlement_time"; //'新结算功能开启时间'
	public final static String PlatFormGuestDiscount = "platform_guest_discount";//平台散客折扣
	public final static String PlatFormCompDiscount = "platform_comp_discount";//平台单位折扣
	public final static String GuestOnlineCompDiscount = "guest_online_comp_discount";//个人网上预约折扣
	public final static String GuestOfflineCompDiscount = "guest_offline_comp_discount";//前台散客折扣
	public final static String HospitalCompDiscount = "hospital_comp_discount";//普通单位折扣
	public final static String RefundRule = "refund_rule"; //1:差价加项和退款,默认 2:全价加项和退款'
	public final static String PayTipText = "pay_tip_text";//手机支付时提示文字
	public final static String FastBookPayTipText = "fastbook_pay_tip_text";//极速预约提示文字


	
	
	
}
