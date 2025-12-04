package com.coolcollege.intelligent.dao.qyy;

import com.coolcollege.intelligent.model.qyy.QyyConversationSceneAuthDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author zhangchenbiao
 * @date 2023-04-14 04:10
 */
public interface QyyConversationSceneAuthMapper {
    /**
     *
     * 默认插入方法，只会给有值的字段赋值
	 * 会对传进来的字段做判空处理，如果字段为空，则使用数据库默认字段或者null
     * dateTime:2023-04-14 04:10
     */
    int batchInsertSelective(@Param("insertList") List<QyyConversationSceneAuthDO> insertList, @Param("enterpriseId") String enterpriseId);

    /**
     *
     * 默认更新方法，根据主键更新，不会把null值更新到数据库，避免覆盖之前有值的
     * dateTime:2023-04-14 04:10
     */
    int updateByPrimaryKeySelective(QyyConversationSceneAuthDO record, @Param("enterpriseId") String enterpriseId);

    /**
     * 获取群场景权限
     * @param enterpriseId
     * @param sceneCode
     * @return
     */
    List<QyyConversationSceneAuthDO> getConversationSceneAuth(@Param("enterpriseId") String enterpriseId, @Param("sceneCode") String sceneCode);

    /**
     * 删除权限
     * @param enterpriseId
     * @param sceneCode
     * @return
     */
    int deleteSceneAuth(@Param("enterpriseId") String enterpriseId, @Param("sceneCode") String sceneCode);

    /**
     * 获取权限
     * @param enterpriseId
     * @param roleIds
     * @return
     */
    List<QyyConversationSceneAuthDO> getAuthByRoleIds(@Param("enterpriseId") String enterpriseId, @Param("roleIds") List<Long> roleIds);
}