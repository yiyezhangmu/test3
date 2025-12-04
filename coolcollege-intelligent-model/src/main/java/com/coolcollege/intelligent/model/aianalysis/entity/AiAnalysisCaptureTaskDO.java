package com.coolcollege.intelligent.model.aianalysis.entity;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author   zhangchenbiao
 * @date   2025-07-02 09:15
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiAnalysisCaptureTaskDO implements Serializable {
    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("抓拍任务id")
    private String captureTaskId;

    @ApiModelProperty("AI分析规则id")
    private Long ruleId;

    @ApiModelProperty("设备id")
    private String deviceId;

    @ApiModelProperty("通道号")
    private String channelNo;

    @ApiModelProperty("门店场景id")
    private Long storeSceneId;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("是否有结果")
    private Boolean hasResult;

    @ApiModelProperty("错误编码")
    private String errorCode;

    @ApiModelProperty("错误原因")
    private String errorMsg;

    @ApiModelProperty("报告生成日期")
    private LocalDate generateDate;
}