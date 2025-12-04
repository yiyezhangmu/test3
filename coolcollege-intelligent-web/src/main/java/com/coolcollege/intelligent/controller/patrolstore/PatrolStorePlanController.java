package com.coolcollege.intelligent.controller.patrolstore;

import com.coolcollege.intelligent.common.annotation.ErrorHelper;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.model.common.IdDTO;
import com.coolcollege.intelligent.model.patrolstore.dto.TbPatrolStorePeopleCountDTO;
import com.coolcollege.intelligent.model.patrolstore.dto.TbPatrolStorePeopleDTO;
import com.coolcollege.intelligent.model.patrolstore.dto.TbPatrolStorePlanAddDTO;
import com.coolcollege.intelligent.model.patrolstore.dto.TbPatrolStorePlanCountDTO;
import com.coolcollege.intelligent.model.userholder.UserHolder;
import com.coolcollege.intelligent.service.patrolstore.PatrolStorePlanService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author byd
 * @date 2023-07-11 14:28
 */
@Api(tags = "巡店计划")
@RestController
@RequestMapping("/v3/enterprises/{enterprise-id}/patrolStore/patrolStorePlan")
@ErrorHelper
@Slf4j
public class PatrolStorePlanController {

    @Autowired
    private PatrolStorePlanService patrolStorePlanService;


    /**
     * 巡店任务初始化
     *
     * @param enterpriseId 企业id
     * @param planDate     巡店日期
     * @return
     */
    @ApiOperation("巡店计划详情")
    @GetMapping(path = "/planInfo")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "planDate", value = "计划巡店日期 2023-07-14", required = true),
            @ApiImplicitParam(name = "userId", value = "用户id", required = true),
            @ApiImplicitParam(name = "longitude", value = "经度"),
            @ApiImplicitParam(name = "latitude", value = "维度")
    })
    public ResponseResult<TbPatrolStorePlanCountDTO> planInfo(@PathVariable(value = "enterprise-id") String enterpriseId,
                                                              @RequestParam("planDate") String planDate,
                                                              @RequestParam(value = "longitude", required = false) String longitude,
                                                              @RequestParam("userId") String userId,
                                                              @RequestParam(value = "latitude", required = false) String latitude) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(patrolStorePlanService.planInfo(enterpriseId, userId, planDate, longitude, latitude));
    }

    @ApiOperation("添加巡店计划门店")
    @PostMapping(path = "/addPlanStore")
    public ResponseResult<Boolean> addPlanStore(@PathVariable(value = "enterprise-id") String enterpriseId,
                                                @RequestBody TbPatrolStorePlanAddDTO patrolStorePlanAddDTO) {
        DataSourceHelper.changeToMy();
        patrolStorePlanService.addPlanStore(enterpriseId, UserHolder.getUser().getUserId(), patrolStorePlanAddDTO);
        return ResponseResult.success(Boolean.TRUE);
    }

    @ApiOperation("删除巡店计划门店")
    @PostMapping(path = "/removePlanStore")
    public ResponseResult<Boolean> removePlanStore(@PathVariable(value = "enterprise-id") String enterpriseId,
                                                   @RequestBody IdDTO idDTO) {
        DataSourceHelper.changeToMy();
        patrolStorePlanService.removePlanStore(enterpriseId, idDTO.getId());
        return ResponseResult.success(Boolean.TRUE);
    }

    @ApiOperation("管辖区域人员报表")
    @PostMapping(path = "/userRangeReportList")
    public ResponseResult<List<TbPatrolStorePeopleCountDTO>> userRangeReportList(@PathVariable(value = "enterprise-id") String enterpriseId,
                                                                                 @RequestBody TbPatrolStorePeopleDTO patrolStorePeopleDTO) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(patrolStorePlanService.userRangeReportList(enterpriseId, patrolStorePeopleDTO));
    }
}
