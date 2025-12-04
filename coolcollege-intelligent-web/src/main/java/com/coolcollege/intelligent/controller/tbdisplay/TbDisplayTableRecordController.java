package com.coolcollege.intelligent.controller.tbdisplay;

import com.coolcollege.intelligent.common.annotation.BaseResponse;
import com.coolcollege.intelligent.common.annotation.OperateLog;
import com.coolcollege.intelligent.common.constant.CommonConstant;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.dao.enterprise.dao.EnterpriseConfigDao;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.tbdisplay.TbMetaDisplayTableColumnDO;
import com.coolcollege.intelligent.model.tbdisplay.param.*;
import com.coolcollege.intelligent.model.tbdisplay.vo.TbDisplayTableRecordDeleteVO;
import com.coolcollege.intelligent.model.tbdisplay.vo.TbDisplayTaskShowVO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.model.userholder.UserHolder;
import com.coolcollege.intelligent.service.metatable.TbMetaTableService;
import com.coolcollege.intelligent.service.tbdisplay.TbDisplayHistoryService;
import com.coolcollege.intelligent.service.tbdisplay.TbDisplayService;
import com.coolcollege.intelligent.service.tbdisplay.TbDisplayTableRecordService;
import com.coolcollege.intelligent.service.tbdisplay.TbMetaDisplayTableColumnService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 *
 * @author : admin
 * @Description : 新陈列任务操作
 * @date ：2021/03/03
 */
@Api(tags = "陈列任务操作")
@RestController
@RequestMapping("/v3/enterprises/{enterprise-id}/tbdisplay/tbdisplayTableRecord")
@BaseResponse
@Slf4j
public class TbDisplayTableRecordController {

    @Resource
    private TbDisplayService tbDisplayService;

    @Resource
    private TbDisplayTableRecordService tbDisplayTableRecordService;

    @Resource
    private TbDisplayHistoryService tbDisplayHistoryService;

    @Autowired
    private TbMetaTableService tableService;

    @Autowired
    private TbMetaDisplayTableColumnService tbMetaDisplayTableColumnService;

    @Resource
    EnterpriseConfigDao enterpriseConfigDao;

    // 陈列记录手动补漏
    @PostMapping(path = "/build")
    @OperateLog(operateModule = CommonConstant.Function.PATROL_TASK, operateType = CommonConstant.LOG_ADD, operateDesc = "陈列记录补漏")
    public ResponseResult build(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                @RequestBody @Valid TbDisplayTableRecordBuildParam param) {
        DataSourceHelper.changeToMy();

        return ResponseResult.success(tbDisplayTableRecordService.buildByTaskId(enterpriseId, param));
    }

    /**
     * 单个任务审核
     *
     * @param enterpriseId
     * @param approveDisplayTaskParam
     * @return
     */
    @PostMapping(path = "/approve")
    @OperateLog(operateModule = CommonConstant.Function.DISPLAY_TASK, operateType = CommonConstant.LOG_UPDATE, operateDesc = "批量评分检查项")
    public ResponseResult approve(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                @RequestBody @Valid TbApproveDisplayTaskParam approveDisplayTaskParam) {
        DataSourceHelper.changeToMy();
        tbDisplayTableRecordService.approve(enterpriseId, UserHolder.getUser(), approveDisplayTaskParam);
        return ResponseResult.success(null);
    }

    /**
     * 多个任务审核
     * @param enterpriseId
     * @param batchApproveDisplayTaskParam
     * @return
     */
    @PostMapping(path = "/batchApprove")
    @OperateLog(operateModule = CommonConstant.Function.DISPLAY_TASK, operateType = CommonConstant.LOG_UPDATE, operateDesc = "批量评分检查项")
    public ResponseResult batchApprove(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                     @RequestBody @Valid TbBatchApproveDisplayTaskParam batchApproveDisplayTaskParam) {
        DataSourceHelper.changeToMy();
        tbDisplayService.batchApprove(enterpriseId, UserHolder.getUser(), batchApproveDisplayTaskParam);
        return ResponseResult.success(true);
    }

    /**
     * 多个任务评分保存
     * @param enterpriseId
     * @param batchApproveDisplayTaskParam
     * @return
     */
    @PostMapping(path = "/batchScore")
    @OperateLog(operateModule = CommonConstant.Function.DISPLAY_TASK, operateType = CommonConstant.LOG_UPDATE, operateDesc = "批量评分检查项")
    public ResponseResult batchScore(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                       @RequestBody @Valid TbBatchApproveDisplayTaskParam batchApproveDisplayTaskParam) {
        DataSourceHelper.changeToMy();
        tbDisplayService.batchScore(enterpriseId, UserHolder.getUser(), batchApproveDisplayTaskParam);
        return ResponseResult.success(true);
    }


