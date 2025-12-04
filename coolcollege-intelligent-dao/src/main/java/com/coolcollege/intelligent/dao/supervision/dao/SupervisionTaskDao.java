package com.coolcollege.intelligent.dao.supervision.dao;

import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.dao.supervision.SupervisionTaskMapper;
import com.coolcollege.intelligent.model.enums.SupervisionSubTaskStatusEnum;
import com.coolcollege.intelligent.model.supervision.SupervisionTaskDO;
import com.coolcollege.intelligent.model.supervision.request.SupervisionTaskHandleRequest;
import com.coolcollege.intelligent.model.supervision.request.SupervisionTaskQueryRequest;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.checkerframework.checker.units.qual.A;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @Author suzhuhong
 * @Date 2023/2/1 14:26
 * @Version 1.0
 */
@Repository
public class SupervisionTaskDao {

    @Resource
    SupervisionTaskMapper supervisionTaskMapper;

    public int insertSelective( String enterpriseId, SupervisionTaskDO record){
        return supervisionTaskMapper.insertSelective(record,enterpriseId);
    }


    /**
     * 批量新增数据
     * @param enterpriseId
     * @param records
     * @return
     */
    public int batchInsert( String enterpriseId, List<SupervisionTaskDO> records){
        if (CollectionUtils.isEmpty(records)){
            return Constants.INDEX_ZERO;
        }
        return supervisionTaskMapper.batchInsert(enterpriseId,records);
    }

    public SupervisionTaskDO selectByPrimaryKey(Long id, String enterpriseId){
        return supervisionTaskMapper.selectByPrimaryKey(id,enterpriseId);
    }

    /**
     * 更新数据
     * @param enterpriseId
     * @param record
     * @return
     */
    public int updateByPrimaryKeySelective( String enterpriseId, SupervisionTaskDO record){
        return supervisionTaskMapper.updateByPrimaryKeySelective(record,enterpriseId);
    }


    /**
     * 删除
     * @param enterpriseId
     * @param id
     * @return
     */
    public int deleteByPrimaryKey( String enterpriseId, Long id){
        return supervisionTaskMapper.deleteByPrimaryKey(id,enterpriseId);
    }

    public int updateStatus( Integer taskState ,  Date completeTime,Long id, String enterpriseId) {
        return supervisionTaskMapper.updateStatus(taskState,completeTime,id,enterpriseId);
    }

    /**
     * 更新数据
     * @param enterpriseId
     * @param taskName
     * @param taskEndTime
     * @param parentId
     * @return
     */
    public int updateByParentId(String enterpriseId,  String taskName,  Date taskEndTime, Long parentId,Date reminderTimeBeforeEnd,String taskGrouping){
        if (parentId==null){
            return 0;
        }
        return supervisionTaskMapper.updateByParentId(enterpriseId,taskName,taskGrouping,taskEndTime,parentId,reminderTimeBeforeEnd);
    }
    public List<SupervisionTaskDO> listMySupervisionTask(String enterpriseId, SupervisionTaskQueryRequest request,String startTime,
                                                         String endTime,List<String> priorityList,List<SupervisionSubTaskStatusEnum> taskStatusEnumList,List<String> taskGroupingList,Integer handleOverTimeStatus ) {
        if (StringUtils.isBlank(enterpriseId)) {
            return Lists.newArrayList();
        }
        return supervisionTaskMapper.listMySupervisionTask(enterpriseId, request,startTime,endTime,priorityList,taskGroupingList,handleOverTimeStatus,taskStatusEnumList);
    }

    public int updateTaskCompleteInfo(String enterpriseId, SupervisionTaskHandleRequest request,Integer currentNode,Integer handleOverTimeStatus ){
        return supervisionTaskMapper.updateTaskCompleteInfo(enterpriseId,request,currentNode,handleOverTimeStatus);
    }

    public int batchUpdateTaskStatus(String enterpriseId, List<SupervisionTaskDO> records){
        if (StringUtils.isBlank(enterpriseId) || CollectionUtils.isEmpty(records)) {
            return 0;
        }
        return supervisionTaskMapper.batchUpdateTaskStatus(enterpriseId, records);
    }

    public int batchUpdateTaskStatusAndCancelStatus(String enterpriseId, List<SupervisionTaskDO> records){
        if (StringUtils.isBlank(enterpriseId) || CollectionUtils.isEmpty(records)) {
            return 0;
        }
        return supervisionTaskMapper.batchUpdateTaskStatusAndCancelStatus(enterpriseId, records);
    }

