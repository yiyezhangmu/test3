package com.coolcollege.intelligent.service.tbdisplay.impl;

import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.annotation.AsyncDynamicExport;
import com.coolcollege.intelligent.common.constant.CommonConstant;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.constant.RedisConstant;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.enums.MsgUniteDataTypeEnum;
import com.coolcollege.intelligent.common.enums.elasticSearch.ElasticSearchQueueMsgTypeEnum;
import com.coolcollege.intelligent.common.enums.importexcel.ImportTaskConstant;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.page.PageVO;
import com.coolcollege.intelligent.common.util.DateUtils;
import com.coolcollege.intelligent.common.util.OpenApiParamCheckUtils;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseUserMapper;
import com.coolcollege.intelligent.dao.enterprise.dao.EnterpriseConfigDao;
import com.coolcollege.intelligent.dao.enterprise.dao.EnterpriseUserDao;
import com.coolcollege.intelligent.dao.metatable.TbMetaTableMapper;
import com.coolcollege.intelligent.dao.patrolstore.TbDataTableMapper;
import com.coolcollege.intelligent.dao.region.RegionMapper;
import com.coolcollege.intelligent.dao.store.StoreGroupMapper;
import com.coolcollege.intelligent.dao.store.StoreGroupMappingMapper;
import com.coolcollege.intelligent.dao.store.StoreMapper;
import com.coolcollege.intelligent.dao.tbdisplay.*;
import com.coolcollege.intelligent.dao.unifytask.TaskMappingMapper;
import com.coolcollege.intelligent.dao.unifytask.TaskParentMapper;
import com.coolcollege.intelligent.dao.unifytask.TaskStoreMapper;
import com.coolcollege.intelligent.dao.unifytask.TaskSubMapper;
import com.coolcollege.intelligent.facade.dto.PageDTO;
import com.coolcollege.intelligent.facade.dto.openApi.DisplayDTO;
import com.coolcollege.intelligent.facade.dto.openApi.vo.DisplayRecordDetailVO;
import com.coolcollege.intelligent.facade.dto.openApi.vo.DisplayRecordVO;
import com.coolcollege.intelligent.facade.dto.openApi.vo.DisplayTableDataColumnVO;
import com.coolcollege.intelligent.facade.dto.openApi.vo.DisplayTableDataContentVO;
import com.coolcollege.intelligent.mapper.mq.MqMessageDAO;
import com.coolcollege.intelligent.model.display.DisplayConstant;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseUserDO;
import com.coolcollege.intelligent.model.enums.*;
import com.coolcollege.intelligent.model.export.request.ExportMsgSendRequest;
import com.coolcollege.intelligent.model.impoetexcel.ImportTaskDO;
import com.coolcollege.intelligent.model.metatable.TbMetaTableDO;
import com.coolcollege.intelligent.model.msg.MsgUniteData;
import com.coolcollege.intelligent.model.patrolstore.TbDataTableDO;
import com.coolcollege.intelligent.model.region.RegionDO;
import com.coolcollege.intelligent.model.store.StoreDO;
import com.coolcollege.intelligent.model.store.StoreGroupDO;
import com.coolcollege.intelligent.model.store.StoreGroupMappingDO;
import com.coolcollege.intelligent.model.store.dto.StoreAreaDTO;
import com.coolcollege.intelligent.model.tbdisplay.*;
import com.coolcollege.intelligent.model.tbdisplay.constant.TbDisplayConstant;
import com.coolcollege.intelligent.model.tbdisplay.param.*;
import com.coolcollege.intelligent.model.tbdisplay.vo.*;
import com.coolcollege.intelligent.model.unifytask.TaskParentDO;
import com.coolcollege.intelligent.model.unifytask.TaskStoreDO;
import com.coolcollege.intelligent.model.unifytask.TaskSubDO;
import com.coolcollege.intelligent.model.unifytask.dto.TaskMessageDTO;
import com.coolcollege.intelligent.model.unifytask.dto.UnifyFormDataDTO;
import com.coolcollege.intelligent.model.unifytask.dto.UnifyPersonDTO;
import com.coolcollege.intelligent.model.unifytask.query.TaskStoreLoopQuery;
import com.coolcollege.intelligent.model.unifytask.vo.GetTaskByPersonVO;
import com.coolcollege.intelligent.model.unifytask.vo.TaskSubVO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.model.workFlow.WorkflowDataDTO;
import com.coolcollege.intelligent.producer.OrderMessageService;
import com.coolcollege.intelligent.producer.SimpleMessageService;
import com.coolcollege.intelligent.service.elasticsearch.ElasticSearchService;
import com.coolcollege.intelligent.service.importexcel.ImportTaskService;
import com.coolcollege.intelligent.service.tbdisplay.TbDisplayTableRecordService;
import com.coolcollege.intelligent.service.unifytask.UnifyTaskParentService;
import com.coolcollege.intelligent.service.unifytask.UnifyTaskService;
import com.coolcollege.intelligent.service.unifytask.UnifyTaskStoreService;
import com.coolcollege.intelligent.service.workflow.WorkflowService;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.coolcollege.intelligent.util.TbDisplayDynamicExcelUtil;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.coolstore.base.enums.RocketMqTagEnum;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.coolcollege.intelligent.common.util.DateUtils.DATE_FORMAT_SEC_5;
import static com.coolcollege.intelligent.model.metatable.MetaTableConstant.BusinessTypeConstant.TB_DISPLAY_TASK;

/**
 * @author wxp
 * @date 2021-03-02 19:49
 */
@Service
@Slf4j
public class TbDisplayTableRecordServiceImpl implements TbDisplayTableRecordService {

    @Resource
    private TaskParentMapper taskParentMapper;
    @Resource
    private TaskSubMapper taskSubMapper;
    @Resource
    private RegionMapper regionMapper;
    @Resource
    private TbDisplayTableRecordMapper tbDisplayTableRecordMapper;
    @Resource
    private TaskMappingMapper taskMappingMapper;
    @Resource
    private StoreMapper storeMapper;
    @Resource
    private EnterpriseUserDao enterpriseUserDao;
    @Resource
    private TbMetaTableMapper tbMetaTableMapper;
    @Resource
    private TbDataTableMapper tbDataTableMapper;
    @Resource
    private TbMetaDisplayTableColumnMapper tbMetaDisplayTableColumnMapper;
    @Resource
    private TbDisplayTableDataColumnMapper tbDisplayTableDataColumnMapper;
    @Resource
    private TbDisplayTableDataContentMapper tbDisplayTableDataContentMapper;
    @Resource
    private TbDisplayHistoryMapper tbDisplayHistoryMapper;
    @Resource
    private TbDisplayHistoryColumnMapper tbDisplayHistoryColumnMapper;

    @Resource
    private TaskStoreMapper taskStoreMapper;

    @Resource
    private StoreGroupMapper storeGroupMapper;

    @Resource
    private StoreGroupMappingMapper storeGroupMappingMapper;

    @Autowired
    private RedisUtilPool redisUtilPool;

    @Resource
    private ImportTaskService importTaskService;

    @Autowired
    private UnifyTaskStoreService unifyTaskStoreService;

    @Autowired
    private ElasticSearchService elasticSearchService;
    @Resource
    private OrderMessageService orderMessageService;
    @Resource
    private SimpleMessageService simpleMessageService;
    @Lazy
    @Resource
    private UnifyTaskService unifyTaskService;
    @Resource
    private EnterpriseConfigDao enterpriseConfigDao;

    /**
     * 默认总体评分
     */
    private final static Integer DEFAULT_TOTAL_SCORE = 100;

    @Resource
    private UnifyTaskParentService unifyTaskParentService;

    @Resource
    private WorkflowService workflowService;
    @Resource
    private MqMessageDAO mqMessageDAO;

