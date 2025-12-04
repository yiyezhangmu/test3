package com.coolcollege.intelligent.model.elasticSearch.response;

import com.coolcollege.intelligent.model.elasticSearch.annotation.GroupKey;
import lombok.Data;

/**
 * @author zhangchenbiao
 * @FileName: PatrolTypeFinishTaskCountDTO
 * @Description: 完成任务统计
 * @date 2021-10-26 17:54
 */
@Data
public class PatrolTypeTaskCountDTO {

    @GroupKey
    private String patrolType;

    /**
     * 总数量
     */
    private Integer totalNum;

    /**
     * 按时完成数量
     */
    private Integer onTimeNum;

    /**
     * 任务总数
     */
    private Integer taskNum;

}
