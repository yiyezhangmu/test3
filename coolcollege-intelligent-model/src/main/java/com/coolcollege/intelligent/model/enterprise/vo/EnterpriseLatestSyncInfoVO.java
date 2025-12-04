package com.coolcollege.intelligent.model.enterprise.vo;

import lombok.Data;

import java.util.Date;

/**
 * describe:
 *
 * @author wxp
 * @date 2021/3/27
 */
@Data
public class EnterpriseLatestSyncInfoVO {

    /**
     * 是否有进行中的同步
     */
    private boolean hasLatestSync = false;
    /**
     * 最新同步状态
     */
    private Integer latestStatus;

    private String latestFailRemark;

    /**
     * 最新同步完成时间
     */
    private Date latestSyncSuccessEndTime;

}
