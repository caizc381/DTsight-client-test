package com.tijiantest.model.order;

import com.tijiantest.model.account.FileAccountImportInfo;

public class FastBookVo {
	private FileAccountImportInfo customer;
	private BookDto bookDto;
	
	public FileAccountImportInfo getCustomer() {
		return customer;
	}
	public void setCustomer(FileAccountImportInfo customer) {
		this.customer = customer;
	}
	public BookDto getBookDto() {
		return bookDto;
	}
	public void setBookDto(BookDto bookDto) {
		this.bookDto = bookDto;
	}
	
	
}
