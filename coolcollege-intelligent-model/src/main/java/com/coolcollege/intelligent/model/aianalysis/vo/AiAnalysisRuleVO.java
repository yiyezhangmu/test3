package com.coolcollege.intelligent.model.aianalysis.vo;

import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.model.aianalysis.AiAnalysisRuleDO;
import com.coolcollege.intelligent.model.storework.dto.StoreWorkCommonDTO;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * <p>
 * AI分析规则VO
 * </p>
 *
 * @author wangff
 * @since 2025/7/1
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AiAnalysisRuleVO {
    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("规则名称")
    private String ruleName;

    @ApiModelProperty("有效期开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startTime;

    @ApiModelProperty("有效期结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endTime;

    @ApiModelProperty("抓拍设备，0所有设备、1指定场景设备")
    private Integer captureDevice;

    @ApiModelProperty("抓拍设备场景，多选逗号隔开")
    private String captureDeviceScene;

    @ApiModelProperty("抓拍时间，时分，多选逗号隔开")
    private String captureTimes;

    @ApiModelProperty("AI分析模型id列表")
    private String models;

    @ApiModelProperty("AI分析模型名称列表")
    private String aiModelNames;

    @ApiModelProperty("报告推送时间")
    @JsonFormat(pattern = "HH:mm")
    private LocalTime reportPushTime;

    @ApiModelProperty("门店范围")
    private List<StoreWorkCommonDTO> storeRange;

    @ApiModelProperty("报告推送人")
    private List<StoreWorkCommonDTO> reportPusher;

    public static AiAnalysisRuleVO convert(AiAnalysisRuleDO entity) {
        if (entity == null) {
            return null;
        }
        return AiAnalysisRuleVO.builder()
                .id(entity.getId())
                .ruleName(entity.getRuleName())
                .startTime(entity.getStartTime())
                .endTime(entity.getEndTime())
                .captureDevice(entity.getCaptureDevice())
                .captureDeviceScene(entity.getCaptureDeviceScene())
                .captureTimes(entity.getCaptureTimes())
                .models(entity.getModels())
                .reportPushTime(entity.getReportPushTime())
                .storeRange(JSONObject.parseArray(entity.getStoreRange(), StoreWorkCommonDTO.class))
                .reportPusher(JSONObject.parseArray(entity.getReportPusher(), StoreWorkCommonDTO.class))
                .build();
    }
}
