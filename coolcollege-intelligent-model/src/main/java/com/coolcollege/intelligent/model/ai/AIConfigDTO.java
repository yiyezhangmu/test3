package com.coolcollege.intelligent.model.ai;

import com.coolcollege.intelligent.model.enums.AIBusinessModuleEnum;
import com.coolcollege.intelligent.model.enums.AICommentStyleEnum;
import lombok.Data;

import java.util.List;

/**
 * <p>
 * 企业AI配置DTO
 * </p>
 *
 * @author wangff
 * @since 2025/6/9
 */
@Data
public class AIConfigDTO {
    /**
     * AI巡检算法配置
     */
    private List<EnableAIModel> enableAiModel;

    /**
     * 是否开启AI巡检
     */
    private Boolean aiCheck;

    /**
     * AI巡检业务模块配置
     */
    private List<AIConfig> aiConfig;

    /**
     * AI巡检算法配置
     */
    @Data
    public static class EnableAIModel {
        /**
         * AI模型
         */
        private String aiModel;

        /**
         * 是否开启
         */
        private Boolean enable;
    }

    /**
     * AI巡检配置
     */
    @Data
    public static class AIConfig {
        /**
         * 业务模块，storeWork等
         */
        private String businessModule;

        /**
         * 风格，detail/normal/brief
         */
        private String style;

        /**
         * 是否开启
         */
        private Boolean enable;
    }

    /**
     * 业务模块是否开启AI
     * @param businessModule 业务模块
     * @return 是否开启
     */
    public boolean aiEnable(AIBusinessModuleEnum businessModule) {
        return Boolean.TRUE.equals(aiCheck) && aiConfig != null && aiConfig.stream().anyMatch(aiConfig -> businessModule.getModule().equals(aiConfig.getBusinessModule()) && Boolean.TRUE.equals(aiConfig.getEnable()));
    }

    /**
     * 获取AI巡检风格
     * @param businessModule 业务模块
     * @return 风格
     */
    public String aiStyle(AIBusinessModuleEnum businessModule) {
        if (aiConfig == null) return AICommentStyleEnum.DETAIL.getStyle();
        for (AIConfig config : aiConfig) {
            if (config.getBusinessModule().equals(businessModule.getModule())) {
                return config.getStyle();
            }
        }
        return AICommentStyleEnum.DETAIL.getStyle();
    }
}
