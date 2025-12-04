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
public class QyyConversationSceneDO implements Serializable {
    @ApiModelProperty("自增id")
    private Long id;

    @ApiModelProperty("群场景code")
    private String sceneCode;

    @ApiModelProperty("群场景名称")
    private String sceneName;

    @ApiModelProperty("权限code")
    private String authCode;

    @ApiModelProperty("名称")
    private String authName;

    @ApiModelProperty("优先级")
    private Integer priority;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    @ApiModelProperty("场景卡片描述")
    private String sceneCardDesc;
}