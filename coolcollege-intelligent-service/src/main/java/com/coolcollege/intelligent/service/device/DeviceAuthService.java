package com.coolcollege.intelligent.service.device;

import com.coolcollege.intelligent.common.enums.device.DeviceAuthAppEnum;
import com.coolcollege.intelligent.model.device.DeviceDO;
import com.coolcollege.intelligent.model.device.dto.DeviceAuthDTO;
import com.coolcollege.intelligent.model.device.dto.DeviceAuthPageDTO;
import com.coolcollege.intelligent.model.device.dto.DeviceCancelAuthDTO;
import com.coolcollege.intelligent.model.device.dto.VideoDTO;
import com.coolcollege.intelligent.model.device.request.ElemeStoreOpenRequest;
import com.coolcollege.intelligent.model.device.request.OpenDevicePageRequest;
import com.coolcollege.intelligent.model.device.vo.DeviceAuthDetailVO;
import com.coolcollege.intelligent.model.device.vo.DeviceAuthRecordVO;
import com.coolcollege.intelligent.model.device.vo.OpenDevicePageVO;
import com.coolcollege.intelligent.model.device.vo.OpenVideoUrlVO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.github.pagehelper.PageInfo;

public interface DeviceAuthService {

    /**
     * 获取设备授权详情
     * @param enterpriseId
     * @param deviceId
     * @param channelNo
     * @return
     */
    DeviceAuthDetailVO getDeviceAuthDetail(String enterpriseId, String deviceId, String channelNo);

    /**
     * 设备授权
     * @param enterpriseId
     * @param param
     * @return
     */
    Boolean deviceAuth(String enterpriseId, DeviceAuthDTO param, DeviceDO device);


    /**
     * 取消设备授权
     * @param enterpriseId
     * @param param
     * @return
     */
    Boolean cancelDeviceAuth(String enterpriseId, DeviceCancelAuthDTO param);


    /**
     * 获取播放流
     * @param appEnum
     * @param param
     * @return
     */
    OpenVideoUrlVO getLiveUrl(DeviceAuthAppEnum appEnum, VideoDTO param);


    /**
     * 门店开通 推送授权设备
     * @param appEnum
     * @param request
     */
    boolean storeOpenPushAuthDevice(DeviceAuthAppEnum appEnum, ElemeStoreOpenRequest request);

    /**
     * 设备推送
     * @param enterpriseId
     */
    void devicePush(String enterpriseId);

    /**
     * 获取授权列表
     * @param enterpriseId
     * @param param
     * @return
     */
    PageInfo getDeviceAuthPage(String enterpriseId, DeviceAuthPageDTO param, CurrentUser currentUser);

    /**
     * 获取设备列表
     * @param request
     * @return
     */
    PageInfo<OpenDevicePageVO> getDevicePage(OpenDevicePageRequest request);
}
