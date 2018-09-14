package com.tijiantest.model.paylog;

/**
 *
 * @author linzhihao
 */
public interface PayLogThreadInfo {
	
	void setOperater(Integer Operator);
	void setOperaterType(Integer operaterType);
	void setTradeType(Integer type);
	void setOrderNum(String orderNum);
	void setTradeBatchNo(String tradeBatchNo);
	void setOrderId(Integer orderId);
	void setRemark(String string);
	
	String getTradeBatchNo();
	String getOrderNum();
	Integer getOrderId();
	Integer getOperaterType();
	Integer getOperater();
	Integer getTradeType();
	String getRemark();
}
