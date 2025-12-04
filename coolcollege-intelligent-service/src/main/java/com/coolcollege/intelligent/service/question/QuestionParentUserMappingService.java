package com.coolcollege.intelligent.service.question;

import com.coolcollege.intelligent.model.unifytask.TaskStoreDO;

import java.util.List;

/**
 * @author byd
 * @date 2022-08-16 15:53
 */
public interface QuestionParentUserMappingService {

    /**
     * 保存任务人员关系
     * @param eid
     * @param taskStoreDO
     */
    void saveUserMapping(String eid, TaskStoreDO taskStoreDO, Long questionParentId, String questionParentName);

    /**
     * 更新任务人员关系
     * @param eid
     * @param unifyTaskId
     */
    void updateUserMapping(String eid, Long unifyTaskId, List<String> addPeopleUserIdList, List<String> removePeopleUserIdList);


    void updateByTaskStore(String eid, TaskStoreDO taskStoreDO, Boolean isCorrectData);
}
