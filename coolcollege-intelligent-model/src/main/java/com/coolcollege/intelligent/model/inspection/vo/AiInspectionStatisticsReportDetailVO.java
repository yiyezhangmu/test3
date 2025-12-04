package com.coolcollege.intelligent.model.inspection.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * AI巡检统计结果VO
 *
 * @author zhangchenbiao
 * @since 2025/10/11
 */
@Data
public class AiInspectionStatisticsReportDetailVO {

    @ApiModelProperty("门店id")
    private String storeId;

    @ApiModelProperty("门店名称")
    private String storeName;

    @ApiModelProperty("报告日期")
    private Date reportTime;

    @ApiModelProperty("报告统计开始时间")
    private Date reportBeginTime;

    @ApiModelProperty("报告统计结束时间")
    private Date reportEndTime;

    /**
     * 巡检总次数
     */
    @ApiModelProperty("巡检总次数")
    private Long patrolTotalNum;

    @ApiModelProperty("有效巡检次数")
    private Long totalValidInspectionCount;

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

    /**
     * 合格率
     */
    @ApiModelProperty("合格率")
    private BigDecimal passRate;

    /**
     * 不合格率
     */
    @ApiModelProperty("不合格率")
    private BigDecimal failRate;


    @ApiModelProperty("问题TOP1")
    private String problemTop;

    /**
     * 巡检总次数
     */
    @ApiModelProperty("巡检问题TOP1不合格次数")
    private Long problemTotalNum;


    @ApiModelProperty("AI巡检场景列表")
    private List<AiInspectionStatisticsSceneVO> sceneList;

    @ApiModelProperty("AI巡检问题图片列表")
    private List<AiInspectionStatisticsProblemPicVO> problemList;
}
