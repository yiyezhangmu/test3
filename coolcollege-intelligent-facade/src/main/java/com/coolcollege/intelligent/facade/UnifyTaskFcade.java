package com.coolcollege.intelligent.facade;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.constant.UnifyTaskConstant;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.enums.RegionTypeEnum;
import com.coolcollege.intelligent.common.enums.role.AuthRoleEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.util.DateUtils;
import com.coolcollege.intelligent.dao.authentication.UserAuthMappingMapper;
import com.coolcollege.intelligent.dao.region.RegionMapper;
import com.coolcollege.intelligent.dao.unifytask.dao.UnifyTaskPersonDao;
import com.coolcollege.intelligent.model.authentication.UserAuthMappingDO;
import com.coolcollege.intelligent.model.enums.TaskTypeEnum;
import com.coolcollege.intelligent.model.enums.UnifyTableEnum;
import com.coolcollege.intelligent.model.enums.UnifyTaskDataTypeEnum;
import com.coolcollege.intelligent.model.enums.UnifyTaskLoopDateEnum;
import com.coolcollege.intelligent.model.metatable.TbMetaTableDO;
import com.coolcollege.intelligent.model.region.RegionDO;
import com.coolcollege.intelligent.model.unifytask.TaskDataMappingDO;
import com.coolcollege.intelligent.model.unifytask.TaskMappingDO;
import com.coolcollege.intelligent.model.unifytask.dto.GeneralDTO;
import com.coolcollege.intelligent.model.unifytask.dto.TaskMessageDTO;
import com.coolcollege.intelligent.model.unifytask.dto.TaskProcessDTO;
import com.coolcollege.intelligent.model.unifytask.dto.UnifyTaskBuildDTO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.service.metatable.TbMetaTableService;
import com.coolcollege.intelligent.service.tbdisplay.TbMetaDisplayQuickColumnService;
import com.coolcollege.intelligent.service.unifytask.UnifyTaskDataMappingService;
import com.coolcollege.intelligent.service.unifytask.UnifyTaskService;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author wxp
 * @date 2021-02-22 10:34
 */
@Service
@Slf4j
public class UnifyTaskFcade {


    @Autowired
    private UnifyTaskDataMappingService unifyTaskDataMappingService;

    @Autowired
    @Lazy
    private UnifyTaskService unifyTaskService;

    @Autowired
    private TbMetaDisplayQuickColumnService tbMetaDisplayQuickColumnService;

    @Autowired
    private TbMetaTableService tbMetaTableService;

    @Resource
    private UserAuthMappingMapper userAuthMappingMapper;

    @Resource
    private RegionMapper  regionMapper;
    @Resource
    private RedisUtilPool redisUtilPool;

    public Long insertUnifyTask(String enterpriseId, UnifyTaskBuildDTO task, CurrentUser user, long createTime){
        checkUnifyTask(task);
        //taskDisplayStoreScopeList转换成List<GeneralDTO> storeIds
        changeStoreScope(enterpriseId,user,task);

        transTaskForm(enterpriseId, task, user);
        TaskMessageDTO messageDTO = unifyTaskService.insertUnifyTask(enterpriseId, task, user.getUserId(), createTime);
        // 创建任务时锁定
        if(TaskTypeEnum.TB_DISPLAY_TASK.getCode().equals(task.getTaskType())){
            List<Long> metaTableIds = getMetaTableIdsByForm(task.getForm());
            tbMetaTableService.updateLockedByIds(enterpriseId, metaTableIds);
        }
        return messageDTO.getUnifyTaskId();
    }

