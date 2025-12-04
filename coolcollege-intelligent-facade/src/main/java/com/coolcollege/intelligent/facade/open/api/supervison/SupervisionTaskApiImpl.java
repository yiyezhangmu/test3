package com.coolcollege.intelligent.facade.open.api.supervison;

import com.alibaba.fastjson.JSONObject;
import com.alipay.sofa.runtime.api.annotation.SofaService;
import com.alipay.sofa.runtime.api.annotation.SofaServiceBinding;
import com.coolcollege.intelligent.common.enums.supervison.SupervisionTaskPriorityEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.constants.ConfigConstants;
import com.coolcollege.intelligent.dao.enterprise.dao.EnterpriseConfigDao;
import com.coolcollege.intelligent.facade.dto.openApi.*;
import com.coolcollege.intelligent.facade.dto.openApi.vo.TaskSopDTO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.enums.SupervisionParentStatusEnum;
import com.coolcollege.intelligent.model.enums.SupervisionSubTaskStatusEnum;
import com.coolcollege.intelligent.model.rpc.RpcLocalHolder;
import com.coolcollege.intelligent.model.sop.vo.TaskSopListVO;
import com.coolcollege.intelligent.model.sop.vo.TaskSopVO;
import com.coolcollege.intelligent.model.supervision.request.AddSupervisionTaskParentRequest;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.service.enterpriseUserGroup.EnterpriseUserGroupService;
import com.coolcollege.intelligent.service.store.StoreService;
import com.coolcollege.intelligent.service.supervison.SupervisionStoreTaskService;
import com.coolcollege.intelligent.service.supervison.SupervisionTaskParentService;
import com.coolcollege.intelligent.service.supervison.SupervisionTaskService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.coolstore.base.response.rpc.OpenApiResponseVO;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.shenyu.client.sofa.common.annotation.ShenyuSofaClient;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Author wxp
 * @Date 2023/2/7 17:03
 * @Version 1.0
 */
@Slf4j
@ConditionalOnProperty(name = "shenyu.register.registerType")
@SofaService(interfaceType = SupervisionTaskApi.class,bindings = {@SofaServiceBinding(bindingType = ConfigConstants.SOFA_BINDING_TYPE)})
@Service
public class SupervisionTaskApiImpl implements SupervisionTaskApi{

    @Resource
    private EnterpriseConfigDao enterpriseConfigDao;
    @Resource
    SupervisionTaskService supervisionTaskService;
    @Resource
    SupervisionTaskParentService supervisionTaskParentService;
    @Resource
    @Lazy
    SupervisionStoreTaskService supervisionStoreTaskService;
    @Resource
    StoreService storeService;
    @Resource
    EnterpriseUserGroupService enterpriseUserGroupService;


