package com.coolcollege.intelligent.service.video.vod;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.oss.ClientException;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.aliyuncs.sts.model.v20150401.AssumeRoleRequest;
import com.aliyuncs.sts.model.v20150401.AssumeRoleResponse;
import com.aliyuncs.vod.model.v20170321.CreateUploadVideoRequest;
import com.aliyuncs.vod.model.v20170321.CreateUploadVideoResponse;
import com.aliyuncs.vod.model.v20170321.RefreshUploadVideoRequest;
import com.aliyuncs.vod.model.v20170321.RefreshUploadVideoResponse;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.constant.RedisConstant;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.enums.video.ResourceStatusEnum;
import com.coolcollege.intelligent.common.enums.video.UploadTypeEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.dao.device.DeviceCaptureLibMapper;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseConfigMapper;
import com.coolcollege.intelligent.dao.patrolstore.TbDataDefTableColumnMapper;
import com.coolcollege.intelligent.dao.patrolstore.TbDataStaTableColumnMapper;
import com.coolcollege.intelligent.dao.patrolstore.TbPatrolStoreRecordMapper;
import com.coolcollege.intelligent.dao.question.dao.QuestionHistoryDao;
import com.coolcollege.intelligent.dao.question.dao.QuestionParentInfoDao;
import com.coolcollege.intelligent.dao.question.dao.QuestionRecordDao;
import com.coolcollege.intelligent.dao.question.dao.QuestionRecordExpandDao;
import com.coolcollege.intelligent.dao.qyy.QyyWeeklyNewspaperMapper;
import com.coolcollege.intelligent.dao.safetycheck.TbDataColumnAppealMapper;
import com.coolcollege.intelligent.dao.safetycheck.dao.TbDataColumnHistoryDao;
import com.coolcollege.intelligent.dao.sop.TaskSopMapper;
import com.coolcollege.intelligent.dao.storework.dao.SwStoreWorkDataTableColumnDao;
import com.coolcollege.intelligent.dao.supervision.SupervisionDefDataColumnMapper;
import com.coolcollege.intelligent.dao.tbdisplay.TbDisplayTableDataColumnMapper;
import com.coolcollege.intelligent.dao.tbdisplay.TbDisplayTableDataContentMapper;
import com.coolcollege.intelligent.dao.unifytask.TaskParentMapper;
import com.coolcollege.intelligent.dao.unifytask.dao.UnifyTaskParentItemDao;
import com.coolcollege.intelligent.mapper.activity.ActivityCommentDAO;
import com.coolcollege.intelligent.model.activity.entity.ActivityCommentDO;
import com.coolcollege.intelligent.model.device.DeviceCaptureLibDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.enums.TaskTypeEnum;
import com.coolcollege.intelligent.model.patrolstore.TbDataDefTableColumnDO;
import com.coolcollege.intelligent.model.patrolstore.TbDataStaTableColumnDO;
import com.coolcollege.intelligent.model.patrolstore.TbPatrolStoreRecordDO;
import com.coolcollege.intelligent.model.patrolstore.entity.TbPatrolStoreCloudDO;
import com.coolcollege.intelligent.model.question.TbQuestionHistoryDO;
import com.coolcollege.intelligent.model.question.TbQuestionRecordDO;
import com.coolcollege.intelligent.model.question.TbQuestionRecordExpandDO;
import com.coolcollege.intelligent.model.question.dto.QuestionTaskInfoDTO;
import com.coolcollege.intelligent.model.qyy.QyyWeeklyNewspaperDO;
import com.coolcollege.intelligent.model.safetycheck.TbDataColumnAppealDO;
import com.coolcollege.intelligent.model.safetycheck.TbDataColumnHistoryDO;
import com.coolcollege.intelligent.model.sop.TaskSopDO;
import com.coolcollege.intelligent.model.sop.vo.TaskSopVO;
import com.coolcollege.intelligent.model.storework.SwStoreWorkDataTableColumnDO;
import com.coolcollege.intelligent.model.supervision.SupervisionDefDataColumnDO;
import com.coolcollege.intelligent.model.tbdisplay.TbDisplayTableDataColumnDO;
import com.coolcollege.intelligent.model.tbdisplay.TbDisplayTableDataContentDO;
import com.coolcollege.intelligent.model.unifytask.TaskParentDO;
import com.coolcollege.intelligent.model.unifytask.UnifyTaskParentItemDO;
import com.coolcollege.intelligent.model.video.SmallVideoDTO;
import com.coolcollege.intelligent.model.video.SmallVideoInfoDTO;
import com.coolcollege.intelligent.model.video.TicketAssumeRoleResponse;
import com.coolcollege.intelligent.model.video.param.SmallVideoParam;
import com.coolcollege.intelligent.service.patrolstore.PatrolStoreCloudService;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.coolcollege.intelligent.util.vod.CallbackRequest;
import com.coolcollege.intelligent.util.vod.CallbackStreamInfo;
import com.coolcollege.intelligent.util.vod.EventType;
import com.coolcollege.intelligent.util.vod.UserData;
import com.google.common.io.Files;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * Created by Joshua on 2017/9/27 17:47
 */
@Service
@Lazy
@Slf4j
public class VodService {

    @Value("${vod.region}")
    private String region;

    @Value("${vod.role.arn}")
    private String roleArn;

    @Value("${vod.access.key.id}")
    private String accessKeyId;

    @Value("${vod.access.key.secret}")
    private String accessKeySecret;

    @Value("${vod.noTranscode.groupId}")
    private String noTranscodeGroupId;

    @Value("${vod.cateId}")
    private Long cateId;

    /**
     * mp4工作流ID
     */
    @Value("${vod.workflowId}")
    private String vodWorkflowId;

    /**
     * vod回调地址
     */
    @Value("${vod.callBackUrl}")
    private String callBackUrl;

    /**
     * vod回调地址
     */
    @Value("${vod.xfsg.callBackUrl}")
    private String xsfgCallBackUrl;


    @Value("${api.domain.url}")
    private String apiDomainUrl;

    private static final String storageLocation = "coolstore-vod-1.oss-cn-shanghai.aliyuncs.com";



    @Autowired
    private TbDataStaTableColumnMapper tbDataStaTableColumnMapper;

    @Resource
    private TbDataDefTableColumnMapper tbDataDefTableColumnMapper;

    @Autowired
    private TbPatrolStoreRecordMapper tbPatrolStoreRecordMapper;
    @Resource
    SwStoreWorkDataTableColumnDao swStoreWorkDataTableColumnDao;

    @Autowired
    private EnterpriseConfigMapper enterpriseConfigMapper;

    @Autowired
    private RedisUtilPool redisUtil;

    @Autowired
    private TaskParentMapper taskParentMapper;

    @Resource
    TaskSopMapper taskSopMapper;

    @Resource
    private QuestionHistoryDao questionHistoryDao;

    @Resource
    private QuestionRecordExpandDao questionRecordExpandDao;

    @Resource
    private QuestionRecordDao questionRecordDao;

    @Resource
    private UnifyTaskParentItemDao unifyTaskParentItemDao;
    
    @Resource
    DeviceCaptureLibMapper deviceCaptureLibMapper;

    @Resource
    ActivityCommentDAO activityCommentDAO;

    @Resource
    QyyWeeklyNewspaperMapper qyyWeeklyNewspaperMapper;

    @Resource
    private SupervisionDefDataColumnMapper supervisionDefDataColumnMapper;

