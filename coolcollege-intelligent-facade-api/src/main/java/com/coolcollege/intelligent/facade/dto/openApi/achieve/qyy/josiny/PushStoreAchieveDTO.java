package com.coolcollege.intelligent.facade.dto.openApi.achieve.qyy.josiny;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class PushStoreAchieveDTO {

    private String dingDeptId;

    private String timeType;

    private List<DataListSub> dataList;

    @Data
    public static class DataListSub {
        //门店爆款
        private String storeBurst;
        //款号
        private String goodsNo;
        //年份
        private String year;
        //销量
        private int salesVolume;
        //销售额
        private BigDecimal sales;
        //库存
        private int inventory;
        //季节
        private String season;
        //货品图片
        private String goodsPic;
        //找门店（将货品挂在门店上）
        private String viewDingDeptId;
        //十四天销量
        private int fourteenDaySales;
    }

}
