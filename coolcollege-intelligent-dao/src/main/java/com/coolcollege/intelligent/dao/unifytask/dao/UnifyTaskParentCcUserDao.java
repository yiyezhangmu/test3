package com.coolcollege.intelligent.dao.unifytask.dao;

import com.coolcollege.intelligent.dao.unifytask.UnifyTaskParentCcUserMapper;
import com.coolcollege.intelligent.model.unifytask.UnifyTaskParentCcUserDO;
import com.coolcollege.intelligent.model.unifytask.query.DisplayQuery;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

/**
 * 父任务抄送人dao
 * @author ：xugangkun
 * @date ：2021/11/17 15:17
 */
@Repository
public class UnifyTaskParentCcUserDao {

    @Resource
    private UnifyTaskParentCcUserMapper unifyTaskParentCcUserMapper;

    public UnifyTaskParentCcUserDO selectById(String eid, Long id) {
        return unifyTaskParentCcUserMapper.selectById(eid, id);
    }

    public List<UnifyTaskParentCcUserDO> selectByCcUserId(String eid, String userId, DisplayQuery query) {
        return unifyTaskParentCcUserMapper.selectByCcUserId(eid, userId, query);
    }

    public Integer selectDisplayParentStatistics(String enterpriseId, String userId, String taskType, String status) {
        return unifyTaskParentCcUserMapper.selectDisplayParentStatistics(enterpriseId, userId, taskType, status);
    }

    public void save(String eid, UnifyTaskParentCcUserDO unifyTaskParentCcUser) {
        unifyTaskParentCcUserMapper.save(eid, unifyTaskParentCcUser);
    }

    public void batchInsertOrUpdate(String eid, List<UnifyTaskParentCcUserDO> list) {
        unifyTaskParentCcUserMapper.batchInsertOrUpdate(eid, list);
    }

    public void deleteById(String eid, Long id) {
        unifyTaskParentCcUserMapper.deleteById(eid, id);
    }

    public void deleteByUnifyTaskId(String eid, Long unifyTaskId) {
        unifyTaskParentCcUserMapper.deleteByUnifyTaskId(eid, unifyTaskId);
    }

    public void updateTaskParentStatus(String eid, Long taskId, String parentStatus) {
        unifyTaskParentCcUserMapper.updateTaskParentStatus(eid, taskId, parentStatus);
    }

    /**
     * 根据父任务id列表查询
     * @param enterpriseId
     * @param unifyTaskIds
     * @return
     */
    public List<UnifyTaskParentCcUserDO> selectByUnifyTaskIds(String enterpriseId, List<Long> unifyTaskIds) {
        if(StringUtils.isBlank(enterpriseId) || CollectionUtils.isEmpty(unifyTaskIds)) {
            return Lists.newArrayList();
        }
        return unifyTaskParentCcUserMapper.selectByUnifyTaskIds(enterpriseId, unifyTaskIds);
    }

}
