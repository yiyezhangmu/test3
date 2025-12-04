package com.coolcollege.intelligent.controller.question;

import com.alipay.remoting.util.StringUtils;
import com.coolcollege.intelligent.common.annotation.ErrorHelper;
import com.coolcollege.intelligent.common.annotation.SysLog;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.constant.RedisConstant;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.enums.syslog.OpModuleEnum;
import com.coolcollege.intelligent.common.enums.syslog.OpTypeEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.page.PageVO;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseConfigMapper;
import com.coolcollege.intelligent.facade.dto.PageDTO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.impoetexcel.ImportTaskDO;
import com.coolcollege.intelligent.model.page.PageRequest;
import com.coolcollege.intelligent.model.question.request.QuestionCacheDataRequest;
import com.coolcollege.intelligent.model.question.request.RegionQuestionReportRequest;
import com.coolcollege.intelligent.model.question.request.TbQuestionRecordSearchRequest;
import com.coolcollege.intelligent.model.question.vo.*;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.model.userholder.UserHolder;
import com.coolcollege.intelligent.service.question.QuestionHistoryService;
import com.coolcollege.intelligent.service.question.QuestionRecordService;
import com.coolcollege.intelligent.util.RedisConstantUtil;
import com.coolcollege.intelligent.util.RedisUtil;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.taobao.api.ApiException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.text.MessageFormat;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 问题工单任务记录
 * @author zhangnan
 * @date 2021-12-21 19:25
 */
@Api(tags = "工单记录")
@RequestMapping("/v3/enterprises/{enterprise-id}/questionRecord")
@RestController
@ErrorHelper
public class QuestionRecordController {

    @Autowired
    private QuestionHistoryService questionHistoryService;
    @Resource
    private QuestionRecordService questionRecordService;
    @Resource
    private EnterpriseConfigMapper enterpriseConfigMapper;
    @Resource
    private RedisUtil redisUtil;
    @Resource
    private RedisUtilPool redisUtilPool;
    @Resource
    private RedisConstantUtil redisConstantUtil;
    @Resource
    private EnterpriseConfigMapper configMapper;

