package com.tijiantest.base;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieSpecProvider;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
//import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.impl.cookie.BestMatchSpecFactory;
import org.apache.http.impl.cookie.BrowserCompatSpecFactory;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.testng.Assert;

import com.alibaba.fastjson.JSON;

/**
 * 
 * @author huifang
 *
 */
public class MyHttpClient
{  
    /** 
     * 日志对象。 
     */  
      
    /** 
     * 默认HTTP请求客户端对象。 
     */  
    private HttpClient _httpclient;  
  
    protected final static Logger log = Logger.getLogger(MyHttpClient.class);
    /** 
     * 用户自定义消息头。 
     */  
    private Map<String, String> _headers;  
    private CookieStore  cookieStore = new BasicCookieStore();
    
    
    
    private  String token;

    private String xAutoToken;

    /** 
     * 使用默认客户端对象。 
     */  
	public MyHttpClient()   
    {  
        // 1. 创建HttpClient对象。  
        RequestConfig defaultConnectionConfig = RequestConfig.custom()
                .setConnectTimeout(BaseTest.connTimeout)
                .setSocketTimeout(BaseTest.socketTimeout)
//                .setConnectionRequestTimeout(BaseTest.socketTimeout)
                .build();
        _httpclient = HttpClientBuilder.create().setRedirectStrategy(new LaxRedirectStrategy())
                .setMaxConnPerRoute(BaseTest.maxConnPerRoute)
                .setMaxConnTotal(BaseTest.maxConnTotal)
                .setDefaultRequestConfig(defaultConnectionConfig)
                .build();
        log.info("create _httpclient ...");  
    }  
    private HttpClientContext getHttpClientContext()
    {
    	HttpClientContext httpContext = HttpClientContext.create();
    	Registry<CookieSpecProvider> registry = RegistryBuilder
        .<CookieSpecProvider> create()
        .register(CookieSpecs.BEST_MATCH, new BestMatchSpecFactory())
        .register(CookieSpecs.BROWSER_COMPATIBILITY,
            new BrowserCompatSpecFactory()).build();
    	httpContext.setCookieSpecRegistry(registry);
//    	BasicClientCookie cookie = new BasicClientCookie("openid", "oGXXyv-CVPLerXmOJsb4KUu0Nc50");
//    	cookie.setVersion(0);
//		cookie.setDomain("localhost");
//		cookie.setPath("/");
//		cookieStore.addCookie(cookie);
    	httpContext.setCookieStore(cookieStore);
    	return httpContext;
    }
    /** 
     * 调用者指定客户端对象。 
     *  
     * @param httpclient 
     */ 
	public MyHttpClient(Map<String, String> headers)   
    {  
        // 1. 创建HttpClient对象。  
    	RequestConfig defaultConnectionConfig = RequestConfig.custom()
                .setConnectTimeout(10000)
                .setSocketTimeout(2000)
                .build();
        _httpclient = HttpClientBuilder.create().setRedirectStrategy(new LaxRedirectStrategy())
                .setMaxConnPerRoute(5)
                .setMaxConnTotal(20)
                .setDefaultRequestConfig(defaultConnectionConfig)
                .build();
        this._headers = headers;  
        log.info("create _httpclient ...");  
    }  
  
	public synchronized HttpResult post(String uri, List<NameValuePair>params){
		return post(Flag.CRM,uri,params);
	}
    
    /** 
     * HTTP POST请求。 
     *  
     * @param url 
     * @param params 
     * @return 
     * @throws InterruptedException 
     */  
    public synchronized HttpResult post(Flag flag,String uri, List<NameValuePair>params)  
    {  
    	String url = "";
    	if(flag.equals(Flag.CRM))
    		url = BaseTest.defaulturl + "/action" + uri; 
    	if(flag.equals(Flag.MAIN))
    		url = BaseTest.mainurl + "/action" + uri;
    	if(flag.equals(Flag.MANAGE))
    		url = BaseTest.manageurl + "/action" + uri;
    	if(flag.equals(Flag.OPS))
    		url = BaseTest.opsurl + "/action" + uri;
    	if(flag.equals(Flag.MAIN_SECOND)){
    		url = BaseTest.mainurl + "/action" + uri;
    		params.add(new BasicNameValuePair("_p",""));
    		params.add(new BasicNameValuePair("_site", BaseTest.mainSecond));
    	}
    	if(flag.equals(Flag.CHANNEL))
    		url = BaseTest.channelurl + "/action" + uri;
    	
        // 2. 创建请求方法的实例，并定请求URL，添加请求参数。  
        HttpPost post = postForm(url, params); 
        
        setHttpHeaderToken(uri,post);
    
        log.info("create httppost : " + url);  
        
        HttpResult response =  invoke(post); 
        
        setHttpResponseToken(uri, response);
        
        checkResponseException(response,uri);
        return response;
    } 
    

