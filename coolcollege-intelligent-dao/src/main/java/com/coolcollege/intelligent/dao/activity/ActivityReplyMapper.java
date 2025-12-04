package com.coolcollege.intelligent.dao.activity;

import com.coolcollege.intelligent.model.activity.dto.CommentDTO;
import com.coolcollege.intelligent.model.activity.dto.CommentReplyCountDTO;
import com.coolcollege.intelligent.model.activity.entity.ActivityReplyDO;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author zhangchenbiao
 * @date 2023-07-03 08:23
 */
public interface ActivityReplyMapper {
    /**
     *
     * 默认插入方法，只会给有值的字段赋值
	 * 会对传进来的字段做判空处理，如果字段为空，则使用数据库默认字段或者null
     * dateTime:2023-07-03 08:23
     */
    int insertSelective(@Param("record") ActivityReplyDO record, @Param("enterpriseId") String enterpriseId);

    /**
     *
     * 默认更新方法，根据主键更新，不会把null值更新到数据库，避免覆盖之前有值的
     * dateTime:2023-07-03 08:23
     */
    int updateByPrimaryKeySelective(@Param("record") ActivityReplyDO record, @Param("enterpriseId") String enterpriseId);


    /**
     * 获取我回复的评论id
     * @param enterpriseId
     * @param userId
     * @param activityId
     * @return
     */
    List<Long> getMyReplyCommentIds(@Param("enterpriseId") String enterpriseId,@Param("userId") String userId, @Param("activityId") Long activityId);

    /**
     * 获取评论下面的回复
     * @param enterpriseId
     * @param activityId
     * @param commentIds
     * @param userId
     * @return
     */
    List<ActivityReplyDO> getActivityReplyList(@Param("enterpriseId") String enterpriseId, @Param("activityId") Long activityId, @Param("commentIds") List<Long> commentIds, @Param("userId") String userId);

    /**
     * 分页获取活动回复
     * @param enterpriseId
     * @param userId
     * @param activityId
     * @param commentId
     * @return
     */
    Page<ActivityReplyDO> getActivityReplyPage(@Param("enterpriseId") String enterpriseId, @Param("userId") String userId, @Param("activityId") Long activityId, @Param("commentId")Long commentId);

    /**
     * 获取每条评论的前3条回复
     * @param enterpriseId
     * @param activityId
     * @param commentIds
     * @param userId
     * @return
     */
    List<ActivityReplyDO> getLastThreeReplyGroupComment(@Param("enterpriseId") String enterpriseId, @Param("activityId") Long activityId, @Param("commentIds") List<Long> commentIds, @Param("userId") String userId);


    /**
     * 获取回复
     * @param enterpriseId
     * @param ids
     * @return
     */
    List<ActivityReplyDO> getReplyListByIds(@Param("enterpriseId") String enterpriseId, @Param("ids") List<Long> ids);

    /**
     * 获取评论数量
     * @param enterpriseId
     * @param activityId
     * @param commentIds
     * @return
     */
    List<CommentReplyCountDTO> getCommentReplyCount(@Param("enterpriseId") String enterpriseId,
                                                    @Param("activityId") Long activityId,
                                                    @Param("commentIds") List<Long> commentIds);

    /**
     * 查询评论的第一条回复
     * @param enterpriseId
     * @param activityId
     * @param commentIds
     * @return
     */
    List<ActivityReplyDO> getFistReply(@Param("enterpriseId") String enterpriseId,
                                       @Param("activityId") Long activityId,
                                       @Param("commentIds") List<Long> commentIds);


}