package com.coolcollege.intelligent.model.qyy;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @author   zhangchenbiao
 * @date   2023-04-14 04:10
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QyyConversationSceneAuthDO implements Serializable {
    @ApiModelProperty("自增id")
    private Long id;

    @ApiModelProperty("群场景id")
    private String sceneCode;

    @ApiModelProperty("权限code")
    private String authCode;

    @ApiModelProperty("角色id")
    private Long roleId;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新时间")
    private Date updateTime;
}