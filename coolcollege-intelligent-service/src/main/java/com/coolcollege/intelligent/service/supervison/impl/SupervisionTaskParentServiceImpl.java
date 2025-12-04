package com.coolcollege.intelligent.service.supervison.impl;

import com.alibaba.druid.sql.visitor.functions.If;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.constant.ErrContext;
import com.coolcollege.intelligent.common.constant.UnifyTaskConstant;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.enums.MsgUniteDataTypeEnum;
import com.coolcollege.intelligent.common.enums.importexcel.ExportTemplateEnum;
import com.coolcollege.intelligent.common.enums.importexcel.ImportTaskConstant;
import com.coolcollege.intelligent.common.enums.supervison.SupervisionTaskPriorityEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.util.CoolListUtils;
import com.coolcollege.intelligent.common.util.DateUtil;
import com.coolcollege.intelligent.common.util.DateUtils;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseUserMapper;
import com.coolcollege.intelligent.dao.enterprise.dao.EnterpriseUserDao;
import com.coolcollege.intelligent.dao.enterprise.dao.EnterpriseUserRoleDao;
import com.coolcollege.intelligent.dao.enterprise.dao.SubordinateMappingDAO;
import com.coolcollege.intelligent.dao.metatable.TbMetaDefTableColumnMapper;
import com.coolcollege.intelligent.dao.metatable.TbMetaTableMapper;
import com.coolcollege.intelligent.dao.store.StoreGroupMappingMapper;
import com.coolcollege.intelligent.dao.store.StoreMapper;
import com.coolcollege.intelligent.dao.supervision.dao.*;
import com.coolcollege.intelligent.dao.unifytask.TaskSubMapper;
import com.coolcollege.intelligent.dao.unifytask.dao.TaskParentDao;
import com.coolcollege.intelligent.dao.unifytask.dao.TaskSubDao;
import com.coolcollege.intelligent.dao.usergroup.dao.EnterpriseUserGroupDao;
import com.coolcollege.intelligent.dao.usergroup.dao.EnterpriseUserGroupMappingDao;
import com.coolcollege.intelligent.model.authentication.UserAuthMappingDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseUserDO;
import com.coolcollege.intelligent.model.enterprise.SubordinateMappingDO;
import com.coolcollege.intelligent.model.enterprise.dto.EnterpriseUserDTO;
import com.coolcollege.intelligent.model.enterprise.dto.EnterpriseUserSingleDTO;
import com.coolcollege.intelligent.model.enums.*;
import com.coolcollege.intelligent.model.impoetexcel.ImportTaskDO;
import com.coolcollege.intelligent.model.metatable.TbMetaDefTableColumnDO;
import com.coolcollege.intelligent.model.metatable.TbMetaTableDO;
import com.coolcollege.intelligent.model.msg.MsgUniteData;
import com.coolcollege.intelligent.model.question.request.ExportStoreWorkDataRequest;
import com.coolcollege.intelligent.model.region.dto.AuthStoreCountDTO;
import com.coolcollege.intelligent.model.region.dto.AuthVisualDTO;
import com.coolcollege.intelligent.model.sop.TaskSopDO;
import com.coolcollege.intelligent.model.sop.vo.TaskSopVO;
import com.coolcollege.intelligent.model.store.StoreDO;
import com.coolcollege.intelligent.model.store.StoreGroupMappingDO;
import com.coolcollege.intelligent.model.store.dto.SingleStoreDTO;
import com.coolcollege.intelligent.model.store.dto.StoreAreaDTO;
import com.coolcollege.intelligent.model.store.dto.StoreDTO;
import com.coolcollege.intelligent.model.storework.dto.StoreWorkCommonDTO;
import com.coolcollege.intelligent.model.supervision.*;
import com.coolcollege.intelligent.model.supervision.dto.*;
import com.coolcollege.intelligent.model.supervision.request.AddSupervisionTaskParentRequest;
import com.coolcollege.intelligent.model.supervision.request.SupervisionDataDetailListExportRequest;
import com.coolcollege.intelligent.model.supervision.request.SupervisionTaskDataListExportRequest;
import com.coolcollege.intelligent.model.supervision.vo.*;
import com.coolcollege.intelligent.model.unifytask.TaskParentDO;
import com.coolcollege.intelligent.model.unifytask.TaskSubDO;
import com.coolcollege.intelligent.model.unifytask.dto.GeneralDTO;
import com.coolcollege.intelligent.model.usergroup.EnterpriseUserGroupMappingDO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.producer.SimpleMessageService;
import com.coolcollege.intelligent.service.authentication.AuthVisualService;
import com.coolcollege.intelligent.service.enterprise.EnterpriseConfigService;
import com.coolcollege.intelligent.service.enterprise.EnterpriseUserService;
import com.coolcollege.intelligent.service.enterprise.SubordinateMappingService;
import com.coolcollege.intelligent.service.importexcel.ImportTaskService;
import com.coolcollege.intelligent.service.region.RegionService;
import com.coolcollege.intelligent.service.sop.TaskSopService;
import com.coolcollege.intelligent.service.store.StoreService;
import com.coolcollege.intelligent.service.supervison.SupervisionDefDataColumnService;
import com.coolcollege.intelligent.service.supervison.SupervisionStoreTaskService;
import com.coolcollege.intelligent.service.supervison.SupervisionTaskParentService;
import com.coolcollege.intelligent.service.supervison.SupervisionTaskService;
import com.coolcollege.intelligent.service.supervison.open.HsStrategyCenterService;
import com.coolcollege.intelligent.service.task.JmsTaskService;
import com.coolcollege.intelligent.service.unifytask.UnifyTaskParentService;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.coolstore.base.enums.RocketMqTagEnum;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.JsonObject;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.ParseException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.aliyun.openservices.shade.com.alibaba.rocketmq.common.protocol.header.namesrv.GetRouteInfoRequestHeader.split;

/**
 * @Author suzhuhong
 * @Date 2023/2/1 16:38
 * @Version 1.0
 */
@Service
@Slf4j
public class SupervisionTaskParentServiceImpl implements SupervisionTaskParentService {


    public static final String SUPERVISION_TASK_PARENT = "SUPERVISION_TASK_PARENT_";
    @Resource
    UnifyTaskParentService unifyTaskParentService;

    @Resource
    SupervisionTaskParentDao supervisionTaskParentDao;

    @Resource
    private SimpleMessageService simpleMessageService;

    @Resource
    AuthVisualService authVisualService;

    @Resource
    private EnterpriseUserRoleDao enterpriseUserRoleDao;

    @Resource
    SupervisionTaskDao supervisionTaskDao;
    @Resource
    SupervisionStoreTaskDao supervisionStoreTaskDao;
    @Resource
    SupervisionStoreTaskService supervisionStoreTaskService;

    @Resource
    TaskParentDao taskParentDao;

    @Resource
    EnterpriseUserDao enterpriseUserDao;
    @Resource
    private EnterpriseUserService enterpriseUserService;

    @Resource
    private JmsTaskService jmsTaskService;

    @Resource
    private ImportTaskService importTaskService;
    @Resource
    TbMetaTableMapper tbMetaTableMapper;

    @Resource
    TaskSubMapper taskSubMapper;

    @Resource
    StoreMapper storeMapper;
    @Resource
    SupervisionTaskService supervisionTaskService;

    @Resource
    EnterpriseConfigService enterpriseConfigService;

    @Resource
    TaskSopService taskSopService;

    @Autowired
    private RedisUtilPool redis;

    @Resource
    TbMetaDefTableColumnMapper tbMetaDefTableColumnMapper;
    @Resource
    SupervisionDefDataColumnService supervisionDefDataColumnService;

    @Resource
    EnterpriseUserMapper enterpriseUserMapper;
    @Resource
    private EnterpriseUserGroupMappingDao enterpriseUserGroupMappingDao;
    @Resource
    HsStrategyCenterService hsStrategyCenterService;
    @Resource
    SupervisionDefDataColumnDao supervisionDefDataColumnDao;
    @Resource(name = "generalThreadPool")
    private ThreadPoolTaskExecutor EXECUTOR_SERVICE;
    @Resource
    RegionService regionService;
    @Resource
    StoreGroupMappingMapper storeGroupMappingMapper;
    @Resource
    SubordinateMappingService subordinateMappingService;
    @Resource
    SubordinateMappingDAO subordinateMappingDAO;

    @Resource
    SupervisionHistoryDao supervisionHistoryDao;

    @Resource
    SupervisionApproveDao supervisionApproveDao;
    @Override
    public Boolean addSupervisionTaskParent(String enterpriseId, CurrentUser currentUser, AddSupervisionTaskParentRequest request) {
        //必填参数不能为空
        if (StringUtils.isEmpty(request.getTaskName())||request.getPriority()==null||request.getTaskStartTime()==null||request.getTaskEndTime()==null||StringUtils.isEmpty(request.getExecutePersons())){
            throw  new ServiceException(ErrorCodeEnum.PARAM_MISSING);
        }
        log.info("addSupervisionTaskParent:{}",JSONObject.toJSONString(request));
        //新增数据到父任务表
        TaskParentDO taskParentDO = unifyTaskParentService.addSupervisionTaskParent(enterpriseId, currentUser, request);
        //督导父任务定义表
        SupervisionTaskParentDO supervisionTaskParentDO = new  SupervisionTaskParentDO();
        supervisionTaskParentDO.setBusinessType(request.getBusinessType());
        supervisionTaskParentDO.setId(taskParentDO.getId());
        supervisionTaskParentDO.setBusinessId(request.getBusinessId());
        supervisionTaskParentDO.setTaskName(request.getTaskName());
        supervisionTaskParentDO.setTaskStartTime(request.getTaskStartTime());
        supervisionTaskParentDO.setTaskEndTime(request.getTaskEndTime());
        supervisionTaskParentDO.setCheckCode(request.getCheckCode());
        if (CollectionUtils.isNotEmpty(request.getStoreIds())){
            supervisionTaskParentDO.setCheckStoreIds(JSONObject.toJSONString(request.getStoreIds()));
        }
        supervisionTaskParentDO.setCreateTime(new Date());
        supervisionTaskParentDO.setCreateUserId(currentUser.getUserId());
        supervisionTaskParentDO.setCreateUserName(currentUser.getName());
        supervisionTaskParentDO.setDescription(request.getDesc());
        supervisionTaskParentDO.setRemark(request.getRemark());
        supervisionTaskParentDO.setFormId(request.getFormId());
        //锁表
        if (request.getFormId()!=null){
            TbMetaTableDO tbMetaTableDO = tbMetaTableMapper.selectById(enterpriseId, Long.valueOf(request.getFormId()));
            if (tbMetaTableDO!=null && tbMetaTableDO.getLocked()==0){
                tbMetaTableMapper.updateLockedByIds(enterpriseId,Arrays.asList(Long.valueOf(request.getFormId())));
            }
        }
        supervisionTaskParentDO.setExecutePersons(request.getExecutePersons());
        supervisionTaskParentDO.setHandleWay(request.getHandleWay());
        supervisionTaskParentDO.setPriority(request.getPriority().getCode().toUpperCase());
        supervisionTaskParentDO.setTags(request.getTags());
        supervisionTaskParentDO.setTaskGrouping(request.getTaskGrouping());
        if (request.getApproveInfoDTO()!=null){
            supervisionTaskParentDO.setProcessInfo(JSONObject.toJSONString(request.getApproveInfoDTO()));
        }
        if (request.getTimingInfoDTO()!=null){
            supervisionTaskParentDO.setTimingInfo(JSONObject.toJSONString(request.getTimingInfoDTO()));
        }
        if (request.getTaskSopListVO()!=null&&CollectionUtils.isNotEmpty(request.getTaskSopListVO().getSopList())){
            List<TaskSopDO> taskSopDOS = taskSopService.batchInsertSupervisionSop(enterpriseId, request.getTaskSopListVO(), currentUser);
            List<Long> sopIds = taskSopDOS.stream().map(TaskSopDO::getId).collect(Collectors.toList());
            supervisionTaskParentDO.setSopIds(JSONObject.toJSONString(sopIds));
        }
        //新增成功之后 删除任务定义的缓存
        redis.delKey(getKey(enterpriseId,currentUser.getUserId()));
        supervisionTaskParentDao.insertSelective(supervisionTaskParentDO,enterpriseId);
        //判断是否分解
        if (request.getTaskStartTime().getTime()<=System.currentTimeMillis()){
            //分解
            EXECUTOR_SERVICE.submit(() -> {
                try {
                    splitSupervisionTaskForPerson(enterpriseId,currentUser,supervisionTaskParentDO.getId(),null);
                } catch (Exception e) {
                    log.info("分解异常e:{}",e);
                }
            });
        }else {
            //延迟队列
            long time = request.getTaskStartTime().getTime();
            log.info("delay_time:{}",time);
            TaskResolveDelayDTO taskResolveDelayDTO = new TaskResolveDelayDTO();
            taskResolveDelayDTO.setParentId(supervisionTaskParentDO.getId());
            taskResolveDelayDTO.setEnterpriseId(enterpriseId);
            taskResolveDelayDTO.setCurrentUser(currentUser);
            taskResolveDelayDTO.setTaskStartTime(request.getTaskStartTime().getTime());
            simpleMessageService.send(JSONObject.toJSONString(taskResolveDelayDTO), RocketMqTagEnum.SUPERVISION_RESOLVE_DELAY,time);
        }
        return Boolean.TRUE;
    }

    @Override
    public Integer checkSupervisionTaskParent(String enterpriseId,CurrentUser currentUser, AddSupervisionTaskParentRequest request) {

        //区分选择的人与职位
        List<GeneralDTO> generalDTOS = JSONObject.parseArray(request.getExecutePersons(), GeneralDTO.class);
        List<String> userIdLists = getUserId(enterpriseId,generalDTOS);
        if(CollectionUtils.isEmpty(userIdLists)){
            return ErrorCodeEnum.SUPERVISION_USER_IS_NOT_NULL.getCode();
        }

        if (CollectionUtils.isNotEmpty(request.getStoreIds())){
            List<String> storeIdList = new ArrayList<>();
            String checkStoreIds = JSONObject.toJSONString(request.getStoreIds());
            if (StringUtils.isNotEmpty(checkStoreIds)){
                //兼容老数据 新数据JSON  老数据是String'
                if (checkStoreIds.contains("{")){
                    List<GeneralDTO> gen = JSONObject.parseArray(checkStoreIds, GeneralDTO.class);
                    Set<String> storeIdSet = getStoreIdList(enterpriseId, gen, currentUser.getUserId());
                    storeIdList = new ArrayList<>(storeIdSet);
                }else {
                    storeIdList = JSONObject.parseArray(checkStoreIds,String.class);
                }
            }
            List<HsUserStoreDTO> hsUserStoreDTOS = new ArrayList<>();
            //分页查询每个人员有哪些门店的数据
            List<List<String>> partition = Lists.partition(userIdLists, 100);
            for (List<String> userIdList:partition) {
                List<HsUserStoreDTO> supervisorStores = hsStrategyCenterService.getSupervisorStores(enterpriseId,userIdList);
                if (CollectionUtils.isNotEmpty(supervisorStores)){
                    hsUserStoreDTOS.addAll(supervisorStores);
                }
            }
            if (CollectionUtils.isEmpty(hsUserStoreDTOS)){
                return ErrorCodeEnum.SUPERVISION_STORE_UNION_IS_NULL.getCode();
            }

            for (HsUserStoreDTO hsUserStoreDTO:hsUserStoreDTOS) {
                List<String> tempList = new ArrayList<>();
                tempList.addAll(storeIdList);
                List<HsStoreDTO> hsStoreDTOS = hsUserStoreDTO.getStoreList();
                if (CollectionUtils.isNotEmpty(hsStoreDTOS)){
                    List<String> dingDingDeptIdList = hsStoreDTOS.stream().map(HsStoreDTO::getDingDingDeptId).collect(Collectors.toList());
                    List<SingleStoreDTO> effectiveStoreByDingIdList = storeMapper.getSingleStoreByDingDeptIds(enterpriseId, dingDingDeptIdList);
                    List<String> storeIds = effectiveStoreByDingIdList.stream().map(SingleStoreDTO::getStoreId).collect(Collectors.toList());
                    tempList.retainAll(storeIds);
                    if (CollectionUtils.isNotEmpty(tempList)){
                        return Constants.TWO_HUNDRED;
                        //只要有一个人取交集不为空 则表示任务正常
                    }
                }
            }
        }else {
            return Constants.TWO_HUNDRED;
        }
        //选择了门店的情况下，所有的人取交集则返回取交集为空code
        return ErrorCodeEnum.SUPERVISION_STORE_UNION_IS_NULL.getCode();
    }