    /**
     * 新建陈列任务
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addTbDisplayTableRecord(TaskMessageDTO taskMessageDTO, List<TaskSubDO> newSubDOList) {
        String enterpriseId = taskMessageDTO.getEnterpriseId(); // 企业id
        Long unifyTaskId = taskMessageDTO.getUnifyTaskId(); // 父任务id
        String taskInfo = taskMessageDTO.getTaskInfo();
        JSONObject taskInfoJsonObj = JSON.parseObject(taskInfo);
        JSONObject tbdisplaydefindObj = taskInfoJsonObj.getJSONObject("tbDisplayDefined");
        Boolean isSupportScore = false;
        Boolean isSupportPhoto = false;
        if(tbdisplaydefindObj != null){
            isSupportScore = tbdisplaydefindObj.getBoolean("isSupportScore");
            isSupportPhoto = tbdisplaydefindObj.getBoolean("isSupportPhoto");
        }
        // 检查表ids
        List<UnifyFormDataDTO> unifyFormDataDTOList = taskMappingMapper.selectMappingDataByTaskId(enterpriseId, unifyTaskId);
        List<Long> metaTableIds = unifyFormDataDTOList.stream()
                .filter(a -> UnifyTaskDataTypeEnum.TB_DISPLAY.getCode().equals(a.getType()))
                .map(a -> Long.valueOf(a.getOriginMappingId())).collect(Collectors.toList());
        // 子任务
        List<TbDisplayTableRecordBuildParam.TbDisplayTableRecordSubBuildParam> subBuildParams =
                newSubDOList.stream()
                        .map(a -> TbDisplayTableRecordBuildParam.TbDisplayTableRecordSubBuildParam.builder().subTaskId(a.getId())
                                .storeId(a.getStoreId()).handleUserId(a.getHandleUserId()).build())
                        .collect(Collectors.toList());
        //查询任意一个子任务的时间 同步时间到陈列巡店记录表
        TaskSubDO taskSubDO = taskSubMapper.selectSubTaskById(enterpriseId, newSubDOList.get(0).getId());
        log.info("TaskSubDO.taskSubDO={}",JSON.toJSONString(taskSubDO));
        // 参数构建
        TbDisplayTableRecordBuildParam patrolStoreBuildParam = TbDisplayTableRecordBuildParam.builder()
                .unifyTaskId(unifyTaskId)
                .createUserId(taskMessageDTO.getCreateUserId())
                .metaTableId(CollUtil.isNotEmpty(metaTableIds) ? metaTableIds.get(0) : 0L)
                .attachUrl(taskMessageDTO.getAttachUrl())
                .isSupportScore(isSupportScore==null?false:isSupportScore)
                .isSupportPhoto(isSupportPhoto==null?false:isSupportPhoto)
                .loopCount(newSubDOList.get(0).getLoopCount())
                .subBuildParams(subBuildParams)
                .handlerEndTime(newSubDOList.get(0).getHandlerEndTime())
                .subBeginTime(new Date(newSubDOList.get(0).getSubBeginTime()))
                .handlerEndTime(taskSubDO.getHandlerEndTime())
                .subEndTime(new Date(newSubDOList.get(0).getSubEndTime())).build();
        return buildTbDisplayTableRecord(enterpriseId, patrolStoreBuildParam);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean buildTbDisplayTableRecord(String enterpriseId, TbDisplayTableRecordBuildParam param) {
        log.info("新建陈列记录参数，param={}", JSON.toJSONString(param));
        // Map:storeId->storeName
        Set<String> storeIds = param.getSubBuildParams().stream()
                .map(TbDisplayTableRecordBuildParam.TbDisplayTableRecordSubBuildParam::getStoreId).collect(Collectors.toSet());
        List<StoreDO> storeDOList = storeMapper.getByStoreIds(enterpriseId, new ArrayList<>(storeIds));
        Map<String, StoreDO> storeIdDOMap =
                storeDOList.stream().collect(Collectors.toMap(StoreDO::getStoreId, Function.identity(), (a, b) -> a));
        // Map:userId->userName
        // 锁定检查表元数据meta_table
        Long metaTableId = param.getMetaTableId();
        List<Long> metaTableIds = new ArrayList<>();
        metaTableIds.add(metaTableId);
        // 抽出来  消息更改
        tbMetaTableMapper.updateLockedByIds(enterpriseId, metaTableIds);
        List<TbMetaTableDO> metaTableDOList = tbMetaTableMapper.selectByIds(enterpriseId, metaTableIds);


        TaskParentDO taskParentDO = taskParentMapper.selectTaskById(enterpriseId, param.getUnifyTaskId());

        // 初始化陈列任务记录 tb_display_table_record_
        List<TbDisplayTableRecordDO> recordList = storeIds.stream().map(a -> TbDisplayTableRecordDO.builder()
                .unifyTaskId(param.getUnifyTaskId()).storeId(a).loopCount(param.getLoopCount())
                .storeName(storeIdDOMap.get(a).getStoreName())
                .regionId(storeIdDOMap.get(a).getRegionId())
                .regionPath(storeIdDOMap.get(a).getRegionPath())
                .metaTableId(param.getMetaTableId())
                .attachUrl(param.getAttachUrl())
                .isSupportScore(param.getIsSupportScore())
                .isSupportPhoto(param.getIsSupportPhoto())
                .status(TbDisplayConstant.TbDisplayRecordStatusConstant.HANDLE)
                .taskName(taskParentDO.getTaskName())
                .subBeginTime(param.getSubBeginTime())
                .subEndTime(param.getSubEndTime())
                .handlerEndTime(param.getHandlerEndTime())
                .build()).collect(Collectors.toList());
        tbDisplayTableRecordMapper.batchInsert(enterpriseId, recordList);
        log.info("tbDisplayTableRecordMapper.batchInsert recordList = {}",JSON.toJSONString(recordList));

        // 初始化检查表data_table
        List<TbDataTableDO> tbDataTableDOList = recordList.stream().map(a -> {
            return  TbDataTableDO.builder().taskId(a.getUnifyTaskId()).subTaskId(0L)
                    .storeId(a.getStoreId()).storeName(a.getStoreName()).regionId(a.getRegionId())
                    .regionPath(a.getRegionPath()).businessId(a.getId()).businessType(TB_DISPLAY_TASK)
                    .metaTableId(metaTableDOList.get(0).getId()).tableName(metaTableDOList.get(0).getTableName())
                    .description(metaTableDOList.get(0).getDescription())
                    .supportScore(metaTableDOList.get(0).getSupportScore())
                    .createUserId(param.getCreateUserId())
                    .supervisorId("").tableProperty(metaTableDOList.get(0).getTableProperty())
                    .tableType(metaTableDOList.get(0).getTableType()).build();
        }).collect(Collectors.toList());
        tbDataTableMapper.batchInsert(enterpriseId, tbDataTableDOList);
        Map<Long, TbDataTableDO> tbDataTableDOMap =
                tbDataTableDOList.stream().collect(Collectors.toMap(TbDataTableDO::getBusinessId, Function.identity(), (a, b) -> a));

        // 初始化陈列检查项 tb_display_table_data_column_
        if (CollectionUtils.isNotEmpty(metaTableIds)) {
            List<TbMetaDisplayTableColumnDO> tbMetaDisplayTableList =
                    tbMetaDisplayTableColumnMapper.selectColumnListByTableIdList(enterpriseId, metaTableIds);
            List<TbMetaDisplayTableColumnDO> tbMetaDisplayTableColumnDOList = tbMetaDisplayTableList.stream().
                    filter(a -> a.getCheckType() != 1).collect(Collectors.toList());
            Map<Long, List<TbMetaDisplayTableColumnDO>> metaTableIdColumnListMap = tbMetaDisplayTableColumnDOList.stream()
                    .collect(Collectors.groupingBy(TbMetaDisplayTableColumnDO::getMetaTableId));
            List<TbDisplayTableDataColumnDO> tbDisplayTableDataColumnDOList =
                    recordList.stream().flatMap(a -> metaTableIdColumnListMap.get(a.getMetaTableId()).stream()
                            .map(column -> TbDisplayTableDataColumnDO.builder()
                                    .unifyTaskId(a.getUnifyTaskId())
                                    .loopCount(a.getLoopCount())
                                    .metaTableId(a.getMetaTableId())
                                    .metaColumnId(column.getId())
                                    .recordId(a.getId())
                                    .taskName(a.getTaskName())
                                    .storeId(a.getStoreId()).storeName(a.getStoreName())
                                    .regionId(a.getRegionId()).regionPath(a.getRegionPath())
                                    .dataTableId(tbDataTableDOMap.get(a.getId()).getId()).score(new BigDecimal(Constants.ZERO_STR))
                                    .isAiCheck(column.getIsAiCheck())
                                    .build()))
                            .collect(Collectors.toList());
            tbDisplayTableDataColumnMapper.batchInsert(enterpriseId, tbDisplayTableDataColumnDOList);

            //初始化陈列检查内容
            List<TbMetaDisplayTableColumnDO> tbMetaDisplayTableContentDOList = tbMetaDisplayTableList.stream().
                    filter(a -> a.getCheckType() == 1).collect(Collectors.toList());
            Map<Long, List<TbMetaDisplayTableColumnDO>> metaTableIdContentListMap = tbMetaDisplayTableContentDOList.stream()
                    .collect(Collectors.groupingBy(TbMetaDisplayTableColumnDO::getMetaTableId));
            if (metaTableIdContentListMap.size()!=0){
                List<TbDisplayTableDataColumnDO> tbDisplayTableDataContentDOList =
                        recordList.stream().flatMap(a -> metaTableIdContentListMap.get(a.getMetaTableId()).stream()
                                .map(column -> TbDisplayTableDataColumnDO.builder()
                                        .unifyTaskId(a.getUnifyTaskId())
                                        .loopCount(a.getLoopCount())
                                        .metaTableId(a.getMetaTableId())
                                        .metaContentId(column.getId())
                                        .recordId(a.getId())
                                        .taskName(a.getTaskName())
                                        .storeId(a.getStoreId()).storeName(a.getStoreName())
                                        .regionId(a.getRegionId()).regionPath(a.getRegionPath())
                                        .dataTableId(tbDataTableDOMap.get(a.getId()).getId()).score(new BigDecimal(Constants.ZERO_STR))
                                        .build()))
                                .collect(Collectors.toList());
                tbDisplayTableDataContentMapper.batchInsert(enterpriseId,tbDisplayTableDataContentDOList);
            }

            log.info("tbDisplayTableDataColumnMapper.batchInsert tbDisplayTableDataColumnDOList {}", JSON.toJSONString(tbDisplayTableDataColumnDOList));
        }
        return true;
    }

    @Override
    public boolean buildByTaskId(String enterpriseId, TbDisplayTableRecordBuildParam param) {
        Long unifyTaskId = param.getUnifyTaskId();
        Long loopCount = param.getLoopCount();
        Set<String> storeIds = param.getSubBuildParams().stream()
                .map(TbDisplayTableRecordBuildParam.TbDisplayTableRecordSubBuildParam::getStoreId).collect(Collectors.toSet());

        List<TbDisplayTableRecordDO> tbDisplayTableRecordDOList = tbDisplayTableRecordMapper.listByUnifyTaskIdAndloopCountAndStoreIds(
                enterpriseId, unifyTaskId, loopCount, new ArrayList<>(storeIds));
        if(CollUtil.isNotEmpty(tbDisplayTableRecordDOList)){
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "门店对应记录已存在！");
        }
        TaskParentDO taskParentDO = taskParentMapper.selectTaskById(enterpriseId, unifyTaskId);
        String taskInfo = taskParentDO.getTaskInfo();
        JSONObject taskInfoJsonObj = JSON.parseObject(taskInfo);
        JSONObject tbdisplaydefindObj = taskInfoJsonObj.getJSONObject("tbDisplayDefined");
        Boolean isSupportScore = false;
        Boolean isSupportPhoto = false;
        if(tbdisplaydefindObj != null){
            isSupportScore = tbdisplaydefindObj.getBoolean("isSupportScore");
            isSupportPhoto = tbdisplaydefindObj.getBoolean("isSupportPhoto");
        }
        // 检查表ids
        List<UnifyFormDataDTO> unifyFormDataDTOList = taskMappingMapper.selectMappingDataByTaskId(enterpriseId, unifyTaskId);
        List<Long> metaTableIds = unifyFormDataDTOList.stream()
                .filter(a -> UnifyTaskDataTypeEnum.TB_DISPLAY.getCode().equals(a.getType()))
                .map(a -> Long.valueOf(a.getOriginMappingId())).collect(Collectors.toList());

        // 参数构建
        TbDisplayTableRecordBuildParam patrolStoreBuildParam = TbDisplayTableRecordBuildParam.builder()
                .unifyTaskId(unifyTaskId)
                .createUserId(taskParentDO.getCreateUserId())
                .metaTableId(CollUtil.isNotEmpty(metaTableIds) ? metaTableIds.get(0) : 0L)
                .attachUrl(taskParentDO.getAttachUrl())
                .isSupportScore(isSupportScore==null?false:isSupportScore)
                .isSupportPhoto(isSupportPhoto==null?false:isSupportPhoto)
                .loopCount(loopCount)
                .subBuildParams(param.getSubBuildParams()).build();
        return buildTbDisplayTableRecord(enterpriseId, patrolStoreBuildParam);
    }


    @Override
    public TbDisplayTableRecordVO getTbDisplayTableRecordVO(String enterpriseId, String userId, TaskSubDO taskSubDO) {
        if (taskSubDO == null) {
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "未找到对应的子任务");
        }
        TbDisplayTableRecordVO vo = new TbDisplayTableRecordVO();
        TbDisplayTableRecordDO tbDisplayTableRecordDO = tbDisplayTableRecordMapper.getByUnifyTaskIdAndStoreIdAndLoopCount(enterpriseId, taskSubDO.getUnifyTaskId(), taskSubDO.getStoreId(), taskSubDO.getLoopCount());
        if (tbDisplayTableRecordDO == null) {
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "陈列记录不存在");
        }
        TaskParentDO taskParentDO = taskParentMapper.selectTaskById(enterpriseId, tbDisplayTableRecordDO.getUnifyTaskId());
        JSONObject taskInfo = JSONObject.parseObject(taskParentDO.getTaskInfo());
        String shopNo = null;
        String shopName = null;
        if(taskInfo != null){
            JSONObject tbdisplaydefindObj = taskInfo.getJSONObject("tbDisplayDefined");
            if(tbdisplaydefindObj != null){
                shopNo = tbdisplaydefindObj.getString("shopNo");
                shopName = tbdisplaydefindObj.getString("shopName");
            }
        }
        vo.setShopNo(shopNo);
        vo.setShopName(shopName);
        BeanUtils.copyProperties(tbDisplayTableRecordDO, vo);
        StoreDO storeDO = storeMapper.getByStoreId(enterpriseId, tbDisplayTableRecordDO.getStoreId());
        EnterpriseUserDO userDO = enterpriseUserDao.selectByUserId(enterpriseId, userId);
        if(userDO != null){
            vo.setJobNum(userDO.getJobnumber());
        }
        if (Objects.nonNull(storeDO)) {
            vo.setStoreNum(storeDO.getStoreNum());
        }

        UnifyNodeEnum unifyNodeEnum = UnifyNodeEnum.getByCode(taskSubDO.getNodeNo());

        if (unifyNodeEnum == null) {
            log.info("unifyNodeEnum审批节点不存在，subTaskId:" + taskSubDO.getId());
            throw new ServiceException(ErrorCodeEnum.DATA_ERROR.getCode(), "审批节点不存在");
        }
        String operateType = null;
        switch (unifyNodeEnum) {
            case SECOND_NODE:
                operateType = TbDisplayConstant.TbDisplayRecordStatusConstant.APPROVE;
                break;
            case THIRD_NODE:
                operateType = TbDisplayConstant.TbDisplayRecordStatusConstant.RECHECK;
                break;
            case FOUR_NODE:
                operateType = TbDisplayConstant.TbDisplayRecordStatusConstant.THIRD_APPROVE;
                break;
            case FIVE_NODE:
                operateType = TbDisplayConstant.TbDisplayRecordStatusConstant.FOUR_APPROVE;
                break;
            case SIX_NODE:
                operateType = TbDisplayConstant.TbDisplayRecordStatusConstant.FIVE_APPROVE;
                break;
            default:
        }
        Map<Long, TbDisplayHistoryColumnDO> historyColumnMaps = new HashMap<>();
        Map<Long, TbDisplayHistoryColumnDO> historyContentMaps = new HashMap<>();
        Map<Long, List<TbDisplayHistoryColumnVO>> approveHistoryColumnMap = new HashMap<>();
        Map<Long, List<TbDisplayHistoryColumnVO>> approveHistoryContentMap = new HashMap<>();
        if(StringUtils.isNotBlank(operateType) && UnifyStatus.ONGOING.getCode().equals(taskSubDO.getSubStatus())){
            //当前审核人，审核记录
            TbDisplayHistoryDO historyDO = tbDisplayHistoryMapper.selectDisplayHistory(enterpriseId, tbDisplayTableRecordDO.getId(), operateType, userId, taskSubDO.getId());
            if(historyDO != null){
                //审核草稿项
                List<TbDisplayHistoryColumnDO> tbDisplayHistoryColumnOldList = tbDisplayHistoryColumnMapper.getListByHistoryId(enterpriseId, Collections.singletonList(historyDO.getId()));
                List<TbDisplayHistoryColumnDO> list = tbDisplayHistoryColumnOldList.stream().filter(a -> a.getCheckType() == 0).collect(Collectors.toList());
                //检查项 历史记录
                historyColumnMaps = list.stream().collect(Collectors.toMap(TbDisplayHistoryColumnDO::getDataColumnId,Function.identity()));
                //检查内容 历史数据
                List<TbDisplayHistoryColumnDO> contentList = tbDisplayHistoryColumnOldList.stream().filter(a -> a.getCheckType() == 1).collect(Collectors.toList());
                historyContentMaps = contentList.stream().collect(Collectors.toMap(TbDisplayHistoryColumnDO::getDataColumnId,Function.identity()));
            }
        };


        List<TbDisplayHistoryColumnDO> historyColumnDOList = tbDisplayHistoryColumnMapper.getListByRecordIdList(enterpriseId, Collections.singletonList(tbDisplayTableRecordDO.getId()));

        if(CollectionUtils.isNotEmpty(historyColumnDOList)){
            List<String> userIdList = historyColumnDOList.stream().map(TbDisplayHistoryColumnDO::getOperateUserId).collect(Collectors.toList());

            List<EnterpriseUserDO> enterpriseUserDOList = enterpriseUserDao.selectByUserIds(enterpriseId, userIdList);
            Map<String, String> avatarMap = ListUtils.emptyIfNull(enterpriseUserDOList)
                    .stream()
                    .filter(a -> a.getUserId() != null && a.getAvatar() != null)
                    .collect(Collectors.toMap(EnterpriseUserDO::getUserId, EnterpriseUserDO::getAvatar, (a, b) -> a));

            List<TbDisplayHistoryColumnDO> columnList = historyColumnDOList.stream().filter(a -> a.getCheckType() == 0).collect(Collectors.toList());
            //检查项 历史记录
            approveHistoryColumnMap = columnList.stream().map(columnDO -> TbDisplayHistoryColumnVO.builder().columnId(columnDO.getDataColumnId())
                    .operateUserId(columnDO.getOperateUserId()).operateUserName(columnDO.getOperateUserName()).remark(columnDO.getRemark()).avatar(avatarMap.get(columnDO.getOperateUserId()))
                    .operateTime(columnDO.getUpdateTime()).operateType(columnDO.getOperateType()).build()).collect(Collectors.toList()).stream().collect(Collectors.groupingBy(TbDisplayHistoryColumnVO::getColumnId));
            //检查内容 历史数据
            List<TbDisplayHistoryColumnDO> contentList = historyColumnDOList.stream().filter(a -> a.getCheckType() == 1).collect(Collectors.toList());
            //检查项 历史记录
            approveHistoryContentMap = contentList.stream().map(columnDO -> TbDisplayHistoryColumnVO.builder().columnId(columnDO.getDataColumnId())
                    .operateUserId(columnDO.getOperateUserId()).operateUserName(columnDO.getOperateUserName()).remark(columnDO.getRemark()).avatar(avatarMap.get(columnDO.getOperateUserId()))
                    .operateTime(columnDO.getUpdateTime()).operateType(columnDO.getOperateType()).build()).collect(Collectors.toList()).stream().collect(Collectors.groupingBy(TbDisplayHistoryColumnVO::getColumnId));
        }

        List<TbDisplayTableDataColumnDO> tbDisplayTableDataColumnDOList = tbDisplayTableDataColumnMapper.listByRecordId(enterpriseId, tbDisplayTableRecordDO.getId());
        List<Long> metaColumnIdList = tbDisplayTableDataColumnDOList.stream().map(TbDisplayTableDataColumnDO::getMetaColumnId).collect(Collectors.toList());
        List<TbMetaDisplayTableColumnDO>  tbMetaDisplayTableColumnDOList = tbMetaDisplayTableColumnMapper.listByIdList(enterpriseId, metaColumnIdList);
        Map<Long, TbMetaDisplayTableColumnDO> tbMetaDisplayTableColumnDOMap =
                tbMetaDisplayTableColumnDOList.stream().collect(Collectors.toMap(TbMetaDisplayTableColumnDO::getId, Function.identity(), (a, b) -> a));


        List<TbDisplayTableDataColumnVO> tbDisplayDataColumnVOList = new ArrayList<>();
        for (TbDisplayTableDataColumnDO tbDisplayTableDataColumnDO : tbDisplayTableDataColumnDOList) {
            if (tbDisplayTableDataColumnDO == null) {
                continue;
            }
            TbDisplayTableDataColumnVO tbDisplayDataColumnVO = new TbDisplayTableDataColumnVO();
            BeanUtils.copyProperties(tbDisplayTableDataColumnDO, tbDisplayDataColumnVO);
            TbMetaDisplayTableColumnDO tbMetaDisplayTableColumnDO = tbMetaDisplayTableColumnDOMap.get(tbDisplayTableDataColumnDO.getMetaColumnId());
            if(tbMetaDisplayTableColumnDO == null){
                throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "检查项不存在【"+tbDisplayTableDataColumnDO.getMetaColumnId()+"】");
            }
            //是否可以取草稿项
            TbDisplayHistoryColumnDO historyColumnDO = historyColumnMaps.get(tbDisplayTableDataColumnDO.getId());
            if(historyColumnDO != null){
                tbDisplayDataColumnVO.setPhotoArray(historyColumnDO.getPhotoArray());
                tbDisplayDataColumnVO.setScore(historyColumnDO.getScore());
                tbDisplayDataColumnVO.setApproveRemark(historyColumnDO.getRemark());
            }
            tbDisplayDataColumnVO.setStandardPic(tbMetaDisplayTableColumnDO.getStandardPic());
            tbDisplayDataColumnVO.setDescription(tbMetaDisplayTableColumnDO.getDescription());
            tbDisplayDataColumnVO.setColumnName(tbMetaDisplayTableColumnDO.getColumnName());
            tbDisplayDataColumnVO.setOrderNum(tbMetaDisplayTableColumnDO.getOrderNum() == null ? 0 : tbMetaDisplayTableColumnDO.getOrderNum());
            tbDisplayDataColumnVO.setMetaScore(tbMetaDisplayTableColumnDO.getScore());
            tbDisplayDataColumnVO.setHistoryColumnVOList(approveHistoryColumnMap.get(tbDisplayTableDataColumnDO.getId()));
            tbDisplayDataColumnVO.setMustPic(tbMetaDisplayTableColumnDO.getMustPic());
            tbDisplayDataColumnVOList.add(tbDisplayDataColumnVO);
        }
        if(CollectionUtils.isNotEmpty(tbDisplayDataColumnVOList)){
            tbDisplayDataColumnVOList.sort(Comparator.comparingInt(TbDisplayTableDataColumnVO::getOrderNum));
        }
        vo.setTbDisplayDataColumnVOList(tbDisplayDataColumnVOList);

        List<TbDisplayTableDataContentVO> tbDisplayDataContentVOList = new ArrayList<>();
        //在content表中根据任务记录id查询数据
        List<TbDisplayTableDataContentDO> tbDisplayTableDataContentDOList = tbDisplayTableDataContentMapper.listByRecordId(enterpriseId, tbDisplayTableRecordDO.getId());
        if (tbDisplayTableDataContentDOList!=null&&tbDisplayTableDataContentDOList.size()>0){
            //筛选出陈列使用得到的陈列检查内容id
            List<Long> metaContentIdList = tbDisplayTableDataContentDOList.stream().map(TbDisplayTableDataContentDO::getMetaContentId).collect(Collectors.toList());
            //查询出陈列用到的模板数据集合
            List<TbMetaDisplayTableColumnDO>  tbMetaDisplayTableContentDOList = tbMetaDisplayTableColumnMapper.listByIdList(enterpriseId, metaContentIdList);
            Map<Long, TbMetaDisplayTableColumnDO> tbMetaDisplayTableContentDOMap =
                    tbMetaDisplayTableContentDOList.stream().collect(Collectors.toMap(TbMetaDisplayTableColumnDO::getId, Function.identity(), (a, b) -> a));

            for (TbDisplayTableDataContentDO tbDisplayTableDataContentDO : tbDisplayTableDataContentDOList) {
                if (tbDisplayTableDataContentDO == null) {
                    continue;
                }
                TbDisplayTableDataContentVO tbDisplayDataContentVo = new TbDisplayTableDataContentVO();
                BeanUtils.copyProperties(tbDisplayTableDataContentDO, tbDisplayDataContentVo);
                TbMetaDisplayTableColumnDO tbMetaDisplayTableColumnDO = tbMetaDisplayTableContentDOMap.get(tbDisplayTableDataContentDO.getMetaContentId());
                if(tbMetaDisplayTableColumnDO == null){
                    throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "检查内容不存在【"+tbDisplayTableDataContentDO.getMetaContentId()+"】");
                }
                //是否可以取草稿项
                if(historyContentMaps.size()!=0){
                    TbDisplayHistoryColumnDO historyColumnDO = historyContentMaps.get(tbDisplayTableDataContentDO.getId());
                    if(historyColumnDO != null){
                        tbDisplayDataContentVo.setPhotoArray(historyColumnDO.getPhotoArray());
                        tbDisplayDataContentVo.setScore(historyColumnDO.getScore());
                        tbDisplayDataContentVo.setApproveRemark(historyColumnDO.getRemark());
                    }
                }
                tbDisplayDataContentVo.setStandardPic(tbMetaDisplayTableColumnDO.getStandardPic());
                tbDisplayDataContentVo.setDescription(tbMetaDisplayTableColumnDO.getDescription());
                tbDisplayDataContentVo.setColumnName(tbMetaDisplayTableColumnDO.getColumnName());
                tbDisplayDataContentVo.setOrderNum(tbMetaDisplayTableColumnDO.getOrderNum() == null ? 0 : tbMetaDisplayTableColumnDO.getOrderNum());
                tbDisplayDataContentVo.setMetaScore(tbMetaDisplayTableColumnDO.getScore());
                tbDisplayDataContentVo.setHistoryColumnVOList(approveHistoryContentMap.get(tbDisplayTableDataContentDO.getId()));
                tbDisplayDataContentVo.setMustPic(tbMetaDisplayTableColumnDO.getMustPic());
                tbDisplayDataContentVOList.add(tbDisplayDataContentVo);
            }
            vo.setTbDisplayDataContentList(tbDisplayDataContentVOList);
        }
        return vo;
    }

    @Override
    public TbDisplayTableRecordVO getTableRecordByTaskIdAndStoreIdAndLoopCount(String enterpriseId, Long unifyTaskId, String storeId, Long loopCount) {

        TbDisplayTableRecordVO vo = new TbDisplayTableRecordVO();
        TbDisplayTableRecordDO tbDisplayTableRecordDO = tbDisplayTableRecordMapper.getByUnifyTaskIdAndStoreIdAndLoopCount(enterpriseId, unifyTaskId, storeId, loopCount);
        if (tbDisplayTableRecordDO == null) {
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "陈列记录不存在");
        }
        BeanUtils.copyProperties(tbDisplayTableRecordDO, vo);

        //处理超时时间
        vo.setHandlerDuration(formatterTime(tbDisplayTableRecordDO.getFirstHandlerTime(), tbDisplayTableRecordDO.getSubBeginTime()));
        vo.setApproveDuration(formatterTime(tbDisplayTableRecordDO.getFirstApproveTime(), tbDisplayTableRecordDO.getFirstHandlerTime()));

        List<StoreGroupDO> storeGroupMappingList = storeGroupMappingMapper.selectGroupsByStoreId(enterpriseId, tbDisplayTableRecordDO.getStoreId());
        List<String> groupIdList = CollectionUtils.emptyIfNull(storeGroupMappingList).stream().map(StoreGroupDO::getGroupId).collect(Collectors.toList());
        if(CollectionUtils.isNotEmpty(groupIdList)){
            List<StoreGroupDO> groupDOList = storeGroupMapper.getListByIds(enterpriseId, groupIdList);
            if(CollectionUtils.isNotEmpty(groupDOList)){
                List<String> gropNameList = groupDOList.stream().map(StoreGroupDO::getGroupName).collect(Collectors.toList());
                if(CollectionUtils.isNotEmpty(gropNameList)){
                    vo.setStoreGroupName(StringUtils.join(gropNameList, Constants.COMMA));
                }
            }
        }

        List<TbDisplayTableDataColumnDO> tbDisplayTableDataColumnDOList = tbDisplayTableDataColumnMapper.listByRecordId(enterpriseId, tbDisplayTableRecordDO.getId());
        List<Long> metaColumnIdList = tbDisplayTableDataColumnDOList.stream().map(TbDisplayTableDataColumnDO::getMetaColumnId).collect(Collectors.toList());
        List<TbMetaDisplayTableColumnDO>  tbMetaDisplayTableColumnDOList = tbMetaDisplayTableColumnMapper.listByIdList(enterpriseId, metaColumnIdList);
        Map<Long, TbMetaDisplayTableColumnDO> tbMetaDisplayTableColumnMap =
                tbMetaDisplayTableColumnDOList.stream().collect(Collectors.toMap(TbMetaDisplayTableColumnDO::getId, Function.identity(), (a, b) -> a));

        List<TbDisplayTableDataColumnVO> tbDisplayDataColumnVOList = new ArrayList<>();
        for (TbDisplayTableDataColumnDO tbDisplayTableDataColumnDO : tbDisplayTableDataColumnDOList) {
            if (tbDisplayTableDataColumnDO == null) {
                continue;
            }
            TbDisplayTableDataColumnVO tbDisplayDataColumnVO = new TbDisplayTableDataColumnVO();
            BeanUtils.copyProperties(tbDisplayTableDataColumnDO, tbDisplayDataColumnVO);
            TbMetaDisplayTableColumnDO tbMetaDisplayTableColumnDO = tbMetaDisplayTableColumnMap.get(tbDisplayTableDataColumnDO.getMetaColumnId());
            tbDisplayDataColumnVO.setStandardPic(tbMetaDisplayTableColumnDO.getStandardPic());
            tbDisplayDataColumnVO.setDescription(tbMetaDisplayTableColumnDO.getDescription());
            tbDisplayDataColumnVO.setColumnName(tbMetaDisplayTableColumnDO.getColumnName());
            tbDisplayDataColumnVO.setMetaScore(tbMetaDisplayTableColumnDO.getScore());
            tbDisplayDataColumnVO.setOrderNum(tbMetaDisplayTableColumnDO.getOrderNum() == null ? 0 : tbMetaDisplayTableColumnDO.getOrderNum());
            tbDisplayDataColumnVOList.add(tbDisplayDataColumnVO);
        }
        if(CollectionUtils.isNotEmpty(tbDisplayDataColumnVOList)){
            tbDisplayDataColumnVOList.sort(Comparator.comparingInt(TbDisplayTableDataColumnVO::getOrderNum));
        }
        vo.setTbDisplayDataColumnVOList(tbDisplayDataColumnVOList);

        List<TbDisplayHistoryDO> tbDisplayHistoryDOList = tbDisplayHistoryMapper.listByRecordId(enterpriseId, tbDisplayTableRecordDO.getId());
        Map<String, String> approveUserNameMap = new HashMap<>();
        tbDisplayHistoryDOList.forEach(tbDisplayHistoryDO -> {
            approveUserNameMap.put(tbDisplayHistoryDO.getNodeNo(), tbDisplayHistoryDO.getOperateUserName());
        });
        vo.setThirdApproveUserName(approveUserNameMap.get(UnifyNodeEnum.FOUR_NODE.getCode()));
        vo.setFourApproveUserName(approveUserNameMap.get(UnifyNodeEnum.FIVE_NODE.getCode()));
        vo.setFiveApproveUserName(approveUserNameMap.get(UnifyNodeEnum.SIX_NODE.getCode()));
        return vo;
    }

    // 数据报表  按门店  按轮次即按时间
    @Override
    public PageInfo tableRecordReportWithPage(String enterpriseId, TbDisplayReportQueryParam query, CurrentUser user) {
        TaskParentDO taskParentDO = taskParentMapper.selectParentTaskByTaskId(enterpriseId, query.getUnifyTaskId());
        if(taskParentDO == null){
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "未找到对应的父任务【"+query.getUnifyTaskId()+"】");
        }

        PageHelper.startPage(query.getPageNumber(), query.getPageSize());
        List<TbDisplayTaskDataVO> tbDisplayTaskDataVOList = taskParentMapper.getTbDisplayReportPageData(enterpriseId, query);
        if(CollUtil.isEmpty(tbDisplayTaskDataVOList)){
            return new PageInfo(tbDisplayTaskDataVOList);
        }
        Set<String> storeIdSet = tbDisplayTaskDataVOList.stream().map(TbDisplayTaskDataVO::getStoreId).collect(Collectors.toSet());

        Set<Long> unifyTaskIdList = tbDisplayTaskDataVOList.stream().map(TbDisplayTaskDataVO::getUnifyTaskId).collect(Collectors.toSet());
        List<UnifyFormDataDTO> unifyFormDataDTOList = taskMappingMapper.selectMappingData(enterpriseId, new ArrayList<>(unifyTaskIdList));
        List<Long> metaTableIds = unifyFormDataDTOList.stream().map(m -> Long.parseLong(m.getOriginMappingId())).collect(Collectors.toList());
        List<TbMetaDisplayTableColumnDO> tbMetaDisplayTableColumnDOList = tbMetaDisplayTableColumnMapper.listByTableIdList(enterpriseId, metaTableIds);

        List<TaskSubVO>  taskSubVOList = taskSubMapper.getByTaskId(enterpriseId, query.getUnifyTaskId());
        // 每个门店  每个轮次只取一条
        List<TaskSubVO> taskSubVOListNewList = dealAllSubGroup(taskSubVOList);
        Map<String, TaskSubVO> taskSubVOListNewMap = taskSubVOListNewList.stream()
                .collect(Collectors.toMap(k -> k.getUnifyTaskId()+ Constants.MOSAICS +k.getStoreId()+ Constants.MOSAICS +k.getLoopCount(), synOe -> synOe));
        // 获取整体得分，整体评价等信息
        List<TbDisplayTableRecordDO> tbDisplayTableRecordDOList = tbDisplayTableRecordMapper.listByUnifyTaskId(enterpriseId, query.getUnifyTaskId(), null, null);
        Map<String, TbDisplayTableRecordDO> tbDisplayTableRecordDOMap = tbDisplayTableRecordDOList.stream()
                .collect(Collectors.toMap(k -> k.getUnifyTaskId()+k.getStoreId()+k.getLoopCount(), synOe -> synOe));

        // Map:门店id->门店信息（门店名称，门店区域id）
        List<StoreAreaDTO> storeAreaList = storeMapper.getStoreAreaListByStoreIds(enterpriseId, new ArrayList<>(storeIdSet));
        // Map:门店区域id-> 门店区域名称
        Set<String> regionIdSet = storeAreaList.stream().map(m -> String.valueOf(m.getRegionId())).collect(Collectors.toSet());
        Map<String, String> regionMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(regionIdSet)) {
            List<RegionDO> regionList = regionMapper.getRegionByRegionIds(enterpriseId, new ArrayList<>(regionIdSet));
            regionMap.putAll(
                    regionList.stream()
                            .filter(a -> a.getRegionId() != null && a.getName() != null)
                            .collect(Collectors.toMap(RegionDO::getRegionId, RegionDO::getName, (a, b) -> a)));
        }

        List<UnifyPersonDTO> unifyPersonDTOList = unifyTaskStoreService.selectALLNodeUserInfoList(enterpriseId, Collections.singletonList(query.getUnifyTaskId()), null, null);

        Map<String, List<String>> nodePersonMap = unifyPersonDTOList.stream()
                .collect(Collectors.groupingBy(e -> e.getStoreId() + "#" + e.getLoopCount() + "#" + e.getNode() ,
                        Collectors.mapping(s -> s.getUserName(), Collectors.toList())));

        tbDisplayTaskDataVOList.forEach(tbDisplayTaskDataVO -> {
            TaskSubVO taskSubVO = taskSubVOListNewMap.get(tbDisplayTaskDataVO.getUnifyTaskId()+ Constants.MOSAICS +tbDisplayTaskDataVO.getStoreId()+ Constants.MOSAICS +tbDisplayTaskDataVO.getLoopCount());
            TbDisplayTableRecordDO tbDisplayTableRecordDO = tbDisplayTableRecordDOMap.get(tbDisplayTaskDataVO.getUnifyTaskId()+tbDisplayTaskDataVO.getStoreId()+tbDisplayTaskDataVO.getLoopCount());
            tbDisplayTaskDataVO.setStoreAreaName(regionMap.get(tbDisplayTaskDataVO.getRegionId()));
            tbDisplayTaskDataVO.setMetaColumnNum(tbMetaDisplayTableColumnDOList.size());
            tbDisplayTaskDataVO.setHandleUserName(StringUtils.join(nodePersonMap.get(tbDisplayTaskDataVO.getStoreId() + "#" + tbDisplayTaskDataVO.getLoopCount() + "#" + UnifyNodeEnum.FIRST_NODE.getCode()), ","));
            tbDisplayTaskDataVO.setApproveUserName(StringUtils.join(nodePersonMap.get(tbDisplayTaskDataVO.getStoreId() + "#" + tbDisplayTaskDataVO.getLoopCount() + "#" +UnifyNodeEnum.SECOND_NODE.getCode()), ","));
            tbDisplayTaskDataVO.setRecheckUserName(StringUtils.join(nodePersonMap.get(tbDisplayTaskDataVO.getStoreId() + "#" + tbDisplayTaskDataVO.getLoopCount() + "#" +UnifyNodeEnum.THIRD_NODE.getCode()), ","));
            if(tbDisplayTableRecordDO != null){
                tbDisplayTaskDataVO.setRemark(tbDisplayTableRecordDO.getRemark());
                tbDisplayTaskDataVO.setScore(tbDisplayTableRecordDO.getScore());
            }
            String subStatus = taskSubVO.getSubStatus();
            tbDisplayTaskDataVO.setStatus(TaskStatusEnum.getByCode(subStatus.toUpperCase()).getDesc());
            if (TaskStatusEnum.COMPLETE.getCode().toLowerCase().equals(subStatus)) {
                Long handleTime = taskSubVO.getHandleTime();
                Long createTime = taskSubVO.getCreateTime();
                Long endTime = taskSubVO.getSubEndTime();
                // 结束时间
                tbDisplayTaskDataVO.setDoneTime(new Date(handleTime));
                // 检查时长
                double hour = (double)(handleTime - createTime) / 1000 / 60 / 60;
                String checkTime = String.format("%.2f", hour) + "小时";
                if (hour < 1) {
                    double minute = (double)(handleTime - createTime) / 1000 / 60;
                    checkTime = String.format("%.2f", minute) + "分钟";
                }
                tbDisplayTaskDataVO.setCheckTime(checkTime);
                // 是否过期完成
                tbDisplayTaskDataVO.setOverdue(handleTime > endTime ? "已过期" : "未过期");
            }

        });
        PageInfo pageInfo = new PageInfo(tbDisplayTaskDataVOList);
        pageInfo.setList(tbDisplayTaskDataVOList);
        return pageInfo;
    }

    @Override
    public List<TbDisplayTaskDataVO> tableRecordReportExport(String enterpriseId, TbDisplayReportQueryParam query, CurrentUser user) {
        List<TbDisplayTaskDataVO> tbDisplayTaskDataVOList = taskParentMapper.getTbDisplayReportPageData(enterpriseId, query);
        if(CollUtil.isEmpty(tbDisplayTaskDataVOList)){
            return Lists.newArrayList();
        }
        Set<String> storeIdSet = tbDisplayTaskDataVOList.stream().map(TbDisplayTaskDataVO::getStoreId).collect(Collectors.toSet());

        Set<Long> unifyTaskIdList = tbDisplayTaskDataVOList.stream().map(TbDisplayTaskDataVO::getUnifyTaskId).collect(Collectors.toSet());
        List<UnifyFormDataDTO> unifyFormDataDTOList = taskMappingMapper.selectMappingData(enterpriseId, new ArrayList<>(unifyTaskIdList));
        List<Long> metaTableIds = unifyFormDataDTOList.stream().map(m -> Long.parseLong(m.getOriginMappingId())).collect(Collectors.toList());
        List<TbMetaDisplayTableColumnDO> tbMetaDisplayTableColumnDOList = tbMetaDisplayTableColumnMapper.listByTableIdList(enterpriseId, metaTableIds);

        List<TaskSubVO>  taskSubVOList = taskSubMapper.getByTaskId(enterpriseId, query.getUnifyTaskId());
        // 每个门店  每个轮次只取一条
        List<TaskSubVO> taskSubVOListNewList = dealAllSubGroup(taskSubVOList);
        Map<String, TaskSubVO> taskSubVOListNewMap = taskSubVOListNewList.stream()
                .collect(Collectors.toMap(k -> k.getUnifyTaskId()+ Constants.MOSAICS +k.getStoreId()+ Constants.MOSAICS +k.getLoopCount(), synOe -> synOe));
        // 获取整体得分，整体评价等信息
        List<TbDisplayTableRecordDO> tbDisplayTableRecordDOList = tbDisplayTableRecordMapper.listByUnifyTaskId(enterpriseId, query.getUnifyTaskId(), null, null);
        List<Long> recordList = tbDisplayTableRecordDOList.stream().map(TbDisplayTableRecordDO::getId).collect(Collectors.toList());
        List<TbDisplayTableDataColumnDO> tbDisplayTableDataColumnDOList = tbDisplayTableDataColumnMapper.listByRecordIdList(enterpriseId, recordList, null);
        Map<Long, List<TbDisplayTableDataColumnDO>> tbDisplayTableDataColumnDOMap = tbDisplayTableDataColumnDOList.stream()
                .collect(Collectors.groupingBy(TbDisplayTableDataColumnDO::getRecordId));

        Map<String, TbDisplayTableRecordDO> tbDisplayTableRecordDOMap = tbDisplayTableRecordDOList.stream()
                .collect(Collectors.toMap(k -> k.getUnifyTaskId()+k.getStoreId()+k.getLoopCount(), synOe -> synOe));

        // Map:门店id->门店信息（门店名称，门店区域id）
        List<StoreAreaDTO> storeAreaList = storeMapper.getStoreAreaListByStoreIds(enterpriseId, new ArrayList<>(storeIdSet));
        // Map:门店区域id->门店区域名称
        Set<String> regionIdSet = storeAreaList.stream().map(m -> String.valueOf(m.getRegionId())).collect(Collectors.toSet());
        Map<String, String> regionMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(regionIdSet)) {
            List<RegionDO> regionList = regionMapper.getRegionByRegionIds(enterpriseId, new ArrayList<>(regionIdSet));
            regionMap.putAll(
                    regionList.stream()
                            .filter(a -> a.getRegionId() != null && a.getName() != null)
                            .collect(Collectors.toMap(RegionDO::getRegionId, RegionDO::getName, (a, b) -> a)));
        }

        List<UnifyPersonDTO> unifyPersonDTOList =unifyTaskStoreService.selectALLNodeUserInfoList(enterpriseId, Collections.singletonList(query.getUnifyTaskId()), null, null);

        Map<String, List<String>> nodePersonMap = unifyPersonDTOList.stream()
                .collect(Collectors.groupingBy(e -> e.getStoreId() + "#" + e.getLoopCount() + "#" + e.getNode() ,
                        Collectors.mapping(s -> s.getUserName(), Collectors.toList())));

        tbDisplayTaskDataVOList.forEach(tbDisplayTaskDataVO -> {
            TaskSubVO taskSubVO = taskSubVOListNewMap.get(tbDisplayTaskDataVO.getUnifyTaskId()+ Constants.MOSAICS +tbDisplayTaskDataVO.getStoreId()+ Constants.MOSAICS +tbDisplayTaskDataVO.getLoopCount());
            TbDisplayTableRecordDO tbDisplayTableRecordDO = tbDisplayTableRecordDOMap.get(tbDisplayTaskDataVO.getUnifyTaskId()+tbDisplayTaskDataVO.getStoreId()+tbDisplayTaskDataVO.getLoopCount());
            tbDisplayTaskDataVO.setStoreAreaName(regionMap.get(tbDisplayTaskDataVO.getRegionId()));
            tbDisplayTaskDataVO.setMetaColumnNum(tbMetaDisplayTableColumnDOList.size());

            tbDisplayTaskDataVO.setHandleUserName(StringUtils.join(nodePersonMap.get(tbDisplayTaskDataVO.getStoreId() + "#" + tbDisplayTaskDataVO.getLoopCount() + "#" + UnifyNodeEnum.FIRST_NODE.getCode()), ","));
            tbDisplayTaskDataVO.setApproveUserName(StringUtils.join(nodePersonMap.get(tbDisplayTaskDataVO.getStoreId() + "#" + tbDisplayTaskDataVO.getLoopCount() + "#" +UnifyNodeEnum.SECOND_NODE.getCode()), ","));
            tbDisplayTaskDataVO.setRecheckUserName(StringUtils.join(nodePersonMap.get(tbDisplayTaskDataVO.getStoreId() + "#" + tbDisplayTaskDataVO.getLoopCount() + "#" +UnifyNodeEnum.THIRD_NODE.getCode()), ","));

            if(tbDisplayTableRecordDO != null){
                tbDisplayTaskDataVO.setRemark(tbDisplayTableRecordDO.getRemark());
                tbDisplayTaskDataVO.setScore(tbDisplayTableRecordDO.getScore());
                List<TbDisplayTableDataColumnDO> tbDisplayTableDataColumnDOS = tbDisplayTableDataColumnDOMap.get(tbDisplayTableRecordDO.getId());
                tbDisplayTaskDataVO.setPicList(getHandleUrlByPhotoArray(tbDisplayTableDataColumnDOS));
            }

            String subStatus = taskSubVO.getSubStatus();
            tbDisplayTaskDataVO.setStatus(TaskStatusEnum.getByCode(subStatus.toUpperCase()).getDesc());

            if (TaskStatusEnum.COMPLETE.getCode().toLowerCase().equals(subStatus)) {
                Long handleTime = taskSubVO.getHandleTime();
                Long createTime = taskSubVO.getCreateTime();
                Long endTime = taskSubVO.getSubEndTime();
                // 结束时间
                tbDisplayTaskDataVO.setDoneTime(new Date(handleTime));
                // 检查时长
                double hour = (double)(handleTime - createTime) / 1000 / 60 / 60;
                String checkTime = String.format("%.2f", hour) + "小时";
                if (hour < 1) {
                    double minute = (double)(handleTime - createTime) / 1000 / 60;
                    checkTime = String.format("%.2f", minute) + "分钟";
                }
                tbDisplayTaskDataVO.setCheckTime(checkTime);
                // 是否过期完成
                tbDisplayTaskDataVO.setOverdue(handleTime > endTime ? "已过期" : "未过期");
            }

        });
        return tbDisplayTaskDataVOList;
    }

    @Override
    @AsyncDynamicExport(type = ImportTaskConstant.TB_DISPLAY_SUB_DETAIL)
    public Object displayHasPic(String enterpriseId, TbDisplayReportQueryParam query, CurrentUser user) {
        List<TbDisplayTaskDataVO> tbdisplay = tableRecordReportExport(enterpriseId, query, user);
        // 获取动态数据
        List<List<Object>> data = TbDisplayDynamicExcelUtil.displayData(tbdisplay);
        // 获取excel表头
        List<List<String>> head = TbDisplayDynamicExcelUtil.displayHead();
        TbDisplayDynamicExcelUtil.expansionHead(head, data);

        return TbDisplayDynamicExcelUtil.getDataByte("陈列记录详情", head, data);
    }


    /**
     * 单个任务审核
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void approve(String enterpriseId, CurrentUser user, TbApproveDisplayTaskParam tbApproveDisplayTaskParam) {

        String actionKey = tbApproveDisplayTaskParam.getActionKey();


        if (StringUtils.isNotBlank(actionKey) && !TbDisplayConstant.ActionKeyConstant.ACTION_KEY_SET.contains(actionKey)) {
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "审核类型参数有误");
        }

        Long subTaskId = tbApproveDisplayTaskParam.getSubTaskId();
        //审批任务
        TaskSubDO taskSub = taskSubMapper.selectSubTaskById(enterpriseId, subTaskId);
        if (taskSub == null) {
            log.info("审批任务不存在，不能进行审核，subTaskID:{} " , subTaskId);
            throw new ServiceException(ErrorCodeEnum.DATA_ERROR.getCode(), "审批任务不存在，不能进行审核");
        }
        Long unifyTaskId = taskSub.getUnifyTaskId();
        TaskParentDO parentDO = unifyTaskParentService.getTaskParentDOById(enterpriseId, unifyTaskId);
        if(Objects.nonNull(parentDO) && Constants.INDEX_ZERO.equals(parentDO.getStatusType())){
            log.info("任务已停止");
            throw new ServiceException(ErrorCodeEnum.TASK_IS_STOP);
        }
        //只能审核自己的任务
        if(!user.getUserId().equals(taskSub.getHandleUserId())){
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "只能审核自己的审批任务");
        }

        Boolean flag = workflowService.subSubmitCheck(taskSub);
        if (flag == null || !flag) {
            log.info("该任务已被其他人操作，subTaskId:" + tbApproveDisplayTaskParam.getSubTaskId());
            throw new ServiceException(ErrorCodeEnum.DATA_ERROR.getCode(), "该任务已被其他人操作");
        }

        TbDisplayTableRecordDO record = tbDisplayTableRecordMapper.getByUnifyTaskIdAndStoreIdAndLoopCount(enterpriseId, taskSub.getUnifyTaskId(), taskSub.getStoreId(), taskSub.getLoopCount());
        if (record == null) {
            log.info("该记录不存在:");
            throw new ServiceException(ErrorCodeEnum.DATA_ERROR.getCode(), "该记录不存在");
        }
        //如果总评分，则不能超过上限
//        if (tbApproveDisplayTaskParam.getScore() != null && tbApproveDisplayTaskParam.getScore() > HIGH_TOTAL_SCORE) {
//            log.info("该记录不存在:");
//            throw new ServiceException(ErrorCodeEnum.DATA_ERROR.getCode(), "该记录不存在");
//        }
        BigDecimal totalScore = new BigDecimal(Constants.ZERO_STR);
        if(!record.getIsSupportScore()){
            //总评分 不支持分数默认
            totalScore = new BigDecimal(DEFAULT_TOTAL_SCORE);
        }


        Long recordId = record.getId();

        // 发送流程引擎
        String bizCode;
        UnifyNodeEnum unifyNodeEnum = UnifyNodeEnum.getByCode(taskSub.getNodeNo());

        if (unifyNodeEnum == null) {
            log.info("unifyNodeEnum审批节点不存在，recordId:" + recordId);
            throw new ServiceException(ErrorCodeEnum.DATA_ERROR.getCode(), "审批节点不存在");
        }
        String approveUserId = null;
        String approveUserName = null;
        String recheckUserId = null;
        String recheckUserName = null;

        String operateType;
        String redisKeyPrefix;
        Date firstApproveTime = null;
        switch (unifyNodeEnum) {
            case SECOND_NODE:
                bizCode = TbDisplayConstant.BizCodeConstant.DISPLAY_APPROVE;
                operateType = TbDisplayConstant.TbDisplayRecordStatusConstant.APPROVE;
                redisKeyPrefix = RedisConstant.TB_DISPLAY_APPROVE;
                approveUserId = user.getUserId();
                approveUserName = user.getName();
                break;
            case THIRD_NODE:
                bizCode = TbDisplayConstant.BizCodeConstant.DISPLAY_RECHECK;
                operateType = TbDisplayConstant.TbDisplayRecordStatusConstant.RECHECK;
                redisKeyPrefix = RedisConstant.TB_DISPLAY_RECHECK;
                recheckUserId = user.getUserId();
                recheckUserName = user.getName();
                break;
            case FOUR_NODE:
                bizCode = TbDisplayConstant.BizCodeConstant.DISPLAY_THIRD_APPROVE;
                operateType = TbDisplayConstant.TbDisplayRecordStatusConstant.THIRD_APPROVE;
                redisKeyPrefix = RedisConstant.TB_DISPLAY_THIRD_APPROVE;
                break;
            case FIVE_NODE:
                bizCode = TbDisplayConstant.BizCodeConstant.DISPLAY_FOUR_APPROVE;
                operateType = TbDisplayConstant.TbDisplayRecordStatusConstant.FOUR_APPROVE;
                redisKeyPrefix = RedisConstant.TB_DISPLAY_FOUR_APPROVE;
                break;
            case SIX_NODE:
                bizCode = TbDisplayConstant.BizCodeConstant.DISPLAY_FIVE_APPROVE;
                operateType = TbDisplayConstant.TbDisplayRecordStatusConstant.FIVE_APPROVE;
                redisKeyPrefix = RedisConstant.TB_DISPLAY_FIVE_APPROVE;
                break;
            default:
                throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "审核类型参数有误");
        }
        //当前审核人，审核记录
        TbDisplayHistoryDO historyDO = tbDisplayHistoryMapper.selectDisplayHistory(enterpriseId, recordId, operateType, user.getUserId(), subTaskId);

        if (historyDO != null && historyDO.getIsValid()) {
            log.info("该记录已提交审核，不能再次修改，recordId:" + recordId);
            throw new ServiceException(ErrorCodeEnum.DATA_ERROR.getCode(), "该记录已提交审核，不能再次修改");
        }
        String taskKey = redisKeyPrefix + "_" + enterpriseId + "_" + taskSub.getUnifyTaskId() + "_" + taskSub.getStoreId() + "_" + taskSub.getLoopCount();
        //加两分钟防止重复审核
        if(StringUtils.isNotBlank(redisUtilPool.getString(taskKey))){
            log.info("该任务已被其他人操作，subTaskId:" + tbApproveDisplayTaskParam.getSubTaskId());
            throw new ServiceException(ErrorCodeEnum.DATA_ERROR.getCode(), "该任务已被其他人操作");
        }
        redisUtilPool.setString(taskKey, user.getUserId(), 10);

        if(historyDO == null){
            //新增审核记录
            historyDO = new TbDisplayHistoryDO();
            historyDO.setIsValid(true);
            historyDO.setOperateType(operateType);
            historyDO.setOperateUserId(user.getUserId());
            historyDO.setOperateUserName(user.getName());
            historyDO.setSubTaskId(tbApproveDisplayTaskParam.getSubTaskId());
            historyDO.setActionKey(actionKey == null ? "" : actionKey);
            historyDO.setCreateTime(new Date());
            historyDO.setRecordId(recordId);
            historyDO.setNodeNo(taskSub.getNodeNo());
            historyDO.setRemark(tbApproveDisplayTaskParam.getRemark());
            JSONObject extendInfo = new JSONObject();
            extendInfo.put(CommonConstant.ExtendInfo.DISPLAY_APPROVE_IMAGE_LIST, tbApproveDisplayTaskParam.getApproveImageList());
            historyDO.setExtendInfo(JSONObject.toJSONString(extendInfo));
            tbDisplayHistoryMapper.insert(enterpriseId, historyDO);
        }


        //结果项(根据巡店id查询所有的 datacolumn之前一轮的陈列检查项数据)
        List<TbDisplayTableDataColumnDO> dataColumnDOList = tbDisplayTableDataColumnMapper.listByRecordId(enterpriseId, recordId);
        //审核检查项(提交的检查项)
        List<TbDisplayApprovePhotoParam> approveItemList = tbApproveDisplayTaskParam.getApproveItemList();

        //审核项(第一次的审批处理历史数据)
        List<TbDisplayHistoryColumnDO> tbDisplayHistoryColumnOldList = tbDisplayHistoryColumnMapper.getListByHistoryId(enterpriseId, Collections.singletonList(historyDO.getId()));

        //检查项
        List<TbDisplayHistoryColumnDO> historyColumnList = tbDisplayHistoryColumnOldList.stream().filter(a -> a.getCheckType().equals(Constants.INDEX_ZERO)).collect(Collectors.toList());

        //检查内容
        List<TbDisplayHistoryColumnDO> historyContentList = tbDisplayHistoryColumnOldList.stream().filter(a -> a.getCheckType().equals(Constants.INDEX_ONE)).collect(Collectors.toList());

        //历史记录 第一次审批历史记录按检查项id分组
        Map<Long, TbDisplayHistoryColumnDO> historyColumnMaps = historyColumnList.stream().collect(Collectors.toMap(TbDisplayHistoryColumnDO::getDataColumnId,Function.identity()));

        //历史记录
        Map<Long, TbDisplayHistoryColumnDO> historyContentMaps = historyContentList.stream().collect(Collectors.toMap(TbDisplayHistoryColumnDO::getDataColumnId,Function.identity()));


        //如果检查内容不为null，将检查内容放到历史表中
        List<TbDisplayApproveContentParam> approveContentList = tbApproveDisplayTaskParam.getApproveContentList();
        log.info("tbDisplayTableRecordServiceImpl.approveContentList:{}",JSON.toJSONString(approveContentList));
        if (approveContentList!=null){
            List<TbDisplayTableDataContentDO> dataContentDOList = tbDisplayTableDataContentMapper.listByRecordId(enterpriseId, recordId);
            log.info("tbDisplayTableRecordServiceImpl.dataContentDOList:{}",JSON.toJSONString(approveContentList));
            Map<Long, TbDisplayApproveContentParam> approvePhotoContentMaps = approveContentList.stream().collect(Collectors.toMap(TbDisplayApproveContentParam::getDataContentId, Function.identity()));

            for(TbDisplayTableDataContentDO dataContent : dataContentDOList){
                TbDisplayHistoryColumnDO historyContentDO = historyContentMaps.get(dataContent.getId());
                TbDisplayApproveContentParam tbDisplayApproveContentParam = approvePhotoContentMaps.get(dataContent.getId());

                if (tbDisplayApproveContentParam!=null){
                TbDisplayHistoryColumnDO historyContentNew = TbDisplayHistoryColumnDO.builder().dataColumnId(dataContent.getId()).deleted(false)
                        .recordId(recordId).historyId(historyDO.getId()).metaColumnId(dataContent.getMetaContentId())
                        .operateType(operateType).operateUserId(user.getUserId()).operateUserName(user.getName()).checkType(1).build();
                    historyContentNew.setPhotoArray(tbDisplayApproveContentParam.getPhotoArray());
                    historyContentNew.setRemark(tbDisplayApproveContentParam.getRemark());
                    historyContentNew.setScore(new BigDecimal(Constants.ZERO_STR));
                    historyContentNew.setIsTemp(Constants.INDEX_ZERO);

                String differentPhoto = getDifferentContentPhoto(dataContent.getPhotoArray(), tbDisplayApproveContentParam, historyContentDO);
                historyContentNew.setPhoto(differentPhoto);

                //批量审批的时候 actionKey根据differentPhoto来判断 如果differentPhoto!=null 则是拒绝，否则是通过
                if(StringUtils.isBlank(actionKey) && StringUtils.isNotBlank(differentPhoto)){
                    actionKey = TbDisplayConstant.ActionKeyConstant.REJECT;
                }
                if(historyContentDO != null){
                    //更新历史表
                    historyContentNew.setId(historyContentDO.getId());
                    tbDisplayHistoryColumnMapper.updateByPrimaryKeySelective(enterpriseId, historyContentNew);
                }else {
                    //插入数据到历史表
                    tbDisplayHistoryColumnMapper.insertSelective(enterpriseId, historyContentNew);
                }
                TbDisplayTableDataContentDO tableContentDO = new TbDisplayTableDataContentDO();
                tableContentDO.setId(dataContent.getId());
                tableContentDO.setPhotoArray(tbDisplayApproveContentParam.getPhotoArray());
                tableContentDO.setScore(new BigDecimal(Constants.ZERO_STR));
                //数据存储同步到content表中
                tbDisplayTableDataContentMapper.updateByPrimaryKeySelective(enterpriseId, tableContentDO);
                }
            }
        }
        //审核传入修改项
        Map<Long, TbDisplayApprovePhotoParam> approvePhotoMaps = new HashMap<>();
        if(CollectionUtils.isNotEmpty(approveItemList)){
            //每一项
            approvePhotoMaps = approveItemList.stream().collect(Collectors.toMap(TbDisplayApprovePhotoParam::getDataColumnId,Function.identity()));
        }

        for(TbDisplayTableDataColumnDO dataColumn : dataColumnDOList){
            //传入审核项
            TbDisplayApprovePhotoParam photoParam = approvePhotoMaps.get(dataColumn.getId());

            //已有审核记录
            TbDisplayHistoryColumnDO historyColumnDO = historyColumnMaps.get(dataColumn.getId());
            //计算默认分
            BigDecimal dataColumnScoreDefault = dataColumn.getScore();
            BigDecimal score = new BigDecimal(Constants.ZERO_STR);
            //计算总分(没有总评分则计算单项评分)
            if (record.getIsSupportScore()) {
                if (photoParam != null) {
                    score = (photoParam.getScore() == null ? dataColumnScoreDefault : photoParam.getScore());
                } else if (historyColumnDO != null && historyColumnDO.getScore() != null) {
                    score = (historyColumnDO.getScore() == null ? dataColumnScoreDefault : historyColumnDO.getScore());
                } else {
                    score = dataColumnScoreDefault;
                }
                //单项评分不能超过最大值
//                if(score > HIGH_SCORE){
//                    log.info("任务不支持单项评分，recordId:" + recordId);
//                    redisUtilPool.delKey(taskKey);
//                    throw new ServiceException(ErrorCodeEnum.DATA_ERROR.getCode(), "该任务单项评分不能超过" + HIGH_SCORE);
//                }
                totalScore = totalScore.add(score);
            }else {
                if (photoParam != null && photoParam.getTotalScore() != null) {
                    totalScore = photoParam.getTotalScore();
                }
            }

            TbDisplayHistoryColumnDO historyColumnNew = TbDisplayHistoryColumnDO.builder().dataColumnId(dataColumn.getId()).deleted(false)
                    .recordId(recordId).historyId(historyDO.getId()).metaColumnId(dataColumn.getMetaColumnId())
                    .operateType(operateType).operateUserId(user.getUserId()).operateUserName(user.getName()).build();


            String differentPhoto = getDifferentPhoto(dataColumn.getPhotoArray(), photoParam, historyColumnDO);

            if(photoParam != null){
                historyColumnNew.setPhotoArray(photoParam.getPhotoArray());
                historyColumnNew.setRemark(photoParam.getRemark());
            }

            if(historyColumnDO != null){
                historyColumnNew.setScore(score);
                historyColumnNew.setIsTemp(Constants.INDEX_ZERO);
                historyColumnNew.setId(historyColumnDO.getId());
                historyColumnNew.setPhoto(differentPhoto);
                tbDisplayHistoryColumnMapper.updateByPrimaryKeySelective(enterpriseId, historyColumnNew);
            }else {
                if(photoParam == null){
                    historyColumnNew.setPhotoArray(dataColumn.getPhotoArray());
                    historyColumnNew.setRemark(dataColumn.getRemark());
                }
                historyColumnNew.setScore(score);
                historyColumnNew.setIsTemp(Constants.INDEX_ZERO);

                historyColumnNew.setPhoto(differentPhoto);
                tbDisplayHistoryColumnMapper.insertSelective(enterpriseId, historyColumnNew);
            }

            if(StringUtils.isBlank(actionKey) && StringUtils.isNotBlank(differentPhoto)){
                actionKey = TbDisplayConstant.ActionKeyConstant.REJECT;
            }

            //计算拒绝状态
            if(photoParam != null){
                TbDisplayTableDataColumnDO tableColumnDO = new TbDisplayTableDataColumnDO();

                tableColumnDO.setId(dataColumn.getId());
                tableColumnDO.setPhotoArray(photoParam.getPhotoArray());
                tableColumnDO.setScore(photoParam.getScore());
                tbDisplayTableDataColumnMapper.updateByPrimaryKeySelective(enterpriseId, tableColumnDO);
            }else if(historyColumnDO != null){
                TbDisplayTableDataColumnDO tableColumnDO = new TbDisplayTableDataColumnDO();
                tableColumnDO.setId(dataColumn.getId());
                tableColumnDO.setPhotoArray(historyColumnDO.getPhotoArray());
                tableColumnDO.setScore(historyColumnDO.getScore());
                tbDisplayTableDataColumnMapper.updateByPrimaryKeySelective(enterpriseId, tableColumnDO);
            }
        }
        //设置actionKey
        if(StringUtils.isBlank(actionKey)){
            actionKey = TbDisplayConstant.ActionKeyConstant.PASS;
        }

        //总得分
        totalScore = tbApproveDisplayTaskParam.getScore() == null ? totalScore : tbApproveDisplayTaskParam.getScore();

        //修改审核记录
        tbDisplayHistoryMapper.updateByPrimaryKeySelective(enterpriseId, TbDisplayHistoryDO.builder()
                .id(historyDO.getId()).score(totalScore).isValid(true).operateType(operateType).actionKey(actionKey)
                .remark(tbApproveDisplayTaskParam.getRemark()).build());

        //更新为处理状态
        tbDisplayTableRecordMapper.updateByPrimaryKeySelective(enterpriseId,
                TbDisplayTableRecordDO.builder().id(record.getId()).score(totalScore)
                        .approveUserId(approveUserId).approveUserName(approveUserName)
                        .recheckUserId(recheckUserId).recheckUserName(recheckUserName)
                        .remark(tbApproveDisplayTaskParam.getRemark()).status(operateType).latestApproveTime(new Date()).build());

        //同一批次同一节点的同一的必是已完成
        TaskSubDO queryDO = new TaskSubDO(taskSub.getUnifyTaskId(), taskSub.getStoreId(), taskSub.getNodeNo(),
                taskSub.getGroupItem(), taskSub.getLoopCount());
        TaskSubDO updateDO = TaskSubDO.builder()
                .subStatus(UnifyStatus.COMPLETE.getCode())
                .build();
        taskSubMapper.updateSubDetailExclude(enterpriseId, queryDO, updateDO, taskSub.getId());

        //发送消息处理
        WorkflowDataDTO workflowDataDTO = workflowService.getFlowJsonObject(enterpriseId, historyDO.getId(), taskSub,
                bizCode, actionKey, null, user.getUserId(), null, null, null);
        mqMessageDAO.addMessage(enterpriseId, workflowDataDTO.getPrimaryKey(), taskSub.getId(), JSONObject.toJSONString(workflowDataDTO));
        simpleMessageService.send(JSONObject.toJSONString(workflowDataDTO), RocketMqTagEnum.WORKFLOW_SEND_TOPIC);
        //记录任务状态，处理中
        String taskStatusKey = RedisConstant.TASK_STATUS_KEY + enterpriseId + Constants.COLON + taskSub.getUnifyTaskId() + "_" + taskSub.getStoreId() + "_" + taskSub.getLoopCount();
        redisUtilPool.setString(taskStatusKey, "1", 5);

    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void score(String enterpriseId, CurrentUser user, TbApproveDisplayTaskParam tbApproveDisplayTaskParam) {

        Long subTaskId = tbApproveDisplayTaskParam.getSubTaskId();
        //审批任务
        TaskSubDO taskSub = taskSubMapper.selectSubTaskById(enterpriseId, subTaskId);

        if (taskSub == null) {
            log.info("审批任务不存在，不能进行审核，subTaskID:{} " , subTaskId);
            throw new ServiceException(ErrorCodeEnum.DATA_ERROR.getCode(), "审批任务不存在，不能进行审核");
        }
        Boolean flag = workflowService.subSubmitCheck(taskSub);
        if (flag == null || !flag) {
            log.info("该任务已被其他人操作，subTaskId:" + tbApproveDisplayTaskParam.getSubTaskId());
            throw new ServiceException(ErrorCodeEnum.DATA_ERROR.getCode(), "该任务已被其他人操作");
        }

        TbDisplayTableRecordDO record = tbDisplayTableRecordMapper.getByUnifyTaskIdAndStoreIdAndLoopCount(enterpriseId, taskSub.getUnifyTaskId(), taskSub.getStoreId(), taskSub.getLoopCount());

        if (record == null) {
            log.info("该记录不存在:");
            throw new ServiceException(ErrorCodeEnum.DATA_ERROR.getCode(), "该记录不存在");
        }
        Long recordId = record.getId();

        UnifyNodeEnum unifyNodeEnum = UnifyNodeEnum.getByCode(taskSub.getNodeNo());

        if (unifyNodeEnum == null) {
            log.info("unifyNodeEnum审批节点不存在，recordId:" + recordId);
            throw new ServiceException(ErrorCodeEnum.DATA_ERROR.getCode(), "审批节点不存在");
        }
        String operateType = null;
        switch (unifyNodeEnum) {
            case SECOND_NODE:
                operateType = TbDisplayConstant.TbDisplayRecordStatusConstant.APPROVE;
                break;
            case THIRD_NODE:
                operateType = TbDisplayConstant.TbDisplayRecordStatusConstant.RECHECK;
                break;
            case FOUR_NODE:
                operateType = TbDisplayConstant.TbDisplayRecordStatusConstant.THIRD_APPROVE;
                break;
            case FIVE_NODE:
                operateType = TbDisplayConstant.TbDisplayRecordStatusConstant.FOUR_APPROVE;
                break;
            case SIX_NODE:
                operateType = TbDisplayConstant.TbDisplayRecordStatusConstant.FIVE_APPROVE;
                break;
            default:
                throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "审核类型参数有误");
        }

        //当前审核人，审核记录
        TbDisplayHistoryDO historyDO = tbDisplayHistoryMapper.selectDisplayHistory(enterpriseId, recordId, operateType, user.getUserId(), subTaskId);

        if(historyDO == null) {
            //新增审核记录
            historyDO = new TbDisplayHistoryDO();
            historyDO.setIsValid(false);
            historyDO.setOperateType(operateType);
            historyDO.setOperateUserId(user.getUserId());
            historyDO.setOperateUserName(user.getName());
            historyDO.setSubTaskId(tbApproveDisplayTaskParam.getSubTaskId());
            historyDO.setActionKey("");
            historyDO.setCreateTime(new Date());
            historyDO.setRecordId(recordId);
            historyDO.setNodeNo(taskSub.getNodeNo());
            tbDisplayHistoryMapper.insert(enterpriseId, historyDO);
        }
        //审核 检查项+检查内容
        List<TbDisplayHistoryColumnDO> tbDisplayHistoryColumnOldList = tbDisplayHistoryColumnMapper.getListByHistoryId(enterpriseId, Collections.singletonList(historyDO.getId()));

        //将检查项与检查内容区分开
        //检查项
        List<TbDisplayHistoryColumnDO> historyColumnList = tbDisplayHistoryColumnOldList.stream().filter(a -> a.getCheckType().equals(Constants.INDEX_ZERO)).collect(Collectors.toList());

        //检查内容
        List<TbDisplayHistoryColumnDO> historyContentList = tbDisplayHistoryColumnOldList.stream().filter(a -> a.getCheckType().equals(Constants.INDEX_ONE)).collect(Collectors.toList());

        //历史记录 转map 检查项maps
        Map<Long, TbDisplayHistoryColumnDO> historyColumnMaps = historyColumnList.stream().collect(Collectors.toMap(TbDisplayHistoryColumnDO::getDataColumnId,Function.identity()));
        //历史记录 转map  检查内容maps
        Map<Long, TbDisplayHistoryColumnDO> historyContentMaps = historyContentList.stream().collect(Collectors.toMap(TbDisplayHistoryColumnDO::getDataColumnId,Function.identity()));

        //结果项
        List<TbDisplayTableDataColumnDO> dataColumnDOList = tbDisplayTableDataColumnMapper.listByRecordId(enterpriseId, recordId);
        Map<Long, TbDisplayTableDataColumnDO> dataColumnListMaps = dataColumnDOList.stream().collect(Collectors.toMap(TbDisplayTableDataColumnDO::getId,Function.identity()));

        //处理检查内容
        List<TbDisplayApproveContentParam> approveContentList = tbApproveDisplayTaskParam.getApproveContentList();
        log.info("tbDisplayTableRecordServiceImpl.approveContentList:{}",JSON.toJSONString(approveContentList));
        if (approveContentList!=null){
            List<TbDisplayTableDataContentDO> dataContentDOList = tbDisplayTableDataContentMapper.listByRecordId(enterpriseId, recordId);
            log.info("tbDisplayTableRecordServiceImpl.dataContentDOList:{}",JSON.toJSONString(approveContentList));
            Map<Long, TbDisplayApproveContentParam> approvePhotoContentMaps = approveContentList.stream().collect(Collectors.toMap(TbDisplayApproveContentParam::getDataContentId, Function.identity()));

            for(TbDisplayTableDataContentDO dataContent : dataContentDOList){
                TbDisplayHistoryColumnDO historyColumnDO = historyContentMaps.get(dataContent.getId());
                TbDisplayApproveContentParam tbDisplayApproveContentParam = approvePhotoContentMaps.get(dataContent.getId());
                if (tbDisplayApproveContentParam!=null){
                    TbDisplayHistoryColumnDO historyContentNew = TbDisplayHistoryColumnDO.builder().dataColumnId(dataContent.getId()).deleted(false)
                            .recordId(recordId).historyId(historyDO.getId()).metaColumnId(dataContent.getMetaContentId())
                            .operateType(operateType).operateUserId(user.getUserId()).operateUserName(user.getName()).checkType(1).build();
                    historyContentNew.setPhotoArray(tbDisplayApproveContentParam.getPhotoArray());
                    historyContentNew.setRemark(tbDisplayApproveContentParam.getRemark());
                    historyContentNew.setScore(new BigDecimal(Constants.ZERO_STR));
                    historyContentNew.setIsTemp(Constants.INDEX_ONE);
                    if(historyColumnDO != null){
                        historyContentNew.setId(historyColumnDO.getId());
                        tbDisplayHistoryColumnMapper.updateByPrimaryKeySelective(enterpriseId, historyContentNew);
                    }else {
                        //插入数据到历史表
                        tbDisplayHistoryColumnMapper.insertSelective(enterpriseId, historyContentNew);
                    }
                }
            }
        }

        //查询修改检查项
        if(CollectionUtils.isNotEmpty(tbApproveDisplayTaskParam.getApproveItemList())){
            for(TbDisplayApprovePhotoParam photoParam : tbApproveDisplayTaskParam.getApproveItemList()){
                //单项评分不能超过最大值
//                if(photoParam.getScore() != null && photoParam.getScore() > HIGH_SCORE){
//                    throw new ServiceException(ErrorCodeEnum.DATA_ERROR.getCode(), "该任务单项评分不能超过" + HIGH_SCORE);
//                }

                TbDisplayHistoryColumnDO historyColumnDO = historyColumnMaps.get(photoParam.getDataColumnId());
                TbDisplayTableDataColumnDO dataColumn = dataColumnListMaps.get(photoParam.getDataColumnId());
                TbDisplayHistoryColumnDO historyColumnNew = TbDisplayHistoryColumnDO.builder().dataColumnId(photoParam.getDataColumnId()).deleted(false)
                        .recordId(recordId).historyId(historyDO.getId()).metaColumnId(dataColumn.getMetaColumnId())
                        .isTemp(1).remark(photoParam.getRemark())
                        .score(photoParam.getScore() == null ? new BigDecimal(Constants.ZERO_STR) : photoParam.getScore())
                        .photoArray(photoParam.getPhotoArray())
                        .operateType(operateType).operateUserId(user.getUserId()).operateUserName(user.getName()).build();
                if(historyColumnDO != null){
                    historyColumnNew.setId(historyColumnDO.getId());
                    tbDisplayHistoryColumnMapper.updateByPrimaryKeySelective(enterpriseId, historyColumnNew);
                }else {
                    tbDisplayHistoryColumnMapper.insertSelective(enterpriseId, historyColumnNew);
                }
            }
        }

    }

    @Override
    public void turnTbDisplayTask(String enterpriseId, TaskSubDO oldTaskSubDo, TaskSubDO newTaskSubDo) {
        String oldHandleUserId = oldTaskSubDo.getHandleUserId();
        String newHandleUserId = newTaskSubDo.getHandleUserId();
        String oldHandleUserName = enterpriseUserDao.selectNameIgnoreActiveByUserId(enterpriseId, oldHandleUserId);
        String newHandleUserName = enterpriseUserDao.selectNameIgnoreActiveByUserId(enterpriseId, newHandleUserId);
        TbDisplayTableRecordDO tbDisplayTableRecordDO = tbDisplayTableRecordMapper.getByUnifyTaskIdAndStoreIdAndLoopCount(enterpriseId, oldTaskSubDo.getUnifyTaskId(),oldTaskSubDo.getStoreId(), oldTaskSubDo.getLoopCount());
        TbDisplayHistoryDO historyDO = new TbDisplayHistoryDO();
        historyDO.setIsValid(true);
        historyDO.setOperateType("turn");
        historyDO.setOperateUserId(oldHandleUserId);
        historyDO.setOperateUserName(oldHandleUserName);
        historyDO.setSubTaskId(oldTaskSubDo.getId());
        historyDO.setActionKey("" );
        historyDO.setCreateTime(new Date());
        historyDO.setRecordId(tbDisplayTableRecordDO.getId());
        historyDO.setNodeNo(oldTaskSubDo.getNodeNo());

        if (StringUtils.isNotBlank(newTaskSubDo.getContent())){
            historyDO.setRemark(oldHandleUserName + "转交给" + newHandleUserName+ "/n"+newTaskSubDo.getContent());
        }else if (StringUtils.isNotBlank(oldTaskSubDo.getRemark())){
            historyDO.setRemark(oldHandleUserName + "转交给【" + newHandleUserName + "】/n"+oldTaskSubDo.getRemark());
        }else {
            historyDO.setRemark(oldHandleUserName + "转交给" + newHandleUserName);
        }
        tbDisplayHistoryMapper.insert(enterpriseId, historyDO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean completeTbDisplayTask(TaskMessageDTO taskMessageDTO) {
        String enterpriseId = taskMessageDTO.getEnterpriseId();
        Long unifyTaskId = taskMessageDTO.getUnifyTaskId();
        // 子任务
        String data = taskMessageDTO.getData();
        List<TaskSubDO> taskSubDOList = JSON.parseArray(data, TaskSubDO.class);
        if (CollectionUtils.isEmpty(taskSubDOList)) {
            throw new ServiceException(ErrorCodeEnum.DATA_ERROR.getCode(), "taskMessageDTO.data=" + data);
        }
        Long subTaskId = taskSubDOList.get(0).getId();
        String storeId = taskSubDOList.get(0).getStoreId();
        Long loopCount = taskSubDOList.get(0).getLoopCount();
        // 校验一下
        if (unifyTaskId == null || unifyTaskId == 0 || subTaskId == null || subTaskId == 0) {
            throw new ServiceException(ErrorCodeEnum.DATA_ERROR.getCode(),
                    String.format("unifyTaskId:%s,subTaskId:%s", unifyTaskId, subTaskId));
        }
        tbDisplayTableRecordMapper.updateStatusByTaskIdStoreIdLoopCount(enterpriseId, TbDisplayConstant.TbDisplayRecordStatusConstant.COMPLETE, unifyTaskId, storeId, loopCount);
        return true;
    }

    // 数据报表  按门店  按轮次即按时间
    @Override
    public TbDisplayTableRecordPageVO tableRecordList(String enterpriseId, TbDisplayReportQueryParam query) {
        TaskParentDO taskParentDO = taskParentMapper.selectParentTaskByTaskId(enterpriseId, query.getUnifyTaskId());
        if(taskParentDO == null){
            //定规范
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "未找到对应的父任务【"+query.getUnifyTaskId()+"】");
        }
        TbDisplayTableRecordPageVO tbDisplayTableRecordPageVO = new TbDisplayTableRecordPageVO();
        List<UnifyFormDataDTO> unifyFormDataDTOList = taskMappingMapper.selectMappingDataByTaskId(enterpriseId, query.getUnifyTaskId());
        // 非空
        if(CollectionUtils.isEmpty(unifyFormDataDTOList)){
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "检查表不存在");

        }
        List<Long> metaTableIds = unifyFormDataDTOList.stream().map(m -> Long.parseLong(m.getOriginMappingId())).collect(Collectors.toList());

        List<TbMetaDisplayTableColumnDO> tbMetaDisplayTableColumnDOList = tbMetaDisplayTableColumnMapper.listByTableIdList(enterpriseId, metaTableIds);

        Long metaTableId = metaTableIds.get(0);
        TbMetaTableDO metaTableDO = tbMetaTableMapper.selectById(enterpriseId, metaTableId);

        //枚举值
        List<TbMetaDisplayTableColumnDO> columnList = tbMetaDisplayTableColumnDOList.stream().filter(s-> Constants.INDEX_ZERO.equals(s.getCheckType())).collect(Collectors.toList());
        List<TbMetaDisplayTableColumnDO> contentList = tbMetaDisplayTableColumnDOList.stream().filter(s-> Constants.INDEX_ONE.equals(s.getCheckType())).collect(Collectors.toList());

        tbDisplayTableRecordPageVO.setColumnList(columnList);
        tbDisplayTableRecordPageVO.setContentList(contentList);

        //如果是抄送我或者我处理的的陈列任务，需要进行权限控制
        List<String> storeIds = new ArrayList<>();
        Long esTotal = 0L;
        if (StringUtils.isNotBlank(query.getCcUserId()) || StringUtils.isNotBlank(query.getUserId())) {
            PageVO<String> storeIdPage = getStoreIdList(enterpriseId, query);
            if (CollectionUtils.isEmpty(storeIdPage.getList())) {
                tbDisplayTableRecordPageVO.setRecordInfo(new PageInfo(new ArrayList()));
                return tbDisplayTableRecordPageVO;
            }
            esTotal = storeIdPage.getTotal();
            storeIds.addAll(storeIdPage.getList());
        } else {
            PageHelper.startPage(query.getPageNumber(), query.getPageSize());
        }
        // 获取整体得分，整体评价等信息
        List<TbDisplayTableRecordDO> tbDisplayTableRecordDOList = tbDisplayTableRecordMapper.listByUnifyTaskId(enterpriseId, query.getUnifyTaskId(), query.getLoopCount(), storeIds);
        if(CollUtil.isEmpty(tbDisplayTableRecordDOList)){
            tbDisplayTableRecordPageVO.setRecordInfo(new PageInfo(tbDisplayTableRecordDOList));
            return tbDisplayTableRecordPageVO;
        }
        Set<String> storeIdSet = tbDisplayTableRecordDOList.stream().map(TbDisplayTableRecordDO::getStoreId).collect(Collectors.toSet());

        Set<Long> recordIdSet = tbDisplayTableRecordDOList.stream().map(TbDisplayTableRecordDO::getId).collect(Collectors.toSet());
        // 区名称
        List<StoreGroupMappingDO> storeGroupMappingList = storeGroupMappingMapper.selectMappingByStoreIds(enterpriseId, new ArrayList<>(storeIdSet));

        //查询分组信息
        Map<String, String> storeGroupMap = new HashMap<>();

        Map<String, String> storeGroupNameMap = new HashMap<>();
        if(CollectionUtils.isNotEmpty(storeGroupMappingList)){
            List<String> groupIds = new ArrayList<>();
            for (StoreGroupMappingDO mappingDO : storeGroupMappingList) {
                groupIds.add(mappingDO.getGroupId());
            }

            List<StoreGroupDO> groupList= new ArrayList<>();
            if (CollectionUtils.isNotEmpty(groupIds)) {
                groupList = storeGroupMapper.getListByIds(enterpriseId, groupIds);
            }
            for (StoreGroupDO groupDO : groupList) {
                storeGroupMap.put(groupDO.getGroupId(), groupDO.getGroupName());
            }


            storeGroupMappingList.forEach(e -> {
                if(storeGroupNameMap.containsKey(e.getStoreId())){
                    storeGroupNameMap.put(e.getStoreId(), storeGroupNameMap.get(e.getStoreId()) + Constants.COMMA + storeGroupMap.get(e.getGroupId()));
                }else {
                    storeGroupNameMap.put(e.getStoreId(), storeGroupMap.get(e.getGroupId()));
                }
            });
        }



        // Map:门店id->门店信息（门店名称，门店区域id）
        List<StoreAreaDTO> storeAreaList = storeMapper.getStoreAreaListByStoreIds(enterpriseId, new ArrayList<>(storeIdSet));
        // Map:门店区域id-> 门店区域名称
        Set<String> regionIdSet = storeAreaList.stream().map(m -> String.valueOf(m.getRegionId())).collect(Collectors.toSet());
        Map<String, String> regionMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(regionIdSet)) {
            List<RegionDO> regionList = regionMapper.getRegionByRegionIds(enterpriseId, new ArrayList<>(regionIdSet));
            regionMap.putAll(
                    regionList.stream()
                            .filter(a -> a.getRegionId() != null && a.getName() != null)
                            .collect(Collectors.toMap(RegionDO::getRegionId, RegionDO::getName, (a, b) -> a)));
        }

        List<TaskStoreDO> taskStoreList = taskStoreMapper.listByStoreIdAndLoopCount(enterpriseId, query.getUnifyTaskId(), new ArrayList<>(storeIdSet), query.getLoopCount());

        Map<String, TaskStoreDO> taskStoreMap = taskStoreList.stream()
                .collect(Collectors.toMap(k -> k.getUnifyTaskId()+ Constants.MOSAICS +k.getStoreId()+ Constants.MOSAICS +k.getLoopCount(), synOe -> synOe));

        List<TbDisplayTaskDataVO> tbDisplayTaskDataVOList = new ArrayList<>();

        List<TbDisplayTableDataColumnDO> tableDataColumnList = tbDisplayTableDataColumnMapper.listByRecordIdList(enterpriseId, new ArrayList<>(recordIdSet), null);

        Map<Long, List<TbDisplayTableDataColumnDO>> datataColumnMap = new HashMap<>();
        if(CollectionUtils.isNotEmpty(tableDataColumnList)){
            datataColumnMap = tableDataColumnList.stream().collect(Collectors.groupingBy(TbDisplayTableDataColumnDO::getRecordId));
        }


        List<TbDisplayTableDataContentDO> tableDataContentList = tbDisplayTableDataContentMapper.listByRecordIdList(enterpriseId, new ArrayList<>(recordIdSet), null);

        Map<Long, List<TbDisplayTableDataContentDO>> datataContentMap = new HashMap<>();
        if(CollectionUtils.isNotEmpty(tableDataColumnList)){
            datataContentMap = tableDataContentList.stream().collect(Collectors.groupingBy(TbDisplayTableDataContentDO::getRecordId));
        }


        Map<Long, List<TbDisplayTableDataColumnDO>> finalDatataColumnMap = datataColumnMap;
        Map<Long, List<TbDisplayTableDataContentDO>> finalDatataContentMap = datataContentMap;
        tbDisplayTableRecordDOList.forEach(tbDisplayTableRecordDO -> {
            TbDisplayTaskDataVO tbDisplayTaskDataVO = new TbDisplayTaskDataVO();
            tbDisplayTaskDataVO.setId(tbDisplayTableRecordDO.getId());
            tbDisplayTaskDataVO.setRegionPath(tbDisplayTableRecordDO.getRegionPath());
            tbDisplayTaskDataVO.setStoreAreaName(regionMap.get(String.valueOf(tbDisplayTableRecordDO.getRegionId())));
            tbDisplayTaskDataVO.setUnifyTaskId(tbDisplayTableRecordDO.getUnifyTaskId());
            tbDisplayTaskDataVO.setIsSupportScore(tbDisplayTableRecordDO.getIsSupportScore());
            tbDisplayTaskDataVO.setLoopCount(tbDisplayTableRecordDO.getLoopCount());
            tbDisplayTaskDataVO.setMetaColumnNum(columnList.size());
            tbDisplayTaskDataVO.setMetaTableId(metaTableId);
            tbDisplayTaskDataVO.setHandleUserName(tbDisplayTableRecordDO.getHandleUserName());
            tbDisplayTaskDataVO.setLatestHandlerTime(tbDisplayTableRecordDO.getLatestHandlerTime());
            tbDisplayTaskDataVO.setApproveUserName(tbDisplayTableRecordDO.getApproveUserName());
            tbDisplayTaskDataVO.setLatestApproveTime(tbDisplayTableRecordDO.getLatestApproveTime());
            tbDisplayTaskDataVO.setRecheckUserName(tbDisplayTableRecordDO.getRecheckUserName());
            tbDisplayTaskDataVO.setRemark(tbDisplayTableRecordDO.getRemark());
            tbDisplayTaskDataVO.setScore(tbDisplayTableRecordDO.getScore());
            tbDisplayTaskDataVO.setStoreName(tbDisplayTableRecordDO.getStoreName());
            tbDisplayTaskDataVO.setTableName(metaTableDO.getTableName());
            tbDisplayTaskDataVO.setDataColumnList(finalDatataColumnMap.get(tbDisplayTableRecordDO.getId()));
            tbDisplayTaskDataVO.setStoreId(tbDisplayTableRecordDO.getStoreId());
            tbDisplayTaskDataVO.setHandlerEndTime(tbDisplayTableRecordDO.getHandlerEndTime());
            tbDisplayTaskDataVO.setDataContentList(finalDatataContentMap.get(tbDisplayTableRecordDO.getId()));
            tbDisplayTaskDataVO.setStoreGroupName(storeGroupNameMap.get(tbDisplayTableRecordDO.getStoreId()));
            tbDisplayTaskDataVO.setBeginTime(tbDisplayTableRecordDO.getSubBeginTime() == null ? null : tbDisplayTableRecordDO.getSubBeginTime().getTime());
            tbDisplayTaskDataVO.setEndTime(tbDisplayTableRecordDO.getSubEndTime() == null ? null : tbDisplayTableRecordDO.getSubEndTime().getTime());
            tbDisplayTaskDataVO.setTaskName(taskParentDO.getTaskName());
            tbDisplayTaskDataVO.setTaskDesc(taskParentDO.getTaskDesc());
            tbDisplayTaskDataVO.setStoreId(tbDisplayTableRecordDO.getStoreId());
            tbDisplayTaskDataVO.setFirstHandlerTime(tbDisplayTableRecordDO.getFirstHandlerTime());
            tbDisplayTaskDataVO.setFirstApproveTime(tbDisplayTableRecordDO.getFirstApproveTime());
            TaskStoreDO taskStoreDO = taskStoreMap.get(tbDisplayTableRecordDO.getUnifyTaskId()+ Constants.MOSAICS +
                    tbDisplayTableRecordDO.getStoreId()+ Constants.MOSAICS + tbDisplayTableRecordDO.getLoopCount());
            //处理超时时间
            tbDisplayTaskDataVO.setHandlerDuration(formatterTime(tbDisplayTableRecordDO.getFirstHandlerTime(), tbDisplayTableRecordDO.getSubBeginTime()));
            tbDisplayTaskDataVO.setApproveDuration(formatterTime(tbDisplayTableRecordDO.getFirstApproveTime(), tbDisplayTableRecordDO.getFirstHandlerTime()));
            String validTime = DateUtils.convertTimeToString(tbDisplayTableRecordDO.getSubBeginTime().getTime(), DATE_FORMAT_SEC_5) + "-"
                    + DateUtils.convertTimeToString(tbDisplayTableRecordDO.getSubEndTime().getTime(), DATE_FORMAT_SEC_5);
            tbDisplayTaskDataVO.setValidTime(validTime);
            if(tbDisplayTableRecordDO.getFirstHandlerTime() != null && tbDisplayTableRecordDO.getHandlerEndTime() != null){
                tbDisplayTaskDataVO.setHandlerOverdue(tbDisplayTableRecordDO.getFirstHandlerTime().after(tbDisplayTableRecordDO.getHandlerEndTime()) ? "是" : "");
            }else if(tbDisplayTaskDataVO.getHandlerEndTime() != null){
                tbDisplayTaskDataVO.setHandlerOverdue(new Date().after(tbDisplayTableRecordDO.getHandlerEndTime()) ? "是" : "");
            }else {
                tbDisplayTaskDataVO.setHandlerOverdue(new Date().after(tbDisplayTableRecordDO.getSubEndTime()) ? "是" : "");
            }
            if(taskStoreDO != null){
                String subStatus = taskStoreDO.getSubStatus();
                tbDisplayTaskDataVO.setStatus(TaskStatusEnum.getByCode(subStatus.toUpperCase()).getDesc());
                if (TaskStatusEnum.COMPLETE.getCode().toLowerCase().equals(subStatus)) {
                    // 结束时间
                    tbDisplayTaskDataVO.setDoneTime(taskStoreDO.getHandleTime());
                    tbDisplayTaskDataVO.setCheckTime(formatterTime(taskStoreDO.getHandleTime() , taskStoreDO.getSubBeginTime()));
                    // 是否过期完成
                    tbDisplayTaskDataVO.setCompleteOverdue(taskStoreDO.getHandleTime().after(taskStoreDO.getSubEndTime()) ? "是" : "否");
                }
                tbDisplayTaskDataVO.setTaskStatus(UnifyNodeEnum.getNodeName(taskStoreDO.getNodeNo()));
            }
            tbDisplayTaskDataVOList.add(tbDisplayTaskDataVO);
        });
        PageInfo pageInfo = new PageInfo(tbDisplayTableRecordDOList);
        if (esTotal.intValue() > 0) {
            pageInfo.setTotal(esTotal);
        }
        pageInfo.setList(tbDisplayTaskDataVOList);
        tbDisplayTableRecordPageVO.setRecordInfo(pageInfo);
        return tbDisplayTableRecordPageVO;
    }

    /**
     * 获得用户有权限的门店列表
     * @param enterpriseId
     * @param query
     * @author: xugangkun
     * @return java.util.List<java.lang.String>
     * @date: 2021/11/23 15:35
     */
    private PageVO<String> getStoreIdList(String enterpriseId, TbDisplayReportQueryParam query) {
        PageVO<String> result = new PageVO<>();
        // 只有抄送我的，我处理的检索
        if (StringUtils.isBlank(query.getCcUserId()) && StringUtils.isBlank(query.getUserId())) {
            return new PageVO<>();
        }
        try {
            TaskStoreLoopQuery storeQuery = new TaskStoreLoopQuery();
            storeQuery.setPageSize(query.getPageSize());
            storeQuery.setPageNumber(query.getPageNumber());
            storeQuery.setLoopCount(query.getLoopCount());
            storeQuery.setUnifyTaskId(query.getUnifyTaskId());
            storeQuery.setCcUserId(query.getCcUserId());
            storeQuery.setUserId(query.getUserId());
            PageVO<TaskStoreDO> storeList = elasticSearchService.getTaskStoreList(enterpriseId, storeQuery);
            if (CollectionUtils.isNotEmpty(storeList.getList())) {
                List <String> storeIds = storeList.getList().stream().map(TaskStoreDO::getStoreId).collect(Collectors.toList());
                result.setList(storeIds);
                result.setTotal(storeList.getTotal());
            }
        } catch (Exception e) {
            log.error("获取门店任务列表异常", e);
        }
        return result;
    }

