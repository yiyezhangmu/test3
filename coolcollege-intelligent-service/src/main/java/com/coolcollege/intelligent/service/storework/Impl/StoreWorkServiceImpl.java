package com.coolcollege.intelligent.service.storework.Impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.constant.RedisConstant;
import com.coolcollege.intelligent.common.constant.UnifyTaskConstant;
import com.coolcollege.intelligent.common.datasource.DynamicDataSourceContextHolder;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.enums.PersonTypeEnum;
import com.coolcollege.intelligent.common.enums.meta.MetaColumnTypeEnum;
import com.coolcollege.intelligent.common.enums.meta.MetaTablePropertyEnum;
import com.coolcollege.intelligent.common.enums.role.AuthRoleEnum;
import com.coolcollege.intelligent.common.enums.storework.*;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.common.util.DateUtil;
import com.coolcollege.intelligent.common.util.DateUtils;
import com.coolcollege.intelligent.common.util.NumberFormatUtils;
import com.coolcollege.intelligent.common.util.TableInfoLabelUtil;
import com.coolcollege.intelligent.dao.authentication.UserAuthMappingMapper;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseConfigMapper;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseSettingMapper;
import com.coolcollege.intelligent.dao.enterprise.SysDepartmentMapper;
import com.coolcollege.intelligent.dao.enterprise.dao.EnterpriseUserDao;
import com.coolcollege.intelligent.dao.metatable.TbMetaDefTableColumnMapper;
import com.coolcollege.intelligent.dao.metatable.TbMetaStaTableColumnMapper;
import com.coolcollege.intelligent.dao.metatable.TbMetaTableMapper;
import com.coolcollege.intelligent.dao.metatable.dao.TbMetaStaTableColumnDao;
import com.coolcollege.intelligent.dao.region.RegionMapper;
import com.coolcollege.intelligent.dao.store.StoreGroupMappingMapper;
import com.coolcollege.intelligent.dao.store.StoreMapper;
import com.coolcollege.intelligent.dao.storework.dao.*;
import com.coolcollege.intelligent.dao.system.SysRoleMapper;
import com.coolcollege.intelligent.dao.usergroup.dao.EnterpriseUserGroupDao;
import com.coolcollege.intelligent.dao.usergroup.dao.EnterpriseUserGroupMappingDao;
import com.coolcollege.intelligent.dto.EnterpriseStoreWorkSettingsDTO;
import com.coolcollege.intelligent.model.ai.AIConfigDTO;
import com.coolcollege.intelligent.model.authentication.UserAuthMappingDO;
import com.coolcollege.intelligent.model.coolrelation.vo.CoolCourseVO;
import com.coolcollege.intelligent.model.department.DeptNode;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseSettingDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseUserDO;
import com.coolcollege.intelligent.model.enums.AIBusinessModuleEnum;
import com.coolcollege.intelligent.model.enums.DingMsgEnum;
import com.coolcollege.intelligent.model.enums.StoreWorkDateRangeEnum;
import com.coolcollege.intelligent.model.enums.UserAuthMappingTypeEnum;
import com.coolcollege.intelligent.model.metatable.TbMetaDefTableColumnDO;
import com.coolcollege.intelligent.model.metatable.TbMetaStaTableColumnDO;
import com.coolcollege.intelligent.model.metatable.TbMetaTableDO;
import com.coolcollege.intelligent.model.region.RegionDO;
import com.coolcollege.intelligent.model.region.dto.AuthStoreUserDTO;
import com.coolcollege.intelligent.model.region.dto.RegionPathDTO;
import com.coolcollege.intelligent.model.sop.vo.TaskSopVO;
import com.coolcollege.intelligent.model.store.StoreDO;
import com.coolcollege.intelligent.model.store.StoreGroupMappingDO;
import com.coolcollege.intelligent.model.store.dto.StoreAreaDTO;
import com.coolcollege.intelligent.model.storework.*;
import com.coolcollege.intelligent.model.storework.dto.*;
import com.coolcollege.intelligent.model.storework.request.*;
import com.coolcollege.intelligent.model.storework.vo.*;
import com.coolcollege.intelligent.model.system.SysRoleDO;
import com.coolcollege.intelligent.model.usergroup.EnterpriseUserGroupDO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.model.userholder.UserHolder;
import com.coolcollege.intelligent.producer.SimpleMessageService;
import com.coolcollege.intelligent.rpc.config.EnterpriseSettingRpcService;
import com.coolcollege.intelligent.service.enterprise.EnterpriseService;
import com.coolcollege.intelligent.service.metatable.TbMetaTableService;
import com.coolcollege.intelligent.service.region.RegionService;
import com.coolcollege.intelligent.service.sop.TaskSopService;
import com.coolcollege.intelligent.service.store.StoreService;
import com.coolcollege.intelligent.service.storework.StoreWorkRangeService;
import com.coolcollege.intelligent.service.storework.StoreWorkRecordService;
import com.coolcollege.intelligent.service.storework.StoreWorkService;
import com.coolcollege.intelligent.service.storework.StoreWorkTableMappingService;
import com.coolcollege.intelligent.service.system.SysRoleService;
import com.coolcollege.intelligent.service.task.JmsTaskService;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.coolcollege.intelligent.util.patrolStore.TableTypeUtil;
import com.coolstore.base.enums.AppTypeEnum;
import com.coolstore.base.enums.RocketMqTagEnum;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.coolcollege.intelligent.common.util.MD5Util;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

;

/**
 * @Author suzhuhong
 * @Date 2022/9/8 15:22
 * @Version 1.0
 */
@Service
@Slf4j
public class StoreWorkServiceImpl implements StoreWorkService {

    @Resource
    private EnterpriseSettingRpcService enterpriseSettingRpcService;
    @Resource
    SwStoreWorkDao swStoreWorkDao;
    @Resource
    SwStoreWorkRangeDao swStoreWorkRangeDao;

    @Lazy
    @Resource
    RegionService regionService;
    @Resource
    StoreService storeService;
    @Resource
    SwStoreWorkTableMappingDao swStoreWorkTableMappingDao;
    @Resource
    StoreMapper storeMapper;
    @Resource
    TbMetaTableMapper tbMetaTableMapper;
    @Resource
    TbMetaStaTableColumnMapper tbMetaStaTableColumnMapper;
    @Resource
    TbMetaDefTableColumnMapper tbMetaDefTableColumnMapper;
    @Resource
    StoreWorkTableMappingService storeWorkTableMappingService;
    @Resource
    SwStoreWorkDataTableDao swStoreWorkDataTableDao;
    @Resource
    SwStoreWorkDataTableColumnDao swStoreWorkDataTableColumnDao;
    @Resource
    SwStoreWorkRecordDao swStoreWorkRecordDao;
    @Resource
    private EnterpriseUserDao enterpriseUserDao;
    @Resource
    private SysRoleMapper sysRoleMapper;
    @Resource
    private StoreWorkRangeService storeWorkRangeService;
    @Resource(name = "generalThreadPool")
    private ThreadPoolTaskExecutor EXECUTOR_SERVICE;
    @Resource
    private SysRoleMapper roleMapper;
    @Resource
    private StoreWorkRecordService storeWorkRecordService;
    @Autowired
    private SysRoleService sysRoleService;
    @Resource
    private SimpleMessageService simpleMessageService;
    @Resource
    UserAuthMappingMapper userAuthMappingMapper;
    @Resource
    EnterpriseConfigMapper enterpriseConfigMapper;

    @Autowired
    private RedisUtilPool redisUtilPool;
    @Resource
    private TaskSopService taskSopService;
    @Resource
    StoreWorkServiceImpl storeWorkService;
    @Resource
    @Lazy
    private JmsTaskService jmsTaskService;
    @Resource
    private TbMetaTableService tbMetaTableService;
    @Resource
    EnterpriseConfigMapper configMapper;
    @Resource
    private StoreWorkBuildServiceImpl storeWorkBuildService;
    @Resource
    TbMetaStaTableColumnDao tbMetaStaTableColumnDao;
    @Resource
    private StoreGroupMappingMapper storeGroupMappingMapper;

    @Resource
    private EnterpriseUserGroupMappingDao enterpriseUserGroupMappingDao;

    @Autowired
    private EnterpriseService enterpriseService;
    @Resource
    private EnterpriseUserGroupDao enterpriseUserGroupDao;
    @Resource
    private SysDepartmentMapper sysDepartmentMapper;
    @Resource
    private RegionMapper regionMapper;
    @Resource
    private EnterpriseSettingMapper enterpriseSettingMapper;

    private static  final int STORELIMIT = 15000; 

