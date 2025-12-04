package com.coolcollege.intelligent.service.export;

import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.model.achievement.qyy.dto.ConfidenceFeedbackPageDTO;
import com.coolcollege.intelligent.model.achievement.request.AchievementDetailListExport;
import com.coolcollege.intelligent.model.device.request.DeviceListRequest;
import com.coolcollege.intelligent.model.device.request.DeviceReportSearchRequest;
import com.coolcollege.intelligent.model.enums.SupervisionSubTaskStatusEnum;
import com.coolcollege.intelligent.model.export.request.UserInfoExportRequest;
import com.coolcollege.intelligent.model.impoetexcel.ImportTaskDO;
import com.coolcollege.intelligent.model.patrolstore.query.PatrolStoreCheckQuery;
import com.coolcollege.intelligent.model.patrolstore.query.PatrolStoreStatisticsDataTableQuery;
import com.coolcollege.intelligent.model.patrolstore.request.ExportTaskStageRecordListRequest;
import com.coolcollege.intelligent.model.patrolstore.request.PatrolStoreDetailRequest;
import com.coolcollege.intelligent.model.patrolstore.request.TableRecordsRequest;
import com.coolcollege.intelligent.model.question.request.QuestionParentRequest;
import com.coolcollege.intelligent.model.question.request.RegionQuestionReportRequest;
import com.coolcollege.intelligent.model.question.request.TbQuestionRecordSearchRequest;
import com.coolcollege.intelligent.model.region.request.ExternalRegionExportRequest;
import com.coolcollege.intelligent.model.storework.request.StoreWorkDataListRequest;
import com.coolcollege.intelligent.model.storework.request.StoreWorkRecordListRequest;
import com.coolcollege.intelligent.model.unifytask.query.TaskQuestionQuery;
import com.coolcollege.intelligent.model.unifytask.query.TaskReportQuery;
import com.coolcollege.intelligent.model.userholder.CurrentUser;

import java.util.List;

/**
 * @author byd
 * @date 2021-05-20 20:17
 */
public interface ExportService {

    /**
     * 分页导出巡店记录
     * @param eid
     * @param totalNum
     * @param request
     * @param task
     */
    void tableRecordsListExport(String eid, Long totalNum, TableRecordsRequest request, ImportTaskDO task);

    /**
     * 分页导出业绩详情
     * @param achievementDetailListExport 导出请求
     */
    void achievementDetailExport(AchievementDetailListExport achievementDetailListExport);

    /**
     * 分页导出工单报表
     * @param
     */
    void taskQuestionReportExport(String eid, Long totalNum, TaskQuestionQuery query, ImportTaskDO task);

    void taskStageRecordListExport(String eid, Long totalNum, ExportTaskStageRecordListRequest request, ImportTaskDO task);

    void taskStageRecordListDetailExport(String eid, Long totalNum, Long businessId,Long metaTableId, ImportTaskDO task, String dbName);

    /**
     * 分页导出巡店任务报表
     * @param eid
     * @param totalNum
     * @param query
     * @param task
     */
    void patrolStoreTaskReportExport(String eid, Long totalNum, TaskReportQuery query, ImportTaskDO task);

    /**
     *
     * @param eid
     * @param totalNum
     */
    void recordListDetailExport(String eid, Long totalNum, ImportTaskDO task, PatrolStoreStatisticsDataTableQuery query) ;

    /**
     * 问题工单导出
     * @param enterpriseId 企业id
     * @param totalNum 导出数量
     * @param importTaskDO ImportTaskDO
     * @param request TbQuestionRecordSearchRequest
     * @param dbName 数据库名
     */
    void tbQuestionRecordExport(String enterpriseId, Long totalNum, ImportTaskDO importTaskDO, TbQuestionRecordSearchRequest request, String dbName);

    void patrolStoreDetailExport(String eid, Long totalNum, ImportTaskDO task, PatrolStoreDetailRequest request, String dbName);

    /**
     * 工单详情导出
     * @param enterpriseId
     * @param totalNum
     * @param importTaskDO
     * @param request
     * @param dbName
     */
    void subQuestionDetailListExport(String enterpriseId, Long totalNum, ImportTaskDO importTaskDO, TbQuestionRecordSearchRequest request, String dbName);

    /**
     * 区域门店工单导出
     * @param enterpriseId
     * @param totalNum
     * @param importTaskDO
     * @param request
     * @param dbName
     */
    void regionQuestionReportExport(String enterpriseId, Long totalNum, ImportTaskDO importTaskDO, RegionQuestionReportRequest request, String dbName);


