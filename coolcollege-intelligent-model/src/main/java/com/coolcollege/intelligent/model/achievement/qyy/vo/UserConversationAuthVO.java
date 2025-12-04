package com.coolcollege.intelligent.model.achievement.qyy.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zhangchenbiao
 * @FileName: UpdateConversationAuthDTO
 * @Description:
 * @date 2023-04-07 10:24
 */
@Data
public class UserConversationAuthVO {

    @ApiModelProperty("群场景id")
    private String sceneCode;

    @ApiModelProperty("权限code")
    private String authCode;

    @ApiModelProperty("优先级  值大 优先级高")
    private Integer priority;

    public UserConversationAuthVO(String sceneCode, String authCode, Integer priority) {
        this.sceneCode = sceneCode;
        this.authCode = authCode;
        this.priority = priority;
    }
}
