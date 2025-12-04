package com.coolcollege.intelligent.service.ai.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.enums.patrol.CheckResultEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.model.ai.AICommonPromptDTO;
import com.coolcollege.intelligent.model.ai.entity.AiModelLibraryDO;
import com.coolcollege.intelligent.service.ai.AIOpenService;
import com.coolcollege.intelligent.service.video.openapi.impl.YingShiOpenServiceImpl;
import com.coolcollege.intelligent.util.AIHelper;
import com.coolstore.base.enums.AccountTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 萤石 AI服务实现类
 * </p>
 *
 * @author wangff
 * @since 2025/8/4
 */
@Service("yingshiAIOpenServiceImpl")
@Slf4j
public class YingshiAIOpenServiceImpl implements AIOpenService {
    @Resource(name = "yingShiOpenServiceImpl")
    private YingShiOpenServiceImpl yingShiOpenService;

    private static final String API_URL = "https://open.ys7.com/api/lapp/intelligence/reasoning/5A9D1AB536854B8AAF7224C2508571A1";

    @Override
    public String aiResolve(String enterpriseId, AICommonPromptDTO aiCommonPromptDTO, List<String> imageList, AiModelLibraryDO aiModel) {
        List<String> resultMappingList = new ArrayList<>();
        // 一次传一张
        for (String image : imageList) {
            String resultStr = null;
            try {
                String token = yingShiOpenService.getAccessToken(enterpriseId, AccountTypeEnum.PLATFORM);
                Map<String, Object> params = new HashMap<>();
                params.put("image", image);
                params.put("dataType", 0);
                resultStr = ysPost(params, token);
                if (StringUtils.isNotBlank(resultStr)) {
                    JSONObject result = JSONObject.parseObject(resultStr);
                    String code = result.getString("code");
                    if ("200".equals(code)) {
                        resultMappingList.add(ysAiResult(result, aiModel.getCode()));
                    }
                }
            } catch (Exception e) {
                log.info("萤石AI算法调用失败, response:{}, error:{}", resultStr, e.getMessage());
            }
        }
        if (CollectionUtils.isNotEmpty(resultMappingList)) {
            return AIHelper.matchCheckResult(resultMappingList);
        }
        throw new ServiceException(ErrorCodeEnum.AI_API_ERROR);
    }

    private String ysAiResult(JSONObject result, String model) {
        JSONArray data = result.getJSONArray("data");
        List<Object> errorItems = data.stream().filter(v -> !"0".equals(((JSONObject) v).getString("errorcode"))).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(errorItems)) {
            log.info("ysAiResult#推理结果不为0:{}", JSONObject.toJSONString(errorItems));
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
        switch (model) {
            case "ys_mask":
                passType = 1;
                unPassType = 2;
                break;
            case "ys_hat":
                passType = 3;
                unPassType = 4;
                break;
            case "ys_uniform":
                passType = 5;
                unPassType = 6;
                break;
        }
        if (resultTypes.contains(unPassType)) {
            return CheckResultEnum.FAIL.getCode();
        } else if (resultTypes.contains(passType)) {
            return CheckResultEnum.PASS.getCode();
        }
        return CheckResultEnum.INAPPLICABLE.getCode();
    }

    private static String ysPost(Map<String, Object> params, String token) {
        try {
            HttpRequest httpRequest = HttpUtil.createPost(API_URL);
            httpRequest.header("accessToken", token);

            httpRequest.header("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
            httpRequest.form(params);

            HttpResponse httpResponse = httpRequest.execute();
            log.info("sendPostJsonRequest-url:{}", API_URL);
            log.info("sendPostJsonRequest-body:{}", httpResponse.body());
            return httpResponse.body();
        } catch (Exception e) {
            log.error("ysPost", e);
        }
        return null;
    }
}
