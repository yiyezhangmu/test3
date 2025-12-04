package com.coolcollege.intelligent.model.aianalysis;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * AI分析规则
 * @author   zhangchenbiao
 * @date   2025-06-30 04:55
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiAnalysisRuleDO implements Serializable {
    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("规则名称")
    private String ruleName;

    @ApiModelProperty("有效期开始时间")
    private LocalDate startTime;

    @ApiModelProperty("有效期结束时间")
    private LocalDate endTime;

    @ApiModelProperty("抓拍设备，0所有设备、1指定场景设备")
    private Integer captureDevice;

    @ApiModelProperty("抓拍设备场景，多选逗号隔开")
    private String captureDeviceScene;

    @ApiModelProperty("抓拍时间，时分，多选逗号隔开")
    private String captureTimes;

    @ApiModelProperty("AI分析模型id列表")
    private String models;

    @ApiModelProperty("报告推送时间")
    private LocalTime reportPushTime;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("创建人id")
    private String createUserId;

    @ApiModelProperty("创建人名称")
    private String createUserName;

    @ApiModelProperty("修改时间")
    private Date updateTime;

    @ApiModelProperty("修改人id")
    private String updateUserId;

    @ApiModelProperty("修改人名称")
    private String updateUserName;

    @ApiModelProperty("门店范围")
    private String storeRange;

    @ApiModelProperty("报告推送人")
    private String reportPusher;
}