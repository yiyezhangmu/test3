package com.coolcollege.intelligent.facade.dto.openApi.achieve.qyy.josiny;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class PushAchieveDTO {

    private List<OutData> achieveList;

    @Data
    public static class OutData {
        private InnerData dayData;
        private InnerData weekData;
        private InnerData monthData;
        //第三方部门唯一id
        private String dingDeptId;
        /**
         * 推送类型
         * HQ:总部
         * COMP：分公司
         * STORE:门店
         */
        private String pushType;
        //组织名称
        private String deptName;
    }


    @Data
    public static class InnerData {
        //总销售额（业绩）
        private BigDecimal grossSales;
        //总销售额同比
        private BigDecimal grossSalesYoy;
        //销售额达成率
        private BigDecimal grossSalesRate;
        //完成率
        private BigDecimal finishRate;
        //完成率同比
        private BigDecimal finishRateYoy;
        //单产
        private BigDecimal output;
        //单产同比
        private BigDecimal outputYoy;
        //缺口
        private BigDecimal breach;
        //单产达成率
        private BigDecimal outputRate;
        //销量
        private BigDecimal salesVolume;
        //销量达成率
        private BigDecimal salesVolumeRate;
        //客单价
        private BigDecimal perCustomer;
        //客单价达成率
        private BigDecimal perCustomerRate;
        /**
         * String	时间类型
         * month:月
         * week:周
         * day:天
         */
        private String timeType;
        /**
         * 业务日期
         * 月yyyy-MM
         * 周取周一对应yyyy-MM-dd
         * 日yyyy-MM-dd
         */
        private String timeValue;
        //业绩同比
        private BigDecimal achieveYoy;

    }
}
