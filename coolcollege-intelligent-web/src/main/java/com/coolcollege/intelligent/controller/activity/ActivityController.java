package com.coolcollege.intelligent.controller.activity;

import com.cool.store.response.ResponseResult;
import com.coolcollege.intelligent.model.activity.dto.*;
import com.coolcollege.intelligent.model.activity.vo.*;
import com.coolcollege.intelligent.model.common.IdDTO;
import com.coolcollege.intelligent.model.impoetexcel.ImportTaskDO;
import com.coolcollege.intelligent.model.userholder.UserHolder;
import com.coolcollege.intelligent.service.activity.ActivityService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.text.MessageFormat;
import java.util.Date;
import java.util.List;

/**
 * @author zhangchenbiao
 * @FileName: ActivityController
 * @Description:
 * @date 2023-07-03 20:32
 */
@Api(tags = "活动管理")
@RestController
@RequestMapping("/v3/enterprises/{enterprise-id}/activity")
@Slf4j
public class ActivityController {

    @Resource
    private ActivityService activityService;

    @ApiOperation("H5获取活动列表")
    @GetMapping("/h5/page")
    public ResponseResult<PageInfo<ActivityH5PageVO>> getH5ActivityPage(@PathVariable("enterprise-id") String enterpriseId, @RequestParam("pageNum")Integer pageNum, @RequestParam("pageSize") Integer pageSize){
        DataSourceHelper.changeToMy();
        return ResponseResult.success(activityService.getH5ActivityPage(enterpriseId, UserHolder.getUser().getUserId(), pageNum, pageSize));
    }

    @ApiOperation("H5获取活动详情")
    @GetMapping("/h5/detail")
    public ResponseResult<ActivityInfoH5VO> getH5ActivityDetail(@PathVariable("enterprise-id") String enterpriseId, @RequestParam("activityId")Long activityId){
        DataSourceHelper.changeToMy();
        return ResponseResult.success(activityService.getH5ActivityDetail(enterpriseId, UserHolder.getUser().getUserId(), activityId));
    }

    @ApiOperation("PC获取活动列表")
    @GetMapping("/page")
    public ResponseResult<PageInfo<ActivityPCPageVO>> getPCActivityPage(@PathVariable("enterprise-id") String enterpriseId,
                                                                        @RequestParam(value = "activityTitle", required = false) String activityTitle,
                                                                        @RequestParam(value = "status", required = false) Integer status,
                                                                        @RequestParam(value = "startTime", required = false) String startTime,
                                                                        @RequestParam(value = "endTime", required = false) String endTime,
                                                                        @RequestParam("pageNum")Integer pageNum,
                                                                        @RequestParam("pageSize") Integer pageSize){
        DataSourceHelper.changeToMy();
        return ResponseResult.success(activityService.getPCActivityPage(enterpriseId, activityTitle, status, startTime, endTime, pageNum, pageSize));
    }

    @ApiOperation("PC获取活动详情")
    @GetMapping("/detail")
    public ResponseResult<ActivityInfoPCVO> getPCActivityDetail(@PathVariable("enterprise-id") String enterpriseId, @RequestParam("activityId")Long activityId){
        DataSourceHelper.changeToMy();
        return ResponseResult.success(activityService.getPCActivityDetail(enterpriseId, activityId));
    }

    @ApiOperation("新增活动")
    @PostMapping("/add")
    public ResponseResult<Long> addActivity(@PathVariable("enterprise-id") String enterpriseId, @RequestBody AddActivityInfoDTO param){
        DataSourceHelper.changeToMy();
        return ResponseResult.success(activityService.addActivity(enterpriseId, UserHolder.getUser().getUserId(), param));
    }

    @ApiOperation("查询暂存数据")
    @PostMapping("/getStagingActivity")
    public ResponseResult<AddActivityInfoDTO> getStagingActivity(@PathVariable("enterprise-id") String enterpriseId){
        DataSourceHelper.changeToMy();
        return ResponseResult.success(activityService.getStagingActivity(enterpriseId, UserHolder.getUser().getUserId()));
    }


