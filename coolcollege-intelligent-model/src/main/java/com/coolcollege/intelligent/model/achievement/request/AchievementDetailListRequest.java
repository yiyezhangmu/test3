package com.coolcollege.intelligent.model.achievement.request;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author shuchang.wei
 * @date 2021/5/20 14:04
 */
@Data
public class AchievementDetailListRequest {
    /**
     * 开始时间
     */
    private Date beginDate;

    /**
     * 结束时间
     */
    private Date endDate;

    /**
     * 门店id
     */
    private List<String> storeIds;

    /**
     * 页数
     */
    private Integer pageNum;

    /**
     * 页数大小
     */
    private Integer pageSize;

    /**
     * 业绩产生人id
     */
    private List<String> produceUserIds;

    /**
     * 业绩类型id
     */
    private List<Long> achievementTypeIds;

    /**
     * 时间类型
     */
    private String type;

    /**
     * 是否查询空业绩产生人
     */
    private Boolean isNullProduceUser;
}
