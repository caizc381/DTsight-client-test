package com.tijiantest.model.hospital;

import java.io.Serializable;

public class SmsTemplate implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6327609502964617027L;
	//模板ID
	private Integer id;
	//医院ID
	private Integer hospitalId;
	//模板名称
	private String name;
	//模板内容
	private String templateContent;
	private Integer customized;
	private String code;
	private String weixinMsgId;
	private int priority;
	private String url;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getHospitalId() {
		return hospitalId;
	}
	public void setHospitalId(Integer hospitalId) {
		this.hospitalId = hospitalId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getTemplateContent() {
		return templateContent;
	}
	public void setTemplateContent(String templateContent) {
		this.templateContent = templateContent;
	}
	public Integer getCustomized() {
		return customized;
	}
	public void setCustomized(Integer customized) {
		this.customized = customized;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getWeixinMsgId() {
		return weixinMsgId;
	}
	public void setWeixinMsgId(String weixinMsgId) {
		this.weixinMsgId = weixinMsgId;
	}
	public int getPriority() {
		return priority;
	}
	public void setPriority(int priority) {
		this.priority = priority;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}

	
}
