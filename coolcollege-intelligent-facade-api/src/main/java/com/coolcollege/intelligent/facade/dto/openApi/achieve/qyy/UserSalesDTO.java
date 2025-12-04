package com.coolcollege.intelligent.facade.dto.openApi.achieve.qyy;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class UserSalesDTO {
    /**
     * 门店业绩目标列表
     */
    private List<StoreGoal> userGoalList;

    /**
     * 月份(yyyy-MM)
     */
    private String mth;

    /**
     * 时间类型：day\week\month
     */
    private String timeType;

    @Data
    public static class StoreGoal {

        /**
         * 用户id
         */
        private String userId;
        /**
         * 组织id(门店id)
         */
        private Long dingDeptId;

        /**
         * 组织名称(门店名称)
         */
        private String deptName;

        /**
         * 目标金额
         */
        private BigDecimal goalAmt;
    }

}
