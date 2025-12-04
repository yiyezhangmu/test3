package com.coolcollege.intelligent.service.question.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.constant.RedisConstant;
import com.coolcollege.intelligent.common.constant.UnifyTaskConstant;
import com.coolcollege.intelligent.common.enums.*;
import com.coolcollege.intelligent.common.enums.importexcel.ExportTemplateEnum;
import com.coolcollege.intelligent.common.enums.importexcel.ImportTaskConstant;
import com.coolcollege.intelligent.common.enums.meta.MetaTablePropertyEnum;
import com.coolcollege.intelligent.common.enums.patrol.QuestionTypeEnum;
import com.coolcollege.intelligent.common.enums.region.FixedRegionEnum;
import com.coolcollege.intelligent.common.enums.video.ResourceStatusEnum;
import com.coolcollege.intelligent.common.enums.video.UploadTypeEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.page.PageVO;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.common.util.DateUtils;
import com.coolcollege.intelligent.common.util.NumberFormatUtils;
import com.coolcollege.intelligent.common.util.OpenApiParamCheckUtils;
import com.coolcollege.intelligent.dao.enterprise.dao.EnterpriseUserDao;
import com.coolcollege.intelligent.dao.metatable.TbMetaStaTableColumnMapper;
import com.coolcollege.intelligent.dao.metatable.TbMetaTableMapper;
import com.coolcollege.intelligent.dao.metatable.dao.TbMetaStaTableColumnDao;
import com.coolcollege.intelligent.dao.metatable.dao.TbMetaTableDao;
import com.coolcollege.intelligent.dao.patrolstore.TbDataStaTableColumnMapper;
import com.coolcollege.intelligent.dao.patrolstore.dao.TbDataStaTableColumnDao;
import com.coolcollege.intelligent.dao.question.TbQuestionRecordMapper;
import com.coolcollege.intelligent.dao.question.dao.QuestionHistoryDao;
import com.coolcollege.intelligent.dao.question.dao.QuestionParentInfoDao;
import com.coolcollege.intelligent.dao.question.dao.QuestionRecordDao;
import com.coolcollege.intelligent.dao.question.dao.QuestionRecordExpandDao;
import com.coolcollege.intelligent.dao.region.RegionDao;
import com.coolcollege.intelligent.dao.region.RegionMapper;
import com.coolcollege.intelligent.dao.store.StoreDao;
import com.coolcollege.intelligent.dao.storework.dao.SwStoreWorkDataTableColumnDao;
import com.coolcollege.intelligent.dao.unifytask.TaskMappingMapper;
import com.coolcollege.intelligent.dao.unifytask.TaskParentMapper;
import com.coolcollege.intelligent.dao.unifytask.TaskStoreMapper;
import com.coolcollege.intelligent.dao.unifytask.TaskSubMapper;
import com.coolcollege.intelligent.dao.unifytask.dao.*;
import com.coolcollege.intelligent.dto.EnterpriseConfigDTO;
import com.coolcollege.intelligent.facade.dto.PageDTO;
import com.coolcollege.intelligent.facade.dto.openApi.PersonBasicDTO;
import com.coolcollege.intelligent.facade.dto.openApi.QuestionDTO;
import com.coolcollege.intelligent.facade.dto.openApi.TbQuestionDealRecordVO;
import com.coolcollege.intelligent.facade.dto.openApi.vo.QuestionRecordDetailVO;
import com.coolcollege.intelligent.facade.dto.openApi.vo.QuestionRecordListVO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseUserDO;
import com.coolcollege.intelligent.model.enterprise.dto.EnterpriseUserDTO;
import com.coolcollege.intelligent.model.enterprise.dto.PersonDTO;
import com.coolcollege.intelligent.model.enterprise.dto.PersonNodeNoDTO;
import com.coolcollege.intelligent.model.enums.SendUserTypeEnum;
import com.coolcollege.intelligent.model.enums.UnifyNodeEnum;
import com.coolcollege.intelligent.model.impoetexcel.ImportTaskDO;
import com.coolcollege.intelligent.model.metatable.TbMetaStaTableColumnDO;
import com.coolcollege.intelligent.model.metatable.TbMetaTableDO;
import com.coolcollege.intelligent.model.msg.MsgUniteData;
import com.coolcollege.intelligent.model.page.PageRequest;
import com.coolcollege.intelligent.model.patrolstore.TbDataStaTableColumnDO;
import com.coolcollege.intelligent.model.question.TbQuestionHistoryDO;
import com.coolcollege.intelligent.model.question.TbQuestionParentInfoDO;
import com.coolcollege.intelligent.model.question.TbQuestionRecordDO;
import com.coolcollege.intelligent.model.question.TbQuestionRecordExpandDO;
import com.coolcollege.intelligent.model.question.dto.QuestionStageDateDTO;
import com.coolcollege.intelligent.model.question.dto.QuestionTaskInfoDTO;
import com.coolcollege.intelligent.model.question.dto.TbQuestionRecordSearchDTO;
import com.coolcollege.intelligent.model.question.request.*;
import com.coolcollege.intelligent.model.question.vo.*;
import com.coolcollege.intelligent.model.region.RegionDO;
import com.coolcollege.intelligent.model.region.dto.RegionChildDTO;
import com.coolcollege.intelligent.model.region.dto.RegionPathDTO;
import com.coolcollege.intelligent.model.region.vo.RegionPathNameVO;
import com.coolcollege.intelligent.model.selectcomponent.SelectComponentRegionVO;
import com.coolcollege.intelligent.model.store.StoreDO;
import com.coolcollege.intelligent.model.storework.SwStoreWorkDataTableColumnDO;
import com.coolcollege.intelligent.model.unifytask.TaskParentDO;
import com.coolcollege.intelligent.model.unifytask.TaskStoreDO;
import com.coolcollege.intelligent.model.unifytask.TaskSubDO;
import com.coolcollege.intelligent.model.unifytask.UnifyTaskParentItemDO;
import com.coolcollege.intelligent.model.unifytask.dto.TaskMessageDTO;
import com.coolcollege.intelligent.model.unifytask.dto.UnifyFormDataDTO;
import com.coolcollege.intelligent.model.unifytask.dto.UnifySubStatisticsDTO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.model.userholder.UserHolder;
import com.coolcollege.intelligent.model.video.SmallVideoDTO;
import com.coolcollege.intelligent.model.video.SmallVideoInfoDTO;
import com.coolcollege.intelligent.model.video.param.SmallVideoParam;
import com.coolcollege.intelligent.producer.SimpleMessageService;
import com.coolcollege.intelligent.rpc.config.EnterpriseConfigApiService;
import com.coolcollege.intelligent.service.export.ExportUtil;
import com.coolcollege.intelligent.service.importexcel.ImportTaskService;
import com.coolcollege.intelligent.service.question.QuestionParentInfoService;
import com.coolcollege.intelligent.service.question.QuestionParentUserMappingService;
import com.coolcollege.intelligent.service.question.QuestionRecordService;
import com.coolcollege.intelligent.service.region.RegionService;
import com.coolcollege.intelligent.service.selectcomponent.SelectionComponentService;
import com.coolcollege.intelligent.service.system.SysRoleService;
import com.coolcollege.intelligent.service.task.JmsTaskService;
import com.coolcollege.intelligent.service.unifytask.UnifyTaskService;
import com.coolcollege.intelligent.service.unifytask.UnifyTaskStoreService;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.coolstore.base.enums.AppTypeEnum;
import com.coolstore.base.enums.RocketMqTagEnum;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.taobao.api.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.coolcollege.intelligent.model.metatable.MetaTableConstant.BusinessTypeConstant.PATROL_STORE;

/**
 * 问题工单任务记录
 *
 * @author zhangnan
 * @date 2021-12-22 13:53
 */
@Slf4j
@Service
public class QuestionRecordServiceImpl implements QuestionRecordService {

    @Resource
    private QuestionRecordDao questionRecordDao;
    @Resource
    private QuestionRecordExpandDao questionRecordExpandDao;
    @Resource
    private TbDataStaTableColumnDao tbDataStaTableColumnDao;
    @Resource
    private TbMetaTableDao tbMetaTableDao;
    @Resource
    private StoreDao storeDao;
    @Resource
    private RegionDao regionDao;
    @Resource
    private TaskParentDao taskParentDao;
    @Resource
    private UnifyTaskParentCcUserDao unifyTaskParentCcUserDao;
    @Resource
    private EnterpriseUserDao enterpriseUserDao;
    @Resource
    private TbMetaStaTableColumnDao tbMetaStaTableColumnDao;
    @Resource
    private TaskStoreDao taskStoreDao;
    @Autowired
    TbQuestionRecordMapper tbQuestionRecordMapper;
    @Resource
    private RegionService regionService;
    @Resource
    private QuestionHistoryDao questionHistoryDao;
    @Resource
    private TaskSubMapper taskSubMapper;
    @Resource
    private ImportTaskService importTaskService;
    @Resource
    private RedisUtilPool redisUtilPool;
    @Resource
    private UnifyTaskService unifyTaskService;
    @Resource
    private TaskStoreMapper taskStoreMapper;
    @Resource
    private TaskParentMapper taskParentMapper;
    @Resource
    private TbMetaStaTableColumnMapper tbMetaStaTableColumnMapper;
    @Resource
    private TaskMappingMapper taskMappingMapper;
    @Resource
    private SimpleMessageService simpleMessageService;
    @Resource
    private TbMetaTableMapper tbMetaTableMapper;
    @Resource
    private SysRoleService sysRoleService;
    @Resource
    private QuestionParentInfoService questionParentInfoService;
    @Resource
    private QuestionParentInfoDao questionParentInfoDao;
    @Resource
    private UnifyTaskParentItemDao unifyTaskParentItemDao;
    @Resource
    private TaskSubDao taskSubDao;
    @Resource
    private QuestionParentUserMappingService questionParentUserMappingService;
    @Resource
    private JmsTaskService jmsTaskService;
    @Resource
    private EnterpriseConfigApiService enterpriseConfigApiService;
    @Resource
    private UnifyTaskStoreService unifyTaskStoreService;
    @Autowired
    RegionMapper regionMapper;
    @Resource(name = "generalThreadPool")
    private ThreadPoolTaskExecutor EXECUTOR_SERVICE;
    @Resource
    private TbDataStaTableColumnMapper dataStaTableColumnMapper;

    @Resource
    private SelectionComponentService selectionComponentService;
    @Resource
    private SwStoreWorkDataTableColumnDao swStoreWorkDataTableColumnDao;

    public static final String OVERDUE = "已逾期";
    public static final String NOT_OVERDUE = "未逾期";

    @Override
    public PageVO<TbQuestionRecordListVO> list(String enterpriseId, TbQuestionRecordSearchRequest searchRequest,
                                               PageRequest pageRequest, CurrentUser user) {
        // 查询问题工单列表（分页）
        searchRequest.setCurrentUserId(user.getUserId());
        PageInfo<TbQuestionRecordDO> pageInfo = questionRecordDao.selectQuestionRecordPage(this.parseSearchRequestToDTO(enterpriseId, searchRequest),
                pageRequest.getPageNumber(), pageRequest.getPageSize());
        // 构造分页结果，没查到数据直接返回空分页结果
        PageVO<TbQuestionRecordListVO> pageVO = new PageVO<>();
        pageVO.setTotal(pageInfo.getTotal());
        pageVO.setPageNum(pageInfo.getPageNum());
        pageVO.setPageSize(pageInfo.getPageSize());
        if (CollectionUtils.isEmpty(pageInfo.getList())) {
            pageVO.setList(Lists.newArrayList());
            return pageVO;
        }
        // 分页中获取DO列表，用作构造VO结果
        List<TbQuestionRecordDO> recordDOList = pageInfo.getList();
        // 检查项id列表，用作查询检查项标准分，检查项名称
        List<Long> metaColumnIds = recordDOList.stream().map(TbQuestionRecordDO::getMetaColumnId).distinct().collect(Collectors.toList());
        // 检查项数据id列表， 用作查询检查项采集数据
        List<Long> dataColumnIds = recordDOList.stream().map(TbQuestionRecordDO::getDataColumnId).distinct().collect(Collectors.toList());
        // 检查表id列表，用作查询检查表名称
        List<Long> metaTableIds = recordDOList.stream().map(TbQuestionRecordDO::getMetaTableId).distinct().collect(Collectors.toList());
        // 门店id列表，用作查询门店名称，门店编号
        List<String> storeIds = recordDOList.stream().map(TbQuestionRecordDO::getStoreId).distinct().collect(Collectors.toList());
        // 区域id列表，用作查询区域名称
        List<Long> regionIds = recordDOList.stream().map(TbQuestionRecordDO::getRegionId).distinct().collect(Collectors.toList());
        // 父任务id列表，用作查询任务说明，taskInfo
        List<Long> unifyTaskIds = recordDOList.stream().map(TbQuestionRecordDO::getUnifyTaskId).distinct().collect(Collectors.toList());
        // 用户id列表，用作查询处理人，创建人
        List<String> userIds = Lists.newArrayList();
        for (TbQuestionRecordDO recordDO : recordDOList) {
            userIds.add(recordDO.getHandleUserId());
            userIds.add(recordDO.getApproveUserId());
            userIds.add(recordDO.getCreateUserId());
            if (StringUtils.isNotBlank(recordDO.getSecondApproveUserId())) {
                userIds.add(recordDO.getSecondApproveUserId());
            }
            if (StringUtils.isNotBlank(recordDO.getThirdApproveUserId())) {
                userIds.add(recordDO.getThirdApproveUserId());
            }
        }
        try {
            // 查询门店任务用来查询抄送人
            List<TaskStoreDO> taskStoreDOList = taskStoreDao.selectQuestionTaskByTaskIds(enterpriseId, unifyTaskIds);
            Map<Long, String> taskStoreNodeNoMap = taskStoreDOList.stream().collect(Collectors.toMap(TaskStoreDO::getId, TaskStoreDO::getNodeNo));

            // 添加抄送人id到待查询用户id列表中
            Map<Long, List<String>> ccUserIdsMap = Maps.newHashMap();
            for (TaskStoreDO taskStoreDO : taskStoreDOList) {
                if (StringUtils.isBlank(taskStoreDO.getCcUserIds())) {
                    continue;
                }
                List<String> ccUserIdList = Arrays.stream(StringUtils.split(taskStoreDO.getCcUserIds(), Constants.COMMA)).filter(StringUtils::isNotBlank).collect(Collectors.toList());
                ccUserIdsMap.put(taskStoreDO.getId(), ccUserIdList);
                userIds.addAll(ccUserIdList);
            }
            // 查询用户
            Map<String, EnterpriseUserDO> userDOMap = enterpriseUserDao.getUserMap(enterpriseId, userIds);
            // 组装抄送人用户名map
            Map<Long, List<String>> ccUserNameMap = taskStoreDOList.stream().collect(Collectors.toMap(TaskStoreDO::getId,
                    taskStoreDO -> CollectionUtils.emptyIfNull(ccUserIdsMap.get(taskStoreDO.getId())).stream().map(ccUserId -> userDOMap.get(ccUserId).getName()).collect(Collectors.toList())
            ));
            // 根据对应表的ids查询数据检查项，数据项，检查表，门店，区域
            List<TbMetaTableDO> metaTableDOList = tbMetaTableDao.selectByIds(enterpriseId, metaTableIds);
            Map<Long, TbMetaTableDO> metaTableDOMap = metaTableDOList.stream().collect(Collectors.toMap(TbMetaTableDO::getId, Function.identity()));
            List<TbDataStaTableColumnDO> dataStaTableColumnDOList = tbDataStaTableColumnDao.selectByIds(enterpriseId, dataColumnIds);
            Map<Long, TbDataStaTableColumnDO> dataStaTableColumnDOMap = dataStaTableColumnDOList.stream().collect(Collectors.toMap(TbDataStaTableColumnDO::getId, Function.identity()));
            List<StoreDO> storeDoList = storeDao.getByStoreIdList(enterpriseId, storeIds);
            Map<String, StoreDO> storeDOMap = storeDoList.stream().collect(Collectors.toMap(StoreDO::getStoreId, Function.identity()));
            List<RegionDO> regionDOList = regionDao.getAllRegionByRegionIds(enterpriseId, regionIds);
            Map<Long, RegionDO> regionDOMap = regionDOList.stream().collect(Collectors.toMap(RegionDO::getId, Function.identity()));
            List<TaskParentDO> taskParentDOList = taskParentDao.selectByIds(enterpriseId, unifyTaskIds);
            Map<Long, TaskParentDO> taskParentDOMap = taskParentDOList.stream().collect(Collectors.toMap(TaskParentDO::getId, Function.identity()));
            List<TbMetaStaTableColumnDO> metaStaTableColumnDOList = tbMetaStaTableColumnDao.selectByIds(enterpriseId, metaColumnIds);
            Map<Long, TbMetaStaTableColumnDO> metaStaTableColumnDOMap = metaStaTableColumnDOList.stream().collect(Collectors.toMap(TbMetaStaTableColumnDO::getId, Function.identity()));
            // 构建VO数据
            pageVO.setList(recordDOList.stream().map(recordDO -> this.parseDOToQuestionListVO(recordDO, metaStaTableColumnDOMap, dataStaTableColumnDOMap,
                            metaTableDOMap, storeDOMap, regionDOMap,
                            taskParentDOMap, userDOMap, ccUserNameMap, taskStoreNodeNoMap))
                    .collect(Collectors.toList()));
        } catch (Exception e) {
            log.error("question record service list error", e);
            throw new ServiceException(ErrorCodeEnum.DATA_ERROR);
        }
        return pageVO;
    }

