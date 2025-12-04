package com.coolcollege.intelligent.dao.patrolstore.dao;

import com.coolcollege.intelligent.dao.patrolstore.TbPatrolPlanMapper;
import com.coolcollege.intelligent.model.page.PageBaseRequest;
import com.coolcollege.intelligent.model.page.PageRequest;
import com.coolcollege.intelligent.model.patrolstore.entity.TbPatrolPlanDO;
import com.coolcollege.intelligent.model.patrolstore.request.PatrolPlanPageRequest;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @Author: huhu
 * @Date: 2024/9/4 16:48
 * @Description:
 */
@Repository
public class TbPatrolPlanDao {

    @Resource
    private TbPatrolPlanMapper tbPatrolPlanMapper;

    public int insertSelective(TbPatrolPlanDO tbPatrolPlanDO, String enterpriseId) {
        if (StringUtils.isBlank(enterpriseId)) {
            return 0;
        }
        return tbPatrolPlanMapper.insertSelective(tbPatrolPlanDO, enterpriseId);
    }

    public Page<TbPatrolPlanDO> getPatrolPlanList(String enterpriseId, PatrolPlanPageRequest param) {
        if (StringUtils.isBlank(enterpriseId)) {
            return new Page<>();
        }
        PageHelper.startPage(param.getPageNum(), param.getPageSize());
        return tbPatrolPlanMapper.getPatrolPlanList(enterpriseId, param);
    }

    public int updatePatrolPlan(TbPatrolPlanDO tbPatrolPlanDO, String enterpriseId) {
        if (StringUtils.isBlank(enterpriseId) || Objects.isNull(tbPatrolPlanDO) || Objects.isNull(tbPatrolPlanDO.getId())) {
            return 0;
        }
        return tbPatrolPlanMapper.updateByPrimaryKeySelective(tbPatrolPlanDO, enterpriseId);
    }

    public TbPatrolPlanDO selectById(Long id, String enterpriseId) {
        return tbPatrolPlanMapper.selectById(id, enterpriseId);
    }

    public int removePlan(String enterpriseId, String userId, Long id) {
        if (StringUtils.isBlank(enterpriseId) || StringUtils.isBlank(userId) || Objects.isNull(id)) {
            return 0;
        }
        TbPatrolPlanDO tbPatrolPlanDO = TbPatrolPlanDO.builder()
                .id(id).deleted(true)
                .updateUserId(userId).updateTime(new Date())
                .build();
        return tbPatrolPlanMapper.updateByPrimaryKeySelective(tbPatrolPlanDO, enterpriseId);
    }

    /**
     * 获取巡店计划待办
     * @param enterpriseId
     * @param auditUserId
     * @return
     */
    public Page<TbPatrolPlanDO> getPatrolPlanToDo(String enterpriseId, String auditUserId, PageRequest param){
        if(StringUtils.isAnyBlank(enterpriseId, auditUserId)){
            return new Page<>();
        }
        PageHelper.startPage(param.getPageNumber(), param.getPageSize());
        return tbPatrolPlanMapper.getPatrolPlanToDo(enterpriseId, auditUserId);
    }

    public Long getPatrolPlanCount(String enterpriseId, String userId) {
        return tbPatrolPlanMapper.getPatrolPlanCount(enterpriseId, userId);
    }

    public TbPatrolPlanDO getMyPatrolPlanMonthDetail(String enterpriseId, String userId, String planMonth) {
        return tbPatrolPlanMapper.getMyPatrolPlanMonthDetail(enterpriseId, userId, planMonth);
    }

    public List<TbPatrolPlanDO> selectByIds(String enterpriseId, List<Long> planIds) {
        if (StringUtils.isBlank(enterpriseId) || CollectionUtils.isEmpty(planIds)) {
            return Lists.newArrayList();
        }
        return tbPatrolPlanMapper.selectByIds(enterpriseId, planIds);
    }
}
