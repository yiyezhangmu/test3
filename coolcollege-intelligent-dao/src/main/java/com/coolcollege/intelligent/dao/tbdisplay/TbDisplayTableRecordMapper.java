package com.coolcollege.intelligent.dao.tbdisplay;

import com.coolcollege.intelligent.facade.dto.openApi.DisplayDTO;
import com.coolcollege.intelligent.model.tbdisplay.TbDisplayTableRecordDO;
import com.coolcollege.intelligent.model.unifytask.TaskStoreDO;
import com.coolcollege.intelligent.model.unifytask.vo.TaskReportVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

@Mapper
public interface TbDisplayTableRecordMapper {

    /**
     * 批量插入陈列任务记录
     * @param enterpriseId
     * @param list
     * @return
     */
    int batchInsert(@Param("enterpriseId") String enterpriseId, @Param("list")List<TbDisplayTableRecordDO> list);

    TbDisplayTableRecordDO selectByPrimaryKey(@Param("enterpriseId") String enterpriseId, @Param("id") Long id);

    int updateByPrimaryKeySelective(@Param("enterpriseId") String enterpriseId, @Param("record") TbDisplayTableRecordDO record);

    int batchUpdate(@Param("enterpriseId") String enterpriseId, @Param("recordList") List<TbDisplayTableRecordDO> recordList);

    TbDisplayTableRecordDO getByUnifyTaskIdAndStoreIdAndLoopCount(@Param("enterpriseId") String enterpriseId,
                                                                      @Param("unifyTaskId")Long unifyTaskId, @Param("storeId")String storeId, @Param("loopCount")Long loopCount);

    Long getIdByUnifyTaskIdAndStoreIdAndLoopCount(@Param("enterpriseId") String enterpriseId,
                                                                  @Param("unifyTaskId")Long unifyTaskId, @Param("storeId")String storeId, @Param("loopCount")Long loopCount);

    List<TbDisplayTableRecordDO> batchGetByUnifyTaskIdAndStoreIdAndLoopCount(@Param("enterpriseId") String enterpriseId, @Param("taskStoreList")List<TaskStoreDO> taskStoreList);

    List<TbDisplayTableRecordDO> listByUnifyTaskId(@Param("enterpriseId") String enterpriseId, @Param("unifyTaskId")Long unifyTaskId,
                                                   @Param("loopCount")Long loopCount, @Param("storeIdList") List<String> storeIdList);

    int updateHandleInfoByRecordId(@Param("enterpriseId") String enterpriseId, @Param("status")String status
            , @Param("updatedHandleUserId")String updatedHandleUserId,@Param("updatedHandleUserName")String updatedHandleUserName,@Param("id")Long id);

    int updateStatusByTaskIdStoreIdLoopCount(@Param("enterpriseId") String enterpriseId, @Param("status")String status
            , @Param("unifyTaskId")Long unifyTaskId, @Param("storeId")String storeId, @Param("loopCount")Long loopCount);

    List<TbDisplayTableRecordDO> listByIdList(@Param("enterpriseId") String enterpriseId, @Param("list") List<Long> idList);

    List<TbDisplayTableRecordDO> listByUnifyTaskIdAndloopCount(@Param("enterpriseId") String enterpriseId, @Param("unifyTaskId")Long unifyTaskId, @Param("loopCount")Long loopCount);

    List<TbDisplayTableRecordDO> listByUnifyTaskIdAndloopCountAndStoreIds(@Param("enterpriseId") String enterpriseId, @Param("unifyTaskId")Long unifyTaskId, @Param("loopCount")Long loopCount, @Param("storeIdList")List<String> storeIdList);

    List<TbDisplayTableRecordDO> listByUnifyTaskIdsAndloopCountAndStoreIds(@Param("enterpriseId")String enterpriseId,
                                                                           @Param("unifyTaskIds") List<String> unifyTaskIds,
                                                                           @Param("loopCount")Long loopCount,
                                                                           @Param("storeIdList")List<String> storeIdList);


    // 平均得分=已完成任务得分总和/已完成任务数
    List<TaskReportVO> sumTaskScore(@Param("enterpriseId") String enterpriseId, @Param("list") List<Long> unifyTaskIdList);

    /**
     * 通过任务名称查询
     * @Author chenyupeng
     * @Date 2021/8/2
     * @param enterpriseId
     * @param regionPath
     * @param storeIdList
     * @param metaTableId
     * @param taskName
     * @param beginTime
     * @param endTime
     */
    List<TbDisplayTableRecordDO> getRecordByTaskName(@Param("enterpriseId") String enterpriseId,
                                                    @Param("regionPath") String regionPath,
                                                    @Param("storeIdList") List<String> storeIdList,
                                                    @Param("metaTableId") Long metaTableId,
                                                    @Param("taskName") String taskName,
                                                    @Param("beginTime") Date beginTime, @Param("endTime") Date endTime,
                                                     @Param("completeBeginDate") Date completeBeginDate, @Param("completeEndDate") Date completeEndDate,
                                                     @Param("regionPathList") List<String> regionPathList,
                                                     @Param("status") String status);


    List<TbDisplayTableRecordDO> listByUnifyTaskIdList(@Param("enterpriseId") String enterpriseId, @Param("list") List<Long> idList);


    /**
     * 陈列记录列表
     * @param enterpriseId
     * @param displayDTO
     * @return
     */
    List<TbDisplayTableRecordDO> displayList(@Param("enterpriseId") String enterpriseId,
                                             @Param("record") DisplayDTO displayDTO );

    List<TbDisplayTableRecordDO> deleteListByUnifyTaskId(@Param("enterpriseId") String enterpriseId, @Param("unifyTaskId")Long unifyTaskId);

    List<TbDisplayTableRecordDO> deleteListByUnifyTaskIds(@Param("enterpriseId") String enterpriseId, @Param("unifyTaskIds")List<Long> unifyTaskIds);

    /**
     * 根据store表订正陈列任务记录表的regionId和regionPath
     * @param enterpriseId 企业id
     * @param unifyTaskId 父任务id
     * @return int
     */
    int correctRegionIdAndPath(@Param("enterpriseId") String enterpriseId, @Param("unifyTaskId") Long unifyTaskId);
}