package com.coolcollege.intelligent.model.achievement.request;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author chenyupeng
 * @since 2021/12/30
 */
@Data
public class AchievementTotalStatisticsRequest {

    /**
     * 门店ID
     */
    private String storeIdStr;

    /**
     * 区域Id
     */
    private Long regionId;

    private Date beginDate;

    private Date endDate;
}
