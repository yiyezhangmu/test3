package com.coolcollege.intelligent.service.store.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.http.HttpRequest;
import com.coolcollege.intelligent.common.util.DateUtils;
import com.coolcollege.intelligent.dao.authentication.UserAuthMappingMapper;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseUserMapper;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseUserMappingMapper;
import com.coolcollege.intelligent.dao.enterprise.dao.EnterpriseUserDao;
import com.coolcollege.intelligent.dao.enterprise.dao.EnterpriseUserRoleDao;
import com.coolcollege.intelligent.dao.region.RegionMapper;
import com.coolcollege.intelligent.dao.store.StoreMapper;
import com.coolcollege.intelligent.dao.store.dao.StoreOpenRuleDAO;
import com.coolcollege.intelligent.dao.unifytask.TaskParentMapper;
import com.coolcollege.intelligent.model.enterprise.EnterpriseUserDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseUserMappingDO;
import com.coolcollege.intelligent.model.enterprise.dto.PersonDTO;
import com.coolcollege.intelligent.model.enums.*;
import com.coolcollege.intelligent.model.metatable.TbMetaTableDO;
import com.coolcollege.intelligent.model.region.RegionDO;
import com.coolcollege.intelligent.model.scheduler.request.ScheduleCallBackRequest;
import com.coolcollege.intelligent.model.scheduler.request.ScheduleFixedRequest;
import com.coolcollege.intelligent.model.store.StoreOpenRuleDO;
import com.coolcollege.intelligent.model.store.dto.CountStoreRuleDTO;
import com.coolcollege.intelligent.model.store.dto.StoreOpenRuleBuildDTO;
import com.coolcollege.intelligent.model.store.dto.StoreOpenRuleDTO;
import com.coolcollege.intelligent.model.store.dto.UpdateCreateUserDTO;
import com.coolcollege.intelligent.model.unifytask.TaskParentDO;
import com.coolcollege.intelligent.model.unifytask.dto.GeneralDTO;
import com.coolcollege.intelligent.model.unifytask.dto.TaskProcessDTO;
import com.coolcollege.intelligent.model.unifytask.dto.UnifyTaskBuildDTO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.model.userholder.UserHolder;
import com.coolcollege.intelligent.service.schedule.ScheduleService;
import com.coolcollege.intelligent.service.store.StoreOpenRuleService;
import com.coolcollege.intelligent.service.tbdisplay.TbMetaDisplayQuickColumnService;
import com.coolcollege.intelligent.util.ScheduleCallBackUtil;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author byd
 * @date 2023-05-12 14:17
 */
@Service
@Slf4j
public class StoreOpenRuleServiceImpl implements StoreOpenRuleService {

    @Resource
    private StoreOpenRuleDAO storeOpenRuleDAO;

    @Resource
    private ScheduleService scheduleService;

    @Value("${scheduler.api.url}")
    private String schedulerApiUrl;

    @Value("${scheduler.callback.task.url}")
    private String schedulerCallbackTaskUrl;

    @Resource
    private EnterpriseUserMapper enterpriseUserMapper;
    @Resource
    private EnterpriseUserDao enterpriseUserDao;

    @Resource
    private StoreMapper storeMapper;

    @Resource
    private TbMetaDisplayQuickColumnService tbMetaDisplayQuickColumnService;

    @Resource
    private RegionMapper regionMapper;

    @Resource
    private TaskParentMapper taskParentMapper;

    @Resource
    private UserAuthMappingMapper userAuthMappingMapper;

