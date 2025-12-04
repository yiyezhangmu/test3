package com.coolcollege.intelligent.dao.aianalysis;

import com.coolcollege.intelligent.model.aianalysis.entity.AiAnalysisCaptureTaskDO;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

/**
 * @author zhangchenbiao
 * @date 2025-07-02 09:15
 */
public interface AiAnalysisCaptureTaskMapper {
    /**
     *
     * 默认插入方法，只会给有值的字段赋值
	 * 会对传进来的字段做判空处理，如果字段为空，则使用数据库默认字段或者null
     * dateTime:2025-07-02 09:15
     */
    int insertSelective(@Param("record") AiAnalysisCaptureTaskDO record, @Param("enterpriseId") String enterpriseId);

    /**
     *
     * 默认更新方法，根据主键更新，不会把null值更新到数据库，避免覆盖之前有值的
     * dateTime:2025-07-02 09:15
     */
    int updateByPrimaryKeySelective(@Param("record") AiAnalysisCaptureTaskDO record, @Param("enterpriseId") String enterpriseId);

    /**
     * 批量新增
     */
    int insertBatch(@Param("enterpriseId") String enterpriseId, @Param("list") List<AiAnalysisCaptureTaskDO> list);

    /**
     * 批量编辑
     */
    int updateBatch(@Param("enterpriseId") String enterpriseId, @Param("list") List<AiAnalysisCaptureTaskDO> list);

    /**
     * 查询未出结果的抓图任务
     * @param enterpriseId 企业id
     * @param date 日期
     * @return 实体列表
     */
    List<AiAnalysisCaptureTaskDO> getNoResultListByDate(String enterpriseId, LocalDate date);

    /**
     * 查询已经发过抓图任务的规则
     * @param enterpriseId 企业id
     * @param ruleIds 规则id列表
     * @param generateDate 生成日期
     * @return 规则id集合
     */
    List<Long> getExistRule(@Param("enterpriseId") String enterpriseId, @Param("ruleIds") List<Long> ruleIds, @Param("generateDate") LocalDate generateDate);

    /**
     * 根据规则、日期删除任务
     * @param enterpriseId 企业id
     * @param ruleIds 规则id列表
     * @param date 日期
     */
    int deleteByRuleIds(@Param("enterpriseId") String enterpriseId, @Param("ruleIds") List<Long> ruleIds, @Param("date") LocalDate date);
}