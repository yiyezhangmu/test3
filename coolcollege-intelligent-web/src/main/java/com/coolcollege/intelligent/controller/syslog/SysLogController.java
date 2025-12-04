package com.coolcollege.intelligent.controller.syslog;

import com.coolcollege.intelligent.common.annotation.ErrorHelper;
import com.coolcollege.intelligent.common.enums.syslog.OpModuleEnum;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.model.syslog.request.SysLogRequest;
import com.coolcollege.intelligent.service.syslog.SysLogService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * describe: 系统日志前端控制器
 *
 * @author wangff
 * @date 2025/2/7
 */
@Api(tags = "系统日志")
@RestController
@RequestMapping("/v3/enterprises/{enterprise-id}/syslog")
@ErrorHelper
@Slf4j
public class SysLogController {

    @Resource
    private SysLogService sysLogService;

    @ApiOperation("分页查询")
    @PostMapping("/getPage")
    public ResponseResult getPage(@PathVariable("enterprise-id") String enterpriseId, @RequestBody SysLogRequest request) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(sysLogService.getPage(enterpriseId, request));
    }

    @ApiOperation("模块列表")
    @GetMapping("/allModule")
    public ResponseResult allModule() {
        return ResponseResult.success(OpModuleEnum.allModule());
    }
}
