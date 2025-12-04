package com.coolcollege.intelligent.model.elasticSearch.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zhangchenbiao
 * @FileName: MetaTableStatisticsRequest
 * @Description: 检查表请求参数
 * @date 2021-11-17 14:35
 */
@Data
public class MetaTableStatisticsRequest extends RegionPatrolStatisticsRequest{

    /**
     * 检查表id
     */
    private Long metaTableId;

}
