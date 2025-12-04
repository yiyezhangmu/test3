package com.coolcollege.intelligent.model.inspection.entity;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * AI巡检门店设备抓拍图片分析结果表
 * @author   zhangchenbiao
 * @date   2025-10-11 04:33
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiInspectionStorePictureDO implements Serializable {
    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("ai巡检表id")
    private Long inspectionId;

    @ApiModelProperty("场景id")
    private Long sceneId;

    @ApiModelProperty("场景名称")
    private String sceneName;

    @ApiModelProperty("门店id")
    private String storeId;

    @ApiModelProperty("门店名称")
    private String storeName;

    @ApiModelProperty("区域ID")
    private Long regionId;

    @ApiModelProperty("区域路径")
    private String regionPath;

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

    @ApiModelProperty("图片url")
    private String picture;

    @ApiModelProperty("AI检测结果图")
    private String aiCheckPics;

    @ApiModelProperty("AI检查项结果：PASS-合格, FAIL-不合格, INAPPLICABLE-不适用")
    private String aiResult;

    @ApiModelProperty("AI执行状态 ， 0未执行 1分析中 2已完成 3失败  4设备抓拍图片失败")
    private Integer aiStatus;

    @ApiModelProperty("AI执行失败原因")
    private String aiFailReason;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("AI轮次监测结果：PASS-合格, FAIL-不合格, INAPPLICABLE-不适用")
    private String aiPeriodResult;

    @ApiModelProperty("抓拍这周周一的日期")
    private Date weekDay;

    @ApiModelProperty("抓拍批次id")
    private Long inspectionPeriodId;

    @ApiModelProperty("抓拍任务id")
    private String captureTaskId;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("创建者")
    private String createUserId;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    @ApiModelProperty("更新人")
    private String updateUserId;

    @ApiModelProperty("删除标识")
    private Boolean deleted;

    @ApiModelProperty("分析内容")
    private String aiContent;
}