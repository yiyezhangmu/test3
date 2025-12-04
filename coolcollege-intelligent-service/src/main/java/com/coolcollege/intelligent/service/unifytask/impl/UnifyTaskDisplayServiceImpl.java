package com.coolcollege.intelligent.service.unifytask.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.constant.LicenseFieldConstants;
import com.coolcollege.intelligent.common.constant.UnifyTaskConstant;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.util.ValidateUtil;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseStoreCheckSettingMapper;
import com.coolcollege.intelligent.dao.enterprise.SysDepartmentMapper;
import com.coolcollege.intelligent.dao.enterprise.dao.EnterpriseConfigDao;
import com.coolcollege.intelligent.dao.enterprise.dao.EnterpriseUserDao;
import com.coolcollege.intelligent.dao.metatable.TbMetaDefTableColumnMapper;
import com.coolcollege.intelligent.dao.metatable.TbMetaStaTableColumnMapper;
import com.coolcollege.intelligent.dao.region.RegionMapper;
import com.coolcollege.intelligent.dao.store.StoreGroupMapper;
import com.coolcollege.intelligent.dao.store.StoreMapper;
import com.coolcollege.intelligent.dao.store.StoreOpenRuleMapper;
import com.coolcollege.intelligent.dao.system.SysRoleMapper;
import com.coolcollege.intelligent.dao.tbdisplay.TbDisplayHistoryColumnMapper;
import com.coolcollege.intelligent.dao.tbdisplay.TbDisplayTableDataColumnMapper;
import com.coolcollege.intelligent.dao.tbdisplay.TbMetaDisplayTableColumnMapper;
import com.coolcollege.intelligent.dao.unifytask.TaskMappingMapper;
import com.coolcollege.intelligent.dao.unifytask.TaskParentMapper;
import com.coolcollege.intelligent.dao.unifytask.TaskStoreMapper;
import com.coolcollege.intelligent.dao.unifytask.TaskSubMapper;
import com.coolcollege.intelligent.dao.unifytask.dao.UnifyTaskParentCollaboratorDao;
import com.coolcollege.intelligent.dao.unifytask.dao.UnifyTaskParentUserDao;
import com.coolcollege.intelligent.dao.usergroup.dao.EnterpriseUserGroupDao;
import com.coolcollege.intelligent.model.department.DeptNode;
import com.coolcollege.intelligent.model.display.DisplayConstant;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseStoreCheckSettingDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseUserDO;
import com.coolcollege.intelligent.model.enterprise.dto.PersonDTO;
import com.coolcollege.intelligent.model.enums.*;
import com.coolcollege.intelligent.model.region.RegionDO;
import com.coolcollege.intelligent.model.region.dto.AuthVisualDTO;
import com.coolcollege.intelligent.model.store.StoreDO;
import com.coolcollege.intelligent.model.store.StoreGroupDO;
import com.coolcollege.intelligent.model.store.dto.BasicsStoreDTO;
import com.coolcollege.intelligent.model.system.SysRoleDO;
import com.coolcollege.intelligent.model.tbdisplay.TbDisplayHistoryColumnDO;
import com.coolcollege.intelligent.model.tbdisplay.TbDisplayTableDataColumnDO;
import com.coolcollege.intelligent.model.tbdisplay.TbMetaDisplayTableColumnDO;
import com.coolcollege.intelligent.model.unifytask.*;
import com.coolcollege.intelligent.model.unifytask.dto.*;
import com.coolcollege.intelligent.model.unifytask.query.DisplayQuery;
import com.coolcollege.intelligent.model.unifytask.query.TbDisplayQuery;
import com.coolcollege.intelligent.model.unifytask.vo.*;
import com.coolcollege.intelligent.model.usergroup.EnterpriseUserGroupDO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.model.userholder.UserHolder;
import com.coolcollege.intelligent.rpc.license.LicenseTypeApiService;
import com.coolcollege.intelligent.service.authentication.AuthVisualService;
import com.coolcollege.intelligent.service.enterprise.EnterpriseService;
import com.coolcollege.intelligent.service.enterprise.EnterpriseStoreCheckSettingService;
import com.coolcollege.intelligent.service.fileUpload.OssClientService;
import com.coolcollege.intelligent.service.system.SysRoleService;
import com.coolcollege.intelligent.service.unifytask.*;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Description for this class
 *
 * @author : jixiang.jiang
 * @version : 1.0
 * @Description : Description for this class
 * @date ：Created in 2020/10/27 20:23
 */
@Service
@Slf4j
public class UnifyTaskDisplayServiceImpl implements UnifyTaskDisplayService {

    @Resource
    private TaskParentMapper taskParentMapper;
    @Resource
    private TaskSubMapper taskSubMapper;
    @Resource
    private EnterpriseUserDao enterpriseUserDao;
    @Resource
    private TaskMappingMapper taskMappingMapper;
    @Resource
    private EnterpriseStoreCheckSettingMapper storeCheckSettingMapper;
    @Autowired
    private AuthVisualService authVisualService;
    @Resource
    private SysRoleMapper sysRoleMapper;
    @Resource
    private StoreMapper storeMapper;
    @Resource
    private RegionMapper regionMapper;
    @Resource
    private StoreGroupMapper storeGroupMapper;
    @Autowired
    private SysRoleService sysRoleService;
    @Resource
    private TaskStoreMapper taskStoreMapper;

    private static final Pattern PATTERN = Pattern.compile("^-?\\d+(\\.\\d+)?$");

    @Resource
    private TbDisplayHistoryColumnMapper tbDisplayHistoryColumnMapper;
    @Resource
    private TbMetaDisplayTableColumnMapper tbMetaDisplayTableColumnMapper;
    @Resource
    private TbDisplayTableDataColumnMapper tbDisplayTableDataColumnMapper;
    @Resource
    private TbMetaStaTableColumnMapper tbMetaStaTableColumnMapper;
    @Resource
    private TbMetaDefTableColumnMapper tbMetaDefTableColumnMapper;
    @Autowired
    private UnifyTaskReportService unifyTaskReportService;
    @Lazy
    @Autowired
    private UnifyTaskService unifyTaskService;
    @Resource
    private EnterpriseStoreCheckSettingService enterpriseStoreCheckSettingService;

    @Autowired
    private UnifyTaskStoreService unifyTaskStoreService;

    @Autowired
    private UnifyTaskParentCcUserService unifyTaskParentCcUserService;
    @Resource
    private OssClientService ossClientService;
    @Resource
    private LicenseTypeApiService licenseTypeApiService;
    @Resource
    private UnifyTaskParentUserDao unifyTaskParentUserDao;
    @Resource
    private EnterpriseConfigDao enterpriseConfigDao;
    @Resource
    private EnterpriseUserGroupDao enterpriseUserGroupDao;
    @Resource
    private EnterpriseService enterpriseService;
    @Resource
    private SysDepartmentMapper sysDepartmentMapper;
    @Resource
    private UnifyTaskParentCollaboratorDao unifyTaskParentCollaboratorDao;

    @Resource
    StoreOpenRuleMapper storeOpenRuleMapper;

    @Resource
    private RedisUtilPool redisUtilPool;

