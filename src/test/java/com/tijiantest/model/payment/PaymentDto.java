package com.tijiantest.model.payment;

import java.util.List;

import com.tijiantest.model.account.Account;
import com.tijiantest.model.order.Order;
import com.tijiantest.model.payment.invoice.Invoice;

public class PaymentDto {
	private Integer orderId;
	private List<Integer> idcards;
	private Integer selectedCardId;
	private boolean useBalance;

	private Invoice invoice;
	private Integer payType;
	private String site = "";
	
	private Order order;
	private Account currentAccount;
	private Integer treadeType;
	
	private boolean sendMessage = true;
	private boolean updateMongo = true;
	
	public boolean isUpdateMongo() {
		return updateMongo;
	}

	public void setUpdateMongo(boolean updateMongo) {
		this.updateMongo = updateMongo;
	}

	public boolean isSendMessage() {
		return sendMessage;
	}

	public void setSendMessage(boolean sendMessage) {
		this.sendMessage = sendMessage;
	}

	public Order getOrder() {
		return order;
	}

	public void setOrder(Order order) {
		this.order = order;
	}

	public Account getCurrentAccount() {
		return currentAccount;
	}

	public void setCurrentAccount(Account currentAccount) {
		this.currentAccount = currentAccount;
	}

	public Integer getTreadeType() {
		return treadeType;
	}

	public void setTreadeType(Integer treadeType) {
		this.treadeType = treadeType;
	}

	public Integer getOrderId() {
		return orderId;
	}

	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}

	public List<Integer> getIdcards() {
		return idcards;
	}

	public void setIdcards(List<Integer> idcards) {
		this.idcards = idcards;
	}

	public Integer getSelectedCardId() {
		return selectedCardId;
	}

	public void setSelectedCardId(Integer selectedCardId) {
		this.selectedCardId = selectedCardId;
	}

	public boolean isUseBalance() {
		return useBalance;
	}

	public void setUseBalance(boolean useBalance) {
		this.useBalance = useBalance;
	}

	public Invoice getInvoice() {
		return invoice;
	}

	public void setInvoice(Invoice invoice) {
		this.invoice = invoice;
	}

	public Integer getPayType() {
		return payType;
	}

	public void setPayType(Integer payType) {
		this.payType = payType;
	}

	public String getSite() {
		return site;
	}

	public void setSite(String site) {
		this.site = site;
	}

}
