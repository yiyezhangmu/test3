package com.coolcollege.intelligent.facade.open.api.video;

import cn.hutool.json.JSONUtil;
import com.alipay.sofa.runtime.api.annotation.SofaService;
import com.alipay.sofa.runtime.api.annotation.SofaServiceBinding;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.constants.ConfigConstants;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseUserMapper;
import com.coolcollege.intelligent.dao.enterprise.dao.EnterpriseConfigDao;
import com.coolcollege.intelligent.dao.enterprise.dao.EnterpriseUserDao;
import com.coolcollege.intelligent.dto.video.LiveWatchDetailDTO;
import com.coolcollege.intelligent.dto.video.LiveWatchUserListDTO;
import com.coolcollege.intelligent.dto.video.VideLiveInfoDTO;
import com.coolcollege.intelligent.dto.video.VideoCreateDTO;
import com.coolcollege.intelligent.facade.dto.openApi.video.*;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseUserDO;
import com.coolcollege.intelligent.model.enterprise.dto.EnterpriseUserDTO;
import com.coolcollege.intelligent.model.rpc.RpcLocalHolder;
import com.coolcollege.intelligent.rpc.config.VideoLiveApiRpcService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.coolstore.base.enums.AppTypeEnum;
import com.coolstore.base.response.rpc.OpenApiResponseVO;
import com.taobao.api.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.apache.shenyu.client.sofa.common.annotation.ShenyuSofaClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author byd
 * @date 2023-07-31 16:53
 */
@Slf4j
@ConditionalOnProperty(name = "shenyu.register.registerType")
@SofaService(interfaceType = VideoLiveApi.class, bindings = {@SofaServiceBinding(bindingType = ConfigConstants.SOFA_BINDING_TYPE)})
@Service
public class VideoLiveApiImpl implements VideoLiveApi {

    @Resource
    private EnterpriseConfigDao enterpriseConfigDao;

    @Resource
    private VideoLiveApiRpcService videoLiveApiRpcService;

    @Resource
    private EnterpriseUserDao enterpriseUserDao;


    @Override
    @ShenyuSofaClient(path = "/video/startUniformLive")
    public OpenApiResponseVO<String> startUniformLive(VideoCreateApiDTO videoCreateApiDTO) {
        String enterpriseId = RpcLocalHolder.getEnterpriseId();
        try {
            DataSourceHelper.reset();
            EnterpriseConfigDO enterpriseConfig = enterpriseConfigDao.getEnterpriseConfig(enterpriseId);
            DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());
            EnterpriseUserDO userDO;
            if(AppTypeEnum.ONE_PARTY_APP.getValue().equals(enterpriseConfig.getAppType())){
                //一方
                userDO = enterpriseUserDao.selectById(enterpriseId, videoCreateApiDTO.getUserId());
            }else {
                userDO = enterpriseUserDao.selectByUserId(enterpriseId, videoCreateApiDTO.getUserId());
            }
            if (userDO == null) {
                throw new ServiceException(ErrorCodeEnum.USER_NOT_EXIST);
            }
            VideoCreateDTO videoCreateDTO = new VideoCreateDTO();
            videoCreateDTO.setLiveId(videoCreateApiDTO.getLiveId());
            videoCreateDTO.setEnterpriseId(enterpriseId);
            videoCreateDTO.setUnionId(userDO.getUnionid());
            videoCreateDTO.setTitle(videoCreateApiDTO.getTitle());
            videoCreateDTO.setIntroduction(videoCreateApiDTO.getIntroduction());
            videoCreateDTO.setCoverUrl(videoCreateApiDTO.getCoverUrl());
            videoCreateDTO.setPreStartTime(videoCreateApiDTO.getPreStartTime());
            videoCreateDTO.setPreEndTime(videoCreateApiDTO.getPreEndTime());
            videoCreateDTO.setPublicType(videoCreateApiDTO.getPublicType());
            return OpenApiResponseVO.success(videoLiveApiRpcService.startUniformLive(videoCreateDTO));
        } catch (ServiceException e) {
            log.error("openApi#video/startUniformLive,ServiceException", e);
            return OpenApiResponseVO.fail(e.getErrorCode(), e.getErrorMessage());
        } catch (ApiException e) {
            log.error("openApi#video/startUniformLive,ApiException", e);
            return OpenApiResponseVO.fail(ErrorCodeEnum.API_ERROR.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("openApi#video/startUniformLive,Exception", e);
            return OpenApiResponseVO.fail();
        }
    }

