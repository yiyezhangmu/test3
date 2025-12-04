package com.coolcollege.intelligent.model.brand;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 品牌
 *
 * @author wangff
 * @date 2025-03-06 10:28
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnterpriseBrandDO implements Serializable {
    @ApiModelProperty("主键")
    private Long id;

    @ApiModelProperty("品牌code")
    private String code;

    @ApiModelProperty("品牌名称")
    private String name;

    @ApiModelProperty("扩展字段")
    private String extendInfo;

    @ApiModelProperty("创建人id")
    private String createUserId;

    @ApiModelProperty("创建人名称")
    private String createUserName;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新人id")
    private String updateUserId;

    @ApiModelProperty("更新人名称")
    private String updateUserName;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    @ApiModelProperty("特殊标识，2表示系统默认品牌无法更改")
    private Integer status;

    @ApiModelProperty("初始化状态")
    private Integer initStatus;
}