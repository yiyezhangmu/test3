package com.coolcollege.intelligent.model.elasticSearch.response;

import com.coolcollege.intelligent.model.elasticSearch.annotation.GroupKey;
import lombok.Data;

/**
 * @author zhangchenbiao
 * @FileName: StoreQuestionRankDTO
 * @Description:
 * @date 2021-10-26 17:02
 */
@Data
public class StoreQuestionRankDTO {

    /**
     * 门店
     */
    @GroupKey
    private String storeId;

    /**
     * 总工单数
     */
    private Integer totalNum;

    /**
     * 解决问题数
     */
    private Integer finishQuestionNum;

}
