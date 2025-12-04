package com.coolcollege.intelligent.controller.store;

import com.coolcollege.intelligent.common.annotation.ErrorHelper;
import com.coolcollege.intelligent.common.constant.UnifyTaskConstant;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.dao.authentication.UserAuthMappingMapper;
import com.coolcollege.intelligent.dao.enterprise.dao.EnterpriseConfigDao;
import com.coolcollege.intelligent.dao.enterprise.dao.EnterpriseUserRoleDao;
import com.coolcollege.intelligent.dao.region.RegionMapper;
import com.coolcollege.intelligent.model.common.IdDTO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.enums.UnifyNodeEnum;
import com.coolcollege.intelligent.model.metatable.request.UpdateTableCreateUserRequest;
import com.coolcollege.intelligent.model.patrolstore.dto.StopTaskDTO;
import com.coolcollege.intelligent.model.store.dto.StoreOpenRuleBuildDTO;
import com.coolcollege.intelligent.model.store.dto.StoreOpenRuleDTO;
import com.coolcollege.intelligent.model.store.dto.UpdateCreateUserDTO;
import com.coolcollege.intelligent.model.unifytask.query.TaskStoreLoopQuery;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.model.userholder.UserHolder;
import com.coolcollege.intelligent.service.patrolstore.PatrolStoreService;
import com.coolcollege.intelligent.service.store.StoreOpenRuleService;
import com.coolcollege.intelligent.service.unifytask.UnifyTaskStoreService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * @author byd
 * @date 2023-05-12 14:19
 */
@Api(tags = "门店开业规则")
@RestController
@RequestMapping("/v3/enterprises/{enterprise-id}/storeOpenRule")
@ErrorHelper
@Slf4j
public class StoreOpenRuleController {

    @Resource
    private StoreOpenRuleService storeOpenRuleService;

    @Resource
    private PatrolStoreService patrolStoreService;

    @Resource
    private UnifyTaskStoreService unifyTaskStoreService;

    @Resource
    EnterpriseConfigDao enterpriseConfigDao;

    @Resource
    EnterpriseUserRoleDao enterpriseUserRoleDao;

    @Resource
    UserAuthMappingMapper userAuthMappingMapper;

    @Resource
    RegionMapper regionMapper;

    @ApiOperation("门店开业规则列表")
    @GetMapping("/list")
    public ResponseResult list(@PathVariable("enterprise-id") String enterpriseId,
                                                           @RequestParam(value = "createUserid", required = false) String createUserid,
                                                           @RequestParam(value = "pageNum", defaultValue = "1", required = false) Integer pageNum,
                                                           @RequestParam(value = "pageSize", defaultValue = "10", required = false) Integer pageSize,
                                                           @RequestParam(value = "regionId" ,required = false) String regionId,
                                                           @RequestParam(value = "newStoreTaskStatus" ,required = false)String newStoreTaskStatus,
                               @RequestParam(value = "ruleName" ,required = false)String ruleName) {
        DataSourceHelper.changeToMy();
        List<String> mappingId = getMappingId(enterpriseId,regionId);
        PageInfo<StoreOpenRuleDTO> list = storeOpenRuleService.list(enterpriseId, createUserid, pageNum, pageSize,
                regionId, newStoreTaskStatus,mappingId, ruleName);
        Map<String, Object> stringObjectMap = storeOpenRuleService.toMap(enterpriseId, list, regionId,mappingId);
        return ResponseResult.success(stringObjectMap);
    }

    private List<String> getMappingId(String enterpriseId,String regionId) {
        CurrentUser user = UserHolder.getUser();
        List<String> mappingId = null;
        Boolean isAdmin = enterpriseUserRoleDao.checkIsAdmin(enterpriseId, user.getUserId());
        if(isAdmin || "a100000001".equals(user.getUserId())){
            return mappingId;
        }
        if (StringUtils.isBlank(regionId)){
            mappingId = userAuthMappingMapper.getMappingIdsByUserId(enterpriseId, user.getUserId());
            List<String> oldMappingIds = mappingId;
            mappingId = mappingId.stream().map(item -> "/" + item + "/").collect(Collectors.toList());
            List<String> subIdsByRegionIds = regionMapper.getSubIdsByRegionpaths(enterpriseId, mappingId);
            oldMappingIds.addAll(subIdsByRegionIds);
            return oldMappingIds;
        }
        return null;
    }

