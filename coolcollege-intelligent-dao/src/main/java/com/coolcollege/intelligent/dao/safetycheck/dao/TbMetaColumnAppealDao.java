package com.coolcollege.intelligent.dao.safetycheck.dao;

import com.coolcollege.intelligent.dao.safetycheck.TbMetaColumnAppealMapper;
import com.coolcollege.intelligent.model.safetycheck.TbMetaColumnAppealDO;
import com.coolcollege.intelligent.model.safetycheck.dto.TbMetaColumnAppealDTO;
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
public class TbMetaColumnAppealDao {

    @Resource
    TbMetaColumnAppealMapper tbMetaColumnAppealMapper;
    /**
     *
     * 默认插入方法，只会给有值的字段赋值
	 * 会对传进来的字段做判空处理，如果字段为空，则使用数据库默认字段或者null
     * dateTime:2022-09-08 02:01
     */
    public int insertSelective(TbMetaColumnAppealDO record, String enterpriseId){
        return tbMetaColumnAppealMapper.insertSelective(record,enterpriseId);
    }

    /**
     *
     * 默认查询方法，通过主键获取所有字段的值
     * dateTime:2022-09-08 02:01
     */
    public TbMetaColumnAppealDO selectByPrimaryKey(Long id, String enterpriseId){
        return tbMetaColumnAppealMapper.selectByPrimaryKey(id, enterpriseId);
    }

    /**
     *
     * 默认更新方法，根据主键更新，不会把null值更新到数据库，避免覆盖之前有值的
     * dateTime:2022-09-08 02:01
     */
    public int updateByPrimaryKeySelective(TbMetaColumnAppealDO record, String enterpriseId){
        return tbMetaColumnAppealMapper.updateByPrimaryKeySelective(record,enterpriseId);
    }

    /**
     *
     * 默认更新方法，根据主键物理删除
     * dateTime:2022-09-08 02:01
     */
    public int deleteByPrimaryKey(Long id, String enterpriseId){
        return tbMetaColumnAppealMapper.deleteByPrimaryKey(id,enterpriseId);
    }


    public List<TbMetaColumnAppealDTO> getListByMetaTableId(String eid, Long metaTableId) {
        return tbMetaColumnAppealMapper.getListByMetaTableId(eid, metaTableId);
    }


    public void batchInsert(String enterpriseId, List<TbMetaColumnAppealDO> list) {
        tbMetaColumnAppealMapper.batchInsert(enterpriseId, list);
    }

    public List<TbMetaColumnAppealDTO> getListByColumnId(String eid, Long staColumnId) {
        return tbMetaColumnAppealMapper.getListByColumnId(eid, staColumnId);
    }

    public List<TbMetaColumnAppealDTO> getListByColumnIdList(String eid, List<Long> columnIdList) {
        if(CollectionUtils.isEmpty(columnIdList)){
            return new ArrayList<>();
        }
        return tbMetaColumnAppealMapper.getListByColumnIdList(eid, columnIdList);
    }
}