    @Override
    public Boolean stagingSupervisionTaskParent(String enterpriseId, CurrentUser user, AddSupervisionTaskParentRequest request) {
        //暂存到缓存中
        redis.setString(getKey(enterpriseId,user.getUserId()),JSONObject.toJSONString(request));
        return Boolean.TRUE;
    }

    @Override
    public SupervisionTaskParentDetailVO getStagingSupervisionTaskParent(String enterpriseId,CurrentUser user) {
        String string = redis.getString(getKey(enterpriseId, user.getUserId()));
        SupervisionTaskParentDetailVO supervisionTaskParentDetailVO = new SupervisionTaskParentDetailVO();
        if (StringUtils.isEmpty(string)){
            return supervisionTaskParentDetailVO;
        }
        AddSupervisionTaskParentRequest addSupervisionTaskParentRequest = JSONObject.parseObject(string, AddSupervisionTaskParentRequest.class);

        supervisionTaskParentDetailVO = JSONObject.parseObject(string, SupervisionTaskParentDetailVO.class);

        //加载sop文档
        if (addSupervisionTaskParentRequest.getTaskSopListVO()!=null){
            List<TaskSopVO> sopList = addSupervisionTaskParentRequest.getTaskSopListVO().getSopList();
            supervisionTaskParentDetailVO.setTaskSopVOList(sopList);
        }
        if (StringUtils.isNotEmpty(supervisionTaskParentDetailVO.getFormId())){
            TbMetaTableDO tbMetaTableDO = tbMetaTableMapper.selectById(enterpriseId, Long.valueOf(supervisionTaskParentDetailVO.getFormId()));
            supervisionTaskParentDetailVO.setTbMetaTableDO(tbMetaTableDO);
        }

        if (CollectionUtils.isNotEmpty(supervisionTaskParentDetailVO.getStoreIds())){
            List<GeneralDTO> storeIds = supervisionTaskParentDetailVO.getStoreIds();
            if (CollectionUtils.isEmpty(storeIds)){
                supervisionTaskParentDetailVO.setStoreRangeList(Collections.emptyList());
            }else {
                supervisionTaskParentDetailVO.setStoreRangeList(storeIds);
            }
        }
        return supervisionTaskParentDetailVO;
    }

    /**
     * 任务定义缓存
     * @param eid
     * @return
     */
    private String getKey(String eid,String userId) {
        return String.format("%s%s%s", SUPERVISION_TASK_PARENT, eid, userId);
    }

