package com.coolcollege.intelligent.service.export.impl;

import cn.afterturn.easypoi.entity.vo.NormalExcelConstants;
import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import cn.afterturn.easypoi.excel.entity.params.ExcelExportEntity;
import cn.afterturn.easypoi.excel.entity.vo.BaseEntityTypeConstants;
import cn.afterturn.easypoi.excel.export.ExcelExportService;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollStreamUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.constant.RedisConstant;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.enums.baili.BailiCustomizeFieldEnum;
import com.coolcollege.intelligent.common.enums.baili.BailiEnterpriseEnum;
import com.coolcollege.intelligent.common.enums.importexcel.ImportTaskStatusEnum;
import com.coolcollege.intelligent.common.enums.meta.MetaColumnTypeEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.page.PageVO;
import com.coolcollege.intelligent.common.util.DateUtil;
import com.coolcollege.intelligent.common.util.DateUtils;
import com.coolcollege.intelligent.common.util.OpenApiParamCheckUtils;
import com.coolcollege.intelligent.dao.achievement.AchievementDetailMapper;
import com.coolcollege.intelligent.dao.achievement.AchievementTypeMapper;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseSettingMapper;
import com.coolcollege.intelligent.dao.metatable.TbMetaDefTableColumnMapper;
import com.coolcollege.intelligent.dao.metatable.TbMetaStaTableColumnMapper;
import com.coolcollege.intelligent.dao.metatable.TbMetaTableMapper;
import com.coolcollege.intelligent.dao.patrolstore.TbDataDefTableColumnMapper;
import com.coolcollege.intelligent.dao.patrolstore.TbDataStaTableColumnMapper;
import com.coolcollege.intelligent.dao.patrolstore.TbDataTableMapper;
import com.coolcollege.intelligent.dao.patrolstore.TbPatrolStoreRecordMapper;
import com.coolcollege.intelligent.dao.patrolstore.dao.TbDataStaTableColumnDao;
import com.coolcollege.intelligent.dao.store.StoreMapper;
import com.coolcollege.intelligent.dao.system.dao.SysRoleDao;
import com.coolcollege.intelligent.dao.unifytask.TaskMappingMapper;
import com.coolcollege.intelligent.dao.unifytask.TaskParentMapper;
import com.coolcollege.intelligent.facade.dto.PageDTO;
import com.coolcollege.intelligent.mapper.achieve.qyy.QyyWeeklyNewspaperDAO;
import com.coolcollege.intelligent.model.achievement.dto.AchievementDetailExportDTO;
import com.coolcollege.intelligent.model.achievement.entity.AchievementTypeDO;
import com.coolcollege.intelligent.model.achievement.qyy.dto.ConfidenceFeedbackExportDTO;
import com.coolcollege.intelligent.model.achievement.qyy.dto.ConfidenceFeedbackPageDTO;
import com.coolcollege.intelligent.model.achievement.qyy.dto.StoreNewsPaperDTO;
import com.coolcollege.intelligent.model.achievement.qyy.vo.ConfidenceFeedbackDetailVO;
import com.coolcollege.intelligent.model.achievement.request.AchievementDetailListExport;
import com.coolcollege.intelligent.model.achievement.request.AchievementDetailListRequest;
import com.coolcollege.intelligent.model.activity.vo.ActivityCommentExportVO;
import com.coolcollege.intelligent.model.activity.vo.ActivityUserVO;
import com.coolcollege.intelligent.model.device.dto.ChannelDTO;
import com.coolcollege.intelligent.model.device.dto.DeviceMappingDTO;
import com.coolcollege.intelligent.model.device.dto.DeviceSummaryListDTO;
import com.coolcollege.intelligent.model.device.request.DeviceListRequest;
import com.coolcollege.intelligent.model.device.request.DeviceReportSearchRequest;
import com.coolcollege.intelligent.model.enterprise.EnterpriseSettingDO;
import com.coolcollege.intelligent.model.enterprise.dto.UserRoleDTO;
import com.coolcollege.intelligent.model.enums.AIBusinessModuleEnum;
import com.coolcollege.intelligent.model.enums.SupervisionSubTaskStatusEnum;
import com.coolcollege.intelligent.model.enums.TaskTypeEnum;
import com.coolcollege.intelligent.model.export.dto.ExternalRegionExportDTO;
import com.coolcollege.intelligent.model.export.dto.ExternalUserInfoExportDTO;
import com.coolcollege.intelligent.model.export.dto.UserInfoExportDTO;
import com.coolcollege.intelligent.model.export.request.UserInfoExportRequest;
import com.coolcollege.intelligent.model.impoetexcel.ImportTaskDO;
import com.coolcollege.intelligent.model.metatable.TbMetaDefTableColumnDO;
import com.coolcollege.intelligent.model.metatable.TbMetaStaTableColumnDO;
import com.coolcollege.intelligent.model.metatable.TbMetaTableDO;
import com.coolcollege.intelligent.model.page.PageRequest;
import com.coolcollege.intelligent.model.patrolstore.TbDataDefTableColumnDO;
import com.coolcollege.intelligent.model.patrolstore.TbDataStaTableColumnDO;
import com.coolcollege.intelligent.model.patrolstore.TbDataTableDO;
import com.coolcollege.intelligent.model.patrolstore.TbPatrolStoreRecordDO;
import com.coolcollege.intelligent.model.patrolstore.query.PatrolStoreCheckQuery;
import com.coolcollege.intelligent.model.patrolstore.query.PatrolStoreStatisticsDataTableQuery;
import com.coolcollege.intelligent.model.patrolstore.records.PatrolStoreRecordsTableAndPicDTO;
import com.coolcollege.intelligent.model.patrolstore.request.ExportTaskStageRecordListRequest;
import com.coolcollege.intelligent.model.patrolstore.request.PatrolStoreDetailRequest;
import com.coolcollege.intelligent.model.patrolstore.request.TableRecordsRequest;
import com.coolcollege.intelligent.model.patrolstore.statistics.*;
import com.coolcollege.intelligent.model.patrolstore.vo.PatrolStoreDetailExportVO;
import com.coolcollege.intelligent.model.patrolstore.vo.PatrolStoreReviewVO;
import com.coolcollege.intelligent.model.question.request.QuestionParentRequest;
import com.coolcollege.intelligent.model.question.request.RegionQuestionReportRequest;
import com.coolcollege.intelligent.model.question.request.TbQuestionRecordSearchRequest;
import com.coolcollege.intelligent.model.question.vo.*;
import com.coolcollege.intelligent.model.qyy.QyyWeeklyNewspaperDO;
import com.coolcollege.intelligent.model.region.RegionDO;
import com.coolcollege.intelligent.model.region.dto.RegionNode;
import com.coolcollege.intelligent.model.region.request.ExternalRegionExportRequest;
import com.coolcollege.intelligent.model.store.StoreDO;
import com.coolcollege.intelligent.model.store.dto.StorePathDTO;
import com.coolcollege.intelligent.model.store.vo.ExtendFieldInfoVO;
import com.coolcollege.intelligent.model.ai.AIConfigDTO;
import com.coolcollege.intelligent.model.storework.dto.SwStoreWorkColumnResultDTO;
import com.coolcollege.intelligent.model.storework.dto.SwStoreWorkTableDTO;
import com.coolcollege.intelligent.model.storework.request.StoreWorkDataListRequest;
import com.coolcollege.intelligent.model.storework.request.StoreWorkRecordListRequest;
import com.coolcollege.intelligent.model.storework.vo.*;
import com.coolcollege.intelligent.model.supervision.dto.SupervisionDefDataColumnDTO;
import com.coolcollege.intelligent.model.supervision.vo.SupervisionStoreTaskDataVO;
import com.coolcollege.intelligent.model.supervision.vo.SupervisionTaskDataVO;
import com.coolcollege.intelligent.model.supervision.vo.SupervisionTaskParentDetailVO;
import com.coolcollege.intelligent.model.unifytask.TaskParentDO;
import com.coolcollege.intelligent.model.unifytask.dto.UnifyFormDataDTO;
import com.coolcollege.intelligent.model.unifytask.query.TaskQuestionQuery;
import com.coolcollege.intelligent.model.unifytask.query.TaskReportQuery;
import com.coolcollege.intelligent.model.unifytask.vo.TaskQuestionVO;
import com.coolcollege.intelligent.model.unifytask.vo.TaskReportExportBaseVO;
import com.coolcollege.intelligent.model.unifytask.vo.TaskReportExportVO;
import com.coolcollege.intelligent.model.unifytask.vo.TaskReportVO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.model.video.SmallVideoDTO;
import com.coolcollege.intelligent.model.video.SmallVideoInfoDTO;
import com.coolcollege.intelligent.service.achievement.qyy.ConfidenceFeedbackService;
import com.coolcollege.intelligent.service.activity.ActivityService;
import com.coolcollege.intelligent.service.device.DeviceService;
import com.coolcollege.intelligent.service.enterprise.EnterpriseUserService;
import com.coolcollege.intelligent.service.export.CustomExcelExportUtil;
import com.coolcollege.intelligent.service.export.ExportService;
import com.coolcollege.intelligent.service.export.ExportUtil;
import com.coolcollege.intelligent.service.importexcel.ExportAsyncService;
import com.coolcollege.intelligent.service.importexcel.ImportTaskService;
import com.coolcollege.intelligent.service.patrolstore.PatrolStoreRecordsService;
import com.coolcollege.intelligent.service.patrolstore.PatrolStoreService;
import com.coolcollege.intelligent.service.patrolstore.PatrolStoreStatisticsService;
import com.coolcollege.intelligent.service.question.QuestionParentInfoService;
import com.coolcollege.intelligent.service.question.QuestionRecordService;
import com.coolcollege.intelligent.service.region.RegionService;
import com.coolcollege.intelligent.service.storework.StoreWorkRecordService;
import com.coolcollege.intelligent.service.supervison.SupervisionTaskParentService;
import com.coolcollege.intelligent.service.unifytask.UnifyTaskReportService;
import com.coolcollege.intelligent.service.unifytask.UnifyTaskService;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.coolcollege.intelligent.util.patrolStore.TableTypeUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.coolcollege.intelligent.model.enums.TaskTypeEnum.TB_DISPLAY_TASK;
import static com.coolcollege.intelligent.model.metatable.MetaTableConstant.BusinessTypeConstant.PATROL_STORE;

/**
 * @author byd
 * @date 2021-05-20 21:14
 */
@Slf4j
@Service
public class ExportServiceImpl implements ExportService {

    @Autowired
    private PatrolStoreRecordsService patrolStoreRecordsService;
    @Resource
    private ExportAsyncService exportAsyncService;
    @Resource
    private PatrolStoreStatisticsService patrolStoreStatisticsService;
    @Resource
    private PatrolStoreService patrolStoreService;
    @Resource
    private TbPatrolStoreRecordMapper tbPatrolStoreRecordMapper;
    @Resource
    private TbDataStaTableColumnMapper tbDataStaTableColumnMapper;
    @Resource
    private TbDataDefTableColumnMapper tbDataDefTableColumnMapper;
    @Resource
    private ImportTaskService importTaskService;
    @Resource
    private UnifyTaskService unifyTaskService;
    @Resource
    private AchievementDetailMapper achievementDetailMapper;
    @Resource
    private AchievementTypeMapper achievementTypeMapper;
    @Resource
    private TaskParentMapper taskParentMapper;
    @Autowired
    private UnifyTaskReportService unifyTaskReportService;
    @Resource
    private TaskMappingMapper taskMappingMapper;
    @Resource
    private TbMetaTableMapper tbMetaTableMapper;
    @Resource
    private TbMetaStaTableColumnMapper tbMetaStaTableColumnMapper;
    @Resource
    private TbMetaDefTableColumnMapper tbMetaDefTableColumnMapper;
    @Autowired
    QuestionParentInfoService questionParentInfoService;
    @Resource
    private QuestionRecordService questionRecordService;

    @Autowired
    private RegionService regionService;

    @Resource
    private DeviceService deviceService;

    @Resource
    private StoreMapper storeMapper;

    @Resource
    private SysRoleDao sysRoleDao;
    @Resource
    StoreWorkRecordService storeWorkRecordService;
    @Resource
    private TbDataTableMapper tbDataTableMapper;
    @Resource
    SupervisionTaskParentService supervisionTaskParentService;
    @Resource
    private ConfidenceFeedbackService confidenceFeedbackService;
    @Resource
    ActivityService activityService;
    @Resource
    private TbDataStaTableColumnDao tbDataStaTableColumnDao;
    @Resource
    private QyyWeeklyNewspaperDAO qyyWeeklyNewspaperDAO;
    @Resource
    private UserExportService userExportService;
    @Resource
    private RedisUtilPool redisUtilPool;
    @Resource
    private EnterpriseUserService enterpriseUserService;
    @Resource
    private EnterpriseSettingMapper enterpriseSettingMapper;

    @Value("${coolstore.url.h5:'https://store-h5.coolstore.cn'}")
    private String h5Url;

    @Override
    public void tableRecordsListExport(String eid, Long totalNum, TableRecordsRequest request, ImportTaskDO task) {

        try {
            DataSourceHelper.changeToSpecificDataSource(request.getDbName());

            int pageSize = Constants.PAGE_SIZE;
            long pages = (totalNum + pageSize - 1) / pageSize;
            // 设置excel的基本参数
            ExportParams params = new ExportParams(null, null, ExcelType.XSSF);
            Workbook workbook = null;
            for (int pageNum = 1; pageNum <= pages; pageNum++) {
                request.setPageNum(pageNum);
                request.setPageSize(Constants.PAGE_SIZE);
                List<PatrolStoreRecordsTableAndPicDTO> result = patrolStoreRecordsService.tableRecordsListExport(eid, request);
                if (CollectionUtils.isEmpty(result)) {
                    break;
                }
                workbook = ExcelExportUtil.exportBigExcel(params, PatrolStoreRecordsTableAndPicDTO.class, result);
                result.clear();
            }

            if (workbook != null) {
                try {
                    ExcelExportUtil.closeExportBigExcel();
                } catch (Exception ex) {
                    log.error("EXCEL文件导出对象关闭异常", ex);
                }
                exportAsyncService.asyncDynamicExportListFile(eid, task, workbook, request.getDbName());
            } else {
                log.error("查询不到数据");
                task.setStatus(ImportTaskStatusEnum.ERROR.getCode());
                task.setRemark("查询不到数据");
            }
        } catch (Throwable e) {
            log.error("获取数据失败", e);
            task.setStatus(ImportTaskStatusEnum.ERROR.getCode());
            task.setRemark("导出数据异常");
        } finally {
            try {
                ExcelExportUtil.closeExportBigExcel();
            } catch (Exception ex) {
                log.error("EXCEL文件导出对象关闭异常", ex);
            }
            importTaskService.updateImportTask(eid, task);
        }
    }

    @Override
    public void achievementDetailExport(AchievementDetailListExport achievementDetailListExport) {
        DataSourceHelper.changeToSpecificDataSource(achievementDetailListExport.getDbName());
        AchievementDetailListRequest request = achievementDetailListExport.getRequest();
        String enterpriseId = achievementDetailListExport.getEnterpriseId();
        Date beginDate = request.getBeginDate();
        //获取结束日期
        Date endDate = request.getEndDate();
        List<String> storeIds = request.getStoreIds();
        List<Long> typeIds = request.getAchievementTypeIds();
        List<String> produceUserIds = request.getProduceUserIds();
        Long totalNum = achievementDetailListExport.getTotalNum();
        Boolean isNullProduceUser = request.getIsNullProduceUser();
        ImportTaskDO task = achievementDetailListExport.getImportTaskDO();
        try {
            int pageSize = Constants.PAGE_SIZE;
            long pages = (totalNum + pageSize - 1) / pageSize;
            // 设置excel的基本参数
            ExportParams params = new ExportParams(null, null, ExcelType.XSSF);
            Workbook workbook = null;
            for (int pageNum = 1; pageNum <= pages; pageNum++) {
                PageHelper.startPage(pageNum, Constants.PAGE_SIZE, false);
                List<AchievementDetailExportDTO> exportList = achievementDetailMapper.exportList(enterpriseId, beginDate, endDate, storeIds, typeIds, produceUserIds, isNullProduceUser);
                if (CollectionUtils.isEmpty(exportList)) {
                    break;
                }
                List<Long> typeIdList = exportList.stream().map(data -> data.getTypeId()).collect(Collectors.toList());
                List<AchievementTypeDO> typeDOList = new ArrayList<>();
                //查询分类
                if (CollectionUtils.isNotEmpty(typeIdList)) {
                    typeDOList = achievementTypeMapper.getListById(enterpriseId, typeIdList);
                }
                Map<Long, String> typeNameMap = new HashMap();
                //分类map构建
                for (AchievementTypeDO achievementTypeDO : typeDOList) {
                    typeNameMap.put(achievementTypeDO.getId(), achievementTypeDO.getName());
                }
                //导出结果构建
                exportList.stream().forEach(data -> {
                    data.setAchievementTypeName(typeNameMap.get(data.getTypeId()));
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    data.setCreateDate(format.format(data.getCreateTime()));
                    SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
                    data.setProduceDate(format1.format(data.getProduceTime()));
                });
                workbook = ExcelExportUtil.exportBigExcel(params, AchievementDetailExportDTO.class, exportList);
            }
            if (workbook != null) {
                try {
                    ExcelExportUtil.closeExportBigExcel();
                } catch (Exception ex) {
                    log.error("EXCEL文件导出对象关闭异常", ex);
                }
                exportAsyncService.asyncDynamicExportListFile(enterpriseId, task, workbook, achievementDetailListExport.getDbName());
            } else {
                log.error("查询不到数据");
                task.setStatus(ImportTaskStatusEnum.ERROR.getCode());
                task.setRemark("查询不到数据");
            }
        } catch (Throwable e) {
            log.error("获取数据失败", e);
            task.setStatus(ImportTaskStatusEnum.ERROR.getCode());
            task.setRemark("导出数据异常");
        } finally {
            try {
                ExcelExportUtil.closeExportBigExcel();
            } catch (Exception ex) {
                log.error("EXCEL文件导出对象关闭异常", ex);
            }
            importTaskService.updateImportTask(enterpriseId, task);
        }
    }

    @Override
    public void taskQuestionReportExport(String eid, Long totalNum, TaskQuestionQuery query, ImportTaskDO task) {
        try {
            DataSourceHelper.changeToSpecificDataSource(query.getDbName());

            int pageSize = Constants.PAGE_SIZE;
            long pages = (totalNum + pageSize - 1) / pageSize;
            // 设置excel的基本参数
            ExportParams params = new ExportParams(null, null, ExcelType.XSSF);
            Workbook workbook = null;
            for (int pageNum = 1; pageNum <= pages; pageNum++) {
                PageInfo result = unifyTaskService.taskQuestionReportList(eid, query.getUserIdList(), query.getBeginTime(), query.getEndTime(), pageNum, pageSize);
                if (result == null || CollectionUtils.isEmpty(result.getList())) {
                    break;
                }
                workbook = ExcelExportUtil.exportBigExcel(params, TaskQuestionVO.class, result.getList());
                result.getList().clear();
            }

            if (workbook != null) {
                try {
                    ExcelExportUtil.closeExportBigExcel();
                } catch (Exception ex) {
                    log.error("EXCEL文件导出对象关闭异常", ex);
                }
                exportAsyncService.asyncDynamicExportListFile(eid, task, workbook, query.getDbName());
            } else {
                log.error("查询不到数据");
                task.setStatus(ImportTaskStatusEnum.ERROR.getCode());
                task.setRemark("查询不到数据");
            }
        } catch (Throwable e) {
            log.error("获取数据失败", e);
            task.setStatus(ImportTaskStatusEnum.ERROR.getCode());
            task.setRemark("导出数据异常");
        } finally {
            try {
                ExcelExportUtil.closeExportBigExcel();
            } catch (Exception ex) {
                log.error("EXCEL文件导出对象关闭异常", ex);
            }
            importTaskService.updateImportTask(eid, task);
        }
    }

