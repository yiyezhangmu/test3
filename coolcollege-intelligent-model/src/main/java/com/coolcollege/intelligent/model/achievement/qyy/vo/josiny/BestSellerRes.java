package com.coolcollege.intelligent.model.achievement.qyy.vo.josiny;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class BestSellerRes {
    @ApiModelProperty("ding部门id")
    private String dingDeptId;

    private Date updateTime;
    @ApiModelProperty("子列表")
    private List<DataListSub> dataList;

    @Data
    public static class DataListSub {

        //
        @ApiModelProperty("季节+鞋类")
        private String seasonAndFootWear;
        //
        @ApiModelProperty("销量")
        private BigDecimal salesVolume;
        //
        @ApiModelProperty("销量星级")
        private Integer salesVolumeStar;
        //
        @ApiModelProperty("动销")
        private BigDecimal movablePin;
        //
        @ApiModelProperty("动销星级")
        private Integer movablePinStar;
        //
        @ApiModelProperty("颜色")
        private String color;
        //
        @ApiModelProperty("单价")
        private BigDecimal price;
        //
        @ApiModelProperty("库存")
        private Integer inventory;
        //
        @ApiModelProperty("铺货")
        private Integer distribution;
        //
        @ApiModelProperty("货号")
        private String goodsNo;
        //
        @ApiModelProperty("货品图片url")
        private String goodsPic;

        @ApiModelProperty("类型\n" +
                "ws:女鞋\n" +
                "ms:男鞋\n" +
                "bg:箱包")
        private String tag;
    }
}