    /**
     * 工单列表导出
     * @param enterpriseId
     * @param totalNum
     * @param importTaskDO
     * @param request
     * @param dbName
     */
    void questionParentInfoListExport(String enterpriseId, Long totalNum, ImportTaskDO importTaskDO, QuestionParentRequest request, String dbName);

    void storeWorkStoreStatisticsListExport(String enterpriseId, Long totalNum, ImportTaskDO importTaskDO, StoreWorkDataListRequest request, CurrentUser user, String dbName);

    void storeWorkRegionStatisticsListExport(String enterpriseId, Long totalNum, ImportTaskDO importTaskDO, StoreWorkDataListRequest request, CurrentUser user, String dbName);

    void storeWorkDayStatisticsListExport(String enterpriseId, Long totalNum, ImportTaskDO importTaskDO, StoreWorkDataListRequest request, CurrentUser user, String dbName);

    void storeWorkRecordListExport(String enterpriseId, Long totalNum, ImportTaskDO importTaskDO, StoreWorkRecordListRequest request, CurrentUser user, String dbName);

    void storeWorkRecordListDetailExport(String enterpriseId, Long totalNum, ImportTaskDO importTaskDO, StoreWorkRecordListRequest request, CurrentUser user, String dbName);

    void storeWorkTableListExport(String enterpriseId, Long totalNum, ImportTaskDO importTaskDO, StoreWorkRecordListRequest request, String dbName, CurrentUser user);

    void storeWorkColumnListExport(String enterpriseId, Long totalNum, ImportTaskDO importTaskDO, StoreWorkRecordListRequest request, String dbName, CurrentUser user);

    void deviceListExport(String enterpriseId, Long totalNum, ImportTaskDO importTaskDO, DeviceListRequest request, String dbName, CurrentUser user);

    void exportDeviceSummaryExport(String enterpriseId, Long totalNum, ImportTaskDO importTaskDO, DeviceReportSearchRequest request, String dbName, CurrentUser user);

    void exportSupervisionTask(String enterpriseId, Long totalNum, ImportTaskDO importTaskDO, Long parentId,String userName, List<SupervisionSubTaskStatusEnum> completeStatus, CurrentUser currentUser, String dbName,Integer handleOverTimeStatus);

    void exportSupervisionStoreTask(String enterpriseId, Long totalNum, ImportTaskDO importTaskDO, Long parentId, List<String> storeIds,String userName, List<SupervisionSubTaskStatusEnum> completeStatusList,
                                    CurrentUser currentUser, String dbName,Long taskId,List<String> regionIds,Integer handleOverTimeStatus);

    void exportSupervisionDataDetail(String enterpriseId,Long totalNum, ImportTaskDO importTaskDO,List<Long> parentIds,String formId,Long submitStartTime,Long submitEndTime,String type,CurrentUser currentUser,String dbName);

    void exportConfidenceFeedback(String enterpriseId, Long totalNum, ImportTaskDO importTaskDO , ConfidenceFeedbackPageDTO request, CurrentUser currentUser, String dbName);

    void exportActivityUser(String enterpriseId, ImportTaskDO importTaskDO,Long activityId, String dbName);

    void exportActivityComment(String enterpriseId, ImportTaskDO importTaskDO,Long activityId, Long total,String dbName);

    void exportWeeklyNewspaperList(String enterpriseId, ImportTaskDO importTaskDO, Long totalNum, String dbName);

    void exportPatrolStoreReviewList(String enterpriseId, ImportTaskDO importTaskDO, Long totalNum, String dbName ,List<String> recordIds);

    void exportUserInfo(String enterpriseId, ImportTaskDO importTaskDO, Long totalNum, String dbName, JSONObject request);

    void externalUserInfoExport(String enterpriseId, Long totalNum, ImportTaskDO importTaskDO, UserInfoExportRequest request, String dbName, CurrentUser user);

    void externalRegionExport(String enterpriseId, Long totalNum, ImportTaskDO importTaskDO, ExternalRegionExportRequest request, String dbName, CurrentUser user);

    void exportPatrolStoreCheckList(PatrolStoreCheckQuery patrolStoreCheckQuery);

    void exportCheckDetailList(PatrolStoreCheckQuery query);

    void exportCheckAnalyzeList(PatrolStoreCheckQuery checkQuery);
}
