package com.coolcollege.intelligent.service.ai;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.enums.patrol.CheckResultEnum;
import com.coolcollege.intelligent.common.enums.patrol.PatrolAITypeEnum;
import com.coolcollege.intelligent.service.video.openapi.impl.YingShiOpenServiceImpl;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.coolstore.base.enums.AccountTypeEnum;
import com.coolcollege.intelligent.common.http.CoolHttpClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author chenyupeng
 * @since 2022/4/1
 */
@Slf4j
@Service
@Deprecated // 整合到HikvisionAIOpenServiceImpl
public class HikvisionAIService {

    @Resource
    private RedisUtilPool redisUtilPool;
    @Resource(name = "yingShiOpenServiceImpl")
    private YingShiOpenServiceImpl yingShiOpenService;


    @Deprecated
    public String aiDetection(String enterpriseId, String picUrl, String aiType) {
        try {
            PatrolAITypeEnum aiTypeEnum = PatrolAITypeEnum.getByCode(aiType);
            if (aiTypeEnum == null) {
                log.info("没有查到该算法aiType:{}", aiType);
                return CheckResultEnum.INAPPLICABLE.getCode();
            }

            String apiUrl = aiTypeEnum.getApiUrl();
            Map<String, Object> params = new HashMap<>();
            String resultStr;
            if (PatrolAITypeEnum.isYsAi(aiTypeEnum)) {
                String toekn = yingShiOpenService.getAccessToken(enterpriseId, AccountTypeEnum.PLATFORM);
                params.put("image", picUrl);
                params.put("dataType", 0);
                resultStr = ysPost(apiUrl, params, toekn);
            } else {
                String token = this.token();
                params.put("imageUrl", picUrl);
                resultStr = thisPost(apiUrl, params, token);
            }
            if (StringUtils.isNotBlank(resultStr)) {
                JSONObject result = JSONObject.parseObject(resultStr);
                String code = result.getString("code");
                if ("200".equals(code)) {
                    if (PatrolAITypeEnum.isYsAi(aiTypeEnum)) {
                        return ysAiResult(result, aiTypeEnum);
                    } else {
                        return aiResult(result, aiTypeEnum);
                    }
                }
            }
        } catch (Exception e) {
            log.info("aiDetection", e);
        }
        return CheckResultEnum.INAPPLICABLE.getCode();
    }

    public String token() {
        String tokenKey = "hikvision_ai_token";
        String token = redisUtilPool.getString(tokenKey);
        if (StringUtils.isNotBlank(token)) {
            return token;
        }
        String url = "https://ai.hikvision.com/api/user/user-service/v1/user/token";
        Map<String, Object> params = new HashMap<>();
        params.put("ak", "2a17698067ce45b0ad92caeb4311ba62");
        params.put("sk", "b861c5b3c9e44c85bb2f99ebc642c613");
        String s = CoolHttpClient.sendPostJsonRequest(url, JSONUtil.toJsonStr(params));
        JSONObject result = JSONObject.parseObject(s);
        token = result.getJSONObject("data").getString("token");
        //30 分钟有效期，提前2分钟过期
        redisUtilPool.setString(tokenKey, token, 28 * 60);
        return token;
    }


    public static String thisPost(String uri, Map<String, Object> params, String token) {
        // 确定计算方法
        try {
            // 加密后的字符串,生成401认证token
            HttpRequest httpRequest = HttpUtil.createPost(uri);
            httpRequest.header("token", token);
            httpRequest.header("referer", "https://ai.hikvision.com/");

            httpRequest.header("Content-Type",
                    "application/x-www-form-urlencoded; charset=utf-8");
            httpRequest.form(params);

            HttpResponse httpResponse = httpRequest.execute();
            log.info("sendPostJsonRequest-url:{}", uri);
            log.info("sendPostJsonRequest-body:{}", httpResponse.body());
            return httpResponse.body();
        } catch (Exception e) {
            log.error("thisPost", e);
        }
        return null;
    }

    public static String ysPost(String uri, Map<String, Object> params, String token) {
        try {
            HttpRequest httpRequest = HttpUtil.createPost(uri);
            httpRequest.header("accessToken", token);

            httpRequest.header("Content-Type",
                    "application/x-www-form-urlencoded; charset=utf-8");
            httpRequest.form(params);

            HttpResponse httpResponse = httpRequest.execute();
            log.info("sendPostJsonRequest-url:{}", uri);
            log.info("sendPostJsonRequest-body:{}", httpResponse.body());
            return httpResponse.body();
        } catch (Exception e) {
            log.error("ysPost", e);
        }
        return null;
    }

