package com.coolcollege.intelligent.model.operationboard.query;

import java.util.List;

import com.coolcollege.intelligent.model.metatable.TbMetaTableDO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author yezhe
 * @date 2021-01-08 15:54
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TableBoardQuery extends BaseQuery{
    private Long metaTableId;
    private List<Long> storeIds;
    private Long regionId;

    private String regionPathLeft;

    private List<Long> metaTableIds;

    private List<TbMetaTableDO> defaultTables;
}
