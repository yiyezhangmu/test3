package com.coolcollege.intelligent.model.elasticSearch.response;

import lombok.Data;

import java.util.List;

/**
 * @author zhangchenbiao
 * @FileName: PatrolStatisticsGroupByPatrolTypeDTO
 * @Description: 巡店类型统计
 * @date 2021-10-26 10:27
 */
@Data
public class PatrolStatisticsGroupByPatrolTypeDTO extends RegionStoreBaseDTO{

    /**
     * 巡店类型
     */
    private List<PatrolTypeCountDTO> patrolTypeList;

}
