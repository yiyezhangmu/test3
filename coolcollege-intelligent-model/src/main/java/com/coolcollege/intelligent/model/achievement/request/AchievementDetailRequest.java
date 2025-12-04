package com.coolcollege.intelligent.model.achievement.request;

import lombok.Data;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2021/10/27
 */
@Data
public class AchievementDetailRequest extends AchievementBaseRequest {

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
     * 业绩产生人userId（逗号分隔）
     */
    private String produceUserIdStr;

    /**
     * 业绩模板Id
     */
    private Long achievementFormworkId;

    /**
     * 业绩类型id(逗号分隔)
     */
    private String achievementTypeIdStr;

    /**
     * 是否查询空业绩产生人
     */
    private Boolean isNullProduceUser;

    private Integer pageSize;

    private Integer pageNo;



}
