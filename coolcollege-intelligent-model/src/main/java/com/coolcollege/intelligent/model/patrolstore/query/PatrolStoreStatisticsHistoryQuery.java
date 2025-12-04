package com.coolcollege.intelligent.model.patrolstore.query;

import java.util.Date;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 门店相关统计
 * 
 * @author jeffrey
 * @date 2020/12/10
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatrolStoreStatisticsHistoryQuery {
    /**
     * 区域id
     */
    @NotNull(message = "门店id不能为空")
    private String storeId;
    /**
     * 时间范围起始值
     */
    @NotNull(message = "开始时间不能为空")
    private Date beginDate;
    /**
     * 时间范围截至值
     */
    @NotNull(message = "结束时间不能为空")
    private Date endDate;

}