    @ApiOperation("更新活动")
    @PostMapping("/update")
    public ResponseResult<Integer> updateActivity(@PathVariable("enterprise-id") String enterpriseId, @RequestBody UpdateActivityInfoDTO param){
        DataSourceHelper.changeToMy();
        return ResponseResult.success(activityService.updateActivity(enterpriseId, UserHolder.getUser().getUserId(), param));
    }

    @ApiOperation("删除活动")
    @PostMapping("/delete")
    public ResponseResult<Integer> deleteActivity(@PathVariable("enterprise-id") String enterpriseId, @RequestBody IdDTO param){
        DataSourceHelper.changeToMy();
        return ResponseResult.success(activityService.deleteActivity(enterpriseId, UserHolder.getUser().getUserId(), param.getId()));
    }

    @ApiOperation("停用活动")
    @PostMapping("/stop")
    public ResponseResult<Integer> stopActivity(@PathVariable("enterprise-id") String enterpriseId, @RequestBody IdDTO param){
        DataSourceHelper.changeToMy();
        return ResponseResult.success(activityService.stopActivity(enterpriseId, UserHolder.getUser().getUserId(), param.getId()));
    }

    @ApiOperation("获取评论列表")
    @GetMapping("/comment/page")
    public ResponseResult<PageInfo<ActivityCommentPageVO>> getActivityCommentPage(@PathVariable("enterprise-id") String enterpriseId,
                                                                                  @RequestParam("activityId")Long activityId,
                                                                                  @RequestParam(value = "isGetMySelf", required = false)Boolean isGetMySelf,
                                                                                  @RequestParam(value = "isContainsPic", required = false)Boolean isContainsPic,
                                                                                  @RequestParam(value = "orderField",required = false, defaultValue = "createTime") String orderField,
                                                                                  @RequestParam("pageNum")Integer pageNum,
                                                                                  @RequestParam("pageSize") Integer pageSize){
        DataSourceHelper.changeToMy();
        return ResponseResult.success(activityService.getActivityCommentPage(enterpriseId, UserHolder.getUser().getUserId(), activityId, isGetMySelf, isContainsPic, orderField, pageNum, pageSize));
    }

    @ApiOperation("获取评论的回复列表")
    @GetMapping("/comment/reply/page")
    public ResponseResult<PageInfo<ActivityReplyVO>> getActivityReplyPage(@PathVariable("enterprise-id") String enterpriseId,
                                                                          @RequestParam("activityId")Long activityId,
                                                                          @RequestParam("commentId")Long commentId,
                                                                          @RequestParam(value = "isGetMySelf", required = false)Boolean isGetMySelf,
                                                                          @RequestParam("pageNum")Integer pageNum,
                                                                          @RequestParam("pageSize") Integer pageSize){
        DataSourceHelper.changeToMy();
        return ResponseResult.success(activityService.getActivityReplyPage(enterpriseId, UserHolder.getUser().getUserId(), activityId, commentId, isGetMySelf, pageNum, pageSize));
    }

    @ApiOperation("获取点赞列表")
    @GetMapping("/like/page")
    public ResponseResult<PageInfo<ActivityLikePageVO>> getActivityLikePage(@PathVariable("enterprise-id") String enterpriseId, @RequestParam("activityId")Long activityId, @RequestParam("pageNum")Integer pageNum, @RequestParam("pageSize") Integer pageSize){
        DataSourceHelper.changeToMy();
        return ResponseResult.success(activityService.getActivityLikePage(enterpriseId, activityId, pageNum, pageSize));
    }

    @ApiOperation("评论")
    @PostMapping("/comment")
    public ResponseResult<Long> addActivityComment(@PathVariable("enterprise-id") String enterpriseId, @RequestBody ActivityCommentDTO param){
        DataSourceHelper.changeToMy();
        String userId = UserHolder.getUser().getUserId();
        return ResponseResult.success(activityService.addActivityComment(enterpriseId, userId, param));
    }

    @ApiOperation("删除评论")
    @PostMapping("/comment/delete")
    public ResponseResult<Integer> deleteActivityComment(@PathVariable("enterprise-id") String enterpriseId, @RequestBody ActivityCommentReplyIdDTO param){
        String userId = UserHolder.getUser().getUserId();
        DataSourceHelper.changeToMy();
        return ResponseResult.success(activityService.deleteActivityComment(enterpriseId, userId, param.getActivityId(), param.getCommentId()));
    }