    @ApiOperation("问题工单-查询列表（分页）")
    @GetMapping("/list")
    public ResponseResult<PageVO<TbQuestionRecordListVO>> list(@PathVariable("enterprise-id") String enterpriseId, TbQuestionRecordSearchRequest recordSearchRequest,
                                                                 PageRequest pageRequest) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(questionRecordService.list(enterpriseId, recordSearchRequest, pageRequest, UserHolder.getUser()));
    }

    @ApiOperation("问题工单-查询移动端列表（分页）")
    @GetMapping("/listForMobile")
    public ResponseResult<PageVO<TbQuestionRecordMobileListVO>> listForMobile(@PathVariable("enterprise-id") String enterpriseId, TbQuestionRecordSearchRequest recordSearchRequest,
                                                                              PageRequest pageRequest) {
        DataSourceHelper.changeToMy();
        recordSearchRequest.setCurrentUserId(UserHolder.getUser().getId());
        return ResponseResult.success(questionRecordService.listForMobile(enterpriseId, recordSearchRequest, pageRequest, UserHolder.getUser()));
    }

    @ApiOperation("问题工单-导出")
    @GetMapping("/export")
    public ResponseResult<ImportTaskDO> export(@PathVariable("enterprise-id") String enterpriseId, TbQuestionRecordSearchRequest recordSearchRequest) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(questionRecordService.export(enterpriseId, recordSearchRequest, UserHolder.getUser()));
    }

    @ApiOperation("问题工单-详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "unifyTaskId", value = "父任务id", required = true),
            @ApiImplicitParam(name = "storeId", value = "门店id", required = true)
    })
    @GetMapping("/detail")
    public ResponseResult<TbQuestionRecordDetailVO> detail(@PathVariable("enterprise-id") String enterpriseId,
                                                           @RequestParam(required = true) Long unifyTaskId,
                                                           @RequestParam(required = true) String storeId,
                                                           @RequestParam(value = "loopCount", required = false) Long loopCount) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(questionRecordService.detail(enterpriseId, unifyTaskId, storeId, UserHolder.getUser(), loopCount));
    }

    @ApiOperation("问题工单-处理记录列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "unifyTaskId", value = "父任务id", required = true, dataType = "Long"),
            @ApiImplicitParam(name = "storeId", value = "门店id", required = true, dataType = "String")
    })
    @GetMapping("/historyList")
    public ResponseResult<List<TbQuestionHistoryVO>> historyList(@PathVariable("enterprise-id") String enterpriseId,
                                                                 @RequestParam(value = "unifyTaskId", required = true) Long unifyTaskId,
                                                                 @RequestParam(value = "storeId", required = true) String storeId,
                                                                 @RequestParam(value = "loopCount", required = false) Long loopCount) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(questionHistoryService.selectHistoryList(enterpriseId, unifyTaskId, storeId, loopCount));
    }

    @ApiOperation("问题工单-删除工单(旧)")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "questionRecordId", value = "问题工单id", required = true, dataType = "Long"),
    })
    @DeleteMapping("/delete")
    public ResponseResult delete(@PathVariable("enterprise-id") String enterpriseId,
                                                                 @RequestParam(value = "questionRecordId", required = true) Long questionRecordId) {
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigMapper.selectByEnterpriseId(enterpriseId);
        DataSourceHelper.changeToMy();
        questionRecordService.deleteByQuestionRecordId(enterpriseId, questionRecordId, enterpriseConfigDO.getDingCorpId(),enterpriseConfigDO.getAppType());
        return ResponseResult.success();
    }

    @ApiOperation("问题工单-获取是否引导")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "menuId", value = "菜单id", required = true, dataType = "Long"),
    })
    @GetMapping("/getGuideInfo")
    public ResponseResult<Boolean> getGuideInfo(@PathVariable("enterprise-id") String enterpriseId,
                                 @RequestParam(value = "menuId") Long menuId) {
        boolean hase = redisUtil.haseHashKey(redisConstantUtil.getGuideInfoKey(enterpriseId, menuId), UserHolder.getUser().getUserId());
        return ResponseResult.success(hase);
    }

    @ApiOperation("问题工单-设置已经引导")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "menuId", value = "菜单id", required = true, dataType = "Long"),
    })
    @PostMapping("/setGuideInfo")
    public ResponseResult setGuideInfo(@PathVariable("enterprise-id") String enterpriseId,
                                       @RequestParam(value = "menuId") Long menuId) {
        redisUtil.put(redisConstantUtil.getGuideInfoKey(enterpriseId, menuId), UserHolder.getUser().getUserId(), Boolean.TRUE);
        return ResponseResult.success();
    }

    @ApiOperation("问题工单-删除子工单(新)")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "questionRecordId", value = "问题工单id", required = true, dataType = "Long"),
    })
    @DeleteMapping("/delQuestionRecord")
    @SysLog(func = "详情", subFunc = "删除", opModule = OpModuleEnum.QUESTION, opType = OpTypeEnum.QUESTION_RECORD_DELETE)
    public ResponseResult delQuestionRecord(@PathVariable("enterprise-id") String enterpriseId,
                                 @RequestParam(value = "questionRecordId") Long questionRecordId) {
        DataSourceHelper.changeToMy();
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfigDO = configMapper.selectByEnterpriseId(enterpriseId);
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());
        questionRecordService.delQuestionRecord(enterpriseId, questionRecordId, enterpriseConfigDO.getDingCorpId(), enterpriseConfigDO.getAppType());
        return ResponseResult.success();
    }

    @ApiOperation("工单催办")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "questionParentInfoId", value = "父工单id", required = true, dataType = "Long"),
            @ApiImplicitParam(name = "questionRecordId", value = "问题工单id", required = true, dataType = "Long"),
    })
    @GetMapping("/questionReminder")
    @SysLog(func = "催办", opModule = OpModuleEnum.QUESTION, opType = OpTypeEnum.REMIND)
    public ResponseResult<List<String>> questionReminder(@PathVariable("enterprise-id") String enterpriseId,
                                            @RequestParam(value = "questionParentInfoId") Long questionParentInfoId,
                                            @RequestParam(value = "questionRecordId", required = false) Long questionRecordId) {
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        String appType = user.getAppType();
        return questionRecordService.questionReminder(enterpriseId, questionParentInfoId, questionRecordId, appType);
    }


    @ApiOperation("工单批量催办")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "questionParentInfoIds", value = "父工单id", required = true, dataType = "List"),
    })
    @GetMapping("/batchQuestionReminder")
    @SysLog(func = "一键催办", opModule = OpModuleEnum.QUESTION, opType = OpTypeEnum.BATCH_REMIND)
    public ResponseResult batchQuestionReminder(@PathVariable("enterprise-id") String enterpriseId,
                                                         @RequestParam(value = "questionParentInfoIds") List<Long> questionParentInfoIds) {
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        String appType = user.getAppType();
        questionRecordService.batchQuestionReminder(enterpriseId, questionParentInfoIds, appType);
        return ResponseResult.success();
    }

    @ApiOperation("工单分享")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "questionRecordIds", value = "问题工单id列表", required = true, dataType = "Long"),
    })
    @GetMapping("/questionShare")
    public ResponseResult questionShare(@PathVariable("enterprise-id") String enterpriseId,
                                           @RequestParam(value = "questionRecordIds") List<Long> questionRecordIds,
                                           @RequestParam(value = "isOneKeyShare", required = false) Boolean isOneKeyShare,
                                           @RequestParam(value = "shareKey") String shareKey) {
        String userId = UserHolder.getUser().getUserId();
        DataSourceHelper.changeToMy();
        questionRecordService.questionShare(enterpriseId, isOneKeyShare, questionRecordIds, userId, shareKey);
        return ResponseResult.success();
    }

    @ApiOperation("工单分享详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "shareKey", value = "分享key", required = true, dataType = "Long")
    })
    @GetMapping("/getQuestionShareDetail")
    public ResponseResult getQuestionShareDetail(@PathVariable("enterprise-id") String enterpriseId,
                                                @RequestParam(value = "shareKey") String shareKey,
                                                @RequestParam(value = "questionRecordId", required = false) Long questionRecordId) {
        try {
            DataSourceHelper.changeToMy();
            return questionRecordService.getQuestionShareDetail(enterpriseId, questionRecordId, shareKey);
        } catch (ApiException e) {
            throw new ServiceException(ErrorCodeEnum.DATA_ERROR);
        }
    }

    /**
     * 工单详情表
     * @param enterpriseId
     * @param tbQuestionRecordSearchRequest
     * @return
     */
    @ApiOperation("工单详情表")
    @PostMapping("/subQuestionDetailList")
    public ResponseResult<PageDTO<SubQuestionDetailVO>> subQuestionDetailList(@PathVariable("enterprise-id") String enterpriseId,
                                                                              @RequestBody TbQuestionRecordSearchRequest tbQuestionRecordSearchRequest){
        DataSourceHelper.changeToMy();
        if (CollectionUtils.isNotEmpty(tbQuestionRecordSearchRequest.getCreateUserIds()) && StringUtils.isNotBlank(tbQuestionRecordSearchRequest.getCreateUserId())) {
            tbQuestionRecordSearchRequest.getCreateUserIds().add(tbQuestionRecordSearchRequest.getCreateUserId());
            tbQuestionRecordSearchRequest.setCreateUserId(null);
        }
        if (CollectionUtils.isNotEmpty(tbQuestionRecordSearchRequest.getMetaTableIds()) && Objects.nonNull(tbQuestionRecordSearchRequest.getMetaTableId())) {
            tbQuestionRecordSearchRequest.getMetaTableIds().add(tbQuestionRecordSearchRequest.getMetaTableId());
            tbQuestionRecordSearchRequest.setMetaTableId(null);
        }
        return ResponseResult.success(questionRecordService.subQuestionDetailList(enterpriseId, UserHolder.getUser().getUserId(), tbQuestionRecordSearchRequest));
    }

    @ApiOperation("工单区域报表")
    @PostMapping("/getQuestionRegionReport")
    public ResponseResult<List<RegionQuestionReportVO>> getQuestionRegionReport(@PathVariable("enterprise-id") String enterpriseId,
                                                @RequestBody RegionQuestionReportRequest request){
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        return ResponseResult.success(questionRecordService.getQuestionReport(enterpriseId,request,user));
    }

    @ApiOperation("工单详情表导出")
    @PostMapping("/subQuestionDetailListExport")
    @SysLog(func = "导出", opModule = OpModuleEnum.IMPORT_EXPORT, opType = OpTypeEnum.EXPORT, menus = "工单-工单记录")
    public ResponseResult<ImportTaskDO> subQuestionDetailListExport(@PathVariable("enterprise-id") String enterpriseId,
                                                @RequestBody TbQuestionRecordSearchRequest tbQuestionRecordSearchRequest){
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        if (CollectionUtils.isNotEmpty(tbQuestionRecordSearchRequest.getCreateUserIds()) && StringUtils.isNotBlank(tbQuestionRecordSearchRequest.getCreateUserId())) {
            tbQuestionRecordSearchRequest.getCreateUserIds().add(tbQuestionRecordSearchRequest.getCreateUserId());
            tbQuestionRecordSearchRequest.setCreateUserId(null);
        }
        if (CollectionUtils.isNotEmpty(tbQuestionRecordSearchRequest.getMetaTableIds()) && Objects.nonNull(tbQuestionRecordSearchRequest.getMetaTableId())) {
            tbQuestionRecordSearchRequest.getMetaTableIds().add(tbQuestionRecordSearchRequest.getMetaTableId());
            tbQuestionRecordSearchRequest.setMetaTableId(null);
        }
        return ResponseResult.success(questionRecordService.subQuestionDetailListExport(enterpriseId,tbQuestionRecordSearchRequest,user));
    }


    @ApiOperation("工单区域报表导出")
    @PostMapping("/questionRegionReportExport")
    @SysLog(func = "导出", opModule = OpModuleEnum.IMPORT_EXPORT, opType = OpTypeEnum.EXPORT, menus = "工单-工单报表")
    public ResponseResult<ImportTaskDO> questionRegionReportExport(@PathVariable("enterprise-id") String enterpriseId,
                                                  @RequestBody RegionQuestionReportRequest request){
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        return ResponseResult.success(questionRecordService.regionQuestionReportExport(enterpriseId,request,user));
    }

    @ApiOperation("工单管理-工单记录列表（分页）")
    @GetMapping("/recordList")
    public ResponseResult<PageVO<SubQuestionRecordListVO>> recordList(@PathVariable("enterprise-id") String enterpriseId, TbQuestionRecordSearchRequest recordSearchRequest,
                                                                      PageRequest pageRequest) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(questionRecordService.recordList(enterpriseId, recordSearchRequest, pageRequest, UserHolder.getUser()));
    }

    @ApiOperation("保存工单处理或审批数据")
    @GetMapping("/saveQuestionData")
    public ResponseResult saveQuestionData(@PathVariable("enterprise-id") String enterpriseId, @Valid @RequestBody QuestionCacheDataRequest request) {
        String userId = UserHolder.getUser().getUserId();
        Long questionRecordId = request.getQuestionRecordId();
        if(Objects.isNull(questionRecordId)){
            questionRecordId = Constants.LONG_ZERO;
        }
        String cacheKey = MessageFormat.format(RedisConstant.QUESTION_CACHE_KEY, enterpriseId, userId, request.getQuestionParentInfoId(), questionRecordId);
        redisUtilPool.setString(cacheKey, request.getSaveData(), RedisConstant.THREE_DAY);
        return ResponseResult.success();
    }

    @ApiOperation("获取工单处理或审批数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "questionParentInfoId", value = "父工单id", required = true, dataType = "Long"),
            @ApiImplicitParam(name = "questionRecordId", value = "子工单id", required = false, dataType = "Long"),
    })
    @GetMapping("/getQuestionDataData")
    public ResponseResult getQuestionDataData(@PathVariable("enterprise-id") String enterpriseId,@RequestParam(value = "questionParentInfoId") Long questionParentInfoId,
                                      @RequestParam(value = "questionRecordId") Long questionRecordId) {
        String userId = UserHolder.getUser().getUserId();
        if(Objects.isNull(questionRecordId)){
            questionRecordId = Constants.LONG_ZERO;
        }
        String cacheKey = MessageFormat.format(RedisConstant.QUESTION_CACHE_KEY, enterpriseId, userId, questionParentInfoId, questionRecordId);
        return ResponseResult.success(redisUtilPool.getString(cacheKey));
    }


    @ApiOperation("获取检查项工单跳转详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "dataColumnId", value = "不合格数据项id", required = true, dataType = "Long"),
    })
    @GetMapping("/getRecordByDataColumnId")
    public ResponseResult<SubQuestionRecordListVO> getRecordByDataColumnId(@PathVariable("enterprise-id") String enterpriseId,
                                              @RequestParam(value = "dataColumnId") Long dataColumnId) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(questionRecordService.getRecordByDataColumnId(enterpriseId, dataColumnId));
    }

    @ApiOperation("巡店-工单追踪")
    @GetMapping("/questionPatrolList")
    public ResponseResult<List<SubQuestionRecordListVO>> questionPatrolList(@PathVariable("enterprise-id") String enterpriseId,
                                                                            @RequestParam(value = "businessId") Long businessId) {
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        return ResponseResult.success(questionRecordService.questionPatrolList(enterpriseId, businessId, user));
    }
}
