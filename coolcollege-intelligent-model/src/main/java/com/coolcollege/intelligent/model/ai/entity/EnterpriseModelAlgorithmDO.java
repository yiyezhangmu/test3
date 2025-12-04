package com.coolcollege.intelligent.model.ai.entity;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 企业AI算法模型库
 * @author   zhangchenbiao
 * @date   2025-09-25 03:50
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnterpriseModelAlgorithmDO implements Serializable {
    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("企业Id")
    private String enterpriseId;

    @ApiModelProperty("场景id")
    private Long sceneId;

    @ApiModelProperty("场景名称")
    private String sceneName;

    @ApiModelProperty("模型名称")
    private String modelName;

    @ApiModelProperty("模型编码")
    private String modelCode;

    @ApiModelProperty("系统prompt")
    private String systemPrompt;

    @ApiModelProperty("用户prompt")
    private String userPrompt;

    @ApiModelProperty("特殊prompt 如果有值需要跟用户prompt拼接起来统一传值")
    private String specialPrompt;

    @ApiModelProperty("标准图")
    private String standardPic;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    @ApiModelProperty("启用状态 0: 未启用 1:已启用")
    private Integer algorithmStatus;

    /**
     * 整改人信息
     */
    @ApiModelProperty("整改人信息")
    private String nodeInfo;

    /**
     * 有效期设置策略 0-跟随全局配置 1-需要设置有效期
     */
    @ApiModelProperty("有效期设置策略 0-跟随全局配置 1-需要设置有效期")
    private Integer expiryPolicy;

    /**
     * 问题工单有效期（单位小时）
     */
    @ApiModelProperty("问题工单有效期（单位小时）")
    private Integer expiryTimes;

}