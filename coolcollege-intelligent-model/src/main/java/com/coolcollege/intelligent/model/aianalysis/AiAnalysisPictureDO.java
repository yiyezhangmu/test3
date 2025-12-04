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
 * AI分析报告图片
 * @author   zhangchenbiao
 * @date   2025-06-30 05:04
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiAnalysisPictureDO implements Serializable {
    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("AI分析规则id")
    private Long ruleId;

    @ApiModelProperty("门店id")
    private String storeId;

    @ApiModelProperty("图片url")
    private String url;

    @ApiModelProperty("抓拍时间")
    private LocalTime captureTime;

    @ApiModelProperty("图片生成日期")
    private LocalDate generateDate;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("设备id")
    private String deviceId;

    @ApiModelProperty("通道号")
    private String channelNo;

    @ApiModelProperty("门店场景id")
    private Long storeSceneId;
}