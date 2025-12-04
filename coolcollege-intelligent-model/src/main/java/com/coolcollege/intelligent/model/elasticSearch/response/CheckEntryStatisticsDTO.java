package com.coolcollege.intelligent.model.elasticSearch.response;

import lombok.Data;

/**
 * @author zhangchenbiao
 * @FileName: CheckEntryStatisticsDTO
 * @Description: 检查项统计
 * @date 2021-11-17 14:26
 */
@Data
public class CheckEntryStatisticsDTO extends RegionStoreBaseDTO{

    private Integer checkStoreNum;

    private Integer checkNum;

    private Integer questionNum;

    private Integer qualifiedNum;

    private Integer todoQuestionNum;

    private Integer unRecheckQuestionNum;

    private Integer finishQuestionNum;

}
