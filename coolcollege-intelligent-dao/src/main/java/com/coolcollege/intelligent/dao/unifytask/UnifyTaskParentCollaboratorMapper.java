package com.coolcollege.intelligent.dao.unifytask;

import com.coolcollege.intelligent.model.unifytask.UnifyTaskParentCollaboratorDO;
import com.coolcollege.intelligent.model.unifytask.dto.UnifyPersonDTO;
import com.coolcollege.intelligent.model.unifytask.query.DisplayQuery;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author zhangchenbiao
 * @date 2023-02-13 01:42
 */
@Mapper
public interface UnifyTaskParentCollaboratorMapper {
    /**
     *
     * 默认插入方法，只会给有值的字段赋值
	 * 会对传进来的字段做判空处理，如果字段为空，则使用数据库默认字段或者null
     * dateTime:2023-02-13 01:42
     */
    int insertSelective(UnifyTaskParentCollaboratorDO record, @Param("enterpriseId") String enterpriseId);

    /**
     * 批量保存
     * @param eid
     * @param list
     * @return: void
     * @Author: xugangkun
     */
    void batchInsertOrUpdate(@Param("eid") String eid, @Param("list") List<UnifyTaskParentCollaboratorDO> list);

    /**
     *
     * 默认查询方法，通过主键获取所有字段的值
     * dateTime:2023-02-13 01:42
     */
    UnifyTaskParentCollaboratorDO selectByPrimaryKey(Long id, @Param("enterpriseId") String enterpriseId);

    /**
     *
     * 默认更新方法，根据主键更新，不会把null值更新到数据库，避免覆盖之前有值的
     * dateTime:2023-02-13 01:42
     */
    int updateByPrimaryKeySelective(UnifyTaskParentCollaboratorDO record, @Param("enterpriseId") String enterpriseId);

    /**
     *
     * 默认更新方法，根据主键物理删除
     * dateTime:2023-02-13 01:42
     */
    int deleteByPrimaryKey(Long id, @Param("enterpriseId") String enterpriseId);

    /**
     *
     * 根据任务id物理删除
     * dateTime:2023-02-13 01:42
     */
    int deleteByTaskId(@Param("enterpriseId") String enterpriseId,  @Param("unifyTaskId") Long unifyTaskId);

    /**
     *
     * 获取协作人列表
     * dateTime:2023-02-13 01:42
     */
    List<String> selectCollaboratorIdByTaskId(@Param("enterpriseId") String enterpriseId,  @Param("unifyTaskId") Long unifyTaskId);

    /**
     *
     * 获取协作人列表
     * dateTime:2023-02-13 01:42
     */
    List<UnifyPersonDTO> selectCollaboratorIdByTaskIdList(@Param("enterpriseId") String enterpriseId, @Param("unifyTaskIdList") List<Long> unifyTaskIdList);


    List<UnifyTaskParentCollaboratorDO> selectByCollaboratorId(@Param("enterpriseId") String enterpriseId, @Param("userId") String userId, @Param("query") DisplayQuery query);

    Integer selectDisplayParentStatistics(@Param("enterpriseId") String enterpriseId, @Param("userId") String userId,
                                          @Param("taskType") String taskType,  @Param("status") String status);

    void updateTaskParentStatus(@Param("eid") String eid, @Param("taskId") Long taskId, @Param("parentStatus") String parentStatus);

    List<String> selectByUserId(@Param("enterpriseId") String enterpriseId,
                                @Param("userId") String userId,
                                @Param("taskType") String taskType);
}