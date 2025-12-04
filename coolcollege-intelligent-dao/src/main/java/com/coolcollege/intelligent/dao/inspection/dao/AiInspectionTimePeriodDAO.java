package com.coolcollege.intelligent.dao.inspection.dao;

import com.coolcollege.intelligent.dao.inspection.AiInspectionTimePeriodMapper;
import com.coolcollege.intelligent.model.inspection.entity.AiInspectionTimePeriodDO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * AI巡检抓拍时间段信息表
 * </p>
 *
 * @author zhangchenbiao
 * @since 2025/9/25
 */
@RequiredArgsConstructor
@Repository
public class AiInspectionTimePeriodDAO {
    private final AiInspectionTimePeriodMapper aiInspectionTimePeriodMapper;

    /**
     * 插入时间段记录
     * @param record 时间段实体
     * @param enterpriseId 企业ID
     * @return 影响行数
     */
    public int insertSelective(AiInspectionTimePeriodDO record, String enterpriseId) {
        return aiInspectionTimePeriodMapper.insertSelective(record, enterpriseId);
    }

    /**
     * 根据主键更新时间段记录
     * @param record 时间段实体
     * @param enterpriseId 企业ID
     * @return 影响行数
     */
    public int updateByPrimaryKeySelective(AiInspectionTimePeriodDO record, String enterpriseId) {
        return aiInspectionTimePeriodMapper.updateByPrimaryKeySelective(record, enterpriseId);
    }

    /**
     * 根据巡检策略ID删除时间段记录
     * @param inspectionId 巡检策略ID
     * @param enterpriseId 企业ID
     * @return 影响行数
     */
    public int deleteByInspectionId(Long inspectionId, String enterpriseId) {
        return aiInspectionTimePeriodMapper.deleteByInspectionId(inspectionId, enterpriseId);
    }

    public  List<AiInspectionTimePeriodDO> selectByInspectionIdList(List<Long> inspectionIdList, String enterpriseId){
        return aiInspectionTimePeriodMapper.selectByInspectionIdList(inspectionIdList, enterpriseId);
    }

    public int removeByInspectionId(Long inspectionId, String enterpriseId) {
        return aiInspectionTimePeriodMapper.removeByInspectionId(inspectionId, enterpriseId);
    }

}