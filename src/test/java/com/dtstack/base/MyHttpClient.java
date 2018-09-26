package com.dtstack.base;

import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieSpecProvider;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.impl.cookie.BestMatchSpecFactory;
import org.apache.http.impl.cookie.BrowserCompatSpecFactory;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.testng.Assert;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.*;
import java.util.Map.Entry;

//import org.apache.http.entity.mime.MultipartEntityBuilder;

/**
 * @author huifang
 */
public class MyHttpClient {
    /**
     * 日志对象。
     */

    /**
     * 默认HTTP请求客户端对象。
     */
    private HttpClient _httpclient;
    //private CloseableHttpClient _httpclient;

    protected final static Logger log = Logger.getLogger(MyHttpClient.class);
    /**
     * 用户自定义消息头。
     */
    private Map<String, String> _headers;
    private CookieStore cookieStore = new BasicCookieStore();


    private String token;

    private String cookie;

    private String xAutoToken;

    private List<String> setCookies = new ArrayList<>();

    /**
     * 使用默认客户端对象。
     */
    public MyHttpClient() {

        // 1. 创建HttpClient对象。
        RequestConfig defaultConnectionConfig = RequestConfig.custom()
                .setConnectTimeout(BaseTest.connTimeout)
                .setSocketTimeout(BaseTest.socketTimeout)
//                .setConnectionRequestTimeout(BaseTest.socketTimeout)
                .setCookieSpec(CookieSpecs.STANDARD)
                .build();
        _httpclient = HttpClientBuilder.create().setRedirectStrategy(new LaxRedirectStrategy())
                .setMaxConnPerRoute(BaseTest.maxConnPerRoute)
                .setMaxConnTotal(BaseTest.maxConnTotal)
                .setDefaultRequestConfig(defaultConnectionConfig)
                .build();
        log.info("create _httpclient ...");

    }

    private HttpClientContext getHttpClientContext() {
        HttpClientContext httpContext = HttpClientContext.create();
        Registry<CookieSpecProvider> registry = RegistryBuilder
                .<CookieSpecProvider>create()
                .register(CookieSpecs.BEST_MATCH, new BestMatchSpecFactory())
                .register(CookieSpecs.BROWSER_COMPATIBILITY,
                        new BrowserCompatSpecFactory()).build();
        httpContext.setCookieSpecRegistry(registry);
        httpContext.setCookieStore(cookieStore);
        return httpContext;
    }

    /**
     * 调用者指定客户端对象。
     */
    public MyHttpClient(Map<String, String> headers) {
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

    public synchronized HttpResult post(String uri, List<NameValuePair> params) {
        return post(Flag.UIC, uri, params);
    }

    /**
     * HTTP POST请求。
     *
     * @return
     * @throws InterruptedException
     */
    public synchronized HttpResult post(Flag flag, String uri, List<NameValuePair> params) {
        String url = "";
        if (flag.equals(Flag.UIC))
            url = BaseTest.uicurl + uri;

        // 2. 创建请求方法的实例，并定请求URL，添加请求参数。
        HttpPost post = postForm(url, params);

        setHttpHeaderCookie(post);

        log.info("create httppost : " + url);

        HttpResult response = invoke(post);

        setHttpResonseCookie(response);
        checkResponseException(response, uri);

        System.out.println("============ request cookie===============");
        System.out.println(Arrays.toString(post.getHeaders("Cookie")));

        System.out.println("============ response cookie===============");
        System.out.println(response.getHeader().get("Set-Cookie"));
        return response;
    }


    public synchronized HttpResult post(String uri, Map<String, String> params) {
        return post(Flag.UIC, uri, params);
    }

    /**
     * HTTP POST请求。
     *
     * @return
     * @throws InterruptedException
     */
    public synchronized HttpResult post(Flag flag, String uri, Map<String, String> params) {
        String url = "";
        if (flag.equals(Flag.UIC))
            url = BaseTest.uicurl + uri;
        // 2. 创建请求方法的实例，并指定请求URL，添加请求参数。
        HttpPost post = postForm(url, params);
        setHttpHeaderCookie(post);

        log.info("create httppost : " + url);

        HttpResult response = invoke(post);

        setHttpResonseCookie(response);

        //System.out.println("response访问后获取的常规Cookie:=========");

        checkResponseException(response, uri);
        return response;
    }

    private void setHttpHeaderCookie(HttpRequestBase post) {
    	System.out.println("getCookie() = "+ getCookie());
        post.addHeader("Cookie", getCookie());
    }

    private void setHttpResonseCookie(HttpResult response) {
        Map<String, String> headers = response.getHeader();
        String headerCookies = "";
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            //System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
            if (entry.getKey().equals("Set-Cookie") && !entry.getValue().equals("")) {
                headerCookies += entry.getValue();
            }
        }
        if (!headerCookies.equals("")) {
            setCookie(headerCookies);
        }
    }

