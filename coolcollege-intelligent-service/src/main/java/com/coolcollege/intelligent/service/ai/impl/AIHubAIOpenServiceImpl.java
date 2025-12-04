package com.coolcollege.intelligent.service.ai.impl;

import cn.hutool.core.collection.CollStreamUtil;
import com.adpexai.aihub.*;
import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.model.ai.AICommonPromptDTO;
import com.coolcollege.intelligent.model.ai.entity.AiModelLibraryDO;
import com.coolcollege.intelligent.service.ai.AIOpenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/**
 * <p>
 * AIHUB平台
 * </p>
 *
 * @author wangff
 * @since 2025/7/8
 */
@Service("aIHubAIOpenServiceImpl")
@Slf4j
@RequiredArgsConstructor
public class AIHubAIOpenServiceImpl implements AIOpenService {

    @Value("${ai.aihub.apiKey}")
    private String apiKey;

    @Override
    public String aiResolve(String enterpriseId, AICommonPromptDTO aiCommonPromptDTO, List<String> imageList, AiModelLibraryDO aiModel) {
        throw new UnsupportedOperationException();
    }

    /**
     * AI分析，直接返回结果
     * @param enterpriseId 企业id
     * @param prompt 提示词
     * @param imageList 图片url列表
     * @return AI处理DTO
     */
    private String aiResolveNotProcessed(String enterpriseId, String prompt, List<String> imageList) {
        try {
            AihubClient client = AihubClientFactory.newFactory()
                    .host("aihub.adpexai.com")
                    .port(80)
                    .enableChannelPooling(true)
                    .apiKey(apiKey)
                    .build();
            AihubRequestBuilder aihubRequestBuilder = buildAIContent(prompt, imageList);
            String response = client.generateText(aihubRequestBuilder);
            log.info("enterpriseId: {}, prompt:{}, imageList: {}", enterpriseId, prompt, imageList);
            if (StringUtils.isNotBlank(response)) {
                JSONObject result = JSONObject.parseObject(response);
                if (Objects.nonNull(result)) {
                    return result.getJSONArray("choices").getJSONObject(0).getJSONObject("message").getString("content").replace("```", "");
                }
            }
        } catch (Exception e) {
            log.info("AIHub平台调用失败，prompt：{}，imageList：{}, error: {}", prompt, imageList, e.getMessage());
        }
        throw new ServiceException(ErrorCodeEnum.AI_API_ERROR);
    }

    /**
     * 构建AI请求内容
     */
    private AihubRequestBuilder buildAIContent(String prompt, List<String> imageList) {
        // 图片处理成Content
        List<ComplexContentDTO> imageContentList = CollStreamUtil.toList(imageList, picture -> ComplexContentDTO.builder()
                .imageUrl(ImageUrlDTO.builder().url(picture).build())
                .type("image_url")
                .build());
        List<ComplexContentDTO> content = new ArrayList<>(imageContentList);
        // 添加prompt
        content.add(ComplexContentDTO.builder()
                .text(prompt)
                .type("text")
                .build());
        return AihubRequestBuilder.builder()
                .addUserMessage(content)
                .model("gpt-4o")
                .maxTokens(16380)
                .stream(false)
                .temperature(0.7f);
    }
}
