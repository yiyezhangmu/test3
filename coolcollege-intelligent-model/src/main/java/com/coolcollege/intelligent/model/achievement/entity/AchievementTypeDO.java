package com.coolcollege.intelligent.model.achievement.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Description: 业绩分类DO
 * @Author: mao
 * @CreateDate: 2021/5/20
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class AchievementTypeDO {
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
     * 分类名称
     */
    private String name;
    /**
     * 是否锁定,0:未锁定，1：锁定
     */
    private Integer locked;

    /**
     * 是否删除标记
     */
    private Boolean deleted;

    public AchievementTypeDO(String name) {
        this.name = name;
    }
}
