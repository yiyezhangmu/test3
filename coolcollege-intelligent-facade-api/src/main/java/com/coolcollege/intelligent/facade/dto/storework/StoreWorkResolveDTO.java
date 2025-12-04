package com.coolcollege.intelligent.facade.dto.storework;

import lombok.Data;

import java.util.Date;

/**
 * @Author suzhuhong
 * @Date 2022/10/17 18:37
 * @Version 1.0 分解
 */
@Data
public class StoreWorkResolveDTO {
    /**
     * 当前企业ID
     */
    private String enterpriseId;

    /**
     * 当前时间
     */
    private Date currentDate;

    /**
     * 是否当天发起定时任务 是 true 否 false
     */
    private Boolean pushFlag = Boolean.TRUE;

    private String workCycle;

    /**
     * 分解指定的店务记录
     */
    private Long storeWorkId;

    /**
     * 是否补发  补发 true 非补发 false
     */
    private Boolean reissueFlag = Boolean.FALSE;


}