    private String aiResult(JSONObject result, PatrolAITypeEnum aiTypeEnum) {
        JSONObject algFormData = result.getJSONObject("data").getJSONObject("algFormData");
        Integer errorCode = algFormData.getInteger("errorCode");
        if (errorCode != 0) {
            return CheckResultEnum.INAPPLICABLE.getCode();
        }
        JSONArray resultList = algFormData.getJSONArray("results");
        JSONArray mappingList = algFormData.getJSONArray("mapping");
        Map<String, Integer> passResultValue = new HashMap<>();
        Integer label = 1;
        switch (aiTypeEnum) {
            case MOUSE:
                passResultValue.put(aiTypeEnum.getCode(), 0);
                label = 4;
                break;
            case TRASH:
                passResultValue.put(aiTypeEnum.getCode(), 0);
                break;
            case SMOKING:
                label = 2;
                break;
            case MASK:
                label = 2;
                passResultValue.put(aiTypeEnum.getCode(), 0);
                break;
            case HAT:
                passResultValue.put(aiTypeEnum.getCode(), 0);
                break;
            case UNIFORM:
                label = 3;
                passResultValue.put(aiTypeEnum.getCode(), 0);
                break;
            case SLEEP:
                break;
            case MOBILE:
                break;
            default:
        }

        Map<Integer, String> modelLabelMap = new HashMap<>();
        mappingList.forEach(mapping -> {
            JSONObject mappingObj = (JSONObject) mapping;
            String resultType = mappingObj.getString("type");
            if ("2".equals(resultType)) {
                JSONObject relationObj = mappingObj.getJSONObject("relation");
                modelLabelMap.put(relationObj.getInteger("label"), mappingObj.getString("modelId"));
            }
        });

        List<String> resultPassList = new ArrayList<>();
        Integer finalLabel = label;
        resultList.forEach(e -> {
            JSONObject jsonObject = (JSONObject) e;
            Integer errCode = jsonObject.getInteger("errorcode");
            if (errCode == 0) {
                JSONArray targets = jsonObject.getJSONArray("targets");
                targets.forEach(target -> {
                    JSONObject targetObj = (JSONObject) target;
                    JSONObject obj = targetObj.getJSONObject("obj");
                    JSONArray propertiesArray = targetObj.getJSONArray("properties");
                    if (propertiesArray != null && propertiesArray.size() > 0) {
                        propertiesArray.forEach(property -> {
                            JSONObject propertyObj = (JSONObject) property;

                            if (modelLabelMap.containsKey(finalLabel)) {
                                String modelId = propertyObj.getString("modelID");
                                Integer confidence = obj.getInteger("confidence");
                                Integer attrConf = propertyObj.getJSONObject("classify").getInteger("attrConf");

                                Integer attrValue = propertyObj.getJSONObject("classify").getInteger("attrValue");
                                Integer code = passResultValue.getOrDefault(aiTypeEnum.getCode(), 0);
                                if (confidence > 700 && attrConf > 700 && code.equals(attrValue)) {
                                    resultPassList.add(CheckResultEnum.PASS.getCode());
                                    return;
                                }
                                resultPassList.add(CheckResultEnum.FAIL.getCode());
                            } else if (PatrolAITypeEnum.MOUSE.getCode().equals(aiTypeEnum.getCode())) {
                                resultPassList.add(CheckResultEnum.PASS.getCode());
                            }
                        });
                    }
                    if (PatrolAITypeEnum.MOBILE.getCode().equals(aiTypeEnum.getCode()) || PatrolAITypeEnum.SLEEP.getCode().equals(aiTypeEnum.getCode())) {
                        //抽烟
                        String modelId = modelLabelMap.get(finalLabel);
                        Integer confidence = obj.getInteger("confidence");
                        if (confidence != null && confidence > 700) {
                            resultPassList.add(CheckResultEnum.FAIL.getCode());
                            return;
                        }
                        resultPassList.add(CheckResultEnum.PASS.getCode());

                    } else if (PatrolAITypeEnum.SMOKING.getCode().equals(aiTypeEnum.getCode())) {
                        //抽烟
                        String modelId = modelLabelMap.get(finalLabel);
                        Integer confidence = obj.getInteger("confidence");
                        Integer labelType = obj.getInteger("type");

                        if (confidence != null && finalLabel.equals(labelType) && confidence > 700) {
                            resultPassList.add(CheckResultEnum.FAIL.getCode());
                            return;
                        }
                        resultPassList.add(CheckResultEnum.PASS.getCode());
                    }
                });
                if(CollectionUtils.isEmpty(targets) && (PatrolAITypeEnum.SLEEP.getCode().equals(aiTypeEnum.getCode()) || PatrolAITypeEnum.MOBILE.getCode().equals(aiTypeEnum.getCode()))){
                    resultPassList.add(CheckResultEnum.PASS.getCode());
                }
            }
        });

        if (resultPassList.contains(CheckResultEnum.FAIL.getCode())) {
            return CheckResultEnum.FAIL.getCode();
        } else if (resultPassList.contains(CheckResultEnum.PASS.getCode())) {
            return CheckResultEnum.PASS.getCode();
        }
        return CheckResultEnum.INAPPLICABLE.getCode();
    }

    private String ysAiResult(JSONObject result, PatrolAITypeEnum aiTypeEnum) {
        JSONArray data = result.getJSONArray("data");
        List<Object> errorItems = data.stream().filter(v -> !"0".equals(((JSONObject) v).getString("errorcode"))).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(errorItems)) {
            log.info("ysAiResult#推理结果不为0");
        }
        Set<Integer> resultTypes = data.stream()
                .filter(v -> "0".equals(((JSONObject) v).getString("errorcode")))
                .flatMap(v -> ((JSONObject) v).getJSONArray("targets").stream())
                .map(v -> {
                    JSONObject obj = ((JSONObject) v).getJSONObject("obj");
                    return Objects.nonNull(obj) ? obj.getInteger("type") : null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        int passType = -1, unPassType = -1;
        switch (aiTypeEnum) {
            case YS_MASK:
                passType = 1;
                unPassType = 2;
                break;
            case YS_HAT:
                passType = 3;
                unPassType = 4;
                break;
            case YS_UNIFORM:
                passType = 5;
                unPassType = 6;
            default:
                break;
        }
        if (resultTypes.contains(unPassType)) {
            return CheckResultEnum.FAIL.getCode();
        } else if (resultTypes.contains(passType)) {
            return CheckResultEnum.PASS.getCode();
        }
        return CheckResultEnum.INAPPLICABLE.getCode();
    }
}