    public synchronized HttpResult post(String uri, Map<String, String> params)  
    {
    	return post(Flag.CRM,uri,params);
    }
    /** 
     * HTTP POST请求。 
     *  
     * @param url 
     * @param params 
     * @return 
     * @throws InterruptedException 
     */  
    public synchronized HttpResult post(Flag flag,String uri, Map<String, String> params)  
    {  
    	String url = "";
    	if(flag.equals(Flag.CRM))
    		url = BaseTest.defaulturl + "/action" + uri; 
    	if(flag.equals(Flag.MAIN))
    		url = BaseTest.mainurl + "/action" + uri;
    	if(flag.equals(Flag.MANAGE))
    		url = BaseTest.manageurl + "/action" + uri;
    	if(flag.equals(Flag.OPS))
    		url = BaseTest.opsurl + "/action" + uri;
    	if(flag.equals(Flag.MAIN_SECOND)){
    		url = BaseTest.mainurl + "/action" + uri;
    		params.put("_p", "");
    		params.put("_site", BaseTest.mainSecond);
    	}
    	if(flag.equals(Flag.CHANNEL))
    		url = BaseTest.channelurl + "/action" + uri;
        // 2. 创建请求方法的实例，并指定请求URL，添加请求参数。  
        HttpPost post = postForm(url, params); 
        
        setHttpHeaderToken(uri,post);
    
        log.info("create httppost : " + url);  
        
        HttpResult response =  invoke(post); 
        
        setHttpResponseToken(uri, response);
        
        checkResponseException(response,uri);
        return response;
    }
    

    public synchronized HttpResult post(String uri,String jsonObj)  
    {  
    	return post(Flag.CRM,uri,jsonObj);
    }




	public synchronized HttpResult post(Flag flag,String uri,String jsonObj,String cookieKeyValue)
	{
		// 2. 创建请求方法的实例，并指定请求URL，添加请求参数。
		String url = "";
		if(flag.equals(Flag.CRM))
			url = BaseTest.defaulturl + "/action" + uri;
		if(flag.equals(Flag.MAIN))
			url = BaseTest.mainurl + "/action" + uri;
		if(flag.equals(Flag.MANAGE))
			url = BaseTest.manageurl + "/action" + uri;
		if(flag.equals(Flag.OPS))
			url = BaseTest.opsurl + "/action" + uri;
		if(flag.equals(Flag.MAIN_SECOND)){
			url = BaseTest.mainurl + "/action" + uri;
			@SuppressWarnings("unchecked")
			Map<String,String> map = JSON.parseObject(jsonObj,Map.class);
			map.put("_p", "");
			map.put("_site", BaseTest.mainSecond);
			jsonObj = JSON.toJSONString(map);
		}
		if(flag.equals(Flag.CHANNEL))
			url = BaseTest.channelurl + "/action" + uri;

		HttpPost post = new HttpPost(url);
		post.addHeader("Content-type","application/json; charset=utf-8");
		post.setHeader("Accept", "application/json");
		post.addHeader(new BasicHeader("Cookie",cookieKeyValue));

		setHttpHeaderToken(uri,post);

		log.info("jsonObj:"+jsonObj);
		post.setEntity(new StringEntity(jsonObj,Charset.forName("UTF-8")));

		log.info("create httppost : " + url);

		//请求
		HttpResult response = invoke(post);

		setHttpResponseToken(uri, response);

		checkResponseException(response,uri);
		return response;

	}


	/**
	 * 适用于接口带token的，默认false
	 * @param url
	 * @param jsonObj
	 * @param isUsingToken
	 */
	public synchronized HttpResult post(Flag flag,String uri,String jsonObj)
	{
		// 2. 创建请求方法的实例，并指定请求URL，添加请求参数。
		String url = "";
		if(flag.equals(Flag.CRM))
			url = BaseTest.defaulturl + "/action" + uri;
		if(flag.equals(Flag.MAIN))
			url = BaseTest.mainurl + "/action" + uri;
		if(flag.equals(Flag.MANAGE))
			url = BaseTest.manageurl + "/action" + uri;
		if(flag.equals(Flag.OPS))
			url = BaseTest.opsurl + "/action" + uri;
		if(flag.equals(Flag.MAIN_SECOND)){
			url = BaseTest.mainurl + "/action" + uri;
			@SuppressWarnings("unchecked")
			Map<String,String> map = JSON.parseObject(jsonObj,Map.class);
			map.put("_p", "");
			map.put("_site", BaseTest.mainSecond);
			jsonObj = JSON.toJSONString(map);
		}
		if(flag.equals(Flag.CHANNEL))
			url = BaseTest.channelurl + "/action" + uri;

		HttpPost post = new HttpPost(url);
		post.addHeader("Content-type","application/json; charset=utf-8");
		post.setHeader("Accept", "application/json");

		setHttpHeaderToken(uri,post);

		log.info("jsonObj:"+jsonObj);
		post.setEntity(new StringEntity(jsonObj,Charset.forName("UTF-8")));

		log.info("create httppost : " + url);

		//请求
		HttpResult response = invoke(post);

		setHttpResponseToken(uri, response);

		checkResponseException(response,uri);
		return response;

	}

