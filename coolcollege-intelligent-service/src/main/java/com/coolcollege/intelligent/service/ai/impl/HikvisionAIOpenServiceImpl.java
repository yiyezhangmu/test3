package com.coolcollege.intelligent.service.ai.impl;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.enums.patrol.CheckResultEnum;
import com.coolcollege.intelligent.common.http.CoolHttpClient;
import com.coolcollege.intelligent.model.ai.AICommonPromptDTO;
import com.coolcollege.intelligent.model.ai.entity.AiModelLibraryDO;
import com.coolcollege.intelligent.service.ai.AIOpenService;
import com.coolcollege.intelligent.util.RedisUtilPool;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * <p>
 * 海康AI算法服务实现了
 * </p>
 *
 * @author wangff
 * @since 2025/8/4
 */
@Service("hikvisionAIOpenServiceImpl")
@Slf4j
@RequiredArgsConstructor
public class HikvisionAIOpenServiceImpl implements AIOpenService {
    private final RedisUtilPool redisUtilPool;

    @Override
    public String aiResolve(String enterpriseId, AICommonPromptDTO aiCommonPromptDTO, List<String> imageList, AiModelLibraryDO aiModel) {
        Map<String, Object> params = new HashMap<>();
        String apiUrl = aiModel.getExtendInfo("apiUrl", String.class);
        for (String image : imageList) {
            String resultStr = null;
            try {
                String token = token();
                params.put("imageUrl", image);
                resultStr = thisPost(apiUrl, params, token);
                if (StringUtils.isNotBlank(resultStr)) {
                    JSONObject result = JSONObject.parseObject(resultStr);
                    String code = result.getString("code");
                    if ("200".equals(code)) {
                        return aiResult(result, aiModel);
                    }
                }
            } catch (Exception e) {
                log.info("海康AI算法调用失败, response:{}, error:{}", resultStr, e.getMessage());
            }
        }
        return "";
    }

    private String aiResult(JSONObject result, AiModelLibraryDO aiModel) {
        JSONObject algFormData = result.getJSONObject("data").getJSONObject("algFormData");
        Integer errorCode = algFormData.getInteger("errorCode");
        if (errorCode != 0) {
            return CheckResultEnum.INAPPLICABLE.getCode();
        }
        JSONArray resultList = algFormData.getJSONArray("results");
        JSONArray mappingList = algFormData.getJSONArray("mapping");
        Map<String, Integer> passResultValue = new HashMap<>();

        Integer modelLabel = aiModel.getExtendInfo("label", Integer.class);
        Integer modelPassResultValue = aiModel.getExtendInfo("passResultValue", Integer.class);
        Integer label = Objects.nonNull(modelLabel) ? modelLabel : 1;
        if (Objects.nonNull(modelPassResultValue)) {
            passResultValue.put(aiModel.getCode(), modelPassResultValue);
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
                                Integer code = passResultValue.getOrDefault(aiModel.getCode(), 0);
                                if (confidence > 700 && attrConf > 700 && code.equals(attrValue)) {
                                    resultPassList.add(CheckResultEnum.PASS.getCode());
                                    return;
                                }
                                resultPassList.add(CheckResultEnum.FAIL.getCode());
                            } else if ("mouse".equals(aiModel.getCode())) {
                                resultPassList.add(CheckResultEnum.PASS.getCode());
                            }
                        });
                    }
                    if ("mobile".equals(aiModel.getCode()) || "sleep".equals(aiModel.getCode())) {
                        //抽烟
                        String modelId = modelLabelMap.get(finalLabel);
                        Integer confidence = obj.getInteger("confidence");
                        if (confidence != null && confidence > 700) {
                            resultPassList.add(CheckResultEnum.FAIL.getCode());
                            return;
                        }
                        resultPassList.add(CheckResultEnum.PASS.getCode());

                    } else if ("smoking".equals(aiModel.getCode())) {
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
                if(CollectionUtils.isEmpty(targets) && ("sleep".equals(aiModel.getCode()) || "mobile".equals(aiModel.getCode()))){
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

    private String thisPost(String uri, Map<String, Object> params, String token) {
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

    private String token() {
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
}
