package com.coolcollege.intelligent.model.storework.vo;

import com.coolcollege.intelligent.common.util.NumberFormatUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;


/**
 * @author wxp
 */
@ApiModel
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StoreWorkStoreSummaryVO {

    @ApiModelProperty("门店id")
    private String storeId;

    @ApiModelProperty("门店名称")
    private String storeName;

    @ApiModelProperty("门店编号")
    private String storeNum;

    @ApiModelProperty("所属区域")
    private String fullRegionName;

    @ApiModelProperty("明细列表")
    private List<StoreWorkDataDetailVO> detailList;

    @ApiModelProperty(value = "门店区域ID")
    private Long storeRegionId;

}
