package com.coolcollege.intelligent.dao.activity;

import com.coolcollege.intelligent.model.activity.entity.ActivityLikeDO;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author zhangchenbiao
 * @date 2023-07-03 08:23
 */
public interface ActivityLikeMapper {
    /**
     *
     * 默认插入方法，只会给有值的字段赋值
	 * 会对传进来的字段做判空处理，如果字段为空，则使用数据库默认字段或者null
     * dateTime:2023-07-03 08:23
     */
    int addOrCancelActivityLike(@Param("record") ActivityLikeDO record, @Param("enterpriseId") String enterpriseId);

    /**
     *
     * 默认更新方法，根据主键更新，不会把null值更新到数据库，避免覆盖之前有值的
     * dateTime:2023-07-03 08:23
     */
    int updateByPrimaryKeySelective(@Param("record") ActivityLikeDO record, @Param("enterpriseId") String enterpriseId);

    /**
     * 获取点赞列表
     * @param enterpriseId
     * @param targetId
     * @param likeType
     * @return
     */
    Page<ActivityLikeDO> getLikePage(@Param("enterpriseId") String enterpriseId, @Param("targetId") Long targetId, @Param("likeType")Integer likeType);


    /**
     * 获取哪些目标被点赞过
     * @param enterpriseId
     * @param userId
     * @param targetIds
     * @param likeType
     * @return
     */
    List<Long> getUserLikeTargetIds(@Param("enterpriseId") String enterpriseId, @Param("userId") String userId, @Param("targetIds") List<Long> targetIds, @Param("likeType") Integer likeType);

    /**
     * 获取点赞数量
     * @param enterpriseId
     * @param targetId
     * @param likeType
     * @return
     */
    Integer getLikeCount(@Param("enterpriseId") String enterpriseId, @Param("targetId") Long targetId, @Param("likeType") Integer likeType);
}