package com.coolcollege.intelligent.service.video;


import com.coolcollege.intelligent.model.device.dto.OpenDevicePageDTO;
import com.coolcollege.intelligent.model.video.platform.hik.dto.HikCloudChannelsDTO;
import com.coolcollege.intelligent.model.video.platform.hik.dto.HikCloudDeviceDTO;
import com.coolcollege.intelligent.model.video.platform.hik.dto.HikCloudLiveAddressDTO;

import java.util.List;

/**
 * 海康云眸设备打通
 * @Author suzhuhong
 * @Date 2022/8/23 15:13
 * @Version 1.0
 */
public interface HikCloudDeviceService {

    /**
     * 获取海康云眸 access_token
     * @param eid
     * @return
     */
    String getHikCloudRedisToken(String eid);

    /**
     * 获取设备列表
     * @param eid
     * @return
     */
    List<OpenDevicePageDTO> getAllDeviceList(String eid);

    /**
     * 开通标准流预览
     * @param eid
     * @param channelIds
     * @return
     */
    Boolean liveVideoOpen(String eid,List<String> channelIds);

    /**
     * 获取标准流预览地址
     * @param eid
     * @param channelId
     * @return
     */
    HikCloudLiveAddressDTO getLiveAddress(String eid, String channelId);

    /**
     * 抓图
     * @param eid
     * @param deviceSerial
     * @param channelNo
     * @param quality
     * @return
     */
    String capture(String eid ,String deviceSerial,String channelNo,String quality);


    /**
     * 同步海康云眸设备信息到酷点掌
     * @param eid
     * @param userId
     */
    void asyncDevice(String eid,String userId);

    /**
     * 取流认证
     * @param eid
     * @return
     */
    String authentication(String eid);


    /**
     * 根据设备序列号获取 该设备下的设备通道
     * @param eid
     * @param deviceSerial
     * @return
     */
    List<HikCloudChannelsDTO> listByDevSerial(String eid ,String deviceSerial);



}