    private void changeStoreScope(String eid,CurrentUser user,UnifyTaskBuildDTO task) {
        List<GeneralDTO> storeIds = task.getStoreIds();
        List<GeneralDTO> taskDisplayStoreScopeList = task.getTaskDisplayStoreScopeList();
        if(CollectionUtils.isEmpty(storeIds)&&CollectionUtils.isEmpty(taskDisplayStoreScopeList)){
            throw new ServiceException(ErrorCodeEnum.UNIFY_TASK_STORE_SCOPE_NOT_NULL);
        }
        task.setRegionModel(false);
        if(CollectionUtils.isNotEmpty(taskDisplayStoreScopeList)){
            task.setRegionModel(true);
            List<GeneralDTO> allStoreScopeList=new ArrayList<>();
            if(AuthRoleEnum.ALL.getCode().equals(user.getRoleAuth())){
                task.setStoreIds(taskDisplayStoreScopeList);
                return;
            }
            //将用户输入的区域转换成有权限的区域和门店，输入的门店分组不转换
            List<String> userRegionIdList = taskDisplayStoreScopeList.stream()
                    .filter(data -> StringUtils.equals(data.getType(), UnifyTaskConstant.StoreType.REGION))
                    .map(GeneralDTO::getValue)
                    .collect(Collectors.toList());
            List<String> authRegionIdList = userAuthMappingMapper.getMappingUserAuthMappingByUserId(eid, user.getUserId());
            if(CollectionUtils.isEmpty(authRegionIdList)){
                throw new ServiceException(ErrorCodeEnum.UNIFY_TASK_STORE_SCOPE_NOT_AUTH);
            }
            List<RegionDO> authRegionList = regionMapper.getRegionByRegionIds(eid, authRegionIdList);
            Map<String, RegionDO> authRegionMap = ListUtils.emptyIfNull(authRegionList)
                    .stream()
                    .filter(data->!StringUtils.equals(data.getRegionType(), RegionTypeEnum.STORE.getType()))
                    .collect(Collectors.toMap(data->data.getId().toString(), data -> data, (a, b) -> a));
            List<RegionDO> userRegionList=new ArrayList<>();
            if(CollectionUtils.isNotEmpty(userRegionIdList)){
                userRegionList = regionMapper.getRegionByRegionIds(eid, userRegionIdList);
            }
            Map<String, RegionDO> userRegionMap = ListUtils.emptyIfNull(userRegionList)
                    .stream()
                    .collect(Collectors.toMap(data->data.getId().toString(), data -> data, (a, b) -> a));
            //对比用户权限和用户输入，两次对比
            // 1.以权限为主对比输入，权限区域父节点包含输入权限，则保留权限区域节点。
            // 2.以输入为主对比权限，输入区域父节点包含权限区域，则保留输入区域节点。
            List<GeneralDTO> authRegionScopeList = authRegionList.stream()
                    .filter(data -> {
                        List<String> regionIdList = StrUtil.splitTrim(data.getFullRegionPath(), "/");
                        return regionIdList.stream().anyMatch(regionId -> userRegionMap.get(regionId) != null);
                    })
                    .map(this::mapGeneralDTO)
                    .collect(Collectors.toList());

            if(CollectionUtils.isNotEmpty(authRegionScopeList)){
                List<String> auRegionList = authRegionScopeList.stream()
                        .map(GeneralDTO::getValue)
                        .collect(Collectors.toList());
                //将权限区域转换
                List<RegionDO> authRegionDOList = regionMapper.getRegionByRegionIds(eid, auRegionList);
                Map<String, RegionDO> auRegionMap = ListUtils.emptyIfNull(authRegionDOList)
                        .stream()
                        .collect(Collectors.toMap(RegionDO::getRegionId, data -> data, (a, b) -> a));
                List<GeneralDTO> generalAuthRegionScopeList= authRegionScopeList.stream()
                        .map(data -> {
                            if (StringUtils.equals(auRegionMap.get(data.getValue()).getRegionType(), RegionTypeEnum.STORE.getType())) {
                                data.setType(UnifyTaskConstant.StoreType.STORE);
                                data.setValue(auRegionMap.get(data.getValue()).getStoreId());
                            } else {
                                data.setType(UnifyTaskConstant.StoreType.REGION);
                            }
                            return data;
                        }).collect(Collectors.toList());

                allStoreScopeList.addAll(generalAuthRegionScopeList);
            }

            List<GeneralDTO> userRegionScopeList = userRegionList.stream()
                    .filter(data -> {
                        List<String> regionIdList = StrUtil.splitTrim(data.getFullRegionPath(), "/");
                        return regionIdList.stream().anyMatch(regionId -> authRegionMap.get(regionId) != null);
                    })
                    .map(this::mapGeneralDTO)
                    .collect(Collectors.toList());
            if(CollectionUtils.isNotEmpty(userRegionScopeList)){
                allStoreScopeList.addAll(userRegionScopeList);
            }
            List<GeneralDTO> storeOrGroupScopeList = taskDisplayStoreScopeList.stream()
                    .filter(data -> !StringUtils.equals(data.getType(), UnifyTaskConstant.StoreType.REGION))
                    .collect(Collectors.toList());
            if(CollectionUtils.isNotEmpty(storeOrGroupScopeList)){
                allStoreScopeList.addAll(storeOrGroupScopeList);
            }
            if(CollectionUtils.isEmpty(allStoreScopeList)){
                throw new ServiceException(ErrorCodeEnum.UNIFY_TASK_STORE_SCOPE_NOT_AUTH);
            }
            //区域id去重
            List<GeneralDTO> collect = new ArrayList<>(allStoreScopeList.stream()
                    .collect(Collectors.toMap(GeneralDTO::getValue, data -> data, (a, b) -> a))
                    .values());
            task.setStoreIds(collect);
        }
    }

