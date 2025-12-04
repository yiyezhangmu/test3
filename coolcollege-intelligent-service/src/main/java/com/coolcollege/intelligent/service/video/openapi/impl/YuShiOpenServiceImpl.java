package com.coolcollege.intelligent.service.video.openapi.impl;

import com.coolcollege.intelligent.model.device.DeviceChannelDO;
import com.coolcollege.intelligent.model.device.DeviceDO;
import com.coolcollege.intelligent.model.device.dto.DeviceChannelYunMouDTO;
import com.coolcollege.intelligent.model.device.dto.OpenDeviceDTO;
import com.coolcollege.intelligent.model.device.dto.OpenDevicePageDTO;
import com.coolcollege.intelligent.model.device.dto.VideoDTO;
import com.coolcollege.intelligent.model.video.platform.hik.dto.PassengerDTO;
import com.coolcollege.intelligent.model.video.platform.hik.dto.AppKeyDTO;
import com.coolcollege.intelligent.model.video.platform.hik.dto.HikCloudAreasDTO;
import com.coolcollege.intelligent.model.video.platform.hik.dto.VideoFileDTO;
import com.coolcollege.intelligent.model.video.platform.yingshi.PassengerFlowConfigDTO;
import com.coolcollege.intelligent.model.video.platform.yingshi.PassengerFlowSwitchStatusDTO;
import com.coolcollege.intelligent.model.video.vo.LiveVideoVO;
import com.coolcollege.intelligent.service.video.openapi.VideoOpenService;
import com.coolstore.base.enums.AccountTypeEnum;
import com.coolstore.base.enums.VideoProtocolTypeEnum;
import com.coolstore.base.enums.YunTypeEnum;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author zhangchenbiao
 * @FileName: YuShiOpenServiceImpl
 * @Description:
 * @date 2022-12-16 17:21
 */
@Service
public class YuShiOpenServiceImpl implements VideoOpenService {

    @Override
    public YunTypeEnum getYunTypeNum() {
        return null;
    }

    @Override
    public String getAccessToken(String enterpriseId, AccountTypeEnum accountType) {
        return null;
    }

    @Override
    public OpenDeviceDTO getDeviceDetail(String enterpriseId, String deviceId, AccountTypeEnum accountType) {
        return null;
    }

    @Override
    public PageInfo<OpenDevicePageDTO> getDeviceList(String enterpriseId, AccountTypeEnum accountType, Integer pageNum, Integer pageSize) {
        return null;
    }

    @Override
    public LiveVideoVO getLiveUrl(String enterpriseId, DeviceDO device, VideoDTO param) {
        return null;
    }

    @Override
    public LiveVideoVO getPastVideoUrl(String enterpriseId, DeviceDO device, VideoDTO param) {
        return null;
    }

    @Override
    public void cancelAuth(String enterpriseId, DeviceDO device, List<DeviceChannelDO> channelList) {

    }

    @Override
    public Boolean ptzStart(String enterpriseId, DeviceDO device, String channelNo, Integer command, Integer speed, Long duration) {
        return null;
    }

    @Override
    public Boolean ptzStop(String enterpriseId, DeviceDO device, String channelNo) {
        return null;
    }

    @Override
    public String addPtzPreset(String enterpriseId, DeviceDO device, String channelNo, String devicePositionName) {
        return null;
    }

    @Override
    public Boolean deletePtzPreset(String enterpriseId, DeviceDO device, String channelNo, String presetIndex) {
        return null;
    }

    @Override
    public Boolean loadPtzPreset(String enterpriseId, DeviceDO device, String channelNo, String presetIndex) {
        return null;
    }

    @Override
    public String capture(String eid, DeviceDO device, String channelNo, String quality) {
        return null;
    }

    @Override
    public String videoTransCode(String enterpriseId,  DeviceDO device, VideoDTO param) {
        return null;
    }

    @Override
    public VideoFileDTO getVideoFile(String enterpriseId,DeviceDO device, String fileId) {
        return null;
    }

    @Override
    public List<String> getVideoDownloadUrl(String enterpriseId,DeviceDO device, String fileId) {
        return null;
    }

}
