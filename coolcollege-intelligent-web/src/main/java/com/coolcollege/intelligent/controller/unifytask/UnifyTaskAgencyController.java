package com.coolcollege.intelligent.controller.unifytask;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.coolcollege.intelligent.common.annotation.BaseResponse;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.model.unifytask.dto.CommissionTotalDTO;
import com.coolcollege.intelligent.model.unifytask.dto.TaskReminderDTO;
import com.coolcollege.intelligent.model.unifytask.query.QuestionQuery;
import com.coolcollege.intelligent.model.unifytask.query.TaskAgencyQuery;
import com.coolcollege.intelligent.model.unifytask.vo.PatrolPlanVO;
import com.coolcollege.intelligent.model.unifytask.vo.QuestionToDoVO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.model.userholder.UserHolder;
import com.coolcollege.intelligent.service.unifytask.UnifyTaskAgencyService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.github.pagehelper.PageInfo;
import io.etcd.jetcd.api.User;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * Description for this class
 *
 * @author : jixiang.jiang
 * @version : 1.0
 * @Description : Description for this class
 * @date ：Created in 2020/12/11 10:08
 */
@Api(tags = "待办")
@RestController
@RequestMapping("/v3/enterprises/{enterprise-id}/unifytask")
@BaseResponse
@Slf4j
public class UnifyTaskAgencyController {

    @Autowired
    private UnifyTaskAgencyService agencyService;

    /**
     * 代办
     * @param enterpriseId
     * @param query
     * @return
     */
    @PostMapping(path = "/agency")
    public ResponseResult getAgencyList(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                        @RequestBody @Validated TaskAgencyQuery query) throws Exception {
        PageInfo agencyList = new PageInfo();
        log.info("#agency body is ={}", JSON.toJSONString(query));
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        agencyList = agencyService.getTaskAgencyList(enterpriseId, query, user, Boolean.TRUE);
        return ResponseResult.success(agencyList);
    }

    /**
     * 按照多类型查询
     * @param enterpriseId
     * @param query
     * @return
     * @throws Exception
     */
    @PostMapping(path = "/getTodoTaskList")
    public ResponseResult getTodoTaskList(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                          @RequestBody @Validated TaskAgencyQuery query) {
        log.info("#getTodoTaskList body is ={}", JSON.toJSONString(query));
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        PageInfo agencyList = agencyService.getTaskAgencyList(enterpriseId, query, user, Boolean.TRUE);
        return ResponseResult.success(agencyList);
    }

    /**
     * 催办返回列表
     * @param enterpriseId
     * @param query
     * @return
     */
    @PostMapping(path = "/reminderList")
    public ResponseResult getReminderList(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                        @RequestBody @Validated TaskReminderDTO query) {
        PageInfo agencyList = new PageInfo();
        log.info("#agency body is ={}", JSON.toJSONString(query));
        CurrentUser user = UserHolder.getUser();
        query.setUserId(user.getUserId());
        DataSourceHelper.changeToMy();
        agencyList = agencyService.getReminderList(enterpriseId, query);
        return ResponseResult.success(agencyList);
    }

    /**
     * 催办返回列表
     * @param enterpriseId
     * @param query
     * @return
     */
    @ApiOperation("待办-工单列表")
    @GetMapping(path = "/questionToDoList")
    public ResponseResult<PageInfo<QuestionToDoVO>> questionToDoList(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                                                   @Validated QuestionQuery query) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(agencyService.questionToDoList(enterpriseId, query, UserHolder.getUser()));
    }

    @ApiOperation("待办-代办分类条数")
    @PostMapping(path = "/totalList")
    public ResponseResult<CommissionTotalDTO> totalList(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                                        @RequestBody @Validated TaskAgencyQuery query) {
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        String queryUserId = user.getUserId();
        if (StrUtil.isEmpty(query.getUserId())) {
            query.setUserId(user.getUserId());
        }
        CommissionTotalDTO totalDTO = agencyService.getTotal(enterpriseId, query);
        return ResponseResult.success(totalDTO);
    }



    @ApiOperation("巡店计划数据-根据人查询")
    @GetMapping(path = "/getPatrolPlanList")
    public ResponseResult<PageInfo<PatrolPlanVO>> getPatrolPlanList(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                                                    @RequestParam(name = "pageNum", required = false, defaultValue = "1") Integer pageNum,
                                                                    @RequestParam(name = "pageSize", required = false, defaultValue = "10") Integer pageSize,
                                                                    @RequestParam(name = "completeFlag", required = false, defaultValue = "0") Integer completeFlag) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(agencyService.getPatrolPlanList(enterpriseId, UserHolder.getUser(),completeFlag,pageSize,pageNum));
    }
}
