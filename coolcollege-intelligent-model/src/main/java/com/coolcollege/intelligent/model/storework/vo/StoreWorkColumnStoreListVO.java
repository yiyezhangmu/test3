package com.coolcollege.intelligent.model.storework.vo;

import cn.afterturn.easypoi.excel.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.models.auth.In;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * @author wxp
 */
@ApiModel
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StoreWorkColumnStoreListVO {


    @ApiModelProperty("检查项id")
    private String metaColumnId;

    @ApiModelProperty("作业事项名称")
    private String metaColumnName;

    @ApiModelProperty("门店id")
    private String storeId;

    @Excel(name = "门店名称")
    @ApiModelProperty("门店名称")
    private String storeName;

    @Excel(name = "门店编号")
    @ApiModelProperty("门店编号")
    private String storeNum;

    @Excel(name = "所属区域")
    @ApiModelProperty("区域名称(全路径)")
    private String fullRegionName;

    @Excel(name = "执行日期")
    @ApiModelProperty("执行日期")
    private String storeWorkDate;

    @Excel(name = "完成状态")
    @ApiModelProperty("完成状态")
    private Integer submitStatus;

    @Excel(name = "检查项结果")
    @ApiModelProperty("检查项结果:PASS,FAIL,INAPPLICABLE")
    private String checkResult;
}
