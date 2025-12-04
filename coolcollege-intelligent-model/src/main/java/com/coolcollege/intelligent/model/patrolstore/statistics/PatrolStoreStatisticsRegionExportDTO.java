package com.coolcollege.intelligent.model.patrolstore.statistics;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

/**tza
 * 区域相关统计
 *
 * @author byd
 * @date 2022/5/20
 */
@AllArgsConstructor
@NoArgsConstructor
public class PatrolStoreStatisticsRegionExportDTO extends PatrolStoreStatisticsRegionDTO{
    //表单巡店数
    @Excel(name = "表单巡店数", orderNum = "14")
    private int formPatrolNum;
}
