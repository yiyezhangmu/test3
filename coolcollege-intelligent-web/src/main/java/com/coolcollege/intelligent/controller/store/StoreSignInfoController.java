package com.coolcollege.intelligent.controller.store;

import com.coolcollege.intelligent.common.annotation.ErrorHelper;
import com.coolcollege.intelligent.common.annotation.SysLog;
import com.coolcollege.intelligent.common.enums.syslog.OpModuleEnum;
import com.coolcollege.intelligent.common.enums.syslog.OpTypeEnum;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.model.impoetexcel.ImportTaskDO;
import com.coolcollege.intelligent.model.store.dto.StoreSignInDTO;
import com.coolcollege.intelligent.model.store.dto.StoreSignInfoDTO;
import com.coolcollege.intelligent.model.store.dto.StoreSignOutDTO;
import com.coolcollege.intelligent.model.store.vo.StoreSignInfoVO;
import com.coolcollege.intelligent.model.unifytask.request.StoreTaskListRequest;
import com.coolcollege.intelligent.model.unifytask.request.StoreTaskReportListRequest;
import com.coolcollege.intelligent.model.unifytask.vo.StoreReportDetailVO;
import com.coolcollege.intelligent.model.unifytask.vo.StoreTaskDetailVO;
import com.coolcollege.intelligent.model.userholder.UserHolder;
import com.coolcollege.intelligent.service.store.StoreSignInfoService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author byd
 * @date 2023-05-18 14:23
 */
@Api(tags = "门店详情报告")
@RestController
@RequestMapping("/v3/enterprises/{enterprise-id}/storeSignInfo")
@ErrorHelper
@Slf4j
public class StoreSignInfoController {

    @Resource
    private StoreSignInfoService storeSignInfoService;


    @ApiOperation("获取门店签到信息")
    @GetMapping("/getStoreSignInfo")
    public ResponseResult<StoreSignInfoDTO> getStoreSignInfo(@PathVariable("enterprise-id") String enterpriseId,
                                                             @RequestParam(value = "signDate") String signDate,
                                                             @RequestParam(value = "storeId") String storeId) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(storeSignInfoService.getStoreSignInfo(enterpriseId, signDate, storeId, UserHolder.getUser().getUserId()));
    }


    @ApiOperation("门店签到")
    @PostMapping("/storeSignIn")
    public ResponseResult<StoreSignInDTO> storeSignIn(@PathVariable("enterprise-id") String enterpriseId,
                                                      @RequestBody StoreSignInDTO storeSignInDTO) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(storeSignInfoService.storeSignIn(enterpriseId, storeSignInDTO, UserHolder.getUser()));
    }

    @ApiOperation("门店签退")
    @PostMapping("/storeSignOut")
    public ResponseResult<StoreSignOutDTO> storeSignOut(@PathVariable("enterprise-id") String enterpriseId,
                                                        @RequestBody StoreSignOutDTO storeSignOutDTO) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(storeSignInfoService.storeSignOut(enterpriseId, storeSignOutDTO, UserHolder.getUser()));
    }

    @ApiOperation("门店任务列表")
    @PostMapping(path = "/task/list")
    public ResponseResult<StoreTaskDetailVO> taskList(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                                      @RequestBody @Validated StoreTaskListRequest query) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(storeSignInfoService.taskList(enterpriseId, query, UserHolder.getUser().getUserId()));
    }

    @ApiOperation("巡店报表列表")
    @GetMapping(path = "/report/list")
    public  ResponseResult<PageInfo<StoreSignInfoVO>>reportList(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                                   @Validated StoreTaskReportListRequest reportListRequest) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(storeSignInfoService.reportList(enterpriseId, reportListRequest));
    }


    @ApiOperation("巡店报表列表-导出")
    @GetMapping(path = "/report/exportList")
    @SysLog(func = "导出", opModule = OpModuleEnum.IMPORT_EXPORT, opType = OpTypeEnum.EXPORT, menus = "巡店-基础报表-巡店报告")
    public ResponseResult<ImportTaskDO> exportList(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                                                @Validated StoreTaskReportListRequest reportListRequest) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(storeSignInfoService.exportReportList(enterpriseId, reportListRequest, UserHolder.getUser().getDbName()));

    }

    @ApiOperation("巡店报表详情")
    @GetMapping(path = "/report/detail")
    public ResponseResult<StoreReportDetailVO> reportDetail(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                                            @RequestParam("id") Long id) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(storeSignInfoService.reportDetail(enterpriseId, id));
    }
}
