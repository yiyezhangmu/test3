package com.coolcollege.intelligent.model.achievement.qyy.vo;

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
public class ConversationAuthVO {

    @ApiModelProperty("群场景id")
    private String sceneCode;

    @ApiModelProperty("卡片场景code")
    private String sceneName;

    @ApiModelProperty("权限列表")
    public List<ConversationAuth> authList;

    @Data
    public static class ConversationAuth{

        @ApiModelProperty("权限code")
        private String authCode;

        @ApiModelProperty("权限名称")
        private String authName;

        @ApiModelProperty("优先级  值大 优先级高")
        private Integer priority;

        @ApiModelProperty("角色列表")
        private List<AuthRole> roleList;

        @ApiModelProperty("描述字段")
        private String sceneCardDesc;

        public ConversationAuth(String authCode, String authName, Integer priority, List<AuthRole> roleList,String sceneCardDesc) {
            this.authCode = authCode;
            this.authName = authName;
            this.priority = priority;
            this.roleList = roleList;
            this.sceneCardDesc = sceneCardDesc;
        }
    }

    @Data
    public static class AuthRole{

        @ApiModelProperty("角色id")
        private Long roleId;

        @ApiModelProperty("角色名称")
        private String roleName;

        public AuthRole(Long roleId, String roleName) {
            this.roleId = roleId;
            this.roleName = roleName;
        }
    }

}
