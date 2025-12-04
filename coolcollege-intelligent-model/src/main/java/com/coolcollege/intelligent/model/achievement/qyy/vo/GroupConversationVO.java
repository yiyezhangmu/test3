package com.coolcollege.intelligent.model.achievement.qyy.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author wxp
 * @FileName: GroupConversationVO
 * @Description:群列表
 * @date 2023-04-13 14:33
 */
@Data
public class GroupConversationVO {

    /**
     * 群id
     */
    @ApiModelProperty("群id")
    private Long id;

    /**
     * 群类型
     */
    @ApiModelProperty("群类型")
    private String conversationType;

    /**
     * 群名称
     */
    @ApiModelProperty("群名称")
    private String conversationTitle;

    /**
     * 开放平台群Id
     */
    private String openConversationId;



}
