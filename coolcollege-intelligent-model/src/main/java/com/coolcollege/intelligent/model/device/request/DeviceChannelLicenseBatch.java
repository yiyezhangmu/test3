package com.coolcollege.intelligent.model.device.request;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class DeviceChannelLicenseBatch {

    /**
     * 数量
     */
    @NotNull(message = "数量不能为空")
    private Integer count;


    /**
     * license ids
     */
    @NotEmpty(message = "id 不能为空")
    private List<String> licenseIds;
}
