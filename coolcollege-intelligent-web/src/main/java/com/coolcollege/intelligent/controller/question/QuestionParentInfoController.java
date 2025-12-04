package com.coolcollege.intelligent.controller.question;

import com.coolcollege.intelligent.common.annotation.ErrorHelper;
import com.coolcollege.intelligent.common.annotation.SysLog;
import com.coolcollege.intelligent.common.enums.baili.BailiEnterpriseEnum;
import com.coolcollege.intelligent.common.enums.syslog.OpModuleEnum;
import com.coolcollege.intelligent.common.enums.syslog.OpTypeEnum;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.common.sync.conf.Role;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseConfigMapper;
import com.coolcollege.intelligent.dao.question.TbQuestionParentInfoMapper;
import com.coolcollege.intelligent.facade.dto.PageDTO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.impoetexcel.ImportTaskDO;
import com.coolcollege.intelligent.model.question.TbQuestionParentInfoDO;
import com.coolcollege.intelligent.model.question.request.BuildQuestionRequest;
import com.coolcollege.intelligent.model.question.request.QuestionParentRequest;
import com.coolcollege.intelligent.model.question.request.QuestionRecordListRequest;
import com.coolcollege.intelligent.model.question.vo.SubQuestionRecordListVO;
import com.coolcollege.intelligent.model.question.vo.TbQuestionParentInfoDetailVO;
import com.coolcollege.intelligent.model.question.vo.TbQuestionParentInfoVO;
import com.coolcollege.intelligent.model.unifytask.vo.TaskParentQuestionVO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.model.userholder.UserHolder;
import com.coolcollege.intelligent.service.enterprise.impl.EnterpriseConfigServiceImpl;
import com.coolcollege.intelligent.service.question.QuestionParentInfoService;
import com.coolcollege.intelligent.service.question.QuestionRecordService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author byd
 * @date 2022-08-04 14:15
 */
@Api(tags = "工单管理")
@RestController
@RequestMapping("/v3/enterprises/{enterprise-id}/questionParentInfo")
@ErrorHelper
@Slf4j
public class QuestionParentInfoController {

    @Autowired
    private QuestionParentInfoService questionParentInfoService;

    @Autowired
    private QuestionRecordService questionRecordService;

    @Resource
    private EnterpriseConfigMapper configMapper;

    @Resource
    private TbQuestionParentInfoMapper tbQuestionParentInfoMapper;

    @Autowired
    private EnterpriseConfigServiceImpl enterpriseConfigService;


    @ApiOperation("工单管理-查询列表（分页）")
    @PostMapping("/list")
    public ResponseResult<PageInfo<TbQuestionParentInfoVO>> list(@PathVariable("enterprise-id") String enterpriseId,
                                                                 @RequestBody QuestionParentRequest questionParentRequest) {
        DataSourceHelper.changeToMy();
        //管理员可以查询全部，非管理员只能查询自己创建的
        Boolean isAdmin = Role.MASTER.getRoleEnum().equals(UserHolder.getUser().getSysRoleDO().getRoleEnum());
        if (BailiEnterpriseEnum.bailiAffiliatedCompany(enterpriseId)) {
            isAdmin = isAdmin || Role.SUB_MASTER.getRoleEnum().equals(UserHolder.getUser().getSysRoleDO().getRoleEnum());
        }
        if(!isAdmin){
            questionParentRequest.setCreateUserIdList(Collections.singletonList(UserHolder.getUser().getUserId()));
        }
        return ResponseResult.success(questionParentInfoService.questionList(enterpriseId, questionParentRequest));
    }

