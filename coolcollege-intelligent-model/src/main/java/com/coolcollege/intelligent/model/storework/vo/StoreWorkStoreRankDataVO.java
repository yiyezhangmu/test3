package com.coolcollege.intelligent.model.storework.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author byd
 * @date 2022-09-21 11:18
 */
@ApiModel
@Data
public class StoreWorkStoreRankDataVO {

    @ApiModelProperty("门店Id")
    private String storeId;

    @ApiModelProperty("门店名")
    private String storeName;

    @ApiModelProperty("应完成项数")
    private Long totalColumnNum;

    @ApiModelProperty("已完成项数")
    private Long finishColumnNum;

    @ApiModelProperty("未完成项数")
    private Long unFinishColumnNum;

    @ApiModelProperty("店务完成率")
    private BigDecimal completeRate;

    @ApiModelProperty("平均合格率")
    private BigDecimal averagePassRate;

    @ApiModelProperty("平均得分")
    private BigDecimal averageScore;

    @ApiModelProperty("平均得分率")
    private BigDecimal averageScoreRate;

    @ApiModelProperty("平均点评率")
    private BigDecimal averageCommentRate;
    
    @ApiModelProperty("工单数(总)")
    private Long questionNum;
}
