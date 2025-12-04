package com.coolcollege.intelligent.dao.unifytask;

import com.coolcollege.intelligent.model.unifytask.UnifyTaskParentUserDO;
import com.coolcollege.intelligent.model.unifytask.query.DisplayQuery;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author zhangnan
 * @date 2022-02-23 09:50
 */
public interface UnifyTaskParentUserMapper {
    /**
     *
     * 默认插入方法，只会给有值的字段赋值
	 * 会对传进来的字段做判空处理，如果字段为空，则使用数据库默认字段或者null
     * dateTime:2022-02-23 09:50
     */
    int insertSelective(@Param("record") UnifyTaskParentUserDO record, @Param("enterpriseId") String enterpriseId);

    /**
     *
     * 默认查询方法，通过主键获取所有字段的值
     * dateTime:2022-02-23 09:50
     */
    UnifyTaskParentUserDO selectByPrimaryKey(@Param("id") Long id, @Param("enterpriseId") String enterpriseId);

    /**
     *
     * 默认更新方法，根据主键更新，不会把null值更新到数据库，避免覆盖之前有值的
     * dateTime:2022-02-23 09:50
     */
    int updateByPrimaryKeySelective(@Param("record")UnifyTaskParentUserDO record, @Param("enterpriseId") String enterpriseId);

    /**
     *
     * 默认更新方法，根据主键物理删除
     * dateTime:2022-02-23 09:50
     */
    int deleteByPrimaryKey(@Param("id") Long id, @Param("enterpriseId") String enterpriseId);

    /**
     * 新增或更新
     * @param enterpriseId 企业id
     * @param userDOList List<UnifyTaskParentUserDO>
     * @return 执行条数
     */
    int batchInsertOrUpdate(@Param("enterpriseId") String enterpriseId, @Param("list") List<UnifyTaskParentUserDO> userDOList);

    /**
     * 根据父任务id删除
     * @param enterpriseId 企业id
     * @param unifyTaskId 父任务id
     * @return int
     */
    int deleteByUnifyTaskId(@Param("enterpriseId") String enterpriseId, @Param("unifyTaskId") Long unifyTaskId);

    /**
     * 根据父任务id更新状态
     * @param enterpriseId 企业id
     * @param unifyTaskId 父任务id
     * @param status 父任务状态
     * @return int
     */
    int updateParentStatusByUnifyTaskId(@Param("enterpriseId") String enterpriseId, @Param("unifyTaskId") Long unifyTaskId, @Param("parentStatus") String status);

    /**
     * 根据处理人，审批人，复审人查询父任务
     * @param enterpriseId 企业id
     * @param userId 操作人id
     * @param query DisplayQuery
     * @return List<UnifyTaskParentUserDO>
     */
    List<UnifyTaskParentUserDO> selectByUserId(@Param("enterpriseId") String enterpriseId, @Param("userId") String userId, @Param("query") DisplayQuery query);

    /**
     * 查询陈列父任务统计
     * @param enterpriseId 企业id
     * @param userId 操作人id
     * @param taskType 任务类型
     * @param status 任务状态
     * @return Integer
     */
    Integer selectDisplayParentStatistics(@Param("enterpriseId")String enterpriseId, @Param("userId")String userId, @Param("taskType")String taskType, @Param("status")String status);
}