package com.coolcollege.intelligent.service.senyu.impl;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.http.HttpRestTemplateService;
import com.coolcollege.intelligent.model.senyu.request.SenYuBaseRequest;
import com.coolcollege.intelligent.model.senyu.request.SenYuEmployeeInfoRequest;
import com.coolcollege.intelligent.model.senyu.request.SenYuStoreRequest;
import com.coolcollege.intelligent.model.senyu.response.*;
import com.coolcollege.intelligent.service.senyu.SenYuService;
import com.coolcollege.intelligent.common.util.MD5Util;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * describe: 森宇接口对接
 *
 * @author wxp
 * @date 2021/09/07
 */
@Service
@Slf4j
public class SenYuServiceImpl implements SenYuService {

    @Value("${senyu.url}")
    private String url;

    @Value("${senyu.publickey}")
    private String senYuPublicKey;

    @Autowired
    private HttpRestTemplateService httpRestTemplateService;

    private final static TypeReference<SenYuBaseResponse<String>> TEST_RESPONSE =
            new TypeReference<SenYuBaseResponse<String>>() {};

    private final static TypeReference<SenYuBaseResponse<SenYuEmployeeInfoResponse>> EMPLOYEE_INFO_RESPONSE =
            new TypeReference<SenYuBaseResponse<SenYuEmployeeInfoResponse>>() {};

    private final static TypeReference<SenYuBaseResponse<SenYuStorePageResponse>> STORE_PAGE_RESPONSE =
            new TypeReference<SenYuBaseResponse<SenYuStorePageResponse>>() {};

    private final static TypeReference<SenYuBaseListResponse<SenYuStoreResponse>> AUTH_STORE_RESPONSE =
            new TypeReference<SenYuBaseListResponse<SenYuStoreResponse>>() {};

    private final static TypeReference<SenYuBaseListResponse<SenYuRoleResponse>> ROLE_RESPONSE =
            new TypeReference<SenYuBaseListResponse<SenYuRoleResponse>>() {};

    private final static TypeReference<SenYuBaseListResponse<SenYuEmployeeInfoResponse>> EMPLOYEE_LIST_RESPONSE =
            new TypeReference<SenYuBaseListResponse<SenYuEmployeeInfoResponse>>() {};

    @Override
    public SenYuBaseResponse<String> test(SenYuBaseRequest request) {
        fillReqeuest(request);
        String result = httpRestTemplateService.postForObject(url, request, String.class);
        SenYuBaseResponse<String> response = JSONObject.parseObject(result, TEST_RESPONSE);
        if(response==null){
            throw new ServiceException(ErrorCodeEnum.SEN_YU_API_ERROR);
        }
        if(!response.getCode().equals("200")){
            log.error("森宇测试接口错误，request={},response={}",request,response);
            throw new ServiceException(ErrorCodeEnum.SEN_YU_API_ERROR.getCode(),response.getMsg());
        }
        return response;
    }

    // 根据身份证获取用户信息
    @Override
    public SenYuBaseResponse<SenYuEmployeeInfoResponse> getEmployeeInfoByIdCard(SenYuEmployeeInfoRequest request) {
        request.setApi("getEmployeeInfoByIdCard");
        fillReqeuest(request);
        String result = httpRestTemplateService.postForObject(url, request, String.class);
        SenYuBaseResponse<SenYuEmployeeInfoResponse> response = JSONObject.parseObject(result, EMPLOYEE_INFO_RESPONSE);
        if(response==null){
            throw new ServiceException(ErrorCodeEnum.SEN_YU_API_ERROR);
        }
        if(!"200".equals(response.getCode())){
            log.error("根据身份证获取森宇用户信息接口错误，request={},response={}",request,response);
            throw new ServiceException(ErrorCodeEnum.SEN_YU_API_ERROR.getCode(),response.getMsg());
        }
        return response;
    }