    @Resource
    TbDataColumnHistoryDao tbDataColumnHistoryDao;

    @Resource
    private TbDisplayTableDataColumnMapper tbDisplayTableDataColumnMapper;
    @Resource
    private TbDisplayTableDataContentMapper tbDisplayTableDataContentMapper;
    @Resource
    private PatrolStoreCloudService patrolStoreCloudService;
    @Resource
    private TbDataColumnAppealMapper tbDataColumnAppealMapper;


    public static final Integer expireTime = 3600 * 72;

    public static String transSmallVideoDtoTOString(String videoInfo, SmallVideoDTO smallVideoDto) {

        log.info("transSmallVideoDtoTOString,videoInfo:{},smallVideoDto:{}", videoInfo, smallVideoDto.toString());
        SmallVideoInfoDTO smallVideoInfoDTOS = JSONObject.parseObject(videoInfo, SmallVideoInfoDTO.class);
        if (smallVideoInfoDTOS == null) {
            smallVideoInfoDTOS = new SmallVideoInfoDTO();
        }
        if (CollectionUtils.isEmpty(smallVideoInfoDTOS.getVideoList())) {
            smallVideoInfoDTOS.setVideoList(Collections.singletonList(smallVideoDto));
            return JSONObject.toJSONString(smallVideoInfoDTOS);
        }
        for (SmallVideoDTO smallVideo : smallVideoInfoDTOS.getVideoList()) {
            if (smallVideo.getVideoId() == null || StringUtils.equals(smallVideo.getVideoId(), smallVideoDto.getVideoId())) {
                BeanUtils.copyProperties(smallVideoDto, smallVideo);
            }
        }
        return JSONObject.toJSONString(smallVideoInfoDTOS);
    }

    public static String transHistorySmallVideoDtoTOString(String videoInfo, SmallVideoDTO smallVideoDto) {
        log.info("transHistorySmallVideoDtoTOString,videoInfo:{},smallVideoDto:{}", videoInfo, smallVideoDto.toString());
        SmallVideoInfoDTO smallVideoInfoDTOS = JSONObject.parseObject(videoInfo, SmallVideoInfoDTO.class);
        if (smallVideoInfoDTOS == null) {
            return videoInfo;
        }
        for (SmallVideoDTO smallVideo : smallVideoInfoDTOS.getVideoList()) {
            if (smallVideo.getVideoId() == null || StringUtils.equals(smallVideo.getVideoId(), smallVideoDto.getVideoId())) {
                BeanUtils.copyProperties(smallVideoDto, smallVideo);
            }
        }
        return JSONObject.toJSONString(smallVideoInfoDTOS);
    }

    public static SmallVideoDTO getSmallVideoDto(String videoInfo, String videoId) {

        log.info("SmallVideoInfoDTO,videoInfo:{},videoId:{}", videoInfo, videoId);
        if (StringUtils.isBlank(videoInfo) || StringUtils.isBlank(videoId)) {
            return new SmallVideoDTO();
        }
        SmallVideoInfoDTO smallVideoInfo = JSONObject.parseObject(videoInfo, SmallVideoInfoDTO.class);
        if (CollectionUtils.isEmpty(smallVideoInfo.getVideoList())) {
            return new SmallVideoDTO();
        }
        for (SmallVideoDTO smallVideo : smallVideoInfo.getVideoList()) {
            if (StringUtils.equals(smallVideo.getVideoId(), videoId)) {
                return smallVideo;
            }
        }
        return new SmallVideoDTO();
    }

    public void callback(CallbackRequest request) {
        log.info("CallbackRequest:{}", JSONObject.toJSONString(request));
        //未完成转码的视频
        String videoId = request.getVideoId();

        String eventType = request.getEventType();
        String newVideoInfo = "";
        String callbackCache = redisUtil.getString(RedisConstant.VIDEO_CALLBACK_CACHE + videoId);
        SmallVideoDTO smallVideoDTO;
        if (StringUtils.isNotBlank(callbackCache)) {
            smallVideoDTO = JSONObject.parseObject(callbackCache, SmallVideoDTO.class);
        } else {
            smallVideoDTO = new SmallVideoDTO();
        }
        log.info("smallVideoDTO:{}",JSONObject.toJSONString(smallVideoDTO));
        smallVideoDTO.setSize(request.getSize());
        smallVideoDTO.setVideoId(videoId);
        switch (eventType) {
            //视频上传完成
            case EventType.FileUploadComplete:
                smallVideoDTO.setStatus(ResourceStatusEnum.TRANSCODING.getValue());
                smallVideoDTO.setVideoUrlBefore(request.getFileUrl());
                log.info("视频原始地址：{}", request.getFileUrl());
                //mp4不转码处理
                if (Files.getFileExtension(request.getFileUrl()).equalsIgnoreCase(Constants.TRANSCODE_VIDEO)) {
                    if (Constants.SUCCESS_STR.equalsIgnoreCase(request.getStatus())) {
                        smallVideoDTO.setVideoUrl(request.getFileUrl());
                    } else {
                        smallVideoDTO.setStatus(ResourceStatusEnum.TRANSCODE_FAILED.getValue());
                    }
                }
                smallVideoDTO.setUploadStatus(true);
                newVideoInfo = JSONObject.toJSONString(smallVideoDTO);
                //过期时间为一周
                redisUtil.setString(RedisConstant.VIDEO_CALLBACK_CACHE + videoId, newVideoInfo, expireTime);
                break;
            //视频截图完成
            case EventType.SnapshotComplete:
                // 视频截图完成，且课件状态在转码完成之前时，更新状态为截图完成
                if (smallVideoDTO.getStatus() == null || smallVideoDTO.getStatus() < ResourceStatusEnum.TRANSCODE_FINISH.getValue()) {
                    smallVideoDTO.setStatus(ResourceStatusEnum.SCREENSHOT_FINISHED.getValue());
                }
                //视频封面
                smallVideoDTO.setVideoSnapshot(request.getCoverUrl());

                //如果上传的就是mp4，就不进行转码，截图完成就把状态改为转码完成
                if(StringUtils.isNotBlank(smallVideoDTO.getVideoUrlBefore())
                        && Files.getFileExtension(smallVideoDTO.getVideoUrlBefore()).equalsIgnoreCase(Constants.TRANSCODE_VIDEO)
                        && !smallVideoDTO.getStatus().equals(ResourceStatusEnum.TRANSCODE_FAILED.getValue())){

                    smallVideoDTO.setStatus(ResourceStatusEnum.TRANSCODE_FINISH.getValue());
                    smallVideoDTO.setTransCodeStatus(true);
                }
                smallVideoDTO.setSnapShotStatus(true);
                newVideoInfo = JSONObject.toJSONString(smallVideoDTO);

                //过期时间为一周
                redisUtil.setString(RedisConstant.VIDEO_CALLBACK_CACHE + videoId, newVideoInfo, expireTime);
                break;
            //视频全部清晰度转码完成
            case EventType.TranscodeComplete:
                if (Constants.SUCCESS_STR.equalsIgnoreCase(request.getStatus())) {
                    smallVideoDTO.setTransCodeStatus(true);
                    smallVideoDTO.setStatus(ResourceStatusEnum.TRANSCODE_FINISH.getValue());
                    List<CallbackStreamInfo> streamInfoList = request.getStreamInfos();
                    for (CallbackStreamInfo streamInfo : streamInfoList) {
                        log.info("VodService callback playInfo.getFormat:{}", streamInfo.getFormat());
                        if (Objects.nonNull(smallVideoDTO.getStatus()) && smallVideoDTO.getStatus().intValue() != ResourceStatusEnum.TRANSCODE_FAILED.getValue()) {
                            smallVideoDTO.setVideoUrl(streamInfo.getFileUrl());
                        }
                    }
                } else {
                    smallVideoDTO.setStatus(ResourceStatusEnum.TRANSCODE_FAILED.getValue());
                }

                log.info("视频转码完成,videoId:{}", videoId);
                newVideoInfo = JSONObject.toJSONString(smallVideoDTO);
                //过期时间为一周
                redisUtil.setString(RedisConstant.VIDEO_CALLBACK_CACHE + videoId, newVideoInfo, expireTime);
                break;
            default:
        }
        log.info("videoId:{}，转码状态:{}", videoId, smallVideoDTO.getStatus());
        //未完成转码处理
        notCompleteCacheHandle(smallVideoDTO, videoId);
    }

