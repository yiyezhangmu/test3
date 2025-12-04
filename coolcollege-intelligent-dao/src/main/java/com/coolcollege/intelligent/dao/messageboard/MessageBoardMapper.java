package com.coolcollege.intelligent.dao.messageboard;

import com.coolcollege.intelligent.model.messageboard.entity.MessageBoardDO;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Param;

/**
 * @author wxp
 * @date 2024-07-29 16:24
 */
public interface MessageBoardMapper {
    /**
     *
     * 默认插入方法，只会给有值的字段赋值
	 * 会对传进来的字段做判空处理，如果字段为空，则使用数据库默认字段或者null
     * dateTime:2024-07-29 02:13
     */
    int insertSelective(@Param("record")MessageBoardDO record, @Param("enterpriseId") String enterpriseId);

    /**
     *
     * 默认查询方法，通过主键获取所有字段的值
     * dateTime:2024-07-29 02:13
     */
    MessageBoardDO selectByPrimaryKey(Long id, @Param("enterpriseId") String enterpriseId);

    /**
     *
     * 默认更新方法，根据主键更新，不会把null值更新到数据库，避免覆盖之前有值的
     * dateTime:2024-07-29 02:13
     */
    int updateByPrimaryKeySelective(@Param("record")MessageBoardDO record, @Param("enterpriseId") String enterpriseId);

    /**
     *
     * 默认更新方法，根据主键物理删除
     * dateTime:2024-07-29 02:13
     */
    int deleteByPrimaryKey(Long id, @Param("enterpriseId") String enterpriseId);


    /**
     * 获取留言列表
     * @param enterpriseId
     * @param businessId
     * @param businessType
     * @return
     */
    Page<MessageBoardDO> getMessagePage(@Param("enterpriseId") String enterpriseId, @Param("businessId") String businessId, @Param("businessType")String businessType);

    /**
     * 获取点赞记录
     * @param enterpriseId
     * @param businessId
     * @param businessType
     * @param createUserId
     * @return
     */
    MessageBoardDO getLikeRecord(@Param("enterpriseId") String enterpriseId, @Param("businessId") String businessId, @Param("businessType") String businessType, @Param("createUserId") String createUserId);

}