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
public class ColumnCompleteRateRankDataVO {

    @ApiModelProperty("检查项id")
    private String metaColumnId;

    @ApiModelProperty("检查项名称")
    private String metaColumnName;

    @ApiModelProperty("未完成数量")
    private Long unFinishColumnNum;

    @ApiModelProperty("完成率")
    private BigDecimal completeRate;
}
