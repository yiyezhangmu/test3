package com.coolcollege.intelligent.model.device.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2020/08/06
 */
@Data
@ApiModel
public class BindDeviceRequest {

    /**
     * 门店id
     */
    @JsonProperty(value= "store_ids")
    @ApiModelProperty("门店名称")
    private List<String> storeIds;

    /**
     * 绑定 的B1设备deviceId
     */
    @JsonProperty(value= "device_id_list")
    @NotNull
    @ApiModelProperty("设备ID列表")
    private List<String> deviceIdList;

}
