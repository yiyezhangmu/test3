package com.coolcollege.intelligent.dao.supervision;

import com.coolcollege.intelligent.model.supervision.SupervisionApproveDO;
import com.coolcollege.intelligent.model.supervision.dto.SupervisionApproveCountDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author zhangchenbiao
 * @date 2023-04-10 03:56
 */
public interface SupervisionApproveMapper {
    /**
     *
     * 默认插入方法，只会给有值的字段赋值
	 * 会对传进来的字段做判空处理，如果字段为空，则使用数据库默认字段或者null
     * dateTime:2023-04-10 03:56
     */
    int insertSelective(@Param("record") SupervisionApproveDO record, @Param("enterpriseId") String enterpriseId);

    /**
     *
     * 默认查询方法，通过主键获取所有字段的值
     * dateTime:2023-04-10 03:56
     */
    SupervisionApproveDO selectByPrimaryKey(Long id, @Param("enterpriseId") String enterpriseId);

    /**
     *
     * 默认更新方法，根据主键更新，不会把null值更新到数据库，避免覆盖之前有值的
     * dateTime:2023-04-10 03:56
     */
    int updateByPrimaryKeySelective(SupervisionApproveDO record, @Param("enterpriseId") String enterpriseId);

    /**
     *
     * 默认更新方法，根据主键物理删除
     * dateTime:2023-04-10 03:56
     */
    int deleteByPrimaryKey(Long id, @Param("enterpriseId") String enterpriseId);


    /**
     *
     * 默认查询方
     * dateTime:2023-04-10 03:56
     */
    List<SupervisionApproveDO> selectByTaskIdList(@Param("enterpriseId") String enterpriseId,
                                                  @Param("taskIdList") List<Long> taskIdList,
                                                  @Param("type") String type);


    /**
     * 批量新增数据
     * @param enterpriseId
     * @param records
     * @return
     */
    Integer batchInsert(@Param("enterpriseId") String enterpriseId,
                        @Param("records") List<SupervisionApproveDO> records);

    /**
     * 批量删除数据
     * @param enterpriseId
     * @param taskIdList
     * @param type
     * @return
     */
    Integer batchDelete(@Param("enterpriseId") String enterpriseId,
                        @Param("taskIdList") List<Long> taskIdList,
                        @Param("type") String type);


    /**
     * 查询审批人是否有审批数据
     * @param enterpriseId
     * @param userId
     * @return
     */
    Integer selectApproveDataByUserId(@Param("enterpriseId") String enterpriseId, @Param("userId") String userId);


    /**
     * 查询审批人 按人任务数据与按门店任务数据
     * @param enterpriseId
     * @param userId
     * @return
     */
    SupervisionApproveCountDTO getApproveCountByUserId(@Param("enterpriseId") String enterpriseId, @Param("userId") String userId, @Param("taskName") String taskName);


    List<SupervisionApproveDO> getSupervisionApproveData(@Param("enterpriseId") String enterpriseId,
                                                         @Param("userId") String userId,
                                                         @Param("type") String type,
                                                         @Param("taskName") String taskName);

    SupervisionApproveDO getSupervisionApproveDataByTaskId(@Param("enterpriseId") String enterpriseId,
                                                         @Param("userId") String userId,
                                                         @Param("type") String type,
                                                         @Param("taskId") Long taskId);

    int batchDeleteByTaskParentId(@Param("enterpriseId") String enterpriseId, @Param("taskIds") List<Long> taskIds, @Param("type") String type, @Param("taskPatentId") Long taskPatentId);
}