package com.coolcollege.intelligent.dao.inspection.dao;

import com.coolcollege.intelligent.dao.inspection.AiInspectionStrategiesMapper;
import com.coolcollege.intelligent.model.inspection.AiInspectionStrategiesDTO;
import com.coolcollege.intelligent.model.inspection.entity.AiInspectionStrategiesDO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * AI巡检策略表
 * </p>
 *
 * @author zhangchenbiao
 * @since 2025/9/25
 */
@RequiredArgsConstructor
@Repository
public class AiInspectionStrategiesDAO {
    private final AiInspectionStrategiesMapper aiInspectionStrategiesMapper;

    /**
     * 插入巡检策略记录
     * @param record 巡检策略实体
     * @param enterpriseId 企业ID
     * @return 影响行数
     */
    public int insertSelective(AiInspectionStrategiesDO record, String enterpriseId) {
        return aiInspectionStrategiesMapper.insertSelective(record, enterpriseId);
    }

    /**
     * 根据主键更新巡检策略记录
     * @param record 巡检策略实体
     * @param enterpriseId 企业ID
     * @return 影响行数
     */
    public int updateByPrimaryKeySelective(AiInspectionStrategiesDO record, String enterpriseId) {
        return aiInspectionStrategiesMapper.updateByPrimaryKeySelective(record, enterpriseId);
    }

    /**
     * 根据主键查询巡检策略记录
     * @param id 主键
     * @param enterpriseId 企业ID
     * @return 巡检策略实体
     */
    public AiInspectionStrategiesDO selectByPrimaryKey(Long id, String enterpriseId) {
        return aiInspectionStrategiesMapper.selectByPrimaryKey(id, enterpriseId);
    }

    /**
     * 根据主键删除巡检策略记录
     * @param id 主键
     * @param enterpriseId 企业ID
     * @return 影响行数
     */
    public int deleteByPrimaryKey(Long id, String enterpriseId) {
        return aiInspectionStrategiesMapper.deleteByPrimaryKey(id, enterpriseId);
    }

    /**
     * 根据条件查询巡检策略记录
     * @param query 查询条件
     * @param enterpriseId 企业ID
     * @return 巡检策略实体列表
     */
    public List<AiInspectionStrategiesDO> selectByQuery(AiInspectionStrategiesDTO query, String enterpriseId) {
        return aiInspectionStrategiesMapper.selectByQuery(query, enterpriseId);
    }
}