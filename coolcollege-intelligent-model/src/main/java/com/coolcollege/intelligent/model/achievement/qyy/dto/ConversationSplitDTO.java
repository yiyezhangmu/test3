package com.coolcollege.intelligent.model.achievement.qyy.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zhangchenbiao
 * @FileName: ConversationSplitDTO
 * @Description: 群拆分
 * @date 2023-04-11 17:42
 */
@Data
public class ConversationSplitDTO {

    @ApiModelProperty("门店群")
    private String storeConversation;

    @ApiModelProperty("分公司群")
    private ConversationInfo corpConversation;

    @ApiModelProperty("其他群")
    private ConversationInfo otherConversation;

    @Data
    public static class ConversationInfo{

        /**
         * 一方群主键id
         */
        private String id;
        /**
         * 群名
         */
        private String name;
        /**
         * 一方开放群聊id
         */
        private String openConversationId;
    }

}
