package com.coolcollege.intelligent.model.inspection.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * AI巡检统计结果VO
 *
 * @author zhangchenbiao
 * @since 2025/10/11
 */
@Data
public class AiInspectionProblemTimeReportVO {

    @ApiModelProperty("场景id")
    private Long sceneId;

    @ApiModelProperty("场景名称")
    private String sceneName;

    @ApiModelProperty("时间点")
    private String hourTime;

    @ApiModelProperty("不合格次数")
    private Long failNum;
}
