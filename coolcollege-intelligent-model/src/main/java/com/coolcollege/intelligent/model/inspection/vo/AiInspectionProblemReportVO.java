package com.coolcollege.intelligent.model.inspection.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * AI巡检统计结果VO
 *
 * @author zhangchenbiao
 * @since 2025/10/11
 */
@Data
public class AiInspectionProblemReportVO {

    @ApiModelProperty("场景id")
    private Long sceneId;


    @ApiModelProperty("场景名称")
    private String sceneName;

    /**
     * 不合格次数
     */
    @ApiModelProperty("不合格次数")
    private Long failNum;

    /**
     * 不合格次数
     */
    @ApiModelProperty("不合格占比")
    private BigDecimal failRate;
}
