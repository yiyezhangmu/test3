package com.coolcollege.intelligent.model.patrolstore.query;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 检查项基础详情
 * 
 * @author 叶哲
 * @date 2020/12/16
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatrolStoreStatisticsMetaTableQuery extends PatrolStoreStatisticsBaseQuery {
    /**
     * 检查表
     */
    private List<Long> metaTableIds;

}
