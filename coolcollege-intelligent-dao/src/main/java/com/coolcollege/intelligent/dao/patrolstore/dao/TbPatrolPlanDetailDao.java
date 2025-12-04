package com.coolcollege.intelligent.dao.patrolstore.dao;

import com.coolcollege.intelligent.common.enums.patrol.PatrolPlanStatusEnum;
import com.coolcollege.intelligent.dao.patrolstore.TbPatrolPlanDetailMapper;
import com.coolcollege.intelligent.dao.patrolstore.TbPatrolPlanMapper;
import com.coolcollege.intelligent.model.patrolstore.entity.TbPatrolPlanDO;
import com.coolcollege.intelligent.model.patrolstore.entity.TbPatrolPlanDetailDO;
import com.coolcollege.intelligent.model.patrolstore.request.PatrolPlanPageRequest;
import com.coolcollege.intelligent.model.patrolstore.request.PatrolRecordTodoRequest;
import com.coolcollege.intelligent.model.patrolstore.vo.PatrolPlanDetailExportVO;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author: huhu
 * @Date: 2024/9/4 16:49
 * @Description:
 */
@Repository
public class TbPatrolPlanDetailDao {
    @Resource
    private TbPatrolPlanDetailMapper tbPatrolPlanDetailMapper;
    @Resource
    private TbPatrolPlanMapper tbPatrolPlanMapper;

    public int insertBatch(List<TbPatrolPlanDetailDO> list, String enterpriseId) {
        if (CollectionUtils.isEmpty(list) || StringUtils.isBlank(enterpriseId)) {
            return 0;
        }
        return tbPatrolPlanDetailMapper.insertBatch(list, enterpriseId);
    }

    public int updatePatrolPlanDetail(TbPatrolPlanDetailDO tbPatrolPlanDetailDO, String enterpriseId) {
        return tbPatrolPlanDetailMapper.updateByPrimaryKeySelective(tbPatrolPlanDetailDO, enterpriseId);
    }

    public List<TbPatrolPlanDetailDO> getByPlanId(Long planId, String enterpriseId) {
        if (Objects.isNull(planId) || StringUtils.isBlank(enterpriseId)) {
            return new ArrayList<>();
        }
        return tbPatrolPlanDetailMapper.getByPlanId(planId, enterpriseId);
    }

    public int removeDetail(List<Long> deleteIds,String userId, String enterpriseId) {
        if (CollectionUtils.isEmpty(deleteIds) || StringUtils.isBlank(userId) || StringUtils.isBlank(enterpriseId)) {
            return 0;
        }
        return tbPatrolPlanDetailMapper.removeDetail(deleteIds, enterpriseId, userId);
    }

    public int removeDetailByPlanId(String enterpriseId, String userId, Long planId) {
        if (StringUtils.isBlank(enterpriseId) || StringUtils.isBlank(userId) || Objects.isNull(planId)) {
            return 0;
        }
        return tbPatrolPlanDetailMapper.removeDetailByPlanId(enterpriseId, userId, planId);
    }

    /**
     * 获取某人门店最近巡店时间
     * @param enterpriseId
     * @param storeIds
     * @param metaTableId
     * @return
     */
    public Map<String, Date> getLatestPatrolTime(String enterpriseId, List<String> storeIds, Long metaTableId){
        if(CollectionUtils.isEmpty(storeIds) || StringUtils.isBlank(enterpriseId) || metaTableId == null){
            return new HashMap<>();
        }
        List<TbPatrolPlanDetailDO> latestPatrolTime = tbPatrolPlanDetailMapper.getLatestPatrolTime(enterpriseId, storeIds, metaTableId);
        return ListUtils.emptyIfNull(latestPatrolTime).stream().filter(t-> Objects.nonNull(t.getFinishTime())).collect(Collectors.toMap(k->k.getStoreId(), v->v.getFinishTime()));
    }

    /**
     * 批量更新
     * @param enterpriseId
     * @param updateList
     * @return
     */
    public Integer batchUpdate(String enterpriseId, List<TbPatrolPlanDetailDO> updateList) {
        if(CollectionUtils.isEmpty(updateList)){
            return 0;
        }
        return tbPatrolPlanDetailMapper.batchUpdate(enterpriseId, updateList);
    }

    /**
     * 更新完成时间及状态
     * @param enterpriseId
     * @param businessId
     * @return
     */
    public Integer updateFinishTimeAndStatus(String enterpriseId, Long businessId){
        if(StringUtils.isBlank(enterpriseId) || Objects.isNull(businessId)){
            return null;
        }
        tbPatrolPlanDetailMapper.updateFinishTimeAndStatus(enterpriseId, businessId);
        TbPatrolPlanDetailDO planDetail = tbPatrolPlanDetailMapper.getPlanDetailByBusinessId(enterpriseId, businessId);
        if(Objects.nonNull(planDetail)){
            Integer finishNum = tbPatrolPlanDetailMapper.getFinishNumByPlanId(enterpriseId, planDetail.getPlanId());
            TbPatrolPlanDO updatePlan = TbPatrolPlanDO.builder().id(planDetail.getPlanId()).patrolFinishStoreNum(finishNum).build();
            Integer total = tbPatrolPlanDetailMapper.getNumByPlanId(enterpriseId, planDetail.getPlanId());
            // 巡店数量与完成数量相等，更新行事历状态为已完成
            if (total.equals(finishNum)) {
                updatePlan.setAuditStatus(PatrolPlanStatusEnum.FINISHED.getCode());
            }
            tbPatrolPlanMapper.updateByPrimaryKeySelective(updatePlan, enterpriseId);
        }
        return null;
    }

    public Page<TbPatrolPlanDetailDO> getPatrolRecordToDo(String enterpriseId, PatrolRecordTodoRequest param) {
        if(StringUtils.isBlank(enterpriseId)){
            return new Page<>();
        }
        PageHelper.startPage(param.getPageNumber(), param.getPageSize());
        return tbPatrolPlanDetailMapper.getPatrolRecordToDo(enterpriseId, param);
    }

    public TbPatrolPlanDetailDO getPlanDetailByBusinessId(String enterpriseId, Long businessId) {
        if(StringUtils.isBlank(enterpriseId) || Objects.isNull(businessId)){
            return null;
        }
        return tbPatrolPlanDetailMapper.getPlanDetailByBusinessId(enterpriseId, businessId);
    }

    public long getPatrolPlanDetailCount(String enterpriseId, PatrolPlanPageRequest param) {
        return tbPatrolPlanDetailMapper.getPatrolPlanDetailCount(enterpriseId, param);
    }

    public List<PatrolPlanDetailExportVO> getPatrolPlanDetailExportList(String enterpriseId, PatrolPlanPageRequest param) {
        return tbPatrolPlanDetailMapper.getPatrolPlanDetailExportList(enterpriseId, param);
    }
}
