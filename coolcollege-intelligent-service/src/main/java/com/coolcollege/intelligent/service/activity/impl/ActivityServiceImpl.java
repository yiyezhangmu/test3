package com.coolcollege.intelligent.service.activity.impl;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.enums.MsgUniteDataTypeEnum;
import com.coolcollege.intelligent.common.enums.activity.ActivityLikeTypeEnum;
import com.coolcollege.intelligent.common.enums.enterprise.UserSelectRangeEnum;
import com.coolcollege.intelligent.common.enums.importexcel.ExportTemplateEnum;
import com.coolcollege.intelligent.common.enums.importexcel.ImportTaskConstant;
import com.coolcollege.intelligent.common.enums.video.ResourceStatusEnum;
import com.coolcollege.intelligent.common.enums.video.UploadTypeEnum;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseUserMapper;
import com.coolcollege.intelligent.common.constant.RedisConstant;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.enums.activity.ActivityStatusEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.dao.enterprise.dao.EnterpriseUserDao;
import com.coolcollege.intelligent.dao.region.RegionDao;
import com.coolcollege.intelligent.dao.region.RegionMapper;
import com.coolcollege.intelligent.dao.store.StoreDao;
import com.coolcollege.intelligent.dao.store.StoreMapper;
import com.coolcollege.intelligent.mapper.activity.*;
import com.coolcollege.intelligent.mapper.user.UserRegionMappingDAO;
import com.coolcollege.intelligent.model.activity.dto.*;
import com.coolcollege.intelligent.model.activity.entity.*;
import com.coolcollege.intelligent.model.activity.vo.*;
import com.coolcollege.intelligent.model.enterprise.EnterpriseUserDO;
import com.coolcollege.intelligent.model.impoetexcel.ImportTaskDO;
import com.coolcollege.intelligent.model.msg.MsgUniteData;
import com.coolcollege.intelligent.model.patrolstore.TbDataStaTableColumnDO;
import com.coolcollege.intelligent.model.region.RegionDO;
import com.coolcollege.intelligent.model.store.StoreDO;
import com.coolcollege.intelligent.model.store.dto.StorePathDTO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.model.video.SmallVideoDTO;
import com.coolcollege.intelligent.model.video.SmallVideoInfoDTO;
import com.coolcollege.intelligent.model.video.param.SmallVideoParam;
import com.coolcollege.intelligent.producer.SimpleMessageService;
import com.coolcollege.intelligent.service.activity.ActivityService;
import com.coolcollege.intelligent.service.importexcel.ImportTaskService;
import com.coolcollege.intelligent.service.region.RegionService;
import com.coolcollege.intelligent.service.waringText.TextAutoRouteService;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.coolstore.base.enums.RocketMqTagEnum;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author zhangchenbiao
 * @FileName: ActivityServiceImpl
 * @Description:
 * @date 2023-07-03 16:24
 */
@Slf4j
@Service
public class ActivityServiceImpl implements ActivityService {

    @Resource
    private EnterpriseUserDao enterpriseUserDao;
    @Resource
    private ActivityViewRangeDAO activityViewRangeDAO;
    @Resource
    private ActivityInfoDAO activityInfoDAO;
    @Resource
    private ActivityReplyDAO activityReplyDAO;
    @Resource
    private ActivityCommentDAO activityCommentDAO;
    @Resource
    private ActivityLikeDAO activityLikeDAO;
    @Resource
    private RegionDao regionDao;
    @Resource
    private UserRegionMappingDAO userRegionMappingDAO;
    @Resource
    private RedisUtilPool redisUtilPool;
    @Resource
    private EnterpriseUserMapper enterpriseUserMapper;
    @Resource
    private StoreDao storeDao;
    @Resource
    private SimpleMessageService simpleMessageService;
    @Resource
    private ImportTaskService importTaskService;
    @Resource
    RegionMapper regionMapper;
    @Resource
    TextAutoRouteService textAutoRouteService;
    @Override
    public PageInfo<ActivityH5PageVO> getH5ActivityPage(String enterpriseId, String userId, Integer pageNum, Integer pageSize) {
        EnterpriseUserDO enterpriseUser = enterpriseUserDao.selectByUserId(enterpriseId, userId);
        if(Objects.isNull(enterpriseUser)){
            throw new ServiceException(ErrorCodeEnum.PARAMS_INVALID_ERROR);
        }
        // 查询需要包含区域下级
        List<String> regionIds = getUserAllRegionId(enterpriseId, userId);
        //获取有哪些满足的活动
        List<Long> activityIds = activityViewRangeDAO.getUserViewActivityIds(enterpriseId, userId, regionIds);
        Page<ActivityInfoDO> h5ActivityPage = activityInfoDAO.getH5ActivityPage(enterpriseId, activityIds, pageNum, pageSize);
        List<ActivityH5PageVO> activityList = ActivityH5PageVO.convertVO(h5ActivityPage);
        PageInfo resultPage = new PageInfo<>(h5ActivityPage);
        resultPage.setList(activityList);
        return resultPage;
    }

    /**
     * 获取用户所属区域 路径包含的所有区域id
     * @param enterpriseId
     * @param userId
     * @return
     */
    private List<String> getUserAllRegionId(String enterpriseId, String userId) {
        EnterpriseUserDO enterpriseUserDO = enterpriseUserDao.selectByUserId(enterpriseId, userId);
        List<String> result = Lists.newArrayList();
        if (Objects.nonNull(enterpriseUserDO) && StringUtils.isNotBlank(enterpriseUserDO.getUserRegionIds())) {
            String userRegionIds = enterpriseUserDO.getUserRegionIds();
            List<String> regionPathList = new ArrayList<>(Arrays.asList(userRegionIds.substring(1, userRegionIds.length() - 1).split(",")));
            Set<String> regionIdSet = Sets.newHashSet();
            for (String regionPath : regionPathList) {
                String[] split = regionPath.split("/");
                for (String s : split) {
                    if (StringUtils.isNotBlank(s)) {
                        regionIdSet.add(s);
                    }
                }
            }
            result.addAll(regionIdSet);
        }
        return result;
    }

    @Override
    public ActivityInfoH5VO getH5ActivityDetail(String enterpriseId, String userId, Long activityId) {
        ActivityInfoDO activityDetail = activityInfoDAO.getActivityDetail(enterpriseId, activityId);
        if(Objects.isNull(activityDetail)){
            return null;
        }
        if(Objects.nonNull(activityDetail) && UserSelectRangeEnum.DEFINE.getCode().equals(activityDetail.getViewRangeType())){
            List<ActivityViewRangeDO> viewRangeList = activityViewRangeDAO.getViewRangeList(enterpriseId, activityId);
            List<String> userRegionIds = getUserAllRegionId(enterpriseId, userId);
            boolean isHaveAuth = true;
            if(CollectionUtils.isNotEmpty(viewRangeList)){
                List<String> personalIds= ListUtils.emptyIfNull(viewRangeList).stream().filter(o -> "personal".equals(o.getNodeType())).map(ActivityViewRangeDO::getPersonalId).collect(Collectors.toList());
                List<String> regionIds= ListUtils.emptyIfNull(viewRangeList).stream().filter(o -> "region".equals(o.getNodeType())).map(ActivityViewRangeDO::getRegionId).collect(Collectors.toList());
                if(!personalIds.contains(userId) && Collections.disjoint(userRegionIds, regionIds)){
                    isHaveAuth = false;
                }
            }else{
                isHaveAuth = false;
            }
            if(!isHaveAuth){
                return new ActivityInfoH5VO(Boolean.FALSE);
            }
        }
        //新增流量次数
        activityInfoDAO.addViewCount(enterpriseId, activityId);
        ActivityInfoH5VO result = ActivityInfoH5VO.convertVO(activityDetail);
        result.setIsHaveAuth(Boolean.TRUE);
        result.setIsLike(activityLikeDAO.isLikeActivity(enterpriseId, activityId, userId));
        return result;
    }

