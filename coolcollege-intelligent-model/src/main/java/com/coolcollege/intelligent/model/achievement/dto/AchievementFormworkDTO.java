package com.coolcollege.intelligent.model.achievement.dto;

import lombok.Data;

import java.util.List;

/**
 * 业绩模板
 *
 * @author chenyupeng
 * @since 2021/10/25
 */
@Data
public class AchievementFormworkDTO {

    /**
     * 模板id
     */
    private Long id;
    /**
     * 模板名称
     */
    private String name;
    /**
     * 模板类型
     */
    private String type;
    /**
     * 模板状态
     */
    private Integer status;
    /**
     * 模板名称
     */
    private List<Long> achievementTypeIdList;
}
