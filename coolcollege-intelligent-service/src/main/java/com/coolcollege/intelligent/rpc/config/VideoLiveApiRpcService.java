package com.coolcollege.intelligent.rpc.config;

import com.alibaba.fastjson.JSONObject;
import com.alipay.sofa.runtime.api.annotation.SofaReference;
import com.alipay.sofa.runtime.api.annotation.SofaReferenceBinding;
import com.coolcollege.intelligent.constants.ConfigConstants;
import com.coolcollege.intelligent.dto.BaseResultDTO;
import com.coolcollege.intelligent.dto.ResultCodeDTO;
import com.coolcollege.intelligent.dto.video.LiveWatchDetailDTO;
import com.coolcollege.intelligent.dto.video.LiveWatchUserListDTO;
import com.coolcollege.intelligent.dto.video.VideLiveInfoDTO;
import com.coolcollege.intelligent.dto.video.VideoCreateDTO;
import com.coolcollege.intelligent.rpc.api.VideoLiveServiceApi;
import com.taobao.api.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author byd
 * @date 2023-07-31 16:22
 */
@Slf4j
@Service
public class VideoLiveApiRpcService {

    @SofaReference(uniqueId = ConfigConstants.CONFIG_FACADE_VIDEO_UNIQUE_ID, interfaceType = VideoLiveServiceApi.class, binding = @SofaReferenceBinding(bindingType = "bolt"))
    private VideoLiveServiceApi videoLiveServiceApi;

    public String startUniformLive(VideoCreateDTO videoCreateDTO) throws Exception {
        log.info("rpc startUniformLive param : eid: {}", videoCreateDTO.getLiveId());
        BaseResultDTO<String> baseResultDTO = videoLiveServiceApi.startUniformLive(videoCreateDTO);
        log.info("rpc startUniformLive response : {}", JSONObject.toJSONString(baseResultDTO));
        if (baseResultDTO.getResultCode() != ResultCodeDTO.SUCCESS.getCode()) {
            throw new ApiException(String.valueOf(baseResultDTO.getResultCode()),baseResultDTO.getMessage());
        }
        return baseResultDTO.getData();
    }

    public VideLiveInfoDTO getUniformLiveInfo(String eid, String liveId, String unionId) throws Exception {
        log.info("rpc getUniformLiveInfo param : eid: {}, liveId: {}, unionId:{}", eid, liveId, unionId);
        BaseResultDTO<VideLiveInfoDTO> baseResultDTO = videoLiveServiceApi.getUniformLiveInfo(eid, liveId, unionId);
        log.info("rpc getUniformLiveInfo response : {}", JSONObject.toJSONString(baseResultDTO));
        if (baseResultDTO.getResultCode() != ResultCodeDTO.SUCCESS.getCode()) {
            throw new ApiException(String.valueOf(baseResultDTO.getResultCode()),baseResultDTO.getMessage());
        }
        return baseResultDTO.getData();
    }

    public Boolean updateUniformLive(VideoCreateDTO videoCreateDTO) throws Exception {
        log.info("rpc updateUniformLive param : eid: {}, liveId: {}", videoCreateDTO.getEnterpriseId(),  videoCreateDTO.getLiveId());
        BaseResultDTO<Boolean> baseResultDTO = videoLiveServiceApi.updateUniformLive(videoCreateDTO);
        log.info("rpc updateUniformLive response : {}", JSONObject.toJSONString(baseResultDTO));
        if (baseResultDTO.getResultCode() != ResultCodeDTO.SUCCESS.getCode()) {
            throw new ApiException(String.valueOf(baseResultDTO.getResultCode()),baseResultDTO.getMessage());
        }
        return baseResultDTO.getData();
    }

    public LiveWatchDetailDTO watchDetails(String eid, String liveId, String unionId) throws Exception {
        log.info("rpc watchDetails param : eid: {}, liveId: {}, unionId:{}", eid, liveId, unionId);
        BaseResultDTO<LiveWatchDetailDTO> baseResultDTO = videoLiveServiceApi.watchDetails(eid, liveId, unionId);
        log.info("rpc watchDetails response : {}", JSONObject.toJSONString(baseResultDTO));
        if (baseResultDTO.getResultCode() != ResultCodeDTO.SUCCESS.getCode()) {
            throw new ApiException(String.valueOf(baseResultDTO.getResultCode()),baseResultDTO.getMessage());
        }
        return baseResultDTO.getData();
    }

    public LiveWatchUserListDTO watchUsers(String eid, String liveId, String unionId) throws Exception {
        log.info("rpc watchUsers param : eid: {}, liveId: {}, unionId:{}", eid, liveId, unionId);
        BaseResultDTO<LiveWatchUserListDTO> baseResultDTO = videoLiveServiceApi.watchUsers(eid, liveId, unionId);
        log.info("rpc watchUsers response : {}", JSONObject.toJSONString(baseResultDTO));
        if (baseResultDTO.getResultCode() != ResultCodeDTO.SUCCESS.getCode()) {
            throw new ApiException(String.valueOf(baseResultDTO.getResultCode()),baseResultDTO.getMessage());
        }
        return baseResultDTO.getData();
    }


    public Boolean deleteLives(String eid, String liveId, String unionId) throws Exception {
        log.info("rpc deleteLives param : eid: {}, liveId: {}, unionId:{}", eid, liveId, unionId);
        BaseResultDTO<Boolean> baseResultDTO = videoLiveServiceApi.deleteLives(eid, liveId, unionId);
        log.info("rpc deleteLives response : {}", JSONObject.toJSONString(baseResultDTO));
        if (baseResultDTO.getResultCode() != ResultCodeDTO.SUCCESS.getCode()) {
            throw new ApiException(String.valueOf(baseResultDTO.getResultCode()),baseResultDTO.getMessage());
        }
        return baseResultDTO.getData();
    }
}
