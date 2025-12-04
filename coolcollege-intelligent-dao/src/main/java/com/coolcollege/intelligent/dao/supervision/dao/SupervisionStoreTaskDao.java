package com.coolcollege.intelligent.dao.supervision.dao;

import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.dao.supervision.SupervisionStoreTaskMapper;
import com.coolcollege.intelligent.model.enums.SupervisionSubTaskStatusEnum;
import com.coolcollege.intelligent.model.supervision.SupervisionDefDataColumnDO;
import com.coolcollege.intelligent.model.supervision.SupervisionStoreTaskDO;
import com.coolcollege.intelligent.model.supervision.SupervisionTaskDO;
import com.coolcollege.intelligent.model.supervision.dto.SupervisionStoreDataDTO;
import com.coolcollege.intelligent.model.supervision.dto.SupervisionStoreTaskBasicDataDTO;
import com.coolcollege.intelligent.model.supervision.request.SupervisionStoreTaskQueryRequest;
import com.coolcollege.intelligent.model.supervision.request.SupervisionTaskQueryRequest;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @Author suzhuhong
 * @Date 2023/2/27 17:11
 * @Version 1.0
 */
@Repository
public class SupervisionStoreTaskDao {

    @Resource
    SupervisionStoreTaskMapper supervisionStoreTaskMapper;


    public int insertSelective(SupervisionStoreTaskDO record,  String enterpriseId){
        return supervisionStoreTaskMapper.insertSelective(record,enterpriseId);
    }

    /**
     *
     * 默认查询方法，通过主键获取所有字段的值
     * dateTime:2023-02-27 03:03
     */
    public SupervisionStoreTaskDO selectByPrimaryKey(Long id,  String enterpriseId){
        return supervisionStoreTaskMapper.selectByPrimaryKey(id,enterpriseId);
    }

    /**
     *
     * 默认更新方法，根据主键更新，不会把null值更新到数据库，避免覆盖之前有值的
     * dateTime:2023-02-27 03:03
     */
    public int updateByPrimaryKeySelective(SupervisionStoreTaskDO record,  String enterpriseId){
        return supervisionStoreTaskMapper.updateByPrimaryKeySelective(record,enterpriseId);
    }

    /**
     *
     * 默认更新方法，根据主键物理删除
     * dateTime:2023-02-27 03:03
     */
    public int deleteByPrimaryKey(Long id,  String enterpriseId){
        return supervisionStoreTaskMapper.deleteByPrimaryKey(id,enterpriseId);
    }

    /**
     * 门店任务取消
     * @param enterpriseId
     * @param taskId
     * @param id
     * @return
     */
    public int storeTaskCancel(String enterpriseId, Long taskParentId,Long taskId,Long id){
        if (taskParentId==null&&id==null&&taskId==null){
            return -1;
        }
        return supervisionStoreTaskMapper.storeTaskCancel(enterpriseId,taskParentId,taskId,id);
    }

    /**
     * 未取消门店数量 根据督导任务表ID 查询
     * @param enterpriseId
     * @param supervisionTaskId
     * @return
     */
    public int notCancelCountByParentId(String enterpriseId, Long supervisionTaskId){
        if (supervisionTaskId==null){
            return -1;
        }
        return supervisionStoreTaskMapper.notCancelCountByParentId(enterpriseId,supervisionTaskId);
    }



    public int storeTaskDel(String enterpriseId, Long parentId){
        if (parentId==null){
            return -1;
        }
        return supervisionStoreTaskMapper.storeTaskDel(enterpriseId,parentId);
    }

    public List<SupervisionStoreTaskDO> listSupervisionTaskByParentId(String enterpriseId, Long parentId,Long supervisionTaskId, List<String> storeIds,List<String> regionIds,
                                                                      List<SupervisionSubTaskStatusEnum> completeStatusList, String  userName,Integer handleOverTimeStatus){
        if (parentId==null&&supervisionTaskId==null) {
            return Collections.emptyList();
        }
        return supervisionStoreTaskMapper.listByParentId(enterpriseId, parentId, supervisionTaskId, storeIds,regionIds,userName,completeStatusList,handleOverTimeStatus);
    }

