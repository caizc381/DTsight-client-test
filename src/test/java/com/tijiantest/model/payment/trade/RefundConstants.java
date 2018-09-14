package com.tijiantest.model.payment.trade;

/**
 * Created by wangzhongxing on 2017/5/18.
 */
public interface RefundConstants {

    interface RefundStatus {
        Integer REFUND_SUCCESS = 1; // 退款成功
        Integer REFUNDING = 2; // 退款中
        Integer REFUND_FAILED = 3; // 退款失败
    }

    interface RefundScene {
        Integer REVOKE_ORDER_REFUND = 1;        // C端,manage,渠道商撤单
        Integer CRM_REVOKE_ORDER_REFUND = 2;    // CRM撤单
        Integer RETURN_ORDER_REFUND = 3;        // 深对接和浅对接回单
        Integer CHANGE_ORDER_REFUND = 4;        // 改项退款
    }

}
