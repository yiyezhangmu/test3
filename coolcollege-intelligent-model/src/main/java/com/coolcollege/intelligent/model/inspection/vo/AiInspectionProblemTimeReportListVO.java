package com.coolcollege.intelligent.model.inspection.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * AI巡检统计结果VO
 *
 * @author zhangchenbiao
 * @since 2025/10/11
 */
@Data
public class AiInspectionProblemTimeReportListVO {

    @ApiModelProperty("时间点")
    private String hourTime;

    @ApiModelProperty("场景统计数据")
    List<AiInspectionProblemTimeReportVO> sceneTimeList;

}