    @Override
    public Boolean editSupervisionTaskParent(String enterpriseId, CurrentUser user, AddSupervisionTaskParentRequest request) {
        //编辑父任务
        unifyTaskParentService.updateSuperVisionParent(enterpriseId,user,request);
        //原始任务
        SupervisionTaskParentDO sup = supervisionTaskParentDao.selectByPrimaryKey(request.getId(), enterpriseId);
        SupervisionTaskParentDO supervisionTaskParentDO = new SupervisionTaskParentDO();
        supervisionTaskParentDO.setId(request.getId());
        supervisionTaskParentDO.setTaskName(request.getTaskName());
        supervisionTaskParentDO.setRemark(request.getRemark());
        supervisionTaskParentDO.setDescription(request.getDesc());
        supervisionTaskParentDO.setUpdateTime(new Date());
        supervisionTaskParentDO.setUpdateUserId(user.getUserId());
        supervisionTaskParentDO.setTaskGrouping(request.getTaskGrouping());
        if (request.getTaskSopListVO()!=null&&CollectionUtils.isNotEmpty(request.getTaskSopListVO().getSopList())){
            List<TaskSopVO> sopList = request.getTaskSopListVO().getSopList();
            List<TaskSopVO> insertList = sopList.stream().filter(x -> x.getId() == null).collect(Collectors.toList());
            List<TaskSopVO> updateList = sopList.stream().filter(x -> x.getId() != null).collect(Collectors.toList());
            List<TaskSopDO> taskSopDOS = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(insertList)){
                request.getTaskSopListVO().setSopList(insertList);
                taskSopDOS = taskSopService.batchInsertSupervisionSop(enterpriseId, request.getTaskSopListVO(), user);
            }
            List<Long> sopIds = updateList.stream().map(TaskSopVO::getId).collect(Collectors.toList());
            sopIds.addAll(taskSopDOS.stream().map(TaskSopDO::getId).collect(Collectors.toList()));
            supervisionTaskParentDO.setSopIds(JSONObject.toJSONString(sopIds));
        }
        //未开始状态下 什么都可以编辑
        if (System.currentTimeMillis()<sup.getTaskStartTime().getTime()&&sup.getCancelStatus()==0){
            supervisionTaskParentDO.setBusinessType(request.getBusinessType());
            supervisionTaskParentDO.setBusinessId(request.getBusinessId());
            supervisionTaskParentDO.setTaskStartTime(request.getTaskStartTime());
            supervisionTaskParentDO.setTaskEndTime(request.getTaskEndTime());
            supervisionTaskParentDO.setCheckCode(request.getCheckCode());
            String storeString = "";
            if (CollectionUtils.isNotEmpty(request.getStoreIds())){
                storeString = JSONObject.toJSONString(request.getStoreIds());
            }
            supervisionTaskParentDO.setCheckStoreIds(storeString);
            supervisionTaskParentDO.setFormId(request.getFormId());
            //锁表
            if (request.getFormId()!=null){
                TbMetaTableDO tbMetaTableDO = tbMetaTableMapper.selectById(enterpriseId, Long.valueOf(request.getFormId()));
                if (tbMetaTableDO!=null && tbMetaTableDO.getLocked()==0){
                    tbMetaTableMapper.updateLockedByIds(enterpriseId,Arrays.asList(Long.valueOf(request.getFormId())));
                }
            }
            supervisionTaskParentDO.setExecutePersons(request.getExecutePersons());
            supervisionTaskParentDO.setHandleWay(request.getHandleWay());
            supervisionTaskParentDO.setPriority(request.getPriority().getCode().toUpperCase());
            supervisionTaskParentDO.setTags(request.getTags());
            String timingInfoDTO = "";
            if (request.getTimingInfoDTO()!=null){
                timingInfoDTO = JSONObject.toJSONString(request.getTimingInfoDTO());
            }
            supervisionTaskParentDO.setTimingInfo(timingInfoDTO);
            String processInfo = "";
            if (request.getApproveInfoDTO()!=null){
                processInfo = JSONObject.toJSONString(request.getApproveInfoDTO());
            }
            supervisionTaskParentDO.setProcessInfo(processInfo);

        }
        //进行中 只能编辑-任务名称、任务描述、任务截止时间、备注可编辑，其他不可编辑
        Date reminderTimeBeforeEnd = null;
        if (System.currentTimeMillis()>sup.getTaskStartTime().getTime()&&System.currentTimeMillis()<sup.getTaskEndTime().getTime()&&sup.getCancelStatus()==0){
            if (StringUtils.isNotEmpty(sup.getTimingInfo())){
                TimingInfoDTO timingInfoDTO = JSONObject.parseObject(sup.getTimingInfo(), TimingInfoDTO.class);
                if (timingInfoDTO.getBeforeTheEnd()!=null){
                    Integer dayNum = timingInfoDTO.getBeforeTheEnd().getDayNum();
                    reminderTimeBeforeEnd = DateUtil.plusDays(sup.getTaskEndTime(), -dayNum);
                }
            }
            supervisionTaskParentDO.setTaskEndTime(request.getTaskEndTime());
        }
        supervisionTaskParentDao.updateByPrimaryKeySelective(supervisionTaskParentDO,enterpriseId);
        //修改分解的子任务数据
        supervisionTaskDao.updateByParentId(enterpriseId,request.getTaskName(),request.getTaskEndTime(),sup.getId(),reminderTimeBeforeEnd,request.getTaskGrouping());
        //修改分解的门店任务
        supervisionStoreTaskDao.updateByParentId(enterpriseId,request.getTaskName(),request.getTaskEndTime(),sup.getId(),reminderTimeBeforeEnd,request.getTaskGrouping());
        //如果未分解 在延迟队列中 编辑继续分解(两次分解做幂等)
        //判断是否分解
        if (request.getTaskStartTime().getTime()<System.currentTimeMillis()){
            //分解
            log.info("任务开始分解parentId:{}",supervisionTaskParentDO.getId());
            EXECUTOR_SERVICE.submit(() -> {
                try {
                    splitSupervisionTaskForPerson(enterpriseId,user,supervisionTaskParentDO.getId(),null);
                } catch (Exception e) {
                    log.info("分解异常e:{}",e);
                }
            });
        }else {
            //延迟队列
            long time = request.getTaskStartTime().getTime();
            log.info("delay_time:{}",time);
            TaskResolveDelayDTO taskResolveDelayDTO = new TaskResolveDelayDTO();
            taskResolveDelayDTO.setParentId(supervisionTaskParentDO.getId());
            taskResolveDelayDTO.setEnterpriseId(enterpriseId);
            taskResolveDelayDTO.setCurrentUser(user);
            taskResolveDelayDTO.setTaskStartTime(request.getTaskStartTime().getTime());
            simpleMessageService.send(JSONObject.toJSONString(taskResolveDelayDTO), RocketMqTagEnum.SUPERVISION_RESOLVE_DELAY,time);
        }
        return Boolean.TRUE;
    }


    /**
     * 分解任务
     * @param enterpriseId
     * @param currentUser
     * @param parentId
     */
    @Override
    public void  splitSupervisionTaskForPerson(String enterpriseId, CurrentUser currentUser,Long parentId,Long taskStartTime) throws Exception{
        log.info("任务开始分解parentId:{},taskStartTime{}",parentId,taskStartTime);
        String key = String.format("%s%d", enterpriseId, parentId);
        boolean nxExpire = redis.setNxExpire(key, key, 10*1000);
        if (!nxExpire){
            log.info("splitSupervisionTaskForPerson 任务正在分解");
            return;
        }
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigService.selectByEnterpriseId(enterpriseId);
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());
        SupervisionTaskParentDO supervisionTaskParentDO = supervisionTaskParentDao.selectByPrimaryKey(parentId, enterpriseId);

        if (supervisionTaskParentDO==null){
            throw  new ServiceException(ErrorCodeEnum.SUPERVISION_TASK_PARENT_NOT_EXIST);
        }
        //如果任务取消或者数据被删除 不分解
        if (supervisionTaskParentDO.getDeleted()||supervisionTaskParentDO.getCancelStatus()==1){
            log.info("splitSupervisionTaskForPerson deleted:{},cancelStatus:{}",supervisionTaskParentDO.getDeleted(),supervisionTaskParentDO.getCancelStatus());
            return;
        }
        //延迟队列发出去之后 用户编辑一次会发另外一个延迟队列，用时间校验 保证正确的时间分解
        if (taskStartTime!=null){
            long a  = taskStartTime - supervisionTaskParentDO.getTaskStartTime().getTime();
            log.info("splitSupervisionTaskForPerson taskStartTime:{},supervisionTaskParentTime:{},时间差：{}",taskStartTime,supervisionTaskParentDO.getTaskStartTime().getTime(),Math.abs(a));
            //5秒之内的任务算同一个任务 因为数据库时间戳存在精度丢失的问题
            if (Math.abs(a)>5000){
                log.info("开始时间差超过5秒 不分解 {}",Math.abs(a));
                return;
            }
        }
        //如果任务已经分解，直接推出
        List<SupervisionTaskDO> supervisionTaskDOS = supervisionTaskDao.listSupervisionTaskByParentId(enterpriseId, parentId, null, null,null);
        if (CollectionUtils.isNotEmpty(supervisionTaskDOS)){
            log.info("splitSupervisionTaskForPerson 任务已经分解");
            return;
        }

        //开始分解任务的时候校验
        AddSupervisionTaskParentRequest addSupervisionTaskParentRequest = new AddSupervisionTaskParentRequest();
        addSupervisionTaskParentRequest.setExecutePersons(supervisionTaskParentDO.getExecutePersons());
        List<GeneralDTO> generalDTOS = JSONObject.parseArray(supervisionTaskParentDO.getCheckStoreIds(), GeneralDTO.class);
        addSupervisionTaskParentRequest.setStoreIds(generalDTOS);
        Integer flag = checkSupervisionTaskParent(enterpriseId, currentUser, addSupervisionTaskParentRequest);
        log.info("校验状态码值为{}",flag);
        if (!flag.equals(200)){
            //失效状态
            log.info("任务设置为失效状态 任务ID:{} flag:{}",supervisionTaskParentDO.getId(),flag);
            supervisionTaskParentDao.updateFailureState(enterpriseId,supervisionTaskParentDO.getId());
            return;
        }

        if (StringUtils.isEmpty(supervisionTaskParentDO.getExecutePersons()) &&StringUtils.isEmpty(supervisionTaskParentDO.getCheckStoreIds())){
            throw  new ServiceException(ErrorCodeEnum.PARAM_MISSING);
        }

        //区分选择的人与职位
        List<GeneralDTO> userDTOS = JSONObject.parseArray(supervisionTaskParentDO.getExecutePersons(), GeneralDTO.class);
        List<String> userIdLists = getUserId(enterpriseId,userDTOS);
        List<SupervisionTaskDO> result= new ArrayList<>();
        List<TaskSubDO> subList= new ArrayList<>();
        List<SupervisionDefDataColumnDO> supervisionDefDataColumnDOS = new ArrayList<>();


        List<EnterpriseUserSingleDTO> enterpriseUserSingleDTOS = enterpriseUserDao.usersByUserIdList(enterpriseId, userIdLists);
        Map<String, String> userNameMap = enterpriseUserSingleDTOS.stream().collect(Collectors.toMap(EnterpriseUserSingleDTO::getUserId, EnterpriseUserSingleDTO::getUserName));

        //门店
        String checkStoreIds = supervisionTaskParentDO.getCheckStoreIds();
        List<String> storeIdList = new ArrayList<>();
        Map<String, StoreDTO> storeDTOMap = new HashMap<>();
        if (StringUtils.isNotEmpty(checkStoreIds)){
            //兼容老数据 新数据JSON  老数据是String'
            if (checkStoreIds.contains("{")){
                List<GeneralDTO> gen = JSONObject.parseArray(checkStoreIds, GeneralDTO.class);
                Set<String> storeIdSet = getStoreIdList(enterpriseId, gen, currentUser.getUserId());
                storeIdList = new ArrayList<>(storeIdSet);
            }else {
                storeIdList = JSONObject.parseArray(checkStoreIds,String.class);
            }
            List<StoreDTO> storeList = storeMapper.getStoreListByStoreIds(enterpriseId, storeIdList);
            storeDTOMap = storeList.stream().collect(Collectors.toMap(StoreDTO::getStoreId, data -> data));
        }
        //审批状态
        Integer approveStatus = null;
        if (StringUtils.isEmpty(supervisionTaskParentDO.getProcessInfo())){
            approveStatus = 0;
        }

        //自定义时 查询一级审批人的管辖用户  慢
        HashMap<String, List<String>> userSubMap = new HashMap<>();
        Map<String, String> userSubordinateMap = new HashMap<>();
        Map<String, String> secondaryApproveSubordinateMap = new HashMap<>();
        Map<String, String> thirdApproveSubordinateMap = new HashMap<>();
        if (StringUtils.isNotEmpty(supervisionTaskParentDO.getProcessInfo())){
            ApproveInfoDTO approveInfoDTO = JSONObject.parseObject(supervisionTaskParentDO.getProcessInfo(), ApproveInfoDTO.class);
            if (approveInfoDTO.getApproveType()==1){
                //自定义有审批的时候 只有一级
                approveInfoDTO.setApproveHierarchy(1);
                //审批人员
                List<String> approveUserIdList = getUserId(enterpriseId, approveInfoDTO.getFirstApproveList());
                if (CollectionUtils.isNotEmpty(approveUserIdList)){
                    approveUserIdList.forEach(x->{
                        List<String> tempList = new ArrayList<>();
                        tempList.addAll(userIdLists);
                        List<String> list = subordinateMappingService.retainSubordinateUserIdList(enterpriseId, x, tempList, Boolean.FALSE);
                        userSubMap.put(x,list);
                    });
                }
            }
            //执行人的直属上级
            List<SubordinateMappingDO> subordinateMappingList = new ArrayList<>();
            List<List<String>> handleSubordinateList = Lists.partition(userIdLists, 100);
            for (List<String> userIdList:handleSubordinateList) {
                List<SubordinateMappingDO> subordinateMappingDOS = subordinateMappingDAO.selectByUserIdsAndType(enterpriseId, userIdList);
                if (CollectionUtils.isNotEmpty(subordinateMappingDOS)){
                    subordinateMappingList.addAll(subordinateMappingDOS);
                }
            }
            //执行人直属上级map
            userSubordinateMap = subordinateMappingList.stream().collect(Collectors.toMap(SubordinateMappingDO::getUserId, SubordinateMappingDO::getPersonalId));

            List<SubordinateMappingDO> secondaryApprove = new ArrayList<>();
            if (approveInfoDTO.getApproveHierarchy()>1){
                List<String> firstApproveList = subordinateMappingList.stream().map(SubordinateMappingDO::getPersonalId).collect(Collectors.toList());
                firstApproveList.add(supervisionTaskParentDO.getCreateUserId());
                List<List<String>> secondaryApproveSubordinateList = Lists.partition(firstApproveList, 100);
                for (List<String> userIdList:secondaryApproveSubordinateList) {
                    List<SubordinateMappingDO> subordinateMappingDOS = subordinateMappingDAO.selectByUserIdsAndType(enterpriseId, userIdList);
                    if (CollectionUtils.isNotEmpty(subordinateMappingDOS)){
                        secondaryApprove.addAll(subordinateMappingDOS);
                    }
                }
                secondaryApproveSubordinateMap = secondaryApprove.stream().collect(Collectors.toMap(SubordinateMappingDO::getUserId, SubordinateMappingDO::getPersonalId));
            }

            if (approveInfoDTO.getApproveHierarchy()>2){
                List<String> secondaryApproveList = secondaryApprove.stream().map(SubordinateMappingDO::getPersonalId).collect(Collectors.toList());
                secondaryApproveList.add(supervisionTaskParentDO.getCreateUserId());
                List<SubordinateMappingDO> thirdApprove = new ArrayList<>();
                List<List<String>> thirdApproveSubordinateList = Lists.partition(secondaryApproveList, 100);
                for (List<String> userIdList:thirdApproveSubordinateList) {
                    List<SubordinateMappingDO> subordinateMappingDOS = subordinateMappingDAO.selectByUserIdsAndType(enterpriseId, userIdList);
                    if (CollectionUtils.isNotEmpty(subordinateMappingDOS)){
                        thirdApprove.addAll(subordinateMappingDOS);
                    }
                }
                thirdApproveSubordinateMap = thirdApprove.stream().collect(Collectors.toMap(SubordinateMappingDO::getUserId, SubordinateMappingDO::getPersonalId));
            }
        }



        //提醒时间
        Date reminderTimeBeforeStarting = null;
        Date reminderTimeBeforeEnd = null;
        if (StringUtils.isNotEmpty(supervisionTaskParentDO.getTimingInfo())){
            TimingInfoDTO timingInfoDTO = JSONObject.parseObject(supervisionTaskParentDO.getTimingInfo(), TimingInfoDTO.class);
            if (timingInfoDTO.getBeforeTheEnd()!=null){
                Integer dayNum = timingInfoDTO.getBeforeTheEnd().getDayNum();
                reminderTimeBeforeEnd = DateUtil.plusDays(supervisionTaskParentDO.getTaskEndTime(), -dayNum);
            }
            if (timingInfoDTO.getAfterStarting()!=null){
                Integer dayNum = timingInfoDTO.getAfterStarting().getDayNum();
                reminderTimeBeforeStarting = DateUtil.plusDays(supervisionTaskParentDO.getTaskStartTime(), dayNum);
            }
        }

        //如果是空 没有选择门店 直接按人分解
        if (StringUtils.isEmpty(checkStoreIds)){
            for (String userId:userIdLists) {
                SupervisionTaskDO supervisionTaskDO = new SupervisionTaskDO();
                supervisionTaskDO.setTaskParentId(parentId);
                supervisionTaskDO.setTaskName(supervisionTaskParentDO.getTaskName());
                supervisionTaskDO.setTaskStartTime(supervisionTaskParentDO.getTaskStartTime());
                supervisionTaskDO.setTaskState(Constants.ZERO);
                supervisionTaskDO.setFormId(supervisionTaskParentDO.getFormId());
                supervisionTaskDO.setSopIds(supervisionTaskParentDO.getSopIds());
                supervisionTaskDO.setBusinessType(supervisionTaskParentDO.getBusinessType());
                supervisionTaskDO.setTaskEndTime(supervisionTaskParentDO.getTaskEndTime());
                supervisionTaskDO.setSupervisionUserId(userId);
                supervisionTaskDO.setSupervisionHandleUserId(userId);
                supervisionTaskDO.setSupervisionHandleUserName(userNameMap.get(userId));
                supervisionTaskDO.setPriority(supervisionTaskParentDO.getPriority());
                supervisionTaskDO.setBusinessType(supervisionTaskParentDO.getBusinessType());
                supervisionTaskDO.setCreateTime(new Date());

                supervisionTaskDO.setReminderTimeBeforeEnd(reminderTimeBeforeEnd);
                supervisionTaskDO.setReminderTimeBeforeStarting(reminderTimeBeforeStarting);


                if (StringUtils.isNotEmpty(supervisionTaskParentDO.getProcessInfo())){
                    ApproveInfoDTO approveInfoDTO = JSONObject.parseObject(supervisionTaskParentDO.getProcessInfo(), ApproveInfoDTO.class);
                    //自定义
                    if (approveInfoDTO.getApproveType()==1){
                        //只有一级审批人
                        List<String> approveList = queryFirstApproveList(userSubMap, userId, userSubordinateMap);
                        if (CollectionUtils.isEmpty(approveList)){
                            approveList.add(supervisionTaskParentDO.getCreateUserId());
                        }
                        supervisionTaskDO.setFirstApprove(String.format("%s%s%s", Constants.COMMA, approveList.stream().collect(Collectors.joining(Constants.COMMA)), Constants.COMMA));
                    }else {
                        String firstApprove = userSubordinateMap.getOrDefault(userId, supervisionTaskParentDO.getCreateUserId());
                        String secondaryApprove = secondaryApproveSubordinateMap.getOrDefault(firstApprove,supervisionTaskParentDO.getCreateUserId());
                        String thirdApprove  = thirdApproveSubordinateMap.getOrDefault(secondaryApprove,supervisionTaskParentDO.getCreateUserId());
                        if (approveInfoDTO.getApproveHierarchy()>=1){
                            supervisionTaskDO.setFirstApprove(String.format("%s%s%s", Constants.COMMA, firstApprove, Constants.COMMA));
                        }
                        if (approveInfoDTO.getApproveHierarchy()>=2){
                            supervisionTaskDO.setSecondaryApprove(String.format("%s%s%s", Constants.COMMA, secondaryApprove, Constants.COMMA));
                        }
                        if (approveInfoDTO.getApproveHierarchy()>=3){
                            supervisionTaskDO.setThirdApprove(String.format("%s%s%s", Constants.COMMA, thirdApprove, Constants.COMMA));
                        }
                    }
                }
                if (StringUtils.isNotEmpty(supervisionTaskParentDO.getSopIds())){
                    supervisionTaskDO.setSopIds(supervisionTaskParentDO.getSopIds());
                }
                if (StringUtils.isNotEmpty(supervisionTaskParentDO.getTaskGrouping())){
                    supervisionTaskDO.setTaskGrouping(supervisionTaskParentDO.getTaskGrouping());
                }
                supervisionTaskDO.setApproveStatus(approveStatus);
                result.add(supervisionTaskDO);
                HsUserStoreDTO hsUserStoreDTO = new HsUserStoreDTO();
                hsUserStoreDTO.setDingDingUserId(userId);
                subList.add(addTaskSub(supervisionTaskParentDO,hsUserStoreDTO,parentId));
                if (StringUtils.isNotEmpty(supervisionTaskParentDO.getFormId())){
                    //看一下是否返回ID
                    supervisionTaskDao.insertSelective(enterpriseId,supervisionTaskDO);
                    supervisionDefDataColumnDOS.addAll(buildSupervisionDefDataColumnList(enterpriseId, supervisionTaskParentDO.getCreateUserId(),supervisionTaskParentDO.getFormId(), supervisionTaskDO));
                }
            }
            if (StringUtils.isEmpty(supervisionTaskParentDO.getFormId())){
                supervisionTaskDao.batchInsert(enterpriseId,result);
            }

        }else {
            List<HsUserStoreDTO> hsUserStoreDTOS = new ArrayList<>();
            //分页查询每个人员有哪些门店的数据
            List<List<String>> partition = Lists.partition(userIdLists, 100);
            for (List<String> userIdList:partition) {
                List<HsUserStoreDTO> supervisorStores = hsStrategyCenterService.getSupervisorStores(enterpriseId,userIdList);
                if (CollectionUtils.isNotEmpty(supervisorStores)){
                    hsUserStoreDTOS.addAll(supervisorStores);
                }
            }
            //没有人员数据 直接退出
            if (CollectionUtils.isEmpty(hsUserStoreDTOS)){
                log.info("沪上真实人点关系为空:{}",JSONObject.toJSONString(hsUserStoreDTOS));
                return;
            }
            Map<String, List<HsStoreDTO>> stringListMap = hsUserStoreDTOS.stream().filter(x->CollectionUtils.isNotEmpty(x.getStoreList())).collect(Collectors.toMap(HsUserStoreDTO::getDingDingUserId, HsUserStoreDTO::getStoreList));

            for (String userId:userIdLists) {
                log.info("当前分解任务人员ID:{}",userId);
                SupervisionTaskDO supervisionTaskDO = new SupervisionTaskDO();
                supervisionTaskDO.setTaskParentId(parentId);
                supervisionTaskDO.setTaskName(supervisionTaskParentDO.getTaskName());
                supervisionTaskDO.setTaskStartTime(supervisionTaskParentDO.getTaskStartTime());
                supervisionTaskDO.setTaskState(Constants.ZERO);
                supervisionTaskDO.setFormId(supervisionTaskParentDO.getFormId());
                supervisionTaskDO.setSopIds(supervisionTaskParentDO.getSopIds());
                supervisionTaskDO.setBusinessType(supervisionTaskParentDO.getBusinessType());
                supervisionTaskDO.setTaskEndTime(supervisionTaskParentDO.getTaskEndTime());
                supervisionTaskDO.setPriority(supervisionTaskParentDO.getPriority());
                supervisionTaskDO.setSupervisionUserId(userId);
                if (StringUtils.isNotEmpty(supervisionTaskParentDO.getSopIds())){
                    supervisionTaskDO.setSopIds(supervisionTaskParentDO.getSopIds());
                }
                List<String> deptDingIdList = new ArrayList<>();
                if (StringUtils.isNotEmpty(supervisionTaskParentDO.getCheckStoreIds())){
                    List<HsStoreDTO> dingStoreList = stringListMap.get(userId);
                    log.info("当前分解任务人店关系dingStoreList:{}",JSONObject.toJSONString(dingStoreList));
                    if (CollectionUtils.isEmpty(dingStoreList)){
                        log.info("当前人员任务分解人店关系是空 不分解任务");
                        continue;
                    }
                    List<String> dingDingDeptId = dingStoreList.stream().map(HsStoreDTO::getDingDingDeptId).collect(Collectors.toList());
                    //人店关系
                    List<SingleStoreDTO> effectiveStoreByDingIdList = storeMapper.getSingleStoreByDingDeptIds(enterpriseId, dingDingDeptId);
                    deptDingIdList = effectiveStoreByDingIdList.stream().map(SingleStoreDTO::getStoreId).collect(Collectors.toList());
                    //如果是null 直接跳出
                    if (CollectionUtils.isEmpty(deptDingIdList)){
                        log.info("当前人员部门ID是空 不分解任务");
                        continue;
                    }
                    deptDingIdList.retainAll(storeIdList);
                    //取交集之后判null
                    if (CollectionUtils.isEmpty(deptDingIdList)){
                        log.info("当前人员门店取交集之后为空 不分解deptDingIdList：{} ",deptDingIdList);
                        continue;
                    }
                    supervisionTaskDO.setCheckObjectIds(deptDingIdList.stream().collect(Collectors.joining(",")));
                }
                supervisionTaskDO.setBusinessType(supervisionTaskParentDO.getBusinessType());
                supervisionTaskDO.setCreateTime(new Date());
                supervisionTaskDO.setSupervisionHandleUserId(userId);
                supervisionTaskDO.setSupervisionHandleUserName(userNameMap.get(userId));
                supervisionTaskDO.setReminderTimeBeforeEnd(reminderTimeBeforeEnd);
                supervisionTaskDO.setReminderTimeBeforeStarting(reminderTimeBeforeStarting);
                if (StringUtils.isNotEmpty(supervisionTaskParentDO.getTaskGrouping())){
                    supervisionTaskDO.setTaskGrouping(supervisionTaskParentDO.getTaskGrouping());
                }
                supervisionTaskDO.setApproveStatus(approveStatus);
                if (StringUtils.isNotEmpty(supervisionTaskParentDO.getProcessInfo())){
                    ApproveInfoDTO approveInfoDTO = JSONObject.parseObject(supervisionTaskParentDO.getProcessInfo(), ApproveInfoDTO.class);
                    if (approveInfoDTO.getApproveType()==1){
                        //只有一级审批人
                        List<String> approveList = queryFirstApproveList(userSubMap, userId, userSubordinateMap);
                        if (CollectionUtils.isEmpty(approveList)){
                            approveList.add(supervisionTaskParentDO.getCreateUserId());
                        }
                        supervisionTaskDO.setFirstApprove(String.format("%s%s%s", Constants.COMMA, approveList.stream().collect(Collectors.joining(Constants.COMMA)), Constants.COMMA));
                    }else {
                        String firstApprove = userSubordinateMap.getOrDefault(userId, supervisionTaskParentDO.getCreateUserId());
                        String secondaryApprove = secondaryApproveSubordinateMap.getOrDefault(firstApprove,supervisionTaskParentDO.getCreateUserId());
                        String thirdApprove  = thirdApproveSubordinateMap.getOrDefault(secondaryApprove,supervisionTaskParentDO.getCreateUserId());
                        if (approveInfoDTO.getApproveHierarchy()>=1){
                            supervisionTaskDO.setFirstApprove(String.format("%s%s%s", Constants.COMMA, firstApprove, Constants.COMMA));
                        }
                        if (approveInfoDTO.getApproveHierarchy()>=2){
                            supervisionTaskDO.setSecondaryApprove(String.format("%s%s%s", Constants.COMMA, secondaryApprove, Constants.COMMA));
                        }
                        if (approveInfoDTO.getApproveHierarchy()>=3){
                            supervisionTaskDO.setThirdApprove(String.format("%s%s%s", Constants.COMMA, thirdApprove, Constants.COMMA));
                        }
                    }
                }
                result.add(supervisionTaskDO);
                HsUserStoreDTO hsUserStoreDTO = new HsUserStoreDTO();
                hsUserStoreDTO.setDingDingUserId(userId);
                subList.add(addTaskSub(supervisionTaskParentDO,hsUserStoreDTO,parentId));
                supervisionTaskDao.insertSelective(enterpriseId,supervisionTaskDO);
                SupervisionStoreTaskDO supervisionStoreTaskDO = null;
                List<SupervisionStoreTaskDO> supervisionStoreTaskDOS = new ArrayList<>();
                for (String storeId:deptDingIdList) {
                    StoreDTO storeDTO = storeDTOMap.get(storeId);
                    if (storeDTO==null){
                        continue;
                    }
                    supervisionStoreTaskDO = new SupervisionStoreTaskDO();
                    supervisionStoreTaskDO.setSupervisionUserId(hsUserStoreDTO.getDingDingUserId());
                    supervisionStoreTaskDO.setStoreId(storeDTO.getStoreId());
                    supervisionStoreTaskDO.setStoreName(storeDTO.getStoreName());
                    supervisionStoreTaskDO.setTaskParentId(parentId);
                    supervisionStoreTaskDO.setRegionPath(storeDTO.getRegionPath());
                    supervisionStoreTaskDO.setTaskName(supervisionTaskParentDO.getTaskName());
                    supervisionStoreTaskDO.setTaskState(Constants.ZERO);
                    supervisionStoreTaskDO.setTaskStartTime(supervisionTaskParentDO.getTaskStartTime());
                    supervisionStoreTaskDO.setTaskState(Constants.ZERO);
                    supervisionStoreTaskDO.setFormId(supervisionTaskParentDO.getFormId());
                    supervisionStoreTaskDO.setPriority(supervisionTaskParentDO.getPriority());
                    supervisionStoreTaskDO.setSupervisionTaskId(supervisionTaskDO.getId());
                    supervisionStoreTaskDO.setSopIds(supervisionTaskParentDO.getSopIds());
                    supervisionStoreTaskDO.setBusinessType(supervisionTaskParentDO.getBusinessType());
                    supervisionStoreTaskDO.setTaskEndTime(supervisionTaskParentDO.getTaskEndTime());
                    supervisionStoreTaskDO.setTaskGrouping(supervisionTaskDO.getTaskGrouping());
                    supervisionStoreTaskDO.setFirstApprove(supervisionTaskDO.getFirstApprove());
                    supervisionStoreTaskDO.setSecondaryApprove(supervisionTaskDO.getSecondaryApprove());
                    supervisionStoreTaskDO.setThirdApprove(supervisionTaskDO.getThirdApprove());
                    supervisionStoreTaskDO.setReminderTimeBeforeStarting(supervisionTaskDO.getReminderTimeBeforeStarting());
                    supervisionStoreTaskDO.setReminderTimeBeforeEnd(supervisionTaskDO.getReminderTimeBeforeEnd());
                    supervisionStoreTaskDO.setApproveStatus(supervisionTaskDO.getApproveStatus());
                    supervisionStoreTaskDO.setSupervisionHandleUserId(supervisionTaskDO.getSupervisionHandleUserId());
                    supervisionStoreTaskDO.setSupervisionHandleUserName(supervisionTaskDO.getSupervisionHandleUserName());
                    if (StringUtils.isNotEmpty(supervisionTaskParentDO.getSopIds())){
                        supervisionStoreTaskDO.setSopIds(supervisionTaskParentDO.getSopIds());
                    }
                    supervisionStoreTaskDOS.add(supervisionStoreTaskDO);
                    if (StringUtils.isNotEmpty(supervisionTaskParentDO.getFormId())){
                        //看一下是否需要
                        supervisionStoreTaskDao.insertSelective(supervisionStoreTaskDO,enterpriseId);
                        supervisionDefDataColumnDOS.addAll(buildStoreSupervisionDefDataColumnList(enterpriseId,supervisionTaskParentDO.getCreateUserId(), supervisionTaskParentDO.getFormId(), supervisionStoreTaskDO));
                    }
                }
                //如果没有填写表单 可以直接批量新增
                if (StringUtils.isEmpty(supervisionTaskParentDO.getFormId())){
                    supervisionStoreTaskDao.batchInsert(enterpriseId,supervisionStoreTaskDOS);
                }
            }
        }
        //批量新增
        supervisionDefDataColumnDao.batchInsert(enterpriseId,supervisionDefDataColumnDOS);
        if (CollectionUtils.isNotEmpty(subList)){
            taskSubMapper.batchInsertTaskSub(enterpriseId,subList);
        }
        // 发送待办
        for (SupervisionTaskDO supervisionTaskDO : result) {
            jmsTaskService.sendSupervisionTaskBacklogByTaskId(enterpriseId, supervisionTaskDO.getId());
        }
    }


    /**
     * 查询用户(去重)
     * @param enterpriseId
     * @param generalDTOS
     * @return
     */
    public List<String> getUserId(String enterpriseId,List<GeneralDTO> generalDTOS){
        Set<String> personList = Sets.newHashSet();
        List<Long> positionList = Lists.newArrayList();
        //区分选择的人与职位
        personList.addAll(generalDTOS.stream().filter(f -> UnifyTaskConstant.PersonType.PERSON.equals(f.getType()))
                .map(GeneralDTO::getValue).collect(Collectors.toList()));
        positionList.addAll(generalDTOS.stream().filter(f -> UnifyTaskConstant.PersonType.POSITION.equals(f.getType()))
                .map(GeneralDTO::getValue).map(Long::parseLong).collect(Collectors.toList()));
        List<String> groupIdList = generalDTOS.stream().filter(x -> UnifyTaskConstant.PersonType.USER_GROUP.equals(x.getType()))
                .map(GeneralDTO::getValue).collect(Collectors.toList());
        List<String> regionIdList = generalDTOS.stream().filter(x -> UnifyTaskConstant.PersonType.ORGANIZATION.equals(x.getType()))
                .map(GeneralDTO::getValue).collect(Collectors.toList());
        // 根据职位获取人员
        personList.addAll(enterpriseUserRoleDao.selectUserIdsByRoleIdList(enterpriseId, positionList));
        if (CollectionUtils.isNotEmpty(groupIdList)){
            List<String> userIds = enterpriseUserGroupMappingDao.getUserIdsByGroupIdList(enterpriseId, groupIdList);
            if(CollectionUtils.isNotEmpty(userIds)){
                //添加分组的人员
                personList.addAll(userIds);
            }
        }
        if (CollectionUtils.isNotEmpty(regionIdList)){
            List<String> userIdsList = enterpriseUserMapper.getUserIdsByRegionIdList(enterpriseId, regionIdList);
            if(CollectionUtils.isNotEmpty(userIdsList)){
                personList.addAll(userIdsList);
            }
        }
        if (CollectionUtils.isEmpty(personList)){
            return new ArrayList<>();
        }
        //过滤离职人员
        List<String> list = enterpriseUserMapper.selectUserIdsByUserList(enterpriseId, new ArrayList<>(personList));
        return list;
    }


    /**
     * 查询用户的一级审批人
     * @param userSubMap 审批人管辖用户map
     * @param userId 执行人ID
     * @param userSubordinateMap 执行人直属上级map
     * @return
     */
    public List<String> queryFirstApproveList(HashMap<String, List<String>> userSubMap,String userId,Map<String, String> userSubordinateMap){
        Set<String> resultSet = new HashSet<>();
        //执行人属于审批人管辖
        for (Map.Entry<String, List<String>> entry : userSubMap.entrySet()) {
            if (entry.getValue().contains(userId)){
                resultSet.add(entry.getKey());
            }
        }
        return new ArrayList<>(resultSet);
    }



    /**
     * add supervision task to sub
     * @param supervisionTaskParentDO
     * @param hsUserStoreDTO
     * @param id
     * @return
     */
    public TaskSubDO addTaskSub(SupervisionTaskParentDO supervisionTaskParentDO,HsUserStoreDTO hsUserStoreDTO,Long id){
        TaskSubDO taskSubDO  = new TaskSubDO();
        taskSubDO.setUnifyTaskId(id);
        taskSubDO.setCreateTime(System.currentTimeMillis());
        taskSubDO.setSubBeginTime(supervisionTaskParentDO.getTaskStartTime().getTime());
        taskSubDO.setSubEndTime(supervisionTaskParentDO.getTaskEndTime().getTime());
        taskSubDO.setRemark(supervisionTaskParentDO.getRemark());
        taskSubDO.setCreateUserId(supervisionTaskParentDO.getCreateUserId());
        taskSubDO.setHandleUserId(hsUserStoreDTO.getDingDingUserId());
        taskSubDO.setLoopCount(1L);
        taskSubDO.setTaskType(TaskTypeEnum.SUPERVISION.getCode());
        taskSubDO.setSubTaskCode(id+"#");
        taskSubDO.setSubStatus(UnifyStatus.ONGOING.getCode());
        taskSubDO.setNodeNo("1");
        return taskSubDO;
    }

    /**
     * buildStoreSupervisionDefDataColumnList
     * @param eid
     * @param createUSerId
     * @param formId
     * @param supervisionStoreTaskDO
     * @return
     */
    private List<SupervisionDefDataColumnDO> buildStoreSupervisionDefDataColumnList(String eid,String createUSerId,String formId,SupervisionStoreTaskDO supervisionStoreTaskDO){
        List<SupervisionDefDataColumnDO> result = new ArrayList<>();
        List<TbMetaDefTableColumnDO> tbMetaDefTableColumnDOS = tbMetaDefTableColumnMapper.selectByTableId(eid, Long.valueOf(formId));
        for (TbMetaDefTableColumnDO tbMetaDefTableColumnDO:tbMetaDefTableColumnDOS) {
            SupervisionDefDataColumnDO supervisionDefDataColumnDO = new SupervisionDefDataColumnDO();
            supervisionDefDataColumnDO.setTaskParentId(supervisionStoreTaskDO.getTaskParentId());
            supervisionDefDataColumnDO.setTaskId(supervisionStoreTaskDO.getId());
            supervisionDefDataColumnDO.setStoreId(supervisionStoreTaskDO.getStoreId());
            supervisionDefDataColumnDO.setStoreName(supervisionStoreTaskDO.getStoreName());
            supervisionDefDataColumnDO.setMetaTableId(tbMetaDefTableColumnDO.getMetaTableId());
            supervisionDefDataColumnDO.setMetaColumnId(tbMetaDefTableColumnDO.getId());
            supervisionDefDataColumnDO.setMetaColumnName(tbMetaDefTableColumnDO.getColumnName());
            supervisionDefDataColumnDO.setDescription(tbMetaDefTableColumnDO.getDescription());
            supervisionDefDataColumnDO.setCreateUserId(createUSerId);
            supervisionDefDataColumnDO.setType("store");
            result.add(supervisionDefDataColumnDO);
        }
        return result;
    }

    /**
     * buildSupervisionDefDataColumnList
     * @param eid
     * @param createUSerId
     * @param formId
     * @param supervisionTaskDO
     * @return
     */
    private List<SupervisionDefDataColumnDO> buildSupervisionDefDataColumnList(String eid,String createUSerId,String formId,SupervisionTaskDO supervisionTaskDO){
        List<SupervisionDefDataColumnDO> result = new ArrayList<>();
        List<TbMetaDefTableColumnDO> tbMetaDefTableColumnDOS = tbMetaDefTableColumnMapper.selectByTableId(eid, Long.valueOf(formId));
        for (TbMetaDefTableColumnDO tbMetaDefTableColumnDO:tbMetaDefTableColumnDOS) {
            SupervisionDefDataColumnDO supervisionDefDataColumnDO = new SupervisionDefDataColumnDO();
            supervisionDefDataColumnDO.setTaskParentId(supervisionTaskDO.getTaskParentId());
            supervisionDefDataColumnDO.setTaskId(supervisionTaskDO.getId());
            supervisionDefDataColumnDO.setMetaTableId(tbMetaDefTableColumnDO.getMetaTableId());
            supervisionDefDataColumnDO.setMetaColumnId(tbMetaDefTableColumnDO.getId());
            supervisionDefDataColumnDO.setMetaColumnName(tbMetaDefTableColumnDO.getColumnName());
            supervisionDefDataColumnDO.setDescription(tbMetaDefTableColumnDO.getDescription());
            supervisionDefDataColumnDO.setCreateUserId(createUSerId);
            supervisionDefDataColumnDO.setType("person");
            result.add(supervisionDefDataColumnDO);
        }
        return result;
    }

    @Override
    public PageInfo<SupervisionTaskParentVO> getSupervisionTaskParentList(String enterpriseId, String keyWords, Long startTime, Long endTime, List<SupervisionParentStatusEnum> statusEnumList,
                                                                          Integer pageSize, Integer pageNum, List<SupervisionTaskPriorityEnum> supervisionTaskPriorityEnums, List<String> taskGroupingList, List<String> tags) {
        if (pageNum==null||pageSize==null){
            pageNum = Constants.INDEX_ONE;
            pageSize = Constants.PAGE_SIZE_TEN;
        }
        if (pageSize>Constants.PAGE_SIZE){
            throw new ServiceException(ErrorCodeEnum.PAGE_SIZE_MAX);
        }
        Date startTimeDate = null;
        Date endTimeDate = null;
        try {
            if (startTime!=null){
                startTimeDate = DateUtil.longToDate(startTime);
            }
            if (endTime!=null){
                endTimeDate = DateUtil.longToDate(endTime);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        List<Integer> statusInt = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(statusEnumList)){
            for (SupervisionParentStatusEnum x : statusEnumList) {
                statusInt.add(x.getStatus());
            }
        }
        List<String> supervisionTaskPriorityList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(supervisionTaskPriorityEnums)){
            for (SupervisionTaskPriorityEnum x : supervisionTaskPriorityEnums) {
                supervisionTaskPriorityList.add(x.getCode());
            }
        }
        PageHelper.startPage(pageNum,pageSize);
        List<SupervisionTaskParentDO> supervisionTaskParentDOS = supervisionTaskParentDao.listByCondition(enterpriseId, keyWords, startTimeDate, endTimeDate, statusInt,supervisionTaskPriorityList,taskGroupingList,tags);
        PageInfo supervisionTaskParentDOPageInfo = new PageInfo<>(supervisionTaskParentDOS);

        supervisionTaskParentDOPageInfo.setList(convertListVO(enterpriseId,supervisionTaskParentDOS));
        return supervisionTaskParentDOPageInfo;
    }

    @Override
    public Boolean taskCancel(String enterpriseId, Long taskId, EnterpriseConfigDO enterpriseConfigDO) {
        SupervisionTaskParentDO supervisionTaskParentDO = supervisionTaskParentDao.selectByPrimaryKey(taskId, enterpriseId);
        if (supervisionTaskParentDO==null){
            throw  new ServiceException(ErrorCodeEnum.SUPERVISION_TASK_PARENT_NOT_EXIST);
        }
        //父任务取消
        supervisionTaskParentDao.taskParentCancel(enterpriseId,taskId);
        supervisionApproveDao.batchDeleteByTaskParentId(enterpriseId,null,null,taskId);
        //按人任务取消
        supervisionTaskDao.taskCancel(enterpriseId,taskId,null);
        //门店任务取消
        supervisionStoreTaskDao.storeTaskCancel(enterpriseId,taskId,null,null);
        //cancelUpcoming
        List<SupervisionTaskDO> supervisionTaskDOS = supervisionTaskDao.listSupervisionTaskByParentId(enterpriseId, taskId, null, null,null);
        for (SupervisionTaskDO supervisionTaskDO:supervisionTaskDOS) {
            supervisionTaskService.cancelUpcoming(enterpriseId,enterpriseConfigDO.getDingCorpId(),enterpriseConfigDO.getAppType(),supervisionTaskDO.getId());
        }
        return Boolean.TRUE;
    }

    @Override
    public Boolean taskDel(String enterpriseId, Long taskId,EnterpriseConfigDO enterpriseConfigDO) {
        SupervisionTaskParentDO supervisionTaskParentDO = supervisionTaskParentDao.selectByPrimaryKey(taskId, enterpriseId);
        if (supervisionTaskParentDO==null){
            throw  new ServiceException(ErrorCodeEnum.SUPERVISION_TASK_PARENT_NOT_EXIST);
        }
        //父任务删除
        supervisionTaskParentDao.taskParentDel(enterpriseId,taskId);
        //删除审批中审批任务
        supervisionApproveDao.batchDeleteByTaskParentId(enterpriseId,null,null,taskId);
        //按人任务删除
        supervisionTaskDao.taskDel(enterpriseId,taskId);
        //按门店任务删除
        supervisionStoreTaskDao.storeTaskDel(enterpriseId,taskId);
        //cancelUpcoming 钉钉待办只有按人层面 ，所有只需要取消按人发的钉钉待办
        List<SupervisionTaskDO> supervisionTaskDOS = supervisionTaskDao.listSupervisionTaskByParentId(enterpriseId, taskId, null, null,null);
        for (SupervisionTaskDO supervisionTaskDO:supervisionTaskDOS) {
            //表示是纯人任务 删除按人任务待办
            if (StringUtils.isEmpty(supervisionTaskParentDO.getCheckStoreIds())){
                //取消待完成人的待办
                supervisionTaskService.cancelUpcoming(enterpriseId,enterpriseConfigDO.getDingCorpId(),enterpriseConfigDO.getAppType(),supervisionTaskDO.getId());
            }else {
                //门店任务
                if (supervisionTaskDO.getTaskState()==0){
                    //取消待完成人的待办
                    supervisionTaskService.cancelUpcoming(enterpriseId,enterpriseConfigDO.getDingCorpId(),enterpriseConfigDO.getAppType(),supervisionTaskDO.getId());
                }
                if (supervisionTaskDO.getTaskState()==4){
                    //取消审批人的待办
                    supervisionStoreTaskService.cancelSupervisionStoreTaskUpcoming(enterpriseId,enterpriseConfigDO.getDingCorpId(),enterpriseConfigDO.getAppType(),supervisionTaskDO.getId());
                }
            }

        }
        return Boolean.TRUE;
    }

    @Override
    public PageInfo<SupervisionTaskDataVO> listSupervisionTaskByParentId(String enterpriseId, Long parentId, String  userName, List<SupervisionSubTaskStatusEnum> completeStatusList, Integer pageSize, Integer pageNum,Integer handleOverTimeStatus) {
        SupervisionTaskParentDO supervisionTaskParentDO = supervisionTaskParentDao.selectByPrimaryKey(parentId, enterpriseId);
        if (supervisionTaskParentDO==null){
            throw  new ServiceException(ErrorCodeEnum.SUPERVISION_TASK_PARENT_NOT_EXIST);
        }
        if (pageNum==null||pageSize==null){
            pageNum = Constants.INDEX_ONE;
            pageSize = Constants.PAGE_SIZE_TEN;
        }
        if (pageSize>Constants.PAGE_SIZE){
            throw new ServiceException(ErrorCodeEnum.PAGE_SIZE_MAX);
        }
        PageHelper.startPage(pageNum,pageSize);
        List<SupervisionTaskDO> supervisionTaskDOS = supervisionTaskDao.listSupervisionTaskByParentId(enterpriseId, parentId, userName, completeStatusList,handleOverTimeStatus);
        PageInfo result = new PageInfo<>(supervisionTaskDOS);
        if (CollectionUtils.isEmpty(supervisionTaskDOS)){
            return result;
        }
        List<SupervisionDefDataColumnDTO> dataColumnListByTaskIdAndType = new ArrayList<>();
        List<TbMetaDefTableColumnDO> tbMetaDefTableColumnDOS = new ArrayList<>();
        if (StringUtils.isNotEmpty(supervisionTaskParentDO.getFormId())){
            List<Long> taskIds = supervisionTaskDOS.stream().map(SupervisionTaskDO::getId).collect(Collectors.toList());
            tbMetaDefTableColumnMapper.selectByTableId(enterpriseId,Long.valueOf(supervisionTaskParentDO.getFormId()));
            dataColumnListByTaskIdAndType = supervisionDefDataColumnService.getDataColumnListByTaskIdAndType(enterpriseId, taskIds, "person");
            tbMetaDefTableColumnDOS = tbMetaDefTableColumnMapper.selectByTableId(enterpriseId, Long.valueOf(supervisionTaskParentDO.getFormId()));
        }
        result.setList(convertSupervisionTaskToVO(enterpriseId,supervisionTaskDOS,tbMetaDefTableColumnDOS,dataColumnListByTaskIdAndType));
        return result;
    }

    @Override
    public PageInfo<SupervisionStoreTaskDataVO> listSupervisionStoreTaskByParentId(String enterpriseId, Long parentId,Long supervisionTaskId, List<String> storeIds,List<String> regionIds,String userName, List<SupervisionSubTaskStatusEnum> completeStatusList, Integer pageSize, Integer pageNum,Integer handleOverTimeStatus) {
        Long taskParentId = parentId;
        if (taskParentId==null&&supervisionTaskId!=null){
            SupervisionTaskDO supervisionTaskDO = supervisionTaskDao.selectByPrimaryKey(supervisionTaskId, enterpriseId);
            if (supervisionTaskDO==null){
                throw  new ServiceException(ErrorCodeEnum.SUPERVISION_TASK_NOT_EXIST);
            }
            taskParentId = supervisionTaskDO.getTaskParentId();
        }
        SupervisionTaskParentDO supervisionTaskParentDO = supervisionTaskParentDao.selectByPrimaryKey(taskParentId, enterpriseId);
        if (supervisionTaskParentDO==null){
            throw  new ServiceException(ErrorCodeEnum.SUPERVISION_TASK_PARENT_NOT_EXIST);
        }
        if (pageNum==null||pageSize==null){
            pageNum = Constants.INDEX_ONE;
            pageSize = Constants.PAGE_SIZE_TEN;
        }
        if (pageSize>Constants.PAGE_SIZE){
            throw new ServiceException(ErrorCodeEnum.PAGE_SIZE_MAX);
        }
        PageHelper.startPage(pageNum,pageSize);
        List<SupervisionStoreTaskDO> supervisionStoreTaskDOS = supervisionStoreTaskDao.listSupervisionTaskByParentId(enterpriseId, parentId,supervisionTaskId, storeIds,regionIds, completeStatusList,userName,handleOverTimeStatus);
        PageInfo result = new PageInfo<>(supervisionStoreTaskDOS);
        if (CollectionUtils.isEmpty(supervisionStoreTaskDOS)){
            return result;
        }

        List<SupervisionDefDataColumnDTO> dataColumnListByTaskIdAndType = new ArrayList<>();
        List<TbMetaDefTableColumnDO> tbMetaDefTableColumnDOS = new ArrayList<>();
        if (StringUtils.isNotEmpty(supervisionTaskParentDO.getFormId())){
            List<Long> taskIds = supervisionStoreTaskDOS.stream().map(SupervisionStoreTaskDO::getId).collect(Collectors.toList());
            dataColumnListByTaskIdAndType = supervisionDefDataColumnService.getDataColumnListByTaskIdAndType(enterpriseId, taskIds, "store");
            tbMetaDefTableColumnDOS = tbMetaDefTableColumnMapper.selectByTableId(enterpriseId, Long.valueOf(supervisionTaskParentDO.getFormId()));
        }
        result.setList(convertSupervisionStoreTaskToVO(enterpriseId,supervisionStoreTaskDOS,tbMetaDefTableColumnDOS,dataColumnListByTaskIdAndType));
        return result;
    }


    @Override
    public PageInfo<SupervisionStoreTaskDataVO> taskDetail(String enterpriseId, Long tbMetaTableId, List<Long> parentIds, Long submitStartTime, Long submitEndTime, String type, Integer pageSize, Integer pageNum) {
        if (tbMetaTableId==null){
            throw new ServiceException(ErrorCodeEnum.PARAM_MISSING);
        }
        Date startTimeDate = null;
        Date endTimeDate = null;
        try {
            if (submitStartTime!=null){
                startTimeDate = DateUtil.longToDate(submitStartTime);
            }
            if (submitEndTime!=null){
                endTimeDate = DateUtil.longToDate(submitEndTime);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        List<Long> taskIds = new ArrayList<>();
        PageInfo pageInfo = new PageInfo();
        List<TbMetaDefTableColumnDO> tbMetaDefTableColumnDOS = tbMetaDefTableColumnMapper.selectByTableId(enterpriseId, tbMetaTableId);
        PageHelper.startPage(pageNum,pageSize);
        if ("store".equals(type)){
            List<SupervisionStoreTaskDO> supervisionStoreTaskDOS = supervisionStoreTaskDao.listSupervisionStoreTaskByFormId(enterpriseId, String.valueOf(tbMetaTableId), parentIds, startTimeDate, endTimeDate);
            if (CollectionUtils.isEmpty(supervisionStoreTaskDOS)){
                return pageInfo;
            }
            taskIds = supervisionStoreTaskDOS.stream().map(SupervisionStoreTaskDO::getId).collect(Collectors.toList());
            pageInfo = new PageInfo(supervisionStoreTaskDOS);
            List<String> userIds = supervisionStoreTaskDOS.stream().map(SupervisionStoreTaskDO::getSupervisionHandleUserId).collect(Collectors.toList());
            List<EnterpriseUserDTO> userDTOList = enterpriseUserService.getUserByUserIds(enterpriseId, userIds);
            Map<String, EnterpriseUserDTO> userMap = userDTOList.stream().collect(Collectors.toMap(EnterpriseUserDTO::getUserId, Function.identity()));
            List<SupervisionDefDataColumnDTO> dataColumnListByTaskIdAndType = supervisionDefDataColumnService.getDataColumnListByTaskIdAndType(enterpriseId, taskIds, type);
            Map<Long, List<SupervisionDefDataColumnDTO>> listMap = dataColumnListByTaskIdAndType.stream().collect(Collectors.groupingBy(SupervisionDefDataColumnDTO::getTaskId));
            List<SupervisionStoreTaskDataVO> supervisionStoreTaskDataVOS = new ArrayList<>();
            for (SupervisionStoreTaskDO supervisionStoreTaskDO:supervisionStoreTaskDOS) {
                SupervisionStoreTaskDataVO supervisionStoreTaskDataVO = new SupervisionStoreTaskDataVO();
                supervisionStoreTaskDataVO.setStoreId(supervisionStoreTaskDO.getStoreId());
                supervisionStoreTaskDataVO.setStoreName(supervisionStoreTaskDO.getStoreName());
                supervisionStoreTaskDataVO.setSupervisionUserId(supervisionStoreTaskDO.getSupervisionHandleUserId());
                supervisionStoreTaskDataVO.setSupervisionUserName(userMap.getOrDefault(supervisionStoreTaskDO.getSupervisionHandleUserId(),new EnterpriseUserDTO()).getName());
                supervisionStoreTaskDataVO.setRoleName(userMap.getOrDefault(supervisionStoreTaskDO.getSupervisionHandleUserId(),new EnterpriseUserDTO()).getRoleName());
                supervisionStoreTaskDataVO.setDepartment(userMap.getOrDefault(supervisionStoreTaskDO.getSupervisionHandleUserId(),new EnterpriseUserDTO()).getDepartment());
                supervisionStoreTaskDataVO.setId(supervisionStoreTaskDO.getId());
                supervisionStoreTaskDataVO.setTaskName(supervisionStoreTaskDO.getTaskName());
                supervisionStoreTaskDataVO.setSubmitTime(supervisionStoreTaskDO.getSubmitTime());
                supervisionStoreTaskDataVO.setTbMetaDefTableColumnDOS(tbMetaDefTableColumnDOS);
                supervisionStoreTaskDataVO.setSupervisionDefDataColumnDTOS(listMap.get(supervisionStoreTaskDO.getId()));
                supervisionStoreTaskDataVOS.add(supervisionStoreTaskDataVO);
            }
            pageInfo.setList(supervisionStoreTaskDataVOS);
            return pageInfo;
        }
        if ("person".equals(type)){
            List<SupervisionTaskDO> supervisionTaskDOS = supervisionTaskDao.listSupervisionTaskByFormId(enterpriseId, String.valueOf(tbMetaTableId), parentIds, startTimeDate, endTimeDate);
            if (CollectionUtils.isEmpty(supervisionTaskDOS)){
                return pageInfo;
            }
            taskIds = supervisionTaskDOS.stream().map(SupervisionTaskDO::getId).collect(Collectors.toList());
            pageInfo = new PageInfo(supervisionTaskDOS);
            List<String> userIds = supervisionTaskDOS.stream().map(SupervisionTaskDO::getSupervisionHandleUserId).collect(Collectors.toList());
            List<EnterpriseUserDTO> userDTOList = enterpriseUserService.getUserByUserIds(enterpriseId, userIds);
            Map<String, EnterpriseUserDTO> userMap = userDTOList.stream().collect(Collectors.toMap(EnterpriseUserDTO::getUserId, Function.identity()));
            List<SupervisionDefDataColumnDTO> dataColumnListByTaskIdAndType = supervisionDefDataColumnService.getDataColumnListByTaskIdAndType(enterpriseId, taskIds, type);
            Map<Long, List<SupervisionDefDataColumnDTO>> listMap = dataColumnListByTaskIdAndType.stream().collect(Collectors.groupingBy(SupervisionDefDataColumnDTO::getTaskId));
            List<SupervisionStoreTaskDataVO> supervisionStoreTaskDataVOS = new ArrayList<>();
            for (SupervisionTaskDO supervisionTaskDO:supervisionTaskDOS) {
                SupervisionStoreTaskDataVO supervisionStoreTaskDataVO = new SupervisionStoreTaskDataVO();
                supervisionStoreTaskDataVO.setSupervisionUserId(supervisionTaskDO.getSupervisionHandleUserId());
                supervisionStoreTaskDataVO.setSupervisionUserName(userMap.getOrDefault(supervisionTaskDO.getSupervisionHandleUserId(),new EnterpriseUserDTO()).getName());
                supervisionStoreTaskDataVO.setRoleName(userMap.getOrDefault(supervisionTaskDO.getSupervisionHandleUserId(),new EnterpriseUserDTO()).getRoleName());
                supervisionStoreTaskDataVO.setDepartment(userMap.getOrDefault(supervisionTaskDO.getSupervisionHandleUserId(),new EnterpriseUserDTO()).getDepartment());
                supervisionStoreTaskDataVO.setId(supervisionTaskDO.getId());
                supervisionStoreTaskDataVO.setTaskName(supervisionTaskDO.getTaskName());
                supervisionStoreTaskDataVO.setSubmitTime(supervisionTaskDO.getCompleteTime());
                supervisionStoreTaskDataVO.setTbMetaDefTableColumnDOS(tbMetaDefTableColumnDOS);
                supervisionStoreTaskDataVO.setSupervisionDefDataColumnDTOS(listMap.get(supervisionTaskDO.getId()));
                supervisionStoreTaskDataVOS.add(supervisionStoreTaskDataVO);
            }
            pageInfo.setList(supervisionStoreTaskDataVOS);
            return pageInfo;
        }
        return pageInfo;
    }

    @Override
    public ImportTaskDO taskDetailExport(String enterpriseId, Long tbMetaTableId, List<Long> parentIds, Long submitStartTime, Long submitEndTime, String type, CurrentUser user) {
        // 查询导出数量，限流
        Long count = 0L;
        Date startTimeDate = null;
        Date endTimeDate = null;
        try {
            if (submitStartTime!=null){
                startTimeDate = DateUtil.longToDate(submitStartTime);
            }
            if (submitEndTime!=null){
                endTimeDate = DateUtil.longToDate(submitEndTime);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if ("store".equals(type)){
            count = supervisionStoreTaskDao.countSupervisionStoreTaskByFormId(enterpriseId,String.valueOf(tbMetaTableId),parentIds,startTimeDate,endTimeDate);
        }else if ("person".equals(type)){
            count = supervisionTaskDao.countSupervisionTaskByFormId(enterpriseId,String.valueOf(tbMetaTableId),parentIds,startTimeDate,endTimeDate);
        }
        if (count == null || count == 0L) {
            throw new ServiceException("当前无记录可导出");
        }
        if (count > Constants.MAX_EXPORT_SIZE) {
            throw new ServiceException("导出数据不能超过" + Constants.MAX_EXPORT_SIZE + "条，请缩小导出范围");
        }
        // 通过枚举获取文件名称 ExportServiceEnum
        String fileName = ExportTemplateEnum.getByCode(ImportTaskConstant.SUPERVISION_DATA_DETAIL_REPORT);
        // 保存导出任务
        ImportTaskDO importTaskDO = importTaskService.insertExportTask(enterpriseId, fileName, ImportTaskConstant.SUPERVISION_DATA_DETAIL_REPORT);
        // 构造异步导出参数
        SupervisionDataDetailListExportRequest msg = new SupervisionDataDetailListExportRequest();
        msg.setEnterpriseId(enterpriseId);
        msg.setParentIds(parentIds);
        msg.setEndTimeDate(submitEndTime);
        msg.setTbMetaTableId(String.valueOf(tbMetaTableId));
        msg.setStartTimeDate(submitStartTime);
        msg.setUser(user);
        msg.setType(type);
        msg.setTotalNum(count);
        msg.setImportTaskDO(importTaskDO);
        msg.setDbName(user.getDbName());
        MsgUniteData msgUniteData = new MsgUniteData();
        msgUniteData.setData(JSONObject.toJSONString(msg));
        msgUniteData.setMsgType(MsgUniteDataTypeEnum.SUPERVISION_DATA_DETAIL_EXPORT.getCode());
        //分开异步导出
        simpleMessageService.send(JSONObject.toJSONString(msgUniteData), RocketMqTagEnum.EXPORT_IMPORT_MESSAGE);
        return importTaskDO;
    }


    /**
     * 按人任务订正
     * @param enterpriseId
     */
    private void personHistoryCorrect(String enterpriseId){
        //查询完成的数据 写入历史记录
        int innerPageNum = 1;
        boolean innerHasNext = true;
        int pageSize = 100;
        while (innerHasNext) {
            PageHelper.startPage(innerPageNum, pageSize);
            List<SupervisionTaskDO> supervisionTaskDOS = supervisionTaskDao.correctData(enterpriseId);
            PageInfo<SupervisionTaskDO> supervisionTaskDOPageInfo = new PageInfo<>(supervisionTaskDOS);
            innerHasNext = supervisionTaskDOPageInfo.isHasNextPage();
            List<SupervisionHistoryDO> supervisionHistoryDOS = new ArrayList<>();
            for (SupervisionTaskDO temp : supervisionTaskDOS) {
                CurrentUser currentUser = new CurrentUser();
                currentUser.setUserId(temp.getSupervisionHandleUserId());
                currentUser.setName(temp.getSupervisionHandleUserName());
                supervisionHistoryDOS.add(supervisionTaskService.handleSupervisionHistory(temp.getId(),"person",ActionTypeEnum.HANDLE.name(),currentUser,0));
            }
            //每次插入100条
            supervisionHistoryDao.batchInsert(enterpriseId,supervisionHistoryDOS);
            innerPageNum++;
        }
    }

    /**
     * 按门店任务订正
     * @param enterpriseId
     */
    private void storeHistoryCorrect(String enterpriseId){
        //查询完成的数据 写入历史记录
        int innerPageNum = 1;
        boolean innerHasNext = true;
        int pageSize = 100;
        while (innerHasNext) {
            PageHelper.startPage(innerPageNum, pageSize);
            List<SupervisionStoreTaskDO> supervisionStoreTaskDOS = supervisionStoreTaskDao.correctData(enterpriseId);
            PageInfo<SupervisionStoreTaskDO> supervisionTaskStorePage = new PageInfo<>(supervisionStoreTaskDOS);
            innerHasNext = supervisionTaskStorePage.isHasNextPage();
            List<SupervisionHistoryDO> supervisionHistoryDOS = new ArrayList<>();
            for (SupervisionStoreTaskDO temp : supervisionStoreTaskDOS) {
                CurrentUser currentUser = new CurrentUser();
                currentUser.setUserId(temp.getSupervisionHandleUserId());
                currentUser.setName(temp.getSupervisionHandleUserName());
                supervisionHistoryDOS.add(supervisionTaskService.handleSupervisionHistory(temp.getId(),"store",ActionTypeEnum.HANDLE.name(),currentUser,0));
            }
            //每次插入100条
            supervisionHistoryDao.batchInsert(enterpriseId,supervisionHistoryDOS);
            innerPageNum++;
        }
    }



    @Override
    public List<SupervisionHistoryHandleVO> getSupervisionHistoryHandleVO(String eid, Long taskId, String type) {
        List<SupervisionHistoryHandleVO> result = new ArrayList<>();
        List<SupervisionHistoryDO> supervisionHistoryDOS = supervisionHistoryDao.selectByTaskIdAndType(eid, taskId, type,Boolean.FALSE);
        if (CollectionUtils.isEmpty(supervisionHistoryDOS)){
            return Collections.emptyList();
        }
        List<String> userIds = supervisionHistoryDOS.stream().map(SupervisionHistoryDO::getOperateUserId).collect(Collectors.toList());
        List<EnterpriseUserSingleDTO> enterpriseUserSingleDTOS = enterpriseUserDao.usersByUserIdList(eid, userIds);
        Map<String, String> userMap = enterpriseUserSingleDTOS.stream().filter(x->(StringUtils.isNotEmpty(x.getUserId())&&StringUtils.isNotEmpty(x.getAvatar()))).collect(Collectors.toMap(EnterpriseUserSingleDTO::getUserId, EnterpriseUserSingleDTO::getAvatar));
        for (SupervisionHistoryDO supervisionHistoryDO:supervisionHistoryDOS) {
            SupervisionHistoryHandleVO supervisionHistoryHandleVO = new SupervisionHistoryHandleVO();
            supervisionHistoryHandleVO.setOperateUserId(supervisionHistoryDO.getOperateUserId());
            supervisionHistoryHandleVO.setOperateUserName(supervisionHistoryDO.getOperateUserName());
            supervisionHistoryHandleVO.setNodeNo(supervisionHistoryDO.getNodeNo());
            supervisionHistoryHandleVO.setAvatar(userMap.get(supervisionHistoryDO.getOperateUserId()));
            supervisionHistoryHandleVO.setOperateType(supervisionHistoryDO.getOperateType().toLowerCase());
            supervisionHistoryHandleVO.setId(taskId);
            supervisionHistoryHandleVO.setToUserId(supervisionHistoryDO.getToUserId());
            supervisionHistoryHandleVO.setToUserName(supervisionHistoryDO.getToUserName());
            supervisionHistoryHandleVO.setCreateTime(supervisionHistoryDO.getCreateTime());
            supervisionHistoryHandleVO.setActionKey(supervisionHistoryDO.getActionKey());
            supervisionHistoryHandleVO.setRemark(supervisionHistoryDO.getRemark());
            result.add(supervisionHistoryHandleVO);
        }
        return result;
    }



    @Override
    public ImportTaskDO listSupervisionTaskByParentIdExport(String enterpriseId, Long parentId, String userName, List<SupervisionSubTaskStatusEnum> completeStatusList, CurrentUser user,Integer handleOverTimeStatus) {
        // 查询导出数量，限流
        Long count = supervisionTaskDao.countByParentId(enterpriseId,parentId,userName,completeStatusList,handleOverTimeStatus);
        if (count == null || count == 0L) {
            throw new ServiceException("当前无记录可导出");
        }
        if (count > Constants.MAX_EXPORT_SIZE) {
            throw new ServiceException("导出数据不能超过" + Constants.MAX_EXPORT_SIZE + "条，请缩小导出范围");
        }
        // 通过枚举获取文件名称 ExportServiceEnum
        String fileName = ExportTemplateEnum.getByCode(ImportTaskConstant.SUPERVISION_DATA_LIST_REPORT);
        // 保存导出任务
        ImportTaskDO importTaskDO = importTaskService.insertExportTask(enterpriseId, fileName, ImportTaskConstant.SUPERVISION_DATA_LIST_REPORT);
        // 构造异步导出参数
        SupervisionTaskDataListExportRequest msg = new SupervisionTaskDataListExportRequest();
        msg.setEnterpriseId(enterpriseId);
        msg.setParentId(parentId);
        msg.setCompleteStatusList(completeStatusList);
        msg.setUserName(userName);
        msg.setHandleOverTimeStatus(handleOverTimeStatus);
        msg.setUser(user);
        msg.setTotalNum(count);
        msg.setImportTaskDO(importTaskDO);
        msg.setDbName(user.getDbName());
        MsgUniteData msgUniteData = new MsgUniteData();
        msgUniteData.setData(JSONObject.toJSONString(msg));
        msgUniteData.setMsgType(MsgUniteDataTypeEnum.SUPERVISION_DATA_EXPORT.getCode());
        //分开异步导出
        simpleMessageService.send(JSONObject.toJSONString(msgUniteData), RocketMqTagEnum.EXPORT_IMPORT_MESSAGE);
        return importTaskDO;
    }
    @Override
    public ImportTaskDO listSupervisionStoreTaskByParentIdExport(String enterpriseId, Long parentId, List<String> storeIds,String userName,  List<SupervisionSubTaskStatusEnum> completeStatusList,
                                                                 CurrentUser user,Long taskId,List<String> regionIds,Integer handleOverTimeStatus) {
        // 查询导出数量，限流
        Long count = supervisionStoreTaskDao.countByParentId(enterpriseId,parentId,taskId,storeIds,regionIds,completeStatusList,userName,handleOverTimeStatus);
        if (count == null || count == 0L) {
            throw new ServiceException("当前无记录可导出");
        }
        if (count > Constants.MAX_EXPORT_SIZE) {
            throw new ServiceException("导出数据不能超过" + Constants.MAX_EXPORT_SIZE + "条，请缩小导出范围");
        }
        // 通过枚举获取文件名称 ExportServiceEnum
        String fileName = ExportTemplateEnum.getByCode(ImportTaskConstant.SUPERVISION_DATA_STORE_LIST_REPORT);
        // 保存导出任务
        ImportTaskDO importTaskDO = importTaskService.insertExportTask(enterpriseId, fileName, ImportTaskConstant.SUPERVISION_DATA_STORE_LIST_REPORT);
        // 构造异步导出参数
        SupervisionTaskDataListExportRequest msg = new SupervisionTaskDataListExportRequest();
        msg.setEnterpriseId(enterpriseId);
        msg.setParentId(parentId);
        msg.setCompleteStatusList(completeStatusList);
        msg.setStoreIds(storeIds);
        msg.setUser(user);
        msg.setUserName(userName);
        msg.setTotalNum(count);
        msg.setTaskId(taskId);
        msg.setRegionId(regionIds);
        msg.setHandleOverTimeStatus(handleOverTimeStatus);
        msg.setImportTaskDO(importTaskDO);
        msg.setDbName(user.getDbName());
        MsgUniteData msgUniteData = new MsgUniteData();
        msgUniteData.setData(JSONObject.toJSONString(msg));
        msgUniteData.setMsgType(MsgUniteDataTypeEnum.SUPERVISION_DATA_STORE_EXPORT.getCode());
        //分开异步导出
        simpleMessageService.send(JSONObject.toJSONString(msgUniteData), RocketMqTagEnum.EXPORT_IMPORT_MESSAGE);
        return importTaskDO;
    }


    @Override
    public SupervisionTaskParentDetailVO selectDetailById(String enterpriseId, Long id,CurrentUser currentUser) {
        SupervisionTaskParentDO supervisionTaskParentDO = supervisionTaskParentDao.selectByPrimaryKey(id, enterpriseId);
        if (supervisionTaskParentDO==null){
            throw new ServiceException(ErrorCodeEnum.SUPERVISION_TASK_NOT_EXIST);
        }
        SupervisionTaskParentDetailVO supervisionTaskParentDetailVO = new SupervisionTaskParentDetailVO();
        supervisionTaskParentDetailVO.setId(supervisionTaskParentDO.getId());
        supervisionTaskParentDetailVO.setBusinessType(supervisionTaskParentDO.getBusinessType());
        supervisionTaskParentDetailVO.setTaskEndTime(supervisionTaskParentDO.getTaskEndTime());
        supervisionTaskParentDetailVO.setTaskName(supervisionTaskParentDO.getTaskName());
        supervisionTaskParentDetailVO.setBusinessId(supervisionTaskParentDO.getBusinessId());
        supervisionTaskParentDetailVO.setCheckCode(supervisionTaskParentDO.getCheckCode());
        supervisionTaskParentDetailVO.setCancelStatus(supervisionTaskParentDO.getCancelStatus());
        supervisionTaskParentDetailVO.setCreateTime(supervisionTaskParentDO.getCreateTime());
        supervisionTaskParentDetailVO.setTaskStartTime(supervisionTaskParentDO.getTaskStartTime());
        supervisionTaskParentDetailVO.setDesc(supervisionTaskParentDO.getDescription());
        supervisionTaskParentDetailVO.setRemark(supervisionTaskParentDO.getRemark());
        supervisionTaskParentDetailVO.setCheckStoreIds(supervisionTaskParentDO.getCheckStoreIds());
        supervisionTaskParentDetailVO.setExecutePersons(supervisionTaskParentDO.getExecutePersons());
        supervisionTaskParentDetailVO.setHandleWay(supervisionTaskParentDO.getHandleWay());
        supervisionTaskParentDetailVO.setPriority(supervisionTaskParentDO.getPriority());
        supervisionTaskParentDetailVO.setTags(supervisionTaskParentDO.getTags());
        supervisionTaskParentDetailVO.setFormId(supervisionTaskParentDO.getFormId());
        supervisionTaskParentDetailVO.setTaskGrouping(supervisionTaskParentDO.getTaskGrouping());
        String sopIdsStr = supervisionTaskParentDO.getSopIds();
        //加载sop文档
        if (StringUtils.isNotEmpty(sopIdsStr)){
            List<Long> sopIds = JSONObject.parseArray(sopIdsStr, Long.class);
            List<TaskSopVO> taskSopVOS = taskSopService.listByIdList(enterpriseId, sopIds);
            supervisionTaskParentDetailVO.setTaskSopVOList(taskSopVOS);
        }
        if (StringUtils.isNotEmpty(supervisionTaskParentDO.getFormId())){
            TbMetaTableDO tbMetaTableDO = tbMetaTableMapper.selectById(enterpriseId, Long.valueOf(supervisionTaskParentDO.getFormId()));
            supervisionTaskParentDetailVO.setTbMetaTableDO(tbMetaTableDO);
        }
        if (StringUtils.isNotEmpty(supervisionTaskParentDO.getTimingInfo())){
            supervisionTaskParentDetailVO.setTimingInfoDTO(JSONObject.parseObject(supervisionTaskParentDO.getTimingInfo(),TimingInfoDTO.class));
        }
        if (StringUtils.isNotEmpty(supervisionTaskParentDO.getProcessInfo())){
            supervisionTaskParentDetailVO.setApproveInfoDTO(JSONObject.parseObject(supervisionTaskParentDO.getProcessInfo(),ApproveInfoDTO.class));
        }


        List<String> storeIdList  = new ArrayList<>();
        if (StringUtils.isNotEmpty(supervisionTaskParentDO.getCheckStoreIds())){
            String checkStoreIds = supervisionTaskParentDO.getCheckStoreIds();
            if (StringUtils.isEmpty(checkStoreIds)){
                supervisionTaskParentDetailVO.setStoreRangeList(Collections.emptyList());
            }else {
                //兼容老数据 新数据JSON  老数据是String'
                if (checkStoreIds.contains("{")){
                    List<GeneralDTO> gen = JSONObject.parseArray(checkStoreIds, GeneralDTO.class);
                    supervisionTaskParentDetailVO.setStoreRangeList(gen);
                }else {
                    storeIdList = JSONObject.parseArray(checkStoreIds,String.class);
                    List<StoreDTO> storeListByStoreIds = storeMapper.getStoreListByStoreIds(enterpriseId, storeIdList);
                    List<GeneralDTO> result = new ArrayList<>();
                    if (CollectionUtils.isNotEmpty(storeListByStoreIds)){
                        for (StoreDTO storeDTO:storeListByStoreIds) {
                            GeneralDTO generalDTO = new GeneralDTO();
                            generalDTO.setName(storeDTO.getStoreName());
                            generalDTO.setValue(storeDTO.getStoreId());
                            generalDTO.setType("store");
                            result.add(generalDTO);
                        }
                        supervisionTaskParentDetailVO.setStoreRangeList(result);
                    }
                }
            }
        }
        return supervisionTaskParentDetailVO;
    }


    private List<SupervisionStoreTaskDataVO> convertSupervisionStoreTaskToVO(String enterpriseId,List<SupervisionStoreTaskDO> list,List<TbMetaDefTableColumnDO> tbMetaDefTableColumnDOS,
                                                   List<SupervisionDefDataColumnDTO> dataColumnListByTaskIdAndType){

        List<SupervisionStoreTaskDataVO> result = new ArrayList<>();
        if (CollectionUtils.isEmpty(list)){
            return result;
        }
        Map<Long, List<SupervisionDefDataColumnDTO>> listMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(dataColumnListByTaskIdAndType)){
            listMap = dataColumnListByTaskIdAndType.stream().collect(Collectors.groupingBy(SupervisionDefDataColumnDTO::getTaskId));
        }
        Set<String> userIds = list.stream().map(SupervisionStoreTaskDO::getSupervisionHandleUserId).collect(Collectors.toSet());
        Set<String> supervisionUserIdList = list.stream().map(SupervisionStoreTaskDO::getSupervisionUserId).collect(Collectors.toSet());
        userIds.addAll(supervisionUserIdList);
        List<EnterpriseUserDTO> userDTOList = enterpriseUserService.getUserByUserIds(enterpriseId, new ArrayList<>(userIds));
        Map<String, EnterpriseUserDTO> userMap = userDTOList.stream().collect(Collectors.toMap(EnterpriseUserDTO::getUserId, Function.identity()));
        List<String> storeIds = list.stream().map(SupervisionStoreTaskDO::getStoreId).collect(Collectors.toList());
        Map<String, String> storeNumMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(storeIds)){
            List<StoreDTO> storeListByStoreIds = storeMapper.getStoreListByStoreIds(enterpriseId, storeIds);
            storeNumMap = storeListByStoreIds.stream().filter(x->StringUtils.isNotEmpty(x.getStoreNum())).collect(Collectors.toMap(StoreDTO::getStoreId, StoreDTO::getStoreNum));
        }

        for (SupervisionStoreTaskDO supervisionStoreTaskDO:list) {
            SupervisionStoreTaskDataVO supervisionStoreTaskDataVO = new SupervisionStoreTaskDataVO();
            supervisionStoreTaskDataVO.setSupervisionUserId(supervisionStoreTaskDO.getSupervisionUserId());
            supervisionStoreTaskDataVO.setSupervisionUserName(userMap.getOrDefault(supervisionStoreTaskDO.getSupervisionUserId(),new EnterpriseUserDTO()).getName());
            supervisionStoreTaskDataVO.setStoreId(supervisionStoreTaskDO.getStoreId());
            supervisionStoreTaskDataVO.setTaskName(supervisionStoreTaskDO.getTaskName());
            supervisionStoreTaskDataVO.setStoreNum(storeNumMap.get(supervisionStoreTaskDO.getStoreId()));
            supervisionStoreTaskDataVO.setStoreName(supervisionStoreTaskDO.getStoreName());
            supervisionStoreTaskDataVO.setRoleName(userMap.getOrDefault(supervisionStoreTaskDO.getSupervisionHandleUserId(),new EnterpriseUserDTO()).getRoleName());
            supervisionStoreTaskDataVO.setDepartment(userMap.getOrDefault(supervisionStoreTaskDO.getSupervisionHandleUserId(),new EnterpriseUserDTO()).getDepartment());
            supervisionStoreTaskDataVO.setCompleteStatus(getSubStatus(supervisionStoreTaskDO.getTaskEndTime(),supervisionStoreTaskDO.getCancelStatus(),supervisionStoreTaskDO.getTaskState()));
            supervisionStoreTaskDataVO.setTaskStatusStr(SupervisionSubTaskStatusEnum.getByCode(supervisionStoreTaskDataVO.getCompleteStatus()).getDesc());
            supervisionStoreTaskDataVO.setCompleteTime(supervisionStoreTaskDO.getCompleteTime());
            supervisionStoreTaskDataVO.setSupervisionTaskId(supervisionStoreTaskDO.getSupervisionTaskId());
            supervisionStoreTaskDataVO.setCancelStatus(supervisionStoreTaskDO.getCancelStatus());
            supervisionStoreTaskDataVO.setTbMetaDefTableColumnDOS(tbMetaDefTableColumnDOS);
            supervisionStoreTaskDataVO.setId(supervisionStoreTaskDO.getId());
            supervisionStoreTaskDataVO.setSupervisionDefDataColumnDTOS(listMap.get(supervisionStoreTaskDO.getId()));
            supervisionStoreTaskDataVO.setSubmitTime(supervisionStoreTaskDO.getSubmitTime());
            supervisionStoreTaskDataVO.setHandleOverTimeStatus(getTaskHandleOverTimeStatus(supervisionStoreTaskDO.getHandleOverTimeStatus(),supervisionStoreTaskDO.getTaskEndTime().getTime(),supervisionStoreTaskDO.getTaskState()));
            supervisionStoreTaskDataVO.setHandleOverTimeStatusStr(supervisionStoreTaskDO.getHandleOverTimeStatus()==0?"未逾期":"已逾期");
            supervisionStoreTaskDataVO.setSupervisionUserRoleName(userMap.getOrDefault(supervisionStoreTaskDO.getSupervisionUserId(),new EnterpriseUserDTO()).getRoleName());
            supervisionStoreTaskDataVO.setSupervisionUserDepartment(userMap.getOrDefault(supervisionStoreTaskDO.getSupervisionUserId(),new EnterpriseUserDTO()).getDepartment());
            Integer transferReassignFlag = supervisionStoreTaskDO.getTransferReassignFlag();
            String  transferReassignStr= "";
            if (transferReassignFlag==1){
                transferReassignStr="（已转交）";
            }else if (transferReassignFlag==2){
                transferReassignStr="（已重新分配）";
            }
            supervisionStoreTaskDataVO.setTempName(supervisionStoreTaskDataVO.getSupervisionUserName()+transferReassignStr);

            supervisionStoreTaskDataVO.setTransferReassignFlag(supervisionStoreTaskDO.getTransferReassignFlag());
            supervisionStoreTaskDataVO.setSupervisionHandleUserId(supervisionStoreTaskDO.getSupervisionHandleUserId());
            supervisionStoreTaskDataVO.setSupervisionHandleUserName(supervisionStoreTaskDO.getSupervisionHandleUserName());
            supervisionStoreTaskDataVO.setCurrentNode(supervisionStoreTaskDO.getCurrentNode());
            result.add(supervisionStoreTaskDataVO);
        }
        return  result;
    }
    /**
     * 转数据
     * @param enterpriseId
     * @param list
     * @return
     */
    private List<SupervisionTaskDataVO> convertSupervisionTaskToVO(String enterpriseId,List<SupervisionTaskDO> list,List<TbMetaDefTableColumnDO> tbMetaDefTableColumnDOS,
                                                                   List<SupervisionDefDataColumnDTO> dataColumnListByTaskIdAndType ){
        List<SupervisionTaskDataVO> result = new ArrayList<>();
        if (CollectionUtils.isEmpty(list)){
            return result;
        }
        Map<Long, List<SupervisionDefDataColumnDTO>> listMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(dataColumnListByTaskIdAndType)){
            listMap = dataColumnListByTaskIdAndType.stream().collect(Collectors.groupingBy(SupervisionDefDataColumnDTO::getTaskId));
        }
        Set<String> userIds = list.stream().map(SupervisionTaskDO::getSupervisionHandleUserId).collect(Collectors.toSet());
        Set<String> supervisionUserIdList = list.stream().map(SupervisionTaskDO::getSupervisionUserId).collect(Collectors.toSet());
        userIds.addAll(supervisionUserIdList);
        List<EnterpriseUserDTO> userDTOList = enterpriseUserService.getUserByUserIds(enterpriseId, new ArrayList<>(userIds));
        Map<String, EnterpriseUserDTO> userMap = userDTOList.stream().collect(Collectors.toMap(EnterpriseUserDTO::getUserId, Function.identity()));
        for (SupervisionTaskDO supervisionTaskDO:list) {
            SupervisionTaskDataVO supervisionTaskDataVO = new SupervisionTaskDataVO();
            supervisionTaskDataVO.setSupervisionUserId(supervisionTaskDO.getSupervisionUserId());
            supervisionTaskDataVO.setSupervisionTaskId(supervisionTaskDO.getId());
            supervisionTaskDataVO.setSupervisionUserName(userMap.getOrDefault(supervisionTaskDO.getSupervisionUserId(),new EnterpriseUserDTO()).getName());
            supervisionTaskDataVO.setRoleName(userMap.getOrDefault(supervisionTaskDO.getSupervisionHandleUserId(),new EnterpriseUserDTO()).getRoleName());
            supervisionTaskDataVO.setDepartment(userMap.getOrDefault(supervisionTaskDO.getSupervisionHandleUserId(),new EnterpriseUserDTO()).getDepartment());

            supervisionTaskDataVO.setSupervisionUserRoleName(userMap.getOrDefault(supervisionTaskDO.getSupervisionUserId(),new EnterpriseUserDTO()).getRoleName());
            supervisionTaskDataVO.setSupervisionUserDepartment(userMap.getOrDefault(supervisionTaskDO.getSupervisionUserId(),new EnterpriseUserDTO()).getDepartment());

            supervisionTaskDataVO.setCompleteStatus(getSubStatus(supervisionTaskDO.getTaskEndTime(),supervisionTaskDO.getCancelStatus(),supervisionTaskDO.getTaskState()));
            supervisionTaskDataVO.setTaskStatusStr(SupervisionSubTaskStatusEnum.getByCode(supervisionTaskDataVO.getCompleteStatus()).getDesc());
            supervisionTaskDataVO.setCompleteTime(supervisionTaskDO.getCompleteTime());
            supervisionTaskDataVO.setSubmitTime(supervisionTaskDO.getSubmitTime());
            supervisionTaskDataVO.setManualText(supervisionTaskDO.getManualText());
            supervisionTaskDataVO.setTaskName(supervisionTaskDO.getTaskName());
            supervisionTaskDataVO.setManualAttach(supervisionTaskDO.getManualAttach());
            supervisionTaskDataVO.setManualPics(supervisionTaskDO.getManualPics());
            supervisionTaskDataVO.setCancelStatus(supervisionTaskDO.getCancelStatus());
            supervisionTaskDataVO.setTbMetaDefTableColumnDOS(tbMetaDefTableColumnDOS);
            supervisionTaskDataVO.setSupervisionDefDataColumnDTOS(listMap.get(supervisionTaskDO.getId()));
            Integer transferReassignFlag = supervisionTaskDO.getTransferReassignFlag();
            String  transferReassignStr= "";
            if (transferReassignFlag==1){
                transferReassignStr="（已转交）";
            }else if (transferReassignFlag==2){
                transferReassignStr="（已重新分配）";
            }
            supervisionTaskDataVO.setTempName(supervisionTaskDataVO.getSupervisionUserName()+transferReassignStr);

            supervisionTaskDataVO.setTransferReassignFlag(supervisionTaskDO.getTransferReassignFlag());
            supervisionTaskDataVO.setHandleOverTimeStatus(getTaskHandleOverTimeStatus(supervisionTaskDO.getHandleOverTimeStatus(),supervisionTaskDO.getTaskEndTime().getTime(),supervisionTaskDO.getTaskState()));
            supervisionTaskDataVO.setHandleOverTimeStatusStr(supervisionTaskDataVO.getHandleOverTimeStatus()==0?"未逾期":"已逾期");
            supervisionTaskDataVO.setSupervisionHandleUserId(supervisionTaskDO.getSupervisionHandleUserId());
            supervisionTaskDataVO.setSupervisionHandleUserName(supervisionTaskDO.getSupervisionHandleUserName());
            supervisionTaskDataVO.setCurrentNode(supervisionTaskDO.getCurrentNode());


            if (StringUtils.isNotEmpty(supervisionTaskDO.getCheckObjectIds())){
                List<String> stringList = Arrays.asList(supervisionTaskDO.getCheckObjectIds().split(","));
                List<String> subList = stringList.size() > 1 ? stringList.subList(0, 2) : stringList.subList(0, 1);
                List<StoreDTO> storeListByStoreIds = storeMapper.getStoreListByStoreIdsInCludingDeleted(enterpriseId,subList);
                supervisionTaskDataVO.setStoreDTOList(storeListByStoreIds);
                supervisionTaskDataVO.setStoreNameList(storeListByStoreIds.stream().map(StoreDTO::getStoreName).collect(Collectors.joining(Constants.COMMA)));
            }
            result.add(supervisionTaskDataVO);
        }
        return  result;
    }

    private List<SupervisionTaskParentVO> convertListVO(String enterpriseId,List<SupervisionTaskParentDO> list){
        List<String> userIds = list.stream().map(SupervisionTaskParentDO::getCreateUserId).collect(Collectors.toList());
        List<EnterpriseUserDO> enterpriseUserDOS = enterpriseUserDao.selectByUserIds(enterpriseId, userIds);
        Map<String, String> userNameMap = enterpriseUserDOS.stream().collect(Collectors.toMap(EnterpriseUserDO::getUserId, EnterpriseUserDO::getName));
        List<SupervisionTaskParentVO> result = new ArrayList<>();
        for (SupervisionTaskParentDO supervisionTaskParentDO:list) {
            SupervisionTaskParentVO supervisionTaskParentVO = new SupervisionTaskParentVO();
            supervisionTaskParentVO.setExecutePersons(supervisionTaskParentDO.getExecutePersons());
            supervisionTaskParentVO.setTaskName(supervisionTaskParentDO.getTaskName());
            supervisionTaskParentVO.setId(supervisionTaskParentDO.getId());
            supervisionTaskParentVO.setCancelStatus(supervisionTaskParentDO.getCancelStatus());
            supervisionTaskParentVO.setBusinessType(supervisionTaskParentDO.getBusinessType());
            supervisionTaskParentVO.setTaskEndTime(supervisionTaskParentDO.getTaskEndTime());
            supervisionTaskParentVO.setTaskStartTime(supervisionTaskParentDO.getTaskStartTime());
            supervisionTaskParentVO.setCreateTime(supervisionTaskParentDO.getCreateTime());
            supervisionTaskParentVO.setTags(supervisionTaskParentDO.getTags());
            supervisionTaskParentVO.setTaskStatusStr(getParentStatusStr(supervisionTaskParentDO.getTaskStartTime(),supervisionTaskParentDO.getTaskEndTime(),supervisionTaskParentDO.getCancelStatus(),supervisionTaskParentDO.getFailureState()));
            supervisionTaskParentVO.setCreateUserId(supervisionTaskParentDO.getCreateUserId());
            supervisionTaskParentVO.setCreateUserName(userNameMap.getOrDefault(supervisionTaskParentDO.getCreateUserId(),""));
            supervisionTaskParentVO.setPriority(supervisionTaskParentDO.getPriority());
            supervisionTaskParentVO.setRemark(supervisionTaskParentDO.getRemark());
            supervisionTaskParentVO.setCheckStoreIds(supervisionTaskParentDO.getCheckStoreIds());
            supervisionTaskParentVO.setTaskGrouping(supervisionTaskParentDO.getTaskGrouping());
            result.add(supervisionTaskParentVO);
        }
        return result;
    }

    @Override
    public Boolean SupervisionHistoryCorrect(String enterpriseId) {
        //查询完成的数据 写入历史记录
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfig = enterpriseConfigService.selectByEnterpriseId(enterpriseId);
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());
        //按人任务订正
        personHistoryCorrect(enterpriseId);
        //按门店任务订正
        storeHistoryCorrect(enterpriseId);
        return Boolean.TRUE;
    }

    /**
     * 获取父任务状态中文
     * @param taskStartTime
     * @param taskEndTime
     * @param cancelStatus
     * @return
     */
    public static String getParentStatusStr(Date taskStartTime,Date taskEndTime,Integer cancelStatus,Integer failState){
        Long now = System.currentTimeMillis();
        if (failState==1){
            return SupervisionParentStatusEnum.FAILURE.getDesc();
        }
        if (cancelStatus==1){
            return SupervisionParentStatusEnum.CANCEL.getDesc();
        }
        if (now<taskStartTime.getTime()){
            return SupervisionParentStatusEnum.NOT_STARTED.getDesc();
        }
        if (now>taskStartTime.getTime()&&now<taskEndTime.getTime()){
            return SupervisionParentStatusEnum.ONGOING.getDesc();
        }
        if (now>taskEndTime.getTime()){
            return SupervisionParentStatusEnum.OVER.getDesc();
        }
        return "";
    }


    /**
     * 查询子任务状态
     * @param cancelStatus
     * @param taskState
     * @return
     */
    public static Integer getSubStatus(Date taskStartTime,Integer cancelStatus,Integer taskState){
        if (cancelStatus==1){
            //已取消
            return SupervisionSubTaskStatusEnum.CANCEL.getStatus();
        }
        if (taskState==0){
            return SupervisionSubTaskStatusEnum.TODO.getStatus();
        }
        if (taskState==4){
            return SupervisionSubTaskStatusEnum.APPROVAL.getStatus();
        }
        if (taskState==1){
            return SupervisionSubTaskStatusEnum.COMPLETE.getStatus();
        }
        return 0;
    }


    /**
     * 计算逾期状态
     * @return
     */
    @Override
    public Integer getTaskHandleOverTimeStatus(Integer handleOverTimeStatus,Long taskEndTime,Integer taskState){
        if (handleOverTimeStatus!=0){
            return handleOverTimeStatus;
        }else {
            if (taskState==0&&taskEndTime<System.currentTimeMillis()){
                return 1;
            }
        }
        return 0;
    }

    /**
     * 获取门店列表
     * @param enterpriseId
     * @param storeGeneralList
     * @param userId
     * @return
     */
    private Set<String> getStoreIdList(String enterpriseId, List<GeneralDTO> storeGeneralList, String userId) {
        Set<String> storeSet = Sets.newHashSet();
        //有效set
        Set<String> storeEffitiveSet = Sets.newHashSet();
        List<String> regionList = Lists.newArrayList();
        List<String> groupList = Lists.newArrayList();
        List<String> filterGroupIdList = Lists.newArrayList();
        List<String> filterDeptIdList = Lists.newArrayList();
        for (GeneralDTO item : storeGeneralList) {
            switch (item.getType()) {
                case UnifyTaskConstant.StoreType.STORE:
                    String value = item.getValue();
                    if (value == null) {
                        log.info("item.value=null", JSON.toJSONString(storeGeneralList));
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
                case UnifyTaskConstant.StoreType.GROUP_REGION:
                    filterGroupIdList.add(item.getValue());
                    filterDeptIdList.add(item.getFilterRegionId());
                    break;
                default:
                    throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(),
                            "操作类型有误：operate=" + item.getType());
            }
        }
        //FIXME 存在空
        if (!storeEffitiveSet.isEmpty()) {
            List<String> effticeStoreIdList = storeMapper.getEffectiveStoreByIdList(enterpriseId, new ArrayList<>(storeEffitiveSet));
            if (CollectionUtils.isNotEmpty(effticeStoreIdList)) {
                storeSet.addAll(new HashSet<>(effticeStoreIdList));
            }
        }


        //区域
        //fixme 区域下门店过多会出现问题
        if (CollectionUtils.isNotEmpty(regionList)) {
            List<String> regionPathList = new ArrayList<>();
            for (String regionId : regionList) {
                regionPathList.add(regionService.getRegionPath(enterpriseId, regionId));
            }

            List<StoreAreaDTO> areaDTOList = storeMapper.listStoreByRegionPathList(enterpriseId, regionPathList);
            if (CollectionUtils.isNotEmpty(areaDTOList)) {
                Set<String> areaStoreSet = areaDTOList.stream().map(StoreAreaDTO::getStoreId).collect(Collectors.toSet());
                AuthVisualDTO authStore = authVisualService.authRegionStoreByStore(enterpriseId, userId, new ArrayList<>(areaStoreSet));
                log.info("##unify task regionList authStore={}", JSON.toJSONString(authStore));
                if (Objects.nonNull(authStore) && CollectionUtils.isNotEmpty(authStore.getStoreIdList())) {
                    storeSet.addAll(new HashSet<>(authStore.getStoreIdList()));
                }
            }
        }
        //分组
        if (CollectionUtils.isNotEmpty(groupList)) {
            List<StoreGroupMappingDO> groupStoreList = storeGroupMappingMapper.getStoreGroupMappingByGroupIDs(enterpriseId, groupList);
            if (CollectionUtils.isNotEmpty(groupStoreList)) {
                Set<String> groupStoreSet = groupStoreList.stream().map(StoreGroupMappingDO::getStoreId).collect(Collectors.toSet());
                AuthVisualDTO authStore = authVisualService.authRegionStoreByStore(enterpriseId, userId, new ArrayList<>(groupStoreSet));
                log.info("##unify task groupList authStore={}", JSON.toJSONString(authStore));
                if (Objects.nonNull(authStore) && CollectionUtils.isNotEmpty(authStore.getStoreIdList())) {
                    List<String> effticeGroupStoreIdList = storeMapper.getEffectiveStoreByIdList(enterpriseId, new ArrayList<>(authStore.getStoreIdList()));
                    if (CollectionUtils.isNotEmpty(effticeGroupStoreIdList)) {
                        storeSet.addAll(new HashSet<>(effticeGroupStoreIdList));
                    }
                }
            }
        }
        //分组部门交叉
        if(CollectionUtils.isNotEmpty(filterDeptIdList) && CollectionUtils.isNotEmpty(filterGroupIdList)){
            Set<String> filterDeptStoreList = new HashSet<>();
            //区域
            List<String> regionPathList = new ArrayList<>();
            for (String regionId : filterDeptIdList) {
                regionPathList.add(regionService.getRegionPath(enterpriseId, regionId));
            }

            List<StoreAreaDTO> areaDTOList = storeMapper.listStoreByRegionPathList(enterpriseId, regionPathList);
            if (CollectionUtils.isNotEmpty(areaDTOList)) {
                Set<String> areaStoreSet = areaDTOList.stream().map(StoreAreaDTO::getStoreId).collect(Collectors.toSet());
                AuthVisualDTO authStore = authVisualService.authRegionStoreByStore(enterpriseId, userId, new ArrayList<>(areaStoreSet));
                log.info("##unify task regionList authStore={}", JSON.toJSONString(authStore));
                if (Objects.nonNull(authStore) && CollectionUtils.isNotEmpty(authStore.getStoreIdList())) {
                    filterDeptStoreList.addAll(new HashSet<>(authStore.getStoreIdList()));
                }
            }
            //分组
            Set<String> filterGroupStoreList = new HashSet<>();
            List<StoreGroupMappingDO> groupStoreList = storeGroupMappingMapper.getStoreGroupMappingByGroupIDs(enterpriseId, filterGroupIdList);
            if (CollectionUtils.isNotEmpty(groupStoreList)) {
                Set<String> groupStoreSet = groupStoreList.stream().map(StoreGroupMappingDO::getStoreId).collect(Collectors.toSet());
                AuthVisualDTO authStore = authVisualService.authRegionStoreByStore(enterpriseId, userId, new ArrayList<>(groupStoreSet));
                log.info("##unify task groupList authStore={}", JSON.toJSONString(authStore));
                if (Objects.nonNull(authStore) && CollectionUtils.isNotEmpty(authStore.getStoreIdList())) {
                    List<String> effticeGroupStoreIdList = storeMapper.getEffectiveStoreByIdList(enterpriseId, new ArrayList<>(authStore.getStoreIdList()));
                    if (CollectionUtils.isNotEmpty(effticeGroupStoreIdList)) {
                        filterGroupStoreList.addAll(new HashSet<>(effticeGroupStoreIdList));
                    }
                }
            }
            if(CollectionUtils.isNotEmpty(filterGroupStoreList) && CollectionUtils.isNotEmpty(filterDeptStoreList)){
                filterDeptStoreList.retainAll(filterGroupStoreList);
                if(CollectionUtils.isNotEmpty(filterDeptStoreList)){
                    storeSet.addAll(filterDeptStoreList);
                }
            }
        }

        return storeSet;
    }



}
