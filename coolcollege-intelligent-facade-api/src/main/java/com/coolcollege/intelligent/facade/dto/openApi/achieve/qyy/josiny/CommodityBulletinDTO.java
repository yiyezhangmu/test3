package com.coolcollege.intelligent.facade.dto.openApi.achieve.qyy.josiny;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class CommodityBulletinDTO {

    private String type;

    private String dingDeptId;

    private String time;

    private Date updateTime;

    private String title;

    private List<DataListSub> dataList;

    @Data
    public static class DataListSub{
        private String seasonAndFootWear;

        private int fourteenDaySales;

        private int yesterdaySales;

        private String goodsPic;

        private String goodsNo;

        private List<InventoryListSub> inventoryList;

        private String inventoryCount = "0";
    }

    @Data
    public static class InventoryListSub{

        private String size;

        private int inventory;
    }
}