    // 获取全部门店列表的接口
    @Override
    public SenYuBaseResponse<SenYuStorePageResponse> listAllStoreByPage(SenYuStoreRequest request) {
        request.setApi("listAllStoreByPage");
        fillReqeuest(request);
        String result = httpRestTemplateService.postForObject(url, request, String.class);
        SenYuBaseResponse<SenYuStorePageResponse> response = JSONObject.parseObject(result, STORE_PAGE_RESPONSE);
        if(response==null){
            throw new ServiceException(ErrorCodeEnum.SEN_YU_API_ERROR);
        }
        if(!"200".equals(response.getCode())){
            log.error("获取全部门店列表接口错误，request={},response={}",request,response);
            throw new ServiceException(ErrorCodeEnum.SEN_YU_API_ERROR.getCode(),response.getMsg());
        }
        return response;
    }
    // 获取用户直接管辖的门店列表
    @Override
    public SenYuBaseListResponse<SenYuStoreResponse> listAuthStores(SenYuEmployeeInfoRequest request) {
        request.setApi("listAuthStores");
        fillReqeuest(request);
        String result = httpRestTemplateService.postForObject(url, request, String.class);
        SenYuBaseListResponse<SenYuStoreResponse> response = JSONObject.parseObject(result, AUTH_STORE_RESPONSE);
        if(response==null){
            throw new ServiceException(ErrorCodeEnum.SEN_YU_API_ERROR);
        }
        if(!"200".equals(response.getCode())){
            log.error("获取用户直接管辖的门店列表接口错误，request={},response={}",request,response);
            throw new ServiceException(ErrorCodeEnum.SEN_YU_API_ERROR.getCode(),response.getMsg());
        }
        return response;
    }
    // 获取所有岗位的接口
    @Override
    public SenYuBaseListResponse<SenYuRoleResponse> listAllRoles(SenYuBaseRequest request) {
        request.setApi("listAllRoles");
        fillReqeuest(request);
        String result = httpRestTemplateService.postForObject(url, request, String.class);
        SenYuBaseListResponse<SenYuRoleResponse> response = JSONObject.parseObject(result, ROLE_RESPONSE);
        if(response==null){
            throw new ServiceException(ErrorCodeEnum.SEN_YU_API_ERROR);
        }
        if(!"200".equals(response.getCode())){
            log.error("获取所有岗位的接口接口错误，request={},response={}",request,response);
            throw new ServiceException(ErrorCodeEnum.SEN_YU_API_ERROR.getCode(),response.getMsg());
        }
        return response;
    }
    // 根据上级编码查询直属员⼯的接⼝
    @Override
    public SenYuBaseListResponse<SenYuEmployeeInfoResponse> listDirectEmployees(SenYuEmployeeInfoRequest request) {
        request.setApi("listDirectEmployees");
        fillReqeuest(request);
        String result = httpRestTemplateService.postForObject(url, request, String.class);
        SenYuBaseListResponse<SenYuEmployeeInfoResponse> response = JSONObject.parseObject(result, EMPLOYEE_LIST_RESPONSE);
        if(response==null){
            throw new ServiceException(ErrorCodeEnum.SEN_YU_API_ERROR);
        }
        if(!"200".equals(response.getCode())){
            log.error("根据上级编码查询直属员⼯的接口错误，request={},response={}",request,response);
            throw new ServiceException(ErrorCodeEnum.SEN_YU_API_ERROR.getCode(),response.getMsg());
        }
        return response;
    }

    @Override
    public SenYuBaseListResponse<SenYuEmployeeInfoResponse> listEmployeesByRoldIds(SenYuEmployeeInfoRequest request) {
        request.setApi("listEmployeesByRoldIds");
        fillReqeuest(request);
        String result = httpRestTemplateService.postForObject(url, request, String.class);
        SenYuBaseListResponse<SenYuEmployeeInfoResponse> response = JSONObject.parseObject(result, EMPLOYEE_LIST_RESPONSE);
        if(response==null){
            throw new ServiceException(ErrorCodeEnum.SEN_YU_API_ERROR);
        }
        if(!"200".equals(response.getCode())){
            log.error("根据岗位编码查询员⼯的接口错误，request={},response={}",request,response);
            throw new ServiceException(ErrorCodeEnum.SEN_YU_API_ERROR.getCode(),response.getMsg());
        }
        return response;
    }


    private void fillReqeuest(SenYuBaseRequest request) {
        long timeStamp = System.currentTimeMillis();
        request.setTimeStamp(timeStamp);
        String sign = StringUtils.upperCase(MD5Util.md5(timeStamp + senYuPublicKey));
        request.setSign(sign);
    }

    public static void main(String[] args) {
        String sign = StringUtils.upperCase(MD5Util.md5("1628067859759" + "senyustore123abc" ));
        System.out.println(sign);
    }


}