	public synchronized HttpResult post(String uri,Map<String,Object> params,String jsonObj)
    {  
    	return post(Flag.CRM,uri,params,jsonObj);
    }
    /**
     * 适用于接口带token的，默认false
     * @param url
     * @param params
     * @param jsonObj
     * @param isUsingToken
     */
    public synchronized HttpResult post(Flag flag,String uri,Map<String,Object> params,String jsonObj)  
    {  
        // 2. 创建请求方法的实例，并指定请求URL，添加请求参数。
    	String url = "";
    	if(flag.equals(Flag.CRM))
    		url = BaseTest.defaulturl + "/action" + uri+"?" ;
    	if(flag.equals(Flag.MAIN))
    		url = BaseTest.mainurl + "/action" + uri+"?" ;
    	if(flag.equals(Flag.MANAGE))
    		url = BaseTest.manageurl + "/action" + uri+"?" ;
    	if(flag.equals(Flag.OPS))
    		url = BaseTest.opsurl + "/action" + uri+"?";
    	if(flag.equals(Flag.CHANNEL))
    		url = BaseTest.channelurl + "/action" + uri+"?";
    	
    	for (String key : params.keySet()) {    
    		url+=key+"="+params.get(key)+"&";
    	}  
    	url=url.substring(0,url.length()-1);
    	
    	HttpPost post = new HttpPost(url);
    	post.addHeader("Content-type","application/json; charset=utf-8");
    	post.setHeader("Accept", "application/json");	
    	
    	setHttpHeaderToken(uri,post);
    	
    	log.info("jsonObj:"+jsonObj);
    	post.setEntity(new StringEntity(jsonObj,Charset.forName("UTF-8")));
    	
        log.info("create httppost : " + url);  
        
        //请求
        HttpResult response = invoke(post);  
        
        setHttpResponseToken(uri, response);
        		
        checkResponseException(response,uri);
        return response;
        
    }  
    
    public synchronized HttpResult post(String uri,List<NameValuePair>params,String jsonObj)  
    {  
    	return post(Flag.CRM,uri,params,jsonObj);
    }
    /**
     * 适用于接口带token的，默认false
     * @param url
     * @param params
     * @param jsonObj
     * @param isUsingToken
     */
    public synchronized HttpResult post(Flag flag,String uri,List<NameValuePair>params,String jsonObj)  
    {  
        // 2. 创建请求方法的实例，并指定请求URL，添加请求参数。
    	String url = "";
    	if(flag.equals(Flag.CRM))
    		url = BaseTest.defaulturl + "/action" + uri+"?" ;
    	if(flag.equals(Flag.MAIN))
    		url = BaseTest.mainurl + "/action" + uri+"?" ;
    	if(flag.equals(Flag.MANAGE))
    		url = BaseTest.manageurl + "/action" + uri+"?" ;
    	if(flag.equals(Flag.OPS))
    		url = BaseTest.opsurl + "/action" + uri+"?";
    	if(flag.equals(Flag.MAIN_SECOND)){
    		url = BaseTest.mainurl + "/action" + uri+"?" ;
    		params.add(new BasicNameValuePair("_site", BaseTest.mainSecond));
    		params.add(new BasicNameValuePair("_p",""));
    	}
    	if(flag.equals(Flag.CHANNEL))
    		url = BaseTest.channelurl+ "/action" + uri+"?" ;
    	for (int i=0;i<params.size();i++) {    
    		url+=params.get(i).getName()+"="+params.get(i).getValue()+"&";
    	}  
    	url=url.substring(0,url.length()-1);
    	
    	HttpPost post = new HttpPost(url);
    	post.addHeader("Content-type","application/json; charset=utf-8");
    	post.setHeader("Accept", "application/json");	
    	
    	setHttpHeaderToken(uri,post);
    	
    	log.info("jsonObj:"+jsonObj);
    	post.setEntity(new StringEntity(jsonObj,Charset.forName("UTF-8")));
    	
        log.info("create httppost : " + url);  
        
        //请求
        HttpResult response = invoke(post);  
        
        setHttpResponseToken(uri, response);
        	
        checkResponseException(response,uri);
        return response;
        
    }
    /**
     * post请求读取InputStream 用于文件下载等
     * @param flag
     * @param uri
     * @param params
     * @return
     */
    public synchronized HttpResult postForInputStream(Flag flag,String uri,List<NameValuePair>params)  
    {  
    	String url = "";
    	if(flag.equals(Flag.CRM))
    		url = BaseTest.defaulturl + "/action" + uri; 
    	if(flag.equals(Flag.MAIN))
    		url = BaseTest.mainurl + "/action" + uri;
    	if(flag.equals(Flag.MANAGE))
    		url = BaseTest.manageurl + "/action" + uri;
    	if(flag.equals(Flag.OPS))
    		url = BaseTest.opsurl + "/action" + uri;
    	if(flag.equals(Flag.MAIN_SECOND)){
    		url = BaseTest.mainurl + "/action" + uri;
    		params.add(new BasicNameValuePair("_p",""));
    		params.add(new BasicNameValuePair("_site", BaseTest.mainSecond));
    	}
    	if(flag.equals(Flag.CHANNEL))
    		url = BaseTest.channelurl + "/action" + uri;
    	
        // 2. 创建请求方法的实例，并定请求URL，添加请求参数。  
        HttpPost post = postForm(url, params); 
        
        setHttpHeaderToken(uri,post);
    
        log.info("create httppost : " + url);  
        
        HttpResult response =  invoke(post,true); 
        
        setHttpResponseToken(uri, response);
        
        checkResponseException(response,uri);
        return response;
    }
    public HttpResult get(String uri, List<NameValuePair>params){
    	return get(Flag.CRM,uri,params);
    }
    