    @Override
    public PageInfo<ActivityPCPageVO> getPCActivityPage(String enterpriseId, String activityTitle, Integer status, String startTime, String endTime, Integer pageNum, Integer pageSize) {
        Page<ActivityInfoDO> activityPage = activityInfoDAO.getPCActivityPage(enterpriseId, activityTitle, status, startTime, endTime, pageNum, pageSize);
        List<ActivityPCPageVO> resultList = Lists.newArrayList();
        if(CollectionUtils.isNotEmpty(activityPage)){
            List<String> userIds = activityPage.stream().map(ActivityInfoDO::getCreateUserId).distinct().collect(Collectors.toList());
            List<String> updateUserIds = activityPage.stream().map(ActivityInfoDO::getUpdateUserId).collect(Collectors.toList());
            if(CollectionUtils.isNotEmpty(updateUserIds)){
                userIds.addAll(updateUserIds);
            }
            Map<String, String> userNameMap = enterpriseUserDao.getUserNameMap(enterpriseId, userIds);
            resultList = ActivityPCPageVO.convertVO(activityPage, userNameMap);
        }
        PageInfo resultPage = new PageInfo(activityPage);
        resultPage.setList(resultList);
        return resultPage;
    }

    @Override
    public ActivityInfoPCVO getPCActivityDetail(String enterpriseId, Long activityId) {
        ActivityInfoDO activityDetail = activityInfoDAO.getActivityDetail(enterpriseId, activityId);
        ActivityInfoPCVO result = ActivityInfoPCVO.convertVO(activityDetail);
        if(Objects.nonNull(activityDetail) && UserSelectRangeEnum.DEFINE.getCode().equals(activityDetail.getViewRangeType())){
            List<ActivityViewRangeDO> viewRangeList = activityViewRangeDAO.getViewRangeList(enterpriseId, activityId);
            List<String> personalIds= ListUtils.emptyIfNull(viewRangeList).stream().filter(o -> "personal".equals(o.getNodeType())).map(ActivityViewRangeDO::getPersonalId).collect(Collectors.toList());
            List<Long> regionIds= ListUtils.emptyIfNull(viewRangeList).stream().filter(o -> "region".equals(o.getNodeType())).map(o->Long.valueOf(o.getRegionId())).collect(Collectors.toList());
            Map<Long, String> regionNameMap = regionDao.getRegionNameMap(enterpriseId, regionIds);
            Map<String, String> userNameMap = enterpriseUserDao.getUserNameMap(enterpriseId, personalIds);
            List<ViewRangeVO> viewRange = ViewRangeVO.convertVO(viewRangeList, regionNameMap, userNameMap);
            result.setViewRangeList(viewRange);
        }
        return result;
    }

    @Override
    public Long addActivity(String enterpriseId, String userId, AddActivityInfoDTO param) {
        String cacheKey = MessageFormat.format("activity_storage:{0}:{1}", enterpriseId, userId);;
        if(Constants.INDEX_ZERO.equals(param.getSubmitFlag())){
            redisUtilPool.setString(cacheKey, JSONObject.toJSONString(param), RedisConstant.SEVEN_DAY);
            return null;
        }
        if(Constants.INDEX_TWO.equals(param.getSubmitFlag())){
            redisUtilPool.delKey(cacheKey);
        }
        if(StringUtils.isAnyBlank(enterpriseId, userId, param.getActivityContent(), param.getCoverImage(), param.getViewRangeType(), param.getActivityTitle()) || Objects.isNull(param.getStartTime()) || Objects.isNull(param.getEndTime())){
            log.info("参数校验失败：{}", JSONObject.toJSONString(param));
            throw new ServiceException(ErrorCodeEnum.PARAMS_INVALID_ERROR);
        }
        Integer needJoinUserCount = 0;
        if(UserSelectRangeEnum.DEFINE.getCode().equals(param.getViewRangeType())){
            List<String> personalIds = param.getViewRangeList().stream().filter(o->"personal".equals(o.getNodeType())).map(ViewRangeDTO::getPersonalId).collect(Collectors.toList());
            List<String> regionIds = param.getViewRangeList().stream().filter(o->"region".equals(o.getNodeType())).map(ViewRangeDTO::getRegionId).collect(Collectors.toList());
            needJoinUserCount = enterpriseUserDao.getUserCountByUserIdOrRegionIds(enterpriseId, personalIds, regionIds);
        }
        if(UserSelectRangeEnum.ALL.getCode().equals(param.getViewRangeType())){
            needJoinUserCount = enterpriseUserDao.getEnterpriseUserCount(enterpriseId);
        }
        if(UserSelectRangeEnum.DEFINE.getCode().equals(param.getViewRangeType()) && CollectionUtils.isEmpty(param.getViewRangeList())){
            log.info("可见范围数据不能为空");
            throw new ServiceException(ErrorCodeEnum.PARAMS_INVALID_ERROR);
        }
        ActivityInfoDO activityInfo = AddActivityInfoDTO.convertDO(param);
        activityInfo.setNeedJoinUserCount(needJoinUserCount);
        activityInfo.setCreateUserId(userId);
        activityInfo.setCreateTime(new Date());
        activityInfo.setUpdateTime(new Date());
        Long activityId = activityInfoDAO.addActivity(enterpriseId, activityInfo);
        if(UserSelectRangeEnum.DEFINE.getCode().equals(param.getViewRangeType())){
            List<ActivityViewRangeDO> viewRangeList = ViewRangeDTO.convertDO(activityId, userId, param.getViewRangeList());
            activityViewRangeDAO.updateViewRange(enterpriseId, activityId, "add", viewRangeList);
        }
        //新增成功 删除暂存数据
        redisUtilPool.delKey(cacheKey);
        //发送异步事件  更新活动状态
        simpleMessageService.send(JSONObject.toJSONString(new ActivityMqMessageDTO(enterpriseId, activityId)), RocketMqTagEnum.ACTIVITY_STATUS_UPDATE, activityInfo.getStartTime().getTime());
        return activityId;
    }

