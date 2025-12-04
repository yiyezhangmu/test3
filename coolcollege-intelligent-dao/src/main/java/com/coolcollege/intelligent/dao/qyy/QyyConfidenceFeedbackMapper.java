package com.coolcollege.intelligent.dao.qyy;

import com.coolcollege.intelligent.model.qyy.QyyConfidenceFeedbackDO;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * @author zhangchenbiao
 * @date 2023-04-12 07:14
 */
public interface QyyConfidenceFeedbackMapper {
    /**
     *
     * 默认插入方法，只会给有值的字段赋值
	 * 会对传进来的字段做判空处理，如果字段为空，则使用数据库默认字段或者null
     * dateTime:2023-04-12 07:14
     */
//    int insertSelective(@Param("record") QyyConfidenceFeedbackDO record, @Param("enterpriseId") String enterpriseId);
    Long insertSelective(@Param("record") QyyConfidenceFeedbackDO record, @Param("enterpriseId") String enterpriseId);

    /**
     *
     * 默认更新方法，根据主键更新，不会把null值更新到数据库，避免覆盖之前有值的
     * dateTime:2023-04-12 07:14
     */
    int updateByPrimaryKeySelective(@Param("record") QyyConfidenceFeedbackDO record, @Param("enterpriseId") String enterpriseId);


    /**
     * 获取信心反馈详情
     * @param enterpriseId
     * @param id
     * @return
     */
    QyyConfidenceFeedbackDO getConfidenceFeedback(@Param("enterpriseId") String enterpriseId, @Param("id") Long id);


    /**
     * 分页获取信心反馈
     * @param enterpriseId
     * @param userIds
     * @param beginTime
     * @param endTime
     * @return
     */
    Page<QyyConfidenceFeedbackDO> getConfidenceFeedbackPage(@Param("enterpriseId") String enterpriseId, @Param("userIds") List<String> userIds, @Param("beginTime") Date beginTime, @Param("endTime") Date endTime);

    /**
     * 获取条数
     * @param enterpriseId
     * @param userIds
     * @param beginTime
     * @param endTime
     * @return
     */
    Long getConfidenceFeedbackPageCount(@Param("enterpriseId") String enterpriseId, @Param("userIds") List<String> userIds, @Param("beginTime") Date beginTime, @Param("endTime") Date endTime);

    Long getConfidenceFeedbackId(@Param("enterpriseId") String enterpriseId, @Param("insert") QyyConfidenceFeedbackDO insert);
}