    @ApiOperation("工单管理-父工单详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "questionParentInfoId", value = "父工单id", required = true, dataType = "Long"),
    })
    @GetMapping("/detail")
    public ResponseResult<TbQuestionParentInfoDetailVO> detail(@PathVariable("enterprise-id") String enterpriseId,
                                                               @RequestParam(value = "questionParentInfoId") Long questionParentInfoId) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(questionParentInfoService.questionParentInfoDetail(enterpriseId, questionParentInfoId));
    }

    @ApiOperation("工单管理-详情-子工单列表(分页)")
    @GetMapping("/questionRecordList")
    public ResponseResult<PageDTO<SubQuestionRecordListVO>> questionRecordList(@PathVariable("enterprise-id") String enterpriseId,
                                                                               QuestionRecordListRequest questionRecordListRequest) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(questionRecordService.subQuestionRecordList(enterpriseId, questionRecordListRequest));
    }

    @ApiOperation("工单管理-工单详情(复制使用)")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "questionParentInfoId", value = "父工单id", required = true, dataType = "Long"),
    })
    @GetMapping("/questionDetail")
    public ResponseResult<TaskParentQuestionVO> questionDetail(@PathVariable("enterprise-id") String enterpriseId,
                                                       @RequestParam(value = "questionParentInfoId") Long questionParentInfoId) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(questionParentInfoService.questionDetail(enterpriseId, questionParentInfoId, UserHolder.getUser().getUserId()));
    }

    @ApiOperation("工单管理-删除父工单")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "questionParentInfoId", value = "父工单id", required = true, dataType = "Long"),
    })
    @PostMapping("/deleteQuestion")
    @SysLog(func = "删除", opModule = OpModuleEnum.QUESTION, opType = OpTypeEnum.DELETE, preprocess = true)
    public ResponseResult<Boolean> deleteQuestion(@PathVariable("enterprise-id") String enterpriseId,
                                                       @RequestParam(value = "questionParentInfoId") Long questionParentInfoId) {
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfigDO = configMapper.selectByEnterpriseId(enterpriseId);
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());
        questionParentInfoService.deleteQuestion(enterpriseId, questionParentInfoId, enterpriseConfigDO.getAppType(), enterpriseConfigDO.getDingCorpId());
        return ResponseResult.success(true);
    }

    @ApiOperation("工单管理-发起工单")
    @PostMapping("/buildQuestion")
    @SysLog(func = "发起工单", opModule = OpModuleEnum.QUESTION, opType = OpTypeEnum.INSERT)
    public ResponseResult<Long> buildQuestion(@PathVariable("enterprise-id") String enterpriseId,
                                                  @RequestBody BuildQuestionRequest buildQuestionRequest) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(questionParentInfoService.buildQuestion(enterpriseId, buildQuestionRequest, UserHolder.getUser().getUserId(), Boolean.FALSE,null));
    }

    /**
     * 工单详情表
     * @param enterpriseId
     * @return
     */
    @ApiOperation("待办-工单详情列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "questionParentInfoId", value = "父工单id", required = true, dataType = "Long"),
            @ApiImplicitParam(name = "status", value = "状态, 1 : 待处理 2:待审核 endNode:已完成", dataType = "String"),
            @ApiImplicitParam(name = "isBatchApprove", value = "是否进行批量审批 是：true, 不是:false", dataType = "Boolean"),
            @ApiImplicitParam(name = "type", value = "类型 我创建的/我管理的:all 待我处理/审批:pending 抄送给我的:cc ,默认查pending", dataType = "String"),
    })
    @GetMapping("/questionDetailList")
    public ResponseResult<List<SubQuestionRecordListVO>>questionDetailList(@PathVariable("enterprise-id") String enterpriseId,
                                                                           @RequestParam(value = "questionParentInfoId",required = false) Long questionParentInfoId,
                                                                           @RequestParam(value = "taskQuestionId",required = false) Long taskQuestionId,
                                                                           @RequestParam(value = "status", required = false) String status,
                                                                           @RequestParam(value = "isBatchApprove", required = false) Boolean isBatchApprove,
                                                                           @RequestParam(value = "type", required = false) String type,
                                                                           @RequestParam(value = "handleUserId", required = false) String handleUserId){
        DataSourceHelper.changeToMy();
        String userId = UserHolder.getUser().getUserId();
        if(StringUtils.isNotBlank(handleUserId)){
            userId = handleUserId;
        }
        if (questionParentInfoId == null || Objects.isNull(questionParentInfoId)){
            TbQuestionParentInfoDO tbQuestionParentInfoDO = tbQuestionParentInfoMapper.selectByUnifyTaskId(enterpriseId, taskQuestionId);
            if (Objects.isNull(tbQuestionParentInfoDO) || Objects.isNull(tbQuestionParentInfoDO.getId())){
                return null;
            }
            questionParentInfoId = tbQuestionParentInfoDO.getId();
        }
        return ResponseResult.success(questionRecordService.questionDetailList(enterpriseId, Arrays.asList(questionParentInfoId), userId, status, isBatchApprove, type));
    }


    @ApiOperation("工单列表导出(父任务)")
    @PostMapping("/questionListExport")
    @SysLog(func = "导出", opModule = OpModuleEnum.IMPORT_EXPORT, opType = OpTypeEnum.EXPORT, menus = "工单-工单管理")
    public ResponseResult<ImportTaskDO> questionListExport(@PathVariable("enterprise-id") String enterpriseId,
                                                           @RequestBody QuestionParentRequest questionParentRequest) {
        DataSourceHelper.changeToMy();
        //管理员可以查询全部，非管理员只能查询自己创建的
        if(!Role.MASTER.getRoleEnum().equals(UserHolder.getUser().getSysRoleDO().getRoleEnum())){
            questionParentRequest.setCreateUserIdList(Collections.singletonList(UserHolder.getUser().getUserId()));
        }
        CurrentUser user = UserHolder.getUser();
        return ResponseResult.success(questionParentInfoService.questionListExport(enterpriseId, questionParentRequest,user));
    }

    @ApiOperation("巡店记录表内工单完成情况")
    @GetMapping("/workOrder/completionStatus")
    public ResponseResult workOrderCompletionStatus(@PathVariable("enterprise-id") String enterpriseId,
                                                    @RequestParam Long businessId) {
        DataSourceHelper.reset();
        EnterpriseConfigDO configDO = enterpriseConfigService.selectByEnterpriseId(enterpriseId);
        DataSourceHelper.changeToSpecificDataSource(configDO.getDbName());
        return ResponseResult.success(questionParentInfoService.workOrderCompletionStatus(enterpriseId, businessId));
    }

}
