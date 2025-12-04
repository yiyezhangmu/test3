package com.coolcollege.intelligent.service.video;

import com.coolcollege.intelligent.model.callback.CallbackRequest;
import com.coolcollege.intelligent.model.device.DeviceChannelDO;
import com.coolcollege.intelligent.model.device.DeviceDO;
import com.coolcollege.intelligent.model.video.platform.yushi.DTO.YushiChannelDTO;
import com.coolcollege.intelligent.model.video.platform.yushi.response.PositionResponse;
import com.coolcollege.intelligent.model.video.platform.yushi.response.YonghuiDeviceResponse;

import java.util.List;

/**
 * @author byd
 * @date 2021-02-05 14:29
 */
public interface YushiDeviceService {

    String getRedisToken(String eid);

    String getLiveUrl(String deviceId, String channelNo, String streamType, Integer streamIndex, Long startTime, Long endTime, Integer recordTypes, String token);

    /**
     * 获取全量永辉设备列表
     */
    List<YonghuiDeviceResponse> getDeviceList(String eid);

    List<YushiChannelDTO> getChannelList(String eid, String deviceId);

    //云台控制

    /**
     * 开始云台控制
     *
     * @param deviceId
     * @param channelNo
     * @param command   操作命令，0-上，1-下，2-左，3-右，4-左上，5-左下，6-右上，7-右下，8-放大，9-缩小，10-近焦距，11-远焦距
     * @param speed     云台转速，1-9，9最快
     * @param token
     */
    Boolean ptzStart(String deviceId, Integer channelNo, Integer command, Integer speed, String token);

    /**
     * 停止云台控制
     *
     * @param deviceId
     * @param channelNo
     * @param token
     */
    Boolean ptzStop(String deviceId, Integer channelNo, String token);


    /**
     * 抓拍图片
     *
     * @param deviceId
     * @param channelNo
     * @param token
     * @return
     */
    String capture(String deviceId, Integer channelNo, String token);
}