    @ApiOperation("回复评论/回复")
    @PostMapping("/reply")
    public ResponseResult<Long> addActivityReply(@PathVariable("enterprise-id") String enterpriseId, @RequestBody ActivityReplyDTO param){
        String userId = UserHolder.getUser().getUserId();
        DataSourceHelper.changeToMy();
        return ResponseResult.success(activityService.addActivityReply(enterpriseId, userId, param));
    }

    @ApiOperation("删除回复")
    @PostMapping("/reply/delete")
    public ResponseResult<Integer> deleteActivityReply(@PathVariable("enterprise-id") String enterpriseId, @RequestBody ActivityCommentReplyIdDTO param){
        String userId = UserHolder.getUser().getUserId();
        DataSourceHelper.changeToMy();
        return ResponseResult.success(activityService.deleteActivityReply(enterpriseId, userId, param.getActivityId(), param.getCommentId(), param.getReplyId()));
    }

    @ApiOperation("点赞(取消点赞)活动/评论")
    @PostMapping("/like")
    public ResponseResult<Boolean> addOrCancelActivityLike(@PathVariable("enterprise-id") String enterpriseId, @RequestBody ActivityLikeDTO param){
        String userId = UserHolder.getUser().getUserId();
        DataSourceHelper.changeToMy();
        return ResponseResult.success(activityService.addOrCancelActivityLike(enterpriseId, userId, param));
    }

    @ApiOperation("活动人员列表数据")
    @PostMapping("/activityUserListExport")
    public ResponseResult<ImportTaskDO> activityUserListExport(@PathVariable("enterprise-id") String enterpriseId,
                                                               @RequestBody ActivityCommentDTO activityCommentDTO){
        DataSourceHelper.changeToMy();
        return ResponseResult.success(activityService.activityUserListExport(UserHolder.getUser(),enterpriseId,activityCommentDTO.getActivityId()));
    }

    @ApiOperation("活动评论内容导出")
    @PostMapping("/activityCommentExport")
    public ResponseResult<ImportTaskDO> activityCommentExport(@PathVariable("enterprise-id") String enterpriseId,
                                                              @RequestBody ActivityCommentDTO activityCommentDTO){
        DataSourceHelper.changeToMy();
        return ResponseResult.success(activityService.activityCommentExport(UserHolder.getUser(),enterpriseId,activityCommentDTO.getActivityId()));
    }

    @ApiOperation("获取评论列表")
    @GetMapping("/comment/getActivityCommentList")
    public ResponseResult<PageInfo<ActivityCommentExportVO>> getActivityCommentList(@PathVariable("enterprise-id") String enterpriseId,
                                                                                    @RequestParam("activityId")Long activityId,
                                                                                    @RequestParam("pageNum")Integer pageNum,
                                                                                    @RequestParam("pageSize") Integer pageSize){
        DataSourceHelper.changeToMy();
        return ResponseResult.success(activityService.getActivityCommentList(enterpriseId,  activityId,pageNum, pageSize));
    }

    @ApiOperation("活动人员列表数据")
    @GetMapping("/comment/getActivityUserList")
    public ResponseResult<List<ActivityUserVO>> getActivityUserList(@PathVariable("enterprise-id") String enterpriseId,
                                                                    @RequestParam("activityId")Long activityId){
        DataSourceHelper.changeToMy();
        return ResponseResult.success(activityService.getActivityUserList(enterpriseId,  activityId));
    }

    @ApiOperation("评论置顶/取消置顶")
    @PostMapping("/comment/topAndUnTop")
    public ResponseResult<Integer> topAndUnTop(@PathVariable("enterprise-id") String enterpriseId, @RequestBody ActivityCommentIdDTO param){
        String userId = UserHolder.getUser().getUserId();
        DataSourceHelper.changeToMy();
        return ResponseResult.success(activityService.topAndUnTop(enterpriseId,  userId, param));
    }

}
