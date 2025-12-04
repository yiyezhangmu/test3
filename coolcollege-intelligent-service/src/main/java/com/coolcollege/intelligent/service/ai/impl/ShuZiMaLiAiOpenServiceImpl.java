package com.coolcollege.intelligent.service.ai.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.enums.AiResolveBusinessTypeEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.http.HttpRequest;
import com.coolcollege.intelligent.common.util.MD5Util;
import com.coolcollege.intelligent.common.util.UUIDUtils;
import com.coolcollege.intelligent.common.util.sign.HmacSHATool;
import com.coolcollege.intelligent.model.ai.AICommonPromptDTO;
import com.coolcollege.intelligent.model.ai.AIResolveDTO;
import com.coolcollege.intelligent.model.ai.AIResolveRequestDTO;
import com.coolcollege.intelligent.model.ai.dto.ShuZhiMaLiGetAiResultDTO;
import com.coolcollege.intelligent.model.ai.dto.ShuZhiMaLiGetTokenDTO;
import com.coolcollege.intelligent.model.ai.entity.AiModelLibraryDO;
import com.coolcollege.intelligent.model.ai.response.ShuZhiMaLiResponse;
import com.coolcollege.intelligent.rpc.config.EnterpriseConfigApiService;
import com.coolcollege.intelligent.service.ai.AIOpenService;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.coolstore.base.utils.CommonContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service("shuzimaliAIOpenServiceImpl")
public class ShuZiMaLiAiOpenServiceImpl implements AIOpenService {

    @Resource
    private RedisUtilPool redisUtilPool;
    @Resource
    private EnterpriseConfigApiService enterpriseConfigApiService;

    private final static boolean isOnline = Arrays.asList("online", "hd").contains(CommonContextUtil.getProfileName());

    // 生产环境配置
    private static final String PROD_URL = "https://afqualityinspection.antgroup.com";
    private static final String PROD_APP_KEY = "n2ufguVVJTABfR2H6Zzj";
    private static final String PROD_APP_SECRET = "Q8lN20UKeVEH9m6ZDV4YRd1OCa8dsi1p";

    // 预发/测试环境配置
    private static final String PRE_URL = "https://afqualityinspection-pre.antgroup.com";
    private static final String PRE_APP_KEY = "aftmYThfCpt4FT5xqHTE";
    private static final String PRE_APP_SECRET = "5esXyaKL6B6r7JZxk5TNTHxLG2rOdCcv";

    private String getUrl() {
        return isOnline ? PROD_URL : PRE_URL;
    }

    private String getAppKey() {
        return isOnline ? PROD_APP_KEY : PRE_APP_KEY;
    }

    private String getAppSecret() {
        return isOnline ? PROD_APP_SECRET : PRE_APP_SECRET;
    }

    private static final String BUSINESS_NO = "{0}#{1}#{2}";