    @Override
    public ImportTaskDO tableRecordListExport(String enterpriseId, TbDisplayReportQueryParam query) {
        GetTaskByPersonVO taskParentById = unifyTaskParentService.getTaskParentById(enterpriseId, query.getUnifyTaskId());
        log.info("tableRecordListExport taskParentById:{}",JSONObject.toJSONString(taskParentById));
        String fileName = "陈列数据表(" + taskParentById.getTaskName() + ").xlsl";
        ImportTaskDO importTaskDO = importTaskService.insertExportTask(enterpriseId, fileName, ImportTaskConstant.TB_DISPLAY_TASK_REPORT_NEW);
        //使陈列任务导出时文件名可变
        ExportServiceEnum exportDisplayRecordList = ExportServiceEnum.EXPORT_DISPLAY_RECORD_LIST;
        exportDisplayRecordList.setFileName(exportDisplayRecordList.getFileName() + "(" + taskParentById.getTaskName() + ")");

        query.setExportServiceEnum(exportDisplayRecordList);
        MsgUniteData msgUniteData = new MsgUniteData();
        ExportMsgSendRequest exportMsgSendRequest = new ExportMsgSendRequest();
        exportMsgSendRequest.setEnterpriseId(enterpriseId);
        exportMsgSendRequest.setRequest(JSONObject.parseObject(JSONObject.toJSONString(query)));
        exportMsgSendRequest.setTotalNum(Constants.MAX_EXPORT_SIZE);
        exportMsgSendRequest.setImportTaskDO(importTaskDO);
        exportMsgSendRequest.setDbName(query.getDbName());
        exportMsgSendRequest.setIsAddRegion(true);
        msgUniteData.setData(JSONObject.toJSONString(exportMsgSendRequest));
        msgUniteData.setMsgType(MsgUniteDataTypeEnum.EXPORT_FILE_COMMON.getCode());
        //分开异步导出
        simpleMessageService.send(JSONObject.toJSONString(msgUniteData), RocketMqTagEnum.EXPORT_IMPORT_MESSAGE);
        return importTaskDO;
    }