    @Override
    @ShenyuSofaClient(path = "/video/getUniformLiveInfo")
    public OpenApiResponseVO<VideLiveInfoApiDTO> getUniformLiveInfo(VideoApiDTO videoApiDTO) {
        String enterpriseId = RpcLocalHolder.getEnterpriseId();
        try {
            DataSourceHelper.reset();
            EnterpriseConfigDO enterpriseConfig = enterpriseConfigDao.getEnterpriseConfig(enterpriseId);
            DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());
            EnterpriseUserDO userDO;
            if(AppTypeEnum.ONE_PARTY_APP.getValue().equals(enterpriseConfig.getAppType())){
                //一方
                userDO = enterpriseUserDao.selectById(enterpriseId, videoApiDTO.getUserId());
            }else {
                userDO = enterpriseUserDao.selectByUserId(enterpriseId, videoApiDTO.getUserId());
            }
            if (userDO == null) {
                throw new ServiceException(ErrorCodeEnum.USER_NOT_EXIST);
            }
            VideLiveInfoDTO videLiveInfoDTO = videoLiveApiRpcService.getUniformLiveInfo(enterpriseId, videoApiDTO.getLiveId(), userDO.getUnionid());
            VideLiveInfoApiDTO videLiveInfoApiDTO = new VideLiveInfoApiDTO();
            videLiveInfoApiDTO.setCoverUrl(videLiveInfoDTO.getCoverUrl());
            videLiveInfoApiDTO.setDuration(videLiveInfoDTO.getDuration());
            videLiveInfoApiDTO.setEndTime(videLiveInfoDTO.getEndTime());
            videLiveInfoApiDTO.setIntroduction(videLiveInfoDTO.getIntroduction());
            videLiveInfoApiDTO.setLiveId(videLiveInfoDTO.getLiveId());
            videLiveInfoApiDTO.setLivePlayUrl(videLiveInfoDTO.getLivePlayUrl());
            videLiveInfoApiDTO.setLiveStatus(videLiveInfoDTO.getLiveStatus());
            videLiveInfoApiDTO.setPlaybackDuration(videLiveInfoDTO.getPlaybackDuration());
            videLiveInfoApiDTO.setStartTime(videLiveInfoDTO.getStartTime());
            videLiveInfoApiDTO.setSubscribeCount(videLiveInfoDTO.getSubscribeCount());
            videLiveInfoApiDTO.setTitle(videLiveInfoDTO.getTitle());
            videLiveInfoApiDTO.setUnionId(videLiveInfoDTO.getUnionId());
            EnterpriseUserDTO userDetailByUnionId = enterpriseUserDao.getUserDetailByUnionId(enterpriseId, videLiveInfoDTO.getUnionId());
            if(AppTypeEnum.ONE_PARTY_APP.getValue().equals(enterpriseConfig.getAppType())){
                videLiveInfoApiDTO.setUserId(userDetailByUnionId.getId());
            }else {
                videLiveInfoApiDTO.setUserId(userDetailByUnionId.getUserId());
            }
            videLiveInfoApiDTO.setUv(videLiveInfoDTO.getUv());
            return OpenApiResponseVO.success(videLiveInfoApiDTO);
        } catch (ServiceException e) {
            log.error("openApi#video/getUniformLiveInfo,ServiceException", e);
            return OpenApiResponseVO.fail(e.getErrorCode(), e.getErrorMessage());
        } catch (ApiException e) {
            log.error("openApi#video/getUniformLiveInfo,ApiException", e);
            return OpenApiResponseVO.fail(ErrorCodeEnum.API_ERROR.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("openApi#video/getUniformLiveInfo,Exception", e);
            return OpenApiResponseVO.fail();
        }
    }

