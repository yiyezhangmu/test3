package com.coolcollege.intelligent.model.elasticSearch.response;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

/**
 * @author zhangchenbiao
 * @FileName: PatrolStatisticsDataDTO
 * @Description: 巡店统计数据
 * @date 2021-10-25 11:06
 */
@Data
public class PatrolStatisticsDataDTO  extends RegionStoreBaseDTO{
    /**
     * 巡店覆盖门店数
     */
    private Integer patrolStoreNum;
    /**
     * 巡店次数
     */
    private Integer patrolNum;
    /**
     * 巡店人数
     */
    private Integer patrolPersonNum;
    /**
     * 任务巡店数
     */
    private Integer taskPatrolNum;

}
