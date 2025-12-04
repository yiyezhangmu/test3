package com.coolcollege.intelligent.controller.unifytask;

import com.coolcollege.intelligent.common.annotation.BaseResponse;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.model.unifytask.dto.ParentTaskDTO;
import com.coolcollege.intelligent.model.unifytask.query.DisplayQuery;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.model.userholder.UserHolder;
import com.coolcollege.intelligent.service.unifytask.BackgroundUnifyTaskService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Description for this class
 *
 * @author : jixiang.jiang
 * @version : 1.0
 * @Description : Description for this class
 * @date ：Created in 2020/10/29 16:50
 */
@Api(tags = "PC端任务接口")
@RestController
@RequestMapping({"/v2/enterprises/{enterprise-id}/background/unify",
        "/v3/enterprises/{enterprise-id}/background/unify"})
@BaseResponse
@Slf4j
public class BackgroundUnifyTaskController {

    @Autowired
    private BackgroundUnifyTaskService backgroundTaskService;

    @ApiOperation(value = "父任务列表", notes = "1.nodeType:我创建的create,我收到的/抄送我的cc,(新)我处理的approval")
    @PostMapping(path = "/parent/statistics/list")
    public ResponseResult getBackgroundTaskStatisticsList(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                                          @RequestBody @Validated DisplayQuery query) {
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        ParentTaskDTO task = backgroundTaskService.getBackgroundParentList(enterpriseId, query, user);
        return ResponseResult.success(task);
    }

    @PostMapping(path = "/sub/statistics/list")
    public ResponseResult getBackgroundTaskSubStatisticsList(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                        @RequestBody @Validated DisplayQuery query) {
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        return ResponseResult.success(backgroundTaskService.getBackgroundTaskSubStatisticsList(enterpriseId, query, user));
    }
}

