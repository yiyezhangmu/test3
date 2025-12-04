package com.coolcollege.intelligent.facade.dto.openApi.achieve.qyy;

import lombok.Data;

import java.util.List;

/**
 * 周报业务数据dto
 */
@Data
public class WeeklyNewspaperDataDTO {
    /**
     * 所在周的周一（yyyy-MM-dd）
     */
    private String mondyOfWeek;

    /**
     * 组织id(门店id)
     */
    private String dingDeptId;

    /**
     * 门店业绩概况
     */
    private StoreAchieve storeAchieve;

    /**
     * 销量top5
     */
    private List<SalesVolums> salesVolums;

//    public Date getMondyOfWeek(){
//        DateFormat ft = new SimpleDateFormat("yyyy-MM-dd");
//        Date date = null;
//        try {
//            date = ft.parse(this.mondyOfWeek);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        return date;
//    }

    /**
     * 门店业绩概况
     */
    @Data
    public static class StoreAchieve {
        /**
         * 分公司排名
         */
        private String compRank;

        /**
         * 全国排名
         */
        private String nationalRank;

        /**
         * 月目标
         */
        private String monthTarget;

        /**
         * 月达成率
         */
        private String monthAchieveRate;
        /**
         * 周目标
         */
        private String weekTarget;
        /**
         * 周业绩
         */
        private String weekAchieve;

        /**
         * 周连带率
         */
        private String weekAssociatedRate;

    }

    /**
     * 销量top5
     */
    @Data
    public static class SalesVolums {
        /**
         * 款号
         */
        private String styleNum;

        /**
         * 周销量
         */
        private String weekSales;

        /**
         * 销售占比
         */
        private String salesShare;

        /**
         * 库存
         */
        private String inventory;

    }
}
