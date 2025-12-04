package com.coolcollege.intelligent.dao.user;

import com.coolcollege.intelligent.model.user.UserCollectStoreDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author zhangchenbiao
 * @date 2022-12-20 11:27
 */
public interface UserCollectStoreMapper {
    /**
     *
     * 默认插入方法，只会给有值的字段赋值
	 * 会对传进来的字段做判空处理，如果字段为空，则使用数据库默认字段或者null
     * dateTime:2022-12-20 11:27
     */
    int insertSelective(@Param("record") UserCollectStoreDO record, @Param("enterpriseId") String enterpriseId);

    /**
     *
     * 默认查询方法，通过主键获取所有字段的值
     * dateTime:2022-12-20 11:27
     */
    UserCollectStoreDO selectByPrimaryKey(Long id, @Param("enterpriseId") String enterpriseId);

    /**
     *
     * 默认更新方法，根据主键更新，不会把null值更新到数据库，避免覆盖之前有值的
     * dateTime:2022-12-20 11:27
     */
    int updateByPrimaryKeySelective(UserCollectStoreDO record, @Param("enterpriseId") String enterpriseId);

    /**
     *
     * 默认更新方法，根据主键物理删除
     * dateTime:2022-12-20 11:27
     */
    int deleteByPrimaryKey(Long id, @Param("enterpriseId") String enterpriseId);

    /**
     * 删除用户门店收藏
     * @param enterpriseId
     * @param userId
     * @param storeId
     * @return
     */
    int deleteUserCollectStore(@Param("enterpriseId") String enterpriseId, @Param("userId") String userId, @Param("storeId") String storeId);

    /**
     * 获取用户收藏的门店
     * @param enterpriseId
     * @param userId
     * @return
     */
    List<UserCollectStoreDO> getUserCollectStore(@Param("enterpriseId") String enterpriseId, @Param("userId") String userId);
}