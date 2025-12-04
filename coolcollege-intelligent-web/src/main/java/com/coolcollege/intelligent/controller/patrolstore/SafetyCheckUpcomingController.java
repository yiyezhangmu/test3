package com.coolcollege.intelligent.controller.patrolstore;

import cn.hutool.core.util.StrUtil;
import com.coolcollege.intelligent.common.annotation.ErrorHelper;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.model.safetycheck.vo.ScSafetyCheckUpcomingVO;
import com.coolcollege.intelligent.model.unifytask.query.TaskAgencyQuery;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.model.userholder.UserHolder;
import com.coolcollege.intelligent.service.safetycheck.SafetyCheckUpcomingService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author byd
 * @date 2023-08-17 14:22
 */
@Api(tags = "稽核待办")
@RestController
@RequestMapping("/v3/enterprises/{enterprise-id}/patrolStore/safetyCheckUpcoming")
@ErrorHelper
@Slf4j
public class SafetyCheckUpcomingController {

    @Resource
    private SafetyCheckUpcomingService safetyCheckUpcomingService;


    @ApiOperation("稽核待办列表-待我处理")
    @PostMapping(path = "/appealList")
    public ResponseResult<PageInfo<ScSafetyCheckUpcomingVO>> appealList(@PathVariable(value = "enterprise-id") String enterpriseId,
                                                                        @RequestBody @Validated TaskAgencyQuery query) {
        CurrentUser user = UserHolder.getUser();
        if (StrUtil.isEmpty(query.getUserId())) {
            query.setUserId(user.getUserId());
        }
        DataSourceHelper.changeToMy();
        return ResponseResult.success(safetyCheckUpcomingService.safetyCheckUpcomingList(enterpriseId, query.getUserId(), query.getStoreIdList(), query.getPageNumber(), query.getPageSize()));
    }
}
