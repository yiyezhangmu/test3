package com.coolcollege.intelligent.model.operationboard.dto;

import com.coolcollege.intelligent.model.metatable.TbMetaTableDO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author yezhe
 * @date 2021-01-08 16:13
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TableBoardStatisticDTO {

    /** 方案总数 */
    private int tableNum;
    /** 检查项总数 */
    private int columnNum;
    /** 总门店数 */
    private int enterpriseStoreNum;
    /** 检查次数 */
    private int patrolNum;
    /** 检查门店数 */
    private int patrolStoreNum;
    /** 创建问题数 */
    private int totalQuestionNum;

    private List<TbMetaTableDO> defaultTableList;
}