    @Override
    public void reallocateTbDisplayTask(String enterpriseId, TaskStoreDO taskStoreDO, String operUserId) {
        String operateUserName = enterpriseUserDao.selectNameIgnoreActiveByUserId(enterpriseId, operUserId);
        TbDisplayTableRecordDO tbDisplayTableRecordDO = tbDisplayTableRecordMapper.getByUnifyTaskIdAndStoreIdAndLoopCount(enterpriseId, taskStoreDO.getUnifyTaskId(),taskStoreDO.getStoreId(), taskStoreDO.getLoopCount());
        TbDisplayHistoryDO historyDO = new TbDisplayHistoryDO();
        historyDO.setIsValid(true);
        historyDO.setOperateType(DisplayConstant.ActionKeyConstant.REALLOCATE);
        historyDO.setOperateUserId(operUserId);
        historyDO.setOperateUserName(operateUserName);
        historyDO.setSubTaskId(0L);
        historyDO.setActionKey(DisplayConstant.ActionKeyConstant.REALLOCATE);
        historyDO.setCreateTime(new Date());
        historyDO.setRecordId(tbDisplayTableRecordDO.getId());
        historyDO.setNodeNo(taskStoreDO.getNodeNo());
        historyDO.setRemark(operateUserName + "重新分配门店任务");
        tbDisplayHistoryMapper.insert(enterpriseId, historyDO);
    }

