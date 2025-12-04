package com.coolcollege.intelligent.dao.unifytask;

import com.coolcollege.intelligent.model.unifytask.UnifyTaskPersonDO;
import com.coolcollege.intelligent.model.unifytask.request.GetMiddlePageDataByPersonRequest;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * @author zhangchenbiao
 * @date 2022-04-14 03:19
 */
public interface UnifyTaskPersonMapper {
    /**
     *
     * 默认插入方法，只会给有值的字段赋值
	 * 会对传进来的字段做判空处理，如果字段为空，则使用数据库默认字段或者null
     * dateTime:2022-04-14 03:19
     */
    int insertSelective(@Param("record") UnifyTaskPersonDO record, @Param("enterpriseId") String enterpriseId);

    /**
     *
     * 默认查询方法，通过主键获取所有字段的值
     * dateTime:2022-04-14 03:19
     */
    UnifyTaskPersonDO selectByPrimaryKey(@Param("id") Long id, @Param("enterpriseId") String enterpriseId);

    /**
     *
     * 默认更新方法，根据主键更新，不会把null值更新到数据库，避免覆盖之前有值的
     * dateTime:2022-04-14 03:19
     */
    int updateByPrimaryKeySelective(@Param("record") UnifyTaskPersonDO record, @Param("enterpriseId") String enterpriseId);

    /**
     *
     * 默认更新方法，根据主键物理删除
     * dateTime:2022-04-14 03:19
     */
    int deleteByPrimaryKey(@Param("id") Long id, @Param("enterpriseId") String enterpriseId);

    /**
     * 根据子任务id查询
     * @param enterpriseId
     * @param subTaskId
     * @return
     */
    UnifyTaskPersonDO selectBySubTaskId(@Param("enterpriseId") String enterpriseId, @Param("subTaskId") Long subTaskId);


    /**
     * 查询中间页数据
     * @param enterpriseId
     * @param request
     * @return
     */
    List<UnifyTaskPersonDO> selectMiddlePageData(@Param("enterpriseId") String enterpriseId, @Param("params") GetMiddlePageDataByPersonRequest request);

    /**
     * 根据父任务id删除
     * @param unifyTaskId
     * @param enterpriseId
     */
    void deleteByUnifyTaskId(@Param("enterpriseId") String enterpriseId, @Param("unifyTaskId") Long unifyTaskId);

    void updateTaskPersonStoreIds(@Param("enterpriseId") String enterpriseId, @Param("subTaskId") Long subTaskId, @Param("storeIds") String storeIds);

    void updateTaskPersonCompleteStatus(@Param("enterpriseId") String enterpriseId, @Param("subTaskId") Long subTaskId, @Param("subStatus") String subStatus, @Param("completeTime") Date completeTime);

    Integer countByTaskIdAndStatus(@Param("enterpriseId") String enterpriseId, @Param("taskId") Long taskId, @Param("loopCount") Long loopCount,
                                      @Param("subStatus") String subStatus);

    List<UnifyTaskPersonDO> listBySubTaskIdList(@Param("enterpriseId") String enterpriseId,
                                        @Param("subTaskIdList") List<Long> subTaskIdList);

    /**
     * 根据父任务id和创建时间统计
     * @param enterpriseId
     * @param taskId
     * @param beginDate
     * @param endTime
     * @return
     */
    Integer countByTaskIdAndCreateTime(@Param("enterpriseId") String enterpriseId, @Param("taskId") Long taskId, @Param("beginDate") Date beginDate, @Param("endTime") Date endTime);

    /**
     *
     * @return
     */
    UnifyTaskPersonDO selectByUserIdAndLoopCount(@Param("enterpriseId") String enterpriseId, @Param("taskId") Long taskId
            , @Param("handleUserId") String handleUserId, @Param("loopCount") Long loopCount);

    /**
     *
     * @return
     */
    void updateTaskPersonById(@Param("enterpriseId") String enterpriseId, @Param("subTaskId") Long subTaskId
            , @Param("handleUserId") String handleUserId, @Param("id") Long id);

    /**
     *
     * @return
     */
    List<UnifyTaskPersonDO> selectList(@Param("enterpriseId") String enterpriseId, @Param("subStatus") String subStatus
            , @Param("handleUserId") String handleUserId);
}