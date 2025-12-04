package com.coolcollege.intelligent.service.sop.impl;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.constant.RedisConstant;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.enums.video.ResourceStatusEnum;
import com.coolcollege.intelligent.common.enums.video.UploadTypeEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.page.PageVO;
import com.coolcollege.intelligent.common.sync.conf.Role;
import com.coolcollege.intelligent.common.util.PageHelperUtil;
import com.coolcollege.intelligent.dao.sop.TaskSopMapper;
import com.coolcollege.intelligent.model.sop.TaskSopClassifyDO;
import com.coolcollege.intelligent.model.sop.TaskSopDO;
import com.coolcollege.intelligent.model.sop.dto.TaskSopClassifyDTO;
import com.coolcollege.intelligent.model.sop.dto.TaskSopClassifySelectDTO;
import com.coolcollege.intelligent.model.sop.dto.TaskSopDTO;
import com.coolcollege.intelligent.model.sop.query.TaskSopQuery;
import com.coolcollege.intelligent.model.sop.vo.TaskSopListVO;
import com.coolcollege.intelligent.model.sop.vo.TaskSopVO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.model.userholder.UserHolder;
import com.coolcollege.intelligent.model.video.SmallVideoDTO;
import com.coolcollege.intelligent.model.video.param.SmallVideoParam;
import com.coolcollege.intelligent.service.enterprise.EnterpriseUserService;
import com.coolcollege.intelligent.service.enterprise.UserPersonInfoService;
import com.coolcollege.intelligent.service.fileUpload.OssClientService;
import com.coolcollege.intelligent.service.oneparty.OnePartyService;
import com.coolcollege.intelligent.service.sop.TaskSopService;
import com.coolcollege.intelligent.service.system.SysRoleService;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author 邵凌志
 * @date 2021/2/20 17:08
 */
@Service
@Slf4j
public class TaskSopServiceImpl implements TaskSopService {

    @Resource
    private TaskSopMapper taskSopMapper;

    @Autowired
    private OssClientService ossClientService;

    @Autowired
    private RedisUtilPool redis;

    @Autowired
    private SysRoleService sysRoleService;

    @Autowired
    private EnterpriseUserService enterpriseUserService;
    @Resource
    private OnePartyService onePartyService;
    @Autowired
    private RedisUtilPool redisUtil;

    @Autowired
    @Lazy
    private  UserPersonInfoService userPersonInfoService;

    public static final String TASK_SOP_DOC_KEY = "task_sop_doc_";

    private static final int CACHE_TIME = 60 * 60 * 24 * 7; // 7天

    @Override
    public Boolean insertSop(String eid, TaskSopVO sop) {
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        TaskSopDO taskSop = buildTaskSop(sop, user,eid);
        taskSopMapper.addSopFile(eid, taskSop);
        return Boolean.TRUE;
    }

    @Override
    public TaskSopDO insertSopInfo(String eid, TaskSopVO sop) {
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        TaskSopDO taskSop = buildTaskSop(sop, user,eid);
        taskSopMapper.addSopFile(eid, taskSop);
        if("video".equals(taskSop.getCategory())){
            checkVideo(taskSop, eid);
            taskSopMapper.batchUpdateVideoUrl(eid, Arrays.asList(taskSop));
        }
        return taskSop;
    }

    @Override
    public Boolean batchInsertSop(String eid, TaskSopListVO sopInfo, CurrentUser user) {
        List<TaskSopVO> sopList = sopInfo. getSopList();
        List<TaskSopDO> sopDOList = new ArrayList<>();
        sopList.forEach(sop -> {
            TaskSopDO taskSopDO = buildTaskSop(sop, user,eid);
            taskSopDO.setVisibleUser(sopInfo.getVisibleUser());
            taskSopDO.setVisibleRole(sopInfo.getVisibleRole());
            taskSopDO.setUrl(sop.getUrl());
            if ("video".equals(sop.getCategory())){
                taskSopDO.setUrl(sop.getUrl());
            }
            taskSopDO.setVisibleUserName(sopInfo.getVisibleUserName());
            taskSopDO.setVisibleRoleName(sopInfo.getVisibleRoleName());
            taskSopDO.setBusinessType(sopInfo.getBusinessType());
            taskSopDO.setUseRange(sopInfo.getUseRange());
            taskSopDO.setUseUserids(userPersonInfoService.getUserIds(eid, sopInfo.getUsePersonInfo(), sopInfo.getUseRange(), user.getUserId()));
            taskSopDO.setUsePersonInfo(sopInfo.getUsePersonInfo());
            sopDOList.add(taskSopDO);
        });
        taskSopMapper.batchInsertSop(eid, sopDOList);
        //视频更新转码之后的数据
        List<TaskSopDO> videoList = sopDOList.stream().filter(x -> "video".equals(x.getCategory())).collect(Collectors.toList());
        log.info("videoList_before{}",JSONObject.toJSONString(videoList));
        if (CollectionUtils.isNotEmpty(videoList)){
            videoList.forEach(x->{
                checkVideo(x,eid);
            });
            log.info("videoList_after{}",JSONObject.toJSONString(videoList));
            taskSopMapper.batchUpdateVideoUrl(eid,videoList);
        }
        return Boolean.TRUE;
    }