    @Override
    public void taskStageRecordListExport(String eid, Long totalNum, ExportTaskStageRecordListRequest request, ImportTaskDO task) {
        PatrolStoreStatisticsDataTableQuery query = request.getRequest();
        try {
            DataSourceHelper.changeToSpecificDataSource(query.getDbName());

            int pageSize = Constants.PAGE_SIZE;
            long pages = (totalNum + pageSize - 1) / pageSize;
            // 设置excel的基本参数
            ExportParams params = new ExportParams(null, null, ExcelType.XSSF);
            TaskParentDO taskParentDO = null;
            if (query.getType() == null || query.getType() == 2) {
                // 检查表ids
                List<UnifyFormDataDTO> unifyFormDataDTOList =
                        taskMappingMapper.selectMappingDataByTaskId(eid, query.getTaskId());
                taskParentDO = taskParentMapper.selectParentTaskById(eid, query.getTaskId());
                task.setFileName(taskParentDO.getTaskName() + "_" + task.getFileName());
            }
            Workbook workbook = null;

            Boolean isColumn = true;
            if (query.getType() != null && query.getType() == 1) {
                isColumn = false;
            }
            List<ExcelExportEntity> beanList;
            if (StringUtils.isNotBlank(query.getPatrolType()) && TaskTypeEnum.PATROL_STORE_SAFETY_CHECK.getCode().equals(query.getPatrolType())) {
                beanList = getSafetyCheckExportEntityList(query);
            }else {
                beanList = getPotralRecordExportEntityList(query);
            }
            boolean isAddRegion = false;
            for (int pageNum = 1; pageNum <= pages; pageNum++) {
                PageHelper.startPage(pageNum, Constants.PAGE_SIZE, false);
                List<TbPatrolStoreRecordDO> tableRecordDOList =
                        tbPatrolStoreRecordMapper.statisticsDataTable(eid, query, null);


                List<PatrolStoreStatisticsMetaStaTableVO> result = patrolStoreService.statisticsStaTableDataList(eid, tableRecordDOList, taskParentDO, query.getLevelInfo(), isColumn, query.getMetaTableIds(), null);

                if (CollectionUtils.isEmpty(result)) {
                    break;
                }
                //处理人列表
                Set<String> userIdSet = result.stream().map(PatrolStoreStatisticsMetaStaTableVO::getSupervisorId).filter(StringUtils::isNotBlank).collect(Collectors.toSet());

                Map<String, String> userRoleNameMap = new HashMap<>();
                //数据表展示职位
                if (query.getType() == null || query.getType() == 1) {
                    List<UserRoleDTO> userRoleDTOList = sysRoleDao.getUserRoleNameByUserIdList(eid, new ArrayList<>(userIdSet));
                    if (CollectionUtils.isNotEmpty(userRoleDTOList)) {
                        userRoleNameMap = userRoleDTOList.stream()
                                .collect(Collectors.toMap(UserRoleDTO::getUserId, UserRoleDTO::getRoleName));
                    }
                }

                if (!isAddRegion) {
                    //10级区域
                    beanList.addAll(ExportUtil.getTenRegionExportEntityList());
                    isAddRegion = true;
                }
                List<Map<String, Object>> resultMap = new ArrayList<>();
                for (PatrolStoreStatisticsMetaStaTableVO vo : result) {
                    vo.setSummaryPicture(vo.getSummaryInfo());
                    Map<String, String> finalUserRoleNameMap = userRoleNameMap;
                    if(CollectionUtils.isEmpty(vo.getDataTableVOList())){
                        Map<String, Object> map = BeanUtil.beanToMap(vo);
                        //数据表
                        if (query.getType() == null || query.getType() == 1) {
                            map.put("supervisorPositionName", finalUserRoleNameMap.get(vo.getSupervisorId()));
                        }
                        //10级区域
                        List<String> regionNameList = vo.getRegionNameList();
                        if (CollectionUtils.isNotEmpty(regionNameList)) {
                            int i = 0;
                            for (String regionName : regionNameList) {
                                map.put(Constants.EXPORT_REGION_CODE + i, regionName);
                                i++;
                            }
                        }
                        resultMap.add(map);
                    }
                    vo.getDataTableVOList().forEach(patrolDataTableVO -> {
                        Map<String, Object> map = BeanUtil.beanToMap(vo);
                        map.put("metaTableName", patrolDataTableVO.getMetaTableName());
                        map.put("totalColumnCount", patrolDataTableVO.getTotalColumnCount());
                        map.put("passColumnCount", patrolDataTableVO.getPassColumnCount());
                        map.put("failColumnCount", patrolDataTableVO.getFailColumnCount());
                        map.put("inapplicableColumnCount", patrolDataTableVO.getInapplicableColumnCount());
                        map.put("totalScore", patrolDataTableVO.getTaskCalTotalScore());
                        map.put("score", patrolDataTableVO.getScore());
                        map.put("allColumnCheckScore", patrolDataTableVO.getAllColumnCheckScore());
                        map.put("percent", patrolDataTableVO.getScore());
                        map.put("checkResult", patrolDataTableVO.getCheckResult());
                        map.put("rewardPenaltMoney", patrolDataTableVO.getRewardPenaltMoney());
                        //表单已提交
                        if (patrolDataTableVO.getSubmitStatus() != null && (patrolDataTableVO.getSubmitStatus() & 1) == 1) {
                            map.put("percent", patrolDataTableVO.getPercent());
                            map.put("allColumnCheckScorePercent", patrolDataTableVO.getAllColumnCheckScorePercent());
                        }
                        //数据表
                        if (query.getType() == null || query.getType() == 1) {
                            map.put("supervisorPositionName", finalUserRoleNameMap.get(vo.getSupervisorId()));
                        }
                        map.put("recordInfoShareUrl", MessageFormat.format(h5Url + Constants.PATROL_STORE_RECORD_SHARE_SUFFIX, String.valueOf(vo.getId()), vo.getStoreId(), eid));
                        //10级区域
                        List<String> regionNameList = vo.getRegionNameList();
                        if (CollectionUtils.isNotEmpty(regionNameList)) {
                            int i = 0;
                            for (String regionName : regionNameList) {
                                map.put(Constants.EXPORT_REGION_CODE + i, regionName);
                                i++;
                            }
                        }
                        resultMap.add(map);
                    });
                }
                workbook = ExcelExportUtil.exportBigExcel(params, beanList, resultMap);
                result.clear();
                resultMap.clear();
            }
            if (workbook != null) {
                try {
                    ExcelExportUtil.closeExportBigExcel();
                } catch (Exception ex) {
                    log.error("EXCEL文件导出对象关闭异常", ex);
                }
                exportAsyncService.asyncDynamicExportListFile(eid, task, workbook, (query.getDbName()));
            } else {
                log.error("查询不到数据");
                task.setStatus(ImportTaskStatusEnum.ERROR.getCode());
                task.setRemark("查询不到数据");
            }
        } catch (Throwable e) {
            log.error("获取数据失败", e);
            task.setStatus(ImportTaskStatusEnum.ERROR.getCode());
            task.setRemark("导出数据异常");
        } finally {
            try {
                ExcelExportUtil.closeExportBigExcel();
            } catch (Exception ex) {
                log.error("EXCEL文件导出对象关闭异常", ex);
            }
            DataSourceHelper.changeToSpecificDataSource(query.getDbName());
            importTaskService.updateImportTask(eid, task);
        }
    }

    @Override
    public void patrolStoreDetailExport(String eid, Long totalNum, ImportTaskDO task, PatrolStoreDetailRequest request, String dbName) {

        try {
            DataSourceHelper.changeToSpecificDataSource(dbName);

            //校验必填参数
            OpenApiParamCheckUtils.checkNecessaryParam(request.getBeginTime(), request.getEndTime(), request.getMetaTableId());
            OpenApiParamCheckUtils.checkTime(request.getBeginTime().getTime(), request.getEndTime().getTime());
            int pageSize = Constants.PAGE_SIZE;
            long pages = (totalNum + pageSize - 1) / pageSize;
            // 设置excel的基本参数
            ExportParams params = new ExportParams(null, null, ExcelType.XSSF);

            Long metaTableId;
            TaskParentDO taskParentDO = null;

            Workbook workbook = null;


            List<ExcelExportEntity> beanList = new ArrayList<>();
            //数据表
            beanList.add(new ExcelExportEntity("序号", "index"));
            beanList.add(new ExcelExportEntity("巡店人", "patrolStoreUserName"));
            beanList.add(new ExcelExportEntity("巡店人职位", "patrolStoreUserRoleName"));
            beanList.add(new ExcelExportEntity("门店名称", "storeName"));
            beanList.add(new ExcelExportEntity("门店编号", "storeNum"));

            if (BailiEnterpriseEnum.bailiAffiliatedCompany(eid)) {
                beanList.add(new ExcelExportEntity("品牌", BailiCustomizeFieldEnum.BRAND.getCode()));
                beanList.add(new ExcelExportEntity("大区", BailiCustomizeFieldEnum.ZONENAME.getCode()));
                beanList.add(new ExcelExportEntity("省区", BailiCustomizeFieldEnum.PROVINCENAME.getCode()));
                beanList.add(new ExcelExportEntity("管理分区", BailiCustomizeFieldEnum.MANGERCITY.getCode()));
                beanList.add(new ExcelExportEntity("经营城市", BailiCustomizeFieldEnum.BIZCITY.getCode()));

            }

            beanList.add(new ExcelExportEntity("门店地址", "storeAddress"));
            ExcelExportEntity patrolStoreDate = new ExcelExportEntity("巡店日期", "patrolStoreDate");
            patrolStoreDate.setFormat(DateUtils.DATE_FORMAT_SEC_6);
            beanList.add(patrolStoreDate);

            beanList.add(new ExcelExportEntity("门店总得分", "storeTotalScoreStr"));
            beanList.add(new ExcelExportEntity("门店得分率", "storeScoreRate"));
            beanList.add(new ExcelExportEntity("得分率排行", "storeScoreRateRank"));
            beanList.add(new ExcelExportEntity("合格率", "passRate"));
            beanList.add(new ExcelExportEntity("不合格率", "failureRate"));

            beanList.add(new ExcelExportEntity("巡店时长", "patrolStoreDuration"));

            TbMetaTableDO tbMetaTableDO = tbMetaTableMapper.selectById(eid, request.getMetaTableId());
            Boolean tableProperty = TableTypeUtil.isUserDefinedTable(tbMetaTableDO.getTableProperty(), tbMetaTableDO.getTableType());
            List<TbMetaStaTableColumnDO> tbMetaStaTableColumnDOS = new ArrayList<>();
            List<TbMetaDefTableColumnDO> tbMetaDefTableColumnDOS = new ArrayList<>();
            if (tableProperty) {
                tbMetaDefTableColumnDOS = tbMetaDefTableColumnMapper.selectByTableId(eid, request.getMetaTableId());
                for (TbMetaDefTableColumnDO tbMetaDefTableColumnDO : tbMetaDefTableColumnDOS) {
                    beanList.add(new ExcelExportEntity(tbMetaDefTableColumnDO.getColumnName(), String.format("value%d", tbMetaDefTableColumnDO.getId())));
                }
            } else {
                tbMetaStaTableColumnDOS = tbMetaStaTableColumnMapper.selectColumnListByTableIdList(eid, Arrays.asList(request.getMetaTableId()), Boolean.FALSE);
                for (TbMetaStaTableColumnDO tbMetaStaTableColumnDO : tbMetaStaTableColumnDOS) {

                    if (tbMetaStaTableColumnDO.getColumnType().equals(MetaColumnTypeEnum.COLLECT_COLUMN.getCode())) {
                        beanList.add(new ExcelExportEntity(tbMetaStaTableColumnDO.getColumnName(), String.format("checkText%d", tbMetaStaTableColumnDO.getId())));
                    } else {
                        beanList.add(new ExcelExportEntity(tbMetaStaTableColumnDO.getColumnName() + "(分值)", "score" + tbMetaStaTableColumnDO.getId()));
                        beanList.add(new ExcelExportEntity(tbMetaStaTableColumnDO.getColumnName() + "(得分)", "totalScore" + tbMetaStaTableColumnDO.getId()));
                        beanList.add(new ExcelExportEntity(tbMetaStaTableColumnDO.getColumnName() + "(检查结果)", "checkResultName" + tbMetaStaTableColumnDO.getId()));
                    }

                }
            }

            ExcelExportEntity signEndTime = new ExcelExportEntity("签到时间", "signInTime");
            signEndTime.setFormat(DateUtils.DATE_FORMAT_SEC_5);
            beanList.add(signEndTime);
            ExcelExportEntity signOutTime = new ExcelExportEntity("签退时间", "signOutTime");
            signOutTime.setFormat(DateUtils.DATE_FORMAT_SEC_5);
            beanList.add(signOutTime);
            beanList.add(new ExcelExportEntity("巡店结果", "patrolStoreResult"));
            beanList.add(new ExcelExportEntity("奖罚金额", "checkAwardPunish"));
            beanList.add(new ExcelExportEntity("所属区域", "regionName"));

            //10级区域
            beanList.addAll(ExportUtil.getTenRegionExportEntityList());
            boolean isAdd = false;
            for (int pageNum = 1; pageNum <= pages; pageNum++) {
                request.setPageSize(Constants.PAGE_SIZE);
                request.setPageNumber(pageNum);
                PageDTO<PatrolStoreDetailExportVO> patrolStoreDetail = patrolStoreService.getPatrolStoreDetail(eid, request);
                List<PatrolStoreDetailExportVO> result = patrolStoreDetail.getList();
                if (CollectionUtils.isEmpty(result)) {
                    break;
                }
                // 计算得分率排行
                setRank(result);

                List<Map<String, Object>> resultMap = new ArrayList<>();
                Map<String, BigDecimal> tbDataStaTableColumnMap = new HashMap<>();
                Map<String, String> columnMap = new HashMap<>();
                Map<String, String> checkResultNameMap = new HashMap<>();
                Map<String, String> defTableColumnMap = new HashMap<>();
                if (tableProperty) {
                    List<TbDataDefTableColumnDO> tbDataDefTableColumnDOS = tbDataDefTableColumnMapper.selectByBusinessIdList(eid, result.get(0).getBusinessIds());
                    for (TbDataDefTableColumnDO tbDataDefTableColumnDO : tbDataDefTableColumnDOS) {
                        defTableColumnMap.put(String.format("%d%d", tbDataDefTableColumnDO.getBusinessId(), tbDataDefTableColumnDO.getMetaColumnId()), tbDataDefTableColumnDO.getValue2());
                    }
                } else {
                    List<TbDataStaTableColumnDO> tbDataStaTableColumnDOS = tbDataStaTableColumnMapper.selectByBusinessIdList(eid, result.get(0).getBusinessIds(), null);
                    for (TbDataStaTableColumnDO tbDataStaTableColumnDO : tbDataStaTableColumnDOS) {
                        if (tbDataStaTableColumnDO.getColumnType().equals(MetaColumnTypeEnum.COLLECT_COLUMN.getCode())) {
                            columnMap.put(String.format("%d%d", tbDataStaTableColumnDO.getBusinessId(), tbDataStaTableColumnDO.getMetaColumnId()), tbDataStaTableColumnDO.getCheckText());
                        } else {
                            tbDataStaTableColumnMap.put("" + tbDataStaTableColumnDO.getBusinessId() + tbDataStaTableColumnDO.getMetaColumnId(), tbDataStaTableColumnDO.getCheckScore());
                            checkResultNameMap.put(String.format("%d%d", tbDataStaTableColumnDO.getBusinessId(), tbDataStaTableColumnDO.getMetaColumnId()), tbDataStaTableColumnDO.getCheckResultName());
                        }

                    }
                }
                Integer index = 1;
                for (PatrolStoreDetailExportVO vo : result) {
                    Map<String, Object> map = BeanUtil.beanToMap(vo);
                    map.put("index", index++);
                    if (tableProperty) {
                        for (TbMetaDefTableColumnDO tbMetaDefTableColumnDO : tbMetaDefTableColumnDOS) {
                            String s = defTableColumnMap.get(String.format("%d%d", vo.getBusinessId(), tbMetaDefTableColumnDO.getId()));
                            if (tbMetaDefTableColumnDO.getFormat().equals("AgGrid") && StringUtils.isNotEmpty(s)) {
                                s = s.substring(1, s.length() - 1);
                            }
                            map.put("value" + tbMetaDefTableColumnDO.getId(), s);
                        }
                    } else {
                        for (TbMetaStaTableColumnDO tbMetaStaTableColumnDO : tbMetaStaTableColumnDOS) {
                            if (tbMetaStaTableColumnDO.getColumnType().equals(MetaColumnTypeEnum.COLLECT_COLUMN.getCode())) {
                                map.put("checkText" + tbMetaStaTableColumnDO.getId(), columnMap.get(String.format("%d%d", vo.getBusinessId(), tbMetaStaTableColumnDO.getId())));
                            } else {
                                map.put("score" + tbMetaStaTableColumnDO.getId(), tbMetaStaTableColumnDO.getSupportScore());
                                map.put("totalScore" + tbMetaStaTableColumnDO.getId(), tbDataStaTableColumnMap.get(String.format("%d%d", vo.getBusinessId(), tbMetaStaTableColumnDO.getId())));
                                map.put("checkResultName" + tbMetaStaTableColumnDO.getId(), checkResultNameMap.get(String.format("%d%d", vo.getBusinessId(), tbMetaStaTableColumnDO.getId())));
                            }

                        }
                    }
                    if (BailiEnterpriseEnum.bailiAffiliatedCompany(eid)) {
                        List<ExtendFieldInfoVO> storeExtendField = vo.getStoreExtendField();
                        for (ExtendFieldInfoVO extendFieldInfoVO : storeExtendField) {
                            if (extendFieldInfoVO.getExtendFieldName().equals(BailiCustomizeFieldEnum.ZONENAME.getName())) {
                                map.put(BailiCustomizeFieldEnum.ZONENAME.getCode(), extendFieldInfoVO.getExtendFieldValue());
                            }
                            if (extendFieldInfoVO.getExtendFieldName().equals(BailiCustomizeFieldEnum.MANGERCITY.getName())) {
                                map.put(BailiCustomizeFieldEnum.MANGERCITY.getCode(), extendFieldInfoVO.getExtendFieldValue());
                            }
                            if (extendFieldInfoVO.getExtendFieldName().equals(BailiCustomizeFieldEnum.BIZCITY.getName())) {
                                map.put(BailiCustomizeFieldEnum.BIZCITY.getCode(), extendFieldInfoVO.getExtendFieldValue());
                            }
                            if (extendFieldInfoVO.getExtendFieldName().equals(BailiCustomizeFieldEnum.BRAND.getName())) {
                                map.put(BailiCustomizeFieldEnum.BRAND.getCode(), extendFieldInfoVO.getExtendFieldValue());
                            }
                            if (extendFieldInfoVO.getExtendFieldName().equals(BailiCustomizeFieldEnum.PROVINCENAME.getName())) {
                                map.put(BailiCustomizeFieldEnum.PROVINCENAME.getCode(), extendFieldInfoVO.getExtendFieldValue());
                            }
                        }
                    }

                    //10级区域
                    List<String> regionNameList = vo.getRegionNameList();
                    if (CollectionUtils.isNotEmpty(regionNameList)) {
                        int i = 0;
                        for (String regionName : regionNameList) {
                            map.put(Constants.EXPORT_REGION_CODE + i, regionName);
                            i++;
                        }
                    }
                    resultMap.add(map);
                }
                workbook = ExcelExportUtil.exportBigExcel(params, beanList, resultMap);
                result.clear();
                resultMap.clear();
            }
            if (workbook != null) {
                try {
                    ExcelExportUtil.closeExportBigExcel();
                } catch (Exception ex) {
                    log.error("EXCEL文件导出对象关闭异常", ex);
                }
                task.setFileName(tbMetaTableDO.getTableName() + "-" + DateUtils.getTime(request.getBeginTime()) + "-" + DateUtils.getTime(request.getEndTime()));
                exportAsyncService.asyncDynamicExportListFile(eid, task, workbook, (dbName));
            } else {
                log.error("查询不到数据");
                task.setStatus(ImportTaskStatusEnum.ERROR.getCode());
                task.setRemark("查询不到数据");
            }
        } catch (Throwable e) {
            log.error("获取数据失败", e);
            task.setStatus(ImportTaskStatusEnum.ERROR.getCode());
            task.setRemark("导出数据异常");
        } finally {
            try {
                ExcelExportUtil.closeExportBigExcel();
            } catch (Exception ex) {
                log.error("EXCEL文件导出对象关闭异常", ex);
            }
            DataSourceHelper.changeToSpecificDataSource(dbName);
            importTaskService.updateImportTask(eid, task);
        }
    }