    public Long countByParentId(String enterpriseId, Long parentId,Long supervisionTaskId, List<String> storeIds,List<String> regionIds,
                                List<SupervisionSubTaskStatusEnum> completeStatusList, String  userName,Integer handleOverTimeStatus){
        if (parentId==null) {
            return 0L;
        }
        return supervisionStoreTaskMapper.countByParentId(enterpriseId, parentId,supervisionTaskId, storeIds,regionIds,userName,completeStatusList,handleOverTimeStatus);
    }


    public List<SupervisionStoreTaskDO> getSupervisionStoreList(String enterpriseId, Long taskId, String userId, Integer taskState,Integer handleOverTimeStatus,String storeName){
        if (userId==null){
            return Collections.emptyList();
        }
        return supervisionStoreTaskMapper.getSupervisionStoreList(enterpriseId,taskId,userId,taskState,handleOverTimeStatus, storeName);
    }

    /**
     * 根据门店ID查询我的任务列表
     * @param enterpriseId
     * @param request
     * @param startTime
     * @param endTime
     * @param priority
     * @param taskStatus
     * @return
     */
    public List<SupervisionStoreTaskDO> listMySupervisionStoreTask(String enterpriseId, SupervisionStoreTaskQueryRequest request, String startTime,
                                                                   String endTime, String priority, Integer taskStatus,Integer handleOverTimeStatus) {
        if (StringUtils.isBlank(enterpriseId)) {
            return Lists.newArrayList();
        }
        return supervisionStoreTaskMapper.listMySupervisionStoreTask(enterpriseId, request,startTime,endTime,priority,taskStatus,handleOverTimeStatus);
    }

    public int batchInsert( String enterpriseId, List<SupervisionStoreTaskDO> records){
        if (CollectionUtils.isEmpty(records)){
            return Constants.INDEX_ZERO;
        }
        return supervisionStoreTaskMapper.batchInsert(enterpriseId,records);
    }

    public List<SupervisionStoreDataDTO> getStoreIdList(String enterpriseId, String userId){
        if (userId==null){
            return Collections.emptyList();
        }
        return supervisionStoreTaskMapper.getStoreIdList(enterpriseId,userId);
    }

    public int batchUpdateTaskStatus(String enterpriseId, List<SupervisionStoreTaskDO> records){
        if (StringUtils.isBlank(enterpriseId) || CollectionUtils.isEmpty(records)) {
            return 0;
        }
        return supervisionStoreTaskMapper.batchUpdateTaskStatus(enterpriseId, records);
    }

    public int batchUpdateStoreTask(String enterpriseId, List<SupervisionStoreTaskDO> records){
        if (StringUtils.isBlank(enterpriseId) || CollectionUtils.isEmpty(records)) {
            return 0;
        }
        return supervisionStoreTaskMapper.batchUpdateStoreTask(enterpriseId, records);
    }

    public int updateTaskStatus(String enterpriseId, Long id, Integer status,Integer currentNode){
        if (StringUtils.isBlank(enterpriseId) || id==null || status == null) {
            return 0;
        }
        return supervisionStoreTaskMapper.updateTaskStatus(enterpriseId, id, status,currentNode);
    }


