package com.coolcollege.intelligent.model.patrolstore.dto;

import com.coolcollege.intelligent.model.metatable.TbMetaStaTableColumnDO;

import com.coolcollege.intelligent.model.patrolstore.TbDataStaTableColumnDO;
import com.coolcollege.intelligent.model.storework.SwStoreWorkDataTableColumnDO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author yezhe
 * @date 2020-12-16 17:45
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StaColumnDTO {
    private TbDataStaTableColumnDO tbDataStaTableColumnDO;
    private TbMetaStaTableColumnDO tbMetaStaTableColumnDO;
    private SwStoreWorkDataTableColumnDO swStoreWorkDataTableColumnDODO;
}