    @Override
    public Integer updateActivity(String enterpriseId, String userId, UpdateActivityInfoDTO param) {
        ActivityInfoDO activityInfo = UpdateActivityInfoDTO.convertDO(param);
        if(UserSelectRangeEnum.DEFINE.getCode().equals(param.getViewRangeType())){
            List<ActivityViewRangeDO> viewRangeList = ViewRangeDTO.convertDO(param.getActivityId(), userId, param.getViewRangeList());
            activityViewRangeDAO.updateViewRange(enterpriseId, param.getActivityId(), "update", viewRangeList);
        }
        Integer needJoinUserCount = 0;
        if(UserSelectRangeEnum.DEFINE.getCode().equals(param.getViewRangeType())){
            List<String> personalIds = param.getViewRangeList().stream().filter(o->"personal".equals(o.getNodeType())).map(ViewRangeDTO::getPersonalId).collect(Collectors.toList());
            List<String> regionIds = param.getViewRangeList().stream().filter(o->"region".equals(o.getNodeType())).map(ViewRangeDTO::getRegionId).collect(Collectors.toList());
            needJoinUserCount = enterpriseUserDao.getUserCountByUserIdOrRegionIds(enterpriseId, personalIds, regionIds);
        }
        if(UserSelectRangeEnum.ALL.getCode().equals(param.getViewRangeType())){
            needJoinUserCount = enterpriseUserDao.getEnterpriseUserCount(enterpriseId);
        }
        activityInfo.setNeedJoinUserCount(needJoinUserCount);
        activityInfo.setUpdateUserId(userId);
        activityInfo.setUpdateTime(new Date());
        //发送异步事件  更新活动状态
        long startDeliverTime = activityInfo.getStartTime().getTime();
        if(startDeliverTime <= System.currentTimeMillis()){
            startDeliverTime = System.currentTimeMillis() + Constants.ONE_MILLISECOND;
        }
        simpleMessageService.send(JSONObject.toJSONString(new ActivityMqMessageDTO(enterpriseId, param.getActivityId())), RocketMqTagEnum.ACTIVITY_STATUS_UPDATE, startDeliverTime);
        return activityInfoDAO.updateActivity(enterpriseId, activityInfo);
    }

