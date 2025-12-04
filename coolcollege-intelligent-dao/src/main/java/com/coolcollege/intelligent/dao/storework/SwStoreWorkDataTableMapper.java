package com.coolcollege.intelligent.dao.storework;

import com.coolcollege.intelligent.model.storework.SwStoreWorkDataTableDO;
import com.coolcollege.intelligent.model.storework.SwStoreWorkRecordDO;
import com.coolcollege.intelligent.model.storework.dto.StoreWorkOverViewDataDTO;

import com.coolcollege.intelligent.model.storework.dto.StoreWorkQuestionDataDTO;
import com.coolcollege.intelligent.model.storework.dto.SwStoreWorkRecordStatisticsDTO;
import com.coolcollege.intelligent.model.storework.request.RegionSummaryDataStatisticRequest;
import com.coolcollege.intelligent.model.storework.request.StoreWorkDataListRequest;
import com.coolcollege.intelligent.model.storework.request.StoreWorkRecordListRequest;
import com.coolcollege.intelligent.model.storework.vo.*;
import io.swagger.models.auth.In;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * @author wxp
 * @date 2022-09-08 02:01
 */
public interface SwStoreWorkDataTableMapper {
    /**
     *
     * 默认插入方法，只会给有值的字段赋值
	 * 会对传进来的字段做判空处理，如果字段为空，则使用数据库默认字段或者null
     * dateTime:2022-09-08 02:01
     */
    int insertSelective(@Param("record")SwStoreWorkDataTableDO record, @Param("enterpriseId") String enterpriseId);

    /**
     *
     * 默认查询方法，通过主键获取所有字段的值
     * dateTime:2022-09-08 02:01
     */
    SwStoreWorkDataTableDO selectByPrimaryKey(@Param("enterpriseId") String enterpriseId, @Param("id")Long id);

    /**
     *
     * 默认更新方法，根据主键更新，不会把null值更新到数据库，避免覆盖之前有值的
     * dateTime:2022-09-08 02:01
     */
    int updateByPrimaryKeySelective(@Param("record")SwStoreWorkDataTableDO record, @Param("enterpriseId") String enterpriseId);

    /**
     *
     * 默认更新方法，根据主键物理删除
     * dateTime:2022-09-08 02:01
     */
    int deleteByPrimaryKey(Long id, @Param("enterpriseId") String enterpriseId);

    int batchInsert(@Param("enterpriseId") String enterpriseId, @Param("entityList") List<SwStoreWorkDataTableDO> entityList);

    List<SwStoreWorkDataTableDO> selectSwStoreWorkDataTable(@Param("enterpriseId") String enterpriseId,
                                                            @Param("queryDate") String queryDate,
                                                            @Param("workCycle") String workCycle,
                                                            @Param("userId") String userId,
                                                            @Param("storeId") String storeId);

    SwStoreWorkDataTableDO selectDataTableByStoreWorkId(@Param("enterpriseId") String enterpriseId,
                                                            @Param("storeWorkDate") String storeWorkDate,
                                                            @Param("userId") String userId,
                                                            @Param("storeWorkId") Long storeWorkId);

    List<SwStoreWorkDataTableDO> selectSwStoreWorkDataTableByBusinessId(@Param("enterpriseId") String enterpriseId,
                                                                        @Param("businessId") String businessId,
                                                                        @Param("storeId") String storeId);

    List<SwStoreWorkDataTableDO> selectSwStoreWorkDataTableByBusinessIds(@Param("enterpriseId") String enterpriseId,
                                                                        @Param("businessIds") List<String> businessIds);

    List<SwStoreWorkDataTableDO> selectSwStoreWorkDataTableNoCommentByBusinessIds(@Param("enterpriseId") String enterpriseId,
                                                                                  @Param("businessIds") List<String> businessIds,
                                                                                  @Param("tableMappingId") Long tableMappingId);

    List<SwStoreWorkDataTableDO> noCommentStoreWorkCount(@Param("enterpriseId") String enterpriseId,
                                    @Param("queryDate") String queryDate,
                                    @Param("workCycle") String workCycle,
                                    @Param("userId") String userId,
                                    @Param("storeId") String storeId);

    /**
     * 获取上次检查结果
     * @param enterpriseId
     * @param storeId
     * @param metaTableId
     * @return
     */
    SwStoreWorkDataTableDO getLastTimeDataTableDO(@Param("enterpriseId") String enterpriseId,
                                                  @Param("storeId") String storeId,
                                                  @Param("metaTableId") Long metaTableId);

