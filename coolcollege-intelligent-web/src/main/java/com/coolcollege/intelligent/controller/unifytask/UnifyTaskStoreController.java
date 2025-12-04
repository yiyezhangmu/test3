package com.coolcollege.intelligent.controller.unifytask;

import com.coolcollege.intelligent.common.annotation.ErrorHelper;
import com.coolcollege.intelligent.common.annotation.SysLog;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.constant.UnifyTaskConstant;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.enums.baili.BailiEnterpriseEnum;
import com.coolcollege.intelligent.common.enums.syslog.OpModuleEnum;
import com.coolcollege.intelligent.common.enums.syslog.OpTypeEnum;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.dao.enterprise.dao.EnterpriseConfigDao;
import com.coolcollege.intelligent.model.common.IdListDTO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.enterprise.dto.PersonDTO;
import com.coolcollege.intelligent.model.enums.UnifyNodeEnum;
import com.coolcollege.intelligent.model.metatable.dto.StoreMetaTableDTO;
import com.coolcollege.intelligent.model.metatable.vo.TaskStoreMetaDataVO;
import com.coolcollege.intelligent.model.unifytask.dto.CombineTaskStoreDTO;
import com.coolcollege.intelligent.model.unifytask.dto.PostponeTaskDTO;
import com.coolcollege.intelligent.model.unifytask.query.TaskStoreLoopQuery;
import com.coolcollege.intelligent.model.unifytask.query.TaskStoreQuery;
import com.coolcollege.intelligent.model.unifytask.request.StoreTaskClearRequest;
import com.coolcollege.intelligent.model.unifytask.vo.TaskPersonVO;
import com.coolcollege.intelligent.model.unifytask.vo.TaskStoreClearVO;
import com.coolcollege.intelligent.model.unifytask.vo.TaskSubVO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.model.userholder.UserHolder;
import com.coolcollege.intelligent.service.enterprise.EnterpriseStoreCheckSettingService;
import com.coolcollege.intelligent.service.unifytask.UnifyTaskStoreService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author byd
 * @date 2021-02-22 10:36
 */
@Api("门店任务接口")
@RestController
@RequestMapping("/v3/enterprises/{enterprise-id}/taskStore")
@ErrorHelper
@Slf4j
public class UnifyTaskStoreController {

    @Autowired
    private UnifyTaskStoreService unifyTaskStoreService;

    @Autowired
    private EnterpriseStoreCheckSettingService enterpriseStoreCheckSettingService;

    @Autowired
    private EnterpriseConfigDao enterpriseConfigDao;


    /**
     * 门店日期列表
     *
     * @param enterpriseId
     * @param query
     * @return
     */
    @PostMapping(path = "/clear/list")
    public ResponseResult storeClearList(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                         @RequestBody @Validated TaskStoreQuery query) {
        DataSourceHelper.changeToMy();
        query.setDbName(UserHolder.getUser().getDbName());
        return ResponseResult.success(unifyTaskStoreService.selectStoreClearList(enterpriseId, query));
    }

