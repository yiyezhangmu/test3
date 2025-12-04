package com.coolcollege.intelligent.model.safetycheck.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 大店长审核请求
 * @author wxp
 */
@ApiModel
@Data
public class BigStoreManagerAuditRequest {

    @NotNull(message = "巡店记录id不能为空")
    @ApiModelProperty("巡店记录id")
    private Long businessId;

    @NotBlank(message = "审核结果不能为空")
    @ApiModelProperty("审核结果 pass同意 reject拒绝")
    private String action;

    @ApiModelProperty("整体原因")
    @Length(max = 1000, message = "原因最多1000个字")
    private String remark;


}
