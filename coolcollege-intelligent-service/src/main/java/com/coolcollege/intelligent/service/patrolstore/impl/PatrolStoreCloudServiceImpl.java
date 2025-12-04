package com.coolcollege.intelligent.service.patrolstore.impl;

import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.RedisConstant;
import com.coolcollege.intelligent.common.enums.video.ResourceStatusEnum;
import com.coolcollege.intelligent.common.enums.video.UploadTypeEnum;
import com.coolcollege.intelligent.dao.patrolstore.dao.TbPatrolStoreCloudDao;
import com.coolcollege.intelligent.model.patrolstore.entity.TbPatrolStoreCloudDO;
import com.coolcollege.intelligent.model.patrolstore.request.AddPatrolStoreCloudRequest;
import com.coolcollege.intelligent.model.patrolstore.vo.PatrolStoreCloudVO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.model.video.SmallVideoDTO;
import com.coolcollege.intelligent.model.video.SmallVideoInfoDTO;
import com.coolcollege.intelligent.model.video.param.SmallVideoParam;
import com.coolcollege.intelligent.service.patrolstore.PatrolStoreCloudService;
import com.coolcollege.intelligent.util.RedisUtilPool;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Objects;

/**
 * @Author: hu hu
 * @Date: 2024/11/27 14:00
 * @Description:
 */
@Service
public class PatrolStoreCloudServiceImpl implements PatrolStoreCloudService {

    @Resource
    private TbPatrolStoreCloudDao tbPatrolStoreCloudDao;
    @Autowired
    private RedisUtilPool redisUtil;

    @Override
    public Long insertOrUpdate(String enterpriseId, AddPatrolStoreCloudRequest param, CurrentUser currentUser) {
        Long businessId = param.getBusinessId();
        TbPatrolStoreCloudDO record = tbPatrolStoreCloudDao.getByBusinessId(businessId, currentUser.getUserId(), enterpriseId);
        TbPatrolStoreCloudDO patrolStoreCloudDO = AddPatrolStoreCloudRequest.convert(param, currentUser);
        if (Objects.isNull(record)) {
            // 新增
            patrolStoreCloudDO.setCreateTime(new Date());
            tbPatrolStoreCloudDao.insert(patrolStoreCloudDO, enterpriseId);
            // 视频转码
            checkDefTableVideoHandel(patrolStoreCloudDO, enterpriseId);
        } else {
            // 更新
            patrolStoreCloudDO.setId(record.getId());
            // 视频转码
            checkDefTableVideoHandel(patrolStoreCloudDO, enterpriseId);
            tbPatrolStoreCloudDao.update(patrolStoreCloudDO, enterpriseId);
        }
        return patrolStoreCloudDO.getId();
    }

    @Override
    public PatrolStoreCloudVO getCloudByBusinessId(String enterpriseId, Long businessId, String userId) {
        TbPatrolStoreCloudDO record = tbPatrolStoreCloudDao.getByBusinessId(businessId, userId, enterpriseId);
        if (Objects.isNull(record)) {
            return null;
        }
        return PatrolStoreCloudVO.convert(record);
    }

    @Override
    public Integer deleteCloud(String enterpriseId, Long businessId, String userId) {
        TbPatrolStoreCloudDO record = tbPatrolStoreCloudDao.getByBusinessId(businessId, userId, enterpriseId);
        if (Objects.isNull(record)) {
            return 0;
        }
        return tbPatrolStoreCloudDao.delete(record.getId(), enterpriseId);
    }

    @Override
    public TbPatrolStoreCloudDO selectById(String enterpriseId, Long id) {
        return tbPatrolStoreCloudDao.selectById(id, enterpriseId);
    }

    @Override
    public void updateVideo(String enterpriseId, Long id, String video) {
        TbPatrolStoreCloudDO tbPatrolStoreCloudDO = TbPatrolStoreCloudDO.builder()
                .id(id).video(video).build();
        tbPatrolStoreCloudDao.update(tbPatrolStoreCloudDO, enterpriseId);
    }

    /**
     * 云图库视频转码
     *
     * @param patrolStoreCloudDO 云图库
     * @param enterpriseId       企业id
     */
    public void checkDefTableVideoHandel(TbPatrolStoreCloudDO patrolStoreCloudDO, String enterpriseId) {
        SmallVideoInfoDTO smallVideoInfo = JSONObject.parseObject(patrolStoreCloudDO.getVideo(), SmallVideoInfoDTO.class);
        if (smallVideoInfo != null && CollectionUtils.isNotEmpty(smallVideoInfo.getVideoList())) {
            String callbackCache;
            SmallVideoDTO smallVideoCache;
            SmallVideoParam smallVideoParam;
            for (SmallVideoDTO smallVideo : smallVideoInfo.getVideoList()) {
                //如果转码完成
                if (smallVideo.getStatus() != null && smallVideo.getStatus() >= ResourceStatusEnum.TRANSCODE_FINISH.getValue()) {
                    continue;
                }
                callbackCache = redisUtil.getString(RedisConstant.VIDEO_CALLBACK_CACHE + smallVideo.getVideoId());
                if (StringUtils.isNotBlank(callbackCache)) {
                    smallVideoCache = JSONObject.parseObject(callbackCache, SmallVideoDTO.class);
                    if (smallVideoCache != null && smallVideoCache.getStatus() != null && smallVideoCache.getStatus() >= 3) {
                        BeanUtils.copyProperties(smallVideoCache, smallVideo);
                    } else {
                        smallVideoParam = new SmallVideoParam();
                        setDefTableNotCompleteCache(smallVideoParam, smallVideo, patrolStoreCloudDO.getId(), enterpriseId);
                    }
                } else {
                    smallVideoParam = new SmallVideoParam();
                    setDefTableNotCompleteCache(smallVideoParam, smallVideo, patrolStoreCloudDO.getId(), enterpriseId);
                }
            }
            patrolStoreCloudDO.setVideo(JSONObject.toJSONString(smallVideoInfo));
        }
    }

    /**
     * 视频转码设置
     *
     * @param smallVideoParam 视频信息参数
     * @param smallVideo      上传的视频信息
     * @param businessId      巡店id
     * @param enterpriseId    企业id
     */
    public void setDefTableNotCompleteCache(SmallVideoParam smallVideoParam, SmallVideoDTO smallVideo, Long businessId, String enterpriseId) {
        smallVideoParam.setVideoId(smallVideo.getVideoId());
        smallVideoParam.setUploadType(UploadTypeEnum.STORE_CLOUD.getValue());
        smallVideoParam.setBusinessId(businessId);
        smallVideoParam.setUploadTime(new Date());
        smallVideoParam.setEnterpriseId(enterpriseId);
        //存入未转码完成的map，vod回调的时候使用
        redisUtil.hashSet(RedisConstant.VIDEO_NOT_COMPLETE_CACHE, smallVideo.getVideoId(), JSONObject.toJSONString(smallVideoParam));
    }
}
