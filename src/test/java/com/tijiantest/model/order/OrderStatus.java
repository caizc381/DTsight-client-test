package com.tijiantest.model.order;

public enum OrderStatus{

	/***未支付**/
	NOT_PAY(0),
	/**已经支付**/
	ALREADY_PAY(1),
	/***已预约**/
	ALREADY_BOOKED(2),
	/***体检完成**/
	EXAM_FINISHED(3),
	/***还未体检**/
	NOT_EXAM(4),
	/***已撤销**/
	REVOCATION(5),
	/***已删除**/
	DELETED(6),
	/***正在支付中**/
	PAYING(7),
	/***已关闭**/
	CLOSED(8),
	/***部分退款**/
	PART_BACK(9),
	/***导出失败**/
	EXPORT_FAIL(10),
	/***现场支付**/
	SITE_PAY(11);
	
	  // 定义私有变量
    private int nCode;

    // 构造函数，枚举类型只能为私有
     private OrderStatus( int _nCode) {
        this.nCode = _nCode;
    }

    @Override
    public String toString() {
        return String.valueOf (this.nCode );
    }

    
    public int intValue(){
    	return this.nCode;
    }
}
