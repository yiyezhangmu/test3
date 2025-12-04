package com.coolcollege.intelligent.dao.question;

import com.coolcollege.intelligent.model.question.TbQuestionParentUserMappingDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author zhangchenbiao
 * @date 2022-08-16 03:47
 */
public interface TbQuestionParentUserMappingMapper {
    /**
     *
     * 默认插入方法，只会给有值的字段赋值
	 * 会对传进来的字段做判空处理，如果字段为空，则使用数据库默认字段或者null
     * dateTime:2022-08-16 03:47
     */
    int insertSelective(TbQuestionParentUserMappingDO record, @Param("enterpriseId") String enterpriseId);

    int insert(@Param("enterpriseId") String enterpriseId,
                    @Param("record") TbQuestionParentUserMappingDO record);

    int update(@Param("enterpriseId") String enterpriseId,
               @Param("record") TbQuestionParentUserMappingDO record);

    /**
     *
     * 默认查询方法，通过主键获取所有字段的值
     * dateTime:2022-08-16 03:47
     */
    TbQuestionParentUserMappingDO selectByPrimaryKey(Long id, @Param("enterpriseId") String enterpriseId);

    /**
     *
     * 默认更新方法，根据主键更新，不会把null值更新到数据库，避免覆盖之前有值的
     * dateTime:2022-08-16 03:47
     */
    int updateByPrimaryKeySelective(TbQuestionParentUserMappingDO record, @Param("enterpriseId") String enterpriseId);

    /**
     *
     * 默认更新方法，根据主键物理删除
     * dateTime:2022-08-16 03:47
     */
    int deleteByPrimaryKey(Long id, @Param("enterpriseId") String enterpriseId);


    /**
     *
     * 默认查询方法，通过主键获取所有字段的值
     * dateTime:2022-08-16 03:47
     */
    List<TbQuestionParentUserMappingDO> list(@Param("enterpriseId") String enterpriseId, @Param("handleUserId") String handleUserId
            , @Param("questionParentName") String questionParentName
            , @Param("isHandleUser") Boolean isHandleUser, @Param("isCcUser") Boolean isCcUser
            , @Param("status") Integer status, @Param("questionExpireHandle") Boolean questionExpireHandle, @Param("questionExpireApprove") Boolean questionExpireApprove);

    /**
     *
     * 默认更新方法，根据主键物理删除
     * dateTime:2022-08-16 03:47
     */
    int deleteByUnifyTaskId(@Param("enterpriseId") String enterpriseId, @Param("unifyTaskId") Long unifyTaskId);


    /**
     *
     * 默认查询方法，通过主键获取所有字段的值
     * dateTime:2022-08-16 03:47
     */
    TbQuestionParentUserMappingDO selectByUnifyTaskIdAndUerId(@Param("enterpriseId") String enterpriseId,
                                                              @Param("unifyTaskId") Long unifyTaskId,
                                                              @Param("handleUserId") String handleUserId);

    /**
     *
     * 默认查询方法，通过主键获取所有字段的值
     * dateTime:2022-08-16 03:47
     */
    TbQuestionParentUserMappingDO selectByQuestionParentIdAndUerId(@Param("enterpriseId") String enterpriseId,
                                                              @Param("questionParentId") Long questionParentId,
                                                              @Param("handleUserId") String handleUserId);

    /**
     * 获取任务的人员信息
     * @param enterpriseId
     * @param unifyTaskId
     * @return
     */
    List<TbQuestionParentUserMappingDO> selectByQuestionParentByUnifyTaskId(@Param("enterpriseId") String enterpriseId, @Param("unifyTaskId")Long unifyTaskId);
}