package com.coolcollege.intelligent.dao.qyy;

import com.coolcollege.intelligent.common.enums.ConversationTypeEnum;
import com.coolcollege.intelligent.model.qyy.QyyRecommendStyleDO;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author zhangchenbiao
 * @date 2023-04-11 03:48
 */
public interface QyyRecommendStyleMapper {
    /**
     *
     * 默认插入方法，只会给有值的字段赋值
	 * 会对传进来的字段做判空处理，如果字段为空，则使用数据库默认字段或者null
     * dateTime:2023-04-11 03:48
     */
    int insertSelective(@Param("enterpriseId") String enterpriseId, @Param("record") QyyRecommendStyleDO record);

    /**
     *
     * 默认更新方法，根据主键更新，不会把null值更新到数据库，避免覆盖之前有值的
     * dateTime:2023-04-11 03:48
     */
    int updateByPrimaryKeySelective(@Param("enterpriseId") String enterpriseId, @Param("record") QyyRecommendStyleDO record);
    int updateByPrimaryKey(@Param("enterpriseId") String enterpriseId, @Param("record") QyyRecommendStyleDO record);

    /**
     * 通过群获取主推款
     * @param enterpriseId
     * @param conversationId
     * @param conversationType
     * @return
     */
    List<QyyRecommendStyleDO> getRecommendStyleByConversationId(@Param("enterpriseId") String enterpriseId, @Param("conversationId") String conversationId, @Param("conversationType") String conversationType);

    /**
     * 获取主推款详情
     * @param enterpriseId
     * @param id
     * @return
     */
    QyyRecommendStyleDO getRecommendStyleDetail(@Param("enterpriseId") String enterpriseId, @Param("id") Long id);

    /**
     * 分页获取主推款
     * @param enterpriseId
     * @param name
     * @return
     */
    Page<QyyRecommendStyleDO> getPCRecommendStylePage(@Param("enterpriseId") String enterpriseId, @Param("name") String name);

    /**
     * 分页获取定时任务主推款
     * @param enterpriseId
     * @param beginTime
     * @param endTime
     * @return
     */
    List<QyyRecommendStyleDO> getTimerRecommendStylePage(@Param("enterpriseId") String enterpriseId, @Param("beginTime") String beginTime, @Param("endTime") String endTime);

    /**
     * 更新发送状态
     * @param enterpriseId
     * @param ids
     * @return
     */
    Integer updateRecommendStyleSendStatus(@Param("enterpriseId") String enterpriseId, @Param("ids") List<Long> ids);
}