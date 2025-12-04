package com.coolcollege.intelligent.model.device.request;

import com.coolstore.base.enums.AccountTypeEnum;
import com.coolstore.base.enums.YunTypeEnum;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Author suzhuhong
 * @Date 2022/12/9 16:08
 * @Version 1.0
 */
@Data
public class DeviceAddRequest extends BaseDeviceRequest {

    @ApiModelProperty("门店ID")
    @JsonProperty("store_ids")
    private List<String> storeIds;

    @ApiModelProperty("场景")
    @JsonProperty("scene")
    private String scene;

    @ApiModelProperty("设备名称")
    @JsonProperty("device_name")
    private String deviceName;

    @ApiModelProperty("备注")
    @JsonProperty("remark")
    private String remark;

    @ApiModelProperty("场景ID")
    private Long storeSceneId;

    @ApiModelProperty("来源")
    @JsonProperty("yunType")
    private YunTypeEnum yunType;

    @ApiModelProperty("平台 不传默认是平台账号")
    @JsonProperty("accountType")
    private AccountTypeEnum accountType;
}
