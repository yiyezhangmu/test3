package com.coolcollege.intelligent.model.patrolstore.param;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author yezhe
 * @date 2020-12-08 19:25
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatrolStoreSignOutParam {
    @NotNull(message = "巡店记录id不能为空")
    private Long businessId;

    private String signEndAddress;

    private String endLongitudeLatitude;

    @NotNull(message = "巡店打卡状态")
    private Integer signOutStatus;
    /**
     * 签退时间,不传则使用当前时间
     */
    private Date signEndTime;
    /**
     * 单位:分钟
     */
    private Long defaultTourTime;

    /**
     * 签退方式
     */
    private String signOutWay;

    /**
     * 签退备注
     */
    private String signOutRemark;

    /**
     * 稽核选择的签字人
     */
    private String signatureUser;

    /**
     * 签退图片
     */
    private String signOutImg;


}