    @Override
    public PageDTO<DisplayRecordVO> displayList(String enterpriseId, DisplayDTO displayDTO) {
        //校验必要参数
        OpenApiParamCheckUtils.checkNecessaryParam(displayDTO.getPageSize(),displayDTO.getPageNum());
        //校验页码，最大100
        OpenApiParamCheckUtils.checkParamLimit(displayDTO.getPageSize(),0,100);
        OpenApiParamCheckUtils.checkTimeInterval(displayDTO.getBeginTime(),displayDTO.getEndTime());
        PageDTO<DisplayRecordVO> tbDisplayTableRecordDOPageDTO = new PageDTO<>();
        tbDisplayTableRecordDOPageDTO.setPageNum(displayDTO.getPageNum());
        tbDisplayTableRecordDOPageDTO.setPageSize(displayDTO.getPageSize());
        PageHelper.startPage(displayDTO.getPageNum(),displayDTO.getPageSize());
        List<TbDisplayTableRecordDO> list = tbDisplayTableRecordMapper.displayList(enterpriseId, displayDTO);
        List<DisplayRecordVO> displayRecordVOS = new ArrayList<>();
        for (TbDisplayTableRecordDO tbDisplayTableRecordDO:list) {
            DisplayRecordVO displayRecordVO = new DisplayRecordVO();
            BeanUtils.copyProperties(tbDisplayTableRecordDO, displayRecordVO);
            displayRecordVOS.add(displayRecordVO);
        }
        PageInfo pageInfo = new PageInfo(list);
        tbDisplayTableRecordDOPageDTO.setList(displayRecordVOS);
        tbDisplayTableRecordDOPageDTO.setTotal(pageInfo.getTotal());
        return tbDisplayTableRecordDOPageDTO;
    }

