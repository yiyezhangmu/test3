package com.coolcollege.intelligent.dao.qyy;


import com.coolcollege.intelligent.model.qyy.QyyConversationSceneDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author zhangchenbiao
 * @date 2023-04-14 04:10
 */
public interface QyyConversationSceneMapper {
    /**
     *
     * 默认插入方法，只会给有值的字段赋值
	 * 会对传进来的字段做判空处理，如果字段为空，则使用数据库默认字段或者null
     * dateTime:2023-04-14 04:10
     */
    int insertSelective(QyyConversationSceneDO record, @Param("enterpriseId") String enterpriseId);

    /**
     *
     * 默认更新方法，根据主键更新，不会把null值更新到数据库，避免覆盖之前有值的
     * dateTime:2023-04-14 04:10
     */
    int updateByPrimaryKeySelective(QyyConversationSceneDO record, @Param("enterpriseId") String enterpriseId);


    List<QyyConversationSceneDO> getConversationScene(@Param("enterpriseId") String enterpriseId, @Param("sceneCode") String sceneCode);

    /**
     * 获取场景  更加权限code
     * @param enterpriseId
     * @param authCodes
     * @return
     */
    List<QyyConversationSceneDO> getConversationSceneByAuthCodes(@Param("enterpriseId") String enterpriseId, @Param("authCodes") List<String> authCodes);


    /**
     * 获取所有场景权限
     * @param enterpriseId
     * @return
     */
    List<QyyConversationSceneDO> getAllConversationScene(@Param("enterpriseId") String enterpriseId);
}