package com.tijiantest.util.pagination;
//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import java.io.Serializable;
import java.util.Arrays;


import java.io.Serializable;
import java.util.Arrays;

public class Page implements Serializable {
	private static final long serialVersionUID = -2635651952307048652L;
	private int rowCount = -1;
	private int currentPage = 0;
	private int pageSize = 2147483647;
	private int offset = 0;
	private String[] orderBys;
	private Page.CountMethod countMethod;

	public Page() {
		this.countMethod = Page.CountMethod.Count;
	}

	public Page(int currentPage, int pageSize) {
		this.countMethod = Page.CountMethod.Count;
		this.currentPage = currentPage;
		this.pageSize = pageSize;
	}

	public Page(int currentPage, int pageSize, String... orderBy) {
		this.countMethod = Page.CountMethod.Count;
		this.currentPage = currentPage;
		this.pageSize = pageSize;
		this.setOrderBys(orderBy);
	}

	public int getRowCount() {
		return this.rowCount;
	}

	public void setRowCount(int rowCount) {
		this.rowCount = rowCount;
	}

	public int getCurrentPage() {
		return this.currentPage;
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}

	public String[] getOrderBys() {
		return this.orderBys;
	}

	public void setOrderBys(String[] orderBys) {
		this.orderBys = orderBys;
	}

	public int getPageSize() {
		return this.pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public boolean hasMore() {
		if(this.countMethod == Page.CountMethod.Count) {
			if(this.currentPage < 0) {
				this.currentPage = 0;
				return true;
			} else {
				return this.pageSize * this.currentPage < this.rowCount;
			}
		} else {
			return this.pageSize < this.rowCount;
		}
	}

	public int hashCode() {
		boolean prime = true;
		byte result = 1;
		int result1 = 31 * result + this.currentPage;
		result1 = 31 * result1 + Arrays.hashCode(this.orderBys);
		result1 = 31 * result1 + this.pageSize;
		result1 = 31 * result1 + this.rowCount;
		return result1;
	}

	public boolean equals(Object obj) {
		if(this == obj) {
			return true;
		} else if(obj == null) {
			return false;
		} else if(this.getClass() != obj.getClass()) {
			return false;
		} else {
			Page other = (Page)obj;
			return this.currentPage != other.currentPage?false:(!Arrays.equals(this.orderBys, other.orderBys)?false:(this.pageSize != other.pageSize?false:(this.rowCount != other.rowCount?false:this.offset == other.offset)));
		}
	}

	public int getOffset() {
		return this.offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public Page.CountMethod getCountMethod() {
		return this.countMethod;
	}

	public void setCountMethod(Page.CountMethod countMethod) {
		this.countMethod = countMethod;
	}

	public static enum CountMethod {
		Count(1),
		NextPage(2);

		private int code;

		private CountMethod(int code) {
			this.code = code;
		}

		public int getCode() {
			return this.code;
		}

		public void setCode(int code) {
			this.code = code;
		}
	}
}
