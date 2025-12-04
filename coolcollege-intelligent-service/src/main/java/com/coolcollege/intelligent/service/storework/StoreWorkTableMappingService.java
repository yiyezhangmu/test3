package com.coolcollege.intelligent.service.storework;

import com.coolcollege.intelligent.model.metatable.TbMetaStaTableColumnDO;
import com.coolcollege.intelligent.model.storework.SwStoreWorkDO;
import com.coolcollege.intelligent.model.storework.SwStoreWorkTableMappingDO;
import com.coolcollege.intelligent.model.storework.request.StoreWorkDutyInfoRequest;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

/**
 * @Author wxp
 * @Date 2022/9/14 9:39
 * @Version 1.0
 */
public interface StoreWorkTableMappingService {

    /**
     * 插入执行要求、执行人、更新检查项执行要求
     * @param enterpriseId
     * @param dutyInfoList
     */
    Pair<List<SwStoreWorkTableMappingDO>, List<TbMetaStaTableColumnDO>> insertDutyInfoList(String enterpriseId, List<StoreWorkDutyInfoRequest> dutyInfoList, SwStoreWorkDO storeWorkDO, String createUserId);

    List<SwStoreWorkTableMappingDO> listByStoreWorkId(String enterpriseId, Long storeWorkId);
}
