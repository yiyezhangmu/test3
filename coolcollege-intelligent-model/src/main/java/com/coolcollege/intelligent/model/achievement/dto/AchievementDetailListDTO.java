package com.coolcollege.intelligent.model.achievement.dto;

import com.coolcollege.intelligent.model.achievement.entity.AchievementDetailDO;
import com.coolcollege.intelligent.model.achievement.entity.AchievementTypeDO;
import lombok.Data;

/**
 * @author shuchang.wei
 * @date 2021/5/20 14:01
 */
@Data
public class AchievementDetailListDTO {
    /**
     * 业绩详情
     */
    private AchievementDetailDO achievementDetail;

    /**
     * 业绩分类
     */
    private AchievementTypeDO achievementTypeDO;
}
