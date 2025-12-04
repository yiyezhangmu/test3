package com.coolcollege.intelligent.dao.supervision;

import com.coolcollege.intelligent.model.supervision.SupervisionTaskDO;
import com.coolcollege.intelligent.model.supervision.SupervisionTaskParentDO;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * @author wxp
 * @date 2023-02-01 02:19
 */
public interface SupervisionTaskParentMapper {
    /**
     *
     * 默认插入方法，只会给有值的字段赋值
	 * 会对传进来的字段做判空处理，如果字段为空，则使用数据库默认字段或者null
     * dateTime:2023-02-01 02:19
     */
    int insertSelective(@Param("record") SupervisionTaskParentDO record, @Param("enterpriseId") String enterpriseId);

    /**
     *
     * 默认查询方法，通过主键获取所有字段的值
     * dateTime:2023-02-01 02:19
     */
    SupervisionTaskParentDO selectByPrimaryKey(@Param("id") Long id, @Param("enterpriseId") String enterpriseId);

    /**
     *
     * 默认更新方法，根据主键更新，不会把null值更新到数据库，避免覆盖之前有值的
     * dateTime:2023-02-01 02:19
     */
    int updateByPrimaryKeySelective(@Param("record") SupervisionTaskParentDO record, @Param("enterpriseId") String enterpriseId);

    /**
     *
     * 默认更新方法，根据主键物理删除
     * dateTime:2023-02-01 02:19
     */
    int deleteByPrimaryKey(@Param("id") Long id, @Param("enterpriseId") String enterpriseId);


    /**
     * 任务列表
     * @param enterpriseId
     * @param taskName
     * @param startTime
     * @param endTime
     * @param status
     * @return
     */
    List<SupervisionTaskParentDO> listByCondition(@Param("enterpriseId") String enterpriseId,
                                                  @Param("keyWords") String keyWords,
                                                  @Param("startTime") Date startTime,
                                                  @Param("endTime") Date endTime,
                                                  @Param("statusList") List<Integer> statusList,
                                                  @Param("supervisionTaskPriorityList") List<String> supervisionTaskPriorityList,
                                                  @Param("taskGroupingList") List<String> taskGroupingList,
                                                  @Param("tags") List<String> tags);

    /**
     * 任务是否取消
     * @param enterpriseId
     * @param taskId
     * @return
     */
    int taskParentCancel(@Param("enterpriseId") String enterpriseId, @Param("taskId") Long taskId);

    /**
     * 任务删除
     * @param enterpriseId
     * @param taskId
     * @return
     */
    int taskParentDel(@Param("enterpriseId") String enterpriseId, @Param("taskId") Long taskId);

    List<SupervisionTaskParentDO> listByTaskIdList(@Param("enterpriseId") String enterpriseId, @Param("taskIdList") List<Long> taskIdList);


    /**
     * 父任务 失效状态
     * @param enterpriseId
     * @param id
     * @return
     */
    int updateFailureState(@Param("enterpriseId") String enterpriseId, @Param("id") Long id);
}