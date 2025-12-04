package com.coolcollege.intelligent.dao.aianalysis.dao;

import cn.hutool.core.collection.CollStreamUtil;
import com.coolcollege.intelligent.dao.aianalysis.AiAnalysisReportMapper;
import com.coolcollege.intelligent.model.aianalysis.AiAnalysisReportDO;
import com.coolcollege.intelligent.model.aianalysis.vo.AiAnalysisReportSimpleVO;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * <p>
 * AI分析报告DAO
 * </p>
 *
 * @author wangff
 * @since 2025/7/2
 */
@Repository
@RequiredArgsConstructor
public class AiAnalysisReportDAO {
    private final AiAnalysisReportMapper aiAnalysisReportMapper;

    /**
     * 新增
     */
    public Integer insert(String enterpriseId, AiAnalysisReportDO aiAnalysisReportDO) {
        return aiAnalysisReportMapper.insertSelective(aiAnalysisReportDO, enterpriseId);
    }

    /**
     * 简单信息列表查询
     * @param enterpriseId 企业id
     * @param ids 报告id列表
     * @return AI分析报告列表
     */
    public List<AiAnalysisReportSimpleVO> getSimpleListByIds(String enterpriseId, List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return Collections.emptyList();
        }
        return aiAnalysisReportMapper.getSimpleListByIds(enterpriseId, ids);
    }

    /**
     * 根据id查询
     * @param enterpriseId 企业id
     * @param id id
     * @return AI分析报告
     */
    public AiAnalysisReportDO getById(String enterpriseId, Long id) {
        return aiAnalysisReportMapper.getById(enterpriseId, id);
    }

    /**
     * 查询已经发过报告的规则
     * @param enterpriseId 企业id
     * @param ruleIds 规则id列表
     * @return 规则id集合
     */
    public Set<Long> getExistRule(String enterpriseId, List<Long> ruleIds, LocalDate date) {
        if (CollectionUtils.isEmpty(ruleIds)) {
            return Collections.emptySet();
        }
        return new HashSet<>(aiAnalysisReportMapper.getExistRule(enterpriseId, ruleIds, date));
    }

    /**
     * 根据推送时间查询
     * @param enterpriseId 企业id
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 报告列表
     */
    public List<AiAnalysisReportDO> getListByPushTime(String enterpriseId, LocalDateTime startTime, LocalDateTime endTime) {
        return aiAnalysisReportMapper.getListByPushTime(enterpriseId, startTime, endTime);
    }

    /**
     * 根据规则id及报告日期查询报告id
     * @param enterpriseId 企业id
     * @param ruleIds 规则id列表
     * @param reportDate 报告分析日期
     * @return 报告id列表
     */
    public List<Long> getReportIdsByRuleIds(String enterpriseId, List<Long> ruleIds, LocalDate reportDate) {
        if (CollectionUtils.isEmpty(ruleIds)) {
            return Collections.emptyList();
        }
        List<AiAnalysisReportDO> reports = aiAnalysisReportMapper.getReportByRuleIds(enterpriseId, ruleIds, reportDate);
        return CollStreamUtil.toList(reports, AiAnalysisReportDO::getId);
    }

    /**
     * 根据规则id及报告日期查询报告id列表映射
     * @param enterpriseId 企业id
     * @param ruleIds 规则id列表
     * @param reportDate 报告分析日期
     * @return 规则id-报告id列表映射
     */
    public Map<Long, List<AiAnalysisReportDO>> getReportMapByRuleIds(String enterpriseId, List<Long> ruleIds, LocalDate reportDate) {
        if (CollectionUtils.isEmpty(ruleIds)) {
            return Collections.emptyMap();
        }
        List<AiAnalysisReportDO> reports = aiAnalysisReportMapper.getReportByRuleIds(enterpriseId, ruleIds, reportDate);
        return CollStreamUtil.groupByKey(reports, AiAnalysisReportDO::getRuleId);
    }

    /**
     * 根据规则id及报告日期删除
     * @param enterpriseId 企业id
     * @param ruleIds 规则id列表
     * @param reportDate 报告分析日期
     */
    public Boolean removeByRuleIds(String enterpriseId, List<Long> ruleIds, LocalDate reportDate) {
        if (CollectionUtils.isEmpty(ruleIds)) {
            return false;
        }
        return aiAnalysisReportMapper.deleteByRuleIds(enterpriseId, ruleIds, reportDate) > 0;
    }
}
