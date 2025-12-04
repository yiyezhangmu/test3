package com.coolcollege.intelligent.model.storework.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;


/**
 * @author wxp
 */
@ApiModel
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StoreWorkFailColumnStoreListVO {


    @ApiModelProperty("检查项id")
    private String metaColumnId;

    @ApiModelProperty("作业事项名称")
    private String metaColumnName;

    @ApiModelProperty("门店id")
    private String storeId;

    @ApiModelProperty("门店名称")
    private String storeName;

    @ApiModelProperty("门店编号")
    private String storeNum;

    @ApiModelProperty("区域名称(全路径)")
    private String fullRegionName;

    @ApiModelProperty("不合格次数")
    private String failColumnNum;
}
