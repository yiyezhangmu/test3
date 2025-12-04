package com.coolcollege.intelligent.model.homepage.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zhangchenbiao
 * @FileName: AuthDataStatisticRequestDTO
 * @Description:
 * @date 2022-06-20 15:26
 */
@Data
public class DataStatisticRequestDTO {

    @ApiModelProperty("执行类型：YEAR:年 MONTH:月 WEEK:周 DAY:天")
    private String businessCycle;

    @ApiModelProperty("年yyyy  月yyyymm 周yyyymmdd(某周第一天)  天yyyymmdd")
    private Integer timeUnion;

    @ApiModelProperty("排序字段")
    private String sortField;

    @ApiModelProperty("排序类型  DESC  ASC")
    private String sortType;

    @ApiModelProperty("获取数量记录数")
    private Integer limitNum;

    @ApiModelProperty("是否需要跟前一个周期比较")
    private Boolean isNeedCompare;

    @ApiModelProperty("门店Id")
    private String storeId;

}
