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
public class AiInspectionProblemStoreReportVO {

    @ApiModelProperty("门店Id")
    private String storeId;


    @ApiModelProperty("门店名称")
    private String storeName;

    /**
     * 不合格次数
     */
    @ApiModelProperty("不合格次数")
    private Long failNum;

    /**
     * 不合格
     */
    @ApiModelProperty("不合格占比")
    private BigDecimal failRate;
}
