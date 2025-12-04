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
public class AchievementFormworkMappingDTO {

    /**
     * 模板id
     */
    private Long formworkId;
    /**
     * 类型id
     */
    private Long typeId;
    /**
     * 状态 -1：删除；0：冻结；1：正常；
     */
    private Integer status;

    /**
     * 类型名称
     */
    private String typeName;
}