    @PostMapping(path = "/clear/list/new")
    public ResponseResult<List<TaskStoreClearVO>> storeClearListNew(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                                                     @RequestBody @Validated StoreTaskClearRequest query) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(unifyTaskStoreService.getStoreClearListNew(enterpriseId,UserHolder.getUser().getDbName(), query));
    }


    /**
     * 查询日清跳转的一条记录
     *
     * @param enterpriseId
     * @param taskStoreId
     * @return
     */
    @GetMapping(path = "/clear/jumpDetail")
    public ResponseResult<TaskSubVO> jumpDetail(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                                @RequestParam(value = "taskStoreId") Long taskStoreId) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(unifyTaskStoreService.jumpDetail(enterpriseId, taskStoreId, UserHolder.getUser()));
    }

    /**
     * 门店任务列表
     *
     * @param enterpriseId
     * @param query
     * @return
     */
    @PostMapping(path = "/store/list")
    public ResponseResult list(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                               @RequestBody @Validated TaskStoreLoopQuery query) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(unifyTaskStoreService.taskStoreList(enterpriseId, query));
    }

    /**
     * 门店任务数量
     *
     * @param enterpriseId
     * @param query
     * @return
     */
    @PostMapping(path = "/store/listCount")
    public ResponseResult listCount(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                    @RequestBody @Validated TaskStoreLoopQuery query) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(unifyTaskStoreService.taskStoreListCount(enterpriseId, query));
    }

    @GetMapping(path = "/stage/list")
    public ResponseResult stageList(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                    @Param("unifyTaskId") Long unifyTaskId, @Param("subStatus") String status) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(new PageInfo<>(unifyTaskStoreService.taskStageList(enterpriseId, unifyTaskId, status)));
    }

    @GetMapping(path = "/stage/listCount")
    public ResponseResult stageListCount(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                         @Param("unifyTaskId") Long unifyTaskId) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(unifyTaskStoreService.taskStageListCount(enterpriseId, unifyTaskId));
    }


    /**
     * 陈列门店任务列表
     *
     * @param enterpriseId
     * @param query
     * @return
     */
    @ApiOperation(value = "陈列门店任务列表", notes = "1.nodeType:我创建的create,我收到的/抄送我的cc,(新)我处理的approval")
    @PostMapping(path = "/display/storeTask/list")
    public ResponseResult displayStoreTaskList(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                               @RequestBody @Validated TaskStoreLoopQuery query) {
        DataSourceHelper.changeToMy();
        if (UnifyNodeEnum.CC.getCode().equals(query.getNodeType())) {
            query.setCcUserId(UserHolder.getUser().getUserId());
        }
        if(UnifyTaskConstant.ROLE_APPROVAL.equals(query.getNodeType())) {
            query.setUserId(UserHolder.getUser().getUserId());
        }
        if (BailiEnterpriseEnum.bailiAffiliatedCompany(enterpriseId) ||
                "e17cd2dc350541df8a8b0af9bd27f77d".equals(enterpriseId) || "140e9bf7acf445a08864d1afcc1814fa".equals(enterpriseId)) {
            return ResponseResult.success(unifyTaskStoreService.displayStoreTaskNewList(enterpriseId, query));
        }
        return ResponseResult.success(unifyTaskStoreService.displayStoreTaskList(enterpriseId, query));
    }


    /**
     * 陈列门店任务列表查询db
     *
     * @param enterpriseId
     * @param query
     * @return
     */
    @ApiOperation(value = "陈列门店任务列表db", notes = "1.nodeType:我创建的create,我收到的/抄送我的cc,(新)我处理的approval")
    @PostMapping(path = "/display/storeTask/newList")
    public ResponseResult displayStoreTaskNewList(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                               @RequestBody @Validated TaskStoreLoopQuery query) {
        DataSourceHelper.changeToMy();
        if (UnifyNodeEnum.CC.getCode().equals(query.getNodeType())) {
            query.setCcUserId(UserHolder.getUser().getUserId());
        }
        if(UnifyTaskConstant.ROLE_APPROVAL.equals(query.getNodeType())) {
            query.setUserId(UserHolder.getUser().getUserId());
        }
        return ResponseResult.success(unifyTaskStoreService.displayStoreTaskNewList(enterpriseId, query));
    }


    /**
     * 门店最新数据
     * @param enterpriseId
     * @return
     */
    @PostMapping(path = "/latestData")
    public ResponseResult taskStoreQuestionStatistics(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                                      @RequestBody @Validated StoreMetaTableDTO query) {
        CurrentUser user = UserHolder.getUser();
        if (query.getMetaTables().size() > Constants.MAX_META_TABLE_NUM) {
            return ResponseResult.fail(ErrorCodeEnum.OUT_OF_MAX_TABLE_NUM);
        }
        List<TaskStoreMetaDataVO> result = unifyTaskStoreService.getStoreMetaTableData(enterpriseId, query.getStoreId(), query.getMetaTables(), user);
        return ResponseResult.success(result);
    }

    @ApiOperation("门店任务-查询当前任务节点的处理人")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "taskStoreId", value = "门店任务id", required = true),
            @ApiImplicitParam(name = "nodeNo", value = "任务节点：1待处理，2待审批", required = true)
    })
    @GetMapping(path = "/getCurrentNodePerson")
    public ResponseResult<List<PersonDTO>> getCurrentNodePerson(@PathVariable("enterprise-id") String enterpriseId,
                                                                @RequestParam(required = true) Long taskStoreId,
                                                                @RequestParam(required = true) String nodeNo) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(unifyTaskStoreService.getCurrentNodePerson(enterpriseId, taskStoreId, nodeNo));
    }

    @ApiOperation("门店任务-查询处理人和审批人用作重新分配")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "taskStoreId", value = "门店任务id", required = true)
    })
    @GetMapping(path = "/getNodePersonForReallocate")
    public ResponseResult<TaskPersonVO> getNodePersonForReallocate(@PathVariable("enterprise-id") String enterpriseId,
                                                             @RequestParam(required = true) Long taskStoreId) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(unifyTaskStoreService.getNodePersonForReallocate(enterpriseId, taskStoreId));
    }

    /**
     * 合并通知任务列表
     *
     * @param enterpriseId
     * @param query
     * @return
     */
    @ApiOperation(value = "合并通知任务列表", notes = "合并通知任务列表")
    @PostMapping(path = "/combineTaskList")
    public ResponseResult<CombineTaskStoreDTO> combineTaskList(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                                               @RequestBody @Validated TaskStoreLoopQuery query) {
        DataSourceHelper.changeToMy();
        query.setHandleUserId(UserHolder.getUser().getUserId());
        // nodeNo用作筛选，前端交互上只有1、2、endNo
        if(UnifyNodeEnum.SECOND_NODE.getCode().equals(query.getNodeNo())){
            query.setNodeNo(null);
            query.setApproveAll(true);
        }
        // 解决审批人查询不到的问题
        // 前端nodeNo有其他判断，因此这里额外使用一个字段来判断当前是哪个节点
        if (UnifyNodeEnum.isApproveNode(query.getNodeStr())) {
            // nodeNo=2时，筛选所有待审批节点，否则筛选当前节点
            if(StringUtils.isBlank(query.getNodeNo())){
                query.setNodeNo(query.getNodeStr());
                query.setHandleUserId(null);
                query.setUserId(UserHolder.getUser().getUserId());
            } else if (UnifyNodeEnum.FIRST_NODE.getCode().equals(query.getNodeNo())) {
                query.setHandleUserId(UserHolder.getUser().getUserId());
            } else {
                query.setHandleUserId(null);
                query.setUserId(UserHolder.getUser().getUserId());
            }
        }
        return ResponseResult.success(unifyTaskStoreService.combineTaskList(enterpriseId, query));
    }

    @ApiOperation("门店任务-批量停止任务")
    @PostMapping(path = "/store/batchStopTask")
    @SysLog(func = "停止任务", opModule = OpModuleEnum.UNIFY_TASK, opType = OpTypeEnum.EDIT)
    public ResponseResult batchStopTask(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                    @RequestBody @Validated IdListDTO idListDTO) {
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfig = enterpriseConfigDao.getEnterpriseConfig(enterpriseId);
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());
        unifyTaskStoreService.batchStopTask(enterpriseId, idListDTO, enterpriseConfig, UserHolder.getUser());
        return ResponseResult.success();
    }

    @ApiOperation("门店任务-批量延期任务")
    @PostMapping(path = "/store/batchPostponeTask")
    @SysLog(func = "延期任务", opModule = OpModuleEnum.UNIFY_TASK, opType = OpTypeEnum.EDIT)
    public ResponseResult batchPostponeTask(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                        @RequestBody @Validated PostponeTaskDTO postponeTaskDTO) {
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfig = enterpriseConfigDao.getEnterpriseConfig(enterpriseId);
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());
        unifyTaskStoreService.batchPostponeTask(enterpriseId, postponeTaskDTO, enterpriseConfig, UserHolder.getUser());
        return ResponseResult.success();
    }
}
