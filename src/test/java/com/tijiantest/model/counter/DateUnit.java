package com.tijiantest.model.counter;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class DateUnit implements Serializable{
	private static final long serialVersionUID = 4948945342441381258L;
	/**
	 * 预约日期
	 */
	private Date date;
	/**
	 * 当天是否可用
	 */
	private boolean enable;
	/**
	 * 单日小标识，如单位预设体检日、单项已满限制等等
	 */
	private Map<String, String> indicators;
	/**
	 * 可用的时间段Id列表
	 */
	private List<Integer> durationAvaliables;

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public boolean isEnable() {
		return enable;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}

	public Map<String, String> getIndicators() {
		return indicators;
	}

	public void setIndicators(Map<String, String> indicators) {
		this.indicators = indicators;
	}

	public List<Integer> getDurationAvaliables() {
		return durationAvaliables;
	}

	public void setDurationAvaliables(List<Integer> durationAvaliables) {
		this.durationAvaliables = durationAvaliables;
	}
}
