package com.coolcollege.intelligent.common.http;

import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
public class HttpRestTemplateService {
    @Autowired
    private RestTemplate restTemplate;

    /**
     * @param object
     * @param access_token
     * @return
     * @Title getHttpEntity
     * @Description 获取请求实体对象
     */
    public HttpEntity getHttpEntity(Object object, String access_token) {
        return new HttpEntity(object, this.getHeaders(access_token));
    }

    /**
     * @param uri            请求路径
     * @param httpMethod     方法类型
     * @param httpEntity     请求实体
     * @param responseEntity 返回对象
     * @param params         uri参数列表
     * @return 返回实体
     * @Title exchange
     * @Description restTemplate统一请求
     */
    public <T> ResponseEntity<T> exchange(String uri, HttpMethod httpMethod, HttpEntity httpEntity,
                                          Class<T> responseEntity, Map<String, ?> params) {
        List<String> queryStrings = Lists.newArrayList();
        if (!CollectionUtils.isEmpty(params)) {
            params.forEach((k, v) -> {
                queryStrings.add(k + "=" + v);
            });
        }
        String queryString = null;
        if (!CollectionUtils.isEmpty(queryStrings)) {
            queryString = String.join("&", queryStrings);
        }
        if (StringUtils.isNotBlank(queryString)) {
            uri = uri + "?" + queryString;
        }
        ResponseEntity<T> exchange = null;
        log.info("start exchange=uri:{},httpMethod:{},httpEntity:{},responseEntity:{},params:{}", uri,
                httpMethod.toString(), httpEntity.toString(), responseEntity.toString(),
                Objects.nonNull(params) ? params.toString() : null);
        try {
            exchange = restTemplate.exchange(uri, httpMethod, httpEntity, responseEntity);
        } catch (HttpStatusCodeException e) {
            log.error("exchange err, resp={}", e.getResponseBodyAsString());
        } catch (Exception e) {
            log.error("exchange err:", e);
        }
        log.info("end exchange=exchange:{},", exchange);
        return exchange;
    }

    /**
     * @param url
     * @param responseType
     * @param uriVariables
     * @return
     * @Title getForObject
     * @Description GET:HTTP请求封装
     */
    public <T> T getForObject(String url, Class<T> responseType, Map<String, ?> uriVariables) {
        log.info("getForObject start:url={},responseType={},uriVariables={}", url, responseType.getName(),
                JSONObject.toJSONString(uriVariables));
        T result = null;
        try {
            result = restTemplate.getForObject(transUrl(url, uriVariables), responseType, uriVariables);
        } catch (RestClientException e) {
            log.error("getForObject error:{}", e);
            throw new ServiceException(ErrorCodeEnum.DATA_ERROR.getCode(), "instance create error");
        }
        log.info("getForObject end:result={}", JSONObject.toJSONString(result));
        return result;
    }

    private String transUrl(String url, Map<String, ?> uriVariables) {
        StringBuilder finalUrl = new StringBuilder(url);
        if (Objects.isNull(uriVariables)) {
            return finalUrl.toString();
        }
        if (StringUtils.isNotBlank(url)) {
            if (url.contains("?")) {
                if (url.endsWith("?")) {
                    finalUrl.append("1=1");
                }
            } else {
                finalUrl.append("?1=1");
            }
            uriVariables.forEach((k, v) -> {
                finalUrl.append("&").append(k).append("={").append(k).append("}");
            });

        }
        return finalUrl.toString();
    }

    /**
     * @param url
     * @param request
     * @param responseType
     * @return
     * @Title postForObject
     * @Description POST:HTTP请求封装
     */
    public <T> T postForObject(String url, Object request, Class<T> responseType) {
        log.info("postForObject start:url={},request={},responseType={}", url, JSONObject.toJSONString(request),
                responseType.getName());
        T result = null;
        try {
            // 确保添加必要的请求头
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Type", "application/json");
            // 如果URL中没有access_token参数，则可能需要在请求头中添加认证信息
            HttpEntity<?> httpEntity = new HttpEntity<>(request, headers);
            result = restTemplate.postForObject(url, httpEntity, responseType);
        } catch (RestClientException e) {
            log.error("postForObject error:{}", e);
            throw new ServiceException(ErrorCodeEnum.DATA_ERROR.getCode(), "instance create error");
        }
        log.info("postForObject end:result={}", JSONObject.toJSONString(result));
        return result;
    }

    /**
     * @param url
     * @param request
     * @param responseType
     * @return
     * @Title putForObject
     * @Description Put:HTTP请求封装
     */
    public void putForObject(String url, Object request, Class responseType) {
        log.info("putForObject start:url={},request={},responseType={}", url, JSONObject.toJSONString(request),
                responseType.getName());
        try {
            restTemplate.put(url, getHttpEntity(request, null), responseType);
        } catch (RestClientException e) {
            log.error("putForObject error:{}", e);
            throw new ServiceException(ErrorCodeEnum.DATA_ERROR.getCode(), "instance create error");
        }
    }

    /**
     * @param url
     * @param request
     * @param responseType
     * @return
     * @Title deleteForObject
     * @Description Delete:HTTP请求封装
     */
    public void deleteForObject(String url, Object request, Class responseType) {
        log.info("deleteForObject start:url={},request={},responseType={}", url, JSONObject.toJSONString(request),
                responseType.getName());
        try {
            restTemplate.exchange(url, HttpMethod.DELETE, getHttpEntity(request, null), responseType);
        } catch (RestClientException e) {
            log.error("deleteForObject error:{}", e);
            throw new ServiceException(ErrorCodeEnum.DATA_ERROR.getCode(), "instance create error");
        }
    }


