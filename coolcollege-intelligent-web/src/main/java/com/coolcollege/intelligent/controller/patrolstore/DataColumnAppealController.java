package com.coolcollege.intelligent.controller.patrolstore;

import com.coolcollege.intelligent.common.annotation.ErrorHelper;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseConfigMapper;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.patrolstore.dto.BatchDataColumnAppealDTO;
import com.coolcollege.intelligent.model.patrolstore.dto.TbDataColumnAppealHistoryDTO;
import com.coolcollege.intelligent.model.patrolstore.dto.TbDataColumnAppealListDTO;
import com.coolcollege.intelligent.model.patrolstore.param.DataColumnAppealParam;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.model.userholder.UserHolder;
import com.coolcollege.intelligent.service.patrolstore.DataColumnAppealService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author byd
 * @date 2023-07-11 14:28
 */
@Api(tags = "稽核申诉")
@RestController
@RequestMapping("/v3/enterprises/{enterprise-id}/patrolStore/dataColumnAppeal")
@ErrorHelper
@Slf4j
public class DataColumnAppealController {

    @Resource
    private DataColumnAppealService dataColumnAppealService;

    @Resource
    private EnterpriseConfigMapper configMapper;

    @ApiOperation("申诉")
    @PostMapping(path = "/appeal")
    public ResponseResult<Boolean> appeal(@PathVariable(value = "enterprise-id") String enterpriseId,
                                          @RequestBody BatchDataColumnAppealDTO batchDataColumnAppealDTO) {
        DataSourceHelper.changeToMy();
        dataColumnAppealService.appeal(enterpriseId, UserHolder.getUser().getUserId(), UserHolder.getUser().getName(), batchDataColumnAppealDTO);
        return ResponseResult.success(Boolean.TRUE);
    }


    @ApiOperation("申诉记录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "businessId", value = "巡店记录id", required = true)
    })
    @GetMapping(path = "/appealList")
    public ResponseResult<List<TbDataColumnAppealListDTO>> appealList(@PathVariable(value = "enterprise-id") String enterpriseId,
                                                                      @RequestParam("businessId") Long businessId) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(dataColumnAppealService.appealList(enterpriseId, UserHolder.getUser().getUserId(), businessId));
    }

    @ApiOperation("申诉审批")
    @PostMapping(path = "/appealApprove")
    public ResponseResult<Boolean> appealApprove(@PathVariable(value = "enterprise-id") String enterpriseId,
                                                                         @RequestBody DataColumnAppealParam dataColumnAppealParam) {
        DataSourceHelper.reset();
        EnterpriseConfigDO config = configMapper.selectByEnterpriseId(enterpriseId);
        DataSourceHelper.changeToMy();
        dataColumnAppealService.appealApprove(enterpriseId, UserHolder.getUser().getUserId(), UserHolder.getUser().getName(), dataColumnAppealParam, config.getDingCorpId(), config.getAppType());
        return ResponseResult.success(true);
    }


    @ApiOperation("申诉历史")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "dataColumnId", value = "巡店数据项id", required = true)
    })
    @GetMapping(path = "/appealHistoryList")
    public ResponseResult<List<TbDataColumnAppealHistoryDTO>> appealHistoryList(@PathVariable(value = "enterprise-id") String enterpriseId,
                                                                                @RequestParam(value = "dataColumnId") Long dataColumnId) {
        DataSourceHelper.reset();
        EnterpriseConfigDO config = configMapper.selectByEnterpriseId(enterpriseId);
        DataSourceHelper.changeToSpecificDataSource(config.getDbName());
        return ResponseResult.success(dataColumnAppealService.appealHistoryList(enterpriseId, dataColumnId));
    }


    @ApiOperation("申诉详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "appealId", value = "申诉记录id", required = true)
    })
    @GetMapping(path = "/appealDetail")
    public ResponseResult<TbDataColumnAppealListDTO> appealDetail(@PathVariable(value = "enterprise-id") String enterpriseId,
                                                                      @RequestParam("appealId") Long appealId) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(dataColumnAppealService.appealDetail(enterpriseId, UserHolder.getUser().getUserId(), appealId));
    }
}
