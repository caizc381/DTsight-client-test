package com.tijiantest.model.payment.trade;

import java.io.Serializable;

public class PayResponse implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private boolean success;
	
	private String msg;
	
	/**
	 * 是否全部支付完成
	 */
	private boolean done;
	
	/**
	 * 客户端是否需要进行操作
	 */
	private boolean needNextAction;
	private String nextActionName;
	private String nextActionBizContext;
	/**
	 * 要求支付金额
	 */
	private long requireAmount;
	
	/**
	 * 已支付金额 (这个金额表示已经到账金额)
	 */
	private long successAmount;
	
	/**
	 * 等待支付金额
	 */
	private long payingAmount;
	
	
	private String tradeOrderNum;
	
	private String refOrderNum;
	private String refOrderNumVersion;
	private String refOrderBizContext;

	public boolean isDone() {
		return done;
	}

	public void setDone(boolean done) {
		this.done = done;
	}

	public boolean isNeedNextAction() {
		return needNextAction;
	}

	public void setNeedNextAction(boolean needNextAction) {
		this.needNextAction = needNextAction;
	}

	public String getNextActionName() {
		return nextActionName;
	}

	public void setNextActionName(String nextActionName) {
		this.nextActionName = nextActionName;
	}

	public String getNextActionBizContext() {
		return nextActionBizContext;
	}

	public void setNextActionBizContext(String nextActionBizContext) {
		this.nextActionBizContext = nextActionBizContext;
	}

	public long getRequireAmount() {
		return requireAmount;
	}

	public void setRequireAmount(long requireAmount) {
		this.requireAmount = requireAmount;
	}

	public long getSuccessAmount() {
		return successAmount;
	}

	public void setSuccessAmount(long successAmount) {
		this.successAmount = successAmount;
	}

	public long getPayingAmount() {
		return payingAmount;
	}

	public void setPayingAmount(long payingAmount) {
		this.payingAmount = payingAmount;
	}

	public String getTradeOrderNum() {
		return tradeOrderNum;
	}

	public void setTradeOrderNum(String tradeOrderNum) {
		this.tradeOrderNum = tradeOrderNum;
	}

	public String getRefOrderNum() {
		return refOrderNum;
	}

	public void setRefOrderNum(String refOrderNum) {
		this.refOrderNum = refOrderNum;
	}

	public String getRefOrderNumVersion() {
		return refOrderNumVersion;
	}

	public void setRefOrderNumVersion(String refOrderNumVersion) {
		this.refOrderNumVersion = refOrderNumVersion;
	}

	public String getRefOrderBizContext() {
		return refOrderBizContext;
	}

	public void setRefOrderBizContext(String refOrderBizContext) {
		this.refOrderBizContext = refOrderBizContext;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}
}
