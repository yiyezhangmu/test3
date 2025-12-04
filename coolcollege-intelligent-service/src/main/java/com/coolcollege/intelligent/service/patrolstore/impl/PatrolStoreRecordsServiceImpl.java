package com.coolcollege.intelligent.service.patrolstore.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.annotation.AsyncDynamicExport;
import com.coolcollege.intelligent.common.annotation.AsyncExport;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.constant.RedisConstant;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.enums.MsgUniteDataTypeEnum;
import com.coolcollege.intelligent.common.enums.UserRangeTypeEnum;
import com.coolcollege.intelligent.common.enums.importexcel.ExportTemplateEnum;
import com.coolcollege.intelligent.common.enums.importexcel.ImportTaskConstant;
import com.coolcollege.intelligent.common.enums.meta.MetaColumnTypeEnum;
import com.coolcollege.intelligent.common.enums.meta.MetaTablePropertyEnum;
import com.coolcollege.intelligent.common.enums.patrol.CheckResultEnum;
import com.coolcollege.intelligent.common.enums.video.ResourceStatusEnum;
import com.coolcollege.intelligent.common.enums.video.UploadTypeEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.common.util.DateUtil;
import com.coolcollege.intelligent.common.util.DateUtils;
import com.coolcollege.intelligent.common.util.MD5Util;
import com.coolcollege.intelligent.common.util.UUIDUtils;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseConfigMapper;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseSettingMapper;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseStoreCheckSettingMapper;
import com.coolcollege.intelligent.dao.enterprise.dao.EnterpriseUserDao;
import com.coolcollege.intelligent.dao.metatable.TbMetaColumnResultMapper;
import com.coolcollege.intelligent.dao.metatable.TbMetaDefTableColumnMapper;
import com.coolcollege.intelligent.dao.metatable.TbMetaStaTableColumnMapper;
import com.coolcollege.intelligent.dao.metatable.TbMetaTableMapper;
import com.coolcollege.intelligent.dao.metatable.dao.TbMetaColumnReasonDao;
import com.coolcollege.intelligent.dao.patrolstore.*;
import com.coolcollege.intelligent.dao.region.RegionMapper;
import com.coolcollege.intelligent.dao.safetycheck.dao.TbMetaColumnAppealDao;
import com.coolcollege.intelligent.dao.sop.TaskSopMapper;
import com.coolcollege.intelligent.dao.store.StoreMapper;
import com.coolcollege.intelligent.dao.system.SysRoleMapper;
import com.coolcollege.intelligent.dao.unifytask.TaskParentMapper;
import com.coolcollege.intelligent.dao.unifytask.TaskSubMapper;
import com.coolcollege.intelligent.mapper.metatable.TbMetaTableUserAuthDAO;
import com.coolcollege.intelligent.model.coolrelation.vo.CoolCourseVO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseSettingDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseStoreCheckSettingDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseUserDO;
import com.coolcollege.intelligent.model.enums.BusinessCheckType;
import com.coolcollege.intelligent.model.enums.TaskTypeEnum;
import com.coolcollege.intelligent.model.enums.UnifyNodeEnum;
import com.coolcollege.intelligent.model.impoetexcel.ImportTaskDO;
import com.coolcollege.intelligent.model.metatable.*;
import com.coolcollege.intelligent.model.metatable.dto.TbMetaColumnReasonDTO;
import com.coolcollege.intelligent.model.metatable.dto.TbMetaColumnResultDTO;
import com.coolcollege.intelligent.model.metatable.vo.CategoryStatisticsVO;
import com.coolcollege.intelligent.model.metatable.vo.MetaStaColumnVO;
import com.coolcollege.intelligent.model.metatable.vo.TbMetaTableInfoVO;
import com.coolcollege.intelligent.model.msg.MsgUniteData;
import com.coolcollege.intelligent.model.patrolstore.*;
import com.coolcollege.intelligent.model.patrolstore.dto.ColumnNameDTO;
import com.coolcollege.intelligent.model.patrolstore.dto.ColumnValueDTO;
import com.coolcollege.intelligent.model.patrolstore.query.GetCheckUserVO;
import com.coolcollege.intelligent.model.patrolstore.query.PatrolStoreCheckQuery;
import com.coolcollege.intelligent.model.patrolstore.query.PatrolStoreStatisticsDataTableQuery;
import com.coolcollege.intelligent.model.patrolstore.query.SetCheckUserQuery;
import com.coolcollege.intelligent.model.patrolstore.records.PatrolStoreRecordsTableAndPicDTO;
import com.coolcollege.intelligent.model.patrolstore.records.PatrolStoreRecordsTableDTO;
import com.coolcollege.intelligent.model.patrolstore.records.SingleTableColumnsRecordsDTO;
import com.coolcollege.intelligent.model.patrolstore.request.*;
import com.coolcollege.intelligent.model.patrolstore.statistics.*;
import com.coolcollege.intelligent.model.patrolstore.vo.*;
import com.coolcollege.intelligent.model.region.RegionDO;
import com.coolcollege.intelligent.model.region.dto.RegionPathDTO;
import com.coolcollege.intelligent.model.safetycheck.ScSafetyCheckFlowDO;
import com.coolcollege.intelligent.model.safetycheck.dto.TbMetaColumnAppealDTO;
import com.coolcollege.intelligent.model.safetycheck.vo.DataColumnHasHistoryVO;
import com.coolcollege.intelligent.model.safetycheck.vo.TbDataColumnCommentAppealVO;
import com.coolcollege.intelligent.model.selectcomponent.SelectComponentRegionVO;
import com.coolcollege.intelligent.model.selectcomponent.SelectComptRegionStoreVO;
import com.coolcollege.intelligent.model.sop.vo.TaskSopVO;
import com.coolcollege.intelligent.model.store.StoreDO;
import com.coolcollege.intelligent.model.store.dto.StoreAreaDTO;
import com.coolcollege.intelligent.model.store.dto.StoreDTO;
import com.coolcollege.intelligent.model.store.dto.StorePathDTO;
import com.coolcollege.intelligent.model.storework.vo.HandlerUserVO;
import com.coolcollege.intelligent.model.system.SysRoleDO;
import com.coolcollege.intelligent.model.unifytask.TaskParentDO;
import com.coolcollege.intelligent.model.unifytask.TaskSubDO;
import com.coolcollege.intelligent.model.unifytask.dto.TaskMessageDTO;
import com.coolcollege.intelligent.model.unifytask.dto.UnifyPersonDTO;
import com.coolcollege.intelligent.model.unifytask.vo.TaskProcessVO;
import com.coolcollege.intelligent.model.userholder.UserHolder;
import com.coolcollege.intelligent.model.video.SmallVideoDTO;
import com.coolcollege.intelligent.model.video.SmallVideoInfoDTO;
import com.coolcollege.intelligent.model.video.param.SmallVideoParam;
import com.coolcollege.intelligent.producer.SimpleMessageService;
import com.coolcollege.intelligent.service.enterprise.impl.EnterpriseUserServiceImpl;
import com.coolcollege.intelligent.service.importexcel.ImportTaskService;
import com.coolcollege.intelligent.service.metatable.TbMetaTableService;
import com.coolcollege.intelligent.service.patrolstore.PatrolStoreRecordsService;
import com.coolcollege.intelligent.service.patrolstore.PatrolStoreService;
import com.coolcollege.intelligent.service.region.RegionService;
import com.coolcollege.intelligent.service.safetycheck.ScSafetyCheckFlowService;
import com.coolcollege.intelligent.service.selectcomponent.SelectionComponentService;
import com.coolcollege.intelligent.service.system.SysRoleService;
import com.coolcollege.intelligent.service.unifytask.UnifyTaskService;
import com.coolcollege.intelligent.service.unifytask.UnifyTaskStoreService;
import com.coolcollege.intelligent.util.DateFormatUtil;
import com.coolcollege.intelligent.util.DynamicExcelUtil;
import com.coolcollege.intelligent.util.RedisConstantUtil;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.coolcollege.intelligent.util.patrolStore.TableTypeUtil;
import com.coolstore.base.enums.RocketMqTagEnum;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.coolcollege.intelligent.common.util.DateUtils.DATE_FORMAT_SEC_5;
import static com.coolcollege.intelligent.model.metatable.MetaTableConstant.BusinessTypeConstant.PATROL_STORE;
import static com.coolcollege.intelligent.model.metatable.MetaTableConstant.CheckResultConstant.FAIL;
import static com.coolcollege.intelligent.model.metatable.MetaTableConstant.CheckResultConstant.PASS;

/**
 * @author shucahng.wei
 * @date 2020/12/16
 */
@Service
@Slf4j
public class PatrolStoreRecordsServiceImpl implements PatrolStoreRecordsService {
    @Resource
    private StoreMapper storeMapper;
    @Resource
    private TbPatrolStoreRecordMapper tbPatrolStoreRecordMapper;
    @Resource
    private TbDataStaTableColumnMapper tbDataStaTableColumnMapper;
    @Resource
    private TbDataDefTableColumnMapper tbDataDefTableColumnMapper;
    @Resource
    private TaskParentMapper taskParentMapper;
    @Resource
    private TaskSubMapper taskSubMapper;
    @Resource
    private TbDataTableMapper tbDataTableMapper;
    @Resource
    private TbMetaTableMapper tbMetaTableMapper;
    @Resource
    private TbMetaStaTableColumnMapper tbMetaStaTableColumnMapper;
    @Resource
    private TbMetaDefTableColumnMapper tbMetaDefTableColumnMapper;
    @Resource
    private RegionMapper regionMapper;
    @Resource
    private RegionService regionService;

    @Resource
    private ImportTaskService importTaskService;

    @Lazy
    @Resource
    private PatrolStoreService patrolStoreService;
    @Lazy
    @Autowired
    private UnifyTaskService unifyTaskService;
    @Resource
    private EnterpriseStoreCheckSettingMapper storeCheckSettingMapper;
    @Resource
    private TaskSopMapper taskSopMapper;
    @Resource
    private EnterpriseConfigMapper enterpriseConfigMapper;
    @Resource
    private TbMetaColumnResultMapper tbMetaColumnResultMapper;
    @Resource
    private EnterpriseSettingMapper enterpriseSettingMapper;

    @Autowired
    private RedisUtilPool redisUtil;
    @Resource
    private SimpleMessageService simpleMessageService;

    @Resource
    private SelectionComponentService selectionComponentService;
    @Resource
    private TbPatrolStoreCheckMapper tbPatrolStoreCheckMapper;
    @Resource
    private TbPatrolCheckDataTableMapper tbPatrolCheckDataTableMapper;
    @Resource
    private TbCheckDataStaColumnMapper tbCheckDataStaColumnMapper;
    @Resource
    private EnterpriseStoreCheckSettingMapper enterpriseStoreCheckSettingMapper;
    @Resource
    private TbPatrolStoreRecordInfoMapper tbPatrolStoreRecordInfoMapper;
    @Resource
    private UnifyTaskStoreService unifyTaskStoreService;
    @Resource
    private SysRoleMapper sysRoleMapper;
    @Resource
    private SysRoleService sysRoleService;
    @Resource
    private ScSafetyCheckFlowService scSafetyCheckFlowService;
    @Autowired
    private RedisConstantUtil redisConstantUtil;
    @Autowired
    private RedisUtilPool redisUtilPool;
    @Resource
    private TbMetaTableService tbMetaTableService;
    @Resource
    private TbMetaTableService metaTableService;
    @Resource
    private TbMetaColumnReasonDao metaColumnReasonDao;
    @Resource
    private TbMetaColumnAppealDao metaColumnAppealDao;

    @Resource
    private TbPatrolStoreHistoryMapper tbPatrolStoreHistoryMapper;
    @Autowired
    private EnterpriseUserServiceImpl enterpriseUserService;
    @Resource
    private TbMetaTableUserAuthDAO tbMetaTableUserAuthDAO;
    @Resource
    private EnterpriseUserDao enterpriseUserDao;


    @Override
    public PageInfo tableRecords(String enterpriseId, TableRecordsRequest tableRecordsRequest) {
        List<PatrolStoreRecordsTableDTO> result =new ArrayList<>();
        Date endTime = new Date(System.currentTimeMillis());
        Calendar now = Calendar.getInstance();
        now.setTime(endTime);
        now.set(Calendar.DATE, now.get(Calendar.DATE) - 30);
        Date beginTime = now.getTime();
        beginTime = tableRecordsRequest.getBeginDate() == null ? beginTime : tableRecordsRequest.getBeginDate();
        endTime = tableRecordsRequest.getEndDate() == null ? endTime : tableRecordsRequest.getEndDate();

        String regionId = tableRecordsRequest.getRegionId();
        String regionPath = regionService.getRegionPath(enterpriseId, String.valueOf(regionId)).replaceAll("]","");

        if(tableRecordsRequest.getPageSize()!=null && tableRecordsRequest.getPageNum()!=null){
            PageHelper.startPage(tableRecordsRequest.getPageNum(),tableRecordsRequest.getPageSize());
        }
        List<TbPatrolStoreRecordDO> recordList = tbPatrolStoreRecordMapper.tableRecords(enterpriseId,regionPath,beginTime,endTime,tableRecordsRequest.getIsComplete()
        ,tableRecordsRequest.getMetaTableId(), tableRecordsRequest.getSupervisorId(), tableRecordsRequest.getStatus());
        PageInfo pageInfo = new PageInfo(recordList);
        if(CollectionUtils.isEmpty(recordList)){
            return pageInfo;
        }

        List<String> userIdList = recordList.stream().map(TbPatrolStoreRecordDO::getCreateUserId).collect(Collectors.toList());
        Map<String,EnterpriseUserDO> userMap = enterpriseUserDao.getUserMap(enterpriseId,userIdList);

        List<String> regionIdList = new ArrayList<>();
        List<Long> unifyTaskIds =  new ArrayList<>();
        List<Long> recordIdList =  new ArrayList<>();
        List<Long> subTaskList =  new ArrayList<>();
        List<String> storeIdList = new ArrayList<>();
        recordList.stream().forEach(data -> {
            unifyTaskIds.add(data.getTaskId());
            recordIdList.add(data.getId());
            subTaskList.add(data.getSubTaskId());
            storeIdList.add(data.getStoreId());
            regionIdList.add(String.valueOf(data.getRegionId()));
        });
        List<RegionDO> regionPathDOList = regionService.getRegionDOsByRegionIds(enterpriseId,regionIdList);
        Map<String,String> regionMap = regionPathDOList.stream()
                .filter(a -> a.getRegionId() != null && a.getName() != null)
                .collect(Collectors.toMap(RegionDO::getRegionId, RegionDO::getName,(a, b) -> a));

        List<StoreDTO> storeList = storeMapper.getStoreListByStoreIds(enterpriseId,storeIdList);
        Map<String,StoreDTO> storeMap = storeList.stream().collect(Collectors.toMap(StoreDTO::getStoreId, data -> data,(a, b)->a));


        List<TbDataTableDO> tbDataTableDOList = tbDataTableMapper.getListByBusinessIdList(enterpriseId,recordIdList, MetaTableConstant.BusinessTypeConstant.PATROL_STORE);
        Map<Long,List<TbDataTableDO>> tbDataTableMap = tbDataTableDOList.stream().collect(Collectors.groupingBy(tbDataTableDO -> tbDataTableDO.getBusinessId()));

        List<TbDataStaTableColumnDO> tbDataStaTableColumnDOS = tbDataStaTableColumnMapper.getListByRecordIdList(enterpriseId,recordIdList);
        List<TbDataDefTableColumnDO> tbDataDefTableColumnDOS = tbDataDefTableColumnMapper.getListByRecordIdList(enterpriseId,recordIdList, null);
        Map<Long,List<TbDataStaTableColumnDO>> recordStaColumnMap =
                tbDataStaTableColumnDOS.stream().collect(Collectors.groupingBy(tbDataStaTableColumnDO -> tbDataStaTableColumnDO.getBusinessId()));

        Map<Long,List<TbDataDefTableColumnDO>> recordDefColumnMap =
                tbDataDefTableColumnDOS.stream().collect(Collectors.groupingBy(data -> data.getBusinessId()));
        List<TaskParentDO> parentDOList = taskParentMapper.selectTaskByIds(enterpriseId, new ArrayList<>(unifyTaskIds));
        List<TaskSubDO> taskSubVOS = taskSubMapper.getDOByIdList(enterpriseId,subTaskList);
        Map<Long,TaskSubDO> subTaskMap = taskSubVOS.stream().collect(Collectors.toMap(data -> data.getId(),data -> data,(a,b)->a));
        Map<Long,TaskParentDO> parentDOMap = parentDOList.stream().collect(Collectors.toMap(taskParentDO -> taskParentDO.getId(),data -> data,(a,b)->a));

        List<PatrolStoreStatisticsDataStaTableDTO> columnCountList =
                tbDataStaTableColumnMapper.statisticsColumnCountByBusinessId(enterpriseId, new ArrayList<>(recordIdList));
        Map<Long, PatrolStoreStatisticsDataStaTableDTO> dataTableIdColumnCountMap = columnCountList.stream().collect(
                Collectors.toMap(PatrolStoreStatisticsDataStaTableDTO::getBusinessId, Function.identity(), (a, b) -> a));


        recordList.forEach(record ->{
            PatrolStoreRecordsTableDTO recordsTableDTO = new PatrolStoreRecordsTableDTO();
            recordsTableDTO.setMetaTableId(record.getMetaTableId());
            recordsTableDTO.setTableType(record.getTableType());
            recordsTableDTO.setBusinessId(record.getId());
            TaskSubDO taskSubDO = subTaskMap.get(record.getSubTaskId());
            recordsTableDTO.setIsOverdue("未过期");
            recordsTableDTO.setTaskStatus("待处理");
            if(record.getStatus() == 1){
                recordsTableDTO.setTaskStatus("已完成");
            }
            if(taskSubDO!=null){
                //处理人取子任务处理人，复审人不取
                if(userMap.get(taskSubDO.getHandleUserId()) != null){
                    recordsTableDTO.setHandler(userMap.get(taskSubDO.getHandleUserId()).getName());
                }else {
                    recordsTableDTO.setHandler(record.getSupervisorName());
                }

                recordsTableDTO.setReChecker("");
                Date taskEndTime = new Date(taskSubDO.getSubEndTime());
                if(record.getSignStartTime() != null && compareDate(taskEndTime,record.getSignStartTime())){
                    recordsTableDTO.setIsOverdue("过期");
                }
                //有效期
                recordsTableDTO.setEffectiveTime(getTime(new Date(taskSubDO.getSubEndTime())));
            }
            TaskParentDO taskParentDO = parentDOMap.get(record.getTaskId());
            if(taskParentDO != null){
                //任务名称
                recordsTableDTO.setTaskName(taskParentDO.getTaskName());
                recordsTableDTO.setNote(taskParentDO.getTaskDesc());
            }

            StoreDTO storeDO = storeMap.get(record.getStoreId());
            List<TbDataTableDO> tableList = tbDataTableMap.get(record.getId());
            if(storeDO!=null){
                recordsTableDTO.setAreaId(storeDO.getRegionId());
                recordsTableDTO.setStoreName(storeDO.getStoreName());
                recordsTableDTO.setStoreId(storeDO.getStoreId());
            }else {
                recordsTableDTO.setStoreName(record.getStoreName());
            }

            recordsTableDTO.setAreaName(regionMap.get(String.valueOf(record.getRegionId())));
            recordsTableDTO.setSupervisorName(record.getSupervisorName());
            //检查表名称
            StringBuffer tableName = new StringBuffer();
            CollectionUtils.emptyIfNull(tableList).stream().filter(table ->!tableName.toString().contains(table.getTableName()))
                    .forEach(table -> {
                        tableName.append(table.getTableName()).append(",");
                    });
            if(tableName.lastIndexOf(",") != -1){
                tableName.deleteCharAt(tableName.lastIndexOf(","));
            }
            recordsTableDTO.setTableName(tableName.toString());

            Integer allCount =0;
            recordsTableDTO.setScore(new BigDecimal(Constants.ZERO_STR));
            recordsTableDTO.setUnQualifiedCount(0);
            List<TbDataStaTableColumnDO> tbDataStaTableColumnDOS1 = recordStaColumnMap.get(record.getId());
            if(tbDataStaTableColumnDOS1 != null){
                allCount += tbDataStaTableColumnDOS1.size();
                tbDataStaTableColumnDOS1.stream().forEach(data ->{
                    if("INAPPLICABLE".equals(data.getCheckResult() )){
                        int a = recordsTableDTO.getUnQualifiedCount();
                        recordsTableDTO.setUnQualifiedCount(a+1);
                    }
                    BigDecimal b = data.getCheckScore() == null?new BigDecimal(Constants.ZERO_STR) :data.getCheckScore();
                    BigDecimal c = recordsTableDTO.getScore();
                    recordsTableDTO.setScore(b.add(c));
                });
            }
            List<TbDataDefTableColumnDO> tbDataDefTableColumnDOS1 = recordDefColumnMap.get(record.getId());
            if(tbDataDefTableColumnDOS1 != null){
                allCount += allCount+tbDataDefTableColumnDOS1.size();
            }
                //不适用项数 = 未通过的检查项数?
            recordsTableDTO.setTotalColumnCount(allCount);


            //门店评价不取
            recordsTableDTO.setStoreEvaluation("");

            recordsTableDTO.setSignInTime(getTime(record.getSignStartTime()));
            recordsTableDTO.setSignOutTime(getTime(record.getSignEndTime()));
            recordsTableDTO.setPatrolTime(String.valueOf(record.getTourTime()));
            recordsTableDTO.setSignInAddress(record.getSignStartAddress());
            recordsTableDTO.setSignEndAddress(record.getSignEndAddress());
            recordsTableDTO.setSignOutStatus("未签到");
            if(record.getSignOutStatus() == 2){
                recordsTableDTO.setSignOutStatus("异常");
            }
            if(record.getSignOutStatus() == 1){
                recordsTableDTO.setSignOutStatus("正常");
            }
            recordsTableDTO.setIsAddException("未签到");
            if(record.getSignInStatus() == 2){
                recordsTableDTO.setIsAddException("异常");
            }
            if(record.getSignInStatus() == 1){
                recordsTableDTO.setIsAddException("正常");
            }
            if("PATROL_STORE_OFFLINE".equals(record.getPatrolType())){
                recordsTableDTO.setRecordType("线下巡店");
            }
            if("PATROL_STORE_ONLINE".equals(record.getPatrolType())){
                recordsTableDTO.setRecordType("线上巡店");
            }
            if("PATROL_STORE_PICTURE_ONLINE".equals(record.getPatrolType())){
                recordsTableDTO.setRecordType("定时巡检");
            }
            PatrolStoreStatisticsDataStaTableDTO dataStaTableDTO = dataTableIdColumnCountMap.get(record.getId());
            if(dataStaTableDTO != null){
                recordsTableDTO.setPassColumnCount(dataStaTableDTO.getPassColumnCount());
                recordsTableDTO.setFailColumnCount(dataStaTableDTO.getFailColumnCount());
                recordsTableDTO.setInapplicableColumnCount(dataStaTableDTO.getInapplicableColumnCount());
                recordsTableDTO.setScore(dataStaTableDTO.getScore());
            }

            //创建人
            recordsTableDTO.setCreateUserName(record.getSupervisorName());

            //创建时间
            recordsTableDTO.setCreateTime(getTime(record.getCreateTime()));


            result.add(recordsTableDTO);
        });
        pageInfo.setList(result);
        return pageInfo;
    }

