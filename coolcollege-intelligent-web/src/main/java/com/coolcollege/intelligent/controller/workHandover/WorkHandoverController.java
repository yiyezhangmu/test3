package com.coolcollege.intelligent.controller.workHandover;

import com.coolcollege.intelligent.common.annotation.ErrorHelper;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.model.userholder.UserHolder;
import com.coolcollege.intelligent.model.workHandover.request.WorkHandoverRequest;
import com.coolcollege.intelligent.model.workHandover.vo.WorkHandoverVO;
import com.coolcollege.intelligent.service.workHandover.WorkHandoverService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author byd
 * @date 2022-11-17 11:35
 */
@Api(tags = "工作交接")
@RestController
@RequestMapping("/v3/enterprises/{enterprise-id}/workHandover")
@ErrorHelper
@Slf4j
public class WorkHandoverController {

    @Autowired
    private WorkHandoverService workHandoverService;

    @ApiOperation("工作交接-查询列表（分页）")
    @GetMapping("/list")
    public ResponseResult<PageInfo<WorkHandoverVO>> list(@PathVariable("enterprise-id") String enterpriseId,
                                                         @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                                                         @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                                         @RequestParam(value = "name", required = false) String name) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(workHandoverService.list(enterpriseId, name, pageNum, pageSize));
    }

    @ApiOperation("工作交接-新建工作交接")
    @PostMapping("/addWorkHandover")
    public ResponseResult<Long> addWorkHandover(@PathVariable("enterprise-id") String enterpriseId,
                                                @Validated @RequestBody WorkHandoverRequest handoverRequest) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(workHandoverService.addWorkHandover(enterpriseId, handoverRequest, UserHolder.getUser().getUserId()));
    }

    @ApiOperation("工作交接-重新交接")
    @GetMapping("/againWorkHandover")
    public ResponseResult<Boolean> againWorkHandover(@PathVariable("enterprise-id") String enterpriseId,
                                                     @RequestParam Long workHandoverId) {
        DataSourceHelper.changeToMy();
        workHandoverService.againWorkHandover(enterpriseId, workHandoverId);
        return ResponseResult.success(Boolean.TRUE);
    }
}
