package com.coolcollege.intelligent.dao.unifytask.dao;

import com.coolcollege.intelligent.dao.unifytask.UnifyTaskParentCollaboratorMapper;
import com.coolcollege.intelligent.model.unifytask.UnifyTaskParentCollaboratorDO;
import com.coolcollege.intelligent.model.unifytask.dto.UnifyPersonDTO;
import com.coolcollege.intelligent.model.unifytask.query.DisplayQuery;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author byd
 * @date 2023-02-13 14:57
 */
@Repository
public class UnifyTaskParentCollaboratorDao {

    @Resource
    private UnifyTaskParentCollaboratorMapper unifyTaskParentCollaboratorMapper;


    public void batchInsertOrUpdate(String eid, List<UnifyTaskParentCollaboratorDO> list) {
        unifyTaskParentCollaboratorMapper.batchInsertOrUpdate(eid, list);
    }

    public void deleteByTaskId(String eid, Long unifyTaskId) {
        if(unifyTaskId == null){
            return;
        }
        unifyTaskParentCollaboratorMapper.deleteByTaskId(eid, unifyTaskId);
    }

    public List<String> selectCollaboratorIdByTaskId(String enterpriseId, Long unifyTaskId){
        return unifyTaskParentCollaboratorMapper.selectCollaboratorIdByTaskId(enterpriseId, unifyTaskId);
    }

    public List<UnifyPersonDTO> selectCollaboratorIdByTaskIdList(String enterpriseId, List<Long> unifyTaskIdList){
        if(CollectionUtils.isEmpty(unifyTaskIdList)){
            return new ArrayList<>();
        }
        return unifyTaskParentCollaboratorMapper.selectCollaboratorIdByTaskIdList(enterpriseId, unifyTaskIdList);
    }

    public List<UnifyTaskParentCollaboratorDO> selectByCollaboratorId(String eid, String userId, DisplayQuery query){
        return unifyTaskParentCollaboratorMapper.selectByCollaboratorId(eid, userId, query);
    }

    public Integer selectDisplayParentStatistics(String enterpriseId, String userId, String taskType, String status) {
        return unifyTaskParentCollaboratorMapper.selectDisplayParentStatistics(enterpriseId, userId, taskType, status);
    }

    public void updateTaskParentStatus(String eid, Long taskId, String parentStatus) {
        unifyTaskParentCollaboratorMapper.updateTaskParentStatus(eid, taskId, parentStatus);
    }

    public List<String> selectByUserId(String enterpriseId, String userId,String taskType) {
        return unifyTaskParentCollaboratorMapper.selectByUserId(enterpriseId,userId,taskType);
    }
}
