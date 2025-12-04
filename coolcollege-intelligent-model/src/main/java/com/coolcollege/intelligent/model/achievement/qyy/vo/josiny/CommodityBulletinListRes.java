package com.coolcollege.intelligent.model.achievement.qyy.vo.josiny;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class CommodityBulletinListRes {
    @ApiModelProperty("类型")
    private String type;
    @ApiModelProperty("三方唯一id")
    private String dingDeptId;
    @ApiModelProperty("时间")
    private String time;

    private Date updateTime;
    @ApiModelProperty("标题")
    private String title;
    @ApiModelProperty("数据列表")
    private List<DataListSub> dataList;

    @Data
    public static class DataListSub{
        @ApiModelProperty("季节+鞋类")
        private String seasonAndFootWear;
        @ApiModelProperty("14天销量")
        private Integer fourteenDaySales;
        @ApiModelProperty("昨日销量")
        private Integer yesterdaySales;
        @ApiModelProperty("图片")
        private String goodsPic;
        @ApiModelProperty("货号")
        private String goodsNo;
        @ApiModelProperty("尺码与库存")
        private List<InventoryListSub> inventoryList;
    }

    @Data
    public static class InventoryListSub{

        private String size;

        private Integer inventory;
    }
}