    @Override
    @AsyncExport(type = ImportTaskConstant.EXPORT_CHECK_RECORD)
    public Object  tableRecordsExport(String enterpriseId, TableRecordsRequest request) {
        List<PatrolStoreRecordsTableAndPicDTO> result =new ArrayList<>();
        Date endTime = new Date(System.currentTimeMillis());
        Calendar now = Calendar.getInstance();
        now.setTime(endTime);
        now.set(Calendar.DATE, now.get(Calendar.DATE) - 30);
        Date beginTime = now.getTime();
        beginTime = request.getBeginDate() == null ? beginTime : request.getBeginDate();
        endTime = request.getEndDate() == null ? endTime : request.getEndDate();

        String regionId = request.getRegionId();
        String regionPath = regionService.getRegionPath(enterpriseId, String.valueOf(regionId)).replaceAll("]","");
        if(request.getPageSize()!=null && request.getPageNum()!=null){
            PageHelper.startPage(request.getPageNum(),request.getPageSize());
        }
        List<TbPatrolStoreRecordDO> recordList = tbPatrolStoreRecordMapper.tableRecords(enterpriseId,regionPath,beginTime,endTime,request.getIsComplete(),
                request.getMetaTableId(), request.getSupervisorId(), request.getStatus());
        if(CollectionUtils.isEmpty(recordList)){
            return result;
        }
        // 获取检查项列表
        List<Long> businessIdList = recordList.stream().map(m -> m.getId()).collect(Collectors.toList());
        List<TbDataStaTableColumnDO> tbDataStaTableColumnList =
                tbDataStaTableColumnMapper.getListByRecordIdList(enterpriseId, businessIdList);
        Map<Long, List<String>> picMap = tbDataStaTableColumnList.stream()
                .collect(Collectors.groupingBy(TbDataStaTableColumnDO::getBusinessId,
                        Collectors.mapping(TbDataStaTableColumnDO::getCheckPics, Collectors.toList())));
        List<String> userIdList = recordList.stream().map(TbPatrolStoreRecordDO::getCreateUserId).collect(Collectors.toList());
        Map<String,EnterpriseUserDO> userMap = enterpriseUserDao.getUserMap(enterpriseId,userIdList);

        List<String> regionIdList = new ArrayList<>();
        List<Long> unifyTaskIds =  new ArrayList<>();
        List<Long> recordIdList =  new ArrayList<>();
        List<Long> subTaskList =  new ArrayList<>();
        List<String> storeIdList = new ArrayList<>();
        recordList.stream().forEach(data -> {
            unifyTaskIds.add(data.getTaskId());
            recordIdList.add(data.getId());
            subTaskList.add(data.getSubTaskId());
            storeIdList.add(data.getStoreId());
            regionIdList.add(String.valueOf(data.getRegionId()));
        });
        List<RegionDO> regionPathDOList = regionService.getRegionDOsByRegionIds(enterpriseId,regionIdList);
        Map<String,String> regionMap = regionPathDOList.stream()
                .filter(a -> a.getRegionId() != null && a.getName() != null)
                .collect(Collectors.toMap(data -> data.getRegionId(),data -> data.getName(),(a,b) -> a));

        List<StoreDTO> storeList = storeMapper.getStoreListByStoreIds(enterpriseId,storeIdList);
        Map<String,StoreDTO> storeMap = storeList.stream().collect(Collectors.toMap(storeDO -> storeDO.getStoreId(),data -> data,(a,b)->a));


        List<TbDataTableDO> tbDataTableDOList = tbDataTableMapper.getListByBusinessIdList(enterpriseId,recordIdList,MetaTableConstant.BusinessTypeConstant.PATROL_STORE);
        Map<Long,List<TbDataTableDO>> tbDataTableMap = tbDataTableDOList.stream().collect(Collectors.groupingBy(tbDataTableDO -> tbDataTableDO.getBusinessId()));

        List<TbDataStaTableColumnDO> tbDataStaTableColumnDOS = tbDataStaTableColumnMapper.getListByRecordIdList(enterpriseId,recordIdList);
        List<TbDataDefTableColumnDO> tbDataDefTableColumnDOS = tbDataDefTableColumnMapper.getListByRecordIdList(enterpriseId,recordIdList, null);
        Map<Long,List<TbDataStaTableColumnDO>> recordStaColumnMap =
                tbDataStaTableColumnDOS.stream().collect(Collectors.groupingBy(tbDataStaTableColumnDO -> tbDataStaTableColumnDO.getBusinessId()));

        Map<Long,List<TbDataDefTableColumnDO>> recordDefColumnMap =
                tbDataDefTableColumnDOS.stream().collect(Collectors.groupingBy(data -> data.getBusinessId()));
        List<TaskParentDO> parentDOList = taskParentMapper.selectTaskByIds(enterpriseId, new ArrayList<>(unifyTaskIds));
        List<TaskSubDO> taskSubVOS = taskSubMapper.getDOByIdList(enterpriseId,subTaskList);
        Map<Long,TaskSubDO> subTaskMap = taskSubVOS.stream().collect(Collectors.toMap(data -> data.getId(),data -> data,(a,b)->a));
        Map<Long,TaskParentDO> parentDOMap = parentDOList.stream().collect(Collectors.toMap(taskParentDO -> taskParentDO.getId(),data -> data,(a,b)->a));


        recordList.forEach(record ->{
            PatrolStoreRecordsTableAndPicDTO recordsTableDTO = new PatrolStoreRecordsTableAndPicDTO();
            recordsTableDTO.setBusinessId(record.getId());
            TaskSubDO taskSubDO = subTaskMap.get(record.getSubTaskId());
            recordsTableDTO.setIsOverdue("未过期");
            recordsTableDTO.setTaskStatus("待处理");
            if(record.getStatus() == 1){
                recordsTableDTO.setTaskStatus("已完成");
            }
            if(taskSubDO!=null){
                //处理人取子任务处理人，复审人不取
                if(userMap.get(taskSubDO.getHandleUserId()) != null){
                    recordsTableDTO.setHandler(userMap.get(taskSubDO.getHandleUserId()).getName());
                }else {
                    recordsTableDTO.setHandler(record.getSupervisorName());
                }
                recordsTableDTO.setReChecker("");
                Date taskEndTime = new Date(taskSubDO.getSubEndTime());
                if(record.getSignStartTime() != null && compareDate(taskEndTime,record.getSignStartTime())){
                    recordsTableDTO.setIsOverdue("过期");
                }
                //有效期
                recordsTableDTO.setEffectiveTime(getTime(new Date(taskSubDO.getSubEndTime())));
            }
            TaskParentDO taskParentDO = parentDOMap.get(record.getTaskId());
            if(taskParentDO != null){
                //任务名称
                recordsTableDTO.setTaskName(taskParentDO.getTaskName());
                recordsTableDTO.setNote(taskParentDO.getTaskDesc());
            }

            StoreDTO storeDO = storeMap.get(record.getStoreId());
            List<TbDataTableDO> tableList = tbDataTableMap.get(record.getId());
            if(storeDO!=null){
                recordsTableDTO.setAreaId(storeDO.getRegionId());
                recordsTableDTO.setStoreName(storeDO.getStoreName());
                recordsTableDTO.setStoreId(storeDO.getStoreId());
            }
            recordsTableDTO.setAreaName(regionMap.get(String.valueOf(record.getRegionId())));
            recordsTableDTO.setSupervisorName(record.getSupervisorName());
            //检查表名称
            StringBuffer tableName = new StringBuffer();
            CollectionUtils.emptyIfNull(tableList).stream().filter(table ->!tableName.toString().contains(table.getTableName()))
                    .forEach(table -> {
                        tableName.append(table.getTableName()).append(",");
                    });
            if(tableName.lastIndexOf(",") != -1){
                tableName.deleteCharAt(tableName.lastIndexOf(","));
            }
            recordsTableDTO.setTableName(tableName.toString());

            Integer allCount =0;
            recordsTableDTO.setScore(new BigDecimal(Constants.ZERO_STR));
            recordsTableDTO.setUnQualifiedCount(0);
            List<TbDataStaTableColumnDO> tbDataStaTableColumnDOS1 = recordStaColumnMap.get(record.getId());
            if(tbDataStaTableColumnDOS1 != null){
                allCount += tbDataStaTableColumnDOS1.size();
                tbDataStaTableColumnDOS1.stream().forEach(data ->{
                    if("INAPPLICABLE".equals(data.getCheckResult() )){
                        int a = recordsTableDTO.getUnQualifiedCount();
                        recordsTableDTO.setUnQualifiedCount(a+1);
                    }
                    BigDecimal b = data.getCheckScore() == null?new BigDecimal(Constants.ZERO_STR):data.getCheckScore();
                    BigDecimal c = recordsTableDTO.getScore();
                    recordsTableDTO.setScore(b.add(c));
                });
            }
            List<TbDataDefTableColumnDO> tbDataDefTableColumnDOS1 = recordDefColumnMap.get(record.getId());
            if(tbDataDefTableColumnDOS1 != null){
                allCount += allCount+tbDataDefTableColumnDOS1.size();
            }
            //不适用项数 = 未通过的检查项数?
            recordsTableDTO.setTotalColumnCount(allCount);


            //门店评价不取
            recordsTableDTO.setStoreEvaluation("");

            recordsTableDTO.setSignInTime(getTime(record.getSignStartTime()));
            recordsTableDTO.setSignOutTime(getTime(record.getSignEndTime()));
            recordsTableDTO.setPatrolTime(String.valueOf(record.getTourTime()));
            recordsTableDTO.setSignInAddress(record.getSignStartAddress());
            recordsTableDTO.setSignEndAddress(record.getSignEndAddress());
            if(record.getSignOutStatus() == 2){
                recordsTableDTO.setSignOutStatus("异常");
            }
            else if(record.getSignOutStatus() == 1){
                recordsTableDTO.setSignOutStatus("正常");
            }else {
                recordsTableDTO.setSignOutStatus("未签退");
            }
            if(record.getSignInStatus() == 2){
                recordsTableDTO.setIsAddException("异常");
            }
            else if(record.getSignInStatus() == 1){
                recordsTableDTO.setIsAddException("正常");
            }else {
                recordsTableDTO.setIsAddException("未签到");

            }
            if("PATROL_STORE_OFFLINE".equals(record.getPatrolType())){
                recordsTableDTO.setRecordType("线下巡店");
            }else if("PATROL_STORE_ONLINE".equals(record.getPatrolType())){
                recordsTableDTO.setRecordType("线上巡店");
            }else   if("PATROL_STORE_PICTURE_ONLINE".equals(record.getPatrolType())){
                recordsTableDTO.setRecordType("定时巡检");
            }else {
                recordsTableDTO.setRecordType("线下巡店");
            }

            //创建人
            recordsTableDTO.setCreateUserName(record.getSupervisorName());

            //创建时间
            recordsTableDTO.setCreateTime(getTime(record.getCreateTime()));
            List<String> totalPicList = picMap.get(record.getId());
            List<String> picList = new ArrayList<>();
            if (CollUtil.isNotEmpty(totalPicList)) {
                for (String picStr : totalPicList) {
                    if (StrUtil.isNotBlank(picStr)) {
                        Collections.addAll(picList, picStr.split(","));
                    }
                }
            }
            recordsTableDTO.setPicList(picList);
            result.add(recordsTableDTO);
        });
        return result;
    }

    @Override
    public ImportTaskDO starRecordsExport(String enterpriseId, TableRecordsRequest request) {
        DataSourceHelper.changeToSpecificDataSource(request.getDbName());
        String fileName = ExportTemplateEnum.getByCode(ImportTaskConstant.EXPORT_CHECK_RECORD);
        Date endTime = new Date(System.currentTimeMillis());
        Calendar now = Calendar.getInstance();
        now.setTime(endTime);
        now.set(Calendar.DATE, now.get(Calendar.DATE) - 30);
        Date beginTime = now.getTime();
        beginTime = request.getBeginDate() == null ? beginTime : request.getBeginDate();
        endTime = request.getEndDate() == null ? endTime : request.getEndDate();
        request.setBeginDate(beginTime);
        request.setEndDate(endTime);
        String regionId = request.getRegionId();
        String regionPath = regionService.getRegionPath(enterpriseId, String.valueOf(regionId)).replaceAll("]","");
        request.setRegionPath(regionPath);
        Long totalNum = tbPatrolStoreRecordMapper.tableRecordsCount(enterpriseId, regionPath, beginTime, endTime, request.getIsComplete(),
                request.getMetaTableId(), request.getSupervisorId(), request.getStatus());
        if(totalNum == null || totalNum == 0){
            throw new ServiceException("当前无记录可导出");
        }
        if(totalNum > Constants.MAX_EXPORT_SIZE){
            throw new ServiceException("导出数据不能超过" + Constants.MAX_EXPORT_SIZE +"条，请缩小导出范围");
        }
        ImportTaskDO importTaskDO = importTaskService.insertExportTask(enterpriseId, fileName, ImportTaskConstant.EXPORT_CHECK_RECORD);

        MsgUniteData msgUniteData = new MsgUniteData();

        TableRecordsExportMsg msg = new TableRecordsExportMsg();
        msg.setEnterpriseId(enterpriseId);
        msg.setRequest(request);
        msg.setTotalNum(totalNum);
        msg.setImportTaskDO(importTaskDO);

        msgUniteData.setData(JSONObject.toJSONString(msg));
        msgUniteData.setMsgType(MsgUniteDataTypeEnum.PATROL_RECORD_EXPORT.getCode());
        //分开异步导出
        simpleMessageService.send(JSONObject.toJSONString(msgUniteData), RocketMqTagEnum.EXPORT_IMPORT_MESSAGE);
        return importTaskDO;
    }

    @Override
    @AsyncDynamicExport(type = ImportTaskConstant.EXPORT_CHECK_RECORD)
    public Object tableRecordsAsyncExport(String enterpriseId, TableRecordsRequest request) {
        request.setPageSize(1000);
        request.setPageNum(1);
        List<PatrolStoreRecordsTableAndPicDTO> result = ( List<PatrolStoreRecordsTableAndPicDTO>) tableRecordsExport(enterpriseId, request);
        // 获取动态数据
        List<List<Object>> data = DynamicExcelUtil.tableRecordsData(result);
        // 获取excel表头
        List<List<String>> head = DynamicExcelUtil.tableRecordsHead();
        DynamicExcelUtil.expansionHead(head, data);

        return DynamicExcelUtil.getDataByte("巡店记录", head, data);
    }


    @Deprecated
    @Override
    public PageInfo singleTableColumnsRecords(String enterpriseId, SingleTableColumnsRecordsRequest request) {
        //FIXME wsc 请加上时间范围查询的校验，，
        PageInfo pageInfo = new PageInfo();
        List<SingleTableColumnsRecordsDTO> singleTableColumnsRecordsDTOS = new ArrayList<>();
        List<Long> tableIdList = request.getTableId();

        List<TbMetaTableDO> metaTableDOS = tbMetaTableMapper.selectByIdsAndType(enterpriseId,tableIdList,request.getTableType());
        if(CollectionUtils.isEmpty(metaTableDOS)){
            return pageInfo;
        }
        Map<Long,TbMetaTableDO> metaTableDOMap = metaTableDOS.stream().collect(Collectors.toMap(data -> data.getId(),data -> data,(a,b)->a));
        tableIdList = metaTableDOS.stream().map(data -> data.getId()).collect(Collectors.toList());

        if(request.getPageNum()!=null&&request.getPageSize()!=null){
            PageHelper.startPage(request.getPageNum(),request.getPageSize());
        }
        List<TbPatrolStoreRecordDO> recordDOList = tbPatrolStoreRecordMapper.getListByMetaTableIdListAndTime(enterpriseId,tableIdList,request.getBeginDate(),request.getEndDate());
        pageInfo = new PageInfo(recordDOList);
        if(CollectionUtils.isEmpty(recordDOList)){
            return  pageInfo;
        }
        List<String> regionIdList =new ArrayList<>();
        List<String> storeIdList = new ArrayList<>();
        List<Long> recordIdList = new ArrayList<>();
        recordDOList.stream().forEach(data -> {
            regionIdList.add(String.valueOf(data.getRegionId()));
            storeIdList.add(data.getStoreId());
            recordIdList.add(data.getId());
        });

        List<RegionDO> regionDOList = regionMapper.getRegionByRegionIds(enterpriseId,regionIdList);
        Map<Long,String> regionDOMap = regionDOList.stream()
                .filter(a -> a.getId() != null && a.getName() != null)
                .collect(Collectors.toMap(data -> data.getId(),data->data.getName(),(a,b)->a));
        List<StoreAreaDTO> storeDOList = storeMapper.getStoreAreaList(enterpriseId,storeIdList);
        Map<String,StoreAreaDTO> storeMap = storeDOList.stream().collect(Collectors.toMap(data->data.getStoreId(),data -> data,(a,b)->a));

        List<TbMetaStaTableColumnDO> metaStaTableColumnDOS = tbMetaStaTableColumnMapper.selectColumnListByTableIdList(enterpriseId,tableIdList,Boolean.FALSE);
        Map<Long,List<TbMetaStaTableColumnDO>> metaStaTableMap = metaStaTableColumnDOS.stream().collect(Collectors.groupingBy(data -> data.getMetaTableId()));
        List<TbMetaDefTableColumnDO> metaDefTableColumnDOS = tbMetaDefTableColumnMapper.selectByTableIds(enterpriseId,tableIdList);
        Map<Long,List<TbMetaDefTableColumnDO>> metaDefTableMap = metaDefTableColumnDOS.stream().collect(Collectors.groupingBy(data -> data.getMetaTableId()));
        List<TbDataDefTableColumnDO> dataDefTableColumnDOS = tbDataDefTableColumnMapper.getListByRecordIdList(enterpriseId,recordIdList, null);
        Map<Long,List<TbDataDefTableColumnDO>> dataDefTableMap = dataDefTableColumnDOS.stream().collect(Collectors.groupingBy(data -> data.getBusinessId()));
        List<TbDataStaTableColumnDO> dataStaTableColumnDOS = tbDataStaTableColumnMapper.getListByRecordIdList(enterpriseId,recordIdList);
        Map<Long,List<TbDataStaTableColumnDO>> dataStaTableMap = dataStaTableColumnDOS.stream().collect(Collectors.groupingBy(data -> data.getBusinessId()));

        List<ColumnNameDTO> columnNameList = new ArrayList<>();
        metaStaTableColumnDOS.stream().forEach(data ->{
            String columnName = data.getColumnName();
            Boolean isExit = columnNameList.stream().anyMatch(columnNameDTO -> columnName.equals(columnNameDTO.getColumnName()));
            if(!isExit){
                ColumnNameDTO columnNameDTO = new ColumnNameDTO();
                columnNameDTO.setColumnName(columnName);
                columnNameDTO.setCode(UUIDUtils.get8UUID());
                columnNameList.add(columnNameDTO);
            }
        });
        metaDefTableColumnDOS.stream().forEach(data ->{
            String columnName = data.getColumnName();
            Boolean isExit = columnNameList.stream().anyMatch(columnNameDTO -> columnName.equals(columnNameDTO.getColumnName()));
            if(!isExit){
                ColumnNameDTO columnNameDTO = new ColumnNameDTO();
                columnNameDTO.setColumnName(columnName);
                columnNameDTO.setCode(UUIDUtils.get8UUID());
                columnNameList.add(columnNameDTO);
            }
        });
        Map<String,String> columnNameMap = columnNameList.stream()
                .filter(a -> a.getColumnName() != null && a.getCode() != null)
                .collect(Collectors.toMap(data -> data.getColumnName(),data -> data.getCode(),(a,b)->a));

        recordDOList.stream().forEach(record ->{
            SingleTableColumnsRecordsDTO singleTableColumnsRecordsDTO = new SingleTableColumnsRecordsDTO();
            Long metaTableId = record.getMetaTableId();
            Long businessId = record.getId();
            String storeId = record.getStoreId();
            StoreAreaDTO storeDO = storeMap.get(storeId);

            if(storeDO != null){
                singleTableColumnsRecordsDTO.setStoreName(storeDO.getStoreName());
                singleTableColumnsRecordsDTO.setAreaName(regionDOMap.get(storeDO.getRegionId()));
            }
            TbMetaTableDO metaTableDO =  metaTableDOMap.get(metaTableId);
            List<TbMetaStaTableColumnDO> metaStaTableColumnDOList = sortMetaStaList(metaStaTableMap.get(metaTableId));
            List<TbMetaDefTableColumnDO> metaDefTableColumnDOList = sortMetaDefList(metaDefTableMap.get(metaTableId));
            List<TbDataStaTableColumnDO> tbDataStaTableColumnDOList = sortDataStaList(dataStaTableMap.get(businessId));
            List<TbDataDefTableColumnDO> tbDataDefTableColumnDOList = sortDataDefList(dataDefTableMap.get(businessId));

            singleTableColumnsRecordsDTO.setDefDataColumnList(tbDataDefTableColumnDOList);
            singleTableColumnsRecordsDTO.setDefMetaColumnList(metaDefTableColumnDOList);
            singleTableColumnsRecordsDTO.setStaMetaColumnList(metaStaTableColumnDOList);
            singleTableColumnsRecordsDTO.setStaDataColumnList(tbDataStaTableColumnDOList);
            singleTableColumnsRecordsDTO.setTableName(metaTableDO.getTableName());
            List<ColumnValueDTO> columnValueList = new ArrayList<>();
            if(!TableTypeUtil.isUserDefinedTable(metaTableDO.getTableProperty(),metaTableDO.getTableType())){
                singleTableColumnsRecordsDTO.setTableType("标准检查表");
                tbDataStaTableColumnDOList.stream().forEach(data -> {
                    ColumnValueDTO entity = new ColumnValueDTO();
                    String value = "";
                    if("PASS".equals(data.getCheckResult())){
                        value = "通过";
                    }
                    if("FAIL".equals(data.getCheckResult())){
                        value = "未通过";
                    }
                    if("INAPPLICABLE".equals(data.getCheckResult())){
                        value = "不适用";
                    }
                    entity.setValue(value);
                    entity.setCode(columnNameMap.get(data.getMetaColumnName()));
                    columnValueList.add(entity);
                });

            }
            if(TableTypeUtil.isUserDefinedTable(metaTableDO.getTableProperty(),metaTableDO.getTableType())){
                singleTableColumnsRecordsDTO.setTableType("自定义检查表");
                tbDataDefTableColumnDOList.stream().forEach(data -> {
                    ColumnValueDTO entity = new ColumnValueDTO();
                    String value = data.getValue1();
                    if(data.getValue2() != null){
                        value = value+data.getValue2();
                    }
                    entity.setValue(value);
                    entity.setCode(columnNameMap.get(data.getMetaColumnName()));
                    columnValueList.add(entity);
                });
            }
            singleTableColumnsRecordsDTO.setPatrolTime(getTime(record.getSignStartTime()));
            if("PATROL_STORE_OFFLINE".equals(record.getPatrolType())){
                singleTableColumnsRecordsDTO.setPatrolType("线下巡店");
            }
            if("PATROL_STORE_ONLINE".equals(record.getPatrolType())){
                singleTableColumnsRecordsDTO.setPatrolType("线上巡店");
            }
            if("PATROL_STORE_PICTURE_ONLINE".equals(record.getPatrolType())){
                singleTableColumnsRecordsDTO.setPatrolType("定时巡检");
            }
            singleTableColumnsRecordsDTO.setPatroller(record.getSupervisorName());
            singleTableColumnsRecordsDTO.setColumnValueList(columnValueList);
            singleTableColumnsRecordsDTO.setColumnNameList(columnNameList);
            singleTableColumnsRecordsDTOS.add(singleTableColumnsRecordsDTO);

        });
        pageInfo.setList(singleTableColumnsRecordsDTOS);
        return pageInfo;
    }



