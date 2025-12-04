package com.coolcollege.intelligent.service.patrolstore;

import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.facade.dto.PageDTO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseSettingDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseStoreCheckSettingDO;
import com.coolcollege.intelligent.model.impoetexcel.ImportTaskDO;
import com.coolcollege.intelligent.model.metatable.TbMetaStaTableColumnDO;
import com.coolcollege.intelligent.model.metatable.TbMetaTableDO;
import com.coolcollege.intelligent.model.metatable.vo.TbRecordVO;
import com.coolcollege.intelligent.model.patrolstore.TbDataStaTableColumnDO;
import com.coolcollege.intelligent.model.patrolstore.TbPatrolStoreHistoryDo;
import com.coolcollege.intelligent.model.patrolstore.TbPatrolStoreRecordDO;
import com.coolcollege.intelligent.model.patrolstore.dto.PatrolOverviewDTO;
import com.coolcollege.intelligent.model.patrolstore.dto.StopTaskDTO;
import com.coolcollege.intelligent.model.patrolstore.param.*;
import com.coolcollege.intelligent.model.patrolstore.query.PatrolStoreStatisticsDataTableQuery;
import com.coolcollege.intelligent.model.patrolstore.records.PatrolRecordAuthDTO;
import com.coolcollege.intelligent.model.patrolstore.request.*;
import com.coolcollege.intelligent.model.patrolstore.statistics.PatrolStoreStatisticsMetaStaColumnVO;
import com.coolcollege.intelligent.model.patrolstore.statistics.PatrolStoreStatisticsMetaStaTableVO;
import com.coolcollege.intelligent.model.patrolstore.vo.*;
import com.coolcollege.intelligent.model.system.SysRoleDO;
import com.coolcollege.intelligent.model.tbdisplay.param.TbDisplayDeleteParam;
import com.coolcollege.intelligent.model.tbdisplay.vo.TbDisplayTableRecordDeleteVO;
import com.coolcollege.intelligent.model.unifytask.TaskParentDO;
import com.coolcollege.intelligent.model.unifytask.TaskStoreDO;
import com.coolcollege.intelligent.model.unifytask.TaskSubDO;
import com.coolcollege.intelligent.model.unifytask.dto.TaskMessageDTO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.service.metatable.calscore.CalColumnScoreDTO;
import com.github.pagehelper.PageInfo;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @author yezhe
 * @date 2020-12-08 19:21
 */
public interface PatrolStoreService {

    boolean addPatrolStoreTask(TaskMessageDTO taskMessageDTO, TaskSubDO taskSubDO);

    /**
     * 完成巡店任务后删除或签的其他巡店任务
     * @param taskMessageDTO
     * @return
     */
    boolean completePatrolStoreTask(TaskMessageDTO taskMessageDTO);

    boolean buildPatrolStore(String enterpriseId, PatrolStoreBuildParam param);

    boolean delPatrolStoreByBusinessIds(String enterpriseId, List<Long> businessIds);

    Long signIn(String enterpriseId, PatrolStoreSignInParam param);

    Long signOut(String dingCorpId,String enterpriseId, PatrolStoreSignOutParam param, EnterpriseStoreCheckSettingDO storeCheckSettingDO, String userId, String userName,String appType, EnterpriseSettingDO enterpriseSettingDO);

    Boolean giveUp(String enterpriseId, Long businessId);

    boolean configMetaTable(String enterpriseId, MetaTableConfigParam param);

    /**
     * 新增表
     * @param enterpriseId
     * @param record
     * @param addMetaTableIds
     */
    void addMetaTable(String enterpriseId, TbPatrolStoreRecordDO record, List<Long> addMetaTableIds);

    Boolean submit(String enterpriseId, PatrolStoreSubmitParam param, String userId);

    Long submitOnline(String dingCorpId,String enterpriseId, PatrolStoreSubmitOnlineParam param, EnterpriseStoreCheckSettingDO storeCheckSettingDO, String userId, String userName,String appType, EnterpriseSettingDO enterpriseSettingDO);

