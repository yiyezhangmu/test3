package com.coolcollege.intelligent.model.qyy;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 
 * @author   zhangchenbiao
 * @date   2023-04-12 07:14
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QyyConfidenceFeedbackDO implements Serializable {
    @ApiModelProperty("自增id")
    private Long id;

    @ApiModelProperty("用户名")
    private String username;

    @ApiModelProperty("用户id")
    private String userId;

    @ApiModelProperty("区域id")
    private Long regionId;

    @ApiModelProperty("群id")
    private String conversationId;

    @ApiModelProperty("分数")
    private BigDecimal score;

    @ApiModelProperty("保障举措")
    private String measure;

    @ApiModelProperty("资源支持")
    private String resourceSupport;

    @ApiModelProperty("删除")
    private Boolean deleted;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新时间")
    private Date updateTime;
}