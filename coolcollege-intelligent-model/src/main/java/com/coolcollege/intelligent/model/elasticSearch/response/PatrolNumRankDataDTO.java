package com.coolcollege.intelligent.model.elasticSearch.response;

import com.coolcollege.intelligent.model.elasticSearch.annotation.DocCount;
import com.coolcollege.intelligent.model.elasticSearch.annotation.GroupKey;
import lombok.Data;

/**
 * @author zhangchenbiao
 * @FileName: PatrolStatisticsDataDTO
 * @Description: 巡店人数排行
 * @date 2021-10-25 11:06
 */
@Data
public class PatrolNumRankDataDTO{
    /**
     * 用户id
     */
    @GroupKey
    private String userId;
    /**
     * 巡店次数
     */
    @DocCount
    private Integer patrolNum;

}
