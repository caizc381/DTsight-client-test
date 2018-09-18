package com.dtstack.base;

import org.apache.http.client.methods.CloseableHttpResponse;

import java.io.InputStream;
import java.util.Map;

public class HttpResult {

	private int code;
	private String body;
	private Map<String,String> header;
	private InputStream inputStream;
	
	public HttpResult(){}
	
	public HttpResult(int code,String body){
		this.code = code;
		this.body = body;
	}
	public HttpResult(int code,String body,Map<String,String>header){
		this(code,body);
		this.header = header;
	}
	public HttpResult(int code,String body,Map<String,String>header,InputStream inputStream){
		this(code,body,header);
		this.inputStream = inputStream;
	}
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public Map<String, String> getHeader() {
		return header;
	}
	public void setHeader(Map<String, String> header) {
		this.header = header;
	}

	public InputStream getInputStream() {
		return inputStream;
	}

	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}
	
	
	
}
