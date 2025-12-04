package com.coolcollege.intelligent.model.inspection.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author byd
 * @date 2025-10-14 14:33
 */
@Data
public class AiInspectionStatisticsSceneVO {

    @ApiModelProperty("场景id")
    private Long sceneId;

    @ApiModelProperty("场景名称")
    private String sceneName;

    /**
     * 巡检总次数
     */
    @ApiModelProperty("巡检总次数")
    private Long patrolTotalNum;

    /**
     * 不合格次数
     */
    @ApiModelProperty("巡检不合格次数")
    private Long failNum;

    /**
     * 合格次数
     */
    @ApiModelProperty("巡检合格次数")
    private Long passNum;

    @ApiModelProperty("有效巡检次数")
    private Long totalValidInspectionCount;
}