    public synchronized HttpResult post(String uri, String jsonObj) {
        return post(Flag.UIC, uri, jsonObj);
    }

    public synchronized HttpResult post(Flag flag, String uri, String jsonObj, String cookieKeyValue) {
        // 2. 创建请求方法的实例，并指定请求URL，添加请求参数。
        String url = "";
        if (flag.equals(Flag.UIC))
            url = BaseTest.uicurl + uri;

        HttpPost post = new HttpPost(url);
        post.addHeader("Authorization", "your token");//认证token
        post.addHeader("Content-type", "application/json; charset=utf-8");
        post.addHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/68.0.3440.106 Safari/537.36");
        post.setHeader("Accept", "application/json");
        post.addHeader(new BasicHeader("Cookie", cookieKeyValue));

        setHttpHeaderCookie(post);

        log.info("jsonObj:" + jsonObj);
        post.setEntity(new StringEntity(jsonObj, Charset.forName("UTF-8")));

        log.info("create httppost : " + url);

        //请求
        HttpResult response = invoke(post);

        setHttpResonseCookie(response);

        checkResponseException(response, uri);
        return response;

    }


    /**
     * 适用于接口带token的，默认false
     */
    public synchronized HttpResult post(Flag flag, String uri, String jsonObj) {
        // 2. 创建请求方法的实例，并指定请求URL，添加请求参数。
        String url = "";
        if (flag.equals(Flag.UIC))
            url = BaseTest.uicurl + uri;
        if (flag.equals(Flag.IDE)) {
            url = BaseTest.ideurl + uri;
        }
        if (flag.equals(Flag.CONSOLE)) {
        	url = BaseTest.consoleurl+uri;
			
		}
        HttpPost post = new HttpPost(url);
        post.addHeader("Content-type", "application/json; charset=utf-8");
        post.setHeader("Accept", "application/json");
        //post.addHeader("Cookie",ActionsDefine.cookieValue);

        setHttpHeaderCookie(post);

        log.info("jsonObj:" + jsonObj);
        post.setEntity(new StringEntity(jsonObj, Charset.forName("UTF-8")));

        log.info("create httppost : " + url);

        //请求
        HttpResult response = invoke(post);

        setHttpResonseCookie(response);

        checkResponseException(response, uri);

        System.out.println("============ request cookie===============");
        System.out.println(Arrays.toString(post.getHeaders("Cookie")));


        System.out.println("============ response cookie===============");
        System.out.println(response.getHeader().get("Set-Cookie"));
        return response;

    }

    public synchronized HttpResult post(String uri, Map<String, Object> params, String jsonObj) {
        return post(Flag.UIC, uri, params, jsonObj);
    }

    /**
     * 适用于接口带token的，默认false
     */
    public synchronized HttpResult post(Flag flag, String uri, Map<String, Object> params, String jsonObj) {
        // 2. 创建请求方法的实例，并指定请求URL，添加请求参数。
        String url = "";
        if (flag.equals(Flag.UIC))
            url = BaseTest.uicurl + uri + "?";

        for (String key : params.keySet()) {
            url += key + "=" + params.get(key) + "&";
        }
        url = url.substring(0, url.length() - 1);

        HttpPost post = new HttpPost(url);
        post.addHeader("Content-type", "application/json; charset=utf-8");
        post.setHeader("Accept", "application/json");

        setHttpHeaderCookie(post);

        log.info("jsonObj:" + jsonObj);
        post.setEntity(new StringEntity(jsonObj, Charset.forName("UTF-8")));

        log.info("create httppost : " + url);

        //请求
        HttpResult response = invoke(post);

        setHttpResonseCookie(response);

        checkResponseException(response, uri);
        return response;

    }