    /**
     * 查询店务检查表数据表  列表数据
     * @param enterpriseId
     * @param queryDate
     * @param workCycle
     * @param userId
     * @param storeId
     * @return
     */
    List<SwStoreWorkDataTableDO> getSwStoreWorkDataTableList(@Param("enterpriseId") String enterpriseId,
                                                             @Param("queryDate") String queryDate,
                                                             @Param("workCycle") String workCycle,
                                                             @Param("userId") String userId,
                                                             @Param("storeId") String storeId);

    List<SwStoreWorkDataTableDO> getSwStoreWorkDataTableListByBusinessId(@Param("enterpriseId") String enterpriseId,
                                                             @Param("userId") String userId,
                                                             @Param("businessId") String businessId);


    StoreWorkOverViewDataDTO getStoreWorkOverViewData(@Param("enterpriseId") String enterpriseId,
                                                      @Param("queryDate") String queryDate,
                                                      @Param("workCycle") String workCycle,
                                                      @Param("userId") String userId,
                                                      @Param("storeId") String storeId,
                                                      @Param("excludeDefTable") Boolean excludeDefTable,
                                                      @Param("businessId") String businessId);

    List<SwStoreWorkDataTableDO> selectSpecialTimeNoCompleteStoreWork(@Param("enterpriseId") String enterpriseId,
                                                                   @Param("storeId") String storeId,
                                                                   @Param("userId") String userId,
                                                                   @Param("workCycle") String workCycle,
                                                                   @Param("queryTimes") List<String> queryTimes);


    /**
     * 数据 -- 门店统计 -- 数据概况
     * @param enterpriseId
     * @param request
     * @return
     */
    StoreWorkStatisticsOverviewVO storeWorkStoreStatisticsOverview(@Param("enterpriseId") String enterpriseId, @Param("params") StoreWorkDataListRequest request);

    Long countStoreWorkStoreStatistics(@Param("enterpriseId") String enterpriseId, @Param("params")
            StoreWorkDataListRequest request);
    /**
     * 店务 数据页--门店统计  按storeWorkId  storeWorkDate  metaTableId 进行分页统计
     * @param enterpriseId
     * @param request
     * @return
     */
    List<StoreWorkDataDetailVO> storeWorkStoreStatisticsList(@Param("enterpriseId") String enterpriseId,
                                                             @Param("params") StoreWorkDataListRequest request,
                                                             @Param("pageNum") Integer pageNum,
                                                             @Param("pageSize") Integer pageSize);

    Integer storeWorkStoreStatisticsListCount(@Param("enterpriseId") String enterpriseId,
                                              @Param("params") StoreWorkDataListRequest request);


    List<String> getStoreWorkStoreIdList(@Param("enterpriseId") String enterpriseId, @Param("params")
            StoreWorkDataListRequest request);

    List<SwStoreWorkDataTableDO> selectByIds(@Param("ids") List<Long> ids, @Param("enterpriseId") String enterpriseId);


    Boolean batchUpdate(@Param("enterpriseId") String enterpriseId, @Param("list") List<SwStoreWorkDataTableDO> list);

    int updateCommentUserIds(@Param("enterpriseId") String enterpriseId, @Param("record") SwStoreWorkDataTableDO record);

    List<StoreWorkDataTableStatisticsVO> storeWorkTableStatisticsList(@Param("enterpriseId") String enterpriseId, @Param("params")
            StoreWorkDataListRequest request);

    Long countStoreWorkDayStatistics(@Param("enterpriseId") String enterpriseId, @Param("params")
            StoreWorkDataListRequest request);

    List<StoreWorkDayStatisticsVO> storeWorkDayStatisticsList(@Param("enterpriseId") String enterpriseId, @Param("params")
            StoreWorkDataListRequest request);

    // 提交时统计
    SwStoreWorkRecordStatisticsDTO statisticsWhenSubmit(@Param("enterpriseId") String enterpriseId,
                                                @Param("tcBusinessId") String tcBusinessId);
    // 点评时统计
    SwStoreWorkRecordStatisticsDTO statisticsWhenComment(@Param("enterpriseId") String enterpriseId,
                                                         @Param("tcBusinessId") String tcBusinessId);




    List<StoreWorkStatisticsOverviewVO> regionExecutiveSummaryList(@Param("enterpriseId") String enterpriseId, @Param("params")
            StoreWorkDataListRequest request);

