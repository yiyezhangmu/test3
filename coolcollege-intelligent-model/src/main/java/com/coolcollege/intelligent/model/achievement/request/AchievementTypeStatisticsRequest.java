package com.coolcollege.intelligent.model.achievement.request;

import lombok.Data;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2021/10/28
 */
@Data
public class AchievementTypeStatisticsRequest extends AchievementBaseRequest{

    /**
     * 门店ID
     */
    private String storeIdStr;

    /**
     * 区域Id
     */
    private Long regionId;

    /**
     * 是否展示当前
     */
    private Boolean showCurrent;

    /**
     * 门店名称
     */
    private String storeName;

    /**
     * 业绩模板Id
     */
    private Long achievementFormworkId;

    /**
     * 业绩类型id(逗号分隔)
     */
    private String achievementTypeIdStr;



    private Integer pageSize;

    private Integer pageNo;

}