    public synchronized HttpResult post(String uri, List<NameValuePair> params, String jsonObj) {
        return post(Flag.UIC, uri, params, jsonObj);
    }

    /**
     * 适用于接口带token的，默认false
     */
    public synchronized HttpResult post(Flag flag, String uri, List<NameValuePair> params, String jsonObj) {
        // 2. 创建请求方法的实例，并指定请求URL，添加请求参数。
        String url = "";
        if (flag.equals(Flag.UIC))
            url = BaseTest.uicurl + uri + "?";
        for (int i = 0; i < params.size(); i++) {
            url += params.get(i).getName() + "=" + params.get(i).getValue() + "&";
        }
        url = url.substring(0, url.length() - 1);

        HttpPost post = new HttpPost(url);
        post.addHeader("Content-type", "application/json; charset=utf-8");
        post.setHeader("Accept", "application/json");

        setHttpHeaderCookie(post);

        log.info("jsonObj:" + jsonObj);
        post.setEntity(new StringEntity(jsonObj, Charset.forName("UTF-8")));

        log.info("create httppost : " + url);

        //请求
        HttpResult response = invoke(post);

        setHttpResonseCookie(response);

        checkResponseException(response, uri);
        return response;

    }

    /**
     * post请求读取InputStream 用于文件下载等
     *
     * @param flag
     * @param uri
     * @param params
     * @return
     */
    public synchronized HttpResult postForInputStream(Flag flag, String uri, List<NameValuePair> params) {
        String url = "";
        if (flag.equals(Flag.UIC))
            url = BaseTest.uicurl + uri;

        // 2. 创建请求方法的实例，并定请求URL，添加请求参数。
        HttpPost post = postForm(url, params);

        setHttpHeaderCookie(post);

        log.info("create httppost : " + url);

        HttpResult response = invoke(post, true);

        setHttpResonseCookie(response);

        checkResponseException(response, uri);
        return response;
    }

    public HttpResult get(String uri, List<NameValuePair> params) {
        return get(Flag.UIC, uri, params);
    }

    /**
     * get带参数
     *
     * @param uri
     * @param params
     * @return
     */
    public HttpResult get(Flag flag, String uri, List<NameValuePair> params) {
        String url = "";
        if (flag.equals(Flag.UIC))
            url = BaseTest.uicurl + uri + "?" + URLEncodedUtils.format(params, Consts.UTF_8);

        log.info("create httpget : " + url);

        HttpGet get = new HttpGet(url);
        setHttpHeaderCookie(get);

        HttpResult response = invoke(get);

        setHttpResonseCookie(response);
        checkResponseException(response, uri);
        return response;

    }

    public HttpResult get(String uri, Map<String, Object> params) {
        return get(Flag.UIC, uri, params);

    }

    /**
     * HTTP GET请求。
     *
     * @return
     */
    public HttpResult get(Flag flag, String uri, Map<String, Object> params) {
        String url = "";
        if (flag.equals(Flag.UIC))
            url = BaseTest.uicurl + uri + "?";
        for (String key : params.keySet()) {
            url += key + "=" + params.get(key) + "&";
        }
        url = url.substring(0, url.length() - 1);
        HttpGet get = new HttpGet(url);
        log.info("create httpget : " + url);

        return invoke(get);
    }

    public HttpResult get(String uri, List<NameValuePair> params, String... pathParams) {
        return get(Flag.UIC, uri, params, pathParams);
    }

    /**
     * @param flag
     * @param uri
     * @param params
     * @param pathParams
     * @return
     */
    public HttpResult get(Flag flag, String uri, List<NameValuePair> params, String... pathParams) {
        String url = "";
        if (flag.equals(Flag.UIC))
            url = BaseTest.uicurl + uri;
        for (String p : pathParams)
            url += "/" + p;

        url += "?" + URLEncodedUtils.format(params, Consts.UTF_8);

        HttpGet get = new HttpGet(url);
        setHttpHeaderCookie(get);
        log.info("create httpget : " + url);

        HttpResult response = invoke(get);

        setHttpResonseCookie(response);
        checkResponseException(response, uri);
        return response;
    }


    public HttpResult get(String uri, String... pathParams) {
        return get(Flag.UIC, uri, pathParams);
    }


