package com.coolcollege.intelligent.facade.dto.openApi.achieve.qyy;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author zhangchenbiao
 * @FileName: UserAchieveDTO
 * @Description:用户业绩完成推送
 * @date 2023-03-30 15:03
 */
@Data
public class UserAchieveSalesDTO {

    /**
     * 用户业绩列表
     */
    private List<UserAchieveSales> userSalesList;

    @Data
    public static class UserAchieveSales{

        /**
         * 钉钉用户Id
          */
        private String userId;

        /**
         * 日期(yyyy-MM-dd)
         */
        private String salesDt;

        /**
         * 组织id
         */
        private String dingDeptId;

        /**
         * 组织名称(门店名称)
         */
        private String deptName;

        /**
         * 当日实际业绩
         */
        private BigDecimal salesAmtD;

        /**
         * 当日完成率
         */
        private BigDecimal salesRateD;

        /**
         * 本月累计
         */
        private BigDecimal salesAmtM;

        /**
         * 月完成率
         */
        private BigDecimal salesRateM;

        /**
         * 上报时间(yyyy-MM-dd HH:mm:ss)
         */
        private Date etlTm;

        /**
         * 分子公司排名
         */
        private Integer topComp;
    }
}
