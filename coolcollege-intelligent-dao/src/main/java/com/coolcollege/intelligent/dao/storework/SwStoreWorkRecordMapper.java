package com.coolcollege.intelligent.dao.storework;

import com.coolcollege.intelligent.model.homepage.vo.StoreWorkDataVO;
import com.coolcollege.intelligent.model.storework.SwStoreWorkRecordDO;
import com.coolcollege.intelligent.model.storework.dto.StoreWorkStatisticsDTO;
import com.coolcollege.intelligent.model.storework.dto.StoreWorkStatisticsExecutiveDTO;
import com.coolcollege.intelligent.model.storework.request.StoreWorkDataListRequest;
import com.coolcollege.intelligent.model.storework.request.StoreWorkRecordListRequest;
import com.coolcollege.intelligent.model.storework.vo.*;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author wxp
 * @date 2022-09-08 02:01
 */
public interface SwStoreWorkRecordMapper {
    /**
     *
     * 默认插入方法，只会给有值的字段赋值
	 * 会对传进来的字段做判空处理，如果字段为空，则使用数据库默认字段或者null
     * dateTime:2022-09-08 02:01
     */
    int insertSelective(@Param("record")SwStoreWorkRecordDO record, @Param("enterpriseId") String enterpriseId);

    /**
     *
     * 默认查询方法，通过主键获取所有字段的值
     * dateTime:2022-09-08 02:01
     */
    SwStoreWorkRecordDO selectByPrimaryKey(@Param("enterpriseId") String enterpriseId, @Param("id")Long id);

    /**
     *
     * 默认更新方法，根据主键更新，不会把null值更新到数据库，避免覆盖之前有值的
     * dateTime:2022-09-08 02:01
     */
    int updateByPrimaryKeySelective(@Param("record")SwStoreWorkRecordDO record, @Param("enterpriseId") String enterpriseId);

    /**
     *
     * 默认更新方法，根据主键物理删除
     * dateTime:2022-09-08 02:01
     */
    int deleteByPrimaryKey(Long id, @Param("enterpriseId") String enterpriseId);

    /**
     * 批量新增
     * @param enterpriseId
     * @param entityList
     * @return
     */
    int batchInsert(@Param("enterpriseId") String enterpriseId, @Param("entityList") List<SwStoreWorkRecordDO> entityList);

    StoreWorkStatisticsDTO countByStoreWorkId(@Param("enterpriseId") String enterpriseId, @Param("storeWorkId") Long storeWorkId);


    /**
     * 数据 -- 门店统计 -- 数据概况
     * @param enterpriseId
     * @param request
     * @return
     */
    StoreWorkStatisticsOverviewVO storeWorkStoreStatisticsOverview(@Param("enterpriseId") String enterpriseId, @Param("params") StoreWorkDataListRequest request);

    /**
     * 数据 -- 门店统计 -- 数据概况
     * @param enterpriseId
     * @param request
     * @return
     */
    List<StoreWorkStatisticsOverviewVO> regionExecutiveSummaryList(@Param("enterpriseId") String enterpriseId, @Param("params") StoreWorkDataListRequest request);

    Long countStoreWorkStoreStatistics(@Param("enterpriseId") String enterpriseId, @Param("params") StoreWorkDataListRequest request);

    /**
     * 店务记录列表
     * @param enterpriseId
     * @param request
     * @return
     */
    List<StoreWorkDataDetailVO> storeWorkStoreStatisticsList(@Param("enterpriseId") String enterpriseId,
                                                             @Param("params") StoreWorkDataListRequest request,
                                                             @Param("pageNum") Integer pageNum,
                                                             @Param("pageSize") Integer pageSize);

    Integer storeWorkStoreStatisticsCount(@Param("enterpriseId") String enterpriseId,
                                                             @Param("params") StoreWorkDataListRequest request);

    Long countStoreWorkDayStatistics(@Param("enterpriseId") String enterpriseId, @Param("params") StoreWorkDataListRequest request);

    /**
     * 店务记录列表
     * @param enterpriseId
     * @param request
     * @return
     */
    List<String > getStoreWorkStoreIdList(@Param("enterpriseId") String enterpriseId, @Param("params") StoreWorkDataListRequest request);

    /**
     * 日报表统计
     * @param enterpriseId
     * @param request
     * @return
     */
    List<StoreWorkDayStatisticsVO> storeWorkDayStatisticsList(@Param("enterpriseId") String enterpriseId, @Param("params") StoreWorkDataListRequest request);


    List<StoreWorkDataVO> countStoreWorkStatistics(@Param("enterpriseId") String enterpriseId,
                                                   @Param("storeWorkBeginDate") String storeWorkBeginDate, @Param("storeWorkEndDate") String storeWorkEndDate,
                                                   @Param("regionPathList") List<String> regionPathList,
                                                   @Param("workCycle") String workCycle,
                                                   @Param("storeId") String storeId);

    StoreWorkDataVO getStoreWorkStatistics(@Param("enterpriseId") String enterpriseId,
                                                                  @Param("storeWorkBeginDate") String storeWorkBeginDate,  @Param("storeWorkEndDate") String storeWorkEndDate,
                                                                  @Param("regionPathList") List<String> regionPathList,
                                                                  @Param("workCycle") String workCycle);

