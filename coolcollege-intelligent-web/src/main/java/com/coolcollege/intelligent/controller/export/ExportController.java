package com.coolcollege.intelligent.controller.export;

import com.coolcollege.intelligent.common.annotation.ErrorHelper;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.model.enums.DisplayDynamicFieldsEnum;
import com.coolcollege.intelligent.model.enums.ExportServiceEnum;
import com.coolcollege.intelligent.model.export.request.*;
import com.coolcollege.intelligent.model.impoetexcel.ImportTaskDO;
import com.coolcollege.intelligent.model.license.LicenseExportRequest;
import com.coolcollege.intelligent.model.license.StoreLicenseExportRequest;
import com.coolcollege.intelligent.model.patrolstore.query.PatrolStoreStatisticsDataTableQuery;
import com.coolcollege.intelligent.model.userholder.UserHolder;
import com.coolcollege.intelligent.service.export.ExportUtil;
import com.coolcollege.intelligent.service.export.impl.DisplayHasPicExportService;
import com.coolcollege.intelligent.service.patrolstore.PatrolStoreService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author shuchang.wei
 * @date 2021/6/4 15:30
 */
@Api(tags = "导出接口")
@ErrorHelper
@RestController
@RequestMapping("/v3/enterprises/{enterprise-id}/export/export")
public class ExportController {
    @Resource
    private ExportUtil exportUtil;
    @Resource
    private DisplayHasPicExportService displayHasPicExportService;

    @Resource
    private PatrolStoreService patrolStoreService;


    /**
     * 快捷检查项导出
     * @param enterpriseId 企业id
     * @param request 请求参数
     * @return
     */
    @PostMapping("/quickColumnExport")
    public ResponseResult<ImportTaskDO> quickColumnExport(@PathVariable("enterprise-id") String enterpriseId, @RequestBody FileExportBaseRequest request) {
        request.setExportServiceEnum(ExportServiceEnum.EXPORT_QUICK_COLUMN);
        ImportTaskDO importTaskDO = exportUtil.exportFile(enterpriseId, request, UserHolder.getUser().getDbName());
        return ResponseResult.success(importTaskDO);
    }



    /**
     * 陈列数据导出
     * @param enterpriseId
     * @param request
     * @return
     */
    @PostMapping("displayRecordExport")
    public ResponseResult<ImportTaskDO> displayRecordExport(@PathVariable("enterprise-id") String enterpriseId,
                                                        @RequestBody DisplayRecordExportRequest request){
        request.setExportServiceEnum(ExportServiceEnum.EXPORT_DISPLAY_RECORD);
        if(CollectionUtils.isEmpty(request.getFieldList())){
            request.setFieldList(DisplayDynamicFieldsEnum.nameList());
        }
        ImportTaskDO importTaskDO = exportUtil.exportFile(enterpriseId, request, UserHolder.getUser().getDbName());
        return ResponseResult.success(importTaskDO);
    }


    /**
     * 陈列详情导出
     */
    @PostMapping("displayDetailExport")
    public ResponseResult<ImportTaskDO> displayDetailExport(@PathVariable("enterprise-id") String enterpriseId,@RequestBody DisplayRecordExportRequest request){
        request.setExportServiceEnum(ExportServiceEnum.EXPORT_DISPLAY_RECORD);
        if(CollectionUtils.isEmpty(request.getFieldList())){
            request.setFieldList(DisplayDynamicFieldsEnum.nameList());
        }
        ImportTaskDO importTaskDO = exportUtil.exportFile(enterpriseId, request, UserHolder.getUser().getDbName());
        return ResponseResult.success(importTaskDO);
    }


    /**
     * 新店列表导出
     * @param enterpriseId
     * @param request
     * @return
     */
    @ApiOperation(value = "新店列表导出")
    @GetMapping("/newStoreListExport")
    public ResponseResult<ImportTaskDO> newStoreListExport(@PathVariable("enterprise-id") String enterpriseId, @Validated NsStoreListExportRequest request){
        request.setExportServiceEnum(ExportServiceEnum.EXPORT_NEW_STORE_LIST);
        if(CollectionUtils.isEmpty(request.getFieldList())){
            request.setFieldList(DisplayDynamicFieldsEnum.nameList());
        }
        ImportTaskDO importTaskDO = exportUtil.exportFile(enterpriseId, request, UserHolder.getUser().getDbName());
        return ResponseResult.success(importTaskDO);
    }

