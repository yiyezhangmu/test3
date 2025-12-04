package com.coolcollege.intelligent.model.achievement.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Description: 业绩目标表DO
 * @Author: mao
 * @CreateDate: 2021/5/20
 */
@Data
public class AchievementTargetDO {
    /**
     * ID
     */
    private Long id;
    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date editTime;
    /**
     * 创建人id
     */
    private String createUserId;
    /**
     * 创建人名称
     */
    private String createUserName;
    /**
     * 修改人id
     */
    private String updateUserId;
    /**
     * 修改人名称
     */
    private String updateUserName;
    /**
     * 门店id
     */
    private String storeId;
    /**
     * 门店名称
     */
    private String storeName;
    /**
     * 区域id
     */
    private Long regionId;
    /**
     * 区域路径
     */
    private String regionPath;
    /**
     * 年份
     */
    private Integer achievementYear;
    /**
     * 年业绩目标
     */
    private BigDecimal yearAchievementTarget;
    /**
     * 门店编号
     */
    private String storeNum;

}
