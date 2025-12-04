package com.coolcollege.intelligent.model.qyy;

import com.alibaba.fastjson.JSONArray;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QyyNewspaperAchieveDO implements Serializable {
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

    /**
     * 导购员业绩
     */
    private List<UserGoalByWeek> userAchieve;

    public static QyyNewspaperAchieveDO convert(WeeklyNewspaperDataDO weeklyNewspaperDataDO) {
        if (weeklyNewspaperDataDO == null) {
            return null;
        }
        QyyNewspaperAchieveDO qyyNewspaperAchieveDO = new QyyNewspaperAchieveDO();
        qyyNewspaperAchieveDO.setMondyOfWeek(weeklyNewspaperDataDO.getMondyOfWeek());
        qyyNewspaperAchieveDO.setDingDeptId(weeklyNewspaperDataDO.getDingDeptId());
        List<SalesVolums> salesVolums = JSONArray.parseArray(weeklyNewspaperDataDO.getSalesVolums(), SalesVolums.class);
        qyyNewspaperAchieveDO.setSalesVolums(salesVolums);
        qyyNewspaperAchieveDO.setStoreAchieve(toStoreAchieve(weeklyNewspaperDataDO));
        return qyyNewspaperAchieveDO;
    }

    public static StoreAchieve toStoreAchieve(WeeklyNewspaperDataDO weeklyNewspaperDataDO) {
        if (weeklyNewspaperDataDO == null) {
            return null;
        }
        StoreAchieve storeAchieve = new StoreAchieve();
        storeAchieve.setCompRank(weeklyNewspaperDataDO.getCompRank());
        storeAchieve.setNationalRank(weeklyNewspaperDataDO.getNationalRank());
        storeAchieve.setMonthTarget(weeklyNewspaperDataDO.getMonthTarget());
        storeAchieve.setMonthAchieveRate(weeklyNewspaperDataDO.getMonthAchieveRate());
        storeAchieve.setWeekTarget(weeklyNewspaperDataDO.getWeekTarget());
        storeAchieve.setWeekAchieve(weeklyNewspaperDataDO.getWeekAchieve());
        storeAchieve.setWeekAssociatedRate(weeklyNewspaperDataDO.getWeekAssociatedRate());
        return storeAchieve;
    }

    @Data
    public static class UserGoalByWeek {

        private String userId;

        private String name;

        private String weekAchieve;

        private String monthTarget;

        private String monthRate;
    }


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