    @Override
    @ShenyuSofaClient(path = "/video/updateUniformLive")
    public OpenApiResponseVO<Boolean> updateUniformLive(VideoCreateApiDTO videoCreateApiDTO) {
        String enterpriseId = RpcLocalHolder.getEnterpriseId();
        try {
            DataSourceHelper.reset();
            EnterpriseConfigDO enterpriseConfig = enterpriseConfigDao.getEnterpriseConfig(enterpriseId);
            DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());
            EnterpriseUserDO userDO;
            if(AppTypeEnum.ONE_PARTY_APP.getValue().equals(enterpriseConfig.getAppType())){
                //一方
                userDO = enterpriseUserDao.selectById(enterpriseId, videoCreateApiDTO.getUserId());
            }else {
                userDO = enterpriseUserDao.selectByUserId(enterpriseId, videoCreateApiDTO.getUserId());
            }
            if (userDO == null) {
                throw new ServiceException(ErrorCodeEnum.USER_NOT_EXIST);
            }
            VideoCreateDTO videoCreateDTO = new VideoCreateDTO();
            videoCreateDTO.setLiveId(videoCreateApiDTO.getLiveId());
            videoCreateDTO.setEnterpriseId(enterpriseId);
            videoCreateDTO.setUnionId(userDO.getUnionid());
            videoCreateDTO.setTitle(videoCreateApiDTO.getTitle());
            videoCreateDTO.setIntroduction(videoCreateApiDTO.getIntroduction());
            videoCreateDTO.setCoverUrl(videoCreateApiDTO.getCoverUrl());
            videoCreateDTO.setPreStartTime(videoCreateApiDTO.getPreStartTime());
            videoCreateDTO.setPreEndTime(videoCreateApiDTO.getPreEndTime());
            return OpenApiResponseVO.success(videoLiveApiRpcService.updateUniformLive(videoCreateDTO));
        } catch (ServiceException e) {
            log.error("openApi#video/updateUniformLive,ServiceException", e);
            return OpenApiResponseVO.fail(e.getErrorCode(), e.getErrorMessage());
        } catch (ApiException e) {
            log.error("openApi#video/updateUniformLive,ApiException", e);
            return OpenApiResponseVO.fail(ErrorCodeEnum.API_ERROR.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("openApi#video/updateUniformLive,Exception", e);
            return OpenApiResponseVO.fail();
        }
    }

    @Override
    @ShenyuSofaClient(path = "/video/watchDetails")
    public OpenApiResponseVO<LiveWatchDetailApiDTO> watchDetails(VideoApiDTO videoApiDTO) {
        String enterpriseId = RpcLocalHolder.getEnterpriseId();
        try {
            DataSourceHelper.reset();
            EnterpriseConfigDO enterpriseConfig = enterpriseConfigDao.getEnterpriseConfig(enterpriseId);
            DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());
            EnterpriseUserDO userDO;
            if(AppTypeEnum.ONE_PARTY_APP.getValue().equals(enterpriseConfig.getAppType())){
                //一方
                userDO = enterpriseUserDao.selectById(enterpriseId, videoApiDTO.getUserId());
            }else {
                userDO = enterpriseUserDao.selectByUserId(enterpriseId, videoApiDTO.getUserId());
            }
            if (userDO == null) {
                throw new ServiceException(ErrorCodeEnum.USER_NOT_EXIST);
            }
            LiveWatchDetailDTO liveWatchDetailDTO = videoLiveApiRpcService.watchDetails(enterpriseId, videoApiDTO.getLiveId(), userDO.getUnionid());
            LiveWatchDetailApiDTO liveWatchDetailApiDTO = new LiveWatchDetailApiDTO();
            liveWatchDetailApiDTO.setAvgWatchTime(liveWatchDetailDTO.getAvgWatchTime());
            liveWatchDetailApiDTO.setLiveUv(liveWatchDetailDTO.getLiveUv());
            liveWatchDetailApiDTO.setMsgCount(liveWatchDetailDTO.getMsgCount());
            liveWatchDetailApiDTO.setPlaybackUv(liveWatchDetailDTO.getPlaybackUv());
            liveWatchDetailApiDTO.setPraiseCount(liveWatchDetailDTO.getPraiseCount());
            liveWatchDetailApiDTO.setPv(liveWatchDetailDTO.getPv());
            liveWatchDetailApiDTO.setTotalWatchTime(liveWatchDetailDTO.getTotalWatchTime());
            liveWatchDetailApiDTO.setUv(liveWatchDetailDTO.getUv());
            return OpenApiResponseVO.success(liveWatchDetailApiDTO);
        } catch (ServiceException e) {
            log.error("openApi#video/watchDetails,ServiceException", e);
            return OpenApiResponseVO.fail(e.getErrorCode(), e.getErrorMessage());
        } catch (ApiException e) {
            log.error("openApi#video/watchDetails,ApiException", e);
            return OpenApiResponseVO.fail(ErrorCodeEnum.API_ERROR.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("openApi#video/watchDetails,Exception", e);
            return OpenApiResponseVO.fail();
        }
    }