    List<StoreWorkRecordVO> storeWorkRecordList(@Param("enterpriseId") String enterpriseId, @Param("params")
            StoreWorkRecordListRequest request);

    Long countStoreWorkRecord(@Param("enterpriseId") String enterpriseId, @Param("params")
            StoreWorkRecordListRequest request);


    List<SwStoreWorkDataTableDO> selectStoreWorkDataTableList(@Param("fullRegionPathList") List<String> fullRegionPathList,
                                                              @Param("enterpriseId") String enterpriseId,
                                                              @Param("storeWorkId") Long storeWorkId,
                                                              @Param("tableMappingId") Long tableMappingId,
                                                              @Param("beginStoreWorkDate") String beginStoreWorkDate,
                                                              @Param("endStoreWorkDate") String endStoreWorkDate,
                                                              @Param("businessId") String businessId,
                                                              @Param("workCycle") String workCycle,
                                                              @Param("storeId") String storeId,
                                                              @Param("completeStatus") Integer completeStatus,
                                                              @Param("commentStatus") Integer commentStatus);

    Long countStoreWorkDataTableList(@Param("fullRegionPathList") List<String> fullRegionPathList,
                                      @Param("enterpriseId") String enterpriseId,
                                      @Param("storeWorkId") Long storeWorkId,
                                      @Param("tableMappingId") Long tableMappingId,
                                      @Param("beginStoreWorkDate") String beginStoreWorkDate,
                                      @Param("endStoreWorkDate") String endStoreWorkDate,
                                      @Param("businessId") String businessId,
                                      @Param("workCycle") String workCycle,
                                      @Param("storeId") String storeId,
                                      @Param("completeStatus") Integer completeStatus,
                                      @Param("commentStatus") Integer commentStatus);


    List<SwStoreWorkDataTableDO> listNeedRemindDataTable(@Param("enterpriseId") String enterpriseId, @Param("params")
            StoreWorkDataListRequest request);

    StoreWorkQuestionDataDTO recordQuestionDate(@Param("enterpriseId")String enterpriseId, @Param("tcBusinessId") String tcBusinessId);

    List<SwStoreWorkDataTableDO> listNotCompleteDataTableByStoreWorkId(@Param("enterpriseId") String enterpriseId,
                                                                        @Param("storeWorkId") Long storeWorkId);
    List<SwStoreWorkDataTableDO> listDataTableHasDelByStoreWorkId(@Param("enterpriseId") String enterpriseId,
                                                            @Param("storeWorkId") Long storeWorkId);

    Integer updateDelByStoreWorkId(@Param("enterpriseId") String enterpriseId,
                                         @Param("storeWorkId") Long storeWorkId);

    List<SwStoreWorkDataTableDO> listDataTable(@Param("enterpriseId")String enterpriseId,
                                               @Param("workCycle") String workCycle,
                                               @Param("storeWorkId") Long storeWorkId,
                                               @Param("metaTableId") Long metaTableId,
                                               @Param("tableMappingId") Long tableMappingId,
                                               @Param("beginTime") String beginTime,
                                               @Param("endTime") String endTime,
                                               @Param("fullRegionPathList") List<String> fullRegionPathList);



    List<SwStoreWorkDataTableDO> selectSwStoreWorkDataTableByUserId(@Param("enterpriseId")String enterpriseId,
                                               @Param("userId") String userId);


    List<SwStoreWorkDataTableDO> selectCommentSwStoreWorkDataTableByUserId(@Param("enterpriseId")String enterpriseId,
                                                                    @Param("userId") String userId);

    List<SwStoreWorkDataTableDO> getResultByTcBusinessId(@Param("enterpriseId") String enterpriseId,
                                                         @Param("tcBusinessId") String tcBusinessId);

    List<SwStoreWorkRecordDO> selectSwStoreWorkDataTableById(@Param("enterpriseId") String enterpriseId,
                                                             @Param("workCycle") String workCycle,
                                                             @Param("queryDate") String queryDate,
                                                             @Param("completeStatue") Integer completeStatue,
                                                             @Param("commentStatus") Integer commentStatus,
                                                             @Param("list") List<String> list,
                                                             @Param("eligibleStatus") String eligibleStatus,
                                                             @Param("metaTableId") String metaTableId);

    void delByStoreWorkIdAndStoreIdAndDate(@Param("enterpriseId")String enterpriseId, @Param("storeWorkId") Long storeWorkId, @Param("storeIds") List<String> storeIds, @Param("storeWorkDate") String storeWorkDate);
}