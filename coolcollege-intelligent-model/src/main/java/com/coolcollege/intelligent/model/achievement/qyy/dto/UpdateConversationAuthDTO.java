package com.coolcollege.intelligent.model.achievement.qyy.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author zhangchenbiao
 * @FileName: UpdateConversationAuthDTO
 * @Description:
 * @date 2023-04-07 10:24
 */
@Data
public class UpdateConversationAuthDTO {

    @ApiModelProperty("场景id")
    private String sceneCode;

    @ApiModelProperty("权限列表")
    private List<SceneAuth> authList;

    @Data
    public static class SceneAuth{

        @ApiModelProperty("卡片场景code")
        private String authCode;

        @ApiModelProperty("角色id")
        private List<Long> roleIds;
    }

}
