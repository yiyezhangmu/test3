package com.coolcollege.intelligent.controller.passengerflow;

import com.coolcollege.intelligent.common.annotation.ErrorHelper;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.model.passengerflow.request.PassengerNewBoardRequest;
import com.coolcollege.intelligent.model.passengerflow.request.PassengerStoreDayRequest;
import com.coolcollege.intelligent.model.passengerflow.vo.PassengerDeviceHourDayVO;
import com.coolcollege.intelligent.model.passengerflow.vo.PassengerGroupVO;
import com.coolcollege.intelligent.model.passengerflow.vo.PassengerStoreRankVO;
import com.coolcollege.intelligent.model.passengerflow.vo.PassengerTrendVO;
import com.coolcollege.intelligent.service.passengerflow.PassengerFlowService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * @author wxp
 * @date 2024-09-19 14:15
 */
@Api(tags = "新客流看板")
@RestController
@RequestMapping("/v3/enterprises/{enterprise-id}/passengerNew")
@ErrorHelper
@Slf4j
public class PassengerFlowNewController {

    @Autowired
    private PassengerFlowService passengerFlowService;

    @ApiOperation("客流实况总览")
    @PostMapping("/getPassengerFlowOverview")
    public ResponseResult<PassengerDeviceHourDayVO> getPassengerFlowOverview(@PathVariable("enterprise-id") String enterpriseId,
                                                                          @RequestBody PassengerNewBoardRequest request) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(passengerFlowService.getPassengerFlowOverview(enterpriseId, request));
    }

    @ApiOperation("客流趋势图")
    @PostMapping("/trend")
    public ResponseResult<List<PassengerTrendVO>> trend(@PathVariable("enterprise-id") String enterpriseId,
                                                          @RequestBody PassengerNewBoardRequest request) {
        DataSourceHelper.changeToMy();
        List<PassengerTrendVO> passengerTrendVOList = passengerFlowService.trend(enterpriseId, request);
        return ResponseResult.success(passengerTrendVOList);
    }

    @ApiOperation("经店人数、进店人数、进店转化排行")
    @PostMapping("/storeRankNew")
    public ResponseResult<List<PassengerStoreRankVO>> storeRankNew(@PathVariable("enterprise-id") String eid,
                                                                   @RequestBody PassengerNewBoardRequest request) {

        DataSourceHelper.changeToMy();
        List<PassengerStoreRankVO> passengerStoreRankVOList = passengerFlowService.storeRankNew(eid, request);
        return ResponseResult.success(passengerStoreRankVOList);
    }

    @ApiOperation("客流数据明细")
    @PostMapping("/list")
    public ResponseResult<PageInfo<PassengerStoreRankVO>> passengerFlowList(@PathVariable("enterprise-id") String eid,
                                                                            @RequestBody PassengerStoreDayRequest request) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(passengerFlowService.passengerFlowList(eid, request));
    }

    @ApiOperation("客群分布")
    @PostMapping("/passengerGroupDistribution")
    public ResponseResult<PassengerGroupVO> passengerGroupDistribution(@PathVariable("enterprise-id") String enterpriseId,
                                                                             @RequestBody PassengerNewBoardRequest request) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(passengerFlowService.passengerGroupDistribution(enterpriseId, request));
    }


}