    /**
     * 如果前端提交的时候，视频还没有转码成功，会把videoId存入缓存，回调的时候再进行处理
     * @author chenyupeng
     * @date 2021/10/14
     * @param smallVideoDTO
     * @param videoId
     * @return void
     */
    public void notCompleteCacheHandle(SmallVideoDTO smallVideoDTO, String videoId) {
        log.info("videoId:{}, smallVideoDTO:{}", videoId, JSONObject.toJSONString(smallVideoDTO));
        //回调事件都完成才入库
        if(!(smallVideoDTO.isUploadStatus() && smallVideoDTO.isSnapShotStatus() && smallVideoDTO.isTransCodeStatus())){
            log.info("回调事件都完成才入库videoId:{}", videoId);
            return;
        }
        String notCompleteCache = redisUtil.hashGet(RedisConstant.VIDEO_NOT_COMPLETE_CACHE, videoId);
        log.info("notCompleteCache:{}", notCompleteCache);
        if (StringUtils.isNotBlank(notCompleteCache)) {
            if(StringUtils.isNotBlank(smallVideoDTO.getVideoUrl())){
                smallVideoDTO.setVideoUrl(smallVideoDTO.getVideoUrl().replace("http://", "https://"));
            }

            if(StringUtils.isNotBlank(smallVideoDTO.getVideoUrlBefore())){
                smallVideoDTO.setVideoUrlBefore(smallVideoDTO.getVideoUrlBefore().replace("http://", "https://"));
            }
            SmallVideoParam smallVideoParam = JSONObject.parseObject(notCompleteCache, SmallVideoParam.class);
            if (UploadTypeEnum.TB_DATA_STA_TABLE_COLUMN.getValue().equals(smallVideoParam.getUploadType())) {
                //检查项提交
                tbDataStaTableColumnDeal(smallVideoDTO, smallVideoParam);
                //保存完删除缓存
                redisUtil.hashDel(RedisConstant.VIDEO_NOT_COMPLETE_CACHE, videoId);
            } else if (UploadTypeEnum.TB_PATROL_STORE_RECORD.getValue().equals(smallVideoParam.getUploadType())) {
                //巡店记录总结
                tbPatrolStoreRecordDeal(smallVideoDTO, smallVideoParam);
                //保存完删除缓存
                redisUtil.hashDel(RedisConstant.VIDEO_NOT_COMPLETE_CACHE, videoId);
            } else if (UploadTypeEnum.QUESTION_CREATE.getValue().equals(smallVideoParam.getUploadType())) {
                //工单任务视频
                taskQuestionDeal(smallVideoDTO, smallVideoParam);
                //保存完删除缓存
                redisUtil.hashDel(RedisConstant.VIDEO_NOT_COMPLETE_CACHE, videoId);
            } else if (UploadTypeEnum.QUESTION_PARENT_CREATE.getValue().equals(smallVideoParam.getUploadType())) {
                //工单任务视频
                taskParentQuestionCreateDeal(smallVideoDTO, smallVideoParam);
                //保存完删除缓存
                redisUtil.hashDel(RedisConstant.VIDEO_NOT_COMPLETE_CACHE, videoId);
            }else if (UploadTypeEnum.QUESTION_SUMMIT.getValue().equals(smallVideoParam.getUploadType())) {
                //工单任务视频
                taskQuestionSummitVideoDeal(smallVideoDTO, smallVideoParam);
                //保存完删除缓存
                redisUtil.hashDel(RedisConstant.VIDEO_NOT_COMPLETE_CACHE, videoId);
            }else if (UploadTypeEnum.STORE_WORK_SUBMIT.getValue().equals(smallVideoParam.getUploadType())) {
                //店务
                swStoreWorkTableColumnDeal(smallVideoDTO, smallVideoParam);
                //保存完删除缓存
                redisUtil.hashDel(RedisConstant.VIDEO_NOT_COMPLETE_CACHE, videoId);
            }else if (UploadTypeEnum.TASK_SOP_ADD.getValue().equals(smallVideoParam.getUploadType())) {
                //工单任务视频
                taskSopDeal(smallVideoDTO, smallVideoParam);
                //保存完删除缓存
                redisUtil.hashDel(RedisConstant.VIDEO_NOT_COMPLETE_CACHE, videoId);
            }else if (UploadTypeEnum.DEVICE_CAPTURE.getValue().equals(smallVideoParam.getUploadType())) {
                //设备抓拍库视频处理
                deviceCaptureDeal(smallVideoDTO, smallVideoParam);
                //保存完删除缓存
                redisUtil.hashDel(RedisConstant.VIDEO_NOT_COMPLETE_CACHE, videoId);
            }else if (UploadTypeEnum.TB_DATA_DEF_TABLE_COLUMN.getValue().equals(smallVideoParam.getUploadType())) {
                //设备抓拍库视频处理
                defColumnVideoDeal(smallVideoDTO, smallVideoParam);
                //保存完删除缓存
                redisUtil.hashDel(RedisConstant.VIDEO_NOT_COMPLETE_CACHE, videoId);
            }else if (UploadTypeEnum.SUPERVISION_DATA_DEF_TABLE_COLUMN.getValue().equals(smallVideoParam.getUploadType())) {
                //设备抓拍库视频处理
                supervisionDefTableVideoDeal(smallVideoDTO, smallVideoParam);
                //保存完删除缓存
                redisUtil.hashDel(RedisConstant.VIDEO_NOT_COMPLETE_CACHE, videoId);
            }else if (UploadTypeEnum.ACTIVITY_COMMENT.getValue().equals(smallVideoParam.getUploadType())) {
                //设备抓拍库视频处理
                activityCommentDeal(smallVideoDTO, smallVideoParam);
                //保存完删除缓存
                redisUtil.hashDel(RedisConstant.VIDEO_NOT_COMPLETE_CACHE, videoId);
            }else if (UploadTypeEnum.WEEKLY_NEWSPAPER_LIST.getValue().equals(smallVideoParam.getUploadType())) {
                newsPaperListDeal(smallVideoDTO, smallVideoParam);
                //保存完删除缓存
                redisUtil.hashDel(RedisConstant.VIDEO_NOT_COMPLETE_CACHE, videoId);
            }else if (UploadTypeEnum.TB_DISPLAY_TABLE_DATA_COLUMN.getValue().equals(smallVideoParam.getUploadType())) {
                //陈列检查项提交
                tbDisplayTableDataColumnDeal(smallVideoDTO, smallVideoParam);
                //保存完删除缓存
                redisUtil.hashDel(RedisConstant.VIDEO_NOT_COMPLETE_CACHE, videoId);
            }else if (UploadTypeEnum.TB_DISPLAY_TABLE_DATA_CONTENT.getValue().equals(smallVideoParam.getUploadType())) {
                //陈列检查内容提交
                tbDisplayTableDataContentDeal(smallVideoDTO, smallVideoParam);
                //保存完删除缓存
                redisUtil.hashDel(RedisConstant.VIDEO_NOT_COMPLETE_CACHE, videoId);
            } else if (UploadTypeEnum.STORE_CLOUD.getValue().equals(smallVideoParam.getUploadType())) {
                // 云图库上传
                storeCloudDeal(smallVideoDTO, smallVideoParam);
                //保存完删除缓存
                redisUtil.hashDel(RedisConstant.VIDEO_NOT_COMPLETE_CACHE, videoId);
            } else if (UploadTypeEnum.DATA_COLUMN_APPEAL.getValue().equals(smallVideoParam.getUploadType())) {
                dataColumnAppealDeal(smallVideoDTO, smallVideoParam);
                //保存完删除缓存
                redisUtil.hashDel(RedisConstant.VIDEO_NOT_COMPLETE_CACHE, videoId);
            }
        }
    }

