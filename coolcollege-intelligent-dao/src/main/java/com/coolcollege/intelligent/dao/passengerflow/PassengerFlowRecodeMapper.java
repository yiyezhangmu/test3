package com.coolcollege.intelligent.dao.passengerflow;

import com.coolcollege.intelligent.model.passengerflow.PassengerFlowRecordDO;
import com.coolcollege.intelligent.model.passengerflow.vo.*;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2021/10/08
 */
@Mapper
public interface PassengerFlowRecodeMapper {

   void  batchInsertPassengerFlowRecordDO(@Param("eid")String eid,
                                          @Param("list") List<PassengerFlowRecordDO> passengerFlowRecordDOList);

    void  updateFlowInOutById(@Param("eid")String eid,
                              @Param("id")Long id,
                              @Param("flowIn")Integer flowIn,
                              @Param("flowOut")Integer flowOut,
                              @Param("flowInOut")Integer flowInOut);

    List<PassengerFlowRecordDO> hourDay(@Param("eid")String eid,
                                              @Param("sceneId")Long sceneId,
                                             @Param("deviceId")String deviceId,
                                             @Param("storeIdList")List<String> storeIdList,
                                              @Param("regionPath")String regionPath,
                                              @Param("startTime") Date startTime,
                                              @Param("endTime")Date endTime);

    List<PassengerFlowRecordDO>storeDay(@Param("eid")String eid,
                                        @Param("storeIdList")List<String> storeIdList,
                                        @Param("startTime") Date startTime,
                                        @Param("endTime")Date endTime);

    List<PassengerStoreDayVO> pageStore(@Param("eid")String eid,
                                        @Param("storeIdList")List<String> storeIdList,
                                        @Param("regionPath")String regionPath,
                                       @Param("startTime") Date startTime,
                                       @Param("endTime")Date endTime);

    List<PassengerStoreRankVO> storeRankByIn(@Param("eid")String eid,
                                             @Param("sceneId")Long sceneId,
                                             @Param("storeIdList")List<String> storeId,
                                             @Param("regionPath")String regionPath,
                                             @Param("startTime") Date startTime,
                                             @Param("endTime")Date endTime);

    List<PassengerStoreRankVO> storeRankByInOut(@Param("eid")String eid,
                                            @Param("sceneId")Long sceneId,
                                            @Param("storeIdList")List<String> storeId,
                                            @Param("regionPath")String regionPath,
                                            @Param("startTime") Date startTime,
                                            @Param("endTime")Date endTime);

    List<PassengerAchievementVO> flowInPercent(@Param("eid")String eid,
                                               @Param("storeIdList")List<String> storeIdList,
                                               @Param("regionPath")String regionPath,
                                               @Param("startTime") Date startTime,
                                               @Param("endTime")Date endTime);

 Page<PassengerStoreRankVO> passengerFlowList(@Param("eid")String eid,
                                     @Param("storeIdList")List<String> storeIdList,
                                     @Param("regionPath")String regionPath,
                                     @Param("startTime") Date startTime,
                                     @Param("endTime")Date endTime);

 List<PassengerStoreRankVO> storeRankNew(@Param("eid")String eid,
                                          @Param("storeIdList")List<String> storeId,
                                          @Param("regionPath")String regionPath,
                                          @Param("startTime") Date startTime,
                                          @Param("endTime")Date endTime ,
                                         @Param("sortField") String sortField,
                                         @Param("sortType") String sortType);

 List<PassengerTrendVO> trend(@Param("eid")String eid,
                              @Param("storeIdList")List<String> storeId,
                              @Param("regionPath")String regionPath,
                              @Param("startTime") Date startTime,
                              @Param("endTime")Date endTime);


 PassengerDeviceHourDayVO getPassengerFlowOverview(@Param("eid")String eid,
                                                   @Param("storeIdList")List<String> storeId,
                                                   @Param("regionPath")String regionPath,
                                                   @Param("startTime") Date startTime,
                                                   @Param("endTime")Date endTime);

 PassengerGroupVO passengerGroupDistribution(@Param("eid")String eid,
                                                   @Param("storeIdList")List<String> storeId,
                                                   @Param("regionPath")String regionPath,
                                                   @Param("startTime") Date startTime,
                                                   @Param("endTime")Date endTime);
}
