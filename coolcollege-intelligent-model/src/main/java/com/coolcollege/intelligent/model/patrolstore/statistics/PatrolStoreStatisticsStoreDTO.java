package com.coolcollege.intelligent.model.patrolstore.statistics;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.*;

/**
 * 门店相关统计
 * @author jeffrey
 * @date 2020/12/10
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PatrolStoreStatisticsStoreDTO extends BaseQuestionStatisticsDTO {

    private static final long serialVersionUID = 1L;

    /** 门店ID */
    private String storeId;
    /** 门店名称 */
    @Excel(name = "门店名称")
    private String storeName;
    /** 巡店次数 */
    @Excel(name = "巡店次数")
    private int patrolNum;
    /** 巡店总人数 */
    @Excel(name = "巡店总人数")
    private int patrolPersonNum;

}