    @Override
    public DisplayRecordDetailVO displayRecordDetail(String enterpriseId, Long recordId) {
        OpenApiParamCheckUtils.checkNecessaryParam(recordId);
        DisplayRecordDetailVO vo = new DisplayRecordDetailVO();
        TbDisplayTableRecordDO tbDisplayTableRecordDO = tbDisplayTableRecordMapper.selectByPrimaryKey(enterpriseId, recordId);
        if (tbDisplayTableRecordDO == null) {
            throw new ServiceException(ErrorCodeEnum.DISPLAY_RECORD_NOT_EXIST);
        }
        BeanUtils.copyProperties(tbDisplayTableRecordDO, vo);

        //父任务信息
        TaskParentDO taskParentDO = taskParentMapper.selectTaskById(enterpriseId, tbDisplayTableRecordDO.getUnifyTaskId());
        if (taskParentDO!=null){
           vo.setTaskName(taskParentDO.getTaskName());
           vo.setTaskDesc(taskParentDO.getTaskDesc());
           vo.setTaskType(taskParentDO.getTaskType());
        }

        //门店信息
        StoreDO storeDO = storeMapper.getByStoreId(enterpriseId, tbDisplayTableRecordDO.getStoreId());
        if (storeDO!=null){
            vo.setStoreName(storeDO.getStoreName());
            vo.setAvatar(storeDO.getAvatar());
        }

        //表名称
        TbMetaTableDO tbMetaTableDO = tbMetaTableMapper.selectById(enterpriseId, tbDisplayTableRecordDO.getMetaTableId());
        if (tbMetaTableDO!=null){
            vo.setTableName(tbMetaTableDO.getTableName());
        }

        //处理超时时间
        vo.setHandlerDuration(formatterTime(tbDisplayTableRecordDO.getFirstHandlerTime(), tbDisplayTableRecordDO.getSubBeginTime()));
        vo.setApproveDuration(formatterTime(tbDisplayTableRecordDO.getFirstApproveTime(), tbDisplayTableRecordDO.getFirstHandlerTime()));

        List<TbDisplayTableDataColumnDO> tbDisplayTableDataColumnDOList = tbDisplayTableDataColumnMapper.listByRecordId(enterpriseId, tbDisplayTableRecordDO.getId());
        List<Long> metaColumnIdList = tbDisplayTableDataColumnDOList.stream().map(TbDisplayTableDataColumnDO::getMetaColumnId).collect(Collectors.toList());
        List<TbMetaDisplayTableColumnDO>  tbMetaDisplayTableColumnDOList = tbMetaDisplayTableColumnMapper.listByIdList(enterpriseId, metaColumnIdList);
        Map<Long, TbMetaDisplayTableColumnDO> tbMetaDisplayTableColumnDOMap =
                tbMetaDisplayTableColumnDOList.stream().collect(Collectors.toMap(TbMetaDisplayTableColumnDO::getId, Function.identity(), (a, b) -> a));


        List<DisplayTableDataColumnVO> displayDataColumnVOList = new ArrayList<>();
        for (TbDisplayTableDataColumnDO tbDisplayTableDataColumnDO : tbDisplayTableDataColumnDOList) {
            if (tbDisplayTableDataColumnDO == null) {
                continue;
            }
            DisplayTableDataColumnVO tbDisplayDataColumnVO = new DisplayTableDataColumnVO();
            BeanUtils.copyProperties(tbDisplayTableDataColumnDO, tbDisplayDataColumnVO);
            TbMetaDisplayTableColumnDO tbMetaDisplayTableColumnDO = tbMetaDisplayTableColumnDOMap.get(tbDisplayTableDataColumnDO.getMetaColumnId());
            if(tbMetaDisplayTableColumnDO == null){
                throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "检查项不存在【"+tbDisplayTableDataColumnDO.getMetaColumnId()+"】");
            }
            tbDisplayDataColumnVO.setStandardPic(tbMetaDisplayTableColumnDO.getStandardPic());
            tbDisplayDataColumnVO.setDescription(tbMetaDisplayTableColumnDO.getDescription());
            tbDisplayDataColumnVO.setColumnName(tbMetaDisplayTableColumnDO.getColumnName());
            tbDisplayDataColumnVO.setOrderNum(tbMetaDisplayTableColumnDO.getOrderNum() == null ? 0 : tbMetaDisplayTableColumnDO.getOrderNum());
            tbDisplayDataColumnVO.setMetaScore(tbMetaDisplayTableColumnDO.getScore());
            displayDataColumnVOList.add(tbDisplayDataColumnVO);
        }
        if(CollectionUtils.isNotEmpty(displayDataColumnVOList)){
            displayDataColumnVOList.sort(Comparator.comparingInt(DisplayTableDataColumnVO::getOrderNum));
        }
        vo.setTbDisplayDataColumnList(displayDataColumnVOList);

