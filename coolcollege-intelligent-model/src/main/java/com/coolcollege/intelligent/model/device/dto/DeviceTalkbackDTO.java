package com.coolcollege.intelligent.model.device.dto;

import com.coolstore.base.enums.VideoProtocolTypeEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * <p>
 * 设备对讲DTO
 * </p>
 *
 * @author wangff
 * @since 2025/8/8
 */
@Data
public class DeviceTalkbackDTO {
    @ApiModelProperty("设备id")
    @NotBlank(message = "设备id不能为空")
    private String deviceId;

    @ApiModelProperty("通道号")
    private String channelNo;

    @ApiModelProperty("协议类型，如果是雄迈的设备，对讲协议需和直播流相同")
    private VideoProtocolTypeEnum protocolType;
}
