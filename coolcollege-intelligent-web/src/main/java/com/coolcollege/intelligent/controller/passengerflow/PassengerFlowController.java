package com.coolcollege.intelligent.controller.passengerflow;

import com.coolcollege.intelligent.common.annotation.ErrorHelper;
import com.coolcollege.intelligent.common.page.PageVO;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.common.util.PageHelperUtil;
import com.coolcollege.intelligent.model.passengerflow.request.*;
import com.coolcollege.intelligent.model.passengerflow.vo.PassengerAchievementVO;
import com.coolcollege.intelligent.model.passengerflow.vo.PassengerDeviceHourVO;
import com.coolcollege.intelligent.model.passengerflow.vo.PassengerStoreDayVO;
import com.coolcollege.intelligent.model.passengerflow.vo.PassengerStoreRankVO;
import com.coolcollege.intelligent.service.passengerflow.PassengerFlowService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.coolstore.base.enums.AccountTypeEnum;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2021/10/11
 */
@RestController
@RequestMapping({"/v3/enterprises/{enterprise-id}/passenger"})
@ErrorHelper
@Slf4j
public class PassengerFlowController {

    @Autowired
    private PassengerFlowService passengerFlowService;

    /**
     * 回调同步客流数据
     *
     * @param eid
     * @return
     */
    @PostMapping("/callback")
    public ResponseResult<Boolean> callback(@PathVariable("enterprise-id") String eid) {

        return ResponseResult.success(passengerFlowService.callback(eid, LocalDateTime.now()));
    }

    @PostMapping("/callback/date")
    public ResponseResult<Boolean> callback(@PathVariable("enterprise-id") String eid, @RequestParam("dateTime")String dateTime) {
        LocalDateTime parse = LocalDateTime.parse(dateTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        return ResponseResult.success(passengerFlowService.callback(eid, parse));
    }

    /**
     * 设备小时客流
     *
     * @param eid
     * @param request
     * @return
     */
    @GetMapping("/device/hour/day")
    public ResponseResult<List<PassengerDeviceHourVO>> deviceHourDay(@PathVariable("enterprise-id") String eid,
                                                                     PassengerDeviceHourDayRequest request) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(passengerFlowService.deviceHourDay(eid, request));
    }

    /**
     * 场景小时客流
     *
     * @param eid
     * @param request
     * @return
     */
    @GetMapping("/scene/hour/day")
    public ResponseResult<List<PassengerDeviceHourVO>> sceneHourDay(@PathVariable("enterprise-id") String eid,
                                                                    PassengerSceneHourDayRequest request) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(passengerFlowService.sceneHourDay(eid, request));
    }

    /**
     * 门店客流趋势
     *
     * @param eid
     * @param request
     * @return
     */
    @GetMapping("/store/day")
    public ResponseResult<PageVO<PassengerStoreDayVO>> storeDay(@PathVariable("enterprise-id") String eid,
                                                                PassengerStoreDayRequest request) {
        DataSourceHelper.changeToMy();
        List<PassengerStoreDayVO> passengerStoreDayVOList = passengerFlowService.storeDay(eid, request);
        if (CollectionUtils.isNotEmpty(passengerStoreDayVOList)) {
            return ResponseResult.success(PageHelperUtil.getPageVO(new PageInfo<>(passengerStoreDayVOList)));
        }
        return ResponseResult.success(PageHelperUtil.getPageVO(new PageInfo<>()));
    }

    /**
     * 门店客流排名
     *
     * @param eid
     * @param request
     * @return
     */
    @GetMapping("/store/rank")
    public ResponseResult<PageVO<PassengerStoreRankVO>> storeDay(@PathVariable("enterprise-id") String eid,
                                                                 @Valid PassengerStoreRankRequest request) {

        DataSourceHelper.changeToMy();
        List<PassengerStoreRankVO> passengerStoreRankVOList = passengerFlowService.storeRank(eid, request);
        if (CollectionUtils.isNotEmpty(passengerStoreRankVOList)) {
            return ResponseResult.success(PageHelperUtil.getPageVO(new PageInfo<>(passengerStoreRankVOList)));
        }
        return ResponseResult.success(PageHelperUtil.getPageVO(new PageInfo<>()));
    }

    @GetMapping("/day/passengerAndAchievement")
    public ResponseResult<List<PassengerAchievementVO>> passengerAndAchievement(@PathVariable("enterprise-id") String eid,
                                                                                  PassengerAchievementRequest request) {

        DataSourceHelper.changeToMy();
        List<PassengerAchievementVO> passengerStoreRankVOList = passengerFlowService.passengerAchievement(eid, request);
            return ResponseResult.success(passengerStoreRankVOList);
    }

    @GetMapping("/syncHikPassengerFlow")
    public ResponseResult<Boolean> syncHikPassengerFlow(@PathVariable("enterprise-id") String eid,
                                                        @RequestParam(required = true) String dateTime) {

        DataSourceHelper.changeToMy();
        Boolean aBoolean = passengerFlowService.syncHikPassengerFlow(eid, dateTime, AccountTypeEnum.PRIVATE);
        return ResponseResult.success(aBoolean);
    }

    @GetMapping("/syncHikPassengerFlowForPlatForm")
    public ResponseResult<Boolean> syncHikPassengerFlowForPlatForm(@PathVariable("enterprise-id") String eid,
                                                        @RequestParam(required = true) String dateTime) {

        DataSourceHelper.changeToMy();
        Boolean aBoolean = passengerFlowService.syncHikPassengerFlow(eid, dateTime, AccountTypeEnum.PLATFORM);
        return ResponseResult.success(aBoolean);
    }
}
