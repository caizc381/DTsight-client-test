package com.tijiantest.model.paylog;

/**
 * 常量定义
 * @author linzhihao
 */
public interface PayConsts {
	
	/**
	 * 提交给第三方支付的订单号
	 */
	String OutOrderNoRegex = "^(\\d+)([AW])(\\d+)$";
	
	/**
	 * 交易类型
	 */
	interface TradeTypes {
		/**
		 * 订单支付
		 */
		int OrderPay = 1001;
		
		/**
		 * 订单改项支付
		 */
		int OrderChange = 1003;
		/**
		 * 订单撤销
		 */
		int OrderRevoke = 1005;
		
		/**
		 * 订单退款
		 */
		int OrderRefund = 1007;
		/**
		 * 回单退款
		 */
		int OrderCompleteRefund = 1008;	
		/**
		 * 订单关闭
		 */
		int OrderClose = 1009;
		
		/**
		 * 订单删除
		 */
		int OrderDelete = 1010;
		
		/**
		 * 充值
		 */
		int Recharge = 2000;		
		/**
		 * 提现
		 */
		int Withdraw = 3000;	
		/**
		 * 提现失败
		 */
		int WithdrawFailed = 3001;
		
		int SendCard = 5001;
		int CardRevoke = 5002;
		int CardRecoverBalance = 5003;
		int CardRecharge = 5005;
		int CreateManage = 5006;
		int CardSettlement = 5007;
		int CardProxyOrder = 5008;
	}
	
	static interface TradeBodyTypes {
		int Balance = 1;
		int Card = 2;
	}
	
	interface OperaterTypes {
		int User = 1;
		int Crm  = 2;
		int Mediator  = 3;
		int ScheduleJob = 4;
		int AlipayCallback = 5;
		int Manage = 6;
		int MyTijianService = 7;
	}
	
	interface TradeStatus {
		int Created = 1;
		int Successful  = 2;
		int Failed = 3;
		/**
		 * 交易作废
		 */
		int Canceled = 4;
	}
	
	interface TradeChannel {
		int Alipay = 1;
		int Weixin = 2;
	}
	
}
