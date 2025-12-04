package com.coolcollege.intelligent.model.setting.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author ：xugangkun
 * @description：TODO
 * @date ：2021/5/10 15:19
 */
@Data
public class EnterpriseAccessCoolCollegeDTO {
    /**
     * 企业Id
     */
    @NotBlank(message = "企业id不能为空")
    private String enterpriseId;

    /**
     * 企业Id
     */
    @NotNull(message = "接入酷学院配置不能为空")
    private Boolean accessCoolCollege;

    @NotNull(message = "发送待办不能为空")
    private Boolean sendUpcoming;

    @NotNull(message = "是否支持客流不能为空")
    private Boolean syncPassenger;

    @ApiModelProperty("是否开启外部用户")
    @NotNull(message = "是否开启外部用户配置不能为空")
    private Boolean enableExternalUser;

    @ApiModelProperty("自定义套餐结束时间")
    private Long CustomizePackageEndTime;

    @ApiModelProperty("ai算法")
    private String aiAlgorithms;

    @ApiModelProperty("扩展字段")
    private String extendField;

}
