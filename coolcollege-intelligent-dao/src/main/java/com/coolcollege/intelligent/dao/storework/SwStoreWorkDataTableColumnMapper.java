package com.coolcollege.intelligent.dao.storework;

import com.coolcollege.intelligent.model.storework.SwStoreWorkDataTableColumnDO;
import com.coolcollege.intelligent.model.storework.SwStoreWorkDataTableDO;
import com.coolcollege.intelligent.model.storework.dto.StoreWorkQuestionDataDTO;
import com.coolcollege.intelligent.model.storework.request.StoreDataListRequest;
import com.coolcollege.intelligent.model.storework.request.StoreWorkColumnDetailListRequest;
import com.coolcollege.intelligent.model.storework.request.StoreWorkDataListRequest;
import com.coolcollege.intelligent.model.storework.vo.*;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author wxp
 * @date 2022-09-08 02:01
 */
public interface SwStoreWorkDataTableColumnMapper {
    /**
     *
     * 默认插入方法，只会给有值的字段赋值
	 * 会对传进来的字段做判空处理，如果字段为空，则使用数据库默认字段或者null
     * dateTime:2022-09-08 02:01
     */
    int insertSelective(@Param("record")SwStoreWorkDataTableColumnDO record, @Param("enterpriseId") String enterpriseId);

    /**
     *
     * 默认查询方法，通过主键获取所有字段的值
     * dateTime:2022-09-08 02:01
     */
    SwStoreWorkDataTableColumnDO selectByPrimaryKey(@Param("enterpriseId") String enterpriseId, @Param("id")Long id);

    /**
     *
     * 默认更新方法，根据主键更新，不会把null值更新到数据库，避免覆盖之前有值的
     * dateTime:2022-09-08 02:01
     */
    int updateByPrimaryKeySelective(@Param("record")SwStoreWorkDataTableColumnDO record, @Param("enterpriseId") String enterpriseId);

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
    int batchInsert(@Param("enterpriseId") String enterpriseId,@Param("entityList") List<SwStoreWorkDataTableColumnDO> entityList);

    /**
     * 根据表条件ID查询该表查询作业项数据
     * @param enterpriseId
     * @param dataTableIds
     * @return
     */
    List<SwStoreWorkDataTableColumnDO> selectColumnByDataTableId(@Param("enterpriseId") String enterpriseId,
                                                                 @Param("dataTableIds") List<Long> dataTableIds,
                                                                 @Param("checkResultList") List<String> checkResultList,
                                                                 @Param("submitStatus") Integer submitStatus,
                                                                 @Param("aiCheckResultList") List<String> aiCheckResultList);

    /**
     * 根据业务id获取标准检查项数据
     */
    List<SwStoreWorkDataTableColumnDO> selectByDataTableId(@Param("enterpriseId") String enterpriseId,
                                                    @Param("dataTableId") Long dataTableId);

    List<StoreWorkColumnRankDataVO> countFailRank(@Param("enterpriseId") String enterpriseId,
                                                  @Param("storeWorkBeginDate") String storeWorkBeginDate,
                                                  @Param("storeWorkEndDate") String storeWorkEndDate,
                                                  @Param("regionPathList") List<String> regionPathList,
                                                  @Param("workCycle") String workCycle);

    List<ColumnCompleteRateRankDataVO> completeRateRank(@Param("enterpriseId") String enterpriseId,
                                                        @Param("storeWorkBeginDate") String storeWorkBeginDate,
                                                        @Param("storeWorkEndDate") String storeWorkEndDate,
                                                        @Param("regionPathList") List<String> regionPathList,
                                                        @Param("workCycle") String workCycle,
                                                        @Param("sortField") String sortField,
                                                        @Param("sortType") String sortType);

    /**
     * 批量更新
     * @param enterpriseId
     * @param list
     * @return
     */
    Boolean batchUpdate(@Param("enterpriseId") String enterpriseId, @Param("list") List<SwStoreWorkDataTableColumnDO> list);