    /**
     * get带参数
     * @param uri
     * @param params
     * @return
     */
    public HttpResult get(Flag flag,String uri, List<NameValuePair>params){
    	String url = "";
    	if(flag.equals(Flag.CRM))
    		url = BaseTest.defaulturl + "/action" + uri + "?" + URLEncodedUtils.format(params, Consts.UTF_8); 
    	if(flag.equals(Flag.MAIN))
    		url = BaseTest.mainurl + "/action" + uri + "?" + URLEncodedUtils.format(params, Consts.UTF_8); 
    	if(flag.equals(Flag.MANAGE))
    		url = BaseTest.manageurl + "/action" + uri + "?" + URLEncodedUtils.format(params, Consts.UTF_8); 
    	if(flag.equals(Flag.OPS))
    		url = BaseTest.opsurl + "/action" + uri+ "?" + URLEncodedUtils.format(params, Consts.UTF_8);
    	if(flag.equals(Flag.MAIN_SECOND)){
    		params.add(new BasicNameValuePair("_p", ""));
    		params.add(new BasicNameValuePair("_site", BaseTest.mainSecond));
    		url = BaseTest.mainurl + "/action" + uri+"?" + URLEncodedUtils.format(params, Consts.UTF_8); 
    	}
    	if(flag.equals(Flag.CHANNEL))
    		url = BaseTest.channelurl + "/action" + uri + "?" + URLEncodedUtils.format(params, Consts.UTF_8); 
    	log.info("create httpget : " + url);  
    	
    	HttpGet get = new HttpGet(url);  
        setHttpHeaderToken(uri,get);
                
        HttpResult response =  invoke(get); 
        
        setHttpResponseToken(uri, response);
        checkResponseException(response,uri);
        return response;
    	
    }
    
    public HttpResult get(String uri,Map<String,Object> params)   
    {   return get(Flag.CRM,uri,params);
    	
    }
    /** 
     * HTTP GET请求。 
     *  
     * @param url 
     * @return 
     */  
    public HttpResult get(Flag flag,String uri,Map<String,Object> params)   
    {   
    	String url = "";
    	if(flag.equals(Flag.CRM))
    		url = BaseTest.defaulturl + "/action" + uri+"?" ;
    	if(flag.equals(Flag.MAIN))
    		url = BaseTest.mainurl + "/action" + uri+"?" ;
    	if(flag.equals(Flag.MANAGE))
    		url = BaseTest.manageurl + "/action" + uri+"?" ;
    	if(flag.equals(Flag.OPS))
    		url = BaseTest.opsurl + "/action" + uri+"?";
    	if(flag.equals(Flag.CHANNEL))
    		url = BaseTest.channelurl + "/action" + uri+"?" ;
    	for (String key : params.keySet()) {    
    		url+=key+"="+params.get(key)+"&"; 
    	}  
    	url=url.substring(0,url.length()-1);
        HttpGet get = new HttpGet(url);  
        log.info("create httpget : " + url);  
          
        return invoke(get);  
    }
 
    public HttpResult get(String uri,List<NameValuePair>params ,String ... pathParams){
      	return get(Flag.CRM,uri,params,pathParams);
      }
    /**
     * 
     * @param flag
     * @param uri
     * @param params
     * @param pathParams
     * @return
     */
      public HttpResult get(Flag flag,String uri,List<NameValuePair>params ,String ... pathParams){
     	 	String url = "";
     	 	if(flag.equals(Flag.CRM))
     	 		url = BaseTest.defaulturl + "/action" + uri ;
     	 	if(flag.equals(Flag.MAIN))
     	 		url = BaseTest.mainurl + "/action" + uri;
     	 	if(flag.equals(Flag.MANAGE))
     	 		url = BaseTest.manageurl + "/action" + uri;
     	 	if(flag.equals(Flag.OPS))
        		url = BaseTest.opsurl + "/action" + uri;
        	if(flag.equals(Flag.MAIN_SECOND)){
        		url = BaseTest.mainurl + "/action" + uri;
        		params.add(new BasicNameValuePair("_p",""));
        		params.add(new BasicNameValuePair("_site", BaseTest.mainSecond));
        	}
     	 	if(flag.equals(Flag.CHANNEL))
     	 		url = BaseTest.channelurl + "/action" + uri;
     	 	for(String p : pathParams)
     		 url += "/" + p;
     	 	
     	 	url +=  "?" + URLEncodedUtils.format(params, Consts.UTF_8);
     	 
     	 	HttpGet get = new HttpGet(url);
     	 	setHttpHeaderToken(uri,get);
     	 	log.info("create httpget : " + url);  
        
     	 	HttpResult response =  invoke(get);

		    setHttpResponseToken(uri, response);
		    checkResponseException(response,uri);
		   return  response;
     	 }
      
     
    public HttpResult get(String uri,String ... pathParams){
    	return get(Flag.CRM,uri,pathParams);
    }

  
    /**
     * 传入多个参数 
     * @param uri
     * @param pathParams
     * @return
     */
    public HttpResult get(Flag flag,String uri,String ... pathParams){
   	 	String url = "";
   	 	if(flag.equals(Flag.CRM))
   	 		url = BaseTest.defaulturl + "/action" + uri ;
   	 	if(flag.equals(Flag.MAIN))
   	 		url = BaseTest.mainurl + "/action" + uri;
   	 	if(flag.equals(Flag.MANAGE))
   	 		url = BaseTest.manageurl + "/action" + uri;
   	 if(flag.equals(Flag.OPS))
 		url = BaseTest.opsurl + "/action" + uri;
   	 if(flag.equals(Flag.CHANNEL))
	 		url = BaseTest.channelurl + "/action" + uri;
   	 	for(String p : pathParams)
   		 url += "/" + p;
   	 
   	 	HttpGet get = new HttpGet(url);  
   	 	log.info("create httpget : " + url);  
      
   	 	return invoke(get); 
   	 }
   	 
    
    public HttpResult get(String uri){
    	return get(Flag.CRM,uri);
    }
    
