package com.coolcollege.intelligent.dao.unifytask;

import com.coolcollege.intelligent.model.unifytask.TaskDataMappingDO;
import com.coolcollege.intelligent.model.unifytask.TaskMappingDO;
import com.coolcollege.intelligent.model.unifytask.dto.StorePersonDto;
import com.coolcollege.intelligent.model.unifytask.dto.UnifyFormDataDTO;
import com.coolcollege.intelligent.model.unifytask.dto.UnifyPersonDTO;
import com.coolcollege.intelligent.model.unifytask.dto.UnifyStoreDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Description for this class
 *
 * @author : jixiang.jiang
 * @version : 1.0
 * @Description : Description for this class
 * @date ：Created in 2020/10/26 16:40
 */
@Mapper
public interface TaskMappingMapper {

    /**
     * 插入任务关联数据
     * @param enterpriseId
     * @param table
     * @param mappingDOList
     */
    void insertTaskMapping(@Param("enterpriseId") String enterpriseId, @Param("table") String table, @Param("list") List<TaskMappingDO> mappingDOList);

    /**
     * 插入任务关联数据
     * @param enterpriseId
     * @param mappingDOList
     */
    void insertDataTaskMapping(@Param("enterpriseId") String enterpriseId, @Param("list") List<TaskDataMappingDO> mappingDOList);

    /**
     * 查询映射关系
     * @param enterpriseId
     * @param table
     * @param taskId
     * @return
     */
    List<TaskMappingDO> selectMappingByTaskId(@Param("enterpriseId") String enterpriseId, @Param("table") String table, @Param("taskId") Long taskId);

    /**
     * 批量查询
     * @param enterpriseId
     * @param table
     * @param taskList
     * @return
     */
    List<TaskMappingDO> selectMappingByBatchTaskId(@Param("enterpriseId") String enterpriseId, @Param("table") String table, @Param("taskList") List<Long> taskList);

    /**
     * 人员详细信息
     * @param enterpriseId
     * @param mappingDO
     * @param storeIds
     * @return
     */
    List<UnifyPersonDTO> selectPersonInfo(@Param("enterpriseId") String enterpriseId, @Param("mappingDO")  TaskMappingDO mappingDO
            ,@Param("list") List<String> storeIds);

    List<UnifyPersonDTO> getTaskPersonFromSubTask(@Param("enterpriseId") String enterpriseId,  @Param("unifyTaskId") Long unifyTaskId
            ,@Param("list") List<String> storeIds, @Param("loopCount") Long loopCount, @Param("subStatus") String subStatus);

    /**
     * 获取task关联data数据
     * @param enterpriseId
     * @param taskList
     * @return
     */
    List<UnifyFormDataDTO> selectMappingData(@Param("enterpriseId") String enterpriseId, @Param("taskList") List<Long> taskList);
    /**
     * 获取task关联data数据
     * @param enterpriseId
     * @param taskId
     * @return
     */
    List<UnifyFormDataDTO> selectMappingDataByTaskId(@Param("enterpriseId") String enterpriseId, @Param("taskId") Long taskId);

    /**
     * 获取映射id
     * @param enterpriseId
     * @param taskId
     * @return
     */
    List<String> selectOriginMappingIdByTaskId(@Param("enterpriseId") String enterpriseId, @Param("taskId") Long taskId);

    /**
     *  门店详细信息
     * @param enterpriseId
     * @param taskList
     * @return
     */
    List<UnifyStoreDTO> selectStoreInfo(@Param("enterpriseId") String enterpriseId, @Param("taskList") List<Long> taskList);

    /**
     * 删除映射关系
     * @param enterpriseId
     * @param table
     * @param unifyTaskId
     * @return
     */
    void delMappingByTaskId(@Param("enterpriseId") String enterpriseId, @Param("table") String table, @Param("unifyTaskId") Long unifyTaskId);

    /**
     * 根据父任务id和门店id删除门店映射
     * @param enterpriseId
     * @param unifyTaskId
     * @param storeId
     */
    void delStoreMappingByTaskIdAndStoreId(@Param("enterpriseId") String enterpriseId,
        @Param("unifyTaskId") Long unifyTaskId, @Param("storeId") String storeId);


    /**
     * 根据父任务id和类型获取MappingId
     *
     * @param enterpriseId
     * @param unifyTaskId
     * @param table
     * @param type
     */
    List<Long> selectMappingIdByTaskIdAndType(@Param("enterpriseId") String enterpriseId,
        @Param("unifyTaskId") Long unifyTaskId, @Param("table") String table, @Param("type") String type);

    List<Long> selectOriginMappingIdByTaskIdAndType(@Param("enterpriseId") String enterpriseId,
                                              @Param("unifyTaskId") Long unifyTaskId, @Param("table") String table, @Param("type") String type);

    /**
     * 根据父任务id和类型获取MappingId
     * 
     * @param enterpriseId
     * @param unifyTaskIdList
     * @param table
     * @param type
     */
    List<TaskMappingDO> selectByTaskIdsAndType(@Param("enterpriseId") String enterpriseId,
        @Param("list") List<Long> unifyTaskIdList, @Param("table") String table, @Param("type") String type);


    List<TaskMappingDO> selectMappingByTaskIds(@Param("enterpriseId") String enterpriseId, @Param("taskIdList") List<Long> taskIdList);


    List<TaskDataMappingDO> getAllDataMappingByUnifyTaskIds(@Param("enterpriseId") String enterpriseId, @Param("taskIdList") List<Long> taskIdList);
}
