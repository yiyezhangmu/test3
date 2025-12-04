package com.coolcollege.intelligent.model.safetycheck.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author wxp
 */
@ApiModel
@Data
public class SignatureConfirmRequest {

    @ApiModelProperty("巡店ID")
    private Long businessId;

    @ApiModelProperty("签字url")
    private String signatureUrl;

    @ApiModelProperty("签字结果 pass同意 reject拒绝")
    private String signatureResult;

    @ApiModelProperty("签字备注")
    private String signatureRemark;


}
