package com.coolcollege.intelligent.dao.aianalysis;

import com.coolcollege.intelligent.model.aianalysis.AiAnalysisPictureDO;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

/**
 * @author zhangchenbiao
 * @date 2025-06-30 05:04
 */
public interface AiAnalysisPictureMapper {
    /**
     *
     * 默认插入方法，只会给有值的字段赋值
	 * 会对传进来的字段做判空处理，如果字段为空，则使用数据库默认字段或者null
     * dateTime:2025-06-30 05:04
     */
    int insertSelective(@Param("record") AiAnalysisPictureDO record, @Param("enterpriseId") String enterpriseId);

    /**
     *
     * 默认更新方法，根据主键更新，不会把null值更新到数据库，避免覆盖之前有值的
     * dateTime:2025-06-30 05:04
     */
    int updateByPrimaryKeySelective(@Param("record") AiAnalysisPictureDO record, @Param("enterpriseId") String enterpriseId);

    /**
     * 批量新增
     */
    int insertBatch(@Param("enterpriseId") String enterpriseId, @Param("list") List<AiAnalysisPictureDO> list);

    /**
     * 根据AI分析规则Id及日期查询抓图
     */
    List<AiAnalysisPictureDO> getByRuleIdAndDate(@Param("enterpriseId") String enterpriseId, @Param("ruleId") Long ruleId, @Param("date") LocalDate date);

    /**
     * 根据规则Id、日期、门店id查询
     * @param enterpriseId 企业id
     * @param ruleId 规则id
     * @param date 生成日期
     * @param storeId 门店id
     * @return AI分析报告图片列表
     */
    List<AiAnalysisPictureDO> getList(@Param("enterpriseId") String enterpriseId,
                                      @Param("ruleId") Long ruleId,
                                      @Param("date") LocalDate date,
                                      @Param("storeId") String storeId);

    /**
     * 根据规则、日期删除图片
     * @param enterpriseId 企业id
     * @param ruleIds 规则id列表
     * @param date 日期
     */
    int deleteByRuleIds(@Param("enterpriseId") String enterpriseId, @Param("ruleIds") List<Long> ruleIds, @Param("date") LocalDate date);
}