    public HttpResult get(Flag flag,String uri)   
    {   String url = "";
   	 	if(flag.equals(Flag.CRM))
   	 		url = BaseTest.defaulturl + "/action" + uri ;
   	 	if(flag.equals(Flag.MAIN))
   	 		url = BaseTest.mainurl + "/action" + uri;
   	 	if(flag.equals(Flag.MANAGE))
   	 		url = BaseTest.manageurl + "/action" + uri;
   	 if(flag.equals(Flag.OPS))
 		url = BaseTest.opsurl + "/action" + uri;
   	 if(flag.equals(Flag.CHANNEL))
	 		url = BaseTest.channelurl + "/action" + uri;
   	 
   	 HttpGet get = new HttpGet(url);  
     
     log.info("create httpget : " + url);  
     
     setHttpHeaderToken(uri,get);
            
     HttpResult response =  invoke(get); 
    
     setHttpResponseToken(uri, response);
     checkResponseException(response,uri);
     return response;

    }

    public HttpResult gets(String uri,Map<Integer,Integer>params){
    	return gets(Flag.CRM,uri,params);
    }
    
    public HttpResult gets(Flag flag,String uri,Map<Integer,Integer> params){
    	 String url = "";
    	 	if(flag.equals(Flag.CRM))
    	 		url = BaseTest.defaulturl + "/action" + uri ;
    	 	if(flag.equals(Flag.MAIN))
    	 		url = BaseTest.mainurl + "/action" + uri;
    	 	if(flag.equals(Flag.MANAGE))
    	 		url = BaseTest.manageurl + "/action" + uri;
    	 	if(flag.equals(Flag.OPS))
        		url = BaseTest.opsurl + "/action" + uri;
    	 	 if(flag.equals(Flag.CHANNEL))
    		 		url = BaseTest.channelurl + "/action" + uri;
    	for (Integer key : params.keySet()) {    
    		url+= params.get(key)+"/"; 
    	}  
    	url=url.substring(0,url.length()-1);
        HttpGet get = new HttpGet(url);  
        log.info("create httpget : " + url);  
          
        return invoke(get);  
    }
    
    public HttpResult options(String uri,Map<String,Object>params){
    	return options(Flag.CRM,uri,params);
    }

    /** 
     * HTTP OPTIONS请求。 
     *  
     * @param url 
     * @return 
     */
    public HttpResult options(Flag flag,String uri,Map<String,Object>params){
    	String url = "";
    	if(flag.equals(Flag.CRM))
    		url = BaseTest.defaulturl + "/action" + uri+"?" ;
    	if(flag.equals(Flag.MAIN))
    		url = BaseTest.mainurl + "/action" + uri+"?" ;
    	if(flag.equals(Flag.MANAGE))
    		url = BaseTest.manageurl + "/action" + uri+"?" ;
    	if(flag.equals(Flag.OPS))
    		url = BaseTest.opsurl + "/action" + uri+"?";
    	 if(flag.equals(Flag.CHANNEL))
 	 		url = BaseTest.channelurl + "/action" + uri+"?";
    	for (String key : params.keySet()) {    
    		url+=key+"="+params.get(key)+"&"; 
    	}  
    	url=url.substring(0,url.length()-1);
    	
    	HttpOptions options = new HttpOptions(url);
    	log.info("create httpoptions : " + url); 
    	
    	return invoke(options);
    }

    public synchronized HttpResult upload(String uri,Map<String,Object> params,File file) throws ClientProtocolException, IOException{
    	return upload(Flag.CRM,uri,params,file);
    }

    /**
     * 适用于接口带token的，默认false
     * @param url
     * @param jsonObj
     * @param isUsingToken
     * @throws IOException 
     * @throws ClientProtocolException 
     */
     public synchronized HttpResult upload(Flag flag,String uri,Map<String,Object> params,File file) throws ClientProtocolException, IOException { 
    	String url = "";
     	if(flag.equals(Flag.CRM))
     		url = BaseTest.defaulturl + "/action" + uri+"?" ;
     	if(flag.equals(Flag.MAIN))
     		url = BaseTest.mainurl + "/action" + uri+"?" ;
     	if(flag.equals(Flag.MANAGE))
     		url = BaseTest.manageurl + "/action" + uri+"?" ;
     	if(flag.equals(Flag.OPS))
    		url = BaseTest.opsurl + "/action" + uri+"?";
     	 if(flag.equals(Flag.CHANNEL))
 	 		url = BaseTest.channelurl + "/action" + uri+"?";
     	 if(params!= null){
			 for (String key : params.keySet()) {
				 url+=key+"="+params.get(key)+"&";
			 }
		 }

     	url=url.substring(0,url.length()-1);
    	MultipartEntityBuilder entity= MultipartEntityBuilder.create().addBinaryBody("file", file,ContentType.create("application/octet-stream"),file.getName());
        HttpPost httpPost =new HttpPost(url);
        log.info("create httpupload : " + url);  
        httpPost.setEntity(entity.build());
        HttpResult response =  invoke(httpPost);  
        checkResponseException(response,uri);
    	return response;
    }  
    