    @Override
    public TbQuestionRecordDetailVO detail(String enterpriseId, Long unifyTaskId, String storeId, CurrentUser user, Long loopCount) {
        loopCount = loopCount == null ? 1L : loopCount;
        TbQuestionRecordDO recordDO = questionRecordDao.selectByTaskIdAndStoreId(enterpriseId, unifyTaskId, storeId, loopCount);
        TbQuestionRecordExpandDO expandDO = questionRecordExpandDao.selectByRecordId(enterpriseId, recordDO.getId());
        // 查询门店任务
        TaskStoreDO taskStoreDO = taskStoreDao.selectById(enterpriseId, recordDO.getTaskStoreId());
        if (taskStoreDO == null) {
            throw new ServiceException(ErrorCodeEnum.TASK_NOT_EXIST);
        }
        List<String> userIds = Lists.newArrayList(recordDO.getCreateUserId(), recordDO.getHandleUserId(), recordDO.getApproveUserId());
        // 抄送人
        List<String> ccUserIds = Lists.newArrayList(StringUtils.split(Optional.ofNullable(taskStoreDO.getCcUserIds())
                .orElse(Constants.EMPTY_STRING), Constants.COMMA));
        userIds.addAll(ccUserIds);
        // 获取门店任务的extendInfo字段，后面用作获取处理人和审批人
        JSONObject extendInfo = JSONObject.parseObject(taskStoreDO.getExtendInfo());
        // 处理人，如果是待处理状态 在子任务查询处理人。其他状态可以在taskStore查询
        List<String> handleUserIds = null;
        if (UnifyNodeEnum.FIRST_NODE.getCode().equals(recordDO.getStatus())) {
            List<PersonNodeNoDTO> personNodeNoDTOList = taskSubMapper.selectUserIdByLoopCountAndStoreIdList(enterpriseId, unifyTaskId,
                    Lists.newArrayList(recordDO.getStoreId()), loopCount, null);
            handleUserIds = personNodeNoDTOList.stream().map(PersonNodeNoDTO::getUserId).collect(Collectors.toList());
        } else {
            // taskStore中extendInfo获取第一个节点，处理人
            handleUserIds = Lists.newArrayList(StringUtils.split(Optional.ofNullable(extendInfo.getString(UnifyNodeEnum.FIRST_NODE.getCode()))
                    .orElse(Constants.EMPTY_STRING), Constants.COMMA));
        }
        userIds.addAll(handleUserIds);
        // taskStore中extendInfo获取第二个节点，审批人
        List<String> secondNodePersons = Lists.newArrayList(StringUtils.split(Optional.ofNullable(extendInfo.getString(UnifyNodeEnum.SECOND_NODE.getCode()))
                .orElse(Constants.EMPTY_STRING), Constants.COMMA));
        userIds.addAll(secondNodePersons);
        // taskStore中extendInfo获取第三个节点，审批人
        List<String> thirdNodePersons = Lists.newArrayList(StringUtils.split(Optional.ofNullable(extendInfo.getString(UnifyNodeEnum.THIRD_NODE.getCode()))
                .orElse(Constants.EMPTY_STRING), Constants.COMMA));
        userIds.addAll(thirdNodePersons);
        // taskStore中extendInfo获取第三个节点，审批人
        List<String> fourNodePersons = Lists.newArrayList(StringUtils.split(Optional.ofNullable(extendInfo.getString(UnifyNodeEnum.FOUR_NODE.getCode()))
                .orElse(Constants.EMPTY_STRING), Constants.COMMA));
        userIds.addAll(fourNodePersons);
        if (StringUtils.isNotBlank(recordDO.getSecondApproveUserId())) {
            userIds.add(recordDO.getSecondApproveUserId());
        }
        if (StringUtils.isNotBlank(recordDO.getThirdApproveUserId())) {
            userIds.add(recordDO.getThirdApproveUserId());
        }
        // 查询用户
        List<EnterpriseUserDO> userDOList = enterpriseUserDao.selectByUserIds(enterpriseId, userIds.stream().distinct().collect(Collectors.toList()));
        Map<String, PersonDTO> userDOMap = userDOList.stream().collect(Collectors.toMap(EnterpriseUserDO::getUserId, this::parseUserToPersonDTO));
        try {
            TbMetaStaTableColumnDO metaStaTableColumnDO = tbMetaStaTableColumnDao.selectById(enterpriseId, recordDO.getMetaColumnId());
            TbDataStaTableColumnDO dataStaTableColumnDO = tbDataStaTableColumnDao.selectById(enterpriseId, recordDO.getDataColumnId());
            TbMetaTableDO metaTableDO = tbMetaTableDao.selectById(enterpriseId, recordDO.getMetaTableId());
            StoreDO storeDO = storeDao.getByStoreId(enterpriseId, recordDO.getStoreId());
            RegionDO regionDO = regionDao.getRegionById(enterpriseId, recordDO.getRegionId());
            String userId = Optional.ofNullable(user).map(CurrentUser::getUserId).orElse("");
            return this.parseDOToQuestionDetailVO(enterpriseId, recordDO, metaTableDO, metaStaTableColumnDO, dataStaTableColumnDO,
                    storeDO, regionDO, expandDO, userDOMap,
                    handleUserIds, secondNodePersons, thirdNodePersons, fourNodePersons, ccUserIds, userId, taskStoreDO.getNodeNo());
        } catch (Exception e) {
            log.error("question record service detail error", e);
            throw new ServiceException(ErrorCodeEnum.DATA_ERROR);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateQuestionTaskRecord(TaskMessageDTO taskMessageDTO, Boolean isComplete) {
        String enterpriseId = taskMessageDTO.getEnterpriseId();

        JSONObject taskHandleDataObj = new JSONObject();
        String taskHandleData = taskMessageDTO.getTaskHandleData();
        if (StringUtils.isNotBlank(taskHandleData)) {
            taskHandleDataObj = JSONObject.parseObject(taskHandleData);
        }
        //增加处理就
        addHistoryAndPhoto(enterpriseId, isComplete, taskHandleDataObj, taskMessageDTO.getNodeNo());
        return true;
    }

    @Override
    public PageVO<TbQuestionRecordMobileListVO> listForMobile(String enterpriseId, TbQuestionRecordSearchRequest searchRequest,
                                                              PageRequest pageRequest, CurrentUser user) {
        // 查询问题工单列表（分页）
        PageInfo<TbQuestionRecordDO> pageInfo = questionRecordDao.selectQuestionRecordPage(this.parseSearchRequestToDTO(enterpriseId, searchRequest),
                pageRequest.getPageNumber(), pageRequest.getPageSize());
        // 构造分页结果，没查到数据直接返回空分页结果
        PageVO<TbQuestionRecordMobileListVO> pageVO = new PageVO<>();
        pageVO.setTotal(pageInfo.getTotal());
        pageVO.setPageNum(pageInfo.getPageNum());
        pageVO.setPageSize(pageInfo.getPageSize());
        if (CollectionUtils.isEmpty(pageInfo.getList())) {
            pageVO.setList(Lists.newArrayList());
            return pageVO;
        }
        // 分页中获取DO列表，用作构造VO结果
        List<TbQuestionRecordDO> recordDOList = pageInfo.getList();
        // 检查项id列表，用作查询检查项标准分，检查项名称
        List<Long> metaColumnIds = recordDOList.stream().map(TbQuestionRecordDO::getMetaColumnId).distinct().collect(Collectors.toList());
        // 门店id列表，用作查询门店名称，门店编号
        List<String> storeIds = recordDOList.stream().map(TbQuestionRecordDO::getStoreId).distinct().collect(Collectors.toList());
        // 用户id列表，用作查询处理人
        List<String> userIds = recordDOList.stream().map(TbQuestionRecordDO::getHandleUserId).collect(Collectors.toList());
        userIds.addAll(recordDOList.stream().map(TbQuestionRecordDO::getApproveUserId).collect(Collectors.toList()));
        userIds.addAll(recordDOList.stream().map(TbQuestionRecordDO::getCreateUserId).collect(Collectors.toList()));
        //查询条件审批人，不区分节点
        String approveId = searchRequest.getApproveUserId();
        if (StringUtils.isNotBlank(searchRequest.getSecondApproveUserId())) {
            approveId = searchRequest.getSecondApproveUserId();
        }
        if (StringUtils.isNotBlank(searchRequest.getThirdApproveUserId())) {
            approveId = searchRequest.getThirdApproveUserId();
        }
        if (StringUtils.isNotBlank(approveId)) {
            userIds.add(approveId);
        }
        // 父任务id列表，用作查询任务说明，taskInfo
        List<Long> unifyTaskIds = recordDOList.stream().map(TbQuestionRecordDO::getUnifyTaskId).distinct().collect(Collectors.toList());
        try {
            // 查询门店任务用来查询抄送人
            List<TaskStoreDO> taskStoreDOList = taskStoreDao.selectQuestionTaskByTaskIds(enterpriseId, unifyTaskIds);
            Map<Long, String> taskStoreNodeNoMap = taskStoreDOList.stream().collect(Collectors.toMap(TaskStoreDO::getId, TaskStoreDO::getNodeNo));

            // 查询用户
            Map<String, EnterpriseUserDO> userDOMap = enterpriseUserDao.getUserMap(enterpriseId, userIds.stream().distinct().collect(Collectors.toList()));
            List<TbMetaStaTableColumnDO> metaStaTableColumnDOList = tbMetaStaTableColumnDao.selectByIds(enterpriseId, metaColumnIds);
            Map<Long, TbMetaStaTableColumnDO> metaStaTableColumnDOMap = metaStaTableColumnDOList.stream().collect(Collectors.toMap(TbMetaStaTableColumnDO::getId, Function.identity()));
            List<StoreDO> storeDoList = storeDao.getByStoreIdList(enterpriseId, storeIds);
            Map<String, StoreDO> storeDOMap = storeDoList.stream().collect(Collectors.toMap(StoreDO::getStoreId, Function.identity()));
            // 构建VO数据
            String finalApproveId = approveId;
            pageVO.setList(recordDOList.stream().map(recordDO -> this.parseDOToQuestionMobileListVO(recordDO, metaStaTableColumnDOMap,
                    storeDOMap, userDOMap, taskStoreNodeNoMap, finalApproveId)).collect(Collectors.toList()));
        } catch (Exception e) {
            log.error("question record service listForMobile error", e);
            throw new ServiceException(ErrorCodeEnum.DATA_ERROR);
        }
        return pageVO;
    }

    @Override
    public ImportTaskDO export(String enterpriseId, TbQuestionRecordSearchRequest recordSearchRequest, CurrentUser user) {
        // 查询导出数量，
        recordSearchRequest.setCurrentUserId(user.getUserId());
        Long count = questionRecordDao.countQuestionRecords(this.parseSearchRequestToDTO(enterpriseId, recordSearchRequest));
        if (count == null || count == 0L) {
            throw new ServiceException("当前无记录可导出");
        }
        if (count > Constants.MAX_EXPORT_SIZE) {
            throw new ServiceException("导出数据不能超过" + Constants.MAX_EXPORT_SIZE + "条，请缩小导出范围");
        }
        // 通过枚举获取文件名称
        String fileName = ExportTemplateEnum.getByCode(ImportTaskConstant.TB_QUESTION_RECORD);
        // 保存导出任务
        ImportTaskDO importTaskDO = importTaskService.insertExportTask(enterpriseId, fileName, ImportTaskConstant.TASK_QUESTION_REPORT);
        // 构造异步导出参数
        recordSearchRequest.setCurrentUserId(user.getUserId());
        ExportTbQuestionRecordRequest msg = new ExportTbQuestionRecordRequest();
        msg.setEnterpriseId(enterpriseId);
        msg.setRequest(recordSearchRequest);
        msg.setTotalNum(count);
        msg.setImportTaskDO(importTaskDO);
        msg.setDbName(user.getDbName());
        MsgUniteData msgUniteData = new MsgUniteData();
        msgUniteData.setData(JSONObject.toJSONString(msg));
        msgUniteData.setMsgType(MsgUniteDataTypeEnum.TB_QUESTION_RECORD.getCode());
        //分开异步导出
        simpleMessageService.send(JSONObject.toJSONString(msgUniteData), RocketMqTagEnum.EXPORT_IMPORT_MESSAGE);
        return importTaskDO;
    }

    @Override
    public PageVO<TbQuestionRecordExportVO> listForExport(String enterpriseId, TbQuestionRecordSearchRequest searchRequest, PageRequest pageRequest, String dbName) {
        // 查询问题工单列表（分页）
        PageInfo<TbQuestionRecordDO> pageInfo = questionRecordDao.selectQuestionRecordPage(this.parseSearchRequestToDTO(enterpriseId, searchRequest),
                pageRequest.getPageNumber(), pageRequest.getPageSize());
        // 构造分页结果，没查到数据直接返回空分页结果
        PageVO<TbQuestionRecordExportVO> pageVO = new PageVO<>();
        pageVO.setTotal(pageInfo.getTotal());
        pageVO.setPageNum(pageInfo.getPageNum());
        pageVO.setPageSize(pageInfo.getPageSize());
        if (CollectionUtils.isEmpty(pageInfo.getList())) {
            pageVO.setList(Lists.newArrayList());
            return pageVO;
        }
        List<Long> questionRecordIds = pageInfo.getList().stream().map(TbQuestionRecordDO::getId).distinct().collect(Collectors.toList());
        // 检查项id列表，用作查询检查项标准分，检查项名称
        List<Long> metaColumnIds = pageInfo.getList().stream().map(TbQuestionRecordDO::getMetaColumnId).distinct().collect(Collectors.toList());
        // 检查项数据id列表， 用作查询检查项采集数据
        List<Long> dataColumnIds = pageInfo.getList().stream().map(TbQuestionRecordDO::getDataColumnId).distinct().collect(Collectors.toList());
        // 检查表id列表，用作查询检查表名称
        List<Long> metaTableIds = pageInfo.getList().stream().map(TbQuestionRecordDO::getMetaTableId).distinct().collect(Collectors.toList());
        // 门店id列表，用作查询门店名称，门店编号
        List<String> storeIds = pageInfo.getList().stream().map(TbQuestionRecordDO::getStoreId).distinct().collect(Collectors.toList());
        // 父任务id列表，用作查询任务说明，taskInfo
        List<Long> unifyTaskIds = pageInfo.getList().stream().map(TbQuestionRecordDO::getUnifyTaskId).distinct().collect(Collectors.toList());
        // 用户id列表，用作查询处理人，创建人
        List<String> userIds = Lists.newArrayList();
        for (TbQuestionRecordDO recordDO : pageInfo.getList()) {
            userIds.add(recordDO.getCreateUserId());
            userIds.add(recordDO.getHandleUserId());
            userIds.add(recordDO.getApproveUserId());
            if (StringUtils.isNotBlank(recordDO.getSecondApproveUserId())) {
                userIds.add(recordDO.getSecondApproveUserId());
            }
            if (StringUtils.isNotBlank(recordDO.getThirdApproveUserId())) {
                userIds.add(recordDO.getThirdApproveUserId());
            }
        }
        try {
            // 查询门店任务用来查询抄送人
            List<TaskStoreDO> taskStoreDOList = taskStoreDao.selectQuestionTaskByTaskIds(enterpriseId, unifyTaskIds);
            // 添加抄送人id到待查询用户id列表中
            Map<Long, List<String>> ccUserIdMap = Maps.newHashMap();
            Map<Long, List<String>> handleUserIdMap = Maps.newHashMap();
            Map<Long, List<String>> approveUserIdMap = Maps.newHashMap();
            Map<Long, List<String>> secondApproveUserIdMap = Maps.newHashMap();
            Map<Long, List<String>> thirdApproveUserIdMap = Maps.newHashMap();
            for (TaskStoreDO taskStoreDO : taskStoreDOList) {
                if (StringUtils.isNotBlank(taskStoreDO.getCcUserIds())) {
                    List<String> ccUserIdList = Arrays.stream(StringUtils.split(taskStoreDO.getCcUserIds(), Constants.COMMA)).filter(StringUtils::isNotBlank).collect(Collectors.toList());
                    userIds.addAll(ccUserIdList);
                    ccUserIdMap.put(taskStoreDO.getId(), ccUserIdList);
                }
                Map<String, List<String>> taskNodeNoUserMap = unifyTaskStoreService.getNodePersonByTaskStore(taskStoreDO);
                if (taskNodeNoUserMap.containsKey(UnifyNodeEnum.FIRST_NODE.getCode())) {
                    userIds.addAll(taskNodeNoUserMap.get(UnifyNodeEnum.FIRST_NODE.getCode()));
                    handleUserIdMap.put(taskStoreDO.getId(), taskNodeNoUserMap.get(UnifyNodeEnum.FIRST_NODE.getCode()));
                }
                if (taskNodeNoUserMap.containsKey(UnifyNodeEnum.SECOND_NODE.getCode())) {
                    userIds.addAll(taskNodeNoUserMap.get(UnifyNodeEnum.SECOND_NODE.getCode()));
                    approveUserIdMap.put(taskStoreDO.getId(), taskNodeNoUserMap.get(UnifyNodeEnum.SECOND_NODE.getCode()));
                }
                if (taskNodeNoUserMap.containsKey(UnifyNodeEnum.THIRD_NODE.getCode())) {
                    userIds.addAll(taskNodeNoUserMap.get(UnifyNodeEnum.THIRD_NODE.getCode()));
                    secondApproveUserIdMap.put(taskStoreDO.getId(), taskNodeNoUserMap.get(UnifyNodeEnum.THIRD_NODE.getCode()));
                }
                if (taskNodeNoUserMap.containsKey(UnifyNodeEnum.FOUR_NODE.getCode())) {
                    userIds.addAll(taskNodeNoUserMap.get(UnifyNodeEnum.FOUR_NODE.getCode()));
                    thirdApproveUserIdMap.put(taskStoreDO.getId(), taskNodeNoUserMap.get(UnifyNodeEnum.FOUR_NODE.getCode()));
                }
            }
            // 查询用户
            Map<String, EnterpriseUserDO> userDOMap = enterpriseUserDao.getUserMap(enterpriseId, userIds);
            // 组装抄送人用户名map
            Map<Long, List<String>> ccUserNameMap = taskStoreDOList.stream().collect(Collectors.toMap(TaskStoreDO::getId,
                    taskStoreDO -> CollectionUtils.emptyIfNull(ccUserIdMap.get(taskStoreDO.getId())).stream().filter(userId -> userDOMap.get(userId) != null).map(ccUserId -> userDOMap.get(ccUserId).getName()).collect(Collectors.toList())
            ));
            // 组装指派整改人用户名map
            Map<Long, List<String>> handleUserNameMap = taskStoreDOList.stream().collect(Collectors.toMap(TaskStoreDO::getId,
                    taskStoreDO -> CollectionUtils.emptyIfNull(handleUserIdMap.get(taskStoreDO.getId())).stream().filter(u -> userDOMap.get(u) != null).map(userId -> userDOMap.get(userId).getName()).collect(Collectors.toList())
            ));
            // 组装指派一级审批人用户名map
            Map<Long, List<String>> approveUserNameMap = taskStoreDOList.stream().collect(Collectors.toMap(TaskStoreDO::getId,
                    taskStoreDO -> CollectionUtils.emptyIfNull(approveUserIdMap.get(taskStoreDO.getId())).stream().filter(userId -> userDOMap.get(userId) != null).map(userId -> userDOMap.get(userId).getName()).collect(Collectors.toList())
            ));
            // 组装指派二级审批人用户名map
            Map<Long, List<String>> secondApproveUserNameMap = taskStoreDOList.stream().collect(Collectors.toMap(TaskStoreDO::getId,
                    taskStoreDO -> CollectionUtils.emptyIfNull(secondApproveUserIdMap.get(taskStoreDO.getId())).stream().filter(userId -> userDOMap.get(userId) != null).map(userId -> userDOMap.get(userId).getName()).collect(Collectors.toList())
            ));
            // 组装指派三级审批人用户名map
            Map<Long, List<String>> thirdApproveUserNameMap = taskStoreDOList.stream().collect(Collectors.toMap(TaskStoreDO::getId,
                    taskStoreDO -> CollectionUtils.emptyIfNull(thirdApproveUserIdMap.get(taskStoreDO.getId())).stream().filter(userId -> userDOMap.get(userId) != null).map(userId -> userDOMap.get(userId).getName()).collect(Collectors.toList())
            ));
            List<TbMetaStaTableColumnDO> metaStaTableColumnDOList = tbMetaStaTableColumnDao.selectByIds(enterpriseId, metaColumnIds);
            Map<Long, TbMetaStaTableColumnDO> metaStaTableColumnDOMap = metaStaTableColumnDOList.stream().collect(Collectors.toMap(TbMetaStaTableColumnDO::getId, Function.identity()));
            List<TbDataStaTableColumnDO> dataStaTableColumnDOList = tbDataStaTableColumnDao.selectByIds(enterpriseId, dataColumnIds);
            Map<Long, TbDataStaTableColumnDO> dataStaTableColumnDOMap = dataStaTableColumnDOList.stream().collect(Collectors.toMap(TbDataStaTableColumnDO::getId, Function.identity()));
            List<TbMetaTableDO> metaTableDOList = tbMetaTableDao.selectByIds(enterpriseId, metaTableIds);
            Map<Long, TbMetaTableDO> metaTableDOMap = metaTableDOList.stream().collect(Collectors.toMap(TbMetaTableDO::getId, Function.identity()));
            List<StoreDO> storeDoList = storeDao.getByStoreIdList(enterpriseId, storeIds);
            Map<String, StoreDO> storeDOMap = storeDoList.stream().collect(Collectors.toMap(StoreDO::getStoreId, Function.identity()));
            List<TaskParentDO> taskParentDOList = taskParentDao.selectByIds(enterpriseId, unifyTaskIds);
            Map<Long, TaskParentDO> taskParentDOMap = taskParentDOList.stream().collect(Collectors.toMap(TaskParentDO::getId, Function.identity()));
            List<TbQuestionRecordExpandDO> expandDOList = questionRecordExpandDao.selectByQuestionRecordIds(enterpriseId, questionRecordIds);
            Map<Long, TbQuestionRecordExpandDO> expandDOMap = expandDOList.stream().collect(Collectors.toMap(TbQuestionRecordExpandDO::getRecordId, Function.identity()));
            List<TbQuestionHistoryVO> questionHistoryVOList = questionHistoryDao.selectLatestHistoryListByRecordIdList(enterpriseId, questionRecordIds, null);
            Map<String, TbQuestionHistoryVO> questionHistoryMap = questionHistoryVOList.stream().collect(Collectors.toMap(e -> e.getRecordId() + Constants.MOSAICS + e.getNodeNo(), Function.identity(), (a, b) -> a));
            // 构建VO数据
            pageVO.setList(pageInfo.getList().stream().map(recordDO -> this.parseDOToQuestionExportVO(enterpriseId, recordDO, metaStaTableColumnDOMap,
                            dataStaTableColumnDOMap, metaTableDOMap, storeDOMap,
                            taskParentDOMap, userDOMap, ccUserNameMap, expandDOMap, questionHistoryMap, handleUserNameMap, approveUserNameMap,
                            secondApproveUserNameMap, thirdApproveUserNameMap))
                    .collect(Collectors.toList()));
            return pageVO;
        } catch (Exception e) {
            log.error("question record service export error", e);
            throw new ServiceException(ErrorCodeEnum.DATA_ERROR);
        }
    }

    @Override
    public void deleteByQuestionRecordId(String enterpriseId, Long questionRecordId, String dingCorpId, String appType) {
        TbQuestionRecordDO recordDO = questionRecordDao.selectById(questionRecordId, enterpriseId);
        if (Objects.isNull(recordDO) || recordDO.getDeleted()) {
            return;
        }
        // 检查项id不为0是巡店工单，不可以删除
        if (!Constants.LONG_ZERO.equals(recordDO.getDataColumnId())) {
            throw new ServiceException(ErrorCodeEnum.TB_QUESTION_RECORD_EXIST_COLUMN_ERROR);
        }
        unifyTaskService.batchDelUnifyTask(enterpriseId, Lists.newArrayList(recordDO.getUnifyTaskId()), dingCorpId, appType);
    }


    /**
     * DO转ListVO
     *
     * @param recordDO                TbQuestionRecordDO
     * @param metaStaTableColumnDOMap 检查项map
     * @param dataStaTableColumnDOMap 检查项数据map
     * @param metaTableDOMap          检查表map
     * @param storeDOMap              门店map
     * @param regionDOMap             区域map
     * @param taskParentDOMap         父任务map
     * @param userDOMap               用户map
     * @param ccUserNameMap           抄送人map
     * @return TbQuestionRecordListVO
     */
    private TbQuestionRecordListVO parseDOToQuestionListVO(TbQuestionRecordDO recordDO, Map<Long, TbMetaStaTableColumnDO> metaStaTableColumnDOMap,
                                                           Map<Long, TbDataStaTableColumnDO> dataStaTableColumnDOMap, Map<Long, TbMetaTableDO> metaTableDOMap,
                                                           Map<String, StoreDO> storeDOMap, Map<Long, RegionDO> regionDOMap,
                                                           Map<Long, TaskParentDO> taskParentDOMap, Map<String, EnterpriseUserDO> userDOMap,
                                                           Map<Long, List<String>> ccUserNameMap, Map<Long, String> taskStoreNodeNoMap) {
        TbQuestionRecordListVO recordListVO = new TbQuestionRecordListVO();
        recordListVO.setId(recordDO.getId());
        recordListVO.setTaskName(recordDO.getTaskName());
        recordListVO.setTaskStoreId(recordDO.getTaskStoreId());
        // 检查项id不等于0
        recordListVO.setMetaColumnId(recordDO.getMetaColumnId());
        TbMetaStaTableColumnDO metaStaTableColumnDO = metaStaTableColumnDOMap.get(recordDO.getMetaColumnId());
        if (Objects.nonNull(metaStaTableColumnDO)) {
            recordListVO.setMetaColumnName(metaStaTableColumnDO.getColumnName());
            recordListVO.setSupportScore(metaStaTableColumnDO.getSupportScore());
            recordListVO.setDataColumnId(recordDO.getDataColumnId());
            // 检查表
            recordListVO.setMetaTableId(recordDO.getMetaTableId());
        }
        TbMetaTableDO tableDO = metaTableDOMap.get(recordDO.getMetaTableId());
        if (Objects.nonNull(tableDO)) {
            recordListVO.setMetaTableName(tableDO.getTableName());
        }
        // 数据检查项id不等于0
        TbDataStaTableColumnDO dataStaTableColumnDO = dataStaTableColumnDOMap.get(recordDO.getDataColumnId());
        if (Objects.nonNull(dataStaTableColumnDO)) {
            BigDecimal checkScore = dataStaTableColumnDO.getCheckScore().multiply(dataStaTableColumnDO.getScoreTimes()).setScale(2, BigDecimal.ROUND_HALF_UP);
            if (tableDO != null && MetaTablePropertyEnum.WEIGHT_TABLE.getCode().equals(tableDO.getTableProperty())) {
                checkScore = checkScore.multiply(dataStaTableColumnDO.getWeightPercent().divide(new BigDecimal(Constants.ONE_HUNDRED), 2, BigDecimal.ROUND_HALF_UP));
            }
            recordListVO.setCheckScore(checkScore);
            //RewardPenaltMoney*加倍数
            recordListVO.setRewardPenaltMoney(dataStaTableColumnDO.getRewardPenaltMoney().multiply(dataStaTableColumnDO.getAwardTimes()).setScale(2, BigDecimal.ROUND_HALF_UP));
        }
        // 门店信息
        StoreDO storeDO = storeDOMap.get(recordDO.getStoreId());
        if (Objects.nonNull(storeDO)) {
            recordListVO.setStoreId(storeDO.getStoreId());
            recordListVO.setStoreName(storeDO.getStoreName());
            recordListVO.setStoreNum(storeDO.getStoreNum());
        }
        // 区域信息
        RegionDO regionDO = regionDOMap.get(recordDO.getRegionId());
        if (Objects.nonNull(regionDO)) {
            recordListVO.setRegionId(regionDO.getId());
            recordListVO.setRegionPath(regionDO.getRegionPath());
            recordListVO.setRegionName(regionDO.getName());
        }
        // 任务说明任务,图片，视频
        TaskParentDO taskParentDO = taskParentDOMap.get(recordDO.getUnifyTaskId());
        recordListVO.setUnifyTaskId(recordDO.getUnifyTaskId());
        recordListVO.setTaskDesc(taskParentDO.getTaskDesc());
        recordListVO.setTaskInfo(taskParentDO.getTaskInfo());
        // 判断逾期
        recordListVO.setIsOverdue(this.checkOverdue(recordDO));
        recordListVO.setStatus(recordDO.getStatus());
        // 用户
        recordListVO.setCreateUserId(recordDO.getCreateUserId());
        EnterpriseUserDO createUser = userDOMap.get(recordDO.getCreateUserId());
        if (Objects.nonNull(createUser)) {
            recordListVO.setCreateUserName(createUser.getName());
        } else if (Constants.AI.equals(recordDO.getCreateUserId())) {
            recordListVO.setCreateUserName(Constants.AI);
        }
        recordListVO.setHandleUserId(recordDO.getHandleUserId());
        EnterpriseUserDO handleUser = userDOMap.get(recordDO.getHandleUserId());
        if (Objects.nonNull(handleUser)) {
            recordListVO.setHandleUserName(handleUser.getName());
        }
        recordListVO.setApproveUserId(recordDO.getApproveUserId());
        EnterpriseUserDO approveUser = userDOMap.get(recordDO.getApproveUserId());
        if (Objects.nonNull(approveUser)) {
            recordListVO.setApproveUserName(approveUser.getName());
        }
        recordListVO.setCreateTime(recordDO.getCreateTime());
        recordListVO.setHandleActionKey(recordDO.getHandleActionKey());
        recordListVO.setApproveActionKey(recordDO.getApproveActionKey());
        // 抄送人
        recordListVO.setCcUserNames(ccUserNameMap.get(recordDO.getTaskStoreId()));
        recordListVO.setCompleteTime(recordDO.getCompleteTime());
        recordListVO.setHandlerEndTime(recordDO.getSubEndTime());
        // 完成时间不为空，计算总用时
        if (!Objects.isNull(recordDO.getCompleteTime())) {
            recordListVO.setTotalDurationTime(DateUtil.betweenMs(recordDO.getCreateTime(), recordDO.getCompleteTime()));
            recordListVO.setTotalDuration(DateUtils.formatBetween(recordListVO.getTotalDurationTime()));
        }
        recordListVO.setCreateType(recordDO.getCreateType());
        recordListVO.setLearnFirst(recordDO.getLearnFirst());
        recordListVO.setSecondApproveUserId(recordDO.getSecondApproveUserId());
        recordListVO.setSecondApproveActionKey(recordDO.getSecondApproveActionKey());
        //二级审批人
        EnterpriseUserDO secondApproveUser = userDOMap.get(recordDO.getSecondApproveUserId());
        if (Objects.nonNull(secondApproveUser)) {
            recordListVO.setSecondApproveUserName(secondApproveUser.getName());
        }
        recordListVO.setThirdApproveUserId(recordDO.getThirdApproveUserId());
        recordListVO.setThirdApproveActionKey(recordDO.getThirdApproveActionKey());
        //三级审批人
        EnterpriseUserDO thirdApproveUser = userDOMap.get(recordDO.getThirdApproveUserId());
        if (Objects.nonNull(thirdApproveUser)) {
            recordListVO.setThirdApproveUserName(thirdApproveUser.getName());
        }
        recordListVO.setNodeNo(taskStoreNodeNoMap.get(recordDO.getTaskStoreId()));
        return recordListVO;
    }

    private TbQuestionRecordMobileListVO parseDOToQuestionMobileListVO(TbQuestionRecordDO recordDO, Map<Long, TbMetaStaTableColumnDO> metaStaTableColumnDOMap,
                                                                       Map<String, StoreDO> storeDOMap, Map<String, EnterpriseUserDO> userDOMap,
                                                                       Map<Long, String> taskStoreNodeNoMap, String approveId) {
        TbQuestionRecordMobileListVO recordListVO = new TbQuestionRecordMobileListVO();
        recordListVO.setId(recordDO.getId());
        recordListVO.setTaskName(recordDO.getTaskName());
        recordListVO.setTaskStoreId(recordDO.getTaskStoreId());
        // 检查项id不等于0，获取检查项数据
        recordListVO.setMetaColumnId(recordDO.getMetaColumnId());
        TbMetaStaTableColumnDO metaStaTableColumnDO = metaStaTableColumnDOMap.get(recordDO.getMetaColumnId());
        if (Objects.nonNull(metaStaTableColumnDO)) {
            recordListVO.setMetaColumnName(metaStaTableColumnDO.getColumnName());
            recordListVO.setDataColumnId(recordDO.getDataColumnId());
        }
        // 门店信息
        StoreDO storeDO = storeDOMap.get(recordDO.getStoreId());
        if (Objects.nonNull(storeDO)) {
            recordListVO.setStoreId(storeDO.getStoreId());
            recordListVO.setStoreName(storeDO.getStoreName());
        }
        recordListVO.setUnifyTaskId(recordDO.getUnifyTaskId());
        // 判断逾期
        recordListVO.setIsOverdue(this.checkOverdue(recordDO));
        recordListVO.setStatus(recordDO.getStatus());
        // 用户
        recordListVO.setCreateUserId(recordDO.getCreateUserId());
        EnterpriseUserDO createUser = userDOMap.get(recordDO.getCreateUserId());
        if (Objects.nonNull(createUser)) {
            recordListVO.setCreateUserName(createUser.getName());
        } else if (Constants.AI.equals(recordDO.getCreateUserId())) {
            recordListVO.setCreateUserName(Constants.AI);
        }
        recordListVO.setHandleUserId(recordDO.getHandleUserId());
        EnterpriseUserDO handleUser = userDOMap.get(recordDO.getHandleUserId());
        if (Objects.nonNull(handleUser)) {
            recordListVO.setHandleUserName(handleUser.getName());
        }
        recordListVO.setApproveUserId(recordDO.getApproveUserId());
        EnterpriseUserDO approveUser = userDOMap.get(recordDO.getApproveUserId());
        if (Objects.nonNull(approveUser)) {
            recordListVO.setApproveUserName(approveUser.getName());
        }
        if (StringUtils.isNotBlank(approveId)) {
            recordListVO.setApproveUserId(approveId);
            EnterpriseUserDO approveUserDO = userDOMap.get(approveId);
            if (Objects.nonNull(approveUserDO)) {
                recordListVO.setApproveUserName(approveUserDO.getName());
            }
        }

        recordListVO.setCreateTime(recordDO.getCreateTime());
        recordListVO.setHandlerEndTime(recordDO.getSubEndTime());
        recordListVO.setCreateType(recordDO.getCreateType());
        recordListVO.setLearnFirst(recordDO.getLearnFirst());
        recordListVO.setNodeNo(taskStoreNodeNoMap.get(recordDO.getTaskStoreId()));
        return recordListVO;
    }

    private TbQuestionRecordDetailVO parseDOToQuestionDetailVO(String enterpriseId, TbQuestionRecordDO recordDO, TbMetaTableDO metaTableDO, TbMetaStaTableColumnDO metaStaTableColumnDO,
                                                               TbDataStaTableColumnDO dataStaTableColumnDO, StoreDO storeDO, RegionDO regionDO,
                                                               TbQuestionRecordExpandDO expandDO, Map<String, PersonDTO> userDOMap,
                                                               List<String> handleUserIds, List<String> approveUserIds,
                                                               List<String> secondApproveUserIds, List<String> thirdApproveUserIds,
                                                               List<String> ccUserIds,
                                                               String userId, String nodeNo) {
        TbQuestionRecordDetailVO detailVO = new TbQuestionRecordDetailVO();
        detailVO.setId(recordDO.getId());
        detailVO.setLoopCount(recordDO.getLoopCount());
        detailVO.setTaskName(recordDO.getTaskName());
        detailVO.setStatus(nodeNo);
        detailVO.setCreateType(recordDO.getCreateType());
        detailVO.setLearnFirst(recordDO.getLearnFirst());
        detailVO.setAttachUrl(recordDO.getAttachUrl());
        detailVO.setCreateTime(recordDO.getCreateTime());
        detailVO.setHandlerEndTime(recordDO.getSubEndTime());
        detailVO.setTaskStoreId(recordDO.getTaskStoreId());
        detailVO.setParentQuestionId(recordDO.getParentQuestionId());
        // 任务说明，图片，视频
        detailVO.setUnifyTaskId(recordDO.getUnifyTaskId());
        detailVO.setTaskDesc(recordDO.getTaskDesc());
        if (Objects.nonNull(expandDO)) {
            detailVO.setTaskInfo(expandDO.getTaskInfo());
        }
        detailVO.setMetaColumnId(recordDO.getMetaTableId());
        if (metaTableDO != null) {
            detailVO.setMetaTableName(metaTableDO.getTableName());
        }
        // 检查项id不等于0，获取检查项数据
        detailVO.setMetaColumnId(recordDO.getMetaColumnId());
        if (!Constants.LONG_ZERO.equals(recordDO.getMetaColumnId()) && Objects.nonNull(metaStaTableColumnDO)) {
            detailVO.setMetaColumnName(metaStaTableColumnDO.getColumnName());
            detailVO.setSupportScore(metaStaTableColumnDO.getSupportScore());
            detailVO.setMetaColumnDescription(metaStaTableColumnDO.getDescription());
            detailVO.setColumnType(metaStaTableColumnDO.getColumnType());
            detailVO.setStandardPic(metaStaTableColumnDO.getStandardPic());
            // 开启高级检查表，显示红线项
            if (Objects.nonNull(metaTableDO) && Constants.INDEX_ONE.equals(metaTableDO.getTableProperty())) {
                detailVO.setMetaColumnLevel(metaStaTableColumnDO.getLevel());
            }
        }
        if (!Constants.LONG_ZERO.equals(recordDO.getDataColumnId()) && Objects.nonNull(dataStaTableColumnDO)) {
            BigDecimal checkScore = Objects.nonNull(dataStaTableColumnDO.getCheckScore()) ? dataStaTableColumnDO.getCheckScore().multiply(dataStaTableColumnDO.getScoreTimes()).setScale(Constants.SCALE, RoundingMode.HALF_UP) : new BigDecimal(Constants.ZERO_STR);
            detailVO.setCheckScore(checkScore);
            BigDecimal rewardPenaltMoney = Objects.nonNull(dataStaTableColumnDO.getCheckScore()) ? dataStaTableColumnDO.getRewardPenaltMoney().multiply(dataStaTableColumnDO.getAwardTimes()).setScale(Constants.SCALE, RoundingMode.HALF_UP) : new BigDecimal(Constants.ZERO_STR);
            detailVO.setRewardPenaltMoney(rewardPenaltMoney);
            detailVO.setDataColumnId(recordDO.getDataColumnId());
            detailVO.setCheckResult(dataStaTableColumnDO.getCheckResult());
            detailVO.setCheckResultName(dataStaTableColumnDO.getCheckResultName());
            detailVO.setCheckResultReason(dataStaTableColumnDO.getCheckResultReason());
            detailVO.setIsAiCheck(metaStaTableColumnDO.getIsAiCheck());
        }
        // 门店信息
        if (Objects.nonNull(storeDO)) {
            detailVO.setStoreId(storeDO.getStoreId());
            detailVO.setStoreName(storeDO.getStoreName());
            detailVO.setStoreNum(storeDO.getStoreNum());
        }

        // 区域信息
        if (Objects.nonNull(regionDO)) {
            RegionPathNameVO regionPathNameVO = regionService.getAllRegionName(enterpriseId, regionDO.getId());

            detailVO.setRegionId(regionDO.getId());
            detailVO.setRegionPath(regionDO.getRegionPath());
            detailVO.setRegionName(regionPathNameVO.getAllRegionName());
        }
        // 逾期
        detailVO.setIsOverdue(this.checkOverdue(recordDO));
        // 人员信息
        detailVO.setHandleUserId(recordDO.getHandleUserId());
        PersonDTO handleUser = userDOMap.get(recordDO.getHandleUserId());
        if (Objects.nonNull(handleUser)) {
            detailVO.setHandleUserName(handleUser.getUserName());
        }
        detailVO.setApproveUserId(recordDO.getApproveUserId());
        PersonDTO approveUser = userDOMap.get(recordDO.getApproveUserId());
        if (Objects.nonNull(approveUser)) {
            detailVO.setApproveUserName(approveUser.getUserName());
        }
        detailVO.setCreateUserId(recordDO.getCreateUserId());
        PersonDTO createUser = userDOMap.get(recordDO.getCreateUserId());
        if (Objects.nonNull(createUser)) {
            detailVO.setCreateUserName(createUser.getUserName());
        } else if (Constants.AI.equals(recordDO.getCreateUserId())) {
            detailVO.setCreateUserName(Constants.AI);
        }
        detailVO.setCcUsers(ccUserIds.stream().map(userDOMap::get).collect(Collectors.toList()));
        detailVO.setHandleUsers(handleUserIds.stream().map(userDOMap::get).collect(Collectors.toList()));
        detailVO.setApproveUsers(approveUserIds.stream().map(userDOMap::get).filter(Objects::nonNull).collect(Collectors.toList()));
        detailVO.setSecondApproveUsers(secondApproveUserIds.stream().map(userDOMap::get).collect(Collectors.toList()));
        detailVO.setThirdApproveUsers(thirdApproveUserIds.stream().map(userDOMap::get).collect(Collectors.toList()));
        if (StringUtils.isNotBlank(recordDO.getSecondApproveUserId())) {
            detailVO.setSecondApproveUserId(recordDO.getSecondApproveUserId());
            PersonDTO secondPeople = userDOMap.get(recordDO.getSecondApproveUserId());
            if (Objects.nonNull(secondPeople)) {
                detailVO.setSecondApproveUserName(secondPeople.getUserName());
            }
        }
        if (StringUtils.isNotBlank(recordDO.getThirdApproveUserId())) {
            detailVO.setThirdApproveUserId(recordDO.getThirdApproveUserId());
            PersonDTO thirdPeople = userDOMap.get(recordDO.getThirdApproveUserId());
            if (Objects.nonNull(thirdPeople)) {
                detailVO.setThirdApproveUserName(thirdPeople.getUserName());
            }
        }
        // 根据节点判断是否是处理人
        if (UnifyNodeEnum.FIRST_NODE.getCode().equals(nodeNo)) {
            detailVO.setIsHandleUser(handleUserIds.contains(userId));
        } else if (UnifyNodeEnum.SECOND_NODE.getCode().equals(nodeNo)) {
            detailVO.setIsHandleUser(approveUserIds.contains(userId));
        } else if (UnifyNodeEnum.THIRD_NODE.getCode().equals(nodeNo)) {
            detailVO.setIsHandleUser(secondApproveUserIds.contains(userId));
        } else if (UnifyNodeEnum.FOUR_NODE.getCode().equals(nodeNo)) {
            detailVO.setIsHandleUser(thirdApproveUserIds.contains(userId));
        } else {
            detailVO.setIsHandleUser(Boolean.FALSE);
        }
        detailVO.setQuestionType(recordDO.getQuestionType());
        return detailVO;
    }


    private TbQuestionSubRecordListExportVO parseDOToSubQuestionExportVO(String enterpriseId, TbQuestionRecordDO recordDO, Map<Long, TbMetaStaTableColumnDO> metaStaTableColumnDOMap,
                                                                         Map<Long, TbDataStaTableColumnDO> dataStaTableColumnDOMap, Map<Long, TbMetaTableDO> metaTableDOMap, Map<String, StoreDO> storeDOMap,
                                                                         Map<String, EnterpriseUserDO> userDOMap, Map<Long, List<String>> ccUserNameMap,
                                                                         Map<Long, TbQuestionRecordExpandDO> expandDOMap, Map<String, TbQuestionHistoryVO> questionHistoryMap,
                                                                         Map<Long, List<String>> handleUserNameMap, Map<Long, List<String>> approveUserNameMap,
                                                                         Map<Long, List<String>> secondApproveUserNameMap, Map<Long, List<String>> thirdApproveUserNameMap) {
        TbQuestionSubRecordListExportVO recordExportVO = new TbQuestionSubRecordListExportVO();
        recordExportVO.setTaskName(recordDO.getTaskName());
        recordExportVO.setId(recordDO.getParentQuestionId());
        recordExportVO.setRecordId(recordDO.getId());
        recordExportVO.setQuestionType(recordDO.getQuestionType());
        recordExportVO.setQuestionName(recordDO.getParentQuestionName());
        // 检查项id不等于0，获取检查项数据
        TbMetaStaTableColumnDO metaStaTableColumnDO = metaStaTableColumnDOMap.get(recordDO.getMetaColumnId());
        if (Objects.nonNull(metaStaTableColumnDO)) {
            recordExportVO.setMetaColumnName(metaStaTableColumnDO.getColumnName());
            // 检查表
            TbMetaTableDO tableDO = metaTableDOMap.get(recordDO.getMetaTableId());
            if (Objects.nonNull(tableDO)) {
                recordExportVO.setMetaTableName(tableDO.getTableName());
            }
        }
        TbDataStaTableColumnDO dataStaTableColumnDO = dataStaTableColumnDOMap.get(recordDO.getDataColumnId());
        if (Objects.nonNull(dataStaTableColumnDO)) {
            recordExportVO.setCheckResultName(dataStaTableColumnDO.getCheckResultName());
            BigDecimal checkScore = Objects.nonNull(dataStaTableColumnDO.getCheckScore()) ? dataStaTableColumnDO.getCheckScore().multiply(dataStaTableColumnDO.getScoreTimes()).setScale(Constants.SCALE, RoundingMode.HALF_UP) : new BigDecimal(Constants.ZERO);
            recordExportVO.setCheckScore(checkScore);
            if (!Objects.isNull(dataStaTableColumnDO.getRewardPenaltMoney())) {
                recordExportVO.setRewardPenaltMoney(Constants.RMB + dataStaTableColumnDO.getRewardPenaltMoney().multiply(dataStaTableColumnDO.getAwardTimes()).setScale(Constants.SCALE, RoundingMode.HALF_UP));
            }
        }
        // 门店信息
        StoreDO storeDO = storeDOMap.get(recordDO.getStoreId());
        if (Objects.nonNull(storeDO)) {
            recordExportVO.setStoreName(storeDO.getStoreName());
            recordExportVO.setStoreNum(storeDO.getStoreNum());
        }
        RegionPathNameVO regionPathNameVO = regionService.getAllRegionName(enterpriseId, recordDO.getRegionId());
        // 区域信息
        recordExportVO.setFullRegionName(regionPathNameVO.getAllRegionName());
        // 任务说明任务,图片，视频
        recordExportVO.setTaskDesc(recordDO.getTaskDesc());
        TbQuestionRecordExpandDO expandDO = expandDOMap.get(recordDO.getId());
        if (expandDO != null) {
            QuestionTaskInfoDTO taskInfo = JSONObject.parseObject(expandDO.getTaskInfo(), QuestionTaskInfoDTO.class);
            StringBuilder photosAndVideos = new StringBuilder();
            if (CollectionUtils.isNotEmpty(taskInfo.getPhotos())) {
                photosAndVideos.append(String.join(Constants.BR, taskInfo.getPhotos()));
            }
            String videos = taskInfo.getVideos();
            if (StringUtils.isNotBlank(videos)) {
                SmallVideoInfoDTO videoInfoDTO = JSONObject.parseObject(videos, SmallVideoInfoDTO.class);
                List<String> videoList = CollectionUtils.emptyIfNull(videoInfoDTO.getVideoList())
                        .stream().map(SmallVideoDTO::getVideoUrl).collect(Collectors.toList());
                photosAndVideos.append(Constants.BR).append(String.join(Constants.BR, videoList));
            }
            if (CollectionUtils.isNotEmpty(taskInfo.getSoundRecordingList())) {
                List<String> soundRecordingList = taskInfo.getSoundRecordingList();
                if (CollectionUtils.isNotEmpty(soundRecordingList)) {
                    photosAndVideos.append(Constants.BR).append(StringUtils.join(soundRecordingList, Constants.BR));
                }
            }
            recordExportVO.setPhotosAndVideos(photosAndVideos.toString());
        }

        // 判断逾期
        recordExportVO.setIsOverdue(this.checkOverdue(recordDO) ? OVERDUE : NOT_OVERDUE);
        recordExportVO.setStatus(UnifyNodeEnum.getByCode(recordDO.getStatus()).getDesc());
        // 用户
        EnterpriseUserDO createUser = userDOMap.get(recordDO.getCreateUserId());
        if (Objects.nonNull(createUser)) {
            recordExportVO.setCreateUserName(createUser.getName());
        }
        EnterpriseUserDO handleUser = userDOMap.get(recordDO.getHandleUserId());
        if (Objects.nonNull(handleUser)) {
            recordExportVO.setHandleUserName(handleUser.getName());
        }
        EnterpriseUserDO approveUser = userDOMap.get(recordDO.getApproveUserId());
        if (Objects.nonNull(approveUser)) {
            recordExportVO.setApproveUserName(approveUser.getName());
        }
        recordExportVO.setCreateTime(recordDO.getCreateTime());

        // 抄送人
        List<String> ccUsers = ccUserNameMap.get(recordDO.getTaskStoreId());
        if (CollectionUtils.isNotEmpty(ccUsers)) {
            recordExportVO.setCcUserNames(String.join(Constants.COMMA, ccUsers));
        }
        // 指派整改人
        List<String> handleUsers = handleUserNameMap.get(recordDO.getTaskStoreId());
        if (CollectionUtils.isNotEmpty(handleUsers)) {
            recordExportVO.setAssignHandleUserName(String.join(Constants.COMMA, handleUsers));
        }
        // 指派一级审批人
        List<String> approveUsers = approveUserNameMap.get(recordDO.getTaskStoreId());
        if (CollectionUtils.isNotEmpty(approveUsers)) {
            recordExportVO.setAssignApproveUserName(String.join(Constants.COMMA, approveUsers));
        }
        // 指派二级审批人
        List<String> secondApproveUsers = secondApproveUserNameMap.get(recordDO.getTaskStoreId());
        if (CollectionUtils.isNotEmpty(secondApproveUsers)) {
            recordExportVO.setAssignSecondApproveUserName(String.join(Constants.COMMA, secondApproveUsers));
        }
        // 指派三级审批人
        List<String> thirdApproveUsers = thirdApproveUserNameMap.get(recordDO.getTaskStoreId());
        if (CollectionUtils.isNotEmpty(thirdApproveUsers)) {
            recordExportVO.setAssignThirdApproveUserName(String.join(Constants.COMMA, thirdApproveUsers));
        }
        recordExportVO.setCompleteTime(recordDO.getCompleteTime());
        recordExportVO.setHandlerEndTime(recordDO.getSubEndTime());
        // 完成时间不为空，计算总用时
        if (!Objects.isNull(recordDO.getCompleteTime())) {
            recordExportVO.setTotalDurationTime(DateUtil.betweenMs(recordDO.getCreateTime(), recordDO.getCompleteTime()));
            recordExportVO.setTotalDuration(DateUtils.formatBetween(recordExportVO.getTotalDurationTime()));
        }
        recordExportVO.setHandleRemark(recordDO.getHandleRemark());
        recordExportVO.setHandleTime(recordDO.getHandleTime());
        recordExportVO.setApproveRemark(recordDO.getApproveRemark());
        recordExportVO.setApproveTime(recordDO.getApproveTime());
        recordExportVO.setHandleActionKey(QuestionActionKeyEnum.getDescByCode(recordDO.getHandleActionKey()));
        if (!Objects.isNull(expandDO)) {
            if (StringUtils.isNotBlank(expandDO.getHandlePhoto())) {
                recordExportVO.setHandlePhoto(JSONArray.parseArray(expandDO.getHandlePhoto(), String.class).stream().collect(Collectors.joining(Constants.BR)));
            }
            if (StringUtils.isNotBlank(expandDO.getHandleVideo())) {
                SmallVideoInfoDTO handleVideoInfo = JSONObject.parseObject(expandDO.getHandleVideo(), SmallVideoInfoDTO.class);
                String handleVideo = CollectionUtils.emptyIfNull(handleVideoInfo.getVideoList())
                        .stream().map(SmallVideoDTO::getVideoUrl).filter(Objects::nonNull).collect(Collectors.joining(Constants.BR));
                recordExportVO.setHandlePhoto(recordExportVO.getHandlePhoto() + Constants.BR + handleVideo);
                String sounds = null;
                if (CollectionUtils.isNotEmpty(handleVideoInfo.getSoundRecordingList())) {
                    sounds = StringUtils.join(handleVideoInfo.getSoundRecordingList(), Constants.BR);
                }
                if (StringUtils.isNotBlank(sounds)) {
                    recordExportVO.setHandlePhoto(recordExportVO.getHandlePhoto() + Constants.BR + sounds);
                }
            }
            if (StringUtils.isNotBlank(expandDO.getApprovePhoto())) {
                recordExportVO.setApprovePhoto(JSONArray.parseArray(expandDO.getApprovePhoto(), String.class).stream().collect(Collectors.joining(Constants.BR)));
            }
            if (StringUtils.isNotBlank(expandDO.getApproveVideo())) {
                SmallVideoInfoDTO approveVideoInfo = JSONObject.parseObject(expandDO.getApproveVideo(), SmallVideoInfoDTO.class);
                String approveVideo = CollectionUtils.emptyIfNull(approveVideoInfo.getVideoList())
                        .stream().map(SmallVideoDTO::getVideoUrl).filter(Objects::nonNull).collect(Collectors.joining(Constants.BR));

                recordExportVO.setApprovePhoto(recordExportVO.getApprovePhoto() + Constants.BR + approveVideo);
                String approveSounds = null;
                if (CollectionUtils.isNotEmpty(approveVideoInfo.getSoundRecordingList())) {
                    approveSounds = StringUtils.join(approveVideoInfo.getSoundRecordingList(), Constants.BR);
                }
                if (StringUtils.isNotBlank(approveSounds)) {
                    recordExportVO.setApprovePhoto(recordExportVO.getApprovePhoto() + Constants.BR + approveSounds);
                }
            }
        }
        if (StringUtils.isNotBlank(recordDO.getSecondApproveUserId())) {
            EnterpriseUserDO secondPeople = userDOMap.get(recordDO.getSecondApproveUserId());
            if (Objects.nonNull(secondPeople)) {
                recordExportVO.setSecondApproveUserName(secondPeople.getName());
            }
        }
        if (StringUtils.isNotBlank(recordDO.getThirdApproveUserId())) {
            EnterpriseUserDO thirdPeople = userDOMap.get(recordDO.getThirdApproveUserId());
            if (Objects.nonNull(thirdPeople)) {
                recordExportVO.setThirdApproveUserName(thirdPeople.getName());
            }
        }
        if (UnifyNodeEnum.END_NODE.getCode().equals(recordDO.getStatus())) {
            TbQuestionHistoryVO secondQuestionHistoryVO = questionHistoryMap.get(recordDO.getId() + Constants.MOSAICS + UnifyNodeEnum.THIRD_NODE.getCode());
            if (secondQuestionHistoryVO != null) {
                recordExportVO.setSecondApproveUserName(secondQuestionHistoryVO.getOperateUserName());
                recordExportVO.setSecondApproveRemark(secondQuestionHistoryVO.getRemark());
                recordExportVO.setSecondApproveTime(secondQuestionHistoryVO.getCreateTime());
                if (StringUtils.isNotBlank(secondQuestionHistoryVO.getPhoto())) {
                    recordExportVO.setSecondApprovePhoto(String.join(Constants.BR, JSONArray.parseArray(secondQuestionHistoryVO.getPhoto(), String.class)));
                }
                if (StringUtils.isNotBlank(secondQuestionHistoryVO.getVideo())) {
                    SmallVideoInfoDTO videoInfo = JSONObject.parseObject(secondQuestionHistoryVO.getVideo(), SmallVideoInfoDTO.class);
                    String video = CollectionUtils.emptyIfNull(videoInfo.getVideoList())
                            .stream().map(SmallVideoDTO::getVideoUrl).filter(Objects::nonNull).collect(Collectors.joining(Constants.BR));
                    recordExportVO.setSecondApprovePhoto(recordExportVO.getSecondApprovePhoto() + Constants.BR + video);
                    String sounds = null;
                    if (CollectionUtils.isNotEmpty(videoInfo.getSoundRecordingList())) {
                        sounds = StringUtils.join(videoInfo.getSoundRecordingList(), Constants.BR);
                    }
                    if (StringUtils.isNotBlank(sounds)) {
                        recordExportVO.setSecondApprovePhoto(recordExportVO.getSecondApprovePhoto() + Constants.BR + sounds);
                    }
                }
            }
            TbQuestionHistoryVO thirdQuestionHistoryVO = questionHistoryMap.get(recordDO.getId() + Constants.MOSAICS + UnifyNodeEnum.FOUR_NODE.getCode());
            if (thirdQuestionHistoryVO != null) {
                recordExportVO.setThirdApproveUserName(thirdQuestionHistoryVO.getOperateUserName());
                recordExportVO.setThirdApproveRemark(thirdQuestionHistoryVO.getRemark());
                recordExportVO.setThirdApproveTime(thirdQuestionHistoryVO.getCreateTime());
                if (StringUtils.isNotBlank(thirdQuestionHistoryVO.getPhoto())) {
                    recordExportVO.setThirdApprovePhoto(String.join(Constants.BR, JSONArray.parseArray(thirdQuestionHistoryVO.getPhoto(), String.class)));
                }
                if (StringUtils.isNotBlank(thirdQuestionHistoryVO.getVideo())) {
                    SmallVideoInfoDTO videoInfo = JSONObject.parseObject(thirdQuestionHistoryVO.getVideo(), SmallVideoInfoDTO.class);
                    String video = CollectionUtils.emptyIfNull(videoInfo.getVideoList())
                            .stream().map(SmallVideoDTO::getVideoUrl).filter(Objects::nonNull).collect(Collectors.joining(Constants.BR));

                    recordExportVO.setThirdApprovePhoto(recordExportVO.getThirdApprovePhoto() + Constants.BR + video);
                    String sounds = null;
                    if (CollectionUtils.isNotEmpty(videoInfo.getSoundRecordingList())) {
                        sounds = StringUtils.join(videoInfo.getSoundRecordingList(), Constants.BR);
                    }
                    if (StringUtils.isNotBlank(sounds)) {
                        recordExportVO.setThirdApprovePhoto(recordExportVO.getThirdApprovePhoto() + Constants.BR + sounds);
                    }
                }
            }
        }
        return recordExportVO;
    }


    private TbQuestionRecordExportVO parseDOToQuestionExportVO(String enterpriseId, TbQuestionRecordDO recordDO, Map<Long, TbMetaStaTableColumnDO> metaStaTableColumnDOMap,
                                                               Map<Long, TbDataStaTableColumnDO> dataStaTableColumnDOMap, Map<Long, TbMetaTableDO> metaTableDOMap, Map<String, StoreDO> storeDOMap,
                                                               Map<Long, TaskParentDO> taskParentDOMap, Map<String, EnterpriseUserDO> userDOMap, Map<Long, List<String>> ccUserNameMap,
                                                               Map<Long, TbQuestionRecordExpandDO> expandDOMap, Map<String, TbQuestionHistoryVO> questionHistoryMap,
                                                               Map<Long, List<String>> handleUserNameMap, Map<Long, List<String>> approveUserNameMap,
                                                               Map<Long, List<String>> secondApproveUserNameMap, Map<Long, List<String>> thirdApproveUserNameMap) {
        TbQuestionRecordExportVO recordExportVO = new TbQuestionRecordExportVO();
        recordExportVO.setId(recordDO.getId());
        recordExportVO.setTaskType(UnifyTaskConstant.TASK_TYPE_QUESTION_RECORD);
        recordExportVO.setCreateType(QuestionCreateTypeEnum.getMsgByCode(recordDO.getCreateType()));
        recordExportVO.setTaskName(recordDO.getTaskName());
        recordExportVO.setParentQuestionId(recordDO.getParentQuestionId());
        // 检查项id不等于0，获取检查项数据
        TbMetaStaTableColumnDO metaStaTableColumnDO = metaStaTableColumnDOMap.get(recordDO.getMetaColumnId());
        if (Objects.nonNull(metaStaTableColumnDO)) {
            recordExportVO.setMetaColumnName(metaStaTableColumnDO.getColumnName());
            if (StringUtils.isNotBlank(metaStaTableColumnDO.getLevel())) {
                recordExportVO.setLevel(TbMetaStaTableColumnLevelEnum.getDescByValue(metaStaTableColumnDO.getLevel()));
            }
            recordExportVO.setSupportScore(metaStaTableColumnDO.getSupportScore());
            // 检查表
            TbMetaTableDO tableDO = metaTableDOMap.get(recordDO.getMetaTableId());
            if (Objects.nonNull(tableDO)) {
                recordExportVO.setMetaTableName(tableDO.getTableName());
            }
        }
        TbDataStaTableColumnDO dataStaTableColumnDO = dataStaTableColumnDOMap.get(recordDO.getDataColumnId());
        if (Objects.nonNull(dataStaTableColumnDO)) {
            recordExportVO.setCheckResultName(dataStaTableColumnDO.getCheckResultName());
            BigDecimal checkScore = Objects.nonNull(dataStaTableColumnDO.getCheckScore()) ? dataStaTableColumnDO.getCheckScore().multiply(dataStaTableColumnDO.getScoreTimes()).setScale(Constants.SCALE, RoundingMode.HALF_UP) : new BigDecimal(Constants.ZERO);
            recordExportVO.setCheckScore(checkScore);
            if (!Objects.isNull(dataStaTableColumnDO.getRewardPenaltMoney())) {
                recordExportVO.setRewardPenaltMoney(Constants.RMB + dataStaTableColumnDO.getRewardPenaltMoney().multiply(dataStaTableColumnDO.getAwardTimes()).setScale(Constants.SCALE, RoundingMode.HALF_UP));
            }
        }
        // 门店信息
        StoreDO storeDO = storeDOMap.get(recordDO.getStoreId());
        if (Objects.nonNull(storeDO)) {
            recordExportVO.setStoreName(storeDO.getStoreName());
            recordExportVO.setStoreNum(storeDO.getStoreNum());
        }
        RegionPathNameVO regionPathNameVO = regionService.getAllRegionName(enterpriseId, recordDO.getRegionId());
        // 区域信息
        recordExportVO.setFullRegionName(regionPathNameVO.getAllRegionName());
        ExportUtil.setRegionEntityExport(recordExportVO, regionPathNameVO.getRegionNameList());
        // 任务说明任务,图片，视频
        recordExportVO.setTaskDesc(recordDO.getTaskDesc());
        TbQuestionRecordExpandDO expandDO = expandDOMap.get(recordDO.getId());
        if (expandDO != null) {
            QuestionTaskInfoDTO taskInfo = JSONObject.parseObject(expandDO.getTaskInfo(), QuestionTaskInfoDTO.class);
            StringBuilder photosAndVideos = new StringBuilder();
            if (CollectionUtils.isNotEmpty(taskInfo.getPhotos())) {
                photosAndVideos.append(String.join(Constants.BR, taskInfo.getPhotos()));
            }
            String videos = taskInfo.getVideos();
            if (StringUtils.isNotBlank(videos)) {
                SmallVideoInfoDTO videoInfoDTO = JSONObject.parseObject(videos, SmallVideoInfoDTO.class);
                List<String> videoList = CollectionUtils.emptyIfNull(videoInfoDTO.getVideoList())
                        .stream().map(SmallVideoDTO::getVideoUrl).collect(Collectors.toList());
                photosAndVideos.append(Constants.BR).append(String.join(Constants.BR, videoList));
            }
            if (CollectionUtils.isNotEmpty(taskInfo.getSoundRecordingList())) {
                List<String> soundRecordingList = taskInfo.getSoundRecordingList();
                if (CollectionUtils.isNotEmpty(soundRecordingList)) {
                    photosAndVideos.append(Constants.BR).append(StringUtils.join(soundRecordingList, Constants.BR));
                }
            }
            recordExportVO.setPhotosAndVideos(photosAndVideos.toString());
        }

        // 判断逾期
        recordExportVO.setIsOverdue(this.checkOverdue(recordDO) ? OVERDUE : NOT_OVERDUE);
        recordExportVO.setStatus(UnifyNodeEnum.getByCode(recordDO.getStatus()).getDesc());
        // 用户
        EnterpriseUserDO createUser = userDOMap.get(recordDO.getCreateUserId());
        if (Objects.nonNull(createUser)) {
            recordExportVO.setCreateUserName(createUser.getName());
        }
        EnterpriseUserDO handleUser = userDOMap.get(recordDO.getHandleUserId());
        if (Objects.nonNull(handleUser)) {
            recordExportVO.setHandleUserName(handleUser.getName());
        }
        EnterpriseUserDO approveUser = userDOMap.get(recordDO.getApproveUserId());
        if (Objects.nonNull(approveUser)) {
            recordExportVO.setApproveUserName(approveUser.getName());
        }
        recordExportVO.setCreateDate(recordDO.getCreateTime());
        recordExportVO.setCreateTime(recordDO.getCreateTime());

        recordExportVO.setHandleActionKey(QuestionActionKeyEnum.getDescByCode(recordDO.getHandleActionKey()));
        recordExportVO.setApproveActionKey(QuestionActionKeyEnum.getDescByCode(recordDO.getApproveActionKey()));
        recordExportVO.setSecondApproveActionKey(QuestionActionKeyEnum.getDescByCode(recordDO.getSecondApproveActionKey()));
        recordExportVO.setThirdApproveActionKey(QuestionActionKeyEnum.getDescByCode(recordDO.getThirdApproveActionKey()));

        // 抄送人
        List<String> ccUsers = ccUserNameMap.get(recordDO.getTaskStoreId());
        if (CollectionUtils.isNotEmpty(ccUsers)) {
            recordExportVO.setCcUserNames(String.join(Constants.COMMA, ccUsers));
        }
        // 指派整改人
        List<String> handleUsers = handleUserNameMap.get(recordDO.getTaskStoreId());
        if (CollectionUtils.isNotEmpty(handleUsers)) {
            recordExportVO.setAssignHandleUserName(String.join(Constants.COMMA, handleUsers));
        }
        // 指派一级审批人
        List<String> approveUsers = approveUserNameMap.get(recordDO.getTaskStoreId());
        if (CollectionUtils.isNotEmpty(approveUsers)) {
            recordExportVO.setAssignApproveUserName(String.join(Constants.COMMA, approveUsers));
        }
        // 指派二级审批人
        List<String> secondApproveUsers = secondApproveUserNameMap.get(recordDO.getTaskStoreId());
        if (CollectionUtils.isNotEmpty(secondApproveUsers)) {
            recordExportVO.setAssignSecondApproveUserName(String.join(Constants.COMMA, secondApproveUsers));
        }
        // 指派三级审批人
        List<String> thirdApproveUsers = thirdApproveUserNameMap.get(recordDO.getTaskStoreId());
        if (CollectionUtils.isNotEmpty(thirdApproveUsers)) {
            recordExportVO.setAssignThirdApproveUserName(String.join(Constants.COMMA, thirdApproveUsers));
        }
        recordExportVO.setCompleteTime(recordDO.getCompleteTime());
        recordExportVO.setHandlerEndDate(recordDO.getSubEndTime());
        recordExportVO.setHandlerEndTime(recordDO.getSubEndTime());
        // 完成时间不为空，计算总用时
        if (!Objects.isNull(recordDO.getCompleteTime())) {
            recordExportVO.setTotalDurationTime(DateUtil.betweenMs(recordDO.getCreateTime(), recordDO.getCompleteTime()));
            recordExportVO.setTotalDuration(DateUtils.formatBetween(recordExportVO.getTotalDurationTime()));
        }
        recordExportVO.setHandleRemark(recordDO.getHandleRemark());
        recordExportVO.setHandleTime(recordDO.getHandleTime());
        recordExportVO.setApproveRemark(recordDO.getApproveRemark());
        recordExportVO.setApproveTime(recordDO.getApproveTime());

        if (!Objects.isNull(expandDO)) {
            if (StringUtils.isNotBlank(expandDO.getHandlePhoto())) {
                recordExportVO.setHandlePhoto(JSONArray.parseArray(expandDO.getHandlePhoto(), String.class).stream().collect(Collectors.joining(Constants.BR)));
            }
            if (StringUtils.isNotBlank(expandDO.getHandleVideo())) {
                SmallVideoInfoDTO handleVideoInfo = JSONObject.parseObject(expandDO.getHandleVideo(), SmallVideoInfoDTO.class);
                String handleVideo = CollectionUtils.emptyIfNull(handleVideoInfo.getVideoList())
                        .stream().map(SmallVideoDTO::getVideoUrl).filter(Objects::nonNull).collect(Collectors.joining(Constants.BR));
                recordExportVO.setHandlePhoto(recordExportVO.getHandlePhoto() + Constants.BR + handleVideo);
                String sounds = null;
                if (CollectionUtils.isNotEmpty(handleVideoInfo.getSoundRecordingList())) {
                    sounds = StringUtils.join(handleVideoInfo.getSoundRecordingList(), Constants.BR);
                }
                if (StringUtils.isNotBlank(sounds)) {
                    recordExportVO.setHandlePhoto(recordExportVO.getHandlePhoto() + Constants.BR + sounds);
                }
            }
            if (StringUtils.isNotBlank(expandDO.getApprovePhoto())) {
                recordExportVO.setApprovePhoto(JSONArray.parseArray(expandDO.getApprovePhoto(), String.class).stream().collect(Collectors.joining(Constants.BR)));
            }
            if (StringUtils.isNotBlank(expandDO.getApproveVideo())) {
                SmallVideoInfoDTO approveVideoInfo = JSONObject.parseObject(expandDO.getApproveVideo(), SmallVideoInfoDTO.class);
                String approveVideo = CollectionUtils.emptyIfNull(approveVideoInfo.getVideoList())
                        .stream().map(SmallVideoDTO::getVideoUrl).filter(Objects::nonNull).collect(Collectors.joining(Constants.BR));

                recordExportVO.setApprovePhoto(recordExportVO.getApprovePhoto() + Constants.BR + approveVideo);
                String approveSounds = null;
                if (CollectionUtils.isNotEmpty(approveVideoInfo.getSoundRecordingList())) {
                    approveSounds = StringUtils.join(approveVideoInfo.getSoundRecordingList(), Constants.BR);
                }
                if (StringUtils.isNotBlank(approveSounds)) {
                    recordExportVO.setApprovePhoto(recordExportVO.getApprovePhoto() + Constants.BR + approveSounds);
                }
            }
        }
        if (StringUtils.isNotBlank(recordDO.getSecondApproveUserId())) {
            EnterpriseUserDO secondPeople = userDOMap.get(recordDO.getSecondApproveUserId());
            if (Objects.nonNull(secondPeople)) {
                recordExportVO.setSecondApproveUserName(secondPeople.getName());
            }
        }
        if (StringUtils.isNotBlank(recordDO.getThirdApproveUserId())) {
            EnterpriseUserDO thirdPeople = userDOMap.get(recordDO.getThirdApproveUserId());
            if (Objects.nonNull(thirdPeople)) {
                recordExportVO.setThirdApproveUserName(thirdPeople.getName());
            }
        }
        if (UnifyNodeEnum.END_NODE.getCode().equals(recordDO.getStatus())) {
            TbQuestionHistoryVO secondQuestionHistoryVO = questionHistoryMap.get(recordDO.getId() + Constants.MOSAICS + UnifyNodeEnum.THIRD_NODE.getCode());
            if (secondQuestionHistoryVO != null) {
                recordExportVO.setSecondApproveActionKey(QuestionActionKeyEnum.getDescByCode(secondQuestionHistoryVO.getActionKey()));
                recordExportVO.setSecondApproveUserName(secondQuestionHistoryVO.getOperateUserName());
                recordExportVO.setSecondApproveRemark(secondQuestionHistoryVO.getRemark());
                recordExportVO.setSecondApproveTime(secondQuestionHistoryVO.getCreateTime());
                if (StringUtils.isNotBlank(secondQuestionHistoryVO.getPhoto())) {
                    recordExportVO.setSecondApprovePhoto(String.join(Constants.BR, JSONArray.parseArray(secondQuestionHistoryVO.getPhoto(), String.class)));
                }
                if (StringUtils.isNotBlank(secondQuestionHistoryVO.getVideo())) {
                    SmallVideoInfoDTO videoInfo = JSONObject.parseObject(secondQuestionHistoryVO.getVideo(), SmallVideoInfoDTO.class);
                    String video = CollectionUtils.emptyIfNull(videoInfo.getVideoList())
                            .stream().map(SmallVideoDTO::getVideoUrl).filter(Objects::nonNull).collect(Collectors.joining(Constants.BR));
                    recordExportVO.setSecondApprovePhoto(recordExportVO.getSecondApprovePhoto() + Constants.BR + video);
                    String sounds = null;
                    if (CollectionUtils.isNotEmpty(videoInfo.getSoundRecordingList())) {
                        sounds = StringUtils.join(videoInfo.getSoundRecordingList(), Constants.BR);
                    }
                    if (StringUtils.isNotBlank(sounds)) {
                        recordExportVO.setSecondApprovePhoto(recordExportVO.getSecondApprovePhoto() + Constants.BR + sounds);
                    }
                }
            }
            TbQuestionHistoryVO thirdQuestionHistoryVO = questionHistoryMap.get(recordDO.getId() + Constants.MOSAICS + UnifyNodeEnum.FOUR_NODE.getCode());
            if (thirdQuestionHistoryVO != null) {
                recordExportVO.setThirdApproveActionKey(QuestionActionKeyEnum.getDescByCode(thirdQuestionHistoryVO.getActionKey()));
                recordExportVO.setThirdApproveUserName(thirdQuestionHistoryVO.getOperateUserName());
                recordExportVO.setThirdApproveRemark(thirdQuestionHistoryVO.getRemark());
                recordExportVO.setThirdApproveTime(thirdQuestionHistoryVO.getCreateTime());
                if (StringUtils.isNotBlank(thirdQuestionHistoryVO.getPhoto())) {
                    recordExportVO.setThirdApprovePhoto(String.join(Constants.BR, JSONArray.parseArray(thirdQuestionHistoryVO.getPhoto(), String.class)));
                }
                if (StringUtils.isNotBlank(thirdQuestionHistoryVO.getVideo())) {
                    SmallVideoInfoDTO videoInfo = JSONObject.parseObject(thirdQuestionHistoryVO.getVideo(), SmallVideoInfoDTO.class);
                    String video = CollectionUtils.emptyIfNull(videoInfo.getVideoList())
                            .stream().map(SmallVideoDTO::getVideoUrl).filter(Objects::nonNull).collect(Collectors.joining(Constants.BR));

                    recordExportVO.setThirdApprovePhoto(recordExportVO.getThirdApprovePhoto() + Constants.BR + video);
                    String sounds = null;
                    if (CollectionUtils.isNotEmpty(videoInfo.getSoundRecordingList())) {
                        sounds = StringUtils.join(videoInfo.getSoundRecordingList(), Constants.BR);
                    }
                    if (StringUtils.isNotBlank(sounds)) {
                        recordExportVO.setThirdApprovePhoto(recordExportVO.getThirdApprovePhoto() + Constants.BR + sounds);
                    }
                }
            }
        }
        return recordExportVO;
    }

    /**
     * 问题工单查询request转DTO
     *
     * @param enterpriseId  企业id
     * @param searchRequest TbQuestionRecordSearchRequest
     * @return TbQuestionRecordSearchDTO
     */
    private TbQuestionRecordSearchDTO parseSearchRequestToDTO(String enterpriseId, TbQuestionRecordSearchRequest searchRequest) {
        TbQuestionRecordSearchDTO searchDTO = new TbQuestionRecordSearchDTO();
        searchDTO.setEnterpriseId(enterpriseId);
        // 根据区域id查询完整的区域路径
        if (StringUtils.isBlank(searchRequest.getStoreId()) && StringUtils.isNotBlank(searchRequest.getRegionId())) {
            searchDTO.setFullRegionPath(regionService.getRegionPath(enterpriseId, searchRequest.getRegionId()));
        }
        boolean isAdmin = sysRoleService.checkIsAdmin(enterpriseId, searchRequest.getCurrentUserId());
        //兼容 ，如果入参为空数组，查出用户所有区域
        if (CollectionUtils.isEmpty(searchRequest.getRegionIds()) && CollectionUtils.isEmpty(searchRequest.getStoreIds())) {
            if(!isAdmin){
                List<SelectComponentRegionVO> allRegionList = selectionComponentService.getRegionAndStore(enterpriseId, null, UserHolder.getUser().getUserId(), null).getAllRegionList();
                if (CollectionUtils.isNotEmpty(allRegionList)){
                    List<String> ids = allRegionList.stream().map(SelectComponentRegionVO::getId).collect(Collectors.toList());
                    searchRequest.setRegionIds(ids);
                }
            }
        }
        //兼容
        List<String> fullRegionPathList = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(searchRequest.getRegionIds())) {
            List<String> regionIds = searchRequest.getRegionIds();
            fullRegionPathList = regionDao.getRegionByRegionIds(enterpriseId, regionIds).stream().map(RegionDO::getFullRegionPath).collect(Collectors.toList());
        }
        if(!isAdmin && CollectionUtils.isEmpty(searchRequest.getRegionIds()) && CollectionUtils.isEmpty(searchRequest.getStoreIds())){
            //非管理员 没权限的时候应该查不出任何数据
            fullRegionPathList = Collections.singletonList(UUID.randomUUID().toString());
        }
        searchDTO.setFullRegionPathList(fullRegionPathList);
        searchDTO.setStoreId(searchRequest.getStoreId());
        searchDTO.setStoreIds(searchRequest.getStoreIds());
        searchDTO.setStatus(searchRequest.getStatus());
        searchDTO.setIsOverdue(searchRequest.getIsOverdue());
        searchDTO.setHandleUserId(searchRequest.getHandleUserId());
        searchDTO.setCreateUserId(searchRequest.getCreateUserId());
        if (Objects.nonNull(searchRequest.getBeginCreateDate())) {
            searchDTO.setBeginCreateDate(new Date(searchRequest.getBeginCreateDate()));
        }
        if (Objects.nonNull(searchRequest.getEndCreateDate())) {
            searchDTO.setEndCreateDate(new Date(searchRequest.getEndCreateDate()));
        }
        searchDTO.setMetaTableId(searchRequest.getMetaTableId());
        searchDTO.setMetaColumnIds(searchRequest.getMetaColumnIds());
        searchDTO.setTaskName(searchRequest.getTaskName());
        searchDTO.setApproveUserId(searchRequest.getApproveUserId());
        searchDTO.setQuestionType(searchRequest.getQuestionType());
        searchDTO.setSecondApproveUserId(searchRequest.getSecondApproveUserId());
        searchDTO.setThirdApproveUserId(searchRequest.getThirdApproveUserId());
        searchDTO.setQuestionParentInfoIdList(searchRequest.getQuestionParentInfoIdList());
        searchDTO.setIsAdmin(isAdmin);
        return searchDTO;
    }

    /**
     * 检查逾期
     *
     * @param recordDO TbQuestionRecordDO
     * @return boolean
     */
    private boolean checkOverdue(TbQuestionRecordDO recordDO) {
        Date checkTime = new Date();
        // 已完成状态，判断逾期条件为任务的完成时间
        if (UnifyNodeEnum.END_NODE.getCode().equals(recordDO.getStatus())) {
            checkTime = recordDO.getCompleteTime();
        }
        return checkTime.after(recordDO.getSubEndTime());
    }

    private void addHistoryAndPhoto(String enterpriseId, Boolean isComplete, JSONObject taskHandleData, String currNodeNo) {
        log.info("addHistoryAndPhoto#taskHandleData:{}", JSONObject.toJSONString(taskHandleData));
        Long subTaskId = taskHandleData.getLong("sub_task_id");
        TaskSubDO taskSubDO = taskSubMapper.getTaskSubDOById(enterpriseId, subTaskId);
        if (taskSubDO == null) {
            log.error("子任务存在:{}", enterpriseId);
            return;
        }
        JSONObject taskData = taskHandleData.getJSONObject("task_data");
        log.info("addHistoryAndPhoto taskData:{}", JSONObject.toJSONString(taskData));
        //工单记录
        TbQuestionRecordDO questionRecord = questionRecordDao.selectByTaskIdAndStoreId(enterpriseId, taskSubDO.getUnifyTaskId(), taskSubDO.getStoreId(), taskSubDO.getLoopCount());
        if (questionRecord == null) {
            log.error("工单记录不存在:{}", enterpriseId);
            return;
        }
        TaskStoreDO taskStoreDO = taskStoreDao.selectById(enterpriseId, questionRecord.getTaskStoreId());
        if (taskStoreDO == null) {
            log.error("门店任务不存在:{}，taskStoreId:{}", enterpriseId, questionRecord.getTaskStoreId());
            return;
        }
        log.info("addHistoryAndPhoto#taskStoreDO:{}", JSONObject.toJSONString(taskStoreDO));
        TbQuestionRecordExpandDO questionRecordExpandDO = questionRecordExpandDao.selectByRecordId(enterpriseId, questionRecord.getId());

        if (questionRecordExpandDO == null) {
            questionRecordExpandDO = new TbQuestionRecordExpandDO();
            questionRecordExpandDO.setCreateTime(new Date());
        }

        //更新工单状态
        TbQuestionRecordDO questionRecordUpdate = new TbQuestionRecordDO();
        questionRecordUpdate.setNodeNo(currNodeNo);
        questionRecordUpdate.setId(questionRecord.getId());
        questionRecordUpdate.setStatus(currNodeNo);
        if (UnifyNodeEnum.FIRST_NODE.getCode().equals(currNodeNo)) {
            questionRecordUpdate.setStatus(UnifyNodeEnum.FIRST_NODE.getCode());
        }
        if (!isComplete && !UnifyNodeEnum.FIRST_NODE.getCode().equals(currNodeNo)) {
            questionRecordUpdate.setStatus(UnifyNodeEnum.SECOND_NODE.getCode());
        }
        String photo = null;
        String video = null;
        if (taskData != null) {
            photo = taskData.getString(UnifyTaskConstant.TaskInfo.PHOTOS);
            video = taskData.getString(UnifyTaskConstant.TaskInfo.VIDEOS);
        }

        if (StringUtils.isNotBlank(video)) {
            video = getQuestionVideoHandelVideos(video);
        }
        String remark = taskHandleData.getString("remark");
        if (StringUtils.isEmpty(remark)) {
            remark = StringUtils.EMPTY;
        }
        String action = taskHandleData.getString("flow_action_key");
        //已完成
        if (isComplete) {
            questionRecordUpdate.setStatus(UnifyNodeEnum.END_NODE.getCode());
            questionRecordUpdate.setCompleteTime(new Date());
            questionRecordUpdate.setNodeNo(UnifyNodeEnum.END_NODE.getCode());
        }
        //状态空的话去，task_stroe表的节点
        if (StringUtils.isBlank(questionRecordUpdate.getStatus())) {
            questionRecordUpdate.setStatus(taskStoreDO.getNodeNo());
        }
        String nodeNo = taskHandleData.getString("flow_node_no");
        String handleAction = taskHandleData.getString("handle_action");
        String userId = taskSubDO.getHandleUserId();
        String userName = enterpriseUserDao.selectNameIgnoreActiveByUserId(enterpriseId, userId);
        // 发送流程引擎
        UnifyNodeEnum unifyNodeEnum = UnifyNodeEnum.getByCode(nodeNo);
        questionRecordExpandDO.setRecordId(questionRecord.getId());
        questionRecordExpandDO.setUnifyTaskId(questionRecord.getUnifyTaskId());
        questionRecordExpandDO.setUpdateTime(new Date());
        String operateType;
        //获取工单记录
        TbQuestionHistoryDO historyDO = new TbQuestionHistoryDO();
        switch (unifyNodeEnum) {
            case SECOND_NODE:
                operateType = UnifyTaskConstant.OperateType.APPROVE;
                questionRecordUpdate.setApproveTime(new Date());
                questionRecordUpdate.setApproveUserId(userId);
                questionRecordUpdate.setApproveUserName(userName);
                questionRecordUpdate.setApproveActionKey(action);
                questionRecordUpdate.setApproveRemark(remark);
                questionRecordExpandDO.setApprovePhoto(photo);
                questionRecordExpandDO.setApproveVideo(video);
                historyDO.setActionKey(action);
                break;
            case THIRD_NODE:
                operateType = UnifyTaskConstant.OperateType.APPROVE;
                questionRecordUpdate.setSecondApproveUserId(userId);
                questionRecordUpdate.setSecondApproveActionKey(action);
                historyDO.setActionKey(action);
                break;
            case FOUR_NODE:
                operateType = UnifyTaskConstant.OperateType.APPROVE;
                questionRecordUpdate.setThirdApproveUserId(userId);
                questionRecordUpdate.setThirdApproveActionKey(action);
                historyDO.setActionKey(action);
                break;
            default:
                questionRecordUpdate.setHandleTime(new Date());
                questionRecordUpdate.setHandleUserId(userId);
                questionRecordUpdate.setHandleUserName(userName);
                questionRecordUpdate.setHandleActionKey(handleAction);
                questionRecordUpdate.setHandleRemark(remark);
                questionRecordExpandDO.setHandlePhoto(photo);
                questionRecordExpandDO.setHandleVideo(video);
                historyDO.setActionKey(handleAction);
                operateType = UnifyTaskConstant.OperateType.HANDLE;
                //审核
                if (questionRecord.getFirstHandleTime() == null) {
                    questionRecordUpdate.setFirstHandleTime(questionRecordUpdate.getHandleTime());
                }
                break;
        }
        if (UnifyTaskConstant.OperateType.APPROVE.equals(operateType)) {
            //审核
            if (questionRecord.getFirstApproveTime() == null) {
                questionRecordUpdate.setFirstApproveTime(new Date());
            }
            if (QuestionActionKeyEnum.REJECT.getCode().equals(action)) {
                Integer approveRejectCount = questionRecord.getApproveRejectCount() == null ? 1 : questionRecord.getApproveRejectCount() + 1;
                questionRecordUpdate.setApproveRejectCount(approveRejectCount);
            }
            if (QuestionActionKeyEnum.PASS.getCode().equals(action)) {
                Integer approvePassCount = questionRecord.getApprovePassCount() == null ? 1 : questionRecord.getApprovePassCount() + 1;
                questionRecordUpdate.setApprovePassCount(approvePassCount);
            }
        }

        questionRecordDao.updateByPrimaryKeySelective(questionRecordUpdate, enterpriseId);
        //审批拒绝，情况人员信息
        if (QuestionActionKeyEnum.REJECT.getCode().equals(action)) {
            questionRecordDao.clearUserIdById(enterpriseId, questionRecordUpdate.getId());
        }

        if (questionRecordExpandDO.getId() == null) {
            questionRecordExpandDao.insertSelective(enterpriseId, questionRecordExpandDO);
        } else {
            questionRecordExpandDao.updateByPrimaryKeySelective(enterpriseId, questionRecordExpandDO);
        }

        //插入工单记录
        historyDO.setOperateType(operateType);
        historyDO.setOperateUserId(userId);
        historyDO.setOperateUserName(userName);
        historyDO.setSubTaskId(subTaskId);
        if (StringUtils.isBlank(historyDO.getActionKey())) {
            historyDO.setActionKey(action);
        }
        historyDO.setCreateTime(new Date());
        historyDO.setUpdateTime(new Date());
        historyDO.setRecordId(questionRecord.getId());
        historyDO.setNodeNo(nodeNo);
        historyDO.setPhoto(photo);
        historyDO.setVideo(video);
        historyDO.setRemark(remark);
        questionHistoryDao.insert(enterpriseId, historyDO);

        Long historyId = historyDO.getId();
        log.info("addHistoryAndPhoto##historyId:{}", historyId);
        //处理未转码完成视频
        if (StringUtils.isNotBlank(video)) {
            try {
                checkQuestionVideoHandel(enterpriseId, video, historyId);
            } catch (Exception e) {
                log.error("addHistoryAndPhoto##historyId:{},转码失败", historyId, e);
            }
        }
        //新增成功数量
        if (isComplete && questionRecord.getParentQuestionId() != null) {
            questionParentInfoDao.addFinishNum(enterpriseId, questionRecord.getParentQuestionId());
            //计算父工单子工单数量
            UnifySubStatisticsDTO unifySubStatisticsDTO = questionRecordDao.selectQuestionTaskCount(enterpriseId, questionRecord.getUnifyTaskId());
            TbQuestionParentInfoDO questionParentInfoDO = new TbQuestionParentInfoDO();
            if (unifySubStatisticsDTO.getAll().equals(unifySubStatisticsDTO.getComplete())) {
                questionParentInfoDO.setStatus(1);
                questionParentInfoDO.setId(questionRecord.getParentQuestionId());
                questionParentInfoDao.updateByPrimaryKeySelective(enterpriseId, questionParentInfoDO);
            }
        }
    }

    /**
     * user转PersonDTO
     *
     * @param userDO EnterpriseUserDO
     * @return PersonDTO
     */
    private PersonDTO parseUserToPersonDTO(EnterpriseUserDO userDO) {
        PersonDTO personDTO = new PersonDTO();
        personDTO.setUserId(userDO.getUserId());
        personDTO.setUserName(userDO.getName());
        personDTO.setAvatar(userDO.getAvatar());
        return personDTO;
    }

    private String getQuestionVideoHandelVideos(String videos) {

        SmallVideoInfoDTO smallVideoInfo = JSONObject.parseObject(videos, SmallVideoInfoDTO.class);

        if (smallVideoInfo == null || CollectionUtils.isEmpty(smallVideoInfo.getVideoList())) {
            return videos;
        }

        String callbackCache;
        SmallVideoDTO smallVideoCache;
        for (SmallVideoDTO smallVideo : smallVideoInfo.getVideoList()) {
            //如果转码完成
            if (smallVideo.getStatus() != null && smallVideo.getStatus() >= ResourceStatusEnum.TRANSCODE_FINISH.getValue()) {
                continue;
            }
            callbackCache = redisUtilPool.getString(RedisConstant.VIDEO_CALLBACK_CACHE + smallVideo.getVideoId());
            if (StringUtils.isNotBlank(callbackCache)) {
                smallVideoCache = JSONObject.parseObject(callbackCache, SmallVideoDTO.class);
                if (smallVideoCache != null && smallVideoCache.getStatus() != null && smallVideoCache.getStatus() >= ResourceStatusEnum.TRANSCODE_FINISH.getValue()) {
                    BeanUtils.copyProperties(smallVideoCache, smallVideo);
                    if (StringUtils.isNotBlank(smallVideo.getVideoUrl())) {
                        smallVideo.setVideoUrl(smallVideo.getVideoUrl().replace("http://", "https://"));
                    }
                    if (StringUtils.isNotBlank(smallVideo.getVideoUrlBefore())) {
                        smallVideo.setVideoUrlBefore(smallVideo.getVideoUrlBefore().replace("http://", "https://"));
                    }
                }
            }
        }
        return JSONObject.toJSONString(smallVideoInfo);
    }

    private void checkQuestionVideoHandel(String enterpriseId, String videos, Long historyId) {

        SmallVideoInfoDTO smallVideoInfo = JSONObject.parseObject(videos, SmallVideoInfoDTO.class);

        String callbackCache;
        SmallVideoDTO smallVideoCache;
        SmallVideoParam smallVideoParam;

        if (smallVideoInfo == null || CollectionUtils.isEmpty(smallVideoInfo.getVideoList())) {
            return;
        }

        for (SmallVideoDTO smallVideo : smallVideoInfo.getVideoList()) {

            callbackCache = redisUtilPool.getString(RedisConstant.VIDEO_CALLBACK_CACHE + smallVideo.getVideoId());

            //如果转码完成
            if (smallVideo.getStatus() != null && smallVideo.getStatus() >= ResourceStatusEnum.TRANSCODE_FINISH.getValue()) {
                continue;
            }

            if (StringUtils.isNotBlank(callbackCache)) {
                smallVideoCache = JSONObject.parseObject(callbackCache, SmallVideoDTO.class);
                if (smallVideoCache == null || smallVideoCache.getStatus() == null ||
                        smallVideoCache.getStatus() < ResourceStatusEnum.TRANSCODE_FINISH.getValue()) {
                    smallVideoParam = new SmallVideoParam();
                    setNotCompleteCache(smallVideoParam, smallVideo, historyId, enterpriseId);
                }
            } else {
                smallVideoParam = new SmallVideoParam();
                setNotCompleteCache(smallVideoParam, smallVideo, historyId, enterpriseId);
            }
        }
    }

    /**
     * 如果前端提交的时候，视频还没有转码成功，会把videoId存入缓存，回调的时候再进行处理
     *
     * @param smallVideoParam
     * @param smallVideo
     * @param historyId
     * @param enterpriseId
     * @return void
     * @author chenyupeng
     * @date 2021/10/14
     */
    public void setNotCompleteCache(SmallVideoParam smallVideoParam, SmallVideoDTO smallVideo, Long historyId, String enterpriseId) {
        smallVideoParam.setVideoId(smallVideo.getVideoId());
        smallVideoParam.setUploadType(UploadTypeEnum.QUESTION_SUMMIT.getValue());
        smallVideoParam.setBusinessId(historyId);
        smallVideoParam.setUploadTime(new Date());
        smallVideoParam.setEnterpriseId(enterpriseId);
        //存入未转码完成的map，vod回调的时候使用
        redisUtilPool.hashSet(RedisConstant.VIDEO_NOT_COMPLETE_CACHE, smallVideo.getVideoId(), JSONObject.toJSONString(smallVideoParam));
    }


    /**
     * 新建工单记录
     */
    @Override
    public void addQuestionRecord(TaskMessageDTO taskMessageDTO, Long dataColumnId, Long metaColumnId, Boolean contentLearnFirst) {
        String enterpriseId = taskMessageDTO.getEnterpriseId();
        QuestionTaskInfoDTO questionTaskInfoDTO = JSON.parseObject(taskMessageDTO.getTaskInfo(), QuestionTaskInfoDTO.class);
        Long unifyTaskId = taskMessageDTO.getUnifyTaskId();
        //获取检查项id
        if (metaColumnId == null || metaColumnId == 0L) {
            // 检查表ids
            List<UnifyFormDataDTO> unifyFormDataDTOList =
                    taskMappingMapper.selectMappingDataByTaskId(enterpriseId, unifyTaskId);
            if (CollectionUtils.isEmpty(unifyFormDataDTOList)) {
                metaColumnId = 0L;
            } else {
                Set<Long> metaTableIds = unifyFormDataDTOList.stream()
                        .map(a -> Long.valueOf(a.getOriginMappingId())).collect(Collectors.toSet());
                if (CollectionUtils.isNotEmpty(metaTableIds)) {
                    metaColumnId = new ArrayList<>(metaTableIds).get(0);
                }
            }
        }

        dataColumnId = dataColumnId == null ? 0 : dataColumnId;
        Long metaTableId = 0L;
        TbMetaStaTableColumnDO tbMetaStaTableColumnDO = tbMetaStaTableColumnMapper.selectByPrimaryKey(enterpriseId, metaColumnId);
        if (tbMetaStaTableColumnDO != null) {
            metaTableId = tbMetaStaTableColumnDO.getMetaTableId();
        }
        Integer createType = questionTaskInfoDTO.getCreateType();
        //如果没有传类型，默认手动
        createType = createType == null ? QuestionCreateTypeEnum.MANUAL.getCode() : createType;
        Long coopCount = taskMessageDTO.getLoopCount() == null ? 1L : taskMessageDTO.getLoopCount();
        String storeId = null;
        String taskName = null;
        String taskDesc = null;
        String attachUrl = questionTaskInfoDTO.getAttachUrl();
        if (taskMessageDTO.getTaskParentItemId() != null) {
            UnifyTaskParentItemDO unifyTaskParentItemDO = unifyTaskParentItemDao.selectByPrimaryKey(enterpriseId, taskMessageDTO.getTaskParentItemId());
            if (unifyTaskParentItemDO != null) {
                storeId = unifyTaskParentItemDO.getStoreId();
                taskName = unifyTaskParentItemDO.getItemName();
                taskDesc = unifyTaskParentItemDO.getTaskDesc();
            }
        }

        TaskStoreDO taskStoreDO = taskStoreMapper.getTaskQuestionStore(enterpriseId, unifyTaskId, storeId, coopCount);
        TaskParentDO taskParentDO = taskParentMapper.selectParentTaskById(enterpriseId, taskStoreDO.getUnifyTaskId());
        Long questionParentInfoId = null;
        TbQuestionParentInfoDO tbQuestionParentInfoDO = questionParentInfoDao.selectByUnifyTaskId(enterpriseId, taskStoreDO.getUnifyTaskId());
        if (tbQuestionParentInfoDO != null) {
            questionParentInfoId = tbQuestionParentInfoDO.getId();
        }

        if (StringUtils.isBlank(taskName)) {
            taskName = taskParentDO.getTaskName();
        }
        if (StringUtils.isBlank(taskDesc)) {
            taskDesc = taskParentDO.getTaskDesc();
        }
        if (StringUtils.isBlank(attachUrl)) {
            attachUrl = taskParentDO.getAttachUrl();
        }
        TbQuestionRecordDO questionRecordDO = new TbQuestionRecordDO();
        questionRecordDO.setUnifyTaskId(unifyTaskId);
        questionRecordDO.setCreateUserId(taskStoreDO.getCreateUserId());
        questionRecordDO.setStoreId(taskStoreDO.getStoreId());
        questionRecordDO.setTaskStoreId(taskStoreDO.getId());
        questionRecordDO.setCreateType(createType);
        questionRecordDO.setCreateTime(new Date());
        questionRecordDO.setUpdateTime(new Date());
        questionRecordDO.setDeleted(false);
        questionRecordDO.setStoreName(taskStoreDO.getStoreName());
        questionRecordDO.setRegionId(taskStoreDO.getRegionId());
        questionRecordDO.setRegionPath(taskStoreDO.getRegionWay());
        questionRecordDO.setMetaTableId(metaTableId);
        questionRecordDO.setMetaColumnId(metaColumnId);
        questionRecordDO.setDataColumnId(dataColumnId);
        questionRecordDO.setStatus(taskStoreDO.getNodeNo());
        questionRecordDO.setLearnFirst(contentLearnFirst);
        questionRecordDO.setAttachUrl(attachUrl);
        questionRecordDO.setTaskName(taskName);
        questionRecordDO.setSubBeginTime(taskStoreDO.getSubBeginTime());
        questionRecordDO.setSubEndTime(taskStoreDO.getSubEndTime());
        questionRecordDO.setTaskDesc(taskDesc);
        questionRecordDO.setParentQuestionName(taskParentDO.getTaskName());
        questionRecordDO.setLoopCount(taskStoreDO.getLoopCount());
        questionRecordDO.setParentQuestionId(questionParentInfoId);
        questionRecordDO.setNodeNo(taskStoreDO.getNodeNo());
        if (tbMetaStaTableColumnDO != null) {
            if (StringUtils.isNotBlank(tbMetaStaTableColumnDO.getAiType())) {
                questionRecordDO.setQuestionType(QuestionTypeEnum.AI.getCode());
            } else if (dataColumnId > 0) {
                questionRecordDO.setQuestionType(QuestionTypeEnum.PATROL_STORE.getCode());
            }
        }
        questionRecordDO.setQuestionType(QuestionTypeEnum.getQuestionTypeByCode(taskMessageDTO.getQuestionType()).getCode());
        questionRecordDao.insertSelective(questionRecordDO, enterpriseId);
        //锁定检查表
        if (metaTableId != null && metaTableId > 0) {
            tbMetaTableMapper.updateLockedByIds(enterpriseId, Collections.singletonList(metaTableId));
        }
        TbQuestionRecordExpandDO questionRecordExpandDO = new TbQuestionRecordExpandDO();
        questionRecordExpandDO.setCreateTime(new Date());
        questionRecordExpandDO.setRecordId(questionRecordDO.getId());
        questionRecordExpandDO.setUnifyTaskId(questionRecordDO.getUnifyTaskId());
        questionRecordExpandDO.setUpdateTime(new Date());
        questionRecordExpandDO.setDeleted(false);
        questionRecordExpandDO.setTaskInfo(taskMessageDTO.getTaskInfo());
        questionRecordExpandDao.insertSelective(enterpriseId, questionRecordExpandDO);
        //        //添加人员关系表
        questionParentUserMappingService.saveUserMapping(enterpriseId, taskStoreDO, questionParentInfoId, questionRecordDO.getParentQuestionName());
    }

    @Override
    public PageDTO<QuestionRecordListVO> questionList(String enterpriseId, QuestionDTO questionDTO) {
        //校验参数
        OpenApiParamCheckUtils.checkNecessaryParam(questionDTO.getPageSize(), questionDTO.getPageNum());
        //校验页码，最大100
        OpenApiParamCheckUtils.checkParamLimit(questionDTO.getPageSize(), 0, 100);
        OpenApiParamCheckUtils.checkTimeInterval(questionDTO.getBeginCreateDate(), questionDTO.getEndCreateDate());
        PageHelper.startPage(questionDTO.getPageNum(), questionDTO.getPageSize());
        List<TbQuestionRecordDO> tbQuestionRecordDOS = questionRecordDao.questionList(enterpriseId, questionDTO);
        PageInfo<TbQuestionRecordDO> tbQuestionRecordDOPageInfo = new PageInfo<>(tbQuestionRecordDOS);
        List<QuestionRecordListVO> questionRecordListVOS = new ArrayList<>();
        for (TbQuestionRecordDO tbQuestionRecordDO : tbQuestionRecordDOS) {
            QuestionRecordListVO questionRecordListVO = convertTbQuestionRecordDO(tbQuestionRecordDO);
            questionRecordListVOS.add(questionRecordListVO);
        }
        PageDTO<QuestionRecordListVO> questionRecordListVOPageDTO = new PageDTO<>();
        questionRecordListVOPageDTO.setPageNum(questionDTO.getPageNum());
        questionRecordListVOPageDTO.setPageSize(questionDTO.getPageSize());
        questionRecordListVOPageDTO.setTotal(tbQuestionRecordDOPageInfo.getTotal());
        questionRecordListVOPageDTO.setList(questionRecordListVOS);
        return questionRecordListVOPageDTO;
    }

    @Override
    public QuestionRecordDetailVO questionDetail(String enterpriseId, Long questionId) {
        TbQuestionRecordDO recordDO = questionRecordDao.selectById(questionId, enterpriseId);
        if (recordDO == null) {
            throw new ServiceException(ErrorCodeEnum.QUESTION_RECORD_NOT_EXIST);
        }
        // 查询门店任务
        TaskStoreDO taskStoreDO = taskStoreDao.selectById(enterpriseId, recordDO.getTaskStoreId());
        List<String> userIds = Lists.newArrayList(recordDO.getCreateUserId(), recordDO.getHandleUserId(), recordDO.getApproveUserId());
        // 抄送人
        List<String> ccUserIds = Lists.newArrayList(StringUtils.split(Optional.ofNullable(taskStoreDO.getCcUserIds())
                .orElse(Constants.EMPTY_STRING), Constants.COMMA));
        userIds.addAll(ccUserIds);
        // 获取门店任务的extendInfo字段，后面用作获取处理人和审批人
        JSONObject extendInfo = JSONObject.parseObject(taskStoreDO.getExtendInfo());
        // 处理人，如果是待处理状态 在子任务查询处理人。其他状态可以在taskStore查询
        List<String> handleUserIds = null;
        if (UnifyNodeEnum.FIRST_NODE.getCode().equals(recordDO.getStatus())) {
            List<PersonNodeNoDTO> personNodeNoDTOList = taskSubMapper.selectUserIdByLoopCountAndStoreIdList(enterpriseId, recordDO.getUnifyTaskId(),
                    Lists.newArrayList(recordDO.getStoreId()), taskStoreDO.getLoopCount(), null);
            handleUserIds = personNodeNoDTOList.stream().map(PersonNodeNoDTO::getUserId).collect(Collectors.toList());
        } else {
            // taskStore中extendInfo获取第一个节点，处理人
            handleUserIds = Lists.newArrayList(StringUtils.split(Optional.ofNullable(extendInfo.getString(UnifyNodeEnum.FIRST_NODE.getCode()))
                    .orElse(Constants.EMPTY_STRING), Constants.COMMA));
        }
        userIds.addAll(handleUserIds);
        // taskStore中extendInfo获取第二个节点，审批人
        List<String> secondNodePersons = Lists.newArrayList(StringUtils.split(Optional.ofNullable(extendInfo.getString(UnifyNodeEnum.SECOND_NODE.getCode()))
                .orElse(Constants.EMPTY_STRING), Constants.COMMA));
        userIds.addAll(secondNodePersons);
        // taskStore中extendInfo获取第三个节点，审批人
        List<String> thirdNodePersons = Lists.newArrayList(StringUtils.split(Optional.ofNullable(extendInfo.getString(UnifyNodeEnum.THIRD_NODE.getCode()))
                .orElse(Constants.EMPTY_STRING), Constants.COMMA));
        userIds.addAll(thirdNodePersons);
        // taskStore中extendInfo获取第三个节点，审批人
        List<String> fourNodePersons = Lists.newArrayList(StringUtils.split(Optional.ofNullable(extendInfo.getString(UnifyNodeEnum.FOUR_NODE.getCode()))
                .orElse(Constants.EMPTY_STRING), Constants.COMMA));
        userIds.addAll(fourNodePersons);
        if (StringUtils.isNotBlank(recordDO.getSecondApproveUserId())) {
            userIds.add(recordDO.getSecondApproveUserId());
        }
        if (StringUtils.isNotBlank(recordDO.getThirdApproveUserId())) {
            userIds.add(recordDO.getThirdApproveUserId());
        }
        List<TbQuestionHistoryVO> historyList = questionHistoryDao.selectHistoryList(questionId, enterpriseId);
        Set<String> userIdSet = ListUtils.emptyIfNull(historyList).stream().map(TbQuestionHistoryVO::getOperateUserId).collect(Collectors.toSet());
        Map<String, EnterpriseUserDO> nameMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(userIdSet)) {
            userIds.addAll(userIdSet);
        }
        // 查询用户
        List<EnterpriseUserDO> userDOList = enterpriseUserDao.selectByUserIds(enterpriseId, userIds.stream().distinct().collect(Collectors.toList()));
        Map<String, PersonBasicDTO> userDOMap = userDOList.stream().collect(Collectors.toMap(EnterpriseUserDO::getUserId, this::parseUserToPersonBasicDTO));
        TbMetaStaTableColumnDO metaStaTableColumnDO = tbMetaStaTableColumnDao.selectById(enterpriseId, recordDO.getMetaColumnId());
        TbDataStaTableColumnDO dataStaTableColumnDO = tbDataStaTableColumnDao.selectById(enterpriseId, recordDO.getDataColumnId());
        TbMetaTableDO metaTableDO = tbMetaTableDao.selectById(enterpriseId, recordDO.getMetaTableId());
        StoreDO storeDO = storeDao.getByStoreId(enterpriseId, recordDO.getStoreId());
        RegionDO regionDO = regionDao.getRegionById(enterpriseId, recordDO.getRegionId());
        TaskParentDO taskParentDO = taskParentDao.selectById(enterpriseId, recordDO.getUnifyTaskId());
        Map<String, EnterpriseUserDO> finalNameMap = nameMap;
        List<TbQuestionDealRecordVO> dealList = new ArrayList<>();
        historyList.forEach(e -> {
            TbQuestionDealRecordVO record = new TbQuestionDealRecordVO();
            record.setOperateType(e.getOperateType());
            record.setDealTime(e.getCreateTime());
            record.setOperateUserId(e.getOperateUserId());
            EnterpriseUserDO userDO = finalNameMap.get(e.getOperateUserId());
            if (userDO != null) {
                record.setOperateUserAvatar(userDO.getAvatar());
                record.setOperateUserName(userDO.getName());
            } else {
                record.setOperateUserName(e.getOperateUserName());
            }
            record.setActionKey(e.getActionKey());
            record.setRemark(e.getRemark());
            record.setPhotoList(JSONObject.parseArray(e.getPhoto(), String.class));
            JSONObject jsonObject = JSONObject.parseObject(e.getVideo());
            if(Objects.nonNull(jsonObject) && StringUtils.isNotBlank(jsonObject.getString("videoList"))){
                record.setVideoList(JSONObject.parseArray(jsonObject.getString("videoList"), TbQuestionDealRecordVO.VideoDTO.class));
            }
            dealList.add(record);
        });
        QuestionRecordDetailVO questionRecordDetailVO = convertTbQuestionRecordDetailDO(recordDO, metaTableDO, metaStaTableColumnDO, dataStaTableColumnDO,
                storeDO, regionDO, taskParentDO, userDOMap,
                handleUserIds, secondNodePersons, thirdNodePersons, fourNodePersons, ccUserIds, taskStoreDO.getNodeNo());
        questionRecordDetailVO.setDealList(dealList);
        return questionRecordDetailVO;
    }

