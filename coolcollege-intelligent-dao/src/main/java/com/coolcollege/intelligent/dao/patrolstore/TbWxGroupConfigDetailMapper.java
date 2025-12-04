package com.coolcollege.intelligent.dao.patrolstore;

import com.coolcollege.intelligent.model.patrolstore.entity.TbWxGroupConfigDetailDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author zhangchenbiao
 * @date 2024-09-06 11:22
 */
public interface TbWxGroupConfigDetailMapper {
    /**
     *
     * 默认插入方法，只会给有值的字段赋值
	 * 会对传进来的字段做判空处理，如果字段为空，则使用数据库默认字段或者null
     * dateTime:2024-09-06 11:22
     */
    int insertSelective(@Param("record") TbWxGroupConfigDetailDO record, @Param("enterpriseId") String enterpriseId);

    /**
     *
     * 默认更新方法，根据主键更新，不会把null值更新到数据库，避免覆盖之前有值的
     * dateTime:2024-09-06 11:22
     */
    int updateByPrimaryKeySelective(@Param("record") TbWxGroupConfigDetailDO record, @Param("enterpriseId") String enterpriseId);

    /**
     * 批量新增
     * @param list
     * @param enterpriseId
     * @return
     */
    int insertBatch(@Param("list") List<TbWxGroupConfigDetailDO> list, @Param("enterpriseId") String enterpriseId);

    /**
     * 批量删除
     * @param groupId
     * @param userId
     * @param enterpriseId
     * @return
     */
    int removeByGroupId(@Param("groupId") Long groupId, @Param("userId") String userId, @Param("enterpriseId") String enterpriseId);

    /**
     * 根据群组id查询明细
     * @param groupId
     * @param enterpriseId
     * @return
     */
    List<TbWxGroupConfigDetailDO> getListByGroupId(@Param("groupId") Long groupId, @Param("enterpriseId") String enterpriseId);

    /**
     * 根据群组ids查询明细
     * @param groupIds
     * @param enterpriseId
     * @return
     */
    List<TbWxGroupConfigDetailDO> getListByGroupIds(@Param("groupIds") List<Long> groupIds,  @Param("enterpriseId") String enterpriseId);

    /**
     * 更新明细
     * @param groupId
     * @param userId
     * @param pushAddress
     * @param enterpriseId
     * @return
     */
    int updateByGroupId(@Param("groupId") Long groupId, @Param("userId") String userId, @Param("pushAddress") String pushAddress, @Param("enterpriseId") String enterpriseId);

    /**
     * 根据ids删除数据
     * @param ids
     * @param userId
     * @param enterpriseId
     * @return
     */
    int removeByIds(@Param("ids") List<Long> ids, @Param("userId") String userId, @Param("enterpriseId") String enterpriseId);

    /**
     * 根据用户获取群组明细
     * @param enterpriseId
     * @param userId
     * @return
     */
    List<TbWxGroupConfigDetailDO> getDetailByUserId(@Param("enterpriseId") String enterpriseId, @Param("userId") String userId);
}