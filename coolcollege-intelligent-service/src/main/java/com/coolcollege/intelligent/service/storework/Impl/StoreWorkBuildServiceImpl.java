package com.coolcollege.intelligent.service.storework.Impl;

import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.dao.storework.dao.SwStoreWorkRangeDao;
import com.coolcollege.intelligent.dao.storework.dao.SwStoreWorkTableMappingDao;
import com.coolcollege.intelligent.model.storework.SwStoreWorkRangeDO;
import com.coolcollege.intelligent.model.storework.SwStoreWorkTableMappingDO;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author wxp
 * @Date 2022/10/24 15:22
 * @Version 1.0
 */
@Service
@Slf4j
public class StoreWorkBuildServiceImpl {

    @Resource
    SwStoreWorkRangeDao swStoreWorkRangeDao;
    @Resource
    SwStoreWorkTableMappingDao swStoreWorkTableMappingDao;

    @Transactional(rollbackFor = Exception.class)
    public void insertStoreWorkInfo(String enterpriseId, List<SwStoreWorkRangeDO> storeRangeList, List<SwStoreWorkTableMappingDO>  tableMappingDOList) {
        Lists.partition(storeRangeList, Constants.BATCH_INSERT_COUNT).forEach(partStoreList -> {
            swStoreWorkRangeDao.batchInsertStoreWorkRange(enterpriseId, partStoreList);
        });

        Lists.partition(tableMappingDOList, Constants.BATCH_INSERT_COUNT).forEach(partTableMappingList -> {
            swStoreWorkTableMappingDao.batchInsertOrUpdateStoreWorkTable(enterpriseId, partTableMappingList);
        });

    }

}
