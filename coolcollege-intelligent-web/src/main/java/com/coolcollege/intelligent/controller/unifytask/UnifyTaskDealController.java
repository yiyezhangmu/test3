package com.coolcollege.intelligent.controller.unifytask;

import com.alibaba.fastjson.JSON;
import com.coolcollege.intelligent.common.annotation.BaseResponse;
import com.coolcollege.intelligent.common.annotation.ErrorHelper;
import com.coolcollege.intelligent.common.annotation.OperateLog;
import com.coolcollege.intelligent.common.constant.CommonConstant;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.common.util.ValidList;
import com.coolcollege.intelligent.model.task.param.DealParam;
import com.coolcollege.intelligent.model.task.param.DealTaskParam;
import com.coolcollege.intelligent.model.userholder.UserHolder;
import com.coolcollege.intelligent.service.task.TaskDealService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * Description for this class
 *
 * @author : jixiang.jiang
 * @version : 1.0
 * @Description : Description for this class
 * @date ：Created in 2020/12/14 15:23
 */
@Api(tags = "任务处理")
@RestController
@RequestMapping("/v3/enterprises/{enterprise-id}/taskdeal")
@ErrorHelper
@Slf4j
public class UnifyTaskDealController {

    @Autowired
    private TaskDealService taskDealService;
    /**
     * （批量）任务处理-有审批流无关联检查表的任务
     *
     * @param enterpriseId
     * @param paramList
     * @return
     */
    @PostMapping(path = "/batch")
    @OperateLog(operateModule = CommonConstant.Function.TASK, operateType = CommonConstant.LOG_UPDATE, operateDesc = "批量处理任务")
    public ResponseResult batchDeal(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                      @RequestBody @Valid ValidList<DealParam> paramList) {
        log.info("#taskdeal batch body is ={}", JSON.toJSONString(paramList));
        DataSourceHelper.changeToMy();
        taskDealService.batchDeal(enterpriseId, paramList, UserHolder.getUser());
        return ResponseResult.success(true);
    }


    /**
     * 单个处理任务
     *
     * @param enterpriseId
     * @param dealTaskParam
     * @return
     */
    @ApiOperation("问题工单单个整改-审批")
    @PostMapping(path = "/dealTask")
    @OperateLog(operateModule = CommonConstant.Function.TASK, operateType = CommonConstant.LOG_UPDATE, operateDesc = "单个处理任务")
    public ResponseResult dealTask(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                    @RequestBody @Valid DealTaskParam dealTaskParam) {
        log.info("#dealTask body is ={}", JSON.toJSONString(dealTaskParam));
        DataSourceHelper.changeToMy();
        taskDealService.dealTask(enterpriseId, dealTaskParam, UserHolder.getUser());
        return ResponseResult.success(true);
    }

    /**
     * （批量）任务处理-有审批流无关联检查表的任务
     *
     * @param enterpriseId
     * @param paramList
     * @return
     */
    @ApiOperation("批量整改-审批工单")
    @PostMapping(path = "/batchDealQuestion")
    @OperateLog(operateModule = CommonConstant.Function.TASK, operateType = CommonConstant.LOG_UPDATE, operateDesc = "批量处理任务")
    public ResponseResult<Boolean> batchDealQuestion(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                    @RequestBody @Valid ValidList<DealTaskParam> paramList) {
        log.info("#batchDealQuestion#batch#body#is ={}", JSON.toJSONString(paramList));
        DataSourceHelper.changeToMy();
        taskDealService.batchDealQuestion(enterpriseId, paramList, UserHolder.getUser());
        return ResponseResult.success(true);
    }
}
