package com.coolcollege.intelligent.model.achievement.vo;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class NewProductDataVO {
    @ApiModelProperty("型号")
    private String type;
    @ApiModelProperty("中类")
    private String middleClass;
    @ApiModelProperty("品类")
    private String category;
    @ApiModelProperty("销售金额")
    private Long salesAmount;
    @ApiModelProperty("有这个商品的门店数")
    private Long storeNum;
    @ApiModelProperty("有这个商品的且已上架门店数")
    private Long onStoreNum;
    @ApiModelProperty("所有门店数量")
    private Long allStoreNum;
    @ApiModelProperty("出样率-暂时前端算，怕改动")
    private String sampleRate;
}
