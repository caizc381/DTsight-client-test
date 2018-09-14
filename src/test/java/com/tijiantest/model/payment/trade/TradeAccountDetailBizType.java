package com.tijiantest.model.payment.trade;
/**
 * 子账户流水业务类型
 * @author Administrator
 *
 */
public interface TradeAccountDetailBizType {

	/**
	 * 支付
	 */
	public static final int PAYMENT = 1;
	/**
	 * 退款
	 */
	public static final int REFUND = 2;
	/**
	 * 充值
	 */
	public static final int RECHARGE = 3;
	/**
	 * 提现
	 */
	public static final int WITHDRAW = 4;
}
