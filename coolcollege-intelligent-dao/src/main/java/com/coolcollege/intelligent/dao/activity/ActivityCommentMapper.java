package com.coolcollege.intelligent.dao.activity;

import com.coolcollege.intelligent.model.activity.dto.CommentCountDTO;
import com.coolcollege.intelligent.model.activity.dto.ActivityCommentCountDTO;
import com.coolcollege.intelligent.model.activity.dto.CommentDTO;
import com.coolcollege.intelligent.model.activity.entity.ActivityCommentDO;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author zhangchenbiao
 * @date 2023-07-03 08:23
 */
public interface ActivityCommentMapper {
    /**
     *
     * 默认插入方法，只会给有值的字段赋值
	 * 会对传进来的字段做判空处理，如果字段为空，则使用数据库默认字段或者null
     * dateTime:2023-07-03 08:23
     */
    int insertSelective(@Param("record") ActivityCommentDO record, @Param("enterpriseId") String enterpriseId);

    /**
     *
     * 默认更新方法，根据主键更新，不会把null值更新到数据库，避免覆盖之前有值的
     * dateTime:2023-07-03 08:23
     */
    int updateByPrimaryKeySelective(@Param("record") ActivityCommentDO record, @Param("enterpriseId") String enterpriseId);


    ActivityCommentDO selectByPrimaryKeySelective(@Param("id") Long id, @Param("enterpriseId") String enterpriseId);

    /**
     * 获取评论列表
     * @param enterpriseId
     * @param userId
     * @param activityId
     * @param commentIds
     * @param isContainsPic
     * @return
     */
    Page<ActivityCommentDO> getActivityCommentPage(@Param("enterpriseId") String enterpriseId, @Param("activityId") Long activityId,
                                                   @Param("commentIds") List<Long> commentIds, @Param("isContainsPic") Boolean isContainsPic, @Param("orderField") String orderField);


    /**
     *
     * @param enterpriseId
     * @param activityId
     * @return
     */
    Page<ActivityCommentDO> getActivityCommentList(@Param("enterpriseId") String enterpriseId,@Param("activityId") Long activityId);


    /**
     * 获取评论数量
     * @param enterpriseId
     * @param activityId
     * @return
     */
    ActivityCommentCountDTO getActivityCommentCount(@Param("enterpriseId") String enterpriseId, @Param("activityId") Long activityId);

    /**
     * 查询评论人与评论次数
     * @param enterpriseId
     * @param activityId
     * @return
     */
    List<CommentCountDTO> queryCommentUserCommentCount(@Param("enterpriseId") String enterpriseId, @Param("activityId") Long activityId);

    /**
     * 查询点评数量 count
     * @param enterpriseId
     * @param activityId
     * @return
     */
    Long queryActivityCommentCount(@Param("enterpriseId") String enterpriseId, @Param("activityId") Long activityId);


    /**
     * 获取用户某个活动的评论id
     * @param enterpriseId
     * @param activityId
     * @param userId
     * @return
     */
    List<Long> getActivityCommentIdsByUserId(@Param("enterpriseId") String enterpriseId, @Param("activityId") Long activityId, @Param("userId") String userId);

    /**
     * 评论置顶/取消置顶
     * @param enterpriseId
     * @param activityId
     * @param commentId
     * @return
     */
    Integer topAndUnTop(@Param("enterpriseId") String enterpriseId, @Param("activityId") Long activityId, @Param("commentId") Long commentId);

    /**
     * 获取下一个评论楼层
     * @param enterpriseId
     * @param activityId
     * @return
     */
    Integer getNextFloorNum(@Param("enterpriseId") String enterpriseId, @Param("activityId") Long activityId);

}