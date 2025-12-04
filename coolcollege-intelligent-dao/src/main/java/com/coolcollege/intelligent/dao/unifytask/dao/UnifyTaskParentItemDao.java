package com.coolcollege.intelligent.dao.unifytask.dao;

import com.coolcollege.intelligent.dao.unifytask.UnifyTaskParentItemMapper;
import com.coolcollege.intelligent.model.unifytask.UnifyTaskParentItemDO;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author byd
 * @date 2022-08-04 14:13
 */
@Repository
public class UnifyTaskParentItemDao {

    @Resource
    private UnifyTaskParentItemMapper unifyTaskParentItemMapper;

    public List<UnifyTaskParentItemDO> list(String eid, Long unifyTaskId) {
        return unifyTaskParentItemMapper.list(eid, unifyTaskId);
    }

    public void deleteByUnifyTaskId(String eid, Long unifyTaskId) {
        unifyTaskParentItemMapper.deleteByUnifyTaskId(eid, unifyTaskId);
    }

    public void insertSelective(String eid, UnifyTaskParentItemDO record) {
        unifyTaskParentItemMapper.insertSelective(eid, record);
    }

    public UnifyTaskParentItemDO selectByPrimaryKey(String eid, Long id) {
        return unifyTaskParentItemMapper.selectByPrimaryKey(id, eid);
    }


    public void updateByPrimaryKeySelective(String eid, UnifyTaskParentItemDO record) {
        unifyTaskParentItemMapper.updateByPrimaryKeySelective(record, eid);
    }

    public void deleteByUnifyTaskIdAndStoreIdAndLoopCount(String eid, Long unifyTaskId, String storeId, Long loopCount) {
        unifyTaskParentItemMapper.deleteByUnifyTaskIdAndStoreIdAndLoopCount(eid, unifyTaskId, storeId, loopCount);
    }

    public UnifyTaskParentItemDO getByUnifyTaskIdAndStoreIdAndLoopCount(String eid, Long unifyTaskId, String storeId, Long loopCount) {
        return unifyTaskParentItemMapper.getByUnifyTaskIdAndStoreIdAndLoopCount(eid, unifyTaskId, storeId, loopCount);
    }
}
