package com.coolcollege.intelligent.service.activity;

import com.coolcollege.intelligent.model.activity.dto.*;
import com.coolcollege.intelligent.model.activity.vo.*;
import com.coolcollege.intelligent.model.enterprise.EnterpriseUserDO;
import com.coolcollege.intelligent.model.impoetexcel.ImportTaskDO;
import com.coolcollege.intelligent.model.store.dto.StorePathDTO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.github.pagehelper.PageInfo;

import java.util.Currency;
import java.util.Date;

import java.util.List;
import java.util.Map;

/**
 * @author zhangchenbiao
 * @FileName: ActivityService
 * @Description:
 * @date 2023-07-03 16:24
 */
public interface ActivityService {

    /**
     * 小程序获取活动分页
     * @param userId
     * @param pageNum
     * @param pageSize
     * @return
     */
    PageInfo<ActivityH5PageVO> getH5ActivityPage(String enterpriseId, String userId, Integer pageNum, Integer pageSize);

    /**
     * 小程序获取活动详情
     * @param enterpriseId
     * @param userId
     * @param activityId
     * @return
     */
    ActivityInfoH5VO getH5ActivityDetail(String enterpriseId, String userId, Long activityId);

    /**
     * PC端获取活动列表
     * @param enterpriseId
     * @param activityTitle
     * @param status
     * @param startTime
     * @param endTime
     * @param pageNum
     * @param pageSize
     * @return
     */
    PageInfo<ActivityPCPageVO> getPCActivityPage(String enterpriseId, String activityTitle, Integer status, String startTime, String endTime, Integer pageNum, Integer pageSize);

    /**
     * PC端获取活动详情
     * @param enterpriseId
     * @param activityId
     * @return
     */
    ActivityInfoPCVO getPCActivityDetail(String enterpriseId, Long activityId);

    /**
     * 新增活动
     * @param enterpriseId
     * @param userId
     * @param param
     * @return
     */
    Long addActivity(String enterpriseId, String userId, AddActivityInfoDTO param);

    /**
     * 更新活动
     * @param enterpriseId
     * @param userId
     * @param param
     * @return
     */
    Integer updateActivity(String enterpriseId, String userId, UpdateActivityInfoDTO param);

    /**
     * 删除活动
     * @param enterpriseId
     * @param userId
     * @param id
     * @return
     */
    Integer deleteActivity(String enterpriseId, String userId, Long id);

    /**
     * 停用活动
     * @param enterpriseId
     * @param userId
     * @param id
     * @return
     */
    Integer stopActivity(String enterpriseId, String userId, Long id);


    /**
     * 分页活动评论
     * @param enterpriseId
     * @param userId
     * @param activityId
     * @param isGetMySelf
     * @param isContainsPic
     * @param pageNum
     * @param pageSize
     * @return
     */
    PageInfo<ActivityCommentPageVO> getActivityCommentPage(String enterpriseId, String userId, Long activityId, Boolean isGetMySelf, Boolean isContainsPic, String orderField, Integer pageNum, Integer pageSize);


    /**
     * 获取回复列表
     * @param enterpriseId
     * @param currentUserId
     * @param activityId
     * @param commentId
     * @param isGetMySelf
     * @param pageNum
     * @param pageSize
     * @return
     */
    PageInfo<ActivityReplyVO> getActivityReplyPage(String enterpriseId, String currentUserId, Long activityId, Long commentId, Boolean isGetMySelf, Integer pageNum, Integer pageSize);
    /**
     * 获取点赞列表
     * @param activityId
     * @param pageNum
     * @param pageSize
     * @return
     */
    PageInfo<ActivityLikePageVO> getActivityLikePage(String enterpriseId, Long activityId, Integer pageNum, Integer pageSize);

    /**
     * 活动评论
     * @param userId
     * @param param
     * @return
     */
    Long addActivityComment(String enterpriseId, String userId, ActivityCommentDTO param);

    /**
     * 删除活动评论
     * @param userId
     * @param id
     * @return
     */
    Integer deleteActivityComment(String enterpriseId, String userId, Long activityId, Long commentId);

    /**
     * 评论回复
     * @param userId
     * @param param
     * @return
     */
    Long addActivityReply(String enterpriseId, String userId, ActivityReplyDTO param);

    /**
     * 删除回复
     * @param userId
     * @param id
     * @return
     */
    Integer deleteActivityReply(String enterpriseId, String userId, Long activityId, Long commentId, Long replyId);

    /**
     * 活动/评论点赞
     * @param userId
     * @param param
     * @return
     */
    Boolean addOrCancelActivityLike(String enterpriseId, String userId, ActivityLikeDTO param);

    /**
     * 更新评论数量
     * @param param
     */
    void updateCommentCount(ActivityMqMessageDTO param);

    /**
     * 更新回复数量
     * @param param
     */
    void updateReplyCount(ActivityMqMessageDTO param);

    /**
     * 更新点赞数量
     * @param param
     */
    void updateLikeCount(ActivityMqMessageDTO param);

    /**
     * 获取活动人员
     * @param eid
     * @param activityId
     * @param viewRange
     * @return
     */
    List<EnterpriseUserDO> getActivityUserByActivityId(String eid,Long activityId,String viewRange);

    /**
     * 活动人员列表数据
     * @param eid
     * @param activityId
     * @return
     */
    List<ActivityUserVO> getActivityUserList(String eid,Long activityId);


    /**
     * 活动人员列表数据 列表导出
     * @param eid
     * @param activityId
     * @return
     */
    ImportTaskDO activityUserListExport(CurrentUser user, String eid, Long activityId);

    /**
     * 活动点评导出
     * @param user
     * @param eid
     * @param activityId
     * @return
     */
    ImportTaskDO activityCommentExport(CurrentUser user, String eid, Long activityId);

    /**
     * 查询暂存的活动
     * @param enterpriseId
     * @param userId
     * @return
     */
    AddActivityInfoDTO getStagingActivity(String enterpriseId, String userId);

    /**
     * 查询活动点评列表
     * @param eid
     * @param activityId
     * @param pageNum
     * @param pageSize
     * @return
     */
    PageInfo<ActivityCommentExportVO> getActivityCommentList(String eid,Long activityId,Integer pageNum,Integer pageSize);


    /**
     * 更新活动状态
     * @param enterpriseId
     * @param activityId
     */
    void updateActivityStatus(String enterpriseId, Long activityId);

    /**
     * 更新活动评论数量
     * @param enterpriseId
     * @param activityId
     */
    void updateCommentCount(String enterpriseId, Long activityId);

    /**
     * 更新评论回复数量
     * @param enterpriseId
     * @param activityId
     * @param commentId
     */
    void updateCommentReplyCount(String enterpriseId, Long activityId, Long commentId);

    /**
     * 更新点赞数量
     * @param enterpriseId
     * @param activityId
     * @param commentId
     */
    void updateLikeCount(String enterpriseId, Long activityId, Long commentId);


    List<String> getFullRegionNameList(String eid, List<String> storeDOList);

    /**
     * 置顶评论 取消置顶评论
     * @param enterpriseId
     * @param userId
     * @param param
     * @return
     */
    Integer topAndUnTop(String enterpriseId, String userId, ActivityCommentIdDTO param);
}