    @Override
    public PageDTO<SubQuestionRecordListVO> subQuestionRecordList(String enterpriseId, QuestionRecordListRequest questionRecordListRequest) {
        // 查询问题工单列表（分页）
        TbQuestionRecordSearchDTO searchRequest = new TbQuestionRecordSearchDTO();
        searchRequest.setEnterpriseId(enterpriseId);
        searchRequest.setTaskName(questionRecordListRequest.getQuestionName());
        searchRequest.setQuestionParentInfoId(questionRecordListRequest.getQuestionParentInfoId());
        searchRequest.setIsOverdue(questionRecordListRequest.getOverdue());
        searchRequest.setStatus(questionRecordListRequest.getStatus());
        searchRequest.setNodeNo(questionRecordListRequest.getNodeNo());
        PageInfo<TbQuestionRecordDO> pageInfo = questionRecordDao.selectQuestionRecordPage(searchRequest,
                questionRecordListRequest.getPageNumber(), questionRecordListRequest.getPageSize());
        // 构造分页结果，没查到数据直接返回空分页结果
        PageDTO<SubQuestionRecordListVO> pageVO = new PageDTO<>();
        pageVO.setTotal(pageInfo.getTotal());
        pageVO.setPageNum(pageInfo.getPageNum());
        pageVO.setPageSize(pageInfo.getPageSize());
        if (CollectionUtils.isEmpty(pageInfo.getList())) {
            pageVO.setList(Lists.newArrayList());
            return pageVO;
        }
        String createUserId = pageInfo.getList().get(0).getCreateUserId();

        List<Long> metaColumnIdList = pageInfo.getList().stream().map(TbQuestionRecordDO::getMetaColumnId).collect(Collectors.toList());
        List<Long> metaTableIdList = pageInfo.getList().stream().map(TbQuestionRecordDO::getMetaTableId).collect(Collectors.toList());

        List<TbMetaStaTableColumnDO> metaStaTableColumnDOList = tbMetaStaTableColumnDao.selectByIds(enterpriseId, metaColumnIdList);
        Map<Long, String> metaStaTableColumnNameMap = metaStaTableColumnDOList.stream().collect(Collectors.toMap(TbMetaStaTableColumnDO::getId, TbMetaStaTableColumnDO::getColumnName));
        List<Long> taskStoreIdList = pageInfo.getList().stream().map(TbQuestionRecordDO::getTaskStoreId).collect(Collectors.toList());
        List<TaskStoreDO> storeDOList = taskStoreDao.selectQuestionTaskByIds(enterpriseId, taskStoreIdList, null);
        if (CollectionUtils.isEmpty(storeDOList)) {
            pageVO.setList(Lists.newArrayList());
            return pageVO;
        }
        //检查表名称
        List<TbMetaTableDO> tbMetaTableDOList = tbMetaTableDao.selectByIds(enterpriseId, metaTableIdList);
        Map<Long, String> metaTableNameMap = ListUtils.emptyIfNull(tbMetaTableDOList).stream().collect(Collectors.toMap(TbMetaTableDO::getId, TbMetaTableDO::getTableName));

        //处理人列表
        Map<Long, List<String>> hanUserIdMap = new HashMap<>();
        Map<Long, String> nodeNoMap = new HashMap<>();
        storeDOList.forEach(taskStoreDO -> {
            List<String> handleUserIdList = getCurrentHandleUserId(enterpriseId, taskStoreDO);
            hanUserIdMap.put(taskStoreDO.getId(), handleUserIdList);
            nodeNoMap.put(taskStoreDO.getId(), taskStoreDO.getNodeNo());
        });
        Map<Long, List<PersonDTO>> currentHandleUserName = getCurrentHandleUserName(enterpriseId, hanUserIdMap);
        //发起人
        String createUserName = enterpriseUserDao.selectNameIgnoreActiveByUserId(enterpriseId, createUserId);
        List<SubQuestionRecordListVO> recordList = new ArrayList<>();
        pageInfo.getList().forEach(recordDO -> {
            SubQuestionRecordListVO subQuestionRecordListVO = new SubQuestionRecordListVO();
            subQuestionRecordListVO.setId(recordDO.getId());
            subQuestionRecordListVO.setTaskStoreId(recordDO.getTaskStoreId());
            subQuestionRecordListVO.setSubBeginTime(recordDO.getSubBeginTime());
            subQuestionRecordListVO.setSubEndTime(recordDO.getSubEndTime());
            subQuestionRecordListVO.setStatus(recordDO.getStatus());
            subQuestionRecordListVO.setTaskName(recordDO.getTaskName());
            subQuestionRecordListVO.setUnifyTaskId(recordDO.getUnifyTaskId());
            subQuestionRecordListVO.setTaskDesc(recordDO.getTaskDesc());
            subQuestionRecordListVO.setStoreId(recordDO.getStoreId());
            subQuestionRecordListVO.setStoreName(recordDO.getStoreName());
            subQuestionRecordListVO.setOverdue(recordDO.getSubEndTime().before(new Date()));
            subQuestionRecordListVO.setCreateUserId(recordDO.getCreateUserId());
            subQuestionRecordListVO.setCreateUserName(createUserName);
            subQuestionRecordListVO.setLoopCount(recordDO.getLoopCount());
            subQuestionRecordListVO.setQuestionType(recordDO.getQuestionType());
            subQuestionRecordListVO.setMetaColumnId(recordDO.getMetaColumnId());
            subQuestionRecordListVO.setMetaColumnName(metaStaTableColumnNameMap.get(recordDO.getMetaColumnId()));
            subQuestionRecordListVO.setCurrentUserList(currentHandleUserName.get(recordDO.getTaskStoreId()));
            subQuestionRecordListVO.setMetaTableId(recordDO.getMetaTableId());
            subQuestionRecordListVO.setMetaTableName(metaTableNameMap.get(recordDO.getMetaTableId()));
            subQuestionRecordListVO.setNodeNo(nodeNoMap.get(recordDO.getTaskStoreId()));
            if (UnifyNodeEnum.END_NODE.getCode().equals(recordDO.getStatus())) {
                subQuestionRecordListVO.setOverdue(recordDO.getSubEndTime().before(recordDO.getCompleteTime()));
            }
            recordList.add(subQuestionRecordListVO);
        });
        pageVO.setList(recordList);
        return pageVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delQuestionRecord(String enterpriseId, Long recordId, String dingCorpId, String appType) {
        TbQuestionRecordDO recordDO = questionRecordDao.selectById(recordId, enterpriseId);
        if (recordDO == null || recordDO.getDeleted()) {
            throw new ServiceException(ErrorCodeEnum.QUESTION_RECORD_NOT_EXIST);
        }
        TaskStoreDO taskStoreDO = taskStoreDao.selectById(enterpriseId, recordDO.getTaskStoreId());
        unifyTaskStoreService.fillSingleTaskStoreExtendAndCcInfo(enterpriseId, taskStoreDO);
        Map<String, List<String>> nodePersonMap = unifyTaskStoreService.getNodePersonByTaskStore(taskStoreDO);
        Map<SendUserTypeEnum, List<String>> sendUserIds = getSendUserIds(nodePersonMap, taskStoreDO.getNodeNo());
        String title = recordDO.getTaskName();
        String content = AppTypeEnum.isQwType(appType) ? "【{0}】的工单【{1}】已被【$userName={2}$】删除，请知悉~" : "【{0}】的工单【{1}】已被【{2}】删除，请知悉~";
        content = MessageFormat.format(content, recordDO.getStoreName(), recordDO.getTaskName(), UserHolder.getUser().getName());
        // 删除父任务权限校验（只有创建人管理员可以删除）
        String currentUserId = UserHolder.getUser().getUserId();
        // 是否创建人
        boolean isCreateUser = currentUserId.equals(recordDO.getCreateUserId());
        // 是否管理员
        boolean isAdmin = sysRoleService.checkIsAdmin(enterpriseId, currentUserId);
        if (!isCreateUser && !isAdmin) {
            throw new ServiceException(ErrorCodeEnum.NO_PERMISSION.getCode(), "非创建人或管理员，无删除权限");
        }

        UnifySubStatisticsDTO unifySubStatisticsDTO = questionRecordDao.selectQuestionTaskCount(enterpriseId, recordDO.getUnifyTaskId());
        //子工单只有一个删除同时删除父工单
        if (unifySubStatisticsDTO.getAll() == 1) {
            TbQuestionParentInfoDO tbQuestionParentInfoDO = questionParentInfoDao.selectByUnifyTaskId(enterpriseId, recordDO.getUnifyTaskId());
            questionParentInfoService.deleteQuestion(enterpriseId, tbQuestionParentInfoDO.getId(), appType, dingCorpId);
            return;
        }
        // 删除子任务表
        taskSubMapper.delSubTaskByTaskIdAndStoreIdAndLoopCount(enterpriseId, recordDO.getUnifyTaskId(), recordDO.getStoreId(), taskStoreDO.getLoopCount());

        taskStoreDO.setDeleted(1);
        taskStoreDao.delTaskStoreById(enterpriseId, taskStoreDO.getId());
        // 删除子任务表
        TaskMessageDTO taskMessage = new TaskMessageDTO(UnifyTaskConstant.TaskMessage.OPERATE_DELETE, taskStoreDO.getUnifyTaskId(),
                taskStoreDO.getTaskType(), null, System.currentTimeMillis(), JSON.toJSONString(taskStoreDO), enterpriseId, null, null);
        taskMessage.setQuestionRecordId(recordId);
        taskMessage.setStoreId(recordDO.getStoreId());
        taskMessage.setLoopCount(recordDO.getLoopCount());
        unifyTaskService.sendTaskMessage(taskMessage);
        jmsTaskService.sendDeleteQuestionReminder(enterpriseId, sendUserIds, title, content, recordDO.getParentQuestionId(), recordDO.getId());
    }

    /**
     * 获取发送者
     *
     * @param nodePersonMap
     * @param nodeNo
     * @return
     */
    private Map<SendUserTypeEnum, List<String>> getSendUserIds(Map<String, List<String>> nodePersonMap, String nodeNo) {
        if (Objects.isNull(nodePersonMap)) {
            return Maps.newHashMap();
        }
        Map<SendUserTypeEnum, List<String>> resultMap = new HashMap<>();
        List<String> userIds = nodePersonMap.get(nodeNo);
        resultMap.put(SendUserTypeEnum.CREATE_USER, nodePersonMap.get(UnifyNodeEnum.ZERO_NODE.getCode()));
        resultMap.put(SendUserTypeEnum.CC_USER, nodePersonMap.get(UnifyNodeEnum.CC.getCode()));
        resultMap.put(UnifyNodeEnum.isApproveNode(nodeNo) ? SendUserTypeEnum.APPROVE_USER : SendUserTypeEnum.HANDLER_USER, userIds);
        return resultMap;
    }

    @Override
    public ResponseResult<List<String>> questionReminder(String enterpriseId, Long questionParentInfoId, Long questionRecordId, String appType) {
        //钉钉类型的企业返回需要催办的人即可，其他类型的企业，发送通知
        //先查询对应节点的人
        Long unifyTaskId = null;
        String storeId = null;
        Long loopCount = null;
        String title, content;
        if (questionRecordId != null) {
            //某个子工单的催办, 获取某个子工单当前节点的处理人
            TbQuestionRecordDO tbQuestionRecordDO = questionRecordDao.selectById(questionRecordId, enterpriseId);
            unifyTaskId = tbQuestionRecordDO.getUnifyTaskId();
            storeId = tbQuestionRecordDO.getStoreId();
            loopCount = tbQuestionRecordDO.getLoopCount();
            title = tbQuestionRecordDO.getTaskName();
            String createName = enterpriseUserDao.selectNameIgnoreActiveByUserId(enterpriseId, tbQuestionRecordDO.getCreateUserId());
            content = AppTypeEnum.isQwType(appType) ? "您有【$userName={0}$】发起的【{1}】的工单【{2}】需处理，请您于【{3}】前完成" : "您有【{0}】发起的【{1}】的工单【{2}】需处理，请您于【{3}】前完成";
            content = MessageFormat.format(content, createName, tbQuestionRecordDO.getStoreName(), tbQuestionRecordDO.getTaskName(), DateUtil.format(tbQuestionRecordDO.getSubEndTime(), DateUtils.DATE_FORMAT_MINUTE));
        } else {
            TbQuestionParentInfoDO tbQuestionParentInfoDO = questionParentInfoDao.selectById(enterpriseId, questionParentInfoId);
            unifyTaskId = tbQuestionParentInfoDO.getUnifyTaskId();
            title = tbQuestionParentInfoDO.getQuestionName();
            String createName = enterpriseUserDao.selectNameIgnoreActiveByUserId(enterpriseId, tbQuestionParentInfoDO.getCreateId());
            content = AppTypeEnum.isQwType(appType) ? "您有【$userName={0}$】发起的工单【{1}】需处理，请尽快处理" : "您有【{0}】发起的工单【{1}】需处理，请尽快处理";
            content = MessageFormat.format(content, createName, title);
        }
        List<String> pendingUserList = taskSubDao.getPendingUserByUnifyTaskId(enterpriseId, unifyTaskId, storeId, loopCount);
        if (AppTypeEnum.isDingType(appType)) {
            log.info("$$$$$$$$$$$$$$$$$$$$$$$$$$");
            return ResponseResult.success(pendingUserList);
        }
        //发送通知
        jmsTaskService.sendQuestionReminder(enterpriseId, questionParentInfoId, unifyTaskId, storeId, loopCount, pendingUserList, title, content);
        return ResponseResult.success(pendingUserList);
    }

    @Override
    public void batchQuestionReminder(String enterpriseId, List<Long> questionParentInfoIds, String appType) {
        List<TbQuestionParentInfoDO> tbQuestionParentInfoDOList = questionParentInfoDao.selectByIdList(enterpriseId, questionParentInfoIds);
        if (CollectionUtils.isEmpty(tbQuestionParentInfoDOList)) {
            return;
        }
        //钉钉类型的企业返回需要催办的人即可，其他类型的企业，发送通知
        //先查询对应节点的人
        Long unifyTaskId = null;
        String storeId = null;
        Long loopCount = null;
        String title, content;
        for (TbQuestionParentInfoDO tbQuestionParentInfoDO : tbQuestionParentInfoDOList) {
            unifyTaskId = tbQuestionParentInfoDO.getUnifyTaskId();
            title = tbQuestionParentInfoDO.getQuestionName();
            String createName = enterpriseUserDao.selectNameIgnoreActiveByUserId(enterpriseId, tbQuestionParentInfoDO.getCreateId());
            content = AppTypeEnum.isQwType(appType) ? "您有【$userName={0}$】发起的工单【{1}】需处理，请尽快处理" : "您有【{0}】发起的工单【{1}】需处理，请尽快处理";
            content = MessageFormat.format(content, createName, title);
            List<String> pendingUserList = taskSubDao.getPendingUserByUnifyTaskId(enterpriseId, unifyTaskId, storeId, loopCount);
            //发送通知
            jmsTaskService.sendQuestionReminder(enterpriseId, tbQuestionParentInfoDO.getId(), unifyTaskId, storeId, loopCount, pendingUserList, title, content);
        }
    }

    @Override
    public void questionShare(String enterpriseId, Boolean isOneKeyShare, List<Long> questionRecordIds, String shareUserId, String shareKey) {
        if (CollectionUtils.isEmpty(questionRecordIds)) {
            throw new ServiceException(ErrorCodeEnum.PARAMS_INVALID_ERROR);
        }
        String key = MessageFormat.format(RedisConstant.QUESTION_SHARE_KEY, shareKey);
        Map<String, Object> cacheValue = new HashMap<>();
        cacheValue.put("isOneKeyShare", Objects.isNull(isOneKeyShare) ? Boolean.FALSE : isOneKeyShare);
        cacheValue.put("questionRecordIds", questionRecordIds);
        cacheValue.put("enterpriseId", enterpriseId);
        redisUtilPool.setString(key, JSON.toJSONString(cacheValue), RedisConstant.SEVEN_DAY);
    }

    @Override
    public ResponseResult getQuestionShareDetail(String enterpriseId, Long questionRecordId, String shareKey) throws ApiException {
        if (Objects.nonNull(questionRecordId)) {
            //子工单id不为空 获取子工单详情
            TbQuestionRecordDO tbQuestionRecordDO = questionRecordDao.selectById(questionRecordId, enterpriseId);
            TbQuestionRecordDetailVO detail = detail(enterpriseId, tbQuestionRecordDO.getUnifyTaskId(), tbQuestionRecordDO.getStoreId(), null, tbQuestionRecordDO.getLoopCount());
            return ResponseResult.success(detail);
        }
        String key = MessageFormat.format(RedisConstant.QUESTION_SHARE_KEY, shareKey);
        String value = redisUtilPool.getString(key);
        if (StringUtils.isBlank(value)) {
            //分享链接失效
            throw new ServiceException(ErrorCodeEnum.SHARE_KEY_EXPIRE);
        }
        JSONObject jsonObject = JSONObject.parseObject(value);
        List<Long> questionRecordIds = JSON.parseArray(jsonObject.getString("questionRecordIds"), Long.class);
        Boolean isOneKeyShare = jsonObject.getBoolean("isOneKeyShare");
        EnterpriseConfigDTO enterpriseConfig = enterpriseConfigApiService.getEnterpriseConfig(enterpriseId);
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());
        if (CollectionUtils.isEmpty(questionRecordIds)) {
            return ResponseResult.success();
        }
        if (!isOneKeyShare) {
            //子工单id不为空 获取子工单详情
            TbQuestionRecordDO tbQuestionRecordDO = questionRecordDao.selectById(questionRecordIds.get(Constants.INDEX_ZERO), enterpriseId);
            TbQuestionRecordDetailVO detail = detail(enterpriseId, tbQuestionRecordDO.getUnifyTaskId(), tbQuestionRecordDO.getStoreId(), null, tbQuestionRecordDO.getLoopCount());
            return ResponseResult.success(detail);
        }
        List<TbQuestionRecordDO> tbQuestionRecordDOS = questionRecordDao.questionListByIds(enterpriseId, questionRecordIds);
        List<TbQuestionHistoryVO> tbQuestionHistoryVOS = questionHistoryDao.selectHistoryListByRecordIdList(enterpriseId, questionRecordIds, null);
        Map<Long, List<TbQuestionHistoryVO>> questionHistoryMap = tbQuestionHistoryVOS.stream().collect(Collectors.groupingBy(k -> k.getRecordId()));
        List<Long> metaColumnIdList = tbQuestionRecordDOS.stream().map(TbQuestionRecordDO::getMetaColumnId).filter(Objects::nonNull).collect(Collectors.toList());
        List<TbMetaStaTableColumnDO> metaStaTableColumnDOList = tbMetaStaTableColumnDao.selectByIds(enterpriseId, metaColumnIdList);
        Map<Long, String> metaStaTableColumnNameMap = metaStaTableColumnDOList.stream().collect(Collectors.toMap(TbMetaStaTableColumnDO::getId, TbMetaStaTableColumnDO::getColumnName));
        List<SubQuestionRecordListVO> resultList = new ArrayList<>();
        tbQuestionRecordDOS.forEach(o -> {
            SubQuestionRecordListVO result = SubQuestionRecordListVO.convertToVO(o);
            result.setMetaColumnName(metaStaTableColumnNameMap.get(o.getMetaColumnId()));
            result.setHandler(Boolean.FALSE);
            result.setNodeNo(UnifyNodeEnum.END_NODE.getCode());
            result.setOperateHistory(questionHistoryMap.get(o.getId()));
            resultList.add(result);
        });
        return ResponseResult.success(resultList);
    }

