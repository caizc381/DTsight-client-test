package com.tijiantest.model.order.snapshot;

import java.io.Serializable;
import java.util.Date;

public class ExamDateSnapshot implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3331227752599597318L;
	/**
	 * 体检日期
	 */
	private Date examDate;
	/**
	 * 体检时段id
	 */
	private Integer examTimeIntervalId;
	/**
	 * 体检时段名称
	 */
	private String examTimeIntervalName;
	
	public Date getExamDate() {
		return examDate;
	}
	public void setExamDate(Date examDate) {
		this.examDate = examDate;
	}
	public Integer getExamTimeIntervalId() {
		return examTimeIntervalId;
	}
	public void setExamTimeIntervalId(Integer examTimeIntervalId) {
		this.examTimeIntervalId = examTimeIntervalId;
	}
	public String getExamTimeIntervalName() {
		return examTimeIntervalName;
	}
	public void setExamTimeIntervalName(String examTimeIntervalName) {
		this.examTimeIntervalName = examTimeIntervalName;
	}
	
}
