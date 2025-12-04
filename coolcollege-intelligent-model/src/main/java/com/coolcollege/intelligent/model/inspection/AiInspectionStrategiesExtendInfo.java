package com.coolcollege.intelligent.model.inspection;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author byd
 */
@ApiModel(description = "AI巡检策略扩展配置信息")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiInspectionStrategiesExtendInfo {

    @ApiModelProperty(value = "启用抓拍频率智能调度: 0-禁用, 1-启用", example = "1")
    private Integer enableFrequencyScheduling = 0;

    @ApiModelProperty(value = "连续检测合格次数阈值", example = "3")
    private Integer qualifiedCountThreshold = 0;

    @ApiModelProperty(value = "抓拍间隔增加分钟数", example = "10")
    private Integer intervalIncrement = 0;

    @ApiModelProperty(value = "最大抓拍间隔(分钟)", example = "60")
    private Integer maxCaptureInterval = 0;

    @ApiModelProperty(value = "重置抓拍间隔类型: 0-未开启, 1-下一时段重置, 2-下一时段重置或检测不合格时重置", example = "0")
    private Integer resetIntervalType = 0;

    @ApiModelProperty(value = "检测不合格处理方式: 0-继续检测, 1-当前时段不再检测, 2-当天不再检测", example = "0")
    private Integer failHandleType = 0;

    @ApiModelProperty(value = "工单创建规则: 0-不自动发起, 1-自动发起, 2-自动发起(当前抓拍时段仅发起一次), 3-自动发起(当天仅发起一次)", example = "2")
    private Integer ticketCreateRule = 0;
}
