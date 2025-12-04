package com.coolcollege.intelligent.model.achievement.request;

import com.coolcollege.intelligent.model.achievement.dto.AchievementTargetDetailDTO;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author chenyupeng
 * @since 2021/10/26
 */
@Data
public class AchievementTargetRequest {

    /**
     * id
     */
    private Long id;

    /**
     * 门店数组集合
     */
    private List<String> storeIds;

    /**
     * 日期
     */
    private Date beginDate;

    /**
     * 年份
     */
    private Integer achievementYear;

    /**
     * 时间类型
     */
    private String timeType;

    /**
     * 门店id
     */
    private String storeId;

    /**
     * 页数
     */
    private Integer pageNum;

    /**
     * 页数大小
     */
    private Integer pageSize;

    private List<AchievementTargetDetailDTO> targetDetailList;

    /**
     * 年业绩目标
     */
    private BigDecimal yearAchievementTarget;

}
