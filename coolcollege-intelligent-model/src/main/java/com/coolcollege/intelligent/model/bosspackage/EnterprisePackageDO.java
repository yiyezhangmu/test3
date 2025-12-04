package com.coolcollege.intelligent.model.bosspackage;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @author   xugangkun
 * @date   2022-03-22 04:10
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnterprisePackageDO implements Serializable {
    @ApiModelProperty("主键")
    private Long id;

    @ApiModelProperty("套餐名称")
    private String packageName;

    @ApiModelProperty("状态 1-正常 0-禁用")
    private String status;

    @ApiModelProperty("备注")
    private String remarks;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("创建人id")
    private String createUserId;

    @ApiModelProperty("创建人名称")
    private String createUserName;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    @ApiModelProperty("更新人id")
    private String updateUserId;

    @ApiModelProperty("更新人名称")
    private String updateUserName;
}