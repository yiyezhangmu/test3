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
public class StoreWorkColumnRankDataVO {

    @ApiModelProperty("检查项id")
    private Long metaColumnId;

    @ApiModelProperty("检查项名称")
    private String metaColumnName;

    @ApiModelProperty("不合格数量")
    private Long failNum;


}