    @Override
    public PageDTO<SubQuestionDetailVO> subQuestionDetailList(String enterpriseId, String userId, TbQuestionRecordSearchRequest request) {
        //如果分页参数为null，给默认值
        if (request.getPageSize() == null || request.getPageNumber() == null) {
            request.setPageSize(Constants.DEFAULT_PAGE_SIZE);
            request.setPageNumber(Constants.INDEX_ONE);
        }
        //如果regionIds为空数组，查出用户所管辖区域填充
        if (CollectionUtils.isEmpty(request.getRegionIds())){
            List<SelectComponentRegionVO> allRegionList = selectionComponentService.getRegionAndStore(enterpriseId, null, UserHolder.getUser().getUserId(), null).getAllRegionList();
            if (CollectionUtils.isEmpty(allRegionList)){
                return new PageDTO<>();
            }
            List<String> ids = allRegionList.stream().map(SelectComponentRegionVO::getId).collect(Collectors.toList());
            request.setRegionIds(ids);
        }
        String fullRegionPath = "";
        if (StringUtils.isNotEmpty(request.getRegionId())) {
            RegionDO regionDO = regionDao.getRegionById(enterpriseId, Long.valueOf(request.getRegionId()));
            fullRegionPath = regionDO.getFullRegionPath();
        }
        //兼容
        List<String> fullRegionPathList = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(request.getRegionIds())) {
            List<String> regionIds = request.getRegionIds();
            fullRegionPathList = regionDao.getRegionByRegionIds(enterpriseId, regionIds).stream().map(RegionDO::getFullRegionPath).collect(Collectors.toList());
        }
        //查询子工单数据
        PageInfo<TbQuestionRecordDO> tbQuestionRecordDOPageInfo = questionRecordDao.selectSubQuestionDetailList(enterpriseId, fullRegionPath,fullRegionPathList, request, request.getPageNumber(), request.getPageSize());
        List<TbQuestionRecordDO> list = tbQuestionRecordDOPageInfo.getList();
        List<Long> questionRecordIds = list.stream().map(TbQuestionRecordDO::getId).collect(Collectors.toList());
        List<String> storeIds = list.stream().map(TbQuestionRecordDO::getStoreId).collect(Collectors.toList());
        List<StoreDO> storeDOList = storeDao.getByStoreIdList(enterpriseId, storeIds);
        Map<String, String> storeNameMap = storeDOList.stream().collect(Collectors.toMap(StoreDO::getStoreId, StoreDO::getStoreName));
        List<Long> unApprovelTaskStoreIds = list.stream().map(TbQuestionRecordDO::getTaskStoreId).distinct().collect(Collectors.toList());
        List<TaskStoreDO> taskStoreList = taskStoreDao.selectQuestionTaskByIds(enterpriseId, unApprovelTaskStoreIds, null);
        Map<Long, TaskStoreDO> taskStoreMap = ListUtils.emptyIfNull(taskStoreList).stream().collect(Collectors.toMap(TaskStoreDO::getId, Function.identity()));
        List<Long> regionids = list.stream().map(TbQuestionRecordDO::getRegionId).collect(Collectors.toList());
        List<RegionDO> regionDOS = regionDao.getAllRegionByRegionIds(enterpriseId, regionids);
        Map<Long, String> regionNameMap = regionDOS.stream().collect(Collectors.toMap(RegionDO::getId, RegionDO::getName));