    private void setRank(List<PatrolStoreDetailExportVO> list) {
        List<Map.Entry<Integer, PatrolStoreDetailExportVO>> listWithIndex = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            listWithIndex.add(new AbstractMap.SimpleEntry<>(i, list.get(i)));
        }

        listWithIndex.sort((a, b) -> {
            String aScoreRate = a.getValue().getStoreScoreRate();
            String bScoreRate = b.getValue().getStoreScoreRate();
            if ((aScoreRate == null || aScoreRate.equals("/")) && (bScoreRate == null || bScoreRate.equals("/"))) return 0;
            if (aScoreRate == null || aScoreRate.equals("/")) return 1;
            if (bScoreRate == null || bScoreRate.equals("/")) return -1;
            return Double.valueOf(bScoreRate.replace("%", "")).compareTo(Double.valueOf(aScoreRate.replace("%", "")));
        });

        int rank = 1;
        for (Map.Entry<Integer, PatrolStoreDetailExportVO> withIndex : listWithIndex) {
            list.get(withIndex.getKey()).setStoreScoreRateRank(rank++);
        }
    }


    @Override
    public void taskStageRecordListDetailExport(String eid, Long totalNum, Long businessId, Long metaTableId, ImportTaskDO task, String dbName) {
        DataSourceHelper.changeToSpecificDataSource(dbName);

        Workbook workbook = null;
        try {

            List<TbDataTableDO> dataTableDOList = tbDataTableMapper.selectByBusinessId(eid, businessId, PATROL_STORE);
            Collections.reverse(dataTableDOList);
            for (TbDataTableDO dataTableDO : dataTableDOList) {

                List<Map<String, Object>> exportParamList = Lists.newArrayList();
                //概览数据
                ExportParams totalDataExportParams = new ExportParams(null, dataTableDO.getTableName(), ExcelType.XSSF);
                Map<String, Object> totalDataExportValueMap = Maps.newHashMap();
                totalDataExportValueMap.put("title", totalDataExportParams);
                PatrolStoreStatisticsMetaStaColumnVO vo = patrolStoreService.taskStageRecordDetailList(eid, businessId, dataTableDO.getMetaTableId(), Constants.INDEX_ONE, Constants.ZERO);
                if (vo.getColumnList() == null || CollectionUtils.isEmpty(vo.getColumnList().getList())) {
                    log.error("查询不到数据");
                    task.setStatus(ImportTaskStatusEnum.ERROR.getCode());
                    task.setRemark("查询不到数据");
                    return;
                }

                List<TbMetaStaColumnDetailVO> list = vo.getColumnList().getList();
                List<String> storeIdList = ListUtils.emptyIfNull(list)
                        .stream()
                        .map(TbMetaStaColumnDetailVO::getStoreId)
                        .collect(Collectors.toList());

                List<StorePathDTO> pathDTOList = ListUtils.emptyIfNull(list)
                        .stream()
                        .map(data -> {
                            StorePathDTO storePathDTO = new StorePathDTO();
                            storePathDTO.setStoreId(data.getStoreId());
                            storePathDTO.setRegionPath(data.getRegionPath());
                            return storePathDTO;
                        })
                        .collect(Collectors.toList());

                List<StoreDO> storeList = storeMapper.getStoreByStoreIdList(eid, storeIdList);
                Map<String, String> storeNumMap = ListUtils.emptyIfNull(storeList)
                        .stream()
                        .filter(data -> StringUtils.isNotBlank(data.getStoreNum()))
                        .collect(Collectors.toMap(StoreDO::getStoreId, StoreDO::getStoreNum, (a, b) -> a));
                Map<String, List<String>> fullRegionNameMap = regionService.getFullRegionNameList(eid, pathDTOList);
                //添加门店编号以及全区域
                List<TbMetaStaColumnDetailVO> columnDetailVOList = vo.getColumnList().getList();
                for (TbMetaStaColumnDetailVO result : columnDetailVOList) {
                    result.setStoreNum(storeNumMap.get(result.getStoreId()));
                    result.setFullRegionName(StringUtils.join(fullRegionNameMap.get(result.getStoreId()), Constants.SPLIT_LINE));
                    result.setRegionNameList(fullRegionNameMap.get(result.getStoreId()));
                }
                //标准表
                if (!TableTypeUtil.isUserDefinedTable(dataTableDO.getTableProperty(), dataTableDO.getTableType())) {
                    //如果是高级检查表，添加统计维度
                    if (Constants.INDEX_ONE.equals(((TbMetaStaColumnDetailVO) vo.getColumnList().getList().get(Constants.INDEX_ZERO)).getTableProperty())) {
                        List<TbMetaStaColumnDetailVO> resultList = vo.getColumnList().getList();
                        List<TbOpenHighCheckColumnDetailVO> columnList = new ArrayList<>();
                        for (TbMetaStaColumnDetailVO tbMetaStaColumnDetailVO : resultList) {
                            TbOpenHighCheckColumnDetailVO tbOpenHighCheckColumnDetailVO = new TbOpenHighCheckColumnDetailVO();
                            BeanUtils.copyProperties(tbMetaStaColumnDetailVO, tbOpenHighCheckColumnDetailVO);
                            ExportUtil.setRegionEntityExport(tbOpenHighCheckColumnDetailVO, tbMetaStaColumnDetailVO.getRegionNameList());
                            columnList.add(tbOpenHighCheckColumnDetailVO);
                        }
                        totalDataExportValueMap.put(NormalExcelConstants.DATA_LIST, columnList);
                        totalDataExportValueMap.put(NormalExcelConstants.CLASS, TbOpenHighCheckColumnDetailVO.class);

                        //        // 设置excel的基本参数
                        if (workbook == null) {
                            exportParamList.add(totalDataExportValueMap);
                            workbook = ExcelExportUtil.exportExcel(exportParamList, ExcelType.XSSF);
                        } else {
                            //构建区域分类统计sheet
                            new ExcelExportService().createSheet(workbook, totalDataExportParams, TbOpenHighCheckColumnDetailVO.class, columnList);
                        }
                    } else {
                        List<TbMetaStaColumnDetailVO> resultList = vo.getColumnList().getList();
                        for (TbMetaStaColumnDetailVO tbMetaStaColumnDetailVO : resultList) {
                            ExportUtil.setRegionEntityExport(tbMetaStaColumnDetailVO, tbMetaStaColumnDetailVO.getRegionNameList());
                        }
                        totalDataExportValueMap.put(NormalExcelConstants.DATA_LIST, vo.getColumnList().getList());
                        totalDataExportValueMap.put(NormalExcelConstants.CLASS, TbMetaStaColumnDetailVO.class);
                        //        // 设置excel的基本参数
                        if (workbook == null) {
                            exportParamList.add(totalDataExportValueMap);
                            workbook = ExcelExportUtil.exportExcel(exportParamList, ExcelType.XSSF);
                        } else {
                            //构建区域分类统计sheet
                            new ExcelExportService().createSheet(workbook, totalDataExportParams, TbMetaStaColumnDetailVO.class, vo.getColumnList().getList());
                        }
                    }
                } else {
                    List<TbMetaStaColumnDetailVO> resultList = vo.getColumnList().getList();
                    List<TbMetaDefColumnDetailVO> columnList = new ArrayList<>();
                    for (TbMetaStaColumnDetailVO tbMetaStaColumnDetailVO : resultList) {
                        TbMetaDefColumnDetailVO tbMetaDefColumnDetailVO = new TbMetaDefColumnDetailVO();
                        BeanUtils.copyProperties(tbMetaStaColumnDetailVO, tbMetaDefColumnDetailVO);
                        ExportUtil.setRegionEntityExport(tbMetaDefColumnDetailVO, tbMetaStaColumnDetailVO.getRegionNameList());

                        columnList.add(tbMetaDefColumnDetailVO);
                    }
                    totalDataExportValueMap.put(NormalExcelConstants.DATA_LIST, columnList);
                    totalDataExportValueMap.put(NormalExcelConstants.CLASS, TbMetaDefColumnDetailVO.class);
                    //        // 设置excel的基本参数R
                    if (workbook == null) {
                        exportParamList.add(totalDataExportValueMap);
                        workbook = ExcelExportUtil.exportExcel(exportParamList, ExcelType.XSSF);
                    } else {
                        //构建区域分类统计sheet
                        new ExcelExportService().createSheet(workbook, totalDataExportParams, TbMetaDefColumnDetailVO.class, columnList);
                    }
                }
                vo.getColumnList().getList().clear();
            }

            if (workbook != null) {
                try {
                    ExcelExportUtil.closeExportBigExcel();
                } catch (Exception ex) {
                    log.error("EXCEL文件导出对象关闭异常", ex);
                }
                exportAsyncService.asyncDynamicExportListFile(eid, task, workbook, dbName);
            } else {
                log.error("查询不到数据");
                task.setStatus(ImportTaskStatusEnum.ERROR.getCode());
                task.setRemark("查询不到数据");
            }
        } catch (Throwable e) {
            log.error("获取数据失败", e);
            task.setStatus(ImportTaskStatusEnum.ERROR.getCode());
            task.setRemark("导出数据异常");
        } finally {
            try {
                ExcelExportUtil.closeExportBigExcel();
            } catch (Exception ex) {
                log.error("EXCEL文件导出对象关闭异常", ex);
            }
            DataSourceHelper.changeToSpecificDataSource(dbName);
            importTaskService.updateImportTask(eid, task);
        }
    }

    @Override
    public void recordListDetailExport(String eid, Long totalNum, ImportTaskDO task, PatrolStoreStatisticsDataTableQuery query) {
        DataSourceHelper.changeToSpecificDataSource(query.getDbName());

        int pageSize = Constants.PAGE_SIZE;
        long pages = (totalNum + pageSize - 1) / pageSize;
        // 设置excel的基本参数
        ExportParams params = new ExportParams(null, null, ExcelType.XSSF);

        Workbook workbook = null;
        try {
            for (int pageNum = 1; pageNum <= pages; pageNum++) {
                query.setPageNum(pageNum);
                query.setPageSize(pageSize);
                PageInfo vo = patrolStoreRecordsService.potralRecordDetailList(eid, query);
                if (vo == null || CollectionUtils.isEmpty(vo.getList())) {
                    break;
                }
                List<TbMetaStaColumnDetailExportVO> resultList = vo.getList();
                //处理人列表
                Set<String> userIdSet = resultList.stream().map(TbMetaStaColumnDetailExportVO::getSupervisorId).filter(StringUtils::isNotBlank).collect(Collectors.toSet());

                Map<String, String> userRoleNameMap = new HashMap<>();
                List<UserRoleDTO> userRoleDTOList = sysRoleDao.getUserRoleNameByUserIdList(eid, new ArrayList<>(userIdSet));
                if (CollectionUtils.isNotEmpty(userRoleDTOList)) {
                    userRoleNameMap = userRoleDTOList.stream()
                            .collect(Collectors.toMap(UserRoleDTO::getUserId, UserRoleDTO::getRoleName));
                }

                if (!query.getIsDefine()) {
                    for (TbMetaStaColumnDetailExportVO tbMetaStaColumnDetailVO : resultList) {
                        tbMetaStaColumnDetailVO.setSupervisorPositionName(userRoleNameMap.get(tbMetaStaColumnDetailVO.getSupervisorId()));
                        ExportUtil.setRegionEntityExport(tbMetaStaColumnDetailVO, tbMetaStaColumnDetailVO.getRegionNameList());
                    }
                    workbook = ExcelExportUtil.exportBigExcel(params, TbMetaStaColumnDetailExportVO.class, resultList);
                } else {
                    List<TbMetaDefColumnDetailExtVO> columnList = new ArrayList<>();
                    for (TbMetaStaColumnDetailExportVO tbMetaStaColumnDetailVO : resultList) {
                        TbMetaDefColumnDetailExtVO tbMetaDefColumnDetailVO = new TbMetaDefColumnDetailExtVO();
                        BeanUtils.copyProperties(tbMetaStaColumnDetailVO, tbMetaDefColumnDetailVO);
                        ExportUtil.setRegionEntityExport(tbMetaDefColumnDetailVO, tbMetaStaColumnDetailVO.getRegionNameList());
                        //职位名称
                        tbMetaDefColumnDetailVO.setSupervisorPositionName(userRoleNameMap.get(tbMetaStaColumnDetailVO.getSupervisorId()));
                        columnList.add(tbMetaDefColumnDetailVO);
                    }
                    workbook = ExcelExportUtil.exportBigExcel(params, TbMetaDefColumnDetailExtVO.class, columnList);

                }

                vo.getList().clear();
            }
            if (workbook != null) {
                try {
                    ExcelExportUtil.closeExportBigExcel();
                } catch (Exception ex) {
                    log.error("EXCEL文件导出对象关闭异常", ex);
                }
                exportAsyncService.asyncDynamicExportListFile(eid, task, workbook, query.getDbName());
            } else {
                log.error("查询不到数据");
                task.setStatus(ImportTaskStatusEnum.ERROR.getCode());
                task.setRemark("查询不到数据");
            }
        } catch (Throwable e) {
            log.error("获取数据失败", e);
            task.setStatus(ImportTaskStatusEnum.ERROR.getCode());
            task.setRemark("导出数据异常");
        } finally {
            try {
                ExcelExportUtil.closeExportBigExcel();
            } catch (Exception ex) {
                log.error("EXCEL文件导出对象关闭异常", ex);
            }
            DataSourceHelper.changeToSpecificDataSource(query.getDbName());
            importTaskService.updateImportTask(eid, task);
        }
    }

    @Override
    public void tbQuestionRecordExport(String enterpriseId, Long totalNum, ImportTaskDO importTaskDO, TbQuestionRecordSearchRequest request,
                                       String dbName) {
        DataSourceHelper.changeToSpecificDataSource(dbName);
        ExportParams params = new ExportParams(null, null, ExcelType.XSSF);
        Workbook workbook = null;
        int pageSize = Constants.PAGE_SIZE;
        long pages = (totalNum + pageSize - 1) / pageSize;
        try {
            for (int pageNum = 1; pageNum <= pages; pageNum++) {
                PageRequest pageRequest = new PageRequest();
                pageRequest.setPageNumber(pageNum);
                pageRequest.setPageSize(pageSize);
                // 查询问题工单导出数据
                PageVO<TbQuestionRecordExportVO> exportVOPage = questionRecordService.listForExport(enterpriseId, request, pageRequest, dbName);
                if (Objects.isNull(exportVOPage) || CollectionUtils.isEmpty(exportVOPage.getList())) {
                    break;
                }
                workbook = ExcelExportUtil.exportBigExcel(params, TbQuestionRecordExportVO.class, exportVOPage.getList());
            }
            if (workbook == null) {
                log.error("问题工单查询不到数据");
                importTaskDO.setStatus(ImportTaskStatusEnum.ERROR.getCode());
                importTaskDO.setRemark("问题工单查询不到数据");
            } else {
                try {
                    ExcelExportUtil.closeExportBigExcel();
                } catch (Exception ex) {
                    log.error("问题工单EXCEL文件导出对象关闭异常", ex);
                }
                exportAsyncService.asyncDynamicExportListFile(enterpriseId, importTaskDO, workbook, dbName);
            }
        } catch (Throwable e) {
            log.error("问题工单获取数据失败", e);
            importTaskDO.setStatus(ImportTaskStatusEnum.ERROR.getCode());
            importTaskDO.setRemark("问题工单导出数据异常");
        } finally {
            try {
                ExcelExportUtil.closeExportBigExcel();
            } catch (Exception ex) {
                log.error("问题工单EXCEL文件导出对象关闭异常", ex);
            }
            importTaskService.updateImportTask(enterpriseId, importTaskDO);
        }
    }


    @Override
    public void subQuestionDetailListExport(String enterpriseId, Long totalNum, ImportTaskDO importTaskDO, TbQuestionRecordSearchRequest request, String dbName) {
        DataSourceHelper.changeToSpecificDataSource(dbName);
        ExportParams params = new ExportParams(null, null, ExcelType.XSSF);
        Workbook workbook = null;
        int pageSize = Constants.PAGE_SIZE;
        long pages = (totalNum + pageSize - 1) / pageSize;
        try {
            for (int pageNum = 1; pageNum <= pages; pageNum++) {
                request.setPageNumber(pageNum);
                request.setPageSize(pageSize);
                request.setExport(true);
                // 查询问题工单导出数据
                PageDTO<TbQuestionSubRecordListExportVO> subQuestionDetailVOPageDTO = questionRecordService.subQuestionDetailListForExport(enterpriseId, request);
                if (Objects.isNull(subQuestionDetailVOPageDTO) || CollectionUtils.isEmpty(subQuestionDetailVOPageDTO.getList())) {
                    break;
                }
                workbook = ExcelExportUtil.exportBigExcel(params, TbQuestionSubRecordListExportVO.class, subQuestionDetailVOPageDTO.getList());
            }
            if (workbook == null) {
                log.error("工单详情表没有数据");
                importTaskDO.setStatus(ImportTaskStatusEnum.ERROR.getCode());
                importTaskDO.setRemark("工单详情表没有数据");
            } else {
                try {
                    ExcelExportUtil.closeExportBigExcel();
                } catch (Exception ex) {
                    log.error("问题工单EXCEL文件导出对象关闭异常", ex);
                }
                exportAsyncService.asyncDynamicExportListFile(enterpriseId, importTaskDO, workbook, dbName);
            }
        } catch (Throwable e) {
            log.error("工单详情表获取数据失败", e);
            importTaskDO.setStatus(ImportTaskStatusEnum.ERROR.getCode());
            importTaskDO.setRemark("工单详情表导出数据异常");
        } finally {
            try {
                ExcelExportUtil.closeExportBigExcel();
            } catch (Exception ex) {
                log.error("问题工单EXCEL文件导出对象关闭异常", ex);
            }
            importTaskService.updateImportTask(enterpriseId, importTaskDO);
        }
    }

    @Override
    public void regionQuestionReportExport(String enterpriseId, Long totalNum, ImportTaskDO importTaskDO, RegionQuestionReportRequest request, String dbName) {
        DataSourceHelper.changeToSpecificDataSource(dbName);
        ExportParams params = new ExportParams(null, null, ExcelType.XSSF);
        Workbook workbook = null;
        CurrentUser currentUser = new CurrentUser();
        currentUser.setDbName(dbName);
        try {
            List<RegionQuestionReportVO> list = questionRecordService.getQuestionReport(enterpriseId, request, currentUser);
            workbook = ExcelExportUtil.exportBigExcel(params, RegionQuestionReportVO.class, list);
            if (workbook == null) {
                log.error("区域门店工单报表没有数据");
                importTaskDO.setStatus(ImportTaskStatusEnum.ERROR.getCode());
                importTaskDO.setRemark("区域门店工单报表没有数据");
            } else {
                try {
                    ExcelExportUtil.closeExportBigExcel();
                } catch (Exception ex) {
                    log.error("区域门店工单报表EXCEL文件导出对象关闭异常", ex);
                }
                exportAsyncService.asyncDynamicExportListFile(enterpriseId, importTaskDO, workbook, dbName);
            }
        } catch (Throwable e) {
            log.error("区域门店工单报表获取数据失败", e);
            importTaskDO.setStatus(ImportTaskStatusEnum.ERROR.getCode());
            importTaskDO.setRemark("区域门店工单报表导出数据异常");
        } finally {
            try {
                ExcelExportUtil.closeExportBigExcel();
            } catch (Exception ex) {
                log.error("区域门店工单报表EXCEL文件导出对象关闭异常", ex);
            }
            importTaskService.updateImportTask(enterpriseId, importTaskDO);
        }
    }

    @Override
    public void questionParentInfoListExport(String enterpriseId, Long totalNum, ImportTaskDO importTaskDO, QuestionParentRequest request, String dbName) {
        DataSourceHelper.changeToSpecificDataSource(dbName);
        ExportParams params = new ExportParams(null, null, ExcelType.XSSF);
        Workbook workbook = null;
        int pageSize = Constants.PAGE_SIZE_TEN;
        long pages = (totalNum + pageSize - 1) / pageSize;
        try {
            for (int pageNum = 1; pageNum <= pages; pageNum++) {
                request.setPageNumber(pageNum);
                request.setPageSize(pageSize);
                // 查询问题工单导出数据
                PageInfo<TbQuestionParentInfoVO> tbQuestionParentInfoVOPageInfo = questionParentInfoService.questionList(enterpriseId, request);
                if (Objects.isNull(tbQuestionParentInfoVOPageInfo) || CollectionUtils.isEmpty(tbQuestionParentInfoVOPageInfo.getList())) {
                    break;
                }
                List<Long> questionParentInfoIdList = tbQuestionParentInfoVOPageInfo.getList().stream().map(TbQuestionParentInfoVO::getId).collect(Collectors.toList());
                TbQuestionRecordSearchRequest searchRequest = new TbQuestionRecordSearchRequest();
                searchRequest.setQuestionParentInfoIdList(questionParentInfoIdList);
                searchRequest.setCurrentUserId(request.getCurrentUserId());
                //不分页查询
                PageRequest pageRequest = new PageRequest();
                pageRequest.setPageNumber(1);
                pageRequest.setPageSize(0);
                PageVO<TbQuestionRecordExportVO> pageVO = questionRecordService.listForExport(enterpriseId, searchRequest, pageRequest, dbName);
                // map: userId to recordList
                Map<Long, List<TbQuestionRecordExportVO>> questionRecordListMap = pageVO.getList().stream().collect(Collectors.groupingBy(TbQuestionRecordExportVO::getParentQuestionId));
                List<TbQuestionRecordListExportVO> tbQuestionRecordListExportVOList = new ArrayList<>();
                for (TbQuestionParentInfoVO parentInfoVO : tbQuestionParentInfoVOPageInfo.getList()) {
                    List<TbQuestionRecordExportVO> recordExportVOList = questionRecordListMap.get(parentInfoVO.getId());
                    if (CollectionUtils.isNotEmpty(recordExportVOList)) {
                        for (TbQuestionRecordExportVO exportVO : recordExportVOList) {
                            TbQuestionRecordListExportVO listExportVO = new TbQuestionRecordListExportVO();
                            listExportVO.convertQuestionParentInfoVOForExport(parentInfoVO);
                            listExportVO.convertQuestionRecordExportVOForExport(exportVO);
                            tbQuestionRecordListExportVOList.add(listExportVO);
                        }
                    }
                }
                if (CollectionUtils.isNotEmpty(tbQuestionRecordListExportVOList)) {
                    workbook = ExcelExportUtil.exportBigExcel(params, TbQuestionRecordListExportVO.class, tbQuestionRecordListExportVOList);
                }
            }
            if (workbook == null) {
                log.error("工单列表没有数据");
                importTaskDO.setStatus(ImportTaskStatusEnum.ERROR.getCode());
                importTaskDO.setRemark("工单列表没有数据");
            } else {
                try {
                    ExcelExportUtil.closeExportBigExcel();
                } catch (Exception ex) {
                    log.error("工单列表EXCEL文件导出对象关闭异常", ex);
                }
                exportAsyncService.asyncDynamicExportListFile(enterpriseId, importTaskDO, workbook, dbName);
            }
        } catch (Throwable e) {
            log.error("工单列表获取数据失败", e);
            importTaskDO.setStatus(ImportTaskStatusEnum.ERROR.getCode());
            importTaskDO.setRemark("工单列表导出数据异常");
        } finally {
            try {
                ExcelExportUtil.closeExportBigExcel();
            } catch (Exception ex) {
                log.error("工单列表EXCEL文件导出对象关闭异常", ex);
            }
            importTaskService.updateImportTask(enterpriseId, importTaskDO);
        }
    }


    @Override
    public void patrolStoreTaskReportExport(String eid, Long totalNum, TaskReportQuery query, ImportTaskDO task) {
        try {
            DataSourceHelper.changeToSpecificDataSource(query.getDbName());
            ExportParams params = new ExportParams(null, null, ExcelType.XSSF);
            PageInfo pageInfo = unifyTaskReportService.listTaskReport(eid, query);
            List<TaskReportVO> taskReportVOList = pageInfo.getList();
            if (CollectionUtils.isEmpty(taskReportVOList)) {
                return;
            }
            Workbook workbook = null;
            if (TB_DISPLAY_TASK.getCode().equals(query.getTaskType())) {
                List<TaskReportExportVO> exportVOList = unifyTaskReportService.translateToExportVO(taskReportVOList);
                workbook = ExcelExportUtil.exportBigExcel(params, TaskReportExportVO.class, exportVOList);
            } else {
                List<TaskReportExportBaseVO> exportVOList = unifyTaskReportService.translateToExportBaseVO(taskReportVOList);
                workbook = ExcelExportUtil.exportBigExcel(params, TaskReportExportBaseVO.class, exportVOList);
            }

            if (workbook != null) {
                try {
                    ExcelExportUtil.closeExportBigExcel();
                } catch (Exception ex) {
                    log.error("EXCEL文件导出对象关闭异常", ex);
                }
                exportAsyncService.asyncDynamicExportListFile(eid, task, workbook, query.getDbName());
            } else {
                log.error("查询不到数据");
                task.setStatus(ImportTaskStatusEnum.ERROR.getCode());
                task.setRemark("查询不到数据");
            }
        } catch (Throwable e) {
            log.error("获取数据失败", e);
            task.setStatus(ImportTaskStatusEnum.ERROR.getCode());
            task.setRemark("导出数据异常");
        } finally {
            try {
                ExcelExportUtil.closeExportBigExcel();
            } catch (Exception ex) {
                log.error("EXCEL文件导出对象关闭异常", ex);
            }
            importTaskService.updateImportTask(eid, task);
        }
    }

    @Override
    public void storeWorkStoreStatisticsListExport(String enterpriseId, Long totalNum, ImportTaskDO importTaskDO, StoreWorkDataListRequest request, CurrentUser user, String dbName) {
        DataSourceHelper.changeToSpecificDataSource(dbName);
        ExportParams params = new ExportParams(null, importTaskDO.getFileName(), ExcelType.XSSF);
        Workbook workbook = null;
        int pageSize = Constants.PAGE_SIZE;
        long pages = (totalNum + pageSize - 1) / pageSize;
        try {
            for (int pageNum = 1; pageNum <= pages; pageNum++) {
                request.setPageNumber(pageNum);
                request.setPageSize(pageSize);
                request.setExport(true);
                // 查询店务门店统计导出数据
                PageInfo<StoreWorkDataDetailVO> storeWorkRecordVOPageInfo = storeWorkRecordService.storeWorkStoreStatisticsList(enterpriseId, request, user);
                if (Objects.isNull(storeWorkRecordVOPageInfo) || CollectionUtils.isEmpty(storeWorkRecordVOPageInfo.getList())) {
                    break;
                }
                for (StoreWorkDataDetailVO storeWorkDataDetailVO : storeWorkRecordVOPageInfo.getList()) {
                    ExportUtil.setRegionEntityExport(storeWorkDataDetailVO, storeWorkDataDetailVO.getRegionNameList());
                }
                workbook = ExcelExportUtil.exportBigExcel(params, StoreWorkDataDetailVO.class, storeWorkRecordVOPageInfo.getList());
            }
            if (workbook == null) {
                log.error("店务门店统计没有数据");
                importTaskDO.setStatus(ImportTaskStatusEnum.ERROR.getCode());
                importTaskDO.setRemark("店务门店统计没有数据");
            } else {
                try {
                    ExcelExportUtil.closeExportBigExcel();
                } catch (Exception ex) {
                    log.error("店务门店统计EXCEL文件导出对象关闭异常", ex);
                }
                exportAsyncService.asyncDynamicExportListFile(enterpriseId, importTaskDO, workbook, dbName);
            }
        } catch (Throwable e) {
            log.error("店务门店统计获取数据失败", e);
            importTaskDO.setStatus(ImportTaskStatusEnum.ERROR.getCode());
            importTaskDO.setRemark("店务门店统计导出数据异常");
        } finally {
            try {
                ExcelExportUtil.closeExportBigExcel();
            } catch (Exception ex) {
                log.error("店务门店统计EXCEL文件导出对象关闭异常", ex);
            }
            importTaskService.updateImportTask(enterpriseId, importTaskDO);
        }
    }

    @Override
    public void storeWorkRegionStatisticsListExport(String enterpriseId, Long totalNum, ImportTaskDO importTaskDO, StoreWorkDataListRequest request, CurrentUser user, String dbName) {
        DataSourceHelper.changeToSpecificDataSource(dbName);
        ExportParams params = new ExportParams(null, null, ExcelType.XSSF);
        Workbook workbook = null;
        try {
            List<StoreWorkStatisticsOverviewVO> list = storeWorkRecordService.storeWorkRegionStatisticsList(enterpriseId, request, user);
            if (CollectionUtils.isNotEmpty(list)) {
                AtomicInteger i = new AtomicInteger(1);
                list.forEach(dataView -> {
                    dataView.setRank(i.getAndIncrement());
                });
            }
            workbook = ExcelExportUtil.exportBigExcel(params, StoreWorkStatisticsOverviewVO.class, list);
            if (workbook == null) {
                log.error("店务区域统计没有数据");
                importTaskDO.setStatus(ImportTaskStatusEnum.ERROR.getCode());
                importTaskDO.setRemark("店务区域统计没有数据");
            } else {
                try {
                    ExcelExportUtil.closeExportBigExcel();
                } catch (Exception ex) {
                    log.error("店务区域统计EXCEL文件导出对象关闭异常", ex);
                }
                exportAsyncService.asyncDynamicExportListFile(enterpriseId, importTaskDO, workbook, dbName);
            }
        } catch (Throwable e) {
            log.error("店务区域统计获取数据失败", e);
            importTaskDO.setStatus(ImportTaskStatusEnum.ERROR.getCode());
            importTaskDO.setRemark("店务区域统计导出数据异常");
        } finally {
            try {
                ExcelExportUtil.closeExportBigExcel();
            } catch (Exception ex) {
                log.error("店务区域统计EXCEL文件导出对象关闭异常", ex);
            }
            importTaskService.updateImportTask(enterpriseId, importTaskDO);
        }
    }

    @Override
    public void storeWorkDayStatisticsListExport(String enterpriseId, Long totalNum, ImportTaskDO importTaskDO, StoreWorkDataListRequest request, CurrentUser user, String dbName) {
        DataSourceHelper.changeToSpecificDataSource(dbName);
        ExportParams params = new ExportParams(null, null, ExcelType.XSSF);
        Workbook workbook = null;
        int pageSize = Constants.PAGE_SIZE;
        long pages = (totalNum + pageSize - 1) / pageSize;
        try {
            for (int pageNum = 1; pageNum <= pages; pageNum++) {
                request.setPageNumber(pageNum);
                request.setPageSize(pageSize);
                request.setExport(true);
                // 查询店务门店统计导出数据
                PageInfo<StoreWorkDayStatisticsVO> storeWorkDayStatisticsVOPageInfo = storeWorkRecordService.storeWorkDayStatisticsList(enterpriseId, request, user);
                if (Objects.isNull(storeWorkDayStatisticsVOPageInfo) || CollectionUtils.isEmpty(storeWorkDayStatisticsVOPageInfo.getList())) {
                    break;
                }
                workbook = ExcelExportUtil.exportBigExcel(params, StoreWorkDayStatisticsVO.class, storeWorkDayStatisticsVOPageInfo.getList());
            }
            if (workbook == null) {
                log.error("店务日报表统计没有数据");
                importTaskDO.setStatus(ImportTaskStatusEnum.ERROR.getCode());
                importTaskDO.setRemark("店务日报表统计没有数据");
            } else {
                try {
                    ExcelExportUtil.closeExportBigExcel();
                } catch (Exception ex) {
                    log.error("店务日报表统计EXCEL文件导出对象关闭异常", ex);
                }
                exportAsyncService.asyncDynamicExportListFile(enterpriseId, importTaskDO, workbook, dbName);
            }
        } catch (Throwable e) {
            log.error("店务日报表统计获取数据失败", e);
            importTaskDO.setStatus(ImportTaskStatusEnum.ERROR.getCode());
            importTaskDO.setRemark("店务日报表统计导出数据异常");
        } finally {
            try {
                ExcelExportUtil.closeExportBigExcel();
            } catch (Exception ex) {
                log.error("店务日报表统计EXCEL文件导出对象关闭异常", ex);
            }
            importTaskService.updateImportTask(enterpriseId, importTaskDO);
        }
    }

    @Override
    public void storeWorkRecordListExport(String enterpriseId, Long totalNum, ImportTaskDO importTaskDO, StoreWorkRecordListRequest request, CurrentUser user, String dbName) {
        DataSourceHelper.changeToSpecificDataSource(dbName);
        ExportParams params = new ExportParams(null, null, ExcelType.XSSF);
        Workbook workbook = null;
        int pageSize = Constants.PAGE_SIZE;
        long pages = (totalNum + pageSize - 1) / pageSize;
        try {
            for (int pageNum = 1; pageNum <= pages; pageNum++) {
                request.setPageNumber(pageNum);
                request.setPageSize(pageSize);
                request.setExport(true);
                // 查询店务门店统计导出数据
                PageInfo<StoreWorkRecordVO> storeWorkRecordVOPageInfo = storeWorkRecordService.storeWorkRecordList(enterpriseId, request, user);
                if (Objects.isNull(storeWorkRecordVOPageInfo) || CollectionUtils.isEmpty(storeWorkRecordVOPageInfo.getList())) {
                    break;
                }
                workbook = ExcelExportUtil.exportBigExcel(params, StoreWorkRecordVO.class, storeWorkRecordVOPageInfo.getList());
            }
            if (workbook == null) {
                log.error("店务记录没有数据");
                importTaskDO.setStatus(ImportTaskStatusEnum.ERROR.getCode());
                importTaskDO.setRemark("店务记录没有数据");
            } else {
                try {
                    ExcelExportUtil.closeExportBigExcel();
                } catch (Exception ex) {
                    log.error("店务记录EXCEL文件导出对象关闭异常", ex);
                }
                exportAsyncService.asyncDynamicExportListFile(enterpriseId, importTaskDO, workbook, dbName);
            }
        } catch (Throwable e) {
            log.error("店务记录获取数据失败", e);
            importTaskDO.setStatus(ImportTaskStatusEnum.ERROR.getCode());
            importTaskDO.setRemark("店务记录导出数据异常");
        } finally {
            try {
                ExcelExportUtil.closeExportBigExcel();
            } catch (Exception ex) {
                log.error("店务记录EXCEL文件导出对象关闭异常", ex);
            }
            importTaskService.updateImportTask(enterpriseId, importTaskDO);
        }
    }

    @Override
    public void storeWorkTableListExport(String enterpriseId, Long totalNum, ImportTaskDO importTaskDO, StoreWorkRecordListRequest request, String dbName, CurrentUser user) {
        DataSourceHelper.changeToSpecificDataSource(dbName);
        ExportParams params = new ExportParams(null, null, ExcelType.XSSF);
        Workbook workbook = null;
        int pageSize = Constants.PAGE_SIZE;
        long pages = (totalNum + pageSize - 1) / pageSize;
        try {
            for (int pageNum = 1; pageNum <= pages; pageNum++) {
                request.setPageNumber(pageNum);
                request.setPageSize(pageSize);
                request.setExport(true);
                // 查询店务门店统计导出数据
                PageInfo<StoreWorkTableVO> storeWorkRecordVOPageInfo = storeWorkRecordService.storeWorkTableList(enterpriseId, request, user);
                if (Objects.isNull(storeWorkRecordVOPageInfo) || CollectionUtils.isEmpty(storeWorkRecordVOPageInfo.getList())) {
                    break;
                }
                workbook = ExcelExportUtil.exportBigExcel(params, StoreWorkTableVO.class, storeWorkRecordVOPageInfo.getList());
            }
            if (workbook == null) {
                log.error("店务记录没有数据");
                importTaskDO.setStatus(ImportTaskStatusEnum.ERROR.getCode());
                importTaskDO.setRemark("店务记录没有数据");
            } else {
                try {
                    ExcelExportUtil.closeExportBigExcel();
                } catch (Exception ex) {
                    log.error("店务记录EXCEL文件导出对象关闭异常", ex);
                }
                exportAsyncService.asyncDynamicExportListFile(enterpriseId, importTaskDO, workbook, dbName);
            }
        } catch (Throwable e) {
            log.error("店务记录获取数据失败", e);
            importTaskDO.setStatus(ImportTaskStatusEnum.ERROR.getCode());
            importTaskDO.setRemark("店务记录导出数据异常");
        } finally {
            try {
                ExcelExportUtil.closeExportBigExcel();
            } catch (Exception ex) {
                log.error("店务记录EXCEL文件导出对象关闭异常", ex);
            }
            importTaskService.updateImportTask(enterpriseId, importTaskDO);
        }
    }

    @Override
    public void storeWorkColumnListExport(String enterpriseId, Long totalNum, ImportTaskDO importTaskDO, StoreWorkRecordListRequest request, String dbName, CurrentUser user) {
        DataSourceHelper.changeToSpecificDataSource(dbName);
        ExportParams params = new ExportParams(null, null, ExcelType.XSSF);
        Workbook workbook = null;
        int pageSize = Constants.PAGE_SIZE;
        long pages = (totalNum + pageSize - 1) / pageSize;
        try {
            boolean enableAI = enableAI(enterpriseId, dbName);
            for (int pageNum = 1; pageNum <= pages; pageNum++) {
                request.setPageNumber(pageNum);
                request.setPageSize(pageSize);
                request.setExport(true);
                // 查询店务门店统计导出数据
                PageInfo<StoreWorkColumnVO> storeWorkRecordVOPageInfo = storeWorkRecordService.storeWorkColumnList(enterpriseId, request, user);
                if (Objects.isNull(storeWorkRecordVOPageInfo) || CollectionUtils.isEmpty(storeWorkRecordVOPageInfo.getList())) {
                    break;
                }
                for (StoreWorkColumnVO storeWorkColumnVO : ListUtils.emptyIfNull(storeWorkRecordVOPageInfo.getList())) {
                    if(StringUtils.isNotBlank(storeWorkColumnVO.getCheckPics())){
                        // 获取数据项的图片
                        JSONArray jsonArray = JSONObject.parseArray(storeWorkColumnVO.getCheckPics());
                        List<String> imageList = CollStreamUtil.toList(jsonArray, v -> ((JSONObject) v).getString("handle"));
                        storeWorkColumnVO.setCheckPics(JSONObject.toJSONString(imageList));
                    }
                }
                if (enableAI) {
                    List<StoreWorkColumnAIVO> dataList = BeanUtil.copyToList(storeWorkRecordVOPageInfo.getList(), StoreWorkColumnAIVO.class);
                    workbook = ExcelExportUtil.exportBigExcel(params, StoreWorkColumnAIVO.class, dataList);
                } else {
                    workbook = ExcelExportUtil.exportBigExcel(params, StoreWorkColumnVO.class, storeWorkRecordVOPageInfo.getList());
                }
            }
            if (workbook == null) {
                log.error("店务记录没有数据");
                importTaskDO.setStatus(ImportTaskStatusEnum.ERROR.getCode());
                importTaskDO.setRemark("店务记录没有数据");
            } else {
                try {
                    ExcelExportUtil.closeExportBigExcel();
                } catch (Exception ex) {
                    log.error("店务记录EXCEL文件导出对象关闭异常", ex);
                }
                exportAsyncService.asyncDynamicExportListFile(enterpriseId, importTaskDO, workbook, dbName);
            }
        } catch (Throwable e) {
            log.error("店务记录获取数据失败", e);
            importTaskDO.setStatus(ImportTaskStatusEnum.ERROR.getCode());
            importTaskDO.setRemark("店务记录导出数据异常");
        } finally {
            try {
                ExcelExportUtil.closeExportBigExcel();
            } catch (Exception ex) {
                log.error("店务记录EXCEL文件导出对象关闭异常", ex);
            }
            importTaskService.updateImportTask(enterpriseId, importTaskDO);
        }
    }

    /**
     * 是否开启AI巡检
     */
    public boolean enableAI(String enterpriseId, String dbName) {
        DataSourceHelper.reset();
        EnterpriseSettingDO enterpriseSettingDO = enterpriseSettingMapper.selectByEnterpriseId(enterpriseId);
        DataSourceHelper.changeToSpecificDataSource(dbName);
        AIConfigDTO aiConfigDTO = JSONObject.parseObject(enterpriseSettingDO.getExtendField(), AIConfigDTO.class);
        if (Objects.nonNull(aiConfigDTO) && Boolean.TRUE.equals(aiConfigDTO.getAiCheck()) && CollectionUtils.isNotEmpty(aiConfigDTO.getAiConfig())) {
            return aiConfigDTO.aiEnable(AIBusinessModuleEnum.STORE_WORK);
        }
        return false;
    }

    @Override
    public void deviceListExport(String enterpriseId, Long totalNum, ImportTaskDO importTaskDO, DeviceListRequest request, String dbName, CurrentUser user) {
        DataSourceHelper.changeToSpecificDataSource(dbName);
        ExportParams params = new ExportParams(null, null, ExcelType.XSSF);
        Workbook workbook = null;
        try {
            // 查询店务门店统计导出数据
            request.setPageNumber(1);
            request.setPageSize(Constants.MAX_EXPORT_SIZE.intValue());
            List<DeviceMappingDTO> deviceMappingDTOS = deviceService.deviceList(enterpriseId, request, user);
            if (CollectionUtils.isEmpty(deviceMappingDTOS)) {
                throw new ServiceException(ErrorCodeEnum.DEVICE_NOT_EXPORT);
            }
            List<DeviceMappingDTO> result = new ArrayList<>();
            for (DeviceMappingDTO dto : deviceMappingDTOS) {
                //添加通道
                dto.setDeviceType("IPC");
                dto.setCreateDate(dto.getCreateTime() == null ? "" : DateUtils.convertTimeToString(dto.getCreateTime(), "yyyy-MM-dd HH:mm:ss"));
                if (CollectionUtils.isNotEmpty(dto.getChannelList())) {
                    dto.setDeviceType("NVR");
                    for (ChannelDTO channelDTO : dto.getChannelList()) {
                        DeviceMappingDTO deviceChannelDto = new DeviceMappingDTO();
                        deviceChannelDto.setDeviceName(channelDTO.getChannelName());
                        deviceChannelDto.setDeviceId(channelDTO.getUnionId());
                        deviceChannelDto.setChannelNo(channelDTO.getChannelNo());
                        deviceChannelDto.setDeviceStatus(channelDTO.getStatus());
                        deviceChannelDto.setStoreName(dto.getStoreName());
                        deviceChannelDto.setStoreStatusName(dto.getStoreStatusName());
                        deviceChannelDto.setHasPtz(channelDTO.getHasPtz());
                        deviceChannelDto.setDeviceType("IPC");
                        deviceChannelDto.setStoreSceneName(channelDTO.getStoreSceneName());
                        deviceChannelDto.setCreateDate(channelDTO.getCreateTime() == null ? "" : DateUtils.convertTimeToString(channelDTO.getCreateTime().getTime(), "yyyy-MM-dd HH:mm:ss"));
                        deviceChannelDto.setRemark(channelDTO.getRemark());
                        result.add(deviceChannelDto);
                    }
                }
                //添加设备本身
                result.add(dto);
            }
            workbook = ExcelExportUtil.exportBigExcel(params, DeviceMappingDTO.class, result);
            if (workbook == null) {
                log.error("设备没有数据");
                importTaskDO.setStatus(ImportTaskStatusEnum.ERROR.getCode());
                importTaskDO.setRemark("设备没有数据");
            } else {
                try {
                    ExcelExportUtil.closeExportBigExcel();
                } catch (Exception ex) {
                    log.error("设备EXCEL文件导出对象关闭异常", ex);
                }
                exportAsyncService.asyncDynamicExportListFile(enterpriseId, importTaskDO, workbook, dbName);
            }
        } catch (Throwable e) {
            log.error("设备获取数据失败", e);
            importTaskDO.setStatus(ImportTaskStatusEnum.ERROR.getCode());
            importTaskDO.setRemark("设备导出数据异常");
        } finally {
            try {
                ExcelExportUtil.closeExportBigExcel();
            } catch (Exception ex) {
                log.error("设备EXCEL文件导出对象关闭异常", ex);
            }
            importTaskService.updateImportTask(enterpriseId, importTaskDO);
        }
    }

    @Override
    public void exportDeviceSummaryExport(String enterpriseId, Long totalNum, ImportTaskDO importTaskDO, DeviceReportSearchRequest request, String dbName, CurrentUser user) {
        DataSourceHelper.changeToSpecificDataSource(dbName);
        ExportParams params = new ExportParams(null, null, ExcelType.XSSF);
        Workbook workbook = null;
        int pageSize = Constants.PAGE_SIZE;
        long pages = (totalNum + pageSize - 1) / pageSize;
        try {
            for (int pageNum = 1; pageNum <= pages; pageNum++) {
                // 查询店务门店统计导出数据
                request.setPageNum(pageNum);
                request.setPageSize(pageSize);
                PageInfo<DeviceSummaryListDTO> deviceSummaryGroupStoreId = deviceService.getDeviceSummaryGroupStoreId(enterpriseId, user, request);
                if (Objects.isNull(deviceSummaryGroupStoreId) || CollectionUtils.isEmpty(deviceSummaryGroupStoreId.getList())) {
                    break;
                }
                workbook = ExcelExportUtil.exportBigExcel(params, DeviceSummaryListDTO.class, deviceSummaryGroupStoreId.getList());
            }
            if (workbook == null) {
                log.error("设备汇总没有数据");
                importTaskDO.setStatus(ImportTaskStatusEnum.ERROR.getCode());
                importTaskDO.setRemark("设备汇总没有数据");
            } else {
                try {
                    ExcelExportUtil.closeExportBigExcel();
                } catch (Exception ex) {
                    log.error("设备汇总EXCEL文件导出对象关闭异常", ex);
                }
                exportAsyncService.asyncDynamicExportListFile(enterpriseId, importTaskDO, workbook, dbName);
            }
        } catch (Throwable e) {
            log.error("设备汇总获取数据失败", e);
            importTaskDO.setStatus(ImportTaskStatusEnum.ERROR.getCode());
            importTaskDO.setRemark("设备汇总导出数据异常");
        } finally {
            try {
                ExcelExportUtil.closeExportBigExcel();
            } catch (Exception ex) {
                log.error("设备汇总EXCEL文件导出对象关闭异常", ex);
            }
            importTaskService.updateImportTask(enterpriseId, importTaskDO);
        }
    }

    @Override
    public void exportSupervisionTask(String enterpriseId, Long totalNum, ImportTaskDO importTaskDO, Long parentId, String userName, List<SupervisionSubTaskStatusEnum> completeStatusList, CurrentUser currentUser, String dbName, Integer handleOverTimeStatus) {
        DataSourceHelper.changeToSpecificDataSource(dbName);
        ExportParams params = new ExportParams(null, null, ExcelType.XSSF);
        Workbook workbook = null;
        int pageSize = Constants.PAGE_SIZE;
        long pages = (totalNum + pageSize - 1) / pageSize;

        SupervisionTaskParentDetailVO supervisionTaskParentDetailVO = supervisionTaskParentService.selectDetailById(enterpriseId, parentId, currentUser);
        List<ExcelExportEntity> beanList = new ArrayList<>();
        //数据表
        beanList.add(new ExcelExportEntity("任务名称", "taskName"));
        beanList.add(new ExcelExportEntity("任务执行人", "supervisionHandleUserName"));
        beanList.add(new ExcelExportEntity("执行人职位", "roleName"));
        beanList.add(new ExcelExportEntity("执行人所属部门", "department"));
        beanList.add(new ExcelExportEntity("完成状态", "taskStatusStr"));
        beanList.add(new ExcelExportEntity("是否逾期", "handleOverTimeStatusStr"));
        beanList.add(new ExcelExportEntity("任务接收人", "tempName"));
        beanList.add(new ExcelExportEntity("接收人职位", "supervisionUserRoleName"));
        beanList.add(new ExcelExportEntity("接收人所属部门", "supervisionUserDepartment"));
        ExcelExportEntity completeTime = new ExcelExportEntity("完成时间", "completeTime");
        completeTime.setFormat(DateUtils.DATE_FORMAT_SEC);
        beanList.add(completeTime);

        ExcelExportEntity submitTime = new ExcelExportEntity("提交时间", "submitTime");
        submitTime.setFormat(DateUtils.DATE_FORMAT_SEC);
        beanList.add(submitTime);

        Map<Long, String> formatMap = new HashMap<>();
        if (StringUtils.isNotEmpty(supervisionTaskParentDetailVO.getFormId()) && CollectionUtils.isEmpty(supervisionTaskParentDetailVO.getStoreRangeList())) {
            List<TbMetaDefTableColumnDO> tbMetaDefTableColumnDOS = tbMetaDefTableColumnMapper.selectByTableId(enterpriseId, Long.valueOf(supervisionTaskParentDetailVO.getFormId()));
            formatMap = tbMetaDefTableColumnDOS.stream().collect(Collectors.toMap(TbMetaDefTableColumnDO::getId, TbMetaDefTableColumnDO::getFormat));
            for (TbMetaDefTableColumnDO tbMetaDefTableColumnDO : tbMetaDefTableColumnDOS) {
                beanList.add(new ExcelExportEntity(tbMetaDefTableColumnDO.getColumnName(), String.format("%d%s", tbMetaDefTableColumnDO.getId(), tbMetaDefTableColumnDO.getColumnName())));
            }
        }

        try {
            for (int pageNum = 1; pageNum <= pages; pageNum++) {
                // 查询店务门店统计导出数据
                PageInfo<SupervisionTaskDataVO> supervisionTaskDataVOPageInfo = supervisionTaskParentService.listSupervisionTaskByParentId(enterpriseId, parentId,
                        userName, completeStatusList, Constants.PAGE_SIZE, pageNum, handleOverTimeStatus);
                if (Objects.isNull(supervisionTaskDataVOPageInfo.getList()) || CollectionUtils.isEmpty(supervisionTaskDataVOPageInfo.getList())) {
                    break;
                }
                List<SupervisionTaskDataVO> result = supervisionTaskDataVOPageInfo.getList();
                List<Map<String, Object>> resultMap = new ArrayList<>();
                for (SupervisionTaskDataVO vo : result) {
                    //对应字段直接添加
                    Map<String, Object> map = BeanUtil.beanToMap(vo);
                    if (StringUtils.isNotEmpty(supervisionTaskParentDetailVO.getFormId()) && CollectionUtils.isNotEmpty(vo.getSupervisionDefDataColumnDTOS())) {
                        for (SupervisionDefDataColumnDTO supervisionDefDataColumnDTO : vo.getSupervisionDefDataColumnDTOS()) {
                            Object value1 = supervisionDefDataColumnDTO.getValue1();
                            if (value1 == null) {
                                value1 = "";
                            }
                            String s = formatMap.get(supervisionDefDataColumnDTO.getMetaColumnId());
                            if (StringUtils.isNotEmpty(s) && (s.equals("RangePicker") || s.equals("DatePicker"))) {
                                if (StringUtils.isNotEmpty(String.valueOf(value1)) && s.equals("DatePicker")) {
                                    value1 = DateUtil.format(DateUtil.longToDate(Long.valueOf(value1.toString())), DateUtils.DATE_FORMAT_DAY);
                                } else if (StringUtils.isNotEmpty(String.valueOf(value1)) && s.equals("RangePicker")) {
                                    String[] split = value1.toString().split(Constants.COMMA);
                                    ;
                                    String startTime = DateUtil.format(DateUtil.longToDate((Long.valueOf(split[0]))), DateUtils.DATE_FORMAT_DAY);
                                    String endTime = DateUtil.format(DateUtil.longToDate((Long.valueOf(split[1]))), DateUtils.DATE_FORMAT_DAY);
                                    value1 = String.format("%s-%s", startTime, endTime);
                                }
                            }
                            if (StringUtils.isNotBlank(supervisionDefDataColumnDTO.getCheckVideo())) {
                                SmallVideoInfoDTO handleVideoInfo = JSONObject.parseObject(supervisionDefDataColumnDTO.getCheckVideo(), SmallVideoInfoDTO.class);
                                String handleVideo = CollectionUtils.emptyIfNull(handleVideoInfo.getVideoList())
                                        .stream().map(SmallVideoDTO::getVideoUrl).filter(Objects::nonNull).collect(Collectors.joining(Constants.COMMA));
                                if (StringUtils.isNotBlank(String.valueOf(value1))) {
                                    value1 = value1 + Constants.COMMA + handleVideo;
                                } else {
                                    value1 = value1 + handleVideo;
                                }
                            }
                            map.put(String.format("%d%s", supervisionDefDataColumnDTO.getMetaColumnId(), supervisionDefDataColumnDTO.getMetaColumnName()), value1);
                        }
                    }
                    resultMap.add(map);
                }
                workbook = ExcelExportUtil.exportBigExcel(params, beanList, resultMap);
                result.clear();
                resultMap.clear();
            }
            if (workbook == null) {
                log.error("督导助手任务列表没有数据");
                importTaskDO.setStatus(ImportTaskStatusEnum.ERROR.getCode());
                importTaskDO.setRemark("督导助手任务列表没有数据");
            } else {
                try {
                    ExcelExportUtil.closeExportBigExcel();
                } catch (Exception ex) {
                    log.error("督导助手任务列表EXCEL文件导出对象关闭异常", ex);
                }
                exportAsyncService.asyncDynamicExportListFile(enterpriseId, importTaskDO, workbook, dbName);
            }
        } catch (Throwable e) {
            log.error("督导助手任务列表获取数据失败", e);
            importTaskDO.setStatus(ImportTaskStatusEnum.ERROR.getCode());
            importTaskDO.setRemark("督导助手任务列表导出数据异常");
        } finally {
            try {
                ExcelExportUtil.closeExportBigExcel();
            } catch (Exception ex) {
                log.error("督导助手任务列表EXCEL文件导出对象关闭异常", ex);
            }
            importTaskService.updateImportTask(enterpriseId, importTaskDO);
        }
    }

    @Override
    public void exportSupervisionStoreTask(String enterpriseId, Long totalNum, ImportTaskDO importTaskDO, Long parentId, List<String> storeIds, String userName, List<SupervisionSubTaskStatusEnum> completeStatusList,
                                           CurrentUser currentUser, String dbName, Long taskId, List<String> regionIds, Integer handleOverTimeStatus) {
        DataSourceHelper.changeToSpecificDataSource(dbName);
        ExportParams params = new ExportParams(null, null, ExcelType.XSSF);
        Workbook workbook = null;
        int pageSize = Constants.PAGE_SIZE;
        long pages = (totalNum + pageSize - 1) / pageSize;

        SupervisionTaskParentDetailVO supervisionTaskParentDetailVO = supervisionTaskParentService.selectDetailById(enterpriseId, parentId, currentUser);
        List<ExcelExportEntity> beanList = new ArrayList<>();
        //数据表
        beanList.add(new ExcelExportEntity("任务名称", "taskName"));
        beanList.add(new ExcelExportEntity("门店名称", "storeName"));
        beanList.add(new ExcelExportEntity("任务执行人", "supervisionHandleUserName"));
        beanList.add(new ExcelExportEntity("执行人职位", "roleName"));
        beanList.add(new ExcelExportEntity("执行人所属部门", "department"));
        beanList.add(new ExcelExportEntity("完成状态", "taskStatusStr"));
        beanList.add(new ExcelExportEntity("是否逾期", "handleOverTimeStatusStr"));
        beanList.add(new ExcelExportEntity("任务接收人", "tempName"));
        beanList.add(new ExcelExportEntity("接收人职位", "supervisionUserRoleName"));
        beanList.add(new ExcelExportEntity("接收人所属部门", "supervisionUserDepartment"));
        ExcelExportEntity completeTime = new ExcelExportEntity("完成时间", "completeTime");
        completeTime.setFormat(DateUtils.DATE_FORMAT_SEC);
        beanList.add(completeTime);

        ExcelExportEntity submitTime = new ExcelExportEntity("提交时间", "submitTime");
        submitTime.setFormat(DateUtils.DATE_FORMAT_SEC);
        beanList.add(submitTime);

        Map<Long, String> formatMap = new HashMap<>();
        if (StringUtils.isNotEmpty(supervisionTaskParentDetailVO.getFormId())) {
            List<TbMetaDefTableColumnDO> tbMetaDefTableColumnDOS = tbMetaDefTableColumnMapper.selectByTableId(enterpriseId, Long.valueOf(supervisionTaskParentDetailVO.getFormId()));
            formatMap = tbMetaDefTableColumnDOS.stream().collect(Collectors.toMap(TbMetaDefTableColumnDO::getId, TbMetaDefTableColumnDO::getFormat));
            for (TbMetaDefTableColumnDO tbMetaDefTableColumnDO : tbMetaDefTableColumnDOS) {
                beanList.add(new ExcelExportEntity(tbMetaDefTableColumnDO.getColumnName(), String.format("%d%s", tbMetaDefTableColumnDO.getId(), tbMetaDefTableColumnDO.getColumnName())));
            }
        }
        try {
            for (int pageNum = 1; pageNum <= pages; pageNum++) {
                // 查询店务门店统计导出数据
                PageInfo<SupervisionStoreTaskDataVO> supervisionStoreTaskDataVOPageInfo = supervisionTaskParentService.listSupervisionStoreTaskByParentId(enterpriseId, parentId, taskId,
                        storeIds, regionIds, userName, completeStatusList, Constants.PAGE_SIZE, pageNum, handleOverTimeStatus);
                if (Objects.isNull(supervisionStoreTaskDataVOPageInfo.getList()) || CollectionUtils.isEmpty(supervisionStoreTaskDataVOPageInfo.getList())) {
                    break;
                }
                List<SupervisionStoreTaskDataVO> result = supervisionStoreTaskDataVOPageInfo.getList();
                List<Map<String, Object>> resultMap = new ArrayList<>();
                for (SupervisionStoreTaskDataVO vo : result) {
                    //对应字段直接添加
                    Map<String, Object> map = BeanUtil.beanToMap(vo);
                    if (StringUtils.isNotEmpty(supervisionTaskParentDetailVO.getFormId())) {
                        for (SupervisionDefDataColumnDTO supervisionDefDataColumnDTO : vo.getSupervisionDefDataColumnDTOS()) {
                            Object value1 = supervisionDefDataColumnDTO.getValue1();
                            if (value1 == null) {
                                value1 = "";
                            }
                            String s = formatMap.get(supervisionDefDataColumnDTO.getMetaColumnId());
                            if (StringUtils.isNotEmpty(s) && (s.equals("RangePicker") || s.equals("DatePicker"))) {
                                if (StringUtils.isNotEmpty(String.valueOf(value1)) && s.equals("DatePicker")) {
                                    value1 = DateUtil.format(DateUtil.longToDate(Long.valueOf(value1.toString())), DateUtils.DATE_FORMAT_DAY);
                                } else if (StringUtils.isNotEmpty(String.valueOf(value1)) && s.equals("RangePicker")) {
                                    String[] split = value1.toString().split(Constants.COMMA);
                                    ;
                                    String startTime = DateUtil.format(DateUtil.longToDate((Long.valueOf(split[0]))), DateUtils.DATE_FORMAT_DAY);
                                    String endTime = DateUtil.format(DateUtil.longToDate((Long.valueOf(split[1]))), DateUtils.DATE_FORMAT_DAY);
                                    value1 = String.format("%s-%s", startTime, endTime);
                                }
                            }
                            if (StringUtils.isNotBlank(supervisionDefDataColumnDTO.getCheckVideo())) {
                                SmallVideoInfoDTO handleVideoInfo = JSONObject.parseObject(supervisionDefDataColumnDTO.getCheckVideo(), SmallVideoInfoDTO.class);
                                String handleVideo = CollectionUtils.emptyIfNull(handleVideoInfo.getVideoList())
                                        .stream().map(SmallVideoDTO::getVideoUrl).filter(Objects::nonNull).collect(Collectors.joining(Constants.COMMA));
                                if (StringUtils.isNotBlank(String.valueOf(value1))) {
                                    value1 = value1 + Constants.COMMA + handleVideo;
                                } else {
                                    value1 = value1 + handleVideo;
                                }
                            }
                            map.put(String.format("%d%s", supervisionDefDataColumnDTO.getMetaColumnId(), supervisionDefDataColumnDTO.getMetaColumnName()), value1);
                        }
                    }
                    resultMap.add(map);
                }
                workbook = ExcelExportUtil.exportBigExcel(params, beanList, resultMap);
                result.clear();
                resultMap.clear();
            }
            if (workbook == null) {
                log.error("督导助手任务列表没有数据");
                importTaskDO.setStatus(ImportTaskStatusEnum.ERROR.getCode());
                importTaskDO.setRemark("督导助手任务列表没有数据");
            } else {
                try {
                    ExcelExportUtil.closeExportBigExcel();
                } catch (Exception ex) {
                    log.error("督导助手任务列表EXCEL文件导出对象关闭异常", ex);
                }
                exportAsyncService.asyncDynamicExportListFile(enterpriseId, importTaskDO, workbook, dbName);
            }
        } catch (Throwable e) {
            log.error("督导助手任务列表获取数据失败", e);
            importTaskDO.setStatus(ImportTaskStatusEnum.ERROR.getCode());
            importTaskDO.setRemark("督导助手任务列表导出数据异常");
        } finally {
            try {
                ExcelExportUtil.closeExportBigExcel();
            } catch (Exception ex) {
                log.error("督导助手任务列表EXCEL文件导出对象关闭异常", ex);
            }
            importTaskService.updateImportTask(enterpriseId, importTaskDO);
        }
    }

    @Override
    public void exportSupervisionDataDetail(String enterpriseId, Long totalNum, ImportTaskDO importTaskDO, List<Long> parentIds, String formId, Long submitStartTime, Long submitEndTime, String type, CurrentUser currentUser, String dbName) {
        DataSourceHelper.changeToSpecificDataSource(dbName);
        ExportParams params = new ExportParams(null, null, ExcelType.XSSF);
        Workbook workbook = null;
        int pageSize = Constants.PAGE_SIZE;
        long pages = (totalNum + pageSize - 1) / pageSize;

        List<ExcelExportEntity> beanList = new ArrayList<>();
        //数据表
        beanList.add(new ExcelExportEntity("用户信息", "supervisionUserName"));
        beanList.add(new ExcelExportEntity("职位", "roleName"));
        beanList.add(new ExcelExportEntity("所属部门", "department"));
        beanList.add(new ExcelExportEntity("任务名称", "taskName"));
        beanList.add(new ExcelExportEntity("任务ID", "id"));
        beanList.add(new ExcelExportEntity("门店名称", "storeName"));
        ExcelExportEntity completeTime = new ExcelExportEntity("填写时间", "submitTime");
        completeTime.setFormat(DateUtils.DATE_FORMAT_SEC);
        beanList.add(completeTime);
        Map<Long, String> formatMap = new HashMap<>();
        if (StringUtils.isNotEmpty(formId)) {
            List<TbMetaDefTableColumnDO> tbMetaDefTableColumnDOS = tbMetaDefTableColumnMapper.selectByTableId(enterpriseId, Long.valueOf(formId));
            formatMap = tbMetaDefTableColumnDOS.stream().collect(Collectors.toMap(TbMetaDefTableColumnDO::getId, TbMetaDefTableColumnDO::getFormat));
            for (TbMetaDefTableColumnDO tbMetaDefTableColumnDO : tbMetaDefTableColumnDOS) {
                beanList.add(new ExcelExportEntity(tbMetaDefTableColumnDO.getColumnName(), String.format("%d%s", tbMetaDefTableColumnDO.getId(), tbMetaDefTableColumnDO.getColumnName())));
            }
        }
        try {
            for (int pageNum = 1; pageNum <= pages; pageNum++) {
                // 查询店务门店统计导出数据
                PageInfo<SupervisionStoreTaskDataVO> supervisionStoreTaskDataVOPageInfo = supervisionTaskParentService.taskDetail(enterpriseId, Long.valueOf(formId), parentIds, submitStartTime, submitEndTime, type, pageSize, pageNum);
                if (Objects.isNull(supervisionStoreTaskDataVOPageInfo.getList()) || CollectionUtils.isEmpty(supervisionStoreTaskDataVOPageInfo.getList())) {
                    break;
                }
                List<SupervisionStoreTaskDataVO> result = supervisionStoreTaskDataVOPageInfo.getList();
                List<Map<String, Object>> resultMap = new ArrayList<>();
                for (SupervisionStoreTaskDataVO vo : result) {
                    //对应字段直接添加
                    Map<String, Object> map = BeanUtil.beanToMap(vo);
                    if (StringUtils.isNotEmpty(formId)) {
                        for (SupervisionDefDataColumnDTO supervisionDefDataColumnDTO : vo.getSupervisionDefDataColumnDTOS()) {
                            Object value1 = supervisionDefDataColumnDTO.getValue1();
                            if (value1 == null) {
                                value1 = "";
                            }
                            String s = formatMap.get(supervisionDefDataColumnDTO.getMetaColumnId());
                            if (StringUtils.isNotEmpty(s) && (s.equals("RangePicker") || s.equals("DatePicker"))) {
                                if (StringUtils.isNotEmpty(String.valueOf(value1)) && s.equals("DatePicker")) {
                                    value1 = DateUtil.format(DateUtil.longToDate(Long.valueOf(value1.toString())), DateUtils.DATE_FORMAT_DAY);
                                } else if (StringUtils.isNotEmpty(String.valueOf(value1)) && s.equals("RangePicker")) {
                                    String[] split = value1.toString().split(Constants.COMMA);
                                    ;
                                    String startTime = DateUtil.format(DateUtil.longToDate((Long.valueOf(split[0]))), DateUtils.DATE_FORMAT_DAY);
                                    String endTime = DateUtil.format(DateUtil.longToDate((Long.valueOf(split[1]))), DateUtils.DATE_FORMAT_DAY);
                                    value1 = String.format("%s-%s", startTime, endTime);
                                }
                            }
                            if (StringUtils.isNotBlank(supervisionDefDataColumnDTO.getCheckVideo())) {
                                SmallVideoInfoDTO handleVideoInfo = JSONObject.parseObject(supervisionDefDataColumnDTO.getCheckVideo(), SmallVideoInfoDTO.class);
                                String handleVideo = CollectionUtils.emptyIfNull(handleVideoInfo.getVideoList())
                                        .stream().map(SmallVideoDTO::getVideoUrl).filter(Objects::nonNull).collect(Collectors.joining(Constants.COMMA));
                                if (StringUtils.isNotBlank(String.valueOf(value1))) {
                                    value1 = value1 + Constants.COMMA + handleVideo;
                                } else {
                                    value1 = value1 + handleVideo;
                                }
                            }
                            map.put(String.format("%d%s", supervisionDefDataColumnDTO.getMetaColumnId(), supervisionDefDataColumnDTO.getMetaColumnName()), value1);
                        }
                    }
                    resultMap.add(map);
                }
                workbook = ExcelExportUtil.exportBigExcel(params, beanList, resultMap);
                result.clear();
                resultMap.clear();
            }
            if (workbook == null) {
                log.error("督导任务数据明细没有数据");
                importTaskDO.setStatus(ImportTaskStatusEnum.ERROR.getCode());
                importTaskDO.setRemark("督导任务数据明细没有数据");
            } else {
                try {
                    ExcelExportUtil.closeExportBigExcel();
                } catch (Exception ex) {
                    log.error("督导任务数据明细EXCEL文件导出对象关闭异常", ex);
                }
                exportAsyncService.asyncDynamicExportListFile(enterpriseId, importTaskDO, workbook, dbName);
            }
        } catch (Throwable e) {
            log.error("督导任务数据明细获取数据失败", e);
            importTaskDO.setStatus(ImportTaskStatusEnum.ERROR.getCode());
            importTaskDO.setRemark("督导任务数据明细导出数据异常");
        } finally {
            try {
                ExcelExportUtil.closeExportBigExcel();
            } catch (Exception ex) {
                log.error("督导任务数据明细EXCEL文件导出对象关闭异常", ex);
            }
            importTaskService.updateImportTask(enterpriseId, importTaskDO);
        }
    }

    @Override
    public void exportConfidenceFeedback(String enterpriseId, Long totalNum, ImportTaskDO importTaskDO, ConfidenceFeedbackPageDTO request, CurrentUser currentUser, String dbName) {
        DataSourceHelper.changeToSpecificDataSource(dbName);
        ExportParams params = new ExportParams(null, null, ExcelType.XSSF);
        Workbook workbook = null;
        int pageSize = Constants.PAGE_SIZE;
        long pages = (totalNum + pageSize - 1) / pageSize;
        try {
            for (int pageNum = 1; pageNum <= pages; pageNum++) {
                request.setPageNum(pageNum);
                request.setPageSize(pageSize);
                List<ConfidenceFeedbackDetailVO> confidenceFeedbackPage = confidenceFeedbackService.exportConfidenceFeedbackPage(enterpriseId, request);
                List<ConfidenceFeedbackExportDTO> result = new ArrayList<>();
                for (ConfidenceFeedbackDetailVO dto : confidenceFeedbackPage) {
                    result.add(ConfidenceFeedbackExportDTO.convert(dto));
                }
                workbook = ExcelExportUtil.exportBigExcel(params, ConfidenceFeedbackExportDTO.class, result);
                result.clear();
            }
            if (workbook == null) {
                log.error("信息反馈没有数据");
                importTaskDO.setStatus(ImportTaskStatusEnum.ERROR.getCode());
                importTaskDO.setRemark("信息反馈没有数据");
            } else {
                try {
                    ExcelExportUtil.closeExportBigExcel();
                } catch (Exception ex) {
                    log.error("信息反馈EXCEL文件导出对象关闭异常", ex);
                }
                exportAsyncService.asyncDynamicExportListFile(enterpriseId, importTaskDO, workbook, dbName);
            }
        } catch (Throwable e) {
            log.error("信息反馈获取数据失败", e);
            importTaskDO.setStatus(ImportTaskStatusEnum.ERROR.getCode());
            importTaskDO.setRemark("信息反馈导出数据异常");
        } finally {
            try {
                ExcelExportUtil.closeExportBigExcel();
            } catch (Exception ex) {
                log.error("信息反馈EXCEL文件导出对象关闭异常", ex);
            }
            importTaskService.updateImportTask(enterpriseId, importTaskDO);
        }
    }

    @Override
    public void exportActivityUser(String enterpriseId, ImportTaskDO importTaskDO, Long activityId, String dbName) {
        DataSourceHelper.changeToSpecificDataSource(dbName);
        ExportParams params = new ExportParams(null, null, ExcelType.XSSF);
        Workbook workbook = null;
        try {
            List<ActivityUserVO> activityUserList = activityService.getActivityUserList(enterpriseId, activityId);
            workbook = ExcelExportUtil.exportBigExcel(params, ActivityUserVO.class, activityUserList);
            if (workbook == null) {
                log.error("活动人员没有数据");
                importTaskDO.setStatus(ImportTaskStatusEnum.ERROR.getCode());
                importTaskDO.setRemark("活动人员没有数据");
            } else {
                try {
                    ExcelExportUtil.closeExportBigExcel();
                } catch (Exception ex) {
                    log.error("信活动人员EXCEL文件导出对象关闭异常", ex);
                }
                exportAsyncService.asyncDynamicExportListFile(enterpriseId, importTaskDO, workbook, dbName);
            }
        } catch (Throwable e) {
            log.error("活动人员获取数据失败", e);
            importTaskDO.setStatus(ImportTaskStatusEnum.ERROR.getCode());
            importTaskDO.setRemark("活动人员导出数据异常");
        } finally {
            try {
                ExcelExportUtil.closeExportBigExcel();
            } catch (Exception ex) {
                log.error("活动人员EXCEL文件导出对象关闭异常", ex);
            }
            importTaskService.updateImportTask(enterpriseId, importTaskDO);
        }
    }

    @Override
    public void exportActivityComment(String enterpriseId, ImportTaskDO importTaskDO, Long activityId, Long totalNum, String dbName) {
        DataSourceHelper.changeToSpecificDataSource(dbName);
        ExportParams params = new ExportParams(null, null, ExcelType.XSSF);
        Workbook workbook = null;
        int pageSize = Constants.PAGE_SIZE;
        long pages = (totalNum + pageSize - 1) / pageSize;
        try {
            for (int pageNum = 1; pageNum <= pages; pageNum++) {
                PageInfo<ActivityCommentExportVO> activityCommentExportVOPageInfo = activityService.getActivityCommentList(enterpriseId, activityId, pageNum, pageSize);
                workbook = ExcelExportUtil.exportBigExcel(params, ActivityCommentExportVO.class, activityCommentExportVOPageInfo.getList());
            }
            if (workbook == null) {
                log.error("活动点评没有数据");
                importTaskDO.setStatus(ImportTaskStatusEnum.ERROR.getCode());
                importTaskDO.setRemark("活动点评没有数据");
            } else {
                try {
                    ExcelExportUtil.closeExportBigExcel();
                } catch (Exception ex) {
                    log.error("活动点评EXCEL文件导出对象关闭异常", ex);
                }
                exportAsyncService.asyncDynamicExportListFile(enterpriseId, importTaskDO, workbook, dbName);
            }
        } catch (Throwable e) {
            log.error("活动点评获取数据失败", e);
            importTaskDO.setStatus(ImportTaskStatusEnum.ERROR.getCode());
            importTaskDO.setRemark("活动点评导出数据异常");
        } finally {
            try {
                ExcelExportUtil.closeExportBigExcel();
            } catch (Exception ex) {
                log.error("活动点评EXCEL文件导出对象关闭异常", ex);
            }
            importTaskService.updateImportTask(enterpriseId, importTaskDO);
        }
    }

    @Override
    public void exportWeeklyNewspaperList(String enterpriseId, ImportTaskDO importTaskDO, Long totalNum, String dbName) {
        DataSourceHelper.changeToSpecificDataSource(dbName);
        ExportParams params = new ExportParams(null, null, ExcelType.XSSF);
        Workbook workbook = null;
        int pageSize = Constants.PAGE_SIZE;
        long pages = (totalNum + pageSize - 1) / pageSize;
        List<QyyWeeklyNewspaperDO> list = new ArrayList<>();
        try {
            for (int pageNum = 1; pageNum <= pages; pageNum++) {
                StoreNewsPaperDTO paperDTO = new StoreNewsPaperDTO();
                paperDTO.setPageNum(pageNum);
                paperDTO.setPageSize(pageSize);
//                PageInfo<QyyWeeklyNewspaperDO> weeklyNewspaper = qyyWeeklyNewspaperDAO.storeWeeklyNewsPaperByPage(enterpriseId, paperDTO);
                PageInfo<QyyWeeklyNewspaperDO> weeklyNewspaper = qyyWeeklyNewspaperDAO.storeWeeklyNewsPaperByPageNoParam(enterpriseId,paperDTO.getPageNum(),paperDTO.getPageSize());
                log.info("exportWeeklyNewspaperListWeeklyNewspaper:{}", JSONObject.toJSONString(weeklyNewspaper.getList()));
                list.addAll(weeklyNewspaper.getList());
            }
            workbook = ExcelExportUtil.exportBigExcel(params, QyyWeeklyNewspaperDO.class, list);
            list.clear();
            if (workbook == null) {
                log.error("周报列表没有数据");
                importTaskDO.setStatus(ImportTaskStatusEnum.ERROR.getCode());
                importTaskDO.setRemark("周报列表没有数据");
            } else {
                try {
                    ExcelExportUtil.closeExportBigExcel();
                } catch (Exception ex) {
                    log.error("周报列表EXCEL文件导出对象关闭异常", ex);
                }
                exportAsyncService.asyncDynamicExportListFile(enterpriseId, importTaskDO, workbook, dbName);
            }
        } catch (Throwable e) {
            log.error("周报列表获取数据失败", e);
            importTaskDO.setStatus(ImportTaskStatusEnum.ERROR.getCode());
            importTaskDO.setRemark("周报列表导出数据异常");
        } finally {
            try {
                ExcelExportUtil.closeExportBigExcel();
            } catch (Exception ex) {
                log.error("周报列表EXCEL文件导出对象关闭异常", ex);
            }
            importTaskService.updateImportTask(enterpriseId, importTaskDO);
        }
    }

    @Override
    public void exportPatrolStoreReviewList(String enterpriseId, ImportTaskDO importTaskDO, Long totalNum, String dbName,List<String> recordIds) {
        DataSourceHelper.changeToSpecificDataSource(dbName);
        ExportParams params = new ExportParams(null, null, ExcelType.XSSF);
        Workbook workbook = null;
        int pageSize = Constants.BATCH_INSERT_COUNT;
        long pages = (totalNum + pageSize - 1) / pageSize;
        try {
            for (int pageNum = 1; pageNum <= pages; pageNum++) {
                List<PatrolStoreReviewVO> vos = tbDataStaTableColumnDao.exportPatrolStoreReviewList(enterpriseId, pageNum, pageSize, recordIds);
                if (CollectionUtils.isNotEmpty(vos)){
                    workbook = ExcelExportUtil.exportBigExcel(params, PatrolStoreReviewVO.class, vos);
                }
            }
            if (workbook == null) {
                log.error("复审任务列表没有数据");
                importTaskDO.setStatus(ImportTaskStatusEnum.ERROR.getCode());
                importTaskDO.setRemark("复审任务列表没有数据");
            } else {
                try {
                    ExcelExportUtil.closeExportBigExcel();
                } catch (Exception ex) {
                    log.error("复审任务列表EXCEL文件导出对象关闭异常", ex);
                }
                exportAsyncService.asyncDynamicExportListFile(enterpriseId, importTaskDO, workbook, dbName);
            }
        } catch (Throwable e) {
            log.error("复审任务列表获取数据失败", e);
            e.printStackTrace();
            importTaskDO.setStatus(ImportTaskStatusEnum.ERROR.getCode());
            importTaskDO.setRemark("复审任务列表导出数据异常");
        } finally {
            try {
                ExcelExportUtil.closeExportBigExcel();
            } catch (Exception ex) {
                log.error("复审任务列表EXCEL文件导出对象关闭异常", ex);
            }
            importTaskService.updateImportTask(enterpriseId, importTaskDO);
        }
    }

    @Override
    public void exportUserInfo(String enterpriseId, ImportTaskDO importTaskDO, Long totalNum, String dbName, JSONObject request) {
        String isHistory = redisUtilPool.hashGet(RedisConstant.HISTORY_ENTERPRISE, enterpriseId);
        Boolean isHistoryEnterprise = StringUtils.isNotBlank(isHistory);
        DataSourceHelper.changeToSpecificDataSource(dbName);
        int pageSize = Constants.BATCH_INSERT_COUNT;
        long pages = (totalNum + pageSize - 1) / pageSize;
        String template = isHistoryEnterprise ? "template/批量导入用户-1.xlsx":"template/批量导入用户.xlsx";
        try {
            XSSFWorkbook xssfWorkbook = new XSSFWorkbook(this.getClass().getClassLoader().getResourceAsStream(template));
            Workbook workbook = new SXSSFWorkbook(xssfWorkbook);
            int index = Constants.INDEX_THREE;
            for (int pageNum = 1; pageNum <= pages; pageNum++) {
                List vos = userExportService.exportList(enterpriseId, request, pageSize, pageNum);
                if (CollectionUtils.isEmpty(vos)) {
                    break;
                }
                workbook = new CustomExcelExportUtil().exportBigExcel(index, workbook, vos);
                index = index + vos.size();
            }

            if (workbook == null) {
                log.error("用户导出没有数据");
                importTaskDO.setStatus(ImportTaskStatusEnum.ERROR.getCode());
                importTaskDO.setRemark("复审任务列表没有数据");
            } else {
                try {
                    ExcelExportUtil.closeExportBigExcel();
                } catch (Exception ex) {
                    log.error("用户导出列表EXCEL文件导出对象关闭异常", ex);
                }
                exportAsyncService.asyncDynamicExportListFile(enterpriseId, importTaskDO, workbook, dbName);
            }
        } catch (Throwable e) {
            log.error("用户导出任务列表获取数据失败", e);
            e.printStackTrace();
            importTaskDO.setStatus(ImportTaskStatusEnum.ERROR.getCode());
            importTaskDO.setRemark("用户导出任务列表导出数据异常");
        } finally {
            try {
                ExcelExportUtil.closeExportBigExcel();
            } catch (Exception ex) {
                log.error("用户导出任务列表EXCEL文件导出对象关闭异常", ex);
            }
            importTaskService.updateImportTask(enterpriseId, importTaskDO);
        }
    }

    @Override
    public void externalUserInfoExport(String enterpriseId, Long totalNum, ImportTaskDO importTaskDO, UserInfoExportRequest request, String dbName, CurrentUser user) {
        DataSourceHelper.changeToSpecificDataSource(dbName);
        RegionNode rootRegion = regionService.getRootRegion(enterpriseId);
        String rootName = Optional.ofNullable(rootRegion).map(o->o.getName()).orElse("");
        int pageSize = Constants.BATCH_INSERT_COUNT;
        long pages = (totalNum + pageSize - 1) / pageSize;
        List<ExternalUserInfoExportDTO> list = new ArrayList<>();
        String template = "template/外部用户导入模板.xlsx";
        try {
            XSSFWorkbook xssfWorkbook = new XSSFWorkbook(this.getClass().getClassLoader().getResourceAsStream(template));
            Workbook workbook = new SXSSFWorkbook(xssfWorkbook);
            for (int pageNum = 1; pageNum <= pages; pageNum++) {
                List<UserInfoExportDTO> vos = userExportService.exportUserList(enterpriseId, request, pageSize, pageNum);
                List<ExternalUserInfoExportDTO> externalUserInfoExports = ExternalUserInfoExportDTO.convertList(rootName, vos);
                list.addAll(externalUserInfoExports);
            }
            workbook = new CustomExcelExportUtil().exportBigExcel(Constants.INDEX_THREE, workbook, list);
            if (workbook == null) {
                log.error("外部用户导出没有数据");
                importTaskDO.setStatus(ImportTaskStatusEnum.ERROR.getCode());
                importTaskDO.setRemark("外部用户列表没有数据");
            } else {
                try {
                    ExcelExportUtil.closeExportBigExcel();
                } catch (Exception ex) {
                    log.error("外部用户导出列表EXCEL文件导出对象关闭异常", ex);
                }
                exportAsyncService.asyncDynamicExportListFile(enterpriseId, importTaskDO, workbook, dbName);
            }
        } catch (Throwable e) {
            log.error("外部用户导出任务列表获取数据失败", e);
            e.printStackTrace();
            importTaskDO.setStatus(ImportTaskStatusEnum.ERROR.getCode());
            importTaskDO.setRemark("用户导出任务列表导出数据异常");
        } finally {
            try {
                ExcelExportUtil.closeExportBigExcel();
            } catch (Exception ex) {
                log.error("外部用户导出任务列表EXCEL文件导出对象关闭异常", ex);
            }
            importTaskService.updateImportTask(enterpriseId, importTaskDO);
        }
    }

    @Override
    public void externalRegionExport(String enterpriseId, Long totalNum, ImportTaskDO importTaskDO, ExternalRegionExportRequest request, String dbName, CurrentUser user) {
        DataSourceHelper.changeToSpecificDataSource(dbName);
        int pageSize = Constants.BATCH_INSERT_COUNT;
        long pages = (totalNum + pageSize - 1) / pageSize;
        List<RegionDO> allExternalRegionlist = new ArrayList<>();
        String template = "template/批量导入外部用户架构.xlsx";
        try {
            XSSFWorkbook xssfWorkbook = new XSSFWorkbook(this.getClass().getClassLoader().getResourceAsStream(template));
            Workbook workbook = new SXSSFWorkbook(xssfWorkbook);
            for (int pageNum = 1; pageNum <= pages; pageNum++) {
                List<RegionDO> regionList = regionService.exportExternalRegionList(enterpriseId, request, pageNum, pageSize);
                allExternalRegionlist.addAll(regionList);
            }
            List<ExternalRegionExportDTO> list = ExternalRegionExportDTO.convertList(allExternalRegionlist);
            workbook = new CustomExcelExportUtil().exportBigExcel(Constants.INDEX_THREE, workbook, list);
            if (workbook == null) {
                log.error("外部用户架构导出没有数据");
                importTaskDO.setStatus(ImportTaskStatusEnum.ERROR.getCode());
                importTaskDO.setRemark("外部用户架构没有数据");
            } else {
                try {
                    ExcelExportUtil.closeExportBigExcel();
                } catch (Exception ex) {
                    log.error("用户导出列表EXCEL文件导出对象关闭异常", ex);
                }
                exportAsyncService.asyncDynamicExportListFile(enterpriseId, importTaskDO, workbook, dbName);
            }
        } catch (Throwable e) {
            log.error("外部用户架构导出任务列表获取数据失败", e);
            e.printStackTrace();
            importTaskDO.setStatus(ImportTaskStatusEnum.ERROR.getCode());
            importTaskDO.setRemark("外部用户架构导出任务列表导出数据异常");
        } finally {
            try {
                ExcelExportUtil.closeExportBigExcel();
            } catch (Exception ex) {
                log.error("外部用户架构导出任务列表EXCEL文件导出对象关闭异常", ex);
            }
            importTaskService.updateImportTask(enterpriseId, importTaskDO);
        }
    }

    @Override
    public void storeWorkRecordListDetailExport(String enterpriseId, Long totalNum, ImportTaskDO importTaskDO, StoreWorkRecordListRequest request, CurrentUser user, String dbName) {
        DataSourceHelper.changeToSpecificDataSource(dbName);
        ExportParams params = new ExportParams(null, null, ExcelType.XSSF);
        Workbook workbook = null;
        int pageSize = Constants.PAGE_SIZE;
        long pages = (totalNum + pageSize - 1) / pageSize;

        List<ExcelExportEntity> beanList = new ArrayList<>();
        //数据表
        AtomicInteger orderNum = new AtomicInteger();
        ExcelExportEntity storeEntity = new ExcelExportEntity("门店名称", "storeName");
        storeEntity.setOrderNum(orderNum.getAndIncrement());
        beanList.add(storeEntity);
        ExcelExportEntity storeNumEntity = new ExcelExportEntity("门店编号", "storeNum");
        storeNumEntity.setOrderNum(orderNum.getAndIncrement());
        beanList.add(storeNumEntity);
        ExcelExportEntity storeScoreEntity = new ExcelExportEntity("门店得分", "score");
        storeScoreEntity.setOrderNum(orderNum.getAndIncrement());
        beanList.add(storeScoreEntity);
        ExcelExportEntity storeWorkDateEntity = new ExcelExportEntity("店务日期", "storeWorkDate");
        storeWorkDateEntity.setOrderNum(orderNum.getAndIncrement());
        beanList.add(storeWorkDateEntity);
        request.setPageSize(1);
        request.setPageNumber(1);

        PageInfo<SwStoreWorkRecordDetailVO> swStoreWorkRecordDetailVOPageInfo = storeWorkRecordService.storeWorkRecordDetailList(enterpriseId, request, user);
        List<SwStoreWorkRecordDetailVO> list = swStoreWorkRecordDetailVOPageInfo.getList();
        SwStoreWorkRecordDetailVO swStoreWorkRecordDetailVO = new SwStoreWorkRecordDetailVO();
        if (CollectionUtils.isNotEmpty(list)) {
            swStoreWorkRecordDetailVO = list.get(0);
        }
        List<SwStoreWorkTableDTO> swStoreWorkTableDTOS = swStoreWorkRecordDetailVO.getSwStoreWorkTableDTOS();
        AtomicInteger num = new AtomicInteger(1);
        swStoreWorkTableDTOS.forEach(data -> {
            List<SwStoreWorkColumnResultDTO> swStoreWorkColumnResultDTOS = data.getSwStoreWorkColumnResultDTOS();
            String tableName = data.getTableName() + "(" + DateUtil.format(data.getEndTime(), DateUtils.DATE_FORMAT_SEC) + "-" +
                    DateUtil.format(data.getEndTime(), DateUtils.DATE_FORMAT_SEC + ")" + "(" + num + ")");

            swStoreWorkColumnResultDTOS.forEach(sw -> {
                ExcelExportEntity exColumn = new ExcelExportEntity(sw.getColumnName(), "columnName_" + data.getTableMappingId() + Constants.UNDERLINE + sw.getTbMetaColumnId());
                exColumn.setGroupName(tableName);
                exColumn.setOrderNum(orderNum.getAndIncrement());
                beanList.add(exColumn);
                ExcelExportEntity score = new ExcelExportEntity("项得分", "score_" + data.getTableMappingId() + Constants.UNDERLINE + sw.getTbMetaColumnId());
                score.setGroupName(tableName);
                score.setOrderNum(orderNum.getAndIncrement());
                beanList.add(score);
            });
            ExcelExportEntity totalScore = new ExcelExportEntity("检查表得分", "totalScore_" + data.getTableMappingId());
            totalScore.setGroupName(tableName);
            totalScore.setOrderNum(orderNum.getAndIncrement());
            beanList.add(totalScore);
            num.getAndIncrement();
        });
        //10级区域
        beanList.addAll(ExportUtil.getRegionExportEntityList());
        try {
            request.setPageSize(pageSize);
            for (int pageNum = 1; pageNum <= pages; pageNum++) {
                // 查询店务门店统计导出数据
                request.setPageNumber(pageNum);
                PageInfo<SwStoreWorkRecordDetailVO> swInfo = storeWorkRecordService.storeWorkRecordDetailList(enterpriseId, request, user);
                if (Objects.isNull(swInfo.getList()) || CollectionUtils.isEmpty(swInfo.getList())) {
                    break;
                }
                List<SwStoreWorkRecordDetailVO> result = swInfo.getList();
                List<Map<String, Object>> resultMap = new ArrayList<>();
                for (SwStoreWorkRecordDetailVO vo : result) {
                    //对应字段直接添加
                    Map<String, Object> map = new HashMap<>();
                    //数据表
                    map.put("storeName", vo.getStoreName());
                    map.put("storeNum", vo.getStoreNum());
                    map.put("score", vo.getScore());
                    map.put("storeWorkDate", vo.getStoreWorkDate());

                    for (SwStoreWorkTableDTO swStoreWorkTableDTO : vo.getSwStoreWorkTableDTOS()) {
                        map.put("totalScore_" + swStoreWorkTableDTO.getTableMappingId(), swStoreWorkTableDTO.getTotalScore());
                        swStoreWorkTableDTO.getSwStoreWorkColumnResultDTOS().forEach(swStoreWorkColumnResultDTO -> {
                            map.put("columnName_" + swStoreWorkTableDTO.getTableMappingId() + Constants.UNDERLINE + swStoreWorkColumnResultDTO.getTbMetaColumnId(),
                                    swStoreWorkColumnResultDTO.getHandleStatus());
                            map.put("score_" + swStoreWorkTableDTO.getTableMappingId() + Constants.UNDERLINE + swStoreWorkColumnResultDTO.getTbMetaColumnId(), swStoreWorkColumnResultDTO.getScore());
                        });
                    }
                    //10级区域
                    List<String> regionNameList = vo.getRegionNameList();
                    if (CollectionUtils.isNotEmpty(regionNameList)) {
                        int i = 0;
                        for (String regionName : regionNameList) {
                            map.put(Constants.EXPORT_REGION_CODE + i, regionName);
                            i++;
                        }
                    }

                    resultMap.add(map);
                }
                workbook = ExcelExportUtil.exportBigExcel(params, beanList, resultMap);
                result.clear();
                resultMap.clear();
            }
            if (workbook == null) {
                log.error("店务任务数据明细没有数据");
                importTaskDO.setStatus(ImportTaskStatusEnum.ERROR.getCode());
                importTaskDO.setRemark("店务任务数据明细没有数据");
            } else {
                try {
                    ExcelExportUtil.closeExportBigExcel();
                } catch (Exception ex) {
                    log.error("店务任务数据明细EXCEL文件导出对象关闭异常", ex);
                }
                exportAsyncService.asyncDynamicExportListFile(enterpriseId, importTaskDO, workbook, dbName);
            }
        } catch (Throwable e) {
            log.error("店务任务数据明细获取数据失败", e);
            importTaskDO.setStatus(ImportTaskStatusEnum.ERROR.getCode());
            importTaskDO.setRemark("店务任务数据明细导出数据异常");
        } finally {
            try {
                ExcelExportUtil.closeExportBigExcel();
            } catch (Exception ex) {
                log.error("店务任务数据明细EXCEL文件导出对象关闭异常", ex);
            }
            importTaskService.updateImportTask(enterpriseId, importTaskDO);
        }

    }

    private List<ExcelExportEntity> getPotralRecordExportEntityList(PatrolStoreStatisticsDataTableQuery query) {
        List<ExcelExportEntity> beanList = new ArrayList<>();
        //数据表
        if (query.getType() == null || query.getType() == 1) {
            beanList.add(new ExcelExportEntity("巡店记录id", "id"));
        }
        beanList.add(new ExcelExportEntity("类型", "patrolType"));
        beanList.add(new ExcelExportEntity("所属区域", "fullRegionName"));
        beanList.add(new ExcelExportEntity("门店名称", "storeName"));
        beanList.add(new ExcelExportEntity("门店编号", "storeNum"));
        beanList.add(new ExcelExportEntity("任务名称", "taskName"));
        beanList.add(new ExcelExportEntity("任务说明", "taskDesc"));
        beanList.add(new ExcelExportEntity("任务有效期", "validTime"));
        beanList.add(new ExcelExportEntity("任务状态", "statusStr"));
        beanList.add(new ExcelExportEntity("任务内容", "metaTableName"));
        ExcelExportEntity totalColumnCount = new ExcelExportEntity("总检查项数", "totalColumnCount");
        totalColumnCount.setType(BaseEntityTypeConstants.DOUBLE_TYPE);
        //是否为自定义表

        beanList.add(totalColumnCount);
        ExcelExportEntity passColumnCount = new ExcelExportEntity("合格项数", "passColumnCount");
        passColumnCount.setType(BaseEntityTypeConstants.DOUBLE_TYPE);
        beanList.add(passColumnCount);
        ExcelExportEntity failColumnCount = new ExcelExportEntity("不合格项数", "failColumnCount");
        failColumnCount.setType(BaseEntityTypeConstants.DOUBLE_TYPE);
        beanList.add(failColumnCount);
        ExcelExportEntity inapplicableColumnCount = new ExcelExportEntity("不适用项数", "inapplicableColumnCount");
        inapplicableColumnCount.setType(BaseEntityTypeConstants.DOUBLE_TYPE);
        beanList.add(inapplicableColumnCount);
        ExcelExportEntity totalScore = new ExcelExportEntity("总分（值）", "totalScore");
        totalScore.setType(BaseEntityTypeConstants.DOUBLE_TYPE);
        beanList.add(totalScore);
        ExcelExportEntity score = new ExcelExportEntity("得分（值）", "score");
        score.setType(BaseEntityTypeConstants.DOUBLE_TYPE);
        beanList.add(score);
        ExcelExportEntity percentEntity = new ExcelExportEntity("得分率", "percent");
        percentEntity.setType(BaseEntityTypeConstants.STRING_TYPE);
        percentEntity.setNumFormat("0.00");
        percentEntity.setSuffix("%");
        beanList.add(percentEntity);

        ExcelExportEntity allColumnCheckScore = new ExcelExportEntity("除红线否决外得分（值）", "allColumnCheckScore");
        allColumnCheckScore.setType(BaseEntityTypeConstants.DOUBLE_TYPE);
        beanList.add(allColumnCheckScore);

        ExcelExportEntity allColumnCheckScorePercent = new ExcelExportEntity("除红线否决外得分率", "allColumnCheckScorePercent");
        allColumnCheckScorePercent.setType(BaseEntityTypeConstants.STRING_TYPE);
        allColumnCheckScorePercent.setNumFormat("0.00");
        allColumnCheckScorePercent.setSuffix("%");
        beanList.add(allColumnCheckScorePercent);

        beanList.add(new ExcelExportEntity("巡店结果", "checkResult"));
        ExcelExportEntity rewardPenaltMoney = new ExcelExportEntity("奖罚金额", "rewardPenaltMoney");
        rewardPenaltMoney.setType(BaseEntityTypeConstants.DOUBLE_TYPE);
        beanList.add(rewardPenaltMoney);


        beanList.add(new ExcelExportEntity("是否逾期完成", "overdue"));
        beanList.add(new ExcelExportEntity("巡店人", "supervisorName"));
        //数据表
        if (query.getType() == null || query.getType() == 1) {
            beanList.add(new ExcelExportEntity("巡店人职位", "supervisorPositionName"));
        }

        ExcelExportEntity tourTime = new ExcelExportEntity("巡店时长（规则）", "tourTimeStr");
        beanList.add(tourTime);
        ExcelExportEntity actualPatrolStoreDuration = new ExcelExportEntity("巡店时长（实际）", "actualPatrolStoreDuration");
        beanList.add(actualPatrolStoreDuration);

        ExcelExportEntity tourTimeStrBySeconds = new ExcelExportEntity("巡店时长（秒）（规则）", "tourTimeStrBySeconds");
        beanList.add(tourTimeStrBySeconds);
        ExcelExportEntity actualPatrolStoreDurationBySeconds = new ExcelExportEntity("巡店时长（秒）（实际）", "actualPatrolStoreDurationBySeconds");
        beanList.add(actualPatrolStoreDurationBySeconds);

        ExcelExportEntity signInDate = new ExcelExportEntity("签到日期", "signInDate");
        signInDate.setFormat(DateUtils.DATE_FORMAT_SEC_6);
        beanList.add(signInDate);

        ExcelExportEntity signStartTime = new ExcelExportEntity("签到时间", "signStartTime");
        signStartTime.setFormat(DateUtils.DATE_FORMAT_SEC_5);
        beanList.add(signStartTime);

        ExcelExportEntity signEndTime = new ExcelExportEntity("签退时间", "signEndTime");
        signEndTime.setFormat(DateUtils.DATE_FORMAT_SEC_5);
        beanList.add(signEndTime);
        beanList.add(new ExcelExportEntity("签到/签退方式", "signWay"));
        beanList.add(new ExcelExportEntity("门店地址", "storeAddress"));
        beanList.add(new ExcelExportEntity("签到地址", "signStartAddress"));
        beanList.add(new ExcelExportEntity("签到是否异常", "signInRemark"));

        beanList.add(new ExcelExportEntity("签退地址", "signEndAddress"));
        beanList.add(new ExcelExportEntity("签退是否异常", "signOutRemark"));

        if (query.getType() != null && query.getType() == 1) {
            beanList.add(new ExcelExportEntity("创建人", "createrUserName"));
            ExcelExportEntity createTime = new ExcelExportEntity("创建时间", "createTime");
            createTime.setFormat(DateUtils.DATE_FORMAT_SEC_5);
            beanList.add(createTime);
        }

        beanList.add(new ExcelExportEntity("巡店总结", "summary"));
        beanList.add(new ExcelExportEntity("巡店总结-图片/视频", "summaryPicture"));
        beanList.add(new ExcelExportEntity("巡店签名", "supervisorSignature"));
        beanList.add(new ExcelExportEntity("审批人", "auditUserName"));
        beanList.add(new ExcelExportEntity("审批意见", "auditOpinionStr"));
        beanList.add(new ExcelExportEntity("审批-备注", "auditRemark"));
        beanList.add(new ExcelExportEntity("审批-图片", "auditPicture"));
        ExcelExportEntity aduitTime = new ExcelExportEntity("审批时间", "auditTime");
        aduitTime.setFormat(DateUtils.DATE_FORMAT_SEC_5);
        beanList.add(aduitTime);
        beanList.add(new ExcelExportEntity("巡店记录URL链接", "recordInfoShareUrl"));
        return beanList;
    }

    /**
     * 获取集合报告导出表头
     * @param query
     * @return
     */
    private List<ExcelExportEntity> getSafetyCheckExportEntityList(PatrolStoreStatisticsDataTableQuery query) {
        List<ExcelExportEntity> beanList = new ArrayList<>();
        //数据表
        if (query.getType() == null || query.getType() == 1) {
            beanList.add(new ExcelExportEntity("巡店记录id", "id"));
        }
        beanList.add(new ExcelExportEntity("门店名称", "storeName"));
        beanList.add(new ExcelExportEntity("任务内容（检查表）", "metaTableName"));
        ExcelExportEntity totalColumnCount = new ExcelExportEntity("总检查项数", "totalColumnCount");
        totalColumnCount.setType(BaseEntityTypeConstants.DOUBLE_TYPE);
        //是否为自定义表

        beanList.add(totalColumnCount);
        ExcelExportEntity passColumnCount = new ExcelExportEntity("合格项数", "passColumnCount");
        passColumnCount.setType(BaseEntityTypeConstants.DOUBLE_TYPE);
        beanList.add(passColumnCount);
        ExcelExportEntity failColumnCount = new ExcelExportEntity("不合格项数", "failColumnCount");
        failColumnCount.setType(BaseEntityTypeConstants.DOUBLE_TYPE);
        beanList.add(failColumnCount);
        ExcelExportEntity inapplicableColumnCount = new ExcelExportEntity("不适用项数", "inapplicableColumnCount");
        inapplicableColumnCount.setType(BaseEntityTypeConstants.DOUBLE_TYPE);
        beanList.add(inapplicableColumnCount);
        ExcelExportEntity totalScore = new ExcelExportEntity("总分", "totalScore");
        totalScore.setType(BaseEntityTypeConstants.DOUBLE_TYPE);
        beanList.add(totalScore);
        ExcelExportEntity score = new ExcelExportEntity("得分", "score");
        score.setType(BaseEntityTypeConstants.DOUBLE_TYPE);
        beanList.add(score);

        ExcelExportEntity allColumnCheckScore = new ExcelExportEntity("各项得分之和", "allColumnCheckScore");
        allColumnCheckScore.setType(BaseEntityTypeConstants.DOUBLE_TYPE);
        beanList.add(allColumnCheckScore);

        ExcelExportEntity allColumnCheckScorePercent = new ExcelExportEntity("得分率", "allColumnCheckScorePercent");
        allColumnCheckScorePercent.setType(BaseEntityTypeConstants.STRING_TYPE);
        allColumnCheckScorePercent.setNumFormat("0.00");
        allColumnCheckScorePercent.setSuffix("%");
        beanList.add(allColumnCheckScorePercent);

        ExcelExportEntity rewardPenaltMoney = new ExcelExportEntity("奖罚金额", "rewardPenaltMoney");
        rewardPenaltMoney.setType(BaseEntityTypeConstants.DOUBLE_TYPE);
        beanList.add(rewardPenaltMoney);

        beanList.add(new ExcelExportEntity("巡店人", "supervisorName"));

        ExcelExportEntity signStartTime = new ExcelExportEntity("签到时间", "signStartTime");
        signStartTime.setFormat(DateUtils.DATE_FORMAT_SEC_5);
        beanList.add(signStartTime);

        ExcelExportEntity signEndTime = new ExcelExportEntity("签退时间", "signEndTime");
        signEndTime.setFormat(DateUtils.DATE_FORMAT_SEC_5);
        beanList.add(signEndTime);
        return beanList;
    }

    @Override
    public void exportPatrolStoreCheckList(PatrolStoreCheckQuery query) {

        DataSourceHelper.changeToSpecificDataSource(query.getDbName());
        ImportTaskDO task = query.getImportTaskDO();
        Long totalNum = query.getTotalNum();
        int pageSize = Constants.PAGE_SIZE;
        long pages = (totalNum + pageSize - 1) / pageSize;
        try {
            // 设置excel的基本参数
            ExportParams params = new ExportParams(null, null, ExcelType.XSSF);

            Workbook workbook = null;
            for (int pageNum = 1; pageNum <= pages; pageNum++) {
                query.setPageNum(pageNum);
                query.setPageSize(pageSize);

                PageInfo<ExportPatrolStoreCheckVO> pageInfo = patrolStoreRecordsService.exportPatrolStoreCheckList(query);
                if(query.getType()!=null && query.getType()==0){
                    List<ExportPatrolStoreCheckVO> list = pageInfo.getList();
                    if (CollectionUtils.isEmpty(list)) {
                        break;
                    }
                    log.info("稽核概览列表 list：{}", list);
                    workbook = ExcelExportUtil.exportBigExcel(params, ExportPatrolStoreCheckVO.class, list);

                    log.info("稽核概览列表 workbook：{}", workbook);

                }else if(query.getType()==null && query.getCheckType()==1 ) {
                    if (query.getBigRegionCheckStatus() != null) {
                        if (query.getBigRegionCheckStatus() == 0) {
                            //大区未稽核
                            List<ExportPatrolStoreCheckVO> list = pageInfo.getList();
                            List<ExportPatrolStoreNotCheckVO> voList = BeanUtil.copyToList(list, ExportPatrolStoreNotCheckVO.class);
                            if (CollectionUtils.isEmpty(voList)) {
                                break;
                            }
                            log.info("大区未稽核列表 list：{}", voList);
                            workbook = ExcelExportUtil.exportBigExcel(params, ExportPatrolStoreNotCheckVO.class, voList);

                            log.info("大区未稽核列表 workbook：{}", workbook);

                        } else {
                            //大区已稽核
                            List<ExportPatrolStoreCheckVO> list = pageInfo.getList();
                            List<ExportPatrolStorePassCheckVO> voList = BeanUtil.copyToList(list, ExportPatrolStorePassCheckVO.class);
                            if (CollectionUtils.isEmpty(voList)) {
                                break;
                            }
                            log.info("大区已稽核列表 list：{}", voList);
                            workbook = ExcelExportUtil.exportBigExcel(params, ExportPatrolStorePassCheckVO.class, voList);
                            log.info("大区已稽核列表 workbook：{}", workbook);
                        }
                    }
                }else if(query.getType()==null && query.getCheckType()==2){
                    if(query.getWarZoneCheckStatus() != null) {
                        if( query.getWarZoneCheckStatus() == 0){
                            //战区未稽核
                            List<ExportPatrolStoreCheckVO> list = pageInfo.getList();
                            List<ExportPatrolStoreNotCheckVO> voList = BeanUtil.copyToList(list, ExportPatrolStoreNotCheckVO.class);
                            if (CollectionUtils.isEmpty(voList)) {
                                break;
                            }
                            log.info("战区未稽核 list：{}", voList);
                            workbook = ExcelExportUtil.exportBigExcel(params, ExportPatrolStoreNotCheckVO.class, voList);
                            log.info("战区未稽核 workbook：{}", workbook);
                        }else{
                            //战区已稽核
                            List<ExportPatrolStoreCheckVO> list = pageInfo.getList();
                            List<ExportPatrolStorePassCheckVO> voList = BeanUtil.copyToList(list, ExportPatrolStorePassCheckVO.class);
                            if (CollectionUtils.isEmpty(voList)) {
                                break;
                            }
                            log.info("战区已稽核 list：{}", voList);
                            workbook = ExcelExportUtil.exportBigExcel(params, ExportPatrolStorePassCheckVO.class, voList);
                            log.info("战区已稽核 workbook：{}", workbook);
                        }
                    }

                }

            }
            log.info("打印workbook:{}", workbook);
            if (workbook != null) {
                try {
                    ExcelExportUtil.closeExportBigExcel();
                } catch (Exception ex) {
                    log.error("EXCEL文件导出对象关闭异常", ex);
                }
                exportAsyncService.asyncDynamicExportListFile(query.getEnterpriseId(),query.getImportTaskDO(), workbook, query.getDbName());
            } else {
                log.error("查询不到数据");
                task.setStatus(ImportTaskStatusEnum.ERROR.getCode());
                task.setRemark("查询不到数据");
            }
        } catch (com.cool.store.exception.ServiceException e) {
            log.error("获取数据失败", e);
            task.setStatus(ImportTaskStatusEnum.ERROR.getCode());
            task.setRemark(e.getErrorMessage());
        } catch (Exception e) {
            log.error("获取数据失败", e);
            task.setStatus(ImportTaskStatusEnum.ERROR.getCode());
            task.setRemark("部分数据获取异常");
        } finally {
            try {
                ExcelExportUtil.closeExportBigExcel();
            } catch (Exception ex) {
                log.error("EXCEL文件导出对象关闭异常", ex);
            }
            DataSourceHelper.changeToSpecificDataSource(query.getDbName());
            importTaskService.updateImportTask(query.getEnterpriseId(),task);
        }
    }

    @Override
    public void exportCheckDetailList(PatrolStoreCheckQuery query) {
        DataSourceHelper.changeToSpecificDataSource(query.getDbName());
        ImportTaskDO task = query.getImportTaskDO();
        Long totalNum = query.getTotalNum();
        int pageSize = Constants.PAGE_SIZE;
        long pages = (totalNum + pageSize - 1) / pageSize;
        try {
            // 设置excel的基本参数
            ExportParams params = new ExportParams(null, null, ExcelType.XSSF);

            Workbook workbook = null;
            for (int pageNum = 1; pageNum <= pages; pageNum++) {
                query.setPageNum(pageNum);
                query.setPageSize(pageSize);
                PageInfo<DataStaTableColumnVO> pageInfo = patrolStoreRecordsService.getCheckDetailList(query.getEnterpriseId(), query);
                List<DataStaTableColumnVO> list = pageInfo.getList();
                if (CollectionUtils.isEmpty(list)) {
                    break;
                }
                workbook = ExcelExportUtil.exportBigExcel(params, DataStaTableColumnVO.class, list);
            }
            if (workbook != null) {
                try {
                    ExcelExportUtil.closeExportBigExcel();
                } catch (Exception ex) {
                    log.error("EXCEL文件导出对象关闭异常", ex);
                }
                exportAsyncService.asyncDynamicExportListFile(query.getEnterpriseId(),query.getImportTaskDO(), workbook, query.getDbName());
            } else {
                log.error("查询不到数据");
                task.setStatus(ImportTaskStatusEnum.ERROR.getCode());
                task.setRemark("查询不到数据");
            }
        } catch (com.cool.store.exception.ServiceException e) {
            log.error("获取数据失败", e);
            task.setStatus(ImportTaskStatusEnum.ERROR.getCode());
            task.setRemark(e.getErrorMessage());
        } catch (Exception e) {
            log.error("获取数据失败", e);
            task.setStatus(ImportTaskStatusEnum.ERROR.getCode());
            task.setRemark("部分数据获取异常");
        } finally {
            try {
                ExcelExportUtil.closeExportBigExcel();
            } catch (Exception ex) {
                log.error("EXCEL文件导出对象关闭异常", ex);
            }
            DataSourceHelper.changeToSpecificDataSource(query.getDbName());
            importTaskService.updateImportTask(query.getEnterpriseId(),task);
        }
    }

    @Override
    public void exportCheckAnalyzeList(PatrolStoreCheckQuery query) {
        DataSourceHelper.changeToSpecificDataSource(query.getDbName());
        ImportTaskDO task = query.getImportTaskDO();
        Long totalNum = query.getTotalNum();
        int pageSize = Constants.PAGE_SIZE;
        long pages = (totalNum + pageSize - 1) / pageSize;
        try {
            // 设置excel的基本参数
            ExportParams params = new ExportParams(null, null, ExcelType.XSSF);

            Workbook workbook = null;
            for (int pageNum = 1; pageNum <= pages; pageNum++) {
                query.setPageNum(pageNum);
                query.setPageSize(pageSize);
                PageInfo<CheckAnalyzeVO> pageInfo = patrolStoreRecordsService.getCheckAnalyzeList(query.getEnterpriseId(), query);
                List<CheckAnalyzeVO> list = pageInfo.getList();
                if (CollectionUtils.isEmpty(list)) {
                    break;
                }
                workbook = ExcelExportUtil.exportBigExcel(params, CheckAnalyzeVO.class, list);
            }
            if (workbook != null) {
                try {
                    ExcelExportUtil.closeExportBigExcel();
                } catch (Exception ex) {
                    log.error("EXCEL文件导出对象关闭异常", ex);
                }
                exportAsyncService.asyncDynamicExportListFile(query.getEnterpriseId(),query.getImportTaskDO(), workbook, query.getDbName());
            } else {
                log.error("查询不到数据");
                task.setStatus(ImportTaskStatusEnum.ERROR.getCode());
                task.setRemark("查询不到数据");
            }
        } catch (com.cool.store.exception.ServiceException e) {
            log.error("获取数据失败", e);
            task.setStatus(ImportTaskStatusEnum.ERROR.getCode());
            task.setRemark(e.getErrorMessage());
        } catch (Exception e) {
            log.error("获取数据失败", e);
            task.setStatus(ImportTaskStatusEnum.ERROR.getCode());
            task.setRemark("部分数据获取异常");
        } finally {
            try {
                ExcelExportUtil.closeExportBigExcel();
            } catch (Exception ex) {
                log.error("EXCEL文件导出对象关闭异常", ex);
            }
            DataSourceHelper.changeToSpecificDataSource(query.getDbName());
            importTaskService.updateImportTask(query.getEnterpriseId(),task);
        }
    }
}
