package com.coolcollege.intelligent.service.video;

import com.coolcollege.intelligent.model.video.platform.imou.response.ImouChannelInfoResponse;
import com.coolcollege.intelligent.model.video.platform.imou.response.ImouDeviceInfoResponse;

import java.util.List;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2022/04/24
 */
public interface ImouDeviceService {
//    String getAccessToken();

    /**
     * 获取设备的kitToken
     *
     * @param deviceId
     * @param channelNo
     * @param type 0：所有权限；1：实时预览；2：录像回放（云录像+本地录像）
     * @return
     */
//    String getKitToken(String deviceId,String channelNo,String type);

    /**
     * 单个设备通道的详细信息获取
     * @param deviceId
     * @param channelNo
     * @return
     */
    ImouChannelInfoResponse getChannelInfo(String deviceId, String channelNo);


    ImouDeviceInfoResponse authorizedDeviceList(Integer pageNo, Integer pageSize);

    /**
     * 批量设置设备的托管状态
     * 开发者调用此接口批量设置终端用户托管给自己的设备托管状态,其中已拒绝的设备可再次接受，已接受的设备可再次拒绝，终端用户取消的设备将无法设置状态
     *
     *       确认授权接口不报错,该接口只针对授权调用
     * @param deviceIdList
     * @param status 目标状态，接受(accept)或者拒绝(refuse)
     * @return
     */
    Boolean setDeviceAuthStatus(List<String> deviceIdList, String status);

    /**
     * 云台移动控制
     *备注：云台相关功能需要设备拥有PT或PTZ云台能力集
     * @param deviceId
     * @param channelNo
     * @param operation
     * @param duration
     * @return
     */
    Boolean ptzMove(String deviceId,String channelNo,String operation,Long duration);

    String capture(String deviceId,String channelNo);

    String createYingshiAuthUrl(String eid,String storeId,String userId);
}
