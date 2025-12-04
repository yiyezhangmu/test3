package com.coolcollege.intelligent.model.inspection;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 *
 * @author byd
 * @date 2025-11-03 17:37
 */
@Data
public class AiInspectionQuestionCreateDTO {

    @ApiModelProperty("场景id")
    private Long sceneId;

    @ApiModelProperty("巡检策略ID")
    private Long inspectionId;

    @ApiModelProperty("巡检批次id")
    private Long inspectionPeriodId;


    @ApiModelProperty("门店id")
    private String storeId;

    @ApiModelProperty("企业ID")
    private String enterpriseId;

    @ApiModelProperty("抓拍批次")
    private String captureTime;

    @ApiModelProperty("不合格图片列表")
    List<String> failImageList;

    @ApiModelProperty("不合格图片描述列表")
    List<String> errMsgList;

    @ApiModelProperty("不合格图片以及描述列表")
    List<AiInspectionStoreFailPictureDTO> failPictureList;

    @ApiModelProperty(value = "工单创建规则: 0-不自动发起, 1-自动发起, 2-自动发起(当前抓拍时段仅发起一次), 3-自动发起(当天仅发起一次)", example = "2")
    private Integer ticketCreateRule = 0;

    @ApiModelProperty("开始时间 如 09:00")
    private String beginTime;

    @ApiModelProperty("结束时间 如 21:00")
    private String endTime;

}
