package com.coolcollege.intelligent.controller.achievement;

import com.coolcollege.intelligent.common.annotation.ErrorHelper;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.model.achievement.dto.AchievementTaskRecordDTO;
import com.coolcollege.intelligent.model.achievement.dto.AchievementTaskRecordDetailDTO;
import com.coolcollege.intelligent.model.unifytask.dto.AchievementTaskStoreSubmitDTO;
import com.coolcollege.intelligent.model.unifytask.query.AchievementTaskStoreQuery;
import com.coolcollege.intelligent.model.userholder.UserHolder;
import com.coolcollege.intelligent.service.achievement.AchievementTaskRecordService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author byd
 * @date 2024-03-16 17:28
 */
@Api(tags = "新品和旧品任务")
@Slf4j
@RestController
@ErrorHelper
@RequestMapping("/v3/enterprises/{enterprise-id}/achievement/taskRecord")
public class AchievementTaskRecordController {

    @Resource
    private AchievementTaskRecordService achievementTaskRecordService;


    @ApiOperation("新品和旧品门店任务详情")
    @GetMapping(path = "/storeTaskDetail")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "unifyTaskId", value = "父任务id", required = true, dataType = "Long"),
            @ApiImplicitParam(name = "storeId", value = "门店id", required = true, dataType = "String")
    })
    public ResponseResult<AchievementTaskRecordDetailDTO> storeTaskDetail(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                                                          @RequestParam(value = "unifyTaskId") Long unifyTaskId,
                                                                          @RequestParam(value = "storeId") String storeId) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(achievementTaskRecordService.storeTaskDetail(enterpriseId, unifyTaskId, storeId));
    }


    /**
     * 门店任务列表
     *
     * @param enterpriseId
     * @param query
     * @return
     */
    @ApiOperation("我的出样和撤样门店任务列表")
    @PostMapping(path = "/list")
    public ResponseResult<PageInfo<AchievementTaskRecordDTO>> list(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                                                   @RequestBody @Validated AchievementTaskStoreQuery query) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(achievementTaskRecordService.achievementMyStoreTaskList(enterpriseId, query, UserHolder.getUser().getUserId()));
    }


    @ApiOperation("出样和撤样门店任务提交")
    @PostMapping(path = "/submitTask")
    public ResponseResult<Boolean> submitTask(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                                                   @RequestBody @Validated AchievementTaskStoreSubmitDTO storeSubmitDTO) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(achievementTaskRecordService.submitTask(enterpriseId, storeSubmitDTO, UserHolder.getUser().getUserId(),
                UserHolder.getUser().getName()));
    }

    @ApiOperation("消息提醒")
    @PostMapping(path = "/sendRemindMsg")
    public ResponseResult<Boolean> sendRemindMsg(@PathVariable(value = "enterprise-id", required = false) String enterpriseId) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(achievementTaskRecordService.sendRemindMsg(enterpriseId));
    }
}
