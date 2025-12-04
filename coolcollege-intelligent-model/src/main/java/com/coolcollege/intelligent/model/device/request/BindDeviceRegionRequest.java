package com.coolcollege.intelligent.model.device.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2021/01/12
 */
@Data
public class BindDeviceRegionRequest {
    @JsonProperty("region_id")
    private String regionId;
}
