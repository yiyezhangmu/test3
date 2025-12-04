package com.coolcollege.intelligent.dao.metatable;

import com.coolcollege.intelligent.model.metatable.TbMetaTableUserAuthDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author zhangchenbiao
 * @date 2025-04-18 06:29
 */
public interface TbMetaTableUserAuthMapper {


    void batchAddOrUpdate(@Param("enterpriseId")String enterpriseId, @Param("addList") List<TbMetaTableUserAuthDO> addList);

    /**
     * 根据业务id删除
     * @param enterpriseId 企业id
     * @param businessIds 业务id列表
     * @param businessType 业务类型
     */
    void deleteByBusinessIds(@Param("enterpriseId") String enterpriseId, @Param("businessIds") List<String> businessIds, @Param("businessType") String businessType, @Param("filterUserIds") List<String> filterUserIds);


    /**
     * 根据用户的权限表
     * @param enterpriseId
     * @param userId
     * @return
     */
    List<TbMetaTableUserAuthDO> getUserAuthMetaTableList(@Param("enterpriseId") String enterpriseId, @Param("userId") String userId);

    /**
     * 获取有结果查看权限的表
     * @param enterpriseId
     * @param userId
     * @return
     */
    List<TbMetaTableUserAuthDO> getUserAuthViewMetaTableList(@Param("enterpriseId")String enterpriseId, @Param("userId")String userId);

    /**
     * 获取可编辑的表id
     * @param enterpriseId
     * @param userId
     * @param metaTableIds
     * @return
     */
    List<String> getEditAuthTableIds(@Param("enterpriseId") String enterpriseId, @Param("userId") String userId, @Param("metaTableIds") List<Long> metaTableIds);

    /**
     * 获取用户权限表
     * @param enterpriseId
     * @param userId
     * @param metaTableIds
     * @return
     */
    List<TbMetaTableUserAuthDO> getTableAuth(@Param("enterpriseId") String enterpriseId, @Param("userId") String userId, @Param("metaTableIds") List<Long> metaTableIds);
}