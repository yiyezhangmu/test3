package com.coolcollege.intelligent.facade.consumer.listener;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.MessageListener;
import com.coolcollege.intelligent.common.enums.MsgUniteDataTypeEnum;
import com.coolcollege.intelligent.facade.SyncUserFacade;
import com.coolcollege.intelligent.model.achievement.dto.QyyWeeklyNewsPaperExportDTO;
import com.coolcollege.intelligent.model.achievement.qyy.dto.ExportConfidenceFeedbackRequest;
import com.coolcollege.intelligent.model.achievement.request.AchievementDetailListExport;
import com.coolcollege.intelligent.model.activity.dto.ActivityCommentExportDTO;
import com.coolcollege.intelligent.model.activity.dto.ActivityUserDTO;
import com.coolcollege.intelligent.model.device.request.ExportDeviceRequest;
import com.coolcollege.intelligent.model.device.request.ExportDeviceSummaryRequest;
import com.coolcollege.intelligent.model.export.request.ExportMsgSendRequest;
import com.coolcollege.intelligent.model.export.request.UserInfoExportRequest;
import com.coolcollege.intelligent.model.msg.MsgUniteData;
import com.coolcollege.intelligent.model.patrolstore.query.PatrolStoreCheckQuery;
import com.coolcollege.intelligent.model.patrolstore.request.PatrolStoreReviewExportRequest;
import com.coolcollege.intelligent.model.patrolstore.request.*;
import com.coolcollege.intelligent.model.question.request.ExportRegionQuestionReportRequest;
import com.coolcollege.intelligent.model.question.request.ExportStoreWorkDataRequest;
import com.coolcollege.intelligent.model.question.request.ExportStoreWorkRecordRequest;
import com.coolcollege.intelligent.model.question.request.ExportTbQuestionRecordRequest;
import com.coolcollege.intelligent.model.region.request.ExportExternalRegionRequest;
import com.coolcollege.intelligent.model.supervision.request.SupervisionDataDetailListExportRequest;
import com.coolcollege.intelligent.model.supervision.request.SupervisionTaskDataListExportRequest;
import com.coolcollege.intelligent.model.unifytask.request.PatrolStoreTaskReportExport;
import com.coolcollege.intelligent.model.user.dto.ExportUserRequest;
import com.coolcollege.intelligent.service.export.ExportService;
import com.coolcollege.intelligent.service.export.ExportUtil;
import com.coolcollege.intelligent.service.export.impl.DisplayHasPicExportService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 统一消息监听消费者
 *
 * @author chenyupeng
 * @since 2021/12/23
 */
@Slf4j
@Service
public class ExportImportMessageListener implements MessageListener {

    @Resource
    private ExportService exportService;

    @Autowired
    private SyncUserFacade syncUserFacade;

    @Resource
    private ExportUtil exportUtil;

    @Resource
    private DisplayHasPicExportService displayHasPicExportService;


