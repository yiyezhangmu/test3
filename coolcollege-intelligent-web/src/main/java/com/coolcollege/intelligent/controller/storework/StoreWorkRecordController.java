package com.coolcollege.intelligent.controller.storework;

import com.coolcollege.intelligent.common.annotation.ErrorHelper;
import com.coolcollege.intelligent.common.annotation.SysLog;
import com.coolcollege.intelligent.common.enums.syslog.OpModuleEnum;
import com.coolcollege.intelligent.common.enums.syslog.OpTypeEnum;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseConfigMapper;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.impoetexcel.ImportTaskDO;
import com.coolcollege.intelligent.model.storework.request.StoreWorkRecordListRequest;
import com.coolcollege.intelligent.model.storework.vo.*;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.model.userholder.UserHolder;
import com.coolcollege.intelligent.service.storework.StoreWorkRecordService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author wxp
 * @date 2022-10-09 14:15
 */
@Api(tags = "店务记录")
@RestController
@RequestMapping("/v3/enterprises/{enterprise-id}/storeWorkRecord")
@ErrorHelper
@Slf4j
public class StoreWorkRecordController {

    @Resource
    private StoreWorkRecordService storeWorkRecordService;
    @Resource
    private EnterpriseConfigMapper configMapper;

    @ApiOperation("基础详情表-店务记录-列表(分页)")
    @PostMapping("/storeWorkRecordList")
    public ResponseResult<PageInfo<StoreWorkRecordVO>> storeWorkRecordList(@PathVariable("enterprise-id") String enterpriseId,
                                                                           @RequestBody StoreWorkRecordListRequest storeWorkRecordListRequest) {
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        return ResponseResult.success(storeWorkRecordService.storeWorkRecordList(enterpriseId, storeWorkRecordListRequest, user));
    }

    @ApiOperation("基础详情表-店务记录-列表详情(分页)")
    @PostMapping("/storeWorkRecordDetailList")
    public ResponseResult<PageInfo<SwStoreWorkRecordDetailVO>> storeWorkRecordDetailList(@PathVariable("enterprise-id") String enterpriseId,
                                                                           @RequestBody StoreWorkRecordListRequest storeWorkRecordListRequest) {
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        return ResponseResult.success(storeWorkRecordService.storeWorkRecordDetailList(enterpriseId, storeWorkRecordListRequest, user));
    }

    @ApiOperation("基础详情表-店务记录-列表详情(分页)")
    @PostMapping("/storeWorkRecordDetailListExport")
    @SysLog(func = "导出", opModule = OpModuleEnum.IMPORT_EXPORT, opType = OpTypeEnum.EXPORT, menus = "店务-基础详情表-店务记录")
    public ResponseResult<ImportTaskDO> storeWorkRecordDetailListExport(@PathVariable("enterprise-id") String enterpriseId,
                                                                                         @RequestBody StoreWorkRecordListRequest storeWorkRecordListRequest) {
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        return ResponseResult.success(storeWorkRecordService.storeWorkRecordDetailListExport(enterpriseId, storeWorkRecordListRequest, user));
    }

    @ApiOperation("基础详情表-店务记录-导出")
    @PostMapping("/storeWorkRecordListExport")
    @SysLog(func = "导出", opModule = OpModuleEnum.IMPORT_EXPORT, opType = OpTypeEnum.EXPORT, menus = "店务-基础详情表-店务记录")
    public ResponseResult<ImportTaskDO> storeWorkRecordListExport(@PathVariable("enterprise-id") String enterpriseId,
                                                                           @RequestBody StoreWorkRecordListRequest storeWorkRecordListRequest){
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        return ResponseResult.success(storeWorkRecordService.storeWorkRecordListExport(enterpriseId,storeWorkRecordListRequest,user));
    }

    @ApiOperation("基础详情表-检查表记录表-列表(分页)")
    @PostMapping("/storeWorkTableList")
    public ResponseResult<PageInfo<StoreWorkTableVO>> storeWorkTableList(@PathVariable("enterprise-id") String enterpriseId,
                                                                         @RequestBody StoreWorkRecordListRequest storeWorkRecordListRequest) {
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        return ResponseResult.success(storeWorkRecordService.storeWorkTableList(enterpriseId, storeWorkRecordListRequest,user));
    }

