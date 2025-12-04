package com.coolcollege.intelligent.dao.aianalysis.dao;

import cn.hutool.core.collection.CollStreamUtil;
import com.coolcollege.intelligent.dao.aianalysis.AiAnalysisReportUserMappingMapper;
import com.coolcollege.intelligent.model.aianalysis.AiAnalysisReportUserMappingDO;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>
 * AI分析报告用户映射DAO
 * </p>
 *
 * @author wangff
 * @since 2025/7/3
 */
@Repository
@RequiredArgsConstructor
public class AiAnalysisReportUserMappingDAO {
    private final AiAnalysisReportUserMappingMapper aiAnalysisReportUserMappingMapper;

    /**
     * 批量新增
     */
    public boolean insertBatch(String enterpriseId, List<AiAnalysisReportUserMappingDO> list) {
        if (CollectionUtils.isEmpty(list)) {
            return false;
        }
        return aiAnalysisReportUserMappingMapper.insertBatch(enterpriseId, list) > 0;
    }

    /**
     * 查询用户可见的报告
     * @param enterpriseId 企业id
     * @param userId 用户id
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param storeIds 门店id列表
     * @return 报告id列表
     */
    public List<Long> getReportIdByUser(String enterpriseId, String userId, LocalDate startDate, LocalDate endDate, List<String> storeIds) {
        return aiAnalysisReportUserMappingMapper.getReportIdByUser(enterpriseId, userId, startDate, endDate, storeIds);
    }

    /**
     * 根据报告id查询用户id列表
     * @param enterpriseId 企业id
     * @param reportId 报告id
     * @return userId列表
     */
    public List<String> getUserIdsByReportId(String enterpriseId, Long reportId) {
        return aiAnalysisReportUserMappingMapper.getUserIdsByReportId(enterpriseId, reportId);
    }

    /**
     * 根据报告id获取用户id列表映射
     * @param enterpriseId 企业id
     * @param reportIds 报告id列表
     * @return 报告id-用户id列表映射
     */
    public Map<Long, Set<String>> getUserIdMapByReportIds(String enterpriseId, List<Long> reportIds) {
        if (CollectionUtils.isEmpty(reportIds)) {
            return Collections.emptyMap();
        }
        List<AiAnalysisReportUserMappingDO> reportUserMappings = aiAnalysisReportUserMappingMapper.getUserIdsByReportIds(enterpriseId, reportIds);
        return CollStreamUtil.groupBy(reportUserMappings, AiAnalysisReportUserMappingDO::getReportId, Collectors.mapping(AiAnalysisReportUserMappingDO::getUserId, Collectors.toSet()));
    }

    /**
     * 根据报告id删除人员映射
     * @param enterpriseId 企业id
     * @param reportIds 报告id列表
     * @return java.lang.Boolean
     */
    public Boolean removeByReportIds(String enterpriseId, List<Long> reportIds) {
        if (CollectionUtils.isEmpty(reportIds)) {
            return false;
        }
        return aiAnalysisReportUserMappingMapper.deleteByReportIds(enterpriseId, reportIds) > 0;
    }
}