        List<String> createUserIds = list.stream().map(TbQuestionRecordDO::getCreateUserId).collect(Collectors.toList());
        Map<String, String> createUserNameMap = enterpriseUserDao.getUserNameMap(enterpriseId, createUserIds);

        List<Long> metaColumnIds = list.stream().map(TbQuestionRecordDO::getMetaColumnId).collect(Collectors.toList());
        List<TbMetaStaTableColumnDO> tbMetaStaTableColumnDOS = tbMetaStaTableColumnDao.selectByIds(enterpriseId, metaColumnIds);
        Map<Long, String> metaColumnMap = tbMetaStaTableColumnDOS.stream().collect(Collectors.toMap(TbMetaStaTableColumnDO::getId, TbMetaStaTableColumnDO::getColumnName));

        List<Long> metaTableIds = list.stream().map(TbQuestionRecordDO::getMetaTableId).collect(Collectors.toList());
        List<TbMetaTableDO> tbMetaTableDOS = tbMetaTableDao.selectByIds(enterpriseId, metaTableIds);
        Map<Long, String> metaTableMap = tbMetaTableDOS.stream().collect(Collectors.toMap(TbMetaTableDO::getId, TbMetaTableDO::getTableName));

        List<Long> dataColumnIds = list.stream().map(TbQuestionRecordDO::getDataColumnId).collect(Collectors.toList());
        List<TbDataStaTableColumnDO> tbDataStaTableColumnDOS = tbDataStaTableColumnDao.selectByIds(enterpriseId, dataColumnIds);
        Map<Long, TbDataStaTableColumnDO> dataStaTableColumnDOMap = tbDataStaTableColumnDOS.stream().collect(Collectors.toMap(TbDataStaTableColumnDO::getId, Function.identity()));

