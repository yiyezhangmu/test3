package com.coolcollege.intelligent.service.patrolstore;

import com.coolcollege.intelligent.model.metatable.TbMetaTableDO;
import com.coolcollege.intelligent.model.patrolstore.CheckDataStaColumnDO;
import com.coolcollege.intelligent.model.patrolstore.TbPatrolStoreRecordDO;
import com.coolcollege.intelligent.model.patrolstore.param.PatrolStoreSubmitTableParam;

import java.util.List;

/**
 * @author byd
 * @date 2024-09-03 16:10
 */
public interface PatrolStoreCheckService {

    void patrolCheck(String enterpriseId, Long businessId, String userId);

    Boolean storeCheckSubmit(String enterpriseId, PatrolStoreSubmitTableParam param, String userId);

    void countCheckTableScore(String eid, TbPatrolStoreRecordDO recordDO, TbMetaTableDO tbMetaTable, Long dataTableId,
                              List<CheckDataStaColumnDO> dataStaTableColumnList);

    boolean canCheck(String enterpriseId, String userId, Integer checkType);
}
