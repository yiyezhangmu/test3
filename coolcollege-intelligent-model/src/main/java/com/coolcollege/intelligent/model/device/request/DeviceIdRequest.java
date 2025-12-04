package com.coolcollege.intelligent.model.device.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2020/08/18
 */
@Data
@ApiModel
public class DeviceIdRequest {

    @NotEmpty
    @ApiModelProperty("设备ID列表")
    private List<String>  deviceIdList;
}
