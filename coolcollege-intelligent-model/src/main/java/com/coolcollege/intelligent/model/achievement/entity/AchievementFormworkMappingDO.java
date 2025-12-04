package com.coolcollege.intelligent.model.achievement.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author chenyupeng
 * @since 2021/10/26
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AchievementFormworkMappingDO {

    /**
     * ID
     */
    private Long id;
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

    public AchievementFormworkMappingDO(Long formworkId, Long typeId, Integer status) {
        this.formworkId = formworkId;
        this.typeId = typeId;
        this.status = status;
    }
}
