package com.coolcollege.intelligent.dao.aianalysis.dao;

import com.coolcollege.intelligent.dao.aianalysis.AiAnalysisRuleMapper;
import com.coolcollege.intelligent.model.aianalysis.AiAnalysisRuleDO;
import com.coolcollege.intelligent.model.aianalysis.dto.AiAnalysisRuleQueryDTO;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * <p>
 * AI分析规则DAO
 * </p>
 *
 * @author wangff
 * @since 2025/7/1
 */
@RequiredArgsConstructor
@Repository
public class AiAnalysisRuleDAO {
    private final AiAnalysisRuleMapper aiAnalysisRuleMapper;

    /**
     * 新增
     */
    public Boolean insert(String enterpriseId, AiAnalysisRuleDO entity) {
        return aiAnalysisRuleMapper.insertSelective(entity, enterpriseId) > 0;
    }

    /**
     * 编辑
     */
    public Boolean update(String enterpriseId, AiAnalysisRuleDO entity) {
        return aiAnalysisRuleMapper.updateByPrimaryKeySelective(entity, enterpriseId) > 0;
    }

    /**
     * 批量删除
     */
    public Boolean deleteBatch(String enterpriseId, List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return false;
        }
        return aiAnalysisRuleMapper.deleteBatch(enterpriseId, ids) > 0;
    }

    /**
     * 列表查询
     */
    public List<AiAnalysisRuleDO> getList(String enterpriseId, AiAnalysisRuleQueryDTO query) {
        return aiAnalysisRuleMapper.getList(enterpriseId, query);
    }

    /**
     * 根据id查询
     */
    public AiAnalysisRuleDO getById(String enterpriseId, Long id) {
        return aiAnalysisRuleMapper.getById(enterpriseId, id);
    }

    /**
     * 查询日期在有效期内的AI规则
     */
    public List<AiAnalysisRuleDO> getListByPeriod(String enterpriseId, LocalDate time, List<Long> ruleIds) {
        return aiAnalysisRuleMapper.getListByPeriod(enterpriseId, time, ruleIds);
    }
}