    @Override
    @ShenyuSofaClient(path = "/video/watchUsers")
    public OpenApiResponseVO<LiveWatchUserListApiDTO> watchUsers(VideoApiDTO videoApiDTO) {
        String enterpriseId = RpcLocalHolder.getEnterpriseId();
        try {
            DataSourceHelper.reset();
            EnterpriseConfigDO enterpriseConfig = enterpriseConfigDao.getEnterpriseConfig(enterpriseId);
            DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());
            EnterpriseUserDO userDO;
            if(AppTypeEnum.ONE_PARTY_APP.getValue().equals(enterpriseConfig.getAppType())){
                //一方
                userDO = enterpriseUserDao.selectById(enterpriseId, videoApiDTO.getUserId());
            }else {
                userDO = enterpriseUserDao.selectByUserId(enterpriseId, videoApiDTO.getUserId());
            }
            if (userDO == null) {
                throw new ServiceException(ErrorCodeEnum.USER_NOT_EXIST);
            }
            LiveWatchUserListDTO liveWatchUserListDTO = videoLiveApiRpcService.watchUsers(enterpriseId, videoApiDTO.getLiveId(), userDO.getUnionid());
            LiveWatchUserListApiDTO liveWatchUserListApiDTO = JSONUtil.toBean(JSONUtil.toJsonStr(liveWatchUserListDTO), LiveWatchUserListApiDTO.class);
            if(AppTypeEnum.ONE_PARTY_APP.getValue().equals(enterpriseConfig.getAppType())){
                //一方
                liveWatchUserListApiDTO.orgUsesList.forEach(orgUser -> {
                    EnterpriseUserDO userDO1 = enterpriseUserDao.selectByUserId(enterpriseId, orgUser.getUserId());
                    if (userDO1 == null) {
                        log.error("openApi#video/watchUsers,用户不存在,userId:{}", orgUser.getUserId());
                        throw new ServiceException(ErrorCodeEnum.USER_NOT_EXIST);
                    }
                    orgUser.setUserId(userDO1.getId());
                });
            }
            return OpenApiResponseVO.success(liveWatchUserListApiDTO);
        } catch (ServiceException e) {
            log.error("openApi#video/watchUsers,ServiceException", e);
            return OpenApiResponseVO.fail(e.getErrorCode(), e.getErrorMessage());
        } catch (ApiException e) {
            log.error("openApi#video/watchUsers,ApiException", e);
            return OpenApiResponseVO.fail(ErrorCodeEnum.API_ERROR.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("openApi#video/watchUsers,Exception", e);
            return OpenApiResponseVO.fail();
        }
    }

    @Override
    @ShenyuSofaClient(path = "/video/deleteLives")
    public OpenApiResponseVO<Boolean> deleteLives(VideoApiDTO videoApiDTO) {
        String enterpriseId = RpcLocalHolder.getEnterpriseId();
        try {
            DataSourceHelper.reset();
            EnterpriseConfigDO enterpriseConfig = enterpriseConfigDao.getEnterpriseConfig(enterpriseId);
            DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());
            EnterpriseUserDO userDO;
            if(AppTypeEnum.ONE_PARTY_APP.getValue().equals(enterpriseConfig.getAppType())){
                //一方
                userDO = enterpriseUserDao.selectById(enterpriseId, videoApiDTO.getUserId());
            }else {
                userDO = enterpriseUserDao.selectByUserId(enterpriseId, videoApiDTO.getUserId());
            }            if (userDO == null) {
                throw new ServiceException(ErrorCodeEnum.USER_NOT_EXIST);
            }
            return OpenApiResponseVO.success(videoLiveApiRpcService.deleteLives(enterpriseId, videoApiDTO.getLiveId(), userDO.getUnionid()));
        } catch (ServiceException e) {
            log.error("openApi#video/deleteLives,ServiceException", e);
            return OpenApiResponseVO.fail(e.getErrorCode(), e.getErrorMessage());
        } catch (ApiException e) {
            log.error("openApi#video/deleteLives,ApiException", e);
            return OpenApiResponseVO.fail(ErrorCodeEnum.API_ERROR.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("openApi#video/deleteLives,Exception", e);
            return OpenApiResponseVO.fail();
        }
    }
}
