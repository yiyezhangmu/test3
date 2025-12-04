package com.coolcollege.intelligent.controller.patrolstore;

import com.coolcollege.intelligent.common.annotation.ErrorHelper;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.model.impoetexcel.ImportTaskDO;
import com.coolcollege.intelligent.model.patrolstore.query.SafetyCheckCountQuery;
import com.coolcollege.intelligent.model.safetycheck.vo.ScSafetyCheckCountVO;
import com.coolcollege.intelligent.model.userholder.UserHolder;
import com.coolcollege.intelligent.service.safetycheck.SafetyCheckCountService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author byd
 * @date 2023-08-17 14:22
 */
@Api(tags = "稽核执行力")
@RestController
@RequestMapping("/v3/enterprises/{enterprise-id}/patrolStore/safetyCheckCount")
@ErrorHelper
@Slf4j
public class SafetyCheckCountController {

    @Autowired
    private SafetyCheckCountService safetyCheckCountService;


    @ApiOperation("稽核执行力列表")
    @PostMapping(path = "/list")
    public ResponseResult<PageInfo<ScSafetyCheckCountVO>> list(@PathVariable(value = "enterprise-id") String enterpriseId,
                                                               @RequestBody SafetyCheckCountQuery query) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(safetyCheckCountService.list(enterpriseId, query));
    }

    @ApiOperation("稽核执行力列表-导出")
    @PostMapping(path = "/exportList")
    public ResponseResult<ImportTaskDO> exportList(@PathVariable(value = "enterprise-id") String enterpriseId,
                                             @RequestBody SafetyCheckCountQuery query) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(safetyCheckCountService.exportList(enterpriseId, query, UserHolder.getUser().getDbName()));
    }
}