    public HttpResult delete(String uri,String param){
    	return delete(Flag.CRM,uri,param);
    }
    public HttpResult delete(Flag flag ,String uri,String param){

    	String url = "";
     	if(flag.equals(Flag.CRM))
     		url = BaseTest.defaulturl + "/action" + uri +"/" + param; 
     	if(flag.equals(Flag.MAIN))
     		url = BaseTest.mainurl + "/action" + uri +"/" + param; 
     	if(flag.equals(Flag.MANAGE))
     		url = BaseTest.manageurl + "/action" + uri +"/" + param; 
     	if(flag.equals(Flag.OPS))
    		url = BaseTest.opsurl + "/action" + uri+"/"+param;
     	 if(flag.equals(Flag.CHANNEL))
 	 		url = BaseTest.channelurl + "/action" + uri+"/"+param;
    	HttpDelete delete = new HttpDelete(url);
    	log.info("create httpdelete : " + url);  
    	
    	return invoke(delete);
    }
    
    public HttpResult delete(String uri,Map<String,Object> params){
    	return delete(Flag.CRM,uri,params);
    }
    public HttpResult delete(Flag flag,String uri,Map<String,Object> params){
    	String url = "";
     	if(flag.equals(Flag.CRM))
     		url = BaseTest.defaulturl + "/action" + uri +"?" ;
     	if(flag.equals(Flag.MAIN))
     		url = BaseTest.mainurl + "/action" + uri +"?" ;
     	if(flag.equals(Flag.MANAGE))
     		url = BaseTest.manageurl + "/action" + uri +"?" ;
     	if(flag.equals(Flag.OPS))
    		url = BaseTest.opsurl + "/action" + uri+"?";
     	 if(flag.equals(Flag.CHANNEL))
 	 		url = BaseTest.channelurl + "/action" + uri+"?";
    	for (String key : params.keySet()) {    
    		url+=key+"="+params.get(key)+"&"; 
    	}  
    	url=url.substring(0,url.length()-1);
    	
    	HttpDelete delete = new HttpDelete(url);
    	log.info("create httpdelete : " + url);  
    	
    	return invoke(delete);
    }
      
    
    
    private HttpResult invoke(HttpRequestBase request ,Boolean isGetInputStream)  {   
    	if(isGetInputStream){
    		if (this._headers != null)   
            {  
                //  
                addHeaders(request);  
            }  
              
            try  
            {  
                // 3. 调用HttpClient对象的execute(HttpUriRequest request)发送请求，返回一个HttpResponse。  
            	HttpClientContext httpClientContext = getHttpClientContext();
              HttpResponse  response = _httpclient.execute(request,httpClientContext); 
             
              int code = response.getStatusLine().getStatusCode();
              InputStream ism = response.getEntity().getContent();
              Map<String, String> headerMap = new HashMap<String, String>();
              Header headers[]=response.getAllHeaders();
              if(headers !=null){
            	  for(Header header:headers){
            		  headerMap.put(header.getName(), header.getValue());
            	  }
              }
              return new HttpResult(code, null, headerMap,ism);
              // log.info("execute http success... ; body = " + EntityUtils.toString(response.getEntity())); 
               
            }  
            catch (Exception e)  
            {  
                e.printStackTrace();  
                log.info("execute http exception..."); 
                throw new RuntimeException(e);
            }  
            
    	}else
    		return this.invoke(request); 
    }  
    
    /** 
     * 发送请求，处理响应。 
     * @param request 
     * @return 
     * http://stackoverflow.com/questions/19165420/socket-closed-exception-when-trying-to-read-httpresponse
     */  
    private HttpResult invoke(HttpRequestBase request)  
    {   
        if (this._headers != null)   
        {  
            //  
            addHeaders(request);  
//            log.info("addHeaders to http ...");  
        }  
          
        try  
        {  
            // 3. 调用HttpClient对象的execute(HttpUriRequest request)发送请求，返回一个HttpResponse。  
        	HttpClientContext httpClientContext = getHttpClientContext();
          HttpResponse  response = _httpclient.execute(request,httpClientContext); 
          int code = response.getStatusLine().getStatusCode();
          String body = EntityUtils.toString(response.getEntity(),"utf-8");
          Map<String, String> headerMap = new HashMap<String, String>();
          Header headers[]=response.getAllHeaders();
          if(headers !=null){
        	  for(Header header:headers){
        		  headerMap.put(header.getName(), header.getValue());
        	  }
          }
          return new HttpResult(code, body, headerMap);
          // log.info("execute http success... ; body = " + EntityUtils.toString(response.getEntity())); 
           
        }  
        catch (Exception e)  
        {  
            e.printStackTrace();  
            log.info("execute http exception..."); 
            throw new RuntimeException(e);
        }  
//        finally  
//        {  
//            // 4. 无论执行方法是否成功，都必须释放连接。  
//           // request.abort();  
//        	request.releaseConnection();
//            log.info("release http ...");  
//        }  
        
    }  
      
