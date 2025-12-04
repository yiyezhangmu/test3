package com.coolcollege.intelligent.model.storework.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @Author suzhuhong
 * @Date 2022/9/8 16:05
 * @Version 1.0
 */
@Data
public class StoreTaskResolveRequest {

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

    /**
     * 是否手动点击补发  是 true 不是 false
     */
    @ApiModelProperty("是否手动点击补发  是 true 不是 false")
    private Boolean manualReissue;
}
