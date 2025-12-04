package com.coolcollege.intelligent.model.elasticSearch.response;

import lombok.Data;

/**
 * @author zhangchenbiao
 * @FileName: TaskStoreStatisticsQuestsionDTO
 * @Description:
 * @date 2021-10-25 19:32
 */
@Data
public class TaskStoreStatisticsQuestionDTO extends RegionStoreBaseDTO{

    /**
     * 总问题数
     */
    private Integer totalQuestionNum;

    /**
     * 待整改问题数
     */
    private Integer todoQuestionNum;

    /**
     * 待复检问题数
     */
    private Integer unRecheckQuestionNum;
    /**
     * 已经解决的问题数量
     */
    private Integer finishQuestionNum;
}