    StoreWorkStatisticsExecutiveDTO storeExecutiveCompleteRateStatistics(@Param("enterpriseId") String enterpriseId,
                                                                         @Param("storeWorkBeginDate") String storeWorkBeginDate, @Param("storeWorkEndDate") String storeWorkEndDate,
                                                                         @Param("regionPathList") List<String> regionPathList,
                                                                         @Param("workCycle") String workCycle);

    StoreWorkStatisticsExecutiveDTO storeExecutivePassRateStatistics(@Param("enterpriseId") String enterpriseId,
                                                                         @Param("storeWorkBeginDate") String storeWorkBeginDate, @Param("storeWorkEndDate") String storeWorkEndDate,
                                                                         @Param("regionPathList") List<String> regionPathList,
                                                                         @Param("workCycle") String workCycle);

    List<StoreWorkStoreRankDataVO> storeExecutiveRank(@Param("enterpriseId") String enterpriseId,
                                                @Param("storeWorkBeginDate") String storeWorkBeginDate,
                                                @Param("storeWorkEndDate") String storeWorkEndDate,
                                                @Param("regionPathList") List<String> regionPathList,
                                                @Param("workCycle") String workCycle,
                                                @Param("sortField") String sortField,
                                                @Param("sortType") String sortType,
                                                @Param("storeId") String storeId);


    /**
     * 查询每个门店每天的完成情况
     * @param enterpriseId
     * @param workCycle
     * @param queryDate
     * @param completeStatue
     * @param commentStatus
     * @param regionPathList
     * @return
     */
    List<SwStoreWorkRecordDO> selectStoreWorkRecord(@Param("enterpriseId") String enterpriseId,
                                                    @Param("workCycle") String workCycle,
                                                    @Param("storeWorkDate") String queryDate,
                                                    @Param("completeStatue") Integer completeStatue,
                                                    @Param("commentStatus") Integer commentStatus,
                                                    @Param("regionPathList") List<String> regionPathList,
                                                    @Param("eligibleStatus") String eligibleStatus);

    StoreWorkRecordStatisticsVO getStoreWorkRecordStatistics(@Param("enterpriseId") String enterpriseId,
                                                             @Param("workCycle") String workCycle,
                                                             @Param("storeWorkDate") String queryDate,
                                                             @Param("completeStatue")Integer completeStatue,
                                                             @Param("commentStatus") Integer commentStatus,
                                                             @Param("regionPathList") List<String> regionPathList);

    /**
     * 查询当天哪些门店已经有电务记录
     * @param enterpriseId
     * @param queryDate
     * @param workCycle
     * @return
     */
    List<SwStoreWorkRecordDO> selectSwStoreWorkRecord(@Param("enterpriseId") String enterpriseId,
                                          @Param("queryDate") String queryDate,
                                          @Param("workCycle") String workCycle);

    SwStoreWorkRecordDO getByTcBusinessId(@Param("enterpriseId") String enterpriseId, @Param("tcBusinessId") String tcBusinessId);

    List<SwStoreWorkRecordDO> getByTcBusinessIds(@Param("enterpriseId") String enterpriseId, @Param("tcBusinessIds") List<String> tcBusinessIds);


    /**
     * 店务记录列表
     * @param enterpriseId
     * @param request
     * @return
     */
    List<StoreWorkRecordVO> storeWorkRecordList(@Param("enterpriseId") String enterpriseId, @Param("params") StoreWorkRecordListRequest request);

    Long countStoreWorkRecord(@Param("enterpriseId") String enterpriseId, @Param("params") StoreWorkRecordListRequest request);

    List<StoreWorkDataStoreDetailList> getStoreDetailWorkStatisticList(@Param("enterpriseId") String enterpriseId,
                                                                       @Param("storeWorkBeginDate") String storeWorkBeginDate,
                                                                       @Param("storeWorkEndDate") String storeWorkEndDate,
                                                                       @Param("workCycle") String workCycle,
                                                                       @Param("storeId") String storeId);

    StoreRankVO selectStoreRank(@Param("enterpriseId") String enterpriseId,
                                @Param("workCycle") String workCycle,
                                @Param("storeId") String storeId,
                                @Param("storeWorkDate") String storeWorkDate);

    int updateDelByStoreWorkId(@Param("enterpriseId") String enterpriseId, @Param("storeWorkId") Long storeWorkId);

    SwStoreWorkRecordDO getStoreWorkOverViewData(@Param("enterpriseId") String enterpriseId,
                                                      @Param("queryDate") String queryDate,
                                                      @Param("workCycle") String workCycle,
                                                      @Param("storeId") String storeId,
                                                      @Param("excludeDefTable") Boolean excludeDefTable,
                                                      @Param("businessId") String businessId);


    boolean updateBytcBusinessId(@Param("enterpriseId")String enterpriseId,
                                 @Param("tcBusinessId") String tcBusinessId,
                                 @Param("checkResult") String checkResult);

    Integer storeWorkStoreStatisticsListCount(@Param("enterpriseId")String enterpriseId, @Param("params")StoreWorkDataListRequest storeWorkDataListRequest);

    void delByStoreWorkIdAndStoreIdAndDate(@Param("enterpriseId")String enterpriseId, @Param("storeWorkId") Long storeWorkId, @Param("storeIds") List<String> storeIds, @Param("storeWorkDate") String storeWorkDate);
}