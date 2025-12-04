package com.coolcollege.intelligent.model.patrolstore.vo;

import com.coolcollege.intelligent.model.patrolstore.TbDataStaTableColumnDO;
import com.coolcollege.intelligent.model.patrolstore.TbPatrolStoreRecordDO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecordByMetaStaColumnIdVO {
    /**
     * 巡店记录
     */
    private TbDataStaTableColumnDO column;

    /**
     * 巡店记录
     */
    private TbPatrolStoreRecordDO record;

}