        List<SwStoreWorkDataTableColumnDO> storeWorkDataColumnList = swStoreWorkDataTableColumnDao.selectByIds(enterpriseId, dataColumnIds);
        Map<Long, SwStoreWorkDataTableColumnDO> storeWorkDataColumnMap = storeWorkDataColumnList.stream().collect(Collectors.toMap(SwStoreWorkDataTableColumnDO::getId, Function.identity()));

        List<TbQuestionRecordExpandDO> expandDOList = questionRecordExpandDao.selectByQuestionRecordIds(enterpriseId, questionRecordIds);
        Map<Long, String> expandDOMap = expandDOList.stream().collect(Collectors.toMap(TbQuestionRecordExpandDO::getRecordId, TbQuestionRecordExpandDO::getTaskInfo));

        if (CollectionUtils.isEmpty(list)) {
            return new PageDTO<>();
        }
        List<SubQuestionDetailVO> subQuestionDetailVOS = new ArrayList<>();
        for (TbQuestionRecordDO temp : list) {
            SubQuestionDetailVO subQuestionDetailVO = new SubQuestionDetailVO();
            subQuestionDetailVO.setId(temp.getId());
            subQuestionDetailVO.setQuestionName(temp.getParentQuestionName());
            subQuestionDetailVO.setQuestionType(QuestionTypeEnum.getByCode(temp.getQuestionType()));
            subQuestionDetailVO.setSubQuestionCode(temp.getId());
            subQuestionDetailVO.setSubQuestionName(temp.getTaskName());
            subQuestionDetailVO.setAttachUrl(temp.getAttachUrl());
            subQuestionDetailVO.setUnifyTaskId(temp.getUnifyTaskId());
            subQuestionDetailVO.setLoopCount(temp.getLoopCount());
            subQuestionDetailVO.setBeginCreateDate(temp.getCreateTime());
            subQuestionDetailVO.setCreateUserId(temp.getCreateUserId());
            subQuestionDetailVO.setCreateUserName(createUserNameMap.getOrDefault(temp.getCreateUserId(), "AI用户"));
            subQuestionDetailVO.setMetaColumnId(temp.getMetaColumnId());
            subQuestionDetailVO.setMetaColumnName(metaColumnMap.getOrDefault(temp.getMetaColumnId(), ""));
            subQuestionDetailVO.setMetaTableId(temp.getMetaTableId());
            subQuestionDetailVO.setMetaTableName(metaTableMap.getOrDefault(temp.getMetaTableId(), ""));
            subQuestionDetailVO.setParentQuestionId(temp.getParentQuestionId());
            subQuestionDetailVO.setRegionId(temp.getRegionId());
            subQuestionDetailVO.setRegionName(regionNameMap.getOrDefault(temp.getRegionId(), ""));
            subQuestionDetailVO.setStoreId(temp.getStoreId());
            subQuestionDetailVO.setStoreName(storeNameMap.getOrDefault(temp.getStoreId(), ""));
            subQuestionDetailVO.setSubEndTime(temp.getSubEndTime());
            subQuestionDetailVO.setTaskDesc(temp.getTaskDesc());
            subQuestionDetailVO.setStatus(temp.getStatus());
            subQuestionDetailVO.setTaskInfo(expandDOMap.get(temp.getId()));
            TaskStoreDO taskStore = taskStoreMap.get(temp.getTaskStoreId());
            subQuestionDetailVO.setIsCanHandled(isHandledUser(taskStore, userId, temp.getNodeNo()));
            TbDataStaTableColumnDO dataStaTableColumnDO = dataStaTableColumnDOMap.get(temp.getDataColumnId());
            if (Objects.nonNull(dataStaTableColumnDO)) {
                subQuestionDetailVO.setColumnCheckResult(dataStaTableColumnDO.getCheckResultName());
                BigDecimal checkScore = Objects.nonNull(dataStaTableColumnDO.getCheckScore()) ? dataStaTableColumnDO.getCheckScore().multiply(dataStaTableColumnDO.getScoreTimes()).setScale(Constants.SCALE, RoundingMode.HALF_UP) : new BigDecimal(Constants.ZERO);
                subQuestionDetailVO.setCheckScore(checkScore);
                if (!Objects.isNull(dataStaTableColumnDO.getRewardPenaltMoney())) {
                    subQuestionDetailVO.setRewardPenaltMoney(Constants.RMB + dataStaTableColumnDO.getRewardPenaltMoney().multiply(dataStaTableColumnDO.getAwardTimes()).setScale(Constants.SCALE, RoundingMode.HALF_UP));
                }
            }
            if(QuestionTypeEnum.STORE_WORK.getCode().equals(temp.getQuestionType())){
                SwStoreWorkDataTableColumnDO storeWorkDataColumn = storeWorkDataColumnMap.get(temp.getDataColumnId());
                if (Objects.nonNull(storeWorkDataColumn)) {
                    subQuestionDetailVO.setColumnCheckResult(storeWorkDataColumn.getCheckResultName());
                    BigDecimal checkScore = Objects.nonNull(storeWorkDataColumn.getCheckScore()) ? storeWorkDataColumn.getCheckScore().multiply(storeWorkDataColumn.getScoreTimes()).setScale(Constants.SCALE, RoundingMode.HALF_UP) : new BigDecimal(Constants.ZERO);
                    subQuestionDetailVO.setCheckScore(checkScore);
                    if (!Objects.isNull(storeWorkDataColumn.getRewardPenaltMoney())) {
                        subQuestionDetailVO.setRewardPenaltMoney(Constants.RMB + storeWorkDataColumn.getRewardPenaltMoney().multiply(storeWorkDataColumn.getAwardTimes()).setScale(Constants.SCALE, RoundingMode.HALF_UP));
                    }
                }
            }
            //默认不逾期
            Boolean overdue = checkOverdue(temp);
            String overDueStr = overdue ? OVERDUE : NOT_OVERDUE;
            UnifyNodeEnum unifyNodeEnum = UnifyNodeEnum.getByCode(temp.getStatus());
            if (unifyNodeEnum != null) {
                subQuestionDetailVO.setStatusStr(unifyNodeEnum.getDesc());
            }
            subQuestionDetailVO.setOverDue(overdue);
            subQuestionDetailVO.setOverDueStr(overDueStr);
            //导出全区域
            if (request.getExport() != null && request.getExport()) {
                RegionPathNameVO regionPathNameVO = regionService.getAllRegionName(enterpriseId, temp.getRegionId());
                // 区域信息
                subQuestionDetailVO.setFullRegionName(regionPathNameVO.getAllRegionName());
            }
            subQuestionDetailVOS.add(subQuestionDetailVO);
        }

