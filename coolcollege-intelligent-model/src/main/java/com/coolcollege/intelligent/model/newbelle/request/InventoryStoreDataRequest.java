package com.coolcollege.intelligent.model.newbelle.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 百丽货品反馈-半年内有库存店铺数据
 */
@Data
public class InventoryStoreDataRequest {
    @ApiModelProperty("货品编码")
    private List<String> productNo;

    @ApiModelProperty("地区编码")
    private List<String> regionNo;

    @ApiModelProperty("地区名称")
    private List<String> regionName;

    @ApiModelProperty("品牌编码")
    private List<String> brandNo;

    private List<String> brandCNAME;

    private List<String> managerCityNo;

    private List<String> managerCityName;

    @ApiModelProperty("新店店铺编码/仓库编码")
    private List<String> storeNewNo;

    @ApiModelProperty("店铺名称")
    private List<String> storeName;

    @ApiModelProperty("店铺类别编码")
    private List<String> storeCategoryNo1;

    @ApiModelProperty("店铺大类编码")
    private List<String> storeCategoryNo2;

    @ApiModelProperty("店铺小类编码")
    private List<String> storeCategoryNo3;

    @ApiModelProperty("店铺级别编码")
    private List<String> storeLevelNo;

    @ApiModelProperty("店铺级别名称")
    private List<String> storeLevelName;



}