    @Override
    public List<PatrolStoreRecordsTableAndPicDTO>  tableRecordsListExport(String enterpriseId, TableRecordsRequest request) {
        List<PatrolStoreRecordsTableAndPicDTO> result =new ArrayList<>();
        PageHelper.startPage(request.getPageNum(),request.getPageSize(), false);
        List<TbPatrolStoreRecordDO> recordList = tbPatrolStoreRecordMapper.tableRecords(enterpriseId,request.getRegionPath(),
                request.getBeginDate(),request.getEndDate(),request.getIsComplete(),
                request.getMetaTableId(), request.getSupervisorId(), request.getStatus());
        if(CollectionUtils.isEmpty(recordList)){
            return result;
        }
        // 获取检查项列表
        List<Long> businessIdList = recordList.stream().map(TbPatrolStoreRecordDO::getId).collect(Collectors.toList());
        List<TbDataStaTableColumnDO> tbDataStaTableColumnList =
                tbDataStaTableColumnMapper.getListByRecordIdListForMap(enterpriseId, businessIdList);
        Map<Long, List<String>> picMap = tbDataStaTableColumnList.stream()
                .collect(Collectors.groupingBy(TbDataStaTableColumnDO::getBusinessId,
                        Collectors.mapping(TbDataStaTableColumnDO::getCheckPics, Collectors.toList())));
        List<String> userIdList = recordList.stream().map(TbPatrolStoreRecordDO::getCreateUserId).collect(Collectors.toList());
        Map<String,EnterpriseUserDO> userMap = enterpriseUserDao.getUserMap(enterpriseId,userIdList);

        List<String> regionIdList = new ArrayList<>();
        List<Long> unifyTaskIds =  new ArrayList<>();
        List<Long> recordIdList =  new ArrayList<>();
        List<Long> subTaskList =  new ArrayList<>();
        List<String> storeIdList = new ArrayList<>();
        recordList.forEach(data -> {
            unifyTaskIds.add(data.getTaskId());
            recordIdList.add(data.getId());
            subTaskList.add(data.getSubTaskId());
            storeIdList.add(data.getStoreId());
            regionIdList.add(String.valueOf(data.getRegionId()));
        });
        List<RegionDO> regionPathDOList = regionMapper.getRegionByRegionIdsForMap(enterpriseId,regionIdList);
        Map<String,String> regionMap = new HashMap<>();
        if(CollectionUtils.isNotEmpty(regionPathDOList)){
            regionPathDOList.forEach(regionDO -> {
                regionMap.put(regionDO.getRegionId(), regionDO.getName());
            });
        }

        List<StoreDTO> storeList = storeMapper.getStoreListByStoreIds(enterpriseId,storeIdList);
        Map<String,StoreDTO> storeMap = storeList.stream().collect(Collectors.toMap(StoreDTO::getStoreId, data -> data,(a, b)->a));


        List<TbDataTableDO> tbDataTableDOList = tbDataTableMapper.getListByBusinessIdList(enterpriseId,recordIdList, MetaTableConstant.BusinessTypeConstant.PATROL_STORE);
        Map<Long,List<TbDataTableDO>> tbDataTableMap = tbDataTableDOList.stream().collect(Collectors.groupingBy(TbDataTableDO::getBusinessId));

        List<TbDataStaTableColumnDO> tbDataStaTableColumnDOS = tbDataStaTableColumnMapper.getListByRecordIdListForMap(enterpriseId,recordIdList);
        List<TbDataDefTableColumnDO> tbDataDefTableColumnDOS = tbDataDefTableColumnMapper.getListByRecordIdList(enterpriseId,recordIdList, null);
        Map<Long,List<TbDataStaTableColumnDO>> recordStaColumnMap =
                tbDataStaTableColumnDOS.stream().collect(Collectors.groupingBy(TbDataStaTableColumnDO::getBusinessId));

        Map<Long,List<TbDataDefTableColumnDO>> recordDefColumnMap =
                tbDataDefTableColumnDOS.stream().collect(Collectors.groupingBy(TbDataDefTableColumnDO::getBusinessId));
        List<TaskParentDO> parentDOList = taskParentMapper.selectTaskByIds(enterpriseId, new ArrayList<>(unifyTaskIds));
        List<TaskSubDO> taskSubVOS = taskSubMapper.getDOByIdListForMap(enterpriseId,subTaskList);
        Map<Long,TaskSubDO> subTaskMap = taskSubVOS.stream().collect(Collectors.toMap(TaskSubDO::getId, data -> data,(a, b)->a));
        Map<Long,TaskParentDO> parentDOMap = parentDOList.stream().collect(Collectors.toMap(TaskParentDO::getId, data -> data,(a, b)->a));

        List<PatrolStoreStatisticsDataStaTableDTO> columnCountList =
                tbDataStaTableColumnMapper.statisticsColumnCountByBusinessId(enterpriseId, new ArrayList<>(recordIdList));
        Map<Long, PatrolStoreStatisticsDataStaTableDTO> dataTableIdColumnCountMap = columnCountList.stream().collect(
                Collectors.toMap(PatrolStoreStatisticsDataStaTableDTO::getBusinessId, Function.identity(), (a, b) -> a));

        recordList.forEach(record ->{
            PatrolStoreRecordsTableAndPicDTO recordsTableDTO = new PatrolStoreRecordsTableAndPicDTO();
            recordsTableDTO.setBusinessId(record.getId());
            TaskSubDO taskSubDO = subTaskMap.get(record.getSubTaskId());
            recordsTableDTO.setIsOverdue("未过期");
            recordsTableDTO.setTaskStatus("待处理");
            if(record.getStatus() == 1){
                recordsTableDTO.setTaskStatus("已完成");
            }
            if(taskSubDO!=null){
                //处理人取子任务处理人，复审人不取
                if(userMap.get(taskSubDO.getHandleUserId()) != null){
                    recordsTableDTO.setHandler(userMap.get(taskSubDO.getHandleUserId()).getName());
                }else {
                    recordsTableDTO.setHandler(record.getSupervisorName());
                }
                recordsTableDTO.setReChecker("");
                Date taskEndTime = new Date(taskSubDO.getSubEndTime());
                if(record.getSignStartTime() != null && compareDate(taskEndTime,record.getSignStartTime())){
                    recordsTableDTO.setIsOverdue("过期");
                }
                //有效期
                recordsTableDTO.setEffectiveTime(getTime(new Date(taskSubDO.getSubEndTime())));
            }
            TaskParentDO taskParentDO = parentDOMap.get(record.getTaskId());
            if(taskParentDO != null){
                //任务名称
                recordsTableDTO.setTaskName(taskParentDO.getTaskName());
                recordsTableDTO.setNote(taskParentDO.getTaskDesc());
            }

            StoreDTO storeDO = storeMap.get(record.getStoreId());
            List<TbDataTableDO> tableList = tbDataTableMap.get(record.getId());
            if(storeDO!=null){
                recordsTableDTO.setAreaId(storeDO.getRegionId());
                recordsTableDTO.setStoreName(storeDO.getStoreName());
                recordsTableDTO.setStoreId(storeDO.getStoreId());
            }else {
                recordsTableDTO.setStoreName(record.getStoreName());
            }
            recordsTableDTO.setAreaName(regionMap.get(String.valueOf(record.getRegionId())));
            recordsTableDTO.setSupervisorName(record.getSupervisorName());
            //检查表名称
            StringBuffer tableName = new StringBuffer();
            CollectionUtils.emptyIfNull(tableList).stream().filter(table ->!tableName.toString().contains(table.getTableName()))
                    .forEach(table -> {
                        tableName.append(table.getTableName()).append(",");
                    });
            if(tableName.lastIndexOf(",") != -1){
                tableName.deleteCharAt(tableName.lastIndexOf(","));
            }
            recordsTableDTO.setTableName(tableName.toString());

            int allCount = 0;
            recordsTableDTO.setScore(new BigDecimal(Constants.ZERO_STR));
            recordsTableDTO.setUnQualifiedCount(0);
            List<TbDataStaTableColumnDO> tbDataStaTableColumnDOS1 = recordStaColumnMap.get(record.getId());
            if(tbDataStaTableColumnDOS1 != null){
                allCount += tbDataStaTableColumnDOS1.size();
                tbDataStaTableColumnDOS1.forEach(data ->{
                    if("INAPPLICABLE".equals(data.getCheckResult() )){
                        int a = recordsTableDTO.getUnQualifiedCount();
                        recordsTableDTO.setUnQualifiedCount(a+1);
                    }
                    BigDecimal b = data.getCheckScore() == null?new BigDecimal(Constants.ZERO_STR):data.getCheckScore();
                    BigDecimal c = recordsTableDTO.getScore();
                    recordsTableDTO.setScore(b.add(c));
                });
            }
            List<TbDataDefTableColumnDO> tbDataDefTableColumnDOS1 = recordDefColumnMap.get(record.getId());
            if(tbDataDefTableColumnDOS1 != null){
                allCount += allCount+tbDataDefTableColumnDOS1.size();
            }
            //不适用项数 = 未通过的检查项数?
            recordsTableDTO.setTotalColumnCount(allCount);


            //门店评价不取
            recordsTableDTO.setStoreEvaluation("");

            recordsTableDTO.setSignInTime(getTime(record.getSignStartTime()));
            recordsTableDTO.setSignOutTime(getTime(record.getSignEndTime()));
            recordsTableDTO.setPatrolTime(String.valueOf(record.getTourTime()));
            recordsTableDTO.setSignInAddress(record.getSignStartAddress());
            recordsTableDTO.setSignEndAddress(record.getSignEndAddress());
            if(record.getSignOutStatus() == 2){
                recordsTableDTO.setSignOutStatus("异常");
            }
            else if(record.getSignOutStatus() == 1){
                recordsTableDTO.setSignOutStatus("正常");
            }else {
                recordsTableDTO.setSignOutStatus("未签退");
            }
            if(record.getSignInStatus() == 2){
                recordsTableDTO.setIsAddException("异常");
            }
            else if(record.getSignInStatus() == 1){
                recordsTableDTO.setIsAddException("正常");
            }else {
                recordsTableDTO.setIsAddException("未签到");

            }
            if("PATROL_STORE_OFFLINE".equals(record.getPatrolType())){
                recordsTableDTO.setRecordType("线下巡店");
            }else if("PATROL_STORE_ONLINE".equals(record.getPatrolType())){
                recordsTableDTO.setRecordType("线上巡店");
            }else if("PATROL_STORE_PICTURE_ONLINE".equals(record.getPatrolType())){
                recordsTableDTO.setRecordType("定时巡检");
            }else {
                recordsTableDTO.setRecordType("线下巡店");
            }
            PatrolStoreStatisticsDataStaTableDTO dataStaTableDTO = dataTableIdColumnCountMap.get(record.getId());
            if(dataStaTableDTO != null){
                recordsTableDTO.setPassColumnCount(dataStaTableDTO.getPassColumnCount());
                recordsTableDTO.setFailColumnCount(dataStaTableDTO.getFailColumnCount());
                recordsTableDTO.setInapplicableColumnCount(dataStaTableDTO.getInapplicableColumnCount());
                recordsTableDTO.setScore(dataStaTableDTO.getScore());
            }

            //创建人
            recordsTableDTO.setCreateUserName(record.getSupervisorName());

            //创建时间
            recordsTableDTO.setCreateTime(getTime(record.getCreateTime()));
            List<String> totalPicList = picMap.get(record.getId());
            List<String> picList = new ArrayList<>();
            if (CollUtil.isNotEmpty(totalPicList)) {
                for (String picStr : totalPicList) {
                    if (StrUtil.isNotBlank(picStr)) {
                        Collections.addAll(picList, picStr.split(","));
                    }
                }
            }
            recordsTableDTO.setPicList(picList);
            result.add(recordsTableDTO);
        });
        return result;
    }

    @Override
    public PageInfo potralRecordList(String enterpriseId, PatrolStoreStatisticsDataTableQuery query) {
        // 查询巡店检查表记录
        //检查输入是否为空
        if(CollectionUtils.isEmpty(query.getRegionIdList())  && CollectionUtils.isEmpty(query.getStoreIdList())){
            SelectComptRegionStoreVO regionAndStore = selectionComponentService.getRegionAndStore(enterpriseId, null, UserHolder.getUser().getUserId(), null);
            //如果管辖为空返回空数组
            if (CollectionUtils.isEmpty(regionAndStore.getAllRegionList())){
                return new PageInfo(Lists.newArrayList());
            }
            query.setRegionIdList(regionAndStore.getAllRegionList().stream().map(SelectComponentRegionVO::getId).collect(Collectors.toList()));
        }

        if(query.getRegionId() != null){
            String regionPath = regionService.getRegionPath(enterpriseId, String.valueOf(query.getRegionId()));
            query.setRegionPath(regionPath);
        }
        if(CollectionUtils.isNotEmpty(query.getRegionIdList())){
            List<RegionPathDTO> regionPathDTOList = regionService.getRegionPathByList(enterpriseId, query.getRegionIdList());
            if(CollectionUtils.isNotEmpty(regionPathDTOList)){
                List<String> regionPathList =  regionPathDTOList.stream().map(RegionPathDTO::getRegionPath).collect(Collectors.toList());
                query.setRegionPathList(regionPathList);
            }
        }

        PageHelper.startPage(query.getPageNum(), query.getPageSize());
        List<TbPatrolStoreRecordDO> tableRecordDOList =
                tbPatrolStoreRecordMapper.statisticsDataTable(enterpriseId, query, null);
        //未开始逻辑添加
        tableRecordDOList.stream().forEach(tbPatrolStoreRecordDO -> {
            if (tbPatrolStoreRecordDO.getSubBeginTime()!=null&&tbPatrolStoreRecordDO.getSubBeginTime().compareTo(new Date())>0){
                tbPatrolStoreRecordDO.setStatus(3);
            }
        });
        if (CollectionUtils.isEmpty(tableRecordDOList)) {
            return new PageInfo();
        }
        PageInfo pageInfo = new PageInfo(tableRecordDOList);
        String businessType = query.getBusinessCheckType();
        pageInfo.setList(patrolStoreService.statisticsStaTableDataList(enterpriseId, tableRecordDOList, null, query.getLevelInfo(), false, query.getMetaTableIds(), businessType));
        return pageInfo;
    }

    @Override
    public ImportTaskDO potralRecordListExport(String enterpriseId, PatrolStoreStatisticsDataTableQuery query) {
        if(query.getRegionId() != null){
            String regionPath = regionService.getRegionPath(enterpriseId, String.valueOf(query.getRegionId()));
            query.setRegionPath(regionPath);
        }
        if(CollectionUtils.isNotEmpty(query.getRegionIdList())){
            List<RegionPathDTO> regionPathDTOList = regionService.getRegionPathByList(enterpriseId, query.getRegionIdList());
            if(CollectionUtils.isNotEmpty(regionPathDTOList)){
                List<String> regionPathList =  regionPathDTOList.stream().map(RegionPathDTO::getRegionPath).collect(Collectors.toList());
                query.setRegionPathList(regionPathList);
            }
        }
        Long totalNum = tbPatrolStoreRecordMapper.statisticsDataTableCount(enterpriseId, query,  null);
        if(totalNum == null || totalNum == 0){
            throw new ServiceException("当前无记录可导出");
        }
        if(totalNum > Constants.MAX_EXPORT_SIZE){
            throw new ServiceException("导出数据不能超过" + Constants.MAX_EXPORT_SIZE +"条，请缩小导出范围");
        }
        String fileName = ExportTemplateEnum.getByCode(ImportTaskConstant.EXPORT_PATROL_RECORD);
        if (TaskTypeEnum.PATROL_STORE_SAFETY_CHECK.getCode().equals(query.getPatrolType())) {
            fileName = ExportTemplateEnum.getByCode(ImportTaskConstant.EXPORT_SAFETY_CHECK);
            fileName = fileName.replace("exportTime", DateUtil.format(new Date(), DateUtils.DATE_FORMAT_MINUTE));
        }
        ImportTaskDO importTaskDO = importTaskService.insertExportTask(enterpriseId, fileName, ImportTaskConstant.EXPORT_PATROL_RECORD);

        MsgUniteData msgUniteData = new MsgUniteData();
        query.setType(1);
        ExportTaskStageRecordListRequest msg = new ExportTaskStageRecordListRequest();
        msg.setEnterpriseId(enterpriseId);
        msg.setRequest(query);
        msg.setTotalNum(totalNum);
        msg.setImportTaskDO(importTaskDO);
        msgUniteData.setData(JSONObject.toJSONString(msg));
        msgUniteData.setMsgType(MsgUniteDataTypeEnum.EXPORT_TASK_STAGE_LIST_RECORD.getCode());
        //分开异步导出
        simpleMessageService.send(JSONObject.toJSONString(msgUniteData), RocketMqTagEnum.EXPORT_IMPORT_MESSAGE);
        return importTaskDO;
    }