    /** 
     * 获取post方法。 
     *  
     * @param url 
     * @param params 
     * @return 
     */  
    private HttpPost postForm(String url, Map<String, String> params)   
    {  
        HttpPost httpost = new HttpPost(url);  
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();  
  
        // 组装参数。  
        Set<String> keySet = params.keySet();  
        for (String key : keySet)   
        {  
            nvps.add(new BasicNameValuePair(key, params.get(key)));  
        }  
        
		log.info("set params:"+nvps);
		httpost.setEntity(new UrlEncodedFormEntity(nvps, Consts.UTF_8));  
        return httpost;  
    }  
    
    
    /** 
     * 获取post方法。 
     *  
     * @param url 
     * @param params 
     * @return 
     */  
    private HttpPost postForm(String url, List<NameValuePair> params)   
    {  
    	HttpPost httpost = new HttpPost(url);  
		log.info("set params:"+params);
		httpost.setEntity(new UrlEncodedFormEntity(params, Consts.UTF_8));  
        return httpost;  
    }  
    
    
    /** 
     * 增加消息头。 
     *  
     * @param httpost 
     */  
    private void addHeaders(HttpUriRequest httpost)  
    {  
        Iterator<Entry<String, String>> it = this._headers.entrySet()  
                .iterator();  
        Entry<String, String> entry = null;  
        String name = null;  
        String value = null;  
  
        while (it.hasNext())  
        {  
            entry = it.next();  
            name = entry.getKey();  
            value = entry.getValue();  
  
            httpost.addHeader(name, value);  
        }  
    }  
      
    /** 
     * 关闭HTTP客户端链接。 
     */  
    @SuppressWarnings("deprecation")
	public void shutdown()  
    {  
        _httpclient.getConnectionManager().shutdown();  
        log.info("shutdown _httpclient ...");  
    }  
    
    public Map<String,String> getCookies(){
    	Map<String, String> map = new HashMap<String, String>();
    	for(Cookie cookie:cookieStore.getCookies()){
    		map.put(cookie.getName(), cookie.getValue());
    	}
    	return map;
    }

	public  String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getxAutoToken() {
		return xAutoToken;
	}

	public void setxAutoToken(String xAutoToken) {
		this.xAutoToken = xAutoToken;
	}

	/***
 * 在@Token方法头加上二次提交token
 * @param uri
 * @param get
 */
public void setHttpHeaderToken(String uri ,HttpGet get){
	if(ActionsDefine.CheckTokenActionList.contains(uri)){
		log.debug("设置请求头UNIQUE_SUBMIT_TOKEN..."+getToken());
		get.addHeader(ActionsDefine.UNIQUE_SUBMIT_TOKEN, getToken());
	}
	if(ActionsDefine.CheckauthTokenList.contains(uri)){
		log.debug("设置请求头X_Auth_Mytijian_Token..."+getxAutoToken());
		get.addHeader(ActionsDefine.X_Auth_Mytijian_Token, getxAutoToken());
	}
}

	/***
	 * 在@Token方法头加上二次提交token
	 * @param uri
	 * @param post
	 */
	public void setHttpHeaderToken(String uri ,HttpPost post){
		if(ActionsDefine.CheckTokenActionList.contains(uri)){
			log.debug("设置请求头UNIQUE_SUBMIT_TOKEN..."+getToken());
			post.addHeader(ActionsDefine.UNIQUE_SUBMIT_TOKEN, getToken());
		}
		if(ActionsDefine.CheckauthTokenList.contains(uri)){
			log.debug("设置请求头X_Auth_Mytijian_Token..."+getxAutoToken());
			post.addHeader(ActionsDefine.X_Auth_Mytijian_Token, getxAutoToken());
		}
	}


	/***
	 * 在@Token方法头加上二次提交token
	 * @param uri
	 * @param post
	 */
	public void setHttpHeaderToken(String uri ,HttpPut put){
		if(ActionsDefine.CheckTokenActionList.contains(uri)){
			log.debug("设置请求头UNIQUE_SUBMIT_TOKEN..."+getToken());
			put.addHeader(ActionsDefine.UNIQUE_SUBMIT_TOKEN, getToken());
		}
		if(ActionsDefine.CheckauthTokenList.contains(uri)){
			log.debug("设置请求头X_Auth_Mytijian_Token..."+getxAutoToken());
			put.addHeader(ActionsDefine.X_Auth_Mytijian_Token, getxAutoToken());
		}
	}
	
