package com.coolcollege.intelligent.model.device.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2020/08/18
 */
@Data
public class BaseDeviceRequest {

    @JsonProperty(value= "device_id")
    @NotBlank(message = "设备ID不能为空")
    private String deviceId;
}
