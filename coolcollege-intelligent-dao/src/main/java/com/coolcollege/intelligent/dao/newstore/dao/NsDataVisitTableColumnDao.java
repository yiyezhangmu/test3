package com.coolcollege.intelligent.dao.newstore.dao;

import com.coolcollege.intelligent.dao.newstore.NsDataVisitTableColumnMapper;
import com.coolcollege.intelligent.model.newstore.NsDataVisitTableColumnDO;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * @author zhangnan
 * @description: 拜访表数据检查项
 * @date 2022/3/6 9:31 PM
 */
@Repository
public class NsDataVisitTableColumnDao {

    @Resource
    private NsDataVisitTableColumnMapper nsDataVisitTableColumnMapper;

    /**
     * 批量新增
     * @param enterpriseId 企业id
     * @param columnDOList List<NsDataVisitTableColumnDO>
     */
    public void batchInsert(String enterpriseId, List<NsDataVisitTableColumnDO> columnDOList) {
        if(StringUtils.isBlank(enterpriseId) || CollectionUtils.isEmpty(columnDOList)) {
            return;
        }
        nsDataVisitTableColumnMapper.batchInsert(enterpriseId, columnDOList);
    }

    /**
     * 根据拜访记录id删除
     * @param enterpriseId 企业id
     * @param recordId 拜访记录id
     */
    public void deleteByRecordId(String enterpriseId, Long recordId) {
        if(StringUtils.isBlank(enterpriseId) || Objects.isNull(recordId)) {
            return;
        }
        nsDataVisitTableColumnMapper.deleteByRecordId(enterpriseId, recordId);
    }

    /**
     * 根据拜访记录id查询
     * @param enterpriseId 企业id
     * @param recordId 拜访记录id
     * @return List<NsDataVisitTableColumnDO>
     */
    public List<NsDataVisitTableColumnDO> selectByRecordId(String enterpriseId, Long recordId) {
        if(StringUtils.isBlank(enterpriseId) || Objects.isNull(recordId)) {
            return Lists.newArrayList();
        }
        return nsDataVisitTableColumnMapper.selectByRecordId(enterpriseId, recordId);
    }

    /**
     * 批量更新数据
     * @param enterpriseId 企业id
     * @param updateDOList List<NsDataVisitTableColumnDO>
     */
    public void batchUpdateValue(String enterpriseId, List<NsDataVisitTableColumnDO> updateDOList) {
        if(StringUtils.isBlank(enterpriseId) || CollectionUtils.isEmpty(updateDOList)) {
            return;
        }
        nsDataVisitTableColumnMapper.batchUpdateValue(enterpriseId, updateDOList);
    }

    /**
     * 根据拜访记录id更新拜访状态
     * @param enterpriseId 企业id
     * @param recordId 拜访记录id
     * @param recordStatus 拜访记录状态
     */
    public void updateRecordStatusByRecordId(String enterpriseId, Long recordId, String recordStatus) {
        if(StringUtils.isBlank(enterpriseId) || Objects.isNull(recordId) || StringUtils.isBlank(recordStatus)) {
            return;
        }
        nsDataVisitTableColumnMapper.updateRecordStatusByRecordId(enterpriseId, recordId, recordStatus);
    }
}