    @Override
    public void dayClearTaskResolve(StoreTaskResolveRequest request) {

        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigMapper.selectByEnterpriseId(request.getEnterpriseId());
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());
        //本月推下月 本周推下周 本月推下月数据的时候 计算时间
        String workCycle  = request.getWorkCycle();
        //查询店务定义表
        List<SwStoreWorkDO> swStoreWorkDOS = swStoreWorkDao.selectByTime(request.getEnterpriseId(), request.getCurrentDate(),request.getStoreWorkId(), workCycle);
        if (!request.getPushFlag()){
            //本周跑下周数据
            request.setCurrentDate(getNextCurrentDate(request.getCurrentDate(),workCycle));
        }else {
            //本周跑本周数据
            request.setCurrentDate(getCurrentDate(request.getCurrentDate(),workCycle));
        }
        //如果没有店务记录，直接退出
        if (CollectionUtils.isEmpty(swStoreWorkDOS)){
            return;
        }
        for (SwStoreWorkDO swStoreWorkDO:swStoreWorkDOS) {
            if (!storeWorkGenerateVerify(swStoreWorkDO)) {
                log.info("店务不生成，storeWorkName:{}，storeWorkId:{}", swStoreWorkDO.getWorkName(), swStoreWorkDO.getId());
                continue;
            }
            StoreWorkResolveDTO storeWorkResolveDTO = new StoreWorkResolveDTO();
            storeWorkResolveDTO.setSwStoreWorkDO(swStoreWorkDO);
            storeWorkResolveDTO.setStoreTaskResolveRequest(request);
            //每个店务发一个消息分解店务
            simpleMessageService.send(JSONObject.toJSONString(storeWorkResolveDTO), RocketMqTagEnum.STOREWORK_TASK_RESOLVE);

        }
    }

    /**
     * 店务生成校验
     * @param swStoreWorkDO 店务定义DO对象
     * @return 是否生成店务
     */
    public boolean storeWorkGenerateVerify(SwStoreWorkDO swStoreWorkDO) {
        Date now = new Date();
        if (now.after(swStoreWorkDO.getEndTime()) || now.before(swStoreWorkDO.getBeginTime())) {
            return false;
        }
        try {
            JSONObject aiRange = JSONObject.parseObject(swStoreWorkDO.getAiRange());
            if (Objects.nonNull(aiRange)) {
                StoreWorkDateRangeDTO notGenerateRange = aiRange.getObject(Constants.STORE_WORK_AI.NOT_GENERATE_RANGE, StoreWorkDateRangeDTO.class);
                // 今天是否生成店务
                if (storeWorkDateRangeMatch(LocalDate.now(), notGenerateRange)) {
                    log.info("今天为不生成店务的日期，storeWorkId:{}, notGenerateRange:{}", swStoreWorkDO.getId(), JSONObject.toJSONString(notGenerateRange));
                    return false;
                }
            }
        } catch (Exception e) {
            log.error("店务生成校验失败", e);
        }
        return true;
    }

    /**
     * 店务日期范围匹配
     * @param range 日期范围
     * @return 是否匹配
     */
    private boolean storeWorkDateRangeMatch(LocalDate date, StoreWorkDateRangeDTO range) {
        // yyyy-MM-dd
        String dayStr = date.toString();
        // 周几
        String dayOfWeekStr = String.valueOf(date.getDayOfWeek().getValue());
        // 第几周
        String weekOfYearStr = String.valueOf(date.get(WeekFields.ISO.weekOfWeekBasedYear()));
        // 几月
        String monthStr = String.valueOf(date.getMonthValue());
        if (Objects.nonNull(range)) {
            StoreWorkDateRangeEnum rangeEnum = StoreWorkDateRangeEnum.getByType(range.getType());
            String verifyDateStr = StoreWorkDateRangeEnum.DAY.equals(rangeEnum)
                    ? dayStr : StoreWorkDateRangeEnum.WEEKDAY.equals(rangeEnum)
                    ? dayOfWeekStr : StoreWorkDateRangeEnum.WEEK_OF_YEAR.equals(rangeEnum)
                    ? weekOfYearStr : StoreWorkDateRangeEnum.MONTH.equals(rangeEnum)
                    ? monthStr : null;
            if (StringUtils.isNotBlank(verifyDateStr)) {
                String s = ListUtils.emptyIfNull(range.getValues()).stream().filter(verifyDateStr::equals).findFirst().orElse("");
                return StringUtils.isNotBlank(s);
            }
        }
        return false;
    }

    @Override
    public void resolve(StoreWorkResolveDTO storeWorkResolveDTO) {

        SwStoreWorkDO swStoreWorkDO = storeWorkResolveDTO.getSwStoreWorkDO();
        StoreTaskResolveRequest request = storeWorkResolveDTO.getStoreTaskResolveRequest();

        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigMapper.selectByEnterpriseId(request.getEnterpriseId());
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());

        //店务检查表映射表记录
        List<SwStoreWorkTableMappingDO> swStoreWorkTableMappingDOS = swStoreWorkTableMappingDao.selectListByStoreWorkIds(request.getEnterpriseId(), Arrays.asList(swStoreWorkDO.getId()));

        List<Long> tbMetaTableList = swStoreWorkTableMappingDOS.stream().map(SwStoreWorkTableMappingDO::getMetaTableId).collect(Collectors.toList());

        List<SwStoreWorkRangeDO> swStoreWorkRangeList = swStoreWorkRangeDao.selectListByStoreWorkIds(request.getEnterpriseId(), Arrays.asList(swStoreWorkDO.getId()));
        List<StoreAreaDTO> storeDTOList = getStoreRange(request.getEnterpriseId(),swStoreWorkRangeList);

        //查询哪些门店当前时间已经生成店务记录，生成的当天不在生成店务记录
        String queryDate = DateUtil.format(request.getCurrentDate(), DateUtils.DATE_FORMAT_DAY);
        List<SwStoreWorkRecordDO> storeWorkRecordDOList = swStoreWorkRecordDao.selectSwStoreWorkRecord(request.getEnterpriseId(), queryDate, request.getWorkCycle());
        List<String> list = ListUtils.emptyIfNull(storeWorkRecordDOList).stream().map(SwStoreWorkRecordDO::getStoreId).collect(Collectors.toList());

        if (CollectionUtils.isNotEmpty(list)){
            storeDTOList = storeDTOList.stream().filter(x->!list.contains(x.getStoreId())).collect(Collectors.toList());
        }

        if (CollectionUtils.isNotEmpty(storeWorkRecordDOList)){
            storeWorkRecordDOList = storeWorkRecordDOList.stream().filter(x->x.getStoreWorkId().equals(swStoreWorkDO.getId())).collect(Collectors.toList());
            ListUtils.emptyIfNull(storeWorkRecordDOList).forEach(x->{
                log.info("店务更新同步最新处理人和点评人:{}", JSONObject.toJSONString(x));
                // 同步最新处理人和点评人
                StoreWorkHandleCommentUpdateDTO storeWorkHandleCommentUpdateDTO = new StoreWorkHandleCommentUpdateDTO();
                storeWorkHandleCommentUpdateDTO.setEnterpriseId(request.getEnterpriseId());
                storeWorkHandleCommentUpdateDTO.setTcBusinessId(x.getTcBusinessId());
                storeWorkHandleCommentUpdateDTO.setStoreId(x.getStoreId());
                storeWorkHandleCommentUpdateDTO.setStoreWorkId(x.getStoreWorkId());
                storeWorkHandleCommentUpdateDTO.setReissueFlag(request.getReissueFlag());
                simpleMessageService.send(JSONObject.toJSONString(storeWorkHandleCommentUpdateDTO), RocketMqTagEnum.STOREWORK_HANDLE_COMMENT_PERSON_UPDATE);
            });


        }
        //门店范围如果为null 直接不分解
        if (CollectionUtils.isEmpty(storeDTOList)){
            log.info("店务门店范围为空:{}", JSONObject.toJSONString(swStoreWorkDO));
            return;
        }
        //添加店务记录
        addSwStoreWorkRecord(request.getEnterpriseId(), swStoreWorkDO, request.getCurrentDate(), storeDTOList,
                tbMetaTableList,swStoreWorkTableMappingDOS,enterpriseConfigDO.getDbName(),request.getReissueFlag(), request.getManualReissue());

    }

    /**
     * 门店有权限的人员
     * @param enterpriseId
     * @param personInfo
     * @param storeIds
     * @return
     */
    public Map<String, List<String>>  getAuthUser(String enterpriseId,String personInfo,List<String> storeIds, String createUserId, Boolean isHandle){
        if (StringUtils.isEmpty(personInfo)){
            return new HashMap<>();
        }
        List<StoreWorkCommonDTO> handlePersonDTOS = JSONObject.parseArray(personInfo, StoreWorkCommonDTO.class);
        List<String> personIds = handlePersonDTOS.stream().filter(x -> UnifyTaskConstant.PersonType.PERSON.equals(x.getType()))
                .map(StoreWorkCommonDTO::getValue).collect(Collectors.toList());
        List<String> positionIds = handlePersonDTOS.stream().filter(x -> UnifyTaskConstant.PersonType.POSITION.equals(x.getType()))
                .map(StoreWorkCommonDTO::getValue).collect(Collectors.toList());
        List<String> groupIdList = handlePersonDTOS.stream().filter(x -> UnifyTaskConstant.PersonType.USER_GROUP.equals(x.getType()))
                .map(StoreWorkCommonDTO::getValue).collect(Collectors.toList());
        List<String> regionIdList = handlePersonDTOS.stream().filter(x -> UnifyTaskConstant.PersonType.ORGANIZATION.equals(x.getType()))
                .map(StoreWorkCommonDTO::getValue).collect(Collectors.toList());
        //权限
        List<AuthStoreUserDTO> authStoreUserList = storeService.getStorePositionUserList(enterpriseId,
                storeIds, positionIds, personIds, groupIdList, regionIdList, createUserId, isHandle);
        Map<String, List<String>> storeUserMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(authStoreUserList)) {
            storeUserMap = authStoreUserList.stream().collect(Collectors.toMap(AuthStoreUserDTO::getStoreId,
                    AuthStoreUserDTO::getUserIdList, (a, b) -> a));
        }
        return storeUserMap;
    }




    /**
     * 分解店务数据入库
     * @param enterpriseId
     * @param swStoreWorkDO
     * @param currentDate
     * @param storeDTOList
     * @param tbMetaTableList
     * @param swStoreWorkTableMappingDOS
     */
    public void addSwStoreWorkRecord(String enterpriseId, SwStoreWorkDO swStoreWorkDO, Date currentDate, List<StoreAreaDTO> storeDTOList,
                                     List<Long> tbMetaTableList, List<SwStoreWorkTableMappingDO> swStoreWorkTableMappingDOS, String dbName,
                                     Boolean reissueFlag, Boolean manualReissue) {

        List<TbMetaTableDO> tbMetaTableDOS = tbMetaTableMapper.selectByIds(enterpriseId, tbMetaTableList);

        //所有检查表总分
        BigDecimal totalScore = tbMetaTableDOS.stream().map(TbMetaTableDO::getTotalScore).reduce(BigDecimal.ZERO, BigDecimal::add);

        Integer totalColumnNum = Constants.INDEX_ZERO;
        Integer collectColumnNum = Constants.INDEX_ZERO;
        //自定义检查表ID
        List<Long> defTableIds = tbMetaTableDOS.stream().filter(x -> MetaTablePropertyEnum.USER_DEFINED_TABLE.getCode().equals(x.getTableProperty()))
                .map(TbMetaTableDO::getId).collect(Collectors.toList());
        List<TbMetaDefTableColumnDO> allColumnByMetaTableIdList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(defTableIds)){
            allColumnByMetaTableIdList = tbMetaDefTableColumnMapper.getAllColumnByMetaTableIdList(enterpriseId, defTableIds);
            totalColumnNum += allColumnByMetaTableIdList.size();
        }
        //非自定义检查表ids
        List<Long> staTableIds = tbMetaTableDOS.stream().filter(x -> !MetaTablePropertyEnum.USER_DEFINED_TABLE.getCode().equals(x.getTableProperty()))
                .map(TbMetaTableDO::getId).collect(Collectors.toList());
        List<TbMetaStaTableColumnDO> tbMetaStaTableColumnDOS = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(staTableIds)){
            tbMetaStaTableColumnDOS = tbMetaStaTableColumnMapper.selectColumnListByTableIdList(enterpriseId, staTableIds, Boolean.TRUE);
            //采集项
            List<TbMetaStaTableColumnDO> collectColumnList = tbMetaStaTableColumnDOS.stream().
                    filter(x -> MetaColumnTypeEnum.COLLECT_COLUMN.getCode().equals(x.getColumnType())).collect(Collectors.toList());
            collectColumnNum += collectColumnList.size();
            totalColumnNum += tbMetaStaTableColumnDOS.size();
        }

        Integer finalCollectColumnNum = collectColumnNum;
        Integer finalTotalColumnNum = totalColumnNum;
        List<TbMetaDefTableColumnDO> finalAllColumnByMetaTableIdList = allColumnByMetaTableIdList;
        List<TbMetaStaTableColumnDO> finalTbMetaStaTableColumnDOS = tbMetaStaTableColumnDOS;
        List<String> storeIds = storeDTOList.stream().map(StoreAreaDTO::getStoreId).collect(Collectors.toList());

        StoreWorkSingleStoreResolveDTO storeWorkSingleStoreResolveDTO = new StoreWorkSingleStoreResolveDTO();
        storeWorkSingleStoreResolveDTO.setSwStoreWorkDO(swStoreWorkDO);
        storeWorkSingleStoreResolveDTO.setEnterpriseId(enterpriseId);
        storeWorkSingleStoreResolveDTO.setSwStoreWorkTableMappingDOS(swStoreWorkTableMappingDOS);
        storeWorkSingleStoreResolveDTO.setCurrentDate(currentDate);
        storeWorkSingleStoreResolveDTO.setTotalScore(totalScore);
        storeWorkSingleStoreResolveDTO.setTbMetaTableDOS(tbMetaTableDOS);
        storeWorkSingleStoreResolveDTO.setFinalCollectColumnNum(finalCollectColumnNum);
        storeWorkSingleStoreResolveDTO.setFinalTotalColumnNum(finalTotalColumnNum);
        storeWorkSingleStoreResolveDTO.setTbMetaDefTableColumnDOS(finalAllColumnByMetaTableIdList);
        storeWorkSingleStoreResolveDTO.setTbMetaStaTableColumnDOS(finalTbMetaStaTableColumnDOS);
        storeWorkSingleStoreResolveDTO.setReissueFlag(reissueFlag);
        storeWorkSingleStoreResolveDTO.setManualReissue(manualReissue);
        log.info("store_count:{}",storeDTOList.size());

        // 是否使用AI检查的处理
        StoreWorkAiDTO storeWorkAiDTO = storeWorkAi(enterpriseId, swStoreWorkDO);
        log.info("storeWorkAiDTO:{}", JSONObject.toJSONString(storeWorkAiDTO));

        storeDTOList.forEach(x -> {
            log.info("enterpriseId:{},storeId：{},storeWorkId:{}", enterpriseId, x.getStoreId(), swStoreWorkDO.getId());
            storeWorkSingleStoreResolveDTO.setStoreAreaDTO(x);
            if (Boolean.TRUE.equals(storeWorkAiDTO.getUseAi())
                    && (Boolean.TRUE.equals(storeWorkAiDTO.getAllStore()) || storeWorkAiDTO.getAiStoreSet().contains(x.getStoreId()))) {
                storeWorkSingleStoreResolveDTO.setStoreWorkIsAiCheck(Boolean.TRUE);
            } else {
                storeWorkSingleStoreResolveDTO.setStoreWorkIsAiCheck(Boolean.FALSE);
            }
            //每个门店任务分解
            simpleMessageService.send(JSONObject.toJSONString(storeWorkSingleStoreResolveDTO), RocketMqTagEnum.STOREWORK_TASK_SINGLE_STORE_RESOLVE);
        });
        log.info("SwStoreWorkRecordResolveComplete storeWorkId:{}",swStoreWorkDO.getId());
    }

    /**
     * 获取店务的AI使用情况
     * @param enterpriseId 企业id
     * @param swStoreWorkDO 店务定义DO对象
     * @return 店务AI使用情况DTO
     */
    public StoreWorkAiDTO storeWorkAi(String enterpriseId, SwStoreWorkDO swStoreWorkDO) {
        try {
            String dbName = DynamicDataSourceContextHolder.getDataSourceType();
            DataSourceHelper.reset();
            EnterpriseSettingDO enterpriseSettingDO = enterpriseSettingMapper.selectByEnterpriseId(enterpriseId);
            DataSourceHelper.changeToSpecificDataSource(dbName);

            AIConfigDTO aiConfigDTO = JSONObject.parseObject(enterpriseSettingDO.getExtendField(), AIConfigDTO.class);
            if (Objects.isNull(aiConfigDTO) || !aiConfigDTO.aiEnable(AIBusinessModuleEnum.STORE_WORK)) {
                return StoreWorkAiDTO.notUseAi();
            }

            JSONObject aiRange = JSONObject.parseObject(swStoreWorkDO.getAiRange());
            StoreWorkDateRangeDTO useAiRange = aiRange.getObject(Constants.STORE_WORK_AI.USE_AI_RANGE, StoreWorkDateRangeDTO.class);
            boolean allStoreUseAI = Constants.STORE_WORK_AI.ALL_STORE_RANGE.equals(aiRange.getString(Constants.STORE_WORK_AI.AI_STORE_RANGE_METHOD));
            List<StoreWorkCommonDTO> storeRange = Optional.ofNullable(aiRange.getJSONArray(Constants.STORE_WORK_AI.AI_STORE_RANGE))
                    .map(v -> v.toJavaList(StoreWorkCommonDTO.class)).orElse(Collections.emptyList());
            // 是否使用ai分析
            if (Objects.isNull(useAiRange) || !allStoreUseAI && CollectionUtils.isEmpty(storeRange)) {
                return StoreWorkAiDTO.notUseAi();
            }
            // 今天是使用AI的日期
            if (storeWorkDateRangeMatch(LocalDate.now(), useAiRange)) {
                if (allStoreUseAI) {
                    return StoreWorkAiDTO.allStore();
                }
                // 分解使用AI的门店
                List<SwStoreWorkRangeDO> storeRangeList = storeRange.stream().map(v -> new SwStoreWorkRangeDO(null, swStoreWorkDO.getId(), v.getValue(), v.getType(), false)).collect(Collectors.toList());
                List<StoreAreaDTO> storeDTOList = getStoreRange(enterpriseId, storeRangeList);
                Set<String> storeIds = storeDTOList.stream().map(StoreAreaDTO::getStoreId).collect(Collectors.toSet());
                return StoreWorkAiDTO.build(storeIds);
            }
        } catch (Exception e) {
            log.error("店务AI使用情况处理失败", e);
        }
        return StoreWorkAiDTO.notUseAi();
    }

    /**
     * 每个门店店务数据新增
     * @param dto
     */
    @Override
    public void storeWorkSingleStoreResolve(StoreWorkSingleStoreResolveDTO dto){
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigMapper.selectByEnterpriseId(dto.getEnterpriseId());
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());
        storeWorkService.singStoreWork(dto.getEnterpriseId(),dto.getSwStoreWorkTableMappingDOS(),dto.getStoreAreaDTO(),
                dto.getSwStoreWorkDO(), dto.getCurrentDate(), dto.getTotalScore(),dto.getTbMetaTableDOS(), dto.getFinalCollectColumnNum(),
                dto.getFinalTotalColumnNum(), dto.getTbMetaDefTableColumnDOS(),dto.getTbMetaStaTableColumnDOS(),dto.getReissueFlag(), dto.getManualReissue(),
                dto.getStoreWorkIsAiCheck());
    }

    @Override
    public void storeWorkHandleCommentUpdate(StoreWorkHandleCommentUpdateDTO dto){
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigMapper.selectByEnterpriseId(dto.getEnterpriseId());
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());
        storeWorkService.singStoreWorkUpdate(dto.getEnterpriseId(),dto.getTcBusinessId(),dto.getStoreId(),
                dto.getStoreWorkId(), dto.getReissueFlag());
    }

    /**
     * 单店分解（事务）
     * @param enterpriseId
     * @param swStoreWorkTableMappingDOS
     * @param storeAreaDTO
     * @param swStoreWorkDO
     * @param totalScore
     * @param tbMetaTableDOS
     * @param finalCollectColumnNum
     * @param finalTotalColumnNum
     * @param finalDefColumnByMetaTableIdList
     * @param finalTbMetaStaTableColumnDOS
     * @param storeWorkIsAiCheck 店务是否进行AI分析
     */
    // TODO: 2022/10/8 添加事务
    @Transactional(rollbackFor = Exception.class)
    public void singStoreWork(String enterpriseId,List<SwStoreWorkTableMappingDO> swStoreWorkTableMappingDOS,StoreAreaDTO storeAreaDTO,
                              SwStoreWorkDO swStoreWorkDO,Date currentDate,BigDecimal totalScore,List<TbMetaTableDO> tbMetaTableDOS,
                              Integer finalCollectColumnNum, Integer finalTotalColumnNum,List<TbMetaDefTableColumnDO> finalDefColumnByMetaTableIdList,
                              List<TbMetaStaTableColumnDO> finalTbMetaStaTableColumnDOS,Boolean reissueFlag, Boolean manualReissue, Boolean storeWorkIsAiCheck){
        //校验任务是否执行中被删除 删除之后不再分解数据
        SwStoreWorkDO swStoreWork = swStoreWorkDao.selectByPrimaryKey(swStoreWorkDO.getId(), enterpriseId);
        if (swStoreWork==null){
            log.info("任务已经删除 swStoreWork：{}",JSONObject.toJSONString(swStoreWorkDO));
            return;
        }

        //处理数据
        SwStoreWorkRecordDO swStoreWorkRecordDO = handleStoreWorkRecord(storeAreaDTO, swStoreWorkDO, currentDate, totalScore,
                tbMetaTableDOS, finalCollectColumnNum, finalTotalColumnNum, finalDefColumnByMetaTableIdList,swStoreWorkTableMappingDOS.size());

        //一个店务有多组 一组有多个检查表
        Map<Integer, List<SwStoreWorkTableMappingDO>> groupMappingMap = swStoreWorkTableMappingDOS.stream().collect(Collectors.groupingBy(SwStoreWorkTableMappingDO::getGroupNum));

        Boolean existHandlePersonFlag = Boolean.FALSE;
        for (List<SwStoreWorkTableMappingDO> swStoreWorkTableMappingList : groupMappingMap.values()) {
            //每个小组的处理人都相同，去每组的第一个检查表的handlePersonInfo信息
            String handlePersonInfo = swStoreWorkTableMappingList.get(0).getHandlePersonInfo();
            //处理人门店人员权限
            Map<String, List<String>> handleStoreUserMap = getAuthUser(enterpriseId, handlePersonInfo, Arrays.asList(storeAreaDTO.getStoreId()), swStoreWork.getCreateUserId(), true);
            List<String> handleUserIds = handleStoreUserMap.getOrDefault(storeAreaDTO.getStoreId(), new ArrayList<>());
            if (CollectionUtils.isNotEmpty(handleUserIds)){
                existHandlePersonFlag = Boolean.TRUE;
                break;
            }
        }

        if (!existHandlePersonFlag){
            log.info("该店务所有门店下没人，店务不分解 enterpriseId:{}, storeWorkId:{},storeId:{}",enterpriseId,swStoreWorkDO.getId(),storeAreaDTO.getStoreId());
            return;
        }

        //添加电务记录
        swStoreWorkRecordDao.insertSelective(swStoreWorkRecordDO,enterpriseId);

        Map<Long, List<TbMetaDefTableColumnDO>> defMap = finalDefColumnByMetaTableIdList.stream().collect(Collectors.groupingBy(TbMetaDefTableColumnDO::getMetaTableId));
        Map<Long, List<TbMetaStaTableColumnDO>> staMap = finalTbMetaStaTableColumnDOS.stream().collect(Collectors.groupingBy(TbMetaStaTableColumnDO::getMetaTableId));

        for (List<SwStoreWorkTableMappingDO> swStoreWorkTableMappingList : groupMappingMap.values()) {
            List<Long> currentGroupingMetaTableIds = swStoreWorkTableMappingList.stream().map(SwStoreWorkTableMappingDO::getMetaTableId).collect(Collectors.toList());
            //当前分组检查表
            List<TbMetaTableDO> currentGroupingMetaTable = tbMetaTableDOS.stream().filter(y -> currentGroupingMetaTableIds.contains(y.getId())).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(currentGroupingMetaTable)){
                continue;
            }
            //每个小组的处理人都相同，去每组的第一个检查表的handlePersonInfo信息
            String handlePersonInfo = swStoreWorkTableMappingList.get(0).getHandlePersonInfo();
            //处理人门店人员权限
            Map<String, List<String>> handleStoreUserMap = getAuthUser(enterpriseId, handlePersonInfo, Arrays.asList(storeAreaDTO.getStoreId()), swStoreWork.getCreateUserId(), true);
            //检查表数据项
            List<SwStoreWorkDataTableDO> swStoreWorkDataTableDOS = handleStoreWorkDataTable(currentGroupingMetaTable, swStoreWorkTableMappingList, currentDate, defMap, staMap, storeWorkIsAiCheck);
            swStoreWorkDataTableDOS = swStoreWorkDataTableDOS.stream().filter(x->x.getBeginTime().getTime() < x.getEndTime().getTime()).collect(Collectors.toList());
            //补发且逾期不可执行过滤掉过期表
            if (reissueFlag) {
                if(manualReissue != null && manualReissue){
                    //手动补发
                    if(Constants.INDEX_ZERO.equals(swStoreWork.getOverdueContinue())){
                        //补发的情况下，过滤结束时间小于当前时间的，也就是过滤掉逾期的
                        swStoreWorkDataTableDOS = swStoreWorkDataTableDOS.stream().filter(x->x.getEndTime().getTime()>System.currentTimeMillis()).collect(Collectors.toList());
                    }
                }else if(manualReissue != null){
                    //手动不补发
                    swStoreWorkDataTableDOS = swStoreWorkDataTableDOS.stream().filter(x->x.getBeginTime().getTime()>System.currentTimeMillis()).collect(Collectors.toList());
                }else {
                    //补发的情况下，过滤结束时间小于当前时间的，也就是过滤掉逾期的
                    swStoreWorkDataTableDOS = swStoreWorkDataTableDOS.stream().filter(x->x.getEndTime().getTime()>System.currentTimeMillis()).collect(Collectors.toList());
                }
            }
            //todo 补发过滤逾期的
            if (CollectionUtils.isEmpty(swStoreWorkDataTableDOS)){
                log.info("swStoreWorkDataTableDOS 为空");
                continue;
            }
            String commentPersonInfos = swStoreWorkTableMappingList.get(0).getCommentPersonInfo();
            Map<String, List<String>> commentPersonAuthUser = new HashMap<>();
            if (StringUtils.isNotEmpty(commentPersonInfos)){
                List<StoreWorkCommonDTO> storeWorkCommonDTOS = JSONObject.parseArray(commentPersonInfos, StoreWorkCommonDTO.class);
                if (CollectionUtils.isNotEmpty(storeWorkCommonDTOS)){
                    commentPersonAuthUser = getAuthUser(enterpriseId, JSONObject.toJSONString(storeWorkCommonDTOS), Arrays.asList(storeAreaDTO.getStoreId()), swStoreWork.getCreateUserId(), false);
                }
            }
            //新增sw_store_work_data_table表数据
            addStoreWorkDataTable(enterpriseId,currentDate, swStoreWorkDataTableDOS,storeAreaDTO,handleStoreUserMap,swStoreWorkDO,commentPersonAuthUser,defMap,staMap,currentGroupingMetaTable);
            if (reissueFlag){
                HashMap<String, String> paramMap = new HashMap<>();
                paramMap.put("reissueFlag", String.valueOf(reissueFlag));
                for (SwStoreWorkDataTableDO swStoreWorkDataTableDO : swStoreWorkDataTableDOS) {
                    // 已经开始
                    if(swStoreWorkDataTableDO.getBeginTime().getTime() < System.currentTimeMillis()&&swStoreWorkDataTableDO.getId()!=null){
                        jmsTaskService.sendStoreWorkMessage(enterpriseId, swStoreWorkDataTableDO.getId(),  StoreWorkNoticeEnum.BEFORE_START_REMIND.getOperate(), paramMap);
                    }
                }
            }
        }
        if (reissueFlag){
            List<SwStoreWorkDataTableDO> tc = swStoreWorkDataTableDao.getSwStoreWorkDataTableListByBusinessId(enterpriseId, null, swStoreWorkRecordDO.getTcBusinessId());
            if (CollectionUtils.isEmpty(tc)){
                //如果没有检查表数据，删除店务记录数据
                swStoreWorkRecordDao.deleteByPrimaryKey(swStoreWorkRecordDO.getId(),enterpriseId);
            }
        }
    }



    /**
     * 处理电务记录数据
     * @param storeAreaDTO
     * @param swStoreWorkDO
     * @param currentDate
     * @param totalScore
     * @param tbMetaTableDOS
     * @param finalCollectColumnNum
     * @param finalTotalColumnNum
     * @param finalAllColumnByMetaTableIdList
     * @return
     */
    public  SwStoreWorkRecordDO handleStoreWorkRecord(StoreAreaDTO storeAreaDTO, SwStoreWorkDO swStoreWorkDO,Date currentDate,BigDecimal totalScore,List<TbMetaTableDO> tbMetaTableDOS,
                                                      Integer finalCollectColumnNum, Integer finalTotalColumnNum,List<TbMetaDefTableColumnDO> finalAllColumnByMetaTableIdList,Integer totalTableNum){
        //店务分解到每个门店
        SwStoreWorkRecordDO swStoreWorkRecordDO = new SwStoreWorkRecordDO();
        //tc_business_id
        String businessId = getTcBusinessIdMd5(swStoreWorkDO.getWorkCycle(), storeAreaDTO.getStoreId(), currentDate);
        swStoreWorkRecordDO.setTcBusinessId(businessId);
        swStoreWorkRecordDO.setStoreWorkId(swStoreWorkDO.getId());
        swStoreWorkRecordDO.setWorkCycle(swStoreWorkDO.getWorkCycle());
        swStoreWorkRecordDO.setStoreId(storeAreaDTO.getStoreId());
        swStoreWorkRecordDO.setStoreWorkDate(currentDate);
        swStoreWorkRecordDO.setStoreName(storeAreaDTO.getStoreName());
        swStoreWorkRecordDO.setRegionId(storeAreaDTO.getRegionId());
        swStoreWorkRecordDO.setRegionPath(storeAreaDTO.getRegionPath());
        swStoreWorkRecordDO.setDefColumnNum(finalAllColumnByMetaTableIdList.size());
        swStoreWorkRecordDO.setFinishColumnNum(Constants.INDEX_ZERO);
        swStoreWorkRecordDO.setCollectColumnNum(finalCollectColumnNum);
        swStoreWorkRecordDO.setTotalColumnNum(finalTotalColumnNum);
        swStoreWorkRecordDO.setTotalFullScore(totalScore);
        swStoreWorkRecordDO.setTableNum(totalTableNum);
        swStoreWorkRecordDO.setCreateTime(new Date());
        swStoreWorkRecordDO.setCreateUserId(swStoreWorkDO.getCreateUserId());
        swStoreWorkRecordDO.setUpdateTime(new Date());
        swStoreWorkRecordDO.setUpdateUserId(swStoreWorkDO.getUpdateUserId());
        return swStoreWorkRecordDO;
    }

    /**
     * 生成businesId MD5
     * @param workCycle
     * @param storeId
     * @param storeWorkDate
     * @return
     */
    public String  getTcBusinessIdMd5(String workCycle,String storeId,Date storeWorkDate){
        return MD5Util.md5(String.format("%s%s%s", workCycle, storeId, storeWorkDate));
    }



    /**
     * 获取门店范围
     * @param swStoreWorkRangeDOS
     * @return
     */
    @Override
    public List<StoreAreaDTO> getStoreRange(String enterpriseId,List<SwStoreWorkRangeDO> swStoreWorkRangeDOS){
        List<String> regionIds = swStoreWorkRangeDOS.stream().filter(x -> UnifyTaskConstant.StoreType.REGION.equals(x.getType()))
                .map(SwStoreWorkRangeDO::getMappingId).collect(Collectors.toList());

        List<RegionDO> regionDOsByRegionIds = new ArrayList<>();
        Set<String> storeIdSet = new HashSet<>();
        if (CollectionUtils.isNotEmpty(regionIds)){
            regionDOsByRegionIds = regionService.getRegionDOsByRegionIds(enterpriseId, regionIds);
            //区域全路径地址集合 ["/1/37139128281/","/1/37139128283/"]
            List<String> regionFullRegionPathList = regionDOsByRegionIds.stream().map(RegionDO::getFullRegionPath).collect(Collectors.toList());
            //根据regionFullRegionPathList查询区域下所有的门店(包括子区域门店)
            List<StoreAreaDTO> storeAreaDTOS = storeMapper.listStoreByRegionPathList(enterpriseId, regionFullRegionPathList);
            storeIdSet = storeAreaDTOS.stream().map(StoreAreaDTO::getStoreId).collect(Collectors.toSet());
        }
        List<String> storeIdList = swStoreWorkRangeDOS.stream().filter(x -> UnifyTaskConstant.StoreType.STORE.equals(x.getType()))
                .map(SwStoreWorkRangeDO::getMappingId).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(storeIdList)){
            //将寻找的门店添加到set中
            storeIdSet.addAll(storeIdList);
        }
        List<String> groupList = swStoreWorkRangeDOS.stream().filter(x -> UnifyTaskConstant.StoreType.GROUP.equals(x.getType()))
                .map(SwStoreWorkRangeDO::getMappingId).collect(Collectors.toList());
        //分组
        if (CollectionUtils.isNotEmpty(groupList)) {
            List<StoreGroupMappingDO> groupStoreList = storeGroupMappingMapper.getStoreGroupMappingByGroupIDs(enterpriseId, groupList);
            if (CollectionUtils.isNotEmpty(groupStoreList)) {
                Set<String> groupStoreSet = groupStoreList.stream().map(StoreGroupMappingDO::getStoreId).collect(Collectors.toSet());
                if(CollectionUtils.isNotEmpty(groupStoreSet)){
                    //将寻找的门店添加到set中
                    storeIdSet.addAll(groupStoreSet);
                }
            }
        }
        if(CollectionUtils.isEmpty(storeIdSet)){
           return new ArrayList<>();
        }
        List<StoreAreaDTO> storeDTOList = storeMapper.getStoreAreaList(enterpriseId, new ArrayList<>(storeIdSet));
        if(CollectionUtils.isNotEmpty(storeDTOList)){
            storeDTOList = storeDTOList.stream().filter(o -> Constants.STORE_STATUS_OPEN.equals(o.getStoreStatus())).collect(Collectors.toList());
        }
        return storeDTOList;
    }


    /**
     * 分组处理检查表数据
     * @param tbMetaTableDOS
     * @param swStoreWorkTableMappingDOS
     * @param currentDate
     * @param defMap
     * @param staMap
     * @param storeWorkIsAiCheck 店务是否进行AI分析
     * @return
     */
    public List<SwStoreWorkDataTableDO> handleStoreWorkDataTable(List<TbMetaTableDO> tbMetaTableDOS, List<SwStoreWorkTableMappingDO> swStoreWorkTableMappingDOS,
                                                                 Date currentDate, Map<Long, List<TbMetaDefTableColumnDO>> defMap,Map<Long, List<TbMetaStaTableColumnDO>> staMap,
                                                                 Boolean storeWorkIsAiCheck){

        Integer totalColumnNum = Constants.INDEX_ZERO;
        Integer collectColumnNum = Constants.INDEX_ZERO;

        Map<Long, TbMetaTableDO> metaTableDOMap = tbMetaTableDOS.stream().collect(Collectors.toMap(TbMetaTableDO::getId, x -> x));

        List<SwStoreWorkDataTableDO> swStoreWorkDataTableDOS = new ArrayList<>();
        for (SwStoreWorkTableMappingDO swStoreWorkTableMappingDO:swStoreWorkTableMappingDOS) {
            SwStoreWorkDataTableDO swStoreWorkDataTableDO = new SwStoreWorkDataTableDO();
            TbMetaTableDO tbMetaTableDO = metaTableDOMap.get(swStoreWorkTableMappingDO.getMetaTableId());
            String workCycle = swStoreWorkTableMappingDO.getWorkCycle();
            //计算开始时间
            Date beginTime = calculateBeginTime(workCycle, swStoreWorkTableMappingDO.getBeginDate(), swStoreWorkTableMappingDO.getBeginTime(), currentDate);
            swStoreWorkDataTableDO.setBeginTime(beginTime);
            swStoreWorkDataTableDO.setEndTime(DateUtils.getSpecialTime(beginTime,swStoreWorkTableMappingDO.getLimitHour()));
            swStoreWorkDataTableDO.setTableMappingId(swStoreWorkTableMappingDO.getId());
            swStoreWorkDataTableDO.setMetaTableId(tbMetaTableDO.getId());
            swStoreWorkDataTableDO.setGroupNum(swStoreWorkTableMappingDO.getGroupNum());
            swStoreWorkDataTableDO.setTableProperty(tbMetaTableDO.getTableProperty());
            swStoreWorkDataTableDO.setTableName(swStoreWorkTableMappingDO.getDutyName());
            swStoreWorkDataTableDO.setStoreWorkDate(currentDate);
            swStoreWorkDataTableDO.setTotalScore(tbMetaTableDO.getTotalScore());

            //是否是自定义表
            Boolean tableProperty = TableTypeUtil.isUserDefinedTable(tbMetaTableDO.getTableProperty(), tbMetaTableDO.getTableType());
            if (!tableProperty){
                List<TbMetaStaTableColumnDO> tbMetaStaTableColumnList = staMap.getOrDefault(tbMetaTableDO.getId(),new ArrayList<>());
                totalColumnNum = tbMetaStaTableColumnList.size();
                List<TbMetaStaTableColumnDO> collectColumnList = tbMetaStaTableColumnList.stream().
                        filter(x -> MetaColumnTypeEnum.COLLECT_COLUMN.getCode().equals(x.getColumnType())).collect(Collectors.toList());
                collectColumnNum = collectColumnList.size();
            }else {
                List<TbMetaDefTableColumnDO> tbMetaDefTableColumnDOS = defMap.getOrDefault(tbMetaTableDO.getId(),new ArrayList<>());
                totalColumnNum = tbMetaDefTableColumnDOS.size();
            }
            swStoreWorkDataTableDO.setCollectColumnNum(collectColumnNum);
            swStoreWorkDataTableDO.setTotalColumnNum(totalColumnNum);
            swStoreWorkDataTableDO.setIsAiCheck(tbMetaTableDO.getIsAiCheck());
            swStoreWorkDataTableDO.setIsAiProcess(Boolean.TRUE.equals(storeWorkIsAiCheck) && Constants.INDEX_ONE.equals(tbMetaTableDO.getIsAiCheck()) ? 1 : 0);
            swStoreWorkDataTableDOS.add(swStoreWorkDataTableDO);
        }
        return swStoreWorkDataTableDOS;
    }

    /**
     * 计算日清，周清 月清开始时间
     * @param workCycle
     * @param beginDate
     * @param beginTime
     * @param currentDate
     * @return
     */
    public Date calculateBeginTime(String workCycle,String beginDate,String beginTime,Date currentDate){
        if (workCycle.equals(StoreWorkCycleEnum.DAY.getCode())){
            return DateUtils.transferString2Date(String.format(DateUtil.format(currentDate) + "%s%s", " ", beginTime));
        }
        if (workCycle.equals(StoreWorkCycleEnum.WEEK.getCode())){
            Date nextDayTime = DateUtils.getNextDayTime(currentDate, Integer.parseInt(beginDate)-1);
            return DateUtils.transferString2Date(String.format(DateUtil.format(nextDayTime) + "%s%s", " ", beginTime));
        }
        if (workCycle.equals(StoreWorkCycleEnum.MONTH.getCode())){
            Date nextDayTime = DateUtils.getNextDayTime(currentDate, Integer.parseInt(beginDate)-1);
            LocalDate currentLocalDate = currentDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate nextDayLocalDate = nextDayTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            if(!YearMonth.from(currentLocalDate).equals(YearMonth.from(nextDayLocalDate))){
                //如果计算出来的不是同一个月份  开始时间置为当前月份的最后一天
                nextDayTime = Date.from(currentLocalDate.with(TemporalAdjusters.lastDayOfMonth()).atStartOfDay(ZoneId.systemDefault()).toInstant());
            }
            return DateUtils.transferString2Date(String.format(DateUtil.format(nextDayTime) + "%s%s", " ", beginTime));
        }
        return new Date();
    }


    @Override
    public Boolean checkCanReissue(String eid, Long storeWordId) {
        SwStoreWorkDO swStoreWorkDO = swStoreWorkDao.selectByPrimaryKey(storeWordId, eid);
        Date now = new Date();
        //不在有效期内
        if (now.after(swStoreWorkDO.getEndTime()) || now.before(swStoreWorkDO.getBeginTime())) {
            return false;
        }
        boolean needReissue = false;

        //店务检查表映射表记录
        List<SwStoreWorkTableMappingDO> swStoreWorkTableMappingDOList = swStoreWorkTableMappingDao.selectListByStoreWorkIds(eid, Collections.singletonList(swStoreWorkDO.getId()));
        for (SwStoreWorkTableMappingDO swStoreWorkTableMappingDO : swStoreWorkTableMappingDOList) {
            Date beginTime = calculateBeginTime(swStoreWorkDO.getWorkCycle(), swStoreWorkTableMappingDO.getBeginDate(),
                    swStoreWorkTableMappingDO.getBeginTime(), getCurrentDate(new Date(), swStoreWorkDO.getWorkCycle()));
            Date endTime = DateUtils.getSpecialTime(beginTime, swStoreWorkTableMappingDO.getLimitHour());
            if(now.before(beginTime)){
                needReissue = true;
            }
            if (now.after(beginTime) && now.before(endTime)) {
                return true;
            }
            if (now.after(endTime) && Constants.INDEX_ONE.equals(swStoreWorkDO.getOverdueContinue())) {
                return true;
            }
        }
        //没有提醒，但是需要自动补发
        if(needReissue){
            StoreTaskResolveRequest storeTaskResolveRequest = new StoreTaskResolveRequest();
            storeTaskResolveRequest.setEnterpriseId(eid);
            storeTaskResolveRequest.setCurrentDate(new Date());
            storeTaskResolveRequest.setPushFlag(true);
            storeTaskResolveRequest.setReissueFlag(true);
            storeTaskResolveRequest.setStoreWorkId(storeWordId);
            storeTaskResolveRequest.setWorkCycle(swStoreWorkDO.getWorkCycle());
            storeTaskResolveRequest.setManualReissue(false);
            storeWorkService.dayClearTaskResolve(storeTaskResolveRequest);
        }
        return false;
    }

    /**
     * 本周跑下周数据处理(保证currentDate都是本周数据，便于查询数据)
     * @param currentDate
     * @param workCycle
     */
    public static Date getNextCurrentDate(Date currentDate,String workCycle){
        if (workCycle.equals(StoreWorkCycleEnum.DAY.getCode())){
            return DateUtils.getNextDayTime(currentDate,1);
        }
        if (workCycle.equals(StoreWorkCycleEnum.WEEK.getCode())){
            return DateUtils.getThisWeekMonday(currentDate);
        }
        if (workCycle.equals(StoreWorkCycleEnum.MONTH.getCode())){
            return DateUtils.firstDayOfNextMonth(currentDate);
        }
        return new Date();
    }

    /**
     * 本周跑本周数据处理(保证currentDate都是本周数据，便于查询数据)
     * @param currentDate
     * @param workCycle
     */
    public static Date getCurrentDate(Date currentDate,String workCycle){
        //直接返回当天
        if (workCycle.equals(StoreWorkCycleEnum.DAY.getCode())){
            return currentDate;
        }
        //先算出下周，往前推7天
        if (workCycle.equals(StoreWorkCycleEnum.WEEK.getCode())){
            return DateUtils.getNextDayTime(getNextCurrentDate(currentDate,workCycle),-7);
        }
        if (workCycle.equals(StoreWorkCycleEnum.MONTH.getCode())){
            return DateUtil.getFirstOfDayMonth(currentDate);
        }
        return new Date();
    }


    /**
     * 检查表 检查项data数据插入
     * @param enterpriseId
     * @param currentDate
     * @param swStoreWorkDataTableDOS
     * @param storeAreaDTO
     * @param storeUserMap
     * @param swStoreWorkDO
     * @param commentPersonAuthUser
     * @param defMap
     * @param staMap
     * @param currentGroupingMetaTable
     */
    public void addStoreWorkDataTable(String enterpriseId,Date currentDate, List<SwStoreWorkDataTableDO> swStoreWorkDataTableDOS,StoreAreaDTO storeAreaDTO,
                                      Map<String, List<String>> storeUserMap,SwStoreWorkDO swStoreWorkDO,Map<String, List<String>> commentPersonAuthUser,
                                      Map<Long, List<TbMetaDefTableColumnDO>> defMap,Map<Long, List<TbMetaStaTableColumnDO>> staMap,List<TbMetaTableDO> currentGroupingMetaTable){
        Map<Long, TbMetaTableDO> metaTableMap = currentGroupingMetaTable.stream().collect(Collectors.toMap(TbMetaTableDO::getId, x -> x));
        for (SwStoreWorkDataTableDO swStoreWorkDataTableDO:swStoreWorkDataTableDOS) {
            swStoreWorkDataTableDO.setTcBusinessId(getTcBusinessIdMd5(swStoreWorkDO.getWorkCycle(), storeAreaDTO.getStoreId(), currentDate));
            swStoreWorkDataTableDO.setStoreWorkId(swStoreWorkDO.getId());
            swStoreWorkDataTableDO.setWorkCycle(swStoreWorkDO.getWorkCycle());
            swStoreWorkDataTableDO.setStoreId(storeAreaDTO.getStoreId());
            swStoreWorkDataTableDO.setOverdueContinue(swStoreWorkDO.getOverdueContinue());
            swStoreWorkDataTableDO.setStoreName(storeAreaDTO.getStoreName());
            swStoreWorkDataTableDO.setRegionId(storeAreaDTO.getRegionId());
            swStoreWorkDataTableDO.setRegionPath(storeAreaDTO.getRegionPath());
            List<String> handleUserIds = storeUserMap.getOrDefault(storeAreaDTO.getStoreId(), new ArrayList<>());
            if (CollectionUtils.isEmpty(handleUserIds)){
                //数据回滚
                log.info("该门店下没人，该店店务不分解 enterpriseId:{}, storeWorkId:{},storeId:{}",enterpriseId,swStoreWorkDO.getId(),storeAreaDTO.getStoreId());
              return;
            }
            //处理人信息
            String handleUserIdsStr = String.join(Constants.COMMA, handleUserIds);
            handleUserIdsStr = String.format("%s%s%s", Constants.COMMA, handleUserIdsStr, Constants.COMMA);
            swStoreWorkDataTableDO.setHandleUserIds(handleUserIdsStr);
            swStoreWorkDataTableDO.setCreateTime(new Date());
            swStoreWorkDataTableDO.setCreateUserId(swStoreWorkDO.getCreateUserId());
            swStoreWorkDataTableDO.setUpdateTime(new Date());
            swStoreWorkDataTableDO.setUpdateUserId(swStoreWorkDO.getUpdateUserId());
            List<String> commentUserIds = commentPersonAuthUser.getOrDefault(storeAreaDTO.getStoreId(), new ArrayList<>());
            String commentUserIdsStr = "";
            if(CollectionUtils.isNotEmpty(commentUserIds)){
                commentUserIdsStr = String.format("%s%s%s", Constants.COMMA, String.join(Constants.COMMA, commentUserIds), Constants.COMMA);
            }
            swStoreWorkDataTableDO.setCommentUserIds(commentUserIdsStr);
        }
        if (CollectionUtils.isNotEmpty(swStoreWorkDataTableDOS)){
            swStoreWorkDataTableDao.batchInsert(enterpriseId,swStoreWorkDataTableDOS);
        }

        //数据表
        for (SwStoreWorkDataTableDO swStoreWorkDataTableDO:swStoreWorkDataTableDOS) {
            Long metaTableId = swStoreWorkDataTableDO.getMetaTableId();
            TbMetaTableDO tbMetaTableDO = metaTableMap.get(metaTableId);
            //是否是自定义表
            Boolean tableProperty = TableTypeUtil.isUserDefinedTable(tbMetaTableDO.getTableProperty(), tbMetaTableDO.getTableType());
            List<SwStoreWorkDataTableColumnDO> list = new ArrayList<>();
            if (tableProperty){
                List<TbMetaDefTableColumnDO> tbMetaDefTableColumnDOS = defMap.get(metaTableId);
                tbMetaDefTableColumnDOS.forEach(tbMetaDefTableColumnDO->{
                    SwStoreWorkDataTableColumnDO swStoreWorkDataTableColumnDO = handleSwStoreWorkDataTableColumnDO(swStoreWorkDataTableDO,swStoreWorkDO);
                    swStoreWorkDataTableColumnDO.setMetaColumnId(tbMetaDefTableColumnDO.getId());
                    swStoreWorkDataTableColumnDO.setMetaColumnName(tbMetaDefTableColumnDO.getColumnName());
                    swStoreWorkDataTableColumnDO.setDescription(tbMetaDefTableColumnDO.getDescription());
                    swStoreWorkDataTableColumnDO.setCategoryName("");
                    swStoreWorkDataTableColumnDO.setWeightPercent(new BigDecimal(Constants.INDEX_ZERO));
                    swStoreWorkDataTableColumnDO.setColumnMaxScore(new BigDecimal(Constants.INDEX_ZERO));
                    swStoreWorkDataTableColumnDO.setColumnMaxAward(new BigDecimal(Constants.INDEX_ZERO));
                    swStoreWorkDataTableColumnDO.setColumnType(7);
                    list.add(swStoreWorkDataTableColumnDO);
                });
            }else {
                List<TbMetaStaTableColumnDO> tbMetaStaTableColumnDOS = staMap.get(metaTableId);
                tbMetaStaTableColumnDOS.forEach(tbMetaStaTableColumnDO->{
                    SwStoreWorkDataTableColumnDO swStoreWorkDataTableColumnDO = handleSwStoreWorkDataTableColumnDO(swStoreWorkDataTableDO,swStoreWorkDO);
                    swStoreWorkDataTableColumnDO.setMetaColumnId(tbMetaStaTableColumnDO.getId());
                    swStoreWorkDataTableColumnDO.setMetaColumnName(tbMetaStaTableColumnDO.getColumnName());
                    swStoreWorkDataTableColumnDO.setDescription(tbMetaStaTableColumnDO.getDescription());
                    swStoreWorkDataTableColumnDO.setCategoryName(tbMetaStaTableColumnDO.getCategoryName());
                    swStoreWorkDataTableColumnDO.setWeightPercent(tbMetaStaTableColumnDO.getWeightPercent());
                    swStoreWorkDataTableColumnDO.setColumnMaxScore(tbMetaStaTableColumnDO.getSupportScore());
                    swStoreWorkDataTableColumnDO.setColumnMaxAward(tbMetaStaTableColumnDO.getAwardMoney());
                    swStoreWorkDataTableColumnDO.setColumnType(tbMetaStaTableColumnDO.getColumnType());
                    swStoreWorkDataTableColumnDO.setIsAiCheck(tbMetaStaTableColumnDO.getIsAiCheck());
                    list.add(swStoreWorkDataTableColumnDO);
                });
            }
            swStoreWorkDataTableColumnDao.batchInsert(enterpriseId,list);
        }
    }


    /**
     * 处理
     * @param swStoreWorkDataTableDO
     * @return
     */
    public SwStoreWorkDataTableColumnDO handleSwStoreWorkDataTableColumnDO(SwStoreWorkDataTableDO swStoreWorkDataTableDO,SwStoreWorkDO swStoreWorkDO){
        SwStoreWorkDataTableColumnDO swStoreWorkDataTableColumnDO = new SwStoreWorkDataTableColumnDO();
        swStoreWorkDataTableColumnDO.setTcBusinessId(swStoreWorkDataTableDO.getTcBusinessId());
        swStoreWorkDataTableColumnDO.setStoreWorkId(swStoreWorkDataTableDO.getStoreWorkId());
        swStoreWorkDataTableColumnDO.setStoreId(swStoreWorkDataTableDO.getStoreId());
        swStoreWorkDataTableColumnDO.setStoreName(swStoreWorkDataTableDO.getStoreName());
        swStoreWorkDataTableColumnDO.setRegionId(swStoreWorkDataTableDO.getRegionId());
        swStoreWorkDataTableColumnDO.setRegionPath(swStoreWorkDataTableDO.getRegionPath());
        swStoreWorkDataTableColumnDO.setTableMappingId(swStoreWorkDataTableDO.getTableMappingId());

        swStoreWorkDataTableColumnDO.setStoreWorkDate(swStoreWorkDataTableDO.getStoreWorkDate());
        swStoreWorkDataTableColumnDO.setWorkCycle(swStoreWorkDataTableDO.getWorkCycle());

        swStoreWorkDataTableColumnDO.setDataTableId(swStoreWorkDataTableDO.getId());
        swStoreWorkDataTableColumnDO.setMetaTableId(swStoreWorkDataTableDO.getMetaTableId());
        swStoreWorkDataTableColumnDO.setTableName(swStoreWorkDataTableDO.getTableName());
        swStoreWorkDataTableColumnDO.setCreateTime(new Date());
        swStoreWorkDataTableColumnDO.setCreateUserId(swStoreWorkDO.getCreateUserId());
        swStoreWorkDataTableColumnDO.setUpdateTime(new Date());
        swStoreWorkDataTableColumnDO.setUpdateUserId(swStoreWorkDO.getUpdateUserId());
        swStoreWorkDataTableColumnDO.setTableMappingId(swStoreWorkDataTableDO.getTableMappingId());
        return swStoreWorkDataTableColumnDO;
    }



    @Override
    public Long buildStoreWork(String enterpriseId, CurrentUser user, BuildStoreWorkRequest request) {
        Set<String> storeIdSet = getStoreNum(enterpriseId, request.getStoreRangeList());
        if (CollectionUtils.isEmpty(storeIdSet)) {
            throw new ServiceException(ErrorCodeEnum.DATA_ERROR.getCode(), "该门店范围内无门店");
        }
        if (storeIdSet.size() > STORELIMIT) {
            throw new ServiceException(ErrorCodeEnum.WORK_FLOW_ERROR.getCode(), "门店数量不允许超过" + STORELIMIT);
        }
        // 店务主表
        SwStoreWorkDO storeWorkDO = this.fillStoreWorkByRequest(user, request);
        if(request.getStoreWorkId() != null){
            swStoreWorkDao.updateByPrimaryKeySelective(storeWorkDO, enterpriseId);
        }else {
            swStoreWorkDao.insertSelective(storeWorkDO, enterpriseId);
        }
        Long storeWorkId = storeWorkDO.getId();
        //门店范围
        List<SwStoreWorkRangeDO> storeRangeList = getStoreRangeList(request.getStoreRangeList(), storeWorkId);
        List<SwStoreWorkTableMappingDO>  tableMappingDOList = Lists.newArrayList();
        List<TbMetaStaTableColumnDO> columnDOList = Lists.newArrayList();
        // 执行要求 执行人
        List<Long>  metaTableIds = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(request.getDutyInfoList())) {
            Pair<List<SwStoreWorkTableMappingDO>, List<TbMetaStaTableColumnDO>> pair = storeWorkTableMappingService.insertDutyInfoList(enterpriseId, request.getDutyInfoList(), storeWorkDO, user.getUserId());
            tableMappingDOList = pair.getLeft();
            columnDOList = pair.getRight();
            metaTableIds = tableMappingDOList.stream().map(SwStoreWorkTableMappingDO::getMetaTableId).collect(Collectors.toList());
        }

        storeWorkBuildService.insertStoreWorkInfo(enterpriseId, storeRangeList, tableMappingDOList);

        // 检查表锁表
        if (CollectionUtils.isNotEmpty(metaTableIds)){
            tbMetaTableService.updateLockedByIds(enterpriseId, metaTableIds);
        }
        if (CollectionUtils.isNotEmpty(columnDOList)){
            Lists.partition(columnDOList, Constants.BATCH_INSERT_COUNT).forEach(partColumnDOList -> {
                tbMetaStaTableColumnDao.batchUpdateExecuteDemand(enterpriseId, partColumnDOList);
            });
        }
        if(request.getTempCacheDataId() != null ){
            String cacheKey = MessageFormat.format(RedisConstant.STOREWORK_BUILD_CACHE_KEY, enterpriseId, user.getUserId(), request.getTempCacheDataId());
            redisUtilPool.delKey(cacheKey);
        }
        return storeWorkId;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SwStoreWorkDO changeStoreWork(String enterpriseId, BuildStoreWorkRequest request, CurrentUser user, String dingCorpId, String appType) {
        // 获取店务信息
        SwStoreWorkDO storeWorkDO = swStoreWorkDao.selectByPrimaryKey(request.getStoreWorkId(), enterpriseId);
        if (storeWorkDO == null) {
            throw new ServiceException(ErrorCodeEnum.STORE_WORK_NOT_EXIST);
        }
        if (storeWorkDO.getEndTime().getTime() < System.currentTimeMillis()) {
            throw new ServiceException(ErrorCodeEnum.STORE_WORK_END_CHECK);
        }
        // 删除门店映射表
        swStoreWorkRangeDao.delStoreRangeByStoreWorkId(enterpriseId, request.getStoreWorkId());
        buildStoreWork(enterpriseId, user, request);
        return storeWorkDO;
    }

    @Override
    public SwStoreWorkDetailVO getBuildCacheData(String enterpriseId, Long tempCacheDataId, CurrentUser user) {

        String userId = user.getUserId();
        String cacheKey = MessageFormat.format(RedisConstant.STOREWORK_BUILD_CACHE_KEY, enterpriseId, userId, tempCacheDataId);
        if (StringUtils.isBlank(redisUtilPool.getString(cacheKey))) {
            return null;
        }
        SwStoreWorkDetailVO storeWorkDetailVO = JSONObject.parseObject(redisUtilPool.getString(cacheKey), SwStoreWorkDetailVO.class);
        //店务检查表映射表记录
        List<StoreWorkDutyGroupInfoVO> dutyGroupInfoList = storeWorkDetailVO.getDutyInfoList();
        if (CollectionUtils.isEmpty(dutyGroupInfoList)){
            return storeWorkDetailVO;
        }
        List<Long> metaTableIdList = Lists.newArrayList();
        dutyGroupInfoList.forEach(dutyGroupInfoVO -> {
            if (CollectionUtils.isNotEmpty(dutyGroupInfoVO.getTableInfoList())) {
                List<Long> groupMetaTableIds = dutyGroupInfoVO.getTableInfoList().stream().map(StoreWorkTableInfoVO::getMetaTableId).collect(Collectors.toList());
                metaTableIdList.addAll(groupMetaTableIds);
            }
        });
        List<TbMetaTableDO> tbMetaTableDOS = tbMetaTableMapper.selectByIds(enterpriseId, metaTableIdList);
        Map<Long, TbMetaTableDO> metaTableMap = tbMetaTableDOS.stream().collect(Collectors.toMap(TbMetaTableDO::getId, x -> x));

        //自定义检查表ID
        List<Long> defTableIds = tbMetaTableDOS.stream().filter(x -> MetaTablePropertyEnum.USER_DEFINED_TABLE.getCode().equals(x.getTableProperty()))
                .map(TbMetaTableDO::getId).collect(Collectors.toList());
        List<TbMetaDefTableColumnDO> tbMetaDefTableColumnDOList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(defTableIds)){
            tbMetaDefTableColumnDOList = tbMetaDefTableColumnMapper.getAllColumnByMetaTableIdList(enterpriseId, defTableIds);
        }
        //非自定义检查表ids
        List<Long> staTableIds = tbMetaTableDOS.stream().filter(x -> !MetaTablePropertyEnum.USER_DEFINED_TABLE.getCode().equals(x.getTableProperty()))
                .map(TbMetaTableDO::getId).collect(Collectors.toList());
        List<TbMetaStaTableColumnDO> tbMetaStaTableColumnDOList = new ArrayList<>();
        Map<Long, TaskSopVO> taskSopVOMap = Maps.newHashMap();
        if (CollectionUtils.isNotEmpty(staTableIds)){
            tbMetaStaTableColumnDOList = tbMetaStaTableColumnMapper.selectColumnListByTableIdList(enterpriseId, staTableIds, Boolean.TRUE);
            List<Long> sopIdList = tbMetaStaTableColumnDOList.stream().map(TbMetaStaTableColumnDO::getSopId).collect(Collectors.toList());
            List<TaskSopVO> taskSopVOList = taskSopService.listByIdList(enterpriseId,sopIdList);
            taskSopVOMap = taskSopVOList.stream().collect(Collectors.toMap(TaskSopVO::getId, t -> t));// t->t 表示对象本身
        }
        Map<Long, List<TbMetaDefTableColumnDO>> defMap = tbMetaDefTableColumnDOList.stream().collect(Collectors.groupingBy(TbMetaDefTableColumnDO::getMetaTableId));
        Map<Long, List<TbMetaStaTableColumnDO>> staMap = tbMetaStaTableColumnDOList.stream().collect(Collectors.groupingBy(TbMetaStaTableColumnDO::getMetaTableId));

        //一个店务有多组 一组有多个检查表
        Map<Integer, StoreWorkDutyGroupInfoVO> groupMappingMap = dutyGroupInfoList.stream().collect(Collectors.toMap(StoreWorkDutyGroupInfoVO::getGroupNum, data -> data, (a, b) -> a));

        for (StoreWorkDutyGroupInfoVO dutyGroupInfoVO : groupMappingMap.values()) {

            List<StoreWorkTableInfoVO> tableInfoList = dutyGroupInfoVO.getTableInfoList();
            //检查表
            for (StoreWorkTableInfoVO storeWorkTableInfoVO : tableInfoList) {
                Long metaTableId = storeWorkTableInfoVO.getMetaTableId();
                TbMetaTableDO tbMetaTableDO = metaTableMap.get(metaTableId);

                storeWorkTableInfoVO.setTableProperty(tbMetaTableDO.getTableProperty());
                storeWorkTableInfoVO.setTableName(tbMetaTableDO.getTableName());
                storeWorkTableInfoVO.setLocked(tbMetaTableDO.getLocked());

                //是否是自定义表
                Boolean tableProperty = TableTypeUtil.isUserDefinedTable(tbMetaTableDO.getTableProperty(), tbMetaTableDO.getTableType());
                List<StoreWorkColumnInfoVO> oldColumnInfoList = storeWorkTableInfoVO.getColumnInfoList();
                Map<Long, StoreWorkColumnInfoVO> oldColumnInfoMap = oldColumnInfoList.stream().collect(Collectors.toMap(StoreWorkColumnInfoVO::getMetaColumnId, Function.identity()));
                List<StoreWorkColumnInfoVO> newColumnInfoList = new ArrayList<>();
                if (tableProperty){
                    List<TbMetaDefTableColumnDO> tbMetaDefTableColumnDOS = defMap.get(metaTableId);
                    tbMetaDefTableColumnDOS.forEach(tbMetaDefTableColumnDO->{
                        StoreWorkColumnInfoVO storeWorkColumnInfoVO = new StoreWorkColumnInfoVO();
                        storeWorkColumnInfoVO.setMetaColumnId(tbMetaDefTableColumnDO.getId());
                        storeWorkColumnInfoVO.setMetaColumnName(tbMetaDefTableColumnDO.getColumnName());
                        storeWorkColumnInfoVO.setDescription(tbMetaDefTableColumnDO.getDescription());
                        storeWorkColumnInfoVO.setCategoryName("");
                        if(MapUtils.isNotEmpty(oldColumnInfoMap) && oldColumnInfoMap.get(tbMetaDefTableColumnDO.getId()) != null){
                            StoreWorkColumnInfoVO temp = oldColumnInfoMap.get(tbMetaDefTableColumnDO.getId());
                            storeWorkColumnInfoVO.setExecuteDemand(temp.getExecuteDemand());
                        }
                        newColumnInfoList.add(storeWorkColumnInfoVO);
                    });
                }else {
                    List<TbMetaStaTableColumnDO> tbMetaStaTableColumnDOS = staMap.get(metaTableId);
                    Map<Long, TaskSopVO> finalTaskSopVOMap = taskSopVOMap;
                    tbMetaStaTableColumnDOS.forEach(tbMetaStaTableColumnDO->{
                        StoreWorkColumnInfoVO storeWorkColumnInfoVO = new StoreWorkColumnInfoVO();
                        storeWorkColumnInfoVO.setCategoryName(tbMetaStaTableColumnDO.getCategoryName());
                        storeWorkColumnInfoVO.setMetaColumnId(tbMetaStaTableColumnDO.getId());
                        storeWorkColumnInfoVO.setMetaColumnName(tbMetaStaTableColumnDO.getColumnName());
                        storeWorkColumnInfoVO.setDescription(tbMetaStaTableColumnDO.getDescription());
                        if(MapUtils.isNotEmpty(oldColumnInfoMap) && oldColumnInfoMap.get(tbMetaStaTableColumnDO.getId()) != null){
                            StoreWorkColumnInfoVO temp = oldColumnInfoMap.get(tbMetaStaTableColumnDO.getId());
                            storeWorkColumnInfoVO.setExecuteDemand(temp.getExecuteDemand());
                        }
                        // sop文档对象
                        storeWorkColumnInfoVO.setTaskSopVO(finalTaskSopVOMap.get(tbMetaStaTableColumnDO.getSopId()));
                        //酷学院课程信息
                        if (StringUtils.isNoneEmpty(tbMetaStaTableColumnDO.getCoolCourse())) {
                            storeWorkColumnInfoVO.setCoolCourseVO(JSON.parseObject(tbMetaStaTableColumnDO.getCoolCourse(), CoolCourseVO.class));
                        }
                        //免费课程信息
                        if (StringUtils.isNoneEmpty(tbMetaStaTableColumnDO.getFreeCourse())) {
                            storeWorkColumnInfoVO.setFreeCourseVO(JSON.parseObject(tbMetaStaTableColumnDO.getFreeCourse(), CoolCourseVO.class));
                        }
                        newColumnInfoList.add(storeWorkColumnInfoVO);
                    });
                }
                storeWorkTableInfoVO.setColumnInfoList(newColumnInfoList);
            }
        }
        storeWorkDetailVO.setDutyInfoList(dutyGroupInfoList);
        return storeWorkDetailVO;
    }

    @Override
    public List<TbMetaStaTableColumnDO> columnList(String enterpriseId, ColumnListRequest request, CurrentUser user) {
//        List<SwStoreWorkDO> swStoreWorkDOS = swStoreWorkDao.selectListByWorkCycle(enterpriseId, request.getWorkCycle(),request.getSwWorkId());
//        if (CollectionUtils.isEmpty(swStoreWorkDOS)){
//            return null;
//        }
//        List<Long> SwStoreWorkDOIds = swStoreWorkDOS.stream().map(SwStoreWorkDO::getId).collect(Collectors.toList());
        List<SwStoreWorkTableMappingDO> swStoreWorkTableMappingDOS = swStoreWorkTableMappingDao.selectListByStoreWorkIdsAndMappingId(enterpriseId, request.getSwWorkId(),request.getTableMappingId());
        if (CollectionUtils.isEmpty(swStoreWorkTableMappingDOS)){
            return null;
        }
        List<Long> swStoreWorkTableMappingDOSIds = swStoreWorkTableMappingDOS.stream().map(SwStoreWorkTableMappingDO::getMetaTableId).collect(Collectors.toList());
        List<TbMetaStaTableColumnDO> detailByIdList = tbMetaStaTableColumnMapper.getDetailByMetaTableIdList(enterpriseId, swStoreWorkTableMappingDOSIds);
        return detailByIdList;
    }

    @Override
    public PageInfo<SwStoreWorkVO> storeWorkList(String eid, StoreWorkSearchRequest storeWorkSearchRequest, CurrentUser user) {
        PageHelper.startPage(storeWorkSearchRequest.getPageNumber(), storeWorkSearchRequest.getPageSize());
        if(storeWorkSearchRequest.getBeginCreateTime() != null){
            storeWorkSearchRequest.setBeginCreateDate(DateUtils.convertTimeToString(storeWorkSearchRequest.getBeginCreateTime(), DateUtils.DATE_FORMAT_SEC));
        }
        if(storeWorkSearchRequest.getEndCreateTime() != null){
            storeWorkSearchRequest.setEndCreateDate(DateUtils.convertTimeToString(storeWorkSearchRequest.getEndCreateTime(), DateUtils.DATE_FORMAT_SEC));
        }
        List<SwStoreWorkDO> storeWorkDOList = swStoreWorkDao.list(eid, storeWorkSearchRequest);
        List<SwStoreWorkVO> resultList = new ArrayList<>();
        if (CollectionUtils.isEmpty(storeWorkDOList)) {
            return new PageInfo<>(resultList);
        }
        List<Long> storeWorkIdList = storeWorkDOList.stream().map(SwStoreWorkDO::getId).collect(Collectors.toList());
        PageInfo pageInfo = new PageInfo<>(storeWorkDOList);
        Set<String> userIdSet = storeWorkDOList.stream()
                .flatMap(c->Stream.of(c.getCreateUserId(),c.getUpdateUserId()))
                .collect(Collectors.toSet());
        // 查询用户
        Map<String, EnterpriseUserDO> userMap = enterpriseUserDao.getUserMap(eid, new ArrayList<>(userIdSet));
        Map<Long, List<StoreWorkCommonDTO>>  storeWorkRangeMap =  storeWorkRangeService.listStoreRange(eid, storeWorkIdList);
        List<Future<SwStoreWorkVO>> futures = new ArrayList<>();
        for (SwStoreWorkDO storeWorkDO : storeWorkDOList) {
            futures.add(EXECUTOR_SERVICE.submit(() -> getSwStoreWorkVOByDO(eid, storeWorkDO, userMap,storeWorkRangeMap, user)));
        }
        for (Future<SwStoreWorkVO> future: futures) {
            try {
                SwStoreWorkVO swStoreWorkVO = future.get();
                if(Objects.isNull(swStoreWorkVO) || swStoreWorkVO.getTotalNum() == 0){
                    continue;
                }
                resultList.add(swStoreWorkVO);
            } catch (Exception e) {
                log.error("转换店务VO失败：", e);
            }
        }
        pageInfo.setList(resultList);
        return pageInfo;
    }

    /**
     * 店务详情
     * @param enterpriseId
     * @param storeWorkId
     * @return
     */
    @Override
    public SwStoreWorkDetailVO getStoreWorkDetail(String enterpriseId, Long storeWorkId) {

        SwStoreWorkDO storeWorkDO = swStoreWorkDao.selectByPrimaryKey(storeWorkId, enterpriseId);
        if (storeWorkDO == null) {
            throw new ServiceException(ErrorCodeEnum.STORE_WORK_NOT_EXIST);
        }
        SwStoreWorkDetailVO storeWorkDetailVO = new SwStoreWorkDetailVO();
        BeanUtils.copyProperties(storeWorkDO, storeWorkDetailVO);
        String personInfo = storeWorkDO.getPersonInfo();
        if (StringUtils.isNotEmpty(personInfo)){
            PersonInfoDTO personInfoDTO = JSONObject.parseObject(personInfo, PersonInfoDTO.class);
            // 填充点评人 职位 人员 名称
//            fillPersonPositionName(enterpriseId, personInfoDTO.getCommentPersonInfo());
            // 填充协作人 职位 人员 名称
            fillPersonPositionName(enterpriseId, personInfoDTO.getCooperatePersonInfo());
            storeWorkDetailVO.setPersonInfo(personInfoDTO);
        }
        Map<Long, List<StoreWorkCommonDTO>> storeWorkRangeMap = storeWorkRangeService.listStoreRange(enterpriseId, Collections.singletonList(storeWorkDO.getId()));
        if(MapUtils.isNotEmpty(storeWorkRangeMap)){
            storeWorkDetailVO.setStoreRangeList(storeWorkRangeMap.get(storeWorkDO.getId()));
        }
        List<StoreWorkDutyGroupInfoVO> dutyGroupInfoList = Lists.newArrayList();
        //店务检查表映射表记录
        List<SwStoreWorkTableMappingDO> allStoreWorkTableMapping = swStoreWorkTableMappingDao.selectListByStoreWorkIds(enterpriseId, Arrays.asList(storeWorkDO.getId()));

        List<Long> metaTableIdList = allStoreWorkTableMapping.stream().map(SwStoreWorkTableMappingDO::getMetaTableId).collect(Collectors.toList());

        List<TbMetaTableDO> tbMetaTableDOS = tbMetaTableMapper.selectByIds(enterpriseId, metaTableIdList);
        //自定义检查表ID
        List<Long> defTableIds = tbMetaTableDOS.stream().filter(x -> MetaTablePropertyEnum.USER_DEFINED_TABLE.getCode().equals(x.getTableProperty()))
                .map(TbMetaTableDO::getId).collect(Collectors.toList());
        List<TbMetaDefTableColumnDO> tbMetaDefTableColumnDOList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(defTableIds)){
            tbMetaDefTableColumnDOList = tbMetaDefTableColumnMapper.getAllColumnByMetaTableIdList(enterpriseId, defTableIds);
        }
        //非自定义检查表ids
        List<Long> staTableIds = tbMetaTableDOS.stream().filter(x -> !MetaTablePropertyEnum.USER_DEFINED_TABLE.getCode().equals(x.getTableProperty()))
                .map(TbMetaTableDO::getId).collect(Collectors.toList());
        List<TbMetaStaTableColumnDO> tbMetaStaTableColumnDOList = new ArrayList<>();
        Map<Long, TaskSopVO> taskSopVOMap = Maps.newHashMap();
        if (CollectionUtils.isNotEmpty(staTableIds)){
            tbMetaStaTableColumnDOList = tbMetaStaTableColumnMapper.selectColumnListByTableIdList(enterpriseId, staTableIds, Boolean.TRUE);
            List<Long> sopIdList = tbMetaStaTableColumnDOList.stream().map(TbMetaStaTableColumnDO::getSopId).collect(Collectors.toList());
            List<TaskSopVO> taskSopVOList = taskSopService.listByIdList(enterpriseId,sopIdList);
            taskSopVOMap = taskSopVOList.stream().collect(Collectors.toMap(TaskSopVO::getId, t -> t));// t->t 表示对象本身
        }
        Map<Long, List<TbMetaDefTableColumnDO>> defMap = tbMetaDefTableColumnDOList.stream().collect(Collectors.groupingBy(TbMetaDefTableColumnDO::getMetaTableId));
        Map<Long, List<TbMetaStaTableColumnDO>> staMap = tbMetaStaTableColumnDOList.stream().collect(Collectors.groupingBy(TbMetaStaTableColumnDO::getMetaTableId));

        //一个店务有多组 一组有多个检查表
        Map<Integer, List<SwStoreWorkTableMappingDO>> groupMappingMap = allStoreWorkTableMapping.stream().collect(Collectors.groupingBy(SwStoreWorkTableMappingDO::getGroupNum));
        for (List<SwStoreWorkTableMappingDO> storeWorkTableMappingList : groupMappingMap.values()) {
            StoreWorkDutyGroupInfoVO dutyGroupInfoVO = new StoreWorkDutyGroupInfoVO();
            dutyGroupInfoVO.setGroupNum(storeWorkTableMappingList.get(0).getGroupNum());
            //每个小组的处理人都相同，取每组的第一个检查表的handlePersonInfo信息
            String handlePersonInfo = storeWorkTableMappingList.get(0).getHandlePersonInfo();
            List<StoreWorkCommonDTO> handlePersonDTOS = JSONObject.parseArray(handlePersonInfo, StoreWorkCommonDTO.class);
            fillPersonPositionName(enterpriseId, handlePersonDTOS);
            dutyGroupInfoVO.setHandlePersonInfo(handlePersonDTOS);

            String commentPersonInfo = storeWorkTableMappingList.get(0).getCommentPersonInfo();
            if (StringUtils.isNotEmpty(commentPersonInfo)) {
                List<StoreWorkCommonDTO> commentPersonDTOS = JSONObject.parseArray(commentPersonInfo, StoreWorkCommonDTO.class);
                fillPersonPositionName(enterpriseId, commentPersonDTOS);
                dutyGroupInfoVO.setCommentPersonInfo(commentPersonDTOS);
            }

            List<StoreWorkTableInfoVO> tableInfoList = Lists.newArrayList();

            List<Long> currentGroupingMetaTableIds = storeWorkTableMappingList.stream().map(SwStoreWorkTableMappingDO::getMetaTableId).collect(Collectors.toList());
            //当前分组检查表
            List<TbMetaTableDO> currentGroupingMetaTable = tbMetaTableDOS.stream().filter(x -> currentGroupingMetaTableIds.contains(x.getId())).collect(Collectors.toList());
            Map<Long, TbMetaTableDO> metaTableMap = currentGroupingMetaTable.stream().collect(Collectors.toMap(TbMetaTableDO::getId, x -> x));
            //检查表
            for (SwStoreWorkTableMappingDO storeWorkTableMappingDO : storeWorkTableMappingList) {
                Long metaTableId = storeWorkTableMappingDO.getMetaTableId();
                TbMetaTableDO tbMetaTableDO = metaTableMap.get(metaTableId);
                StoreWorkTableInfoVO  storeWorkTableInfoVO = new StoreWorkTableInfoVO();
                storeWorkTableInfoVO.setBeginDate(storeWorkTableMappingDO.getBeginDate());
                storeWorkTableInfoVO.setBeginTime(storeWorkTableMappingDO.getBeginTime());
                storeWorkTableInfoVO.setLimitHour(storeWorkTableMappingDO.getLimitHour());
                storeWorkTableInfoVO.setMetaTableId(storeWorkTableMappingDO.getMetaTableId());
                storeWorkTableInfoVO.setDutyName(storeWorkTableMappingDO.getDutyName());
                storeWorkTableInfoVO.setGroupNum(storeWorkTableMappingDO.getGroupNum());
                storeWorkTableInfoVO.setTableMappingId(storeWorkTableMappingDO.getId());
                storeWorkTableInfoVO.setTableInfo(storeWorkTableMappingDO.getTableInfo());
                storeWorkTableInfoVO.setTableProperty(tbMetaTableDO.getTableProperty());
                storeWorkTableInfoVO.setTableName(tbMetaTableDO.getTableName());
                storeWorkTableInfoVO.setLocked(tbMetaTableDO.getLocked());
                //是否是自定义表
                Boolean tableProperty = TableTypeUtil.isUserDefinedTable(tbMetaTableDO.getTableProperty(), tbMetaTableDO.getTableType());
                List<StoreWorkColumnInfoVO> columnInfoList = new ArrayList<>();
                if (tableProperty){
                    List<TbMetaDefTableColumnDO> tbMetaDefTableColumnDOS = defMap.get(metaTableId);
                    tbMetaDefTableColumnDOS.forEach(tbMetaDefTableColumnDO->{
                        StoreWorkColumnInfoVO storeWorkColumnInfoVO = new StoreWorkColumnInfoVO();
                        storeWorkColumnInfoVO.setMetaColumnId(tbMetaDefTableColumnDO.getId());
                        storeWorkColumnInfoVO.setMetaColumnName(tbMetaDefTableColumnDO.getColumnName());
                        storeWorkColumnInfoVO.setDescription(tbMetaDefTableColumnDO.getDescription());
                        storeWorkColumnInfoVO.setCategoryName("");
                        columnInfoList.add(storeWorkColumnInfoVO);
                    });
                }else {
                    List<TbMetaStaTableColumnDO> tbMetaStaTableColumnDOS = staMap.get(metaTableId);
                    Map<Long, TaskSopVO> finalTaskSopVOMap = taskSopVOMap;
                    tbMetaStaTableColumnDOS.forEach(tbMetaStaTableColumnDO->{
                        StoreWorkColumnInfoVO storeWorkColumnInfoVO = new StoreWorkColumnInfoVO();
                        storeWorkColumnInfoVO.setCategoryName(tbMetaStaTableColumnDO.getCategoryName());
                        storeWorkColumnInfoVO.setMetaColumnId(tbMetaStaTableColumnDO.getId());
                        storeWorkColumnInfoVO.setMetaColumnName(tbMetaStaTableColumnDO.getColumnName());
                        storeWorkColumnInfoVO.setDescription(tbMetaStaTableColumnDO.getDescription());
                        storeWorkColumnInfoVO.setIsAiCheck(tbMetaStaTableColumnDO.getIsAiCheck());
                        storeWorkColumnInfoVO.setExecuteDemand(JSONObject.parseArray(tbMetaStaTableColumnDO.getExecuteDemand(), Boolean.class));
                        // sop文档对象
                        storeWorkColumnInfoVO.setTaskSopVO(finalTaskSopVOMap.get(tbMetaStaTableColumnDO.getSopId()));
                        //酷学院课程信息
                        if (StringUtils.isNoneEmpty(tbMetaStaTableColumnDO.getCoolCourse())) {
                            storeWorkColumnInfoVO.setCoolCourseVO(JSON.parseObject(tbMetaStaTableColumnDO.getCoolCourse(), CoolCourseVO.class));
                        }
                        //免费课程信息
                        if (StringUtils.isNoneEmpty(tbMetaStaTableColumnDO.getFreeCourse())) {
                            storeWorkColumnInfoVO.setFreeCourseVO(JSON.parseObject(tbMetaStaTableColumnDO.getFreeCourse(), CoolCourseVO.class));
                        }
                        columnInfoList.add(storeWorkColumnInfoVO);
                    });
                }
                storeWorkTableInfoVO.setColumnInfoList(columnInfoList);
                tableInfoList.add(storeWorkTableInfoVO);
            }
            dutyGroupInfoVO.setTableInfoList(tableInfoList);
            dutyGroupInfoList.add(dutyGroupInfoVO);
        }
        storeWorkDetailVO.setDutyInfoList(dutyGroupInfoList);
        String createUserName = enterpriseUserDao.selectNameIgnoreActiveByUserId(enterpriseId, storeWorkDO.getCreateUserId());
        storeWorkDetailVO.setCreateUserName(createUserName);
        if (Constants.SYSTEM_USER_ID.equals(storeWorkDO.getCreateUserId())) {
            storeWorkDetailVO.setCreateUserName(Constants.SYSTEM_USER_NAME);
        }
        JSONObject aiRange = JSONObject.parseObject(storeWorkDO.getAiRange());
        if (Objects.nonNull(aiRange)) {
            storeWorkDetailVO.setNotGenerateRange(aiRange.getObject(Constants.STORE_WORK_AI.NOT_GENERATE_RANGE, StoreWorkDateRangeDTO.class));
            storeWorkDetailVO.setUseAiRange(aiRange.getObject(Constants.STORE_WORK_AI.USE_AI_RANGE, StoreWorkDateRangeDTO.class));
            List<StoreWorkCommonDTO> aiStoreRange = Optional.ofNullable(aiRange.getJSONArray(Constants.STORE_WORK_AI.AI_STORE_RANGE))
                    .map(v -> v.toJavaList(StoreWorkCommonDTO.class)).orElse(Collections.emptyList());
            storeWorkDetailVO.setAiStoreRange(aiStoreRange);
            storeWorkDetailVO.setAiStoreRangeMethod(aiRange.getString(Constants.STORE_WORK_AI.AI_STORE_RANGE_METHOD));
        }
        return storeWorkDetailVO;
    }

    @Override
    public void stopStoreWork(String enterpriseId, Long storeWorkId) {
        SwStoreWorkDO storeWorkDO = swStoreWorkDao.selectByPrimaryKey(storeWorkId, enterpriseId);
        if (storeWorkDO == null) {
            throw new ServiceException(ErrorCodeEnum.STORE_WORK_NOT_EXIST);
        }
        swStoreWorkDao.updateStatusByStoreWorkId(enterpriseId, StoreWorkStatusEnum.STOP.getCode(), storeWorkId);
    }

    @Override
    public void delStoreWork(String enterpriseId, Long storeWorkId, String appType, String dingCorpId, CurrentUser user) {

        SwStoreWorkDO storeWorkDO = swStoreWorkDao.selectByPrimaryKey(storeWorkId, enterpriseId);
        if (storeWorkDO == null) {
            throw new ServiceException(ErrorCodeEnum.STORE_WORK_NOT_EXIST);
        }
        /*
        // 删除店务权限校验（只有创建人、管理员可以删除）
        String currentUserId = user.getUserId();
        // 是否创建人
        boolean isCreateUser = currentUserId.equals(storeWorkDO.getCreateUserId());
        // 是否管理员
        boolean isAdmin = sysRoleService.checkIsAdmin(enterpriseId, currentUserId);
        if (!isCreateUser && !isAdmin) {
            throw new ServiceException(ErrorCodeEnum.STORE_WORK_DELETE_AUTH);
        }*/
        StoreWorkSubmitCommentMsgData storeWorkSubmitCommentMsgData = new StoreWorkSubmitCommentMsgData();
        storeWorkSubmitCommentMsgData.setEnterpriseId(enterpriseId);
        storeWorkSubmitCommentMsgData.setStoreWorkId(storeWorkId);
        simpleMessageService.send(JSONObject.toJSONString(storeWorkSubmitCommentMsgData), RocketMqTagEnum.STOREWORK_DELETE_DATA_QUEUE);
    }

    @Override
    public void delStoreWorkTableData(String enterpriseId, Long storeWorkId, String appType, String dingCorpId) {

        EnterpriseStoreWorkSettingsDTO storeWorkSetting = enterpriseSettingRpcService.getStoreWorkSetting(enterpriseId);
        SwStoreWorkDO storeWorkDO = swStoreWorkDao.selectByPrimaryKey(storeWorkId, enterpriseId);
        if (storeWorkDO == null) {
            return;
        }
        // 删除检查表映射
        swStoreWorkTableMappingDao.delTableMappingByStoreWorkId(enterpriseId, storeWorkId);
        // 删除门店范围
        swStoreWorkRangeDao.delStoreRangeByStoreWorkId(enterpriseId, storeWorkId);
        // 删除店务表
        swStoreWorkDao.deleteByPrimaryKey(storeWorkId, enterpriseId);
        if(storeWorkSetting.getStartWorkRemind()){
            boolean hasNext = true;
            int pageSize = 100;
            int pageNum = 1;
            while(hasNext){
                PageHelper.startPage(pageNum, pageSize);
                List<SwStoreWorkDataTableDO> storeWorkDataTableDOList = swStoreWorkDataTableDao.listNotCompleteDataTableByStoreWorkId(enterpriseId, storeWorkId);
                PageHelper.clearPage();
                hasNext = storeWorkDataTableDOList.size() >= pageSize;
                if(CollectionUtils.isEmpty(storeWorkDataTableDOList)){
                    break;
                }
                pageNum++;
                //删除店务待办
                storeWorkDataTableDOList.forEach(f -> {
                    // 未完成取消待办
                    cancelUpcoming(enterpriseId, dingCorpId, appType, f.getId(), StoreWorkNoticeEnum.BEFORE_START_REMIND.getOperate());
                });
            }
        }
        // 删除数据项
        swStoreWorkDataTableColumnDao.updateDelByStoreWorkId(enterpriseId, storeWorkId);
        // 删除数据表
        swStoreWorkDataTableDao.updateDelByStoreWorkId(enterpriseId, storeWorkId);
        // 删除店务记录
        swStoreWorkRecordDao.updateDelByStoreWorkId(enterpriseId, storeWorkId);
    }


    @Override
    public PageInfo<StoreDayClearDataVO> getCurrentUserStoreWorkData(String enterpriseId,CurrentUser user, StoreWorkClearDetailRequest storeWorkClearDetailRequest) {
        //查询当前人员门店权限
        List<String> regionPathList = this.queryRegionPath(enterpriseId, user, storeWorkClearDetailRequest);
        //默认分页参数
        if (storeWorkClearDetailRequest.getPageNumber()==null||storeWorkClearDetailRequest.getPageSize()==null){
            storeWorkClearDetailRequest.setPageNumber(1);
            storeWorkClearDetailRequest.setPageSize(10);
        }
        PageHelper.startPage(storeWorkClearDetailRequest.getPageNumber(),storeWorkClearDetailRequest.getPageSize());
        //权限下每个门店的数据(按执行人完成率排序)
        String queryDate = DateUtil.format(storeWorkClearDetailRequest.getStoreWorkDate(), DateUtils.DATE_FORMAT_DAY);

        PageInfo<SwStoreWorkRecordDO> swStoreWorkRecordDOPageInfo = new PageInfo<>();
        if (storeWorkClearDetailRequest.getMetaTableId() == null){
            swStoreWorkRecordDOPageInfo = swStoreWorkRecordDao.selectStoreWorkRecord(enterpriseId,queryDate, storeWorkClearDetailRequest, regionPathList);
        }else{
            //sw_store_work_data_table_
            swStoreWorkRecordDOPageInfo = swStoreWorkDataTableDao.selectSwStoreWorkDataTableById(enterpriseId,queryDate,storeWorkClearDetailRequest,regionPathList);
        }
        List<StoreDayClearDataVO> result = new ArrayList<>();
        swStoreWorkRecordDOPageInfo.getList().forEach(x->{
            StoreDayClearDataVO storeDayClearDataVO = new StoreDayClearDataVO();
            storeDayClearDataVO.setStoreId(x.getStoreId());
            storeDayClearDataVO.setFinishColumnNum(x.getFinishColumnNum());
            storeDayClearDataVO.setTableNum(x.getTableNum());
            storeDayClearDataVO.setBusinessId(x.getTcBusinessId());
            storeDayClearDataVO.setStoreWorkId(x.getStoreWorkId());
            storeDayClearDataVO.setWorkCycle(x.getWorkCycle());
            storeDayClearDataVO.setStoreWorkDate(x.getStoreWorkDate());
            storeDayClearDataVO.setStoreName(x.getStoreName());
            storeDayClearDataVO.setCommentTableNum(x.getCommentTableNum());
            storeDayClearDataVO.setEndHandleTime(x.getEndHandleTime());
            storeDayClearDataVO.setTotalColumnNum(x.getTotalColumnNum());
            String finishRateStr = "0%";
            if (x.getTotalColumnNum()!=0){
                BigDecimal divide = new BigDecimal(x.getFinishColumnNum()).multiply(new BigDecimal(100)).divide(new BigDecimal(x.getTotalColumnNum()), 2, BigDecimal.ROUND_HALF_UP);
                finishRateStr = String.format("%s%%", divide);
            }
            storeDayClearDataVO.setHandlerCompleteRate(finishRateStr);

            result.add(storeDayClearDataVO);
        });
        List<String> ids = swStoreWorkRecordDOPageInfo.getList().stream().map(c -> c.getTcBusinessId()).collect(Collectors.toList());
        List<SwStoreWorkDataTableDO> swStoreWorkDataTableDOS = swStoreWorkDataTableDao.selectSwStoreWorkDataTableByBusinessIds(enterpriseId, ids);
        if (CollectionUtils.isNotEmpty(swStoreWorkDataTableDOS)){
            Map<String, List<SwStoreWorkDataTableDO>> map = swStoreWorkDataTableDOS.stream().collect(Collectors.groupingBy(c -> c.getTcBusinessId()));
            result.stream().forEach(c-> {
                        List<SwStoreWorkDataTableDO> swStoreWorkDataTableDOS1 = map.get(c.getBusinessId());
                        Long count = swStoreWorkDataTableDOS1.stream().filter(d -> d.getCompleteStatus() == 1).collect(Collectors.counting());
                        c.setFinishTableNum(count);
                        List<StoreWorkTableSimpleInfoVO> storeWorkTableInfoList = new ArrayList<>();
                        for (SwStoreWorkDataTableDO tableDO : swStoreWorkDataTableDOS1) {
                            StoreWorkTableSimpleInfoVO infoVO = new StoreWorkTableSimpleInfoVO();
                            infoVO.setTableName(tableDO.getTableName());
                            infoVO.setDataTableId(tableDO.getId());
                            BigDecimal score = tableDO.getScore();
                            BigDecimal totalScore = tableDO.getTotalScore();
                            infoVO.setScore(score);
                            infoVO.setTotalScore(totalScore);
                            if (totalScore.compareTo(BigDecimal.ZERO) == 0) {
                                // 处理总分为0的情况，避免除以0错误
                                infoVO.setScoreRate("0.0%");
                            } else {
                                // 计算得分率并添加百分号
                                BigDecimal scoreRate = score.divide(totalScore, 4, RoundingMode.HALF_UP); // 不进行四舍五入，保留两位小数
                                scoreRate = scoreRate.multiply(BigDecimal.valueOf(100)); // 乘以100
                                String formattedScoreRate = scoreRate.setScale(2) + "%"; // 设置小数位数为两位，然后附加百分号
                                infoVO.setScoreRate(formattedScoreRate);
                            }
                            Integer passColumnNum = tableDO.getPassColumnNum();
                            Integer totalColumnNum = tableDO.getTotalCalColumnNum();
                            if (totalColumnNum == 0) {
                                // 处理总列数为0的情况，避免除以0错误
                                infoVO.setPassRate("0.0%");
                            } else {
                                // 计算合格率并添加百分号
                                double passRate = (double) passColumnNum / totalColumnNum * 100;
                                String formattedPassRate = String.format("%.2f%%", passRate);
                                infoVO.setPassRate(formattedPassRate);
                            }
                            infoVO.setPassColumnNum(passColumnNum);
                            infoVO.setTotalColumnNum(totalColumnNum);
                            infoVO.setCommentStatus(tableDO.getCommentStatus());
                            infoVO.setCompleteStatus(tableDO.getCompleteStatus());
                            //格式化为小时分钟
                            String beginTimeStr = DateUtil.format(tableDO.getBeginTime(), DateUtils.TIME_FORMAT_SEC2);
                            String endTimeStr = DateUtil.format(tableDO.getEndTime(), DateUtils.TIME_FORMAT_SEC2);
                            infoVO.setBeginTime(beginTimeStr);
                            infoVO.setEndTime(endTimeStr);
                            infoVO.setMetaTableId(tableDO.getMetaTableId());
                            storeWorkTableInfoList.add(infoVO);
                        }
                        c.setStoreWorkTableInfoList(storeWorkTableInfoList);
                    }
            );
        }
        PageInfo<StoreDayClearDataVO> resultPageInfo = new PageInfo<>();
        resultPageInfo.setList(result);
        resultPageInfo.setTotal(swStoreWorkRecordDOPageInfo.getTotal());
        return resultPageInfo;
    }

    @Override
    public PageInfo<StoreWorkDataDTO> getCurrentUserStoreWorkNoCommentData(String enterpriseId, CurrentUser user, StoreWorkClearDetailRequest storeWorkClearDetailRequest) {
        //只查询门店未点评的数据
        storeWorkClearDetailRequest.setCommentStatus(0);
        //查询当前人员门店权限
        List<String> regionPathList = this.queryRegionPath(enterpriseId, user, storeWorkClearDetailRequest);
        //默认分页参数
        if (storeWorkClearDetailRequest.getPageNumber()==null||storeWorkClearDetailRequest.getPageSize()==null){
            storeWorkClearDetailRequest.setPageNumber(1);
            storeWorkClearDetailRequest.setPageSize(10);
        }
        PageHelper.startPage(storeWorkClearDetailRequest.getPageNumber(),storeWorkClearDetailRequest.getPageSize());
        //权限下每个门店的数据(按执行人完成率排序)
        String queryDate = DateUtil.format(storeWorkClearDetailRequest.getStoreWorkDate(), DateUtils.DATE_FORMAT_DAY);
        PageInfo<SwStoreWorkRecordDO> swStoreWorkRecordDOPageInfo = swStoreWorkRecordDao.selectStoreWorkRecord(enterpriseId,queryDate, storeWorkClearDetailRequest, regionPathList);
        if (CollectionUtils.isEmpty(swStoreWorkRecordDOPageInfo.getList())){
            return null;
        }
        List<String> businessIds = swStoreWorkRecordDOPageInfo.getList().stream().map(SwStoreWorkRecordDO::getTcBusinessId).collect(Collectors.toList());
        List<SwStoreWorkDataTableDO> swStoreWorkDataTableDOS = swStoreWorkDataTableDao.selectSwStoreWorkDataTableNoCommentByBusinessIds(enterpriseId, businessIds, null);
        swStoreWorkDataTableDOS = swStoreWorkDataTableDOS.stream().filter(x->((!MetaTablePropertyEnum.USER_DEFINED_TABLE.getCode().equals(x.getTableProperty())))&&(x.getCompleteStatus()==1||(x.getCompleteStatus()==0&&x.getEndTime().getTime()<System.currentTimeMillis()))).collect(Collectors.toList());
        Map<String, List<SwStoreWorkDataTableDO>> map = swStoreWorkDataTableDOS.stream().collect(Collectors.groupingBy(SwStoreWorkDataTableDO::getStoreId));
        List<StoreWorkDataDTO> result = new ArrayList<>();
        swStoreWorkRecordDOPageInfo.getList().forEach(x->{
            StoreWorkDataDTO storeWorkDataDTO = new StoreWorkDataDTO();
            storeWorkDataDTO.setStoreId(x.getStoreId());
            storeWorkDataDTO.setStoreName(x.getStoreName());
            List<SwStoreWorkDataTableDO> sw = map.get(x.getStoreId());
            if (CollectionUtils.isNotEmpty(sw)){
                storeWorkDataDTO.setDataTableIds(sw.stream().map(SwStoreWorkDataTableDO::getId).collect(Collectors.toList()));
                result.add(storeWorkDataDTO);
            }
        });
        PageInfo<StoreWorkDataDTO> resultPageInfo = new PageInfo<>();
        resultPageInfo.setList(result);
        resultPageInfo.setTotal(swStoreWorkRecordDOPageInfo.getTotal());
        return resultPageInfo;
    }

    @Override
    public StoreWorkRecordStatisticsVO getStoreWorkRecordStatistics(String enterpriseId, CurrentUser user, StoreWorkClearDetailRequest storeWorkClearDetailRequest) {
        //查询当前人员门店权限
        if(storeWorkClearDetailRequest.getStoreWorkDate()==null||storeWorkClearDetailRequest.getWorkCycle()==null){
            throw  new ServiceException(ErrorCodeEnum.PARAM_MISSING);
        }
        List<String> regionPathList = this.queryRegionPath(enterpriseId, user, storeWorkClearDetailRequest);
        String queryDate = DateUtil.format(storeWorkClearDetailRequest.getStoreWorkDate(), DateUtils.DATE_FORMAT_DAY);
        StoreWorkRecordStatisticsVO storeWorkRecordStatistics = swStoreWorkRecordDao.getStoreWorkRecordStatistics(enterpriseId, queryDate, storeWorkClearDetailRequest, regionPathList);
        storeWorkRecordStatistics.setStoreCompleteRate(NumberFormatUtils.getPercentString(storeWorkRecordStatistics.getCompleteStoreNum(),
                storeWorkRecordStatistics.getNoCompleteStoreNum()+storeWorkRecordStatistics.getCompleteStoreNum()));
        return storeWorkRecordStatistics;
    }

    /**
     * 查询人员区域权限
     * @param enterpriseId
     * @param currentUser
     * @param request
     * @return
     */
    public List<String> queryRegionPath(String enterpriseId,CurrentUser currentUser, StoreWorkClearDetailRequest request){
        List<String> regionIds = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(request.getStoreIds())){
            List<RegionDO> regionDOS = regionService.listRegionByStoreIds(enterpriseId, request.getStoreIds());
            for (RegionDO regionDO : regionDOS) {
                regionIds.add(String.valueOf(regionDO.getId()));
            }
        }
        List<String> list = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(request.getRegionIds())||CollectionUtils.isNotEmpty(regionIds)){
            if (CollectionUtils.isNotEmpty(request.getRegionIds())){
                regionIds.addAll(request.getRegionIds());
            }
            List<RegionPathDTO> regionPathList = regionService.getRegionPathByList(enterpriseId,regionIds);
            if(CollectionUtils.isNotEmpty(regionPathList)){
                list =  regionPathList.stream().map(RegionPathDTO::getRegionPath).collect(Collectors.toList());
            }else {
                list =  Collections.singletonList(Constants.STORE_PATH_SPILT + request.getRegionIds().get(0) + Constants.STORE_PATH_SPILT);
            }
        }else if(currentUser!= null){
            list =   getUserRegionPathList(enterpriseId, null, currentUser,null);
        }
        return list;
    }

    /**
     * 根据人员区域权限，查询当前 人员区域路径集合
     * @param enterpriseId
     * @param regionId
     * @param currentUser
     * @param storeIdList
     * @return
     */
    private List<String> getUserRegionPathList(String enterpriseId, String regionId, CurrentUser currentUser,List<String> storeIdList) {
        List<String> regionPathList = new ArrayList<>();
        SysRoleDO currUserRole = currentUser.getSysRoleDO();
        if (currUserRole != null && !AuthRoleEnum.ALL.getCode().equals(currUserRole.getRoleAuth()) && CollectionUtils.isEmpty(storeIdList)
                && StringUtils.isBlank(regionId)) {
            List<UserAuthMappingDO> region = new ArrayList<>();
            List<UserAuthMappingDO> userAuthMappingList = userAuthMappingMapper.listUserAuthMappingByUserId(enterpriseId, currentUser.getUserId());
            ListUtils.emptyIfNull(userAuthMappingList)
                    .forEach(data -> {
                        if (data.getType().equals(UserAuthMappingTypeEnum.REGION.getCode())) {
                            region.add(data);
                        }
                    });
            List<String> regionIdList = ListUtils.emptyIfNull(region).stream()
                    .map(UserAuthMappingDO::getMappingId).distinct().filter(Objects::nonNull).collect(Collectors.toList());

            if (CollectionUtils.isNotEmpty(regionIdList)) {
                List<RegionPathDTO> regionPathByList = regionService.getRegionPathByList(enterpriseId, regionIdList);
                regionPathList = ListUtils.emptyIfNull(regionPathByList)
                        .stream()
                        .map(RegionPathDTO::getRegionPath)
                        .collect(Collectors.toList());
            }
            //如果查询用户权限为空，则不能查询数据
            if(CollectionUtils.isEmpty(regionPathList)){
                regionPathList.add(Constants.ROOT_DELETE_REGION_PATH);
            }
        }
        //如果只有一条记录，且为根节点，则返回空查询全部
        if (CollectionUtils.isNotEmpty(regionPathList) && regionPathList.size() == 1 && Constants.ROOT_REGION_PATH.equals(regionPathList.get(0))) {
            return new ArrayList<>();
        }
        return regionPathList;
    }

    public void cancelUpcoming(String enterpriseId, String dingCorpId,String appType, Long dataTableId, String operate) {
        //重新分配的时候处理待办
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("enterpriseId", enterpriseId);
        jsonObject.put("corpId", dingCorpId);
        jsonObject.put("taskKey", DingMsgEnum.STOREWORK.getDesc() + "_" + operate + "_" + dataTableId);
        jsonObject.put("appType",appType);
        simpleMessageService.send(jsonObject.toJSONString(), RocketMqTagEnum.UPCOMING_FINISH);
    }

    // 填充职位 人员 名称
    @Override
    public void fillPersonPositionName(String enterpriseId, List<StoreWorkCommonDTO> personPositionList) {

        List<String> personIdList = personPositionList.stream().filter(x -> x.getType().equals(UnifyTaskConstant.PersonType.PERSON)).map(StoreWorkCommonDTO::getValue).collect(Collectors.toList());
        List<String> positionIdList = personPositionList.stream().filter(x -> x.getType().equals(UnifyTaskConstant.PersonType.POSITION)).map(StoreWorkCommonDTO::getValue).collect(Collectors.toList());
        List<String> groupIdList = personPositionList.stream().filter(x -> x.getType().equals(UnifyTaskConstant.PersonType.USER_GROUP)).map(StoreWorkCommonDTO::getValue).collect(Collectors.toList());
        List<String> regionIdList = personPositionList.stream().filter(x -> x.getType().equals(UnifyTaskConstant.PersonType.ORGANIZATION)).map(StoreWorkCommonDTO::getValue).collect(Collectors.toList());

        Map<Long, String> roleDOMap = Maps.newHashMap();
        if (CollectionUtils.isNotEmpty(positionIdList)) {
            List<SysRoleDO> roleDOList = sysRoleMapper.selectRoleByIdList(enterpriseId, positionIdList);
            roleDOMap = roleDOList.stream().filter(a -> a.getRoleName() != null && a.getId() != null)
                    .collect(Collectors.toMap(SysRoleDO::getId, SysRoleDO::getRoleName));
        }
        Map<String, String> allNodeUserDOMap = enterpriseUserDao.getUserNameMap(enterpriseId, personIdList);

        List<EnterpriseUserGroupDO> userGroupDOList = enterpriseUserGroupDao.listByGroupIdList(enterpriseId, groupIdList);
        Map<String, String> groupNameMap = userGroupDOList.stream()
                .filter(a -> a.getId() != null && a.getGroupName() != null)
                .collect(Collectors.toMap(EnterpriseUserGroupDO::getGroupId, EnterpriseUserGroupDO::getGroupName, (a, b) -> a));

        Map<String, String> regionNameMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(regionIdList)) {
            //查看是否是老企业
            boolean historyEnterprise = enterpriseService.isHistoryEnterprise(enterpriseId);
            if (historyEnterprise) {
                List<Long> collect = regionIdList.stream().map(s -> Long.parseLong(s.trim()))
                        .collect(Collectors.toList());
                List<DeptNode> depListByDepName = sysDepartmentMapper.getDepListByDepName(enterpriseId, null, collect);
                regionNameMap = depListByDepName.stream().collect(Collectors.toMap(DeptNode::getId, DeptNode::getDepartmentName));
            } else {
                List<RegionDO> regionDOList = regionMapper.getRegionByRegionIds(enterpriseId, regionIdList);
                regionNameMap = regionDOList.stream()
                        .filter(a -> a.getId() != null && a.getName() != null)
                        .collect(Collectors.toMap(data -> String.valueOf(data.getId()), RegionDO::getName, (a, b) -> a));
            }
        }

        Map<Long, String> finalRoleDOMap = roleDOMap;
        Map<String, String> finalAllNodeUserDOMap = allNodeUserDOMap;
        Map<String, String> finalRegionNameMap = regionNameMap;
        personPositionList.forEach(personPosition -> {
            if(UnifyTaskConstant.PersonType.POSITION.equals(personPosition.getType())){
                personPosition.setName(finalRoleDOMap.get(Long.parseLong(personPosition.getValue())));
            }
            if(UnifyTaskConstant.PersonType.PERSON.equals(personPosition.getType())){
                personPosition.setName(finalAllNodeUserDOMap.get(personPosition.getValue()));
            }
            if(UnifyTaskConstant.PersonType.USER_GROUP.equals(personPosition.getType())){
                personPosition.setName(groupNameMap.get(personPosition.getValue()));
            }
            if(UnifyTaskConstant.PersonType.ORGANIZATION.equals(personPosition.getType())){
                personPosition.setName(finalRegionNameMap.get(personPosition.getValue()));
            }
        });
    }

    /**
     * 店务催办
     * @param enterpriseId
     * @param storeWorkDataListRequest
     * @param appType
     * @return
     */
    @Override
    public ResponseResult<List<String>> storeWorkRemind(String enterpriseId, StoreWorkDataListRequest storeWorkDataListRequest, String appType) {
        //钉钉类型的企业返回需要催办的人即可，其他类型的企业，发送通知
        //先查询对应节点的人
        List<String> pendingUserList = Lists.newArrayList();
        // 查未完成的处理人
        storeWorkDataListRequest.setCompleteStatus(StoreWorkFinishStatusEnum.NO.getCode());
        List<SwStoreWorkDataTableDO>  storeWorkDataTableDOList = swStoreWorkDataTableDao.listNeedRemindDataTable(enterpriseId, storeWorkDataListRequest);
        storeWorkDataTableDOList.forEach(x->{
            List<String> handleUserIdList = Arrays.asList(StringUtils.split(x.getHandleUserIds(), Constants.COMMA));
            pendingUserList.addAll(handleUserIdList);
        });
        // 去重
        List<String> distinctHandleUserIds = pendingUserList.stream()
                .distinct().collect(Collectors.toList());
        if (CollectionUtils.isEmpty(distinctHandleUserIds)) {
            return ResponseResult.fail(ErrorCodeEnum.NO_NEED_REMIND_HANDLE_USER);
            // throw new ServiceException(ErrorCodeEnum.NO_NEED_REMIND_HANDLE_USER);
        }
        if(AppTypeEnum.isDingType(appType)){
            return ResponseResult.success(distinctHandleUserIds);
        }
        String cycleName = StoreWorkCycleEnum.getByCode(storeWorkDataTableDOList.get(0).getWorkCycle());
        String title = cycleName;
        
        String content = "您的【{0}】的【{1}】未完成，请尽快处理。";
        String timeRange = TableInfoLabelUtil.getTimeRange(storeWorkDataTableDOList.get(0).getStoreWorkDate(), storeWorkDataTableDOList.get(0).getWorkCycle());
        content = MessageFormat.format(content, timeRange, cycleName);
        jmsTaskService.sendStoreWorkReminder(enterpriseId, storeWorkDataListRequest.getStoreWorkId(), distinctHandleUserIds, title, content);
        return ResponseResult.success(distinctHandleUserIds);
    }

    @Override
    public EnterpriseStoreWorkSettingsDTO getEnterpriseStoreWorkSetting(String enterpriseId) {
        EnterpriseStoreWorkSettingsDTO storeWorkSetting = enterpriseSettingRpcService.getStoreWorkSetting(enterpriseId);
        return storeWorkSetting;
    }

    @Override
    public void cancelUpcomingWhenDel(String enterpriseId, Long storeWorkId, String appType, String dingCorpId, CurrentUser user) {

        List<SwStoreWorkDataTableDO> storeWorkDataTableDOList = swStoreWorkDataTableDao.listDataTableHasDelByStoreWorkId(enterpriseId, storeWorkId);
        //删除店务待办
        storeWorkDataTableDOList.forEach(f -> {
            // 未完成取消待办
            if(StoreWorkFinishStatusEnum.NO.getCode().equals(f.getCompleteStatus())){
                cancelUpcoming(enterpriseId, dingCorpId, appType, f.getId(), StoreWorkNoticeEnum.BEFORE_START_REMIND.getOperate());
            }
        });
    }

    @Override
    public Boolean fixCommentUser(List<String> enterpriseIds) {
        DataSourceHelper.reset();
        List<EnterpriseConfigDO> configDOS = enterpriseConfigMapper.selectByEnterpriseIds(enterpriseIds);
        for (EnterpriseConfigDO configDO : configDOS) {
            DataSourceHelper.changeToSpecificDataSource(configDO.getDbName());
            //修正企业的数据
            fixCommentUserSingle(configDO.getEnterpriseId());
        }
        log.info("修正完毕");
        return true;
    }
    // 修改已生成 店务的处理人和点评人
    public void singStoreWorkUpdate(String enterpriseId, String tcBusinessId, String storeId,
                              Long storeWorkId, Boolean reissueFlag){
        //校验任务是否执行中被删除 删除之后不再分解数据
        SwStoreWorkDO swStoreWork = swStoreWorkDao.selectByPrimaryKey(storeWorkId, enterpriseId);
        if (swStoreWork==null){
            log.info("任务已经删除 swStoreWork：{}",storeWorkId);
            return;
        }
        SwStoreWorkRecordDO storeWorkRecordDO = swStoreWorkRecordDao.getByTcBusinessId(enterpriseId, tcBusinessId);
        if(Objects.nonNull(storeWorkRecordDO)){
            StoreDO storeInfo = storeMapper.getByStoreId(enterpriseId, storeId);
            if(Objects.nonNull(storeInfo) && !storeWorkRecordDO.getRegionPath().equals(storeInfo.getRegionPath())){
                log.info("店务补发更新区域路径");
                SwStoreWorkRecordDO updateStoreWorkRecordDO = new SwStoreWorkRecordDO();
                updateStoreWorkRecordDO.setId(storeWorkRecordDO.getId());
                updateStoreWorkRecordDO.setRegionPath(storeInfo.getRegionPath());
                swStoreWorkRecordDao.updateByPrimaryKeySelective(updateStoreWorkRecordDO, enterpriseId);
            }
        }
        List<SwStoreWorkDataTableDO> storeWorkDataTableDOList = swStoreWorkDataTableDao.selectSwStoreWorkDataTableByBusinessId(enterpriseId, tcBusinessId, storeId);
        Map<Integer, List<SwStoreWorkDataTableDO>> storeWorkDataTableDOMap = storeWorkDataTableDOList.stream().collect(Collectors.groupingBy(SwStoreWorkDataTableDO::getGroupNum));

        List<SwStoreWorkTableMappingDO> swStoreWorkTableMappingDOS = swStoreWorkTableMappingDao.selectListByStoreWorkIds(enterpriseId, Arrays.asList(swStoreWork.getId()));
        //一个店务有多组 一组有多个检查表
        Map<Integer, List<SwStoreWorkTableMappingDO>> groupMappingMap = swStoreWorkTableMappingDOS.stream().collect(Collectors.groupingBy(SwStoreWorkTableMappingDO::getGroupNum));

        for (List<SwStoreWorkTableMappingDO> swStoreWorkTableMappingList : groupMappingMap.values()) {
            //每个小组的处理人都相同，去每组的第一个检查表的handlePersonInfo信息
            Integer groupNum =  swStoreWorkTableMappingList.get(0).getGroupNum();
            String handlePersonInfo = swStoreWorkTableMappingList.get(0).getHandlePersonInfo();
            //处理人门店人员权限
            Map<String, List<String>> handleStoreUserMap = getAuthUser(enterpriseId, handlePersonInfo, Arrays.asList(storeId), swStoreWork.getCreateUserId(), true);

            String commentPersonInfos = swStoreWorkTableMappingList.get(0).getCommentPersonInfo();
            Map<String, List<String>> commentPersonAuthUser = new HashMap<>();
            if (StringUtils.isNotEmpty(commentPersonInfos)){
                List<StoreWorkCommonDTO> storeWorkCommonDTOS = JSONObject.parseArray(commentPersonInfos, StoreWorkCommonDTO.class);
                if (CollectionUtils.isNotEmpty(storeWorkCommonDTOS)){
                    commentPersonAuthUser = getAuthUser(enterpriseId, JSONObject.toJSONString(storeWorkCommonDTOS), Arrays.asList(storeId), swStoreWork.getCreateUserId(), false);
                }
            }
            Map<String, List<String>> finalCommentPersonAuthUser = commentPersonAuthUser;
            CollectionUtils.emptyIfNull(storeWorkDataTableDOMap.get(groupNum)).forEach(data ->{
                List<String> handleUserIds = handleStoreUserMap.getOrDefault(storeId, new ArrayList<>());
                if (CollectionUtils.isEmpty(handleUserIds)){
                    //数据回滚
                    log.info("该门店下没人，该店店务不分解 enterpriseId:{}, storeWorkId:{},storeId:{}",enterpriseId,swStoreWork.getId(),storeId);
                    return;
                }
                //处理人信息
                String handleUserIdsStr = handleUserIds.stream().collect(Collectors.joining(Constants.COMMA));
                handleUserIdsStr = String.format("%s%s%s", Constants.COMMA, handleUserIdsStr, Constants.COMMA);
                data.setHandleUserIds(handleUserIdsStr);
                List<String> commentUserIds = finalCommentPersonAuthUser.getOrDefault(storeId, new ArrayList<>());
                String commentUserIdsStr = "";
                if(CollectionUtils.isNotEmpty(commentUserIds)){
                    commentUserIdsStr = String.format("%s%s%s", Constants.COMMA, commentUserIds.stream().collect(Collectors.joining(Constants.COMMA)), Constants.COMMA);
                }
                data.setCommentUserIds(commentUserIdsStr);
            });
        }
        swStoreWorkDataTableDao.batchUpdate(enterpriseId, storeWorkDataTableDOList);
        if (reissueFlag){
            HashMap<String, String> paramMap = new HashMap<>();
            paramMap.put("reissueFlag", String.valueOf(reissueFlag));
            for (SwStoreWorkDataTableDO swStoreWorkDataTableDO : storeWorkDataTableDOList) {
                // 未完成
                if(swStoreWorkDataTableDO.getBeginTime().getTime() < System.currentTimeMillis()&&swStoreWorkDataTableDO.getId()!=null){
                    jmsTaskService.sendStoreWorkMessage(enterpriseId, swStoreWorkDataTableDO.getId(),  StoreWorkNoticeEnum.BEFORE_START_REMIND.getOperate(), paramMap);
                }
            }
        }
    }

    private Boolean fixCommentUserSingle(String enterpriseId){
        //将店务主表的person_info的点评人移到表映射mapping表的comment_person_info上
        //查出当前企业的所有店务记录
        int pageNum=1;
        int pageSize=300;
        while (true){
            Page<String> page = PageHelper.startPage(pageNum, pageSize);
            //先查询所有的主表数据
            List<SwStoreWorkDO> swStoreWorkDOS = swStoreWorkDao.selectAllPersonInfo(enterpriseId)
                    .stream()
                    .filter(c->
                            StringUtils.isNotEmpty(c.getPersonInfo()) && JSONObject.parseObject(c.getPersonInfo(), PersonInfoDTO.class).getCommentPersonInfo()!=null
                    )
                    .collect(Collectors.toList());
            if (CollectionUtils.isEmpty(swStoreWorkDOS)){
                log.info("没有对应店务数据:{}",enterpriseId);
                return Boolean.FALSE;
            }
            //根据id查寻mapping表的内容
            swStoreWorkDOS.stream().forEach(c->{
                List<SwStoreWorkTableMappingDO> mappingDOS = swStoreWorkTableMappingDao.selectListByStoreWorkIds(enterpriseId, Arrays.asList(c.getId()));
                List<StoreWorkCommonDTO> commentPersonInfo = JSONObject.parseObject(c.getPersonInfo(), PersonInfoDTO.class).getCommentPersonInfo();
                mappingDOS.stream().forEach(mappingDO->{
                    mappingDO.setCommentPersonInfo(JSONObject.toJSONString(commentPersonInfo));
                });
                //将内容复制到mapping表中
                if (CollectionUtils.isNotEmpty(mappingDOS)){
                    Integer integer = swStoreWorkTableMappingDao.batchInsertOrUpdateStoreWorkTable(enterpriseId, mappingDOS);
                    log.info("店务id{}更新了:{}条对应表",c.getId(),integer);
                }else {
                    log.info("店务没有对应表");
                }
            });
            if (swStoreWorkDOS.size()<pageSize){
                break;
            }
            pageNum++;
        }
        return true;
    }

    // 店务DO --> VO
    public SwStoreWorkVO getSwStoreWorkVOByDO(String eid, SwStoreWorkDO storeWorkDO, Map<String, EnterpriseUserDO> userMap, Map<Long, List<StoreWorkCommonDTO>>  storeWorkRangeMap, CurrentUser user) {
        DataSourceHelper.changeToSpecificDataSource(user.getDbName());
        SwStoreWorkVO storeWorkVO = new SwStoreWorkVO();
        storeWorkVO.setId(storeWorkDO.getId());
        storeWorkVO.setWorkName(storeWorkDO.getWorkName());
        storeWorkVO.setStoreRange(storeWorkRangeMap.get(storeWorkDO.getId()));
        storeWorkVO.setEditFlag(checkUserEditFlag(eid, storeWorkDO, user.getUserId()));
        storeWorkVO.setBeginTime(storeWorkDO.getBeginTime());
        storeWorkVO.setEndTime(storeWorkDO.getEndTime());
        storeWorkVO.setCreateTime(storeWorkDO.getCreateTime());
        storeWorkVO.setUpdateTime(storeWorkDO.getUpdateTime());
        storeWorkVO.setStatus(storeWorkDO.getStatus());
        storeWorkVO.setOverdueContinue(storeWorkDO.getOverdueContinue());
        storeWorkVO.setOverdue(new Date().after(storeWorkDO.getEndTime()));
        EnterpriseUserDO createUser = userMap.get(storeWorkDO.getCreateUserId());
        if (createUser != null) {
            storeWorkVO.setCreateUserName(createUser.getName());
        }
        EnterpriseUserDO updateUser = userMap.get(storeWorkDO.getUpdateUserId());
        if (updateUser != null) {
            storeWorkVO.setUpdateUserName(updateUser.getName());
        }
        StoreWorkStatisticsDTO storeWorkStatisticsDTO = storeWorkRecordService.countByStoreWorkId(eid, storeWorkDO.getId());
        if(storeWorkStatisticsDTO != null && storeWorkStatisticsDTO.getStoreWorkId() != null){
            storeWorkVO.setTotalNum(storeWorkStatisticsDTO.getTotalNum());
            storeWorkVO.setFinishNum(storeWorkStatisticsDTO.getTotalNum() - storeWorkStatisticsDTO.getUnFinishNum());
        }else {
            storeWorkVO.setTotalNum(getStoreNum(eid, storeWorkRangeMap.get(storeWorkDO.getId())).size());
            storeWorkVO.setFinishNum(0);
        }
        return storeWorkVO;
    }

    // 校验用户是否有编辑权限
    public Boolean checkUserEditFlag(String eid, SwStoreWorkDO storeWorkDO, String userId){
        String personInfo = storeWorkDO.getPersonInfo();
        List<StoreWorkCommonDTO> cooperatePersonInfo = new ArrayList<>();
        if (StringUtils.isNotEmpty(personInfo)){
            PersonInfoDTO personInfoDTO = JSONObject.parseObject(personInfo, PersonInfoDTO.class);
            cooperatePersonInfo = personInfoDTO.getCooperatePersonInfo();
        }
        List<String> personIds = cooperatePersonInfo.stream().filter(x -> x.getType().equals(PersonTypeEnum.PERSON.getType())).map(StoreWorkCommonDTO::getValue).collect(Collectors.toList());
        List<String> positionIds = cooperatePersonInfo.stream().filter(x -> x.getType().equals(PersonTypeEnum.POSITION.getType())).map(StoreWorkCommonDTO::getValue).collect(Collectors.toList());
        List<String> groupIdList = cooperatePersonInfo.stream().filter(x -> x.getType().equals(PersonTypeEnum.USER_GROUP.getType())).map(StoreWorkCommonDTO::getValue).collect(Collectors.toList());
        List<String> regionIdList = cooperatePersonInfo.stream().filter(x -> x.getType().equals(PersonTypeEnum.ORGANIZATION.getType())).map(StoreWorkCommonDTO::getValue).collect(Collectors.toList());
        List<String> allUserIds = Lists.newArrayList();
        allUserIds.add(storeWorkDO.getCreateUserId());
        if (CollectionUtils.isNotEmpty(positionIds)) {
            List<String> userIds = roleMapper.getPositionUserIds(eid, positionIds);
            allUserIds.addAll(userIds);
        }
        if (CollectionUtils.isNotEmpty(groupIdList)) {
            List<String> groupUserIdList = enterpriseUserGroupMappingDao.getUserIdsByGroupIdList(eid, groupIdList);
            if(CollectionUtils.isNotEmpty(groupUserIdList)){
                allUserIds.addAll(groupUserIdList);
            }
        }
        if (CollectionUtils.isNotEmpty(regionIdList)) {
            List<String> regionUserIdList = new ArrayList<>();
            //查看是否是老企业
            boolean historyEnterprise = enterpriseService.isHistoryEnterprise(eid);
            if (historyEnterprise) {
                regionUserIdList = enterpriseUserDao.listUserIdByDepartmentIdList(eid, regionIdList);
            } else {
                regionUserIdList = enterpriseUserDao.getUserIdsByRegionIdList(eid, regionIdList);
            }
            if (CollectionUtils.isNotEmpty(regionUserIdList)) {
                allUserIds.addAll(regionUserIdList);
            }
        }
        if (CollectionUtils.isNotEmpty(personIds)) {
            allUserIds.addAll(personIds);
        }
        // 是否管理员
        boolean isAdmin = sysRoleService.checkIsAdmin(eid, userId);
        if(isAdmin || allUserIds.contains(userId)){
            return true;
        }
        return false;
    }

    private List<SwStoreWorkRangeDO> getStoreRangeList(List<StoreWorkCommonDTO> list, Long storeWorkId) {
        List<SwStoreWorkRangeDO>  storeRangeList = Lists.newArrayList();
        list.forEach(e -> {
            SwStoreWorkRangeDO storeWorkRangeDO = new SwStoreWorkRangeDO();
            storeWorkRangeDO.setStoreWorkId(storeWorkId);
            storeWorkRangeDO.setType(e.getType());
            storeWorkRangeDO.setMappingId(e.getValue());
            storeWorkRangeDO.setDeleted(false);
            storeRangeList.add(storeWorkRangeDO);
        });
        return storeRangeList;
    }

    public SwStoreWorkDO fillStoreWorkByRequest(CurrentUser user, BuildStoreWorkRequest request) {
        // 构建店务DO数据
        SwStoreWorkDO storeWorkDO = new SwStoreWorkDO();
        storeWorkDO.setWorkName(request.getWorkName());
        storeWorkDO.setWorkDesc(request.getWorkDesc());
        storeWorkDO.setUpdateUserId(user.getUserId());
        //只有协作人信息了
        storeWorkDO.setPersonInfo(JSONObject.toJSONString(request.getPersonInfo()));
        storeWorkDO.setWorkCycle(request.getWorkCycle());
        storeWorkDO.setBeginTime(DateUtil.setMillisecondToZero(new Date(request.getBeginTime())));
        storeWorkDO.setEndTime(DateUtil.setMillisecondToZero(new Date(request.getEndTime())));
        storeWorkDO.setOverdueContinue(request.getOverdueContinue());
        if(request.getStoreWorkId() == null){
            storeWorkDO.setStatus(StoreWorkStatusEnum.ONGOING.getCode());
            storeWorkDO.setCreateTime(new Date());
            storeWorkDO.setCreateUserId(user.getUserId());
        }else {
            storeWorkDO.setId(request.getStoreWorkId());
        }
        JSONObject aiRange = new JSONObject();
        if (Objects.nonNull(request.getNotGenerateRange())) {
            aiRange.put(Constants.STORE_WORK_AI.NOT_GENERATE_RANGE, request.getNotGenerateRange());
        }
        if (Objects.nonNull(request.getUseAiRange())) {
            aiRange.put(Constants.STORE_WORK_AI.USE_AI_RANGE, request.getUseAiRange());
        }
        if (Objects.nonNull(request.getAiStoreRange())) {
            aiRange.put(Constants.STORE_WORK_AI.AI_STORE_RANGE, request.getAiStoreRange());
        }
        if (Objects.nonNull(request.getAiStoreRangeMethod())) {
            aiRange.put(Constants.STORE_WORK_AI.AI_STORE_RANGE_METHOD, request.getAiStoreRangeMethod());
        }
        storeWorkDO.setAiRange(aiRange.toJSONString());
        return storeWorkDO;
    }

    /**
     * 获取门店数量
     *
     * @param enterpriseId
     * @param storeRangeList
     * @return
     */
    private Set<String> getStoreNum(String enterpriseId, List<StoreWorkCommonDTO> storeRangeList) {
        if(CollectionUtils.isEmpty(storeRangeList)){
            return Sets.newHashSet();
        }
        Set<String> storeSet = Sets.newHashSet();
        //有效set
        Set<String> storeEffitiveSet = Sets.newHashSet();
        List<String> regionList = Lists.newArrayList();
        List<String> groupList = Lists.newArrayList();
        for (StoreWorkCommonDTO item : storeRangeList) {
            switch (item.getType()) {
                case UnifyTaskConstant.StoreType.STORE:
                    String value = item.getValue();
                    if (value == null) {
                        continue;
                    }
                    storeEffitiveSet.add(item.getValue());
                    break;
                case UnifyTaskConstant.StoreType.REGION:
                    regionList.add(item.getValue());
                    break;
                case UnifyTaskConstant.StoreType.GROUP:
                    groupList.add(item.getValue());
                    break;
                default:
                    throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(),
                            "操作类型有误：operate=" + item.getType());
            }
        }
        if (!storeEffitiveSet.isEmpty()) {
            List<String> effticeStoreIdList = storeMapper.getEffectiveStoreByIdList(enterpriseId, new ArrayList<>(storeEffitiveSet));
            if (CollectionUtils.isNotEmpty(effticeStoreIdList)) {
                storeSet.addAll(new HashSet<>(effticeStoreIdList));
            }
        }
        if (CollectionUtils.isNotEmpty(regionList)) {
            List<String> regionPathList = new ArrayList<>();
            for (String regionId : regionList) {
                regionPathList.add(regionService.getRegionPath(enterpriseId, regionId));
            }

            List<StoreAreaDTO> areaDTOList = storeMapper.listStoreByRegionPathList(enterpriseId, regionPathList);
            if (CollectionUtils.isNotEmpty(areaDTOList)) {
                Set<String> areaStoreSet = areaDTOList.stream().map(StoreAreaDTO::getStoreId).collect(Collectors.toSet());
                if (CollectionUtils.isNotEmpty(areaStoreSet)) {
                    storeSet.addAll(areaStoreSet);
                }
            }
        }
        //分组
        if (CollectionUtils.isNotEmpty(groupList)) {
            List<StoreGroupMappingDO> groupStoreList = storeGroupMappingMapper.getStoreGroupMappingByGroupIDs(enterpriseId, groupList);
            if (CollectionUtils.isNotEmpty(groupStoreList)) {
                Set<String> groupStoreSet = groupStoreList.stream().map(StoreGroupMappingDO::getStoreId).collect(Collectors.toSet());
                if (CollectionUtils.isNotEmpty(groupStoreSet)) {
                    List<String> effticeGroupStoreIdList = storeMapper.getEffectiveStoreByIdList(enterpriseId, new ArrayList<>(groupStoreSet));
                    if (CollectionUtils.isNotEmpty(effticeGroupStoreIdList)) {
                        storeSet.addAll(new HashSet<>(effticeGroupStoreIdList));
                    }
                }
            }
        }
        return storeSet;
    }

    @Override
    public List<String> pmdStoreWorkRemind(String enterpriseId, PmdStoreWorkDataListRequest storeWorkDataListRequest, String appType) {
        CurrentUser user = UserHolder.getUser();
        List<String> userList = new ArrayList<>();
        StoreWorkClearDetailRequest param = new StoreWorkClearDetailRequest();
        pmdStoreWorkRemindSearchConvert(param,storeWorkDataListRequest);
        //查询当前人员门店权限
        List<String> regionPathList = this.queryRegionPath(enterpriseId, user, param);
        String queryDate = DateUtil.format(storeWorkDataListRequest.getTimeValue(), DateUtils.DATE_FORMAT_DAY);
        List<SwStoreWorkRecordDO> remindList = swStoreWorkRecordDao.selectStoreWorkRecord(enterpriseId, queryDate, param, regionPathList).getList();

            for (SwStoreWorkRecordDO swStoreWorkRecordDO : remindList) {
                StoreWorkDataListRequest remindParam = new StoreWorkDataListRequest();
                remindParam.setStoreId(swStoreWorkRecordDO.getStoreId());
                String paramDate = DateUtil.format(swStoreWorkRecordDO.getStoreWorkDate(), DateUtils.DATE_FORMAT_DAY);
                remindParam.setStoreWorkDate(paramDate);
                remindParam.setStoreWorkId(swStoreWorkRecordDO.getStoreWorkId());
                remindParam.setWorkCycle(swStoreWorkRecordDO.getWorkCycle());
                ResponseResult listResponseResult = storeWorkRemind(enterpriseId, remindParam, appType);
                Object data = listResponseResult.getData();
                if (data instanceof Boolean){
                    continue;
                }else {
                    List<String> strings = JSONArray.parseArray(JSONArray.toJSONString(data), String.class);
                    userList.addAll(strings);
                }
            }


        return userList;
    }

    @Override
    public Boolean delStoreWorkSubtask(String enterpriseId, StoreWorkSubtaskDelRequest param, String currentUserId) {
        SwStoreWorkDO storeWorkDO = swStoreWorkDao.selectByPrimaryKey(param.getStoreWorkId(), enterpriseId);
        if (Objects.isNull(storeWorkDO)) {
            throw new ServiceException(ErrorCodeEnum.STORE_WORK_NOT_EXIST);
        }
        /*// 删除店务权限校验（只有创建人、管理员可以删除）
        if (!StringUtils.equals(currentUserId, storeWorkDO.getCreateUserId()) && !sysRoleService.checkIsAdmin(enterpriseId, currentUserId)) {
            throw new ServiceException(ErrorCodeEnum.STORE_WORK_DELETE_AUTH);
        }*/
        // 删除店务记录
        swStoreWorkRecordDao.delByStoreWorkIdAndStoreIdAndDate(enterpriseId, param.getStoreWorkId(), param.getStoreIds(), param.getStoreWorkDate());
        // 删除数据项
        swStoreWorkDataTableColumnDao.delByStoreWorkIdAndStoreIdAndDate(enterpriseId, param.getStoreWorkId(), param.getStoreIds(), param.getStoreWorkDate());
        // 删除数据表
        swStoreWorkDataTableDao.delByStoreWorkIdAndStoreIdAndDate(enterpriseId, param.getStoreWorkId(), param.getStoreIds(), param.getStoreWorkDate());
        return true;
    }

    private void pmdStoreWorkRemindSearchConvert(StoreWorkClearDetailRequest param, PmdStoreWorkDataListRequest storeWorkDataListRequest) {
        param.setStoreWorkDate(storeWorkDataListRequest.getTimeValue());
        param.setWorkCycle(storeWorkDataListRequest.getTimeType());
    }
}
