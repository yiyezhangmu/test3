package com.coolcollege.intelligent.model.ai;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * OpenAI接口调用DTO
 * </p>
 *
 * @author wangff
 * @since 2025/7/18
 */
@Data
public class OpenAIRestAPIDTO {
    /**
     * 模型
     */
    private String model;

    /**
     * 采样温度
     */
    private float temperature;

    /**
     * 内容
     */
    private List<Message> messages;

    private OpenAIRestAPIDTO(String model) {
        this.model = model;
        this.messages = new ArrayList<>();
    }

    public static OpenAIRestAPIDTO create(String model, String systemPrompt) {
        OpenAIRestAPIDTO openAIRestAPIDTO = new OpenAIRestAPIDTO(model);
        if (systemPrompt != null && !systemPrompt.isEmpty()) {
            openAIRestAPIDTO.addSystemMessage(systemPrompt);
        }
        return openAIRestAPIDTO;
    }

    public static OpenAIRestAPIDTO create(String model, AICommonPromptDTO aiCommonPrompt) {
        OpenAIRestAPIDTO openAIRestAPIDTO = new OpenAIRestAPIDTO(model);
        if (aiCommonPrompt != null) {
            if(CollectionUtils.isNotEmpty(aiCommonPrompt.getSystemPromptList())){
                aiCommonPrompt.getSystemPromptList().forEach(openAIRestAPIDTO::addSystemMessage);
            }
        }
        return openAIRestAPIDTO;
    }

    /**
     * 添加系统消息，只需要在第一次调用时设置
     */
    private void addSystemMessage(String systemPrompt) {
        Message message = new Message();
        message.setRole("system");
        Content content = Content.createByText(systemPrompt);
        message.setContent(Collections.singletonList(content));
        this.messages.add(message);
    }

    /**
     * 添加用户消息
     */
    public void addUserMessage(String userPrompt, List<String> imageList) {
        Message message = new Message();
        message.setRole("user");
        List<Content> contents = new ArrayList<>();
        contents.add(Content.createByText(userPrompt));
        if (imageList != null && !imageList.isEmpty()) {
            List<Content> imageContents = imageList.stream().map(Content::createByImage).collect(Collectors.toList());
            contents.addAll(imageContents);
        }
        message.setContent(contents);
        this.messages.add(message);
    }

    /**
     * 添加AI回复消息
     */
    public void addAssistantMessage(String aiResponse) {
        Message message = new Message();
        message.setRole("assistant");
        Content content = Content.createByText(aiResponse);
        message.setContent(Collections.singletonList(content));
        this.messages.add(message);
    }


    @Data
    public static class Message {
        /**
         * 内容
         */
        private List<Content> content;

        /**
         * 角色，system/user/assistant
         */
        private String role;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Content {
        /**
         * 文本
         */
        private String text;

        /**
         * 类型，text/image_url
         */
        private String type;

        @JsonProperty("image_url")
        @JSONField(name = "image_url")
        private ImageUrl imageUrl;

        public static Content createByText(String text) {
            return new Content(text, "text", null);
        }

        public static Content createByImage(String url) {
            return new Content(null, "image_url", new ImageUrl(url));
        }
    }

    @Data
    @AllArgsConstructor
    public static class ImageUrl {
        private String url;
    }
}
