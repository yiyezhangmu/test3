package com.coolcollege.intelligent.dao.question;

import com.coolcollege.intelligent.model.question.TbQuestionParentInfoDO;
import com.coolcollege.intelligent.model.question.request.QuestionParentRequest;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author zhangchenbiao
 * @date 2022-08-04 11:31
 */
public interface TbQuestionParentInfoMapper {
    /**
     *
     * 默认插入方法，只会给有值的字段赋值
	 * 会对传进来的字段做判空处理，如果字段为空，则使用数据库默认字段或者null
     * dateTime:2022-08-04 11:31
     */
    int insertSelective(@Param("enterpriseId") String enterpriseId, @Param("record") TbQuestionParentInfoDO record);

    /**
     *
     * 默认查询方法，通过主键获取所有字段的值
     * dateTime:2022-08-04 11:31
     */
    TbQuestionParentInfoDO selectByUnifyTaskId(@Param("enterpriseId") String enterpriseId, @Param("unifyTaskId")Long unifyTaskId);
    /**
     *
     * 默认查询方法，通过主键获取所有字段的值
     * dateTime:2022-08-04 11:31
     */
    TbQuestionParentInfoDO selectByPrimaryKey( @Param("enterpriseId") String enterpriseId, Long id);

    /**
     *
     * 默认更新方法，根据主键更新，不会把null值更新到数据库，避免覆盖之前有值的
     * dateTime:2022-08-04 11:31
     */
    int updateByPrimaryKeySelective(@Param("enterpriseId") String enterpriseId, @Param("record") TbQuestionParentInfoDO record);

    /**
     *
     * 默认更新方法，根据主键物理删除
     * dateTime:2022-08-04 11:31
     */
    int deleteByPrimaryKey(@Param("enterpriseId") String enterpriseId, Long id);

    /**
     *
     * 查询列表
     */
    List<TbQuestionParentInfoDO> list(@Param("enterpriseId") String enterpriseId, @Param("params") QuestionParentRequest request);

    /**
     *
     * 默认查询方法，通过主键获取所有字段的值
     * dateTime:2022-08-04 11:31
     */
    List<TbQuestionParentInfoDO> selectByIdList( @Param("enterpriseId") String enterpriseId, @Param("idList") List<Long> idList);

    /**
     *
     * 默认更新方法，根据主键更新，不会把null值更新到数据库，避免覆盖之前有值的
     * dateTime:2022-08-04 11:31
     */
    int addFinishNum(@Param("enterpriseId") String enterpriseId, @Param("id") Long id);

    /**
     * 工单数量
     * @param enterpriseId
     * @param request
     * @return
     */
    Long questionListCount(@Param("enterpriseId") String enterpriseId, @Param("params") QuestionParentRequest request);

    /**
     *
     * 默认查询方法，通过主键获取所有字段的值
     * dateTime:2022-08-04 11:31
     */
    List<TbQuestionParentInfoDO> selectByUnifyTaskIds(@Param("enterpriseId") String enterpriseId, @Param("unifyTaskIds")List<Long> unifyTaskIds);

    Long selectCount(@Param("enterpriseId") String enterpriseId,
                     @Param("userId") String userId,
                     @Param("questionExpireHandle") Boolean questionExpireHandle,
                     @Param("questionExpireApprove") Boolean questionExpireApprove,
                     @Param("status") Integer status,
                     @Param("isHandleUser") Boolean isHandleUser);

}