        PageDTO<SubQuestionDetailVO> pageDTO = new PageDTO<>();
        pageDTO.setList(subQuestionDetailVOS);
        pageDTO.setPageSize(request.getPageSize());
        pageDTO.setPageNum(request.getPageNumber());
        pageDTO.setTotal(tbQuestionRecordDOPageInfo.getTotal());
        return pageDTO;
    }

    private boolean isHandledUser(TaskStoreDO taskStore, String userId, String nodeNo){
        if(Objects.isNull(taskStore) || StringUtils.isBlank(taskStore.getExtendInfo())){
            return false;
        }
        Map<String, String> extendInfo = JSONObject.parseObject(taskStore.getExtendInfo(), Map.class);
        Map<String, List<String>> nodePerson = extendInfo.entrySet().stream()
                .filter(v -> UnifyNodeEnum.isHandleNode(v.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, v -> Arrays.asList(StringUtils.split(v.getValue(), ","))));
        List<String> auditUserList = nodePerson.get(nodeNo);
        return CollectionUtils.isNotEmpty(auditUserList) && auditUserList.contains(userId);
    }


    @Override
    public PageDTO<TbQuestionSubRecordListExportVO> subQuestionDetailListForExport(String enterpriseId, TbQuestionRecordSearchRequest request) {
        //如果分页参数为null，给默认值
        if (request.getPageSize() == null || request.getPageNumber() == null) {
            request.setPageSize(Constants.DEFAULT_PAGE_SIZE);
            request.setPageNumber(Constants.INDEX_ONE);
        }
        String fullRegionPath = "";
        if (StringUtils.isNotEmpty(request.getRegionId())) {
            RegionDO regionDO = regionDao.getRegionById(enterpriseId, Long.valueOf(request.getRegionId()));
            fullRegionPath = regionDO.getFullRegionPath();
        }
        //兼容
        List<String> fullRegionPathList = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(request.getRegionIds())) {
            List<String> regionIds = request.getRegionIds();
            fullRegionPathList = regionDao.getRegionByRegionIds(enterpriseId, regionIds).stream().map(RegionDO::getFullRegionPath).collect(Collectors.toList());
        }
        //查询子工单数据
        PageInfo<TbQuestionRecordDO> pageInfo = questionRecordDao.selectSubQuestionDetailList(enterpriseId, fullRegionPath, fullRegionPathList,request, request.getPageNumber(), request.getPageSize());
        // 构造分页结果，没查到数据直接返回空分页结果
        PageDTO<TbQuestionSubRecordListExportVO> pageVO = new PageDTO<>();
        pageVO.setTotal(pageInfo.getTotal());
        pageVO.setPageNum(pageInfo.getPageNum());
        pageVO.setPageSize(pageInfo.getPageSize());
        if (CollectionUtils.isEmpty(pageInfo.getList())) {
            pageVO.setList(Lists.newArrayList());
            return pageVO;
        }
        List<Long> questionRecordIds = pageInfo.getList().stream().map(TbQuestionRecordDO::getId).distinct().collect(Collectors.toList());
        // 检查项id列表，用作查询检查项标准分，检查项名称
        List<Long> metaColumnIds = pageInfo.getList().stream().map(TbQuestionRecordDO::getMetaColumnId).distinct().collect(Collectors.toList());
        // 检查项数据id列表， 用作查询检查项采集数据
        List<Long> dataColumnIds = pageInfo.getList().stream().map(TbQuestionRecordDO::getDataColumnId).distinct().collect(Collectors.toList());
        // 检查表id列表，用作查询检查表名称
        List<Long> metaTableIds = pageInfo.getList().stream().map(TbQuestionRecordDO::getMetaTableId).distinct().collect(Collectors.toList());
        // 门店id列表，用作查询门店名称，门店编号
        List<String> storeIds = pageInfo.getList().stream().map(TbQuestionRecordDO::getStoreId).distinct().collect(Collectors.toList());
        // 父任务id列表，用作查询任务说明，taskInfo
        List<Long> unifyTaskIds = pageInfo.getList().stream().map(TbQuestionRecordDO::getUnifyTaskId).distinct().collect(Collectors.toList());
        // 用户id列表，用作查询处理人，创建人
        List<String> userIds = Lists.newArrayList();
        for (TbQuestionRecordDO recordDO : pageInfo.getList()) {
            userIds.add(recordDO.getCreateUserId());
            userIds.add(recordDO.getHandleUserId());
            userIds.add(recordDO.getApproveUserId());
            if (StringUtils.isNotBlank(recordDO.getSecondApproveUserId())) {
                userIds.add(recordDO.getSecondApproveUserId());
            }
            if (StringUtils.isNotBlank(recordDO.getThirdApproveUserId())) {
                userIds.add(recordDO.getThirdApproveUserId());
            }
        }
        try {
            // 查询门店任务用来查询抄送人
            List<TaskStoreDO> taskStoreDOList = taskStoreDao.selectQuestionTaskByTaskIds(enterpriseId, unifyTaskIds);
            // 添加抄送人id到待查询用户id列表中
            Map<Long, List<String>> ccUserIdMap = Maps.newHashMap();
            Map<Long, List<String>> handleUserIdMap = Maps.newHashMap();
            Map<Long, List<String>> approveUserIdMap = Maps.newHashMap();
            Map<Long, List<String>> secondApproveUserIdMap = Maps.newHashMap();
            Map<Long, List<String>> thirdApproveUserIdMap = Maps.newHashMap();
            for (TaskStoreDO taskStoreDO : taskStoreDOList) {
                if (StringUtils.isNotBlank(taskStoreDO.getCcUserIds())) {
                    List<String> ccUserIdList = Arrays.stream(StringUtils.split(taskStoreDO.getCcUserIds(), Constants.COMMA)).filter(StringUtils::isNotBlank).collect(Collectors.toList());
                    userIds.addAll(ccUserIdList);
                    ccUserIdMap.put(taskStoreDO.getId(), ccUserIdList);
                }
                Map<String, List<String>> taskNodeNoUserMap = unifyTaskStoreService.getNodePersonByTaskStore(taskStoreDO);
                if (taskNodeNoUserMap.containsKey(UnifyNodeEnum.FIRST_NODE.getCode())) {
                    userIds.addAll(taskNodeNoUserMap.get(UnifyNodeEnum.FIRST_NODE.getCode()));
                    handleUserIdMap.put(taskStoreDO.getId(), taskNodeNoUserMap.get(UnifyNodeEnum.FIRST_NODE.getCode()));
                }
                if (taskNodeNoUserMap.containsKey(UnifyNodeEnum.SECOND_NODE.getCode())) {
                    userIds.addAll(taskNodeNoUserMap.get(UnifyNodeEnum.SECOND_NODE.getCode()));
                    approveUserIdMap.put(taskStoreDO.getId(), taskNodeNoUserMap.get(UnifyNodeEnum.SECOND_NODE.getCode()));
                }
                if (taskNodeNoUserMap.containsKey(UnifyNodeEnum.THIRD_NODE.getCode())) {
                    userIds.addAll(taskNodeNoUserMap.get(UnifyNodeEnum.THIRD_NODE.getCode()));
                    secondApproveUserIdMap.put(taskStoreDO.getId(), taskNodeNoUserMap.get(UnifyNodeEnum.THIRD_NODE.getCode()));
                }
                if (taskNodeNoUserMap.containsKey(UnifyNodeEnum.FOUR_NODE.getCode())) {
                    userIds.addAll(taskNodeNoUserMap.get(UnifyNodeEnum.FOUR_NODE.getCode()));
                    thirdApproveUserIdMap.put(taskStoreDO.getId(), taskNodeNoUserMap.get(UnifyNodeEnum.FOUR_NODE.getCode()));
                }
            }
            // 查询用户
            Map<String, EnterpriseUserDO> userDOMap = enterpriseUserDao.getUserMap(enterpriseId, userIds);
            // 组装抄送人用户名map
            Map<Long, List<String>> ccUserNameMap = taskStoreDOList.stream().collect(Collectors.toMap(TaskStoreDO::getId,
                    taskStoreDO -> CollectionUtils.emptyIfNull(ccUserIdMap.get(taskStoreDO.getId())).stream().filter(userDOMap::containsKey).map(ccUserId -> userDOMap.get(ccUserId).getName()).collect(Collectors.toList())
            ));
            // 组装指派整改人用户名map
            Map<Long, List<String>> handleUserNameMap = taskStoreDOList.stream().collect(Collectors.toMap(TaskStoreDO::getId,
                    taskStoreDO -> CollectionUtils.emptyIfNull(handleUserIdMap.get(taskStoreDO.getId())).stream().filter(u -> userDOMap.get(u) != null).map(userId -> userDOMap.get(userId).getName()).collect(Collectors.toList())
            ));
            // 组装指派一级审批人用户名map
            Map<Long, List<String>> approveUserNameMap = taskStoreDOList.stream().collect(Collectors.toMap(TaskStoreDO::getId,
                    taskStoreDO -> CollectionUtils.emptyIfNull(approveUserIdMap.get(taskStoreDO.getId())).stream().filter(userDOMap::containsKey).map(userId -> userDOMap.get(userId).getName()).collect(Collectors.toList())
            ));
            // 组装指派二级审批人用户名map
            Map<Long, List<String>> secondApproveUserNameMap = taskStoreDOList.stream().collect(Collectors.toMap(TaskStoreDO::getId,
                    taskStoreDO -> CollectionUtils.emptyIfNull(secondApproveUserIdMap.get(taskStoreDO.getId())).stream().filter(userDOMap::containsKey).map(userId -> userDOMap.get(userId).getName()).collect(Collectors.toList())
            ));
            // 组装指派三级审批人用户名map
            Map<Long, List<String>> thirdApproveUserNameMap = taskStoreDOList.stream().collect(Collectors.toMap(TaskStoreDO::getId,
                    taskStoreDO -> CollectionUtils.emptyIfNull(thirdApproveUserIdMap.get(taskStoreDO.getId())).stream().filter(userDOMap::containsKey).map(userId -> userDOMap.get(userId).getName()).collect(Collectors.toList())
            ));
            List<TbMetaStaTableColumnDO> metaStaTableColumnDOList = tbMetaStaTableColumnDao.selectByIds(enterpriseId, metaColumnIds);
            Map<Long, TbMetaStaTableColumnDO> metaStaTableColumnDOMap = metaStaTableColumnDOList.stream().collect(Collectors.toMap(TbMetaStaTableColumnDO::getId, Function.identity()));
            List<TbDataStaTableColumnDO> dataStaTableColumnDOList = tbDataStaTableColumnDao.selectByIds(enterpriseId, dataColumnIds);
            Map<Long, TbDataStaTableColumnDO> dataStaTableColumnDOMap = dataStaTableColumnDOList.stream().collect(Collectors.toMap(TbDataStaTableColumnDO::getId, Function.identity()));
            List<TbMetaTableDO> metaTableDOList = tbMetaTableDao.selectByIds(enterpriseId, metaTableIds);
            Map<Long, TbMetaTableDO> metaTableDOMap = metaTableDOList.stream().collect(Collectors.toMap(TbMetaTableDO::getId, Function.identity()));
            List<StoreDO> storeDoList = storeDao.getByStoreIdList(enterpriseId, storeIds);
            Map<String, StoreDO> storeDOMap = storeDoList.stream().collect(Collectors.toMap(StoreDO::getStoreId, Function.identity()));
            List<TbQuestionRecordExpandDO> expandDOList = questionRecordExpandDao.selectByQuestionRecordIds(enterpriseId, questionRecordIds);
            Map<Long, TbQuestionRecordExpandDO> expandDOMap = expandDOList.stream().collect(Collectors.toMap(TbQuestionRecordExpandDO::getRecordId, Function.identity()));
            List<TbQuestionHistoryVO> questionHistoryVOList = questionHistoryDao.selectLatestHistoryListByRecordIdList(enterpriseId, questionRecordIds, null);
            Map<String, TbQuestionHistoryVO> questionHistoryMap = questionHistoryVOList.stream().collect(Collectors.toMap(e -> e.getRecordId() + Constants.MOSAICS + e.getNodeNo(), Function.identity(), (a, b) -> a));
            // 构建VO数据
            pageVO.setList(pageInfo.getList().stream().map(recordDO -> this.parseDOToSubQuestionExportVO(enterpriseId, recordDO, metaStaTableColumnDOMap,
                            dataStaTableColumnDOMap, metaTableDOMap, storeDOMap,
                            userDOMap, ccUserNameMap, expandDOMap, questionHistoryMap, handleUserNameMap, approveUserNameMap,
                            secondApproveUserNameMap, thirdApproveUserNameMap))
                    .collect(Collectors.toList()));
            return pageVO;
        } catch (Exception e) {
            log.error("question record service export error", e);
            throw new ServiceException(ErrorCodeEnum.DATA_ERROR);
        }
    }


    @Override
    public List<RegionQuestionReportVO> getQuestionReport(String enterpriseId, RegionQuestionReportRequest request, CurrentUser user) {
        List<String> regionIds = request.getRegionIds();
        List<RegionQuestionReportVO> result = new ArrayList<>();
        //校验原始的regionIds
        if (CollUtil.isEmpty(regionIds)) {
            throw new ServiceException(ErrorCodeEnum.VALIDATION_RULES_1060001);
        }
        //是否是查询子节点数据
        if (request.getChildRegion()) {
            List<RegionChildDTO> regionByParentId = regionMapper.getRegionByParentId(enterpriseId, regionIds, Boolean.FALSE);
            regionIds = regionByParentId.stream().map(RegionChildDTO::getId).collect(Collectors.toList());
        }
        //校验重新赋值后的regionIds
        if (CollUtil.isEmpty(regionIds)) {
            return result;
        }
        // 获取区域的路径
        List<RegionPathDTO> regionPathList = regionService.getRegionPathByList(enterpriseId, regionIds);
        Map<String, Future<RegionQuestionReportVO>> idTaskMap = new HashMap<>();

        for (RegionPathDTO region : regionPathList) {
            String regionId = region.getRegionId();
            if(FixedRegionEnum.getExcludeRegionId().contains(regionId)){
                continue;
            }
            idTaskMap.put(regionId,
                    EXECUTOR_SERVICE.submit(() -> getRegionQuestionDate(enterpriseId, region.getRegionPath(), user, request)));
        }
        // 获取结果
        for (RegionPathDTO region : regionPathList) {
            String regionId = region.getRegionId();
            Future<RegionQuestionReportVO> future = idTaskMap.get(regionId);
            try {
                if (future == null){
                    log.info("getQuestionReport future regionId：{} not found",regionId);
                    continue;
                }
                // 获取统计信息
                RegionQuestionReportVO regionQuestionReportVO = future.get();
                regionQuestionReportVO.setRegionId(Long.valueOf(regionId));
                regionQuestionReportVO.setRegionName(region.getRegionName());
                regionQuestionReportVO.setStoreNum(region.getStoreNum());
                result.add(regionQuestionReportVO);
            } catch (Exception e) {
                log.error("工单统计：", e);
            }
        }
        return result;
    }


    /**
     * 查询工单区域数据
     *
     * @param enterpriseId
     * @param fullRegionPath
     * @param user
     * @param request
     * @return
     */
    private RegionQuestionReportVO getRegionQuestionDate(String enterpriseId, String fullRegionPath, CurrentUser user, RegionQuestionReportRequest request) {
        DataSourceHelper.changeToSpecificDataSource(user.getDbName());
        RegionQuestionReportVO regionQuestionReportVO = new RegionQuestionReportVO();
        //获取各个阶段工单数量
        List<QuestionStageDateDTO> questionStageDateDTOS = questionRecordDao.selectQuestionStageDate(enterpriseId, request, fullRegionPath);
        //待整改工单
        Integer toBeRectifiedQuestionCount = Constants.INDEX_ZERO;
        //待审批工单
        Integer approveQuestionCount = Constants.INDEX_ZERO;
        //已经完成工单
        Integer completeQuestionCount = Constants.INDEX_ZERO;
        for (QuestionStageDateDTO questionStageDateDTO : questionStageDateDTOS) {
            if (questionStageDateDTO.getStatus().equals(UnifyNodeEnum.FIRST_NODE.getCode())) {
                toBeRectifiedQuestionCount = questionStageDateDTO.getStageQuestionCount();
            } else if (questionStageDateDTO.getStatus().equals(UnifyNodeEnum.END_NODE.getCode())) {
                completeQuestionCount = questionStageDateDTO.getStageQuestionCount();
            } else {
                approveQuestionCount += questionStageDateDTO.getStageQuestionCount();
            }
        }
        //已整改工单
        Integer rectifiedQuestionCount = questionRecordDao.getRectifiedQuestionCount(enterpriseId, request, fullRegionPath);
        //总的完成时间
        Long questionTotalDuration = questionRecordDao.questionTotalDuration(enterpriseId, request, fullRegionPath);
        //完成工单逾期数
        Integer completeStageOverdueCount = questionRecordDao.completeStageOverdueCount(enterpriseId, request, fullRegionPath);
        //处理阶段逾期数
        Integer handleStageOverdueCount = questionRecordDao.handleStageOverdueCount(enterpriseId, request, fullRegionPath);


        //通过驳回
        QuestionStageDateDTO approveStagePassOrRejectCount = questionRecordDao.approveStagePassOrRejectCount(enterpriseId, request, fullRegionPath);
        //总工单数
        Integer totalQuestionCount = approveQuestionCount + completeQuestionCount + toBeRectifiedQuestionCount;

        regionQuestionReportVO.setToBeRectifiedQuestionCount(toBeRectifiedQuestionCount);
        regionQuestionReportVO.setCompleteQuestionCount(completeQuestionCount);
        regionQuestionReportVO.setApproveQuestionCount(approveQuestionCount);
        regionQuestionReportVO.setTotalQuestionCount(totalQuestionCount);
        regionQuestionReportVO.setRectifiedQuestionCount(rectifiedQuestionCount);
        regionQuestionReportVO.setCompleteTotalDuration(new BigDecimal(questionTotalDuration).divide(new BigDecimal(60), 2, BigDecimal.ROUND_HALF_UP));
        regionQuestionReportVO.setCompleteOverDueCount(completeStageOverdueCount);
        regionQuestionReportVO.setCompleteOverDueRate(NumberFormatUtils.getPercentString(completeStageOverdueCount, completeQuestionCount));
        regionQuestionReportVO.setRectificationStageOverdueCount(handleStageOverdueCount);
        //工单整改率
        String rectificationStageQuestionCorrectionRate = NumberFormatUtils.getPercentString(rectifiedQuestionCount, totalQuestionCount);
        regionQuestionReportVO.setRectificationStageQuestionCorrectionRate(rectificationStageQuestionCorrectionRate);
        //待处理工单逾期率
        String rectificationStageOverdueRate = NumberFormatUtils.getPercentString(handleStageOverdueCount, toBeRectifiedQuestionCount);
        regionQuestionReportVO.setRectificationStageOverdueRate(rectificationStageOverdueRate);
        //已完成逾期率
        String completeOverDueRate = NumberFormatUtils.getPercentString(completeStageOverdueCount, completeQuestionCount);
        regionQuestionReportVO.setQuestionCompleteRate(completeOverDueRate);
        //工单完成率
        String questionCompleteRate = NumberFormatUtils.getPercentString(completeQuestionCount, totalQuestionCount);
        regionQuestionReportVO.setQuestionCompleteRate(questionCompleteRate);
        //完成平均时长
        BigDecimal avgDuration = new BigDecimal(0);
        if (questionTotalDuration != 0 && completeQuestionCount != 0) {
            avgDuration = regionQuestionReportVO.getCompleteTotalDuration().divide(new BigDecimal(completeQuestionCount), 2, BigDecimal.ROUND_HALF_UP);
        }
        regionQuestionReportVO.setCompleteAvgDuration(avgDuration);
        String approvePassRate = NumberFormatUtils.getPercentString(approveStagePassOrRejectCount.getPassCount(), approveStagePassOrRejectCount.getPassCount() + approveStagePassOrRejectCount.getRejectCount());
        regionQuestionReportVO.setApprovePassRate(approvePassRate);
        regionQuestionReportVO.setApprovePassCount(approveStagePassOrRejectCount.getPassCount());
        regionQuestionReportVO.setApproveRejectCount(approveStagePassOrRejectCount.getRejectCount());
        return regionQuestionReportVO;
    }


    //计算百分百


    @Override
    public List<SubQuestionRecordListVO> questionDetailList(String enterpriseId, List<Long> questionParentInfoIds, String userId, String status, Boolean isBatchApprove, String type) {
        if (StringUtils.isBlank(type)) {
            type = QuestionQueryTypeEnum.PENDING.getCode();
        }
        List<TbQuestionParentInfoDO> tbQuestionParentInfoDOS = questionParentInfoDao.selectByIdList(enterpriseId, questionParentInfoIds);
        if (CollectionUtils.isEmpty(tbQuestionParentInfoDOS)) {
            throw new ServiceException(ErrorCodeEnum.TASK_NOT_EXIST);
        }
        //发起人
        List<String> userIds = tbQuestionParentInfoDOS.stream().map(TbQuestionParentInfoDO::getCreateId).collect(Collectors.toList());
        Map<String, String> userNameMap = enterpriseUserDao.getUserNameMap(enterpriseId, userIds);
        List<Long> unifyIds = tbQuestionParentInfoDOS.stream().map(TbQuestionParentInfoDO::getUnifyTaskId).collect(Collectors.toList());
        List<TbQuestionRecordDO> questionRecordDOList = questionRecordDao.questionListByTaskId(enterpriseId, unifyIds, status);
        if (CollectionUtils.isEmpty(questionRecordDOList)) {
            return new ArrayList<>();
        }
        List<Long> taskStoreIdList = questionRecordDOList.stream().map(TbQuestionRecordDO::getTaskStoreId).collect(Collectors.toList());
        List<TaskStoreDO> storeDOList = taskStoreDao.selectQuestionTaskByIds(enterpriseId, taskStoreIdList, null);
        if (CollectionUtils.isEmpty(storeDOList)) {
            return new ArrayList<>();
        }
        //处理人列表
        Map<Long, List<String>> hanUserIdMap = new HashMap<>();
        Map<Long, String> nodeNoMap = new HashMap<>();
        //过滤有权限的任务
        String finalType = type;
        taskStoreIdList = storeDOList.stream().filter(taskStoreDO -> {
            List<String> handleUserIdList = getCurrentHandleUserId(enterpriseId, taskStoreDO);
            if (CollectionUtils.isEmpty(handleUserIdList) && QuestionQueryTypeEnum.PENDING.getCode().equals(finalType)) {
                return false;
            }
            boolean isView = true;
            //不过滤
            if (QuestionQueryTypeEnum.PENDING.getCode().equals(finalType)) {
                isView = handleUserIdList.contains(userId);
            } else if (QuestionQueryTypeEnum.CC.getCode().equals(finalType)) {
                // 抄送人
                List<String> ccUserIds = Lists.newArrayList(StringUtils.split(Optional.ofNullable(taskStoreDO.getCcUserIds())
                        .orElse(Constants.EMPTY_STRING), Constants.COMMA));
                isView = ccUserIds.contains(userId);
            }
            if (isView) {
                hanUserIdMap.put(taskStoreDO.getId(), handleUserIdList);
                nodeNoMap.put(taskStoreDO.getId(), taskStoreDO.getNodeNo());
            }
            return isView;
        }).map(TaskStoreDO::getId).collect(Collectors.toList());

        if (CollectionUtils.isEmpty(taskStoreIdList)) {
            return new ArrayList<>();
        }


        Set<Long> taskStoreIdSet = new HashSet<>(taskStoreIdList);
        Map<Long, List<PersonDTO>> currentHandleUserName = getCurrentHandleUserName(enterpriseId, hanUserIdMap);
        List<Long> metaColumnIdList = questionRecordDOList.stream().map(TbQuestionRecordDO::getMetaColumnId).collect(Collectors.toList());
        List<TbMetaStaTableColumnDO> metaStaTableColumnDOList = tbMetaStaTableColumnDao.selectByIds(enterpriseId, metaColumnIdList);
        Map<Long, String> metaStaTableColumnNameMap = metaStaTableColumnDOList.stream().collect(Collectors.toMap(TbMetaStaTableColumnDO::getId, TbMetaStaTableColumnDO::getColumnName));
        List<Long> metaTableIdList = questionRecordDOList.stream().map(TbQuestionRecordDO::getMetaTableId).collect(Collectors.toList());

        //检查表名称
        List<TbMetaTableDO> tbMetaTableDOList = tbMetaTableDao.selectByIds(enterpriseId, metaTableIdList);
        Map<Long, String> metaTableNameMap = ListUtils.emptyIfNull(tbMetaTableDOList).stream().collect(Collectors.toMap(TbMetaTableDO::getId, TbMetaTableDO::getTableName));

        List<SubQuestionRecordListVO> recordList = new ArrayList<>();
        questionRecordDOList.stream().filter(e -> taskStoreIdSet.contains(e.getTaskStoreId())).forEach(recordDO -> {
            SubQuestionRecordListVO result = SubQuestionRecordListVO.convertToVO(recordDO);
            result.setMetaColumnName(metaStaTableColumnNameMap.get(recordDO.getMetaColumnId()));
            result.setMetaTableName(metaTableNameMap.get(recordDO.getMetaTableId()));
            if (UnifyNodeEnum.END_NODE.getCode().equals(recordDO.getStatus())) {
                result.setOverdue(recordDO.getSubEndTime().before(recordDO.getCompleteTime()));
            }
            result.setParentQuestionId(recordDO.getParentQuestionId());
            result.setCurrentUserList(currentHandleUserName.get(recordDO.getTaskStoreId()));
            result.setHandler(hanUserIdMap.get(recordDO.getTaskStoreId()).contains(userId));
            result.setNodeNo(nodeNoMap.get(recordDO.getTaskStoreId()));
            result.setCreateUserName(userNameMap.getOrDefault(recordDO.getCreateUserId(), ""));
            recordList.add(result);
        });
        //如果是批量审批，查询最新的整改记录
        if (isBatchApprove != null && isBatchApprove) {
            approveOrderDetailList(enterpriseId, recordList);
        }
        return recordList;
    }

    @Override
    public ImportTaskDO subQuestionDetailListExport(String enterpriseId, TbQuestionRecordSearchRequest tbQuestionRecordSearchRequest, CurrentUser user) {
        //判断入参是否全部为空
        if (StringUtils.isBlank(tbQuestionRecordSearchRequest.getRegionId()) && CollectionUtils.isEmpty(tbQuestionRecordSearchRequest.getRegionIds())) {
            //全空就用当前用户全部区域
            List<SelectComponentRegionVO> allRegionList = selectionComponentService.getRegionAndStore(enterpriseId, null, UserHolder.getUser().getUserId(), null).getAllRegionList();
            if (CollectionUtils.isEmpty(allRegionList)){
                throw new ServiceException("当前无记录可导出");
            }
            tbQuestionRecordSearchRequest.setRegionIds(allRegionList.stream().map(SelectComponentRegionVO::getId).collect(Collectors.toList()));
        }
        // 查询导出数量，限流
        String fullRegionPath="";
        if (StringUtils.isNotBlank(tbQuestionRecordSearchRequest.getRegionId())) {
            RegionDO regionById = regionDao.getRegionById(enterpriseId, Long.valueOf(tbQuestionRecordSearchRequest.getRegionId()));
            fullRegionPath=regionById.getFullRegionPath();
        }
        //兼容
        List<String> fullRegionPathList = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(tbQuestionRecordSearchRequest.getRegionIds())) {
            List<RegionDO> regionDO = regionDao.getRegionByRegionIds(enterpriseId, tbQuestionRecordSearchRequest.getRegionIds());
            if (CollectionUtils.isNotEmpty(regionDO)){
                fullRegionPathList = regionDO.stream().map(RegionDO::getFullRegionPath).collect(Collectors.toList());
            }
        }
        Long count = questionRecordDao.countSubQuestionDetailList(enterpriseId, fullRegionPath,fullRegionPathList, tbQuestionRecordSearchRequest);
        if (count == null || count == 0L) {
            throw new ServiceException("当前无记录可导出");
        }
        if (count > Constants.MAX_EXPORT_SIZE) {
            throw new ServiceException("导出数据不能超过" + Constants.MAX_EXPORT_SIZE + "条，请缩小导出范围");
        }
        // 通过枚举获取文件名称
        String fileName = ExportTemplateEnum.getByCode(ImportTaskConstant.SUB_QUESTION_DETAIL);
        // 保存导出任务
        ImportTaskDO importTaskDO = importTaskService.insertExportTask(enterpriseId, fileName, ImportTaskConstant.TASK_QUESTION_REPORT);
        // 构造异步导出参数
        ExportTbQuestionRecordRequest msg = new ExportTbQuestionRecordRequest();
        msg.setEnterpriseId(enterpriseId);
        msg.setRequest(tbQuestionRecordSearchRequest);
        msg.setTotalNum(count);
        msg.setImportTaskDO(importTaskDO);
        msg.setDbName(user.getDbName());
        MsgUniteData msgUniteData = new MsgUniteData();
        msgUniteData.setData(JSONObject.toJSONString(msg));
        msgUniteData.setMsgType(MsgUniteDataTypeEnum.SUB_QUESTION_DETAIL.getCode());
        //分开异步导出
        simpleMessageService.send(JSONObject.toJSONString(msgUniteData), RocketMqTagEnum.EXPORT_IMPORT_MESSAGE);
        return importTaskDO;
    }

    @Override
    public ImportTaskDO regionQuestionReportExport(String enterpriseId, RegionQuestionReportRequest request, CurrentUser user) {
        // 通过枚举获取文件名称
        String fileName = ExportTemplateEnum.getByCode(ImportTaskConstant.REGION_STORE_QUESTION_REPORT);
        // 保存导出任务
        ImportTaskDO importTaskDO = importTaskService.insertExportTask(enterpriseId, fileName, ImportTaskConstant.TASK_QUESTION_REPORT);
        // 构造异步导出参数
        ExportRegionQuestionReportRequest msg = new ExportRegionQuestionReportRequest();
        msg.setEnterpriseId(enterpriseId);
        msg.setRequest(request);
        msg.setImportTaskDO(importTaskDO);
        msg.setDbName(user.getDbName());
        MsgUniteData msgUniteData = new MsgUniteData();
        msgUniteData.setData(JSONObject.toJSONString(msg));
        msgUniteData.setMsgType(MsgUniteDataTypeEnum.REGION_STORE_QUESTION_REPORT.getCode());
        //分开异步导出
        simpleMessageService.send(JSONObject.toJSONString(msgUniteData), RocketMqTagEnum.EXPORT_IMPORT_MESSAGE);
        return importTaskDO;
    }

    @Override
    public PageVO<SubQuestionRecordListVO> recordList(String enterpriseId, TbQuestionRecordSearchRequest searchRequest,
                                                      PageRequest pageRequest, CurrentUser user) {
        searchRequest.setCurrentUserId(user.getUserId());
        TbQuestionRecordSearchDTO tbQuestionRecordSearchDTO = this.parseSearchRequestToDTO(enterpriseId, searchRequest);
        //如果没有管辖区域，返回空
        if (!Boolean.TRUE.equals(tbQuestionRecordSearchDTO.getIsAdmin()) && CollectionUtils.isEmpty(tbQuestionRecordSearchDTO.getFullRegionPathList())&& CollectionUtils.isEmpty(tbQuestionRecordSearchDTO.getStoreIds())){
            return new PageVO<>(Collections.emptyList());
        }
        // 查询问题工单列表（分页）
        PageInfo<TbQuestionRecordDO> pageInfo = questionRecordDao.selectQuestionRecordPage(tbQuestionRecordSearchDTO,
                pageRequest.getPageNumber(), pageRequest.getPageSize());
        // 构造分页结果，没查到数据直接返回空分页结果
        PageVO<SubQuestionRecordListVO> pageVO = new PageVO<>();
        pageVO.setTotal(pageInfo.getTotal());
        pageVO.setPageNum(pageInfo.getPageNum());
        pageVO.setPageSize(pageInfo.getPageSize());
        if (CollectionUtils.isEmpty(pageInfo.getList())) {
            pageVO.setList(Lists.newArrayList());
            return pageVO;
        }
        // 分页中获取DO列表，用作构造VO结果
        List<TbQuestionRecordDO> recordDOList = pageInfo.getList();
        // 检查项id列表，用作查询检查项标准分，检查项名称
        List<Long> metaColumnIds = recordDOList.stream().map(TbQuestionRecordDO::getMetaColumnId).distinct().collect(Collectors.toList());
        // 门店id列表，用作查询门店名称，门店编号
        List<String> storeIds = recordDOList.stream().map(TbQuestionRecordDO::getStoreId).distinct().collect(Collectors.toList());
        // 用户id列表，用作查询处理人
        List<String> userIds = recordDOList.stream().map(TbQuestionRecordDO::getHandleUserId).collect(Collectors.toList());
        userIds.addAll(recordDOList.stream().map(TbQuestionRecordDO::getApproveUserId).collect(Collectors.toList()));
        userIds.addAll(recordDOList.stream().map(TbQuestionRecordDO::getCreateUserId).collect(Collectors.toList()));
        //查询条件审批人，不区分节点
        String approveId = searchRequest.getApproveUserId();
        if (StringUtils.isNotBlank(searchRequest.getSecondApproveUserId())) {
            approveId = searchRequest.getSecondApproveUserId();
        }
        if (StringUtils.isNotBlank(searchRequest.getThirdApproveUserId())) {
            approveId = searchRequest.getThirdApproveUserId();
        }
        if (StringUtils.isNotBlank(approveId)) {
            userIds.add(approveId);
        }
        // 父任务id列表，用作查询任务说明，taskInfo
        List<Long> unifyTaskIds = recordDOList.stream().map(TbQuestionRecordDO::getUnifyTaskId).distinct().collect(Collectors.toList());
        try {
            // 查询门店任务用来查询抄送人
            List<TaskStoreDO> taskStoreDOList = taskStoreDao.selectQuestionTaskByTaskIds(enterpriseId, unifyTaskIds);
            //处理人列表
            Map<Long, List<String>> hanUserIdMap = new HashMap<>();
            //过滤有权限的任务
            taskStoreDOList.forEach(taskStoreDO -> {
                List<String> handleUserIdList = getCurrentHandleUserId(enterpriseId, taskStoreDO);
                hanUserIdMap.put(taskStoreDO.getId(), handleUserIdList);

            });
            //当前节点处理人
            Map<Long, List<PersonDTO>> currentHandleUserNameMap = getCurrentHandleUserName(enterpriseId, hanUserIdMap);
            // 查询用户
            Map<String, EnterpriseUserDO> userDOMap = enterpriseUserDao.getUserMap(enterpriseId, userIds.stream().distinct().collect(Collectors.toList()));
            List<TbMetaStaTableColumnDO> metaStaTableColumnDOList = tbMetaStaTableColumnDao.selectByIds(enterpriseId, metaColumnIds);
            Map<Long, TbMetaStaTableColumnDO> metaStaTableColumnDOMap = metaStaTableColumnDOList.stream().collect(Collectors.toMap(TbMetaStaTableColumnDO::getId, Function.identity()));
            List<StoreDO> storeDoList = storeDao.getByStoreIdList(enterpriseId, storeIds);
            Map<String, StoreDO> storeDOMap = storeDoList.stream().collect(Collectors.toMap(StoreDO::getStoreId, Function.identity()));
            // 构建VO数据
            String finalApproveId = approveId;
            pageVO.setList(recordDOList.stream().map(recordDO -> this.parseDOToQuestionRecordListVO(recordDO, metaStaTableColumnDOMap,
                    storeDOMap, userDOMap, finalApproveId, currentHandleUserNameMap, hanUserIdMap, user.getUserId())).collect(Collectors.toList()));
        } catch (Exception e) {
            log.error("question record service listForMobile error", e);
            throw new ServiceException(ErrorCodeEnum.DATA_ERROR);
        }
        return pageVO;
    }

    @Override
    public SubQuestionRecordListVO getRecordByDataColumnId(String enterpriseId, Long dataColumnId) {
        SubQuestionRecordListVO recordListVO = new SubQuestionRecordListVO();
        TbQuestionRecordDO recordDO = questionRecordDao.getByDataColumnId(enterpriseId, dataColumnId, Boolean.FALSE);
        TbDataStaTableColumnDO dataStaTableColumnDO = tbDataStaTableColumnDao.selectById(enterpriseId, dataColumnId);
        if (recordDO != null && Objects.nonNull(dataStaTableColumnDO)) {
            recordListVO.setUnifyTaskId(dataStaTableColumnDO.getTaskQuestionId());
            recordListVO.setStoreId(dataStaTableColumnDO.getStoreId());
            recordListVO.setLoopCount(Constants.LONG_ONE);
            recordListVO.setId(recordDO.getId());
            recordListVO.setParentQuestionId(recordDO.getParentQuestionId());
            recordListVO.setTaskName(recordDO.getTaskName());
            recordListVO.setTaskStoreId(recordDO.getTaskStoreId());
            recordListVO.setTaskDesc(recordDO.getTaskDesc());
            // 检查项id不等于0，获取检查项数据
            recordListVO.setMetaColumnId(recordDO.getMetaColumnId());
            recordListVO.setSubEndTime(recordDO.getSubEndTime());
            recordListVO.setSubBeginTime(recordDO.getSubBeginTime());
            recordListVO.setLoopCount(recordDO.getLoopCount());
            recordListVO.setStoreId(recordDO.getStoreId());
            recordListVO.setUnifyTaskId(recordDO.getUnifyTaskId());
            // 判断逾期
            recordListVO.setStatus(recordDO.getStatus());
            // 用户
            recordListVO.setCreateUserId(recordDO.getCreateUserId());

            recordListVO.setHandleUserId(recordDO.getHandleUserId());
            recordListVO.setQuestionType(recordDO.getQuestionType());
        }
        return recordListVO;
    }

    @Override
    public List<SubQuestionRecordListVO> questionPatrolList(String enterpriseId, Long businessId, CurrentUser user) {
        //查询工单ID
        List<TbDataStaTableColumnDO> tbDataStaTableColumnDOS = dataStaTableColumnMapper.selectByBusinessId(enterpriseId, businessId, PATROL_STORE);
        if (CollectionUtils.isEmpty(tbDataStaTableColumnDOS)) {
            return Collections.emptyList();
        }
        //父任务IDs 去重
        List<Long> unifyParentIds = tbDataStaTableColumnDOS.stream().map(TbDataStaTableColumnDO::getTaskQuestionId).distinct().collect(Collectors.toList());
        //父任务定义表数据
        List<TbQuestionParentInfoDO> tbQuestionParentInfoDOS = questionParentInfoDao.selectByUnifyTaskIds(enterpriseId, unifyParentIds);
        if (CollectionUtils.isEmpty(tbQuestionParentInfoDOS)) {
            return Collections.emptyList();
        }
        List<Long> questionParentInfoIdList = tbQuestionParentInfoDOS.stream().map(TbQuestionParentInfoDO::getId).distinct().collect(Collectors.toList());
        return this.questionDetailList(enterpriseId, questionParentInfoIdList, user.getUserId(), null, Boolean.FALSE, QuestionQueryTypeEnum.ALL.getCode());
    }

    @Override
    public List<TbQuestionRecordDO> getSubQuestionByParentUnifyTaskId(String enterpriseId, Long unifyTaskId) {
        return questionRecordDao.getSubQuestionByParentUnifyTaskId(enterpriseId, unifyTaskId);
    }

    private Map<Long, List<PersonDTO>> getCurrentHandleUserName(String enterpriseId, Map<Long, List<String>> hanUserIdMap) {
        Set<String> userIdSet = new HashSet<>();
        for (Map.Entry<Long, List<String>> m : hanUserIdMap.entrySet()) {
            userIdSet.addAll(m.getValue());
        }
        Map<String, String> enterpriseUserMap = enterpriseUserDao.getUserNameMap(enterpriseId, new ArrayList<>(userIdSet));
        Map<Long, List<PersonDTO>> personMap = new HashMap<>();
        for (Map.Entry<Long, List<String>> m : hanUserIdMap.entrySet()) {
            List<PersonDTO> personList = new ArrayList<>();
            for (String userId : m.getValue()) {
                PersonDTO personDTO = new PersonDTO();
                personDTO.setUserId(userId);
                personDTO.setUserName(enterpriseUserMap.get(userId));
                personList.add(personDTO);
            }
            personMap.put(m.getKey(), personList);
        }
        return personMap;
    }

    private List<String> getCurrentHandleUserId(String enterpriseId, TaskStoreDO taskStoreDO) {
        // 处理人，如果是待处理状态 在子任务查询处理人。其他状态可以在taskStore查询
        List<String> handleUserIds = null;
        if (UnifyNodeEnum.FIRST_NODE.getCode().equals(taskStoreDO.getNodeNo())) {
            List<PersonNodeNoDTO> personNodeNoDTOList = taskSubMapper.selectUserIdByLoopCountAndStoreIdList(enterpriseId, taskStoreDO.getUnifyTaskId(),
                    Lists.newArrayList(taskStoreDO.getStoreId()), taskStoreDO.getLoopCount(), taskStoreDO.getNodeNo());
            handleUserIds = personNodeNoDTOList.stream().map(PersonNodeNoDTO::getUserId).collect(Collectors.toList());
        } else {
            // 获取门店任务的extendInfo字段，后面用作获取处理人和审批人
            JSONObject extendInfo = JSONObject.parseObject(taskStoreDO.getExtendInfo());
            // taskStore中extendInfo获取第一个节点，处理人
            handleUserIds = Lists.newArrayList(StringUtils.split(Optional.ofNullable(extendInfo.getString(taskStoreDO.getNodeNo()))
                    .orElse(Constants.EMPTY_STRING), Constants.COMMA));
        }
        return handleUserIds;
    }

    /**
     * tbQuestionRecordDO转QuestionRecordListVO
     *
     * @param tbQuestionRecordDO
     * @return
     */
    QuestionRecordListVO convertTbQuestionRecordDO(TbQuestionRecordDO tbQuestionRecordDO) {
        QuestionRecordListVO questionRecordListVO = new QuestionRecordListVO();
        questionRecordListVO.setQuestionType(tbQuestionRecordDO.getQuestionType());
        questionRecordListVO.setApproveRemark(tbQuestionRecordDO.getApproveRemark());
        questionRecordListVO.setApproveTime(tbQuestionRecordDO.getApproveTime());
        questionRecordListVO.setApproveUserId(tbQuestionRecordDO.getApproveUserId());
        questionRecordListVO.setApproveUserName(tbQuestionRecordDO.getApproveUserName());
        questionRecordListVO.setApproveActionKey(tbQuestionRecordDO.getApproveActionKey());
        questionRecordListVO.setAttachUrl(tbQuestionRecordDO.getAttachUrl());
        questionRecordListVO.setCompleteTime(tbQuestionRecordDO.getCompleteTime());
        questionRecordListVO.setCreateTime(tbQuestionRecordDO.getCreateTime());
        questionRecordListVO.setCreateType(tbQuestionRecordDO.getCreateType());
        questionRecordListVO.setCreateUserId(tbQuestionRecordDO.getCreateUserId());
        questionRecordListVO.setDataColumnId(tbQuestionRecordDO.getDataColumnId());
        questionRecordListVO.setDeleted(tbQuestionRecordDO.getDeleted());
        questionRecordListVO.setHandleActionKey(tbQuestionRecordDO.getHandleActionKey());
        questionRecordListVO.setHandleRemark(tbQuestionRecordDO.getHandleRemark());
        questionRecordListVO.setHandleTime(tbQuestionRecordDO.getHandleTime());
        questionRecordListVO.setHandleUserId(tbQuestionRecordDO.getHandleUserId());
        questionRecordListVO.setHandleUserName(tbQuestionRecordDO.getHandleUserName());
        questionRecordListVO.setId(tbQuestionRecordDO.getId());
        questionRecordListVO.setLearnFirst(tbQuestionRecordDO.getLearnFirst());
        questionRecordListVO.setMetaColumnId(tbQuestionRecordDO.getMetaColumnId());
        questionRecordListVO.setMetaTableId(tbQuestionRecordDO.getMetaTableId());
        questionRecordListVO.setRegionId(tbQuestionRecordDO.getRegionId());
        questionRecordListVO.setRegionPath(tbQuestionRecordDO.getRegionPath());
        questionRecordListVO.setSecondApproveActionKey(tbQuestionRecordDO.getSecondApproveActionKey());
        questionRecordListVO.setSecondApproveUserId(tbQuestionRecordDO.getSecondApproveUserId());
        questionRecordListVO.setStatus(tbQuestionRecordDO.getStatus());
        questionRecordListVO.setStoreId(tbQuestionRecordDO.getStoreId());
        questionRecordListVO.setStoreName(tbQuestionRecordDO.getStoreName());
        questionRecordListVO.setSubBeginTime(tbQuestionRecordDO.getSubBeginTime());
        questionRecordListVO.setSubEndTime(tbQuestionRecordDO.getSubEndTime());
        questionRecordListVO.setTaskName(tbQuestionRecordDO.getTaskName());
        questionRecordListVO.setTaskStoreId(tbQuestionRecordDO.getTaskStoreId());
        questionRecordListVO.setThirdApproveActionKey(tbQuestionRecordDO.getThirdApproveActionKey());
        questionRecordListVO.setThirdApproveUserId(tbQuestionRecordDO.getThirdApproveUserId());
        questionRecordListVO.setUnifyTaskId(tbQuestionRecordDO.getUnifyTaskId());
        questionRecordListVO.setUpdateTime(tbQuestionRecordDO.getUpdateTime());
        return questionRecordListVO;
    }

    /**
     * 详情Do——详情VO
     *
     * @param recordDO
     * @param metaTableDO
     * @param metaStaTableColumnDO
     * @param dataStaTableColumnDO
     * @param storeDO
     * @param regionDO
     * @param taskParentDO
     * @param userDOMap
     * @param handleUserIds
     * @param approveUserIds
     * @param secondApproveUserIds
     * @param thirdApproveUserIds
     * @param ccUserIds
     * @param nodeNo
     * @return
     */
    private QuestionRecordDetailVO convertTbQuestionRecordDetailDO(TbQuestionRecordDO recordDO, TbMetaTableDO metaTableDO, TbMetaStaTableColumnDO metaStaTableColumnDO,
                                                                   TbDataStaTableColumnDO dataStaTableColumnDO, StoreDO storeDO, RegionDO regionDO,
                                                                   TaskParentDO taskParentDO, Map<String, PersonBasicDTO> userDOMap,
                                                                   List<String> handleUserIds, List<String> approveUserIds,
                                                                   List<String> secondApproveUserIds, List<String> thirdApproveUserIds,
                                                                   List<String> ccUserIds, String nodeNo) {
        QuestionRecordDetailVO detailVO = new QuestionRecordDetailVO();
        detailVO.setId(recordDO.getId());
        detailVO.setTaskName(recordDO.getTaskName());
        detailVO.setStatus(nodeNo);
        detailVO.setCreateType(recordDO.getCreateType());
        detailVO.setLearnFirst(recordDO.getLearnFirst());
        detailVO.setAttachUrl(recordDO.getAttachUrl());
        detailVO.setCreateTime(recordDO.getCreateTime());
        detailVO.setHandlerEndTime(recordDO.getSubEndTime());
        detailVO.setTaskStoreId(recordDO.getTaskStoreId());
        // 任务说明，图片，视频
        detailVO.setUnifyTaskId(recordDO.getUnifyTaskId());
        if (Objects.nonNull(taskParentDO)) {
            detailVO.setTaskDesc(taskParentDO.getTaskDesc());
            detailVO.setTaskInfo(taskParentDO.getTaskInfo());
        }
        // 检查项id不等于0，获取检查项数据
        detailVO.setMetaColumnId(recordDO.getMetaColumnId());
        if (!Constants.LONG_ZERO.equals(recordDO.getMetaColumnId()) && Objects.nonNull(metaStaTableColumnDO)) {
            detailVO.setMetaColumnName(metaStaTableColumnDO.getColumnName());
            detailVO.setSupportScore(metaStaTableColumnDO.getSupportScore());
            detailVO.setMetaColumnDescription(metaStaTableColumnDO.getDescription());
            // 开启高级检查表，显示红线项
            if (Objects.nonNull(metaTableDO) && Constants.INDEX_ONE.equals(metaTableDO.getTableProperty())) {
                detailVO.setMetaColumnLevel(metaStaTableColumnDO.getLevel());
            }
        }
        if (!Constants.LONG_ZERO.equals(recordDO.getDataColumnId()) && Objects.nonNull(dataStaTableColumnDO)) {
            BigDecimal checkScore = Objects.nonNull(dataStaTableColumnDO.getCheckScore()) ? dataStaTableColumnDO.getCheckScore().multiply(dataStaTableColumnDO.getScoreTimes()).setScale(Constants.SCALE, RoundingMode.HALF_UP) : new BigDecimal(Constants.ZERO_STR);
            detailVO.setCheckScore(checkScore);
            BigDecimal rewardPenaltMoney = Objects.nonNull(dataStaTableColumnDO.getCheckScore()) ? dataStaTableColumnDO.getRewardPenaltMoney().multiply(dataStaTableColumnDO.getAwardTimes()).setScale(Constants.SCALE, RoundingMode.HALF_UP) : new BigDecimal(Constants.ZERO_STR);
            detailVO.setRewardPenaltMoney(rewardPenaltMoney);
            detailVO.setDataColumnId(recordDO.getDataColumnId());
            detailVO.setCheckResultReason(dataStaTableColumnDO.getCheckResultReason());
        }
        // 门店信息
        if (Objects.nonNull(storeDO)) {
            detailVO.setStoreId(storeDO.getStoreId());
            detailVO.setStoreName(storeDO.getStoreName());
            detailVO.setStoreNum(storeDO.getStoreNum());
        }
        // 区域信息
        if (Objects.nonNull(regionDO)) {
            detailVO.setRegionId(regionDO.getId());
            detailVO.setRegionPath(regionDO.getRegionPath());
            detailVO.setRegionName(regionDO.getName());
        }
        // 逾期
        detailVO.setIsOverdue(this.checkOverdue(recordDO));
        // 人员信息
        detailVO.setHandleUserId(recordDO.getHandleUserId());
        PersonBasicDTO handleUser = userDOMap.get(recordDO.getHandleUserId());
        if (Objects.nonNull(handleUser)) {
            detailVO.setHandleUserName(handleUser.getUserName());
        }
        detailVO.setApproveUserId(recordDO.getApproveUserId());
        PersonBasicDTO approveUser = userDOMap.get(recordDO.getApproveUserId());
        if (Objects.nonNull(approveUser)) {
            detailVO.setApproveUserName(approveUser.getUserName());
        }
        detailVO.setCreateUserId(recordDO.getCreateUserId());
        PersonBasicDTO createUser = userDOMap.get(recordDO.getCreateUserId());
        if (Objects.nonNull(createUser)) {
            detailVO.setCreateUserName(createUser.getUserName());
        } else if (Constants.AI.equals(recordDO.getCreateUserId())) {
            detailVO.setCreateUserName(Constants.AI);
        }
        detailVO.setCcUsers(ccUserIds.stream().map(userDOMap::get).collect(Collectors.toList()));
        detailVO.setHandleUsers(handleUserIds.stream().map(userDOMap::get).collect(Collectors.toList()));
        detailVO.setApproveUsers(approveUserIds.stream().map(userDOMap::get).filter(Objects::nonNull).collect(Collectors.toList()));
        detailVO.setSecondApproveUsers(secondApproveUserIds.stream().map(userDOMap::get).collect(Collectors.toList()));
        detailVO.setThirdApproveUsers(thirdApproveUserIds.stream().map(userDOMap::get).collect(Collectors.toList()));
        if (StringUtils.isNotBlank(recordDO.getSecondApproveUserId())) {
            detailVO.setSecondApproveUserId(recordDO.getSecondApproveUserId());
            PersonBasicDTO secondPeople = userDOMap.get(recordDO.getSecondApproveUserId());
            if (Objects.nonNull(secondPeople)) {
                detailVO.setSecondApproveUserName(secondPeople.getUserName());
            }
        }
        if (StringUtils.isNotBlank(recordDO.getThirdApproveUserId())) {
            detailVO.setThirdApproveUserId(recordDO.getThirdApproveUserId());
            PersonBasicDTO thirdPeople = userDOMap.get(recordDO.getThirdApproveUserId());
            if (Objects.nonNull(thirdPeople)) {
                detailVO.setThirdApproveUserName(thirdPeople.getUserName());
            }
        }
        // 根据节点判断是否是处理人
        return detailVO;
    }

    /**
     * 开发平台
     * user转PersonBasicDTO
     *
     * @param userDO EnterpriseUserDO
     * @return PersonDTO
     */
    private PersonBasicDTO parseUserToPersonBasicDTO(EnterpriseUserDO userDO) {
        PersonBasicDTO personBasicDTO = new PersonBasicDTO();
        personBasicDTO.setUserId(userDO.getUserId());
        personBasicDTO.setUserName(userDO.getName());
        personBasicDTO.setAvatar(userDO.getAvatar());
        return personBasicDTO;
    }


    private void approveOrderDetailList(String enterpriseId, List<SubQuestionRecordListVO> list) {
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        List<Long> recordId = list.stream().map(SubQuestionRecordListVO::getId).collect(Collectors.toList());
        List<TbQuestionHistoryVO> historyVOList = questionHistoryDao.selectLatestHistoryListByRecordIdList(enterpriseId, recordId, UnifyNodeEnum.FIRST_NODE.getCode());
        Set<String> userIdSet = historyVOList.stream().map(TbQuestionHistoryVO::getOperateUserId).collect(Collectors.toSet());
        Map<String, EnterpriseUserDO> nameMap = enterpriseUserDao.getUserMap(enterpriseId, new ArrayList<>(userIdSet));
        historyVOList.forEach(e -> {
            EnterpriseUserDO userDO = nameMap.get(e.getOperateUserId());
            if (userDO != null) {
                e.setAvatar(userDO.getAvatar());
                e.setOperateUserName(userDO.getName());
            } else if (Constants.AI.equals(e.getOperateUserId())) {
                e.setOperateUserName(Constants.AI);
            }
        });
        Map<Long, TbQuestionHistoryVO> questionHistoryMap = historyVOList.stream().collect(Collectors.toMap(TbQuestionHistoryVO::getRecordId, Function.identity(), (a, b) -> a));
        list.forEach(recordListVO -> recordListVO.setHandleHistory(questionHistoryMap.get(recordListVO.getId())));
    }


    private SubQuestionRecordListVO parseDOToQuestionRecordListVO(TbQuestionRecordDO recordDO, Map<Long, TbMetaStaTableColumnDO> metaStaTableColumnDOMap,
                                                                  Map<String, StoreDO> storeMap, Map<String, EnterpriseUserDO> userDOMap,
                                                                  String approveId, Map<Long, List<PersonDTO>> currentHandleUserNameMap,
                                                                  Map<Long, List<String>> hanUserIdMap,
                                                                  String userId) {
        SubQuestionRecordListVO recordListVO = new SubQuestionRecordListVO();
        recordListVO.setId(recordDO.getId());
        recordListVO.setTaskName(recordDO.getTaskName());
        recordListVO.setTaskStoreId(recordDO.getTaskStoreId());
        recordListVO.setTaskDesc(recordDO.getTaskDesc());
        // 检查项id不等于0，获取检查项数据
        recordListVO.setMetaColumnId(recordDO.getMetaColumnId());
        recordListVO.setSubEndTime(recordDO.getSubEndTime());
        recordListVO.setSubBeginTime(recordDO.getSubBeginTime());
        recordListVO.setLoopCount(recordDO.getLoopCount());
        TbMetaStaTableColumnDO metaStaTableColumnDO = metaStaTableColumnDOMap.get(recordDO.getMetaColumnId());
        if (Objects.nonNull(metaStaTableColumnDO)) {
            recordListVO.setMetaColumnName(metaStaTableColumnDO.getColumnName());
            recordListVO.setIsAiCheck(metaStaTableColumnDO.getIsAiCheck());
        }
        // 门店信息
        StoreDO storeDO = storeMap.get(recordDO.getStoreId());
        if (Objects.nonNull(storeDO)) {
            recordListVO.setStoreId(storeDO.getStoreId());
            recordListVO.setStoreName(storeDO.getStoreName());
        }
        recordListVO.setUnifyTaskId(recordDO.getUnifyTaskId());
        // 判断逾期
        recordListVO.setOverdue(this.checkOverdue(recordDO));
        recordListVO.setStatus(recordDO.getStatus());
        // 用户
        recordListVO.setCreateUserId(recordDO.getCreateUserId());
        EnterpriseUserDO createUser = userDOMap.get(recordDO.getCreateUserId());
        if (Objects.nonNull(createUser)) {
            recordListVO.setCreateUserName(createUser.getName());
        }
        recordListVO.setHandleUserId(recordDO.getHandleUserId());
        recordListVO.setQuestionType(recordDO.getQuestionType());
        EnterpriseUserDO handleUser = userDOMap.get(recordDO.getHandleUserId());
        if (Objects.nonNull(handleUser)) {
            recordListVO.setHandleUserName(handleUser.getName());
        }
        recordListVO.setApproveUserId(recordDO.getApproveUserId());
        EnterpriseUserDO approveUser = userDOMap.get(recordDO.getApproveUserId());
        if (Objects.nonNull(approveUser)) {
            recordListVO.setApproveUserName(approveUser.getName());
        }
        if (StringUtils.isNotBlank(approveId)) {
            recordListVO.setApproveUserId(approveId);
            EnterpriseUserDO approveUserDO = userDOMap.get(approveId);
            if (Objects.nonNull(approveUserDO)) {
                recordListVO.setApproveUserName(approveUserDO.getName());
            }
        }
        List<String> handleIdList = hanUserIdMap.get(recordDO.getTaskStoreId());
        boolean handler = false;
        if (CollectionUtils.isNotEmpty(handleIdList)) {
            handler = handleIdList.contains(userId);
        }
        recordListVO.setHandler(handler);
        recordListVO.setCurrentUserList(currentHandleUserNameMap.get(recordDO.getTaskStoreId()));
        return recordListVO;
    }
}