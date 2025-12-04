package com.coolcollege.intelligent.model.elasticSearch.response;

import com.coolcollege.intelligent.model.elasticSearch.annotation.GroupKey;
import lombok.Data;

/**
 * @author zhangchenbiao
 * @FileName: PatrolTypeTaskCountDTO
 * @Description: 任务情况
 * @date 2021-10-26 17:27
 */
@Data
public class PatrolTypeUnFinishTaskCountDTO {

    /**
     * 巡店类型 （线上巡店 线上巡店 定时巡检）
     */
    @GroupKey
    private String patrolType;

    /**
     * 未逾期数量
     */
    private Integer unExpireTaskNum;

    /**
     * 逾期数量
     */
    private Integer expireTaskNum;
}
