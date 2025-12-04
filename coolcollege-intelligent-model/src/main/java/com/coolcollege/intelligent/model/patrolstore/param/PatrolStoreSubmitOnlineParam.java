package com.coolcollege.intelligent.model.patrolstore.param;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author yezhe
 * @date 2020-12-08 19:25
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatrolStoreSubmitOnlineParam {

    /**
     * 业务id(不传时为自主巡店)
     */
    private Long businessId;
    /**
     * 门店id(自主巡店时必传)
     */
    private String storeId;

    @NotNull(message = "检查表id不能为空")
    private Long metaTableId;

    /**
     * 签到时间,不传则使用当前时间
     */
    private Date signStartTime;

    /**
     * 签到时间,不传则使用当前时间
     */
    private Date signEndTime;

    @NotEmpty(message = "检查项信息不能为空")
    private List<DataStaTableColumnParam> dataStaTableColumnParamList;

    /**
     * 是否提交
     */
    private Boolean submit;

    /**
     * 标准检查项数据
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DataStaTableColumnParam {
        /**
         * 标准检查项模板id
         */
        @NotNull(message = "标准检查项模板id不能为空")
        private Long metaColumnId;

        /**
         * 检查项结果
         */
        private String checkResult;

        /**
         * 检查项结果id
         */
        private Long checkResultId;

        /**
         * 检查项结果名称
         */
        private String checkResultName;

        /**
         * 检查项上传的图片
         */
        private String checkPics;

        /**
         * 检查项的描述信息
         */
        private String checkText;

        /**
         * 分值
         */
        private BigDecimal checkScore;

        /**
         * 得分倍数
         */
        private BigDecimal scoreTimes;

        /**
         * 奖罚倍数
         */
        private BigDecimal awardTimes;
    }
}
