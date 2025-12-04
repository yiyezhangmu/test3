package com.coolcollege.intelligent.facade.dto.openApi.achieve.qyy;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author zhangchenbiao
 * @FileName: StoreAchieveGoalDTO
 * @Description:门店业绩目标推送
 * @date 2023-03-30 14:57
 */
@Data
public class StoreAchieveGoalDTO {

    private List<StoreAchieveGoal> storeGoalList;

    /**
     * 月份(yyyy-MM)
     */
    private String mth;

    /**
     * 时间类型
     */
    private String timeType;

    @Data
    public static class StoreAchieveGoal{

        /**
         * 组织id(门店id)
         */
        private String dingDeptId;

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
