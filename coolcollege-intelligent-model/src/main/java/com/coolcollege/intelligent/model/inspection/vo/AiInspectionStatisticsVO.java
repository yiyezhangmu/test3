package com.coolcollege.intelligent.model.inspection.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * AI巡检统计结果VO
 *
 * @author zhangchenbiao
 * @since 2025/10/11
 */
@Data
public class AiInspectionStatisticsVO {

    /**
     * 门店名称
     */
    @ApiModelProperty("门店名称")
    private String storeName;

    /**
     * 门店id
     */
    @ApiModelProperty("门店id")
    private String storeId;

    /**
     * 日期
     */
    @ApiModelProperty("日期")
    private Date captureDate;

    /**
     * 日期
     */
    @ApiModelProperty("周报日期(周一)")
    private Date weekDay;

    /**
     * 日期
     */
    @ApiModelProperty("周报日期(周日)")
    private Date weekEndDay;

    /**
     * 巡检场景数
     */
    @ApiModelProperty("巡检场景数")
    private Long patrolSceneNum;

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
    @ApiModelProperty("不合格次数")
    private Long failNum;

    /**
     * 合格次数
     */
    @ApiModelProperty("合格次数")
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

    /**
     * 不合格图片
     */
    @ApiModelProperty("不合格图片")
    private String failPic;

    @ApiModelProperty("报表类型 日报:DAY 周报:WEEK")
    private String reportType;

    @ApiModelProperty("场景id")
    private Long sceneId;
}