	/**
	 * 保存二次提交token至本地
	 * @param uri
	 * @param response
	 */
	public void setHttpResponseToken(String uri,HttpResult response){
	        	if( ActionsDefine.SetTokenActionList.contains(uri)){
			Map<String,String> maps = response.getHeader();
			if(maps.containsKey(ActionsDefine.UNIQUE_SUBMIT_TOKEN)){
				log.debug("提取UNIQUE_SUBMIT_TOKEN...."+maps.get(ActionsDefine.UNIQUE_SUBMIT_TOKEN));
				setToken(maps.get(ActionsDefine.UNIQUE_SUBMIT_TOKEN));
			}
		}
		if( ActionsDefine.SetAuthTokenList.contains(uri)){
			Map<String,String> maps = response.getHeader();
			if(maps.containsKey(ActionsDefine.X_Auth_Mytijian_Token)){
				log.debug("提取X_Auth_Mytijian_Token...."+maps.get(ActionsDefine.X_Auth_Mytijian_Token));
				setxAutoToken(maps.get(ActionsDefine.X_Auth_Mytijian_Token));
			}
		}
	        
	}
	
	
	/**
     * 适用于接口带token的，默认false
     * @param flag
	 * @param  uri
     * @param params
     * @param pairs
     */
    public synchronized HttpResult post(Flag flag,String uri,List<NameValuePair>params, Map<String, String> pairs)  
    {  
        // 2. 创建请求方法的实例，并指定请求URL，添加请求参数。
    	String url = "";
    	if(flag.equals(Flag.CRM))
    		url = BaseTest.defaulturl + "/action" + uri+"?" ;
    	if(flag.equals(Flag.MAIN))
    		url = BaseTest.mainurl + "/action" + uri+"?" ;
    	if(flag.equals(Flag.MANAGE))
    		url = BaseTest.manageurl + "/action" + uri+"?" ;
    	if(flag.equals(Flag.OPS))
    		url = BaseTest.opsurl + "/action" + uri+"?";
    	 if(flag.equals(Flag.CHANNEL))
 	 		url = BaseTest.channelurl + "/action" + uri+"?";
    	for (int i=0;i<params.size();i++) {    
    		url+=params.get(i).getName()+"="+params.get(i).getValue()+"&";
    	}  
    	url=url.substring(0,url.length()-1);    	
    	
    	HttpPost post = new HttpPost(url);
    	post.addHeader("Content-type","application/x-www-form-urlencoded;charset=UTF-8");
    	post.setHeader("Accept", "application/json");	
    	
    	setHttpHeaderToken(uri,post);
    	
        log.info("create httppost : " + url);  
        log.info("set params:"+pairs);
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();  
        
        // 组装参数。  
        Set<String> keySet = pairs.keySet();
        
        for (String key : keySet)   
        {  
            nvps.add(new BasicNameValuePair(key, pairs.get(key)));  
        }  
        
		log.info("set params:"+nvps);
		post.setEntity(new UrlEncodedFormEntity(nvps, Consts.UTF_8));
        //请求
        HttpResult response = invoke(post);  
        
        setHttpResponseToken(uri, response);
        		
        checkResponseException(response,uri);
        return response;
    }
    
    /**
     * 用于接口只带一个int类
     * @param flag
     * @param uri
     * @param param
     * @return
     */
    public synchronized HttpResult post(Flag flag,String uri,int param)  
    {  
        // 2. 创建请求方法的实例，并指定请求URL，添加请求参数。
    	String url = "";
    	if(flag.equals(Flag.CRM))
    		url = BaseTest.defaulturl + "/action" + uri + param ;
    	if(flag.equals(Flag.MAIN))
    		url = BaseTest.mainurl + "/action" + uri + param ;
    	if(flag.equals(Flag.MANAGE))
    		url = BaseTest.manageurl + "/action" + uri + param ;  
    	if(flag.equals(Flag.OPS))
    		url = BaseTest.opsurl + "/action" + uri+param;
    	 if(flag.equals(Flag.CHANNEL))
 	 		url = BaseTest.channelurl + "/action" + uri+param;
    	
    	HttpPost post = new HttpPost(url);
    	post.addHeader("Content-type","application/x-www-form-urlencoded");
    	post.setHeader("Accept", "application/json");	
    	
    	setHttpHeaderToken(uri,post);
    	
        log.info("create httppost : " + url);         

        //请求
        HttpResult response = invoke(post);  
        
        setHttpResponseToken(uri, response);
        	
        checkResponseException(response,uri);
        return response;
    }
    
	public synchronized HttpResult put(Flag flag, String uri, String jsonObj) {
		// 2. 创建请求方法的实例，并指定请求URL，添加请求参数。
		String url = "";
		if (flag.equals(Flag.CRM))
			url = BaseTest.defaulturl + "/action" + uri;
		if (flag.equals(Flag.MAIN))
			url = BaseTest.mainurl + "/action" + uri;
		if (flag.equals(Flag.MANAGE))
			url = BaseTest.manageurl + "/action" + uri;
		if(flag.equals(Flag.OPS))
    		url = BaseTest.opsurl + "/action" + uri;
		if (flag.equals(Flag.CHANNEL))
			url = BaseTest.channelurl + "/action" + uri;

		HttpPut put = new HttpPut(url);
		put.addHeader("Content-type", "application/json; charset=utf-8");
		put.setHeader("Accept", "application/json");

		setHttpHeaderToken(uri, put);

		log.info("jsonObj:" + jsonObj);
		put.setEntity(new StringEntity(jsonObj, Charset.forName("UTF-8")));

		log.info("create httpput : " + url);

		// 请求
		HttpResult response = invoke(put);

		setHttpResponseToken(uri, response);
		
		checkResponseException(response,uri);
		return response;

	} 
	
	private void checkResponseException(HttpResult result,String uri){
		if(result.getCode() == HttpStatus.SC_OK)
			if(result.getBody()!=null)
				Assert.assertFalse(result.getBody().contains("EX_0_0_SYS_00_00_000"),uri+"提示:"+result.getBody());
			
	}


}
