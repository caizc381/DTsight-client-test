package com.tijiantest.util.pagination;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;

import com.tijiantest.util.pagination.Page.CountMethod;


/**
 * This class use for combine collection output and row count together, mostly
 * in service interface
 * 
 * @author twu
 * 
 * @param <T>
 */
public class PageView<T> implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6839969541735946792L;

	private Collection<T> records;
	private Page page;
	public PageView(){	
	}
	
	public PageView(Collection<T> records, Page page){
		this.page = page;
		setRecords(records);
	}

	public Collection<T> getRecords() {
		return records;
	}

	public void setRecords(Collection<T> records) {
		int size = records.size();
		if (this.page != null && this.page.getCountMethod() == CountMethod.NextPage) {
			if (size>this.page.getPageSize()) {
				Iterator<T> iter = records.iterator();
				while (iter.hasNext()) {
					iter.next();
				}
				iter.remove();
			}

			int currentPage = this.page.getCurrentPage();
			if (currentPage < 1) {
				currentPage = 1; 
			}
			this.page.setRowCount((currentPage-1)*this.getPage().getPageSize()+size);
		}
		this.records = records;
	}

	public Page getPage() {
		return page;
	}

	public void setPage(Page page) {
		this.page = page;
	}

}
