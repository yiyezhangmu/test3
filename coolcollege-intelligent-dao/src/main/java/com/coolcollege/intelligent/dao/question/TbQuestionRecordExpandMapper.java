package com.coolcollege.intelligent.dao.question;

import com.coolcollege.intelligent.model.question.TbQuestionRecordExpandDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author zhangchenbiao
 * @date 2021-12-20 07:18
 */
@Mapper
public interface TbQuestionRecordExpandMapper {
    /**
     * 默认插入方法，只会给有值的字段赋值
     * 会对传进来的字段做判空处理，如果字段为空，则使用数据库默认字段或者null
     * dateTime:2021-12-20 07:18
     */
    int insertSelective(@Param("record") TbQuestionRecordExpandDO record, @Param("enterpriseId") String enterpriseId);

    /**
     * 默认查询方法，通过主键获取所有字段的值
     * dateTime:2021-12-20 07:18
     */
    TbQuestionRecordExpandDO selectByPrimaryKey(@Param("id") Long id, @Param("enterpriseId") String enterpriseId);

    /**
     * 默认更新方法，根据主键更新，不会把null值更新到数据库，避免覆盖之前有值的
     * dateTime:2021-12-20 07:18
     */
    int updateByPrimaryKeySelective(@Param("record") TbQuestionRecordExpandDO record, @Param("enterpriseId") String enterpriseId);

    /**
     * 默认更新方法，根据主键物理删除
     * dateTime:2021-12-20 07:18
     */
    int deleteByPrimaryKey(@Param("id") Long id, @Param("enterpriseId") String enterpriseId);

    /**
     * 根据问题工单id列表查询
     *
     * @param enterpriseId      企业id
     * @param questionRecordIds 问题工单id列表
     * @return List<TbQuestionRecordExpandDO>
     */
    List<TbQuestionRecordExpandDO> selectByQuestionRecordIds(@Param("enterpriseId") String enterpriseId, @Param("questionRecordIds") List<Long> questionRecordIds);

    /**
     * 根据问题工单id列表查询
     *
     * @param enterpriseId      企业id
     * @param recordId 问题工单id列表
     * @return List<TbQuestionRecordExpandDO>
     */
    TbQuestionRecordExpandDO selectByRecordId(@Param("enterpriseId") String enterpriseId, @Param("recordId") Long recordId);

}