    private GeneralDTO mapGeneralDTO(RegionDO data) {
        GeneralDTO generalDTO = new GeneralDTO();
        generalDTO.setValue(data.getId().toString());
        generalDTO.setName(data.getName());
        generalDTO.setType(UnifyTaskConstant.StoreType.REGION);
        return generalDTO;
    }

    public  void schedulerTask(String enterpriseId,Long taskId, String dbName,String isOperateOverdue, boolean isRefresh){
        unifyTaskService.taskParentResolve(enterpriseId, taskId, dbName, false, isRefresh);
    }

    private  UnifyTaskBuildDTO transTaskForm(String enterpriseId, UnifyTaskBuildDTO task, CurrentUser user){
        List<GeneralDTO> formList = task.getForm();
        List<TaskDataMappingDO> mappingDOList = Lists.newArrayList();
        if(CollUtil.isEmpty(formList)){
            return task;
        }
        String dataFormType = formList.get(0).getType();
        // 根据快捷陈列检查项 创建临时检查表
        if(UnifyTaskDataTypeEnum.TB_DISPLAY_QUICK_COLUMN.getCode().equals(dataFormType)){
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
        return  task;
    }


    public List<Long> getMetaTableIdsByForm(List<GeneralDTO> formList) {
        List<Long>  metaTableIds = Lists.newArrayList();
        if(CollUtil.isNotEmpty(formList)){
            for (GeneralDTO generalDTO : formList) {
                metaTableIds.add(Long.parseLong(generalDTO.getValue()));
            }
            return metaTableIds;
        }
        return metaTableIds;
    }

    /**
     * 检查任务
     * @param task
     */
    private void checkUnifyTask(UnifyTaskBuildDTO task) {
        if (UnifyTaskLoopDateEnum.QUARTER.getCode().equals(task.getTaskCycle())) {
            Long runDate = DateUtils.convertStringToLong(task.getRunDate()+ " " + task.getCalendarTime() + ":00");
            //任务不在有效期
            if (!(runDate >= task.getBeginTime() && runDate <= task.getEndTime())) {
                throw new ServiceException(ErrorCodeEnum.UNIFY_TASK_TASKCYCLE_RUNDATE__VALID);
            }
        }
        //新增指派人员
        if(CollectionUtils.isNotEmpty(task.getAddProcessList())){
            task.getProcess().addAll(task.getAddProcessList());
        }
        //新增指派门店范围
        if(CollectionUtils.isNotEmpty(task.getAddStoreList())){
            //门店映射--按区域、分组、门店三种格式存
            task.getStoreIds().addAll(task.getAddStoreList());
        }

    }

}