    @Resource
    private EnterpriseUserRoleDao enterpriseUserRoleDao;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public StoreOpenRuleBuildDTO addStoreOpenRule(String enterpriseId, CurrentUser currentUser, StoreOpenRuleBuildDTO storeOpenRuleBuildDTO) {
        StoreOpenRuleDO storeOpenRuleDO = transBuild(enterpriseId, storeOpenRuleBuildDTO, currentUser);
        storeOpenRuleDAO.insertSelective(enterpriseId, storeOpenRuleDO);
        Boolean isSuc = setStoreOpenScheduler(enterpriseId, storeOpenRuleDO);
        if (!isSuc) {
            throw new ServiceException(ErrorCodeEnum.API_ERROR);
        }
        storeOpenRuleBuildDTO.setRuleId(storeOpenRuleDO.getId());
        return storeOpenRuleBuildDTO;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public StoreOpenRuleBuildDTO updateStoreOpenRule(String enterpriseId, CurrentUser currentUser, StoreOpenRuleBuildDTO storeOpenRuleBuildDTO) {
        StoreOpenRuleDO storeOpenRuleDO = storeOpenRuleDAO.selectByPrimaryKey(enterpriseId, storeOpenRuleBuildDTO.getRuleId());
        if (storeOpenRuleDO == null) {
            throw new ServiceException(ErrorCodeEnum.TASK_NOT_EXIST);
        }
        StoreOpenRuleDO updateStoreOpenRuleDO = transBuild(enterpriseId, storeOpenRuleBuildDTO, currentUser);
        updateStoreOpenRuleDO.setId(storeOpenRuleBuildDTO.getRuleId());
        Boolean success = scheduleService.deleteSchedule(enterpriseId, storeOpenRuleDO.getScheduleId());
        if (!success) {
            log.error("定时调度器删除失败，enterpriseId={},scheduleId={}", enterpriseId, storeOpenRuleDO.getScheduleId());
            throw new ServiceException(ErrorCodeEnum.SCHEDULE_DELETE_ERROR);
        }
        Boolean isSuc = setStoreOpenScheduler(enterpriseId, updateStoreOpenRuleDO);
        if (!isSuc) {
            throw new ServiceException(ErrorCodeEnum.SCHEDULE_ADD_ERROR);
        }
        storeOpenRuleDAO.updateByPrimaryKeySelective(enterpriseId, updateStoreOpenRuleDO);
        return storeOpenRuleBuildDTO;
    }

    @Override
    public PageInfo<StoreOpenRuleDTO> list(String enterpriseId, String createUserid, int pageNum, int pageSize,String regionId,String newStoreTaskStatus,List<String> mappingIds,
                                           String ruleName) {
        List<StoreOpenRuleDTO> list = new ArrayList<>();
        PageHelper.startPage(pageNum, pageSize);
        List<StoreOpenRuleDO> storeOpenRuleDOList = storeOpenRuleDAO.list(enterpriseId, createUserid,regionId,newStoreTaskStatus,mappingIds, ruleName);
        PageInfo pageInfo = new PageInfo<>(storeOpenRuleDOList);
        if (CollectionUtils.isEmpty(storeOpenRuleDOList)) {
            return pageInfo;
        }
        Map<Long, List<String>> resultMap = convertToMap(enterpriseId,storeOpenRuleDOList);
        List<Long> storeOpenRuleIds = storeOpenRuleDOList.stream()
                .map(StoreOpenRuleDO::getId)
                .collect(Collectors.toList());
        Map<Long, List<TaskParentDO>> ruleIdMap = taskParentMapper.getByRuleIds(enterpriseId, storeOpenRuleIds)
                .stream()
                .collect(Collectors.groupingBy(TaskParentDO::getStoreOpenRuleId));

        storeOpenRuleDOList.forEach(storeOpenRuleDO -> {
            StoreOpenRuleDTO storeOpenRuleDTO = new StoreOpenRuleDTO();
            storeOpenRuleDTO.setId(storeOpenRuleDO.getId());
            storeOpenRuleDTO.setRuleName(storeOpenRuleDO.getRuleName());
            storeOpenRuleDTO.setOpenDateDay(storeOpenRuleDO.getOpenDateDay());
            storeOpenRuleDTO.setRuleType(storeOpenRuleDO.getRuleType());
            storeOpenRuleDTO.setStatus(storeOpenRuleDO.getStatus());
            storeOpenRuleDTO.setBeginTime(storeOpenRuleDO.getBeginTime());
            storeOpenRuleDTO.setEndTime(storeOpenRuleDO.getEndTime());
            storeOpenRuleDTO.setCreateTime(storeOpenRuleDO.getCreateTime());
            if (StringUtils.isNotBlank(storeOpenRuleDO.getRuleInfo())) {
                storeOpenRuleDTO.setForm(JSONObject.parseArray(storeOpenRuleDO.getRuleInfo(), GeneralDTO.class));
            }
            //已过期
            if (Constants.ZERO == storeOpenRuleDO.getStatus() && storeOpenRuleDO.getEndTime().before(new Date())) {
                storeOpenRuleDO.setStatus(Constants.INDEX_THREE);
            }
            storeOpenRuleDTO.setRegionName(CollectionUtils.isEmpty(resultMap.get(storeOpenRuleDO.getId())) ? null : resultMap.get(storeOpenRuleDO.getId()));
            if (CollectionUtils.isNotEmpty(ruleIdMap.get(storeOpenRuleDO.getId()))){
                List<String> unifyTaskIds = ruleIdMap.get(storeOpenRuleDO.getId()).stream().map(TaskParentDO::getId).map(String::valueOf).collect(Collectors.toList());
                storeOpenRuleDTO.setUnifyTaskId(unifyTaskIds);
            }
            storeOpenRuleDTO.setUpdateTime(storeOpenRuleDO.getUpdateTime());
            String updateUserName = enterpriseUserDao.selectNameByUserId(enterpriseId, storeOpenRuleDO.getUpdateUserId());
            if (StringUtils.isNotBlank(updateUserName)){
                storeOpenRuleDTO.setUpdateUserName(updateUserName);
            }
            String createUserName = enterpriseUserDao.selectNameByUserId(enterpriseId, storeOpenRuleDO.getCreateUserId());
            if (StringUtils.isNotBlank(createUserName)){
                storeOpenRuleDTO.setCreateUserName(createUserName);
            }
            list.add(storeOpenRuleDTO);
        });
        pageInfo.setList(list);
        return pageInfo;
    }

    private Map<Long, List<String>> convertToMap(String eid,List<StoreOpenRuleDO> storeOpenRuleDOList) {
        Map<Long, List<String>> resultMap = new HashMap<>();
        for (StoreOpenRuleDO obj : storeOpenRuleDOList) {
            Long id = obj.getId();
            String extraParam = obj.getExtraParam();
            // 去除首尾逗号，并拆分为数字列表
            List<String> extraParamList = new ArrayList<>();
            if (StringUtils.isBlank(extraParam)){
                continue;
            }
            extraParamList = Arrays
                    .asList(extraParam.replaceAll("^,|,$", "").split(","))
                    .stream()
                    .map(String::valueOf)
                    .collect(Collectors.toList());
            List<String> regionNameList = regionMapper.getRegionByRegionIds(eid, extraParamList).stream().map(RegionDO::getName).collect(Collectors.toList());
            // 将结果放入 Map
            resultMap.put(id, regionNameList);
        }
        return resultMap;
    }

    @Override
    public void enableStoreOpenRule(String enterpriseId, Long ruleId) {
        StoreOpenRuleDO storeOpenRuleDO = storeOpenRuleDAO.selectByPrimaryKey(enterpriseId, ruleId);
        if (storeOpenRuleDO == null) {
            throw new ServiceException(ErrorCodeEnum.TASK_NOT_EXIST);
        }
        if (Constants.INDEX_ZERO.equals(storeOpenRuleDO.getStatus())) {
            throw new ServiceException(ErrorCodeEnum.STORE_OPEN_TASK_ENABLE);
        }
        StoreOpenRuleDO updateDO = new StoreOpenRuleDO();
        updateDO.setId(storeOpenRuleDO.getId());
        updateDO.setStatus(Constants.INDEX_ZERO);
        storeOpenRuleDAO.updateByPrimaryKeySelective(enterpriseId, updateDO);

        Boolean isSuc = setStoreOpenScheduler(enterpriseId, storeOpenRuleDO);
        if (!isSuc) {
            throw new ServiceException(ErrorCodeEnum.SCHEDULE_ADD_ERROR);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void disableStoreOpenRule(String enterpriseId, Long ruleId) {
        StoreOpenRuleDO storeOpenRuleDO = storeOpenRuleDAO.selectByPrimaryKey(enterpriseId, ruleId);
        if (storeOpenRuleDO == null) {
            throw new ServiceException(ErrorCodeEnum.TASK_NOT_EXIST);
        }
        if (Constants.INDEX_ONE.equals(storeOpenRuleDO.getStatus())) {
            throw new ServiceException(ErrorCodeEnum.STORE_OPEN_TASK_NOT_ABLE);
        }
        StoreOpenRuleDO updateDO = new StoreOpenRuleDO();
        updateDO.setId(storeOpenRuleDO.getId());
        updateDO.setStatus(Constants.INDEX_ONE);
        updateDO.setScheduleId("");
        storeOpenRuleDAO.updateByPrimaryKeySelective(enterpriseId, updateDO);

        Boolean success = scheduleService.deleteSchedule(enterpriseId, storeOpenRuleDO.getScheduleId());
        if (!success) {
            log.error("定时调度器删除失败，enterpriseId={},scheduleId={}", enterpriseId, storeOpenRuleDO.getScheduleId());
            throw new ServiceException(ErrorCodeEnum.SCHEDULE_DELETE_ERROR);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void removeStoreOpenRule(String enterpriseId, Long ruleId) {
        StoreOpenRuleDO storeOpenRuleDO = storeOpenRuleDAO.selectByPrimaryKey(enterpriseId, ruleId);
        if (storeOpenRuleDO == null) {
            throw new ServiceException(ErrorCodeEnum.TASK_NOT_EXIST);
        }

        StoreOpenRuleDO updateDO = new StoreOpenRuleDO();
        updateDO.setId(storeOpenRuleDO.getId());
        updateDO.setDeleted(Boolean.TRUE);
        storeOpenRuleDAO.updateByPrimaryKeySelective(enterpriseId, updateDO);
        if (StringUtils.isNotBlank(storeOpenRuleDO.getScheduleId())) {
            Boolean success = scheduleService.deleteSchedule(enterpriseId, storeOpenRuleDO.getScheduleId());
            if (!success) {
                log.error("定时调度器删除失败，enterpriseId={},scheduleId={}", enterpriseId, storeOpenRuleDO.getScheduleId());
                throw new ServiceException(ErrorCodeEnum.SCHEDULE_DELETE_ERROR);
            }
        }
    }

    @Override
    public StoreOpenRuleBuildDTO detail(String enterpriseId, Long ruleId) {
        StoreOpenRuleDO storeOpenRuleDO = storeOpenRuleDAO.selectByPrimaryKey(enterpriseId, ruleId);
        StoreOpenRuleBuildDTO storeOpenRuleBuildDTO = new StoreOpenRuleBuildDTO();
        storeOpenRuleBuildDTO.setRuleId(ruleId);
        storeOpenRuleBuildDTO.setRuleName(storeOpenRuleDO.getRuleName());
        storeOpenRuleBuildDTO.setRuleType(storeOpenRuleDO.getRuleType());
        storeOpenRuleBuildDTO.setBeginTime(storeOpenRuleDO.getBeginTime().getTime());
        storeOpenRuleBuildDTO.setEndTime(storeOpenRuleDO.getEndTime().getTime());
        storeOpenRuleBuildDTO.setTaskName(storeOpenRuleDO.getTaskName());
        storeOpenRuleBuildDTO.setTaskDesc(storeOpenRuleDO.getTaskDesc());
        storeOpenRuleBuildDTO.setStatus(storeOpenRuleDO.getStatus());
        storeOpenRuleBuildDTO.setOpenDateDay(storeOpenRuleDO.getOpenDateDay());
        storeOpenRuleBuildDTO.setProcess(JSONObject.parseArray(storeOpenRuleDO.getNodeInfo(), TaskProcessDTO.class));
        storeOpenRuleBuildDTO.setForm(JSONObject.parseArray(storeOpenRuleDO.getRuleInfo(), GeneralDTO.class));
        storeOpenRuleBuildDTO.setRunDate(storeOpenRuleDO.getRunDate());
        storeOpenRuleBuildDTO.setCalendarTime(storeOpenRuleDO.getCalendarTime());
        storeOpenRuleBuildDTO.setTaskInfo(storeOpenRuleDO.getTaskInfo());
        storeOpenRuleBuildDTO.setLimitHour(storeOpenRuleDO.getLimitHour());
        storeOpenRuleBuildDTO.setAttachUrl(storeOpenRuleDO.getAttachUrl());
        if (StringUtils.isNotBlank(storeOpenRuleDO.getExtraParam())){
            storeOpenRuleBuildDTO.setExtraParam(storeOpenRuleDO.getExtraParam());
            String[] array = storeOpenRuleDO.getExtraParam().replaceAll("^,|,$", "").split(",");
            List<String> regionIds = Arrays.asList(array);
            storeOpenRuleBuildDTO.setRegionList(regionMapper.getRegionByRegionIds(enterpriseId,regionIds));
        }

        if (StringUtils.isNotBlank(storeOpenRuleDO.getCollaboratorId())) {
            List<PersonDTO> collaboratorUserList = new ArrayList<>();
            String[] collaboratorIdArr = storeOpenRuleDO.getCollaboratorId().split(Constants.COMMA);
            List<EnterpriseUserDO> userDOList = enterpriseUserMapper.selectUsersByUserIds(enterpriseId, Arrays.asList(collaboratorIdArr));
            Map<String, String> userMap = userDOList.stream().collect(Collectors.toMap(EnterpriseUserDO::getUserId, EnterpriseUserDO::getName));
            Arrays.asList(collaboratorIdArr).forEach(editUserId -> {
                PersonDTO personDTO = new PersonDTO();
                personDTO.setUserId(editUserId);
                personDTO.setUserName(userMap.get(editUserId));
                collaboratorUserList.add(personDTO);
            });
            storeOpenRuleBuildDTO.setCollaboratorIdList(Arrays.asList(collaboratorIdArr));
            storeOpenRuleBuildDTO.setCollaboratorUserList(collaboratorUserList);
        }
        return storeOpenRuleBuildDTO;
    }

    @Override
    public UnifyTaskBuildDTO buildStoreRuleTaskDTO(String enterpriseId, Long ruleId) {
        StoreOpenRuleDO storeOpenRuleDO = storeOpenRuleDAO.selectByPrimaryKey(enterpriseId, ruleId);
        if (storeOpenRuleDO == null) {
            log.info("buildStoreRuleTask#门店规则不存在,eid:{},ruleId:{}", enterpriseId, ruleId);
            return null;
        }
        if (storeOpenRuleDO.getDeleted()) {
            log.info("buildStoreRuleTask#门店规则已被删除,eid:{},ruleId:{}", enterpriseId, ruleId);
            return null;
        }
        if (Constants.INDEX_ONE.equals(storeOpenRuleDO.getStatus())) {
            log.info("buildStoreRuleTask#门店规则已被停用,eid:{},ruleId:{}", enterpriseId, ruleId);
            return null;
        }
        if (storeOpenRuleDO.getEndTime().before(new Date())) {
            log.info("buildStoreRuleTask#门店规则已过期,eid:{},ruleId:{}", enterpriseId, ruleId);
            return null;
        }

        //更改原先的自定义门店规则
//        List<String> storeIdList = storeMapper.getStoresByOpenDateDay(enterpriseId, storeOpenRuleDO.getOpenDateDay());
        List<String> storeIdList = new ArrayList<>();
        String extraParam = storeOpenRuleDO.getExtraParam();
        List<String> regionIdList = Arrays.asList(extraParam.split(","));
        List<RegionDO> regionByRegionIds = regionMapper.getRegionByRegionIds(enterpriseId, regionIdList);
        List<String> regionList = regionByRegionIds.stream()
                .filter(item -> Constants.REGION_TYPE_PATH.equals(item.getRegionType()))
                .map(RegionDO::getRegionId)
                .collect(Collectors.toList());
        List<RegionDO> HQRegionList = regionByRegionIds.stream()
                .filter(item -> Constants.REGION_TYPE_ROOT.equals(item.getRegionType()))
                .collect(Collectors.toList());
        Integer diffDays = storeOpenRuleDO.getOpenDateDay() > 0 ? storeOpenRuleDO.getOpenDateDay() - 1 : storeOpenRuleDO.getOpenDateDay();
        if (CollectionUtils.isNotEmpty(HQRegionList)){
            storeIdList = storeMapper.getStoresByOpenDateDay(enterpriseId, diffDays,null);
        }else if(CollectionUtils.isNotEmpty(regionList)){
            List<String> storesByParentIds = regionMapper.getStoresByParentIds(enterpriseId, regionList)
                    .stream()
                    .map(RegionDO::getStoreId)
                    .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(storesByParentIds)) {
                storeIdList = storeMapper.getStoresByOpenDateDay(enterpriseId, diffDays, storesByParentIds);
            }
        }

        if (CollectionUtils.isEmpty(storeIdList)) {
            log.info("buildStoreRuleTask#门店规则门店为空,eid:{},ruleId:{},openDateDay:{}", enterpriseId, ruleId, storeOpenRuleDO.getOpenDateDay());
            return null;
        }
        UnifyTaskBuildDTO unifyTaskBuildDTO = new UnifyTaskBuildDTO();
        unifyTaskBuildDTO.setBeginTime(System.currentTimeMillis());
        int limitInt = new Double(storeOpenRuleDO.getLimitHour() * 60).intValue();
        Long endTime = org.apache.commons.lang3.time.DateUtils.addMinutes(new Date(unifyTaskBuildDTO.getBeginTime()), limitInt).getTime();
        unifyTaskBuildDTO.setEndTime(endTime);
        unifyTaskBuildDTO.setTaskType(storeOpenRuleDO.getRuleType());
        unifyTaskBuildDTO.setTaskName(storeOpenRuleDO.getTaskName());
        unifyTaskBuildDTO.setTaskDesc(storeOpenRuleDO.getTaskDesc());
        unifyTaskBuildDTO.setForm(JSONObject.parseArray(storeOpenRuleDO.getRuleInfo(), GeneralDTO.class));
        List<GeneralDTO> storeList = new ArrayList<>();
        storeIdList.forEach(storeId -> {
            GeneralDTO generalDTO = new GeneralDTO();
            generalDTO.setValue(storeId);
            generalDTO.setType(Constants.STORE);
            storeList.add(generalDTO);
        });
        unifyTaskBuildDTO.setStoreIds(storeList);
        List<TaskProcessDTO> processDTOList = JSONObject.parseArray(storeOpenRuleDO.getNodeInfo(), TaskProcessDTO.class);
        String taskPattern = UnifyTaskPatternEnum.NORMAL.getCode();
        for (TaskProcessDTO taskProcessDTO : processDTOList) {
            if (UnifyNodeEnum.SECOND_NODE.getCode().equals(taskProcessDTO.getNodeNo())) {
                taskPattern = UnifyTaskPatternEnum.WORKFLOW.getCode();
                break;
            }
        }
        unifyTaskBuildDTO.setProcess(JSONObject.parseArray(storeOpenRuleDO.getNodeInfo(), TaskProcessDTO.class));
        unifyTaskBuildDTO.setTaskPattern(taskPattern);
        unifyTaskBuildDTO.setRunRule(TaskRunRuleEnum.ONCE.getCode());
        unifyTaskBuildDTO.setTaskCycle("");
        unifyTaskBuildDTO.setRunDate("");
        unifyTaskBuildDTO.setCalendarTime("");
        unifyTaskBuildDTO.setTaskInfo(storeOpenRuleDO.getTaskInfo());
        unifyTaskBuildDTO.setLimitHour(storeOpenRuleDO.getLimitHour());
        unifyTaskBuildDTO.setAttachUrl(storeOpenRuleDO.getAttachUrl());
        if (StringUtils.isNotBlank(storeOpenRuleDO.getCollaboratorId())) {
            unifyTaskBuildDTO.setCollaboratorIdList(Arrays.asList(storeOpenRuleDO.getCollaboratorId().split(Constants.COMMA)));
        }
        unifyTaskBuildDTO.setEnterpriseId(enterpriseId);
        unifyTaskBuildDTO.setRuleId(ruleId);
        unifyTaskBuildDTO.setUserId(storeOpenRuleDO.getCreateUserId());
        unifyTaskBuildDTO.setUserName(enterpriseUserDao.selectNameIgnoreActiveByUserId(enterpriseId, storeOpenRuleDO.getCreateUserId()));
        return unifyTaskBuildDTO;
    }

    private StoreOpenRuleDO transBuild(String eid, StoreOpenRuleBuildDTO storeOpenRuleBuildDTO, CurrentUser user) {
        StoreOpenRuleDO storeOpenRuleDO = new StoreOpenRuleDO();
        storeOpenRuleDO.setRuleName(storeOpenRuleBuildDTO.getRuleName());
        storeOpenRuleDO.setRuleType(storeOpenRuleBuildDTO.getRuleType());
        storeOpenRuleDO.setBeginTime(new Date(storeOpenRuleBuildDTO.getBeginTime()));
        storeOpenRuleDO.setEndTime(new Date(storeOpenRuleBuildDTO.getEndTime()));
        storeOpenRuleDO.setTaskName(storeOpenRuleBuildDTO.getTaskName());
        if(storeOpenRuleBuildDTO.getRuleId() == null){
            storeOpenRuleDO.setCreateUserId(user.getUserId());
        }else {
            storeOpenRuleDO.setUpdateUserId(user.getUserId());
        }
        storeOpenRuleDO.setTaskDesc(storeOpenRuleBuildDTO.getTaskDesc());
        storeOpenRuleDO.setStatus(Constants.INDEX_ZERO);
        storeOpenRuleDO.setOpenDateDay(storeOpenRuleBuildDTO.getOpenDateDay());
        storeOpenRuleDO.setNodeInfo(JSONObject.toJSONString(storeOpenRuleBuildDTO.getProcess()));
        storeOpenRuleDO.setRunDate(storeOpenRuleBuildDTO.getRunDate());
        storeOpenRuleDO.setCalendarTime(storeOpenRuleBuildDTO.getCalendarTime());
        storeOpenRuleDO.setTaskInfo(storeOpenRuleBuildDTO.getTaskInfo());
        transTaskForm(eid, storeOpenRuleBuildDTO, user);
        storeOpenRuleDO.setRuleInfo(JSONObject.toJSONString(storeOpenRuleBuildDTO.getForm()));
        storeOpenRuleDO.setLimitHour(storeOpenRuleBuildDTO.getLimitHour());
        storeOpenRuleDO.setLoopCount(Constants.INDEX_ONE);
        storeOpenRuleDO.setAttachUrl(storeOpenRuleBuildDTO.getAttachUrl());
        storeOpenRuleDO.setDeleted(Boolean.FALSE);
        if (CollectionUtils.isNotEmpty(storeOpenRuleBuildDTO.getTaskScope())){
            storeOpenRuleDO.setExtraParam(storeOpenRuleBuildDTO.getTaskScope().toString());
        }else {
            storeOpenRuleDO.setExtraParam(","+storeOpenRuleBuildDTO.getExtraParam()+",");
        }
        if (CollectionUtils.isNotEmpty(storeOpenRuleBuildDTO.getCollaboratorIdList())) {
            storeOpenRuleDO.setCollaboratorId(StringUtils.join(storeOpenRuleBuildDTO.getCollaboratorIdList(), Constants.COMMA));
        }
        return storeOpenRuleDO;
    }


    private Boolean setStoreOpenScheduler(String enterpriseId, StoreOpenRuleDO storeOpenRuleDO) {
        //循环任务入参校验
        String requestString = null;
        List<ScheduleCallBackRequest> jobs = Lists.newArrayList();
        jobs.add(ScheduleCallBackUtil.getCallBack(schedulerCallbackTaskUrl + "/v2/" + enterpriseId + "/communication/storeOpenRule/" + storeOpenRuleDO.getId(), ScheduleCallBackEnum.api.getValue()));

        String startTime = DateUtils.convertTimeToString(storeOpenRuleDO.getBeginTime().getTime(), DateUtils.DATE_FORMAT_DAY);
        Date beginTime = storeOpenRuleDO.getBeginTime();
        Date endTime = storeOpenRuleDO.getEndTime();
        long day = DateUtil.betweenDay(beginTime, endTime, true);
        ScheduleFixedRequest fixedRequest = new ScheduleFixedRequest(startTime + " " + storeOpenRuleDO.getCalendarTime(), jobs);
        LocalTime now = LocalTime.now();
        LocalTime calendarTime = LocalTime.parse(storeOpenRuleDO.getCalendarTime() + ":00");

        LocalDate beginDate = beginTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        //如果执行时间在任务当前时间之后，则今天会执行
        if (calendarTime.isAfter(now) || beginDate.isAfter(LocalDate.now())) {
            day = day + 1;
        }
        fixedRequest.setTimes((int) day);
        requestString = JSON.toJSONString(fixedRequest);

        log.info("新增门店规则循环任务，开始调用定时器enterpriseId={},taskId={},开始调用参数={}", enterpriseId, storeOpenRuleDO.getId(), requestString);

        String schedule = HttpRequest.sendPost(schedulerApiUrl + "/v2/" + enterpriseId + "/schedulers", requestString, ScheduleCallBackUtil.buildHeaderMap());
        JSONObject jsonObjectSchedule = JSONObject.parseObject(schedule);

        log.info("新增门店规则循环任务，结束调用定时器enterpriseId={},ruleId={},返回结果={}", enterpriseId, storeOpenRuleDO.getId(), jsonObjectSchedule);

        String scheduleId = null;
        if (ObjectUtil.isNotEmpty(jsonObjectSchedule)) {
            scheduleId = jsonObjectSchedule.getString("scheduler_id");
        }
        storeOpenRuleDO.setScheduleId(scheduleId);
        StoreOpenRuleDO update = new StoreOpenRuleDO();
        update.setId(storeOpenRuleDO.getId());
        update.setScheduleId(scheduleId);
        storeOpenRuleDAO.updateByPrimaryKeySelective(enterpriseId, update);
        return scheduleId == null ? Boolean.FALSE : Boolean.TRUE;
    }

    private void transTaskForm(String enterpriseId, StoreOpenRuleBuildDTO task, CurrentUser user) {
        List<GeneralDTO> formList = task.getForm();
        if (CollUtil.isEmpty(formList)) {
            return;
        }
        String dataFormType = formList.get(0).getType();
        // 根据快捷陈列检查项 创建临时检查表
        if (UnifyTaskDataTypeEnum.TB_DISPLAY_QUICK_COLUMN.getCode().equals(dataFormType)) {
            List<Long> tbDisplayQuickColumnIds = formList.stream().map(a -> Long.valueOf(a.getValue())).collect(Collectors.toList());
            TbMetaTableDO tbMetaTableDO = tbMetaDisplayQuickColumnService.createTableByColumnIdList(enterpriseId, tbDisplayQuickColumnIds, user);
            GeneralDTO generalDTO = new GeneralDTO();
            generalDTO.setName(tbMetaTableDO.getTableName());
            generalDTO.setType(UnifyTaskDataTypeEnum.TB_DISPLAY.getCode());
            generalDTO.setValue(tbMetaTableDO.getId().toString());
            formList = new ArrayList<>();
            formList.add(generalDTO);
        }
        task.setForm(formList);
    }

    @Override
    public Map<String, Object> toMap(String eid,PageInfo<StoreOpenRuleDTO> list,String regionId,List<String> mappingIds) {
        HashMap<String, Object> objectObjectHashMap = new HashMap<>();
        objectObjectHashMap.put("data",list);
        CountStoreRuleDTO num = storeOpenRuleDAO.count(eid,regionId,mappingIds);
        objectObjectHashMap.put("total",num);
        return objectObjectHashMap;
    }

    @Override
    public Boolean updateStoreRuleCreateUser(String eid, UpdateCreateUserDTO createUserDTO) {
        StoreOpenRuleDO storeOpenRuleDO = storeOpenRuleDAO.selectByPrimaryKey(eid,createUserDTO.getId());
        storeOpenRuleDO.setCreateUserId(createUserDTO.getCreateUserId());
        storeOpenRuleDO.setUpdateTime(new Date());
        return storeOpenRuleDAO.updateByPrimaryKeySelective(eid, storeOpenRuleDO) > 0;
    }
}
