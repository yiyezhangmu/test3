package com.coolcollege.intelligent.service.achievement;

import com.coolcollege.intelligent.model.achievement.dto.AchievementTaskRecordDTO;
import com.coolcollege.intelligent.model.achievement.dto.AchievementTaskRecordDetailDTO;
import com.coolcollege.intelligent.model.unifytask.TaskParentDO;
import com.coolcollege.intelligent.model.unifytask.TaskSubDO;
import com.coolcollege.intelligent.model.unifytask.dto.AchievementTaskStoreSubmitDTO;
import com.coolcollege.intelligent.model.unifytask.dto.TaskMessageDTO;
import com.coolcollege.intelligent.model.unifytask.query.AchievementTaskStoreQuery;
import com.github.pagehelper.PageInfo;

/**
 * @author byd
 * @date 2024-03-16 14:26
 */
public interface AchievementTaskRecordService {

    boolean addRecord(TaskMessageDTO taskMessageDTO, TaskSubDO taskSubDO);

    boolean addRecord(String enterpriseId, TaskParentDO taskParent, TaskSubDO taskSubDO);

    boolean delRecord(String enterpriseId, Long taskId);

    PageInfo<AchievementTaskRecordDTO> achievementStoreTaskList(String enterpriseId, AchievementTaskStoreQuery query, String userId);


    AchievementTaskRecordDetailDTO storeTaskDetail(String enterpriseId, Long taskId, String storeId);

    PageInfo<AchievementTaskRecordDTO> achievementMyStoreTaskList(String enterpriseId, AchievementTaskStoreQuery query, String userId);


    boolean submitTask(String enterpriseId, AchievementTaskStoreSubmitDTO storeSubmitDTO, String userId, String userName);

    boolean sendRemindMsg(String enterpriseId);

}
