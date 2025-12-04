package com.coolcollege.intelligent.model.achievement.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 业绩模板
 *
 * @author chenyupeng
 * @since 2021/10/25
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AchievementFormworkDO {

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
    private String createId;
    /**
     * 创建人名称
     */
    private String createName;
    /**
     * 修改人id
     */
    private String updateId;
    /**
     * 修改人名称
     */
    private String updateName;
    /**
     * 模板名称
     */
    private String name;
    /**
     * 模板类型 normal：通用；temp：临时
     */
    private String type;
    /**
     * 状态 -1：删除；0：冻结；1：正常；
     */
    private Integer status;

    public AchievementFormworkDO(String name, String type) {
        this.name = name;
        this.type = type;
    }
}
