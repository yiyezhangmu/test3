package com.coolcollege.intelligent.model.device.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
/**
 * @Author suzhuhong
 * @Date 2022/12/9 16:08
 * @Version 1.0
 */
@Data
@ApiModel
public class DeviceUpdateRequest implements Serializable {

    private static final long serialVersionUID = 8873696000420282805L;

    /**
     *设备Id
     */
    @JsonProperty(value= "device_id")
    @ApiModelProperty("设备ID")
    @NotBlank(message = "设备ID不能为空")
    private String deviceId;
    /**
     * 设备名称
     */
    @JsonProperty(value= "device_name")
    @ApiModelProperty("设备名称")
    @NotBlank(message = "设备名称不能为空")
    private String name;

    /**
     * 设备名称
     */
    @JsonProperty(value= "channel_name")
    @ApiModelProperty("通道名称")
    @NotBlank(message = "通道名称不能为空")
    private String channelName;

    /**
     * 设备名称
     */
    @JsonProperty(value= "channel_no")
    @ApiModelProperty("通道号")
    @NotBlank(message = "通道号")
    private String channelNo;
    /**
     * 备注
     *
     */
    @JsonProperty(value= "remark")
    @ApiModelProperty("备注")
    private String remark;

    /**
     * 场景
     */
    @JsonProperty(value= "scene")
    @ApiModelProperty("场景")
    private String  scene;

    @JsonProperty(value= "has_ptz")
    @ApiModelProperty("云台")
    private Boolean hasPtz;

    /**
     * 门店 场景id
     */
    @ApiModelProperty("门店场景ID")
    private Long storeSceneId;

    @ApiModelProperty("offline离线、online在线")
    private String deviceStatus;

}
