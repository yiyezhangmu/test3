package com.coolcollege.intelligent.dao.safetycheck.dao;

import com.coolcollege.intelligent.dao.safetycheck.TbMetaQuickColumnAppealMapper;
import com.coolcollege.intelligent.model.metatable.dto.TbQuickColumnAppealDTO;
import com.coolcollege.intelligent.model.metatable.dto.TbQuickColumnReasonDTO;
import com.coolcollege.intelligent.model.safetycheck.TbMetaQuickColumnAppealDO;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wxp
 * @date 2022-09-08 02:01
 */
@Repository
public class TbMetaQuickColumnAppealDao {

    @Resource
    TbMetaQuickColumnAppealMapper tbMetaQuickColumnAppealMapper;
    /**
     *
     * 默认插入方法，只会给有值的字段赋值
	 * 会对传进来的字段做判空处理，如果字段为空，则使用数据库默认字段或者null
     * dateTime:2022-09-08 02:01
     */
    public int insertSelective(TbMetaQuickColumnAppealDO record, String enterpriseId){
        return tbMetaQuickColumnAppealMapper.insertSelective(record,enterpriseId);
    }

    /**
     *
     * 默认查询方法，通过主键获取所有字段的值
     * dateTime:2022-09-08 02:01
     */
    public TbMetaQuickColumnAppealDO selectByPrimaryKey(Long id, String enterpriseId){
        return tbMetaQuickColumnAppealMapper.selectByPrimaryKey(id, enterpriseId);
    }

    /**
     *
     * 默认更新方法，根据主键更新，不会把null值更新到数据库，避免覆盖之前有值的
     * dateTime:2022-09-08 02:01
     */
    public int updateByPrimaryKeySelective(TbMetaQuickColumnAppealDO record, String enterpriseId){
        return tbMetaQuickColumnAppealMapper.updateByPrimaryKeySelective(record,enterpriseId);
    }

    /**
     *
     * 默认更新方法，根据主键物理删除
     * dateTime:2022-09-08 02:01
     */
    public int deleteByPrimaryKey(Long id, String enterpriseId){
        return tbMetaQuickColumnAppealMapper.deleteByPrimaryKey(id,enterpriseId);
    }


    public void batchInsert(String enterpriseId, List<TbMetaQuickColumnAppealDO> list) {
        tbMetaQuickColumnAppealMapper.batchInsert(enterpriseId, list);
    }

    public List<TbQuickColumnAppealDTO> selectListByColumnId(String enterpriseId, Long columnId) {
        return tbMetaQuickColumnAppealMapper.selectListByColumnId(enterpriseId, columnId);
    }

    public void deleteByQuickColumnId(String eid, Long columnId) {
        tbMetaQuickColumnAppealMapper.deleteByQuickColumnId(eid, columnId);
    }

    public List<Long> getIdListByColumnId(String eid, Long columnId) {
        return tbMetaQuickColumnAppealMapper.selectIdListByColumnId(eid, columnId);
    }

    public void logicallyDeleteByIds(String enterpriseId, List<Long> ids) {
        tbMetaQuickColumnAppealMapper.logicallyDeleteByIds(enterpriseId, ids);
    }


    public List<TbQuickColumnAppealDTO> getListByColumnIdList(String eid, List<Long> columnIdList) {
        if(CollectionUtils.isEmpty(columnIdList)){
            return new ArrayList<>();
        }
        return tbMetaQuickColumnAppealMapper.getListByColumnIdList(eid, columnIdList);
    }
}