    public List<SupervisionStoreTaskDO> listSupervisionStoreTask(String enterpriseId, List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return Lists.newArrayList();
        }
        return supervisionStoreTaskMapper.listSupervisionStoreTask(enterpriseId,ids);
    }

    public List<SupervisionStoreTaskDO> listSupervisionStoreTaskBySupervisionTaskId(String enterpriseId, List<Long> ids,Boolean filterCancel,Boolean filterTaskState) {
        if (CollectionUtils.isEmpty(ids)) {
            return Lists.newArrayList();
        }
        return supervisionStoreTaskMapper.listSupervisionStoreTaskBySupervisionTaskId(enterpriseId,ids,filterCancel,filterTaskState);
    }

    public List<SupervisionStoreTaskDO> listSupervisionStoreTaskByFormId(String enterpriseId, String formId, List<Long> taskParentIds, Date startTime, Date endTime) {
        if (formId==null) {
            return Lists.newArrayList();
        }
        return supervisionStoreTaskMapper.listSupervisionStoreTaskByFormId(enterpriseId,formId,taskParentIds,startTime,endTime);
    }

    public Long countSupervisionStoreTaskByFormId(String enterpriseId, String formId, List<Long> taskParentIds, Date startTime, Date endTime) {
        if (formId==null) {
            return 0L;
        }
        return supervisionStoreTaskMapper.countSupervisionStoreTaskByFormId(enterpriseId,formId,taskParentIds,startTime,endTime);
    }

    public Long noCompleteListByTaskId(String enterpriseId, Long taskId) {
        if (StringUtils.isBlank(enterpriseId)) {
            return -1L;
        }
        return supervisionStoreTaskMapper.noCompleteListByTaskId(enterpriseId, taskId);
    }

    public SupervisionStoreTaskDO completeStatus(String enterpriseId, Long taskId) {
        return supervisionStoreTaskMapper.completeStatus(enterpriseId, taskId);
    }




    public int batchUpdateStateBySupervisionTaskId(String enterpriseId, List<Long> idList,Integer status){
        if (StringUtils.isBlank(enterpriseId) || CollectionUtils.isEmpty(idList)) {
            return 0;
        }
        return supervisionStoreTaskMapper.batchUpdateStateBySupervisionTaskId(enterpriseId, idList,status);
    }

    public int updateByParentId(String enterpriseId,  String taskName,  Date taskEndTime, Long parentId,Date reminderTimeBeforeEnd,String taskGrouping){
        if (parentId==null){
            return 0;
        }
        return supervisionStoreTaskMapper.updateByParentId(enterpriseId,taskName,taskGrouping,taskEndTime,parentId,reminderTimeBeforeEnd);
    }

    public List<SupervisionStoreTaskDO> listStoreTaskBySupervisionTaskId(String enterpriseId, List<Long> ids,String storeName,Boolean filterTransferReassign,Boolean filterCancel,Boolean filterComplete) {
        if (CollectionUtils.isEmpty(ids)) {
            return Lists.newArrayList();
        }
        return supervisionStoreTaskMapper.listStoreTaskBySupervisionTaskId(enterpriseId,ids,storeName,filterTransferReassign,filterCancel,filterComplete);
    }

    public Integer countStoreTaskBySupervisionTaskId(String enterpriseId, List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return -1;
        }
        return supervisionStoreTaskMapper.countStoreTaskBySupervisionTaskId(enterpriseId,ids);
    }

    public List<SupervisionStoreTaskDO> listReminderBeforeSupervisionTask(String enterpriseId, String reminderTimeBeforeStarting) {
        if (StringUtils.isBlank(enterpriseId)) {
            return Lists.newArrayList();
        }
        return supervisionStoreTaskMapper.listReminderBeforeSupervisionTask(enterpriseId, reminderTimeBeforeStarting);
    }

    public List<SupervisionStoreTaskDO> listReminderAfterSupervisionTask(String enterpriseId, String reminderTimeAfterEnd) {
        if (StringUtils.isBlank(enterpriseId)) {
            return Lists.newArrayList();
        }
        return supervisionStoreTaskMapper.listReminderAfterSupervisionTask(enterpriseId, reminderTimeAfterEnd);
    }

    public List<SupervisionStoreTaskBasicDataDTO> supervisionStoreTaskBasicData(String enterpriseId, List<Long> reminderTimeAfterEnd) {
        if (StringUtils.isBlank(enterpriseId)) {
            return Lists.newArrayList();
        }
        return supervisionStoreTaskMapper.supervisionStoreTaskBasicData(enterpriseId, reminderTimeAfterEnd);
    }

    public Integer batchUpdateStoreTaskStatus(String enterpriseId, List<SupervisionStoreTaskDO> supervisionStoreTaskDOS) {
        if (StringUtils.isBlank(enterpriseId)) {
            return 0;
        }
        return supervisionStoreTaskMapper.batchUpdateStoreTaskStatus(enterpriseId, supervisionStoreTaskDOS);
    }

    public List<SupervisionStoreTaskDO> listSupervisionStoreTaskIdList(String enterpriseId, List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return Lists.newArrayList();
        }
        return supervisionStoreTaskMapper.listSupervisionStoreTaskIdList(enterpriseId, ids);
    }


    public int updateHandleOverTimeData(String enterpriseId) {
        if (StringUtils.isBlank(enterpriseId)) {
            return 0;
        }
        return supervisionStoreTaskMapper.updateHandleOverTimeData(enterpriseId);
    }

    public List<SupervisionStoreTaskDO> correctData(String enterpriseId) {
        if (StringUtils.isBlank(enterpriseId)) {
            return Collections.emptyList();
        }
        return supervisionStoreTaskMapper.correctData(enterpriseId);
    }
}
