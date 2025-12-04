package com.coolcollege.intelligent.model.ai.entity;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * AI模型场景
 * @author   zhangchenbiao
 * @date   2025-09-25 03:51
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiModelSceneDO implements Serializable {
    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("场景名称")
    private String sceneName;

    @ApiModelProperty("场景图片")
    private String scenePic;

    @ApiModelProperty("模型编码")
    private String modelName;

    @ApiModelProperty("模型编码")
    private String modelCode;

    @ApiModelProperty("分组id")
    private Long groupId;

    @ApiModelProperty("分组名称")
    private String groupName;

    @ApiModelProperty("场景图片")
    private String standardPic;

    @ApiModelProperty("场景介绍")
    private String standardDesc;

    @ApiModelProperty("系统prompt")
    private String systemPrompt;

    @ApiModelProperty("用户prompt")
    private String userPrompt;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新时间")
    private Date updateTime;
}