package com.coolcollege.intelligent.model.device.dto;

import com.coolcollege.intelligent.model.page.PageBaseRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class DeviceAuthPageDTO extends PageBaseRequest {

    @ApiModelProperty("设备ID/设备名称")
    private String keywords;

    @ApiModelProperty("appId")
    private String appId;

    @ApiModelProperty("授权状态 0未授权 1授权中 2取消授权")
    private String authStatus;

}