    public List<SupervisionTaskDO> listSupervisionTaskByParentId( String enterpriseId, Long parentId,String userName, List<SupervisionSubTaskStatusEnum> completeStatusList,Integer handleOverTimeStatus){
        if (parentId==null) {
            return Collections.emptyList();
        }
        return supervisionTaskMapper.listByParentId(enterpriseId, parentId, userName,completeStatusList,handleOverTimeStatus);
    }

    public Long countByParentId( String enterpriseId, Long parentId, String userName,List<SupervisionSubTaskStatusEnum> completeStatusList,Integer handleOverTimeStatus){
        if (parentId==null) {
            return 0L;
        }
        return supervisionTaskMapper.countByParentId(enterpriseId, parentId, userName,completeStatusList,handleOverTimeStatus );
    }


    public int taskCancel(String enterpriseId, Long taskId,Long id){
        if (taskId==null&&id==null){
            return -1;
        }
        return supervisionTaskMapper.taskCancel(enterpriseId,taskId,id);
    }

    /**
     * 任务删除
     * @param enterpriseId
     * @param taskId
     * @return
     */
    public int taskDel(String enterpriseId, Long taskId){
        if (taskId==null){
            return 0;
        }
        return supervisionTaskMapper.taskDel(enterpriseId,taskId);
    }


    public List<SupervisionTaskDO> noCompleteListByParentId(String enterpriseId, Long taskId){
        if (taskId==null){
            return Collections.emptyList();
        }
        return supervisionTaskMapper.noCompleteListByParentId(enterpriseId,taskId);
    }


    public Integer notCancelCountByParentId(String enterpriseId, Long taskId){
        if (taskId==null){
            return -1;
        }
        return supervisionTaskMapper.notCancelCountByParentId(enterpriseId,taskId);
    }


    public List<SupervisionTaskDO> listSupervisionTaskByFormId( String enterpriseId,  String formId,  List<Long> taskParentIds, Date startTime, Date endTime){
        if (formId==null) {
            return Collections.emptyList();
        }
        return supervisionTaskMapper.listSupervisionTaskByFormId(enterpriseId, formId,taskParentIds,startTime,endTime);
    }

    public Long countSupervisionTaskByFormId( String enterpriseId,  String formId,  List<Long> taskParentIds, Date startTime, Date endTime){
        if (formId==null) {
            return 0L;
        }
        return supervisionTaskMapper.countSupervisionTaskByFormId(enterpriseId, formId,taskParentIds,startTime,endTime);
    }

    public List<SupervisionTaskDO> listByIds( String enterpriseId,  List<Long> taskIds){
        if (CollectionUtils.isEmpty(taskIds)) {
            return Collections.emptyList();
        }
        return supervisionTaskMapper.listByIds(enterpriseId, taskIds);
    }

    public List<SupervisionTaskDO> listReminderBeforeSupervisionTask(String enterpriseId, String reminderTimeBeforeStarting) {
        if (StringUtils.isBlank(enterpriseId)) {
            return Lists.newArrayList();
        }
        return supervisionTaskMapper.listReminderBeforeSupervisionTask(enterpriseId, reminderTimeBeforeStarting);
    }

    public List<SupervisionTaskDO> listReminderAfterSupervisionTask(String enterpriseId, String reminderTimeAfterEnd) {
        if (StringUtils.isBlank(enterpriseId)) {
            return Lists.newArrayList();
        }
        return supervisionTaskMapper.listReminderAfterSupervisionTask(enterpriseId, reminderTimeAfterEnd);
    }


    public Integer batchUpdateTask(String enterpriseId, List<SupervisionTaskDO> supervisionTaskDOS) {
        if (StringUtils.isBlank(enterpriseId)&&CollectionUtils.isEmpty(supervisionTaskDOS)) {
            return 0;
        }
        return supervisionTaskMapper.batchUpdateTask(enterpriseId, supervisionTaskDOS);
    }

    public SupervisionTaskDO selectSupervisionTask(String enterpriseId,Long parentId, String userId) {
        if (StringUtils.isBlank(enterpriseId)) {
            return null;
        }
        return supervisionTaskMapper.selectSupervisionTask(enterpriseId, parentId,userId);
    }

    public int updateHandleOverTimeData(String enterpriseId) {
        if (StringUtils.isBlank(enterpriseId)) {
            return 0;
        }
        return supervisionTaskMapper.updateHandleOverTimeData(enterpriseId);
    }

    public List<SupervisionTaskDO> correctData(String enterpriseId) {
        if (StringUtils.isBlank(enterpriseId)) {
            return Collections.emptyList();
        }
        return supervisionTaskMapper.correctData(enterpriseId);
    }

}