    @PostMapping(path = "/store/list")
    public ResponseResult list(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                               @RequestBody @Validated TaskStoreLoopQuery query) {
        DataSourceHelper.changeToMy();
        if (query.getTaskType().equals("TB_DISPLAY_TASK")){
            DataSourceHelper.changeToMy();
            if (UnifyNodeEnum.CC.getCode().equals(query.getNodeType())) {
                query.setCcUserId(UserHolder.getUser().getUserId());
            }
            if(UnifyTaskConstant.ROLE_APPROVAL.equals(query.getNodeType())) {
                query.setUserId(UserHolder.getUser().getUserId());
            }
            return ResponseResult.success(unifyTaskStoreService.displayStoreTaskList(enterpriseId, query));
        }else{
            return ResponseResult.success(unifyTaskStoreService.taskStoreList(enterpriseId, query));
        }
    }

    @ApiOperation("门店开业规则-新增")
    @PostMapping("/addStoreOpenRule")
    public ResponseResult<StoreOpenRuleBuildDTO> addStoreOpenRule(@PathVariable("enterprise-id") String enterpriseId,
                                                                  @RequestBody StoreOpenRuleBuildDTO storeOpenRuleBuildDTO) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(storeOpenRuleService.addStoreOpenRule(enterpriseId, UserHolder.getUser(), storeOpenRuleBuildDTO));
    }

    @ApiOperation("门店开业规则-编辑")
    @PostMapping("/updateStoreOpenRule")
    public ResponseResult<StoreOpenRuleBuildDTO> updateStoreOpenRule(@PathVariable("enterprise-id") String enterpriseId,
                                                                     @RequestBody StoreOpenRuleBuildDTO storeOpenRuleBuildDTO) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(storeOpenRuleService.updateStoreOpenRule(enterpriseId, UserHolder.getUser(), storeOpenRuleBuildDTO));
    }

    @ApiOperation("门店开业规则-开启")
    @PostMapping("/enableStoreOpenRule")
    public ResponseResult<Boolean> enableStoreOpenRule(@PathVariable("enterprise-id") String enterpriseId,
                                                       @RequestBody IdDTO idDTO) {
        DataSourceHelper.changeToMy();
        storeOpenRuleService.enableStoreOpenRule(enterpriseId, idDTO.getId());
        return ResponseResult.success(true);
    }

    @ApiOperation("门店开业规则-停用")
    @PostMapping("/disableStoreOpenRule")
    public ResponseResult<Boolean> disableStoreOpenRule(@PathVariable("enterprise-id") String enterpriseId,
                                                        @RequestBody IdDTO idDTO) {
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfig = enterpriseConfigDao.getEnterpriseConfig(enterpriseId);
        DataSourceHelper.changeToMy();
        storeOpenRuleService.disableStoreOpenRule(enterpriseId, idDTO.getId());

        for (Long unifyTaskId : idDTO.getUnifyTaskIds()) {
            StopTaskDTO stopTaskDTO = new StopTaskDTO();
            stopTaskDTO.setParentTaskId(unifyTaskId);
            patrolStoreService.stopTask(enterpriseId, stopTaskDTO, enterpriseConfig);
        }

        return ResponseResult.success(true);
    }

    @ApiOperation("门店开业规则-删除")
    @PostMapping("/removeStoreOpenRule")
    public ResponseResult<Boolean> removeStoreOpenRule(@PathVariable("enterprise-id") String enterpriseId,
                                                       @RequestBody IdDTO idDTO) {
        DataSourceHelper.changeToMy();
        storeOpenRuleService.removeStoreOpenRule(enterpriseId, idDTO.getId());
        return ResponseResult.success(true);
    }

    @ApiOperation("门店开业规则详情")
    @GetMapping("/detail")
    public ResponseResult<StoreOpenRuleBuildDTO> detail(@PathVariable("enterprise-id") String enterpriseId,
                                                        @RequestParam("ruleId") Long ruleId) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(storeOpenRuleService.detail(enterpriseId, ruleId));
    }

    @ApiOperation(value = "更新创建人")
    @PostMapping("/updateStoreRuleCreateUser")
    public ResponseResult<Boolean> updateStoreRuleCreateUser(@PathVariable(value = "enterprise-id") String enterpriseId, @RequestBody @Valid UpdateCreateUserDTO createUserDTO) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(storeOpenRuleService.updateStoreRuleCreateUser(enterpriseId, createUserDTO));
    }

}
