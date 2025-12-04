package com.coolcollege.intelligent.dao.workHandover.dao;

import com.coolcollege.intelligent.dao.workHandover.WorkHandoverMapper;
import com.coolcollege.intelligent.model.workHandover.WorkHandoverDO;
import com.coolcollege.intelligent.model.workHandover.vo.WorkHandoverVO;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author byd
 * @date 2022-11-17 13:53
 */
@Repository
public class WorkHandoverDao {

    @Resource
    private WorkHandoverMapper workHandoverMapper;

    public WorkHandoverDO selectById(Long workHandoverId) {
        return workHandoverMapper.selectByPrimaryKey(workHandoverId);
    }

    public void save(WorkHandoverDO record) {
        workHandoverMapper.insertSelective(record);
    }

    public List<WorkHandoverDO> selectList(String eid, String name) {
        return workHandoverMapper.selectList(eid, name);
    }

    public int updateById( WorkHandoverDO record) {
        return workHandoverMapper.updateByPrimaryKeySelective(record);
    }
}
