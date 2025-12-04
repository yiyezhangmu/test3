package com.coolcollege.intelligent.common.util;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.codec.Charsets;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;

import com.google.common.base.Joiner;

import lombok.extern.slf4j.Slf4j;

/**
 * HTTP请求工具
 */
@Slf4j
public class HttpClientUtils {

    /**
     * HTTP Client
     */
    private static final CloseableHttpClient HTTPCLIENT;

    /**
     * Connection Manager
     */
    private static PoolingHttpClientConnectionManager connectionManager;

    /**
     * Request Config
     */
    private static RequestConfig requestConfig;

    /**
     * 连接池最大连接数
     */
    public static final int MAX_TOTAL = 200;

    /**
     * 每个路由最大连接数
     */
    public static final int MAX_PER_ROUTE = 20;

    /**
     * 连接池获取到连接的超时时间
     */
    public static final int REQ_TIMEOUT = 5000;

    /**
     * 建立连接的超时时间
     */
    public static final int CONN_TIMEOUT = 5000;

    /**
     * 获取数据的超时时间
     */
    public static final int SOCK_TIMEOUT = 5000;

    /**
     * 默认返回结果
     */
    public static final String DEFAULT_RESULT = "{}";

    /**
     * 默认编码UTF-8
     */
    public static final Charset DEFAULT_CHARSET = Charsets.UTF_8;

    /**
     * Content Type : JSON
     */
    private static final String CONTENT_TYPE_JSON = "application/json;charset=utf-8";

    /**
     * Http Method Get
     */
    public static final String HTTP_METHOD_GET = "get";

    /**
     * Http Method Post
     */
    public static final String HTTP_METHOD_POST = "post";

    /**
     * Http Method Put
     */
    public static final String HTTP_METHOD_PUT = "put";

    /**
     * Http Method Delete
     */
    public static final String HTTP_METHOD_DELETE = "delete";

    static {
        connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(MAX_TOTAL);
        connectionManager.setDefaultMaxPerRoute(MAX_PER_ROUTE);
        requestConfig = RequestConfig.custom().setConnectionRequestTimeout(REQ_TIMEOUT).setConnectTimeout(CONN_TIMEOUT)
            .setSocketTimeout(SOCK_TIMEOUT).build();
        HTTPCLIENT =
            HttpClients.custom().setConnectionManager(connectionManager).setDefaultRequestConfig(requestConfig).build();
    }

    public CloseableHttpClient getHttpClient() {
        return HTTPCLIENT;
    }

    /**
     * Get请求
     *
     * @param uri
     * @return
     */
    public static String get(String uri) {
        String result = DEFAULT_RESULT;
        HttpRequestBase httpRequest = null;
        CloseableHttpResponse httpResponse = null;
        try {
            httpRequest = new HttpGet(uri);
            httpResponse = HTTPCLIENT.execute(httpRequest);
            if (isHttpRequestOk(httpResponse)) {
                log.error("Http Request Failed: uri={}, response={}", httpResponse.getStatusLine());
                return result;
            }
            result = EntityUtils.toString(httpResponse.getEntity(), DEFAULT_CHARSET);
        } catch (IOException e) {
            log.error("Http Request Occur Exception: uri={}, exception={}", uri, e);
        } finally {
            release(httpResponse);
        }

        return result;
    }

    /**
     * Get请求
     *
     * @param uri
     * @return
     */
    public static String sendGetRequestParam(String uri, Map<String, String> params, Map<String, String> headers) {
        String result = DEFAULT_RESULT;
        if (Objects.nonNull(params) && !params.isEmpty()) {
            StringBuilder builder = new StringBuilder(uri);
            if (!uri.contains("?")) {
                builder.append("?");
            }
            builder.append(Joiner.on("&").withKeyValueSeparator("=").join(params));
            uri = builder.toString();
        }
        HttpRequestBase httpRequest = null;
        CloseableHttpResponse httpResponse = null;
        try {
            httpRequest = new HttpGet(uri);
            if (Objects.nonNull(headers) && !headers.isEmpty()) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    httpRequest.setHeader(entry.getKey(), entry.getValue());
                }
            }
            httpResponse = HTTPCLIENT.execute(httpRequest);
            if (isHttpRequestOk(httpResponse)) {
                log.error("Http Request Failed: uri={}, response={}", httpResponse.getStatusLine());
                return result;
            }
            result = EntityUtils.toString(httpResponse.getEntity(), DEFAULT_CHARSET);
        } catch (IOException e) {
            log.error("Http Request Occur Exception: url={}, exception={}", uri, e);
        } finally {
            release(httpResponse);
        }

        return result;
    }

    /**
     * 判断HttpRequest请求是否Ok
     *
     * @param httpResponse
     * @return true-Ok；false-非Ok
     */
    private static boolean isHttpRequestOk(CloseableHttpResponse httpResponse) {
        return httpResponse == null || httpResponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK;
    }

    /**
     * 释放HttpResponse相关资源
     *
     * @param httpResponse
     */
    private static void release(CloseableHttpResponse httpResponse) {
        if (httpResponse != null) {
            try {
                EntityUtils.consume(httpResponse.getEntity());
                httpResponse.close();
            } catch (IOException e) {
                log.error("Occur Exception: Release HttpResponse Resource Failed!", e);
            }
        }
    }
}
