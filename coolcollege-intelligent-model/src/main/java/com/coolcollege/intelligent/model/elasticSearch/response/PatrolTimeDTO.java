package com.coolcollege.intelligent.model.elasticSearch.response;

import lombok.Data;

/**
 * @author zhangchenbiao
 * @FileName: PatrolRuleTimeDTO
 * @Description: 巡店规则时长
 * @date 2021-11-01 17:07
 */
@Data
public class PatrolTimeDTO extends RegionStoreBaseDTO{

    /**
     * 总的规则时长
     */
    private Long totalPatrolTime;
}