    TbPatrolStoreRecordVO recordInfo(String enterpriseId, Long businessId,String accessToken,String key, EnterpriseStoreCheckSettingDO settingDO);

    List<DataTableInfoDTO> dataTableInfoList(String enterpriseId, Long businessId, String userId);

    List<StoreTaskMapVO> listStoreTaskMap(String enterpriseId, StoreTaskMapParam param);

    TbRecordVO getPatrolMetaTable(String enterpriseId, String storeId, CurrentUser user, Long subTaskId, String patrolType,EnterpriseStoreCheckSettingDO settingDO,
                                  Long businessId);

    /**
     * 获取巡店记录列表
     * @param enterpriseId
     * @param recordListRequest
     * @param currUserRole
     * @return
     */
    @Deprecated
    PageInfo getRecordList(String enterpriseId, RecordListRequest recordListRequest, SysRoleDO currUserRole);

    RecordByCheckColumnIdVO getRecordListByMetaStaColumnId(String enterpriseId, RecordByMetaStaColumnIdRequest request);

    List<QuestionListVO> getSimpleQuestionList(String enterpriseId, QuestionListRequest request);

    List<QuestionListVO> getDefaultSimpleQuestionList(String enterpriseId, QuestionListRequest request, CurrentUser user);

    /**
     * 获取巡店记录列表
     * @param enterpriseId
     * @param patrolRecordRequest
     * @param currentUser
     * @return
     */
    PageInfo getPatrolRecordList(String enterpriseId, PatrolRecordRequest patrolRecordRequest, CurrentUser currentUser);

    PatrolRecordDataVO getPatrolRecordData(String enterpriseId,CurrentUser currentUser);

    PageInfo getStaffPlanPatrolRecordList(String enterpriseId, StaffPlanPatrolRecordRequest staffPlanPatrolRecordRequest, CurrentUser currentUser);

    PatrolRecordAuthDTO getRecordAuth(String enterpriseId, CurrentUser currentUser, Long businessId, Long subTaskId);

    List<QuestionListVO> getOperateQuestionList(String enterpriseId, QuestionListRequest request);

    TbPatrolStoreRecordVO taskRecordInfo(String enterpriseId, Long taskStoreId, Long businessId);

//    /**
//     * 根据区域获取工单列表
//     * @param enterpriseId
//     * @param request
//     * @return
//     */
//    List<QuestionListVO> getSimpleQuestionListByRegion(String enterpriseId, QuestionListRequest request);


    PageInfo taskStageRecordList(String enterpriseId, Long unifyTaskId, Long loopCount, Integer pageNum, Integer pageSize, Boolean levelInfo);


    List<PatrolStoreStatisticsMetaStaTableVO> statisticsStaTableDataList(String enterpriseId,
                                                                         List<TbPatrolStoreRecordDO> tableRecordDOList, TaskParentDO taskParentDO,
                                                                         Boolean levelInfo, Boolean isColumn, List<Long> metaTableIds, String businessType);

    PatrolStoreStatisticsMetaStaColumnVO  taskStageRecordDetailList(String enterpriseId, Long businessId, Long metaTableId, Integer pageNum, Integer pageSize);

    void countScore(String eid, TbPatrolStoreRecordDO recordDO, TbMetaTableDO tbMetaTable, Long dataTableId);

    void countPatrolStoreRecordScore(String enterpriseId, PatrolStoreSubmitParam param, String businessCheckType);


    List<CalColumnScoreDTO> buildCalColumnStore(List<TbDataStaTableColumnDO> dataStaTableColumnList, Map<Long, TbMetaStaTableColumnDO> metaTableColumnMap);

    /**
     * 巡店记录转交
     * @param enterpriseId
     * @param recordTurnRequest
     * @param user
     */
    void turnPatrolStoreRecord(String enterpriseId, PatrolStoreRecordTurnRequest recordTurnRequest, CurrentUser user);
    /**
     * 巡店记录审核
     */
    void audit(String enterpriseId, CurrentUser user, PatrolStoreAuditParam patrolStoreAuditParam);

