package com.coolcollege.intelligent.model.device.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ElemeOpenDeviceVideoRequest {

    @ApiModelProperty("请求数据")
    private OpenDeviceVideoRequest data;

}