    /**
     * 根据多个子任务id查询任务记录详情和检查项列表（批量审批 ）
     *
     * @return
     */
    @PostMapping(path = "/detailGroupBySubTaskId")
    public ResponseResult detailGroupBySubTaskId(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                      @RequestBody List<Long> subTaskIdList) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(tbDisplayService.detailGroupBySubTaskId(enterpriseId, UserHolder.getUser().getUserId(), subTaskIdList));
    }


    /**
     * 根据多个门店任务id查询任务记录详情和检查项列表（批量审批 ）
     *
     * @return
     */
    @PostMapping(path = "/detailGroupByTaskStoreId")
    public ResponseResult detailGroupByTaskStoreId(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                                 @RequestBody List<Long> taskStoreIdList) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(tbDisplayService.detailGroupByTaskStoreId(enterpriseId, UserHolder.getUser().getUserId(), taskStoreIdList));
    }

    /**
     * 根据子任务id查询任务记录详情和检查项列表
     *
     * @return
     */
    @GetMapping(path = "/detail")
    public ResponseResult batchDetail(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                      @RequestParam(name = "subTaskId", required = true)Long subTaskId) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(tbDisplayService.detail(enterpriseId, UserHolder.getUser().getUserId(), subTaskId));
    }

    /**
     * 根据父任务id、门店id、轮次查询任务记录详情和检查项列表
     * @return
     */
    @GetMapping(path = "/detailByTaskIdAndStoreIdAndLoopCount")
    public ResponseResult detailByTaskIdAndStoreIdAndLoopCount(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                      @RequestParam(name = "unifyTaskId", required = true)Long unifyTaskId,
                                      @RequestParam(name = "storeId", required = true)String storeId,
                                      @RequestParam(name = "loopCount", required = true)Long loopCount) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(tbDisplayService.detailByTaskIdAndStoreIdAndLoopCount(enterpriseId, unifyTaskId, storeId, loopCount));
    }


    /**
     * 陈列检查项处理
     *
     * @param enterpriseId
     * @param handleParam
     * @return
     */
    @PostMapping(path = "/tbDisplayColumnHandle")
    @OperateLog(operateModule = CommonConstant.Function.DISPLAY_TASK, operateType = CommonConstant.LOG_UPDATE, operateDesc = "批量处理陈列检查项")
    public ResponseResult tbDisplayColumnHandle(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                                  @RequestBody @Valid TbDisplayHandleParam handleParam) {
        DataSourceHelper.changeToMy();
        tbDisplayService.tbDisplayColumnHandle(enterpriseId, handleParam, UserHolder.getUser(), false);
        return ResponseResult.success(true);
    }

    /**
     * 单个陈列记录处理提交
     *
     * @param enterpriseId
     * @param handleParam
     * @return
     */
    @PostMapping(path = "/tableRecordHandleSubmit")
    @OperateLog(operateModule = CommonConstant.Function.DISPLAY_TASK, operateType = CommonConstant.LOG_UPDATE, operateDesc = "单个陈列记录处理")
    public ResponseResult tableRecordHandleSubmit(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                      @RequestBody @Valid TbDisplayHandleParam handleParam) {
        DataSourceHelper.changeToMy();
        tbDisplayService.tableRecordHandleSubmit(enterpriseId, handleParam, UserHolder.getUser());
        return ResponseResult.success(true);
    }

    /**
     * 记录操作历史
     *
     * @return
     */
    @GetMapping(path = "/listHistoryByTaskSubId")
    public ResponseResult listHistoryByTaskSubId(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                                @RequestParam(name = "subTaskId", required = true) Long subTaskId) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(tbDisplayHistoryService.listHistoryByTaskSubId(enterpriseId, subTaskId));
    }

    /**
     * 陈列子任务报表   数据接口  根据父任务id查询陈列记录报表
     *
     * @return
     */
    @PostMapping(path = "/tableRecordReport")
    public ResponseResult tableRecordReport(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                  @RequestBody @Valid TbDisplayReportQueryParam query) {
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        return ResponseResult.success(tbDisplayTableRecordService.tableRecordReportWithPage(enterpriseId, query, user));
    }

    // 根据陈列检查表id查询快捷检查项id集合，及任务表单类型
    @GetMapping("/getQuickColumnIdListByMetaTableId")
    public ResponseResult getQuickColumnIdListByMetaTableId(@PathVariable("enterprise-id")String enterpriseId,
                                            @RequestParam(name = "metaTableId", required = true) Long metaTableId){
        DataSourceHelper.changeToMy();
        TbDisplayTaskShowVO vo = tableService.getQuickColumnIdListByMetaTableId(enterpriseId, metaTableId);
        return ResponseResult.success(vo);
    }

    // 根据检查项id查询检查项信息
    @GetMapping("/getByMetaColumnId")
    public ResponseResult getByMetaColumnId(@PathVariable("enterprise-id")String enterpriseId,
                                                            @RequestParam(name = "metaColumnId", required = true) Long metaColumnId){
        DataSourceHelper.changeToMy();
        TbMetaDisplayTableColumnDO tbMetaDisplayTableColumnDO = tbMetaDisplayTableColumnService.getByMetaColumnId(enterpriseId, metaColumnId);
        return ResponseResult.success(tbMetaDisplayTableColumnDO);
    }

    /**
     * 父任务数据列表
     *
     * @return
     */
    @ApiOperation(value = "父任务数据列表", notes = "1、百里专项需求，我处理的列表查询数据列表是新增userId参数")
    @PostMapping(path = "/taskTableRecordList")
    public ResponseResult taskTableRecordList(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                            @RequestBody @Valid TbDisplayReportQueryParam query) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(tbDisplayTableRecordService.tableRecordList(enterpriseId, query));
    }

    /**
     * 导出
     *
     * @return
     */
    @ApiOperation(value = "导出", notes = "1、百里专项需求，我处理的列表查询数据列表是新增userId参数")
    @PostMapping(path = "/taskTableRecordListExport")
    public ResponseResult taskTableRecordListExport(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                              @RequestBody @Valid TbDisplayReportQueryParam query) {
        DataSourceHelper.changeToMy();
        query.setDbName(UserHolder.getUser().getDbName());
        query.setEid(enterpriseId);
        return ResponseResult.success(tbDisplayTableRecordService.tableRecordListExport(enterpriseId, query));
    }

    /**
     * 带图导出
     * @param enterpriseId
     * @param query
     * @return
     */
    @ApiOperation(value = "带图导出", notes = "1、百里专项需求，我处理的列表查询数据列表是新增userId参数")
    @PostMapping(path = "/taskTableRecordListPictureExport")
    public ResponseResult taskTableRecordListPictureExport(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                                    @RequestBody @Valid TbDisplayReportQueryParam query) {
        DataSourceHelper.changeToMy();
        query.setDbName(UserHolder.getUser().getDbName());
        query.setEid(enterpriseId);
        query.setPicture(true);
        return ResponseResult.success(tbDisplayTableRecordService.tableRecordListExport(enterpriseId, query));
    }

    /**
     * 刪除陈列门店任务
     * @param enterpriseId
     * @param displayDeleteParam
     * @return
     */
    @ApiOperation(value = "刪除陈列门店任务")
    @PostMapping(path = "/deleteRecord")
    @OperateLog(operateModule = CommonConstant.Function.DISPLAY_TASK, operateType = CommonConstant.LOG_DELETE, operateDesc = "删除陈列门店任务")
    public ResponseResult deleteRecord(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                     @RequestBody @Valid TbDisplayDeleteParam displayDeleteParam) {
        DataSourceHelper.reset();
        EnterpriseConfigDO config = enterpriseConfigDao.getEnterpriseConfig(enterpriseId);
        DataSourceHelper.changeToMy();
        tbDisplayTableRecordService.deleteRecord(enterpriseId, displayDeleteParam, UserHolder.getUser(),"deleteRecord", config);
        return ResponseResult.success(true);
    }

    @ApiOperation(value = "批量刪除陈列门店任务")
    @PostMapping(path = "/batchDeleteRecord")
    @OperateLog(operateModule = CommonConstant.Function.DISPLAY_TASK, operateType = CommonConstant.LOG_DELETE, operateDesc = "批量刪除陈列门店任务")
    public ResponseResult batchDeleteRecord(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                       @RequestBody @Valid TbDisplayBatchDeleteParam displayDeleteParam) {
        DataSourceHelper.changeToMy();
        tbDisplayTableRecordService.batchDeleteRecord(enterpriseId, displayDeleteParam, UserHolder.getUser());
        return ResponseResult.success(true);
    }

    /**
     * 刪除陈列门店任务
     * @param enterpriseId
     * @param unifyTaskId
     * @return
     */
    @ApiOperation(value = "获取删除的陈列门店任务")
    @GetMapping(path = "/getDeleteRecordList")
    @ApiImplicitParam(name = "unifyTaskId", value = "任务is", required = true)
    public ResponseResult<PageInfo<TbDisplayTableRecordDeleteVO>> getDeleteRecordList(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                                                                      @RequestParam(value = "pageNum", defaultValue = "1", required = false) Integer pageNum,
                                                                                      @RequestParam(value = "pageSize", defaultValue = "20", required = false) Integer pageSize,
                                                                                      @RequestParam(value = "unifyTaskId", required = false) Long unifyTaskId,
                                                                                      @RequestParam(value = "unifyTaskIds", required = false) String unifyTaskIds
                                                                                      ) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(tbDisplayTableRecordService.getDeleteRecordList(enterpriseId, unifyTaskId, pageNum, pageSize,unifyTaskIds));
    }

}