    @Override
    public PageInfo potralRecordDetailList(String enterpriseId, PatrolStoreStatisticsDataTableQuery query) {
        List<Long> businessIdList = new ArrayList<>();
        // 查询巡店检查表记录
        if (query.getBusinessId() == null && query.getBeginDate() == null && query.getEndDate() == null
                && query.getSignInBeginDate() == null && query.getSignInEndDate() == null
                && query.getSignOutBeginDate() == null && query.getSignOutEndDate() == null) {
            Date endTime = new Date(System.currentTimeMillis());
            Calendar now = Calendar.getInstance();
            now.setTime(endTime);
            now.set(Calendar.DATE, now.get(Calendar.DATE) - 30);
            query.setBeginDate(now.getTime());
            query.setEndDate(endTime);
        }
        boolean isDefine = false;
        if(query.getIsDefine() != null){
            isDefine = query.getIsDefine();
        }
        if(query.getBusinessId() != null){
            TbPatrolStoreRecordDO recordDO = tbPatrolStoreRecordMapper.selectById(enterpriseId, query.getBusinessId());
            if(recordDO == null){
                throw new ServiceException("巡店记录不存在");
            }
            Long metaTableId = getFirstMetaTableId(recordDO.getMetaTableIds());
            if(Objects.nonNull(metaTableId)){
                TbMetaTableDO tbMetaTableDO = tbMetaTableMapper.selectById(enterpriseId, metaTableId);
                isDefine = TableTypeUtil.isUserDefinedTable(tbMetaTableDO.getTableProperty(), tbMetaTableDO.getTableType());
            }
            businessIdList.add(query.getBusinessId());
        }

        if(CollectionUtils.isNotEmpty(query.getMetaTableIds())){
            TbMetaTableDO tbMetaTableDO = tbMetaTableMapper.selectById(enterpriseId, query.getMetaTableIds().get(0));
            if(tbMetaTableDO != null){
                isDefine = TableTypeUtil.isUserDefinedTable(tbMetaTableDO.getTableProperty(), tbMetaTableDO.getTableType());
            }
        }
        if(query.getRegionId() != null){
            String regionPath = regionService.getRegionPath(enterpriseId, String.valueOf(query.getRegionId()));
            query.setRegionPath(regionPath);
        }

        List<TbMetaStaColumnDetailExportVO> staColumnList = new ArrayList<>();

        PageInfo pageInfo;

        if(!isDefine){

            PageHelper.startPage(query.getPageNum(), query.getPageSize());
            List<TbMetaStaColumnVO> tbDataStaTableColumnList = tbDataStaTableColumnMapper.getVOListByRecordIdAndPatrolStoreTime(enterpriseId, businessIdList, query);
            pageInfo = new PageInfo(tbDataStaTableColumnList);
            if(CollectionUtils.isEmpty(tbDataStaTableColumnList)){
                pageInfo.setList(tbDataStaTableColumnList);
                return pageInfo;
            }
            Set<String> userIds = new HashSet<>();
            Set<Long> metaTableIds = new HashSet<>();
            Set<Long> businessIds = new HashSet<>();
            Set<Long> taskIds = new HashSet<>();
            Set<String> regionIds = new HashSet<>();
            for(TbMetaStaColumnVO staDO : tbDataStaTableColumnList){
                metaTableIds.add(staDO.getMetaTableId());
                businessIds.add(staDO.getBusinessId());
                taskIds.add(staDO.getTaskId());
                taskIds.add(staDO.getTaskId());
                regionIds.add(String.valueOf(staDO.getRegionId()));
            }

            List<TaskParentDO> parentDOList = taskParentMapper.selectTaskByIdsForMap(enterpriseId, new ArrayList<>(taskIds));
            List<RegionDO> regionList = regionMapper.getRegionByRegionIdsForMap(enterpriseId, new ArrayList<>(regionIds));
            Map<Long, TaskParentDO> parentMap = parentDOList.stream()
                    .collect(Collectors.toMap(TaskParentDO::getId, Function.identity(), (a, b) -> a));
            Map<String, RegionDO> regionMap = regionList.stream()
                    .collect(Collectors.toMap(RegionDO::getRegionId, Function.identity(), (a, b) -> a));

            List<TbMetaStaTableColumnDO> list = tbMetaStaTableColumnMapper.getAllColumnBymetaTableIdList(enterpriseId, new ArrayList<>(metaTableIds));


            List<TbMetaTableDO> mataTableList = tbMetaTableMapper.selectByIds(enterpriseId, new ArrayList<>(metaTableIds));

            Map<Long, TbMetaTableDO> mataTableMap = mataTableList.stream().collect(Collectors.toMap(TbMetaTableDO::getId, data -> data));

            // 获取检查表停留时间
            Map<Long, String> dataTableDwellTime = getDataTableDwellTimeMap(enterpriseId, new ArrayList<>(businessIds));
            List<TbPatrolStoreRecordDO> recordList = tbPatrolStoreRecordMapper.selectByIds(enterpriseId, new ArrayList<>(businessIds));
            Map<Long, TbPatrolStoreRecordDO> recordMap = recordList.stream()
                    .collect(Collectors.toMap(TbPatrolStoreRecordDO::getId, Function.identity(), (a, b) -> a));

            List<String> storeIdList = ListUtils.emptyIfNull(recordList)
                    .stream()
                    .map(TbPatrolStoreRecordDO::getStoreId)
                    .distinct()
                    .collect(Collectors.toList());
            List<StoreDO> storeList= storeMapper.getStoreByStoreIdList(enterpriseId, storeIdList);
            Map<String, String> storeNumMap = ListUtils.emptyIfNull(storeList)
                    .stream()
                    .filter(data-> StringUtils.isNotBlank(data.getStoreNum()))
                    .collect(Collectors.toMap(StoreDO::getStoreId, StoreDO::getStoreNum, (a, b) -> a));
            Map<String, List<String>> fullRegionNameMap = regionService.getFullRegionNameListByStoreList(enterpriseId, storeList);
            Set<Long> sopIds = new HashSet<>();
            Set<String> coolCourseIds = new HashSet<>();
            for(TbMetaStaTableColumnDO tbMetaStaColumnVO : list){
                if(tbMetaStaColumnVO.getSopId() != null){
                    sopIds.add(tbMetaStaColumnVO.getSopId());
                }
                if(StringUtils.isNotBlank(tbMetaStaColumnVO.getCoolCourse())){
                    coolCourseIds.add(tbMetaStaColumnVO.getCoolCourse());
                }
            }
            Map<Long, String> taskSopMap = new HashMap<>();
            if(CollectionUtils.isNotEmpty(sopIds)){
                List<TaskSopVO> taskSopList = taskSopMapper.listByIdList(enterpriseId, new ArrayList<>(sopIds));
                taskSopMap = taskSopList.stream().collect(Collectors.toMap(TaskSopVO::getId,TaskSopVO::getFileName));
            }


            Map<Long, TbMetaStaTableColumnDO> idMetaTableColumnMap = list.stream()
                    .collect(Collectors.toMap(TbMetaStaTableColumnDO::getId, Function.identity(), (a, b) -> a));


            Boolean accessCoolCollege = false;
            DataSourceHelper.reset();
            EnterpriseSettingDO settingDO = enterpriseSettingMapper.selectByEnterpriseId(enterpriseId);
            accessCoolCollege = settingDO.getAccessCoolCollege();
            EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigMapper.selectByEnterpriseId(enterpriseId);
            DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());

            Map<Long, String> finalTaskSopMap = taskSopMap;
            Boolean finalAccessCoolCollege = accessCoolCollege;
                tbDataStaTableColumnList.forEach(e -> {
                    try {
                        TbMetaStaColumnDetailExportVO metaStaColumnVO = new TbMetaStaColumnDetailExportVO();
                        metaStaColumnVO.setId(e.getId());
                        metaStaColumnVO.setColumnName(e.getColumnName());
                        metaStaColumnVO.setBusinessId(e.getBusinessId());
                        metaStaColumnVO.setSupervisorId(e.getSupervisorId());
                        metaStaColumnVO.setRegionId(e.getRegionId());
                        TbPatrolStoreRecordDO tbPatrolStoreRecordDO = null;
                        TaskParentDO taskParentDO = null;
                        if (Objects.nonNull(recordMap)){
                            tbPatrolStoreRecordDO = recordMap.get(e.getBusinessId());
                        }
                        if (Objects.nonNull(parentMap)){
                            taskParentDO = parentMap.get(e.getTaskId());
                        }
                        metaStaColumnVO.setCreateTime(e.getCreateTime());
                        if(Objects.nonNull(taskParentDO)){
                            metaStaColumnVO.setTaskDesc(taskParentDO.getTaskDesc());
                            metaStaColumnVO.setTaskName(taskParentDO.getTaskName());
                        }
                        if(Objects.nonNull(tbPatrolStoreRecordDO)){
                            metaStaColumnVO.setStoreName(tbPatrolStoreRecordDO.getStoreName());
                            metaStaColumnVO.setStoreNum(storeNumMap.get(tbPatrolStoreRecordDO.getStoreId()));
                            String validTime = "";
                            // validTime
                            if(tbPatrolStoreRecordDO.getSubBeginTime() != null){
                                validTime = DateUtils.convertTimeToString(tbPatrolStoreRecordDO.getSubBeginTime().getTime(), DATE_FORMAT_SEC_5) + "-";
                            }
                            if(Objects.nonNull(tbPatrolStoreRecordDO.getSubEndTime())){
                                validTime = validTime + DateUtils.convertTimeToString(tbPatrolStoreRecordDO.getSubEndTime().getTime(), DATE_FORMAT_SEC_5);
                            }
                            metaStaColumnVO.setValidTime(validTime);
                            metaStaColumnVO.setStoreId(tbPatrolStoreRecordDO.getStoreId());
                            metaStaColumnVO.setSubBeginTime(tbPatrolStoreRecordDO.getSubBeginTime());
                            metaStaColumnVO.setSubEndTime(tbPatrolStoreRecordDO.getSubEndTime());
                            metaStaColumnVO.setSupervisorName(tbPatrolStoreRecordDO.getSupervisorName());
                            metaStaColumnVO.setSupervisorId(tbPatrolStoreRecordDO.getSupervisorId());
                        }

                        metaStaColumnVO.setCheckResultName(e.getCheckResultName());
                        metaStaColumnVO.setStatisticalDimension(e.getStatisticalDimension());
                        metaStaColumnVO.setCheckResult(e.getCheckResult());
                        metaStaColumnVO.setCheckPics(e.getCheckPics());
                        metaStaColumnVO.setCheckVideo(e.getCheckVideo());
                        metaStaColumnVO.setCheckInfo(e.getCheckInfo());

                        metaStaColumnVO.setCheckText(e.getCheckText());
                        metaStaColumnVO.setCheckResultReason(e.getCheckResultReason());
                        metaStaColumnVO.setStoreSceneId(e.getStoreSceneId());
                        metaStaColumnVO.setStoreSceneName(e.getStoreSceneName());
                        RegionDO regionDO = regionMap.get(String.valueOf(e.getRegionId()));

                        if(regionDO != null){
                            metaStaColumnVO.setRegionName(regionDO.getName());
                        }
                        metaStaColumnVO.setFullRegionName(StringUtils.join(fullRegionNameMap.get(tbPatrolStoreRecordDO.getStoreId()), Constants.SPLIT_LINE));
                        metaStaColumnVO.setRegionNameList(fullRegionNameMap.get(tbPatrolStoreRecordDO.getStoreId()));
                        metaStaColumnVO.setMetaTableId(e.getMetaTableId());

                        // 检查表页面停留时间
                        metaStaColumnVO.setDwellTime(dataTableDwellTime.get(e.getDataTableId()));
                        metaStaColumnVO.setMetaTableName(mataTableMap.get(e.getMetaTableId()).getTableName());
                        metaStaColumnVO.setTableProperty(mataTableMap.get(e.getMetaTableId()).getTableProperty());
                        TbMetaStaTableColumnDO tableColumnDO = idMetaTableColumnMap.get(e.getMetaColumnId());
                        if(tableColumnDO != null){
                            String coolCourseAndSop = null;
                            metaStaColumnVO.setCoolCourse(tableColumnDO.getCoolCourse());
                            if(tableColumnDO.getSopId() != null){
                                coolCourseAndSop = finalTaskSopMap.get(tableColumnDO.getSopId());
                            }
                            String course;
                            if(finalAccessCoolCollege){
                                course = tableColumnDO.getCoolCourse();
                            }else{
                                course = tableColumnDO.getFreeCourse();
                            }

                            if(StringUtils.isNotBlank(course)){
                                CoolCourseVO coolCourseVO = JSON.parseObject(course, CoolCourseVO.class);
                                if(coolCourseVO != null){
                                    coolCourseAndSop = StringUtils.isNotBlank(coolCourseAndSop) ?  coolCourseAndSop + "/" + coolCourseVO.getTitle() : coolCourseVO.getTitle();
                                }
                            }
                            metaStaColumnVO.setCoolCourseAndSop(coolCourseAndSop);
                            metaStaColumnVO.setCategoryName(tableColumnDO.getCategoryName());
                            metaStaColumnVO.setSupportScore(tableColumnDO.getSupportScore());
                            metaStaColumnVO.setPunishMoney(tableColumnDO.getPunishMoney());
                            metaStaColumnVO.setAwardMoney(tableColumnDO.getAwardMoney());
                            metaStaColumnVO.setStandardPic(tableColumnDO.getStandardPic());
                            metaStaColumnVO.setSopId(tableColumnDO.getSopId());
                            metaStaColumnVO.setDescription(tableColumnDO.getDescription());
                            //
                            String checkAwardPunish = "";
                            if (e.getCheckResultId() != null && e.getCheckResultId() != 0){
                                metaStaColumnVO.setAwardPunish(String.format("奖:%s,罚:%s",e.getColumnMaxAward(), e.getColumnMaxAward()));
                                if (PASS.equals(e.getCheckResult())) {
                                    checkAwardPunish = String.format("奖:%s", e.getRewardPenaltMoney().multiply(e.getAwardTimes()).setScale(2,BigDecimal.ROUND_HALF_UP));
                                }
                                if (FAIL.equals(e.getCheckResult())) {
                                    checkAwardPunish = String.format("罚:%s", e.getRewardPenaltMoney().multiply(e.getAwardTimes()).setScale(2,BigDecimal.ROUND_HALF_UP));
                                }
                            }
                            metaStaColumnVO.setCheckAwardPunish(checkAwardPunish);
                            BigDecimal checkScore =e.getCheckScore().multiply(e.getScoreTimes()).setScale(2,BigDecimal.ROUND_HALF_UP);
                            TbMetaTableDO tbMetaTableDO = mataTableMap.get(e.getMetaTableId());
                            if (tbMetaTableDO!=null&& MetaTablePropertyEnum.WEIGHT_TABLE.getCode().equals(tbMetaTableDO.getTableProperty())){
                                checkScore = checkScore.multiply(e.getWeightPercent()).divide(new BigDecimal(Constants.ONE_HUNDRED), 2,BigDecimal.ROUND_HALF_UP);
                            }
                            metaStaColumnVO.setCheckScore(checkScore);
                            metaStaColumnVO.setPatrolType(TaskTypeEnum.getDescByCode(e.getPatrolType()));
                        }
                        staColumnList.add(metaStaColumnVO);
                    } catch (Exception exception) {
                        log.error("tbDataStaTableColumnList foreach error",exception);
                    }
                });

        }else {
            PageHelper.startPage(query.getPageNum(), query.getPageSize());
            List<TbDataDefTableColumnDO> tbDataDefTableColumnList = tbDataDefTableColumnMapper.getListByRecordIdListAndPatrolStoreTime(enterpriseId, businessIdList, query);
            pageInfo = new PageInfo(tbDataDefTableColumnList);
            if(CollectionUtils.isEmpty(tbDataDefTableColumnList)){
                pageInfo.setList(staColumnList);
                return pageInfo;
            }

            Set<Long> metaTableIds = new HashSet<>();
            Set<Long> businessIds = new HashSet<>();
            Set<Long> taskIds = new HashSet<>();
            Set<String> regionIds = new HashSet<>();
            for(TbDataDefTableColumnDO staDO : tbDataDefTableColumnList){
                metaTableIds.add(staDO.getMetaTableId());
                businessIds.add(staDO.getBusinessId());
                taskIds.add(staDO.getTaskId());
                taskIds.add(staDO.getTaskId());
                regionIds.add(String.valueOf(staDO.getRegionId()));
            }

            List<TbMetaDefTableColumnDO> list = tbMetaDefTableColumnMapper.getAllColumnByMetaTableIdList(enterpriseId, new ArrayList<>(metaTableIds));


            List<TaskParentDO> parentDOList = taskParentMapper.selectTaskByIdsForMap(enterpriseId, new ArrayList<>(taskIds));

            List<RegionDO> regionList = regionMapper.getRegionByRegionIdsForMap(enterpriseId, new ArrayList<>(regionIds));

            Map<Long, TaskParentDO> parentMap = parentDOList.stream()
                    .collect(Collectors.toMap(TaskParentDO::getId, Function.identity(), (a, b) -> a));

            Map<String, RegionDO> regionMap = regionList.stream()
                    .collect(Collectors.toMap(RegionDO::getRegionId, Function.identity(), (a, b) -> a));


            List<TbMetaTableDO> mataTableList = tbMetaTableMapper.selectByIds(enterpriseId, new ArrayList<>(metaTableIds));

            Map<Long, String> mataTableMap = mataTableList.stream().collect(Collectors.toMap(TbMetaTableDO::getId, TbMetaTableDO::getTableName));
            // 获取检查表停留时间
            Map<Long, String> dataTableDwellTime = getDataTableDwellTimeMap(enterpriseId, new ArrayList<>(businessIds));
            List<TbPatrolStoreRecordDO> recordList = tbPatrolStoreRecordMapper.selectByIds(enterpriseId, new ArrayList<>(businessIds));
            Map<Long, TbPatrolStoreRecordDO> recordMap = ListUtils.emptyIfNull(recordList).stream()
                    .collect(Collectors.toMap(TbPatrolStoreRecordDO::getId, Function.identity(), (a, b) -> a));
                List<StorePathDTO> storePathDTOList = ListUtils.emptyIfNull(recordList)
                        .stream()
                        .map(data -> {
                            StorePathDTO storePathDTO = new StorePathDTO();
                            storePathDTO.setStoreId(data.getStoreId());
                            storePathDTO.setRegionPath(data.getRegionWay());
                            return storePathDTO;
                        }).collect(Collectors.toList());

                List<String> storeIdList = ListUtils.emptyIfNull(recordList)
                        .stream()
                        .map(TbPatrolStoreRecordDO::getStoreId)
                        .distinct()
                        .collect(Collectors.toList());
            List<StoreDO> storeList =new ArrayList<>();
                if(CollectionUtils.isNotEmpty(storeIdList)){
                    storeList = storeMapper.getStoreByStoreIdList(enterpriseId, storeIdList);

                }
            Map<String, String> storeNumMap= ListUtils.emptyIfNull(storeList)
                        .stream()
                        .filter(data-> StringUtils.isNotBlank(data.getStoreNum()))
                        .collect(Collectors.toMap(StoreDO::getStoreId, StoreDO::getStoreNum, (a, b) -> a));
            Map<String, List<String>> fullRegionNameMap  = regionService.getFullRegionNameList(enterpriseId, storePathDTOList);

            Map<Long, TbMetaDefTableColumnDO> idMetaDefTableColumnMap = list.stream()
                    .collect(Collectors.toMap(TbMetaDefTableColumnDO::getId, Function.identity(), (a, b) -> a));
            tbDataDefTableColumnList.forEach(e -> {
                TbMetaStaColumnDetailExportVO metaStaColumnVO = new TbMetaStaColumnDetailExportVO();
                metaStaColumnVO.setId(e.getId());
                metaStaColumnVO.setBusinessId(e.getBusinessId());
                metaStaColumnVO.setColumnName(e.getMetaColumnName());
                metaStaColumnVO.setCreateTime(e.getPatrolStoreTime());
                metaStaColumnVO.setSupervisorId(e.getSupervisorId());
                metaStaColumnVO.setRegionId(e.getRegionId());
                TaskParentDO taskParentDO = parentMap.get(e.getTaskId());
                if(taskParentDO != null){
                    metaStaColumnVO.setTaskDesc(taskParentDO.getTaskDesc());

                    metaStaColumnVO.setTaskName(taskParentDO.getTaskName());
                }
                TbPatrolStoreRecordDO recordDO = recordMap.get(e.getBusinessId());
                metaStaColumnVO.setValue1(e.getValue1());
                metaStaColumnVO.setValue2(e.getValue2());
                metaStaColumnVO.setCheckVideo(e.getCheckVideo());
                TbMetaDefTableColumnDO metaDefTableColumnDO = idMetaDefTableColumnMap.get(e.getMetaColumnId());
                if(metaDefTableColumnDO != null){
                    metaStaColumnVO.setFormat(metaDefTableColumnDO.getFormat());
                }
                RegionDO regionDO = regionMap.get(String.valueOf(e.getRegionId()));

                if(regionDO != null){
                    metaStaColumnVO.setRegionName(regionDO.getName());
                }
                metaStaColumnVO.setMetaTableId(e.getMetaTableId());
                // 检查表页面停留时间
                metaStaColumnVO.setDwellTime(dataTableDwellTime.get(e.getDataTableId()));
                metaStaColumnVO.setMetaTableName(mataTableMap.get(e.getMetaTableId()));
                metaStaColumnVO.setDescription(e.getDescription());
                if(recordDO != null){
                    metaStaColumnVO.setStoreName(recordDO.getStoreName());
                    // validTime
                    if(recordDO.getSubBeginTime() != null&&recordDO.getSubEndTime()!=null){
                        String validTime = DateUtils.convertTimeToString(recordDO.getSubBeginTime().getTime(), DATE_FORMAT_SEC_5) + "-"
                                + DateUtils.convertTimeToString(recordDO.getSubEndTime().getTime(), DATE_FORMAT_SEC_5);
                        metaStaColumnVO.setValidTime(validTime);
                    }
                    metaStaColumnVO.setStoreId(recordDO.getStoreId());
                    metaStaColumnVO.setStoreNum(storeNumMap.get(recordDO.getStoreId()));
                    metaStaColumnVO.setFullRegionName(StringUtils.join(fullRegionNameMap.get(recordDO.getStoreId()),Constants.SPLIT_LINE));
                    metaStaColumnVO.setRegionNameList(fullRegionNameMap.get(recordDO.getStoreId()));
                    metaStaColumnVO.setSubBeginTime(recordDO.getSubBeginTime());
                    metaStaColumnVO.setSubEndTime(recordDO.getSubEndTime());
                    metaStaColumnVO.setSupervisorName(recordDO.getSupervisorName());
                    metaStaColumnVO.setSupervisorId(recordDO.getSupervisorId());
                    metaStaColumnVO.setPatrolType(TaskTypeEnum.getDescByCode(recordDO.getPatrolType()));
                }
                staColumnList.add(metaStaColumnVO);
            });
        }
        pageInfo.setList(staColumnList);
        return pageInfo;
    }

    private static Long getFirstMetaTableId(String metaTableIds){
        if(StringUtils.isBlank(metaTableIds)){
            return null;
        }
        return Arrays.stream(metaTableIds.split(Constants.COMMA)).filter(StringUtils::isNotBlank).map(Long::valueOf).findFirst().orElse(null);
    }


    /**
     * 获取检查表停留时间
     * @param enterpriseId 企业id
     * @param businessIds  业务id集合
     * @return 检查表停留时间map集合
     */
    @Override
    public Map<Long, String> getDataTableDwellTimeMap(String enterpriseId, List<Long> businessIds) {
        if (StringUtils.isBlank(enterpriseId) || CollectionUtils.isEmpty(businessIds)) {
            return Maps.newHashMap();
        }
        List<TbPatrolStoreRecordInfoDO> recordInfoList = tbPatrolStoreRecordInfoMapper.selectTbPatrolStoreRecordInfoList(enterpriseId, businessIds);
        Map<Long, String> result = new HashMap<>(recordInfoList.size() * 2);
        recordInfoList.forEach(record -> {
            String params = record.getParams();
            if (StringUtils.isNotBlank(params)) {
                JSONObject paramJson = JSONObject.parseObject(params);
                // 检查表停留时间 key: dataTableId, value: dwellTime
                JSONObject dataTableDwellTime = paramJson.getJSONObject("dataTableDwellTime");
                if (Objects.nonNull(dataTableDwellTime)) {
                    Set<String> keys = dataTableDwellTime.keySet();
                    for (String key : keys) {
                        String value = dataTableDwellTime.getString(key);
                        result.put(Long.valueOf(key), value);
                    }
                }
            }
        });
        return result;
    }

    @Override
    public ImportTaskDO potralRecordDetailListExport(String enterpriseId, PatrolStoreStatisticsDataTableQuery query){
        String md5 = MD5Util.md5(JSONObject.toJSONString(query));
        String lockKey  = MessageFormat.format("patrolRecordDetailListExport:{0}:{1}:{2}", enterpriseId, UserHolder.getUser().getUserId(), md5);
        boolean isLock = redisUtilPool.setNxExpire(lockKey, String.valueOf(System.currentTimeMillis()), 60 * 1000);
        if(!isLock){
            throw new ServiceException(ErrorCodeEnum.EXPORTING);
        }
        List<Long> buesinessIds = new ArrayList<>();
        Boolean isDefine = false;
        if(query.getBusinessId() != null){
            TbPatrolStoreRecordDO recordDO = tbPatrolStoreRecordMapper.selectById(enterpriseId, query.getBusinessId());
            if(recordDO == null){
                redisUtilPool.delKey(lockKey);
                throw new ServiceException("巡店记录不存在");
            }
            Long metaTableId = getFirstMetaTableId(recordDO.getMetaTableIds());
            if(Objects.nonNull(metaTableId)){
                TbMetaTableDO tbMetaTableDO = tbMetaTableMapper.selectById(enterpriseId, metaTableId);
                isDefine = TableTypeUtil.isUserDefinedTable(tbMetaTableDO.getTableProperty(), tbMetaTableDO.getTableType());
            }
            buesinessIds.add(query.getBusinessId());
        }
        if(CollectionUtils.isNotEmpty(query.getMetaTableIds())){
            TbMetaTableDO tbMetaTableDO = tbMetaTableMapper.selectById(enterpriseId, query.getMetaTableIds().get(0));
            if(tbMetaTableDO != null){
                isDefine = TableTypeUtil.isUserDefinedTable(tbMetaTableDO.getTableProperty(), tbMetaTableDO.getTableType());
            }
        }
        // 查询巡店检查表记录
        if (query.getBusinessId() == null && query.getBeginDate() == null && query.getEndDate() == null
                && query.getSignInBeginDate() == null && query.getSignInEndDate() == null
                && query.getSignOutBeginDate() == null && query.getSignOutEndDate() == null) {
            Date endTime = new Date(System.currentTimeMillis());
            Calendar now = Calendar.getInstance();
            now.setTime(endTime);
            now.set(Calendar.DATE, now.get(Calendar.DATE) - 30);
            query.setBeginDate(now.getTime());
            query.setEndDate(endTime);
        }
        if(isDefine == null){
            redisUtilPool.delKey(lockKey);
            throw new ServiceException("请先选择检查表或者巡店记录");
        }
        query.setIsDefine(isDefine);
        Long totalNum = 0L;
        if(query.getRegionId() != null){
            String regionPath = regionService.getRegionPath(enterpriseId, String.valueOf(query.getRegionId()));
            query.setRegionPath(regionPath);
        }
        if(!isDefine){
            totalNum = tbDataStaTableColumnMapper.getVOListByRecordIdListCount(enterpriseId, buesinessIds, query);
        }else {
            totalNum = tbDataDefTableColumnMapper.getListByRecordIdListCount(enterpriseId, buesinessIds, query);
        }
        if(totalNum == null || totalNum == 0){
            redisUtilPool.delKey(lockKey);
            throw new ServiceException("当前无记录可导出");
        }
        if(totalNum > Constants.MAX_EXPORT_SIZE * 2){
            redisUtilPool.delKey(lockKey);
            throw new ServiceException("导出数据不能超过" + Constants.MAX_EXPORT_SIZE * 2 +"条，请缩小导出范围");
        }
        String fileName = ExportTemplateEnum.getByCode(ImportTaskConstant.EXPORT_BASE_DETAIL_RECORD);
        ImportTaskDO importTaskDO = importTaskService.insertExportTask(enterpriseId, fileName, ImportTaskConstant.EXPORT_BASE_DETAIL_RECORD);
        MsgUniteData msgUniteData = new MsgUniteData();
        ExportDefTableRequest msg = new ExportDefTableRequest();
        msg.setEnterpriseId(enterpriseId);
        msg.setRequest(query);
        msg.setTotalNum(totalNum);
        msg.setDbName(query.getDbName());
        msg.setImportTaskDO(importTaskDO);
        msgUniteData.setData(JSONObject.toJSONString(msg));
        msgUniteData.setMsgType(MsgUniteDataTypeEnum.EXPORT_BASE_DETAIL_RECORD_EXPORT.getCode());
        //分开异步导出
        simpleMessageService.send(JSONObject.toJSONString(msgUniteData), RocketMqTagEnum.EXPORT_IMPORT_MESSAGE);
        return importTaskDO;
    }

    @Override
    public ResponseResult potralStoreSummarySave(String enterpriseId, PatrolRecordRequest query) {
        try{
            //查询巡店记录
            TbPatrolStoreRecordDO tbPatrolStoreRecordDO = tbPatrolStoreRecordMapper.selectById(enterpriseId, Long.valueOf(query.getBusinessId()));
            if (tbPatrolStoreRecordDO!=null&&tbPatrolStoreRecordDO.getSignInStatus()==0){
                return ResponseResult.fail(ErrorCodeEnum.FAIL.getCode(),"签到之后才能完成提交");
            }
            if (tbPatrolStoreRecordDO!=null&&tbPatrolStoreRecordDO.getStatus() != 0) {
                throw new ServiceException(ErrorCodeEnum.DATA_ERROR.getCode(), "该巡店已处理，无法再次提交");
            }
            //上传视频处理，从缓存获取转码后的url
            summaryVideoHandel(query,enterpriseId);
            //保存数据
            tbPatrolStoreRecordMapper.potralStoreSummarySave(enterpriseId, query);
            //判断submitStatus的状态,如果SubmitStatus不为null，表示提交，如果为null，表示保存
            if (query.getSaveOrSubmit()!=0){
                tbPatrolStoreRecordMapper.updateSubmitStatus(enterpriseId, Long.valueOf(query.getBusinessId()), Constants.SUBMITSTATUS_TWO | tbPatrolStoreRecordDO.getSubmitStatus());
            }
        }catch (Exception e){
            log.error("potralStoreSummarySave 总结保存失败", e);
            return ResponseResult.fail(ErrorCodeEnum.FAIL.getCode(),"总结保存失败");
        }
        return ResponseResult.success(true);
    }

    /**
     * 如果状态为转码完成，直接修改，否则从redis获取转码的视频信息
     * @author chenyupeng
     * @date 2021/10/14
     * @param request
     * @param enterpriseId
     * @return void
     */
    public void summaryVideoHandel(PatrolRecordRequest request,String enterpriseId){

        if(StringUtils.isBlank(request.getSummaryVideo())){
            return;
        }
            SmallVideoInfoDTO smallVideoInfo = JSONObject.parseObject(request.getSummaryVideo(), SmallVideoInfoDTO.class);
            if(smallVideoInfo != null && CollectionUtils.isNotEmpty(smallVideoInfo.getVideoList())){
                String callbackCache;
                SmallVideoDTO smallVideoCache;
                SmallVideoParam smallVideoParam;
                for (SmallVideoDTO smallVideo : smallVideoInfo.getVideoList()) {
                    //如果转码完成就不处理，直接修改
                    if(smallVideo.getStatus() != null && smallVideo.getStatus() >= ResourceStatusEnum.TRANSCODE_FINISH.getValue()){
                        continue;
                    }
                    callbackCache = redisUtil.getString(RedisConstant.VIDEO_CALLBACK_CACHE + smallVideo.getVideoId());
                    if(StringUtils.isNotBlank(callbackCache)){
                        smallVideoCache = JSONObject.parseObject(callbackCache,SmallVideoDTO.class);
                        if(smallVideoCache !=null && smallVideoCache.getStatus() !=null && smallVideoCache.getStatus() >=3){
                            BeanUtils.copyProperties(smallVideoCache,smallVideo);
                        }else {
                            smallVideoParam = new SmallVideoParam();
                            setNotCompleteCache(smallVideoParam,smallVideo,request.getBusinessId(),enterpriseId);
                        }
                    }else {
                        smallVideoParam = new SmallVideoParam();
                        setNotCompleteCache(smallVideoParam,smallVideo,request.getBusinessId(),enterpriseId);
                    }
                }
            }
        if(StringUtils.isNotBlank(request.getSummaryVideo())){
            request.setSummaryVideo(JSONObject.toJSONString(smallVideoInfo));
        }

    }

    /**
     * 如果前端提交的时候，视频还没有转码成功，会把videoId存入缓存，回调的时候再进行处理
     * @author chenyupeng
     * @date 2021/10/14
     * @param smallVideoParam
     * @param smallVideo
     * @param businessId
     * @param enterpriseId
     * @return void
     */
    public void setNotCompleteCache(SmallVideoParam smallVideoParam,SmallVideoDTO smallVideo,String businessId,String enterpriseId){
        smallVideoParam.setVideoId(smallVideo.getVideoId());
        smallVideoParam.setUploadType(UploadTypeEnum.TB_PATROL_STORE_RECORD.getValue());
        smallVideoParam.setBusinessId(Long.valueOf(businessId));
        smallVideoParam.setUploadTime(new Date());
        smallVideoParam.setEnterpriseId(enterpriseId);
        //存入未转码完成的map，vod回调的时候使用
        redisUtil.hashSet(RedisConstant.VIDEO_NOT_COMPLETE_CACHE,smallVideo.getVideoId(),JSONObject.toJSONString(smallVideoParam));
    }

    @Override
    public ResponseResult patrolStoreSignatureSave(String enterpriseId, PatrolRecordRequest query) {
        try{
            //查询巡店记录
            TbPatrolStoreRecordDO tbPatrolStoreRecordDO = tbPatrolStoreRecordMapper.selectById(enterpriseId, Long.valueOf(query.getBusinessId()));
            if (tbPatrolStoreRecordDO!=null&&tbPatrolStoreRecordDO.getSignInStatus()==0){
                return ResponseResult.fail(ErrorCodeEnum.FAIL.getCode(),"签到之后才能完成提交");
            }
            if (tbPatrolStoreRecordDO!=null&&tbPatrolStoreRecordDO.getStatus() != 0) {
                throw new ServiceException(ErrorCodeEnum.DATA_ERROR.getCode(), "该巡店已处理，无法再次提交");
            }
            tbPatrolStoreRecordMapper.patrolStoreSignatureSave(enterpriseId,query);
            if (query.getSaveOrSubmit()!=0){
                tbPatrolStoreRecordMapper.updateSubmitStatus(enterpriseId, Long.valueOf(query.getBusinessId()), Constants.SUBMITSTATUS_FOUR | tbPatrolStoreRecordDO.getSubmitStatus());
            }
        }catch (Exception e){
            log.info("patrolStoreSignatureSave 签名图片保存异常");
            return ResponseResult.fail(ErrorCodeEnum.FAIL.getCode(),"签名图片保存异常");
        }
        return ResponseResult.success(true);
    }

    /**
     * 补偿机制，创建巡店任务
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void makeUpPatrolMetaRecord(String enterpriseId, Long subTaskId, String patrolType, EnterpriseStoreCheckSettingDO settingDO, String storeId) {
        // 根据子任务获取信息
        TaskMessageDTO taskMessageDTO = unifyTaskService.getMessageBySubTaskId(enterpriseId, subTaskId);
        if (taskMessageDTO == null) {
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(),
                    "该子任务无对应的巡店任务，subTaskId:" + subTaskId);
        }
        taskMessageDTO.setStoreCheckSetting(settingDO);
        // 校验
        String taskType = taskMessageDTO.getTaskType();
        if (!patrolType.equals(taskType) && !TaskTypeEnum.PATROL_STORE_PLAN.getCode().equals(taskType)) {
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(),
                    "该子任务无对应巡店类型的巡店任务，subTaskId:" + subTaskId + ",patrolType:" + patrolType);
        }
        String data = taskMessageDTO.getData();
        List<TaskSubDO> taskSubDOList = JSON.parseArray(data, TaskSubDO.class);
        if (CollectionUtils.isEmpty(taskSubDOList)) {
            return;
        }
        taskSubDOList.forEach(taskSubDO -> {
            if(TaskTypeEnum.PATROL_STORE_PLAN.getCode().equals(taskType) && StringUtils.isNotBlank(storeId)){
                taskSubDO.setStoreId(storeId);
            }
            patrolStoreService.addPatrolStoreTask(taskMessageDTO, taskSubDO);
        });

    }

    private List<List<Object>> data(List<SingleTableColumnsRecordsDTO> result) {
        List<List<Object>> list = new ArrayList<List<Object>>();
        for(SingleTableColumnsRecordsDTO entity:result){
            List<Object> data = new ArrayList<Object>();
            //检查表名
            data.add(entity.getTableName());
            //检查表类型
            data.add(entity.getTableType());
            //所属区域
            data.add(entity.getAreaName());
            //所属门店
            data.add(entity.getStoreName());
            //检查人
            data.add(entity.getPatroller());
            //检查时间
            data.add(entity.getPatrolTime());
            if("标准检查表".equals(entity.getTableType())){
                for(int i=0;i<entity.getStaMetaColumnList().size();i++){
                    if(i == 0 || i%2 == 0){
                        data.add(entity.getStaMetaColumnList().get(i).getColumnName());
                    }else{
                        String checkResult = entity.getStaDataColumnList().get(i).getCheckResult();
                        if("PASS".equals(checkResult)){
                            data.add("通过");
                        }
                        if("FAIL".equals(checkResult)){
                            data.add("未通过");
                        }
                        if("INAPPLICABLE".equals(checkResult)){
                            data.add("不适用");
                        }
                        data.add(entity.getStaDataColumnList().get(i).getCheckResult());
                    }
                }
            }
            if("自定义检查表".equals(entity.getTableType())){
                for(int i=0;i<entity.getDefMetaColumnList().size();i++){
                    if(i % 2 == 0){
                        data.add(entity.getDefMetaColumnList().get(i).getColumnName());
                    }else{
                        data.add(entity.getDefDataColumnList().get(i).getValue1());
                    }
                }
            }
            list.add(data);
        }
        return list;
    }

    //比较两个时间的大小
    public boolean compareDate(Date nowBegin,Date nowEnd) {
        if (nowBegin.getTime() < nowEnd.getTime()) {
            return true;
        } else {
            return false;
        }
    }
    /** 获取当前时间（返回格式：yyyyMMddHHmmss） */
    public static String getTime(Date date) {
        if(date == null){
            return "";
        }
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // new Date()为获取当前系统时间
        String time = df.format(date);
        return time;

    }
    public List<TbDataStaTableColumnDO> sortDataStaList(List<TbDataStaTableColumnDO> list){
        if(list == null){
            return new ArrayList<>();
        }
        Collections.sort(list, (o1, o2) -> o1.getMetaColumnId().longValue()<o2.getMetaColumnId().longValue()?1:-1);
        return list;
    }
    public List<TbDataDefTableColumnDO> sortDataDefList(List<TbDataDefTableColumnDO> list){
        if(list == null){
            return new ArrayList<>();
        }
        Collections.sort(list, (o1, o2) -> o1.getMetaColumnId().longValue()<o2.getMetaColumnId().longValue()?1:-1);
        return list;
    }
    public List<TbMetaDefTableColumnDO> sortMetaDefList(List<TbMetaDefTableColumnDO> list){
        if(list == null){
            return new ArrayList<>();
        }
        Collections.sort(list, (o1, o2) -> o1.getId().longValue()<o2.getId().longValue()?1:-1);
        return list;
    }
    public List<TbMetaStaTableColumnDO> sortMetaStaList(List<TbMetaStaTableColumnDO> list){
        if(list == null){
            return new ArrayList<>();
        }
        Collections.sort(list, (o1, o2) -> o1.getId().longValue()<o2.getId().longValue()?1:-1);
        return list;
    }
    private List<List<String>> head(List<SingleTableColumnsRecordsDTO> list) {
        int length = 0;
        for(SingleTableColumnsRecordsDTO entity:list){
            int a = entity.getStaMetaColumnList().size();
            int b = entity.getDefMetaColumnList().size();
            int c = a>b?a:b;
            length = length>c?length:c;
        }
        List<List<String>> result = new ArrayList<List<String>>();
        List<String> head0 = new ArrayList<String>();
        head0.add("所属检查表");
        List<String> head1 = new ArrayList<String>();
        head1.add("检查表类型");
        List<String> head2 = new ArrayList<String>();
        head2.add("所属区域");
        List<String> head3 = new ArrayList<String>();
        head3.add("所属门店");
        List<String> head4 = new ArrayList<String>();
        head4.add("检查人");
        List<String> head5 = new ArrayList<String>();
        head5.add("检查时间");
        result.add(head0);
        result.add(head1);
        result.add(head2);
        result.add(head3);
        result.add(head4);
        result.add(head5);
        for(int i=0;i<=length;i++){
            List<String> head = new ArrayList<String>();
            if(i % 2 == 0){
                head.add("检查项");
            }
            else{
                head.add("结果");
            }
            result.add(head);
        }
        return result;
    }

    @Override
    public PageInfo<PatrolStoreCheckVO> getPatrolStoreCheckList(String enterpriseId, PatrolStoreCheckQuery query) throws IOException {
        //检查输入是否为空
        if(CollectionUtils.isEmpty(query.getRegionIdList())  && CollectionUtils.isEmpty(query.getStoreIdList())){
            SelectComptRegionStoreVO regionAndStore = selectionComponentService.getRegionAndStore(enterpriseId, null, UserHolder.getUser().getUserId(), null);
            //如果管辖为空返回空数组
            if (CollectionUtils.isEmpty(regionAndStore.getAllRegionList())){
                return new PageInfo(Lists.newArrayList());
            }
            query.setRegionIdList(regionAndStore.getAllRegionList().stream().map(SelectComponentRegionVO::getId).collect(Collectors.toList()));
        }

        if(query.getRegionId() != null){
            String regionPath = regionService.getRegionPath(enterpriseId, String.valueOf(query.getRegionId()));
            query.setRegionPath(regionPath);
        }
        if(CollectionUtils.isNotEmpty(query.getRegionIdList())){
            List<RegionPathDTO> regionPathDTOList = regionService.getRegionPathByList(enterpriseId, query.getRegionIdList());
            if(CollectionUtils.isNotEmpty(regionPathDTOList)){
                List<String> regionPathList =  regionPathDTOList.stream().map(RegionPathDTO::getRegionPath).collect(Collectors.toList());
                query.setRegionPathList(regionPathList);
            }
        }

        PageHelper.startPage(query.getPageNum()==null?1:query.getPageNum(), query.getPageSize()==null?10:query.getPageSize());
       List<PatrolStoreCheckVO> vos = tbPatrolStoreCheckMapper.getPatrolStoreCheckList(enterpriseId,query);
       if(CollectionUtils.isEmpty(vos)){
            return new PageInfo(new ArrayList<>());
       }
        //所属区域
        List<StorePathDTO> storePathDTOList = ListUtils.emptyIfNull(vos)
                .stream()
                .map(data -> {
                    StorePathDTO storePathDTO = new StorePathDTO();
                    storePathDTO.setStoreId(data.getStoreId());
                    storePathDTO.setRegionPath(data.getRegionWay());
                    return storePathDTO;
                }).collect(Collectors.toList());
       Map<String, List<String>> fullRegionNameMap = regionService.getFullRegionNameList(enterpriseId, storePathDTOList);

       Set<Long> regionIdSet = new HashSet<>();
       Set<Long> businessIdSet = new HashSet<>();

       Map<Long, Set<Long>> metaTableIdMap = new HashMap<>();
       for (PatrolStoreCheckVO vo : vos) {
           regionIdSet.add(vo.getRegionId());
           businessIdSet.add(vo.getBusinessId());

           if (StringUtils.isNotEmpty(vo.getMetaTableIds())) {
                String[] metaTableIds = vo.getMetaTableIds().split(",");
                Set<Long> metaTableIdsSet = Arrays.stream(metaTableIds).filter(StringUtils::isNotBlank).map(Long::parseLong).collect(Collectors.toSet());
                metaTableIdMap.put(vo.getId(), metaTableIdsSet);
           }
       }


        List<RegionDO> regionList = regionMapper.getByIds(enterpriseId, new ArrayList<>(regionIdSet));
        Map<Long, RegionDO> regionIdNameMap = regionList.stream().filter(a -> a.getId() != null && a.getName() != null)
                .collect(Collectors.toMap(RegionDO::getId, data -> data, (a, b) -> a));
        //巡店检查表
        List<TbDataTableDO> dataTableList = tbDataTableMapper.getTableListByBusinessId(enterpriseId, new ArrayList<>(businessIdSet));
        Map<String,TbDataTableDO> tbDataTableDOMap = dataTableList.stream().collect(Collectors.toMap(data->data.getBusinessId()+"_"+data.getMetaTableId(), data -> data, (a, b) -> a));
        //巡店不合格原因
        Map<Long, List<TbDataStaTableColumnDO>> tableColumnMap = Maps.newHashMap();
        if(CollectionUtils.isNotEmpty(dataTableList)) {
            Set<Long> dataTableIdSet = dataTableList.stream().map(TbDataTableDO::getId).collect(Collectors.toSet());
            List<Long> dataTableIdList = new ArrayList<>(dataTableIdSet);
            List<TbDataStaTableColumnDO> tableColumnDOList = tbDataStaTableColumnMapper.selectDataColumn(enterpriseId,dataTableIdList);

            if(CollectionUtils.isNotEmpty(tableColumnDOList)) {
                tableColumnMap =  tableColumnDOList.stream()
                        .collect(Collectors.groupingBy(TbDataStaTableColumnDO::getDataTableId));
            }
        }
        //大区稽核表
        List<PatrolCheckDataTableDO> bigRegionCheckDataList = tbPatrolCheckDataTableMapper.getTableListByBusinessId(enterpriseId, new ArrayList<>(businessIdSet));
        Map<String,PatrolCheckDataTableDO> bigRegionCheckDataTableMap = bigRegionCheckDataList.stream().collect(Collectors.toMap(data->data.getBusinessId()+"_"+data.getMetaTableId(), data -> data, (a, b) -> a));
        //不合格原因
        Map<Long, List<CheckDataStaColumnDO>> bigRegionCheckTableColumnMap = Maps.newHashMap();
        if(CollectionUtils.isNotEmpty(bigRegionCheckDataList)) {
            Set<Long> dataTableIdSet = bigRegionCheckDataList.stream().map(PatrolCheckDataTableDO::getDataTableId).collect(Collectors.toSet());
            List<Long> dataTableIdList = new ArrayList<>(dataTableIdSet);
            List<CheckDataStaColumnDO> tableColumnDOList = tbCheckDataStaColumnMapper.selectDataColumn(enterpriseId,dataTableIdList);

            if(CollectionUtils.isNotEmpty(tableColumnDOList)) {
                bigRegionCheckTableColumnMap =  tableColumnDOList.stream()
                        .collect(Collectors.groupingBy(CheckDataStaColumnDO::getDataTableId));
            }
        }

        //战区稽核表
        List<PatrolCheckDataTableDO> warCheckDataList = tbPatrolCheckDataTableMapper.getWarTableListByBusinessId(enterpriseId, new ArrayList<>(businessIdSet));
        Map<String,PatrolCheckDataTableDO> warCheckDataTableMap = warCheckDataList.stream().collect(Collectors.toMap(data->data.getBusinessId()+"_"+data.getMetaTableId(), data -> data, (a, b) -> a));
        //不合格原因
        Map<Long, List<CheckDataStaColumnDO>> warCheckTableColumnMap = Maps.newHashMap();
        if(CollectionUtils.isNotEmpty(warCheckDataList)) {
            Set<Long> dataTableIdSet = warCheckDataList.stream().map(PatrolCheckDataTableDO::getDataTableId).collect(Collectors.toSet());
            List<Long> dataTableIdList = new ArrayList<>(dataTableIdSet);
            List<CheckDataStaColumnDO> tableColumnDOList = tbCheckDataStaColumnMapper.selectWarDataColumn(enterpriseId,dataTableIdList);

            if(CollectionUtils.isNotEmpty(tableColumnDOList)) {
                warCheckTableColumnMap =  tableColumnDOList.stream()
                        .collect(Collectors.groupingBy(CheckDataStaColumnDO::getDataTableId));
            }
        }

        //组装数据
        for (PatrolStoreCheckVO vo : vos) {
            if (CollectionUtils.isEmpty(vo.getMetaTableVOS())) {
                vo.setMetaTableVOS(new ArrayList<>());
            }
            List<MetaTableVO> metaTableVOS = new ArrayList<>();
            //获取区域名称
            RegionDO regionDO = regionIdNameMap.get(vo.getRegionId());//区域
            if(regionDO != null){
                vo.setRegionName(regionDO.getName());
            }
            //所属区域
            List<String> names = fullRegionNameMap.get(vo.getStoreId());
            if(CollectionUtils.isNotEmpty(names)){
                vo.setFullRegionName(StringUtils.join(names, Constants.SPLIT_LINE));
                vo.setRegionNameList(names);
            }

            Set<Long> metaIds = metaTableIdMap.get(vo.getId());
            if (CollectionUtils.isNotEmpty(metaIds)) {
                for (Long metaTableId : metaIds) {
                    String key = vo.getBusinessId()+ "_"+metaTableId;
                    MetaTableVO metaTableVO = new MetaTableVO();
                    metaTableVO.setMtaTableId(metaTableId);
                    TbDataTableDO dataTableDO = tbDataTableDOMap.get(key);//巡店检查表
                    if(dataTableDO != null){
                        metaTableVO.setCheckNum(dataTableDO.getTotalCalColumnNum());//检查项
                        metaTableVO.setTableName(dataTableDO.getTableName());  //检查表名称
                        metaTableVO.setPassNum(dataTableDO.getPassNum());  //合格项数
                        metaTableVO.setFailNum(dataTableDO.getFailNum());//不合格项数
                        metaTableVO.setTotalScore(dataTableDO.getTaskCalTotalScore());//总分值
                        metaTableVO.setCheckScore(dataTableDO.getCheckScore());//巡店得分
                        metaTableVO.setCheckResultLevel(dataTableDO.getCheckResultLevel());//巡店结果
                        //巡店不合格原因汇总
                        List<TbDataStaTableColumnDO> tableColumnDOList = tableColumnMap.get(dataTableDO.getId());
                        if(CollectionUtils.isNotEmpty(tableColumnDOList)){
                            List<String> reasons = tableColumnDOList.stream().filter(o -> o.getCheckResult() != null && "FAIL".equals(o.getCheckResult())).map(o -> o.getCheckResultReason()).filter(StringUtils::isNotBlank).collect(Collectors.toList());
                            log.info("Filtered reasons: {}", reasons);
                            ObjectMapper objectMapper = new ObjectMapper();
                            if(CollectionUtils.isNotEmpty(reasons)){
                                List<TbMetaColumnReasonDO> reasonList = new ArrayList<>();
                                for (String reason : reasons){
                                    List<TbMetaColumnReasonDO> reasonLists = objectMapper.readValue(reason, new TypeReference<List<TbMetaColumnReasonDO>>(){});
                                    reasonList.addAll(reasonLists);
                                }
                                    // 处理 reasonLists 逻辑
                                    List<String> allReasonNames = reasonList.stream().map(TbMetaColumnReasonDO::getReasonName).collect(Collectors.toList());
                                    // 将 List<String> 转换为以“、”分隔的字符串
                                    log.info("巡店不合格原因 reasonNames:{}",allReasonNames);
                                    String result = String.join("、", allReasonNames);
                                    metaTableVO.setCheckResultReason(result);

                            }
                        }

                    }
                    //大区稽核表
                    PatrolCheckDataTableDO bigRegionPatrolCheckDataTableDO = bigRegionCheckDataTableMap.get(key);
                    if(bigRegionPatrolCheckDataTableDO != null) {
                        //大区稽核得分
                        metaTableVO.setBigRegionCheckScore(bigRegionPatrolCheckDataTableDO.getCheckScore());
                        //大区稽核结果
                        metaTableVO.setBigRegionCheckResultLevel(bigRegionPatrolCheckDataTableDO.getCheckResultLevel());
                        List<CheckDataStaColumnDO> checkDataStaColumnDOS = bigRegionCheckTableColumnMap.get(bigRegionPatrolCheckDataTableDO.getDataTableId());
                        if(CollectionUtils.isNotEmpty(checkDataStaColumnDOS)){
                            List<String> reason =checkDataStaColumnDOS .stream().filter(o -> o.getCheckResult() != null && "FAIL".equals(o.getCheckResult())) .map(o -> o.getCheckText()).filter(StringUtils::isNotBlank).collect(Collectors.toList());
                            if (CollectionUtils.isNotEmpty(reason)) {
                                //大区稽核不合格原因
                                String result = String.join("、", reason);
                                metaTableVO.setBigRegionCheckResultReason(result);
                            }
                        }

                    }
                    //战区稽核表
                    PatrolCheckDataTableDO warPatrolCheckDataTableDO = warCheckDataTableMap.get(key);
                    if (warPatrolCheckDataTableDO != null){
                        //战区稽核得分
                        metaTableVO.setWarCheckScore(warPatrolCheckDataTableDO.getCheckScore());
                        //战区稽核结果
                        metaTableVO.setWarResultLevel(warPatrolCheckDataTableDO.getCheckResultLevel());
                        //战区稽核不合格原因
                        List<CheckDataStaColumnDO> checkDataStaColumnDOS = warCheckTableColumnMap.get(warPatrolCheckDataTableDO.getDataTableId());
                        if(CollectionUtils.isNotEmpty(checkDataStaColumnDOS)){
                            List<String> reason = checkDataStaColumnDOS .stream().filter(o -> o.getCheckResult() != null && "FAIL".equals(o.getCheckResult())).map(o -> o.getCheckText()).filter(StringUtils::isNotBlank).collect(Collectors.toList());
                            if(CollectionUtils.isNotEmpty(reason)){
                                //战区稽核不合格原因
                                String result = String.join("、", reason);
                                metaTableVO.setWarResultReason(result);
                            }
                        }

                    }

                    metaTableVOS.add(metaTableVO);
                }

            }
          vo.getMetaTableVOS().addAll(metaTableVOS);
        }
        return new PageInfo<>(vos);
    }

    @Override
    public Boolean setCheckUser(String enterpriseId, SetCheckUserQuery query) {

        EnterpriseStoreCheckSettingDO enterpriseStoreCheckSetting = enterpriseStoreCheckSettingMapper.getEnterpriseStoreCheckSetting(enterpriseId);
        enterpriseStoreCheckSetting.setBigRegionCheckUser(query.getBigRegionUserIds().getPatrolRecheck());
        enterpriseStoreCheckSetting.setWarZoneCheckUser(query.getWarZoneUserIds().getPatrolRecheck());
        enterpriseStoreCheckSetting.setExtendField(query.getExtendField());
        enterpriseStoreCheckSettingMapper.insertOrUpdate(enterpriseId,enterpriseStoreCheckSetting);

        return true;
    }

    @Override
    public GetCheckUserVO getCheckUser(String enterpriseId) {
        EnterpriseStoreCheckSettingDO enterpriseStoreCheckSetting = enterpriseStoreCheckSettingMapper.getEnterpriseStoreCheckSetting(enterpriseId);
       if(enterpriseStoreCheckSetting != null){
           GetCheckUserVO query = new GetCheckUserVO();
           query.setBigRegionUsers(enterpriseStoreCheckSetting.getBigRegionCheckUser());
           query.setWarZoneUsers(enterpriseStoreCheckSetting.getWarZoneCheckUser());
           query.setExtendField(enterpriseStoreCheckSetting.getExtendField());
           return query;
       }
        return null;
    }

    @Override
    public PageInfo<DataStaTableColumnVO> getCheckDetailList(String enterpriseId, PatrolStoreCheckQuery query) throws IOException {
        //检查输入是否为空
        if(CollectionUtils.isEmpty(query.getRegionIdList())  && CollectionUtils.isEmpty(query.getStoreIdList())){
            SelectComptRegionStoreVO regionAndStore = selectionComponentService.getRegionAndStore(enterpriseId, null, UserHolder.getUser().getUserId(), null);
            //如果管辖为空返回空数组
            if (CollectionUtils.isEmpty(regionAndStore.getAllRegionList())){
                return new PageInfo(Lists.newArrayList());
            }
            query.setRegionIdList(regionAndStore.getAllRegionList().stream().map(SelectComponentRegionVO::getId).collect(Collectors.toList()));
        }

        if(query.getRegionId() != null){
            String regionPath = regionService.getRegionPath(enterpriseId, String.valueOf(query.getRegionId()));
            query.setRegionPath(regionPath);
        }
        if(CollectionUtils.isNotEmpty(query.getRegionIdList())){
            List<RegionPathDTO> regionPathDTOList = regionService.getRegionPathByList(enterpriseId, query.getRegionIdList());
            if(CollectionUtils.isNotEmpty(regionPathDTOList)){
                List<String> regionPathList =  regionPathDTOList.stream().map(RegionPathDTO::getRegionPath).collect(Collectors.toList());
                query.setRegionPathList(regionPathList);
            }
        }
//        List<PatrolStoreCheckVO> vos = tbPatrolStoreCheckMapper.getPatrolStoreCheckList(enterpriseId,query);
        PageHelper.startPage(query.getPageNum()==null?1:query.getPageNum(), query.getPageSize()==null?10:query.getPageSize());
        List<PatrolStoreCheckVO> vos = tbPatrolStoreCheckMapper.getPatrolStoreCheckListById(enterpriseId,query);
        if(CollectionUtils.isEmpty(vos)){
            return new PageInfo(Lists.newArrayList());
        }
        List<Long> ids = null;
        if(CollectionUtils.isNotEmpty(vos)){
//            businessIds = vos.stream().map(PatrolStoreCheckVO::getBusinessId).collect(Collectors.toList());
            ids = vos.stream().map(PatrolStoreCheckVO::getId).collect(Collectors.toList());
        }
//        if(CollectionUtils.isEmpty(businessIds)){
//            return new PageInfo(Lists.newArrayList());
//        }
        PageInfo pageInfo = new PageInfo<>(vos);
        List<DataStaTableColumnVO> list =  tbDataStaTableColumnMapper.getCheckDetailList(enterpriseId,ids);
        List<Long> businessIds = null;
        if(CollectionUtils.isNotEmpty(list)){
            businessIds = list.stream().map(DataStaTableColumnVO::getBusinessId).collect(Collectors.toList());
        }
        if(CollectionUtils.isEmpty(list)){
            return new PageInfo(Lists.newArrayList());
        }

        Set<Long> columnIdSet = new HashSet<>();
        Set<Long> regionIdSet = new HashSet<>();
        Set<Long> dataStaColumnIdSet = new HashSet<>();
        for(DataStaTableColumnVO vo : list){
            columnIdSet.add(vo.getMetaColumnId());
            regionIdSet.add(vo.getRegionId());
            dataStaColumnIdSet.add(vo.getId());
        }


        //标准检查表
        List<TbMetaStaTableColumnDO> dos = tbMetaStaTableColumnMapper.getMetaStaTableColumnList(enterpriseId,new ArrayList<>(columnIdSet));
        Map<Long,TbMetaStaTableColumnDO> columnMap = dos.stream().collect(Collectors.toMap(TbMetaStaTableColumnDO::getId, Function.identity()));

        //任务表
        List<PatrolStoreCheckDO> patrolStoreCheckDOS = tbPatrolStoreCheckMapper.getPatrolStoreCheckListByBusinessId(enterpriseId,businessIds);
        Map<Long,PatrolStoreCheckDO> patrolStoreCheckDOMap = patrolStoreCheckDOS.stream().collect(Collectors.toMap(PatrolStoreCheckDO::getBusinessId, Function.identity()));
        //区域
        List<RegionDO> regionList = regionMapper.getByIds(enterpriseId, new ArrayList<>(regionIdSet));
        Map<Long, RegionDO> regionIdNameMap = regionList.stream().filter(a -> a.getId() != null && a.getName() != null)
                .collect(Collectors.toMap(RegionDO::getId, data -> data, (a, b) -> a));
        //所属区域
        List<StorePathDTO> storePathDTOList = ListUtils.emptyIfNull(patrolStoreCheckDOS)
                .stream()
                .map(data -> {
                    StorePathDTO storePathDTO = new StorePathDTO();
                    storePathDTO.setStoreId(data.getStoreId());
                    storePathDTO.setRegionPath(data.getRegionWay());
                    return storePathDTO;
                }).collect(Collectors.toList());
        Map<String, List<String>> fullRegionNameMap = regionService.getFullRegionNameList(enterpriseId, storePathDTOList);



        //巡店检查表
        List<TbDataTableDO> dataTableList = tbDataTableMapper.getTableListByBusinessId(enterpriseId, businessIds);
        Map<Long,TbDataTableDO> tbDataTableDOMap = dataTableList.stream().collect(Collectors.toMap(TbDataTableDO::getId, data -> data, (a, b) -> a));

        //大区稽核图片
        List<CheckDataStaColumnDO> checkDataStaColumnDOList = tbCheckDataStaColumnMapper.checkDataStaColumnDOList(enterpriseId, new ArrayList<>(dataStaColumnIdSet));
        Map<Long, CheckDataStaColumnDO> checkDataStaColumnDOMap = checkDataStaColumnDOList.stream().collect(Collectors.toMap(CheckDataStaColumnDO::getDataStaColumnId, data -> data, (a, b) -> a));



        //战区稽核图片
        List<CheckDataStaColumnDO> warCheckDataStaColumnDOList = tbCheckDataStaColumnMapper.warCheckDataStaColumnDOList(enterpriseId, new ArrayList<>(dataStaColumnIdSet));
        Map<Long, CheckDataStaColumnDO> warCheckDataStaColumnDOMap = warCheckDataStaColumnDOList.stream().collect(Collectors.toMap(CheckDataStaColumnDO::getDataStaColumnId, data -> data, (a, b) -> a));

        //组装数据
        for (DataStaTableColumnVO vo : list) {
            RegionDO regionDO = regionIdNameMap.get(vo.getRegionId());//区域
            List<String> names = fullRegionNameMap.get(vo.getStoreId());
            PatrolStoreCheckDO patrolStoreCheckDO =  patrolStoreCheckDOMap.get(vo.getBusinessId());//任务表
            TbMetaStaTableColumnDO tbMetaStaTableColumnDO = columnMap.get(vo.getMetaColumnId());//标准检查表
            TbDataTableDO dataTableDO = tbDataTableDOMap.get(vo.getDataTableId());//巡店检查表
            CheckDataStaColumnDO bigRegionCheckDataStaColumnDO = checkDataStaColumnDOMap.get(vo.getId());//大区稽核数据项
            CheckDataStaColumnDO warCheckDataStaColumnDO = warCheckDataStaColumnDOMap.get(vo.getId());//战区稽核数据项
            if (regionDO != null) {
                vo.setRegionName(regionDO.getName());
            }
            if(CollectionUtils.isNotEmpty(names)){
                vo.setFullRegionName(StringUtils.join(names, Constants.SPLIT_LINE));
                vo.setRegionNameList(names);
            }
            if(tbMetaStaTableColumnDO !=null){
                vo.setStandardPic(tbMetaStaTableColumnDO.getStandardPic());
            }
            //巡店检查项不合格原因
            if(vo.getCheckResult() != null && "FAIL".equals(vo.getCheckResult())){
                if(StringUtils.isNotBlank(vo.getCheckResultReason())){
                    String reason = vo.getCheckResultReason();
                    ObjectMapper objectMapper = new ObjectMapper();
                    List<TbMetaColumnReasonDO> reasonLists = objectMapper.readValue(reason, new TypeReference<List<TbMetaColumnReasonDO>>(){});
                    List<String> allReasonNames = reasonLists.stream().map(TbMetaColumnReasonDO::getReasonName).collect(Collectors.toList());
                    String result = String.join("、", allReasonNames);
                    vo.setCheckResultReasons(result);
                }
            }
            if(vo.getCheckResult() != null){
                vo.setCheckResultName(vo.getCheckResult().equals("PASS")?"合格":
                        vo.getCheckResult().equals("FAIL")?"不合格":
                         vo.getCheckResult().equals("INAPPLICABLE")?"不适用":"");
            }

            if(dataTableDO !=null) {
                vo.setPassNum(dataTableDO.getPassNum());
            }

            if(patrolStoreCheckDO !=null){
                vo.setPatrolType(patrolStoreCheckDO.getPatrolType());//任务类型
                vo.setPatrolTypeName(patrolStoreCheckDO.getPatrolType().equals("PATROL_STORE_OFFLINE")?"线下巡店":"线上巡店");
                vo.setStoreNum(patrolStoreCheckDO.getStoreNum());//门店编号
                vo.setSupervisorName(patrolStoreCheckDO.getSupervisorName());//巡店人姓名
                vo.setSupervisorJobNum(patrolStoreCheckDO.getSupervisorJobNum());//巡店人工号
                vo.setTaskName(patrolStoreCheckDO.getTaskName());//任务名称
                vo.setBigRegionUserId(patrolStoreCheckDO.getBigRegionUserId());//大区稽核人id
                vo.setBigRegionUserName(patrolStoreCheckDO.getBigRegionUserName());//大区稽核人姓名
                vo.setBigRegionUserJobNum(patrolStoreCheckDO.getBigRegionUserJobNum());//大区稽核人工号
                vo.setBigRegionCheckTime(patrolStoreCheckDO.getBigRegionCheckTime());//大区稽核时间
                vo.setWarZoneUserId(patrolStoreCheckDO.getWarZoneUserId());//战区稽核人id
                vo.setWarZoneUserName(patrolStoreCheckDO.getWarZoneUserName());//战区稽核人姓名
                vo.setWarZoneUserJobNum(patrolStoreCheckDO.getWarZoneUserJobNum());//战区稽核人工号
                vo.setWarZoneCheckTime(patrolStoreCheckDO.getWarZoneCheckTime());//战区稽核时间


            }
            if(bigRegionCheckDataStaColumnDO!=null) {
                //大区稽核结果图
                vo.setBigRegionCheckPics(bigRegionCheckDataStaColumnDO.getCheckPics());
                //大区检查项稽核结果
                vo.setBigRegionCheckResultLevel(bigRegionCheckDataStaColumnDO.getCheckResult());
                if(bigRegionCheckDataStaColumnDO.getCheckResult() != null){
                    vo.setBigRegionCheckResultName(bigRegionCheckDataStaColumnDO.getCheckResult().equals("PASS")?"合格":
                            bigRegionCheckDataStaColumnDO.getCheckResult().equals("FAIL")?"不合格":
                            bigRegionCheckDataStaColumnDO.getCheckResult().equals("INAPPLICABLE")?"不适用":"");
                }
                //大区检查项不合格原因
                if(bigRegionCheckDataStaColumnDO.getCheckResult() != null && "FAIL".equals(bigRegionCheckDataStaColumnDO.getCheckResult())){
                    vo.setBigRegionCheckResultReason(bigRegionCheckDataStaColumnDO.getCheckText());
                }
                //大区检查项稽核得分
                vo.setBigRegionCheckScore(bigRegionCheckDataStaColumnDO.getCheckScore());

            }
            if(warCheckDataStaColumnDO!=null) {
                //战区稽核结果图
                vo.setWarCheckPics(warCheckDataStaColumnDO.getCheckPics());
                //战区检查项稽核结果
                vo.setWarResultLevel(warCheckDataStaColumnDO.getCheckResult());
                if(warCheckDataStaColumnDO.getCheckResult() != null){
                    vo.setWarResultName(warCheckDataStaColumnDO.getCheckResult().equals("PASS")?"合格":
                            warCheckDataStaColumnDO.getCheckResult().equals("FAIL")?"不合格":
                            warCheckDataStaColumnDO.getCheckResult().equals("INAPPLICABLE")?"不适用":"");
                }
                //战区检查项不合格原因
                if(warCheckDataStaColumnDO.getCheckResult() != null && "FAIL".equals(warCheckDataStaColumnDO.getCheckResult())){
                    vo.setWarResultReason(warCheckDataStaColumnDO.getCheckText());
                }
                //战区稽核得分
                vo.setWarCheckScore(warCheckDataStaColumnDO.getCheckScore());
            }

        }
        pageInfo.setList(list);
        return pageInfo;
    }

    @Override
    public PageInfo<CheckAnalyzeVO> getCheckAnalyzeList(String enterpriseId, PatrolStoreCheckQuery query) {
        //检查输入是否为空
        if(CollectionUtils.isEmpty(query.getRegionIdList())  && CollectionUtils.isEmpty(query.getStoreIdList())){
            SelectComptRegionStoreVO regionAndStore = selectionComponentService.getRegionAndStore(enterpriseId, null, UserHolder.getUser().getUserId(), null);
            //如果管辖为空返回空数组
            if (CollectionUtils.isEmpty(regionAndStore.getAllRegionList())){
                return new PageInfo(Lists.newArrayList());
            }
            query.setRegionIdList(regionAndStore.getAllRegionList().stream().map(SelectComponentRegionVO::getId).collect(Collectors.toList()));
        }

        if(query.getRegionId() != null){
            String regionPath = regionService.getRegionPath(enterpriseId, String.valueOf(query.getRegionId()));
            query.setRegionPath(regionPath);
        }
        if(CollectionUtils.isNotEmpty(query.getRegionIdList())){
            List<RegionPathDTO> regionPathDTOList = regionService.getRegionPathByList(enterpriseId, query.getRegionIdList());
            if(CollectionUtils.isNotEmpty(regionPathDTOList)){
                List<String> regionPathList =  regionPathDTOList.stream().map(RegionPathDTO::getRegionPath).collect(Collectors.toList());
                query.setRegionPathList(regionPathList);
            }
        }

        PageHelper.startPage(query.getPageNum()==null?1:query.getPageNum(), query.getPageSize()==null?10:query.getPageSize());
        List<CheckAnalyzeVO> list = tbPatrolStoreCheckMapper.getCheckAnalyzeList(enterpriseId,query);
        if(CollectionUtils.isEmpty(list)){
            return new PageInfo(Lists.newArrayList());
        }
        List<String> supervisorIdSet = list.stream().map(CheckAnalyzeVO::getSupervisorId).collect(Collectors.toList());
        //所属区域
        List<StorePathDTO> storePathDTOList = ListUtils.emptyIfNull(list)
                .stream()
                .map(data -> {
                    StorePathDTO storePathDTO = new StorePathDTO();
                    storePathDTO.setStoreId(data.getStoreId());
                    storePathDTO.setRegionPath(data.getRegionWay());
                    return storePathDTO;
                }).collect(Collectors.toList());
        Map<String, List<String>> fullRegionNameMap = regionService.getFullRegionNameList(enterpriseId, storePathDTOList);

        List<PatrolStoreCheckDO> dos = tbPatrolStoreCheckMapper.selectListBySupervisorId(enterpriseId, supervisorIdSet);
        Map<String, List<PatrolStoreCheckDO>> supervisorIdMap = dos.stream().collect(Collectors.groupingBy(PatrolStoreCheckDO::getSupervisorId));
        Set<Long> metaTableIds = new HashSet<>();
        Map<String , Set<Long>> userMap = new HashMap<>();
        for (CheckAnalyzeVO vo : list) {
            List<PatrolStoreCheckDO> checkDOS = supervisorIdMap.get(vo.getSupervisorId());
            Set<Long> metaTables = new HashSet<>();
            if(CollectionUtils.isNotEmpty(checkDOS)){
                for (PatrolStoreCheckDO checkDO : checkDOS) {
                    if(checkDO.getMetaTableIds() != null){
                        String[] ids = checkDO.getMetaTableIds().split(",");
                        Set<Long> metaTableIdsSet = Arrays.stream(ids).filter(StringUtils::isNotBlank).map(Long::parseLong).collect(Collectors.toSet());
                        metaTables.addAll(metaTableIdsSet);
                        metaTableIds.addAll(metaTableIdsSet);
                    }
                }
            }
            userMap.put(vo.getSupervisorId(), metaTables);
        }
        //查表名
        TbMetaTableDO tableDO =null;
        if(query.getMetaTableId()!=null){
            tableDO = tbMetaTableMapper.selectListByTableId(enterpriseId, query.getMetaTableId());
        }

        //组装数据
        for (CheckAnalyzeVO vo : list) {
            List<String> strings = fullRegionNameMap.get(vo.getStoreId());
            if(CollectionUtils.isNotEmpty(strings) ){
                if(strings.size() > 3){
                    List<String> topThreeStrings = strings.subList(0, 3);
                    vo.setRegionName1(topThreeStrings.get(0));
                    vo.setRegionName2(topThreeStrings.get(1));
                    vo.setRegionName3(topThreeStrings.get(2));
                    vo.setFullRegionName(StringUtils.join(topThreeStrings, Constants.SPLIT_LINE));
                    vo.setRegionNameList(topThreeStrings);
                }else {
                    vo.setRegionName1(strings.size() > 0 ? strings.get(0) : "");
                    vo.setRegionName2(strings.size() > 1 ? strings.get(1) : "");
                    vo.setRegionName3(strings.size() > 2 ? strings.get(2) : "");
                    vo.setFullRegionName(StringUtils.join(strings, Constants.SPLIT_LINE));
                    vo.setRegionNameList(strings);
                }
            }

            vo.setBigRegionCheckRate(BigDecimal.valueOf(vo.getBigRegionCheckNum()).divide(BigDecimal.valueOf(vo.getCheckTotalNum()), 2, RoundingMode.HALF_UP));
            vo.setWarCheckRate(BigDecimal.valueOf(vo.getWarCheckNum()).divide(BigDecimal.valueOf(vo.getCheckTotalNum()), 2, RoundingMode.HALF_UP));

            if(tableDO!=null){
                vo.setTableName(tableDO.getTableName());
            }else{
                vo.setTableName("/");
            }
        }
        return new PageInfo<>(list);
    }

    @Override
    public ImportTaskDO ExportCheckList(String enterpriseId, PatrolStoreCheckQuery query) throws IOException {
        query.setEnterpriseId(enterpriseId);
        PageInfo<ExportPatrolStoreCheckVO> pageInfo = exportPatrolStoreCheckList(query);
        Long totalNum = pageInfo.getTotal();
        if(totalNum == null || totalNum == 0){
            throw new ServiceException("当前无记录可导出");
        }
        if(totalNum > Constants.MAX_EXPORT_SIZE){
            throw new ServiceException("导出数据不能超过" + Constants.MAX_EXPORT_SIZE +"条，请缩小导出范围");
        }
        String fileName = "";
        ImportTaskDO importTaskDO = null;
        if(query.getType()!=null && query.getType()==0){
            fileName = ExportTemplateEnum.getByCode(ImportTaskConstant.EXPORT_CHECK_LIST);
            importTaskDO = importTaskService.insertExportTask(enterpriseId, fileName, ImportTaskConstant.EXPORT_CHECK_LIST);
        }else if(query.getType()==null && query.getCheckType()==1 ){
            if(query.getBigRegionCheckStatus()!=null){
                if(query.getBigRegionCheckStatus()==0){
                    //大区未稽核
                    fileName = ExportTemplateEnum.getByCode(ImportTaskConstant.EXPORT_BIG_REGION_NOT_CHECK_LIST);
                    importTaskDO = importTaskService.insertExportTask(enterpriseId, fileName, ImportTaskConstant.EXPORT_BIG_REGION_NOT_CHECK_LIST);

                }else {
                    //大区已稽核
                    fileName = ExportTemplateEnum.getByCode(ImportTaskConstant.EXPORT_BIG_REGION_PASS_CHECK_LIST);
                    importTaskDO = importTaskService.insertExportTask(enterpriseId, fileName, ImportTaskConstant.EXPORT_BIG_REGION_PASS_CHECK_LIST);

                }
            }

        }else if(query.getType()==null && query.getCheckType()==2){
            if(query.getWarZoneCheckStatus()!=null){
                if(query.getWarZoneCheckStatus()==0) {
                    //战区未稽核
                    fileName = ExportTemplateEnum.getByCode(ImportTaskConstant.EXPORT_WAR_NOT_CHECK_LIST);
                    importTaskDO = importTaskService.insertExportTask(enterpriseId, fileName, ImportTaskConstant.EXPORT_WAR_NOT_CHECK_LIST);

                }else {
                    //战区已稽核
                    fileName = ExportTemplateEnum.getByCode(ImportTaskConstant.EXPORT_WAR_PASS_CHECK_LIST);
                    importTaskDO = importTaskService.insertExportTask(enterpriseId, fileName, ImportTaskConstant.EXPORT_WAR_PASS_CHECK_LIST);

                }
            }
        }

        MsgUniteData msgUniteData = new MsgUniteData();

        query.setEnterpriseId(enterpriseId);

        query.setTotalNum(totalNum);
        query.setImportTaskDO(importTaskDO);
        msgUniteData.setData(JSONObject.toJSONString(query));
        msgUniteData.setMsgType(MsgUniteDataTypeEnum.EXPORT_CHECK_LIST.getCode());
        //分开异步导出
        simpleMessageService.send(JSONObject.toJSONString(msgUniteData), RocketMqTagEnum.EXPORT_IMPORT_MESSAGE);
        return importTaskDO;
    }

    @Override
    public ImportTaskDO ExportCheckDetailList(String enterpriseId, PatrolStoreCheckQuery query) throws IOException {
        PageInfo<DataStaTableColumnVO> checkDetailList = getCheckDetailList(enterpriseId, query);
        Long totalNum = checkDetailList.getTotal();
        if(totalNum == null || totalNum == 0){
            throw new ServiceException("当前无记录可导出");
        }
        if(totalNum > Constants.MAX_EXPORT_SIZE){
            throw new ServiceException("导出数据不能超过" + Constants.MAX_EXPORT_SIZE +"条，请缩小导出范围");
        }
        String  fileName = ExportTemplateEnum.getByCode(ImportTaskConstant.EXPORT_CHECK_DETAIL_LIST);
        ImportTaskDO  importTaskDO = importTaskService.insertExportTask(enterpriseId, fileName, ImportTaskConstant.EXPORT_CHECK_DETAIL_LIST);

        MsgUniteData msgUniteData = new MsgUniteData();
        query.setEnterpriseId(enterpriseId);
        query.setTotalNum(totalNum);
        query.setImportTaskDO(importTaskDO);
        msgUniteData.setData(JSONObject.toJSONString(query));
        msgUniteData.setMsgType(MsgUniteDataTypeEnum.EXPORT_CHECK_DETAIL_LIST.getCode());
        //分开异步导出
        simpleMessageService.send(JSONObject.toJSONString(msgUniteData), RocketMqTagEnum.EXPORT_IMPORT_MESSAGE);
        return importTaskDO;

    }

    @Override
    public ImportTaskDO ExportCheckAnalyzeList(String enterpriseId, PatrolStoreCheckQuery query) {
        PageInfo<CheckAnalyzeVO> checkAnalyzeList = getCheckAnalyzeList(enterpriseId, query);
        Long totalNum = checkAnalyzeList.getTotal();
        if(totalNum == null || totalNum == 0){
            throw new ServiceException("当前无记录可导出");
        }
        if(totalNum > Constants.MAX_EXPORT_SIZE){
            throw new ServiceException("导出数据不能超过" + Constants.MAX_EXPORT_SIZE +"条，请缩小导出范围");
        }
        String  fileName = ExportTemplateEnum.getByCode(ImportTaskConstant.EXPORT_CHECK_ANALYZE_LIST);
        ImportTaskDO  importTaskDO = importTaskService.insertExportTask(enterpriseId, fileName, ImportTaskConstant.EXPORT_CHECK_ANALYZE_LIST);

        MsgUniteData msgUniteData = new MsgUniteData();
        query.setEnterpriseId(enterpriseId);
        query.setTotalNum(totalNum);
        query.setImportTaskDO(importTaskDO);
        msgUniteData.setData(JSONObject.toJSONString(query));
        msgUniteData.setMsgType(MsgUniteDataTypeEnum.EXPORT_CHECK_ANALYZE_LIST.getCode());
        //分开异步导出
        simpleMessageService.send(JSONObject.toJSONString(msgUniteData), RocketMqTagEnum.EXPORT_IMPORT_MESSAGE);
        return importTaskDO;

    }

    /**
     * 稽核列表导出
     * @param query
     * @return
     */
    @Override
    public PageInfo<ExportPatrolStoreCheckVO> exportPatrolStoreCheckList(PatrolStoreCheckQuery query) throws IOException {
        String enterpriseId = query.getEnterpriseId();
        //检查输入是否为空
        if(CollectionUtils.isEmpty(query.getRegionIdList())  && CollectionUtils.isEmpty(query.getStoreIdList())){
            SelectComptRegionStoreVO regionAndStore = selectionComponentService.getRegionAndStore(enterpriseId, null, UserHolder.getUser().getUserId(), null);
            //如果管辖为空返回空数组
            if (CollectionUtils.isEmpty(regionAndStore.getAllRegionList())){
                return new PageInfo(Lists.newArrayList());
            }
            query.setRegionIdList(regionAndStore.getAllRegionList().stream().map(SelectComponentRegionVO::getId).collect(Collectors.toList()));
        }

        if(query.getRegionId() != null){
            String regionPath = regionService.getRegionPath(enterpriseId, String.valueOf(query.getRegionId()));
            query.setRegionPath(regionPath);
        }
        if(CollectionUtils.isNotEmpty(query.getRegionIdList())){
            List<RegionPathDTO> regionPathDTOList = regionService.getRegionPathByList(enterpriseId, query.getRegionIdList());
            if(CollectionUtils.isNotEmpty(regionPathDTOList)){
                List<String> regionPathList =  regionPathDTOList.stream().map(RegionPathDTO::getRegionPath).collect(Collectors.toList());
                query.setRegionPathList(regionPathList);
            }
        }

        List<PatrolStoreCheckVO> vos = tbPatrolStoreCheckMapper.getPatrolStoreCheckList(enterpriseId,query);
        if(CollectionUtils.isEmpty(vos)){
            return new PageInfo(new ArrayList<>());
        }

        List<Long> businessIds = null;
        if(CollectionUtils.isNotEmpty(vos)){
            businessIds = vos.stream().map(PatrolStoreCheckVO::getBusinessId).collect(Collectors.toList());
        }
        if(CollectionUtils.isEmpty(businessIds)){
            return new PageInfo(Lists.newArrayList());
        }
        Set<Long> tableIds = new HashSet<>() ;
        for (PatrolStoreCheckVO vo : vos) {
            if (StringUtils.isNotEmpty(vo.getMetaTableIds())) {
                String[] metaTableIds = vo.getMetaTableIds().split(",");
                Set<Long> metaTableIdsList = Arrays.stream(metaTableIds).filter(StringUtils::isNotBlank).map(Long::parseLong).collect(Collectors.toSet());
                tableIds.addAll(metaTableIdsList);
            }
        }

        PageHelper.startPage(query.getPageNum()==null?1:query.getPageNum(), query.getPageSize()==null?10:query.getPageSize());
        List<TbDataTableDO> dataTableList = tbDataTableMapper.getTableListByBusinessIds(enterpriseId,businessIds,new ArrayList<>(tableIds));
       log.info("dataTableList:{}",dataTableList);
        if(CollectionUtils.isEmpty(dataTableList)){
            return new PageInfo(Lists.newArrayList());
        }
        //任务表
        Map<Long,PatrolStoreCheckVO> patrolStoreCheckDOMap = vos.stream().collect(Collectors.toMap(PatrolStoreCheckVO::getBusinessId, Function.identity()));

        //所属区域
        List<StorePathDTO> storePathDTOList = ListUtils.emptyIfNull(vos)
                .stream()
                .map(data -> {
                    StorePathDTO storePathDTO = new StorePathDTO();
                    storePathDTO.setStoreId(data.getStoreId());
                    storePathDTO.setRegionPath(data.getRegionWay());
                    return storePathDTO;
                }).collect(Collectors.toList());
        Map<String, List<String>> fullRegionNameMap = regionService.getFullRegionNameList(enterpriseId, storePathDTOList);

        //巡店不合格原因汇总
        Map<Long, List<TbDataStaTableColumnDO>> tableColumnMap = Maps.newHashMap();

        Set<Long> dataTableIdSet = dataTableList.stream().map(TbDataTableDO::getId).collect(Collectors.toSet());
        List<Long> dataTableIdList = new ArrayList<>(dataTableIdSet);
        List<TbDataStaTableColumnDO> tableColumnDOList = tbDataStaTableColumnMapper.selectDataColumn(enterpriseId,dataTableIdList);

        if(CollectionUtils.isNotEmpty(tableColumnDOList)) {
            tableColumnMap =  tableColumnDOList.stream()
                    .collect(Collectors.groupingBy(TbDataStaTableColumnDO::getDataTableId));
        }


        //大区稽核表
        List<PatrolCheckDataTableDO> bigRegionCheckDataList = tbPatrolCheckDataTableMapper.getTableListByBusinessId(enterpriseId, businessIds);
        Map<Long,PatrolCheckDataTableDO> bigRegionCheckDataTableMap = bigRegionCheckDataList.stream().collect(Collectors.toMap(PatrolCheckDataTableDO::getDataTableId, data -> data, (a, b) -> a));
        //大区不合格原因
        Map<Long, List<CheckDataStaColumnDO>> bigRegionCheckTableColumnMap = Maps.newHashMap();
        if(CollectionUtils.isNotEmpty(bigRegionCheckDataList)) {
            Set<Long> dataIds = bigRegionCheckDataList.stream().map(PatrolCheckDataTableDO::getDataTableId).collect(Collectors.toSet());
            List<Long> dataTableIds = new ArrayList<>(dataIds);
            List<CheckDataStaColumnDO> tableColumnList = tbCheckDataStaColumnMapper.selectDataColumn(enterpriseId,dataTableIds);
            if(CollectionUtils.isNotEmpty(tableColumnDOList)) {
                bigRegionCheckTableColumnMap =  tableColumnList.stream()
                        .collect(Collectors.groupingBy(CheckDataStaColumnDO::getDataTableId));
            }
        }
        //战区稽核表
        List<PatrolCheckDataTableDO> warCheckDataList = tbPatrolCheckDataTableMapper.getWarTableListByBusinessId(enterpriseId, businessIds);
        Map<Long,PatrolCheckDataTableDO> warCheckDataTableMap = warCheckDataList.stream().collect(Collectors.toMap(PatrolCheckDataTableDO::getDataTableId, data -> data, (a, b) -> a));
        //不合格原因
        Map<Long, List<CheckDataStaColumnDO>> warCheckTableColumnMap = Maps.newHashMap();
        if(CollectionUtils.isNotEmpty(warCheckDataList)) {
            Set<Long> ids = warCheckDataList.stream().map(PatrolCheckDataTableDO::getDataTableId).collect(Collectors.toSet());
            List<Long> dataTableIdsList = new ArrayList<>(ids);
            List<CheckDataStaColumnDO> warTableColumnDOList = tbCheckDataStaColumnMapper.selectWarDataColumn(enterpriseId,dataTableIdsList);

            if(CollectionUtils.isNotEmpty(tableColumnDOList)) {
                warCheckTableColumnMap =  warTableColumnDOList.stream()
                        .collect(Collectors.groupingBy(CheckDataStaColumnDO::getDataTableId));
            }
        }



        //组装数据
        List<ExportPatrolStoreCheckVO> voList = new ArrayList<>();
        PageInfo pageInfo = new PageInfo<>(dataTableList);
        for (TbDataTableDO data : dataTableList) {
            ExportPatrolStoreCheckVO exportPatrolStoreCheckVO = new ExportPatrolStoreCheckVO();
            PatrolStoreCheckVO patrolStoreCheckVO = patrolStoreCheckDOMap.get(data.getBusinessId());//任务表
            List<String> names = fullRegionNameMap.get(data.getStoreId());

            exportPatrolStoreCheckVO.setId(data.getId());
            exportPatrolStoreCheckVO.setTaskId(data.getTaskId());
            exportPatrolStoreCheckVO.setBusinessId(data.getBusinessId());
            exportPatrolStoreCheckVO.setRegionId(data.getRegionId());
            exportPatrolStoreCheckVO.setRegionWay(data.getRegionPath());
            exportPatrolStoreCheckVO.setTableName(data.getTableName());
            exportPatrolStoreCheckVO.setPassNum(data.getPassNum());
            exportPatrolStoreCheckVO.setFailNum(data.getFailNum());
            exportPatrolStoreCheckVO.setCheckNum(data.getTotalCalColumnNum());
            exportPatrolStoreCheckVO.setTotalScore(data.getTaskCalTotalScore());//总分值
            exportPatrolStoreCheckVO.setCheckScore(data.getCheckScore());//巡店得分
            exportPatrolStoreCheckVO.setCheckResultLevel(data.getCheckResultLevel().equals("excellent")?"优秀":
                                                         data.getCheckResultLevel().equals("good")?"良好":
                                                         data.getCheckResultLevel().equals("eligible")?"合格":
                                                         data.getCheckResultLevel().equals("disqualification")?"不合格":"" );//巡店结果

            if(patrolStoreCheckVO != null){
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm");
                exportPatrolStoreCheckVO.setStoreNum(patrolStoreCheckVO.getStoreNum());
                exportPatrolStoreCheckVO.setSupervisorId(patrolStoreCheckVO.getSupervisorId());
                exportPatrolStoreCheckVO.setSupervisorName(patrolStoreCheckVO.getSupervisorName());
                exportPatrolStoreCheckVO.setSupervisorJobNum(patrolStoreCheckVO.getSupervisorJobNum());
                exportPatrolStoreCheckVO.setTaskName(patrolStoreCheckVO.getTaskName());
                exportPatrolStoreCheckVO.setPatrolType(patrolStoreCheckVO.getPatrolType().equals("PATROL_STORE_OFFLINE")?"线下巡店":"线上巡店");
                exportPatrolStoreCheckVO.setStoreId(patrolStoreCheckVO.getStoreId());
                exportPatrolStoreCheckVO.setStoreName(patrolStoreCheckVO.getStoreName());
                if(patrolStoreCheckVO.getSignStartTime() !=null){
                    String signStartTime = sdf.format(patrolStoreCheckVO.getSignStartTime());
                    exportPatrolStoreCheckVO.setSignStartTime(signStartTime);
                }
                if(patrolStoreCheckVO.getSignEndTime() !=null){
                    String signEndTime = sdf.format(patrolStoreCheckVO.getSignEndTime());
                    exportPatrolStoreCheckVO.setSignEndTime(signEndTime);
                }
                String startTime = "";
                String endTime = "";
                if(patrolStoreCheckVO.getSubBeginTime() !=null ){
                     startTime = sdf.format(patrolStoreCheckVO.getSubBeginTime());
                    exportPatrolStoreCheckVO.setSubBeginTime(startTime);
                }
                if(patrolStoreCheckVO.getSubEndTime() !=null ){
                     endTime = sdf.format(patrolStoreCheckVO.getSubEndTime());
                    exportPatrolStoreCheckVO.setSubEndTime(endTime);
                }
                exportPatrolStoreCheckVO.setDataTime(startTime + "-" + endTime);
                exportPatrolStoreCheckVO.setBigRegionCheckStatus(patrolStoreCheckVO.getBigRegionCheckStatus());
                exportPatrolStoreCheckVO.setBigRegionCheckStatusName(patrolStoreCheckVO.getBigRegionCheckStatus()==0?"未稽核":"已稽核");
                exportPatrolStoreCheckVO.setWarZoneCheckStatus(patrolStoreCheckVO.getWarZoneCheckStatus());
                exportPatrolStoreCheckVO.setWarZoneCheckStatusName(patrolStoreCheckVO.getWarZoneCheckStatus()==0?"未稽核":"已稽核");

                String status = (patrolStoreCheckVO.getBigRegionCheckStatus()==0 && patrolStoreCheckVO.getWarZoneCheckStatus()==0)?
                        "全部未稽核":(patrolStoreCheckVO.getBigRegionCheckStatus()==1 && patrolStoreCheckVO.getWarZoneCheckStatus()==1)?
                        "全部已稽核":(patrolStoreCheckVO.getBigRegionCheckStatus()==1 && patrolStoreCheckVO.getWarZoneCheckStatus()==0)?
                        "大区已稽核战区未稽核":"大区未稽核战区已稽核";
                exportPatrolStoreCheckVO.setStatus(status);
                exportPatrolStoreCheckVO.setBigRegionUserId(patrolStoreCheckVO.getBigRegionUserId());
                exportPatrolStoreCheckVO.setBigRegionUserName(patrolStoreCheckVO.getBigRegionUserName());
                exportPatrolStoreCheckVO.setBigRegionUserJobNum(patrolStoreCheckVO.getBigRegionUserJobNum());
                exportPatrolStoreCheckVO.setWarZoneUserId(patrolStoreCheckVO.getWarZoneUserId());
                exportPatrolStoreCheckVO.setWarZoneUserName(patrolStoreCheckVO.getWarZoneUserName());
                exportPatrolStoreCheckVO.setWarZoneUserJobNum(patrolStoreCheckVO.getWarZoneUserJobNum());
                if(patrolStoreCheckVO.getBigRegionCheckTime()!=null){
                    String bigTime = sdf.format(patrolStoreCheckVO.getBigRegionCheckTime());
                    exportPatrolStoreCheckVO.setBigRegionCheckTime(bigTime);
                }
                if(patrolStoreCheckVO.getWarZoneCheckTime()!=null){
                    String warTime = sdf.format(patrolStoreCheckVO.getWarZoneCheckTime());
                    exportPatrolStoreCheckVO.setWarZoneCheckTime(warTime);
                }
            }
            if(CollectionUtils.isNotEmpty(names)){
                if(CollectionUtils.isNotEmpty(names)){
                    exportPatrolStoreCheckVO.setFullRegionName(StringUtils.join(names, Constants.SPLIT_LINE));
                }
            }
            //巡店不合格原因汇总
            List<TbDataStaTableColumnDO> dos = tableColumnMap.get(data.getId());
            log.info("巡店检查表id ：",data.getId());
            if(CollectionUtils.isNotEmpty(dos)){
                List<String> reasons = dos.stream().filter(o -> o.getCheckResult() != null && "FAIL".equals(o.getCheckResult())).map(o -> o.getCheckResultReason()).filter(StringUtils::isNotBlank).collect(Collectors.toList());
                log.info("Filtered reasons: {}", reasons);
                ObjectMapper objectMapper = new ObjectMapper();
                if(CollectionUtils.isNotEmpty(reasons)){
                    List<TbMetaColumnReasonDO> reasonList = new ArrayList<>();
                    for (String reason : reasons){
                        List<TbMetaColumnReasonDO> reasonLists = objectMapper.readValue(reason, new TypeReference<List<TbMetaColumnReasonDO>>(){});
                        reasonList.addAll(reasonLists);
                    }
                    // 处理 reasonLists 逻辑
                    List<String> allReasonNames = reasonList.stream().map(TbMetaColumnReasonDO::getReasonName).collect(Collectors.toList());
                    // 将 List<String> 转换为以“、”分隔的字符串
                    log.info("巡店不合格原因 reasonNames:{}",allReasonNames);
                    String   result = String.join("、", allReasonNames);
                    exportPatrolStoreCheckVO.setCheckResultReason(result);
                }

            }

            //大区稽核表
            PatrolCheckDataTableDO bigRegionPatrolCheckDataTableDO = bigRegionCheckDataTableMap.get(data.getId());
            if(bigRegionPatrolCheckDataTableDO != null) {
                //大区稽核得分
                exportPatrolStoreCheckVO.setBigRegionCheckScore(bigRegionPatrolCheckDataTableDO.getCheckScore());
                //大区稽核结果
                exportPatrolStoreCheckVO.setBigRegionCheckResultLevel(bigRegionPatrolCheckDataTableDO.getCheckResultLevel().equals("excellent")?"优秀":
                                                bigRegionPatrolCheckDataTableDO.getCheckResultLevel().equals("good")?"良好":
                                                bigRegionPatrolCheckDataTableDO.getCheckResultLevel().equals("eligible")?"合格":
                                                bigRegionPatrolCheckDataTableDO.getCheckResultLevel().equals("disqualification")?"不合格":"" );

                List<CheckDataStaColumnDO> dos1 = bigRegionCheckTableColumnMap.get(bigRegionPatrolCheckDataTableDO.getDataTableId());
                if(CollectionUtils.isNotEmpty(dos1)){
                    List<String> reason = dos1.stream().filter(o -> o.getCheckResult() != null && "FAIL".equals(o.getCheckResult())).map(o -> o.getCheckText()).filter(StringUtils::isNotBlank).collect(Collectors.toList());
                    if (CollectionUtils.isNotEmpty(reason)) {
                        //大区稽核不合格原因
                        String result = String.join("、", reason);
                        exportPatrolStoreCheckVO.setBigRegionCheckResultReason(result);
                    }
                }

            }
            //战区稽核表
            PatrolCheckDataTableDO warPatrolCheckDataTableDO = warCheckDataTableMap.get(data.getId());
            if (warPatrolCheckDataTableDO != null){
                //战区稽核得分
                exportPatrolStoreCheckVO.setWarCheckScore(warPatrolCheckDataTableDO.getCheckScore());
                //战区稽核结果
                exportPatrolStoreCheckVO.setWarResultLevel(warPatrolCheckDataTableDO.getCheckResultLevel().equals("excellent")?"优秀":
                                               warPatrolCheckDataTableDO.getCheckResultLevel().equals("good")?"良好":
                                               warPatrolCheckDataTableDO.getCheckResultLevel().equals("eligible")?"合格":
                                               warPatrolCheckDataTableDO.getCheckResultLevel().equals("disqualification")?"不合格":"" );
                //战区稽核不合格原因
                List<CheckDataStaColumnDO> dos2 = warCheckTableColumnMap.get(warPatrolCheckDataTableDO.getDataTableId());
                if(CollectionUtils.isNotEmpty(dos2)){
                    List<String> reason =  dos2.stream().filter(o -> o.getCheckResult() != null && "FAIL".equals(o.getCheckResult())).map(o -> o.getCheckText()).filter(StringUtils::isNotBlank).collect(Collectors.toList());
                    if(CollectionUtils.isNotEmpty(reason)){
                        //大区稽核不合格原因
                        String result = String.join("、", reason);
                        exportPatrolStoreCheckVO.setWarResultReason(result);
                    }
                }

            }

            voList.add(exportPatrolStoreCheckVO);
        }
        voList.sort((o1, o2) -> o2.getSignStartTime().compareTo(o1.getSignStartTime()));
        pageInfo.setList(voList);
        return pageInfo;
    }

    @Override
    public PatrolStoreCheckRecordVO taskRecordInfo(String enterpriseId, Long businessId,  Integer checkType) {

        PatrolStoreCheckDO patrolStoreCheckDO = tbPatrolStoreCheckMapper.selectByBusinessId(businessId,enterpriseId);
        if (patrolStoreCheckDO == null || patrolStoreCheckDO.getDeleted()) {
            throw new ServiceException("记录不存在");
        }

        TbPatrolStoreRecordDO  storeRecordDO = tbPatrolStoreRecordMapper.selectById(enterpriseId, businessId);
        if(storeRecordDO==null){
            return null;
        }
        PatrolStoreCheckRecordVO tbPatrolStoreRecordVO = new PatrolStoreCheckRecordVO();
        BeanUtils.copyProperties(storeRecordDO, tbPatrolStoreRecordVO);
        tbPatrolStoreRecordVO.setBigRegionCheckStatus(patrolStoreCheckDO.getBigRegionCheckStatus());
        tbPatrolStoreRecordVO.setBigRegionCheckTime(patrolStoreCheckDO.getBigRegionCheckTime());
        tbPatrolStoreRecordVO.setBigRegionUserId(patrolStoreCheckDO.getBigRegionUserId());
        tbPatrolStoreRecordVO.setBigRegionUserJobNum(patrolStoreCheckDO.getBigRegionUserJobNum());
        tbPatrolStoreRecordVO.setBigRegionUserName(patrolStoreCheckDO.getBigRegionUserName());
        tbPatrolStoreRecordVO.setWarZoneCheckStatus(patrolStoreCheckDO.getWarZoneCheckStatus());
        tbPatrolStoreRecordVO.setWarZoneCheckTime(patrolStoreCheckDO.getWarZoneCheckTime());
        tbPatrolStoreRecordVO.setWarZoneUserId(patrolStoreCheckDO.getWarZoneUserId());
        tbPatrolStoreRecordVO.setWarZoneUserJobNum(patrolStoreCheckDO.getWarZoneUserJobNum());
        tbPatrolStoreRecordVO.setWarZoneUserName(patrolStoreCheckDO.getWarZoneUserName());

        TaskParentDO taskParentDO = taskParentMapper.selectParentTaskById(enterpriseId, storeRecordDO.getTaskId());
        if (taskParentDO != null) {
            tbPatrolStoreRecordVO.setRunRule(taskParentDO.getRunRule());
            tbPatrolStoreRecordVO.setTaskCycle(taskParentDO.getTaskCycle() != null ? taskParentDO.getTaskCycle() : Constants.ONCE);
            tbPatrolStoreRecordVO.setTaskDesc(taskParentDO.getTaskDesc());
        }

        EnterpriseUserDO userDO = enterpriseUserDao.selectByUserId(enterpriseId, storeRecordDO.getCreateUserId());
        if (userDO != null) {
            tbPatrolStoreRecordVO.setCreateUserName(userDO.getName());
        }
        if (Constants.SYSTEM_USER_ID.equals(storeRecordDO.getCreateUserId())) {
            tbPatrolStoreRecordVO.setCreateUserName(Constants.SYSTEM_USER_NAME);
        }
        List<PatrolCheckDataTableDO> dataTableList = tbPatrolCheckDataTableMapper.getTableInfo(enterpriseId,businessId,checkType);

        TbMetaTableDO metaTableDO = null;
        if(CollectionUtils.isNotEmpty(dataTableList)){
            metaTableDO = tbMetaTableMapper.selectById(enterpriseId, dataTableList.get(0).getMetaTableId());
        }
        if (metaTableDO != null) {
            tbPatrolStoreRecordVO.setMetaTableName(metaTableDO.getTableName());
            if(metaTableDO != null && !TableTypeUtil.isUserDefinedTable(metaTableDO.getTableProperty(), metaTableDO.getTableType())){
                Integer num = tbDataStaTableColumnMapper.dataStaColumnNumCount(enterpriseId, storeRecordDO.getId());
                tbPatrolStoreRecordVO.setTotalColumnNum(num);
            }
        }
        //多表名称展示
        if(CollectionUtils.isNotEmpty(dataTableList)) {
            List<String> metaTableNameList = dataTableList.stream().map(PatrolCheckDataTableDO::getTableName).collect(Collectors.toList());
            String metaTableNames = StringUtils.join(metaTableNameList, Constants.PAUSE);
            tbPatrolStoreRecordVO.setMetaTableName(metaTableNames);
        }

        tbPatrolStoreRecordVO.setActualPatrolStoreDuration(DateUtils.formatBetween(storeRecordDO.getSignStartTime(),storeRecordDO.getSignEndTime()));
        TbPatrolStoreRecordInfoDO recordInfoDO = tbPatrolStoreRecordInfoMapper.selectTbPatrolStoreRecordInfo(enterpriseId, storeRecordDO.getId());
        if (recordInfoDO != null) {
            tbPatrolStoreRecordVO.setAuditTime(recordInfoDO.getAuditTime());
            tbPatrolStoreRecordVO.setAuditUserId(recordInfoDO.getAuditUserId());
            tbPatrolStoreRecordVO.setAuditUserName(recordInfoDO.getAuditUserName());
        }

        tbPatrolStoreRecordVO.setOverdue(false);
        if(checkType==1){
            if (patrolStoreCheckDO.getBigRegionCheckStatus() == 1 && patrolStoreCheckDO.getSubEndTime() != null
                    && patrolStoreCheckDO.getSignEndTime() != null) {
                tbPatrolStoreRecordVO.setOverdue(patrolStoreCheckDO.getSignEndTime().after(patrolStoreCheckDO.getSubEndTime()));
            } else if (patrolStoreCheckDO.getSubEndTime() != null) {
                tbPatrolStoreRecordVO.setOverdue(new Date().after(patrolStoreCheckDO.getSubEndTime()));
            }
        }else if(checkType==2){
            if (patrolStoreCheckDO.getWarZoneCheckStatus() == 1 && patrolStoreCheckDO.getSubEndTime() != null
                    && patrolStoreCheckDO.getSignEndTime() != null) {
                tbPatrolStoreRecordVO.setOverdue(patrolStoreCheckDO.getSignEndTime().after(patrolStoreCheckDO.getSubEndTime()));
            } else if (patrolStoreCheckDO.getSubEndTime() != null) {
                tbPatrolStoreRecordVO.setOverdue(new Date().after(patrolStoreCheckDO.getSubEndTime()));
            }
        }
        if(checkType==1){
            if (tbPatrolStoreRecordVO.getBigRegionCheckStatus() != 1 && tbPatrolStoreRecordVO.getTaskId() > 0){
                List<UnifyPersonDTO> list = userIdList(enterpriseId, storeRecordDO.getTaskId(),
                        storeRecordDO.getStoreId(), UnifyNodeEnum.FIRST_NODE.getCode(), storeRecordDO.getLoopCount());
                tbPatrolStoreRecordVO.setHanderUserList(list);
            }
        }else if(checkType==2){
            if (tbPatrolStoreRecordVO.getWarZoneCheckStatus() != 1 && tbPatrolStoreRecordVO.getTaskId() > 0){
                List<UnifyPersonDTO> list = userIdList(enterpriseId, storeRecordDO.getTaskId(),
                        storeRecordDO.getStoreId(), UnifyNodeEnum.FIRST_NODE.getCode(), storeRecordDO.getLoopCount());
                tbPatrolStoreRecordVO.setHanderUserList(list);
            }
        }


        //查询
        if (taskParentDO != null) {
            Map<String, List<UnifyPersonDTO>> listMap = getTaskPerson(enterpriseId, Collections.singletonList(storeRecordDO.getTaskId()), storeRecordDO.getStoreId(), storeRecordDO.getLoopCount());
            tbPatrolStoreRecordVO.setCcUserList(listMap.get(UnifyNodeEnum.CC.getCode()));
            tbPatrolStoreRecordVO.setAduitUserList(listMap.get(UnifyNodeEnum.SECOND_NODE.getCode()));
            if (CollectionUtils.isEmpty(tbPatrolStoreRecordVO.getHanderUserList())) {
                tbPatrolStoreRecordVO.setHanderUserList(listMap.get(UnifyNodeEnum.FIRST_NODE.getCode()));
            }
        }
        if(tbPatrolStoreRecordVO.getTaskId() > 0 && taskParentDO != null){
            //流程信息处理
            List<TaskProcessVO> process = JSONArray.parseArray(taskParentDO.getNodeInfo(), TaskProcessVO.class);
            // 节点配置信息组装
            for (TaskProcessVO taskProcessVO : process) {
                taskProcessVO.setTaskId(storeRecordDO.getTaskId());
            }
            Map<Long, TaskProcessVO> taskProcessVOMap = unifyTaskService.dealTaskProcess(enterpriseId, process);
            if(MapUtils.isNotEmpty(taskProcessVOMap)){
                TaskProcessVO taskProcessVO = taskProcessVOMap.get(tbPatrolStoreRecordVO.getTaskId());
                tbPatrolStoreRecordVO.setAssignPeopleRang(taskProcessVO);
            }
        }
        HandlerUserVO handlerUserVO = new HandlerUserVO();
        if(StringUtils.isNotBlank(tbPatrolStoreRecordVO.getSupervisorId())){
            String actualHandleUserId = tbPatrolStoreRecordVO.getSupervisorId();
            EnterpriseUserDO enterpriseUserDO = enterpriseUserDao.selectByUserIdIgnoreActive(enterpriseId, actualHandleUserId);
            if(enterpriseUserDO != null){
                handlerUserVO.setUserId(enterpriseUserDO.getUserId());
                handlerUserVO.setAvatar(enterpriseUserDO.getAvatar());
                handlerUserVO.setUserName(enterpriseUserDO.getName());
                handlerUserVO.setUserMobile(enterpriseUserDO.getMobile());
                handlerUserVO.setJobnumber(enterpriseUserDO.getJobnumber());
                List<SysRoleDO> sysRoleList = sysRoleMapper.getSysRoleByUserId(enterpriseId, enterpriseUserDO.getUserId());
                if (CollectionUtils.isNotEmpty(sysRoleList)){
                    handlerUserVO.setUserRoles(sysRoleList);
                }
            }
            if(Constants.AI.equals(tbPatrolStoreRecordVO.getSupervisorId())){
                handlerUserVO.setUserId(tbPatrolStoreRecordVO.getSupervisorId());
                handlerUserVO.setUserName(Constants.AI);
            }

        }
        tbPatrolStoreRecordVO.setHandlerUserVO(handlerUserVO);
        return tbPatrolStoreRecordVO;
    }
    public List<UnifyPersonDTO> userIdList(String enterpriseId, Long getTaskId,String storeId,String code, Long loopCount){
        List<String> userIdList = taskSubMapper.selectUserIdByLoopCount(enterpriseId, getTaskId,storeId, code, loopCount);
        List<UnifyPersonDTO> list = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(userIdList)) {
            List<EnterpriseUserDO> userList = enterpriseUserDao.selectByUserIds(enterpriseId, userIdList);
            Map<String, UnifyPersonDTO> peopleMap = new HashMap<>();
            for (EnterpriseUserDO enterpriseUserDO : userList) {
                UnifyPersonDTO unifyPersonDTO = new UnifyPersonDTO();
                unifyPersonDTO.setAvatar(enterpriseUserDO.getAvatar());
                unifyPersonDTO.setUserName(enterpriseUserDO.getName());
                unifyPersonDTO.setUserId(enterpriseUserDO.getUserId());
                peopleMap.put(enterpriseUserDO.getUserId(), unifyPersonDTO);
            }
            for (String userId : userIdList) {
                if (peopleMap.containsKey(userId)) {
                    list.add(peopleMap.get(userId));
                }
            }
        }
        return list;
    }
    private Map<String, List<UnifyPersonDTO>> getTaskPerson(String enterpriseId, List<Long> taskIdList, String storeId, Long loopCount) {
        List<UnifyPersonDTO> unifyPersonDTOS = unifyTaskStoreService.selectALLNodeUserInfoList(enterpriseId, taskIdList, Collections.singletonList(storeId), loopCount);
        if (CollectionUtils.isEmpty(unifyPersonDTOS)) {
            return new HashMap<>();
        }

        return unifyPersonDTOS.stream()
                .collect(Collectors.groupingBy(UnifyPersonDTO::getNode));
    }
    @Override
    public List<TableInfoDTO> dataTableInfoList(String enterpriseId, Long businessId, String userId,Integer checkType) {

        PatrolStoreCheckDO patrolStoreCheckDO = tbPatrolStoreCheckMapper.selectByBusinessId(businessId,enterpriseId);
        if (patrolStoreCheckDO == null || patrolStoreCheckDO.getDeleted()) {
            throw new ServiceException("记录不存在");
        }
        TbPatrolStoreRecordDO tbPatrolStoreRecordDO = tbPatrolStoreRecordMapper.selectById(enterpriseId, businessId);
        if (tbPatrolStoreRecordDO==null){
            return new ArrayList<>();
        }
        String businessType = PATROL_STORE;
        if(BusinessCheckType.PATROL_RECHECK.getCode().equals(tbPatrolStoreRecordDO.getBusinessCheckType())){
            businessType = tbPatrolStoreRecordDO.getBusinessCheckType();
        }
        List<PatrolCheckDataTableDO> tbDataTableDOList =
                tbPatrolCheckDataTableMapper.selectByBusinessId(enterpriseId, businessId, checkType);
        if (CollectionUtils.isEmpty(tbDataTableDOList)) {
            return new ArrayList<>();
        }
        Set<Long> tableIds = tbDataTableDOList.stream().map(a -> a.getDataTableId()).collect(Collectors.toSet());
        List<TbDataTableDO> dataTableDOS = tbDataTableMapper.selectListByIdList(enterpriseId, new ArrayList<>(tableIds));
        Map<Long, TbDataTableDO> dataTableDOMap = dataTableDOS.stream().collect(Collectors.toMap(TbDataTableDO::getId, a -> a));
        //稽核检查项
        List<CheckDataStaColumnDO> checkDataStaColumnDOS =tbCheckDataStaColumnMapper.selectDataColumnById(enterpriseId,new ArrayList<>(tableIds),checkType);
        List<CheckDataStaTableColumnVO> columnVOS = checkDataStaColumnDOS.stream().map(a -> {
            CheckDataStaTableColumnVO vo = BeanUtil.toBean(a, CheckDataStaTableColumnVO.class);
            vo.setSubTaskId(tbPatrolStoreRecordDO.getSubTaskId());
            vo.setStoreId(tbPatrolStoreRecordDO.getStoreId());
            vo.setStoreName(tbPatrolStoreRecordDO.getStoreName());
            vo.setRegionId(tbPatrolStoreRecordDO.getRegionId());
            vo.setRegionWay(tbPatrolStoreRecordDO.getRegionWay());
            vo.setCheckResultReason("");
            return vo;
        }).collect(Collectors.toList());

        Map<Long, List<CheckDataStaTableColumnVO>> dataTableIdCheckColumnsMap = columnVOS.stream().collect(Collectors.groupingBy(CheckDataStaTableColumnVO::getDataTableId));



        boolean isAdmin = false;
        if(StringUtils.isNotBlank(userId)){
            isAdmin = sysRoleService.checkIsAdmin(enterpriseId, userId);
        }
        Map<Long, List<TbDataStaTableColumnVO>> dataTableIdStaColumnsMap = new HashMap<>();
        Map<Long, List<MetaStaColumnVO>> metaTableIdStaColumnsMap = new HashMap<>();
        List<TbDataStaTableColumnDO> dataStaColumnList =
                tbDataStaTableColumnMapper.selectByBusinessId(enterpriseId, businessId, businessType);
        //统计分类的合格率/得分率信息
        List<CategoryStatisticsVO> categoryStatisticsVOList =
                tbCheckDataStaColumnMapper.selectCategoryStatisticsListByBusinessId(enterpriseId, businessId, checkType);
        Map<Long, List<CategoryStatisticsVO>> categoryStatisticsVOMap = new HashMap<>();
        if(CollectionUtils.isNotEmpty(categoryStatisticsVOList)){
            categoryStatisticsVOMap =
                    categoryStatisticsVOList.stream().collect(Collectors.groupingBy(CategoryStatisticsVO::getDataTableId));
        }
        Map<Long, TbDataStaTableColumnDO> lastTimeCheckResultMap = new HashMap<>();
        Set<String> hasCategoryNameList = new HashSet<>();

        // 是否有发稽核工单的权限
        Boolean sendProblemAuth = false;
        ScSafetyCheckFlowDO safetyCheckFlowDO = null;
        if (TaskTypeEnum.PATROL_STORE_SAFETY_CHECK.getCode().equals(tbPatrolStoreRecordDO.getPatrolType())) {
            safetyCheckFlowDO = scSafetyCheckFlowService.getByBusinessId(enterpriseId, businessId);
            sendProblemAuth = scSafetyCheckFlowService.checkSendProblemAuth(enterpriseId, businessId, userId);
        }
        if (CollectionUtils.isNotEmpty(dataStaColumnList)) {
            TbDataTableDO tbDataTableDO = dataTableDOS.get(Constants.INDEX_ZERO);
            TbDataTableDO tb = tbDataTableMapper.getLastTimeDataTableDO(enterpriseId, tbDataTableDO.getStoreId(), tbDataTableDO.getMetaTableId());            //查询该门店该表已完成巡店中 最近一次各项的检查结果
            if(tb!=null){
                List<TbDataStaTableColumnDO> lastTimeCheckResulList = tbDataStaTableColumnMapper.selectByBusinessId(enterpriseId,tb.getBusinessId(),PATROL_STORE);
                lastTimeCheckResultMap = lastTimeCheckResulList.stream().collect(Collectors.toMap(TbDataStaTableColumnDO::getMetaColumnId, Function.identity()));
            }
            Map<Long, TbDataColumnCommentAppealVO> commentAppealMap = Maps.newHashMap();
            Map<Long, DataColumnHasHistoryVO> dataColumnHistoryMap = Maps.newHashMap();
            Map<String, String> handlerUserMap = Maps.newHashMap();
            if (TaskTypeEnum.PATROL_STORE_SAFETY_CHECK.getCode().equals(tbPatrolStoreRecordDO.getPatrolType())) {
                List<Long> dataColumnIdList = dataStaColumnList.stream()
                        .map(TbDataStaTableColumnDO::getId).collect(Collectors.toList());
                commentAppealMap = scSafetyCheckFlowService.getLatestCommentAppealInfo(enterpriseId, tbPatrolStoreRecordDO.getId(), dataColumnIdList);
                dataColumnHistoryMap = scSafetyCheckFlowService.checkDataColumnHasHistory(enterpriseId, tbPatrolStoreRecordDO.getId(), dataColumnIdList);
                List<String> handlerUserIdList = dataStaColumnList.stream()
                        .map(TbDataStaTableColumnDO::getHandlerUserId).collect(Collectors.toList());
                handlerUserMap = enterpriseUserDao.getUserNameMap(enterpriseId, handlerUserIdList);
            }
            Map<Long, TbDataStaTableColumnDO> finalLastTimeCheckResultMap = lastTimeCheckResultMap;
            Map<Long, TbDataColumnCommentAppealVO> finalCommentAppealMap = commentAppealMap;
            Map<String, String> finalHandlerUserMap = handlerUserMap;
            Map<Long, DataColumnHasHistoryVO> finalDataColumnHistoryMap = dataColumnHistoryMap;

            Boolean finalSendProblemAuth = sendProblemAuth;
            List<TbDataStaTableColumnVO> tbDataStaTableColumnVOS = dataStaColumnList.stream().map(a -> {
                TbDataStaTableColumnVO tbDataStaTableColumnVO = new TbDataStaTableColumnVO();
                BeanUtils.copyProperties(a, tbDataStaTableColumnVO);
                //异步创建任务延迟，从缓存中取值
                setTaskQuestionId(enterpriseId, tbDataStaTableColumnVO);
                //每个项上次的检查结果，没有结果显示null
                TbDataStaTableColumnDO lastDataStaColumn = finalLastTimeCheckResultMap.get(tbDataStaTableColumnVO.getMetaColumnId());
                if(lastDataStaColumn != null){
                    tbDataStaTableColumnVO.setLastTimeCheckResult(lastDataStaColumn.getCheckResult());
                    tbDataStaTableColumnVO.setLastDataColumnId(lastDataStaColumn.getId());
                }
                Boolean canSendProblem = CheckResultEnum.FAIL.getCode().equals(tbDataStaTableColumnVO.getCheckResult()) &&
                        (tbDataStaTableColumnVO.getTaskQuestionId() == null || tbDataStaTableColumnVO.getTaskQuestionId() == 0);
                if (TaskTypeEnum.PATROL_STORE_SAFETY_CHECK.getCode().equals(tbPatrolStoreRecordDO.getPatrolType())) {
                    TbDataColumnCommentAppealVO commentAppealVO= finalCommentAppealMap.get(a.getId());
                    DataColumnHasHistoryVO dataColumnHasHistoryVO = finalDataColumnHistoryMap.get(a.getId());
                    if(dataColumnHasHistoryVO != null){
                        tbDataStaTableColumnVO.setHasCheckHistory(dataColumnHasHistoryVO.getHasCheckHistory());
                        if(commentAppealVO.getTbDataColumnCommentVO() != null ){
                            commentAppealVO.getTbDataColumnCommentVO().setHasCommentHistory(dataColumnHasHistoryVO.getHasCommentHistory());
                        }
                        if(commentAppealVO.getTbDataColumnAppealVO() != null ){
                            commentAppealVO.getTbDataColumnAppealVO().setHasAppealHistory(dataColumnHasHistoryVO.getHasAppealHistory());
                        }
                    }
                    tbDataStaTableColumnVO.setCommentAppealVO(commentAppealVO);
                    if(StringUtils.isNotBlank(finalHandlerUserMap.get(a.getHandlerUserId()))){
                        tbDataStaTableColumnVO.setHandlerUserName(finalHandlerUserMap.get(a.getHandlerUserId()));
                    }
                    canSendProblem = canSendProblem && finalSendProblemAuth;
                }
                tbDataStaTableColumnVO.setCanSendProblem(canSendProblem);
                return tbDataStaTableColumnVO;
            }).collect(Collectors.toList());


            dataTableIdStaColumnsMap.putAll(
                    tbDataStaTableColumnVOS.stream().collect(Collectors.groupingBy(TbDataStaTableColumnVO::getDataTableId)));
            Set<Long> metaStaColumnIds =
                    dataStaColumnList.stream().map(TbDataStaTableColumnDO::getMetaColumnId).collect(Collectors.toSet());
            List<TbMetaStaTableColumnDO> metaStaColumnList =
                    tbMetaStaTableColumnMapper.selectByIds(enterpriseId, new ArrayList<>(metaStaColumnIds));
            // 设置名字数据
            tbMetaTableService.setNameData(enterpriseId, metaStaColumnList);
            hasCategoryNameList = metaStaColumnList.stream().map(TbMetaStaTableColumnDO::getCategoryName).collect(Collectors.toSet());

            // 结果项
            List<TbMetaColumnResultDO> columnResultDOList =
                    tbMetaColumnResultMapper.selectByColumnIds(enterpriseId, new ArrayList<>(metaStaColumnIds));
            List<TbMetaColumnResultDTO> columnResultDTOList = metaTableService.getMetaColumnResultList(enterpriseId, columnResultDOList);
            Map<Long, List<TbMetaColumnResultDTO>> columnIdResultDOsMap =
                    columnResultDTOList.stream().collect(Collectors.groupingBy(TbMetaColumnResultDTO::getMetaColumnId));

            //不合格原因
            List<TbMetaColumnReasonDTO> columnReasonDTOList = metaColumnReasonDao.getListByColumnIdList(enterpriseId, new ArrayList<>(metaStaColumnIds));
            Map<Long, List<TbMetaColumnReasonDTO>> columnIdReasonMap = new HashMap<>();
            if(CollectionUtils.isNotEmpty(columnReasonDTOList)){
                columnIdReasonMap =
                        columnReasonDTOList.stream().collect(Collectors.groupingBy(TbMetaColumnReasonDTO::getMetaColumnId));
            }

            //不合格原因
            List<TbMetaColumnAppealDTO> columnAppealDTOList = metaColumnAppealDao.getListByColumnIdList(enterpriseId, new ArrayList<>(metaStaColumnIds));
            Map<Long, List<TbMetaColumnAppealDTO>> columnAppealMap = new HashMap<>();
            if(CollectionUtils.isNotEmpty(columnAppealDTOList)){
                columnAppealMap =
                        columnAppealDTOList.stream().collect(Collectors.groupingBy(TbMetaColumnAppealDTO::getMetaColumnId));
            }
            Map<Long, List<TbMetaColumnReasonDTO>> finalColumnIdReasonMap = columnIdReasonMap;
            Map<Long, List<TbMetaColumnAppealDTO>> finalColumnAppealMap = columnAppealMap;
            List<MetaStaColumnVO> metaStaColumnVOList = metaStaColumnList.stream().map(a -> {
                MetaStaColumnVO metaStaColumnVO = new MetaStaColumnVO();
                BeanUtils.copyProperties(a, metaStaColumnVO);
                //如果是采集项
                if (MetaColumnTypeEnum.COLLECT_COLUMN.getCode().equals(metaStaColumnVO.getColumnType())){
                    metaStaColumnVO.setMaxScore(metaStaColumnVO.getSupportScore());
                    metaStaColumnVO.setMinScore(metaStaColumnVO.getLowestScore());
                }
                metaStaColumnVO
                        .setColumnResultList(columnIdResultDOsMap.getOrDefault(a.getId(), new ArrayList<>()));
                // 填充结果项
                metaStaColumnVO.fillColumnResultList();
                metaStaColumnVO.setColumnReasonList(finalColumnIdReasonMap.get(a.getId()));
                metaStaColumnVO.setColumnAppealList(finalColumnAppealMap.get(a.getId()));
                return metaStaColumnVO;
            }).collect(Collectors.toList());
            metaTableIdStaColumnsMap
                    .putAll(metaStaColumnVOList.stream().collect(Collectors.groupingBy(MetaStaColumnVO::getMetaTableId)));
        }
        Map<Long, List<TbDataDefTableColumnDO>> dataTableIdDefColumnsMap = new HashMap<>();
        Map<Long, List<TbMetaDefTableColumnDO>> metaTableIdDefColumnsMap = new HashMap<>();
        List<TbDataDefTableColumnDO> dataDefColumnList =
                tbDataDefTableColumnMapper.selectByBusinessId(enterpriseId, businessId, businessType);
        if (CollectionUtils.isNotEmpty(dataDefColumnList)) {
            dataTableIdDefColumnsMap.putAll(
                    dataDefColumnList.stream().collect(Collectors.groupingBy(TbDataDefTableColumnDO::getDataTableId)));
            Set<Long> metaDefColumnIds =
                    dataDefColumnList.stream().map(TbDataDefTableColumnDO::getMetaColumnId).collect(Collectors.toSet());
            if (CollectionUtils.isNotEmpty(metaDefColumnIds)) {
                List<TbMetaDefTableColumnDO> metaDefColumnList =
                        tbMetaDefTableColumnMapper.selectByIds(enterpriseId, new ArrayList<>(metaDefColumnIds));
                metaTableIdDefColumnsMap.putAll(
                        metaDefColumnList.stream().collect(Collectors.groupingBy(TbMetaDefTableColumnDO::getMetaTableId)));
            }
        }
        // mateTableIds
        Set<Long> metaTableIds = dataTableDOS.stream().map(TbDataTableDO::getMetaTableId).collect(Collectors.toSet());
        // Map:metaTableId->metaTableDO
        List<TbMetaTableDO> tbMetaTableDOS = tbMetaTableMapper.selectByIds(enterpriseId, new ArrayList<>(metaTableIds));
        Set<String> finalHasCategoryNameList = hasCategoryNameList;
        boolean finalIsAdmin = isAdmin;
        List<TbMetaTableUserAuthDO> tableAuth = tbMetaTableUserAuthDAO.getTableAuth(enterpriseId, userId, new ArrayList<>(metaTableIds));
        Map<String, TbMetaTableUserAuthDO> tableAuthMap = ListUtils.emptyIfNull(tableAuth).stream().collect(Collectors.toMap(k -> k.getBusinessId() + Constants.MOSAICS + k.getUserId(), v -> v, (k, v) -> k));
        Map<Long, TbMetaTableInfoVO> idMetaTableMap =
                tbMetaTableDOS.stream().collect(Collectors.toMap(TbMetaTableDO::getId, data ->
                        TbMetaTableInfoVO.builder().id(data.getId()).createTime(data.getCreateTime()).editTime(data.getEditTime())
                                .tableName(data.getTableName()).description(data.getDescription()).createUserId(data.getCreateUserId())
                                .createUserName(data.getCreateUserName()).supportScore(data.getSupportScore()).locked(data.getLocked())
                                .active(data.getActive()).tableType(data.getTableType()).shareGroup(data.getShareGroup()).deleted(data.getDeleted())
                                .editUserId(data.getEditUserId()).editUserName(data.getEditUserName()).shareGroupName(data.getShareGroupName())
                                .resultShareGroup(data.getResultShareGroup()).resultShareGroup(data.getResultShareGroup()).resultShareGroupName(data.getResultShareGroupName())
                                .levelRule(data.getLevelRule()).levelInfo(data.getLevelInfo()).storeSceneId(data.getStoreSceneId()).defaultResultColumn(data.getDefaultResultColumn())
                                .noApplicableRule(data.getNoApplicableRule()).viewResultAuth(getTableViewResultAuth(data.getId(), userId, finalIsAdmin, tableAuthMap))
                                .categoryNameList(CollectionUtils.isEmpty(JSONObject.parseArray(data.getCategoryNameList(), String.class)) ||
                                        CollectionUtils.isEmpty(finalHasCategoryNameList) ? null :
                                        ListUtils.retainAll(JSONObject.parseArray(data.getCategoryNameList(), String.class), new ArrayList<>(finalHasCategoryNameList)))
                                .orderNum(data.getOrderNum()).status(data.getStatus()).totalScore(data.getTotalScore()).tableProperty(data.getTableProperty())
                                .useRange(data.getUseRange())
                                .resultViewRange(data.getResultViewRange())
                                .build(), (a, b) -> a));
        List<PatrolStoreStatisticsDataStaTableCountDTO>  dataStaTableCountList = tbCheckDataStaColumnMapper.statisticsColumnCountByBusinessIdGroupByDataTableId(enterpriseId, Collections.singletonList(businessId));

        Map<Long, PatrolStoreStatisticsDataStaTableCountDTO> dataTableByEveryColumnCountMap = ListUtils.emptyIfNull(dataStaTableCountList).stream().collect(
                Collectors.toMap(PatrolStoreStatisticsDataStaTableCountDTO::getDataTableId, Function.identity(), (a, b) -> a));

        Map<Long, List<CategoryStatisticsVO>> finalCategoryStatisticsVOMap = categoryStatisticsVOMap;
        Boolean finalSendProblemAuth1 = sendProblemAuth;
        ScSafetyCheckFlowDO finalSafetyCheckFlowDO = safetyCheckFlowDO;

        return tbDataTableDOList.stream().map(a -> {
            Long dataTableId = a.getDataTableId();
            Long metaTableId = a.getMetaTableId();
            TbDataTableDO dataTableDO = dataTableDOMap.get(dataTableId);
            TbDataTableInfoVO dataTableInfoVO = new TbDataTableInfoVO();
            BeanUtils.copyProperties(dataTableDO, dataTableInfoVO);
            dataTableInfoVO.setCheckDataTableId(a.getId());
            dataTableInfoVO.setSubmitStatus(a.getSubmitStatus());
            dataTableInfoVO.setTableType(a.getTableType());
            dataTableInfoVO.setTotalScore(a.getTotalScore());
            dataTableInfoVO.setCheckScore(a.getCheckScore());
            dataTableInfoVO.setTaskCalTotalScore(a.getTaskCalTotalScore());
            dataTableInfoVO.setTotalResultAward(a.getTotalResultAward());
            dataTableInfoVO.setNoApplicableRule(a.getNoApplicableRule());
            dataTableInfoVO.setFailNum(a.getFailNum());
            dataTableInfoVO.setPassNum(a.getPassNum());
            dataTableInfoVO.setInapplicableNum(a.getInapplicableNum());
            dataTableInfoVO.setTotalColumnNum(a.getTotalCalColumnNum());
            dataTableInfoVO.setCollectColumnNum(a.getCollectColumnNum());
            dataTableInfoVO.setCheckResultLevel(a.getCheckResultLevel());
            dataTableInfoVO.setSignStartTime(patrolStoreCheckDO.getSignStartTime());
            dataTableInfoVO.setSignEndTime(patrolStoreCheckDO.getSignEndTime());

            TableInfoDTO dataTableInfoDTO =
                    TableInfoDTO.builder().dataTable(dataTableInfoVO).dataStaColumns(dataTableIdCheckColumnsMap.get(dataTableId))
                            .dataDefColumns(dataTableIdDefColumnsMap.get(dataTableId))
                            .metaTable(idMetaTableMap.get(metaTableId)).build();

            Boolean canBatchSendProblem = ListUtils.emptyIfNull(dataTableIdStaColumnsMap.get(dataTableId)).stream().anyMatch(dataColumn -> dataColumn.getCanSendProblem());
            if (TaskTypeEnum.PATROL_STORE_SAFETY_CHECK.getCode().equals(tbPatrolStoreRecordDO.getPatrolType())) {
                canBatchSendProblem = canBatchSendProblem && finalSendProblemAuth1;
            }
            dataTableInfoVO.setCanBatchSendProblem(canBatchSendProblem);
            //是否表单提交
            Integer status = a.getSubmitStatus() & Constants.INDEX_ONE;
            if (Constants.INDEX_ONE.equals(status)
                    || (TaskTypeEnum.PATROL_STORE_SAFETY_CHECK.getCode().equals(tbPatrolStoreRecordDO.getPatrolType()) && finalSafetyCheckFlowDO != null && finalSafetyCheckFlowDO.getCycleCount() > 0)){
                Integer failNum = getNum(a.getFailNum());
                Integer passNum = getNum(a.getPassNum());
                Integer inapplicableNum = getNum(a.getInapplicableNum());
                Integer collectColumnNum = getNum(a.getCollectColumnNum());
                dataTableInfoDTO.setTotalColumn(failNum+passNum+inapplicableNum+collectColumnNum);
                dataTableInfoDTO.setFailNum(failNum);
                dataTableInfoDTO.setPassNum(passNum);
                dataTableInfoDTO.setInapplicableNum(inapplicableNum);
                dataTableInfoDTO.setCollectColumnNum(collectColumnNum);
                dataTableInfoDTO.setTaskCalTotalScore(a.getTaskCalTotalScore());
                dataTableInfoDTO.setTotalCalColumnNum(a.getTotalCalColumnNum());
                dataTableInfoDTO.setScore(a.getCheckScore());
                //总项数
                dataTableInfoVO.setTotalColumnNum(dataTableInfoDTO.getTotalColumn());
                boolean isDefine = TableTypeUtil.isUserDefinedTable(a.getTableProperty(), a.getTableType());
                BigDecimal allColumnCheckScorePercent = new BigDecimal(Constants.ZERO_STR);
                if (!isDefine) {
                    PatrolStoreStatisticsDataStaTableCountDTO pts = dataTableByEveryColumnCountMap.getOrDefault(a.getDataTableId(),new PatrolStoreStatisticsDataStaTableCountDTO());
                    BigDecimal checkScore = pts.getCheckScore();
                    if (MetaTablePropertyEnum.DEDUCT_SCORE_TABLE.getCode().equals(a.getTableProperty())){
                        checkScore = a.getTotalScore().subtract(checkScore);
                    }
                    dataTableInfoDTO.setAllColumnCheckScore(checkScore);
                    if (new BigDecimal(Constants.ZERO_STR).compareTo(checkScore) != 0 && new BigDecimal(Constants.ZERO_STR).compareTo(dataTableInfoDTO.getTaskCalTotalScore()) != 0) {
                        allColumnCheckScorePercent = (checkScore.divide(dataTableInfoDTO.getTaskCalTotalScore(), 4, RoundingMode.HALF_UP)
                                .multiply(new BigDecimal(Constants.ONE_HUNDRED))).setScale(2, RoundingMode.HALF_UP);
                    }
                    dataTableInfoDTO.setAllColumnCheckScorePercent(allColumnCheckScorePercent);
                }
            }
            //分类统计信息
            dataTableInfoDTO.setCategoryStatisticsList(finalCategoryStatisticsVOMap.get(dataTableId));
            // 检查表类型
            Boolean isDefine = TableTypeUtil.isUserDefinedTable(a.getTableProperty(), a.getTableType());
            if (!isDefine) {
                //标准表
                List<MetaStaColumnVO> metaStaColumnVOList = metaTableIdStaColumnsMap.get(metaTableId);
                if (metaStaColumnVOList == null) {
                    log.error("##标准检查项column 不存在，enterpriseId={},businessId={},metaTableId={}", enterpriseId, businessId, metaTableId);
                }
                dataTableInfoDTO.setMetaStaColumns(metaStaColumnVOList);
            } else {
                //自定义表
                List<TbMetaDefTableColumnDO> metaDefColumns = metaTableIdDefColumnsMap.get(metaTableId);
                if (metaDefColumns == null) {
                    log.error("##自定义检查项column 不存在，enterpriseId={},businessId={},metaTableId={}", enterpriseId, businessId, metaTableId);
                } else {
                    //检查项排序
                    metaDefColumns = metaDefColumns.stream().sorted(Comparator.comparing(TbMetaDefTableColumnDO::getOrderNum)).collect(Collectors.toList());
                }
                dataTableInfoDTO.setMetaDefColumns(metaDefColumns);
            }
            return dataTableInfoDTO;
        }).collect(Collectors.toList());
    }
    private void setTaskQuestionId(String enterpriseId , TbDataStaTableColumnVO dataStaTableColumnVO){
        try{
            if(dataStaTableColumnVO.getTaskQuestionId() > 0){
                return;
            }
            String taskQuestionId = redisUtilPool.getString(redisConstantUtil.getQuestionTaskLockKey(enterpriseId, String.valueOf(dataStaTableColumnVO.getId())));
            if(StringUtils.isNotBlank(taskQuestionId)){
                dataStaTableColumnVO.setTaskQuestionId(Long.valueOf(taskQuestionId));
            }
        } catch (Exception exception) {
            log.error("##setTaskQuestionId报错enterpriseId={},dataColumnId={}", enterpriseId, dataStaTableColumnVO.getId());
        }

    }
    private boolean getTableViewResultAuth(Long tableId, String userId, boolean isAdmin, Map<String, TbMetaTableUserAuthDO> tableAuthMap) {
        if(isAdmin){
            return true;
        }
        TbMetaTableUserAuthDO allUser = tableAuthMap.get(tableId + Constants.MOSAICS + "all_user_id");
        TbMetaTableUserAuthDO userAuth = tableAuthMap.get(tableId + Constants.MOSAICS + userId);
        Boolean allUserView = Optional.ofNullable(allUser).map(TbMetaTableUserAuthDO::getViewAuth).orElse(false);
        Boolean userAuthView = Optional.ofNullable(userAuth).map(TbMetaTableUserAuthDO::getViewAuth).orElse(false);
        return userAuthView || allUserView;
    }
    public Integer getNum(Integer num){

        if (null == num){
            return 0;
        }
        return num;
    }
    @Override
    public List<TbPatrolStoreHistoryDo> selectPatrolStoreHistoryList(String enterpriseId,Long businessId) {
        PatrolStoreCheckDO patrolStoreCheckDO = tbPatrolStoreCheckMapper.selectByBusinessId(businessId,enterpriseId);

        List<TbPatrolStoreHistoryDo> TbPatrolStoreHistoryList = tbPatrolStoreHistoryMapper.selectPatrolStoreHistoryList(enterpriseId, String.valueOf(businessId));

        if (patrolStoreCheckDO != null && patrolStoreCheckDO.getTaskId() == 0) {

            EnterpriseUserDO enterpriseUserDO = enterpriseUserDao.selectByUserIdIgnoreActive(enterpriseId, patrolStoreCheckDO.getCreateUserId());
            TbPatrolStoreHistoryDo tb = TbPatrolStoreHistoryDo.builder().avatar(enterpriseUserDO.getAvatar()).operateUserName(enterpriseUserDO.getName()).
                    createTime(patrolStoreCheckDO.getCreateTime()).operateType("create").build();
            TbPatrolStoreHistoryList.add(tb);
        }
        if (patrolStoreCheckDO != null) {
            //封装任务创建者到  TbPatrolStoreHistoryList
            TaskParentDO taskParentDO = taskParentMapper.selectParentTaskById(enterpriseId, patrolStoreCheckDO.getTaskId());
            if(taskParentDO != null){

                EnterpriseUserDO createUserDO = enterpriseUserDao.selectByUserIdIgnoreActive(enterpriseId, taskParentDO.getCreateUserId());

                if (Constants.SYSTEM_USER_ID.equals(taskParentDO.getCreateUserId())) {
                    taskParentDO.setCreateUserName(Constants.SYSTEM_USER_NAME);
                }else if(Constants.AI.equals(taskParentDO.getCreateUserId())){
                    taskParentDO.setCreateUserName(Constants.AI);
                }else {
                    taskParentDO.setCreateUserName(createUserDO.getName());
                }
                TbPatrolStoreHistoryDo createHistory = new TbPatrolStoreHistoryDo();
                createHistory.setCreateTime(new Date(taskParentDO.getCreateTime()));
                createHistory.setOperateType("create");
                createHistory.setOperateUserId(taskParentDO.getCreateUserId());
                createHistory.setOperateUserName(taskParentDO.getCreateUserName());
                createHistory.setAvatar("");
                if(createUserDO != null){
                    createHistory.setAvatar(createUserDO.getAvatar());
                }
                TbPatrolStoreHistoryList.add(createHistory);
            }
        }
        //按照时间排序返回
        return TbPatrolStoreHistoryList.stream().sorted(Comparator.comparing(TbPatrolStoreHistoryDo::getCreateTime)).collect(Collectors.toList());
    }

    @Override
    public List<PatrolRecordStatusEveryDayVO> patrolRecordStatusEveryDay(String enterpriseId, PatrolRecordStatusRequest param) {
        // 根据日期查询已完成的巡店记录
        List<TbPatrolStoreRecordDO> recordList = tbPatrolStoreRecordMapper.getFinishedRecord(enterpriseId, param.getStoreId(), param.getBeginDate(), param.getEndDate(), null);
        Map<String, List<TbPatrolStoreRecordDO>> recordDateMap = recordList.stream().filter(o->Objects.nonNull(o.getSignStartTime())).collect(Collectors.groupingBy(c -> DateUtils.getTime(c.getSignStartTime())));
        String endDate = DateUtils.getTime(param.getEndDate());
        List<Date> betweenDate = DateUtils.getBetweenDate(param.getBeginDate(), DateFormatUtil.parse(endDate, DateUtils.DATE_FORMAT_DAY, Locale.US));
        List<PatrolRecordStatusEveryDayVO> result = Lists.newArrayList();
        for (Date everyDaTe : betweenDate) {
            String everyDay = DateUtils.getTime(everyDaTe);
            // 是否已巡
            boolean status = CollectionUtils.isNotEmpty(recordDateMap.get(everyDay));
            PatrolRecordStatusEveryDayVO patrolRecordStatusEveryDayVO = PatrolRecordStatusEveryDayVO.builder().dayDate(everyDay).status(status).build();
            result.add(patrolRecordStatusEveryDayVO);
        }
        return result;
    }

    @Override
    public List<PatrolRecordListByDayVO> patrolRecordListByDay(String enterpriseId, PatrolRecordListByDayRequest param) {
        List<TbPatrolStoreRecordDO> tableRecordDOList = tbPatrolStoreRecordMapper.getFinishedRecord(enterpriseId, param.getStoreId(), null, null, param.getQueryDate());
        if (tableRecordDOList.isEmpty()) {
            return Lists.newArrayList();
        }
        List<Long> businessIds = tableRecordDOList.stream().map(TbPatrolStoreRecordDO::getId).collect(Collectors.toList());
        // 标准检查表
        List<PatrolStoreStatisticsDataStaTableDTO> columnCountList =
                tbDataStaTableColumnMapper.statisticsColumnCountByBusinessId(enterpriseId, businessIds);
        Map<Long, PatrolStoreStatisticsDataStaTableDTO> dataTableIdColumnCountMap = columnCountList.stream().collect(Collectors.toMap(PatrolStoreStatisticsDataStaTableDTO::getBusinessId, Function.identity(), (a, b) -> a));
        // 自定义检查表
        List<PatrolStoreStatisticsDataColumnCountDTO> columnDefCountList = tbDataDefTableColumnMapper.statisticsColumnCountByBusinessIds(enterpriseId, new ArrayList<>(businessIds));
        Map<Long, PatrolStoreStatisticsDataColumnCountDTO> dataDefTableIdColumnCountMap = columnDefCountList.stream().collect(Collectors.toMap(PatrolStoreStatisticsDataColumnCountDTO::getBusinessId, Function.identity(), (a, b) -> a));
        return PatrolRecordListByDayVO.convert(tableRecordDOList, dataTableIdColumnCountMap, dataDefTableIdColumnCountMap);
    }

    @Override
    public List<StoreAcceptanceVO> getStoreAcceptanceRecords(String enterpriseId, StoreAcceptanceRequest request) {
        List<TbPatrolStoreRecordDO> records = tbPatrolStoreRecordMapper.getByThirdBusinessId(enterpriseId, request.getThirdBusinessId());
        if (CollectionUtils.isEmpty(records)) {
            return Collections.emptyList();
        }
        List<Long> businessIds = CollStreamUtil.toList(records, TbPatrolStoreRecordDO::getId);
        List<TbDataTableDO> dataTableList = tbDataTableMapper.getTableListByBusinessId(enterpriseId, businessIds);
        Map<Long, TbDataTableDO> dataTableMap = CollStreamUtil.toMap(dataTableList, TbDataTableDO::getBusinessId, v -> v);

        return CollStreamUtil.toList(records, v -> new StoreAcceptanceVO(v, dataTableMap.get(v.getId())));
    }
}