    /**
     * 结束巡店或者提交审核
     * @param businessId 巡店记录id
     */
    void overPatrol(String enterpriseId, Long businessId, String userId, String userName, String dingCorpId, Boolean isSignOut,String appType, String signatureUser,EnterpriseStoreCheckSettingDO storeCheckSettingDO, EnterpriseSettingDO enterpriseSettingDO);

    /**
     * 结束巡店或者提交审核
     * @param businessId 巡店记录id
     */
    void completePotral(String enterpriseId, Long businessId, String userId, String userName, Long subTaskId);

    /**
     * 查询巡店处理列表
     * @param enterpriseId
     * @param businessId
     * @return
     */
    List<TbPatrolStoreHistoryDo> selectPatrolStoreHistoryList(String enterpriseId, String businessId);

    /**
     * 巡店记录详情分享时间留存
     * @param enterpriseId
     * @param businessId
     * @return
     */
    Boolean recordInfoShare(String enterpriseId, Long businessId,String key);

    PageInfo getAutonomyPatrolRecordList(String enterpriseId, PatrolRecordRequest patrolRecordRequest, CurrentUser currentUser);

    /**
     * 重新分配
     * @author chenyupeng
     * @date 2021/11/22
     * @param enterpriseId
     * @param taskStoreDO
     * @param operUserId
     */
    void reallocatePatrolTask(String enterpriseId, TaskStoreDO taskStoreDO, String operUserId);

    PageInfo getGroupDataByStore(String enterpriseId, RecordByMetaStaColumnIdRequest request);
    /**
     * 已完成巡店记录
     * @param enterpriseId
     * @param patrolRecordRequest
     * @return
     */
    PageInfo getCompletePatrolRecordList(String enterpriseId, PatrolRecordRequest patrolRecordRequest);


    PageDTO<PatrolStoreDetailExportVO> getPatrolStoreDetail(String enterpriseId,PatrolStoreDetailRequest patrolStoreDetailRequest);

    ResponseResult<List<String>> patrolStoreReminder(String enterpriseId, Long taskId, String appType);

    /**
     * 巡店复审
     * @param enterpriseId
     * @param businessId
     * @param userId
     * @return
     */
    Long recheckPatrol(String enterpriseId, Long businessId, String userId, String userName);

    /**
     * 复审概览
     * @param enterpriseId
     * @param beginTime
     * @param endTime
     * @param userId
     * @return
     */
    PatrolOverviewDTO recheckOverview(String enterpriseId, Long beginTime, Long endTime, String userId, String dbName);

    ImportTaskDO patrolStoreReviewListExport(CurrentUser user, String enterpriseId, PatrolStoreStatisticsDataTableQuery query);

    void checkVideoHandel(List<TbDataStaTableColumnDO> tbDataStaTableColumnDOList, String enterpriseId);

    /**
     * 删除门店任务
     * @param enterpriseId
     * @param tbDisplayDeleteParam
     * @return
     */
    void deleteRecord(String enterpriseId, TbDisplayDeleteParam tbDisplayDeleteParam, CurrentUser currentUser,String isDone, EnterpriseConfigDO config);

    PageInfo<TbDisplayTableRecordDeleteVO> getDeleteRecordList(String enterpriseId, Long unifyTaskId, Integer pageNum, Integer pageSize,String unifyTaskIds,
                                                               String taskStatus);

    /**
     * 停止任务
     * @param enterpriseId
     * @param stopTaskDTO
     * @return
     */
    boolean stopTask(String enterpriseId, StopTaskDTO stopTaskDTO, EnterpriseConfigDO enterpriseConfig);

    Map<String, Object> getTaskDetail(String enterpriseId, Long businessId, EnterpriseConfigDO enterpriseConfig);

    List<Long> deleteRecords(String enterpriseId, TbDisplayDeleteParam displayDeleteParam, CurrentUser user, String done);

    String getCheckResultLevel(Integer passNum, TbMetaTableDO tableDO, BigDecimal score, BigDecimal taskCalTotalScore);

    Long getBusinessId(String enterpriseId, Long unifyTaskId, String storeId, Long loopCount);

    void cancelUpcoming(String enterpriseId, List<Long> subTaskIdList, String dingCorpId, String appType);
}
