package com.coolcollege.intelligent.model.achievement.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @Description: 门店目标年记录dto
 * @Author: mao
 * @CreateDate: 2021/5/24 18:22
 */
@Data
public class AchievementTargetDTO {
    /**
     * id
     */
    private Long id;
    /**
     * 门店id
     */
    private String storeId;
    /**
     * 门店名称
     */
    private String storeName;
    /**
     * 目标年份
     */
    private Integer achievementYear;
    /**
     * 修改时间
     */
    private Date editTime;
    /**
     * 年业绩目标
     */
    private BigDecimal yearAchievementTarget;
    /**
     * 门店编号
     */
    private String storeNum;

    /**
     * 所属区域
     */
    private String regionName;

    /**
     * 所属区域id
     */
    private Long regionId;
    /**
     * 详细记录
     */
    List<AchievementTargetDetailDTO> targetDetail;
}