    @Override
    public ParentTaskDTO getDisplayParent(String enterpriseId, DisplayQuery query, CurrentUser user) {
        String userId = user.getUserId();
        String taskType = query.getTaskType();
        if (StringUtils.isBlank(query.getNodeType())) {
            query.setNodeType(TaskQueryEnum.ALL.getCode());
        }
        ParentTaskDTO result = new ParentTaskDTO();
        //管理员逻辑判断
        boolean isAdmin = sysRoleService.checkIsAdmin(enterpriseId,userId);
        switch (query.getNodeType()) {
            case UnifyTaskConstant.ROLE_CREATE:
                getCreateDisplayTask(enterpriseId, query, userId, taskType, result);
                break;
            case UnifyTaskConstant.ROLE_CC:
                PageHelper.startPage(query.getPageNumber(), query.getPageSize());
                List<UnifyTaskParentCcUserDO> taskParentCcUsers = unifyTaskParentCcUserService.selectByCcUserId(enterpriseId, userId, query);
                PageInfo taskParentPageInfo = new PageInfo(taskParentCcUsers);
                List<Long> taskIds = taskParentCcUsers.stream().map(UnifyTaskParentCcUserDO::getUnifyTaskId).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(taskIds)) {
                    List<TaskParentDO> ccTaskParentDOList = taskParentMapper.selectParentTaskByTaskIds(enterpriseId, taskIds);
                    PageInfo ccPageInfo = new PageInfo(ccTaskParentDOList);
                    List<TaskParentVO> ccTaskParentInfo = this.getParentInfo(enterpriseId, ccTaskParentDOList);
                    ccPageInfo.setList(ccTaskParentInfo);
                    ccPageInfo.setTotal(taskParentPageInfo.getTotal());
                    result.setPageInfo(ccPageInfo);
                }
                //统计信息
                Integer ccAll = unifyTaskParentCcUserService.selectDisplayParentStatistics(enterpriseId, userId, taskType, null);
                Integer ccNotStart = unifyTaskParentCcUserService.selectDisplayParentStatistics(enterpriseId, userId, taskType, UnifyTaskQueryEnum.NOSTART.getCode());
                Integer ccOngoing = unifyTaskParentCcUserService.selectDisplayParentStatistics(enterpriseId, userId, taskType, UnifyTaskQueryEnum.ONGOING.getCode());
                Integer ccComplete = unifyTaskParentCcUserService.selectDisplayParentStatistics(enterpriseId, userId, taskType, UnifyTaskQueryEnum.COMPLETE.getCode());
                UnifyParentStatisticsDTO ccStatistics = new UnifyParentStatisticsDTO(ccAll, ccNotStart, ccOngoing, ccComplete);
                result.setStatistics(ccStatistics);
                break;
            case UnifyTaskConstant.ROLE_APPROVAL:
                // 查询我处理的或者我审批的
                PageInfo<UnifyTaskParentUserDO> taskParentUserPage = unifyTaskParentUserDao.selectPageByUserId(enterpriseId, userId, query);
                List<Long> unifyTaskIds = taskParentUserPage.getList().stream().map(UnifyTaskParentUserDO::getUnifyTaskId).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(unifyTaskIds)) {
                    List<TaskParentDO> taskParentDOList = taskParentMapper.selectParentTaskByTaskIds(enterpriseId, unifyTaskIds);
                    List<TaskParentVO> taskParentInfo = this.getParentInfo(enterpriseId, taskParentDOList);
                    PageInfo pageInfo = new PageInfo(taskParentInfo);
                    pageInfo.setTotal(taskParentUserPage.getTotal());
                    result.setPageInfo(pageInfo);
                }
                //统计信息
                Integer myAll = unifyTaskParentUserDao.selectDisplayParentStatistics(enterpriseId, userId, taskType, null);
                Integer myNotStart = Constants.INDEX_ZERO;
                Integer myOngoing = unifyTaskParentUserDao.selectDisplayParentStatistics(enterpriseId, userId, taskType, UnifyTaskQueryEnum.ONGOING.getCode());
                Integer myComplete = unifyTaskParentUserDao.selectDisplayParentStatistics(enterpriseId, userId, taskType, UnifyTaskQueryEnum.COMPLETE.getCode());
                UnifyParentStatisticsDTO myStatistics = new UnifyParentStatisticsDTO(myAll, myNotStart, myOngoing, myComplete);
                result.setStatistics(myStatistics);
                break;
            default:
                if(!isAdmin){
                    if(TaskTypeEnum.TB_DISPLAY_TASK.getCode().equals(query.getTaskType())){
                        PageHelper.startPage(query.getPageNumber(), query.getPageSize());
                        List<UnifyTaskParentCollaboratorDO> taskCollaboratorUsers = unifyTaskParentCollaboratorDao.selectByCollaboratorId (enterpriseId, userId, query);
                        PageInfo taskParentCollaboratorPageInfo = new PageInfo(taskCollaboratorUsers);
                        List<Long> unifyTaskIdList = taskCollaboratorUsers.stream().map(UnifyTaskParentCollaboratorDO::getUnifyTaskId).collect(Collectors.toList());
                        if (CollectionUtils.isNotEmpty(unifyTaskIdList)) {
                            List<TaskParentDO> parentDOList = taskParentMapper.selectParentTaskByTaskIds(enterpriseId, unifyTaskIdList);
                            PageInfo collaboratorPageInfo = new PageInfo(parentDOList);
                            List<TaskParentVO>  collaboratorTaskParentInfo = this.getParentInfo(enterpriseId, parentDOList);
                            collaboratorPageInfo.setList(collaboratorTaskParentInfo);
                            collaboratorPageInfo.setTotal(taskParentCollaboratorPageInfo.getTotal());
                            result.setPageInfo(collaboratorPageInfo);
                        }
                        //统计信息
                        Integer collaboratorAll = unifyTaskParentCollaboratorDao.selectDisplayParentStatistics(enterpriseId, userId, taskType, null);
                        Integer collaboratorNotStart = unifyTaskParentCollaboratorDao.selectDisplayParentStatistics(enterpriseId, userId, taskType, UnifyTaskQueryEnum.NOSTART.getCode());
                        Integer collaboratorOngoing = unifyTaskParentCollaboratorDao.selectDisplayParentStatistics(enterpriseId, userId, taskType, UnifyTaskQueryEnum.ONGOING.getCode());
                        Integer collaboratorComplete = unifyTaskParentCollaboratorDao.selectDisplayParentStatistics(enterpriseId, userId, taskType, UnifyTaskQueryEnum.COMPLETE.getCode());
                        UnifyParentStatisticsDTO collaboratorStatistics = new UnifyParentStatisticsDTO(collaboratorAll, collaboratorNotStart, collaboratorOngoing, collaboratorComplete);
                        result.setStatistics(collaboratorStatistics);
                    }else {
                        getCreateDisplayTask(enterpriseId, query, userId, taskType, result);
                    }
                } else {
                    PageHelper.startPage(query.getPageNumber(), query.getPageSize());
                    List<TaskParentDO> taskParentDOList = taskParentMapper.selectParentTaskByAdmin(enterpriseId, query);
                    PageInfo pageInfo = new PageInfo(taskParentDOList);
                    List<TaskParentVO> taskParentInfo = this.getParentInfo(enterpriseId, taskParentDOList);
                    pageInfo.setList(taskParentInfo);
                    result.setPageInfo(pageInfo);
                    //统计信息
                    List<Long> all = taskParentMapper.selectDisplayParentStatisticsByAdmin(enterpriseId, taskType, null);
                    List<Long> notStart = taskParentMapper.selectDisplayParentStatisticsByAdmin(enterpriseId, taskType, UnifyTaskQueryEnum.NOSTART.getCode());
                    List<Long> ongoing = taskParentMapper.selectDisplayParentStatisticsByAdmin(enterpriseId, taskType, UnifyTaskQueryEnum.ONGOING.getCode());
                    List<Long> complete = taskParentMapper.selectDisplayParentStatisticsByAdmin(enterpriseId, taskType, UnifyTaskQueryEnum.COMPLETE.getCode());
                    UnifyParentStatisticsDTO statistics = new UnifyParentStatisticsDTO(all.size(), notStart.size(), ongoing.size(), complete.size());
                    result.setStatistics(statistics);
                }
                break;
        }

        return result;
    }

    /**
     * 获得我创建的陈列任务
     * @param enterpriseId
     * @param query
     * @param userId
     * @param taskType
     * @param result
     * @author: xugangkun
     * @return void
     * @date: 2021/11/18 14:40
     */
    private void getCreateDisplayTask(String enterpriseId, DisplayQuery query, String userId, String taskType, ParentTaskDTO result) {
        PageHelper.startPage(query.getPageNumber(), query.getPageSize());
        List<TaskParentDO> taskParentDOList = taskParentMapper.selectParentTaskByUserId(enterpriseId, userId, query);
        PageInfo pageInfo = new PageInfo(taskParentDOList);
        List<TaskParentVO> taskParentInfo = this.getParentInfo(enterpriseId, taskParentDOList);
        pageInfo.setList(taskParentInfo);
        result.setPageInfo(pageInfo);
        //统计信息
        Integer all = taskParentMapper.selectDisplayParentStatistics(enterpriseId, userId, taskType, null);
        Integer notStart = taskParentMapper.selectDisplayParentStatistics(enterpriseId, userId, taskType, UnifyTaskQueryEnum.NOSTART.getCode());
        Integer ongoing = taskParentMapper.selectDisplayParentStatistics(enterpriseId, userId, taskType, UnifyTaskQueryEnum.ONGOING.getCode());
        Integer complete = taskParentMapper.selectDisplayParentStatistics(enterpriseId, userId, taskType, UnifyTaskQueryEnum.COMPLETE.getCode());
        UnifyParentStatisticsDTO statistics = new UnifyParentStatisticsDTO(all, notStart, ongoing, complete);
        result.setStatistics(statistics);
    }

    @Override
    public ParentTaskMiddlePageVO getParentMiddlePageData(String enterpriseId, TbDisplayQuery query, CurrentUser user) {
        Long unifyTaskId = query.getUnifyTaskId();
        if(unifyTaskId == null){
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "父任务id【unifyTaskId】不能为空");
        }
        TaskParentDO taskParentDO = taskParentMapper.selectParentTaskByTaskId(enterpriseId, query.getUnifyTaskId());
        if(taskParentDO == null){
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "未找到对应的父任务【"+query.getUnifyTaskId()+"】");
        }
        ParentTaskMiddlePageVO result = new ParentTaskMiddlePageVO();
        PageInfo pageInfo = new PageInfo(new ArrayList());

        TaskParentVO vo = new TaskParentVO();
        BeanUtils.copyProperties(taskParentDO, vo);
        List<String> storeIdList = taskStoreMapper.listStoreIdByUnifyTaskId(enterpriseId, unifyTaskId);
        List<String> storeRange = Lists.newArrayList();
        if(CollUtil.isNotEmpty(storeIdList)){
            List<StoreDO> storeDOList = storeMapper.getByStoreIdList(enterpriseId, storeIdList);
            storeRange = storeDOList.stream().map(StoreDO::getStoreName).collect(Collectors.toList());
        }
        List<UnifyFormDataDTO> unifyFormDataDTOList = taskMappingMapper.selectMappingDataByTaskId(enterpriseId, unifyTaskId);
        Set<Long> metaTableIds = unifyFormDataDTOList.stream().map(m -> Long.parseLong(m.getOriginMappingId())).collect(Collectors.toSet());
        List<TbMetaDisplayTableColumnDO> tbMetaDisplayTableColumnDOList = tbMetaDisplayTableColumnMapper.listByTableIdList(enterpriseId, new ArrayList<>(metaTableIds));
        vo.setMetaColumnCount(Long.valueOf(tbMetaDisplayTableColumnDOList.size()));
        vo.setStoreRange(CollUtil.isNotEmpty(storeRange)?StringUtils.join(storeRange, ","):"还未生成门店对应的子任务");

        // 处理人 审批人  复审人
        List<String> allPositionList =  Lists.newArrayList();
        List<String> allNodeUserIdList =  Lists.newArrayList();
        List<String> groupIdList = Lists.newArrayList();
        List<String> regionIdList = Lists.newArrayList();
        // 任务id#节点 人员职位
        Map<String, List<GeneralDTO>> taskNodePersonTypeMap = Maps.newHashMap();
        List<TaskProcessDTO> process = JSON.parseArray(taskParentDO.getNodeInfo(), TaskProcessDTO.class);
        List<TaskProcessDTO> newProcess = getNewProcess(process);
        newProcess.forEach(proItem -> {
            String proNode = proItem.getNodeNo();
            List<GeneralDTO> proUserList = proItem.getUser();
            if (CollectionUtils.isNotEmpty(proUserList)) {
                List<String> positionList = proUserList.stream().filter(f -> UnifyTaskConstant.PersonType.POSITION.equals(f.getType()))
                        .map(GeneralDTO::getValue).collect(Collectors.toList());
                List<String> nodePersonList = proUserList.stream().filter(f -> UnifyTaskConstant.PersonType.PERSON.equals(f.getType()))
                        .map(GeneralDTO::getValue).collect(Collectors.toList());
                List<String> groupList = proUserList.stream().filter(f -> UnifyTaskConstant.PersonType.USER_GROUP.equals(f.getType()))
                        .map(GeneralDTO::getValue).collect(Collectors.toList());
                List<String> organizationIdList = proUserList.stream().filter(f -> UnifyTaskConstant.PersonType.ORGANIZATION.equals(f.getType()))
                        .map(GeneralDTO::getValue).collect(Collectors.toList());
                allPositionList.addAll(positionList);
                allNodeUserIdList.addAll(nodePersonList);
                groupIdList.addAll(groupList);
                regionIdList.addAll(organizationIdList);
            }
            taskNodePersonTypeMap.put(taskParentDO.getId()+Constants.MOSAICS+proNode, proUserList);
        });
        Map<Long, String> roleDOMap = Maps.newHashMap();
        if (CollUtil.isNotEmpty(allPositionList)) {
            List<SysRoleDO> roleDOList = sysRoleMapper.selectRoleByIdList(enterpriseId, allPositionList);
            roleDOMap = roleDOList.stream().filter(a -> a.getRoleName() != null && a.getId() != null)
                    .collect(Collectors.toMap(SysRoleDO::getId, SysRoleDO::getRoleName));
        }
        Map<String, String> allNodeUserDOMap = enterpriseUserDao.getUserNameMap(enterpriseId, allNodeUserIdList);
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
        taskNodePersonTypeMap.forEach((node, userPositionList) -> {
            fillPersonName(userPositionList, finalRoleDOMap, finalAllNodeUserDOMap, finalRegionNameMap, groupNameMap);
        });
        List<GeneralDTO> handlePerson= taskNodePersonTypeMap.get(StringUtils.join(taskParentDO.getId(), Constants.MOSAICS, UnifyNodeEnum.FIRST_NODE.getCode()));
        String handleUserName = getNodePersonName(handlePerson);
        vo.setHandleUserName(handleUserName);
        List<GeneralDTO> approvePerson= taskNodePersonTypeMap.get(StringUtils.join(taskParentDO.getId(), Constants.MOSAICS, UnifyNodeEnum.SECOND_NODE.getCode()));
        String approveUserName = getNodePersonName(approvePerson);
        vo.setApproveUserName(approveUserName);
        List<GeneralDTO> recheckPerson= taskNodePersonTypeMap.get(StringUtils.join(taskParentDO.getId(), Constants.MOSAICS, UnifyNodeEnum.THIRD_NODE.getCode()));
        String recheckUserName = getNodePersonName(recheckPerson);
        vo.setRecheckUserName(recheckUserName);
        List<GeneralDTO> ccPerson= taskNodePersonTypeMap.get(StringUtils.join(taskParentDO.getId(), Constants.MOSAICS, UnifyNodeEnum.CC.getCode()));
        String ccUserName = getNodePersonName(ccPerson);
        vo.setCcUserName(ccUserName);

        if(CollUtil.isEmpty(storeIdList)){
            result.setPageInfo(pageInfo);
            UnifyParentStatisticsDTO statisticsTemp = new UnifyParentStatisticsDTO(0, 0, 0, 0);
            result.setStatistics(statisticsTemp);
            result.setTaskParentVO(vo);
            return  result;
            // throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "还未生成门店对应的子任务");
        }

        PageHelper.startPage(query.getPageNumber(), query.getPageSize());
        List<TaskParentCycleVO> taskParentCycleVOList = taskParentMapper.getParentMiddlePageData(enterpriseId, query);
        //统计信息
        List<TaskStoreDO> allTaskStoreList = taskStoreMapper.selectTbDisplayParentStatistics(enterpriseId, unifyTaskId);
        Map<Long, List<TaskStoreDO>> allTaskStoreMap = allTaskStoreList.stream()
                .collect(Collectors.groupingBy(TaskStoreDO::getLoopCount));
        Integer all = allTaskStoreMap.size(), nostart = 0, ongoing = 0, complete = 0;
        for (Map.Entry<Long, List<TaskStoreDO>> entry : allTaskStoreMap.entrySet()) {
            List<TaskStoreDO> taskStoreDOList = entry.getValue();
            Integer nostartTmp = checkLoopStatusNum(taskStoreDOList,UnifyTaskQueryEnum.NOSTART.getCode());
            Integer ongoingTmp = checkLoopStatusNum(taskStoreDOList,UnifyTaskQueryEnum.ONGOING.getCode());
            Integer completeTmp = checkLoopStatusNum(taskStoreDOList,UnifyTaskQueryEnum.COMPLETE.getCode());
            nostart += nostartTmp;
            ongoing += ongoingTmp;
            complete += completeTmp;
        }
        UnifyParentStatisticsDTO statistics = new UnifyParentStatisticsDTO(all, nostart, ongoing, complete);
        result.setStatistics(statistics);

        List<Long> taskIds = new ArrayList<>();
        taskIds.add(unifyTaskId);
        //未完成的人员集合,用于发催办
        List<UnifyParentUser> urgingUserList = taskSubMapper.selectUnCompleteUser(enterpriseId, taskIds, null, null, null, null, null, null, null);
        Map<Long, Set<String>> urgingUserMap = urgingUserList.stream().collect(Collectors.groupingBy(UnifyParentUser::getUnifyTaskId,
                Collectors.mapping(UnifyParentUser::getUserId, Collectors.toSet())));
        vo.setUrgingUser(urgingUserMap.get(unifyTaskId));
        result.setTaskParentVO(vo);

        if(CollUtil.isEmpty(taskParentCycleVOList)){
            result.setPageInfo(pageInfo);
            return result;
        }

        List<TaskStoreDO> taskStoreDOList = taskStoreMapper.listByUnifyTaskId(enterpriseId, query.getUnifyTaskId());
        Map<Long, List<TaskStoreDO>> taskStoreMap = taskStoreDOList.stream().collect(Collectors.groupingBy(TaskStoreDO::getLoopCount));
        Map<String, List<TaskStoreDO>> taskStoreStatusMap = taskStoreDOList.stream().collect(Collectors.groupingBy(e ->
                StringUtils.join(e.getLoopCount(), Constants.MOSAICS, e.getSubStatus())));

        List<String> finalStoreRange = storeRange;
        taskParentCycleVOList.forEach(taskParentCycleVO -> {
            taskParentCycleVO.setMetaColumnCount(tbMetaDisplayTableColumnDOList.size());
            taskParentCycleVO.setTableName(unifyFormDataDTOList.get(0).getMappingName());
            taskParentCycleVO.setHandleUserName(handleUserName);
            taskParentCycleVO.setApproveUserName(approveUserName);
            taskParentCycleVO.setRecheckUserName(recheckUserName);
            taskParentCycleVO.setCcUserName(ccUserName);
            List<TaskStoreDO> taskStoreDOListByLoopCountStatus = taskStoreStatusMap.get(taskParentCycleVO.getLoopCount() +Constants.MOSAICS+ UnifyStatus.COMPLETE.getCode());
            if(CollUtil.isNotEmpty(taskStoreDOListByLoopCountStatus)){
                taskParentCycleVO.setFinishCount(taskStoreDOListByLoopCountStatus.size());
            }
            List<TaskStoreDO> taskStoreDOListByLoopCount = taskStoreMap.get(taskParentCycleVO.getLoopCount());
            taskParentCycleVO.setAllCount(taskStoreDOListByLoopCount.size());
            taskParentCycleVO.setOverdueTask(getOverDueByTaskStore(taskStoreDOListByLoopCount));
            taskParentCycleVO.setStatus(getSubStatusByTaskStore(taskStoreDOListByLoopCount));
            taskParentCycleVO.setStoreRange(StringUtils.join(finalStoreRange, ","));
            taskParentCycleVO.setUrgingUser(urgingUserMap.get(taskParentCycleVO.getUnifyTaskId()));
        });
        pageInfo = new PageInfo(taskParentCycleVOList);
        pageInfo.setList(taskParentCycleVOList);
        result.setPageInfo(pageInfo);
        return result;
    }

    /**
     * 获取父任务详情数据
     *
     * @param enterpriseId
     * @param taskParentDOList
     * @return
     */
    @Override
    public List<TaskParentVO> getParentInfo(String enterpriseId, List<TaskParentDO> taskParentDOList) {
        CurrentUser user = UserHolder.getUser();
        String userId = user.getUserId();
        List<TaskParentVO> taskList = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(taskParentDOList)) {
            //获取数据列表
            List<Long> taskIds = taskParentDOList.stream().map(TaskParentDO::getId).collect(Collectors.toList());
            List<UnifyFormDataDTO> formDataList = taskMappingMapper.selectMappingData(enterpriseId, taskIds);
            Map<Long, List<UnifyFormDataDTO>> checkMap = formDataList.stream()
                    .collect(Collectors.groupingBy(UnifyFormDataDTO::getUnifyTaskId));
            //协作人
            List<UnifyPersonDTO> collaboratorList = unifyTaskParentCollaboratorDao.selectCollaboratorIdByTaskIdList(enterpriseId, taskIds);
            Map<Long, List<UnifyPersonDTO>> collaboratorMap =
                    ListUtils.emptyIfNull(collaboratorList).stream().collect(Collectors.groupingBy(UnifyPersonDTO::getUnifyTaskId));
            List<String> collaboratorIdList = ListUtils.emptyIfNull(collaboratorList).stream().map(UnifyPersonDTO::getUserId).collect(Collectors.toList());
            // ##### start
            // 任务id 门店范围
            Map<Long, List<GeneralDTO>> taskStoreRangeMap = unifyTaskReportService.getTaskStoreRange(enterpriseId, taskIds);
            // 处理人 审批人  复审人
            List<String> allPositionList =  Lists.newArrayList();
            List<String> allNodeUserIdList =  Lists.newArrayList();
            List<String> groupIdList = Lists.newArrayList();
            List<String> regionIdList = Lists.newArrayList();
            if(CollectionUtils.isNotEmpty(collaboratorIdList)){
                allNodeUserIdList.addAll(collaboratorIdList);
            }
            // 任务id#节点 人员职位
            Map<String, List<GeneralDTO>> taskNodePersonTypeMap = Maps.newHashMap();
            taskParentDOList.forEach(taskParentDO -> {
                List<TaskProcessDTO> process = JSON.parseArray(taskParentDO.getNodeInfo(), TaskProcessDTO.class);
                List<TaskProcessDTO> newProcess = getNewProcess(process);
                newProcess.forEach(proItem -> {
                    String proNode = proItem.getNodeNo();
                    List<GeneralDTO> proUserList = proItem.getUser();
                    if (CollectionUtils.isNotEmpty(proUserList)) {
                        List<String> positionList = proUserList.stream().filter(f -> UnifyTaskConstant.PersonType.POSITION.equals(f.getType()))
                                .map(GeneralDTO::getValue).collect(Collectors.toList());
                        List<String> nodePersonList = proUserList.stream().filter(f -> UnifyTaskConstant.PersonType.PERSON.equals(f.getType()))
                                .map(GeneralDTO::getValue).collect(Collectors.toList());
                        List<String> groupList = proUserList.stream().filter(f -> UnifyTaskConstant.PersonType.USER_GROUP.equals(f.getType()))
                                .map(GeneralDTO::getValue).collect(Collectors.toList());
                        List<String> organizationIdList = proUserList.stream().filter(f -> UnifyTaskConstant.PersonType.ORGANIZATION.equals(f.getType()))
                                .map(GeneralDTO::getValue).collect(Collectors.toList());
                        allPositionList.addAll(positionList);
                        allNodeUserIdList.addAll(nodePersonList);
                        groupIdList.addAll(groupList);
                        regionIdList.addAll(organizationIdList);
                    }
                    taskNodePersonTypeMap.put(taskParentDO.getId()+Constants.MOSAICS+proNode, proUserList);
                });
            });
            Map<Long, String> roleDOMap = Maps.newHashMap();
            if (CollUtil.isNotEmpty(allPositionList)) {
                List<SysRoleDO> roleDOList = sysRoleMapper.selectRoleByIdList(enterpriseId, allPositionList);
                roleDOMap = roleDOList.stream().filter(a -> a.getRoleName() != null && a.getId() != null)
                        .collect(Collectors.toMap(SysRoleDO::getId, SysRoleDO::getRoleName));
            }
            Map<String, String> allNodeUserDOMap = enterpriseUserDao.getUserNameMap(enterpriseId, allNodeUserIdList);

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
            // ##### end

            //获取 完成数量
            List<UnifyParentCount> endList = taskParentMapper.selectEndTaskCount(enterpriseId, taskIds);
            Map<Long, Integer> endMap = endList
                    .stream()
                    .filter(a -> a.getUnifyTaskId() != null && a.getCount() != null)
                    .collect(Collectors.toMap(UnifyParentCount::getUnifyTaskId, UnifyParentCount::getCount, (a, b) -> a));
            //未完成的人员集合,用于发催办
            List<UnifyParentUser> urgingUserList =
                taskSubMapper.selectUnCompleteUser(enterpriseId, taskIds, null, null, null, null, null, null, null);
            Map<Long, Set<String>> urgingUserMap = urgingUserList.stream()
                    .collect(Collectors.groupingBy(UnifyParentUser::getUnifyTaskId,
                            Collectors.mapping(UnifyParentUser::getUserId, Collectors.toSet())));
            //获取门店
            List<UnifyStoreDTO> storeDTOS = taskStoreMapper.selectStoreByTaskIds(enterpriseId, taskIds);
            Map<Long, List<BasicsStoreDTO>> storeMap = Maps.newHashMap();
            if(CollUtil.isNotEmpty(storeDTOS)){
                Set<String> existStoreIds = storeDTOS.stream().map(UnifyStoreDTO::getStoreId).collect(Collectors.toSet());
                List<StoreDO> storeInfo = storeMapper.getStoresByStoreIds(enterpriseId, new ArrayList<>(existStoreIds));
                Map<String, String> storeInfoMap = ListUtils.emptyIfNull(storeInfo)
                        .stream()
                        .filter(a -> a.getStoreId() != null && a.getStoreName() != null)
                        .collect(Collectors.toMap(StoreDO::getStoreId, StoreDO::getStoreName, (a, b) -> a));
                storeMap = storeDTOS.stream()
                        .collect(Collectors.groupingBy(UnifyStoreDTO::getUnifyTaskId,
                                Collectors.mapping(s -> new BasicsStoreDTO(s.getStoreId(), storeInfoMap.get(s.getStoreId())), Collectors.toList())));

            }
            
            List<UnifyFormDataDTO> unifyFormDataDTOList = taskMappingMapper.selectMappingData(enterpriseId, taskIds);
            Map<Long, String> unifyTaskIdTableIdMap = Maps.newHashMap();
            Map<Long, Long> tableColumnDOMap = Maps.newHashMap();
            if(CollUtil.isNotEmpty(unifyFormDataDTOList)){
                Set<Long> metaTableIds = unifyFormDataDTOList.stream().map(m -> Long.parseLong(m.getOriginMappingId())).collect(Collectors.toSet());
                List<TbMetaDisplayTableColumnDO> tableColumnDOList = tbMetaDisplayTableColumnMapper.listByTableIdList(enterpriseId, new ArrayList<>(metaTableIds));
                unifyTaskIdTableIdMap = unifyFormDataDTOList.stream()
                        .filter(a -> a.getUnifyTaskId() != null && a.getOriginMappingId() != null)
                        .collect(Collectors.toMap(UnifyFormDataDTO::getUnifyTaskId, UnifyFormDataDTO::getOriginMappingId, (a, b) -> a));
                tableColumnDOMap = ListUtils.emptyIfNull(tableColumnDOList)
                        .stream().filter(e -> Constants.INDEX_ZERO.equals(e.getCheckType())).collect(Collectors.groupingBy(TbMetaDisplayTableColumnDO::getMetaTableId, Collectors.counting()));
            }

            Map<Long, String> finalRoleDOMap = roleDOMap;
            Map<String, String> finalAllNodeUserDOMap = allNodeUserDOMap;
            //DO转VO
            for (TaskParentDO item : taskParentDOList) {
                TaskParentVO taskVO = new TaskParentVO();
                BeanUtil.copyProperties(item, taskVO);
                Boolean expireFlag = Boolean.FALSE;
                Boolean editFlag = Boolean.FALSE;
                if(item.getBeginTime() > System.currentTimeMillis()){
                    taskVO.setParentStatus(UnifyStatus.NOSTART.getCode());
                    if(userId.equals(item.getCreateUserId())){
                        editFlag = Boolean.TRUE;
                    }
                }
                if (item.getEndTime() < System.currentTimeMillis()) {
                    if(!UnifyStatus.COMPLETE.getCode().equals(item.getParentStatus())){
                        expireFlag = Boolean.TRUE;
                    }
                    editFlag = Boolean.FALSE;
                }
                taskVO.setExpireFlag(expireFlag);
                taskVO.setEditFlag(editFlag);
                taskVO.setFormData(checkMap.get(item.getId()));

                List<GeneralDTO> handlePersonPosition= taskNodePersonTypeMap.get(StringUtils.join(item.getId(), Constants.MOSAICS, UnifyNodeEnum.FIRST_NODE.getCode()));
                fillPersonName(handlePersonPosition, finalRoleDOMap, finalAllNodeUserDOMap, regionNameMap, groupNameMap);
                taskVO.setHandlePersonPosition(handlePersonPosition);

                List<GeneralDTO> approvalPersonPosition= taskNodePersonTypeMap.get(StringUtils.join(item.getId(), Constants.MOSAICS, UnifyNodeEnum.SECOND_NODE.getCode()));
                if(CollUtil.isNotEmpty(approvalPersonPosition)){
                    fillPersonName(approvalPersonPosition, finalRoleDOMap, finalAllNodeUserDOMap, regionNameMap, groupNameMap);
                    taskVO.setApprovalPersonPosition(approvalPersonPosition);
                }
                List<BasicsStoreDTO> basicsStore = storeMap.get(item.getId());
                taskVO.setAllCount(basicsStore == null ? 0 : basicsStore.size());
                taskVO.setStoreList(basicsStore);
                List<GeneralDTO> taskStoreRange = taskStoreRangeMap.get(item.getId());
                taskVO.setTaskStoreRange(taskStoreRange);
                Integer endCount = endMap.get(item.getId()) == null ? 0 : endMap.get(item.getId());
                taskVO.setEndCount(endCount);
                taskVO.setUrgingUser(urgingUserMap.get(item.getId()));
                taskVO.setTaskCycle(item.getTaskCycle());
                taskVO.setRunRule(item.getRunRule());
                taskVO.setRunDate(item.getRunDate());
                taskVO.setCalendarTime(item.getCalendarTime());
                taskVO.setTaskInfo(item.getTaskInfo());
                taskVO.setLimitHour(item.getLimitHour());
                taskVO.setStatusType(item.getStatusType());
                if(TaskTypeEnum.TB_DISPLAY_TASK.getCode().equals(item.getTaskType())){
                    String metaTableId = unifyTaskIdTableIdMap.get(item.getId());
                    Long checkItemNum = tableColumnDOMap.get(Long.parseLong(metaTableId));
                    taskVO.setMetaColumnCount(checkItemNum==null?0:checkItemNum);
                }
                if(Constants.SYSTEM_USER_ID.equals(taskVO.getCreateUserId())){
                    taskVO.setCreateUserName(Constants.SYSTEM_USER_NAME);
                }
                List<UnifyPersonDTO> collaboratorTaskList = collaboratorMap.get(item.getId());
                if(CollectionUtils.isNotEmpty(collaboratorTaskList)){
                    taskVO.setCollaboratorList(getCollaboratorNameList(collaboratorTaskList, allNodeUserDOMap));
                }
                if(TaskTypeEnum.TB_DISPLAY_TASK.getCode().equals(item.getTaskType())){
                    String metaTableId = unifyTaskIdTableIdMap.get(item.getId());
                    Long checkItemNum = tableColumnDOMap.get(Long.parseLong(metaTableId));
                    taskVO.setMetaColumnCount(checkItemNum==null?0:checkItemNum);
                }
                if(TaskTypeEnum.ACHIEVEMENT_OLD_PRODUCTS_OFF.getCode().equals(item.getTaskType()) ||
                        TaskTypeEnum.ACHIEVEMENT_NEW_RELEASE.getCode().equals(item.getTaskType())){
                    JSONObject jsonObject = JSON.parseObject(item.getTaskInfo());
                    String taskInfoStr = jsonObject.getString(Constants.PRODUCT);
                    taskVO.setProductInfoDTO(JSONObject.parseObject(taskInfoStr, ProductInfoDTO.class));
                }
                taskList.add(taskVO);
            }
        }
        return taskList;
    }

    @Override
    public SubTaskDTO getDisplaySub(String enterpriseId, DisplayQuery query, CurrentUser user) {
        SubTaskDTO subTaskDTO = new SubTaskDTO();
        String userId = user.getUserId();
        if (ObjectUtil.isEmpty(query.getUnifyTaskId()) || StringUtils.isEmpty(userId)) {
            throw new ServiceException(ErrorCodeEnum.PARAMS_REQUIRED);
        }
        query.setUserId(userId);
        boolean isAdmin = sysRoleService.checkIsAdmin(enterpriseId,userId);
        //与某任务下当前处理人有关系的门店集合
        // List<String> storeList = taskMappingMapper.selectStoreByPerson(enterpriseId, query.getUnifyTaskId(), userId);
        List<TaskSubVO> result = null;
        PageInfo pageInfo = new PageInfo(new ArrayList());
        if (UnifyTaskQueryEnum.ALL.getCode().equals(query.getQueryType()) || StringUtils.isEmpty(query.getQueryType())) {
            //全部查询要经过数据筛选，取子任务最新节点，所有人可见,
            //全部查出来自定义筛选再分页
            //查询的门店子任务与当前处理人有关
            //管理员判断
            PageHelper.startPage(query.getPageNumber(), query.getPageSize());
            List<TaskStoreDO> taskStoreDOList =  taskStoreMapper.selectForSubTaskListByTaskId(enterpriseId, query, null, UnifyTaskQueryEnum.ALL.getCode());
            PageInfo tempPageInfo = new PageInfo(taskStoreDOList);
            List<String> subTaskCodeLoopCounts = Lists.newArrayList();
            List<Long> loopCounts = Lists.newArrayList();
            List<String> taskCodeLoopCountKey = Lists.newArrayList();
            taskStoreDOList.forEach(taskStore -> {
                subTaskCodeLoopCounts.add(StringUtils.join(taskStore.getUnifyTaskId(), Constants.MOSAICS, taskStore.getStoreId()));
                loopCounts.add(taskStore.getLoopCount());
                taskCodeLoopCountKey.add(StringUtils.join(taskStore.getUnifyTaskId(), Constants.MOSAICS, taskStore.getStoreId(), Constants.MOSAICS, taskStore.getLoopCount()));
            });
            if(CollectionUtils.isNotEmpty(subTaskCodeLoopCounts)){
                result = taskSubMapper.selectBySubTaskCodeLoopCounts(enterpriseId, subTaskCodeLoopCounts, loopCounts);
                if(CollectionUtils.isNotEmpty(result)){
                    result = result.stream().filter(o->taskCodeLoopCountKey.contains(StringUtils.join(o.getUnifyTaskId(), Constants.MOSAICS, o.getStoreId(), Constants.MOSAICS, o.getLoopCount()))).collect(Collectors.toList());
                }
            }
            if(CollectionUtils.isNotEmpty(result)){
                result = allSubGroup(result, userId);
                getSubDetailAbout(result, enterpriseId, query);
                pageInfo = new PageInfo(result);
                pageInfo.setTotal(tempPageInfo.getTotal());
                pageInfo.setPageSize(query.getPageSize());
                pageInfo.setPageNum(query.getPageNumber());
            }
        } else if (UnifyTaskQueryEnum.COMPLETE.getCode().equals(query.getQueryType())) {
            //查询的门店子任务与当前处理人有关
            //管理员判断
            PageHelper.startPage(query.getPageNumber(), query.getPageSize());
            result = taskSubMapper.selectAllOrEndSubTaskDataNew(enterpriseId, query, null, UnifyTaskQueryEnum.COMPLETE.getCode());
            getSubDetailAbout(result, enterpriseId, query);
            pageInfo = new PageInfo(result);
        } else {
            //待处理或审批或复检  都与当前用户有关
            PageHelper.startPage(query.getPageNumber(), query.getPageSize());
            result = taskSubMapper.selectSubTaskDataNew(enterpriseId, query);
            getSubDetailAbout(result, enterpriseId, query);
            pageInfo = new PageInfo(result);
        }
        subTaskDTO.setPageInfo(pageInfo);
        UnifySubStatisticsDTO statistics = getDisplaySubStatistics(enterpriseId, query.getUnifyTaskId(), query.getLoopCount(), null, isAdmin, userId);
        subTaskDTO.setStatistics(statistics);
        return subTaskDTO;
    }

    @Override
    public TaskParentDetailVO getDisplayParentDetail(String enterpriseId, Long taskId) {
        String userId = UserHolder.getUser().getUserId();
        TaskParentDO taskParentDO = taskParentMapper.selectParentTaskById(enterpriseId, taskId);
        ValidateUtil.validateObj(taskParentDO);
        //获取陈列列表
        TaskParentDetailVO taskVO = new TaskParentDetailVO();
        BeanUtil.copyProperties(taskParentDO, taskVO);
        if(taskParentDO.getBeginTime() > System.currentTimeMillis()){
            taskVO.setParentStatus(UnifyStatus.NOSTART.getCode());
        }
        taskVO.setRegionModel(taskParentDO.getRegionModel());
        //模板权限
        List<UnifyFormDataDTO> formDataList = taskMappingMapper.selectMappingDataByTaskId(enterpriseId, taskId);
        if (CollectionUtils.isNotEmpty(formDataList)) {
            formDataList.stream().forEach(unifyFormDataDTO -> {
                String tableType = unifyFormDataDTO.getType();
                if(StringUtils.isNotBlank(tableType)&& UnifyTaskDataTypeEnum.STANDARD.getCode().equals(tableType)){
                    Integer columnCount = tbMetaStaTableColumnMapper.countByMetaTableId(enterpriseId, Collections.singletonList(Long.valueOf(unifyFormDataDTO.getOriginMappingId())));
                    unifyFormDataDTO.setColumnCount(columnCount);
                }
                if(StringUtils.isNotBlank(tableType)&&UnifyTaskDataTypeEnum.DEFINE.getCode().equals(tableType)) {
                    Integer columnCount = tbMetaDefTableColumnMapper.selectColumnCountByTableId(enterpriseId, Long.valueOf(unifyFormDataDTO.getOriginMappingId()));;
                    unifyFormDataDTO.setColumnCount(columnCount);
                }
                if(StringUtils.isNotBlank(tableType)&&UnifyTaskDataTypeEnum.TB_DISPLAY.getCode().equals(tableType)) {
                    Integer columnCount = tbMetaDisplayTableColumnMapper.selectColumnCountByTableId(enterpriseId, Long.valueOf(unifyFormDataDTO.getOriginMappingId()));
                    unifyFormDataDTO.setColumnCount(columnCount);
                }
            });
            taskVO.setFormData(formDataList);

        } else {
            taskVO.setFormData(Lists.newArrayList());
        }
        //门店范围回显
        List<BasicsStoreDTO> basicsStoreDTOS =  dealStoreInfo(enterpriseId, taskId, userId);
        taskVO.setStoreList(basicsStoreDTOS);
        List<BasicsStoreDTO> storeScopeInputList = getStoreScopeInput(enterpriseId, taskId);
        if(CollectionUtils.isEmpty(storeScopeInputList)){
            storeScopeInputList=basicsStoreDTOS;
        }
        taskVO.setInputStoreScopeList(storeScopeInputList);
        //流程信息处理
        List<TaskProcessDTO> process = JSONArray.parseArray(taskParentDO.getNodeInfo(), TaskProcessDTO.class);
        List<String> positionIdList = Lists.newArrayList();
        List<String> userIdList = Lists.newArrayList();
        List<String> groupIdList = Lists.newArrayList();
        List<String> regionIdList = Lists.newArrayList();
        userIdList.add(taskParentDO.getCreateUserId());
        String updateId = taskParentDO.getUpdateUserId();
        if (StringUtils.isNotEmpty(updateId)) {
            userIdList.add(updateId);
        }
        process.forEach(item -> {
            List<GeneralDTO> user = item.getUser();
            user.forEach(u -> {
                switch (u.getType()) {
                    case UnifyTaskConstant.PersonType.POSITION:
                        positionIdList.add(u.getValue());
                        break;
                    case UnifyTaskConstant.PersonType.PERSON:
                        userIdList.add(u.getValue());
                        break;
                    case UnifyTaskConstant.PersonType.USER_GROUP:
                        groupIdList.add(u.getValue());
                        break;
                    case UnifyTaskConstant.PersonType.ORGANIZATION:
                        regionIdList.add(u.getValue());
                        break;
                    default:
                        break;
                }
            });
        });
        Map<String, String> useMap = enterpriseUserDao.getUserNameMap(enterpriseId, userIdList);
        //将positionId的类型转换成long
        List<Long> longPositionList = ListUtils.emptyIfNull(positionIdList)
                .stream()
                .map(data -> {
                    Matcher isNum = PATTERN.matcher(data);
                    if (isNum.matches() && data.length() != 32) {
                        return Long.valueOf(data);
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        List<SysRoleDO> roleList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(longPositionList)) {
            roleList = sysRoleMapper.getRoleByRoleIds(enterpriseId, longPositionList);
        }
        Map<String, String> positionMap = roleList.stream()
                .filter(a -> a.getId() != null && a.getRoleName() != null)
                .collect(Collectors.toMap(data -> String.valueOf(data.getId()), SysRoleDO::getRoleName, (a, b) -> a));

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

        //用户、岗位有效性校验
        Map<String, String> finalRegionNameMap = regionNameMap;
        process.forEach(item -> {
            List<GeneralDTO> user = item.getUser();
            user.forEach(u -> {
                switch (u.getType()) {
                    case UnifyTaskConstant.PersonType.POSITION:
                        String name = positionMap.get(u.getValue());
                        u.setName(name);
                        if (StringUtils.isEmpty(name)) {
                            u.setValid(false);
                        } else {
                            u.setValid(true);
                        }
                        break;
                    case UnifyTaskConstant.PersonType.PERSON:
                        u.setName(useMap.get(u.getValue()));
                        if(Constants.AI.equals(u.getValue())){
                            u.setName(Constants.AI);
                        }
                        break;
                    case UnifyTaskConstant.PersonType.USER_GROUP:
                        u.setName(groupNameMap.get(u.getValue()));
                        u.setValid(!StringUtils.isEmpty(u.getName()));
                        break;
                    case UnifyTaskConstant.PersonType.ORGANIZATION:
                        u.setName(finalRegionNameMap.get(u.getValue()));
                        u.setValid(!StringUtils.isEmpty(u.getName()));
                        break;
                    default:
                        break;
                }
            });
        });
        String createUserName = useMap.get(taskVO.getCreateUserId());
        if (Constants.SYSTEM_USER_ID.equals(taskVO.getCreateUserId())) {
            createUserName = Constants.SYSTEM_USER_NAME;
        }
        taskVO.setCreateUserName(createUserName);
        taskVO.setUpdateUserName(useMap.get(taskVO.getUpdateUserId()));
        taskVO.setProcess(process);
        //协作人列表
        List<PersonDTO> personDTOList = new ArrayList<>();
        List<String> collaboratorIdList = unifyTaskParentCollaboratorDao.selectCollaboratorIdByTaskId(enterpriseId, taskId);
        if(CollectionUtils.isNotEmpty(collaboratorIdList)){
            Map<String, String> userMap = enterpriseUserDao.getUserNameMap(enterpriseId, collaboratorIdList);
            collaboratorIdList.forEach(editUserId -> {
                PersonDTO personDTO = new PersonDTO();
                personDTO.setUserId(editUserId);
                personDTO.setUserName(userMap.get(editUserId));
                personDTOList.add(personDTO);
            });
        }
        if(TaskTypeEnum.ACHIEVEMENT_OLD_PRODUCTS_OFF.getCode().equals(taskParentDO.getTaskType()) ||
                TaskTypeEnum.ACHIEVEMENT_NEW_RELEASE.getCode().equals(taskParentDO.getTaskType())){
            JSONObject jsonObject = JSON.parseObject(taskParentDO.getTaskInfo());
            String taskInfoStr = jsonObject.getString(Constants.PRODUCT);
            taskVO.setProductInfoDTOList(JSONObject.parseArray(taskInfoStr, ProductInfoDTO.class));
        }
        taskVO.setCollaboratorList(personDTOList);
//        boolean operateOverdue = taskSubMapper.getByTaskId(enterpriseId, taskId).get(0).isOperateOverdue();
        String taskInfo = taskParentDO.getTaskInfo();
        JSONObject taskInfoJson = JSON.parseObject(taskInfo);
        DataSourceHelper.reset();
        EnterpriseStoreCheckSettingDO enterpriseStoreCheckSetting = storeCheckSettingMapper.getEnterpriseStoreCheckSetting(enterpriseId);
        boolean isOperateOverdue = taskInfoJson.containsKey("isOperateOverdue") ?
                taskInfoJson.getBooleanValue("isOperateOverdue") : enterpriseStoreCheckSetting.getOverdueTaskContinue();
        taskVO.setOverdueTaskContinue(isOperateOverdue);
        return taskVO;
    }

    @Override
    public List<BasicsStoreDTO> getStoreScopeInput(String eid, Long taskId) {

        TaskParentDO taskParentDO = taskParentMapper.selectParentTaskById(eid, taskId);
        String taskInfo = taskParentDO.getExtraParam();
        JSONObject taskInfoJson = JSONObject.parseObject(taskInfo);
        if(taskInfoJson==null){
            return Collections.emptyList();
        }
        String taskInputScopeList = taskInfoJson.getString("inputStoreScopeList");
        if (taskInputScopeList==null){
            return Collections.emptyList();
        }
        List<GeneralDTO> generalDTOS = JSONObject.parseObject(taskInputScopeList, new TypeReference<List<GeneralDTO>>() {
        });
        List<String> storeList = Lists.newArrayList();
        List<String> regionList = Lists.newArrayList();
        List<String> groupList = Lists.newArrayList();
        for (GeneralDTO item : generalDTOS) {
            switch (item.getType()) {
                case UnifyTaskConstant.StoreType.STORE:
                    storeList.add(item.getValue());
                    break;
                case UnifyTaskConstant.StoreType.REGION:
                    regionList.add(item.getValue());
                    break;
                case UnifyTaskConstant.StoreType.GROUP:
                    groupList.add(item.getValue());
                    break;
                default:
                    break;
            }
        }
        //区域
        Map<String, String> regionDOMap = getRegionMap(eid, regionList);
        //分组
        Map<String, String> groupDOMap = getGroupMap(eid, groupList);
        //门点
        Map<String, String> storeMap = getStoreMap(eid, storeList);
        List<BasicsStoreDTO> basicsStore = Lists.newArrayList();
        for (GeneralDTO generalDTO : generalDTOS) {
            BasicsStoreDTO basicsStoreDTO;
            switch (generalDTO.getType()) {
                case UnifyTaskConstant.StoreType.STORE:
                    basicsStoreDTO = new BasicsStoreDTO(generalDTO.getValue(), storeMap.get(generalDTO.getValue()), true, generalDTO.getType(), null, null);
                    basicsStore.add(basicsStoreDTO);
                    break;
                case UnifyTaskConstant.StoreType.REGION:
                    basicsStoreDTO = new BasicsStoreDTO(generalDTO.getValue(), regionDOMap.get(generalDTO.getValue()), true, generalDTO.getType(), null, null);
                    basicsStore.add(basicsStoreDTO);
                    break;
                case UnifyTaskConstant.StoreType.GROUP:
                    basicsStoreDTO = new BasicsStoreDTO(generalDTO.getValue(), groupDOMap.get(generalDTO.getValue()), true, generalDTO.getType(), null, null);
                    basicsStore.add(basicsStoreDTO);
                    break;
                default:
                    break;
            }
        }
        return basicsStore;
    }

    private List<BasicsStoreDTO> dealStoreInfo(String enterpriseId, Long taskId, String userId){
        //门店权限
        List<UnifyStoreDTO> storeTaskList = taskMappingMapper.selectStoreInfo(enterpriseId, Arrays.asList(taskId));
        List<String> storeList = Lists.newArrayList();
        List<String> regionList = Lists.newArrayList();
        List<String> groupList = Lists.newArrayList();
        for (UnifyStoreDTO item : storeTaskList) {
            switch (item.getType()) {
                case UnifyTaskConstant.StoreType.STORE:
                    storeList.add(item.getStoreId());
                    break;
                case UnifyTaskConstant.StoreType.REGION:
                    regionList.add(item.getStoreId());
                    break;
                case UnifyTaskConstant.StoreType.GROUP:
                    groupList.add(item.getStoreId());
                    break;
                case UnifyTaskConstant.StoreType.GROUP_REGION:
                    groupList.add(item.getStoreId());
                    regionList.add(item.getFilterRegionId());
                    break;
                default:
                    break;
            }
        }
        //区域
        Map<String, String> regionDOMap = getRegionMap(enterpriseId, regionList);
        //分组
        Map<String, String> groupDOMap = getGroupMap(enterpriseId, groupList);
        //常规
        List<String> authStoreList = Lists.newArrayList();
        Map<String, String> storeMap = Maps.newHashMap();
        if (CollectionUtils.isNotEmpty(storeList)) {
            AuthVisualDTO authStore = authVisualService.authRegionStoreByStore(enterpriseId, userId, storeList);
            if (ObjectUtil.isEmpty(authStore)) {
                authStoreList = Lists.newArrayList();
            } else {
                authStoreList = ListUtils.emptyIfNull(authStore.getStoreIdList());
            }
            storeMap = getStoreMap(enterpriseId, storeList);
        }
        List<BasicsStoreDTO> basicsStore = Lists.newArrayList();
        for (UnifyStoreDTO s : storeTaskList) {
            BasicsStoreDTO basicsStoreDTO;
            switch (s.getType()) {
                case UnifyTaskConstant.StoreType.STORE:
                    if (authStoreList.contains(s.getStoreId())) {
                        basicsStoreDTO = new BasicsStoreDTO(s.getStoreId(), storeMap.get(s.getStoreId()), true, s.getType(), null, null);
                    } else {
                        basicsStoreDTO = new BasicsStoreDTO(s.getStoreId(), storeMap.get(s.getStoreId()), false, s.getType(), null, null);
                    }
                    basicsStore.add(basicsStoreDTO);
                    break;
                case UnifyTaskConstant.StoreType.REGION:
                    basicsStoreDTO = new BasicsStoreDTO(s.getStoreId(), regionDOMap.get(s.getStoreId()), true, s.getType(), null, null);
                    basicsStore.add(basicsStoreDTO);
                    break;
                case UnifyTaskConstant.StoreType.GROUP:
                    basicsStoreDTO = new BasicsStoreDTO(s.getStoreId(), groupDOMap.get(s.getStoreId()), true, s.getType(), null, null);
                    basicsStore.add(basicsStoreDTO);
                    break;
                case UnifyTaskConstant.StoreType.GROUP_REGION:
                    basicsStoreDTO = new BasicsStoreDTO(s.getStoreId(), groupDOMap.get(s.getStoreId()), true, s.getType(), regionDOMap.get(s.getFilterRegionId()), s.getFilterRegionId());
                    basicsStore.add(basicsStoreDTO);
                    break;
                default:
                    break;
            }
        }
        basicsStore = basicsStore.stream().distinct().collect(Collectors.toList());
        return basicsStore;
    }

    private Map<String, String> getStoreMap(String enterpriseId, List<String> storeList) {
        Map<String, String> storeMap;
        if(CollectionUtils.isEmpty(storeList)){
            return new HashMap<>();
        }
        List<StoreDO> storeDOList = storeMapper.getStoresByStoreIds(enterpriseId, new ArrayList<>(storeList));
        storeMap = ListUtils.emptyIfNull(storeDOList)
                .stream()
                .filter(a -> a.getStoreId() != null && a.getStoreName() != null)
                .collect(Collectors.toMap(StoreDO::getStoreId, StoreDO::getStoreName, (a, b) -> a));
        return storeMap;
    }

    private Map<String, String> getRegionMap(String enterpriseId, List<String> regionList) {
        Map<String, String> regionDOMap = Maps.newHashMap();
        if (CollectionUtils.isNotEmpty(regionList)) {
            List<RegionDO> regionDOList = regionMapper.getRegionByRegionIds(enterpriseId, regionList);
            regionDOMap = regionDOList.stream()
                    .filter(a -> a.getRegionId() != null && a.getName() != null)
                    .collect(Collectors.toMap(RegionDO::getRegionId, RegionDO::getName, (a, b) -> a));
        }
        return regionDOMap;
    }

    private Map<String, String> getGroupMap(String enterpriseId, List<String> groupList) {
        Map<String, String> groupDOMap = Maps.newHashMap();
        if (CollectionUtils.isNotEmpty(groupList)) {
            List<StoreGroupDO> storeGroupDOS = storeGroupMapper.getListByIds(enterpriseId, groupList);
            groupDOMap = storeGroupDOS.stream()
                    .filter(a -> a.getGroupId() != null && a.getGroupName() != null)
                    .collect(Collectors.toMap(StoreGroupDO::getGroupId, StoreGroupDO::getGroupName, (a, b) -> a));
        }
        return groupDOMap;
    }

    @Override
    public TaskSubVO getDisplaySubDetail(String enterpriseId, Long subTaskId, String userId, Long taskStoreId) {
        if(subTaskId == null && taskStoreId == null){
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "任务参数不能为空");
        }

        Long uifyTaskId = 0L;
        String storeId = "";
        Long loopCount = 1L;
        String OverdueTaskContinue = null;
        if(subTaskId != null){
            TaskSubDO taskSubDO = taskSubMapper.selectSubTaskById(enterpriseId, subTaskId);
            if (ObjectUtil.isEmpty(taskSubDO)) {
                // return  null;
                throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "未找到对应的子任务");
            }
            uifyTaskId = taskSubDO.getUnifyTaskId();
            storeId = taskSubDO.getStoreId();
            loopCount = taskSubDO.getLoopCount();
            if (Objects.nonNull(taskSubDO.getIsOperateOverdue())){
                //审批节点没有这个状态
                OverdueTaskContinue = taskSubDO.getIsOperateOverdue();
                String redisKey = "OverdueTaskContinue:" + subTaskId + "_" + enterpriseId;
                redisUtilPool.setString(redisKey, OverdueTaskContinue.replaceAll("^\"|\"$", ""));
            }
        }

        if(taskStoreId != null){
            TaskStoreDO taskStoreDO = taskStoreMapper.selectByPrimaryKey(enterpriseId, taskStoreId);
            if (ObjectUtil.isEmpty(taskStoreDO)) {
                // return  null;
                throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "未找到对应的子任务");
            }
            uifyTaskId = taskStoreDO.getUnifyTaskId();
            storeId = taskStoreDO.getStoreId();
            loopCount = taskStoreDO.getLoopCount();
        }



        TaskParentDO taskParentDO = taskParentMapper.selectTaskById(enterpriseId, uifyTaskId);
        // 查询最新node节点
        // 查询当前人记录
        TaskSubVO taskSub = taskSubMapper.getLatestSubId(enterpriseId, uifyTaskId, storeId, loopCount,
                userId, UnifyStatus.ONGOING.getCode(), null);
        if (taskSub == null) {
            //查不到当前人记录查询进行中最新的一条,
            taskSub = taskSubMapper.getLatestSubId(enterpriseId, uifyTaskId, storeId, loopCount,
                    null, UnifyStatus.ONGOING.getCode(), null);
        }

        if (taskSub == null) {
            //查不到进行中的记录，则任务已完成查询完成的记录
            taskSub = taskSubMapper.getLatestSubId(enterpriseId, uifyTaskId, storeId, loopCount,
                    null, UnifyStatus.COMPLETE.getCode(), UnifyNodeEnum.END_NODE.getCode());
        }
        if (taskSub == null) {
            //查不到进行中的记录，则任务已完成查询完成的记录
            taskSub = taskSubMapper.getLatestSubId(enterpriseId, uifyTaskId, storeId, loopCount,
                    null, UnifyStatus.COMPLETE.getCode(), null);
        }
        if (taskSub == null) {
            throw new ServiceException(ErrorCodeEnum.SUB_TASK_NOT_EXIST);
        }
        subTaskId = taskSub.getSubTaskId();
        //子任务详情
        DisplayQuery query = new DisplayQuery();
        query.setSubTaskId(subTaskId);

        ValidateUtil.validateString(userId);
        query.setUserId(userId);
        List<TaskSubVO> subVOList = Lists.newArrayList();
        TaskSubVO subDetail = taskSubMapper.selectSubTaskDetailByIdNew(enterpriseId, query.getSubTaskId());
        subVOList.add(subDetail);
        Long taskId = subDetail.getUnifyTaskId();
        ValidateUtil.validateObj(taskId);
        query.setUnifyTaskId(taskId);
        query.setLoopCount(subDetail.getLoopCount());
        getSubDetailAbout(subVOList, enterpriseId, query);
        //获取陈列表数据
        TaskSubVO taskSubVO = subVOList.get(0);
        StoreDO storeDO = storeMapper.getByStoreId(enterpriseId, storeId);
        if (Objects.nonNull(storeDO)) {
            taskSubVO.setStoreNum(storeDO.getStoreNum());
        }
        List<UnifyFormDataDTO> formDataList = taskMappingMapper.selectMappingDataByTaskId(enterpriseId, taskId);
        taskSubVO.setFormData(formDataList);
        assembleHistoryData(taskSubVO, enterpriseId, taskId, subDetail.getStoreId());
        //2021-5-13 业培一体需求，需要回显抄送人
        taskSubVO.setNodeInfo(taskParentDO.getNodeInfo());
        taskSubVO.setAttachUrl(taskParentDO.getAttachUrl());
        taskSubVO.setRunRule(taskParentDO.getRunRule());
        taskSubVO.setTaskCycle(taskParentDO.getTaskCycle());
        EnterpriseStoreCheckSettingDO settingDO = enterpriseStoreCheckSettingService.getEnterpriseStoreCheckSetting(enterpriseId);
        taskSubVO.setOverdueRun(settingDO.getOverdueTaskContinue());
        // 处理门店采集任务图片
        this.handleStoreLicensePicture(enterpriseId, taskSubVO, userId);
        taskSubVO.setOverdueTaskContinue("1".equals(OverdueTaskContinue) || StringUtils.isBlank(OverdueTaskContinue));
        return taskSubVO;
    }

    @Override
    public List<Object> getSubOperHistoryData(String enterpriseId, Long subTaskId) {
        TaskSubVO subDetail = taskSubMapper.selectSubTaskDetailByIdNew(enterpriseId, subTaskId);

        List<TaskSubVO> result = Lists.newArrayList();
        result.add(subDetail);
        //  填充父任务信息
        fillParentTaskInfo(enterpriseId, result);
        String storeId = subDetail.getStoreId();
        List<Object> list = assembleHistoryData(subDetail, enterpriseId, subDetail.getUnifyTaskId(), storeId);
        return list;
    }

    @Override
    public TaskSubVO getDetailByParentId(String enterpriseId, Long taskId, CurrentUser user) {
        String userId = UserHolder.getUser().getUserId();
        ValidateUtil.validateString(userId);
        DisplayQuery subQuery = new DisplayQuery();
        subQuery.setUnifyTaskId(taskId);
        List<TaskSubVO> result = taskSubMapper.selectAllOrEndSubTaskDataNew(enterpriseId, subQuery, null, UnifyTaskQueryEnum.ALL.getCode());
        result = allSubGroup(result, userId);
        ValidateUtil.validateList(result);
        //子任务详情
        DisplayQuery query = new DisplayQuery();
        query.setSubTaskId(result.get(0).getSubTaskId());
        query.setUserId(userId);
        List<TaskSubVO> subVOList = Lists.newArrayList();
        TaskSubVO subDetail = taskSubMapper.selectSubTaskDetailByIdNew(enterpriseId, query.getSubTaskId());
        subVOList.add(subDetail);
        query.setUnifyTaskId(taskId);
        query.setLoopCount(subDetail.getLoopCount());
        getSubDetailAbout(subVOList, enterpriseId, query);
        //获取陈列表数据
        TaskSubVO taskSubVO = subVOList.get(0);
        List<UnifyFormDataDTO> formDataList = taskMappingMapper.selectMappingDataByTaskId(enterpriseId, taskId);
        taskSubVO.setFormData(formDataList);
        String storeId = subDetail.getStoreId();
        assembleHistoryData(taskSubVO, enterpriseId, taskId, storeId);
        taskSubVO.setNodeInfo(null);
        return taskSubVO;
    }

    @Override
    public List<TaskSubVO> getDisplayBatchSubDetail(String enterpriseId, List<Long> subTaskIdList, String userId, List<Long> taskStoreIdList) {
        List<TaskSubVO> taskSubVOList = Lists.newArrayList();
        if(CollectionUtils.isNotEmpty(subTaskIdList)){
            for(Long subTaskId: subTaskIdList){
                TaskSubVO subVO = getDisplaySubDetail(enterpriseId, subTaskId, userId, null);
                if(ObjectUtil.isNotEmpty(subVO)){
                    taskSubVOList.add(subVO);
                }
            }
        }
        if(CollectionUtils.isNotEmpty(taskStoreIdList)){
            for(Long taskStoreId: taskStoreIdList){
                TaskSubVO subVO = getDisplaySubDetail(enterpriseId, null, userId, taskStoreId);
                if(ObjectUtil.isNotEmpty(subVO)){
                    taskSubVOList.add(subVO);
                }
            }
        }
        return taskSubVOList;
    }

    /**
     * 历史数据处理
     * @param taskSubVO
     * @param enterpriseId
     * @param taskId
     * @param storeId
     */
    private List<Object> assembleHistoryData(TaskSubVO taskSubVO, String enterpriseId, Long taskId, String storeId) {//FIXME 这是遗留的恶心，垃圾的代码
        String taskType = taskSubVO.getTaskType();
        //流程引擎历史数据处理
        List<UnifySubHistoryDTO> subHistoryList = taskSubMapper.selectSubTaskHistoryByTaskId(enterpriseId, taskId, storeId);
        //批量获取用户名头像
        Set<String> userIds = Sets.newHashSet();
        userIds.add(taskSubVO.getTaskCreateUserId());
        subHistoryList.forEach(item -> {
            userIds.add(item.getCreateUserId());
            userIds.add(item.getHandleUserId());
            if(StringUtils.isNotEmpty(item.getTurnUserId())){
                userIds.add(item.getTurnUserId());
            }
        });
        Map<String, EnterpriseUserDO> userMap = enterpriseUserDao.getUserMap(enterpriseId, new ArrayList<>(userIds));
        EnterpriseUserDO taskCreateUser =userMap.get(taskSubVO.getTaskCreateUserId());
        if(ObjectUtil.isNotEmpty(taskCreateUser)){
            taskSubVO.setTaskCreateUserName(taskCreateUser.getName());
        }
        List<Long> rejectCidList = Lists.newArrayList();
        //原始历史数据处理，补全人员信息，收集拒绝记录
        subHistoryList.stream().map(m -> {
            EnterpriseUserDO createUser = userMap.get(m.getCreateUserId());
            EnterpriseUserDO handleUser = userMap.get(m.getHandleUserId());
            if (ObjectUtil.isNotEmpty(createUser)) {
                m.setCreateAvatar(createUser.getAvatar());
                m.setCreateUserName(createUser.getName());
            }
            if (ObjectUtil.isNotEmpty(handleUser)) {
                m.setHandleUserName(handleUser.getName());
                m.setHandleAvatar(handleUser.getAvatar());
            }
            if(StringUtils.isNotEmpty(m.getTurnUserId())){
                EnterpriseUserDO turnUser = userMap.get(m.getTurnUserId());
                if(ObjectUtil.isNotEmpty(turnUser)){
                    m.setTurnUserName(turnUser.getName());
                }
            }
            if(DisplayConstant.ActionKeyConstant.REJECT.equals(m.getAction()) && StringUtils.isNotEmpty(m.getCid())){
                rejectCidList.add(Long.parseLong(m.getCid()));
            }
            if(UnifyNodeEnum.END_NODE.getCode().equals(m.getNodeNo())){
                JSONArray jsonArray = JSONArray.parseArray(taskSubVO.getNodeInfo());
                //寻找最大节点
                if(CollectionUtils.isNotEmpty(jsonArray)){
                    int node = 1;
                    for(int i=0;i<jsonArray.size();i++) {
                        String numStr =jsonArray.getJSONObject(i).getString("nodeNo");
                        if(StringUtils.isNumeric(numStr) && Integer.parseInt(numStr)> node){
                            node = Integer.parseInt(numStr);
                        }
                    }
                    m.setNodeNo(String.valueOf(node));
                }
            }
            return m;
        }).collect(Collectors.toList());
        //按循环次数分组
        List<Object> history = Lists.newArrayList();
        //历史中假如任务创建者数据
        UnifySubHistoryDTO createHistory = UnifySubHistoryDTO.builder()
                .action("create")
                .createUserId(taskSubVO.getTaskCreateUserId())
                .handleUserId(taskSubVO.getTaskCreateUserId())
                .createTime(taskSubVO.getParentCreateTime())
                .handleTime(taskSubVO.getParentCreateTime())
                .nodeNo(UnifyNodeEnum.ZERO_NODE.getCode())
                .flowState(UnifyTaskConstant.FLOW_PROCESSED)
                .taskData(taskSubVO.getTaskInfo())
                .build();
        EnterpriseUserDO taskUser = userMap.get(taskSubVO.getTaskCreateUserId());
        if(Objects.nonNull(taskUser)){
            createHistory.setCreateUserName(taskUser.getName());
            createHistory.setHandleUserName(taskUser.getName());
            createHistory.setCreateAvatar(taskUser.getAvatar());
            createHistory.setHandleAvatar(taskUser.getAvatar());
        }
        history.add(createHistory);
        LinkedHashMap<String, List<UnifySubHistoryDTO>> historyMap = subHistoryList.stream()
            .collect(Collectors.groupingBy(
                s -> StringUtils.join(s.getLoopCount(), "#", s.getGroupItem(), "#", s.getNodeNo()), LinkedHashMap::new,
                        Collectors.toList()));
        //只有陈列显示拒绝图片
        Map<Long, List<TbDisplayHistoryColumnDO>> tbRejectMap = Maps.newLinkedHashMap();

        if(CollectionUtils.isNotEmpty(rejectCidList) && TaskTypeEnum.TB_DISPLAY_TASK.getCode().equals(taskType)){
            List<TbDisplayHistoryColumnDO> tbDisplayHistoryColumnDOList = tbDisplayHistoryColumnMapper.getListByHistoryId(enterpriseId, rejectCidList);
            tbRejectMap = tbDisplayHistoryColumnDOList.stream()
                    .collect(Collectors.groupingBy(TbDisplayHistoryColumnDO::getHistoryId));
        }
        for (Map.Entry<String, List<UnifySubHistoryDTO>> entry : historyMap.entrySet()) {
            List<UnifySubHistoryDTO> mapValue = entry.getValue();
            //筛选出处理的，拒绝的，转交的记录集合
            Map<Long, UnifySubHistoryDTO> goalMap = Maps.newLinkedHashMap();
            Set<HistoryUserDTO> historyUserSet  = Sets.newLinkedHashSet();
            Boolean addUserFlag = Boolean.FALSE;
            for(UnifySubHistoryDTO item : mapValue){
                goalMap.put(item.getId(),item);
                if(UnifyTaskConstant.FLOW_PROCESSED.equals(item.getFlowState())){
                    addUserFlag = Boolean.TRUE;

                    if(DisplayConstant.ActionKeyConstant.REJECT.equals(item.getAction())&&
                            TaskTypeEnum.TB_DISPLAY_TASK.getCode().equals(taskType)){
                        item.setApprovalDataNew(tbRejectMap.get(Long.valueOf(item.getCid())));
                    }
                    history.add(item);
                }
                if(UnifyTaskConstant.FLOW_INIT.equals(item.getFlowState())
                        ||DisplayConstant.ActionKeyConstant.PASS.equals(item.getAction())
                        ||DisplayConstant.ActionKeyConstant.REJECT.equals(item.getAction())){
                    HistoryUserDTO userDTO = new HistoryUserDTO(item.getNodeNo(), item.getHandleUserId(),item.getHandleUserName()
                            ,item.getHandleAvatar(),item.getAction());
                    historyUserSet.add(userDTO);

                }
            }
            List<UnifySubHistoryDTO> turnList = dealTurnParent(mapValue,  goalMap);
            //节点处理情况显示
            findTurnUser(turnList, historyUserSet);
            if(addUserFlag ){
                history.add(historyUserSet);
            }
        }
        taskSubVO.setHistory(history);
        return  history;
    }

    /**
     * 转交父子关系处理
     * @param listGoal
     * @param goalMap
     * @return
     */
    private  List<UnifySubHistoryDTO> dealTurnParent(List<UnifySubHistoryDTO> listGoal, Map<Long, UnifySubHistoryDTO> goalMap) {
        //父子关系建立
        for (UnifySubHistoryDTO g : listGoal) {
            Long pid = g.getParentTurnId();
            if (ObjectUtil.isNotEmpty(pid)) {
                UnifySubHistoryDTO parentGoal = goalMap.get(pid);
                List<UnifySubHistoryDTO> subListGoal = parentGoal.getSubList();
                if (CollectionUtils.isEmpty(subListGoal)) {
                    subListGoal = Lists.newArrayList();
                }
                subListGoal.add(g);
                parentGoal.setSubList(subListGoal);
            }
        }
        //筛选出首次转交的人
        List<UnifySubHistoryDTO> list = Lists.newArrayList();
        for (Long k : goalMap.keySet()) {
            UnifySubHistoryDTO tempGoal = goalMap.get(k);
            if (ObjectUtil.isEmpty(tempGoal.getParentTurnId()) && DisplayConstant.ActionKeyConstant.TURN.equals(tempGoal.getAction())) {
                list.add(tempGoal);
            }
        }
        return list;
    }

    /**
     * 寻找最终转交者
     * @param turnList
     * @param historyUserSet
     */
    private void findTurnUser(List<UnifySubHistoryDTO> turnList, Set<HistoryUserDTO> historyUserSet){
        if(CollectionUtils.isNotEmpty(turnList)){
            for(UnifySubHistoryDTO item : turnList){
                recursionUser(item, historyUserSet);
            }
        }
    }
    private  void recursionUser(UnifySubHistoryDTO item, Set<HistoryUserDTO> historyUserSet){
        List<UnifySubHistoryDTO> sublist = item.getSubList();
        if(CollectionUtils.isNotEmpty(sublist)){
            recursionUser(sublist.get(0), historyUserSet);
        }else{
            HistoryUserDTO userDTO = new HistoryUserDTO(item.getNodeNo(), item.getHandleUserId(),item.getHandleUserName()
                    ,item.getHandleAvatar(),item.getAction());
            historyUserSet.add(userDTO);
        }
    }

    /**
     * 子任务列表页面获取子任务详情的一些关联数据
     *
     * @param result
     */
    private void getSubDetailAbout(List<TaskSubVO> result, String enterpriseId, DisplayQuery query) {
        if (CollectionUtils.isNotEmpty(result)) {

            //  填充父任务信息
            fillParentTaskInfo(enterpriseId, result);

            //查询是否企业配置逾期代办是否显示
            DataSourceHelper.reset();
            EnterpriseStoreCheckSettingDO storeCheckSettingDO = storeCheckSettingMapper.getEnterpriseStoreCheckSetting(enterpriseId);
            Boolean overTask = storeCheckSettingDO.getOverdueTaskContinue();
            DataSourceHelper.changeToMy();
            //创建人处理人员 其他他相关人员汇总.
            Set<String> allUseIdList = Sets.newHashSet();
            List<String> storeIds = Lists.newArrayList();
            List<Long> taskIds = Lists.newArrayList();
            result.forEach(item->{
                allUseIdList.add(item.getTaskCreateUserId());
                allUseIdList.add(item.getCreateUserId());
                allUseIdList.add(item.getHandleUserId());
                storeIds.add(item.getStoreId());
                taskIds.add(item.getUnifyTaskId());
            });
            Map<String, List<UnifyPersonDTO>> personMap = getTaskPerson(enterpriseId, query.getUnifyTaskId(), storeIds);
            Map<String, List<UnifyPersonDTO>> personMapFromSubTask = unifyTaskService.getTaskPersonFromSubTask(enterpriseId, query.getUnifyTaskId(), storeIds, query.getLoopCount(), null);
            Map<String, String> useMap = enterpriseUserDao.getUserNameMap(enterpriseId, new ArrayList<>(allUseIdList));

            /**
             * 修复线上oom错误
             */
            List<TbDisplayTableDataColumnDO> tbDisplayTableDataColumnDOList = null;//tbDisplayTableDataColumnMapper.listByUnifyTaskIdList(enterpriseId, taskIds);
            Map<String, Long> taskIdCheckItemNumMap = ListUtils.emptyIfNull(tbDisplayTableDataColumnDOList).stream()
                    .collect(Collectors.groupingBy(k -> k.getUnifyTaskId()+ Constants.MOSAICS +k.getStoreId()+ Constants.MOSAICS +k.getLoopCount(), Collectors.counting()));


            for (TaskSubVO m : result) {
                //需要对system创建 的企业进行特殊判断。设置name为system
                String mapTaskCreateName = useMap.get(m.getTaskCreateUserId());
                String taskCreateUserName = Constants.SYSTEM_USER_ID.equals(m.getTaskCreateUserId()) ? Constants.SYSTEM_USER_NAME : mapTaskCreateName;
                String mapCreateName = useMap.get(m.getCreateUserId());
                String createUserName = Constants.SYSTEM_USER_ID.equals(m.getCreateUserId()) ? Constants.SYSTEM_USER_NAME : mapCreateName;
                m.setCreateUserName(createUserName);
                m.setHandleUserName(useMap.get(m.getHandleUserId()));
                m.setTaskCreateUserName(taskCreateUserName);
                if (m.getSubEndTime() < System.currentTimeMillis()) {
                    if(!UnifyStatus.COMPLETE.getCode().equals(m.getSubStatus())){
                        m.setExpireFlag(Boolean.TRUE);
                    }else if(m.getHandleTime() != null && m.getHandleTime() > m.getSubEndTime() ){
                        m.setExpireFlag(Boolean.TRUE);
                    }else {
                        m.setExpireFlag(Boolean.FALSE);
                    }
                } else {
                    m.setExpireFlag(Boolean.FALSE);
                }
                //审批方式回显
                JSONArray jsonArray = JSONArray.parseArray(m.getNodeInfo());
                Map<String, String> nodeMap = Maps.newHashMap();
                //工单不处理
                if(!TaskTypeEnum.QUESTION_ORDER.getCode().equals(m.getTaskType())){
                    jsonArray.stream().map(p -> {
                        JSONObject nodeObj = JSONObject.parseObject(JSON.toJSONString(p));
                        nodeMap.put(nodeObj.getString("nodeNo"), nodeObj.getString("approveType"));
                        return p;
                    }).collect(Collectors.toList());
                }
;
                m.setApproveType(nodeMap.get(m.getFlowNodeNo()));
                if(StringUtils.isBlank(m.getApproveType())){
                    m.setApproveType(UnifyTaskConstant.ApproveType.ANY);
                }
                //相关人员处理
                List<UnifyPersonDTO> person = personMap.get(m.getStoreId() + Constants.MOSAICS + m.getLoopCount() );
                List<UnifyPersonDTO> personFromSubTask = personMapFromSubTask.get(m.getStoreId());

                Map<String, List<PersonDTO>> processUser = person.stream()
                        .collect(Collectors.groupingBy(UnifyPersonDTO::getNode,
                                Collectors.mapping(s -> new PersonDTO(s.getUserId(), s.getUserName(), s.getAvatar()), Collectors.toList())));
                if (CollectionUtils.isNotEmpty(personFromSubTask)) {
                    Map<String, List<PersonDTO>> processUserFromSubTask = personFromSubTask.stream()
                            .collect(Collectors.groupingBy(UnifyPersonDTO::getNode,
                                    Collectors.mapping(s -> new PersonDTO(s.getUserId(), s.getUserName(), s.getAvatar()), Collectors.toList())));
                    // 处理人从子任务获取
                    processUser.put(UnifyNodeEnum.FIRST_NODE.getCode(), processUserFromSubTask.get(UnifyNodeEnum.FIRST_NODE.getCode()));
                }
                m.setProcessUser(processUser);
                //用于一键提醒
                List<UnifyParentUser> urgingUser = taskSubMapper.selectUnCompleteUser(enterpriseId, Arrays.asList(m.getUnifyTaskId()),
                    m.getFlowNodeNo(), m.getGroupItem(), m.getLoopCount(), m.getStoreId(), null, null, null);
                m.setUrgingUser(urgingUser.stream().map(UnifyParentUser::getUserId).collect(Collectors.toSet()));
                //用户操作权限判断
                if(m.getSubBeginTime() > System.currentTimeMillis()){
                    //未开始不可编辑
                    m.setEditFlag(Boolean.FALSE);
                    m.setTurnFlag(Boolean.FALSE);
                }else{
//                    dealButtonShow(m, processUser, query.getUserId(), overTask, storeCheckSettingDO.getHandlerOvertimeTaskContinue(), storeCheckSettingDO.getApproveOvertimeTaskContinue());
                    String redisKey = "OverdueTaskContinue:" + query.getSubTaskId() + "_" + enterpriseId;
                    String value = redisUtilPool.getString(redisKey);
                    boolean taskContinue = "1".equals(value) || StringUtils.isBlank(value);
                    dealButtonShow(m, processUser, query.getUserId(), overTask, taskContinue, storeCheckSettingDO.getApproveOvertimeTaskContinue());
                }

                Long metaColumnCount = taskIdCheckItemNumMap.get(m.getUnifyTaskId()+ Constants.MOSAICS +m.getStoreId()+ Constants.MOSAICS +m.getLoopCount());
                m.setMetaColumnCount(1L);
            }
        }
    }

    // 子任务列表填充父任务相关信息
    private void fillParentTaskInfo(String enterpriseId, List<TaskSubVO> result) {
        if (CollectionUtils.isNotEmpty(result)) {
            List<Long> taskIds = result.stream().map(data -> data.getUnifyTaskId()).collect(Collectors.toList());
            List<TaskParentDO> taskParentList = taskParentMapper.selectTaskByIds(enterpriseId, taskIds);
            Map<Long, TaskParentDO> taskParentMap = taskParentList.stream().collect(Collectors.toMap(TaskParentDO::getId, Function.identity(), (a, b) -> a));
            for (TaskSubVO m : result) {
                m.setTaskName(taskParentMap.get(m.getUnifyTaskId()).getTaskName());
                m.setBeginTime(taskParentMap.get(m.getUnifyTaskId()).getBeginTime());
                m.setEndTime(taskParentMap.get(m.getUnifyTaskId()).getEndTime());
                m.setNodeInfo(taskParentMap.get(m.getUnifyTaskId()).getNodeInfo());
                m.setTaskCreateUserId(taskParentMap.get(m.getUnifyTaskId()).getCreateUserId());
                m.setParentCreateTime(taskParentMap.get(m.getUnifyTaskId()).getCreateTime());
                m.setTaskType(taskParentMap.get(m.getUnifyTaskId()).getTaskType());
                m.setTaskInfo(taskParentMap.get(m.getUnifyTaskId()).getTaskInfo());
                m.setTaskDesc(taskParentMap.get(m.getUnifyTaskId()).getTaskDesc());
            }
        }
    }

    /**
     * 用户操作权限判断
     * @param m
     * @param user
     * @param userId
     */
    @Override
    public void dealButtonShow(TaskSubVO m, Map<String, List<PersonDTO>> user,
                               String userId, Boolean overTask, Boolean handlerOvertimeTaskContinue, Boolean approveOvertimeTaskContinue) {

        if (TaskTypeEnum.TB_DISPLAY_TASK.getCode().equals(m.getTaskType())) {
            Long handlerEndTime = m.getHandlerEndTime() == null ? m.getSubEndTime() : m.getHandlerEndTime().getTime();
            //待处理截止逾期
            if (UnifyNodeEnum.FIRST_NODE.getCode().equals(m.getFlowNodeNo())
                    && handlerEndTime < System.currentTimeMillis()
                    && !handlerOvertimeTaskContinue) {
                m.setEditFlag(Boolean.FALSE);
                m.setTurnFlag(Boolean.FALSE);
                return;
            }
            //待审批已逾期
            if ((UnifyNodeEnum.isApproveNode(m.getFlowNodeNo()))
                    && m.getSubEndTime() < System.currentTimeMillis() && !approveOvertimeTaskContinue) {
                m.setEditFlag(Boolean.FALSE);
                m.setTurnFlag(Boolean.FALSE);
                return;
            }

        } else {
            //已逾期并且，企业配置逾期任务不可执行，不允许任务操作
            if (m.getSubEndTime() < System.currentTimeMillis() && !overTask) {
                m.setEditFlag(Boolean.FALSE);
                m.setTurnFlag(Boolean.FALSE);
                return;
            }
        }
        /*        按钮显示判断
                第一步：判断subStatus =complete  按钮全部不显示
                第二步  判断subStatus  =ongoing，判断node；
                node=“1”，flowInstanceId =null    判断当前用户在不在数组handlePerson里
                node=“1”，flowInstanceId不为null(拒绝打回的情况，只有当时的发起人能处理) 判断当前用户是否是handleUserId
                node=“2”  判断当前用户在不在数组approvalPerson里
                node=“3”  判断当前用户在不在数组recheckPerson里*/
        Boolean editFlag = Boolean.FALSE;
        Boolean turnFlag = Boolean.FALSE;
        if (UnifyStatus.ONGOING.getCode().equals(m.getSubStatus())) {
           List<String> processUser  = user.get(m.getFlowNodeNo()).stream().map(PersonDTO::getUserId).collect(Collectors.toList());
            switch (m.getFlowNodeNo()) {
                case "1":
                    if (m.getGroupItem().equals(1L)) {
                        if (processUser.contains(userId)) {
                            editFlag = Boolean.TRUE;
                            turnFlag = Boolean.TRUE;
                        }
                    } else {
                        if (userId.equals(m.getHandleUserId())) {
                            editFlag = Boolean.TRUE;
                            turnFlag = Boolean.TRUE;
                        }
                    }

                   /* if (userId.equals(m.getTaskCreateUserId())) {
                        turnFlag = Boolean.TRUE;
                    }*/
                    break;
                case "2":
                case "3":
                case "4":
                case "5":
                case "6":
                    if (processUser.contains(userId)) {
                        editFlag = Boolean.TRUE;
                        turnFlag = Boolean.TRUE;
                    }
                    /*if (userId.equals(m.getTaskCreateUserId())) {
                        turnFlag = Boolean.TRUE;
                    }*/
                    break;
                default:
                    break;
            }
        }
        m.setEditFlag(editFlag);
        m.setTurnFlag(turnFlag);
    }

    /**
     * 查询全部子任务专属分组方法
     * 遇到当前用户为某个节点处理人时，优先选择属于当前处理人的任务
     * @param oldList
     * @return
     */
    private List<TaskSubVO> allSubGroup(List<TaskSubVO> oldList, String userId) {
        if (CollectionUtils.isEmpty(oldList)) {
            return new ArrayList<>();
        }
        List<TaskSubVO> newList = Lists.newArrayList();
        Map<String, List<TaskSubVO>> collect = oldList.stream().collect(Collectors.groupingBy(e ->
                StringUtils.join(e.getUnifyTaskId(), Constants.MOSAICS, e.getStoreId(), Constants.MOSAICS, e.getLoopCount())));
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
            TaskSubVO subVO = mapValue.get(choice);
            //TODO jjx 目前只有或签
            if(UnifyStatus.ONGOING.getCode().equals(subVO.getSubStatus())){
                List<TaskSubVO> sameSubList = mapValue.stream().filter(s -> s.getGroupSign().equals(
                    StringUtils.join(s.getUnifyTaskId(), '#', s.getStoreId(), '#', s.getGroupItem(), '#',
                        s.getLoopCount(), '#', s.getFlowNodeNo()))
                        && UnifyStatus.ONGOING.getCode().equals(s.getSubStatus())).collect(Collectors.toList());
                List<TaskSubVO> selfSub = ListUtils.emptyIfNull(sameSubList).stream().filter(s->userId.equals(s.getHandleUserId())).collect(Collectors.toList());
                if(CollectionUtils.isNotEmpty(selfSub)){
                    newList.add(selfSub.get(0));
                }else{
                    newList.add(subVO);
                }
            }else{
                newList.add(subVO);
            }
        }
        if(CollectionUtils.isNotEmpty(newList)){
            newList = newList.stream().sorted(Comparator.comparing(TaskSubVO::getCreateTime).reversed())
                    .collect(Collectors.toList());
        }
        return newList;
    }

    @Override
    public List<TaskSubVO> dealAllSubGroup(List<TaskSubVO> oldList) {
        if (CollectionUtils.isEmpty(oldList)) {
            return new ArrayList<>();
        }
        List<TaskSubVO> newList = Lists.newArrayList();
        Map<String, List<TaskSubVO>> collect = oldList.stream().collect(Collectors.groupingBy(e -> StringUtils.join(e.getUnifyTaskId(), Constants.MOSAICS, e.getStoreId())));
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

    /**
     * 子任务统计信息
     * @param enterpriseId
     * @param taskId
     * @param storeList
     * @param isAdmin
     * @param userId
     * @return
     */
    private UnifySubStatisticsDTO getDisplaySubStatistics(String enterpriseId, Long taskId, Long loopCount, List<String> storeList, Boolean isAdmin, String userId) {
        Integer handle = taskSubMapper.selectDisplaySubStatistics(enterpriseId, taskId, userId, UnifyTaskQueryEnum.HANDLE.getCode(),loopCount,null);
        Integer approver = taskSubMapper.selectDisplaySubStatistics(enterpriseId, taskId, userId, UnifyTaskQueryEnum.APPROVER.getCode(),loopCount,null);
        Integer recheck = taskSubMapper.selectDisplaySubStatistics(enterpriseId, taskId, userId, UnifyTaskQueryEnum.RECHECK.getCode(),loopCount,null);
        Integer complete = taskSubMapper.selectDisplaySubStatistics(enterpriseId, taskId, null, UnifyTaskQueryEnum.COMPLETE.getCode(), loopCount,null);
        Integer all = taskSubMapper.selectDisplayAllSubStatistics(enterpriseId, taskId, loopCount, null);
        UnifySubStatisticsDTO result = new UnifySubStatisticsDTO(all, handle, approver, recheck, complete);
        return result;
    }

    /**
     * 分组获取人员
     *
     * @param enterpriseId
     * @param taskId
     * @param storeIds
     * @return
     */
    private Map<String, List<UnifyPersonDTO>> getTaskPerson(String enterpriseId, Long taskId, List<String> storeIds) {
        if (CollectionUtils.isEmpty(storeIds)) {
            return Maps.newHashMap();
        }
        List<UnifyPersonDTO> unifyPersonDTOS = unifyTaskStoreService.selectALLNodeUserInfoList(enterpriseId, Collections.singletonList(taskId), storeIds, null);
        Map<String, List<UnifyPersonDTO>> personMap = unifyPersonDTOS.stream()
                .collect(Collectors.groupingBy(e -> e.getStoreId() + Constants.MOSAICS + e.getLoopCount()));
        return personMap;
    }


    // 查看父任务是否逾期
    private Boolean getOverDueByTaskStore( List<TaskStoreDO> taskStoreDOListByLoopCount){
        Boolean overDue = false;
        for (TaskStoreDO m : taskStoreDOListByLoopCount) {
            if (m.getSubEndTime().getTime() < System.currentTimeMillis()) {
                if (!UnifyStatus.COMPLETE.getCode().equals(m.getSubStatus())) {
                    overDue = Boolean.TRUE;
                    break;
                } else if (m.getHandleTime().getTime() < m.getSubEndTime().getTime()) {
                    overDue = Boolean.TRUE;
                    break;
                } else {
                    overDue = Boolean.FALSE;
                }
            } else {
                overDue = Boolean.FALSE;
            }
        }
        return overDue;
    }

    private String getSubStatusByTaskStore( List<TaskStoreDO> taskStoreDOListByLoopCount){
        String subStatus = UnifyStatus.COMPLETE.getDesc();
        for (TaskStoreDO taskStoreDO : taskStoreDOListByLoopCount) {
            if (!UnifyStatus.COMPLETE.getCode().equals(taskStoreDO.getSubStatus())) {
                subStatus = UnifyStatus.ONGOING.getDesc();
                break;
            }
        }
        return subStatus;
    }

    private Integer checkLoopStatusNum(List<TaskStoreDO> taskStoreDOList , String status){
        Integer count = 1;
        for (TaskStoreDO taskStoreDO : taskStoreDOList) {
            if(UnifyStatus.ONGOING.getCode().equals(status) && !UnifyStatus.COMPLETE.getCode().equals(taskStoreDO.getSubStatus())){
                count = 1;
                break;
            }else if(!status.equals(taskStoreDO.getSubStatus())){
                count = 0;
                break;
            }
        }
        return  count;
    }

    private List<TaskProcessDTO> getNewProcess(List<TaskProcessDTO> process) {
        List<TaskProcessDTO> newProcess = Lists.newArrayList();
        Map<String, TaskProcessDTO> nodeUserListMap = Maps.newHashMap();
        process.forEach(proItem -> {
            String proNode = proItem.getNodeNo();
            List<GeneralDTO> proUserList = proItem.getUser();
            String approveType = proItem.getApproveType();
            TaskProcessDTO exsitProcess = nodeUserListMap.get(proNode);
            if (exsitProcess != null) {
                exsitProcess.getUser().addAll(proUserList);
            }else {
                exsitProcess = new TaskProcessDTO();
                exsitProcess.setUser(proUserList);
                exsitProcess.setNodeNo(proNode);
                exsitProcess.setApproveType(approveType);
                nodeUserListMap.put(proNode, exsitProcess);
            }
        });
        nodeUserListMap.forEach((node, userList) -> {
            newProcess.add(userList);
        });
        return  newProcess;
    }

    private void fillPersonName(List<GeneralDTO> generalDTOList, Map<Long, String> roleDOMap, Map<String, String> allNodeUserDOMap,
                                Map<String, String> regionNameMap, Map<String, String> groupNameMap) {
        generalDTOList.forEach(generalDTO -> {
            if(UnifyTaskConstant.PersonType.POSITION.equals(generalDTO.getType())){
                generalDTO.setName(roleDOMap.get(Long.parseLong(generalDTO.getValue())));
            }
            if(UnifyTaskConstant.PersonType.PERSON.equals(generalDTO.getType())){
                generalDTO.setName(allNodeUserDOMap.get(generalDTO.getValue()));
            }
            if(UnifyTaskConstant.PersonType.USER_GROUP.equals(generalDTO.getType())){
                generalDTO.setName(groupNameMap.get(generalDTO.getValue()));
            }
            if(UnifyTaskConstant.PersonType.ORGANIZATION.equals(generalDTO.getType())){
                generalDTO.setName(regionNameMap.get(generalDTO.getValue()));
            }
        });
    }


    private List<PersonDTO> getCollaboratorNameList(List<UnifyPersonDTO> unifyPersonDTOList, Map<String, String> allNodeUserDOMap) {
        List<PersonDTO> collaboratorList = new ArrayList<>();
        unifyPersonDTOList.forEach(unifyPersonDTO -> {
            PersonDTO personDTO = new PersonDTO();
            personDTO.setUserId(unifyPersonDTO.getUserId());
            personDTO.setUserName(allNodeUserDOMap.get(unifyPersonDTO.getUserId()));
            collaboratorList.add(personDTO);
        });
        return collaboratorList;
    }


    //  获得相关节点人员的名字
    private String getNodePersonName(List<GeneralDTO> personPositionList) {
        if(CollUtil.isEmpty(personPositionList)){
            return  "";
        }
        List<String> personNameList = Lists.newArrayList();
        List<String> positionNameList = Lists.newArrayList();
        for (GeneralDTO item : personPositionList) {
            switch (item.getType()) {
                case UnifyTaskConstant.PersonType.PERSON:
                    personNameList.add(item.getName());
                    break;
                case UnifyTaskConstant.PersonType.POSITION:
                    positionNameList.add(item.getName());
                    break;
                default:
                    break;
            }
        }
        String nodePersonName = "";
        String personNameStr = String.join(",", ListUtils.emptyIfNull(personNameList));
        String positionNameStr = String.join(",", ListUtils.emptyIfNull(positionNameList));
        if(StrUtil.isNotEmpty(personNameStr)){
            nodePersonName += "" + personNameStr + "、";
        }
        if(StrUtil.isNotEmpty(positionNameStr)){
            nodePersonName += "" + positionNameStr + "、";
        }
        if (nodePersonName.length() > 0) {
            nodePersonName = nodePersonName.substring(0, nodePersonName.length() - 1);
        }
        return nodePersonName;
    }


    /**
     * 处理门店采集任务的证照图片
     * @param taskSubVO TaskSubVO
     */
    private void handleStoreLicensePicture(String enterpriseId, TaskSubVO taskSubVO, String userId){
        // 只处理门店采集任务
        if(!TaskTypeEnum.PATROL_STORE_INFORMATION.getCode().equals(taskSubVO.getTaskType())) {
            return;
        }
        // taskData为空不处理
        if(StringUtils.isBlank(taskSubVO.getTaskData())) {
            return;
        }
        // storeLienseInstances为空不处理
        JSONObject taskDTO = JSONObject.parseObject(taskSubVO.getTaskData());
        if(Strings.isBlank(taskDTO.getString(LicenseFieldConstants.STORE_LICENSE_INSTANCES))) {
            return;
        }
        List<JSONObject> storeLicenses = JSONObject.parseArray(taskDTO.getString(LicenseFieldConstants.STORE_LICENSE_INSTANCES), JSONObject.class);
        // 组装证照类型id，用作rpc获取证照类型
        List<Long> typeIds = storeLicenses.stream().map(license -> license.getLong(LicenseFieldConstants.LICENSE_TYPE_ID)).collect(Collectors.toList());
        DataSourceHelper.reset();
        EnterpriseConfigDO configDO = enterpriseConfigDao.getEnterpriseConfig(enterpriseId);
        DataSourceHelper.changeToSpecificDataSource(configDO.getDbName());
        Map<Long, String> licenseWaterMarkMap = licenseTypeApiService.getStoreLicenseWaterMarkMap(configDO, typeIds);
        // 图片水印签名处理
        for (JSONObject storeLicenseInstance : storeLicenses) {
            String username = enterpriseUserDao.selectNameIgnoreActiveByUserId(enterpriseId, userId);
            String picture = storeLicenseInstance.getString(LicenseFieldConstants.PICTURE);
            String waterMark = licenseWaterMarkMap.get(storeLicenseInstance.getLong(LicenseFieldConstants.LICENSE_TYPE_ID)) + Constants.LINE + username;
            storeLicenseInstance.put(LicenseFieldConstants.PICTURE, ossClientService.generatePresignedUrls(picture, waterMark, null));
        }
        // 将处理好的证照列表放回到结果中
        taskDTO.put(LicenseFieldConstants.STORE_LICENSE_INSTANCES, storeLicenses);
        taskSubVO.setTaskData(JSONObject.toJSONString(taskDTO));
    }

}
