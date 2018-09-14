package com.tijiantest.model.order;

public class OrderWithBatch extends Order{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * 现场支付
	 */
	private Boolean isLocalePay;
	
	/**
	 * 可改期
	 */
	private Boolean isChangeDate;
	
	/**
	 * 隐藏价格
	 */
	private Boolean isHidePrice;
	
	/**
	 * 卡代预约
	 */
	private Boolean isProxyCard;
	
	/**
	 * 可减少项目
	 */
	private Boolean isReduceItem;
	
	/**
	 * 是否是极速预约
	 */
	private Boolean isFastBook;

	public Boolean getIsLocalePay() {
		return isLocalePay;
	}

	public void setIsLocalePay(Boolean isLocalePay) {
		this.isLocalePay = isLocalePay;
	}

	public Boolean getIsChangeDate() {
		return isChangeDate;
	}

	public void setIsChangeDate(Boolean isChangeDate) {
		this.isChangeDate = isChangeDate;
	}

	public Boolean getIsHidePrice() {
		return isHidePrice;
	}

	public void setIsHidePrice(Boolean isHidePrice) {
		this.isHidePrice = isHidePrice;
	}

	public Boolean getIsReduceItem() {
		return isReduceItem;
	}

	public void setIsReduceItem(Boolean isReduceItem) {
		this.isReduceItem = isReduceItem;
	}

	public Boolean getIsProxyCard() {
		return isProxyCard;
	}

	public void setIsProxyCard(Boolean isProxyCard) {
		this.isProxyCard = isProxyCard;
	}

	public Boolean getIsFastBook() {
		return isFastBook;
	}

	public void setIsFastBook(Boolean isFastBook) {
		this.isFastBook = isFastBook;
	}

}
