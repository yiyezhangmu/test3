package com.coolcollege.intelligent.service.passengerflow;

import com.coolcollege.intelligent.model.device.DeviceDO;
import com.coolcollege.intelligent.model.passengerflow.request.*;
import com.coolcollege.intelligent.model.passengerflow.vo.*;
import com.coolstore.base.enums.AccountTypeEnum;
import com.github.pagehelper.PageInfo;

import java.time.LocalDateTime;
import java.util.List;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2021/10/11
 */
public interface PassengerFlowService {
    /**
     * 客流分析数据同步回调
     * @param eid
     */
    Boolean callback(String eid,LocalDateTime nowLocalDateTime);

    /**
     * 客流分析数据同步回调
     * @param eid
     * @param localDateTime 调用的时间
     */
    Boolean callbackByTime(String eid , LocalDateTime localDateTime, List<DeviceDO> deviceDOList);

    Boolean updatePassenger(String eid,Long id ,Integer flowIn,Integer flowOut,Integer flowInOut);

    List<PassengerDeviceHourVO> deviceHourDay(String eid, PassengerDeviceHourDayRequest request);

    List<PassengerDeviceHourVO> sceneHourDay(String eid, PassengerSceneHourDayRequest request);

    List<PassengerStoreDayVO> storeDay(String eid, PassengerStoreDayRequest request);

    List<PassengerStoreRankVO> storeRank(String eid, PassengerStoreRankRequest request);

    List<PassengerAchievementVO> passengerAchievement(String eid, PassengerAchievementRequest request);

    Boolean syncHikPassengerFlow(String eid,String dataTime, AccountTypeEnum accountType);

    PassengerDeviceHourDayVO getPassengerFlowOverview(String eid, PassengerNewBoardRequest request);

    List<PassengerTrendVO> trend(String eid, PassengerNewBoardRequest request);
    List<PassengerStoreRankVO> storeRankNew(String eid, PassengerNewBoardRequest request);

    PageInfo<PassengerStoreRankVO> passengerFlowList(String enterpriseId, PassengerStoreDayRequest request);

    PassengerGroupVO passengerGroupDistribution(String eid, PassengerNewBoardRequest request);

}