    /**
     * 拜访记录导出
     * @param enterpriseId
     * @param request
     * @return
     */
    @ApiOperation(value = "拜访记录导出")
    @GetMapping("/visitRecordListExport")
    public ResponseResult<ImportTaskDO> visitRecordListExport(@PathVariable("enterprise-id") String enterpriseId,@Validated NsVisitRecordListExportRequest request){
        request.setExportServiceEnum(ExportServiceEnum.EXPORT_VISIT_RECORD_LIST);
        if(CollectionUtils.isEmpty(request.getFieldList())){
            request.setFieldList(DisplayDynamicFieldsEnum.nameList());
        }
        ImportTaskDO importTaskDO = exportUtil.exportFile(enterpriseId, request, UserHolder.getUser().getDbName());
        return ResponseResult.success(importTaskDO);
    }

    /**
     * 新店分析表导出
     * @param enterpriseId
     * @param request
     * @return
     */
    @ApiOperation(value = "新店分析表导出")
    @GetMapping("/newStoreStatisticsExport")
    public ResponseResult<ImportTaskDO> newStoreStatisticsExport(@PathVariable("enterprise-id") String enterpriseId, @Validated NsStoreExportStatisticsRequest request){
        request.setExportServiceEnum(ExportServiceEnum.EXPORT_NEW_STORE_STATISTICS);
        if(CollectionUtils.isEmpty(request.getFieldList())){
            request.setFieldList(DisplayDynamicFieldsEnum.nameList());
        }
        ImportTaskDO importTaskDO = exportUtil.exportFile(enterpriseId, request, UserHolder.getUser().getDbName());
        return ResponseResult.success(importTaskDO);
    }


    @ApiOperation(value = "复审列表导出")
    @PostMapping("/patrolStoreReviewListExport")
    public ResponseResult<ImportTaskDO> patrolStoreReviewListExport(@PathVariable("enterprise-id") String enterpriseId,@RequestBody PatrolStoreStatisticsDataTableQuery query){
        DataSourceHelper.changeToMy();
        return ResponseResult.success(patrolStoreService.patrolStoreReviewListExport(UserHolder.getUser(),enterpriseId,query));
    }

    @ApiOperation(" 门店证照导出")
    @PostMapping("/storeLicenseReportExport")
    public ResponseResult<ImportTaskDO> storeLicenseReportExport(@PathVariable("enterprise-id") String enterpriseId,@RequestBody LicenseExportRequest request){
        DataSourceHelper.changeToMy();
        request.setExportServiceEnum(ExportServiceEnum.STORE_LICENSE_REPORT);
        ImportTaskDO importTaskDO = exportUtil.exportFile(enterpriseId, request, UserHolder.getUser().getDbName());
        return ResponseResult.success(importTaskDO);
    }

    @ApiOperation(" 门店证照导出")
    @PostMapping("/storeLicenseExport")
    public ResponseResult<ImportTaskDO> storeLicenseExport(@PathVariable("enterprise-id") String enterpriseId,@RequestBody StoreLicenseExportRequest request){
        DataSourceHelper.changeToMy();
        request.setExportServiceEnum(ExportServiceEnum.STORE_LICENSE_EXPORT);
        request.setEnterpriseId(enterpriseId);
        ImportTaskDO importTaskDO = exportUtil.exportFile(enterpriseId, request, UserHolder.getUser().getDbName());
        return ResponseResult.success(importTaskDO);
    }

    @ApiOperation(" 用户证照导出")
    @PostMapping("/userLicenseReportExport")
    public ResponseResult<ImportTaskDO> userLicenseReportExport(@PathVariable("enterprise-id") String enterpriseId,@RequestBody LicenseExportRequest request){
        DataSourceHelper.changeToMy();
        request.setExportServiceEnum(ExportServiceEnum.USER_LICENSE_REPORT);
        ImportTaskDO importTaskDO = exportUtil.exportFile(enterpriseId, request, UserHolder.getUser().getDbName());
        return ResponseResult.success(importTaskDO);
    }

    @ApiOperation(" 用户证照导出V2")
    @PostMapping("/userLicenseExport")
    public ResponseResult<ImportTaskDO> userLicenseExport(@PathVariable("enterprise-id") String enterpriseId,@RequestBody LicenseExportRequest request){
        DataSourceHelper.changeToMy();
        request.setExportServiceEnum(ExportServiceEnum.USER_LICENSE_EXPORT);
        ImportTaskDO importTaskDO = exportUtil.exportFile(enterpriseId, request, UserHolder.getUser().getDbName());
        return ResponseResult.success(importTaskDO);
    }
}
