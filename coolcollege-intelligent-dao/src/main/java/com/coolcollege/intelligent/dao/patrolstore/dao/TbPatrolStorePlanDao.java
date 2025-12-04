package com.coolcollege.intelligent.dao.patrolstore.dao;

import com.coolcollege.intelligent.dao.patrolstore.TbPatrolStorePlanMapper;
import com.coolcollege.intelligent.model.patrolstore.TbPatrolStorePlanDO;
import com.coolcollege.intelligent.model.patrolstore.dto.TbPatrolStoreCountDTO;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author byd
 * @date 2023-07-12 10:28
 */
@Repository
public class TbPatrolStorePlanDao {

    @Resource
    private TbPatrolStorePlanMapper patrolStorePlanMapper;

    public List<TbPatrolStorePlanDO> getPlanList(String eid, String userId, String planDate) {
        return patrolStorePlanMapper.getPlanList(eid, userId, planDate);
    }

    public TbPatrolStoreCountDTO getPlanCount(String eid, String userId, String planDateBegin, String planDateEnd) {
        return patrolStorePlanMapper.getPlanCount(eid, userId, planDateBegin, planDateEnd);
    }

    public List<TbPatrolStoreCountDTO> getPlanTimesCount(String eid, String userId, String planDateBegin, String planDateEnd, List<String> storeIdList) {
        return patrolStorePlanMapper.getPlanTimesCount(eid, userId, planDateBegin, planDateEnd, storeIdList);
    }

    public int insertSelective(String eid, TbPatrolStorePlanDO patrolStorePlanDO) {
        return patrolStorePlanMapper.insertSelective(patrolStorePlanDO, eid);
    }

    public int updateByPrimaryKeySelective(String eid, TbPatrolStorePlanDO patrolStorePlanDO) {
        return patrolStorePlanMapper.updateByPrimaryKeySelective(patrolStorePlanDO, eid);
    }

    public List<TbPatrolStoreCountDTO> getPlanPeopleTimesCount(String enterpriseId, String planDateBegin,
                                                               String planDateEnd,
                                                               List<String> supervisorIdList) {
        return patrolStorePlanMapper.getPlanPeopleTimesCount(enterpriseId, planDateBegin, planDateEnd, supervisorIdList);
    }

    public TbPatrolStorePlanDO getPlanByUserId(String eid, String userId, String planDate, String storeId) {
        return patrolStorePlanMapper.getPlanByUserId(eid, userId, planDate, storeId);
    }
}
