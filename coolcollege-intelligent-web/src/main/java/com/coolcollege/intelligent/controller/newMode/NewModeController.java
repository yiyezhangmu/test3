package com.coolcollege.intelligent.controller.newMode;

import com.coolcollege.intelligent.common.annotation.ErrorHelper;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.model.newMode.EnterpriseTypeRequest;
import com.coolcollege.intelligent.service.newMode.NewModeService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Api(tags = "新开通模式")
@ErrorHelper
@RequestMapping("/v3/enterprises/enterprise/newBelle")
@RestController
public class NewModeController {


    @Resource
    NewModeService newModeService;

    @ApiOperation(value = "获取当前企业的开通类型")
    @PostMapping("/getAppType")
    public ResponseResult<String> getAppType(@RequestBody EnterpriseTypeRequest request) {
        DataSourceHelper.reset();
        return ResponseResult.success(newModeService.getAppType(request));
    }


}
