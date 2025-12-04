package com.coolcollege.intelligent.service.baili.impl;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.http.HttpRestTemplateService;
import com.coolcollege.intelligent.model.baili.request.BailiBaseRequest;
import com.coolcollege.intelligent.model.baili.request.BailiEmployeeRequest;
import com.coolcollege.intelligent.model.baili.request.BailiOrgRequest;
import com.coolcollege.intelligent.model.baili.request.BailiStoreRequest;
import com.coolcollege.intelligent.model.baili.response.BailiEmployeeResponse;
import com.coolcollege.intelligent.model.baili.response.BailiOrgResponse;
import com.coolcollege.intelligent.model.baili.response.BailiPageResponseBase;
import com.coolcollege.intelligent.model.baili.response.BailiStoreResponse;
import com.coolcollege.intelligent.service.baili.EhrService;
import com.coolcollege.intelligent.common.util.MD5Util;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2021/08/04
 */
@Service
@Slf4j
public class EhrServiceImpl implements EhrService {

    @Value("${baili.ehr.url}")
    private String url;
    @Value("${baili.sysCode}")
    private String sysCode;
    @Value("${baili.secret}")
    private String secret;

    @Autowired
    private HttpRestTemplateService httpRestTemplateService;

    private final static TypeReference<BailiPageResponseBase<BailiEmployeeResponse>> EMPLOYEE_PAGE_RESPONSE =
            new TypeReference<BailiPageResponseBase<BailiEmployeeResponse>>() {};
    private final static TypeReference<BailiPageResponseBase<BailiOrgResponse>> ORG_PAGE_RESPONSE =
            new TypeReference<BailiPageResponseBase<BailiOrgResponse>>() {};
    private final static TypeReference<BailiPageResponseBase<BailiStoreResponse>> STORE_PAGE_RESPONSE =
            new TypeReference<BailiPageResponseBase<BailiStoreResponse>>() {};

    @Override
    public BailiPageResponseBase<BailiEmployeeResponse> listEmployeeBaseInfo(BailiEmployeeRequest request) {

        request.setApi("employee.base.info");
        fillReqeuest(request);
        String result = httpRestTemplateService.postForObject(url, request, String.class);
        BailiPageResponseBase<BailiEmployeeResponse> response = JSONObject.parseObject(result, EMPLOYEE_PAGE_RESPONSE);
        if(response==null){
            throw new ServiceException(ErrorCodeEnum.BAI_LI_API_ERROR);
        }
        if(response.getResult()==1){
            log.error("百丽获取用户信息接口错误，request={},response={}",request,response);
            throw new ServiceException(ErrorCodeEnum.BAI_LI_API_ERROR.getCode(),response.getMessage());
        }
        return response;
    }

    @Override
    public BailiPageResponseBase<BailiOrgResponse> listOrg(BailiOrgRequest request) {
        request.setApi("org.unit");
        fillReqeuest(request);
        String result = httpRestTemplateService.postForObject(url, request, String.class);
        log.info(result);
        BailiPageResponseBase<BailiOrgResponse> response = JSONObject.parseObject(result, ORG_PAGE_RESPONSE);
        if(response==null){
            throw new ServiceException(ErrorCodeEnum.BAI_LI_API_ERROR);
        }
        if(response.getResult()==1){
            log.error("百丽获取组织架构接口错误，request={},response={}",request,response);
            throw new ServiceException(ErrorCodeEnum.BAI_LI_API_ERROR.getCode(),response.getMessage());
        }
        return response;
    }

    @Override
    public BailiPageResponseBase<BailiStoreResponse> liststoreInfo(BailiStoreRequest request) {
        request.setApi("operation.store.info");
        fillReqeuest(request);
        String result = httpRestTemplateService.postForObject(url, request, String.class);
        BailiPageResponseBase<BailiStoreResponse> response = JSONObject.parseObject(result, STORE_PAGE_RESPONSE);
        if(response==null){
            throw new ServiceException(ErrorCodeEnum.BAI_LI_API_ERROR);
        }
        if(response.getResult()==1){
            log.error("百丽获取营运下的管辖范围接口错误，request={},response={}",request,response);
            throw new ServiceException(ErrorCodeEnum.BAI_LI_API_ERROR.getCode(),response.getMessage());
        }
        return response;
    }

    private void fillReqeuest(BailiBaseRequest request) {
        request.setSysCode(sysCode);
        long timeStamp = System.currentTimeMillis();
        request.setTimeStamp(timeStamp);
        String accessToken = StringUtils.upperCase(MD5Util.md5(secret + request.getApi() + timeStamp));
        request.setAccessToken(accessToken);
    }

    public static void main(String[] args) {
        String accessToken = StringUtils.upperCase(MD5Util.md5("7094D349C785679E" + "employee.base.info" + "1628067859759"));
        System.out.println(accessToken);

    }
}