    @Override
    public Boolean updateSopVisibleUser(String eid, TaskSopListVO sop, CurrentUser user) {
        List<Long> sopIds = sop.getSopList().stream().map(TaskSopVO::getId).collect(Collectors.toList());
        sop.setUseUserids(userPersonInfoService.getUserIds(eid, sop.getUsePersonInfo(), sop.getUseRange(), user.getUserId()));
        if (Role.MASTER.getId().equals(user.getSysRoleDO().getId().toString())) {
            taskSopMapper.updateSopVisibleUser(eid, sopIds, sop);
            return Boolean.TRUE;
        }
        List<TaskSopVO> sopList = taskSopMapper.listByIdList(eid, sopIds);
        List<TaskSopVO> checkList = sopList.stream()
                .filter(check -> !user.getUserId().equals(check.getCreateUserId())).collect(Collectors.toList());
        if (checkList.size() > 0) {
            throw new ServiceException("只有管理员和文档创建者才能修改文档可视范围");
        }
        taskSopMapper.updateSopVisibleUser(eid, sopIds, sop);
        return Boolean.TRUE;
    }

    private TaskSopDO buildTaskSop(TaskSopVO sop, CurrentUser user,String eid) {
        String fileName = sop.getFileName();
        String[] split = fileName.split("\\.");
        if (split.length <= 1) {
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "文件名称不合法");
        }
        String type = split[split.length - 1];
        TaskSopDO taskSop = TaskSopDO.builder()
                .category(sop.getCategory())
                .fileName(sop.getFileName())
                .type(type)
                .createUser(user.getName())
                .createUserId(user.getUserId())
                .businessType(sop.getBusinessType())
                .useRange(sop.getUseRange())
                .usePersonInfo(sop.getUsePersonInfo())
                .build();
        if(StringUtils.isNotBlank(sop.getUseRange())){
            taskSop.setUseUserids(userPersonInfoService.getUserIds(eid, sop.getUsePersonInfo(), sop.getUseRange(), user.getUserId()));
        }
        if ("video".equals(sop.getCategory())){
            taskSop.setUrl(sop.getVideoUrl());
        }else {
            taskSop.setUrl(sop.getUrl());
        }
        return taskSop;
    }

    /**
     * 如果状态为转码完成，直接修改，否则从redis获取转码的视频信息
     * @author chenyupeng
     * @date 2021/10/14
     * @param taskSopDO
     * @param enterpriseId
     * @return void
     */
    public void checkVideo(TaskSopDO taskSopDO, String enterpriseId){
        //视频转码
        log.info("sop_转码开始,{}",JSONObject.toJSONString(taskSopDO));
        if(taskSopDO ==null){
            return;
        }
        SmallVideoDTO smallVideoDTO = JSONObject.parseObject(taskSopDO.getUrl(), SmallVideoDTO.class);
        if(smallVideoDTO != null){
            String callbackCache;
            SmallVideoDTO smallVideoCache;
            SmallVideoParam smallVideoParam;
            //如果转码完成 直接修改
            if(smallVideoDTO.getStatus() != null && smallVideoDTO.getStatus() >= ResourceStatusEnum.TRANSCODE_FINISH.getValue()){
                return;
            }
            callbackCache = redisUtil.getString(RedisConstant.VIDEO_CALLBACK_CACHE + smallVideoDTO.getVideoId());
            if(StringUtils.isNotBlank(callbackCache)){
                smallVideoCache = JSONObject.parseObject(callbackCache,SmallVideoDTO.class);
                if(smallVideoCache !=null && smallVideoCache.getStatus() !=null && smallVideoCache.getStatus() >= ResourceStatusEnum.TRANSCODE_FINISH.getValue()){
                    BeanUtils.copyProperties(smallVideoCache,smallVideoDTO);
                    //直接修改
                    taskSopDO.setVideoUrl(JSONObject.toJSONString(smallVideoDTO));
                    taskSopDO.setUrl(smallVideoDTO.getVideoUrl());
                }else {
                    smallVideoParam = new SmallVideoParam();
                    setNotCompleteCache(smallVideoParam,smallVideoDTO,enterpriseId,taskSopDO.getId());
                }
            }else {
                smallVideoParam = new SmallVideoParam();
                setNotCompleteCache(smallVideoParam,smallVideoDTO,enterpriseId,taskSopDO.getId());
            }
        }
    }

    /**
     * 如果前端提交的时候，视频还没有转码成功，会把videoId存入缓存，回调的时候再进行处理
     * @param smallVideoParam
     * @param smallVideo
     * @param enterpriseId
     */
    public void setNotCompleteCache(SmallVideoParam smallVideoParam, SmallVideoDTO smallVideo, String enterpriseId,Long id){
        smallVideoParam.setVideoId(smallVideo.getVideoId());
        smallVideoParam.setUploadType(UploadTypeEnum.TASK_SOP_ADD.getValue());
        smallVideoParam.setUploadTime(new Date());
        smallVideoParam.setBusinessId(id);
        smallVideoParam.setEnterpriseId(enterpriseId);
        //存入未转码完成的map，vod回调的时候使用
        redisUtil.hashSet(RedisConstant.VIDEO_NOT_COMPLETE_CACHE,smallVideo.getVideoId(), JSONObject.toJSONString(smallVideoParam));
    }

    @Override
    public PageVO selectTaskSopList(String eid, TaskSopQuery query , CurrentUser user) {
        List<String> roles = sysRoleService.getRoleIdByUserId(eid, user.getUserId());
        PageHelper.startPage(query.getPageNum(), query.getPageSize());
        // 未删除
        query.setIsDeleted(0);
        query.setUserId(user.getUserId());
        List<TaskSopDTO> taskSopDTOList = new ArrayList<>();
        if (roles.contains(Role.MASTER.getId())) {
            taskSopDTOList = taskSopMapper.selectAllTaskSop(eid, query);
        } else {
            taskSopDTOList = taskSopMapper.selectTaskSopList(eid, query);
        }
        return PageHelperUtil.getPageVO(new PageInfo<>(taskSopDTOList));
    }

    @Override
    public List<TaskSopVO> listByIdList(String enterpriseId, List<Long> sopIdList) {
        if(CollUtil.isEmpty(sopIdList)){
            return Collections.EMPTY_LIST;
        }
        List<TaskSopVO> taskSopVOList = taskSopMapper.listByIdList(enterpriseId, sopIdList);
        taskSopVOList.forEach(taskSopVO -> {
            fillPreviewUrl(taskSopVO);
        });
        return taskSopVOList;
    }

    @Override
    public TaskSopVO getSopById(String enterpriseId, Long id) {
        TaskSopVO taskSopVO = taskSopMapper.getSopById(enterpriseId, id);
        taskSopVO = fillPreviewUrl(taskSopVO);
        return taskSopVO;
    }

    @Override
    public void batchDeleteSop(String enterpriseId, List<Long> sopIdList, CurrentUser user) {
        final boolean[] isCurrent = {true};
        List<TaskSopVO> taskSopVOList = this.listByIdList(enterpriseId, sopIdList);
        taskSopVOList.stream().forEach(taskSopVO -> {
            if(!user.getUserId().equals(taskSopVO.getCreateUserId())){
                isCurrent[0] = false;
            }
        });
        Boolean isAdmin = sysRoleService.checkIsAdmin(enterpriseId, user.getUserId());
        if(isAdmin || isCurrent[0]){
            taskSopMapper.batchDeleteSop(enterpriseId, sopIdList);
        }else {
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "只有管理员或本人才能删除！");
        }

    }

    @Override
    public Boolean addSopClassify(String eid, String classifyName) {
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        TaskSopClassifyDO taskSopClassify = TaskSopClassifyDO.builder()
                .classifyName(classifyName)
                .createUserId(user.getUserId())
                .createUser(user.getName())
                .build();
        taskSopMapper.addSopClassify(eid, taskSopClassify);
        return Boolean.TRUE;
    }

    @Override
    public Boolean updateSopClassify(String eid, TaskSopClassifyDTO classify) {
        if (classify.getId() == null) {
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "请选择需要修改的分类");
        }
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        classify.setUpdateUserId(user.getUserId());
        classify.setUpdateUser(user.getName());
        taskSopMapper.updateSopClassify(eid, classify);
        return Boolean.TRUE;
    }

    @Override
    public List<TaskSopClassifySelectDTO> selectSopClassifyList(String eid) {
        return taskSopMapper.selectSopClassifyList(eid);
    }

    @Override
    public List<String> selectAllCategory(String enterpriseId) {
        List<String> categoryList = taskSopMapper.selectAllCategory(enterpriseId);
        return categoryList;
    }

    @Override
    public List<TaskSopDO> batchInsertSupervisionSop(String eid, TaskSopListVO sopInfo, CurrentUser user) {
        List<TaskSopVO> sopList = sopInfo.getSopList();
        List<TaskSopDO> sopDOList = new ArrayList<>();
        sopList.forEach(sop -> {
            TaskSopDO taskSopDO = buildTaskSop(sop, user,eid);
            taskSopDO.setVisibleUser(sopInfo.getVisibleUser());
            taskSopDO.setVisibleRole(sopInfo.getVisibleRole());
            taskSopDO.setUrl(sop.getUrl());
            if ("video".equals(sop.getCategory())){
                SmallVideoDTO smallVideoDTO = new SmallVideoDTO();
                smallVideoDTO.setVideoId(sop.getVideoId());
                taskSopDO.setUrl(JSONObject.toJSONString(smallVideoDTO));
            }
            taskSopDO.setVisibleUserName(sopInfo.getVisibleUserName());
            taskSopDO.setVisibleRoleName(sopInfo.getVisibleRoleName());
            taskSopDO.setBusinessType(sopInfo.getBusinessType());
            taskSopDO.setUseRange(sopInfo.getUseRange());
            taskSopDO.setUseUserids(userPersonInfoService.getUserIds(eid, sopInfo.getUsePersonInfo(), sopInfo.getUseRange(), user.getUserId()));
            taskSopDO.setUsePersonInfo(sopInfo.getUsePersonInfo());
            sopDOList.add(taskSopDO);
        });
        taskSopMapper.batchInsertSop(eid, sopDOList);
        //视频更新转码之后的数据
        List<TaskSopDO> videoList = sopDOList.stream().filter(x -> "video".equals(x.getCategory())).collect(Collectors.toList());
        log.info("videoList_before{}",JSONObject.toJSONString(videoList));
        if (CollectionUtils.isNotEmpty(videoList)){
            videoList.forEach(x->{
                checkVideo(x,eid);
            });
            log.info("videoList_after{}",JSONObject.toJSONString(videoList));
            taskSopMapper.batchUpdateVideoUrl(eid,videoList);
        }
        return sopDOList;
    }

    @Override
    public Boolean updateSopUrl(String enterpriseId, List<TaskSopDO> sops) {
        taskSopMapper.updateSopUrl(enterpriseId, sops);
        return Boolean.TRUE;
    }

    @Override
    public List<TaskSopDO> getDisplaySopAndUsedUserContainUserId(String enterpriseId, String userId, String name, String startTime, String endTime) {
        return taskSopMapper.selectDisplaySopAndUsedUserContainUserId(enterpriseId, userId, name, startTime, endTime);
    }


    public TaskSopVO fillPreviewUrl(TaskSopVO taskSopVO) {
        if(taskSopVO == null){
            return taskSopVO;
        }
        /*String key = TASK_SOP_DOC_KEY + taskSopVO.getId();
        String previewUrl = redis.getString(key);
        if(StringUtils.isBlank(previewUrl)){
            previewUrl = ossClientService.getPreviewUrl(FileUtils.getFilePathWithOutHost(taskSopVO.getUrl()));
            // 缓存7天
            redis.setString(key, previewUrl, CACHE_TIME);
        }*/
        String previewUrl;
        if (isPictureOrVideo(taskSopVO.getType())) {
            previewUrl = taskSopVO.getUrl();
        } else {
            previewUrl = ossClientService.getPreviewUrl(taskSopVO.getUrl());
        }
        taskSopVO.setPreviewUrl(previewUrl);
        return  taskSopVO;
    }

    private Boolean isPictureOrVideo(String type) {
        return Constants.PNG.equals(type) || Constants.JPG.equals(type) || Constants.JPEG.equals(type) || Constants.MP4.equals(type);
    }

}