    public <T> T deleteForObject(String url, Class<T> responseType, Map<String, String> headers) {
        log.info("delete start: url={}, responseType={}, headers={}", url, responseType.getName(), headers);
        T result = null;
        try {
            HttpHeaders httpHeaders = new HttpHeaders();
            if (headers != null) {
                headers.forEach(httpHeaders::add);
            }
            HttpEntity<?> requestEntity = new HttpEntity<>(httpHeaders);
            ResponseEntity<T> responseEntity = restTemplate.exchange(url, HttpMethod.DELETE, requestEntity, responseType);
            result = responseEntity.getBody();
            log.info("delete success: status={}, result={}", responseEntity.getStatusCode(), result);
        } catch (RestClientException e) {
            log.error("delete error: {}", e.getMessage());
            throw new ServiceException(ErrorCodeEnum.DATA_ERROR.getCode(), "Failed to execute DELETE request: " + e.getMessage());
        }
        return result;
    }

    public <T, R> T deleteWithBody(String url, R requestBody, Class<T> responseType, Map<String, String> headers) {
        log.info("deleteWithBody start: url={}, requestBody={}, responseType={}, headers={}", url, requestBody, responseType.getName(), headers);
        T result = null;
        try {
            HttpHeaders httpHeaders = new HttpHeaders();
            if (headers != null) {
                headers.forEach(httpHeaders::add);
            }
            HttpEntity<R> requestEntity = new HttpEntity<>(requestBody, httpHeaders);
            ResponseEntity<T> responseEntity = restTemplate.exchange(url, HttpMethod.DELETE, requestEntity, responseType);
            result = responseEntity.getBody();
            log.info("deleteWithBody success: status={}, result={}", responseEntity.getStatusCode(), result);
        } catch (RestClientException e) {
            log.error("deleteWithBody error: {}", e.getMessage());
            throw new ServiceException(ErrorCodeEnum.DATA_ERROR.getCode(), "Failed to execute DELETE request with body: " + e.getMessage());
        }
        return result;
    }

    public  <T, R> T patchWithBody(String url, R requestBody, Class<T> responseType, Map<String, String> headers) {
        HttpHeaders httpHeaders = new HttpHeaders();
        if (headers != null) {
            headers.forEach(httpHeaders::add);
        }
        HttpEntity<R> requestEntity = new HttpEntity<>(requestBody, httpHeaders);
        try {
            ResponseEntity<T> responseEntity = restTemplate.exchange(url, HttpMethod.PATCH, requestEntity, responseType);
            log.info("PATCH request to {} successful", url);
            return responseEntity.getBody();
        } catch (Exception e) {
            log.error("PATCH request to {} failed: {}", url, e.getMessage());
            throw e;
        }
    }



    public String postForString(String url, Map<String, Object> map) {
        try {
            ResponseEntity<String> forEntity = restTemplate.postForEntity(url, map, String.class);
            return forEntity.getBody();
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            return e.getResponseBodyAsString();
        }
    }

    public String getForString(String url) {
        try {
            MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
            headers.add("Content-Type", "application/json;charset=utf-8");
            ResponseEntity<String> entity = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers),
                    String.class);
            return entity.getBody();
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            return e.getResponseBodyAsString();
        }
    }

    public <T> T getForObject(String url, Class<T> responseType, Map<String, ?> uriVariables , Map<String, String> headMap) {
        log.info("getForObject start:url={},responseType={},uriVariables={}", url, responseType.getName(),
                JSONObject.toJSONString(uriVariables));
        ResponseEntity<T> result = null;
        try {
            //封装请求头
            HttpHeaders headers = new HttpHeaders();
            headMap.forEach((k, v)->{
                headers.add(k, v);
            });
            HttpEntity<MultiValueMap<String, Object>> formEntity = new HttpEntity<MultiValueMap<String, Object>>(headers);
            result = exchange(url, HttpMethod.GET, formEntity, responseType, uriVariables);
        } catch (RestClientException e) {
            log.error("getForObject error:{}", e);
            throw new ServiceException(ErrorCodeEnum.UNKNOWN);
        }
        log.info("getForObject end:result={}", JSONObject.toJSONString(result));
        if(Objects.nonNull(result)){
            return result.getBody();
        }
        throw new ServiceException(ErrorCodeEnum.API_ERROR);
    }

    public <T> T postForObject(String url, Object request, Class<T> responseType, Map<String, String> headMap) {
        try {
            log.info("postForObject start:url={},request={},responseType={}, tenantAccessToken:{}", url, JSONObject.toJSONString(request), responseType.getName(), JSONObject.toJSONString(headMap));
        }catch (Exception e){
            log.info("json error");
        }
        T result = null;
        try {
            MultiValueMap<String, String> headers = new LinkedMultiValueMap();
            headMap.forEach((k, v)->{
                headers.add(k, v);
            });
            HttpEntity httpEntity = new HttpEntity(request, headers);
            result = restTemplate.postForObject(url, httpEntity, responseType);
        } catch (RestClientException e) {
            log.error("postForObject error:{}", JSONObject.toJSONString(result));
            log.error("postForObject error:{}", e);
            throw e;
        }
        log.info("postForObject end:result={}", JSONObject.toJSONString(result));
        return result;
    }


    /**
     * @param access_token
     * @return
     * @Title getHeaders
     * @Description 获取Headers
     */
    private MultiValueMap<String, String> getHeaders(String access_token) {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap();
        headers.add("X-Access-Token", access_token);
        headers.add("Content-Type", "application/json");
        return headers;
    }
}
