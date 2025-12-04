package com.coolcollege.intelligent.controller.unifytask;

import com.coolcollege.intelligent.common.annotation.BaseResponse;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.common.util.PageHelperUtil;
import com.coolcollege.intelligent.model.enums.TaskTypeEnum;
import com.coolcollege.intelligent.model.unifytask.query.TaskFinishStorePageRequest;
import com.coolcollege.intelligent.model.unifytask.query.TaskReportQuery;
import com.coolcollege.intelligent.model.unifytask.vo.TaskFinishStoreVO;
import com.coolcollege.intelligent.model.userholder.UserHolder;
import com.coolcollege.intelligent.service.unifytask.UnifyTaskReportService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author wxp
 * @date 2021-6-21
 */

@Api(tags = "任务报告")
@RestController
@RequestMapping("/v3/enterprises/{enterprise-id}/taskReport")
@BaseResponse
@Slf4j
public class UnifyTaskReportController {

    @Resource
    private UnifyTaskReportService unifyTaskReportService;

    /**
     * 巡店任务报表
     * @param enterpriseId
     * @param query
     * @return
     */
    @PostMapping("/patrolStoreReport")
    public ResponseResult patrolStoreReport(@PathVariable("enterprise-id") String enterpriseId, @RequestBody TaskReportQuery query){
        DataSourceHelper.changeToMy();
        query.setTaskType(Constants.PATROL_STORE_TYPE);
        PageInfo list = unifyTaskReportService.listTaskReport(enterpriseId, query);
        return ResponseResult.success(PageHelperUtil.getPageInfo(list));
    }

    /**
     * 巡店任务报表导出
     * @param enterpriseId
     * @param query
     * @return
     */
    @PostMapping("/patrolStoreReportExport")
    public Object patrolStoreReportExport(@PathVariable("enterprise-id") String enterpriseId, @RequestBody TaskReportQuery query){
        DataSourceHelper.changeToMy();
        query.setDbName(UserHolder.getUser().getDbName());
        query.setTaskType(Constants.PATROL_STORE_TYPE);
        return unifyTaskReportService.taskReportExport(enterpriseId,query);

    }

    /**
     * 陈列任务报表
     * @param enterpriseId
     * @param query
     * @return
     */
    @PostMapping("/tbDisplayReport")
    public ResponseResult tbDisplayReport(@PathVariable("enterprise-id") String enterpriseId, @RequestBody TaskReportQuery query){
        DataSourceHelper.changeToMy();
        query.setTaskType(TaskTypeEnum.TB_DISPLAY_TASK.getCode());
        PageInfo list = unifyTaskReportService.listTaskReport(enterpriseId, query);
        return ResponseResult.success(PageHelperUtil.getPageInfo(list));
    }

    @ApiOperation("获取任务完成门店情况")
    @PostMapping("/getTaskFinishStorePage")
    public ResponseResult<PageInfo<TaskFinishStoreVO>> getTaskFinishStorePage(@PathVariable("enterprise-id") String enterpriseId, @Validated @RequestBody TaskFinishStorePageRequest query){
        DataSourceHelper.changeToMy();
        return ResponseResult.success(unifyTaskReportService.getTaskFinishStorePage(enterpriseId, query));
    }


    /**
     * 陈列任务报表导出
     * @param enterpriseId
     * @param query
     * @return
     */
    @PostMapping("/tbDisplayReportExport")
    public Object tbDisplayReportExport(@PathVariable("enterprise-id") String enterpriseId, @RequestBody TaskReportQuery query){
        DataSourceHelper.changeToMy();
        query.setDbName(UserHolder.getUser().getDbName());
        query.setTaskType(TaskTypeEnum.TB_DISPLAY_TASK.getCode());
        return unifyTaskReportService.taskReportExport(enterpriseId,query);

    }



}
