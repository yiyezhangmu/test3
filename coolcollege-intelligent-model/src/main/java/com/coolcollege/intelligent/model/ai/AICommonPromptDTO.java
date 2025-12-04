package com.coolcollege.intelligent.model.ai;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * <p>
 * AI调用通用提示词DTO
 * </p>
 *
 * @author wangff
 * @since 2025/7/18
 */
@Data
@Builder
public class AICommonPromptDTO {
    /**
     * 系统prompt
     */
    private String systemPrompt;

    /**
     * 系统prompt列表
     */
    private List<String> systemPromptList;

    /**
     * 图片传输prompt
     */
    private String transferImagePrompt;

    /**
     * 结束prompt
     */
    private String finishPrompt;


    public AICommonPromptDTO(String systemPrompt, String transferImagePrompt, String finishPrompt) {
        this.systemPrompt = systemPrompt;
        this.transferImagePrompt = transferImagePrompt;
        this.finishPrompt = finishPrompt;
    }

    public AICommonPromptDTO(String systemPrompt, List<String> systemPromptList, String transferImagePrompt, String finishPrompt) {
        this.systemPrompt = systemPrompt;
        this.systemPromptList = systemPromptList;
        this.transferImagePrompt = transferImagePrompt;
        this.finishPrompt = finishPrompt;
    }

    public AICommonPromptDTO() {

    }
}
