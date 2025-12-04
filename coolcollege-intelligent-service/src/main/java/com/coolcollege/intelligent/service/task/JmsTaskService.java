package com.coolcollege.intelligent.service.task;

import com.coolcollege.intelligent.model.enums.DingMsgEnum;
import com.coolcollege.intelligent.model.enums.SendUserTypeEnum;
import com.coolcollege.intelligent.model.msg.SupervisionTaskMessageDTO;
import com.coolcollege.intelligent.model.unifytask.TaskParentDO;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * @author 邵凌志
 * @date 2020/7/24 14:58
 */
public interface JmsTaskService {

    /**
     * 统一任务消息发送提醒
     * @param taskType
     * @param handleUserId
     * @param nodeStr
     * @param eid
     * @param storeName
     * @param unifyTaskSubId
     * @param createUserName
     * @param endTime
     * @param taskName
     * @param isTransmit
     * @param beginTime
     * @param storeId
     * @param outBusinessId
     * @param isCC
     */
    void sendUnifyTaskJms(String taskType, List<String> handleUserId, String nodeStr, String eid, String storeName,
                          Long unifyTaskSubId, String createUserName, Long endTime, String taskName, Boolean isTransmit, Long beginTime, String storeId,
                          String outBusinessId, Boolean isCC, Long unifyTaskId, Long cycleCount);


    void sendUnifyTaskJms(String taskType, List<String> handleUserId, String nodeStr, String eid, String storeName,
                          Long unifyTaskSubId, String createUserName, Long endTime, String taskName, Boolean isTransmit, Long beginTime, String storeId,
                          String outBusinessId, Boolean isCC, Long unifyTaskId, Long cycleCount, Long loopCount, Long businessId);

    /**
     *
     * @param enterpriseId
     * @param unifyTaskId
     * @param storeId
     * @param loopCount
     * @param paramMap
     */
    void sendQuestionMessage(String enterpriseId, Long unifyTaskId, String storeId, Long loopCount, String operate, Map<String, String> paramMap);


    /**
     *
     * @param enterpriseId
     * @param parentQuestionId
     * @param unifyTaskId
     * @param storeId
     * @param loopCount
     * @param userIds
     * @param title
     * @param content
     */
    void sendQuestionReminder(String enterpriseId, Long parentQuestionId, Long unifyTaskId, String storeId, Long loopCount, List<String> userIds, String title, String content);

    /**
     *
     * @param enterpriseId
     * @param title
     * @param content
     */
    void sendDeleteQuestionReminder(String enterpriseId, Map<SendUserTypeEnum, List<String>> nodePersonMap, String title, String content, Long parentQuestionId, Long questionRecordId);

    void sendPatrolStoreReminder(String enterpriseId, TaskParentDO taskParentDO, List<String> userIds, String content);

    /**
     * 店务消息提醒、钉钉待办
     * @param enterpriseId
     * @param dataTableId
     * @param operate
     * @param paramMap
     */
    void sendStoreWorkMessage(String enterpriseId, Long dataTableId, String operate, Map<String, String> paramMap);

    /**
     * 店务催办
     * @param enterpriseId
     * @param storeWorkId
     * @param userIds
     * @param title
     * @param content
     */
    void sendStoreWorkReminder(String enterpriseId, Long storeWorkId, List<String> userIds, String title, String content);

    /**
     * 食安稽核消息提醒、钉钉待办
     * @param enterpriseId
     * @param businessId
     * @param node
     * @param nodeUserList
     */
    void sendSafetyCheckMessage(String enterpriseId, Long businessId, String node, List<String> nodeUserList);



    void sendLicenseMessage(String enterpriseId, String dingCorpId, String appType, List<String> userIds, String title, String content, String imageUrl, Long noticeSettingId, String type);

    /**
     * 发送文本消息
     * @param enterpriseId
     * @param userIds
     * @param title
     * @param content
     */
    void sendTextMessage(String enterpriseId, List<String> userIds, String title, String content);

    /**
     * 督导助手钉钉待办
     * @param enterpriseId
     * @param supervisionTaskId
     */
    void sendSupervisionTaskBacklogByTaskId(String enterpriseId, Long supervisionTaskId);

    /**
     * 督导文本消息
     * @param enterpriseId
     * @param supervisionTaskId
     * @param handleUserIdList
     * @param title
     * @param content
     */
    void sendSupervisionTaskTextMessage(String enterpriseId, Long supervisionTaskId, List<String> handleUserIdList, String title, String content);

    /**
     * 督导消息
     * @param enterpriseId
     * @param taskMessageDTO
     */
    void sendSupervisionTaskMessage(String enterpriseId, SupervisionTaskMessageDTO taskMessageDTO);

    /**
     * 发送按门店 审批人钉钉待办
     * @param enterpriseId
     * @param supervisionStoreTaskId
     */
    void sendSupervisionStoreTaskBacklogByTaskId(String enterpriseId, Long supervisionStoreTaskId);

    void sendNoticeUnifyTask(String taskType, List<String> handleUserIdList, String nodeStr, String eid, String storeName, Long unifyTaskSubId,  Long endTime,
                            Long beginTime, String storeId,  Long unifyTaskId, String content, String taskName);

    void sendStoreReportNoticeUnifyTask( String eid, String taskType, List<String> handleUserIdList, String storeName, String storeId, Long reportId, String content);



    void sendUnifyTaskReminder(String enterpriseId, String dingCorpId, String appType, Long unifyTaskId, Long subTaskId, String taskType, List<String> handleUserIds, Long loopCount, String nodeNo, Map<String, String> paramMap);

    /**
     * 发送AI店报消息
     * @param enterpriseId 企业id
     * @param reportId 店报id
     * @param storeId 门店id
     * @param storeName 门店名称
     * @param date AI店报分析日期
     * @param userIds 用户id列表
     */
    void sendAiAnalysisReportMessage(String enterpriseId, Long reportId, String storeId, String storeName, LocalDate date, List<String> userIds, String appType, String dingCorpId);

    /**
     * 发送消息
     * @param enterpriseId
     * @param outBusinessId
     * @param dingMsgEnum
     * @param userIds
     * @param title
     * @param content
     * @param paramMap
     */
    void sendMessage(String enterpriseId, String outBusinessId, DingMsgEnum dingMsgEnum, List<String> userIds, String title, String content, Map<String, String> paramMap);
}
