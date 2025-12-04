package com.coolcollege.intelligent.model.inspection.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author byd
 * @date 2025-10-14 16:09
 */
@Data
public class AiInspectionStatisticsPicDetailVO {

    @ApiModelProperty("门店id")
    private String storeId;

    @ApiModelProperty("场景id")
    private Long sceneId;

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

    @ApiModelProperty("图片url")
    private String picture;

    @ApiModelProperty("AI检测结果图")
    private String aiCheckPics;

    @ApiModelProperty("AI检查项结果：PASS-合格, FAIL-不合格, INAPPLICABLE-不适用")
    private String aiResult;

    @ApiModelProperty("AI执行状态 ， 0未执行 1分析中 2已完成 3 分析失败  4 设备抓拍图片失败'")
    private Integer aiStatus;

    @ApiModelProperty("AI执行失败原因")
    private String aiFailReason;
}
