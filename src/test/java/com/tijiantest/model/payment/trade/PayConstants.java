package com.tijiantest.model.payment.trade;


public interface PayConstants {
	
	interface PayChannel {
		String Alipay = "alipay";
		String Wxpay  = "wxpay";
	}
	
	interface CallbackType {
		/**<pre> 
		 *
		 * 第三方支付点对点通知
		 * 
		 * 服务器点对点通知，即第三方(支付宝、微信)服务器直接发送通知到我方服务器，用户无感知。
		 * 
		 * 由于点对点通知包含完整数据，所以本次交易系统改造支付凭证，均以点对点通知为准。
		 * 
		 * 点对点通知在用户完成支付后马上会发起，一般在1秒内即可完成处理。
		 * 
		 */
		String Notify = "notify";
		
		/**<pre>
		 * 
		 * 第三方支付重定向通知
		 * 
		 * 即第三方支付完成后、用户在第三方平台点击(返回商户、完成、取货)等按钮的时候，第三方平台重新跳转到mytijian地址。
		 * 
		 * 重定向通知不包含完整数据(如收付款方信息)，不作为业务凭据使用，仅作页面跳转使用(即跳转到支付成功或支付失败页面)
		 * 
		 * 重定向通知往往在点对点通知之后出现，会有5秒以上的延时(也是第三方支付提供商为避免通知同时到达)
		 * 
		 * 目前只有支付宝有重定向通知，微信支付则没有。
		 */
		String Redirect = "rec";
	}
	
	interface OrderType {
		int MytijianOrder = 1;
		int RechargeOrder = 2;
		int PaymentOrder = 3;
	}
	
	interface TradeStatus {
		int Created = 1;
		int Paying = 2;
		int Successful =3;
		int Failed = 4;
		int Canceled = 5;
	}
	
	interface TradeType {
		int pay = 1;
		int refund = 2;
	}
	
	interface Apis {
		/**
		 * 支付宝在线支付网关
		 */
		String alipayGateway= "https://mapi.alipay.com/gateway.do?";
		/**
		 * 支付宝操作统一网关
		 */
		String alipayOpenapiGateway = "https://openapi.alipay.com/gateway.do";
		
		// 微信统一下单接口
		String WxpayUnifyedOrder = "https://api.mch.weixin.qq.com/pay/unifiedorder";
		// 微信账单下载接口
		String WxpayBillDownload = "https://api.mch.weixin.qq.com/pay/downloadbill";
		// 微信扫码支付接口
		String WxpayScan = "https://api.mch.weixin.qq.com/pay/micropay";
		// 微信扫码支付结果查询
		String WxpayScanQuery = "https://api.mch.weixin.qq.com/pay/orderquery";
	}
	
	interface PayMethod {
		int Card = 1;
		int Balance = 2;
		int Alipay = 3;
		int Wxpay = 4;
		int AlipayScan = 5;
		int WxpayScan = 6;
		int OfflinePay = 7;
		int OnlinePay = 8;
		int ParentCard = 9;
		int Coupon = 10;
		int WxApp = 13;
	}
	
	interface PayMethodBit {
		// 2  
		int CardBit = 1 << PayMethod.Card;
		// 4  
		int BalanceBit = 1 << PayMethod.Balance;
		// 8   
		int AlipayBit = 1 << PayMethod.Alipay;
		// 16
		int WxpayBit = 1 << PayMethod.Wxpay;
		// 32
		int AlipayScanBit = 1 << PayMethod.AlipayScan;
		// 64
		int WxpayScanBit = 1 << PayMethod.WxpayScan;
		// 128
		int OfflinePayBit = 1 << PayMethod.OfflinePay;
		// 256
		int OnlinePayBit = 1 << PayMethod.OnlinePay;
		// 512
		int ParentCardBit = 1 << PayMethod.ParentCard;
		//1024
		int CouponBit = 1 << PayMethod.Coupon;

		int WxAppBit = 1<<PayMethod.WxApp;
	}
	
	
	interface NextActions {
		/**
		 * 支付宝表单提交
		 */
		String AlipaySubmitFrom = "ALIPAY_SUBMIT_FROM";
		String WxpayJSAPI = "WXPAY_JS_API";
		String AlipayQueryStatus = "ALIPAY_QUERY_STATUS";
		String WxpayQueryStatus = "WXPAY_QUERY_STATUS";
	}
	
}
