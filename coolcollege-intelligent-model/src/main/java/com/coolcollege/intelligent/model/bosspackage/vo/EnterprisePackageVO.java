package com.coolcollege.intelligent.model.bosspackage.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author ：xugangkun
 * @description：TODO
 * @date ：2022/3/24 14:11
 */
@Data
public class EnterprisePackageVO {
    @ApiModelProperty("主键")
    private Long id;

    @ApiModelProperty("套餐名称")
    private String packageName;

    @ApiModelProperty("状态 1-正常 0-禁用")
    private String status;

    @ApiModelProperty("备注")
    private String remarks;

    @ApiModelProperty("使用数量")
    private Integer useNum;

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
