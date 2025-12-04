package com.coolcollege.intelligent.dao.inspection.dao;

import com.coolcollege.intelligent.dao.inspection.AiInspectionStoreMappingMapper;
import com.coolcollege.intelligent.model.inspection.entity.AiInspectionStoreMappingDO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * AI巡检门店映射表
 * </p>
 *
 * @author zhangchenbiao
 * @since 2025/9/25
 */
@RequiredArgsConstructor
@Repository
public class AiInspectionStoreMappingDAO {
    private final AiInspectionStoreMappingMapper aiInspectionStoreMappingMapper;

    /**
     * 插入门店映射记录
     *
     * @param record       门店映射实体
     * @param enterpriseId 企业ID
     * @return 影响行数
     */
    public int insertSelective(AiInspectionStoreMappingDO record, String enterpriseId) {
        return aiInspectionStoreMappingMapper.insertSelective(record, enterpriseId);
    }

    /**
     * 根据主键更新门店映射记录
     *
     * @param record       门店映射实体
     * @param enterpriseId 企业ID
     * @return 影响行数
     */
    public int updateByPrimaryKeySelective(AiInspectionStoreMappingDO record, String enterpriseId) {
        return aiInspectionStoreMappingMapper.updateByPrimaryKeySelective(record, enterpriseId);
    }

    /**
     * 根据主键查询门店映射记录
     *
     * @param id           主键
     * @param enterpriseId 企业ID
     * @return 门店映射实体
     */
    public AiInspectionStoreMappingDO selectByPrimaryKey(Long id, String enterpriseId) {
        return aiInspectionStoreMappingMapper.selectByPrimaryKey(id, enterpriseId);
    }

    /**
     * 根据主键删除门店映射记录
     *
     * @param id           主键
     * @param enterpriseId 企业ID
     * @return 影响行数
     */
    public int deleteByPrimaryKey(Long id, String enterpriseId) {
        return aiInspectionStoreMappingMapper.deleteByPrimaryKey(id, enterpriseId);
    }

    /**
     * 根据巡检策略ID删除门店映射记录
     *
     * @param inspectionId 巡检策略ID
     * @param enterpriseId 企业ID
     * @return 影响行数
     */
    public int deleteByInspectionId(Long inspectionId, String enterpriseId) {
        return aiInspectionStoreMappingMapper.deleteByInspectionId(inspectionId, enterpriseId);
    }

    /**
     * 根据条件查询门店映射记录
     *
     * @param inspectionIdList 查询条件
     * @param enterpriseId 企业ID
     * @return 门店映射实体列表
     */
    public List<AiInspectionStoreMappingDO> selectByInspectionIdList(List<Long> inspectionIdList, String enterpriseId) {
        if (inspectionIdList == null || inspectionIdList.isEmpty()) {
            return new ArrayList<>();
        }
        return aiInspectionStoreMappingMapper.selectByInspectionIdList(inspectionIdList, enterpriseId);
    }
}