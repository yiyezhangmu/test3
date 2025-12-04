package com.coolcollege.intelligent.model.device.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2021/06/11
 */
@Data
public class DeviceChannelUpdateRequest {

    @ApiModelProperty("通道id")
    @NotNull(message = "主键不能为空")
    private Long id;

    @ApiModelProperty("通道名称")
    @NotBlank(message = "通道名称不能为空")
    private String channelName;

    @ApiModelProperty("是否是球形机")
    private Boolean hasPtz;

    @ApiModelProperty("门店场景")
    private Long storeSceneId;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("offline离线、online在线")
    private String deviceStatus;


}
