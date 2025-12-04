package com.coolcollege.intelligent.controller.unifytask;

import com.coolcollege.intelligent.common.annotation.BaseResponse;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.model.unifytask.query.TaskQuestionQuery;
import com.coolcollege.intelligent.model.unifytask.vo.TaskStoreQuestionDataVO;
import com.coolcollege.intelligent.model.userholder.UserHolder;
import com.coolcollege.intelligent.service.unifytask.UnifyTaskService;
import com.coolcollege.intelligent.service.unifytask.UnifyTaskStoreService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author byd
 * @date 2021-06-22 10:36
 */
@RestController
@RequestMapping("/v3/enterprises/{enterprise-id}/taskQuestion")
@BaseResponse
@Slf4j
public class UnifyTaskQuestionController {


    @Autowired
    private UnifyTaskService unifyTaskService;

    @Autowired
    private UnifyTaskStoreService unifyTaskStoreService;

    /**
     * 问题工单列表
     *
     * @param enterpriseId
     * @param query
     * @return
     */
    @PostMapping(path = "/list")
    public ResponseResult list(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                         @RequestBody @Validated TaskQuestionQuery query) {
        DataSourceHelper.changeToMy();
        query.setDbName(UserHolder.getUser().getDbName());
        return ResponseResult.success(unifyTaskService.taskQuestionReportList(enterpriseId, query.getUserIdList(), query.getBeginTime(), query.getEndTime(),
                query.getPageNumber(), query.getPageSize()));
    }

    /**
     * 问题工单列表导出
     *
     * @param enterpriseId
     * @param query
     * @return
     */
    @PostMapping(path = "/exportList")
    public ResponseResult exportList(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                         @RequestBody @Validated TaskQuestionQuery query) {
        DataSourceHelper.changeToMy();
        query.setDbName(UserHolder.getUser().getDbName());
        return ResponseResult.success(unifyTaskService.taskQuestionReportListExport(enterpriseId, query));
    }

    /**
     * 问题工单列表
     *
     * @param enterpriseId
     * @return
     */
    @GetMapping(path = "/lastStoreQuestion")
    public ResponseResult<TaskStoreQuestionDataVO> lastStoreQuestion(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                                                     @RequestParam(value = "storeId") String storeId) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(unifyTaskStoreService.getLastTaskStoreQuestion(enterpriseId, storeId));
    }

}