    /**
     * 传入多个参数
     *
     * @param uri
     * @param pathParams
     * @return
     */
    public HttpResult get(Flag flag, String uri, String... pathParams) {
        String url = "";
        if (flag.equals(Flag.UIC))
            url = BaseTest.uicurl + uri;
        for (String p : pathParams)
            url += "/" + p;

        HttpGet get = new HttpGet(url);
        log.info("create httpget : " + url);

        return invoke(get);
    }


    public HttpResult get(String uri) {
        return get(Flag.UIC, uri);
    }

    public HttpResult get(Flag flag, String uri) {
        String url = "";
        if (flag.equals(Flag.UIC))
            url = BaseTest.uicurl + uri;
        if (flag.equals(Flag.UICAPI)) {
            url = BaseTest.uicapiurl + uri;
        }

        HttpGet get = new HttpGet(url);

        log.info("create httpget : " + url);

        setHttpHeaderCookie(get);

        HttpResult response = invoke(get);
        setHttpResonseCookie(response);
        //System.out.println("response访问后获取的常规Cookie:=========");

        checkResponseException(response, uri);
        return response;

    }

    public HttpResult gets(String uri, Map<Integer, Integer> params) {
        return gets(Flag.UIC, uri, params);
    }

    public HttpResult gets(Flag flag, String uri, Map<Integer, Integer> params) {
        String url = "";
        if (flag.equals(Flag.UIC))
            url = BaseTest.uicurl + uri;
        for (Integer key : params.keySet()) {
            url += params.get(key) + "/";
        }
        url = url.substring(0, url.length() - 1);
        HttpGet get = new HttpGet(url);
        log.info("create httpget : " + url);

        return invoke(get);
    }

    public HttpResult options(String uri, Map<String, Object> params) {
        return options(Flag.UIC, uri, params);
    }

    /**
     * HTTP OPTIONS请求。
     *
     * @return
     */
    public HttpResult options(Flag flag, String uri, Map<String, Object> params) {
        String url = "";
        if (flag.equals(Flag.UIC))
            url = BaseTest.uicurl + uri + "?";
        for (String key : params.keySet()) {
            url += key + "=" + params.get(key) + "&";
        }
        url = url.substring(0, url.length() - 1);

        HttpOptions options = new HttpOptions(url);
        log.info("create httpoptions : " + url);

        return invoke(options);
    }

    public synchronized HttpResult upload(String uri, Map<String, Object> params, File file) throws ClientProtocolException, IOException {
        return upload(Flag.UIC, uri, params, file);
    }

    /**
     * 适用于接口带token的，默认false
     */
    public synchronized HttpResult upload(Flag flag, String uri, Map<String, Object> params, File file) throws ClientProtocolException, IOException {
        String url = "";
        if (flag.equals(Flag.UIC))
            url = BaseTest.uicurl + uri + "?";
        if (params != null) {
            for (String key : params.keySet()) {
                url += key + "=" + params.get(key) + "&";
            }
        }

        url = url.substring(0, url.length() - 1);
        MultipartEntityBuilder entity = MultipartEntityBuilder.create().addBinaryBody("file", file, ContentType.create("application/octet-stream"), file.getName());
        HttpPost httpPost = new HttpPost(url);
        log.info("create httpupload : " + url);
        httpPost.setEntity(entity.build());
        HttpResult response = invoke(httpPost);
        checkResponseException(response, uri);
        return response;
    }

    public HttpResult delete(String uri, String param) {
        return delete(Flag.UIC, uri, param);
    }

    public HttpResult delete(Flag flag, String uri, String param) {

        String url = "";
        if (flag.equals(Flag.UIC))
            url = BaseTest.uicurl + uri + "/" + param;
        HttpDelete delete = new HttpDelete(url);
        log.info("create httpdelete : " + url);

        return invoke(delete);
    }

    public HttpResult delete(String uri, Map<String, Object> params) {
        return delete(Flag.UIC, uri, params);
    }

    public HttpResult delete(Flag flag, String uri, Map<String, Object> params) {
        String url = "";
        if (flag.equals(Flag.UIC))
            url = BaseTest.uicurl + uri + "?";
        for (String key : params.keySet()) {
            url += key + "=" + params.get(key) + "&";
        }
        url = url.substring(0, url.length() - 1);

        HttpDelete delete = new HttpDelete(url);
        log.info("create httpdelete : " + url);

        return invoke(delete);
    }


