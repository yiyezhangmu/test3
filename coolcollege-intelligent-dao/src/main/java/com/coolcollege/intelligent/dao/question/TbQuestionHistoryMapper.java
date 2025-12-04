package com.coolcollege.intelligent.dao.question;

import com.coolcollege.intelligent.model.question.TbQuestionHistoryDO;
import com.coolcollege.intelligent.model.question.vo.TbQuestionHistoryVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author zhangchenbiao
 * @date 2021-12-20 07:18
 */
@Mapper
public interface TbQuestionHistoryMapper {
    /**
     *
     * 默认插入方法，只会给有值的字段赋值
	 * 会对传进来的字段做判空处理，如果字段为空，则使用数据库默认字段或者null
     * dateTime:2021-12-20 07:18
     */
    int insertSelective(TbQuestionHistoryDO record, @Param("enterpriseId") String enterpriseId);

    /**
     *
     * 默认查询方法，通过主键获取所有字段的值
     * dateTime:2021-12-20 07:18
     */
    TbQuestionHistoryDO selectByPrimaryKey(@Param("id")Long id, @Param("enterpriseId") String enterpriseId);

    /**
     *
     * 默认更新方法，根据主键更新，不会把null值更新到数据库，避免覆盖之前有值的
     * dateTime:2021-12-20 07:18
     */
    int updateByPrimaryKeySelective(@Param("record")TbQuestionHistoryDO record, @Param("enterpriseId") String enterpriseId);

    /**
     *
     * 默认更新方法，根据主键物理删除
     * dateTime:2021-12-20 07:18
     */
    int deleteByPrimaryKey(Long id, @Param("enterpriseId") String enterpriseId);

    /**
     * 查询处理记录list
     * @param recordId
     * @param enterpriseId
     * @return
     */
    List<TbQuestionHistoryVO> selectHistoryList(@Param("recordId") Long recordId, @Param("enterpriseId") String enterpriseId);

    /**
     * 查询处理记录list
     * @param list
     * @param enterpriseId
     * @return
     */
    List<TbQuestionHistoryVO> selectHistoryListByRecordIdList(@Param("list") List<Long> list, @Param("enterpriseId") String enterpriseId, @Param("nodeNo") Integer nodeNo);

    /**
     * 查询处理记录list
     * @param list
     * @param enterpriseId
     * @return
     */
    List<Long> selectMaxIdByRecordIdList(@Param("list") List<Long> list, @Param("enterpriseId") String enterpriseId, @Param("nodeNo") String nodeNo);

    /**
     * 查询处理记录list
     * @param list
     * @param enterpriseId
     * @return
     */
    List<TbQuestionHistoryVO> selectLatestHistoryListByIdList(@Param("list") List<Long> list, @Param("enterpriseId") String enterpriseId);

    /**
     * 获取最新一套处理记录
     * @param enterpriseId
     * @param recordId
     * @param nodeNo
     * @return
     */
    TbQuestionHistoryDO selectLatestHistoryListByRecordId(@Param("enterpriseId") String enterpriseId, @Param("recordId") Long recordId, @Param("nodeNo") String nodeNo);

}