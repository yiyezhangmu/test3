package com.coolcollege.intelligent.model.inspection.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author byd
 * @date 2025-10-14 14:47
 */
@Data
public class AiInspectionStatisticsProblemPicVO {

    @ApiModelProperty("场景id")
    private Long sceneId;

    @ApiModelProperty("场景名称")
    private String sceneName;

    @ApiModelProperty("图片地址")
    private String picture;

    @ApiModelProperty("抓拍日期")
    private Date captureDate;

    @ApiModelProperty("抓拍时间")
    private Date captureTime;

    @ApiModelProperty("设备id")
    private Long deviceId;

    @ApiModelProperty("设备通道id")
    private Long deviceChannelId;

    @ApiModelProperty("设备场景id")
    private Long storeSceneId;

    @ApiModelProperty("设备场景名称")
    private String storeSceneName;
}
