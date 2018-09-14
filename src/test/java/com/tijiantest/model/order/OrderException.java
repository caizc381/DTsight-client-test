package com.tijiantest.model.order;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.tijiantest.util.exception.ExceptionWithCode;

public class OrderException extends Exception implements ExceptionWithCode{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */

	private Set<ExceptionItem> accoutSet = new HashSet<ExceptionItem>();

	private ExceptionType exceptType = null;
	
	public OrderException(){
	}
	
	public OrderException(ExceptionType exceptType ) {
		this.exceptType = exceptType;
	}
	
	public OrderException(ExceptionType exceptType ,String message) {
		super(message);
		this.exceptType = exceptType;
	}
	
	public OrderException(ExceptionType exceptType ,String message, Throwable t) {
		super(message, t);
		this.exceptType = exceptType;
	}
	
	public OrderException(ExceptionType exceptType, Throwable t) {
		super(t);
		this.exceptType = exceptType;
	}

	public Set<ExceptionItem> getAccoutSet() {
		return this.accoutSet;
	}
	
	public void addAccount(Integer accountId, Date examDate){
		this.accoutSet.add(new ExceptionItem(accountId, examDate));
	}
	
	public ExceptionType getExceptType(){
		return this.exceptType;
	}
	
	public enum ExceptionType {
		duplicate(1,"30天内有订单异常"), 
		same(2,"同一天有两个订单异常"), 
		cannotRevoke(3,"订单无法撤销"), 
		exported(4,"已经被导出的订单"), 
		cannotDelete(5,"订单不能被删除"), 
		cannotChangeItem(6,"不能改项目的订单"), 
		refundExcelError(7,"退单excel格式错误"),
		outOfTime(8, "订单过期"),
		cannotBacktoOrigin(9, "订单无法回滚到稳定状态"),
		cannotOperate(10, "订单无法操作"),
		genderError(11, "用户性别和套餐性别不一致"),
		exportOrderError(12, "导出订单到体检中心失败，详情请咨询系统管理员"),
		nopermission(13,"没有权限"),
	    queryOrderOutOfRange(14,"全选订单数量不能多于1000条"),
	    exportOrderStatusError(15, "订单状态错误，无法导出到体检中心"),
	    biggerThan1w5(16,"最多导出15000条数据"),
	    thereIsNoValidateOrder(17,"无可用数据导出"),
		orderStatusException(18,"订单状态有误");
		
		private int code;
		private String message;

		private ExceptionType(int code, String message) {
			this.code = code;
			this.message = message;
		}

		public int getCode() {
			return code;
		}

		public void setCode(int code) {
			this.code = code;
		}

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}

	}

	private static SimpleDateFormat formt = new SimpleDateFormat("yyyy-MM-dd");

	public static class ExceptionItem implements Serializable{
		
        private static final long serialVersionUID = 3757894716045358495L;
        private Integer accountId;
		private String examDate;
		
		public ExceptionItem(){}
		
		public ExceptionItem(Integer accountId, Date ed){
			this.accountId = accountId;
			this.examDate = formt.format(ed);
		}

		public Integer getAccountId() {
			return accountId;
		}

		public String getExamDate() {
			return examDate;
		}

        public void setAccountId(Integer accountId) {
            this.accountId = accountId;
        }

        public void setExamDate(String examDate) {
            this.examDate = examDate;
        }
		
	}

	@Override
	public int getCode() {
		return this.exceptType.getCode();
	}
	
}
