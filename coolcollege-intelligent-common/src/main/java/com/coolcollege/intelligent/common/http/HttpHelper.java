package com.coolcollege.intelligent.common.http;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.exception.ServiceException;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;


public class HttpHelper {

    private final static Logger logger = LoggerFactory.getLogger(HttpHelper.class);

    public static JSONObject httpGet(String url) throws ServiceException {

        HttpGet httpGet = new HttpGet(url);
        CloseableHttpResponse response = null;
        CloseableHttpClient httpClient = HttpClients.createDefault();
        RequestConfig requestConfig = RequestConfig.custom().
                setSocketTimeout(2000).setConnectTimeout(2000).build();
        httpGet.setConfig(requestConfig);

        try {
            response = httpClient.execute(httpGet, new BasicHttpContext());

            if (response.getStatusLine().getStatusCode() != 200) {

                System.out.println("request url failed, http code=" + response.getStatusLine().getStatusCode()
                        + ", url=" + url);
                return null;
            }

            HttpEntity entity = response.getEntity();

            if (entity != null) {
                String resultStr = EntityUtils.toString(entity, "utf-8");

                JSONObject result = JSON.parseObject(resultStr);
                if (result.getInteger("errcode") == 0) {
//                	result.remove("errcode");
//                	result.remove("errmsg");
                    return result;
                } else {
                    System.out.println("request url=" + url + ",return value=");
                    System.out.println(resultStr);
                    int errCode = result.getInteger("errcode");
                    String errMsg = result.getString("errmsg");
                    throw new ServiceException(errCode, errMsg);
                }
            }
        } catch (IOException e) {
            System.out.println("request url=" + url + ", exception, msg=" + e.getMessage());
            e.printStackTrace();
        } finally {
            if (response != null) try {
                response.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }


    public static JSONObject httpGetCrm(String url) {

        HttpGet httpGet = new HttpGet(url);

        CloseableHttpResponse response = null;

        CloseableHttpClient httpClient = HttpClients.createDefault();

        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(2000).setConnectTimeout(2000).build();

        httpGet.setConfig(requestConfig);

        try {

            logger.info("request url：" + url);

            response = httpClient.execute(httpGet, new BasicHttpContext());

            if (response.getStatusLine().getStatusCode() != 200) {
                logger.info("request url failed, http code={},url:{}", response.getStatusLine().getStatusCode(), url);
                return null;
            }

            HttpEntity entity = response.getEntity();

            if (entity != null) {

                String resultStr = EntityUtils.toString(entity, "utf-8");

                logger.info("response：" + resultStr);

                JSONObject result = JSON.parseObject(resultStr);

                if (result == null || !result.containsKey("errcode")) {
                    return result;
                }

                if (result.getInteger("errcode") == 0) {
                    return result;
                } else {
                    int errCode = result.getInteger("errcode");
                    String errMsg = result.getString("errmsg");
                    throw new ServiceException(errCode, errMsg);
                }
            }
        } catch (IOException e) {
            logger.info("request url={}, exception, msg={}", url, e.getMessage());
            e.printStackTrace();
        } finally {
            if (response != null) try {
                response.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }


    public static JSONObject httpPost(String url, Object data) throws ServiceException {
        HttpPost httpPost = new HttpPost(url);
        CloseableHttpResponse response = null;
        CloseableHttpClient httpClient = HttpClients.createDefault();
        RequestConfig requestConfig = RequestConfig.custom().
                setSocketTimeout(2000).setConnectTimeout(2000).build();
        httpPost.setConfig(requestConfig);
        httpPost.addHeader("Content-Type", "application/json");

        try {
            StringEntity requestEntity = new StringEntity(JSON.toJSONString(data), "utf-8");

            httpPost.setEntity(requestEntity);

            response = httpClient.execute(httpPost, new BasicHttpContext());

            if (response.getStatusLine().getStatusCode() != 200) {

                System.out.println("request url failed, http code=" + response.getStatusLine().getStatusCode()
                        + ", url=" + url);
                return null;
            }
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                String resultStr = EntityUtils.toString(entity, "utf-8");

                System.out.println("resultStr:" + resultStr);
                JSONObject result = JSON.parseObject(resultStr.replace("\\", ""));
                if (result.getInteger("errcode") == 0) {
                    result.remove("errcode");
                    result.remove("errmsg");
                    return result;
                } else {
                    System.out.println("request url=" + url + ",return value=");
                    System.out.println(resultStr);
                    int errCode = result.getInteger("errcode");
                    String errMsg = result.getString("errmsg");
                    throw new ServiceException(errCode, errMsg);
                }
            }
        } catch (IOException e) {
            System.out.println("request url=" + url + ", exception, msg=" + e.getMessage());
            e.printStackTrace();
        } finally {
            if (response != null) try {
                response.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }


    public static JSONObject post(String url, Object data) throws ServiceException {
        HttpPost httpPost = new HttpPost(url);
        CloseableHttpResponse response = null;
        CloseableHttpClient httpClient = HttpClients.createDefault();
        RequestConfig requestConfig = RequestConfig.custom().
                setSocketTimeout(2000).setConnectTimeout(2000).build();
        httpPost.setConfig(requestConfig);
        httpPost.addHeader("Content-Type", "application/json");

        try {
            StringEntity requestEntity = new StringEntity(JSON.toJSONString(data), "utf-8");

            httpPost.setEntity(requestEntity);

            response = httpClient.execute(httpPost, new BasicHttpContext());

            if (response.getStatusLine().getStatusCode() != 200) {

                System.out.println("request url failed, http code=" + response.getStatusLine().getStatusCode()
                        + ", url=" + url);
                return null;
            }
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                String resultStr = EntityUtils.toString(entity, "utf-8");

                //System.out.println("resultStr:"+resultStr);
                JSONObject result = JSON.parseObject(resultStr.replace("\\", ""));
                return result;
                /*if (result.getInteger("errcode") == 0) {
                    result.remove("errcode");
                    result.remove("errmsg");
                    return result;
                } else {
                    System.out.println("request url=" + url + ",return value=");
                    System.out.println(resultStr);
                    int errCode = result.getInteger("errcode");
                    String errMsg = result.getString("errmsg");
                    throw new OApiException(errCode, errMsg);
                }*/
            }
        } catch (IOException e) {
            System.out.println("request url=" + url + ", exception, msg=" + e.getMessage());
            e.printStackTrace();
        } finally {
            if (response != null) try {
                response.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    public static JSONObject get(String url) throws ServiceException {

        HttpGet httpGet = new HttpGet(url);
        CloseableHttpResponse response = null;
        CloseableHttpClient httpClient = HttpClients.createDefault();
        RequestConfig requestConfig = RequestConfig.custom().
                setSocketTimeout(2000).setConnectTimeout(2000).build();
        httpGet.setConfig(requestConfig);

        try {
            response = httpClient.execute(httpGet, new BasicHttpContext());

            if (response.getStatusLine().getStatusCode() != 200) {

                System.out.println("request url failed, http code=" + response.getStatusLine().getStatusCode()
                        + ", url=" + url);
                return null;
            }

            HttpEntity entity = response.getEntity();

            if (entity != null) {
                String resultStr = EntityUtils.toString(entity, "utf-8");

                JSONObject result = JSON.parseObject(resultStr);
                return result;
                /*if (result.getInteger("errcode") == 0) {
//                	result.remove("errcode");
//                	result.remove("errmsg");
                    return result;
                } else {
                    System.out.println("request url=" + url + ",return value=");
                    System.out.println(resultStr);
                    int errCode = result.getInteger("errcode");
                    String errMsg = result.getString("errmsg");
                    throw new OApiException(errCode, errMsg);
                }*/
            }
        } catch (IOException e) {
            System.out.println("request url=" + url + ", exception, msg=" + e.getMessage());
            e.printStackTrace();
        } finally {
            if (response != null) try {
                response.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }


    public static String getIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    public static String checkTreminal(HttpServletRequest request) {

        String browser = request.getHeader("User-Agent");
        logger.info("checkTreminal browser:{}", browser);
        String treminal = "";
        if (browser.contains("dingtalk-win")) {
            treminal = "Ding_PC";
        } else if (browser.contains("iPhone") || browser.contains("Android")) {
            treminal = "Mobile";
        } else {
            treminal = "Web";
        }
        return treminal;
    }


/*
    private SearchLog initSearchLog(HttpServletRequest request) {

// userAgent中有很多获取请求信息的方法
        UserAgent
                userAgent = UserAgent.parseUserAgentString(request.getHeader("User-Agent"));
        SearchLog searchLog = new SearchLog();

        searchLog.setSearchExpression(getParam("searchExpression", request));
        searchLog.setUserId(SessionUtil.getUserId());
        searchLog.setUsername(SessionUtil.getCurrentUserName());

// searchLog.setSearchTime(new DateTime().toString("yyyy-MM-dd HH:mm:ss"));
        searchLog.setSearchTime(new Date());


// 获取客户端请求的浏览器类型
        searchLog.setBrowserType(userAgent.getBrowser().toString());

        String s = request.getParameter("searchChannel");
        searchLog.setSearchChannel(request.getParameter("searchChannel"));

// 获得终端设备的IP地址
        searchLog.setTerminalIp(request.getRemoteAddr());

        return searchLog;
    }*/


//	public static JSONObject uploadMedia(String url, File file) throws OApiException {
//        HttpPost httpPost = new HttpPost(url);
//        CloseableHttpResponse response = null;
//        CloseableHttpClient httpClient = HttpClients.createDefault();
//        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(2000).setConnectTimeout(2000).build();
//        httpPost.setConfig(requestConfig);
//
//        HttpEntity requestEntity = MultipartEntityBuilder.create().addPart("media",
//        		new FileBody(file, ContentType.APPLICATION_OCTET_STREAM, file.getName())).build();
//        httpPost.setEntity(requestEntity);
//
//        try {
//            response = httpClient.execute(httpPost, new BasicHttpContext());
//
//            if (response.getStatusLine().getStatusCode() != 200) {
//
//                System.out.println("request url failed, http code=" + response.getStatusLine().getStatusCode()
//                                   + ", url=" + url);
//                return null;
//            }
//            HttpEntity entity = response.getEntity();
//            if (entity != null) {
//                String resultStr = EntityUtils.toString(entity, "utf-8");
//
//                JSONObject result = JSON.parseObject(resultStr);
//                if (result.getInteger("errcode") == 0) {
//                    // 成功
//                	result.remove("errcode");
//                	result.remove("errmsg");
//                    return result;
//                } else {
//                    System.out.println("request url=" + url + ",return value=");
//                    System.out.println(resultStr);
//                    int errCode = result.getInteger("errcode");
//                    String errMsg = result.getString("errmsg");
//                    throw new OApiException(errCode, errMsg);
//                }
//            }
//        } catch (IOException e) {
//            System.out.println("request url=" + url + ", exception, msg=" + e.getMessage());
//            e.printStackTrace();
//        } finally {
//            if (response != null) try {
//                response.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//
//        return null;
//    }


//	public static JSONObject downloadMedia(String url, String fileDir) throws OApiException {
//        HttpGet httpGet = new HttpGet(url);
//        CloseableHttpResponse response = null;
//        CloseableHttpClient httpClient = HttpClients.createDefault();
//        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(2000).setConnectTimeout(2000).build();
//        httpGet.setConfig(requestConfig);
//
//        try {
//            HttpContext localContext = new BasicHttpContext();
//
//            response = httpClient.execute(httpGet, localContext);
//
//            RedirectLocations locations = (RedirectLocations) localContext.getAttribute(HttpClientContext.REDIRECT_LOCATIONS);
//            if (locations != null) {
//                URI downloadUrl = locations.getAll().get(0);
//                String filename = downloadUrl.toURL().getFile();
//                System.out.println("downloadUrl=" + downloadUrl);
//                File downloadFile = new File(fileDir + File.separator + filename);
//                FileUtils.writeByteArrayToFile(downloadFile, EntityUtils.toByteArray(response.getEntity()));
//                JSONObject obj = new JSONObject();
//                obj.put("downloadFilePath", downloadFile.getAbsolutePath());
//                obj.put("httpcode", response.getStatusLine().getStatusCode());
//
//
//
//                return obj;
//            } else {
//                if (response.getStatusLine().getStatusCode() != 200) {
//
//                    System.out.println("request url failed, http code=" + response.getStatusLine().getStatusCode()
//                                       + ", url=" + url);
//                    return null;
//                }
//                HttpEntity entity = response.getEntity();
//                if (entity != null) {
//                    String resultStr = EntityUtils.toString(entity, "utf-8");
//
//                    JSONObject result = JSON.parseObject(resultStr);
//                    if (result.getInteger("errcode") == 0) {
//                        // 成功
//                    	result.remove("errcode");
//                    	result.remove("errmsg");
//                        return result;
//                    } else {
//                        System.out.println("request url=" + url + ",return value=");
//                        System.out.println(resultStr);
//                        int errCode = result.getInteger("errcode");
//                        String errMsg = result.getString("errmsg");
//                        throw new OApiException(errCode, errMsg);
//                    }
//                }
//            }
//        } catch (IOException e) {
//            System.out.println("request url=" + url + ", exception, msg=" + e.getMessage());
//            e.printStackTrace();
//        } finally {
//            if (response != null) try {
//                response.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//
//        return null;
//    }
}
