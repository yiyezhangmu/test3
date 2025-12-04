package com.coolcollege.intelligent.dao.patrolstore.dao;

import com.coolcollege.intelligent.dao.patrolstore.TbPatrolPlanDealHistoryMapper;
import com.coolcollege.intelligent.model.patrolstore.entity.TbPatrolPlanDealHistoryDO;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author zhangchenbiao
 * @FileName: TbPatrolStoreHistoryDAO
 * @Description:
 * @date 2024-09-05 17:40
 */
@Repository
public class TbPatrolPlanDealHistoryDAO {

    @Resource
    private TbPatrolPlanDealHistoryMapper tbPatrolPlanDealHistoryMapper;

    public void addDealHistory(String enterpriseId, Long planId, String handleUserId, String nodeName, String remark, Integer status) {
        TbPatrolPlanDealHistoryDO dealHistory = TbPatrolPlanDealHistoryDO.builder()
                .planId(planId)
                .handleUserId(handleUserId)
                .nodeName(nodeName)
                .remark(remark)
                .status(status)
                .createTime(new Date())
                .createUserId(handleUserId)
                .build();
        tbPatrolPlanDealHistoryMapper.insertSelective(dealHistory, enterpriseId);
    }

    /**
     * 获取处理记录列表
     * @param enterpriseId
     * @param planId
     * @return
     */
    public List<TbPatrolPlanDealHistoryDO> getProcessHistoryList(String enterpriseId, Long planId) {
        if(StringUtils.isBlank(enterpriseId) || Objects.isNull(planId)){
            return Lists.newArrayList();
        }
        return tbPatrolPlanDealHistoryMapper.getProcessHistoryList(enterpriseId, planId);
    }

}
