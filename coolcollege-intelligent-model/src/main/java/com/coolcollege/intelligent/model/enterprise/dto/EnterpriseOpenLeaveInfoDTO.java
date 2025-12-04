package com.coolcollege.intelligent.model.enterprise.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 
 * @author   wxp
 * @date   2022-08-17 08:10
 */
@Data
@ApiModel(value = "企业留资入参实体")
public class EnterpriseOpenLeaveInfoDTO implements Serializable {

    @ApiModelProperty("企业cropId")
    private String corpId;

    @ApiModelProperty("用户授权code")
    private String authCode;

}