    @Override
    public Action consume(Message message, ConsumeContext consumeContext) {
        String text = new String(message.getBody());
        if(StringUtils.isBlank(text)){
            log.info("消息体为空,tag:{},messageId:{}",message.getTag(),message.getMsgID());
            return Action.CommitMessage;
        }
        log.info("重试消费次数 messageId：{}，try times：{}", message.getMsgID(), message.getReconsumeTimes());
        log.info("msgUniteDataQueue, reqBody={}", text);
        try {
            MsgUniteData msgUniteData = JSONObject.parseObject(text, MsgUniteData.class);
            MsgUniteDataTypeEnum msgUniteDataTypeEnum = MsgUniteDataTypeEnum.getByCode(msgUniteData.getMsgType());
            if(msgUniteDataTypeEnum == null){
                log.error("msgUniteData 消息类型不存在{}", msgUniteData.getMsgType());
                return Action.CommitMessage;
            }
            switch (msgUniteDataTypeEnum) {
                case PATROL_RECORD_EXPORT:
                    //分开异步导出
                    TableRecordsExportMsg msg = JSONObject.parseObject(msgUniteData.getData(), TableRecordsExportMsg.class);
                    log.info(" TableRecordsExportMsg {}", msg);
                    exportService.tableRecordsListExport(msg.getEnterpriseId(), msg.getTotalNum(), msg.getRequest(), msg.getImportTaskDO());
                    break;
                case EXPORT_BASE_DETAIL_RECORD_EXPORT:
                    //分开异步导出
                    ExportDefTableRequest dataTableQuery = JSONObject.parseObject(msgUniteData.getData(), ExportDefTableRequest.class);
                    log.info(" dataTableQuery {}", dataTableQuery);
                    exportService.recordListDetailExport(dataTableQuery.getEnterpriseId(), dataTableQuery.getTotalNum(), dataTableQuery.getImportTaskDO(), dataTableQuery.getRequest());
                    break;
                case ACHIEVEMENT_DETAIL_EXPORT:
                    //分开异步导出
                    AchievementDetailListExport achievementDetailListExport = JSONObject.parseObject(msgUniteData.getData(), AchievementDetailListExport.class);
                    log.info(" exportBaseTableDetailRequest {}", achievementDetailListExport);
                    exportService.achievementDetailExport(achievementDetailListExport);
                    break;
                case TASK_QUESTION_REPORT:
                    //分开异步导出
                    ExportTaskQuestionRequest exportTaskQuestionRequest = JSONObject.parseObject(msgUniteData.getData(), ExportTaskQuestionRequest.class);
                    log.info(" exportTaskQuestionRequest {}", exportTaskQuestionRequest);
                    exportService.taskQuestionReportExport(exportTaskQuestionRequest.getEnterpriseId(), exportTaskQuestionRequest.getTotalNum(), exportTaskQuestionRequest.getRequest()
                            , exportTaskQuestionRequest.getImportTaskDO());
                    break;
                case EXPORT_TASK_STAGE_LIST_RECORD:
                    //分开异步导出
                    ExportTaskStageRecordListRequest exportTaskStageRecordListRequest = JSONObject.parseObject(msgUniteData.getData(), ExportTaskStageRecordListRequest.class);
                    log.info(" exportTaskStageRecordListRequest {}", exportTaskStageRecordListRequest);
                    exportService.taskStageRecordListExport(exportTaskStageRecordListRequest.getEnterpriseId(),exportTaskStageRecordListRequest.getTotalNum(),
                            exportTaskStageRecordListRequest, exportTaskStageRecordListRequest.getImportTaskDO());
                    break;
                case EXPORT_TASK_STAGE_LIST_RECORD_DETAIL:
                    //分开异步导出
                    ExportTaskStageRecordListDetailRequest exportTaskStageRecordListDetailRequest = JSONObject.parseObject(msgUniteData.getData(), ExportTaskStageRecordListDetailRequest.class);
                    log.info(" exportTaskStageRecordListDetailRequest {}", exportTaskStageRecordListDetailRequest);
                    exportService.taskStageRecordListDetailExport(exportTaskStageRecordListDetailRequest.getEnterpriseId(),exportTaskStageRecordListDetailRequest.getTotalNum(),
                            exportTaskStageRecordListDetailRequest.getBusinessId(), null, exportTaskStageRecordListDetailRequest.getImportTaskDO(), exportTaskStageRecordListDetailRequest.getDbName());
                    break;
                case PATROL_STORE_TASK_REPORT_EXPORT:
                    //巡店任务报表导出
                    PatrolStoreTaskReportExport patrolStoreTaskReportExport = JSONObject.parseObject(msgUniteData.getData(), PatrolStoreTaskReportExport.class);
                    log.info(" PatrolStoreTaskReportExportMsg {}", patrolStoreTaskReportExport);
                    exportService.patrolStoreTaskReportExport(patrolStoreTaskReportExport.getEnterpriseId(), patrolStoreTaskReportExport.getTotalNum(), patrolStoreTaskReportExport.getQuery(), patrolStoreTaskReportExport.getImportTaskDO());
                    break;
                case EXPORT_FILE_COMMON:
                    //分开异步导出
                    ExportMsgSendRequest msgSendRequest = JSONObject.parseObject(msgUniteData.getData(), ExportMsgSendRequest.class);
                    log.info(" 通用导出请求参数 {}", msgSendRequest);
                    exportUtil.doExport(msgSendRequest);
                    break;
                case DISPLAY_HAS_EXPORT:
                    //分开异步导出
                    ExportMsgSendRequest request = JSONObject.parseObject(msgUniteData.getData(), ExportMsgSendRequest.class);
                    log.info(" exportBaseTableDetailRequest {}", request);
                    displayHasPicExportService.doExport(request);
                    break;
                case TB_QUESTION_RECORD:
                    ExportTbQuestionRecordRequest questionRecordRequest = JSONObject.parseObject(msgUniteData.getData(), ExportTbQuestionRecordRequest.class);
                    log.info("question record export request {}", questionRecordRequest);
                    exportService.tbQuestionRecordExport(questionRecordRequest.getEnterpriseId(), questionRecordRequest.getTotalNum(),
                            questionRecordRequest.getImportTaskDO(), questionRecordRequest.getRequest(), questionRecordRequest.getDbName());
                    break;
                case PATROL_STORE_DETAIL:
                    ExportPatrolStoreDetailRequest exportPatrolStoreDetailRequest = JSONObject.parseObject(msgUniteData.getData(), ExportPatrolStoreDetailRequest.class);
                    log.info("PATROL_STORE_DETAIL record export request {}", exportPatrolStoreDetailRequest);
                    exportService.patrolStoreDetailExport(exportPatrolStoreDetailRequest.getEnterpriseId(), exportPatrolStoreDetailRequest.getTotalNum(),
                            exportPatrolStoreDetailRequest.getImportTaskDO(), exportPatrolStoreDetailRequest.getRequest(),exportPatrolStoreDetailRequest.getDbName());
                    break;
                case SUB_QUESTION_DETAIL:
                    ExportTbQuestionRecordRequest questionDetail = JSONObject.parseObject(msgUniteData.getData(), ExportTbQuestionRecordRequest.class);
                    log.info("question record export request {}", questionDetail);
                    exportService.subQuestionDetailListExport(questionDetail.getEnterpriseId(), questionDetail.getTotalNum(),
                            questionDetail.getImportTaskDO(), questionDetail.getRequest(), questionDetail.getDbName());
                    break;
                case REGION_STORE_QUESTION_REPORT:
                    ExportRegionQuestionReportRequest exportRegionQuestionReportRequest = JSONObject.parseObject(msgUniteData.getData(), ExportRegionQuestionReportRequest.class);
                    log.info("question record export request {}", exportRegionQuestionReportRequest);
                    exportService.regionQuestionReportExport(exportRegionQuestionReportRequest.getEnterpriseId(), exportRegionQuestionReportRequest.getTotalNum(),
                            exportRegionQuestionReportRequest.getImportTaskDO(), exportRegionQuestionReportRequest.getRequest(), exportRegionQuestionReportRequest.getDbName());
                    break;
                case QUESTION_LIST:
                    ExportRegionQuestionReportRequest exportRegionQuestionReport = JSONObject.parseObject(msgUniteData.getData(), ExportRegionQuestionReportRequest.class);
                    log.info("question record export request {}", exportRegionQuestionReport);
                    exportService.questionParentInfoListExport(exportRegionQuestionReport.getEnterpriseId(), exportRegionQuestionReport.getTotalNum(),
                            exportRegionQuestionReport.getImportTaskDO(), exportRegionQuestionReport.getQuestionParentRequest(), exportRegionQuestionReport.getDbName());
                break;
                case STOREWORK_STORE_STATISTICS:
                    ExportStoreWorkDataRequest exportStoreWorkDataRequest = JSONObject.parseObject(msgUniteData.getData(), ExportStoreWorkDataRequest.class);
                    log.info("storework record export request {}", exportStoreWorkDataRequest);
                    exportService.storeWorkStoreStatisticsListExport(exportStoreWorkDataRequest.getEnterpriseId(), exportStoreWorkDataRequest.getTotalNum(),
                            exportStoreWorkDataRequest.getImportTaskDO(), exportStoreWorkDataRequest.getRequest(), exportStoreWorkDataRequest.getUser(), exportStoreWorkDataRequest.getDbName());
                    break;
                case STOREWORK_REGION_STATISTICS:
                    ExportStoreWorkDataRequest exportStoreWorkRegionDataRequest = JSONObject.parseObject(msgUniteData.getData(), ExportStoreWorkDataRequest.class);
                    log.info("storework record export request {}", exportStoreWorkRegionDataRequest);
                    exportService.storeWorkRegionStatisticsListExport(exportStoreWorkRegionDataRequest.getEnterpriseId(), exportStoreWorkRegionDataRequest.getTotalNum(),
                            exportStoreWorkRegionDataRequest.getImportTaskDO(), exportStoreWorkRegionDataRequest.getRequest(), exportStoreWorkRegionDataRequest.getUser(), exportStoreWorkRegionDataRequest.getDbName());
                    break;
                case STOREWORK_DAY_STATISTICS:
                    ExportStoreWorkDataRequest exportStoreWorkDayDataRequest = JSONObject.parseObject(msgUniteData.getData(), ExportStoreWorkDataRequest.class);
                    log.info("storework record export request {}", exportStoreWorkDayDataRequest);
                    exportService.storeWorkDayStatisticsListExport(exportStoreWorkDayDataRequest.getEnterpriseId(), exportStoreWorkDayDataRequest.getTotalNum(),
                            exportStoreWorkDayDataRequest.getImportTaskDO(), exportStoreWorkDayDataRequest.getRequest(), exportStoreWorkDayDataRequest.getUser(), exportStoreWorkDayDataRequest.getDbName());
                    break;
                case STOREWORK_STORERECORD_LIST:
                    ExportStoreWorkRecordRequest exportStoreWorkRecordRequest = JSONObject.parseObject(msgUniteData.getData(), ExportStoreWorkRecordRequest.class);
                    log.info("storework record export request {}", exportStoreWorkRecordRequest);
                    exportService.storeWorkRecordListExport(exportStoreWorkRecordRequest.getEnterpriseId(), exportStoreWorkRecordRequest.getTotalNum(),
                            exportStoreWorkRecordRequest.getImportTaskDO(), exportStoreWorkRecordRequest.getRequest(), exportStoreWorkRecordRequest.getUser(), exportStoreWorkRecordRequest.getDbName());
                    break;
                case STOREWORK_STORERECORD_DETAIL_LIST:
                    ExportStoreWorkRecordRequest export = JSONObject.parseObject(msgUniteData.getData(), ExportStoreWorkRecordRequest.class);
                    log.info("storework record export request {}", export);
                    exportService.storeWorkRecordListDetailExport(export.getEnterpriseId(), export.getTotalNum(),
                            export.getImportTaskDO(), export.getRequest(), export.getUser(), export.getDbName());
                    break;
                case STOREWORK_TABLE_LIST:
                    ExportStoreWorkRecordRequest exportStoreWorkRecordTableRequest = JSONObject.parseObject(msgUniteData.getData(), ExportStoreWorkRecordRequest.class);
                    log.info("storework record export request {}", exportStoreWorkRecordTableRequest);
                    exportService.storeWorkTableListExport(exportStoreWorkRecordTableRequest.getEnterpriseId(), exportStoreWorkRecordTableRequest.getTotalNum(),
                            exportStoreWorkRecordTableRequest.getImportTaskDO(), exportStoreWorkRecordTableRequest.getRequest(), exportStoreWorkRecordTableRequest.getDbName(),exportStoreWorkRecordTableRequest.getUser());
                    break;
                case STOREWORK_COLUMN_LIST:
                    ExportStoreWorkRecordRequest exportStoreWorkRecord = JSONObject.parseObject(msgUniteData.getData(), ExportStoreWorkRecordRequest.class);
                    log.info("storework record export request {}", exportStoreWorkRecord);
                    exportService.storeWorkColumnListExport(exportStoreWorkRecord.getEnterpriseId(), exportStoreWorkRecord.getTotalNum(),
                            exportStoreWorkRecord.getImportTaskDO(), exportStoreWorkRecord.getRequest(), exportStoreWorkRecord.getDbName(),exportStoreWorkRecord.getUser());
                    break;
                case DEVICE_LIST:
                    ExportDeviceRequest exportDeviceRequest = JSONObject.parseObject(msgUniteData.getData(), ExportDeviceRequest.class);
                    log.info("exportDeviceRequest export request {}", exportDeviceRequest);
                    exportService.deviceListExport(exportDeviceRequest.getEnterpriseId(), exportDeviceRequest.getTotalNum(),
                            exportDeviceRequest.getImportTaskDO(), exportDeviceRequest.getRequest(), exportDeviceRequest.getDbName(),exportDeviceRequest.getUser());
                    break;
                case DEVICE_SUMMARY_LIST:
                    ExportDeviceSummaryRequest req = JSONObject.parseObject(msgUniteData.getData(), ExportDeviceSummaryRequest.class);
                    log.info("exportDeviceSummaryRequest export request {}", req);
                    exportService.exportDeviceSummaryExport(req.getEnterpriseId(),req.getTotalNum(),req.getImportTaskDO(),req.getRequest(),req.getDbName(),req.getUser());
                    break;
                case SUPERVISION_DATA_EXPORT:
                    SupervisionTaskDataListExportRequest stdler = JSONObject.parseObject(msgUniteData.getData(), SupervisionTaskDataListExportRequest.class);
                    log.info("exportDeviceSummaryRequest export request {}", stdler);
                    exportService.exportSupervisionTask(stdler.getEnterpriseId(),stdler.getTotalNum(),stdler.getImportTaskDO(),stdler.getParentId(),stdler.getUserName(),stdler.getCompleteStatusList(),stdler.getUser(),stdler.getDbName(),stdler.getHandleOverTimeStatus());
                    break;
                case SUPERVISION_DATA_STORE_EXPORT:
                    SupervisionTaskDataListExportRequest ts = JSONObject.parseObject(msgUniteData.getData(), SupervisionTaskDataListExportRequest.class);
                    log.info("exportDeviceSummaryRequest export request {}", ts);
                    exportService.exportSupervisionStoreTask(ts.getEnterpriseId(),ts.getTotalNum(),ts.getImportTaskDO(),ts.getParentId(),ts.getStoreIds(),ts.getUserName(),ts.getCompleteStatusList(),ts.getUser(),ts.getDbName(),ts.getTaskId(),
                            ts.getRegionId(), ts.getHandleOverTimeStatus());
                    break;
                case SUPERVISION_DATA_DETAIL_EXPORT:
                    SupervisionDataDetailListExportRequest sd = JSONObject.parseObject(msgUniteData.getData(), SupervisionDataDetailListExportRequest.class);
                    log.info("exportDeviceSummaryRequest export request {}", sd);
                    exportService.exportSupervisionDataDetail(sd.getEnterpriseId(),sd.getTotalNum(),sd.getImportTaskDO(),sd.getParentIds(),sd.getTbMetaTableId(),sd.getStartTimeDate(),sd.getEndTimeDate(),sd.getType(),sd.getUser(),sd.getDbName());
                    break;
                case CONFIDENCE_FEEDBACK_EXPORT:
                    ExportConfidenceFeedbackRequest exportParam = JSONObject.parseObject(msgUniteData.getData(), ExportConfidenceFeedbackRequest.class);
                    log.info("exportConfidenceFeedbackPage export request {}", exportParam);
                    exportService.exportConfidenceFeedback(exportParam.getEnterpriseId(), exportParam.getTotalNum(),exportParam.getImportTaskDO(),exportParam.getRequest(),exportParam.getUser(), exportParam.getDbName());
                    break;
                case ACTIVITY_USER:
                    ActivityUserDTO activityUserDTO = JSONObject.parseObject(msgUniteData.getData(), ActivityUserDTO.class);
                    log.info("exportActiveUser export request {}", JSONObject.toJSONString(activityUserDTO));
                    exportService.exportActivityUser(activityUserDTO.getEnterpriseId(),activityUserDTO.getImportTaskDO(),activityUserDTO.getActivityId(), activityUserDTO.getDbName());
                    break;
                case ACTIVITY_COMMENT:
                    ActivityCommentExportDTO activityCommentDTO = JSONObject.parseObject(msgUniteData.getData(), ActivityCommentExportDTO.class);
                    log.info("exportActiveUser export request {}", JSONObject.toJSONString(activityCommentDTO));
                    exportService.exportActivityComment(activityCommentDTO.getEnterpriseId(),activityCommentDTO.getImportTaskDO(),activityCommentDTO.getActivityId(),activityCommentDTO.getTotalNum(), activityCommentDTO.getDbName());
                    break;
                case WEEKLY_NEWSPAPER_LIST:
                    QyyWeeklyNewsPaperExportDTO qyyWeeklyNewsPaperExportDTO = JSONObject.parseObject(msgUniteData.getData(), QyyWeeklyNewsPaperExportDTO.class);
                    log.info("WEEKLY_NEWSPAPER_LIST export request {}", JSONObject.toJSONString(qyyWeeklyNewsPaperExportDTO));
                    exportService.exportWeeklyNewspaperList(qyyWeeklyNewsPaperExportDTO.getEnterpriseId(),qyyWeeklyNewsPaperExportDTO.getImportTaskDO(),qyyWeeklyNewsPaperExportDTO.getTotalNum(),qyyWeeklyNewsPaperExportDTO.getDbName());
                    break;
                case PATROL_STORE_REVIEW_LIST_EXPORT:
                    PatrolStoreReviewExportRequest reviewExportRequest = JSONObject.parseObject(msgUniteData.getData(), PatrolStoreReviewExportRequest.class);
                    log.info("PATROL_STORE_REVIEW_LIST_EXPORT export request {}", JSONObject.toJSONString(reviewExportRequest));
                    exportService.exportPatrolStoreReviewList(reviewExportRequest.getEnterpriseId(),reviewExportRequest.getImportTaskDO(),reviewExportRequest.getTotalNum(),reviewExportRequest.getDbName(),reviewExportRequest.getRecordIds());
                    break;
                case EXTERNAL_USER_LIST:
                    ExportUserRequest exportUserRequest = JSONObject.parseObject(msgUniteData.getData(), ExportUserRequest.class);
                    log.info("exportExternalRequest export request {}", exportUserRequest);
                    exportService.externalUserInfoExport(exportUserRequest.getEnterpriseId(), exportUserRequest.getTotalNum(),
                            exportUserRequest.getImportTaskDO(), exportUserRequest.getRequest(), exportUserRequest.getDbName(),exportUserRequest.getUser());
                    break;
                case EXTERNAL_REGION_LIST:
                    ExportExternalRegionRequest exportExternalRegionRequest = JSONObject.parseObject(msgUniteData.getData(), ExportExternalRegionRequest.class);
                    log.info("exportExternalRequest export request {}", exportExternalRegionRequest);
                    exportService.externalRegionExport(exportExternalRegionRequest.getEnterpriseId(), exportExternalRegionRequest.getTotalNum(),
                            exportExternalRegionRequest.getImportTaskDO(), exportExternalRegionRequest.getRequest(), exportExternalRegionRequest.getDbName(),exportExternalRegionRequest.getUser());
                    break;
                case EXPORT_CHECK_LIST:
                    PatrolStoreCheckQuery patrolStoreCheckQuery = JSONObject.parseObject(msgUniteData.getData(), PatrolStoreCheckQuery.class);
                    log.info("EXPORT_CHECK_LIST export request {}", patrolStoreCheckQuery);
                    exportService.exportPatrolStoreCheckList(patrolStoreCheckQuery);
                    break;
                case EXPORT_CHECK_DETAIL_LIST:
                    PatrolStoreCheckQuery query = JSONObject.parseObject(msgUniteData.getData(), PatrolStoreCheckQuery.class);
                    log.info("EXPORT_CHECK_DETAIL_LIST export request {}", query);
                    exportService.exportCheckDetailList(query);
                    break;
                case EXPORT_CHECK_ANALYZE_LIST:
                    PatrolStoreCheckQuery checkQuery = JSONObject.parseObject(msgUniteData.getData(), PatrolStoreCheckQuery.class);
                    log.info("EXPORT_CHECK_ANALYZE_LIST export request {}", checkQuery);
                    exportService.exportCheckAnalyzeList(checkQuery);
                    break;
                default :
                    break;
            }
        }catch (Exception e){
            log.error("ExportImportMessageListener consume error", e);
            return Action.ReconsumeLater;
        }
        log.info("消费成功,tag:{},messageId:{}",message.getTag(),message.getMsgID());
        return Action.CommitMessage;
    }
}
