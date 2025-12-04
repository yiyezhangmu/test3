package com.coolcollege.intelligent.controller.newstore;

import com.coolcollege.intelligent.common.annotation.ErrorHelper;
import com.coolcollege.intelligent.common.annotation.OperateLog;
import com.coolcollege.intelligent.common.constant.CommonConstant;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.model.newstore.request.*;
import com.coolcollege.intelligent.model.newstore.vo.*;
import com.coolcollege.intelligent.model.userholder.UserHolder;
import com.coolcollege.intelligent.service.newstore.NsVisitRecordService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 新店拜访记录
 * @author zhangnan
 * @date 2022-03-04 17:26
 */
@Api(tags = "新店拜访记录")
@ErrorHelper
@RequestMapping("/v3/enterprises/{enterprise-id}/nsVisitRecord")
@RestController
public class NsVisitRecordController {

    @Resource
    private NsVisitRecordService nsVisitRecordService;

    @ApiOperation(value = "拜访签到")
    @PostMapping("/signIn")
    @OperateLog(operateModule = CommonConstant.Function.NSSTORE, operateType = CommonConstant.LOG_ADD, operateDesc = "拜访签到")
    public ResponseResult<NsVisitSignInVO> signIn(@PathVariable(value = "enterprise-id") String enterpriseId, @RequestBody NsVisitSignInRequest request) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(nsVisitRecordService.signIn(enterpriseId, request, UserHolder.getUser()));
    }

    @ApiOperation(value = "拜访记录-根据新店id查询当日进行中")
    @GetMapping("/getTodayOngoingRecord")
    public ResponseResult<NsTodayOngoingRecordVO> getTodayOngoingRecord(@PathVariable(value = "enterprise-id") String enterpriseId, @RequestParam Long newStoreId) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(nsVisitRecordService.getTodayOngoingRecord(enterpriseId, newStoreId, UserHolder.getUser().getUserId()));
    }

    @ApiOperation(value = "拜访记录更新")
    @PostMapping("/updateRecord")
    @OperateLog(operateModule = CommonConstant.Function.NSSTORE, operateType = CommonConstant.LOG_UPDATE, operateDesc = "拜访记录更新")
    public ResponseResult updateRecord(@PathVariable("enterprise-id") String enterpriseId, @RequestBody NsVisitRecordUpdateRequest request) {
        DataSourceHelper.changeToMy();
        nsVisitRecordService.updateRecord(enterpriseId, request, UserHolder.getUser());
        return ResponseResult.success();
    }


    @ApiOperation(value = "拜访记录提交")
    @PostMapping("/submitRecord")
    @OperateLog(operateModule = CommonConstant.Function.NSSTORE, operateType = CommonConstant.LOG_UPDATE, operateDesc = "拜访记录提交")
    public ResponseResult submitRecord(@PathVariable("enterprise-id") String enterpriseId, @RequestBody NsVisitRecordSubmitRequest request) {
        DataSourceHelper.changeToMy();
        nsVisitRecordService.submitRecord(enterpriseId, request, UserHolder.getUser());
        return ResponseResult.success();
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "recordId", value = "拜访记录id", dataType = "Long", required = true, example = "1"),
    })
    @ApiOperation(value = "拜访表信息查询", notes = "拜访内容回显，拜访记录详情中的拜访数据都使用此接口")
    @GetMapping("/getVisitTableInfoByRecordId")
    public ResponseResult<NsVisitTableInfoVO> getVisitTableInfoByRecordId(@PathVariable("enterprise-id") String enterpriseId, @RequestParam Long recordId) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(nsVisitRecordService.getVisitTableInfoByRecordId(enterpriseId, recordId));
    }

    @ApiOperation(value = "拜访表信息保存")
    @PostMapping("/saveVisitTableInfo")
    @OperateLog(operateModule = CommonConstant.Function.NSSTORE, operateType = CommonConstant.LOG_UPDATE, operateDesc = "拜访表信息保存")
    public ResponseResult saveVisitTableInfo(@PathVariable("enterprise-id") String enterpriseId, @RequestBody NsDataVisitTableColumnSaveRequest request) {
        DataSourceHelper.changeToMy();
        nsVisitRecordService.saveVisitTableInfo(enterpriseId, request, UserHolder.getUser());
        return ResponseResult.success();
    }

    @ApiOperation(value = "拜访记录分页查询")
    @GetMapping("/getRecordList")
    public ResponseResult<PageInfo<NsVisitRecordListVO>> getRecordList(@PathVariable("enterprise-id") String enterpriseId, @Validated NsVisitRecordListRequest request) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(nsVisitRecordService.getRecordList(enterpriseId, request));
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "recordId", value = "拜访记录id", dataType = "Long", required = true, example = "1"),
    })
    @ApiOperation(value = "拜访记录详情")
    @GetMapping("/getRecordDetail")
    public ResponseResult<NsVisitRecordDetailVO> getRecordDetail(@PathVariable("enterprise-id") String enterpriseId, @RequestParam Long recordId) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(nsVisitRecordService.getRecordDetail(enterpriseId, recordId));
    }

}
