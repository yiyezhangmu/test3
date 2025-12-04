package com.coolcollege.intelligent.dao.aianalysis.dao;

import com.coolcollege.intelligent.dao.aianalysis.AiAnalysisPictureMapper;
import com.coolcollege.intelligent.model.aianalysis.AiAnalysisPictureDO;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 * AI分析图片DAO
 * </p>
 *
 * @author wangff
 * @since 2025/7/1
 */
@RequiredArgsConstructor
@Repository
public class AiAnalysisPictureDAO {
    private final AiAnalysisPictureMapper aiAnalysisPictureMapper;

    /**
     * 批量新增
     */
    public boolean insertBatch(String enterpriseId, List<AiAnalysisPictureDO> list) {
        if (CollectionUtils.isEmpty(list)) {
            return false;
        }
        return aiAnalysisPictureMapper.insertBatch(enterpriseId, list) > 0;
    }

    /**
     * 根据AI分析规则Id及日期查询抓图
     */
    public List<AiAnalysisPictureDO> getByRuleIdAndDate(String enterpriseId, Long ruleId, LocalDate date) {
        return aiAnalysisPictureMapper.getByRuleIdAndDate(enterpriseId, ruleId, date);
    }

    /**
     * 根据规则Id、日期、门店id查询
     * @param enterpriseId 企业id
     * @param ruleId 规则id
     * @param date 生成日期
     * @param storeId 门店id
     * @return AI分析报告图片列表
     */
    public List<AiAnalysisPictureDO> getList(String enterpriseId, Long ruleId, LocalDate date, String storeId) {
        if (Objects.isNull(ruleId) || Objects.isNull(date) || Objects.isNull(storeId)) {
            throw new IllegalArgumentException("必填参数不能为空");
        }
        return aiAnalysisPictureMapper.getList(enterpriseId, ruleId, date, storeId);
    }

    /**
     * 根据规则、日期删除图片
     * @param enterpriseId 企业id
     * @param ruleIds 规则id列表
     * @param date 日期
     * @return java.lang.Boolean
     */
    public Boolean removeByRuleIds(String enterpriseId, List<Long> ruleIds, LocalDate date) {
        if (CollectionUtils.isEmpty(ruleIds)) {
            return false;
        }
        return aiAnalysisPictureMapper.deleteByRuleIds(enterpriseId, ruleIds, date) > 0;
    }
}