    @Override
    @ShenyuSofaClient(path = "/supervision/batchUpdate")
    public OpenApiResponseVO batchUpdateSupervisionTaskStatus(OpenApiUpdateSupervisionTaskDTO dto) {
        String enterpriseId = RpcLocalHolder.getEnterpriseId();
        try {
            EnterpriseConfigDO enterpriseConfig = enterpriseConfigDao.getEnterpriseConfig(enterpriseId);
            DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());
            return OpenApiResponseVO.success(supervisionTaskService.batchUpdateSupervisionTaskStatus(enterpriseId,dto));
        }catch (ServiceException e){
            return OpenApiResponseVO.fail(e.getErrorCode(),e.getErrorMessage());
        } catch (Exception e) {
            log.error("openApi#supervision/batchUpdate,Exception", e);
            return OpenApiResponseVO.fail();
        }
    }

    @Override
    @ShenyuSofaClient(path = "/supervision/addSupervisionTaskParent")
    public OpenApiResponseVO addSupervisionTaskParent(JSONObject jsonObject) {
        String enterpriseId = RpcLocalHolder.getEnterpriseId();
        try {
            EnterpriseConfigDO enterpriseConfig = enterpriseConfigDao.getEnterpriseConfig(enterpriseId);
            CurrentUser currentUser = new CurrentUser();
            AddSupervisionTaskParentDTO dto = JSONObject.parseObject(JSONObject.toJSONString(jsonObject), AddSupervisionTaskParentDTO.class);
            currentUser.setUserId(dto.getCreateUserId());
            AddSupervisionTaskParentRequest request = convertAddSupervisionTaskParent(dto);
            DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());
            return OpenApiResponseVO.success(supervisionTaskParentService.addSupervisionTaskParent(enterpriseId,currentUser,request));
        }catch (ServiceException e){
            return OpenApiResponseVO.fail(e.getErrorCode(),e.getErrorMessage());
        } catch (Exception e) {
            log.error("openApi#supervision/addSupervisionTaskParent,Exception", e);
            return OpenApiResponseVO.fail();
        }
    }

    @Override
    @ShenyuSofaClient(path = "/supervision/getSupervisionTaskParentList")
    public OpenApiResponseVO getSupervisionTaskParentList(JSONObject jsonObject) {
        String enterpriseId = RpcLocalHolder.getEnterpriseId();
        try {
            EnterpriseConfigDO enterpriseConfig = enterpriseConfigDao.getEnterpriseConfig(enterpriseId);
            DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());
            SupervisionParentListDTO dto = JSONObject.parseObject(JSONObject.toJSONString(jsonObject), SupervisionParentListDTO.class);
            return OpenApiResponseVO.success(supervisionTaskParentService.getSupervisionTaskParentList(enterpriseId,dto.getTaskName(),dto.getStartTime(),dto.getEndTime(),SupervisionParentStatusEnum.getSupervisionParentStatusEnumList(dto.getStatusList()),dto.getPageSize(),dto.getPageNum(),
                    SupervisionTaskPriorityEnum.getSupervisionTaskPriorityEnumList(dto.getPriorityList()),dto.getTaskGroupingList(),dto.getTags()));
        }catch (ServiceException e){
            return OpenApiResponseVO.fail(e.getErrorCode(),e.getErrorMessage());
        } catch (Exception e) {
            log.error("openApi#supervision/getSupervisionTaskParentList,Exception", e);
            return OpenApiResponseVO.fail();
        }
    }

    @Override
    @ShenyuSofaClient(path = "/supervision/listSupervisionTaskByParentId")
    public OpenApiResponseVO listSupervisionTaskByParentId(JSONObject jsonObject) {
        String enterpriseId = RpcLocalHolder.getEnterpriseId();
        try {
            EnterpriseConfigDO enterpriseConfig = enterpriseConfigDao.getEnterpriseConfig(enterpriseId);
            DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());
            SupervisionTaskListDTO dto = JSONObject.parseObject(JSONObject.toJSONString(jsonObject), SupervisionTaskListDTO.class);
            return OpenApiResponseVO.success(supervisionTaskParentService.listSupervisionTaskByParentId(enterpriseId,dto.getParentId(),null,SupervisionSubTaskStatusEnum.getSupervisionSubTaskStatusEnumList(dto.getCompleteStatusList()) ,dto.getPageSize(),dto.getPageNum(),dto.getHandleOverTimeStatus()));
        }catch (ServiceException e){
            return OpenApiResponseVO.fail(e.getErrorCode(),e.getErrorMessage());
        } catch (Exception e) {
            log.error("openApi#supervision/listSupervisionTaskByParentId,Exception", e);
            return OpenApiResponseVO.fail();
        }
    }

    @Override
    @ShenyuSofaClient(path = "/supervision/listSupervisionStoreTaskByParentId")
    public OpenApiResponseVO listSupervisionStoreTaskByParentId(JSONObject jsonObject) {
        String enterpriseId = RpcLocalHolder.getEnterpriseId();
        try {
            EnterpriseConfigDO enterpriseConfig = enterpriseConfigDao.getEnterpriseConfig(enterpriseId);
            DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());
            SupervisionTaskListDTO dto = JSONObject.parseObject(JSONObject.toJSONString(jsonObject), SupervisionTaskListDTO.class);
            return OpenApiResponseVO.success(supervisionTaskParentService.listSupervisionStoreTaskByParentId(enterpriseId,null,dto.getSupervisionTaskId(),null,null,null,SupervisionSubTaskStatusEnum.getSupervisionSubTaskStatusEnumList(dto.getCompleteStatusList()),dto.getPageSize(),dto.getPageNum(),dto.getHandleOverTimeStatus()));
        }catch (ServiceException e){
            return OpenApiResponseVO.fail(e.getErrorCode(),e.getErrorMessage());
        } catch (Exception e) {
            log.error("openApi#supervision/listSupervisionTaskByParentId,Exception", e);
            return OpenApiResponseVO.fail();
        }
    }
    @Override
    @ShenyuSofaClient(path = "/supervision/batchUpdateSupervisionStoreTaskStatus")
    public OpenApiResponseVO batchUpdateSupervisionStoreTaskStatus(OpenApiUpdateSupervisionTaskDTO dto) {
        String enterpriseId = RpcLocalHolder.getEnterpriseId();
        try {
            EnterpriseConfigDO enterpriseConfig = enterpriseConfigDao.getEnterpriseConfig(enterpriseId);
            DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());
            return OpenApiResponseVO.success(supervisionStoreTaskService.batchUpdateSupervisionStoreTaskStatus(enterpriseId,dto));
        }catch (ServiceException e){
            return OpenApiResponseVO.fail(e.getErrorCode(),e.getErrorMessage());
        } catch (Exception e) {
            log.error("openApi#supervision/batchUpdate,Exception", e);
            return OpenApiResponseVO.fail();
        }
    }
    @Override
    @ShenyuSofaClient(path = "/storeGroup/updateStoreGroup")
    public OpenApiResponseVO updateStoreGroup(JSONObject jsonObject) {
        String enterpriseId = RpcLocalHolder.getEnterpriseId();
        try {
            EnterpriseConfigDO enterpriseConfig = enterpriseConfigDao.getEnterpriseConfig(enterpriseId);
            DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());
            UpdateStoreGroupDTO updateStoreGroupDTO = JSONObject.parseObject(JSONObject.toJSONString(jsonObject), UpdateStoreGroupDTO.class);
            return OpenApiResponseVO.success(storeService.updateStoreGroupStoreList(enterpriseId,updateStoreGroupDTO.getGroupId(),updateStoreGroupDTO.getDingDeptIds()));
        }catch (ServiceException e){
            return OpenApiResponseVO.fail(e.getErrorCode(),e.getErrorMessage());
        } catch (Exception e) {
            log.error("openApi#supervision/batchUpdate,Exception", e);
            return OpenApiResponseVO.fail();
        }
    }

    @Override
    @ShenyuSofaClient(path = "/userGroup/updateUserGroup")
    public OpenApiResponseVO updateUserGroup(JSONObject jsonObject) {
        String enterpriseId = RpcLocalHolder.getEnterpriseId();
        try {
            EnterpriseConfigDO enterpriseConfig = enterpriseConfigDao.getEnterpriseConfig(enterpriseId);
            DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());
            UpdateUserGroupDTO updateUserGroupDTO = JSONObject.parseObject(JSONObject.toJSONString(jsonObject), UpdateUserGroupDTO.class);
            return OpenApiResponseVO.success(enterpriseUserGroupService.updateUserGroup(enterpriseId,updateUserGroupDTO.getGroupId(),updateUserGroupDTO.getUserIdList()));
        }catch (ServiceException e){
            return OpenApiResponseVO.fail(e.getErrorCode(),e.getErrorMessage());
        } catch (Exception e) {
            log.error("openApi#supervision/batchUpdate,Exception", e);
            return OpenApiResponseVO.fail();
        }
    }

    /**
     * DTO——>request
     * @param dto
     * @return
     */
    AddSupervisionTaskParentRequest convertAddSupervisionTaskParent(AddSupervisionTaskParentDTO dto){
        AddSupervisionTaskParentRequest addSupervisionTaskParentRequest = new AddSupervisionTaskParentRequest();
        addSupervisionTaskParentRequest.setId(dto.getId());
        addSupervisionTaskParentRequest.setBusinessId(dto.getBusinessId());
        addSupervisionTaskParentRequest.setBusinessType(dto.getBusinessType());
        addSupervisionTaskParentRequest.setCheckCode(dto.getCheckCode());
        addSupervisionTaskParentRequest.setTaskName(dto.getTaskName());
        addSupervisionTaskParentRequest.setTaskEndTime(dto.getTaskEndTime());
        addSupervisionTaskParentRequest.setTaskStartTime(dto.getTaskStartTime());
        addSupervisionTaskParentRequest.setDesc(dto.getDesc());
        addSupervisionTaskParentRequest.setRemark(dto.getRemark());
        addSupervisionTaskParentRequest.setCheckStoreIds(dto.getCheckStoreIds());
        addSupervisionTaskParentRequest.setExecutePersons(dto.getExecutePersons());
        addSupervisionTaskParentRequest.setFormId(dto.getFormId());
        addSupervisionTaskParentRequest.setPriority(dto.getPriority());
        addSupervisionTaskParentRequest.setTags(dto.getTags());
        addSupervisionTaskParentRequest.setHandleWay(dto.getHandleWay());
        List<TaskSopDTO> sopVOList = dto.getSopVOList();
        if (CollectionUtils.isNotEmpty(sopVOList)){
            addSupervisionTaskParentRequest.setTaskSopListVO(convertSop(sopVOList,dto.getCreateUserId()));
        }
        return addSupervisionTaskParentRequest;
    }


    /**
     *
     * @param sopVOList
     * @return
     */
    private TaskSopListVO convertSop(List<TaskSopDTO> sopVOList,String userId){
        TaskSopListVO taskSopListVO = new TaskSopListVO();
        List<TaskSopVO> list = new ArrayList<>();
        for (TaskSopDTO taskSopDTO:sopVOList) {
            TaskSopVO taskSopVO = new TaskSopVO();
            BeanUtils.copyProperties(taskSopDTO,taskSopVO);
            list.add(taskSopVO);
        }
        taskSopListVO.setSopList(list);
        taskSopListVO.setBusinessType("SUPERVISION");
        taskSopListVO.setUseRange("self");
        taskSopListVO.setUseUserids(userId);
        return taskSopListVO;
    }


}
