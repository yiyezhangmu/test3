package com.coolcollege.intelligent.facade.dto.openApi.achieve.qyy;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author zhangchenbiao
 * @FileName: PullUserAchieveSalesDTO
 * @Description:
 * @date 2023-04-28 11:31
 */
@Data
public class PullUserAchieveSalesDTO {

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
     * 日目标
     */
    private BigDecimal salesGoalD;

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
     * 月目标
     */
    private BigDecimal salesGoalM;

    /**
     * 上报时间(yyyy-MM-dd HH:mm:ss)
     */
    private Date etlTm;
}
