package com.coolcollege.intelligent.controller.supervison;

import com.coolcollege.intelligent.common.annotation.ErrorHelper;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.facade.dto.openApi.OpenApiUpdateSupervisionTaskDTO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.supervision.request.SupervisionTaskHandleRequest;
import com.coolcollege.intelligent.model.supervision.request.SupervisionTaskQueryRequest;
import com.coolcollege.intelligent.model.supervision.vo.SupervisionDataColumnVO;
import com.coolcollege.intelligent.model.supervision.vo.SupervisionTaskVO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.model.userholder.UserHolder;
import com.coolcollege.intelligent.service.enterprise.EnterpriseConfigService;
import com.coolcollege.intelligent.service.supervison.SupervisionDefDataColumnService;
import com.coolcollege.intelligent.service.supervison.SupervisionTaskService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * @author
 * @date 2023-02-01 14:15
 */
@Api(tags = "督导助手数据表")
@RestController
@RequestMapping("/v3/enterprises/{enterprise-id}/supervision/dataColumn")
@ErrorHelper
@Slf4j
public class SupervisionDefDataColumnController {

    @Resource
    SupervisionDefDataColumnService supervisionDefDataColumnService;
    @Resource
    private EnterpriseConfigService enterpriseConfigService;



    @ApiOperation("获取表单数据")
    @GetMapping(path = "/getSupervisionDataColumn")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "taskId", value = "按人任务ID 或者按门店任务ID", required = false),
            @ApiImplicitParam(name = "formId", value = "表单ID", required = false),
            @ApiImplicitParam(name = "type", value = "按人任务 person  按店任务 store", required = false)
    })
    public ResponseResult<SupervisionDataColumnVO> getSupervisionDataColumn(@PathVariable(value = "enterprise-id") String enterpriseId,
                                                              @RequestParam(name = "taskId",required = true)Long taskId,
                                                              @RequestParam(name = "formId",required = true)String formId,
                                                              @RequestParam(name = "type",required = true)String type) {
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigService.selectByEnterpriseId(enterpriseId);
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());
        return ResponseResult.success(supervisionDefDataColumnService.getSupervisionDataColumn(enterpriseId, taskId,formId,type));
    }

}
