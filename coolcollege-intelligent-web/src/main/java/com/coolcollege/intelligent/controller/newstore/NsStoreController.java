package com.coolcollege.intelligent.controller.newstore;

import com.coolcollege.intelligent.common.annotation.ErrorHelper;
import com.coolcollege.intelligent.common.annotation.OperateLog;
import com.coolcollege.intelligent.common.constant.CommonConstant;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.model.newstore.request.NsBatchHandoverRequest;
import com.coolcollege.intelligent.model.newstore.request.NsStoreGetStatisticsRequest;
import com.coolcollege.intelligent.model.newstore.request.NsStoreAddOrUpdateRequest;
import com.coolcollege.intelligent.model.newstore.request.NsStoreListRequest;
import com.coolcollege.intelligent.model.newstore.vo.NsHandoverHistoryVO;
import com.coolcollege.intelligent.model.newstore.vo.NsStoreGetStatisticsVO;
import com.coolcollege.intelligent.model.newstore.vo.NsStoreVO;
import com.coolcollege.intelligent.model.page.PageBaseRequest;
import com.coolcollege.intelligent.model.userholder.UserHolder;
import com.coolcollege.intelligent.service.newstore.NsStoreService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 新店管理
 * @author zhangnan
 * @date 2022-03-04 17:26
 */
@Api(tags = "新店管理")
@ErrorHelper
@RequestMapping("/v3/enterprises/{enterprise-id}/nsStore")
@RestController
public class NsStoreController {

    @Resource
    private NsStoreService nsStoreService;

    @ApiOperation(value = "新店-批量交接")
    @PostMapping("/batchHandOver")
    public ResponseResult batchHandOver(@PathVariable(value = "enterprise-id") String enterpriseId, @RequestBody NsBatchHandoverRequest request) {
        DataSourceHelper.changeToMy();
        nsStoreService.batchHandOver(enterpriseId, request, UserHolder.getUser());
        return ResponseResult.success();
    }

    @ApiOperation(value = "新店-交接记录")
    @GetMapping("/getHandOverHistoryList")
    public ResponseResult<PageInfo<NsHandoverHistoryVO>> getHandOverHistoryList(@PathVariable(value = "enterprise-id") String enterpriseId, PageBaseRequest request) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(nsStoreService.getHandOverHistoryList(enterpriseId, request.getPageNum(), request.getPageSize()));
    }

    @ApiOperation(value = "新店-分析表")
    @GetMapping("/getStatistics")
    public ResponseResult<List<NsStoreGetStatisticsVO>> getStatistics(@PathVariable("enterprise-id") String enterpriseId, @Validated NsStoreGetStatisticsRequest request) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(nsStoreService.getStatistics(enterpriseId, request));
    }

    @ApiOperation(value = "新店分页查询")
    @GetMapping("/getNsStoreList")
    public ResponseResult<PageInfo<NsStoreVO>> getNsStoreList(@PathVariable("enterprise-id") String enterpriseId, @Validated NsStoreListRequest request) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(nsStoreService.getNsStoreList(enterpriseId, request));
    }

    @ApiOperation(value = "新店-新增")
    @PostMapping("/add")
    public ResponseResult addNsStore(@PathVariable(value = "enterprise-id") String enterpriseId,
                           @RequestBody NsStoreAddOrUpdateRequest nsStoreAddOrUpdateRequest) {
        DataSourceHelper.changeToMy();
        nsStoreService.addNsStore(enterpriseId, nsStoreAddOrUpdateRequest, UserHolder.getUser());
        return ResponseResult.success();
    }

    @ApiOperation(value = "新店-编辑")
    @PostMapping("/update")
    @OperateLog(operateModule = CommonConstant.Function.NSSTORE, operateType = CommonConstant.LOG_UPDATE, operateDesc = "新店编辑")
    public ResponseResult updateNsStore(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                              @RequestBody NsStoreAddOrUpdateRequest nsStoreAddOrUpdateRequest) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(nsStoreService.updateNsStore(enterpriseId, nsStoreAddOrUpdateRequest, UserHolder.getUser()));
    }

    @ApiOperation(value = "新店-删除")
    @DeleteMapping("/delete")
    public ResponseResult deleteNsStoreById(@PathVariable(value = "enterprise-id") String eid,
                                             @RequestParam(required = true, name = "id") Long id){
        DataSourceHelper.changeToMy();
        nsStoreService.deleteNsStoreById(eid, id);
        return ResponseResult.success();
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "新店id", dataType = "Long", required = true, example = "1"),
    })
    @ApiOperation(value = "新店详情")
    @GetMapping("/get")
    public ResponseResult<NsStoreVO> getNsStoreDetailById(@PathVariable("enterprise-id") String enterpriseId, @RequestParam Long id) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(nsStoreService.getNsStoreDetailById(enterpriseId, id));
    }


}
