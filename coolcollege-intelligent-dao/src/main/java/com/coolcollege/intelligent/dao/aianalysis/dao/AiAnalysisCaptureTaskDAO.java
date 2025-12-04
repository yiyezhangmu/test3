package com.coolcollege.intelligent.dao.aianalysis.dao;

import com.coolcollege.intelligent.dao.aianalysis.AiAnalysisCaptureTaskMapper;
import com.coolcollege.intelligent.model.aianalysis.entity.AiAnalysisCaptureTaskDO;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * <p>
 * AI分析抓图任务DAO
 * </p>
 *
 * @author wangff
 * @since 2025/7/2
 */
@RequiredArgsConstructor
@Repository
public class AiAnalysisCaptureTaskDAO {
    private final AiAnalysisCaptureTaskMapper aiAnalysisCaptureTaskMapper;

    /**
     * 批量新增
     * @param enterpriseId 企业id
     * @param list 实体列表
     * @return 是否成功
     */
    public boolean insertBatch(String enterpriseId, List<AiAnalysisCaptureTaskDO> list) {
        if (CollectionUtils.isEmpty(list)) {
            return false;
        }
        return aiAnalysisCaptureTaskMapper.insertBatch(enterpriseId, list) > 0;
    }

    /**
     * 批量编辑
     * @param enterpriseId 企业id
     * @param list 实体列表
     * @return 是否成功
     */
    public boolean updateBatch(String enterpriseId, List<AiAnalysisCaptureTaskDO> list) {
        if (CollectionUtils.isEmpty(list)) {
            return false;
        }
        return aiAnalysisCaptureTaskMapper.updateBatch(enterpriseId, list) > 0;
    }

    /**
     * 查询未出结果的抓图任务
     * @param enterpriseId 企业id
     * @param date 日期
     * @return 实体列表
     */
    public List<AiAnalysisCaptureTaskDO> getNoResultListByDate(String enterpriseId, LocalDate date) {
        return aiAnalysisCaptureTaskMapper.getNoResultListByDate(enterpriseId, date);
    }

    /**
     * 查询已经发过抓图任务的规则
     * @param enterpriseId 企业id
     * @param ruleIds 规则id列表
     * @param generateDate 生成日期
     * @return 规则id集合
     */
    public Set<Long> getExistRule(String enterpriseId, List<Long> ruleIds, LocalDate generateDate) {
        if (CollectionUtils.isEmpty(ruleIds)) {
            return Collections.emptySet();
        }
        return new HashSet<>(aiAnalysisCaptureTaskMapper.getExistRule(enterpriseId, ruleIds, generateDate));
    }

    /**
     * 根据规则、日期删除任务
     * @param enterpriseId 企业id
     * @param ruleIds 规则id列表
     * @param date 日期
     * @return java.lang.Boolean
     */
    public Boolean removeByRuleIds(String enterpriseId, List<Long> ruleIds, LocalDate date) {
        if (CollectionUtils.isEmpty(ruleIds)) {
            return false;
        }
        return aiAnalysisCaptureTaskMapper.deleteByRuleIds(enterpriseId, ruleIds, date) > 0;
    }
}
