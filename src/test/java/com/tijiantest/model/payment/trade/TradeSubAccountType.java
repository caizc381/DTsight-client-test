package com.tijiantest.model.payment.trade;
/**
 * 交易子账户类型
 * @author Administrator
 *
 */
public interface TradeSubAccountType {
	
	/**
	 * 客户经理余额账户
	 */
	public static final int TRADE_MANAGER_BALANCE_ACCOUNT = 1;
	/**
	 * 用户体检卡账户
	 */
	public static final int TRADE_CARD_ACCOUNT = 2;
	/**
	 * 用户支付宝支付账户
	 */
//	public static final int TRADE_THIRD_PARTY_ACCOUNT = 3;//去掉支付宝/微信子账号
	/**
	 * 用户余额账户
	 */
	public static final int TRADE_BALANCE_ACCOUNT = 5;
	/**
	 * 用户现场支付账户（废弃)
	 */
//	public static final int TRADE_LOCAL_PAY_ACCOUNT = 6;
	
	/**
     * 客户经理母卡账户
     */
    public static final int TRADE_PARENT_CARD_ACCOUNT = 7;


	/**
	 * 新第三方支付账户
	 */
//	public static final int TRADE_THREE_ACCOUNTS = 8;//去掉新三方子账号


	/**
	 * 优惠券支付账户
	 */
	public static final int TRADE_COUPON_ACCOUNT = 10;


	/**
	 * 授信账户
	 */
	public static final int TRADE_CREDIT_ACCOUNT = 9;

}