    SwStoreWorkDataTableDO statisticsByDataTableId(@Param("enterpriseId") String enterpriseId,
                                                                  @Param("dataTableId") Long dataTableId);

    List<SwStoreWorkDataTableColumnDO> selectByBusinessId(@Param("enterpriseId") String enterpriseId,
                                                     @Param("businessId") String businessId);

    List<SwStoreWorkDataTableColumnDO> selectByBusinessIds(@Param("enterpriseId") String enterpriseId,
                                                          @Param("businessIds") List<String> businessIds);

    List<SwStoreWorkDataTableColumnDO> selectStoreWorkDataColumnList(@Param("fullRegionPathList") List<String> fullRegionPathList,
                                                              @Param("enterpriseId") String enterpriseId,
                                                              @Param("storeWorkId") Long storeWorkId,
                                                              @Param("tableMappingId") Long tableMappingId,
                                                              @Param("beginStoreWorkDate") String beginStoreWorkDate,
                                                              @Param("endStoreWorkDate") String endStoreWorkDate,
                                                              @Param("workCycle") String workCycle,
                                                              @Param("dataTableId") Long dataTableId);

    Long countStoreWorkDataColumnList(@Param("fullRegionPathList") List<String> fullRegionPathList,
                                                                     @Param("enterpriseId") String enterpriseId,
                                                                     @Param("storeWorkId") Long storeWorkId,
                                                                     @Param("tableMappingId") Long tableMappingId,
                                                                     @Param("beginStoreWorkDate") String beginStoreWorkDate,
                                                                     @Param("endStoreWorkDate") String endStoreWorkDate,
                                                                     @Param("workCycle") String workCycle,
                                                                     @Param("dataTableId") Long dataTableId);

    List<StoreWorkDataTableColumnListVO> columnCompleteRateList(@Param("enterpriseId")String enterpriseId, @Param("params")StoreWorkDataListRequest params);

    List<StoreWorkColumnStoreListVO> columnStoreCompleteList(@Param("enterpriseId")String enterpriseId, @Param("params") StoreWorkColumnDetailListRequest params);

    List<StoreWorkFailColumnStoreListVO> failColumnStoreList(@Param("enterpriseId")String enterpriseId, @Param("params") StoreDataListRequest queryParam);

    StoreWorkQuestionDataDTO tableQuestionDate(@Param("enterpriseId")String enterpriseId,@Param("dataTableId") Long dataTableId);

    /**
     * 删除问题工单
     */
    int delStoreWorkQuestion(@Param("enterpriseId") String enterpriseId, @Param("taskQuestionId") Long taskQuestionId, @Param("id") Long id);

    /**
     * 根据业务id获取标准检查项数据
     */
    List<SwStoreWorkDataTableColumnDO> selectFailColumnByDataTableId(@Param("enterpriseId") String enterpriseId,
                                                                     @Param("dataTableId") Long dataTableId);

    /**
     * 根据storeWorkId删除
     */
    int updateDelByStoreWorkId(@Param("enterpriseId") String enterpriseId, @Param("storeWorkId") Long storeWorkId);

    List<SwStoreWorkDataTableColumnDO> selectByIds(@Param("enterpriseId") String enterpriseId, @Param("ids") List<Long> ids);


    void delByStoreWorkIdAndStoreIdAndDate(@Param("enterpriseId")String enterpriseId, @Param("storeWorkId") Long storeWorkId, @Param("storeIds") List<String> storeIds, @Param("storeWorkDate") String storeWorkDate);

    /**
     * 根据数据表id获取AI检查项数据
     */
    List<SwStoreWorkDataTableColumnDO> selectAiColumnByDataTableId(@Param("enterpriseId") String enterpriseId,
                                                                   @Param("dataTableId") Long dataTableId);


    List<SwStoreWorkDataTableColumnDO> selectAiColumn(@Param("enterpriseId") String enterpriseId);
}