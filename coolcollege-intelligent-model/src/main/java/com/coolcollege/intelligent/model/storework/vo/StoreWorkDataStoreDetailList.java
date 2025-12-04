package com.coolcollege.intelligent.model.storework.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author byd
 * @date 2022-09-21 11:18
 */
@ApiModel
@Data
public class StoreWorkDataStoreDetailList {

    @ApiModelProperty("应完成检查项数")
    private Long totalColumnNum;

    @ApiModelProperty("未完成检查项数")
    private Long unFinishColumnNum;

    @ApiModelProperty("已完成检查项数")
    private Long finishColumnNum;

    @ApiModelProperty("完成率")
    private BigDecimal completeRate;

    @ApiModelProperty("工单数(总)")
    private Long questionNum;

    @ApiModelProperty("合格率")
    private BigDecimal passRate;

    @ApiModelProperty("门店排名")
    private Long rank;

    @ApiModelProperty("日期")
    private String storeWorkDate;
}