    @Override
    public Integer deleteActivity(String enterpriseId, String userId, Long id) {
        if(StringUtils.isAnyBlank(enterpriseId, userId) || Objects.isNull(id)){
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR);
        }
        return activityInfoDAO.deleteActivity(enterpriseId, userId, id);
    }

    @Override
    public Integer stopActivity(String enterpriseId, String userId, Long id) {
        ActivityInfoDO update = new ActivityInfoDO();
        update.setId(id);
        update.setUpdateUserId(userId);
        update.setUpdateTime(new Date());
        update.setStatus(ActivityStatusEnum.STOP.getCode());
        return activityInfoDAO.updateActivity(enterpriseId, update);
    }

    @Override
    public PageInfo<ActivityCommentPageVO> getActivityCommentPage(String enterpriseId, String currentUserId, Long activityId, Boolean isGetMySelf, Boolean isContainsPic, String orderField, Integer pageNum, Integer pageSize) {
        List<Long> commentIds = null;
        String userId = null;
        if(Objects.nonNull(isGetMySelf) && isGetMySelf){
            commentIds = new ArrayList<>();
            //获取该用户关于活动相关的回复
            userId = currentUserId;
            List<Long> myReplyCommentIds = activityReplyDAO.getMyReplyCommentIds(enterpriseId, userId, activityId);
            List<Long> myCommentIds = activityCommentDAO.getActivityCommentIdsByUserId(enterpriseId, activityId, userId);
            if(CollectionUtils.isNotEmpty(myReplyCommentIds)){
                commentIds.addAll(myReplyCommentIds);
            }
            if(CollectionUtils.isNotEmpty(myCommentIds)){
                commentIds.addAll(myCommentIds);
            }
            if(CollectionUtils.isEmpty(commentIds)){
                return new PageInfo<>();
            }
        }
        Page<ActivityCommentDO> activityCommentPage = activityCommentDAO.getActivityCommentPage(enterpriseId, activityId, commentIds, isContainsPic, orderField, pageNum, pageSize);
        List<ActivityCommentPageVO> resultList = Lists.newArrayList();
        if(CollectionUtils.isNotEmpty(activityCommentPage)){
            List<String> userIds = activityCommentPage.stream().map(ActivityCommentDO::getCommentUserId).collect(Collectors.toList());
            //获取评论下的所有回复
            List<Long> allCommentIds = activityCommentPage.stream().map(ActivityCommentDO::getId).collect(Collectors.toList());
            List<ActivityReplyDO> activityReplyList = activityReplyDAO.getLastThreeReplyGroupComment(enterpriseId, activityId, allCommentIds, userId);
            List<String> replyUserIds = activityReplyList.stream().map(ActivityReplyDO::getReplyUserId).collect(Collectors.toList());
            if(CollectionUtils.isNotEmpty(replyUserIds)){
                userIds.addAll(replyUserIds);
            }
            Map<Long, List<ActivityReplyDO>> commentReplyMap = activityReplyList.stream().collect(Collectors.groupingBy(k -> k.getCommentId()));
            List<Long> parentReplyIds = activityReplyList.stream().map(ActivityReplyDO::getParentReplyId).collect(Collectors.toList());
            List<ActivityReplyDO> parentReplyList = activityReplyDAO.getReplyListByIds(enterpriseId, parentReplyIds);
            List<String> repliedUserIds = ListUtils.emptyIfNull(parentReplyList).stream().map(ActivityReplyDO::getReplyUserId).collect(Collectors.toList());
            if(CollectionUtils.isNotEmpty(repliedUserIds)){
                userIds.addAll(repliedUserIds);
            }
            Map<String, EnterpriseUserDO> userMap = enterpriseUserDao.getUserMap(enterpriseId, userIds);
            List<Long> userLikeCommentIds = activityLikeDAO.getUserLikeCommentIds(enterpriseId, currentUserId, allCommentIds);
            resultList = ActivityCommentPageVO.convertVO(activityCommentPage, userMap, commentReplyMap, parentReplyList, userLikeCommentIds);
        }
        PageInfo resultPage = new PageInfo(activityCommentPage);
        resultPage.setList(resultList);
        return resultPage;
    }

    @Override
    public PageInfo<ActivityReplyVO> getActivityReplyPage(String enterpriseId, String currentUserId, Long activityId, Long commentId, Boolean isGetMySelf, Integer pageNum, Integer pageSize) {
        String userId = null;
        if(Objects.nonNull(isGetMySelf) && isGetMySelf){
            //获取该用户关于活动相关的回复
            userId = currentUserId;
        }
        Page<ActivityReplyDO> activityReplyPage = activityReplyDAO.getActivityReplyPage(enterpriseId, userId, activityId, commentId, pageNum, pageSize);
        List<ActivityReplyVO> resultList = Lists.newArrayList();
        if(CollectionUtils.isNotEmpty(activityReplyPage)){
            List<String> userIds = activityReplyPage.stream().map(ActivityReplyDO::getReplyUserId).collect(Collectors.toList());
            List<Long> parentReplyIds = activityReplyPage.stream().map(ActivityReplyDO::getParentReplyId).collect(Collectors.toList());
            List<ActivityReplyDO> parentReplyList = activityReplyDAO.getReplyListByIds(enterpriseId, parentReplyIds);
            List<String> replyUserIds = ListUtils.emptyIfNull(parentReplyList).stream().map(ActivityReplyDO::getReplyUserId).collect(Collectors.toList());
            if(CollectionUtils.isNotEmpty(replyUserIds)){
                userIds.addAll(replyUserIds);
            }
            Map<String, EnterpriseUserDO> userMap = enterpriseUserDao.getUserMap(enterpriseId, userIds);
            resultList = ActivityReplyVO.convertVO(activityReplyPage, userMap, parentReplyList);
        }
        PageInfo resultPage = new PageInfo(activityReplyPage);
        resultPage.setList(resultList);
        return resultPage;
    }

    @Override
    public PageInfo<ActivityLikePageVO> getActivityLikePage(String enterpriseId, Long activityId, Integer pageNum, Integer pageSize) {
        Page<ActivityLikeDO> activityLikePage = activityLikeDAO.getActivityLikePage(enterpriseId, activityId, pageNum, pageSize);
        List<ActivityLikePageVO> resultList = Lists.newArrayList();
        if(CollectionUtils.isNotEmpty(activityLikePage)){
            List<String> userIds = activityLikePage.stream().map(ActivityLikeDO::getLikeUserId).collect(Collectors.toList());
            Map<String, EnterpriseUserDO> userMap = enterpriseUserDao.getUserMap(enterpriseId, userIds);
            resultList = ActivityLikePageVO.convertVO(activityLikePage, userMap);
        }
        PageInfo resultPage = new PageInfo(activityLikePage);
        resultPage.setList(resultList);
        return resultPage;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addActivityComment(String enterpriseId, String userId, ActivityCommentDTO param) {
        if(Objects.isNull(param.getActivityId())){
            throw new ServiceException(ErrorCodeEnum.PARAMS_INVALID_ERROR);
        }
        ActivityCommentDO insert = ActivityCommentDTO.convertDO(param, userId);
        Boolean commentWarning = textAutoRouteService.isCommentWarning(insert.getActivityId(), 1, param.getContent(), enterpriseId);
        if (commentWarning){
            throw new ServiceException(ErrorCodeEnum.WARNING_TEXT);
        }
        Long result = activityCommentDAO.addActivityComment(enterpriseId, insert);
        //视频处理
        if (StringUtils.isNotEmpty(param.getContentVideo())){
            checkVideoHandel(param,result,enterpriseId);
            insert.setContentVideo(param.getContentVideo());
            activityCommentDAO.updateActivityComment(enterpriseId,insert);
        }
        updateCommentCount(enterpriseId, param.getActivityId());
        return result;
    }

    private void checkActivityStatus(String enterpriseId, Long activityId){
        ActivityInfoDO activityInfo = activityInfoDAO.getActivityDetail(enterpriseId, activityId);
        if(Objects.nonNull(activityInfo)){
            throw new ServiceException(ErrorCodeEnum.ACTIVITY_NOT_EXISTS);
        }
        ActivityStatusEnum activityStatus = ActivityStatusEnum.NOT_STARTED;
        if(!ActivityStatusEnum.STOP.getCode().equals(activityInfo.getStatus())){
            long currentTime = System.currentTimeMillis();
            if(currentTime >= activityInfo.getStartTime().getTime() && currentTime <= activityInfo.getEndTime().getTime()){
                //进行中
                activityStatus = ActivityStatusEnum.ONGOING;
            }
            if(currentTime >= activityInfo.getEndTime().getTime()){
                //已结束
                activityStatus = ActivityStatusEnum.END;
            }
        }else{
            activityStatus = ActivityStatusEnum.STOP;
        }
        if(activityStatus.equals(ActivityStatusEnum.NOT_STARTED)){
            throw new ServiceException(ErrorCodeEnum.ACTIVITY_NO_START);
        }
        if(activityStatus.equals(ActivityStatusEnum.END)){
            throw new ServiceException(ErrorCodeEnum.ACTIVITY_END);
        }
        if(activityStatus.equals(ActivityStatusEnum.STOP)){
            throw new ServiceException(ErrorCodeEnum.ACTIVITY_STOP);
        }
    }


    public void checkVideoHandel(ActivityCommentDTO param,Long id, String enterpriseId){
        log.info("活动评论视频转码数据 videoList:{}",JSONObject.toJSONString(param.getContentVideo()));
        if(StringUtils.isEmpty(param.getContentVideo())){
            return;
        }
        List<SmallVideoDTO> smallVideoDTOS = JSONObject.parseArray(param.getContentVideo(), SmallVideoDTO.class);
        if(CollectionUtils.isNotEmpty(smallVideoDTOS)){
            String callbackCache;
            SmallVideoDTO smallVideoCache;
            SmallVideoParam smallVideoParam;
            for (SmallVideoDTO smallVideo : smallVideoDTOS) {
                //如果转码完成
                if(smallVideo.getStatus() != null && smallVideo.getStatus() >= ResourceStatusEnum.TRANSCODE_FINISH.getValue()){
                    continue;
                }
                callbackCache = redisUtilPool.getString(RedisConstant.VIDEO_CALLBACK_CACHE + smallVideo.getVideoId());
                if(StringUtils.isNotBlank(callbackCache)){
                    smallVideoCache = JSONObject.parseObject(callbackCache,SmallVideoDTO.class);
                    if(smallVideoCache !=null && smallVideoCache.getStatus() !=null && smallVideoCache.getStatus() >= 3){
                        BeanUtils.copyProperties(smallVideoCache,smallVideo);
                    }else {
                        smallVideoParam = new SmallVideoParam();
                        setNotCompleteCache(smallVideoParam,smallVideo,enterpriseId,id);
                    }
                }else {
                    smallVideoParam = new SmallVideoParam();
                    setNotCompleteCache(smallVideoParam,smallVideo,enterpriseId,id);
                }
            }
            param.setContentVideo(JSONObject.toJSONString(smallVideoDTOS));
        }
    }


    public void setNotCompleteCache(SmallVideoParam smallVideoParam, SmallVideoDTO smallVideo, String enterpriseId,Long id){
        smallVideoParam.setVideoId(smallVideo.getVideoId());
        smallVideoParam.setUploadType(UploadTypeEnum.ACTIVITY_COMMENT.getValue());
        smallVideoParam.setUploadTime(new Date());
        smallVideoParam.setBusinessId(id);
        smallVideoParam.setEnterpriseId(enterpriseId);
        //存入未转码完成的map，vod回调的时候使用
        redisUtilPool.hashSet(RedisConstant.VIDEO_NOT_COMPLETE_CACHE,smallVideo.getVideoId(), JSONObject.toJSONString(smallVideoParam));
    }

    @Override
    public Integer deleteActivityComment(String enterpriseId, String userId, Long activityId, Long commentId) {
        if(StringUtils.isAnyBlank(enterpriseId, userId) || Objects.isNull(activityId) || Objects.isNull(commentId)){
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR);
        }
        ActivityCommentDO update = new ActivityCommentDO();
        update.setId(commentId);
        update.setUpdateUserId(userId);
        update.setUpdateTime(new Date());
        update.setDeleted(Boolean.TRUE);
        Integer result = activityCommentDAO.updateActivityComment(enterpriseId, update);
        updateCommentCount(enterpriseId, activityId);
        return result;
    }

    @Override
    public Long addActivityReply(String enterpriseId, String userId, ActivityReplyDTO param) {
        if(StringUtils.isAnyBlank(enterpriseId, userId, param.getContent()) || Objects.isNull(param.getActivityId()) || Objects.isNull(param.getCommentId())){
            throw new ServiceException(ErrorCodeEnum.PARAMS_INVALID_ERROR);
        }
        ActivityReplyDO insert = new ActivityReplyDO();
        insert.setActivityId(param.getActivityId());
        insert.setReplyUserId(userId);
        insert.setContent(param.getContent());
        insert.setCommentId(param.getCommentId());
        insert.setParentReplyId(param.getParentReplyId());
        insert.setCreateUserId(userId);
        insert.setUpdateUserId(userId);
        insert.setCreateTime(new Date());
        insert.setUpdateTime(new Date());
        Boolean commentWarning = textAutoRouteService.isCommentWarning(insert.getActivityId(), 2, param.getContent(), enterpriseId);
        if (commentWarning){
            throw new ServiceException(ErrorCodeEnum.WARNING_TEXT);
        }
        Long result = activityReplyDAO.addActivityReply(enterpriseId, insert);
        updateCommentReplyCount(enterpriseId, param.getActivityId(), param.getCommentId());
        return result;
    }

    @Override
    public Integer deleteActivityReply(String enterpriseId, String userId, Long activityId, Long commentId, Long replyId) {
        if(StringUtils.isAnyBlank(enterpriseId, userId) || Objects.isNull(activityId) || Objects.isNull(commentId) || Objects.isNull(replyId)){
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR);
        }
        ActivityReplyDO update = new ActivityReplyDO();
        update.setId(replyId);
        update.setCreateUserId(userId);
        update.setUpdateUserId(userId);
        update.setCreateTime(new Date());
        update.setUpdateTime(new Date());
        update.setDeleted(Boolean.TRUE);
        Integer result = activityReplyDAO.updateActivityReply(enterpriseId, update);
        updateCommentReplyCount(enterpriseId, activityId, commentId);
        return result;
    }

    @Override
    public Boolean addOrCancelActivityLike(String enterpriseId, String userId, ActivityLikeDTO param) {
        if(StringUtils.isAnyBlank(enterpriseId, userId) || Objects.isNull(param.getLikeType()) || Objects.isNull(param.getActivityId())){
            throw new ServiceException(ErrorCodeEnum.PARAMS_INVALID_ERROR);
        }
        ActivityLikeDO like = new ActivityLikeDO();
        if(Constants.INDEX_ZERO.equals(param.getLikeType())){
            like.setTargetId(param.getActivityId());
        }
        if(Constants.INDEX_ONE.equals(param.getLikeType())){
            if(Objects.isNull(param.getCommentId())){
                throw new ServiceException(ErrorCodeEnum.PARAMS_INVALID_ERROR);
            }
            like.setTargetId(param.getCommentId());
        }
        like.setLikeUserId(userId);
        like.setLikeType(param.getLikeType());
        like.setCreateUserId(userId);
        like.setUpdateUserId(userId);
        like.setCreateTime(new Date());
        like.setUpdateTime(new Date());
        activityLikeDAO.addOrCancelActivityLike(enterpriseId, like);
        updateLikeCount(enterpriseId, param.getActivityId(), param.getCommentId());
        return Boolean.TRUE;
    }

    @Override
    public List<EnterpriseUserDO> getActivityUserByActivityId(String eid,Long activityId, String viewRange) {
       if ( UserSelectRangeEnum.ALL.getCode().equals(viewRange)){
           return enterpriseUserMapper.selectAllList(eid);
       }
        List<String> allUserIdList =Lists.newArrayList();
       if ( UserSelectRangeEnum.DEFINE.getCode().equals(viewRange)){
           List<ActivityViewRangeDO> viewRangeList = activityViewRangeDAO.getViewRangeList(eid, activityId);
           List<String> personalIds = ListUtils.emptyIfNull(viewRangeList).stream().filter(x -> StringUtils.isNotBlank(x.getPersonalId()))
                   .map(ActivityViewRangeDO::getPersonalId).collect(Collectors.toList());
           if(CollectionUtils.isNotEmpty(personalIds)) {
               allUserIdList.addAll(personalIds);
           }
           List<String> regionIds = ListUtils.emptyIfNull(viewRangeList).stream().filter(x -> StringUtils.isNotBlank(x.getRegionId()))
                   .map(ActivityViewRangeDO::getRegionId).collect(Collectors.toList());

           if(CollectionUtils.isNotEmpty(regionIds)) {
               List<String> enterpriseUserIds = enterpriseUserMapper.getUserIdsByRegionIdList(eid, regionIds);
               if (CollectionUtils.isNotEmpty(enterpriseUserIds)) {
                   allUserIdList.addAll(enterpriseUserIds);
               }
           }
           //过滤删除的人员
           return enterpriseUserMapper.selectUserRegionIdsByUserList(eid,allUserIdList,Boolean.TRUE);
       }
        return new ArrayList<>();
    }

    @Override
    public List<ActivityUserVO> getActivityUserList(String eid, Long activityId) {
        ActivityInfoDO activityDetail = activityInfoDAO.getActivityDetail(eid, activityId);
        if (activityDetail==null){
            throw new ServiceException(ErrorCodeEnum.PARAMS_INVALID_ERROR);
        }
        List<EnterpriseUserDO> activityUserList = getActivityUserByActivityId(eid, activityId, activityDetail.getViewRangeType());
        List<CommentCountDTO> commentCountDTOS = activityCommentDAO.queryCommentUserCommentCount(eid, activityId);
        Map<String, Integer> CommentCountMap = commentCountDTOS.stream().collect(Collectors.toMap(CommentCountDTO::getCommentUserId, CommentCountDTO::getCommentCount));
        //人员列表
        List<ActivityUserVO> result= new ArrayList<>();

        activityUserList.forEach(x->{
            ActivityUserVO activityUserVO = getActivityUser(eid, x);
            Integer count = CommentCountMap.getOrDefault(x.getUserId(), 0);
            activityUserVO.setParticipateCount(count);
            activityUserVO.setUserId(x.getUserId());
            activityUserVO.setActivityTitle(activityDetail.getActivityTitle());
            activityUserVO.setJobNumber(x.getJobnumber());
            activityUserVO.setUserName(x.getName());
            activityUserVO.setParticipateFlag(count>0);
            result.add(activityUserVO);
        });
        return result;
    }

    @Override
    public ImportTaskDO activityUserListExport(CurrentUser user,String enterpriseId, Long activityId) {
        // 查询导出数量，限流
        // 通过枚举获取文件名称 ExportServiceEnum
        String fileName = ExportTemplateEnum.getByCode(ImportTaskConstant.ACTIVITY_USER);
        // 保存导出任务
        ImportTaskDO importTaskDO = importTaskService.insertExportTask(enterpriseId, fileName, ImportTaskConstant.ACTIVITY_USER);
        // 构造异步导出参数
        ActivityUserDTO msg = new ActivityUserDTO();
        msg.setEnterpriseId(enterpriseId);
        msg.setActivityId(activityId);
        msg.setImportTaskDO(importTaskDO);
        msg.setDbName(user.getDbName());
        MsgUniteData msgUniteData = new MsgUniteData();
        msgUniteData.setData(JSONObject.toJSONString(msg));
        msgUniteData.setMsgType(MsgUniteDataTypeEnum.ACTIVITY_USER.getCode());
        //分开异步导出
        simpleMessageService.send(JSONObject.toJSONString(msgUniteData), RocketMqTagEnum.EXPORT_IMPORT_MESSAGE);
        return importTaskDO;
    }

    @Override
    public ImportTaskDO activityCommentExport(CurrentUser user, String eid, Long activityId) {
        // 查询导出数量，限流
        Long count = activityCommentDAO.queryActivityCommentCount(eid, activityId);
        if (count == null || count == 0L) {
            throw new ServiceException("当前无记录可导出");
        }
        if (count > Constants.MAX_EXPORT_SIZE) {
            throw new ServiceException("导出数据不能超过" + Constants.MAX_EXPORT_SIZE + "条，请缩小导出范围");
        }
        String fileName = ExportTemplateEnum.getByCode(ImportTaskConstant.ACTIVITY_COMMENT);
        // 保存导出任务
        ImportTaskDO importTaskDO = importTaskService.insertExportTask(eid, fileName, ImportTaskConstant.CONFIDENCE_FEEDBACK);
        // 构造异步导出参数
        ActivityCommentExportDTO msg = new ActivityCommentExportDTO();
        msg.setEnterpriseId(eid);
        msg.setTotalNum(count);
        msg.setImportTaskDO(importTaskDO);
        msg.setDbName(user.getDbName());
        msg.setActivityId(activityId);
        msg.setUser(user);
        MsgUniteData msgUniteData = new MsgUniteData();
        msgUniteData.setData(JSONObject.toJSONString(msg));
        msgUniteData.setMsgType(MsgUniteDataTypeEnum.ACTIVITY_COMMENT.getCode());
        //分开异步导出
        simpleMessageService.send(JSONObject.toJSONString(msgUniteData), RocketMqTagEnum.EXPORT_IMPORT_MESSAGE);
        return importTaskDO;
    }

    @Override
    public AddActivityInfoDTO getStagingActivity(String enterpriseId, String userId) {
        String cacheKey = MessageFormat.format("activity_storage:{0}:{1}", enterpriseId, userId);
        String stagingActivity = redisUtilPool.getString(cacheKey);
        if (StringUtils.isNotEmpty(stagingActivity)){
            return JSONObject.parseObject(stagingActivity,AddActivityInfoDTO.class);
        }
        return null;
    }

    @Override
    public PageInfo<ActivityCommentExportVO> getActivityCommentList(String eid,Long activityId,Integer pageNum,Integer pageSize) {
        //查询活动详情
        ActivityInfoDO activityDetail = activityInfoDAO.getActivityDetail(eid, activityId);
        Page<ActivityCommentDO> activityCommentPage = activityCommentDAO.getActivityCommentList(eid, activityId, pageNum, pageSize);
        List<Long> commentIdList = activityCommentPage.stream().map(ActivityCommentDO::getId).collect(Collectors.toList());
        List<String> commentUserIdList = activityCommentPage.stream().map(ActivityCommentDO::getCommentUserId).collect(Collectors.toList());
        List<ActivityCommentExportVO> result = new ArrayList<>();
        Map<Long, Integer> commentReplyCountMap = activityReplyDAO.getCommentReplyCount(eid, activityId, commentIdList);
        List<ActivityReplyDO> activityReplyDOS = activityReplyDAO.getFistReply(eid, activityId, commentIdList);
        Map<Long, ActivityReplyDO> ActivityReplyMap = activityReplyDOS.stream().collect(Collectors.toMap(ActivityReplyDO::getCommentId, date -> date));
        List<String> replyUserIds = activityReplyDOS.stream().map(ActivityReplyDO::getReplyUserId).collect(Collectors.toList());
        commentUserIdList.addAll(replyUserIds);
        List<EnterpriseUserDO> enterpriseUserDOS = enterpriseUserMapper.selectUserRegionIdsByUserList(eid, commentUserIdList,Boolean.FALSE);
        Map<String, EnterpriseUserDO> userMap = enterpriseUserDOS.stream().collect(Collectors.toMap(EnterpriseUserDO::getUserId, date->date));
        List<ActivityUserVO>  enterpriseVO = new ArrayList<>();
        enterpriseUserDOS.forEach(x->{
                enterpriseVO.add(getActivityUser(eid,x));
        });
        Map<String, ActivityUserVO> userVOMap = enterpriseVO.stream().collect(Collectors.toMap(ActivityUserVO::getUserId, date -> date));

        activityCommentPage.forEach(x->{
            ActivityCommentExportVO activityCommentExportVO = new ActivityCommentExportVO();
            activityCommentExportVO.setActivityTitle(activityDetail.getActivityTitle());
            activityCommentExportVO.setCommentName(userMap.getOrDefault(x.getCommentUserId(),new EnterpriseUserDO()).getName());
            activityCommentExportVO.setJobNumber(userMap.getOrDefault(x.getCommentUserId(),new EnterpriseUserDO()).getJobnumber());
            activityCommentExportVO.setCommentTime(x.getCreateTime());
            activityCommentExportVO.setContent(x.getContent());
            List<String> picOrVideo = new ArrayList<>();
            String contentPics = x.getContentPics();
            List<String> list = JSONObject.parseArray(contentPics, String.class);
            if (CollectionUtils.isNotEmpty(list)){
                picOrVideo.addAll(list);
            }
            String contentVideo = x.getContentVideo();
            List<SmallVideoDTO> smallVideoDTOS = JSONObject.parseArray(contentVideo, SmallVideoDTO.class);
            if (CollectionUtils.isNotEmpty(smallVideoDTOS)){
                List<String> videoList = smallVideoDTOS.stream().filter(smallVideoDTO -> StringUtils.isNotEmpty(smallVideoDTO.getVideoUrl())).map(SmallVideoDTO::getVideoUrl).collect(Collectors.toList());
                picOrVideo.addAll(videoList);
            }
            activityCommentExportVO.setPicOrVideo(CollectionUtils.isNotEmpty(picOrVideo)?picOrVideo.stream().collect(Collectors.joining(",")):"");
            activityCommentExportVO.setFloor(x.getFloorNum());
            ActivityUserVO activityUserVO = userVOMap.getOrDefault(x.getCommentUserId(), new ActivityUserVO());
            activityCommentExportVO.setDeptName(activityUserVO.getDeptName());
            activityCommentExportVO.setStoreNum(activityUserVO.getStoreNum());
            activityCommentExportVO.setThirdDept(activityUserVO.getThirdDept());
            activityCommentExportVO.setFullRegionPathName(activityUserVO.getFullRegionPathName());
            activityCommentExportVO.setLikeCount(x.getLikeCount());
            activityCommentExportVO.setReplyCount(commentReplyCountMap.getOrDefault(x.getId(),0));
            ActivityReplyDO activityReplyDO = ActivityReplyMap.getOrDefault(x.getId(), new ActivityReplyDO());
            activityCommentExportVO.setReplyContent(activityReplyDO.getContent());
            ActivityUserVO activityUser = userVOMap.getOrDefault(activityReplyDO.getReplyUserId(), new ActivityUserVO());
            activityCommentExportVO.setReplyDept(activityUser.getFullRegionPathName());
            activityCommentExportVO.setReplyTime(activityReplyDO.getCreateTime());
            activityCommentExportVO.setReplyJobNumber(userMap.getOrDefault(activityReplyDO.getReplyUserId(),new EnterpriseUserDO()).getJobnumber());
            activityCommentExportVO.setReplyUserName(userMap.getOrDefault(activityReplyDO.getReplyUserId(),new EnterpriseUserDO()).getName());
            result.add(activityCommentExportVO);
        });

        PageInfo activityCommentDOPageInfo = new PageInfo<>(activityCommentPage);
        activityCommentDOPageInfo.setList(result);
        return activityCommentDOPageInfo;
    }

    @Override
    public void updateActivityStatus(String enterpriseId, Long activityId) {
        log.info("异步更新活动状态开始enterpriseId：{}， activityId：{}", enterpriseId, activityId);
        ActivityInfoDO activityDetail = activityInfoDAO.getActivityDetail(enterpriseId, activityId);
        //为空 或者停止状态不做任何处理
        if(Objects.isNull(activityDetail) || ActivityStatusEnum.STOP.getCode().equals(activityDetail.getStatus())){
            return;
        }
        log.info("activity info:{}", JSONObject.toJSONString(activityDetail));
        ActivityStatusEnum status = null;
        Long startTime = activityDetail.getStartTime().getTime();
        Long endTime = activityDetail.getEndTime().getTime();
        Long currentTime = System.currentTimeMillis();
        Long currentStartTime = currentTime - RedisConstant.ONE_MINUTES;
        Long currentEndTime = currentTime + RedisConstant.ONE_MINUTES;
        if((startTime >= currentStartTime && startTime <= currentEndTime) || (currentTime >= startTime && currentTime <= endTime)){
            //开始时间在当前时间前后1分钟
            status = ActivityStatusEnum.ONGOING;
        }
        if(endTime >= currentStartTime && endTime <= currentEndTime || currentTime > endTime){
            //结束时间在当前时间前后1分钟
            status = ActivityStatusEnum.END;
        }
        if(Objects.isNull(status)){
            log.info("状态为空");
            return;
        }
        ActivityInfoDO update = new ActivityInfoDO();
        update.setId(activityId);
        update.setStatus(status.getCode());
        activityInfoDAO.updateActivity(enterpriseId, update);
        if(ActivityStatusEnum.ONGOING.getCode().equals(status.getCode())){
            //发送异步事件  更新活动结束状态
            simpleMessageService.send(JSONObject.toJSONString(new ActivityMqMessageDTO(enterpriseId, activityId)), RocketMqTagEnum.ACTIVITY_STATUS_UPDATE, activityDetail.getEndTime().getTime());
        }
    }

    @Override
    public void updateCommentCount(String enterpriseId, Long activityId) {
        ActivityCommentCountDTO activityCommentCount = activityCommentDAO.getActivityCommentCount(enterpriseId, activityId);
        if(Objects.isNull(activityCommentCount)){
            return;
        }
        ActivityInfoDO update = new ActivityInfoDO();
        update.setId(activityId);
        update.setCommentsCount(activityCommentCount.getCommentsCount());
        update.setCommentsUserCount(activityCommentCount.getCommentsUserCount());
        activityInfoDAO.updateActivity(enterpriseId, update);
    }

    @Override
    public void updateCommentReplyCount(String enterpriseId, Long activityId, Long commentId) {
        Map<Long, Integer> commentReplyCount = activityReplyDAO.getCommentReplyCount(enterpriseId, activityId, Arrays.asList(commentId));
        Integer replyCount = commentReplyCount.getOrDefault(commentId, Constants.ZERO);
        ActivityCommentDO update = new ActivityCommentDO();
        update.setId(commentId);
        update.setReplyCount(replyCount);
        activityCommentDAO.updateActivityComment(enterpriseId, update);
    }

    @Override
    public void updateLikeCount(String enterpriseId, Long activityId, Long commentId) {
        //获取活动的点赞
        Integer likeCount = activityLikeDAO.getLikeCount(enterpriseId, activityId, ActivityLikeTypeEnum.ACTIVITY);
        if(Objects.nonNull(likeCount)){
            ActivityInfoDO update = new ActivityInfoDO();
            update.setId(activityId);
            update.setLikeCount(likeCount);
            activityInfoDAO.updateActivity(enterpriseId, update);
        }
        if(Objects.nonNull(commentId)){
            Integer commentLikeCount = activityLikeDAO.getLikeCount(enterpriseId, commentId, ActivityLikeTypeEnum.COMMENT);
            if(Objects.isNull(commentLikeCount)){
                return;
            }
            ActivityCommentDO updateComment = new ActivityCommentDO();
            updateComment.setId(commentId);
            updateComment.setLikeCount(commentLikeCount);
            activityCommentDAO.updateActivityComment(enterpriseId, updateComment);
        }
    }

    @Override
    public Integer topAndUnTop(String enterpriseId, String userId, ActivityCommentIdDTO param) {
        return activityCommentDAO.topAndUnTop(enterpriseId, userId, param.getActivityId(), param.getCommentId());
    }


    /**
     * getActivityUser
     * @param eid
     * @param userDO
     * @return
     */
    private ActivityUserVO getActivityUser(String eid,EnterpriseUserDO userDO){
        ActivityUserVO activityUserVO = new ActivityUserVO();
        String userRegionIds = userDO.getUserRegionIds();
        List<String> list = new ArrayList<>();
        if (StringUtils.isNotEmpty(userRegionIds)) {
            list = Arrays.asList(userRegionIds.substring(1, userRegionIds.length() - 1).split(","));
            Set<Long> storeIdList = new HashSet<>();
            Set<Long> regionIdList = new HashSet<>();
            for (String str : list) {
                String[] numbers = str.split("/");
                if (numbers.length >= 2) {
                    if (StringUtils.isNotEmpty(numbers[numbers.length - 1])) {
                        storeIdList.add(Long.valueOf(numbers[numbers.length - 1]));
                    }
                    if (StringUtils.isNotEmpty(numbers[numbers.length - 2])) {
                        regionIdList.add(Long.valueOf(numbers[numbers.length - 2]));
                    }
                }
            }

            List<RegionDO> allStore = regionDao.getAllRegionByRegionIds(eid, new ArrayList<>(storeIdList));
            String allStoreStr = allStore.stream().map(RegionDO::getName).collect(Collectors.joining(Constants.COMMA));

            List<String> storeIds = allStore.stream().filter(store -> StringUtils.isNotEmpty(store.getStoreId())).map(RegionDO::getStoreId).collect(Collectors.toList());

            List<StoreDO> storeList = storeDao.getByStoreIdList(eid, storeIds);
            String storeNumStr = storeList.stream().filter(x->StringUtils.isNotEmpty(x.getStoreNum())).map(StoreDO::getStoreNum).collect(Collectors.joining(Constants.COMMA));
            activityUserVO.setStoreNum(storeNumStr);
            activityUserVO.setDeptName(allStoreStr);
            activityUserVO.setUserId(userDO.getUserId());
            //查询全路径名称
            getFullRegionNameList(eid,list,activityUserVO);
        }
        return activityUserVO;
    }


    public  List<String> getFullRegionNameList(String eid, List<String> regionIdList,ActivityUserVO activityUserVO) {
        Set<Long> regionIds = new HashSet<>();
        for (int i = 0; i < regionIdList.size(); i++) {
            String s = regionIdList.get(i);
            String[] split = s.substring(1,s.length()-1).split("/");
            List<Long> longList = Arrays.stream(split)
                    .map(Long::parseLong)
                    .collect(Collectors.toList());
            regionIds.addAll(longList);
        }
        List<RegionDO> allRegionByRegionIds = regionDao.getAllRegionByRegionIds(eid, new ArrayList<>(regionIds));
        Map<Long, String> regionDOMap = allRegionByRegionIds.stream().collect(Collectors.toMap(RegionDO::getId, RegionDO::getName));


        List<String> result = new ArrayList<>();
        List<String> nameList = new ArrayList<>();

        for (String str : regionIdList) {
            String[] ids = str.split("/");
            StringBuilder sb = new StringBuilder();
            int count = 0;
            for (String id : ids) {
                if (!id.isEmpty()) {
                    Long regionId = Long.valueOf(id);
                    String name = regionDOMap.getOrDefault(regionId, "Unknown"); // 如果ID没有对应的name，则使用"Unknown"
                    sb.append("/").append(name);
                    count++;
                    if (count == 4) {
                        nameList.add(name);
                    }
                }
            }
            sb.append("/");
            result.add(sb.toString());
        }
        if (CollectionUtils.isNotEmpty(nameList)){
            activityUserVO.setThirdDept(nameList.stream().distinct().collect(Collectors.joining(Constants.COMMA)));
        }
        if (CollectionUtils.isNotEmpty(result)){
            activityUserVO.setFullRegionPathName(result.stream().collect(Collectors.joining(Constants.COMMA)));
        }
        return result;
    }



    @Override
    public  List<String> getFullRegionNameList(String eid, List<String> regionIdList) {
        List<Long> regionIds = new ArrayList<>();
        for (int i = 0; i < regionIdList.size(); i++) {
            String s = regionIdList.get(i);
            String[] split = s.substring(1,s.length()-1).split("/");
            List<Long> longList = Arrays.stream(split)
                    .map(Long::parseLong)
                    .collect(Collectors.toList());
            regionIds.addAll(longList);
        }
        List<RegionDO> allRegionByRegionIds = regionDao.getAllRegionByRegionIds(eid, regionIds);
        Map<Long, String> regionDOMap = allRegionByRegionIds.stream().collect(Collectors.toMap(RegionDO::getId, RegionDO::getName));


        List<String> result = new ArrayList<>();
        List<String> nameList = new ArrayList<>();

        for (String str : regionIdList) {
            String[] ids = str.split("/");
            StringBuilder sb = new StringBuilder();
            int count = 0;
            for (String id : ids) {
                if (!id.isEmpty()) {
                    Long regionId = Long.valueOf(id);
                    String name = regionDOMap.getOrDefault(regionId, "Unknown"); // 如果ID没有对应的name，则使用"Unknown"
                    sb.append("/").append(name);
                    count++;
                    if (count == 4) {
                        nameList.add(name);
                    }
                }
            }
            sb.append("/");
            result.add(sb.toString());
        }
        System.out.println(JSONObject.toJSONString(nameList));
        return result;
    }






    @Override
    public void updateCommentCount(ActivityMqMessageDTO param) {
        String enterpriseId = param.getEnterpriseId();
        Long activityId = param.getActivityId();
        ActivityCommentCountDTO activityCommentCount = activityCommentDAO.getActivityCommentCount(enterpriseId, activityId);
        if(Objects.isNull(activityCommentCount)){
            return;
        }
        ActivityInfoDO update = new ActivityInfoDO();
        update.setId(activityId);
        update.setCommentsCount(activityCommentCount.getCommentsCount());
        update.setCommentsUserCount(activityCommentCount.getCommentsUserCount());
        activityInfoDAO.updateActivity(enterpriseId, update);
    }

    @Override
    public void updateReplyCount(ActivityMqMessageDTO param) {
        String enterpriseId = param.getEnterpriseId();
        Long activityId = param.getActivityId();
        Long commentId = param.getCommentId();
        Map<Long, Integer> commentReplyCount = activityReplyDAO.getCommentReplyCount(enterpriseId, activityId, Arrays.asList(commentId));
        Integer replyCount = commentReplyCount.getOrDefault(commentId, Constants.ZERO);
        ActivityCommentDO update = new ActivityCommentDO();
        update.setId(commentId);
        update.setReplyCount(replyCount);
        activityCommentDAO.updateActivityComment(enterpriseId, update);
    }

    @Override
    public void updateLikeCount(ActivityMqMessageDTO param) {
        String enterpriseId = param.getEnterpriseId();
        Long activityId = param.getActivityId();
        Long commentId = param.getCommentId();
        //获取活动的点赞
        Integer likeCount = activityLikeDAO.getLikeCount(enterpriseId, activityId, ActivityLikeTypeEnum.ACTIVITY);
        if(Objects.nonNull(likeCount)){
            ActivityInfoDO update = new ActivityInfoDO();
            update.setId(activityId);
            update.setLikeCount(likeCount);
            activityInfoDAO.updateActivity(enterpriseId, update);
        }
        if(Objects.nonNull(commentId)){
            Integer commentLikeCount = activityLikeDAO.getLikeCount(enterpriseId, commentId, ActivityLikeTypeEnum.COMMENT);
            if(Objects.isNull(commentLikeCount)){
                return;
            }
            ActivityCommentDO updateComment = new ActivityCommentDO();
            updateComment.setId(commentId);
            updateComment.setLikeCount(commentLikeCount);
            activityCommentDAO.updateActivityComment(enterpriseId, updateComment);
        }
    }
}
