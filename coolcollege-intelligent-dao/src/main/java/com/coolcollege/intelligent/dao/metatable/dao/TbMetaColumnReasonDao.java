package com.coolcollege.intelligent.dao.metatable.dao;

import com.coolcollege.intelligent.dao.metatable.TbMetaColumnReasonMapper;
import com.coolcollege.intelligent.model.metatable.TbMetaColumnReasonDO;
import com.coolcollege.intelligent.model.metatable.dto.TbMetaColumnReasonDTO;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author byd
 * @date 2023-06-05 14:35
 */
@Repository
public class TbMetaColumnReasonDao {

    @Resource
    private TbMetaColumnReasonMapper tbMetaColumnReasonMapper;

    public List<TbMetaColumnReasonDTO> getListByColumnId(String eid, Long columnId) {
        return tbMetaColumnReasonMapper.selectListByColumnId(eid, columnId);
    }

    public List<TbMetaColumnReasonDTO> getListByMetaTableId(String eid, Long metaTableId) {
        return tbMetaColumnReasonMapper.getListByMetaTableId(eid, metaTableId);
    }

    public List<TbMetaColumnReasonDTO> getListByColumnIdList(String eid, List<Long> columnIdList) {
        if(CollectionUtils.isEmpty(columnIdList)){
            return new ArrayList<>();
        }
        return tbMetaColumnReasonMapper.getListByColumnIdList(eid, columnIdList);
    }

    public void batchInsert(String enterpriseId, List<TbMetaColumnReasonDO> list) {
        tbMetaColumnReasonMapper.batchInsert(enterpriseId, list);
    }

    public void deleteByMetaTableId(String eid, Long metaTableId) {
        tbMetaColumnReasonMapper.deleteByMetaTableId(eid, metaTableId);
    }

    public void updateByPrimaryKeySelective(String eid, TbMetaColumnReasonDO reasonDO) {
        tbMetaColumnReasonMapper.updateByPrimaryKeySelective(reasonDO, eid);
    }

    public void insertSelective(String eid, TbMetaColumnReasonDO reasonDO) {
        tbMetaColumnReasonMapper.insertSelective(reasonDO, eid);
    }

    public void logicallyDeleteByIds(String enterpriseId, List<Long> ids) {
        tbMetaColumnReasonMapper.logicallyDeleteByIds(enterpriseId, ids);
    }

    public void deleteByColumnId(String eid, Long columnId) {
        tbMetaColumnReasonMapper.deleteByColumnId(eid, columnId);
    }
}
