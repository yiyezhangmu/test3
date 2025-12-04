package com.coolcollege.intelligent.service.safetycheck.impl;

import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.dao.patrolstore.TbDataTableMapper;
import com.coolcollege.intelligent.dao.patrolstore.TbPatrolStoreRecordMapper;
import com.coolcollege.intelligent.dao.safetycheck.dao.ScSafetyCheckUpcomingDao;
import com.coolcollege.intelligent.model.patrolstore.TbDataTableDO;
import com.coolcollege.intelligent.model.patrolstore.TbPatrolStoreRecordDO;
import com.coolcollege.intelligent.model.safetycheck.ScSafetyCheckUpcomingDO;
import com.coolcollege.intelligent.model.safetycheck.vo.ScSafetyCheckUpcomingVO;
import com.coolcollege.intelligent.service.safetycheck.SafetyCheckUpcomingService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.coolcollege.intelligent.model.metatable.MetaTableConstant.BusinessTypeConstant.PATROL_STORE;

/**
 * @author byd
 * @date 2023-08-17 14:25
 */
@Service
public class SafetyCheckUpcomingServiceImpl implements SafetyCheckUpcomingService {

    @Resource
    private ScSafetyCheckUpcomingDao scSafetyCheckUpcomingDao;

    @Resource
    private TbPatrolStoreRecordMapper patrolStoreRecordMapper;

    @Resource
    private TbDataTableMapper dataTableMapper;

    @Override
    public PageInfo<ScSafetyCheckUpcomingVO> safetyCheckUpcomingList(String eid, String userId, List<String> storeIdList, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<ScSafetyCheckUpcomingDO> scSafetyCheckUpcomingDOList = scSafetyCheckUpcomingDao.totoList(eid, userId, storeIdList);
        PageInfo pageInfo = new PageInfo<>(scSafetyCheckUpcomingDOList);
        if (CollectionUtils.isEmpty(scSafetyCheckUpcomingDOList)) {
            return pageInfo;
        }
        List<ScSafetyCheckUpcomingVO> resultList = new ArrayList<>();
        List<Long> businessIdList = scSafetyCheckUpcomingDOList.stream().map(ScSafetyCheckUpcomingDO::getBusinessId).collect(Collectors.toList());
        List<TbPatrolStoreRecordDO> recordDOList = patrolStoreRecordMapper.selectByIds(eid, businessIdList);

        List<TbDataTableDO> dataTableList = dataTableMapper.getListByBusinessIdList(eid, businessIdList, PATROL_STORE);
        Map<Long, String> dataTableNameMap = dataTableList.stream().collect(Collectors.groupingBy(TbDataTableDO::getBusinessId,
                Collectors.mapping(TbDataTableDO::getTableName, Collectors.joining(Constants.COMMA))));

        Map<Long, TbPatrolStoreRecordDO> recordMap = ListUtils.emptyIfNull(recordDOList).stream().collect(Collectors.toMap(TbPatrolStoreRecordDO::getId, v -> v));
        scSafetyCheckUpcomingDOList.forEach(scSafetyCheckUpcomingDO -> {
            ScSafetyCheckUpcomingVO safetyCheckUpcomingVO = new ScSafetyCheckUpcomingVO();
            safetyCheckUpcomingVO.setId(scSafetyCheckUpcomingDO.getId());
            safetyCheckUpcomingVO.setBusinessId(scSafetyCheckUpcomingDO.getBusinessId());
            safetyCheckUpcomingVO.setUserId(scSafetyCheckUpcomingDO.getUserId());
            safetyCheckUpcomingVO.setNodeNo(scSafetyCheckUpcomingDO.getNodeNo());
            safetyCheckUpcomingVO.setStatus(scSafetyCheckUpcomingDO.getStatus());
            safetyCheckUpcomingVO.setCycleCount(scSafetyCheckUpcomingDO.getCycleCount());
            TbPatrolStoreRecordDO patrolStoreRecordDO = recordMap.get(scSafetyCheckUpcomingDO.getBusinessId());
            safetyCheckUpcomingVO.setSignEndTime(patrolStoreRecordDO.getSignEndTime());
            safetyCheckUpcomingVO.setStoreId(patrolStoreRecordDO.getStoreId());
            safetyCheckUpcomingVO.setStoreName(patrolStoreRecordDO.getStoreName());
            safetyCheckUpcomingVO.setTableName(dataTableNameMap.get(scSafetyCheckUpcomingDO.getBusinessId()));
            safetyCheckUpcomingVO.setSupervisorId(patrolStoreRecordDO.getSupervisorId());
            safetyCheckUpcomingVO.setSupervisorName(patrolStoreRecordDO.getSupervisorName());
            resultList.add(safetyCheckUpcomingVO);
        });
        pageInfo.setList(resultList);
        return pageInfo;
    }
}
