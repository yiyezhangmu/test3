package com.coolcollege.intelligent.model.achievement.vo;

import com.coolcollege.intelligent.model.achievement.dto.AchievementFormworkMappingDTO;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 业绩模板
 *
 * @author chenyupeng
 * @since 2021/10/25
 */
@Data
public class AchievementFormworkVO {

    /**
     * id
     */
    private Long id;

    /**
     * 业绩模板名称
     */
    private String name;

    /**
     * 业绩模板类型
     */
    private String type;

    /**
     * 修改人名称
     */
    private String updateName;

    /**
     * 修改时间
     */
    private Date editTime;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 业绩类型集合
     */
    private List<AchievementFormworkMappingDTO> typeList;
}