    public String getToken(){
        String cacheKey = MessageFormat.format("shuzhimali_token:{0}", getAppKey());
        String token = redisUtilPool.getString(cacheKey);
        if(StringUtils.isNotBlank(token)){
            return token;
        }
        String nonce = UUIDUtils.get8UUID();
        String timestamp = String.valueOf(System.currentTimeMillis());
        String signStr = String.format("appKey=%s&nonce=%s&timestamp=%s", getAppKey(), nonce, timestamp);
        String sign = HmacSHATool.szmlmacSHA256(signStr, getAppSecret());
        log.info("nonce=" + nonce + "; timestamp=" + timestamp + "; sign=" + sign);
        String interfaceUrl = getUrl() + "/auth/token";
        Map<String, String> headMap = new HashMap<>();
        Map<String, Object> bodyMap = new HashMap<>();
        headMap.put("appKey", getAppKey());
        headMap.put("Signature", sign);
        bodyMap.put("timestamp", timestamp);
        bodyMap.put("nonce", nonce);
        try {
            ShuZhiMaLiGetTokenDTO response = sendPost(interfaceUrl, headMap, bodyMap, new TypeReference<ShuZhiMaLiGetTokenDTO>() {});
            if(Objects.isNull(response)){
                return null;
            }
            String accessToken = response.getAccessToken();
            redisUtilPool.setString(cacheKey, accessToken, response.getExpires() - Constants.THREE_HUNDRED);
            return accessToken;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String aiResolve(String enterpriseId, AICommonPromptDTO aiCommonPromptDTO, List<String> imageList, AiModelLibraryDO aiModel) {
        if(CollectionUtils.isEmpty(imageList)){
            return null;
        }
        return null;
    }

    public ShuZhiMaLiGetAiResultDTO getAiResult(String enterpriseId, AiResolveBusinessTypeEnum businessType, AIResolveRequestDTO request) {
        String interfaceUrl = getUrl() + "/api/afqi/outInspect/queryFileDetectResult";
        String token = getToken();
        Map<String, String> headMap = new HashMap<>();
        Pair<String, List<String>> aiImageOrBusinessId = AIOpenService.getAiImageOrBusinessId(businessType, request);
        if(Objects.isNull(aiImageOrBusinessId)){
            return null;
        }
        String businessId = aiImageOrBusinessId.getKey();
        String outBizNo = MessageFormat.format(BUSINESS_NO, enterpriseId, businessType.getCode(), businessId);
        headMap.put("Authorization", "Bearer " + token);
        Map<String, Object> bodyMap = new HashMap<>();
        bodyMap.put("outBizNo", outBizNo);
        ShuZhiMaLiGetAiResultDTO data  = sendPost(interfaceUrl, headMap, bodyMap, new TypeReference<ShuZhiMaLiGetAiResultDTO>(){});
        return data;
    }

    @Override
    public AIResolveDTO asyncAiResolve(String enterpriseId, AiResolveBusinessTypeEnum businessType, AIResolveRequestDTO request) {
        String enterpriseName = enterpriseConfigApiService.getEnterpriseName(enterpriseId);;
        String interfaceUrl = getUrl() + "/api/afqi/outInspect/fileDetect";
        String token = getToken();
        Map<String, String> headMap = new HashMap<>();
        Pair<String, List<String>> aiImageOrBusinessId = AIOpenService.getAiImageOrBusinessId(businessType, request);
        if(Objects.isNull(aiImageOrBusinessId)){
            return null;
        }
        List<String> aiImage = aiImageOrBusinessId.getValue();
        List<JSONObject> fileInfoList = aiImage.stream().map(fileUrl -> {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("fileId", MD5Util.md5(fileUrl));
            jsonObject.put("filePath", fileUrl);
            return jsonObject;
        }).collect(Collectors.toList());
        String businessId = aiImageOrBusinessId.getKey();
        String outBizNo = MessageFormat.format(BUSINESS_NO, enterpriseId, businessType.getCode(), businessId);
        headMap.put("Authorization", "Bearer " + token);
        Map<String, Object> bodyMap = new HashMap<>();
        bodyMap.put("outBizNo", outBizNo);
        bodyMap.put("monitorItem", request.getModelCode(businessType));
        JSONObject outBizParam = new JSONObject();
        outBizParam.put("customerId", enterpriseId);
        outBizParam.put("customerName", enterpriseName);
        if(bodyMap.get("monitorItem").equals("WORK_WEAR_DETECT")){
            outBizParam.put("workWearImageUrl", request.getStandardPic(businessType));
        }
        bodyMap.put("outBizParam", JSONObject.toJSONString(outBizParam));
        bodyMap.put("fileInfo", fileInfoList);
        bodyMap.put("fileType", "IMAGE");
        sendPost(interfaceUrl, headMap, bodyMap, new TypeReference<String>(){});
        return null;
    }

    private <T> T sendPost(String interfaceUrl, Map<String, String> headMap, Map<String, Object> bodyMap, TypeReference<T> typeReference){
        try {
            headMap.put("Content-Type", "application/json");
            String response = HttpRequest.sendPost(interfaceUrl, headMap, bodyMap);
            ShuZhiMaLiResponse shuZhiMaLiResponse = JSONObject.parseObject(response, ShuZhiMaLiResponse.class);
            if(Objects.isNull(shuZhiMaLiResponse)){
                log.warn("Received null response from ShuZhiMaLi API: {}", interfaceUrl);
                return null;
            }
            if(shuZhiMaLiResponse.getSuccess()){
                return JSON.parseObject(shuZhiMaLiResponse.getData(), typeReference);
            } else {
                throw new ServiceException("提交数字码力AI处理结果失败");
            }
        } catch (Exception e) {
            log.error("Error occurred while calling ShuZhiMaLi API: {}", interfaceUrl, e);
            throw new ServiceException("提交数字码力AI处理结果失败");

        }
    }
}
