package com.coolcollege.intelligent.model.achievement.vo;

import lombok.Data;

import java.util.List;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2021/10/28
 */
@Data
public class AchievementFormworkDetailVO {

    private Long id;

    /**
     * 模板名称
     */
    private String formworkName;

    /**
     * 类型
     */
    private List<AchievementTypeDetailVO> typeDetailVOList;

}
