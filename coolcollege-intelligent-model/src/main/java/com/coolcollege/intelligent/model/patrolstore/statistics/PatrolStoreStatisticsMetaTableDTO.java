package com.coolcollege.intelligent.model.patrolstore.statistics;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author yezhe
 * @date 2021/01/10
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatrolStoreStatisticsMetaTableDTO {

    private static final long serialVersionUID = 1L;
    /** 表ID */
    private Long metaTableId;
    /** 使用人数 */
    @Excel(name = "使用人数")
    private int usePersonNum;
    /** 巡店次数 */
    @Excel(name = "检查次数")
    private int patrolNum;
    /** 巡查门店数量 */
    @Excel(name = "检查门店数")
    private int patrolStoreNum;
}