        List<DisplayTableDataContentVO> tbDisplayDataContentVOList = new ArrayList<>();
        //在content表中根据任务记录id查询数据
        List<TbDisplayTableDataContentDO> tbDisplayTableDataContentDOList = tbDisplayTableDataContentMapper.listByRecordId(enterpriseId, tbDisplayTableRecordDO.getId());
        if (tbDisplayTableDataContentDOList!=null&&tbDisplayTableDataContentDOList.size()>0){
            //筛选出陈列使用得到的陈列检查内容id
            List<Long> metaContentIdList = tbDisplayTableDataContentDOList.stream().map(TbDisplayTableDataContentDO::getMetaContentId).collect(Collectors.toList());
            //查询出陈列用到的模板数据集合
            List<TbMetaDisplayTableColumnDO>  tbMetaDisplayTableContentDOList = tbMetaDisplayTableColumnMapper.listByIdList(enterpriseId, metaContentIdList);
            Map<Long, TbMetaDisplayTableColumnDO> tbMetaDisplayTableContentDOMap =
                    tbMetaDisplayTableContentDOList.stream().collect(Collectors.toMap(TbMetaDisplayTableColumnDO::getId, Function.identity(), (a, b) -> a));

            for (TbDisplayTableDataContentDO tbDisplayTableDataContentDO : tbDisplayTableDataContentDOList) {
                if (tbDisplayTableDataContentDO == null) {
                    continue;
                }
                DisplayTableDataContentVO tbDisplayDataContentVo = new DisplayTableDataContentVO();
                BeanUtils.copyProperties(tbDisplayTableDataContentDO, tbDisplayDataContentVo);
                TbMetaDisplayTableColumnDO tbMetaDisplayTableColumnDO = tbMetaDisplayTableContentDOMap.get(tbDisplayTableDataContentDO.getMetaContentId());
                if(tbMetaDisplayTableColumnDO == null){
                    throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "检查内容不存在【"+tbDisplayTableDataContentDO.getMetaContentId()+"】");
                }
                tbDisplayDataContentVo.setStandardPic(tbMetaDisplayTableColumnDO.getStandardPic());
                tbDisplayDataContentVo.setDescription(tbMetaDisplayTableColumnDO.getDescription());
                tbDisplayDataContentVo.setColumnName(tbMetaDisplayTableColumnDO.getColumnName());
                tbDisplayDataContentVo.setOrderNum(tbMetaDisplayTableColumnDO.getOrderNum() == null ? 0 : tbMetaDisplayTableColumnDO.getOrderNum());
                tbDisplayDataContentVo.setMetaScore(tbMetaDisplayTableColumnDO.getScore());
                tbDisplayDataContentVOList.add(tbDisplayDataContentVo);
            }
            vo.setTbDisplayDataContentList(tbDisplayDataContentVOList);
        }
        return vo;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteRecord(String enterpriseId, TbDisplayDeleteParam tbDisplayDeleteParam, CurrentUser currentUser,String isDone, EnterpriseConfigDO config) {
        Long taskStoreId = tbDisplayDeleteParam.getTaskStoreId();
        TaskStoreDO taskStoreDO = taskStoreMapper.selectByPrimaryKey(enterpriseId, taskStoreId);
        if (isDone.equals("notDone") && !taskStoreDO.getSubStatus().equals("ongoing")){
            log.info("当前门店任务没有进行中的任务");
            return;
        }
        if(taskStoreDO == null || "delete".equals(taskStoreDO.getSubStatus())){
            throw new ServiceException(ErrorCodeEnum.TASK_NOT_EXIST);
        }
        TbDisplayTableRecordDO tbDisplayTableRecordDO = tbDisplayTableRecordMapper.getByUnifyTaskIdAndStoreIdAndLoopCount(enterpriseId,
                taskStoreDO.getUnifyTaskId(), taskStoreDO.getStoreId(), taskStoreDO.getLoopCount());
        if(tbDisplayTableRecordDO == null){
            throw new ServiceException(ErrorCodeEnum.TASK_NOT_EXIST);
        }
        //删除陈列任务与停止陈列任务逻辑不同;(用于导出陈列)
        if ("deleteRecord".equals(isDone)){
            //更改为逻辑删除
            taskStoreMapper.deleteTaskStoreById(enterpriseId,taskStoreId);
        }else if ("notDone".equals(isDone)){
            tbDisplayTableRecordDO.setStatus(TbDisplayConstant.TbDisplayRecordStatusConstant.STOP);
            taskStoreMapper.delTaskStoreById(enterpriseId, taskStoreId);
        }
        List<TaskSubVO> taskSubList = taskSubMapper.selectSubTaskByTaskIdAndStoreIdAndLoop(enterpriseId, taskStoreDO.getUnifyTaskId(), taskStoreDO.getStoreId(), taskStoreDO.getLoopCount());
        taskSubMapper.delSubTaskByTaskIdAndStoreIdAndLoopCount(enterpriseId, taskStoreDO.getUnifyTaskId(), taskStoreDO.getStoreId(), taskStoreDO.getLoopCount());
        tbDisplayTableRecordDO.setDeleted(true);
        tbDisplayTableRecordDO.setDeleteUserId(currentUser.getUserId());
        tbDisplayTableRecordDO.setDeleteUserName(currentUser.getName());
        tbDisplayTableRecordDO.setDeleteTime(new Date());
        tbDisplayTableRecordMapper.updateByPrimaryKeySelective(enterpriseId, tbDisplayTableRecordDO);
        tbDisplayTableDataContentMapper.deleteByRecordId(enterpriseId, tbDisplayTableRecordDO.getId());
        tbDisplayTableDataColumnMapper.deleteByRecordId(enterpriseId, tbDisplayTableRecordDO.getId());
        taskStoreDO.setDeleted(1);
        // 取消待办
        if (TaskTypeEnum.isCombineNoticeTypes(taskStoreDO.getTaskType())) {
            List<String> userIds = CollStreamUtil.toList(taskSubList, TaskSubVO::getHandleUserId);
            unifyTaskService.cancelCombineUpcoming(enterpriseId, taskStoreDO.getUnifyTaskId(), taskStoreDO.getLoopCount(), taskStoreDO.getStoreId(), taskStoreDO.getNodeNo(), userIds, config.getDingCorpId(), config.getAppType());
        } else {
            List<Long> subTaskIds = CollStreamUtil.toList(taskSubList, TaskSubVO::getSubTaskId);
            unifyTaskService.cancelUpcoming(enterpriseId, subTaskIds, config.getDingCorpId(), config.getAppType());
        }
    }

    @Override
    public void batchDeleteRecord(String enterpriseId, TbDisplayBatchDeleteParam param, CurrentUser user) {
        List<TaskStoreDO> taskStoreList = taskStoreMapper.taskStoreListByIdList(enterpriseId, param.getTaskStoreIds());

        List<TbDisplayTableRecordDO> recordList= tbDisplayTableRecordMapper.batchGetByUnifyTaskIdAndStoreIdAndLoopCount(enterpriseId, taskStoreList);
        taskStoreMapper.deleteTaskStoreByIds(enterpriseId, param.getTaskStoreIds());
        List<TaskSubVO> taskSubVOS = taskSubMapper.selectSubTaskByTaskIdAndStoreIdAndLoopCount(enterpriseId, taskStoreList);
        taskSubMapper.batchDelSubTaskByTaskIdAndStoreIdAndLoopCount(enterpriseId, taskStoreList);
        recordList.forEach(o->{
            o.setDeleted(true);
            o.setDeleteUserId(user.getUserId());
            o.setDeleteUserName(user.getName());
            o.setDeleteTime(new Date());
        });
        List<Long> recordIds = recordList.stream().map(TbDisplayTableRecordDO::getId).collect(Collectors.toList());
        tbDisplayTableRecordMapper.batchUpdate(enterpriseId, recordList);
        tbDisplayTableDataContentMapper.deleteByRecordIds(enterpriseId, recordIds);
        tbDisplayTableDataColumnMapper.deleteByRecordIds(enterpriseId, recordIds);
        taskStoreList.forEach(o->o.setDeleted(1));

        // 钉钉待办取消
        DataSourceHelper.reset();
        EnterpriseConfigDO config = enterpriseConfigDao.getEnterpriseConfig(enterpriseId);
        DataSourceHelper.changeToMy();
        if (CollectionUtils.isNotEmpty(taskStoreList)) {
            TaskStoreDO taskStoreDO = taskStoreList.get(0);
            // 按门店分组发送消息
            Map<String, List<TaskSubVO>> taskSubGroup = CollStreamUtil.groupByKey(taskSubVOS, TaskSubVO::getStoreId);
            taskSubGroup.forEach((storeId, taskSubList) -> {
                // 取消待办
                if (TaskTypeEnum.isCombineNoticeTypes(taskStoreDO.getTaskType())) {
                    List<String> userIds = CollStreamUtil.toList(taskSubList, TaskSubVO::getHandleUserId);
                    unifyTaskService.cancelCombineUpcoming(enterpriseId, taskStoreDO.getUnifyTaskId(), taskStoreDO.getLoopCount(), storeId, taskStoreDO.getNodeNo(), userIds, config.getDingCorpId(), config.getAppType());
                } else {
                    List<Long> subTaskIds = CollStreamUtil.toList(taskSubList, TaskSubVO::getSubTaskId);
                    unifyTaskService.cancelUpcoming(enterpriseId, subTaskIds, config.getDingCorpId(), config.getAppType());
                }
            });
        }
    }

    @Override
    public PageInfo<TbDisplayTableRecordDeleteVO> getDeleteRecordList(String enterpriseId, Long unifyTaskId, Integer pageNum, Integer pageSize,String unifyTaskIds) {
        PageHelper.startPage(pageNum, pageSize);
        List<TbDisplayTableRecordDO> list = new ArrayList<>();
        if (StringUtils.isNotBlank(unifyTaskIds)){
            String[] idArray = StringUtils.split(unifyTaskIds, ",");
            List<Long> unifyTaskIdList = Arrays.stream(idArray)
                    .map(s -> Long.parseLong(s.trim())).collect(Collectors.toList());
            list = tbDisplayTableRecordMapper.deleteListByUnifyTaskIds(enterpriseId, unifyTaskIdList);
        }else {
            list = tbDisplayTableRecordMapper.deleteListByUnifyTaskId(enterpriseId, unifyTaskId);
        }

        PageInfo pageInfo = new PageInfo(list);
        if(CollectionUtils.isEmpty(list)){
            return pageInfo;
        }
        List<TbDisplayTableRecordDeleteVO> result = new ArrayList<>();
        list.forEach(tbDisplayTableRecordDO -> {
            TbDisplayTableRecordDeleteVO deleteVO = new TbDisplayTableRecordDeleteVO();
            deleteVO.setId(tbDisplayTableRecordDO.getId());
            deleteVO.setDeleteUserId(tbDisplayTableRecordDO.getDeleteUserId());
            deleteVO.setDeleteTime(tbDisplayTableRecordDO.getDeleteTime());
            deleteVO.setStoreId(tbDisplayTableRecordDO.getStoreId());
            deleteVO.setStoreName(tbDisplayTableRecordDO.getStoreName());
            deleteVO.setDeleteUserName(tbDisplayTableRecordDO.getDeleteUserName());
            result.add(deleteVO);
        });
        pageInfo.setList(result);
        return pageInfo;
    }


    /**
     * 获取记录报表数据
     *
     * @param enterpriseId
     * @param tbDisplayTableRecordDOList
     * @return
     */
    private List<TbDisplayTableRecordVO> getReportDisplaySubVONew(String enterpriseId,
                                                                  List<TbDisplayTableRecordDO> tbDisplayTableRecordDOList) {
        if (CollectionUtils.isEmpty(tbDisplayTableRecordDOList)) {
            return new ArrayList<>();
        }
        Set<Long> unifyTaskIdSet =
                tbDisplayTableRecordDOList.stream().map(TbDisplayTableRecordDO::getUnifyTaskId).collect(Collectors.toSet());
        Set<String> subTaskCodeSet = tbDisplayTableRecordDOList.stream().map(a -> a.getUnifyTaskId() + "#" + a.getStoreId())
                .collect(Collectors.toSet());
        Set<String> storeIdSet =
                tbDisplayTableRecordDOList.stream().map(TbDisplayTableRecordDO::getStoreId).collect(Collectors.toSet());

        // Map:父任务id->父任务
        List<TaskParentDO> taskParentDOList =
                taskParentMapper.selectTaskByIds(enterpriseId, new ArrayList<>(unifyTaskIdSet));
        if (CollectionUtils.isEmpty(taskParentDOList)) {
            return new ArrayList<>();
        }
        Map<Long, TaskParentDO> idTaskParentDOMap =
                taskParentDOList.stream().collect(Collectors.toMap(TaskParentDO::getId, Function.identity(), (a, b) -> a));

        List<Long> tableRecordIdList =
                tbDisplayTableRecordDOList.stream().map(TbDisplayTableRecordDO::getId).collect(Collectors.toList());
        List<TbDisplayTableDataColumnDO> tbDisplayTableDataColumnDOList = tbDisplayTableDataColumnMapper.listByRecordIdList(enterpriseId, tableRecordIdList, null);
        Map<Long, List<TbDisplayTableDataColumnDO>> tbDisplayTableDataColumnDOMap = tbDisplayTableDataColumnDOList.stream()
                .collect(Collectors.groupingBy(TbDisplayTableDataColumnDO::getRecordId));


        // 子任务信息
        List<TaskSubVO> taskSubVOList =
                taskSubMapper.selectSubTaskBySubTaskCodes(enterpriseId, new ArrayList<>(subTaskCodeSet));
        if (CollectionUtils.isEmpty(taskSubVOList)) {
            return new ArrayList<>();
        }
        // Map:门店id->门店信息（门店名称，门店区域id）
        List<StoreAreaDTO> storeAreaList =
                storeMapper.getStoreAreaListByStoreIds(enterpriseId, new ArrayList<>(storeIdSet));
        Set<String> regionIdSet = storeAreaList.stream().map(m -> String.valueOf(m.getRegionId())).collect(Collectors.toSet());
        Map<String, String> regionMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(regionIdSet)) {
            List<RegionDO> regionList = regionMapper.getRegionByRegionIds(enterpriseId, new ArrayList<>(regionIdSet));
            regionMap.putAll(
                    regionList.stream()
                            .filter(a -> a.getRegionId() != null && a.getName() != null)
                            .collect(Collectors.toMap(RegionDO::getRegionId, RegionDO::getName, (a, b) -> a)));
        }
        // 循环解析数据
        List<TbDisplayTableRecordVO> tbDisplayTableRecordVOList = tbDisplayTableRecordDOList.stream().map(a -> {
            TaskParentDO taskParentDO = idTaskParentDOMap.get(a.getUnifyTaskId());
            if (taskParentDO == null) {
                log.error("未找到父任务信息，unifyTaskId={}", a.getUnifyTaskId());
            }
            TbDisplayTableRecordVO tbDisplayTableRecordVO = new TbDisplayTableRecordVO();
            BeanUtils.copyProperties(a, tbDisplayTableRecordVO);
            List<TbDisplayTableDataColumnDO> tbDisplayTableDataColumnDOListTemp = tbDisplayTableDataColumnDOMap.get(a.getId());
            List<TbDisplayTableDataColumnVO> tbDisplayTableDataColumnVOList = tbDisplayTableDataColumnDOListTemp.stream().map(tbDisplayTableDataColumnDO -> {
                TbDisplayTableDataColumnVO tbDisplayTableDataColumnVO = new TbDisplayTableDataColumnVO();
                BeanUtils.copyProperties(tbDisplayTableDataColumnDO, tbDisplayTableDataColumnVO);
                return  tbDisplayTableDataColumnVO;
            }).collect(Collectors.toList());
            tbDisplayTableRecordVO.setTbDisplayDataColumnVOList(tbDisplayTableDataColumnVOList);
            return tbDisplayTableRecordVO;
        }).collect(Collectors.toList());

        return  tbDisplayTableRecordVOList;
    }


    public List<TaskSubVO> dealAllSubGroup(List<TaskSubVO> oldList) {
        if (CollectionUtils.isEmpty(oldList)) {
            return new ArrayList<>();
        }
        List<TaskSubVO> newList = Lists.newArrayList();
        Map<String, List<TaskSubVO>> collect = oldList.stream().collect(Collectors.groupingBy(e -> StringUtils.join(e.getUnifyTaskId(), Constants.MOSAICS, e.getStoreId(), Constants.MOSAICS, e.getLoopCount())));
        for (Map.Entry<String, List<TaskSubVO>> entry : collect.entrySet()) {
            List<TaskSubVO> mapValue = entry.getValue();
            Long maxTime = mapValue.get(0).getCreateTime();
            Long firstHandleTime = mapValue.get(0).getHandleTime();
            if (ObjectUtil.isNotEmpty(firstHandleTime)) {
                maxTime = firstHandleTime;
            }
            //一次遍历获取list中最新时间的审批记录
            int choice = 0;
            for (int i = 0; i < mapValue.size(); i++) {
                TaskSubVO item = mapValue.get(i);
                Long time = item.getCreateTime();
                if (ObjectUtil.isNotEmpty(item.getHandleTime())) {
                    time = item.getHandleTime();
                }
                if (time > maxTime) {
                    maxTime = time;
                    choice = i;
                }
            }
            //同一个父任务下的同一家门店的子任务只取最新的
            newList.add(mapValue.get(choice));
        }
        if(CollectionUtils.isNotEmpty(newList)){
            newList = newList.stream().sorted(Comparator.comparing(TaskSubVO::getCreateTime).reversed())
                    .collect(Collectors.toList());
        }
        return newList;
    }

    private String getDifferentPhoto(String dataPhotoArray, TbDisplayApprovePhotoParam photoParams, TbDisplayHistoryColumnDO historyPhotoColumn) {
        String photoParamsPhoto = photoParams != null && photoParams.getPhotoArray() != null ? photoParams.getPhotoArray() : "";
        String historyPhotoColumnPhoto = historyPhotoColumn != null && historyPhotoColumn.getPhotoArray() != null ? historyPhotoColumn.getPhotoArray() : "";
        photoParamsPhoto = StringUtils.isNotBlank(photoParamsPhoto) ? photoParamsPhoto : historyPhotoColumnPhoto;
        if (StringUtils.isBlank(dataPhotoArray)) {
            return photoParamsPhoto != null ? photoParamsPhoto : "";
        }

        if (StringUtils.isBlank(photoParamsPhoto)) {
            return "";
        }

        if (dataPhotoArray.equals(photoParamsPhoto)) {
            return "";
        }

        List<String> dataPhotoList = JSONUtil.toList(JSONUtil.parseArray(dataPhotoArray), String.class);

        List<String> photoParamsList = JSONUtil.toList(JSONUtil.parseArray(photoParamsPhoto), String.class);

        photoParamsList.removeAll(dataPhotoList);

        String photo = "";
        if (CollectionUtils.isNotEmpty(photoParamsList)) {
            photo = photoParamsList.toString();
        }
        return photo;
    }


    private String getDifferentContentPhoto(String dataPhotoArray, TbDisplayApproveContentParam photoParams, TbDisplayHistoryColumnDO historyPhotoColumn) {
        String photoParamsPhoto = photoParams != null && photoParams.getPhotoArray() != null ? photoParams.getPhotoArray() : "";
        String historyPhotoColumnPhoto = historyPhotoColumn != null && historyPhotoColumn.getPhotoArray() != null ? historyPhotoColumn.getPhotoArray() : "";
        photoParamsPhoto = StringUtils.isNotBlank(photoParamsPhoto) ? photoParamsPhoto : historyPhotoColumnPhoto;
        if (StringUtils.isBlank(dataPhotoArray)) {
            return photoParamsPhoto != null ? photoParamsPhoto : "";
        }

        if (StringUtils.isBlank(photoParamsPhoto)) {
            return "";
        }

        if (dataPhotoArray.equals(photoParamsPhoto)) {
            return "";
        }

        List<String> dataPhotoList = JSONUtil.toList(JSONUtil.parseArray(dataPhotoArray), String.class);

        List<String> photoParamsList = JSONUtil.toList(JSONUtil.parseArray(photoParamsPhoto), String.class);

        photoParamsList.removeAll(dataPhotoList);

        String photo = "";
        if (CollectionUtils.isNotEmpty(photoParamsList)) {
            photo = photoParamsList.toString();
        }
        return photo;
    }

    public List<String> getHandleUrlByPhotoArray(List<TbDisplayTableDataColumnDO> tbDisplayTableDataColumnDOS) {
        List<String>  handleUrlList = Lists.newArrayList();
        if(CollUtil.isNotEmpty(tbDisplayTableDataColumnDOS)){
            for (TbDisplayTableDataColumnDO tbDisplayTableDataColumnDO : tbDisplayTableDataColumnDOS) {
                if(StrUtil.isNotEmpty(tbDisplayTableDataColumnDO.getPhotoArray())){
                    JSONArray jsonArray = JSONArray.parseArray(tbDisplayTableDataColumnDO.getPhotoArray());
                    for (Object obj : jsonArray) {
                        JSONObject jsonObject = (JSONObject) obj;
                        String handleUrl = jsonObject.getString("handleUrl");
                        handleUrlList.add(handleUrl);
                    }
                }
            }
            return handleUrlList;
        }
        return handleUrlList;
    }

    private String formatterTime(Date endTime, Date beginTime){
        return DateUtils.formatBetween(beginTime, endTime);
    }
}
