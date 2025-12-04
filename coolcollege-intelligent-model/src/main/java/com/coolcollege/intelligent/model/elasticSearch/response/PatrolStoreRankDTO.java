package com.coolcollege.intelligent.model.elasticSearch.response;

import com.coolcollege.intelligent.model.elasticSearch.annotation.DocCount;
import com.coolcollege.intelligent.model.elasticSearch.annotation.GroupKey;
import lombok.Data;

/**
 * @author zhangchenbiao
 * @FileName: PatrolStoreRankDTO
 * @Description:门店排行
 * @date 2021-10-26 16:00
 */
@Data
public class PatrolStoreRankDTO {

    @GroupKey
    private String storeId;

    @DocCount
    private Integer count;

}
