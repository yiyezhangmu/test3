package com.coolcollege.intelligent.service.achievement.qyy.open;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.util.Base64Utils;
import com.coolcollege.intelligent.model.achievement.qyy.vo.RecommendStyleGoodsVO;
import com.coolcollege.intelligent.util.RedisUtilPool;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author zhangchenbiao
 * @FileName: AoKangService
 * @Description:
 * @date 2023-04-21 9:47
 */
@Service
@Slf4j
public class AoKangOpenApiService {

    @Resource
    private RestTemplate restTemplate;
    @Resource
    private RedisUtilPool redisUtilPool;
    /**
     * token过期的code
     */
    private static final List<String> tokenExpireCodes = new ArrayList<>(Arrays.asList("900901", "900902"));

    public List<RecommendStyleGoodsVO> searchGoods(String enterpriseId, String goodsIds) {
        String url = "https://am.aokang.com:8246/goodsCenter/v1.0.0/web/coolstore/getGoodsInfo";
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        headers.add("Authorization","Bearer " + getAKToken());
        HashMap<String, String> reqMap = new HashMap<>();
        reqMap.put("goodsIds", goodsIds);
        HttpEntity<Object> req = new HttpEntity<>(reqMap, headers);
        log.info("url:{}, req:{}", url, JSONObject.toJSONString(req));
        ResponseEntity<JSONObject> response = restTemplate.exchange(url, HttpMethod.POST, req, JSONObject.class);
        log.info("response:{}", JSONObject.toJSONString(response));
        if(Objects.nonNull(response) && Objects.nonNull(response.getBody()) && tokenExpireCodes.contains(response.getBody().getString("code"))){
            headers.add("Authorization","Bearer " + getAKToken());
            response = restTemplate.exchange(url, HttpMethod.POST, req, JSONObject.class);
            log.info("retry response:{}", JSONObject.toJSONString(response));
        }
        List<RecommendStyleGoodsVO> resultList = new ArrayList<>();
        if(Objects.nonNull(response) && Objects.nonNull(response.getBody()) && Objects.nonNull(response.getBody())){
            JSONArray resultArray = response.getBody().getJSONArray("result");
            if(Objects.nonNull(resultArray) && !resultArray.isEmpty()){
                for (Object o : resultArray) {
                    RecommendStyleGoodsVO goods = JSONObject.parseObject(JSONObject.toJSONString(o), RecommendStyleGoodsVO.class);
                    resultList.add(goods);
                }
            }
        }else{
            throw new ServiceException(ErrorCodeEnum.THIRD_PARTY_INTERFACE_EXCEPTION);
        }
        return resultList;
    }

    public String getAKToken(){
        String accessTokenKey = "akAccessTokenKey";
        String accessToken = redisUtilPool.getString(accessTokenKey);
        if(StringUtils.isNotBlank(accessToken)){
            return accessToken;
        }
        String auth = Base64Utils.strConvertBase("si74sqRlevTCjq2eEB6GOj3XPCUa:Hpe98djdvqWshn5yoL78gXppHoMa");
        String url = "https://am.aokang.com:8246/token?grant_type=client_credentials";
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE);
        headers.add("Authorization","Basic "+auth);
        HttpEntity<Object> req = new HttpEntity<>(null, headers);
        log.info("url:{}, req:{}", url, JSONObject.toJSONString(req));
        ResponseEntity<JSONObject> response = restTemplate.exchange(url, HttpMethod.POST, req, JSONObject.class);
        log.info("response:{}", JSONObject.toJSONString(response));
        if(Objects.isNull(response)){
            response = restTemplate.exchange(url, HttpMethod.POST, req, JSONObject.class);
            log.info("retry response:{}", JSONObject.toJSONString(response));
        }
        if (Objects.nonNull(response) && response.getStatusCodeValue()== HttpStatus.OK.value()){
            JSONObject body = response.getBody();
            if(Objects.nonNull(body)){
                accessToken = body.getString("access_token");
                Integer expiresIn = body.getInteger("expires_in");
                redisUtilPool.setString(accessTokenKey, accessToken, expiresIn);
            }
        }
        return accessToken;
    }
}
