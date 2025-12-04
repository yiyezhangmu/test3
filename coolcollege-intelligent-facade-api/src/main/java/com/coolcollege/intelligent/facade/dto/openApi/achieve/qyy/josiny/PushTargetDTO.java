package com.coolcollege.intelligent.facade.dto.openApi.achieve.qyy.josiny;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class PushTargetDTO {

    private List<OutData> pushTarget;

    @Data
    public static class OutData {

        private InnerData dayData;
        private InnerData weekData;
        private InnerData monthData;
        /**
         * 组织id
         */
        private String dingDeptId;
        /**
         * 推送类型
         * HQ:总部
         * COMP：分公司
         * SUP：督导
         * STORE:门店
         */
        private String pushType;
        /**
         * 打气的话
         */
        private String pepTalk;
        /**
         * 组织名称
         */
        private String deptName;

        @Data
        public static class InnerData {
            /**
             * 单产目标
             */
            private BigDecimal unitYieldTarget;
            /**
             * 业绩目标
             */
            private BigDecimal goalAmt;
            /**
             * 销量目标
             */
            private BigDecimal salesTarget;

            /**
             * 时间类型
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
        }
    }

}