    private HttpResult invoke(HttpRequestBase request, Boolean isGetInputStream) {
        if (isGetInputStream) {
            if (this._headers != null) {
                //
                addHeaders(request);
            }

            try {
                // 3. 调用HttpClient对象的execute(HttpUriRequest request)发送请求，返回一个HttpResponse。
                HttpClientContext httpClientContext = getHttpClientContext();
                HttpResponse response = _httpclient.execute(request, httpClientContext);

                int code = response.getStatusLine().getStatusCode();
                InputStream ism = response.getEntity().getContent();
                Map<String, String> headerMap = new HashMap<String, String>();
                Header headers[] = response.getAllHeaders();
                if (headers != null) {
                    for (Header header : headers) {
                        headerMap.put(header.getName(), header.getValue());
                    }
                }
                return new HttpResult(code, null, headerMap, ism);
                // log.info("execute http success... ; body = " + EntityUtils.toString(response.getEntity()));

            } catch (Exception e) {
                e.printStackTrace();
                log.info("execute http exception...");
                throw new RuntimeException(e);
            }

        } else
            return this.invoke(request);
    }

    /**
     * 发送请求，处理响应。
     *
     * @param request
     * @return http://stackoverflow.com/questions/19165420/socket-closed-exception-when-trying-to-read-httpresponse
     */
    private HttpResult invoke(HttpRequestBase request) {
        if (this._headers != null) {
            //
            addHeaders(request);
//            log.info("addHeaders to http ...");
        }

        try {
            // 3. 调用HttpClient对象的execute(HttpUriRequest request)发送请求，返回一个HttpResponse。
            HttpClientContext httpClientContext = getHttpClientContext();
            Header[] requestHeaders = request.getHeaders("Cookie");
            //System.out.println("request访问后获取的常规Cookie:=========");

            HttpResponse response = _httpclient.execute(request, httpClientContext);

            Header[] setCookieHeaders = response.getHeaders("Set-Cookie");
            //System.out.println("response访问后获取的常规Cookie:=========" + setCookieHeaders.length + "     this.cookie = "+ getCookie());

            String cookieHeaders = "";
            for (Header header : setCookieHeaders
            ) {
                cookieHeaders += header.getValue() + ";";
            }

            if (setCookieHeaders.length == 0) {
                response.setHeader("Set-Cookie", this.getCookie());

            }
            int code = response.getStatusLine().getStatusCode();
            String body = EntityUtils.toString(response.getEntity(), "utf-8");
            Map<String, String> headerMap = new HashMap<String, String>();
            Header headers[] = response.getAllHeaders();
            if (headers != null) {
                for (Header header : headers) {
                    headerMap.put(header.getName(), header.getValue());
                }
            }
            if (!cookieHeaders.equals("")) {
                headerMap.put("Set-Cookie", cookieHeaders);
            }

            return new HttpResult(code, body, headerMap);
            // log.info("execute http success... ; body = " + EntityUtils.toString(response.getEntity()));

        } catch (Exception e) {
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
    private HttpPost postForm(String url, Map<String, String> params) {
        HttpPost httppost = new HttpPost(url);
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();

        // 组装参数。
        Set<String> keySet = params.keySet();
        for (String key : keySet) {
            nvps.add(new BasicNameValuePair(key, params.get(key)));
        }

        log.info("set params:" + nvps);
        httppost.setEntity(new UrlEncodedFormEntity(nvps, Consts.UTF_8));

        return httppost;
    }

    /**
     * 获取post方法。
     *
     * @param url
     * @param params
     * @return
     */
    private HttpPost postForm(String url, Map<String, String> params, String cookieValue) {
        HttpPost httppost = new HttpPost(url);
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();

        // 组装参数。
        Set<String> keySet = params.keySet();
        for (String key : keySet) {
            nvps.add(new BasicNameValuePair(key, params.get(key)));
        }

        log.info("set params:" + nvps);
        httppost.setEntity(new UrlEncodedFormEntity(nvps, Consts.UTF_8));
        httppost.addHeader("Authorization", "your token"); //认证token
        httppost.addHeader("Content-Type", "application/json");
        httppost.addHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/68.0.3440.106 Safari/537.36");
        //httppost.addHeader("Cookie",cookieValue );
        return httppost;
    }


    /**
     * 获取post方法。
     *
     * @param url
     * @param params
     * @return
     */
    private HttpPost postForm(String url, List<NameValuePair> params) {
        HttpPost httpost = new HttpPost(url);
        log.info("set params:" + params);
        httpost.setEntity(new UrlEncodedFormEntity(params, Consts.UTF_8));
        return httpost;
    }


    /**
     * 增加消息头。
     *
     * @param httpost
     */
    private void addHeaders(HttpUriRequest httpost) {
        Iterator<Entry<String, String>> it = this._headers.entrySet()
                .iterator();
        Entry<String, String> entry = null;
        String name = null;
        String value = null;

        while (it.hasNext()) {
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
    public void shutdown() {
        _httpclient.getConnectionManager().shutdown();
        log.info("shutdown _httpclient ...");
    }

    public Map<String, String> getCookies() {
        Map<String, String> map = new HashMap<String, String>();
        for (Cookie cookie : cookieStore.getCookies()) {
            map.put(cookie.getName(), cookie.getValue());
        }
        return map;
    }

    public String getToken() {
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

    public String getCookie() {
        return cookie;
    }

    public void setCookie(String cookie) {
        this.cookie = cookie;
    }

    /**
     * 适用于接口带token的，默认false
     *
     * @param flag
     * @param uri
     * @param params
     * @param pairs
     */
    public synchronized HttpResult post(Flag flag, String uri, List<NameValuePair> params, Map<String, String> pairs) {
        // 2. 创建请求方法的实例，并指定请求URL，添加请求参数。
        String url = "";
        if (flag.equals(Flag.UIC))
            url = BaseTest.uicurl + uri + "?";
        for (int i = 0; i < params.size(); i++) {
            url += params.get(i).getName() + "=" + params.get(i).getValue() + "&";
        }
        url = url.substring(0, url.length() - 1);

        HttpPost post = new HttpPost(url);
        post.addHeader("Content-type", "application/x-www-form-urlencoded;charset=UTF-8");
        post.setHeader("Accept", "application/json");

        setHttpHeaderCookie(post);

        log.info("create httppost : " + url);
        log.info("set params:" + pairs);
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();

        // 组装参数。
        Set<String> keySet = pairs.keySet();

        for (String key : keySet) {
            nvps.add(new BasicNameValuePair(key, pairs.get(key)));
        }

        log.info("set params:" + nvps);
        post.setEntity(new UrlEncodedFormEntity(nvps, Consts.UTF_8));
        //请求
        HttpResult response = invoke(post);

        setHttpResonseCookie(response);

        checkResponseException(response, uri);
        return response;
    }

    /**
     * 用于接口只带一个int类
     *
     * @param flag
     * @param uri
     * @param param
     * @return
     */
    public synchronized HttpResult post(Flag flag, String uri, int param) {
        // 2. 创建请求方法的实例，并指定请求URL，添加请求参数。
        String url = "";
        if (flag.equals(Flag.UIC))
            url = BaseTest.uicurl + uri + param;

        HttpPost post = new HttpPost(url);
        post.addHeader("Content-type", "application/x-www-form-urlencoded");
        post.setHeader("Accept", "application/json");

        setHttpHeaderCookie(post);

        log.info("create httppost : " + url);

        //请求
        HttpResult response = invoke(post);

        setHttpResonseCookie(response);
        checkResponseException(response, uri);
        return response;
    }

    public synchronized HttpResult put(Flag flag, String uri, String jsonObj) {
        // 2. 创建请求方法的实例，并指定请求URL，添加请求参数。
        String url = "";
        if (flag.equals(Flag.UIC))
            url = BaseTest.uicurl + uri;

        HttpPut put = new HttpPut(url);
        put.addHeader("Content-type", "application/json; charset=utf-8");
        put.setHeader("Accept", "application/json");

        setHttpHeaderCookie(put);

        log.info("jsonObj:" + jsonObj);
        put.setEntity(new StringEntity(jsonObj, Charset.forName("UTF-8")));

        log.info("create httpput : " + url);

        // 请求
        HttpResult response = invoke(put);

        setHttpResonseCookie(response);

        checkResponseException(response, uri);
        return response;

    }

    private void checkResponseException(HttpResult result, String uri) {
        if (result.getCode() == HttpStatus.SC_OK)
            if (result.getBody() != null)
                Assert.assertFalse(result.getBody().contains("EX_0_0_SYS_00_00_000"), uri + "提示:" + result.getBody());

    }


}
