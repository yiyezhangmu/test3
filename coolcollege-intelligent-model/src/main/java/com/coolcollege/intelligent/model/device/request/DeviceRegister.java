package com.coolcollege.intelligent.model.device.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
public class DeviceRegister {

    /**
     * 设备萤石序列号
     */
    @NotBlank(message = "设备萤石序列号 不能为空")
    private String ysDeviceSerial;


    /**
     * 设备国标通道 license ids
     */
    //@NotBlank(message = "设备国标通道 license ids 不能为空")
    private List<String> chlIds;

    /**
     * 设备国标license id
     */
    @NotBlank(message = "设备国标license id 不能为空")
    private String id;


}
