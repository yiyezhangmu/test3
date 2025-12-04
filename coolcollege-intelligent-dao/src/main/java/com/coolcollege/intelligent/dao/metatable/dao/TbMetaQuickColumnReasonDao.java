package com.coolcollege.intelligent.dao.metatable.dao;

import com.coolcollege.intelligent.dao.metatable.TbMetaQuickColumnReasonMapper;
import com.coolcollege.intelligent.model.metatable.TbMetaQuickColumnReasonDO;
import com.coolcollege.intelligent.model.metatable.dto.TbQuickColumnReasonDTO;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author byd
 * @date 2023-06-05 14:35
 */
@Repository
public class TbMetaQuickColumnReasonDao {

    @Resource
    private TbMetaQuickColumnReasonMapper metaQuickColumnReasonMapper;

    public List<TbQuickColumnReasonDTO> getListByColumnId(String eid, Long columnId) {
        return metaQuickColumnReasonMapper.selectListByColumnId(eid, columnId);
    }

    public void batchInsert(String enterpriseId, List<TbMetaQuickColumnReasonDO> list) {
        metaQuickColumnReasonMapper.batchInsert(enterpriseId, list);
    }

    public void updateByPrimaryKeySelective(String enterpriseId, TbMetaQuickColumnReasonDO record) {
        metaQuickColumnReasonMapper.updateByPrimaryKeySelective(record, enterpriseId);
    }

    public void deleteByQuickColumnId(String eid, Long columnId) {
        metaQuickColumnReasonMapper.deleteByQuickColumnId(eid, columnId);
    }

    public void logicallyDeleteByIds(String enterpriseId, List<Long> ids) {
        metaQuickColumnReasonMapper.logicallyDeleteByIds(enterpriseId, ids);
    }

    public List<Long> getIdListByColumnId(String eid, Long columnId) {
        return metaQuickColumnReasonMapper.selectIdListByColumnId(eid, columnId);
    }

    public List<TbQuickColumnReasonDTO> getListByColumnIdList(String eid, List<Long> columnIdList) {
        if(StringUtils.isBlank(eid) || CollectionUtils.isEmpty(columnIdList)){
            return new ArrayList<>();
        }
        return metaQuickColumnReasonMapper.getListByColumnIdList(eid, columnIdList);
    }
}