    @ApiOperation("基础详情表-检查表记录表-导出")
    @PostMapping("/storeWorkTableListExport")
    @SysLog(func = "导出", opModule = OpModuleEnum.IMPORT_EXPORT, opType = OpTypeEnum.EXPORT, menus = "店务-基础详情表-检查表记录")
    public ResponseResult<ImportTaskDO> storeWorkTableListExport(@PathVariable("enterprise-id") String enterpriseId,
                                                                  @RequestBody StoreWorkRecordListRequest storeWorkRecordListRequest){
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        return ResponseResult.success(storeWorkRecordService.storeWorkTableListExport(enterpriseId,storeWorkRecordListRequest,user));
    }

    @ApiOperation("基础详情表-检查项记录表-列表(分页)")
    @PostMapping("/storeWorkColumnList")
    public ResponseResult<PageInfo<StoreWorkColumnVO>> storeWorkColumnList(@PathVariable("enterprise-id") String enterpriseId,
                                                                          @RequestBody StoreWorkRecordListRequest storeWorkRecordListRequest) {
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        return ResponseResult.success(storeWorkRecordService.storeWorkColumnList(enterpriseId, storeWorkRecordListRequest,user));
    }

    @ApiOperation("基础详情表-检查项记录表-导出")
    @PostMapping("/storeWorkColumnListExport")
    @SysLog(func = "导出", opModule = OpModuleEnum.IMPORT_EXPORT, opType = OpTypeEnum.EXPORT, menus = "店务-基础详情表-检查项记录")
    public ResponseResult<ImportTaskDO> storeWorkColumnListExport(@PathVariable("enterprise-id") String enterpriseId,
                                                                 @RequestBody StoreWorkRecordListRequest storeWorkRecordListRequest){
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        return ResponseResult.success(storeWorkRecordService.storeWorkColumnListExport(enterpriseId,storeWorkRecordListRequest,user));
    }

    @ApiOperation("根据businessId获取店务基础信息")
    @GetMapping("/getStoreWorkBaseDetail")
    public ResponseResult<StoreWorkBaseDetailVO> getStoreWorkBaseDetail(@PathVariable("enterprise-id") String enterpriseId,
                                                                        @RequestParam(required = true,value = "businessId") String businessId) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(storeWorkRecordService.getStoreWorkBaseDetail(enterpriseId, businessId));
    }

    @ApiOperation("根据businessId查询店务是否是同一个执行人")
    @GetMapping("/theSameExecutor")
    public ResponseResult<SameExecutorInfoVO> theSameExecutor(@PathVariable("enterprise-id") String enterpriseId,
                                                              @RequestParam(required = true,value = "businessId") String businessId) {
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfigDO = configMapper.selectByEnterpriseId(enterpriseId);
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());
        return ResponseResult.success(storeWorkRecordService.theSameExecutor(enterpriseId, businessId));
    }

    /**
     * 店务详情分享时间设置
     */
    @ApiOperation("分享时间设置")
    @GetMapping(path = "/storeWorkRecordInfoShare")
    public ResponseResult storeWorkRecordInfoShare(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                          String businessId,String key) {
        return ResponseResult.success(storeWorkRecordService.storeWorkRecordInfoShare(enterpriseId, businessId,key));
    }

    @ApiOperation("是否过期")
    @GetMapping(path = "/storeWorkRecordExpired")
    public ResponseResult storeWorkRecordExpired(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                                   String businessId,String key) {
        return ResponseResult.success(storeWorkRecordService.storeWorkRecordExpired(enterpriseId, businessId,key));
    }

    @ApiOperation("查询店务记录工单跳转信息")
    @GetMapping(path = "/getStoreWorkRecordByDataColumnId")
    public ResponseResult getStoreWorkRecordByDataColumnId(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                                 @RequestParam(required = true,value = "dataColumnId") Long dataColumnId) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(storeWorkRecordService.getStoreWorkRecordByDataColumnId(enterpriseId, dataColumnId));
    }


}