    /**
     * 云图库上传处理
     * @param smallVideoDTO   视频信息
     * @param smallVideoParam 视频信息业务参数
     */
    private void storeCloudDeal(SmallVideoDTO smallVideoDTO, SmallVideoParam smallVideoParam) {
        DataSourceHelper.reset();
        String enterpriseId = smallVideoParam.getEnterpriseId();
        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigMapper.selectByEnterpriseId(enterpriseId);
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());

        TbPatrolStoreCloudDO patrolStoreCloudDO = patrolStoreCloudService.selectById(enterpriseId, smallVideoParam.getBusinessId());
        log.info("patrolStoreCloudDO:{}", JSONObject.toJSONString(patrolStoreCloudDO));
        smallVideoDTO.setStatus(ResourceStatusEnum.TRANSCODE_FINISH.getValue());
        String checkVideo = transSmallVideoDtoTOString(patrolStoreCloudDO.getVideo(), smallVideoDTO);
        patrolStoreCloudService.updateVideo(enterpriseId, patrolStoreCloudDO.getId(), checkVideo);
    }

    /**
     * 食安稽核申诉视频处理
     * @param smallVideoDTO   视频信息
     * @param smallVideoParam 视频信息业务参数
     */
    private void dataColumnAppealDeal(SmallVideoDTO smallVideoDTO, SmallVideoParam smallVideoParam) {
        DataSourceHelper.reset();
        String enterpriseId = smallVideoParam.getEnterpriseId();
        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigMapper.selectByEnterpriseId(enterpriseId);
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());

        TbDataColumnAppealDO tbDataColumnAppealDO = tbDataColumnAppealMapper.selectByPrimaryKey(smallVideoParam.getBusinessId(), enterpriseId);
        log.info("tbDataColumnAppealDO:{}", JSONObject.toJSONString(tbDataColumnAppealDO));
        smallVideoDTO.setStatus(ResourceStatusEnum.TRANSCODE_FINISH.getValue());
        String checkVideo = transSmallVideoDtoTOString(tbDataColumnAppealDO.getVideos(), smallVideoDTO);
        TbDataColumnAppealDO updateDO = new TbDataColumnAppealDO();
        updateDO.setId(tbDataColumnAppealDO.getId());
        updateDO.setVideos(checkVideo);
        tbDataColumnAppealMapper.updateByPrimaryKeySelective(updateDO, enterpriseId);
    }


    /**
     *
     * @param smallVideoDTO
     * @param smallVideoParam
     */
    public void activityCommentDeal(SmallVideoDTO smallVideoDTO, SmallVideoParam smallVideoParam) {
        DataSourceHelper.reset();
        String enterpriseId = smallVideoParam.getEnterpriseId();
        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigMapper.selectByEnterpriseId(enterpriseId);
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());
        ActivityCommentDO activityCommentDO = activityCommentDAO.selectByPrimaryKeySelective(enterpriseId,smallVideoParam.getBusinessId());
        smallVideoDTO.setStatus(ResourceStatusEnum.TRANSCODE_FINISH.getValue());
        String checkVideo = transSmallVideoDto(activityCommentDO.getContentVideo(), smallVideoDTO);
        activityCommentDO.setContentVideo(checkVideo);
        activityCommentDAO.updateActivityComment(enterpriseId, activityCommentDO);
    }

    public void newsPaperListDeal(SmallVideoDTO smallVideoDTO, SmallVideoParam smallVideoParam) {
        DataSourceHelper.reset();
        String enterpriseId = smallVideoParam.getEnterpriseId();
        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigMapper.selectByEnterpriseId(enterpriseId);
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());
        QyyWeeklyNewspaperDO qyyWeeklyNewspaperDO = qyyWeeklyNewspaperMapper.selectById(enterpriseId,smallVideoParam.getBusinessId());
        smallVideoDTO.setStatus(ResourceStatusEnum.TRANSCODE_FINISH.getValue());
        String checkVideo = transSmallVideoDto(qyyWeeklyNewspaperDO.getVideoUrl(), smallVideoDTO);
        qyyWeeklyNewspaperDO.setVideoUrl(checkVideo);
        qyyWeeklyNewspaperMapper.updateByPrimaryKeySelective(qyyWeeklyNewspaperDO,enterpriseId);
    }

    public void tbDisplayTableDataColumnDeal(SmallVideoDTO smallVideoDTO, SmallVideoParam smallVideoParam) {
        DataSourceHelper.reset();
        String enterpriseId = smallVideoParam.getEnterpriseId();
        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigMapper.selectByEnterpriseId(enterpriseId);
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());
        TbDisplayTableDataColumnDO tbDisplayTableDataColumnDO = tbDisplayTableDataColumnMapper.selectById(enterpriseId, smallVideoParam.getBusinessId());
        tbDisplayTableDataColumnDO.setCheckVideo(transSmallVideoDtoTOString(tbDisplayTableDataColumnDO.getCheckVideo(), smallVideoDTO));
        tbDisplayTableDataColumnMapper.batchUpdate(enterpriseId, Collections.singletonList(tbDisplayTableDataColumnDO));
    }

    public void tbDisplayTableDataContentDeal(SmallVideoDTO smallVideoDTO, SmallVideoParam smallVideoParam) {
        DataSourceHelper.reset();
        String enterpriseId = smallVideoParam.getEnterpriseId();
        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigMapper.selectByEnterpriseId(enterpriseId);
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());
        TbDisplayTableDataContentDO tbDisplayTableDataContentDO = tbDisplayTableDataContentMapper.selectById(enterpriseId, smallVideoParam.getBusinessId());
        tbDisplayTableDataContentDO.setCheckVideo(transSmallVideoDtoTOString(tbDisplayTableDataContentDO.getCheckVideo(), smallVideoDTO));
        tbDisplayTableDataContentMapper.updateByPrimaryKeySelective(enterpriseId, tbDisplayTableDataContentDO);
    }

    public static String transSmallVideoDto(String videoInfo, SmallVideoDTO smallVideoDto) {
        log.info("activityCommentDeal,videoInfo:{},smallVideoDto:{}", videoInfo, smallVideoDto.toString());
        List<SmallVideoDTO> smallVideoDTOS = JSONObject.parseArray(videoInfo, SmallVideoDTO.class);
        for (SmallVideoDTO smallVideo : smallVideoDTOS) {
            if (smallVideo.getVideoId() == null || StringUtils.equals(smallVideo.getVideoId(), smallVideoDto.getVideoId())) {
                BeanUtils.copyProperties(smallVideoDto, smallVideo);
            }
        }
        return JSONObject.toJSONString(smallVideoDTOS);
    }


    /**
     * 设备抓拍库视频处理
     * @param smallVideoDTO
     * @param smallVideoParam
     */
    public void deviceCaptureDeal(SmallVideoDTO smallVideoDTO, SmallVideoParam smallVideoParam) {
        DataSourceHelper.reset();
        String enterpriseId = smallVideoParam.getEnterpriseId();
        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigMapper.selectByEnterpriseId(enterpriseId);
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());

        DeviceCaptureLibDO deviceCaptureLibDO = deviceCaptureLibMapper.selectByPrimaryKey(smallVideoParam.getBusinessId(), enterpriseId);

        if(Objects.isNull(deviceCaptureLibDO)){
            return;
        }

        smallVideoDTO.setStatus(ResourceStatusEnum.TRANSCODE_FINISH.getValue());

        deviceCaptureLibDO.setFileUrl(JSONObject.toJSONString(smallVideoDTO));

        DeviceCaptureLibDO deviceCaptureLib = DeviceCaptureLibDO.builder().fileUrl(deviceCaptureLibDO.getFileUrl()).id(deviceCaptureLibDO.getId()).build();

        deviceCaptureLibMapper.updateByPrimaryKeySelective(deviceCaptureLib,enterpriseId);
    }

    /**
     * 督导自定义检查项视频转码
     * @param smallVideoDTO
     * @param smallVideoParam
     */
    public void supervisionDefTableVideoDeal(SmallVideoDTO smallVideoDTO, SmallVideoParam smallVideoParam) {
        DataSourceHelper.reset();
        String enterpriseId = smallVideoParam.getEnterpriseId();
        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigMapper.selectByEnterpriseId(enterpriseId);
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());
        SupervisionDefDataColumnDO supervisionDefDataColumnDO = supervisionDefDataColumnMapper.selectByPrimaryKey(smallVideoParam.getBusinessId(), enterpriseId);
        smallVideoDTO.setStatus(ResourceStatusEnum.TRANSCODE_FINISH.getValue());
        String checkVideo = transSmallVideoDtoTOString(supervisionDefDataColumnDO.getCheckVideo(), smallVideoDTO);
        supervisionDefDataColumnMapper.updateDelVideo(enterpriseId, supervisionDefDataColumnDO.getId(), checkVideo);
    }

    /**
     * 巡店自定义检查项视频转码
     * @param smallVideoDTO
     * @param smallVideoParam
     */
    public void defColumnVideoDeal(SmallVideoDTO smallVideoDTO, SmallVideoParam smallVideoParam) {
        DataSourceHelper.reset();
        String enterpriseId = smallVideoParam.getEnterpriseId();
        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigMapper.selectByEnterpriseId(enterpriseId);
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());

        TbDataDefTableColumnDO dataDefTableColumnDO = tbDataDefTableColumnMapper.selectById(enterpriseId, smallVideoParam.getBusinessId());
        smallVideoDTO.setStatus(ResourceStatusEnum.TRANSCODE_FINISH.getValue());
        String checkVideo = transSmallVideoDtoTOString(dataDefTableColumnDO.getCheckVideo(), smallVideoDTO);
        tbDataDefTableColumnMapper.updateDelVideo(enterpriseId, dataDefTableColumnDO.getId(), checkVideo);
    }

    /**
     * 运营手册视频转码处理
     * @param smallVideoDTO
     * @param smallVideoParam
     */
    public void taskSopDeal(SmallVideoDTO smallVideoDTO, SmallVideoParam smallVideoParam) {
        DataSourceHelper.reset();
        String enterpriseId = smallVideoParam.getEnterpriseId();
        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigMapper.selectByEnterpriseId(enterpriseId);
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());

        TaskSopVO taskSop = taskSopMapper.getSopById(enterpriseId, smallVideoParam.getBusinessId());

        smallVideoDTO.setStatus(ResourceStatusEnum.TRANSCODE_FINISH.getValue());

        taskSop.setUrl(JSONObject.toJSONString(smallVideoDTO));

        TaskSopDO taskSopDO = TaskSopDO.builder().url(smallVideoDTO.getVideoUrl()).videoUrl(taskSop.getUrl()).id(taskSop.getId()).build();

        taskSopMapper.batchUpdateVideoUrl(enterpriseId, Arrays.asList(taskSopDO));
    }

    public void swStoreWorkTableColumnDeal(SmallVideoDTO smallVideoDTO, SmallVideoParam smallVideoParam) {
        DataSourceHelper.reset();
        String enterpriseId = smallVideoParam.getEnterpriseId();
        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigMapper.selectByEnterpriseId(enterpriseId);
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());
        SwStoreWorkDataTableColumnDO swStoreWorkDataTableColumnDO = swStoreWorkDataTableColumnDao.selectByPrimaryKey(smallVideoParam.getBusinessId(), enterpriseId);

        smallVideoDTO.setStatus(ResourceStatusEnum.TRANSCODE_FINISH.getValue());

        log.info("视频信息,checkVideo:{}", swStoreWorkDataTableColumnDO.getCheckVideo());
        String checkVideo = transSmallVideoDtoTOString(swStoreWorkDataTableColumnDO.getCheckVideo(), smallVideoDTO);
        swStoreWorkDataTableColumnDO.setCheckVideo(checkVideo);
        swStoreWorkDataTableColumnDao.updateByPrimaryKeySelective(swStoreWorkDataTableColumnDO,enterpriseId);

        //工单处理
        if(swStoreWorkDataTableColumnDO.getTaskQuestionId() > 0){
            //新工单视频转码处理
            TbQuestionRecordDO recordDO = questionRecordDao.getByDataColumnId(enterpriseId, swStoreWorkDataTableColumnDO.getId(),Boolean.TRUE);
            if(recordDO != null){
                TbQuestionRecordExpandDO expandDO = questionRecordExpandDao.selectByRecordId(enterpriseId, recordDO.getId());
                if(expandDO != null && StringUtils.isBlank(expandDO.getTaskInfo())){
                    QuestionTaskInfoDTO questionTaskInfoDTO = JSONObject.parseObject(expandDO.getTaskInfo(), QuestionTaskInfoDTO.class);
                    questionTaskInfoDTO.setVideos(checkVideo);
                    expandDO.setTaskInfo(JSONObject.toJSONString(questionTaskInfoDTO));
                    questionRecordExpandDao.updateByPrimaryKeySelective(enterpriseId, expandDO);
                }
            }
        }

    }

    public void tbDataStaTableColumnDeal(SmallVideoDTO smallVideoDTO, SmallVideoParam smallVideoParam) {

        DataSourceHelper.reset();
        String enterpriseId = smallVideoParam.getEnterpriseId();
        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigMapper.selectByEnterpriseId(enterpriseId);
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());
        TbDataStaTableColumnDO tbDataStaTableColumnDO = tbDataStaTableColumnMapper.selectById(enterpriseId, smallVideoParam.getBusinessId());

        smallVideoDTO.setStatus(ResourceStatusEnum.TRANSCODE_FINISH.getValue());

        log.info("视频信息,checkVideo:{}", tbDataStaTableColumnDO.getCheckVideo());
        String checkVideo = transSmallVideoDtoTOString(tbDataStaTableColumnDO.getCheckVideo(), smallVideoDTO);
        tbDataStaTableColumnDO.setCheckVideo(checkVideo);
        tbDataStaTableColumnMapper.updateVideo(enterpriseId, tbDataStaTableColumnDO);

        //工单处理
        if(tbDataStaTableColumnDO.getTaskQuestionId() > 0){
            //新工单视频转码处理
            TbQuestionRecordDO recordDO = questionRecordDao.getByDataColumnId(enterpriseId, tbDataStaTableColumnDO.getId(),Boolean.FALSE);
            if(recordDO != null){
                TbQuestionRecordExpandDO expandDO = questionRecordExpandDao.selectByRecordId(enterpriseId, recordDO.getId());
                if(expandDO != null && StringUtils.isBlank(expandDO.getTaskInfo())){
                    QuestionTaskInfoDTO questionTaskInfoDTO = JSONObject.parseObject(expandDO.getTaskInfo(), QuestionTaskInfoDTO.class);
                    questionTaskInfoDTO.setVideos(checkVideo);
                    expandDO.setTaskInfo(JSONObject.toJSONString(questionTaskInfoDTO));
                    questionRecordExpandDao.updateByPrimaryKeySelective(enterpriseId, expandDO);
                }
            }
        }
        TbPatrolStoreRecordDO tbPatrolStoreRecordDO = tbPatrolStoreRecordMapper.selectById(enterpriseId, tbDataStaTableColumnDO.getBusinessId());
        if (TaskTypeEnum.PATROL_STORE_SAFETY_CHECK.getCode().equals(tbPatrolStoreRecordDO.getPatrolType())) {
            List<TbDataColumnHistoryDO> dataColumnHistoryDOList = tbDataColumnHistoryDao.listDataColumnCheckHistory(enterpriseId, tbDataStaTableColumnDO.getBusinessId(), tbDataStaTableColumnDO.getId());
            if (CollectionUtils.isNotEmpty(dataColumnHistoryDOList)){
                for (TbDataColumnHistoryDO tbDataColumnHistoryDO : dataColumnHistoryDOList) {
                    String historyCheckVideo = transHistorySmallVideoDtoTOString(tbDataColumnHistoryDO.getCheckVideo(), smallVideoDTO);
                    tbDataColumnHistoryDO.setCheckVideo(historyCheckVideo);
                    tbDataColumnHistoryDao.updateByPrimaryKeySelective(tbDataColumnHistoryDO, enterpriseId);
                }
            }
        }

    }

    public void tbPatrolStoreRecordDeal(SmallVideoDTO smallVideoDTO, SmallVideoParam smallVideoParam) {
        DataSourceHelper.reset();
        String enterpriseId = smallVideoParam.getEnterpriseId();
        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigMapper.selectByEnterpriseId(enterpriseId);
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());
        TbPatrolStoreRecordDO tbPatrolStoreRecordDO = tbPatrolStoreRecordMapper.selectById(enterpriseId, smallVideoParam.getBusinessId());

        log.info("视频信息,checkVideo:{}", tbPatrolStoreRecordDO.getSummaryVideo());
        tbPatrolStoreRecordDO.setSummaryVideo(transSmallVideoDtoTOString(tbPatrolStoreRecordDO.getSummaryVideo(), smallVideoDTO));
        tbPatrolStoreRecordMapper.updateVideo(enterpriseId, tbPatrolStoreRecordDO);
    }

    public void taskQuestionDeal(SmallVideoDTO smallVideoDTO, SmallVideoParam smallVideoParam) {
        DataSourceHelper.reset();
        String enterpriseId = smallVideoParam.getEnterpriseId();
        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigMapper.selectByEnterpriseId(enterpriseId);
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());
        TaskParentDO taskParentDO = taskParentMapper.selectTaskById(enterpriseId, smallVideoParam.getBusinessId());
        String taskInfo = taskParentDO.getTaskInfo();
        log.info("工单信息,taskInfo:{}", taskInfo);
        if(StringUtils.isNotBlank(taskInfo)){
            JSONObject jsonObject = JSONObject.parseObject(taskInfo);
            String videos = jsonObject.getString("videos");
            if(StringUtils.isNotBlank(videos)){
                String checkVideo = transSmallVideoDtoTOString(videos, smallVideoDTO);
                jsonObject.put("videos" ,checkVideo);
                taskParentDO.setTaskInfo(jsonObject.toJSONString());
                taskParentMapper.updateParentByDO(enterpriseId,taskParentDO);
            }
        }
    }

    public void taskParentQuestionCreateDeal(SmallVideoDTO smallVideoDTO, SmallVideoParam smallVideoParam) {
        DataSourceHelper.reset();
        String enterpriseId = smallVideoParam.getEnterpriseId();
        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigMapper.selectByEnterpriseId(enterpriseId);
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());
        UnifyTaskParentItemDO unifyTaskParentItemDO = unifyTaskParentItemDao.selectByPrimaryKey(enterpriseId, smallVideoParam.getBusinessId());
        if(unifyTaskParentItemDO == null){
            log.info("工单任务不存在eid:{},businessId:{}", enterpriseId, smallVideoParam.getBusinessId());
            return;
        }
        String taskInfo = unifyTaskParentItemDO.getTaskInfo();
        log.info("工单信息,taskInfo:{}", taskInfo);
        if(StringUtils.isNotBlank(taskInfo)){
            QuestionTaskInfoDTO taskInfoDTO = JSONObject.parseObject(unifyTaskParentItemDO.getTaskInfo(), QuestionTaskInfoDTO.class);
            String videos = taskInfoDTO.getVideos();
            if(StringUtils.isNotBlank(videos)){
                String checkVideo = transSmallVideoDtoTOString(videos, smallVideoDTO);
                taskInfoDTO.setVideos(checkVideo);
                unifyTaskParentItemDO.setTaskInfo(JSONObject.toJSONString(taskInfoDTO));
                unifyTaskParentItemDao.updateByPrimaryKeySelective(enterpriseId, unifyTaskParentItemDO);
                TbQuestionRecordDO recordDO = questionRecordDao.selectByTaskIdAndStoreId(enterpriseId, unifyTaskParentItemDO.getUnifyTaskId(),
                        unifyTaskParentItemDO.getStoreId(), unifyTaskParentItemDO.getLoopCount());
                if(recordDO != null){
                    TbQuestionRecordExpandDO expandDO = questionRecordExpandDao.selectByRecordId(enterpriseId, recordDO.getId());
                    if(expandDO != null){
                        expandDO.setTaskInfo(unifyTaskParentItemDO.getTaskInfo());
                        questionRecordExpandDao.updateByPrimaryKeySelective(enterpriseId, expandDO);
                    }
                }
            }
        }
    }

    public void taskQuestionSummitVideoDeal(SmallVideoDTO smallVideoDTO, SmallVideoParam smallVideoParam) {
        DataSourceHelper.reset();
        String enterpriseId = smallVideoParam.getEnterpriseId();
        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigMapper.selectByEnterpriseId(enterpriseId);
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());


        TbQuestionHistoryDO questionHistoryDO = questionHistoryDao.selectByPrimaryKey(enterpriseId, smallVideoParam.getBusinessId());
        if(questionHistoryDO == null){
            log.info("taskQuestionSummitVideoDeal eid:{}不存在", enterpriseId);
            return;
        }

        String videos = questionHistoryDO.getVideo();

        if(StringUtils.isNotBlank(videos)){
            TbQuestionHistoryDO historyUpdate = new TbQuestionHistoryDO();
            historyUpdate.setId(questionHistoryDO.getId());
            historyUpdate.setUpdateTime(new Date());
            historyUpdate.setVideo(transSmallVideoDtoTOString(videos, smallVideoDTO));
            questionHistoryDao.updateByPrimaryKeySelective(enterpriseId, historyUpdate);
        }

        TbQuestionRecordExpandDO tbQuestionRecordExpandDO = questionRecordExpandDao.selectByRecordId(enterpriseId, questionHistoryDO.getRecordId());
        if(tbQuestionRecordExpandDO != null){
            TbQuestionRecordExpandDO tbQuestionRecordExpandUpdate = new TbQuestionRecordExpandDO();
            tbQuestionRecordExpandUpdate.setId(tbQuestionRecordExpandDO.getId());
            if(StringUtils.isNotBlank(tbQuestionRecordExpandDO.getHandleVideo())){
                tbQuestionRecordExpandUpdate.setHandleVideo(transSmallVideoDtoTOString(tbQuestionRecordExpandDO.getHandleVideo(), smallVideoDTO));
            }

            if(StringUtils.isNotBlank(tbQuestionRecordExpandDO.getApproveVideo())){
                tbQuestionRecordExpandUpdate.setApproveVideo(transSmallVideoDtoTOString(tbQuestionRecordExpandDO.getApproveVideo(), smallVideoDTO));
            }
            tbQuestionRecordExpandUpdate.setUpdateTime(new Date());
            questionRecordExpandDao.updateByPrimaryKeySelective(enterpriseId, tbQuestionRecordExpandUpdate);
        }

    }

    public void questionOrderDeal(SmallVideoDTO smallVideoDTO, SmallVideoParam smallVideoParam) {
        DataSourceHelper.reset();
        String enterpriseId = smallVideoParam.getEnterpriseId();
        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigMapper.selectByEnterpriseId(enterpriseId);
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());
        TbPatrolStoreRecordDO tbPatrolStoreRecordDO = tbPatrolStoreRecordMapper.selectById(enterpriseId, smallVideoParam.getBusinessId());

        log.info("视频信息,checkVideo:{}", tbPatrolStoreRecordDO.getSummaryVideo());
        tbPatrolStoreRecordDO.setSummaryVideo(transSmallVideoDtoTOString(tbPatrolStoreRecordDO.getSummaryVideo(), smallVideoDTO));
        tbPatrolStoreRecordMapper.updateVideo(enterpriseId, tbPatrolStoreRecordDO);
    }

    public Object getTicket(String fileName) {
        try {
            DefaultAcsClient client = new DefaultAcsClient(DefaultProfile.getProfile(region, accessKeyId, accessKeySecret));

            CreateUploadVideoRequest request = new CreateUploadVideoRequest();

            /*必选，视频源文件名称（必须带后缀, 支持 ".3gp", ".asf", ".avi", ".dat", ".dv", ".flv", ".f4v", ".gif", ".m2t", ".m3u8", ".m4v", ".mj2", ".mjpeg", ".mkv", ".mov", ".mp4", ".mpe", ".mpg", ".mpeg", ".mts", ".ogg", ".qt", ".rm", ".rmvb", ".swf", ".ts", ".vob", ".wmv", ".webm"".aac", ".ac3", ".acm", ".amr", ".ape", ".caf", ".flac", ".m4a", ".mp3", ".ra", ".wav", ".wma"）*/
            //如果上传的是mp4，则不进行转码
            if (Files.getFileExtension(fileName).equalsIgnoreCase(Constants.TRANSCODE_VIDEO)) {
                request.setTemplateGroupId(noTranscodeGroupId);
            } else {
                request.setWorkflowId(vodWorkflowId);
            }
            request.setFileName(fileName);
            //必选，视频标题
            request.setTitle(fileName);
            // 消息回调设置
            UserData userData = new UserData();
            userData.setCallBack(callBackUrl);
            request.setUserData(JSONObject.toJSONString(userData));
            request.setCateId(cateId);
            request.setStorageLocation(storageLocation);
            log.info("getTicket request:{}", JSONObject.toJSONString(request));
            CreateUploadVideoResponse response = client.getAcsResponse(request);
            log.info("RequestId:{}", response.getRequestId());
            log.info("UploadAuth:{}", response.getUploadAuth());
            log.info("UploadAddress:{}", response.getUploadAddress());
            log.info("VideoId:{}", response.getVideoId());
            return response;

        } catch (ServerException e) {
            log.error("CreateUploadVideoRequest Server Exception:");
            e.printStackTrace();
            return null;
        } catch (ClientException e) {
            log.error("CreateUploadVideoRequest Client Exception:");
            e.printStackTrace();
            return null;
        } catch (com.aliyuncs.exceptions.ClientException e) {
            log.error("CreateUploadVideoRequest Client Exception:");
            e.printStackTrace();
        }

        return null;
    }

    public Object refreshTicket(String videoId) {

        DefaultAcsClient client = new DefaultAcsClient(DefaultProfile.getProfile(region, accessKeyId, accessKeySecret));

        RefreshUploadVideoRequest request = new RefreshUploadVideoRequest();

        RefreshUploadVideoResponse response = null;
        try {
            request.setVideoId(videoId);
            response = client.getAcsResponse(request);
            return response;
        } catch (ServerException e) {
            log.error("RefreshUploadVideoRequest Server Exception:");
            e.printStackTrace();
        } catch (ClientException | com.aliyuncs.exceptions.ClientException e) {
            log.error("RefreshUploadVideoRequest Client Exception:");
            e.printStackTrace();
        }
        return null;
    }

    public Object getVideoInfoByRedis(String videoId) {
        String callbackCache = redisUtil.getString(RedisConstant.VIDEO_CALLBACK_CACHE + videoId);
        if (StringUtils.isEmpty(callbackCache)){
            return ResponseResult.fail(ErrorCodeEnum.FAIL);
        }
        return JSONObject.parseObject(callbackCache, SmallVideoDTO.class);
    }

    public TicketAssumeRoleResponse getSTSTicket() {
        TicketAssumeRoleResponse ticketAssumeRoleResponse = new TicketAssumeRoleResponse();

        AssumeRoleResponse response = null;
                // 只有RAM用户（子账号）才能调用 AssumeRole 接口
        // 阿里云主账号的AccessKeys不能用于发起AssumeRole请求
        // 请首先在RAM控制台创建一个RAM用户，并为这个用户创建AccessKeys
        // AssumeRole API 请求参数: RoleArn, RoleSessionName, Policy, and DurationSeconds
        // RoleArn 需要在 RAM 控制台上获取
        // RoleSessionName 角色会话名称，自定义参数
        String roleSessionName = "voderole_";// 自定义即可
        // 定制你的policy
        String policy = "{\n" +
                "  \"Version\": \"1\",\n" +
                "  \"Statement\": [\n" +
                "    {\n" +
                "      \"Action\": \"vod:*\",\n" +
                "      \"Resource\": \"*\",\n" +
                "      \"Effect\": \"Allow\"\n" +
                "    }\n" +
                "  ]\n" +
                "}";
        try {
            response = assumeRole(accessKeyId, accessKeySecret, roleArn, roleSessionName, policy, region);
            log.info("getSTSTicket#response:{}", JSONObject.toJSONString(response));
            ticketAssumeRoleResponse.setCallBackUrl(callBackUrl);
            ticketAssumeRoleResponse.setCateId(cateId);
            ticketAssumeRoleResponse.setRequestId(response.getRequestId());
            ticketAssumeRoleResponse.setCredentials(response.getCredentials());
            ticketAssumeRoleResponse.setAssumedRoleUser(response.getAssumedRoleUser());
        } catch (Exception e) {
            log.error("getSTSTicket#error", e);
            throw new ServiceException(ErrorCodeEnum.VOD_ERROR);
        }
        return ticketAssumeRoleResponse;
    }

    static AssumeRoleResponse assumeRole(String accessKeyId, String accessKeySecret, String roleArn, String roleSessionName, String policy,
                                         String regionId) throws ClientException {
        AssumeRoleResponse response = null;
        try {
            //构造default profile（参数留空，无需添加Region ID）
            /*
            说明：当设置SysEndpoint为sts.aliyuncs.com时，regionId可填可不填；反之，regionId必填，根据使用的服务区域填写，例如：cn-shanghai
            详情参考STS各地域的Endpoint，请参见接入地址。
             */
            IClientProfile profile = DefaultProfile.getProfile(regionId, accessKeyId, accessKeySecret);
            //用profile构造client
            DefaultAcsClient client = new DefaultAcsClient(profile);
            // 创建一个 AssumeRoleRequest 并设置请求参数
            final AssumeRoleRequest request = new AssumeRoleRequest();
            request.setSysEndpoint("sts.aliyuncs.com");
            request.setSysMethod(MethodType.POST);
            request.setRoleArn(roleArn);
            request.setRoleSessionName(roleSessionName);
            request.setPolicy(policy);
            // 发起请求，并得到response
            response = client.getAcsResponse(request);
            return response;
        } catch (Exception e) {
            log.error("assumeRole#error", e);
            throw new ServiceException(ErrorCodeEnum.VOD_ERROR);
        }
    }

    public CreateUploadVideoResponse getXfsgSTSTicket(String fileName) {
        try {
            DefaultAcsClient client = new DefaultAcsClient(DefaultProfile.getProfile(region, accessKeyId, accessKeySecret));

            CreateUploadVideoRequest request = new CreateUploadVideoRequest();

            /*必选，视频源文件名称（必须带后缀, 支持 ".3gp", ".asf", ".avi", ".dat", ".dv", ".flv", ".f4v", ".gif", ".m2t", ".m3u8", ".m4v", ".mj2", ".mjpeg", ".mkv", ".mov", ".mp4", ".mpe", ".mpg", ".mpeg", ".mts", ".ogg", ".qt", ".rm", ".rmvb", ".swf", ".ts", ".vob", ".wmv", ".webm"".aac", ".ac3", ".acm", ".amr", ".ape", ".caf", ".flac", ".m4a", ".mp3", ".ra", ".wav", ".wma"）*/
            //如果上传的是mp4，则不进行转码
            if (Files.getFileExtension(fileName).equalsIgnoreCase(Constants.TRANSCODE_VIDEO)) {
                request.setTemplateGroupId(noTranscodeGroupId);
            } else {
                request.setWorkflowId(vodWorkflowId);
            }
            request.setFileName(fileName);
            //必选，视频标题
            request.setTitle(fileName);
            // 消息回调设置
            UserData userData = new UserData();
            userData.setCallBack(xsfgCallBackUrl);
            request.setUserData(JSONObject.toJSONString(userData));
            request.setStorageLocation(storageLocation);
            request.setCateId(cateId);
            log.info("getTicket request:{}", JSONObject.toJSONString(request));
            CreateUploadVideoResponse response = client.getAcsResponse(request);
            log.info("RequestId:{}", response.getRequestId());
            log.info("UploadAuth:{}", response.getUploadAuth());
            log.info("UploadAddress:{}", response.getUploadAddress());
            log.info("VideoId:{}", response.getVideoId());
            return response;

        } catch (ServerException e) {
            log.error("CreateUploadVideoRequest Server Exception:");
            e.printStackTrace();
            return null;
        } catch (ClientException e) {
            log.error("CreateUploadVideoRequest Client Exception:");
            e.printStackTrace();
            return null;
        } catch (com.aliyuncs.exceptions.ClientException e) {
            log.error("CreateUploadVideoRequest Client Exception:");
            e.printStackTrace();
        }

        return null;
    }

    public CreateUploadVideoResponse getVideoTicket(String fileName, String callbackUrlSuffix) {
        try {
            DefaultAcsClient client = new DefaultAcsClient(DefaultProfile.getProfile(region, accessKeyId, accessKeySecret));

            CreateUploadVideoRequest request = new CreateUploadVideoRequest();

            /*必选，视频源文件名称（必须带后缀, 支持 ".3gp", ".asf", ".avi", ".dat", ".dv", ".flv", ".f4v", ".gif", ".m2t", ".m3u8", ".m4v", ".mj2", ".mjpeg", ".mkv", ".mov", ".mp4", ".mpe", ".mpg", ".mpeg", ".mts", ".ogg", ".qt", ".rm", ".rmvb", ".swf", ".ts", ".vob", ".wmv", ".webm"".aac", ".ac3", ".acm", ".amr", ".ape", ".caf", ".flac", ".m4a", ".mp3", ".ra", ".wav", ".wma"）*/
            //如果上传的是mp4，则不进行转码
            if (Files.getFileExtension(fileName).equalsIgnoreCase(Constants.TRANSCODE_VIDEO)) {
                request.setTemplateGroupId(noTranscodeGroupId);
            } else {
                request.setWorkflowId(vodWorkflowId);
            }
            request.setFileName(fileName);
            //必选，视频标题
            request.setTitle(fileName);
            // 消息回调设置
            UserData userData = new UserData();
            userData.setCallBack(apiDomainUrl + callbackUrlSuffix);
            request.setUserData(JSONObject.toJSONString(userData));
            request.setCateId(cateId);
            request.setStorageLocation(storageLocation);
            log.info("getTicket request:{}", JSONObject.toJSONString(request));
            CreateUploadVideoResponse response = client.getAcsResponse(request);
            log.info("RequestId:{}", response.getRequestId());
            log.info("UploadAuth:{}", response.getUploadAuth());
            log.info("UploadAddress:{}", response.getUploadAddress());
            log.info("VideoId:{}", response.getVideoId());
            return response;
        } catch (ServerException e) {
            log.error("CreateUploadVideoRequest Server Exception:");
            e.printStackTrace();
            return null;
        } catch (ClientException e) {
            log.error("CreateUploadVideoRequest Client Exception:");
            e.printStackTrace();
            return null;
        } catch (com.aliyuncs.exceptions.ClientException e) {
            log.error("CreateUploadVideoRequest Client Exception:");
            e.printStackTrace();
        }
        return null;
    }
}
