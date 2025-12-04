package com.coolcollege.intelligent.model.device.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2020/08/18
 */
@Data
@ApiModel
public class UnBindDeviceRequest {
    /**
     * 设备id
     */
    @ApiModelProperty("设备ID列表")
    @JsonProperty(value= "device_id_list")
    private List<String> deviceIdList;


}
